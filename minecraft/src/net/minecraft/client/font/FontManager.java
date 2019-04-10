package net.minecraft.client.font;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloadListener;
import net.minecraft.resource.SupplyingResourceReloadListener;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.profiler.DummyProfiler;
import net.minecraft.util.profiler.Profiler;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class FontManager implements AutoCloseable {
	private static final Logger LOGGER = LogManager.getLogger();
	private final Map<Identifier, TextRenderer> textRenderers = Maps.<Identifier, TextRenderer>newHashMap();
	private final Set<Font> fonts = Sets.<Font>newHashSet();
	private final TextureManager textureManager;
	private boolean forceUnicodeFont;
	private final ResourceReloadListener resourceReloadListener = new SupplyingResourceReloadListener<Map<Identifier, List<Font>>>() {
		protected Map<Identifier, List<Font>> method_18638(ResourceManager resourceManager, Profiler profiler) {
			profiler.startTick();
			Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
			Map<Identifier, List<Font>> map = Maps.<Identifier, List<Font>>newHashMap();

			for (Identifier identifier : resourceManager.findResources("font", stringx -> stringx.endsWith(".json"))) {
				String string = identifier.getPath();
				Identifier identifier2 = new Identifier(identifier.getNamespace(), string.substring("font/".length(), string.length() - ".json".length()));
				List<Font> list = (List<Font>)map.computeIfAbsent(identifier2, identifierx -> Lists.<Font>newArrayList(new BlankFont()));
				profiler.push(identifier2::toString);

				try {
					for (Resource resource : resourceManager.getAllResources(identifier)) {
						profiler.push(resource::getResourcePackName);

						try {
							InputStream inputStream = resource.getInputStream();
							Throwable var13 = null;

							try {
								profiler.push("reading");
								JsonArray jsonArray = JsonHelper.getArray(
									JsonHelper.deserialize(gson, IOUtils.toString(inputStream, StandardCharsets.UTF_8), JsonObject.class), "providers"
								);
								profiler.swap("parsing");

								for (int i = jsonArray.size() - 1; i >= 0; i--) {
									JsonObject jsonObject = JsonHelper.asObject(jsonArray.get(i), "providers[" + i + "]");

									try {
										String string2 = JsonHelper.getString(jsonObject, "type");
										FontType fontType = FontType.byId(string2);
										if (!FontManager.this.forceUnicodeFont || fontType == FontType.LEGACY_UNICODE || !identifier2.equals(MinecraftClient.DEFAULT_TEXT_RENDERER_ID)) {
											profiler.push(string2);
											list.add(fontType.createLoader(jsonObject).load(resourceManager));
											profiler.pop();
										}
									} catch (RuntimeException var29) {
										FontManager.LOGGER
											.warn("Unable to read definition '{}' in fonts.json in resourcepack: '{}': {}", identifier2, resource.getResourcePackName(), var29.getMessage());
									}
								}

								profiler.pop();
							} catch (Throwable var30) {
								var13 = var30;
								throw var30;
							} finally {
								if (inputStream != null) {
									if (var13 != null) {
										try {
											inputStream.close();
										} catch (Throwable var28) {
											var13.addSuppressed(var28);
										}
									} else {
										inputStream.close();
									}
								}
							}
						} catch (RuntimeException var32) {
							FontManager.LOGGER
								.warn("Unable to load font '{}' in fonts.json in resourcepack: '{}': {}", identifier2, resource.getResourcePackName(), var32.getMessage());
						}

						profiler.pop();
					}
				} catch (IOException var33) {
					FontManager.LOGGER.warn("Unable to load font '{}' in fonts.json: {}", identifier2, var33.getMessage());
				}

				profiler.push("caching");

				for (char c = 0; c < '\uffff'; c++) {
					if (c != ' ') {
						for (Font font : Lists.reverse(list)) {
							if (font.getGlyph(c) != null) {
								break;
							}
						}
					}
				}

				profiler.pop();
				profiler.pop();
			}

			profiler.endTick();
			return map;
		}

		protected void method_18635(Map<Identifier, List<Font>> map, ResourceManager resourceManager, Profiler profiler) {
			profiler.startTick();
			profiler.push("reloading");
			Stream.concat(FontManager.this.textRenderers.keySet().stream(), map.keySet().stream())
				.distinct()
				.forEach(
					identifier -> {
						List<Font> list = (List<Font>)map.getOrDefault(identifier, Collections.emptyList());
						Collections.reverse(list);
						((TextRenderer)FontManager.this.textRenderers
								.computeIfAbsent(
									identifier, identifierx -> new TextRenderer(FontManager.this.textureManager, new FontStorage(FontManager.this.textureManager, identifierx))
								))
							.setFonts(list);
					}
				);
			map.values().forEach(FontManager.this.fonts::addAll);
			profiler.pop();
			profiler.endTick();
		}
	};

	public FontManager(TextureManager textureManager, boolean bl) {
		this.textureManager = textureManager;
		this.forceUnicodeFont = bl;
	}

	@Nullable
	public TextRenderer getTextRenderer(Identifier identifier) {
		return (TextRenderer)this.textRenderers.computeIfAbsent(identifier, identifierx -> {
			TextRenderer textRenderer = new TextRenderer(this.textureManager, new FontStorage(this.textureManager, identifierx));
			textRenderer.setFonts(Lists.<Font>newArrayList(new BlankFont()));
			return textRenderer;
		});
	}

	public void setForceUnicodeFont(boolean bl, Executor executor, Executor executor2) {
		if (bl != this.forceUnicodeFont) {
			this.forceUnicodeFont = bl;
			ResourceManager resourceManager = MinecraftClient.getInstance().getResourceManager();
			ResourceReloadListener.Helper helper = new ResourceReloadListener.Helper() {
				@Override
				public <T> CompletableFuture<T> waitForAll(T object) {
					return CompletableFuture.completedFuture(object);
				}
			};
			this.resourceReloadListener.reload(helper, resourceManager, DummyProfiler.INSTANCE, DummyProfiler.INSTANCE, executor, executor2);
		}
	}

	public ResourceReloadListener getResourceReloadListener() {
		return this.resourceReloadListener;
	}

	public void close() {
		this.textRenderers.values().forEach(TextRenderer::close);
		this.fonts.forEach(Font::close);
	}
}
