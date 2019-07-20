# OptiForge

**NOTE**: 
- This is NOT a Minecraft Forge mod, but only a tool that can build a special OptiFine jar file which can be loaded with Forge as a mod.
- This tool only supports Minecraft 1.14.3 now (2019.7.21).
- The special OptiFine version may cause many issues that are beyond my ability to fix them.

### How to Use
1. [Download](https://www.optifine.net/downloads) OptiFine 1.14.3 HD U F2 pre3.
1. Run OptiFine jar you just downloaded.
1. Select `.minecraft` folder which contains Minecraft 1.14.3 and click `Extract` button.
1. [Download](https://github.com/ZekerZhayard/OptiForge/archive/1.14.3.zip) this repositry and unzip it.
1. Move the OptiFine jar file which was just extracted and the vanilla Minecraft jar file (`.minecraft/versions/1.14.3/1.14.3.jar`) to `libs` folder
1. [Download](https://github.com/MinecraftForge/MCPConfig/archive/master.zip) MCPConfig and unzip it.
1. Run the command `./gradlew 1.14.3:makeSRG` in MCPConfig.
1. Move `build/versions/1.14.3/data/joined.srg` to `libs` folder and rename it to `1.14.3.srg`
1. Run the command `./gradlew build` in this repository.
1. You will get the special OptiFine jar in `output` folder and you an install it as a Forge mod.