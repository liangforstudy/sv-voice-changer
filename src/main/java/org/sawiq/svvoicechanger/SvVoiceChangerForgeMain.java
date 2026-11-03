package org.sawiq.svvoicechanger;

//? if forge {
/*import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.loading.FMLPaths;
import org.sawiq.svvoicechanger.network.VoiceChangerNetwork;
import org.sawiq.svvoicechanger.server.ServerPermissions;
import org.sawiq.svvoicechanger.server.VoiceChangerCommand;
import org.sawiq.svvoicechanger.server.VoiceChangerServerRuntime;

/^*
 * The Minecraft Forge entrypoint (1.20.1).
 *
 * <p>Forge of this era has one mod class for both sides, so the client half is
 * started from here, behind a physical-side check. The client class is only
 * named inside that branch, which keeps it from being loaded on a dedicated
 * server.</p>
 ^/
@Mod(SvVoiceChanger.NEOFORGE_MOD_ID)
public final class SvVoiceChangerForgeMain {
    public SvVoiceChangerForgeMain() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        VoiceChangerNetwork.initialize();

        MinecraftForge.EVENT_BUS.addListener(ServerPermissions::gather);
        MinecraftForge.EVENT_BUS.addListener(SvVoiceChangerForgeMain::onRegisterCommands);
        MinecraftForge.EVENT_BUS.addListener(SvVoiceChangerForgeMain::onServerStarting);
        MinecraftForge.EVENT_BUS.addListener(SvVoiceChangerForgeMain::onServerStopped);

        if (FMLEnvironment.dist.isClient()) {
            org.sawiq.svvoicechanger.client.SvVoiceChangerForge.initialize(modEventBus);
        }
    }

    private static void onRegisterCommands(RegisterCommandsEvent event) {
        VoiceChangerCommand.register(event.getDispatcher());
    }

    private static void onServerStarting(ServerStartingEvent event) {
        VoiceChangerServerRuntime.INSTANCE.start(event.getServer(), FMLPaths.CONFIGDIR.get());
    }

    private static void onServerStopped(ServerStoppedEvent event) {
        VoiceChangerServerRuntime.INSTANCE.stop();
    }
}
*///?}
