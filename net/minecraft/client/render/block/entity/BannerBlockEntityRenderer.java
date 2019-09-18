/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.block.entity;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BannerBlock;
import net.minecraft.block.BlockRenderLayer;
import net.minecraft.block.BlockState;
import net.minecraft.block.WallBannerBlock;
import net.minecraft.block.entity.BannerBlockEntity;
import net.minecraft.block.entity.BannerPattern;
import net.minecraft.class_4576;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Quaternion;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(value=EnvType.CLIENT)
public class BannerBlockEntityRenderer
extends class_4576<BannerBlockEntity> {
    private static final Logger field_20809 = LogManager.getLogger();
    private final ModelPart field_20810 = new ModelPart(64, 64, 0, 0);
    private final ModelPart field_20811;
    private final ModelPart field_20812;

    public BannerBlockEntityRenderer() {
        this.field_20810.addCuboid(-10.0f, 0.0f, -2.0f, 20.0f, 40.0f, 1.0f, 0.0f);
        this.field_20811 = new ModelPart(64, 64, 44, 0);
        this.field_20811.addCuboid(-1.0f, -30.0f, -1.0f, 2.0f, 42.0f, 2.0f, 0.0f);
        this.field_20812 = new ModelPart(64, 64, 0, 42);
        this.field_20812.addCuboid(-10.0f, -32.0f, -1.0f, 20.0f, 2.0f, 2.0f, 0.0f);
    }

    protected void method_3546(BannerBlockEntity bannerBlockEntity, double d, double e, double f, float g, int i, BlockRenderLayer blockRenderLayer, BufferBuilder bufferBuilder, int j, int k) {
        long l;
        float h = 0.6666667f;
        boolean bl = bannerBlockEntity.getWorld() == null;
        bufferBuilder.method_22629();
        if (bl) {
            l = 0L;
            bufferBuilder.method_22626(0.5, 0.5, f + 0.5);
            this.field_20811.visible = !bannerBlockEntity.method_22535();
        } else {
            l = bannerBlockEntity.getWorld().getTime();
            BlockState blockState = bannerBlockEntity.getCachedState();
            if (blockState.getBlock() instanceof BannerBlock) {
                bufferBuilder.method_22626(0.5, 0.5, 0.5);
                bufferBuilder.method_22622(new Quaternion(Vector3f.field_20705, (float)(-blockState.get(BannerBlock.ROTATION).intValue() * 360) / 16.0f, true));
                this.field_20811.visible = true;
            } else {
                bufferBuilder.method_22626(0.5, -0.1666666716337204, 0.5);
                bufferBuilder.method_22622(new Quaternion(Vector3f.field_20705, -blockState.get(WallBannerBlock.FACING).asRotation(), true));
                bufferBuilder.method_22626(0.0, -0.3125, -0.4375);
                this.field_20811.visible = false;
            }
        }
        Sprite sprite = this.method_22739(ModelLoader.field_20847);
        bufferBuilder.method_22629();
        bufferBuilder.method_22627(0.6666667f, -0.6666667f, -0.6666667f);
        float m = 0.0625f;
        this.field_20811.method_22698(bufferBuilder, 0.0625f, j, k, sprite);
        this.field_20812.method_22698(bufferBuilder, 0.0625f, j, k, sprite);
        if (bannerBlockEntity.method_22535()) {
            this.field_20810.pitch = 0.0f;
        } else {
            BlockPos blockPos = bannerBlockEntity.getPos();
            float n = (float)((long)(blockPos.getX() * 7 + blockPos.getY() * 9 + blockPos.getZ() * 13) + l) + g;
            this.field_20810.pitch = (-0.0125f + 0.01f * MathHelper.cos(n * (float)Math.PI * 0.02f)) * (float)Math.PI;
        }
        this.field_20810.rotationPointY = -32.0f;
        this.field_20810.method_22698(bufferBuilder, 0.0625f, j, k, sprite);
        List<BannerPattern> list = bannerBlockEntity.getPatterns();
        List<DyeColor> list2 = bannerBlockEntity.getPatternColors();
        if (list == null) {
            field_20809.error("patterns are null");
        } else if (list2 == null) {
            field_20809.error("colors are null");
        } else {
            for (int o = 0; o < 17 && o < list.size() && o < list2.size(); ++o) {
                BannerPattern bannerPattern = list.get(o);
                DyeColor dyeColor = list2.get(o);
                float[] fs = dyeColor.getColorComponents();
                this.field_20810.method_22699(bufferBuilder, 0.0625f, j, k, this.method_22739(bannerPattern.method_22536()), fs[0], fs[1], fs[2]);
            }
        }
        bufferBuilder.method_22630();
        bufferBuilder.method_22630();
    }
}

