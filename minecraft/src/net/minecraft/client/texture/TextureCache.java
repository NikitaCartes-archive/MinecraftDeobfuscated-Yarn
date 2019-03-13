package net.minecraft.client.texture;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BannerPattern;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.SystemUtil;

@Environment(EnvType.CLIENT)
public class TextureCache {
	public static final TextureCache.Manager BANNER = new TextureCache.Manager(
		"banner_", new Identifier("textures/entity/banner_base.png"), "textures/entity/banner/"
	);
	public static final TextureCache.Manager SHIELD = new TextureCache.Manager(
		"shield_", new Identifier("textures/entity/shield_base.png"), "textures/entity/shield/"
	);
	public static final Identifier field_4152 = new Identifier("textures/entity/shield_base_nopattern.png");
	public static final Identifier field_4153 = new Identifier("textures/entity/banner/base.png");

	@Environment(EnvType.CLIENT)
	static class Entry {
		public long lastRequestTimeMillis;
		public Identifier field_4160;

		private Entry() {
		}
	}

	@Environment(EnvType.CLIENT)
	public static class Manager {
		private final Map<String, TextureCache.Entry> cacheMap = Maps.<String, TextureCache.Entry>newLinkedHashMap();
		private final Identifier field_4157;
		private final String baseDir;
		private final String id;

		public Manager(String string, Identifier identifier, String string2) {
			this.id = string;
			this.field_4157 = identifier;
			this.baseDir = string2;
		}

		@Nullable
		public Identifier method_3331(String string, List<BannerPattern> list, List<DyeColor> list2) {
			if (string.isEmpty()) {
				return null;
			} else if (!list.isEmpty() && !list2.isEmpty()) {
				string = this.id + string;
				TextureCache.Entry entry = (TextureCache.Entry)this.cacheMap.get(string);
				if (entry == null) {
					if (this.cacheMap.size() >= 256 && !this.removeOldEntries()) {
						return TextureCache.field_4153;
					}

					List<String> list3 = Lists.<String>newArrayList();

					for (BannerPattern bannerPattern : list) {
						list3.add(this.baseDir + bannerPattern.getName() + ".png");
					}

					entry = new TextureCache.Entry();
					entry.field_4160 = new Identifier(string);
					MinecraftClient.getInstance().method_1531().method_4616(entry.field_4160, new BannerTexture(this.field_4157, list3, list2));
					this.cacheMap.put(string, entry);
				}

				entry.lastRequestTimeMillis = SystemUtil.getMeasuringTimeMs();
				return entry.field_4160;
			} else {
				return MissingSprite.method_4539();
			}
		}

		private boolean removeOldEntries() {
			long l = SystemUtil.getMeasuringTimeMs();
			Iterator<String> iterator = this.cacheMap.keySet().iterator();

			while (iterator.hasNext()) {
				String string = (String)iterator.next();
				TextureCache.Entry entry = (TextureCache.Entry)this.cacheMap.get(string);
				if (l - entry.lastRequestTimeMillis > 5000L) {
					MinecraftClient.getInstance().method_1531().method_4615(entry.field_4160);
					iterator.remove();
					return true;
				}
			}

			return this.cacheMap.size() < 256;
		}
	}
}
