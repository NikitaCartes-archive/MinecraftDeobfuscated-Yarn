package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.dynamic.GlobalPos;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.poi.PointOfInterestStorage;
import net.minecraft.world.poi.PointOfInterestType;

public class HideInHomeTask extends Task<LivingEntity> {
	private final float walkSpeed;
	private final int maxDistance;
	private final int preferredDistance;
	private Optional<BlockPos> homePosition = Optional.empty();

	public HideInHomeTask(int maxDistance, float walkSpeed, int preferredDistance) {
		super(
			ImmutableMap.of(
				MemoryModuleType.WALK_TARGET,
				MemoryModuleState.VALUE_ABSENT,
				MemoryModuleType.HOME,
				MemoryModuleState.REGISTERED,
				MemoryModuleType.HIDING_PLACE,
				MemoryModuleState.REGISTERED
			)
		);
		this.maxDistance = maxDistance;
		this.walkSpeed = walkSpeed;
		this.preferredDistance = preferredDistance;
	}

	@Override
	protected boolean shouldRun(ServerWorld world, LivingEntity entity) {
		Optional<BlockPos> optional = world.getPointOfInterestStorage()
			.getPosition(
				pointOfInterestType -> pointOfInterestType == PointOfInterestType.HOME,
				blockPos -> true,
				entity.getBlockPos(),
				this.preferredDistance + 1,
				PointOfInterestStorage.OccupationStatus.ANY
			);
		if (optional.isPresent() && ((BlockPos)optional.get()).isWithinDistance(entity.getPos(), (double)this.preferredDistance)) {
			this.homePosition = optional;
		} else {
			this.homePosition = Optional.empty();
		}

		return true;
	}

	@Override
	protected void run(ServerWorld world, LivingEntity entity, long time) {
		Brain<?> brain = entity.getBrain();
		Optional<BlockPos> optional = this.homePosition;
		if (!optional.isPresent()) {
			optional = world.getPointOfInterestStorage()
				.getPosition(
					pointOfInterestType -> pointOfInterestType == PointOfInterestType.HOME,
					blockPos -> true,
					PointOfInterestStorage.OccupationStatus.ANY,
					entity.getBlockPos(),
					this.maxDistance,
					entity.getRandom()
				);
			if (!optional.isPresent()) {
				Optional<GlobalPos> optional2 = brain.getOptionalMemory(MemoryModuleType.HOME);
				if (optional2.isPresent()) {
					optional = Optional.of(((GlobalPos)optional2.get()).getPos());
				}
			}
		}

		if (optional.isPresent()) {
			brain.forget(MemoryModuleType.PATH);
			brain.forget(MemoryModuleType.LOOK_TARGET);
			brain.forget(MemoryModuleType.BREED_TARGET);
			brain.forget(MemoryModuleType.INTERACTION_TARGET);
			brain.remember(MemoryModuleType.HIDING_PLACE, GlobalPos.create(world.getRegistryKey(), (BlockPos)optional.get()));
			if (!((BlockPos)optional.get()).isWithinDistance(entity.getPos(), (double)this.preferredDistance)) {
				brain.remember(MemoryModuleType.WALK_TARGET, new WalkTarget((BlockPos)optional.get(), this.walkSpeed, this.preferredDistance));
			}
		}
	}
}
