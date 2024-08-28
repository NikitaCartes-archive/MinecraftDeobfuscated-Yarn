package net.minecraft.block.spawner;

import java.util.Optional;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentTable;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DataPool;

public class TrialSpawnerConfigs {
	private static final TrialSpawnerConfigs.ConfigKeyPair BREEZE = TrialSpawnerConfigs.ConfigKeyPair.of("trial_chamber/breeze");
	private static final TrialSpawnerConfigs.ConfigKeyPair MELEE_HUSK = TrialSpawnerConfigs.ConfigKeyPair.of("trial_chamber/melee/husk");
	private static final TrialSpawnerConfigs.ConfigKeyPair MELEE_SPIDER = TrialSpawnerConfigs.ConfigKeyPair.of("trial_chamber/melee/spider");
	private static final TrialSpawnerConfigs.ConfigKeyPair MELEE_ZOMBIE = TrialSpawnerConfigs.ConfigKeyPair.of("trial_chamber/melee/zombie");
	private static final TrialSpawnerConfigs.ConfigKeyPair RANGED_POISON_SKELETON = TrialSpawnerConfigs.ConfigKeyPair.of("trial_chamber/ranged/poison_skeleton");
	private static final TrialSpawnerConfigs.ConfigKeyPair RANGED_SKELETON = TrialSpawnerConfigs.ConfigKeyPair.of("trial_chamber/ranged/skeleton");
	private static final TrialSpawnerConfigs.ConfigKeyPair RANGED_STRAY = TrialSpawnerConfigs.ConfigKeyPair.of("trial_chamber/ranged/stray");
	private static final TrialSpawnerConfigs.ConfigKeyPair SLOW_RANGED_POISON_SKELETON = TrialSpawnerConfigs.ConfigKeyPair.of(
		"trial_chamber/slow_ranged/poison_skeleton"
	);
	private static final TrialSpawnerConfigs.ConfigKeyPair SLOW_RANGED_SKELETON = TrialSpawnerConfigs.ConfigKeyPair.of("trial_chamber/slow_ranged/skeleton");
	private static final TrialSpawnerConfigs.ConfigKeyPair SLOW_RANGED_STRAY = TrialSpawnerConfigs.ConfigKeyPair.of("trial_chamber/slow_ranged/stray");
	private static final TrialSpawnerConfigs.ConfigKeyPair SMALL_MELEE_BABY_ZOMBIE = TrialSpawnerConfigs.ConfigKeyPair.of("trial_chamber/small_melee/baby_zombie");
	private static final TrialSpawnerConfigs.ConfigKeyPair SMALL_MELEE_CAVE_SPIDER = TrialSpawnerConfigs.ConfigKeyPair.of("trial_chamber/small_melee/cave_spider");
	private static final TrialSpawnerConfigs.ConfigKeyPair SMALL_MELEE_SILVERFISH = TrialSpawnerConfigs.ConfigKeyPair.of("trial_chamber/small_melee/silverfish");
	private static final TrialSpawnerConfigs.ConfigKeyPair SMALL_MELEE_SLIME = TrialSpawnerConfigs.ConfigKeyPair.of("trial_chamber/small_melee/slime");

