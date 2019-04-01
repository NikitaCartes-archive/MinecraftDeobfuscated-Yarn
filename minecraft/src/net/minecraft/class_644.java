package net.minecraft;

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
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class class_644 {
	private static final Splitter field_3770 = Splitter.on('\u0000').limit(6);
	private static final Logger field_3771 = LogManager.getLogger();
	private final List<class_2535> field_3769 = Collections.synchronizedList(Lists.newArrayList());

	public void method_3003(class_642 arg) throws UnknownHostException {
		class_639 lv = class_639.method_2950(arg.field_3761);
		final class_2535 lv2 = class_2535.method_10753(InetAddress.getByName(lv.method_2952()), lv.method_2954(), false);
		this.field_3769.add(lv2);
		arg.field_3757 = class_1074.method_4662("multiplayer.status.pinging");
		arg.field_3758 = -1L;
		arg.field_3762 = null;
		lv2.method_10763(
			new class_2921() {
				private boolean field_3775;
				private boolean field_3773;
				private long field_3772;

				@Override
				public void method_12667(class_2924 arg) {
					if (this.field_3773) {
						lv2.method_10747(new class_2588("multiplayer.status.unrequested"));
					} else {
						this.field_3773 = true;
						class_2926 lv = arg.method_12672();
						if (lv.method_12680() != null) {
							arg.field_3757 = lv.method_12680().method_10863();
						} else {
							arg.field_3757 = "";
						}

						if (lv.method_12683() != null) {
							arg.field_3760 = lv.method_12683().method_12693();
							arg.field_3756 = lv.method_12683().method_12694();
						} else {
							arg.field_3760 = class_1074.method_4662("multiplayer.status.old");
							arg.field_3756 = 0;
						}

						if (lv.method_12682() != null) {
							arg.field_3753 = class_124.field_1080
								+ ""
								+ lv.method_12682().method_12688()
								+ ""
								+ class_124.field_1063
								+ "/"
								+ class_124.field_1080
								+ lv.method_12682().method_12687();
							if (ArrayUtils.isNotEmpty(lv.method_12682().method_12685())) {
								StringBuilder stringBuilder = new StringBuilder();

								for (GameProfile gameProfile : lv.method_12682().method_12685()) {
									if (stringBuilder.length() > 0) {
										stringBuilder.append("\n");
									}

									stringBuilder.append(gameProfile.getName());
								}

								if (lv.method_12682().method_12685().length < lv.method_12682().method_12688()) {
									if (stringBuilder.length() > 0) {
										stringBuilder.append("\n");
									}

									stringBuilder.append(class_1074.method_4662("multiplayer.status.and_more", lv.method_12682().method_12688() - lv.method_12682().method_12685().length));
								}

								arg.field_3762 = stringBuilder.toString();
							}
						} else {
							arg.field_3753 = class_124.field_1063 + class_1074.method_4662("multiplayer.status.unknown");
						}

						if (lv.method_12678() != null) {
							String string = lv.method_12678();
							if (string.startsWith("data:image/png;base64,")) {
								arg.method_2989(string.substring("data:image/png;base64,".length()));
							} else {
								class_644.field_3771.error("Invalid server icon (unknown format)");
							}
						} else {
							arg.method_2989(null);
						}

						this.field_3772 = class_156.method_658();
						lv2.method_10743(new class_2935(this.field_3772));
						this.field_3775 = true;
					}
				}

				@Override
				public void method_12666(class_2923 arg) {
					long l = this.field_3772;
					long m = class_156.method_658();
					arg.field_3758 = m - l;
					lv2.method_10747(new class_2588("multiplayer.status.finished"));
				}

				@Override
				public void method_10839(class_2561 arg) {
					if (!this.field_3775) {
						class_644.field_3771.error("Can't ping {}: {}", arg.field_3761, arg.getString());
						arg.field_3757 = class_124.field_1079 + class_1074.method_4662("multiplayer.status.cannot_connect");
						arg.field_3753 = "";
						class_644.this.method_3001(arg);
					}
				}
			}
		);

		try {
			lv2.method_10743(new class_2889(lv.method_2952(), lv.method_2954(), class_2539.field_11691));
			lv2.method_10743(new class_2937());
		} catch (Throwable var5) {
			field_3771.error(var5);
		}
	}

	private void method_3001(class_642 arg) {
		final class_639 lv = class_639.method_2950(arg.field_3761);
		new Bootstrap().group(class_2535.field_11650.method_15332()).handler(new ChannelInitializer<Channel>() {
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

							byteBuf.writeShort(7 + 2 * lv.method_2952().length());
							byteBuf.writeByte(127);
							cs = lv.method_2952().toCharArray();
							byteBuf.writeShort(cs.length);

							for (char c : cs) {
								byteBuf.writeChar(c);
							}

							byteBuf.writeInt(lv.method_2954());
							channelHandlerContext.channel().writeAndFlush(byteBuf).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
						} finally {
							byteBuf.release();
						}
					}

					protected void method_3005(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf) throws Exception {
						short s = byteBuf.readUnsignedByte();
						if (s == 255) {
							String string = new String(byteBuf.readBytes(byteBuf.readShort() * 2).array(), StandardCharsets.UTF_16BE);
							String[] strings = Iterables.toArray(class_644.field_3770.split(string), String.class);
							if ("§1".equals(strings[0])) {
								int i = class_3532.method_15343(strings[1], 0);
								String string2 = strings[2];
								String string3 = strings[3];
								int j = class_3532.method_15343(strings[4], -1);
								int k = class_3532.method_15343(strings[5], -1);
								arg.field_3756 = -1;
								arg.field_3760 = string2;
								arg.field_3757 = string3;
								arg.field_3753 = class_124.field_1080 + "" + j + "" + class_124.field_1063 + "/" + class_124.field_1080 + k;
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
		}).channel(NioSocketChannel.class).connect(lv.method_2952(), lv.method_2954());
	}

	public void method_3000() {
		synchronized (this.field_3769) {
			Iterator<class_2535> iterator = this.field_3769.iterator();

			while (iterator.hasNext()) {
				class_2535 lv = (class_2535)iterator.next();
				if (lv.method_10758()) {
					lv.method_10754();
				} else {
					iterator.remove();
					lv.method_10768();
				}
			}
		}
	}

	public void method_3004() {
		synchronized (this.field_3769) {
			Iterator<class_2535> iterator = this.field_3769.iterator();

			while (iterator.hasNext()) {
				class_2535 lv = (class_2535)iterator.next();
				if (lv.method_10758()) {
					iterator.remove();
					lv.method_10747(new class_2588("multiplayer.status.cancelled"));
				}
			}
		}
	}
}
