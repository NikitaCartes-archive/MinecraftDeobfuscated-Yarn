package net.minecraft;

import com.google.common.collect.ImmutableSet;
import java.util.Set;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.world.ServerWorld;

public class class_4144 extends class_4148<LivingEntity> {
	@Override
	public void method_19101(ServerWorld serverWorld, LivingEntity livingEntity) {
		class_4095<?> lv = livingEntity.method_18868();
		if (livingEntity.method_6081() != null) {
			lv.method_18878(class_4140.field_18451, livingEntity.method_6081());
			Entity entity = ((DamageSource)lv.method_18904(class_4140.field_18451).get()).method_5529();
			if (entity instanceof LivingEntity) {
				lv.method_18878(class_4140.field_18452, (LivingEntity)entity);
			}
		} else {
			lv.method_18875(class_4140.field_18451);
		}
	}

	@Override
	protected Set<class_4140<?>> method_19099() {
		return ImmutableSet.of(class_4140.field_18451, class_4140.field_18452);
	}
}
