/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.realms.dto;

import com.google.gson.annotations.SerializedName;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.RealmsSerializable;
import net.minecraft.client.realms.dto.ValueObject;

@Environment(value=EnvType.CLIENT)
public class RealmsWorldResetDto
extends ValueObject
implements RealmsSerializable {
    @SerializedName(value="seed")
    private final String seed;
    @SerializedName(value="worldTemplateId")
    private final long worldTemplateId;
    @SerializedName(value="levelType")
    private final int levelType;
    @SerializedName(value="generateStructures")
    private final boolean generateStructures;

    public RealmsWorldResetDto(String seed, long worldTemplateId, int levelType, boolean generateStructures) {
        this.seed = seed;
        this.worldTemplateId = worldTemplateId;
        this.levelType = levelType;
        this.generateStructures = generateStructures;
    }
}

