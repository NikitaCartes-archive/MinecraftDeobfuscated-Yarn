package net.minecraft.network;

import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.handler.PacketBundleHandler;
import net.minecraft.network.handler.SideValidatingDispatchingCodecBuilder;
import net.minecraft.network.listener.ClientPacketListener;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.listener.ServerPacketListener;
import net.minecraft.network.packet.BundlePacket;
import net.minecraft.network.packet.BundleSplitterPacket;
import net.minecraft.network.packet.Packet;

public class NetworkStateBuilder<T extends PacketListener, B extends ByteBuf> {
	private final NetworkPhase type;
	private final NetworkSide side;
	private final List<NetworkStateBuilder.PacketType<T, ?, B>> packetTypes = new ArrayList();
	@Nullable
	private PacketBundleHandler bundleHandler;

	public NetworkStateBuilder(NetworkPhase type, NetworkSide side) {
		this.type = type;
		this.side = side;
	}

	public <P extends Packet<? super T>> NetworkStateBuilder<T, B> add(net.minecraft.network.packet.PacketType<P> id, PacketCodec<? super B, P> codec) {
		this.packetTypes.add(new NetworkStateBuilder.PacketType<>(id, codec));
		return this;
	}

	public <P extends BundlePacket<? super T>, D extends BundleSplitterPacket<? super T>> NetworkStateBuilder<T, B> addBundle(
		net.minecraft.network.packet.PacketType<P> id, Function<Iterable<Packet<? super T>>, P> bundler, D splitter
	) {
		PacketCodec<ByteBuf, D> packetCodec = PacketCodec.unit(splitter);
		net.minecraft.network.packet.PacketType<D> packetType = (net.minecraft.network.packet.PacketType<D>)splitter.getPacketId();
		this.packetTypes.add(new NetworkStateBuilder.PacketType<>(packetType, packetCodec));
		this.bundleHandler = PacketBundleHandler.create(id, bundler, splitter);
		return this;
	}

	private PacketCodec<ByteBuf, Packet<? super T>> createCodec(Function<ByteBuf, B> bufUpgrader, List<NetworkStateBuilder.PacketType<T, ?, B>> packetTypes) {
		SideValidatingDispatchingCodecBuilder<ByteBuf, T> sideValidatingDispatchingCodecBuilder = new SideValidatingDispatchingCodecBuilder<>(this.side);

		for (NetworkStateBuilder.PacketType<T, ?, B> packetType : packetTypes) {
			packetType.add(sideValidatingDispatchingCodecBuilder, bufUpgrader);
		}

		return sideValidatingDispatchingCodecBuilder.build();
	}

	public NetworkState<T> build(Function<ByteBuf, B> bufUpgrader) {
		return new NetworkStateBuilder.NetworkStateImpl<>(this.type, this.side, this.createCodec(bufUpgrader, this.packetTypes), this.bundleHandler);
	}

	public NetworkState.Factory<T, B> buildFactory() {
		List<NetworkStateBuilder.PacketType<T, ?, B>> list = List.copyOf(this.packetTypes);
		PacketBundleHandler packetBundleHandler = this.bundleHandler;
		return bufUpgrader -> new NetworkStateBuilder.NetworkStateImpl<>(this.type, this.side, this.createCodec(bufUpgrader, list), packetBundleHandler);
	}

	private static <L extends PacketListener> NetworkState<L> build(NetworkPhase type, NetworkSide side, Consumer<NetworkStateBuilder<L, PacketByteBuf>> registrar) {
		NetworkStateBuilder<L, PacketByteBuf> networkStateBuilder = new NetworkStateBuilder<>(type, side);
		registrar.accept(networkStateBuilder);
		return networkStateBuilder.build(PacketByteBuf::new);
	}

	public static <T extends ServerPacketListener> NetworkState<T> c2s(NetworkPhase type, Consumer<NetworkStateBuilder<T, PacketByteBuf>> registrar) {
		return build(type, NetworkSide.SERVERBOUND, registrar);
	}

	public static <T extends ClientPacketListener> NetworkState<T> s2c(NetworkPhase type, Consumer<NetworkStateBuilder<T, PacketByteBuf>> registrar) {
		return build(type, NetworkSide.CLIENTBOUND, registrar);
	}

	private static <L extends PacketListener, B extends ByteBuf> NetworkState.Factory<L, B> buildFactory(
		NetworkPhase type, NetworkSide side, Consumer<NetworkStateBuilder<L, B>> registrar
	) {
		NetworkStateBuilder<L, B> networkStateBuilder = new NetworkStateBuilder<>(type, side);
		registrar.accept(networkStateBuilder);
		return networkStateBuilder.buildFactory();
	}

	public static <T extends ServerPacketListener, B extends ByteBuf> NetworkState.Factory<T, B> c2sFactory(
		NetworkPhase type, Consumer<NetworkStateBuilder<T, B>> registrar
	) {
		return buildFactory(type, NetworkSide.SERVERBOUND, registrar);
	}

	public static <T extends ClientPacketListener, B extends ByteBuf> NetworkState.Factory<T, B> s2cFactory(
		NetworkPhase type, Consumer<NetworkStateBuilder<T, B>> registrar
	) {
		return buildFactory(type, NetworkSide.CLIENTBOUND, registrar);
	}

	static record NetworkStateImpl<L extends PacketListener>(
		NetworkPhase id, NetworkSide side, PacketCodec<ByteBuf, Packet<? super L>> codec, @Nullable PacketBundleHandler bundleHandler
	) implements NetworkState<L> {
	}

	static record PacketType<T extends PacketListener, P extends Packet<? super T>, B extends ByteBuf>(
		net.minecraft.network.packet.PacketType<P> id, PacketCodec<? super B, P> codec
	) {
		public void add(SideValidatingDispatchingCodecBuilder<ByteBuf, T> builder, Function<ByteBuf, B> bufUpgrader) {
			PacketCodec<ByteBuf, P> packetCodec = this.codec.mapBuf(bufUpgrader);
			builder.add(this.id, packetCodec);
		}
	}
}
