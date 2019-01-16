package net.minecraft.block;

import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

public class FlowerBlock extends PlantBlock {
	protected static final VoxelShape field_11085 = Block.createCubeShape(5.0, 0.0, 5.0, 11.0, 10.0, 11.0);
	private final StatusEffect field_11087;
	private final int field_11086;

	public FlowerBlock(StatusEffect statusEffect, int i, Block.Settings settings) {
		super(settings);
		this.field_11087 = statusEffect;
		this.field_11086 = i * 20;
	}

	@Override
	public VoxelShape getOutlineShape(BlockState blockState, BlockView blockView, BlockPos blockPos, VerticalEntityPosition verticalEntityPosition) {
		Vec3d vec3d = blockState.getOffsetPos(blockView, blockPos);
		return field_11085.method_1096(vec3d.x, vec3d.y, vec3d.z);
	}

	@Override
	public Block.OffsetType getOffsetType() {
		return Block.OffsetType.XZ;
	}

	public StatusEffect method_10188() {
		return this.field_11087;
	}

	public int method_10187() {
		return this.field_11086;
	}
}
