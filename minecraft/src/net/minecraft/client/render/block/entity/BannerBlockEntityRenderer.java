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
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class BannerBlockEntityRenderer extends BlockEntityRenderer<BannerBlockEntity> {
	private final ModelPart field = createField();
	private final ModelPart verticalBar = new ModelPart(64, 64, 44, 0);
	private final ModelPart topBar;

	public BannerBlockEntityRenderer(BlockEntityRenderDispatcher blockEntityRenderDispatcher) {
		super(blockEntityRenderDispatcher);
		this.verticalBar.addCuboid(-1.0F, -30.0F, -1.0F, 2.0F, 42.0F, 2.0F, 0.0F);
		this.topBar = new ModelPart(64, 64, 0, 42);
		this.topBar.addCuboid(-10.0F, -32.0F, -1.0F, 20.0F, 2.0F, 2.0F, 0.0F);
	}

	public static ModelPart createField() {
		ModelPart modelPart = new ModelPart(64, 64, 0, 0);
		modelPart.addCuboid(-10.0F, 0.0F, -2.0F, 20.0F, 40.0F, 1.0F, 0.0F);
		return modelPart;
	}

	public void render(BannerBlockEntity bannerBlockEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j) {
		List<Pair<BannerPattern, DyeColor>> list = bannerBlockEntity.getPatterns();
		if (list != null) {
			float g = 0.6666667F;
			boolean bl = bannerBlockEntity.getWorld() == null;
			matrixStack.push();
			long l;
			if (bl) {
				l = 0L;
				matrixStack.translate(0.5, 0.5, 0.5);
				this.verticalBar.visible = true;
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
			VertexConsumer vertexConsumer = ModelLoader.BANNER_BASE.getVertexConsumer(vertexConsumerProvider, RenderLayer::getEntitySolid);
			this.verticalBar.render(matrixStack, vertexConsumer, i, j);
			this.topBar.render(matrixStack, vertexConsumer, i, j);
			BlockPos blockPos = bannerBlockEntity.getPos();
			float k = ((float)Math.floorMod((long)(blockPos.getX() * 7 + blockPos.getY() * 9 + blockPos.getZ() * 13) + l, 100L) + f) / 100.0F;
			this.field.pitch = (-0.0125F + 0.01F * MathHelper.cos((float) (Math.PI * 2) * k)) * (float) Math.PI;
			this.field.pivotY = -32.0F;
			method_23802(matrixStack, vertexConsumerProvider, i, j, this.field, ModelLoader.BANNER_BASE, true, list);
			matrixStack.pop();
			matrixStack.pop();
		}
	}

	public static void method_23802(
		MatrixStack matrixStack,
		VertexConsumerProvider vertexConsumerProvider,
		int i,
		int j,
		ModelPart modelPart,
		SpriteIdentifier spriteIdentifier,
		boolean bl,
		List<Pair<BannerPattern, DyeColor>> list
	) {
		modelPart.render(matrixStack, spriteIdentifier.getVertexConsumer(vertexConsumerProvider, RenderLayer::getEntitySolid), i, j);

		for (int k = 0; k < 17 && k < list.size(); k++) {
			Pair<BannerPattern, DyeColor> pair = (Pair<BannerPattern, DyeColor>)list.get(k);
			float[] fs = pair.getSecond().getColorComponents();
			SpriteIdentifier spriteIdentifier2 = new SpriteIdentifier(
				bl ? TexturedRenderLayers.BANNER_PATTERNS_ATLAS_TEXTURE : TexturedRenderLayers.SHIELD_PATTERNS_ATLAS_TEXTURE, pair.getFirst().getSpriteId(bl)
			);
			modelPart.render(matrixStack, spriteIdentifier2.getVertexConsumer(vertexConsumerProvider, RenderLayer::getEntityNoOutline), i, j, fs[0], fs[1], fs[2], 1.0F);
		}
	}
}
