/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.hud;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

@Environment(value=EnvType.CLIENT)
public class InGameOverlayRenderer {
    private static final Identifier UNDERWATER_TEX = new Identifier("textures/misc/underwater.png");

    public static void renderOverlays(MinecraftClient minecraftClient, MatrixStack matrixStack) {
        RenderSystem.disableAlphaTest();
        if (minecraftClient.player.isInsideWall()) {
            BlockState blockState = minecraftClient.world.getBlockState(new BlockPos(minecraftClient.player));
            ClientPlayerEntity playerEntity = minecraftClient.player;
            for (int i = 0; i < 8; ++i) {
                double d = playerEntity.getX() + (double)(((float)((i >> 0) % 2) - 0.5f) * playerEntity.getWidth() * 0.8f);
                double e = playerEntity.getY() + (double)(((float)((i >> 1) % 2) - 0.5f) * 0.1f);
                double f = playerEntity.getZ() + (double)(((float)((i >> 2) % 2) - 0.5f) * playerEntity.getWidth() * 0.8f);
                BlockPos blockPos = new BlockPos(d, e + (double)playerEntity.getStandingEyeHeight(), f);
                BlockState blockState2 = minecraftClient.world.getBlockState(blockPos);
                if (!blockState2.canSuffocate(minecraftClient.world, blockPos)) continue;
                blockState = blockState2;
            }
            if (blockState.getRenderType() != BlockRenderType.INVISIBLE) {
                InGameOverlayRenderer.renderInWallOverlay(minecraftClient, minecraftClient.getBlockRenderManager().getModels().getSprite(blockState), matrixStack);
            }
        }
        if (!minecraftClient.player.isSpectator()) {
            if (minecraftClient.player.isInFluid(FluidTags.WATER)) {
                InGameOverlayRenderer.renderUnderwaterOverlay(minecraftClient, matrixStack);
            }
            if (minecraftClient.player.isOnFire()) {
                InGameOverlayRenderer.renderFireOverlay(minecraftClient, matrixStack);
            }
        }
        RenderSystem.enableAlphaTest();
    }

    private static void renderInWallOverlay(MinecraftClient minecraftClient, Sprite sprite, MatrixStack matrixStack) {
        minecraftClient.getTextureManager().bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        float f = 0.1f;
        float g = -1.0f;
        float h = 1.0f;
        float i = -1.0f;
        float j = 1.0f;
        float k = -0.5f;
        float l = sprite.getMinU();
        float m = sprite.getMaxU();
        float n = sprite.getMinV();
        float o = sprite.getMaxV();
        Matrix4f matrix4f = matrixStack.peekModel();
        bufferBuilder.begin(7, VertexFormats.POSITION_COLOR_TEXTURE);
        bufferBuilder.vertex(matrix4f, -1.0f, -1.0f, -0.5f).color(0.1f, 0.1f, 0.1f, 1.0f).texture(m, o).next();
        bufferBuilder.vertex(matrix4f, 1.0f, -1.0f, -0.5f).color(0.1f, 0.1f, 0.1f, 1.0f).texture(l, o).next();
        bufferBuilder.vertex(matrix4f, 1.0f, 1.0f, -0.5f).color(0.1f, 0.1f, 0.1f, 1.0f).texture(l, n).next();
        bufferBuilder.vertex(matrix4f, -1.0f, 1.0f, -0.5f).color(0.1f, 0.1f, 0.1f, 1.0f).texture(m, n).next();
        bufferBuilder.end();
        BufferRenderer.draw(bufferBuilder);
    }

    private static void renderUnderwaterOverlay(MinecraftClient minecraftClient, MatrixStack matrixStack) {
        minecraftClient.getTextureManager().bindTexture(UNDERWATER_TEX);
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        float f = minecraftClient.player.getBrightnessAtEyes();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        float g = 4.0f;
        float h = -1.0f;
        float i = 1.0f;
        float j = -1.0f;
        float k = 1.0f;
        float l = -0.5f;
        float m = -minecraftClient.player.yaw / 64.0f;
        float n = minecraftClient.player.pitch / 64.0f;
        Matrix4f matrix4f = matrixStack.peekModel();
        bufferBuilder.begin(7, VertexFormats.POSITION_COLOR_TEXTURE);
        bufferBuilder.vertex(matrix4f, -1.0f, -1.0f, -0.5f).color(f, f, f, 0.1f).texture(4.0f + m, 4.0f + n).next();
        bufferBuilder.vertex(matrix4f, 1.0f, -1.0f, -0.5f).color(f, f, f, 0.1f).texture(0.0f + m, 4.0f + n).next();
        bufferBuilder.vertex(matrix4f, 1.0f, 1.0f, -0.5f).color(f, f, f, 0.1f).texture(0.0f + m, 0.0f + n).next();
        bufferBuilder.vertex(matrix4f, -1.0f, 1.0f, -0.5f).color(f, f, f, 0.1f).texture(4.0f + m, 0.0f + n).next();
        bufferBuilder.end();
        BufferRenderer.draw(bufferBuilder);
        RenderSystem.disableBlend();
    }

    private static void renderFireOverlay(MinecraftClient minecraftClient, MatrixStack matrixStack) {
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        RenderSystem.depthFunc(519);
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        float f = 1.0f;
        for (int i = 0; i < 2; ++i) {
            matrixStack.push();
            Sprite sprite = minecraftClient.getSpriteAtlas().getSprite(ModelLoader.FIRE_1);
            minecraftClient.getTextureManager().bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
            float g = sprite.getMinU();
            float h = sprite.getMaxU();
            float j = sprite.getMinV();
            float k = sprite.getMaxV();
            float l = -0.5f;
            float m = 0.5f;
            float n = -0.5f;
            float o = 0.5f;
            float p = -0.5f;
            matrixStack.translate((float)(-(i * 2 - 1)) * 0.24f, -0.3f, 0.0);
            matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion((float)(i * 2 - 1) * 10.0f));
            Matrix4f matrix4f = matrixStack.peekModel();
            bufferBuilder.begin(7, VertexFormats.POSITION_COLOR_TEXTURE);
            bufferBuilder.vertex(matrix4f, -0.5f, -0.5f, -0.5f).color(1.0f, 1.0f, 1.0f, 0.9f).texture(h, k).next();
            bufferBuilder.vertex(matrix4f, 0.5f, -0.5f, -0.5f).color(1.0f, 1.0f, 1.0f, 0.9f).texture(g, k).next();
            bufferBuilder.vertex(matrix4f, 0.5f, 0.5f, -0.5f).color(1.0f, 1.0f, 1.0f, 0.9f).texture(g, j).next();
            bufferBuilder.vertex(matrix4f, -0.5f, 0.5f, -0.5f).color(1.0f, 1.0f, 1.0f, 0.9f).texture(h, j).next();
            bufferBuilder.end();
            BufferRenderer.draw(bufferBuilder);
            matrixStack.pop();
        }
        RenderSystem.disableBlend();
        RenderSystem.depthMask(true);
        RenderSystem.depthFunc(515);
    }
}

