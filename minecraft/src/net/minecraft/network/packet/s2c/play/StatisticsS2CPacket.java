package net.minecraft.network.packet.s2c.play;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;
import net.minecraft.stat.Stat;

public record StatisticsS2CPacket(Object2IntMap<Stat<?>> stats) implements Packet<ClientPlayPacketListener> {
	private static final PacketCodec<RegistryByteBuf, Object2IntMap<Stat<?>>> STAT_MAP_CODEC = PacketCodecs.map(
		Object2IntOpenHashMap::new, Stat.PACKET_CODEC, PacketCodecs.VAR_INT
	);
	public static final PacketCodec<RegistryByteBuf, StatisticsS2CPacket> CODEC = STAT_MAP_CODEC.xmap(StatisticsS2CPacket::new, StatisticsS2CPacket::stats);

	@Override
	public PacketType<StatisticsS2CPacket> getPacketId() {
		return PlayPackets.AWARD_STATS;
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onStatistics(this);
	}
}
