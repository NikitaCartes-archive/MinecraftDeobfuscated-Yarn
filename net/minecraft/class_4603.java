/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.class_4587;
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
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

@Environment(value=EnvType.CLIENT)
public class class_4603 {
    private static final Identifier field_20986 = new Identifier("textures/misc/underwater.png");

    public static void method_23067(MinecraftClient minecraftClient, class_4587 arg) {
        RenderSystem.disableAlphaTest();
        if (minecraftClient.player.isInsideWall()) {
            BlockState blockState = minecraftClient.world.getBlockState(new BlockPos(minecraftClient.player));
            ClientPlayerEntity playerEntity = minecraftClient.player;
            for (int i = 0; i < 8; ++i) {
                double d = playerEntity.x + (double)(((float)((i >> 0) % 2) - 0.5f) * playerEntity.getWidth() * 0.8f);
                double e = playerEntity.y + (double)(((float)((i >> 1) % 2) - 0.5f) * 0.1f);
                double f = playerEntity.z + (double)(((float)((i >> 2) % 2) - 0.5f) * playerEntity.getWidth() * 0.8f);
                BlockPos blockPos = new BlockPos(d, e + (double)playerEntity.getStandingEyeHeight(), f);
                BlockState blockState2 = minecraftClient.world.getBlockState(blockPos);
                if (!blockState2.canSuffocate(minecraftClient.world, blockPos)) continue;
                blockState = blockState2;
            }
            if (blockState.getRenderType() != BlockRenderType.INVISIBLE) {
                class_4603.method_23068(minecraftClient, minecraftClient.getBlockRenderManager().getModels().getSprite(blockState), arg);
            }
        }
        if (!minecraftClient.player.isSpectator()) {
            if (minecraftClient.player.isInFluid(FluidTags.WATER)) {
                class_4603.method_23069(minecraftClient, arg);
            }
            if (minecraftClient.player.isOnFire()) {
                class_4603.method_23070(minecraftClient, arg);
            }
        }
        RenderSystem.enableAlphaTest();
    }

    private static void method_23068(MinecraftClient minecraftClient, Sprite sprite, class_4587 arg) {
        minecraftClient.getTextureManager().bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBufferBuilder();
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
        Matrix4f matrix4f = arg.method_22910();
        bufferBuilder.begin(7, VertexFormats.field_20887);
        bufferBuilder.method_22918(matrix4f, -1.0f, -1.0f, -0.5f).method_22915(0.1f, 0.1f, 0.1f, 0.5f).texture(m, o).next();
        bufferBuilder.method_22918(matrix4f, 1.0f, -1.0f, -0.5f).method_22915(0.1f, 0.1f, 0.1f, 0.5f).texture(l, o).next();
        bufferBuilder.method_22918(matrix4f, 1.0f, 1.0f, -0.5f).method_22915(0.1f, 0.1f, 0.1f, 0.5f).texture(l, n).next();
        bufferBuilder.method_22918(matrix4f, -1.0f, 1.0f, -0.5f).method_22915(0.1f, 0.1f, 0.1f, 0.5f).texture(m, n).next();
        bufferBuilder.end();
        BufferRenderer.draw(bufferBuilder);
    }

    private static void method_23069(MinecraftClient minecraftClient, class_4587 arg) {
        minecraftClient.getTextureManager().bindTexture(field_20986);
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBufferBuilder();
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
        Matrix4f matrix4f = arg.method_22910();
        bufferBuilder.begin(7, VertexFormats.field_20887);
        bufferBuilder.method_22918(matrix4f, -1.0f, -1.0f, -0.5f).method_22915(f, f, f, 0.1f).texture(4.0f + m, 4.0f + n).next();
        bufferBuilder.method_22918(matrix4f, 1.0f, -1.0f, -0.5f).method_22915(f, f, f, 0.1f).texture(0.0f + m, 4.0f + n).next();
        bufferBuilder.method_22918(matrix4f, 1.0f, 1.0f, -0.5f).method_22915(f, f, f, 0.1f).texture(0.0f + m, 0.0f + n).next();
        bufferBuilder.method_22918(matrix4f, -1.0f, 1.0f, -0.5f).method_22915(f, f, f, 0.1f).texture(4.0f + m, 0.0f + n).next();
        bufferBuilder.end();
        BufferRenderer.draw(bufferBuilder);
        RenderSystem.disableBlend();
    }

    private static void method_23070(MinecraftClient minecraftClient, class_4587 arg) {
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBufferBuilder();
        RenderSystem.depthFunc(519);
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        float f = 1.0f;
        for (int i = 0; i < 2; ++i) {
            arg.method_22903();
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
            arg.method_22904((float)(-(i * 2 - 1)) * 0.24f, -0.3f, 0.0);
            arg.method_22907(Vector3f.field_20705.method_23214((float)(i * 2 - 1) * 10.0f, true));
            Matrix4f matrix4f = arg.method_22910();
            bufferBuilder.begin(7, VertexFormats.field_20887);
            bufferBuilder.method_22918(matrix4f, -0.5f, -0.5f, -0.5f).method_22915(1.0f, 1.0f, 1.0f, 0.9f).texture(h, k).next();
            bufferBuilder.method_22918(matrix4f, 0.5f, -0.5f, -0.5f).method_22915(1.0f, 1.0f, 1.0f, 0.9f).texture(g, k).next();
            bufferBuilder.method_22918(matrix4f, 0.5f, 0.5f, -0.5f).method_22915(1.0f, 1.0f, 1.0f, 0.9f).texture(g, j).next();
            bufferBuilder.method_22918(matrix4f, -0.5f, 0.5f, -0.5f).method_22915(1.0f, 1.0f, 1.0f, 0.9f).texture(h, j).next();
            bufferBuilder.end();
            BufferRenderer.draw(bufferBuilder);
            arg.method_22909();
        }
        RenderSystem.disableBlend();
        RenderSystem.depthMask(true);
        RenderSystem.depthFunc(515);
    }
}

