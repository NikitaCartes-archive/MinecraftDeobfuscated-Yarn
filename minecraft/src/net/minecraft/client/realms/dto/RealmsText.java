package net.minecraft.client.realms.dto;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Objects;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.util.JsonUtils;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class RealmsText {
	private static final String TRANSLATION_KEY_KEY = "translationKey";
	private static final String ARGS_KEY = "args";
	private final String translationKey;
	@Nullable
	private final String[] args;

	private RealmsText(String translationKey, @Nullable String[] args) {
		this.translationKey = translationKey;
		this.args = args;
	}

	public Text toText(Text fallback) {
		return (Text)Objects.requireNonNullElse(this.toText(), fallback);
	}

	@Nullable
	public Text toText() {
		if (!I18n.hasTranslation(this.translationKey)) {
			return null;
		} else {
			return this.args == null ? Text.translatable(this.translationKey) : Text.translatable(this.translationKey, this.args);
		}
	}

	public static RealmsText fromJson(JsonObject json) {
		String string = JsonUtils.getString("translationKey", json);
		JsonElement jsonElement = json.get("args");
		String[] strings;
		if (jsonElement != null && !jsonElement.isJsonNull()) {
			JsonArray jsonArray = jsonElement.getAsJsonArray();
			strings = new String[jsonArray.size()];

			for (int i = 0; i < jsonArray.size(); i++) {
				strings[i] = jsonArray.get(i).getAsString();
			}
		} else {
			strings = null;
		}

		return new RealmsText(string, strings);
	}

	public String toString() {
		return this.translationKey;
	}
}
