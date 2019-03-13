package net.minecraft;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;

public class class_4124 extends class_4097<LivingEntity> {
	@Override
	protected boolean method_18919(ServerWorld serverWorld, LivingEntity livingEntity) {
		class_4095<?> lv = livingEntity.method_18868();
		Optional<class_4208> optional = lv.method_18904(class_4140.field_18440);
		return serverWorld.getRandom().nextInt(100) == 0
			&& optional.isPresent()
			&& Objects.equals(serverWorld.method_8597().method_12460(), ((class_4208)optional.get()).method_19442())
			&& livingEntity.method_5831(((class_4208)optional.get()).method_19446()) <= 16.0
			&& ((List)lv.method_18904(class_4140.field_18442).get()).stream().anyMatch(livingEntityx -> EntityType.VILLAGER.equals(livingEntityx.method_5864()));
	}

	@Override
	protected Set<Pair<class_4140<?>, class_4141>> method_18914() {
		return ImmutableSet.of(
			Pair.of(class_4140.field_18445, class_4141.field_18458),
			Pair.of(class_4140.field_18446, class_4141.field_18458),
			Pair.of(class_4140.field_18440, class_4141.field_18456),
			Pair.of(class_4140.field_18442, class_4141.field_18456),
			Pair.of(class_4140.field_18447, class_4141.field_18457)
		);
	}

	@Override
	protected void method_18920(ServerWorld serverWorld, LivingEntity livingEntity, long l) {
		class_4095<?> lv = livingEntity.method_18868();
		lv.method_18904(class_4140.field_18442)
			.ifPresent(
				list -> list.stream()
						.filter(livingEntityxx -> EntityType.VILLAGER.equals(livingEntityxx.method_5864()))
						.filter(livingEntity2 -> livingEntity2.squaredDistanceTo(livingEntity) <= 32.0)
						.findFirst()
						.ifPresent(livingEntityxx -> {
							lv.method_18878(class_4140.field_18447, livingEntityxx);
							lv.method_18878(class_4140.field_18446, new class_4102(livingEntityxx));
							lv.method_18878(class_4140.field_18445, new class_4142(new class_4102(livingEntityxx), 0.3F, 1));
						})
			);
	}
}
