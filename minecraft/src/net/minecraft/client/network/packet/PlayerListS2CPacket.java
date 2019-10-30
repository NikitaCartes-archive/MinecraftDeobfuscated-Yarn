package net.minecraft.client.network.packet;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import java.io.IOException;
import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.world.GameMode;

public class PlayerListS2CPacket implements Packet<ClientPlayPacketListener> {
	private PlayerListS2CPacket.Action action;
	private final List<PlayerListS2CPacket.Entry> entries = Lists.<PlayerListS2CPacket.Entry>newArrayList();

	public PlayerListS2CPacket() {
	}

	public PlayerListS2CPacket(PlayerListS2CPacket.Action action, ServerPlayerEntity... players) {
		this.action = action;

		for (ServerPlayerEntity serverPlayerEntity : players) {
			this.entries
				.add(
					new PlayerListS2CPacket.Entry(
						serverPlayerEntity.getGameProfile(),
						serverPlayerEntity.pingMilliseconds,
						serverPlayerEntity.interactionManager.getGameMode(),
						serverPlayerEntity.method_14206()
					)
				);
		}
	}

	public PlayerListS2CPacket(PlayerListS2CPacket.Action action, Iterable<ServerPlayerEntity> iterable) {
		this.action = action;

		for (ServerPlayerEntity serverPlayerEntity : iterable) {
			this.entries
				.add(
					new PlayerListS2CPacket.Entry(
						serverPlayerEntity.getGameProfile(),
						serverPlayerEntity.pingMilliseconds,
						serverPlayerEntity.interactionManager.getGameMode(),
						serverPlayerEntity.method_14206()
					)
				);
		}
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.action = buf.readEnumConstant(PlayerListS2CPacket.Action.class);
		int i = buf.readVarInt();

		for (int j = 0; j < i; j++) {
			GameProfile gameProfile = null;
			int k = 0;
			GameMode gameMode = null;
			Text text = null;
			switch (this.action) {
				case ADD_PLAYER:
					gameProfile = new GameProfile(buf.readUuid(), buf.readString(16));
					int l = buf.readVarInt();
					int m = 0;

					for (; m < l; m++) {
						String string = buf.readString(32767);
						String string2 = buf.readString(32767);
						if (buf.readBoolean()) {
							gameProfile.getProperties().put(string, new Property(string, string2, buf.readString(32767)));
						} else {
							gameProfile.getProperties().put(string, new Property(string, string2));
						}
					}

					gameMode = GameMode.byId(buf.readVarInt());
					k = buf.readVarInt();
					if (buf.readBoolean()) {
						text = buf.readText();
					}
					break;
				case UPDATE_GAME_MODE:
					gameProfile = new GameProfile(buf.readUuid(), null);
					gameMode = GameMode.byId(buf.readVarInt());
					break;
				case UPDATE_LATENCY:
					gameProfile = new GameProfile(buf.readUuid(), null);
					k = buf.readVarInt();
					break;
				case UPDATE_DISPLAY_NAME:
					gameProfile = new GameProfile(buf.readUuid(), null);
					if (buf.readBoolean()) {
						text = buf.readText();
					}
					break;
				case REMOVE_PLAYER:
					gameProfile = new GameProfile(buf.readUuid(), null);
			}

			this.entries.add(new PlayerListS2CPacket.Entry(gameProfile, k, gameMode, text));
		}
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeEnumConstant(this.action);
		buf.writeVarInt(this.entries.size());

		for (PlayerListS2CPacket.Entry entry : this.entries) {
			switch (this.action) {
				case ADD_PLAYER:
					buf.writeUuid(entry.getProfile().getId());
					buf.writeString(entry.getProfile().getName());
					buf.writeVarInt(entry.getProfile().getProperties().size());

					for (Property property : entry.getProfile().getProperties().values()) {
						buf.writeString(property.getName());
						buf.writeString(property.getValue());
						if (property.hasSignature()) {
							buf.writeBoolean(true);
							buf.writeString(property.getSignature());
						} else {
							buf.writeBoolean(false);
						}
					}

					buf.writeVarInt(entry.getGameMode().getId());
					buf.writeVarInt(entry.getLatency());
					if (entry.getDisplayName() == null) {
						buf.writeBoolean(false);
					} else {
						buf.writeBoolean(true);
						buf.writeText(entry.getDisplayName());
					}
					break;
				case UPDATE_GAME_MODE:
					buf.writeUuid(entry.getProfile().getId());
					buf.writeVarInt(entry.getGameMode().getId());
					break;
				case UPDATE_LATENCY:
					buf.writeUuid(entry.getProfile().getId());
					buf.writeVarInt(entry.getLatency());
					break;
				case UPDATE_DISPLAY_NAME:
					buf.writeUuid(entry.getProfile().getId());
					if (entry.getDisplayName() == null) {
						buf.writeBoolean(false);
					} else {
						buf.writeBoolean(true);
						buf.writeText(entry.getDisplayName());
					}
					break;
				case REMOVE_PLAYER:
					buf.writeUuid(entry.getProfile().getId());
			}
		}
	}

	public void method_11721(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onPlayerList(this);
	}

	@Environment(EnvType.CLIENT)
	public List<PlayerListS2CPacket.Entry> getEntries() {
		return this.entries;
	}

	@Environment(EnvType.CLIENT)
	public PlayerListS2CPacket.Action getAction() {
		return this.action;
	}

	public String toString() {
		return MoreObjects.toStringHelper(this).add("action", this.action).add("entries", this.entries).toString();
	}

	public static enum Action {
		ADD_PLAYER,
		UPDATE_GAME_MODE,
		UPDATE_LATENCY,
		UPDATE_DISPLAY_NAME,
		REMOVE_PLAYER;
	}

	public class Entry {
		private final int latency;
		private final GameMode gameMode;
		private final GameProfile profile;
		private final Text displayName;

		public Entry(GameProfile gameProfile, int i, @Nullable GameMode gameMode, @Nullable Text text) {
			this.profile = gameProfile;
			this.latency = i;
			this.gameMode = gameMode;
			this.displayName = text;
		}

		public GameProfile getProfile() {
			return this.profile;
		}

		public int getLatency() {
			return this.latency;
		}

		public GameMode getGameMode() {
			return this.gameMode;
		}

		@Nullable
		public Text getDisplayName() {
			return this.displayName;
		}

		public String toString() {
			return MoreObjects.toStringHelper(this)
				.add("latency", this.latency)
				.add("gameMode", this.gameMode)
				.add("profile", this.profile)
				.add("displayName", this.displayName == null ? null : Text.Serializer.toJson(this.displayName))
				.toString();
		}
	}
}
