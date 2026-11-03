# Simple Voice Voice Changer: 1.20.1 backport

This is a backport of [imsawiq/sv-voice-changer](https://github.com/imsawiq/sv-voice-changer) to Minecraft 1.20.1.

It is based on upstream version 1.3.0 and has every feature of that release. For what the mod does and how to use it, see the [upstream README](https://github.com/imsawiq/sv-voice-changer#readme) and [docs/](docs/).

## Requirements

- Minecraft 1.20.1
- Simple Voice Chat for 1.20.1
- Fabric: Fabric Loader and Fabric API
- Forge: Forge 47 or later

## Building

```
./gradlew :1.20.1-fabric:buildAndCollect :1.20.1-forge:buildAndCollect
```

The jars are written to `dist/`. The upstream 1.21 and 26.x targets are still in the build and are unchanged.

## License

CC BY-NC-SA 4.0, the same as upstream. The original mod is by sawiq_.
