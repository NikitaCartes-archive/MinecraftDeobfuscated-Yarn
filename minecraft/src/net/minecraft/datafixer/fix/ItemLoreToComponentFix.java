package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import java.util.stream.Stream;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

public class ItemLoreToComponentFix extends DataFix {
	public ItemLoreToComponentFix(Schema outputSchema, boolean changesType) {
		super(outputSchema, changesType);
	}

	@Override
	protected TypeRewriteRule makeRule() {
		Type<?> type = this.getInputSchema().getType(TypeReferences.ITEM_STACK);
		OpticFinder<?> opticFinder = type.findField("tag");
		return this.fixTypeEverywhereTyped(
			"Item Lore componentize",
			type,
			typed -> typed.updateTyped(
					opticFinder,
					typedx -> typedx.update(
							DSL.remainderFinder(),
							dynamic -> dynamic.update(
									"display",
									dynamicx -> dynamicx.update(
											"Lore", dynamicxx -> DataFixUtils.orElse(dynamicxx.asStreamOpt().map(ItemLoreToComponentFix::fixLoreTags).map(dynamicxx::createList), dynamicxx)
										)
								)
						)
				)
		);
	}

	private static <T> Stream<Dynamic<T>> fixLoreTags(Stream<Dynamic<T>> tags) {
		return tags.map(dynamic -> DataFixUtils.orElse(dynamic.asString().map(ItemLoreToComponentFix::componentize).map(dynamic::createString), dynamic));
	}

	private static String componentize(String string) {
		return Text.Serializer.toJson(new LiteralText(string));
	}
}
