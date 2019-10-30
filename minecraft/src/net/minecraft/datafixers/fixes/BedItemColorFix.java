package net.minecraft.datafixers.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.util.Pair;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.datafixers.TypeReferences;

public class BedItemColorFix extends DataFix {
	public BedItemColorFix(Schema outputSchema, boolean changesType) {
		super(outputSchema, changesType);
	}

	@Override
	public TypeRewriteRule makeRule() {
		OpticFinder<Pair<String, String>> opticFinder = DSL.fieldFinder("id", DSL.named(TypeReferences.ITEM_NAME.typeName(), DSL.namespacedString()));
		return this.fixTypeEverywhereTyped("BedItemColorFix", this.getInputSchema().getType(TypeReferences.ITEM_STACK), typed -> {
			Optional<Pair<String, String>> optional = typed.getOptional(opticFinder);
			if (optional.isPresent() && Objects.equals(((Pair)optional.get()).getSecond(), "minecraft:bed")) {
				Dynamic<?> dynamic = typed.get(DSL.remainderFinder());
				if (dynamic.get("Damage").asInt(0) == 0) {
					return typed.set(DSL.remainderFinder(), dynamic.set("Damage", dynamic.createShort((short)14)));
				}
			}

			return typed;
		});
	}
}
