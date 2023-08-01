package net.minecraft.client.realms.dto;

import com.google.gson.annotations.SerializedName;
import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.RealmsSerializable;

@Environment(EnvType.CLIENT)
public class PlayerInfo extends ValueObject implements RealmsSerializable {
	@SerializedName("name")
	private String name;
	@SerializedName("uuid")
	private UUID uuid;
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

	public UUID getUuid() {
		return this.uuid;
	}

	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}

	public boolean isOperator() {
		return this.operator;
	}

	public void setOperator(boolean operator) {
		this.operator = operator;
	}

	public boolean isAccepted() {
		return this.accepted;
	}

	public void setAccepted(boolean accepted) {
		this.accepted = accepted;
	}

	public boolean isOnline() {
		return this.online;
	}

	public void setOnline(boolean online) {
		this.online = online;
	}
}
