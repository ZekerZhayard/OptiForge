# OptiForge

## OptiFine has been compatible with Forge since 1.14.4 HD U F4 pre1

**NOTE**: 
- This is NOT a Minecraft Forge mod, but only a tool that can build a special OptiFine jar file which can be loaded with Forge as a mod.
- This tool supports Minecraft 1.14.3 and 1.14.4 now (2019.8.1).
- The special OptiFine version may cause many issues that are beyond my ability to fix them.

### How to Use
1. [Download](https://www.optifine.net/downloads) OptiFine_1.14.4_HD_U_F3_pre2.
1. Run OptiFine jar you just downloaded.
1. Select `.minecraft` folder which contains Minecraft 1.14.4 and click `Extract` button.
1. [Download](https://github.com/ZekerZhayard/OptiForge/archive/1.14.4.zip) this repositry and unzip it.
1. Move the OptiFine jar file which was just extracted and the vanilla Minecraft jar file (`.minecraft/versions/1.14.4/1.14.4.jar`) to `libs` folder
1. [Download](https://github.com/MinecraftForge/MCPConfig/archive/master.zip) MCPConfig and unzip it.
1. Run the command `./gradlew 1.14.4:makeSRG` in MCPConfig.
1. Move `build/versions/1.14.4/data/joined.srg` to `libs` folder and rename it to `1.14.4.srg`
1. Modify `minecraftVersion` and `optifineJarName` in `gradle.properties`
1. Run the command `./gradlew build` in this repository.
1. You will get the special OptiFine jar in `output` folder and you can install it as a Forge mod.