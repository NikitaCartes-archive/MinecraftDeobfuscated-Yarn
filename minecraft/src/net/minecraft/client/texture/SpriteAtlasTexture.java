package net.minecraft.client.texture;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.platform.TextureUtil;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1050;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.metadata.AnimationResourceMetadata;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportElement;
import net.minecraft.util.crash.ICrashCallable;
import net.minecraft.util.math.MathHelper;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class SpriteAtlasTexture extends AbstractTexture implements TickableTexture {
	private static final Logger LOGGER = LogManager.getLogger();
	public static final Identifier BLOCK_ATLAS_TEX = new Identifier("textures/atlas/blocks.png");
	private final List<Sprite> animatedSprites = Lists.<Sprite>newArrayList();
	private final Set<Identifier> spritesToLoad = Sets.<Identifier>newHashSet();
	private final Map<Identifier, Sprite> sprites = Maps.<Identifier, Sprite>newHashMap();
	private final String pathPrefix;
	private int mipLevel;
	private final Sprite missingSprite = MissingSprite.getMissingSprite();

	public SpriteAtlasTexture(String string) {
		this.pathPrefix = string;
	}

	@Override
	public void load(ResourceManager resourceManager) throws IOException {
	}

	public void build(ResourceManager resourceManager, Iterable<Identifier> iterable) {
		this.spritesToLoad.clear();
		iterable.forEach(identifier -> this.addSpriteToLoad(resourceManager, identifier));
		this.reload(resourceManager);
	}

	public void reload(ResourceManager resourceManager) {
		int i = MinecraftClient.getMaxTextureSize();
		TextureStitcher textureStitcher = new TextureStitcher(i, i, 0, this.mipLevel);
		this.clear();
		int j = Integer.MAX_VALUE;
		int k = 1 << this.mipLevel;

		for (Identifier identifier : this.spritesToLoad) {
			if (!this.missingSprite.getId().equals(identifier)) {
				Identifier identifier2 = this.getTexturePath(identifier);

				Sprite sprite;
				try {
					Resource resource = resourceManager.getResource(identifier2);
					Throwable crashReport = null;

					try {
						class_1050 lv = new class_1050(resource.toString(), resource.getInputStream());
						AnimationResourceMetadata animationResourceMetadata = resource.getMetadata(AnimationResourceMetadata.READER);
						sprite = new Sprite(identifier, lv, animationResourceMetadata);
					} catch (Throwable var27) {
						crashReport = var27;
						throw var27;
					} finally {
						if (resource != null) {
							if (crashReport != null) {
								try {
									resource.close();
								} catch (Throwable var26) {
									crashReport.addSuppressed(var26);
								}
							} else {
								resource.close();
							}
						}
					}
				} catch (RuntimeException var29) {
					LOGGER.error("Unable to parse metadata from {} : {}", identifier2, var29);
					continue;
				} catch (IOException var30) {
					LOGGER.error("Using missing texture, unable to load {} : {}", identifier2, var30);
					continue;
				}

				j = Math.min(j, Math.min(sprite.getWidth(), sprite.getHeight()));
				int l = Math.min(Integer.lowestOneBit(sprite.getWidth()), Integer.lowestOneBit(sprite.getHeight()));
				if (l < k) {
					LOGGER.warn(
						"Texture {} with size {}x{} limits mip level from {} to {}", identifier2, sprite.getWidth(), sprite.getHeight(), MathHelper.log2(k), MathHelper.log2(l)
					);
					k = l;
				}

				textureStitcher.add(sprite);
			}
		}

		int m = Math.min(j, k);
		int n = MathHelper.log2(m);
		if (n < this.mipLevel) {
			LOGGER.warn("{}: dropping miplevel from {} to {}, because of minimum power of two: {}", this.pathPrefix, this.mipLevel, n, m);
			this.mipLevel = n;
		}

		this.missingSprite.generateMipmaps(this.mipLevel);
		textureStitcher.add(this.missingSprite);

		try {
			textureStitcher.stitch();
		} catch (TextureStitcherCannotFitException var25) {
			throw var25;
		}

		LOGGER.info("Created: {}x{} {}-atlas", textureStitcher.getWidth(), textureStitcher.getHeight(), this.pathPrefix);
		TextureUtil.prepareImage(this.getGlId(), this.mipLevel, textureStitcher.getWidth(), textureStitcher.getHeight());

		for (Sprite spritex : textureStitcher.getStitchedSprites()) {
			if (spritex == this.missingSprite || this.loadSprite(resourceManager, spritex)) {
				this.sprites.put(spritex.getId(), spritex);

				try {
					spritex.method_4584();
				} catch (Throwable var24) {
					CrashReport crashReport = CrashReport.create(var24, "Stitching texture atlas");
					CrashReportElement crashReportElement = crashReport.addElement("Texture being stitched together");
					crashReportElement.add("Atlas path", this.pathPrefix);
					crashReportElement.add("Sprite", spritex);
					throw new CrashException(crashReport);
				}

				if (spritex.isAnimated()) {
					this.animatedSprites.add(spritex);
				}
			}
		}
	}

	private boolean loadSprite(ResourceManager resourceManager, Sprite sprite) {
		Identifier identifier = this.getTexturePath(sprite.getId());
		Resource resource = null;

		label45: {
			boolean crashReport;
			try {
				resource = resourceManager.getResource(identifier);
				sprite.load(resource, this.mipLevel + 1);
				break label45;
			} catch (RuntimeException var13) {
				LOGGER.error("Unable to parse metadata from {}", identifier, var13);
				return false;
			} catch (IOException var14) {
				LOGGER.error("Using missing texture, unable to load {}", identifier, var14);
				crashReport = false;
			} finally {
				IOUtils.closeQuietly(resource);
			}

			return crashReport;
		}

		try {
			sprite.generateMipmaps(this.mipLevel);
			return true;
		} catch (Throwable var12) {
			CrashReport crashReport = CrashReport.create(var12, "Applying mipmap");
			CrashReportElement crashReportElement = crashReport.addElement("Sprite being mipmapped");
			crashReportElement.add("Sprite name", (ICrashCallable<String>)(() -> sprite.getId().toString()));
			crashReportElement.add("Sprite size", (ICrashCallable<String>)(() -> sprite.getWidth() + " x " + sprite.getHeight()));
			crashReportElement.add("Sprite frames", (ICrashCallable<String>)(() -> sprite.method_4592() + " frames"));
			crashReportElement.add("Mipmap levels", this.mipLevel);
			throw new CrashException(crashReport);
		}
	}

	private Identifier getTexturePath(Identifier identifier) {
		return new Identifier(identifier.getNamespace(), String.format("%s/%s%s", this.pathPrefix, identifier.getPath(), ".png"));
	}

	public Sprite getSprite(String string) {
		return this.getSprite(new Identifier(string));
	}

	public void updateAnimatedSprites() {
		this.bindTexture();

		for (Sprite sprite : this.animatedSprites) {
			sprite.tick();
		}
	}

	public void addSpriteToLoad(ResourceManager resourceManager, Identifier identifier) {
		if (identifier == null) {
			throw new IllegalArgumentException("Location cannot be null!");
		} else {
			this.spritesToLoad.add(identifier);
		}
	}

	@Override
	public void tick() {
		this.updateAnimatedSprites();
	}

	public void setMipLevel(int i) {
		this.mipLevel = i;
	}

	public Sprite getSprite(Identifier identifier) {
		Sprite sprite = (Sprite)this.sprites.get(identifier);
		return sprite == null ? this.missingSprite : sprite;
	}

	public void clear() {
		for (Sprite sprite : this.sprites.values()) {
			sprite.destroy();
		}

		this.sprites.clear();
		this.animatedSprites.clear();
	}
}
