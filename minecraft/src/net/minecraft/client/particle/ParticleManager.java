package net.minecraft.client.particle;

import com.google.common.base.Charsets;
import com.google.common.collect.EvictingQueue;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.texture.MissingSprite;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloadListener;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.shape.VoxelShape;

@Environment(EnvType.CLIENT)
public class ParticleManager implements ResourceReloadListener {
	private static final List<ParticleTextureSheet> PARTICLE_TEXTURE_SHEETS = ImmutableList.of(
		ParticleTextureSheet.TERRAIN_SHEET,
		ParticleTextureSheet.PARTICLE_SHEET_OPAQUE,
		ParticleTextureSheet.PARTICLE_SHEET_LIT,
		ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT,
		ParticleTextureSheet.CUSTOM
	);
	protected ClientWorld world;
	private final Map<ParticleTextureSheet, Queue<Particle>> particles = Maps.<ParticleTextureSheet, Queue<Particle>>newIdentityHashMap();
	private final Queue<EmitterParticle> newEmitterParticles = Queues.<EmitterParticle>newArrayDeque();
	private final TextureManager textureManager;
	private final Random random = new Random();
	private final Int2ObjectMap<ParticleFactory<?>> factories = new Int2ObjectOpenHashMap<>();
	private final Queue<Particle> newParticles = Queues.<Particle>newArrayDeque();
	private final Map<Identifier, ParticleManager.SimpleSpriteProvider> spriteAwareFactories = Maps.<Identifier, ParticleManager.SimpleSpriteProvider>newHashMap();
	private final SpriteAtlasTexture particleAtlasTexture = new SpriteAtlasTexture(SpriteAtlasTexture.PARTICLE_ATLAS_TEX);

	public ParticleManager(ClientWorld world, TextureManager textureManager) {
		textureManager.registerTexture(this.particleAtlasTexture.getId(), this.particleAtlasTexture);
		this.world = world;
		this.textureManager = textureManager;
		this.registerDefaultFactories();
	}

