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
import net.minecraft.util.Util;

@Environment(EnvType.CLIENT)
public class TextureCache {
	public static final TextureCache.Manager BANNER = new TextureCache.Manager(
		"banner_", new Identifier("textures/entity/banner_base.png"), "textures/entity/banner/"
	);
	public static final TextureCache.Manager SHIELD = new TextureCache.Manager(
		"shield_", new Identifier("textures/entity/shield_base.png"), "textures/entity/shield/"
	);
	public static final Identifier DEFAULT_SHIELD = new Identifier("textures/entity/shield_base_nopattern.png");
	public static final Identifier DEFAULT_BANNER = new Identifier("textures/entity/banner/base.png");

	@Environment(EnvType.CLIENT)
	static class Entry {
		public long lastRequestTimeMillis;
		public Identifier filename;

		private Entry() {
		}
	}

	@Environment(EnvType.CLIENT)
	public static class Manager {
		private final Map<String, TextureCache.Entry> cacheMap = Maps.<String, TextureCache.Entry>newLinkedHashMap();
		private final Identifier filename;
		private final String baseDir;
		private final String id;

		public Manager(String string, Identifier identifier, String string2) {
			this.id = string;
			this.filename = identifier;
			this.baseDir = string2;
		}

		@Nullable
		public Identifier get(String cacheKey, List<BannerPattern> patterns, List<DyeColor> colors) {
			if (cacheKey.isEmpty()) {
				return null;
			} else if (!patterns.isEmpty() && !colors.isEmpty()) {
				cacheKey = this.id + cacheKey;
				TextureCache.Entry entry = (TextureCache.Entry)this.cacheMap.get(cacheKey);
				if (entry == null) {
					if (this.cacheMap.size() >= 256 && !this.removeOldEntries()) {
						return TextureCache.DEFAULT_BANNER;
					}

					List<String> list = Lists.<String>newArrayList();

					for (BannerPattern bannerPattern : patterns) {
						list.add(this.baseDir + bannerPattern.getName() + ".png");
					}

					entry = new TextureCache.Entry();
					entry.filename = new Identifier(cacheKey);
					MinecraftClient.getInstance().getTextureManager().registerTexture(entry.filename, new BannerTexture(this.filename, list, colors));
					this.cacheMap.put(cacheKey, entry);
				}

				entry.lastRequestTimeMillis = Util.getMeasuringTimeMs();
				return entry.filename;
			} else {
				return MissingSprite.getMissingSpriteId();
			}
		}

		private boolean removeOldEntries() {
			long l = Util.getMeasuringTimeMs();
			Iterator<String> iterator = this.cacheMap.keySet().iterator();

			while (iterator.hasNext()) {
				String string = (String)iterator.next();
				TextureCache.Entry entry = (TextureCache.Entry)this.cacheMap.get(string);
				if (l - entry.lastRequestTimeMillis > 5000L) {
					MinecraftClient.getInstance().getTextureManager().destroyTexture(entry.filename);
					iterator.remove();
					return true;
				}
			}

			return this.cacheMap.size() < 256;
		}
	}
}
