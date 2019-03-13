package net.minecraft;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import java.util.Set;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.registry.Registry;
import net.minecraft.village.VillagerProfession;

public class class_4114 extends class_4097<VillagerEntity> {
	@Override
	protected Set<Pair<class_4140<?>, class_4141>> method_18914() {
		return ImmutableSet.of(Pair.of(class_4140.field_18439, class_4141.field_18456));
	}

	protected boolean method_18987(ServerWorld serverWorld, VillagerEntity villagerEntity) {
		return villagerEntity.getVillagerData().method_16924() == VillagerProfession.field_17051;
	}

	protected void method_18988(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
		class_4208 lv = (class_4208)villagerEntity.method_18868().method_18904(class_4140.field_18439).get();
		MinecraftServer minecraftServer = serverWorld.getServer();
		minecraftServer.method_3847(lv.method_19442())
			.method_19494()
			.method_19132(lv.method_19446())
			.ifPresent(
				arg -> Registry.VILLAGER_PROFESSION
						.stream()
						.filter(villagerProfession -> villagerProfession.method_19198() == arg)
						.findFirst()
						.ifPresent(villagerProfession -> {
							villagerEntity.method_7221(villagerEntity.getVillagerData().method_16921(villagerProfession));
							villagerEntity.method_19179(serverWorld);
						})
			);
	}
}
