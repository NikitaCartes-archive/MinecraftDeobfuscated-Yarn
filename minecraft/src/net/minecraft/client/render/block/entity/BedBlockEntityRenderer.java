package net.minecraft.client.render.block.entity;

import java.util.Arrays;
import java.util.Comparator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4576;
import net.minecraft.block.BedBlock;
import net.minecraft.block.BlockRenderLayer;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BedBlockEntity;
import net.minecraft.block.enums.BedPart;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Quaternion;

@Environment(EnvType.CLIENT)
public class BedBlockEntityRenderer extends class_4576<BedBlockEntity> {
	public static final Identifier[] TEXTURES = (Identifier[])Arrays.stream(DyeColor.values())
		.sorted(Comparator.comparingInt(DyeColor::getId))
		.map(dyeColor -> new Identifier("entity/bed/" + dyeColor.getName()))
		.toArray(Identifier[]::new);
	private final ModelPart field_20813;
	private final ModelPart field_20814;
	private final ModelPart[] field_20815 = new ModelPart[4];

	public BedBlockEntityRenderer() {
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

	protected void method_3557(
		BedBlockEntity bedBlockEntity, double d, double e, double f, float g, int i, BlockRenderLayer blockRenderLayer, BufferBuilder bufferBuilder, int j, int k
	) {
		Identifier identifier;
		if (i >= 0) {
			identifier = (Identifier)ModelLoader.field_20848.get(i);
		} else {
			identifier = TEXTURES[bedBlockEntity.getColor().getId()];
		}

		this.method_22742(bufferBuilder, identifier, bedBlockEntity, j, k);
	}

	public void method_22742(BufferBuilder bufferBuilder, Identifier identifier, BedBlockEntity bedBlockEntity, int i, int j) {
		if (bedBlockEntity.hasWorld()) {
			BlockState blockState = bedBlockEntity.getCachedState();
			this.method_3558(bufferBuilder, blockState.get(BedBlock.PART) == BedPart.HEAD, blockState.get(BedBlock.FACING), identifier, i, j, false);
		} else {
			this.method_3558(bufferBuilder, true, Direction.SOUTH, identifier, i, j, false);
			this.method_3558(bufferBuilder, false, Direction.SOUTH, identifier, i, j, true);
		}
	}

	private void method_3558(BufferBuilder bufferBuilder, boolean bl, Direction direction, Identifier identifier, int i, int j, boolean bl2) {
		this.field_20813.visible = bl;
		this.field_20814.visible = !bl;
		this.field_20815[0].visible = !bl;
		this.field_20815[1].visible = bl;
		this.field_20815[2].visible = !bl;
		this.field_20815[3].visible = bl;
		bufferBuilder.method_22629();
		bufferBuilder.method_22626(0.0, 0.5625, bl2 ? -1.0 : 0.0);
		bufferBuilder.method_22622(new Quaternion(Vector3f.field_20703, 90.0F, true));
		bufferBuilder.method_22626(0.5, 0.5, 0.5);
		bufferBuilder.method_22622(new Quaternion(Vector3f.field_20707, 180.0F + direction.asRotation(), true));
		bufferBuilder.method_22626(-0.5, -0.5, -0.5);
		Sprite sprite = this.method_22739(identifier);
		this.field_20813.method_22698(bufferBuilder, 0.0625F, i, j, sprite);
		this.field_20814.method_22698(bufferBuilder, 0.0625F, i, j, sprite);
		this.field_20815[0].method_22698(bufferBuilder, 0.0625F, i, j, sprite);
		this.field_20815[1].method_22698(bufferBuilder, 0.0625F, i, j, sprite);
		this.field_20815[2].method_22698(bufferBuilder, 0.0625F, i, j, sprite);
		this.field_20815[3].method_22698(bufferBuilder, 0.0625F, i, j, sprite);
		bufferBuilder.method_22630();
	}
}
