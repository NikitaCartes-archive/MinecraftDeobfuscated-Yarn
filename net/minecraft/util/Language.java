/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util;

import com.google.common.collect.ImmutableMap;
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
import java.util.function.BiConsumer;
import java.util.regex.Pattern;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.JsonHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class Language {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson field_25307 = new Gson();
    private static final Pattern TOKEN_PATTERN = Pattern.compile("%(\\d+\\$)?[\\d.]*[df]");
    private static volatile Language INSTANCE = Language.method_29429();

    private static Language method_29429() {
        ImmutableMap.Builder builder = ImmutableMap.builder();
        BiConsumer<String, String> biConsumer = builder::put;
        try (InputStream inputStream = Language.class.getResourceAsStream("/assets/minecraft/lang/en_us.json");){
            Language.method_29425(inputStream, biConsumer);
        } catch (JsonParseException | IOException exception) {
            LOGGER.error("Couldn't read strings from /assets/minecraft/lang/en_us.json", (Throwable)exception);
        }
        final ImmutableMap map = builder.build();
        return new Language(){

            @Override
            public String get(String string) {
                return map.getOrDefault(string, string);
            }

            @Override
            public boolean hasTranslation(String key) {
                return map.containsKey(key);
            }

            @Override
            @Environment(value=EnvType.CLIENT)
            public boolean method_29428() {
                return false;
            }

            @Override
            public String method_29426(String string, boolean bl) {
                return string;
            }
        };
    }

    public static void method_29425(InputStream inputStream, BiConsumer<String, String> biConsumer) {
        JsonObject jsonObject = field_25307.fromJson((Reader)new InputStreamReader(inputStream, StandardCharsets.UTF_8), JsonObject.class);
        for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
            String string = TOKEN_PATTERN.matcher(JsonHelper.asString(entry.getValue(), entry.getKey())).replaceAll("%$1s");
            biConsumer.accept(entry.getKey(), string);
        }
    }

    public static Language getInstance() {
        return INSTANCE;
    }

    @Environment(value=EnvType.CLIENT)
    public static void method_29427(Language language) {
        INSTANCE = language;
    }

    public abstract String get(String var1);

    public abstract boolean hasTranslation(String var1);

    @Environment(value=EnvType.CLIENT)
    public abstract boolean method_29428();

    public abstract String method_29426(String var1, boolean var2);
}

