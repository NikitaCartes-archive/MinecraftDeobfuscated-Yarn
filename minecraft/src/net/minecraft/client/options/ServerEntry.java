package net.minecraft.client.options;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

@Environment(EnvType.CLIENT)
public class ServerEntry {
	public String name;
	public String address;
	public String playerCountLabel;
	public String label;
	public long ping;
	public int protocolVersion = SharedConstants.getGameVersion().getProtocolVersion();
	public String version = SharedConstants.getGameVersion().getName();
	public boolean online;
	public String playerListSummary;
	private ServerEntry.ResourcePackState resourcePackState = ServerEntry.ResourcePackState.field_3767;
	private String icon;
	private boolean local;

	public ServerEntry(String string, String string2, boolean bl) {
		this.name = string;
		this.address = string2;
		this.local = bl;
	}

	public CompoundTag serialize() {
		CompoundTag compoundTag = new CompoundTag();
		compoundTag.putString("name", this.name);
		compoundTag.putString("ip", this.address);
		if (this.icon != null) {
			compoundTag.putString("icon", this.icon);
		}

		if (this.resourcePackState == ServerEntry.ResourcePackState.field_3768) {
			compoundTag.putBoolean("acceptTextures", true);
		} else if (this.resourcePackState == ServerEntry.ResourcePackState.field_3764) {
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

	public static ServerEntry deserialize(CompoundTag compoundTag) {
		ServerEntry serverEntry = new ServerEntry(compoundTag.getString("name"), compoundTag.getString("ip"), false);
		if (compoundTag.containsKey("icon", 8)) {
			serverEntry.setIcon(compoundTag.getString("icon"));
		}

		if (compoundTag.containsKey("acceptTextures", 1)) {
			if (compoundTag.getBoolean("acceptTextures")) {
				serverEntry.setResourcePackState(ServerEntry.ResourcePackState.field_3768);
			} else {
				serverEntry.setResourcePackState(ServerEntry.ResourcePackState.field_3764);
			}
		} else {
			serverEntry.setResourcePackState(ServerEntry.ResourcePackState.field_3767);
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
		field_3768("enabled"),
		field_3764("disabled"),
		field_3767("prompt");

		private final Text name;

		private ResourcePackState(String string2) {
			this.name = new TranslatableText("addServer.resourcePack." + string2);
		}

		public Text getName() {
			return this.name;
		}
	}
}
