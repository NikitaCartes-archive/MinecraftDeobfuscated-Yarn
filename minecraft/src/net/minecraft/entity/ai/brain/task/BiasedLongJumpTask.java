package net.minecraft.entity.ai.brain.task;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Function;
import net.minecraft.block.Block;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.intprovider.UniformIntProvider;

public class BiasedLongJumpTask<E extends MobEntity> extends LongJumpTask<E> {
	private final TagKey<Block> favoredBlocks;
	private final float biasChance;
	private final List<LongJumpTask.Target> unfavoredTargets = new ArrayList();
	private boolean useBias;

	public BiasedLongJumpTask(
		UniformIntProvider cooldownRange,
		int verticalRange,
		int horizontalRange,
		float maxRange,
		Function<E, SoundEvent> entityToSound,
		TagKey<Block> favoredBlocks,
		float biasChance,
		BiPredicate<E, BlockPos> jumpToPredicate
	) {
		super(cooldownRange, verticalRange, horizontalRange, maxRange, entityToSound, jumpToPredicate);
		this.favoredBlocks = favoredBlocks;
		this.biasChance = biasChance;
	}

	@Override
	protected void run(ServerWorld serverWorld, E mobEntity, long l) {
		super.run(serverWorld, mobEntity, l);
		this.unfavoredTargets.clear();
		this.useBias = mobEntity.getRandom().nextFloat() < this.biasChance;
	}

	@Override
	protected Optional<LongJumpTask.Target> getTarget(ServerWorld world) {
		if (!this.useBias) {
			return super.getTarget(world);
		} else {
			BlockPos.Mutable mutable = new BlockPos.Mutable();

			while (!this.targets.isEmpty()) {
				Optional<LongJumpTask.Target> optional = super.getTarget(world);
				if (optional.isPresent()) {
					LongJumpTask.Target target = (LongJumpTask.Target)optional.get();
					if (world.getBlockState(mutable.set(target.getPos(), Direction.DOWN)).isIn(this.favoredBlocks)) {
						return optional;
					}

					this.unfavoredTargets.add(target);
				}
			}

			return !this.unfavoredTargets.isEmpty() ? Optional.of((LongJumpTask.Target)this.unfavoredTargets.remove(0)) : Optional.empty();
		}
	}
}
