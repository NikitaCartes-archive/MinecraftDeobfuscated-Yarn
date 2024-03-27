package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.Dynamic;
import java.util.Optional;
import net.minecraft.datafixer.TypeReferences;

public class ItemCustomNameToComponentFix extends DataFix {
	public ItemCustomNameToComponentFix(Schema outputSchema, boolean changesType) {
		super(outputSchema, changesType);
	}

	private Dynamic<?> fixCustomName(Dynamic<?> dynamic) {
		Optional<? extends Dynamic<?>> optional = dynamic.get("display").result();
		if (optional.isPresent()) {
			Dynamic<?> dynamic2 = (Dynamic<?>)optional.get();
			Optional<String> optional2 = dynamic2.get("Name").asString().result();
			if (optional2.isPresent()) {
				dynamic2 = dynamic2.set("Name", TextFixes.text(dynamic2.getOps(), (String)optional2.get()));
			}

			return dynamic.set("display", dynamic2);
		} else {
			return dynamic;
		}
	}

	@Override
	public TypeRewriteRule makeRule() {
		Type<?> type = this.getInputSchema().getType(TypeReferences.ITEM_STACK);
		OpticFinder<?> opticFinder = type.findField("tag");
		return this.fixTypeEverywhereTyped(
			"ItemCustomNameToComponentFix", type, typed -> typed.updateTyped(opticFinder, typedx -> typedx.update(DSL.remainderFinder(), this::fixCustomName))
		);
	}
}
