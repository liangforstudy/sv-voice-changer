package probe;

import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;

@Mod("runtime_check_probe")
public final class ForgeProbe {
    public ForgeProbe() {
        if (!FMLEnvironment.dist.isClient()) {
            return;
        }
        Probe probe = new Probe();
        MinecraftForge.EVENT_BUS.addListener((TickEvent.ClientTickEvent event) -> {
            if (event.phase == TickEvent.Phase.END) {
                probe.tick(Minecraft.getInstance());
            }
        });
    }
}
