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

public class StatisticsS2CPacket implements Packet<ClientPlayPacketListener> {
	private Object2IntMap<Stat<?>> stats;

	public StatisticsS2CPacket() {
	}

	public StatisticsS2CPacket(Object2IntMap<Stat<?>> stats) {
		this.stats = stats;
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onStatistics(this);
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		int i = buf.readVarInt();
		this.stats = new Object2IntOpenHashMap<>(i);

		for (int j = 0; j < i; j++) {
			this.readStat(Registry.STAT_TYPE.get(buf.readVarInt()), buf);
		}
	}

	private <T> void readStat(StatType<T> type, PacketByteBuf packetByteBuf) {
		int i = packetByteBuf.readVarInt();
		int j = packetByteBuf.readVarInt();
		this.stats.put(type.getOrCreateStat(type.getRegistry().get(i)), j);
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeVarInt(this.stats.size());

		for (Entry<Stat<?>> entry : this.stats.object2IntEntrySet()) {
			Stat<?> stat = (Stat<?>)entry.getKey();
			buf.writeVarInt(Registry.STAT_TYPE.getRawId(stat.getType()));
			buf.writeVarInt(this.getStatId(stat));
			buf.writeVarInt(entry.getIntValue());
		}
	}

	private <T> int getStatId(Stat<T> stat) {
		return stat.getType().getRegistry().getRawId(stat.getValue());
	}

	@Environment(EnvType.CLIENT)
	public Map<Stat<?>, Integer> getStatMap() {
		return this.stats;
	}
}
