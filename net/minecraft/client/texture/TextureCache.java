/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.texture;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BannerPattern;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.BannerTexture;
import net.minecraft.client.texture.MissingSprite;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class TextureCache {
    public static final Manager BANNER = new Manager("banner_", new Identifier("textures/entity/banner_base.png"), "textures/entity/banner/");
    public static final Manager SHIELD = new Manager("shield_", new Identifier("textures/entity/shield_base.png"), "textures/entity/shield/");
    public static final Identifier DEFAULT_SHIELD = new Identifier("textures/entity/shield_base_nopattern.png");
    public static final Identifier DEFAULT_BANNER = new Identifier("textures/entity/banner/base.png");

    @Environment(value=EnvType.CLIENT)
    static class Entry {
        public long lastRequestTimeMillis;
        public Identifier filename;

        private Entry() {
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class Manager {
        private final Map<String, Entry> cacheMap = Maps.newLinkedHashMap();
        private final Identifier filename;
        private final String baseDir;
        private final String id;

        public Manager(String string, Identifier identifier, String string2) {
            this.id = string;
            this.filename = identifier;
            this.baseDir = string2;
        }

        @Nullable
        public Identifier get(String string, List<BannerPattern> list, List<DyeColor> list2) {
            if (string.isEmpty()) {
                return null;
            }
            if (list.isEmpty() || list2.isEmpty()) {
                return MissingSprite.getMissingSpriteId();
            }
            string = this.id + string;
            Entry entry = this.cacheMap.get(string);
            if (entry == null) {
                if (this.cacheMap.size() >= 256 && !this.removeOldEntries()) {
                    return DEFAULT_BANNER;
                }
                ArrayList<String> list3 = Lists.newArrayList();
                for (BannerPattern bannerPattern : list) {
                    list3.add(this.baseDir + bannerPattern.getName() + ".png");
                }
                entry = new Entry();
                entry.filename = new Identifier(string);
                MinecraftClient.getInstance().getTextureManager().registerTexture(entry.filename, new BannerTexture(this.filename, list3, list2));
                this.cacheMap.put(string, entry);
            }
            entry.lastRequestTimeMillis = Util.getMeasuringTimeMs();
            return entry.filename;
        }

        private boolean removeOldEntries() {
            long l = Util.getMeasuringTimeMs();
            Iterator<String> iterator = this.cacheMap.keySet().iterator();
            while (iterator.hasNext()) {
                String string = iterator.next();
                Entry entry = this.cacheMap.get(string);
                if (l - entry.lastRequestTimeMillis <= 5000L) continue;
                MinecraftClient.getInstance().getTextureManager().destroyTexture(entry.filename);
                iterator.remove();
                return true;
            }
            return this.cacheMap.size() < 256;
        }
    }
}

