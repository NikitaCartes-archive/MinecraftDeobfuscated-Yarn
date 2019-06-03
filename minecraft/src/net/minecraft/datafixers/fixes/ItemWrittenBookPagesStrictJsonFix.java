package net.minecraft.datafixers.fixes;

import com.google.gson.JsonParseException;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import net.minecraft.datafixers.TypeReferences;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.JsonHelper;
import org.apache.commons.lang3.StringUtils;

public class ItemWrittenBookPagesStrictJsonFix extends DataFix {
	public ItemWrittenBookPagesStrictJsonFix(Schema schema, boolean bl) {
		super(schema, bl);
	}

	public Dynamic<?> fixBookPages(Dynamic<?> dynamic) {
		return dynamic.update("pages", dynamic2 -> DataFixUtils.orElse(dynamic2.asStreamOpt().map(stream -> stream.map(dynamicxx -> {
					if (!dynamicxx.asString().isPresent()) {
						return dynamicxx;
					} else {
						String string = dynamicxx.asString("");
						Text text = null;
						if (!"null".equals(string) && !StringUtils.isEmpty(string)) {
							if (string.charAt(0) == '"' && string.charAt(string.length() - 1) == '"' || string.charAt(0) == '{' && string.charAt(string.length() - 1) == '}') {
								try {
									text = JsonHelper.deserialize(BlockEntitySignTextStrictJsonFix.GSON, string, Text.class, true);
									if (text == null) {
										text = new LiteralText("");
									}
								} catch (JsonParseException var6) {
								}

								if (text == null) {
									try {
										text = Text.Serializer.fromJson(string);
									} catch (JsonParseException var5) {
									}
								}

								if (text == null) {
									try {
										text = Text.Serializer.fromLenientJson(string);
									} catch (JsonParseException var4) {
									}
								}

								if (text == null) {
									text = new LiteralText(string);
								}
							} else {
								text = new LiteralText(string);
							}
						} else {
							text = new LiteralText("");
						}

						return dynamicxx.createString(Text.Serializer.toJson(text));
					}
				})).map(dynamic::createList), dynamic.emptyList()));
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
