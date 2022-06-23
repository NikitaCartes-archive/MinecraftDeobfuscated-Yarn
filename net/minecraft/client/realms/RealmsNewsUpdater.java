/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.realms;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.dto.RealmsNews;
import net.minecraft.client.realms.util.RealmsPersistence;

@Environment(value=EnvType.CLIENT)
public class RealmsNewsUpdater {
    private final RealmsPersistence persistence;
    private boolean hasUnreadNews;
    private String newsLink;

    public RealmsNewsUpdater(RealmsPersistence persistence) {
        this.persistence = persistence;
        RealmsPersistence.RealmsPersistenceData realmsPersistenceData = persistence.load();
        this.hasUnreadNews = realmsPersistenceData.hasUnreadNews;
        this.newsLink = realmsPersistenceData.newsLink;
    }

    public boolean hasUnreadNews() {
        return this.hasUnreadNews;
    }

    public String getNewsLink() {
        return this.newsLink;
    }

    public void updateNews(RealmsNews news) {
        RealmsPersistence.RealmsPersistenceData realmsPersistenceData = this.checkLinkUpdated(news);
        this.hasUnreadNews = realmsPersistenceData.hasUnreadNews;
        this.newsLink = realmsPersistenceData.newsLink;
    }

    private RealmsPersistence.RealmsPersistenceData checkLinkUpdated(RealmsNews news) {
        boolean bl;
        RealmsPersistence.RealmsPersistenceData realmsPersistenceData = new RealmsPersistence.RealmsPersistenceData();
        realmsPersistenceData.newsLink = news.newsLink;
        RealmsPersistence.RealmsPersistenceData realmsPersistenceData2 = this.persistence.load();
        boolean bl2 = bl = realmsPersistenceData.newsLink == null || realmsPersistenceData.newsLink.equals(realmsPersistenceData2.newsLink);
        if (bl) {
            return realmsPersistenceData2;
        }
        realmsPersistenceData.hasUnreadNews = true;
        this.persistence.save(realmsPersistenceData);
        return realmsPersistenceData;
    }
}

