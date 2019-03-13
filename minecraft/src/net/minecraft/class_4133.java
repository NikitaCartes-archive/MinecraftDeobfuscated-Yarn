package net.minecraft;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import java.util.Objects;
import java.util.Set;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.server.world.ServerWorld;

public class class_4133 extends class_4097<VillagerEntity> {
	private boolean field_18403;
	private int field_18404;

	protected boolean method_19037(ServerWorld serverWorld, VillagerEntity villagerEntity) {
		return this.method_19036(serverWorld.getTimeOfDay() % 24000L, villagerEntity.method_19186());
	}

	@Override
	protected Set<Pair<class_4140<?>, class_4141>> method_18914() {
		return ImmutableSet.of(Pair.of(class_4140.field_18439, class_4141.field_18456));
	}

	protected void method_19038(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
		this.field_18403 = false;
		this.field_18404 = 0;
	}

	protected void method_19039(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
		if (!this.field_18403) {
			villagerEntity.method_19182();
			this.field_18403 = true;
			villagerEntity.method_19183();
		}

		this.field_18404++;
	}

	protected boolean method_19040(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
		class_4208 lv = (class_4208)villagerEntity.method_18868().method_18904(class_4140.field_18439).get();
		return this.field_18404 < 100
			&& Objects.equals(lv.method_19442(), serverWorld.method_8597().method_12460())
			&& villagerEntity.method_5831(lv.method_19446()) <= 4.0;
	}

	private boolean method_19036(long l, long m) {
		return m == 0L || l < m || l > m + 3500L;
	}
}
