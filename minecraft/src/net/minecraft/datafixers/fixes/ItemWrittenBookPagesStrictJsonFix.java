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
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
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
						Component component = null;
						if (!"null".equals(string) && !StringUtils.isEmpty(string)) {
							if (string.charAt(0) == '"' && string.charAt(string.length() - 1) == '"' || string.charAt(0) == '{' && string.charAt(string.length() - 1) == '}') {
								try {
									component = JsonHelper.deserialize(BlockEntitySignTextStrictJsonFix.GSON, string, Component.class, true);
									if (component == null) {
										component = new TextComponent("");
									}
								} catch (JsonParseException var6) {
								}

								if (component == null) {
									try {
										component = Component.Serializer.fromJsonString(string);
									} catch (JsonParseException var5) {
									}
								}

								if (component == null) {
									try {
										component = Component.Serializer.fromLenientJsonString(string);
									} catch (JsonParseException var4) {
									}
								}

								if (component == null) {
									component = new TextComponent(string);
								}
							} else {
								component = new TextComponent(string);
							}
						} else {
							component = new TextComponent("");
						}

						return dynamicxx.createString(Component.Serializer.toJsonString(component));
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
