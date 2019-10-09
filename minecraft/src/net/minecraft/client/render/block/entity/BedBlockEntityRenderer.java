package net.minecraft.client.render.block.entity;

import java.util.Arrays;
import java.util.Comparator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BedBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BedBlockEntity;
import net.minecraft.block.enums.BedPart;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.LayeredVertexConsumerStorage;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MatrixStack;

@Environment(EnvType.CLIENT)
public class BedBlockEntityRenderer extends BlockEntityRenderer<BedBlockEntity> {
	public static final Identifier[] TEXTURES = (Identifier[])Arrays.stream(DyeColor.values())
		.sorted(Comparator.comparingInt(DyeColor::getId))
		.map(dyeColor -> new Identifier("entity/bed/" + dyeColor.getName()))
		.toArray(Identifier[]::new);
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

	public void method_3557(
		BedBlockEntity bedBlockEntity,
		double d,
		double e,
		double f,
		float g,
		MatrixStack matrixStack,
		LayeredVertexConsumerStorage layeredVertexConsumerStorage,
		int i,
		int j
	) {
		Identifier identifier = TEXTURES[bedBlockEntity.getColor().getId()];
		VertexConsumer vertexConsumer = layeredVertexConsumerStorage.getBuffer(RenderLayer.getEntitySolid(SpriteAtlasTexture.BLOCK_ATLAS_TEX));
		if (bedBlockEntity.hasWorld()) {
			BlockState blockState = bedBlockEntity.getCachedState();
			this.method_3558(matrixStack, vertexConsumer, blockState.get(BedBlock.PART) == BedPart.HEAD, blockState.get(BedBlock.FACING), identifier, i, j, false);
		} else {
			this.method_3558(matrixStack, vertexConsumer, true, Direction.SOUTH, identifier, i, j, false);
			this.method_3558(matrixStack, vertexConsumer, false, Direction.SOUTH, identifier, i, j, true);
		}
	}

	private void method_3558(
		MatrixStack matrixStack, VertexConsumer vertexConsumer, boolean bl, Direction direction, Identifier identifier, int i, int j, boolean bl2
	) {
		this.field_20813.visible = bl;
		this.field_20814.visible = !bl;
		this.field_20815[0].visible = !bl;
		this.field_20815[1].visible = bl;
		this.field_20815[2].visible = !bl;
		this.field_20815[3].visible = bl;
		matrixStack.push();
		matrixStack.translate(0.0, 0.5625, bl2 ? -1.0 : 0.0);
		matrixStack.multiply(Vector3f.POSITIVE_X.getRotationQuaternion(90.0F));
		matrixStack.translate(0.5, 0.5, 0.5);
		matrixStack.multiply(Vector3f.POSITIVE_Z.getRotationQuaternion(180.0F + direction.asRotation()));
		matrixStack.translate(-0.5, -0.5, -0.5);
		Sprite sprite = this.getSprite(identifier);
		this.field_20813.render(matrixStack, vertexConsumer, 0.0625F, i, j, sprite);
		this.field_20814.render(matrixStack, vertexConsumer, 0.0625F, i, j, sprite);
		this.field_20815[0].render(matrixStack, vertexConsumer, 0.0625F, i, j, sprite);
		this.field_20815[1].render(matrixStack, vertexConsumer, 0.0625F, i, j, sprite);
		this.field_20815[2].render(matrixStack, vertexConsumer, 0.0625F, i, j, sprite);
		this.field_20815[3].render(matrixStack, vertexConsumer, 0.0625F, i, j, sprite);
		matrixStack.pop();
	}
}
