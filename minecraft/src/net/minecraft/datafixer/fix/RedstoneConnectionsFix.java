package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import net.minecraft.datafixer.TypeReferences;

public class RedstoneConnectionsFix extends DataFix {
	public RedstoneConnectionsFix(Schema outputSchema) {
		super(outputSchema, false);
	}

	@Override
	protected TypeRewriteRule makeRule() {
		Schema schema = this.getInputSchema();
		return this.fixTypeEverywhereTyped(
			"RedstoneConnectionsFix", schema.getType(TypeReferences.BLOCK_STATE), typed -> typed.update(DSL.remainderFinder(), this::updateBlockState)
		);
	}

	private <T> Dynamic<T> updateBlockState(Dynamic<T> data) {
		boolean bl = data.get("Name").asString().filter("minecraft:redstone_wire"::equals).isPresent();
		return !bl
			? data
			: data.update(
				"Properties",
				dynamic -> {
					String string = (String)dynamic.get("east").asString().orElseGet(() -> "none");
					String string2 = (String)dynamic.get("west").asString().orElseGet(() -> "none");
					String string3 = (String)dynamic.get("north").asString().orElseGet(() -> "none");
					String string4 = (String)dynamic.get("south").asString().orElseGet(() -> "none");
					boolean blx = hasObsoleteValue(string) || hasObsoleteValue(string2);
					boolean bl2 = hasObsoleteValue(string3) || hasObsoleteValue(string4);
					String string5 = !hasObsoleteValue(string) && !bl2 ? "side" : string;
					String string6 = !hasObsoleteValue(string2) && !bl2 ? "side" : string2;
					String string7 = !hasObsoleteValue(string3) && !blx ? "side" : string3;
					String string8 = !hasObsoleteValue(string4) && !blx ? "side" : string4;
					return dynamic.update("east", dynamicx -> dynamicx.createString(string5))
						.update("west", dynamicx -> dynamicx.createString(string6))
						.update("north", dynamicx -> dynamicx.createString(string7))
						.update("south", dynamicx -> dynamicx.createString(string8));
				}
			);
	}

	private static boolean hasObsoleteValue(String string) {
		return !"none".equals(string);
	}
}
