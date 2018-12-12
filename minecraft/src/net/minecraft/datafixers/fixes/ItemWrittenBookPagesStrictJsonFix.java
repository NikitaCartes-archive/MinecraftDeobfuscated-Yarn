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
import net.minecraft.text.StringTextComponent;
import net.minecraft.text.TextComponent;
import net.minecraft.util.JsonHelper;
import org.apache.commons.lang3.StringUtils;

public class ItemWrittenBookPagesStrictJsonFix extends DataFix {
	public ItemWrittenBookPagesStrictJsonFix(Schema schema, boolean bl) {
		super(schema, bl);
	}

	public Dynamic<?> method_5085(Dynamic<?> dynamic) {
		return dynamic.update("pages", dynamic2 -> DataFixUtils.orElse(dynamic2.getStream().map(stream -> stream.map(dynamicxx -> {
					if (!dynamicxx.getStringValue().isPresent()) {
						return dynamicxx;
					} else {
						String string = (String)dynamicxx.getStringValue().get();
						TextComponent textComponent = null;
						if (!"null".equals(string) && !StringUtils.isEmpty(string)) {
							if (string.charAt(0) == '"' && string.charAt(string.length() - 1) == '"' || string.charAt(0) == '{' && string.charAt(string.length() - 1) == '}') {
								try {
									textComponent = JsonHelper.deserialize(BlockEntitySignTextStrictJsonFix.GSON, string, TextComponent.class, true);
									if (textComponent == null) {
										textComponent = new StringTextComponent("");
									}
								} catch (JsonParseException var6) {
								}

								if (textComponent == null) {
									try {
										textComponent = TextComponent.Serializer.fromJsonString(string);
									} catch (JsonParseException var5) {
									}
								}

								if (textComponent == null) {
									try {
										textComponent = TextComponent.Serializer.fromLenientJsonString(string);
									} catch (JsonParseException var4) {
									}
								}

								if (textComponent == null) {
									textComponent = new StringTextComponent(string);
								}
							} else {
								textComponent = new StringTextComponent(string);
							}
						} else {
							textComponent = new StringTextComponent("");
						}

						return dynamicxx.createString(TextComponent.Serializer.toJsonString(textComponent));
					}
				})).map(dynamic::createList), dynamic.emptyList()));
	}

	@Override
	public TypeRewriteRule makeRule() {
		Type<?> type = this.getInputSchema().getType(TypeReferences.ITEM_STACK);
		OpticFinder<?> opticFinder = type.findField("tag");
		return this.fixTypeEverywhereTyped(
			"ItemWrittenBookPagesStrictJsonFix", type, typed -> typed.updateTyped(opticFinder, typedx -> typedx.update(DSL.remainderFinder(), this::method_5085))
		);
	}
}
