package net.minecraft;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

public class class_4116 extends class_4097<MobEntityWithAi> {
	private final class_4140<class_4208> field_18372;
	private long field_18373;
	private final int field_18374;

	public class_4116(class_4140<class_4208> arg, int i) {
		this.field_18372 = arg;
		this.field_18374 = i;
	}

	protected boolean method_18993(ServerWorld serverWorld, MobEntityWithAi mobEntityWithAi) {
		Optional<class_4208> optional = mobEntityWithAi.method_18868().method_18904(this.field_18372);
		return optional.isPresent()
			&& Objects.equals(serverWorld.method_8597().method_12460(), ((class_4208)optional.get()).method_19442())
			&& mobEntityWithAi.method_5831(((class_4208)optional.get()).method_19446()) <= (double)this.field_18374;
	}

	@Override
	protected Set<Pair<class_4140<?>, class_4141>> method_18914() {
		return ImmutableSet.of(Pair.of(class_4140.field_18445, class_4141.field_18458), Pair.of(this.field_18372, class_4141.field_18456));
	}

	protected void method_18994(ServerWorld serverWorld, MobEntityWithAi mobEntityWithAi, long l) {
		if (l > this.field_18373) {
			Optional<Vec3d> optional = Optional.ofNullable(class_1414.method_6378(mobEntityWithAi, 8, 6));
			mobEntityWithAi.method_18868().method_18879(class_4140.field_18445, optional.map(vec3d -> new class_4142(vec3d, 0.3F, 1)));
			this.field_18373 = l + 80L;
		}
	}
}
