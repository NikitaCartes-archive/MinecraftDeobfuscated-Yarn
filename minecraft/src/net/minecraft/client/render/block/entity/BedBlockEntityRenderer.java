package net.minecraft.client.render.block.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BedBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.DoubleBlockProperties;
import net.minecraft.block.entity.BedBlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.enums.BedPart;
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
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3f;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class BedBlockEntityRenderer implements BlockEntityRenderer<BedBlockEntity> {
	private final ModelPart field_27744;
	private final ModelPart field_27745;

	public BedBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
		this.field_27744 = context.getLayerModelPart(EntityModelLayers.BED_HEAD);
		this.field_27745 = context.getLayerModelPart(EntityModelLayers.BED_FOOT);
	}

	public static TexturedModelData getHeadTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		modelPartData.addChild("main", ModelPartBuilder.create().uv(0, 0).cuboid(0.0F, 0.0F, 0.0F, 16.0F, 16.0F, 6.0F), ModelTransform.NONE);
		modelPartData.addChild(
			"left_leg",
			ModelPartBuilder.create().uv(50, 6).cuboid(0.0F, 6.0F, 0.0F, 3.0F, 3.0F, 3.0F),
			ModelTransform.rotation((float) (Math.PI / 2), 0.0F, (float) (Math.PI / 2))
		);
		modelPartData.addChild(
			"right_leg",
			ModelPartBuilder.create().uv(50, 18).cuboid(-16.0F, 6.0F, 0.0F, 3.0F, 3.0F, 3.0F),
			ModelTransform.rotation((float) (Math.PI / 2), 0.0F, (float) Math.PI)
		);
		return TexturedModelData.of(modelData, 64, 64);
	}

	public static TexturedModelData getFootTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		modelPartData.addChild("main", ModelPartBuilder.create().uv(0, 22).cuboid(0.0F, 0.0F, 0.0F, 16.0F, 16.0F, 6.0F), ModelTransform.NONE);
		modelPartData.addChild(
			"left_leg", ModelPartBuilder.create().uv(50, 0).cuboid(0.0F, 6.0F, -16.0F, 3.0F, 3.0F, 3.0F), ModelTransform.rotation((float) (Math.PI / 2), 0.0F, 0.0F)
		);
		modelPartData.addChild(
			"right_leg",
			ModelPartBuilder.create().uv(50, 12).cuboid(-16.0F, 6.0F, -16.0F, 3.0F, 3.0F, 3.0F),
			ModelTransform.rotation((float) (Math.PI / 2), 0.0F, (float) (Math.PI * 3.0 / 2.0))
		);
		return TexturedModelData.of(modelData, 64, 64);
	}

	public void render(BedBlockEntity bedBlockEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j) {
		SpriteIdentifier spriteIdentifier = TexturedRenderLayers.BED_TEXTURES[bedBlockEntity.getColor().getId()];
		World world = bedBlockEntity.getWorld();
		if (world != null) {
			BlockState blockState = bedBlockEntity.getCachedState();
			DoubleBlockProperties.PropertySource<? extends BedBlockEntity> propertySource = DoubleBlockProperties.toPropertySource(
				BlockEntityType.BED,
				BedBlock::getBedPart,
				BedBlock::getOppositePartDirection,
				ChestBlock.FACING,
				blockState,
				world,
				bedBlockEntity.getPos(),
				(worldAccess, blockPos) -> false
			);
			int k = propertySource.apply(new LightmapCoordinatesRetriever<>()).get(i);
			this.method_3558(
				matrixStack,
				vertexConsumerProvider,
				blockState.get(BedBlock.PART) == BedPart.HEAD ? this.field_27744 : this.field_27745,
				blockState.get(BedBlock.FACING),
				spriteIdentifier,
				k,
				j,
				false
			);
		} else {
			this.method_3558(matrixStack, vertexConsumerProvider, this.field_27744, Direction.SOUTH, spriteIdentifier, i, j, false);
			this.method_3558(matrixStack, vertexConsumerProvider, this.field_27745, Direction.SOUTH, spriteIdentifier, i, j, true);
		}
	}

	private void method_3558(
		MatrixStack matrix,
		VertexConsumerProvider vertexConsumerProvider,
		ModelPart modelPart,
		Direction direction,
		SpriteIdentifier spriteIdentifier,
		int light,
		int overlay,
		boolean bl
	) {
		matrix.push();
		matrix.translate(0.0, 0.5625, bl ? -1.0 : 0.0);
		matrix.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(90.0F));
		matrix.translate(0.5, 0.5, 0.5);
		matrix.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(180.0F + direction.asRotation()));
		matrix.translate(-0.5, -0.5, -0.5);
		VertexConsumer vertexConsumer = spriteIdentifier.getVertexConsumer(vertexConsumerProvider, RenderLayer::getEntitySolid);
		modelPart.render(matrix, vertexConsumer, light, overlay);
		matrix.pop();
	}
}
