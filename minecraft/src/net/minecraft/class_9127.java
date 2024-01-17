package net.minecraft;

import io.netty.buffer.ByteBuf;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.NetworkState;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.handler.PacketBundleHandler;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.Packet;

public interface class_9127<T extends PacketListener> {
	NetworkState id();

	NetworkSide flow();

	PacketCodec<ByteBuf, Packet<? super T>> codec();

	@Nullable
	PacketBundleHandler bundlerInfo();

	public interface class_9128<T extends PacketListener, B extends ByteBuf> {
		class_9127<T> bind(Function<ByteBuf, B> function);
	}
}
