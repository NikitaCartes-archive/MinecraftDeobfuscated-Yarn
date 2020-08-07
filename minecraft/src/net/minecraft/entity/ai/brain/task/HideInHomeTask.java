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
				MemoryModuleType.field_18445,
				MemoryModuleState.field_18457,
				MemoryModuleType.field_18438,
				MemoryModuleState.field_18458,
				MemoryModuleType.field_19008,
				MemoryModuleState.field_18458
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
				pointOfInterestType -> pointOfInterestType == PointOfInterestType.field_18517,
				blockPos -> true,
				entity.getBlockPos(),
				this.preferredDistance + 1,
				PointOfInterestStorage.OccupationStatus.field_18489
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
					pointOfInterestType -> pointOfInterestType == PointOfInterestType.field_18517,
					blockPos -> true,
					PointOfInterestStorage.OccupationStatus.field_18489,
					entity.getBlockPos(),
					this.maxDistance,
					entity.getRandom()
				);
			if (!optional.isPresent()) {
				Optional<GlobalPos> optional2 = brain.getOptionalMemory(MemoryModuleType.field_18438);
				if (optional2.isPresent()) {
					optional = Optional.of(((GlobalPos)optional2.get()).getPos());
				}
			}
		}

		if (optional.isPresent()) {
			brain.forget(MemoryModuleType.field_18449);
			brain.forget(MemoryModuleType.field_18446);
			brain.forget(MemoryModuleType.field_18448);
			brain.forget(MemoryModuleType.field_18447);
			brain.remember(MemoryModuleType.field_19008, GlobalPos.create(world.getRegistryKey(), (BlockPos)optional.get()));
			if (!((BlockPos)optional.get()).isWithinDistance(entity.getPos(), (double)this.preferredDistance)) {
				brain.remember(MemoryModuleType.field_18445, new WalkTarget((BlockPos)optional.get(), this.walkSpeed, this.preferredDistance));
			}
		}
	}
}
