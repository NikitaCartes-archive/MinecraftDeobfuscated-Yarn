/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.texture;

import com.google.common.collect.ImmutableList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.metadata.AnimationFrameResourceMetadata;
import net.minecraft.client.resource.metadata.AnimationResourceMetadata;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.texture.SpriteContents;
import net.minecraft.client.texture.SpriteDimensions;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public final class MissingSprite {
    private static final int WIDTH = 16;
    private static final int HEIGHT = 16;
    private static final String MISSINGNO_ID = "missingno";
    private static final Identifier MISSINGNO = new Identifier("missingno");
    private static final AnimationResourceMetadata METADATA = new AnimationResourceMetadata(ImmutableList.of(new AnimationFrameResourceMetadata(0, -1)), 16, 16, 1, false);
    @Nullable
    private static NativeImageBackedTexture texture;

    private static NativeImage createImage(int width, int height) {
        NativeImage nativeImage = new NativeImage(width, height, false);
        int i = -16777216;
        int j = -524040;
        for (int k = 0; k < height; ++k) {
            for (int l = 0; l < width; ++l) {
                if (k < height / 2 ^ l < width / 2) {
                    nativeImage.setColor(l, k, -524040);
                    continue;
                }
                nativeImage.setColor(l, k, -16777216);
            }
        }
        return nativeImage;
    }

    public static SpriteContents createSpriteContents() {
        NativeImage nativeImage = MissingSprite.createImage(16, 16);
        return new SpriteContents(MISSINGNO, new SpriteDimensions(16, 16), nativeImage, METADATA);
    }

    public static Identifier getMissingSpriteId() {
        return MISSINGNO;
    }

    public static NativeImageBackedTexture getMissingSpriteTexture() {
        if (texture == null) {
            NativeImage nativeImage = MissingSprite.createImage(16, 16);
            nativeImage.untrack();
            texture = new NativeImageBackedTexture(nativeImage);
            MinecraftClient.getInstance().getTextureManager().registerTexture(MISSINGNO, texture);
        }
        return texture;
    }
}

