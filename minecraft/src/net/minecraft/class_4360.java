package net.minecraft;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.realmsclient.dto.RealmsNews;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.RealmsServerPlayerLists;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.realms.Realms;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class class_4360 {
	private static final Logger field_19639 = LogManager.getLogger();
	private final ScheduledExecutorService field_19640 = Executors.newScheduledThreadPool(3);
	private volatile boolean field_19641 = true;
	private final class_4360.class_4363 field_19642 = new class_4360.class_4363();
	private final class_4360.class_4362 field_19643 = new class_4360.class_4362();
	private final class_4360.class_4365 field_19644 = new class_4360.class_4365();
	private final class_4360.class_4361 field_19645 = new class_4360.class_4361();
	private final class_4360.class_4366 field_19646 = new class_4360.class_4366();
	private final Set<RealmsServer> field_19647 = Sets.<RealmsServer>newHashSet();
	private List<RealmsServer> field_19648 = Lists.<RealmsServer>newArrayList();
	private RealmsServerPlayerLists field_19649;
	private int field_19650;
	private boolean field_19651;
	private boolean field_19652;
	private String field_19653;
	private ScheduledFuture<?> field_19654;
	private ScheduledFuture<?> field_19655;
	private ScheduledFuture<?> field_19656;
	private ScheduledFuture<?> field_19657;
	private ScheduledFuture<?> field_19658;
	private final Map<class_4360.class_4364, Boolean> field_19659 = new ConcurrentHashMap(class_4360.class_4364.values().length);

	public boolean method_21073() {
		return this.field_19641;
	}

	public synchronized void method_21083() {
		if (this.field_19641) {
			this.field_19641 = false;
			this.method_21100();
			this.method_21099();
		}
	}

	public synchronized void method_21082(List<class_4360.class_4364> list) {
		if (this.field_19641) {
			this.field_19641 = false;
			this.method_21100();

			for (class_4360.class_4364 lv : list) {
				this.field_19659.put(lv, false);
				switch (lv) {
					case SERVER_LIST:
						this.field_19654 = this.field_19640.scheduleAtFixedRate(this.field_19642, 0L, 60L, TimeUnit.SECONDS);
						break;
					case PENDING_INVITE:
						this.field_19655 = this.field_19640.scheduleAtFixedRate(this.field_19643, 0L, 10L, TimeUnit.SECONDS);
						break;
					case TRIAL_AVAILABLE:
						this.field_19656 = this.field_19640.scheduleAtFixedRate(this.field_19644, 0L, 60L, TimeUnit.SECONDS);
						break;
					case LIVE_STATS:
						this.field_19657 = this.field_19640.scheduleAtFixedRate(this.field_19645, 0L, 10L, TimeUnit.SECONDS);
						break;
					case UNREAD_NEWS:
						this.field_19658 = this.field_19640.scheduleAtFixedRate(this.field_19646, 0L, 300L, TimeUnit.SECONDS);
				}
			}
		}
	}

	public boolean method_21075(class_4360.class_4364 arg) {
		Boolean boolean_ = (Boolean)this.field_19659.get(arg);
		return boolean_ == null ? false : boolean_;
	}

	public void method_21088() {
		for (class_4360.class_4364 lv : this.field_19659.keySet()) {
			this.field_19659.put(lv, false);
		}
	}

	public synchronized void method_21090() {
		this.method_21097();
		this.method_21083();
	}

	public synchronized List<RealmsServer> method_21091() {
		return Lists.<RealmsServer>newArrayList(this.field_19648);
	}

	public synchronized int method_21092() {
		return this.field_19650;
	}

	public synchronized boolean method_21093() {
		return this.field_19651;
	}

	public synchronized RealmsServerPlayerLists method_21094() {
		return this.field_19649;
	}

	public synchronized boolean method_21095() {
		return this.field_19652;
	}

	public synchronized String method_21096() {
		return this.field_19653;
	}

	public synchronized void method_21097() {
		this.field_19641 = true;
		this.method_21100();
	}

	private void method_21099() {
		for (class_4360.class_4364 lv : class_4360.class_4364.values()) {
			this.field_19659.put(lv, false);
		}

		this.field_19654 = this.field_19640.scheduleAtFixedRate(this.field_19642, 0L, 60L, TimeUnit.SECONDS);
		this.field_19655 = this.field_19640.scheduleAtFixedRate(this.field_19643, 0L, 10L, TimeUnit.SECONDS);
		this.field_19656 = this.field_19640.scheduleAtFixedRate(this.field_19644, 0L, 60L, TimeUnit.SECONDS);
		this.field_19657 = this.field_19640.scheduleAtFixedRate(this.field_19645, 0L, 10L, TimeUnit.SECONDS);
		this.field_19658 = this.field_19640.scheduleAtFixedRate(this.field_19646, 0L, 300L, TimeUnit.SECONDS);
	}

	private void method_21100() {
		try {
			if (this.field_19654 != null) {
				this.field_19654.cancel(false);
			}

			if (this.field_19655 != null) {
				this.field_19655.cancel(false);
			}

			if (this.field_19656 != null) {
				this.field_19656.cancel(false);
			}

			if (this.field_19657 != null) {
				this.field_19657.cancel(false);
			}

			if (this.field_19658 != null) {
				this.field_19658.cancel(false);
			}
		} catch (Exception var2) {
			field_19639.error("Failed to cancel Realms tasks", (Throwable)var2);
		}
	}

	private synchronized void method_21087(List<RealmsServer> list) {
		int i = 0;

		for (RealmsServer realmsServer : this.field_19647) {
			if (list.remove(realmsServer)) {
				i++;
			}
		}

		if (i == 0) {
			this.field_19647.clear();
		}

		this.field_19648 = list;
	}

	public synchronized void method_21074(RealmsServer realmsServer) {
		this.field_19648.remove(realmsServer);
		this.field_19647.add(realmsServer);
	}

	private void method_21089(List<RealmsServer> list) {
		Collections.sort(list, new RealmsServer.class_4319(Realms.getName()));
	}

	private boolean method_21101() {
		return !this.field_19641;
	}

	@Environment(EnvType.CLIENT)
	class class_4361 implements Runnable {
		private class_4361() {
		}

		public void run() {
			if (class_4360.this.method_21101()) {
				this.method_21102();
			}
		}

		private void method_21102() {
			try {
				class_4341 lv = class_4341.method_20989();
				if (lv != null) {
					class_4360.this.field_19649 = lv.method_21018();
					class_4360.this.field_19659.put(class_4360.class_4364.LIVE_STATS, true);
				}
			} catch (Exception var2) {
				class_4360.field_19639.error("Couldn't get live stats", (Throwable)var2);
			}
		}
	}

	@Environment(EnvType.CLIENT)
	class class_4362 implements Runnable {
		private class_4362() {
		}

		public void run() {
			if (class_4360.this.method_21101()) {
				this.method_21103();
			}
		}

		private void method_21103() {
			try {
				class_4341 lv = class_4341.method_20989();
				if (lv != null) {
					class_4360.this.field_19650 = lv.method_21029();
					class_4360.this.field_19659.put(class_4360.class_4364.PENDING_INVITE, true);
				}
			} catch (Exception var2) {
				class_4360.field_19639.error("Couldn't get pending invite count", (Throwable)var2);
			}
		}
	}

	@Environment(EnvType.CLIENT)
	class class_4363 implements Runnable {
		private class_4363() {
		}

		public void run() {
			if (class_4360.this.method_21101()) {
				this.method_21104();
			}
		}

		private void method_21104() {
			try {
				class_4341 lv = class_4341.method_20989();
				if (lv != null) {
					List<RealmsServer> list = lv.method_21015().servers;
					if (list != null) {
						class_4360.this.method_21089(list);
						class_4360.this.method_21087(list);
						class_4360.this.field_19659.put(class_4360.class_4364.SERVER_LIST, true);
					} else {
						class_4360.field_19639.warn("Realms server list was null or empty");
					}
				}
			} catch (Exception var3) {
				class_4360.this.field_19659.put(class_4360.class_4364.SERVER_LIST, true);
				class_4360.field_19639.error("Couldn't get server list", (Throwable)var3);
			}
		}
	}

	@Environment(EnvType.CLIENT)
	public static enum class_4364 {
		SERVER_LIST,
		PENDING_INVITE,
		TRIAL_AVAILABLE,
		LIVE_STATS,
		UNREAD_NEWS;
	}

	@Environment(EnvType.CLIENT)
	class class_4365 implements Runnable {
		private class_4365() {
		}

		public void run() {
			if (class_4360.this.method_21101()) {
				this.method_21105();
			}
		}

		private void method_21105() {
			try {
				class_4341 lv = class_4341.method_20989();
				if (lv != null) {
					class_4360.this.field_19651 = lv.method_21033();
					class_4360.this.field_19659.put(class_4360.class_4364.TRIAL_AVAILABLE, true);
				}
			} catch (Exception var2) {
				class_4360.field_19639.error("Couldn't get trial availability", (Throwable)var2);
			}
		}
	}

	@Environment(EnvType.CLIENT)
	class class_4366 implements Runnable {
		private class_4366() {
		}

		public void run() {
			if (class_4360.this.method_21101()) {
				this.method_21106();
			}
		}

		private void method_21106() {
			try {
				class_4341 lv = class_4341.method_20989();
				if (lv != null) {
					RealmsNews realmsNews = null;

					try {
						realmsNews = lv.method_21032();
					} catch (Exception var5) {
					}

					class_4432.class_4433 lv2 = class_4432.method_21549();
					if (realmsNews != null) {
						String string = realmsNews.newsLink;
						if (string != null && !string.equals(lv2.field_20209)) {
							lv2.field_20210 = true;
							lv2.field_20209 = string;
							class_4432.method_21550(lv2);
						}
					}

					class_4360.this.field_19652 = lv2.field_20210;
					class_4360.this.field_19653 = lv2.field_20209;
					class_4360.this.field_19659.put(class_4360.class_4364.UNREAD_NEWS, true);
				}
			} catch (Exception var6) {
				class_4360.field_19639.error("Couldn't get unread news", (Throwable)var6);
			}
		}
	}
}