	public static void bootstrap(Registerable<TrialSpawnerConfig> registry) {
		register(
			registry,
			BREEZE,
			TrialSpawnerConfig.builder()
				.simultaneousMobs(1.0F)
				.simultaneousMobsAddedPerPlayer(0.5F)
				.ticksBetweenSpawn(20)
				.totalMobs(2.0F)
				.totalMobsAddedPerPlayer(1.0F)
				.spawnPotentials(DataPool.of(createEntry(EntityType.BREEZE)))
				.build(),
			TrialSpawnerConfig.builder()
				.simultaneousMobsAddedPerPlayer(0.5F)
				.ticksBetweenSpawn(20)
				.totalMobs(4.0F)
				.totalMobsAddedPerPlayer(1.0F)
				.spawnPotentials(DataPool.of(createEntry(EntityType.BREEZE)))
				.lootTablesToEject(
					DataPool.<RegistryKey<LootTable>>builder()
						.add(LootTables.OMINOUS_TRIAL_CHAMBER_KEY_SPAWNER, 3)
						.add(LootTables.OMINOUS_TRIAL_CHAMBER_CONSUMABLES_SPAWNER, 7)
						.build()
				)
				.build()
		);
		register(
			registry,
			MELEE_HUSK,
			genericBuilder().spawnPotentials(DataPool.of(createEntry(EntityType.HUSK))).build(),
			genericBuilder()
				.spawnPotentials(DataPool.of(createEntry(EntityType.HUSK, LootTables.TRIAL_CHAMBER_MELEE_EQUIPMENT)))
				.lootTablesToEject(
					DataPool.<RegistryKey<LootTable>>builder()
						.add(LootTables.OMINOUS_TRIAL_CHAMBER_KEY_SPAWNER, 3)
						.add(LootTables.OMINOUS_TRIAL_CHAMBER_CONSUMABLES_SPAWNER, 7)
						.build()
				)
				.build()
		);
		register(
			registry,
			MELEE_SPIDER,
			genericBuilder().spawnPotentials(DataPool.of(createEntry(EntityType.SPIDER))).build(),
			ominousMeleeBuilder()
				.spawnPotentials(DataPool.of(createEntry(EntityType.SPIDER)))
				.lootTablesToEject(
					DataPool.<RegistryKey<LootTable>>builder()
						.add(LootTables.OMINOUS_TRIAL_CHAMBER_KEY_SPAWNER, 3)
						.add(LootTables.OMINOUS_TRIAL_CHAMBER_CONSUMABLES_SPAWNER, 7)
						.build()
				)
				.build()
		);
		register(
			registry,
			MELEE_ZOMBIE,
			genericBuilder().spawnPotentials(DataPool.of(createEntry(EntityType.ZOMBIE))).build(),
			genericBuilder()
				.lootTablesToEject(
					DataPool.<RegistryKey<LootTable>>builder()
						.add(LootTables.OMINOUS_TRIAL_CHAMBER_KEY_SPAWNER, 3)
						.add(LootTables.OMINOUS_TRIAL_CHAMBER_CONSUMABLES_SPAWNER, 7)
						.build()
				)
				.spawnPotentials(DataPool.of(createEntry(EntityType.ZOMBIE, LootTables.TRIAL_CHAMBER_MELEE_EQUIPMENT)))
				.build()
		);
		register(
			registry,
			RANGED_POISON_SKELETON,
			genericBuilder().spawnPotentials(DataPool.of(createEntry(EntityType.BOGGED))).build(),
			genericBuilder()
				.lootTablesToEject(
					DataPool.<RegistryKey<LootTable>>builder()
						.add(LootTables.OMINOUS_TRIAL_CHAMBER_KEY_SPAWNER, 3)
						.add(LootTables.OMINOUS_TRIAL_CHAMBER_CONSUMABLES_SPAWNER, 7)
						.build()
				)
				.spawnPotentials(DataPool.of(createEntry(EntityType.BOGGED, LootTables.TRIAL_CHAMBER_RANGED_EQUIPMENT)))
				.build()
		);
		register(
			registry,
			RANGED_SKELETON,
			genericBuilder().spawnPotentials(DataPool.of(createEntry(EntityType.SKELETON))).build(),
			genericBuilder()
				.lootTablesToEject(
					DataPool.<RegistryKey<LootTable>>builder()
						.add(LootTables.OMINOUS_TRIAL_CHAMBER_KEY_SPAWNER, 3)
						.add(LootTables.OMINOUS_TRIAL_CHAMBER_CONSUMABLES_SPAWNER, 7)
						.build()
				)
				.spawnPotentials(DataPool.of(createEntry(EntityType.SKELETON, LootTables.TRIAL_CHAMBER_RANGED_EQUIPMENT)))
				.build()
		);
		register(
			registry,
			RANGED_STRAY,
			genericBuilder().spawnPotentials(DataPool.of(createEntry(EntityType.STRAY))).build(),
			genericBuilder()
				.lootTablesToEject(
					DataPool.<RegistryKey<LootTable>>builder()
						.add(LootTables.OMINOUS_TRIAL_CHAMBER_KEY_SPAWNER, 3)
						.add(LootTables.OMINOUS_TRIAL_CHAMBER_CONSUMABLES_SPAWNER, 7)
						.build()
				)
				.spawnPotentials(DataPool.of(createEntry(EntityType.STRAY, LootTables.TRIAL_CHAMBER_RANGED_EQUIPMENT)))
				.build()
		);
		register(
			registry,
			SLOW_RANGED_POISON_SKELETON,
			slowRangedBuilder().spawnPotentials(DataPool.of(createEntry(EntityType.BOGGED))).build(),
			slowRangedBuilder()
				.lootTablesToEject(
					DataPool.<RegistryKey<LootTable>>builder()
						.add(LootTables.OMINOUS_TRIAL_CHAMBER_KEY_SPAWNER, 3)
						.add(LootTables.OMINOUS_TRIAL_CHAMBER_CONSUMABLES_SPAWNER, 7)
						.build()
				)
				.spawnPotentials(DataPool.of(createEntry(EntityType.BOGGED, LootTables.TRIAL_CHAMBER_RANGED_EQUIPMENT)))
				.build()
		);
		register(
			registry,
			SLOW_RANGED_SKELETON,
			slowRangedBuilder().spawnPotentials(DataPool.of(createEntry(EntityType.SKELETON))).build(),
			slowRangedBuilder()
				.lootTablesToEject(
					DataPool.<RegistryKey<LootTable>>builder()
						.add(LootTables.OMINOUS_TRIAL_CHAMBER_KEY_SPAWNER, 3)
						.add(LootTables.OMINOUS_TRIAL_CHAMBER_CONSUMABLES_SPAWNER, 7)
						.build()
				)
				.spawnPotentials(DataPool.of(createEntry(EntityType.SKELETON, LootTables.TRIAL_CHAMBER_RANGED_EQUIPMENT)))
				.build()
		);
		register(
			registry,
			SLOW_RANGED_STRAY,
			slowRangedBuilder().spawnPotentials(DataPool.of(createEntry(EntityType.STRAY))).build(),
			slowRangedBuilder()
				.lootTablesToEject(
					DataPool.<RegistryKey<LootTable>>builder()
						.add(LootTables.OMINOUS_TRIAL_CHAMBER_KEY_SPAWNER, 3)
						.add(LootTables.OMINOUS_TRIAL_CHAMBER_CONSUMABLES_SPAWNER, 7)
						.build()
				)
				.spawnPotentials(DataPool.of(createEntry(EntityType.STRAY, LootTables.TRIAL_CHAMBER_RANGED_EQUIPMENT)))
				.build()
		);
		register(
			registry,
			SMALL_MELEE_BABY_ZOMBIE,
			TrialSpawnerConfig.builder()
				.simultaneousMobsAddedPerPlayer(0.5F)
				.ticksBetweenSpawn(20)
				.spawnPotentials(DataPool.of(createEntry(EntityType.ZOMBIE, nbt -> nbt.putBoolean("IsBaby", true), null)))
				.build(),
			TrialSpawnerConfig.builder()
				.simultaneousMobsAddedPerPlayer(0.5F)
				.ticksBetweenSpawn(20)
				.lootTablesToEject(
					DataPool.<RegistryKey<LootTable>>builder()
						.add(LootTables.OMINOUS_TRIAL_CHAMBER_KEY_SPAWNER, 3)
						.add(LootTables.OMINOUS_TRIAL_CHAMBER_CONSUMABLES_SPAWNER, 7)
						.build()
				)
				.spawnPotentials(DataPool.of(createEntry(EntityType.ZOMBIE, nbt -> nbt.putBoolean("IsBaby", true), LootTables.TRIAL_CHAMBER_MELEE_EQUIPMENT)))
				.build()
		);
		register(
			registry,
			SMALL_MELEE_CAVE_SPIDER,
			genericBuilder().spawnPotentials(DataPool.of(createEntry(EntityType.CAVE_SPIDER))).build(),
			ominousMeleeBuilder()
				.lootTablesToEject(
					DataPool.<RegistryKey<LootTable>>builder()
						.add(LootTables.OMINOUS_TRIAL_CHAMBER_KEY_SPAWNER, 3)
						.add(LootTables.OMINOUS_TRIAL_CHAMBER_CONSUMABLES_SPAWNER, 7)
						.build()
				)
				.spawnPotentials(DataPool.of(createEntry(EntityType.CAVE_SPIDER)))
				.build()
		);
		register(
			registry,
			SMALL_MELEE_SILVERFISH,
			genericBuilder().spawnPotentials(DataPool.of(createEntry(EntityType.SILVERFISH))).build(),
			ominousMeleeBuilder()
				.lootTablesToEject(
					DataPool.<RegistryKey<LootTable>>builder()
						.add(LootTables.OMINOUS_TRIAL_CHAMBER_KEY_SPAWNER, 3)
						.add(LootTables.OMINOUS_TRIAL_CHAMBER_CONSUMABLES_SPAWNER, 7)
						.build()
				)
				.spawnPotentials(DataPool.of(createEntry(EntityType.SILVERFISH)))
				.build()
		);
		register(
			registry,
			SMALL_MELEE_SLIME,
			genericBuilder()
				.spawnPotentials(
					DataPool.<MobSpawnerEntry>builder()
						.add(createEntry(EntityType.SLIME, nbt -> nbt.putByte("Size", (byte)1)), 3)
						.add(createEntry(EntityType.SLIME, nbt -> nbt.putByte("Size", (byte)2)), 1)
						.build()
				)
				.build(),
			ominousMeleeBuilder()
				.lootTablesToEject(
					DataPool.<RegistryKey<LootTable>>builder()
						.add(LootTables.OMINOUS_TRIAL_CHAMBER_KEY_SPAWNER, 3)
						.add(LootTables.OMINOUS_TRIAL_CHAMBER_CONSUMABLES_SPAWNER, 7)
						.build()
				)
				.spawnPotentials(
					DataPool.<MobSpawnerEntry>builder()
						.add(createEntry(EntityType.SLIME, nbt -> nbt.putByte("Size", (byte)1)), 3)
						.add(createEntry(EntityType.SLIME, nbt -> nbt.putByte("Size", (byte)2)), 1)
						.build()
				)
				.build()
		);
	}

