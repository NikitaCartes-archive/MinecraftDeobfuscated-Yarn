package net.minecraft;

import com.google.common.collect.ImmutableSet;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;

public class class_4146 extends class_4148<LivingEntity> {
	@Override
	public void method_19101(ServerWorld serverWorld, LivingEntity livingEntity) {
		this.field_18463 = serverWorld.getTime();
		List<LivingEntity> list = serverWorld.method_8390(
			LivingEntity.class, livingEntity.method_5829().expand(16.0, 16.0, 16.0), livingEntity2 -> livingEntity2 != livingEntity && livingEntity2.isValid()
		);
		list.sort(Comparator.comparingDouble(livingEntity::squaredDistanceTo));
		class_4095<?> lv = livingEntity.method_18868();
		lv.method_18878(class_4140.field_18441, list);
		lv.method_18878(class_4140.field_18442, (List<LivingEntity>)list.stream().filter(livingEntity::canSee).collect(Collectors.toList()));
	}

	@Override
	protected Set<class_4140<?>> method_19099() {
		return ImmutableSet.of(class_4140.field_18441, class_4140.field_18442);
	}
}
