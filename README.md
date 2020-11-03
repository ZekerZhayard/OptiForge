# OptiForge ([CurseForge](https://www.curseforge.com/minecraft/mc-mods/optiforge))

*Compatibility: (2020-11-03)*  
&emsp;*OptiForge-0.4.0*  
&emsp;*Forge-1.16.4-35.0.0 or newer version*  
&emsp;*OptiFine-1.16.4-G4-pre1 or newer version*  

## How to Use

1. Download [OptiFine](https://www.optifine.net/downloads) 1.16.4
2. Download [OptiForge](https://github.com/ZekerZhayard/OptiForge/releases) at the release page
3. Put the above mods into the `mods` folder.
4. Launch Minecraft!

*Notice: If you find a bug, please report it to OptiForge first unless you are sure it is an OptiFine bug.*

### About reporting crash
I recommend installing [MoreCrashInfo](https://github.com/xfl03/MoreCrashInfo/releases) mod. This mod will print more useful information to crash reports, and these can help us locate errors faster.

### For Development Environment

1. There is a project called [OptiFineDevTweaker](https://github.com/OpenCubicChunks/OptiFineDevTweaker) can help us use OptiFine under development environment. You should put OptiFine itself and OptiFineDevTweaker mod to `<projectDir>/run/mods` folder.
2. If your project doesn't depend on Mixin, you should put MixinBootstrap mod to mods folder.
3. You should also put OptiForge jar with `-deobf` suffix to mods folder.

*Notice:*  
*1. If you redefined your Gradle user home, you should add a VM argument:*  
*`-Dofdev.mcjar=<gradle.user.home>\caches\forge_gradle\minecraft_repo\versions\1.16.1\client.jar`*  
*2. OptiFineDevTweaker will dump all deobfuscated OptiFine classes to `./run/.optifineDev.classes` folder.*  
*3. There are some useful Mixin properties for debugging: [Mixin Java System Properties](https://github.com/SpongePowered/Mixin/wiki/Mixin-Java-System-Properties)*  
*I recommend you add `-Dmixin.debug.export=true`, it can dump all mixed classes to `./run/.mixin.out/class` folder.*  
*4. The recommended mcp mappings version is `snapshot_20201102-1.15.1`*

## Screenshots
![OF0.png](https://i.loli.net/2020/03/31/IBfv1ShQt7wVY2u.png)
