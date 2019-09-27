package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.GlobalPos;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.PointOfInterestStorage;
import net.minecraft.village.PointOfInterestType;

public class HideInHomeTask extends Task<LivingEntity> {
	private final float walkSpeed;
	private final int maxDistance;
	private final int preferredDistance;
	private Optional<BlockPos> homePosition = Optional.empty();

	public HideInHomeTask(int i, float f, int j) {
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
		this.maxDistance = i;
		this.walkSpeed = f;
		this.preferredDistance = j;
	}

	@Override
	protected boolean shouldRun(ServerWorld serverWorld, LivingEntity livingEntity) {
		Optional<BlockPos> optional = serverWorld.getPointOfInterestStorage()
			.getPosition(
				pointOfInterestType -> pointOfInterestType == PointOfInterestType.HOME,
				blockPos -> true,
				new BlockPos(livingEntity),
				this.preferredDistance + 1,
				PointOfInterestStorage.OccupationStatus.ANY
			);
		if (optional.isPresent() && ((BlockPos)optional.get()).isWithinDistance(livingEntity.getPos(), (double)this.preferredDistance)) {
			this.homePosition = optional;
		} else {
			this.homePosition = Optional.empty();
		}

		return true;
	}

	@Override
	protected void run(ServerWorld serverWorld, LivingEntity livingEntity, long l) {
		Brain<?> brain = livingEntity.getBrain();
		Optional<BlockPos> optional = this.homePosition;
		if (!optional.isPresent()) {
			optional = serverWorld.getPointOfInterestStorage()
				.getPosition(
					pointOfInterestType -> pointOfInterestType == PointOfInterestType.HOME,
					blockPos -> true,
					PointOfInterestStorage.OccupationStatus.ANY,
					new BlockPos(livingEntity),
					this.maxDistance,
					livingEntity.getRandom()
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
			brain.putMemory(MemoryModuleType.HIDING_PLACE, GlobalPos.create(serverWorld.getDimension().getType(), (BlockPos)optional.get()));
			if (!((BlockPos)optional.get()).isWithinDistance(livingEntity.getPos(), (double)this.preferredDistance)) {
				brain.putMemory(MemoryModuleType.WALK_TARGET, new WalkTarget((BlockPos)optional.get(), this.walkSpeed, this.preferredDistance));
			}
		}
	}
}
