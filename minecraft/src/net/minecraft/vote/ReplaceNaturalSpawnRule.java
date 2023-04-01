package net.minecraft.vote;

import java.util.EnumSet;
import java.util.Optional;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.class_8275;
import net.minecraft.class_8285;
import net.minecraft.class_8291;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.util.math.random.Random;

public class ReplaceNaturalSpawnRule extends class_8285<EntityType<?>> {
	private static final EnumSet<SpawnGroup> SPAWN_GROUPS = EnumSet.of(
		SpawnGroup.CREATURE, SpawnGroup.MONSTER, SpawnGroup.WATER_CREATURE, SpawnGroup.WATER_AMBIENT
	);

	public ReplaceNaturalSpawnRule() {
		super(RegistryKeys.ENTITY_TYPE);
	}

	@Override
	public Stream<class_8291> method_50118(MinecraftServer minecraftServer, Random random, int i) {
		Registry<EntityType<?>> registry = minecraftServer.getRegistryManager().get(this.field_43488);
		return Stream.generate(() -> registry.getRandom(random))
			.flatMap(Optional::stream)
			.limit(1000L)
			.filter(reference -> SPAWN_GROUPS.contains(((EntityType)reference.value()).getSpawnGroup()))
			.flatMap(
				reference -> {
					RegistryKey<EntityType<?>> registryKey = reference.registryKey();
					SpawnGroup spawnGroup = ((EntityType)reference.value()).getSpawnGroup();
					return Stream.generate(() -> registry.getRandom(random))
						.flatMap(Optional::stream)
						.filter(referencex -> !referencex.matchesKey(registryKey) && ((EntityType)referencex.value()).getSpawnGroup() == spawnGroup)
						.limit((long)i)
						.map(referencex -> new class_8275.class_8276(registryKey, referencex.registryKey()));
				}
			)
			.limit((long)i)
			.map(arg -> arg);
	}

	protected Text method_50162(RegistryKey<EntityType<?>> registryKey, RegistryKey<EntityType<?>> registryKey2) {
		Text text = Text.translatable(Util.createTranslationKey("entity", registryKey.getValue()));
		Text text2 = Text.translatable(Util.createTranslationKey("entity", registryKey2.getValue()));
		return Text.translatable("rule.natural_spawn_replace", text, text2);
	}

	@Nullable
	public RegistryKey<EntityType<?>> method_50404(RegistryKey<EntityType<?>> registryKey) {
		return (RegistryKey<EntityType<?>>)this.field_43489.get(registryKey);
	}
}
