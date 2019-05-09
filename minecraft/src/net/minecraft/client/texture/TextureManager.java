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
import net.minecraft.client.gui.MainMenuScreen;
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

	public void bindTexture(Identifier identifier) {
		Texture texture = (Texture)this.textures.get(identifier);
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
		} else {
			return false;
		}
	}

	public boolean registerTexture(Identifier identifier, Texture texture) {
		boolean bl = true;

		try {
			texture.load(this.resourceContainer);
		} catch (IOException var8) {
			if (identifier != MISSING_IDENTIFIER) {
				LOGGER.warn("Failed to load texture: {}", identifier, var8);
			}

			texture = MissingSprite.getMissingSpriteTexture();
			this.textures.put(identifier, texture);
			bl = false;
		} catch (Throwable var9) {
			CrashReport crashReport = CrashReport.create(var9, "Registering texture");
			CrashReportSection crashReportSection = crashReport.addElement("Resource location being registered");
			crashReportSection.add("Resource location", identifier);
			crashReportSection.add("Texture object class", (CrashCallable<String>)(() -> texture.getClass().getName()));
			throw new CrashException(crashReport);
		}

		this.textures.put(identifier, texture);
		return bl;
	}

	public Texture getTexture(Identifier identifier) {
		return (Texture)this.textures.get(identifier);
	}

	public Identifier registerDynamicTexture(String string, NativeImageBackedTexture nativeImageBackedTexture) {
		Integer integer = (Integer)this.dynamicIdCounters.get(string);
		if (integer == null) {
			integer = 1;
		} else {
			integer = integer + 1;
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
			return asyncTexture.getLoadCompleteFuture().thenRunAsync(() -> this.registerTexture(identifier, asyncTexture), MinecraftClient.getInstance());
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

	public void destroyTexture(Identifier identifier) {
		Texture texture = this.getTexture(identifier);
		if (texture != null) {
			TextureUtil.releaseTextureId(texture.getGlId());
		}
	}

	@Override
	public CompletableFuture<Void> reload(
		ResourceReloadListener.Synchronizer synchronizer,
		ResourceManager resourceManager,
		Profiler profiler,
		Profiler profiler2,
		Executor executor,
		Executor executor2
	) {
		return CompletableFuture.allOf(MainMenuScreen.loadTexturesAsync(this, executor), this.loadTextureAsync(AbstractButtonWidget.WIDGETS_LOCATION, executor))
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
						texture.registerTexture(this, resourceManager, identifier, executor2);
					}
				}
			}, executor2);
	}
}
