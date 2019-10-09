package net.minecraft.client.render;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap.Entry;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.SortedSet;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4618;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.ComposterBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.GlBuffer;
import net.minecraft.client.gl.GlFramebuffer;
import net.minecraft.client.gl.ShaderEffect;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.options.CloudRenderMode;
import net.minecraft.client.options.ParticlesOption;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.chunk.ChunkBatcher;
import net.minecraft.client.render.chunk.ChunkOcclusionGraphBuilder;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.client.util.math.Vector4f;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.BoneMealItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.MusicDiscItem;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SynchronousResourceReloadListener;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.crash.CrashCallable;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.MatrixStack;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.dimension.DimensionType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class WorldRenderer implements AutoCloseable, SynchronousResourceReloadListener {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Identifier MOON_PHASES_TEX = new Identifier("textures/environment/moon_phases.png");
	private static final Identifier SUN_TEX = new Identifier("textures/environment/sun.png");
	private static final Identifier CLOUDS_TEX = new Identifier("textures/environment/clouds.png");
	private static final Identifier END_SKY_TEX = new Identifier("textures/environment/end_sky.png");
	private static final Identifier FORCEFIELD_TEX = new Identifier("textures/misc/forcefield.png");
	private static final Identifier field_20797 = new Identifier("textures/environment/rain.png");
	private static final Identifier field_20798 = new Identifier("textures/environment/snow.png");
	public static final Direction[] DIRECTIONS = Direction.values();
	private final MinecraftClient client;
	private final TextureManager textureManager;
	private final EntityRenderDispatcher entityRenderDispatcher;
	private final LayeredBufferBuilderStorage field_20951;
	private final BackgroundRenderer chunkRendererList;
	private ClientWorld world;
	private Set<ChunkBatcher.ChunkRenderer> chunkRenderers = Sets.<ChunkBatcher.ChunkRenderer>newLinkedHashSet();
	private List<WorldRenderer.ChunkInfo> chunkInfos = Lists.<WorldRenderer.ChunkInfo>newArrayListWithCapacity(69696);
	private final Set<BlockEntity> blockEntities = Sets.<BlockEntity>newHashSet();
	private ChunkRenderDispatcher chunkRenderDispatcher;
	private final VertexFormat field_4100 = VertexFormats.POSITION;
	private GlBuffer starsBuffer;
	private GlBuffer field_4087;
	private GlBuffer field_4102;
	private boolean cloudsDirty = true;
	private GlBuffer cloudsBuffer;
	private int ticks;
	private final Int2ObjectMap<PartiallyBrokenBlockEntry> partiallyBrokenBlocks = new Int2ObjectOpenHashMap<>();
	private final Long2ObjectMap<SortedSet<PartiallyBrokenBlockEntry>> field_20950 = new Long2ObjectOpenHashMap<>();
	private final Map<BlockPos, SoundInstance> playingSongs = Maps.<BlockPos, SoundInstance>newHashMap();
	private GlFramebuffer entityOutlinesFramebuffer;
	private ShaderEffect entityOutlineShader;
	private double lastCameraChunkUpdateX = Double.MIN_VALUE;
	private double lastCameraChunkUpdateY = Double.MIN_VALUE;
	private double lastCameraChunkUpdateZ = Double.MIN_VALUE;
	private int cameraChunkX = Integer.MIN_VALUE;
	private int cameraChunkY = Integer.MIN_VALUE;
	private int cameraChunkZ = Integer.MIN_VALUE;
	private double lastCameraX = Double.MIN_VALUE;
	private double lastCameraY = Double.MIN_VALUE;
	private double lastCameraZ = Double.MIN_VALUE;
	private double lastCameraPitch = Double.MIN_VALUE;
	private double lastCameraYaw = Double.MIN_VALUE;
	private int field_4082 = Integer.MIN_VALUE;
	private int field_4097 = Integer.MIN_VALUE;
	private int field_4116 = Integer.MIN_VALUE;
	private net.minecraft.util.math.Vec3d field_4072 = net.minecraft.util.math.Vec3d.ZERO;
	private CloudRenderMode field_4080;
	private ChunkBatcher chunkBatcher;
	private final VertexFormat field_20791 = VertexFormats.POSITION_COLOR_UV_NORMAL;
	private int renderDistance = -1;
	private int regularEntityCount;
	private int blockEntityCount;
	private boolean shouldCaptureFrustum;
	@Nullable
	private Frustum forcedFrustum;
	private final Vector4f[] field_4065 = new Vector4f[8];
	private final Vec3d forcedFrustumPosition = new Vec3d(0.0, 0.0, 0.0);
	private double lastTranslucentSortX;
	private double lastTranslucentSortY;
	private double lastTranslucentSortZ;
	private boolean terrainUpdateNecessary = true;
	private int field_20792;
	private int field_20793;
	private final float[] field_20794 = new float[1024];
	private final float[] field_20795 = new float[1024];

	public WorldRenderer(MinecraftClient minecraftClient, LayeredBufferBuilderStorage layeredBufferBuilderStorage) {
		this.client = minecraftClient;
		this.entityRenderDispatcher = minecraftClient.getEntityRenderManager();
		this.field_20951 = layeredBufferBuilderStorage;
		this.chunkRendererList = new BackgroundRenderer();
		this.textureManager = minecraftClient.getTextureManager();

		for (int i = 0; i < 32; i++) {
			for (int j = 0; j < 32; j++) {
				float f = (float)(j - 16);
				float g = (float)(i - 16);
				float h = MathHelper.sqrt(f * f + g * g);
				this.field_20794[i << 5 | j] = -g / h;
				this.field_20795[i << 5 | j] = f / h;
			}
		}

		this.renderStars();
		this.method_3277();
		this.method_3265();
	}

	private void method_22714(LightmapTextureManager lightmapTextureManager, float f, double d, double e, double g) {
		float h = this.client.world.getRainGradient(f);
		if (!(h <= 0.0F)) {
			lightmapTextureManager.enable();
			World world = this.client.world;
			int i = MathHelper.floor(d);
			int j = MathHelper.floor(e);
			int k = MathHelper.floor(g);
			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder bufferBuilder = tessellator.getBufferBuilder();
			RenderSystem.disableCull();
			RenderSystem.normal3f(0.0F, 1.0F, 0.0F);
			RenderSystem.enableBlend();
			RenderSystem.defaultBlendFunc();
			RenderSystem.defaultAlphaFunc();
			int l = 5;
			if (this.client.options.fancyGraphics) {
				l = 10;
			}

			int m = -1;
			float n = (float)this.ticks + f;
			RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			BlockPos.Mutable mutable = new BlockPos.Mutable();

			for (int o = k - l; o <= k + l; o++) {
				for (int p = i - l; p <= i + l; p++) {
					int q = (o - k + 16) * 32 + p - i + 16;
					double r = (double)this.field_20794[q] * 0.5;
					double s = (double)this.field_20795[q] * 0.5;
					mutable.set(p, 0, o);
					Biome biome = world.getBiome(mutable);
					if (biome.getPrecipitation() != Biome.Precipitation.NONE) {
						int t = world.getTopPosition(Heightmap.Type.MOTION_BLOCKING, mutable).getY();
						int u = j - l;
						int v = j + l;
						if (u < t) {
							u = t;
						}

						if (v < t) {
							v = t;
						}

						int w = t;
						if (t < j) {
							w = j;
						}

						if (u != v) {
							Random random = new Random((long)(p * p * 3121 + p * 45238971 ^ o * o * 418711 + o * 13761));
							mutable.set(p, u, o);
							float x = biome.getTemperature(mutable);
							if (x >= 0.15F) {
								if (m != 0) {
									if (m >= 0) {
										tessellator.draw();
									}

									m = 0;
									this.client.getTextureManager().bindTexture(field_20797);
									bufferBuilder.begin(7, VertexFormats.POSITION_UV_COLOR_LMAP);
								}

								int y = this.ticks + p * p * 3121 + p * 45238971 + o * o * 418711 + o * 13761 & 31;
								float z = -((float)y + f) / 32.0F * (3.0F + random.nextFloat());
								double aa = (double)((float)p + 0.5F) - d;
								double ab = (double)((float)o + 0.5F) - g;
								float ac = MathHelper.sqrt(aa * aa + ab * ab) / (float)l;
								float ad = ((1.0F - ac * ac) * 0.5F + 0.5F) * h;
								mutable.set(p, w, o);
								int ae = world.getLightmapCoordinates(mutable);
								bufferBuilder.vertex((double)p - d - r + 0.5, (double)v - e, (double)o - g - s + 0.5)
									.texture(0.0F, (float)u * 0.25F + z)
									.color(1.0F, 1.0F, 1.0F, ad)
									.light(ae)
									.next();
								bufferBuilder.vertex((double)p - d + r + 0.5, (double)v - e, (double)o - g + s + 0.5)
									.texture(1.0F, (float)u * 0.25F + z)
									.color(1.0F, 1.0F, 1.0F, ad)
									.light(ae)
									.next();
								bufferBuilder.vertex((double)p - d + r + 0.5, (double)u - e, (double)o - g + s + 0.5)
									.texture(1.0F, (float)v * 0.25F + z)
									.color(1.0F, 1.0F, 1.0F, ad)
									.light(ae)
									.next();
								bufferBuilder.vertex((double)p - d - r + 0.5, (double)u - e, (double)o - g - s + 0.5)
									.texture(0.0F, (float)v * 0.25F + z)
									.color(1.0F, 1.0F, 1.0F, ad)
									.light(ae)
									.next();
							} else {
								if (m != 1) {
									if (m >= 0) {
										tessellator.draw();
									}

									m = 1;
									this.client.getTextureManager().bindTexture(field_20798);
									bufferBuilder.begin(7, VertexFormats.POSITION_UV_COLOR_LMAP);
								}

								float af = -((float)(this.ticks & 511) + f) / 512.0F;
								float z = (float)(random.nextDouble() + (double)n * 0.01 * (double)((float)random.nextGaussian()));
								float ag = (float)(random.nextDouble() + (double)(n * (float)random.nextGaussian()) * 0.001);
								double ah = (double)((float)p + 0.5F) - d;
								double ai = (double)((float)o + 0.5F) - g;
								float ad = MathHelper.sqrt(ah * ah + ai * ai) / (float)l;
								float aj = ((1.0F - ad * ad) * 0.3F + 0.5F) * h;
								mutable.set(p, w, o);
								int ak = world.getLightmapCoordinates(mutable);
								int al = ak >> 16 & 65535;
								int am = (ak & 65535) * 3;
								int an = (al * 3 + 240) / 4;
								int ao = (am * 3 + 240) / 4;
								bufferBuilder.vertex((double)p - d - r + 0.5, (double)v - e, (double)o - g - s + 0.5)
									.texture(0.0F + z, (float)u * 0.25F + af + ag)
									.color(1.0F, 1.0F, 1.0F, aj)
									.light(ao, an)
									.next();
								bufferBuilder.vertex((double)p - d + r + 0.5, (double)v - e, (double)o - g + s + 0.5)
									.texture(1.0F + z, (float)u * 0.25F + af + ag)
									.color(1.0F, 1.0F, 1.0F, aj)
									.light(ao, an)
									.next();
								bufferBuilder.vertex((double)p - d + r + 0.5, (double)u - e, (double)o - g + s + 0.5)
									.texture(1.0F + z, (float)v * 0.25F + af + ag)
									.color(1.0F, 1.0F, 1.0F, aj)
									.light(ao, an)
									.next();
								bufferBuilder.vertex((double)p - d - r + 0.5, (double)u - e, (double)o - g - s + 0.5)
									.texture(0.0F + z, (float)v * 0.25F + af + ag)
									.color(1.0F, 1.0F, 1.0F, aj)
									.light(ao, an)
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
			RenderSystem.defaultAlphaFunc();
			lightmapTextureManager.disable();
		}
	}

	public void method_22713(Camera camera) {
		float f = this.client.world.getRainGradient(1.0F);
		if (!this.client.options.fancyGraphics) {
			f /= 2.0F;
		}

		if (f != 0.0F) {
			Random random = new Random((long)this.ticks * 312987231L);
			WorldView worldView = this.client.world;
			BlockPos blockPos = new BlockPos(camera.getPos());
			int i = 10;
			double d = 0.0;
			double e = 0.0;
			double g = 0.0;
			int j = 0;
			int k = (int)(100.0F * f * f);
			if (this.client.options.particles == ParticlesOption.DECREASED) {
				k >>= 1;
			} else if (this.client.options.particles == ParticlesOption.MINIMAL) {
				k = 0;
			}

			for (int l = 0; l < k; l++) {
				BlockPos blockPos2 = worldView.getTopPosition(
					Heightmap.Type.MOTION_BLOCKING, blockPos.add(random.nextInt(10) - random.nextInt(10), 0, random.nextInt(10) - random.nextInt(10))
				);
				Biome biome = worldView.getBiome(blockPos2);
				BlockPos blockPos3 = blockPos2.method_10074();
				if (blockPos2.getY() <= blockPos.getY() + 10
					&& blockPos2.getY() >= blockPos.getY() - 10
					&& biome.getPrecipitation() == Biome.Precipitation.RAIN
					&& biome.getTemperature(blockPos2) >= 0.15F) {
					double h = random.nextDouble();
					double m = random.nextDouble();
					BlockState blockState = worldView.getBlockState(blockPos3);
					FluidState fluidState = worldView.getFluidState(blockPos2);
					VoxelShape voxelShape = blockState.getCollisionShape(worldView, blockPos3);
					double n = voxelShape.method_1102(Direction.Axis.Y, h, m);
					double o = (double)fluidState.getHeight(worldView, blockPos2);
					double p;
					double q;
					if (n >= o) {
						p = n;
						q = voxelShape.method_1093(Direction.Axis.Y, h, m);
					} else {
						p = 0.0;
						q = 0.0;
					}

					if (p > -Double.MAX_VALUE) {
						if (!fluidState.matches(FluidTags.LAVA)
							&& blockState.getBlock() != Blocks.MAGMA_BLOCK
							&& (blockState.getBlock() != Blocks.CAMPFIRE || !(Boolean)blockState.get(CampfireBlock.LIT))) {
							if (random.nextInt(++j) == 0) {
								d = (double)blockPos3.getX() + h;
								e = (double)((float)blockPos3.getY() + 0.1F) + p - 1.0;
								g = (double)blockPos3.getZ() + m;
							}

							this.client
								.world
								.addParticle(
									ParticleTypes.RAIN, (double)blockPos3.getX() + h, (double)((float)blockPos3.getY() + 0.1F) + p, (double)blockPos3.getZ() + m, 0.0, 0.0, 0.0
								);
						} else {
							this.client
								.world
								.addParticle(
									ParticleTypes.SMOKE, (double)blockPos2.getX() + h, (double)((float)blockPos2.getY() + 0.1F) - q, (double)blockPos2.getZ() + m, 0.0, 0.0, 0.0
								);
						}
					}
				}
			}

			if (j > 0 && random.nextInt(3) < this.field_20793++) {
				this.field_20793 = 0;
				if (e > (double)(blockPos.getY() + 1)
					&& worldView.getTopPosition(Heightmap.Type.MOTION_BLOCKING, blockPos).getY() > MathHelper.floor((float)blockPos.getY())) {
					this.client.world.playSound(d, e, g, SoundEvents.WEATHER_RAIN_ABOVE, SoundCategory.WEATHER, 0.1F, 0.5F, false);
				} else {
					this.client.world.playSound(d, e, g, SoundEvents.WEATHER_RAIN, SoundCategory.WEATHER, 0.2F, 1.0F, false);
				}
			}
		}
	}

	public void close() {
		if (this.entityOutlineShader != null) {
			this.entityOutlineShader.close();
		}
	}

	@Override
	public void apply(ResourceManager resourceManager) {
		this.textureManager.bindTexture(FORCEFIELD_TEX);
		RenderSystem.texParameter(3553, 10242, 10497);
		RenderSystem.texParameter(3553, 10243, 10497);
		RenderSystem.bindTexture(0);
		this.loadEntityOutlineShader();
	}

	public void loadEntityOutlineShader() {
		if (this.entityOutlineShader != null) {
			this.entityOutlineShader.close();
		}

		Identifier identifier = new Identifier("shaders/post/entity_outline.json");

		try {
			this.entityOutlineShader = new ShaderEffect(this.client.getTextureManager(), this.client.getResourceManager(), this.client.getFramebuffer(), identifier);
			this.entityOutlineShader.setupDimensions(this.client.getWindow().getFramebufferWidth(), this.client.getWindow().getFramebufferHeight());
			this.entityOutlinesFramebuffer = this.entityOutlineShader.getSecondaryTarget("final");
		} catch (IOException var3) {
			LOGGER.warn("Failed to load shader: {}", identifier, var3);
			this.entityOutlineShader = null;
			this.entityOutlinesFramebuffer = null;
		} catch (JsonSyntaxException var4) {
			LOGGER.warn("Failed to load shader: {}", identifier, var4);
			this.entityOutlineShader = null;
			this.entityOutlinesFramebuffer = null;
		}
	}

	public void drawEntityOutlinesFramebuffer() {
		if (this.canDrawEntityOutlines()) {
			RenderSystem.enableBlend();
			RenderSystem.blendFuncSeparate(
				GlStateManager.class_4535.SRC_ALPHA, GlStateManager.class_4534.ONE_MINUS_SRC_ALPHA, GlStateManager.class_4535.ZERO, GlStateManager.class_4534.ONE
			);
			this.entityOutlinesFramebuffer.method_22594(this.client.getWindow().getFramebufferWidth(), this.client.getWindow().getFramebufferHeight(), false);
			RenderSystem.disableBlend();
		}
	}

	protected boolean canDrawEntityOutlines() {
		return this.entityOutlinesFramebuffer != null && this.entityOutlineShader != null && this.client.player != null;
	}

	private void method_3265() {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBufferBuilder();
		if (this.field_4102 != null) {
			this.field_4102.delete();
		}

		this.field_4102 = new GlBuffer(this.field_4100);
		this.method_3283(bufferBuilder, -16.0F, true);
		bufferBuilder.end();
		this.field_4102.set(bufferBuilder);
	}

	private void method_3277() {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBufferBuilder();
		if (this.field_4087 != null) {
			this.field_4087.delete();
		}

		this.field_4087 = new GlBuffer(this.field_4100);
		this.method_3283(bufferBuilder, 16.0F, false);
		bufferBuilder.end();
		this.field_4087.set(bufferBuilder);
	}

	private void method_3283(BufferBuilder bufferBuilder, float f, boolean bl) {
		int i = 64;
		int j = 6;
		bufferBuilder.begin(7, VertexFormats.POSITION);

		for (int k = -384; k <= 384; k += 64) {
			for (int l = -384; l <= 384; l += 64) {
				float g = (float)k;
				float h = (float)(k + 64);
				if (bl) {
					h = (float)k;
					g = (float)(k + 64);
				}

				bufferBuilder.vertex((double)g, (double)f, (double)l).next();
				bufferBuilder.vertex((double)h, (double)f, (double)l).next();
				bufferBuilder.vertex((double)h, (double)f, (double)(l + 64)).next();
				bufferBuilder.vertex((double)g, (double)f, (double)(l + 64)).next();
			}
		}
	}

	private void renderStars() {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBufferBuilder();
		if (this.starsBuffer != null) {
			this.starsBuffer.delete();
		}

		this.starsBuffer = new GlBuffer(this.field_4100);
		this.renderStars(bufferBuilder);
		bufferBuilder.end();
		this.starsBuffer.set(bufferBuilder);
	}

	private void renderStars(BufferBuilder bufferBuilder) {
		Random random = new Random(10842L);
		bufferBuilder.begin(7, VertexFormats.POSITION);

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
					bufferBuilder.vertex(j + af, k + ad, l + ah).next();
				}
			}
		}
	}

	public void setWorld(@Nullable ClientWorld clientWorld) {
		this.lastCameraChunkUpdateX = Double.MIN_VALUE;
		this.lastCameraChunkUpdateY = Double.MIN_VALUE;
		this.lastCameraChunkUpdateZ = Double.MIN_VALUE;
		this.cameraChunkX = Integer.MIN_VALUE;
		this.cameraChunkY = Integer.MIN_VALUE;
		this.cameraChunkZ = Integer.MIN_VALUE;
		this.entityRenderDispatcher.setWorld(clientWorld);
		this.world = clientWorld;
		if (clientWorld != null) {
			this.reload();
		} else {
			this.chunkRenderers.clear();
			this.chunkInfos.clear();
			if (this.chunkRenderDispatcher != null) {
				this.chunkRenderDispatcher.delete();
				this.chunkRenderDispatcher = null;
			}

			if (this.chunkBatcher != null) {
				this.chunkBatcher.stop();
			}

			this.chunkBatcher = null;
			this.blockEntities.clear();
		}
	}

	public void reload() {
		if (this.world != null) {
			if (this.chunkBatcher == null) {
				this.chunkBatcher = new ChunkBatcher(
					this.world, this, SystemUtil.getServerWorkerExecutor(), this.client.is64Bit(), this.field_20951.getBlockBufferBuilders()
				);
			} else {
				this.chunkBatcher.method_22752(this.world);
			}

			this.terrainUpdateNecessary = true;
			this.cloudsDirty = true;
			RenderLayer.method_22719(this.client.options.fancyGraphics);
			this.renderDistance = this.client.options.viewDistance;
			if (this.chunkRenderDispatcher != null) {
				this.chunkRenderDispatcher.delete();
			}

			this.clearChunkRenderers();
			synchronized (this.blockEntities) {
				this.blockEntities.clear();
			}

			this.chunkRenderDispatcher = new ChunkRenderDispatcher(this.chunkBatcher, this.world, this.client.options.viewDistance, this);
			if (this.world != null) {
				Entity entity = this.client.getCameraEntity();
				if (entity != null) {
					this.chunkRenderDispatcher.updateCameraPosition(entity.getX(), entity.getZ());
				}
			}
		}
	}

	protected void clearChunkRenderers() {
		this.chunkRenderers.clear();
		this.chunkBatcher.reset();
	}

	public void onResized(int i, int j) {
		this.scheduleTerrainUpdate();
		if (this.entityOutlineShader != null) {
			this.entityOutlineShader.setupDimensions(i, j);
		}
	}

	public String getChunksDebugString() {
		int i = this.chunkRenderDispatcher.renderers.length;
		int j = this.getCompletedChunkCount();
		return String.format(
			"C: %d/%d %sD: %d, %s",
			j,
			i,
			this.client.field_1730 ? "(s) " : "",
			this.renderDistance,
			this.chunkBatcher == null ? "null" : this.chunkBatcher.getDebugString()
		);
	}

	protected int getCompletedChunkCount() {
		int i = 0;

		for (WorldRenderer.ChunkInfo chunkInfo : this.chunkInfos) {
			if (!chunkInfo.renderer.getData().isEmpty()) {
				i++;
			}
		}

		return i;
	}

	public String getEntitiesDebugString() {
		return "E: " + this.regularEntityCount + "/" + this.world.getRegularEntityCount() + ", B: " + this.blockEntityCount;
	}

	private void setUpTerrain(Camera camera, Frustum frustum, boolean bl, int i, boolean bl2) {
		net.minecraft.util.math.Vec3d vec3d = camera.getPos();
		if (this.client.options.viewDistance != this.renderDistance) {
			this.reload();
		}

		this.world.getProfiler().push("camera");
		double d = this.client.player.getX() - this.lastCameraChunkUpdateX;
		double e = this.client.player.getY() - this.lastCameraChunkUpdateY;
		double f = this.client.player.getZ() - this.lastCameraChunkUpdateZ;
		if (this.cameraChunkX != this.client.player.chunkX
			|| this.cameraChunkY != this.client.player.chunkY
			|| this.cameraChunkZ != this.client.player.chunkZ
			|| d * d + e * e + f * f > 16.0) {
			this.lastCameraChunkUpdateX = this.client.player.getX();
			this.lastCameraChunkUpdateY = this.client.player.getY();
			this.lastCameraChunkUpdateZ = this.client.player.getZ();
			this.cameraChunkX = this.client.player.chunkX;
			this.cameraChunkY = this.client.player.chunkY;
			this.cameraChunkZ = this.client.player.chunkZ;
			this.chunkRenderDispatcher.updateCameraPosition(this.client.player.getX(), this.client.player.getZ());
		}

		this.chunkBatcher.setCameraPosition(vec3d);
		this.world.getProfiler().swap("cull");
		this.client.getProfiler().swap("culling");
		BlockPos blockPos = camera.getBlockPos();
		ChunkBatcher.ChunkRenderer chunkRenderer = this.chunkRenderDispatcher.getChunkRenderer(blockPos);
		BlockPos blockPos2 = new BlockPos(MathHelper.floor(vec3d.x / 16.0) * 16, MathHelper.floor(vec3d.y / 16.0) * 16, MathHelper.floor(vec3d.z / 16.0) * 16);
		float g = camera.getPitch();
		float h = camera.getYaw();
		this.terrainUpdateNecessary = this.terrainUpdateNecessary
			|| !this.chunkRenderers.isEmpty()
			|| vec3d.x != this.lastCameraX
			|| vec3d.y != this.lastCameraY
			|| vec3d.z != this.lastCameraZ
			|| (double)g != this.lastCameraPitch
			|| (double)h != this.lastCameraYaw;
		this.lastCameraX = vec3d.x;
		this.lastCameraY = vec3d.y;
		this.lastCameraZ = vec3d.z;
		this.lastCameraPitch = (double)g;
		this.lastCameraYaw = (double)h;
		this.client.getProfiler().swap("update");
		if (!bl && this.terrainUpdateNecessary) {
			this.terrainUpdateNecessary = false;
			this.chunkInfos = Lists.<WorldRenderer.ChunkInfo>newArrayList();
			Queue<WorldRenderer.ChunkInfo> queue = Queues.<WorldRenderer.ChunkInfo>newArrayDeque();
			Entity.setRenderDistanceMultiplier(MathHelper.clamp((double)this.client.options.viewDistance / 8.0, 1.0, 2.5));
			boolean bl3 = this.client.field_1730;
			if (chunkRenderer != null) {
				boolean bl4 = false;
				WorldRenderer.ChunkInfo chunkInfo = new WorldRenderer.ChunkInfo(chunkRenderer, null, 0);
				Set<Direction> set = this.getOpenChunkFaces(blockPos);
				if (set.size() == 1) {
					net.minecraft.util.math.Vec3d vec3d2 = camera.getHorizontalPlane();
					Direction direction = Direction.getFacing(vec3d2.x, vec3d2.y, vec3d2.z).getOpposite();
					set.remove(direction);
				}

				if (set.isEmpty()) {
					bl4 = true;
				}

				if (bl4 && !bl2) {
					this.chunkInfos.add(chunkInfo);
				} else {
					if (bl2 && this.world.getBlockState(blockPos).isFullOpaque(this.world, blockPos)) {
						bl3 = false;
					}

					chunkRenderer.method_3671(i);
					queue.add(chunkInfo);
				}
			} else {
				int j = blockPos.getY() > 0 ? 248 : 8;

				for (int k = -this.renderDistance; k <= this.renderDistance; k++) {
					for (int l = -this.renderDistance; l <= this.renderDistance; l++) {
						ChunkBatcher.ChunkRenderer chunkRenderer2 = this.chunkRenderDispatcher.getChunkRenderer(new BlockPos((k << 4) + 8, j, (l << 4) + 8));
						if (chunkRenderer2 != null && frustum.method_23093(chunkRenderer2.boundingBox)) {
							chunkRenderer2.method_3671(i);
							queue.add(new WorldRenderer.ChunkInfo(chunkRenderer2, null, 0));
						}
					}
				}
			}

			this.client.getProfiler().push("iteration");

			while (!queue.isEmpty()) {
				WorldRenderer.ChunkInfo chunkInfo2 = (WorldRenderer.ChunkInfo)queue.poll();
				ChunkBatcher.ChunkRenderer chunkRenderer3 = chunkInfo2.renderer;
				Direction direction2 = chunkInfo2.field_4125;
				this.chunkInfos.add(chunkInfo2);

				for (Direction direction3 : DIRECTIONS) {
					ChunkBatcher.ChunkRenderer chunkRenderer4 = this.getAdjacentChunkRenderer(blockPos2, chunkRenderer3, direction3);
					if ((!bl3 || !chunkInfo2.method_3298(direction3.getOpposite()))
						&& (!bl3 || direction2 == null || chunkRenderer3.getData().isVisibleThrough(direction2.getOpposite(), direction3))
						&& chunkRenderer4 != null
						&& chunkRenderer4.shouldBuild()
						&& chunkRenderer4.method_3671(i)
						&& frustum.method_23093(chunkRenderer4.boundingBox)) {
						WorldRenderer.ChunkInfo chunkInfo3 = new WorldRenderer.ChunkInfo(chunkRenderer4, direction3, chunkInfo2.field_4122 + 1);
						chunkInfo3.method_3299(chunkInfo2.field_4126, direction3);
						queue.add(chunkInfo3);
					}
				}
			}

			this.client.getProfiler().pop();
		}

		this.client.getProfiler().swap("rebuildNear");
		Set<ChunkBatcher.ChunkRenderer> set2 = this.chunkRenderers;
		this.chunkRenderers = Sets.<ChunkBatcher.ChunkRenderer>newLinkedHashSet();

		for (WorldRenderer.ChunkInfo chunkInfo2 : this.chunkInfos) {
			ChunkBatcher.ChunkRenderer chunkRenderer3 = chunkInfo2.renderer;
			if (chunkRenderer3.shouldRebuild() || set2.contains(chunkRenderer3)) {
				this.terrainUpdateNecessary = true;
				BlockPos blockPos3 = chunkRenderer3.getOrigin().add(8, 8, 8);
				boolean bl5 = blockPos3.getSquaredDistance(blockPos) < 768.0;
				if (!chunkRenderer3.shouldRebuildOnClientThread() && !bl5) {
					this.chunkRenderers.add(chunkRenderer3);
				} else {
					this.client.getProfiler().push("build near");
					this.chunkBatcher.rebuildSync(chunkRenderer3);
					chunkRenderer3.unscheduleRebuild();
					this.client.getProfiler().pop();
				}
			}
		}

		this.chunkRenderers.addAll(set2);
		this.client.getProfiler().pop();
	}

	private Set<Direction> getOpenChunkFaces(BlockPos blockPos) {
		ChunkOcclusionGraphBuilder chunkOcclusionGraphBuilder = new ChunkOcclusionGraphBuilder();
		BlockPos blockPos2 = new BlockPos(blockPos.getX() >> 4 << 4, blockPos.getY() >> 4 << 4, blockPos.getZ() >> 4 << 4);
		WorldChunk worldChunk = this.world.getWorldChunk(blockPos2);

		for (BlockPos blockPos3 : BlockPos.iterate(blockPos2, blockPos2.add(15, 15, 15))) {
			if (worldChunk.getBlockState(blockPos3).isFullOpaque(this.world, blockPos3)) {
				chunkOcclusionGraphBuilder.markClosed(blockPos3);
			}
		}

		return chunkOcclusionGraphBuilder.getOpenFaces(blockPos);
	}

	@Nullable
	private ChunkBatcher.ChunkRenderer getAdjacentChunkRenderer(BlockPos blockPos, ChunkBatcher.ChunkRenderer chunkRenderer, Direction direction) {
		BlockPos blockPos2 = chunkRenderer.getNeighborPosition(direction);
		if (MathHelper.abs(blockPos.getX() - blockPos2.getX()) > this.renderDistance * 16) {
			return null;
		} else if (blockPos2.getY() < 0 || blockPos2.getY() >= 256) {
			return null;
		} else {
			return MathHelper.abs(blockPos.getZ() - blockPos2.getZ()) > this.renderDistance * 16 ? null : this.chunkRenderDispatcher.getChunkRenderer(blockPos2);
		}
	}

	private void captureFrustum(Matrix4f matrix4f, Matrix4f matrix4f2, double d, double e, double f, Frustum frustum) {
		this.forcedFrustum = frustum;
		Matrix4f matrix4f3 = new Matrix4f(matrix4f2);
		matrix4f3.multiply(matrix4f);
		matrix4f3.invert();
		this.forcedFrustumPosition.x = d;
		this.forcedFrustumPosition.y = e;
		this.forcedFrustumPosition.z = f;
		this.field_4065[0] = new Vector4f(-1.0F, -1.0F, -1.0F, 1.0F);
		this.field_4065[1] = new Vector4f(1.0F, -1.0F, -1.0F, 1.0F);
		this.field_4065[2] = new Vector4f(1.0F, 1.0F, -1.0F, 1.0F);
		this.field_4065[3] = new Vector4f(-1.0F, 1.0F, -1.0F, 1.0F);
		this.field_4065[4] = new Vector4f(-1.0F, -1.0F, 1.0F, 1.0F);
		this.field_4065[5] = new Vector4f(1.0F, -1.0F, 1.0F, 1.0F);
		this.field_4065[6] = new Vector4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.field_4065[7] = new Vector4f(-1.0F, 1.0F, 1.0F, 1.0F);

		for (int i = 0; i < 8; i++) {
			this.field_4065[i].multiply(matrix4f3);
			this.field_4065[i].method_23219();
		}
	}

	public void render(
		MatrixStack matrixStack, float f, long l, boolean bl, Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager
	) {
		BlockEntityRenderDispatcher.INSTANCE.configure(this.world, this.client.getTextureManager(), this.client.textRenderer, camera, this.client.hitResult);
		this.entityRenderDispatcher.configure(this.world, camera, this.client.targetedEntity);
		Profiler profiler = this.world.getProfiler();
		profiler.swap("light_updates");
		this.client.world.method_2935().getLightingProvider().doLightUpdates(Integer.MAX_VALUE, true, true);
		net.minecraft.util.math.Vec3d vec3d = camera.getPos();
		double d = vec3d.getX();
		double e = vec3d.getY();
		double g = vec3d.getZ();
		Matrix4f matrix4f = matrixStack.peek();
		Matrix4f matrix4f2 = gameRenderer.method_22973(camera, f, true, true, MathHelper.SQUARE_ROOT_OF_TWO);
		profiler.swap("culling");
		boolean bl2 = this.forcedFrustum != null;
		Frustum frustum;
		if (bl2) {
			frustum = this.forcedFrustum;
			frustum.method_23088(this.forcedFrustumPosition.x, this.forcedFrustumPosition.y, this.forcedFrustumPosition.z);
		} else {
			frustum = new Frustum(matrix4f, matrix4f2);
			frustum.method_23088(d, e, g);
		}

		this.client.getProfiler().swap("captureFrustum");
		if (this.shouldCaptureFrustum) {
			this.captureFrustum(matrix4f, matrix4f2, vec3d.x, vec3d.y, vec3d.z, bl2 ? new Frustum(matrix4f, matrix4f2) : frustum);
			this.shouldCaptureFrustum = false;
		}

		profiler.swap("clear");
		this.chunkRendererList.renderBackground(camera, f, this.client.world, this.client.options.viewDistance, gameRenderer.getSkyDarkness(f));
		RenderSystem.clear(16640, MinecraftClient.IS_SYSTEM_MAC);
		float h = gameRenderer.getViewDistance();
		boolean bl3 = this.client.world.dimension.shouldRenderFog(MathHelper.floor(d), MathHelper.floor(e))
			|| this.client.inGameHud.getBossBarHud().shouldThickenFog();
		if (this.client.options.viewDistance >= 4) {
			BackgroundRenderer.applyFog(camera, BackgroundRenderer.class_4596.FOG_SKY, h, bl3);
			profiler.swap("sky");
			gameRenderer.method_22709(camera, f, true, false, 2.0F);
			this.renderSky(matrixStack, f);
			gameRenderer.method_22709(camera, f, true, false, MathHelper.SQUARE_ROOT_OF_TWO);
		}

		profiler.swap("fog");
		BackgroundRenderer.applyFog(camera, BackgroundRenderer.class_4596.FOG_TERRAIN, h, bl3);
		profiler.swap("terrain_setup");
		this.setUpTerrain(camera, frustum, bl2, this.field_20792++, this.client.player.isSpectator());
		profiler.swap("updatechunks");
		this.updateChunks(l);
		profiler.swap("terrain");
		this.renderLayer(RenderLayer.getSolid(), matrixStack, d, e, g);
		this.renderLayer(RenderLayer.getCutoutMipped(), matrixStack, d, e, g);
		this.renderLayer(RenderLayer.getCutout(), matrixStack, d, e, g);
		GuiLighting.enable(matrixStack.peek());
		profiler.swap("entities");
		profiler.push("prepare");
		this.regularEntityCount = 0;
		this.blockEntityCount = 0;
		RenderSystem.defaultAlphaFunc();
		RenderSystem.enableAlphaTest();
		profiler.swap("entities");
		if (this.canDrawEntityOutlines()) {
			profiler.swap("entityOutlines");
			this.entityOutlinesFramebuffer.clear(MinecraftClient.IS_SYSTEM_MAC);
			this.client.getFramebuffer().beginWrite(false);
		}

		boolean bl4 = false;
		LayeredVertexConsumerStorage.class_4598 lv = this.field_20951.method_23000();

		for (Entity entity : this.world.getEntities()) {
			if ((this.entityRenderDispatcher.shouldRender(entity, frustum, d, e, g) || entity.hasPassengerDeep(this.client.player))
				&& (
					entity != camera.getFocusedEntity()
						|| camera.isThirdPerson()
						|| camera.getFocusedEntity() instanceof LivingEntity && ((LivingEntity)camera.getFocusedEntity()).isSleeping()
				)
				&& (!(entity instanceof ClientPlayerEntity) || camera.getFocusedEntity() == entity)) {
				this.regularEntityCount++;
				if (entity.age == 0) {
					entity.prevRenderX = entity.getX();
					entity.prevRenderY = entity.getY();
					entity.prevRenderZ = entity.getZ();
				}

				boolean bl5 = this.canDrawEntityOutlines()
					&& (entity.isGlowing() || entity instanceof PlayerEntity && this.client.player.isSpectator() && this.client.options.keySpectatorOutlines.isPressed());
				LayeredVertexConsumerStorage layeredVertexConsumerStorage;
				if (bl5) {
					bl4 = true;
					class_4618 lv2 = this.field_20951.method_23003();
					layeredVertexConsumerStorage = lv2;
					int i = entity.method_22861();
					int j = 255;
					int k = i >> 16 & 0xFF;
					int m = i >> 8 & 0xFF;
					int n = i & 0xFF;
					lv2.method_23286(k, m, n, 255);
				} else {
					layeredVertexConsumerStorage = lv;
				}

				this.renderEntity(entity, d, e, g, f, matrixStack, layeredVertexConsumerStorage);
			}
		}

		this.checkEmpty(matrixStack);
		profiler.swap("blockentities");

		for (WorldRenderer.ChunkInfo chunkInfo : this.chunkInfos) {
			List<BlockEntity> list = chunkInfo.renderer.getData().getBlockEntities();
			if (!list.isEmpty()) {
				for (BlockEntity blockEntity : list) {
					BlockPos blockPos = blockEntity.getPos();
					LayeredVertexConsumerStorage layeredVertexConsumerStorage2 = lv;
					matrixStack.push();
					matrixStack.translate((double)blockPos.getX() - d, (double)blockPos.getY() - e, (double)blockPos.getZ() - g);
					SortedSet<PartiallyBrokenBlockEntry> sortedSet = this.field_20950.get(blockPos.asLong());
					if (sortedSet != null && !sortedSet.isEmpty()) {
						int m = ((PartiallyBrokenBlockEntry)sortedSet.last()).getStage();
						if (m >= 0) {
							VertexConsumer vertexConsumer = new MatrixVertexConsumer(this.field_20951.method_23001().getBuffer(RenderLayer.getCrumbling(m)), matrixStack.peek());
							layeredVertexConsumerStorage2 = renderLayer -> {
								VertexConsumer vertexConsumer2x = lv.getBuffer(renderLayer);
								return (VertexConsumer)(renderLayer.method_23037()
									? new DelegatingVertexConsumer(ImmutableList.of(vertexConsumer, vertexConsumer2x))
									: vertexConsumer2x);
							};
						}
					}

					BlockEntityRenderDispatcher.INSTANCE.render(blockEntity, f, matrixStack, layeredVertexConsumerStorage2, d, e, g);
					matrixStack.pop();
				}
			}
		}

		synchronized (this.blockEntities) {
			for (BlockEntity blockEntity2 : this.blockEntities) {
				BlockPos blockPos2 = blockEntity2.getPos();
				matrixStack.push();
				matrixStack.translate((double)blockPos2.getX() - d, (double)blockPos2.getY() - e, (double)blockPos2.getZ() - g);
				BlockEntityRenderDispatcher.INSTANCE.render(blockEntity2, f, matrixStack, lv, d, e, g);
				matrixStack.pop();
			}
		}

		this.checkEmpty(matrixStack);
		lv.method_22994(RenderLayer.getSolid());
		lv.method_22994(RenderLayer.getEntitySolid(SpriteAtlasTexture.BLOCK_ATLAS_TEX));
		lv.method_22994(RenderLayer.getEntityCutout(SpriteAtlasTexture.BLOCK_ATLAS_TEX));
		this.field_20951.method_23003().method_23285();
		if (bl4) {
			this.entityOutlineShader.render(f);
			this.client.getFramebuffer().beginWrite(false);
		}

		profiler.swap("destroyProgress");

		for (Entry<SortedSet<PartiallyBrokenBlockEntry>> entry : this.field_20950.long2ObjectEntrySet()) {
			BlockPos blockPos3 = BlockPos.fromLong(entry.getLongKey());
			double o = (double)blockPos3.getX() - d;
			double p = (double)blockPos3.getY() - e;
			double q = (double)blockPos3.getZ() - g;
			if (!(o * o + p * p + q * q > 1024.0)) {
				matrixStack.push();
				matrixStack.translate((double)(blockPos3.getX() & -16) - d, (double)(blockPos3.getY() & -16) - e, (double)(blockPos3.getZ() & -16) - g);
				SortedSet<PartiallyBrokenBlockEntry> sortedSet2 = (SortedSet<PartiallyBrokenBlockEntry>)entry.getValue();
				if (sortedSet2 != null && !sortedSet2.isEmpty()) {
					int r = ((PartiallyBrokenBlockEntry)sortedSet2.last()).getStage();
					VertexConsumer vertexConsumer2 = new MatrixVertexConsumer(this.field_20951.method_23001().getBuffer(RenderLayer.getCrumbling(r)), matrixStack.peek());
					this.client.getBlockRenderManager().tesselateDamage(this.world.getBlockState(blockPos3), blockPos3, this.world, matrixStack, vertexConsumer2);
					matrixStack.pop();
				}
			}
		}

		this.checkEmpty(matrixStack);
		profiler.pop();
		HitResult hitResult = this.client.hitResult;
		if (bl && hitResult != null && hitResult.getType() == HitResult.Type.BLOCK) {
			profiler.swap("outline");
			BlockPos blockPos4 = ((BlockHitResult)hitResult).getBlockPos();
			BlockState blockState = this.world.getBlockState(blockPos4);
			if (!blockState.isAir() && this.world.getWorldBorder().contains(blockPos4)) {
				VertexConsumer vertexConsumer3 = lv.getBuffer(RenderLayer.getLines());
				this.drawBlockOutline(matrixStack, vertexConsumer3, camera.getFocusedEntity(), d, e, g, blockPos4, blockState);
			}
		}

		lv.method_22993();
		this.field_20951.method_23001().method_22993();
		RenderSystem.pushMatrix();
		RenderSystem.multMatrix(matrixStack.peek());
		this.client.debugRenderer.method_23099(l);
		this.renderWorldBorder(camera, f);
		this.client.getTextureManager().bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
		BackgroundRenderer.applyFog(camera, BackgroundRenderer.class_4596.FOG_TERRAIN, h, bl3);
		profiler.swap("translucent");
		this.renderLayer(RenderLayer.getTranslucent(), matrixStack, d, e, g);
		lightmapTextureManager.enable();
		BackgroundRenderer.applyFog(camera, BackgroundRenderer.class_4596.FOG_TERRAIN, h, bl3);
		profiler.swap("particles");
		RenderSystem.enableAlphaTest();
		RenderSystem.defaultAlphaFunc();
		RenderSystem.enableDepthTest();
		this.client.particleManager.renderParticles(camera, f);
		lightmapTextureManager.disable();
		profiler.swap("cloudsLayers");
		if (this.client.options.getCloudRenderMode() != CloudRenderMode.OFF) {
			profiler.swap("clouds");
			gameRenderer.method_22709(camera, f, true, false, 4.0F);
			BackgroundRenderer.applyFog(camera, BackgroundRenderer.class_4596.FOG_TERRAIN, gameRenderer.getViewDistance(), bl3);
			RenderSystem.disableCull();
			RenderSystem.enableBlend();
			RenderSystem.enableAlphaTest();
			RenderSystem.enableDepthTest();
			RenderSystem.defaultAlphaFunc();
			RenderSystem.defaultBlendFunc();
			this.renderClouds(matrixStack, f, d, e, g);
			RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			RenderSystem.disableAlphaTest();
			RenderSystem.enableCull();
			RenderSystem.disableBlend();
			RenderSystem.disableFog();
			gameRenderer.method_22709(camera, f, true, false, MathHelper.SQUARE_ROOT_OF_TWO);
		}

		RenderSystem.depthMask(false);
		profiler.swap("weather");
		this.method_22714(lightmapTextureManager, f, d, e, g);
		RenderSystem.depthMask(true);
		this.method_22989(camera);
		RenderSystem.shadeModel(7424);
		RenderSystem.depthMask(true);
		RenderSystem.disableBlend();
		RenderSystem.disableFog();
		RenderSystem.popMatrix();
	}

	private void checkEmpty(MatrixStack matrixStack) {
		if (!matrixStack.isEmpty()) {
			throw new IllegalStateException("Pose stack not empty");
		}
	}

	private void renderEntity(
		Entity entity, double d, double e, double f, float g, MatrixStack matrixStack, LayeredVertexConsumerStorage layeredVertexConsumerStorage
	) {
		double h = MathHelper.lerp((double)g, entity.prevRenderX, entity.getX());
		double i = MathHelper.lerp((double)g, entity.prevRenderY, entity.getY());
		double j = MathHelper.lerp((double)g, entity.prevRenderZ, entity.getZ());
		float k = MathHelper.lerp(g, entity.prevYaw, entity.yaw);
		this.entityRenderDispatcher.render(entity, h - d, i - e, j - f, k, g, matrixStack, layeredVertexConsumerStorage);
	}

	private void renderLayer(RenderLayer renderLayer, MatrixStack matrixStack, double d, double e, double f) {
		renderLayer.method_23516();
		if (renderLayer == RenderLayer.getTranslucent()) {
			this.client.getProfiler().push("translucent_sort");
			double g = d - this.lastTranslucentSortX;
			double h = e - this.lastTranslucentSortY;
			double i = f - this.lastTranslucentSortZ;
			if (g * g + h * h + i * i > 1.0) {
				this.lastTranslucentSortX = d;
				this.lastTranslucentSortY = e;
				this.lastTranslucentSortZ = f;
				int j = 0;

				for (WorldRenderer.ChunkInfo chunkInfo : this.chunkInfos) {
					if (j < 15 && chunkInfo.renderer.method_22773(renderLayer, this.chunkBatcher)) {
						j++;
					}
				}
			}

			this.client.getProfiler().pop();
		}

		this.client.getProfiler().push("filterempty");
		List<ChunkBatcher.ChunkRenderer> list = Lists.<ChunkBatcher.ChunkRenderer>newArrayList();

		for (WorldRenderer.ChunkInfo chunkInfo2 : renderLayer == RenderLayer.getTranslucent() ? Lists.reverse(this.chunkInfos) : this.chunkInfos) {
			ChunkBatcher.ChunkRenderer chunkRenderer = chunkInfo2.renderer;
			if (!chunkRenderer.getData().isEmpty(renderLayer)) {
				list.add(chunkRenderer);
			}
		}

		this.client.getProfiler().swap((Supplier<String>)(() -> "render_" + renderLayer));

		for (ChunkBatcher.ChunkRenderer chunkRenderer2 : list) {
			GlBuffer glBuffer = chunkRenderer2.getGlBuffer(renderLayer);
			matrixStack.push();
			BlockPos blockPos = chunkRenderer2.getOrigin();
			matrixStack.translate((double)blockPos.getX() - d, (double)blockPos.getY() - e, (double)blockPos.getZ() - f);
			glBuffer.bind();
			this.field_20791.method_22649(0L);
			glBuffer.draw(matrixStack.peek(), 7);
			matrixStack.pop();
		}

		GlBuffer.unbind();
		RenderSystem.clearCurrentColor();
		this.field_20791.method_22651();
		this.client.getProfiler().pop();
		renderLayer.method_23518();
	}

	private void method_22989(Camera camera) {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBufferBuilder();
		if (this.client.field_20907 || this.client.field_20908) {
			double d = camera.getPos().getX();
			double e = camera.getPos().getY();
			double f = camera.getPos().getZ();
			RenderSystem.depthMask(true);
			RenderSystem.disableCull();
			RenderSystem.enableBlend();
			RenderSystem.defaultBlendFunc();
			RenderSystem.disableTexture();

			for (WorldRenderer.ChunkInfo chunkInfo : this.chunkInfos) {
				ChunkBatcher.ChunkRenderer chunkRenderer = chunkInfo.renderer;
				RenderSystem.pushMatrix();
				BlockPos blockPos = chunkRenderer.getOrigin();
				RenderSystem.translated((double)blockPos.getX() - d, (double)blockPos.getY() - e, (double)blockPos.getZ() - f);
				if (this.client.field_20907) {
					bufferBuilder.begin(1, VertexFormats.POSITION_COLOR);
					RenderSystem.lineWidth(10.0F);
					int i = chunkInfo.field_4122 == 0 ? 0 : MathHelper.hsvToRgb((float)chunkInfo.field_4122 / 50.0F, 0.9F, 0.9F);
					int j = i >> 16 & 0xFF;
					int k = i >> 8 & 0xFF;
					int l = i & 0xFF;
					Direction direction = chunkInfo.field_4125;
					if (direction != null) {
						bufferBuilder.vertex(8.0, 8.0, 8.0).color(j, k, l, 255).next();
						bufferBuilder.vertex((double)(8 - 16 * direction.getOffsetX()), (double)(8 - 16 * direction.getOffsetY()), (double)(8 - 16 * direction.getOffsetZ()))
							.color(j, k, l, 255)
							.next();
					}

					tessellator.draw();
					RenderSystem.lineWidth(1.0F);
				}

				if (this.client.field_20908 && !chunkRenderer.getData().isEmpty()) {
					bufferBuilder.begin(1, VertexFormats.POSITION_COLOR);
					RenderSystem.lineWidth(10.0F);
					int i = 0;

					for (Direction direction : Direction.values()) {
						for (Direction direction2 : Direction.values()) {
							boolean bl = chunkRenderer.getData().isVisibleThrough(direction, direction2);
							if (!bl) {
								i++;
								bufferBuilder.vertex((double)(8 + 8 * direction.getOffsetX()), (double)(8 + 8 * direction.getOffsetY()), (double)(8 + 8 * direction.getOffsetZ()))
									.color(1, 0, 0, 1)
									.next();
								bufferBuilder.vertex((double)(8 + 8 * direction2.getOffsetX()), (double)(8 + 8 * direction2.getOffsetY()), (double)(8 + 8 * direction2.getOffsetZ()))
									.color(1, 0, 0, 1)
									.next();
							}
						}
					}

					tessellator.draw();
					RenderSystem.lineWidth(1.0F);
					if (i > 0) {
						bufferBuilder.begin(7, VertexFormats.POSITION_COLOR);
						float g = 0.5F;
						float h = 0.2F;
						bufferBuilder.vertex(0.5, 15.5, 0.5).color(0.9F, 0.9F, 0.0F, 0.2F).next();
						bufferBuilder.vertex(15.5, 15.5, 0.5).color(0.9F, 0.9F, 0.0F, 0.2F).next();
						bufferBuilder.vertex(15.5, 15.5, 15.5).color(0.9F, 0.9F, 0.0F, 0.2F).next();
						bufferBuilder.vertex(0.5, 15.5, 15.5).color(0.9F, 0.9F, 0.0F, 0.2F).next();
						bufferBuilder.vertex(0.5, 0.5, 15.5).color(0.9F, 0.9F, 0.0F, 0.2F).next();
						bufferBuilder.vertex(15.5, 0.5, 15.5).color(0.9F, 0.9F, 0.0F, 0.2F).next();
						bufferBuilder.vertex(15.5, 0.5, 0.5).color(0.9F, 0.9F, 0.0F, 0.2F).next();
						bufferBuilder.vertex(0.5, 0.5, 0.5).color(0.9F, 0.9F, 0.0F, 0.2F).next();
						bufferBuilder.vertex(0.5, 15.5, 0.5).color(0.9F, 0.9F, 0.0F, 0.2F).next();
						bufferBuilder.vertex(0.5, 15.5, 15.5).color(0.9F, 0.9F, 0.0F, 0.2F).next();
						bufferBuilder.vertex(0.5, 0.5, 15.5).color(0.9F, 0.9F, 0.0F, 0.2F).next();
						bufferBuilder.vertex(0.5, 0.5, 0.5).color(0.9F, 0.9F, 0.0F, 0.2F).next();
						bufferBuilder.vertex(15.5, 0.5, 0.5).color(0.9F, 0.9F, 0.0F, 0.2F).next();
						bufferBuilder.vertex(15.5, 0.5, 15.5).color(0.9F, 0.9F, 0.0F, 0.2F).next();
						bufferBuilder.vertex(15.5, 15.5, 15.5).color(0.9F, 0.9F, 0.0F, 0.2F).next();
						bufferBuilder.vertex(15.5, 15.5, 0.5).color(0.9F, 0.9F, 0.0F, 0.2F).next();
						bufferBuilder.vertex(0.5, 0.5, 0.5).color(0.9F, 0.9F, 0.0F, 0.2F).next();
						bufferBuilder.vertex(15.5, 0.5, 0.5).color(0.9F, 0.9F, 0.0F, 0.2F).next();
						bufferBuilder.vertex(15.5, 15.5, 0.5).color(0.9F, 0.9F, 0.0F, 0.2F).next();
						bufferBuilder.vertex(0.5, 15.5, 0.5).color(0.9F, 0.9F, 0.0F, 0.2F).next();
						bufferBuilder.vertex(0.5, 15.5, 15.5).color(0.9F, 0.9F, 0.0F, 0.2F).next();
						bufferBuilder.vertex(15.5, 15.5, 15.5).color(0.9F, 0.9F, 0.0F, 0.2F).next();
						bufferBuilder.vertex(15.5, 0.5, 15.5).color(0.9F, 0.9F, 0.0F, 0.2F).next();
						bufferBuilder.vertex(0.5, 0.5, 15.5).color(0.9F, 0.9F, 0.0F, 0.2F).next();
						tessellator.draw();
					}
				}

				RenderSystem.popMatrix();
			}

			RenderSystem.depthMask(true);
			RenderSystem.disableBlend();
			RenderSystem.enableCull();
			RenderSystem.enableTexture();
		}

		if (this.forcedFrustum != null) {
			RenderSystem.disableCull();
			RenderSystem.disableTexture();
			RenderSystem.enableBlend();
			RenderSystem.defaultBlendFunc();
			RenderSystem.disableFog();
			RenderSystem.lineWidth(10.0F);
			RenderSystem.pushMatrix();
			RenderSystem.translatef(
				(float)(this.forcedFrustumPosition.x - camera.getPos().x),
				(float)(this.forcedFrustumPosition.y - camera.getPos().y),
				(float)(this.forcedFrustumPosition.z - camera.getPos().z)
			);
			RenderSystem.depthMask(true);
			bufferBuilder.begin(7, VertexFormats.POSITION_COLOR);
			this.method_22985(bufferBuilder, 0, 1, 2, 3, 0, 1, 1);
			this.method_22985(bufferBuilder, 4, 5, 6, 7, 1, 0, 0);
			this.method_22985(bufferBuilder, 0, 1, 5, 4, 1, 1, 0);
			this.method_22985(bufferBuilder, 2, 3, 7, 6, 0, 0, 1);
			this.method_22985(bufferBuilder, 0, 4, 7, 3, 0, 1, 0);
			this.method_22985(bufferBuilder, 1, 5, 6, 2, 1, 0, 1);
			tessellator.draw();
			RenderSystem.depthMask(false);
			bufferBuilder.begin(1, VertexFormats.POSITION);
			RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			this.method_22984(bufferBuilder, 0);
			this.method_22984(bufferBuilder, 1);
			this.method_22984(bufferBuilder, 1);
			this.method_22984(bufferBuilder, 2);
			this.method_22984(bufferBuilder, 2);
			this.method_22984(bufferBuilder, 3);
			this.method_22984(bufferBuilder, 3);
			this.method_22984(bufferBuilder, 0);
			this.method_22984(bufferBuilder, 4);
			this.method_22984(bufferBuilder, 5);
			this.method_22984(bufferBuilder, 5);
			this.method_22984(bufferBuilder, 6);
			this.method_22984(bufferBuilder, 6);
			this.method_22984(bufferBuilder, 7);
			this.method_22984(bufferBuilder, 7);
			this.method_22984(bufferBuilder, 4);
			this.method_22984(bufferBuilder, 0);
			this.method_22984(bufferBuilder, 4);
			this.method_22984(bufferBuilder, 1);
			this.method_22984(bufferBuilder, 5);
			this.method_22984(bufferBuilder, 2);
			this.method_22984(bufferBuilder, 6);
			this.method_22984(bufferBuilder, 3);
			this.method_22984(bufferBuilder, 7);
			tessellator.draw();
			RenderSystem.popMatrix();
			RenderSystem.depthMask(true);
			RenderSystem.disableBlend();
			RenderSystem.enableCull();
			RenderSystem.enableTexture();
			RenderSystem.enableFog();
			RenderSystem.lineWidth(1.0F);
		}
	}

	private void method_22984(VertexConsumer vertexConsumer, int i) {
		vertexConsumer.vertex((double)this.field_4065[i].getX(), (double)this.field_4065[i].getY(), (double)this.field_4065[i].getZ()).next();
	}

	private void method_22985(VertexConsumer vertexConsumer, int i, int j, int k, int l, int m, int n, int o) {
		float f = 0.25F;
		vertexConsumer.vertex((double)this.field_4065[i].getX(), (double)this.field_4065[i].getY(), (double)this.field_4065[i].getZ())
			.color((float)m, (float)n, (float)o, 0.25F)
			.next();
		vertexConsumer.vertex((double)this.field_4065[j].getX(), (double)this.field_4065[j].getY(), (double)this.field_4065[j].getZ())
			.color((float)m, (float)n, (float)o, 0.25F)
			.next();
		vertexConsumer.vertex((double)this.field_4065[k].getX(), (double)this.field_4065[k].getY(), (double)this.field_4065[k].getZ())
			.color((float)m, (float)n, (float)o, 0.25F)
			.next();
		vertexConsumer.vertex((double)this.field_4065[l].getX(), (double)this.field_4065[l].getY(), (double)this.field_4065[l].getZ())
			.color((float)m, (float)n, (float)o, 0.25F)
			.next();
	}

	public void tick() {
		this.ticks++;
		if (this.ticks % 20 == 0) {
			Iterator<PartiallyBrokenBlockEntry> iterator = this.partiallyBrokenBlocks.values().iterator();

			while (iterator.hasNext()) {
				PartiallyBrokenBlockEntry partiallyBrokenBlockEntry = (PartiallyBrokenBlockEntry)iterator.next();
				int i = partiallyBrokenBlockEntry.getLastUpdateTicks();
				if (this.ticks - i > 400) {
					iterator.remove();
					this.method_22987(partiallyBrokenBlockEntry);
				}
			}
		}
	}

	private void method_22987(PartiallyBrokenBlockEntry partiallyBrokenBlockEntry) {
		long l = partiallyBrokenBlockEntry.getPos().asLong();
		Set<PartiallyBrokenBlockEntry> set = (Set<PartiallyBrokenBlockEntry>)this.field_20950.get(l);
		set.remove(partiallyBrokenBlockEntry);
		if (set.isEmpty()) {
			this.field_20950.remove(l);
		}
	}

	private void renderEndSky(MatrixStack matrixStack) {
		RenderSystem.disableFog();
		RenderSystem.disableAlphaTest();
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.depthMask(false);
		this.textureManager.bindTexture(END_SKY_TEX);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBufferBuilder();

		for (int i = 0; i < 6; i++) {
			matrixStack.push();
			if (i == 1) {
				matrixStack.multiply(Vector3f.POSITIVE_X.getRotationQuaternion(90.0F));
			}

			if (i == 2) {
				matrixStack.multiply(Vector3f.POSITIVE_X.getRotationQuaternion(-90.0F));
			}

			if (i == 3) {
				matrixStack.multiply(Vector3f.POSITIVE_X.getRotationQuaternion(180.0F));
			}

			if (i == 4) {
				matrixStack.multiply(Vector3f.POSITIVE_Z.getRotationQuaternion(90.0F));
			}

			if (i == 5) {
				matrixStack.multiply(Vector3f.POSITIVE_Z.getRotationQuaternion(-90.0F));
			}

			Matrix4f matrix4f = matrixStack.peek();
			bufferBuilder.begin(7, VertexFormats.POSITION_UV_COLOR);
			bufferBuilder.vertex(matrix4f, -100.0F, -100.0F, -100.0F).texture(0.0F, 0.0F).color(40, 40, 40, 255).next();
			bufferBuilder.vertex(matrix4f, -100.0F, -100.0F, 100.0F).texture(0.0F, 16.0F).color(40, 40, 40, 255).next();
			bufferBuilder.vertex(matrix4f, 100.0F, -100.0F, 100.0F).texture(16.0F, 16.0F).color(40, 40, 40, 255).next();
			bufferBuilder.vertex(matrix4f, 100.0F, -100.0F, -100.0F).texture(16.0F, 0.0F).color(40, 40, 40, 255).next();
			tessellator.draw();
			matrixStack.pop();
		}

		RenderSystem.depthMask(true);
		RenderSystem.enableTexture();
		RenderSystem.disableBlend();
		RenderSystem.enableAlphaTest();
	}

	public void renderSky(MatrixStack matrixStack, float f) {
		if (this.client.world.dimension.getType() == DimensionType.THE_END) {
			this.renderEndSky(matrixStack);
		} else if (this.client.world.dimension.hasVisibleSky()) {
			RenderSystem.disableTexture();
			net.minecraft.util.math.Vec3d vec3d = this.world.getSkyColor(this.client.gameRenderer.getCamera().getBlockPos(), f);
			float g = (float)vec3d.x;
			float h = (float)vec3d.y;
			float i = (float)vec3d.z;
			RenderSystem.color3f(g, h, i);
			BufferBuilder bufferBuilder = Tessellator.getInstance().getBufferBuilder();
			RenderSystem.depthMask(false);
			RenderSystem.enableFog();
			RenderSystem.color3f(g, h, i);
			this.field_4087.bind();
			this.field_4100.method_22649(0L);
			this.field_4087.draw(matrixStack.peek(), 7);
			GlBuffer.unbind();
			this.field_4100.method_22651();
			RenderSystem.disableFog();
			RenderSystem.disableAlphaTest();
			RenderSystem.enableBlend();
			RenderSystem.defaultBlendFunc();
			float[] fs = this.world.dimension.getBackgroundColor(this.world.getSkyAngle(f), f);
			if (fs != null) {
				RenderSystem.disableTexture();
				RenderSystem.shadeModel(7425);
				matrixStack.push();
				matrixStack.multiply(Vector3f.POSITIVE_X.getRotationQuaternion(90.0F));
				float j = MathHelper.sin(this.world.getSkyAngleRadians(f)) < 0.0F ? 180.0F : 0.0F;
				matrixStack.multiply(Vector3f.POSITIVE_Z.getRotationQuaternion(j));
				matrixStack.multiply(Vector3f.POSITIVE_Z.getRotationQuaternion(90.0F));
				float k = fs[0];
				float l = fs[1];
				float m = fs[2];
				Matrix4f matrix4f = matrixStack.peek();
				bufferBuilder.begin(6, VertexFormats.POSITION_COLOR);
				bufferBuilder.vertex(matrix4f, 0.0F, 100.0F, 0.0F).color(k, l, m, fs[3]).next();
				int n = 16;

				for (int o = 0; o <= 16; o++) {
					float p = (float)o * (float) (Math.PI * 2) / 16.0F;
					float q = MathHelper.sin(p);
					float r = MathHelper.cos(p);
					bufferBuilder.vertex(matrix4f, q * 120.0F, r * 120.0F, -r * 40.0F * fs[3]).color(fs[0], fs[1], fs[2], 0.0F).next();
				}

				bufferBuilder.end();
				BufferRenderer.draw(bufferBuilder);
				matrixStack.pop();
				RenderSystem.shadeModel(7424);
			}

			RenderSystem.enableTexture();
			RenderSystem.blendFuncSeparate(
				GlStateManager.class_4535.SRC_ALPHA, GlStateManager.class_4534.ONE, GlStateManager.class_4535.ONE, GlStateManager.class_4534.ZERO
			);
			matrixStack.push();
			float j = 1.0F - this.world.getRainGradient(f);
			RenderSystem.color4f(1.0F, 1.0F, 1.0F, j);
			matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion(-90.0F));
			matrixStack.multiply(Vector3f.POSITIVE_X.getRotationQuaternion(this.world.getSkyAngle(f) * 360.0F));
			Matrix4f matrix4f2 = matrixStack.peek();
			float l = 30.0F;
			this.textureManager.bindTexture(SUN_TEX);
			bufferBuilder.begin(7, VertexFormats.POSITION_UV);
			bufferBuilder.vertex(matrix4f2, -l, 100.0F, -l).texture(0.0F, 0.0F).next();
			bufferBuilder.vertex(matrix4f2, l, 100.0F, -l).texture(1.0F, 0.0F).next();
			bufferBuilder.vertex(matrix4f2, l, 100.0F, l).texture(1.0F, 1.0F).next();
			bufferBuilder.vertex(matrix4f2, -l, 100.0F, l).texture(0.0F, 1.0F).next();
			bufferBuilder.end();
			BufferRenderer.draw(bufferBuilder);
			l = 20.0F;
			this.textureManager.bindTexture(MOON_PHASES_TEX);
			int s = this.world.getMoonPhase();
			int t = s % 4;
			int n = s / 4 % 2;
			float u = (float)(t + 0) / 4.0F;
			float p = (float)(n + 0) / 2.0F;
			float q = (float)(t + 1) / 4.0F;
			float r = (float)(n + 1) / 2.0F;
			bufferBuilder.begin(7, VertexFormats.POSITION_UV);
			bufferBuilder.vertex(matrix4f2, -l, -100.0F, l).texture(q, r).next();
			bufferBuilder.vertex(matrix4f2, l, -100.0F, l).texture(u, r).next();
			bufferBuilder.vertex(matrix4f2, l, -100.0F, -l).texture(u, p).next();
			bufferBuilder.vertex(matrix4f2, -l, -100.0F, -l).texture(q, p).next();
			bufferBuilder.end();
			BufferRenderer.draw(bufferBuilder);
			RenderSystem.disableTexture();
			float v = this.world.getStarsBrightness(f) * j;
			if (v > 0.0F) {
				RenderSystem.color4f(v, v, v, v);
				this.starsBuffer.bind();
				this.field_4100.method_22649(0L);
				this.starsBuffer.draw(matrixStack.peek(), 7);
				GlBuffer.unbind();
				this.field_4100.method_22651();
			}

			RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			RenderSystem.disableBlend();
			RenderSystem.enableAlphaTest();
			RenderSystem.enableFog();
			matrixStack.pop();
			RenderSystem.disableTexture();
			RenderSystem.color3f(0.0F, 0.0F, 0.0F);
			double d = this.client.player.getCameraPosVec(f).y - this.world.getHorizonHeight();
			if (d < 0.0) {
				matrixStack.push();
				matrixStack.translate(0.0, 12.0, 0.0);
				this.field_4102.bind();
				this.field_4100.method_22649(0L);
				this.field_4102.draw(matrixStack.peek(), 7);
				GlBuffer.unbind();
				this.field_4100.method_22651();
				matrixStack.pop();
			}

			if (this.world.dimension.method_12449()) {
				RenderSystem.color3f(g * 0.2F + 0.04F, h * 0.2F + 0.04F, i * 0.6F + 0.1F);
			} else {
				RenderSystem.color3f(g, h, i);
			}

			RenderSystem.enableTexture();
			RenderSystem.depthMask(true);
		}
	}

	public void renderClouds(MatrixStack matrixStack, float f, double d, double e, double g) {
		if (this.client.world.dimension.hasVisibleSky()) {
			float h = 12.0F;
			float i = 4.0F;
			double j = 2.0E-4;
			double k = (double)(((float)this.ticks + f) * 0.03F);
			double l = (d + k) / 12.0;
			double m = (double)(this.world.dimension.getCloudHeight() - (float)e + 0.33F);
			double n = g / 12.0 + 0.33F;
			l -= (double)(MathHelper.floor(l / 2048.0) * 2048);
			n -= (double)(MathHelper.floor(n / 2048.0) * 2048);
			float o = (float)(l - (double)MathHelper.floor(l));
			float p = (float)(m / 4.0 - (double)MathHelper.floor(m / 4.0)) * 4.0F;
			float q = (float)(n - (double)MathHelper.floor(n));
			net.minecraft.util.math.Vec3d vec3d = this.world.getCloudColor(f);
			int r = (int)Math.floor(l);
			int s = (int)Math.floor(m / 4.0);
			int t = (int)Math.floor(n);
			if (r != this.field_4082
				|| s != this.field_4097
				|| t != this.field_4116
				|| this.client.options.getCloudRenderMode() != this.field_4080
				|| this.field_4072.squaredDistanceTo(vec3d) > 2.0E-4) {
				this.field_4082 = r;
				this.field_4097 = s;
				this.field_4116 = t;
				this.field_4072 = vec3d;
				this.field_4080 = this.client.options.getCloudRenderMode();
				this.cloudsDirty = true;
			}

			if (this.cloudsDirty) {
				this.cloudsDirty = false;
				BufferBuilder bufferBuilder = Tessellator.getInstance().getBufferBuilder();
				if (this.cloudsBuffer != null) {
					this.cloudsBuffer.delete();
				}

				this.cloudsBuffer = new GlBuffer(VertexFormats.POSITION_UV_COLOR_NORMAL);
				this.renderClouds(bufferBuilder, l, m, n, vec3d);
				bufferBuilder.end();
				this.cloudsBuffer.set(bufferBuilder);
			}

			this.textureManager.bindTexture(CLOUDS_TEX);
			matrixStack.push();
			matrixStack.scale(12.0F, 1.0F, 12.0F);
			matrixStack.translate((double)(-o), (double)p, (double)(-q));
			if (this.cloudsBuffer != null) {
				this.cloudsBuffer.bind();
				VertexFormats.POSITION_UV_COLOR_NORMAL.method_22649(0L);
				int u = this.field_4080 == CloudRenderMode.FANCY ? 0 : 1;

				for (int v = u; v < 2; v++) {
					if (v == 0) {
						RenderSystem.colorMask(false, false, false, false);
					} else {
						RenderSystem.colorMask(true, true, true, true);
					}

					this.cloudsBuffer.draw(matrixStack.peek(), 7);
				}

				GlBuffer.unbind();
				VertexFormats.POSITION_UV_COLOR_NORMAL.method_22651();
			}

			matrixStack.pop();
		}
	}

	private void renderClouds(BufferBuilder bufferBuilder, double d, double e, double f, net.minecraft.util.math.Vec3d vec3d) {
		float g = 4.0F;
		float h = 0.00390625F;
		int i = 8;
		int j = 4;
		float k = 9.765625E-4F;
		float l = (float)MathHelper.floor(d) * 0.00390625F;
		float m = (float)MathHelper.floor(f) * 0.00390625F;
		float n = (float)vec3d.x;
		float o = (float)vec3d.y;
		float p = (float)vec3d.z;
		float q = n * 0.9F;
		float r = o * 0.9F;
		float s = p * 0.9F;
		float t = n * 0.7F;
		float u = o * 0.7F;
		float v = p * 0.7F;
		float w = n * 0.8F;
		float x = o * 0.8F;
		float y = p * 0.8F;
		bufferBuilder.begin(7, VertexFormats.POSITION_UV_COLOR_NORMAL);
		float z = (float)Math.floor(e / 4.0) * 4.0F;
		if (this.field_4080 == CloudRenderMode.FANCY) {
			for (int aa = -3; aa <= 4; aa++) {
				for (int ab = -3; ab <= 4; ab++) {
					float ac = (float)(aa * 8);
					float ad = (float)(ab * 8);
					if (z > -5.0F) {
						bufferBuilder.vertex((double)(ac + 0.0F), (double)(z + 0.0F), (double)(ad + 8.0F))
							.texture((ac + 0.0F) * 0.00390625F + l, (ad + 8.0F) * 0.00390625F + m)
							.color(t, u, v, 0.8F)
							.normal(0.0F, -1.0F, 0.0F)
							.next();
						bufferBuilder.vertex((double)(ac + 8.0F), (double)(z + 0.0F), (double)(ad + 8.0F))
							.texture((ac + 8.0F) * 0.00390625F + l, (ad + 8.0F) * 0.00390625F + m)
							.color(t, u, v, 0.8F)
							.normal(0.0F, -1.0F, 0.0F)
							.next();
						bufferBuilder.vertex((double)(ac + 8.0F), (double)(z + 0.0F), (double)(ad + 0.0F))
							.texture((ac + 8.0F) * 0.00390625F + l, (ad + 0.0F) * 0.00390625F + m)
							.color(t, u, v, 0.8F)
							.normal(0.0F, -1.0F, 0.0F)
							.next();
						bufferBuilder.vertex((double)(ac + 0.0F), (double)(z + 0.0F), (double)(ad + 0.0F))
							.texture((ac + 0.0F) * 0.00390625F + l, (ad + 0.0F) * 0.00390625F + m)
							.color(t, u, v, 0.8F)
							.normal(0.0F, -1.0F, 0.0F)
							.next();
					}

					if (z <= 5.0F) {
						bufferBuilder.vertex((double)(ac + 0.0F), (double)(z + 4.0F - 9.765625E-4F), (double)(ad + 8.0F))
							.texture((ac + 0.0F) * 0.00390625F + l, (ad + 8.0F) * 0.00390625F + m)
							.color(n, o, p, 0.8F)
							.normal(0.0F, 1.0F, 0.0F)
							.next();
						bufferBuilder.vertex((double)(ac + 8.0F), (double)(z + 4.0F - 9.765625E-4F), (double)(ad + 8.0F))
							.texture((ac + 8.0F) * 0.00390625F + l, (ad + 8.0F) * 0.00390625F + m)
							.color(n, o, p, 0.8F)
							.normal(0.0F, 1.0F, 0.0F)
							.next();
						bufferBuilder.vertex((double)(ac + 8.0F), (double)(z + 4.0F - 9.765625E-4F), (double)(ad + 0.0F))
							.texture((ac + 8.0F) * 0.00390625F + l, (ad + 0.0F) * 0.00390625F + m)
							.color(n, o, p, 0.8F)
							.normal(0.0F, 1.0F, 0.0F)
							.next();
						bufferBuilder.vertex((double)(ac + 0.0F), (double)(z + 4.0F - 9.765625E-4F), (double)(ad + 0.0F))
							.texture((ac + 0.0F) * 0.00390625F + l, (ad + 0.0F) * 0.00390625F + m)
							.color(n, o, p, 0.8F)
							.normal(0.0F, 1.0F, 0.0F)
							.next();
					}

					if (aa > -1) {
						for (int ae = 0; ae < 8; ae++) {
							bufferBuilder.vertex((double)(ac + (float)ae + 0.0F), (double)(z + 0.0F), (double)(ad + 8.0F))
								.texture((ac + (float)ae + 0.5F) * 0.00390625F + l, (ad + 8.0F) * 0.00390625F + m)
								.color(q, r, s, 0.8F)
								.normal(-1.0F, 0.0F, 0.0F)
								.next();
							bufferBuilder.vertex((double)(ac + (float)ae + 0.0F), (double)(z + 4.0F), (double)(ad + 8.0F))
								.texture((ac + (float)ae + 0.5F) * 0.00390625F + l, (ad + 8.0F) * 0.00390625F + m)
								.color(q, r, s, 0.8F)
								.normal(-1.0F, 0.0F, 0.0F)
								.next();
							bufferBuilder.vertex((double)(ac + (float)ae + 0.0F), (double)(z + 4.0F), (double)(ad + 0.0F))
								.texture((ac + (float)ae + 0.5F) * 0.00390625F + l, (ad + 0.0F) * 0.00390625F + m)
								.color(q, r, s, 0.8F)
								.normal(-1.0F, 0.0F, 0.0F)
								.next();
							bufferBuilder.vertex((double)(ac + (float)ae + 0.0F), (double)(z + 0.0F), (double)(ad + 0.0F))
								.texture((ac + (float)ae + 0.5F) * 0.00390625F + l, (ad + 0.0F) * 0.00390625F + m)
								.color(q, r, s, 0.8F)
								.normal(-1.0F, 0.0F, 0.0F)
								.next();
						}
					}

					if (aa <= 1) {
						for (int ae = 0; ae < 8; ae++) {
							bufferBuilder.vertex((double)(ac + (float)ae + 1.0F - 9.765625E-4F), (double)(z + 0.0F), (double)(ad + 8.0F))
								.texture((ac + (float)ae + 0.5F) * 0.00390625F + l, (ad + 8.0F) * 0.00390625F + m)
								.color(q, r, s, 0.8F)
								.normal(1.0F, 0.0F, 0.0F)
								.next();
							bufferBuilder.vertex((double)(ac + (float)ae + 1.0F - 9.765625E-4F), (double)(z + 4.0F), (double)(ad + 8.0F))
								.texture((ac + (float)ae + 0.5F) * 0.00390625F + l, (ad + 8.0F) * 0.00390625F + m)
								.color(q, r, s, 0.8F)
								.normal(1.0F, 0.0F, 0.0F)
								.next();
							bufferBuilder.vertex((double)(ac + (float)ae + 1.0F - 9.765625E-4F), (double)(z + 4.0F), (double)(ad + 0.0F))
								.texture((ac + (float)ae + 0.5F) * 0.00390625F + l, (ad + 0.0F) * 0.00390625F + m)
								.color(q, r, s, 0.8F)
								.normal(1.0F, 0.0F, 0.0F)
								.next();
							bufferBuilder.vertex((double)(ac + (float)ae + 1.0F - 9.765625E-4F), (double)(z + 0.0F), (double)(ad + 0.0F))
								.texture((ac + (float)ae + 0.5F) * 0.00390625F + l, (ad + 0.0F) * 0.00390625F + m)
								.color(q, r, s, 0.8F)
								.normal(1.0F, 0.0F, 0.0F)
								.next();
						}
					}

					if (ab > -1) {
						for (int ae = 0; ae < 8; ae++) {
							bufferBuilder.vertex((double)(ac + 0.0F), (double)(z + 4.0F), (double)(ad + (float)ae + 0.0F))
								.texture((ac + 0.0F) * 0.00390625F + l, (ad + (float)ae + 0.5F) * 0.00390625F + m)
								.color(w, x, y, 0.8F)
								.normal(0.0F, 0.0F, -1.0F)
								.next();
							bufferBuilder.vertex((double)(ac + 8.0F), (double)(z + 4.0F), (double)(ad + (float)ae + 0.0F))
								.texture((ac + 8.0F) * 0.00390625F + l, (ad + (float)ae + 0.5F) * 0.00390625F + m)
								.color(w, x, y, 0.8F)
								.normal(0.0F, 0.0F, -1.0F)
								.next();
							bufferBuilder.vertex((double)(ac + 8.0F), (double)(z + 0.0F), (double)(ad + (float)ae + 0.0F))
								.texture((ac + 8.0F) * 0.00390625F + l, (ad + (float)ae + 0.5F) * 0.00390625F + m)
								.color(w, x, y, 0.8F)
								.normal(0.0F, 0.0F, -1.0F)
								.next();
							bufferBuilder.vertex((double)(ac + 0.0F), (double)(z + 0.0F), (double)(ad + (float)ae + 0.0F))
								.texture((ac + 0.0F) * 0.00390625F + l, (ad + (float)ae + 0.5F) * 0.00390625F + m)
								.color(w, x, y, 0.8F)
								.normal(0.0F, 0.0F, -1.0F)
								.next();
						}
					}

					if (ab <= 1) {
						for (int ae = 0; ae < 8; ae++) {
							bufferBuilder.vertex((double)(ac + 0.0F), (double)(z + 4.0F), (double)(ad + (float)ae + 1.0F - 9.765625E-4F))
								.texture((ac + 0.0F) * 0.00390625F + l, (ad + (float)ae + 0.5F) * 0.00390625F + m)
								.color(w, x, y, 0.8F)
								.normal(0.0F, 0.0F, 1.0F)
								.next();
							bufferBuilder.vertex((double)(ac + 8.0F), (double)(z + 4.0F), (double)(ad + (float)ae + 1.0F - 9.765625E-4F))
								.texture((ac + 8.0F) * 0.00390625F + l, (ad + (float)ae + 0.5F) * 0.00390625F + m)
								.color(w, x, y, 0.8F)
								.normal(0.0F, 0.0F, 1.0F)
								.next();
							bufferBuilder.vertex((double)(ac + 8.0F), (double)(z + 0.0F), (double)(ad + (float)ae + 1.0F - 9.765625E-4F))
								.texture((ac + 8.0F) * 0.00390625F + l, (ad + (float)ae + 0.5F) * 0.00390625F + m)
								.color(w, x, y, 0.8F)
								.normal(0.0F, 0.0F, 1.0F)
								.next();
							bufferBuilder.vertex((double)(ac + 0.0F), (double)(z + 0.0F), (double)(ad + (float)ae + 1.0F - 9.765625E-4F))
								.texture((ac + 0.0F) * 0.00390625F + l, (ad + (float)ae + 0.5F) * 0.00390625F + m)
								.color(w, x, y, 0.8F)
								.normal(0.0F, 0.0F, 1.0F)
								.next();
						}
					}
				}
			}
		} else {
			int aa = 1;
			int ab = 32;

			for (int af = -32; af < 32; af += 32) {
				for (int ag = -32; ag < 32; ag += 32) {
					bufferBuilder.vertex((double)(af + 0), (double)z, (double)(ag + 32))
						.texture((float)(af + 0) * 0.00390625F + l, (float)(ag + 32) * 0.00390625F + m)
						.color(n, o, p, 0.8F)
						.normal(0.0F, -1.0F, 0.0F)
						.next();
					bufferBuilder.vertex((double)(af + 32), (double)z, (double)(ag + 32))
						.texture((float)(af + 32) * 0.00390625F + l, (float)(ag + 32) * 0.00390625F + m)
						.color(n, o, p, 0.8F)
						.normal(0.0F, -1.0F, 0.0F)
						.next();
					bufferBuilder.vertex((double)(af + 32), (double)z, (double)(ag + 0))
						.texture((float)(af + 32) * 0.00390625F + l, (float)(ag + 0) * 0.00390625F + m)
						.color(n, o, p, 0.8F)
						.normal(0.0F, -1.0F, 0.0F)
						.next();
					bufferBuilder.vertex((double)(af + 0), (double)z, (double)(ag + 0))
						.texture((float)(af + 0) * 0.00390625F + l, (float)(ag + 0) * 0.00390625F + m)
						.color(n, o, p, 0.8F)
						.normal(0.0F, -1.0F, 0.0F)
						.next();
				}
			}
		}
	}

	private void updateChunks(long l) {
		this.terrainUpdateNecessary = this.terrainUpdateNecessary | this.chunkBatcher.method_22761();
		if (!this.chunkRenderers.isEmpty()) {
			Iterator<ChunkBatcher.ChunkRenderer> iterator = this.chunkRenderers.iterator();

			while (iterator.hasNext()) {
				ChunkBatcher.ChunkRenderer chunkRenderer = (ChunkBatcher.ChunkRenderer)iterator.next();
				if (chunkRenderer.shouldRebuildOnClientThread()) {
					this.chunkBatcher.rebuildSync(chunkRenderer);
				} else {
					chunkRenderer.method_22777(this.chunkBatcher);
				}

				chunkRenderer.unscheduleRebuild();
				iterator.remove();
				long m = l - SystemUtil.getMeasuringTimeNano();
				if (m < 0L) {
					break;
				}
			}
		}
	}

	private void renderWorldBorder(Camera camera, float f) {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBufferBuilder();
		WorldBorder worldBorder = this.world.getWorldBorder();
		double d = (double)(this.client.options.viewDistance * 16);
		if (!(camera.getPos().x < worldBorder.getBoundEast() - d)
			|| !(camera.getPos().x > worldBorder.getBoundWest() + d)
			|| !(camera.getPos().z < worldBorder.getBoundSouth() - d)
			|| !(camera.getPos().z > worldBorder.getBoundNorth() + d)) {
			double e = 1.0 - worldBorder.getDistanceInsideBorder(camera.getPos().x, camera.getPos().z) / d;
			e = Math.pow(e, 4.0);
			double g = camera.getPos().x;
			double h = camera.getPos().y;
			double i = camera.getPos().z;
			RenderSystem.enableBlend();
			RenderSystem.blendFuncSeparate(
				GlStateManager.class_4535.SRC_ALPHA, GlStateManager.class_4534.ONE, GlStateManager.class_4535.ONE, GlStateManager.class_4534.ZERO
			);
			this.textureManager.bindTexture(FORCEFIELD_TEX);
			RenderSystem.depthMask(false);
			RenderSystem.pushMatrix();
			int j = worldBorder.getStage().getColor();
			float k = (float)(j >> 16 & 0xFF) / 255.0F;
			float l = (float)(j >> 8 & 0xFF) / 255.0F;
			float m = (float)(j & 0xFF) / 255.0F;
			RenderSystem.color4f(k, l, m, (float)e);
			RenderSystem.polygonOffset(-3.0F, -3.0F);
			RenderSystem.enablePolygonOffset();
			RenderSystem.defaultAlphaFunc();
			RenderSystem.enableAlphaTest();
			RenderSystem.disableCull();
			float n = (float)(SystemUtil.getMeasuringTimeMs() % 3000L) / 3000.0F;
			float o = 0.0F;
			float p = 0.0F;
			float q = 128.0F;
			bufferBuilder.begin(7, VertexFormats.POSITION_UV);
			double r = Math.max((double)MathHelper.floor(i - d), worldBorder.getBoundNorth());
			double s = Math.min((double)MathHelper.ceil(i + d), worldBorder.getBoundSouth());
			if (g > worldBorder.getBoundEast() - d) {
				float t = 0.0F;

				for (double u = r; u < s; t += 0.5F) {
					double v = Math.min(1.0, s - u);
					float w = (float)v * 0.5F;
					this.method_22978(bufferBuilder, g, h, i, worldBorder.getBoundEast(), 256, u, n + t, n + 0.0F);
					this.method_22978(bufferBuilder, g, h, i, worldBorder.getBoundEast(), 256, u + v, n + w + t, n + 0.0F);
					this.method_22978(bufferBuilder, g, h, i, worldBorder.getBoundEast(), 0, u + v, n + w + t, n + 128.0F);
					this.method_22978(bufferBuilder, g, h, i, worldBorder.getBoundEast(), 0, u, n + t, n + 128.0F);
					u++;
				}
			}

			if (g < worldBorder.getBoundWest() + d) {
				float t = 0.0F;

				for (double u = r; u < s; t += 0.5F) {
					double v = Math.min(1.0, s - u);
					float w = (float)v * 0.5F;
					this.method_22978(bufferBuilder, g, h, i, worldBorder.getBoundWest(), 256, u, n + t, n + 0.0F);
					this.method_22978(bufferBuilder, g, h, i, worldBorder.getBoundWest(), 256, u + v, n + w + t, n + 0.0F);
					this.method_22978(bufferBuilder, g, h, i, worldBorder.getBoundWest(), 0, u + v, n + w + t, n + 128.0F);
					this.method_22978(bufferBuilder, g, h, i, worldBorder.getBoundWest(), 0, u, n + t, n + 128.0F);
					u++;
				}
			}

			r = Math.max((double)MathHelper.floor(g - d), worldBorder.getBoundWest());
			s = Math.min((double)MathHelper.ceil(g + d), worldBorder.getBoundEast());
			if (i > worldBorder.getBoundSouth() - d) {
				float t = 0.0F;

				for (double u = r; u < s; t += 0.5F) {
					double v = Math.min(1.0, s - u);
					float w = (float)v * 0.5F;
					this.method_22978(bufferBuilder, g, h, i, u, 256, worldBorder.getBoundSouth(), n + t, n + 0.0F);
					this.method_22978(bufferBuilder, g, h, i, u + v, 256, worldBorder.getBoundSouth(), n + w + t, n + 0.0F);
					this.method_22978(bufferBuilder, g, h, i, u + v, 0, worldBorder.getBoundSouth(), n + w + t, n + 128.0F);
					this.method_22978(bufferBuilder, g, h, i, u, 0, worldBorder.getBoundSouth(), n + t, n + 128.0F);
					u++;
				}
			}

			if (i < worldBorder.getBoundNorth() + d) {
				float t = 0.0F;

				for (double u = r; u < s; t += 0.5F) {
					double v = Math.min(1.0, s - u);
					float w = (float)v * 0.5F;
					this.method_22978(bufferBuilder, g, h, i, u, 256, worldBorder.getBoundNorth(), n + t, n + 0.0F);
					this.method_22978(bufferBuilder, g, h, i, u + v, 256, worldBorder.getBoundNorth(), n + w + t, n + 0.0F);
					this.method_22978(bufferBuilder, g, h, i, u + v, 0, worldBorder.getBoundNorth(), n + w + t, n + 128.0F);
					this.method_22978(bufferBuilder, g, h, i, u, 0, worldBorder.getBoundNorth(), n + t, n + 128.0F);
					u++;
				}
			}

			tessellator.draw();
			RenderSystem.enableCull();
			RenderSystem.disableAlphaTest();
			RenderSystem.polygonOffset(0.0F, 0.0F);
			RenderSystem.disablePolygonOffset();
			RenderSystem.enableAlphaTest();
			RenderSystem.disableBlend();
			RenderSystem.popMatrix();
			RenderSystem.depthMask(true);
		}
	}

	private void method_22978(BufferBuilder bufferBuilder, double d, double e, double f, double g, int i, double h, float j, float k) {
		bufferBuilder.vertex(g - d, (double)i - e, h - f).texture(j, k).next();
	}

	private void drawBlockOutline(
		MatrixStack matrixStack, VertexConsumer vertexConsumer, Entity entity, double d, double e, double f, BlockPos blockPos, BlockState blockState
	) {
		drawShapeOutline(
			matrixStack,
			vertexConsumer,
			blockState.getOutlineShape(this.world, blockPos, EntityContext.of(entity)),
			(double)blockPos.getX() - d,
			(double)blockPos.getY() - e,
			(double)blockPos.getZ() - f,
			0.0F,
			0.0F,
			0.0F,
			0.4F
		);
	}

	public static void method_22983(
		MatrixStack matrixStack, VertexConsumer vertexConsumer, VoxelShape voxelShape, double d, double e, double f, float g, float h, float i, float j
	) {
		List<Box> list = voxelShape.getBoundingBoxes();
		int k = MathHelper.ceil((double)list.size() / 3.0);

		for (int l = 0; l < list.size(); l++) {
			Box box = (Box)list.get(l);
			float m = ((float)l % (float)k + 1.0F) / (float)k;
			float n = (float)(l / k);
			float o = m * (float)(n == 0.0F ? 1 : 0);
			float p = m * (float)(n == 1.0F ? 1 : 0);
			float q = m * (float)(n == 2.0F ? 1 : 0);
			drawShapeOutline(matrixStack, vertexConsumer, VoxelShapes.cuboid(box.offset(0.0, 0.0, 0.0)), d, e, f, o, p, q, 1.0F);
		}
	}

	private static void drawShapeOutline(
		MatrixStack matrixStack, VertexConsumer vertexConsumer, VoxelShape voxelShape, double d, double e, double f, float g, float h, float i, float j
	) {
		Matrix4f matrix4f = matrixStack.peek();
		voxelShape.forEachEdge((k, l, m, n, o, p) -> {
			vertexConsumer.vertex(matrix4f, (float)(k + d), (float)(l + e), (float)(m + f)).color(g, h, i, j).next();
			vertexConsumer.vertex(matrix4f, (float)(n + d), (float)(o + e), (float)(p + f)).color(g, h, i, j).next();
		});
	}

	public static void drawBoxOutline(
		VertexConsumer vertexConsumer, double d, double e, double f, double g, double h, double i, float j, float k, float l, float m
	) {
		method_22981(new MatrixStack(), vertexConsumer, d, e, f, g, h, i, j, k, l, m, j, k, l);
	}

	public static void method_22982(MatrixStack matrixStack, VertexConsumer vertexConsumer, Box box, float f, float g, float h, float i) {
		method_22981(matrixStack, vertexConsumer, box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ, f, g, h, i, f, g, h);
	}

	public static void method_22980(
		MatrixStack matrixStack, VertexConsumer vertexConsumer, double d, double e, double f, double g, double h, double i, float j, float k, float l, float m
	) {
		method_22981(matrixStack, vertexConsumer, d, e, f, g, h, i, j, k, l, m, j, k, l);
	}

	public static void method_22981(
		MatrixStack matrixStack,
		VertexConsumer vertexConsumer,
		double d,
		double e,
		double f,
		double g,
		double h,
		double i,
		float j,
		float k,
		float l,
		float m,
		float n,
		float o,
		float p
	) {
		Matrix4f matrix4f = matrixStack.peek();
		float q = (float)d;
		float r = (float)e;
		float s = (float)f;
		float t = (float)g;
		float u = (float)h;
		float v = (float)i;
		vertexConsumer.vertex(matrix4f, q, r, s).color(j, o, p, m).next();
		vertexConsumer.vertex(matrix4f, t, r, s).color(j, o, p, m).next();
		vertexConsumer.vertex(matrix4f, q, r, s).color(n, k, p, m).next();
		vertexConsumer.vertex(matrix4f, q, u, s).color(n, k, p, m).next();
		vertexConsumer.vertex(matrix4f, q, r, s).color(n, o, l, m).next();
		vertexConsumer.vertex(matrix4f, q, r, v).color(n, o, l, m).next();
		vertexConsumer.vertex(matrix4f, t, r, s).color(j, k, l, m).next();
		vertexConsumer.vertex(matrix4f, t, u, s).color(j, k, l, m).next();
		vertexConsumer.vertex(matrix4f, t, u, s).color(j, k, l, m).next();
		vertexConsumer.vertex(matrix4f, q, u, s).color(j, k, l, m).next();
		vertexConsumer.vertex(matrix4f, q, u, s).color(j, k, l, m).next();
		vertexConsumer.vertex(matrix4f, q, u, v).color(j, k, l, m).next();
		vertexConsumer.vertex(matrix4f, q, u, v).color(j, k, l, m).next();
		vertexConsumer.vertex(matrix4f, q, r, v).color(j, k, l, m).next();
		vertexConsumer.vertex(matrix4f, q, r, v).color(j, k, l, m).next();
		vertexConsumer.vertex(matrix4f, t, r, v).color(j, k, l, m).next();
		vertexConsumer.vertex(matrix4f, t, r, v).color(j, k, l, m).next();
		vertexConsumer.vertex(matrix4f, t, r, s).color(j, k, l, m).next();
		vertexConsumer.vertex(matrix4f, q, u, v).color(j, k, l, m).next();
		vertexConsumer.vertex(matrix4f, t, u, v).color(j, k, l, m).next();
		vertexConsumer.vertex(matrix4f, t, r, v).color(j, k, l, m).next();
		vertexConsumer.vertex(matrix4f, t, u, v).color(j, k, l, m).next();
		vertexConsumer.vertex(matrix4f, t, u, s).color(j, k, l, m).next();
		vertexConsumer.vertex(matrix4f, t, u, v).color(j, k, l, m).next();
	}

	public static void buildBoxOutline(BufferBuilder bufferBuilder, double d, double e, double f, double g, double h, double i, float j, float k, float l, float m) {
		bufferBuilder.vertex(d, e, f).color(j, k, l, m).next();
		bufferBuilder.vertex(d, e, f).color(j, k, l, m).next();
		bufferBuilder.vertex(d, e, f).color(j, k, l, m).next();
		bufferBuilder.vertex(d, e, i).color(j, k, l, m).next();
		bufferBuilder.vertex(d, h, f).color(j, k, l, m).next();
		bufferBuilder.vertex(d, h, i).color(j, k, l, m).next();
		bufferBuilder.vertex(d, h, i).color(j, k, l, m).next();
		bufferBuilder.vertex(d, e, i).color(j, k, l, m).next();
		bufferBuilder.vertex(g, h, i).color(j, k, l, m).next();
		bufferBuilder.vertex(g, e, i).color(j, k, l, m).next();
		bufferBuilder.vertex(g, e, i).color(j, k, l, m).next();
		bufferBuilder.vertex(g, e, f).color(j, k, l, m).next();
		bufferBuilder.vertex(g, h, i).color(j, k, l, m).next();
		bufferBuilder.vertex(g, h, f).color(j, k, l, m).next();
		bufferBuilder.vertex(g, h, f).color(j, k, l, m).next();
		bufferBuilder.vertex(g, e, f).color(j, k, l, m).next();
		bufferBuilder.vertex(d, h, f).color(j, k, l, m).next();
		bufferBuilder.vertex(d, e, f).color(j, k, l, m).next();
		bufferBuilder.vertex(d, e, f).color(j, k, l, m).next();
		bufferBuilder.vertex(g, e, f).color(j, k, l, m).next();
		bufferBuilder.vertex(d, e, i).color(j, k, l, m).next();
		bufferBuilder.vertex(g, e, i).color(j, k, l, m).next();
		bufferBuilder.vertex(g, e, i).color(j, k, l, m).next();
		bufferBuilder.vertex(d, h, f).color(j, k, l, m).next();
		bufferBuilder.vertex(d, h, f).color(j, k, l, m).next();
		bufferBuilder.vertex(d, h, i).color(j, k, l, m).next();
		bufferBuilder.vertex(g, h, f).color(j, k, l, m).next();
		bufferBuilder.vertex(g, h, i).color(j, k, l, m).next();
		bufferBuilder.vertex(g, h, i).color(j, k, l, m).next();
		bufferBuilder.vertex(g, h, i).color(j, k, l, m).next();
	}

	public void updateBlock(BlockView blockView, BlockPos blockPos, BlockState blockState, BlockState blockState2, int i) {
		this.scheduleSectionRender(blockPos, (i & 8) != 0);
	}

	private void scheduleSectionRender(BlockPos blockPos, boolean bl) {
		for (int i = blockPos.getZ() - 1; i <= blockPos.getZ() + 1; i++) {
			for (int j = blockPos.getX() - 1; j <= blockPos.getX() + 1; j++) {
				for (int k = blockPos.getY() - 1; k <= blockPos.getY() + 1; k++) {
					this.scheduleChunkRender(j >> 4, k >> 4, i >> 4, bl);
				}
			}
		}
	}

	public void scheduleBlockRenders(int i, int j, int k, int l, int m, int n) {
		for (int o = k - 1; o <= n + 1; o++) {
			for (int p = i - 1; p <= l + 1; p++) {
				for (int q = j - 1; q <= m + 1; q++) {
					this.scheduleBlockRender(p >> 4, q >> 4, o >> 4);
				}
			}
		}
	}

	public void method_21596(BlockPos blockPos, BlockState blockState, BlockState blockState2) {
		if (this.client.getBakedModelManager().method_21611(blockState, blockState2)) {
			this.scheduleBlockRenders(blockPos.getX(), blockPos.getY(), blockPos.getZ(), blockPos.getX(), blockPos.getY(), blockPos.getZ());
		}
	}

	public void scheduleBlockRenders(int i, int j, int k) {
		for (int l = k - 1; l <= k + 1; l++) {
			for (int m = i - 1; m <= i + 1; m++) {
				for (int n = j - 1; n <= j + 1; n++) {
					this.scheduleBlockRender(m, n, l);
				}
			}
		}
	}

	public void scheduleBlockRender(int i, int j, int k) {
		this.scheduleChunkRender(i, j, k, false);
	}

	private void scheduleChunkRender(int i, int j, int k, boolean bl) {
		this.chunkRenderDispatcher.scheduleChunkRender(i, j, k, bl);
	}

	public void playSong(@Nullable SoundEvent soundEvent, BlockPos blockPos) {
		SoundInstance soundInstance = (SoundInstance)this.playingSongs.get(blockPos);
		if (soundInstance != null) {
			this.client.getSoundManager().stop(soundInstance);
			this.playingSongs.remove(blockPos);
		}

		if (soundEvent != null) {
			MusicDiscItem musicDiscItem = MusicDiscItem.bySound(soundEvent);
			if (musicDiscItem != null) {
				this.client.inGameHud.setRecordPlayingOverlay(musicDiscItem.getDescription().asFormattedString());
			}

			SoundInstance var5 = PositionedSoundInstance.record(soundEvent, (float)blockPos.getX(), (float)blockPos.getY(), (float)blockPos.getZ());
			this.playingSongs.put(blockPos, var5);
			this.client.getSoundManager().play(var5);
		}

		this.updateEntitiesForSong(this.world, blockPos, soundEvent != null);
	}

	private void updateEntitiesForSong(World world, BlockPos blockPos, boolean bl) {
		for (LivingEntity livingEntity : world.getNonSpectatingEntities(LivingEntity.class, new Box(blockPos).expand(3.0))) {
			livingEntity.setNearbySongPlaying(blockPos, bl);
		}
	}

	public void addParticle(ParticleEffect particleEffect, boolean bl, double d, double e, double f, double g, double h, double i) {
		this.addParticle(particleEffect, bl, false, d, e, f, g, h, i);
	}

	public void addParticle(ParticleEffect particleEffect, boolean bl, boolean bl2, double d, double e, double f, double g, double h, double i) {
		try {
			this.spawnParticle(particleEffect, bl, bl2, d, e, f, g, h, i);
		} catch (Throwable var19) {
			CrashReport crashReport = CrashReport.create(var19, "Exception while adding particle");
			CrashReportSection crashReportSection = crashReport.addElement("Particle being added");
			crashReportSection.add("ID", Registry.PARTICLE_TYPE.getId((ParticleType<? extends ParticleEffect>)particleEffect.getType()));
			crashReportSection.add("Parameters", particleEffect.asString());
			crashReportSection.add("Position", (CrashCallable<String>)(() -> CrashReportSection.createPositionString(d, e, f)));
			throw new CrashException(crashReport);
		}
	}

	private <T extends ParticleEffect> void addParticle(T particleEffect, double d, double e, double f, double g, double h, double i) {
		this.addParticle(particleEffect, particleEffect.getType().shouldAlwaysSpawn(), d, e, f, g, h, i);
	}

	@Nullable
	private Particle spawnParticle(ParticleEffect particleEffect, boolean bl, double d, double e, double f, double g, double h, double i) {
		return this.spawnParticle(particleEffect, bl, false, d, e, f, g, h, i);
	}

	@Nullable
	private Particle spawnParticle(ParticleEffect particleEffect, boolean bl, boolean bl2, double d, double e, double f, double g, double h, double i) {
		Camera camera = this.client.gameRenderer.getCamera();
		if (this.client != null && camera.isReady() && this.client.particleManager != null) {
			ParticlesOption particlesOption = this.getRandomParticleSpawnChance(bl2);
			if (bl) {
				return this.client.particleManager.addParticle(particleEffect, d, e, f, g, h, i);
			} else if (camera.getPos().squaredDistanceTo(d, e, f) > 1024.0) {
				return null;
			} else {
				return particlesOption == ParticlesOption.MINIMAL ? null : this.client.particleManager.addParticle(particleEffect, d, e, f, g, h, i);
			}
		} else {
			return null;
		}
	}

	private ParticlesOption getRandomParticleSpawnChance(boolean bl) {
		ParticlesOption particlesOption = this.client.options.particles;
		if (bl && particlesOption == ParticlesOption.MINIMAL && this.world.random.nextInt(10) == 0) {
			particlesOption = ParticlesOption.DECREASED;
		}

		if (particlesOption == ParticlesOption.DECREASED && this.world.random.nextInt(3) == 0) {
			particlesOption = ParticlesOption.MINIMAL;
		}

		return particlesOption;
	}

	public void method_3267() {
	}

	public void playGlobalEvent(int i, BlockPos blockPos, int j) {
		switch (i) {
			case 1023:
			case 1028:
			case 1038:
				Camera camera = this.client.gameRenderer.getCamera();
				if (camera.isReady()) {
					double d = (double)blockPos.getX() - camera.getPos().x;
					double e = (double)blockPos.getY() - camera.getPos().y;
					double f = (double)blockPos.getZ() - camera.getPos().z;
					double g = Math.sqrt(d * d + e * e + f * f);
					double h = camera.getPos().x;
					double k = camera.getPos().y;
					double l = camera.getPos().z;
					if (g > 0.0) {
						h += d / g * 2.0;
						k += e / g * 2.0;
						l += f / g * 2.0;
					}

					if (i == 1023) {
						this.world.playSound(h, k, l, SoundEvents.ENTITY_WITHER_SPAWN, SoundCategory.HOSTILE, 1.0F, 1.0F, false);
					} else if (i == 1038) {
						this.world.playSound(h, k, l, SoundEvents.BLOCK_END_PORTAL_SPAWN, SoundCategory.HOSTILE, 1.0F, 1.0F, false);
					} else {
						this.world.playSound(h, k, l, SoundEvents.ENTITY_ENDER_DRAGON_DEATH, SoundCategory.HOSTILE, 5.0F, 1.0F, false);
					}
				}
		}
	}

	public void playLevelEvent(PlayerEntity playerEntity, int i, BlockPos blockPos, int j) {
		Random random = this.world.random;
		switch (i) {
			case 1000:
				this.world.playSound(blockPos, SoundEvents.BLOCK_DISPENSER_DISPENSE, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
				break;
			case 1001:
				this.world.playSound(blockPos, SoundEvents.BLOCK_DISPENSER_FAIL, SoundCategory.BLOCKS, 1.0F, 1.2F, false);
				break;
			case 1002:
				this.world.playSound(blockPos, SoundEvents.BLOCK_DISPENSER_LAUNCH, SoundCategory.BLOCKS, 1.0F, 1.2F, false);
				break;
			case 1003:
				this.world.playSound(blockPos, SoundEvents.ENTITY_ENDER_EYE_LAUNCH, SoundCategory.NEUTRAL, 1.0F, 1.2F, false);
				break;
			case 1004:
				this.world.playSound(blockPos, SoundEvents.ENTITY_FIREWORK_ROCKET_SHOOT, SoundCategory.NEUTRAL, 1.0F, 1.2F, false);
				break;
			case 1005:
				this.world.playSound(blockPos, SoundEvents.BLOCK_IRON_DOOR_OPEN, SoundCategory.BLOCKS, 1.0F, this.world.random.nextFloat() * 0.1F + 0.9F, false);
				break;
			case 1006:
				this.world.playSound(blockPos, SoundEvents.BLOCK_WOODEN_DOOR_OPEN, SoundCategory.BLOCKS, 1.0F, this.world.random.nextFloat() * 0.1F + 0.9F, false);
				break;
			case 1007:
				this.world.playSound(blockPos, SoundEvents.BLOCK_WOODEN_TRAPDOOR_OPEN, SoundCategory.BLOCKS, 1.0F, this.world.random.nextFloat() * 0.1F + 0.9F, false);
				break;
			case 1008:
				this.world.playSound(blockPos, SoundEvents.BLOCK_FENCE_GATE_OPEN, SoundCategory.BLOCKS, 1.0F, this.world.random.nextFloat() * 0.1F + 0.9F, false);
				break;
			case 1009:
				this.world
					.playSound(blockPos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.5F, 2.6F + (random.nextFloat() - random.nextFloat()) * 0.8F, false);
				break;
			case 1010:
				if (Item.byRawId(j) instanceof MusicDiscItem) {
					this.playSong(((MusicDiscItem)Item.byRawId(j)).getSound(), blockPos);
				} else {
					this.playSong(null, blockPos);
				}
				break;
			case 1011:
				this.world.playSound(blockPos, SoundEvents.BLOCK_IRON_DOOR_CLOSE, SoundCategory.BLOCKS, 1.0F, this.world.random.nextFloat() * 0.1F + 0.9F, false);
				break;
			case 1012:
				this.world.playSound(blockPos, SoundEvents.BLOCK_WOODEN_DOOR_CLOSE, SoundCategory.BLOCKS, 1.0F, this.world.random.nextFloat() * 0.1F + 0.9F, false);
				break;
			case 1013:
				this.world.playSound(blockPos, SoundEvents.BLOCK_WOODEN_TRAPDOOR_CLOSE, SoundCategory.BLOCKS, 1.0F, this.world.random.nextFloat() * 0.1F + 0.9F, false);
				break;
			case 1014:
				this.world.playSound(blockPos, SoundEvents.BLOCK_FENCE_GATE_CLOSE, SoundCategory.BLOCKS, 1.0F, this.world.random.nextFloat() * 0.1F + 0.9F, false);
				break;
			case 1015:
				this.world.playSound(blockPos, SoundEvents.ENTITY_GHAST_WARN, SoundCategory.HOSTILE, 10.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
				break;
			case 1016:
				this.world
					.playSound(blockPos, SoundEvents.ENTITY_GHAST_SHOOT, SoundCategory.HOSTILE, 10.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
				break;
			case 1017:
				this.world
					.playSound(blockPos, SoundEvents.ENTITY_ENDER_DRAGON_SHOOT, SoundCategory.HOSTILE, 10.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
				break;
			case 1018:
				this.world.playSound(blockPos, SoundEvents.ENTITY_BLAZE_SHOOT, SoundCategory.HOSTILE, 2.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
				break;
			case 1019:
				this.world
					.playSound(
						blockPos, SoundEvents.ENTITY_ZOMBIE_ATTACK_WOODEN_DOOR, SoundCategory.HOSTILE, 2.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false
					);
				break;
			case 1020:
				this.world
					.playSound(
						blockPos, SoundEvents.ENTITY_ZOMBIE_ATTACK_IRON_DOOR, SoundCategory.HOSTILE, 2.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false
					);
				break;
			case 1021:
				this.world
					.playSound(
						blockPos, SoundEvents.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR, SoundCategory.HOSTILE, 2.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false
					);
				break;
			case 1022:
				this.world
					.playSound(blockPos, SoundEvents.ENTITY_WITHER_BREAK_BLOCK, SoundCategory.HOSTILE, 2.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
				break;
			case 1024:
				this.world
					.playSound(blockPos, SoundEvents.ENTITY_WITHER_SHOOT, SoundCategory.HOSTILE, 2.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
				break;
			case 1025:
				this.world
					.playSound(blockPos, SoundEvents.ENTITY_BAT_TAKEOFF, SoundCategory.NEUTRAL, 0.05F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
				break;
			case 1026:
				this.world
					.playSound(blockPos, SoundEvents.ENTITY_ZOMBIE_INFECT, SoundCategory.HOSTILE, 2.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
				break;
			case 1027:
				this.world
					.playSound(
						blockPos, SoundEvents.ENTITY_ZOMBIE_VILLAGER_CONVERTED, SoundCategory.NEUTRAL, 2.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false
					);
				break;
			case 1029:
				this.world.playSound(blockPos, SoundEvents.BLOCK_ANVIL_DESTROY, SoundCategory.BLOCKS, 1.0F, this.world.random.nextFloat() * 0.1F + 0.9F, false);
				break;
			case 1030:
				this.world.playSound(blockPos, SoundEvents.BLOCK_ANVIL_USE, SoundCategory.BLOCKS, 1.0F, this.world.random.nextFloat() * 0.1F + 0.9F, false);
				break;
			case 1031:
				this.world.playSound(blockPos, SoundEvents.BLOCK_ANVIL_LAND, SoundCategory.BLOCKS, 0.3F, this.world.random.nextFloat() * 0.1F + 0.9F, false);
				break;
			case 1032:
				this.client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.BLOCK_PORTAL_TRAVEL, random.nextFloat() * 0.4F + 0.8F));
				break;
			case 1033:
				this.world.playSound(blockPos, SoundEvents.BLOCK_CHORUS_FLOWER_GROW, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
				break;
			case 1034:
				this.world.playSound(blockPos, SoundEvents.BLOCK_CHORUS_FLOWER_DEATH, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
				break;
			case 1035:
				this.world.playSound(blockPos, SoundEvents.BLOCK_BREWING_STAND_BREW, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
				break;
			case 1036:
				this.world.playSound(blockPos, SoundEvents.BLOCK_IRON_TRAPDOOR_CLOSE, SoundCategory.BLOCKS, 1.0F, this.world.random.nextFloat() * 0.1F + 0.9F, false);
				break;
			case 1037:
				this.world.playSound(blockPos, SoundEvents.BLOCK_IRON_TRAPDOOR_OPEN, SoundCategory.BLOCKS, 1.0F, this.world.random.nextFloat() * 0.1F + 0.9F, false);
				break;
			case 1039:
				this.world.playSound(blockPos, SoundEvents.ENTITY_PHANTOM_BITE, SoundCategory.HOSTILE, 0.3F, this.world.random.nextFloat() * 0.1F + 0.9F, false);
				break;
			case 1040:
				this.world
					.playSound(
						blockPos, SoundEvents.ENTITY_ZOMBIE_CONVERTED_TO_DROWNED, SoundCategory.NEUTRAL, 2.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false
					);
				break;
			case 1041:
				this.world
					.playSound(
						blockPos, SoundEvents.ENTITY_HUSK_CONVERTED_TO_ZOMBIE, SoundCategory.NEUTRAL, 2.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false
					);
				break;
			case 1042:
				this.world.playSound(blockPos, SoundEvents.BLOCK_GRINDSTONE_USE, SoundCategory.BLOCKS, 1.0F, this.world.random.nextFloat() * 0.1F + 0.9F, false);
				break;
			case 1043:
				this.world.playSound(blockPos, SoundEvents.ITEM_BOOK_PAGE_TURN, SoundCategory.BLOCKS, 1.0F, this.world.random.nextFloat() * 0.1F + 0.9F, false);
				break;
			case 1500:
				ComposterBlock.playEffects(this.world, blockPos, j > 0);
				break;
			case 1501:
				this.world
					.playSound(
						blockPos,
						SoundEvents.BLOCK_LAVA_EXTINGUISH,
						SoundCategory.BLOCKS,
						0.5F,
						2.6F + (this.world.getRandom().nextFloat() - this.world.getRandom().nextFloat()) * 0.8F,
						false
					);

				for (int kx = 0; kx < 8; kx++) {
					this.world
						.addParticle(
							ParticleTypes.LARGE_SMOKE,
							(double)blockPos.getX() + Math.random(),
							(double)blockPos.getY() + 1.2,
							(double)blockPos.getZ() + Math.random(),
							0.0,
							0.0,
							0.0
						);
				}
				break;
			case 1502:
				this.world
					.playSound(
						blockPos,
						SoundEvents.BLOCK_REDSTONE_TORCH_BURNOUT,
						SoundCategory.BLOCKS,
						0.5F,
						2.6F + (this.world.random.nextFloat() - this.world.random.nextFloat()) * 0.8F,
						false
					);

				for (int kx = 0; kx < 5; kx++) {
					double u = (double)blockPos.getX() + random.nextDouble() * 0.6 + 0.2;
					double d = (double)blockPos.getY() + random.nextDouble() * 0.6 + 0.2;
					double e = (double)blockPos.getZ() + random.nextDouble() * 0.6 + 0.2;
					this.world.addParticle(ParticleTypes.SMOKE, u, d, e, 0.0, 0.0, 0.0);
				}
				break;
			case 1503:
				this.world.playSound(blockPos, SoundEvents.BLOCK_END_PORTAL_FRAME_FILL, SoundCategory.BLOCKS, 1.0F, 1.0F, false);

				for (int kx = 0; kx < 16; kx++) {
					double u = (double)((float)blockPos.getX() + (5.0F + random.nextFloat() * 6.0F) / 16.0F);
					double d = (double)((float)blockPos.getY() + 0.8125F);
					double e = (double)((float)blockPos.getZ() + (5.0F + random.nextFloat() * 6.0F) / 16.0F);
					double f = 0.0;
					double aa = 0.0;
					double ab = 0.0;
					this.world.addParticle(ParticleTypes.SMOKE, u, d, e, 0.0, 0.0, 0.0);
				}
				break;
			case 2000:
				Direction direction = Direction.byId(j);
				int kx = direction.getOffsetX();
				int l = direction.getOffsetY();
				int m = direction.getOffsetZ();
				double d = (double)blockPos.getX() + (double)kx * 0.6 + 0.5;
				double e = (double)blockPos.getY() + (double)l * 0.6 + 0.5;
				double f = (double)blockPos.getZ() + (double)m * 0.6 + 0.5;

				for (int nx = 0; nx < 10; nx++) {
					double g = random.nextDouble() * 0.2 + 0.01;
					double h = d + (double)kx * 0.01 + (random.nextDouble() - 0.5) * (double)m * 0.5;
					double o = e + (double)l * 0.01 + (random.nextDouble() - 0.5) * (double)l * 0.5;
					double p = f + (double)m * 0.01 + (random.nextDouble() - 0.5) * (double)kx * 0.5;
					double q = (double)kx * g + random.nextGaussian() * 0.01;
					double r = (double)l * g + random.nextGaussian() * 0.01;
					double s = (double)m * g + random.nextGaussian() * 0.01;
					this.addParticle(ParticleTypes.SMOKE, h, o, p, q, r, s);
				}
				break;
			case 2001:
				BlockState blockState = Block.getStateFromRawId(j);
				if (!blockState.isAir()) {
					BlockSoundGroup blockSoundGroup = blockState.getSoundGroup();
					this.world
						.playSound(
							blockPos, blockSoundGroup.getBreakSound(), SoundCategory.BLOCKS, (blockSoundGroup.getVolume() + 1.0F) / 2.0F, blockSoundGroup.getPitch() * 0.8F, false
						);
				}

				this.client.particleManager.addBlockBreakParticles(blockPos, blockState);
				break;
			case 2002:
			case 2007:
				double t = (double)blockPos.getX();
				double u = (double)blockPos.getY();
				double d = (double)blockPos.getZ();

				for (int v = 0; v < 8; v++) {
					this.addParticle(
						new ItemStackParticleEffect(ParticleTypes.ITEM, new ItemStack(Items.SPLASH_POTION)),
						t,
						u,
						d,
						random.nextGaussian() * 0.15,
						random.nextDouble() * 0.2,
						random.nextGaussian() * 0.15
					);
				}

				float w = (float)(j >> 16 & 0xFF) / 255.0F;
				float x = (float)(j >> 8 & 0xFF) / 255.0F;
				float y = (float)(j >> 0 & 0xFF) / 255.0F;
				ParticleEffect particleEffect = i == 2007 ? ParticleTypes.INSTANT_EFFECT : ParticleTypes.EFFECT;

				for (int n = 0; n < 100; n++) {
					double g = random.nextDouble() * 4.0;
					double h = random.nextDouble() * Math.PI * 2.0;
					double o = Math.cos(h) * g;
					double p = 0.01 + random.nextDouble() * 0.5;
					double q = Math.sin(h) * g;
					Particle particle = this.spawnParticle(particleEffect, particleEffect.getType().shouldAlwaysSpawn(), t + o * 0.1, u + 0.3, d + q * 0.1, o, p, q);
					if (particle != null) {
						float z = 0.75F + random.nextFloat() * 0.25F;
						particle.setColor(w * z, x * z, y * z);
						particle.move((float)g);
					}
				}

				this.world.playSound(blockPos, SoundEvents.ENTITY_SPLASH_POTION_BREAK, SoundCategory.NEUTRAL, 1.0F, this.world.random.nextFloat() * 0.1F + 0.9F, false);
				break;
			case 2003:
				double t = (double)blockPos.getX() + 0.5;
				double u = (double)blockPos.getY();
				double d = (double)blockPos.getZ() + 0.5;

				for (int v = 0; v < 8; v++) {
					this.addParticle(
						new ItemStackParticleEffect(ParticleTypes.ITEM, new ItemStack(Items.ENDER_EYE)),
						t,
						u,
						d,
						random.nextGaussian() * 0.15,
						random.nextDouble() * 0.2,
						random.nextGaussian() * 0.15
					);
				}

				for (double e = 0.0; e < Math.PI * 2; e += Math.PI / 20) {
					this.addParticle(ParticleTypes.PORTAL, t + Math.cos(e) * 5.0, u - 0.4, d + Math.sin(e) * 5.0, Math.cos(e) * -5.0, 0.0, Math.sin(e) * -5.0);
					this.addParticle(ParticleTypes.PORTAL, t + Math.cos(e) * 5.0, u - 0.4, d + Math.sin(e) * 5.0, Math.cos(e) * -7.0, 0.0, Math.sin(e) * -7.0);
				}
				break;
			case 2004:
				for (int kx = 0; kx < 20; kx++) {
					double u = (double)blockPos.getX() + 0.5 + ((double)this.world.random.nextFloat() - 0.5) * 2.0;
					double d = (double)blockPos.getY() + 0.5 + ((double)this.world.random.nextFloat() - 0.5) * 2.0;
					double e = (double)blockPos.getZ() + 0.5 + ((double)this.world.random.nextFloat() - 0.5) * 2.0;
					this.world.addParticle(ParticleTypes.SMOKE, u, d, e, 0.0, 0.0, 0.0);
					this.world.addParticle(ParticleTypes.FLAME, u, d, e, 0.0, 0.0, 0.0);
				}
				break;
			case 2005:
				BoneMealItem.createParticles(this.world, blockPos, j);
				break;
			case 2006:
				for (int k = 0; k < 200; k++) {
					float ac = random.nextFloat() * 4.0F;
					float ad = random.nextFloat() * (float) (Math.PI * 2);
					double d = (double)(MathHelper.cos(ad) * ac);
					double e = 0.01 + random.nextDouble() * 0.5;
					double f = (double)(MathHelper.sin(ad) * ac);
					Particle particle2 = this.spawnParticle(
						ParticleTypes.DRAGON_BREATH, false, (double)blockPos.getX() + d * 0.1, (double)blockPos.getY() + 0.3, (double)blockPos.getZ() + f * 0.1, d, e, f
					);
					if (particle2 != null) {
						particle2.move(ac);
					}
				}

				this.world.playSound(blockPos, SoundEvents.ENTITY_DRAGON_FIREBALL_EXPLODE, SoundCategory.HOSTILE, 1.0F, this.world.random.nextFloat() * 0.1F + 0.9F, false);
				break;
			case 2008:
				this.world.addParticle(ParticleTypes.EXPLOSION, (double)blockPos.getX() + 0.5, (double)blockPos.getY() + 0.5, (double)blockPos.getZ() + 0.5, 0.0, 0.0, 0.0);
				break;
			case 2009:
				for (int k = 0; k < 8; k++) {
					this.world
						.addParticle(
							ParticleTypes.CLOUD, (double)blockPos.getX() + Math.random(), (double)blockPos.getY() + 1.2, (double)blockPos.getZ() + Math.random(), 0.0, 0.0, 0.0
						);
				}
				break;
			case 3000:
				this.world
					.addParticle(
						ParticleTypes.EXPLOSION_EMITTER, true, (double)blockPos.getX() + 0.5, (double)blockPos.getY() + 0.5, (double)blockPos.getZ() + 0.5, 0.0, 0.0, 0.0
					);
				this.world
					.playSound(
						blockPos,
						SoundEvents.BLOCK_END_GATEWAY_SPAWN,
						SoundCategory.BLOCKS,
						10.0F,
						(1.0F + (this.world.random.nextFloat() - this.world.random.nextFloat()) * 0.2F) * 0.7F,
						false
					);
				break;
			case 3001:
				this.world.playSound(blockPos, SoundEvents.ENTITY_ENDER_DRAGON_GROWL, SoundCategory.HOSTILE, 64.0F, 0.8F + this.world.random.nextFloat() * 0.3F, false);
		}
	}

	public void setBlockBreakingProgress(int i, BlockPos blockPos, int j) {
		if (j >= 0 && j < 10) {
			PartiallyBrokenBlockEntry partiallyBrokenBlockEntry = this.partiallyBrokenBlocks.get(i);
			if (partiallyBrokenBlockEntry != null) {
				this.method_22987(partiallyBrokenBlockEntry);
			}

			if (partiallyBrokenBlockEntry == null
				|| partiallyBrokenBlockEntry.getPos().getX() != blockPos.getX()
				|| partiallyBrokenBlockEntry.getPos().getY() != blockPos.getY()
				|| partiallyBrokenBlockEntry.getPos().getZ() != blockPos.getZ()) {
				partiallyBrokenBlockEntry = new PartiallyBrokenBlockEntry(i, blockPos);
				this.partiallyBrokenBlocks.put(i, partiallyBrokenBlockEntry);
			}

			partiallyBrokenBlockEntry.setStage(j);
			partiallyBrokenBlockEntry.setLastUpdateTicks(this.ticks);
			this.field_20950.computeIfAbsent(partiallyBrokenBlockEntry.getPos().asLong(), l -> Sets.newTreeSet()).add(partiallyBrokenBlockEntry);
		} else {
			PartiallyBrokenBlockEntry partiallyBrokenBlockEntryx = this.partiallyBrokenBlocks.remove(i);
			if (partiallyBrokenBlockEntryx != null) {
				this.method_22987(partiallyBrokenBlockEntryx);
			}
		}
	}

	public boolean isTerrainRenderComplete() {
		return this.chunkRenderers.isEmpty() && this.chunkBatcher.isEmpty();
	}

	public void scheduleTerrainUpdate() {
		this.terrainUpdateNecessary = true;
		this.cloudsDirty = true;
	}

	public void updateBlockEntities(Collection<BlockEntity> collection, Collection<BlockEntity> collection2) {
		synchronized (this.blockEntities) {
			this.blockEntities.removeAll(collection);
			this.blockEntities.addAll(collection2);
		}
	}

	public GlFramebuffer method_22990() {
		return this.entityOutlinesFramebuffer;
	}

	@Environment(EnvType.CLIENT)
	class ChunkInfo {
		private final ChunkBatcher.ChunkRenderer renderer;
		private final Direction field_4125;
		private byte field_4126;
		private final int field_4122;

		private ChunkInfo(ChunkBatcher.ChunkRenderer chunkRenderer, @Nullable Direction direction, int i) {
			this.renderer = chunkRenderer;
			this.field_4125 = direction;
			this.field_4122 = i;
		}

		public void method_3299(byte b, Direction direction) {
			this.field_4126 = (byte)(this.field_4126 | b | 1 << direction.ordinal());
		}

		public boolean method_3298(Direction direction) {
			return (this.field_4126 & 1 << direction.ordinal()) > 0;
		}
	}
}
