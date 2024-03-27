package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.Dynamic;
import net.minecraft.datafixer.TypeReferences;

public class ItemWrittenBookPagesStrictJsonFix extends DataFix {
	public ItemWrittenBookPagesStrictJsonFix(Schema outputSchema, boolean changesType) {
		super(outputSchema, changesType);
	}

	public Dynamic<?> fixBookPages(Dynamic<?> dynamic) {
		return dynamic.update(
			"pages",
			dynamic2 -> DataFixUtils.orElse(dynamic2.asStreamOpt().map(stream -> stream.map(TextFixes::text)).map(dynamic::createList).result(), dynamic.emptyList())
		);
	}

	@Override
	public TypeRewriteRule makeRule() {
		Type<?> type = this.getInputSchema().getType(TypeReferences.ITEM_STACK);
		OpticFinder<?> opticFinder = type.findField("tag");
		return this.fixTypeEverywhereTyped(
			"ItemWrittenBookPagesStrictJsonFix", type, typed -> typed.updateTyped(opticFinder, typedx -> typedx.update(DSL.remainderFinder(), this::fixBookPages))
		);
	}
}
