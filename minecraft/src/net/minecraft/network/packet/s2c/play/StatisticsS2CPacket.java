package net.minecraft.network.packet.s2c.play;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.codec.RegistryByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketIdentifier;
import net.minecraft.network.packet.PlayPackets;
import net.minecraft.stat.Stat;

public record StatisticsS2CPacket(Object2IntMap<Stat<?>> stats) implements Packet<ClientPlayPacketListener> {
	private static final PacketCodec<RegistryByteBuf, Object2IntMap<Stat<?>>> field_47900 = PacketCodecs.map(
		Object2IntOpenHashMap::new, Stat.PACKET_CODEC, PacketCodecs.VAR_INT
	);
	public static final PacketCodec<RegistryByteBuf, StatisticsS2CPacket> field_47899 = field_47900.xmap(StatisticsS2CPacket::new, StatisticsS2CPacket::stats);

	@Override
	public PacketIdentifier<StatisticsS2CPacket> getPacketId() {
		return PlayPackets.AWARD_STATS;
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onStatistics(this);
	}
}
