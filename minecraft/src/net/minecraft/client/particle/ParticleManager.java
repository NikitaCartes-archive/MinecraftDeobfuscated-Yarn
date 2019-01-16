package net.minecraft.client.particle;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.mojang.blaze3d.platform.GlStateManager;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_295;
import net.minecraft.class_3937;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.entity.Entity;
import net.minecraft.particle.ParticleParameters;
import net.minecraft.particle.ParticleType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.Identifier;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.crash.ICrashCallable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class ParticleManager {
	private static final Identifier PARTICLE_TEX = new Identifier("textures/particle/particles.png");
	protected World world;
	private final ArrayDeque<Particle>[][] particleQueues = new ArrayDeque[4][];
	private final Queue<EmitterParticle> newEmitterParticles = Queues.<EmitterParticle>newArrayDeque();
	private final TextureManager textureManager;
	private final Random random = new Random();
	private final Int2ObjectMap<ParticleFactory<?>> factories = new Int2ObjectOpenHashMap<>();
	private final Queue<Particle> newParticles = Queues.<Particle>newArrayDeque();

	public ParticleManager(World world, TextureManager textureManager) {
		this.world = world;
		this.textureManager = textureManager;

		for (int i = 0; i < 4; i++) {
			this.particleQueues[i] = new ArrayDeque[2];

			for (int j = 0; j < 2; j++) {
				this.particleQueues[i][j] = Queues.newArrayDeque();
			}
		}

		this.registerDefaultFactories();
	}

	private void registerDefaultFactories() {
		this.registerFactory(ParticleTypes.field_11225, new SpellParticle.EntityAmbientFactory());
		this.registerFactory(ParticleTypes.field_11231, new EmotionParticle.AngryVillagerFactory());
		this.registerFactory(ParticleTypes.field_11235, new BarrierParticle.Factory());
		this.registerFactory(ParticleTypes.field_11217, new BlockCrackParticle.Factory());
		this.registerFactory(ParticleTypes.field_11247, new WaterBubbleParticle.Factory());
		this.registerFactory(ParticleTypes.field_11238, new BubbleColumnUpParticle.Factory());
		this.registerFactory(ParticleTypes.field_11241, new BubblePopParticle.Factory());
		this.registerFactory(ParticleTypes.field_17430, new class_3937.class_3938());
		this.registerFactory(ParticleTypes.field_17431, new class_3937.class_3939());
		this.registerFactory(ParticleTypes.field_11204, new CloudParticle.CloudFactory());
		this.registerFactory(ParticleTypes.field_11205, new DamageParticle.CritFactory());
		this.registerFactory(ParticleTypes.field_11243, new CurrentDownParticle.Factory());
		this.registerFactory(ParticleTypes.field_11209, new DamageParticle.DefaultFactory());
		this.registerFactory(ParticleTypes.field_11216, new DragonBreathParticle.Factory());
		this.registerFactory(ParticleTypes.field_11222, new SuspendParticle.DolphinFactory());
		this.registerFactory(ParticleTypes.field_11223, new BlockLeakParticle.LavaFactory());
		this.registerFactory(ParticleTypes.field_11232, new BlockLeakParticle.WaterFactory());
		this.registerFactory(ParticleTypes.field_11212, new RedDustParticle.Factory());
		this.registerFactory(ParticleTypes.field_11245, new SpellParticle.DefaultFactory());
		this.registerFactory(ParticleTypes.field_11250, new ElderGuardianAppearanceParticle.Factory());
		this.registerFactory(ParticleTypes.field_11208, new DamageParticle.EnchantedHitFactory());
		this.registerFactory(ParticleTypes.field_11215, new EnchantGlyphParticle.EnchantFactory());
		this.registerFactory(ParticleTypes.field_11207, new EndRodParticle.Factory());
		this.registerFactory(ParticleTypes.field_11226, new SpellParticle.EntityFactory());
		this.registerFactory(ParticleTypes.field_11221, new ExplosionEmitterParticle.Factory());
		this.registerFactory(ParticleTypes.field_11236, new ExplosionLargeParticle.Factory());
		this.registerFactory(ParticleTypes.field_11206, new BlockFallingDustParticle.Factory());
		this.registerFactory(ParticleTypes.field_11248, new FireworksSparkParticle.Factory());
		this.registerFactory(ParticleTypes.field_11244, new FishingParticle.Factory());
		this.registerFactory(ParticleTypes.field_11240, new FlameParticle.Factory());
		this.registerFactory(ParticleTypes.field_11211, new SuspendParticle.HappyVillagerFactory());
		this.registerFactory(ParticleTypes.field_11201, new EmotionParticle.HeartFactory());
		this.registerFactory(ParticleTypes.field_11213, new SpellParticle.InstantFactory());
		this.registerFactory(ParticleTypes.field_11218, new CrackParticle.ItemFactory());
		this.registerFactory(ParticleTypes.field_11246, new CrackParticle.SlimeballFactory());
		this.registerFactory(ParticleTypes.field_11230, new CrackParticle.SnowballFactory());
		this.registerFactory(ParticleTypes.field_11237, new FireSmokeLargeParticle.Factory());
		this.registerFactory(ParticleTypes.field_11239, new LavaEmberParticle.Factory());
		this.registerFactory(ParticleTypes.field_11219, new SuspendParticle.MyceliumFactory());
		this.registerFactory(ParticleTypes.field_11229, new EnchantGlyphParticle.NautilusFactory());
		this.registerFactory(ParticleTypes.field_11224, new NoteParticle.Factory());
		this.registerFactory(ParticleTypes.field_11203, new ExplosionSmokeParticle.Factory());
		this.registerFactory(ParticleTypes.field_11214, new PortalParticle.Factory());
		this.registerFactory(ParticleTypes.field_11242, new RainSplashParticle.Factory());
		this.registerFactory(ParticleTypes.field_11251, new FireSmokeParticle.Factory());
		this.registerFactory(ParticleTypes.field_11234, new CloudParticle.SneezeFactory());
		this.registerFactory(ParticleTypes.field_11228, new SpitParticle.Factory());
		this.registerFactory(ParticleTypes.field_11227, new SweepAttackParticle.Factory());
		this.registerFactory(ParticleTypes.field_11220, new TotemParticle.Factory());
		this.registerFactory(ParticleTypes.field_11233, new SquidInkParticle.Factory());
		this.registerFactory(ParticleTypes.field_11210, new WaterSuspendParticle.UnderwaterFactory());
		this.registerFactory(ParticleTypes.field_11202, new WaterSplashParticle.SplashFactory());
		this.registerFactory(ParticleTypes.field_11249, new SpellParticle.WitchFactory());
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
		for (int i = 0; i < 4; i++) {
			this.updateGroup(i);
		}

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

		if (!this.newParticles.isEmpty()) {
			for (Particle particle = (Particle)this.newParticles.poll(); particle != null; particle = (Particle)this.newParticles.poll()) {
				int j = particle.getParticleGroup();
				int k = particle.hasAlpha() ? 0 : 1;
				if (this.particleQueues[j][k].size() >= 16384) {
					this.particleQueues[j][k].removeFirst();
				}

				this.particleQueues[j][k].add(particle);
			}
		}
	}

	private void updateGroup(int i) {
		this.world.getProfiler().push(String.valueOf(i));

		for (int j = 0; j < 2; j++) {
			this.world.getProfiler().push(String.valueOf(j));
			this.updateParticleQueue(this.particleQueues[i][j]);
			this.world.getProfiler().pop();
		}

		this.world.getProfiler().pop();
	}

	private void updateParticleQueue(Queue<Particle> queue) {
		if (!queue.isEmpty()) {
			Iterator<Particle> iterator = queue.iterator();

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
		} catch (Throwable var6) {
			CrashReport crashReport = CrashReport.create(var6, "Ticking Particle");
			CrashReportSection crashReportSection = crashReport.addElement("Particle being ticked");
			int i = particle.getParticleGroup();
			crashReportSection.add("Particle", particle::toString);
			crashReportSection.add("Particle Type", (ICrashCallable<String>)(() -> {
				if (i == 0) {
					return "MISC_TEXTURE";
				} else if (i == 1) {
					return "TERRAIN_TEXTURE";
				} else {
					return i == 3 ? "ENTITY_PARTICLE_TEXTURE" : "Unknown - " + i;
				}
			}));
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
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GlStateManager.class_1033.SRC_ALPHA, GlStateManager.class_1027.ONE_MINUS_SRC_ALPHA);
		GlStateManager.alphaFunc(516, 0.003921569F);

		for (int l = 0; l < 3; l++) {
			for (int m = 0; m < 2; m++) {
				if (!this.particleQueues[l][m].isEmpty()) {
					switch (m) {
						case 0:
							GlStateManager.depthMask(false);
							break;
						case 1:
							GlStateManager.depthMask(true);
					}

					switch (l) {
						case 0:
						default:
							this.textureManager.bindTexture(PARTICLE_TEX);
							break;
						case 1:
							this.textureManager.bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
					}

					GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
					Tessellator tessellator = Tessellator.getInstance();
					BufferBuilder bufferBuilder = tessellator.getBufferBuilder();
					bufferBuilder.begin(7, VertexFormats.POSITION_UV_COLOR_LMAP);

					for (Particle particle : this.particleQueues[l][m]) {
						try {
							particle.buildGeometry(bufferBuilder, entity, f, g, k, h, i, j);
						} catch (Throwable var18) {
							CrashReport crashReport = CrashReport.create(var18, "Rendering Particle");
							CrashReportSection crashReportSection = crashReport.addElement("Particle being rendered");
							int n = l;
							crashReportSection.add("Particle", particle::toString);
							crashReportSection.add("Particle Type", (ICrashCallable<String>)(() -> {
								if (n == 0) {
									return "MISC_TEXTURE";
								} else if (n == 1) {
									return "TERRAIN_TEXTURE";
								} else {
									return n == 3 ? "ENTITY_PARTICLE_TEXTURE" : "Unknown - " + n;
								}
							}));
							throw new CrashException(crashReport);
						}
					}

					tessellator.draw();
				}
			}
		}

		GlStateManager.depthMask(true);
		GlStateManager.disableBlend();
		GlStateManager.alphaFunc(516, 0.1F);
	}

	public void renderLitParticles(Entity entity, float f) {
		float g = class_295.method_1375();
		float h = class_295.method_1380();
		float i = class_295.method_1381();
		float j = class_295.method_1378();
		float k = class_295.method_1377();
		Particle.cameraX = MathHelper.lerp((double)f, entity.prevRenderX, entity.x);
		Particle.cameraY = MathHelper.lerp((double)f, entity.prevRenderY, entity.y);
		Particle.cameraZ = MathHelper.lerp((double)f, entity.prevRenderZ, entity.z);
		Particle.cameraRotation = entity.getRotationVec(f);

		for (int l = 0; l < 2; l++) {
			Queue<Particle> queue = this.particleQueues[3][l];
			if (!queue.isEmpty()) {
				Tessellator tessellator = Tessellator.getInstance();
				BufferBuilder bufferBuilder = tessellator.getBufferBuilder();

				for (Particle particle : queue) {
					particle.buildGeometry(bufferBuilder, entity, f, g, k, h, i, j);
				}
			}
		}
	}

	public void setWorld(@Nullable World world) {
		this.world = world;

		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 2; j++) {
				this.particleQueues[i][j].clear();
			}
		}

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
		int i = 0;

		for (int j = 0; j < 4; j++) {
			for (int k = 0; k < 2; k++) {
				i += this.particleQueues[j][k].size();
			}
		}

		return String.valueOf(i);
	}
}
