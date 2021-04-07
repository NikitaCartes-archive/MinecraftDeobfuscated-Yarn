/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.texture;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.realms.gui.screen.RealmsMainScreen;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.AsyncTexture;
import net.minecraft.client.texture.MissingSprite;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.texture.ResourceTexture;
import net.minecraft.client.texture.TextureTickListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloader;
import net.minecraft.util.Identifier;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.profiler.Profiler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(value=EnvType.CLIENT)
public class TextureManager
implements ResourceReloader,
TextureTickListener,
AutoCloseable {
    private static final Logger LOGGER = LogManager.getLogger();
    public static final Identifier MISSING_IDENTIFIER = new Identifier("");
    private final Map<Identifier, AbstractTexture> textures = Maps.newHashMap();
    private final Set<TextureTickListener> tickListeners = Sets.newHashSet();
    private final Map<String, Integer> dynamicIdCounters = Maps.newHashMap();
    private final ResourceManager resourceContainer;

    public TextureManager(ResourceManager resourceManager) {
        this.resourceContainer = resourceManager;
    }

    public void bindTexture(Identifier id) {
        if (!RenderSystem.isOnRenderThread()) {
            RenderSystem.recordRenderCall(() -> this.bindTextureInner(id));
        } else {
            this.bindTextureInner(id);
        }
    }

    private void bindTextureInner(Identifier id) {
        AbstractTexture abstractTexture = this.textures.get(id);
        if (abstractTexture == null) {
            abstractTexture = new ResourceTexture(id);
            this.registerTexture(id, abstractTexture);
        }
        abstractTexture.bindTexture();
    }

    public void registerTexture(Identifier id, AbstractTexture texture) {
        AbstractTexture abstractTexture = this.textures.put(id, texture = this.loadTexture(id, texture));
        if (abstractTexture != texture) {
            if (abstractTexture != null && abstractTexture != MissingSprite.getMissingSpriteTexture()) {
                this.tickListeners.remove(abstractTexture);
                this.closeTexture(id, abstractTexture);
            }
            if (texture instanceof TextureTickListener) {
                this.tickListeners.add((TextureTickListener)((Object)texture));
            }
        }
    }

    private void closeTexture(Identifier id, AbstractTexture texture) {
        if (texture != MissingSprite.getMissingSpriteTexture()) {
            try {
                texture.close();
            } catch (Exception exception) {
                LOGGER.warn("Failed to close texture {}", (Object)id, (Object)exception);
            }
        }
        texture.clearGlId();
    }

    private AbstractTexture loadTexture(Identifier id, AbstractTexture texture) {
        try {
            texture.load(this.resourceContainer);
            return texture;
        } catch (IOException iOException) {
            if (id != MISSING_IDENTIFIER) {
                LOGGER.warn("Failed to load texture: {}", (Object)id, (Object)iOException);
            }
            return MissingSprite.getMissingSpriteTexture();
        } catch (Throwable throwable) {
            CrashReport crashReport = CrashReport.create(throwable, "Registering texture");
            CrashReportSection crashReportSection = crashReport.addElement("Resource location being registered");
            crashReportSection.add("Resource location", id);
            crashReportSection.add("Texture object class", () -> texture.getClass().getName());
            throw new CrashException(crashReport);
        }
    }

    public AbstractTexture getTexture(Identifier id) {
        AbstractTexture abstractTexture = this.textures.get(id);
        if (abstractTexture == null) {
            abstractTexture = new ResourceTexture(id);
            this.registerTexture(id, abstractTexture);
        }
        return abstractTexture;
    }

    public AbstractTexture getOrDefault(Identifier id, AbstractTexture fallback) {
        return this.textures.getOrDefault(id, fallback);
    }

    public Identifier registerDynamicTexture(String prefix, NativeImageBackedTexture texture) {
        Integer integer = this.dynamicIdCounters.get(prefix);
        if (integer == null) {
            integer = 1;
        } else {
            Integer n = integer;
            Integer n2 = integer = Integer.valueOf(integer + 1);
        }
        this.dynamicIdCounters.put(prefix, integer);
        Identifier identifier = new Identifier(String.format("dynamic/%s_%d", prefix, integer));
        this.registerTexture(identifier, texture);
        return identifier;
    }

    public CompletableFuture<Void> loadTextureAsync(Identifier id, Executor executor) {
        if (!this.textures.containsKey(id)) {
            AsyncTexture asyncTexture = new AsyncTexture(this.resourceContainer, id, executor);
            this.textures.put(id, asyncTexture);
            return asyncTexture.getLoadCompleteFuture().thenRunAsync(() -> this.registerTexture(id, asyncTexture), TextureManager::runOnRenderThread);
        }
        return CompletableFuture.completedFuture(null);
    }

    private static void runOnRenderThread(Runnable runnable) {
        MinecraftClient.getInstance().execute(() -> RenderSystem.recordRenderCall(runnable::run));
    }

    @Override
    public void tick() {
        for (TextureTickListener textureTickListener : this.tickListeners) {
            textureTickListener.tick();
        }
    }

    public void destroyTexture(Identifier id) {
        AbstractTexture abstractTexture = this.getOrDefault(id, MissingSprite.getMissingSpriteTexture());
        if (abstractTexture != MissingSprite.getMissingSpriteTexture()) {
            TextureUtil.releaseTextureId(abstractTexture.getGlId());
        }
    }

    @Override
    public void close() {
        this.textures.forEach(this::closeTexture);
        this.textures.clear();
        this.tickListeners.clear();
        this.dynamicIdCounters.clear();
    }

    @Override
    public CompletableFuture<Void> reload(ResourceReloader.Synchronizer synchronizer, ResourceManager manager, Profiler prepareProfiler, Profiler applyProfiler, Executor prepareExecutor, Executor applyExecutor) {
        return ((CompletableFuture)CompletableFuture.allOf(TitleScreen.loadTexturesAsync(this, prepareExecutor), this.loadTextureAsync(AbstractButtonWidget.WIDGETS_LOCATION, prepareExecutor)).thenCompose(synchronizer::whenPrepared)).thenAcceptAsync(void_ -> {
            MissingSprite.getMissingSpriteTexture();
            RealmsMainScreen.method_23765(this.resourceContainer);
            Iterator<Map.Entry<Identifier, AbstractTexture>> iterator = this.textures.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Identifier, AbstractTexture> entry = iterator.next();
                Identifier identifier = entry.getKey();
                AbstractTexture abstractTexture = entry.getValue();
                if (abstractTexture == MissingSprite.getMissingSpriteTexture() && !identifier.equals(MissingSprite.getMissingSpriteId())) {
                    iterator.remove();
                    continue;
                }
                abstractTexture.registerTexture(this, manager, identifier, applyExecutor);
            }
        }, runnable -> RenderSystem.recordRenderCall(runnable::run));
    }
}

