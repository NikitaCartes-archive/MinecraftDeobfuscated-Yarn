package net.minecraft.client.network.packet;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import java.io.IOException;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.stat.Stat;
import net.minecraft.stat.StatType;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.registry.Registry;

public class StatisticsClientPacket implements Packet<ClientPlayPacketListener> {
	private Object2IntMap<Stat<?>> stats;

	public StatisticsClientPacket() {
	}

	public StatisticsClientPacket(Object2IntMap<Stat<?>> object2IntMap) {
		this.stats = object2IntMap;
	}

	public void method_11270(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onStatistics(this);
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		int i = packetByteBuf.readVarInt();
		this.stats = new Object2IntOpenHashMap<>(i);

		for (int j = 0; j < i; j++) {
			this.readStat(Registry.STAT_TYPE.getInt(packetByteBuf.readVarInt()), packetByteBuf);
		}
	}

	private <T> void readStat(StatType<T> statType, PacketByteBuf packetByteBuf) {
		int i = packetByteBuf.readVarInt();
		int j = packetByteBuf.readVarInt();
		this.stats.put(statType.getOrCreateStat(statType.getRegistry().getInt(i)), j);
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeVarInt(this.stats.size());

		for (Entry<Stat<?>> entry : this.stats.object2IntEntrySet()) {
			Stat<?> stat = (Stat<?>)entry.getKey();
			packetByteBuf.writeVarInt(Registry.STAT_TYPE.getRawId(stat.getType()));
			packetByteBuf.writeVarInt(this.method_11272(stat));
			packetByteBuf.writeVarInt(entry.getIntValue());
		}
	}

	private <T> int method_11272(Stat<T> stat) {
		return stat.getType().getRegistry().getRawId(stat.getValue());
	}

	@Environment(EnvType.CLIENT)
	public Map<Stat<?>, Integer> getStatMap() {
		return this.stats;
	}
}
