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
public class RealmsDescriptionDto
extends ValueObject
implements RealmsSerializable {
    @SerializedName(value="name")
    public String name;
    @SerializedName(value="description")
    public String description;

    public RealmsDescriptionDto(String name, String description) {
        this.name = name;
        this.description = description;
    }
}

