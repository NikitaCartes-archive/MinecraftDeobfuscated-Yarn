package com.mojang.realmsclient.dto;

import com.google.gson.annotations.SerializedName;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.realms.RealmsSerializable;

@Environment(EnvType.CLIENT)
public class PlayerInfo extends ValueObject implements RealmsSerializable {
	@SerializedName("name")
	private String name;
	@SerializedName("uuid")
	private String uuid;
	@SerializedName("operator")
	private boolean operator;
	@SerializedName("accepted")
	private boolean accepted;
	@SerializedName("online")
	private boolean online;

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUuid() {
		return this.uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public boolean isOperator() {
		return this.operator;
	}

	public void setOperator(boolean operator) {
		this.operator = operator;
	}

	public boolean getAccepted() {
		return this.accepted;
	}

	public void setAccepted(boolean accepted) {
		this.accepted = accepted;
	}

	public boolean getOnline() {
		return this.online;
	}

	public void setOnline(boolean online) {
		this.online = online;
	}
}
