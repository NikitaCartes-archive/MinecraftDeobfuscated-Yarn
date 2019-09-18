package net.minecraft.client.render.block.entity;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4576;
import net.minecraft.block.BannerBlock;
import net.minecraft.block.BlockRenderLayer;
import net.minecraft.block.BlockState;
import net.minecraft.block.WallBannerBlock;
import net.minecraft.block.entity.BannerBlockEntity;
import net.minecraft.block.entity.BannerPattern;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Quaternion;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class BannerBlockEntityRenderer extends class_4576<BannerBlockEntity> {
	private static final Logger field_20809 = LogManager.getLogger();
	private final ModelPart field_20810 = new ModelPart(64, 64, 0, 0);
	private final ModelPart field_20811;
	private final ModelPart field_20812;

	public BannerBlockEntityRenderer() {
		this.field_20810.addCuboid(-10.0F, 0.0F, -2.0F, 20.0F, 40.0F, 1.0F, 0.0F);
		this.field_20811 = new ModelPart(64, 64, 44, 0);
		this.field_20811.addCuboid(-1.0F, -30.0F, -1.0F, 2.0F, 42.0F, 2.0F, 0.0F);
		this.field_20812 = new ModelPart(64, 64, 0, 42);
		this.field_20812.addCuboid(-10.0F, -32.0F, -1.0F, 20.0F, 2.0F, 2.0F, 0.0F);
	}

	protected void method_3546(
		BannerBlockEntity bannerBlockEntity,
		double d,
		double e,
		double f,
		float g,
		int i,
		BlockRenderLayer blockRenderLayer,
		BufferBuilder bufferBuilder,
		int j,
		int k
	) {
		float h = 0.6666667F;
		boolean bl = bannerBlockEntity.getWorld() == null;
		bufferBuilder.method_22629();
		long l;
		if (bl) {
			l = 0L;
			bufferBuilder.method_22626(0.5, 0.5, f + 0.5);
			this.field_20811.visible = !bannerBlockEntity.method_22535();
		} else {
			l = bannerBlockEntity.getWorld().getTime();
			BlockState blockState = bannerBlockEntity.getCachedState();
			if (blockState.getBlock() instanceof BannerBlock) {
				bufferBuilder.method_22626(0.5, 0.5, 0.5);
				bufferBuilder.method_22622(new Quaternion(Vector3f.field_20705, (float)(-(Integer)blockState.get(BannerBlock.ROTATION) * 360) / 16.0F, true));
				this.field_20811.visible = true;
			} else {
				bufferBuilder.method_22626(0.5, -0.16666667F, 0.5);
				bufferBuilder.method_22622(new Quaternion(Vector3f.field_20705, -((Direction)blockState.get(WallBannerBlock.FACING)).asRotation(), true));
				bufferBuilder.method_22626(0.0, -0.3125, -0.4375);
				this.field_20811.visible = false;
			}
		}

		Sprite sprite = this.method_22739(ModelLoader.field_20847);
		bufferBuilder.method_22629();
		bufferBuilder.method_22627(0.6666667F, -0.6666667F, -0.6666667F);
		float m = 0.0625F;
		this.field_20811.method_22698(bufferBuilder, 0.0625F, j, k, sprite);
		this.field_20812.method_22698(bufferBuilder, 0.0625F, j, k, sprite);
		if (bannerBlockEntity.method_22535()) {
			this.field_20810.pitch = 0.0F;
		} else {
			BlockPos blockPos = bannerBlockEntity.getPos();
			float n = (float)((long)(blockPos.getX() * 7 + blockPos.getY() * 9 + blockPos.getZ() * 13) + l) + g;
			this.field_20810.pitch = (-0.0125F + 0.01F * MathHelper.cos(n * (float) Math.PI * 0.02F)) * (float) Math.PI;
		}

		this.field_20810.rotationPointY = -32.0F;
		this.field_20810.method_22698(bufferBuilder, 0.0625F, j, k, sprite);
		List<BannerPattern> list = bannerBlockEntity.getPatterns();
		List<DyeColor> list2 = bannerBlockEntity.getPatternColors();
		if (list == null) {
			field_20809.error("patterns are null");
		} else if (list2 == null) {
			field_20809.error("colors are null");
		} else {
			for (int o = 0; o < 17 && o < list.size() && o < list2.size(); o++) {
				BannerPattern bannerPattern = (BannerPattern)list.get(o);
				DyeColor dyeColor = (DyeColor)list2.get(o);
				float[] fs = dyeColor.getColorComponents();
				this.field_20810.method_22699(bufferBuilder, 0.0625F, j, k, this.method_22739(bannerPattern.method_22536()), fs[0], fs[1], fs[2]);
			}
		}

		bufferBuilder.method_22630();
		bufferBuilder.method_22630();
	}
}