	private void registerDefaultFactories() {
		this.registerFactory(ParticleTypes.field_11225, SpellParticle.EntityAmbientFactory::new);
		this.registerFactory(ParticleTypes.field_11231, EmotionParticle.AngryVillagerFactory::new);
		this.registerFactory(ParticleTypes.field_11235, new BarrierParticle.Factory());
		this.registerFactory(ParticleTypes.field_11217, new BlockDustParticle.Factory());
		this.registerFactory(ParticleTypes.field_11247, WaterBubbleParticle.Factory::new);
		this.registerFactory(ParticleTypes.field_11238, BubbleColumnUpParticle.Factory::new);
		this.registerFactory(ParticleTypes.field_11241, BubblePopParticle.Factory::new);
		this.registerFactory(ParticleTypes.field_17430, CampfireSmokeParticle.CosySmokeFactory::new);
		this.registerFactory(ParticleTypes.field_17431, CampfireSmokeParticle.SignalSmokeFactory::new);
		this.registerFactory(ParticleTypes.field_11204, CloudParticle.CloudFactory::new);
		this.registerFactory(ParticleTypes.field_17741, SuspendParticle.Factory::new);
		this.registerFactory(ParticleTypes.field_11205, DamageParticle.Factory::new);
		this.registerFactory(ParticleTypes.field_11243, CurrentDownParticle.Factory::new);
		this.registerFactory(ParticleTypes.field_11209, DamageParticle.DefaultFactory::new);
		this.registerFactory(ParticleTypes.field_11216, DragonBreathParticle.Factory::new);
		this.registerFactory(ParticleTypes.field_11222, SuspendParticle.DolphinFactory::new);
		this.registerFactory(ParticleTypes.field_11223, BlockLeakParticle.DrippingLavaFactory::new);
		this.registerFactory(ParticleTypes.field_18304, BlockLeakParticle.FallingLavaFactory::new);
		this.registerFactory(ParticleTypes.field_18305, BlockLeakParticle.LandingLavaFactory::new);
		this.registerFactory(ParticleTypes.field_11232, BlockLeakParticle.DrippingWaterFactory::new);
		this.registerFactory(ParticleTypes.field_18306, BlockLeakParticle.FallingWaterFactory::new);
		this.registerFactory(ParticleTypes.field_11212, RedDustParticle.Factory::new);
		this.registerFactory(ParticleTypes.field_11245, SpellParticle.DefaultFactory::new);
		this.registerFactory(ParticleTypes.field_11250, new ElderGuardianAppearanceParticle.Factory());
		this.registerFactory(ParticleTypes.field_11208, DamageParticle.EnchantedHitFactory::new);
		this.registerFactory(ParticleTypes.field_11215, EnchantGlyphParticle.EnchantFactory::new);
		this.registerFactory(ParticleTypes.field_11207, EndRodParticle.Factory::new);
		this.registerFactory(ParticleTypes.field_11226, SpellParticle.EntityFactory::new);
		this.registerFactory(ParticleTypes.field_11221, new ExplosionEmitterParticle.Factory());
		this.registerFactory(ParticleTypes.field_11236, ExplosionLargeParticle.Factory::new);
		this.registerFactory(ParticleTypes.field_11206, BlockFallingDustParticle.Factory::new);
		this.registerFactory(ParticleTypes.field_11248, FireworksSparkParticle.ExplosionFactory::new);
		this.registerFactory(ParticleTypes.field_11244, FishingParticle.Factory::new);
		this.registerFactory(ParticleTypes.field_11240, FlameParticle.Factory::new);
		this.registerFactory(ParticleTypes.field_23114, SoulParticle.Factory::new);
		this.registerFactory(ParticleTypes.field_22246, FlameParticle.Factory::new);
		this.registerFactory(ParticleTypes.field_17909, FireworksSparkParticle.FlashFactory::new);
		this.registerFactory(ParticleTypes.field_11211, SuspendParticle.HappyVillagerFactory::new);
		this.registerFactory(ParticleTypes.field_11201, EmotionParticle.HeartFactory::new);
		this.registerFactory(ParticleTypes.field_11213, SpellParticle.InstantFactory::new);
		this.registerFactory(ParticleTypes.field_11218, new CrackParticle.ItemFactory());
		this.registerFactory(ParticleTypes.field_11246, new CrackParticle.SlimeballFactory());
		this.registerFactory(ParticleTypes.field_11230, new CrackParticle.SnowballFactory());
		this.registerFactory(ParticleTypes.field_11237, LargeFireSmokeParticle.Factory::new);
		this.registerFactory(ParticleTypes.field_11239, LavaEmberParticle.Factory::new);
		this.registerFactory(ParticleTypes.field_11219, SuspendParticle.MyceliumFactory::new);
		this.registerFactory(ParticleTypes.field_11229, EnchantGlyphParticle.NautilusFactory::new);
		this.registerFactory(ParticleTypes.field_11224, NoteParticle.Factory::new);
		this.registerFactory(ParticleTypes.field_11203, ExplosionSmokeParticle.Factory::new);
		this.registerFactory(ParticleTypes.field_11214, PortalParticle.Factory::new);
		this.registerFactory(ParticleTypes.field_11242, RainSplashParticle.Factory::new);
		this.registerFactory(ParticleTypes.field_11251, FireSmokeParticle.Factory::new);
		this.registerFactory(ParticleTypes.field_11234, CloudParticle.SneezeFactory::new);
		this.registerFactory(ParticleTypes.field_11228, SpitParticle.Factory::new);
		this.registerFactory(ParticleTypes.field_11227, SweepAttackParticle.Factory::new);
		this.registerFactory(ParticleTypes.field_11220, TotemParticle.Factory::new);
		this.registerFactory(ParticleTypes.field_11233, SquidInkParticle.Factory::new);
		this.registerFactory(ParticleTypes.field_11210, WaterSuspendParticle.UnderwaterFactory::new);
		this.registerFactory(ParticleTypes.field_11202, WaterSplashParticle.SplashFactory::new);
		this.registerFactory(ParticleTypes.field_11249, SpellParticle.WitchFactory::new);
		this.registerFactory(ParticleTypes.field_20534, BlockLeakParticle.DrippingHoneyFactory::new);
		this.registerFactory(ParticleTypes.field_20535, BlockLeakParticle.FallingHoneyFactory::new);
		this.registerFactory(ParticleTypes.field_20536, BlockLeakParticle.LandingHoneyFactory::new);
		this.registerFactory(ParticleTypes.field_20537, BlockLeakParticle.FallingNectarFactory::new);
		this.registerFactory(ParticleTypes.field_22247, AshParticle.Factory::new);
		this.registerFactory(ParticleTypes.field_22248, WaterSuspendParticle.CrimsonSporeFactory::new);
		this.registerFactory(ParticleTypes.field_22249, WaterSuspendParticle.WarpedSporeFactory::new);
		this.registerFactory(ParticleTypes.field_22446, BlockLeakParticle.DrippingObsidianTearFactory::new);
		this.registerFactory(ParticleTypes.field_22447, BlockLeakParticle.FallingObsidianTearFactory::new);
		this.registerFactory(ParticleTypes.field_22448, BlockLeakParticle.LandingObsidianTearFactory::new);
		this.registerFactory(ParticleTypes.field_23190, ReversePortalParticle.Factory::new);
		this.registerFactory(ParticleTypes.field_23956, WhiteAshParticle.Factory::new);
	}

