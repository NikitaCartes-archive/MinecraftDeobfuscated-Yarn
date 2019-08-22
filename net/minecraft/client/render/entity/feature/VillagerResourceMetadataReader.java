/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.feature;

import com.google.gson.JsonObject;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.feature.VillagerResourceMetadata;
import net.minecraft.resource.metadata.ResourceMetadataReader;
import net.minecraft.util.JsonHelper;

@Environment(value=EnvType.CLIENT)
public class VillagerResourceMetadataReader
implements ResourceMetadataReader<VillagerResourceMetadata> {
    public VillagerResourceMetadata method_17171(JsonObject jsonObject) {
        return new VillagerResourceMetadata(VillagerResourceMetadata.HatType.from(JsonHelper.getString(jsonObject, "hat", "none")));
    }

    @Override
    public String getKey() {
        return "villager";
    }

    @Override
    public /* synthetic */ Object fromJson(JsonObject jsonObject) {
        return this.method_17171(jsonObject);
    }
}

