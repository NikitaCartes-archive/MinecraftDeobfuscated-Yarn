package net.minecraft.world;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.lang.runtime.ObjectMethods;
import java.util.Optional;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.collection.DataPool;
import net.minecraft.util.dynamic.Range;

public final class MobSpawnerEntry extends Record {
	private final NbtCompound entity;
	private final Optional<MobSpawnerEntry.CustomSpawnRules> customSpawnRules;
	public static final Codec<MobSpawnerEntry> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					NbtCompound.CODEC.fieldOf("entity").forGetter(entry -> entry.entity),
					MobSpawnerEntry.CustomSpawnRules.CODEC.optionalFieldOf("custom_spawn_rules").forGetter(mobSpawnerEntry -> mobSpawnerEntry.customSpawnRules)
				)
				.apply(instance, MobSpawnerEntry::new)
	);
	public static final Codec<DataPool<MobSpawnerEntry>> DATA_POOL_CODEC = DataPool.method_39521(CODEC);
	public static final String DEFAULT_ENTITY_ID = "minecraft:pig";

	public MobSpawnerEntry() {
		this(Util.make(new NbtCompound(), nbt -> nbt.putString("id", "minecraft:pig")), Optional.empty());
	}

	public MobSpawnerEntry(NbtCompound nbtCompound, Optional<MobSpawnerEntry.CustomSpawnRules> optional) {
		Identifier identifier = Identifier.tryParse(nbtCompound.getString("id"));
		nbtCompound.putString("id", identifier != null ? identifier.toString() : "minecraft:pig");
		this.entity = nbtCompound;
		this.customSpawnRules = optional;
	}

	public NbtCompound getNbt() {
		return this.entity;
	}

	public Optional<MobSpawnerEntry.CustomSpawnRules> getCustomSpawnRules() {
		return this.customSpawnRules;
	}

	public final String toString() {
		return ObjectMethods.bootstrap<"toString",MobSpawnerEntry,"entityToSpawn;customSpawnRules",MobSpawnerEntry::entity,MobSpawnerEntry::customSpawnRules>(this);
	}

	public final int hashCode() {
		return ObjectMethods.bootstrap<"hashCode",MobSpawnerEntry,"entityToSpawn;customSpawnRules",MobSpawnerEntry::entity,MobSpawnerEntry::customSpawnRules>(this);
	}

	public final boolean equals(Object o) {
		return ObjectMethods.bootstrap<"equals",MobSpawnerEntry,"entityToSpawn;customSpawnRules",MobSpawnerEntry::entity,MobSpawnerEntry::customSpawnRules>(this, o);
	}

	public NbtCompound entity() {
		return this.entity;
	}

	public Optional<MobSpawnerEntry.CustomSpawnRules> customSpawnRules() {
		return this.customSpawnRules;
	}

	public static final class CustomSpawnRules extends Record {
		private final Range<Integer> blockLightLimit;
		private final Range<Integer> skyLightLimit;
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

		public CustomSpawnRules(Range<Integer> range, Range<Integer> range2) {
			this.blockLightLimit = range;
			this.skyLightLimit = range2;
		}

		private static DataResult<Range<Integer>> validate(Range<Integer> provider) {
			return !DEFAULT.contains(provider) ? DataResult.error("Light values must be withing range " + DEFAULT) : DataResult.success(provider);
		}

		public final String toString() {
			return ObjectMethods.bootstrap<"toString",MobSpawnerEntry.CustomSpawnRules,"blockLightLimit;skyLightLimit",MobSpawnerEntry.CustomSpawnRules::blockLightLimit,MobSpawnerEntry.CustomSpawnRules::skyLightLimit>(
				this
			);
		}

		public final int hashCode() {
			return ObjectMethods.bootstrap<"hashCode",MobSpawnerEntry.CustomSpawnRules,"blockLightLimit;skyLightLimit",MobSpawnerEntry.CustomSpawnRules::blockLightLimit,MobSpawnerEntry.CustomSpawnRules::skyLightLimit>(
				this
			);
		}

		public final boolean equals(Object o) {
			return ObjectMethods.bootstrap<"equals",MobSpawnerEntry.CustomSpawnRules,"blockLightLimit;skyLightLimit",MobSpawnerEntry.CustomSpawnRules::blockLightLimit,MobSpawnerEntry.CustomSpawnRules::skyLightLimit>(
				this, o
			);
		}

		public Range<Integer> blockLightLimit() {
			return this.blockLightLimit;
		}

		public Range<Integer> skyLightLimit() {
			return this.skyLightLimit;
		}
	}
}
