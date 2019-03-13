package net.minecraft;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import java.util.Optional;
import java.util.Set;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

public class class_4110 extends class_4097<MobEntity> {
	public class_4110(int i, int j) {
		super(i, j);
	}

	@Override
	protected Set<Pair<class_4140<?>, class_4141>> method_18914() {
		return ImmutableSet.of(Pair.of(class_4140.field_18446, class_4141.field_18456));
	}

	protected boolean method_18967(ServerWorld serverWorld, MobEntity mobEntity, long l) {
		Optional<class_4115> optional = mobEntity.method_18868().method_18904(class_4140.field_18446);
		return optional.isPresent() && ((class_4115)optional.get()).method_18990(mobEntity);
	}

	protected void method_18968(ServerWorld serverWorld, MobEntity mobEntity, long l) {
		mobEntity.method_18868().method_18875(class_4140.field_18446);
	}

	protected void method_18969(ServerWorld serverWorld, MobEntity mobEntity, long l) {
		mobEntity.method_18868().method_18904(class_4140.field_18446).ifPresent(arg -> {
			Vec3d vec3d = arg.method_18991();
			mobEntity.method_5988().lookAt(vec3d.x, vec3d.y, vec3d.z, (float)mobEntity.method_5986(), (float)mobEntity.method_5978());
		});
	}
}
