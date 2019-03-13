package net.minecraft;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;

public class class_4128 extends class_4097<LivingEntity> {
	private final class_4140<class_4208> field_18390;
	private final Predicate<class_4158> field_18391;

	public class_4128(class_4158 arg, class_4140<class_4208> arg2) {
		this.field_18391 = arg.method_19164();
		this.field_18390 = arg2;
	}

	@Override
	protected Set<Pair<class_4140<?>, class_4141>> method_18914() {
		return ImmutableSet.of(Pair.of(this.field_18390, class_4141.field_18456));
	}

	@Override
	protected boolean method_18919(ServerWorld serverWorld, LivingEntity livingEntity) {
		class_4208 lv = (class_4208)livingEntity.method_18868().method_18904(this.field_18390).get();
		return Objects.equals(serverWorld.method_8597().method_12460(), lv.method_19442()) && livingEntity.method_5831(lv.method_19446()) <= 9.0;
	}

	@Override
	protected void method_18920(ServerWorld serverWorld, LivingEntity livingEntity, long l) {
		MinecraftServer minecraftServer = serverWorld.getServer();
		class_4095<?> lv = livingEntity.method_18868();
		class_4208 lv2 = (class_4208)lv.method_18904(this.field_18390).get();
		if (!minecraftServer.method_3847(lv2.method_19442()).method_19494().method_19116(lv2.method_19446(), this.field_18391)) {
			lv.method_18875(this.field_18390);
		}
	}
}
