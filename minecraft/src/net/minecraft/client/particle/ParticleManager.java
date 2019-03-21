package net.minecraft.client.particle;

import com.google.common.base.Charsets;
import com.google.common.collect.EvictingQueue;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import com.mojang.blaze3d.platform.GlStateManager;
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
import java.util.Set;
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
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.texture.MissingSprite;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.entity.Entity;
import net.minecraft.particle.ParticleParameters;
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
import net.minecraft.util.math.BoundingBox;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class ParticleManager implements ResourceReloadListener {
	private static final List<ParticleTextureSheet> field_17820 = ImmutableList.of(
		ParticleTextureSheet.TERRAIN_SHEET,
		ParticleTextureSheet.PARTICLE_SHEET_OPAQUE,
		ParticleTextureSheet.PARTICLE_SHEET_LIT,
		ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT,
		ParticleTextureSheet.CUSTOM
	);
	protected World world;
	private final Map<ParticleTextureSheet, Queue<Particle>> particleQueues = Maps.<ParticleTextureSheet, Queue<Particle>>newIdentityHashMap();
	private final Queue<EmitterParticle> newEmitterParticles = Queues.<EmitterParticle>newArrayDeque();
	private final TextureManager textureManager;
	private final Random random = new Random();
	private final Int2ObjectMap<ParticleFactory<?>> factories = new Int2ObjectOpenHashMap<>();
	private final Queue<Particle> newParticles = Queues.<Particle>newArrayDeque();
	private final Map<Identifier, ParticleManager.class_4090> field_18300 = Maps.<Identifier, ParticleManager.class_4090>newHashMap();
	private final SpriteAtlasTexture field_18301 = new SpriteAtlasTexture("textures/particle");

	public ParticleManager(World world, TextureManager textureManager) {
		textureManager.registerTextureUpdateable(SpriteAtlasTexture.PARTICLE_ATLAS_TEX, this.field_18301);
		this.world = world;
		this.textureManager = textureManager;
		this.registerDefaultFactories();
	}

	private void registerDefaultFactories() {
		this.method_18834(ParticleTypes.field_11225, SpellParticle.EntityAmbientFactory::new);
		this.method_18834(ParticleTypes.field_11231, EmotionParticle.AngryVillagerFactory::new);
		this.registerFactory(ParticleTypes.field_11235, new BarrierParticle.Factory());
		this.registerFactory(ParticleTypes.field_11217, new BlockCrackParticle.Factory());
		this.method_18834(ParticleTypes.field_11247, WaterBubbleParticle.Factory::new);
		this.method_18834(ParticleTypes.field_11238, BubbleColumnUpParticle.Factory::new);
		this.method_18834(ParticleTypes.field_11241, BubblePopParticle.Factory::new);
		this.method_18834(ParticleTypes.field_17430, CampfireSmokeParticle.class_3938::new);
		this.method_18834(ParticleTypes.field_17431, CampfireSmokeParticle.class_3995::new);
		this.method_18834(ParticleTypes.field_11204, CloudParticle.CloudFactory::new);
		this.method_18834(ParticleTypes.field_17741, SuspendParticle.class_3991::new);
		this.method_18834(ParticleTypes.field_11205, DamageParticle.class_3939::new);
		this.method_18834(ParticleTypes.field_11243, CurrentDownParticle.Factory::new);
		this.method_18834(ParticleTypes.field_11209, DamageParticle.DefaultFactory::new);
		this.method_18834(ParticleTypes.field_11216, DragonBreathParticle.Factory::new);
		this.method_18834(ParticleTypes.field_11222, SuspendParticle.DolphinFactory::new);
		this.method_18834(ParticleTypes.field_11223, BlockLeakParticle.LavaFactory::new);
		this.method_18834(ParticleTypes.field_18304, BlockLeakParticle.class_4086::new);
		this.method_18834(ParticleTypes.field_18305, BlockLeakParticle.class_4087::new);
		this.method_18834(ParticleTypes.field_11232, BlockLeakParticle.class_4088::new);
		this.method_18834(ParticleTypes.field_18306, BlockLeakParticle.WaterFactory::new);
		this.method_18834(ParticleTypes.field_11212, RedDustParticle.Factory::new);
		this.method_18834(ParticleTypes.field_11245, SpellParticle.DefaultFactory::new);
		this.registerFactory(ParticleTypes.field_11250, new ElderGuardianAppearanceParticle.Factory());
		this.method_18834(ParticleTypes.field_11208, DamageParticle.EnchantedHitFactory::new);
		this.method_18834(ParticleTypes.field_11215, EnchantGlyphParticle.NautilusFactory::new);
		this.method_18834(ParticleTypes.field_11207, EndRodParticle.Factory::new);
		this.method_18834(ParticleTypes.field_11226, SpellParticle.EntityFactory::new);
		this.registerFactory(ParticleTypes.field_11221, new ExplosionEmitterParticle.Factory());
		this.method_18834(ParticleTypes.field_11236, ExplosionLargeParticle.Factory::new);
		this.method_18834(ParticleTypes.field_11206, BlockFallingDustParticle.Factory::new);
		this.method_18834(ParticleTypes.field_11248, FireworksSparkParticle.Factory::new);
		this.method_18834(ParticleTypes.field_11244, FishingParticle.Factory::new);
		this.method_18834(ParticleTypes.field_11240, FlameParticle.Factory::new);
		this.method_18834(ParticleTypes.field_17909, FireworksSparkParticle.class_3997::new);
		this.method_18834(ParticleTypes.field_11211, SuspendParticle.HappyVillagerFactory::new);
		this.method_18834(ParticleTypes.field_11201, EmotionParticle.HeartFactory::new);
		this.method_18834(ParticleTypes.field_11213, SpellParticle.InstantFactory::new);
		this.registerFactory(ParticleTypes.field_11218, new CrackParticle.ItemFactory());
		this.registerFactory(ParticleTypes.field_11246, new CrackParticle.SlimeballFactory());
		this.registerFactory(ParticleTypes.field_11230, new CrackParticle.SnowballFactory());
		this.method_18834(ParticleTypes.field_11237, FireSmokeLargeParticle.Factory::new);
		this.method_18834(ParticleTypes.field_11239, LavaEmberParticle.Factory::new);
		this.method_18834(ParticleTypes.field_11219, SuspendParticle.MyceliumFactory::new);
		this.method_18834(ParticleTypes.field_11229, EnchantGlyphParticle.EnchantFactory::new);
		this.method_18834(ParticleTypes.field_11224, NoteParticle.Factory::new);
		this.method_18834(ParticleTypes.field_11203, ExplosionSmokeParticle.Factory::new);
		this.method_18834(ParticleTypes.field_11214, PortalParticle.Factory::new);
		this.method_18834(ParticleTypes.field_11242, RainSplashParticle.Factory::new);
		this.method_18834(ParticleTypes.field_11251, FireSmokeParticle.Factory::new);
		this.method_18834(ParticleTypes.field_11234, CloudParticle.SneezeFactory::new);
		this.method_18834(ParticleTypes.field_11228, SpitParticle.Factory::new);
		this.method_18834(ParticleTypes.field_11227, SweepAttackParticle.Factory::new);
		this.method_18834(ParticleTypes.field_11220, TotemParticle.Factory::new);
		this.method_18834(ParticleTypes.field_11233, SquidInkParticle.Factory::new);
		this.method_18834(ParticleTypes.field_11210, WaterSuspendParticle.UnderwaterFactory::new);
		this.method_18834(ParticleTypes.field_11202, WaterSplashParticle.SplashFactory::new);
		this.method_18834(ParticleTypes.field_11249, SpellParticle.WitchFactory::new);
	}

	private <T extends ParticleParameters> void registerFactory(ParticleType<T> particleType, ParticleFactory<T> particleFactory) {
		this.factories.put(Registry.PARTICLE_TYPE.getRawId(particleType), particleFactory);
	}

	private <T extends ParticleParameters> void method_18834(ParticleType<T> particleType, ParticleManager.class_4091<T> arg) {
		ParticleManager.class_4090 lv = new ParticleManager.class_4090();
		this.field_18300.put(Registry.PARTICLE_TYPE.getId(particleType), lv);
		this.factories.put(Registry.PARTICLE_TYPE.getRawId(particleType), arg.create(lv));
	}

	@Override
	public CompletableFuture<Void> apply(
		ResourceReloadListener.Helper helper, ResourceManager resourceManager, Profiler profiler, Profiler profiler2, Executor executor, Executor executor2
	) {
		Map<Identifier, List<Identifier>> map = Maps.<Identifier, List<Identifier>>newConcurrentMap();
		CompletableFuture<?>[] completableFutures = (CompletableFuture<?>[])Registry.PARTICLE_TYPE
			.getIds()
			.stream()
			.map(identifier -> CompletableFuture.runAsync(() -> this.method_18836(resourceManager, identifier, map), executor))
			.toArray(CompletableFuture[]::new);
		return CompletableFuture.allOf(completableFutures)
			.thenApplyAsync(void_ -> {
				profiler.startTick();
				profiler.push("stitching");
				Set<Identifier> set = (Set<Identifier>)map.values().stream().flatMap(Collection::stream).collect(Collectors.toSet());
				SpriteAtlasTexture.Data data = this.field_18301.stitch(resourceManager, set, profiler);
				profiler.pop();
				profiler.endTick();
				return data;
			}, executor)
			.thenCompose(helper::waitForAll)
			.thenAcceptAsync(
				data -> {
					profiler2.startTick();
					profiler2.push("upload");
					this.field_18301.upload(data);
					profiler2.swap("bindSpriteSets");
					Sprite sprite = this.field_18301.getSprite(MissingSprite.getMissingSpriteId());
					map.forEach(
						(identifier, list) -> {
							ImmutableList<Sprite> immutableList = list.isEmpty()
								? ImmutableList.of(sprite)
								: (ImmutableList)list.stream().map(this.field_18301::getSprite).collect(ImmutableList.toImmutableList());
							((ParticleManager.class_4090)this.field_18300.get(identifier)).method_18838(immutableList);
						}
					);
					profiler2.pop();
					profiler2.endTick();
				},
				executor2
			);
	}

	public void method_18829() {
		this.field_18301.clear();
	}

	private void method_18836(ResourceManager resourceManager, Identifier identifier, Map<Identifier, List<Identifier>> map) {
		Identifier identifier2 = new Identifier(identifier.getNamespace(), "particles/" + identifier.getPath() + ".json");

		try {
			Resource resource = resourceManager.getResource(identifier2);
			Throwable var6 = null;

			try {
				Reader reader = new InputStreamReader(resource.getInputStream(), Charsets.UTF_8);
				Throwable var8 = null;

				try {
					ParticleTextureData particleTextureData = ParticleTextureData.load(JsonHelper.deserialize(reader));
					List<Identifier> list = particleTextureData.getTextureList();
					boolean bl = this.field_18300.containsKey(identifier);
					if (list == null) {
						if (bl) {
							throw new IllegalStateException("Missing texture list for particle " + identifier);
						}
					} else {
						if (!bl) {
							throw new IllegalStateException("Redundant texture list for particle " + identifier);
						}

						map.put(identifier, list);
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
			throw new IllegalStateException("Failed to load description for particle " + identifier, var39);
		}
	}

	public void addEmitter(Entity entity, ParticleParameters particleParameters) {
		this.newEmitterParticles.add(new EmitterParticle(this.world, entity, particleParameters));
	}

	public void addEmitter(Entity entity, ParticleParameters particleParameters, int i) {
		this.newEmitterParticles.add(new EmitterParticle(this.world, entity, particleParameters, i));
	}

	@Nullable
	public Particle addParticle(ParticleParameters particleParameters, double d, double e, double f, double g, double h, double i) {
		Particle particle = this.createParticle(particleParameters, d, e, f, g, h, i);
		if (particle != null) {
			this.addParticle(particle);
			return particle;
		} else {
			return null;
		}
	}

	@Nullable
	private <T extends ParticleParameters> Particle createParticle(T particleParameters, double d, double e, double f, double g, double h, double i) {
		ParticleFactory<T> particleFactory = (ParticleFactory<T>)this.factories
			.get(Registry.PARTICLE_TYPE.getRawId((ParticleType<? extends ParticleParameters>)particleParameters.getType()));
		return particleFactory == null ? null : particleFactory.createParticle(particleParameters, this.world, d, e, f, g, h, i);
	}

	public void addParticle(Particle particle) {
		this.newParticles.add(particle);
	}

	public void tick() {
		this.particleQueues.forEach((particleTextureSheet, queue) -> {
			this.world.getProfiler().push(particleTextureSheet.toString());
			this.updateParticleQueue(queue);
			this.world.getProfiler().pop();
		});
		if (!this.newEmitterParticles.isEmpty()) {
			List<EmitterParticle> list = Lists.<EmitterParticle>newArrayList();

			for (EmitterParticle emitterParticle : this.newEmitterParticles) {
				emitterParticle.update();
				if (!emitterParticle.isAlive()) {
					list.add(emitterParticle);
				}
			}

			this.newEmitterParticles.removeAll(list);
		}

		Particle particle;
		if (!this.newParticles.isEmpty()) {
			while ((particle = (Particle)this.newParticles.poll()) != null) {
				((Queue)this.particleQueues.computeIfAbsent(particle.getTextureSheet(), particleTextureSheet -> EvictingQueue.create(16384))).add(particle);
			}
		}
	}

	private void updateParticleQueue(Collection<Particle> collection) {
		if (!collection.isEmpty()) {
			Iterator<Particle> iterator = collection.iterator();

			while (iterator.hasNext()) {
				Particle particle = (Particle)iterator.next();
				this.updateParticle(particle);
				if (!particle.isAlive()) {
					iterator.remove();
				}
			}
		}
	}

	private void updateParticle(Particle particle) {
		try {
			particle.update();
		} catch (Throwable var5) {
			CrashReport crashReport = CrashReport.create(var5, "Ticking Particle");
			CrashReportSection crashReportSection = crashReport.addElement("Particle being ticked");
			crashReportSection.add("Particle", particle::toString);
			crashReportSection.add("Particle Type", particle.getTextureSheet()::toString);
			throw new CrashException(crashReport);
		}
	}

	public void renderUnlitParticles(Camera camera, float f) {
		float g = MathHelper.cos(camera.getYaw() * (float) (Math.PI / 180.0));
		float h = MathHelper.sin(camera.getYaw() * (float) (Math.PI / 180.0));
		float i = -h * MathHelper.sin(camera.getPitch() * (float) (Math.PI / 180.0));
		float j = g * MathHelper.sin(camera.getPitch() * (float) (Math.PI / 180.0));
		float k = MathHelper.cos(camera.getPitch() * (float) (Math.PI / 180.0));
		Particle.cameraX = camera.getPos().x;
		Particle.cameraY = camera.getPos().y;
		Particle.cameraZ = camera.getPos().z;

		for (ParticleTextureSheet particleTextureSheet : field_17820) {
			Iterable<Particle> iterable = (Iterable<Particle>)this.particleQueues.get(particleTextureSheet);
			if (iterable != null) {
				GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
				Tessellator tessellator = Tessellator.getInstance();
				BufferBuilder bufferBuilder = tessellator.getBufferBuilder();
				particleTextureSheet.begin(bufferBuilder, this.textureManager);

				for (Particle particle : iterable) {
					try {
						particle.buildGeometry(bufferBuilder, camera, f, g, k, h, i, j);
					} catch (Throwable var18) {
						CrashReport crashReport = CrashReport.create(var18, "Rendering Particle");
						CrashReportSection crashReportSection = crashReport.addElement("Particle being rendered");
						crashReportSection.add("Particle", particle::toString);
						crashReportSection.add("Particle Type", particleTextureSheet::toString);
						throw new CrashException(crashReport);
					}
				}

				particleTextureSheet.draw(tessellator);
			}
		}

		GlStateManager.depthMask(true);
		GlStateManager.disableBlend();
		GlStateManager.alphaFunc(516, 0.1F);
	}

	public void setWorld(@Nullable World world) {
		this.world = world;
		this.particleQueues.clear();
		this.newEmitterParticles.clear();
	}

	public void addBlockBreakParticles(BlockPos blockPos, BlockState blockState) {
		if (!blockState.isAir()) {
			VoxelShape voxelShape = blockState.getOutlineShape(this.world, blockPos);
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
									new BlockCrackParticle(
											this.world, (double)blockPos.getX() + v, (double)blockPos.getY() + w, (double)blockPos.getZ() + x, s - 0.5, t - 0.5, u - 0.5, blockState
										)
										.setBlockPos(blockPos)
								);
							}
						}
					}
				}
			);
		}
	}

	public void addBlockBreakingParticles(BlockPos blockPos, Direction direction) {
		BlockState blockState = this.world.getBlockState(blockPos);
		if (blockState.getRenderType() != BlockRenderType.field_11455) {
			int i = blockPos.getX();
			int j = blockPos.getY();
			int k = blockPos.getZ();
			float f = 0.1F;
			BoundingBox boundingBox = blockState.getOutlineShape(this.world, blockPos).getBoundingBox();
			double d = (double)i + this.random.nextDouble() * (boundingBox.maxX - boundingBox.minX - 0.2F) + 0.1F + boundingBox.minX;
			double e = (double)j + this.random.nextDouble() * (boundingBox.maxY - boundingBox.minY - 0.2F) + 0.1F + boundingBox.minY;
			double g = (double)k + this.random.nextDouble() * (boundingBox.maxZ - boundingBox.minZ - 0.2F) + 0.1F + boundingBox.minZ;
			if (direction == Direction.DOWN) {
				e = (double)j + boundingBox.minY - 0.1F;
			}

			if (direction == Direction.UP) {
				e = (double)j + boundingBox.maxY + 0.1F;
			}

			if (direction == Direction.NORTH) {
				g = (double)k + boundingBox.minZ - 0.1F;
			}

			if (direction == Direction.SOUTH) {
				g = (double)k + boundingBox.maxZ + 0.1F;
			}

			if (direction == Direction.WEST) {
				d = (double)i + boundingBox.minX - 0.1F;
			}

			if (direction == Direction.EAST) {
				d = (double)i + boundingBox.maxX + 0.1F;
			}

			this.addParticle(new BlockCrackParticle(this.world, d, e, g, 0.0, 0.0, 0.0, blockState).setBlockPos(blockPos).move(0.2F).method_3087(0.6F));
		}
	}

	public String getDebugString() {
		return String.valueOf(this.particleQueues.values().stream().mapToInt(Collection::size).sum());
	}

	@Environment(EnvType.CLIENT)
	class class_4090 implements SpriteProvider {
		private List<Sprite> field_18303;

		private class_4090() {
		}

		@Override
		public Sprite getSprite(int i, int j) {
			return (Sprite)this.field_18303.get(i * (this.field_18303.size() - 1) / j);
		}

		@Override
		public Sprite getSprite(Random random) {
			return (Sprite)this.field_18303.get(random.nextInt(this.field_18303.size()));
		}

		public void method_18838(List<Sprite> list) {
			this.field_18303 = ImmutableList.copyOf(list);
		}
	}

	@FunctionalInterface
	@Environment(EnvType.CLIENT)
	interface class_4091<T extends ParticleParameters> {
		ParticleFactory<T> create(SpriteProvider spriteProvider);
	}
}
