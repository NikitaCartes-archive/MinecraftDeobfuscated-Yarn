package net.minecraft.client.network;

import com.google.common.base.Splitter;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.handler.LegacyQueries;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class LegacyServerPinger extends SimpleChannelInboundHandler<ByteBuf> {
	private static final Splitter SPLITTER = Splitter.on('\u0000').limit(6);
	private final ServerAddress serverAddress;
	private final LegacyServerPinger.ResponseHandler handler;

	public LegacyServerPinger(ServerAddress serverAddress, LegacyServerPinger.ResponseHandler handler) {
		this.serverAddress = serverAddress;
		this.handler = handler;
	}

	@Override
	public void channelActive(ChannelHandlerContext context) throws Exception {
		super.channelActive(context);
		ByteBuf byteBuf = context.alloc().buffer();

		try {
			byteBuf.writeByte(254);
			byteBuf.writeByte(1);
			byteBuf.writeByte(250);
			LegacyQueries.write(byteBuf, "MC|PingHost");
			int i = byteBuf.writerIndex();
			byteBuf.writeShort(0);
			int j = byteBuf.writerIndex();
			byteBuf.writeByte(127);
			LegacyQueries.write(byteBuf, this.serverAddress.getAddress());
			byteBuf.writeInt(this.serverAddress.getPort());
			int k = byteBuf.writerIndex() - j;
			byteBuf.setShort(i, k);
			context.channel().writeAndFlush(byteBuf).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
		} catch (Exception var6) {
			byteBuf.release();
			throw var6;
		}
	}

	protected void channelRead0(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf) {
		short s = byteBuf.readUnsignedByte();
		if (s == 255) {
			String string = LegacyQueries.read(byteBuf);
			List<String> list = SPLITTER.splitToList(string);
			if ("§1".equals(list.get(0))) {
				int i = MathHelper.parseInt((String)list.get(1), 0);
				String string2 = (String)list.get(2);
				String string3 = (String)list.get(3);
				int j = MathHelper.parseInt((String)list.get(4), -1);
				int k = MathHelper.parseInt((String)list.get(5), -1);
				this.handler.handleResponse(i, string2, string3, j, k);
			}
		}

		channelHandlerContext.close();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext context, Throwable throwable) {
		context.close();
	}

	@FunctionalInterface
	@Environment(EnvType.CLIENT)
	public interface ResponseHandler {
		void handleResponse(int protocolVersion, String version, String label, int currentPlayers, int maxPlayers);
	}
}