	private static <T extends Entity> MobSpawnerEntry createEntry(EntityType<T> entityType) {
		return createEntry(entityType, nbt -> {
		}, null);
	}

	private static <T extends Entity> MobSpawnerEntry createEntry(EntityType<T> entityType, Consumer<NbtCompound> nbtConsumer) {
		return createEntry(entityType, nbtConsumer, null);
	}

	private static <T extends Entity> MobSpawnerEntry createEntry(EntityType<T> entityType, RegistryKey<LootTable> equipmentTable) {
		return createEntry(entityType, nbt -> {
		}, equipmentTable);
	}

	private static <T extends Entity> MobSpawnerEntry createEntry(
		EntityType<T> entityType, Consumer<NbtCompound> nbtConsumer, @Nullable RegistryKey<LootTable> equipmentTable
	) {
		NbtCompound nbtCompound = new NbtCompound();
		nbtCompound.putString("id", Registries.ENTITY_TYPE.getId(entityType).toString());
		nbtConsumer.accept(nbtCompound);
		Optional<EquipmentTable> optional = Optional.ofNullable(equipmentTable).map(lootTable -> new EquipmentTable(lootTable, 0.0F));
		return new MobSpawnerEntry(nbtCompound, Optional.empty(), optional);
	}

	private static void register(
		Registerable<TrialSpawnerConfig> registry, TrialSpawnerConfigs.ConfigKeyPair configPair, TrialSpawnerConfig normalConfig, TrialSpawnerConfig ominousConfig
	) {
		registry.register(configPair.normal, normalConfig);
		registry.register(configPair.ominous, ominousConfig);
	}

