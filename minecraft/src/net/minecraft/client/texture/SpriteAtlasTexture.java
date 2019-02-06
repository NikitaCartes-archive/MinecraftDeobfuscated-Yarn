package net.minecraft.client.texture;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.platform.TextureUtil;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
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
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.crash.ICrashCallable;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.profiler.Profiler;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class SpriteAtlasTexture extends AbstractTexture implements TickableTexture {
	private static final Logger LOGGER = LogManager.getLogger();
	public static final Identifier BLOCK_ATLAS_TEX = new Identifier("textures/atlas/blocks.png");
	public static final Identifier field_17898 = new Identifier("textures/atlas/particles.png");
	private final List<Sprite> animatedSprites = Lists.<Sprite>newArrayList();
	private final Set<Identifier> spritesToLoad = Sets.<Identifier>newHashSet();
	private final Map<Identifier, Sprite> sprites = Maps.<Identifier, Sprite>newHashMap();
	private final String pathPrefix;
	private final int field_17899;
	private int mipLevel;
	private final Sprite missingSprite = MissingSprite.getMissingSprite();

	public SpriteAtlasTexture(String string) {
		this.pathPrefix = string;
		this.field_17899 = MinecraftClient.getMaxTextureSize();
	}

	@Override
	public void load(ResourceManager resourceManager) throws IOException {
	}

	public void method_18159(SpriteAtlasTexture.class_4007 arg) {
		this.spritesToLoad.clear();
		this.spritesToLoad.addAll(arg.field_17900);
		LOGGER.info("Created: {}x{} {}-atlas", arg.field_17901, arg.field_17902, this.pathPrefix);
		TextureUtil.prepareImage(this.getGlId(), this.mipLevel, arg.field_17901, arg.field_17902);
		this.clear();

		for (Sprite sprite : arg.field_17903) {
			this.sprites.put(sprite.getId(), sprite);

			try {
				sprite.method_4584();
			} catch (Throwable var7) {
				CrashReport crashReport = CrashReport.create(var7, "Stitching texture atlas");
				CrashReportSection crashReportSection = crashReport.addElement("Texture being stitched together");
				crashReportSection.add("Atlas path", this.pathPrefix);
				crashReportSection.add("Sprite", sprite);
				throw new CrashException(crashReport);
			}

			if (sprite.isAnimated()) {
				this.animatedSprites.add(sprite);
			}
		}
	}

	public SpriteAtlasTexture.class_4007 method_18163(ResourceManager resourceManager, Iterable<Identifier> iterable, Profiler profiler) {
		Set<Identifier> set = Sets.<Identifier>newHashSet();
		profiler.push("preparing");
		iterable.forEach(identifier -> {
			if (identifier == null) {
				throw new IllegalArgumentException("Location cannot be null!");
			} else {
				set.add(identifier);
			}
		});
		int i = this.field_17899;
		TextureStitcher textureStitcher = new TextureStitcher(i, i, 0, this.mipLevel);
		int j = Integer.MAX_VALUE;
		int k = 1 << this.mipLevel;
		profiler.swap("extracting_frames");

		for (Sprite sprite : this.method_18164(resourceManager, set)) {
			j = Math.min(j, Math.min(sprite.getWidth(), sprite.getHeight()));
			int l = Math.min(Integer.lowestOneBit(sprite.getWidth()), Integer.lowestOneBit(sprite.getHeight()));
			if (l < k) {
				LOGGER.warn(
					"Texture {} with size {}x{} limits mip level from {} to {}", sprite.getId(), sprite.getWidth(), sprite.getHeight(), MathHelper.log2(k), MathHelper.log2(l)
				);
				k = l;
			}

			textureStitcher.add(sprite);
		}

		int m = Math.min(j, k);
		int n = MathHelper.log2(m);
		if (n < this.mipLevel) {
			LOGGER.warn("{}: dropping miplevel from {} to {}, because of minimum power of two: {}", this.pathPrefix, this.mipLevel, n, m);
			this.mipLevel = n;
		}

		profiler.swap("mipmapping");
		this.missingSprite.generateMipmaps(this.mipLevel);
		profiler.swap("register");
		textureStitcher.add(this.missingSprite);
		profiler.swap("stitching");

		try {
			textureStitcher.stitch();
		} catch (TextureStitcherCannotFitException var12) {
			throw var12;
		}

		profiler.swap("loading");
		List<Sprite> list = this.method_18161(resourceManager, textureStitcher);
		profiler.pop();
		return new SpriteAtlasTexture.class_4007(set, textureStitcher.getWidth(), textureStitcher.getHeight(), list);
	}

	private Collection<Sprite> method_18164(ResourceManager resourceManager, Set<Identifier> set) {
		List<CompletableFuture<?>> list = new ArrayList();
		ConcurrentLinkedQueue<Sprite> concurrentLinkedQueue = new ConcurrentLinkedQueue();

		for (Identifier identifier : set) {
			if (!this.missingSprite.getId().equals(identifier)) {
				list.add(CompletableFuture.runAsync(() -> {
					Identifier identifier2 = this.getTexturePath(identifier);

					Sprite sprite;
					try {
						Resource resource = resourceManager.getResource(identifier2);
						Throwable var7 = null;

						try {
							class_1050 lv = new class_1050(resource.toString(), resource.getInputStream());
							AnimationResourceMetadata animationResourceMetadata = resource.getMetadata(AnimationResourceMetadata.READER);
							sprite = new Sprite(identifier, lv, animationResourceMetadata);
						} catch (Throwable var19) {
							var7 = var19;
							throw var19;
						} finally {
							if (resource != null) {
								if (var7 != null) {
									try {
										resource.close();
									} catch (Throwable var18) {
										var7.addSuppressed(var18);
									}
								} else {
									resource.close();
								}
							}
						}
					} catch (RuntimeException var21) {
						LOGGER.error("Unable to parse metadata from {} : {}", identifier2, var21);
						return;
					} catch (IOException var22) {
						LOGGER.error("Using missing texture, unable to load {} : {}", identifier2, var22);
						return;
					}

					concurrentLinkedQueue.add(sprite);
				}));
			}
		}

		CompletableFuture.allOf((CompletableFuture[])list.toArray(new CompletableFuture[0])).join();
		return concurrentLinkedQueue;
	}

	private List<Sprite> method_18161(ResourceManager resourceManager, TextureStitcher textureStitcher) {
		ConcurrentLinkedQueue<Sprite> concurrentLinkedQueue = new ConcurrentLinkedQueue();
		List<CompletableFuture<?>> list = new ArrayList();

		for (Sprite sprite : textureStitcher.getStitchedSprites()) {
			if (sprite == this.missingSprite) {
				concurrentLinkedQueue.add(sprite);
			} else {
				list.add(CompletableFuture.runAsync(() -> {
					if (this.loadSprite(resourceManager, sprite)) {
						concurrentLinkedQueue.add(sprite);
					}
				}));
			}
		}

		CompletableFuture.allOf((CompletableFuture[])list.toArray(new CompletableFuture[0])).join();
		return new ArrayList(concurrentLinkedQueue);
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
			CrashReportSection crashReportSection = crashReport.addElement("Sprite being mipmapped");
			crashReportSection.add("Sprite name", (ICrashCallable<String>)(() -> sprite.getId().toString()));
			crashReportSection.add("Sprite size", (ICrashCallable<String>)(() -> sprite.getWidth() + " x " + sprite.getHeight()));
			crashReportSection.add("Sprite frames", (ICrashCallable<String>)(() -> sprite.method_4592() + " frames"));
			crashReportSection.add("Mipmap levels", this.mipLevel);
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

	@Environment(EnvType.CLIENT)
	public static class class_4007 {
		final Set<Identifier> field_17900;
		final int field_17901;
		final int field_17902;
		final List<Sprite> field_17903;

		public class_4007(Set<Identifier> set, int i, int j, List<Sprite> list) {
			this.field_17900 = set;
			this.field_17901 = i;
			this.field_17902 = j;
			this.field_17903 = list;
		}
	}
}
