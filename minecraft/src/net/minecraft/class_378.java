package net.minecraft;

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
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.FontRenderer;
import net.minecraft.client.font.FontStorage;
import net.minecraft.client.font.FontType;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloadListener;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class class_378 implements ResourceReloadListener {
	private static final Logger field_2261 = LogManager.getLogger();
	private final Map<Identifier, FontRenderer> field_2259 = Maps.<Identifier, FontRenderer>newHashMap();
	private final TextureManager field_2260;
	private boolean forceUnicodeFont;

	public class_378(TextureManager textureManager, boolean bl) {
		this.field_2260 = textureManager;
		this.forceUnicodeFont = bl;
	}

	@Override
	public void onResourceReload(ResourceManager resourceManager) {
		Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
		Map<Identifier, List<class_390>> map = Maps.<Identifier, List<class_390>>newHashMap();

		for (Identifier identifier : resourceManager.findResources("font", stringx -> stringx.endsWith(".json"))) {
			String string = identifier.getPath();
			Identifier identifier2 = new Identifier(identifier.getNamespace(), string.substring("font/".length(), string.length() - ".json".length()));
			List<class_390> list = (List<class_390>)map.computeIfAbsent(identifier2, identifierx -> Lists.<class_390>newArrayList(new class_376()));

			try {
				for (Resource resource : resourceManager.getAllResources(identifier)) {
					try {
						InputStream inputStream = resource.getInputStream();
						Throwable var12 = null;

						try {
							JsonArray jsonArray = JsonHelper.getArray(
								JsonHelper.deserialize(gson, IOUtils.toString(inputStream, StandardCharsets.UTF_8), JsonObject.class), "providers"
							);

							for (int i = jsonArray.size() - 1; i >= 0; i--) {
								JsonObject jsonObject = JsonHelper.asObject(jsonArray.get(i), "providers[" + i + "]");

								try {
									FontType fontType = FontType.byId(JsonHelper.getString(jsonObject, "type"));
									if (!this.forceUnicodeFont || fontType == FontType.field_2313 || !identifier2.equals(MinecraftClient.field_1740)) {
										class_390 lv = fontType.create(jsonObject).load(resourceManager);
										if (lv != null) {
											list.add(lv);
										}
									}
								} catch (RuntimeException var28) {
									field_2261.warn("Unable to read definition '{}' in fonts.json in resourcepack: '{}': {}", identifier2, resource.getPackName(), var28.getMessage());
								}
							}
						} catch (Throwable var29) {
							var12 = var29;
							throw var29;
						} finally {
							if (inputStream != null) {
								if (var12 != null) {
									try {
										inputStream.close();
									} catch (Throwable var27) {
										var12.addSuppressed(var27);
									}
								} else {
									inputStream.close();
								}
							}
						}
					} catch (RuntimeException var31) {
						field_2261.warn("Unable to load font '{}' in fonts.json in resourcepack: '{}': {}", identifier2, resource.getPackName(), var31.getMessage());
					}
				}
			} catch (IOException var32) {
				field_2261.warn("Unable to load font '{}' in fonts.json: {}", identifier2, var32.getMessage());
			}
		}

		Stream.concat(this.field_2259.keySet().stream(), map.keySet().stream())
			.distinct()
			.forEach(
				identifierx -> {
					List<class_390> listx = (List<class_390>)map.getOrDefault(identifierx, Collections.emptyList());
					Collections.reverse(listx);
					((FontRenderer)this.field_2259
							.computeIfAbsent(identifierx, identifierxx -> new FontRenderer(this.field_2260, new FontStorage(this.field_2260, identifierxx))))
						.method_1715(listx);
				}
			);
	}

	@Nullable
	public FontRenderer method_2019(Identifier identifier) {
		return (FontRenderer)this.field_2259.computeIfAbsent(identifier, identifierx -> {
			FontRenderer fontRenderer = new FontRenderer(this.field_2260, new FontStorage(this.field_2260, identifierx));
			fontRenderer.method_1715(Lists.<class_390>newArrayList(new class_376()));
			return fontRenderer;
		});
	}

	public void setForceUnicodeFont(boolean bl) {
		if (bl != this.forceUnicodeFont) {
			this.forceUnicodeFont = bl;
			this.onResourceReload(MinecraftClient.getInstance().getResourceManager());
		}
	}
}
