package net.minecraft;

import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.NetworkState;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.handler.PacketBundleHandler;
import net.minecraft.network.listener.ClientPacketListener;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.listener.ServerPacketListener;
import net.minecraft.network.packet.BundlePacket;
import net.minecraft.network.packet.BundleSplitterPacket;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketIdentifier;

public class class_9147<T extends PacketListener, B extends ByteBuf> {
	private final NetworkState field_48616;
	private final NetworkSide field_48617;
	private final List<class_9147.class_9148<T, ?, B>> field_48618 = new ArrayList();
	@Nullable
	private PacketBundleHandler field_48619;

	public class_9147(NetworkState networkState, NetworkSide networkSide) {
		this.field_48616 = networkState;
		this.field_48617 = networkSide;
	}

	public <P extends Packet<? super T>> class_9147<T, B> method_56454(PacketIdentifier<P> packetIdentifier, PacketCodec<? super B, P> packetCodec) {
		this.field_48618.add(new class_9147.class_9148<>(packetIdentifier, packetCodec));
		return this;
	}

	public <P extends BundlePacket<? super T>, D extends BundleSplitterPacket<? super T>> class_9147<T, B> method_56453(
		PacketIdentifier<P> packetIdentifier, Function<Iterable<Packet<? super T>>, P> function, D bundleSplitterPacket
	) {
		PacketCodec<ByteBuf, D> packetCodec = PacketCodec.unit(bundleSplitterPacket);
		PacketIdentifier<D> packetIdentifier2 = (PacketIdentifier<D>)bundleSplitterPacket.getPacketId();
		this.field_48618.add(new class_9147.class_9148<>(packetIdentifier2, packetCodec));
		this.field_48619 = PacketBundleHandler.create(packetIdentifier, function, bundleSplitterPacket);
		return this;
	}

	private PacketCodec<ByteBuf, Packet<? super T>> method_56450(Function<ByteBuf, B> function, List<class_9147.class_9148<T, ?, B>> list) {
		class_9146<ByteBuf, T> lv = new class_9146<>(this.field_48617);

		for (class_9147.class_9148<T, ?, B> lv2 : list) {
			lv2.method_56459(lv, function);
		}

		return lv.method_56445();
	}

	public class_9127<T> method_56449(Function<ByteBuf, B> function) {
		return new class_9147.class_9149<>(this.field_48616, this.field_48617, this.method_56450(function, this.field_48618), this.field_48619);
	}

	public class_9127.class_9128<T, B> method_56447() {
		List<class_9147.class_9148<T, ?, B>> list = List.copyOf(this.field_48618);
		PacketBundleHandler packetBundleHandler = this.field_48619;
		return function -> new class_9147.class_9149<>(this.field_48616, this.field_48617, this.method_56450(function, list), packetBundleHandler);
	}

	private static <L extends PacketListener> class_9127<L> method_56452(
		NetworkState networkState, NetworkSide networkSide, Consumer<class_9147<L, PacketByteBuf>> consumer
	) {
		class_9147<L, PacketByteBuf> lv = new class_9147<>(networkState, networkSide);
		consumer.accept(lv);
		return lv.method_56449(PacketByteBuf::new);
	}

	public static <T extends ServerPacketListener> class_9127<T> method_56451(NetworkState networkState, Consumer<class_9147<T, PacketByteBuf>> consumer) {
		return method_56452(networkState, NetworkSide.SERVERBOUND, consumer);
	}

	public static <T extends ClientPacketListener> class_9127<T> method_56455(NetworkState networkState, Consumer<class_9147<T, PacketByteBuf>> consumer) {
		return method_56452(networkState, NetworkSide.CLIENTBOUND, consumer);
	}

	private static <L extends PacketListener, B extends ByteBuf> class_9127.class_9128<L, B> method_56456(
		NetworkState networkState, NetworkSide networkSide, Consumer<class_9147<L, B>> consumer
	) {
		class_9147<L, B> lv = new class_9147<>(networkState, networkSide);
		consumer.accept(lv);
		return lv.method_56447();
	}

	public static <T extends ServerPacketListener, B extends ByteBuf> class_9127.class_9128<T, B> method_56457(
		NetworkState networkState, Consumer<class_9147<T, B>> consumer
	) {
		return method_56456(networkState, NetworkSide.SERVERBOUND, consumer);
	}

	public static <T extends ClientPacketListener, B extends ByteBuf> class_9127.class_9128<T, B> method_56458(
		NetworkState networkState, Consumer<class_9147<T, B>> consumer
	) {
		return method_56456(networkState, NetworkSide.CLIENTBOUND, consumer);
	}

	static record class_9148<T extends PacketListener, P extends Packet<? super T>, B extends ByteBuf>(
		PacketIdentifier<P> type, PacketCodec<? super B, P> serializer
	) {
		public void method_56459(class_9146<ByteBuf, T> arg, Function<ByteBuf, B> function) {
			PacketCodec<ByteBuf, P> packetCodec = this.serializer.mapBuf(function);
			arg.method_56446(this.type, packetCodec);
		}
	}

	static record class_9149<L extends PacketListener>(
		NetworkState id, NetworkSide flow, PacketCodec<ByteBuf, Packet<? super L>> codec, @Nullable PacketBundleHandler bundlerInfo
	) implements class_9127<L> {
	}
}
