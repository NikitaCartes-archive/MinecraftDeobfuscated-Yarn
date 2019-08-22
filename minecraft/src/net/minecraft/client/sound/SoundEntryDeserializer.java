package net.minecraft.client.sound;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.JsonHelper;
import org.apache.commons.lang3.Validate;

@Environment(EnvType.CLIENT)
public class SoundEntryDeserializer implements JsonDeserializer<SoundEntry> {
	public SoundEntry method_4791(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
		JsonObject jsonObject = JsonHelper.asObject(jsonElement, "entry");
		boolean bl = JsonHelper.getBoolean(jsonObject, "replace", false);
		String string = JsonHelper.getString(jsonObject, "subtitle", null);
		List<Sound> list = this.deserializeSounds(jsonObject);
		return new SoundEntry(list, bl, string);
	}

	private List<Sound> deserializeSounds(JsonObject jsonObject) {
		List<Sound> list = Lists.<Sound>newArrayList();
		if (jsonObject.has("sounds")) {
			JsonArray jsonArray = JsonHelper.getArray(jsonObject, "sounds");

			for (int i = 0; i < jsonArray.size(); i++) {
				JsonElement jsonElement = jsonArray.get(i);
				if (JsonHelper.isString(jsonElement)) {
					String string = JsonHelper.asString(jsonElement, "sound");
					list.add(new Sound(string, 1.0F, 1.0F, 1, Sound.RegistrationType.FILE, false, false, 16));
				} else {
					list.add(this.deserializeSound(JsonHelper.asObject(jsonElement, "sound")));
				}
			}
		}

		return list;
	}

	private Sound deserializeSound(JsonObject jsonObject) {
		String string = JsonHelper.getString(jsonObject, "name");
		Sound.RegistrationType registrationType = this.deserializeType(jsonObject, Sound.RegistrationType.FILE);
		float f = JsonHelper.getFloat(jsonObject, "volume", 1.0F);
		Validate.isTrue(f > 0.0F, "Invalid volume");
		float g = JsonHelper.getFloat(jsonObject, "pitch", 1.0F);
		Validate.isTrue(g > 0.0F, "Invalid pitch");
		int i = JsonHelper.getInt(jsonObject, "weight", 1);
		Validate.isTrue(i > 0, "Invalid weight");
		boolean bl = JsonHelper.getBoolean(jsonObject, "preload", false);
		boolean bl2 = JsonHelper.getBoolean(jsonObject, "stream", false);
		int j = JsonHelper.getInt(jsonObject, "attenuation_distance", 16);
		return new Sound(string, f, g, i, registrationType, bl2, bl, j);
	}

	private Sound.RegistrationType deserializeType(JsonObject jsonObject, Sound.RegistrationType registrationType) {
		Sound.RegistrationType registrationType2 = registrationType;
		if (jsonObject.has("type")) {
			registrationType2 = Sound.RegistrationType.getByName(JsonHelper.getString(jsonObject, "type"));
			Validate.notNull(registrationType2, "Invalid type");
		}

		return registrationType2;
	}
}
