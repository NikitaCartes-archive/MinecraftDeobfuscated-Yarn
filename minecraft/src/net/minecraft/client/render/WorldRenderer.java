package net.minecraft.client.render;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.SortedSet;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.CameraSubmersionType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.GlUniform;
import net.minecraft.client.gl.PostEffectProcessor;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gl.SimpleFramebuffer;
import net.minecraft.client.gl.SimpleFramebufferFactory;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.CloudRenderMode;
import net.minecraft.client.option.GraphicsMode;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.chunk.ChunkBuilder;
import net.minecraft.client.render.chunk.ChunkRendererRegionBuilder;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.model.ModelBaker;
import net.minecraft.client.util.Handle;
import net.minecraft.client.util.ObjectAllocator;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.BlockBreakingInfo;
import net.minecraft.nbt.NbtOps;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.particle.ParticlesMode;
import net.minecraft.registry.Registries;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SynchronousResourceReloader;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.annotation.Debug;
import net.minecraft.util.crash.CrashCallable;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.BlockView;
import net.minecraft.world.LightType;
import net.minecraft.world.chunk.light.LightingProvider;
import net.minecraft.world.tick.TickManager;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Vector4f;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class WorldRenderer implements SynchronousResourceReloader, AutoCloseable {
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final Identifier field_53900 = Identifier.ofVanilla("transparency");
	private static final Identifier field_53901 = Identifier.ofVanilla("entity_outline");
	public static final int field_32759 = 16;
	public static final int field_34812 = 8;
	private static final int field_32766 = 15;
	private final MinecraftClient client;
	private final EntityRenderDispatcher entityRenderDispatcher;
	private final BlockEntityRenderDispatcher blockEntityRenderDispatcher;
	private final BufferBuilderStorage bufferBuilders;
	private final SkyRendering skyRendering = new SkyRendering();
	private final CloudRenderer cloudRenderer = new CloudRenderer();
	private final WorldBorderRendering worldBorderRendering = new WorldBorderRendering();
	private final WeatherRendering weatherRendering = new WeatherRendering();
	@Nullable
	private ClientWorld world;
	private final ChunkRenderingDataPreparer chunkRenderingDataPreparer = new ChunkRenderingDataPreparer();
	private final ObjectArrayList<ChunkBuilder.BuiltChunk> builtChunks = new ObjectArrayList<>(10000);
	private final Set<BlockEntity> noCullingBlockEntities = Sets.<BlockEntity>newHashSet();
	@Nullable
	private BuiltChunkStorage chunks;
	private int ticks;
	private final Int2ObjectMap<BlockBreakingInfo> blockBreakingInfos = new Int2ObjectOpenHashMap<>();
	private final Long2ObjectMap<SortedSet<BlockBreakingInfo>> blockBreakingProgressions = new Long2ObjectOpenHashMap<>();
	@Nullable
	private Framebuffer entityOutlineFramebuffer;
	private final DefaultFramebufferSet framebufferSet = new DefaultFramebufferSet();
	private int cameraChunkX = Integer.MIN_VALUE;
	private int cameraChunkY = Integer.MIN_VALUE;
	private int cameraChunkZ = Integer.MIN_VALUE;
	private double lastCameraX = Double.MIN_VALUE;
	private double lastCameraY = Double.MIN_VALUE;
	private double lastCameraZ = Double.MIN_VALUE;
	private double lastCameraPitch = Double.MIN_VALUE;
	private double lastCameraYaw = Double.MIN_VALUE;
	@Nullable
	private ChunkBuilder chunkBuilder;
	private int viewDistance = -1;
	private final List<Entity> renderedEntities = new ArrayList();
	private int renderedEntitiesCount;
	private Frustum frustum;
	private boolean shouldCaptureFrustum;
	@Nullable
	private Frustum capturedFrustum;
	@Nullable
	private Vec3d field_53074;

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
	}

	public void method_62209(Camera camera) {
		this.weatherRendering.method_62319(this.client.world, camera, this.ticks, this.client.options.getParticles().getValue());
	}

	public void close() {
		if (this.entityOutlineFramebuffer != null) {
			this.entityOutlineFramebuffer.delete();
		}

		this.skyRendering.close();
		this.cloudRenderer.close();
	}

	@Override
	public void reload(ResourceManager manager) {
		this.loadEntityOutlinePostProcessor();
	}

	public void loadEntityOutlinePostProcessor() {
		if (this.entityOutlineFramebuffer != null) {
			this.entityOutlineFramebuffer.delete();
		}

		this.entityOutlineFramebuffer = new SimpleFramebuffer(this.client.getWindow().getFramebufferWidth(), this.client.getWindow().getFramebufferHeight(), true);
		this.entityOutlineFramebuffer.setClearColor(0.0F, 0.0F, 0.0F, 0.0F);
	}

	@Nullable
	private PostEffectProcessor method_62907() {
		if (!MinecraftClient.isFabulousGraphicsOrBetter()) {
			return null;
		} else {
			PostEffectProcessor postEffectProcessor = this.client.method_62887().loadPostEffect(field_53900, DefaultFramebufferSet.STAGES);
			if (postEffectProcessor == null) {
				String string = "Failed to load shader: " + field_53900;
				WorldRenderer.ProgramInitException programInitException = new WorldRenderer.ProgramInitException(string);
				if (this.client.getResourcePackManager().getEnabledIds().size() > 1) {
					Text text = (Text)this.client.getResourceManager().streamResourcePacks().findFirst().map(resourcePack -> Text.literal(resourcePack.getId())).orElse(null);
					this.client.options.getGraphicsMode().setValue(GraphicsMode.FANCY);
					this.client.onResourceReloadFailure(programInitException, text, null);
				} else {
					this.client.options.getGraphicsMode().setValue(GraphicsMode.FANCY);
					this.client.options.write();
					LOGGER.error(LogUtils.FATAL_MARKER, string, (Throwable)programInitException);
					this.client.printCrashReport(new CrashReport(string, programInitException));
				}
			}

			return postEffectProcessor;
		}
	}

	public void drawEntityOutlinesFramebuffer() {
		if (this.canDrawEntityOutlines()) {
			RenderSystem.enableBlend();
			RenderSystem.blendFuncSeparate(
				GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SrcFactor.ZERO, GlStateManager.DstFactor.ONE
			);
			this.entityOutlineFramebuffer.drawInternal(this.client.getWindow().getFramebufferWidth(), this.client.getWindow().getFramebufferHeight());
			RenderSystem.disableBlend();
			RenderSystem.defaultBlendFunc();
		}
	}

	protected boolean canDrawEntityOutlines() {
		return !this.client.gameRenderer.isRenderingPanorama() && this.entityOutlineFramebuffer != null && this.client.player != null;
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

			if (this.chunkBuilder != null) {
				this.chunkBuilder.stop();
			}

			this.chunkBuilder = null;
			this.noCullingBlockEntities.clear();
			this.chunkRenderingDataPreparer.method_52826(null);
			this.builtChunks.clear();
		}
	}

	public void reload() {
		if (this.world != null) {
			this.world.reloadColor();
			if (this.chunkBuilder == null) {
				this.chunkBuilder = new ChunkBuilder(
					this.world, this, Util.getMainWorkerExecutor(), this.bufferBuilders, this.client.getBlockRenderManager(), this.client.getBlockEntityRenderDispatcher()
				);
			} else {
				this.chunkBuilder.setWorld(this.world);
			}

			this.cloudRenderer.scheduleTerrainUpdate();
			RenderLayers.setFancyGraphicsOrBetter(MinecraftClient.isFancyGraphicsOrBetter());
			this.viewDistance = this.client.options.getClampedViewDistance();
			if (this.chunks != null) {
				this.chunks.clear();
			}

			this.chunkBuilder.reset();
			synchronized (this.noCullingBlockEntities) {
				this.noCullingBlockEntities.clear();
			}

			this.chunks = new BuiltChunkStorage(this.chunkBuilder, this.world, this.client.options.getClampedViewDistance(), this);
			this.chunkRenderingDataPreparer.method_52826(this.chunks);
			this.builtChunks.clear();
			Entity entity = this.client.getCameraEntity();
			if (entity != null) {
				this.chunks.updateCameraPosition(ChunkSectionPos.from(entity));
			}
		}
	}

	public void onResized(int width, int height) {
		this.scheduleTerrainUpdate();
		if (this.entityOutlineFramebuffer != null) {
			this.entityOutlineFramebuffer.resize(width, height);
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
			this.chunkBuilder == null ? "null" : this.chunkBuilder.getDebugString()
		);
	}

	public ChunkBuilder getChunkBuilder() {
		return this.chunkBuilder;
	}

	public double getChunkCount() {
		return (double)this.chunks.chunks.length;
	}

	public double getViewDistance() {
		return (double)this.viewDistance;
	}

	public int getCompletedChunkCount() {
		int i = 0;

		for (ChunkBuilder.BuiltChunk builtChunk : this.builtChunks) {
			if (builtChunk.getData().method_62972()) {
				i++;
			}
		}

		return i;
	}

	public String getEntitiesDebugString() {
		return "E: " + this.renderedEntitiesCount + "/" + this.world.getRegularEntityCount() + ", SD: " + this.world.getSimulationDistance();
	}

	private void setupTerrain(Camera camera, Frustum frustum, boolean hasForcedFrustum, boolean spectator) {
		Vec3d vec3d = camera.getPos();
		if (this.client.options.getClampedViewDistance() != this.viewDistance) {
			this.reload();
		}

		Profiler profiler = this.world.getProfiler();
		profiler.push("camera");
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
			this.chunks.updateCameraPosition(ChunkSectionPos.from(this.client.player));
		}

		this.chunkBuilder.setCameraPosition(vec3d);
		profiler.swap("cull");
		double g = Math.floor(vec3d.x / 8.0);
		double h = Math.floor(vec3d.y / 8.0);
		double l = Math.floor(vec3d.z / 8.0);
		if (g != this.lastCameraX || h != this.lastCameraY || l != this.lastCameraZ) {
			this.chunkRenderingDataPreparer.scheduleTerrainUpdate();
		}

		this.lastCameraX = g;
		this.lastCameraY = h;
		this.lastCameraZ = l;
		profiler.swap("update");
		if (!hasForcedFrustum) {
			boolean bl = this.client.chunkCullingEnabled;
			if (spectator && this.world.getBlockState(camera.getBlockPos()).isOpaqueFullCube()) {
				bl = false;
			}

			profiler.push("section_occlusion_graph");
			this.chunkRenderingDataPreparer.method_52834(bl, camera, frustum, this.builtChunks, this.world.getChunkManager().method_62890());
			profiler.pop();
			double m = Math.floor((double)(camera.getPitch() / 2.0F));
			double n = Math.floor((double)(camera.getYaw() / 2.0F));
			if (this.chunkRenderingDataPreparer.method_52836() || m != this.lastCameraPitch || n != this.lastCameraYaw) {
				this.applyFrustum(method_52816(frustum));
				this.lastCameraPitch = m;
				this.lastCameraYaw = n;
			}
		}

		profiler.pop();
	}

	public static Frustum method_52816(Frustum frustum) {
		return new Frustum(frustum).coverBoxAroundSetPosition(8);
	}

	private void applyFrustum(Frustum frustum) {
		if (!MinecraftClient.getInstance().isOnThread()) {
			throw new IllegalStateException("applyFrustum called from wrong thread: " + Thread.currentThread().getName());
		} else {
			this.client.getProfiler().push("apply_frustum");
			this.builtChunks.clear();
			this.chunkRenderingDataPreparer.method_52828(frustum, this.builtChunks);
			this.client.getProfiler().pop();
		}
	}

	public void addBuiltChunk(ChunkBuilder.BuiltChunk chunk) {
		this.chunkRenderingDataPreparer.method_52827(chunk);
	}

	public void setupFrustum(Vec3d vec3d, Matrix4f matrix4f, Matrix4f matrix4f2) {
		this.frustum = new Frustum(matrix4f, matrix4f2);
		this.frustum.setPosition(vec3d.getX(), vec3d.getY(), vec3d.getZ());
	}

	public void render(
		ObjectAllocator objectAllocator,
		RenderTickCounter tickCounter,
		boolean bl,
		Camera camera,
		GameRenderer gameRenderer,
		LightmapTextureManager lightmapTextureManager,
		Matrix4f positionMatrix,
		Matrix4f projectionMatrix
	) {
		float f = tickCounter.getTickDelta(false);
		RenderSystem.setShaderGameTime(this.world.getTime(), f);
		this.blockEntityRenderDispatcher.configure(this.world, camera, this.client.crosshairTarget);
		this.entityRenderDispatcher.configure(this.world, camera, this.client.targetedEntity);
		final Profiler profiler = this.world.getProfiler();
		profiler.swap("light_update_queue");
		this.world.runQueuedChunkUpdates();
		profiler.swap("light_updates");
		this.world.getChunkManager().getLightingProvider().doLightUpdates();
		Vec3d vec3d = camera.getPos();
		double d = vec3d.getX();
		double e = vec3d.getY();
		double g = vec3d.getZ();
		profiler.swap("culling");
		boolean bl2 = this.capturedFrustum != null;
		Frustum frustum = bl2 ? this.capturedFrustum : this.frustum;
		this.client.getProfiler().swap("captureFrustum");
		if (this.shouldCaptureFrustum) {
			this.capturedFrustum = bl2 ? new Frustum(positionMatrix, projectionMatrix) : frustum;
			this.capturedFrustum.setPosition(d, e, g);
			this.shouldCaptureFrustum = false;
		}

		profiler.swap("fog");
		float h = gameRenderer.getViewDistance();
		boolean bl3 = this.client.world.getDimensionEffects().useThickFog(MathHelper.floor(d), MathHelper.floor(e))
			|| this.client.inGameHud.getBossBarHud().shouldThickenFog();
		Vector4f vector4f = BackgroundRenderer.getFogColor(camera, f, this.client.world, this.client.options.getClampedViewDistance(), gameRenderer.getSkyDarkness(f));
		Fog fog = BackgroundRenderer.applyFog(camera, BackgroundRenderer.FogType.FOG_TERRAIN, vector4f, h, bl3, f);
		Fog fog2 = BackgroundRenderer.applyFog(camera, BackgroundRenderer.FogType.FOG_SKY, vector4f, h, bl3, f);
		profiler.swap("cullEntities");
		boolean bl4 = this.getEntitiesToRender(camera, frustum, this.renderedEntities);
		this.renderedEntitiesCount = this.renderedEntities.size();
		profiler.swap("terrain_setup");
		this.setupTerrain(camera, frustum, bl2, this.client.player.isSpectator());
		profiler.swap("compile_sections");
		this.updateChunks(camera);
		Matrix4fStack matrix4fStack = RenderSystem.getModelViewStack();
		matrix4fStack.pushMatrix();
		matrix4fStack.mul(positionMatrix);
		FrameGraphBuilder frameGraphBuilder = new FrameGraphBuilder();
		this.framebufferSet.mainFramebuffer = frameGraphBuilder.createObjectNode("main", this.client.getFramebuffer());
		int i = this.client.getFramebuffer().textureWidth;
		int j = this.client.getFramebuffer().textureHeight;
		SimpleFramebufferFactory simpleFramebufferFactory = new SimpleFramebufferFactory(i, j, true);
		PostEffectProcessor postEffectProcessor = this.method_62907();
		if (postEffectProcessor != null) {
			this.framebufferSet.translucentFramebuffer = frameGraphBuilder.createResourceHandle("translucent", simpleFramebufferFactory);
			this.framebufferSet.itemEntityFramebuffer = frameGraphBuilder.createResourceHandle("item_entity", simpleFramebufferFactory);
			this.framebufferSet.particlesFramebuffer = frameGraphBuilder.createResourceHandle("particles", simpleFramebufferFactory);
			this.framebufferSet.weatherFramebuffer = frameGraphBuilder.createResourceHandle("weather", simpleFramebufferFactory);
			this.framebufferSet.cloudsFramebuffer = frameGraphBuilder.createResourceHandle("clouds", simpleFramebufferFactory);
		}

		if (this.entityOutlineFramebuffer != null) {
			this.framebufferSet.entityOutlineFramebuffer = frameGraphBuilder.createObjectNode("entity_outline", this.entityOutlineFramebuffer);
		}

		RenderPass renderPass = frameGraphBuilder.createPass("clear");
		this.framebufferSet.mainFramebuffer = renderPass.transfer(this.framebufferSet.mainFramebuffer);
		renderPass.setRenderer(() -> {
			RenderSystem.clearColor(vector4f.x, vector4f.y, vector4f.z, 0.0F);
			RenderSystem.clear(16640);
		});
		if (!bl3) {
			this.renderSky(frameGraphBuilder, camera, f, fog2);
		}

		this.renderMain(frameGraphBuilder, frustum, camera, positionMatrix, projectionMatrix, fog, bl, bl4, tickCounter, profiler);
		PostEffectProcessor postEffectProcessor2 = this.client.method_62887().loadPostEffect(field_53901, DefaultFramebufferSet.field_53903);
		if (bl4 && postEffectProcessor2 != null) {
			postEffectProcessor2.render(frameGraphBuilder, i, j, this.framebufferSet);
		}

		this.renderParticles(frameGraphBuilder, camera, lightmapTextureManager, f, fog);
		CloudRenderMode cloudRenderMode = this.client.options.getCloudRenderModeValue();
		if (cloudRenderMode != CloudRenderMode.OFF) {
			float k = this.world.getDimensionEffects().getCloudsHeight();
			if (!Float.isNaN(k)) {
				float l = (float)this.ticks + f;
				int m = this.world.getCloudsColor(f);
				this.renderClouds(frameGraphBuilder, positionMatrix, projectionMatrix, cloudRenderMode, camera.getPos(), l, m, k + 0.33F);
			}
		}

		this.renderWeather(frameGraphBuilder, lightmapTextureManager, camera.getPos(), f, fog);
		if (postEffectProcessor != null) {
			postEffectProcessor.render(frameGraphBuilder, i, j, this.framebufferSet);
		}

		this.renderLateDebug(frameGraphBuilder, vec3d, fog);
		profiler.swap("framegraph");
		frameGraphBuilder.run(objectAllocator, new FrameGraphBuilder.Profiler() {
			@Override
			public void push(String location) {
				profiler.push(location);
			}

			@Override
			public void pop(String location) {
				profiler.pop();
			}
		});
		this.client.getFramebuffer().beginWrite(false);
		this.renderedEntities.clear();
		this.framebufferSet.clear();
		matrix4fStack.popMatrix();
		RenderSystem.depthMask(true);
		RenderSystem.disableBlend();
		RenderSystem.setShaderFog(Fog.DUMMY);
	}

	private void renderMain(
		FrameGraphBuilder frameGraphBuilder,
		Frustum frustum,
		Camera camera,
		Matrix4f matrix4f,
		Matrix4f matrix4f2,
		Fog fog,
		boolean bl,
		boolean bl2,
		RenderTickCounter renderTickCounter,
		Profiler profiler
	) {
		RenderPass renderPass = frameGraphBuilder.createPass("main");
		this.framebufferSet.mainFramebuffer = renderPass.transfer(this.framebufferSet.mainFramebuffer);
		if (this.framebufferSet.translucentFramebuffer != null) {
			this.framebufferSet.translucentFramebuffer = renderPass.transfer(this.framebufferSet.translucentFramebuffer);
		}

		if (this.framebufferSet.itemEntityFramebuffer != null) {
			this.framebufferSet.itemEntityFramebuffer = renderPass.transfer(this.framebufferSet.itemEntityFramebuffer);
		}

		if (this.framebufferSet.weatherFramebuffer != null) {
			this.framebufferSet.weatherFramebuffer = renderPass.transfer(this.framebufferSet.weatherFramebuffer);
		}

		if (bl2 && this.framebufferSet.entityOutlineFramebuffer != null) {
			this.framebufferSet.entityOutlineFramebuffer = renderPass.transfer(this.framebufferSet.entityOutlineFramebuffer);
		}

		Handle<Framebuffer> handle = this.framebufferSet.mainFramebuffer;
		Handle<Framebuffer> handle2 = this.framebufferSet.translucentFramebuffer;
		Handle<Framebuffer> handle3 = this.framebufferSet.itemEntityFramebuffer;
		Handle<Framebuffer> handle4 = this.framebufferSet.weatherFramebuffer;
		Handle<Framebuffer> handle5 = this.framebufferSet.entityOutlineFramebuffer;
		renderPass.setRenderer(() -> {
			RenderSystem.setShaderFog(fog);
			float f = renderTickCounter.getTickDelta(false);
			Vec3d vec3d = camera.getPos();
			double d = vec3d.getX();
			double e = vec3d.getY();
			double g = vec3d.getZ();
			profiler.push("terrain");
			this.renderLayer(RenderLayer.getSolid(), d, e, g, matrix4f, matrix4f2);
			this.renderLayer(RenderLayer.getCutoutMipped(), d, e, g, matrix4f, matrix4f2);
			this.renderLayer(RenderLayer.getCutout(), d, e, g, matrix4f, matrix4f2);
			if (this.world.getDimensionEffects().isDarkened()) {
				DiffuseLighting.enableForLevel();
			} else {
				DiffuseLighting.disableForLevel();
			}

			if (handle3 != null) {
				handle3.get().setClearColor(0.0F, 0.0F, 0.0F, 0.0F);
				handle3.get().clear();
				handle3.get().copyDepthFrom(this.client.getFramebuffer());
				handle.get().beginWrite(false);
			}

			if (handle4 != null) {
				handle4.get().setClearColor(0.0F, 0.0F, 0.0F, 0.0F);
				handle4.get().clear();
			}

			if (this.canDrawEntityOutlines() && handle5 != null) {
				handle5.get().setClearColor(0.0F, 0.0F, 0.0F, 0.0F);
				handle5.get().clear();
				handle.get().beginWrite(false);
			}

			MatrixStack matrixStack = new MatrixStack();
			VertexConsumerProvider.Immediate immediate = this.bufferBuilders.getEntityVertexConsumers();
			VertexConsumerProvider.Immediate immediate2 = this.bufferBuilders.getEffectVertexConsumers();
			profiler.swap("entities");
			this.renderEntities(matrixStack, immediate, camera, renderTickCounter, this.renderedEntities);
			immediate.drawCurrentLayer();
			this.checkEmpty(matrixStack);
			profiler.swap("blockentities");
			this.renderBlockEntities(matrixStack, immediate, immediate2, camera, f);
			immediate.drawCurrentLayer();
			this.checkEmpty(matrixStack);
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
			if (bl) {
				this.renderTargetBlockOutline(camera, immediate, matrixStack, false);
			}

			profiler.swap("debug");
			this.client.debugRenderer.render(matrixStack, frustum, immediate, d, e, g);
			immediate.drawCurrentLayer();
			this.checkEmpty(matrixStack);
			immediate.draw(TexturedRenderLayers.getItemEntityTranslucentCull());
			immediate.draw(TexturedRenderLayers.getBannerPatterns());
			immediate.draw(TexturedRenderLayers.getShieldPatterns());
			immediate.draw(RenderLayer.getArmorEntityGlint());
			immediate.draw(RenderLayer.getGlint());
			immediate.draw(RenderLayer.getGlintTranslucent());
			immediate.draw(RenderLayer.getEntityGlint());
			profiler.swap("destroyProgress");
			this.renderBlockDamage(matrixStack, camera, immediate2);
			immediate2.draw();
			this.checkEmpty(matrixStack);
			immediate.draw(RenderLayer.getWaterMask());
			immediate.draw();
			if (handle2 != null) {
				handle2.get().setClearColor(0.0F, 0.0F, 0.0F, 0.0F);
				handle2.get().clear();
				handle2.get().copyDepthFrom(handle.get());
			}

			profiler.swap("translucent");
			this.renderLayer(RenderLayer.getTranslucent(), d, e, g, matrix4f, matrix4f2);
			profiler.swap("string");
			this.renderLayer(RenderLayer.getTripwire(), d, e, g, matrix4f, matrix4f2);
			if (bl) {
				this.renderTargetBlockOutline(camera, immediate, matrixStack, true);
			}

			immediate.draw();
			profiler.pop();
		});
	}

	private void renderParticles(FrameGraphBuilder frameGraphBuilder, Camera camera, LightmapTextureManager lightmapTextureManager, float f, Fog fog) {
		RenderPass renderPass = frameGraphBuilder.createPass("particles");
		if (this.framebufferSet.particlesFramebuffer != null) {
			this.framebufferSet.particlesFramebuffer = renderPass.transfer(this.framebufferSet.particlesFramebuffer);
			renderPass.dependsOn(this.framebufferSet.mainFramebuffer);
		} else {
			this.framebufferSet.mainFramebuffer = renderPass.transfer(this.framebufferSet.mainFramebuffer);
		}

		Handle<Framebuffer> handle = this.framebufferSet.mainFramebuffer;
		Handle<Framebuffer> handle2 = this.framebufferSet.particlesFramebuffer;
		renderPass.setRenderer(() -> {
			RenderSystem.setShaderFog(fog);
			if (handle2 != null) {
				handle2.get().setClearColor(0.0F, 0.0F, 0.0F, 0.0F);
				handle2.get().clear();
				handle2.get().copyDepthFrom(handle.get());
			}

			RenderPhase.PARTICLES_TARGET.startDrawing();
			this.client.particleManager.renderParticles(lightmapTextureManager, camera, f);
			RenderPhase.PARTICLES_TARGET.endDrawing();
		});
	}

	private void renderClouds(
		FrameGraphBuilder frameGraphBuilder,
		Matrix4f positionMatrix,
		Matrix4f projectionMatrix,
		CloudRenderMode renderMode,
		Vec3d cameraPos,
		float ticks,
		int color,
		float cloudHeight
	) {
		RenderPass renderPass = frameGraphBuilder.createPass("clouds");
		if (this.framebufferSet.cloudsFramebuffer != null) {
			this.framebufferSet.cloudsFramebuffer = renderPass.transfer(this.framebufferSet.cloudsFramebuffer);
		} else {
			this.framebufferSet.mainFramebuffer = renderPass.transfer(this.framebufferSet.mainFramebuffer);
		}

		Handle<Framebuffer> handle = this.framebufferSet.cloudsFramebuffer;
		renderPass.setRenderer(() -> {
			if (handle != null) {
				handle.get().setClearColor(0.0F, 0.0F, 0.0F, 0.0F);
				handle.get().clear();
			}

			this.cloudRenderer.renderClouds(color, renderMode, cloudHeight, positionMatrix, projectionMatrix, cameraPos, ticks);
		});
	}

	private void renderWeather(FrameGraphBuilder frameGraphBuilder, LightmapTextureManager lightmapTextureManager, Vec3d vec3d, float f, Fog fog) {
		int i = this.client.options.getClampedViewDistance() * 16;
		float g = this.client.gameRenderer.getFarPlaneDistance();
		RenderPass renderPass = frameGraphBuilder.createPass("weather");
		if (this.framebufferSet.weatherFramebuffer != null) {
			this.framebufferSet.weatherFramebuffer = renderPass.transfer(this.framebufferSet.weatherFramebuffer);
		} else {
			this.framebufferSet.mainFramebuffer = renderPass.transfer(this.framebufferSet.mainFramebuffer);
		}

		renderPass.setRenderer(() -> {
			RenderSystem.setShaderFog(fog);
			RenderPhase.WEATHER_TARGET.startDrawing();
			this.weatherRendering.method_62316(this.client.world, lightmapTextureManager, this.ticks, f, vec3d);
			this.worldBorderRendering.render(this.world.getWorldBorder(), vec3d, (double)i, (double)g);
			RenderPhase.WEATHER_TARGET.endDrawing();
		});
	}

	private void renderLateDebug(FrameGraphBuilder frameGraphBuilder, Vec3d vec3d, Fog fog) {
		RenderPass renderPass = frameGraphBuilder.createPass("late_debug");
		this.framebufferSet.mainFramebuffer = renderPass.transfer(this.framebufferSet.mainFramebuffer);
		if (this.framebufferSet.itemEntityFramebuffer != null) {
			this.framebufferSet.itemEntityFramebuffer = renderPass.transfer(this.framebufferSet.itemEntityFramebuffer);
		}

		Handle<Framebuffer> handle = this.framebufferSet.mainFramebuffer;
		renderPass.setRenderer(() -> {
			RenderSystem.setShaderFog(fog);
			handle.get().beginWrite(false);
			MatrixStack matrixStack = new MatrixStack();
			VertexConsumerProvider.Immediate immediate = this.bufferBuilders.getEntityVertexConsumers();
			this.client.debugRenderer.renderLate(matrixStack, immediate, vec3d.x, vec3d.y, vec3d.z);
			immediate.drawCurrentLayer();
			this.checkEmpty(matrixStack);
		});
	}

	private boolean getEntitiesToRender(Camera camera, Frustum frustum, List<Entity> output) {
		Vec3d vec3d = camera.getPos();
		double d = vec3d.getX();
		double e = vec3d.getY();
		double f = vec3d.getZ();
		boolean bl = false;
		boolean bl2 = this.canDrawEntityOutlines();
		Entity.setRenderDistanceMultiplier(
			MathHelper.clamp((double)this.client.options.getClampedViewDistance() / 8.0, 1.0, 2.5) * this.client.options.getEntityDistanceScaling().getValue()
		);

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
					output.add(entity);
					if (bl2 && this.client.hasOutline(entity)) {
						bl = true;
					}
				}
			}
		}

		return bl;
	}

	private void renderEntities(
		MatrixStack matrices, VertexConsumerProvider.Immediate immediate, Camera camera, RenderTickCounter tickCounter, List<Entity> entities
	) {
		Vec3d vec3d = camera.getPos();
		double d = vec3d.getX();
		double e = vec3d.getY();
		double f = vec3d.getZ();
		TickManager tickManager = this.client.world.getTickManager();
		boolean bl = this.canDrawEntityOutlines();

		for (Entity entity : entities) {
			if (entity.age == 0) {
				entity.lastRenderX = entity.getX();
				entity.lastRenderY = entity.getY();
				entity.lastRenderZ = entity.getZ();
			}

			VertexConsumerProvider vertexConsumerProvider;
			if (bl && this.client.hasOutline(entity)) {
				OutlineVertexConsumerProvider outlineVertexConsumerProvider = this.bufferBuilders.getOutlineVertexConsumers();
				vertexConsumerProvider = outlineVertexConsumerProvider;
				int i = entity.getTeamColorValue();
				outlineVertexConsumerProvider.setColor(ColorHelper.getRed(i), ColorHelper.getGreen(i), ColorHelper.getBlue(i), 255);
			} else {
				vertexConsumerProvider = immediate;
			}

			float g = tickCounter.getTickDelta(!tickManager.shouldSkipTick(entity));
			this.renderEntity(entity, d, e, f, g, matrices, vertexConsumerProvider);
		}
	}

	private void renderBlockEntities(
		MatrixStack matrices, VertexConsumerProvider.Immediate immediate, VertexConsumerProvider.Immediate immediate2, Camera camera, float tickDelta
	) {
		Vec3d vec3d = camera.getPos();
		double d = vec3d.getX();
		double e = vec3d.getY();
		double f = vec3d.getZ();

		for (ChunkBuilder.BuiltChunk builtChunk : this.builtChunks) {
			List<BlockEntity> list = builtChunk.getData().getBlockEntities();
			if (!list.isEmpty()) {
				for (BlockEntity blockEntity : list) {
					BlockPos blockPos = blockEntity.getPos();
					VertexConsumerProvider vertexConsumerProvider = immediate;
					matrices.push();
					matrices.translate((double)blockPos.getX() - d, (double)blockPos.getY() - e, (double)blockPos.getZ() - f);
					SortedSet<BlockBreakingInfo> sortedSet = this.blockBreakingProgressions.get(blockPos.asLong());
					if (sortedSet != null && !sortedSet.isEmpty()) {
						int i = ((BlockBreakingInfo)sortedSet.last()).getStage();
						if (i >= 0) {
							MatrixStack.Entry entry = matrices.peek();
							VertexConsumer vertexConsumer = new OverlayVertexConsumer(
								immediate2.getBuffer((RenderLayer)ModelBaker.BLOCK_DESTRUCTION_RENDER_LAYERS.get(i)), entry, 1.0F
							);
							vertexConsumerProvider = renderLayer -> {
								VertexConsumer vertexConsumer2 = immediate.getBuffer(renderLayer);
								return renderLayer.hasCrumbling() ? VertexConsumers.union(vertexConsumer, vertexConsumer2) : vertexConsumer2;
							};
						}
					}

					this.blockEntityRenderDispatcher.render(blockEntity, tickDelta, matrices, vertexConsumerProvider);
					matrices.pop();
				}
			}
		}

		synchronized (this.noCullingBlockEntities) {
			for (BlockEntity blockEntity2 : this.noCullingBlockEntities) {
				BlockPos blockPos2 = blockEntity2.getPos();
				matrices.push();
				matrices.translate((double)blockPos2.getX() - d, (double)blockPos2.getY() - e, (double)blockPos2.getZ() - f);
				this.blockEntityRenderDispatcher.render(blockEntity2, tickDelta, matrices, immediate);
				matrices.pop();
			}
		}
	}

	private void renderBlockDamage(MatrixStack matrices, Camera camera, VertexConsumerProvider.Immediate immediate) {
		Vec3d vec3d = camera.getPos();
		double d = vec3d.getX();
		double e = vec3d.getY();
		double f = vec3d.getZ();

		for (Entry<SortedSet<BlockBreakingInfo>> entry : this.blockBreakingProgressions.long2ObjectEntrySet()) {
			BlockPos blockPos = BlockPos.fromLong(entry.getLongKey());
			if (!(blockPos.getSquaredDistanceFromCenter(d, e, f) > 1024.0)) {
				SortedSet<BlockBreakingInfo> sortedSet = (SortedSet<BlockBreakingInfo>)entry.getValue();
				if (sortedSet != null && !sortedSet.isEmpty()) {
					int i = ((BlockBreakingInfo)sortedSet.last()).getStage();
					matrices.push();
					matrices.translate((double)blockPos.getX() - d, (double)blockPos.getY() - e, (double)blockPos.getZ() - f);
					MatrixStack.Entry entry2 = matrices.peek();
					VertexConsumer vertexConsumer = new OverlayVertexConsumer(
						immediate.getBuffer((RenderLayer)ModelBaker.BLOCK_DESTRUCTION_RENDER_LAYERS.get(i)), entry2, 1.0F
					);
					this.client.getBlockRenderManager().renderDamage(this.world.getBlockState(blockPos), blockPos, this.world, matrices, vertexConsumer);
					matrices.pop();
				}
			}
		}
	}

	private void renderTargetBlockOutline(Camera camera, VertexConsumerProvider.Immediate vertexConsumers, MatrixStack matrices, boolean bl) {
		if (this.client.crosshairTarget instanceof BlockHitResult blockHitResult) {
			if (blockHitResult.getType() != HitResult.Type.MISS) {
				BlockPos blockPos = blockHitResult.getBlockPos();
				BlockState blockState = this.world.getBlockState(blockPos);
				if (!blockState.isAir() && this.world.getWorldBorder().contains(blockPos)) {
					boolean bl2 = RenderLayers.getBlockLayer(blockState).isTranslucent();
					if (bl2 != bl) {
						return;
					}

					VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getLines());
					Vec3d vec3d = camera.getPos();
					this.drawBlockOutline(matrices, vertexConsumer, camera.getFocusedEntity(), vec3d.x, vec3d.y, vec3d.z, blockPos, blockState);
					vertexConsumers.drawCurrentLayer();
				}
			}
		}
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
		this.entityRenderDispatcher
			.render(entity, d - cameraX, e - cameraY, f - cameraZ, tickDelta, matrices, vertexConsumers, this.entityRenderDispatcher.getLight(entity, tickDelta));
	}

	private void method_62198(Vec3d vec3d, RenderLayer renderLayer) {
		if (this.field_53074 == null || !(vec3d.squaredDistanceTo(this.field_53074) <= 1.0)) {
			this.client.getProfiler().push("translucent_sort");
			int i = ChunkSectionPos.getSectionCoord(vec3d.x);
			int j = ChunkSectionPos.getSectionCoord(vec3d.y);
			int k = ChunkSectionPos.getSectionCoord(vec3d.z);
			boolean bl = this.field_53074 == null
				|| i != ChunkSectionPos.getSectionCoord(this.field_53074.x)
				|| k != ChunkSectionPos.getSectionCoord(this.field_53074.y)
				|| j != ChunkSectionPos.getSectionCoord(this.field_53074.z);
			this.field_53074 = vec3d;
			int l = 0;

			for (ChunkBuilder.BuiltChunk builtChunk : this.builtChunks) {
				if (l < 15 && (bl || builtChunk.method_52841(i, j, k)) && builtChunk.scheduleSort(renderLayer, this.chunkBuilder)) {
					l++;
				}
			}

			this.client.getProfiler().pop();
		}
	}

	private void renderLayer(RenderLayer renderLayer, double x, double y, double z, Matrix4f viewMatrix, Matrix4f positionMatrix) {
		RenderSystem.assertOnRenderThread();
		this.client.getProfiler().push((Supplier<String>)(() -> "render_" + renderLayer));
		boolean bl = renderLayer != RenderLayer.getTranslucent();
		ObjectListIterator<ChunkBuilder.BuiltChunk> objectListIterator = this.builtChunks.listIterator(bl ? 0 : this.builtChunks.size());
		renderLayer.startDrawing();
		ShaderProgram shaderProgram = RenderSystem.getShader();
		shaderProgram.initializeUniforms(VertexFormat.DrawMode.QUADS, viewMatrix, positionMatrix, this.client.getWindow());
		shaderProgram.bind();
		GlUniform glUniform = shaderProgram.modelOffset;

		while (bl ? objectListIterator.hasNext() : objectListIterator.hasPrevious()) {
			ChunkBuilder.BuiltChunk builtChunk = bl ? (ChunkBuilder.BuiltChunk)objectListIterator.next() : objectListIterator.previous();
			if (!builtChunk.getData().isEmpty(renderLayer)) {
				VertexBuffer vertexBuffer = builtChunk.getBuffer(renderLayer);
				BlockPos blockPos = builtChunk.getOrigin();
				if (glUniform != null) {
					glUniform.set((float)((double)blockPos.getX() - x), (float)((double)blockPos.getY() - y), (float)((double)blockPos.getZ() - z));
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

	public void captureFrustum() {
		this.shouldCaptureFrustum = true;
	}

	public void killFrustum() {
		this.capturedFrustum = null;
	}

	public void tick() {
		if (this.world.getTickManager().shouldTick()) {
			this.ticks++;
		}

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

	private void renderSky(FrameGraphBuilder frameGraphBuilder, Camera camera, float tickDelta, Fog fog) {
		CameraSubmersionType cameraSubmersionType = camera.getSubmersionType();
		if (cameraSubmersionType != CameraSubmersionType.POWDER_SNOW && cameraSubmersionType != CameraSubmersionType.LAVA && !this.hasBlindnessOrDarkness(camera)) {
			DimensionEffects dimensionEffects = this.world.getDimensionEffects();
			DimensionEffects.SkyType skyType = dimensionEffects.getSkyType();
			if (skyType != DimensionEffects.SkyType.NONE) {
				RenderPass renderPass = frameGraphBuilder.createPass("sky");
				this.framebufferSet.mainFramebuffer = renderPass.transfer(this.framebufferSet.mainFramebuffer);
				renderPass.setRenderer(() -> {
					RenderSystem.setShaderFog(fog);
					RenderPhase.MAIN_TARGET.startDrawing();
					MatrixStack matrixStack = new MatrixStack();
					if (skyType == DimensionEffects.SkyType.END) {
						this.skyRendering.renderEndSky(matrixStack);
					} else {
						Tessellator tessellator = Tessellator.getInstance();
						float g = this.world.getSkyAngleRadians(tickDelta);
						float h = this.world.getSkyAngle(tickDelta);
						float i = 1.0F - this.world.getRainGradient(tickDelta);
						float j = this.world.getStarBrightness(tickDelta) * i;
						int k = dimensionEffects.getSkyColor(h);
						int l = this.world.getMoonPhase();
						int m = this.world.getSkyColor(this.client.gameRenderer.getCamera().getPos(), tickDelta);
						float n = ColorHelper.floatFromChannel(ColorHelper.getRed(m));
						float o = ColorHelper.floatFromChannel(ColorHelper.getGreen(m));
						float p = ColorHelper.floatFromChannel(ColorHelper.getBlue(m));
						this.skyRendering.renderSky(n, o, p);
						if (dimensionEffects.method_62183(h)) {
							this.skyRendering.method_62306(matrixStack, tessellator, g, k);
						}

						this.skyRendering.renderCelestialBodies(matrixStack, tessellator, h, l, i, j, fog);
						if (this.isSkyDark(tickDelta)) {
							this.skyRendering.renderSkyDark(matrixStack);
						}
					}
				});
			}
		}
	}

	private boolean isSkyDark(float tickDelta) {
		return this.client.player.getCameraPosVec(tickDelta).y - this.world.getLevelProperties().getSkyDarknessHeight(this.world) < 0.0;
	}

	private boolean hasBlindnessOrDarkness(Camera camera) {
		return !(camera.getFocusedEntity() instanceof LivingEntity livingEntity)
			? false
			: livingEntity.hasStatusEffect(StatusEffects.BLINDNESS) || livingEntity.hasStatusEffect(StatusEffects.DARKNESS);
	}

	private void updateChunks(Camera camera) {
		this.client.getProfiler().push("populate_sections_to_compile");
		LightingProvider lightingProvider = this.world.getLightingProvider();
		ChunkRendererRegionBuilder chunkRendererRegionBuilder = new ChunkRendererRegionBuilder();
		BlockPos blockPos = camera.getBlockPos();
		List<ChunkBuilder.BuiltChunk> list = Lists.<ChunkBuilder.BuiltChunk>newArrayList();

		for (ChunkBuilder.BuiltChunk builtChunk : this.builtChunks) {
			long l = builtChunk.method_62975();
			if (builtChunk.needsRebuild() && builtChunk.shouldBuild() && method_62910(lightingProvider, l)) {
				boolean bl = false;
				if (this.client.options.getChunkBuilderMode().getValue() == ChunkBuilderMode.NEARBY) {
					BlockPos blockPos2 = builtChunk.getOrigin().add(8, 8, 8);
					bl = blockPos2.getSquaredDistance(blockPos) < 768.0 || builtChunk.needsImportantRebuild();
				} else if (this.client.options.getChunkBuilderMode().getValue() == ChunkBuilderMode.PLAYER_AFFECTED) {
					bl = builtChunk.needsImportantRebuild();
				}

				if (bl) {
					this.client.getProfiler().push("build_near_sync");
					this.chunkBuilder.rebuild(builtChunk, chunkRendererRegionBuilder);
					builtChunk.cancelRebuild();
					this.client.getProfiler().pop();
				} else {
					list.add(builtChunk);
				}
			}
		}

		this.client.getProfiler().swap("upload");
		this.chunkBuilder.upload();
		this.client.getProfiler().swap("schedule_async_compile");

		for (ChunkBuilder.BuiltChunk builtChunkx : list) {
			builtChunkx.scheduleRebuild(this.chunkBuilder, chunkRendererRegionBuilder);
			builtChunkx.cancelRebuild();
		}

		this.client.getProfiler().pop();
		this.method_62198(camera.getPos(), RenderLayer.getTranslucent());
	}

	private static boolean method_62910(LightingProvider lightingProvider, long l) {
		int i = ChunkSectionPos.unpackZ(l);
		int j = ChunkSectionPos.unpackX(l);

		for (int k = i - 1; k <= i + 1; k++) {
			for (int m = j - 1; m <= j + 1; m++) {
				if (!lightingProvider.isLightingEnabled(ChunkSectionPos.withZeroY(m, k))) {
					return false;
				}
			}
		}

		return true;
	}

	private void drawBlockOutline(
		MatrixStack matrices, VertexConsumer vertexConsumer, Entity entity, double cameraX, double cameraY, double cameraZ, BlockPos pos, BlockState state
	) {
		VertexRendering.drawOutline(
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

	public void scheduleBlockRenders(int i, int j, int k) {
		this.method_62219(i - 1, j - 1, k - 1, i + 1, j + 1, k + 1);
	}

	public void method_62219(int i, int j, int k, int l, int m, int n) {
		for (int o = k; o <= n; o++) {
			for (int p = i; p <= l; p++) {
				for (int q = j; q <= m; q++) {
					this.scheduleBlockRender(p, q, o);
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

	public void method_62908(long l) {
		ChunkBuilder.BuiltChunk builtChunk = this.chunks.method_62963(l);
		if (builtChunk != null) {
			this.chunkRenderingDataPreparer.method_52827(builtChunk);
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
			crashReportSection.add(
				"Parameters",
				(CrashCallable<String>)(() -> ParticleTypes.TYPE_CODEC.encodeStart(this.world.getRegistryManager().getOps(NbtOps.INSTANCE), parameters).toString())
			);
			crashReportSection.add("Position", (CrashCallable<String>)(() -> CrashReportSection.createPositionString(this.world, x, y, z)));
			throw new CrashException(crashReport);
		}
	}

	public <T extends ParticleEffect> void addParticle(T parameters, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
		this.addParticle(parameters, parameters.getType().shouldAlwaysSpawn(), x, y, z, velocityX, velocityY, velocityZ);
	}

	@Nullable
	public Particle spawnParticle(
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
		return this.chunkBuilder.isEmpty();
	}

	public void method_52815(ChunkPos chunkPos) {
		this.chunkRenderingDataPreparer.method_52819(chunkPos);
	}

	public void scheduleTerrainUpdate() {
		this.chunkRenderingDataPreparer.scheduleTerrainUpdate();
		this.cloudRenderer.scheduleTerrainUpdate();
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
		return this.framebufferSet.entityOutlineFramebuffer != null ? this.framebufferSet.entityOutlineFramebuffer.get() : null;
	}

	@Nullable
	public Framebuffer getTranslucentFramebuffer() {
		return this.framebufferSet.translucentFramebuffer != null ? this.framebufferSet.translucentFramebuffer.get() : null;
	}

	@Nullable
	public Framebuffer getEntityFramebuffer() {
		return this.framebufferSet.itemEntityFramebuffer != null ? this.framebufferSet.itemEntityFramebuffer.get() : null;
	}

	@Nullable
	public Framebuffer getParticlesFramebuffer() {
		return this.framebufferSet.particlesFramebuffer != null ? this.framebufferSet.particlesFramebuffer.get() : null;
	}

	@Nullable
	public Framebuffer getWeatherFramebuffer() {
		return this.framebufferSet.weatherFramebuffer != null ? this.framebufferSet.weatherFramebuffer.get() : null;
	}

	@Nullable
	public Framebuffer getCloudsFramebuffer() {
		return this.framebufferSet.cloudsFramebuffer != null ? this.framebufferSet.cloudsFramebuffer.get() : null;
	}

	@Debug
	public ObjectArrayList<ChunkBuilder.BuiltChunk> getBuiltChunks() {
		return this.builtChunks;
	}

	@Debug
	public ChunkRenderingDataPreparer getChunkRenderingDataPreparer() {
		return this.chunkRenderingDataPreparer;
	}

	@Nullable
	public Frustum getCapturedFrustum() {
		return this.capturedFrustum;
	}

	public CloudRenderer getCloudRenderer() {
		return this.cloudRenderer;
	}

	@Environment(EnvType.CLIENT)
	public static class ProgramInitException extends RuntimeException {
		public ProgramInitException(String message) {
			super(message);
		}
	}
}
