package net.minecraft.datafixers.fixes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import java.lang.reflect.Type;
import net.minecraft.datafixers.TypeReferences;
import net.minecraft.text.StringTextComponent;
import net.minecraft.text.TextComponent;
import net.minecraft.util.JsonHelper;
import org.apache.commons.lang3.StringUtils;

public class BlockEntitySignTextStrictJsonFix extends ChoiceFix {
	public static final Gson GSON = new GsonBuilder().registerTypeAdapter(TextComponent.class, new JsonDeserializer<TextComponent>() {
		public TextComponent method_15583(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
			if (jsonElement.isJsonPrimitive()) {
				return new StringTextComponent(jsonElement.getAsString());
			} else if (jsonElement.isJsonArray()) {
				JsonArray jsonArray = jsonElement.getAsJsonArray();
				TextComponent textComponent = null;

				for (JsonElement jsonElement2 : jsonArray) {
					TextComponent textComponent2 = this.method_15583(jsonElement2, jsonElement2.getClass(), jsonDeserializationContext);
					if (textComponent == null) {
						textComponent = textComponent2;
					} else {
						textComponent.append(textComponent2);
					}
				}

				return textComponent;
			} else {
				throw new JsonParseException("Don't know how to turn " + jsonElement + " into a Component");
			}
		}
	}).create();

	public BlockEntitySignTextStrictJsonFix(Schema schema, boolean bl) {
		super(schema, bl, "BlockEntitySignTextStrictJsonFix", TypeReferences.BLOCK_ENTITY, "Sign");
	}

	private Dynamic<?> method_15582(Dynamic<?> dynamic, String string) {
		String string2 = dynamic.getString(string);
		TextComponent textComponent = null;
		if (!"null".equals(string2) && !StringUtils.isEmpty(string2)) {
			if (string2.charAt(0) == '"' && string2.charAt(string2.length() - 1) == '"' || string2.charAt(0) == '{' && string2.charAt(string2.length() - 1) == '}') {
				try {
					textComponent = JsonHelper.deserialize(GSON, string2, TextComponent.class, true);
					if (textComponent == null) {
						textComponent = new StringTextComponent("");
					}
				} catch (JsonParseException var8) {
				}

				if (textComponent == null) {
					try {
						textComponent = TextComponent.Serializer.fromJsonString(string2);
					} catch (JsonParseException var7) {
					}
				}

				if (textComponent == null) {
					try {
						textComponent = TextComponent.Serializer.fromLenientJsonString(string2);
					} catch (JsonParseException var6) {
					}
				}

				if (textComponent == null) {
					textComponent = new StringTextComponent(string2);
				}
			} else {
				textComponent = new StringTextComponent(string2);
			}
		} else {
			textComponent = new StringTextComponent("");
		}

		return dynamic.set(string, dynamic.createString(TextComponent.Serializer.toJsonString(textComponent)));
	}

	@Override
	protected Typed<?> transform(Typed<?> typed) {
		return typed.update(DSL.remainderFinder(), dynamic -> {
			dynamic = this.method_15582(dynamic, "Text1");
			dynamic = this.method_15582(dynamic, "Text2");
			dynamic = this.method_15582(dynamic, "Text3");
			return this.method_15582(dynamic, "Text4");
		});
	}
}
