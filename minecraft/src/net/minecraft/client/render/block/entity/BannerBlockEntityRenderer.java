package net.minecraft.client.render.block.entity;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BannerBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.WallBannerBlock;
import net.minecraft.block.entity.BannerBlockEntity;
import net.minecraft.block.entity.BannerPattern;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class BannerBlockEntityRenderer extends BlockEntityRenderer<BannerBlockEntity> {
	private final ModelPart area = new ModelPart(64, 64, 0, 0);
	private final ModelPart verticalBar;
	private final ModelPart topBar;

	public BannerBlockEntityRenderer(BlockEntityRenderDispatcher blockEntityRenderDispatcher) {
		super(blockEntityRenderDispatcher);
		this.area.addCuboid(-10.0F, 0.0F, -2.0F, 20.0F, 40.0F, 1.0F, 0.0F);
		this.verticalBar = new ModelPart(64, 64, 44, 0);
		this.verticalBar.addCuboid(-1.0F, -30.0F, -1.0F, 2.0F, 42.0F, 2.0F, 0.0F);
		this.topBar = new ModelPart(64, 64, 0, 42);
		this.topBar.addCuboid(-10.0F, -32.0F, -1.0F, 20.0F, 2.0F, 2.0F, 0.0F);
	}

	public void method_3546(BannerBlockEntity bannerBlockEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j) {
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

			Sprite sprite = this.getSprite(ModelLoader.BANNER_BASE);
			matrixStack.push();
			matrixStack.scale(0.6666667F, -0.6666667F, -0.6666667F);
			VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.method_23946());
			this.verticalBar.render(matrixStack, vertexConsumer, i, j, sprite);
			this.topBar.render(matrixStack, vertexConsumer, i, j, sprite);
			if (bannerBlockEntity.isPreview()) {
				this.area.pitch = 0.0F;
			} else {
				BlockPos blockPos = bannerBlockEntity.getPos();
				float k = (float)((long)(blockPos.getX() * 7 + blockPos.getY() * 9 + blockPos.getZ() * 13) + l) + f;
				this.area.pitch = (-0.0125F + 0.01F * MathHelper.cos(k * (float) Math.PI * 0.02F)) * (float) Math.PI;
			}

			this.area.pivotY = -32.0F;
			this.area.render(matrixStack, vertexConsumer, i, j, sprite);
			method_23802(bannerBlockEntity, matrixStack, vertexConsumerProvider, i, j, this.area, true);
			matrixStack.pop();
			matrixStack.pop();
		}
	}

	public static void method_23802(
		BannerBlockEntity bannerBlockEntity, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j, ModelPart modelPart, boolean bl
	) {
		List<BannerPattern> list = bannerBlockEntity.getPatterns();
		List<DyeColor> list2 = bannerBlockEntity.getPatternColors();
		SpriteAtlasTexture spriteAtlasTexture = MinecraftClient.getInstance().getSpriteAtlas();
		VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.method_23951());

		for (int k = 0; k < 17 && k < list.size() && k < list2.size(); k++) {
			BannerPattern bannerPattern = (BannerPattern)list.get(k);
			DyeColor dyeColor = (DyeColor)list2.get(k);
			float[] fs = dyeColor.getColorComponents();
			modelPart.render(matrixStack, vertexConsumer, i, j, spriteAtlasTexture.getSprite(bannerPattern.getSpriteId(bl)), fs[0], fs[1], fs[2]);
		}
	}
}
