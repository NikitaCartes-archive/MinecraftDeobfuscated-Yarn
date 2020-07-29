/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util;

import com.google.common.collect.ImmutableList;
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
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.regex.Pattern;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextVisitFactory;
import net.minecraft.text.OrderedText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Style;
import net.minecraft.util.JsonHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class Language {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = new Gson();
    private static final Pattern TOKEN_PATTERN = Pattern.compile("%(\\d+\\$)?[\\d.]*[df]");
    private static volatile Language instance = Language.create();

    private static Language create() {
        ImmutableMap.Builder builder = ImmutableMap.builder();
        BiConsumer<String, String> biConsumer = builder::put;
        try (InputStream inputStream = Language.class.getResourceAsStream("/assets/minecraft/lang/en_us.json");){
            Language.load(inputStream, biConsumer);
        } catch (JsonParseException | IOException exception) {
            LOGGER.error("Couldn't read strings from /assets/minecraft/lang/en_us.json", (Throwable)exception);
        }
        final ImmutableMap map = builder.build();
        return new Language(){

            @Override
            public String get(String key) {
                return map.getOrDefault(key, key);
            }

            @Override
            public boolean hasTranslation(String key) {
                return map.containsKey(key);
            }

            @Override
            @Environment(value=EnvType.CLIENT)
            public boolean isRightToLeft() {
                return false;
            }

            @Override
            @Environment(value=EnvType.CLIENT)
            public OrderedText reorder(StringVisitable text) {
                return visitor -> text.visit((style, string) -> TextVisitFactory.visitFormatted(string, style, visitor) ? Optional.empty() : StringVisitable.TERMINATE_VISIT, Style.EMPTY).isPresent();
            }
        };
    }

    public static void load(InputStream inputStream, BiConsumer<String, String> entryConsumer) {
        JsonObject jsonObject = GSON.fromJson((Reader)new InputStreamReader(inputStream, StandardCharsets.UTF_8), JsonObject.class);
        for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
            String string = TOKEN_PATTERN.matcher(JsonHelper.asString(entry.getValue(), entry.getKey())).replaceAll("%$1s");
            entryConsumer.accept(entry.getKey(), string);
        }
    }

    public static Language getInstance() {
        return instance;
    }

    @Environment(value=EnvType.CLIENT)
    public static void setInstance(Language language) {
        instance = language;
    }

    public abstract String get(String var1);

    public abstract boolean hasTranslation(String var1);

    @Environment(value=EnvType.CLIENT)
    public abstract boolean isRightToLeft();

    @Environment(value=EnvType.CLIENT)
    public abstract OrderedText reorder(StringVisitable var1);

    @Environment(value=EnvType.CLIENT)
    public List<OrderedText> reorder(List<StringVisitable> texts) {
        return texts.stream().map(Language.getInstance()::reorder).collect(ImmutableList.toImmutableList());
    }
}

