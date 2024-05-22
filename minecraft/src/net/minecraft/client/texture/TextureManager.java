package net.minecraft.client.texture;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.realms.gui.screen.BuyRealmsScreen;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloader;
import net.minecraft.util.Identifier;
import net.minecraft.util.crash.CrashCallable;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.profiler.Profiler;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class TextureManager implements ResourceReloader, TextureTickListener, AutoCloseable {
	private static final Logger LOGGER = LogUtils.getLogger();
	public static final Identifier MISSING_IDENTIFIER = Identifier.ofVanilla("");
	private final Map<Identifier, AbstractTexture> textures = Maps.<Identifier, AbstractTexture>newHashMap();
	private final Set<TextureTickListener> tickListeners = Sets.<TextureTickListener>newHashSet();
	private final Map<String, Integer> dynamicIdCounters = Maps.<String, Integer>newHashMap();
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
		AbstractTexture abstractTexture = (AbstractTexture)this.textures.get(id);
		if (abstractTexture == null) {
			abstractTexture = new ResourceTexture(id);
			this.registerTexture(id, abstractTexture);
		}

		abstractTexture.bindTexture();
	}

	public void registerTexture(Identifier id, AbstractTexture texture) {
		texture = this.loadTexture(id, texture);
		AbstractTexture abstractTexture = (AbstractTexture)this.textures.put(id, texture);
		if (abstractTexture != texture) {
			if (abstractTexture != null && abstractTexture != MissingSprite.getMissingSpriteTexture()) {
				this.closeTexture(id, abstractTexture);
			}

			if (texture instanceof TextureTickListener) {
				this.tickListeners.add((TextureTickListener)texture);
			}
		}
	}

	private void closeTexture(Identifier id, AbstractTexture texture) {
		if (texture != MissingSprite.getMissingSpriteTexture()) {
			this.tickListeners.remove(texture);

			try {
				texture.close();
			} catch (Exception var4) {
				LOGGER.warn("Failed to close texture {}", id, var4);
			}
		}

		texture.clearGlId();
	}

	private AbstractTexture loadTexture(Identifier id, AbstractTexture texture) {
		try {
			texture.load(this.resourceContainer);
			return texture;
		} catch (IOException var6) {
			if (id != MISSING_IDENTIFIER) {
				LOGGER.warn("Failed to load texture: {}", id, var6);
			}

			return MissingSprite.getMissingSpriteTexture();
		} catch (Throwable var7) {
			CrashReport crashReport = CrashReport.create(var7, "Registering texture");
			CrashReportSection crashReportSection = crashReport.addElement("Resource location being registered");
			crashReportSection.add("Resource location", id);
			crashReportSection.add("Texture object class", (CrashCallable<String>)(() -> texture.getClass().getName()));
			throw new CrashException(crashReport);
		}
	}

	public AbstractTexture getTexture(Identifier id) {
		AbstractTexture abstractTexture = (AbstractTexture)this.textures.get(id);
		if (abstractTexture == null) {
			abstractTexture = new ResourceTexture(id);
			this.registerTexture(id, abstractTexture);
		}

		return abstractTexture;
	}

	public AbstractTexture getOrDefault(Identifier id, AbstractTexture fallback) {
		return (AbstractTexture)this.textures.getOrDefault(id, fallback);
	}

	public Identifier registerDynamicTexture(String prefix, NativeImageBackedTexture texture) {
		Integer integer = (Integer)this.dynamicIdCounters.get(prefix);
		if (integer == null) {
			integer = 1;
		} else {
			integer = integer + 1;
		}

		this.dynamicIdCounters.put(prefix, integer);
		Identifier identifier = Identifier.ofVanilla(String.format(Locale.ROOT, "dynamic/%s_%d", prefix, integer));
		this.registerTexture(identifier, texture);
		return identifier;
	}

	public CompletableFuture<Void> loadTextureAsync(Identifier id, Executor executor) {
		if (!this.textures.containsKey(id)) {
			AsyncTexture asyncTexture = new AsyncTexture(this.resourceContainer, id, executor);
			this.textures.put(id, asyncTexture);
			return asyncTexture.getLoadCompleteFuture().thenRunAsync(() -> this.registerTexture(id, asyncTexture), TextureManager::runOnRenderThread);
		} else {
			return CompletableFuture.completedFuture(null);
		}
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
		AbstractTexture abstractTexture = (AbstractTexture)this.textures.remove(id);
		if (abstractTexture != null) {
			this.closeTexture(id, abstractTexture);
		}
	}

	public void close() {
		this.textures.forEach(this::closeTexture);
		this.textures.clear();
		this.tickListeners.clear();
		this.dynamicIdCounters.clear();
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
		CompletableFuture<Void> completableFuture = new CompletableFuture();
		TitleScreen.loadTexturesAsync(this, prepareExecutor).thenCompose(synchronizer::whenPrepared).thenAcceptAsync(void_ -> {
			MissingSprite.getMissingSpriteTexture();
			BuyRealmsScreen.refreshImages(this.resourceContainer);
			Iterator<Entry<Identifier, AbstractTexture>> iterator = this.textures.entrySet().iterator();

			while (iterator.hasNext()) {
				Entry<Identifier, AbstractTexture> entry = (Entry<Identifier, AbstractTexture>)iterator.next();
				Identifier identifier = (Identifier)entry.getKey();
				AbstractTexture abstractTexture = (AbstractTexture)entry.getValue();
				if (abstractTexture == MissingSprite.getMissingSpriteTexture() && !identifier.equals(MissingSprite.getMissingSpriteId())) {
					iterator.remove();
				} else {
					abstractTexture.registerTexture(this, manager, identifier, applyExecutor);
				}
			}

			MinecraftClient.getInstance().send(() -> completableFuture.complete(null));
		}, runnable -> RenderSystem.recordRenderCall(runnable::run));
		return completableFuture;
	}

	public void dumpDynamicTextures(Path path) {
		if (!RenderSystem.isOnRenderThread()) {
			RenderSystem.recordRenderCall(() -> this.dumpDynamicTexturesInternal(path));
		} else {
			this.dumpDynamicTexturesInternal(path);
		}
	}

	private void dumpDynamicTexturesInternal(Path path) {
		try {
			Files.createDirectories(path);
		} catch (IOException var3) {
			LOGGER.error("Failed to create directory {}", path, var3);
			return;
		}

		this.textures.forEach((id, texture) -> {
			if (texture instanceof DynamicTexture dynamicTexture) {
				try {
					dynamicTexture.save(id, path);
				} catch (IOException var5) {
					LOGGER.error("Failed to dump texture {}", id, var5);
				}
			}
		});
	}
}
