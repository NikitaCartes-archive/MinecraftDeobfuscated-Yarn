package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.Dynamic;
import java.util.stream.Stream;
import net.minecraft.datafixer.TypeReferences;

public class ItemLoreToTextFix extends DataFix {
	public ItemLoreToTextFix(Schema outputSchema, boolean changesType) {
		super(outputSchema, changesType);
	}

	@Override
	protected TypeRewriteRule makeRule() {
		Type<?> type = this.getInputSchema().getType(TypeReferences.ITEM_STACK);
		OpticFinder<?> opticFinder = type.findField("tag");
		return this.fixTypeEverywhereTyped(
			"Item Lore componentize",
			type,
			itemStackTyped -> itemStackTyped.updateTyped(
					opticFinder,
					tagTyped -> tagTyped.update(
							DSL.remainderFinder(),
							tagDynamic -> tagDynamic.update(
									"display",
									displaySubtag -> displaySubtag.update(
											"Lore", lore -> DataFixUtils.orElse(lore.asStreamOpt().map(ItemLoreToTextFix::fixLoreNbt).map(lore::createList).result(), lore)
										)
								)
						)
				)
		);
	}

	private static <T> Stream<Dynamic<T>> fixLoreNbt(Stream<Dynamic<T>> nbt) {
		return nbt.map(TextFixes::fixText);
	}
}
