/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.texture;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.texture.AsyncTexture;
import net.minecraft.client.texture.MissingSprite;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.texture.ResourceTexture;
import net.minecraft.client.texture.Texture;
import net.minecraft.client.texture.TextureTickListener;
import net.minecraft.client.texture.TextureUtil;
import net.minecraft.client.texture.TickableTexture;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloadListener;
import net.minecraft.util.Identifier;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.profiler.Profiler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class TextureManager
implements TextureTickListener,
ResourceReloadListener {
    private static final Logger LOGGER = LogManager.getLogger();
    public static final Identifier MISSING_IDENTIFIER = new Identifier("");
    private final Map<Identifier, Texture> textures = Maps.newHashMap();
    private final List<TextureTickListener> tickListeners = Lists.newArrayList();
    private final Map<String, Integer> dynamicIdCounters = Maps.newHashMap();
    private final ResourceManager resourceContainer;

    public TextureManager(ResourceManager resourceManager) {
        this.resourceContainer = resourceManager;
    }

    public void bindTexture(Identifier identifier) {
        if (!RenderSystem.isOnRenderThread()) {
            RenderSystem.recordRenderCall(() -> this.bindTextureInner(identifier));
        } else {
            this.bindTextureInner(identifier);
        }
    }

    private void bindTextureInner(Identifier identifier) {
        Texture texture = this.textures.get(identifier);
        if (texture == null) {
            texture = new ResourceTexture(identifier);
            this.registerTexture(identifier, texture);
        }
        texture.bindTexture();
    }

    public boolean registerTextureUpdateable(Identifier identifier, TickableTexture tickableTexture) {
        if (this.registerTexture(identifier, tickableTexture)) {
            this.tickListeners.add(tickableTexture);
            return true;
        }
        return false;
    }

    public boolean registerTexture(Identifier identifier, Texture texture) {
        boolean bl = true;
        try {
            texture.load(this.resourceContainer);
        } catch (IOException iOException) {
            if (identifier != MISSING_IDENTIFIER) {
                LOGGER.warn("Failed to load texture: {}", (Object)identifier, (Object)iOException);
            }
            texture = MissingSprite.getMissingSpriteTexture();
            this.textures.put(identifier, texture);
            bl = false;
        } catch (Throwable throwable) {
            CrashReport crashReport = CrashReport.create(throwable, "Registering texture");
            CrashReportSection crashReportSection = crashReport.addElement("Resource location being registered");
            Texture texture2 = texture;
            crashReportSection.add("Resource location", identifier);
            crashReportSection.add("Texture object class", () -> texture2.getClass().getName());
            throw new CrashException(crashReport);
        }
        this.textures.put(identifier, texture);
        return bl;
    }

    @Nullable
    public Texture getTexture(Identifier identifier) {
        return this.textures.get(identifier);
    }

    public Identifier registerDynamicTexture(String string, NativeImageBackedTexture nativeImageBackedTexture) {
        Integer integer = this.dynamicIdCounters.get(string);
        if (integer == null) {
            integer = 1;
        } else {
            Integer n = integer;
            Integer n2 = integer = Integer.valueOf(integer + 1);
        }
        this.dynamicIdCounters.put(string, integer);
        Identifier identifier = new Identifier(String.format("dynamic/%s_%d", string, integer));
        this.registerTexture(identifier, nativeImageBackedTexture);
        return identifier;
    }

    public CompletableFuture<Void> loadTextureAsync(Identifier identifier, Executor executor) {
        if (!this.textures.containsKey(identifier)) {
            AsyncTexture asyncTexture = new AsyncTexture(this.resourceContainer, identifier, executor);
            this.textures.put(identifier, asyncTexture);
            return asyncTexture.getLoadCompleteFuture().thenRunAsync(() -> this.registerTexture(identifier, asyncTexture), TextureManager::method_22812);
        }
        return CompletableFuture.completedFuture(null);
    }

    private static void method_22812(Runnable runnable) {
        MinecraftClient.getInstance().execute(() -> RenderSystem.recordRenderCall(runnable::run));
    }

    @Override
    public void tick() {
        for (TextureTickListener textureTickListener : this.tickListeners) {
            textureTickListener.tick();
        }
    }

    public void destroyTexture(Identifier identifier) {
        Texture texture = this.getTexture(identifier);
        if (texture != null) {
            TextureUtil.releaseTextureId(texture.getGlId());
        }
    }

    @Override
    public CompletableFuture<Void> reload(ResourceReloadListener.Synchronizer synchronizer, ResourceManager resourceManager, Profiler profiler, Profiler profiler2, Executor executor, Executor executor2) {
        return ((CompletableFuture)CompletableFuture.allOf(TitleScreen.loadTexturesAsync(this, executor), this.loadTextureAsync(AbstractButtonWidget.WIDGETS_LOCATION, executor)).thenCompose(synchronizer::whenPrepared)).thenAcceptAsync(void_ -> {
            MissingSprite.getMissingSpriteTexture();
            Iterator<Map.Entry<Identifier, Texture>> iterator = this.textures.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Identifier, Texture> entry = iterator.next();
                Identifier identifier = entry.getKey();
                Texture texture = entry.getValue();
                if (texture == MissingSprite.getMissingSpriteTexture() && !identifier.equals(MissingSprite.getMissingSpriteId())) {
                    iterator.remove();
                    continue;
                }
                texture.registerTexture(this, resourceManager, identifier, executor2);
            }
        }, runnable -> RenderSystem.recordRenderCall(runnable::run));
    }
}

