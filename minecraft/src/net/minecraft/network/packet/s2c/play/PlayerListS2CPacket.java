package net.minecraft.network.packet.s2c.play;

import com.google.common.base.MoreObjects;
import com.mojang.authlib.GameProfile;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.encryption.PublicPlayerSession;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.world.GameMode;

public class PlayerListS2CPacket implements Packet<ClientPlayPacketListener> {
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
			PlayerListS2CPacket.Action.UPDATE_DISPLAY_NAME
		);
		return new PlayerListS2CPacket(enumSet, players);
	}

	public PlayerListS2CPacket(PacketByteBuf buf) {
		this.actions = buf.readEnumSet(PlayerListS2CPacket.Action.class);
		this.entries = buf.readList(buf2 -> {
			PlayerListS2CPacket.Serialized serialized = new PlayerListS2CPacket.Serialized(buf2.readUuid());

			for (PlayerListS2CPacket.Action action : this.actions) {
				action.reader.read(serialized, buf2);
			}

			return serialized.toEntry();
		});
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeEnumSet(this.actions, PlayerListS2CPacket.Action.class);
		buf.writeCollection(this.entries, (buf2, entry) -> {
			buf2.writeUuid(entry.profileId());

			for (PlayerListS2CPacket.Action action : this.actions) {
				action.writer.write(buf2, entry);
			}
		});
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
			gameProfile.getProperties().putAll(buf.readPropertyMap());
			serialized.gameProfile = gameProfile;
		}, (buf, entry) -> {
			buf.writeString(entry.profile().getName(), 16);
			buf.writePropertyMap(entry.profile().getProperties());
		}),
		INITIALIZE_CHAT(
			(serialized, buf) -> serialized.session = buf.readNullable(PublicPlayerSession.Serialized::fromBuf),
			(buf, entry) -> buf.writeNullable(entry.chatSession, PublicPlayerSession.Serialized::write)
		),
		UPDATE_GAME_MODE((serialized, buf) -> serialized.gameMode = GameMode.byId(buf.readVarInt()), (buf, entry) -> buf.writeVarInt(entry.gameMode().getId())),
		UPDATE_LISTED((serialized, buf) -> serialized.listed = buf.readBoolean(), (buf, entry) -> buf.writeBoolean(entry.listed())),
		UPDATE_LATENCY((serialized, buf) -> serialized.latency = buf.readVarInt(), (buf, entry) -> buf.writeVarInt(entry.latency())),
		UPDATE_DISPLAY_NAME(
			(serialized, buf) -> serialized.displayName = buf.readNullable(PacketByteBuf::readText),
			(buf, entry) -> buf.writeNullable(entry.displayName(), PacketByteBuf::writeText)
		);

		final PlayerListS2CPacket.Action.Reader reader;
		final PlayerListS2CPacket.Action.Writer writer;

		private Action(PlayerListS2CPacket.Action.Reader reader, PlayerListS2CPacket.Action.Writer writer) {
			this.reader = reader;
			this.writer = writer;
		}

		public interface Reader {
			void read(PlayerListS2CPacket.Serialized serialized, PacketByteBuf buf);
		}

		public interface Writer {
			void write(PacketByteBuf buf, PlayerListS2CPacket.Entry entry);
		}
	}

	public static record Entry(
		UUID profileId,
		GameProfile profile,
		boolean listed,
		int latency,
		GameMode gameMode,
		@Nullable Text displayName,
		@Nullable PublicPlayerSession.Serialized chatSession
	) {

		Entry(ServerPlayerEntity player) {
			this(
				player.getUuid(),
				player.getGameProfile(),
				true,
				player.pingMilliseconds,
				player.interactionManager.getGameMode(),
				player.getPlayerListName(),
				Util.map(player.getSession(), PublicPlayerSession::toSerialized)
			);
		}
	}

	static class Serialized {
		final UUID profileId;
		GameProfile gameProfile;
		boolean listed;
		int latency;
		GameMode gameMode = GameMode.DEFAULT;
		@Nullable
		Text displayName;
		@Nullable
		PublicPlayerSession.Serialized session;

		Serialized(UUID profileId) {
			this.profileId = profileId;
			this.gameProfile = new GameProfile(profileId, null);
		}

		PlayerListS2CPacket.Entry toEntry() {
			return new PlayerListS2CPacket.Entry(this.profileId, this.gameProfile, this.listed, this.latency, this.gameMode, this.displayName, this.session);
		}
	}
}
