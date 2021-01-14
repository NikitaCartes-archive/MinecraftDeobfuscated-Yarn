package net.minecraft.client.texture;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resource.metadata.AnimationResourceMetadata;
import net.minecraft.client.util.PngFile;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.profiler.Profiler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class SpriteAtlasTexture extends AbstractTexture implements TextureTickListener {
	private static final Logger LOGGER = LogManager.getLogger();
	@Deprecated
	public static final Identifier BLOCK_ATLAS_TEXTURE = PlayerScreenHandler.BLOCK_ATLAS_TEXTURE;
	@Deprecated
	public static final Identifier PARTICLE_ATLAS_TEXTURE = new Identifier("textures/atlas/particles.png");
	private final List<Sprite> animatedSprites = Lists.<Sprite>newArrayList();
	private final Set<Identifier> spritesToLoad = Sets.<Identifier>newHashSet();
	private final Map<Identifier, Sprite> sprites = Maps.<Identifier, Sprite>newHashMap();
	private final Identifier id;
	private final int maxTextureSize;

	public SpriteAtlasTexture(Identifier identifier) {
		this.id = identifier;
		this.maxTextureSize = RenderSystem.maxSupportedTextureSize();
	}

	@Override
	public void load(ResourceManager manager) throws IOException {
	}

	public void upload(SpriteAtlasTexture.Data data) {
		this.spritesToLoad.clear();
		this.spritesToLoad.addAll(data.spriteIds);
		LOGGER.info("Created: {}x{}x{} {}-atlas", data.width, data.height, data.maxLevel, this.id);
		TextureUtil.allocate(this.getGlId(), data.maxLevel, data.width, data.height);
		this.clear();

		for (Sprite sprite : data.sprites) {
			this.sprites.put(sprite.getId(), sprite);

			try {
				sprite.upload();
			} catch (Throwable var7) {
				CrashReport crashReport = CrashReport.create(var7, "Stitching texture atlas");
				CrashReportSection crashReportSection = crashReport.addElement("Texture being stitched together");
				crashReportSection.add("Atlas path", this.id);
				crashReportSection.add("Sprite", sprite);
				throw new CrashException(crashReport);
			}

			if (sprite.isAnimated()) {
				this.animatedSprites.add(sprite);
			}
		}
	}

	public SpriteAtlasTexture.Data stitch(ResourceManager resourceManager, Stream<Identifier> idStream, Profiler profiler, int mipmapLevel) {
		profiler.push("preparing");
		Set<Identifier> set = (Set<Identifier>)idStream.peek(identifier -> {
			if (identifier == null) {
				throw new IllegalArgumentException("Location cannot be null!");
			}
		}).collect(Collectors.toSet());
		int i = this.maxTextureSize;
		TextureStitcher textureStitcher = new TextureStitcher(i, i, mipmapLevel);
		int j = Integer.MAX_VALUE;
		int k = 1 << mipmapLevel;
		profiler.swap("extracting_frames");

		for (Sprite.Info info : this.loadSprites(resourceManager, set)) {
			j = Math.min(j, Math.min(info.getWidth(), info.getHeight()));
			int l = Math.min(Integer.lowestOneBit(info.getWidth()), Integer.lowestOneBit(info.getHeight()));
			if (l < k) {
				LOGGER.warn(
					"Texture {} with size {}x{} limits mip level from {} to {}", info.getId(), info.getWidth(), info.getHeight(), MathHelper.log2(k), MathHelper.log2(l)
				);
				k = l;
			}

			textureStitcher.add(info);
		}

		int m = Math.min(j, k);
		int n = MathHelper.log2(m);
		int l;
		if (n < mipmapLevel) {
			LOGGER.warn("{}: dropping miplevel from {} to {}, because of minimum power of two: {}", this.id, mipmapLevel, n, m);
			l = n;
		} else {
			l = mipmapLevel;
		}

		profiler.swap("register");
		textureStitcher.add(MissingSprite.getMissingInfo());
		profiler.swap("stitching");

		try {
			textureStitcher.stitch();
		} catch (TextureStitcherCannotFitException var16) {
			CrashReport crashReport = CrashReport.create(var16, "Stitching");
			CrashReportSection crashReportSection = crashReport.addElement("Stitcher");
			crashReportSection.add(
				"Sprites",
				var16.getSprites().stream().map(info -> String.format("%s[%dx%d]", info.getId(), info.getWidth(), info.getHeight())).collect(Collectors.joining(","))
			);
			crashReportSection.add("Max Texture Size", i);
			throw new CrashException(crashReport);
		}

		profiler.swap("loading");
		List<Sprite> list = this.loadSprites(resourceManager, textureStitcher, l);
		profiler.pop();
		return new SpriteAtlasTexture.Data(set, textureStitcher.getWidth(), textureStitcher.getHeight(), l, list);
	}

	private Collection<Sprite.Info> loadSprites(ResourceManager resourceManager, Set<Identifier> ids) {
		List<CompletableFuture<?>> list = Lists.<CompletableFuture<?>>newArrayList();
		ConcurrentLinkedQueue<Sprite.Info> concurrentLinkedQueue = new ConcurrentLinkedQueue();

		for (Identifier identifier : ids) {
			if (!MissingSprite.getMissingSpriteId().equals(identifier)) {
				list.add(CompletableFuture.runAsync(() -> {
					Identifier identifier2 = this.getTexturePath(identifier);

					Sprite.Info info;
					try {
						Resource resource = resourceManager.getResource(identifier2);
						Throwable var7 = null;

						try {
							PngFile pngFile = new PngFile(resource.toString(), resource.getInputStream());
							AnimationResourceMetadata animationResourceMetadata = resource.getMetadata(AnimationResourceMetadata.READER);
							if (animationResourceMetadata == null) {
								animationResourceMetadata = AnimationResourceMetadata.EMPTY;
							}

							Pair<Integer, Integer> pair = animationResourceMetadata.method_24141(pngFile.width, pngFile.height);
							info = new Sprite.Info(identifier, pair.getFirst(), pair.getSecond(), animationResourceMetadata);
						} catch (Throwable var20) {
							var7 = var20;
							throw var20;
						} finally {
							if (resource != null) {
								if (var7 != null) {
									try {
										resource.close();
									} catch (Throwable var19) {
										var7.addSuppressed(var19);
									}
								} else {
									resource.close();
								}
							}
						}
					} catch (RuntimeException var22) {
						LOGGER.error("Unable to parse metadata from {} : {}", identifier2, var22);
						return;
					} catch (IOException var23) {
						LOGGER.error("Using missing texture, unable to load {} : {}", identifier2, var23);
						return;
					}

					concurrentLinkedQueue.add(info);
				}, Util.getMainWorkerExecutor()));
			}
		}

		CompletableFuture.allOf((CompletableFuture[])list.toArray(new CompletableFuture[0])).join();
		return concurrentLinkedQueue;
	}

	private List<Sprite> loadSprites(ResourceManager resourceManager, TextureStitcher textureStitcher, int maxLevel) {
		ConcurrentLinkedQueue<Sprite> concurrentLinkedQueue = new ConcurrentLinkedQueue();
		List<CompletableFuture<?>> list = Lists.<CompletableFuture<?>>newArrayList();
		textureStitcher.getStitchedSprites((info, atlasWidth, atlasHeight, x, y) -> {
			if (info == MissingSprite.getMissingInfo()) {
				MissingSprite missingSprite = MissingSprite.getMissingSprite(this, maxLevel, atlasWidth, atlasHeight, x, y);
				concurrentLinkedQueue.add(missingSprite);
			} else {
				list.add(CompletableFuture.runAsync(() -> {
					Sprite sprite = this.loadSprite(resourceManager, info, atlasWidth, atlasHeight, maxLevel, x, y);
					if (sprite != null) {
						concurrentLinkedQueue.add(sprite);
					}
				}, Util.getMainWorkerExecutor()));
			}
		});
		CompletableFuture.allOf((CompletableFuture[])list.toArray(new CompletableFuture[0])).join();
		return Lists.<Sprite>newArrayList(concurrentLinkedQueue);
	}

	@Nullable
	private Sprite loadSprite(ResourceManager container, Sprite.Info info, int atlasWidth, int atlasHeight, int maxLevel, int x, int y) {
		Identifier identifier = this.getTexturePath(info.getId());

		try {
			Resource resource = container.getResource(identifier);
			Throwable var10 = null;

			Sprite var12;
			try {
				NativeImage nativeImage = NativeImage.read(resource.getInputStream());
				var12 = new Sprite(this, info, maxLevel, atlasWidth, atlasHeight, x, y, nativeImage);
			} catch (Throwable var23) {
				var10 = var23;
				throw var23;
			} finally {
				if (resource != null) {
					if (var10 != null) {
						try {
							resource.close();
						} catch (Throwable var22) {
							var10.addSuppressed(var22);
						}
					} else {
						resource.close();
					}
				}
			}

			return var12;
		} catch (RuntimeException var25) {
			LOGGER.error("Unable to parse metadata from {}", identifier, var25);
			return null;
		} catch (IOException var26) {
			LOGGER.error("Using missing texture, unable to load {}", identifier, var26);
			return null;
		}
	}

	private Identifier getTexturePath(Identifier id) {
		return new Identifier(id.getNamespace(), String.format("textures/%s%s", id.getPath(), ".png"));
	}

	public void tickAnimatedSprites() {
		this.bindTexture();

		for (Sprite sprite : this.animatedSprites) {
			sprite.tickAnimation();
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
		for (Sprite sprite : this.sprites.values()) {
			sprite.close();
		}

		this.sprites.clear();
		this.animatedSprites.clear();
	}

	public Identifier getId() {
		return this.id;
	}

	public void applyTextureFilter(SpriteAtlasTexture.Data data) {
		this.setFilter(false, data.maxLevel > 0);
	}

	@Environment(EnvType.CLIENT)
	public static class Data {
		final Set<Identifier> spriteIds;
		final int width;
		final int height;
		final int maxLevel;
		final List<Sprite> sprites;

		public Data(Set<Identifier> spriteIds, int width, int height, int maxLevel, List<Sprite> sprites) {
			this.spriteIds = spriteIds;
			this.width = width;
			this.height = height;
			this.maxLevel = maxLevel;
			this.sprites = sprites;
		}
	}
}
