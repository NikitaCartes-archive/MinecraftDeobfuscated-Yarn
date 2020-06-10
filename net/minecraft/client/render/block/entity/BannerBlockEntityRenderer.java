/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.block.entity;

import com.mojang.datafixers.util.Pair;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BannerBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.WallBannerBlock;
import net.minecraft.block.entity.BannerBlockEntity;
import net.minecraft.block.entity.BannerPattern;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class BannerBlockEntityRenderer
extends BlockEntityRenderer<BannerBlockEntity> {
    private final ModelPart banner = BannerBlockEntityRenderer.createBanner();
    private final ModelPart pillar = new ModelPart(64, 64, 44, 0);
    private final ModelPart crossbar;

    public BannerBlockEntityRenderer(BlockEntityRenderDispatcher blockEntityRenderDispatcher) {
        super(blockEntityRenderDispatcher);
        this.pillar.addCuboid(-1.0f, -30.0f, -1.0f, 2.0f, 42.0f, 2.0f, 0.0f);
        this.crossbar = new ModelPart(64, 64, 0, 42);
        this.crossbar.addCuboid(-10.0f, -32.0f, -1.0f, 20.0f, 2.0f, 2.0f, 0.0f);
    }

    public static ModelPart createBanner() {
        ModelPart modelPart = new ModelPart(64, 64, 0, 0);
        modelPart.addCuboid(-10.0f, 0.0f, -2.0f, 20.0f, 40.0f, 1.0f, 0.0f);
        return modelPart;
    }

    @Override
    public void render(BannerBlockEntity bannerBlockEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j) {
        long l;
        List<Pair<BannerPattern, DyeColor>> list = bannerBlockEntity.getPatterns();
        if (list == null) {
            return;
        }
        float g = 0.6666667f;
        boolean bl = bannerBlockEntity.getWorld() == null;
        matrixStack.push();
        if (bl) {
            l = 0L;
            matrixStack.translate(0.5, 0.5, 0.5);
            this.pillar.visible = true;
        } else {
            float h;
            l = bannerBlockEntity.getWorld().getTime();
            BlockState blockState = bannerBlockEntity.getCachedState();
            if (blockState.getBlock() instanceof BannerBlock) {
                matrixStack.translate(0.5, 0.5, 0.5);
                h = (float)(-blockState.get(BannerBlock.ROTATION).intValue() * 360) / 16.0f;
                matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(h));
                this.pillar.visible = true;
            } else {
                matrixStack.translate(0.5, -0.1666666716337204, 0.5);
                h = -blockState.get(WallBannerBlock.FACING).asRotation();
                matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(h));
                matrixStack.translate(0.0, -0.3125, -0.4375);
                this.pillar.visible = false;
            }
        }
        matrixStack.push();
        matrixStack.scale(0.6666667f, -0.6666667f, -0.6666667f);
        VertexConsumer vertexConsumer = ModelLoader.BANNER_BASE.getVertexConsumer(vertexConsumerProvider, RenderLayer::getEntitySolid);
        this.pillar.render(matrixStack, vertexConsumer, i, j);
        this.crossbar.render(matrixStack, vertexConsumer, i, j);
        BlockPos blockPos = bannerBlockEntity.getPos();
        float k = ((float)Math.floorMod((long)(blockPos.getX() * 7 + blockPos.getY() * 9 + blockPos.getZ() * 13) + l, 100L) + f) / 100.0f;
        this.banner.pitch = (-0.0125f + 0.01f * MathHelper.cos((float)Math.PI * 2 * k)) * (float)Math.PI;
        this.banner.pivotY = -32.0f;
        BannerBlockEntityRenderer.method_29999(matrixStack, vertexConsumerProvider, i, j, this.banner, ModelLoader.BANNER_BASE, true, list);
        matrixStack.pop();
        matrixStack.pop();
    }

    public static void method_29999(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j, ModelPart modelPart, SpriteIdentifier spriteIdentifier, boolean bl, List<Pair<BannerPattern, DyeColor>> list) {
        BannerBlockEntityRenderer.renderCanvas(matrixStack, vertexConsumerProvider, i, j, modelPart, spriteIdentifier, bl, list, false);
    }

    public static void renderCanvas(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, ModelPart canvas, SpriteIdentifier baseSprite, boolean isBanner, List<Pair<BannerPattern, DyeColor>> patterns, boolean bl) {
        canvas.render(matrices, baseSprite.method_30001(vertexConsumers, RenderLayer::getEntitySolid, bl), light, overlay);
        for (int i = 0; i < 17 && i < patterns.size(); ++i) {
            Pair<BannerPattern, DyeColor> pair = patterns.get(i);
            float[] fs = pair.getSecond().getColorComponents();
            SpriteIdentifier spriteIdentifier = new SpriteIdentifier(isBanner ? TexturedRenderLayers.BANNER_PATTERNS_ATLAS_TEXTURE : TexturedRenderLayers.SHIELD_PATTERNS_ATLAS_TEXTURE, pair.getFirst().getSpriteId(isBanner));
            canvas.render(matrices, spriteIdentifier.getVertexConsumer(vertexConsumers, RenderLayer::getEntityNoOutline), light, overlay, fs[0], fs[1], fs[2], 1.0f);
        }
    }
}

