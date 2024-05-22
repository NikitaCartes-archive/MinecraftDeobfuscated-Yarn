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
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.floatprovider.ConstantFloatProvider;
import net.minecraft.util.math.floatprovider.FloatProvider;
import org.apache.commons.lang3.Validate;

@Environment(EnvType.CLIENT)
public class SoundEntryDeserializer implements JsonDeserializer<SoundEntry> {
	private static final FloatProvider ONE = ConstantFloatProvider.create(1.0F);

	public SoundEntry deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
		JsonObject jsonObject = JsonHelper.asObject(jsonElement, "entry");
		boolean bl = JsonHelper.getBoolean(jsonObject, "replace", false);
		String string = JsonHelper.getString(jsonObject, "subtitle", null);
		List<Sound> list = this.deserializeSounds(jsonObject);
		return new SoundEntry(list, bl, string);
	}

	private List<Sound> deserializeSounds(JsonObject json) {
		List<Sound> list = Lists.<Sound>newArrayList();
		if (json.has("sounds")) {
			JsonArray jsonArray = JsonHelper.getArray(json, "sounds");

			for (int i = 0; i < jsonArray.size(); i++) {
				JsonElement jsonElement = jsonArray.get(i);
				if (JsonHelper.isString(jsonElement)) {
					Identifier identifier = Identifier.of(JsonHelper.asString(jsonElement, "sound"));
					list.add(new Sound(identifier, ONE, ONE, 1, Sound.RegistrationType.FILE, false, false, 16));
				} else {
					list.add(this.deserializeSound(JsonHelper.asObject(jsonElement, "sound")));
				}
			}
		}

		return list;
	}

	private Sound deserializeSound(JsonObject json) {
		Identifier identifier = Identifier.of(JsonHelper.getString(json, "name"));
		Sound.RegistrationType registrationType = this.deserializeType(json, Sound.RegistrationType.FILE);
		float f = JsonHelper.getFloat(json, "volume", 1.0F);
		Validate.isTrue(f > 0.0F, "Invalid volume");
		float g = JsonHelper.getFloat(json, "pitch", 1.0F);
		Validate.isTrue(g > 0.0F, "Invalid pitch");
		int i = JsonHelper.getInt(json, "weight", 1);
		Validate.isTrue(i > 0, "Invalid weight");
		boolean bl = JsonHelper.getBoolean(json, "preload", false);
		boolean bl2 = JsonHelper.getBoolean(json, "stream", false);
		int j = JsonHelper.getInt(json, "attenuation_distance", 16);
		return new Sound(identifier, ConstantFloatProvider.create(f), ConstantFloatProvider.create(g), i, registrationType, bl2, bl, j);
	}

	private Sound.RegistrationType deserializeType(JsonObject json, Sound.RegistrationType fallback) {
		Sound.RegistrationType registrationType = fallback;
		if (json.has("type")) {
			registrationType = Sound.RegistrationType.getByName(JsonHelper.getString(json, "type"));
			Validate.notNull(registrationType, "Invalid type");
		}

		return registrationType;
	}
}
