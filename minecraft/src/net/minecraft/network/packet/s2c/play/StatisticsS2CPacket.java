package net.minecraft.network.packet.s2c.play;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.Map;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.stat.Stat;
import net.minecraft.stat.StatType;
import net.minecraft.util.registry.Registry;

public class StatisticsS2CPacket implements Packet<ClientPlayPacketListener> {
	private final Object2IntMap<Stat<?>> stats;

	public StatisticsS2CPacket(Object2IntMap<Stat<?>> stats) {
		this.stats = stats;
	}

	public StatisticsS2CPacket(PacketByteBuf buf) {
		this.stats = buf.readMap(Object2IntOpenHashMap::new, bufx -> {
			int i = bufx.readVarInt();
			int j = bufx.readVarInt();
			return getStat(Registry.STAT_TYPE.get(i), j);
		}, PacketByteBuf::readVarInt);
	}

	private static <T> Stat<T> getStat(StatType<T> statType, int id) {
		return statType.getOrCreateStat(statType.getRegistry().get(id));
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onStatistics(this);
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeMap(this.stats, (bufx, stat) -> {
			bufx.writeVarInt(Registry.STAT_TYPE.getRawId(stat.getType()));
			bufx.writeVarInt(this.getStatNetworkId(stat));
		}, PacketByteBuf::writeVarInt);
	}

	private <T> int getStatNetworkId(Stat<T> stat) {
		return stat.getType().getRegistry().getRawId(stat.getValue());
	}

	public Map<Stat<?>, Integer> getStatMap() {
		return this.stats;
	}
}
