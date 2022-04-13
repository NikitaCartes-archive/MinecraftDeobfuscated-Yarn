package net.minecraft.client.particle;

import com.google.common.collect.EvictingQueue;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.io.IOException;
import java.io.Reader;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
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
import net.minecraft.client.render.GameRenderer;
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
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloader;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.AbstractRandom;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.shape.VoxelShape;

@Environment(EnvType.CLIENT)
public class ParticleManager implements ResourceReloader {
	private static final int MAX_PARTICLE_COUNT = 16384;
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
	private final AbstractRandom random = AbstractRandom.createAtomic();
	private final Int2ObjectMap<ParticleFactory<?>> factories = new Int2ObjectOpenHashMap<>();
	private final Queue<Particle> newParticles = Queues.<Particle>newArrayDeque();
	private final Map<Identifier, ParticleManager.SimpleSpriteProvider> spriteAwareFactories = Maps.<Identifier, ParticleManager.SimpleSpriteProvider>newHashMap();
	private final SpriteAtlasTexture particleAtlasTexture;
	private final Object2IntOpenHashMap<ParticleGroup> groupCounts = new Object2IntOpenHashMap<>();

	public ParticleManager(ClientWorld world, TextureManager textureManager) {
		this.particleAtlasTexture = new SpriteAtlasTexture(SpriteAtlasTexture.PARTICLE_ATLAS_TEXTURE);
		textureManager.registerTexture(this.particleAtlasTexture.getId(), this.particleAtlasTexture);
		this.world = world;
		this.textureManager = textureManager;
		this.registerDefaultFactories();
	}

