package net.minecraft.client.particle;

import com.google.common.collect.EvictingQueue;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.platform.GlStateManager;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_295;
import net.minecraft.class_3937;
import net.minecraft.class_3999;
import net.minecraft.class_4001;
import net.minecraft.class_4002;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.texture.MissingSprite;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.entity.Entity;
import net.minecraft.particle.ParticleParameters;
import net.minecraft.particle.ParticleType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloadListener;
import net.minecraft.util.Identifier;
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
public class ParticleManager implements ResourceReloadListener<SpriteAtlasTexture.class_4007> {
	private static final List<class_3999> field_17820 = ImmutableList.of(
		class_3999.field_17827, class_3999.field_17828, class_3999.field_17830, class_3999.field_17829, class_3999.field_17831
	);
	protected World world;
	private final Map<class_3999, Queue<Particle>> particleQueues = Maps.<class_3999, Queue<Particle>>newIdentityHashMap();
	private final Queue<EmitterParticle> newEmitterParticles = Queues.<EmitterParticle>newArrayDeque();
	private final TextureManager textureManager;
	private final Random random = new Random();
	private final Int2ObjectMap<ParticleFactory<?>> factories = new Int2ObjectOpenHashMap<>();
	private final Queue<Particle> newParticles = Queues.<Particle>newArrayDeque();
	private final SpriteAtlasTexture field_17821;
	private final Set<Identifier> field_17822 = Sets.<Identifier>newHashSet();

	private class_4002 method_18127(Collection<Identifier> collection) {
		if (collection.size() <= 1) {
			Iterator<Identifier> iterator = collection.iterator();
			final Identifier identifier = iterator.hasNext() ? (Identifier)iterator.next() : MissingSprite.getMissingSpriteId();
			return new class_4002() {
				@Override
				public Sprite method_18138(int i, int j) {
					return ParticleManager.this.field_17821.getSprite(identifier);
				}

				@Override
				public Sprite method_18139(Random random) {
					return ParticleManager.this.field_17821.getSprite(identifier);
				}
			};
		} else {
			final List<Identifier> list = ImmutableList.copyOf(collection);
			return new class_4002() {
				@Override
				public Sprite method_18138(int i, int j) {
					Identifier identifier = (Identifier)list.get(i * (list.size() - 1) / j);
					return ParticleManager.this.field_17821.getSprite(identifier);
				}

				@Override
				public Sprite method_18139(Random random) {
					Identifier identifier = (Identifier)list.get(random.nextInt(list.size()));
					return ParticleManager.this.field_17821.getSprite(identifier);
				}
			};
		}
	}

	public ParticleManager(World world, TextureManager textureManager) {
		this.world = world;
		this.textureManager = textureManager;
		this.field_17821 = new SpriteAtlasTexture("textures/particle");
		textureManager.registerTextureUpdateable(SpriteAtlasTexture.field_17898, this.field_17821);
		this.registerDefaultFactories(collection -> {
			this.field_17822.addAll(collection);
			return this.method_18127(collection);
		});
	}

	@Override
	public CompletableFuture<SpriteAtlasTexture.class_4007> prepare(ResourceManager resourceManager, Profiler profiler) {
		return CompletableFuture.supplyAsync(() -> {
			profiler.startTick();
			profiler.push("particle_stitching");
			SpriteAtlasTexture.class_4007 lv = this.field_17821.method_18163(resourceManager, this.field_17822, profiler);
			profiler.pop();
			profiler.endTick();
			return lv;
		});
	}

	public void method_18128(ResourceManager resourceManager, SpriteAtlasTexture.class_4007 arg, Profiler profiler) {
		profiler.startTick();
		profiler.push("particle_upload");
		this.field_17821.method_18159(arg);
		profiler.pop();
		profiler.endTick();
	}

