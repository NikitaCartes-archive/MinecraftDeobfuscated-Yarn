package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import java.util.Optional;
import java.util.Set;
import net.minecraft.entity.ai.brain.LookTarget;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

public class LookAroundTask extends Task<MobEntity> {
	public LookAroundTask(int i, int j) {
		super(i, j);
	}

	@Override
	protected Set<Pair<MemoryModuleType<?>, MemoryModuleState>> getRequiredMemoryState() {
		return ImmutableSet.of(Pair.of(MemoryModuleType.field_18446, MemoryModuleState.field_18456));
	}

	protected boolean method_18967(ServerWorld serverWorld, MobEntity mobEntity, long l) {
		Optional<LookTarget> optional = mobEntity.getBrain().getMemory(MemoryModuleType.field_18446);
		return optional.isPresent() && ((LookTarget)optional.get()).method_18990(mobEntity);
	}

	protected void method_18968(ServerWorld serverWorld, MobEntity mobEntity, long l) {
		mobEntity.getBrain().forget(MemoryModuleType.field_18446);
	}

	protected void method_18969(ServerWorld serverWorld, MobEntity mobEntity, long l) {
		mobEntity.getBrain().getMemory(MemoryModuleType.field_18446).ifPresent(lookTarget -> {
			Vec3d vec3d = lookTarget.getPos();
			mobEntity.getLookControl().lookAt(vec3d.x, vec3d.y, vec3d.z, (float)mobEntity.method_5986(), (float)mobEntity.method_5978());
		});
	}
}
