package net.minecraft.client.render;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.platform.GlConst;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectFunction;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap.Entry;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.BrushableBlock;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.ComposterBlock;
import net.minecraft.block.MultifaceGrowthBlock;
import net.minecraft.block.PointedDripstoneBlock;
import net.minecraft.block.SculkShriekerBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.GlUniform;
import net.minecraft.client.gl.PostEffectProcessor;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.CloudRenderMode;
import net.minecraft.client.option.GraphicsMode;
import net.minecraft.client.option.ParticlesMode;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.chunk.ChunkBuilder;
import net.minecraft.client.render.chunk.ChunkRendererRegionBuilder;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.ParticleUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.BoneMealItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.MusicDiscItem;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.particle.SculkChargeParticleEffect;
import net.minecraft.particle.ShriekParticleEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SynchronousResourceReloader;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.crash.CrashCallable;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.BlockView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;
import net.minecraft.world.WorldView;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.light.LightingProvider;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3d;
import org.joml.Vector4f;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class WorldRenderer implements SynchronousResourceReloader, AutoCloseable {
	private static final Logger LOGGER = LogUtils.getLogger();
	public static final int field_32759 = 16;
	public static final int field_34812 = 8;
	private static final float field_32762 = 512.0F;
	private static final int field_32763 = 32;
	private static final int field_32764 = 10;
	private static final int field_32765 = 21;
	private static final int field_32766 = 15;
	private static final Identifier MOON_PHASES = new Identifier("textures/environment/moon_phases.png");
	private static final Identifier SUN = new Identifier("textures/environment/sun.png");
	private static final Identifier CLOUDS = new Identifier("textures/environment/clouds.png");
	private static final Identifier END_SKY = new Identifier("textures/environment/end_sky.png");
	private static final Identifier FORCEFIELD = new Identifier("textures/misc/forcefield.png");
	private static final Identifier RAIN = new Identifier("textures/environment/rain.png");
	private static final Identifier SNOW = new Identifier("textures/environment/snow.png");
	public static final Direction[] DIRECTIONS = Direction.values();
	private final MinecraftClient client;
	private final EntityRenderDispatcher entityRenderDispatcher;
	private final BlockEntityRenderDispatcher blockEntityRenderDispatcher;
	private final BufferBuilderStorage bufferBuilders;
	@Nullable
	private ClientWorld world;
	private final ChunkRenderingDataPreparer field_45615 = new ChunkRenderingDataPreparer();
	private final ObjectArrayList<ChunkBuilder.BuiltChunk> field_45616 = new ObjectArrayList<>(10000);
	private final Set<BlockEntity> noCullingBlockEntities = Sets.<BlockEntity>newHashSet();
	@Nullable
	private BuiltChunkStorage chunks;
	@Nullable
	private VertexBuffer starsBuffer;
	@Nullable
	private VertexBuffer lightSkyBuffer;
	@Nullable
	private VertexBuffer darkSkyBuffer;
	private boolean cloudsDirty = true;
	@Nullable
	private VertexBuffer cloudsBuffer;
	private final FpsSmoother chunkUpdateSmoother = new FpsSmoother(100);
	private int ticks;
	private final Int2ObjectMap<BlockBreakingInfo> blockBreakingInfos = new Int2ObjectOpenHashMap<>();
	private final Long2ObjectMap<SortedSet<BlockBreakingInfo>> blockBreakingProgressions = new Long2ObjectOpenHashMap<>();
	private final Map<BlockPos, SoundInstance> playingSongs = Maps.<BlockPos, SoundInstance>newHashMap();
	@Nullable
	private Framebuffer entityOutlinesFramebuffer;
	@Nullable
	private PostEffectProcessor entityOutlinePostProcessor;
	@Nullable
	private Framebuffer translucentFramebuffer;
	@Nullable
	private Framebuffer entityFramebuffer;
	@Nullable
	private Framebuffer particlesFramebuffer;
	@Nullable
	private Framebuffer weatherFramebuffer;
	@Nullable
	private Framebuffer cloudsFramebuffer;
	@Nullable
	private PostEffectProcessor transparencyPostProcessor;
	private int cameraChunkX = Integer.MIN_VALUE;
	private int cameraChunkY = Integer.MIN_VALUE;
	private int cameraChunkZ = Integer.MIN_VALUE;
	private double lastCameraX = Double.MIN_VALUE;
	private double lastCameraY = Double.MIN_VALUE;
	private double lastCameraZ = Double.MIN_VALUE;
	private double lastCameraPitch = Double.MIN_VALUE;
	private double lastCameraYaw = Double.MIN_VALUE;
	private int lastCloudsBlockX = Integer.MIN_VALUE;
	private int lastCloudsBlockY = Integer.MIN_VALUE;
	private int lastCloudsBlockZ = Integer.MIN_VALUE;
	private Vec3d lastCloudsColor = Vec3d.ZERO;
	@Nullable
	private CloudRenderMode lastCloudRenderMode;
	@Nullable
	private ChunkBuilder field_45614;
	private int viewDistance = -1;
	private int regularEntityCount;
	private int blockEntityCount;
	private Frustum frustum;
	private boolean shouldCaptureFrustum;
	@Nullable
	private Frustum capturedFrustum;
	private final Vector4f[] capturedFrustumOrientation = new Vector4f[8];
	private final Vector3d capturedFrustumPosition = new Vector3d(0.0, 0.0, 0.0);
	private double lastTranslucentSortX;
	private double lastTranslucentSortY;
	private double lastTranslucentSortZ;
	private int rainSoundCounter;
	/**
	 * Given {@code -16 <= z < 16} and {@code -16 <= x < 16}, let {@code i = 32 * (z + 16) + (x + 16)}.
	 * Then {@code NORMAL_LINE_DX[i]} and {@code NORMAL_LINE_DZ[i]} describe the
	 * unit vector perpendicular to {@code (x, z)}.
	 * 
	 * These lookup tables are used for rendering rain and snow.
	 */
	private final float[] NORMAL_LINE_DX = new float[1024];
	private final float[] NORMAL_LINE_DZ = new float[1024];

	public WorldRenderer(
		MinecraftClient client,
		EntityRenderDispatcher entityRenderDispatcher,
		BlockEntityRenderDispatcher blockEntityRenderDispatcher,
		BufferBuilderStorage bufferBuilders
	) {
		this.client = client;
		this.entityRenderDispatcher = entityRenderDispatcher;
		this.blockEntityRenderDispatcher = blockEntityRenderDispatcher;
		this.bufferBuilders = bufferBuilders;

		for (int i = 0; i < 32; i++) {
			for (int j = 0; j < 32; j++) {
				float f = (float)(j - 16);
				float g = (float)(i - 16);
				float h = MathHelper.sqrt(f * f + g * g);
				this.NORMAL_LINE_DX[i << 5 | j] = -g / h;
				this.NORMAL_LINE_DZ[i << 5 | j] = f / h;
			}
		}

		this.renderStars();
		this.renderLightSky();
		this.renderDarkSky();
	}

	private void renderWeather(LightmapTextureManager manager, float tickDelta, double cameraX, double cameraY, double cameraZ) {
		float f = this.client.world.getRainGradient(tickDelta);
		if (!(f <= 0.0F)) {
			manager.enable();
			World world = this.client.world;
			int i = MathHelper.floor(cameraX);
			int j = MathHelper.floor(cameraY);
			int k = MathHelper.floor(cameraZ);
			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder bufferBuilder = tessellator.getBuffer();
			RenderSystem.disableCull();
			RenderSystem.enableBlend();
			RenderSystem.enableDepthTest();
			int l = 5;
			if (MinecraftClient.isFancyGraphicsOrBetter()) {
				l = 10;
			}

			RenderSystem.depthMask(MinecraftClient.isFabulousGraphicsOrBetter());
			int m = -1;
			float g = (float)this.ticks + tickDelta;
			RenderSystem.setShader(GameRenderer::getParticleProgram);
			BlockPos.Mutable mutable = new BlockPos.Mutable();

			for (int n = k - l; n <= k + l; n++) {
				for (int o = i - l; o <= i + l; o++) {
					int p = (n - k + 16) * 32 + o - i + 16;
					double d = (double)this.NORMAL_LINE_DX[p] * 0.5;
					double e = (double)this.NORMAL_LINE_DZ[p] * 0.5;
					mutable.set((double)o, cameraY, (double)n);
					Biome biome = world.getBiome(mutable).value();
					if (biome.hasPrecipitation()) {
						int q = world.getTopY(Heightmap.Type.MOTION_BLOCKING, o, n);
						int r = j - l;
						int s = j + l;
						if (r < q) {
							r = q;
						}

						if (s < q) {
							s = q;
						}

						int t = q;
						if (q < j) {
							t = j;
						}

						if (r != s) {
							Random random = Random.create((long)(o * o * 3121 + o * 45238971 ^ n * n * 418711 + n * 13761));
							mutable.set(o, r, n);
							Biome.Precipitation precipitation = biome.getPrecipitation(mutable);
							if (precipitation == Biome.Precipitation.RAIN) {
								if (m != 0) {
									if (m >= 0) {
										tessellator.draw();
									}

									m = 0;
									RenderSystem.setShaderTexture(0, RAIN);
									bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR_LIGHT);
								}

								int u = this.ticks + o * o * 3121 + o * 45238971 + n * n * 418711 + n * 13761 & 31;
								float h = -((float)u + tickDelta) / 32.0F * (3.0F + random.nextFloat());
								double v = (double)o + 0.5 - cameraX;
								double w = (double)n + 0.5 - cameraZ;
								float x = (float)Math.sqrt(v * v + w * w) / (float)l;
								float y = ((1.0F - x * x) * 0.5F + 0.5F) * f;
								mutable.set(o, t, n);
								int z = getLightmapCoordinates(world, mutable);
								bufferBuilder.vertex((double)o - cameraX - d + 0.5, (double)s - cameraY, (double)n - cameraZ - e + 0.5)
									.texture(0.0F, (float)r * 0.25F + h)
									.color(1.0F, 1.0F, 1.0F, y)
									.light(z)
									.next();
								bufferBuilder.vertex((double)o - cameraX + d + 0.5, (double)s - cameraY, (double)n - cameraZ + e + 0.5)
									.texture(1.0F, (float)r * 0.25F + h)
									.color(1.0F, 1.0F, 1.0F, y)
									.light(z)
									.next();
								bufferBuilder.vertex((double)o - cameraX + d + 0.5, (double)r - cameraY, (double)n - cameraZ + e + 0.5)
									.texture(1.0F, (float)s * 0.25F + h)
									.color(1.0F, 1.0F, 1.0F, y)
									.light(z)
									.next();
								bufferBuilder.vertex((double)o - cameraX - d + 0.5, (double)r - cameraY, (double)n - cameraZ - e + 0.5)
									.texture(0.0F, (float)s * 0.25F + h)
									.color(1.0F, 1.0F, 1.0F, y)
									.light(z)
									.next();
							} else if (precipitation == Biome.Precipitation.SNOW) {
								if (m != 1) {
									if (m >= 0) {
										tessellator.draw();
									}

									m = 1;
									RenderSystem.setShaderTexture(0, SNOW);
									bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR_LIGHT);
								}

								float aa = -((float)(this.ticks & 511) + tickDelta) / 512.0F;
								float h = (float)(random.nextDouble() + (double)g * 0.01 * (double)((float)random.nextGaussian()));
								float ab = (float)(random.nextDouble() + (double)(g * (float)random.nextGaussian()) * 0.001);
								double ac = (double)o + 0.5 - cameraX;
								double ad = (double)n + 0.5 - cameraZ;
								float y = (float)Math.sqrt(ac * ac + ad * ad) / (float)l;
								float ae = ((1.0F - y * y) * 0.3F + 0.5F) * f;
								mutable.set(o, t, n);
								int af = getLightmapCoordinates(world, mutable);
								int ag = af >> 16 & 65535;
								int ah = af & 65535;
								int ai = (ag * 3 + 240) / 4;
								int aj = (ah * 3 + 240) / 4;
								bufferBuilder.vertex((double)o - cameraX - d + 0.5, (double)s - cameraY, (double)n - cameraZ - e + 0.5)
									.texture(0.0F + h, (float)r * 0.25F + aa + ab)
									.color(1.0F, 1.0F, 1.0F, ae)
									.light(aj, ai)
									.next();
								bufferBuilder.vertex((double)o - cameraX + d + 0.5, (double)s - cameraY, (double)n - cameraZ + e + 0.5)
									.texture(1.0F + h, (float)r * 0.25F + aa + ab)
									.color(1.0F, 1.0F, 1.0F, ae)
									.light(aj, ai)
									.next();
								bufferBuilder.vertex((double)o - cameraX + d + 0.5, (double)r - cameraY, (double)n - cameraZ + e + 0.5)
									.texture(1.0F + h, (float)s * 0.25F + aa + ab)
									.color(1.0F, 1.0F, 1.0F, ae)
									.light(aj, ai)
									.next();
								bufferBuilder.vertex((double)o - cameraX - d + 0.5, (double)r - cameraY, (double)n - cameraZ - e + 0.5)
									.texture(0.0F + h, (float)s * 0.25F + aa + ab)
									.color(1.0F, 1.0F, 1.0F, ae)
									.light(aj, ai)
									.next();
							}
						}
					}
				}
			}

			if (m >= 0) {
				tessellator.draw();
			}

			RenderSystem.enableCull();
			RenderSystem.disableBlend();
			manager.disable();
		}
	}

	public void tickRainSplashing(Camera camera) {
		float f = this.client.world.getRainGradient(1.0F) / (MinecraftClient.isFancyGraphicsOrBetter() ? 1.0F : 2.0F);
		if (!(f <= 0.0F)) {
			Random random = Random.create((long)this.ticks * 312987231L);
			WorldView worldView = this.client.world;
			BlockPos blockPos = BlockPos.ofFloored(camera.getPos());
			BlockPos blockPos2 = null;
			int i = (int)(100.0F * f * f) / (this.client.options.getParticles().getValue() == ParticlesMode.DECREASED ? 2 : 1);

			for (int j = 0; j < i; j++) {
				int k = random.nextInt(21) - 10;
				int l = random.nextInt(21) - 10;
				BlockPos blockPos3 = worldView.getTopPosition(Heightmap.Type.MOTION_BLOCKING, blockPos.add(k, 0, l));
				if (blockPos3.getY() > worldView.getBottomY() && blockPos3.getY() <= blockPos.getY() + 10 && blockPos3.getY() >= blockPos.getY() - 10) {
					Biome biome = worldView.getBiome(blockPos3).value();
					if (biome.getPrecipitation(blockPos3) == Biome.Precipitation.RAIN) {
						blockPos2 = blockPos3.down();
						if (this.client.options.getParticles().getValue() == ParticlesMode.MINIMAL) {
							break;
						}

						double d = random.nextDouble();
						double e = random.nextDouble();
						BlockState blockState = worldView.getBlockState(blockPos2);
						FluidState fluidState = worldView.getFluidState(blockPos2);
						VoxelShape voxelShape = blockState.getCollisionShape(worldView, blockPos2);
						double g = voxelShape.getEndingCoord(Direction.Axis.Y, d, e);
						double h = (double)fluidState.getHeight(worldView, blockPos2);
						double m = Math.max(g, h);
						ParticleEffect particleEffect = !fluidState.isIn(FluidTags.LAVA) && !blockState.isOf(Blocks.MAGMA_BLOCK) && !CampfireBlock.isLitCampfire(blockState)
							? ParticleTypes.RAIN
							: ParticleTypes.SMOKE;
						this.client.world.addParticle(particleEffect, (double)blockPos2.getX() + d, (double)blockPos2.getY() + m, (double)blockPos2.getZ() + e, 0.0, 0.0, 0.0);
					}
				}
			}

			if (blockPos2 != null && random.nextInt(3) < this.rainSoundCounter++) {
				this.rainSoundCounter = 0;
				if (blockPos2.getY() > blockPos.getY() + 1
					&& worldView.getTopPosition(Heightmap.Type.MOTION_BLOCKING, blockPos).getY() > MathHelper.floor((float)blockPos.getY())) {
					this.client.world.playSoundAtBlockCenter(blockPos2, SoundEvents.WEATHER_RAIN_ABOVE, SoundCategory.WEATHER, 0.1F, 0.5F, false);
				} else {
					this.client.world.playSoundAtBlockCenter(blockPos2, SoundEvents.WEATHER_RAIN, SoundCategory.WEATHER, 0.2F, 1.0F, false);
				}
			}
		}
	}

	public void close() {
		if (this.entityOutlinePostProcessor != null) {
			this.entityOutlinePostProcessor.close();
		}

		if (this.transparencyPostProcessor != null) {
			this.transparencyPostProcessor.close();
		}
	}

	@Override
	public void reload(ResourceManager manager) {
		this.loadEntityOutlinePostProcessor();
		if (MinecraftClient.isFabulousGraphicsOrBetter()) {
			this.loadTransparencyPostProcessor();
		}
	}

	public void loadEntityOutlinePostProcessor() {
		if (this.entityOutlinePostProcessor != null) {
			this.entityOutlinePostProcessor.close();
		}

		Identifier identifier = new Identifier("shaders/post/entity_outline.json");

		try {
			this.entityOutlinePostProcessor = new PostEffectProcessor(
				this.client.getTextureManager(), this.client.getResourceManager(), this.client.getFramebuffer(), identifier
			);
			this.entityOutlinePostProcessor.setupDimensions(this.client.getWindow().getFramebufferWidth(), this.client.getWindow().getFramebufferHeight());
			this.entityOutlinesFramebuffer = this.entityOutlinePostProcessor.getSecondaryTarget("final");
		} catch (IOException var3) {
			LOGGER.warn("Failed to load shader: {}", identifier, var3);
			this.entityOutlinePostProcessor = null;
			this.entityOutlinesFramebuffer = null;
		} catch (JsonSyntaxException var4) {
			LOGGER.warn("Failed to parse shader: {}", identifier, var4);
			this.entityOutlinePostProcessor = null;
			this.entityOutlinesFramebuffer = null;
		}
	}

	private void loadTransparencyPostProcessor() {
		this.resetTransparencyPostProcessor();
		Identifier identifier = new Identifier("shaders/post/transparency.json");

		try {
			PostEffectProcessor postEffectProcessor = new PostEffectProcessor(
				this.client.getTextureManager(), this.client.getResourceManager(), this.client.getFramebuffer(), identifier
			);
			postEffectProcessor.setupDimensions(this.client.getWindow().getFramebufferWidth(), this.client.getWindow().getFramebufferHeight());
			Framebuffer framebuffer = postEffectProcessor.getSecondaryTarget("translucent");
			Framebuffer framebuffer2 = postEffectProcessor.getSecondaryTarget("itemEntity");
			Framebuffer framebuffer3 = postEffectProcessor.getSecondaryTarget("particles");
			Framebuffer framebuffer4 = postEffectProcessor.getSecondaryTarget("weather");
			Framebuffer framebuffer5 = postEffectProcessor.getSecondaryTarget("clouds");
			this.transparencyPostProcessor = postEffectProcessor;
			this.translucentFramebuffer = framebuffer;
			this.entityFramebuffer = framebuffer2;
			this.particlesFramebuffer = framebuffer3;
			this.weatherFramebuffer = framebuffer4;
			this.cloudsFramebuffer = framebuffer5;
		} catch (Exception var8) {
			String string = var8 instanceof JsonSyntaxException ? "parse" : "load";
			String string2 = "Failed to " + string + " shader: " + identifier;
			WorldRenderer.ProgramInitException programInitException = new WorldRenderer.ProgramInitException(string2, var8);
			if (this.client.getResourcePackManager().getEnabledNames().size() > 1) {
				Text text = (Text)this.client.getResourceManager().streamResourcePacks().findFirst().map(resourcePack -> Text.literal(resourcePack.getName())).orElse(null);
				this.client.options.getGraphicsMode().setValue(GraphicsMode.FANCY);
				this.client.onResourceReloadFailure(programInitException, text);
			} else {
				CrashReport crashReport = this.client.addDetailsToCrashReport(new CrashReport(string2, programInitException));
				this.client.options.getGraphicsMode().setValue(GraphicsMode.FANCY);
				this.client.options.write();
				LOGGER.error(LogUtils.FATAL_MARKER, string2, (Throwable)programInitException);
				this.client.cleanUpAfterCrash();
				MinecraftClient.printCrashReport(crashReport);
			}
		}
	}

	private void resetTransparencyPostProcessor() {
		if (this.transparencyPostProcessor != null) {
			this.transparencyPostProcessor.close();
			this.translucentFramebuffer.delete();
			this.entityFramebuffer.delete();
			this.particlesFramebuffer.delete();
			this.weatherFramebuffer.delete();
			this.cloudsFramebuffer.delete();
			this.transparencyPostProcessor = null;
			this.translucentFramebuffer = null;
			this.entityFramebuffer = null;
			this.particlesFramebuffer = null;
			this.weatherFramebuffer = null;
			this.cloudsFramebuffer = null;
		}
	}

	public void drawEntityOutlinesFramebuffer() {
		if (this.canDrawEntityOutlines()) {
			RenderSystem.enableBlend();
			RenderSystem.blendFuncSeparate(
				GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SrcFactor.ZERO, GlStateManager.DstFactor.ONE
			);
			this.entityOutlinesFramebuffer.draw(this.client.getWindow().getFramebufferWidth(), this.client.getWindow().getFramebufferHeight(), false);
			RenderSystem.disableBlend();
			RenderSystem.defaultBlendFunc();
		}
	}

	protected boolean canDrawEntityOutlines() {
		return !this.client.gameRenderer.isRenderingPanorama()
			&& this.entityOutlinesFramebuffer != null
			&& this.entityOutlinePostProcessor != null
			&& this.client.player != null;
	}

	private void renderDarkSky() {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		if (this.darkSkyBuffer != null) {
			this.darkSkyBuffer.close();
		}

		this.darkSkyBuffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
		BufferBuilder.BuiltBuffer builtBuffer = renderSky(bufferBuilder, -16.0F);
		this.darkSkyBuffer.bind();
		this.darkSkyBuffer.upload(builtBuffer);
		VertexBuffer.unbind();
	}

	private void renderLightSky() {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		if (this.lightSkyBuffer != null) {
			this.lightSkyBuffer.close();
		}

		this.lightSkyBuffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
		BufferBuilder.BuiltBuffer builtBuffer = renderSky(bufferBuilder, 16.0F);
		this.lightSkyBuffer.bind();
		this.lightSkyBuffer.upload(builtBuffer);
		VertexBuffer.unbind();
	}

	private static BufferBuilder.BuiltBuffer renderSky(BufferBuilder builder, float f) {
		float g = Math.signum(f) * 512.0F;
		float h = 512.0F;
		RenderSystem.setShader(GameRenderer::getPositionProgram);
		builder.begin(VertexFormat.DrawMode.TRIANGLE_FAN, VertexFormats.POSITION);
		builder.vertex(0.0, (double)f, 0.0).next();

		for (int i = -180; i <= 180; i += 45) {
			builder.vertex(
					(double)(g * MathHelper.cos((float)i * (float) (Math.PI / 180.0))), (double)f, (double)(512.0F * MathHelper.sin((float)i * (float) (Math.PI / 180.0)))
				)
				.next();
		}

		return builder.end();
	}

	private void renderStars() {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		RenderSystem.setShader(GameRenderer::getPositionProgram);
		if (this.starsBuffer != null) {
			this.starsBuffer.close();
		}

		this.starsBuffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
		BufferBuilder.BuiltBuffer builtBuffer = this.renderStars(bufferBuilder);
		this.starsBuffer.bind();
		this.starsBuffer.upload(builtBuffer);
		VertexBuffer.unbind();
	}

	private BufferBuilder.BuiltBuffer renderStars(BufferBuilder buffer) {
		Random random = Random.create(10842L);
		buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION);

		for (int i = 0; i < 1500; i++) {
			double d = (double)(random.nextFloat() * 2.0F - 1.0F);
			double e = (double)(random.nextFloat() * 2.0F - 1.0F);
			double f = (double)(random.nextFloat() * 2.0F - 1.0F);
			double g = (double)(0.15F + random.nextFloat() * 0.1F);
			double h = d * d + e * e + f * f;
			if (h < 1.0 && h > 0.01) {
				h = 1.0 / Math.sqrt(h);
				d *= h;
				e *= h;
				f *= h;
				double j = d * 100.0;
				double k = e * 100.0;
				double l = f * 100.0;
				double m = Math.atan2(d, f);
				double n = Math.sin(m);
				double o = Math.cos(m);
				double p = Math.atan2(Math.sqrt(d * d + f * f), e);
				double q = Math.sin(p);
				double r = Math.cos(p);
				double s = random.nextDouble() * Math.PI * 2.0;
				double t = Math.sin(s);
				double u = Math.cos(s);

				for (int v = 0; v < 4; v++) {
					double w = 0.0;
					double x = (double)((v & 2) - 1) * g;
					double y = (double)((v + 1 & 2) - 1) * g;
					double z = 0.0;
					double aa = x * u - y * t;
					double ab = y * u + x * t;
					double ad = aa * q + 0.0 * r;
					double ae = 0.0 * q - aa * r;
					double af = ae * n - ab * o;
					double ah = ab * n + ae * o;
					buffer.vertex(j + af, k + ad, l + ah).next();
				}
			}
		}

		return buffer.end();
	}

	public void setWorld(@Nullable ClientWorld world) {
		this.cameraChunkX = Integer.MIN_VALUE;
		this.cameraChunkY = Integer.MIN_VALUE;
		this.cameraChunkZ = Integer.MIN_VALUE;
		this.entityRenderDispatcher.setWorld(world);
		this.world = world;
		if (world != null) {
			this.reload();
		} else {
			if (this.chunks != null) {
				this.chunks.clear();
				this.chunks = null;
			}

			if (this.field_45614 != null) {
				this.field_45614.stop();
			}

			this.field_45614 = null;
			this.noCullingBlockEntities.clear();
			this.field_45615.method_52826(null);
			this.field_45616.clear();
		}
	}

	public void reloadTransparencyPostProcessor() {
		if (MinecraftClient.isFabulousGraphicsOrBetter()) {
			this.loadTransparencyPostProcessor();
		} else {
			this.resetTransparencyPostProcessor();
		}
	}

	public void reload() {
		if (this.world != null) {
			this.reloadTransparencyPostProcessor();
			this.world.reloadColor();
			if (this.field_45614 == null) {
				this.field_45614 = new ChunkBuilder(this.world, this, Util.getMainWorkerExecutor(), this.client.is64Bit(), this.bufferBuilders.getBlockBufferBuilders());
			} else {
				this.field_45614.setWorld(this.world);
			}

			this.cloudsDirty = true;
			RenderLayers.setFancyGraphicsOrBetter(MinecraftClient.isFancyGraphicsOrBetter());
			this.viewDistance = this.client.options.getClampedViewDistance();
			if (this.chunks != null) {
				this.chunks.clear();
			}

			this.field_45614.reset();
			synchronized (this.noCullingBlockEntities) {
				this.noCullingBlockEntities.clear();
			}

			this.chunks = new BuiltChunkStorage(this.field_45614, this.world, this.client.options.getClampedViewDistance(), this);
			this.field_45615.method_52826(this.chunks);
			this.field_45616.clear();
			Entity entity = this.client.getCameraEntity();
			if (entity != null) {
				this.chunks.updateCameraPosition(entity.getX(), entity.getZ());
			}
		}
	}

	public void onResized(int width, int height) {
		this.scheduleTerrainUpdate();
		if (this.entityOutlinePostProcessor != null) {
			this.entityOutlinePostProcessor.setupDimensions(width, height);
		}

		if (this.transparencyPostProcessor != null) {
			this.transparencyPostProcessor.setupDimensions(width, height);
		}
	}

	public String getChunksDebugString() {
		int i = this.chunks.chunks.length;
		int j = this.getCompletedChunkCount();
		return String.format(
			Locale.ROOT,
			"C: %d/%d %sD: %d, %s",
			j,
			i,
			this.client.chunkCullingEnabled ? "(s) " : "",
			this.viewDistance,
			this.field_45614 == null ? "null" : this.field_45614.getDebugString()
		);
	}

	public ChunkBuilder getChunkBuilder() {
		return this.field_45614;
	}

	public double getChunkCount() {
		return (double)this.chunks.chunks.length;
	}

	public double getViewDistance() {
		return (double)this.viewDistance;
	}

	public int getCompletedChunkCount() {
		int i = 0;

		for (ChunkBuilder.BuiltChunk builtChunk : this.field_45616) {
			if (!builtChunk.getData().isEmpty()) {
				i++;
			}
		}

		return i;
	}

	public String getEntitiesDebugString() {
		return "E: "
			+ this.regularEntityCount
			+ "/"
			+ this.world.getRegularEntityCount()
			+ ", B: "
			+ this.blockEntityCount
			+ ", SD: "
			+ this.world.getSimulationDistance();
	}

	private void setupTerrain(Camera camera, Frustum frustum, boolean hasForcedFrustum, boolean spectator) {
		Vec3d vec3d = camera.getPos();
		if (this.client.options.getClampedViewDistance() != this.viewDistance) {
			this.reload();
		}

		this.world.getProfiler().push("camera");
		double d = this.client.player.getX();
		double e = this.client.player.getY();
		double f = this.client.player.getZ();
		int i = ChunkSectionPos.getSectionCoord(d);
		int j = ChunkSectionPos.getSectionCoord(e);
		int k = ChunkSectionPos.getSectionCoord(f);
		if (this.cameraChunkX != i || this.cameraChunkY != j || this.cameraChunkZ != k) {
			this.cameraChunkX = i;
			this.cameraChunkY = j;
			this.cameraChunkZ = k;
			this.chunks.updateCameraPosition(d, f);
		}

		this.field_45614.setCameraPosition(vec3d);
		this.world.getProfiler().swap("cull");
		this.client.getProfiler().swap("culling");
		BlockPos blockPos = camera.getBlockPos();
		double g = Math.floor(vec3d.x / 8.0);
		double h = Math.floor(vec3d.y / 8.0);
		double l = Math.floor(vec3d.z / 8.0);
		if (g != this.lastCameraX || h != this.lastCameraY || l != this.lastCameraZ) {
			this.field_45615.method_52817();
		}

		this.lastCameraX = g;
		this.lastCameraY = h;
		this.lastCameraZ = l;
		this.client.getProfiler().swap("update");
		if (!hasForcedFrustum) {
			boolean bl = this.client.chunkCullingEnabled;
			if (spectator && this.world.getBlockState(blockPos).isOpaqueFullCube(this.world, blockPos)) {
				bl = false;
			}

			Entity.setRenderDistanceMultiplier(
				MathHelper.clamp((double)this.client.options.getClampedViewDistance() / 8.0, 1.0, 2.5) * this.client.options.getEntityDistanceScaling().getValue()
			);
			this.client.getProfiler().push("section_occlusion_graph");
			this.field_45615.method_52834(bl, camera, frustum, this.field_45616);
			this.client.getProfiler().pop();
			double m = Math.floor((double)(camera.getPitch() / 2.0F));
			double n = Math.floor((double)(camera.getYaw() / 2.0F));
			if (this.field_45615.method_52836() || m != this.lastCameraPitch || n != this.lastCameraYaw) {
				this.applyFrustum(method_52816(frustum));
				this.lastCameraPitch = m;
				this.lastCameraYaw = n;
			}
		}

		this.client.getProfiler().pop();
	}

	public static Frustum method_52816(Frustum frustum) {
		return new Frustum(frustum).coverBoxAroundSetPosition(8);
	}

	private void applyFrustum(Frustum frustum) {
		if (!MinecraftClient.getInstance().isOnThread()) {
			throw new IllegalStateException("applyFrustum called from wrong thread: " + Thread.currentThread().getName());
		} else {
			this.client.getProfiler().push("apply_frustum");
			this.field_45616.clear();
			this.field_45615.method_52828(frustum, this.field_45616);
			this.client.getProfiler().pop();
		}
	}

	public void addBuiltChunk(ChunkBuilder.BuiltChunk chunk) {
		this.field_45615.method_52827(chunk);
	}

	private void captureFrustum(Matrix4f positionMatrix, Matrix4f projectionMatrix, double x, double y, double z, Frustum frustum) {
		this.capturedFrustum = frustum;
		Matrix4f matrix4f = new Matrix4f(projectionMatrix);
		matrix4f.mul(positionMatrix);
		matrix4f.invert();
		this.capturedFrustumPosition.x = x;
		this.capturedFrustumPosition.y = y;
		this.capturedFrustumPosition.z = z;
		this.capturedFrustumOrientation[0] = new Vector4f(-1.0F, -1.0F, -1.0F, 1.0F);
		this.capturedFrustumOrientation[1] = new Vector4f(1.0F, -1.0F, -1.0F, 1.0F);
		this.capturedFrustumOrientation[2] = new Vector4f(1.0F, 1.0F, -1.0F, 1.0F);
		this.capturedFrustumOrientation[3] = new Vector4f(-1.0F, 1.0F, -1.0F, 1.0F);
		this.capturedFrustumOrientation[4] = new Vector4f(-1.0F, -1.0F, 1.0F, 1.0F);
		this.capturedFrustumOrientation[5] = new Vector4f(1.0F, -1.0F, 1.0F, 1.0F);
		this.capturedFrustumOrientation[6] = new Vector4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.capturedFrustumOrientation[7] = new Vector4f(-1.0F, 1.0F, 1.0F, 1.0F);

		for (int i = 0; i < 8; i++) {
			matrix4f.transform(this.capturedFrustumOrientation[i]);
			this.capturedFrustumOrientation[i].div(this.capturedFrustumOrientation[i].w());
		}
	}

	public void setupFrustum(MatrixStack matrices, Vec3d pos, Matrix4f projectionMatrix) {
		Matrix4f matrix4f = matrices.peek().getPositionMatrix();
		double d = pos.getX();
		double e = pos.getY();
		double f = pos.getZ();
		this.frustum = new Frustum(matrix4f, projectionMatrix);
		this.frustum.setPosition(d, e, f);
	}

	public void render(
		MatrixStack matrices,
		float tickDelta,
		long limitTime,
		boolean renderBlockOutline,
		Camera camera,
		GameRenderer gameRenderer,
		LightmapTextureManager lightmapTextureManager,
		Matrix4f projectionMatrix
	) {
		RenderSystem.setShaderGameTime(this.world.getTime(), tickDelta);
		this.blockEntityRenderDispatcher.configure(this.world, camera, this.client.crosshairTarget);
		this.entityRenderDispatcher.configure(this.world, camera, this.client.targetedEntity);
		Profiler profiler = this.world.getProfiler();
		profiler.swap("light_update_queue");
		this.world.runQueuedChunkUpdates();
		profiler.swap("light_updates");
		this.world.getChunkManager().getLightingProvider().doLightUpdates();
		Vec3d vec3d = camera.getPos();
		double d = vec3d.getX();
		double e = vec3d.getY();
		double f = vec3d.getZ();
		Matrix4f matrix4f = matrices.peek().getPositionMatrix();
		profiler.swap("culling");
		boolean bl = this.capturedFrustum != null;
		Frustum frustum;
		if (bl) {
			frustum = this.capturedFrustum;
			frustum.setPosition(this.capturedFrustumPosition.x, this.capturedFrustumPosition.y, this.capturedFrustumPosition.z);
		} else {
			frustum = this.frustum;
		}

		this.client.getProfiler().swap("captureFrustum");
		if (this.shouldCaptureFrustum) {
			this.captureFrustum(matrix4f, projectionMatrix, vec3d.x, vec3d.y, vec3d.z, bl ? new Frustum(matrix4f, projectionMatrix) : frustum);
			this.shouldCaptureFrustum = false;
		}

		profiler.swap("clear");
		BackgroundRenderer.render(camera, tickDelta, this.client.world, this.client.options.getClampedViewDistance(), gameRenderer.getSkyDarkness(tickDelta));
		BackgroundRenderer.setFogBlack();
		RenderSystem.clear(GlConst.GL_DEPTH_BUFFER_BIT | GlConst.GL_COLOR_BUFFER_BIT, MinecraftClient.IS_SYSTEM_MAC);
		float g = gameRenderer.getViewDistance();
		boolean bl2 = this.client.world.getDimensionEffects().useThickFog(MathHelper.floor(d), MathHelper.floor(e))
			|| this.client.inGameHud.getBossBarHud().shouldThickenFog();
		profiler.swap("sky");
		RenderSystem.setShader(GameRenderer::getPositionProgram);
		this.renderSky(
			matrices, projectionMatrix, tickDelta, camera, bl2, () -> BackgroundRenderer.applyFog(camera, BackgroundRenderer.FogType.FOG_SKY, g, bl2, tickDelta)
		);
		profiler.swap("fog");
		BackgroundRenderer.applyFog(camera, BackgroundRenderer.FogType.FOG_TERRAIN, Math.max(g, 32.0F), bl2, tickDelta);
		profiler.swap("terrain_setup");
		this.setupTerrain(camera, frustum, bl, this.client.player.isSpectator());
		profiler.swap("compile_sections");
		this.updateChunks(camera);
		profiler.swap("terrain");
		this.renderLayer(RenderLayer.getSolid(), matrices, d, e, f, projectionMatrix);
		this.renderLayer(RenderLayer.getCutoutMipped(), matrices, d, e, f, projectionMatrix);
		this.renderLayer(RenderLayer.getCutout(), matrices, d, e, f, projectionMatrix);
		if (this.world.getDimensionEffects().isDarkened()) {
			DiffuseLighting.enableForLevel(matrices.peek().getPositionMatrix());
		} else {
			DiffuseLighting.disableForLevel(matrices.peek().getPositionMatrix());
		}

		profiler.swap("entities");
		this.regularEntityCount = 0;
		this.blockEntityCount = 0;
		if (this.entityFramebuffer != null) {
			this.entityFramebuffer.clear(MinecraftClient.IS_SYSTEM_MAC);
			this.entityFramebuffer.copyDepthFrom(this.client.getFramebuffer());
			this.client.getFramebuffer().beginWrite(false);
		}

		if (this.weatherFramebuffer != null) {
			this.weatherFramebuffer.clear(MinecraftClient.IS_SYSTEM_MAC);
		}

		if (this.canDrawEntityOutlines()) {
			this.entityOutlinesFramebuffer.clear(MinecraftClient.IS_SYSTEM_MAC);
			this.client.getFramebuffer().beginWrite(false);
		}

		boolean bl3 = false;
		VertexConsumerProvider.Immediate immediate = this.bufferBuilders.getEntityVertexConsumers();

		for (Entity entity : this.world.getEntities()) {
			if (this.entityRenderDispatcher.shouldRender(entity, frustum, d, e, f) || entity.hasPassengerDeep(this.client.player)) {
				BlockPos blockPos = entity.getBlockPos();
				if ((this.world.isOutOfHeightLimit(blockPos.getY()) || this.isRenderingReady(blockPos))
					&& (
						entity != camera.getFocusedEntity()
							|| camera.isThirdPerson()
							|| camera.getFocusedEntity() instanceof LivingEntity && ((LivingEntity)camera.getFocusedEntity()).isSleeping()
					)
					&& (!(entity instanceof ClientPlayerEntity) || camera.getFocusedEntity() == entity)) {
					this.regularEntityCount++;
					if (entity.age == 0) {
						entity.lastRenderX = entity.getX();
						entity.lastRenderY = entity.getY();
						entity.lastRenderZ = entity.getZ();
					}

					VertexConsumerProvider vertexConsumerProvider;
					if (this.canDrawEntityOutlines() && this.client.hasOutline(entity)) {
						bl3 = true;
						OutlineVertexConsumerProvider outlineVertexConsumerProvider = this.bufferBuilders.getOutlineVertexConsumers();
						vertexConsumerProvider = outlineVertexConsumerProvider;
						int i = entity.getTeamColorValue();
						outlineVertexConsumerProvider.setColor(ColorHelper.Argb.getRed(i), ColorHelper.Argb.getGreen(i), ColorHelper.Argb.getBlue(i), 255);
					} else {
						vertexConsumerProvider = immediate;
					}

					this.renderEntity(entity, d, e, f, tickDelta, matrices, vertexConsumerProvider);
				}
			}
		}

		immediate.drawCurrentLayer();
		this.checkEmpty(matrices);
		immediate.draw(RenderLayer.getEntitySolid(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE));
		immediate.draw(RenderLayer.getEntityCutout(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE));
		immediate.draw(RenderLayer.getEntityCutoutNoCull(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE));
		immediate.draw(RenderLayer.getEntitySmoothCutout(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE));
		profiler.swap("blockentities");

		for (ChunkBuilder.BuiltChunk builtChunk : this.field_45616) {
			List<BlockEntity> list = builtChunk.getData().getBlockEntities();
			if (!list.isEmpty()) {
				for (BlockEntity blockEntity : list) {
					BlockPos blockPos2 = blockEntity.getPos();
					VertexConsumerProvider vertexConsumerProvider2 = immediate;
					matrices.push();
					matrices.translate((double)blockPos2.getX() - d, (double)blockPos2.getY() - e, (double)blockPos2.getZ() - f);
					SortedSet<BlockBreakingInfo> sortedSet = this.blockBreakingProgressions.get(blockPos2.asLong());
					if (sortedSet != null && !sortedSet.isEmpty()) {
						int j = ((BlockBreakingInfo)sortedSet.last()).getStage();
						if (j >= 0) {
							MatrixStack.Entry entry = matrices.peek();
							VertexConsumer vertexConsumer = new OverlayVertexConsumer(
								this.bufferBuilders.getEffectVertexConsumers().getBuffer((RenderLayer)ModelLoader.BLOCK_DESTRUCTION_RENDER_LAYERS.get(j)),
								entry.getPositionMatrix(),
								entry.getNormalMatrix(),
								1.0F
							);
							vertexConsumerProvider2 = renderLayer -> {
								VertexConsumer vertexConsumer2x = immediate.getBuffer(renderLayer);
								return renderLayer.hasCrumbling() ? VertexConsumers.union(vertexConsumer, vertexConsumer2x) : vertexConsumer2x;
							};
						}
					}

					this.blockEntityRenderDispatcher.render(blockEntity, tickDelta, matrices, vertexConsumerProvider2);
					matrices.pop();
				}
			}
		}

		synchronized (this.noCullingBlockEntities) {
			for (BlockEntity blockEntity2 : this.noCullingBlockEntities) {
				BlockPos blockPos3 = blockEntity2.getPos();
				matrices.push();
				matrices.translate((double)blockPos3.getX() - d, (double)blockPos3.getY() - e, (double)blockPos3.getZ() - f);
				this.blockEntityRenderDispatcher.render(blockEntity2, tickDelta, matrices, immediate);
				matrices.pop();
			}
		}

		this.checkEmpty(matrices);
		immediate.draw(RenderLayer.getSolid());
		immediate.draw(RenderLayer.getEndPortal());
		immediate.draw(RenderLayer.getEndGateway());
		immediate.draw(TexturedRenderLayers.getEntitySolid());
		immediate.draw(TexturedRenderLayers.getEntityCutout());
		immediate.draw(TexturedRenderLayers.getBeds());
		immediate.draw(TexturedRenderLayers.getShulkerBoxes());
		immediate.draw(TexturedRenderLayers.getSign());
		immediate.draw(TexturedRenderLayers.getHangingSign());
		immediate.draw(TexturedRenderLayers.getChest());
		this.bufferBuilders.getOutlineVertexConsumers().draw();
		if (bl3) {
			this.entityOutlinePostProcessor.render(tickDelta);
			this.client.getFramebuffer().beginWrite(false);
		}

		profiler.swap("destroyProgress");

		for (Entry<SortedSet<BlockBreakingInfo>> entry2 : this.blockBreakingProgressions.long2ObjectEntrySet()) {
			BlockPos blockPos = BlockPos.fromLong(entry2.getLongKey());
			double h = (double)blockPos.getX() - d;
			double k = (double)blockPos.getY() - e;
			double l = (double)blockPos.getZ() - f;
			if (!(h * h + k * k + l * l > 1024.0)) {
				SortedSet<BlockBreakingInfo> sortedSet2 = (SortedSet<BlockBreakingInfo>)entry2.getValue();
				if (sortedSet2 != null && !sortedSet2.isEmpty()) {
					int m = ((BlockBreakingInfo)sortedSet2.last()).getStage();
					matrices.push();
					matrices.translate((double)blockPos.getX() - d, (double)blockPos.getY() - e, (double)blockPos.getZ() - f);
					MatrixStack.Entry entry3 = matrices.peek();
					VertexConsumer vertexConsumer2 = new OverlayVertexConsumer(
						this.bufferBuilders.getEffectVertexConsumers().getBuffer((RenderLayer)ModelLoader.BLOCK_DESTRUCTION_RENDER_LAYERS.get(m)),
						entry3.getPositionMatrix(),
						entry3.getNormalMatrix(),
						1.0F
					);
					this.client.getBlockRenderManager().renderDamage(this.world.getBlockState(blockPos), blockPos, this.world, matrices, vertexConsumer2);
					matrices.pop();
				}
			}
		}

		this.checkEmpty(matrices);
		HitResult hitResult = this.client.crosshairTarget;
		if (renderBlockOutline && hitResult != null && hitResult.getType() == HitResult.Type.BLOCK) {
			profiler.swap("outline");
			BlockPos blockPos4 = ((BlockHitResult)hitResult).getBlockPos();
			BlockState blockState = this.world.getBlockState(blockPos4);
			if (!blockState.isAir() && this.world.getWorldBorder().contains(blockPos4)) {
				VertexConsumer vertexConsumer3 = immediate.getBuffer(RenderLayer.getLines());
				this.drawBlockOutline(matrices, vertexConsumer3, camera.getFocusedEntity(), d, e, f, blockPos4, blockState);
			}
		}

		this.client.debugRenderer.render(matrices, immediate, d, e, f);
		immediate.drawCurrentLayer();
		MatrixStack matrixStack = RenderSystem.getModelViewStack();
		RenderSystem.applyModelViewMatrix();
		immediate.draw(TexturedRenderLayers.getEntityTranslucentCull());
		immediate.draw(TexturedRenderLayers.getBannerPatterns());
		immediate.draw(TexturedRenderLayers.getShieldPatterns());
		immediate.draw(RenderLayer.getArmorGlint());
		immediate.draw(RenderLayer.getArmorEntityGlint());
		immediate.draw(RenderLayer.getGlint());
		immediate.draw(RenderLayer.getDirectGlint());
		immediate.draw(RenderLayer.getGlintTranslucent());
		immediate.draw(RenderLayer.getEntityGlint());
		immediate.draw(RenderLayer.getDirectEntityGlint());
		immediate.draw(RenderLayer.getWaterMask());
		this.bufferBuilders.getEffectVertexConsumers().draw();
		if (this.transparencyPostProcessor != null) {
			immediate.draw(RenderLayer.getLines());
			immediate.draw();
			this.translucentFramebuffer.clear(MinecraftClient.IS_SYSTEM_MAC);
			this.translucentFramebuffer.copyDepthFrom(this.client.getFramebuffer());
			profiler.swap("translucent");
			this.renderLayer(RenderLayer.getTranslucent(), matrices, d, e, f, projectionMatrix);
			profiler.swap("string");
			this.renderLayer(RenderLayer.getTripwire(), matrices, d, e, f, projectionMatrix);
			this.particlesFramebuffer.clear(MinecraftClient.IS_SYSTEM_MAC);
			this.particlesFramebuffer.copyDepthFrom(this.client.getFramebuffer());
			RenderPhase.PARTICLES_TARGET.startDrawing();
			profiler.swap("particles");
			this.client.particleManager.renderParticles(matrices, immediate, lightmapTextureManager, camera, tickDelta);
			RenderPhase.PARTICLES_TARGET.endDrawing();
		} else {
			profiler.swap("translucent");
			if (this.translucentFramebuffer != null) {
				this.translucentFramebuffer.clear(MinecraftClient.IS_SYSTEM_MAC);
			}

			this.renderLayer(RenderLayer.getTranslucent(), matrices, d, e, f, projectionMatrix);
			immediate.draw(RenderLayer.getLines());
			immediate.draw();
			profiler.swap("string");
			this.renderLayer(RenderLayer.getTripwire(), matrices, d, e, f, projectionMatrix);
			profiler.swap("particles");
			this.client.particleManager.renderParticles(matrices, immediate, lightmapTextureManager, camera, tickDelta);
		}

		matrixStack.push();
		matrixStack.multiplyPositionMatrix(matrices.peek().getPositionMatrix());
		RenderSystem.applyModelViewMatrix();
		if (this.client.options.getCloudRenderModeValue() != CloudRenderMode.OFF) {
			if (this.transparencyPostProcessor != null) {
				this.cloudsFramebuffer.clear(MinecraftClient.IS_SYSTEM_MAC);
				RenderPhase.CLOUDS_TARGET.startDrawing();
				profiler.swap("clouds");
				this.renderClouds(matrices, projectionMatrix, tickDelta, d, e, f);
				RenderPhase.CLOUDS_TARGET.endDrawing();
			} else {
				profiler.swap("clouds");
				RenderSystem.setShader(GameRenderer::getPositionTexColorNormalProgram);
				this.renderClouds(matrices, projectionMatrix, tickDelta, d, e, f);
			}
		}

		if (this.transparencyPostProcessor != null) {
			RenderPhase.WEATHER_TARGET.startDrawing();
			profiler.swap("weather");
			this.renderWeather(lightmapTextureManager, tickDelta, d, e, f);
			this.renderWorldBorder(camera);
			RenderPhase.WEATHER_TARGET.endDrawing();
			this.transparencyPostProcessor.render(tickDelta);
			this.client.getFramebuffer().beginWrite(false);
		} else {
			RenderSystem.depthMask(false);
			profiler.swap("weather");
			this.renderWeather(lightmapTextureManager, tickDelta, d, e, f);
			this.renderWorldBorder(camera);
			RenderSystem.depthMask(true);
		}

		matrixStack.pop();
		RenderSystem.applyModelViewMatrix();
		this.renderChunkDebugInfo(matrices, immediate, camera);
		immediate.drawCurrentLayer();
		RenderSystem.depthMask(true);
		RenderSystem.disableBlend();
		BackgroundRenderer.clearFog();
	}

	private void checkEmpty(MatrixStack matrices) {
		if (!matrices.isEmpty()) {
			throw new IllegalStateException("Pose stack not empty");
		}
	}

	private void renderEntity(
		Entity entity, double cameraX, double cameraY, double cameraZ, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers
	) {
		double d = MathHelper.lerp((double)tickDelta, entity.lastRenderX, entity.getX());
		double e = MathHelper.lerp((double)tickDelta, entity.lastRenderY, entity.getY());
		double f = MathHelper.lerp((double)tickDelta, entity.lastRenderZ, entity.getZ());
		float g = MathHelper.lerp(tickDelta, entity.prevYaw, entity.getYaw());
		this.entityRenderDispatcher
			.render(entity, d - cameraX, e - cameraY, f - cameraZ, g, tickDelta, matrices, vertexConsumers, this.entityRenderDispatcher.getLight(entity, tickDelta));
	}

	private void renderLayer(RenderLayer renderLayer, MatrixStack matrices, double cameraX, double cameraY, double cameraZ, Matrix4f positionMatrix) {
		RenderSystem.assertOnRenderThread();
		renderLayer.startDrawing();
		if (renderLayer == RenderLayer.getTranslucent()) {
			this.client.getProfiler().push("translucent_sort");
			double d = cameraX - this.lastTranslucentSortX;
			double e = cameraY - this.lastTranslucentSortY;
			double f = cameraZ - this.lastTranslucentSortZ;
			if (d * d + e * e + f * f > 1.0) {
				int i = ChunkSectionPos.getSectionCoord(cameraX);
				int j = ChunkSectionPos.getSectionCoord(cameraY);
				int k = ChunkSectionPos.getSectionCoord(cameraZ);
				boolean bl = i != ChunkSectionPos.getSectionCoord(this.lastTranslucentSortX)
					|| k != ChunkSectionPos.getSectionCoord(this.lastTranslucentSortZ)
					|| j != ChunkSectionPos.getSectionCoord(this.lastTranslucentSortY);
				this.lastTranslucentSortX = cameraX;
				this.lastTranslucentSortY = cameraY;
				this.lastTranslucentSortZ = cameraZ;
				int l = 0;

				for (ChunkBuilder.BuiltChunk builtChunk : this.field_45616) {
					if (l < 15 && (bl || builtChunk.method_52841(i, j, k)) && builtChunk.scheduleSort(renderLayer, this.field_45614)) {
						l++;
					}
				}
			}

			this.client.getProfiler().pop();
		}

		this.client.getProfiler().push("filterempty");
		this.client.getProfiler().swap((Supplier<String>)(() -> "render_" + renderLayer));
		boolean bl2 = renderLayer != RenderLayer.getTranslucent();
		ObjectListIterator<ChunkBuilder.BuiltChunk> objectListIterator = this.field_45616.listIterator(bl2 ? 0 : this.field_45616.size());
		ShaderProgram shaderProgram = RenderSystem.getShader();

		for (int m = 0; m < 12; m++) {
			int n = RenderSystem.getShaderTexture(m);
			shaderProgram.addSampler("Sampler" + m, n);
		}

		if (shaderProgram.modelViewMat != null) {
			shaderProgram.modelViewMat.set(matrices.peek().getPositionMatrix());
		}

		if (shaderProgram.projectionMat != null) {
			shaderProgram.projectionMat.set(positionMatrix);
		}

		if (shaderProgram.colorModulator != null) {
			shaderProgram.colorModulator.set(RenderSystem.getShaderColor());
		}

		if (shaderProgram.glintAlpha != null) {
			shaderProgram.glintAlpha.set(RenderSystem.getShaderGlintAlpha());
		}

		if (shaderProgram.fogStart != null) {
			shaderProgram.fogStart.set(RenderSystem.getShaderFogStart());
		}

		if (shaderProgram.fogEnd != null) {
			shaderProgram.fogEnd.set(RenderSystem.getShaderFogEnd());
		}

		if (shaderProgram.fogColor != null) {
			shaderProgram.fogColor.set(RenderSystem.getShaderFogColor());
		}

		if (shaderProgram.fogShape != null) {
			shaderProgram.fogShape.set(RenderSystem.getShaderFogShape().getId());
		}

		if (shaderProgram.textureMat != null) {
			shaderProgram.textureMat.set(RenderSystem.getTextureMatrix());
		}

		if (shaderProgram.gameTime != null) {
			shaderProgram.gameTime.set(RenderSystem.getShaderGameTime());
		}

		RenderSystem.setupShaderLights(shaderProgram);
		shaderProgram.bind();
		GlUniform glUniform = shaderProgram.chunkOffset;

		while (bl2 ? objectListIterator.hasNext() : objectListIterator.hasPrevious()) {
			ChunkBuilder.BuiltChunk builtChunk2 = bl2 ? (ChunkBuilder.BuiltChunk)objectListIterator.next() : objectListIterator.previous();
			if (!builtChunk2.getData().isEmpty(renderLayer)) {
				VertexBuffer vertexBuffer = builtChunk2.getBuffer(renderLayer);
				BlockPos blockPos = builtChunk2.getOrigin();
				if (glUniform != null) {
					glUniform.set((float)((double)blockPos.getX() - cameraX), (float)((double)blockPos.getY() - cameraY), (float)((double)blockPos.getZ() - cameraZ));
					glUniform.upload();
				}

				vertexBuffer.bind();
				vertexBuffer.draw();
			}
		}

		if (glUniform != null) {
			glUniform.set(0.0F, 0.0F, 0.0F);
		}

		shaderProgram.unbind();
		VertexBuffer.unbind();
		this.client.getProfiler().pop();
		renderLayer.endDrawing();
	}

	private void renderChunkDebugInfo(MatrixStack matrices, VertexConsumerProvider vertexConsumers, Camera camera) {
		if (this.client.debugChunkInfo || this.client.debugChunkOcclusion) {
			double d = camera.getPos().getX();
			double e = camera.getPos().getY();
			double f = camera.getPos().getZ();

			for (ChunkBuilder.BuiltChunk builtChunk : this.field_45616) {
				ChunkRenderingDataPreparer.ChunkInfo chunkInfo = this.field_45615.method_52837(builtChunk);
				if (chunkInfo != null) {
					BlockPos blockPos = builtChunk.getOrigin();
					matrices.push();
					matrices.translate((double)blockPos.getX() - d, (double)blockPos.getY() - e, (double)blockPos.getZ() - f);
					Matrix4f matrix4f = matrices.peek().getPositionMatrix();
					if (this.client.debugChunkInfo) {
						VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getLines());
						int i = chunkInfo.propagationLevel == 0 ? 0 : MathHelper.hsvToRgb((float)chunkInfo.propagationLevel / 50.0F, 0.9F, 0.9F);
						int j = i >> 16 & 0xFF;
						int k = i >> 8 & 0xFF;
						int l = i & 0xFF;

						for (int m = 0; m < DIRECTIONS.length; m++) {
							if (chunkInfo.hasDirection(m)) {
								Direction direction = DIRECTIONS[m];
								vertexConsumer.vertex(matrix4f, 8.0F, 8.0F, 8.0F)
									.color(j, k, l, 255)
									.normal((float)direction.getOffsetX(), (float)direction.getOffsetY(), (float)direction.getOffsetZ())
									.next();
								vertexConsumer.vertex(
										matrix4f, (float)(8 - 16 * direction.getOffsetX()), (float)(8 - 16 * direction.getOffsetY()), (float)(8 - 16 * direction.getOffsetZ())
									)
									.color(j, k, l, 255)
									.normal((float)direction.getOffsetX(), (float)direction.getOffsetY(), (float)direction.getOffsetZ())
									.next();
							}
						}
					}

					if (this.client.debugChunkOcclusion && !builtChunk.getData().isEmpty()) {
						VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getLines());
						int i = 0;

						for (Direction direction2 : DIRECTIONS) {
							for (Direction direction3 : DIRECTIONS) {
								boolean bl = builtChunk.getData().isVisibleThrough(direction2, direction3);
								if (!bl) {
									i++;
									vertexConsumer.vertex(
											matrix4f, (float)(8 + 8 * direction2.getOffsetX()), (float)(8 + 8 * direction2.getOffsetY()), (float)(8 + 8 * direction2.getOffsetZ())
										)
										.color(255, 0, 0, 255)
										.normal((float)direction2.getOffsetX(), (float)direction2.getOffsetY(), (float)direction2.getOffsetZ())
										.next();
									vertexConsumer.vertex(
											matrix4f, (float)(8 + 8 * direction3.getOffsetX()), (float)(8 + 8 * direction3.getOffsetY()), (float)(8 + 8 * direction3.getOffsetZ())
										)
										.color(255, 0, 0, 255)
										.normal((float)direction3.getOffsetX(), (float)direction3.getOffsetY(), (float)direction3.getOffsetZ())
										.next();
								}
							}
						}

						if (i > 0) {
							VertexConsumer vertexConsumer2 = vertexConsumers.getBuffer(RenderLayer.getDebugQuads());
							float g = 0.5F;
							float h = 0.2F;
							vertexConsumer2.vertex(matrix4f, 0.5F, 15.5F, 0.5F).color(0.9F, 0.9F, 0.0F, 0.2F).next();
							vertexConsumer2.vertex(matrix4f, 15.5F, 15.5F, 0.5F).color(0.9F, 0.9F, 0.0F, 0.2F).next();
							vertexConsumer2.vertex(matrix4f, 15.5F, 15.5F, 15.5F).color(0.9F, 0.9F, 0.0F, 0.2F).next();
							vertexConsumer2.vertex(matrix4f, 0.5F, 15.5F, 15.5F).color(0.9F, 0.9F, 0.0F, 0.2F).next();
							vertexConsumer2.vertex(matrix4f, 0.5F, 0.5F, 15.5F).color(0.9F, 0.9F, 0.0F, 0.2F).next();
							vertexConsumer2.vertex(matrix4f, 15.5F, 0.5F, 15.5F).color(0.9F, 0.9F, 0.0F, 0.2F).next();
							vertexConsumer2.vertex(matrix4f, 15.5F, 0.5F, 0.5F).color(0.9F, 0.9F, 0.0F, 0.2F).next();
							vertexConsumer2.vertex(matrix4f, 0.5F, 0.5F, 0.5F).color(0.9F, 0.9F, 0.0F, 0.2F).next();
							vertexConsumer2.vertex(matrix4f, 0.5F, 15.5F, 0.5F).color(0.9F, 0.9F, 0.0F, 0.2F).next();
							vertexConsumer2.vertex(matrix4f, 0.5F, 15.5F, 15.5F).color(0.9F, 0.9F, 0.0F, 0.2F).next();
							vertexConsumer2.vertex(matrix4f, 0.5F, 0.5F, 15.5F).color(0.9F, 0.9F, 0.0F, 0.2F).next();
							vertexConsumer2.vertex(matrix4f, 0.5F, 0.5F, 0.5F).color(0.9F, 0.9F, 0.0F, 0.2F).next();
							vertexConsumer2.vertex(matrix4f, 15.5F, 0.5F, 0.5F).color(0.9F, 0.9F, 0.0F, 0.2F).next();
							vertexConsumer2.vertex(matrix4f, 15.5F, 0.5F, 15.5F).color(0.9F, 0.9F, 0.0F, 0.2F).next();
							vertexConsumer2.vertex(matrix4f, 15.5F, 15.5F, 15.5F).color(0.9F, 0.9F, 0.0F, 0.2F).next();
							vertexConsumer2.vertex(matrix4f, 15.5F, 15.5F, 0.5F).color(0.9F, 0.9F, 0.0F, 0.2F).next();
							vertexConsumer2.vertex(matrix4f, 0.5F, 0.5F, 0.5F).color(0.9F, 0.9F, 0.0F, 0.2F).next();
							vertexConsumer2.vertex(matrix4f, 15.5F, 0.5F, 0.5F).color(0.9F, 0.9F, 0.0F, 0.2F).next();
							vertexConsumer2.vertex(matrix4f, 15.5F, 15.5F, 0.5F).color(0.9F, 0.9F, 0.0F, 0.2F).next();
							vertexConsumer2.vertex(matrix4f, 0.5F, 15.5F, 0.5F).color(0.9F, 0.9F, 0.0F, 0.2F).next();
							vertexConsumer2.vertex(matrix4f, 0.5F, 15.5F, 15.5F).color(0.9F, 0.9F, 0.0F, 0.2F).next();
							vertexConsumer2.vertex(matrix4f, 15.5F, 15.5F, 15.5F).color(0.9F, 0.9F, 0.0F, 0.2F).next();
							vertexConsumer2.vertex(matrix4f, 15.5F, 0.5F, 15.5F).color(0.9F, 0.9F, 0.0F, 0.2F).next();
							vertexConsumer2.vertex(matrix4f, 0.5F, 0.5F, 15.5F).color(0.9F, 0.9F, 0.0F, 0.2F).next();
						}
					}

					matrices.pop();
				}
			}
		}

		if (this.capturedFrustum != null) {
			matrices.push();
			matrices.translate(
				(float)(this.capturedFrustumPosition.x - camera.getPos().x),
				(float)(this.capturedFrustumPosition.y - camera.getPos().y),
				(float)(this.capturedFrustumPosition.z - camera.getPos().z)
			);
			Matrix4f matrix4f2 = matrices.peek().getPositionMatrix();
			VertexConsumer vertexConsumer3 = vertexConsumers.getBuffer(RenderLayer.getDebugQuads());
			this.renderCapturedFrustumFace(vertexConsumer3, matrix4f2, 0, 1, 2, 3, 0, 1, 1);
			this.renderCapturedFrustumFace(vertexConsumer3, matrix4f2, 4, 5, 6, 7, 1, 0, 0);
			this.renderCapturedFrustumFace(vertexConsumer3, matrix4f2, 0, 1, 5, 4, 1, 1, 0);
			this.renderCapturedFrustumFace(vertexConsumer3, matrix4f2, 2, 3, 7, 6, 0, 0, 1);
			this.renderCapturedFrustumFace(vertexConsumer3, matrix4f2, 0, 4, 7, 3, 0, 1, 0);
			this.renderCapturedFrustumFace(vertexConsumer3, matrix4f2, 1, 5, 6, 2, 1, 0, 1);
			VertexConsumer vertexConsumer4 = vertexConsumers.getBuffer(RenderLayer.getLines());
			this.renderCapturedFrustumVertex(vertexConsumer4, matrix4f2, 0);
			this.renderCapturedFrustumVertex(vertexConsumer4, matrix4f2, 1);
			this.renderCapturedFrustumVertex(vertexConsumer4, matrix4f2, 1);
			this.renderCapturedFrustumVertex(vertexConsumer4, matrix4f2, 2);
			this.renderCapturedFrustumVertex(vertexConsumer4, matrix4f2, 2);
			this.renderCapturedFrustumVertex(vertexConsumer4, matrix4f2, 3);
			this.renderCapturedFrustumVertex(vertexConsumer4, matrix4f2, 3);
			this.renderCapturedFrustumVertex(vertexConsumer4, matrix4f2, 0);
			this.renderCapturedFrustumVertex(vertexConsumer4, matrix4f2, 4);
			this.renderCapturedFrustumVertex(vertexConsumer4, matrix4f2, 5);
			this.renderCapturedFrustumVertex(vertexConsumer4, matrix4f2, 5);
			this.renderCapturedFrustumVertex(vertexConsumer4, matrix4f2, 6);
			this.renderCapturedFrustumVertex(vertexConsumer4, matrix4f2, 6);
			this.renderCapturedFrustumVertex(vertexConsumer4, matrix4f2, 7);
			this.renderCapturedFrustumVertex(vertexConsumer4, matrix4f2, 7);
			this.renderCapturedFrustumVertex(vertexConsumer4, matrix4f2, 4);
			this.renderCapturedFrustumVertex(vertexConsumer4, matrix4f2, 0);
			this.renderCapturedFrustumVertex(vertexConsumer4, matrix4f2, 4);
			this.renderCapturedFrustumVertex(vertexConsumer4, matrix4f2, 1);
			this.renderCapturedFrustumVertex(vertexConsumer4, matrix4f2, 5);
			this.renderCapturedFrustumVertex(vertexConsumer4, matrix4f2, 2);
			this.renderCapturedFrustumVertex(vertexConsumer4, matrix4f2, 6);
			this.renderCapturedFrustumVertex(vertexConsumer4, matrix4f2, 3);
			this.renderCapturedFrustumVertex(vertexConsumer4, matrix4f2, 7);
			matrices.pop();
		}
	}

	private void renderCapturedFrustumVertex(VertexConsumer vertexConsumer, Matrix4f matrix4f, int planeNum) {
		vertexConsumer.vertex(
				matrix4f, this.capturedFrustumOrientation[planeNum].x(), this.capturedFrustumOrientation[planeNum].y(), this.capturedFrustumOrientation[planeNum].z()
			)
			.color(0, 0, 0, 255)
			.normal(0.0F, 0.0F, -1.0F)
			.next();
	}

	private void renderCapturedFrustumFace(VertexConsumer vertexConsumer, Matrix4f matrix4f, int plane0, int plane1, int plane2, int plane3, int r, int g, int b) {
		float f = 0.25F;
		vertexConsumer.vertex(
				matrix4f, this.capturedFrustumOrientation[plane0].x(), this.capturedFrustumOrientation[plane0].y(), this.capturedFrustumOrientation[plane0].z()
			)
			.color((float)r, (float)g, (float)b, 0.25F)
			.next();
		vertexConsumer.vertex(
				matrix4f, this.capturedFrustumOrientation[plane1].x(), this.capturedFrustumOrientation[plane1].y(), this.capturedFrustumOrientation[plane1].z()
			)
			.color((float)r, (float)g, (float)b, 0.25F)
			.next();
		vertexConsumer.vertex(
				matrix4f, this.capturedFrustumOrientation[plane2].x(), this.capturedFrustumOrientation[plane2].y(), this.capturedFrustumOrientation[plane2].z()
			)
			.color((float)r, (float)g, (float)b, 0.25F)
			.next();
		vertexConsumer.vertex(
				matrix4f, this.capturedFrustumOrientation[plane3].x(), this.capturedFrustumOrientation[plane3].y(), this.capturedFrustumOrientation[plane3].z()
			)
			.color((float)r, (float)g, (float)b, 0.25F)
			.next();
	}

	public void captureFrustum() {
		this.shouldCaptureFrustum = true;
	}

	public void killFrustum() {
		this.capturedFrustum = null;
	}

	public void tick() {
		this.ticks++;
		if (this.ticks % 20 == 0) {
			Iterator<BlockBreakingInfo> iterator = this.blockBreakingInfos.values().iterator();

			while (iterator.hasNext()) {
				BlockBreakingInfo blockBreakingInfo = (BlockBreakingInfo)iterator.next();
				int i = blockBreakingInfo.getLastUpdateTick();
				if (this.ticks - i > 400) {
					iterator.remove();
					this.removeBlockBreakingInfo(blockBreakingInfo);
				}
			}
		}
	}

	private void removeBlockBreakingInfo(BlockBreakingInfo info) {
		long l = info.getPos().asLong();
		Set<BlockBreakingInfo> set = (Set<BlockBreakingInfo>)this.blockBreakingProgressions.get(l);
		set.remove(info);
		if (set.isEmpty()) {
			this.blockBreakingProgressions.remove(l);
		}
	}

	private void renderEndSky(MatrixStack matrices) {
		RenderSystem.enableBlend();
		RenderSystem.depthMask(false);
		RenderSystem.setShader(GameRenderer::getPositionTexColorProgram);
		RenderSystem.setShaderTexture(0, END_SKY);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();

		for (int i = 0; i < 6; i++) {
			matrices.push();
			if (i == 1) {
				matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90.0F));
			}

			if (i == 2) {
				matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-90.0F));
			}

			if (i == 3) {
				matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180.0F));
			}

			if (i == 4) {
				matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(90.0F));
			}

			if (i == 5) {
				matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(-90.0F));
			}

			Matrix4f matrix4f = matrices.peek().getPositionMatrix();
			bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
			bufferBuilder.vertex(matrix4f, -100.0F, -100.0F, -100.0F).texture(0.0F, 0.0F).color(40, 40, 40, 255).next();
			bufferBuilder.vertex(matrix4f, -100.0F, -100.0F, 100.0F).texture(0.0F, 16.0F).color(40, 40, 40, 255).next();
			bufferBuilder.vertex(matrix4f, 100.0F, -100.0F, 100.0F).texture(16.0F, 16.0F).color(40, 40, 40, 255).next();
			bufferBuilder.vertex(matrix4f, 100.0F, -100.0F, -100.0F).texture(16.0F, 0.0F).color(40, 40, 40, 255).next();
			tessellator.draw();
			matrices.pop();
		}

		RenderSystem.depthMask(true);
		RenderSystem.disableBlend();
	}

	public void renderSky(MatrixStack matrices, Matrix4f projectionMatrix, float tickDelta, Camera camera, boolean thickFog, Runnable fogCallback) {
		fogCallback.run();
		if (!thickFog) {
			CameraSubmersionType cameraSubmersionType = camera.getSubmersionType();
			if (cameraSubmersionType != CameraSubmersionType.POWDER_SNOW && cameraSubmersionType != CameraSubmersionType.LAVA && !this.hasBlindnessOrDarkness(camera)) {
				if (this.client.world.getDimensionEffects().getSkyType() == DimensionEffects.SkyType.END) {
					this.renderEndSky(matrices);
				} else if (this.client.world.getDimensionEffects().getSkyType() == DimensionEffects.SkyType.NORMAL) {
					Vec3d vec3d = this.world.getSkyColor(this.client.gameRenderer.getCamera().getPos(), tickDelta);
					float f = (float)vec3d.x;
					float g = (float)vec3d.y;
					float h = (float)vec3d.z;
					BackgroundRenderer.setFogBlack();
					BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
					RenderSystem.depthMask(false);
					RenderSystem.setShaderColor(f, g, h, 1.0F);
					ShaderProgram shaderProgram = RenderSystem.getShader();
					this.lightSkyBuffer.bind();
					this.lightSkyBuffer.draw(matrices.peek().getPositionMatrix(), projectionMatrix, shaderProgram);
					VertexBuffer.unbind();
					RenderSystem.enableBlend();
					float[] fs = this.world.getDimensionEffects().getFogColorOverride(this.world.getSkyAngle(tickDelta), tickDelta);
					if (fs != null) {
						RenderSystem.setShader(GameRenderer::getPositionColorProgram);
						RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
						matrices.push();
						matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90.0F));
						float i = MathHelper.sin(this.world.getSkyAngleRadians(tickDelta)) < 0.0F ? 180.0F : 0.0F;
						matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(i));
						matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(90.0F));
						float j = fs[0];
						float k = fs[1];
						float l = fs[2];
						Matrix4f matrix4f = matrices.peek().getPositionMatrix();
						bufferBuilder.begin(VertexFormat.DrawMode.TRIANGLE_FAN, VertexFormats.POSITION_COLOR);
						bufferBuilder.vertex(matrix4f, 0.0F, 100.0F, 0.0F).color(j, k, l, fs[3]).next();
						int m = 16;

						for (int n = 0; n <= 16; n++) {
							float o = (float)n * (float) (Math.PI * 2) / 16.0F;
							float p = MathHelper.sin(o);
							float q = MathHelper.cos(o);
							bufferBuilder.vertex(matrix4f, p * 120.0F, q * 120.0F, -q * 40.0F * fs[3]).color(fs[0], fs[1], fs[2], 0.0F).next();
						}

						BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
						matrices.pop();
					}

					RenderSystem.blendFuncSeparate(
						GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO
					);
					matrices.push();
					float i = 1.0F - this.world.getRainGradient(tickDelta);
					RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, i);
					matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-90.0F));
					matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(this.world.getSkyAngle(tickDelta) * 360.0F));
					Matrix4f matrix4f2 = matrices.peek().getPositionMatrix();
					float k = 30.0F;
					RenderSystem.setShader(GameRenderer::getPositionTexProgram);
					RenderSystem.setShaderTexture(0, SUN);
					bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
					bufferBuilder.vertex(matrix4f2, -k, 100.0F, -k).texture(0.0F, 0.0F).next();
					bufferBuilder.vertex(matrix4f2, k, 100.0F, -k).texture(1.0F, 0.0F).next();
					bufferBuilder.vertex(matrix4f2, k, 100.0F, k).texture(1.0F, 1.0F).next();
					bufferBuilder.vertex(matrix4f2, -k, 100.0F, k).texture(0.0F, 1.0F).next();
					BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
					k = 20.0F;
					RenderSystem.setShaderTexture(0, MOON_PHASES);
					int r = this.world.getMoonPhase();
					int s = r % 4;
					int m = r / 4 % 2;
					float t = (float)(s + 0) / 4.0F;
					float o = (float)(m + 0) / 2.0F;
					float p = (float)(s + 1) / 4.0F;
					float q = (float)(m + 1) / 2.0F;
					bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
					bufferBuilder.vertex(matrix4f2, -k, -100.0F, k).texture(p, q).next();
					bufferBuilder.vertex(matrix4f2, k, -100.0F, k).texture(t, q).next();
					bufferBuilder.vertex(matrix4f2, k, -100.0F, -k).texture(t, o).next();
					bufferBuilder.vertex(matrix4f2, -k, -100.0F, -k).texture(p, o).next();
					BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
					float u = this.world.method_23787(tickDelta) * i;
					if (u > 0.0F) {
						RenderSystem.setShaderColor(u, u, u, u);
						BackgroundRenderer.clearFog();
						this.starsBuffer.bind();
						this.starsBuffer.draw(matrices.peek().getPositionMatrix(), projectionMatrix, GameRenderer.getPositionProgram());
						VertexBuffer.unbind();
						fogCallback.run();
					}

					RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
					RenderSystem.disableBlend();
					RenderSystem.defaultBlendFunc();
					matrices.pop();
					RenderSystem.setShaderColor(0.0F, 0.0F, 0.0F, 1.0F);
					double d = this.client.player.getCameraPosVec(tickDelta).y - this.world.getLevelProperties().getSkyDarknessHeight(this.world);
					if (d < 0.0) {
						matrices.push();
						matrices.translate(0.0F, 12.0F, 0.0F);
						this.darkSkyBuffer.bind();
						this.darkSkyBuffer.draw(matrices.peek().getPositionMatrix(), projectionMatrix, shaderProgram);
						VertexBuffer.unbind();
						matrices.pop();
					}

					RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
					RenderSystem.depthMask(true);
				}
			}
		}
	}

	private boolean hasBlindnessOrDarkness(Camera camera) {
		return !(camera.getFocusedEntity() instanceof LivingEntity livingEntity)
			? false
			: livingEntity.hasStatusEffect(StatusEffects.BLINDNESS) || livingEntity.hasStatusEffect(StatusEffects.DARKNESS);
	}

	public void renderClouds(MatrixStack matrices, Matrix4f projectionMatrix, float tickDelta, double cameraX, double cameraY, double cameraZ) {
		float f = this.world.getDimensionEffects().getCloudsHeight();
		if (!Float.isNaN(f)) {
			RenderSystem.disableCull();
			RenderSystem.enableBlend();
			RenderSystem.enableDepthTest();
			RenderSystem.blendFuncSeparate(
				GlStateManager.SrcFactor.SRC_ALPHA,
				GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA,
				GlStateManager.SrcFactor.ONE,
				GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA
			);
			RenderSystem.depthMask(true);
			float g = 12.0F;
			float h = 4.0F;
			double d = 2.0E-4;
			double e = (double)(((float)this.ticks + tickDelta) * 0.03F);
			double i = (cameraX + e) / 12.0;
			double j = (double)(f - (float)cameraY + 0.33F);
			double k = cameraZ / 12.0 + 0.33F;
			i -= (double)(MathHelper.floor(i / 2048.0) * 2048);
			k -= (double)(MathHelper.floor(k / 2048.0) * 2048);
			float l = (float)(i - (double)MathHelper.floor(i));
			float m = (float)(j / 4.0 - (double)MathHelper.floor(j / 4.0)) * 4.0F;
			float n = (float)(k - (double)MathHelper.floor(k));
			Vec3d vec3d = this.world.getCloudsColor(tickDelta);
			int o = (int)Math.floor(i);
			int p = (int)Math.floor(j / 4.0);
			int q = (int)Math.floor(k);
			if (o != this.lastCloudsBlockX
				|| p != this.lastCloudsBlockY
				|| q != this.lastCloudsBlockZ
				|| this.client.options.getCloudRenderModeValue() != this.lastCloudRenderMode
				|| this.lastCloudsColor.squaredDistanceTo(vec3d) > 2.0E-4) {
				this.lastCloudsBlockX = o;
				this.lastCloudsBlockY = p;
				this.lastCloudsBlockZ = q;
				this.lastCloudsColor = vec3d;
				this.lastCloudRenderMode = this.client.options.getCloudRenderModeValue();
				this.cloudsDirty = true;
			}

			if (this.cloudsDirty) {
				this.cloudsDirty = false;
				BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
				if (this.cloudsBuffer != null) {
					this.cloudsBuffer.close();
				}

				this.cloudsBuffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
				BufferBuilder.BuiltBuffer builtBuffer = this.renderClouds(bufferBuilder, i, j, k, vec3d);
				this.cloudsBuffer.bind();
				this.cloudsBuffer.upload(builtBuffer);
				VertexBuffer.unbind();
			}

			RenderSystem.setShader(GameRenderer::getPositionTexColorNormalProgram);
			RenderSystem.setShaderTexture(0, CLOUDS);
			BackgroundRenderer.setFogBlack();
			matrices.push();
			matrices.scale(12.0F, 1.0F, 12.0F);
			matrices.translate(-l, m, -n);
			if (this.cloudsBuffer != null) {
				this.cloudsBuffer.bind();
				int r = this.lastCloudRenderMode == CloudRenderMode.FANCY ? 0 : 1;

				for (int s = r; s < 2; s++) {
					if (s == 0) {
						RenderSystem.colorMask(false, false, false, false);
					} else {
						RenderSystem.colorMask(true, true, true, true);
					}

					ShaderProgram shaderProgram = RenderSystem.getShader();
					this.cloudsBuffer.draw(matrices.peek().getPositionMatrix(), projectionMatrix, shaderProgram);
				}

				VertexBuffer.unbind();
			}

			matrices.pop();
			RenderSystem.enableCull();
			RenderSystem.disableBlend();
			RenderSystem.defaultBlendFunc();
		}
	}

	private BufferBuilder.BuiltBuffer renderClouds(BufferBuilder builder, double x, double y, double z, Vec3d color) {
		float f = 4.0F;
		float g = 0.00390625F;
		int i = 8;
		int j = 4;
		float h = 9.765625E-4F;
		float k = (float)MathHelper.floor(x) * 0.00390625F;
		float l = (float)MathHelper.floor(z) * 0.00390625F;
		float m = (float)color.x;
		float n = (float)color.y;
		float o = (float)color.z;
		float p = m * 0.9F;
		float q = n * 0.9F;
		float r = o * 0.9F;
		float s = m * 0.7F;
		float t = n * 0.7F;
		float u = o * 0.7F;
		float v = m * 0.8F;
		float w = n * 0.8F;
		float aa = o * 0.8F;
		RenderSystem.setShader(GameRenderer::getPositionTexColorNormalProgram);
		builder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR_NORMAL);
		float ab = (float)Math.floor(y / 4.0) * 4.0F;
		if (this.lastCloudRenderMode == CloudRenderMode.FANCY) {
			for (int ac = -3; ac <= 4; ac++) {
				for (int ad = -3; ad <= 4; ad++) {
					float ae = (float)(ac * 8);
					float af = (float)(ad * 8);
					if (ab > -5.0F) {
						builder.vertex((double)(ae + 0.0F), (double)(ab + 0.0F), (double)(af + 8.0F))
							.texture((ae + 0.0F) * 0.00390625F + k, (af + 8.0F) * 0.00390625F + l)
							.color(s, t, u, 0.8F)
							.normal(0.0F, -1.0F, 0.0F)
							.next();
						builder.vertex((double)(ae + 8.0F), (double)(ab + 0.0F), (double)(af + 8.0F))
							.texture((ae + 8.0F) * 0.00390625F + k, (af + 8.0F) * 0.00390625F + l)
							.color(s, t, u, 0.8F)
							.normal(0.0F, -1.0F, 0.0F)
							.next();
						builder.vertex((double)(ae + 8.0F), (double)(ab + 0.0F), (double)(af + 0.0F))
							.texture((ae + 8.0F) * 0.00390625F + k, (af + 0.0F) * 0.00390625F + l)
							.color(s, t, u, 0.8F)
							.normal(0.0F, -1.0F, 0.0F)
							.next();
						builder.vertex((double)(ae + 0.0F), (double)(ab + 0.0F), (double)(af + 0.0F))
							.texture((ae + 0.0F) * 0.00390625F + k, (af + 0.0F) * 0.00390625F + l)
							.color(s, t, u, 0.8F)
							.normal(0.0F, -1.0F, 0.0F)
							.next();
					}

					if (ab <= 5.0F) {
						builder.vertex((double)(ae + 0.0F), (double)(ab + 4.0F - 9.765625E-4F), (double)(af + 8.0F))
							.texture((ae + 0.0F) * 0.00390625F + k, (af + 8.0F) * 0.00390625F + l)
							.color(m, n, o, 0.8F)
							.normal(0.0F, 1.0F, 0.0F)
							.next();
						builder.vertex((double)(ae + 8.0F), (double)(ab + 4.0F - 9.765625E-4F), (double)(af + 8.0F))
							.texture((ae + 8.0F) * 0.00390625F + k, (af + 8.0F) * 0.00390625F + l)
							.color(m, n, o, 0.8F)
							.normal(0.0F, 1.0F, 0.0F)
							.next();
						builder.vertex((double)(ae + 8.0F), (double)(ab + 4.0F - 9.765625E-4F), (double)(af + 0.0F))
							.texture((ae + 8.0F) * 0.00390625F + k, (af + 0.0F) * 0.00390625F + l)
							.color(m, n, o, 0.8F)
							.normal(0.0F, 1.0F, 0.0F)
							.next();
						builder.vertex((double)(ae + 0.0F), (double)(ab + 4.0F - 9.765625E-4F), (double)(af + 0.0F))
							.texture((ae + 0.0F) * 0.00390625F + k, (af + 0.0F) * 0.00390625F + l)
							.color(m, n, o, 0.8F)
							.normal(0.0F, 1.0F, 0.0F)
							.next();
					}

					if (ac > -1) {
						for (int ag = 0; ag < 8; ag++) {
							builder.vertex((double)(ae + (float)ag + 0.0F), (double)(ab + 0.0F), (double)(af + 8.0F))
								.texture((ae + (float)ag + 0.5F) * 0.00390625F + k, (af + 8.0F) * 0.00390625F + l)
								.color(p, q, r, 0.8F)
								.normal(-1.0F, 0.0F, 0.0F)
								.next();
							builder.vertex((double)(ae + (float)ag + 0.0F), (double)(ab + 4.0F), (double)(af + 8.0F))
								.texture((ae + (float)ag + 0.5F) * 0.00390625F + k, (af + 8.0F) * 0.00390625F + l)
								.color(p, q, r, 0.8F)
								.normal(-1.0F, 0.0F, 0.0F)
								.next();
							builder.vertex((double)(ae + (float)ag + 0.0F), (double)(ab + 4.0F), (double)(af + 0.0F))
								.texture((ae + (float)ag + 0.5F) * 0.00390625F + k, (af + 0.0F) * 0.00390625F + l)
								.color(p, q, r, 0.8F)
								.normal(-1.0F, 0.0F, 0.0F)
								.next();
							builder.vertex((double)(ae + (float)ag + 0.0F), (double)(ab + 0.0F), (double)(af + 0.0F))
								.texture((ae + (float)ag + 0.5F) * 0.00390625F + k, (af + 0.0F) * 0.00390625F + l)
								.color(p, q, r, 0.8F)
								.normal(-1.0F, 0.0F, 0.0F)
								.next();
						}
					}

					if (ac <= 1) {
						for (int ag = 0; ag < 8; ag++) {
							builder.vertex((double)(ae + (float)ag + 1.0F - 9.765625E-4F), (double)(ab + 0.0F), (double)(af + 8.0F))
								.texture((ae + (float)ag + 0.5F) * 0.00390625F + k, (af + 8.0F) * 0.00390625F + l)
								.color(p, q, r, 0.8F)
								.normal(1.0F, 0.0F, 0.0F)
								.next();
							builder.vertex((double)(ae + (float)ag + 1.0F - 9.765625E-4F), (double)(ab + 4.0F), (double)(af + 8.0F))
								.texture((ae + (float)ag + 0.5F) * 0.00390625F + k, (af + 8.0F) * 0.00390625F + l)
								.color(p, q, r, 0.8F)
								.normal(1.0F, 0.0F, 0.0F)
								.next();
							builder.vertex((double)(ae + (float)ag + 1.0F - 9.765625E-4F), (double)(ab + 4.0F), (double)(af + 0.0F))
								.texture((ae + (float)ag + 0.5F) * 0.00390625F + k, (af + 0.0F) * 0.00390625F + l)
								.color(p, q, r, 0.8F)
								.normal(1.0F, 0.0F, 0.0F)
								.next();
							builder.vertex((double)(ae + (float)ag + 1.0F - 9.765625E-4F), (double)(ab + 0.0F), (double)(af + 0.0F))
								.texture((ae + (float)ag + 0.5F) * 0.00390625F + k, (af + 0.0F) * 0.00390625F + l)
								.color(p, q, r, 0.8F)
								.normal(1.0F, 0.0F, 0.0F)
								.next();
						}
					}

					if (ad > -1) {
						for (int ag = 0; ag < 8; ag++) {
							builder.vertex((double)(ae + 0.0F), (double)(ab + 4.0F), (double)(af + (float)ag + 0.0F))
								.texture((ae + 0.0F) * 0.00390625F + k, (af + (float)ag + 0.5F) * 0.00390625F + l)
								.color(v, w, aa, 0.8F)
								.normal(0.0F, 0.0F, -1.0F)
								.next();
							builder.vertex((double)(ae + 8.0F), (double)(ab + 4.0F), (double)(af + (float)ag + 0.0F))
								.texture((ae + 8.0F) * 0.00390625F + k, (af + (float)ag + 0.5F) * 0.00390625F + l)
								.color(v, w, aa, 0.8F)
								.normal(0.0F, 0.0F, -1.0F)
								.next();
							builder.vertex((double)(ae + 8.0F), (double)(ab + 0.0F), (double)(af + (float)ag + 0.0F))
								.texture((ae + 8.0F) * 0.00390625F + k, (af + (float)ag + 0.5F) * 0.00390625F + l)
								.color(v, w, aa, 0.8F)
								.normal(0.0F, 0.0F, -1.0F)
								.next();
							builder.vertex((double)(ae + 0.0F), (double)(ab + 0.0F), (double)(af + (float)ag + 0.0F))
								.texture((ae + 0.0F) * 0.00390625F + k, (af + (float)ag + 0.5F) * 0.00390625F + l)
								.color(v, w, aa, 0.8F)
								.normal(0.0F, 0.0F, -1.0F)
								.next();
						}
					}

					if (ad <= 1) {
						for (int ag = 0; ag < 8; ag++) {
							builder.vertex((double)(ae + 0.0F), (double)(ab + 4.0F), (double)(af + (float)ag + 1.0F - 9.765625E-4F))
								.texture((ae + 0.0F) * 0.00390625F + k, (af + (float)ag + 0.5F) * 0.00390625F + l)
								.color(v, w, aa, 0.8F)
								.normal(0.0F, 0.0F, 1.0F)
								.next();
							builder.vertex((double)(ae + 8.0F), (double)(ab + 4.0F), (double)(af + (float)ag + 1.0F - 9.765625E-4F))
								.texture((ae + 8.0F) * 0.00390625F + k, (af + (float)ag + 0.5F) * 0.00390625F + l)
								.color(v, w, aa, 0.8F)
								.normal(0.0F, 0.0F, 1.0F)
								.next();
							builder.vertex((double)(ae + 8.0F), (double)(ab + 0.0F), (double)(af + (float)ag + 1.0F - 9.765625E-4F))
								.texture((ae + 8.0F) * 0.00390625F + k, (af + (float)ag + 0.5F) * 0.00390625F + l)
								.color(v, w, aa, 0.8F)
								.normal(0.0F, 0.0F, 1.0F)
								.next();
							builder.vertex((double)(ae + 0.0F), (double)(ab + 0.0F), (double)(af + (float)ag + 1.0F - 9.765625E-4F))
								.texture((ae + 0.0F) * 0.00390625F + k, (af + (float)ag + 0.5F) * 0.00390625F + l)
								.color(v, w, aa, 0.8F)
								.normal(0.0F, 0.0F, 1.0F)
								.next();
						}
					}
				}
			}
		} else {
			int ac = 1;
			int ad = 32;

			for (int ah = -32; ah < 32; ah += 32) {
				for (int ai = -32; ai < 32; ai += 32) {
					builder.vertex((double)(ah + 0), (double)ab, (double)(ai + 32))
						.texture((float)(ah + 0) * 0.00390625F + k, (float)(ai + 32) * 0.00390625F + l)
						.color(m, n, o, 0.8F)
						.normal(0.0F, -1.0F, 0.0F)
						.next();
					builder.vertex((double)(ah + 32), (double)ab, (double)(ai + 32))
						.texture((float)(ah + 32) * 0.00390625F + k, (float)(ai + 32) * 0.00390625F + l)
						.color(m, n, o, 0.8F)
						.normal(0.0F, -1.0F, 0.0F)
						.next();
					builder.vertex((double)(ah + 32), (double)ab, (double)(ai + 0))
						.texture((float)(ah + 32) * 0.00390625F + k, (float)(ai + 0) * 0.00390625F + l)
						.color(m, n, o, 0.8F)
						.normal(0.0F, -1.0F, 0.0F)
						.next();
					builder.vertex((double)(ah + 0), (double)ab, (double)(ai + 0))
						.texture((float)(ah + 0) * 0.00390625F + k, (float)(ai + 0) * 0.00390625F + l)
						.color(m, n, o, 0.8F)
						.normal(0.0F, -1.0F, 0.0F)
						.next();
				}
			}
		}

		return builder.end();
	}

	private void updateChunks(Camera camera) {
		this.client.getProfiler().push("populate_sections_to_compile");
		LightingProvider lightingProvider = this.world.getLightingProvider();
		ChunkRendererRegionBuilder chunkRendererRegionBuilder = new ChunkRendererRegionBuilder();
		BlockPos blockPos = camera.getBlockPos();
		List<ChunkBuilder.BuiltChunk> list = Lists.<ChunkBuilder.BuiltChunk>newArrayList();

		for (ChunkBuilder.BuiltChunk builtChunk : this.field_45616) {
			ChunkSectionPos chunkSectionPos = ChunkSectionPos.from(builtChunk.getOrigin());
			if (builtChunk.needsRebuild() && lightingProvider.isLightingEnabled(chunkSectionPos)) {
				boolean bl = false;
				if (this.client.options.getChunkBuilderMode().getValue() == ChunkBuilderMode.NEARBY) {
					BlockPos blockPos2 = builtChunk.getOrigin().add(8, 8, 8);
					bl = blockPos2.getSquaredDistance(blockPos) < 768.0 || builtChunk.needsImportantRebuild();
				} else if (this.client.options.getChunkBuilderMode().getValue() == ChunkBuilderMode.PLAYER_AFFECTED) {
					bl = builtChunk.needsImportantRebuild();
				}

				if (bl) {
					this.client.getProfiler().push("build_near_sync");
					this.field_45614.rebuild(builtChunk, chunkRendererRegionBuilder);
					builtChunk.cancelRebuild();
					this.client.getProfiler().pop();
				} else {
					list.add(builtChunk);
				}
			}
		}

		this.client.getProfiler().swap("upload");
		this.field_45614.upload();
		this.client.getProfiler().swap("schedule_async_compile");

		for (ChunkBuilder.BuiltChunk builtChunkx : list) {
			builtChunkx.scheduleRebuild(this.field_45614, chunkRendererRegionBuilder);
			builtChunkx.cancelRebuild();
		}

		this.client.getProfiler().pop();
	}

	private void renderWorldBorder(Camera camera) {
		BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
		WorldBorder worldBorder = this.world.getWorldBorder();
		double d = (double)(this.client.options.getClampedViewDistance() * 16);
		if (!(camera.getPos().x < worldBorder.getBoundEast() - d)
			|| !(camera.getPos().x > worldBorder.getBoundWest() + d)
			|| !(camera.getPos().z < worldBorder.getBoundSouth() - d)
			|| !(camera.getPos().z > worldBorder.getBoundNorth() + d)) {
			double e = 1.0 - worldBorder.getDistanceInsideBorder(camera.getPos().x, camera.getPos().z) / d;
			e = Math.pow(e, 4.0);
			e = MathHelper.clamp(e, 0.0, 1.0);
			double f = camera.getPos().x;
			double g = camera.getPos().z;
			double h = (double)this.client.gameRenderer.getFarPlaneDistance();
			RenderSystem.enableBlend();
			RenderSystem.enableDepthTest();
			RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO);
			RenderSystem.setShaderTexture(0, FORCEFIELD);
			RenderSystem.depthMask(MinecraftClient.isFabulousGraphicsOrBetter());
			MatrixStack matrixStack = RenderSystem.getModelViewStack();
			matrixStack.push();
			RenderSystem.applyModelViewMatrix();
			int i = worldBorder.getStage().getColor();
			float j = (float)(i >> 16 & 0xFF) / 255.0F;
			float k = (float)(i >> 8 & 0xFF) / 255.0F;
			float l = (float)(i & 0xFF) / 255.0F;
			RenderSystem.setShaderColor(j, k, l, (float)e);
			RenderSystem.setShader(GameRenderer::getPositionTexProgram);
			RenderSystem.polygonOffset(-3.0F, -3.0F);
			RenderSystem.enablePolygonOffset();
			RenderSystem.disableCull();
			float m = (float)(Util.getMeasuringTimeMs() % 3000L) / 3000.0F;
			float n = (float)(-MathHelper.fractionalPart(camera.getPos().y * 0.5));
			float o = n + (float)h;
			bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
			double p = Math.max((double)MathHelper.floor(g - d), worldBorder.getBoundNorth());
			double q = Math.min((double)MathHelper.ceil(g + d), worldBorder.getBoundSouth());
			float r = (float)(MathHelper.floor(p) & 1) * 0.5F;
			if (f > worldBorder.getBoundEast() - d) {
				float s = r;

				for (double t = p; t < q; s += 0.5F) {
					double u = Math.min(1.0, q - t);
					float v = (float)u * 0.5F;
					bufferBuilder.vertex(worldBorder.getBoundEast() - f, -h, t - g).texture(m - s, m + o).next();
					bufferBuilder.vertex(worldBorder.getBoundEast() - f, -h, t + u - g).texture(m - (v + s), m + o).next();
					bufferBuilder.vertex(worldBorder.getBoundEast() - f, h, t + u - g).texture(m - (v + s), m + n).next();
					bufferBuilder.vertex(worldBorder.getBoundEast() - f, h, t - g).texture(m - s, m + n).next();
					t++;
				}
			}

			if (f < worldBorder.getBoundWest() + d) {
				float s = r;

				for (double t = p; t < q; s += 0.5F) {
					double u = Math.min(1.0, q - t);
					float v = (float)u * 0.5F;
					bufferBuilder.vertex(worldBorder.getBoundWest() - f, -h, t - g).texture(m + s, m + o).next();
					bufferBuilder.vertex(worldBorder.getBoundWest() - f, -h, t + u - g).texture(m + v + s, m + o).next();
					bufferBuilder.vertex(worldBorder.getBoundWest() - f, h, t + u - g).texture(m + v + s, m + n).next();
					bufferBuilder.vertex(worldBorder.getBoundWest() - f, h, t - g).texture(m + s, m + n).next();
					t++;
				}
			}

			p = Math.max((double)MathHelper.floor(f - d), worldBorder.getBoundWest());
			q = Math.min((double)MathHelper.ceil(f + d), worldBorder.getBoundEast());
			r = (float)(MathHelper.floor(p) & 1) * 0.5F;
			if (g > worldBorder.getBoundSouth() - d) {
				float s = r;

				for (double t = p; t < q; s += 0.5F) {
					double u = Math.min(1.0, q - t);
					float v = (float)u * 0.5F;
					bufferBuilder.vertex(t - f, -h, worldBorder.getBoundSouth() - g).texture(m + s, m + o).next();
					bufferBuilder.vertex(t + u - f, -h, worldBorder.getBoundSouth() - g).texture(m + v + s, m + o).next();
					bufferBuilder.vertex(t + u - f, h, worldBorder.getBoundSouth() - g).texture(m + v + s, m + n).next();
					bufferBuilder.vertex(t - f, h, worldBorder.getBoundSouth() - g).texture(m + s, m + n).next();
					t++;
				}
			}

			if (g < worldBorder.getBoundNorth() + d) {
				float s = r;

				for (double t = p; t < q; s += 0.5F) {
					double u = Math.min(1.0, q - t);
					float v = (float)u * 0.5F;
					bufferBuilder.vertex(t - f, -h, worldBorder.getBoundNorth() - g).texture(m - s, m + o).next();
					bufferBuilder.vertex(t + u - f, -h, worldBorder.getBoundNorth() - g).texture(m - (v + s), m + o).next();
					bufferBuilder.vertex(t + u - f, h, worldBorder.getBoundNorth() - g).texture(m - (v + s), m + n).next();
					bufferBuilder.vertex(t - f, h, worldBorder.getBoundNorth() - g).texture(m - s, m + n).next();
					t++;
				}
			}

			BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
			RenderSystem.enableCull();
			RenderSystem.polygonOffset(0.0F, 0.0F);
			RenderSystem.disablePolygonOffset();
			RenderSystem.disableBlend();
			RenderSystem.defaultBlendFunc();
			matrixStack.pop();
			RenderSystem.applyModelViewMatrix();
			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
			RenderSystem.depthMask(true);
		}
	}

	private void drawBlockOutline(
		MatrixStack matrices, VertexConsumer vertexConsumer, Entity entity, double cameraX, double cameraY, double cameraZ, BlockPos pos, BlockState state
	) {
		drawCuboidShapeOutline(
			matrices,
			vertexConsumer,
			state.getOutlineShape(this.world, pos, ShapeContext.of(entity)),
			(double)pos.getX() - cameraX,
			(double)pos.getY() - cameraY,
			(double)pos.getZ() - cameraZ,
			0.0F,
			0.0F,
			0.0F,
			0.4F
		);
	}

	private static Vec3d getMaxIntensityColor(float hue) {
		float f = 5.99999F;
		int i = (int)(MathHelper.clamp(hue, 0.0F, 1.0F) * 5.99999F);
		float g = hue * 5.99999F - (float)i;

		return switch (i) {
			case 0 -> new Vec3d(1.0, (double)g, 0.0);
			case 1 -> new Vec3d((double)(1.0F - g), 1.0, 0.0);
			case 2 -> new Vec3d(0.0, 1.0, (double)g);
			case 3 -> new Vec3d(0.0, 1.0 - (double)g, 1.0);
			case 4 -> new Vec3d((double)g, 0.0, 1.0);
			case 5 -> new Vec3d(1.0, 0.0, 1.0 - (double)g);
			default -> throw new IllegalStateException("Unexpected value: " + i);
		};
	}

	private static Vec3d shiftHue(float red, float green, float blue, float hueOffset) {
		Vec3d vec3d = getMaxIntensityColor(hueOffset).multiply((double)red);
		Vec3d vec3d2 = getMaxIntensityColor((hueOffset + 0.33333334F) % 1.0F).multiply((double)green);
		Vec3d vec3d3 = getMaxIntensityColor((hueOffset + 0.6666667F) % 1.0F).multiply((double)blue);
		Vec3d vec3d4 = vec3d.add(vec3d2).add(vec3d3);
		double d = Math.max(Math.max(1.0, vec3d4.x), Math.max(vec3d4.y, vec3d4.z));
		return new Vec3d(vec3d4.x / d, vec3d4.y / d, vec3d4.z / d);
	}

	public static void drawShapeOutline(
		MatrixStack matrices,
		VertexConsumer vertexConsumer,
		VoxelShape shape,
		double offsetX,
		double offsetY,
		double offsetZ,
		float red,
		float green,
		float blue,
		float alpha,
		boolean colorize
	) {
		List<Box> list = shape.getBoundingBoxes();
		if (!list.isEmpty()) {
			int i = colorize ? list.size() : list.size() * 8;
			drawCuboidShapeOutline(matrices, vertexConsumer, VoxelShapes.cuboid((Box)list.get(0)), offsetX, offsetY, offsetZ, red, green, blue, alpha);

			for (int j = 1; j < list.size(); j++) {
				Box box = (Box)list.get(j);
				float f = (float)j / (float)i;
				Vec3d vec3d = shiftHue(red, green, blue, f);
				drawCuboidShapeOutline(matrices, vertexConsumer, VoxelShapes.cuboid(box), offsetX, offsetY, offsetZ, (float)vec3d.x, (float)vec3d.y, (float)vec3d.z, alpha);
			}
		}
	}

	private static void drawCuboidShapeOutline(
		MatrixStack matrices,
		VertexConsumer vertexConsumer,
		VoxelShape shape,
		double offsetX,
		double offsetY,
		double offsetZ,
		float red,
		float green,
		float blue,
		float alpha
	) {
		MatrixStack.Entry entry = matrices.peek();
		shape.forEachEdge(
			(minX, minY, minZ, maxX, maxY, maxZ) -> {
				float k = (float)(maxX - minX);
				float l = (float)(maxY - minY);
				float m = (float)(maxZ - minZ);
				float n = MathHelper.sqrt(k * k + l * l + m * m);
				k /= n;
				l /= n;
				m /= n;
				vertexConsumer.vertex(entry.getPositionMatrix(), (float)(minX + offsetX), (float)(minY + offsetY), (float)(minZ + offsetZ))
					.color(red, green, blue, alpha)
					.normal(entry.getNormalMatrix(), k, l, m)
					.next();
				vertexConsumer.vertex(entry.getPositionMatrix(), (float)(maxX + offsetX), (float)(maxY + offsetY), (float)(maxZ + offsetZ))
					.color(red, green, blue, alpha)
					.normal(entry.getNormalMatrix(), k, l, m)
					.next();
			}
		);
	}

	/**
	 * Draws a box spanning from [x1,y1,z1] to [x2,y2,z2].
	 */
	public static void drawBox(
		VertexConsumer vertexConsumer, double x1, double y1, double z1, double x2, double y2, double z2, float red, float green, float blue, float alpha
	) {
		drawBox(new MatrixStack(), vertexConsumer, x1, y1, z1, x2, y2, z2, red, green, blue, alpha, red, green, blue);
	}

	/**
	 * Draws a box.
	 * 
	 * <p>Note the coordinates the box spans are relative to current translation of the matrices.
	 */
	public static void drawBox(MatrixStack matrices, VertexConsumer vertexConsumer, Box box, float red, float green, float blue, float alpha) {
		drawBox(matrices, vertexConsumer, box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ, red, green, blue, alpha, red, green, blue);
	}

	/**
	 * Draws a box spanning from [x1,y1,z1] to [x2,y2,z2].
	 * 
	 * <p>Note the coordinates the box spans are relative to current translation of the matrices.
	 */
	public static void drawBox(
		MatrixStack matrices,
		VertexConsumer vertexConsumer,
		double x1,
		double y1,
		double z1,
		double x2,
		double y2,
		double z2,
		float red,
		float green,
		float blue,
		float alpha
	) {
		drawBox(matrices, vertexConsumer, x1, y1, z1, x2, y2, z2, red, green, blue, alpha, red, green, blue);
	}

	/**
	 * Draws a box spanning from [x1,y1,z1] to [x2,y2,z2].
	 * The 3 axes centered at [x1,y1,z1] may be colored differently using xAxisRed, yAxisGreen, and zAxisBlue.
	 * 
	 * <p>Note the coordinates the box spans are relative to current translation of the matrices.
	 */
	public static void drawBox(
		MatrixStack matrices,
		VertexConsumer vertexConsumer,
		double x1,
		double y1,
		double z1,
		double x2,
		double y2,
		double z2,
		float red,
		float green,
		float blue,
		float alpha,
		float xAxisRed,
		float yAxisGreen,
		float zAxisBlue
	) {
		Matrix4f matrix4f = matrices.peek().getPositionMatrix();
		Matrix3f matrix3f = matrices.peek().getNormalMatrix();
		float f = (float)x1;
		float g = (float)y1;
		float h = (float)z1;
		float i = (float)x2;
		float j = (float)y2;
		float k = (float)z2;
		vertexConsumer.vertex(matrix4f, f, g, h).color(red, yAxisGreen, zAxisBlue, alpha).normal(matrix3f, 1.0F, 0.0F, 0.0F).next();
		vertexConsumer.vertex(matrix4f, i, g, h).color(red, yAxisGreen, zAxisBlue, alpha).normal(matrix3f, 1.0F, 0.0F, 0.0F).next();
		vertexConsumer.vertex(matrix4f, f, g, h).color(xAxisRed, green, zAxisBlue, alpha).normal(matrix3f, 0.0F, 1.0F, 0.0F).next();
		vertexConsumer.vertex(matrix4f, f, j, h).color(xAxisRed, green, zAxisBlue, alpha).normal(matrix3f, 0.0F, 1.0F, 0.0F).next();
		vertexConsumer.vertex(matrix4f, f, g, h).color(xAxisRed, yAxisGreen, blue, alpha).normal(matrix3f, 0.0F, 0.0F, 1.0F).next();
		vertexConsumer.vertex(matrix4f, f, g, k).color(xAxisRed, yAxisGreen, blue, alpha).normal(matrix3f, 0.0F, 0.0F, 1.0F).next();
		vertexConsumer.vertex(matrix4f, i, g, h).color(red, green, blue, alpha).normal(matrix3f, 0.0F, 1.0F, 0.0F).next();
		vertexConsumer.vertex(matrix4f, i, j, h).color(red, green, blue, alpha).normal(matrix3f, 0.0F, 1.0F, 0.0F).next();
		vertexConsumer.vertex(matrix4f, i, j, h).color(red, green, blue, alpha).normal(matrix3f, -1.0F, 0.0F, 0.0F).next();
		vertexConsumer.vertex(matrix4f, f, j, h).color(red, green, blue, alpha).normal(matrix3f, -1.0F, 0.0F, 0.0F).next();
		vertexConsumer.vertex(matrix4f, f, j, h).color(red, green, blue, alpha).normal(matrix3f, 0.0F, 0.0F, 1.0F).next();
		vertexConsumer.vertex(matrix4f, f, j, k).color(red, green, blue, alpha).normal(matrix3f, 0.0F, 0.0F, 1.0F).next();
		vertexConsumer.vertex(matrix4f, f, j, k).color(red, green, blue, alpha).normal(matrix3f, 0.0F, -1.0F, 0.0F).next();
		vertexConsumer.vertex(matrix4f, f, g, k).color(red, green, blue, alpha).normal(matrix3f, 0.0F, -1.0F, 0.0F).next();
		vertexConsumer.vertex(matrix4f, f, g, k).color(red, green, blue, alpha).normal(matrix3f, 1.0F, 0.0F, 0.0F).next();
		vertexConsumer.vertex(matrix4f, i, g, k).color(red, green, blue, alpha).normal(matrix3f, 1.0F, 0.0F, 0.0F).next();
		vertexConsumer.vertex(matrix4f, i, g, k).color(red, green, blue, alpha).normal(matrix3f, 0.0F, 0.0F, -1.0F).next();
		vertexConsumer.vertex(matrix4f, i, g, h).color(red, green, blue, alpha).normal(matrix3f, 0.0F, 0.0F, -1.0F).next();
		vertexConsumer.vertex(matrix4f, f, j, k).color(red, green, blue, alpha).normal(matrix3f, 1.0F, 0.0F, 0.0F).next();
		vertexConsumer.vertex(matrix4f, i, j, k).color(red, green, blue, alpha).normal(matrix3f, 1.0F, 0.0F, 0.0F).next();
		vertexConsumer.vertex(matrix4f, i, g, k).color(red, green, blue, alpha).normal(matrix3f, 0.0F, 1.0F, 0.0F).next();
		vertexConsumer.vertex(matrix4f, i, j, k).color(red, green, blue, alpha).normal(matrix3f, 0.0F, 1.0F, 0.0F).next();
		vertexConsumer.vertex(matrix4f, i, j, h).color(red, green, blue, alpha).normal(matrix3f, 0.0F, 0.0F, 1.0F).next();
		vertexConsumer.vertex(matrix4f, i, j, k).color(red, green, blue, alpha).normal(matrix3f, 0.0F, 0.0F, 1.0F).next();
	}

	public static void renderFilledBox(
		MatrixStack matrices,
		VertexConsumer vertexConsumer,
		double minX,
		double minY,
		double minZ,
		double maxX,
		double maxY,
		double maxZ,
		float red,
		float green,
		float blue,
		float alpha
	) {
		renderFilledBox(matrices, vertexConsumer, (float)minX, (float)minY, (float)minZ, (float)maxX, (float)maxY, (float)maxZ, red, green, blue, alpha);
	}

	public static void renderFilledBox(
		MatrixStack matrices,
		VertexConsumer vertexConsumer,
		float minX,
		float minY,
		float minZ,
		float maxX,
		float maxY,
		float maxZ,
		float red,
		float green,
		float blue,
		float alpha
	) {
		Matrix4f matrix4f = matrices.peek().getPositionMatrix();
		vertexConsumer.vertex(matrix4f, minX, minY, minZ).color(red, green, blue, alpha).next();
		vertexConsumer.vertex(matrix4f, minX, minY, minZ).color(red, green, blue, alpha).next();
		vertexConsumer.vertex(matrix4f, minX, minY, minZ).color(red, green, blue, alpha).next();
		vertexConsumer.vertex(matrix4f, minX, minY, maxZ).color(red, green, blue, alpha).next();
		vertexConsumer.vertex(matrix4f, minX, maxY, minZ).color(red, green, blue, alpha).next();
		vertexConsumer.vertex(matrix4f, minX, maxY, maxZ).color(red, green, blue, alpha).next();
		vertexConsumer.vertex(matrix4f, minX, maxY, maxZ).color(red, green, blue, alpha).next();
		vertexConsumer.vertex(matrix4f, minX, minY, maxZ).color(red, green, blue, alpha).next();
		vertexConsumer.vertex(matrix4f, maxX, maxY, maxZ).color(red, green, blue, alpha).next();
		vertexConsumer.vertex(matrix4f, maxX, minY, maxZ).color(red, green, blue, alpha).next();
		vertexConsumer.vertex(matrix4f, maxX, minY, maxZ).color(red, green, blue, alpha).next();
		vertexConsumer.vertex(matrix4f, maxX, minY, minZ).color(red, green, blue, alpha).next();
		vertexConsumer.vertex(matrix4f, maxX, maxY, maxZ).color(red, green, blue, alpha).next();
		vertexConsumer.vertex(matrix4f, maxX, maxY, minZ).color(red, green, blue, alpha).next();
		vertexConsumer.vertex(matrix4f, maxX, maxY, minZ).color(red, green, blue, alpha).next();
		vertexConsumer.vertex(matrix4f, maxX, minY, minZ).color(red, green, blue, alpha).next();
		vertexConsumer.vertex(matrix4f, minX, maxY, minZ).color(red, green, blue, alpha).next();
		vertexConsumer.vertex(matrix4f, minX, minY, minZ).color(red, green, blue, alpha).next();
		vertexConsumer.vertex(matrix4f, minX, minY, minZ).color(red, green, blue, alpha).next();
		vertexConsumer.vertex(matrix4f, maxX, minY, minZ).color(red, green, blue, alpha).next();
		vertexConsumer.vertex(matrix4f, minX, minY, maxZ).color(red, green, blue, alpha).next();
		vertexConsumer.vertex(matrix4f, maxX, minY, maxZ).color(red, green, blue, alpha).next();
		vertexConsumer.vertex(matrix4f, maxX, minY, maxZ).color(red, green, blue, alpha).next();
		vertexConsumer.vertex(matrix4f, minX, maxY, minZ).color(red, green, blue, alpha).next();
		vertexConsumer.vertex(matrix4f, minX, maxY, minZ).color(red, green, blue, alpha).next();
		vertexConsumer.vertex(matrix4f, minX, maxY, maxZ).color(red, green, blue, alpha).next();
		vertexConsumer.vertex(matrix4f, maxX, maxY, minZ).color(red, green, blue, alpha).next();
		vertexConsumer.vertex(matrix4f, maxX, maxY, maxZ).color(red, green, blue, alpha).next();
		vertexConsumer.vertex(matrix4f, maxX, maxY, maxZ).color(red, green, blue, alpha).next();
		vertexConsumer.vertex(matrix4f, maxX, maxY, maxZ).color(red, green, blue, alpha).next();
	}

	public void updateBlock(BlockView world, BlockPos pos, BlockState oldState, BlockState newState, int flags) {
		this.scheduleSectionRender(pos, (flags & 8) != 0);
	}

	private void scheduleSectionRender(BlockPos pos, boolean important) {
		for (int i = pos.getZ() - 1; i <= pos.getZ() + 1; i++) {
			for (int j = pos.getX() - 1; j <= pos.getX() + 1; j++) {
				for (int k = pos.getY() - 1; k <= pos.getY() + 1; k++) {
					this.scheduleChunkRender(ChunkSectionPos.getSectionCoord(j), ChunkSectionPos.getSectionCoord(k), ChunkSectionPos.getSectionCoord(i), important);
				}
			}
		}
	}

	public void scheduleBlockRenders(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
		for (int i = minZ - 1; i <= maxZ + 1; i++) {
			for (int j = minX - 1; j <= maxX + 1; j++) {
				for (int k = minY - 1; k <= maxY + 1; k++) {
					this.scheduleBlockRender(ChunkSectionPos.getSectionCoord(j), ChunkSectionPos.getSectionCoord(k), ChunkSectionPos.getSectionCoord(i));
				}
			}
		}
	}

	public void scheduleBlockRerenderIfNeeded(BlockPos pos, BlockState old, BlockState updated) {
		if (this.client.getBakedModelManager().shouldRerender(old, updated)) {
			this.scheduleBlockRenders(pos.getX(), pos.getY(), pos.getZ(), pos.getX(), pos.getY(), pos.getZ());
		}
	}

	public void scheduleBlockRenders(int x, int y, int z) {
		for (int i = z - 1; i <= z + 1; i++) {
			for (int j = x - 1; j <= x + 1; j++) {
				for (int k = y - 1; k <= y + 1; k++) {
					this.scheduleBlockRender(j, k, i);
				}
			}
		}
	}

	public void scheduleBlockRender(int x, int y, int z) {
		this.scheduleChunkRender(x, y, z, false);
	}

	private void scheduleChunkRender(int x, int y, int z, boolean important) {
		this.chunks.scheduleRebuild(x, y, z, important);
	}

	public void playSong(@Nullable SoundEvent song, BlockPos songPosition) {
		SoundInstance soundInstance = (SoundInstance)this.playingSongs.get(songPosition);
		if (soundInstance != null) {
			this.client.getSoundManager().stop(soundInstance);
			this.playingSongs.remove(songPosition);
		}

		if (song != null) {
			MusicDiscItem musicDiscItem = MusicDiscItem.bySound(song);
			if (musicDiscItem != null) {
				this.client.inGameHud.setRecordPlayingOverlay(musicDiscItem.getDescription());
			}

			SoundInstance var5 = PositionedSoundInstance.record(song, Vec3d.ofCenter(songPosition));
			this.playingSongs.put(songPosition, var5);
			this.client.getSoundManager().play(var5);
		}

		this.updateEntitiesForSong(this.world, songPosition, song != null);
	}

	private void updateEntitiesForSong(World world, BlockPos pos, boolean playing) {
		for (LivingEntity livingEntity : world.getNonSpectatingEntities(LivingEntity.class, new Box(pos).expand(3.0))) {
			livingEntity.setNearbySongPlaying(pos, playing);
		}
	}

	public void addParticle(
		ParticleEffect parameters, boolean shouldAlwaysSpawn, double x, double y, double z, double velocityX, double velocityY, double velocityZ
	) {
		this.addParticle(parameters, shouldAlwaysSpawn, false, x, y, z, velocityX, velocityY, velocityZ);
	}

	public void addParticle(
		ParticleEffect parameters, boolean shouldAlwaysSpawn, boolean important, double x, double y, double z, double velocityX, double velocityY, double velocityZ
	) {
		try {
			this.spawnParticle(parameters, shouldAlwaysSpawn, important, x, y, z, velocityX, velocityY, velocityZ);
		} catch (Throwable var19) {
			CrashReport crashReport = CrashReport.create(var19, "Exception while adding particle");
			CrashReportSection crashReportSection = crashReport.addElement("Particle being added");
			crashReportSection.add("ID", Registries.PARTICLE_TYPE.getId(parameters.getType()));
			crashReportSection.add("Parameters", parameters.asString());
			crashReportSection.add("Position", (CrashCallable<String>)(() -> CrashReportSection.createPositionString(this.world, x, y, z)));
			throw new CrashException(crashReport);
		}
	}

	private <T extends ParticleEffect> void addParticle(T parameters, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
		this.addParticle(parameters, parameters.getType().shouldAlwaysSpawn(), x, y, z, velocityX, velocityY, velocityZ);
	}

	@Nullable
	private Particle spawnParticle(
		ParticleEffect parameters, boolean alwaysSpawn, double x, double y, double z, double velocityX, double velocityY, double velocityZ
	) {
		return this.spawnParticle(parameters, alwaysSpawn, false, x, y, z, velocityX, velocityY, velocityZ);
	}

	@Nullable
	private Particle spawnParticle(
		ParticleEffect parameters, boolean alwaysSpawn, boolean canSpawnOnMinimal, double x, double y, double z, double velocityX, double velocityY, double velocityZ
	) {
		Camera camera = this.client.gameRenderer.getCamera();
		ParticlesMode particlesMode = this.getRandomParticleSpawnChance(canSpawnOnMinimal);
		if (alwaysSpawn) {
			return this.client.particleManager.addParticle(parameters, x, y, z, velocityX, velocityY, velocityZ);
		} else if (camera.getPos().squaredDistanceTo(x, y, z) > 1024.0) {
			return null;
		} else {
			return particlesMode == ParticlesMode.MINIMAL ? null : this.client.particleManager.addParticle(parameters, x, y, z, velocityX, velocityY, velocityZ);
		}
	}

	private ParticlesMode getRandomParticleSpawnChance(boolean canSpawnOnMinimal) {
		ParticlesMode particlesMode = this.client.options.getParticles().getValue();
		if (canSpawnOnMinimal && particlesMode == ParticlesMode.MINIMAL && this.world.random.nextInt(10) == 0) {
			particlesMode = ParticlesMode.DECREASED;
		}

		if (particlesMode == ParticlesMode.DECREASED && this.world.random.nextInt(3) == 0) {
			particlesMode = ParticlesMode.MINIMAL;
		}

		return particlesMode;
	}

	public void cleanUp() {
	}

	public void processGlobalEvent(int eventId, BlockPos pos, int data) {
		switch (eventId) {
			case 1023:
			case 1028:
			case 1038:
				Camera camera = this.client.gameRenderer.getCamera();
				if (camera.isReady()) {
					double d = (double)pos.getX() - camera.getPos().x;
					double e = (double)pos.getY() - camera.getPos().y;
					double f = (double)pos.getZ() - camera.getPos().z;
					double g = Math.sqrt(d * d + e * e + f * f);
					double h = camera.getPos().x;
					double i = camera.getPos().y;
					double j = camera.getPos().z;
					if (g > 0.0) {
						h += d / g * 2.0;
						i += e / g * 2.0;
						j += f / g * 2.0;
					}

					if (eventId == WorldEvents.WITHER_SPAWNS) {
						this.world.playSound(h, i, j, SoundEvents.ENTITY_WITHER_SPAWN, SoundCategory.HOSTILE, 1.0F, 1.0F, false);
					} else if (eventId == WorldEvents.END_PORTAL_OPENED) {
						this.world.playSound(h, i, j, SoundEvents.BLOCK_END_PORTAL_SPAWN, SoundCategory.HOSTILE, 1.0F, 1.0F, false);
					} else {
						this.world.playSound(h, i, j, SoundEvents.ENTITY_ENDER_DRAGON_DEATH, SoundCategory.HOSTILE, 5.0F, 1.0F, false);
					}
				}
		}
	}

	public void processWorldEvent(int eventId, BlockPos pos, int data) {
		Random random = this.world.random;
		switch (eventId) {
			case 1000:
				this.world.playSoundAtBlockCenter(pos, SoundEvents.BLOCK_DISPENSER_DISPENSE, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
				break;
			case 1001:
				this.world.playSoundAtBlockCenter(pos, SoundEvents.BLOCK_DISPENSER_FAIL, SoundCategory.BLOCKS, 1.0F, 1.2F, false);
				break;
			case 1002:
				this.world.playSoundAtBlockCenter(pos, SoundEvents.BLOCK_DISPENSER_LAUNCH, SoundCategory.BLOCKS, 1.0F, 1.2F, false);
				break;
			case 1003:
				this.world.playSoundAtBlockCenter(pos, SoundEvents.ENTITY_ENDER_EYE_LAUNCH, SoundCategory.NEUTRAL, 1.0F, 1.2F, false);
				break;
			case 1004:
				this.world.playSoundAtBlockCenter(pos, SoundEvents.ENTITY_FIREWORK_ROCKET_SHOOT, SoundCategory.NEUTRAL, 1.0F, 1.2F, false);
				break;
			case 1009:
				if (data == 0) {
					this.world
						.playSoundAtBlockCenter(
							pos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.5F, 2.6F + (random.nextFloat() - random.nextFloat()) * 0.8F, false
						);
				} else if (data == 1) {
					this.world
						.playSoundAtBlockCenter(
							pos, SoundEvents.ENTITY_GENERIC_EXTINGUISH_FIRE, SoundCategory.BLOCKS, 0.7F, 1.6F + (random.nextFloat() - random.nextFloat()) * 0.4F, false
						);
				}
				break;
			case 1010:
				if (Item.byRawId(data) instanceof MusicDiscItem musicDiscItem) {
					this.playSong(musicDiscItem.getSound(), pos);
				}
				break;
			case 1011:
				this.playSong(null, pos);
				break;
			case 1015:
				this.world
					.playSoundAtBlockCenter(pos, SoundEvents.ENTITY_GHAST_WARN, SoundCategory.HOSTILE, 10.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
				break;
			case 1016:
				this.world
					.playSoundAtBlockCenter(pos, SoundEvents.ENTITY_GHAST_SHOOT, SoundCategory.HOSTILE, 10.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
				break;
			case 1017:
				this.world
					.playSoundAtBlockCenter(
						pos, SoundEvents.ENTITY_ENDER_DRAGON_SHOOT, SoundCategory.HOSTILE, 10.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false
					);
				break;
			case 1018:
				this.world
					.playSoundAtBlockCenter(pos, SoundEvents.ENTITY_BLAZE_SHOOT, SoundCategory.HOSTILE, 2.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
				break;
			case 1019:
				this.world
					.playSoundAtBlockCenter(
						pos, SoundEvents.ENTITY_ZOMBIE_ATTACK_WOODEN_DOOR, SoundCategory.HOSTILE, 2.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false
					);
				break;
			case 1020:
				this.world
					.playSoundAtBlockCenter(
						pos, SoundEvents.ENTITY_ZOMBIE_ATTACK_IRON_DOOR, SoundCategory.HOSTILE, 2.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false
					);
				break;
			case 1021:
				this.world
					.playSoundAtBlockCenter(
						pos, SoundEvents.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR, SoundCategory.HOSTILE, 2.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false
					);
				break;
			case 1022:
				this.world
					.playSoundAtBlockCenter(
						pos, SoundEvents.ENTITY_WITHER_BREAK_BLOCK, SoundCategory.HOSTILE, 2.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false
					);
				break;
			case 1024:
				this.world
					.playSoundAtBlockCenter(pos, SoundEvents.ENTITY_WITHER_SHOOT, SoundCategory.HOSTILE, 2.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
				break;
			case 1025:
				this.world
					.playSoundAtBlockCenter(pos, SoundEvents.ENTITY_BAT_TAKEOFF, SoundCategory.NEUTRAL, 0.05F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
				break;
			case 1026:
				this.world
					.playSoundAtBlockCenter(pos, SoundEvents.ENTITY_ZOMBIE_INFECT, SoundCategory.HOSTILE, 2.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
				break;
			case 1027:
				this.world
					.playSoundAtBlockCenter(
						pos, SoundEvents.ENTITY_ZOMBIE_VILLAGER_CONVERTED, SoundCategory.HOSTILE, 2.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false
					);
				break;
			case 1029:
				this.world.playSoundAtBlockCenter(pos, SoundEvents.BLOCK_ANVIL_DESTROY, SoundCategory.BLOCKS, 1.0F, random.nextFloat() * 0.1F + 0.9F, false);
				break;
			case 1030:
				this.world.playSoundAtBlockCenter(pos, SoundEvents.BLOCK_ANVIL_USE, SoundCategory.BLOCKS, 1.0F, random.nextFloat() * 0.1F + 0.9F, false);
				break;
			case 1031:
				this.world.playSoundAtBlockCenter(pos, SoundEvents.BLOCK_ANVIL_LAND, SoundCategory.BLOCKS, 0.3F, this.world.random.nextFloat() * 0.1F + 0.9F, false);
				break;
			case 1032:
				this.client.getSoundManager().play(PositionedSoundInstance.ambient(SoundEvents.BLOCK_PORTAL_TRAVEL, random.nextFloat() * 0.4F + 0.8F, 0.25F));
				break;
			case 1033:
				this.world.playSoundAtBlockCenter(pos, SoundEvents.BLOCK_CHORUS_FLOWER_GROW, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
				break;
			case 1034:
				this.world.playSoundAtBlockCenter(pos, SoundEvents.BLOCK_CHORUS_FLOWER_DEATH, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
				break;
			case 1035:
				this.world.playSoundAtBlockCenter(pos, SoundEvents.BLOCK_BREWING_STAND_BREW, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
				break;
			case 1039:
				this.world.playSoundAtBlockCenter(pos, SoundEvents.ENTITY_PHANTOM_BITE, SoundCategory.HOSTILE, 0.3F, this.world.random.nextFloat() * 0.1F + 0.9F, false);
				break;
			case 1040:
				this.world
					.playSoundAtBlockCenter(
						pos, SoundEvents.ENTITY_ZOMBIE_CONVERTED_TO_DROWNED, SoundCategory.HOSTILE, 2.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false
					);
				break;
			case 1041:
				this.world
					.playSoundAtBlockCenter(
						pos, SoundEvents.ENTITY_HUSK_CONVERTED_TO_ZOMBIE, SoundCategory.HOSTILE, 2.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false
					);
				break;
			case 1042:
				this.world.playSoundAtBlockCenter(pos, SoundEvents.BLOCK_GRINDSTONE_USE, SoundCategory.BLOCKS, 1.0F, this.world.random.nextFloat() * 0.1F + 0.9F, false);
				break;
			case 1043:
				this.world.playSoundAtBlockCenter(pos, SoundEvents.ITEM_BOOK_PAGE_TURN, SoundCategory.BLOCKS, 1.0F, this.world.random.nextFloat() * 0.1F + 0.9F, false);
				break;
			case 1044:
				this.world
					.playSoundAtBlockCenter(pos, SoundEvents.BLOCK_SMITHING_TABLE_USE, SoundCategory.BLOCKS, 1.0F, this.world.random.nextFloat() * 0.1F + 0.9F, false);
				break;
			case 1045:
				this.world
					.playSoundAtBlockCenter(pos, SoundEvents.BLOCK_POINTED_DRIPSTONE_LAND, SoundCategory.BLOCKS, 2.0F, this.world.random.nextFloat() * 0.1F + 0.9F, false);
				break;
			case 1046:
				this.world
					.playSoundAtBlockCenter(
						pos, SoundEvents.BLOCK_POINTED_DRIPSTONE_DRIP_LAVA_INTO_CAULDRON, SoundCategory.BLOCKS, 2.0F, this.world.random.nextFloat() * 0.1F + 0.9F, false
					);
				break;
			case 1047:
				this.world
					.playSoundAtBlockCenter(
						pos, SoundEvents.BLOCK_POINTED_DRIPSTONE_DRIP_WATER_INTO_CAULDRON, SoundCategory.BLOCKS, 2.0F, this.world.random.nextFloat() * 0.1F + 0.9F, false
					);
				break;
			case 1048:
				this.world
					.playSoundAtBlockCenter(
						pos, SoundEvents.ENTITY_SKELETON_CONVERTED_TO_STRAY, SoundCategory.HOSTILE, 2.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false
					);
				break;
			case 1500:
				ComposterBlock.playEffects(this.world, pos, data > 0);
				break;
			case 1501:
				this.world
					.playSoundAtBlockCenter(pos, SoundEvents.BLOCK_LAVA_EXTINGUISH, SoundCategory.BLOCKS, 0.5F, 2.6F + (random.nextFloat() - random.nextFloat()) * 0.8F, false);

				for (int xx = 0; xx < 8; xx++) {
					this.world
						.addParticle(
							ParticleTypes.LARGE_SMOKE, (double)pos.getX() + random.nextDouble(), (double)pos.getY() + 1.2, (double)pos.getZ() + random.nextDouble(), 0.0, 0.0, 0.0
						);
				}
				break;
			case 1502:
				this.world
					.playSoundAtBlockCenter(
						pos, SoundEvents.BLOCK_REDSTONE_TORCH_BURNOUT, SoundCategory.BLOCKS, 0.5F, 2.6F + (random.nextFloat() - random.nextFloat()) * 0.8F, false
					);

				for (int xx = 0; xx < 5; xx++) {
					double e = (double)pos.getX() + random.nextDouble() * 0.6 + 0.2;
					double f = (double)pos.getY() + random.nextDouble() * 0.6 + 0.2;
					double y = (double)pos.getZ() + random.nextDouble() * 0.6 + 0.2;
					this.world.addParticle(ParticleTypes.SMOKE, e, f, y, 0.0, 0.0, 0.0);
				}
				break;
			case 1503:
				this.world.playSoundAtBlockCenter(pos, SoundEvents.BLOCK_END_PORTAL_FRAME_FILL, SoundCategory.BLOCKS, 1.0F, 1.0F, false);

				for (int xx = 0; xx < 16; xx++) {
					double e = (double)pos.getX() + (5.0 + random.nextDouble() * 6.0) / 16.0;
					double f = (double)pos.getY() + 0.8125;
					double y = (double)pos.getZ() + (5.0 + random.nextDouble() * 6.0) / 16.0;
					this.world.addParticle(ParticleTypes.SMOKE, e, f, y, 0.0, 0.0, 0.0);
				}
				break;
			case 1504:
				PointedDripstoneBlock.createParticle(this.world, pos, this.world.getBlockState(pos));
				break;
			case 1505:
				BoneMealItem.createParticles(this.world, pos, data);
				this.world.playSoundAtBlockCenter(pos, SoundEvents.ITEM_BONE_MEAL_USE, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
				break;
			case 2000:
				Direction direction = Direction.byId(data);
				int i = direction.getOffsetX();
				int jx = direction.getOffsetY();
				int k = direction.getOffsetZ();
				double d = (double)pos.getX() + (double)i * 0.6 + 0.5;
				double e = (double)pos.getY() + (double)jx * 0.6 + 0.5;
				double f = (double)pos.getZ() + (double)k * 0.6 + 0.5;

				for (int l = 0; l < 10; l++) {
					double g = random.nextDouble() * 0.2 + 0.01;
					double h = d + (double)i * 0.01 + (random.nextDouble() - 0.5) * (double)k * 0.5;
					double m = e + (double)jx * 0.01 + (random.nextDouble() - 0.5) * (double)jx * 0.5;
					double n = f + (double)k * 0.01 + (random.nextDouble() - 0.5) * (double)i * 0.5;
					double o = (double)i * g + random.nextGaussian() * 0.01;
					double p = (double)jx * g + random.nextGaussian() * 0.01;
					double q = (double)k * g + random.nextGaussian() * 0.01;
					this.addParticle(ParticleTypes.SMOKE, h, m, n, o, p, q);
				}
				break;
			case 2001:
				BlockState blockState = Block.getStateFromRawId(data);
				if (!blockState.isAir()) {
					BlockSoundGroup blockSoundGroup = blockState.getSoundGroup();
					this.world
						.playSoundAtBlockCenter(
							pos, blockSoundGroup.getBreakSound(), SoundCategory.BLOCKS, (blockSoundGroup.getVolume() + 1.0F) / 2.0F, blockSoundGroup.getPitch() * 0.8F, false
						);
				}

				this.world.addBlockBreakParticles(pos, blockState);
				break;
			case 2002:
			case 2007:
				Vec3d vec3d = Vec3d.ofBottomCenter(pos);

				for (int i = 0; i < 8; i++) {
					this.addParticle(
						new ItemStackParticleEffect(ParticleTypes.ITEM, new ItemStack(Items.SPLASH_POTION)),
						vec3d.x,
						vec3d.y,
						vec3d.z,
						random.nextGaussian() * 0.15,
						random.nextDouble() * 0.2,
						random.nextGaussian() * 0.15
					);
				}

				float u = (float)(data >> 16 & 0xFF) / 255.0F;
				float v = (float)(data >> 8 & 0xFF) / 255.0F;
				float w = (float)(data >> 0 & 0xFF) / 255.0F;
				ParticleEffect particleEffect = eventId == 2007 ? ParticleTypes.INSTANT_EFFECT : ParticleTypes.EFFECT;

				for (int xx = 0; xx < 100; xx++) {
					double e = random.nextDouble() * 4.0;
					double f = random.nextDouble() * Math.PI * 2.0;
					double y = Math.cos(f) * e;
					double z = 0.01 + random.nextDouble() * 0.5;
					double aa = Math.sin(f) * e;
					Particle particle = this.spawnParticle(
						particleEffect, particleEffect.getType().shouldAlwaysSpawn(), vec3d.x + y * 0.1, vec3d.y + 0.3, vec3d.z + aa * 0.1, y, z, aa
					);
					if (particle != null) {
						float ab = 0.75F + random.nextFloat() * 0.25F;
						particle.setColor(u * ab, v * ab, w * ab);
						particle.move((float)e);
					}
				}

				this.world.playSoundAtBlockCenter(pos, SoundEvents.ENTITY_SPLASH_POTION_BREAK, SoundCategory.NEUTRAL, 1.0F, random.nextFloat() * 0.1F + 0.9F, false);
				break;
			case 2003:
				double r = (double)pos.getX() + 0.5;
				double s = (double)pos.getY();
				double d = (double)pos.getZ() + 0.5;

				for (int t = 0; t < 8; t++) {
					this.addParticle(
						new ItemStackParticleEffect(ParticleTypes.ITEM, new ItemStack(Items.ENDER_EYE)),
						r,
						s,
						d,
						random.nextGaussian() * 0.15,
						random.nextDouble() * 0.2,
						random.nextGaussian() * 0.15
					);
				}

				for (double e = 0.0; e < Math.PI * 2; e += Math.PI / 20) {
					this.addParticle(ParticleTypes.PORTAL, r + Math.cos(e) * 5.0, s - 0.4, d + Math.sin(e) * 5.0, Math.cos(e) * -5.0, 0.0, Math.sin(e) * -5.0);
					this.addParticle(ParticleTypes.PORTAL, r + Math.cos(e) * 5.0, s - 0.4, d + Math.sin(e) * 5.0, Math.cos(e) * -7.0, 0.0, Math.sin(e) * -7.0);
				}
				break;
			case 2004:
				for (int jx = 0; jx < 20; jx++) {
					double ac = (double)pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 2.0;
					double ad = (double)pos.getY() + 0.5 + (random.nextDouble() - 0.5) * 2.0;
					double ae = (double)pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 2.0;
					this.world.addParticle(ParticleTypes.SMOKE, ac, ad, ae, 0.0, 0.0, 0.0);
					this.world.addParticle(ParticleTypes.FLAME, ac, ad, ae, 0.0, 0.0, 0.0);
				}
				break;
			case 2005:
				BoneMealItem.createParticles(this.world, pos, data);
				break;
			case 2006:
				for (int x = 0; x < 200; x++) {
					float ak = random.nextFloat() * 4.0F;
					float ao = random.nextFloat() * (float) (Math.PI * 2);
					double f = (double)(MathHelper.cos(ao) * ak);
					double y = 0.01 + random.nextDouble() * 0.5;
					double z = (double)(MathHelper.sin(ao) * ak);
					Particle particle2 = this.spawnParticle(
						ParticleTypes.DRAGON_BREATH, false, (double)pos.getX() + f * 0.1, (double)pos.getY() + 0.3, (double)pos.getZ() + z * 0.1, f, y, z
					);
					if (particle2 != null) {
						particle2.move(ak);
					}
				}

				if (data == 1) {
					this.world.playSoundAtBlockCenter(pos, SoundEvents.ENTITY_DRAGON_FIREBALL_EXPLODE, SoundCategory.HOSTILE, 1.0F, random.nextFloat() * 0.1F + 0.9F, false);
				}
				break;
			case 2008:
				this.world.addParticle(ParticleTypes.EXPLOSION, (double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, 0.0, 0.0, 0.0);
				break;
			case 2009:
				for (int xx = 0; xx < 8; xx++) {
					this.world
						.addParticle(
							ParticleTypes.CLOUD, (double)pos.getX() + random.nextDouble(), (double)pos.getY() + 1.2, (double)pos.getZ() + random.nextDouble(), 0.0, 0.0, 0.0
						);
				}
				break;
			case 3000:
				this.world.addParticle(ParticleTypes.EXPLOSION_EMITTER, true, (double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, 0.0, 0.0, 0.0);
				this.world
					.playSoundAtBlockCenter(
						pos,
						SoundEvents.BLOCK_END_GATEWAY_SPAWN,
						SoundCategory.BLOCKS,
						10.0F,
						(1.0F + (this.world.random.nextFloat() - this.world.random.nextFloat()) * 0.2F) * 0.7F,
						false
					);
				break;
			case 3001:
				this.world
					.playSoundAtBlockCenter(pos, SoundEvents.ENTITY_ENDER_DRAGON_GROWL, SoundCategory.HOSTILE, 64.0F, 0.8F + this.world.random.nextFloat() * 0.3F, false);
				break;
			case 3002:
				if (data >= 0 && data < Direction.Axis.VALUES.length) {
					ParticleUtil.spawnParticle(Direction.Axis.VALUES[data], this.world, pos, 0.125, ParticleTypes.ELECTRIC_SPARK, UniformIntProvider.create(10, 19));
				} else {
					ParticleUtil.spawnParticle(this.world, pos, ParticleTypes.ELECTRIC_SPARK, UniformIntProvider.create(3, 5));
				}
				break;
			case 3003:
				ParticleUtil.spawnParticle(this.world, pos, ParticleTypes.WAX_ON, UniformIntProvider.create(3, 5));
				this.world.playSoundAtBlockCenter(pos, SoundEvents.ITEM_HONEYCOMB_WAX_ON, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
				break;
			case 3004:
				ParticleUtil.spawnParticle(this.world, pos, ParticleTypes.WAX_OFF, UniformIntProvider.create(3, 5));
				break;
			case 3005:
				ParticleUtil.spawnParticle(this.world, pos, ParticleTypes.SCRAPE, UniformIntProvider.create(3, 5));
				break;
			case 3006:
				int j = data >> 6;
				if (j > 0) {
					if (random.nextFloat() < 0.3F + (float)j * 0.1F) {
						float w = 0.15F + 0.02F * (float)j * (float)j * random.nextFloat();
						float af = 0.4F + 0.3F * (float)j * random.nextFloat();
						this.world.playSoundAtBlockCenter(pos, SoundEvents.BLOCK_SCULK_CHARGE, SoundCategory.BLOCKS, w, af, false);
					}

					byte b = (byte)(data & 63);
					IntProvider intProvider = UniformIntProvider.create(0, j);
					float ag = 0.005F;
					Supplier<Vec3d> supplier = () -> new Vec3d(
							MathHelper.nextDouble(random, -0.005F, 0.005F), MathHelper.nextDouble(random, -0.005F, 0.005F), MathHelper.nextDouble(random, -0.005F, 0.005F)
						);
					if (b == 0) {
						for (Direction direction2 : Direction.values()) {
							float ah = direction2 == Direction.DOWN ? (float) Math.PI : 0.0F;
							double z = direction2.getAxis() == Direction.Axis.Y ? 0.65 : 0.57;
							ParticleUtil.spawnParticles(this.world, pos, new SculkChargeParticleEffect(ah), intProvider, direction2, supplier, z);
						}
					} else {
						for (Direction direction3 : MultifaceGrowthBlock.flagToDirections(b)) {
							float ai = direction3 == Direction.UP ? (float) Math.PI : 0.0F;
							double y = 0.35;
							ParticleUtil.spawnParticles(this.world, pos, new SculkChargeParticleEffect(ai), intProvider, direction3, supplier, 0.35);
						}
					}
				} else {
					this.world.playSoundAtBlockCenter(pos, SoundEvents.BLOCK_SCULK_CHARGE, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
					boolean bl = this.world.getBlockState(pos).isFullCube(this.world, pos);
					int aj = bl ? 40 : 20;
					float ag = bl ? 0.45F : 0.25F;
					float ak = 0.07F;

					for (int al = 0; al < aj; al++) {
						float am = 2.0F * random.nextFloat() - 1.0F;
						float ai = 2.0F * random.nextFloat() - 1.0F;
						float an = 2.0F * random.nextFloat() - 1.0F;
						this.world
							.addParticle(
								ParticleTypes.SCULK_CHARGE_POP,
								(double)pos.getX() + 0.5 + (double)(am * ag),
								(double)pos.getY() + 0.5 + (double)(ai * ag),
								(double)pos.getZ() + 0.5 + (double)(an * ag),
								(double)(am * 0.07F),
								(double)(ai * 0.07F),
								(double)(an * 0.07F)
							);
					}
				}
				break;
			case 3007:
				for (int k = 0; k < 10; k++) {
					this.world
						.addParticle(
							new ShriekParticleEffect(k * 5), false, (double)pos.getX() + 0.5, (double)pos.getY() + SculkShriekerBlock.TOP, (double)pos.getZ() + 0.5, 0.0, 0.0, 0.0
						);
				}

				BlockState blockState3 = this.world.getBlockState(pos);
				boolean bl2 = blockState3.contains(Properties.WATERLOGGED) && (Boolean)blockState3.get(Properties.WATERLOGGED);
				if (!bl2) {
					this.world
						.playSound(
							(double)pos.getX() + 0.5,
							(double)pos.getY() + SculkShriekerBlock.TOP,
							(double)pos.getZ() + 0.5,
							SoundEvents.BLOCK_SCULK_SHRIEKER_SHRIEK,
							SoundCategory.BLOCKS,
							2.0F,
							0.6F + this.world.random.nextFloat() * 0.4F,
							false
						);
				}
				break;
			case 3008:
				BlockState blockState2 = Block.getStateFromRawId(data);
				if (blockState2.getBlock() instanceof BrushableBlock brushableBlock) {
					this.world.playSoundAtBlockCenter(pos, brushableBlock.getBrushingCompleteSound(), SoundCategory.PLAYERS, 1.0F, 1.0F, false);
				}

				this.world.addBlockBreakParticles(pos, blockState2);
				break;
			case 3009:
				ParticleUtil.spawnParticle(this.world, pos, ParticleTypes.EGG_CRACK, UniformIntProvider.create(3, 6));
		}
	}

	public void setBlockBreakingInfo(int entityId, BlockPos pos, int stage) {
		if (stage >= 0 && stage < 10) {
			BlockBreakingInfo blockBreakingInfo = this.blockBreakingInfos.get(entityId);
			if (blockBreakingInfo != null) {
				this.removeBlockBreakingInfo(blockBreakingInfo);
			}

			if (blockBreakingInfo == null
				|| blockBreakingInfo.getPos().getX() != pos.getX()
				|| blockBreakingInfo.getPos().getY() != pos.getY()
				|| blockBreakingInfo.getPos().getZ() != pos.getZ()) {
				blockBreakingInfo = new BlockBreakingInfo(entityId, pos);
				this.blockBreakingInfos.put(entityId, blockBreakingInfo);
			}

			blockBreakingInfo.setStage(stage);
			blockBreakingInfo.setLastUpdateTick(this.ticks);
			this.blockBreakingProgressions
				.computeIfAbsent(
					blockBreakingInfo.getPos().asLong(), (Long2ObjectFunction<? extends SortedSet<BlockBreakingInfo>>)(l -> Sets.<BlockBreakingInfo>newTreeSet())
				)
				.add(blockBreakingInfo);
		} else {
			BlockBreakingInfo blockBreakingInfox = this.blockBreakingInfos.remove(entityId);
			if (blockBreakingInfox != null) {
				this.removeBlockBreakingInfo(blockBreakingInfox);
			}
		}
	}

	public boolean isTerrainRenderComplete() {
		return this.field_45614.isEmpty();
	}

	public void method_52815(ChunkPos chunkPos) {
		this.field_45615.method_52819(chunkPos);
	}

	public void scheduleTerrainUpdate() {
		this.field_45615.method_52817();
		this.cloudsDirty = true;
	}

	public void updateNoCullingBlockEntities(Collection<BlockEntity> removed, Collection<BlockEntity> added) {
		synchronized (this.noCullingBlockEntities) {
			this.noCullingBlockEntities.removeAll(removed);
			this.noCullingBlockEntities.addAll(added);
		}
	}

	public static int getLightmapCoordinates(BlockRenderView world, BlockPos pos) {
		return getLightmapCoordinates(world, world.getBlockState(pos), pos);
	}

	public static int getLightmapCoordinates(BlockRenderView world, BlockState state, BlockPos pos) {
		if (state.hasEmissiveLighting(world, pos)) {
			return 15728880;
		} else {
			int i = world.getLightLevel(LightType.SKY, pos);
			int j = world.getLightLevel(LightType.BLOCK, pos);
			int k = state.getLuminance();
			if (j < k) {
				j = k;
			}

			return i << 20 | j << 4;
		}
	}

	public boolean isRenderingReady(BlockPos pos) {
		ChunkBuilder.BuiltChunk builtChunk = this.chunks.getRenderedChunk(pos);
		return builtChunk != null && builtChunk.data.get() != ChunkBuilder.ChunkData.EMPTY;
	}

	@Nullable
	public Framebuffer getEntityOutlinesFramebuffer() {
		return this.entityOutlinesFramebuffer;
	}

	@Nullable
	public Framebuffer getTranslucentFramebuffer() {
		return this.translucentFramebuffer;
	}

	@Nullable
	public Framebuffer getEntityFramebuffer() {
		return this.entityFramebuffer;
	}

	@Nullable
	public Framebuffer getParticlesFramebuffer() {
		return this.particlesFramebuffer;
	}

	@Nullable
	public Framebuffer getWeatherFramebuffer() {
		return this.weatherFramebuffer;
	}

	@Nullable
	public Framebuffer getCloudsFramebuffer() {
		return this.cloudsFramebuffer;
	}

	@Environment(EnvType.CLIENT)
	public static class ProgramInitException extends RuntimeException {
		public ProgramInitException(String message, Throwable cause) {
			super(message, cause);
		}
	}
}
