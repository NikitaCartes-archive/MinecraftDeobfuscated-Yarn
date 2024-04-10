package net.minecraft.entity.effect;

import com.google.common.collect.Sets;
import java.util.Set;
import java.util.function.ToIntFunction;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;

class WeavingStatusEffect extends StatusEffect {
	private final ToIntFunction<Random> cobwebChanceFunction;

	protected WeavingStatusEffect(StatusEffectCategory category, int color, ToIntFunction<Random> cobwebChanceFunction) {
		super(category, color, ParticleTypes.ITEM_COBWEB);
		this.cobwebChanceFunction = cobwebChanceFunction;
	}

	@Override
	public void onEntityRemoval(LivingEntity entity, int amplifier, Entity.RemovalReason reason) {
		if (reason == Entity.RemovalReason.KILLED && (entity instanceof PlayerEntity || entity.getWorld().getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING))) {
			this.tryPlaceCobweb(entity.getWorld(), entity.getRandom(), entity.getSteppingPos());
		}
	}

	private void tryPlaceCobweb(World world, Random random, BlockPos pos) {
		Set<BlockPos> set = Sets.<BlockPos>newHashSet();
		int i = this.cobwebChanceFunction.applyAsInt(random);

		for (BlockPos blockPos : BlockPos.iterateRandomly(random, 15, pos, 1)) {
			BlockPos blockPos2 = blockPos.down();
			if (!set.contains(blockPos)
				&& world.getBlockState(blockPos).isReplaceable()
				&& world.getBlockState(blockPos2).isSideSolidFullSquare(world, blockPos2, Direction.UP)) {
				set.add(blockPos.toImmutable());
				if (set.size() >= i) {
					break;
				}
			}
		}

		for (BlockPos blockPosx : set) {
			world.setBlockState(blockPosx, Blocks.COBWEB.getDefaultState(), Block.NOTIFY_ALL);
			world.syncWorldEvent(WorldEvents.COBWEB_WEAVED, blockPosx, 0);
		}
	}
}
