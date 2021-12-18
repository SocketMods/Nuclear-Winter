package dev.socketmods.nuclearwinter.splash;

import com.mojang.blaze3d.platform.NativeImage;
import dev.socketmods.nuclearwinter.NuclearWinter;
import dev.socketmods.nuclearwinter.UnsafeHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.LoadingOverlay;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.resources.metadata.texture.TextureMetadataSection;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import net.minecraftforge.resource.PathResourcePack;
import net.minecraftforge.resource.ResourcePackLoader;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.function.IntSupplier;

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

    public static void hook() {
        String fieldName = "f_96161" + "_"; // BRAND_BACKGROUND
        final Field supplierField = ObfuscationReflectionHelper.findField(LoadingOverlay.class, fieldName);
        //noinspection ConstantConditions
        @Nullable final IntSupplier backgroundColorSupplier =
            ObfuscationReflectionHelper.getPrivateValue(LoadingOverlay.class, null, fieldName);

        if (backgroundColorSupplier == null) return;

        final IntSupplier wrappedSupplier = () -> {
            replaceLogo();
            return backgroundColorSupplier.getAsInt();
        };

        UnsafeHelper.putObject(supplierField, null, wrappedSupplier);
    }

    public static void replaceLogo() {
        AbstractTexture currentTexture = Minecraft.getInstance().textureManager.getTexture(MOJANG_LOCATION, MissingTextureAtlasSprite.getTexture());
        if (currentTexture instanceof MonagLogoTexture)
            return;

        Minecraft.getInstance().textureManager.register(MOJANG_LOCATION, new MonagLogoTexture());
    }

    static class MonagLogoTexture extends SimpleTexture {
        public MonagLogoTexture() {
            super(MOJANG_LOCATION);
        }

        protected SimpleTexture.TextureImage getTextureImage(ResourceManager resourceManager) {
            // We expect our resource pack to always be present
            PathResourcePack pack = ResourcePackLoader.getPackFor(NuclearWinter.MODID).orElseThrow();

            try {
                try (InputStream monagStream = pack.getResource(PackType.CLIENT_RESOURCES, MONAG_LOCATION)) {
                    return new SimpleTexture.TextureImage(new TextureMetadataSection(true, true), NativeImage.read(monagStream));
                }
            } catch (IOException ex) {
                return new SimpleTexture.TextureImage(ex);
            }
        }
    }
}
