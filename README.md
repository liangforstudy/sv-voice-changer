# Simple Voice Voice Changer: 1.20.1 backport

This is a backport of [imsawiq/sv-voice-changer](https://github.com/imsawiq/sv-voice-changer) to Minecraft 1.20.1.

It is based on upstream version 1.3.0 and has every feature of that release. For what the mod does and how to use it, see the [upstream README](https://github.com/imsawiq/sv-voice-changer#readme) and [docs/](docs/).

## Requirements

- Minecraft 1.20.1
- Simple Voice Chat for 1.20.1
- Fabric: Fabric Loader and Fabric API
- Forge: Forge 47 or later

## Installing

1. Download the jar for your loader from the [latest release](https://github.com/liangforstudy/sv-voice-changer/releases/latest):
   - Fabric: `sv-voice-changer-fabric-1.3.0-1.20.1.jar`
   - Forge: `sv-voice-changer-forge-1.3.0-1.20.1.jar`
2. Download [Simple Voice Chat](https://modrinth.com/plugin/simple-voice-chat/versions?g=1.20.1) for 1.20.1 and the same loader. On Fabric, also download [Fabric API](https://modrinth.com/mod/fabric-api/versions?g=1.20.1) for 1.20.1.
3. Put the jars in the `mods` folder of your 1.20.1 Fabric or Forge instance and start the game.
4. Open the Simple Voice Chat menu (`V` by default) and press the microphone button at the top to open the Voice Changer Studio. `J` turns the effect on and off.

### On a server

Not required: everything except the server settings works on your own client. To let a server allow or deny the voice changer, mute players and share voices, put the same jar and Simple Voice Chat (plus Fabric API on Fabric) in the server's `mods` folder. The commands and settings are described under "For server operators" in the [upstream README](https://github.com/imsawiq/sv-voice-changer#readme).

## Building

```
./gradlew :1.20.1-fabric:buildAndCollect :1.20.1-forge:buildAndCollect
```

The jars are written to `dist/`. The upstream 1.21 and 26.x targets are still in the build and are unchanged.

## Who was this build for?

It was for a Reddit post, but I could no longer find it.

## License

CC BY-NC-SA 4.0, the same as upstream. The original mod is by sawiq_.
