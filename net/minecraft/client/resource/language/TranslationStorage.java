/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.resource.language;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.IllegalFormatException;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(value=EnvType.CLIENT)
public class TranslationStorage {
    private static final Gson GSON = new Gson();
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Pattern PARAM_PATTERN = Pattern.compile("%(\\d+\\$)?[\\d\\.]*[df]");
    protected final Map<String, String> translations = Maps.newHashMap();

    public synchronized void load(ResourceManager container, List<String> list) {
        this.translations.clear();
        for (String string : list) {
            String string2 = String.format("lang/%s.json", string);
            for (String string3 : container.getAllNamespaces()) {
                try {
                    Identifier identifier = new Identifier(string3, string2);
                    this.load(container.getAllResources(identifier));
                } catch (FileNotFoundException identifier) {
                } catch (Exception exception) {
                    LOGGER.warn("Skipped language file: {}:{} ({})", (Object)string3, (Object)string2, (Object)exception.toString());
                }
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void load(List<Resource> list) {
        for (Resource resource : list) {
            InputStream inputStream = resource.getInputStream();
            try {
                this.load(inputStream);
            } finally {
                IOUtils.closeQuietly(inputStream);
            }
        }
    }

    private void load(InputStream inputStream) {
        JsonElement jsonElement = GSON.fromJson((Reader)new InputStreamReader(inputStream, StandardCharsets.UTF_8), JsonElement.class);
        JsonObject jsonObject = JsonHelper.asObject(jsonElement, "strings");
        for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
            String string = PARAM_PATTERN.matcher(JsonHelper.asString(entry.getValue(), entry.getKey())).replaceAll("%$1s");
            this.translations.put(entry.getKey(), string);
        }
    }

    private String get(String string) {
        String string2 = this.translations.get(string);
        return string2 == null ? string : string2;
    }

    public String translate(String key, Object[] objects) {
        String string = this.get(key);
        try {
            return String.format(string, objects);
        } catch (IllegalFormatException illegalFormatException) {
            return "Format error: " + string;
        }
    }

    public boolean containsKey(String string) {
        return this.translations.containsKey(string);
    }
}

