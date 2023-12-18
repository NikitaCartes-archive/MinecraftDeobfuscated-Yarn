package net.minecraft.block.spawner;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.loot.LootTables;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DataPool;

public record TrialSpawnerConfig(
	int requiredPlayerRange,
	int spawnRange,
	float totalMobs,
	float simultaneousMobs,
	float totalMobsAddedPerPlayer,
	float simultaneousMobsAddedPerPlayer,
	int ticksBetweenSpawn,
	int targetCooldownLength,
	DataPool<MobSpawnerEntry> spawnPotentialsDefinition,
	DataPool<Identifier> lootTablesToEject
) {
	public static TrialSpawnerConfig defaultInstance = new TrialSpawnerConfig(
		14,
		4,
		6.0F,
		2.0F,
		2.0F,
		1.0F,
		40,
		36000,
		DataPool.empty(),
		DataPool.<Identifier>builder().add(LootTables.TRIAL_CHAMBER_CONSUMABLES_SPAWNER).add(LootTables.TRIAL_CHAMBER_KEY_SPAWNER).build()
	);
	public static MapCodec<TrialSpawnerConfig> codec = RecordCodecBuilder.mapCodec(
		instance -> instance.group(
					Codec.intRange(1, 128).optionalFieldOf("required_player_range", defaultInstance.requiredPlayerRange).forGetter(TrialSpawnerConfig::requiredPlayerRange),
					Codec.intRange(1, 128).optionalFieldOf("spawn_range", defaultInstance.spawnRange).forGetter(TrialSpawnerConfig::spawnRange),
					Codec.floatRange(0.0F, Float.MAX_VALUE).optionalFieldOf("total_mobs", defaultInstance.totalMobs).forGetter(TrialSpawnerConfig::totalMobs),
					Codec.floatRange(0.0F, Float.MAX_VALUE)
						.optionalFieldOf("simultaneous_mobs", defaultInstance.simultaneousMobs)
						.forGetter(TrialSpawnerConfig::simultaneousMobs),
					Codec.floatRange(0.0F, Float.MAX_VALUE)
						.optionalFieldOf("total_mobs_added_per_player", defaultInstance.totalMobsAddedPerPlayer)
						.forGetter(TrialSpawnerConfig::totalMobsAddedPerPlayer),
					Codec.floatRange(0.0F, Float.MAX_VALUE)
						.optionalFieldOf("simultaneous_mobs_added_per_player", defaultInstance.simultaneousMobsAddedPerPlayer)
						.forGetter(TrialSpawnerConfig::simultaneousMobsAddedPerPlayer),
					Codec.intRange(0, Integer.MAX_VALUE)
						.optionalFieldOf("ticks_between_spawn", defaultInstance.ticksBetweenSpawn)
						.forGetter(TrialSpawnerConfig::ticksBetweenSpawn),
					Codec.intRange(0, Integer.MAX_VALUE)
						.optionalFieldOf("target_cooldown_length", defaultInstance.targetCooldownLength)
						.forGetter(TrialSpawnerConfig::targetCooldownLength),
					MobSpawnerEntry.DATA_POOL_CODEC.optionalFieldOf("spawn_potentials", DataPool.empty()).forGetter(TrialSpawnerConfig::spawnPotentialsDefinition),
					DataPool.createEmptyAllowedCodec(Identifier.CODEC)
						.optionalFieldOf("loot_tables_to_eject", defaultInstance.lootTablesToEject)
						.forGetter(TrialSpawnerConfig::lootTablesToEject)
				)
				.apply(instance, TrialSpawnerConfig::new)
	);

	public int getTotalMobs(int additionalPlayers) {
		return (int)Math.floor((double)(this.totalMobs + this.totalMobsAddedPerPlayer * (float)additionalPlayers));
	}

	public int getSimultaneousMobs(int additionalPlayers) {
		return (int)Math.floor((double)(this.simultaneousMobs + this.simultaneousMobsAddedPerPlayer * (float)additionalPlayers));
	}
}
