package net.minecraft.client.realms.gui;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.logging.LogUtils;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.realms.RealmsClient;
import net.minecraft.client.realms.dto.RealmsNews;
import net.minecraft.client.realms.dto.RealmsServer;
import net.minecraft.client.realms.dto.RealmsServerPlayerLists;
import net.minecraft.client.realms.util.RealmsPersistence;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class RealmsDataFetcher {
	private static final Logger LOGGER = LogUtils.getLogger();
	private final MinecraftClient client;
	private final RealmsClient realms;
	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(3);
	private volatile boolean stopped = true;
	private final FetchTask serverListUpdateTask = FetchTask.create(this::updateServerList, Duration.ofSeconds(60L), this::isActive);
	private final FetchTask liveStatsTask = FetchTask.create(this::updateLiveStats, Duration.ofSeconds(10L), this::isActive);
	private final FetchTask pendingInviteUpdateTask = FetchTask.createRateLimited(this::updatePendingInvites, Duration.ofSeconds(10L), this::isActive);
	private final FetchTask trialAvailabilityTask = FetchTask.createRateLimited(this::updateTrialAvailability, Duration.ofSeconds(60L), this::isActive);
	private final FetchTask unreadNewsTask = FetchTask.createRateLimited(this::updateNews, Duration.ofMinutes(5L), this::isActive);
	private final RealmsPersistence persistence;
	private final Set<RealmsServer> removedServers = Sets.<RealmsServer>newHashSet();
	private List<RealmsServer> servers = Lists.<RealmsServer>newArrayList();
	private RealmsServerPlayerLists livestats;
	private int pendingInvitesCount;
	private boolean trialAvailable;
	private boolean hasUnreadNews;
	private String newsLink;
	private ScheduledFuture<?> serverListScheduledFuture;
	private ScheduledFuture<?> pendingInviteScheduledFuture;
	private ScheduledFuture<?> trialAvailableScheduledFuture;
	private ScheduledFuture<?> liveStatsScheduledFuture;
	private ScheduledFuture<?> unreadNewsScheduledFuture;
	private final Map<RealmsDataFetcher.Task, Boolean> fetchStatus = new ConcurrentHashMap(RealmsDataFetcher.Task.values().length);

	public RealmsDataFetcher(MinecraftClient client, RealmsClient realms) {
		this.client = client;
		this.realms = realms;
		this.persistence = new RealmsPersistence();
	}

	@VisibleForTesting
	protected RealmsDataFetcher(MinecraftClient client, RealmsClient realms, RealmsPersistence persistence) {
		this.client = client;
		this.realms = realms;
		this.persistence = persistence;
	}

	public boolean isStopped() {
		return this.stopped;
	}

	public synchronized void init() {
		if (this.stopped) {
			this.stopped = false;
			this.cancelTasks();
			this.scheduleTasks();
		}
	}

	public synchronized void initWithSpecificTaskList() {
		if (this.stopped) {
			this.stopped = false;
			this.cancelTasks();
			this.fetchStatus.put(RealmsDataFetcher.Task.PENDING_INVITE, false);
			this.pendingInviteScheduledFuture = this.pendingInviteUpdateTask.schedule(this.scheduler);
			this.fetchStatus.put(RealmsDataFetcher.Task.TRIAL_AVAILABLE, false);
			this.trialAvailableScheduledFuture = this.trialAvailabilityTask.schedule(this.scheduler);
			this.fetchStatus.put(RealmsDataFetcher.Task.UNREAD_NEWS, false);
			this.unreadNewsScheduledFuture = this.unreadNewsTask.schedule(this.scheduler);
		}
	}

	public boolean isFetchedSinceLastTry(RealmsDataFetcher.Task task) {
		Boolean boolean_ = (Boolean)this.fetchStatus.get(task);
		return boolean_ != null && boolean_;
	}

	public void markClean() {
		this.fetchStatus.replaceAll((task, fetched) -> false);
	}

	public synchronized void forceUpdate() {
		this.stop();
		this.init();
	}

	public synchronized List<RealmsServer> getServers() {
		return ImmutableList.copyOf(this.servers);
	}

	public synchronized int getPendingInvitesCount() {
		return this.pendingInvitesCount;
	}

	public synchronized boolean isTrialAvailable() {
		return this.trialAvailable;
	}

	public synchronized RealmsServerPlayerLists getLivestats() {
		return this.livestats;
	}

	public synchronized boolean hasUnreadNews() {
		return this.hasUnreadNews;
	}

	public synchronized String newsLink() {
		return this.newsLink;
	}

	public synchronized void stop() {
		this.stopped = true;
		this.cancelTasks();
	}

	private void scheduleTasks() {
		for (RealmsDataFetcher.Task task : RealmsDataFetcher.Task.values()) {
			this.fetchStatus.put(task, false);
		}

		this.serverListScheduledFuture = this.serverListUpdateTask.schedule(this.scheduler);
		this.pendingInviteScheduledFuture = this.pendingInviteUpdateTask.schedule(this.scheduler);
		this.trialAvailableScheduledFuture = this.trialAvailabilityTask.schedule(this.scheduler);
		this.liveStatsScheduledFuture = this.liveStatsTask.schedule(this.scheduler);
		this.unreadNewsScheduledFuture = this.unreadNewsTask.schedule(this.scheduler);
	}

	private void cancelTasks() {
		Stream.of(
				this.serverListScheduledFuture,
				this.pendingInviteScheduledFuture,
				this.trialAvailableScheduledFuture,
				this.liveStatsScheduledFuture,
				this.unreadNewsScheduledFuture
			)
			.filter(Objects::nonNull)
			.forEach(task -> {
				try {
					task.cancel(false);
				} catch (Exception var2) {
					LOGGER.error("Failed to cancel Realms task", (Throwable)var2);
				}
			});
	}

	private synchronized void setServers(List<RealmsServer> newServers) {
		int i = 0;

		for (RealmsServer realmsServer : this.removedServers) {
			if (newServers.remove(realmsServer)) {
				i++;
			}
		}

		if (i == 0) {
			this.removedServers.clear();
		}

		this.servers = newServers;
	}

	public synchronized List<RealmsServer> removeItem(RealmsServer server) {
		this.servers.remove(server);
		this.removedServers.add(server);
		return ImmutableList.copyOf(this.servers);
	}

	private boolean isActive() {
		return !this.stopped;
	}

	private void updateServerList() {
		try {
			List<RealmsServer> list = this.realms.listWorlds().servers;
			if (list != null) {
				list.sort(new RealmsServer.McoServerComparator(this.client.getSession().getUsername()));
				this.setServers(list);
				this.fetchStatus.put(RealmsDataFetcher.Task.SERVER_LIST, true);
			} else {
				LOGGER.warn("Realms server list was null");
			}
		} catch (Exception var2) {
			this.fetchStatus.put(RealmsDataFetcher.Task.SERVER_LIST, true);
			LOGGER.error("Couldn't get server list", (Throwable)var2);
		}
	}

	private void updatePendingInvites() {
		try {
			this.pendingInvitesCount = this.realms.pendingInvitesCount();
			this.fetchStatus.put(RealmsDataFetcher.Task.PENDING_INVITE, true);
		} catch (Exception var2) {
			LOGGER.error("Couldn't get pending invite count", (Throwable)var2);
		}
	}

	private void updateTrialAvailability() {
		try {
			this.trialAvailable = this.realms.trialAvailable();
			this.fetchStatus.put(RealmsDataFetcher.Task.TRIAL_AVAILABLE, true);
		} catch (Exception var2) {
			LOGGER.error("Couldn't get trial availability", (Throwable)var2);
		}
	}

	private void updateLiveStats() {
		try {
			this.livestats = this.realms.getLiveStats();
			this.fetchStatus.put(RealmsDataFetcher.Task.LIVE_STATS, true);
		} catch (Exception var2) {
			LOGGER.error("Couldn't get live stats", (Throwable)var2);
		}
	}

	private void updateNews() {
		try {
			RealmsPersistence.RealmsPersistenceData realmsPersistenceData = this.fetchNews();
			this.hasUnreadNews = realmsPersistenceData.hasUnreadNews;
			this.newsLink = realmsPersistenceData.newsLink;
			this.fetchStatus.put(RealmsDataFetcher.Task.UNREAD_NEWS, true);
		} catch (Exception var2) {
			LOGGER.error("Couldn't update unread news", (Throwable)var2);
		}
	}

	private RealmsPersistence.RealmsPersistenceData fetchNews() {
		RealmsPersistence.RealmsPersistenceData realmsPersistenceData;
		try {
			RealmsNews realmsNews = this.realms.getNews();
			realmsPersistenceData = new RealmsPersistence.RealmsPersistenceData();
			realmsPersistenceData.newsLink = realmsNews.newsLink;
		} catch (Exception var4) {
			LOGGER.warn("Failed fetching news from Realms, falling back to local cache", (Throwable)var4);
			return this.persistence.load();
		}

		RealmsPersistence.RealmsPersistenceData realmsPersistenceData2 = this.persistence.load();
		boolean bl = realmsPersistenceData.newsLink == null || realmsPersistenceData.newsLink.equals(realmsPersistenceData2.newsLink);
		if (bl) {
			return realmsPersistenceData2;
		} else {
			realmsPersistenceData.hasUnreadNews = true;
			this.persistence.save(realmsPersistenceData);
			return realmsPersistenceData;
		}
	}

	@Environment(EnvType.CLIENT)
	public static enum Task {
		SERVER_LIST,
		PENDING_INVITE,
		TRIAL_AVAILABLE,
		LIVE_STATS,
		UNREAD_NEWS;
	}
}
