/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.network;

import io.netty.util.AttributeKey;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.BundlePacket;
import net.minecraft.network.packet.BundleSplitterPacket;
import net.minecraft.network.packet.Packet;
import org.jetbrains.annotations.Nullable;

public interface PacketBundleHandler {
    public static final AttributeKey<BundlerGetter> KEY = AttributeKey.valueOf("bundler");
    public static final int MAX_PACKETS = 4096;
    public static final PacketBundleHandler NOOP = new PacketBundleHandler(){

        @Override
        public void forEachPacket(Packet<?> packet, Consumer<Packet<?>> consumer) {
            consumer.accept(packet);
        }

        @Override
        @Nullable
        public Bundler createBundler(Packet<?> splitter) {
            return null;
        }
    };

    public static <T extends PacketListener, P extends BundlePacket<T>> PacketBundleHandler create(final Class<P> bundlePacketType, final Function<Iterable<Packet<T>>, P> bundleFunction, final BundleSplitterPacket<T> splitter) {
        return new PacketBundleHandler(){

            @Override
            public void forEachPacket(Packet<?> packet, Consumer<Packet<?>> consumer) {
                if (packet.getClass() == bundlePacketType) {
                    BundlePacket bundlePacket = (BundlePacket)packet;
                    consumer.accept(splitter);
                    bundlePacket.getPackets().forEach(consumer);
                    consumer.accept(splitter);
                } else {
                    consumer.accept(packet);
                }
            }

            @Override
            @Nullable
            public Bundler createBundler(Packet<?> splitter2) {
                if (splitter2 == splitter) {
                    return new Bundler(){
                        private final List<Packet<T>> packets = new ArrayList();

                        @Override
                        @Nullable
                        public Packet<?> add(Packet<?> packet) {
                            if (packet == splitter) {
                                return (Packet)bundleFunction.apply(this.packets);
                            }
                            Packet<?> packet2 = packet;
                            if (this.packets.size() >= 4096) {
                                throw new IllegalStateException("Too many packets in a bundle");
                            }
                            this.packets.add(packet2);
                            return null;
                        }
                    };
                }
                return null;
            }
        };
    }

    public void forEachPacket(Packet<?> var1, Consumer<Packet<?>> var2);

    @Nullable
    public Bundler createBundler(Packet<?> var1);

    public static interface BundlerGetter {
        public PacketBundleHandler getBundler(NetworkSide var1);
    }

    public static interface Bundler {
        @Nullable
        public Packet<?> add(Packet<?> var1);
    }
}

