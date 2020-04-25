package io.github.zekerzhayard.optiforge.asm.fml;

import java.io.IOException;
import java.util.Properties;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.apache.maven.artifact.versioning.VersionRange;

public class VersionChecker {
    public final static boolean IS_LOADED;
    public final static String REQUIRED_MINECRAFT_VERSION;
    public final static String REQUIRED_OPTIFINE_VERSION;
    public final static String REQUIRED_FORGE_VERSION;
    public final static Function<String, Boolean> DEFAULT_FUNCTION = str -> {
        JOptionPane.showMessageDialog(null, str, "OptiForge Version Checker", JOptionPane.WARNING_MESSAGE);
        return false;
    };

    private final static Logger LOGGER = LogManager.getLogger();

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

    public static boolean checkOptiFineVersion(Function<String, Boolean> function) {
        try {
            String ofVer = (String) Class.forName("optifine.Installer").getMethod("getOptiFineVersion").invoke(null);
            Pattern pattern = Pattern.compile("OptiFine_(?<mcVersion>\\d+\\.\\d+\\.\\d*)_HD_U_(?<ofMajorVersion>[A-Z])(?<ofMinorVersion>\\d+)(_pre(?<ofPreVersion>\\d+))?");
            Matcher matcher = pattern.matcher(ofVer);
            if (matcher.find()) {
                String mcVersion = matcher.group("mcVersion");
                int ofMajorVersion = matcher.group("ofMajorVersion").toCharArray()[0] - 'A' + 1;
                int ofMinorVersion = Integer.parseInt(matcher.group("ofMinorVersion"));
                String ofPreVersion = matcher.group("ofPreVersion");
                if (ofPreVersion == null) {
                    ofPreVersion = "0";
                } else {
                    ofMinorVersion++;
                }
                String ofVersion = ofMajorVersion + "." + ofMinorVersion + "." + ofPreVersion;
                VersionRange versionRange = VersionRange.createFromVersionSpec("[" + REQUIRED_OPTIFINE_VERSION + ",)");
                if (!mcVersion.equals(REQUIRED_MINECRAFT_VERSION) || !versionRange.containsVersion(new DefaultArtifactVersion(ofVersion))) {
                    DefaultArtifactVersion requiredOFVersion = new DefaultArtifactVersion(REQUIRED_OPTIFINE_VERSION);
                    String requiredOptiFineCompletedVersion = "OptiFine_" + REQUIRED_MINECRAFT_VERSION + "_HD_U_" + (char) (requiredOFVersion.getMajorVersion() + 'A' - 1) + (requiredOFVersion.getMinorVersion() + (requiredOFVersion.getIncrementalVersion() == 0 ? -1 : 0)) + (requiredOFVersion.getIncrementalVersion() == 0 ? "" : "_pre" + requiredOFVersion.getIncrementalVersion());
                    return function.apply(
                        "You are using an unsupported OptiFine version, you can download the newer version from https://optifine.net/downloads.\n" +
                        "The game will continue, and run without OptiFine and OptiForge.\n" +
                        "(You installed: " + ofVer + ", required: " + requiredOptiFineCompletedVersion + " or newer)"
                    );
                }
            } else {
                return function.apply(
                    "Unable to parse OptiFine version, try to re-download OptiFine from https://optifine.net/downloads.\n" +
                    "The game will continue, and run without OptiFine and OptiForge.\n" +
                    "(Detected version: " + ofVer + ")"
                );
            }
        } catch (Exception e) {
            StringBuilder message = new StringBuilder(
                "It looks like you have not put OptiFine itself into the mods folder, you can download it from https://optifine.net/downloads.\n" +
                "The game will continue, and run without OptiFine and OptiForge.\n\n" +
                e + "\n"
            );
            for (StackTraceElement traceElement : e.getStackTrace()) {
                message.append("\tat ").append(traceElement).append("\n");
            }
            LOGGER.error(message.toString());
            return function.apply(message.toString());
        }
        return true;
    }

    /**
     * Mixin has it own version checker system, so this method only check if Mixin exists.
     */
    public static boolean checkMixinVersion(Function<String, Boolean> function) {
        try {
            Class.forName("org.spongepowered.asm.launch.MixinBootstrap").getField("VERSION");
        } catch (Exception e) {
            StringBuilder message = new StringBuilder(
                "It looks like you have not install Mixin, you can download it from https://www.curseforge.com/minecraft/mc-mods/mixinbootstrap.\n" +
                "The game will continue, and run without OptiFine and OptiForge.\n\n" +
                e + "\n"
            );
            for (StackTraceElement traceElement : e.getStackTrace()) {
                message.append("\tat ").append(traceElement).append("\n");
            }
            LOGGER.error(message.toString());
            return function.apply(message.toString());
        }
        return true;
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
}
