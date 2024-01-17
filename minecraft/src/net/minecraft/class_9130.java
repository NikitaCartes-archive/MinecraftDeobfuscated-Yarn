package net.minecraft;

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
import net.minecraft.network.handler.DecoderHandler;
import net.minecraft.network.handler.PacketEncoder;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.Packet;

public class class_9130 {
	public static <T extends PacketListener> class_9130.class_9132 method_56356(class_9127<T> arg) {
		return method_56352(new DecoderHandler<>(arg));
	}

	private static class_9130.class_9132 method_56352(ChannelInboundHandler channelInboundHandler) {
		return channelHandlerContext -> {
			channelHandlerContext.pipeline().replace(channelHandlerContext.name(), "decoder", channelInboundHandler);
			channelHandlerContext.channel().config().setAutoRead(true);
		};
	}

	public static <T extends PacketListener> class_9130.class_9134 method_56357(class_9127<T> arg) {
		return method_56354(new PacketEncoder<>(arg));
	}

	private static class_9130.class_9134 method_56354(ChannelOutboundHandler channelOutboundHandler) {
		return channelHandlerContext -> channelHandlerContext.pipeline().replace(channelHandlerContext.name(), "encoder", channelOutboundHandler);
	}

	public static class class_9131 extends ChannelDuplexHandler {
		@Override
		public void channelRead(ChannelHandlerContext channelHandlerContext, Object object) {
			if (!(object instanceof ByteBuf) && !(object instanceof Packet)) {
				channelHandlerContext.fireChannelRead(object);
			} else {
				ReferenceCountUtil.release(object);
				throw new DecoderException("Pipeline has no inbound protocol configured, can't process packet " + object);
			}
		}

		@Override
		public void write(ChannelHandlerContext channelHandlerContext, Object object, ChannelPromise channelPromise) throws Exception {
			if (object instanceof class_9130.class_9132 lv) {
				try {
					lv.run(channelHandlerContext);
				} finally {
					ReferenceCountUtil.release(object);
				}

				channelPromise.setSuccess();
			} else {
				channelHandlerContext.write(object, channelPromise);
			}
		}
	}

	@FunctionalInterface
	public interface class_9132 {
		void run(ChannelHandlerContext channelHandlerContext);

		default class_9130.class_9132 andThen(class_9130.class_9132 arg) {
			return channelHandlerContext -> {
				this.run(channelHandlerContext);
				arg.run(channelHandlerContext);
			};
		}
	}

	public static class class_9133 extends ChannelOutboundHandlerAdapter {
		@Override
		public void write(ChannelHandlerContext channelHandlerContext, Object object, ChannelPromise channelPromise) throws Exception {
			if (object instanceof Packet) {
				ReferenceCountUtil.release(object);
				throw new EncoderException("Pipeline has no outbound protocol configured, can't process packet " + object);
			} else {
				if (object instanceof class_9130.class_9134 lv) {
					try {
						lv.run(channelHandlerContext);
					} finally {
						ReferenceCountUtil.release(object);
					}

					channelPromise.setSuccess();
				} else {
					channelHandlerContext.write(object, channelPromise);
				}
			}
		}
	}

	@FunctionalInterface
	public interface class_9134 {
		void run(ChannelHandlerContext channelHandlerContext);

		default class_9130.class_9134 andThen(class_9130.class_9134 arg) {
			return channelHandlerContext -> {
				this.run(channelHandlerContext);
				arg.run(channelHandlerContext);
			};
		}
	}
}
