package com.mojang.realmsclient.dto;

import com.google.gson.annotations.SerializedName;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.realms.RealmsSerializable;

@Environment(EnvType.CLIENT)
public class RealmsDescriptionDto extends ValueObject implements RealmsSerializable {
	@SerializedName("name")
	public String name;
	@SerializedName("description")
	public String description;

	public RealmsDescriptionDto(String name, String description) {
		this.name = name;
		this.description = description;
	}
}
