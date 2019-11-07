package net.minecraft.client.texture;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.realmsclient.RealmsMainScreen;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloadListener;
import net.minecraft.util.Identifier;
import net.minecraft.util.crash.CrashCallable;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.profiler.Profiler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class TextureManager implements TextureTickListener, ResourceReloadListener {
	private static final Logger LOGGER = LogManager.getLogger();
	public static final Identifier MISSING_IDENTIFIER = new Identifier("");
	private final Map<Identifier, AbstractTexture> textures = Maps.<Identifier, AbstractTexture>newHashMap();
	private final Set<TextureTickListener> tickListeners = Sets.<TextureTickListener>newHashSet();
	private final Map<String, Integer> dynamicIdCounters = Maps.<String, Integer>newHashMap();
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
		AbstractTexture abstractTexture = (AbstractTexture)this.textures.get(identifier);
		if (abstractTexture == null) {
			abstractTexture = new ResourceTexture(identifier);
			this.registerTexture(identifier, abstractTexture);
		}

		abstractTexture.bindTexture();
	}

	public boolean registerTexture(Identifier id, AbstractTexture abstractTexture) {
		boolean bl = true;

		try {
			abstractTexture.load(this.resourceContainer);
		} catch (IOException var8) {
			if (id != MISSING_IDENTIFIER) {
				LOGGER.warn("Failed to load texture: {}", id, var8);
			}

			abstractTexture = MissingSprite.getMissingSpriteTexture();
			this.textures.put(id, abstractTexture);
			bl = false;
		} catch (Throwable var9) {
			CrashReport crashReport = CrashReport.create(var9, "Registering texture");
			CrashReportSection crashReportSection = crashReport.addElement("Resource location being registered");
			crashReportSection.add("Resource location", id);
			crashReportSection.add("Texture object class", (CrashCallable<String>)(() -> abstractTexture.getClass().getName()));
			throw new CrashException(crashReport);
		}

		this.textures.put(id, abstractTexture);
		if (bl && abstractTexture instanceof TextureTickListener) {
			this.tickListeners.add((TextureTickListener)abstractTexture);
		}

		return bl;
	}

	@Nullable
	public AbstractTexture getTexture(Identifier identifier) {
		return (AbstractTexture)this.textures.get(identifier);
	}

	public Identifier registerDynamicTexture(String prefix, NativeImageBackedTexture nativeImageBackedTexture) {
		Integer integer = (Integer)this.dynamicIdCounters.get(prefix);
		if (integer == null) {
			integer = 1;
		} else {
			integer = integer + 1;
		}

		this.dynamicIdCounters.put(prefix, integer);
		Identifier identifier = new Identifier(String.format("dynamic/%s_%d", prefix, integer));
		this.registerTexture(identifier, nativeImageBackedTexture);
		return identifier;
	}

	public CompletableFuture<Void> loadTextureAsync(Identifier identifier, Executor executor) {
		if (!this.textures.containsKey(identifier)) {
			AsyncTexture asyncTexture = new AsyncTexture(this.resourceContainer, identifier, executor);
			this.textures.put(identifier, asyncTexture);
			return asyncTexture.getLoadCompleteFuture().thenRunAsync(() -> this.registerTexture(identifier, asyncTexture), TextureManager::runOnRenderThread);
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

	public void destroyTexture(Identifier identifier) {
		AbstractTexture abstractTexture = this.getTexture(identifier);
		if (abstractTexture != null) {
			TextureUtil.releaseTextureId(abstractTexture.getGlId());
		}
	}

	@Override
	public CompletableFuture<Void> reload(
		ResourceReloadListener.Synchronizer synchronizer,
		ResourceManager manager,
		Profiler prepareProfiler,
		Profiler applyProfiler,
		Executor prepareExecutor,
		Executor applyExecutor
	) {
		return CompletableFuture.allOf(
				TitleScreen.loadTexturesAsync(this, prepareExecutor), this.loadTextureAsync(AbstractButtonWidget.WIDGETS_LOCATION, prepareExecutor)
			)
			.thenCompose(synchronizer::whenPrepared)
			.thenAcceptAsync(void_ -> {
				MissingSprite.getMissingSpriteTexture();
				RealmsMainScreen.method_23765(this.resourceContainer);
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
			}, runnable -> RenderSystem.recordRenderCall(runnable::run));
	}
}
