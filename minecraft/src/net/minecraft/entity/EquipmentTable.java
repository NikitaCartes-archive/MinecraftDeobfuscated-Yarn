package net.minecraft.entity;

import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import net.minecraft.loot.LootTable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;

public record EquipmentTable(RegistryKey<LootTable> lootTable, Map<EquipmentSlot, Float> slotDropChances) {
	public static final Codec<Map<EquipmentSlot, Float>> SLOT_DROP_CHANCES_CODEC = Codec.either(Codec.FLOAT, Codec.unboundedMap(EquipmentSlot.CODEC, Codec.FLOAT))
		.xmap(either -> either.map(EquipmentTable::createSlotDropChances, Function.identity()), map -> {
			boolean bl = map.values().stream().distinct().count() == 1L;
			boolean bl2 = map.keySet().containsAll(Arrays.asList(EquipmentSlot.values()));
			return bl && bl2 ? Either.left((Float)map.values().stream().findFirst().orElse(0.0F)) : Either.right(map);
		});
	public static final Codec<EquipmentTable> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					RegistryKey.createCodec(RegistryKeys.LOOT_TABLE).fieldOf("loot_table").forGetter(EquipmentTable::lootTable),
					SLOT_DROP_CHANCES_CODEC.optionalFieldOf("slot_drop_chances", Map.of()).forGetter(EquipmentTable::slotDropChances)
				)
				.apply(instance, EquipmentTable::new)
	);

	private static Map<EquipmentSlot, Float> createSlotDropChances(float dropChance) {
		return createSlotDropChances(List.of(EquipmentSlot.values()), dropChance);
	}

	private static Map<EquipmentSlot, Float> createSlotDropChances(List<EquipmentSlot> slots, float dropChance) {
		Map<EquipmentSlot, Float> map = Maps.<EquipmentSlot, Float>newHashMap();

		for (EquipmentSlot equipmentSlot : slots) {
			map.put(equipmentSlot, dropChance);
		}

		return map;
	}
}
