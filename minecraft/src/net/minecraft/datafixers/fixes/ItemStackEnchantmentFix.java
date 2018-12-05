package net.minecraft.datafixers.fixes;

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
import net.minecraft.datafixers.TypeReferences;

public class ItemStackEnchantmentFix extends DataFix {
	private static final Int2ObjectMap<String> enchants = DataFixUtils.make(new Int2ObjectOpenHashMap<>(), int2ObjectOpenHashMap -> {
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

	public ItemStackEnchantmentFix(Schema schema, boolean bl) {
		super(schema, bl);
	}

	@Override
	protected TypeRewriteRule makeRule() {
		Type<?> type = this.getInputSchema().getType(TypeReferences.ITEM_STACK);
		OpticFinder<?> opticFinder = type.findField("tag");
		return this.fixTypeEverywhereTyped(
			"ItemStackEnchantmentFix", type, typed -> typed.updateTyped(opticFinder, typedx -> typedx.update(DSL.remainderFinder(), this::method_5035))
		);
	}

	private Dynamic<?> method_5035(Dynamic<?> dynamic) {
		Optional<Dynamic<?>> optional = dynamic.get("ench")
			.flatMap(Dynamic::getStream)
			.map(stream -> stream.map(dynamicx -> dynamicx.set("id", dynamicx.createString(enchants.getOrDefault(dynamicx.getInt("id"), "null")))))
			.map(dynamic::createList);
		if (optional.isPresent()) {
			dynamic = dynamic.remove("ench").set("Enchantments", (Dynamic<?>)optional.get());
		}

		return dynamic.update(
			"StoredEnchantments",
			dynamicx -> DataFixUtils.orElse(
					dynamicx.getStream()
						.map(stream -> stream.map(dynamicxx -> dynamicxx.set("id", dynamicxx.createString(enchants.getOrDefault(dynamicxx.getInt("id"), "null")))))
						.map(dynamicx::createList),
					dynamicx
				)
		);
	}
}
