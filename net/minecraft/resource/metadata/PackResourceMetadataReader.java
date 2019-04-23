/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.resource.metadata;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.network.chat.Component;
import net.minecraft.resource.metadata.PackResourceMetadata;
import net.minecraft.resource.metadata.ResourceMetadataReader;
import net.minecraft.util.JsonHelper;

public class PackResourceMetadataReader
implements ResourceMetadataReader<PackResourceMetadata> {
    public PackResourceMetadata method_14426(JsonObject jsonObject) {
        Component component = Component.Serializer.fromJson(jsonObject.get("description"));
        if (component == null) {
            throw new JsonParseException("Invalid/missing description!");
        }
        int i = JsonHelper.getInt(jsonObject, "pack_format");
        return new PackResourceMetadata(component, i);
    }

    @Override
    public String getKey() {
        return "pack";
    }

    @Override
    public /* synthetic */ Object fromJson(JsonObject jsonObject) {
        return this.method_14426(jsonObject);
    }
}

