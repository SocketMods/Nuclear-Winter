package dev.socketmods.nuclearwinter.splash;

import com.mojang.blaze3d.platform.NativeImage;
import dev.socketmods.nuclearwinter.NuclearWinter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.metadata.texture.TextureMetadataSection;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import net.minecraftforge.resource.PathResourcePack;
import net.minecraftforge.resource.ResourcePackLoader;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * Client-Side Only.
 * Edits the Mojang Studios splash screen logo to the MONA  G STUDIOS edit made by RainWarrior.
 * The edit only occurs once in 10,000 on average.
 *
 * @author Curle
 */
public class MonagSplashScreen {

    private static final ResourceLocation MOJANG_LOCATION = new ResourceLocation("textures/gui/title/mojangstudios.png");
    private static final ResourceLocation MONAG_LOCATION = new ResourceLocation(NuclearWinter.MODID, "textures/splash/monagstudios.png");

    public static void processLogo() {
        AbstractTexture currentTexture = Minecraft.getInstance().textureManager.getTexture(MOJANG_LOCATION);

        if (currentTexture instanceof MonagLogoTexture)
            return;

        Map<ResourceLocation, AbstractTexture> textureMap = ObfuscationReflectionHelper.getPrivateValue(TextureManager.class, Minecraft.getInstance().textureManager, "f_118" + "468_");
        textureMap.remove(MOJANG_LOCATION);
        Minecraft.getInstance().textureManager.register(MOJANG_LOCATION, new MonagLogoTexture());
    }

    static class MonagLogoTexture extends SimpleTexture {

        public MonagLogoTexture() {
            super(MOJANG_LOCATION);
        }

        protected SimpleTexture.TextureImage getTextureImage(ResourceManager p_96194_) {
            Minecraft minecraft = Minecraft.getInstance();
            PathResourcePack modpack = ResourcePackLoader.getPackFor(NuclearWinter.MODID).get();

            try {
                InputStream inputstream = modpack.getResource(PackType.CLIENT_RESOURCES, MONAG_LOCATION);

                SimpleTexture.TextureImage simpletexture$textureimage;
                try {
                    simpletexture$textureimage = new SimpleTexture.TextureImage(new TextureMetadataSection(true, true), NativeImage.read(inputstream));
                } catch (Throwable throwable1) {
                    try {
                        inputstream.close();
                    } catch (Throwable throwable) {
                        throwable1.addSuppressed(throwable);
                    }

                    throw throwable1;
                }

                inputstream.close();

                return simpletexture$textureimage;
            } catch (IOException ioexception) {
                return new SimpleTexture.TextureImage(ioexception);
            }
        }
    }
}
