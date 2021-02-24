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
	private final PlayerListS2CPacket.class_5893 action;
	private final List<PlayerListS2CPacket.Entry> entries;

	public PlayerListS2CPacket(PlayerListS2CPacket.class_5893 action, ServerPlayerEntity... players) {
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

	public PlayerListS2CPacket(PlayerListS2CPacket.class_5893 arg, Collection<ServerPlayerEntity> collection) {
		this.action = arg;
		this.entries = Lists.<PlayerListS2CPacket.Entry>newArrayListWithCapacity(collection.size());

		for (ServerPlayerEntity serverPlayerEntity : collection) {
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

	public PlayerListS2CPacket(PacketByteBuf packetByteBuf) {
		this.action = packetByteBuf.readEnumConstant(PlayerListS2CPacket.class_5893.class);
		this.entries = packetByteBuf.method_34066(this.action::method_34150);
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeEnumConstant(this.action);
		buf.method_34062(this.entries, this.action::method_34151);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onPlayerList(this);
	}

	@Environment(EnvType.CLIENT)
	public List<PlayerListS2CPacket.Entry> getEntries() {
		return this.entries;
	}

	@Environment(EnvType.CLIENT)
	public PlayerListS2CPacket.class_5893 getAction() {
		return this.action;
	}

	@Nullable
	private static Text method_34149(PacketByteBuf packetByteBuf) {
		return packetByteBuf.readBoolean() ? packetByteBuf.readText() : null;
	}

	private static void method_34148(PacketByteBuf packetByteBuf, @Nullable Text text) {
		if (text == null) {
			packetByteBuf.writeBoolean(false);
		} else {
			packetByteBuf.writeBoolean(true);
			packetByteBuf.writeText(text);
		}
	}

	public String toString() {
		return MoreObjects.toStringHelper(this).add("action", this.action).add("entries", this.entries).toString();
	}

	public static class Entry {
		private final int latency;
		private final GameMode gameMode;
		private final GameProfile profile;
		@Nullable
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

	public static enum class_5893 {
		field_29136 {
			@Override
			protected PlayerListS2CPacket.Entry method_34150(PacketByteBuf packetByteBuf) {
				GameProfile gameProfile = new GameProfile(packetByteBuf.readUuid(), packetByteBuf.readString(16));
				PropertyMap propertyMap = gameProfile.getProperties();
				packetByteBuf.method_34065(packetByteBufx -> {
					String string = packetByteBufx.readString();
					String string2 = packetByteBufx.readString();
					if (packetByteBufx.readBoolean()) {
						String string3 = packetByteBufx.readString();
						propertyMap.put(string, new Property(string, string2, string3));
					} else {
						propertyMap.put(string, new Property(string, string2));
					}
				});
				GameMode gameMode = GameMode.byId(packetByteBuf.readVarInt());
				int i = packetByteBuf.readVarInt();
				Text text = PlayerListS2CPacket.method_34149(packetByteBuf);
				return new PlayerListS2CPacket.Entry(gameProfile, i, gameMode, text);
			}

			@Override
			protected void method_34151(PacketByteBuf packetByteBuf, PlayerListS2CPacket.Entry entry) {
				packetByteBuf.writeUuid(entry.getProfile().getId());
				packetByteBuf.writeString(entry.getProfile().getName());
				packetByteBuf.method_34062(entry.getProfile().getProperties().values(), (packetByteBufx, property) -> {
					packetByteBufx.writeString(property.getName());
					packetByteBufx.writeString(property.getValue());
					if (property.hasSignature()) {
						packetByteBufx.writeBoolean(true);
						packetByteBufx.writeString(property.getSignature());
					} else {
						packetByteBufx.writeBoolean(false);
					}
				});
				packetByteBuf.writeVarInt(entry.getGameMode().getId());
				packetByteBuf.writeVarInt(entry.getLatency());
				PlayerListS2CPacket.method_34148(packetByteBuf, entry.getDisplayName());
			}
		},
		field_29137 {
			@Override
			protected PlayerListS2CPacket.Entry method_34150(PacketByteBuf packetByteBuf) {
				GameProfile gameProfile = new GameProfile(packetByteBuf.readUuid(), null);
				GameMode gameMode = GameMode.byId(packetByteBuf.readVarInt());
				return new PlayerListS2CPacket.Entry(gameProfile, 0, gameMode, null);
			}

			@Override
			protected void method_34151(PacketByteBuf packetByteBuf, PlayerListS2CPacket.Entry entry) {
				packetByteBuf.writeUuid(entry.getProfile().getId());
				packetByteBuf.writeVarInt(entry.getGameMode().getId());
			}
		},
		field_29138 {
			@Override
			protected PlayerListS2CPacket.Entry method_34150(PacketByteBuf packetByteBuf) {
				GameProfile gameProfile = new GameProfile(packetByteBuf.readUuid(), null);
				int i = packetByteBuf.readVarInt();
				return new PlayerListS2CPacket.Entry(gameProfile, i, null, null);
			}

			@Override
			protected void method_34151(PacketByteBuf packetByteBuf, PlayerListS2CPacket.Entry entry) {
				packetByteBuf.writeUuid(entry.getProfile().getId());
				packetByteBuf.writeVarInt(entry.getLatency());
			}
		},
		field_29139 {
			@Override
			protected PlayerListS2CPacket.Entry method_34150(PacketByteBuf packetByteBuf) {
				GameProfile gameProfile = new GameProfile(packetByteBuf.readUuid(), null);
				Text text = PlayerListS2CPacket.method_34149(packetByteBuf);
				return new PlayerListS2CPacket.Entry(gameProfile, 0, null, text);
			}

			@Override
			protected void method_34151(PacketByteBuf packetByteBuf, PlayerListS2CPacket.Entry entry) {
				packetByteBuf.writeUuid(entry.getProfile().getId());
				PlayerListS2CPacket.method_34148(packetByteBuf, entry.getDisplayName());
			}
		},
		field_29140 {
			@Override
			protected PlayerListS2CPacket.Entry method_34150(PacketByteBuf packetByteBuf) {
				GameProfile gameProfile = new GameProfile(packetByteBuf.readUuid(), null);
				return new PlayerListS2CPacket.Entry(gameProfile, 0, null, null);
			}

			@Override
			protected void method_34151(PacketByteBuf packetByteBuf, PlayerListS2CPacket.Entry entry) {
				packetByteBuf.writeUuid(entry.getProfile().getId());
			}
		};

		private class_5893() {
		}

		protected abstract PlayerListS2CPacket.Entry method_34150(PacketByteBuf packetByteBuf);

		protected abstract void method_34151(PacketByteBuf packetByteBuf, PlayerListS2CPacket.Entry entry);
	}
}
