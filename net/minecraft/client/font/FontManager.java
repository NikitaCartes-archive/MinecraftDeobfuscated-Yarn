/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.font;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.BlankFont;
import net.minecraft.client.font.Font;
import net.minecraft.client.font.FontStorage;
import net.minecraft.client.font.FontType;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloader;
import net.minecraft.resource.SinglePreparationResourceReloader;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.Util;
import net.minecraft.util.profiler.Profiler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(value=EnvType.CLIENT)
public class FontManager
implements AutoCloseable {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final String FONTS_JSON = "fonts.json";
    public static final Identifier MISSING_STORAGE_ID = new Identifier("minecraft", "missing");
    private final FontStorage missingStorage;
    private final Map<Identifier, FontStorage> fontStorages = Maps.newHashMap();
    private final TextureManager textureManager;
    private Map<Identifier, Identifier> idOverrides = ImmutableMap.of();
    private final ResourceReloader resourceReloadListener = new SinglePreparationResourceReloader<Map<Identifier, List<Font>>>(){

        @Override
        protected Map<Identifier, List<Font>> prepare(ResourceManager resourceManager, Profiler profiler) {
            profiler.startTick();
            Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
            HashMap<Identifier, List<Font>> map = Maps.newHashMap();
            for (Identifier identifier2 : resourceManager.findResources("font", string -> string.endsWith(".json"))) {
                String string2 = identifier2.getPath();
                Identifier identifier22 = new Identifier(identifier2.getNamespace(), string2.substring("font/".length(), string2.length() - ".json".length()));
                List list = map.computeIfAbsent(identifier22, identifier -> Lists.newArrayList(new BlankFont()));
                profiler.push(identifier22::toString);
                try {
                    for (Resource resource : resourceManager.getAllResources(identifier2)) {
                        profiler.push(resource::getResourcePackName);
                        try (InputStream inputStream = resource.getInputStream();
                             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));){
                            profiler.push("reading");
                            JsonArray jsonArray = JsonHelper.getArray(JsonHelper.deserialize(gson, (Reader)reader, JsonObject.class), "providers");
                            profiler.swap("parsing");
                            for (int i2 = jsonArray.size() - 1; i2 >= 0; --i2) {
                                JsonObject jsonObject = JsonHelper.asObject(jsonArray.get(i2), "providers[" + i2 + "]");
                                try {
                                    String string22 = JsonHelper.getString(jsonObject, "type");
                                    FontType fontType = FontType.byId(string22);
                                    profiler.push(string22);
                                    Font font = fontType.createLoader(jsonObject).load(resourceManager);
                                    if (font != null) {
                                        list.add(font);
                                    }
                                    profiler.pop();
                                    continue;
                                } catch (RuntimeException runtimeException) {
                                    LOGGER.warn("Unable to read definition '{}' in {} in resourcepack: '{}': {}", (Object)identifier22, (Object)FontManager.FONTS_JSON, (Object)resource.getResourcePackName(), (Object)runtimeException.getMessage());
                                }
                            }
                            profiler.pop();
                        } catch (RuntimeException runtimeException2) {
                            LOGGER.warn("Unable to load font '{}' in {} in resourcepack: '{}': {}", (Object)identifier22, (Object)FontManager.FONTS_JSON, (Object)resource.getResourcePackName(), (Object)runtimeException2.getMessage());
                        }
                        profiler.pop();
                    }
                } catch (IOException iOException) {
                    LOGGER.warn("Unable to load font '{}' in {}: {}", (Object)identifier22, (Object)FontManager.FONTS_JSON, (Object)iOException.getMessage());
                }
                profiler.push("caching");
                IntOpenHashSet intSet = new IntOpenHashSet();
                for (Font font2 : list) {
                    intSet.addAll(font2.getProvidedGlyphs());
                }
                intSet.forEach(i -> {
                    Font font;
                    if (i == 32) {
                        return;
                    }
                    Iterator iterator = Lists.reverse(list).iterator();
                    while (iterator.hasNext() && (font = (Font)iterator.next()).getGlyph(i) == null) {
                    }
                });
                profiler.pop();
                profiler.pop();
            }
            profiler.endTick();
            return map;
        }

        @Override
        protected void apply(Map<Identifier, List<Font>> map, ResourceManager resourceManager, Profiler profiler) {
            profiler.startTick();
            profiler.push("closing");
            FontManager.this.fontStorages.values().forEach(FontStorage::close);
            FontManager.this.fontStorages.clear();
            profiler.swap("reloading");
            map.forEach((identifier, list) -> {
                FontStorage fontStorage = new FontStorage(FontManager.this.textureManager, (Identifier)identifier);
                fontStorage.setFonts(Lists.reverse(list));
                FontManager.this.fontStorages.put(identifier, fontStorage);
            });
            profiler.pop();
            profiler.endTick();
        }

        @Override
        public String getName() {
            return "FontManager";
        }

        @Override
        protected /* synthetic */ Object prepare(ResourceManager manager, Profiler profiler) {
            return this.prepare(manager, profiler);
        }
    };

    public FontManager(TextureManager manager) {
        this.textureManager = manager;
        this.missingStorage = Util.make(new FontStorage(manager, MISSING_STORAGE_ID), fontStorage -> fontStorage.setFonts(Lists.newArrayList(new BlankFont())));
    }

    public void setIdOverrides(Map<Identifier, Identifier> overrides) {
        this.idOverrides = overrides;
    }

    public TextRenderer createTextRenderer() {
        return new TextRenderer(identifier -> this.fontStorages.getOrDefault(this.idOverrides.getOrDefault(identifier, (Identifier)identifier), this.missingStorage));
    }

    public ResourceReloader getResourceReloadListener() {
        return this.resourceReloadListener;
    }

    @Override
    public void close() {
        this.fontStorages.values().forEach(FontStorage::close);
        this.missingStorage.close();
    }
}

