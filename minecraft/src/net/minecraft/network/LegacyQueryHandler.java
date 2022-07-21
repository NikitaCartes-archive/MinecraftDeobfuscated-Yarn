package net.minecraft.network;

import com.mojang.logging.LogUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerNetworkIo;
import org.slf4j.Logger;

public class LegacyQueryHandler extends ChannelInboundHandlerAdapter {
	private static final Logger LOGGER = LogUtils.getLogger();
	public static final int field_29771 = 127;
	private final ServerNetworkIo networkIo;

	public LegacyQueryHandler(ServerNetworkIo networkIo) {
		this.networkIo = networkIo;
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

				InetSocketAddress inetSocketAddress = (InetSocketAddress)ctx.channel().remoteAddress();
				MinecraftServer minecraftServer = this.networkIo.getServer();
				int i = byteBuf.readableBytes();
				switch (i) {
					case 0: {
						LOGGER.debug("Ping: (<1.3.x) from {}:{}", inetSocketAddress.getAddress(), inetSocketAddress.getPort());
						String string = String.format(
							Locale.ROOT, "%s§%d§%d", minecraftServer.getServerMotd(), minecraftServer.getCurrentPlayerCount(), minecraftServer.getMaxPlayerCount()
						);
						this.reply(ctx, this.toBuffer(string));
						break;
					}
					case 1: {
						if (byteBuf.readUnsignedByte() != 1) {
							return;
						}

						LOGGER.debug("Ping: (1.4-1.5.x) from {}:{}", inetSocketAddress.getAddress(), inetSocketAddress.getPort());
						String string = String.format(
							Locale.ROOT,
							"§1\u0000%d\u0000%s\u0000%s\u0000%d\u0000%d",
							127,
							minecraftServer.getVersion(),
							minecraftServer.getServerMotd(),
							minecraftServer.getCurrentPlayerCount(),
							minecraftServer.getMaxPlayerCount()
						);
						this.reply(ctx, this.toBuffer(string));
						break;
					}
					default:
						boolean bl2 = byteBuf.readUnsignedByte() == 1;
						bl2 &= byteBuf.readUnsignedByte() == 250;
						bl2 &= "MC|PingHost".equals(new String(byteBuf.readBytes(byteBuf.readShort() * 2).array(), StandardCharsets.UTF_16BE));
						int j = byteBuf.readUnsignedShort();
						bl2 &= byteBuf.readUnsignedByte() >= 73;
						bl2 &= 3 + byteBuf.readBytes(byteBuf.readShort() * 2).array().length + 4 == j;
						bl2 &= byteBuf.readInt() <= 65535;
						bl2 &= byteBuf.readableBytes() == 0;
						if (!bl2) {
							return;
						}

						LOGGER.debug("Ping: (1.6) from {}:{}", inetSocketAddress.getAddress(), inetSocketAddress.getPort());
						String string2 = String.format(
							Locale.ROOT,
							"§1\u0000%d\u0000%s\u0000%s\u0000%d\u0000%d",
							127,
							minecraftServer.getVersion(),
							minecraftServer.getServerMotd(),
							minecraftServer.getCurrentPlayerCount(),
							minecraftServer.getMaxPlayerCount()
						);
						ByteBuf byteBuf2 = this.toBuffer(string2);

						try {
							this.reply(ctx, byteBuf2);
						} finally {
							byteBuf2.release();
						}
				}

				byteBuf.release();
				bl = false;
			} catch (RuntimeException var21) {
			}
		} finally {
			if (bl) {
				byteBuf.resetReaderIndex();
				ctx.channel().pipeline().remove("legacy_query");
				ctx.fireChannelRead(msg);
			}
		}
	}

	private void reply(ChannelHandlerContext ctx, ByteBuf buf) {
		ctx.pipeline().firstContext().writeAndFlush(buf).addListener(ChannelFutureListener.CLOSE);
	}

	private ByteBuf toBuffer(String s) {
		ByteBuf byteBuf = Unpooled.buffer();
		byteBuf.writeByte(255);
		char[] cs = s.toCharArray();
		byteBuf.writeShort(cs.length);

		for (char c : cs) {
			byteBuf.writeChar(c);
		}

		return byteBuf;
	}
}
