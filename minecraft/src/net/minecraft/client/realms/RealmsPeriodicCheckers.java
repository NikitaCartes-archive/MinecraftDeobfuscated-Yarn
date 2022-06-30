package net.minecraft.client.realms;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.dto.RealmsNews;
import net.minecraft.client.realms.dto.RealmsServer;
import net.minecraft.client.realms.dto.RealmsServerPlayerLists;
import net.minecraft.client.realms.util.PeriodicRunnerFactory;
import net.minecraft.client.realms.util.RealmsPersistence;
import net.minecraft.client.util.Backoff;
import net.minecraft.util.Util;

@Environment(EnvType.CLIENT)
public class RealmsPeriodicCheckers {
	public final PeriodicRunnerFactory runnerFactory = new PeriodicRunnerFactory(Util.getIoWorkerExecutor(), TimeUnit.MILLISECONDS, Util.nanoTimeSupplier);
	public final PeriodicRunnerFactory.PeriodicRunner<List<RealmsServer>> serverList;
	public final PeriodicRunnerFactory.PeriodicRunner<RealmsServerPlayerLists> liveStats;
	public final PeriodicRunnerFactory.PeriodicRunner<Integer> pendingInvitesCount;
	public final PeriodicRunnerFactory.PeriodicRunner<Boolean> trialAvailability;
	public final PeriodicRunnerFactory.PeriodicRunner<RealmsNews> news;
	public final RealmsNewsUpdater newsUpdater = new RealmsNewsUpdater(new RealmsPersistence());

	public RealmsPeriodicCheckers(RealmsClient client) {
		this.serverList = this.runnerFactory.create("server list", () -> client.listWorlds().servers, Duration.ofSeconds(60L), Backoff.ONE_CYCLE);
		this.liveStats = this.runnerFactory.create("live stats", client::getLiveStats, Duration.ofSeconds(10L), Backoff.ONE_CYCLE);
		this.pendingInvitesCount = this.runnerFactory.create("pending invite count", client::pendingInvitesCount, Duration.ofSeconds(10L), Backoff.exponential(360));
		this.trialAvailability = this.runnerFactory.create("trial availablity", client::trialAvailable, Duration.ofSeconds(60L), Backoff.exponential(60));
		this.news = this.runnerFactory.create("unread news", client::getNews, Duration.ofMinutes(5L), Backoff.ONE_CYCLE);
	}
}
