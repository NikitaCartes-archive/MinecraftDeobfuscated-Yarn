package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class CobwebBlock extends Block {
	public static final MapCodec<CobwebBlock> CODEC = createCodec(CobwebBlock::new);

	@Override
	public MapCodec<CobwebBlock> getCodec() {
		return CODEC;
	}

	public CobwebBlock(AbstractBlock.Settings settings) {
		super(settings);
	}

	@Override
	protected void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
		Vec3d vec3d = new Vec3d(0.25, 0.05F, 0.25);
		if (entity instanceof LivingEntity livingEntity && livingEntity.hasStatusEffect(StatusEffects.WEAVING)) {
			vec3d = new Vec3d(0.5, 0.25, 0.5);
		}

		entity.slowMovement(state, vec3d);
	}
}