	private void registerDefaultFactories() {
		this.registerFactory(ParticleTypes.AMBIENT_ENTITY_EFFECT, SpellParticle.EntityAmbientFactory::new);
		this.registerFactory(ParticleTypes.ANGRY_VILLAGER, EmotionParticle.AngryVillagerFactory::new);
		this.registerFactory(ParticleTypes.BLOCK_MARKER, new BlockMarkerParticle.Factory());
		this.registerFactory(ParticleTypes.BLOCK, new BlockDustParticle.Factory());
		this.registerFactory(ParticleTypes.BUBBLE, WaterBubbleParticle.Factory::new);
		this.registerFactory(ParticleTypes.BUBBLE_COLUMN_UP, BubbleColumnUpParticle.Factory::new);
		this.registerFactory(ParticleTypes.BUBBLE_POP, BubblePopParticle.Factory::new);
		this.registerFactory(ParticleTypes.CAMPFIRE_COSY_SMOKE, CampfireSmokeParticle.CosySmokeFactory::new);
		this.registerFactory(ParticleTypes.CAMPFIRE_SIGNAL_SMOKE, CampfireSmokeParticle.SignalSmokeFactory::new);
		this.registerFactory(ParticleTypes.CLOUD, CloudParticle.CloudFactory::new);
		this.registerFactory(ParticleTypes.COMPOSTER, SuspendParticle.Factory::new);
		this.registerFactory(ParticleTypes.CRIT, DamageParticle.Factory::new);
		this.registerFactory(ParticleTypes.CURRENT_DOWN, CurrentDownParticle.Factory::new);
		this.registerFactory(ParticleTypes.DAMAGE_INDICATOR, DamageParticle.DefaultFactory::new);
		this.registerFactory(ParticleTypes.DRAGON_BREATH, DragonBreathParticle.Factory::new);
		this.registerFactory(ParticleTypes.DOLPHIN, SuspendParticle.DolphinFactory::new);
		this.registerFactory(ParticleTypes.DRIPPING_LAVA, BlockLeakParticle.DrippingLavaFactory::new);
		this.registerFactory(ParticleTypes.FALLING_LAVA, BlockLeakParticle.FallingLavaFactory::new);
		this.registerFactory(ParticleTypes.LANDING_LAVA, BlockLeakParticle.LandingLavaFactory::new);
		this.registerFactory(ParticleTypes.DRIPPING_WATER, BlockLeakParticle.DrippingWaterFactory::new);
		this.registerFactory(ParticleTypes.FALLING_WATER, BlockLeakParticle.FallingWaterFactory::new);
		this.registerFactory(ParticleTypes.DUST, RedDustParticle.Factory::new);
		this.registerFactory(ParticleTypes.DUST_COLOR_TRANSITION, DustColorTransitionParticle.Factory::new);
		this.registerFactory(ParticleTypes.EFFECT, SpellParticle.DefaultFactory::new);
		this.registerFactory(ParticleTypes.ELDER_GUARDIAN, new ElderGuardianAppearanceParticle.Factory());
		this.registerFactory(ParticleTypes.ENCHANTED_HIT, DamageParticle.EnchantedHitFactory::new);
		this.registerFactory(ParticleTypes.ENCHANT, EnchantGlyphParticle.EnchantFactory::new);
		this.registerFactory(ParticleTypes.END_ROD, EndRodParticle.Factory::new);
		this.registerFactory(ParticleTypes.ENTITY_EFFECT, SpellParticle.EntityFactory::new);
		this.registerFactory(ParticleTypes.EXPLOSION_EMITTER, new ExplosionEmitterParticle.Factory());
		this.registerFactory(ParticleTypes.EXPLOSION, ExplosionLargeParticle.Factory::new);
		this.registerFactory(ParticleTypes.SONIC_BOOM, ExplosionLargeParticle.Factory::new);
		this.registerFactory(ParticleTypes.FALLING_DUST, BlockFallingDustParticle.Factory::new);
		this.registerFactory(ParticleTypes.FIREWORK, FireworksSparkParticle.ExplosionFactory::new);
		this.registerFactory(ParticleTypes.FISHING, FishingParticle.Factory::new);
		this.registerFactory(ParticleTypes.FLAME, FlameParticle.Factory::new);
		this.registerFactory(ParticleTypes.SCULK_SOUL, SoulParticle.SculkSoulFactory::new);
		this.registerFactory(ParticleTypes.SCULK_CHARGE, SculkChargeParticle.Factory::new);
		this.registerFactory(ParticleTypes.SCULK_CHARGE_POP, SculkChargePopParticle.Factory::new);
		this.registerFactory(ParticleTypes.SOUL, SoulParticle.Factory::new);
		this.registerFactory(ParticleTypes.SOUL_FIRE_FLAME, FlameParticle.Factory::new);
		this.registerFactory(ParticleTypes.FLASH, FireworksSparkParticle.FlashFactory::new);
		this.registerFactory(ParticleTypes.HAPPY_VILLAGER, SuspendParticle.HappyVillagerFactory::new);
		this.registerFactory(ParticleTypes.HEART, EmotionParticle.HeartFactory::new);
		this.registerFactory(ParticleTypes.INSTANT_EFFECT, SpellParticle.InstantFactory::new);
		this.registerFactory(ParticleTypes.ITEM, new CrackParticle.ItemFactory());
		this.registerFactory(ParticleTypes.ITEM_SLIME, new CrackParticle.SlimeballFactory());
		this.registerFactory(ParticleTypes.ITEM_SNOWBALL, new CrackParticle.SnowballFactory());
		this.registerFactory(ParticleTypes.LARGE_SMOKE, LargeFireSmokeParticle.Factory::new);
		this.registerFactory(ParticleTypes.LAVA, LavaEmberParticle.Factory::new);
		this.registerFactory(ParticleTypes.MYCELIUM, SuspendParticle.MyceliumFactory::new);
		this.registerFactory(ParticleTypes.NAUTILUS, EnchantGlyphParticle.NautilusFactory::new);
		this.registerFactory(ParticleTypes.NOTE, NoteParticle.Factory::new);
		this.registerFactory(ParticleTypes.POOF, ExplosionSmokeParticle.Factory::new);
		this.registerFactory(ParticleTypes.PORTAL, PortalParticle.Factory::new);
		this.registerFactory(ParticleTypes.RAIN, RainSplashParticle.Factory::new);
		this.registerFactory(ParticleTypes.SMOKE, FireSmokeParticle.Factory::new);
		this.registerFactory(ParticleTypes.SNEEZE, CloudParticle.SneezeFactory::new);
		this.registerFactory(ParticleTypes.SNOWFLAKE, SnowflakeParticle.Factory::new);
		this.registerFactory(ParticleTypes.SPIT, SpitParticle.Factory::new);
		this.registerFactory(ParticleTypes.SWEEP_ATTACK, SweepAttackParticle.Factory::new);
		this.registerFactory(ParticleTypes.TOTEM_OF_UNDYING, TotemParticle.Factory::new);
		this.registerFactory(ParticleTypes.SQUID_INK, SquidInkParticle.Factory::new);
		this.registerFactory(ParticleTypes.UNDERWATER, WaterSuspendParticle.UnderwaterFactory::new);
		this.registerFactory(ParticleTypes.SPLASH, WaterSplashParticle.SplashFactory::new);
		this.registerFactory(ParticleTypes.WITCH, SpellParticle.WitchFactory::new);
		this.registerFactory(ParticleTypes.DRIPPING_HONEY, BlockLeakParticle.DrippingHoneyFactory::new);
		this.registerFactory(ParticleTypes.FALLING_HONEY, BlockLeakParticle.FallingHoneyFactory::new);
		this.registerFactory(ParticleTypes.LANDING_HONEY, BlockLeakParticle.LandingHoneyFactory::new);
		this.registerFactory(ParticleTypes.FALLING_NECTAR, BlockLeakParticle.FallingNectarFactory::new);
		this.registerFactory(ParticleTypes.FALLING_SPORE_BLOSSOM, BlockLeakParticle.FallingSporeBlossomFactory::new);
		this.registerFactory(ParticleTypes.SPORE_BLOSSOM_AIR, WaterSuspendParticle.SporeBlossomAirFactory::new);
		this.registerFactory(ParticleTypes.ASH, AshParticle.Factory::new);
		this.registerFactory(ParticleTypes.CRIMSON_SPORE, WaterSuspendParticle.CrimsonSporeFactory::new);
		this.registerFactory(ParticleTypes.WARPED_SPORE, WaterSuspendParticle.WarpedSporeFactory::new);
		this.registerFactory(ParticleTypes.DRIPPING_OBSIDIAN_TEAR, BlockLeakParticle.DrippingObsidianTearFactory::new);
		this.registerFactory(ParticleTypes.FALLING_OBSIDIAN_TEAR, BlockLeakParticle.FallingObsidianTearFactory::new);
		this.registerFactory(ParticleTypes.LANDING_OBSIDIAN_TEAR, BlockLeakParticle.LandingObsidianTearFactory::new);
		this.registerFactory(ParticleTypes.REVERSE_PORTAL, ReversePortalParticle.Factory::new);
		this.registerFactory(ParticleTypes.WHITE_ASH, WhiteAshParticle.Factory::new);
		this.registerFactory(ParticleTypes.SMALL_FLAME, FlameParticle.SmallFactory::new);
		this.registerFactory(ParticleTypes.DRIPPING_DRIPSTONE_WATER, BlockLeakParticle.FallingDripstoneWaterFactory::new);
		this.registerFactory(ParticleTypes.FALLING_DRIPSTONE_WATER, BlockLeakParticle.DripstoneLavaSplashFactory::new);
		this.registerFactory(ParticleTypes.DRIPPING_DRIPSTONE_LAVA, BlockLeakParticle.FallingDripstoneLavaFactory::new);
		this.registerFactory(ParticleTypes.FALLING_DRIPSTONE_LAVA, BlockLeakParticle.LandingDripstoneLavaFactory::new);
		this.registerFactory(ParticleTypes.VIBRATION, VibrationParticle.Factory::new);
		this.registerFactory(ParticleTypes.GLOW_SQUID_INK, SquidInkParticle.GlowSquidInkFactory::new);
		this.registerFactory(ParticleTypes.GLOW, GlowParticle.GlowFactory::new);
		this.registerFactory(ParticleTypes.WAX_ON, GlowParticle.WaxOnFactory::new);
		this.registerFactory(ParticleTypes.WAX_OFF, GlowParticle.WaxOffFactory::new);
		this.registerFactory(ParticleTypes.ELECTRIC_SPARK, GlowParticle.ElectricSparkFactory::new);
		this.registerFactory(ParticleTypes.SCRAPE, GlowParticle.ScrapeFactory::new);
		this.registerFactory(ParticleTypes.SHRIEK, ShriekParticle.Factory::new);
		this.registerFactory(ParticleTypes.ALLAY_DUST, GlowParticle.AllayDustFactory::new);
	}

