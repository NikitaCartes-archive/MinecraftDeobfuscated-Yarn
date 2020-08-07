package net.minecraft.datafixer.fix;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.Set;
import net.minecraft.datafixer.TypeReferences;

public class WallPropertyFix extends DataFix {
	private static final Set<String> TARGET_BLOCK_IDS = ImmutableSet.of(
		"minecraft:andesite_wall",
		"minecraft:brick_wall",
		"minecraft:cobblestone_wall",
		"minecraft:diorite_wall",
		"minecraft:end_stone_brick_wall",
		"minecraft:granite_wall",
		"minecraft:mossy_cobblestone_wall",
		"minecraft:mossy_stone_brick_wall",
		"minecraft:nether_brick_wall",
		"minecraft:prismarine_wall",
		"minecraft:red_nether_brick_wall",
		"minecraft:red_sandstone_wall",
		"minecraft:sandstone_wall",
		"minecraft:stone_brick_wall"
	);

	public WallPropertyFix(Schema outputSchema, boolean changesType) {
		super(outputSchema, changesType);
	}

	@Override
	public TypeRewriteRule makeRule() {
		return this.fixTypeEverywhereTyped(
			"WallPropertyFix",
			this.getInputSchema().getType(TypeReferences.BLOCK_STATE),
			typed -> typed.update(DSL.remainderFinder(), WallPropertyFix::updateWallProperties)
		);
	}

	private static String booleanToWallType(String value) {
		return "true".equals(value) ? "low" : "none";
	}

	private static <T> Dynamic<T> updateWallValueReference(Dynamic<T> dynamic, String string) {
		return dynamic.update(
			string, dynamicx -> DataFixUtils.orElse(dynamicx.asString().result().map(WallPropertyFix::booleanToWallType).map(dynamicx::createString), dynamicx)
		);
	}

	private static <T> Dynamic<T> updateWallProperties(Dynamic<T> dynamic) {
		boolean bl = dynamic.get("Name").asString().result().filter(TARGET_BLOCK_IDS::contains).isPresent();
		return !bl ? dynamic : dynamic.update("Properties", dynamicx -> {
			Dynamic<?> dynamic2 = updateWallValueReference(dynamicx, "east");
			dynamic2 = updateWallValueReference(dynamic2, "west");
			dynamic2 = updateWallValueReference(dynamic2, "north");
			return updateWallValueReference(dynamic2, "south");
		});
	}
}
