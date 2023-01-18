package net.minecraft.network.packet.s2c.play;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.Map;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.registry.Registries;
import net.minecraft.stat.Stat;
import net.minecraft.stat.StatType;

public class StatisticsS2CPacket implements Packet<ClientPlayPacketListener> {
	private final Object2IntMap<Stat<?>> stats;

	public StatisticsS2CPacket(Object2IntMap<Stat<?>> stats) {
		this.stats = stats;
	}

	public StatisticsS2CPacket(PacketByteBuf buf) {
		this.stats = buf.readMap(Object2IntOpenHashMap::new, bufx -> {
			StatType<?> statType = bufx.readRegistryValue(Registries.STAT_TYPE);
			return getOrCreateStat(buf, statType);
		}, PacketByteBuf::readVarInt);
	}

	private static <T> Stat<T> getOrCreateStat(PacketByteBuf buf, StatType<T> statType) {
		return statType.getOrCreateStat(buf.readRegistryValue(statType.getRegistry()));
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onStatistics(this);
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeMap(this.stats, StatisticsS2CPacket::write, PacketByteBuf::writeVarInt);
	}

	private static <T> void write(PacketByteBuf buf, Stat<T> stat) {
		buf.writeRegistryValue(Registries.STAT_TYPE, stat.getType());
		buf.writeRegistryValue(stat.getType().getRegistry(), stat.getValue());
	}

	public Map<Stat<?>, Integer> getStatMap() {
		return this.stats;
	}
}
