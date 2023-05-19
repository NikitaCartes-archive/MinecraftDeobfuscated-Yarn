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
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.JsonHelper;
import org.apache.commons.lang3.StringUtils;

public class ItemWrittenBookPagesStrictJsonFix extends DataFix {
	public ItemWrittenBookPagesStrictJsonFix(Schema outputSchema, boolean changesType) {
		super(outputSchema, changesType);
	}

	public Dynamic<?> fixBookPages(Dynamic<?> dynamic) {
		return dynamic.update("pages", dynamic2 -> DataFixUtils.orElse(dynamic2.asStreamOpt().map(stream -> stream.map(dynamicxx -> {
					if (!dynamicxx.asString().result().isPresent()) {
						return dynamicxx;
					} else {
						String string = dynamicxx.asString("");
						Text text = null;
						if (!"null".equals(string) && !StringUtils.isEmpty(string)) {
							if (string.charAt(0) == '"' && string.charAt(string.length() - 1) == '"' || string.charAt(0) == '{' && string.charAt(string.length() - 1) == '}') {
								try {
									text = JsonHelper.deserializeNullable(BlockEntitySignTextStrictJsonFix.GSON, string, Text.class, true);
									if (text == null) {
										text = ScreenTexts.EMPTY;
									}
								} catch (Exception var6) {
								}

								if (text == null) {
									try {
										text = Text.Serializer.fromJson(string);
									} catch (Exception var5) {
									}
								}

								if (text == null) {
									try {
										text = Text.Serializer.fromLenientJson(string);
									} catch (Exception var4) {
									}
								}

								if (text == null) {
									text = Text.literal(string);
								}
							} else {
								text = Text.literal(string);
							}
						} else {
							text = ScreenTexts.EMPTY;
						}

						return dynamicxx.createString(Text.Serializer.toJson(text));
					}
				})).map(dynamic::createList).result(), dynamic.emptyList()));
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
