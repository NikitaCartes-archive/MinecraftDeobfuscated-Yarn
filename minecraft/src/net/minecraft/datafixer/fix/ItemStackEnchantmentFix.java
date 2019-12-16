package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.Optional;
import net.minecraft.datafixer.TypeReferences;

public class ItemStackEnchantmentFix extends DataFix {
	private static final Int2ObjectMap<String> ID_TO_ENCHANTMENTS_MAP = DataFixUtils.make(new Int2ObjectOpenHashMap<>(), int2ObjectOpenHashMap -> {
		int2ObjectOpenHashMap.put(0, "minecraft:protection");
		int2ObjectOpenHashMap.put(1, "minecraft:fire_protection");
		int2ObjectOpenHashMap.put(2, "minecraft:feather_falling");
		int2ObjectOpenHashMap.put(3, "minecraft:blast_protection");
		int2ObjectOpenHashMap.put(4, "minecraft:projectile_protection");
		int2ObjectOpenHashMap.put(5, "minecraft:respiration");
		int2ObjectOpenHashMap.put(6, "minecraft:aqua_affinity");
		int2ObjectOpenHashMap.put(7, "minecraft:thorns");
		int2ObjectOpenHashMap.put(8, "minecraft:depth_strider");
		int2ObjectOpenHashMap.put(9, "minecraft:frost_walker");
		int2ObjectOpenHashMap.put(10, "minecraft:binding_curse");
		int2ObjectOpenHashMap.put(16, "minecraft:sharpness");
		int2ObjectOpenHashMap.put(17, "minecraft:smite");
		int2ObjectOpenHashMap.put(18, "minecraft:bane_of_arthropods");
		int2ObjectOpenHashMap.put(19, "minecraft:knockback");
		int2ObjectOpenHashMap.put(20, "minecraft:fire_aspect");
		int2ObjectOpenHashMap.put(21, "minecraft:looting");
		int2ObjectOpenHashMap.put(22, "minecraft:sweeping");
		int2ObjectOpenHashMap.put(32, "minecraft:efficiency");
		int2ObjectOpenHashMap.put(33, "minecraft:silk_touch");
		int2ObjectOpenHashMap.put(34, "minecraft:unbreaking");
		int2ObjectOpenHashMap.put(35, "minecraft:fortune");
		int2ObjectOpenHashMap.put(48, "minecraft:power");
		int2ObjectOpenHashMap.put(49, "minecraft:punch");
		int2ObjectOpenHashMap.put(50, "minecraft:flame");
		int2ObjectOpenHashMap.put(51, "minecraft:infinity");
		int2ObjectOpenHashMap.put(61, "minecraft:luck_of_the_sea");
		int2ObjectOpenHashMap.put(62, "minecraft:lure");
		int2ObjectOpenHashMap.put(65, "minecraft:loyalty");
		int2ObjectOpenHashMap.put(66, "minecraft:impaling");
		int2ObjectOpenHashMap.put(67, "minecraft:riptide");
		int2ObjectOpenHashMap.put(68, "minecraft:channeling");
		int2ObjectOpenHashMap.put(70, "minecraft:mending");
		int2ObjectOpenHashMap.put(71, "minecraft:vanishing_curse");
	});

	public ItemStackEnchantmentFix(Schema outputSchema, boolean changesType) {
		super(outputSchema, changesType);
	}

	@Override
	protected TypeRewriteRule makeRule() {
		Type<?> type = this.getInputSchema().getType(TypeReferences.ITEM_STACK);
		OpticFinder<?> opticFinder = type.findField("tag");
		return this.fixTypeEverywhereTyped(
			"ItemStackEnchantmentFix", type, typed -> typed.updateTyped(opticFinder, typedx -> typedx.update(DSL.remainderFinder(), this::fixEnchantments))
		);
	}

	private Dynamic<?> fixEnchantments(Dynamic<?> tag) {
		Optional<Dynamic<?>> optional = tag.get("ench")
			.asStreamOpt()
			.map(stream -> stream.map(dynamic -> dynamic.set("id", dynamic.createString(ID_TO_ENCHANTMENTS_MAP.getOrDefault(dynamic.get("id").asInt(0), "null")))))
			.map(tag::createList);
		if (optional.isPresent()) {
			tag = tag.remove("ench").set("Enchantments", (Dynamic<?>)optional.get());
		}

		return tag.update(
			"StoredEnchantments",
			dynamic -> DataFixUtils.orElse(
					dynamic.asStreamOpt()
						.map(
							stream -> stream.map(dynamicx -> dynamicx.set("id", dynamicx.createString(ID_TO_ENCHANTMENTS_MAP.getOrDefault(dynamicx.get("id").asInt(0), "null"))))
						)
						.map(dynamic::createList),
					dynamic
				)
		);
	}
}
