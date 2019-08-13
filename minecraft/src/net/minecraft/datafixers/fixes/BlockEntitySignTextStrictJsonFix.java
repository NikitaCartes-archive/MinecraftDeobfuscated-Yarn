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
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.JsonHelper;
import org.apache.commons.lang3.StringUtils;

public class BlockEntitySignTextStrictJsonFix extends ChoiceFix {
	public static final Gson GSON = new GsonBuilder().registerTypeAdapter(Text.class, new JsonDeserializer<Text>() {
		public Text method_15583(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
			if (jsonElement.isJsonPrimitive()) {
				return new LiteralText(jsonElement.getAsString());
			} else if (jsonElement.isJsonArray()) {
				JsonArray jsonArray = jsonElement.getAsJsonArray();
				Text text = null;

				for (JsonElement jsonElement2 : jsonArray) {
					Text text2 = this.method_15583(jsonElement2, jsonElement2.getClass(), jsonDeserializationContext);
					if (text == null) {
						text = text2;
					} else {
						text.append(text2);
					}
				}

				return text;
			} else {
				throw new JsonParseException("Don't know how to turn " + jsonElement + " into a Component");
			}
		}
	}).create();

	public BlockEntitySignTextStrictJsonFix(Schema schema, boolean bl) {
		super(schema, bl, "BlockEntitySignTextStrictJsonFix", TypeReferences.BLOCK_ENTITY, "Sign");
	}

	private Dynamic<?> fix(Dynamic<?> dynamic, String string) {
		String string2 = dynamic.get(string).asString("");
		Text text = null;
		if (!"null".equals(string2) && !StringUtils.isEmpty(string2)) {
			if (string2.charAt(0) == '"' && string2.charAt(string2.length() - 1) == '"' || string2.charAt(0) == '{' && string2.charAt(string2.length() - 1) == '}') {
				try {
					text = JsonHelper.deserialize(GSON, string2, Text.class, true);
					if (text == null) {
						text = new LiteralText("");
					}
				} catch (JsonParseException var8) {
				}

				if (text == null) {
					try {
						text = Text.Serializer.fromJson(string2);
					} catch (JsonParseException var7) {
					}
				}

				if (text == null) {
					try {
						text = Text.Serializer.fromLenientJson(string2);
					} catch (JsonParseException var6) {
					}
				}

				if (text == null) {
					text = new LiteralText(string2);
				}
			} else {
				text = new LiteralText(string2);
			}
		} else {
			text = new LiteralText("");
		}

		return dynamic.set(string, dynamic.createString(Text.Serializer.toJson(text)));
	}

	@Override
	protected Typed<?> transform(Typed<?> typed) {
		return typed.update(DSL.remainderFinder(), dynamic -> {
			dynamic = this.fix(dynamic, "Text1");
			dynamic = this.fix(dynamic, "Text2");
			dynamic = this.fix(dynamic, "Text3");
			return this.fix(dynamic, "Text4");
		});
	}
}
