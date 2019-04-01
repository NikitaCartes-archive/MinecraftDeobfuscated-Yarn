package net.minecraft;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import java.io.IOException;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public class class_2543 extends ByteToMessageDecoder {
	private static final Logger field_11715 = LogManager.getLogger();
	private static final Marker field_11713 = MarkerManager.getMarker("PACKET_RECEIVED", class_2535.field_11639);
	private final class_2598 field_11714;

	public class_2543(class_2598 arg) {
		this.field_11714 = arg;
	}

	@Override
	protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
		if (byteBuf.readableBytes() != 0) {
			class_2540 lv = new class_2540(byteBuf);
			int i = lv.method_10816();
			class_2596<?> lv2 = channelHandlerContext.channel().attr(class_2535.field_11648).get().method_10783(this.field_11714, i);
			if (lv2 == null) {
				throw new IOException("Bad packet id " + i);
			} else {
				lv2.method_11053(lv);
				if (lv.readableBytes() > 0) {
					throw new IOException(
						"Packet "
							+ channelHandlerContext.channel().attr(class_2535.field_11648).get().method_10785()
							+ "/"
							+ i
							+ " ("
							+ lv2.getClass().getSimpleName()
							+ ") was larger than I expected, found "
							+ lv.readableBytes()
							+ " bytes extra whilst reading packet "
							+ i
					);
				} else {
					list.add(lv2);
					if (field_11715.isDebugEnabled()) {
						field_11715.debug(field_11713, " IN: [{}:{}] {}", channelHandlerContext.channel().attr(class_2535.field_11648).get(), i, lv2.getClass().getName());
					}
				}
			}
		}
	}
}
