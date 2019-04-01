package net.minecraft;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import java.io.IOException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public class class_2545 extends MessageToByteEncoder<class_2596<?>> {
	private static final Logger field_11721 = LogManager.getLogger();
	private static final Marker field_11719 = MarkerManager.getMarker("PACKET_SENT", class_2535.field_11639);
	private final class_2598 field_11720;

	public class_2545(class_2598 arg) {
		this.field_11720 = arg;
	}

	protected void method_10838(ChannelHandlerContext channelHandlerContext, class_2596<?> arg, ByteBuf byteBuf) throws Exception {
		class_2539 lv = channelHandlerContext.channel().attr(class_2535.field_11648).get();
		if (lv == null) {
			throw new RuntimeException("ConnectionProtocol unknown: " + arg);
		} else {
			Integer integer = lv.method_10781(this.field_11720, arg);
			if (field_11721.isDebugEnabled()) {
				field_11721.debug(field_11719, "OUT: [{}:{}] {}", channelHandlerContext.channel().attr(class_2535.field_11648).get(), integer, arg.getClass().getName());
			}

			if (integer == null) {
				throw new IOException("Can't serialize unregistered packet");
			} else {
				class_2540 lv2 = new class_2540(byteBuf);
				lv2.method_10804(integer);

				try {
					arg.method_11052(lv2);
				} catch (Throwable var8) {
					field_11721.error(var8);
					if (arg.method_11051()) {
						throw new class_2548(var8);
					} else {
						throw var8;
					}
				}
			}
		}
	}
}
