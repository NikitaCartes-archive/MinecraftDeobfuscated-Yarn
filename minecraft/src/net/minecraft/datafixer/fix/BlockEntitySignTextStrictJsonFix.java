package net.minecraft.datafixer.fix;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.lang.reflect.Type;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.JsonHelper;
import org.apache.commons.lang3.StringUtils;

public class BlockEntitySignTextStrictJsonFix extends ChoiceFix {
	public static final Gson GSON = new GsonBuilder().registerTypeAdapter(Text.class, new JsonDeserializer<Text>() {
		public MutableText deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
			if (jsonElement.isJsonPrimitive()) {
				return new LiteralText(jsonElement.getAsString());
			} else if (jsonElement.isJsonArray()) {
				JsonArray jsonArray = jsonElement.getAsJsonArray();
				MutableText mutableText = null;

				for (JsonElement jsonElement2 : jsonArray) {
					MutableText mutableText2 = this.deserialize(jsonElement2, jsonElement2.getClass(), jsonDeserializationContext);
					if (mutableText == null) {
						mutableText = mutableText2;
					} else {
						mutableText.append(mutableText2);
					}
				}

				return mutableText;
			} else {
				throw new JsonParseException("Don't know how to turn " + jsonElement + " into a Component");
			}
		}
	}).create();

	public BlockEntitySignTextStrictJsonFix(Schema outputSchema, boolean changesType) {
		super(outputSchema, changesType, "BlockEntitySignTextStrictJsonFix", TypeReferences.BLOCK_ENTITY, "Sign");
	}

	private Dynamic<?> fix(Dynamic<?> dynamic, String lineName) {
		String string = dynamic.get(lineName).asString("");
		Text text = null;
		if (!"null".equals(string) && !StringUtils.isEmpty(string)) {
			if (string.charAt(0) == '"' && string.charAt(string.length() - 1) == '"' || string.charAt(0) == '{' && string.charAt(string.length() - 1) == '}') {
				try {
					text = JsonHelper.deserialize(GSON, string, Text.class, true);
					if (text == null) {
						text = LiteralText.EMPTY;
					}
				} catch (JsonParseException var8) {
				}

				if (text == null) {
					try {
						text = Text.Serializer.fromJson(string);
					} catch (JsonParseException var7) {
					}
				}

				if (text == null) {
					try {
						text = Text.Serializer.fromLenientJson(string);
					} catch (JsonParseException var6) {
					}
				}

				if (text == null) {
					text = new LiteralText(string);
				}
			} else {
				text = new LiteralText(string);
			}
		} else {
			text = LiteralText.EMPTY;
		}

		return dynamic.set(lineName, dynamic.createString(Text.Serializer.toJson(text)));
	}

	@Override
	protected Typed<?> transform(Typed<?> inputType) {
		return inputType.update(DSL.remainderFinder(), dynamic -> {
			dynamic = this.fix(dynamic, "Text1");
			dynamic = this.fix(dynamic, "Text2");
			dynamic = this.fix(dynamic, "Text3");
			return this.fix(dynamic, "Text4");
		});
	}
}
