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
				MemoryModuleType.field_18445,
				MemoryModuleState.field_18457,
				MemoryModuleType.field_18438,
				MemoryModuleState.field_18458,
				MemoryModuleType.field_19008,
				MemoryModuleState.field_18458
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
				pointOfInterestType -> pointOfInterestType == PointOfInterestType.field_18517,
				blockPos -> true,
				new BlockPos(livingEntity),
				this.preferredDistance + 1,
				PointOfInterestStorage.OccupationStatus.field_18489
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
					pointOfInterestType -> pointOfInterestType == PointOfInterestType.field_18517,
					blockPos -> true,
					PointOfInterestStorage.OccupationStatus.field_18489,
					new BlockPos(livingEntity),
					this.maxDistance,
					livingEntity.getRand()
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
			brain.putMemory(MemoryModuleType.field_19008, GlobalPos.create(serverWorld.getDimension().getType(), (BlockPos)optional.get()));
			if (!((BlockPos)optional.get()).isWithinDistance(livingEntity.getPos(), (double)this.preferredDistance)) {
				brain.putMemory(MemoryModuleType.field_18445, new WalkTarget((BlockPos)optional.get(), this.walkSpeed, this.preferredDistance));
			}
		}
	}
}
