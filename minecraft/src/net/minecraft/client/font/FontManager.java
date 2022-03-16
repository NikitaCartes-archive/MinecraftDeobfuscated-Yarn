package net.minecraft.client.font;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceRef;
import net.minecraft.resource.ResourceReloader;
import net.minecraft.resource.SinglePreparationResourceReloader;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.Util;
import net.minecraft.util.profiler.Profiler;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class FontManager implements AutoCloseable {
	static final Logger LOGGER = LogUtils.getLogger();
	private static final String FONTS_JSON = "fonts.json";
	public static final Identifier MISSING_STORAGE_ID = new Identifier("minecraft", "missing");
	private final FontStorage missingStorage;
	final Map<Identifier, FontStorage> fontStorages = Maps.<Identifier, FontStorage>newHashMap();
	final TextureManager textureManager;
	private Map<Identifier, Identifier> idOverrides = ImmutableMap.of();
	private final ResourceReloader resourceReloadListener = new SinglePreparationResourceReloader<Map<Identifier, List<Font>>>() {
		protected Map<Identifier, List<Font>> prepare(ResourceManager resourceManager, Profiler profiler) {
			profiler.startTick();
			Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
			Map<Identifier, List<Font>> map = Maps.<Identifier, List<Font>>newHashMap();

			for (Entry<Identifier, List<ResourceRef>> entry : resourceManager.findAllResources("font", id -> id.getPath().endsWith(".json")).entrySet()) {
				Identifier identifier = (Identifier)entry.getKey();
				String string = identifier.getPath();
				Identifier identifier2 = new Identifier(identifier.getNamespace(), string.substring("font/".length(), string.length() - ".json".length()));
				List<Font> list = (List<Font>)map.computeIfAbsent(identifier2, id -> Lists.<Font>newArrayList(new BlankFont()));
				profiler.push(identifier2::toString);

				for (ResourceRef resourceRef : (List)entry.getValue()) {
					profiler.push(resourceRef.getPackName());

					try {
						Resource resource = resourceRef.open();

						try {
							InputStream inputStream = resource.getInputStream();

							try {
								Reader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

								try {
									try {
										profiler.push("reading");
										JsonArray jsonArray = JsonHelper.getArray(JsonHelper.deserialize(gson, reader, JsonObject.class), "providers");
										profiler.swap("parsing");

										for (int i = jsonArray.size() - 1; i >= 0; i--) {
											JsonObject jsonObject = JsonHelper.asObject(jsonArray.get(i), "providers[" + i + "]");
											String string2 = JsonHelper.getString(jsonObject, "type");
											FontType fontType = FontType.byId(string2);

											try {
												profiler.push(string2);
												Font font = fontType.createLoader(jsonObject).load(resourceManager);
												if (font != null) {
													list.add(font);
												}
											} finally {
												profiler.pop();
											}
										}
									} finally {
										profiler.pop();
									}
								} catch (Throwable var47) {
									try {
										reader.close();
									} catch (Throwable var44) {
										var47.addSuppressed(var44);
									}

									throw var47;
								}

								reader.close();
							} catch (Throwable var48) {
								if (inputStream != null) {
									try {
										inputStream.close();
									} catch (Throwable var43) {
										var48.addSuppressed(var43);
									}
								}

								throw var48;
							}

							if (inputStream != null) {
								inputStream.close();
							}
						} catch (Throwable var49) {
							if (resource != null) {
								try {
									resource.close();
								} catch (Throwable var42) {
									var49.addSuppressed(var42);
								}
							}

							throw var49;
						}

						if (resource != null) {
							resource.close();
						}
					} catch (Exception var50) {
						FontManager.LOGGER.warn("Unable to load font '{}' in {} in resourcepack: '{}'", identifier2, "fonts.json", resourceRef.getPackName(), var50);
					}

					profiler.pop();
				}

				profiler.push("caching");
				IntSet intSet = new IntOpenHashSet();

				for (Font font2 : list) {
					intSet.addAll(font2.getProvidedGlyphs());
				}

				intSet.forEach(codePoint -> {
					if (codePoint != 32) {
						for (Font fontx : Lists.reverse(list)) {
							if (fontx.getGlyph(codePoint) != null) {
								break;
							}
						}
					}
				});
				profiler.pop();
				profiler.pop();
			}

			profiler.endTick();
			return map;
		}

		protected void apply(Map<Identifier, List<Font>> map, ResourceManager resourceManager, Profiler profiler) {
			profiler.startTick();
			profiler.push("closing");
			FontManager.this.fontStorages.values().forEach(FontStorage::close);
			FontManager.this.fontStorages.clear();
			profiler.swap("reloading");
			map.forEach((id, fonts) -> {
				FontStorage fontStorage = new FontStorage(FontManager.this.textureManager, id);
				fontStorage.setFonts(Lists.reverse(fonts));
				FontManager.this.fontStorages.put(id, fontStorage);
			});
			profiler.pop();
			profiler.endTick();
		}

		@Override
		public String getName() {
			return "FontManager";
		}
	};

	public FontManager(TextureManager manager) {
		this.textureManager = manager;
		this.missingStorage = Util.make(new FontStorage(manager, MISSING_STORAGE_ID), fontStorage -> fontStorage.setFonts(Lists.<Font>newArrayList(new BlankFont())));
	}

	public void setIdOverrides(Map<Identifier, Identifier> idOverrides) {
		this.idOverrides = idOverrides;
	}

	public TextRenderer createTextRenderer() {
		return new TextRenderer(id -> (FontStorage)this.fontStorages.getOrDefault(this.idOverrides.getOrDefault(id, id), this.missingStorage));
	}

	public ResourceReloader getResourceReloadListener() {
		return this.resourceReloadListener;
	}

	public void close() {
		this.fontStorages.values().forEach(FontStorage::close);
		this.missingStorage.close();
	}
}
