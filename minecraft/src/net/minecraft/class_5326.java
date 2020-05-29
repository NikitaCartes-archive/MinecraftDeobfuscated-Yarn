package net.minecraft;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.LookTargetUtil;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.dynamic.GlobalPos;
import net.minecraft.village.VillagerProfession;
import net.minecraft.world.poi.PointOfInterestType;

public class class_5326 extends Task<VillagerEntity> {
	final VillagerProfession field_25156;

	public class_5326(VillagerProfession villagerProfession) {
		super(ImmutableMap.of(MemoryModuleType.JOB_SITE, MemoryModuleState.VALUE_PRESENT, MemoryModuleType.MOBS, MemoryModuleState.VALUE_PRESENT));
		this.field_25156 = villagerProfession;
	}

	protected void run(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
		GlobalPos globalPos = (GlobalPos)villagerEntity.getBrain().getOptionalMemory(MemoryModuleType.JOB_SITE).get();
		serverWorld.getPointOfInterestStorage()
			.getType(globalPos.getPos())
			.ifPresent(
				pointOfInterestType -> LookTargetUtil.method_29248(villagerEntity, villagerEntityxx -> this.method_29257(globalPos, pointOfInterestType, villagerEntityxx))
						.reduce(villagerEntity, class_5326::method_29255)
			);
	}

	private static VillagerEntity method_29255(VillagerEntity villagerEntity, VillagerEntity villagerEntity2) {
		VillagerEntity villagerEntity3;
		VillagerEntity villagerEntity4;
		if (villagerEntity.getExperience() > villagerEntity2.getExperience()) {
			villagerEntity3 = villagerEntity;
			villagerEntity4 = villagerEntity2;
		} else {
			villagerEntity3 = villagerEntity2;
			villagerEntity4 = villagerEntity;
		}

		villagerEntity4.getBrain().forget(MemoryModuleType.JOB_SITE);
		return villagerEntity3;
	}

	private boolean method_29257(GlobalPos globalPos, PointOfInterestType pointOfInterestType, VillagerEntity villagerEntity) {
		return this.method_29254(villagerEntity)
			&& globalPos.equals(villagerEntity.getBrain().getOptionalMemory(MemoryModuleType.JOB_SITE).get())
			&& this.method_29253(pointOfInterestType, villagerEntity.getVillagerData().getProfession());
	}

	private boolean method_29253(PointOfInterestType pointOfInterestType, VillagerProfession villagerProfession) {
		return villagerProfession.getWorkStation().getCompletionCondition().test(pointOfInterestType);
	}

	private boolean method_29254(VillagerEntity villagerEntity) {
		return villagerEntity.getBrain().getOptionalMemory(MemoryModuleType.JOB_SITE).isPresent();
	}
}
