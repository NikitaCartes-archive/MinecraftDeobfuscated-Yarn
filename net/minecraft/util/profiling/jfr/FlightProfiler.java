/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util.profiling.jfr;

import java.net.SocketAddress;
import java.nio.file.Path;
import java.util.function.Supplier;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.profiling.jfr.Finishable;
import net.minecraft.util.profiling.jfr.InstanceType;
import net.minecraft.util.profiling.jfr.JfrProfiler;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public interface FlightProfiler {
    public static final FlightProfiler INSTANCE = Runtime.class.getModule().getLayer().findModule("jdk.jfr").isPresent() ? new JfrProfiler() : new NoopProfiler();

    public void registerEvents();

    public boolean start(InstanceType var1);

    public Path stop();

    public boolean isProfiling();

    public boolean isAvailable();

    public void onTick(float var1);

    public void onPacketReceived(Supplier<String> var1, SocketAddress var2, int var3);

    public void onPacketSent(Supplier<String> var1, SocketAddress var2, int var3);

    @Nullable
    public Finishable startWorldLoadProfiling();

    @Nullable
    public Finishable startChunkGenerationProfiling(ChunkPos var1, RegistryKey<World> var2, String var3);

    public static class NoopProfiler
    implements FlightProfiler {
        static final Logger LOGGER = LogManager.getLogger();
        static final Finishable NOOP = () -> {};

        @Override
        public void registerEvents() {
        }

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
        public void onPacketReceived(Supplier<String> packetNameSupplier, SocketAddress remoteAddress, int bytes) {
        }

        @Override
        public void onPacketSent(Supplier<String> packetNameSupplier, SocketAddress remoteAddress, int bytes) {
        }

        @Override
        public void onTick(float tickTime) {
        }

        @Override
        public Finishable startWorldLoadProfiling() {
            return NOOP;
        }

        @Override
        @Nullable
        public Finishable startChunkGenerationProfiling(ChunkPos chunkPos, RegistryKey<World> world, String targetStatus) {
            return null;
        }
    }
}

