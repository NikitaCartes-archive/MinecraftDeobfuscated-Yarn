package net.minecraft;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;

public class class_4109 extends class_4097<LivingEntity> {
	private final EntityType<?> field_18363;
	private final int field_18364;
	private final Predicate<LivingEntity> field_18365;
	private final Predicate<LivingEntity> field_18366;

	public class_4109(EntityType<?> entityType, int i, Predicate<LivingEntity> predicate, Predicate<LivingEntity> predicate2) {
		this.field_18363 = entityType;
		this.field_18364 = i * i;
		this.field_18365 = predicate2;
		this.field_18366 = predicate;
	}

	public class_4109(EntityType<?> entityType, int i) {
		this(entityType, i, livingEntity -> true, livingEntity -> true);
	}

	@Override
	public Set<Pair<class_4140<?>, class_4141>> method_18914() {
		return ImmutableSet.of(
			Pair.of(class_4140.field_18446, class_4141.field_18458),
			Pair.of(class_4140.field_18447, class_4141.field_18457),
			Pair.of(class_4140.field_18442, class_4141.field_18456)
		);
	}

	@Override
	public boolean method_18919(ServerWorld serverWorld, LivingEntity livingEntity) {
		return this.field_18366.test(livingEntity)
			&& ((List)livingEntity.method_18868().method_18904(class_4140.field_18442).get())
				.stream()
				.anyMatch(livingEntityx -> this.field_18363.equals(livingEntityx.method_5864()) && this.field_18365.test(livingEntityx));
	}

	@Override
	public void method_18920(ServerWorld serverWorld, LivingEntity livingEntity, long l) {
		super.method_18920(serverWorld, livingEntity, l);
		class_4095<?> lv = livingEntity.method_18868();
		lv.method_18904(class_4140.field_18442)
			.ifPresent(
				list -> list.stream()
						.filter(livingEntityxx -> this.field_18363.equals(livingEntityxx.method_5864()))
						.filter(livingEntity2 -> livingEntity2.squaredDistanceTo(livingEntity) <= (double)this.field_18364)
						.filter(this.field_18365)
						.findFirst()
						.ifPresent(livingEntityxx -> {
							lv.method_18878(class_4140.field_18447, livingEntityxx);
							lv.method_18878(class_4140.field_18446, new class_4102(livingEntityxx));
						})
			);
	}
}
