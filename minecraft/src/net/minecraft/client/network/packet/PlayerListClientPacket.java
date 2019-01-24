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
import net.minecraft.text.TextComponent;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.world.GameMode;

public class PlayerListClientPacket implements Packet<ClientPlayPacketListener> {
	private PlayerListClientPacket.Type type;
	private final List<PlayerListClientPacket.class_2705> field_12369 = Lists.<PlayerListClientPacket.class_2705>newArrayList();

	public PlayerListClientPacket() {
	}

	public PlayerListClientPacket(PlayerListClientPacket.Type type, ServerPlayerEntity... serverPlayerEntitys) {
		this.type = type;

		for (ServerPlayerEntity serverPlayerEntity : serverPlayerEntitys) {
			this.field_12369
				.add(
					new PlayerListClientPacket.class_2705(
						serverPlayerEntity.getGameProfile(),
						serverPlayerEntity.field_13967,
						serverPlayerEntity.interactionManager.getGameMode(),
						serverPlayerEntity.method_14206()
					)
				);
		}
	}

	public PlayerListClientPacket(PlayerListClientPacket.Type type, Iterable<ServerPlayerEntity> iterable) {
		this.type = type;

		for (ServerPlayerEntity serverPlayerEntity : iterable) {
			this.field_12369
				.add(
					new PlayerListClientPacket.class_2705(
						serverPlayerEntity.getGameProfile(),
						serverPlayerEntity.field_13967,
						serverPlayerEntity.interactionManager.getGameMode(),
						serverPlayerEntity.method_14206()
					)
				);
		}
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.type = packetByteBuf.readEnumConstant(PlayerListClientPacket.Type.class);
		int i = packetByteBuf.readVarInt();

		for (int j = 0; j < i; j++) {
			GameProfile gameProfile = null;
			int k = 0;
			GameMode gameMode = null;
			TextComponent textComponent = null;
			switch (this.type) {
				case ADD:
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
						textComponent = packetByteBuf.readTextComponent();
					}
					break;
				case UPDATE_GAMEMODE:
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
						textComponent = packetByteBuf.readTextComponent();
					}
					break;
				case REMOVE:
					gameProfile = new GameProfile(packetByteBuf.readUuid(), null);
			}

			this.field_12369.add(new PlayerListClientPacket.class_2705(gameProfile, k, gameMode, textComponent));
		}
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeEnumConstant(this.type);
		packetByteBuf.writeVarInt(this.field_12369.size());

		for (PlayerListClientPacket.class_2705 lv : this.field_12369) {
			switch (this.type) {
				case ADD:
					packetByteBuf.writeUuid(lv.method_11726().getId());
					packetByteBuf.writeString(lv.method_11726().getName());
					packetByteBuf.writeVarInt(lv.method_11726().getProperties().size());

					for (Property property : lv.method_11726().getProperties().values()) {
						packetByteBuf.writeString(property.getName());
						packetByteBuf.writeString(property.getValue());
						if (property.hasSignature()) {
							packetByteBuf.writeBoolean(true);
							packetByteBuf.writeString(property.getSignature());
						} else {
							packetByteBuf.writeBoolean(false);
						}
					}

					packetByteBuf.writeVarInt(lv.method_11725().getId());
					packetByteBuf.writeVarInt(lv.method_11727());
					if (lv.method_11724() == null) {
						packetByteBuf.writeBoolean(false);
					} else {
						packetByteBuf.writeBoolean(true);
						packetByteBuf.writeTextComponent(lv.method_11724());
					}
					break;
				case UPDATE_GAMEMODE:
					packetByteBuf.writeUuid(lv.method_11726().getId());
					packetByteBuf.writeVarInt(lv.method_11725().getId());
					break;
				case UPDATE_LATENCY:
					packetByteBuf.writeUuid(lv.method_11726().getId());
					packetByteBuf.writeVarInt(lv.method_11727());
					break;
				case UPDATE_DISPLAY_NAME:
					packetByteBuf.writeUuid(lv.method_11726().getId());
					if (lv.method_11724() == null) {
						packetByteBuf.writeBoolean(false);
					} else {
						packetByteBuf.writeBoolean(true);
						packetByteBuf.writeTextComponent(lv.method_11724());
					}
					break;
				case REMOVE:
					packetByteBuf.writeUuid(lv.method_11726().getId());
			}
		}
	}

	public void method_11721(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onPlayerList(this);
	}

	@Environment(EnvType.CLIENT)
	public List<PlayerListClientPacket.class_2705> method_11722() {
		return this.field_12369;
	}

	@Environment(EnvType.CLIENT)
	public PlayerListClientPacket.Type getType() {
		return this.type;
	}

	public String toString() {
		return MoreObjects.toStringHelper(this).add("action", this.type).add("entries", this.field_12369).toString();
	}

	public static enum Type {
		ADD,
		UPDATE_GAMEMODE,
		UPDATE_LATENCY,
		UPDATE_DISPLAY_NAME,
		REMOVE;
	}

	public class class_2705 {
		private final int field_12378;
		private final GameMode field_12379;
		private final GameProfile field_12380;
		private final TextComponent field_12377;

		public class_2705(GameProfile gameProfile, int i, @Nullable GameMode gameMode, @Nullable TextComponent textComponent) {
			this.field_12380 = gameProfile;
			this.field_12378 = i;
			this.field_12379 = gameMode;
			this.field_12377 = textComponent;
		}

		public GameProfile method_11726() {
			return this.field_12380;
		}

		public int method_11727() {
			return this.field_12378;
		}

		public GameMode method_11725() {
			return this.field_12379;
		}

		@Nullable
		public TextComponent method_11724() {
			return this.field_12377;
		}

		public String toString() {
			return MoreObjects.toStringHelper(this)
				.add("latency", this.field_12378)
				.add("gameMode", this.field_12379)
				.add("profile", this.field_12380)
				.add("displayName", this.field_12377 == null ? null : TextComponent.Serializer.toJsonString(this.field_12377))
				.toString();
		}
	}
}
