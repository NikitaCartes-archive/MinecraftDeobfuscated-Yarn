package net.minecraft;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.RealmsClient;
import net.minecraft.client.realms.dto.RealmsNews;
import net.minecraft.client.realms.dto.RealmsServer;
import net.minecraft.client.realms.dto.RealmsServerPlayerLists;
import net.minecraft.client.realms.util.RealmsPersistence;
import net.minecraft.util.Util;

@Environment(EnvType.CLIENT)
public class class_7578 {
	public final class_7581 field_39682 = new class_7581(Util.getIoWorkerExecutor(), TimeUnit.MILLISECONDS, Util.nanoTimeSupplier);
	public final class_7581.class_7586<List<RealmsServer>> field_39683;
	public final class_7581.class_7586<RealmsServerPlayerLists> field_39684;
	public final class_7581.class_7586<Integer> field_39685;
	public final class_7581.class_7586<Boolean> field_39686;
	public final class_7581.class_7586<RealmsNews> field_39687;
	public final class_7579 field_39688 = new class_7579(new RealmsPersistence());

	public class_7578(RealmsClient realmsClient) {
		this.field_39683 = this.field_39682.method_44629("server list", () -> realmsClient.listWorlds().servers, Duration.ofSeconds(60L), class_7587.field_39714);
		this.field_39684 = this.field_39682.method_44629("live stats", realmsClient::getLiveStats, Duration.ofSeconds(10L), class_7587.field_39714);
		this.field_39685 = this.field_39682
			.method_44629("pending invite count", realmsClient::pendingInvitesCount, Duration.ofSeconds(10L), class_7587.method_44644(360));
		this.field_39686 = this.field_39682.method_44629("trial availablity", realmsClient::trialAvailable, Duration.ofSeconds(60L), class_7587.method_44644(60));
		this.field_39687 = this.field_39682.method_44629("unread news", realmsClient::getNews, Duration.ofMinutes(5L), class_7587.field_39714);
	}
}
