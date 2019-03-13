package net.minecraft;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import java.util.Set;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;

public class class_4119 extends class_4097<LivingEntity> {
	private final EntityType<?> field_18376;
	private final float field_18377;

	public class_4119(EntityType<?> entityType, float f) {
		this.field_18376 = entityType;
		this.field_18377 = f * f;
	}

	@Override
	protected Set<Pair<class_4140<?>, class_4141>> method_18914() {
		return ImmutableSet.of(Pair.of(class_4140.field_18446, class_4141.field_18457), Pair.of(class_4140.field_18442, class_4141.field_18456));
	}

	@Override
	protected boolean method_18919(ServerWorld serverWorld, LivingEntity livingEntity) {
		return ((List)livingEntity.method_18868().method_18904(class_4140.field_18442).get())
			.stream()
			.anyMatch(livingEntityx -> this.field_18376.equals(livingEntityx.method_5864()));
	}

	@Override
	protected void method_18920(ServerWorld serverWorld, LivingEntity livingEntity, long l) {
		class_4095<?> lv = livingEntity.method_18868();
		lv.method_18904(class_4140.field_18442)
			.ifPresent(
				list -> list.stream()
						.filter(livingEntityxx -> this.field_18376.equals(livingEntityxx.method_5864()))
						.filter(livingEntity2 -> livingEntity2.squaredDistanceTo(livingEntity) <= (double)this.field_18377)
						.findFirst()
						.ifPresent(livingEntityxx -> lv.method_18878(class_4140.field_18446, new class_4102(livingEntityxx)))
			);
	}
}
