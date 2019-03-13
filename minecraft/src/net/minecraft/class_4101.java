package net.minecraft;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import java.util.Set;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;

public class class_4101 extends class_4097<LivingEntity> {
	public class_4101(int i, int j) {
		super(i, j);
	}

	@Override
	protected boolean method_18927(ServerWorld serverWorld, LivingEntity livingEntity, long l) {
		return true;
	}

	@Override
	protected Set<Pair<class_4140<?>, class_4141>> method_18914() {
		return ImmutableSet.of();
	}
}
