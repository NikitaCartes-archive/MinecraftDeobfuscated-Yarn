package net.minecraft.client.render.block.entity;

import com.mojang.datafixers.util.Pair;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_8293;
import net.minecraft.block.BannerBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.WallBannerBlock;
import net.minecraft.block.entity.BannerBlockEntity;
import net.minecraft.block.entity.BannerPattern;
import net.minecraft.block.entity.BannerPatterns;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.RotationPropertyHelper;
import org.joml.AxisAngle4f;
import org.joml.Quaternionf;

@Environment(EnvType.CLIENT)
public class BannerBlockEntityRenderer implements BlockEntityRenderer<BannerBlockEntity> {
	private static final int WIDTH = 20;
	private static final int HEIGHT = 40;
	private static final int ROTATIONS = 16;
	public static final String BANNER = "flag";
	private static final String PILLAR = "pole";
	private static final String CROSSBAR = "bar";
	private final ModelPart banner;
	private final ModelPart pillar;
	private final ModelPart crossbar;
	private final ModelPart bedFoot;
	private final ModelPart bedHead;

	public BannerBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
		ModelPart modelPart = ctx.getLayerModelPart(EntityModelLayers.BANNER);
		this.banner = modelPart.getChild("flag");
		this.pillar = modelPart.getChild("pole");
		this.crossbar = modelPart.getChild("bar");
		this.bedFoot = ctx.getLayerModelPart(EntityModelLayers.BED_FOOT);
		this.bedHead = ctx.getLayerModelPart(EntityModelLayers.BED_HEAD);
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		modelPartData.addChild("flag", ModelPartBuilder.create().uv(0, 0).cuboid(-10.0F, 0.0F, -2.0F, 20.0F, 40.0F, 1.0F), ModelTransform.NONE);
		modelPartData.addChild("pole", ModelPartBuilder.create().uv(44, 0).cuboid(-1.0F, -30.0F, -1.0F, 2.0F, 42.0F, 2.0F), ModelTransform.NONE);
		modelPartData.addChild("bar", ModelPartBuilder.create().uv(0, 42).cuboid(-10.0F, -32.0F, -1.0F, 20.0F, 2.0F, 2.0F), ModelTransform.NONE);
		return TexturedModelData.of(modelData, 64, 64);
	}

	public void render(BannerBlockEntity bannerBlockEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j) {
		List<Pair<RegistryEntry<BannerPattern>, DyeColor>> list = bannerBlockEntity.getPatterns();
		float g = 0.6666667F;
		boolean bl = bannerBlockEntity.getWorld() == null;
		matrixStack.push();
		SpriteIdentifier spriteIdentifier = TexturedRenderLayers.BED_TEXTURES[bannerBlockEntity.getColorForState().getId()];
		long l;
		if (bl) {
			l = 0L;
			matrixStack.translate(0.5F, 0.5F, 0.5F);
			this.pillar.visible = true;
		} else {
			l = bannerBlockEntity.getWorld().getTime();
			BlockState blockState = bannerBlockEntity.getCachedState();
			if (blockState.getBlock() instanceof BannerBlock) {
				matrixStack.translate(0.5F, 0.5F, 0.5F);
				float h = -RotationPropertyHelper.toDegrees((Integer)blockState.get(BannerBlock.ROTATION));
				matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(h));
				this.pillar.visible = true;
			} else {
				matrixStack.translate(0.5F, -0.16666667F, 0.5F);
				float h = -((Direction)blockState.get(WallBannerBlock.FACING)).asRotation();
				matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(h));
				matrixStack.translate(0.0F, -0.3125F, -0.4375F);
				this.pillar.visible = false;
			}
		}

		matrixStack.push();
		matrixStack.scale(0.6666667F, -0.6666667F, -0.6666667F);
		VertexConsumer vertexConsumer = ModelLoader.BANNER_BASE.getVertexConsumer(vertexConsumerProvider, RenderLayer::getEntitySolid);
		this.pillar.render(matrixStack, vertexConsumer, i, j);
		this.crossbar.render(matrixStack, vertexConsumer, i, j);
		BlockPos blockPos = bannerBlockEntity.getPos();
		float k = ((float)Math.floorMod((long)(blockPos.getX() * 7 + blockPos.getY() * 9 + blockPos.getZ() * 13) + l, 100L) + f) / 100.0F;
		float m = (-0.0125F + 0.01F * MathHelper.cos((float) (Math.PI * 2) * k)) * (float) Math.PI;
		this.banner.pitch = m;
		this.banner.pivotY = -32.0F;
		renderCanvas(matrixStack, vertexConsumerProvider, i, j, this.banner, ModelLoader.BANNER_BASE, true, list);
		if (class_8293.field_43615.method_50116()) {
			matrixStack.push();
			VertexConsumer vertexConsumer2 = spriteIdentifier.getVertexConsumer(vertexConsumerProvider, RenderLayer::getEntitySolid);
			matrixStack.translate(0.0F, -2.0F, 0.01F);
			matrixStack.multiply(new Quaternionf(new AxisAngle4f(m, 1.0F, 0.0F, 0.0F)));
			matrixStack.scale(1.2F, 1.25F, 1.0F);
			matrixStack.translate(-0.5, 0.0, -0.125);
			this.bedHead.render(matrixStack, vertexConsumer2, i, j);
			matrixStack.translate(0.0F, 1.0F, 0.0F);
			this.bedFoot.render(matrixStack, vertexConsumer2, i, j);
			matrixStack.pop();
		}

		matrixStack.pop();
		matrixStack.pop();
	}

	public static void renderCanvas(
		MatrixStack matrices,
		VertexConsumerProvider vertexConsumers,
		int light,
		int overlay,
		ModelPart canvas,
		SpriteIdentifier baseSprite,
		boolean isBanner,
		List<Pair<RegistryEntry<BannerPattern>, DyeColor>> patterns
	) {
		renderCanvas(matrices, vertexConsumers, light, overlay, canvas, baseSprite, isBanner, patterns, false);
	}

	public static void renderCanvas(
		MatrixStack matrices,
		VertexConsumerProvider vertexConsumers,
		int light,
		int overlay,
		ModelPart canvas,
		SpriteIdentifier baseSprite,
		boolean isBanner,
		List<Pair<RegistryEntry<BannerPattern>, DyeColor>> patterns,
		boolean glint
	) {
		boolean bl = isBanner && class_8293.field_43615.method_50116();
		if (!bl) {
			canvas.render(matrices, baseSprite.getVertexConsumer(vertexConsumers, RenderLayer::getEntitySolid, glint), light, overlay);
		}

		for (int i = 0; i < 17 && i < patterns.size(); i++) {
			Pair<RegistryEntry<BannerPattern>, DyeColor> pair = (Pair<RegistryEntry<BannerPattern>, DyeColor>)patterns.get(i);
			float[] fs = pair.getSecond().getColorComponents();
			if (!bl || !pair.getFirst().matchesKey(BannerPatterns.BASE)) {
				pair.getFirst()
					.getKey()
					.map(key -> isBanner ? TexturedRenderLayers.getBannerPatternTextureId(key) : TexturedRenderLayers.getShieldPatternTextureId(key))
					.ifPresent(
						sprite -> canvas.render(matrices, sprite.getVertexConsumer(vertexConsumers, RenderLayer::getEntityNoOutline), light, overlay, fs[0], fs[1], fs[2], 1.0F)
					);
			}
		}
	}
}
