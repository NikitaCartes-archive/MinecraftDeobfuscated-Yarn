package net.minecraft.client.font;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.resource.DependencyTracker;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceFinder;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloader;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.Util;
import net.minecraft.util.profiler.Profiler;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class FontManager implements ResourceReloader, AutoCloseable {
	static final Logger LOGGER = LogUtils.getLogger();
	private static final String FONTS_JSON = "fonts.json";
	public static final Identifier MISSING_STORAGE_ID = new Identifier("minecraft", "missing");
	private static final ResourceFinder FINDER = ResourceFinder.json("font");
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
	private final FontStorage missingStorage;
	private final List<Font> fonts = new ArrayList();
	private final Map<Identifier, FontStorage> fontStorages = new HashMap();
	private final TextureManager textureManager;
	private Map<Identifier, Identifier> idOverrides = ImmutableMap.of();

	public FontManager(TextureManager manager) {
		this.textureManager = manager;
		this.missingStorage = Util.make(new FontStorage(manager, MISSING_STORAGE_ID), fontStorage -> fontStorage.setFonts(Lists.<Font>newArrayList(new BlankFont())));
	}

	@Override
	public CompletableFuture<Void> reload(
		ResourceReloader.Synchronizer synchronizer,
		ResourceManager manager,
		Profiler prepareProfiler,
		Profiler applyProfiler,
		Executor prepareExecutor,
		Executor applyExecutor
	) {
		prepareProfiler.startTick();
		prepareProfiler.endTick();
		return this.loadIndex(manager, prepareExecutor)
			.thenCompose(synchronizer::whenPrepared)
			.thenAcceptAsync(index -> this.reload(index, applyProfiler), applyExecutor);
	}

	private CompletableFuture<FontManager.ProviderIndex> loadIndex(ResourceManager resourceManager, Executor executor) {
		List<CompletableFuture<FontManager.FontEntry>> list = new ArrayList();

		for (Entry<Identifier, List<Resource>> entry : FINDER.findAllResources(resourceManager).entrySet()) {
			Identifier identifier = FINDER.toResourceId((Identifier)entry.getKey());
			list.add(CompletableFuture.supplyAsync(() -> {
				List<Pair<FontManager.FontKey, FontLoader>> listx = loadFontProviders((List<Resource>)entry.getValue(), identifier);
				FontManager.FontEntry fontEntry = new FontManager.FontEntry(identifier);

				for (Pair<FontManager.FontKey, FontLoader> pair : listx) {
					FontManager.FontKey fontKey = pair.getFirst();
					pair.getSecond().build().ifLeft(loadable -> {
						CompletableFuture<Optional<Font>> completableFuture = this.load(fontKey, loadable, resourceManager, executor);
						fontEntry.addBuilder(fontKey, completableFuture);
					}).ifRight(reference -> fontEntry.addReferenceBuilder(fontKey, reference));
				}

				return fontEntry;
			}, executor));
		}

		return Util.combineSafe(list)
			.thenCompose(
				entries -> {
					List<CompletableFuture<Optional<Font>>> listx = (List<CompletableFuture<Optional<Font>>>)entries.stream()
						.flatMap(FontManager.FontEntry::getImmediateProviders)
						.collect(Collectors.toCollection(ArrayList::new));
					Font font = new BlankFont();
					listx.add(CompletableFuture.completedFuture(Optional.of(font)));
					return Util.combineSafe(listx)
						.thenCompose(
							providers -> {
								Map<Identifier, List<Font>> map = this.getRequiredFontProviders(entries);
								CompletableFuture<?>[] completableFutures = (CompletableFuture<?>[])map.values()
									.stream()
									.map(dest -> CompletableFuture.runAsync(() -> this.insertFont(dest, font), executor))
									.toArray(CompletableFuture[]::new);
								return CompletableFuture.allOf(completableFutures).thenApply(ignored -> {
									List<Font> list2 = providers.stream().flatMap(Optional::stream).toList();
									return new FontManager.ProviderIndex(map, list2);
								});
							}
						);
				}
			);
	}

	private CompletableFuture<Optional<Font>> load(FontManager.FontKey key, FontLoader.Loadable loadable, ResourceManager resourceManager, Executor executor) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				return Optional.of(loadable.load(resourceManager));
			} catch (Exception var4) {
				LOGGER.warn("Failed to load builder {}, rejecting", key, var4);
				return Optional.empty();
			}
		}, executor);
	}

	private Map<Identifier, List<Font>> getRequiredFontProviders(List<FontManager.FontEntry> entries) {
		Map<Identifier, List<Font>> map = new HashMap();
		DependencyTracker<Identifier, FontManager.FontEntry> dependencyTracker = new DependencyTracker<>();
		entries.forEach(entry -> dependencyTracker.add(entry.fontId, entry));
		dependencyTracker.traverse((dependent, fontEntry) -> fontEntry.getRequiredFontProviders(map::get).ifPresent(fonts -> map.put(dependent, fonts)));
		return map;
	}

	private void insertFont(List<Font> fonts, Font font) {
		fonts.add(0, font);
		IntSet intSet = new IntOpenHashSet();

		for (Font font2 : fonts) {
			intSet.addAll(font2.getProvidedGlyphs());
		}

		intSet.forEach(codePoint -> {
			if (codePoint != 32) {
				for (Font fontx : Lists.reverse(fonts)) {
					if (fontx.getGlyph(codePoint) != null) {
						break;
					}
				}
			}
		});
	}

	private void reload(FontManager.ProviderIndex index, Profiler profiler) {
		profiler.startTick();
		profiler.push("closing");
		this.fontStorages.values().forEach(FontStorage::close);
		this.fontStorages.clear();
		this.fonts.forEach(Font::close);
		this.fonts.clear();
		profiler.swap("reloading");
		index.providers().forEach((fontId, providers) -> {
			FontStorage fontStorage = new FontStorage(this.textureManager, fontId);
			fontStorage.setFonts(Lists.reverse(providers));
			this.fontStorages.put(fontId, fontStorage);
		});
		this.fonts.addAll(index.allProviders);
		profiler.pop();
		profiler.endTick();
		if (!this.fontStorages.containsKey(this.getEffectiveId(MinecraftClient.DEFAULT_FONT_ID))) {
			throw new IllegalStateException("Default font failed to load");
		}
	}

	private static List<Pair<FontManager.FontKey, FontLoader>> loadFontProviders(List<Resource> fontResources, Identifier id) {
		List<Pair<FontManager.FontKey, FontLoader>> list = new ArrayList();

		for (Resource resource : fontResources) {
			try {
				Reader reader = resource.getReader();

				try {
					JsonArray jsonArray = JsonHelper.getArray(JsonHelper.deserialize(GSON, reader, JsonObject.class), "providers");

					for (int i = jsonArray.size() - 1; i >= 0; i--) {
						JsonObject jsonObject = JsonHelper.asObject(jsonArray.get(i), "providers[" + i + "]");
						String string = JsonHelper.getString(jsonObject, "type");
						FontType fontType = FontType.byId(string);
						FontManager.FontKey fontKey = new FontManager.FontKey(id, resource.getResourcePackName(), i);
						list.add(Pair.of(fontKey, fontType.createLoader(jsonObject)));
					}
				} catch (Throwable var13) {
					if (reader != null) {
						try {
							reader.close();
						} catch (Throwable var12) {
							var13.addSuppressed(var12);
						}
					}

					throw var13;
				}

				if (reader != null) {
					reader.close();
				}
			} catch (Exception var14) {
				LOGGER.warn("Unable to load font '{}' in {} in resourcepack: '{}'", id, "fonts.json", resource.getResourcePackName(), var14);
			}
		}

		return list;
	}

	public void setIdOverrides(Map<Identifier, Identifier> idOverrides) {
		this.idOverrides = idOverrides;
	}

	private Identifier getEffectiveId(Identifier id) {
		return (Identifier)this.idOverrides.getOrDefault(id, id);
	}

	public TextRenderer createTextRenderer() {
		return new TextRenderer(id -> (FontStorage)this.fontStorages.getOrDefault(this.getEffectiveId(id), this.missingStorage), false);
	}

	public TextRenderer createAdvanceValidatingTextRenderer() {
		return new TextRenderer(id -> (FontStorage)this.fontStorages.getOrDefault(this.getEffectiveId(id), this.missingStorage), true);
	}

	public void close() {
		this.fontStorages.values().forEach(FontStorage::close);
		this.fonts.forEach(Font::close);
		this.missingStorage.close();
	}

	@Environment(EnvType.CLIENT)
	static record Builder(FontManager.FontKey id, Either<CompletableFuture<Optional<Font>>, Identifier> result) {

		public Optional<List<Font>> build(Function<Identifier, List<Font>> fontRetriever) {
			return this.result
				.map(
					future -> ((Optional)future.join()).map(List::of),
					referee -> {
						List<Font> list = (List<Font>)fontRetriever.apply(referee);
						if (list == null) {
							FontManager.LOGGER
								.warn("Can't find font {} referenced by builder {}, either because it's missing, failed to load or is part of loading cycle", referee, this.id);
							return Optional.empty();
						} else {
							return Optional.of(list);
						}
					}
				);
		}
	}

	@Environment(EnvType.CLIENT)
	static record FontEntry(Identifier fontId, List<FontManager.Builder> builders, Set<Identifier> dependencies)
		implements DependencyTracker.Dependencies<Identifier> {

		public FontEntry(Identifier fontId) {
			this(fontId, new ArrayList(), new HashSet());
		}

		public void addReferenceBuilder(FontManager.FontKey key, FontLoader.Reference reference) {
			this.builders.add(new FontManager.Builder(key, Either.right(reference.id())));
			this.dependencies.add(reference.id());
		}

		public void addBuilder(FontManager.FontKey key, CompletableFuture<Optional<Font>> provider) {
			this.builders.add(new FontManager.Builder(key, Either.left(provider)));
		}

		private Stream<CompletableFuture<Optional<Font>>> getImmediateProviders() {
			return this.builders.stream().flatMap(builder -> builder.result.left().stream());
		}

		public Optional<List<Font>> getRequiredFontProviders(Function<Identifier, List<Font>> fontRetriever) {
			List<Font> list = new ArrayList();

			for (FontManager.Builder builder : this.builders) {
				Optional<List<Font>> optional = builder.build(fontRetriever);
				if (!optional.isPresent()) {
					return Optional.empty();
				}

				list.addAll((Collection)optional.get());
			}

			return Optional.of(list);
		}

		@Override
		public void forDependencies(Consumer<Identifier> callback) {
			this.dependencies.forEach(callback);
		}

		@Override
		public void forOptionalDependencies(Consumer<Identifier> callback) {
		}
	}

	@Environment(EnvType.CLIENT)
	static record FontKey(Identifier fontId, String pack, int index) {
		public String toString() {
			return "(" + this.fontId + ": builder #" + this.index + " from pack " + this.pack + ")";
		}
	}

	@Environment(EnvType.CLIENT)
	static record ProviderIndex(Map<Identifier, List<Font>> providers, List<Font> allProviders) {
	}
}
