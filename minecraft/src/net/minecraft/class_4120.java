package net.minecraft;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import java.util.Set;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;

public class class_4120 extends class_4097<LivingEntity> {
	private final float field_18378;
	private final float field_18379;

	public class_4120(float f, float g) {
		this.field_18378 = f;
		this.field_18379 = g;
	}

	@Override
	protected Set<Pair<class_4140<?>, class_4141>> method_18914() {
		return ImmutableSet.of(Pair.of(class_4140.field_18445, class_4141.field_18457), Pair.of(class_4140.field_18446, class_4141.field_18456));
	}

	@Override
	protected void method_18920(ServerWorld serverWorld, LivingEntity livingEntity, long l) {
		class_4095<?> lv = livingEntity.method_18868();
		lv.method_18878(class_4140.field_18445, new class_4142((class_4115)lv.method_18904(class_4140.field_18446).get(), this.field_18378, 0));
	}
}
