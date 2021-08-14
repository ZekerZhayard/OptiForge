package io.github.zekerzhayard.optiforge.asm.fml;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.Properties;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import cpw.mods.modlauncher.api.ITransformationService;
import net.minecraftforge.fml.loading.LibraryFinder;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.apache.maven.artifact.versioning.VersionRange;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;

public class VersionChecker {
    public final static boolean IS_LOADED;
    public final static String REQUIRED_MINECRAFT_VERSION;
    public final static String REQUIRED_OPTIFINE_VERSION;
    public final static String REQUIRED_FORGE_VERSION;
    public final static Function<String, Boolean> DEFAULT_FUNCTION = str -> {
        JFrame frame = new JFrame("OptiForge Version Checker");
        frame.setUndecorated(true);
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
        JOptionPane.showMessageDialog(frame, str, "OptiForge Version Checker", JOptionPane.WARNING_MESSAGE);
        frame.dispose();
        optifinePath = null;
        return false;
    };

    private final static Logger LOGGER = LogManager.getLogger();
    private static Path optifinePath;

    static {
        boolean isLoaded;
        Properties properties = new Properties();
        try {
            properties.load(VersionChecker.class.getResourceAsStream("/requiredmods.properties"));
            isLoaded = true;
        } catch (IOException e) {
            LOGGER.error("An unexpected issue occurred when loading required mods versions, version checker will not work: ", e);
            isLoaded = false;
        }
        IS_LOADED = isLoaded;
        REQUIRED_MINECRAFT_VERSION = properties.getProperty("required.minecraft.version");
        REQUIRED_OPTIFINE_VERSION = properties.getProperty("required.optifine.version");
        REQUIRED_FORGE_VERSION = properties.getProperty("required.forge.version");
    }

    public static boolean checkOptiFineVersion(Function<String, Boolean> function, boolean shouldScan, ClassLoader optifineLoader) {
        StringBuilder mcVersion = new StringBuilder(REQUIRED_MINECRAFT_VERSION);
        try {
            // OptiForge 1.17.1 will force searching libraries folder.
            // This part will be reserved because OptiFine may be compatible with forge in the future.
            String ofVer = (String) optifineLoader.loadClass("optifine.Installer").getMethod("getOptiFineVersion").invoke(null);
            String ofVersion = parseOptiFineVersion(ofVer, mcVersion);
            if (ofVersion != null) {
                VersionRange versionRange = VersionRange.createFromVersionSpec("[" + REQUIRED_OPTIFINE_VERSION + ",)");
                if (!mcVersion.toString().equals(REQUIRED_MINECRAFT_VERSION) || !versionRange.containsVersion(new DefaultArtifactVersion(ofVersion))) {
                    DefaultArtifactVersion requiredOFVersion = new DefaultArtifactVersion(REQUIRED_OPTIFINE_VERSION);
                    String requiredOptiFineCompletedVersion = "OptiFine_" + REQUIRED_MINECRAFT_VERSION + "_HD_U_" + (char) (requiredOFVersion.getMajorVersion() + 'A' - 1) + (requiredOFVersion.getMinorVersion() + (requiredOFVersion.getIncrementalVersion() == 0 ? -1 : 0)) + (requiredOFVersion.getIncrementalVersion() == 0 ? "" : "_pre" + requiredOFVersion.getIncrementalVersion());
                    return function.apply("""
                        You are using an unsupported OptiFine version, you can download the newer version from https://optifine.net/downloads.
                        The game will continue, and run without OptiFine and OptiForge.
                        (You installed:\040""" + ofVer + ", required: " + requiredOptiFineCompletedVersion + " or newer)"
                    );
                }
            } else {
                return function.apply("""
                    Unable to parse OptiFine version, try to re-download OptiFine from https://optifine.net/downloads.
                    The game will continue, and run without OptiFine and OptiForge.
                    (Detected version:\040""" + ofVer + ")"
                );
            }
        } catch (Exception e) {
            // Some people always put the "OptiFine" jar which from ".minecraft/versions/<OptiFine-Version>" folder into the mods folder, but that is not real OptiFine jar.
            // Here is to scan ".minecraft/libraries/optifine/OptiFine" folder to find the real OptiFine.
            if (shouldScan) {
                try {
                    Method method = LibraryFinder.class.getDeclaredMethod("findLibsPath");
                    method.setAccessible(true);
                    Path libOptiFinePath = ((Path) method.invoke(null)).resolve("optifine").resolve("OptiFine");
                    if (Files.isDirectory(libOptiFinePath)) {
                        String optifineVersion = "";
                        Path libOptiFine = null;
                        for (Path p : Files.list(libOptiFinePath)
                            .filter(p -> Files.isRegularFile(p.resolve("OptiFine-" + p.getFileName().toString() + ".jar")) && p.getFileName().toString().split("_")[0].equals(mcVersion.toString()))
                            .map(p -> p.resolve("OptiFine-" + p.getFileName().toString() + ".jar")).collect(Collectors.toList())) {

                            LOGGER.info("Found a possible OptiFine jar: {}", p.toAbsolutePath());
                            String ofVersion = parseOptiFineVersion(getOptiFineVersionFromJar(p), null);
                            if (ofVersion != null && optifineVersion.equals("")) {
                                optifineVersion = ofVersion;
                                libOptiFine = p;
                            }
                            if (ofVersion != null && new DefaultArtifactVersion(ofVersion).compareTo(new DefaultArtifactVersion(optifineVersion)) > 0) {
                                optifineVersion = ofVersion;
                                libOptiFine = p;
                            }
                        }

                        if (libOptiFine != null) {
                            optifinePath = libOptiFine.toAbsolutePath();
                            LOGGER.info("Found the real OptiFine jar: {}", libOptiFine.toAbsolutePath());
                            URLClassLoader ucl = URLClassLoader.newInstance(new URL[] { libOptiFine.toUri().toURL() }, VersionChecker.class.getClassLoader());
                            return checkOptiFineVersion(function, false, ucl);
                        }
                    }
                } catch (Throwable throwable) {
                    LOGGER.catching(throwable);
                }
            }

            StringBuilder message = new StringBuilder("""
                It looks like you have not installed OptiFine, you can download it from https://optifine.net/downloads.
                The game will continue, and run without OptiFine and OptiForge.
                """ + e + "\n"
            );
            for (StackTraceElement traceElement : e.getStackTrace()) {
                message.append("\tat ").append(traceElement).append("\n");
            }
            LOGGER.error(message.toString());
            return function.apply(message.toString());
        }
        if (!shouldScan) {
            // Add OptiFinTransformerService if it was found from libraries.
            try {
                (FakeOptiFineTransformationService.getInstance().service = (ITransformationService) optifineLoader.loadClass("optifine.OptiFineTransformationService").getConstructor().newInstance()).onLoad(FakeOptiFineTransformationService.getInstance().getEnv(), FakeOptiFineTransformationService.getInstance().getOtherServices());
            } catch (Exception e) {
                LOGGER.catching(e);
            }
        }
        return true;
    }

