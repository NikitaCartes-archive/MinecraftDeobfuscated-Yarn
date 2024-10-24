package net.minecraft.network.packet.s2c.play;

import com.google.common.base.MoreObjects;
import com.mojang.authlib.GameProfile;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.encryption.PublicPlayerSession;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.Nullables;
import net.minecraft.world.GameMode;

public class PlayerListS2CPacket implements Packet<ClientPlayPacketListener> {
	public static final PacketCodec<RegistryByteBuf, PlayerListS2CPacket> CODEC = Packet.createCodec(PlayerListS2CPacket::write, PlayerListS2CPacket::new);
	private final EnumSet<PlayerListS2CPacket.Action> actions;
	private final List<PlayerListS2CPacket.Entry> entries;

	public PlayerListS2CPacket(EnumSet<PlayerListS2CPacket.Action> actions, Collection<ServerPlayerEntity> players) {
		this.actions = actions;
		this.entries = players.stream().map(PlayerListS2CPacket.Entry::new).toList();
	}

	public PlayerListS2CPacket(PlayerListS2CPacket.Action action, ServerPlayerEntity player) {
		this.actions = EnumSet.of(action);
		this.entries = List.of(new PlayerListS2CPacket.Entry(player));
	}

	public static PlayerListS2CPacket entryFromPlayer(Collection<ServerPlayerEntity> players) {
		EnumSet<PlayerListS2CPacket.Action> enumSet = EnumSet.of(
			PlayerListS2CPacket.Action.ADD_PLAYER,
			PlayerListS2CPacket.Action.INITIALIZE_CHAT,
			PlayerListS2CPacket.Action.UPDATE_GAME_MODE,
			PlayerListS2CPacket.Action.UPDATE_LISTED,
			PlayerListS2CPacket.Action.UPDATE_LATENCY,
			PlayerListS2CPacket.Action.UPDATE_DISPLAY_NAME,
			PlayerListS2CPacket.Action.UPDATE_LIST_ORDER
		);
		return new PlayerListS2CPacket(enumSet, players);
	}

	private PlayerListS2CPacket(RegistryByteBuf buf) {
		this.actions = buf.readEnumSet(PlayerListS2CPacket.Action.class);
		this.entries = buf.readList(buf2 -> {
			PlayerListS2CPacket.Serialized serialized = new PlayerListS2CPacket.Serialized(buf2.readUuid());

			for (PlayerListS2CPacket.Action action : this.actions) {
				action.reader.read(serialized, (RegistryByteBuf)buf2);
			}

			return serialized.toEntry();
		});
	}

	private void write(RegistryByteBuf buf) {
		buf.writeEnumSet(this.actions, PlayerListS2CPacket.Action.class);
		buf.writeCollection(this.entries, (buf2, entry) -> {
			buf2.writeUuid(entry.profileId());

			for (PlayerListS2CPacket.Action action : this.actions) {
				action.writer.write((RegistryByteBuf)buf2, entry);
			}
		});
	}

	@Override
	public PacketType<PlayerListS2CPacket> getPacketId() {
		return PlayPackets.PLAYER_INFO_UPDATE;
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onPlayerList(this);
	}

	public EnumSet<PlayerListS2CPacket.Action> getActions() {
		return this.actions;
	}

	public List<PlayerListS2CPacket.Entry> getEntries() {
		return this.entries;
	}

	public List<PlayerListS2CPacket.Entry> getPlayerAdditionEntries() {
		return this.actions.contains(PlayerListS2CPacket.Action.ADD_PLAYER) ? this.entries : List.of();
	}

	public String toString() {
		return MoreObjects.toStringHelper(this).add("actions", this.actions).add("entries", this.entries).toString();
	}

	public static enum Action {
		ADD_PLAYER((serialized, buf) -> {
			GameProfile gameProfile = new GameProfile(serialized.profileId, buf.readString(16));
			gameProfile.getProperties().putAll(PacketCodecs.PROPERTY_MAP.decode(buf));
			serialized.gameProfile = gameProfile;
		}, (buf, entry) -> {
			GameProfile gameProfile = (GameProfile)Objects.requireNonNull(entry.profile());
			buf.writeString(gameProfile.getName(), 16);
			PacketCodecs.PROPERTY_MAP.encode(buf, gameProfile.getProperties());
		}),
		INITIALIZE_CHAT(
			(serialized, buf) -> serialized.session = buf.readNullable(PublicPlayerSession.Serialized::fromBuf),
			(buf, entry) -> buf.writeNullable(entry.chatSession, PublicPlayerSession.Serialized::write)
		),
		UPDATE_GAME_MODE((serialized, buf) -> serialized.gameMode = GameMode.byId(buf.readVarInt()), (buf, entry) -> buf.writeVarInt(entry.gameMode().getId())),
		UPDATE_LISTED((serialized, buf) -> serialized.listed = buf.readBoolean(), (buf, entry) -> buf.writeBoolean(entry.listed())),
		UPDATE_LATENCY((serialized, buf) -> serialized.latency = buf.readVarInt(), (buf, entry) -> buf.writeVarInt(entry.latency())),
		UPDATE_DISPLAY_NAME(
			(serialized, buf) -> serialized.displayName = PacketByteBuf.readNullable(buf, TextCodecs.UNLIMITED_REGISTRY_PACKET_CODEC),
			(buf, entry) -> PacketByteBuf.writeNullable(buf, entry.displayName(), TextCodecs.UNLIMITED_REGISTRY_PACKET_CODEC)
		),
		UPDATE_LIST_ORDER((serialized, buf) -> serialized.listOrder = buf.readVarInt(), (buf, entry) -> buf.writeVarInt(entry.listOrder));

		final PlayerListS2CPacket.Action.Reader reader;
		final PlayerListS2CPacket.Action.Writer writer;

		private Action(final PlayerListS2CPacket.Action.Reader reader, final PlayerListS2CPacket.Action.Writer writer) {
			this.reader = reader;
			this.writer = writer;
		}

		public interface Reader {
			void read(PlayerListS2CPacket.Serialized serialized, RegistryByteBuf buf);
		}

		public interface Writer {
			void write(RegistryByteBuf buf, PlayerListS2CPacket.Entry entry);
		}
	}

	public static record Entry(
		UUID profileId,
		@Nullable GameProfile profile,
		boolean listed,
		int latency,
		GameMode gameMode,
		@Nullable Text displayName,
		int listOrder,
		@Nullable PublicPlayerSession.Serialized chatSession
	) {

		Entry(ServerPlayerEntity player) {
			this(
				player.getUuid(),
				player.getGameProfile(),
				true,
				player.networkHandler.getLatency(),
				player.interactionManager.getGameMode(),
				player.getPlayerListName(),
				player.getPlayerListOrder(),
				Nullables.map(player.getSession(), PublicPlayerSession::toSerialized)
			);
		}
	}

	static class Serialized {
		final UUID profileId;
		@Nullable
		GameProfile gameProfile;
		boolean listed;
		int latency;
		GameMode gameMode = GameMode.DEFAULT;
		@Nullable
		Text displayName;
		int listOrder;
		@Nullable
		PublicPlayerSession.Serialized session;

		Serialized(UUID profileId) {
			this.profileId = profileId;
		}

		PlayerListS2CPacket.Entry toEntry() {
			return new PlayerListS2CPacket.Entry(
				this.profileId, this.gameProfile, this.listed, this.latency, this.gameMode, this.displayName, this.listOrder, this.session
			);
		}
	}
}