	private <T extends ParticleEffect> void registerFactory(ParticleType<T> type, ParticleFactory<T> factory) {
		this.factories.put(Registry.PARTICLE_TYPE.getRawId(type), factory);
	}

	private <T extends ParticleEffect> void registerFactory(ParticleType<T> particleType, ParticleManager.SpriteAwareFactory<T> spriteAwareFactory) {
		ParticleManager.SimpleSpriteProvider simpleSpriteProvider = new ParticleManager.SimpleSpriteProvider();
		this.spriteAwareFactories.put(Registry.PARTICLE_TYPE.getId(particleType), simpleSpriteProvider);
		this.factories.put(Registry.PARTICLE_TYPE.getRawId(particleType), spriteAwareFactory.create(simpleSpriteProvider));
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
		Map<Identifier, List<Identifier>> map = Maps.<Identifier, List<Identifier>>newConcurrentMap();
		CompletableFuture<?>[] completableFutures = (CompletableFuture<?>[])Registry.PARTICLE_TYPE
			.getIds()
			.stream()
			.map(identifier -> CompletableFuture.runAsync(() -> this.loadTextureList(manager, identifier, map), prepareExecutor))
			.toArray(CompletableFuture[]::new);
		return CompletableFuture.allOf(completableFutures)
			.thenApplyAsync(void_ -> {
				prepareProfiler.startTick();
				prepareProfiler.push("stitching");
				SpriteAtlasTexture.Data data = this.particleAtlasTexture.stitch(manager, map.values().stream().flatMap(Collection::stream), prepareProfiler, 0);
				prepareProfiler.pop();
				prepareProfiler.endTick();
				return data;
			}, prepareExecutor)
			.thenCompose(synchronizer::whenPrepared)
			.thenAcceptAsync(
				data -> {
					this.particles.clear();
					applyProfiler.startTick();
					applyProfiler.push("upload");
					this.particleAtlasTexture.upload(data);
					applyProfiler.swap("bindSpriteSets");
					Sprite sprite = this.particleAtlasTexture.getSprite(MissingSprite.getMissingSpriteId());
					map.forEach(
						(identifier, list) -> {
							ImmutableList<Sprite> immutableList = list.isEmpty()
								? ImmutableList.of(sprite)
								: (ImmutableList)list.stream().map(this.particleAtlasTexture::getSprite).collect(ImmutableList.toImmutableList());
							((ParticleManager.SimpleSpriteProvider)this.spriteAwareFactories.get(identifier)).setSprites(immutableList);
						}
					);
					applyProfiler.pop();
					applyProfiler.endTick();
				},
				applyExecutor
			);
	}

	public void clearAtlas() {
		this.particleAtlasTexture.clear();
	}

	private void loadTextureList(ResourceManager resourceManager, Identifier id, Map<Identifier, List<Identifier>> result) {
		Identifier identifier = new Identifier(id.getNamespace(), "particles/" + id.getPath() + ".json");

		try {
			Resource resource = resourceManager.getResource(identifier);
			Throwable var6 = null;

			try {
				Reader reader = new InputStreamReader(resource.getInputStream(), Charsets.UTF_8);
				Throwable var8 = null;

				try {
					ParticleTextureData particleTextureData = ParticleTextureData.load(JsonHelper.deserialize(reader));
					List<Identifier> list = particleTextureData.getTextureList();
					boolean bl = this.spriteAwareFactories.containsKey(id);
					if (list == null) {
						if (bl) {
							throw new IllegalStateException("Missing texture list for particle " + id);
						}
					} else {
						if (!bl) {
							throw new IllegalStateException("Redundant texture list for particle " + id);
						}

						result.put(
							id, list.stream().map(identifierx -> new Identifier(identifierx.getNamespace(), "particle/" + identifierx.getPath())).collect(Collectors.toList())
						);
					}
				} catch (Throwable var35) {
					var8 = var35;
					throw var35;
				} finally {
					if (reader != null) {
						if (var8 != null) {
							try {
								reader.close();
							} catch (Throwable var34) {
								var8.addSuppressed(var34);
							}
						} else {
							reader.close();
						}
					}
				}
			} catch (Throwable var37) {
				var6 = var37;
				throw var37;
			} finally {
				if (resource != null) {
					if (var6 != null) {
						try {
							resource.close();
						} catch (Throwable var33) {
							var6.addSuppressed(var33);
						}
					} else {
						resource.close();
					}
				}
			}
		} catch (IOException var39) {
			throw new IllegalStateException("Failed to load description for particle " + id, var39);
		}
	}

