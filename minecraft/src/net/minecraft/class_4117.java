package net.minecraft;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import java.util.Optional;
import java.util.Set;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

public class class_4117 extends class_4097<MobEntityWithAi> {
	private final float field_18375;

	public class_4117(float f) {
		this.field_18375 = f;
	}

	@Override
	protected Set<Pair<class_4140<?>, class_4141>> method_18914() {
		return ImmutableSet.of(Pair.of(class_4140.field_18445, class_4141.field_18457));
	}

	protected void method_18996(ServerWorld serverWorld, MobEntityWithAi mobEntityWithAi, long l) {
		Optional<Vec3d> optional = Optional.ofNullable(class_1414.method_6378(mobEntityWithAi, 10, 7));
		mobEntityWithAi.method_18868().method_18879(class_4140.field_18445, optional.map(vec3d -> new class_4142(vec3d, this.field_18375, 0)));
	}
}
