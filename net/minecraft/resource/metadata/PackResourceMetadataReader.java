/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.resource.metadata;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.resource.metadata.PackResourceMetadata;
import net.minecraft.resource.metadata.ResourceMetadataSerializer;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.JsonHelper;

public class PackResourceMetadataReader
implements ResourceMetadataSerializer<PackResourceMetadata> {
    @Override
    public PackResourceMetadata fromJson(JsonObject jsonObject) {
        MutableText text = Text.Serializer.fromJson(jsonObject.get("description"));
        if (text == null) {
            throw new JsonParseException("Invalid/missing description!");
        }
        int i = JsonHelper.getInt(jsonObject, "pack_format");
        return new PackResourceMetadata(text, i);
    }

    @Override
    public JsonObject toJson(PackResourceMetadata packResourceMetadata) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("description", Text.Serializer.toJsonTree(packResourceMetadata.getDescription()));
        jsonObject.addProperty("pack_format", packResourceMetadata.getPackFormat());
        return jsonObject;
    }

    @Override
    public String getKey() {
        return "pack";
    }

    @Override
    public /* synthetic */ Object fromJson(JsonObject json) {
        return this.fromJson(json);
    }
}

