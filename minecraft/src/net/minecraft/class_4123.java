package net.minecraft;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import java.util.Objects;
import java.util.Set;
import net.minecraft.block.BedBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.BlockTags;

public class class_4123 extends class_4097<LivingEntity> {
	@Override
	protected boolean method_18919(ServerWorld serverWorld, LivingEntity livingEntity) {
		class_4208 lv = (class_4208)livingEntity.method_18868().method_18904(class_4140.field_18438).get();
		if (!Objects.equals(serverWorld.method_8597().method_12460(), lv.method_19442())) {
			return false;
		} else {
			BlockState blockState = serverWorld.method_8320(lv.method_19446());
			return livingEntity.method_5831(lv.method_19446()) <= 2.0
				&& blockState.getBlock().method_9525(BlockTags.field_16443)
				&& !(Boolean)blockState.method_11654(BedBlock.field_9968);
		}
	}

	@Override
	protected Set<Pair<class_4140<?>, class_4141>> method_18914() {
		return ImmutableSet.of(Pair.of(class_4140.field_18438, class_4141.field_18456));
	}

	@Override
	protected boolean method_18927(ServerWorld serverWorld, LivingEntity livingEntity, long l) {
		return livingEntity.method_18868().method_18906(class_4168.field_18597);
	}

	@Override
	protected void method_18920(ServerWorld serverWorld, LivingEntity livingEntity, long l) {
		livingEntity.method_18403(((class_4208)livingEntity.method_18868().method_18904(class_4140.field_18438).get()).method_19446());
	}

	@Override
	protected boolean method_18915(long l) {
		return false;
	}

	@Override
	protected void method_18926(ServerWorld serverWorld, LivingEntity livingEntity, long l) {
		livingEntity.wakeUp();
	}
}
