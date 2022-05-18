/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.platform.GlConst;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.SortedSet;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
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
import net.minecraft.client.gl.ShaderEffect;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.CloudRenderMode;
import net.minecraft.client.option.GraphicsMode;
import net.minecraft.client.option.ParticlesMode;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.BlockBreakingInfo;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferBuilderStorage;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.BuiltChunkStorage;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.CameraSubmersionType;
import net.minecraft.client.render.ChunkBuilderMode;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.DimensionEffects;
import net.minecraft.client.render.FpsSmoother;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OutlineVertexConsumerProvider;
import net.minecraft.client.render.OverlayVertexConsumer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.render.Shader;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexConsumers;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
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
import net.minecraft.client.util.math.Vector3d;
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
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.particle.SculkChargeParticleEffect;
import net.minecraft.particle.ShriekParticleEffect;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SynchronousResourceReloader;
import net.minecraft.server.world.ThreadedAnvilChunkStorage;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.FluidTags;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix3f;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.Vector4f;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.BlockView;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.border.WorldBorder;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class WorldRenderer
implements SynchronousResourceReloader,
AutoCloseable {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final int field_32759 = 16;
    private static final int field_34812 = 8;
    private static final float field_32762 = 512.0f;
    private static final int field_34813 = 60;
    private static final double field_34814 = Math.ceil(Math.sqrt(3.0) * 16.0);
    private static final int field_32763 = 32;
    private static final int field_32764 = 10;
    private static final int field_32765 = 21;
    private static final int field_32766 = 15;
    private static final int field_34815 = 500;
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
    private final BlockingQueue<ChunkBuilder.BuiltChunk> builtChunks = new LinkedBlockingQueue<ChunkBuilder.BuiltChunk>();
    private final AtomicReference<RenderableChunks> renderableChunks = new AtomicReference();
    private final ObjectArrayList<ChunkInfo> chunkInfos = new ObjectArrayList(10000);
    private final Set<BlockEntity> noCullingBlockEntities = Sets.newHashSet();
    @Nullable
    private Future<?> fullUpdateFuture;
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
    private final Int2ObjectMap<BlockBreakingInfo> blockBreakingInfos = new Int2ObjectOpenHashMap<BlockBreakingInfo>();
    private final Long2ObjectMap<SortedSet<BlockBreakingInfo>> blockBreakingProgressions = new Long2ObjectOpenHashMap<SortedSet<BlockBreakingInfo>>();
    private final Map<BlockPos, SoundInstance> playingSongs = Maps.newHashMap();
    @Nullable
    private Framebuffer entityOutlinesFramebuffer;
    @Nullable
    private ShaderEffect entityOutlineShader;
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
    private ShaderEffect transparencyShader;
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
    private int lastCloudsBlockX = Integer.MIN_VALUE;
    private int lastCloudsBlockY = Integer.MIN_VALUE;
    private int lastCloudsBlockZ = Integer.MIN_VALUE;
    private Vec3d lastCloudsColor = Vec3d.ZERO;
    @Nullable
    private CloudRenderMode lastCloudsRenderMode;
    @Nullable
    private ChunkBuilder chunkBuilder;
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
    private boolean shouldUpdate = true;
    private final AtomicLong nextUpdateTime = new AtomicLong(0L);
    private final AtomicBoolean updateFinished = new AtomicBoolean(false);
    private int rainSoundCounter;
    private final float[] field_20794 = new float[1024];
    private final float[] field_20795 = new float[1024];

    public WorldRenderer(MinecraftClient client, EntityRenderDispatcher entityRenderDispatcher, BlockEntityRenderDispatcher blockEntityRenderDispatcher, BufferBuilderStorage bufferBuilders) {
        this.client = client;
        this.entityRenderDispatcher = entityRenderDispatcher;
        this.blockEntityRenderDispatcher = blockEntityRenderDispatcher;
        this.bufferBuilders = bufferBuilders;
        for (int i = 0; i < 32; ++i) {
            for (int j = 0; j < 32; ++j) {
                float f = j - 16;
                float g = i - 16;
                float h = MathHelper.sqrt(f * f + g * g);
                this.field_20794[i << 5 | j] = -g / h;
                this.field_20795[i << 5 | j] = f / h;
            }
        }
        this.renderStars();
        this.renderLightSky();
        this.renderDarkSky();
    }

    private void renderWeather(LightmapTextureManager manager, float tickDelta, double cameraX, double cameraY, double cameraZ) {
        float f = this.client.world.getRainGradient(tickDelta);
        if (f <= 0.0f) {
            return;
        }
        manager.enable();
        ClientWorld world = this.client.world;
        int i = MathHelper.floor(cameraX);
        int j = MathHelper.floor(cameraY);
        int k = MathHelper.floor(cameraZ);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        RenderSystem.disableCull();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        int l = 5;
        if (MinecraftClient.isFancyGraphicsOrBetter()) {
            l = 10;
        }
        RenderSystem.depthMask(MinecraftClient.isFabulousGraphicsOrBetter());
        int m = -1;
        float g = (float)this.ticks + tickDelta;
        RenderSystem.setShader(GameRenderer::getParticleShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        for (int n = k - l; n <= k + l; ++n) {
            for (int o = i - l; o <= i + l; ++o) {
                float y;
                float h;
                int t;
                int p = (n - k + 16) * 32 + o - i + 16;
                double d = (double)this.field_20794[p] * 0.5;
                double e = (double)this.field_20795[p] * 0.5;
                mutable.set((double)o, cameraY, (double)n);
                Biome biome = world.getBiome(mutable).value();
                if (biome.getPrecipitation() == Biome.Precipitation.NONE) continue;
                int q = world.getTopY(Heightmap.Type.MOTION_BLOCKING, o, n);
                int r = j - l;
                int s = j + l;
                if (r < q) {
                    r = q;
                }
                if (s < q) {
                    s = q;
                }
                if ((t = q) < j) {
                    t = j;
                }
                if (r == s) continue;
                Random random = Random.create(o * o * 3121 + o * 45238971 ^ n * n * 418711 + n * 13761);
                mutable.set(o, r, n);
                if (biome.doesNotSnow(mutable)) {
                    if (m != 0) {
                        if (m >= 0) {
                            tessellator.draw();
                        }
                        m = 0;
                        RenderSystem.setShaderTexture(0, RAIN);
                        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR_LIGHT);
                    }
                    int u = this.ticks + o * o * 3121 + o * 45238971 + n * n * 418711 + n * 13761 & 0x1F;
                    h = -((float)u + tickDelta) / 32.0f * (3.0f + random.nextFloat());
                    double v = (double)o + 0.5 - cameraX;
                    double w = (double)n + 0.5 - cameraZ;
                    float x = (float)Math.sqrt(v * v + w * w) / (float)l;
                    y = ((1.0f - x * x) * 0.5f + 0.5f) * f;
                    mutable.set(o, t, n);
                    int z = WorldRenderer.getLightmapCoordinates(world, mutable);
                    bufferBuilder.vertex((double)o - cameraX - d + 0.5, (double)s - cameraY, (double)n - cameraZ - e + 0.5).texture(0.0f, (float)r * 0.25f + h).color(1.0f, 1.0f, 1.0f, y).light(z).next();
                    bufferBuilder.vertex((double)o - cameraX + d + 0.5, (double)s - cameraY, (double)n - cameraZ + e + 0.5).texture(1.0f, (float)r * 0.25f + h).color(1.0f, 1.0f, 1.0f, y).light(z).next();
                    bufferBuilder.vertex((double)o - cameraX + d + 0.5, (double)r - cameraY, (double)n - cameraZ + e + 0.5).texture(1.0f, (float)s * 0.25f + h).color(1.0f, 1.0f, 1.0f, y).light(z).next();
                    bufferBuilder.vertex((double)o - cameraX - d + 0.5, (double)r - cameraY, (double)n - cameraZ - e + 0.5).texture(0.0f, (float)s * 0.25f + h).color(1.0f, 1.0f, 1.0f, y).light(z).next();
                    continue;
                }
                if (m != 1) {
                    if (m >= 0) {
                        tessellator.draw();
                    }
                    m = 1;
                    RenderSystem.setShaderTexture(0, SNOW);
                    bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR_LIGHT);
                }
                float aa = -((float)(this.ticks & 0x1FF) + tickDelta) / 512.0f;
                h = (float)(random.nextDouble() + (double)g * 0.01 * (double)((float)random.nextGaussian()));
                float ab = (float)(random.nextDouble() + (double)(g * (float)random.nextGaussian()) * 0.001);
                double ac = (double)o + 0.5 - cameraX;
                double ad = (double)n + 0.5 - cameraZ;
                y = (float)Math.sqrt(ac * ac + ad * ad) / (float)l;
                float ae = ((1.0f - y * y) * 0.3f + 0.5f) * f;
                mutable.set(o, t, n);
                int af = WorldRenderer.getLightmapCoordinates(world, mutable);
                int ag = af >> 16 & 0xFFFF;
                int ah = af & 0xFFFF;
                int ai = (ag * 3 + 240) / 4;
                int aj = (ah * 3 + 240) / 4;
                bufferBuilder.vertex((double)o - cameraX - d + 0.5, (double)s - cameraY, (double)n - cameraZ - e + 0.5).texture(0.0f + h, (float)r * 0.25f + aa + ab).color(1.0f, 1.0f, 1.0f, ae).light(aj, ai).next();
                bufferBuilder.vertex((double)o - cameraX + d + 0.5, (double)s - cameraY, (double)n - cameraZ + e + 0.5).texture(1.0f + h, (float)r * 0.25f + aa + ab).color(1.0f, 1.0f, 1.0f, ae).light(aj, ai).next();
                bufferBuilder.vertex((double)o - cameraX + d + 0.5, (double)r - cameraY, (double)n - cameraZ + e + 0.5).texture(1.0f + h, (float)s * 0.25f + aa + ab).color(1.0f, 1.0f, 1.0f, ae).light(aj, ai).next();
                bufferBuilder.vertex((double)o - cameraX - d + 0.5, (double)r - cameraY, (double)n - cameraZ - e + 0.5).texture(0.0f + h, (float)s * 0.25f + aa + ab).color(1.0f, 1.0f, 1.0f, ae).light(aj, ai).next();
            }
        }
        if (m >= 0) {
            tessellator.draw();
        }
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
        manager.disable();
    }

    public void tickRainSplashing(Camera camera) {
        float f = this.client.world.getRainGradient(1.0f) / (MinecraftClient.isFancyGraphicsOrBetter() ? 1.0f : 2.0f);
        if (f <= 0.0f) {
            return;
        }
        Random random = Random.create((long)this.ticks * 312987231L);
        ClientWorld worldView = this.client.world;
        BlockPos blockPos = new BlockPos(camera.getPos());
        Vec3i blockPos2 = null;
        int i = (int)(100.0f * f * f) / (this.client.options.getParticles().getValue() == ParticlesMode.DECREASED ? 2 : 1);
        for (int j = 0; j < i; ++j) {
            int k = random.nextInt(21) - 10;
            int l = random.nextInt(21) - 10;
            BlockPos blockPos3 = worldView.getTopPosition(Heightmap.Type.MOTION_BLOCKING, blockPos.add(k, 0, l));
            Biome biome = worldView.getBiome(blockPos3).value();
            if (blockPos3.getY() <= worldView.getBottomY() || blockPos3.getY() > blockPos.getY() + 10 || blockPos3.getY() < blockPos.getY() - 10 || biome.getPrecipitation() != Biome.Precipitation.RAIN || !biome.doesNotSnow(blockPos3)) continue;
            blockPos2 = blockPos3.down();
            if (this.client.options.getParticles().getValue() == ParticlesMode.MINIMAL) break;
            double d = random.nextDouble();
            double e = random.nextDouble();
            BlockState blockState = worldView.getBlockState((BlockPos)blockPos2);
            FluidState fluidState = worldView.getFluidState((BlockPos)blockPos2);
            VoxelShape voxelShape = blockState.getCollisionShape(worldView, (BlockPos)blockPos2);
            double g = voxelShape.getEndingCoord(Direction.Axis.Y, d, e);
            double h = fluidState.getHeight(worldView, (BlockPos)blockPos2);
            double m = Math.max(g, h);
            DefaultParticleType particleEffect = fluidState.isIn(FluidTags.LAVA) || blockState.isOf(Blocks.MAGMA_BLOCK) || CampfireBlock.isLitCampfire(blockState) ? ParticleTypes.SMOKE : ParticleTypes.RAIN;
            this.client.world.addParticle(particleEffect, (double)blockPos2.getX() + d, (double)blockPos2.getY() + m, (double)blockPos2.getZ() + e, 0.0, 0.0, 0.0);
        }
        if (blockPos2 != null && random.nextInt(3) < this.rainSoundCounter++) {
            this.rainSoundCounter = 0;
            if (blockPos2.getY() > blockPos.getY() + 1 && worldView.getTopPosition(Heightmap.Type.MOTION_BLOCKING, blockPos).getY() > MathHelper.floor(blockPos.getY())) {
                this.client.world.playSound((BlockPos)blockPos2, SoundEvents.WEATHER_RAIN_ABOVE, SoundCategory.WEATHER, 0.1f, 0.5f, false);
            } else {
                this.client.world.playSound((BlockPos)blockPos2, SoundEvents.WEATHER_RAIN, SoundCategory.WEATHER, 0.2f, 1.0f, false);
            }
        }
    }

    @Override
    public void close() {
        if (this.entityOutlineShader != null) {
            this.entityOutlineShader.close();
        }
        if (this.transparencyShader != null) {
            this.transparencyShader.close();
        }
    }

    @Override
    public void reload(ResourceManager manager) {
        this.loadEntityOutlineShader();
        if (MinecraftClient.isFabulousGraphicsOrBetter()) {
            this.loadTransparencyShader();
        }
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
        } catch (IOException iOException) {
            LOGGER.warn("Failed to load shader: {}", (Object)identifier, (Object)iOException);
            this.entityOutlineShader = null;
            this.entityOutlinesFramebuffer = null;
        } catch (JsonSyntaxException jsonSyntaxException) {
            LOGGER.warn("Failed to parse shader: {}", (Object)identifier, (Object)jsonSyntaxException);
            this.entityOutlineShader = null;
            this.entityOutlinesFramebuffer = null;
        }
    }

    private void loadTransparencyShader() {
        this.resetTransparencyShader();
        Identifier identifier = new Identifier("shaders/post/transparency.json");
        try {
            ShaderEffect shaderEffect = new ShaderEffect(this.client.getTextureManager(), this.client.getResourceManager(), this.client.getFramebuffer(), identifier);
            shaderEffect.setupDimensions(this.client.getWindow().getFramebufferWidth(), this.client.getWindow().getFramebufferHeight());
            Framebuffer framebuffer = shaderEffect.getSecondaryTarget("translucent");
            Framebuffer framebuffer2 = shaderEffect.getSecondaryTarget("itemEntity");
            Framebuffer framebuffer3 = shaderEffect.getSecondaryTarget("particles");
            Framebuffer framebuffer4 = shaderEffect.getSecondaryTarget("weather");
            Framebuffer framebuffer5 = shaderEffect.getSecondaryTarget("clouds");
            this.transparencyShader = shaderEffect;
            this.translucentFramebuffer = framebuffer;
            this.entityFramebuffer = framebuffer2;
            this.particlesFramebuffer = framebuffer3;
            this.weatherFramebuffer = framebuffer4;
            this.cloudsFramebuffer = framebuffer5;
        } catch (Exception exception) {
            String string = exception instanceof JsonSyntaxException ? "parse" : "load";
            String string2 = "Failed to " + string + " shader: " + identifier;
            ShaderException shaderException = new ShaderException(string2, exception);
            if (this.client.getResourcePackManager().getEnabledNames().size() > 1) {
                Text text = this.client.getResourceManager().streamResourcePacks().findFirst().map(resourcePack -> Text.literal(resourcePack.getName())).orElse(null);
                this.client.options.getGraphicsMode().setValue(GraphicsMode.FANCY);
                this.client.onResourceReloadFailure(shaderException, text);
            }
            CrashReport crashReport = this.client.addDetailsToCrashReport(new CrashReport(string2, shaderException));
            this.client.options.getGraphicsMode().setValue(GraphicsMode.FANCY);
            this.client.options.write();
            LOGGER.error(LogUtils.FATAL_MARKER, string2, shaderException);
            this.client.cleanUpAfterCrash();
            MinecraftClient.printCrashReport(crashReport);
        }
    }

    private void resetTransparencyShader() {
        if (this.transparencyShader != null) {
            this.transparencyShader.close();
            this.translucentFramebuffer.delete();
            this.entityFramebuffer.delete();
            this.particlesFramebuffer.delete();
            this.weatherFramebuffer.delete();
            this.cloudsFramebuffer.delete();
            this.transparencyShader = null;
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
            RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SrcFactor.ZERO, GlStateManager.DstFactor.ONE);
            this.entityOutlinesFramebuffer.draw(this.client.getWindow().getFramebufferWidth(), this.client.getWindow().getFramebufferHeight(), false);
            RenderSystem.disableBlend();
        }
    }

    protected boolean canDrawEntityOutlines() {
        return !this.client.gameRenderer.isRenderingPanorama() && this.entityOutlinesFramebuffer != null && this.entityOutlineShader != null && this.client.player != null;
    }

    private void renderDarkSky() {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        if (this.darkSkyBuffer != null) {
            this.darkSkyBuffer.close();
        }
        this.darkSkyBuffer = new VertexBuffer();
        BufferBuilder.BuiltBuffer builtBuffer = WorldRenderer.renderSky(bufferBuilder, -16.0f);
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
        this.lightSkyBuffer = new VertexBuffer();
        BufferBuilder.BuiltBuffer builtBuffer = WorldRenderer.renderSky(bufferBuilder, 16.0f);
        this.lightSkyBuffer.bind();
        this.lightSkyBuffer.upload(builtBuffer);
        VertexBuffer.unbind();
    }

    private static BufferBuilder.BuiltBuffer renderSky(BufferBuilder builder, float f) {
        float g = Math.signum(f) * 512.0f;
        float h = 512.0f;
        RenderSystem.setShader(GameRenderer::getPositionShader);
        builder.begin(VertexFormat.DrawMode.TRIANGLE_FAN, VertexFormats.POSITION);
        builder.vertex(0.0, f, 0.0).next();
        for (int i = -180; i <= 180; i += 45) {
            builder.vertex(g * MathHelper.cos((float)i * ((float)Math.PI / 180)), f, 512.0f * MathHelper.sin((float)i * ((float)Math.PI / 180))).next();
        }
        return builder.end();
    }

    private void renderStars() {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        RenderSystem.setShader(GameRenderer::getPositionShader);
        if (this.starsBuffer != null) {
            this.starsBuffer.close();
        }
        this.starsBuffer = new VertexBuffer();
        BufferBuilder.BuiltBuffer builtBuffer = this.renderStars(bufferBuilder);
        this.starsBuffer.bind();
        this.starsBuffer.upload(builtBuffer);
        VertexBuffer.unbind();
    }

    private BufferBuilder.BuiltBuffer renderStars(BufferBuilder buffer) {
        Random random = Random.create(10842L);
        buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION);
        for (int i = 0; i < 1500; ++i) {
            double d = random.nextFloat() * 2.0f - 1.0f;
            double e = random.nextFloat() * 2.0f - 1.0f;
            double f = random.nextFloat() * 2.0f - 1.0f;
            double g = 0.15f + random.nextFloat() * 0.1f;
            double h = d * d + e * e + f * f;
            if (!(h < 1.0) || !(h > 0.01)) continue;
            h = 1.0 / Math.sqrt(h);
            double j = (d *= h) * 100.0;
            double k = (e *= h) * 100.0;
            double l = (f *= h) * 100.0;
            double m = Math.atan2(d, f);
            double n = Math.sin(m);
            double o = Math.cos(m);
            double p = Math.atan2(Math.sqrt(d * d + f * f), e);
            double q = Math.sin(p);
            double r = Math.cos(p);
            double s = random.nextDouble() * Math.PI * 2.0;
            double t = Math.sin(s);
            double u = Math.cos(s);
            for (int v = 0; v < 4; ++v) {
                double ab;
                double w = 0.0;
                double x = (double)((v & 2) - 1) * g;
                double y = (double)((v + 1 & 2) - 1) * g;
                double z = 0.0;
                double aa = x * u - y * t;
                double ac = ab = y * u + x * t;
                double ad = aa * q + 0.0 * r;
                double ae = 0.0 * q - aa * r;
                double af = ae * n - ac * o;
                double ag = ad;
                double ah = ac * n + ae * o;
                buffer.vertex(j + af, k + ag, l + ah).next();
            }
        }
        return buffer.end();
    }

    public void setWorld(@Nullable ClientWorld world) {
        this.lastCameraChunkUpdateX = Double.MIN_VALUE;
        this.lastCameraChunkUpdateY = Double.MIN_VALUE;
        this.lastCameraChunkUpdateZ = Double.MIN_VALUE;
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
            this.renderableChunks.set(null);
            this.chunkInfos.clear();
        }
    }

    public void reloadTransparencyShader() {
        if (MinecraftClient.isFabulousGraphicsOrBetter()) {
            this.loadTransparencyShader();
        } else {
            this.resetTransparencyShader();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void reload() {
        if (this.world == null) {
            return;
        }
        this.reloadTransparencyShader();
        this.world.reloadColor();
        if (this.chunkBuilder == null) {
            this.chunkBuilder = new ChunkBuilder(this.world, this, Util.getMainWorkerExecutor(), this.client.is64Bit(), this.bufferBuilders.getBlockBufferBuilders());
        } else {
            this.chunkBuilder.setWorld(this.world);
        }
        this.shouldUpdate = true;
        this.cloudsDirty = true;
        this.builtChunks.clear();
        RenderLayers.setFancyGraphicsOrBetter(MinecraftClient.isFancyGraphicsOrBetter());
        this.viewDistance = this.client.options.getClampedViewDistance();
        if (this.chunks != null) {
            this.chunks.clear();
        }
        this.chunkBuilder.reset();
        Set<BlockEntity> set = this.noCullingBlockEntities;
        synchronized (set) {
            this.noCullingBlockEntities.clear();
        }
        this.chunks = new BuiltChunkStorage(this.chunkBuilder, this.world, this.client.options.getClampedViewDistance(), this);
        if (this.fullUpdateFuture != null) {
            try {
                this.fullUpdateFuture.get();
                this.fullUpdateFuture = null;
            } catch (Exception exception) {
                LOGGER.warn("Full update failed", exception);
            }
        }
        this.renderableChunks.set(new RenderableChunks(this.chunks.chunks.length));
        this.chunkInfos.clear();
        Entity entity = this.client.getCameraEntity();
        if (entity != null) {
            this.chunks.updateCameraPosition(entity.getX(), entity.getZ());
        }
    }

    public void onResized(int width, int height) {
        this.scheduleTerrainUpdate();
        if (this.entityOutlineShader != null) {
            this.entityOutlineShader.setupDimensions(width, height);
        }
        if (this.transparencyShader != null) {
            this.transparencyShader.setupDimensions(width, height);
        }
    }

    public String getChunksDebugString() {
        int i = this.chunks.chunks.length;
        int j = this.getCompletedChunkCount();
        return String.format("C: %d/%d %sD: %d, %s", j, i, this.client.chunkCullingEnabled ? "(s) " : "", this.viewDistance, this.chunkBuilder == null ? "null" : this.chunkBuilder.getDebugString());
    }

    public ChunkBuilder getChunkBuilder() {
        return this.chunkBuilder;
    }

    public double getChunkCount() {
        return this.chunks.chunks.length;
    }

    public double getViewDistance() {
        return this.viewDistance;
    }

    public int getCompletedChunkCount() {
        int i = 0;
        for (ChunkInfo chunkInfo : this.chunkInfos) {
            if (chunkInfo.chunk.getData().isEmpty()) continue;
            ++i;
        }
        return i;
    }

    public String getEntitiesDebugString() {
        return "E: " + this.regularEntityCount + "/" + this.world.getRegularEntityCount() + ", B: " + this.blockEntityCount + ", SD: " + this.world.getSimulationDistance();
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
            this.lastCameraChunkUpdateX = d;
            this.lastCameraChunkUpdateY = e;
            this.lastCameraChunkUpdateZ = f;
            this.cameraChunkX = i;
            this.cameraChunkY = j;
            this.cameraChunkZ = k;
            this.chunks.updateCameraPosition(d, f);
        }
        this.chunkBuilder.setCameraPosition(vec3d);
        this.world.getProfiler().swap("cull");
        this.client.getProfiler().swap("culling");
        BlockPos blockPos = camera.getBlockPos();
        double g = Math.floor(vec3d.x / 8.0);
        double h = Math.floor(vec3d.y / 8.0);
        double l = Math.floor(vec3d.z / 8.0);
        this.shouldUpdate = this.shouldUpdate || g != this.lastCameraX || h != this.lastCameraY || l != this.lastCameraZ;
        this.nextUpdateTime.updateAndGet(nextUpdateTime -> {
            if (nextUpdateTime > 0L && System.currentTimeMillis() > nextUpdateTime) {
                this.shouldUpdate = true;
                return 0L;
            }
            return nextUpdateTime;
        });
        this.lastCameraX = g;
        this.lastCameraY = h;
        this.lastCameraZ = l;
        this.client.getProfiler().swap("update");
        boolean bl = this.client.chunkCullingEnabled;
        if (spectator && this.world.getBlockState(blockPos).isOpaqueFullCube(this.world, blockPos)) {
            bl = false;
        }
        if (!hasForcedFrustum) {
            if (this.shouldUpdate && (this.fullUpdateFuture == null || this.fullUpdateFuture.isDone())) {
                this.client.getProfiler().push("full_update_schedule");
                this.shouldUpdate = false;
                boolean bl2 = bl;
                this.fullUpdateFuture = Util.getMainWorkerExecutor().submit(() -> {
                    ArrayDeque<ChunkInfo> queue = Queues.newArrayDeque();
                    this.enqueueChunksInViewDistance(camera, queue);
                    RenderableChunks renderableChunks = new RenderableChunks(this.chunks.chunks.length);
                    this.collectRenderableChunks(renderableChunks.chunks, renderableChunks.chunkInfoList, vec3d, queue, bl2);
                    this.renderableChunks.set(renderableChunks);
                    this.updateFinished.set(true);
                });
                this.client.getProfiler().pop();
            }
            RenderableChunks renderableChunks = this.renderableChunks.get();
            if (!this.builtChunks.isEmpty()) {
                this.client.getProfiler().push("partial_update");
                ArrayDeque<ChunkInfo> queue = Queues.newArrayDeque();
                while (!this.builtChunks.isEmpty()) {
                    ChunkBuilder.BuiltChunk builtChunk = (ChunkBuilder.BuiltChunk)this.builtChunks.poll();
                    ChunkInfo chunkInfo = renderableChunks.chunkInfoList.getInfo(builtChunk);
                    if (chunkInfo == null || chunkInfo.chunk != builtChunk) continue;
                    queue.add(chunkInfo);
                }
                this.collectRenderableChunks(renderableChunks.chunks, renderableChunks.chunkInfoList, vec3d, queue, bl);
                this.updateFinished.set(true);
                this.client.getProfiler().pop();
            }
            double m = Math.floor(camera.getPitch() / 2.0f);
            double n = Math.floor(camera.getYaw() / 2.0f);
            if (this.updateFinished.compareAndSet(true, false) || m != this.lastCameraPitch || n != this.lastCameraYaw) {
                this.applyFrustum(new Frustum(frustum).method_38557(8));
                this.lastCameraPitch = m;
                this.lastCameraYaw = n;
            }
        }
        this.client.getProfiler().pop();
    }

    private void applyFrustum(Frustum frustum) {
        if (!MinecraftClient.getInstance().isOnThread()) {
            throw new IllegalStateException("applyFrustum called from wrong thread: " + Thread.currentThread().getName());
        }
        this.client.getProfiler().push("apply_frustum");
        this.chunkInfos.clear();
        for (ChunkInfo chunkInfo : this.renderableChunks.get().chunks) {
            if (!frustum.isVisible(chunkInfo.chunk.getBoundingBox())) continue;
            this.chunkInfos.add(chunkInfo);
        }
        this.client.getProfiler().pop();
    }

    private void enqueueChunksInViewDistance(Camera camera, Queue<ChunkInfo> queue) {
        int i = 16;
        Vec3d vec3d = camera.getPos();
        BlockPos blockPos = camera.getBlockPos();
        ChunkBuilder.BuiltChunk builtChunk = this.chunks.getRenderedChunk(blockPos);
        if (builtChunk == null) {
            boolean bl = blockPos.getY() > this.world.getBottomY();
            int j = bl ? this.world.getTopY() - 8 : this.world.getBottomY() + 8;
            int k = MathHelper.floor(vec3d.x / 16.0) * 16;
            int l = MathHelper.floor(vec3d.z / 16.0) * 16;
            ArrayList<ChunkInfo> list = Lists.newArrayList();
            for (int m = -this.viewDistance; m <= this.viewDistance; ++m) {
                for (int n = -this.viewDistance; n <= this.viewDistance; ++n) {
                    ChunkBuilder.BuiltChunk builtChunk2 = this.chunks.getRenderedChunk(new BlockPos(k + ChunkSectionPos.getOffsetPos(m, 8), j, l + ChunkSectionPos.getOffsetPos(n, 8)));
                    if (builtChunk2 == null) continue;
                    list.add(new ChunkInfo(builtChunk2, null, 0));
                }
            }
            list.sort(Comparator.comparingDouble(chunkInfo -> blockPos.getSquaredDistance(chunkInfo.chunk.getOrigin().add(8, 8, 8))));
            queue.addAll(list);
        } else {
            queue.add(new ChunkInfo(builtChunk, null, 0));
        }
    }

    public void addBuiltChunk(ChunkBuilder.BuiltChunk chunk) {
        this.builtChunks.add(chunk);
    }

    private void collectRenderableChunks(LinkedHashSet<ChunkInfo> chunks, ChunkInfoList chunkInfoList, Vec3d cameraPos, Queue<ChunkInfo> queue, boolean chunkCullingEnabled) {
        int i = 16;
        BlockPos blockPos = new BlockPos(MathHelper.floor(cameraPos.x / 16.0) * 16, MathHelper.floor(cameraPos.y / 16.0) * 16, MathHelper.floor(cameraPos.z / 16.0) * 16);
        BlockPos blockPos2 = blockPos.add(8, 8, 8);
        Entity.setRenderDistanceMultiplier(MathHelper.clamp((double)this.client.options.getClampedViewDistance() / 8.0, 1.0, 2.5) * this.client.options.getEntityDistanceScaling().getValue());
        while (!queue.isEmpty()) {
            ChunkInfo chunkInfo = queue.poll();
            ChunkBuilder.BuiltChunk builtChunk = chunkInfo.chunk;
            chunks.add(chunkInfo);
            boolean bl = Math.abs(builtChunk.getOrigin().getX() - blockPos.getX()) > 60 || Math.abs(builtChunk.getOrigin().getY() - blockPos.getY()) > 60 || Math.abs(builtChunk.getOrigin().getZ() - blockPos.getZ()) > 60;
            for (Direction direction : DIRECTIONS) {
                ChunkInfo chunkInfo2;
                ChunkBuilder.BuiltChunk builtChunk2 = this.getAdjacentChunk(blockPos, builtChunk, direction);
                if (builtChunk2 == null || chunkCullingEnabled && chunkInfo.canCull(direction.getOpposite())) continue;
                if (chunkCullingEnabled && chunkInfo.hasAnyDirection()) {
                    ChunkBuilder.ChunkData chunkData = builtChunk.getData();
                    boolean bl2 = false;
                    for (int j = 0; j < DIRECTIONS.length; ++j) {
                        if (!chunkInfo.hasDirection(j) || !chunkData.isVisibleThrough(DIRECTIONS[j].getOpposite(), direction)) continue;
                        bl2 = true;
                        break;
                    }
                    if (!bl2) continue;
                }
                if (chunkCullingEnabled && bl) {
                    BlockPos blockPos3 = builtChunk2.getOrigin();
                    BlockPos blockPos4 = blockPos3.add((direction.getAxis() == Direction.Axis.X ? blockPos2.getX() > blockPos3.getX() : blockPos2.getX() < blockPos3.getX()) ? 16 : 0, (direction.getAxis() == Direction.Axis.Y ? blockPos2.getY() > blockPos3.getY() : blockPos2.getY() < blockPos3.getY()) ? 16 : 0, (direction.getAxis() == Direction.Axis.Z ? blockPos2.getZ() > blockPos3.getZ() : blockPos2.getZ() < blockPos3.getZ()) ? 16 : 0);
                    Vec3d vec3d = new Vec3d(blockPos4.getX(), blockPos4.getY(), blockPos4.getZ());
                    Vec3d vec3d2 = cameraPos.subtract(vec3d).normalize().multiply(field_34814);
                    boolean bl3 = true;
                    while (cameraPos.subtract(vec3d).lengthSquared() > 3600.0) {
                        vec3d = vec3d.add(vec3d2);
                        if (vec3d.y > (double)this.world.getTopY() || vec3d.y < (double)this.world.getBottomY()) break;
                        ChunkBuilder.BuiltChunk builtChunk3 = this.chunks.getRenderedChunk(new BlockPos(vec3d.x, vec3d.y, vec3d.z));
                        if (builtChunk3 != null && chunkInfoList.getInfo(builtChunk3) != null) continue;
                        bl3 = false;
                        break;
                    }
                    if (!bl3) continue;
                }
                if ((chunkInfo2 = chunkInfoList.getInfo(builtChunk2)) != null) {
                    chunkInfo2.addDirection(direction);
                    continue;
                }
                if (!builtChunk2.shouldBuild()) {
                    if (this.isOutsideViewDistance(blockPos, builtChunk)) continue;
                    this.nextUpdateTime.set(System.currentTimeMillis() + 500L);
                    continue;
                }
                ChunkInfo chunkInfo3 = new ChunkInfo(builtChunk2, direction, chunkInfo.propagationLevel + 1);
                chunkInfo3.updateCullingState(chunkInfo.cullingState, direction);
                queue.add(chunkInfo3);
                chunkInfoList.setInfo(builtChunk2, chunkInfo3);
            }
        }
    }

    @Nullable
    private ChunkBuilder.BuiltChunk getAdjacentChunk(BlockPos pos, ChunkBuilder.BuiltChunk chunk, Direction direction) {
        BlockPos blockPos = chunk.getNeighborPosition(direction);
        if (MathHelper.abs(pos.getX() - blockPos.getX()) > this.viewDistance * 16) {
            return null;
        }
        if (MathHelper.abs(pos.getY() - blockPos.getY()) > this.viewDistance * 16 || blockPos.getY() < this.world.getBottomY() || blockPos.getY() >= this.world.getTopY()) {
            return null;
        }
        if (MathHelper.abs(pos.getZ() - blockPos.getZ()) > this.viewDistance * 16) {
            return null;
        }
        return this.chunks.getRenderedChunk(blockPos);
    }

    private boolean isOutsideViewDistance(BlockPos pos, ChunkBuilder.BuiltChunk chunk) {
        int l;
        int i = ChunkSectionPos.getSectionCoord(pos.getX());
        int j = ChunkSectionPos.getSectionCoord(pos.getZ());
        BlockPos blockPos = chunk.getOrigin();
        int k = ChunkSectionPos.getSectionCoord(blockPos.getX());
        return !ThreadedAnvilChunkStorage.isWithinDistance(k, l = ChunkSectionPos.getSectionCoord(blockPos.getZ()), i, j, this.viewDistance - 2);
    }

    private void captureFrustum(Matrix4f positionMatrix, Matrix4f matrix4f, double x, double y, double z, Frustum frustum) {
        this.capturedFrustum = frustum;
        Matrix4f matrix4f2 = matrix4f.copy();
        matrix4f2.multiply(positionMatrix);
        matrix4f2.invert();
        this.capturedFrustumPosition.x = x;
        this.capturedFrustumPosition.y = y;
        this.capturedFrustumPosition.z = z;
        this.capturedFrustumOrientation[0] = new Vector4f(-1.0f, -1.0f, -1.0f, 1.0f);
        this.capturedFrustumOrientation[1] = new Vector4f(1.0f, -1.0f, -1.0f, 1.0f);
        this.capturedFrustumOrientation[2] = new Vector4f(1.0f, 1.0f, -1.0f, 1.0f);
        this.capturedFrustumOrientation[3] = new Vector4f(-1.0f, 1.0f, -1.0f, 1.0f);
        this.capturedFrustumOrientation[4] = new Vector4f(-1.0f, -1.0f, 1.0f, 1.0f);
        this.capturedFrustumOrientation[5] = new Vector4f(1.0f, -1.0f, 1.0f, 1.0f);
        this.capturedFrustumOrientation[6] = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.capturedFrustumOrientation[7] = new Vector4f(-1.0f, 1.0f, 1.0f, 1.0f);
        for (int i = 0; i < 8; ++i) {
            this.capturedFrustumOrientation[i].transform(matrix4f2);
            this.capturedFrustumOrientation[i].normalizeProjectiveCoordinates();
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

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void render(MatrixStack matrices, float tickDelta, long limitTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f positionMatrix) {
        int l;
        BlockPos blockPos;
        Frustum frustum;
        boolean bl2;
        RenderSystem.setShaderGameTime(this.world.getTime(), tickDelta);
        this.blockEntityRenderDispatcher.configure(this.world, camera, this.client.crosshairTarget);
        this.entityRenderDispatcher.configure(this.world, camera, this.client.targetedEntity);
        Profiler profiler = this.world.getProfiler();
        profiler.swap("light_update_queue");
        this.world.runQueuedChunkUpdates();
        profiler.swap("light_updates");
        boolean bl = this.world.hasNoChunkUpdaters();
        this.world.getChunkManager().getLightingProvider().doLightUpdates(Integer.MAX_VALUE, bl, true);
        Vec3d vec3d = camera.getPos();
        double d = vec3d.getX();
        double e = vec3d.getY();
        double f = vec3d.getZ();
        Matrix4f matrix4f = matrices.peek().getPositionMatrix();
        profiler.swap("culling");
        boolean bl3 = bl2 = this.capturedFrustum != null;
        if (bl2) {
            frustum = this.capturedFrustum;
            frustum.setPosition(this.capturedFrustumPosition.x, this.capturedFrustumPosition.y, this.capturedFrustumPosition.z);
        } else {
            frustum = this.frustum;
        }
        this.client.getProfiler().swap("captureFrustum");
        if (this.shouldCaptureFrustum) {
            this.captureFrustum(matrix4f, positionMatrix, vec3d.x, vec3d.y, vec3d.z, bl2 ? new Frustum(matrix4f, positionMatrix) : frustum);
            this.shouldCaptureFrustum = false;
        }
        profiler.swap("clear");
        BackgroundRenderer.render(camera, tickDelta, this.client.world, this.client.options.getClampedViewDistance(), gameRenderer.getSkyDarkness(tickDelta));
        BackgroundRenderer.setFogBlack();
        RenderSystem.clear(GlConst.GL_DEPTH_BUFFER_BIT | GlConst.GL_COLOR_BUFFER_BIT, MinecraftClient.IS_SYSTEM_MAC);
        float g = gameRenderer.getViewDistance();
        boolean bl32 = this.client.world.getDimensionEffects().useThickFog(MathHelper.floor(d), MathHelper.floor(e)) || this.client.inGameHud.getBossBarHud().shouldThickenFog();
        profiler.swap("sky");
        RenderSystem.setShader(GameRenderer::getPositionShader);
        this.renderSky(matrices, positionMatrix, tickDelta, camera, bl32, () -> BackgroundRenderer.applyFog(camera, BackgroundRenderer.FogType.FOG_SKY, g, bl32, tickDelta));
        profiler.swap("fog");
        BackgroundRenderer.applyFog(camera, BackgroundRenderer.FogType.FOG_TERRAIN, Math.max(g, 32.0f), bl32, tickDelta);
        profiler.swap("terrain_setup");
        this.setupTerrain(camera, frustum, bl2, this.client.player.isSpectator());
        profiler.swap("compilechunks");
        this.updateChunks(camera);
        profiler.swap("terrain");
        this.renderLayer(RenderLayer.getSolid(), matrices, d, e, f, positionMatrix);
        this.renderLayer(RenderLayer.getCutoutMipped(), matrices, d, e, f, positionMatrix);
        this.renderLayer(RenderLayer.getCutout(), matrices, d, e, f, positionMatrix);
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
        boolean bl4 = false;
        VertexConsumerProvider.Immediate immediate = this.bufferBuilders.getEntityVertexConsumers();
        for (Entity entity : this.world.getEntities()) {
            Object vertexConsumerProvider;
            if (!this.entityRenderDispatcher.shouldRender(entity, frustum, d, e, f) && !entity.hasPassengerDeep(this.client.player) || !this.world.isOutOfHeightLimit((blockPos = entity.getBlockPos()).getY()) && !this.isRenderingReady(blockPos) || entity == camera.getFocusedEntity() && !camera.isThirdPerson() && (!(camera.getFocusedEntity() instanceof LivingEntity) || !((LivingEntity)camera.getFocusedEntity()).isSleeping()) || entity instanceof ClientPlayerEntity && camera.getFocusedEntity() != entity) continue;
            ++this.regularEntityCount;
            if (entity.age == 0) {
                entity.lastRenderX = entity.getX();
                entity.lastRenderY = entity.getY();
                entity.lastRenderZ = entity.getZ();
            }
            if (this.canDrawEntityOutlines() && this.client.hasOutline(entity)) {
                bl4 = true;
                OutlineVertexConsumerProvider outlineVertexConsumerProvider = this.bufferBuilders.getOutlineVertexConsumers();
                vertexConsumerProvider = outlineVertexConsumerProvider;
                int i = entity.getTeamColorValue();
                int j = 255;
                int k = i >> 16 & 0xFF;
                l = i >> 8 & 0xFF;
                int m = i & 0xFF;
                outlineVertexConsumerProvider.setColor(k, l, m, 255);
            } else {
                vertexConsumerProvider = immediate;
            }
            this.renderEntity(entity, d, e, f, tickDelta, matrices, (VertexConsumerProvider)vertexConsumerProvider);
        }
        immediate.drawCurrentLayer();
        this.checkEmpty(matrices);
        immediate.draw(RenderLayer.getEntitySolid(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE));
        immediate.draw(RenderLayer.getEntityCutout(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE));
        immediate.draw(RenderLayer.getEntityCutoutNoCull(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE));
        immediate.draw(RenderLayer.getEntitySmoothCutout(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE));
        profiler.swap("blockentities");
        for (ChunkInfo chunkInfo : this.chunkInfos) {
            List<BlockEntity> list = chunkInfo.chunk.getData().getBlockEntities();
            if (list.isEmpty()) continue;
            for (BlockEntity blockEntity : list) {
                BlockPos blockPos2 = blockEntity.getPos();
                VertexConsumerProvider vertexConsumerProvider2 = immediate;
                matrices.push();
                matrices.translate((double)blockPos2.getX() - d, (double)blockPos2.getY() - e, (double)blockPos2.getZ() - f);
                SortedSet sortedSet = (SortedSet)this.blockBreakingProgressions.get(blockPos2.asLong());
                if (sortedSet != null && !sortedSet.isEmpty() && (l = ((BlockBreakingInfo)sortedSet.last()).getStage()) >= 0) {
                    MatrixStack.Entry entry = matrices.peek();
                    OverlayVertexConsumer vertexConsumer = new OverlayVertexConsumer(this.bufferBuilders.getEffectVertexConsumers().getBuffer(ModelLoader.BLOCK_DESTRUCTION_RENDER_LAYERS.get(l)), entry.getPositionMatrix(), entry.getNormalMatrix());
                    vertexConsumerProvider2 = renderLayer -> {
                        VertexConsumer vertexConsumer2 = immediate.getBuffer(renderLayer);
                        if (renderLayer.hasCrumbling()) {
                            return VertexConsumers.union(vertexConsumer, vertexConsumer2);
                        }
                        return vertexConsumer2;
                    };
                }
                this.blockEntityRenderDispatcher.render(blockEntity, tickDelta, matrices, vertexConsumerProvider2);
                matrices.pop();
            }
        }
        Set<BlockEntity> set = this.noCullingBlockEntities;
        synchronized (set) {
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
        immediate.draw(TexturedRenderLayers.getChest());
        this.bufferBuilders.getOutlineVertexConsumers().draw();
        if (bl4) {
            this.entityOutlineShader.render(tickDelta);
            this.client.getFramebuffer().beginWrite(false);
        }
        profiler.swap("destroyProgress");
        for (Long2ObjectMap.Entry entry : this.blockBreakingProgressions.long2ObjectEntrySet()) {
            SortedSet sortedSet2;
            double o;
            double n;
            blockPos = BlockPos.fromLong(entry.getLongKey());
            double h = (double)blockPos.getX() - d;
            if (h * h + (n = (double)blockPos.getY() - e) * n + (o = (double)blockPos.getZ() - f) * o > 1024.0 || (sortedSet2 = (SortedSet)entry.getValue()) == null || sortedSet2.isEmpty()) continue;
            int p = ((BlockBreakingInfo)sortedSet2.last()).getStage();
            matrices.push();
            matrices.translate((double)blockPos.getX() - d, (double)blockPos.getY() - e, (double)blockPos.getZ() - f);
            MatrixStack.Entry entry3 = matrices.peek();
            OverlayVertexConsumer vertexConsumer2 = new OverlayVertexConsumer(this.bufferBuilders.getEffectVertexConsumers().getBuffer(ModelLoader.BLOCK_DESTRUCTION_RENDER_LAYERS.get(p)), entry3.getPositionMatrix(), entry3.getNormalMatrix());
            this.client.getBlockRenderManager().renderDamage(this.world.getBlockState(blockPos), blockPos, this.world, matrices, vertexConsumer2);
            matrices.pop();
        }
        this.checkEmpty(matrices);
        HitResult hitResult = this.client.crosshairTarget;
        if (renderBlockOutline && hitResult != null && hitResult.getType() == HitResult.Type.BLOCK) {
            profiler.swap("outline");
            BlockPos blockPos2 = ((BlockHitResult)hitResult).getBlockPos();
            BlockState blockState = this.world.getBlockState(blockPos2);
            if (!blockState.isAir() && this.world.getWorldBorder().contains(blockPos2)) {
                VertexConsumer vertexConsumer3 = immediate.getBuffer(RenderLayer.getLines());
                this.drawBlockOutline(matrices, vertexConsumer3, camera.getFocusedEntity(), d, e, f, blockPos2, blockState);
            }
        }
        MatrixStack matrixStack = RenderSystem.getModelViewStack();
        matrixStack.push();
        matrixStack.multiplyPositionMatrix(matrices.peek().getPositionMatrix());
        RenderSystem.applyModelViewMatrix();
        this.client.debugRenderer.render(matrices, immediate, d, e, f);
        matrixStack.pop();
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
        if (this.transparencyShader != null) {
            immediate.draw(RenderLayer.getLines());
            immediate.draw();
            this.translucentFramebuffer.clear(MinecraftClient.IS_SYSTEM_MAC);
            this.translucentFramebuffer.copyDepthFrom(this.client.getFramebuffer());
            profiler.swap("translucent");
            this.renderLayer(RenderLayer.getTranslucent(), matrices, d, e, f, positionMatrix);
            profiler.swap("string");
            this.renderLayer(RenderLayer.getTripwire(), matrices, d, e, f, positionMatrix);
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
            this.renderLayer(RenderLayer.getTranslucent(), matrices, d, e, f, positionMatrix);
            immediate.draw(RenderLayer.getLines());
            immediate.draw();
            profiler.swap("string");
            this.renderLayer(RenderLayer.getTripwire(), matrices, d, e, f, positionMatrix);
            profiler.swap("particles");
            this.client.particleManager.renderParticles(matrices, immediate, lightmapTextureManager, camera, tickDelta);
        }
        matrixStack.push();
        matrixStack.multiplyPositionMatrix(matrices.peek().getPositionMatrix());
        RenderSystem.applyModelViewMatrix();
        if (this.client.options.getCloudRenderMode() != CloudRenderMode.OFF) {
            if (this.transparencyShader != null) {
                this.cloudsFramebuffer.clear(MinecraftClient.IS_SYSTEM_MAC);
                RenderPhase.CLOUDS_TARGET.startDrawing();
                profiler.swap("clouds");
                this.renderClouds(matrices, positionMatrix, tickDelta, d, e, f);
                RenderPhase.CLOUDS_TARGET.endDrawing();
            } else {
                profiler.swap("clouds");
                RenderSystem.setShader(GameRenderer::getPositionTexColorNormalShader);
                this.renderClouds(matrices, positionMatrix, tickDelta, d, e, f);
            }
        }
        if (this.transparencyShader != null) {
            RenderPhase.WEATHER_TARGET.startDrawing();
            profiler.swap("weather");
            this.renderWeather(lightmapTextureManager, tickDelta, d, e, f);
            this.renderWorldBorder(camera);
            RenderPhase.WEATHER_TARGET.endDrawing();
            this.transparencyShader.render(tickDelta);
            this.client.getFramebuffer().beginWrite(false);
        } else {
            RenderSystem.depthMask(false);
            profiler.swap("weather");
            this.renderWeather(lightmapTextureManager, tickDelta, d, e, f);
            this.renderWorldBorder(camera);
            RenderSystem.depthMask(true);
        }
        this.renderChunkDebugInfo(camera);
        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();
        matrixStack.pop();
        RenderSystem.applyModelViewMatrix();
        BackgroundRenderer.clearFog();
    }

    private void checkEmpty(MatrixStack matrices) {
        if (!matrices.isEmpty()) {
            throw new IllegalStateException("Pose stack not empty");
        }
    }

    private void renderEntity(Entity entity, double cameraX, double cameraY, double cameraZ, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers) {
        double d = MathHelper.lerp((double)tickDelta, entity.lastRenderX, entity.getX());
        double e = MathHelper.lerp((double)tickDelta, entity.lastRenderY, entity.getY());
        double f = MathHelper.lerp((double)tickDelta, entity.lastRenderZ, entity.getZ());
        float g = MathHelper.lerp(tickDelta, entity.prevYaw, entity.getYaw());
        this.entityRenderDispatcher.render(entity, d - cameraX, e - cameraY, f - cameraZ, g, tickDelta, matrices, vertexConsumers, this.entityRenderDispatcher.getLight(entity, tickDelta));
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
                this.lastTranslucentSortX = cameraX;
                this.lastTranslucentSortY = cameraY;
                this.lastTranslucentSortZ = cameraZ;
                int i = 0;
                for (ChunkInfo chunkInfo : this.chunkInfos) {
                    if (i >= 15 || !chunkInfo.chunk.scheduleSort(renderLayer, this.chunkBuilder)) continue;
                    ++i;
                }
            }
            this.client.getProfiler().pop();
        }
        this.client.getProfiler().push("filterempty");
        this.client.getProfiler().swap(() -> "render_" + renderLayer);
        boolean bl = renderLayer != RenderLayer.getTranslucent();
        ListIterator objectListIterator = this.chunkInfos.listIterator(bl ? 0 : this.chunkInfos.size());
        Shader shader = RenderSystem.getShader();
        for (int j = 0; j < 12; ++j) {
            int k = RenderSystem.getShaderTexture(j);
            shader.addSampler("Sampler" + j, k);
        }
        if (shader.modelViewMat != null) {
            shader.modelViewMat.set(matrices.peek().getPositionMatrix());
        }
        if (shader.projectionMat != null) {
            shader.projectionMat.set(positionMatrix);
        }
        if (shader.colorModulator != null) {
            shader.colorModulator.set(RenderSystem.getShaderColor());
        }
        if (shader.fogStart != null) {
            shader.fogStart.set(RenderSystem.getShaderFogStart());
        }
        if (shader.fogEnd != null) {
            shader.fogEnd.set(RenderSystem.getShaderFogEnd());
        }
        if (shader.fogColor != null) {
            shader.fogColor.set(RenderSystem.getShaderFogColor());
        }
        if (shader.fogShape != null) {
            shader.fogShape.set(RenderSystem.getShaderFogShape().getId());
        }
        if (shader.textureMat != null) {
            shader.textureMat.set(RenderSystem.getTextureMatrix());
        }
        if (shader.gameTime != null) {
            shader.gameTime.set(RenderSystem.getShaderGameTime());
        }
        RenderSystem.setupShaderLights(shader);
        shader.bind();
        GlUniform glUniform = shader.chunkOffset;
        while (bl ? objectListIterator.hasNext() : objectListIterator.hasPrevious()) {
            ChunkInfo chunkInfo2 = bl ? (ChunkInfo)objectListIterator.next() : (ChunkInfo)objectListIterator.previous();
            ChunkBuilder.BuiltChunk builtChunk = chunkInfo2.chunk;
            if (builtChunk.getData().isEmpty(renderLayer)) continue;
            VertexBuffer vertexBuffer = builtChunk.getBuffer(renderLayer);
            BlockPos blockPos = builtChunk.getOrigin();
            if (glUniform != null) {
                glUniform.set((float)((double)blockPos.getX() - cameraX), (float)((double)blockPos.getY() - cameraY), (float)((double)blockPos.getZ() - cameraZ));
                glUniform.upload();
            }
            vertexBuffer.bind();
            vertexBuffer.drawElements();
        }
        if (glUniform != null) {
            glUniform.set(Vec3f.ZERO);
        }
        shader.unbind();
        VertexBuffer.unbind();
        this.client.getProfiler().pop();
        renderLayer.endDrawing();
    }

    private void renderChunkDebugInfo(Camera camera) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        if (this.client.debugChunkInfo || this.client.debugChunkOcclusion) {
            double d = camera.getPos().getX();
            double e = camera.getPos().getY();
            double f = camera.getPos().getZ();
            RenderSystem.depthMask(true);
            RenderSystem.disableCull();
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.disableTexture();
            for (ChunkInfo chunkInfo : this.chunkInfos) {
                int i;
                ChunkBuilder.BuiltChunk builtChunk = chunkInfo.chunk;
                BlockPos blockPos = builtChunk.getOrigin();
                MatrixStack matrixStack = RenderSystem.getModelViewStack();
                matrixStack.push();
                matrixStack.translate((double)blockPos.getX() - d, (double)blockPos.getY() - e, (double)blockPos.getZ() - f);
                RenderSystem.applyModelViewMatrix();
                RenderSystem.setShader(GameRenderer::getRenderTypeLinesShader);
                if (this.client.debugChunkInfo) {
                    bufferBuilder.begin(VertexFormat.DrawMode.LINES, VertexFormats.LINES);
                    RenderSystem.lineWidth(5.0f);
                    i = chunkInfo.propagationLevel == 0 ? 0 : MathHelper.hsvToRgb((float)chunkInfo.propagationLevel / 50.0f, 0.9f, 0.9f);
                    int j = i >> 16 & 0xFF;
                    int k = i >> 8 & 0xFF;
                    int l = i & 0xFF;
                    for (int m = 0; m < DIRECTIONS.length; ++m) {
                        if (!chunkInfo.hasDirection(m)) continue;
                        Direction direction = DIRECTIONS[m];
                        bufferBuilder.vertex(8.0, 8.0, 8.0).color(j, k, l, 255).normal(direction.getOffsetX(), direction.getOffsetY(), direction.getOffsetZ()).next();
                        bufferBuilder.vertex(8 - 16 * direction.getOffsetX(), 8 - 16 * direction.getOffsetY(), 8 - 16 * direction.getOffsetZ()).color(j, k, l, 255).normal(direction.getOffsetX(), direction.getOffsetY(), direction.getOffsetZ()).next();
                    }
                    tessellator.draw();
                    RenderSystem.lineWidth(1.0f);
                }
                if (this.client.debugChunkOcclusion && !builtChunk.getData().isEmpty()) {
                    bufferBuilder.begin(VertexFormat.DrawMode.LINES, VertexFormats.LINES);
                    RenderSystem.setShader(GameRenderer::getRenderTypeLinesShader);
                    RenderSystem.lineWidth(5.0f);
                    i = 0;
                    for (Direction direction2 : DIRECTIONS) {
                        for (Direction direction3 : DIRECTIONS) {
                            boolean bl = builtChunk.getData().isVisibleThrough(direction2, direction3);
                            if (bl) continue;
                            ++i;
                            bufferBuilder.vertex(8 + 8 * direction2.getOffsetX(), 8 + 8 * direction2.getOffsetY(), 8 + 8 * direction2.getOffsetZ()).color(255, 0, 0, 255).normal(direction2.getOffsetX(), direction2.getOffsetY(), direction2.getOffsetZ()).next();
                            bufferBuilder.vertex(8 + 8 * direction3.getOffsetX(), 8 + 8 * direction3.getOffsetY(), 8 + 8 * direction3.getOffsetZ()).color(255, 0, 0, 255).normal(direction3.getOffsetX(), direction3.getOffsetY(), direction3.getOffsetZ()).next();
                        }
                    }
                    tessellator.draw();
                    RenderSystem.lineWidth(1.0f);
                    RenderSystem.setShader(GameRenderer::getPositionColorShader);
                    if (i > 0) {
                        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
                        float g = 0.5f;
                        float h = 0.2f;
                        bufferBuilder.vertex(0.5, 15.5, 0.5).color(0.9f, 0.9f, 0.0f, 0.2f).next();
                        bufferBuilder.vertex(15.5, 15.5, 0.5).color(0.9f, 0.9f, 0.0f, 0.2f).next();
                        bufferBuilder.vertex(15.5, 15.5, 15.5).color(0.9f, 0.9f, 0.0f, 0.2f).next();
                        bufferBuilder.vertex(0.5, 15.5, 15.5).color(0.9f, 0.9f, 0.0f, 0.2f).next();
                        bufferBuilder.vertex(0.5, 0.5, 15.5).color(0.9f, 0.9f, 0.0f, 0.2f).next();
                        bufferBuilder.vertex(15.5, 0.5, 15.5).color(0.9f, 0.9f, 0.0f, 0.2f).next();
                        bufferBuilder.vertex(15.5, 0.5, 0.5).color(0.9f, 0.9f, 0.0f, 0.2f).next();
                        bufferBuilder.vertex(0.5, 0.5, 0.5).color(0.9f, 0.9f, 0.0f, 0.2f).next();
                        bufferBuilder.vertex(0.5, 15.5, 0.5).color(0.9f, 0.9f, 0.0f, 0.2f).next();
                        bufferBuilder.vertex(0.5, 15.5, 15.5).color(0.9f, 0.9f, 0.0f, 0.2f).next();
                        bufferBuilder.vertex(0.5, 0.5, 15.5).color(0.9f, 0.9f, 0.0f, 0.2f).next();
                        bufferBuilder.vertex(0.5, 0.5, 0.5).color(0.9f, 0.9f, 0.0f, 0.2f).next();
                        bufferBuilder.vertex(15.5, 0.5, 0.5).color(0.9f, 0.9f, 0.0f, 0.2f).next();
                        bufferBuilder.vertex(15.5, 0.5, 15.5).color(0.9f, 0.9f, 0.0f, 0.2f).next();
                        bufferBuilder.vertex(15.5, 15.5, 15.5).color(0.9f, 0.9f, 0.0f, 0.2f).next();
                        bufferBuilder.vertex(15.5, 15.5, 0.5).color(0.9f, 0.9f, 0.0f, 0.2f).next();
                        bufferBuilder.vertex(0.5, 0.5, 0.5).color(0.9f, 0.9f, 0.0f, 0.2f).next();
                        bufferBuilder.vertex(15.5, 0.5, 0.5).color(0.9f, 0.9f, 0.0f, 0.2f).next();
                        bufferBuilder.vertex(15.5, 15.5, 0.5).color(0.9f, 0.9f, 0.0f, 0.2f).next();
                        bufferBuilder.vertex(0.5, 15.5, 0.5).color(0.9f, 0.9f, 0.0f, 0.2f).next();
                        bufferBuilder.vertex(0.5, 15.5, 15.5).color(0.9f, 0.9f, 0.0f, 0.2f).next();
                        bufferBuilder.vertex(15.5, 15.5, 15.5).color(0.9f, 0.9f, 0.0f, 0.2f).next();
                        bufferBuilder.vertex(15.5, 0.5, 15.5).color(0.9f, 0.9f, 0.0f, 0.2f).next();
                        bufferBuilder.vertex(0.5, 0.5, 15.5).color(0.9f, 0.9f, 0.0f, 0.2f).next();
                        tessellator.draw();
                    }
                }
                matrixStack.pop();
                RenderSystem.applyModelViewMatrix();
            }
            RenderSystem.depthMask(true);
            RenderSystem.disableBlend();
            RenderSystem.enableCull();
            RenderSystem.enableTexture();
        }
        if (this.capturedFrustum != null) {
            RenderSystem.disableCull();
            RenderSystem.disableTexture();
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.lineWidth(5.0f);
            RenderSystem.setShader(GameRenderer::getPositionColorShader);
            MatrixStack matrixStack2 = RenderSystem.getModelViewStack();
            matrixStack2.push();
            matrixStack2.translate((float)(this.capturedFrustumPosition.x - camera.getPos().x), (float)(this.capturedFrustumPosition.y - camera.getPos().y), (float)(this.capturedFrustumPosition.z - camera.getPos().z));
            RenderSystem.applyModelViewMatrix();
            RenderSystem.depthMask(true);
            bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
            this.method_22985(bufferBuilder, 0, 1, 2, 3, 0, 1, 1);
            this.method_22985(bufferBuilder, 4, 5, 6, 7, 1, 0, 0);
            this.method_22985(bufferBuilder, 0, 1, 5, 4, 1, 1, 0);
            this.method_22985(bufferBuilder, 2, 3, 7, 6, 0, 0, 1);
            this.method_22985(bufferBuilder, 0, 4, 7, 3, 0, 1, 0);
            this.method_22985(bufferBuilder, 1, 5, 6, 2, 1, 0, 1);
            tessellator.draw();
            RenderSystem.depthMask(false);
            RenderSystem.setShader(GameRenderer::getRenderTypeLinesShader);
            bufferBuilder.begin(VertexFormat.DrawMode.LINES, VertexFormats.LINES);
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
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
            matrixStack2.pop();
            RenderSystem.applyModelViewMatrix();
            RenderSystem.depthMask(true);
            RenderSystem.disableBlend();
            RenderSystem.enableCull();
            RenderSystem.enableTexture();
            RenderSystem.lineWidth(1.0f);
        }
    }

    private void method_22984(VertexConsumer vertexConsumer, int i) {
        vertexConsumer.vertex(this.capturedFrustumOrientation[i].getX(), this.capturedFrustumOrientation[i].getY(), this.capturedFrustumOrientation[i].getZ()).color(0, 0, 0, 255).normal(0.0f, 0.0f, -1.0f).next();
    }

    private void method_22985(VertexConsumer vertexConsumer, int i, int j, int k, int l, int m, int n, int o) {
        float f = 0.25f;
        vertexConsumer.vertex(this.capturedFrustumOrientation[i].getX(), this.capturedFrustumOrientation[i].getY(), this.capturedFrustumOrientation[i].getZ()).color((float)m, (float)n, (float)o, 0.25f).next();
        vertexConsumer.vertex(this.capturedFrustumOrientation[j].getX(), this.capturedFrustumOrientation[j].getY(), this.capturedFrustumOrientation[j].getZ()).color((float)m, (float)n, (float)o, 0.25f).next();
        vertexConsumer.vertex(this.capturedFrustumOrientation[k].getX(), this.capturedFrustumOrientation[k].getY(), this.capturedFrustumOrientation[k].getZ()).color((float)m, (float)n, (float)o, 0.25f).next();
        vertexConsumer.vertex(this.capturedFrustumOrientation[l].getX(), this.capturedFrustumOrientation[l].getY(), this.capturedFrustumOrientation[l].getZ()).color((float)m, (float)n, (float)o, 0.25f).next();
    }

    public void captureFrustum() {
        this.shouldCaptureFrustum = true;
    }

    public void killFrustum() {
        this.capturedFrustum = null;
    }

    public void tick() {
        ++this.ticks;
        if (this.ticks % 20 != 0) {
            return;
        }
        Iterator iterator = this.blockBreakingInfos.values().iterator();
        while (iterator.hasNext()) {
            BlockBreakingInfo blockBreakingInfo = (BlockBreakingInfo)iterator.next();
            int i = blockBreakingInfo.getLastUpdateTick();
            if (this.ticks - i <= 400) continue;
            iterator.remove();
            this.removeBlockBreakingInfo(blockBreakingInfo);
        }
    }

    private void removeBlockBreakingInfo(BlockBreakingInfo info) {
        long l = info.getPos().asLong();
        Set set = (Set)this.blockBreakingProgressions.get(l);
        set.remove(info);
        if (set.isEmpty()) {
            this.blockBreakingProgressions.remove(l);
        }
    }

    private void renderEndSky(MatrixStack matrices) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.depthMask(false);
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderTexture(0, END_SKY);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        for (int i = 0; i < 6; ++i) {
            matrices.push();
            if (i == 1) {
                matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(90.0f));
            }
            if (i == 2) {
                matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(-90.0f));
            }
            if (i == 3) {
                matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(180.0f));
            }
            if (i == 4) {
                matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(90.0f));
            }
            if (i == 5) {
                matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(-90.0f));
            }
            Matrix4f matrix4f = matrices.peek().getPositionMatrix();
            bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
            bufferBuilder.vertex(matrix4f, -100.0f, -100.0f, -100.0f).texture(0.0f, 0.0f).color(40, 40, 40, 255).next();
            bufferBuilder.vertex(matrix4f, -100.0f, -100.0f, 100.0f).texture(0.0f, 16.0f).color(40, 40, 40, 255).next();
            bufferBuilder.vertex(matrix4f, 100.0f, -100.0f, 100.0f).texture(16.0f, 16.0f).color(40, 40, 40, 255).next();
            bufferBuilder.vertex(matrix4f, 100.0f, -100.0f, -100.0f).texture(16.0f, 0.0f).color(40, 40, 40, 255).next();
            tessellator.draw();
            matrices.pop();
        }
        RenderSystem.depthMask(true);
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
    }

    public void renderSky(MatrixStack matrices, Matrix4f projectionMatrix, float tickDelta, Camera camera, boolean bl, Runnable runnable) {
        float q;
        float p;
        float o;
        int m;
        float k;
        float i;
        runnable.run();
        if (bl) {
            return;
        }
        CameraSubmersionType cameraSubmersionType = camera.getSubmersionType();
        if (cameraSubmersionType == CameraSubmersionType.POWDER_SNOW || cameraSubmersionType == CameraSubmersionType.LAVA || this.method_43788(camera)) {
            return;
        }
        if (this.client.world.getDimensionEffects().getSkyType() == DimensionEffects.SkyType.END) {
            this.renderEndSky(matrices);
            return;
        }
        if (this.client.world.getDimensionEffects().getSkyType() != DimensionEffects.SkyType.NORMAL) {
            return;
        }
        RenderSystem.disableTexture();
        Vec3d vec3d = this.world.getSkyColor(this.client.gameRenderer.getCamera().getPos(), tickDelta);
        float f = (float)vec3d.x;
        float g = (float)vec3d.y;
        float h = (float)vec3d.z;
        BackgroundRenderer.setFogBlack();
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        RenderSystem.depthMask(false);
        RenderSystem.setShaderColor(f, g, h, 1.0f);
        Shader shader = RenderSystem.getShader();
        this.lightSkyBuffer.bind();
        this.lightSkyBuffer.draw(matrices.peek().getPositionMatrix(), projectionMatrix, shader);
        VertexBuffer.unbind();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        float[] fs = this.world.getDimensionEffects().getFogColorOverride(this.world.getSkyAngle(tickDelta), tickDelta);
        if (fs != null) {
            RenderSystem.setShader(GameRenderer::getPositionColorShader);
            RenderSystem.disableTexture();
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            matrices.push();
            matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(90.0f));
            i = MathHelper.sin(this.world.getSkyAngleRadians(tickDelta)) < 0.0f ? 180.0f : 0.0f;
            matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(i));
            matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(90.0f));
            float j = fs[0];
            k = fs[1];
            float l = fs[2];
            Matrix4f matrix4f = matrices.peek().getPositionMatrix();
            bufferBuilder.begin(VertexFormat.DrawMode.TRIANGLE_FAN, VertexFormats.POSITION_COLOR);
            bufferBuilder.vertex(matrix4f, 0.0f, 100.0f, 0.0f).color(j, k, l, fs[3]).next();
            m = 16;
            for (int n = 0; n <= 16; ++n) {
                o = (float)n * ((float)Math.PI * 2) / 16.0f;
                p = MathHelper.sin(o);
                q = MathHelper.cos(o);
                bufferBuilder.vertex(matrix4f, p * 120.0f, q * 120.0f, -q * 40.0f * fs[3]).color(fs[0], fs[1], fs[2], 0.0f).next();
            }
            BufferRenderer.drawWithShader(bufferBuilder.end());
            matrices.pop();
        }
        RenderSystem.enableTexture();
        RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO);
        matrices.push();
        i = 1.0f - this.world.getRainGradient(tickDelta);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, i);
        matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(-90.0f));
        matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(this.world.getSkyAngle(tickDelta) * 360.0f));
        Matrix4f matrix4f2 = matrices.peek().getPositionMatrix();
        k = 30.0f;
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, SUN);
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
        bufferBuilder.vertex(matrix4f2, -k, 100.0f, -k).texture(0.0f, 0.0f).next();
        bufferBuilder.vertex(matrix4f2, k, 100.0f, -k).texture(1.0f, 0.0f).next();
        bufferBuilder.vertex(matrix4f2, k, 100.0f, k).texture(1.0f, 1.0f).next();
        bufferBuilder.vertex(matrix4f2, -k, 100.0f, k).texture(0.0f, 1.0f).next();
        BufferRenderer.drawWithShader(bufferBuilder.end());
        k = 20.0f;
        RenderSystem.setShaderTexture(0, MOON_PHASES);
        int r = this.world.getMoonPhase();
        int s = r % 4;
        m = r / 4 % 2;
        float t = (float)(s + 0) / 4.0f;
        o = (float)(m + 0) / 2.0f;
        p = (float)(s + 1) / 4.0f;
        q = (float)(m + 1) / 2.0f;
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
        bufferBuilder.vertex(matrix4f2, -k, -100.0f, k).texture(p, q).next();
        bufferBuilder.vertex(matrix4f2, k, -100.0f, k).texture(t, q).next();
        bufferBuilder.vertex(matrix4f2, k, -100.0f, -k).texture(t, o).next();
        bufferBuilder.vertex(matrix4f2, -k, -100.0f, -k).texture(p, o).next();
        BufferRenderer.drawWithShader(bufferBuilder.end());
        RenderSystem.disableTexture();
        float u = this.world.method_23787(tickDelta) * i;
        if (u > 0.0f) {
            RenderSystem.setShaderColor(u, u, u, u);
            BackgroundRenderer.clearFog();
            this.starsBuffer.bind();
            this.starsBuffer.draw(matrices.peek().getPositionMatrix(), projectionMatrix, GameRenderer.getPositionShader());
            VertexBuffer.unbind();
            runnable.run();
        }
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.disableBlend();
        matrices.pop();
        RenderSystem.disableTexture();
        RenderSystem.setShaderColor(0.0f, 0.0f, 0.0f, 1.0f);
        double d = this.client.player.getCameraPosVec((float)tickDelta).y - this.world.getLevelProperties().getSkyDarknessHeight(this.world);
        if (d < 0.0) {
            matrices.push();
            matrices.translate(0.0, 12.0, 0.0);
            this.darkSkyBuffer.bind();
            this.darkSkyBuffer.draw(matrices.peek().getPositionMatrix(), projectionMatrix, shader);
            VertexBuffer.unbind();
            matrices.pop();
        }
        if (this.world.getDimensionEffects().isAlternateSkyColor()) {
            RenderSystem.setShaderColor(f * 0.2f + 0.04f, g * 0.2f + 0.04f, h * 0.6f + 0.1f, 1.0f);
        } else {
            RenderSystem.setShaderColor(f, g, h, 1.0f);
        }
        RenderSystem.enableTexture();
        RenderSystem.depthMask(true);
    }

    private boolean method_43788(Camera camera) {
        Entity entity = camera.getFocusedEntity();
        if (entity instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity)entity;
            return livingEntity.hasStatusEffect(StatusEffects.BLINDNESS) || livingEntity.hasStatusEffect(StatusEffects.DARKNESS);
        }
        return false;
    }

    public void renderClouds(MatrixStack matrices, Matrix4f projectionMatrix, float tickDelta, double d, double e, double f) {
        float g = this.world.getDimensionEffects().getCloudsHeight();
        if (Float.isNaN(g)) {
            return;
        }
        RenderSystem.disableCull();
        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();
        RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.depthMask(true);
        float h = 12.0f;
        float i = 4.0f;
        double j = 2.0E-4;
        double k = ((float)this.ticks + tickDelta) * 0.03f;
        double l = (d + k) / 12.0;
        double m = g - (float)e + 0.33f;
        double n = f / 12.0 + (double)0.33f;
        l -= (double)(MathHelper.floor(l / 2048.0) * 2048);
        n -= (double)(MathHelper.floor(n / 2048.0) * 2048);
        float o = (float)(l - (double)MathHelper.floor(l));
        float p = (float)(m / 4.0 - (double)MathHelper.floor(m / 4.0)) * 4.0f;
        float q = (float)(n - (double)MathHelper.floor(n));
        Vec3d vec3d = this.world.getCloudsColor(tickDelta);
        int r = (int)Math.floor(l);
        int s = (int)Math.floor(m / 4.0);
        int t = (int)Math.floor(n);
        if (r != this.lastCloudsBlockX || s != this.lastCloudsBlockY || t != this.lastCloudsBlockZ || this.client.options.getCloudRenderMode() != this.lastCloudsRenderMode || this.lastCloudsColor.squaredDistanceTo(vec3d) > 2.0E-4) {
            this.lastCloudsBlockX = r;
            this.lastCloudsBlockY = s;
            this.lastCloudsBlockZ = t;
            this.lastCloudsColor = vec3d;
            this.lastCloudsRenderMode = this.client.options.getCloudRenderMode();
            this.cloudsDirty = true;
        }
        if (this.cloudsDirty) {
            this.cloudsDirty = false;
            BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
            if (this.cloudsBuffer != null) {
                this.cloudsBuffer.close();
            }
            this.cloudsBuffer = new VertexBuffer();
            BufferBuilder.BuiltBuffer builtBuffer = this.renderClouds(bufferBuilder, l, m, n, vec3d);
            this.cloudsBuffer.bind();
            this.cloudsBuffer.upload(builtBuffer);
            VertexBuffer.unbind();
        }
        RenderSystem.setShader(GameRenderer::getPositionTexColorNormalShader);
        RenderSystem.setShaderTexture(0, CLOUDS);
        BackgroundRenderer.setFogBlack();
        matrices.push();
        matrices.scale(12.0f, 1.0f, 12.0f);
        matrices.translate(-o, p, -q);
        if (this.cloudsBuffer != null) {
            int u;
            this.cloudsBuffer.bind();
            for (int v = u = this.lastCloudsRenderMode == CloudRenderMode.FANCY ? 0 : 1; v < 2; ++v) {
                if (v == 0) {
                    RenderSystem.colorMask(false, false, false, false);
                } else {
                    RenderSystem.colorMask(true, true, true, true);
                }
                Shader shader = RenderSystem.getShader();
                this.cloudsBuffer.draw(matrices.peek().getPositionMatrix(), projectionMatrix, shader);
            }
            VertexBuffer.unbind();
        }
        matrices.pop();
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
    }

    private BufferBuilder.BuiltBuffer renderClouds(BufferBuilder builder, double x, double y, double z, Vec3d color) {
        float f = 4.0f;
        float g = 0.00390625f;
        int i = 8;
        int j = 4;
        float h = 9.765625E-4f;
        float k = (float)MathHelper.floor(x) * 0.00390625f;
        float l = (float)MathHelper.floor(z) * 0.00390625f;
        float m = (float)color.x;
        float n = (float)color.y;
        float o = (float)color.z;
        float p = m * 0.9f;
        float q = n * 0.9f;
        float r = o * 0.9f;
        float s = m * 0.7f;
        float t = n * 0.7f;
        float u = o * 0.7f;
        float v = m * 0.8f;
        float w = n * 0.8f;
        float aa = o * 0.8f;
        RenderSystem.setShader(GameRenderer::getPositionTexColorNormalShader);
        builder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR_NORMAL);
        float ab = (float)Math.floor(y / 4.0) * 4.0f;
        if (this.lastCloudsRenderMode == CloudRenderMode.FANCY) {
            for (int ac = -3; ac <= 4; ++ac) {
                for (int ad = -3; ad <= 4; ++ad) {
                    int ag;
                    float ae = ac * 8;
                    float af = ad * 8;
                    if (ab > -5.0f) {
                        builder.vertex(ae + 0.0f, ab + 0.0f, af + 8.0f).texture((ae + 0.0f) * 0.00390625f + k, (af + 8.0f) * 0.00390625f + l).color(s, t, u, 0.8f).normal(0.0f, -1.0f, 0.0f).next();
                        builder.vertex(ae + 8.0f, ab + 0.0f, af + 8.0f).texture((ae + 8.0f) * 0.00390625f + k, (af + 8.0f) * 0.00390625f + l).color(s, t, u, 0.8f).normal(0.0f, -1.0f, 0.0f).next();
                        builder.vertex(ae + 8.0f, ab + 0.0f, af + 0.0f).texture((ae + 8.0f) * 0.00390625f + k, (af + 0.0f) * 0.00390625f + l).color(s, t, u, 0.8f).normal(0.0f, -1.0f, 0.0f).next();
                        builder.vertex(ae + 0.0f, ab + 0.0f, af + 0.0f).texture((ae + 0.0f) * 0.00390625f + k, (af + 0.0f) * 0.00390625f + l).color(s, t, u, 0.8f).normal(0.0f, -1.0f, 0.0f).next();
                    }
                    if (ab <= 5.0f) {
                        builder.vertex(ae + 0.0f, ab + 4.0f - 9.765625E-4f, af + 8.0f).texture((ae + 0.0f) * 0.00390625f + k, (af + 8.0f) * 0.00390625f + l).color(m, n, o, 0.8f).normal(0.0f, 1.0f, 0.0f).next();
                        builder.vertex(ae + 8.0f, ab + 4.0f - 9.765625E-4f, af + 8.0f).texture((ae + 8.0f) * 0.00390625f + k, (af + 8.0f) * 0.00390625f + l).color(m, n, o, 0.8f).normal(0.0f, 1.0f, 0.0f).next();
                        builder.vertex(ae + 8.0f, ab + 4.0f - 9.765625E-4f, af + 0.0f).texture((ae + 8.0f) * 0.00390625f + k, (af + 0.0f) * 0.00390625f + l).color(m, n, o, 0.8f).normal(0.0f, 1.0f, 0.0f).next();
                        builder.vertex(ae + 0.0f, ab + 4.0f - 9.765625E-4f, af + 0.0f).texture((ae + 0.0f) * 0.00390625f + k, (af + 0.0f) * 0.00390625f + l).color(m, n, o, 0.8f).normal(0.0f, 1.0f, 0.0f).next();
                    }
                    if (ac > -1) {
                        for (ag = 0; ag < 8; ++ag) {
                            builder.vertex(ae + (float)ag + 0.0f, ab + 0.0f, af + 8.0f).texture((ae + (float)ag + 0.5f) * 0.00390625f + k, (af + 8.0f) * 0.00390625f + l).color(p, q, r, 0.8f).normal(-1.0f, 0.0f, 0.0f).next();
                            builder.vertex(ae + (float)ag + 0.0f, ab + 4.0f, af + 8.0f).texture((ae + (float)ag + 0.5f) * 0.00390625f + k, (af + 8.0f) * 0.00390625f + l).color(p, q, r, 0.8f).normal(-1.0f, 0.0f, 0.0f).next();
                            builder.vertex(ae + (float)ag + 0.0f, ab + 4.0f, af + 0.0f).texture((ae + (float)ag + 0.5f) * 0.00390625f + k, (af + 0.0f) * 0.00390625f + l).color(p, q, r, 0.8f).normal(-1.0f, 0.0f, 0.0f).next();
                            builder.vertex(ae + (float)ag + 0.0f, ab + 0.0f, af + 0.0f).texture((ae + (float)ag + 0.5f) * 0.00390625f + k, (af + 0.0f) * 0.00390625f + l).color(p, q, r, 0.8f).normal(-1.0f, 0.0f, 0.0f).next();
                        }
                    }
                    if (ac <= 1) {
                        for (ag = 0; ag < 8; ++ag) {
                            builder.vertex(ae + (float)ag + 1.0f - 9.765625E-4f, ab + 0.0f, af + 8.0f).texture((ae + (float)ag + 0.5f) * 0.00390625f + k, (af + 8.0f) * 0.00390625f + l).color(p, q, r, 0.8f).normal(1.0f, 0.0f, 0.0f).next();
                            builder.vertex(ae + (float)ag + 1.0f - 9.765625E-4f, ab + 4.0f, af + 8.0f).texture((ae + (float)ag + 0.5f) * 0.00390625f + k, (af + 8.0f) * 0.00390625f + l).color(p, q, r, 0.8f).normal(1.0f, 0.0f, 0.0f).next();
                            builder.vertex(ae + (float)ag + 1.0f - 9.765625E-4f, ab + 4.0f, af + 0.0f).texture((ae + (float)ag + 0.5f) * 0.00390625f + k, (af + 0.0f) * 0.00390625f + l).color(p, q, r, 0.8f).normal(1.0f, 0.0f, 0.0f).next();
                            builder.vertex(ae + (float)ag + 1.0f - 9.765625E-4f, ab + 0.0f, af + 0.0f).texture((ae + (float)ag + 0.5f) * 0.00390625f + k, (af + 0.0f) * 0.00390625f + l).color(p, q, r, 0.8f).normal(1.0f, 0.0f, 0.0f).next();
                        }
                    }
                    if (ad > -1) {
                        for (ag = 0; ag < 8; ++ag) {
                            builder.vertex(ae + 0.0f, ab + 4.0f, af + (float)ag + 0.0f).texture((ae + 0.0f) * 0.00390625f + k, (af + (float)ag + 0.5f) * 0.00390625f + l).color(v, w, aa, 0.8f).normal(0.0f, 0.0f, -1.0f).next();
                            builder.vertex(ae + 8.0f, ab + 4.0f, af + (float)ag + 0.0f).texture((ae + 8.0f) * 0.00390625f + k, (af + (float)ag + 0.5f) * 0.00390625f + l).color(v, w, aa, 0.8f).normal(0.0f, 0.0f, -1.0f).next();
                            builder.vertex(ae + 8.0f, ab + 0.0f, af + (float)ag + 0.0f).texture((ae + 8.0f) * 0.00390625f + k, (af + (float)ag + 0.5f) * 0.00390625f + l).color(v, w, aa, 0.8f).normal(0.0f, 0.0f, -1.0f).next();
                            builder.vertex(ae + 0.0f, ab + 0.0f, af + (float)ag + 0.0f).texture((ae + 0.0f) * 0.00390625f + k, (af + (float)ag + 0.5f) * 0.00390625f + l).color(v, w, aa, 0.8f).normal(0.0f, 0.0f, -1.0f).next();
                        }
                    }
                    if (ad > 1) continue;
                    for (ag = 0; ag < 8; ++ag) {
                        builder.vertex(ae + 0.0f, ab + 4.0f, af + (float)ag + 1.0f - 9.765625E-4f).texture((ae + 0.0f) * 0.00390625f + k, (af + (float)ag + 0.5f) * 0.00390625f + l).color(v, w, aa, 0.8f).normal(0.0f, 0.0f, 1.0f).next();
                        builder.vertex(ae + 8.0f, ab + 4.0f, af + (float)ag + 1.0f - 9.765625E-4f).texture((ae + 8.0f) * 0.00390625f + k, (af + (float)ag + 0.5f) * 0.00390625f + l).color(v, w, aa, 0.8f).normal(0.0f, 0.0f, 1.0f).next();
                        builder.vertex(ae + 8.0f, ab + 0.0f, af + (float)ag + 1.0f - 9.765625E-4f).texture((ae + 8.0f) * 0.00390625f + k, (af + (float)ag + 0.5f) * 0.00390625f + l).color(v, w, aa, 0.8f).normal(0.0f, 0.0f, 1.0f).next();
                        builder.vertex(ae + 0.0f, ab + 0.0f, af + (float)ag + 1.0f - 9.765625E-4f).texture((ae + 0.0f) * 0.00390625f + k, (af + (float)ag + 0.5f) * 0.00390625f + l).color(v, w, aa, 0.8f).normal(0.0f, 0.0f, 1.0f).next();
                    }
                }
            }
        } else {
            boolean ac = true;
            int ad = 32;
            for (int ah = -32; ah < 32; ah += 32) {
                for (int ai = -32; ai < 32; ai += 32) {
                    builder.vertex(ah + 0, ab, ai + 32).texture((float)(ah + 0) * 0.00390625f + k, (float)(ai + 32) * 0.00390625f + l).color(m, n, o, 0.8f).normal(0.0f, -1.0f, 0.0f).next();
                    builder.vertex(ah + 32, ab, ai + 32).texture((float)(ah + 32) * 0.00390625f + k, (float)(ai + 32) * 0.00390625f + l).color(m, n, o, 0.8f).normal(0.0f, -1.0f, 0.0f).next();
                    builder.vertex(ah + 32, ab, ai + 0).texture((float)(ah + 32) * 0.00390625f + k, (float)(ai + 0) * 0.00390625f + l).color(m, n, o, 0.8f).normal(0.0f, -1.0f, 0.0f).next();
                    builder.vertex(ah + 0, ab, ai + 0).texture((float)(ah + 0) * 0.00390625f + k, (float)(ai + 0) * 0.00390625f + l).color(m, n, o, 0.8f).normal(0.0f, -1.0f, 0.0f).next();
                }
            }
        }
        return builder.end();
    }

    private void updateChunks(Camera camera) {
        this.client.getProfiler().push("populate_chunks_to_compile");
        ChunkRendererRegionBuilder chunkRendererRegionBuilder = new ChunkRendererRegionBuilder();
        BlockPos blockPos = camera.getBlockPos();
        ArrayList<ChunkBuilder.BuiltChunk> list = Lists.newArrayList();
        for (ChunkInfo chunkInfo : this.chunkInfos) {
            ChunkBuilder.BuiltChunk builtChunk = chunkInfo.chunk;
            ChunkPos chunkPos = new ChunkPos(builtChunk.getOrigin());
            if (!builtChunk.needsRebuild() || !this.world.getChunk(chunkPos.x, chunkPos.z).shouldRenderOnUpdate()) continue;
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
                continue;
            }
            list.add(builtChunk);
        }
        this.client.getProfiler().swap("upload");
        this.chunkBuilder.upload();
        this.client.getProfiler().swap("schedule_async_compile");
        for (ChunkBuilder.BuiltChunk builtChunk2 : list) {
            builtChunk2.scheduleRebuild(this.chunkBuilder, chunkRendererRegionBuilder);
            builtChunk2.cancelRebuild();
        }
        this.client.getProfiler().pop();
    }

    private void renderWorldBorder(Camera camera) {
        float v;
        double u;
        double t;
        float s;
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        WorldBorder worldBorder = this.world.getWorldBorder();
        double d = this.client.options.getClampedViewDistance() * 16;
        if (camera.getPos().x < worldBorder.getBoundEast() - d && camera.getPos().x > worldBorder.getBoundWest() + d && camera.getPos().z < worldBorder.getBoundSouth() - d && camera.getPos().z > worldBorder.getBoundNorth() + d) {
            return;
        }
        double e = 1.0 - worldBorder.getDistanceInsideBorder(camera.getPos().x, camera.getPos().z) / d;
        e = Math.pow(e, 4.0);
        e = MathHelper.clamp(e, 0.0, 1.0);
        double f = camera.getPos().x;
        double g = camera.getPos().z;
        double h = this.client.gameRenderer.method_32796();
        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();
        RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO);
        RenderSystem.setShaderTexture(0, FORCEFIELD);
        RenderSystem.depthMask(MinecraftClient.isFabulousGraphicsOrBetter());
        MatrixStack matrixStack = RenderSystem.getModelViewStack();
        matrixStack.push();
        RenderSystem.applyModelViewMatrix();
        int i = worldBorder.getStage().getColor();
        float j = (float)(i >> 16 & 0xFF) / 255.0f;
        float k = (float)(i >> 8 & 0xFF) / 255.0f;
        float l = (float)(i & 0xFF) / 255.0f;
        RenderSystem.setShaderColor(j, k, l, (float)e);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.polygonOffset(-3.0f, -3.0f);
        RenderSystem.enablePolygonOffset();
        RenderSystem.disableCull();
        float m = (float)(Util.getMeasuringTimeMs() % 3000L) / 3000.0f;
        float n = 0.0f;
        float o = 0.0f;
        float p = (float)(h - MathHelper.fractionalPart(camera.getPos().y));
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
        double q = Math.max((double)MathHelper.floor(g - d), worldBorder.getBoundNorth());
        double r = Math.min((double)MathHelper.ceil(g + d), worldBorder.getBoundSouth());
        if (f > worldBorder.getBoundEast() - d) {
            s = 0.0f;
            t = q;
            while (t < r) {
                u = Math.min(1.0, r - t);
                v = (float)u * 0.5f;
                bufferBuilder.vertex(worldBorder.getBoundEast() - f, -h, t - g).texture(m - s, m + p).next();
                bufferBuilder.vertex(worldBorder.getBoundEast() - f, -h, t + u - g).texture(m - (v + s), m + p).next();
                bufferBuilder.vertex(worldBorder.getBoundEast() - f, h, t + u - g).texture(m - (v + s), m + 0.0f).next();
                bufferBuilder.vertex(worldBorder.getBoundEast() - f, h, t - g).texture(m - s, m + 0.0f).next();
                t += 1.0;
                s += 0.5f;
            }
        }
        if (f < worldBorder.getBoundWest() + d) {
            s = 0.0f;
            t = q;
            while (t < r) {
                u = Math.min(1.0, r - t);
                v = (float)u * 0.5f;
                bufferBuilder.vertex(worldBorder.getBoundWest() - f, -h, t - g).texture(m + s, m + p).next();
                bufferBuilder.vertex(worldBorder.getBoundWest() - f, -h, t + u - g).texture(m + v + s, m + p).next();
                bufferBuilder.vertex(worldBorder.getBoundWest() - f, h, t + u - g).texture(m + v + s, m + 0.0f).next();
                bufferBuilder.vertex(worldBorder.getBoundWest() - f, h, t - g).texture(m + s, m + 0.0f).next();
                t += 1.0;
                s += 0.5f;
            }
        }
        q = Math.max((double)MathHelper.floor(f - d), worldBorder.getBoundWest());
        r = Math.min((double)MathHelper.ceil(f + d), worldBorder.getBoundEast());
        if (g > worldBorder.getBoundSouth() - d) {
            s = 0.0f;
            t = q;
            while (t < r) {
                u = Math.min(1.0, r - t);
                v = (float)u * 0.5f;
                bufferBuilder.vertex(t - f, -h, worldBorder.getBoundSouth() - g).texture(m + s, m + p).next();
                bufferBuilder.vertex(t + u - f, -h, worldBorder.getBoundSouth() - g).texture(m + v + s, m + p).next();
                bufferBuilder.vertex(t + u - f, h, worldBorder.getBoundSouth() - g).texture(m + v + s, m + 0.0f).next();
                bufferBuilder.vertex(t - f, h, worldBorder.getBoundSouth() - g).texture(m + s, m + 0.0f).next();
                t += 1.0;
                s += 0.5f;
            }
        }
        if (g < worldBorder.getBoundNorth() + d) {
            s = 0.0f;
            t = q;
            while (t < r) {
                u = Math.min(1.0, r - t);
                v = (float)u * 0.5f;
                bufferBuilder.vertex(t - f, -h, worldBorder.getBoundNorth() - g).texture(m - s, m + p).next();
                bufferBuilder.vertex(t + u - f, -h, worldBorder.getBoundNorth() - g).texture(m - (v + s), m + p).next();
                bufferBuilder.vertex(t + u - f, h, worldBorder.getBoundNorth() - g).texture(m - (v + s), m + 0.0f).next();
                bufferBuilder.vertex(t - f, h, worldBorder.getBoundNorth() - g).texture(m - s, m + 0.0f).next();
                t += 1.0;
                s += 0.5f;
            }
        }
        BufferRenderer.drawWithShader(bufferBuilder.end());
        RenderSystem.enableCull();
        RenderSystem.polygonOffset(0.0f, 0.0f);
        RenderSystem.disablePolygonOffset();
        RenderSystem.disableBlend();
        matrixStack.pop();
        RenderSystem.applyModelViewMatrix();
        RenderSystem.depthMask(true);
    }

    private void drawBlockOutline(MatrixStack matrices, VertexConsumer vertexConsumer, Entity entity, double cameraX, double cameraY, double cameraZ, BlockPos pos, BlockState state) {
        WorldRenderer.drawCuboidShapeOutline(matrices, vertexConsumer, state.getOutlineShape(this.world, pos, ShapeContext.of(entity)), (double)pos.getX() - cameraX, (double)pos.getY() - cameraY, (double)pos.getZ() - cameraZ, 0.0f, 0.0f, 0.0f, 0.4f);
    }

    public static void drawShapeOutline(MatrixStack matrices, VertexConsumer vertexConsumer, VoxelShape shape, double offsetX, double offsetY, double offsetZ, float red, float green, float blue, float alpha) {
        List<Box> list = shape.getBoundingBoxes();
        int i = MathHelper.ceil((double)list.size() / 3.0);
        for (int j = 0; j < list.size(); ++j) {
            Box box = list.get(j);
            float f = ((float)j % (float)i + 1.0f) / (float)i;
            float g = j / i;
            float h = f * (float)(g == 0.0f ? 1 : 0);
            float k = f * (float)(g == 1.0f ? 1 : 0);
            float l = f * (float)(g == 2.0f ? 1 : 0);
            WorldRenderer.drawCuboidShapeOutline(matrices, vertexConsumer, VoxelShapes.cuboid(box.offset(0.0, 0.0, 0.0)), offsetX, offsetY, offsetZ, h, k, l, 1.0f);
        }
    }

    private static void drawCuboidShapeOutline(MatrixStack matrices, VertexConsumer vertexConsumer, VoxelShape shape, double offsetX, double offsetY, double offsetZ, float red, float green, float blue, float alpha) {
        MatrixStack.Entry entry = matrices.peek();
        shape.forEachEdge((minX, minY, minZ, maxX, maxY, maxZ) -> {
            float k = (float)(maxX - minX);
            float l = (float)(maxY - minY);
            float m = (float)(maxZ - minZ);
            float n = MathHelper.sqrt(k * k + l * l + m * m);
            vertexConsumer.vertex(entry.getPositionMatrix(), (float)(minX + offsetX), (float)(minY + offsetY), (float)(minZ + offsetZ)).color(red, green, blue, alpha).normal(entry.getNormalMatrix(), k /= n, l /= n, m /= n).next();
            vertexConsumer.vertex(entry.getPositionMatrix(), (float)(maxX + offsetX), (float)(maxY + offsetY), (float)(maxZ + offsetZ)).color(red, green, blue, alpha).normal(entry.getNormalMatrix(), k, l, m).next();
        });
    }

    /**
     * Draws a box spanning from [x1,y1,z1] to [x2,y2,z2].
     */
    public static void drawBox(VertexConsumer vertexConsumer, double x1, double y1, double z1, double x2, double y2, double z2, float red, float green, float blue, float alpha) {
        WorldRenderer.drawBox(new MatrixStack(), vertexConsumer, x1, y1, z1, x2, y2, z2, red, green, blue, alpha, red, green, blue);
    }

    /**
     * Draws a box.
     * 
     * <p>Note the coordinates the box spans are relative to current translation of the matrices.
     */
    public static void drawBox(MatrixStack matrices, VertexConsumer vertexConsumer, Box box, float red, float green, float blue, float alpha) {
        WorldRenderer.drawBox(matrices, vertexConsumer, box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ, red, green, blue, alpha, red, green, blue);
    }

    /**
     * Draws a box spanning from [x1,y1,z1] to [x2,y2,z2].
     * 
     * <p>Note the coordinates the box spans are relative to current translation of the matrices.
     */
    public static void drawBox(MatrixStack matrices, VertexConsumer vertexConsumer, double x1, double y1, double z1, double x2, double y2, double z2, float red, float green, float blue, float alpha) {
        WorldRenderer.drawBox(matrices, vertexConsumer, x1, y1, z1, x2, y2, z2, red, green, blue, alpha, red, green, blue);
    }

    /**
     * Draws a box spanning from [x1,y1,z1] to [x2,y2,z2].
     * The 3 axes centered at [x1,y1,z1] may be colored differently using xAxisRed, yAxisGreen, and zAxisBlue.
     * 
     * <p>Note the coordinates the box spans are relative to current translation of the matrices.
     */
    public static void drawBox(MatrixStack matrices, VertexConsumer vertexConsumer, double x1, double y1, double z1, double x2, double y2, double z2, float red, float green, float blue, float alpha, float xAxisRed, float yAxisGreen, float zAxisBlue) {
        Matrix4f matrix4f = matrices.peek().getPositionMatrix();
        Matrix3f matrix3f = matrices.peek().getNormalMatrix();
        float f = (float)x1;
        float g = (float)y1;
        float h = (float)z1;
        float i = (float)x2;
        float j = (float)y2;
        float k = (float)z2;
        vertexConsumer.vertex(matrix4f, f, g, h).color(red, yAxisGreen, zAxisBlue, alpha).normal(matrix3f, 1.0f, 0.0f, 0.0f).next();
        vertexConsumer.vertex(matrix4f, i, g, h).color(red, yAxisGreen, zAxisBlue, alpha).normal(matrix3f, 1.0f, 0.0f, 0.0f).next();
        vertexConsumer.vertex(matrix4f, f, g, h).color(xAxisRed, green, zAxisBlue, alpha).normal(matrix3f, 0.0f, 1.0f, 0.0f).next();
        vertexConsumer.vertex(matrix4f, f, j, h).color(xAxisRed, green, zAxisBlue, alpha).normal(matrix3f, 0.0f, 1.0f, 0.0f).next();
        vertexConsumer.vertex(matrix4f, f, g, h).color(xAxisRed, yAxisGreen, blue, alpha).normal(matrix3f, 0.0f, 0.0f, 1.0f).next();
        vertexConsumer.vertex(matrix4f, f, g, k).color(xAxisRed, yAxisGreen, blue, alpha).normal(matrix3f, 0.0f, 0.0f, 1.0f).next();
        vertexConsumer.vertex(matrix4f, i, g, h).color(red, green, blue, alpha).normal(matrix3f, 0.0f, 1.0f, 0.0f).next();
        vertexConsumer.vertex(matrix4f, i, j, h).color(red, green, blue, alpha).normal(matrix3f, 0.0f, 1.0f, 0.0f).next();
        vertexConsumer.vertex(matrix4f, i, j, h).color(red, green, blue, alpha).normal(matrix3f, -1.0f, 0.0f, 0.0f).next();
        vertexConsumer.vertex(matrix4f, f, j, h).color(red, green, blue, alpha).normal(matrix3f, -1.0f, 0.0f, 0.0f).next();
        vertexConsumer.vertex(matrix4f, f, j, h).color(red, green, blue, alpha).normal(matrix3f, 0.0f, 0.0f, 1.0f).next();
        vertexConsumer.vertex(matrix4f, f, j, k).color(red, green, blue, alpha).normal(matrix3f, 0.0f, 0.0f, 1.0f).next();
        vertexConsumer.vertex(matrix4f, f, j, k).color(red, green, blue, alpha).normal(matrix3f, 0.0f, -1.0f, 0.0f).next();
        vertexConsumer.vertex(matrix4f, f, g, k).color(red, green, blue, alpha).normal(matrix3f, 0.0f, -1.0f, 0.0f).next();
        vertexConsumer.vertex(matrix4f, f, g, k).color(red, green, blue, alpha).normal(matrix3f, 1.0f, 0.0f, 0.0f).next();
        vertexConsumer.vertex(matrix4f, i, g, k).color(red, green, blue, alpha).normal(matrix3f, 1.0f, 0.0f, 0.0f).next();
        vertexConsumer.vertex(matrix4f, i, g, k).color(red, green, blue, alpha).normal(matrix3f, 0.0f, 0.0f, -1.0f).next();
        vertexConsumer.vertex(matrix4f, i, g, h).color(red, green, blue, alpha).normal(matrix3f, 0.0f, 0.0f, -1.0f).next();
        vertexConsumer.vertex(matrix4f, f, j, k).color(red, green, blue, alpha).normal(matrix3f, 1.0f, 0.0f, 0.0f).next();
        vertexConsumer.vertex(matrix4f, i, j, k).color(red, green, blue, alpha).normal(matrix3f, 1.0f, 0.0f, 0.0f).next();
        vertexConsumer.vertex(matrix4f, i, g, k).color(red, green, blue, alpha).normal(matrix3f, 0.0f, 1.0f, 0.0f).next();
        vertexConsumer.vertex(matrix4f, i, j, k).color(red, green, blue, alpha).normal(matrix3f, 0.0f, 1.0f, 0.0f).next();
        vertexConsumer.vertex(matrix4f, i, j, h).color(red, green, blue, alpha).normal(matrix3f, 0.0f, 0.0f, 1.0f).next();
        vertexConsumer.vertex(matrix4f, i, j, k).color(red, green, blue, alpha).normal(matrix3f, 0.0f, 0.0f, 1.0f).next();
    }

    public static void drawBox(BufferBuilder buffer, double x1, double y1, double z1, double x2, double y2, double z2, float red, float green, float blue, float alpha) {
        buffer.vertex(x1, y1, z1).color(red, green, blue, alpha).next();
        buffer.vertex(x1, y1, z1).color(red, green, blue, alpha).next();
        buffer.vertex(x1, y1, z1).color(red, green, blue, alpha).next();
        buffer.vertex(x1, y1, z2).color(red, green, blue, alpha).next();
        buffer.vertex(x1, y2, z1).color(red, green, blue, alpha).next();
        buffer.vertex(x1, y2, z2).color(red, green, blue, alpha).next();
        buffer.vertex(x1, y2, z2).color(red, green, blue, alpha).next();
        buffer.vertex(x1, y1, z2).color(red, green, blue, alpha).next();
        buffer.vertex(x2, y2, z2).color(red, green, blue, alpha).next();
        buffer.vertex(x2, y1, z2).color(red, green, blue, alpha).next();
        buffer.vertex(x2, y1, z2).color(red, green, blue, alpha).next();
        buffer.vertex(x2, y1, z1).color(red, green, blue, alpha).next();
        buffer.vertex(x2, y2, z2).color(red, green, blue, alpha).next();
        buffer.vertex(x2, y2, z1).color(red, green, blue, alpha).next();
        buffer.vertex(x2, y2, z1).color(red, green, blue, alpha).next();
        buffer.vertex(x2, y1, z1).color(red, green, blue, alpha).next();
        buffer.vertex(x1, y2, z1).color(red, green, blue, alpha).next();
        buffer.vertex(x1, y1, z1).color(red, green, blue, alpha).next();
        buffer.vertex(x1, y1, z1).color(red, green, blue, alpha).next();
        buffer.vertex(x2, y1, z1).color(red, green, blue, alpha).next();
        buffer.vertex(x1, y1, z2).color(red, green, blue, alpha).next();
        buffer.vertex(x2, y1, z2).color(red, green, blue, alpha).next();
        buffer.vertex(x2, y1, z2).color(red, green, blue, alpha).next();
        buffer.vertex(x1, y2, z1).color(red, green, blue, alpha).next();
        buffer.vertex(x1, y2, z1).color(red, green, blue, alpha).next();
        buffer.vertex(x1, y2, z2).color(red, green, blue, alpha).next();
        buffer.vertex(x2, y2, z1).color(red, green, blue, alpha).next();
        buffer.vertex(x2, y2, z2).color(red, green, blue, alpha).next();
        buffer.vertex(x2, y2, z2).color(red, green, blue, alpha).next();
        buffer.vertex(x2, y2, z2).color(red, green, blue, alpha).next();
    }

    public void updateBlock(BlockView world, BlockPos pos, BlockState oldState, BlockState newState, int flags) {
        this.scheduleSectionRender(pos, (flags & 8) != 0);
    }

    private void scheduleSectionRender(BlockPos pos, boolean important) {
        for (int i = pos.getZ() - 1; i <= pos.getZ() + 1; ++i) {
            for (int j = pos.getX() - 1; j <= pos.getX() + 1; ++j) {
                for (int k = pos.getY() - 1; k <= pos.getY() + 1; ++k) {
                    this.scheduleChunkRender(ChunkSectionPos.getSectionCoord(j), ChunkSectionPos.getSectionCoord(k), ChunkSectionPos.getSectionCoord(i), important);
                }
            }
        }
    }

    public void scheduleBlockRenders(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        for (int i = minZ - 1; i <= maxZ + 1; ++i) {
            for (int j = minX - 1; j <= maxX + 1; ++j) {
                for (int k = minY - 1; k <= maxY + 1; ++k) {
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
        for (int i = z - 1; i <= z + 1; ++i) {
            for (int j = x - 1; j <= x + 1; ++j) {
                for (int k = y - 1; k <= y + 1; ++k) {
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
        SoundInstance soundInstance = this.playingSongs.get(songPosition);
        if (soundInstance != null) {
            this.client.getSoundManager().stop(soundInstance);
            this.playingSongs.remove(songPosition);
        }
        if (song != null) {
            MusicDiscItem musicDiscItem = MusicDiscItem.bySound(song);
            if (musicDiscItem != null) {
                this.client.inGameHud.setRecordPlayingOverlay(musicDiscItem.getDescription());
            }
            soundInstance = PositionedSoundInstance.record(song, songPosition.getX(), songPosition.getY(), songPosition.getZ());
            this.playingSongs.put(songPosition, soundInstance);
            this.client.getSoundManager().play(soundInstance);
        }
        this.updateEntitiesForSong(this.world, songPosition, song != null);
    }

    private void updateEntitiesForSong(World world, BlockPos pos, boolean playing) {
        List<LivingEntity> list = world.getNonSpectatingEntities(LivingEntity.class, new Box(pos).expand(3.0));
        for (LivingEntity livingEntity : list) {
            livingEntity.setNearbySongPlaying(pos, playing);
        }
    }

    public void addParticle(ParticleEffect parameters, boolean shouldAlwaysSpawn, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
        this.addParticle(parameters, shouldAlwaysSpawn, false, x, y, z, velocityX, velocityY, velocityZ);
    }

    public void addParticle(ParticleEffect parameters, boolean shouldAlwaysSpawn, boolean important, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
        try {
            this.spawnParticle(parameters, shouldAlwaysSpawn, important, x, y, z, velocityX, velocityY, velocityZ);
        } catch (Throwable throwable) {
            CrashReport crashReport = CrashReport.create(throwable, "Exception while adding particle");
            CrashReportSection crashReportSection = crashReport.addElement("Particle being added");
            crashReportSection.add("ID", Registry.PARTICLE_TYPE.getId(parameters.getType()));
            crashReportSection.add("Parameters", parameters.asString());
            crashReportSection.add("Position", () -> CrashReportSection.createPositionString((HeightLimitView)this.world, x, y, z));
            throw new CrashException(crashReport);
        }
    }

    private <T extends ParticleEffect> void addParticle(T parameters, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
        this.addParticle(parameters, parameters.getType().shouldAlwaysSpawn(), x, y, z, velocityX, velocityY, velocityZ);
    }

    @Nullable
    private Particle spawnParticle(ParticleEffect parameters, boolean alwaysSpawn, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
        return this.spawnParticle(parameters, alwaysSpawn, false, x, y, z, velocityX, velocityY, velocityZ);
    }

    @Nullable
    private Particle spawnParticle(ParticleEffect parameters, boolean alwaysSpawn, boolean canSpawnOnMinimal, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
        Camera camera = this.client.gameRenderer.getCamera();
        if (this.client == null || !camera.isReady() || this.client.particleManager == null) {
            return null;
        }
        ParticlesMode particlesMode = this.getRandomParticleSpawnChance(canSpawnOnMinimal);
        if (alwaysSpawn) {
            return this.client.particleManager.addParticle(parameters, x, y, z, velocityX, velocityY, velocityZ);
        }
        if (camera.getPos().squaredDistanceTo(x, y, z) > 1024.0) {
            return null;
        }
        if (particlesMode == ParticlesMode.MINIMAL) {
            return null;
        }
        return this.client.particleManager.addParticle(parameters, x, y, z, velocityX, velocityY, velocityZ);
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
            case 1038: {
                Camera camera = this.client.gameRenderer.getCamera();
                if (!camera.isReady()) break;
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
                    this.world.playSound(h, i, j, SoundEvents.ENTITY_WITHER_SPAWN, SoundCategory.HOSTILE, 1.0f, 1.0f, false);
                    break;
                }
                if (eventId == WorldEvents.END_PORTAL_OPENED) {
                    this.world.playSound(h, i, j, SoundEvents.BLOCK_END_PORTAL_SPAWN, SoundCategory.HOSTILE, 1.0f, 1.0f, false);
                    break;
                }
                this.world.playSound(h, i, j, SoundEvents.ENTITY_ENDER_DRAGON_DEATH, SoundCategory.HOSTILE, 5.0f, 1.0f, false);
            }
        }
    }

    public void processWorldEvent(int eventId, BlockPos pos, int data) {
        Random random = this.world.random;
        switch (eventId) {
            case 1035: {
                this.world.playSound(pos, SoundEvents.BLOCK_BREWING_STAND_BREW, SoundCategory.BLOCKS, 1.0f, 1.0f, false);
                break;
            }
            case 1033: {
                this.world.playSound(pos, SoundEvents.BLOCK_CHORUS_FLOWER_GROW, SoundCategory.BLOCKS, 1.0f, 1.0f, false);
                break;
            }
            case 1034: {
                this.world.playSound(pos, SoundEvents.BLOCK_CHORUS_FLOWER_DEATH, SoundCategory.BLOCKS, 1.0f, 1.0f, false);
                break;
            }
            case 1032: {
                this.client.getSoundManager().play(PositionedSoundInstance.ambient(SoundEvents.BLOCK_PORTAL_TRAVEL, random.nextFloat() * 0.4f + 0.8f, 0.25f));
                break;
            }
            case 1001: {
                this.world.playSound(pos, SoundEvents.BLOCK_DISPENSER_FAIL, SoundCategory.BLOCKS, 1.0f, 1.2f, false);
                break;
            }
            case 1000: {
                this.world.playSound(pos, SoundEvents.BLOCK_DISPENSER_DISPENSE, SoundCategory.BLOCKS, 1.0f, 1.0f, false);
                break;
            }
            case 1003: {
                this.world.playSound(pos, SoundEvents.ENTITY_ENDER_EYE_LAUNCH, SoundCategory.NEUTRAL, 1.0f, 1.2f, false);
                break;
            }
            case 1004: {
                this.world.playSound(pos, SoundEvents.ENTITY_FIREWORK_ROCKET_SHOOT, SoundCategory.NEUTRAL, 1.0f, 1.2f, false);
                break;
            }
            case 1002: {
                this.world.playSound(pos, SoundEvents.BLOCK_DISPENSER_LAUNCH, SoundCategory.BLOCKS, 1.0f, 1.2f, false);
                break;
            }
            case 2000: {
                Direction direction = Direction.byId(data);
                int i = direction.getOffsetX();
                int j = direction.getOffsetY();
                int k = direction.getOffsetZ();
                double d = (double)pos.getX() + (double)i * 0.6 + 0.5;
                double e = (double)pos.getY() + (double)j * 0.6 + 0.5;
                double f = (double)pos.getZ() + (double)k * 0.6 + 0.5;
                for (int l = 0; l < 10; ++l) {
                    double g = random.nextDouble() * 0.2 + 0.01;
                    double h = d + (double)i * 0.01 + (random.nextDouble() - 0.5) * (double)k * 0.5;
                    double m = e + (double)j * 0.01 + (random.nextDouble() - 0.5) * (double)j * 0.5;
                    double n = f + (double)k * 0.01 + (random.nextDouble() - 0.5) * (double)i * 0.5;
                    double o = (double)i * g + random.nextGaussian() * 0.01;
                    double p = (double)j * g + random.nextGaussian() * 0.01;
                    double q = (double)k * g + random.nextGaussian() * 0.01;
                    this.addParticle(ParticleTypes.SMOKE, h, m, n, o, p, q);
                }
                break;
            }
            case 2003: {
                double r = (double)pos.getX() + 0.5;
                double s = pos.getY();
                double d = (double)pos.getZ() + 0.5;
                for (int t = 0; t < 8; ++t) {
                    this.addParticle(new ItemStackParticleEffect(ParticleTypes.ITEM, new ItemStack(Items.ENDER_EYE)), r, s, d, random.nextGaussian() * 0.15, random.nextDouble() * 0.2, random.nextGaussian() * 0.15);
                }
                for (double e = 0.0; e < Math.PI * 2; e += 0.15707963267948966) {
                    this.addParticle(ParticleTypes.PORTAL, r + Math.cos(e) * 5.0, s - 0.4, d + Math.sin(e) * 5.0, Math.cos(e) * -5.0, 0.0, Math.sin(e) * -5.0);
                    this.addParticle(ParticleTypes.PORTAL, r + Math.cos(e) * 5.0, s - 0.4, d + Math.sin(e) * 5.0, Math.cos(e) * -7.0, 0.0, Math.sin(e) * -7.0);
                }
                break;
            }
            case 2002: 
            case 2007: {
                Vec3d vec3d = Vec3d.ofBottomCenter(pos);
                for (int i = 0; i < 8; ++i) {
                    this.addParticle(new ItemStackParticleEffect(ParticleTypes.ITEM, new ItemStack(Items.SPLASH_POTION)), vec3d.x, vec3d.y, vec3d.z, random.nextGaussian() * 0.15, random.nextDouble() * 0.2, random.nextGaussian() * 0.15);
                }
                float u = (float)(data >> 16 & 0xFF) / 255.0f;
                float v = (float)(data >> 8 & 0xFF) / 255.0f;
                float w = (float)(data >> 0 & 0xFF) / 255.0f;
                DefaultParticleType particleEffect = eventId == 2007 ? ParticleTypes.INSTANT_EFFECT : ParticleTypes.EFFECT;
                for (int x = 0; x < 100; ++x) {
                    double e = random.nextDouble() * 4.0;
                    double f = random.nextDouble() * Math.PI * 2.0;
                    double y = Math.cos(f) * e;
                    double z = 0.01 + random.nextDouble() * 0.5;
                    double aa = Math.sin(f) * e;
                    Particle particle = this.spawnParticle(particleEffect, particleEffect.getType().shouldAlwaysSpawn(), vec3d.x + y * 0.1, vec3d.y + 0.3, vec3d.z + aa * 0.1, y, z, aa);
                    if (particle == null) continue;
                    float ab = 0.75f + random.nextFloat() * 0.25f;
                    particle.setColor(u * ab, v * ab, w * ab);
                    particle.move((float)e);
                }
                this.world.playSound(pos, SoundEvents.ENTITY_SPLASH_POTION_BREAK, SoundCategory.NEUTRAL, 1.0f, random.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 2001: {
                BlockState blockState = Block.getStateFromRawId(data);
                if (!blockState.isAir()) {
                    BlockSoundGroup blockSoundGroup = blockState.getSoundGroup();
                    this.world.playSound(pos, blockSoundGroup.getBreakSound(), SoundCategory.BLOCKS, (blockSoundGroup.getVolume() + 1.0f) / 2.0f, blockSoundGroup.getPitch() * 0.8f, false);
                }
                this.world.addBlockBreakParticles(pos, blockState);
                break;
            }
            case 2004: {
                for (int i = 0; i < 20; ++i) {
                    double s = (double)pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 2.0;
                    double d = (double)pos.getY() + 0.5 + (random.nextDouble() - 0.5) * 2.0;
                    double e = (double)pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 2.0;
                    this.world.addParticle(ParticleTypes.SMOKE, s, d, e, 0.0, 0.0, 0.0);
                    this.world.addParticle(ParticleTypes.FLAME, s, d, e, 0.0, 0.0, 0.0);
                }
                break;
            }
            case 2005: {
                BoneMealItem.createParticles(this.world, pos, data);
                break;
            }
            case 1505: {
                BoneMealItem.createParticles(this.world, pos, data);
                this.world.playSound(pos, SoundEvents.ITEM_BONE_MEAL_USE, SoundCategory.BLOCKS, 1.0f, 1.0f, false);
                break;
            }
            case 3002: {
                if (data >= 0 && data < Direction.Axis.VALUES.length) {
                    ParticleUtil.spawnParticle(Direction.Axis.VALUES[data], this.world, pos, 0.125, ParticleTypes.ELECTRIC_SPARK, UniformIntProvider.create(10, 19));
                    break;
                }
                ParticleUtil.spawnParticle(this.world, pos, ParticleTypes.ELECTRIC_SPARK, UniformIntProvider.create(3, 5));
                break;
            }
            case 3006: {
                int i = data >> 6;
                if (i > 0) {
                    if (random.nextFloat() < 0.3f + (float)i * 0.1f) {
                        float v = 0.15f + 0.02f * (float)i * (float)i * random.nextFloat();
                        float w = 0.4f + 0.3f * (float)i * random.nextFloat();
                        this.world.playSound(pos, SoundEvents.BLOCK_SCULK_CHARGE, SoundCategory.BLOCKS, v, w, false);
                    }
                    byte b = (byte)(data & 0x3F);
                    UniformIntProvider intProvider = UniformIntProvider.create(0, i);
                    float ac = 0.005f;
                    Supplier<Vec3d> supplier = () -> new Vec3d(MathHelper.nextDouble(random, -0.005f, 0.005f), MathHelper.nextDouble(random, -0.005f, 0.005f), MathHelper.nextDouble(random, -0.005f, 0.005f));
                    if (b == 0) {
                        for (Direction direction2 : Direction.values()) {
                            float ad = direction2 == Direction.DOWN ? (float)Math.PI : 0.0f;
                            double g = direction2.getAxis() == Direction.Axis.Y ? 0.65 : 0.57;
                            ParticleUtil.spawnParticles(this.world, pos, new SculkChargeParticleEffect(ad), intProvider, direction2, supplier, g);
                        }
                    } else {
                        for (Direction direction3 : MultifaceGrowthBlock.flagToDirections(b)) {
                            float ae = direction3 == Direction.UP ? (float)Math.PI : 0.0f;
                            double af = 0.35;
                            ParticleUtil.spawnParticles(this.world, pos, new SculkChargeParticleEffect(ae), intProvider, direction3, supplier, 0.35);
                        }
                    }
                } else {
                    this.world.playSound(pos, SoundEvents.BLOCK_SCULK_CHARGE, SoundCategory.BLOCKS, 1.0f, 1.0f, false);
                    boolean bl = this.world.getBlockState(pos).isFullCube(this.world, pos);
                    int k = bl ? 40 : 20;
                    float ac = bl ? 0.45f : 0.25f;
                    float ag = 0.07f;
                    for (int t = 0; t < k; ++t) {
                        float ah = 2.0f * random.nextFloat() - 1.0f;
                        float ae = 2.0f * random.nextFloat() - 1.0f;
                        float ai = 2.0f * random.nextFloat() - 1.0f;
                        this.world.addParticle(ParticleTypes.SCULK_CHARGE_POP, (double)pos.getX() + 0.5 + (double)(ah * ac), (double)pos.getY() + 0.5 + (double)(ae * ac), (double)pos.getZ() + 0.5 + (double)(ai * ac), ah * 0.07f, ae * 0.07f, ai * 0.07f);
                    }
                }
                break;
            }
            case 3007: {
                for (int j = 0; j < 10; ++j) {
                    this.world.addParticle(new ShriekParticleEffect(j * 5), false, (double)pos.getX() + 0.5, (double)pos.getY() + SculkShriekerBlock.TOP, (double)pos.getZ() + 0.5, 0.0, 0.0, 0.0);
                }
                this.world.playSound((double)pos.getX() + 0.5, (double)pos.getY() + SculkShriekerBlock.TOP, (double)pos.getZ() + 0.5, SoundEvents.BLOCK_SCULK_SHRIEKER_SHRIEK, SoundCategory.BLOCKS, 2.0f, 0.6f + this.world.random.nextFloat() * 0.4f, false);
                break;
            }
            case 3003: {
                ParticleUtil.spawnParticle(this.world, pos, ParticleTypes.WAX_ON, UniformIntProvider.create(3, 5));
                this.world.playSound(pos, SoundEvents.ITEM_HONEYCOMB_WAX_ON, SoundCategory.BLOCKS, 1.0f, 1.0f, false);
                break;
            }
            case 3004: {
                ParticleUtil.spawnParticle(this.world, pos, ParticleTypes.WAX_OFF, UniformIntProvider.create(3, 5));
                break;
            }
            case 3005: {
                ParticleUtil.spawnParticle(this.world, pos, ParticleTypes.SCRAPE, UniformIntProvider.create(3, 5));
                break;
            }
            case 2008: {
                this.world.addParticle(ParticleTypes.EXPLOSION, (double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, 0.0, 0.0, 0.0);
                break;
            }
            case 1500: {
                ComposterBlock.playEffects(this.world, pos, data > 0);
                break;
            }
            case 1504: {
                PointedDripstoneBlock.createParticle(this.world, pos, this.world.getBlockState(pos));
                break;
            }
            case 1501: {
                this.world.playSound(pos, SoundEvents.BLOCK_LAVA_EXTINGUISH, SoundCategory.BLOCKS, 0.5f, 2.6f + (random.nextFloat() - random.nextFloat()) * 0.8f, false);
                for (int j = 0; j < 8; ++j) {
                    this.world.addParticle(ParticleTypes.LARGE_SMOKE, (double)pos.getX() + random.nextDouble(), (double)pos.getY() + 1.2, (double)pos.getZ() + random.nextDouble(), 0.0, 0.0, 0.0);
                }
                break;
            }
            case 1502: {
                this.world.playSound(pos, SoundEvents.BLOCK_REDSTONE_TORCH_BURNOUT, SoundCategory.BLOCKS, 0.5f, 2.6f + (random.nextFloat() - random.nextFloat()) * 0.8f, false);
                for (int j = 0; j < 5; ++j) {
                    double aj = (double)pos.getX() + random.nextDouble() * 0.6 + 0.2;
                    double ak = (double)pos.getY() + random.nextDouble() * 0.6 + 0.2;
                    double al = (double)pos.getZ() + random.nextDouble() * 0.6 + 0.2;
                    this.world.addParticle(ParticleTypes.SMOKE, aj, ak, al, 0.0, 0.0, 0.0);
                }
                break;
            }
            case 1503: {
                this.world.playSound(pos, SoundEvents.BLOCK_END_PORTAL_FRAME_FILL, SoundCategory.BLOCKS, 1.0f, 1.0f, false);
                for (int j = 0; j < 16; ++j) {
                    double aj = (double)pos.getX() + (5.0 + random.nextDouble() * 6.0) / 16.0;
                    double ak = (double)pos.getY() + 0.8125;
                    double al = (double)pos.getZ() + (5.0 + random.nextDouble() * 6.0) / 16.0;
                    this.world.addParticle(ParticleTypes.SMOKE, aj, ak, al, 0.0, 0.0, 0.0);
                }
                break;
            }
            case 2006: {
                for (int j = 0; j < 200; ++j) {
                    float w = random.nextFloat() * 4.0f;
                    float ac = random.nextFloat() * ((float)Math.PI * 2);
                    double ak = MathHelper.cos(ac) * w;
                    double al = 0.01 + random.nextDouble() * 0.5;
                    double af = MathHelper.sin(ac) * w;
                    Particle particle2 = this.spawnParticle(ParticleTypes.DRAGON_BREATH, false, (double)pos.getX() + ak * 0.1, (double)pos.getY() + 0.3, (double)pos.getZ() + af * 0.1, ak, al, af);
                    if (particle2 == null) continue;
                    particle2.move(w);
                }
                if (data != 1) break;
                this.world.playSound(pos, SoundEvents.ENTITY_DRAGON_FIREBALL_EXPLODE, SoundCategory.HOSTILE, 1.0f, random.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 2009: {
                for (int j = 0; j < 8; ++j) {
                    this.world.addParticle(ParticleTypes.CLOUD, (double)pos.getX() + random.nextDouble(), (double)pos.getY() + 1.2, (double)pos.getZ() + random.nextDouble(), 0.0, 0.0, 0.0);
                }
                break;
            }
            case 1012: {
                this.world.playSound(pos, SoundEvents.BLOCK_WOODEN_DOOR_CLOSE, SoundCategory.BLOCKS, 1.0f, random.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1036: {
                this.world.playSound(pos, SoundEvents.BLOCK_IRON_TRAPDOOR_CLOSE, SoundCategory.BLOCKS, 1.0f, random.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1013: {
                this.world.playSound(pos, SoundEvents.BLOCK_WOODEN_TRAPDOOR_CLOSE, SoundCategory.BLOCKS, 1.0f, random.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1014: {
                this.world.playSound(pos, SoundEvents.BLOCK_FENCE_GATE_CLOSE, SoundCategory.BLOCKS, 1.0f, random.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1011: {
                this.world.playSound(pos, SoundEvents.BLOCK_IRON_DOOR_CLOSE, SoundCategory.BLOCKS, 1.0f, random.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1006: {
                this.world.playSound(pos, SoundEvents.BLOCK_WOODEN_DOOR_OPEN, SoundCategory.BLOCKS, 1.0f, random.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1007: {
                this.world.playSound(pos, SoundEvents.BLOCK_WOODEN_TRAPDOOR_OPEN, SoundCategory.BLOCKS, 1.0f, random.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1037: {
                this.world.playSound(pos, SoundEvents.BLOCK_IRON_TRAPDOOR_OPEN, SoundCategory.BLOCKS, 1.0f, random.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1008: {
                this.world.playSound(pos, SoundEvents.BLOCK_FENCE_GATE_OPEN, SoundCategory.BLOCKS, 1.0f, random.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1005: {
                this.world.playSound(pos, SoundEvents.BLOCK_IRON_DOOR_OPEN, SoundCategory.BLOCKS, 1.0f, random.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1009: {
                if (data == 0) {
                    this.world.playSound(pos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.5f, 2.6f + (random.nextFloat() - random.nextFloat()) * 0.8f, false);
                    break;
                }
                if (data != 1) break;
                this.world.playSound(pos, SoundEvents.ENTITY_GENERIC_EXTINGUISH_FIRE, SoundCategory.BLOCKS, 0.7f, 1.6f + (random.nextFloat() - random.nextFloat()) * 0.4f, false);
                break;
            }
            case 1029: {
                this.world.playSound(pos, SoundEvents.BLOCK_ANVIL_DESTROY, SoundCategory.BLOCKS, 1.0f, random.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1030: {
                this.world.playSound(pos, SoundEvents.BLOCK_ANVIL_USE, SoundCategory.BLOCKS, 1.0f, random.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1044: {
                this.world.playSound(pos, SoundEvents.BLOCK_SMITHING_TABLE_USE, SoundCategory.BLOCKS, 1.0f, this.world.random.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1031: {
                this.world.playSound(pos, SoundEvents.BLOCK_ANVIL_LAND, SoundCategory.BLOCKS, 0.3f, this.world.random.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1039: {
                this.world.playSound(pos, SoundEvents.ENTITY_PHANTOM_BITE, SoundCategory.HOSTILE, 0.3f, this.world.random.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1010: {
                if (Item.byRawId(data) instanceof MusicDiscItem) {
                    this.playSong(((MusicDiscItem)Item.byRawId(data)).getSound(), pos);
                    break;
                }
                this.playSong(null, pos);
                break;
            }
            case 1015: {
                this.world.playSound(pos, SoundEvents.ENTITY_GHAST_WARN, SoundCategory.HOSTILE, 10.0f, (random.nextFloat() - random.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1017: {
                this.world.playSound(pos, SoundEvents.ENTITY_ENDER_DRAGON_SHOOT, SoundCategory.HOSTILE, 10.0f, (random.nextFloat() - random.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1016: {
                this.world.playSound(pos, SoundEvents.ENTITY_GHAST_SHOOT, SoundCategory.HOSTILE, 10.0f, (random.nextFloat() - random.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1019: {
                this.world.playSound(pos, SoundEvents.ENTITY_ZOMBIE_ATTACK_WOODEN_DOOR, SoundCategory.HOSTILE, 2.0f, (random.nextFloat() - random.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1022: {
                this.world.playSound(pos, SoundEvents.ENTITY_WITHER_BREAK_BLOCK, SoundCategory.HOSTILE, 2.0f, (random.nextFloat() - random.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1021: {
                this.world.playSound(pos, SoundEvents.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR, SoundCategory.HOSTILE, 2.0f, (random.nextFloat() - random.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1020: {
                this.world.playSound(pos, SoundEvents.ENTITY_ZOMBIE_ATTACK_IRON_DOOR, SoundCategory.HOSTILE, 2.0f, (random.nextFloat() - random.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1018: {
                this.world.playSound(pos, SoundEvents.ENTITY_BLAZE_SHOOT, SoundCategory.HOSTILE, 2.0f, (random.nextFloat() - random.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1024: {
                this.world.playSound(pos, SoundEvents.ENTITY_WITHER_SHOOT, SoundCategory.HOSTILE, 2.0f, (random.nextFloat() - random.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1026: {
                this.world.playSound(pos, SoundEvents.ENTITY_ZOMBIE_INFECT, SoundCategory.HOSTILE, 2.0f, (random.nextFloat() - random.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1027: {
                this.world.playSound(pos, SoundEvents.ENTITY_ZOMBIE_VILLAGER_CONVERTED, SoundCategory.HOSTILE, 2.0f, (random.nextFloat() - random.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1040: {
                this.world.playSound(pos, SoundEvents.ENTITY_ZOMBIE_CONVERTED_TO_DROWNED, SoundCategory.HOSTILE, 2.0f, (random.nextFloat() - random.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1041: {
                this.world.playSound(pos, SoundEvents.ENTITY_HUSK_CONVERTED_TO_ZOMBIE, SoundCategory.HOSTILE, 2.0f, (random.nextFloat() - random.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1025: {
                this.world.playSound(pos, SoundEvents.ENTITY_BAT_TAKEOFF, SoundCategory.NEUTRAL, 0.05f, (random.nextFloat() - random.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1042: {
                this.world.playSound(pos, SoundEvents.BLOCK_GRINDSTONE_USE, SoundCategory.BLOCKS, 1.0f, this.world.random.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1043: {
                this.world.playSound(pos, SoundEvents.ITEM_BOOK_PAGE_TURN, SoundCategory.BLOCKS, 1.0f, this.world.random.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 3000: {
                this.world.addParticle(ParticleTypes.EXPLOSION_EMITTER, true, (double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, 0.0, 0.0, 0.0);
                this.world.playSound(pos, SoundEvents.BLOCK_END_GATEWAY_SPAWN, SoundCategory.BLOCKS, 10.0f, (1.0f + (this.world.random.nextFloat() - this.world.random.nextFloat()) * 0.2f) * 0.7f, false);
                break;
            }
            case 3001: {
                this.world.playSound(pos, SoundEvents.ENTITY_ENDER_DRAGON_GROWL, SoundCategory.HOSTILE, 64.0f, 0.8f + this.world.random.nextFloat() * 0.3f, false);
                break;
            }
            case 1045: {
                this.world.playSound(pos, SoundEvents.BLOCK_POINTED_DRIPSTONE_LAND, SoundCategory.BLOCKS, 2.0f, this.world.random.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1046: {
                this.world.playSound(pos, SoundEvents.BLOCK_POINTED_DRIPSTONE_DRIP_LAVA_INTO_CAULDRON, SoundCategory.BLOCKS, 2.0f, this.world.random.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1047: {
                this.world.playSound(pos, SoundEvents.BLOCK_POINTED_DRIPSTONE_DRIP_WATER_INTO_CAULDRON, SoundCategory.BLOCKS, 2.0f, this.world.random.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1048: {
                this.world.playSound(pos, SoundEvents.ENTITY_SKELETON_CONVERTED_TO_STRAY, SoundCategory.HOSTILE, 2.0f, (random.nextFloat() - random.nextFloat()) * 0.2f + 1.0f, false);
            }
        }
    }

    public void setBlockBreakingInfo(int entityId, BlockPos pos, int stage) {
        if (stage < 0 || stage >= 10) {
            BlockBreakingInfo blockBreakingInfo = (BlockBreakingInfo)this.blockBreakingInfos.remove(entityId);
            if (blockBreakingInfo != null) {
                this.removeBlockBreakingInfo(blockBreakingInfo);
            }
        } else {
            BlockBreakingInfo blockBreakingInfo = (BlockBreakingInfo)this.blockBreakingInfos.get(entityId);
            if (blockBreakingInfo != null) {
                this.removeBlockBreakingInfo(blockBreakingInfo);
            }
            if (blockBreakingInfo == null || blockBreakingInfo.getPos().getX() != pos.getX() || blockBreakingInfo.getPos().getY() != pos.getY() || blockBreakingInfo.getPos().getZ() != pos.getZ()) {
                blockBreakingInfo = new BlockBreakingInfo(entityId, pos);
                this.blockBreakingInfos.put(entityId, blockBreakingInfo);
            }
            blockBreakingInfo.setStage(stage);
            blockBreakingInfo.setLastUpdateTick(this.ticks);
            this.blockBreakingProgressions.computeIfAbsent(blockBreakingInfo.getPos().asLong(), l -> Sets.newTreeSet()).add(blockBreakingInfo);
        }
    }

    public boolean isTerrainRenderComplete() {
        return this.chunkBuilder.isEmpty();
    }

    public void scheduleTerrainUpdate() {
        this.shouldUpdate = true;
        this.cloudsDirty = true;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void updateNoCullingBlockEntities(Collection<BlockEntity> removed, Collection<BlockEntity> added) {
        Set<BlockEntity> set = this.noCullingBlockEntities;
        synchronized (set) {
            this.noCullingBlockEntities.removeAll(removed);
            this.noCullingBlockEntities.addAll(added);
        }
    }

    public static int getLightmapCoordinates(BlockRenderView world, BlockPos pos) {
        return WorldRenderer.getLightmapCoordinates(world, world.getBlockState(pos), pos);
    }

    public static int getLightmapCoordinates(BlockRenderView world, BlockState state, BlockPos pos) {
        int k;
        if (state.hasEmissiveLighting(world, pos)) {
            return 0xF000F0;
        }
        int i = world.getLightLevel(LightType.SKY, pos);
        int j = world.getLightLevel(LightType.BLOCK, pos);
        if (j < (k = state.getLuminance())) {
            j = k;
        }
        return i << 20 | j << 4;
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

    @Environment(value=EnvType.CLIENT)
    public static class ShaderException
    extends RuntimeException {
        public ShaderException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class RenderableChunks {
        public final ChunkInfoList chunkInfoList;
        public final LinkedHashSet<ChunkInfo> chunks;

        public RenderableChunks(int chunkCount) {
            this.chunkInfoList = new ChunkInfoList(chunkCount);
            this.chunks = new LinkedHashSet(chunkCount);
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class ChunkInfo {
        final ChunkBuilder.BuiltChunk chunk;
        private byte direction;
        byte cullingState;
        final int propagationLevel;

        ChunkInfo(ChunkBuilder.BuiltChunk chunk, @Nullable Direction direction, int propagationLevel) {
            this.chunk = chunk;
            if (direction != null) {
                this.addDirection(direction);
            }
            this.propagationLevel = propagationLevel;
        }

        public void updateCullingState(byte parentCullingState, Direction from) {
            this.cullingState = (byte)(this.cullingState | (parentCullingState | 1 << from.ordinal()));
        }

        public boolean canCull(Direction from) {
            return (this.cullingState & 1 << from.ordinal()) > 0;
        }

        public void addDirection(Direction direction) {
            this.direction = (byte)(this.direction | (this.direction | 1 << direction.ordinal()));
        }

        public boolean hasDirection(int ordinal) {
            return (this.direction & 1 << ordinal) > 0;
        }

        public boolean hasAnyDirection() {
            return this.direction != 0;
        }

        public int hashCode() {
            return this.chunk.getOrigin().hashCode();
        }

        public boolean equals(Object o) {
            if (!(o instanceof ChunkInfo)) {
                return false;
            }
            ChunkInfo chunkInfo = (ChunkInfo)o;
            return this.chunk.getOrigin().equals(chunkInfo.chunk.getOrigin());
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class ChunkInfoList {
        private final ChunkInfo[] current;

        ChunkInfoList(int size) {
            this.current = new ChunkInfo[size];
        }

        public void setInfo(ChunkBuilder.BuiltChunk chunk, ChunkInfo info) {
            this.current[chunk.index] = info;
        }

        @Nullable
        public ChunkInfo getInfo(ChunkBuilder.BuiltChunk chunk) {
            int i = chunk.index;
            if (i < 0 || i >= this.current.length) {
                return null;
            }
            return this.current[i];
        }
    }
}

