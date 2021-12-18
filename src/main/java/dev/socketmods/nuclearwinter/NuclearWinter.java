package dev.socketmods.nuclearwinter;

import dev.socketmods.nuclearwinter.splash.MonagSplashScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;

@Mod(NuclearWinter.MODID)
public class NuclearWinter {
    public static final String MODID = "nuclearwinter";

    public NuclearWinter() {

        MinecraftForge.EVENT_BUS.<TickEvent.RenderTickEvent>addListener((event) -> {
            if (event.phase == TickEvent.Phase.START)
                DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> MonagSplashScreen::processLogo);
        });
    }

}
