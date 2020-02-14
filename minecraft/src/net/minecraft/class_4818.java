package net.minecraft;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import net.minecraft.entity.ai.TargetFinder;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

public class class_4818 extends Task<MobEntityWithAi> {
	private final float field_22310;
	private final int field_22311;
	private final int field_22312;

	public class_4818(float f) {
		this(f, 10, 7);
	}

	public class_4818(float f, int i, int j) {
		super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryModuleState.VALUE_ABSENT));
		this.field_22310 = f;
		this.field_22311 = i;
		this.field_22312 = j;
	}

	protected void run(ServerWorld serverWorld, MobEntityWithAi mobEntityWithAi, long l) {
		Optional<Vec3d> optional = Optional.ofNullable(TargetFinder.findGroundTarget(mobEntityWithAi, this.field_22311, this.field_22312));
		mobEntityWithAi.getBrain().setMemory(MemoryModuleType.WALK_TARGET, optional.map(vec3d -> new WalkTarget(vec3d, this.field_22310, 0)));
	}
}