	public void addEmitter(Entity entity, ParticleEffect parameters) {
		this.newEmitterParticles.add(new EmitterParticle(this.world, entity, parameters));
	}

	public void addEmitter(Entity entity, ParticleEffect parameters, int maxAge) {
		this.newEmitterParticles.add(new EmitterParticle(this.world, entity, parameters, maxAge));
	}

	@Nullable
	public Particle addParticle(ParticleEffect parameters, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
		Particle particle = this.createParticle(parameters, x, y, z, velocityX, velocityY, velocityZ);
		if (particle != null) {
			this.addParticle(particle);
			return particle;
		} else {
			return null;
		}
	}

	@Nullable
	private <T extends ParticleEffect> Particle createParticle(T parameters, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
		ParticleFactory<T> particleFactory = (ParticleFactory<T>)this.factories.get(Registry.PARTICLE_TYPE.getRawId(parameters.getType()));
		return particleFactory == null ? null : particleFactory.createParticle(parameters, this.world, x, y, z, velocityX, velocityY, velocityZ);
	}

	public void addParticle(Particle particle) {
		this.newParticles.add(particle);
	}

	public void tick() {
		this.particles.forEach((particleTextureSheet, queue) -> {
			this.world.getProfiler().push(particleTextureSheet.toString());
			this.tickParticles(queue);
			this.world.getProfiler().pop();
		});
		if (!this.newEmitterParticles.isEmpty()) {
			List<EmitterParticle> list = Lists.<EmitterParticle>newArrayList();

			for (EmitterParticle emitterParticle : this.newEmitterParticles) {
				emitterParticle.tick();
				if (!emitterParticle.isAlive()) {
					list.add(emitterParticle);
				}
			}

			this.newEmitterParticles.removeAll(list);
		}

		Particle particle;
		if (!this.newParticles.isEmpty()) {
			while ((particle = (Particle)this.newParticles.poll()) != null) {
				((Queue)this.particles.computeIfAbsent(particle.getType(), particleTextureSheet -> EvictingQueue.create(16384))).add(particle);
			}
		}
	}

	private void tickParticles(Collection<Particle> collection) {
		if (!collection.isEmpty()) {
			Iterator<Particle> iterator = collection.iterator();

			while (iterator.hasNext()) {
				Particle particle = (Particle)iterator.next();
				this.tickParticle(particle);
				if (!particle.isAlive()) {
					iterator.remove();
				}
			}
		}
	}

	private void tickParticle(Particle particle) {
		try {
			particle.tick();
		} catch (Throwable var5) {
			CrashReport crashReport = CrashReport.create(var5, "Ticking Particle");
			CrashReportSection crashReportSection = crashReport.addElement("Particle being ticked");
			crashReportSection.add("Particle", particle::toString);
			crashReportSection.add("Particle Type", particle.getType()::toString);
			throw new CrashException(crashReport);
		}
	}

	public void renderParticles(
		MatrixStack matrixStack, VertexConsumerProvider.Immediate immediate, LightmapTextureManager lightmapTextureManager, Camera camera, float f
	) {
		lightmapTextureManager.enable();
		RenderSystem.enableAlphaTest();
		RenderSystem.defaultAlphaFunc();
		RenderSystem.enableDepthTest();
		RenderSystem.enableFog();
		RenderSystem.pushMatrix();
		RenderSystem.multMatrix(matrixStack.peek().getModel());

		for (ParticleTextureSheet particleTextureSheet : PARTICLE_TEXTURE_SHEETS) {
			Iterable<Particle> iterable = (Iterable<Particle>)this.particles.get(particleTextureSheet);
			if (iterable != null) {
				RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
				Tessellator tessellator = Tessellator.getInstance();
				BufferBuilder bufferBuilder = tessellator.getBuffer();
				particleTextureSheet.begin(bufferBuilder, this.textureManager);

				for (Particle particle : iterable) {
					try {
						particle.buildGeometry(bufferBuilder, camera, f);
					} catch (Throwable var16) {
						CrashReport crashReport = CrashReport.create(var16, "Rendering Particle");
						CrashReportSection crashReportSection = crashReport.addElement("Particle being rendered");
						crashReportSection.add("Particle", particle::toString);
						crashReportSection.add("Particle Type", particleTextureSheet::toString);
						throw new CrashException(crashReport);
					}
				}

				particleTextureSheet.draw(tessellator);
			}
		}

		RenderSystem.popMatrix();
		RenderSystem.depthMask(true);
		RenderSystem.depthFunc(515);
		RenderSystem.disableBlend();
		RenderSystem.defaultAlphaFunc();
		lightmapTextureManager.disable();
		RenderSystem.disableFog();
	}

