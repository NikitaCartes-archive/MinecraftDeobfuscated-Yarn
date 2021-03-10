package net.minecraft.network.packet.s2c.play;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import java.util.Collection;
import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.world.GameMode;

public class PlayerListS2CPacket implements Packet<ClientPlayPacketListener> {
	private final PlayerListS2CPacket.Action action;
	private final List<PlayerListS2CPacket.Entry> entries;

	public PlayerListS2CPacket(PlayerListS2CPacket.Action action, ServerPlayerEntity... players) {
		this.action = action;
		this.entries = Lists.<PlayerListS2CPacket.Entry>newArrayListWithCapacity(players.length);

		for (ServerPlayerEntity serverPlayerEntity : players) {
			this.entries
				.add(
					new PlayerListS2CPacket.Entry(
						serverPlayerEntity.getGameProfile(),
						serverPlayerEntity.pingMilliseconds,
						serverPlayerEntity.interactionManager.getGameMode(),
						serverPlayerEntity.getPlayerListName()
					)
				);
		}
	}

	public PlayerListS2CPacket(PlayerListS2CPacket.Action action, Collection<ServerPlayerEntity> players) {
		this.action = action;
		this.entries = Lists.<PlayerListS2CPacket.Entry>newArrayListWithCapacity(players.size());

		for (ServerPlayerEntity serverPlayerEntity : players) {
			this.entries
				.add(
					new PlayerListS2CPacket.Entry(
						serverPlayerEntity.getGameProfile(),
						serverPlayerEntity.pingMilliseconds,
						serverPlayerEntity.interactionManager.getGameMode(),
						serverPlayerEntity.getPlayerListName()
					)
				);
		}
	}

	public PlayerListS2CPacket(PacketByteBuf buf) {
		this.action = buf.readEnumConstant(PlayerListS2CPacket.Action.class);
		this.entries = buf.readList(this.action::read);
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeEnumConstant(this.action);
		buf.writeCollection(this.entries, this.action::write);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
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

	@Nullable
	private static Text readOptionalText(PacketByteBuf buf) {
		return buf.readBoolean() ? buf.readText() : null;
	}

	private static void writeOptionalText(PacketByteBuf buf, @Nullable Text text) {
		if (text == null) {
			buf.writeBoolean(false);
		} else {
			buf.writeBoolean(true);
			buf.writeText(text);
		}
	}

	public String toString() {
		return MoreObjects.toStringHelper(this).add("action", this.action).add("entries", this.entries).toString();
	}

	public static enum Action {
		ADD_PLAYER {
			@Override
			protected PlayerListS2CPacket.Entry read(PacketByteBuf buf) {
				GameProfile gameProfile = new GameProfile(buf.readUuid(), buf.readString(16));
				PropertyMap propertyMap = gameProfile.getProperties();
				buf.forEachInCollection(bufx -> {
					String string = bufx.readString();
					String string2 = bufx.readString();
					if (bufx.readBoolean()) {
						String string3 = bufx.readString();
						propertyMap.put(string, new Property(string, string2, string3));
					} else {
						propertyMap.put(string, new Property(string, string2));
					}
				});
				GameMode gameMode = GameMode.byId(buf.readVarInt());
				int i = buf.readVarInt();
				Text text = PlayerListS2CPacket.readOptionalText(buf);
				return new PlayerListS2CPacket.Entry(gameProfile, i, gameMode, text);
			}

			@Override
			protected void write(PacketByteBuf buf, PlayerListS2CPacket.Entry entry) {
				buf.writeUuid(entry.getProfile().getId());
				buf.writeString(entry.getProfile().getName());
				buf.writeCollection(entry.getProfile().getProperties().values(), (bufx, property) -> {
					bufx.writeString(property.getName());
					bufx.writeString(property.getValue());
					if (property.hasSignature()) {
						bufx.writeBoolean(true);
						bufx.writeString(property.getSignature());
					} else {
						bufx.writeBoolean(false);
					}
				});
				buf.writeVarInt(entry.getGameMode().getId());
				buf.writeVarInt(entry.getLatency());
				PlayerListS2CPacket.writeOptionalText(buf, entry.getDisplayName());
			}
		},
		UPDATE_GAME_MODE {
			@Override
			protected PlayerListS2CPacket.Entry read(PacketByteBuf buf) {
				GameProfile gameProfile = new GameProfile(buf.readUuid(), null);
				GameMode gameMode = GameMode.byId(buf.readVarInt());
				return new PlayerListS2CPacket.Entry(gameProfile, 0, gameMode, null);
			}

			@Override
			protected void write(PacketByteBuf buf, PlayerListS2CPacket.Entry entry) {
				buf.writeUuid(entry.getProfile().getId());
				buf.writeVarInt(entry.getGameMode().getId());
			}
		},
		UPDATE_LATENCY {
			@Override
			protected PlayerListS2CPacket.Entry read(PacketByteBuf buf) {
				GameProfile gameProfile = new GameProfile(buf.readUuid(), null);
				int i = buf.readVarInt();
				return new PlayerListS2CPacket.Entry(gameProfile, i, null, null);
			}

			@Override
			protected void write(PacketByteBuf buf, PlayerListS2CPacket.Entry entry) {
				buf.writeUuid(entry.getProfile().getId());
				buf.writeVarInt(entry.getLatency());
			}
		},
		UPDATE_DISPLAY_NAME {
			@Override
			protected PlayerListS2CPacket.Entry read(PacketByteBuf buf) {
				GameProfile gameProfile = new GameProfile(buf.readUuid(), null);
				Text text = PlayerListS2CPacket.readOptionalText(buf);
				return new PlayerListS2CPacket.Entry(gameProfile, 0, null, text);
			}

			@Override
			protected void write(PacketByteBuf buf, PlayerListS2CPacket.Entry entry) {
				buf.writeUuid(entry.getProfile().getId());
				PlayerListS2CPacket.writeOptionalText(buf, entry.getDisplayName());
			}
		},
		REMOVE_PLAYER {
			@Override
			protected PlayerListS2CPacket.Entry read(PacketByteBuf buf) {
				GameProfile gameProfile = new GameProfile(buf.readUuid(), null);
				return new PlayerListS2CPacket.Entry(gameProfile, 0, null, null);
			}

			@Override
			protected void write(PacketByteBuf buf, PlayerListS2CPacket.Entry entry) {
				buf.writeUuid(entry.getProfile().getId());
			}
		};

		private Action() {
		}

		protected abstract PlayerListS2CPacket.Entry read(PacketByteBuf buf);

		protected abstract void write(PacketByteBuf buf, PlayerListS2CPacket.Entry entry);
	}

	public static class Entry {
		private final int latency;
		private final GameMode gameMode;
		private final GameProfile profile;
		@Nullable
		private final Text displayName;

		public Entry(GameProfile profile, int latency, @Nullable GameMode gameMode, @Nullable Text displayName) {
			this.profile = profile;
			this.latency = latency;
			this.gameMode = gameMode;
			this.displayName = displayName;
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