	private void registerDefaultFactories(class_4001 arg) {
		this.registerFactory(ParticleTypes.field_11225, new SpellParticle.EntityAmbientFactory(arg));
		this.registerFactory(ParticleTypes.field_11231, new EmotionParticle.AngryVillagerFactory(arg));
		this.registerFactory(ParticleTypes.field_11235, new BarrierParticle.Factory());
		this.registerFactory(ParticleTypes.field_11217, new BlockCrackParticle.Factory());
		this.registerFactory(ParticleTypes.field_11247, new WaterBubbleParticle.Factory(arg));
		this.registerFactory(ParticleTypes.field_11238, new BubbleColumnUpParticle.Factory(arg));
		this.registerFactory(ParticleTypes.field_11241, new BubblePopParticle.Factory(arg));
		this.registerFactory(ParticleTypes.field_17430, new class_3937.class_3938(arg));
		this.registerFactory(ParticleTypes.field_17431, new class_3937.class_3939(arg));
		this.registerFactory(ParticleTypes.field_11204, new CloudParticle.CloudFactory(arg));
		this.registerFactory(ParticleTypes.field_17741, new SuspendParticle.class_3991(arg));
		this.registerFactory(ParticleTypes.field_11205, new DamageParticle.CritFactory(arg));
		this.registerFactory(ParticleTypes.field_11243, new CurrentDownParticle.Factory(arg));
		this.registerFactory(ParticleTypes.field_11209, new DamageParticle.DefaultFactory(arg));
		this.registerFactory(ParticleTypes.field_11216, new DragonBreathParticle.Factory(arg));
		this.registerFactory(ParticleTypes.field_11222, new SuspendParticle.DolphinFactory(arg));
		this.registerFactory(ParticleTypes.field_11223, new BlockLeakParticle.LavaFactory(arg));
		this.registerFactory(ParticleTypes.field_11232, new BlockLeakParticle.WaterFactory(arg));
		this.registerFactory(ParticleTypes.field_11212, new RedDustParticle.Factory(arg));
		this.registerFactory(ParticleTypes.field_11245, new SpellParticle.DefaultFactory(arg));
		this.registerFactory(ParticleTypes.field_11250, new ElderGuardianAppearanceParticle.Factory());
		this.registerFactory(ParticleTypes.field_11208, new DamageParticle.EnchantedHitFactory(arg));
		this.registerFactory(ParticleTypes.field_11215, new EnchantGlyphParticle.NautilusFactory(arg));
		this.registerFactory(ParticleTypes.field_11207, new EndRodParticle.Factory(arg));
		this.registerFactory(ParticleTypes.field_11226, new SpellParticle.EntityFactory(arg));
		this.registerFactory(ParticleTypes.field_11221, new ExplosionEmitterParticle.Factory());
		this.registerFactory(ParticleTypes.field_11236, new ExplosionLargeParticle.Factory(arg));
		this.registerFactory(ParticleTypes.field_11206, new BlockFallingDustParticle.Factory(arg));
		this.registerFactory(ParticleTypes.field_11248, new FireworksSparkParticle.Factory(arg));
		this.registerFactory(ParticleTypes.field_11244, new FishingParticle.Factory(arg));
		this.registerFactory(ParticleTypes.field_11240, new FlameParticle.Factory(arg));
		this.registerFactory(ParticleTypes.field_17909, new FireworksSparkParticle.class_3997(arg));
		this.registerFactory(ParticleTypes.field_11211, new SuspendParticle.HappyVillagerFactory(arg));
		this.registerFactory(ParticleTypes.field_11201, new EmotionParticle.HeartFactory(arg));
		this.registerFactory(ParticleTypes.field_11213, new SpellParticle.InstantFactory(arg));
		this.registerFactory(ParticleTypes.field_11218, new CrackParticle.ItemFactory());
		this.registerFactory(ParticleTypes.field_11246, new CrackParticle.SlimeballFactory());
		this.registerFactory(ParticleTypes.field_11230, new CrackParticle.SnowballFactory());
		this.registerFactory(ParticleTypes.field_11237, new FireSmokeLargeParticle.Factory(arg));
		this.registerFactory(ParticleTypes.field_11239, new LavaEmberParticle.Factory(arg));
		this.registerFactory(ParticleTypes.field_11219, new SuspendParticle.MyceliumFactory(arg));
		this.registerFactory(ParticleTypes.field_11229, new EnchantGlyphParticle.EnchantFactory(arg));
		this.registerFactory(ParticleTypes.field_11224, new NoteParticle.Factory(arg));
		this.registerFactory(ParticleTypes.field_11203, new ExplosionSmokeParticle.Factory(arg));
		this.registerFactory(ParticleTypes.field_11214, new PortalParticle.Factory(arg));
		this.registerFactory(ParticleTypes.field_11242, new RainSplashParticle.Factory(arg));
		this.registerFactory(ParticleTypes.field_11251, new FireSmokeParticle.Factory(arg));
		this.registerFactory(ParticleTypes.field_11234, new CloudParticle.SneezeFactory(arg));
		this.registerFactory(ParticleTypes.field_11228, new SpitParticle.Factory(arg));
		this.registerFactory(ParticleTypes.field_11227, new SweepAttackParticle.Factory(arg));
		this.registerFactory(ParticleTypes.field_11220, new TotemParticle.Factory(arg));
		this.registerFactory(ParticleTypes.field_11233, new SquidInkParticle.Factory(arg));
		this.registerFactory(ParticleTypes.field_11210, new WaterSuspendParticle.UnderwaterFactory(arg));
		this.registerFactory(ParticleTypes.field_11202, new WaterSplashParticle.SplashFactory(arg));
		this.registerFactory(ParticleTypes.field_11249, new SpellParticle.WitchFactory(arg));
	}