	public void setWorld(@Nullable ClientWorld world) {
		this.world = world;
		this.particles.clear();
		this.newEmitterParticles.clear();
	}

	public void addBlockBreakParticles(BlockPos pos, BlockState state) {
		if (!state.isAir()) {
			VoxelShape voxelShape = state.getOutlineShape(this.world, pos);
			double d = 0.25;
			voxelShape.forEachBox(
				(dx, e, f, g, h, i) -> {
					double j = Math.min(1.0, g - dx);
					double k = Math.min(1.0, h - e);
					double l = Math.min(1.0, i - f);
					int m = Math.max(2, MathHelper.ceil(j / 0.25));
					int n = Math.max(2, MathHelper.ceil(k / 0.25));
					int o = Math.max(2, MathHelper.ceil(l / 0.25));

					for (int p = 0; p < m; p++) {
						for (int q = 0; q < n; q++) {
							for (int r = 0; r < o; r++) {
								double s = ((double)p + 0.5) / (double)m;
								double t = ((double)q + 0.5) / (double)n;
								double u = ((double)r + 0.5) / (double)o;
								double v = s * j + dx;
								double w = t * k + e;
								double x = u * l + f;
								this.addParticle(
									new BlockDustParticle(this.world, (double)pos.getX() + v, (double)pos.getY() + w, (double)pos.getZ() + x, s - 0.5, t - 0.5, u - 0.5, state)
										.setBlockPos(pos)
								);
							}
						}
					}
				}
			);
		}
	}

	public void addBlockBreakingParticles(BlockPos pos, Direction direction) {
		BlockState blockState = this.world.getBlockState(pos);
		if (blockState.getRenderType() != BlockRenderType.field_11455) {
			int i = pos.getX();
			int j = pos.getY();
			int k = pos.getZ();
			float f = 0.1F;
			Box box = blockState.getOutlineShape(this.world, pos).getBoundingBox();
			double d = (double)i + this.random.nextDouble() * (box.maxX - box.minX - 0.2F) + 0.1F + box.minX;
			double e = (double)j + this.random.nextDouble() * (box.maxY - box.minY - 0.2F) + 0.1F + box.minY;
			double g = (double)k + this.random.nextDouble() * (box.maxZ - box.minZ - 0.2F) + 0.1F + box.minZ;
			if (direction == Direction.field_11033) {
				e = (double)j + box.minY - 0.1F;
			}

			if (direction == Direction.field_11036) {
				e = (double)j + box.maxY + 0.1F;
			}

			if (direction == Direction.field_11043) {
				g = (double)k + box.minZ - 0.1F;
			}

			if (direction == Direction.field_11035) {
				g = (double)k + box.maxZ + 0.1F;
			}

			if (direction == Direction.field_11039) {
				d = (double)i + box.minX - 0.1F;
			}

			if (direction == Direction.field_11034) {
				d = (double)i + box.maxX + 0.1F;
			}

			this.addParticle(new BlockDustParticle(this.world, d, e, g, 0.0, 0.0, 0.0, blockState).setBlockPos(pos).move(0.2F).scale(0.6F));
		}
	}

	public String getDebugString() {
		return String.valueOf(this.particles.values().stream().mapToInt(Collection::size).sum());
	}

	@Environment(EnvType.CLIENT)
	class SimpleSpriteProvider implements SpriteProvider {
		private List<Sprite> sprites;

		private SimpleSpriteProvider() {
		}

		@Override
		public Sprite getSprite(int i, int j) {
			return (Sprite)this.sprites.get(i * (this.sprites.size() - 1) / j);
		}

		@Override
		public Sprite getSprite(Random random) {
			return (Sprite)this.sprites.get(random.nextInt(this.sprites.size()));
		}

		public void setSprites(List<Sprite> sprites) {
			this.sprites = ImmutableList.copyOf(sprites);
		}
	}

	@FunctionalInterface
	@Environment(EnvType.CLIENT)
	interface SpriteAwareFactory<T extends ParticleEffect> {
		ParticleFactory<T> create(SpriteProvider spriteProvider);
	}
}
