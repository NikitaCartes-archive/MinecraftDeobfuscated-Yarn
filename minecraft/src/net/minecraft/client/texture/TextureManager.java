package net.minecraft.client.texture;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.TextureUtil;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
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
	private final Map<Identifier, Texture> textures = Maps.<Identifier, Texture>newHashMap();
	private final List<TextureTickListener> tickListeners = Lists.<TextureTickListener>newArrayList();
	private final Map<String, Integer> dynamicIdCounters = Maps.<String, Integer>newHashMap();
	private final ResourceManager resourceContainer;

	public TextureManager(ResourceManager resourceManager) {
		this.resourceContainer = resourceManager;
	}

	public void bindTexture(Identifier id) {
		Texture texture = (Texture)this.textures.get(id);
		if (texture == null) {
			texture = new ResourceTexture(id);
			this.registerTexture(id, texture);
		}

		texture.bindTexture();
	}

	public boolean registerTextureUpdateable(Identifier id, TickableTexture tickableTexture) {
		if (this.registerTexture(id, tickableTexture)) {
			this.tickListeners.add(tickableTexture);
			return true;
		} else {
			return false;
		}
	}

	public boolean registerTexture(Identifier id, Texture texture) {
		boolean bl = true;

		try {
			texture.load(this.resourceContainer);
		} catch (IOException var8) {
			if (id != MISSING_IDENTIFIER) {
				LOGGER.warn("Failed to load texture: {}", id, var8);
			}

			texture = MissingSprite.getMissingSpriteTexture();
			this.textures.put(id, texture);
			bl = false;
		} catch (Throwable var9) {
			CrashReport crashReport = CrashReport.create(var9, "Registering texture");
			CrashReportSection crashReportSection = crashReport.addElement("Resource location being registered");
			crashReportSection.add("Resource location", id);
			crashReportSection.add("Texture object class", (CrashCallable<String>)(() -> texture.getClass().getName()));
			throw new CrashException(crashReport);
		}

		this.textures.put(id, texture);
		return bl;
	}

	public Texture getTexture(Identifier identifier) {
		return (Texture)this.textures.get(identifier);
	}

	public Identifier registerDynamicTexture(String prefix, NativeImageBackedTexture texture) {
		Integer integer = (Integer)this.dynamicIdCounters.get(prefix);
		if (integer == null) {
			integer = 1;
		} else {
			integer = integer + 1;
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
			return asyncTexture.getLoadCompleteFuture().thenRunAsync(() -> this.registerTexture(id, asyncTexture), MinecraftClient.getInstance());
		} else {
			return CompletableFuture.completedFuture(null);
		}
	}

	@Override
	public void tick() {
		for (TextureTickListener textureTickListener : this.tickListeners) {
			textureTickListener.tick();
		}
	}

	public void destroyTexture(Identifier id) {
		Texture texture = this.getTexture(id);
		if (texture != null) {
			TextureUtil.releaseTextureId(texture.getGlId());
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
				Iterator<Entry<Identifier, Texture>> iterator = this.textures.entrySet().iterator();

				while (iterator.hasNext()) {
					Entry<Identifier, Texture> entry = (Entry<Identifier, Texture>)iterator.next();
					Identifier identifier = (Identifier)entry.getKey();
					Texture texture = (Texture)entry.getValue();
					if (texture == MissingSprite.getMissingSpriteTexture() && !identifier.equals(MissingSprite.getMissingSpriteId())) {
						iterator.remove();
					} else {
						texture.registerTexture(this, manager, identifier, applyExecutor);
					}
				}
			}, applyExecutor);
	}
}
