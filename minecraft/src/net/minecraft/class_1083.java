package net.minecraft;

import com.google.common.collect.Sets;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.util.Set;
import java.util.Map.Entry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_1083 implements class_3270<class_1082> {
	public class_1082 method_4695(JsonObject jsonObject) {
		Set<class_1077> set = Sets.<class_1077>newHashSet();

		for (Entry<String, JsonElement> entry : jsonObject.entrySet()) {
			String string = (String)entry.getKey();
			if (string.length() > 16) {
				throw new JsonParseException("Invalid language->'" + string + "': language code must not be more than " + 16 + " characters long");
			}

			JsonObject jsonObject2 = class_3518.method_15295((JsonElement)entry.getValue(), "language");
			String string2 = class_3518.method_15265(jsonObject2, "region");
			String string3 = class_3518.method_15265(jsonObject2, "name");
			boolean bl = class_3518.method_15258(jsonObject2, "bidirectional", false);
			if (string2.isEmpty()) {
				throw new JsonParseException("Invalid language->'" + string + "'->region: empty value");
			}

			if (string3.isEmpty()) {
				throw new JsonParseException("Invalid language->'" + string + "'->name: empty value");
			}

			if (!set.add(new class_1077(string, string2, string3, bl))) {
				throw new JsonParseException("Duplicate language->'" + string + "' defined");
			}
		}

		return new class_1082(set);
	}

	@Override
	public String method_14420() {
		return "language";
	}
}
