package net.minecraft.util.profiling.jfr.sample;

import com.mojang.datafixers.util.Pair;
import java.time.Duration;
import java.util.Comparator;
import java.util.List;
import jdk.jfr.consumer.RecordedEvent;

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

	public static record Packet(String side, String protocolId, String packetId) {
		public static NetworkIoStatistics.Packet fromEvent(RecordedEvent event) {
			return new NetworkIoStatistics.Packet(event.getString("packetDirection"), event.getString("protocolId"), event.getString("packetId"));
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
