package net.minecraft.client.network;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.nio.NioSocketChannel;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.packet.QueryPongS2CPacket;
import net.minecraft.client.network.packet.QueryResponseS2CPacket;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkState;
import net.minecraft.network.ServerAddress;
import net.minecraft.network.listener.ClientQueryPacketListener;
import net.minecraft.server.ServerMetadata;
import net.minecraft.server.network.packet.HandshakeC2SPacket;
import net.minecraft.server.network.packet.QueryPingC2SPacket;
import net.minecraft.server.network.packet.QueryRequestC2SPacket;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.math.MathHelper;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class MultiplayerServerListPinger {
	private static final Splitter ZERO_SPLITTER = Splitter.on('\u0000').limit(6);
	private static final Logger LOGGER = LogManager.getLogger();
	private final List<ClientConnection> clientConnections = Collections.synchronizedList(Lists.newArrayList());

	public void add(ServerInfo serverInfo) throws UnknownHostException {
		ServerAddress serverAddress = ServerAddress.parse(serverInfo.address);
		final ClientConnection clientConnection = ClientConnection.connect(InetAddress.getByName(serverAddress.getAddress()), serverAddress.getPort(), false);
		this.clientConnections.add(clientConnection);
		serverInfo.label = I18n.translate("multiplayer.status.pinging");
		serverInfo.ping = -1L;
		serverInfo.playerListSummary = null;
		clientConnection.setPacketListener(
			new ClientQueryPacketListener() {
				private boolean sentQuery;
				private boolean received;
				private long startTime;

				@Override
				public void onResponse(QueryResponseS2CPacket queryResponseS2CPacket) {
					if (this.received) {
						clientConnection.disconnect(new TranslatableText("multiplayer.status.unrequested"));
					} else {
						this.received = true;
						ServerMetadata serverMetadata = queryResponseS2CPacket.getServerMetadata();
						if (serverMetadata.getDescription() != null) {
							serverInfo.label = serverMetadata.getDescription().asFormattedString();
						} else {
							serverInfo.label = "";
						}

						if (serverMetadata.getVersion() != null) {
							serverInfo.version = serverMetadata.getVersion().getGameVersion();
							serverInfo.protocolVersion = serverMetadata.getVersion().getProtocolVersion();
						} else {
							serverInfo.version = I18n.translate("multiplayer.status.old");
							serverInfo.protocolVersion = 0;
						}

						if (serverMetadata.getPlayers() != null) {
							serverInfo.playerCountLabel = Formatting.GRAY
								+ ""
								+ serverMetadata.getPlayers().getOnlinePlayerCount()
								+ ""
								+ Formatting.DARK_GRAY
								+ "/"
								+ Formatting.GRAY
								+ serverMetadata.getPlayers().getPlayerLimit();
							if (ArrayUtils.isNotEmpty(serverMetadata.getPlayers().getSample())) {
								StringBuilder stringBuilder = new StringBuilder();

								for (GameProfile gameProfile : serverMetadata.getPlayers().getSample()) {
									if (stringBuilder.length() > 0) {
										stringBuilder.append("\n");
									}

									stringBuilder.append(gameProfile.getName());
								}

								if (serverMetadata.getPlayers().getSample().length < serverMetadata.getPlayers().getOnlinePlayerCount()) {
									if (stringBuilder.length() > 0) {
										stringBuilder.append("\n");
									}

									stringBuilder.append(
										I18n.translate("multiplayer.status.and_more", serverMetadata.getPlayers().getOnlinePlayerCount() - serverMetadata.getPlayers().getSample().length)
									);
								}

								serverInfo.playerListSummary = stringBuilder.toString();
							}
						} else {
							serverInfo.playerCountLabel = Formatting.DARK_GRAY + I18n.translate("multiplayer.status.unknown");
						}

						if (serverMetadata.getFavicon() != null) {
							String string = serverMetadata.getFavicon();
							if (string.startsWith("data:image/png;base64,")) {
								serverInfo.setIcon(string.substring("data:image/png;base64,".length()));
							} else {
								MultiplayerServerListPinger.LOGGER.error("Invalid server icon (unknown format)");
							}
						} else {
							serverInfo.setIcon(null);
						}

						this.startTime = SystemUtil.getMeasuringTimeMs();
						clientConnection.send(new QueryPingC2SPacket(this.startTime));
						this.sentQuery = true;
					}
				}

				@Override
				public void onPong(QueryPongS2CPacket queryPongS2CPacket) {
					long l = this.startTime;
					long m = SystemUtil.getMeasuringTimeMs();
					serverInfo.ping = m - l;
					clientConnection.disconnect(new TranslatableText("multiplayer.status.finished"));
				}

				@Override
				public void onDisconnected(Text text) {
					if (!this.sentQuery) {
						MultiplayerServerListPinger.LOGGER.error("Can't ping {}: {}", serverInfo.address, text.getString());
						serverInfo.label = Formatting.DARK_RED + I18n.translate("multiplayer.status.cannot_connect");
						serverInfo.playerCountLabel = "";
						MultiplayerServerListPinger.this.ping(serverInfo);
					}
				}

				@Override
				public ClientConnection getConnection() {
					return clientConnection;
				}
			}
		);

		try {
			clientConnection.send(new HandshakeC2SPacket(serverAddress.getAddress(), serverAddress.getPort(), NetworkState.STATUS));
			clientConnection.send(new QueryRequestC2SPacket());
		} catch (Throwable var5) {
			LOGGER.error(var5);
		}
	}

	private void ping(ServerInfo serverInfo) {
		final ServerAddress serverAddress = ServerAddress.parse(serverInfo.address);
		new Bootstrap().group(ClientConnection.CLIENT_IO_GROUP.get()).handler(new ChannelInitializer<Channel>() {
			@Override
			protected void initChannel(Channel channel) throws Exception {
				try {
					channel.config().setOption(ChannelOption.TCP_NODELAY, true);
				} catch (ChannelException var3) {
				}

				channel.pipeline().addLast(new SimpleChannelInboundHandler<ByteBuf>() {
					@Override
					public void channelActive(ChannelHandlerContext channelHandlerContext) throws Exception {
						super.channelActive(channelHandlerContext);
						ByteBuf byteBuf = Unpooled.buffer();

						try {
							byteBuf.writeByte(254);
							byteBuf.writeByte(1);
							byteBuf.writeByte(250);
							char[] cs = "MC|PingHost".toCharArray();
							byteBuf.writeShort(cs.length);

							for (char c : cs) {
								byteBuf.writeChar(c);
							}

							byteBuf.writeShort(7 + 2 * serverAddress.getAddress().length());
							byteBuf.writeByte(127);
							cs = serverAddress.getAddress().toCharArray();
							byteBuf.writeShort(cs.length);

							for (char c : cs) {
								byteBuf.writeChar(c);
							}

							byteBuf.writeInt(serverAddress.getPort());
							channelHandlerContext.channel().writeAndFlush(byteBuf).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
						} finally {
							byteBuf.release();
						}
					}

					protected void method_3005(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf) throws Exception {
						short s = byteBuf.readUnsignedByte();
						if (s == 255) {
							String string = new String(byteBuf.readBytes(byteBuf.readShort() * 2).array(), StandardCharsets.UTF_16BE);
							String[] strings = Iterables.toArray(MultiplayerServerListPinger.ZERO_SPLITTER.split(string), String.class);
							if ("§1".equals(strings[0])) {
								int i = MathHelper.parseInt(strings[1], 0);
								String string2 = strings[2];
								String string3 = strings[3];
								int j = MathHelper.parseInt(strings[4], -1);
								int k = MathHelper.parseInt(strings[5], -1);
								serverInfo.protocolVersion = -1;
								serverInfo.version = string2;
								serverInfo.label = string3;
								serverInfo.playerCountLabel = Formatting.GRAY + "" + j + "" + Formatting.DARK_GRAY + "/" + Formatting.GRAY + k;
							}
						}

						channelHandlerContext.close();
					}

					@Override
					public void exceptionCaught(ChannelHandlerContext channelHandlerContext, Throwable throwable) throws Exception {
						channelHandlerContext.close();
					}
				});
			}
		}).channel(NioSocketChannel.class).connect(serverAddress.getAddress(), serverAddress.getPort());
	}

	public void tick() {
		synchronized (this.clientConnections) {
			Iterator<ClientConnection> iterator = this.clientConnections.iterator();

			while (iterator.hasNext()) {
				ClientConnection clientConnection = (ClientConnection)iterator.next();
				if (clientConnection.isOpen()) {
					clientConnection.tick();
				} else {
					iterator.remove();
					clientConnection.handleDisconnection();
				}
			}
		}
	}

	public void cancel() {
		synchronized (this.clientConnections) {
			Iterator<ClientConnection> iterator = this.clientConnections.iterator();

			while (iterator.hasNext()) {
				ClientConnection clientConnection = (ClientConnection)iterator.next();
				if (clientConnection.isOpen()) {
					iterator.remove();
					clientConnection.disconnect(new TranslatableText("multiplayer.status.cancelled"));
				}
			}
		}
	}
}
