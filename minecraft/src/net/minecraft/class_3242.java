package net.minecraft;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.local.LocalAddress;
import io.netty.channel.local.LocalServerChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_3242 {
	private static final Logger field_14110 = LogManager.getLogger();
	public static final class_3528<NioEventLoopGroup> field_14111 = new class_3528<>(
		() -> new NioEventLoopGroup(0, new ThreadFactoryBuilder().setNameFormat("Netty Server IO #%d").setDaemon(true).build())
	);
	public static final class_3528<EpollEventLoopGroup> field_14105 = new class_3528<>(
		() -> new EpollEventLoopGroup(0, new ThreadFactoryBuilder().setNameFormat("Netty Epoll Server IO #%d").setDaemon(true).build())
	);
	private final MinecraftServer field_14109;
	public volatile boolean field_14108;
	private final List<ChannelFuture> field_14106 = Collections.synchronizedList(Lists.newArrayList());
	private final List<class_2535> field_14107 = Collections.synchronizedList(Lists.newArrayList());

	public class_3242(MinecraftServer minecraftServer) {
		this.field_14109 = minecraftServer;
		this.field_14108 = true;
	}

	public void method_14354(@Nullable InetAddress inetAddress, int i) throws IOException {
		synchronized (this.field_14106) {
			Class<? extends ServerSocketChannel> class_;
			class_3528<? extends EventLoopGroup> lv;
			if (Epoll.isAvailable() && this.field_14109.method_3759()) {
				class_ = EpollServerSocketChannel.class;
				lv = field_14105;
				field_14110.info("Using epoll channel type");
			} else {
				class_ = NioServerSocketChannel.class;
				lv = field_14111;
				field_14110.info("Using default channel type");
			}

			this.field_14106
				.add(
					new ServerBootstrap()
						.channel(class_)
						.childHandler(
							new ChannelInitializer<Channel>() {
								@Override
								protected void initChannel(Channel channel) throws Exception {
									try {
										channel.config().setOption(ChannelOption.TCP_NODELAY, true);
									} catch (ChannelException var3) {
									}

									channel.pipeline()
										.addLast("timeout", new ReadTimeoutHandler(30))
										.addLast("legacy_query", new class_3238(class_3242.this))
										.addLast("splitter", new class_2550())
										.addLast("decoder", new class_2543(class_2598.field_11941))
										.addLast("prepender", new class_2552())
										.addLast("encoder", new class_2545(class_2598.field_11942));
									class_2535 lv = new class_2535(class_2598.field_11941);
									class_3242.this.field_14107.add(lv);
									channel.pipeline().addLast("packet_handler", lv);
									lv.method_10763(new class_3246(class_3242.this.field_14109, lv));
								}
							}
						)
						.group(lv.method_15332())
						.localAddress(inetAddress, i)
						.bind()
						.syncUninterruptibly()
				);
		}
	}

	@Environment(EnvType.CLIENT)
	public SocketAddress method_14353() {
		ChannelFuture channelFuture;
		synchronized (this.field_14106) {
			channelFuture = new ServerBootstrap().channel(LocalServerChannel.class).childHandler(new ChannelInitializer<Channel>() {
				@Override
				protected void initChannel(Channel channel) throws Exception {
					class_2535 lv = new class_2535(class_2598.field_11941);
					lv.method_10763(new class_3240(class_3242.this.field_14109, lv));
					class_3242.this.field_14107.add(lv);
					channel.pipeline().addLast("packet_handler", lv);
				}
			}).group(field_14111.method_15332()).localAddress(LocalAddress.ANY).bind().syncUninterruptibly();
			this.field_14106.add(channelFuture);
		}

		return channelFuture.channel().localAddress();
	}

	public void method_14356() {
		this.field_14108 = false;

		for (ChannelFuture channelFuture : this.field_14106) {
			try {
				channelFuture.channel().close().sync();
			} catch (InterruptedException var4) {
				field_14110.error("Interrupted whilst closing channel");
			}
		}
	}

	public void method_14357() {
		synchronized (this.field_14107) {
			Iterator<class_2535> iterator = this.field_14107.iterator();

			while (iterator.hasNext()) {
				class_2535 lv = (class_2535)iterator.next();
				if (!lv.method_10772()) {
					if (lv.method_10758()) {
						try {
							lv.method_10754();
						} catch (Exception var8) {
							if (lv.method_10756()) {
								class_128 lv2 = class_128.method_560(var8, "Ticking memory connection");
								class_129 lv3 = lv2.method_562("Ticking connection");
								lv3.method_577("Connection", lv::toString);
								throw new class_148(lv2);
							}

							field_14110.warn("Failed to handle packet for {}", lv.method_10755(), var8);
							class_2561 lv4 = new class_2585("Internal server error");
							lv.method_10752(new class_2661(lv4), future -> lv.method_10747(lv4));
							lv.method_10757();
						}
					} else {
						iterator.remove();
						lv.method_10768();
					}
				}
			}
		}
	}

	public MinecraftServer method_14351() {
		return this.field_14109;
	}
}
