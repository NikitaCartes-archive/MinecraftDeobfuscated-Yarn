package net.minecraft;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import java.util.Set;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;

public class class_4113 extends class_4097<LivingEntity> {
	@Override
	protected Set<Pair<class_4140<?>, class_4141>> method_18914() {
		return ImmutableSet.of();
	}

	@Override
	protected void method_18920(ServerWorld serverWorld, LivingEntity livingEntity, long l) {
		class_4095<?> lv = livingEntity.method_18868();
		boolean bl = lv.method_18896(class_4140.field_18451) && lv.method_18904(class_4140.field_18451).isPresent();
		boolean bl2 = lv.method_18896(class_4140.field_18453) && lv.method_18904(class_4140.field_18453).isPresent();
		if (bl || bl2) {
			if (!lv.method_18906(class_4168.field_18599)) {
				lv.method_18875(class_4140.field_18449);
				lv.method_18875(class_4140.field_18445);
				lv.method_18875(class_4140.field_18446);
				lv.method_18875(class_4140.field_18448);
				lv.method_18875(class_4140.field_18447);
			}

			lv.method_18880(class_4168.field_18599);
		}
	}
}
