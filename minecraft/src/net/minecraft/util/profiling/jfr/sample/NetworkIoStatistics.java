package net.minecraft.util.profiling.jfr.sample;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import java.time.Duration;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import jdk.jfr.consumer.RecordedEvent;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.NetworkState;

public final class NetworkIoStatistics {
	private final NetworkIoStatistics.PacketStatistics combinedStatistics;
	private final List<Pair<NetworkIoStatistics.Packet, NetworkIoStatistics.PacketStatistics>> topContributors;
	private final Duration duration;

	public NetworkIoStatistics(Duration duration, List<Pair<NetworkIoStatistics.Packet, NetworkIoStatistics.PacketStatistics>> packetsToStatistics) {
		this.duration = duration;
		this.combinedStatistics = (NetworkIoStatistics.PacketStatistics)packetsToStatistics.stream()
			.map(Pair::getSecond)
			.reduce(NetworkIoStatistics.PacketStatistics::add)
			.orElseGet(() -> new NetworkIoStatistics.PacketStatistics(0L, 0L));
		this.topContributors = packetsToStatistics.stream()
			.sorted(Comparator.comparing(Pair::getSecond, NetworkIoStatistics.PacketStatistics.COMPARATOR))
			.limit(10L)
			.toList();
	}

	public double getCountPerSecond() {
		return (double)this.combinedStatistics.totalCount / (double)this.duration.getSeconds();
	}

	public double getBytesPerSecond() {
		return (double)this.combinedStatistics.totalSize / (double)this.duration.getSeconds();
	}

	public long getTotalCount() {
		return this.combinedStatistics.totalCount;
	}

	public long getTotalSize() {
		return this.combinedStatistics.totalSize;
	}

	public List<Pair<NetworkIoStatistics.Packet, NetworkIoStatistics.PacketStatistics>> getTopContributors() {
		return this.topContributors;
	}

	public static record Packet(NetworkSide side, int protocolId, int packetId) {
		private static final Map<NetworkIoStatistics.Packet, String> PACKET_TO_NAME;

		public String getName() {
			return (String)PACKET_TO_NAME.getOrDefault(this, "unknown");
		}

		public static NetworkIoStatistics.Packet fromEvent(RecordedEvent event) {
			return new NetworkIoStatistics.Packet(
				event.getEventType().getName().equals("minecraft.PacketSent") ? NetworkSide.CLIENTBOUND : NetworkSide.SERVERBOUND,
				event.getInt("protocolId"),
				event.getInt("packetId")
			);
		}

		static {
			Builder<NetworkIoStatistics.Packet, String> builder = ImmutableMap.builder();

			for (NetworkState networkState : NetworkState.values()) {
				for (NetworkSide networkSide : NetworkSide.values()) {
					Int2ObjectMap<Class<? extends net.minecraft.network.packet.Packet<?>>> int2ObjectMap = networkState.getPacketIdToPacketMap(networkSide);
					int2ObjectMap.forEach((packetId, clazz) -> builder.put(new NetworkIoStatistics.Packet(networkSide, networkState.getId(), packetId), clazz.getSimpleName()));
				}
			}

			PACKET_TO_NAME = builder.build();
		}
	}

	public static record PacketStatistics(long totalCount, long totalSize) {
		static final Comparator<NetworkIoStatistics.PacketStatistics> COMPARATOR = Comparator.comparing(NetworkIoStatistics.PacketStatistics::totalSize)
			.thenComparing(NetworkIoStatistics.PacketStatistics::totalCount)
			.reversed();

		NetworkIoStatistics.PacketStatistics add(NetworkIoStatistics.PacketStatistics statistics) {
			return new NetworkIoStatistics.PacketStatistics(this.totalCount + statistics.totalCount, this.totalSize + statistics.totalSize);
		}
	}
}
