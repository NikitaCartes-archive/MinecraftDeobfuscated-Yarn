package net.minecraft.client.font;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
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
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloadListener;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.profiler.DummyProfiler;
import net.minecraft.util.profiler.Profiler;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class FontRendererManager implements ResourceReloadListener<Map<Identifier, List<Font>>> {
	private static final Logger LOGGER = LogManager.getLogger();
	private final Map<Identifier, FontRenderer> fontRenderers = Maps.<Identifier, FontRenderer>newHashMap();
	private final TextureManager textureManager;
	private boolean forceUnicodeFont;

	public FontRendererManager(TextureManager textureManager, boolean bl) {
		this.textureManager = textureManager;
		this.forceUnicodeFont = bl;
	}

	@Override
	public CompletableFuture<Map<Identifier, List<Font>>> prepare(ResourceManager resourceManager, Profiler profiler) {
		return CompletableFuture.supplyAsync(
			() -> {
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
							profiler.push(resource::getPackName);

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
											if (!this.forceUnicodeFont || fontType == FontType.LEGACY_UNICODE || !identifier2.equals(MinecraftClient.defaultFontRendererId)) {
												profiler.push(string2);
												list.add(fontType.createLoader(jsonObject).load(resourceManager));
												profiler.pop();
											}
										} catch (RuntimeException var29) {
											LOGGER.warn("Unable to read definition '{}' in fonts.json in resourcepack: '{}': {}", identifier2, resource.getPackName(), var29.getMessage());
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
								LOGGER.warn("Unable to load font '{}' in fonts.json in resourcepack: '{}': {}", identifier2, resource.getPackName(), var32.getMessage());
							}

							profiler.pop();
						}
					} catch (IOException var33) {
						LOGGER.warn("Unable to load font '{}' in fonts.json: {}", identifier2, var33.getMessage());
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
		);
	}

	public void method_18102(ResourceManager resourceManager, Map<Identifier, List<Font>> map, Profiler profiler) {
		profiler.startTick();
		profiler.push("reloading");
		Stream.concat(this.fontRenderers.keySet().stream(), map.keySet().stream())
			.distinct()
			.forEach(
				identifier -> {
					List<Font> list = (List<Font>)map.getOrDefault(identifier, Collections.emptyList());
					Collections.reverse(list);
					((FontRenderer)this.fontRenderers
							.computeIfAbsent(identifier, identifierx -> new FontRenderer(this.textureManager, new FontStorage(this.textureManager, identifierx))))
						.method_1715(list);
				}
			);
		profiler.pop();
		profiler.endTick();
	}

	@Nullable
	public FontRenderer getFontRenderer(Identifier identifier) {
		return (FontRenderer)this.fontRenderers.computeIfAbsent(identifier, identifierx -> {
			FontRenderer fontRenderer = new FontRenderer(this.textureManager, new FontStorage(this.textureManager, identifierx));
			fontRenderer.method_1715(Lists.<Font>newArrayList(new BlankFont()));
			return fontRenderer;
		});
	}

	public void setForceUnicodeFont(boolean bl) {
		if (bl != this.forceUnicodeFont) {
			this.forceUnicodeFont = bl;
			ResourceManager resourceManager = MinecraftClient.getInstance().getResourceManager();
			this.method_18102(resourceManager, (Map<Identifier, List<Font>>)this.prepare(resourceManager, DummyProfiler.INSTANCE).join(), DummyProfiler.INSTANCE);
		}
	}
}
