package net.minecraft;

import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType.StringType;

public class class_2332 implements class_2314<StringArgumentType> {
	public void method_10053(StringArgumentType stringArgumentType, class_2540 arg) {
		arg.method_10817(stringArgumentType.getType());
	}

	public StringArgumentType method_10052(class_2540 arg) {
		StringType stringType = arg.method_10818(StringType.class);
		switch (stringType) {
			case SINGLE_WORD:
				return StringArgumentType.word();
			case QUOTABLE_PHRASE:
				return StringArgumentType.string();
			case GREEDY_PHRASE:
			default:
				return StringArgumentType.greedyString();
		}
	}

	public void method_10051(StringArgumentType stringArgumentType, JsonObject jsonObject) {
		switch (stringArgumentType.getType()) {
			case SINGLE_WORD:
				jsonObject.addProperty("type", "word");
				break;
			case QUOTABLE_PHRASE:
				jsonObject.addProperty("type", "phrase");
				break;
			case GREEDY_PHRASE:
			default:
				jsonObject.addProperty("type", "greedy");
		}
	}
}
