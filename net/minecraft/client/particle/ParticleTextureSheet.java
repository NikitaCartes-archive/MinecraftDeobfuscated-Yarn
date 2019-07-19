/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.particle;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.texture.TextureManager;

@Environment(value=EnvType.CLIENT)
public interface ParticleTextureSheet {
    public static final ParticleTextureSheet TERRAIN_SHEET = new ParticleTextureSheet(){

        @Override
        public void begin(BufferBuilder bufferBuilder, TextureManager textureManager) {
            DiffuseLighting.disable();
            GlStateManager.disableBlend();
            GlStateManager.depthMask(true);
            textureManager.bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
            bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR_LIGHT);
        }

        @Override
        public void draw(Tessellator tessellator) {
            tessellator.draw();
        }

        public String toString() {
            return "TERRAIN_SHEET";
        }
    };
    public static final ParticleTextureSheet PARTICLE_SHEET_OPAQUE = new ParticleTextureSheet(){

        @Override
        public void begin(BufferBuilder bufferBuilder, TextureManager textureManager) {
            DiffuseLighting.disable();
            GlStateManager.disableBlend();
            GlStateManager.depthMask(true);
            textureManager.bindTexture(SpriteAtlasTexture.PARTICLE_ATLAS_TEX);
            bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR_LIGHT);
        }

        @Override
        public void draw(Tessellator tessellator) {
            tessellator.draw();
        }

        public String toString() {
            return "PARTICLE_SHEET_OPAQUE";
        }
    };
    public static final ParticleTextureSheet PARTICLE_SHEET_TRANSLUCENT = new ParticleTextureSheet(){

        @Override
        public void begin(BufferBuilder bufferBuilder, TextureManager textureManager) {
            DiffuseLighting.disable();
            GlStateManager.depthMask(false);
            textureManager.bindTexture(SpriteAtlasTexture.PARTICLE_ATLAS_TEX);
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            GlStateManager.alphaFunc(516, 0.003921569f);
            bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR_LIGHT);
        }

        @Override
        public void draw(Tessellator tessellator) {
            tessellator.draw();
        }

        public String toString() {
            return "PARTICLE_SHEET_TRANSLUCENT";
        }
    };
    public static final ParticleTextureSheet PARTICLE_SHEET_LIT = new ParticleTextureSheet(){

        @Override
        public void begin(BufferBuilder bufferBuilder, TextureManager textureManager) {
            GlStateManager.disableBlend();
            GlStateManager.depthMask(true);
            textureManager.bindTexture(SpriteAtlasTexture.PARTICLE_ATLAS_TEX);
            DiffuseLighting.disable();
            bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR_LIGHT);
        }

        @Override
        public void draw(Tessellator tessellator) {
            tessellator.draw();
        }

        public String toString() {
            return "PARTICLE_SHEET_LIT";
        }
    };
    public static final ParticleTextureSheet CUSTOM = new ParticleTextureSheet(){

        @Override
        public void begin(BufferBuilder bufferBuilder, TextureManager textureManager) {
            GlStateManager.depthMask(true);
            GlStateManager.disableBlend();
        }

        @Override
        public void draw(Tessellator tessellator) {
        }

        public String toString() {
            return "CUSTOM";
        }
    };
    public static final ParticleTextureSheet NO_RENDER = new ParticleTextureSheet(){

        @Override
        public void begin(BufferBuilder bufferBuilder, TextureManager textureManager) {
        }

        @Override
        public void draw(Tessellator tessellator) {
        }

        public String toString() {
            return "NO_RENDER";
        }
    };

    public void begin(BufferBuilder var1, TextureManager var2);

    public void draw(Tessellator var1);
}

