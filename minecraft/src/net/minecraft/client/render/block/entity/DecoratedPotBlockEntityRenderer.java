package net.minecraft.client.render.block.entity;

import java.util.EnumSet;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.DecoratedPotPatterns;
import net.minecraft.block.entity.DecoratedPotBlockEntity;
import net.minecraft.block.entity.Sherds;
import net.minecraft.client.model.Dilation;
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
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

@Environment(EnvType.CLIENT)
public class DecoratedPotBlockEntityRenderer implements BlockEntityRenderer<DecoratedPotBlockEntity> {
	private static final String NECK = "neck";
	private static final String FRONT = "front";
	private static final String BACK = "back";
	private static final String LEFT = "left";
	private static final String RIGHT = "right";
	private static final String TOP = "top";
	private static final String BOTTOM = "bottom";
	private final ModelPart neck;
	private final ModelPart front;
	private final ModelPart back;
	private final ModelPart left;
	private final ModelPart right;
	private final ModelPart top;
	private final ModelPart bottom;
	private final SpriteIdentifier baseTexture = (SpriteIdentifier)Objects.requireNonNull(
		TexturedRenderLayers.getDecoratedPotPatternTextureId(DecoratedPotPatterns.DECORATED_POT_BASE_KEY)
	);
	private static final float field_46728 = 0.125F;

