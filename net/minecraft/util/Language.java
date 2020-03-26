/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.regex.Pattern;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Language {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Pattern TOKEN_PATTERN = Pattern.compile("%(\\d+\\$)?[\\d\\.]*[df]");
    private static final Language INSTANCE = new Language();
    private final Map<String, String> translations = Maps.newHashMap();
    private long timeLoaded;

    public Language() {
        try (InputStream inputStream = Language.class.getResourceAsStream("/assets/minecraft/lang/en_us.json");){
            JsonElement jsonElement = new Gson().fromJson((Reader)new InputStreamReader(inputStream, StandardCharsets.UTF_8), JsonElement.class);
            JsonObject jsonObject = JsonHelper.asObject(jsonElement, "strings");
            for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
                String string = TOKEN_PATTERN.matcher(JsonHelper.asString(entry.getValue(), entry.getKey())).replaceAll("%$1s");
                this.translations.put(entry.getKey(), string);
            }
            this.timeLoaded = Util.getMeasuringTimeMs();
        } catch (JsonParseException | IOException exception) {
            LOGGER.error("Couldn't read strings from /assets/minecraft/lang/en_us.json", (Throwable)exception);
        }
    }

    public static Language getInstance() {
        return INSTANCE;
    }

    @Environment(value=EnvType.CLIENT)
    public static synchronized void load(Map<String, String> map) {
        Language.INSTANCE.translations.clear();
        Language.INSTANCE.translations.putAll(map);
        Language.INSTANCE.timeLoaded = Util.getMeasuringTimeMs();
    }

    public synchronized String translate(String key) {
        return this.getTranslation(key);
    }

    private String getTranslation(String key) {
        String string = this.translations.get(key);
        return string == null ? key : string;
    }

    public synchronized boolean hasTranslation(String key) {
        return this.translations.containsKey(key);
    }

    public long getTimeLoaded() {
        return this.timeLoaded;
    }
}

