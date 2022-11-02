package net.minecraft.client.texture;

import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resource.ResourceManager;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.Identifier;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class SpriteAtlasTexture extends AbstractTexture implements TextureTickListener {
	private static final Logger LOGGER = LogUtils.getLogger();
	@Deprecated
	public static final Identifier BLOCK_ATLAS_TEXTURE = PlayerScreenHandler.BLOCK_ATLAS_TEXTURE;
	@Deprecated
	public static final Identifier PARTICLE_ATLAS_TEXTURE = new Identifier("textures/atlas/particles.png");
	private List<SpriteContents> spritesToLoad = List.of();
	private List<Sprite.TickableAnimation> animatedSprites = List.of();
	private Map<Identifier, Sprite> sprites = Map.of();
	private final Identifier id;
	private final int maxTextureSize;

	public SpriteAtlasTexture(Identifier id) {
		this.id = id;
		this.maxTextureSize = RenderSystem.maxSupportedTextureSize();
	}

	@Override
	public void load(ResourceManager manager) {
	}

	public void upload(SpriteLoader.StitchResult stitchResult) {
		LOGGER.info("Created: {}x{}x{} {}-atlas", stitchResult.width(), stitchResult.height(), stitchResult.mipLevel(), this.id);
		TextureUtil.prepareImage(this.getGlId(), stitchResult.mipLevel(), stitchResult.width(), stitchResult.height());
		this.clear();
		this.sprites = Map.copyOf(stitchResult.regions());
		List<SpriteContents> list = new ArrayList();
		List<Sprite.TickableAnimation> list2 = new ArrayList();

		for (Sprite sprite : stitchResult.regions().values()) {
			list.add(sprite.getContents());

			try {
				sprite.upload();
			} catch (Throwable var9) {
				CrashReport crashReport = CrashReport.create(var9, "Stitching texture atlas");
				CrashReportSection crashReportSection = crashReport.addElement("Texture being stitched together");
				crashReportSection.add("Atlas path", this.id);
				crashReportSection.add("Sprite", sprite);
				throw new CrashException(crashReport);
			}

			Sprite.TickableAnimation tickableAnimation = sprite.createAnimation();
			if (tickableAnimation != null) {
				list2.add(tickableAnimation);
			}
		}

		this.spritesToLoad = List.copyOf(list);
		this.animatedSprites = List.copyOf(list2);
	}

	private void dumpAtlasTextureAndInfo(int scales, int width, int height) {
		String string = this.id.toUnderscoreSeparatedString();
		TextureUtil.writeAsPNG(string, this.getGlId(), scales, width, height);
		dumpAtlasInfos(string, this.sprites);
	}

	private static void dumpAtlasInfos(String id, Map<Identifier, Sprite> sprites) {
		Path path = Path.of(id + ".txt");

		try {
			Writer writer = Files.newBufferedWriter(path);

			try {
				for (Entry<Identifier, Sprite> entry : sprites.entrySet().stream().sorted(Entry.comparingByKey()).toList()) {
					Sprite sprite = (Sprite)entry.getValue();
					writer.write(
						String.format(
							Locale.ROOT,
							"%s\tx=%d\ty=%d\tw=%d\th=%d%n",
							entry.getKey(),
							sprite.getX(),
							sprite.getY(),
							sprite.getContents().getWidth(),
							sprite.getContents().getHeight()
						)
					);
				}
			} catch (Throwable var8) {
				if (writer != null) {
					try {
						writer.close();
					} catch (Throwable var7) {
						var8.addSuppressed(var7);
					}
				}

				throw var8;
			}

			if (writer != null) {
				writer.close();
			}
		} catch (IOException var9) {
			LOGGER.warn("Failed to write file {}", path, var9);
		}
	}

	public void tickAnimatedSprites() {
		this.bindTexture();

		for (Sprite.TickableAnimation tickableAnimation : this.animatedSprites) {
			tickableAnimation.tick();
		}
	}

	@Override
	public void tick() {
		if (!RenderSystem.isOnRenderThread()) {
			RenderSystem.recordRenderCall(this::tickAnimatedSprites);
		} else {
			this.tickAnimatedSprites();
		}
	}

	public Sprite getSprite(Identifier id) {
		Sprite sprite = (Sprite)this.sprites.get(id);
		return sprite == null ? (Sprite)this.sprites.get(MissingSprite.getMissingSpriteId()) : sprite;
	}

	public void clear() {
		this.spritesToLoad.forEach(SpriteContents::close);
		this.animatedSprites.forEach(Sprite.TickableAnimation::close);
		this.spritesToLoad = List.of();
		this.animatedSprites = List.of();
		this.sprites = Map.of();
	}

	public Identifier getId() {
		return this.id;
	}

	public int getMaxTextureSize() {
		return this.maxTextureSize;
	}

	public void applyTextureFilter(SpriteLoader.StitchResult data) {
		this.setFilter(false, data.mipLevel() > 0);
	}
}
