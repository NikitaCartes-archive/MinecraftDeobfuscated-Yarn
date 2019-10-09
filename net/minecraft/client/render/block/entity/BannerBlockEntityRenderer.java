/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.block.entity;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BannerBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.WallBannerBlock;
import net.minecraft.block.entity.BannerBlockEntity;
import net.minecraft.block.entity.BannerPattern;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.LayeredVertexConsumerStorage;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.MatrixStack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(value=EnvType.CLIENT)
public class BannerBlockEntityRenderer
extends BlockEntityRenderer<BannerBlockEntity> {
    private static final Logger LOGGER = LogManager.getLogger();
    private final ModelPart field_20810 = new ModelPart(64, 64, 0, 0);
    private final ModelPart field_20811;
    private final ModelPart field_20812;

    public BannerBlockEntityRenderer(BlockEntityRenderDispatcher blockEntityRenderDispatcher) {
        super(blockEntityRenderDispatcher);
        this.field_20810.addCuboid(-10.0f, 0.0f, -2.0f, 20.0f, 40.0f, 1.0f, 0.0f);
        this.field_20811 = new ModelPart(64, 64, 44, 0);
        this.field_20811.addCuboid(-1.0f, -30.0f, -1.0f, 2.0f, 42.0f, 2.0f, 0.0f);
        this.field_20812 = new ModelPart(64, 64, 0, 42);
        this.field_20812.addCuboid(-10.0f, -32.0f, -1.0f, 20.0f, 2.0f, 2.0f, 0.0f);
    }

    public void method_3546(BannerBlockEntity bannerBlockEntity, double d, double e, double f, float g, MatrixStack matrixStack, LayeredVertexConsumerStorage layeredVertexConsumerStorage, int i, int j) {
        float k;
        long l;
        float h = 0.6666667f;
        boolean bl = bannerBlockEntity.getWorld() == null;
        matrixStack.push();
        if (bl) {
            l = 0L;
            matrixStack.translate(0.5, 0.5, f + 0.5);
            this.field_20811.visible = !bannerBlockEntity.method_22535();
        } else {
            l = bannerBlockEntity.getWorld().getTime();
            BlockState blockState = bannerBlockEntity.getCachedState();
            if (blockState.getBlock() instanceof BannerBlock) {
                matrixStack.translate(0.5, 0.5, 0.5);
                k = (float)(-blockState.get(BannerBlock.ROTATION).intValue() * 360) / 16.0f;
                matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion(k));
                this.field_20811.visible = true;
            } else {
                matrixStack.translate(0.5, -0.1666666716337204, 0.5);
                k = -blockState.get(WallBannerBlock.FACING).asRotation();
                matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion(k));
                matrixStack.translate(0.0, -0.3125, -0.4375);
                this.field_20811.visible = false;
            }
        }
        Sprite sprite = this.getSprite(ModelLoader.field_20847);
        matrixStack.push();
        matrixStack.scale(0.6666667f, -0.6666667f, -0.6666667f);
        k = 0.0625f;
        VertexConsumer vertexConsumer = layeredVertexConsumerStorage.getBuffer(RenderLayer.getEntitySolid(SpriteAtlasTexture.BLOCK_ATLAS_TEX));
        this.field_20811.render(matrixStack, vertexConsumer, 0.0625f, i, j, sprite);
        this.field_20812.render(matrixStack, vertexConsumer, 0.0625f, i, j, sprite);
        if (bannerBlockEntity.method_22535()) {
            this.field_20810.pitch = 0.0f;
        } else {
            BlockPos blockPos = bannerBlockEntity.getPos();
            float m = (float)((long)(blockPos.getX() * 7 + blockPos.getY() * 9 + blockPos.getZ() * 13) + l) + g;
            this.field_20810.pitch = (-0.0125f + 0.01f * MathHelper.cos(m * (float)Math.PI * 0.02f)) * (float)Math.PI;
        }
        this.field_20810.pivotY = -32.0f;
        this.field_20810.render(matrixStack, vertexConsumer, 0.0625f, i, j, sprite);
        List<BannerPattern> list = bannerBlockEntity.getPatterns();
        List<DyeColor> list2 = bannerBlockEntity.getPatternColors();
        VertexConsumer vertexConsumer2 = layeredVertexConsumerStorage.getBuffer(RenderLayer.getEntityTranslucent(SpriteAtlasTexture.BLOCK_ATLAS_TEX));
        if (list == null) {
            LOGGER.error("patterns are null");
        } else if (list2 == null) {
            LOGGER.error("colors are null");
        } else {
            for (int n = 0; n < 17 && n < list.size() && n < list2.size(); ++n) {
                BannerPattern bannerPattern = list.get(n);
                DyeColor dyeColor = list2.get(n);
                float[] fs = dyeColor.getColorComponents();
                this.field_20810.render(matrixStack, vertexConsumer2, 0.0625f, i, j, this.getSprite(bannerPattern.method_22536()), fs[0], fs[1], fs[2]);
            }
        }
        matrixStack.pop();
        matrixStack.pop();
    }
}

