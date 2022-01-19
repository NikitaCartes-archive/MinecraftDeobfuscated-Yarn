/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
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
import net.minecraft.client.realms.gui.FetchTask;
import net.minecraft.client.realms.util.RealmsPersistence;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
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
    private final Set<RealmsServer> removedServers = Sets.newHashSet();
    private List<RealmsServer> servers = Lists.newArrayList();
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
    private final Map<Task, Boolean> fetchStatus = new ConcurrentHashMap<Task, Boolean>(Task.values().length);

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
            this.fetchStatus.put(Task.PENDING_INVITE, false);
            this.pendingInviteScheduledFuture = this.pendingInviteUpdateTask.schedule(this.scheduler);
            this.fetchStatus.put(Task.TRIAL_AVAILABLE, false);
            this.trialAvailableScheduledFuture = this.trialAvailabilityTask.schedule(this.scheduler);
            this.fetchStatus.put(Task.UNREAD_NEWS, false);
            this.unreadNewsScheduledFuture = this.unreadNewsTask.schedule(this.scheduler);
        }
    }

    public boolean isFetchedSinceLastTry(Task task) {
        Boolean boolean_ = this.fetchStatus.get((Object)task);
        return boolean_ != null && boolean_ != false;
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
        for (Task task : Task.values()) {
            this.fetchStatus.put(task, false);
        }
        this.serverListScheduledFuture = this.serverListUpdateTask.schedule(this.scheduler);
        this.pendingInviteScheduledFuture = this.pendingInviteUpdateTask.schedule(this.scheduler);
        this.trialAvailableScheduledFuture = this.trialAvailabilityTask.schedule(this.scheduler);
        this.liveStatsScheduledFuture = this.liveStatsTask.schedule(this.scheduler);
        this.unreadNewsScheduledFuture = this.unreadNewsTask.schedule(this.scheduler);
    }

    private void cancelTasks() {
        Stream.of(this.serverListScheduledFuture, this.pendingInviteScheduledFuture, this.trialAvailableScheduledFuture, this.liveStatsScheduledFuture, this.unreadNewsScheduledFuture).filter(Objects::nonNull).forEach(task -> {
            try {
                task.cancel(false);
            } catch (Exception exception) {
                LOGGER.error("Failed to cancel Realms task", exception);
            }
        });
    }

    private synchronized void setServers(List<RealmsServer> newServers) {
        int i = 0;
        for (RealmsServer realmsServer : this.removedServers) {
            if (!newServers.remove(realmsServer)) continue;
            ++i;
        }
        if (i == 0) {
            this.removedServers.clear();
        }
        this.servers = newServers;
    }

    public synchronized void removeItem(RealmsServer server) {
        this.servers.remove(server);
        this.removedServers.add(server);
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
                this.fetchStatus.put(Task.SERVER_LIST, true);
            } else {
                LOGGER.warn("Realms server list was null");
            }
        } catch (Exception exception) {
            this.fetchStatus.put(Task.SERVER_LIST, true);
            LOGGER.error("Couldn't get server list", exception);
        }
    }

    private void updatePendingInvites() {
        try {
            this.pendingInvitesCount = this.realms.pendingInvitesCount();
            this.fetchStatus.put(Task.PENDING_INVITE, true);
        } catch (Exception exception) {
            LOGGER.error("Couldn't get pending invite count", exception);
        }
    }

    private void updateTrialAvailability() {
        try {
            this.trialAvailable = this.realms.trialAvailable();
            this.fetchStatus.put(Task.TRIAL_AVAILABLE, true);
        } catch (Exception exception) {
            LOGGER.error("Couldn't get trial availability", exception);
        }
    }

    private void updateLiveStats() {
        try {
            this.livestats = this.realms.getLiveStats();
            this.fetchStatus.put(Task.LIVE_STATS, true);
        } catch (Exception exception) {
            LOGGER.error("Couldn't get live stats", exception);
        }
    }

    private void updateNews() {
        try {
            RealmsPersistence.RealmsPersistenceData realmsPersistenceData = this.fetchNews();
            this.hasUnreadNews = realmsPersistenceData.hasUnreadNews;
            this.newsLink = realmsPersistenceData.newsLink;
            this.fetchStatus.put(Task.UNREAD_NEWS, true);
        } catch (Exception exception) {
            LOGGER.error("Couldn't update unread news", exception);
        }
    }

    private RealmsPersistence.RealmsPersistenceData fetchNews() {
        boolean bl;
        RealmsPersistence.RealmsPersistenceData realmsPersistenceData;
        try {
            RealmsNews realmsNews = this.realms.getNews();
            realmsPersistenceData = new RealmsPersistence.RealmsPersistenceData();
            realmsPersistenceData.newsLink = realmsNews.newsLink;
        } catch (Exception exception) {
            LOGGER.warn("Failed fetching news from Realms, falling back to local cache", exception);
            return this.persistence.load();
        }
        RealmsPersistence.RealmsPersistenceData realmsPersistenceData2 = this.persistence.load();
        boolean bl2 = bl = realmsPersistenceData.newsLink == null || realmsPersistenceData.newsLink.equals(realmsPersistenceData2.newsLink);
        if (bl) {
            return realmsPersistenceData2;
        }
        realmsPersistenceData.hasUnreadNews = true;
        this.persistence.save(realmsPersistenceData);
        return realmsPersistenceData;
    }

    @Environment(value=EnvType.CLIENT)
    public static enum Task {
        SERVER_LIST,
        PENDING_INVITE,
        TRIAL_AVAILABLE,
        LIVE_STATS,
        UNREAD_NEWS;

    }
}

