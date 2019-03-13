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
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloadListener;
import net.minecraft.util.Identifier;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.profiler.Profiler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class TextureManager implements TextureTickListener, ResourceReloadListener {
	private static final Logger LOGGER = LogManager.getLogger();
	public static final Identifier field_5285 = new Identifier("");
	private final Map<Identifier, Texture> textures = Maps.<Identifier, Texture>newHashMap();
	private final List<TextureTickListener> tickListeners = Lists.<TextureTickListener>newArrayList();
	private final Map<String, Integer> dynamicIdCounters = Maps.<String, Integer>newHashMap();
	private final ResourceManager field_5287;

	public TextureManager(ResourceManager resourceManager) {
		this.field_5287 = resourceManager;
	}

	public void method_4618(Identifier identifier) {
		Texture texture = (Texture)this.textures.get(identifier);
		if (texture == null) {
			texture = new ResourceTexture(identifier);
			this.method_4616(identifier, texture);
		}

		texture.bindTexture();
	}

	public boolean method_4620(Identifier identifier, TickableTexture tickableTexture) {
		if (this.method_4616(identifier, tickableTexture)) {
			this.tickListeners.add(tickableTexture);
			return true;
		} else {
			return false;
		}
	}

	public boolean method_4616(Identifier identifier, Texture texture) {
		boolean bl = true;

		try {
			texture.method_4625(this.field_5287);
		} catch (IOException var8) {
			if (identifier != field_5285) {
				LOGGER.warn("Failed to load texture: {}", identifier, var8);
			}

			texture = MissingSprite.getMissingSpriteTexture();
			this.textures.put(identifier, texture);
			bl = false;
		} catch (Throwable var9) {
			CrashReport crashReport = CrashReport.create(var9, "Registering texture");
			CrashReportSection crashReportSection = crashReport.method_562("Resource location being registered");
			crashReportSection.add("Resource location", identifier);
			crashReportSection.method_577("Texture object class", () -> texture.getClass().getName());
			throw new CrashException(crashReport);
		}

		this.textures.put(identifier, texture);
		return bl;
	}

	public Texture method_4619(Identifier identifier) {
		return (Texture)this.textures.get(identifier);
	}

	public Identifier method_4617(String string, NativeImageBackedTexture nativeImageBackedTexture) {
		Integer integer = (Integer)this.dynamicIdCounters.get(string);
		if (integer == null) {
			integer = 1;
		} else {
			integer = integer + 1;
		}

		this.dynamicIdCounters.put(string, integer);
		Identifier identifier = new Identifier(String.format("dynamic/%s_%d", string, integer));
		this.method_4616(identifier, nativeImageBackedTexture);
		return identifier;
	}

	public CompletableFuture<Void> method_18168(Identifier identifier, Executor executor) {
		if (!this.textures.containsKey(identifier)) {
			AsyncTexture asyncTexture = new AsyncTexture(this.field_5287, identifier, executor);
			this.textures.put(identifier, asyncTexture);
			return asyncTexture.method_18148().thenRunAsync(() -> this.method_4616(identifier, asyncTexture), MinecraftClient.getInstance());
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

	public void method_4615(Identifier identifier) {
		Texture texture = this.method_4619(identifier);
		if (texture != null) {
			TextureUtil.releaseTextureId(texture.getGlId());
		}
	}

	@Override
	public CompletableFuture<Void> method_18222(
		ResourceReloadListener.Helper helper, ResourceManager resourceManager, Profiler profiler, Profiler profiler2, Executor executor, Executor executor2
	) {
		return CompletableFuture.allOf(MainMenuScreen.method_18105(this, executor), this.method_18168(ButtonWidget.field_2072, executor))
			.thenCompose(helper::waitForAll)
			.thenAcceptAsync(void_ -> {
				MissingSprite.getMissingSpriteTexture();
				Iterator<Entry<Identifier, Texture>> iterator = this.textures.entrySet().iterator();

				while (iterator.hasNext()) {
					Entry<Identifier, Texture> entry = (Entry<Identifier, Texture>)iterator.next();
					Identifier identifier = (Identifier)entry.getKey();
					Texture texture = (Texture)entry.getValue();
					if (texture == MissingSprite.getMissingSpriteTexture() && !identifier.equals(MissingSprite.method_4539())) {
						iterator.remove();
					} else {
						texture.method_18169(this, resourceManager, identifier, executor2);
					}
				}
			}, executor2);
	}
}
