package net.minecraft;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;

public class class_4106<E extends LivingEntity, T extends LivingEntity> extends class_4097<E> {
	private final int field_18355;
	private final float field_18356;
	private final EntityType<? extends T> field_18357;
	private final int field_18358;
	private final Predicate<T> field_18359;
	private final Predicate<E> field_18360;
	private final class_4140<T> field_18361;

	public class_4106(EntityType<? extends T> entityType, int i, Predicate<E> predicate, Predicate<T> predicate2, class_4140<T> arg, float f, int j) {
		this.field_18357 = entityType;
		this.field_18356 = f;
		this.field_18358 = i * i;
		this.field_18355 = j;
		this.field_18359 = predicate2;
		this.field_18360 = predicate;
		this.field_18361 = arg;
	}

	public static <T extends LivingEntity> class_4106<LivingEntity, T> method_18941(EntityType<? extends T> entityType, int i, class_4140<T> arg, float f, int j) {
		return new class_4106<>(entityType, i, livingEntity -> true, livingEntity -> true, arg, f, j);
	}

	@Override
	protected Set<Pair<class_4140<?>, class_4141>> method_18914() {
		return ImmutableSet.of(
			Pair.of(class_4140.field_18446, class_4141.field_18458),
			Pair.of(class_4140.field_18445, class_4141.field_18457),
			Pair.of(this.field_18361, class_4141.field_18457),
			Pair.of(class_4140.field_18442, class_4141.field_18456)
		);
	}

	@Override
	protected boolean method_18919(ServerWorld serverWorld, E livingEntity) {
		return this.field_18360.test(livingEntity)
			&& ((List)livingEntity.method_18868().method_18904(class_4140.field_18442).get())
				.stream()
				.anyMatch(livingEntityx -> this.field_18357.equals(livingEntityx.method_5864()) && this.field_18359.test(livingEntityx));
	}

	@Override
	protected void method_18920(ServerWorld serverWorld, E livingEntity, long l) {
		class_4095<?> lv = livingEntity.method_18868();
		lv.method_18904(class_4140.field_18442)
			.ifPresent(
				list -> list.stream()
						.filter(livingEntityxx -> this.field_18357.equals(livingEntityxx.method_5864()))
						.map(livingEntityxx -> livingEntityxx)
						.filter(livingEntity2 -> livingEntity2.squaredDistanceTo(livingEntity) <= (double)this.field_18358)
						.filter(this.field_18359)
						.findFirst()
						.ifPresent(livingEntityxx -> {
							lv.method_18878(this.field_18361, (T)livingEntityxx);
							lv.method_18878(class_4140.field_18446, new class_4102(livingEntityxx));
							lv.method_18878(class_4140.field_18445, new class_4142(new class_4102(livingEntityxx), this.field_18356, this.field_18355));
						})
			);
	}
}
