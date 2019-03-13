package net.minecraft.block;

import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

public class FlowerBlock extends PlantBlock {
	protected static final VoxelShape field_11085 = Block.method_9541(5.0, 0.0, 5.0, 11.0, 10.0, 11.0);
	private final StatusEffect effectInStew;
	private final int effectInStewDuration;

	public FlowerBlock(StatusEffect statusEffect, int i, Block.Settings settings) {
		super(settings);
		this.effectInStew = statusEffect;
		this.effectInStewDuration = i * 20;
	}

	@Override
	public VoxelShape method_9530(BlockState blockState, BlockView blockView, BlockPos blockPos, VerticalEntityPosition verticalEntityPosition) {
		Vec3d vec3d = blockState.method_11599(blockView, blockPos);
		return field_11085.offset(vec3d.x, vec3d.y, vec3d.z);
	}

	@Override
	public Block.OffsetType getOffsetType() {
		return Block.OffsetType.XZ;
	}

	public StatusEffect getEffectInStew() {
		return this.effectInStew;
	}

	public int getEffectInStewDuration() {
		return this.effectInStewDuration;
	}
}
