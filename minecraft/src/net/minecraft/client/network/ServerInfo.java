package net.minecraft.client.network;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.text.Text;

/**
 * The information of a server entry in the list of servers available in
 * the multiplayer screen from the menu. The list of these servers is
 * stored in the {@code servers.dat} file within the client game directory.
 */
@Environment(EnvType.CLIENT)
public class ServerInfo {
	public String name;
	public String address;
	public Text playerCountLabel;
	public Text label;
	public long ping;
	public int protocolVersion = SharedConstants.getGameVersion().getProtocolVersion();
	public Text version = Text.method_43470(SharedConstants.getGameVersion().getName());
	public boolean online;
	public List<Text> playerListSummary = Collections.emptyList();
	private ServerInfo.ResourcePackPolicy resourcePackPolicy = ServerInfo.ResourcePackPolicy.PROMPT;
	@Nullable
	private String icon;
	private boolean local;

	public ServerInfo(String name, String address, boolean local) {
		this.name = name;
		this.address = address;
		this.local = local;
	}

	public NbtCompound toNbt() {
		NbtCompound nbtCompound = new NbtCompound();
		nbtCompound.putString("name", this.name);
		nbtCompound.putString("ip", this.address);
		if (this.icon != null) {
			nbtCompound.putString("icon", this.icon);
		}

		if (this.resourcePackPolicy == ServerInfo.ResourcePackPolicy.ENABLED) {
			nbtCompound.putBoolean("acceptTextures", true);
		} else if (this.resourcePackPolicy == ServerInfo.ResourcePackPolicy.DISABLED) {
			nbtCompound.putBoolean("acceptTextures", false);
		}

		return nbtCompound;
	}

	/**
	 * {@return the policy on resource packs sent by this server}
	 */
	public ServerInfo.ResourcePackPolicy getResourcePackPolicy() {
		return this.resourcePackPolicy;
	}

	/**
	 * Sets the resource pack policy on this server.
	 * 
	 * <p>This is called when a user has responded to the prompt on whether to
	 * accept server resource packs from this server in the future.
	 */
	public void setResourcePackPolicy(ServerInfo.ResourcePackPolicy resourcePackPolicy) {
		this.resourcePackPolicy = resourcePackPolicy;
	}

	public static ServerInfo fromNbt(NbtCompound root) {
		ServerInfo serverInfo = new ServerInfo(root.getString("name"), root.getString("ip"), false);
		if (root.contains("icon", NbtElement.STRING_TYPE)) {
			serverInfo.setIcon(root.getString("icon"));
		}

		if (root.contains("acceptTextures", NbtElement.BYTE_TYPE)) {
			if (root.getBoolean("acceptTextures")) {
				serverInfo.setResourcePackPolicy(ServerInfo.ResourcePackPolicy.ENABLED);
			} else {
				serverInfo.setResourcePackPolicy(ServerInfo.ResourcePackPolicy.DISABLED);
			}
		} else {
			serverInfo.setResourcePackPolicy(ServerInfo.ResourcePackPolicy.PROMPT);
		}

		return serverInfo;
	}

	@Nullable
	public String getIcon() {
		return this.icon;
	}

	public void setIcon(@Nullable String icon) {
		this.icon = icon;
	}

	public boolean isLocal() {
		return this.local;
	}

	public void copyFrom(ServerInfo serverInfo) {
		this.address = serverInfo.address;
		this.name = serverInfo.name;
		this.setResourcePackPolicy(serverInfo.getResourcePackPolicy());
		this.icon = serverInfo.icon;
		this.local = serverInfo.local;
	}

	/**
	 * The policy of the client when this server sends a {@linkplain
	 * net.minecraft.network.packet.s2c.play.ResourcePackSendS2CPacket server
	 * resource pack}.
	 * 
	 * @see ServerInfo#getResourcePackPolicy()
	 */
	@Environment(EnvType.CLIENT)
	public static enum ResourcePackPolicy {
		/**
		 * Always accepts the resource pack and starts downloading it.
		 */
		ENABLED("enabled"),
		/**
		 * Always rejects the resource pack.
		 */
		DISABLED("disabled"),
		/**
		 * Opens a screen on whether to always accept or reject resource packs from
		 * this server for the current pack or any pack in the future.
		 */
		PROMPT("prompt");

		private final Text name;

		private ResourcePackPolicy(String name) {
			this.name = Text.method_43471("addServer.resourcePack." + name);
		}

		public Text getName() {
			return this.name;
		}
	}
}
