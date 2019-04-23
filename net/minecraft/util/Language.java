/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.regex.Pattern;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.SystemUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Language {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Pattern field_11489 = Pattern.compile("%(\\d+\\$)?[\\d\\.]*[df]");
    private static final Language INSTANCE = new Language();
    private final Map<String, String> translations = Maps.newHashMap();
    private long timeLoaded;

    public Language() {
        try {
            InputStream inputStream = Language.class.getResourceAsStream("/assets/minecraft/lang/en_us.json");
            JsonElement jsonElement = new Gson().fromJson((Reader)new InputStreamReader(inputStream, StandardCharsets.UTF_8), JsonElement.class);
            JsonObject jsonObject = JsonHelper.asObject(jsonElement, "strings");
            for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
                String string = field_11489.matcher(JsonHelper.asString(entry.getValue(), entry.getKey())).replaceAll("%$1s");
                this.translations.put(entry.getKey(), string);
            }
            this.timeLoaded = SystemUtil.getMeasuringTimeMs();
        } catch (JsonParseException jsonParseException) {
            LOGGER.error("Couldn't read strings from /assets/minecraft/lang/en_us.json", (Throwable)jsonParseException);
        }
    }

    public static Language getInstance() {
        return INSTANCE;
    }

    @Environment(value=EnvType.CLIENT)
    public static synchronized void load(Map<String, String> map) {
        Language.INSTANCE.translations.clear();
        Language.INSTANCE.translations.putAll(map);
        Language.INSTANCE.timeLoaded = SystemUtil.getMeasuringTimeMs();
    }

    public synchronized String translate(String string) {
        return this.getTranslation(string);
    }

    private String getTranslation(String string) {
        String string2 = this.translations.get(string);
        return string2 == null ? string : string2;
    }

    public synchronized boolean hasTranslation(String string) {
        return this.translations.containsKey(string);
    }

    public long getTimeLoaded() {
        return this.timeLoaded;
    }
}

