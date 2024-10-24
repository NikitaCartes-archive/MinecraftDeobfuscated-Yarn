package net.minecraft.client.network;

import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.server.ServerMetadata;
import net.minecraft.text.Text;
import net.minecraft.util.PngMetadata;
import org.slf4j.Logger;

/**
 * The information of a server entry in the list of servers available in
 * the multiplayer screen, or that of the servers connected directly.
 * The information for directly-connected servers are also saved (although
 * hidden from the multiplayer screen) so that chat preview acknowledgements
 * and other settings are saved. The list of these servers is stored in the
 * {@code servers.dat} file within the client game directory.
 * 
 * @see net.minecraft.client.option.ServerList
 */
@Environment(EnvType.CLIENT)
public class ServerInfo {
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final int MAX_FAVICON_SIZE = 1024;
	public String name;
	public String address;
	public Text playerCountLabel;
	public Text label;
	@Nullable
	public ServerMetadata.Players players;
	public long ping;
	public int protocolVersion = SharedConstants.getGameVersion().getProtocolVersion();
	public Text version = Text.literal(SharedConstants.getGameVersion().getName());
	public List<Text> playerListSummary = Collections.emptyList();
	private ServerInfo.ResourcePackPolicy resourcePackPolicy = ServerInfo.ResourcePackPolicy.PROMPT;
	@Nullable
	private byte[] favicon;
	private ServerInfo.ServerType serverType;
	private ServerInfo.Status status = ServerInfo.Status.INITIAL;

	public ServerInfo(String name, String address, ServerInfo.ServerType serverType) {
		this.name = name;
		this.address = address;
		this.serverType = serverType;
	}

	public NbtCompound toNbt() {
		NbtCompound nbtCompound = new NbtCompound();
		nbtCompound.putString("name", this.name);
		nbtCompound.putString("ip", this.address);
		if (this.favicon != null) {
			nbtCompound.putString("icon", Base64.getEncoder().encodeToString(this.favicon));
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
		ServerInfo serverInfo = new ServerInfo(root.getString("name"), root.getString("ip"), ServerInfo.ServerType.OTHER);
		if (root.contains("icon", NbtElement.STRING_TYPE)) {
			try {
				byte[] bs = Base64.getDecoder().decode(root.getString("icon"));
				serverInfo.setFavicon(validateFavicon(bs));
			} catch (IllegalArgumentException var3) {
				LOGGER.warn("Malformed base64 server icon", (Throwable)var3);
			}
		}

		if (root.contains("acceptTextures", NbtElement.NUMBER_TYPE)) {
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
	public byte[] getFavicon() {
		return this.favicon;
	}

	public void setFavicon(@Nullable byte[] favicon) {
		this.favicon = favicon;
	}

	public boolean isLocal() {
		return this.serverType == ServerInfo.ServerType.LAN;
	}

	public boolean isRealm() {
		return this.serverType == ServerInfo.ServerType.REALM;
	}

	public ServerInfo.ServerType getServerType() {
		return this.serverType;
	}

	public void copyFrom(ServerInfo serverInfo) {
		this.address = serverInfo.address;
		this.name = serverInfo.name;
		this.favicon = serverInfo.favicon;
	}

	public void copyWithSettingsFrom(ServerInfo serverInfo) {
		this.copyFrom(serverInfo);
		this.setResourcePackPolicy(serverInfo.getResourcePackPolicy());
		this.serverType = serverInfo.serverType;
	}

	public ServerInfo.Status getStatus() {
		return this.status;
	}

	public void setStatus(ServerInfo.Status status) {
		this.status = status;
	}

	@Nullable
	public static byte[] validateFavicon(@Nullable byte[] favicon) {
		if (favicon != null) {
			try {
				PngMetadata pngMetadata = PngMetadata.fromBytes(favicon);
				if (pngMetadata.width() <= 1024 && pngMetadata.height() <= 1024) {
					return favicon;
				}
			} catch (IOException var2) {
				LOGGER.warn("Failed to decode server icon", (Throwable)var2);
			}
		}

		return null;
	}

	/**
	 * The policy of the client when this server sends a {@linkplain
	 * net.minecraft.network.packet.s2c.common.ResourcePackSendS2CPacket server
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

		private ResourcePackPolicy(final String name) {
			this.name = Text.translatable("addServer.resourcePack." + name);
		}

		public Text getName() {
			return this.name;
		}
	}

	@Environment(EnvType.CLIENT)
	public static enum ServerType {
		LAN,
		REALM,
		OTHER;
	}

	@Environment(EnvType.CLIENT)
	public static enum Status {
		INITIAL,
		PINGING,
		UNREACHABLE,
		INCOMPATIBLE,
		SUCCESSFUL;
	}
}
