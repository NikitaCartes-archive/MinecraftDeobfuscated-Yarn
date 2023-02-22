package net.minecraft.world;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DataPool;
import net.minecraft.util.dynamic.Range;

public record MobSpawnerEntry(NbtCompound entity, Optional<MobSpawnerEntry.CustomSpawnRules> customSpawnRules) {
	public static final String ENTITY_KEY = "entity";
	public static final Codec<MobSpawnerEntry> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					NbtCompound.CODEC.fieldOf("entity").forGetter(entry -> entry.entity),
					MobSpawnerEntry.CustomSpawnRules.CODEC.optionalFieldOf("custom_spawn_rules").forGetter(entry -> entry.customSpawnRules)
				)
				.apply(instance, MobSpawnerEntry::new)
	);
	public static final Codec<DataPool<MobSpawnerEntry>> DATA_POOL_CODEC = DataPool.createEmptyAllowedCodec(CODEC);

	public MobSpawnerEntry() {
		this(new NbtCompound(), Optional.empty());
	}

	public MobSpawnerEntry(NbtCompound entity, Optional<MobSpawnerEntry.CustomSpawnRules> customSpawnRules) {
		if (entity.contains("id")) {
			Identifier identifier = Identifier.tryParse(entity.getString("id"));
			if (identifier != null) {
				entity.putString("id", identifier.toString());
			} else {
				entity.remove("id");
			}
		}

		this.entity = entity;
		this.customSpawnRules = customSpawnRules;
	}

	public NbtCompound getNbt() {
		return this.entity;
	}

	public Optional<MobSpawnerEntry.CustomSpawnRules> getCustomSpawnRules() {
		return this.customSpawnRules;
	}

	public static record CustomSpawnRules(Range<Integer> blockLightLimit, Range<Integer> skyLightLimit) {
		private static final Range<Integer> DEFAULT = new Range(0, 15);
		public static final Codec<MobSpawnerEntry.CustomSpawnRules> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
						Range.CODEC
							.optionalFieldOf("block_light_limit", DEFAULT)
							.flatXmap(MobSpawnerEntry.CustomSpawnRules::validate, MobSpawnerEntry.CustomSpawnRules::validate)
							.forGetter(rules -> rules.blockLightLimit),
						Range.CODEC
							.optionalFieldOf("sky_light_limit", DEFAULT)
							.flatXmap(MobSpawnerEntry.CustomSpawnRules::validate, MobSpawnerEntry.CustomSpawnRules::validate)
							.forGetter(rules -> rules.skyLightLimit)
					)
					.apply(instance, MobSpawnerEntry.CustomSpawnRules::new)
		);

		private static DataResult<Range<Integer>> validate(Range<Integer> provider) {
			return !DEFAULT.contains(provider) ? DataResult.error(() -> "Light values must be withing range " + DEFAULT) : DataResult.success(provider);
		}
	}
}
