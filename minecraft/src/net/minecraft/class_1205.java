package net.minecraft;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import java.util.Map;
import java.util.Objects;

public class class_1205 extends DataFix {
	private static final Map<String, String> field_5707 = ImmutableMap.<String, String>builder()
		.put("minecraft:acacia_bark", "minecraft:acacia_wood")
		.put("minecraft:birch_bark", "minecraft:birch_wood")
		.put("minecraft:dark_oak_bark", "minecraft:dark_oak_wood")
		.put("minecraft:jungle_bark", "minecraft:jungle_wood")
		.put("minecraft:oak_bark", "minecraft:oak_wood")
		.put("minecraft:spruce_bark", "minecraft:spruce_wood")
		.build();

	public class_1205(Schema schema, boolean bl) {
		super(schema, bl);
	}

	@Override
	protected TypeRewriteRule makeRule() {
		Type<Pair<String, String>> type = DSL.named(class_1208.field_5711.typeName(), DSL.namespacedString());
		if (!Objects.equals(type, this.getInputSchema().getType(class_1208.field_5711))) {
			throw new IllegalStateException("Recipe type is not what was expected.");
		} else {
			return this.fixTypeEverywhere(
				"Recipes renamening fix", type, dynamicOps -> pair -> pair.mapSecond(string -> (String)field_5707.getOrDefault(string, string))
			);
		}
	}
}
