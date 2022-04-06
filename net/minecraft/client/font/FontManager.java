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
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import java.io.BufferedReader;
import java.io.Reader;
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
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class FontManager
implements AutoCloseable {
    static final Logger LOGGER = LogUtils.getLogger();
    private static final String FONTS_JSON = "fonts.json";
    public static final Identifier MISSING_STORAGE_ID = new Identifier("minecraft", "missing");
    private final FontStorage missingStorage;
    final Map<Identifier, FontStorage> fontStorages = Maps.newHashMap();
    final TextureManager textureManager;
    private Map<Identifier, Identifier> idOverrides = ImmutableMap.of();
    private final ResourceReloader resourceReloadListener = new SinglePreparationResourceReloader<Map<Identifier, List<Font>>>(){

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        protected Map<Identifier, List<Font>> prepare(ResourceManager resourceManager, Profiler profiler) {
            profiler.startTick();
            Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
            HashMap<Identifier, List<Font>> map = Maps.newHashMap();
            for (Map.Entry<Identifier, List<Resource>> entry : resourceManager.findAllResources("font", id -> id.getPath().endsWith(".json")).entrySet()) {
                Identifier identifier = entry.getKey();
                String string = identifier.getPath();
                Identifier identifier2 = new Identifier(identifier.getNamespace(), string.substring("font/".length(), string.length() - ".json".length()));
                List list = map.computeIfAbsent(identifier2, id -> Lists.newArrayList(new BlankFont()));
                profiler.push(identifier2::toString);
                for (Resource resource : entry.getValue()) {
                    profiler.push(resource.getResourcePackName());
                    try (BufferedReader reader = resource.getReader();){
                        try {
                            profiler.push("reading");
                            JsonArray jsonArray = JsonHelper.getArray(JsonHelper.deserialize(gson, (Reader)reader, JsonObject.class), "providers");
                            profiler.swap("parsing");
                            for (int i = jsonArray.size() - 1; i >= 0; --i) {
                                JsonObject jsonObject = JsonHelper.asObject(jsonArray.get(i), "providers[" + i + "]");
                                String string2 = JsonHelper.getString(jsonObject, "type");
                                FontType fontType = FontType.byId(string2);
                                try {
                                    profiler.push(string2);
                                    Font font = fontType.createLoader(jsonObject).load(resourceManager);
                                    if (font == null) continue;
                                    list.add(font);
                                    continue;
                                } finally {
                                    profiler.pop();
                                }
                            }
                        } finally {
                            profiler.pop();
                        }
                    } catch (Exception exception) {
                        LOGGER.warn("Unable to load font '{}' in {} in resourcepack: '{}'", identifier2, FontManager.FONTS_JSON, resource.getResourcePackName(), exception);
                    }
                    profiler.pop();
                }
                profiler.push("caching");
                IntOpenHashSet intSet = new IntOpenHashSet();
                for (Font font2 : list) {
                    intSet.addAll(font2.getProvidedGlyphs());
                }
                intSet.forEach(codePoint -> {
                    Font font;
                    if (codePoint == 32) {
                        return;
                    }
                    Iterator iterator = Lists.reverse(list).iterator();
                    while (iterator.hasNext() && (font = (Font)iterator.next()).getGlyph(codePoint) == null) {
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
            map.forEach((id, fonts) -> {
                FontStorage fontStorage = new FontStorage(FontManager.this.textureManager, (Identifier)id);
                fontStorage.setFonts(Lists.reverse(fonts));
                FontManager.this.fontStorages.put((Identifier)id, fontStorage);
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

    public void setIdOverrides(Map<Identifier, Identifier> idOverrides) {
        this.idOverrides = idOverrides;
    }

    public TextRenderer createTextRenderer() {
        return new TextRenderer(id -> this.fontStorages.getOrDefault(this.idOverrides.getOrDefault(id, (Identifier)id), this.missingStorage));
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

