package net.minecraft.network.packet.s2c.play;

import com.google.common.collect.Sets;
import java.util.Set;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketIdentifier;
import net.minecraft.network.packet.PlayPackets;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.world.World;

public record GameJoinS2CPacket(
	int playerEntityId,
	boolean hardcore,
	Set<RegistryKey<World>> dimensionIds,
	int maxPlayers,
	int viewDistance,
	int simulationDistance,
	boolean reducedDebugInfo,
	boolean showDeathScreen,
	boolean doLimitedCrafting,
	CommonPlayerSpawnInfo commonPlayerSpawnInfo,
	boolean enforcesSecureChat
) implements Packet<ClientPlayPacketListener> {
	public static final PacketCodec<PacketByteBuf, GameJoinS2CPacket> CODEC = Packet.createCodec(GameJoinS2CPacket::write, GameJoinS2CPacket::new);

	private GameJoinS2CPacket(PacketByteBuf buf) {
		this(
			buf.readInt(),
			buf.readBoolean(),
			buf.readCollection(Sets::newHashSetWithExpectedSize, b -> b.readRegistryKey(RegistryKeys.WORLD)),
			buf.readVarInt(),
			buf.readVarInt(),
			buf.readVarInt(),
			buf.readBoolean(),
			buf.readBoolean(),
			buf.readBoolean(),
			new CommonPlayerSpawnInfo(buf),
			buf.readBoolean()
		);
	}

	private void write(PacketByteBuf buf) {
		buf.writeInt(this.playerEntityId);
		buf.writeBoolean(this.hardcore);
		buf.writeCollection(this.dimensionIds, PacketByteBuf::writeRegistryKey);
		buf.writeVarInt(this.maxPlayers);
		buf.writeVarInt(this.viewDistance);
		buf.writeVarInt(this.simulationDistance);
		buf.writeBoolean(this.reducedDebugInfo);
		buf.writeBoolean(this.showDeathScreen);
		buf.writeBoolean(this.doLimitedCrafting);
		this.commonPlayerSpawnInfo.write(buf);
		buf.writeBoolean(this.enforcesSecureChat);
	}

	@Override
	public PacketIdentifier<GameJoinS2CPacket> getPacketId() {
		return PlayPackets.LOGIN;
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onGameJoin(this);
	}
}
