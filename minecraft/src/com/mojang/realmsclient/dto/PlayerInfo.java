package com.mojang.realmsclient.dto;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4352;

@Environment(EnvType.CLIENT)
public class PlayerInfo extends class_4352 {
	private String name;
	private String uuid;
	private boolean operator = false;
	private boolean accepted = false;
	private boolean online = false;

	public String getName() {
		return this.name;
	}

	public void setName(String string) {
		this.name = string;
	}

	public String getUuid() {
		return this.uuid;
	}

	public void setUuid(String string) {
		this.uuid = string;
	}

	public boolean isOperator() {
		return this.operator;
	}

	public void setOperator(boolean bl) {
		this.operator = bl;
	}

	public boolean getAccepted() {
		return this.accepted;
	}

	public void setAccepted(boolean bl) {
		this.accepted = bl;
	}

	public boolean getOnline() {
		return this.online;
	}

	public void setOnline(boolean bl) {
		this.online = bl;
	}
}