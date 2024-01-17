package net.minecraft.network.handler;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.BundlePacket;
import net.minecraft.network.packet.BundleSplitterPacket;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketIdentifier;

public interface PacketBundleHandler {
	int MAX_PACKETS = 4096;

	static <T extends PacketListener, P extends BundlePacket<? super T>> PacketBundleHandler create(
		PacketIdentifier<P> packetIdentifier, Function<Iterable<Packet<? super T>>, P> bundleFunction, BundleSplitterPacket<? super T> splitter
	) {
		return new PacketBundleHandler() {
			@Override
			public void forEachPacket(Packet<?> packet, Consumer<Packet<?>> consumer) {
				if (packet.getPacketId() == packetIdentifier) {
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
					private final List<Packet<? super T>> packets = new ArrayList();

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
}
