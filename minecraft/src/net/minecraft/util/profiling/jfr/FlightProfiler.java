package net.minecraft.util.profiling.jfr;

import com.mojang.logging.LogUtils;
import java.net.SocketAddress;
import java.nio.file.Path;
import javax.annotation.Nullable;
import net.minecraft.network.NetworkState;
import net.minecraft.network.packet.PacketIdentifier;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import org.slf4j.Logger;

public interface FlightProfiler {
	FlightProfiler INSTANCE = (FlightProfiler)(Runtime.class.getModule().getLayer().findModule("jdk.jfr").isPresent()
		? JfrProfiler.getInstance()
		: new FlightProfiler.NoopProfiler());

	boolean start(InstanceType instanceType);

	Path stop();

	boolean isProfiling();

	boolean isAvailable();

	void onTick(float tickTime);

	void onPacketReceived(NetworkState state, PacketIdentifier<?> packetIdentifier, SocketAddress remoteAddress, int bytes);

	void onPacketSent(NetworkState state, PacketIdentifier<?> packetIdentifier, SocketAddress remoteAddress, int bytes);

	@Nullable
	Finishable startWorldLoadProfiling();

	@Nullable
	Finishable startChunkGenerationProfiling(ChunkPos chunkPos, RegistryKey<World> world, String targetStatus);

	public static class NoopProfiler implements FlightProfiler {
		private static final Logger LOGGER = LogUtils.getLogger();
		static final Finishable NOOP = () -> {
		};

		@Override
		public boolean start(InstanceType instanceType) {
			LOGGER.warn("Attempted to start Flight Recorder, but it's not supported on this JVM");
			return false;
		}

		@Override
		public Path stop() {
			throw new IllegalStateException("Attempted to stop Flight Recorder, but it's not supported on this JVM");
		}

		@Override
		public boolean isProfiling() {
			return false;
		}

		@Override
		public boolean isAvailable() {
			return false;
		}

		@Override
		public void onPacketReceived(NetworkState state, PacketIdentifier<?> packetIdentifier, SocketAddress remoteAddress, int bytes) {
		}

		@Override
		public void onPacketSent(NetworkState state, PacketIdentifier<?> packetIdentifier, SocketAddress remoteAddress, int bytes) {
		}

		@Override
		public void onTick(float tickTime) {
		}

		@Override
		public Finishable startWorldLoadProfiling() {
			return NOOP;
		}

		@Nullable
		@Override
		public Finishable startChunkGenerationProfiling(ChunkPos chunkPos, RegistryKey<World> world, String targetStatus) {
			return null;
		}
	}
}
