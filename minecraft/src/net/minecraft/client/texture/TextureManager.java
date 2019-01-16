package net.minecraft.client.texture;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.TextureUtil;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloadListener;
import net.minecraft.util.Identifier;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.crash.ICrashCallable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class TextureManager implements TextureTickListener, ResourceReloadListener {
	private static final Logger LOGGER = LogManager.getLogger();
	public static final Identifier field_5285 = new Identifier("");
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
			if (identifier != field_5285) {
				LOGGER.warn("Failed to load texture: {}", identifier, var8);
			}

			texture = MissingSprite.method_4540();
			this.textures.put(identifier, texture);
			bl = false;
		} catch (Throwable var9) {
			CrashReport crashReport = CrashReport.create(var9, "Registering texture");
			CrashReportSection crashReportSection = crashReport.addElement("Resource location being registered");
			crashReportSection.add("Resource location", identifier);
			crashReportSection.add("Texture object class", (ICrashCallable<String>)(() -> texture.getClass().getName()));
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
	public void onResourceReload(ResourceManager resourceManager) {
		MissingSprite.method_4540();
		Iterator<Entry<Identifier, Texture>> iterator = this.textures.entrySet().iterator();

		while (iterator.hasNext()) {
			Entry<Identifier, Texture> entry = (Entry<Identifier, Texture>)iterator.next();
			Identifier identifier = (Identifier)entry.getKey();
			Texture texture = (Texture)entry.getValue();
			if (texture == MissingSprite.method_4540() && !identifier.equals(MissingSprite.getMissingTextureId())) {
				iterator.remove();
			} else {
				this.registerTexture((Identifier)entry.getKey(), texture);
			}
		}
	}
}
