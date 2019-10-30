package com.mojang.realmsclient.dto;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class PlayerInfo extends ValueObject {
	private String name;
	private String uuid;
	private boolean operator;
	private boolean accepted;
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
