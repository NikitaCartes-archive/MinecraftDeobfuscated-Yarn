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
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.util.Identifier;
import net.minecraft.util.Lazy;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public final class MissingSprite
extends Sprite {
    private static final int field_32950 = 16;
    private static final int field_32951 = 16;
    private static final String field_32952 = "missingno";
    private static final Identifier MISSINGNO = new Identifier("missingno");
    @Nullable
    private static NativeImageBackedTexture texture;
    private static final Lazy<NativeImage> IMAGE;
    private static final Sprite.Info INFO;

    private MissingSprite(SpriteAtlasTexture spriteAtlasTexture, int maxLevel, int atlasWidth, int atlasHeight, int x, int y) {
        super(spriteAtlasTexture, INFO, maxLevel, atlasWidth, atlasHeight, x, y, IMAGE.get());
    }

    public static MissingSprite getMissingSprite(SpriteAtlasTexture spriteAtlasTexture, int maxLevel, int atlasWidth, int atlasHeight, int x, int y) {
        return new MissingSprite(spriteAtlasTexture, maxLevel, atlasWidth, atlasHeight, x, y);
    }

    public static Identifier getMissingSpriteId() {
        return MISSINGNO;
    }

    public static Sprite.Info getMissingInfo() {
        return INFO;
    }

    @Override
    public void close() {
        for (int i = 1; i < this.images.length; ++i) {
            this.images[i].close();
        }
    }

    public static NativeImageBackedTexture getMissingSpriteTexture() {
        if (texture == null) {
            texture = new NativeImageBackedTexture(IMAGE.get());
            MinecraftClient.getInstance().getTextureManager().registerTexture(MISSINGNO, texture);
        }
        return texture;
    }

    static {
        IMAGE = new Lazy<NativeImage>(() -> {
            NativeImage nativeImage = new NativeImage(16, 16, false);
            int i = -16777216;
            int j = -524040;
            for (int k = 0; k < 16; ++k) {
                for (int l = 0; l < 16; ++l) {
                    if (k < 8 ^ l < 8) {
                        nativeImage.setColor(l, k, -524040);
                        continue;
                    }
                    nativeImage.setColor(l, k, -16777216);
                }
            }
            nativeImage.untrack();
            return nativeImage;
        });
        INFO = new Sprite.Info(MISSINGNO, 16, 16, new AnimationResourceMetadata(ImmutableList.of(new AnimationFrameResourceMetadata(0, -1)), 16, 16, 1, false));
    }
}

