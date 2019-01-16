package net.minecraft.sortme;

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
import net.minecraft.client.network.packet.QueryPongClientPacket;
import net.minecraft.client.network.packet.QueryResponseClientPacket;
import net.minecraft.client.options.ServerEntry;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkState;
import net.minecraft.network.ServerAddress;
import net.minecraft.network.listener.ClientQueryPacketListener;
import net.minecraft.server.ServerMetadata;
import net.minecraft.server.network.packet.HandshakeServerPacket;
import net.minecraft.server.network.packet.QueryPingServerPacket;
import net.minecraft.server.network.packet.QueryRequestServerPacket;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TextFormat;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.math.MathHelper;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class ServerEntryNetworkPart {
	private static final Splitter ZERO_SPLITTER = Splitter.on('\u0000').limit(6);
	private static final Logger LOGGER = LogManager.getLogger();
	private final List<ClientConnection> clientConnections = Collections.synchronizedList(Lists.newArrayList());

	public void method_3003(ServerEntry serverEntry) throws UnknownHostException {
		ServerAddress serverAddress = ServerAddress.parse(serverEntry.address);
		final ClientConnection clientConnection = ClientConnection.connect(InetAddress.getByName(serverAddress.getAddress()), serverAddress.getPort(), false);
		this.clientConnections.add(clientConnection);
		serverEntry.label = I18n.translate("multiplayer.status.pinging");
		serverEntry.ping = -1L;
		serverEntry.playerListSummary = null;
		clientConnection.setPacketListener(
			new ClientQueryPacketListener() {
				private boolean field_3775;
				private boolean field_3773;
				private long field_3772;

				@Override
				public void onResponse(QueryResponseClientPacket queryResponseClientPacket) {
					if (this.field_3773) {
						clientConnection.disconnect(new TranslatableTextComponent("multiplayer.status.unrequested"));
					} else {
						this.field_3773 = true;
						ServerMetadata serverMetadata = queryResponseClientPacket.getServerMetadata();
						if (serverMetadata.getDescription() != null) {
							serverEntry.label = serverMetadata.getDescription().getFormattedText();
						} else {
							serverEntry.label = "";
						}

						if (serverMetadata.getVersion() != null) {
							serverEntry.version = serverMetadata.getVersion().getGameVersion();
							serverEntry.protocolVersion = serverMetadata.getVersion().getProtocolVersion();
						} else {
							serverEntry.version = I18n.translate("multiplayer.status.old");
							serverEntry.protocolVersion = 0;
						}

						if (serverMetadata.getPlayers() != null) {
							serverEntry.playerCountLabel = TextFormat.GRAY
								+ ""
								+ serverMetadata.getPlayers().getOnlinePlayerCount()
								+ ""
								+ TextFormat.DARK_GRAY
								+ "/"
								+ TextFormat.GRAY
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

								serverEntry.playerListSummary = stringBuilder.toString();
							}
						} else {
							serverEntry.playerCountLabel = TextFormat.DARK_GRAY + I18n.translate("multiplayer.status.unknown");
						}

						if (serverMetadata.getFavicon() != null) {
							String string = serverMetadata.getFavicon();
							if (string.startsWith("data:image/png;base64,")) {
								serverEntry.setIcon(string.substring("data:image/png;base64,".length()));
							} else {
								ServerEntryNetworkPart.LOGGER.error("Invalid server icon (unknown format)");
							}
						} else {
							serverEntry.setIcon(null);
						}

						this.field_3772 = SystemUtil.getMeasuringTimeMs();
						clientConnection.sendPacket(new QueryPingServerPacket(this.field_3772));
						this.field_3775 = true;
					}
				}

				@Override
				public void onPong(QueryPongClientPacket queryPongClientPacket) {
					long l = this.field_3772;
					long m = SystemUtil.getMeasuringTimeMs();
					serverEntry.ping = m - l;
					clientConnection.disconnect(new TranslatableTextComponent("multiplayer.status.finished"));
				}

				@Override
				public void onConnectionLost(TextComponent textComponent) {
					if (!this.field_3775) {
						ServerEntryNetworkPart.LOGGER.error("Can't ping {}: {}", serverEntry.address, textComponent.getString());
						serverEntry.label = TextFormat.DARK_RED + I18n.translate("multiplayer.status.cannot_connect");
						serverEntry.playerCountLabel = "";
						ServerEntryNetworkPart.this.ping(serverEntry);
					}
				}
			}
		);

		try {
			clientConnection.sendPacket(new HandshakeServerPacket(serverAddress.getAddress(), serverAddress.getPort(), NetworkState.QUERY));
			clientConnection.sendPacket(new QueryRequestServerPacket());
		} catch (Throwable var5) {
			LOGGER.error(var5);
		}
	}

	private void ping(ServerEntry serverEntry) {
		final ServerAddress serverAddress = ServerAddress.parse(serverEntry.address);
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
							String[] strings = Iterables.toArray(ServerEntryNetworkPart.ZERO_SPLITTER.split(string), String.class);
							if ("§1".equals(strings[0])) {
								int i = MathHelper.parseInt(strings[1], 0);
								String string2 = strings[2];
								String string3 = strings[3];
								int j = MathHelper.parseInt(strings[4], -1);
								int k = MathHelper.parseInt(strings[5], -1);
								serverEntry.protocolVersion = -1;
								serverEntry.version = string2;
								serverEntry.label = string3;
								serverEntry.playerCountLabel = TextFormat.GRAY + "" + j + "" + TextFormat.DARK_GRAY + "/" + TextFormat.GRAY + k;
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

	public void method_3000() {
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

	public void method_3004() {
		synchronized (this.clientConnections) {
			Iterator<ClientConnection> iterator = this.clientConnections.iterator();

			while (iterator.hasNext()) {
				ClientConnection clientConnection = (ClientConnection)iterator.next();
				if (clientConnection.isOpen()) {
					iterator.remove();
					clientConnection.disconnect(new TranslatableTextComponent("multiplayer.status.cancelled"));
				}
			}
		}
	}
}
