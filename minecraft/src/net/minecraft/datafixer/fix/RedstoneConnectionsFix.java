package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import net.minecraft.datafixer.TypeReferences;

public class RedstoneConnectionsFix extends DataFix {
	public RedstoneConnectionsFix(Schema outputSchema) {
		super(outputSchema, false);
	}

	@Override
	protected TypeRewriteRule makeRule() {
		Schema schema = this.getInputSchema();
		return this.fixTypeEverywhereTyped(
			"RedstoneConnectionsFix",
			schema.getType(TypeReferences.BLOCK_STATE),
			blockStateTyped -> blockStateTyped.update(DSL.remainderFinder(), this::updateBlockState)
		);
	}

	private <T> Dynamic<T> updateBlockState(Dynamic<T> blockStateDynamic) {
		boolean bl = blockStateDynamic.get("Name").asString().result().filter("minecraft:redstone_wire"::equals).isPresent();
		return !bl
			? blockStateDynamic
			: blockStateDynamic.update(
				"Properties",
				propertiesDynamic -> {
					String string = propertiesDynamic.get("east").asString("none");
					String string2 = propertiesDynamic.get("west").asString("none");
					String string3 = propertiesDynamic.get("north").asString("none");
					String string4 = propertiesDynamic.get("south").asString("none");
					boolean blx = hasObsoleteValue(string) || hasObsoleteValue(string2);
					boolean bl2 = hasObsoleteValue(string3) || hasObsoleteValue(string4);
					String string5 = !hasObsoleteValue(string) && !bl2 ? "side" : string;
					String string6 = !hasObsoleteValue(string2) && !bl2 ? "side" : string2;
					String string7 = !hasObsoleteValue(string3) && !blx ? "side" : string3;
					String string8 = !hasObsoleteValue(string4) && !blx ? "side" : string4;
					return propertiesDynamic.update("east", eastDynamic -> eastDynamic.createString(string5))
						.update("west", westDynamic -> westDynamic.createString(string6))
						.update("north", northDynamic -> northDynamic.createString(string7))
						.update("south", southDynamic -> southDynamic.createString(string8));
				}
			);
	}

	private static boolean hasObsoleteValue(String value) {
		return !"none".equals(value);
	}
}
