package net.minecraft;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import java.util.Set;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;

public class class_4125 extends class_4097<MobEntity> {
	private final float field_18386;
	private final float field_18387;

	public class_4125(float f, float g) {
		this.field_18386 = f;
		this.field_18387 = g;
	}

	protected boolean method_19010(ServerWorld serverWorld, MobEntity mobEntity) {
		return mobEntity.isInsideWater() && mobEntity.method_5861() > (double)this.field_18386 || mobEntity.isTouchingLava();
	}

	protected boolean method_19011(ServerWorld serverWorld, MobEntity mobEntity, long l) {
		return this.method_19010(serverWorld, mobEntity);
	}

	@Override
	protected Set<Pair<class_4140<?>, class_4141>> method_18914() {
		return ImmutableSet.of();
	}

	protected void method_19012(ServerWorld serverWorld, MobEntity mobEntity, long l) {
		if (mobEntity.getRand().nextFloat() < this.field_18387) {
			mobEntity.method_5993().setActive();
		}
	}
}
