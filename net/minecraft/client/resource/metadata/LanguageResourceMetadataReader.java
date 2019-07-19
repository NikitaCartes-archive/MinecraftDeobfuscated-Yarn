/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.resource.metadata;

import com.google.common.collect.Sets;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.util.HashSet;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resource.language.LanguageDefinition;
import net.minecraft.client.resource.metadata.LanguageResourceMetadata;
import net.minecraft.resource.metadata.ResourceMetadataReader;
import net.minecraft.util.JsonHelper;

@Environment(value=EnvType.CLIENT)
public class LanguageResourceMetadataReader
implements ResourceMetadataReader<LanguageResourceMetadata> {
    @Override
    public LanguageResourceMetadata fromJson(JsonObject jsonObject) {
        HashSet<LanguageDefinition> set = Sets.newHashSet();
        for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
            String string = entry.getKey();
            if (string.length() > 16) {
                throw new JsonParseException("Invalid language->'" + string + "': language code must not be more than " + 16 + " characters long");
            }
            JsonObject jsonObject2 = JsonHelper.asObject(entry.getValue(), "language");
            String string2 = JsonHelper.getString(jsonObject2, "region");
            String string3 = JsonHelper.getString(jsonObject2, "name");
            boolean bl = JsonHelper.getBoolean(jsonObject2, "bidirectional", false);
            if (string2.isEmpty()) {
                throw new JsonParseException("Invalid language->'" + string + "'->region: empty value");
            }
            if (string3.isEmpty()) {
                throw new JsonParseException("Invalid language->'" + string + "'->name: empty value");
            }
            if (set.add(new LanguageDefinition(string, string2, string3, bl))) continue;
            throw new JsonParseException("Duplicate language->'" + string + "' defined");
        }
        return new LanguageResourceMetadata(set);
    }

    @Override
    public String getKey() {
        return "language";
    }

    @Override
    public /* synthetic */ Object fromJson(JsonObject jsonObject) {
        return this.fromJson(jsonObject);
    }
}

