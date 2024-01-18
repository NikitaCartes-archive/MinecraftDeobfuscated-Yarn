package net.minecraft.network.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.ChannelOutboundHandler;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import io.netty.util.ReferenceCountUtil;
import net.minecraft.network.NetworkState;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.Packet;

public class NetworkStateTransitions {
	public static <T extends PacketListener> NetworkStateTransitions.DecoderTransitioner decoderTransitioner(NetworkState<T> newState) {
		return decoderSwapper(new DecoderHandler<>(newState));
	}

	private static NetworkStateTransitions.DecoderTransitioner decoderSwapper(ChannelInboundHandler newDecoder) {
		return context -> {
			context.pipeline().replace(context.name(), "decoder", newDecoder);
			context.channel().config().setAutoRead(true);
		};
	}

	public static <T extends PacketListener> NetworkStateTransitions.EncoderTransitioner encoderTransitioner(NetworkState<T> newState) {
		return encoderSwapper(new EncoderHandler<>(newState));
	}

	private static NetworkStateTransitions.EncoderTransitioner encoderSwapper(ChannelOutboundHandler newEncoder) {
		return context -> context.pipeline().replace(context.name(), "encoder", newEncoder);
	}

	@FunctionalInterface
	public interface DecoderTransitioner {
		void run(ChannelHandlerContext context);

		default NetworkStateTransitions.DecoderTransitioner andThen(NetworkStateTransitions.DecoderTransitioner decoderTransitioner) {
			return context -> {
				this.run(context);
				decoderTransitioner.run(context);
			};
		}
	}

	@FunctionalInterface
	public interface EncoderTransitioner {
		void run(ChannelHandlerContext context);

		default NetworkStateTransitions.EncoderTransitioner andThen(NetworkStateTransitions.EncoderTransitioner encoderTransitioner) {
			return context -> {
				this.run(context);
				encoderTransitioner.run(context);
			};
		}
	}

	public static class InboundConfigurer extends ChannelDuplexHandler {
		@Override
		public void channelRead(ChannelHandlerContext context, Object received) {
			if (!(received instanceof ByteBuf) && !(received instanceof Packet)) {
				context.fireChannelRead(received);
			} else {
				ReferenceCountUtil.release(received);
				throw new DecoderException("Pipeline has no inbound protocol configured, can't process packet " + received);
			}
		}

		@Override
		public void write(ChannelHandlerContext context, Object received, ChannelPromise promise) throws Exception {
			if (received instanceof NetworkStateTransitions.DecoderTransitioner decoderTransitioner) {
				try {
					decoderTransitioner.run(context);
				} finally {
					ReferenceCountUtil.release(received);
				}

				promise.setSuccess();
			} else {
				context.write(received, promise);
			}
		}
	}

	public static class OutboundConfigurer extends ChannelOutboundHandlerAdapter {
		@Override
		public void write(ChannelHandlerContext context, Object received, ChannelPromise promise) throws Exception {
			if (received instanceof Packet) {
				ReferenceCountUtil.release(received);
				throw new EncoderException("Pipeline has no outbound protocol configured, can't process packet " + received);
			} else {
				if (received instanceof NetworkStateTransitions.EncoderTransitioner encoderTransitioner) {
					try {
						encoderTransitioner.run(context);
					} finally {
						ReferenceCountUtil.release(received);
					}

					promise.setSuccess();
				} else {
					context.write(received, promise);
				}
			}
		}
	}
}
