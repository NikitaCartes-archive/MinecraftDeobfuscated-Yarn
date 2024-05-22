package net.minecraft.client.font;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
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
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.resource.DependencyTracker;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceFinder;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloader;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.profiler.Profiler;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class FontManager implements ResourceReloader, AutoCloseable {
	static final Logger LOGGER = LogUtils.getLogger();
	private static final String FONTS_JSON = "fonts.json";
	public static final Identifier MISSING_STORAGE_ID = Identifier.ofVanilla("missing");
	private static final ResourceFinder FINDER = ResourceFinder.json("font");
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
	private final FontStorage missingStorage;
	private final List<Font> fonts = new ArrayList();
	private final Map<Identifier, FontStorage> fontStorages = new HashMap();
	private final TextureManager textureManager;
	@Nullable
	private volatile FontStorage currentStorage;

	public FontManager(TextureManager manager) {
		this.textureManager = manager;
		this.missingStorage = Util.make(new FontStorage(manager, MISSING_STORAGE_ID), fontStorage -> fontStorage.setFonts(List.of(createEmptyFont()), Set.of()));
	}

	private static Font.FontFilterPair createEmptyFont() {
		return new Font.FontFilterPair(new BlankFont(), FontFilterType.FilterMap.NO_FILTER);
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
				List<Pair<FontManager.FontKey, FontLoader.Provider>> listx = loadFontProviders((List<Resource>)entry.getValue(), identifier);
				FontManager.FontEntry fontEntry = new FontManager.FontEntry(identifier);

				for (Pair<FontManager.FontKey, FontLoader.Provider> pair : listx) {
					FontManager.FontKey fontKey = pair.getFirst();
					FontFilterType.FilterMap filterMap = pair.getSecond().filter();
					pair.getSecond().definition().build().ifLeft(loadable -> {
						CompletableFuture<Optional<Font>> completableFuture = this.load(fontKey, loadable, resourceManager, executor);
						fontEntry.addBuilder(fontKey, filterMap, completableFuture);
					}).ifRight(reference -> fontEntry.addReferenceBuilder(fontKey, filterMap, reference));
				}

				return fontEntry;
			}, executor));
		}

		return Util.combineSafe(list)
			.thenCompose(
				entries -> {
					List<CompletableFuture<Optional<Font>>> listx = (List<CompletableFuture<Optional<Font>>>)entries.stream()
						.flatMap(FontManager.FontEntry::getImmediateProviders)
						.collect(Util.toArrayList());
					Font.FontFilterPair fontFilterPair = createEmptyFont();
					listx.add(CompletableFuture.completedFuture(Optional.of(fontFilterPair.provider())));
					return Util.combineSafe(listx)
						.thenCompose(
							providers -> {
								Map<Identifier, List<Font.FontFilterPair>> map = this.getRequiredFontProviders(entries);
								CompletableFuture<?>[] completableFutures = (CompletableFuture<?>[])map.values()
									.stream()
									.map(dest -> CompletableFuture.runAsync(() -> this.insertFont(dest, fontFilterPair), executor))
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

	private Map<Identifier, List<Font.FontFilterPair>> getRequiredFontProviders(List<FontManager.FontEntry> entries) {
		Map<Identifier, List<Font.FontFilterPair>> map = new HashMap();
		DependencyTracker<Identifier, FontManager.FontEntry> dependencyTracker = new DependencyTracker<>();
		entries.forEach(entry -> dependencyTracker.add(entry.fontId, entry));
		dependencyTracker.traverse((dependent, fontEntry) -> fontEntry.getRequiredFontProviders(map::get).ifPresent(fonts -> map.put(dependent, fonts)));
		return map;
	}

	private void insertFont(List<Font.FontFilterPair> fonts, Font.FontFilterPair font) {
		fonts.add(0, font);
		IntSet intSet = new IntOpenHashSet();

		for (Font.FontFilterPair fontFilterPair : fonts) {
			intSet.addAll(fontFilterPair.provider().getProvidedGlyphs());
		}

		intSet.forEach(codePoint -> {
			if (codePoint != 32) {
				for (Font.FontFilterPair fontFilterPairx : Lists.reverse(fonts)) {
					if (fontFilterPairx.provider().getGlyph(codePoint) != null) {
						break;
					}
				}
			}
		});
	}

	private static Set<FontFilterType> getActiveFilters(GameOptions options) {
		Set<FontFilterType> set = EnumSet.noneOf(FontFilterType.class);
		if (options.getForceUnicodeFont().getValue()) {
			set.add(FontFilterType.UNIFORM);
		}

		if (options.getJapaneseGlyphVariants().getValue()) {
			set.add(FontFilterType.JAPANESE_VARIANTS);
		}

		return set;
	}

	private void reload(FontManager.ProviderIndex index, Profiler profiler) {
		profiler.startTick();
		profiler.push("closing");
		this.currentStorage = null;
		this.fontStorages.values().forEach(FontStorage::close);
		this.fontStorages.clear();
		this.fonts.forEach(Font::close);
		this.fonts.clear();
		Set<FontFilterType> set = getActiveFilters(MinecraftClient.getInstance().options);
		profiler.swap("reloading");
		index.fontSets().forEach((id, fonts) -> {
			FontStorage fontStorage = new FontStorage(this.textureManager, id);
			fontStorage.setFonts(Lists.reverse(fonts), set);
			this.fontStorages.put(id, fontStorage);
		});
		this.fonts.addAll(index.allProviders);
		profiler.pop();
		profiler.endTick();
		if (!this.fontStorages.containsKey(MinecraftClient.DEFAULT_FONT_ID)) {
			throw new IllegalStateException("Default font failed to load");
		}
	}

	public void setActiveFilters(GameOptions options) {
		Set<FontFilterType> set = getActiveFilters(options);

		for (FontStorage fontStorage : this.fontStorages.values()) {
			fontStorage.setActiveFilters(set);
		}
	}

	private static List<Pair<FontManager.FontKey, FontLoader.Provider>> loadFontProviders(List<Resource> fontResources, Identifier id) {
		List<Pair<FontManager.FontKey, FontLoader.Provider>> list = new ArrayList();

		for (Resource resource : fontResources) {
			try {
				Reader reader = resource.getReader();

				try {
					JsonElement jsonElement = GSON.fromJson(reader, JsonElement.class);
					FontManager.Providers providers = FontManager.Providers.CODEC.parse(JsonOps.INSTANCE, jsonElement).getOrThrow(JsonParseException::new);
					List<FontLoader.Provider> list2 = providers.providers;

					for (int i = list2.size() - 1; i >= 0; i--) {
						FontManager.FontKey fontKey = new FontManager.FontKey(id, resource.getPackId(), i);
						list.add(Pair.of(fontKey, (FontLoader.Provider)list2.get(i)));
					}
				} catch (Throwable var12) {
					if (reader != null) {
						try {
							reader.close();
						} catch (Throwable var11) {
							var12.addSuppressed(var11);
						}
					}

					throw var12;
				}

				if (reader != null) {
					reader.close();
				}
			} catch (Exception var13) {
				LOGGER.warn("Unable to load font '{}' in {} in resourcepack: '{}'", id, "fonts.json", resource.getPackId(), var13);
			}
		}

		return list;
	}

	public TextRenderer createTextRenderer() {
		return new TextRenderer(this::getStorage, false);
	}

	public TextRenderer createAdvanceValidatingTextRenderer() {
		return new TextRenderer(this::getStorage, true);
	}

	private FontStorage getStorageInternal(Identifier id) {
		return (FontStorage)this.fontStorages.getOrDefault(id, this.missingStorage);
	}

	private FontStorage getStorage(Identifier id) {
		FontStorage fontStorage = this.currentStorage;
		if (fontStorage != null && id.equals(fontStorage.getId())) {
			return fontStorage;
		} else {
			FontStorage fontStorage2 = this.getStorageInternal(id);
			this.currentStorage = fontStorage2;
			return fontStorage2;
		}
	}

	public void close() {
		this.fontStorages.values().forEach(FontStorage::close);
		this.fonts.forEach(Font::close);
		this.missingStorage.close();
	}

	@Environment(EnvType.CLIENT)
	static record Builder(FontManager.FontKey id, FontFilterType.FilterMap filter, Either<CompletableFuture<Optional<Font>>, Identifier> result) {

		public Optional<List<Font.FontFilterPair>> build(Function<Identifier, List<Font.FontFilterPair>> fontRetriever) {
			return this.result
				.map(
					future -> ((Optional)future.join()).map(font -> List.of(new Font.FontFilterPair(font, this.filter))),
					referee -> {
						List<Font.FontFilterPair> list = (List<Font.FontFilterPair>)fontRetriever.apply(referee);
						if (list == null) {
							FontManager.LOGGER
								.warn("Can't find font {} referenced by builder {}, either because it's missing, failed to load or is part of loading cycle", referee, this.id);
							return Optional.empty();
						} else {
							return Optional.of(list.stream().map(this::applyFilter).toList());
						}
					}
				);
		}

		private Font.FontFilterPair applyFilter(Font.FontFilterPair font) {
			return new Font.FontFilterPair(font.provider(), this.filter.apply(font.filter()));
		}
	}

	@Environment(EnvType.CLIENT)
	static record FontEntry(Identifier fontId, List<FontManager.Builder> builders, Set<Identifier> dependencies)
		implements DependencyTracker.Dependencies<Identifier> {

		public FontEntry(Identifier fontId) {
			this(fontId, new ArrayList(), new HashSet());
		}

		public void addReferenceBuilder(FontManager.FontKey key, FontFilterType.FilterMap filters, FontLoader.Reference reference) {
			this.builders.add(new FontManager.Builder(key, filters, Either.right(reference.id())));
			this.dependencies.add(reference.id());
		}

		public void addBuilder(FontManager.FontKey key, FontFilterType.FilterMap filters, CompletableFuture<Optional<Font>> fontFuture) {
			this.builders.add(new FontManager.Builder(key, filters, Either.left(fontFuture)));
		}

		private Stream<CompletableFuture<Optional<Font>>> getImmediateProviders() {
			return this.builders.stream().flatMap(builder -> builder.result.left().stream());
		}

		public Optional<List<Font.FontFilterPair>> getRequiredFontProviders(Function<Identifier, List<Font.FontFilterPair>> fontRetriever) {
			List<Font.FontFilterPair> list = new ArrayList();

			for (FontManager.Builder builder : this.builders) {
				Optional<List<Font.FontFilterPair>> optional = builder.build(fontRetriever);
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
	static record ProviderIndex(Map<Identifier, List<Font.FontFilterPair>> fontSets, List<Font> allProviders) {
	}

	@Environment(EnvType.CLIENT)
	static record Providers(List<FontLoader.Provider> providers) {
		public static final Codec<FontManager.Providers> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(FontLoader.Provider.CODEC.listOf().fieldOf("providers").forGetter(FontManager.Providers::providers))
					.apply(instance, FontManager.Providers::new)
		);
	}
}
