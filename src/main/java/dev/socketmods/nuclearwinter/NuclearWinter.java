package dev.socketmods.nuclearwinter;

import dev.socketmods.nuclearwinter.splash.MonagSplashScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;

@Mod(NuclearWinter.MODID)
public class NuclearWinter {
    public static final String MODID = "nuclearwinter";

    public NuclearWinter() {

        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> MonagSplashScreen::hook);
    }

}
