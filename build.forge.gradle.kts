plugins {
    id("net.neoforged.moddev.legacyforge") version "2.0.147"
    id("neoforge-mutex")
}

// Minecraft Forge, for the versions before NeoForge took over (1.20.1).
// ModDevGradle's legacy plugin handles the SRG remapping Forge still needs in
// production, so the source stays on Mojang names like every other target.

version = "${property("mod.version")}+${sc.current.version}"
base.archivesName = "sv-voice-changer-forge"

// Forge 1.20.1 targets Java 17. The compiler only has to emit Java 17 class
// files, so any newer JDK will do for the toolchain.
val requiredJava = JavaVersion.VERSION_17
val toolchainJava = JavaVersion.VERSION_21

repositories {
    mavenCentral()
    maven("https://maven.maxhenkel.de/repository/public") { name = "MaxHenkel" }
    exclusiveContent {
        forRepository { maven("https://api.modrinth.com/maven") { name = "Modrinth" } }
        filter { includeGroup("maven.modrinth") }
    }
}

dependencies {
    val voicechatCoordinate = (findProperty("deps.voicechat_coordinate") as String?)
        ?: "simple-voice-chat:${property("deps.voicechat")}"

    implementation("de.maxhenkel.voicechat:voicechat-api:${property("deps.voicechat_api")}")
    // Published with SRG names, so it has to be remapped for the dev environment.
    modImplementation("maven.modrinth:$voicechatCoordinate")
}

legacyForge {
    version = property("deps.forge_loader") as String

    mods {
        register("sv_voice_changer") {
            sourceSet(sourceSets.main.get())
        }
    }

    runs {
        register("client") {
            gameDirectory = rootProject.file("run/${sc.current.project}")
            client()
        }
        register("server") {
            gameDirectory = rootProject.file("run/${sc.current.project}-server")
            server()
        }
    }
}

// No refmap: every mixin here targets Simple Voice Chat's own classes, and
// Forge 1.20.1 runs with Mojang class names, so the names in the mixins are
// the ones at runtime. The one exception, Simple Voice Chat's override of
// Screen.init, keeps its SRG name and is listed by both names in
// VoiceChatScreenMixin. Only the config has to be announced.
mixin {
    config("sv-voice-changer.mixins.json")
}

java {
    withSourcesJar()
    sourceCompatibility = requiredJava
    targetCompatibility = requiredJava
    toolchain.languageVersion = JavaLanguageVersion.of(toolchainJava.majorVersion)
}

tasks {
    withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
        options.release = requiredJava.majorVersion.toInt()
    }

    processResources {
        val props: Map<String, String> = mapOf(
            "id" to project.property("mod.neoforge_id").toString(),
            "name" to project.property("mod.name").toString(),
            "version" to project.property("mod.version").toString(),
            "description" to project.property("mod.description").toString(),
            "minecraft" to project.property("mod.mc_compat").toString(),
            "voicechat" to project.property("deps.voicechat_compat").toString(),
            "forge" to project.property("deps.forge_compat").toString()
        )
        inputs.properties(props)
        filesMatching("META-INF/mods.toml") { expand(props) }
        filesMatching("*.mixins.json") { expand("java" to "JAVA_${requiredJava.majorVersion}") }
        exclude("fabric.mod.json", "META-INF/neoforge.mods.toml")
    }

    named("createMinecraftArtifacts") {
        dependsOn("stonecutterGenerate")
    }

    jar {
        // Forge 1.20.1 reads mixin configs from the manifest, not from mods.toml.
        manifest.attributes("MixinConfigs" to "sv-voice-changer.mixins.json")
        from(rootProject.file("LICENSE.txt")) {
            rename { "${it}_sv-voice-changer" }
        }
    }

    register<Copy>("buildAndCollect") {
        group = "build"
        dependsOn("build")
        from(named<Jar>("reobfJar").flatMap { it.archiveFile }, named<Jar>("sourcesJar").flatMap { it.archiveFile })
        into(rootProject.layout.projectDirectory.dir("dist"))
    }
}
