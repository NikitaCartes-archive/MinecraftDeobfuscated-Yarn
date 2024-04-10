package net.minecraft.block.spawner;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.entity.EquipmentTable;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DataPool;
import net.minecraft.util.dynamic.Range;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.LightType;

public record MobSpawnerEntry(NbtCompound entity, Optional<MobSpawnerEntry.CustomSpawnRules> customSpawnRules, Optional<EquipmentTable> equipment) {
	public static final String ENTITY_KEY = "entity";
	public static final Codec<MobSpawnerEntry> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					NbtCompound.CODEC.fieldOf("entity").forGetter(entry -> entry.entity),
					MobSpawnerEntry.CustomSpawnRules.CODEC.optionalFieldOf("custom_spawn_rules").forGetter(entry -> entry.customSpawnRules),
					EquipmentTable.CODEC.optionalFieldOf("equipment").forGetter(entry -> entry.equipment)
				)
				.apply(instance, MobSpawnerEntry::new)
	);
	public static final Codec<DataPool<MobSpawnerEntry>> DATA_POOL_CODEC = DataPool.createEmptyAllowedCodec(CODEC);

	public MobSpawnerEntry() {
		this(new NbtCompound(), Optional.empty(), Optional.empty());
	}

	public MobSpawnerEntry(NbtCompound entity, Optional<MobSpawnerEntry.CustomSpawnRules> customSpawnRules, Optional<EquipmentTable> equipment) {
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
		this.equipment = equipment;
	}

	public NbtCompound getNbt() {
		return this.entity;
	}

	public Optional<MobSpawnerEntry.CustomSpawnRules> getCustomSpawnRules() {
		return this.customSpawnRules;
	}

	public Optional<EquipmentTable> getEquipment() {
		return this.equipment;
	}

	public static record CustomSpawnRules(Range<Integer> blockLightLimit, Range<Integer> skyLightLimit) {
		private static final Range<Integer> DEFAULT = new Range(0, 15);
		public static final Codec<MobSpawnerEntry.CustomSpawnRules> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
						createLightLimitCodec("block_light_limit").forGetter(rules -> rules.blockLightLimit),
						createLightLimitCodec("sky_light_limit").forGetter(rules -> rules.skyLightLimit)
					)
					.apply(instance, MobSpawnerEntry.CustomSpawnRules::new)
		);

		private static DataResult<Range<Integer>> validate(Range<Integer> provider) {
			return !DEFAULT.contains(provider) ? DataResult.error(() -> "Light values must be withing range " + DEFAULT) : DataResult.success(provider);
		}

		private static MapCodec<Range<Integer>> createLightLimitCodec(String name) {
			return Range.CODEC.lenientOptionalFieldOf(name, DEFAULT).validate(MobSpawnerEntry.CustomSpawnRules::validate);
		}

		public boolean canSpawn(BlockPos pos, ServerWorld world) {
			return this.blockLightLimit.contains(world.getLightLevel(LightType.BLOCK, pos)) && this.skyLightLimit.contains(world.getLightLevel(LightType.SKY, pos));
		}
	}
}
