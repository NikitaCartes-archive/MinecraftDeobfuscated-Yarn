package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.dto.RealmsNews;
import net.minecraft.client.realms.util.RealmsPersistence;

@Environment(EnvType.CLIENT)
public class class_7579 {
	private final RealmsPersistence field_39689;
	private boolean field_39690;
	private String field_39691;

	public class_7579(RealmsPersistence realmsPersistence) {
		this.field_39689 = realmsPersistence;
		RealmsPersistence.RealmsPersistenceData realmsPersistenceData = realmsPersistence.load();
		this.field_39690 = realmsPersistenceData.hasUnreadNews;
		this.field_39691 = realmsPersistenceData.newsLink;
	}

	public boolean method_44618() {
		return this.field_39690;
	}

	public String method_44620() {
		return this.field_39691;
	}

	public void method_44619(RealmsNews realmsNews) {
		RealmsPersistence.RealmsPersistenceData realmsPersistenceData = this.method_44621(realmsNews);
		this.field_39690 = realmsPersistenceData.hasUnreadNews;
		this.field_39691 = realmsPersistenceData.newsLink;
	}

	private RealmsPersistence.RealmsPersistenceData method_44621(RealmsNews realmsNews) {
		RealmsPersistence.RealmsPersistenceData realmsPersistenceData = new RealmsPersistence.RealmsPersistenceData();
		realmsPersistenceData.newsLink = realmsNews.newsLink;
		RealmsPersistence.RealmsPersistenceData realmsPersistenceData2 = this.field_39689.load();
		boolean bl = realmsPersistenceData.newsLink == null || realmsPersistenceData.newsLink.equals(realmsPersistenceData2.newsLink);
		if (bl) {
			return realmsPersistenceData2;
		} else {
			realmsPersistenceData.hasUnreadNews = true;
			this.field_39689.save(realmsPersistenceData);
			return realmsPersistenceData;
		}
	}
}
