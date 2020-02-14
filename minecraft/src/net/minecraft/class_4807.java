package net.minecraft;

import com.google.common.collect.ImmutableMap;
import java.util.List;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.EntityPosWrapper;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;

public class class_4807<E extends MobEntity> extends Task<E> {
	private final int field_22285;
	private final float field_22286;

	public class_4807(int i, float f) {
		super(
			ImmutableMap.of(
				MemoryModuleType.WALK_TARGET,
				MemoryModuleState.VALUE_ABSENT,
				MemoryModuleType.LOOK_TARGET,
				MemoryModuleState.REGISTERED,
				MemoryModuleType.ATTACK_TARGET,
				MemoryModuleState.VALUE_PRESENT,
				MemoryModuleType.VISIBLE_MOBS,
				MemoryModuleState.VALUE_PRESENT
			)
		);
		this.field_22285 = i;
		this.field_22286 = f;
	}

	protected boolean shouldRun(ServerWorld serverWorld, E mobEntity) {
		return this.method_24551(mobEntity) && this.method_24554(mobEntity);
	}

	protected void run(ServerWorld serverWorld, E mobEntity, long l) {
		mobEntity.getMoveControl().strafeTo(-this.field_22286, 0.0F);
		mobEntity.getBrain().putMemory(MemoryModuleType.LOOK_TARGET, new EntityPosWrapper(this.method_24555(mobEntity)));
	}

	private boolean method_24551(E mobEntity) {
		return ((List)mobEntity.getBrain().getOptionalMemory(MemoryModuleType.VISIBLE_MOBS).get()).contains(this.method_24555(mobEntity));
	}

	private boolean method_24554(E mobEntity) {
		return this.method_24555(mobEntity).method_24516(mobEntity, (double)this.field_22285);
	}

	private LivingEntity method_24555(E mobEntity) {
		return (LivingEntity)mobEntity.getBrain().getOptionalMemory(MemoryModuleType.ATTACK_TARGET).get();
	}
}
