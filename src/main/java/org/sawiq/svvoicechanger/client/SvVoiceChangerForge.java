package org.sawiq.svvoicechanger.client;

//? if forge {
/*import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.GameShuttingDownEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.loading.FMLPaths;
import org.sawiq.svvoicechanger.client.server.ClientVoiceChannel;

/^*
 * The client half on Minecraft Forge (1.20.1), started by
 * {@link org.sawiq.svvoicechanger.SvVoiceChangerForgeMain} on a physical client only.
 ^/
public final class SvVoiceChangerForge {
    private SvVoiceChangerForge() {
    }

    public static void initialize(IEventBus modEventBus) {
        VoiceChangerClientRuntime runtime = VoiceChangerClientRuntime.INSTANCE;
        runtime.initialize(FMLPaths.CONFIGDIR.get());
        ClientVoiceChannel.initialize(payload ->
                VoiceChangerController.INSTANCE.serverSession().receive(payload));
        modEventBus.addListener(SvVoiceChangerForge::registerKeyMappings);
        MinecraftForge.EVENT_BUS.addListener(SvVoiceChangerForge::onClientTick);
        MinecraftForge.EVENT_BUS.addListener(SvVoiceChangerForge::onShutdown);
    }

    private static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        VoiceChangerClientRuntime.INSTANCE.registerHotkeys(event::register);
    }

    private static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            VoiceChangerClientRuntime.INSTANCE.tick(Minecraft.getInstance());
        }
    }

    private static void onShutdown(GameShuttingDownEvent event) {
        VoiceChangerClientRuntime.INSTANCE.shutdown();
    }
}
*///?}
