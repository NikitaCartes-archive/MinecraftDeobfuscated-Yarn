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
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3f;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class InGameOverlayRenderer {
    private static final Identifier UNDERWATER_TEXTURE = new Identifier("textures/misc/underwater.png");

    public static void renderOverlays(MinecraftClient client, MatrixStack matrices) {
        BlockState blockState;
        ClientPlayerEntity playerEntity = client.player;
        if (!playerEntity.noClip && (blockState = InGameOverlayRenderer.getInWallBlockState(playerEntity)) != null) {
            InGameOverlayRenderer.renderInWallOverlay(client.getBlockRenderManager().getModels().getSprite(blockState), matrices);
        }
        if (!client.player.isSpectator()) {
            if (client.player.isSubmergedIn(FluidTags.WATER)) {
                InGameOverlayRenderer.renderUnderwaterOverlay(client, matrices);
            }
            if (client.player.isOnFire()) {
                InGameOverlayRenderer.renderFireOverlay(client, matrices);
            }
        }
    }

    @Nullable
    private static BlockState getInWallBlockState(PlayerEntity player) {
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        for (int i = 0; i < 8; ++i) {
            double d = player.getX() + (double)(((float)((i >> 0) % 2) - 0.5f) * player.getWidth() * 0.8f);
            double e = player.getEyeY() + (double)(((float)((i >> 1) % 2) - 0.5f) * 0.1f);
            double f = player.getZ() + (double)(((float)((i >> 2) % 2) - 0.5f) * player.getWidth() * 0.8f);
            mutable.set(d, e, f);
            BlockState blockState = player.world.getBlockState(mutable);
            if (blockState.getRenderType() == BlockRenderType.INVISIBLE || !blockState.shouldBlockVision(player.world, mutable)) continue;
            return blockState;
        }
        return null;
    }

    private static void renderInWallOverlay(Sprite sprite, MatrixStack matrixStack) {
        RenderSystem.setShaderTexture(0, sprite.getAtlas().getId());
        RenderSystem.setShader(GameRenderer::getPositionColorTexShader);
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
        Matrix4f matrix4f = matrixStack.peek().getModel();
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR_TEXTURE);
        bufferBuilder.vertex(matrix4f, -1.0f, -1.0f, -0.5f).color(0.1f, 0.1f, 0.1f, 1.0f).texture(m, o).next();
        bufferBuilder.vertex(matrix4f, 1.0f, -1.0f, -0.5f).color(0.1f, 0.1f, 0.1f, 1.0f).texture(l, o).next();
        bufferBuilder.vertex(matrix4f, 1.0f, 1.0f, -0.5f).color(0.1f, 0.1f, 0.1f, 1.0f).texture(l, n).next();
        bufferBuilder.vertex(matrix4f, -1.0f, 1.0f, -0.5f).color(0.1f, 0.1f, 0.1f, 1.0f).texture(m, n).next();
        bufferBuilder.end();
        BufferRenderer.draw(bufferBuilder);
    }

    private static void renderUnderwaterOverlay(MinecraftClient minecraftClient, MatrixStack matrixStack) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.enableTexture();
        RenderSystem.setShaderTexture(0, UNDERWATER_TEXTURE);
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        float f = minecraftClient.player.getBrightnessAtEyes();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderColor(f, f, f, 0.1f);
        float g = 4.0f;
        float h = -1.0f;
        float i = 1.0f;
        float j = -1.0f;
        float k = 1.0f;
        float l = -0.5f;
        float m = -minecraftClient.player.yaw / 64.0f;
        float n = minecraftClient.player.pitch / 64.0f;
        Matrix4f matrix4f = matrixStack.peek().getModel();
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
        bufferBuilder.vertex(matrix4f, -1.0f, -1.0f, -0.5f).texture(4.0f + m, 4.0f + n).next();
        bufferBuilder.vertex(matrix4f, 1.0f, -1.0f, -0.5f).texture(0.0f + m, 4.0f + n).next();
        bufferBuilder.vertex(matrix4f, 1.0f, 1.0f, -0.5f).texture(0.0f + m, 0.0f + n).next();
        bufferBuilder.vertex(matrix4f, -1.0f, 1.0f, -0.5f).texture(4.0f + m, 0.0f + n).next();
        bufferBuilder.end();
        BufferRenderer.draw(bufferBuilder);
        RenderSystem.disableBlend();
    }

    private static void renderFireOverlay(MinecraftClient client, MatrixStack matrices) {
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        RenderSystem.setShader(GameRenderer::getPositionColorTexShader);
        RenderSystem.depthFunc(519);
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableTexture();
        Sprite sprite = ModelLoader.FIRE_1.getSprite();
        RenderSystem.setShaderTexture(0, sprite.getAtlas().getId());
        float f = sprite.getMinU();
        float g = sprite.getMaxU();
        float h = (f + g) / 2.0f;
        float i = sprite.getMinV();
        float j = sprite.getMaxV();
        float k = (i + j) / 2.0f;
        float l = sprite.getAnimationFrameDelta();
        float m = MathHelper.lerp(l, f, h);
        float n = MathHelper.lerp(l, g, h);
        float o = MathHelper.lerp(l, i, k);
        float p = MathHelper.lerp(l, j, k);
        float q = 1.0f;
        for (int r = 0; r < 2; ++r) {
            matrices.push();
            float s = -0.5f;
            float t = 0.5f;
            float u = -0.5f;
            float v = 0.5f;
            float w = -0.5f;
            matrices.translate((float)(-(r * 2 - 1)) * 0.24f, -0.3f, 0.0);
            matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion((float)(r * 2 - 1) * 10.0f));
            Matrix4f matrix4f = matrices.peek().getModel();
            bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR_TEXTURE);
            bufferBuilder.vertex(matrix4f, -0.5f, -0.5f, -0.5f).color(1.0f, 1.0f, 1.0f, 0.9f).texture(n, p).next();
            bufferBuilder.vertex(matrix4f, 0.5f, -0.5f, -0.5f).color(1.0f, 1.0f, 1.0f, 0.9f).texture(m, p).next();
            bufferBuilder.vertex(matrix4f, 0.5f, 0.5f, -0.5f).color(1.0f, 1.0f, 1.0f, 0.9f).texture(m, o).next();
            bufferBuilder.vertex(matrix4f, -0.5f, 0.5f, -0.5f).color(1.0f, 1.0f, 1.0f, 0.9f).texture(n, o).next();
            bufferBuilder.end();
            BufferRenderer.draw(bufferBuilder);
            matrices.pop();
        }
        RenderSystem.disableBlend();
        RenderSystem.depthMask(true);
        RenderSystem.depthFunc(515);
    }
}

