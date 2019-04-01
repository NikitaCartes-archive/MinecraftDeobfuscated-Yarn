package net.minecraft;

import com.google.common.collect.Queues;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.DefaultEventLoopGroup;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.local.LocalChannel;
import io.netty.channel.local.LocalServerChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.TimeoutException;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.util.Queue;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import javax.annotation.Nullable;
import javax.crypto.SecretKey;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public class class_2535 extends SimpleChannelInboundHandler<class_2596<?>> {
	private static final Logger field_11642 = LogManager.getLogger();
	public static final Marker field_11641 = MarkerManager.getMarker("NETWORK");
	public static final Marker field_11639 = MarkerManager.getMarker("NETWORK_PACKETS", field_11641);
	public static final AttributeKey<class_2539> field_11648 = AttributeKey.valueOf("protocol");
	public static final class_3528<NioEventLoopGroup> field_11650 = new class_3528<>(
		() -> new NioEventLoopGroup(0, new ThreadFactoryBuilder().setNameFormat("Netty Client IO #%d").setDaemon(true).build())
	);
	public static final class_3528<EpollEventLoopGroup> field_11657 = new class_3528<>(
		() -> new EpollEventLoopGroup(0, new ThreadFactoryBuilder().setNameFormat("Netty Epoll Client IO #%d").setDaemon(true).build())
	);
	public static final class_3528<DefaultEventLoopGroup> field_11649 = new class_3528<>(
		() -> new DefaultEventLoopGroup(0, new ThreadFactoryBuilder().setNameFormat("Netty Local Client IO #%d").setDaemon(true).build())
	);
	private final class_2598 field_11643;
	private final Queue<class_2535.class_2536> field_11644 = Queues.<class_2535.class_2536>newConcurrentLinkedQueue();
	private final ReentrantReadWriteLock field_11659 = new ReentrantReadWriteLock();
	private Channel field_11651;
	private SocketAddress field_11645;
	private class_2547 field_11652;
	private class_2561 field_11660;
	private boolean field_11647;
	private boolean field_11646;
	private int field_11658;
	private int field_11656;
	private float field_11654;
	private float field_11653;
	private int field_11655;
	private boolean field_11640;

	public class_2535(class_2598 arg) {
		this.field_11643 = arg;
	}

	@Override
	public void channelActive(ChannelHandlerContext channelHandlerContext) throws Exception {
		super.channelActive(channelHandlerContext);
		this.field_11651 = channelHandlerContext.channel();
		this.field_11645 = this.field_11651.remoteAddress();

		try {
			this.method_10750(class_2539.field_11689);
		} catch (Throwable var3) {
			field_11642.fatal(var3);
		}
	}

	public void method_10750(class_2539 arg) {
		this.field_11651.attr(field_11648).set(arg);
		this.field_11651.config().setAutoRead(true);
		field_11642.debug("Enabled auto read");
	}

	@Override
	public void channelInactive(ChannelHandlerContext channelHandlerContext) throws Exception {
		this.method_10747(new class_2588("disconnect.endOfStream"));
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext channelHandlerContext, Throwable throwable) {
		if (throwable instanceof class_2548) {
			field_11642.debug("Skipping packet due to errors", throwable.getCause());
		} else {
			boolean bl = !this.field_11640;
			this.field_11640 = true;
			if (this.field_11651.isOpen()) {
				if (throwable instanceof TimeoutException) {
					field_11642.debug("Timeout", throwable);
					this.method_10747(new class_2588("disconnect.timeout"));
				} else {
					class_2561 lv = new class_2588("disconnect.genericReason", "Internal Exception: " + throwable);
					if (bl) {
						field_11642.debug("Failed to sent packet", throwable);
						this.method_10752(new class_2661(lv), future -> this.method_10747(lv));
						this.method_10757();
					} else {
						field_11642.debug("Double fault", throwable);
						this.method_10747(lv);
					}
				}
			}
		}
	}

	protected void method_10770(ChannelHandlerContext channelHandlerContext, class_2596<?> arg) throws Exception {
		if (this.field_11651.isOpen()) {
			try {
				method_10759(arg, this.field_11652);
			} catch (class_2987 var4) {
			}

			this.field_11658++;
		}
	}

	private static <T extends class_2547> void method_10759(class_2596<T> arg, class_2547 arg2) {
		arg.method_11054((T)arg2);
	}

	public void method_10763(class_2547 arg) {
		Validate.notNull(arg, "packetListener");
		field_11642.debug("Set listener of {} to {}", this, arg);
		this.field_11652 = arg;
	}

	public void method_10743(class_2596<?> arg) {
		this.method_10752(arg, null);
	}

	public void method_10752(class_2596<?> arg, @Nullable GenericFutureListener<? extends Future<? super Void>> genericFutureListener) {
		if (this.method_10758()) {
			this.method_10751();
			this.method_10764(arg, genericFutureListener);
		} else {
			this.field_11659.writeLock().lock();

			try {
				this.field_11644.add(new class_2535.class_2536(arg, genericFutureListener));
			} finally {
				this.field_11659.writeLock().unlock();
			}
		}
	}

	private void method_10764(class_2596<?> arg, @Nullable GenericFutureListener<? extends Future<? super Void>> genericFutureListener) {
		class_2539 lv = class_2539.method_10786(arg);
		class_2539 lv2 = this.field_11651.attr(field_11648).get();
		this.field_11656++;
		if (lv2 != lv) {
			field_11642.debug("Disabled auto read");
			this.field_11651.config().setAutoRead(false);
		}

		if (this.field_11651.eventLoop().inEventLoop()) {
			if (lv != lv2) {
				this.method_10750(lv);
			}

			ChannelFuture channelFuture = this.field_11651.writeAndFlush(arg);
			if (genericFutureListener != null) {
				channelFuture.addListener(genericFutureListener);
			}

			channelFuture.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
		} else {
			this.field_11651.eventLoop().execute(() -> {
				if (lv != lv2) {
					this.method_10750(lv);
				}

				ChannelFuture channelFuturex = this.field_11651.writeAndFlush(arg);
				if (genericFutureListener != null) {
					channelFuturex.addListener(genericFutureListener);
				}

				channelFuturex.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
			});
		}
	}

	private void method_10751() {
		if (this.field_11651 != null && this.field_11651.isOpen()) {
			this.field_11659.readLock().lock();

			try {
				while (!this.field_11644.isEmpty()) {
					class_2535.class_2536 lv = (class_2535.class_2536)this.field_11644.poll();
					this.method_10764(lv.field_11661, lv.field_11662);
				}
			} finally {
				this.field_11659.readLock().unlock();
			}
		}
	}

	public void method_10754() {
		this.method_10751();
		if (this.field_11652 instanceof class_3248) {
			((class_3248)this.field_11652).method_18785();
		}

		if (this.field_11652 instanceof class_3244) {
			((class_3244)this.field_11652).method_18784();
		}

		if (this.field_11651 != null) {
			this.field_11651.flush();
		}

		if (this.field_11655++ % 20 == 0) {
			this.field_11653 = this.field_11653 * 0.75F + (float)this.field_11656 * 0.25F;
			this.field_11654 = this.field_11654 * 0.75F + (float)this.field_11658 * 0.25F;
			this.field_11656 = 0;
			this.field_11658 = 0;
		}
	}

	public SocketAddress method_10755() {
		return this.field_11645;
	}

	public void method_10747(class_2561 arg) {
		if (this.field_11651.isOpen()) {
			this.field_11651.close().awaitUninterruptibly();
			this.field_11660 = arg;
		}
	}

	public boolean method_10756() {
		return this.field_11651 instanceof LocalChannel || this.field_11651 instanceof LocalServerChannel;
	}

	@Environment(EnvType.CLIENT)
	public static class_2535 method_10753(InetAddress inetAddress, int i, boolean bl) {
		final class_2535 lv = new class_2535(class_2598.field_11942);
		Class<? extends SocketChannel> class_;
		class_3528<? extends EventLoopGroup> lv2;
		if (Epoll.isAvailable() && bl) {
			class_ = EpollSocketChannel.class;
			lv2 = field_11657;
		} else {
			class_ = NioSocketChannel.class;
			lv2 = field_11650;
		}

		new Bootstrap()
			.group(lv2.method_15332())
			.handler(
				new ChannelInitializer<Channel>() {
					@Override
					protected void initChannel(Channel channel) throws Exception {
						try {
							channel.config().setOption(ChannelOption.TCP_NODELAY, true);
						} catch (ChannelException var3) {
						}

						channel.pipeline()
							.addLast("timeout", new ReadTimeoutHandler(30))
							.addLast("splitter", new class_2550())
							.addLast("decoder", new class_2543(class_2598.field_11942))
							.addLast("prepender", new class_2552())
							.addLast("encoder", new class_2545(class_2598.field_11941))
							.addLast("packet_handler", lv);
					}
				}
			)
			.channel(class_)
			.connect(inetAddress, i)
			.syncUninterruptibly();
		return lv;
	}

	@Environment(EnvType.CLIENT)
	public static class_2535 method_10769(SocketAddress socketAddress) {
		final class_2535 lv = new class_2535(class_2598.field_11942);
		new Bootstrap().group(field_11649.method_15332()).handler(new ChannelInitializer<Channel>() {
			@Override
			protected void initChannel(Channel channel) throws Exception {
				channel.pipeline().addLast("packet_handler", lv);
			}
		}).channel(LocalChannel.class).connect(socketAddress).syncUninterruptibly();
		return lv;
	}

	public void method_10746(SecretKey secretKey) {
		this.field_11647 = true;
		this.field_11651.pipeline().addBefore("splitter", "decrypt", new class_2528(class_3515.method_15235(2, secretKey)));
		this.field_11651.pipeline().addBefore("prepender", "encrypt", new class_2529(class_3515.method_15235(1, secretKey)));
	}

	@Environment(EnvType.CLIENT)
	public boolean method_10771() {
		return this.field_11647;
	}

	public boolean method_10758() {
		return this.field_11651 != null && this.field_11651.isOpen();
	}

	public boolean method_10772() {
		return this.field_11651 == null;
	}

	public class_2547 method_10744() {
		return this.field_11652;
	}

	@Nullable
	public class_2561 method_10748() {
		return this.field_11660;
	}

	public void method_10757() {
		this.field_11651.config().setAutoRead(false);
	}

	public void method_10760(int i) {
		if (i >= 0) {
			if (this.field_11651.pipeline().get("decompress") instanceof class_2532) {
				((class_2532)this.field_11651.pipeline().get("decompress")).method_10739(i);
			} else {
				this.field_11651.pipeline().addBefore("decoder", "decompress", new class_2532(i));
			}

			if (this.field_11651.pipeline().get("compress") instanceof class_2534) {
				((class_2534)this.field_11651.pipeline().get("compress")).method_10742(i);
			} else {
				this.field_11651.pipeline().addBefore("encoder", "compress", new class_2534(i));
			}
		} else {
			if (this.field_11651.pipeline().get("decompress") instanceof class_2532) {
				this.field_11651.pipeline().remove("decompress");
			}

			if (this.field_11651.pipeline().get("compress") instanceof class_2534) {
				this.field_11651.pipeline().remove("compress");
			}
		}
	}

	public void method_10768() {
		if (this.field_11651 != null && !this.field_11651.isOpen()) {
			if (this.field_11646) {
				field_11642.warn("handleDisconnection() called twice");
			} else {
				this.field_11646 = true;
				if (this.method_10748() != null) {
					this.method_10744().method_10839(this.method_10748());
				} else if (this.method_10744() != null) {
					this.method_10744().method_10839(new class_2588("multiplayer.disconnect.generic"));
				}
			}
		}
	}

	@Environment(EnvType.CLIENT)
	public float method_10762() {
		return this.field_11654;
	}

	@Environment(EnvType.CLIENT)
	public float method_10745() {
		return this.field_11653;
	}

	static class class_2536 {
		private final class_2596<?> field_11661;
		@Nullable
		private final GenericFutureListener<? extends Future<? super Void>> field_11662;

		public class_2536(class_2596<?> arg, @Nullable GenericFutureListener<? extends Future<? super Void>> genericFutureListener) {
			this.field_11661 = arg;
			this.field_11662 = genericFutureListener;
		}
	}
}
