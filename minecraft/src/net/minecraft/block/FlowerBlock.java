package net.minecraft.block;

import java.util.List;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

public class FlowerBlock extends PlantBlock implements SuspiciousStewIngredient {
	protected static final float field_31094 = 3.0F;
	protected static final VoxelShape SHAPE = Block.createCuboidShape(5.0, 0.0, 5.0, 11.0, 10.0, 11.0);
	private final List<SuspiciousStewIngredient.StewEffect> stewEffects;

	public FlowerBlock(StatusEffect suspiciousStewEffect, int effectDuration, AbstractBlock.Settings settings) {
		super(settings);
		int i;
		if (suspiciousStewEffect.isInstant()) {
			i = effectDuration;
		} else {
			i = effectDuration * 20;
		}

		this.stewEffects = List.of(new SuspiciousStewIngredient.StewEffect(suspiciousStewEffect, i));
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		Vec3d vec3d = state.getModelOffset(world, pos);
		return SHAPE.offset(vec3d.x, vec3d.y, vec3d.z);
	}

	@Override
	public List<SuspiciousStewIngredient.StewEffect> getStewEffects() {
		return this.stewEffects;
	}
}
