package net.minecraft;

import com.google.gson.JsonParseException;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import org.apache.commons.lang3.StringUtils;

public class class_1194 extends DataFix {
	public class_1194(Schema schema, boolean bl) {
		super(schema, bl);
	}

	public Dynamic<?> method_5085(Dynamic<?> dynamic) {
		return dynamic.update("pages", dynamic2 -> DataFixUtils.orElse(dynamic2.asStreamOpt().map(stream -> stream.map(dynamicxx -> {
					if (!dynamicxx.asString().isPresent()) {
						return dynamicxx;
					} else {
						String string = dynamicxx.asString("");
						class_2561 lv = null;
						if (!"null".equals(string) && !StringUtils.isEmpty(string)) {
							if (string.charAt(0) == '"' && string.charAt(string.length() - 1) == '"' || string.charAt(0) == '{' && string.charAt(string.length() - 1) == '}') {
								try {
									lv = class_3518.method_15279(class_3577.field_15827, string, class_2561.class, true);
									if (lv == null) {
										lv = new class_2585("");
									}
								} catch (JsonParseException var6) {
								}

								if (lv == null) {
									try {
										lv = class_2561.class_2562.method_10877(string);
									} catch (JsonParseException var5) {
									}
								}

								if (lv == null) {
									try {
										lv = class_2561.class_2562.method_10873(string);
									} catch (JsonParseException var4) {
									}
								}

								if (lv == null) {
									lv = new class_2585(string);
								}
							} else {
								lv = new class_2585(string);
							}
						} else {
							lv = new class_2585("");
						}

						return dynamicxx.createString(class_2561.class_2562.method_10867(lv));
					}
				})).map(dynamic::createList), dynamic.emptyList()));
	}

	@Override
	public TypeRewriteRule makeRule() {
		Type<?> type = this.getInputSchema().getType(class_1208.field_5712);
		OpticFinder<?> opticFinder = type.findField("tag");
		return this.fixTypeEverywhereTyped(
			"ItemWrittenBookPagesStrictJsonFix", type, typed -> typed.updateTyped(opticFinder, typedx -> typedx.update(DSL.remainderFinder(), this::method_5085))
		);
	}
}
