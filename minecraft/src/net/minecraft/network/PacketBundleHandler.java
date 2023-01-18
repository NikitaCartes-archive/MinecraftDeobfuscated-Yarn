package net.minecraft.network;

import io.netty.util.AttributeKey;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.BundlePacket;
import net.minecraft.network.packet.BundleSplitterPacket;
import net.minecraft.network.packet.Packet;

public interface PacketBundleHandler {
	AttributeKey<PacketBundleHandler.BundlerGetter> KEY = AttributeKey.valueOf("bundler");
	int MAX_PACKETS = 4096;
	PacketBundleHandler NOOP = new PacketBundleHandler() {
		@Override
		public void forEachPacket(Packet<?> packet, Consumer<Packet<?>> consumer) {
			consumer.accept(packet);
		}

		@Nullable
		@Override
		public PacketBundleHandler.Bundler createBundler(Packet<?> splitter) {
			return null;
		}
	};

	static <T extends PacketListener, P extends BundlePacket<T>> PacketBundleHandler create(
		Class<P> bundlePacketType, Function<Iterable<Packet<T>>, P> bundleFunction, BundleSplitterPacket<T> splitter
	) {
		return new PacketBundleHandler() {
			@Override
			public void forEachPacket(Packet<?> packet, Consumer<Packet<?>> consumer) {
				if (packet.getClass() == bundlePacketType) {
					P bundlePacket = (P)packet;
					consumer.accept(splitter);
					bundlePacket.getPackets().forEach(consumer);
					consumer.accept(splitter);
				} else {
					consumer.accept(packet);
				}
			}

			@Nullable
			@Override
			public PacketBundleHandler.Bundler createBundler(Packet<?> splitter) {
				return splitter == splitter ? new PacketBundleHandler.Bundler() {
					private final List<Packet<T>> packets = new ArrayList();

					@Nullable
					@Override
					public Packet<?> add(Packet<?> packet) {
						if (packet == splitter) {
							return (Packet<?>)bundleFunction.apply(this.packets);
						} else if (this.packets.size() >= 4096) {
							throw new IllegalStateException("Too many packets in a bundle");
						} else {
							this.packets.add(packet);
							return null;
						}
					}
				} : null;
			}
		};
	}

	void forEachPacket(Packet<?> packet, Consumer<Packet<?>> consumer);

	@Nullable
	PacketBundleHandler.Bundler createBundler(Packet<?> splitter);

	public interface Bundler {
		@Nullable
		Packet<?> add(Packet<?> packet);
	}

	public interface BundlerGetter {
		PacketBundleHandler getBundler(NetworkSide side);
	}
}
