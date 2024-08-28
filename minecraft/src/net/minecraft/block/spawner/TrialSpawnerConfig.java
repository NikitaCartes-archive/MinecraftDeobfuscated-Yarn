package net.minecraft.block.spawner;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryElementCodec;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.collection.DataPool;

public record TrialSpawnerConfig(
	int spawnRange,
	float totalMobs,
	float simultaneousMobs,
	float totalMobsAddedPerPlayer,
	float simultaneousMobsAddedPerPlayer,
	int ticksBetweenSpawn,
	DataPool<MobSpawnerEntry> spawnPotentials,
	DataPool<RegistryKey<LootTable>> lootTablesToEject,
	RegistryKey<LootTable> itemsToDropWhenOminous
) {
	public static final TrialSpawnerConfig DEFAULT = builder().build();
	public static final Codec<TrialSpawnerConfig> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					Codec.intRange(1, 128).optionalFieldOf("spawn_range", DEFAULT.spawnRange).forGetter(TrialSpawnerConfig::spawnRange),
					Codec.floatRange(0.0F, Float.MAX_VALUE).optionalFieldOf("total_mobs", DEFAULT.totalMobs).forGetter(TrialSpawnerConfig::totalMobs),
					Codec.floatRange(0.0F, Float.MAX_VALUE).optionalFieldOf("simultaneous_mobs", DEFAULT.simultaneousMobs).forGetter(TrialSpawnerConfig::simultaneousMobs),
					Codec.floatRange(0.0F, Float.MAX_VALUE)
						.optionalFieldOf("total_mobs_added_per_player", DEFAULT.totalMobsAddedPerPlayer)
						.forGetter(TrialSpawnerConfig::totalMobsAddedPerPlayer),
					Codec.floatRange(0.0F, Float.MAX_VALUE)
						.optionalFieldOf("simultaneous_mobs_added_per_player", DEFAULT.simultaneousMobsAddedPerPlayer)
						.forGetter(TrialSpawnerConfig::simultaneousMobsAddedPerPlayer),
					Codec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("ticks_between_spawn", DEFAULT.ticksBetweenSpawn).forGetter(TrialSpawnerConfig::ticksBetweenSpawn),
					MobSpawnerEntry.DATA_POOL_CODEC.optionalFieldOf("spawn_potentials", DataPool.empty()).forGetter(TrialSpawnerConfig::spawnPotentials),
					DataPool.createEmptyAllowedCodec(RegistryKey.createCodec(RegistryKeys.LOOT_TABLE))
						.optionalFieldOf("loot_tables_to_eject", DEFAULT.lootTablesToEject)
						.forGetter(TrialSpawnerConfig::lootTablesToEject),
					RegistryKey.createCodec(RegistryKeys.LOOT_TABLE)
						.optionalFieldOf("items_to_drop_when_ominous", DEFAULT.itemsToDropWhenOminous)
						.forGetter(TrialSpawnerConfig::itemsToDropWhenOminous)
				)
				.apply(instance, TrialSpawnerConfig::new)
	);
	public static final Codec<RegistryEntry<TrialSpawnerConfig>> ENTRY_CODEC = RegistryElementCodec.of(RegistryKeys.TRIAL_SPAWNER, CODEC);

	public int getTotalMobs(int additionalPlayers) {
		return (int)Math.floor((double)(this.totalMobs + this.totalMobsAddedPerPlayer * (float)additionalPlayers));
	}

	public int getSimultaneousMobs(int additionalPlayers) {
		return (int)Math.floor((double)(this.simultaneousMobs + this.simultaneousMobsAddedPerPlayer * (float)additionalPlayers));
	}

	public long getCooldownLength() {
		return 160L;
	}

	public static TrialSpawnerConfig.Builder builder() {
		return new TrialSpawnerConfig.Builder();
	}

	public static class Builder {
		private int spawnRange = 4;
		private float totalMobs = 6.0F;
		private float simultaneousMobs = 2.0F;
		private float totalMobsAddedPerPlayer = 2.0F;
		private float simultaneousMobsAddedPerPlayer = 1.0F;
		private int ticksBetweenSpawn = 40;
		private DataPool<MobSpawnerEntry> spawnPotentials = DataPool.empty();
		private DataPool<RegistryKey<LootTable>> lootTablesToEject = DataPool.<RegistryKey<LootTable>>builder()
			.add(LootTables.TRIAL_CHAMBER_CONSUMABLES_SPAWNER)
			.add(LootTables.TRIAL_CHAMBER_KEY_SPAWNER)
			.build();
		private RegistryKey<LootTable> itemsToDropWhenOminous = LootTables.TRIAL_CHAMBER_ITEMS_TO_DROP_WHEN_OMINOUS_SPAWNER;

		public TrialSpawnerConfig.Builder spawnRange(int spawnRange) {
			this.spawnRange = spawnRange;
			return this;
		}

		public TrialSpawnerConfig.Builder totalMobs(float totalMobs) {
			this.totalMobs = totalMobs;
			return this;
		}

		public TrialSpawnerConfig.Builder simultaneousMobs(float simultaneousMobs) {
			this.simultaneousMobs = simultaneousMobs;
			return this;
		}

		public TrialSpawnerConfig.Builder totalMobsAddedPerPlayer(float totalMobsAddedPerPlayer) {
			this.totalMobsAddedPerPlayer = totalMobsAddedPerPlayer;
			return this;
		}

		public TrialSpawnerConfig.Builder simultaneousMobsAddedPerPlayer(float simultaneousMobsAddedPerPlayer) {
			this.simultaneousMobsAddedPerPlayer = simultaneousMobsAddedPerPlayer;
			return this;
		}

		public TrialSpawnerConfig.Builder ticksBetweenSpawn(int ticksBetweenSpawn) {
			this.ticksBetweenSpawn = ticksBetweenSpawn;
			return this;
		}

		public TrialSpawnerConfig.Builder spawnPotentials(DataPool<MobSpawnerEntry> spawnPotentials) {
			this.spawnPotentials = spawnPotentials;
			return this;
		}

		public TrialSpawnerConfig.Builder lootTablesToEject(DataPool<RegistryKey<LootTable>> lootTablesToEject) {
			this.lootTablesToEject = lootTablesToEject;
			return this;
		}

		public TrialSpawnerConfig.Builder itemsToDropWhenOminous(RegistryKey<LootTable> itemsToDropWhenOminous) {
			this.itemsToDropWhenOminous = itemsToDropWhenOminous;
			return this;
		}

		public TrialSpawnerConfig build() {
			return new TrialSpawnerConfig(
				this.spawnRange,
				this.totalMobs,
				this.simultaneousMobs,
				this.totalMobsAddedPerPlayer,
				this.simultaneousMobsAddedPerPlayer,
				this.ticksBetweenSpawn,
				this.spawnPotentials,
				this.lootTablesToEject,
				this.itemsToDropWhenOminous
			);
		}
	}
}
