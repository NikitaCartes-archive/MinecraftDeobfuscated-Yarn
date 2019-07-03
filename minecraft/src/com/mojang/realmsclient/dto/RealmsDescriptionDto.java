package com.mojang.realmsclient.dto;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4352;

@Environment(EnvType.CLIENT)
public class RealmsDescriptionDto extends class_4352 {
	public String name;
	public String description;

	public RealmsDescriptionDto(String string, String string2) {
		this.name = string;
		this.description = string2;
	}
}