	public <T extends ParticleParameters> void registerFactory(ParticleType<T> particleType, ParticleFactory<T> particleFactory) {
		this.factories.put(Registry.PARTICLE_TYPE.getRawId(particleType), particleFactory);
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
		this.particleQueues.forEach((arg, queue) -> {
			this.world.getProfiler().push(arg.toString());
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
				((Queue)this.particleQueues.computeIfAbsent(particle.method_18122(), arg -> EvictingQueue.create(16384))).add(particle);
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
			crashReportSection.add("Particle Type", particle.method_18122()::toString);
			throw new CrashException(crashReport);
		}
	}

	public void renderUnlitParticles(Entity entity, float f) {
		float g = class_295.method_1375();
		float h = class_295.method_1380();
		float i = class_295.method_1381();
		float j = class_295.method_1378();
		float k = class_295.method_1377();
		Particle.cameraX = MathHelper.lerp((double)f, entity.prevRenderX, entity.x);
		Particle.cameraY = MathHelper.lerp((double)f, entity.prevRenderY, entity.y);
		Particle.cameraZ = MathHelper.lerp((double)f, entity.prevRenderZ, entity.z);
		Particle.cameraRotation = entity.getRotationVec(f);

		for (class_3999 lv : field_17820) {
			Iterable<Particle> iterable = (Iterable<Particle>)this.particleQueues.get(lv);
			if (iterable != null) {
				GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
				Tessellator tessellator = Tessellator.getInstance();
				BufferBuilder bufferBuilder = tessellator.getBufferBuilder();
				lv.method_18130(bufferBuilder, this.textureManager);

				for (Particle particle : iterable) {
					try {
						particle.buildGeometry(bufferBuilder, entity, f, g, k, h, i, j);
					} catch (Throwable var18) {
						CrashReport crashReport = CrashReport.create(var18, "Rendering Particle");
						CrashReportSection crashReportSection = crashReport.addElement("Particle being rendered");
						crashReportSection.add("Particle", particle::toString);
						crashReportSection.add("Particle Type", lv::toString);
						throw new CrashException(crashReport);
					}
				}

				lv.method_18131(tessellator);
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
			voxelShape.method_1089(
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

			this.addParticle(new BlockCrackParticle(this.world, d, e, g, 0.0, 0.0, 0.0, blockState).setBlockPos(blockPos).method_3075(0.2F).method_3087(0.6F));
		}
	}

	public String getDebugString() {
		return String.valueOf(this.particleQueues.values().stream().mapToInt(Collection::size).sum());
	}
}
