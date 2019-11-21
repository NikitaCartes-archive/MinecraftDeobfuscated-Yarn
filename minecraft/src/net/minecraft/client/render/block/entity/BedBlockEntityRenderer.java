package net.minecraft.client.render.block.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4722;
import net.minecraft.class_4730;
import net.minecraft.block.BedBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BedBlockEntity;
import net.minecraft.block.enums.BedPart;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.math.Direction;

@Environment(EnvType.CLIENT)
public class BedBlockEntityRenderer extends BlockEntityRenderer<BedBlockEntity> {
	private final ModelPart field_20813;
	private final ModelPart field_20814;
	private final ModelPart[] field_20815 = new ModelPart[4];

	public BedBlockEntityRenderer(BlockEntityRenderDispatcher blockEntityRenderDispatcher) {
		super(blockEntityRenderDispatcher);
		this.field_20813 = new ModelPart(64, 64, 0, 0);
		this.field_20813.addCuboid(0.0F, 0.0F, 0.0F, 16.0F, 16.0F, 6.0F, 0.0F);
		this.field_20814 = new ModelPart(64, 64, 0, 22);
		this.field_20814.addCuboid(0.0F, 0.0F, 0.0F, 16.0F, 16.0F, 6.0F, 0.0F);
		this.field_20815[0] = new ModelPart(64, 64, 50, 0);
		this.field_20815[1] = new ModelPart(64, 64, 50, 6);
		this.field_20815[2] = new ModelPart(64, 64, 50, 12);
		this.field_20815[3] = new ModelPart(64, 64, 50, 18);
		this.field_20815[0].addCuboid(0.0F, 6.0F, -16.0F, 3.0F, 3.0F, 3.0F);
		this.field_20815[1].addCuboid(0.0F, 6.0F, 0.0F, 3.0F, 3.0F, 3.0F);
		this.field_20815[2].addCuboid(-16.0F, 6.0F, -16.0F, 3.0F, 3.0F, 3.0F);
		this.field_20815[3].addCuboid(-16.0F, 6.0F, 0.0F, 3.0F, 3.0F, 3.0F);
		this.field_20815[0].pitch = (float) (Math.PI / 2);
		this.field_20815[1].pitch = (float) (Math.PI / 2);
		this.field_20815[2].pitch = (float) (Math.PI / 2);
		this.field_20815[3].pitch = (float) (Math.PI / 2);
		this.field_20815[0].roll = 0.0F;
		this.field_20815[1].roll = (float) (Math.PI / 2);
		this.field_20815[2].roll = (float) (Math.PI * 3.0 / 2.0);
		this.field_20815[3].roll = (float) Math.PI;
	}

	public void render(BedBlockEntity bedBlockEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j) {
		class_4730 lv = class_4722.field_21713[bedBlockEntity.getColor().getId()];
		if (bedBlockEntity.hasWorld()) {
			BlockState blockState = bedBlockEntity.getCachedState();
			this.method_3558(matrixStack, vertexConsumerProvider, blockState.get(BedBlock.PART) == BedPart.HEAD, blockState.get(BedBlock.FACING), lv, i, j, false);
		} else {
			this.method_3558(matrixStack, vertexConsumerProvider, true, Direction.SOUTH, lv, i, j, false);
			this.method_3558(matrixStack, vertexConsumerProvider, false, Direction.SOUTH, lv, i, j, true);
		}
	}

	private void method_3558(
		MatrixStack matrix, VertexConsumerProvider vertexConsumerProvider, boolean bl, Direction direction, class_4730 arg, int light, int overlay, boolean bl2
	) {
		this.field_20813.visible = bl;
		this.field_20814.visible = !bl;
		this.field_20815[0].visible = !bl;
		this.field_20815[1].visible = bl;
		this.field_20815[2].visible = !bl;
		this.field_20815[3].visible = bl;
		matrix.push();
		matrix.translate(0.0, 0.5625, bl2 ? -1.0 : 0.0);
		matrix.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(90.0F));
		matrix.translate(0.5, 0.5, 0.5);
		matrix.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(180.0F + direction.asRotation()));
		matrix.translate(-0.5, -0.5, -0.5);
		VertexConsumer vertexConsumer = arg.method_24145(vertexConsumerProvider, RenderLayer::getEntitySolid);
		this.field_20813.render(matrix, vertexConsumer, light, overlay);
		this.field_20814.render(matrix, vertexConsumer, light, overlay);
		this.field_20815[0].render(matrix, vertexConsumer, light, overlay);
		this.field_20815[1].render(matrix, vertexConsumer, light, overlay);
		this.field_20815[2].render(matrix, vertexConsumer, light, overlay);
		this.field_20815[3].render(matrix, vertexConsumer, light, overlay);
		matrix.pop();
	}
}
