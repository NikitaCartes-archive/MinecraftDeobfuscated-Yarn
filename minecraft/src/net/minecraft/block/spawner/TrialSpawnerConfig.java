package net.minecraft.block.spawner;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.collection.DataPool;

public record TrialSpawnerConfig(
	int spawnRange,
	float totalMobs,
	float simultaneousMobs,
	float totalMobsAddedPerPlayer,
	float simultaneousMobsAddedPerPlayer,
	int ticksBetweenSpawn,
	DataPool<MobSpawnerEntry> spawnPotentialsDefinition,
	DataPool<RegistryKey<LootTable>> lootTablesToEject,
	RegistryKey<LootTable> itemsToDropWhenOminous
) {
	public static final TrialSpawnerConfig DEFAULT = new TrialSpawnerConfig(
		4,
		6.0F,
		2.0F,
		2.0F,
		1.0F,
		40,
		DataPool.empty(),
		DataPool.<RegistryKey<LootTable>>builder().add(LootTables.TRIAL_CHAMBER_CONSUMABLES_SPAWNER).add(LootTables.TRIAL_CHAMBER_KEY_SPAWNER).build(),
		LootTables.TRIAL_CHAMBER_ITEMS_TO_DROP_WHEN_OMINOUS_SPAWNER
	);
	public static final Codec<TrialSpawnerConfig> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					Codec.intRange(1, 128).lenientOptionalFieldOf("spawn_range", DEFAULT.spawnRange).forGetter(TrialSpawnerConfig::spawnRange),
					Codec.floatRange(0.0F, Float.MAX_VALUE).lenientOptionalFieldOf("total_mobs", DEFAULT.totalMobs).forGetter(TrialSpawnerConfig::totalMobs),
					Codec.floatRange(0.0F, Float.MAX_VALUE)
						.lenientOptionalFieldOf("simultaneous_mobs", DEFAULT.simultaneousMobs)
						.forGetter(TrialSpawnerConfig::simultaneousMobs),
					Codec.floatRange(0.0F, Float.MAX_VALUE)
						.lenientOptionalFieldOf("total_mobs_added_per_player", DEFAULT.totalMobsAddedPerPlayer)
						.forGetter(TrialSpawnerConfig::totalMobsAddedPerPlayer),
					Codec.floatRange(0.0F, Float.MAX_VALUE)
						.lenientOptionalFieldOf("simultaneous_mobs_added_per_player", DEFAULT.simultaneousMobsAddedPerPlayer)
						.forGetter(TrialSpawnerConfig::simultaneousMobsAddedPerPlayer),
					Codec.intRange(0, Integer.MAX_VALUE)
						.lenientOptionalFieldOf("ticks_between_spawn", DEFAULT.ticksBetweenSpawn)
						.forGetter(TrialSpawnerConfig::ticksBetweenSpawn),
					MobSpawnerEntry.DATA_POOL_CODEC.lenientOptionalFieldOf("spawn_potentials", DataPool.empty()).forGetter(TrialSpawnerConfig::spawnPotentialsDefinition),
					DataPool.createEmptyAllowedCodec(RegistryKey.createCodec(RegistryKeys.LOOT_TABLE))
						.lenientOptionalFieldOf("loot_tables_to_eject", DEFAULT.lootTablesToEject)
						.forGetter(TrialSpawnerConfig::lootTablesToEject),
					RegistryKey.createCodec(RegistryKeys.LOOT_TABLE)
						.lenientOptionalFieldOf("items_to_drop_when_ominous", DEFAULT.itemsToDropWhenOminous)
						.forGetter(TrialSpawnerConfig::itemsToDropWhenOminous)
				)
				.apply(instance, TrialSpawnerConfig::new)
	);

	public int getTotalMobs(int additionalPlayers) {
		return (int)Math.floor((double)(this.totalMobs + this.totalMobsAddedPerPlayer * (float)additionalPlayers));
	}

	public int getSimultaneousMobs(int additionalPlayers) {
		return (int)Math.floor((double)(this.simultaneousMobs + this.simultaneousMobsAddedPerPlayer * (float)additionalPlayers));
	}

	public long getCooldownLength() {
		return 160L;
	}
}