	private <T extends ParticleEffect> void registerFactory(ParticleType<T> type, ParticleFactory<T> factory) {
		this.factories.put(Registry.PARTICLE_TYPE.getRawId(type), factory);
	}

	private <T extends ParticleEffect> void registerFactory(ParticleType<T> type, ParticleManager.SpriteAwareFactory<T> factory) {
		ParticleManager.SimpleSpriteProvider simpleSpriteProvider = new ParticleManager.SimpleSpriteProvider();
		this.spriteAwareFactories.put(Registry.PARTICLE_TYPE.getId(type), simpleSpriteProvider);
		this.factories.put(Registry.PARTICLE_TYPE.getRawId(type), factory.create(simpleSpriteProvider));
	}

	@Override
	public CompletableFuture<Void> reload(
		ResourceReloader.Synchronizer synchronizer,
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
			.map(id -> CompletableFuture.runAsync(() -> this.loadTextureList(manager, id, map), prepareExecutor))
			.toArray(CompletableFuture[]::new);
		return CompletableFuture.allOf(completableFutures)
			.thenApplyAsync(v -> {
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
			Reader reader = resourceManager.openAsReader(identifier);

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
						id, (List)list.stream().map(identifierx -> new Identifier(identifierx.getNamespace(), "particle/" + identifierx.getPath())).collect(Collectors.toList())
					);
				}
			} catch (Throwable var10) {
				if (reader != null) {
					try {
						reader.close();
					} catch (Throwable var9) {
						var10.addSuppressed(var9);
					}
				}

				throw var10;
			}

