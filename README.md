# OptiForge ([CurseForge](https://www.curseforge.com/minecraft/mc-mods/optiforge))

*Compatibility: (2020-04-27)*  
&emsp;*OptiForge-0.1.13*  
&emsp;*Forge-1.15.2-31.1.51 or newer version*  
&emsp;*OptiFine-1.15.2-G1-pre7 or newer version*  

## How to Use

1. Download [OptiFine](https://www.optifine.net/downloads) 1.15.2
2. Download [MixinBootstrap](https://github.com/LXGaming/MixinBootstrap/releases/download/v1.0.2/MixinBootstrap-1.0.2.jar)
3. Download [OptiForge](https://github.com/ZekerZhayard/OptiForge/releases) at the release page
4. Put the above mods into the `mods` folder.
5. Launch Minecraft!

*Notice: If you find a bug, please report it to OptiForge first unless you are sure it is an OptiFine bug.*

### For Development Environment

1. There is a project called [OptiFineDevTweaker](https://github.com/OpenCubicChunks/OptiFineDevTweaker) can help us use OptiFine under development environment. You should put OptiFine itself and OptiFineDevTweaker mod to `<projectDir>/run/mods` folder.
2. If your project doesn't depend on Mixin, you should put MixinBootstrap mod to mods folder.
3. You should also put OptiForge jar with `-deobf` suffix to mods folder.

*Notice:*  
*1. If you redefined your Gradle user home, you should add a VM argument:*  
*`-Dofdev.mcjar=<gradle.user.home>\caches\forge_gradle\minecraft_repo\versions\1.15.2\client.jar`*  
*2. OptiFineDevTweaker will dump all deobfuscated OptiFine classes to `./run/.optifineDev.classes` folder.*  
*3. There are some useful Mixin properties for debugging: [Mixin Java System Properties](https://github.com/SpongePowered/Mixin/wiki/Mixin-Java-System-Properties)*  
*I recommend you add `-Dmixin.debug.export=true`, it can dump all mixed classes to `./run/.mixin.out/class` folder.*  
*4. The recommended mcp mappings version is `snapshot_20200426-1.15.1`*

## Screenshots
![OF0.png](https://i.loli.net/2020/03/31/IBfv1ShQt7wVY2u.png)
