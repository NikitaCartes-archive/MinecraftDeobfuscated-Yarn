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

	public PlayerListS2CPacket(PlayerListS2CPacket.Action action, ServerPlayerEntity... serverPlayerEntitys) {
		this.action = action;

		for (ServerPlayerEntity serverPlayerEntity : serverPlayerEntitys) {
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
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.action = packetByteBuf.readEnumConstant(PlayerListS2CPacket.Action.class);
		int i = packetByteBuf.readVarInt();

		for (int j = 0; j < i; j++) {
			GameProfile gameProfile = null;
			int k = 0;
			GameMode gameMode = null;
			Text text = null;
			switch (this.action) {
				case ADD_PLAYER:
					gameProfile = new GameProfile(packetByteBuf.readUuid(), packetByteBuf.readString(16));
					int l = packetByteBuf.readVarInt();
					int m = 0;

					for (; m < l; m++) {
						String string = packetByteBuf.readString(32767);
						String string2 = packetByteBuf.readString(32767);
						if (packetByteBuf.readBoolean()) {
							gameProfile.getProperties().put(string, new Property(string, string2, packetByteBuf.readString(32767)));
						} else {
							gameProfile.getProperties().put(string, new Property(string, string2));
						}
					}

					gameMode = GameMode.byId(packetByteBuf.readVarInt());
					k = packetByteBuf.readVarInt();
					if (packetByteBuf.readBoolean()) {
						text = packetByteBuf.readText();
					}
					break;
				case UPDATE_GAME_MODE:
					gameProfile = new GameProfile(packetByteBuf.readUuid(), null);
					gameMode = GameMode.byId(packetByteBuf.readVarInt());
					break;
				case UPDATE_LATENCY:
					gameProfile = new GameProfile(packetByteBuf.readUuid(), null);
					k = packetByteBuf.readVarInt();
					break;
				case UPDATE_DISPLAY_NAME:
					gameProfile = new GameProfile(packetByteBuf.readUuid(), null);
					if (packetByteBuf.readBoolean()) {
						text = packetByteBuf.readText();
					}
					break;
				case REMOVE_PLAYER:
					gameProfile = new GameProfile(packetByteBuf.readUuid(), null);
			}

			this.entries.add(new PlayerListS2CPacket.Entry(gameProfile, k, gameMode, text));
		}
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeEnumConstant(this.action);
		packetByteBuf.writeVarInt(this.entries.size());

		for (PlayerListS2CPacket.Entry entry : this.entries) {
			switch (this.action) {
				case ADD_PLAYER:
					packetByteBuf.writeUuid(entry.getProfile().getId());
					packetByteBuf.writeString(entry.getProfile().getName());
					packetByteBuf.writeVarInt(entry.getProfile().getProperties().size());

					for (Property property : entry.getProfile().getProperties().values()) {
						packetByteBuf.writeString(property.getName());
						packetByteBuf.writeString(property.getValue());
						if (property.hasSignature()) {
							packetByteBuf.writeBoolean(true);
							packetByteBuf.writeString(property.getSignature());
						} else {
							packetByteBuf.writeBoolean(false);
						}
					}

					packetByteBuf.writeVarInt(entry.getGameMode().getId());
					packetByteBuf.writeVarInt(entry.getLatency());
					if (entry.getDisplayName() == null) {
						packetByteBuf.writeBoolean(false);
					} else {
						packetByteBuf.writeBoolean(true);
						packetByteBuf.writeText(entry.getDisplayName());
					}
					break;
				case UPDATE_GAME_MODE:
					packetByteBuf.writeUuid(entry.getProfile().getId());
					packetByteBuf.writeVarInt(entry.getGameMode().getId());
					break;
				case UPDATE_LATENCY:
					packetByteBuf.writeUuid(entry.getProfile().getId());
					packetByteBuf.writeVarInt(entry.getLatency());
					break;
				case UPDATE_DISPLAY_NAME:
					packetByteBuf.writeUuid(entry.getProfile().getId());
					if (entry.getDisplayName() == null) {
						packetByteBuf.writeBoolean(false);
					} else {
						packetByteBuf.writeBoolean(true);
						packetByteBuf.writeText(entry.getDisplayName());
					}
					break;
				case REMOVE_PLAYER:
					packetByteBuf.writeUuid(entry.getProfile().getId());
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
