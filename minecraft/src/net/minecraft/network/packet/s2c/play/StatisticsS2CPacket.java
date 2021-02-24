package net.minecraft.network.packet.s2c.play;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
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

	public StatisticsS2CPacket(PacketByteBuf packetByteBuf) {
		this.stats = packetByteBuf.method_34069(Object2IntOpenHashMap::new, packetByteBufx -> {
			int i = packetByteBufx.readVarInt();
			int j = packetByteBufx.readVarInt();
			return method_34086(Registry.STAT_TYPE.get(i), j);
		}, PacketByteBuf::readVarInt);
	}

	private static <T> Stat<T> method_34086(StatType<T> statType, int i) {
		return statType.getOrCreateStat(statType.getRegistry().get(i));
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onStatistics(this);
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.method_34063(this.stats, (packetByteBuf, stat) -> {
			packetByteBuf.writeVarInt(Registry.STAT_TYPE.getRawId(stat.getType()));
			packetByteBuf.writeVarInt(this.method_34085(stat));
		}, PacketByteBuf::writeVarInt);
	}

	private <T> int method_34085(Stat<T> stat) {
		return stat.getType().getRegistry().getRawId(stat.getValue());
	}

	@Environment(EnvType.CLIENT)
	public Map<Stat<?>, Integer> getStatMap() {
		return this.stats;
	}
}
