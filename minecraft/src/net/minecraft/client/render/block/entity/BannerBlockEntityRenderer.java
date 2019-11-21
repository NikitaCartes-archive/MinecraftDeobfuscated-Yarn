package net.minecraft.client.render.block.entity;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4722;
import net.minecraft.class_4730;
import net.minecraft.block.BannerBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.WallBannerBlock;
import net.minecraft.block.entity.BannerBlockEntity;
import net.minecraft.block.entity.BannerPattern;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class BannerBlockEntityRenderer extends BlockEntityRenderer<BannerBlockEntity> {
	private final ModelPart area = method_24080();
	private final ModelPart verticalBar = new ModelPart(64, 64, 44, 0);
	private final ModelPart topBar;

	public BannerBlockEntityRenderer(BlockEntityRenderDispatcher blockEntityRenderDispatcher) {
		super(blockEntityRenderDispatcher);
		this.verticalBar.addCuboid(-1.0F, -30.0F, -1.0F, 2.0F, 42.0F, 2.0F, 0.0F);
		this.topBar = new ModelPart(64, 64, 0, 42);
		this.topBar.addCuboid(-10.0F, -32.0F, -1.0F, 20.0F, 2.0F, 2.0F, 0.0F);
	}

	public static ModelPart method_24080() {
		ModelPart modelPart = new ModelPart(64, 64, 0, 0);
		modelPart.addCuboid(-10.0F, 0.0F, -2.0F, 20.0F, 40.0F, 1.0F, 0.0F);
		return modelPart;
	}

	public void render(BannerBlockEntity bannerBlockEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j) {
		if (bannerBlockEntity.getPatterns() != null) {
			float g = 0.6666667F;
			boolean bl = bannerBlockEntity.getWorld() == null;
			matrixStack.push();
			long l;
			if (bl) {
				l = 0L;
				matrixStack.translate(0.5, 0.5, 0.5);
				this.verticalBar.visible = !bannerBlockEntity.isPreview();
			} else {
				l = bannerBlockEntity.getWorld().getTime();
				BlockState blockState = bannerBlockEntity.getCachedState();
				if (blockState.getBlock() instanceof BannerBlock) {
					matrixStack.translate(0.5, 0.5, 0.5);
					float h = (float)(-(Integer)blockState.get(BannerBlock.ROTATION) * 360) / 16.0F;
					matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(h));
					this.verticalBar.visible = true;
				} else {
					matrixStack.translate(0.5, -0.16666667F, 0.5);
					float h = -((Direction)blockState.get(WallBannerBlock.FACING)).asRotation();
					matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(h));
					matrixStack.translate(0.0, -0.3125, -0.4375);
					this.verticalBar.visible = false;
				}
			}

			matrixStack.push();
			matrixStack.scale(0.6666667F, -0.6666667F, -0.6666667F);
			VertexConsumer vertexConsumer = ModelLoader.BANNER_BASE.method_24145(vertexConsumerProvider, RenderLayer::getEntitySolid);
			this.verticalBar.render(matrixStack, vertexConsumer, i, j);
			this.topBar.render(matrixStack, vertexConsumer, i, j);
			if (bannerBlockEntity.isPreview()) {
				this.area.pitch = 0.0F;
			} else {
				BlockPos blockPos = bannerBlockEntity.getPos();
				float k = (float)((long)(blockPos.getX() * 7 + blockPos.getY() * 9 + blockPos.getZ() * 13) + l) + f;
				this.area.pitch = (-0.0125F + 0.01F * MathHelper.cos(k * (float) Math.PI * 0.02F)) * (float) Math.PI;
			}

			this.area.pivotY = -32.0F;
			method_23802(bannerBlockEntity, matrixStack, vertexConsumerProvider, i, j, this.area, true);
			matrixStack.pop();
			matrixStack.pop();
		}
	}

	public static void method_23802(
		BannerBlockEntity bannerBlockEntity, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j, ModelPart modelPart, boolean bl
	) {
		modelPart.render(matrixStack, ModelLoader.BANNER_BASE.method_24145(vertexConsumerProvider, RenderLayer::getEntitySolid), i, j);
		List<BannerPattern> list = bannerBlockEntity.getPatterns();
		List<DyeColor> list2 = bannerBlockEntity.getPatternColors();

		for (int k = 0; k < 17 && k < list.size() && k < list2.size(); k++) {
			BannerPattern bannerPattern = (BannerPattern)list.get(k);
			DyeColor dyeColor = (DyeColor)list2.get(k);
			float[] fs = dyeColor.getColorComponents();
			class_4730 lv = new class_4730(bl ? class_4722.field_21706 : class_4722.field_21707, bannerPattern.getSpriteId(bl));
			modelPart.render(matrixStack, lv.method_24145(vertexConsumerProvider, RenderLayer::getEntityNoOutline), i, j, fs[0], fs[1], fs[2], 1.0F);
		}
	}
}
