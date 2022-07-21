package net.minecraft.client.network;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.text.ParseException;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
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
	public String name;
	public String address;
	public Text playerCountLabel;
	public Text label;
	public long ping;
	public int protocolVersion = SharedConstants.getGameVersion().getProtocolVersion();
	public Text version = Text.literal(SharedConstants.getGameVersion().getName());
	public boolean online;
	public List<Text> playerListSummary = Collections.emptyList();
	private ServerInfo.ResourcePackPolicy resourcePackPolicy = ServerInfo.ResourcePackPolicy.PROMPT;
	@Nullable
	private String icon;
	private boolean local;
	@Nullable
	private ServerInfo.ChatPreview chatPreview;
	private boolean temporaryChatPreviewState = true;
	private boolean field_39918;

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

		if (this.chatPreview != null) {
			ServerInfo.ChatPreview.CODEC.encodeStart(NbtOps.INSTANCE, this.chatPreview).result().ifPresent(chatPreview -> nbtCompound.put("chatPreview", chatPreview));
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

		if (root.contains("chatPreview", NbtElement.COMPOUND_TYPE)) {
			ServerInfo.ChatPreview.CODEC
				.parse(NbtOps.INSTANCE, root.getCompound("chatPreview"))
				.resultOrPartial(LOGGER::error)
				.ifPresent(chatPreview -> serverInfo.chatPreview = chatPreview);
		}

		return serverInfo;
	}

	@Nullable
	public String getIcon() {
		return this.icon;
	}

	public static String parseFavicon(String favicon) throws ParseException {
		if (favicon.startsWith("data:image/png;base64,")) {
			return favicon.substring("data:image/png;base64,".length());
		} else {
			throw new ParseException("Unknown format", 0);
		}
	}

	public void setIcon(@Nullable String icon) {
		this.icon = icon;
	}

	public boolean isLocal() {
		return this.local;
	}

	/**
	 * Sets whether the chat preview is enabled. This affects the saved server info;
	 * to disable the chat preview temporarily use {@link #setTemporaryChatPreviewState}.
	 */
	public void setPreviewsChat(boolean enabled) {
		if (enabled && this.chatPreview == null) {
			this.chatPreview = new ServerInfo.ChatPreview(false, false);
		} else if (!enabled && this.chatPreview != null) {
			this.chatPreview = null;
		}
	}

	@Nullable
	public ServerInfo.ChatPreview getChatPreview() {
		return this.chatPreview;
	}

	/**
	 * Sets the temporary chat preview state. Unlike {@link #setPreviewsChat}, this
	 * does not affect the saved server info.
	 */
	public void setTemporaryChatPreviewState(boolean temporaryChatPreviewState) {
		this.temporaryChatPreviewState = temporaryChatPreviewState;
	}

	public boolean shouldPreviewChat() {
		return this.temporaryChatPreviewState && this.chatPreview != null;
	}

	public void method_45055(boolean bl) {
		this.field_39918 = bl;
	}

	public boolean method_45056() {
		return this.field_39918;
	}

	public void copyFrom(ServerInfo serverInfo) {
		this.address = serverInfo.address;
		this.name = serverInfo.name;
		this.icon = serverInfo.icon;
	}

	public void copyWithSettingsFrom(ServerInfo serverInfo) {
		this.copyFrom(serverInfo);
		this.setResourcePackPolicy(serverInfo.getResourcePackPolicy());
		this.local = serverInfo.local;
		this.chatPreview = Util.map(serverInfo.chatPreview, ServerInfo.ChatPreview::copy);
		this.field_39918 = serverInfo.field_39918;
	}

	@Environment(EnvType.CLIENT)
	public static class ChatPreview {
		public static final Codec<ServerInfo.ChatPreview> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
						Codec.BOOL.optionalFieldOf("acknowledged", Boolean.valueOf(false)).forGetter(chatPreview -> chatPreview.acknowledged),
						Codec.BOOL.optionalFieldOf("toastShown", Boolean.valueOf(false)).forGetter(chatPreview -> chatPreview.toastShown)
					)
					.apply(instance, ServerInfo.ChatPreview::new)
		);
		private boolean acknowledged;
		private boolean toastShown;

		ChatPreview(boolean acknowledged, boolean toastShown) {
			this.acknowledged = acknowledged;
			this.toastShown = toastShown;
		}

		public void setAcknowledged() {
			this.acknowledged = true;
		}

		/**
		 * If the chat preview toast is never shown, returns {@code true} and marks that the
		 * toast was shown; otherwise, returns {@code false}.
		 */
		public boolean showToast() {
			if (!this.toastShown) {
				this.toastShown = true;
				return true;
			} else {
				return false;
			}
		}

		/**
		 * {@return whether the player acknowledged the chat preview warning}
		 */
		public boolean isAcknowledged() {
			return this.acknowledged;
		}

		private ServerInfo.ChatPreview copy() {
			return new ServerInfo.ChatPreview(this.acknowledged, this.toastShown);
		}
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
			this.name = Text.translatable("addServer.resourcePack." + name);
		}

		public Text getName() {
			return this.name;
		}
	}
}