			if (reader != null) {
				reader.close();
			}
		} catch (IOException var11) {
			throw new IllegalStateException("Failed to load description for particle " + id, var11);
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
		Optional<ParticleGroup> optional = particle.getGroup();
		if (optional.isPresent()) {
			if (this.canAdd((ParticleGroup)optional.get())) {
				this.newParticles.add(particle);
				this.addTo((ParticleGroup)optional.get(), 1);
			}
		} else {
			this.newParticles.add(particle);
		}
	}

	public void tick() {
		this.particles.forEach((sheet, queue) -> {
			this.world.getProfiler().push(sheet.toString());
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
				((Queue)this.particles.computeIfAbsent(particle.getType(), sheet -> EvictingQueue.create(16384))).add(particle);
			}
		}
	}

	/**
	 * Ticks all particles belonging to the same texture sheet.
	 * 
	 * @param particles a collection of particles from the same sheet
	 */
	private void tickParticles(Collection<Particle> particles) {
		if (!particles.isEmpty()) {
			Iterator<Particle> iterator = particles.iterator();

			while (iterator.hasNext()) {
				Particle particle = (Particle)iterator.next();
				this.tickParticle(particle);
				if (!particle.isAlive()) {
					particle.getGroup().ifPresent(group -> this.addTo(group, -1));
					iterator.remove();
				}
			}
		}
	}

	private void addTo(ParticleGroup group, int count) {
		this.groupCounts.addTo(group, count);
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
		MatrixStack matrices, VertexConsumerProvider.Immediate vertexConsumers, LightmapTextureManager lightmapTextureManager, Camera camera, float tickDelta
	) {
		lightmapTextureManager.enable();
		RenderSystem.enableDepthTest();
		MatrixStack matrixStack = RenderSystem.getModelViewStack();
		matrixStack.push();
		matrixStack.multiplyPositionMatrix(matrices.peek().getPositionMatrix());
		RenderSystem.applyModelViewMatrix();

		for (ParticleTextureSheet particleTextureSheet : PARTICLE_TEXTURE_SHEETS) {
			Iterable<Particle> iterable = (Iterable<Particle>)this.particles.get(particleTextureSheet);
			if (iterable != null) {
				RenderSystem.setShader(GameRenderer::getParticleShader);
				RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
				Tessellator tessellator = Tessellator.getInstance();
				BufferBuilder bufferBuilder = tessellator.getBuffer();
				particleTextureSheet.begin(bufferBuilder, this.textureManager);

				for (Particle particle : iterable) {
					try {
						particle.buildGeometry(bufferBuilder, camera, tickDelta);
					} catch (Throwable var17) {
						CrashReport crashReport = CrashReport.create(var17, "Rendering Particle");
						CrashReportSection crashReportSection = crashReport.addElement("Particle being rendered");
						crashReportSection.add("Particle", particle::toString);
						crashReportSection.add("Particle Type", particleTextureSheet::toString);
						throw new CrashException(crashReport);
					}
				}

				particleTextureSheet.draw(tessellator);
			}
		}

		matrixStack.pop();
		RenderSystem.applyModelViewMatrix();
		RenderSystem.depthMask(true);
		RenderSystem.disableBlend();
		lightmapTextureManager.disable();
	}

	public void setWorld(@Nullable ClientWorld world) {
		this.world = world;
		this.particles.clear();
		this.newEmitterParticles.clear();
		this.groupCounts.clear();
	}

	public void addBlockBreakParticles(BlockPos pos, BlockState state) {
		if (!state.isAir()) {
			VoxelShape voxelShape = state.getOutlineShape(this.world, pos);
			double d = 0.25;
			voxelShape.forEachBox(
				(minX, minY, minZ, maxX, maxY, maxZ) -> {
					double dx = Math.min(1.0, maxX - minX);
					double e = Math.min(1.0, maxY - minY);
					double f = Math.min(1.0, maxZ - minZ);
					int i = Math.max(2, MathHelper.ceil(dx / 0.25));
					int j = Math.max(2, MathHelper.ceil(e / 0.25));
					int k = Math.max(2, MathHelper.ceil(f / 0.25));

					for (int l = 0; l < i; l++) {
						for (int m = 0; m < j; m++) {
							for (int n = 0; n < k; n++) {
								double g = ((double)l + 0.5) / (double)i;
								double h = ((double)m + 0.5) / (double)j;
								double o = ((double)n + 0.5) / (double)k;
								double p = g * dx + minX;
								double q = h * e + minY;
								double r = o * f + minZ;
								this.addParticle(
									new BlockDustParticle(this.world, (double)pos.getX() + p, (double)pos.getY() + q, (double)pos.getZ() + r, g - 0.5, h - 0.5, o - 0.5, state, pos)
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
		if (blockState.getRenderType() != BlockRenderType.INVISIBLE) {
			int i = pos.getX();
			int j = pos.getY();
			int k = pos.getZ();
			float f = 0.1F;
			Box box = blockState.getOutlineShape(this.world, pos).getBoundingBox();
			double d = (double)i + this.random.nextDouble() * (box.maxX - box.minX - 0.2F) + 0.1F + box.minX;
			double e = (double)j + this.random.nextDouble() * (box.maxY - box.minY - 0.2F) + 0.1F + box.minY;
			double g = (double)k + this.random.nextDouble() * (box.maxZ - box.minZ - 0.2F) + 0.1F + box.minZ;
			if (direction == Direction.DOWN) {
				e = (double)j + box.minY - 0.1F;
			}

			if (direction == Direction.UP) {
				e = (double)j + box.maxY + 0.1F;
			}

			if (direction == Direction.NORTH) {
				g = (double)k + box.minZ - 0.1F;
			}

			if (direction == Direction.SOUTH) {
				g = (double)k + box.maxZ + 0.1F;
			}

			if (direction == Direction.WEST) {
				d = (double)i + box.minX - 0.1F;
			}

			if (direction == Direction.EAST) {
				d = (double)i + box.maxX + 0.1F;
			}

			this.addParticle(new BlockDustParticle(this.world, d, e, g, 0.0, 0.0, 0.0, blockState, pos).move(0.2F).scale(0.6F));
		}
	}

	public String getDebugString() {
		return String.valueOf(this.particles.values().stream().mapToInt(Collection::size).sum());
	}

	/**
	 * {@return whether another particle from {@code group} can be rendered by this
	 * manager}
	 */
	private boolean canAdd(ParticleGroup group) {
		return this.groupCounts.getInt(group) < group.getMaxCount();
	}

	@Environment(EnvType.CLIENT)
	static class SimpleSpriteProvider implements SpriteProvider {
		private List<Sprite> sprites;

		@Override
		public Sprite getSprite(int i, int j) {
			return (Sprite)this.sprites.get(i * (this.sprites.size() - 1) / j);
		}

		@Override
		public Sprite getSprite(AbstractRandom random) {
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