	static RegistryKey<TrialSpawnerConfig> keyOf(String id) {
		return RegistryKey.of(RegistryKeys.TRIAL_SPAWNER, Identifier.ofVanilla(id));
	}

	private static TrialSpawnerConfig.Builder ominousMeleeBuilder() {
		return TrialSpawnerConfig.builder().simultaneousMobs(4.0F).simultaneousMobsAddedPerPlayer(0.5F).ticksBetweenSpawn(20).totalMobs(12.0F);
	}

	private static TrialSpawnerConfig.Builder slowRangedBuilder() {
		return TrialSpawnerConfig.builder().simultaneousMobs(4.0F).simultaneousMobsAddedPerPlayer(2.0F).ticksBetweenSpawn(160);
	}

	private static TrialSpawnerConfig.Builder genericBuilder() {
		return TrialSpawnerConfig.builder().simultaneousMobs(3.0F).simultaneousMobsAddedPerPlayer(0.5F).ticksBetweenSpawn(20);
	}

	static record ConfigKeyPair(RegistryKey<TrialSpawnerConfig> normal, RegistryKey<TrialSpawnerConfig> ominous) {

		public static TrialSpawnerConfigs.ConfigKeyPair of(String id) {
			return new TrialSpawnerConfigs.ConfigKeyPair(TrialSpawnerConfigs.keyOf(id + "/normal"), TrialSpawnerConfigs.keyOf(id + "/ominous"));
		}
	}
}
