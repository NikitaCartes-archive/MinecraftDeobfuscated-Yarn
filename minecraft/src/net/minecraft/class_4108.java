package net.minecraft;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import java.util.Set;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;

public class class_4108 extends class_4097<VillagerEntity> {
	private final float field_18362;

	public class_4108(float f) {
		this.field_18362 = f;
	}

	protected boolean method_18954(ServerWorld serverWorld, VillagerEntity villagerEntity) {
		PlayerEntity playerEntity = villagerEntity.getCurrentCustomer();
		return villagerEntity.isValid()
			&& playerEntity != null
			&& !villagerEntity.isInsideWater()
			&& !villagerEntity.velocityModified
			&& villagerEntity.squaredDistanceTo(playerEntity) <= 16.0
			&& playerEntity.field_7512 != null;
	}

	protected boolean method_18955(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
		return this.method_18954(serverWorld, villagerEntity);
	}

	protected void method_18956(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
		this.method_18953(villagerEntity);
	}

	protected void method_18957(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
		villagerEntity.method_19181();
		class_4095<?> lv = villagerEntity.method_18868();
		lv.method_18875(class_4140.field_18445);
		lv.method_18875(class_4140.field_18446);
	}

	protected void method_18958(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
		this.method_18953(villagerEntity);
	}

	@Override
	protected boolean method_18915(long l) {
		return false;
	}

	@Override
	protected Set<Pair<class_4140<?>, class_4141>> method_18914() {
		return ImmutableSet.of(Pair.of(class_4140.field_18445, class_4141.field_18458), Pair.of(class_4140.field_18446, class_4141.field_18458));
	}

	private void method_18953(VillagerEntity villagerEntity) {
		class_4102 lv = new class_4102(villagerEntity.getCurrentCustomer());
		class_4095<?> lv2 = villagerEntity.method_18868();
		lv2.method_18878(class_4140.field_18445, new class_4142(lv, this.field_18362, 2));
		lv2.method_18878(class_4140.field_18446, lv);
	}
}
