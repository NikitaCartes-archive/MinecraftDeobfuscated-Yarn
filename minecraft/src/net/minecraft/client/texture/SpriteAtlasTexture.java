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
import net.minecraft.container.PlayerContainer;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
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
	public static final Identifier BLOCK_ATLAS_TEX = PlayerContainer.field_21668;
	@Deprecated
	public static final Identifier PARTICLE_ATLAS_TEX = new Identifier("textures/atlas/particles.png");
	private final List<Sprite> animatedSprites = Lists.<Sprite>newArrayList();
	private final Set<Identifier> spritesToLoad = Sets.<Identifier>newHashSet();
	private final Map<Identifier, Sprite> sprites = Maps.<Identifier, Sprite>newHashMap();
	private final Identifier field_21749;
	private final int maxTextureSize;
	private int mipLevel;

	public SpriteAtlasTexture(Identifier identifier) {
		this.field_21749 = identifier;
		this.maxTextureSize = RenderSystem.maxSupportedTextureSize();
	}

	@Override
	public void load(ResourceManager manager) throws IOException {
	}

	public void upload(SpriteAtlasTexture.Data data) {
		this.spritesToLoad.clear();
		this.spritesToLoad.addAll(data.spriteIds);
		LOGGER.info("Created: {}x{} {}-atlas", data.width, data.height, this.field_21749);
		TextureUtil.prepareImage(this.getGlId(), this.mipLevel, data.width, data.height);
		this.clear();

		for (Sprite sprite : data.sprites) {
			this.sprites.put(sprite.getId(), sprite);

			try {
				sprite.upload();
			} catch (Throwable var7) {
				CrashReport crashReport = CrashReport.create(var7, "Stitching texture atlas");
				CrashReportSection crashReportSection = crashReport.addElement("Texture being stitched together");
				crashReportSection.add("Atlas path", this.field_21749);
				crashReportSection.add("Sprite", sprite);
				throw new CrashException(crashReport);
			}

			if (sprite.isAnimated()) {
				this.animatedSprites.add(sprite);
			}
		}
	}

	public SpriteAtlasTexture.Data stitch(ResourceManager resourceManager, Stream<Identifier> stream, Profiler profiler, int i) {
		profiler.push("preparing");
		Set<Identifier> set = (Set<Identifier>)stream.peek(identifier -> {
			if (identifier == null) {
				throw new IllegalArgumentException("Location cannot be null!");
			}
		}).collect(Collectors.toSet());
		int j = this.maxTextureSize;
		TextureStitcher textureStitcher = new TextureStitcher(j, j, i);
		int k = Integer.MAX_VALUE;
		int l = 1 << i;
		profiler.swap("extracting_frames");

		for (Sprite.class_4727 lv : this.loadSprites(resourceManager, set)) {
			k = Math.min(k, Math.min(lv.method_24123(), lv.method_24125()));
			int m = Math.min(Integer.lowestOneBit(lv.method_24123()), Integer.lowestOneBit(lv.method_24125()));
			if (m < l) {
				LOGGER.warn(
					"Texture {} with size {}x{} limits mip level from {} to {}",
					lv.method_24121(),
					lv.method_24123(),
					lv.method_24125(),
					MathHelper.log2(l),
					MathHelper.log2(m)
				);
				l = m;
			}

			textureStitcher.add(lv);
		}

		int n = Math.min(k, l);
		int o = MathHelper.log2(n);
		this.mipLevel = i;
		if (o < this.mipLevel) {
			LOGGER.warn("{}: dropping miplevel from {} to {}, because of minimum power of two: {}", this.field_21749, this.mipLevel, o, n);
			this.mipLevel = o;
		}

		profiler.swap("register");
		textureStitcher.add(MissingSprite.method_24104());
		profiler.swap("stitching");

		try {
			textureStitcher.stitch();
		} catch (TextureStitcherCannotFitException var15) {
			CrashReport crashReport = CrashReport.create(var15, "Stitching");
			CrashReportSection crashReportSection = crashReport.addElement("Stitcher");
			crashReportSection.add(
				"Sprites",
				var15.getSprites()
					.stream()
					.map(arg -> String.format("%s[%dx%d]", arg.method_24121(), arg.method_24123(), arg.method_24125()))
					.collect(Collectors.joining(","))
			);
			crashReportSection.add("Max Texture Size", j);
			throw new CrashException(crashReport);
		}

		profiler.swap("loading");
		List<Sprite> list = this.loadSprites(resourceManager, textureStitcher);
		profiler.pop();
		return new SpriteAtlasTexture.Data(set, textureStitcher.getWidth(), textureStitcher.getHeight(), list);
	}

	private Collection<Sprite.class_4727> loadSprites(ResourceManager resourceManager, Set<Identifier> set) {
		List<CompletableFuture<?>> list = Lists.<CompletableFuture<?>>newArrayList();
		ConcurrentLinkedQueue<Sprite.class_4727> concurrentLinkedQueue = new ConcurrentLinkedQueue();

		for (Identifier identifier : set) {
			if (!MissingSprite.getMissingSpriteId().equals(identifier)) {
				list.add(CompletableFuture.runAsync(() -> {
					Identifier identifier2 = this.getTexturePath(identifier);

					Sprite.class_4727 lv;
					try {
						Resource resource = resourceManager.getResource(identifier2);
						Throwable var7 = null;

						try {
							PngFile pngFile = new PngFile(resource.toString(), resource.getInputStream());
							AnimationResourceMetadata animationResourceMetadata = resource.getMetadata(AnimationResourceMetadata.READER);
							if (animationResourceMetadata == null) {
								animationResourceMetadata = AnimationResourceMetadata.field_21768;
							}

							Pair<Integer, Integer> pair = animationResourceMetadata.method_24141(pngFile.width, pngFile.height);
							lv = new Sprite.class_4727(identifier, pair.getFirst(), pair.getSecond(), animationResourceMetadata);
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

					concurrentLinkedQueue.add(lv);
				}, Util.getServerWorkerExecutor()));
			}
		}

		CompletableFuture.allOf((CompletableFuture[])list.toArray(new CompletableFuture[0])).join();
		return concurrentLinkedQueue;
	}

	private List<Sprite> loadSprites(ResourceManager resourceManager, TextureStitcher stitcher) {
		ConcurrentLinkedQueue<Sprite> concurrentLinkedQueue = new ConcurrentLinkedQueue();
		List<CompletableFuture<?>> list = Lists.<CompletableFuture<?>>newArrayList();
		stitcher.getStitchedSprites((arg, i, j, k, l) -> {
			if (arg == MissingSprite.method_24104()) {
				MissingSprite missingSprite = MissingSprite.getMissingSprite(this, this.mipLevel, i, j, k, l);
				concurrentLinkedQueue.add(missingSprite);
			} else {
				list.add(CompletableFuture.runAsync(() -> {
					Sprite sprite = this.loadSprite(resourceManager, arg, i, j, k, l);
					if (sprite != null) {
						concurrentLinkedQueue.add(sprite);
					}
				}, Util.getServerWorkerExecutor()));
			}
		});
		CompletableFuture.allOf((CompletableFuture[])list.toArray(new CompletableFuture[0])).join();
		return Lists.<Sprite>newArrayList(concurrentLinkedQueue);
	}

	@Nullable
	private Sprite loadSprite(ResourceManager container, Sprite.class_4727 arg, int i, int j, int k, int l) {
		Identifier identifier = this.getTexturePath(arg.method_24121());

		try {
			Resource resource = container.getResource(identifier);
			Throwable var9 = null;

			Sprite var11;
			try {
				NativeImage nativeImage = NativeImage.read(resource.getInputStream());
				var11 = new Sprite(this, arg, this.mipLevel, i, j, k, l, nativeImage);
			} catch (Throwable var22) {
				var9 = var22;
				throw var22;
			} finally {
				if (resource != null) {
					if (var9 != null) {
						try {
							resource.close();
						} catch (Throwable var21) {
							var9.addSuppressed(var21);
						}
					} else {
						resource.close();
					}
				}
			}

			return var11;
		} catch (RuntimeException var24) {
			LOGGER.error("Unable to parse metadata from {}", identifier, var24);
			return null;
		} catch (IOException var25) {
			LOGGER.error("Using missing texture, unable to load {}", identifier, var25);
			return null;
		}
	}

	private Identifier getTexturePath(Identifier identifier) {
		return new Identifier(identifier.getNamespace(), String.format("textures/%s%s", identifier.getPath(), ".png"));
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

	public void setMipLevel(int mipLevel) {
		this.mipLevel = mipLevel;
	}

	public Sprite getSprite(Identifier identifier) {
		Sprite sprite = (Sprite)this.sprites.get(identifier);
		return sprite == null ? (Sprite)this.sprites.get(MissingSprite.getMissingSpriteId()) : sprite;
	}

	public void clear() {
		for (Sprite sprite : this.sprites.values()) {
			sprite.destroy();
		}

		this.sprites.clear();
		this.animatedSprites.clear();
	}

	public Identifier method_24106() {
		return this.field_21749;
	}

	@Environment(EnvType.CLIENT)
	public static class Data {
		final Set<Identifier> spriteIds;
		final int width;
		final int height;
		final List<Sprite> sprites;

		public Data(Set<Identifier> spriteIds, int width, int height, List<Sprite> sprites) {
			this.spriteIds = spriteIds;
			this.width = width;
			this.height = height;
			this.sprites = sprites;
		}
	}
}
