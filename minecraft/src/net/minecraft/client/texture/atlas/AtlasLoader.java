package net.minecraft.client.texture.atlas;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.function.Predicate;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.MissingSprite;
import net.minecraft.client.texture.SpriteContents;
import net.minecraft.client.texture.SpriteOpener;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceFinder;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class AtlasLoader {
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final ResourceFinder FINDER = new ResourceFinder("atlases", ".json");
	private final List<AtlasSource> sources;

	private AtlasLoader(List<AtlasSource> sources) {
		this.sources = sources;
	}

	public List<Function<SpriteOpener, SpriteContents>> loadSources(ResourceManager resourceManager) {
		final Map<Identifier, AtlasSource.SpriteRegion> map = new HashMap();
		AtlasSource.SpriteRegions spriteRegions = new AtlasSource.SpriteRegions() {
			@Override
			public void add(Identifier arg, AtlasSource.SpriteRegion region) {
				AtlasSource.SpriteRegion spriteRegion = (AtlasSource.SpriteRegion)map.put(arg, region);
				if (spriteRegion != null) {
					spriteRegion.close();
				}
			}

			@Override
			public void removeIf(Predicate<Identifier> predicate) {
				Iterator<Entry<Identifier, AtlasSource.SpriteRegion>> iterator = map.entrySet().iterator();

				while (iterator.hasNext()) {
					Entry<Identifier, AtlasSource.SpriteRegion> entry = (Entry<Identifier, AtlasSource.SpriteRegion>)iterator.next();
					if (predicate.test((Identifier)entry.getKey())) {
						((AtlasSource.SpriteRegion)entry.getValue()).close();
						iterator.remove();
					}
				}
			}
		};
		this.sources.forEach(source -> source.load(resourceManager, spriteRegions));
		Builder<Function<SpriteOpener, SpriteContents>> builder = ImmutableList.builder();
		builder.add(opener -> MissingSprite.createSpriteContents());
		builder.addAll(map.values());
		return builder.build();
	}

	public static AtlasLoader of(ResourceManager resourceManager, Identifier id) {
		Identifier identifier = FINDER.toResourcePath(id);
		List<AtlasSource> list = new ArrayList();

		for (Resource resource : resourceManager.getAllResources(identifier)) {
			try {
				BufferedReader bufferedReader = resource.getReader();

				try {
					Dynamic<JsonElement> dynamic = new Dynamic<>(JsonOps.INSTANCE, JsonParser.parseReader(bufferedReader));
					list.addAll((Collection)AtlasSourceManager.LIST_CODEC.parse(dynamic).getOrThrow());
				} catch (Throwable var10) {
					if (bufferedReader != null) {
						try {
							bufferedReader.close();
						} catch (Throwable var9) {
							var10.addSuppressed(var9);
						}
					}

					throw var10;
				}

				if (bufferedReader != null) {
					bufferedReader.close();
				}
			} catch (Exception var11) {
				LOGGER.error("Failed to parse atlas definition {} in pack {}", identifier, resource.getPackId(), var11);
			}
		}

		return new AtlasLoader(list);
	}
}
