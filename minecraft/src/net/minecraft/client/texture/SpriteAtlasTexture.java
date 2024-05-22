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
import javax.annotation.Nullable;
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
public class SpriteAtlasTexture extends AbstractTexture implements DynamicTexture, TextureTickListener {
	private static final Logger LOGGER = LogUtils.getLogger();
	@Deprecated
	public static final Identifier BLOCK_ATLAS_TEXTURE = PlayerScreenHandler.BLOCK_ATLAS_TEXTURE;
	@Deprecated
	public static final Identifier PARTICLE_ATLAS_TEXTURE = Identifier.ofVanilla("textures/atlas/particles.png");
	private List<SpriteContents> spritesToLoad = List.of();
	private List<Sprite.TickableAnimation> animatedSprites = List.of();
	private Map<Identifier, Sprite> sprites = Map.of();
	@Nullable
	private Sprite missingSprite;
	private final Identifier id;
	private final int maxTextureSize;
	private int width;
	private int height;
	private int mipLevel;

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
		this.width = stitchResult.width();
		this.height = stitchResult.height();
		this.mipLevel = stitchResult.mipLevel();
		this.clear();
		this.sprites = Map.copyOf(stitchResult.regions());
		this.missingSprite = (Sprite)this.sprites.get(MissingSprite.getMissingSpriteId());
		if (this.missingSprite == null) {
			throw new IllegalStateException("Atlas '" + this.id + "' (" + this.sprites.size() + " sprites) has no missing texture sprite");
		} else {
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
	}

	@Override
	public void save(Identifier id, Path path) throws IOException {
		String string = id.toUnderscoreSeparatedString();
		TextureUtil.writeAsPNG(path, string, this.getGlId(), this.mipLevel, this.width, this.height);
		dumpAtlasInfos(path, string, this.sprites);
	}

	private static void dumpAtlasInfos(Path path, String id, Map<Identifier, Sprite> sprites) {
		Path path2 = path.resolve(id + ".txt");

		try {
			Writer writer = Files.newBufferedWriter(path2);

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
			} catch (Throwable var9) {
				if (writer != null) {
					try {
						writer.close();
					} catch (Throwable var8) {
						var9.addSuppressed(var8);
					}
				}

				throw var9;
			}

			if (writer != null) {
				writer.close();
			}
		} catch (IOException var10) {
			LOGGER.warn("Failed to write file {}", path2, var10);
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
		Sprite sprite = (Sprite)this.sprites.getOrDefault(id, this.missingSprite);
		if (sprite == null) {
			throw new IllegalStateException("Tried to lookup sprite, but atlas is not initialized");
		} else {
			return sprite;
		}
	}

	public void clear() {
		this.spritesToLoad.forEach(SpriteContents::close);
		this.animatedSprites.forEach(Sprite.TickableAnimation::close);
		this.spritesToLoad = List.of();
		this.animatedSprites = List.of();
		this.sprites = Map.of();
		this.missingSprite = null;
	}

	public Identifier getId() {
		return this.id;
	}

	public int getMaxTextureSize() {
		return this.maxTextureSize;
	}

	int getWidth() {
		return this.width;
	}

	int getHeight() {
		return this.height;
	}

	public void applyTextureFilter(SpriteLoader.StitchResult data) {
		this.setFilter(false, data.mipLevel() > 0);
	}
}
