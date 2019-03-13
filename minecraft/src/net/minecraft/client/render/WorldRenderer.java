package net.minecraft.client.render;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4184;
import net.minecraft.class_852;
import net.minecraft.block.AbstractSkullBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderLayer;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.ComposterBlock;
import net.minecraft.block.EnderChestBlock;
import net.minecraft.block.LeavesBlock;
import net.minecraft.block.SignBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.enums.ChestType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.audio.PositionedSoundInstance;
import net.minecraft.client.audio.SoundInstance;
import net.minecraft.client.gl.GlBuffer;
import net.minecraft.client.gl.GlFramebuffer;
import net.minecraft.client.gl.GlProgramManager;
import net.minecraft.client.gl.ShaderEffect;
import net.minecraft.client.options.CloudRenderMode;
import net.minecraft.client.options.ParticlesOption;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.chunk.ChunkBatcher;
import net.minecraft.client.render.chunk.ChunkRenderData;
import net.minecraft.client.render.chunk.ChunkRenderer;
import net.minecraft.client.render.chunk.ChunkRendererFactory;
import net.minecraft.client.render.chunk.ChunkRendererList;
import net.minecraft.client.render.chunk.DisplayListChunkRenderer;
import net.minecraft.client.render.chunk.DisplayListChunkRendererList;
import net.minecraft.client.render.chunk.VboChunkRendererList;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.GlAllocationUtils;
import net.minecraft.client.util.math.Vector4f;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BoneMealItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.RecordItem;
import net.minecraft.particle.ItemStackParticleParameters;
import net.minecraft.particle.ParticleParameters;
import net.minecraft.particle.ParticleType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SynchronousResourceReloadListener;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.dimension.DimensionType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class WorldRenderer implements AutoCloseable, SynchronousResourceReloadListener {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Identifier field_4098 = new Identifier("textures/environment/moon_phases.png");
	private static final Identifier field_4111 = new Identifier("textures/environment/sun.png");
	private static final Identifier field_4108 = new Identifier("textures/environment/clouds.png");
	private static final Identifier field_4061 = new Identifier("textures/environment/end_sky.png");
	private static final Identifier field_4071 = new Identifier("textures/misc/forcefield.png");
	public static final Direction[] field_4095 = Direction.values();
	private final MinecraftClient client;
	private final TextureManager field_4057;
	private final EntityRenderDispatcher field_4109;
	private ClientWorld world;
	private Set<ChunkRenderer> chunkRenderers = Sets.<ChunkRenderer>newLinkedHashSet();
	private List<WorldRenderer.class_762> field_4086 = Lists.<WorldRenderer.class_762>newArrayListWithCapacity(69696);
	private final Set<BlockEntity> field_4055 = Sets.<BlockEntity>newHashSet();
	private ChunkRenderDispatcher field_4112;
	private int starsDisplayList = -1;
	private int field_4117 = -1;
	private int field_4067 = -1;
	private final VertexFormat field_4100;
	private GlBuffer starsBuffer;
	private GlBuffer field_4087;
	private GlBuffer field_4102;
	private final int field_4079 = 28;
	private boolean cloudsDirty = true;
	private int cloudsDisplayList = -1;
	private GlBuffer cloudsBuffer;
	private int ticks;
	private final Map<Integer, PartiallyBrokenBlockEntry> partiallyBrokenBlocks = Maps.<Integer, PartiallyBrokenBlockEntry>newHashMap();
	private final Map<BlockPos, SoundInstance> field_4119 = Maps.<BlockPos, SoundInstance>newHashMap();
	private final Sprite[] field_4068 = new Sprite[10];
	private GlFramebuffer entityOutlinesFramebuffer;
	private ShaderEffect field_4059;
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
	private ChunkBatcher field_4106;
	private ChunkRendererList chunkRendererList;
	private int renderDistance = -1;
	private int field_4076 = 2;
	private int renderedEntities;
	private int field_4110;
	private boolean field_4066;
	private Frustum field_4056;
	private final Vector4f[] field_4065 = new Vector4f[8];
	private final Vec3d forcedFrustumPosition = new Vec3d();
	private boolean vertexBufferObjectsEnabled;
	private ChunkRendererFactory field_4078;
	private double lastTranslucentSortX;
	private double lastTranslucentSortY;
	private double lastTranslucentSortZ;
	private boolean terrainUpdateNecessary = true;
	private boolean entityOutlinesUpdateNecessary;

	public WorldRenderer(MinecraftClient minecraftClient) {
		this.client = minecraftClient;
		this.field_4109 = minecraftClient.method_1561();
		this.field_4057 = minecraftClient.method_1531();
		this.vertexBufferObjectsEnabled = GLX.useVbo();
		if (this.vertexBufferObjectsEnabled) {
			this.chunkRendererList = new VboChunkRendererList();
			this.field_4078 = ChunkRenderer::new;
		} else {
			this.chunkRendererList = new DisplayListChunkRendererList();
			this.field_4078 = DisplayListChunkRenderer::new;
		}

		this.field_4100 = new VertexFormat();
		this.field_4100.method_1361(new VertexFormatElement(0, VertexFormatElement.Format.FLOAT, VertexFormatElement.Type.POSITION, 3));
		this.setupStarRendering();
		this.method_3277();
		this.method_3265();
	}

	public void close() {
		if (this.field_4059 != null) {
			this.field_4059.close();
		}
	}

	@Override
	public void apply(ResourceManager resourceManager) {
		this.field_4057.method_4618(field_4071);
		GlStateManager.texParameter(3553, 10242, 10497);
		GlStateManager.texParameter(3553, 10243, 10497);
		GlStateManager.bindTexture(0);
		this.loadDestroyStageTextures();
		this.loadEntityOutlineShader();
	}

	private void loadDestroyStageTextures() {
		SpriteAtlasTexture spriteAtlasTexture = this.client.method_1549();
		this.field_4068[0] = spriteAtlasTexture.method_4608(ModelLoader.field_5377);
		this.field_4068[1] = spriteAtlasTexture.method_4608(ModelLoader.field_5385);
		this.field_4068[2] = spriteAtlasTexture.method_4608(ModelLoader.field_5375);
		this.field_4068[3] = spriteAtlasTexture.method_4608(ModelLoader.field_5403);
		this.field_4068[4] = spriteAtlasTexture.method_4608(ModelLoader.field_5393);
		this.field_4068[5] = spriteAtlasTexture.method_4608(ModelLoader.field_5386);
		this.field_4068[6] = spriteAtlasTexture.method_4608(ModelLoader.field_5369);
		this.field_4068[7] = spriteAtlasTexture.method_4608(ModelLoader.field_5401);
		this.field_4068[8] = spriteAtlasTexture.method_4608(ModelLoader.field_5392);
		this.field_4068[9] = spriteAtlasTexture.method_4608(ModelLoader.field_5382);
	}

	public void loadEntityOutlineShader() {
		if (GLX.usePostProcess) {
			if (GlProgramManager.getInstance() == null) {
				GlProgramManager.init();
			}

			Identifier identifier = new Identifier("shaders/post/entity_outline.json");

			try {
				this.field_4059 = new ShaderEffect(this.client.method_1531(), this.client.method_1478(), this.client.getFramebuffer(), identifier);
				this.field_4059.setupDimensions(this.client.window.getFramebufferWidth(), this.client.window.getFramebufferHeight());
				this.entityOutlinesFramebuffer = this.field_4059.getSecondaryTarget("final");
			} catch (IOException var3) {
				LOGGER.warn("Failed to load shader: {}", identifier, var3);
				this.field_4059 = null;
				this.entityOutlinesFramebuffer = null;
			} catch (JsonSyntaxException var4) {
				LOGGER.warn("Failed to load shader: {}", identifier, var4);
				this.field_4059 = null;
				this.entityOutlinesFramebuffer = null;
			}
		} else {
			this.field_4059 = null;
			this.entityOutlinesFramebuffer = null;
		}
	}

	public void drawEntityOutlinesFramebuffer() {
		if (this.canDrawEntityOutlines()) {
			GlStateManager.enableBlend();
			GlStateManager.blendFuncSeparate(
				GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE
			);
			this.entityOutlinesFramebuffer.draw(this.client.window.getFramebufferWidth(), this.client.window.getFramebufferHeight(), false);
			GlStateManager.disableBlend();
		}
	}

	protected boolean canDrawEntityOutlines() {
		return this.entityOutlinesFramebuffer != null && this.field_4059 != null && this.client.field_1724 != null;
	}

	private void method_3265() {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBufferBuilder();
		if (this.field_4102 != null) {
			this.field_4102.delete();
		}

		if (this.field_4067 >= 0) {
			GlAllocationUtils.deleteSingletonList(this.field_4067);
			this.field_4067 = -1;
		}

		if (this.vertexBufferObjectsEnabled) {
			this.field_4102 = new GlBuffer(this.field_4100);
			this.method_3283(bufferBuilder, -16.0F, true);
			bufferBuilder.end();
			bufferBuilder.clear();
			this.field_4102.set(bufferBuilder.getByteBuffer());
		} else {
			this.field_4067 = GlAllocationUtils.genLists(1);
			GlStateManager.newList(this.field_4067, 4864);
			this.method_3283(bufferBuilder, -16.0F, true);
			tessellator.draw();
			GlStateManager.endList();
		}
	}

	private void method_3277() {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBufferBuilder();
		if (this.field_4087 != null) {
			this.field_4087.delete();
		}

		if (this.field_4117 >= 0) {
			GlAllocationUtils.deleteSingletonList(this.field_4117);
			this.field_4117 = -1;
		}

		if (this.vertexBufferObjectsEnabled) {
			this.field_4087 = new GlBuffer(this.field_4100);
			this.method_3283(bufferBuilder, 16.0F, false);
			bufferBuilder.end();
			bufferBuilder.clear();
			this.field_4087.set(bufferBuilder.getByteBuffer());
		} else {
			this.field_4117 = GlAllocationUtils.genLists(1);
			GlStateManager.newList(this.field_4117, 4864);
			this.method_3283(bufferBuilder, 16.0F, false);
			tessellator.draw();
			GlStateManager.endList();
		}
	}

	private void method_3283(BufferBuilder bufferBuilder, float f, boolean bl) {
		int i = 64;
		int j = 6;
		bufferBuilder.method_1328(7, VertexFormats.field_1592);

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

	private void setupStarRendering() {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBufferBuilder();
		if (this.starsBuffer != null) {
			this.starsBuffer.delete();
		}

		if (this.starsDisplayList >= 0) {
			GlAllocationUtils.deleteSingletonList(this.starsDisplayList);
			this.starsDisplayList = -1;
		}

		if (this.vertexBufferObjectsEnabled) {
			this.starsBuffer = new GlBuffer(this.field_4100);
			this.renderStars(bufferBuilder);
			bufferBuilder.end();
			bufferBuilder.clear();
			this.starsBuffer.set(bufferBuilder.getByteBuffer());
		} else {
			this.starsDisplayList = GlAllocationUtils.genLists(1);
			GlStateManager.pushMatrix();
			GlStateManager.newList(this.starsDisplayList, 4864);
			this.renderStars(bufferBuilder);
			tessellator.draw();
			GlStateManager.endList();
			GlStateManager.popMatrix();
		}
	}

	private void renderStars(BufferBuilder bufferBuilder) {
		Random random = new Random(10842L);
		bufferBuilder.method_1328(7, VertexFormats.field_1592);

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
		this.field_4109.method_3944(clientWorld);
		this.world = clientWorld;
		if (clientWorld != null) {
			this.reload();
		} else {
			this.chunkRenderers.clear();
			this.field_4086.clear();
			if (this.field_4112 != null) {
				this.field_4112.delete();
				this.field_4112 = null;
			}

			if (this.field_4106 != null) {
				this.field_4106.method_3619();
			}

			this.field_4106 = null;
			this.field_4055.clear();
		}
	}

	public void reload() {
		if (this.world != null) {
			if (this.field_4106 == null) {
				this.field_4106 = new ChunkBatcher();
			}

			this.terrainUpdateNecessary = true;
			this.cloudsDirty = true;
			LeavesBlock.setRenderingMode(this.client.field_1690.fancyGraphics);
			this.renderDistance = this.client.field_1690.viewDistance;
			boolean bl = this.vertexBufferObjectsEnabled;
			this.vertexBufferObjectsEnabled = GLX.useVbo();
			if (bl && !this.vertexBufferObjectsEnabled) {
				this.chunkRendererList = new DisplayListChunkRendererList();
				this.field_4078 = DisplayListChunkRenderer::new;
			} else if (!bl && this.vertexBufferObjectsEnabled) {
				this.chunkRendererList = new VboChunkRendererList();
				this.field_4078 = ChunkRenderer::new;
			}

			if (bl != this.vertexBufferObjectsEnabled) {
				this.setupStarRendering();
				this.method_3277();
				this.method_3265();
			}

			if (this.field_4112 != null) {
				this.field_4112.delete();
			}

			this.method_3280();
			synchronized (this.field_4055) {
				this.field_4055.clear();
			}

			this.field_4112 = new ChunkRenderDispatcher(this.world, this.client.field_1690.viewDistance, this, this.field_4078);
			if (this.world != null) {
				Entity entity = this.client.getCameraEntity();
				if (entity != null) {
					this.field_4112.updateCameraPosition(entity.x, entity.z);
				}
			}

			this.field_4076 = 2;
		}
	}

	protected void method_3280() {
		this.chunkRenderers.clear();
		this.field_4106.method_3632();
	}

	public void onResized(int i, int j) {
		this.method_3292();
		if (GLX.usePostProcess) {
			if (this.field_4059 != null) {
				this.field_4059.setupDimensions(i, j);
			}
		}
	}

	public void method_3271(class_4184 arg, VisibleRegion visibleRegion, float f) {
		if (this.field_4076 > 0) {
			this.field_4076--;
		} else {
			double d = arg.method_19326().x;
			double e = arg.method_19326().y;
			double g = arg.method_19326().z;
			this.world.getProfiler().push("prepare");
			BlockEntityRenderDispatcher.INSTANCE.method_3549(this.world, this.client.method_1531(), this.client.field_1772, arg, this.client.hitResult);
			this.field_4109.method_3941(this.world, this.client.field_1772, arg, this.client.targetedEntity, this.client.field_1690);
			this.renderedEntities = 0;
			this.field_4110 = 0;
			double h = arg.method_19326().x;
			double i = arg.method_19326().y;
			double j = arg.method_19326().z;
			BlockEntityRenderDispatcher.renderOffsetX = h;
			BlockEntityRenderDispatcher.renderOffsetY = i;
			BlockEntityRenderDispatcher.renderOffsetZ = j;
			this.field_4109.setRenderPosition(h, i, j);
			this.client.field_1773.enableLightmap();
			this.world.getProfiler().swap("entities");
			List<Entity> list = Lists.<Entity>newArrayList();
			List<Entity> list2 = Lists.<Entity>newArrayList();

			for (Entity entity : this.world.getEntities()) {
				if ((this.field_4109.method_3950(entity, visibleRegion, d, e, g) || entity.method_5821(this.client.field_1724))
					&& (entity != arg.method_19331() || arg.method_19333() || arg.method_19331() instanceof LivingEntity && ((LivingEntity)arg.method_19331()).isSleeping())) {
					this.renderedEntities++;
					this.field_4109.render(entity, f, false);
					if (entity.isGlowing()
						|| entity instanceof PlayerEntity && this.client.field_1724.isSpectator() && this.client.field_1690.keySpectatorOutlines.isPressed()) {
						list.add(entity);
					}

					if (this.field_4109.hasSecondPass(entity)) {
						list2.add(entity);
					}
				}
			}

			if (!list2.isEmpty()) {
				for (Entity entityx : list2) {
					this.field_4109.renderSecondPass(entityx, f);
				}
			}

			if (this.canDrawEntityOutlines() && (!list.isEmpty() || this.entityOutlinesUpdateNecessary)) {
				this.world.getProfiler().swap("entityOutlines");
				this.entityOutlinesFramebuffer.clear(MinecraftClient.IS_SYSTEM_MAC);
				this.entityOutlinesUpdateNecessary = !list.isEmpty();
				if (!list.isEmpty()) {
					GlStateManager.depthFunc(519);
					GlStateManager.disableFog();
					this.entityOutlinesFramebuffer.beginWrite(false);
					GuiLighting.disable();
					this.field_4109.setRenderOutlines(true);

					for (int k = 0; k < list.size(); k++) {
						this.field_4109.render((Entity)list.get(k), f, false);
					}

					this.field_4109.setRenderOutlines(false);
					GuiLighting.enable();
					GlStateManager.depthMask(false);
					this.field_4059.render(f);
					GlStateManager.enableLighting();
					GlStateManager.depthMask(true);
					GlStateManager.enableFog();
					GlStateManager.enableBlend();
					GlStateManager.enableColorMaterial();
					GlStateManager.depthFunc(515);
					GlStateManager.enableDepthTest();
					GlStateManager.enableAlphaTest();
				}

				this.client.getFramebuffer().beginWrite(false);
			}

			this.world.getProfiler().swap("blockentities");
			GuiLighting.enable();

			for (WorldRenderer.class_762 lv : this.field_4086) {
				List<BlockEntity> list3 = lv.field_4124.getChunkRenderData().getBlockEntities();
				if (!list3.isEmpty()) {
					for (BlockEntity blockEntity : list3) {
						BlockEntityRenderDispatcher.INSTANCE.render(blockEntity, f, -1);
					}
				}
			}

			synchronized (this.field_4055) {
				for (BlockEntity blockEntity2 : this.field_4055) {
					BlockEntityRenderDispatcher.INSTANCE.render(blockEntity2, f, -1);
				}
			}

			this.method_3263();

			for (PartiallyBrokenBlockEntry partiallyBrokenBlockEntry : this.partiallyBrokenBlocks.values()) {
				BlockPos blockPos = partiallyBrokenBlockEntry.getPos();
				BlockState blockState = this.world.method_8320(blockPos);
				if (blockState.getBlock().hasBlockEntity()) {
					BlockEntity blockEntity = this.world.method_8321(blockPos);
					if (blockEntity instanceof ChestBlockEntity && blockState.method_11654(ChestBlock.field_10770) == ChestType.field_12574) {
						blockPos = blockPos.method_10093(((Direction)blockState.method_11654(ChestBlock.field_10768)).rotateYClockwise());
						blockEntity = this.world.method_8321(blockPos);
					}

					if (blockEntity != null && blockState.hasBlockEntityBreakingRender()) {
						BlockEntityRenderDispatcher.INSTANCE.render(blockEntity, f, partiallyBrokenBlockEntry.getStage());
					}
				}
			}

			this.method_3274();
			this.client.field_1773.disableLightmap();
			this.client.getProfiler().pop();
		}
	}

	public String getChunksDebugString() {
		int i = this.field_4112.field_4150.length;
		int j = this.getChunkNumber();
		return String.format(
			"C: %d/%d %sD: %d, %s", j, i, this.client.field_1730 ? "(s) " : "", this.renderDistance, this.field_4106 == null ? "null" : this.field_4106.getDebugString()
		);
	}

	protected int getChunkNumber() {
		int i = 0;

		for (WorldRenderer.class_762 lv : this.field_4086) {
			ChunkRenderData chunkRenderData = lv.field_4124.chunkRenderData;
			if (chunkRenderData != ChunkRenderData.EMPTY && !chunkRenderData.isEmpty()) {
				i++;
			}
		}

		return i;
	}

	public String getEntitiesDebugString() {
		return "E: " + this.renderedEntities + "/" + this.world.method_18120() + ", B: " + this.field_4110;
	}

	public void method_3273(class_4184 arg, VisibleRegion visibleRegion, int i, boolean bl) {
		if (this.client.field_1690.viewDistance != this.renderDistance) {
			this.reload();
		}

		this.world.getProfiler().push("camera");
		double d = this.client.field_1724.x - this.lastCameraChunkUpdateX;
		double e = this.client.field_1724.y - this.lastCameraChunkUpdateY;
		double f = this.client.field_1724.z - this.lastCameraChunkUpdateZ;
		if (this.cameraChunkX != this.client.field_1724.chunkX
			|| this.cameraChunkY != this.client.field_1724.chunkY
			|| this.cameraChunkZ != this.client.field_1724.chunkZ
			|| d * d + e * e + f * f > 16.0) {
			this.lastCameraChunkUpdateX = this.client.field_1724.x;
			this.lastCameraChunkUpdateY = this.client.field_1724.y;
			this.lastCameraChunkUpdateZ = this.client.field_1724.z;
			this.cameraChunkX = this.client.field_1724.chunkX;
			this.cameraChunkY = this.client.field_1724.chunkY;
			this.cameraChunkZ = this.client.field_1724.chunkZ;
			this.field_4112.updateCameraPosition(this.client.field_1724.x, this.client.field_1724.z);
		}

		this.world.getProfiler().swap("renderlistcamera");
		this.chunkRendererList.setCameraPosition(arg.method_19326().x, arg.method_19326().y, arg.method_19326().z);
		this.field_4106.method_19419(arg.method_19326());
		this.world.getProfiler().swap("cull");
		if (this.field_4056 != null) {
			FrustumWithOrigin frustumWithOrigin = new FrustumWithOrigin(this.field_4056);
			frustumWithOrigin.setOrigin(this.forcedFrustumPosition.x, this.forcedFrustumPosition.y, this.forcedFrustumPosition.z);
			visibleRegion = frustumWithOrigin;
		}

		this.client.getProfiler().swap("culling");
		BlockPos blockPos = arg.method_19328();
		ChunkRenderer chunkRenderer = this.field_4112.method_3323(blockPos);
		BlockPos blockPos2 = new BlockPos(
			MathHelper.floor(arg.method_19326().x / 16.0) * 16, MathHelper.floor(arg.method_19326().y / 16.0) * 16, MathHelper.floor(arg.method_19326().z / 16.0) * 16
		);
		float g = arg.method_19329();
		float h = arg.method_19330();
		this.terrainUpdateNecessary = this.terrainUpdateNecessary
			|| !this.chunkRenderers.isEmpty()
			|| arg.method_19326().x != this.lastCameraX
			|| arg.method_19326().y != this.lastCameraY
			|| arg.method_19326().z != this.lastCameraZ
			|| (double)g != this.lastCameraPitch
			|| (double)h != this.lastCameraYaw;
		this.lastCameraX = arg.method_19326().x;
		this.lastCameraY = arg.method_19326().y;
		this.lastCameraZ = arg.method_19326().z;
		this.lastCameraPitch = (double)g;
		this.lastCameraYaw = (double)h;
		boolean bl2 = this.field_4056 != null;
		this.client.getProfiler().swap("update");
		if (!bl2 && this.terrainUpdateNecessary) {
			this.terrainUpdateNecessary = false;
			this.field_4086 = Lists.<WorldRenderer.class_762>newArrayList();
			Queue<WorldRenderer.class_762> queue = Queues.<WorldRenderer.class_762>newArrayDeque();
			Entity.setRenderDistanceMultiplier(MathHelper.clamp((double)this.client.field_1690.viewDistance / 8.0, 1.0, 2.5));
			boolean bl3 = this.client.field_1730;
			if (chunkRenderer != null) {
				boolean bl4 = false;
				WorldRenderer.class_762 lv = new WorldRenderer.class_762(chunkRenderer, null, 0);
				Set<Direction> set = this.method_3285(blockPos);
				if (set.size() == 1) {
					net.minecraft.util.math.Vec3d vec3d = arg.method_19335();
					Direction direction = Direction.getFacing(vec3d.x, vec3d.y, vec3d.z).getOpposite();
					set.remove(direction);
				}

				if (set.isEmpty()) {
					bl4 = true;
				}

				if (bl4 && !bl) {
					this.field_4086.add(lv);
				} else {
					if (bl && this.world.method_8320(blockPos).method_11598(this.world, blockPos)) {
						bl3 = false;
					}

					chunkRenderer.method_3671(i);
					queue.add(lv);
				}
			} else {
				int j = blockPos.getY() > 0 ? 248 : 8;

				for (int k = -this.renderDistance; k <= this.renderDistance; k++) {
					for (int l = -this.renderDistance; l <= this.renderDistance; l++) {
						ChunkRenderer chunkRenderer2 = this.field_4112.method_3323(new BlockPos((k << 4) + 8, j, (l << 4) + 8));
						if (chunkRenderer2 != null && visibleRegion.intersects(chunkRenderer2.boundingBox)) {
							chunkRenderer2.method_3671(i);
							queue.add(new WorldRenderer.class_762(chunkRenderer2, null, 0));
						}
					}
				}
			}

			this.client.getProfiler().push("iteration");

			while (!queue.isEmpty()) {
				WorldRenderer.class_762 lv2 = (WorldRenderer.class_762)queue.poll();
				ChunkRenderer chunkRenderer3 = lv2.field_4124;
				Direction direction2 = lv2.field_4125;
				this.field_4086.add(lv2);

				for (Direction direction3 : field_4095) {
					ChunkRenderer chunkRenderer4 = this.method_3241(blockPos2, chunkRenderer3, direction3);
					if ((!bl3 || !lv2.method_3298(direction3.getOpposite()))
						&& (!bl3 || direction2 == null || chunkRenderer3.getChunkRenderData().method_3650(direction2.getOpposite(), direction3))
						&& chunkRenderer4 != null
						&& chunkRenderer4.method_3673()
						&& chunkRenderer4.method_3671(i)
						&& visibleRegion.intersects(chunkRenderer4.boundingBox)) {
						WorldRenderer.class_762 lv3 = new WorldRenderer.class_762(chunkRenderer4, direction3, lv2.field_4122 + 1);
						lv3.method_3299(lv2.field_4126, direction3);
						queue.add(lv3);
					}
				}
			}

			this.client.getProfiler().pop();
		}

		this.client.getProfiler().swap("captureFrustum");
		if (this.field_4066) {
			this.method_3275(arg.method_19326().x, arg.method_19326().y, arg.method_19326().z);
			this.field_4066 = false;
		}

		this.client.getProfiler().swap("rebuildNear");
		Set<ChunkRenderer> set2 = this.chunkRenderers;
		this.chunkRenderers = Sets.<ChunkRenderer>newLinkedHashSet();

		for (WorldRenderer.class_762 lv2 : this.field_4086) {
			ChunkRenderer chunkRenderer3 = lv2.field_4124;
			if (chunkRenderer3.method_3672() || set2.contains(chunkRenderer3)) {
				this.terrainUpdateNecessary = true;
				BlockPos blockPos3 = chunkRenderer3.method_3670().add(8, 8, 8);
				boolean bl5 = blockPos3.squaredDistanceTo(blockPos) < 768.0;
				if (!chunkRenderer3.method_3661() && !bl5) {
					this.chunkRenderers.add(chunkRenderer3);
				} else {
					this.client.getProfiler().push("build near");
					this.field_4106.method_3627(chunkRenderer3);
					chunkRenderer3.method_3662();
					this.client.getProfiler().pop();
				}
			}
		}

		this.chunkRenderers.addAll(set2);
		this.client.getProfiler().pop();
	}

	private Set<Direction> method_3285(BlockPos blockPos) {
		class_852 lv = new class_852();
		BlockPos blockPos2 = new BlockPos(blockPos.getX() >> 4 << 4, blockPos.getY() >> 4 << 4, blockPos.getZ() >> 4 << 4);
		WorldChunk worldChunk = this.world.method_8500(blockPos2);

		for (BlockPos blockPos3 : BlockPos.iterateBoxPositions(blockPos2, blockPos2.add(15, 15, 15))) {
			if (worldChunk.method_8320(blockPos3).method_11598(this.world, blockPos3)) {
				lv.method_3682(blockPos3);
			}
		}

		return lv.method_3686(blockPos);
	}

	@Nullable
	private ChunkRenderer method_3241(BlockPos blockPos, ChunkRenderer chunkRenderer, Direction direction) {
		BlockPos blockPos2 = chunkRenderer.method_3676(direction);
		if (MathHelper.abs(blockPos.getX() - blockPos2.getX()) > this.renderDistance * 16) {
			return null;
		} else if (blockPos2.getY() < 0 || blockPos2.getY() >= 256) {
			return null;
		} else {
			return MathHelper.abs(blockPos.getZ() - blockPos2.getZ()) > this.renderDistance * 16 ? null : this.field_4112.method_3323(blockPos2);
		}
	}

	private void method_3275(double d, double e, double f) {
	}

	public int renderLayer(BlockRenderLayer blockRenderLayer, class_4184 arg) {
		GuiLighting.disable();
		if (blockRenderLayer == BlockRenderLayer.TRANSLUCENT) {
			this.client.getProfiler().push("translucent_sort");
			double d = arg.method_19326().x - this.lastTranslucentSortX;
			double e = arg.method_19326().y - this.lastTranslucentSortY;
			double f = arg.method_19326().z - this.lastTranslucentSortZ;
			if (d * d + e * e + f * f > 1.0) {
				this.lastTranslucentSortX = arg.method_19326().x;
				this.lastTranslucentSortY = arg.method_19326().y;
				this.lastTranslucentSortZ = arg.method_19326().z;
				int i = 0;

				for (WorldRenderer.class_762 lv : this.field_4086) {
					if (lv.field_4124.chunkRenderData.isBufferInitialized(blockRenderLayer) && i++ < 15) {
						this.field_4106.method_3620(lv.field_4124);
					}
				}
			}

			this.client.getProfiler().pop();
		}

		this.client.getProfiler().push("filterempty");
		int j = 0;
		boolean bl = blockRenderLayer == BlockRenderLayer.TRANSLUCENT;
		int k = bl ? this.field_4086.size() - 1 : 0;
		int l = bl ? -1 : this.field_4086.size();
		int m = bl ? -1 : 1;

		for (int n = k; n != l; n += m) {
			ChunkRenderer chunkRenderer = ((WorldRenderer.class_762)this.field_4086.get(n)).field_4124;
			if (!chunkRenderer.getChunkRenderData().method_3641(blockRenderLayer)) {
				j++;
				this.chunkRendererList.method_3159(chunkRenderer, blockRenderLayer);
			}
		}

		this.client.getProfiler().swap((Supplier<String>)(() -> "render_" + blockRenderLayer));
		this.renderLayer(blockRenderLayer);
		this.client.getProfiler().pop();
		return j;
	}

	private void renderLayer(BlockRenderLayer blockRenderLayer) {
		this.client.field_1773.enableLightmap();
		if (GLX.useVbo()) {
			GlStateManager.enableClientState(32884);
			GLX.glClientActiveTexture(GLX.GL_TEXTURE0);
			GlStateManager.enableClientState(32888);
			GLX.glClientActiveTexture(GLX.GL_TEXTURE1);
			GlStateManager.enableClientState(32888);
			GLX.glClientActiveTexture(GLX.GL_TEXTURE0);
			GlStateManager.enableClientState(32886);
		}

		this.chunkRendererList.render(blockRenderLayer);
		if (GLX.useVbo()) {
			for (VertexFormatElement vertexFormatElement : VertexFormats.field_1582.getElements()) {
				VertexFormatElement.Type type = vertexFormatElement.getType();
				int i = vertexFormatElement.getIndex();
				switch (type) {
					case POSITION:
						GlStateManager.disableClientState(32884);
						break;
					case UV:
						GLX.glClientActiveTexture(GLX.GL_TEXTURE0 + i);
						GlStateManager.disableClientState(32888);
						GLX.glClientActiveTexture(GLX.GL_TEXTURE0);
						break;
					case COLOR:
						GlStateManager.disableClientState(32886);
						GlStateManager.clearCurrentColor();
				}
			}
		}

		this.client.field_1773.disableLightmap();
	}

	private void method_3290(Iterator<PartiallyBrokenBlockEntry> iterator) {
		while (iterator.hasNext()) {
			PartiallyBrokenBlockEntry partiallyBrokenBlockEntry = (PartiallyBrokenBlockEntry)iterator.next();
			int i = partiallyBrokenBlockEntry.getLastUpdateTicks();
			if (this.ticks - i > 400) {
				iterator.remove();
			}
		}
	}

	public void tick() {
		this.ticks++;
		if (this.ticks % 20 == 0) {
			this.method_3290(this.partiallyBrokenBlocks.values().iterator());
		}
	}

	private void renderEndSky() {
		GlStateManager.disableFog();
		GlStateManager.disableAlphaTest();
		GlStateManager.enableBlend();
		GlStateManager.blendFuncSeparate(
			GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO
		);
		GuiLighting.disable();
		GlStateManager.depthMask(false);
		this.field_4057.method_4618(field_4061);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBufferBuilder();

		for (int i = 0; i < 6; i++) {
			GlStateManager.pushMatrix();
			if (i == 1) {
				GlStateManager.rotatef(90.0F, 1.0F, 0.0F, 0.0F);
			}

			if (i == 2) {
				GlStateManager.rotatef(-90.0F, 1.0F, 0.0F, 0.0F);
			}

			if (i == 3) {
				GlStateManager.rotatef(180.0F, 1.0F, 0.0F, 0.0F);
			}

			if (i == 4) {
				GlStateManager.rotatef(90.0F, 0.0F, 0.0F, 1.0F);
			}

			if (i == 5) {
				GlStateManager.rotatef(-90.0F, 0.0F, 0.0F, 1.0F);
			}

			bufferBuilder.method_1328(7, VertexFormats.field_1575);
			bufferBuilder.vertex(-100.0, -100.0, -100.0).texture(0.0, 0.0).color(40, 40, 40, 255).next();
			bufferBuilder.vertex(-100.0, -100.0, 100.0).texture(0.0, 16.0).color(40, 40, 40, 255).next();
			bufferBuilder.vertex(100.0, -100.0, 100.0).texture(16.0, 16.0).color(40, 40, 40, 255).next();
			bufferBuilder.vertex(100.0, -100.0, -100.0).texture(16.0, 0.0).color(40, 40, 40, 255).next();
			tessellator.draw();
			GlStateManager.popMatrix();
		}

		GlStateManager.depthMask(true);
		GlStateManager.enableTexture();
		GlStateManager.enableAlphaTest();
	}

	public void renderSky(float f) {
		if (this.client.field_1687.field_9247.method_12460() == DimensionType.field_13078) {
			this.renderEndSky();
		} else if (this.client.field_1687.field_9247.hasVisibleSky()) {
			GlStateManager.disableTexture();
			net.minecraft.util.math.Vec3d vec3d = this.world.method_8548(this.client.field_1773.method_19418().method_19328(), f);
			float g = (float)vec3d.x;
			float h = (float)vec3d.y;
			float i = (float)vec3d.z;
			GlStateManager.color3f(g, h, i);
			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder bufferBuilder = tessellator.getBufferBuilder();
			GlStateManager.depthMask(false);
			GlStateManager.enableFog();
			GlStateManager.color3f(g, h, i);
			if (this.vertexBufferObjectsEnabled) {
				this.field_4087.bind();
				GlStateManager.enableClientState(32884);
				GlStateManager.vertexPointer(3, 5126, 12, 0);
				this.field_4087.draw(7);
				GlBuffer.unbind();
				GlStateManager.disableClientState(32884);
			} else {
				GlStateManager.callList(this.field_4117);
			}

			GlStateManager.disableFog();
			GlStateManager.disableAlphaTest();
			GlStateManager.enableBlend();
			GlStateManager.blendFuncSeparate(
				GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO
			);
			GuiLighting.disable();
			float[] fs = this.world.field_9247.getBackgroundColor(this.world.getSkyAngle(f), f);
			if (fs != null) {
				GlStateManager.disableTexture();
				GlStateManager.shadeModel(7425);
				GlStateManager.pushMatrix();
				GlStateManager.rotatef(90.0F, 1.0F, 0.0F, 0.0F);
				GlStateManager.rotatef(MathHelper.sin(this.world.method_8442(f)) < 0.0F ? 180.0F : 0.0F, 0.0F, 0.0F, 1.0F);
				GlStateManager.rotatef(90.0F, 0.0F, 0.0F, 1.0F);
				float j = fs[0];
				float k = fs[1];
				float l = fs[2];
				bufferBuilder.method_1328(6, VertexFormats.field_1576);
				bufferBuilder.vertex(0.0, 100.0, 0.0).color(j, k, l, fs[3]).next();
				int m = 16;

				for (int n = 0; n <= 16; n++) {
					float o = (float)n * (float) (Math.PI * 2) / 16.0F;
					float p = MathHelper.sin(o);
					float q = MathHelper.cos(o);
					bufferBuilder.vertex((double)(p * 120.0F), (double)(q * 120.0F), (double)(-q * 40.0F * fs[3])).color(fs[0], fs[1], fs[2], 0.0F).next();
				}

				tessellator.draw();
				GlStateManager.popMatrix();
				GlStateManager.shadeModel(7424);
			}

			GlStateManager.enableTexture();
			GlStateManager.blendFuncSeparate(
				GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO
			);
			GlStateManager.pushMatrix();
			float j = 1.0F - this.world.getRainGradient(f);
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, j);
			GlStateManager.rotatef(-90.0F, 0.0F, 1.0F, 0.0F);
			GlStateManager.rotatef(this.world.getSkyAngle(f) * 360.0F, 1.0F, 0.0F, 0.0F);
			float k = 30.0F;
			this.field_4057.method_4618(field_4111);
			bufferBuilder.method_1328(7, VertexFormats.field_1585);
			bufferBuilder.vertex((double)(-k), 100.0, (double)(-k)).texture(0.0, 0.0).next();
			bufferBuilder.vertex((double)k, 100.0, (double)(-k)).texture(1.0, 0.0).next();
			bufferBuilder.vertex((double)k, 100.0, (double)k).texture(1.0, 1.0).next();
			bufferBuilder.vertex((double)(-k), 100.0, (double)k).texture(0.0, 1.0).next();
			tessellator.draw();
			k = 20.0F;
			this.field_4057.method_4618(field_4098);
			int r = this.world.getMoonPhase();
			int m = r % 4;
			int n = r / 4 % 2;
			float o = (float)(m + 0) / 4.0F;
			float p = (float)(n + 0) / 2.0F;
			float q = (float)(m + 1) / 4.0F;
			float s = (float)(n + 1) / 2.0F;
			bufferBuilder.method_1328(7, VertexFormats.field_1585);
			bufferBuilder.vertex((double)(-k), -100.0, (double)k).texture((double)q, (double)s).next();
			bufferBuilder.vertex((double)k, -100.0, (double)k).texture((double)o, (double)s).next();
			bufferBuilder.vertex((double)k, -100.0, (double)(-k)).texture((double)o, (double)p).next();
			bufferBuilder.vertex((double)(-k), -100.0, (double)(-k)).texture((double)q, (double)p).next();
			tessellator.draw();
			GlStateManager.disableTexture();
			float t = this.world.getStarsBrightness(f) * j;
			if (t > 0.0F) {
				GlStateManager.color4f(t, t, t, t);
				if (this.vertexBufferObjectsEnabled) {
					this.starsBuffer.bind();
					GlStateManager.enableClientState(32884);
					GlStateManager.vertexPointer(3, 5126, 12, 0);
					this.starsBuffer.draw(7);
					GlBuffer.unbind();
					GlStateManager.disableClientState(32884);
				} else {
					GlStateManager.callList(this.starsDisplayList);
				}
			}

			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			GlStateManager.disableBlend();
			GlStateManager.enableAlphaTest();
			GlStateManager.enableFog();
			GlStateManager.popMatrix();
			GlStateManager.disableTexture();
			GlStateManager.color3f(0.0F, 0.0F, 0.0F);
			double d = this.client.field_1724.method_5836(f).y - this.world.getHorizonHeight();
			if (d < 0.0) {
				GlStateManager.pushMatrix();
				GlStateManager.translatef(0.0F, 12.0F, 0.0F);
				if (this.vertexBufferObjectsEnabled) {
					this.field_4102.bind();
					GlStateManager.enableClientState(32884);
					GlStateManager.vertexPointer(3, 5126, 12, 0);
					this.field_4102.draw(7);
					GlBuffer.unbind();
					GlStateManager.disableClientState(32884);
				} else {
					GlStateManager.callList(this.field_4067);
				}

				GlStateManager.popMatrix();
			}

			if (this.world.field_9247.method_12449()) {
				GlStateManager.color3f(g * 0.2F + 0.04F, h * 0.2F + 0.04F, i * 0.6F + 0.1F);
			} else {
				GlStateManager.color3f(g, h, i);
			}

			GlStateManager.pushMatrix();
			GlStateManager.translatef(0.0F, -((float)(d - 16.0)), 0.0F);
			GlStateManager.callList(this.field_4067);
			GlStateManager.popMatrix();
			GlStateManager.enableTexture();
			GlStateManager.depthMask(true);
		}
	}

	public void renderClouds(float f, double d, double e, double g) {
		if (this.client.field_1687.field_9247.hasVisibleSky()) {
			float h = 12.0F;
			float i = 4.0F;
			double j = 2.0E-4;
			double k = (double)(((float)this.ticks + f) * 0.03F);
			double l = (d + k) / 12.0;
			double m = (double)(this.world.field_9247.getCloudHeight() - (float)e + 0.33F);
			double n = g / 12.0 + 0.33F;
			l -= (double)(MathHelper.floor(l / 2048.0) * 2048);
			n -= (double)(MathHelper.floor(n / 2048.0) * 2048);
			float o = (float)(l - (double)MathHelper.floor(l));
			float p = (float)(m / 4.0 - (double)MathHelper.floor(m / 4.0)) * 4.0F;
			float q = (float)(n - (double)MathHelper.floor(n));
			net.minecraft.util.math.Vec3d vec3d = this.world.method_8423(f);
			int r = (int)Math.floor(l);
			int s = (int)Math.floor(m / 4.0);
			int t = (int)Math.floor(n);
			if (r != this.field_4082
				|| s != this.field_4097
				|| t != this.field_4116
				|| this.client.field_1690.getCloudRenderMode() != this.field_4080
				|| this.field_4072.squaredDistanceTo(vec3d) > 2.0E-4) {
				this.field_4082 = r;
				this.field_4097 = s;
				this.field_4116 = t;
				this.field_4072 = vec3d;
				this.field_4080 = this.client.field_1690.getCloudRenderMode();
				this.cloudsDirty = true;
			}

			if (this.cloudsDirty) {
				this.cloudsDirty = false;
				Tessellator tessellator = Tessellator.getInstance();
				BufferBuilder bufferBuilder = tessellator.getBufferBuilder();
				if (this.cloudsBuffer != null) {
					this.cloudsBuffer.delete();
				}

				if (this.cloudsDisplayList >= 0) {
					GlAllocationUtils.deleteSingletonList(this.cloudsDisplayList);
					this.cloudsDisplayList = -1;
				}

				if (this.vertexBufferObjectsEnabled) {
					this.cloudsBuffer = new GlBuffer(VertexFormats.field_1577);
					this.renderClouds(bufferBuilder, l, m, n, vec3d);
					bufferBuilder.end();
					bufferBuilder.clear();
					this.cloudsBuffer.set(bufferBuilder.getByteBuffer());
				} else {
					this.cloudsDisplayList = GlAllocationUtils.genLists(1);
					GlStateManager.newList(this.cloudsDisplayList, 4864);
					this.renderClouds(bufferBuilder, l, m, n, vec3d);
					tessellator.draw();
					GlStateManager.endList();
				}
			}

			GlStateManager.disableCull();
			this.field_4057.method_4618(field_4108);
			GlStateManager.enableBlend();
			GlStateManager.blendFuncSeparate(
				GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO
			);
			GlStateManager.pushMatrix();
			GlStateManager.scalef(12.0F, 1.0F, 12.0F);
			GlStateManager.translatef(-o, p, -q);
			if (this.vertexBufferObjectsEnabled && this.cloudsBuffer != null) {
				this.cloudsBuffer.bind();
				GlStateManager.enableClientState(32884);
				GlStateManager.enableClientState(32888);
				GLX.glClientActiveTexture(GLX.GL_TEXTURE0);
				GlStateManager.enableClientState(32886);
				GlStateManager.enableClientState(32885);
				GlStateManager.vertexPointer(3, 5126, 28, 0);
				GlStateManager.texCoordPointer(2, 5126, 28, 12);
				GlStateManager.colorPointer(4, 5121, 28, 20);
				GlStateManager.normalPointer(5120, 28, 24);
				int u = this.field_4080 == CloudRenderMode.field_18164 ? 0 : 1;

				for (int v = u; v < 2; v++) {
					if (v == 0) {
						GlStateManager.colorMask(false, false, false, false);
					} else {
						GlStateManager.colorMask(true, true, true, true);
					}

					this.cloudsBuffer.draw(7);
				}

				GlBuffer.unbind();
				GlStateManager.disableClientState(32884);
				GlStateManager.disableClientState(32888);
				GlStateManager.disableClientState(32886);
				GlStateManager.disableClientState(32885);
			} else if (this.cloudsDisplayList >= 0) {
				int u = this.field_4080 == CloudRenderMode.field_18164 ? 0 : 1;

				for (int v = u; v < 2; v++) {
					if (v == 0) {
						GlStateManager.colorMask(false, false, false, false);
					} else {
						GlStateManager.colorMask(true, true, true, true);
					}

					GlStateManager.callList(this.cloudsDisplayList);
				}
			}

			GlStateManager.popMatrix();
			GlStateManager.clearCurrentColor();
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			GlStateManager.disableBlend();
			GlStateManager.enableCull();
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
		bufferBuilder.method_1328(7, VertexFormats.field_1577);
		float z = (float)Math.floor(e / 4.0) * 4.0F;
		if (this.field_4080 == CloudRenderMode.field_18164) {
			for (int aa = -3; aa <= 4; aa++) {
				for (int ab = -3; ab <= 4; ab++) {
					float ac = (float)(aa * 8);
					float ad = (float)(ab * 8);
					if (z > -5.0F) {
						bufferBuilder.vertex((double)(ac + 0.0F), (double)(z + 0.0F), (double)(ad + 8.0F))
							.texture((double)((ac + 0.0F) * 0.00390625F + l), (double)((ad + 8.0F) * 0.00390625F + m))
							.color(t, u, v, 0.8F)
							.normal(0.0F, -1.0F, 0.0F)
							.next();
						bufferBuilder.vertex((double)(ac + 8.0F), (double)(z + 0.0F), (double)(ad + 8.0F))
							.texture((double)((ac + 8.0F) * 0.00390625F + l), (double)((ad + 8.0F) * 0.00390625F + m))
							.color(t, u, v, 0.8F)
							.normal(0.0F, -1.0F, 0.0F)
							.next();
						bufferBuilder.vertex((double)(ac + 8.0F), (double)(z + 0.0F), (double)(ad + 0.0F))
							.texture((double)((ac + 8.0F) * 0.00390625F + l), (double)((ad + 0.0F) * 0.00390625F + m))
							.color(t, u, v, 0.8F)
							.normal(0.0F, -1.0F, 0.0F)
							.next();
						bufferBuilder.vertex((double)(ac + 0.0F), (double)(z + 0.0F), (double)(ad + 0.0F))
							.texture((double)((ac + 0.0F) * 0.00390625F + l), (double)((ad + 0.0F) * 0.00390625F + m))
							.color(t, u, v, 0.8F)
							.normal(0.0F, -1.0F, 0.0F)
							.next();
					}

					if (z <= 5.0F) {
						bufferBuilder.vertex((double)(ac + 0.0F), (double)(z + 4.0F - 9.765625E-4F), (double)(ad + 8.0F))
							.texture((double)((ac + 0.0F) * 0.00390625F + l), (double)((ad + 8.0F) * 0.00390625F + m))
							.color(n, o, p, 0.8F)
							.normal(0.0F, 1.0F, 0.0F)
							.next();
						bufferBuilder.vertex((double)(ac + 8.0F), (double)(z + 4.0F - 9.765625E-4F), (double)(ad + 8.0F))
							.texture((double)((ac + 8.0F) * 0.00390625F + l), (double)((ad + 8.0F) * 0.00390625F + m))
							.color(n, o, p, 0.8F)
							.normal(0.0F, 1.0F, 0.0F)
							.next();
						bufferBuilder.vertex((double)(ac + 8.0F), (double)(z + 4.0F - 9.765625E-4F), (double)(ad + 0.0F))
							.texture((double)((ac + 8.0F) * 0.00390625F + l), (double)((ad + 0.0F) * 0.00390625F + m))
							.color(n, o, p, 0.8F)
							.normal(0.0F, 1.0F, 0.0F)
							.next();
						bufferBuilder.vertex((double)(ac + 0.0F), (double)(z + 4.0F - 9.765625E-4F), (double)(ad + 0.0F))
							.texture((double)((ac + 0.0F) * 0.00390625F + l), (double)((ad + 0.0F) * 0.00390625F + m))
							.color(n, o, p, 0.8F)
							.normal(0.0F, 1.0F, 0.0F)
							.next();
					}

					if (aa > -1) {
						for (int ae = 0; ae < 8; ae++) {
							bufferBuilder.vertex((double)(ac + (float)ae + 0.0F), (double)(z + 0.0F), (double)(ad + 8.0F))
								.texture((double)((ac + (float)ae + 0.5F) * 0.00390625F + l), (double)((ad + 8.0F) * 0.00390625F + m))
								.color(q, r, s, 0.8F)
								.normal(-1.0F, 0.0F, 0.0F)
								.next();
							bufferBuilder.vertex((double)(ac + (float)ae + 0.0F), (double)(z + 4.0F), (double)(ad + 8.0F))
								.texture((double)((ac + (float)ae + 0.5F) * 0.00390625F + l), (double)((ad + 8.0F) * 0.00390625F + m))
								.color(q, r, s, 0.8F)
								.normal(-1.0F, 0.0F, 0.0F)
								.next();
							bufferBuilder.vertex((double)(ac + (float)ae + 0.0F), (double)(z + 4.0F), (double)(ad + 0.0F))
								.texture((double)((ac + (float)ae + 0.5F) * 0.00390625F + l), (double)((ad + 0.0F) * 0.00390625F + m))
								.color(q, r, s, 0.8F)
								.normal(-1.0F, 0.0F, 0.0F)
								.next();
							bufferBuilder.vertex((double)(ac + (float)ae + 0.0F), (double)(z + 0.0F), (double)(ad + 0.0F))
								.texture((double)((ac + (float)ae + 0.5F) * 0.00390625F + l), (double)((ad + 0.0F) * 0.00390625F + m))
								.color(q, r, s, 0.8F)
								.normal(-1.0F, 0.0F, 0.0F)
								.next();
						}
					}

					if (aa <= 1) {
						for (int ae = 0; ae < 8; ae++) {
							bufferBuilder.vertex((double)(ac + (float)ae + 1.0F - 9.765625E-4F), (double)(z + 0.0F), (double)(ad + 8.0F))
								.texture((double)((ac + (float)ae + 0.5F) * 0.00390625F + l), (double)((ad + 8.0F) * 0.00390625F + m))
								.color(q, r, s, 0.8F)
								.normal(1.0F, 0.0F, 0.0F)
								.next();
							bufferBuilder.vertex((double)(ac + (float)ae + 1.0F - 9.765625E-4F), (double)(z + 4.0F), (double)(ad + 8.0F))
								.texture((double)((ac + (float)ae + 0.5F) * 0.00390625F + l), (double)((ad + 8.0F) * 0.00390625F + m))
								.color(q, r, s, 0.8F)
								.normal(1.0F, 0.0F, 0.0F)
								.next();
							bufferBuilder.vertex((double)(ac + (float)ae + 1.0F - 9.765625E-4F), (double)(z + 4.0F), (double)(ad + 0.0F))
								.texture((double)((ac + (float)ae + 0.5F) * 0.00390625F + l), (double)((ad + 0.0F) * 0.00390625F + m))
								.color(q, r, s, 0.8F)
								.normal(1.0F, 0.0F, 0.0F)
								.next();
							bufferBuilder.vertex((double)(ac + (float)ae + 1.0F - 9.765625E-4F), (double)(z + 0.0F), (double)(ad + 0.0F))
								.texture((double)((ac + (float)ae + 0.5F) * 0.00390625F + l), (double)((ad + 0.0F) * 0.00390625F + m))
								.color(q, r, s, 0.8F)
								.normal(1.0F, 0.0F, 0.0F)
								.next();
						}
					}

					if (ab > -1) {
						for (int ae = 0; ae < 8; ae++) {
							bufferBuilder.vertex((double)(ac + 0.0F), (double)(z + 4.0F), (double)(ad + (float)ae + 0.0F))
								.texture((double)((ac + 0.0F) * 0.00390625F + l), (double)((ad + (float)ae + 0.5F) * 0.00390625F + m))
								.color(w, x, y, 0.8F)
								.normal(0.0F, 0.0F, -1.0F)
								.next();
							bufferBuilder.vertex((double)(ac + 8.0F), (double)(z + 4.0F), (double)(ad + (float)ae + 0.0F))
								.texture((double)((ac + 8.0F) * 0.00390625F + l), (double)((ad + (float)ae + 0.5F) * 0.00390625F + m))
								.color(w, x, y, 0.8F)
								.normal(0.0F, 0.0F, -1.0F)
								.next();
							bufferBuilder.vertex((double)(ac + 8.0F), (double)(z + 0.0F), (double)(ad + (float)ae + 0.0F))
								.texture((double)((ac + 8.0F) * 0.00390625F + l), (double)((ad + (float)ae + 0.5F) * 0.00390625F + m))
								.color(w, x, y, 0.8F)
								.normal(0.0F, 0.0F, -1.0F)
								.next();
							bufferBuilder.vertex((double)(ac + 0.0F), (double)(z + 0.0F), (double)(ad + (float)ae + 0.0F))
								.texture((double)((ac + 0.0F) * 0.00390625F + l), (double)((ad + (float)ae + 0.5F) * 0.00390625F + m))
								.color(w, x, y, 0.8F)
								.normal(0.0F, 0.0F, -1.0F)
								.next();
						}
					}

					if (ab <= 1) {
						for (int ae = 0; ae < 8; ae++) {
							bufferBuilder.vertex((double)(ac + 0.0F), (double)(z + 4.0F), (double)(ad + (float)ae + 1.0F - 9.765625E-4F))
								.texture((double)((ac + 0.0F) * 0.00390625F + l), (double)((ad + (float)ae + 0.5F) * 0.00390625F + m))
								.color(w, x, y, 0.8F)
								.normal(0.0F, 0.0F, 1.0F)
								.next();
							bufferBuilder.vertex((double)(ac + 8.0F), (double)(z + 4.0F), (double)(ad + (float)ae + 1.0F - 9.765625E-4F))
								.texture((double)((ac + 8.0F) * 0.00390625F + l), (double)((ad + (float)ae + 0.5F) * 0.00390625F + m))
								.color(w, x, y, 0.8F)
								.normal(0.0F, 0.0F, 1.0F)
								.next();
							bufferBuilder.vertex((double)(ac + 8.0F), (double)(z + 0.0F), (double)(ad + (float)ae + 1.0F - 9.765625E-4F))
								.texture((double)((ac + 8.0F) * 0.00390625F + l), (double)((ad + (float)ae + 0.5F) * 0.00390625F + m))
								.color(w, x, y, 0.8F)
								.normal(0.0F, 0.0F, 1.0F)
								.next();
							bufferBuilder.vertex((double)(ac + 0.0F), (double)(z + 0.0F), (double)(ad + (float)ae + 1.0F - 9.765625E-4F))
								.texture((double)((ac + 0.0F) * 0.00390625F + l), (double)((ad + (float)ae + 0.5F) * 0.00390625F + m))
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
						.texture((double)((float)(af + 0) * 0.00390625F + l), (double)((float)(ag + 32) * 0.00390625F + m))
						.color(n, o, p, 0.8F)
						.normal(0.0F, -1.0F, 0.0F)
						.next();
					bufferBuilder.vertex((double)(af + 32), (double)z, (double)(ag + 32))
						.texture((double)((float)(af + 32) * 0.00390625F + l), (double)((float)(ag + 32) * 0.00390625F + m))
						.color(n, o, p, 0.8F)
						.normal(0.0F, -1.0F, 0.0F)
						.next();
					bufferBuilder.vertex((double)(af + 32), (double)z, (double)(ag + 0))
						.texture((double)((float)(af + 32) * 0.00390625F + l), (double)((float)(ag + 0) * 0.00390625F + m))
						.color(n, o, p, 0.8F)
						.normal(0.0F, -1.0F, 0.0F)
						.next();
					bufferBuilder.vertex((double)(af + 0), (double)z, (double)(ag + 0))
						.texture((double)((float)(af + 0) * 0.00390625F + l), (double)((float)(ag + 0) * 0.00390625F + m))
						.color(n, o, p, 0.8F)
						.normal(0.0F, -1.0F, 0.0F)
						.next();
				}
			}
		}
	}

	public void updateChunks(long l) {
		this.terrainUpdateNecessary = this.terrainUpdateNecessary | this.field_4106.method_3631(l);
		if (!this.chunkRenderers.isEmpty()) {
			Iterator<ChunkRenderer> iterator = this.chunkRenderers.iterator();

			while (iterator.hasNext()) {
				ChunkRenderer chunkRenderer = (ChunkRenderer)iterator.next();
				boolean bl;
				if (chunkRenderer.method_3661()) {
					bl = this.field_4106.method_3627(chunkRenderer);
				} else {
					bl = this.field_4106.method_3624(chunkRenderer);
				}

				if (!bl) {
					break;
				}

				chunkRenderer.method_3662();
				iterator.remove();
				long m = l - SystemUtil.getMeasuringTimeNano();
				if (m < 0L) {
					break;
				}
			}
		}
	}

	public void renderWorldBorder(class_4184 arg, float f) {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBufferBuilder();
		WorldBorder worldBorder = this.world.method_8621();
		double d = (double)(this.client.field_1690.viewDistance * 16);
		if (!(arg.method_19326().x < worldBorder.getBoundEast() - d)
			|| !(arg.method_19326().x > worldBorder.getBoundWest() + d)
			|| !(arg.method_19326().z < worldBorder.getBoundSouth() - d)
			|| !(arg.method_19326().z > worldBorder.getBoundNorth() + d)) {
			double e = 1.0 - worldBorder.contains(arg.method_19326().x, arg.method_19326().z) / d;
			e = Math.pow(e, 4.0);
			double g = arg.method_19326().x;
			double h = arg.method_19326().y;
			double i = arg.method_19326().z;
			GlStateManager.enableBlend();
			GlStateManager.blendFuncSeparate(
				GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO
			);
			this.field_4057.method_4618(field_4071);
			GlStateManager.depthMask(false);
			GlStateManager.pushMatrix();
			int j = worldBorder.getStage().getColor();
			float k = (float)(j >> 16 & 0xFF) / 255.0F;
			float l = (float)(j >> 8 & 0xFF) / 255.0F;
			float m = (float)(j & 0xFF) / 255.0F;
			GlStateManager.color4f(k, l, m, (float)e);
			GlStateManager.polygonOffset(-3.0F, -3.0F);
			GlStateManager.enablePolygonOffset();
			GlStateManager.alphaFunc(516, 0.1F);
			GlStateManager.enableAlphaTest();
			GlStateManager.disableCull();
			float n = (float)(SystemUtil.getMeasuringTimeMs() % 3000L) / 3000.0F;
			float o = 0.0F;
			float p = 0.0F;
			float q = 128.0F;
			bufferBuilder.method_1328(7, VertexFormats.field_1585);
			bufferBuilder.setOffset(-g, -h, -i);
			double r = Math.max((double)MathHelper.floor(i - d), worldBorder.getBoundNorth());
			double s = Math.min((double)MathHelper.ceil(i + d), worldBorder.getBoundSouth());
			if (g > worldBorder.getBoundEast() - d) {
				float t = 0.0F;

				for (double u = r; u < s; t += 0.5F) {
					double v = Math.min(1.0, s - u);
					float w = (float)v * 0.5F;
					bufferBuilder.vertex(worldBorder.getBoundEast(), 256.0, u).texture((double)(n + t), (double)(n + 0.0F)).next();
					bufferBuilder.vertex(worldBorder.getBoundEast(), 256.0, u + v).texture((double)(n + w + t), (double)(n + 0.0F)).next();
					bufferBuilder.vertex(worldBorder.getBoundEast(), 0.0, u + v).texture((double)(n + w + t), (double)(n + 128.0F)).next();
					bufferBuilder.vertex(worldBorder.getBoundEast(), 0.0, u).texture((double)(n + t), (double)(n + 128.0F)).next();
					u++;
				}
			}

			if (g < worldBorder.getBoundWest() + d) {
				float t = 0.0F;

				for (double u = r; u < s; t += 0.5F) {
					double v = Math.min(1.0, s - u);
					float w = (float)v * 0.5F;
					bufferBuilder.vertex(worldBorder.getBoundWest(), 256.0, u).texture((double)(n + t), (double)(n + 0.0F)).next();
					bufferBuilder.vertex(worldBorder.getBoundWest(), 256.0, u + v).texture((double)(n + w + t), (double)(n + 0.0F)).next();
					bufferBuilder.vertex(worldBorder.getBoundWest(), 0.0, u + v).texture((double)(n + w + t), (double)(n + 128.0F)).next();
					bufferBuilder.vertex(worldBorder.getBoundWest(), 0.0, u).texture((double)(n + t), (double)(n + 128.0F)).next();
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
					bufferBuilder.vertex(u, 256.0, worldBorder.getBoundSouth()).texture((double)(n + t), (double)(n + 0.0F)).next();
					bufferBuilder.vertex(u + v, 256.0, worldBorder.getBoundSouth()).texture((double)(n + w + t), (double)(n + 0.0F)).next();
					bufferBuilder.vertex(u + v, 0.0, worldBorder.getBoundSouth()).texture((double)(n + w + t), (double)(n + 128.0F)).next();
					bufferBuilder.vertex(u, 0.0, worldBorder.getBoundSouth()).texture((double)(n + t), (double)(n + 128.0F)).next();
					u++;
				}
			}

			if (i < worldBorder.getBoundNorth() + d) {
				float t = 0.0F;

				for (double u = r; u < s; t += 0.5F) {
					double v = Math.min(1.0, s - u);
					float w = (float)v * 0.5F;
					bufferBuilder.vertex(u, 256.0, worldBorder.getBoundNorth()).texture((double)(n + t), (double)(n + 0.0F)).next();
					bufferBuilder.vertex(u + v, 256.0, worldBorder.getBoundNorth()).texture((double)(n + w + t), (double)(n + 0.0F)).next();
					bufferBuilder.vertex(u + v, 0.0, worldBorder.getBoundNorth()).texture((double)(n + w + t), (double)(n + 128.0F)).next();
					bufferBuilder.vertex(u, 0.0, worldBorder.getBoundNorth()).texture((double)(n + t), (double)(n + 128.0F)).next();
					u++;
				}
			}

			tessellator.draw();
			bufferBuilder.setOffset(0.0, 0.0, 0.0);
			GlStateManager.enableCull();
			GlStateManager.disableAlphaTest();
			GlStateManager.polygonOffset(0.0F, 0.0F);
			GlStateManager.disablePolygonOffset();
			GlStateManager.enableAlphaTest();
			GlStateManager.disableBlend();
			GlStateManager.popMatrix();
			GlStateManager.depthMask(true);
		}
	}

	private void method_3263() {
		GlStateManager.blendFuncSeparate(
			GlStateManager.SourceFactor.DST_COLOR, GlStateManager.DestFactor.SRC_COLOR, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO
		);
		GlStateManager.enableBlend();
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 0.5F);
		GlStateManager.polygonOffset(-1.0F, -10.0F);
		GlStateManager.enablePolygonOffset();
		GlStateManager.alphaFunc(516, 0.1F);
		GlStateManager.enableAlphaTest();
		GlStateManager.pushMatrix();
	}

	private void method_3274() {
		GlStateManager.disableAlphaTest();
		GlStateManager.polygonOffset(0.0F, 0.0F);
		GlStateManager.disablePolygonOffset();
		GlStateManager.enableAlphaTest();
		GlStateManager.depthMask(true);
		GlStateManager.popMatrix();
	}

	public void renderPartiallyBrokenBlocks(Tessellator tessellator, BufferBuilder bufferBuilder, class_4184 arg) {
		double d = arg.method_19326().x;
		double e = arg.method_19326().y;
		double f = arg.method_19326().z;
		if (!this.partiallyBrokenBlocks.isEmpty()) {
			this.field_4057.method_4618(SpriteAtlasTexture.field_5275);
			this.method_3263();
			bufferBuilder.method_1328(7, VertexFormats.field_1582);
			bufferBuilder.setOffset(-d, -e, -f);
			bufferBuilder.disableColor();
			Iterator<PartiallyBrokenBlockEntry> iterator = this.partiallyBrokenBlocks.values().iterator();

			while (iterator.hasNext()) {
				PartiallyBrokenBlockEntry partiallyBrokenBlockEntry = (PartiallyBrokenBlockEntry)iterator.next();
				BlockPos blockPos = partiallyBrokenBlockEntry.getPos();
				Block block = this.world.method_8320(blockPos).getBlock();
				if (!(block instanceof ChestBlock) && !(block instanceof EnderChestBlock) && !(block instanceof SignBlock) && !(block instanceof AbstractSkullBlock)) {
					double g = (double)blockPos.getX() - d;
					double h = (double)blockPos.getY() - e;
					double i = (double)blockPos.getZ() - f;
					if (g * g + h * h + i * i > 1024.0) {
						iterator.remove();
					} else {
						BlockState blockState = this.world.method_8320(blockPos);
						if (!blockState.isAir()) {
							int j = partiallyBrokenBlockEntry.getStage();
							Sprite sprite = this.field_4068[j];
							BlockRenderManager blockRenderManager = this.client.method_1541();
							blockRenderManager.method_3354(blockState, blockPos, sprite, this.world);
						}
					}
				}
			}

			tessellator.draw();
			bufferBuilder.setOffset(0.0, 0.0, 0.0);
			this.method_3274();
		}
	}

	public void drawHighlightedBlockOutline(class_4184 arg, HitResult hitResult, int i) {
		if (i == 0 && hitResult.getType() == HitResult.Type.BLOCK) {
			BlockPos blockPos = ((BlockHitResult)hitResult).method_17777();
			BlockState blockState = this.world.method_8320(blockPos);
			if (!blockState.isAir() && this.world.method_8621().method_11952(blockPos)) {
				GlStateManager.enableBlend();
				GlStateManager.blendFuncSeparate(
					GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO
				);
				GlStateManager.lineWidth(Math.max(2.5F, (float)this.client.window.getFramebufferWidth() / 1920.0F * 2.5F));
				GlStateManager.disableTexture();
				GlStateManager.depthMask(false);
				GlStateManager.matrixMode(5889);
				GlStateManager.pushMatrix();
				GlStateManager.scalef(1.0F, 1.0F, 0.999F);
				double d = arg.method_19326().x;
				double e = arg.method_19326().y;
				double f = arg.method_19326().z;
				drawShapeOutline(
					blockState.method_11606(this.world, blockPos, VerticalEntityPosition.fromEntity(arg.method_19331())),
					(double)blockPos.getX() - d,
					(double)blockPos.getY() - e,
					(double)blockPos.getZ() - f,
					0.0F,
					0.0F,
					0.0F,
					0.4F
				);
				GlStateManager.popMatrix();
				GlStateManager.matrixMode(5888);
				GlStateManager.depthMask(true);
				GlStateManager.enableTexture();
				GlStateManager.disableBlend();
			}
		}
	}

	public static void drawDebugShapeOutline(VoxelShape voxelShape, double d, double e, double f, float g, float h, float i, float j) {
		List<BoundingBox> list = voxelShape.getBoundingBoxList();
		int k = MathHelper.ceil((double)list.size() / 3.0);

		for (int l = 0; l < list.size(); l++) {
			BoundingBox boundingBox = (BoundingBox)list.get(l);
			float m = ((float)l % (float)k + 1.0F) / (float)k;
			float n = (float)(l / k);
			float o = m * (float)(n == 0.0F ? 1 : 0);
			float p = m * (float)(n == 1.0F ? 1 : 0);
			float q = m * (float)(n == 2.0F ? 1 : 0);
			drawShapeOutline(VoxelShapes.method_1078(boundingBox.offset(0.0, 0.0, 0.0)), d, e, f, o, p, q, 1.0F);
		}
	}

	public static void drawShapeOutline(VoxelShape voxelShape, double d, double e, double f, float g, float h, float i, float j) {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBufferBuilder();
		bufferBuilder.method_1328(1, VertexFormats.field_1576);
		voxelShape.method_1104((k, l, m, n, o, p) -> {
			bufferBuilder.vertex(k + d, l + e, m + f).color(g, h, i, j).next();
			bufferBuilder.vertex(n + d, o + e, p + f).color(g, h, i, j).next();
		});
		tessellator.draw();
	}

	public static void drawBoxOutline(BoundingBox boundingBox, float f, float g, float h, float i) {
		drawBoxOutline(boundingBox.minX, boundingBox.minY, boundingBox.minZ, boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ, f, g, h, i);
	}

	public static void drawBoxOutline(double d, double e, double f, double g, double h, double i, float j, float k, float l, float m) {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBufferBuilder();
		bufferBuilder.method_1328(3, VertexFormats.field_1576);
		buildBoxOutline(bufferBuilder, d, e, f, g, h, i, j, k, l, m);
		tessellator.draw();
	}

	public static void buildBoxOutline(BufferBuilder bufferBuilder, double d, double e, double f, double g, double h, double i, float j, float k, float l, float m) {
		bufferBuilder.vertex(d, e, f).color(j, k, l, 0.0F).next();
		bufferBuilder.vertex(d, e, f).color(j, k, l, m).next();
		bufferBuilder.vertex(g, e, f).color(j, k, l, m).next();
		bufferBuilder.vertex(g, e, i).color(j, k, l, m).next();
		bufferBuilder.vertex(d, e, i).color(j, k, l, m).next();
		bufferBuilder.vertex(d, e, f).color(j, k, l, m).next();
		bufferBuilder.vertex(d, h, f).color(j, k, l, m).next();
		bufferBuilder.vertex(g, h, f).color(j, k, l, m).next();
		bufferBuilder.vertex(g, h, i).color(j, k, l, m).next();
		bufferBuilder.vertex(d, h, i).color(j, k, l, m).next();
		bufferBuilder.vertex(d, h, f).color(j, k, l, m).next();
		bufferBuilder.vertex(d, h, i).color(j, k, l, 0.0F).next();
		bufferBuilder.vertex(d, e, i).color(j, k, l, m).next();
		bufferBuilder.vertex(g, h, i).color(j, k, l, 0.0F).next();
		bufferBuilder.vertex(g, e, i).color(j, k, l, m).next();
		bufferBuilder.vertex(g, h, f).color(j, k, l, 0.0F).next();
		bufferBuilder.vertex(g, e, f).color(j, k, l, m).next();
		bufferBuilder.vertex(g, e, f).color(j, k, l, 0.0F).next();
	}

	public static void drawBox(BoundingBox boundingBox, float f, float g, float h, float i) {
		drawBox(boundingBox.minX, boundingBox.minY, boundingBox.minZ, boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ, f, g, h, i);
	}

	public static void drawBox(double d, double e, double f, double g, double h, double i, float j, float k, float l, float m) {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBufferBuilder();
		bufferBuilder.method_1328(5, VertexFormats.field_1576);
		buildBox(bufferBuilder, d, e, f, g, h, i, j, k, l, m);
		tessellator.draw();
	}

	public static void buildBox(BufferBuilder bufferBuilder, double d, double e, double f, double g, double h, double i, float j, float k, float l, float m) {
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

	public void method_8570(BlockView blockView, BlockPos blockPos, BlockState blockState, BlockState blockState2, int i) {
		this.method_16037(blockPos, (i & 8) != 0);
	}

	private void method_16037(BlockPos blockPos, boolean bl) {
		for (int i = blockPos.getZ() - 1; i <= blockPos.getZ() + 1; i++) {
			for (int j = blockPos.getX() - 1; j <= blockPos.getX() + 1; j++) {
				for (int k = blockPos.getY() - 1; k <= blockPos.getY() + 1; k++) {
					this.scheduleChunkRender(j >> 4, k >> 4, i >> 4, bl);
				}
			}
		}
	}

	public void scheduleBlockRender(int i, int j, int k, int l, int m, int n) {
		for (int o = k - 1; o <= n + 1; o++) {
			for (int p = i - 1; p <= l + 1; p++) {
				for (int q = j - 1; q <= m + 1; q++) {
					this.method_8571(p >> 4, q >> 4, o >> 4);
				}
			}
		}
	}

	public void method_18145(int i, int j, int k) {
		for (int l = k - 1; l <= k + 1; l++) {
			for (int m = i - 1; m <= i + 1; m++) {
				for (int n = j - 1; n <= j + 1; n++) {
					this.method_8571(m, n, l);
				}
			}
		}
	}

	public void method_8571(int i, int j, int k) {
		this.scheduleChunkRender(i, j, k, false);
	}

	private void scheduleChunkRender(int i, int j, int k, boolean bl) {
		this.field_4112.scheduleChunkRender(i, j, k, bl);
	}

	public void method_8562(@Nullable SoundEvent soundEvent, BlockPos blockPos) {
		SoundInstance soundInstance = (SoundInstance)this.field_4119.get(blockPos);
		if (soundInstance != null) {
			this.client.method_1483().stop(soundInstance);
			this.field_4119.remove(blockPos);
		}

		if (soundEvent != null) {
			RecordItem recordItem = RecordItem.method_8012(soundEvent);
			if (recordItem != null) {
				this.client.field_1705.setRecordPlayingOverlay(recordItem.method_8011().getFormattedText());
			}

			SoundInstance var5 = PositionedSoundInstance.method_4760(soundEvent, (float)blockPos.getX(), (float)blockPos.getY(), (float)blockPos.getZ());
			this.field_4119.put(blockPos, var5);
			this.client.method_1483().play(var5);
		}

		this.method_3247(this.world, blockPos, soundEvent != null);
	}

	private void method_3247(World world, BlockPos blockPos, boolean bl) {
		for (LivingEntity livingEntity : world.method_18467(LivingEntity.class, new BoundingBox(blockPos).expand(3.0))) {
			livingEntity.method_6006(blockPos, bl);
		}
	}

	public void method_8568(ParticleParameters particleParameters, boolean bl, double d, double e, double f, double g, double h, double i) {
		this.method_8563(particleParameters, bl, false, d, e, f, g, h, i);
	}

	public void method_8563(ParticleParameters particleParameters, boolean bl, boolean bl2, double d, double e, double f, double g, double h, double i) {
		try {
			this.method_3288(particleParameters, bl, bl2, d, e, f, g, h, i);
		} catch (Throwable var19) {
			CrashReport crashReport = CrashReport.create(var19, "Exception while adding particle");
			CrashReportSection crashReportSection = crashReport.method_562("Particle being added");
			crashReportSection.add("ID", Registry.PARTICLE_TYPE.method_10221((ParticleType<? extends ParticleParameters>)particleParameters.method_10295()));
			crashReportSection.add("Parameters", particleParameters.asString());
			crashReportSection.method_577("Position", () -> CrashReportSection.createPositionString(d, e, f));
			throw new CrashException(crashReport);
		}
	}

	private <T extends ParticleParameters> void method_3276(T particleParameters, double d, double e, double f, double g, double h, double i) {
		this.method_8568(particleParameters, particleParameters.method_10295().shouldAlwaysSpawn(), d, e, f, g, h, i);
	}

	@Nullable
	private Particle method_3282(ParticleParameters particleParameters, boolean bl, double d, double e, double f, double g, double h, double i) {
		return this.method_3288(particleParameters, bl, false, d, e, f, g, h, i);
	}

	@Nullable
	private Particle method_3288(ParticleParameters particleParameters, boolean bl, boolean bl2, double d, double e, double f, double g, double h, double i) {
		class_4184 lv = this.client.field_1773.method_19418();
		if (this.client != null && lv.method_19332() && this.client.field_1713 != null) {
			ParticlesOption particlesOption = this.getRandomParticleSpawnChance(bl2);
			if (bl) {
				return this.client.field_1713.method_3056(particleParameters, d, e, f, g, h, i);
			} else if (lv.method_19326().squaredDistanceTo(d, e, f) > 1024.0) {
				return null;
			} else {
				return particlesOption == ParticlesOption.field_18199 ? null : this.client.field_1713.method_3056(particleParameters, d, e, f, g, h, i);
			}
		} else {
			return null;
		}
	}

	private ParticlesOption getRandomParticleSpawnChance(boolean bl) {
		ParticlesOption particlesOption = this.client.field_1690.field_1882;
		if (bl && particlesOption == ParticlesOption.field_18199 && this.world.random.nextInt(10) == 0) {
			particlesOption = ParticlesOption.field_18198;
		}

		if (particlesOption == ParticlesOption.field_18198 && this.world.random.nextInt(3) == 0) {
			particlesOption = ParticlesOption.field_18199;
		}

		return particlesOption;
	}

	public void method_3267() {
	}

	public void method_8564(int i, BlockPos blockPos, int j) {
		switch (i) {
			case 1023:
			case 1028:
			case 1038:
				class_4184 lv = this.client.field_1773.method_19418();
				if (lv.method_19332()) {
					double d = (double)blockPos.getX() - lv.method_19326().x;
					double e = (double)blockPos.getY() - lv.method_19326().y;
					double f = (double)blockPos.getZ() - lv.method_19326().z;
					double g = Math.sqrt(d * d + e * e + f * f);
					double h = lv.method_19326().x;
					double k = lv.method_19326().y;
					double l = lv.method_19326().z;
					if (g > 0.0) {
						h += d / g * 2.0;
						k += e / g * 2.0;
						l += f / g * 2.0;
					}

					if (i == 1023) {
						this.world.method_8486(h, k, l, SoundEvents.field_14792, SoundCategory.field_15251, 1.0F, 1.0F, false);
					} else if (i == 1038) {
						this.world.method_8486(h, k, l, SoundEvents.field_14981, SoundCategory.field_15251, 1.0F, 1.0F, false);
					} else {
						this.world.method_8486(h, k, l, SoundEvents.field_14773, SoundCategory.field_15251, 5.0F, 1.0F, false);
					}
				}
		}
	}

	public void method_8567(PlayerEntity playerEntity, int i, BlockPos blockPos, int j) {
		Random random = this.world.random;
		switch (i) {
			case 1000:
				this.world.method_2947(blockPos, SoundEvents.field_14611, SoundCategory.field_15245, 1.0F, 1.0F, false);
				break;
			case 1001:
				this.world.method_2947(blockPos, SoundEvents.field_14701, SoundCategory.field_15245, 1.0F, 1.2F, false);
				break;
			case 1002:
				this.world.method_2947(blockPos, SoundEvents.field_14711, SoundCategory.field_15245, 1.0F, 1.2F, false);
				break;
			case 1003:
				this.world.method_2947(blockPos, SoundEvents.field_15155, SoundCategory.field_15254, 1.0F, 1.2F, false);
				break;
			case 1004:
				this.world.method_2947(blockPos, SoundEvents.field_14712, SoundCategory.field_15254, 1.0F, 1.2F, false);
				break;
			case 1005:
				this.world.method_2947(blockPos, SoundEvents.field_14567, SoundCategory.field_15245, 1.0F, this.world.random.nextFloat() * 0.1F + 0.9F, false);
				break;
			case 1006:
				this.world.method_2947(blockPos, SoundEvents.field_14664, SoundCategory.field_15245, 1.0F, this.world.random.nextFloat() * 0.1F + 0.9F, false);
				break;
			case 1007:
				this.world.method_2947(blockPos, SoundEvents.field_14932, SoundCategory.field_15245, 1.0F, this.world.random.nextFloat() * 0.1F + 0.9F, false);
				break;
			case 1008:
				this.world.method_2947(blockPos, SoundEvents.field_14766, SoundCategory.field_15245, 1.0F, this.world.random.nextFloat() * 0.1F + 0.9F, false);
				break;
			case 1009:
				this.world.method_2947(blockPos, SoundEvents.field_15102, SoundCategory.field_15245, 0.5F, 2.6F + (random.nextFloat() - random.nextFloat()) * 0.8F, false);
				break;
			case 1010:
				if (Item.byRawId(j) instanceof RecordItem) {
					this.method_8562(((RecordItem)Item.byRawId(j)).method_8009(), blockPos);
				} else {
					this.method_8562(null, blockPos);
				}
				break;
			case 1011:
				this.world.method_2947(blockPos, SoundEvents.field_14819, SoundCategory.field_15245, 1.0F, this.world.random.nextFloat() * 0.1F + 0.9F, false);
				break;
			case 1012:
				this.world.method_2947(blockPos, SoundEvents.field_14541, SoundCategory.field_15245, 1.0F, this.world.random.nextFloat() * 0.1F + 0.9F, false);
				break;
			case 1013:
				this.world.method_2947(blockPos, SoundEvents.field_15080, SoundCategory.field_15245, 1.0F, this.world.random.nextFloat() * 0.1F + 0.9F, false);
				break;
			case 1014:
				this.world.method_2947(blockPos, SoundEvents.field_14861, SoundCategory.field_15245, 1.0F, this.world.random.nextFloat() * 0.1F + 0.9F, false);
				break;
			case 1015:
				this.world.method_2947(blockPos, SoundEvents.field_15130, SoundCategory.field_15251, 10.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
				break;
			case 1016:
				this.world.method_2947(blockPos, SoundEvents.field_15231, SoundCategory.field_15251, 10.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
				break;
			case 1017:
				this.world.method_2947(blockPos, SoundEvents.field_14934, SoundCategory.field_15251, 10.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
				break;
			case 1018:
				this.world.method_2947(blockPos, SoundEvents.field_14970, SoundCategory.field_15251, 2.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
				break;
			case 1019:
				this.world.method_2947(blockPos, SoundEvents.field_14562, SoundCategory.field_15251, 2.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
				break;
			case 1020:
				this.world.method_2947(blockPos, SoundEvents.field_14670, SoundCategory.field_15251, 2.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
				break;
			case 1021:
				this.world.method_2947(blockPos, SoundEvents.field_14742, SoundCategory.field_15251, 2.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
				break;
			case 1022:
				this.world.method_2947(blockPos, SoundEvents.field_15236, SoundCategory.field_15251, 2.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
				break;
			case 1024:
				this.world.method_2947(blockPos, SoundEvents.field_14588, SoundCategory.field_15251, 2.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
				break;
			case 1025:
				this.world.method_2947(blockPos, SoundEvents.field_14610, SoundCategory.field_15254, 0.05F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
				break;
			case 1026:
				this.world.method_2947(blockPos, SoundEvents.field_14986, SoundCategory.field_15251, 2.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
				break;
			case 1027:
				this.world.method_2947(blockPos, SoundEvents.field_15168, SoundCategory.field_15254, 2.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
				break;
			case 1029:
				this.world.method_2947(blockPos, SoundEvents.field_14665, SoundCategory.field_15245, 1.0F, this.world.random.nextFloat() * 0.1F + 0.9F, false);
				break;
			case 1030:
				this.world.method_2947(blockPos, SoundEvents.field_14559, SoundCategory.field_15245, 1.0F, this.world.random.nextFloat() * 0.1F + 0.9F, false);
				break;
			case 1031:
				this.world.method_2947(blockPos, SoundEvents.field_14833, SoundCategory.field_15245, 0.3F, this.world.random.nextFloat() * 0.1F + 0.9F, false);
				break;
			case 1032:
				this.client.method_1483().play(PositionedSoundInstance.method_4758(SoundEvents.field_14716, random.nextFloat() * 0.4F + 0.8F));
				break;
			case 1033:
				this.world.method_2947(blockPos, SoundEvents.field_14817, SoundCategory.field_15245, 1.0F, 1.0F, false);
				break;
			case 1034:
				this.world.method_2947(blockPos, SoundEvents.field_14739, SoundCategory.field_15245, 1.0F, 1.0F, false);
				break;
			case 1035:
				this.world.method_2947(blockPos, SoundEvents.field_14978, SoundCategory.field_15245, 1.0F, 1.0F, false);
				break;
			case 1036:
				this.world.method_2947(blockPos, SoundEvents.field_15131, SoundCategory.field_15245, 1.0F, this.world.random.nextFloat() * 0.1F + 0.9F, false);
				break;
			case 1037:
				this.world.method_2947(blockPos, SoundEvents.field_15082, SoundCategory.field_15245, 1.0F, this.world.random.nextFloat() * 0.1F + 0.9F, false);
				break;
			case 1039:
				this.world.method_2947(blockPos, SoundEvents.field_14729, SoundCategory.field_15251, 0.3F, this.world.random.nextFloat() * 0.1F + 0.9F, false);
				break;
			case 1040:
				this.world.method_2947(blockPos, SoundEvents.field_14850, SoundCategory.field_15254, 2.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
				break;
			case 1041:
				this.world.method_2947(blockPos, SoundEvents.field_15128, SoundCategory.field_15254, 2.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
				break;
			case 1042:
				this.world.method_2947(blockPos, SoundEvents.field_16865, SoundCategory.field_15245, 1.0F, this.world.random.nextFloat() * 0.1F + 0.9F, false);
				break;
			case 1043:
				this.world.method_2947(blockPos, SoundEvents.field_17481, SoundCategory.field_15245, 1.0F, this.world.random.nextFloat() * 0.1F + 0.9F, false);
				break;
			case 1500:
				ComposterBlock.method_18027(this.world, blockPos, j > 0);
				break;
			case 2000:
				Direction direction = Direction.byId(j);
				int k = direction.getOffsetX();
				int l = direction.getOffsetY();
				int m = direction.getOffsetZ();
				double d = (double)blockPos.getX() + (double)k * 0.6 + 0.5;
				double e = (double)blockPos.getY() + (double)l * 0.6 + 0.5;
				double f = (double)blockPos.getZ() + (double)m * 0.6 + 0.5;

				for (int nx = 0; nx < 10; nx++) {
					double g = random.nextDouble() * 0.2 + 0.01;
					double h = d + (double)k * 0.01 + (random.nextDouble() - 0.5) * (double)m * 0.5;
					double o = e + (double)l * 0.01 + (random.nextDouble() - 0.5) * (double)l * 0.5;
					double p = f + (double)m * 0.01 + (random.nextDouble() - 0.5) * (double)k * 0.5;
					double q = (double)k * g + random.nextGaussian() * 0.01;
					double r = (double)l * g + random.nextGaussian() * 0.01;
					double s = (double)m * g + random.nextGaussian() * 0.01;
					this.method_3276(ParticleTypes.field_11251, h, o, p, q, r, s);
				}
				break;
			case 2001:
				BlockState blockState = Block.method_9531(j);
				if (!blockState.isAir()) {
					BlockSoundGroup blockSoundGroup = blockState.getSoundGroup();
					this.world
						.method_2947(
							blockPos,
							blockSoundGroup.method_10595(),
							SoundCategory.field_15245,
							(blockSoundGroup.getVolume() + 1.0F) / 2.0F,
							blockSoundGroup.getPitch() * 0.8F,
							false
						);
				}

				this.client.field_1713.method_3046(blockPos, blockState);
				break;
			case 2002:
			case 2007:
				double t = (double)blockPos.getX();
				double u = (double)blockPos.getY();
				double d = (double)blockPos.getZ();

				for (int v = 0; v < 8; v++) {
					this.method_3276(
						new ItemStackParticleParameters(ParticleTypes.field_11218, new ItemStack(Items.field_8436)),
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
				ParticleParameters particleParameters = i == 2007 ? ParticleTypes.field_11213 : ParticleTypes.field_11245;

				for (int n = 0; n < 100; n++) {
					double g = random.nextDouble() * 4.0;
					double h = random.nextDouble() * Math.PI * 2.0;
					double o = Math.cos(h) * g;
					double p = 0.01 + random.nextDouble() * 0.5;
					double q = Math.sin(h) * g;
					Particle particle = this.method_3282(particleParameters, particleParameters.method_10295().shouldAlwaysSpawn(), t + o * 0.1, u + 0.3, d + q * 0.1, o, p, q);
					if (particle != null) {
						float z = 0.75F + random.nextFloat() * 0.25F;
						particle.setColor(w * z, x * z, y * z);
						particle.move((float)g);
					}
				}

				this.world.method_2947(blockPos, SoundEvents.field_14839, SoundCategory.field_15254, 1.0F, this.world.random.nextFloat() * 0.1F + 0.9F, false);
				break;
			case 2003:
				double t = (double)blockPos.getX() + 0.5;
				double u = (double)blockPos.getY();
				double d = (double)blockPos.getZ() + 0.5;

				for (int v = 0; v < 8; v++) {
					this.method_3276(
						new ItemStackParticleParameters(ParticleTypes.field_11218, new ItemStack(Items.field_8449)),
						t,
						u,
						d,
						random.nextGaussian() * 0.15,
						random.nextDouble() * 0.2,
						random.nextGaussian() * 0.15
					);
				}

				for (double e = 0.0; e < Math.PI * 2; e += Math.PI / 20) {
					this.method_3276(ParticleTypes.field_11214, t + Math.cos(e) * 5.0, u - 0.4, d + Math.sin(e) * 5.0, Math.cos(e) * -5.0, 0.0, Math.sin(e) * -5.0);
					this.method_3276(ParticleTypes.field_11214, t + Math.cos(e) * 5.0, u - 0.4, d + Math.sin(e) * 5.0, Math.cos(e) * -7.0, 0.0, Math.sin(e) * -7.0);
				}
				break;
			case 2004:
				for (int aax = 0; aax < 20; aax++) {
					double ab = (double)blockPos.getX() + 0.5 + ((double)this.world.random.nextFloat() - 0.5) * 2.0;
					double ac = (double)blockPos.getY() + 0.5 + ((double)this.world.random.nextFloat() - 0.5) * 2.0;
					double ad = (double)blockPos.getZ() + 0.5 + ((double)this.world.random.nextFloat() - 0.5) * 2.0;
					this.world.method_8406(ParticleTypes.field_11251, ab, ac, ad, 0.0, 0.0, 0.0);
					this.world.method_8406(ParticleTypes.field_11240, ab, ac, ad, 0.0, 0.0, 0.0);
				}
				break;
			case 2005:
				BoneMealItem.method_7721(this.world, blockPos, j);
				break;
			case 2006:
				for (int aa = 0; aa < 200; aa++) {
					float ae = random.nextFloat() * 4.0F;
					float af = random.nextFloat() * (float) (Math.PI * 2);
					double ac = (double)(MathHelper.cos(af) * ae);
					double ad = 0.01 + random.nextDouble() * 0.5;
					double ag = (double)(MathHelper.sin(af) * ae);
					Particle particle2 = this.method_3282(
						ParticleTypes.field_11216, false, (double)blockPos.getX() + ac * 0.1, (double)blockPos.getY() + 0.3, (double)blockPos.getZ() + ag * 0.1, ac, ad, ag
					);
					if (particle2 != null) {
						particle2.move(ae);
					}
				}

				this.world.method_2947(blockPos, SoundEvents.field_14803, SoundCategory.field_15251, 1.0F, this.world.random.nextFloat() * 0.1F + 0.9F, false);
				break;
			case 3000:
				this.world
					.method_8466(ParticleTypes.field_11221, true, (double)blockPos.getX() + 0.5, (double)blockPos.getY() + 0.5, (double)blockPos.getZ() + 0.5, 0.0, 0.0, 0.0);
				this.world
					.method_2947(
						blockPos,
						SoundEvents.field_14816,
						SoundCategory.field_15245,
						10.0F,
						(1.0F + (this.world.random.nextFloat() - this.world.random.nextFloat()) * 0.2F) * 0.7F,
						false
					);
				break;
			case 3001:
				this.world.method_2947(blockPos, SoundEvents.field_14671, SoundCategory.field_15251, 64.0F, 0.8F + this.world.random.nextFloat() * 0.3F, false);
		}
	}

	public void method_8569(int i, BlockPos blockPos, int j) {
		if (j >= 0 && j < 10) {
			PartiallyBrokenBlockEntry partiallyBrokenBlockEntry = (PartiallyBrokenBlockEntry)this.partiallyBrokenBlocks.get(i);
			if (partiallyBrokenBlockEntry == null
				|| partiallyBrokenBlockEntry.getPos().getX() != blockPos.getX()
				|| partiallyBrokenBlockEntry.getPos().getY() != blockPos.getY()
				|| partiallyBrokenBlockEntry.getPos().getZ() != blockPos.getZ()) {
				partiallyBrokenBlockEntry = new PartiallyBrokenBlockEntry(i, blockPos);
				this.partiallyBrokenBlocks.put(i, partiallyBrokenBlockEntry);
			}

			partiallyBrokenBlockEntry.setStage(j);
			partiallyBrokenBlockEntry.setLastUpdateTicks(this.ticks);
		} else {
			this.partiallyBrokenBlocks.remove(i);
		}
	}

	public boolean method_3281() {
		return this.chunkRenderers.isEmpty() && this.field_4106.method_3630();
	}

	public void method_3292() {
		this.terrainUpdateNecessary = true;
		this.cloudsDirty = true;
	}

	public void method_3245(Collection<BlockEntity> collection, Collection<BlockEntity> collection2) {
		synchronized (this.field_4055) {
			this.field_4055.removeAll(collection);
			this.field_4055.addAll(collection2);
		}
	}

	@Environment(EnvType.CLIENT)
	class class_762 {
		private final ChunkRenderer field_4124;
		private final Direction field_4125;
		private byte field_4126;
		private final int field_4122;

		private class_762(ChunkRenderer chunkRenderer, @Nullable Direction direction, int i) {
			this.field_4124 = chunkRenderer;
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
