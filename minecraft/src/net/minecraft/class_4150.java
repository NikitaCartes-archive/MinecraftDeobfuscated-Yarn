package net.minecraft;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.util.Set;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;

public class class_4150 extends class_4148<LivingEntity> {
	private static final ImmutableMap<EntityType<?>, Float> field_18473 = ImmutableMap.<EntityType<?>, Float>builder()
		.put(EntityType.ZOMBIE, 8.0F)
		.put(EntityType.EVOKER, 12.0F)
		.put(EntityType.VINDICATOR, 8.0F)
		.put(EntityType.VEX, 8.0F)
		.put(EntityType.PILLAGER, 15.0F)
		.put(EntityType.ILLUSIONER, 12.0F)
		.build();

	@Override
	public void method_19101(ServerWorld serverWorld, LivingEntity livingEntity) {
		class_4095<?> lv = livingEntity.method_18868();
		lv.method_18904(class_4140.field_18442)
			.ifPresent(
				list -> list.stream()
						.filter(livingEntityxx -> field_18473.containsKey(livingEntityxx.method_5864()))
						.filter(livingEntity2 -> livingEntity2.squaredDistanceTo(livingEntity) <= (double)field_18473.get(livingEntity2.method_5864()).floatValue())
						.findFirst()
						.ifPresent(livingEntityxx -> lv.method_18878(class_4140.field_18453, livingEntityxx))
			);
	}

	@Override
	protected Set<class_4140<?>> method_19099() {
		return ImmutableSet.of(class_4140.field_18453);
	}
}
