/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.resource.metadata;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.util.ArrayList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resource.metadata.AnimationFrameResourceMetadata;
import net.minecraft.client.resource.metadata.AnimationResourceMetadata;
import net.minecraft.resource.metadata.ResourceMetadataReader;
import net.minecraft.util.JsonHelper;
import org.apache.commons.lang3.Validate;

@Environment(value=EnvType.CLIENT)
public class AnimationResourceMetadataReader
implements ResourceMetadataReader<AnimationResourceMetadata> {
    public AnimationResourceMetadata method_4692(JsonObject jsonObject) {
        int j;
        ArrayList<AnimationFrameResourceMetadata> list = Lists.newArrayList();
        int i = JsonHelper.getInt(jsonObject, "frametime", 1);
        if (i != 1) {
            Validate.inclusiveBetween(1L, Integer.MAX_VALUE, i, "Invalid default frame time");
        }
        if (jsonObject.has("frames")) {
            try {
                JsonArray jsonArray = JsonHelper.getArray(jsonObject, "frames");
                for (j = 0; j < jsonArray.size(); ++j) {
                    JsonElement jsonElement = jsonArray.get(j);
                    AnimationFrameResourceMetadata animationFrameResourceMetadata = this.readFrameMetadata(j, jsonElement);
                    if (animationFrameResourceMetadata == null) continue;
                    list.add(animationFrameResourceMetadata);
                }
            } catch (ClassCastException classCastException) {
                throw new JsonParseException("Invalid animation->frames: expected array, was " + jsonObject.get("frames"), classCastException);
            }
        }
        int k = JsonHelper.getInt(jsonObject, "width", -1);
        j = JsonHelper.getInt(jsonObject, "height", -1);
        if (k != -1) {
            Validate.inclusiveBetween(1L, Integer.MAX_VALUE, k, "Invalid width");
        }
        if (j != -1) {
            Validate.inclusiveBetween(1L, Integer.MAX_VALUE, j, "Invalid height");
        }
        boolean bl = JsonHelper.getBoolean(jsonObject, "interpolate", false);
        return new AnimationResourceMetadata(list, k, j, i, bl);
    }

    private AnimationFrameResourceMetadata readFrameMetadata(int i, JsonElement jsonElement) {
        if (jsonElement.isJsonPrimitive()) {
            return new AnimationFrameResourceMetadata(JsonHelper.asInt(jsonElement, "frames[" + i + "]"));
        }
        if (jsonElement.isJsonObject()) {
            JsonObject jsonObject = JsonHelper.asObject(jsonElement, "frames[" + i + "]");
            int j = JsonHelper.getInt(jsonObject, "time", -1);
            if (jsonObject.has("time")) {
                Validate.inclusiveBetween(1L, Integer.MAX_VALUE, j, "Invalid frame time");
            }
            int k = JsonHelper.getInt(jsonObject, "index");
            Validate.inclusiveBetween(0L, Integer.MAX_VALUE, k, "Invalid frame index");
            return new AnimationFrameResourceMetadata(k, j);
        }
        return null;
    }

    @Override
    public String getKey() {
        return "animation";
    }

    @Override
    public /* synthetic */ Object fromJson(JsonObject jsonObject) {
        return this.method_4692(jsonObject);
    }
}

