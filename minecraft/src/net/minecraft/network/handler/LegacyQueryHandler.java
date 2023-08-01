package net.minecraft.network.handler;

import com.mojang.logging.LogUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import java.net.SocketAddress;
import java.util.Locale;
import net.minecraft.network.QueryableServer;
import org.slf4j.Logger;

public class LegacyQueryHandler extends ChannelInboundHandlerAdapter {
	private static final Logger LOGGER = LogUtils.getLogger();
	private final QueryableServer server;

	public LegacyQueryHandler(QueryableServer server) {
		this.server = server;
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) {
		ByteBuf byteBuf = (ByteBuf)msg;
		byteBuf.markReaderIndex();
		boolean bl = true;

		try {
			try {
				if (byteBuf.readUnsignedByte() != 254) {
					return;
				}

				SocketAddress socketAddress = ctx.channel().remoteAddress();
				int i = byteBuf.readableBytes();
				if (i == 0) {
					LOGGER.debug("Ping: (<1.3.x) from {}", socketAddress);
					String string = getResponseFor1_2(this.server);
					reply(ctx, createBuf(ctx.alloc(), string));
				} else {
					if (byteBuf.readUnsignedByte() != 1) {
						return;
					}

					if (byteBuf.isReadable()) {
						if (!isLegacyQuery(byteBuf)) {
							return;
						}

						LOGGER.debug("Ping: (1.6) from {}", socketAddress);
					} else {
						LOGGER.debug("Ping: (1.4-1.5.x) from {}", socketAddress);
					}

					String string = getResponse(this.server);
					reply(ctx, createBuf(ctx.alloc(), string));
				}

				byteBuf.release();
				bl = false;
			} catch (RuntimeException var11) {
			}
		} finally {
			if (bl) {
				byteBuf.resetReaderIndex();
				ctx.channel().pipeline().remove(this);
				ctx.fireChannelRead(msg);
			}
		}
	}

	private static boolean isLegacyQuery(ByteBuf buf) {
		short s = buf.readUnsignedByte();
		if (s != 250) {
			return false;
		} else {
			String string = LegacyQueries.read(buf);
			if (!"MC|PingHost".equals(string)) {
				return false;
			} else {
				int i = buf.readUnsignedShort();
				if (buf.readableBytes() != i) {
					return false;
				} else {
					short t = buf.readUnsignedByte();
					if (t < 73) {
						return false;
					} else {
						String string2 = LegacyQueries.read(buf);
						int j = buf.readInt();
						return j <= 65535;
					}
				}
			}
		}
	}

	private static String getResponseFor1_2(QueryableServer server) {
		return String.format(Locale.ROOT, "%s§%d§%d", server.getServerMotd(), server.getCurrentPlayerCount(), server.getMaxPlayerCount());
	}

	private static String getResponse(QueryableServer server) {
		return String.format(
			Locale.ROOT,
			"§1\u0000%d\u0000%s\u0000%s\u0000%d\u0000%d",
			127,
			server.getVersion(),
			server.getServerMotd(),
			server.getCurrentPlayerCount(),
			server.getMaxPlayerCount()
		);
	}

	private static void reply(ChannelHandlerContext context, ByteBuf buf) {
		context.pipeline().firstContext().writeAndFlush(buf).addListener(ChannelFutureListener.CLOSE);
	}

	private static ByteBuf createBuf(ByteBufAllocator allocator, String string) {
		ByteBuf byteBuf = allocator.buffer();
		byteBuf.writeByte(255);
		LegacyQueries.write(byteBuf, string);
		return byteBuf;
	}
}
