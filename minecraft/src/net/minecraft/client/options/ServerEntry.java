package net.minecraft.client.options;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;

@Environment(EnvType.CLIENT)
public class ServerEntry {
	public String name;
	public String address;
	public String playerCountLabel;
	public String label;
	public long ping;
	public int protocolVersion = SharedConstants.getGameVersion().getProtocolVersion();
	public String version = SharedConstants.getGameVersion().getName();
	public boolean field_3754;
	public String playerListSummary;
	private ServerEntry.ResourcePackState resourcePackState = ServerEntry.ResourcePackState.PROMPT;
	private String icon;
	private boolean local;

	public ServerEntry(String string, String string2, boolean bl) {
		this.name = string;
		this.address = string2;
		this.local = bl;
	}

	public CompoundTag method_2992() {
		CompoundTag compoundTag = new CompoundTag();
		compoundTag.putString("name", this.name);
		compoundTag.putString("ip", this.address);
		if (this.icon != null) {
			compoundTag.putString("icon", this.icon);
		}

		if (this.resourcePackState == ServerEntry.ResourcePackState.ENABLED) {
			compoundTag.putBoolean("acceptTextures", true);
		} else if (this.resourcePackState == ServerEntry.ResourcePackState.DISABLED) {
			compoundTag.putBoolean("acceptTextures", false);
		}

		return compoundTag;
	}

	public ServerEntry.ResourcePackState getResourcePack() {
		return this.resourcePackState;
	}

	public void setResourcePackState(ServerEntry.ResourcePackState resourcePackState) {
		this.resourcePackState = resourcePackState;
	}

	public static ServerEntry method_2993(CompoundTag compoundTag) {
		ServerEntry serverEntry = new ServerEntry(compoundTag.getString("name"), compoundTag.getString("ip"), false);
		if (compoundTag.containsKey("icon", 8)) {
			serverEntry.setIcon(compoundTag.getString("icon"));
		}

		if (compoundTag.containsKey("acceptTextures", 1)) {
			if (compoundTag.getBoolean("acceptTextures")) {
				serverEntry.setResourcePackState(ServerEntry.ResourcePackState.ENABLED);
			} else {
				serverEntry.setResourcePackState(ServerEntry.ResourcePackState.DISABLED);
			}
		} else {
			serverEntry.setResourcePackState(ServerEntry.ResourcePackState.PROMPT);
		}

		return serverEntry;
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

	public void copyFrom(ServerEntry serverEntry) {
		this.address = serverEntry.address;
		this.name = serverEntry.name;
		this.setResourcePackState(serverEntry.getResourcePack());
		this.icon = serverEntry.icon;
		this.local = serverEntry.local;
	}

	@Environment(EnvType.CLIENT)
	public static enum ResourcePackState {
		ENABLED("enabled"),
		DISABLED("disabled"),
		PROMPT("prompt");

		private final TextComponent field_3765;

		private ResourcePackState(String string2) {
			this.field_3765 = new TranslatableTextComponent("addServer.resourcePack." + string2);
		}

		public TextComponent method_2997() {
			return this.field_3765;
		}
	}
}
