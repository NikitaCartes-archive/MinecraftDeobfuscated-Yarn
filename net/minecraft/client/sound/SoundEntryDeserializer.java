/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.sound;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.sound.Sound;
import net.minecraft.client.sound.SoundEntry;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.floatprovider.ConstantFloatProvider;
import net.minecraft.util.math.floatprovider.FloatProvider;
import org.apache.commons.lang3.Validate;

@Environment(value=EnvType.CLIENT)
public class SoundEntryDeserializer
implements JsonDeserializer<SoundEntry> {
    private static final FloatProvider field_38801 = ConstantFloatProvider.create(1.0f);

    @Override
    public SoundEntry deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject jsonObject = JsonHelper.asObject(jsonElement, "entry");
        boolean bl = JsonHelper.getBoolean(jsonObject, "replace", false);
        String string = JsonHelper.getString(jsonObject, "subtitle", null);
        List<Sound> list = this.deserializeSounds(jsonObject);
        return new SoundEntry(list, bl, string);
    }

    private List<Sound> deserializeSounds(JsonObject json) {
        ArrayList<Sound> list = Lists.newArrayList();
        if (json.has("sounds")) {
            JsonArray jsonArray = JsonHelper.getArray(json, "sounds");
            for (int i = 0; i < jsonArray.size(); ++i) {
                JsonElement jsonElement = jsonArray.get(i);
                if (JsonHelper.isString(jsonElement)) {
                    String string = JsonHelper.asString(jsonElement, "sound");
                    list.add(new Sound(string, field_38801, field_38801, 1, Sound.RegistrationType.FILE, false, false, 16));
                    continue;
                }
                list.add(this.deserializeSound(JsonHelper.asObject(jsonElement, "sound")));
            }
        }
        return list;
    }

    private Sound deserializeSound(JsonObject json) {
        String string = JsonHelper.getString(json, "name");
        Sound.RegistrationType registrationType = this.deserializeType(json, Sound.RegistrationType.FILE);
        float f = JsonHelper.getFloat(json, "volume", 1.0f);
        Validate.isTrue(f > 0.0f, "Invalid volume", new Object[0]);
        float g = JsonHelper.getFloat(json, "pitch", 1.0f);
        Validate.isTrue(g > 0.0f, "Invalid pitch", new Object[0]);
        int i = JsonHelper.getInt(json, "weight", 1);
        Validate.isTrue(i > 0, "Invalid weight", new Object[0]);
        boolean bl = JsonHelper.getBoolean(json, "preload", false);
        boolean bl2 = JsonHelper.getBoolean(json, "stream", false);
        int j = JsonHelper.getInt(json, "attenuation_distance", 16);
        return new Sound(string, ConstantFloatProvider.create(f), ConstantFloatProvider.create(g), i, registrationType, bl2, bl, j);
    }

    private Sound.RegistrationType deserializeType(JsonObject json, Sound.RegistrationType fallback) {
        Sound.RegistrationType registrationType = fallback;
        if (json.has("type")) {
            registrationType = Sound.RegistrationType.getByName(JsonHelper.getString(json, "type"));
            Validate.notNull(registrationType, "Invalid type", new Object[0]);
        }
        return registrationType;
    }

    @Override
    public /* synthetic */ Object deserialize(JsonElement functionJson, Type unused, JsonDeserializationContext context) throws JsonParseException {
        return this.deserialize(functionJson, unused, context);
    }
}

