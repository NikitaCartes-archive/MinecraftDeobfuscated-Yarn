/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.font;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.BlankFont;
import net.minecraft.client.font.Font;
import net.minecraft.client.font.FontStorage;
import net.minecraft.client.font.FontType;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloadListener;
import net.minecraft.resource.SinglePreparationResourceReloadListener;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.profiler.DummyProfiler;
import net.minecraft.util.profiler.Profiler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class FontManager
implements AutoCloseable {
    private static final Logger LOGGER = LogManager.getLogger();
    private final Map<Identifier, TextRenderer> textRenderers = Maps.newHashMap();
    private final Set<Font> fonts = Sets.newHashSet();
    private final TextureManager textureManager;
    private boolean forceUnicodeFont;
    private final ResourceReloadListener resourceReloadListener = new SinglePreparationResourceReloadListener<Map<Identifier, List<Font>>>(){

        protected Map<Identifier, List<Font>> method_18638(ResourceManager resourceManager, Profiler profiler) {
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
                            for (int i = jsonArray.size() - 1; i >= 0; --i) {
                                JsonObject jsonObject = JsonHelper.asObject(jsonArray.get(i), "providers[" + i + "]");
                                try {
                                    String string22 = JsonHelper.getString(jsonObject, "type");
                                    FontType fontType = FontType.byId(string22);
                                    if (FontManager.this.forceUnicodeFont && fontType != FontType.LEGACY_UNICODE && identifier22.equals(MinecraftClient.DEFAULT_TEXT_RENDERER_ID)) continue;
                                    profiler.push(string22);
                                    list.add(fontType.createLoader(jsonObject).load(resourceManager));
                                    profiler.pop();
                                    continue;
                                } catch (RuntimeException runtimeException) {
                                    LOGGER.warn("Unable to read definition '{}' in fonts.json in resourcepack: '{}': {}", (Object)identifier22, (Object)resource.getResourcePackName(), (Object)runtimeException.getMessage());
                                }
                            }
                            profiler.pop();
                        } catch (RuntimeException runtimeException2) {
                            LOGGER.warn("Unable to load font '{}' in fonts.json in resourcepack: '{}': {}", (Object)identifier22, (Object)resource.getResourcePackName(), (Object)runtimeException2.getMessage());
                        }
                        profiler.pop();
                    }
                } catch (IOException iOException) {
                    LOGGER.warn("Unable to load font '{}' in fonts.json: {}", (Object)identifier22, (Object)iOException.getMessage());
                }
                profiler.push("caching");
                for (char c = '\u0000'; c < '\uffff'; c = (char)((char)(c + 1))) {
                    Font font;
                    if (c == ' ') continue;
                    Iterator iterator = Lists.reverse(list).iterator();
                    while (iterator.hasNext() && (font = (Font)iterator.next()).getGlyph(c) == null) {
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
            Stream.concat(FontManager.this.textRenderers.keySet().stream(), map.keySet().stream()).distinct().forEach(identifier2 -> {
                List<Font> list = map.getOrDefault(identifier2, Collections.emptyList());
                Collections.reverse(list);
                FontManager.this.textRenderers.computeIfAbsent(identifier2, identifier -> new TextRenderer(FontManager.this.textureManager, new FontStorage(FontManager.this.textureManager, (Identifier)identifier))).setFonts(list);
            });
            map.values().forEach(FontManager.this.fonts::addAll);
            profiler.pop();
            profiler.endTick();
        }

        @Override
        protected /* synthetic */ Object prepare(ResourceManager resourceManager, Profiler profiler) {
            return this.method_18638(resourceManager, profiler);
        }
    };

    public FontManager(TextureManager textureManager, boolean bl) {
        this.textureManager = textureManager;
        this.forceUnicodeFont = bl;
    }

    @Nullable
    public TextRenderer getTextRenderer(Identifier identifier2) {
        return this.textRenderers.computeIfAbsent(identifier2, identifier -> {
            TextRenderer textRenderer = new TextRenderer(this.textureManager, new FontStorage(this.textureManager, (Identifier)identifier));
            textRenderer.setFonts(Lists.newArrayList(new BlankFont()));
            return textRenderer;
        });
    }

    public void setForceUnicodeFont(boolean bl, Executor executor, Executor executor2) {
        if (bl == this.forceUnicodeFont) {
            return;
        }
        this.forceUnicodeFont = bl;
        ResourceManager resourceManager = MinecraftClient.getInstance().getResourceManager();
        ResourceReloadListener.Synchronizer synchronizer = new ResourceReloadListener.Synchronizer(){

            @Override
            public <T> CompletableFuture<T> whenPrepared(T object) {
                return CompletableFuture.completedFuture(object);
            }
        };
        this.resourceReloadListener.reload(synchronizer, resourceManager, DummyProfiler.INSTANCE, DummyProfiler.INSTANCE, executor, executor2);
    }

    public ResourceReloadListener getResourceReloadListener() {
        return this.resourceReloadListener;
    }

    @Override
    public void close() {
        this.textRenderers.values().forEach(TextRenderer::close);
        this.fonts.forEach(Font::close);
    }
}