	public DecoratedPotBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
		ModelPart modelPart = context.getLayerModelPart(EntityModelLayers.DECORATED_POT_BASE);
		this.neck = modelPart.getChild(EntityModelPartNames.NECK);
		this.top = modelPart.getChild("top");
		this.bottom = modelPart.getChild("bottom");
		ModelPart modelPart2 = context.getLayerModelPart(EntityModelLayers.DECORATED_POT_SIDES);
		this.front = modelPart2.getChild("front");
		this.back = modelPart2.getChild("back");
		this.left = modelPart2.getChild("left");
		this.right = modelPart2.getChild("right");
	}

	public static TexturedModelData getTopBottomNeckTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		Dilation dilation = new Dilation(0.2F);
		Dilation dilation2 = new Dilation(-0.1F);
		modelPartData.addChild(
			EntityModelPartNames.NECK,
			ModelPartBuilder.create().uv(0, 0).cuboid(4.0F, 17.0F, 4.0F, 8.0F, 3.0F, 8.0F, dilation2).uv(0, 5).cuboid(5.0F, 20.0F, 5.0F, 6.0F, 1.0F, 6.0F, dilation),
			ModelTransform.of(0.0F, 37.0F, 16.0F, (float) Math.PI, 0.0F, 0.0F)
		);
		ModelPartBuilder modelPartBuilder = ModelPartBuilder.create().uv(-14, 13).cuboid(0.0F, 0.0F, 0.0F, 14.0F, 0.0F, 14.0F);
		modelPartData.addChild("top", modelPartBuilder, ModelTransform.of(1.0F, 16.0F, 1.0F, 0.0F, 0.0F, 0.0F));
		modelPartData.addChild("bottom", modelPartBuilder, ModelTransform.of(1.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F));
		return TexturedModelData.of(modelData, 32, 32);
	}

	public static TexturedModelData getSidesTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartBuilder modelPartBuilder = ModelPartBuilder.create().uv(1, 0).cuboid(0.0F, 0.0F, 0.0F, 14.0F, 16.0F, 0.0F, EnumSet.of(Direction.NORTH));
		modelPartData.addChild("back", modelPartBuilder, ModelTransform.of(15.0F, 16.0F, 1.0F, 0.0F, 0.0F, (float) Math.PI));
		modelPartData.addChild("left", modelPartBuilder, ModelTransform.of(1.0F, 16.0F, 1.0F, 0.0F, (float) (-Math.PI / 2), (float) Math.PI));
		modelPartData.addChild("right", modelPartBuilder, ModelTransform.of(15.0F, 16.0F, 15.0F, 0.0F, (float) (Math.PI / 2), (float) Math.PI));
		modelPartData.addChild("front", modelPartBuilder, ModelTransform.of(1.0F, 16.0F, 15.0F, (float) Math.PI, 0.0F, 0.0F));
		return TexturedModelData.of(modelData, 16, 16);
	}

	@Nullable
	private static SpriteIdentifier getTextureIdFromSherd(Optional<Item> optional) {
		if (optional.isPresent()) {
			SpriteIdentifier spriteIdentifier = TexturedRenderLayers.getDecoratedPotPatternTextureId(DecoratedPotPatterns.fromSherd((Item)optional.get()));
			if (spriteIdentifier != null) {
				return spriteIdentifier;
			}
		}

		return TexturedRenderLayers.getDecoratedPotPatternTextureId(DecoratedPotPatterns.fromSherd(Items.BRICK));
	}

	public void render(
		DecoratedPotBlockEntity decoratedPotBlockEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j
	) {
		matrixStack.push();
		Direction direction = decoratedPotBlockEntity.getHorizontalFacing();
		matrixStack.translate(0.5, 0.0, 0.5);
		matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0F - direction.asRotation()));
		matrixStack.translate(-0.5, 0.0, -0.5);
		DecoratedPotBlockEntity.WobbleType wobbleType = decoratedPotBlockEntity.lastWobbleType;
		if (wobbleType != null && decoratedPotBlockEntity.getWorld() != null) {
			float g = ((float)(decoratedPotBlockEntity.getWorld().getTime() - decoratedPotBlockEntity.lastWobbleTime) + f) / (float)wobbleType.lengthInTicks;
			if (g >= 0.0F && g <= 1.0F) {
				if (wobbleType == DecoratedPotBlockEntity.WobbleType.POSITIVE) {
					float h = 0.015625F;
					float k = g * (float) (Math.PI * 2);
					float l = -1.5F * (MathHelper.cos(k) + 0.5F) * MathHelper.sin(k / 2.0F);
					matrixStack.multiply(RotationAxis.POSITIVE_X.rotation(l * 0.015625F), 0.5F, 0.0F, 0.5F);
					float m = MathHelper.sin(k);
					matrixStack.multiply(RotationAxis.POSITIVE_Z.rotation(m * 0.015625F), 0.5F, 0.0F, 0.5F);
				} else {
					float h = MathHelper.sin(-g * 3.0F * (float) Math.PI) * 0.125F;
					float k = 1.0F - g;
					matrixStack.multiply(RotationAxis.POSITIVE_Y.rotation(h * k), 0.5F, 0.0F, 0.5F);
				}
			}
		}

		VertexConsumer vertexConsumer = this.baseTexture.getVertexConsumer(vertexConsumerProvider, RenderLayer::getEntitySolid);
		this.neck.render(matrixStack, vertexConsumer, i, j);
		this.top.render(matrixStack, vertexConsumer, i, j);
		this.bottom.render(matrixStack, vertexConsumer, i, j);
		Sherds sherds = decoratedPotBlockEntity.getSherds();
		this.renderDecoratedSide(this.front, matrixStack, vertexConsumerProvider, i, j, getTextureIdFromSherd(sherds.front()));
		this.renderDecoratedSide(this.back, matrixStack, vertexConsumerProvider, i, j, getTextureIdFromSherd(sherds.back()));
		this.renderDecoratedSide(this.left, matrixStack, vertexConsumerProvider, i, j, getTextureIdFromSherd(sherds.left()));
		this.renderDecoratedSide(this.right, matrixStack, vertexConsumerProvider, i, j, getTextureIdFromSherd(sherds.right()));
		matrixStack.pop();
	}

	private void renderDecoratedSide(
		ModelPart part, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, @Nullable SpriteIdentifier textureId
	) {
		if (textureId == null) {
			textureId = getTextureIdFromSherd(Optional.empty());
		}

		if (textureId != null) {
			part.render(matrices, textureId.getVertexConsumer(vertexConsumers, RenderLayer::getEntitySolid), light, overlay);
		}
	}
}
