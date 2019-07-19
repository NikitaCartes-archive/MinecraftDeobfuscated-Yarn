package net.minecraft.client.network;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

@Environment(EnvType.CLIENT)
public class ServerInfo {
	public String name;
	public String address;
	public String playerCountLabel;
	public String label;
	public long ping;
	public int protocolVersion = SharedConstants.getGameVersion().getProtocolVersion();
	public String version = SharedConstants.getGameVersion().getName();
	public boolean online;
	public String playerListSummary;
	private ServerInfo.ResourcePackState resourcePackState = ServerInfo.ResourcePackState.PROMPT;
	private String icon;
	private boolean local;

	public ServerInfo(String name, String address, boolean local) {
		this.name = name;
		this.address = address;
		this.local = local;
	}

	public CompoundTag serialize() {
		CompoundTag compoundTag = new CompoundTag();
		compoundTag.putString("name", this.name);
		compoundTag.putString("ip", this.address);
		if (this.icon != null) {
			compoundTag.putString("icon", this.icon);
		}

		if (this.resourcePackState == ServerInfo.ResourcePackState.ENABLED) {
			compoundTag.putBoolean("acceptTextures", true);
		} else if (this.resourcePackState == ServerInfo.ResourcePackState.DISABLED) {
			compoundTag.putBoolean("acceptTextures", false);
		}

		return compoundTag;
	}

	public ServerInfo.ResourcePackState getResourcePack() {
		return this.resourcePackState;
	}

	public void setResourcePackState(ServerInfo.ResourcePackState resourcePackState) {
		this.resourcePackState = resourcePackState;
	}

	public static ServerInfo deserialize(CompoundTag tag) {
		ServerInfo serverInfo = new ServerInfo(tag.getString("name"), tag.getString("ip"), false);
		if (tag.contains("icon", 8)) {
			serverInfo.setIcon(tag.getString("icon"));
		}

		if (tag.contains("acceptTextures", 1)) {
			if (tag.getBoolean("acceptTextures")) {
				serverInfo.setResourcePackState(ServerInfo.ResourcePackState.ENABLED);
			} else {
				serverInfo.setResourcePackState(ServerInfo.ResourcePackState.DISABLED);
			}
		} else {
			serverInfo.setResourcePackState(ServerInfo.ResourcePackState.PROMPT);
		}

		return serverInfo;
	}

	@Nullable
	public String getIcon() {
		return this.icon;
	}

	public void setIcon(@Nullable String string) {
		this.icon = string;
	}

	public boolean isLocal() {
		return this.local;
	}

	public void copyFrom(ServerInfo serverInfo) {
		this.address = serverInfo.address;
		this.name = serverInfo.name;
		this.setResourcePackState(serverInfo.getResourcePack());
		this.icon = serverInfo.icon;
		this.local = serverInfo.local;
	}

	@Environment(EnvType.CLIENT)
	public static enum ResourcePackState {
		ENABLED("enabled"),
		DISABLED("disabled"),
		PROMPT("prompt");

		private final Text name;

		private ResourcePackState(String name) {
			this.name = new TranslatableText("addServer.resourcePack." + name);
		}

		public Text getName() {
			return this.name;
		}
	}
}