    /**
     * Parse OptiFine version.
     *
     * Input: OptiFine_1.0.0_HD_U_A1_pre1
     * Output: return: 1.1.1, mcVersion: 1.0.0
     *
     * Input: OptiFine_1.0.0_HD_U_A1
     * Output: return: 1.2.0, mcVersion: 1.0.0
     */
    public static String parseOptiFineVersion(String optifineVersion, StringBuilder mcVersion) {
        Pattern pattern = Pattern.compile("OptiFine_(?<mcVersion>\\d+\\.\\d+\\.\\d*)_HD_U_(?<ofMajorVersion>[A-Z])(?<ofMinorVersion>\\d+)(_pre(?<ofPreVersion>\\d+))?");
        Matcher matcher = pattern.matcher(optifineVersion);
        if (matcher.find()) {
            if (mcVersion != null) {
                mcVersion.delete(0, mcVersion.length());
                mcVersion.append(matcher.group("mcVersion"));
            }
            int ofMajorVersion = matcher.group("ofMajorVersion").toCharArray()[0] - 'A' + 1;
            int ofMinorVersion = Integer.parseInt(matcher.group("ofMinorVersion"));
            String ofPreVersion = matcher.group("ofPreVersion");
            if (ofPreVersion == null) {
                ofPreVersion = "0";
                ofMinorVersion++;
            }
            return ofMajorVersion + "." + ofMinorVersion + "." + ofPreVersion;
        }
        return null;
    }

    public static String getOptiFineVersionFromJar(Path optifine) {
        try (ZipFile zf = new ZipFile(optifine.toFile())) {
            ZipEntry ze = zf.getEntry("net/optifine/Config.class");
            if (ze != null) {
                try (InputStream is = zf.getInputStream(ze)) {
                    ClassNode cn = new ClassNode();
                    new ClassReader(IOUtils.toByteArray(is)).accept(cn, ClassReader.EXPAND_FRAMES);
                    for (FieldNode fn : cn.fields) {
                        if (fn.name.equals("VERSION") && fn.desc.equals("Ljava/lang/String;")) {
                            return (String) fn.value;
                        }
                    }
                }
            }
        } catch (Throwable t) {
            LOGGER.catching(t);
        }
        return null;
    }

    /**
     * FML can't check the build number and if can't match minor version, game won't crash or prompt players prompts
     */
    public static boolean checkForgeVersion(Function<String, Boolean> function, String currentFMLVersion) {
        try {
            VersionRange version = VersionRange.createFromVersionSpec("[" + REQUIRED_FORGE_VERSION + ",)");
            if (!version.containsVersion(new DefaultArtifactVersion(currentFMLVersion))) {
                return function.apply(
                    "You are using an unsupported Forge version, you can download the newer version from https://files.minecraftforge.net/maven/net/minecraftforge/forge/index_" + REQUIRED_MINECRAFT_VERSION + ".html.\n" +
                    "The game will continue, and run without OptiFine and OptiForge.\n" +
                    "(You installed: " + currentFMLVersion + ", required: " + REQUIRED_FORGE_VERSION + " or newer)"
                );
            }
        } catch (Exception e) {
            LOGGER.error("An unexpected issue occurred when checking Forge version: ", e);
        }
        return true;
    }

    public static Optional<Path> getOptiFinePath() {
        return Optional.ofNullable(optifinePath);
    }
}
