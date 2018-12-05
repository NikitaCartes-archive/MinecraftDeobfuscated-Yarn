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
import net.minecraft.class_279;
import net.minecraft.class_292;
import net.minecraft.class_308;
import net.minecraft.class_3509;
import net.minecraft.class_752;
import net.minecraft.class_767;
import net.minecraft.class_848;
import net.minecraft.class_852;
import net.minecraft.class_856;
import net.minecraft.class_857;
import net.minecraft.class_858;
import net.minecraft.block.AbstractSkullBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
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
import net.minecraft.client.render.block.BlockRenderLayer;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.chunk.ChunkBatcher;
import net.minecraft.client.render.chunk.ChunkRenderData;
import net.minecraft.client.render.chunk.ChunkRenderDispatcher;
import net.minecraft.client.render.chunk.ChunkRenderer;
import net.minecraft.client.render.chunk.ChunkRendererFactory;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.GlAllocationUtils;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BoneMealItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.RecordItem;
import net.minecraft.particle.ItemStackParticle;
import net.minecraft.particle.Particle;
import net.minecraft.particle.ParticleType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloadListener;
import net.minecraft.sortme.Vector3f;
import net.minecraft.sortme.Vector4f;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.HitResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportElement;
import net.minecraft.util.crash.ICrashCallable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldListener;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.dimension.DimensionType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class Renderer implements WorldListener, AutoCloseable, ResourceReloadListener {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Identifier MOON_PHASES_TEX = new Identifier("textures/environment/moon_phases.png");
	private static final Identifier SUN_TEX = new Identifier("textures/environment/sun.png");
	private static final Identifier CLOUDS_TEX = new Identifier("textures/environment/clouds.png");
	private static final Identifier END_SKY_TEX = new Identifier("textures/environment/end_sky.png");
	private static final Identifier FORCEFIELD_TEX = new Identifier("textures/misc/forcefield.png");
	public static final Direction[] field_4095 = Direction.values();
	private final MinecraftClient client;
	private final TextureManager textureManager;
	private final EntityRenderDispatcher entityRenderDispatcher;
	private ClientWorld world;
	private Set<ChunkRenderer> field_4075 = Sets.<ChunkRenderer>newLinkedHashSet();
	private List<Renderer.class_762> field_4086 = Lists.<Renderer.class_762>newArrayListWithCapacity(69696);
	private final Set<BlockEntity> field_4055 = Sets.<BlockEntity>newHashSet();
	private ChunkRenderDispatcher chunkRenderDispatcher;
	private int field_4099 = -1;
	private int field_4117 = -1;
	private int field_4067 = -1;
	private final VertexFormat field_4100;
	private GlBuffer field_4113;
	private GlBuffer field_4087;
	private GlBuffer field_4102;
	private final int field_4079 = 28;
	private boolean field_4107 = true;
	private int field_4114 = -1;
	private GlBuffer field_4094;
	private int ticks;
	private final Map<Integer, PartiallyBrokenBlockEntry> partiallyBrokenBlocks = Maps.<Integer, PartiallyBrokenBlockEntry>newHashMap();
	private final Map<BlockPos, SoundInstance> field_4119 = Maps.<BlockPos, SoundInstance>newHashMap();
	private final Sprite[] destroyStages = new Sprite[10];
	private GlFramebuffer framebuffer;
	private class_279 field_4059;
	private double field_4104 = Double.MIN_VALUE;
	private double field_4120 = Double.MIN_VALUE;
	private double field_4070 = Double.MIN_VALUE;
	private int field_4084 = Integer.MIN_VALUE;
	private int field_4105 = Integer.MIN_VALUE;
	private int field_4121 = Integer.MIN_VALUE;
	private double field_4069 = Double.MIN_VALUE;
	private double field_4081 = Double.MIN_VALUE;
	private double field_4096 = Double.MIN_VALUE;
	private double field_4115 = Double.MIN_VALUE;
	private double field_4064 = Double.MIN_VALUE;
	private int field_4082 = Integer.MIN_VALUE;
	private int field_4097 = Integer.MIN_VALUE;
	private int field_4116 = Integer.MIN_VALUE;
	private net.minecraft.util.math.Vec3d field_4072 = net.minecraft.util.math.Vec3d.ZERO;
	private int field_4080 = -1;
	private ChunkBatcher chunkBatcher;
	private class_752 field_4092;
	private int renderDistance = -1;
	private int field_4076 = 2;
	private int totalEntities;
	private int renderedEntities;
	private int field_4110;
	private boolean field_4066;
	private class_857 field_4056;
	private final Vector4f[] field_4065 = new Vector4f[8];
	private final Vec3d field_4091 = new Vec3d();
	private boolean vertexBufferObjectsEnabled;
	private ChunkRendererFactory chunkRendererFactory;
	private double field_4083;
	private double field_4103;
	private double field_4118;
	private boolean field_4077 = true;
	private boolean field_4090;

	public Renderer(MinecraftClient minecraftClient) {
		this.client = minecraftClient;
		this.entityRenderDispatcher = minecraftClient.getEntityRenderManager();
		this.textureManager = minecraftClient.getTextureManager();
		this.textureManager.bindTexture(FORCEFIELD_TEX);
		GlStateManager.texParameter(3553, 10242, 10497);
		GlStateManager.texParameter(3553, 10243, 10497);
		GlStateManager.bindTexture(0);
		this.loadDestroyStageTextures();
		this.vertexBufferObjectsEnabled = GLX.useVbo();
		if (this.vertexBufferObjectsEnabled) {
			this.field_4092 = new class_292();
			this.chunkRendererFactory = ChunkRenderer::new;
		} else {
			this.field_4092 = new class_767();
			this.chunkRendererFactory = class_848::new;
		}

		this.field_4100 = new VertexFormat();
		this.field_4100.add(new VertexFormatElement(0, VertexFormatElement.Format.FLOAT, VertexFormatElement.Type.POSITION, 3));
		this.method_3293();
		this.method_3277();
		this.method_3265();
	}

	public void close() {
		if (this.field_4059 != null) {
			this.field_4059.close();
		}
	}

	@Override
	public void onResourceReload(ResourceManager resourceManager) {
		this.loadDestroyStageTextures();
	}

	private void loadDestroyStageTextures() {
		SpriteAtlasTexture spriteAtlasTexture = this.client.getSpriteAtlas();
		this.destroyStages[0] = spriteAtlasTexture.getSprite(ModelLoader.field_5377);
		this.destroyStages[1] = spriteAtlasTexture.getSprite(ModelLoader.field_5385);
		this.destroyStages[2] = spriteAtlasTexture.getSprite(ModelLoader.field_5375);
		this.destroyStages[3] = spriteAtlasTexture.getSprite(ModelLoader.field_5403);
		this.destroyStages[4] = spriteAtlasTexture.getSprite(ModelLoader.field_5393);
		this.destroyStages[5] = spriteAtlasTexture.getSprite(ModelLoader.field_5386);
		this.destroyStages[6] = spriteAtlasTexture.getSprite(ModelLoader.field_5369);
		this.destroyStages[7] = spriteAtlasTexture.getSprite(ModelLoader.field_5401);
		this.destroyStages[8] = spriteAtlasTexture.getSprite(ModelLoader.field_5392);
		this.destroyStages[9] = spriteAtlasTexture.getSprite(ModelLoader.field_5382);
	}

	public void method_3296() {
		if (GLX.usePostProcess) {
			if (GlProgramManager.getInstance() == null) {
				GlProgramManager.init();
			}

			Identifier identifier = new Identifier("shaders/post/entity_outline.json");

			try {
				this.field_4059 = new class_279(this.client.getTextureManager(), this.client.getResourceManager(), this.client.getFramebuffer(), identifier);
				this.field_4059.method_1259(this.client.window.getWindowWidth(), this.client.window.getWindowHeight());
				this.framebuffer = this.field_4059.method_1264("final");
			} catch (IOException var3) {
				LOGGER.warn("Failed to load shader: {}", identifier, var3);
				this.field_4059 = null;
				this.framebuffer = null;
			} catch (JsonSyntaxException var4) {
				LOGGER.warn("Failed to load shader: {}", identifier, var4);
				this.field_4059 = null;
				this.framebuffer = null;
			}
		} else {
			this.field_4059 = null;
			this.framebuffer = null;
		}
	}

	public void drawFramebuffer() {
		if (this.method_3270()) {
			GlStateManager.enableBlend();
			GlStateManager.blendFuncSeparate(
				GlStateManager.SrcBlendFactor.SRC_ALPHA,
				GlStateManager.DstBlendFactor.ONE_MINUS_SRC_ALPHA,
				GlStateManager.SrcBlendFactor.ZERO,
				GlStateManager.DstBlendFactor.ONE
			);
			this.framebuffer.draw(this.client.window.getWindowWidth(), this.client.window.getWindowHeight(), false);
			GlStateManager.disableBlend();
		}
	}

	protected boolean method_3270() {
		return this.framebuffer != null && this.field_4059 != null && this.client.player != null;
	}

	private void method_3265() {
		Tessellator tessellator = Tessellator.getInstance();
		VertexBuffer vertexBuffer = tessellator.getVertexBuffer();
		if (this.field_4102 != null) {
			this.field_4102.delete();
		}

		if (this.field_4067 >= 0) {
			GlAllocationUtils.deleteSingletonList(this.field_4067);
			this.field_4067 = -1;
		}

		if (this.vertexBufferObjectsEnabled) {
			this.field_4102 = new GlBuffer(this.field_4100);
			this.method_3283(vertexBuffer, -16.0F, true);
			vertexBuffer.end();
			vertexBuffer.clear();
			this.field_4102.set(vertexBuffer.getByteBuffer());
		} else {
			this.field_4067 = GlAllocationUtils.genLists(1);
			GlStateManager.newList(this.field_4067, 4864);
			this.method_3283(vertexBuffer, -16.0F, true);
			tessellator.draw();
			GlStateManager.endList();
		}
	}

	private void method_3277() {
		Tessellator tessellator = Tessellator.getInstance();
		VertexBuffer vertexBuffer = tessellator.getVertexBuffer();
		if (this.field_4087 != null) {
			this.field_4087.delete();
		}

		if (this.field_4117 >= 0) {
			GlAllocationUtils.deleteSingletonList(this.field_4117);
			this.field_4117 = -1;
		}

		if (this.vertexBufferObjectsEnabled) {
			this.field_4087 = new GlBuffer(this.field_4100);
			this.method_3283(vertexBuffer, 16.0F, false);
			vertexBuffer.end();
			vertexBuffer.clear();
			this.field_4087.set(vertexBuffer.getByteBuffer());
		} else {
			this.field_4117 = GlAllocationUtils.genLists(1);
			GlStateManager.newList(this.field_4117, 4864);
			this.method_3283(vertexBuffer, 16.0F, false);
			tessellator.draw();
			GlStateManager.endList();
		}
	}

	private void method_3283(VertexBuffer vertexBuffer, float f, boolean bl) {
		int i = 64;
		int j = 6;
		vertexBuffer.begin(7, VertexFormats.POSITION);

		for (int k = -384; k <= 384; k += 64) {
			for (int l = -384; l <= 384; l += 64) {
				float g = (float)k;
				float h = (float)(k + 64);
				if (bl) {
					h = (float)k;
					g = (float)(k + 64);
				}

				vertexBuffer.vertex((double)g, (double)f, (double)l).next();
				vertexBuffer.vertex((double)h, (double)f, (double)l).next();
				vertexBuffer.vertex((double)h, (double)f, (double)(l + 64)).next();
				vertexBuffer.vertex((double)g, (double)f, (double)(l + 64)).next();
			}
		}
	}

	private void method_3293() {
		Tessellator tessellator = Tessellator.getInstance();
		VertexBuffer vertexBuffer = tessellator.getVertexBuffer();
		if (this.field_4113 != null) {
			this.field_4113.delete();
		}

		if (this.field_4099 >= 0) {
			GlAllocationUtils.deleteSingletonList(this.field_4099);
			this.field_4099 = -1;
		}

		if (this.vertexBufferObjectsEnabled) {
			this.field_4113 = new GlBuffer(this.field_4100);
			this.method_3255(vertexBuffer);
			vertexBuffer.end();
			vertexBuffer.clear();
			this.field_4113.set(vertexBuffer.getByteBuffer());
		} else {
			this.field_4099 = GlAllocationUtils.genLists(1);
			GlStateManager.pushMatrix();
			GlStateManager.newList(this.field_4099, 4864);
			this.method_3255(vertexBuffer);
			tessellator.draw();
			GlStateManager.endList();
			GlStateManager.popMatrix();
		}
	}

	private void method_3255(VertexBuffer vertexBuffer) {
		Random random = new Random(10842L);
		vertexBuffer.begin(7, VertexFormats.POSITION);

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
					vertexBuffer.vertex(j + af, k + ad, l + ah).next();
				}
			}
		}
	}

	public void setWorld(@Nullable ClientWorld clientWorld) {
		if (this.world != null) {
			this.world.unregisterListener(this);
		}

		this.field_4104 = Double.MIN_VALUE;
		this.field_4120 = Double.MIN_VALUE;
		this.field_4070 = Double.MIN_VALUE;
		this.field_4084 = Integer.MIN_VALUE;
		this.field_4105 = Integer.MIN_VALUE;
		this.field_4121 = Integer.MIN_VALUE;
		this.entityRenderDispatcher.method_3944(clientWorld);
		this.world = clientWorld;
		if (clientWorld != null) {
			clientWorld.registerListener(this);
			this.reload();
		} else {
			this.field_4075.clear();
			this.field_4086.clear();
			if (this.chunkRenderDispatcher != null) {
				this.chunkRenderDispatcher.method_3327();
				this.chunkRenderDispatcher = null;
			}

			if (this.chunkBatcher != null) {
				this.chunkBatcher.method_3619();
			}

			this.chunkBatcher = null;
		}
	}

	public void reload() {
		if (this.world != null) {
			if (this.chunkBatcher == null) {
				this.chunkBatcher = new ChunkBatcher();
			}

			this.field_4077 = true;
			this.field_4107 = true;
			LeavesBlock.setRenderingMode(this.client.options.fancyGraphics);
			this.renderDistance = this.client.options.viewDistance;
			boolean bl = this.vertexBufferObjectsEnabled;
			this.vertexBufferObjectsEnabled = GLX.useVbo();
			if (bl && !this.vertexBufferObjectsEnabled) {
				this.field_4092 = new class_767();
				this.chunkRendererFactory = class_848::new;
			} else if (!bl && this.vertexBufferObjectsEnabled) {
				this.field_4092 = new class_292();
				this.chunkRendererFactory = ChunkRenderer::new;
			}

			if (bl != this.vertexBufferObjectsEnabled) {
				this.method_3293();
				this.method_3277();
				this.method_3265();
			}

			if (this.chunkRenderDispatcher != null) {
				this.chunkRenderDispatcher.method_3327();
			}

			this.method_3280();
			synchronized (this.field_4055) {
				this.field_4055.clear();
			}

			this.chunkRenderDispatcher = new ChunkRenderDispatcher(this.world, this.client.options.viewDistance, this, this.chunkRendererFactory);
			if (this.world != null) {
				Entity entity = this.client.getCameraEntity();
				if (entity != null) {
					this.chunkRenderDispatcher.method_3330(entity.x, entity.z);
				}
			}

			this.field_4076 = 2;
		}
	}

	protected void method_3280() {
		this.field_4075.clear();
		this.chunkBatcher.method_3632();
	}

	public void method_3242(int i, int j) {
		this.method_3292();
		if (GLX.usePostProcess) {
			if (this.field_4059 != null) {
				this.field_4059.method_1259(i, j);
			}
		}
	}

	public void renderEntities(Entity entity, class_856 arg, float f) {
		if (this.field_4076 > 0) {
			this.field_4076--;
		} else {
			double d = MathHelper.lerp((double)f, entity.prevX, entity.x);
			double e = MathHelper.lerp((double)f, entity.prevY, entity.y);
			double g = MathHelper.lerp((double)f, entity.prevZ, entity.z);
			this.world.getProfiler().begin("prepare");
			BlockEntityRenderDispatcher.INSTANCE
				.configure(this.world, this.client.getTextureManager(), this.client.fontRenderer, this.client.getCameraEntity(), this.client.hitResult, f);
			this.entityRenderDispatcher.method_3941(this.world, this.client.fontRenderer, this.client.getCameraEntity(), this.client.field_1692, this.client.options, f);
			this.totalEntities = 0;
			this.renderedEntities = 0;
			this.field_4110 = 0;
			Entity entity2 = this.client.getCameraEntity();
			double h = MathHelper.lerp((double)f, entity2.prevRenderX, entity2.x);
			double i = MathHelper.lerp((double)f, entity2.prevRenderY, entity2.y);
			double j = MathHelper.lerp((double)f, entity2.prevRenderZ, entity2.z);
			BlockEntityRenderDispatcher.renderOffsetX = h;
			BlockEntityRenderDispatcher.renderOffsetY = i;
			BlockEntityRenderDispatcher.renderOffsetZ = j;
			this.entityRenderDispatcher.setRenderPosition(h, i, j);
			this.client.worldRenderer.method_3180();
			this.world.getProfiler().endBegin("global");
			this.totalEntities = this.world.getEntityCount();

			for (int k = 0; k < this.world.globalEntities.size(); k++) {
				Entity entity3 = (Entity)this.world.globalEntities.get(k);
				this.renderedEntities++;
				if (entity3.shouldRenderFrom(d, e, g)) {
					this.entityRenderDispatcher.method_3946(entity3, f, false);
				}
			}

			this.world.getProfiler().endBegin("entities");
			List<Entity> list = Lists.<Entity>newArrayList();
			List<Entity> list2 = Lists.<Entity>newArrayList();

			try (BlockPos.PooledMutable pooledMutable = BlockPos.PooledMutable.get()) {
				for (Renderer.class_762 lv : this.field_4086) {
					WorldChunk worldChunk = this.world.getChunk(lv.field_4124.method_3670());
					class_3509<Entity> lv2 = worldChunk.getEntitySectionArray()[lv.field_4124.method_3670().getY() / 16];
					if (!lv2.isEmpty()) {
						for (Entity entity4 : lv2) {
							boolean bl = this.entityRenderDispatcher.method_3950(entity4, arg, d, e, g) || entity4.method_5821(this.client.player);
							if (bl) {
								boolean bl2 = this.client.getCameraEntity() instanceof LivingEntity && ((LivingEntity)this.client.getCameraEntity()).isSleeping();
								if ((entity4 != this.client.getCameraEntity() || this.client.options.field_1850 != 0 || bl2)
									&& (!(entity4.y >= 0.0) || !(entity4.y < 256.0) || this.world.isBlockLoaded(pooledMutable.set(entity4)))) {
									this.renderedEntities++;
									this.entityRenderDispatcher.method_3946(entity4, f, false);
									if (this.method_3284(entity4, entity2, arg)) {
										list.add(entity4);
									}

									if (this.entityRenderDispatcher.hasSecondPass(entity4)) {
										list2.add(entity4);
									}
								}
							}
						}
					}
				}
			}

			if (!list2.isEmpty()) {
				for (Entity entity5 : list2) {
					this.entityRenderDispatcher.method_3947(entity5, f);
				}
			}

			if (this.method_3270() && (!list.isEmpty() || this.field_4090)) {
				this.world.getProfiler().endBegin("entityOutlines");
				this.framebuffer.clear(MinecraftClient.isSystemMac);
				this.field_4090 = !list.isEmpty();
				if (!list.isEmpty()) {
					GlStateManager.depthFunc(519);
					GlStateManager.disableFog();
					this.framebuffer.beginWrite(false);
					class_308.method_1450();
					this.entityRenderDispatcher.method_3943(true);

					for (int l = 0; l < list.size(); l++) {
						this.entityRenderDispatcher.method_3946((Entity)list.get(l), f, false);
					}

					this.entityRenderDispatcher.method_3943(false);
					class_308.method_1452();
					GlStateManager.depthMask(false);
					this.field_4059.method_1258(f);
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

			this.world.getProfiler().endBegin("blockentities");
			class_308.method_1452();

			for (Renderer.class_762 lv3 : this.field_4086) {
				List<BlockEntity> list3 = lv3.field_4124.getChunkRenderData().getBlockEntities();
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
				BlockState blockState = this.world.getBlockState(blockPos);
				if (blockState.getBlock().hasBlockEntity()) {
					BlockEntity blockEntity = this.world.getBlockEntity(blockPos);
					if (blockEntity instanceof ChestBlockEntity && blockState.get(ChestBlock.field_10770) == ChestType.field_12574) {
						blockPos = blockPos.method_10093(((Direction)blockState.get(ChestBlock.field_10768)).rotateYClockwise());
						blockEntity = this.world.getBlockEntity(blockPos);
					}

					if (blockEntity != null && blockState.hasBlockEntityBreakingRender()) {
						BlockEntityRenderDispatcher.INSTANCE.render(blockEntity, f, partiallyBrokenBlockEntry.getStage());
					}
				}
			}

			this.method_3274();
			this.client.worldRenderer.method_3187();
			this.client.getProfiler().end();
		}
	}

	private boolean method_3284(Entity entity, Entity entity2, class_856 arg) {
		boolean bl = entity2 instanceof LivingEntity && ((LivingEntity)entity2).isSleeping();
		if (entity == entity2 && this.client.options.field_1850 == 0 && !bl) {
			return false;
		} else if (entity.isGlowing()) {
			return true;
		} else {
			return this.client.player.isSpectator() && this.client.options.keySpectatorOutlines.method_1434() && entity instanceof PlayerEntity
				? entity.field_5985 || arg.method_3699(entity.getBoundingBox()) || entity.method_5821(this.client.player)
				: false;
		}
	}

	public String method_3289() {
		int i = this.chunkRenderDispatcher.renderers.length;
		int j = this.getChunkNumber();
		return String.format(
			"C: %d/%d %sD: %d, %s",
			j,
			i,
			this.client.field_1730 ? "(s) " : "",
			this.renderDistance,
			this.chunkBatcher == null ? "null" : this.chunkBatcher.getDebugString()
		);
	}

	protected int getChunkNumber() {
		int i = 0;

		for (Renderer.class_762 lv : this.field_4086) {
			ChunkRenderData chunkRenderData = lv.field_4124.chunkRenderData;
			if (chunkRenderData != ChunkRenderData.EMPTY && !chunkRenderData.isEmpty()) {
				i++;
			}
		}

		return i;
	}

	public String getEntitiesDebugString() {
		return "E: " + this.renderedEntities + "/" + this.totalEntities + ", B: " + this.field_4110;
	}

	public void method_3273(Entity entity, float f, class_856 arg, int i, boolean bl) {
		if (this.client.options.viewDistance != this.renderDistance) {
			this.reload();
		}

		this.world.getProfiler().begin("camera");
		double d = entity.x - this.field_4104;
		double e = entity.y - this.field_4120;
		double g = entity.z - this.field_4070;
		if (this.field_4084 != entity.chunkX || this.field_4105 != entity.chunkY || this.field_4121 != entity.chunkZ || d * d + e * e + g * g > 16.0) {
			this.field_4104 = entity.x;
			this.field_4120 = entity.y;
			this.field_4070 = entity.z;
			this.field_4084 = entity.chunkX;
			this.field_4105 = entity.chunkY;
			this.field_4121 = entity.chunkZ;
			this.chunkRenderDispatcher.method_3330(entity.x, entity.z);
		}

		this.world.getProfiler().endBegin("renderlistcamera");
		double h = MathHelper.lerp((double)f, entity.prevRenderX, entity.x);
		double j = MathHelper.lerp((double)f, entity.prevRenderY, entity.y);
		double k = MathHelper.lerp((double)f, entity.prevRenderZ, entity.z);
		this.field_4092.method_3158(h, j, k);
		this.world.getProfiler().endBegin("cull");
		if (this.field_4056 != null) {
			class_858 lv = new class_858(this.field_4056);
			lv.method_3700(this.field_4091.x, this.field_4091.y, this.field_4091.z);
			arg = lv;
		}

		this.client.getProfiler().endBegin("culling");
		BlockPos blockPos = new BlockPos(h, j + (double)entity.getEyeHeight(), k);
		ChunkRenderer chunkRenderer = this.chunkRenderDispatcher.method_3323(blockPos);
		BlockPos blockPos2 = new BlockPos(MathHelper.floor(h / 16.0) * 16, MathHelper.floor(j / 16.0) * 16, MathHelper.floor(k / 16.0) * 16);
		float l = entity.getPitch(f);
		float m = entity.getYaw(f);
		this.field_4077 = this.field_4077
			|| !this.field_4075.isEmpty()
			|| entity.x != this.field_4069
			|| entity.y != this.field_4081
			|| entity.z != this.field_4096
			|| (double)l != this.field_4115
			|| (double)m != this.field_4064;
		this.field_4069 = entity.x;
		this.field_4081 = entity.y;
		this.field_4096 = entity.z;
		this.field_4115 = (double)l;
		this.field_4064 = (double)m;
		boolean bl2 = this.field_4056 != null;
		this.client.getProfiler().endBegin("update");
		if (!bl2 && this.field_4077) {
			this.field_4077 = false;
			this.field_4086 = Lists.<Renderer.class_762>newArrayList();
			Queue<Renderer.class_762> queue = Queues.<Renderer.class_762>newArrayDeque();
			Entity.setRenderDistanceMultiplier(MathHelper.clamp((double)this.client.options.viewDistance / 8.0, 1.0, 2.5));
			boolean bl3 = this.client.field_1730;
			if (chunkRenderer != null) {
				boolean bl4 = false;
				Renderer.class_762 lv2 = new Renderer.class_762(chunkRenderer, null, 0);
				Set<Direction> set = this.method_3285(blockPos);
				if (set.size() == 1) {
					Vector3f vector3f = this.method_3286(entity, (double)f);
					Direction direction = Direction.getFacing(vector3f.x(), vector3f.y(), vector3f.z()).getOpposite();
					set.remove(direction);
				}

				if (set.isEmpty()) {
					bl4 = true;
				}

				if (bl4 && !bl) {
					this.field_4086.add(lv2);
				} else {
					if (bl && this.world.getBlockState(blockPos).method_11598(this.world, blockPos)) {
						bl3 = false;
					}

					chunkRenderer.method_3671(i);
					queue.add(lv2);
				}
			} else {
				int n = blockPos.getY() > 0 ? 248 : 8;

				for (int o = -this.renderDistance; o <= this.renderDistance; o++) {
					for (int p = -this.renderDistance; p <= this.renderDistance; p++) {
						ChunkRenderer chunkRenderer2 = this.chunkRenderDispatcher.method_3323(new BlockPos((o << 4) + 8, n, (p << 4) + 8));
						if (chunkRenderer2 != null && arg.method_3699(chunkRenderer2.boundingBox)) {
							chunkRenderer2.method_3671(i);
							queue.add(new Renderer.class_762(chunkRenderer2, null, 0));
						}
					}
				}
			}

			this.client.getProfiler().begin("iteration");

			while (!queue.isEmpty()) {
				Renderer.class_762 lv3 = (Renderer.class_762)queue.poll();
				ChunkRenderer chunkRenderer3 = lv3.field_4124;
				Direction direction2 = lv3.field_4125;
				this.field_4086.add(lv3);

				for (Direction direction3 : field_4095) {
					ChunkRenderer chunkRenderer4 = this.method_3241(blockPos2, chunkRenderer3, direction3);
					if ((!bl3 || !lv3.method_3298(direction3.getOpposite()))
						&& (!bl3 || direction2 == null || chunkRenderer3.getChunkRenderData().method_3650(direction2.getOpposite(), direction3))
						&& chunkRenderer4 != null
						&& chunkRenderer4.method_3673()
						&& chunkRenderer4.method_3671(i)
						&& arg.method_3699(chunkRenderer4.boundingBox)) {
						Renderer.class_762 lv4 = new Renderer.class_762(chunkRenderer4, direction3, lv3.field_4122 + 1);
						lv4.method_3299(lv3.field_4126, direction3);
						queue.add(lv4);
					}
				}
			}

			this.client.getProfiler().end();
		}

		this.client.getProfiler().endBegin("captureFrustum");
		if (this.field_4066) {
			this.method_3275(h, j, k);
			this.field_4066 = false;
		}

		this.client.getProfiler().endBegin("rebuildNear");
		Set<ChunkRenderer> set2 = this.field_4075;
		this.field_4075 = Sets.<ChunkRenderer>newLinkedHashSet();

		for (Renderer.class_762 lv3 : this.field_4086) {
			ChunkRenderer chunkRenderer3 = lv3.field_4124;
			if (chunkRenderer3.method_3672() || set2.contains(chunkRenderer3)) {
				this.field_4077 = true;
				BlockPos blockPos3 = chunkRenderer3.method_3670().add(8, 8, 8);
				boolean bl5 = blockPos3.squaredDistanceTo(blockPos) < 768.0;
				if (!chunkRenderer3.method_3661() && !bl5) {
					this.field_4075.add(chunkRenderer3);
				} else {
					this.client.getProfiler().begin("build near");
					this.chunkBatcher.method_3627(chunkRenderer3);
					chunkRenderer3.method_3662();
					this.client.getProfiler().end();
				}
			}
		}

		this.field_4075.addAll(set2);
		this.client.getProfiler().end();
	}

	private Set<Direction> method_3285(BlockPos blockPos) {
		class_852 lv = new class_852();
		BlockPos blockPos2 = new BlockPos(blockPos.getX() >> 4 << 4, blockPos.getY() >> 4 << 4, blockPos.getZ() >> 4 << 4);
		WorldChunk worldChunk = this.world.getChunk(blockPos2);

		for (BlockPos.Mutable mutable : BlockPos.iterateBoxPositionsMutable(blockPos2, blockPos2.add(15, 15, 15))) {
			if (worldChunk.getBlockState(mutable).method_11598(this.world, mutable)) {
				lv.method_3682(mutable);
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
			return MathHelper.abs(blockPos.getZ() - blockPos2.getZ()) > this.renderDistance * 16 ? null : this.chunkRenderDispatcher.method_3323(blockPos2);
		}
	}

	private void method_3275(double d, double e, double f) {
	}

	protected Vector3f method_3286(Entity entity, double d) {
		float f = (float)MathHelper.lerp(d, (double)entity.prevPitch, (double)entity.pitch);
		float g = (float)MathHelper.lerp(d, (double)entity.prevYaw, (double)entity.yaw);
		if (MinecraftClient.getInstance().options.field_1850 == 2) {
			f += 180.0F;
		}

		float h = MathHelper.cos(-g * (float) (Math.PI / 180.0) - (float) Math.PI);
		float i = MathHelper.sin(-g * (float) (Math.PI / 180.0) - (float) Math.PI);
		float j = -MathHelper.cos(-f * (float) (Math.PI / 180.0));
		float k = MathHelper.sin(-f * (float) (Math.PI / 180.0));
		return new Vector3f(i * j, k, h * j);
	}

	public int method_3251(BlockRenderLayer blockRenderLayer, double d, Entity entity) {
		class_308.method_1450();
		if (blockRenderLayer == BlockRenderLayer.TRANSLUCENT) {
			this.client.getProfiler().begin("translucent_sort");
			double e = entity.x - this.field_4083;
			double f = entity.y - this.field_4103;
			double g = entity.z - this.field_4118;
			if (e * e + f * f + g * g > 1.0) {
				this.field_4083 = entity.x;
				this.field_4103 = entity.y;
				this.field_4118 = entity.z;
				int i = 0;

				for (Renderer.class_762 lv : this.field_4086) {
					if (lv.field_4124.chunkRenderData.method_3649(blockRenderLayer) && i++ < 15) {
						this.chunkBatcher.method_3620(lv.field_4124);
					}
				}
			}

			this.client.getProfiler().end();
		}

		this.client.getProfiler().begin("filterempty");
		int j = 0;
		boolean bl = blockRenderLayer == BlockRenderLayer.TRANSLUCENT;
		int k = bl ? this.field_4086.size() - 1 : 0;
		int l = bl ? -1 : this.field_4086.size();
		int m = bl ? -1 : 1;

		for (int n = k; n != l; n += m) {
			ChunkRenderer chunkRenderer = ((Renderer.class_762)this.field_4086.get(n)).field_4124;
			if (!chunkRenderer.getChunkRenderData().method_3641(blockRenderLayer)) {
				j++;
				this.field_4092.method_3159(chunkRenderer, blockRenderLayer);
			}
		}

		this.client.getProfiler().endBegin((Supplier<String>)(() -> "render_" + blockRenderLayer));
		this.method_3287(blockRenderLayer);
		this.client.getProfiler().end();
		return j;
	}

	private void method_3287(BlockRenderLayer blockRenderLayer) {
		this.client.worldRenderer.method_3180();
		if (GLX.useVbo()) {
			GlStateManager.enableClientState(32884);
			GLX.glClientActiveTexture(GLX.GL_TEXTURE0);
			GlStateManager.enableClientState(32888);
			GLX.glClientActiveTexture(GLX.GL_TEXTURE1);
			GlStateManager.enableClientState(32888);
			GLX.glClientActiveTexture(GLX.GL_TEXTURE0);
			GlStateManager.enableClientState(32886);
		}

		this.field_4092.method_3160(blockRenderLayer);
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

		this.client.worldRenderer.method_3187();
	}

	private void method_3290(Iterator<PartiallyBrokenBlockEntry> iterator) {
		while (iterator.hasNext()) {
			PartiallyBrokenBlockEntry partiallyBrokenBlockEntry = (PartiallyBrokenBlockEntry)iterator.next();
			int i = partiallyBrokenBlockEntry.method_13990();
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
			GlStateManager.SrcBlendFactor.SRC_ALPHA,
			GlStateManager.DstBlendFactor.ONE_MINUS_SRC_ALPHA,
			GlStateManager.SrcBlendFactor.ONE,
			GlStateManager.DstBlendFactor.ZERO
		);
		class_308.method_1450();
		GlStateManager.depthMask(false);
		this.textureManager.bindTexture(END_SKY_TEX);
		Tessellator tessellator = Tessellator.getInstance();
		VertexBuffer vertexBuffer = tessellator.getVertexBuffer();

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

			vertexBuffer.begin(7, VertexFormats.POSITION_UV_COLOR);
			vertexBuffer.vertex(-100.0, -100.0, -100.0).texture(0.0, 0.0).color(40, 40, 40, 255).next();
			vertexBuffer.vertex(-100.0, -100.0, 100.0).texture(0.0, 16.0).color(40, 40, 40, 255).next();
			vertexBuffer.vertex(100.0, -100.0, 100.0).texture(16.0, 16.0).color(40, 40, 40, 255).next();
			vertexBuffer.vertex(100.0, -100.0, -100.0).texture(16.0, 0.0).color(40, 40, 40, 255).next();
			tessellator.draw();
			GlStateManager.popMatrix();
		}

		GlStateManager.depthMask(true);
		GlStateManager.enableTexture();
		GlStateManager.enableAlphaTest();
	}

	public void renderSky(float f) {
		if (this.client.world.dimension.getType() == DimensionType.field_13078) {
			this.renderEndSky();
		} else if (this.client.world.dimension.hasVisibleSky()) {
			GlStateManager.disableTexture();
			net.minecraft.util.math.Vec3d vec3d = this.world.method_8548(this.client.getCameraEntity(), f);
			float g = (float)vec3d.x;
			float h = (float)vec3d.y;
			float i = (float)vec3d.z;
			GlStateManager.color3f(g, h, i);
			Tessellator tessellator = Tessellator.getInstance();
			VertexBuffer vertexBuffer = tessellator.getVertexBuffer();
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
				GlStateManager.SrcBlendFactor.SRC_ALPHA,
				GlStateManager.DstBlendFactor.ONE_MINUS_SRC_ALPHA,
				GlStateManager.SrcBlendFactor.ONE,
				GlStateManager.DstBlendFactor.ZERO
			);
			class_308.method_1450();
			float[] fs = this.world.dimension.method_12446(this.world.method_8400(f), f);
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
				vertexBuffer.begin(6, VertexFormats.POSITION_COLOR);
				vertexBuffer.vertex(0.0, 100.0, 0.0).color(j, k, l, fs[3]).next();
				int m = 16;

				for (int n = 0; n <= 16; n++) {
					float o = (float)n * (float) (Math.PI * 2) / 16.0F;
					float p = MathHelper.sin(o);
					float q = MathHelper.cos(o);
					vertexBuffer.vertex((double)(p * 120.0F), (double)(q * 120.0F), (double)(-q * 40.0F * fs[3])).color(fs[0], fs[1], fs[2], 0.0F).next();
				}

				tessellator.draw();
				GlStateManager.popMatrix();
				GlStateManager.shadeModel(7424);
			}

			GlStateManager.enableTexture();
			GlStateManager.blendFuncSeparate(
				GlStateManager.SrcBlendFactor.SRC_ALPHA, GlStateManager.DstBlendFactor.ONE, GlStateManager.SrcBlendFactor.ONE, GlStateManager.DstBlendFactor.ZERO
			);
			GlStateManager.pushMatrix();
			float j = 1.0F - this.world.getRainGradient(f);
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, j);
			GlStateManager.rotatef(-90.0F, 0.0F, 1.0F, 0.0F);
			GlStateManager.rotatef(this.world.method_8400(f) * 360.0F, 1.0F, 0.0F, 0.0F);
			float k = 30.0F;
			this.textureManager.bindTexture(SUN_TEX);
			vertexBuffer.begin(7, VertexFormats.POSITION_UV);
			vertexBuffer.vertex((double)(-k), 100.0, (double)(-k)).texture(0.0, 0.0).next();
			vertexBuffer.vertex((double)k, 100.0, (double)(-k)).texture(1.0, 0.0).next();
			vertexBuffer.vertex((double)k, 100.0, (double)k).texture(1.0, 1.0).next();
			vertexBuffer.vertex((double)(-k), 100.0, (double)k).texture(0.0, 1.0).next();
			tessellator.draw();
			k = 20.0F;
			this.textureManager.bindTexture(MOON_PHASES_TEX);
			int r = this.world.method_8394();
			int m = r % 4;
			int n = r / 4 % 2;
			float o = (float)(m + 0) / 4.0F;
			float p = (float)(n + 0) / 2.0F;
			float q = (float)(m + 1) / 4.0F;
			float s = (float)(n + 1) / 2.0F;
			vertexBuffer.begin(7, VertexFormats.POSITION_UV);
			vertexBuffer.vertex((double)(-k), -100.0, (double)k).texture((double)q, (double)s).next();
			vertexBuffer.vertex((double)k, -100.0, (double)k).texture((double)o, (double)s).next();
			vertexBuffer.vertex((double)k, -100.0, (double)(-k)).texture((double)o, (double)p).next();
			vertexBuffer.vertex((double)(-k), -100.0, (double)(-k)).texture((double)q, (double)p).next();
			tessellator.draw();
			GlStateManager.disableTexture();
			float t = this.world.method_8550(f) * j;
			if (t > 0.0F) {
				GlStateManager.color4f(t, t, t, t);
				if (this.vertexBufferObjectsEnabled) {
					this.field_4113.bind();
					GlStateManager.enableClientState(32884);
					GlStateManager.vertexPointer(3, 5126, 12, 0);
					this.field_4113.draw(7);
					GlBuffer.unbind();
					GlStateManager.disableClientState(32884);
				} else {
					GlStateManager.callList(this.field_4099);
				}
			}

			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			GlStateManager.disableBlend();
			GlStateManager.enableAlphaTest();
			GlStateManager.enableFog();
			GlStateManager.popMatrix();
			GlStateManager.disableTexture();
			GlStateManager.color3f(0.0F, 0.0F, 0.0F);
			double d = this.client.player.getCameraPosVec(f).y - this.world.method_8540();
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

			if (this.world.dimension.method_12449()) {
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

	public void method_3259(float f, double d, double e, double g) {
		if (this.client.world.dimension.hasVisibleSky()) {
			float h = 12.0F;
			float i = 4.0F;
			double j = 2.0E-4;
			double k = (double)(((float)this.ticks + f) * 0.03F);
			double l = (d + k) / 12.0;
			double m = (double)(this.world.dimension.method_12455() - (float)e + 0.33F);
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
				|| this.client.options.getCloudRenderMode() != this.field_4080
				|| this.field_4072.squaredDistanceTo(vec3d) > 2.0E-4) {
				this.field_4082 = r;
				this.field_4097 = s;
				this.field_4116 = t;
				this.field_4072 = vec3d;
				this.field_4080 = this.client.options.getCloudRenderMode();
				this.field_4107 = true;
			}

			if (this.field_4107) {
				this.field_4107 = false;
				Tessellator tessellator = Tessellator.getInstance();
				VertexBuffer vertexBuffer = tessellator.getVertexBuffer();
				if (this.field_4094 != null) {
					this.field_4094.delete();
				}

				if (this.field_4114 >= 0) {
					GlAllocationUtils.deleteSingletonList(this.field_4114);
					this.field_4114 = -1;
				}

				if (this.vertexBufferObjectsEnabled) {
					this.field_4094 = new GlBuffer(VertexFormats.POSITION_UV_COLOR_NORMAL);
					this.method_3239(vertexBuffer, l, m, n, vec3d);
					vertexBuffer.end();
					vertexBuffer.clear();
					this.field_4094.set(vertexBuffer.getByteBuffer());
				} else {
					this.field_4114 = GlAllocationUtils.genLists(1);
					GlStateManager.newList(this.field_4114, 4864);
					this.method_3239(vertexBuffer, l, m, n, vec3d);
					tessellator.draw();
					GlStateManager.endList();
				}
			}

			GlStateManager.disableCull();
			this.textureManager.bindTexture(CLOUDS_TEX);
			GlStateManager.enableBlend();
			GlStateManager.blendFuncSeparate(
				GlStateManager.SrcBlendFactor.SRC_ALPHA,
				GlStateManager.DstBlendFactor.ONE_MINUS_SRC_ALPHA,
				GlStateManager.SrcBlendFactor.ONE,
				GlStateManager.DstBlendFactor.ZERO
			);
			GlStateManager.pushMatrix();
			GlStateManager.scalef(12.0F, 1.0F, 12.0F);
			GlStateManager.translatef(-o, p, -q);
			if (this.vertexBufferObjectsEnabled && this.field_4094 != null) {
				this.field_4094.bind();
				GlStateManager.enableClientState(32884);
				GlStateManager.enableClientState(32888);
				GLX.glClientActiveTexture(GLX.GL_TEXTURE0);
				GlStateManager.enableClientState(32886);
				GlStateManager.enableClientState(32885);
				GlStateManager.vertexPointer(3, 5126, 28, 0);
				GlStateManager.texCoordPointer(2, 5126, 28, 12);
				GlStateManager.colorPointer(4, 5121, 28, 20);
				GlStateManager.normalPointer(5120, 28, 24);
				int u = this.field_4080 == 2 ? 0 : 1;

				for (int v = u; v < 2; v++) {
					if (v == 0) {
						GlStateManager.colorMask(false, false, false, false);
					} else {
						GlStateManager.colorMask(true, true, true, true);
					}

					this.field_4094.draw(7);
				}

				GlBuffer.unbind();
				GlStateManager.disableClientState(32884);
				GlStateManager.disableClientState(32888);
				GlStateManager.disableClientState(32886);
				GlStateManager.disableClientState(32885);
			} else if (this.field_4114 >= 0) {
				int u = this.field_4080 == 2 ? 0 : 1;

				for (int v = u; v < 2; v++) {
					if (v == 0) {
						GlStateManager.colorMask(false, false, false, false);
					} else {
						GlStateManager.colorMask(true, true, true, true);
					}

					GlStateManager.callList(this.field_4114);
				}
			}

			GlStateManager.popMatrix();
			GlStateManager.clearCurrentColor();
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			GlStateManager.disableBlend();
			GlStateManager.enableCull();
		}
	}

	private void method_3239(VertexBuffer vertexBuffer, double d, double e, double f, net.minecraft.util.math.Vec3d vec3d) {
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
		vertexBuffer.begin(7, VertexFormats.POSITION_UV_COLOR_NORMAL);
		float z = (float)Math.floor(e / 4.0) * 4.0F;
		if (this.field_4080 == 2) {
			for (int aa = -3; aa <= 4; aa++) {
				for (int ab = -3; ab <= 4; ab++) {
					float ac = (float)(aa * 8);
					float ad = (float)(ab * 8);
					if (z > -5.0F) {
						vertexBuffer.vertex((double)(ac + 0.0F), (double)(z + 0.0F), (double)(ad + 8.0F))
							.texture((double)((ac + 0.0F) * 0.00390625F + l), (double)((ad + 8.0F) * 0.00390625F + m))
							.color(t, u, v, 0.8F)
							.normal(0.0F, -1.0F, 0.0F)
							.next();
						vertexBuffer.vertex((double)(ac + 8.0F), (double)(z + 0.0F), (double)(ad + 8.0F))
							.texture((double)((ac + 8.0F) * 0.00390625F + l), (double)((ad + 8.0F) * 0.00390625F + m))
							.color(t, u, v, 0.8F)
							.normal(0.0F, -1.0F, 0.0F)
							.next();
						vertexBuffer.vertex((double)(ac + 8.0F), (double)(z + 0.0F), (double)(ad + 0.0F))
							.texture((double)((ac + 8.0F) * 0.00390625F + l), (double)((ad + 0.0F) * 0.00390625F + m))
							.color(t, u, v, 0.8F)
							.normal(0.0F, -1.0F, 0.0F)
							.next();
						vertexBuffer.vertex((double)(ac + 0.0F), (double)(z + 0.0F), (double)(ad + 0.0F))
							.texture((double)((ac + 0.0F) * 0.00390625F + l), (double)((ad + 0.0F) * 0.00390625F + m))
							.color(t, u, v, 0.8F)
							.normal(0.0F, -1.0F, 0.0F)
							.next();
					}

					if (z <= 5.0F) {
						vertexBuffer.vertex((double)(ac + 0.0F), (double)(z + 4.0F - 9.765625E-4F), (double)(ad + 8.0F))
							.texture((double)((ac + 0.0F) * 0.00390625F + l), (double)((ad + 8.0F) * 0.00390625F + m))
							.color(n, o, p, 0.8F)
							.normal(0.0F, 1.0F, 0.0F)
							.next();
						vertexBuffer.vertex((double)(ac + 8.0F), (double)(z + 4.0F - 9.765625E-4F), (double)(ad + 8.0F))
							.texture((double)((ac + 8.0F) * 0.00390625F + l), (double)((ad + 8.0F) * 0.00390625F + m))
							.color(n, o, p, 0.8F)
							.normal(0.0F, 1.0F, 0.0F)
							.next();
						vertexBuffer.vertex((double)(ac + 8.0F), (double)(z + 4.0F - 9.765625E-4F), (double)(ad + 0.0F))
							.texture((double)((ac + 8.0F) * 0.00390625F + l), (double)((ad + 0.0F) * 0.00390625F + m))
							.color(n, o, p, 0.8F)
							.normal(0.0F, 1.0F, 0.0F)
							.next();
						vertexBuffer.vertex((double)(ac + 0.0F), (double)(z + 4.0F - 9.765625E-4F), (double)(ad + 0.0F))
							.texture((double)((ac + 0.0F) * 0.00390625F + l), (double)((ad + 0.0F) * 0.00390625F + m))
							.color(n, o, p, 0.8F)
							.normal(0.0F, 1.0F, 0.0F)
							.next();
					}

					if (aa > -1) {
						for (int ae = 0; ae < 8; ae++) {
							vertexBuffer.vertex((double)(ac + (float)ae + 0.0F), (double)(z + 0.0F), (double)(ad + 8.0F))
								.texture((double)((ac + (float)ae + 0.5F) * 0.00390625F + l), (double)((ad + 8.0F) * 0.00390625F + m))
								.color(q, r, s, 0.8F)
								.normal(-1.0F, 0.0F, 0.0F)
								.next();
							vertexBuffer.vertex((double)(ac + (float)ae + 0.0F), (double)(z + 4.0F), (double)(ad + 8.0F))
								.texture((double)((ac + (float)ae + 0.5F) * 0.00390625F + l), (double)((ad + 8.0F) * 0.00390625F + m))
								.color(q, r, s, 0.8F)
								.normal(-1.0F, 0.0F, 0.0F)
								.next();
							vertexBuffer.vertex((double)(ac + (float)ae + 0.0F), (double)(z + 4.0F), (double)(ad + 0.0F))
								.texture((double)((ac + (float)ae + 0.5F) * 0.00390625F + l), (double)((ad + 0.0F) * 0.00390625F + m))
								.color(q, r, s, 0.8F)
								.normal(-1.0F, 0.0F, 0.0F)
								.next();
							vertexBuffer.vertex((double)(ac + (float)ae + 0.0F), (double)(z + 0.0F), (double)(ad + 0.0F))
								.texture((double)((ac + (float)ae + 0.5F) * 0.00390625F + l), (double)((ad + 0.0F) * 0.00390625F + m))
								.color(q, r, s, 0.8F)
								.normal(-1.0F, 0.0F, 0.0F)
								.next();
						}
					}

					if (aa <= 1) {
						for (int ae = 0; ae < 8; ae++) {
							vertexBuffer.vertex((double)(ac + (float)ae + 1.0F - 9.765625E-4F), (double)(z + 0.0F), (double)(ad + 8.0F))
								.texture((double)((ac + (float)ae + 0.5F) * 0.00390625F + l), (double)((ad + 8.0F) * 0.00390625F + m))
								.color(q, r, s, 0.8F)
								.normal(1.0F, 0.0F, 0.0F)
								.next();
							vertexBuffer.vertex((double)(ac + (float)ae + 1.0F - 9.765625E-4F), (double)(z + 4.0F), (double)(ad + 8.0F))
								.texture((double)((ac + (float)ae + 0.5F) * 0.00390625F + l), (double)((ad + 8.0F) * 0.00390625F + m))
								.color(q, r, s, 0.8F)
								.normal(1.0F, 0.0F, 0.0F)
								.next();
							vertexBuffer.vertex((double)(ac + (float)ae + 1.0F - 9.765625E-4F), (double)(z + 4.0F), (double)(ad + 0.0F))
								.texture((double)((ac + (float)ae + 0.5F) * 0.00390625F + l), (double)((ad + 0.0F) * 0.00390625F + m))
								.color(q, r, s, 0.8F)
								.normal(1.0F, 0.0F, 0.0F)
								.next();
							vertexBuffer.vertex((double)(ac + (float)ae + 1.0F - 9.765625E-4F), (double)(z + 0.0F), (double)(ad + 0.0F))
								.texture((double)((ac + (float)ae + 0.5F) * 0.00390625F + l), (double)((ad + 0.0F) * 0.00390625F + m))
								.color(q, r, s, 0.8F)
								.normal(1.0F, 0.0F, 0.0F)
								.next();
						}
					}

					if (ab > -1) {
						for (int ae = 0; ae < 8; ae++) {
							vertexBuffer.vertex((double)(ac + 0.0F), (double)(z + 4.0F), (double)(ad + (float)ae + 0.0F))
								.texture((double)((ac + 0.0F) * 0.00390625F + l), (double)((ad + (float)ae + 0.5F) * 0.00390625F + m))
								.color(w, x, y, 0.8F)
								.normal(0.0F, 0.0F, -1.0F)
								.next();
							vertexBuffer.vertex((double)(ac + 8.0F), (double)(z + 4.0F), (double)(ad + (float)ae + 0.0F))
								.texture((double)((ac + 8.0F) * 0.00390625F + l), (double)((ad + (float)ae + 0.5F) * 0.00390625F + m))
								.color(w, x, y, 0.8F)
								.normal(0.0F, 0.0F, -1.0F)
								.next();
							vertexBuffer.vertex((double)(ac + 8.0F), (double)(z + 0.0F), (double)(ad + (float)ae + 0.0F))
								.texture((double)((ac + 8.0F) * 0.00390625F + l), (double)((ad + (float)ae + 0.5F) * 0.00390625F + m))
								.color(w, x, y, 0.8F)
								.normal(0.0F, 0.0F, -1.0F)
								.next();
							vertexBuffer.vertex((double)(ac + 0.0F), (double)(z + 0.0F), (double)(ad + (float)ae + 0.0F))
								.texture((double)((ac + 0.0F) * 0.00390625F + l), (double)((ad + (float)ae + 0.5F) * 0.00390625F + m))
								.color(w, x, y, 0.8F)
								.normal(0.0F, 0.0F, -1.0F)
								.next();
						}
					}

					if (ab <= 1) {
						for (int ae = 0; ae < 8; ae++) {
							vertexBuffer.vertex((double)(ac + 0.0F), (double)(z + 4.0F), (double)(ad + (float)ae + 1.0F - 9.765625E-4F))
								.texture((double)((ac + 0.0F) * 0.00390625F + l), (double)((ad + (float)ae + 0.5F) * 0.00390625F + m))
								.color(w, x, y, 0.8F)
								.normal(0.0F, 0.0F, 1.0F)
								.next();
							vertexBuffer.vertex((double)(ac + 8.0F), (double)(z + 4.0F), (double)(ad + (float)ae + 1.0F - 9.765625E-4F))
								.texture((double)((ac + 8.0F) * 0.00390625F + l), (double)((ad + (float)ae + 0.5F) * 0.00390625F + m))
								.color(w, x, y, 0.8F)
								.normal(0.0F, 0.0F, 1.0F)
								.next();
							vertexBuffer.vertex((double)(ac + 8.0F), (double)(z + 0.0F), (double)(ad + (float)ae + 1.0F - 9.765625E-4F))
								.texture((double)((ac + 8.0F) * 0.00390625F + l), (double)((ad + (float)ae + 0.5F) * 0.00390625F + m))
								.color(w, x, y, 0.8F)
								.normal(0.0F, 0.0F, 1.0F)
								.next();
							vertexBuffer.vertex((double)(ac + 0.0F), (double)(z + 0.0F), (double)(ad + (float)ae + 1.0F - 9.765625E-4F))
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
					vertexBuffer.vertex((double)(af + 0), (double)z, (double)(ag + 32))
						.texture((double)((float)(af + 0) * 0.00390625F + l), (double)((float)(ag + 32) * 0.00390625F + m))
						.color(n, o, p, 0.8F)
						.normal(0.0F, -1.0F, 0.0F)
						.next();
					vertexBuffer.vertex((double)(af + 32), (double)z, (double)(ag + 32))
						.texture((double)((float)(af + 32) * 0.00390625F + l), (double)((float)(ag + 32) * 0.00390625F + m))
						.color(n, o, p, 0.8F)
						.normal(0.0F, -1.0F, 0.0F)
						.next();
					vertexBuffer.vertex((double)(af + 32), (double)z, (double)(ag + 0))
						.texture((double)((float)(af + 32) * 0.00390625F + l), (double)((float)(ag + 0) * 0.00390625F + m))
						.color(n, o, p, 0.8F)
						.normal(0.0F, -1.0F, 0.0F)
						.next();
					vertexBuffer.vertex((double)(af + 0), (double)z, (double)(ag + 0))
						.texture((double)((float)(af + 0) * 0.00390625F + l), (double)((float)(ag + 0) * 0.00390625F + m))
						.color(n, o, p, 0.8F)
						.normal(0.0F, -1.0F, 0.0F)
						.next();
				}
			}
		}
	}

	public void method_3269(long l) {
		this.field_4077 = this.field_4077 | this.chunkBatcher.method_3631(l);
		if (!this.field_4075.isEmpty()) {
			Iterator<ChunkRenderer> iterator = this.field_4075.iterator();

			while (iterator.hasNext()) {
				ChunkRenderer chunkRenderer = (ChunkRenderer)iterator.next();
				boolean bl;
				if (chunkRenderer.method_3661()) {
					bl = this.chunkBatcher.method_3627(chunkRenderer);
				} else {
					bl = this.chunkBatcher.method_3624(chunkRenderer);
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

	public void renderWorldBorder(Entity entity, float f) {
		Tessellator tessellator = Tessellator.getInstance();
		VertexBuffer vertexBuffer = tessellator.getVertexBuffer();
		WorldBorder worldBorder = this.world.getWorldBorder();
		double d = (double)(this.client.options.viewDistance * 16);
		if (!(entity.x < worldBorder.getBoundEast() - d)
			|| !(entity.x > worldBorder.getBoundWest() + d)
			|| !(entity.z < worldBorder.getBoundSouth() - d)
			|| !(entity.z > worldBorder.getBoundNorth() + d)) {
			double e = 1.0 - worldBorder.contains(entity) / d;
			e = Math.pow(e, 4.0);
			double g = MathHelper.lerp((double)f, entity.prevRenderX, entity.x);
			double h = MathHelper.lerp((double)f, entity.prevRenderY, entity.y);
			double i = MathHelper.lerp((double)f, entity.prevRenderZ, entity.z);
			GlStateManager.enableBlend();
			GlStateManager.blendFuncSeparate(
				GlStateManager.SrcBlendFactor.SRC_ALPHA, GlStateManager.DstBlendFactor.ONE, GlStateManager.SrcBlendFactor.ONE, GlStateManager.DstBlendFactor.ZERO
			);
			this.textureManager.bindTexture(FORCEFIELD_TEX);
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
			float n = (float)(SystemUtil.getMeasuringTimeMili() % 3000L) / 3000.0F;
			float o = 0.0F;
			float p = 0.0F;
			float q = 128.0F;
			vertexBuffer.begin(7, VertexFormats.POSITION_UV);
			vertexBuffer.setOffset(-g, -h, -i);
			double r = Math.max((double)MathHelper.floor(i - d), worldBorder.getBoundNorth());
			double s = Math.min((double)MathHelper.ceil(i + d), worldBorder.getBoundSouth());
			if (g > worldBorder.getBoundEast() - d) {
				float t = 0.0F;

				for (double u = r; u < s; t += 0.5F) {
					double v = Math.min(1.0, s - u);
					float w = (float)v * 0.5F;
					vertexBuffer.vertex(worldBorder.getBoundEast(), 256.0, u).texture((double)(n + t), (double)(n + 0.0F)).next();
					vertexBuffer.vertex(worldBorder.getBoundEast(), 256.0, u + v).texture((double)(n + w + t), (double)(n + 0.0F)).next();
					vertexBuffer.vertex(worldBorder.getBoundEast(), 0.0, u + v).texture((double)(n + w + t), (double)(n + 128.0F)).next();
					vertexBuffer.vertex(worldBorder.getBoundEast(), 0.0, u).texture((double)(n + t), (double)(n + 128.0F)).next();
					u++;
				}
			}

			if (g < worldBorder.getBoundWest() + d) {
				float t = 0.0F;

				for (double u = r; u < s; t += 0.5F) {
					double v = Math.min(1.0, s - u);
					float w = (float)v * 0.5F;
					vertexBuffer.vertex(worldBorder.getBoundWest(), 256.0, u).texture((double)(n + t), (double)(n + 0.0F)).next();
					vertexBuffer.vertex(worldBorder.getBoundWest(), 256.0, u + v).texture((double)(n + w + t), (double)(n + 0.0F)).next();
					vertexBuffer.vertex(worldBorder.getBoundWest(), 0.0, u + v).texture((double)(n + w + t), (double)(n + 128.0F)).next();
					vertexBuffer.vertex(worldBorder.getBoundWest(), 0.0, u).texture((double)(n + t), (double)(n + 128.0F)).next();
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
					vertexBuffer.vertex(u, 256.0, worldBorder.getBoundSouth()).texture((double)(n + t), (double)(n + 0.0F)).next();
					vertexBuffer.vertex(u + v, 256.0, worldBorder.getBoundSouth()).texture((double)(n + w + t), (double)(n + 0.0F)).next();
					vertexBuffer.vertex(u + v, 0.0, worldBorder.getBoundSouth()).texture((double)(n + w + t), (double)(n + 128.0F)).next();
					vertexBuffer.vertex(u, 0.0, worldBorder.getBoundSouth()).texture((double)(n + t), (double)(n + 128.0F)).next();
					u++;
				}
			}

			if (i < worldBorder.getBoundNorth() + d) {
				float t = 0.0F;

				for (double u = r; u < s; t += 0.5F) {
					double v = Math.min(1.0, s - u);
					float w = (float)v * 0.5F;
					vertexBuffer.vertex(u, 256.0, worldBorder.getBoundNorth()).texture((double)(n + t), (double)(n + 0.0F)).next();
					vertexBuffer.vertex(u + v, 256.0, worldBorder.getBoundNorth()).texture((double)(n + w + t), (double)(n + 0.0F)).next();
					vertexBuffer.vertex(u + v, 0.0, worldBorder.getBoundNorth()).texture((double)(n + w + t), (double)(n + 128.0F)).next();
					vertexBuffer.vertex(u, 0.0, worldBorder.getBoundNorth()).texture((double)(n + t), (double)(n + 128.0F)).next();
					u++;
				}
			}

			tessellator.draw();
			vertexBuffer.setOffset(0.0, 0.0, 0.0);
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
			GlStateManager.SrcBlendFactor.DST_COLOR, GlStateManager.DstBlendFactor.SRC_COLOR, GlStateManager.SrcBlendFactor.ONE, GlStateManager.DstBlendFactor.ZERO
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

	public void renderPartiallyBrokenBlocks(Tessellator tessellator, VertexBuffer vertexBuffer, Entity entity, float f) {
		double d = MathHelper.lerp((double)f, entity.prevRenderX, entity.x);
		double e = MathHelper.lerp((double)f, entity.prevRenderY, entity.y);
		double g = MathHelper.lerp((double)f, entity.prevRenderZ, entity.z);
		if (!this.partiallyBrokenBlocks.isEmpty()) {
			this.textureManager.bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
			this.method_3263();
			vertexBuffer.begin(7, VertexFormats.field_1582);
			vertexBuffer.setOffset(-d, -e, -g);
			vertexBuffer.disableColor();
			Iterator<PartiallyBrokenBlockEntry> iterator = this.partiallyBrokenBlocks.values().iterator();

			while (iterator.hasNext()) {
				PartiallyBrokenBlockEntry partiallyBrokenBlockEntry = (PartiallyBrokenBlockEntry)iterator.next();
				BlockPos blockPos = partiallyBrokenBlockEntry.getPos();
				Block block = this.world.getBlockState(blockPos).getBlock();
				if (!(block instanceof ChestBlock) && !(block instanceof EnderChestBlock) && !(block instanceof SignBlock) && !(block instanceof AbstractSkullBlock)) {
					double h = (double)blockPos.getX() - d;
					double i = (double)blockPos.getY() - e;
					double j = (double)blockPos.getZ() - g;
					if (h * h + i * i + j * j > 1024.0) {
						iterator.remove();
					} else {
						BlockState blockState = this.world.getBlockState(blockPos);
						if (!blockState.isAir()) {
							int k = partiallyBrokenBlockEntry.getStage();
							Sprite sprite = this.destroyStages[k];
							BlockRenderManager blockRenderManager = this.client.getBlockRenderManager();
							blockRenderManager.tesselate(blockState, blockPos, sprite, this.world);
						}
					}
				}
			}

			tessellator.draw();
			vertexBuffer.setOffset(0.0, 0.0, 0.0);
			this.method_3274();
		}
	}

	public void method_3294(PlayerEntity playerEntity, HitResult hitResult, int i, float f) {
		if (i == 0 && hitResult.type == HitResult.Type.BLOCK) {
			BlockPos blockPos = hitResult.getBlockPos();
			BlockState blockState = this.world.getBlockState(blockPos);
			if (!blockState.isAir() && this.world.getWorldBorder().contains(blockPos)) {
				GlStateManager.enableBlend();
				GlStateManager.blendFuncSeparate(
					GlStateManager.SrcBlendFactor.SRC_ALPHA,
					GlStateManager.DstBlendFactor.ONE_MINUS_SRC_ALPHA,
					GlStateManager.SrcBlendFactor.ONE,
					GlStateManager.DstBlendFactor.ZERO
				);
				GlStateManager.lineWidth(Math.max(2.5F, (float)this.client.window.getWindowWidth() / 1920.0F * 2.5F));
				GlStateManager.disableTexture();
				GlStateManager.depthMask(false);
				GlStateManager.matrixMode(5889);
				GlStateManager.pushMatrix();
				GlStateManager.scalef(1.0F, 1.0F, 0.999F);
				double d = MathHelper.lerp((double)f, playerEntity.prevRenderX, playerEntity.x);
				double e = MathHelper.lerp((double)f, playerEntity.prevRenderY, playerEntity.y);
				double g = MathHelper.lerp((double)f, playerEntity.prevRenderZ, playerEntity.z);
				method_3291(
					blockState.getBoundingShape(this.world, blockPos),
					(double)blockPos.getX() - d,
					(double)blockPos.getY() - e,
					(double)blockPos.getZ() - g,
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

	public static void method_3240(VoxelShape voxelShape, double d, double e, double f, float g, float h, float i, float j) {
		List<BoundingBox> list = voxelShape.getBoundingBoxList();
		int k = MathHelper.ceil((double)list.size() / 3.0);

		for (int l = 0; l < list.size(); l++) {
			BoundingBox boundingBox = (BoundingBox)list.get(l);
			float m = ((float)l % (float)k + 1.0F) / (float)k;
			float n = (float)(l / k);
			float o = m * (float)(n == 0.0F ? 1 : 0);
			float p = m * (float)(n == 1.0F ? 1 : 0);
			float q = m * (float)(n == 2.0F ? 1 : 0);
			method_3291(VoxelShapes.cube(boundingBox.offset(0.0, 0.0, 0.0)), d, e, f, o, p, q, 1.0F);
		}
	}

	public static void method_3291(VoxelShape voxelShape, double d, double e, double f, float g, float h, float i, float j) {
		Tessellator tessellator = Tessellator.getInstance();
		VertexBuffer vertexBuffer = tessellator.getVertexBuffer();
		vertexBuffer.begin(1, VertexFormats.POSITION_COLOR);
		voxelShape.method_1104((k, l, m, n, o, p) -> {
			vertexBuffer.vertex(k + d, l + e, m + f).color(g, h, i, j).next();
			vertexBuffer.vertex(n + d, o + e, p + f).color(g, h, i, j).next();
		});
		tessellator.draw();
	}

	public static void method_3260(BoundingBox boundingBox, float f, float g, float h, float i) {
		method_3262(boundingBox.minX, boundingBox.minY, boundingBox.minZ, boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ, f, g, h, i);
	}

	public static void method_3262(double d, double e, double f, double g, double h, double i, float j, float k, float l, float m) {
		Tessellator tessellator = Tessellator.getInstance();
		VertexBuffer vertexBuffer = tessellator.getVertexBuffer();
		vertexBuffer.begin(3, VertexFormats.POSITION_COLOR);
		method_3258(vertexBuffer, d, e, f, g, h, i, j, k, l, m);
		tessellator.draw();
	}

	public static void method_3258(VertexBuffer vertexBuffer, double d, double e, double f, double g, double h, double i, float j, float k, float l, float m) {
		vertexBuffer.vertex(d, e, f).color(j, k, l, 0.0F).next();
		vertexBuffer.vertex(d, e, f).color(j, k, l, m).next();
		vertexBuffer.vertex(g, e, f).color(j, k, l, m).next();
		vertexBuffer.vertex(g, e, i).color(j, k, l, m).next();
		vertexBuffer.vertex(d, e, i).color(j, k, l, m).next();
		vertexBuffer.vertex(d, e, f).color(j, k, l, m).next();
		vertexBuffer.vertex(d, h, f).color(j, k, l, m).next();
		vertexBuffer.vertex(g, h, f).color(j, k, l, m).next();
		vertexBuffer.vertex(g, h, i).color(j, k, l, m).next();
		vertexBuffer.vertex(d, h, i).color(j, k, l, m).next();
		vertexBuffer.vertex(d, h, f).color(j, k, l, m).next();
		vertexBuffer.vertex(d, h, i).color(j, k, l, 0.0F).next();
		vertexBuffer.vertex(d, e, i).color(j, k, l, m).next();
		vertexBuffer.vertex(g, h, i).color(j, k, l, 0.0F).next();
		vertexBuffer.vertex(g, e, i).color(j, k, l, m).next();
		vertexBuffer.vertex(g, h, f).color(j, k, l, 0.0F).next();
		vertexBuffer.vertex(g, e, f).color(j, k, l, m).next();
		vertexBuffer.vertex(g, e, f).color(j, k, l, 0.0F).next();
	}

	public static void renderCuboidOutline(BoundingBox boundingBox, float f, float g, float h, float i) {
		renderCuboidOutline(boundingBox.minX, boundingBox.minY, boundingBox.minZ, boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ, f, g, h, i);
	}

	public static void renderCuboidOutline(double d, double e, double f, double g, double h, double i, float j, float k, float l, float m) {
		Tessellator tessellator = Tessellator.getInstance();
		VertexBuffer vertexBuffer = tessellator.getVertexBuffer();
		vertexBuffer.begin(5, VertexFormats.POSITION_COLOR);
		renderCuboid(vertexBuffer, d, e, f, g, h, i, j, k, l, m);
		tessellator.draw();
	}

	public static void renderCuboid(VertexBuffer vertexBuffer, double d, double e, double f, double g, double h, double i, float j, float k, float l, float m) {
		vertexBuffer.vertex(d, e, f).color(j, k, l, m).next();
		vertexBuffer.vertex(d, e, f).color(j, k, l, m).next();
		vertexBuffer.vertex(d, e, f).color(j, k, l, m).next();
		vertexBuffer.vertex(d, e, i).color(j, k, l, m).next();
		vertexBuffer.vertex(d, h, f).color(j, k, l, m).next();
		vertexBuffer.vertex(d, h, i).color(j, k, l, m).next();
		vertexBuffer.vertex(d, h, i).color(j, k, l, m).next();
		vertexBuffer.vertex(d, e, i).color(j, k, l, m).next();
		vertexBuffer.vertex(g, h, i).color(j, k, l, m).next();
		vertexBuffer.vertex(g, e, i).color(j, k, l, m).next();
		vertexBuffer.vertex(g, e, i).color(j, k, l, m).next();
		vertexBuffer.vertex(g, e, f).color(j, k, l, m).next();
		vertexBuffer.vertex(g, h, i).color(j, k, l, m).next();
		vertexBuffer.vertex(g, h, f).color(j, k, l, m).next();
		vertexBuffer.vertex(g, h, f).color(j, k, l, m).next();
		vertexBuffer.vertex(g, e, f).color(j, k, l, m).next();
		vertexBuffer.vertex(d, h, f).color(j, k, l, m).next();
		vertexBuffer.vertex(d, e, f).color(j, k, l, m).next();
		vertexBuffer.vertex(d, e, f).color(j, k, l, m).next();
		vertexBuffer.vertex(g, e, f).color(j, k, l, m).next();
		vertexBuffer.vertex(d, e, i).color(j, k, l, m).next();
		vertexBuffer.vertex(g, e, i).color(j, k, l, m).next();
		vertexBuffer.vertex(g, e, i).color(j, k, l, m).next();
		vertexBuffer.vertex(d, h, f).color(j, k, l, m).next();
		vertexBuffer.vertex(d, h, f).color(j, k, l, m).next();
		vertexBuffer.vertex(d, h, i).color(j, k, l, m).next();
		vertexBuffer.vertex(g, h, f).color(j, k, l, m).next();
		vertexBuffer.vertex(g, h, i).color(j, k, l, m).next();
		vertexBuffer.vertex(g, h, i).color(j, k, l, m).next();
		vertexBuffer.vertex(g, h, i).color(j, k, l, m).next();
	}

	@Override
	public void onBlockUpdate(BlockView blockView, BlockPos blockPos, BlockState blockState, BlockState blockState2, int i) {
		this.method_16037(blockPos, (i & 8) != 0);
	}

	private void method_16037(BlockPos blockPos, boolean bl) {
		for (int i = blockPos.getZ() - 1; i <= blockPos.getZ() + 1; i++) {
			for (int j = blockPos.getX() - 1; j <= blockPos.getX() + 1; j++) {
				for (int k = blockPos.getY() - 1; k <= blockPos.getY() + 1; k++) {
					this.markRegionForRenderUpdate(j >> 4, k >> 4, i >> 4, bl);
				}
			}
		}
	}

	@Override
	public void method_8571(int i, int j, int k) {
		this.markRegionForRenderUpdate(i, j, k, false);
	}

	private void markRegionForRenderUpdate(int i, int j, int k, boolean bl) {
		this.chunkRenderDispatcher.method_16040(i, j, k, bl);
	}

	@Override
	public void playRecord(@Nullable SoundEvent soundEvent, BlockPos blockPos) {
		SoundInstance soundInstance = (SoundInstance)this.field_4119.get(blockPos);
		if (soundInstance != null) {
			this.client.getSoundLoader().stop(soundInstance);
			this.field_4119.remove(blockPos);
		}

		if (soundEvent != null) {
			RecordItem recordItem = RecordItem.bySound(soundEvent);
			if (recordItem != null) {
				this.client.hudInGame.setRecordPlayingOverlay(recordItem.method_8011().getFormattedText());
			}

			SoundInstance var5 = PositionedSoundInstance.record(soundEvent, (float)blockPos.getX(), (float)blockPos.getY(), (float)blockPos.getZ());
			this.field_4119.put(blockPos, var5);
			this.client.getSoundLoader().play(var5);
		}

		this.method_3247(this.world, blockPos, soundEvent != null);
	}

	private void method_3247(World world, BlockPos blockPos, boolean bl) {
		for (LivingEntity livingEntity : world.getVisibleEntities(LivingEntity.class, new BoundingBox(blockPos).expand(3.0))) {
			livingEntity.method_6006(blockPos, bl);
		}
	}

	@Override
	public void onSound(@Nullable PlayerEntity playerEntity, SoundEvent soundEvent, SoundCategory soundCategory, double d, double e, double f, float g, float h) {
	}

	@Override
	public void method_8565(@Nullable PlayerEntity playerEntity, SoundEvent soundEvent, SoundCategory soundCategory, Entity entity, float f, float g) {
	}

	@Override
	public void addParticle(Particle particle, boolean bl, double d, double e, double f, double g, double h, double i) {
		this.addParticle(particle, bl, false, d, e, f, g, h, i);
	}

	@Override
	public void addParticle(Particle particle, boolean bl, boolean bl2, double d, double e, double f, double g, double h, double i) {
		try {
			this.spawnParticle(particle, bl, bl2, d, e, f, g, h, i);
		} catch (Throwable var19) {
			CrashReport crashReport = CrashReport.create(var19, "Exception while adding particle");
			CrashReportElement crashReportElement = crashReport.addElement("Particle being added");
			crashReportElement.add("ID", Registry.PARTICLE_TYPE.getId((ParticleType<? extends Particle>)particle.getParticleType()));
			crashReportElement.add("Parameters", particle.asString());
			crashReportElement.add("Position", (ICrashCallable<String>)(() -> CrashReportElement.method_583(d, e, f)));
			throw new CrashException(crashReport);
		}
	}

	private <T extends Particle> void method_3276(T particle, double d, double e, double f, double g, double h, double i) {
		this.addParticle(particle, particle.getParticleType().alwaysShow(), d, e, f, g, h, i);
	}

	@Nullable
	private net.minecraft.client.particle.Particle spawnParticle(Particle particle, boolean bl, double d, double e, double f, double g, double h, double i) {
		return this.spawnParticle(particle, bl, false, d, e, f, g, h, i);
	}

	@Nullable
	private net.minecraft.client.particle.Particle spawnParticle(
		Particle particle, boolean bl, boolean bl2, double d, double e, double f, double g, double h, double i
	) {
		Entity entity = this.client.getCameraEntity();
		if (this.client != null && entity != null && this.client.particleManager != null) {
			int j = this.method_3268(bl2);
			double k = entity.x - d;
			double l = entity.y - e;
			double m = entity.z - f;
			if (bl) {
				return this.client.particleManager.method_3056(particle, d, e, f, g, h, i);
			} else if (k * k + l * l + m * m > 1024.0) {
				return null;
			} else {
				return j > 1 ? null : this.client.particleManager.method_3056(particle, d, e, f, g, h, i);
			}
		} else {
			return null;
		}
	}

	private int method_3268(boolean bl) {
		int i = this.client.options.particles;
		if (bl && i == 2 && this.world.random.nextInt(10) == 0) {
			i = 1;
		}

		if (i == 1 && this.world.random.nextInt(3) == 0) {
			i = 2;
		}

		return i;
	}

	@Override
	public void onEntityAdded(Entity entity) {
	}

	@Override
	public void onEntityRemoved(Entity entity) {
	}

	public void method_3267() {
	}

	@Override
	public void onGlobalWorldEvent(int i, BlockPos blockPos, int j) {
		switch (i) {
			case 1023:
			case 1028:
			case 1038:
				Entity entity = this.client.getCameraEntity();
				if (entity != null) {
					double d = (double)blockPos.getX() - entity.x;
					double e = (double)blockPos.getY() - entity.y;
					double f = (double)blockPos.getZ() - entity.z;
					double g = Math.sqrt(d * d + e * e + f * f);
					double h = entity.x;
					double k = entity.y;
					double l = entity.z;
					if (g > 0.0) {
						h += d / g * 2.0;
						k += e / g * 2.0;
						l += f / g * 2.0;
					}

					if (i == 1023) {
						this.world.playSound(h, k, l, SoundEvents.field_14792, SoundCategory.field_15251, 1.0F, 1.0F, false);
					} else if (i == 1038) {
						this.world.playSound(h, k, l, SoundEvents.field_14981, SoundCategory.field_15251, 1.0F, 1.0F, false);
					} else {
						this.world.playSound(h, k, l, SoundEvents.field_14773, SoundCategory.field_15251, 5.0F, 1.0F, false);
					}
				}
		}
	}

	@Override
	public void onWorldEvent(PlayerEntity playerEntity, int i, BlockPos blockPos, int j) {
		Random random = this.world.random;
		switch (i) {
			case 1000:
				this.world.playSoundClient(blockPos, SoundEvents.field_14611, SoundCategory.field_15245, 1.0F, 1.0F, false);
				break;
			case 1001:
				this.world.playSoundClient(blockPos, SoundEvents.field_14701, SoundCategory.field_15245, 1.0F, 1.2F, false);
				break;
			case 1002:
				this.world.playSoundClient(blockPos, SoundEvents.field_14711, SoundCategory.field_15245, 1.0F, 1.2F, false);
				break;
			case 1003:
				this.world.playSoundClient(blockPos, SoundEvents.field_15155, SoundCategory.field_15254, 1.0F, 1.2F, false);
				break;
			case 1004:
				this.world.playSoundClient(blockPos, SoundEvents.field_14712, SoundCategory.field_15254, 1.0F, 1.2F, false);
				break;
			case 1005:
				this.world.playSoundClient(blockPos, SoundEvents.field_14567, SoundCategory.field_15245, 1.0F, this.world.random.nextFloat() * 0.1F + 0.9F, false);
				break;
			case 1006:
				this.world.playSoundClient(blockPos, SoundEvents.field_14664, SoundCategory.field_15245, 1.0F, this.world.random.nextFloat() * 0.1F + 0.9F, false);
				break;
			case 1007:
				this.world.playSoundClient(blockPos, SoundEvents.field_14932, SoundCategory.field_15245, 1.0F, this.world.random.nextFloat() * 0.1F + 0.9F, false);
				break;
			case 1008:
				this.world.playSoundClient(blockPos, SoundEvents.field_14766, SoundCategory.field_15245, 1.0F, this.world.random.nextFloat() * 0.1F + 0.9F, false);
				break;
			case 1009:
				this.world
					.playSoundClient(blockPos, SoundEvents.field_15102, SoundCategory.field_15245, 0.5F, 2.6F + (random.nextFloat() - random.nextFloat()) * 0.8F, false);
				break;
			case 1010:
				if (Item.byRawId(j) instanceof RecordItem) {
					this.world.playRecord(blockPos, ((RecordItem)Item.byRawId(j)).getSound());
				} else {
					this.world.playRecord(blockPos, null);
				}
				break;
			case 1011:
				this.world.playSoundClient(blockPos, SoundEvents.field_14819, SoundCategory.field_15245, 1.0F, this.world.random.nextFloat() * 0.1F + 0.9F, false);
				break;
			case 1012:
				this.world.playSoundClient(blockPos, SoundEvents.field_14541, SoundCategory.field_15245, 1.0F, this.world.random.nextFloat() * 0.1F + 0.9F, false);
				break;
			case 1013:
				this.world.playSoundClient(blockPos, SoundEvents.field_15080, SoundCategory.field_15245, 1.0F, this.world.random.nextFloat() * 0.1F + 0.9F, false);
				break;
			case 1014:
				this.world.playSoundClient(blockPos, SoundEvents.field_14861, SoundCategory.field_15245, 1.0F, this.world.random.nextFloat() * 0.1F + 0.9F, false);
				break;
			case 1015:
				this.world
					.playSoundClient(blockPos, SoundEvents.field_15130, SoundCategory.field_15251, 10.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
				break;
			case 1016:
				this.world
					.playSoundClient(blockPos, SoundEvents.field_15231, SoundCategory.field_15251, 10.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
				break;
			case 1017:
				this.world
					.playSoundClient(blockPos, SoundEvents.field_14934, SoundCategory.field_15251, 10.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
				break;
			case 1018:
				this.world
					.playSoundClient(blockPos, SoundEvents.field_14970, SoundCategory.field_15251, 2.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
				break;
			case 1019:
				this.world
					.playSoundClient(blockPos, SoundEvents.field_14562, SoundCategory.field_15251, 2.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
				break;
			case 1020:
				this.world
					.playSoundClient(blockPos, SoundEvents.field_14670, SoundCategory.field_15251, 2.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
				break;
			case 1021:
				this.world
					.playSoundClient(blockPos, SoundEvents.field_14742, SoundCategory.field_15251, 2.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
				break;
			case 1022:
				this.world
					.playSoundClient(blockPos, SoundEvents.field_15236, SoundCategory.field_15251, 2.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
				break;
			case 1024:
				this.world
					.playSoundClient(blockPos, SoundEvents.field_14588, SoundCategory.field_15251, 2.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
				break;
			case 1025:
				this.world
					.playSoundClient(blockPos, SoundEvents.field_14610, SoundCategory.field_15254, 0.05F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
				break;
			case 1026:
				this.world
					.playSoundClient(blockPos, SoundEvents.field_14986, SoundCategory.field_15251, 2.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
				break;
			case 1027:
				this.world
					.playSoundClient(blockPos, SoundEvents.field_15168, SoundCategory.field_15254, 2.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
				break;
			case 1029:
				this.world.playSoundClient(blockPos, SoundEvents.field_14665, SoundCategory.field_15245, 1.0F, this.world.random.nextFloat() * 0.1F + 0.9F, false);
				break;
			case 1030:
				this.world.playSoundClient(blockPos, SoundEvents.field_14559, SoundCategory.field_15245, 1.0F, this.world.random.nextFloat() * 0.1F + 0.9F, false);
				break;
			case 1031:
				this.world.playSoundClient(blockPos, SoundEvents.field_14833, SoundCategory.field_15245, 0.3F, this.world.random.nextFloat() * 0.1F + 0.9F, false);
				break;
			case 1032:
				this.client.getSoundLoader().play(PositionedSoundInstance.master(SoundEvents.field_14716, random.nextFloat() * 0.4F + 0.8F));
				break;
			case 1033:
				this.world.playSoundClient(blockPos, SoundEvents.field_14817, SoundCategory.field_15245, 1.0F, 1.0F, false);
				break;
			case 1034:
				this.world.playSoundClient(blockPos, SoundEvents.field_14739, SoundCategory.field_15245, 1.0F, 1.0F, false);
				break;
			case 1035:
				this.world.playSoundClient(blockPos, SoundEvents.field_14978, SoundCategory.field_15245, 1.0F, 1.0F, false);
				break;
			case 1036:
				this.world.playSoundClient(blockPos, SoundEvents.field_15131, SoundCategory.field_15245, 1.0F, this.world.random.nextFloat() * 0.1F + 0.9F, false);
				break;
			case 1037:
				this.world.playSoundClient(blockPos, SoundEvents.field_15082, SoundCategory.field_15245, 1.0F, this.world.random.nextFloat() * 0.1F + 0.9F, false);
				break;
			case 1039:
				this.world.playSoundClient(blockPos, SoundEvents.field_14729, SoundCategory.field_15251, 0.3F, this.world.random.nextFloat() * 0.1F + 0.9F, false);
				break;
			case 1040:
				this.world
					.playSoundClient(blockPos, SoundEvents.field_14850, SoundCategory.field_15254, 2.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
				break;
			case 1041:
				this.world
					.playSoundClient(blockPos, SoundEvents.field_15128, SoundCategory.field_15254, 2.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
				break;
			case 1042:
				this.world.playSoundClient(blockPos, SoundEvents.field_16690, SoundCategory.field_15254, 10.0F, 1.0F, false);
				break;
			case 1043:
				this.world.playSoundClient(blockPos, SoundEvents.field_16865, SoundCategory.field_15245, 1.0F, this.world.random.nextFloat() * 0.1F + 0.9F, false);
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
				BlockState blockState = Block.getStateFromRawId(j);
				if (!blockState.isAir()) {
					BlockSoundGroup blockSoundGroup = blockState.getSoundGroup();
					this.world
						.playSoundClient(
							blockPos,
							blockSoundGroup.getBreakSound(),
							SoundCategory.field_15245,
							(blockSoundGroup.getVolume() + 1.0F) / 2.0F,
							blockSoundGroup.getPitch() * 0.8F,
							false
						);
				}

				this.client.particleManager.method_3046(blockPos, blockState);
				break;
			case 2002:
			case 2007:
				double t = (double)blockPos.getX();
				double u = (double)blockPos.getY();
				double d = (double)blockPos.getZ();

				for (int v = 0; v < 8; v++) {
					this.method_3276(
						new ItemStackParticle(ParticleTypes.field_11218, new ItemStack(Items.field_8436)),
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
				Particle particle = i == 2007 ? ParticleTypes.field_11213 : ParticleTypes.field_11245;

				for (int n = 0; n < 100; n++) {
					double g = random.nextDouble() * 4.0;
					double h = random.nextDouble() * Math.PI * 2.0;
					double o = Math.cos(h) * g;
					double p = 0.01 + random.nextDouble() * 0.5;
					double q = Math.sin(h) * g;
					net.minecraft.client.particle.Particle particle2 = this.spawnParticle(
						particle, particle.getParticleType().alwaysShow(), t + o * 0.1, u + 0.3, d + q * 0.1, o, p, q
					);
					if (particle2 != null) {
						float z = 0.75F + random.nextFloat() * 0.25F;
						particle2.setColor(w * z, x * z, y * z);
						particle2.method_3075((float)g);
					}
				}

				this.world.playSoundClient(blockPos, SoundEvents.field_14839, SoundCategory.field_15254, 1.0F, this.world.random.nextFloat() * 0.1F + 0.9F, false);
				break;
			case 2003:
				double t = (double)blockPos.getX() + 0.5;
				double u = (double)blockPos.getY();
				double d = (double)blockPos.getZ() + 0.5;

				for (int v = 0; v < 8; v++) {
					this.method_3276(
						new ItemStackParticle(ParticleTypes.field_11218, new ItemStack(Items.field_8449)),
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
					net.minecraft.client.particle.Particle particle3 = this.spawnParticle(
						ParticleTypes.field_11216, false, (double)blockPos.getX() + ac * 0.1, (double)blockPos.getY() + 0.3, (double)blockPos.getZ() + ag * 0.1, ac, ad, ag
					);
					if (particle3 != null) {
						particle3.method_3075(ae);
					}
				}

				this.world.playSoundClient(blockPos, SoundEvents.field_14803, SoundCategory.field_15251, 1.0F, this.world.random.nextFloat() * 0.1F + 0.9F, false);
				break;
			case 3000:
				this.world
					.method_8466(ParticleTypes.field_11221, true, (double)blockPos.getX() + 0.5, (double)blockPos.getY() + 0.5, (double)blockPos.getZ() + 0.5, 0.0, 0.0, 0.0);
				this.world
					.playSoundClient(
						blockPos,
						SoundEvents.field_14816,
						SoundCategory.field_15245,
						10.0F,
						(1.0F + (this.world.random.nextFloat() - this.world.random.nextFloat()) * 0.2F) * 0.7F,
						false
					);
				break;
			case 3001:
				this.world.playSoundClient(blockPos, SoundEvents.field_14671, SoundCategory.field_15251, 64.0F, 0.8F + this.world.random.nextFloat() * 0.3F, false);
		}
	}

	@Override
	public void onBlockBreakingStage(int i, BlockPos blockPos, int j) {
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
			partiallyBrokenBlockEntry.method_13989(this.ticks);
		} else {
			this.partiallyBrokenBlocks.remove(i);
		}
	}

	public boolean method_3281() {
		return this.field_4075.isEmpty() && this.chunkBatcher.method_3630();
	}

	public void method_3292() {
		this.field_4077 = true;
		this.field_4107 = true;
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
