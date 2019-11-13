/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
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
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.SortedSet;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.ComposterBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.ShaderEffect;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.options.CloudRenderMode;
import net.minecraft.client.options.ParticlesOption;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.BlockBreakingInfo;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferBuilderStorage;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.BuiltChunkStorage;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.DelegatingVertexConsumer;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.GuiLighting;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OutlineVertexConsumerProvider;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.TransformingVertexConsumer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.chunk.ChunkBuilder;
import net.minecraft.client.render.chunk.ChunkOcclusionGraphBuilder;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3d;
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
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SynchronousResourceReloadListener;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.BlockView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.dimension.DimensionType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class WorldRenderer
implements AutoCloseable,
SynchronousResourceReloadListener {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Identifier MOON_PHASES = new Identifier("textures/environment/moon_phases.png");
    private static final Identifier SUN = new Identifier("textures/environment/sun.png");
    private static final Identifier CLOUDS = new Identifier("textures/environment/clouds.png");
    private static final Identifier END_SKY = new Identifier("textures/environment/end_sky.png");
    private static final Identifier FORCEFIELD = new Identifier("textures/misc/forcefield.png");
    private static final Identifier RAIN = new Identifier("textures/environment/rain.png");
    private static final Identifier SNOW = new Identifier("textures/environment/snow.png");
    public static final Direction[] DIRECTIONS = Direction.values();
    private final MinecraftClient client;
    private final TextureManager textureManager;
    private final EntityRenderDispatcher entityRenderDispatcher;
    private final BufferBuilderStorage bufferBuilders;
    private ClientWorld world;
    private Set<ChunkBuilder.BuiltChunk> chunksToRebuild = Sets.newLinkedHashSet();
    private List<BuiltChunkInfo> chunkInfos = Lists.newArrayListWithCapacity(69696);
    private final Set<BlockEntity> blockEntities = Sets.newHashSet();
    private BuiltChunkStorage renderedChunks;
    private final VertexFormat field_4100 = VertexFormats.POSITION;
    @Nullable
    private VertexBuffer starsBuffer;
    @Nullable
    private VertexBuffer field_4087;
    @Nullable
    private VertexBuffer field_4102;
    private boolean cloudsDirty = true;
    @Nullable
    private VertexBuffer cloudsBuffer;
    private int ticks;
    private final Int2ObjectMap<BlockBreakingInfo> blockBreakingInfos = new Int2ObjectOpenHashMap<BlockBreakingInfo>();
    private final Long2ObjectMap<SortedSet<BlockBreakingInfo>> blockBreakingProgressions = new Long2ObjectOpenHashMap<SortedSet<BlockBreakingInfo>>();
    private final Map<BlockPos, SoundInstance> playingSongs = Maps.newHashMap();
    private Framebuffer entityOutlinesFramebuffer;
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
    private Vec3d field_4072 = Vec3d.ZERO;
    private CloudRenderMode field_4080;
    private ChunkBuilder chunkBuilder;
    private final VertexFormat field_20791 = VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL;
    private int renderDistance = -1;
    private int regularEntityCount;
    private int blockEntityCount;
    private boolean shouldCaptureFrustum;
    @Nullable
    private Frustum capturedFrustum;
    private final Vector4f[] field_4065 = new Vector4f[8];
    private final Vector3d capturedFrustumPosition = new Vector3d(0.0, 0.0, 0.0);
    private double lastTranslucentSortX;
    private double lastTranslucentSortY;
    private double lastTranslucentSortZ;
    private boolean needsTerrainUpdate = true;
    private int frame;
    private int field_20793;
    private final float[] field_20794 = new float[1024];
    private final float[] field_20795 = new float[1024];

    public WorldRenderer(MinecraftClient minecraftClient, BufferBuilderStorage bufferBuilderStorage) {
        this.client = minecraftClient;
        this.entityRenderDispatcher = minecraftClient.getEntityRenderManager();
        this.bufferBuilders = bufferBuilderStorage;
        this.textureManager = minecraftClient.getTextureManager();
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
        this.method_3277();
        this.method_3265();
    }

    private void renderWeather(LightmapTextureManager lightmapTextureManager, float f, double d, double e, double g) {
        float h = this.client.world.getRainGradient(f);
        if (h <= 0.0f) {
            return;
        }
        lightmapTextureManager.enable();
        ClientWorld world = this.client.world;
        int i = MathHelper.floor(d);
        int j = MathHelper.floor(e);
        int k = MathHelper.floor(g);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        RenderSystem.disableCull();
        RenderSystem.normal3f(0.0f, 1.0f, 0.0f);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.defaultAlphaFunc();
        int l = 5;
        if (this.client.options.fancyGraphics) {
            l = 10;
        }
        int m = -1;
        float n = (float)this.ticks + f;
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        for (int o = k - l; o <= k + l; ++o) {
            for (int p = i - l; p <= i + l; ++p) {
                float ad;
                float z;
                int w;
                int q = (o - k + 16) * 32 + p - i + 16;
                double r = (double)this.field_20794[q] * 0.5;
                double s = (double)this.field_20795[q] * 0.5;
                mutable.set(p, 0, o);
                Biome biome = world.method_23753(mutable);
                if (biome.getPrecipitation() == Biome.Precipitation.NONE) continue;
                int t = world.getTopPosition(Heightmap.Type.MOTION_BLOCKING, mutable).getY();
                int u = j - l;
                int v = j + l;
                if (u < t) {
                    u = t;
                }
                if (v < t) {
                    v = t;
                }
                if ((w = t) < j) {
                    w = j;
                }
                if (u == v) continue;
                Random random = new Random(p * p * 3121 + p * 45238971 ^ o * o * 418711 + o * 13761);
                mutable.set(p, u, o);
                float x = biome.getTemperature(mutable);
                if (x >= 0.15f) {
                    if (m != 0) {
                        if (m >= 0) {
                            tessellator.draw();
                        }
                        m = 0;
                        this.client.getTextureManager().bindTexture(RAIN);
                        bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR_LIGHT);
                    }
                    int y = this.ticks + p * p * 3121 + p * 45238971 + o * o * 418711 + o * 13761 & 0x1F;
                    z = -((float)y + f) / 32.0f * (3.0f + random.nextFloat());
                    double aa = (double)((float)p + 0.5f) - d;
                    double ab = (double)((float)o + 0.5f) - g;
                    float ac = MathHelper.sqrt(aa * aa + ab * ab) / (float)l;
                    ad = ((1.0f - ac * ac) * 0.5f + 0.5f) * h;
                    mutable.set(p, w, o);
                    int ae = WorldRenderer.method_23794(world, mutable);
                    bufferBuilder.vertex((double)p - d - r + 0.5, (double)v - e, (double)o - g - s + 0.5).texture(0.0f, (float)u * 0.25f + z).color(1.0f, 1.0f, 1.0f, ad).light(ae).next();
                    bufferBuilder.vertex((double)p - d + r + 0.5, (double)v - e, (double)o - g + s + 0.5).texture(1.0f, (float)u * 0.25f + z).color(1.0f, 1.0f, 1.0f, ad).light(ae).next();
                    bufferBuilder.vertex((double)p - d + r + 0.5, (double)u - e, (double)o - g + s + 0.5).texture(1.0f, (float)v * 0.25f + z).color(1.0f, 1.0f, 1.0f, ad).light(ae).next();
                    bufferBuilder.vertex((double)p - d - r + 0.5, (double)u - e, (double)o - g - s + 0.5).texture(0.0f, (float)v * 0.25f + z).color(1.0f, 1.0f, 1.0f, ad).light(ae).next();
                    continue;
                }
                if (m != 1) {
                    if (m >= 0) {
                        tessellator.draw();
                    }
                    m = 1;
                    this.client.getTextureManager().bindTexture(SNOW);
                    bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR_LIGHT);
                }
                float af = -((float)(this.ticks & 0x1FF) + f) / 512.0f;
                z = (float)(random.nextDouble() + (double)n * 0.01 * (double)((float)random.nextGaussian()));
                float ag = (float)(random.nextDouble() + (double)(n * (float)random.nextGaussian()) * 0.001);
                double ah = (double)((float)p + 0.5f) - d;
                double ai = (double)((float)o + 0.5f) - g;
                ad = MathHelper.sqrt(ah * ah + ai * ai) / (float)l;
                float aj = ((1.0f - ad * ad) * 0.3f + 0.5f) * h;
                mutable.set(p, w, o);
                int ak = WorldRenderer.method_23794(world, mutable);
                int al = ak >> 16 & 0xFFFF;
                int am = (ak & 0xFFFF) * 3;
                int an = (al * 3 + 240) / 4;
                int ao = (am * 3 + 240) / 4;
                bufferBuilder.vertex((double)p - d - r + 0.5, (double)v - e, (double)o - g - s + 0.5).texture(0.0f + z, (float)u * 0.25f + af + ag).color(1.0f, 1.0f, 1.0f, aj).light(ao, an).next();
                bufferBuilder.vertex((double)p - d + r + 0.5, (double)v - e, (double)o - g + s + 0.5).texture(1.0f + z, (float)u * 0.25f + af + ag).color(1.0f, 1.0f, 1.0f, aj).light(ao, an).next();
                bufferBuilder.vertex((double)p - d + r + 0.5, (double)u - e, (double)o - g + s + 0.5).texture(1.0f + z, (float)v * 0.25f + af + ag).color(1.0f, 1.0f, 1.0f, aj).light(ao, an).next();
                bufferBuilder.vertex((double)p - d - r + 0.5, (double)u - e, (double)o - g - s + 0.5).texture(0.0f + z, (float)v * 0.25f + af + ag).color(1.0f, 1.0f, 1.0f, aj).light(ao, an).next();
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

    public void method_22713(Camera camera) {
        float f = this.client.world.getRainGradient(1.0f);
        if (!this.client.options.fancyGraphics) {
            f /= 2.0f;
        }
        if (f == 0.0f) {
            return;
        }
        Random random = new Random((long)this.ticks * 312987231L);
        ClientWorld worldView = this.client.world;
        BlockPos blockPos = new BlockPos(camera.getPos());
        int i = 10;
        double d = 0.0;
        double e = 0.0;
        double g = 0.0;
        int j = 0;
        int k = (int)(100.0f * f * f);
        if (this.client.options.particles == ParticlesOption.DECREASED) {
            k >>= 1;
        } else if (this.client.options.particles == ParticlesOption.MINIMAL) {
            k = 0;
        }
        for (int l = 0; l < k; ++l) {
            double q;
            double p;
            double o;
            BlockPos blockPos2 = worldView.getTopPosition(Heightmap.Type.MOTION_BLOCKING, blockPos.add(random.nextInt(10) - random.nextInt(10), 0, random.nextInt(10) - random.nextInt(10)));
            Biome biome = worldView.method_23753(blockPos2);
            BlockPos blockPos3 = blockPos2.method_10074();
            if (blockPos2.getY() > blockPos.getY() + 10 || blockPos2.getY() < blockPos.getY() - 10 || biome.getPrecipitation() != Biome.Precipitation.RAIN || !(biome.getTemperature(blockPos2) >= 0.15f)) continue;
            double h = random.nextDouble();
            double m = random.nextDouble();
            BlockState blockState = worldView.getBlockState(blockPos3);
            FluidState fluidState = worldView.getFluidState(blockPos2);
            VoxelShape voxelShape = blockState.getCollisionShape(worldView, blockPos3);
            double n = voxelShape.getEndingCoord(Direction.Axis.Y, h, m);
            if (n >= (o = (double)fluidState.getHeight(worldView, blockPos2))) {
                p = n;
                q = voxelShape.getBeginningCoord(Direction.Axis.Y, h, m);
            } else {
                p = 0.0;
                q = 0.0;
            }
            if (!(p > -1.7976931348623157E308)) continue;
            if (fluidState.matches(FluidTags.LAVA) || blockState.getBlock() == Blocks.MAGMA_BLOCK || blockState.getBlock() == Blocks.CAMPFIRE && blockState.get(CampfireBlock.LIT).booleanValue()) {
                this.client.world.addParticle(ParticleTypes.SMOKE, (double)blockPos2.getX() + h, (double)((float)blockPos2.getY() + 0.1f) - q, (double)blockPos2.getZ() + m, 0.0, 0.0, 0.0);
                continue;
            }
            if (random.nextInt(++j) == 0) {
                d = (double)blockPos3.getX() + h;
                e = (double)((float)blockPos3.getY() + 0.1f) + p - 1.0;
                g = (double)blockPos3.getZ() + m;
            }
            this.client.world.addParticle(ParticleTypes.RAIN, (double)blockPos3.getX() + h, (double)((float)blockPos3.getY() + 0.1f) + p, (double)blockPos3.getZ() + m, 0.0, 0.0, 0.0);
        }
        if (j > 0 && random.nextInt(3) < this.field_20793++) {
            this.field_20793 = 0;
            if (e > (double)(blockPos.getY() + 1) && worldView.getTopPosition(Heightmap.Type.MOTION_BLOCKING, blockPos).getY() > MathHelper.floor(blockPos.getY())) {
                this.client.world.playSound(d, e, g, SoundEvents.WEATHER_RAIN_ABOVE, SoundCategory.WEATHER, 0.1f, 0.5f, false);
            } else {
                this.client.world.playSound(d, e, g, SoundEvents.WEATHER_RAIN, SoundCategory.WEATHER, 0.2f, 1.0f, false);
            }
        }
    }

    @Override
    public void close() {
        if (this.entityOutlineShader != null) {
            this.entityOutlineShader.close();
        }
    }

    @Override
    public void apply(ResourceManager resourceManager) {
        this.textureManager.bindTexture(FORCEFIELD);
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
        } catch (IOException iOException) {
            LOGGER.warn("Failed to load shader: {}", (Object)identifier, (Object)iOException);
            this.entityOutlineShader = null;
            this.entityOutlinesFramebuffer = null;
        } catch (JsonSyntaxException jsonSyntaxException) {
            LOGGER.warn("Failed to load shader: {}", (Object)identifier, (Object)jsonSyntaxException);
            this.entityOutlineShader = null;
            this.entityOutlinesFramebuffer = null;
        }
    }

    public void drawEntityOutlinesFramebuffer() {
        if (this.canDrawEntityOutlines()) {
            RenderSystem.enableBlend();
            RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE);
            this.entityOutlinesFramebuffer.draw(this.client.getWindow().getFramebufferWidth(), this.client.getWindow().getFramebufferHeight(), false);
            RenderSystem.disableBlend();
        }
    }

    protected boolean canDrawEntityOutlines() {
        return this.entityOutlinesFramebuffer != null && this.entityOutlineShader != null && this.client.player != null;
    }

    private void method_3265() {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        if (this.field_4102 != null) {
            this.field_4102.close();
        }
        this.field_4102 = new VertexBuffer(this.field_4100);
        this.method_3283(bufferBuilder, -16.0f, true);
        bufferBuilder.end();
        this.field_4102.upload(bufferBuilder);
    }

    private void method_3277() {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        if (this.field_4087 != null) {
            this.field_4087.close();
        }
        this.field_4087 = new VertexBuffer(this.field_4100);
        this.method_3283(bufferBuilder, 16.0f, false);
        bufferBuilder.end();
        this.field_4087.upload(bufferBuilder);
    }

    private void method_3283(BufferBuilder bufferBuilder, float f, boolean bl) {
        int i = 64;
        int j = 6;
        bufferBuilder.begin(7, VertexFormats.POSITION);
        for (int k = -384; k <= 384; k += 64) {
            for (int l = -384; l <= 384; l += 64) {
                float g = k;
                float h = k + 64;
                if (bl) {
                    h = k;
                    g = k + 64;
                }
                bufferBuilder.vertex(g, f, l).next();
                bufferBuilder.vertex(h, f, l).next();
                bufferBuilder.vertex(h, f, l + 64).next();
                bufferBuilder.vertex(g, f, l + 64).next();
            }
        }
    }

    private void renderStars() {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        if (this.starsBuffer != null) {
            this.starsBuffer.close();
        }
        this.starsBuffer = new VertexBuffer(this.field_4100);
        this.renderStars(bufferBuilder);
        bufferBuilder.end();
        this.starsBuffer.upload(bufferBuilder);
    }

    private void renderStars(BufferBuilder bufferBuilder) {
        Random random = new Random(10842L);
        bufferBuilder.begin(7, VertexFormats.POSITION);
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
                bufferBuilder.vertex(j + af, k + ag, l + ah).next();
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
            this.chunksToRebuild.clear();
            this.chunkInfos.clear();
            if (this.renderedChunks != null) {
                this.renderedChunks.clear();
                this.renderedChunks = null;
            }
            if (this.chunkBuilder != null) {
                this.chunkBuilder.stop();
            }
            this.chunkBuilder = null;
            this.blockEntities.clear();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void reload() {
        Entity entity;
        if (this.world == null) {
            return;
        }
        this.world.reloadColor();
        if (this.chunkBuilder == null) {
            this.chunkBuilder = new ChunkBuilder(this.world, this, Util.getServerWorkerExecutor(), this.client.is64Bit(), this.bufferBuilders.getBlockBufferBuilders());
        } else {
            this.chunkBuilder.setWorld(this.world);
        }
        this.needsTerrainUpdate = true;
        this.cloudsDirty = true;
        RenderLayers.setFancyGraphics(this.client.options.fancyGraphics);
        this.renderDistance = this.client.options.viewDistance;
        if (this.renderedChunks != null) {
            this.renderedChunks.clear();
        }
        this.clearChunkRenderers();
        Set<BlockEntity> set = this.blockEntities;
        synchronized (set) {
            this.blockEntities.clear();
        }
        this.renderedChunks = new BuiltChunkStorage(this.chunkBuilder, this.world, this.client.options.viewDistance, this);
        if (this.world != null && (entity = this.client.getCameraEntity()) != null) {
            this.renderedChunks.updateCameraPosition(entity.getX(), entity.getZ());
        }
    }

    protected void clearChunkRenderers() {
        this.chunksToRebuild.clear();
        this.chunkBuilder.reset();
    }

    public void onResized(int i, int j) {
        this.scheduleTerrainUpdate();
        if (this.entityOutlineShader != null) {
            this.entityOutlineShader.setupDimensions(i, j);
        }
    }

    public String getChunksDebugString() {
        int i = this.renderedChunks.chunks.length;
        int j = this.getCompletedChunkCount();
        return String.format("C: %d/%d %sD: %d, %s", j, i, this.client.field_1730 ? "(s) " : "", this.renderDistance, this.chunkBuilder == null ? "null" : this.chunkBuilder.getDebugString());
    }

    protected int getCompletedChunkCount() {
        int i = 0;
        for (BuiltChunkInfo builtChunkInfo : this.chunkInfos) {
            if (builtChunkInfo.chunk.getData().isEmpty()) continue;
            ++i;
        }
        return i;
    }

    public String getEntitiesDebugString() {
        return "E: " + this.regularEntityCount + "/" + this.world.getRegularEntityCount() + ", B: " + this.blockEntityCount;
    }

    private void setUpTerrain(Camera camera, Frustum frustum, boolean bl, int i, boolean bl2) {
        Vec3d vec3d = camera.getPos();
        if (this.client.options.viewDistance != this.renderDistance) {
            this.reload();
        }
        this.world.getProfiler().push("camera");
        double d = this.client.player.getX() - this.lastCameraChunkUpdateX;
        double e = this.client.player.getY() - this.lastCameraChunkUpdateY;
        double f = this.client.player.getZ() - this.lastCameraChunkUpdateZ;
        if (this.cameraChunkX != this.client.player.chunkX || this.cameraChunkY != this.client.player.chunkY || this.cameraChunkZ != this.client.player.chunkZ || d * d + e * e + f * f > 16.0) {
            this.lastCameraChunkUpdateX = this.client.player.getX();
            this.lastCameraChunkUpdateY = this.client.player.getY();
            this.lastCameraChunkUpdateZ = this.client.player.getZ();
            this.cameraChunkX = this.client.player.chunkX;
            this.cameraChunkY = this.client.player.chunkY;
            this.cameraChunkZ = this.client.player.chunkZ;
            this.renderedChunks.updateCameraPosition(this.client.player.getX(), this.client.player.getZ());
        }
        this.chunkBuilder.setCameraPosition(vec3d);
        this.world.getProfiler().swap("cull");
        this.client.getProfiler().swap("culling");
        BlockPos blockPos = camera.getBlockPos();
        ChunkBuilder.BuiltChunk builtChunk = this.renderedChunks.getRenderedChunk(blockPos);
        BlockPos blockPos2 = new BlockPos(MathHelper.floor(vec3d.x / 16.0) * 16, MathHelper.floor(vec3d.y / 16.0) * 16, MathHelper.floor(vec3d.z / 16.0) * 16);
        float g = camera.getPitch();
        float h = camera.getYaw();
        this.needsTerrainUpdate = this.needsTerrainUpdate || !this.chunksToRebuild.isEmpty() || vec3d.x != this.lastCameraX || vec3d.y != this.lastCameraY || vec3d.z != this.lastCameraZ || (double)g != this.lastCameraPitch || (double)h != this.lastCameraYaw;
        this.lastCameraX = vec3d.x;
        this.lastCameraY = vec3d.y;
        this.lastCameraZ = vec3d.z;
        this.lastCameraPitch = g;
        this.lastCameraYaw = h;
        this.client.getProfiler().swap("update");
        if (!bl && this.needsTerrainUpdate) {
            this.needsTerrainUpdate = false;
            this.chunkInfos = Lists.newArrayList();
            ArrayDeque<BuiltChunkInfo> queue = Queues.newArrayDeque();
            Entity.setRenderDistanceMultiplier(MathHelper.clamp((double)this.client.options.viewDistance / 8.0, 1.0, 2.5));
            boolean bl3 = this.client.field_1730;
            if (builtChunk == null) {
                int j = blockPos.getY() > 0 ? 248 : 8;
                for (int k = -this.renderDistance; k <= this.renderDistance; ++k) {
                    for (int l = -this.renderDistance; l <= this.renderDistance; ++l) {
                        ChunkBuilder.BuiltChunk builtChunk2 = this.renderedChunks.getRenderedChunk(new BlockPos((k << 4) + 8, j, (l << 4) + 8));
                        if (builtChunk2 == null || !frustum.isVisible(builtChunk2.boundingBox)) continue;
                        builtChunk2.setRebuildFrame(i);
                        queue.add(new BuiltChunkInfo(builtChunk2, null, 0));
                    }
                }
            } else {
                boolean bl4 = false;
                BuiltChunkInfo builtChunkInfo = new BuiltChunkInfo(builtChunk, null, 0);
                Set<Direction> set = this.getOpenChunkFaces(blockPos);
                if (set.size() == 1) {
                    Direction[] vector3f = camera.getHorizontalPlane();
                    Direction direction = Direction.getFacing(vector3f.getX(), vector3f.getY(), vector3f.getZ()).getOpposite();
                    set.remove(direction);
                }
                if (set.isEmpty()) {
                    bl4 = true;
                }
                if (!bl4 || bl2) {
                    if (bl2 && this.world.getBlockState(blockPos).isFullOpaque(this.world, blockPos)) {
                        bl3 = false;
                    }
                    builtChunk.setRebuildFrame(i);
                    queue.add(builtChunkInfo);
                } else {
                    this.chunkInfos.add(builtChunkInfo);
                }
            }
            this.client.getProfiler().push("iteration");
            while (!queue.isEmpty()) {
                BuiltChunkInfo builtChunkInfo2 = (BuiltChunkInfo)queue.poll();
                ChunkBuilder.BuiltChunk builtChunk3 = builtChunkInfo2.chunk;
                Direction direction2 = builtChunkInfo2.field_4125;
                this.chunkInfos.add(builtChunkInfo2);
                for (Direction direction3 : DIRECTIONS) {
                    ChunkBuilder.BuiltChunk builtChunk4 = this.getAdjacentChunkRenderer(blockPos2, builtChunk3, direction3);
                    if (bl3 && builtChunkInfo2.method_3298(direction3.getOpposite()) || bl3 && direction2 != null && !builtChunk3.getData().isVisibleThrough(direction2.getOpposite(), direction3) || builtChunk4 == null || !builtChunk4.shouldBuild() || !builtChunk4.setRebuildFrame(i) || !frustum.isVisible(builtChunk4.boundingBox)) continue;
                    BuiltChunkInfo builtChunkInfo3 = new BuiltChunkInfo(builtChunk4, direction3, builtChunkInfo2.field_4122 + 1);
                    builtChunkInfo3.method_3299(builtChunkInfo2.field_4126, direction3);
                    queue.add(builtChunkInfo3);
                }
            }
            this.client.getProfiler().pop();
        }
        this.client.getProfiler().swap("rebuildNear");
        Set<ChunkBuilder.BuiltChunk> set2 = this.chunksToRebuild;
        this.chunksToRebuild = Sets.newLinkedHashSet();
        for (BuiltChunkInfo builtChunkInfo2 : this.chunkInfos) {
            boolean bl5;
            ChunkBuilder.BuiltChunk builtChunk3 = builtChunkInfo2.chunk;
            if (!builtChunk3.needsRebuild() && !set2.contains(builtChunk3)) continue;
            this.needsTerrainUpdate = true;
            BlockPos blockPos3 = builtChunk3.getOrigin().add(8, 8, 8);
            boolean bl3 = bl5 = blockPos3.getSquaredDistance(blockPos) < 768.0;
            if (builtChunk3.needsImportantRebuild() || bl5) {
                this.client.getProfiler().push("build near");
                this.chunkBuilder.rebuild(builtChunk3);
                builtChunk3.cancelRebuild();
                this.client.getProfiler().pop();
                continue;
            }
            this.chunksToRebuild.add(builtChunk3);
        }
        this.chunksToRebuild.addAll(set2);
        this.client.getProfiler().pop();
    }

    private Set<Direction> getOpenChunkFaces(BlockPos blockPos) {
        ChunkOcclusionGraphBuilder chunkOcclusionGraphBuilder = new ChunkOcclusionGraphBuilder();
        BlockPos blockPos2 = new BlockPos(blockPos.getX() >> 4 << 4, blockPos.getY() >> 4 << 4, blockPos.getZ() >> 4 << 4);
        WorldChunk worldChunk = this.world.getWorldChunk(blockPos2);
        for (BlockPos blockPos3 : BlockPos.iterate(blockPos2, blockPos2.add(15, 15, 15))) {
            if (!worldChunk.getBlockState(blockPos3).isFullOpaque(this.world, blockPos3)) continue;
            chunkOcclusionGraphBuilder.markClosed(blockPos3);
        }
        return chunkOcclusionGraphBuilder.getOpenFaces(blockPos);
    }

    @Nullable
    private ChunkBuilder.BuiltChunk getAdjacentChunkRenderer(BlockPos blockPos, ChunkBuilder.BuiltChunk builtChunk, Direction direction) {
        BlockPos blockPos2 = builtChunk.getNeighborPosition(direction);
        if (MathHelper.abs(blockPos.getX() - blockPos2.getX()) > this.renderDistance * 16) {
            return null;
        }
        if (blockPos2.getY() < 0 || blockPos2.getY() >= 256) {
            return null;
        }
        if (MathHelper.abs(blockPos.getZ() - blockPos2.getZ()) > this.renderDistance * 16) {
            return null;
        }
        return this.renderedChunks.getRenderedChunk(blockPos2);
    }

    private void captureFrustum(Matrix4f matrix4f, Matrix4f matrix4f2, double d, double e, double f, Frustum frustum) {
        this.capturedFrustum = frustum;
        Matrix4f matrix4f3 = matrix4f2.copy();
        matrix4f3.multiply(matrix4f);
        matrix4f3.invert();
        this.capturedFrustumPosition.x = d;
        this.capturedFrustumPosition.y = e;
        this.capturedFrustumPosition.z = f;
        this.field_4065[0] = new Vector4f(-1.0f, -1.0f, -1.0f, 1.0f);
        this.field_4065[1] = new Vector4f(1.0f, -1.0f, -1.0f, 1.0f);
        this.field_4065[2] = new Vector4f(1.0f, 1.0f, -1.0f, 1.0f);
        this.field_4065[3] = new Vector4f(-1.0f, 1.0f, -1.0f, 1.0f);
        this.field_4065[4] = new Vector4f(-1.0f, -1.0f, 1.0f, 1.0f);
        this.field_4065[5] = new Vector4f(1.0f, -1.0f, 1.0f, 1.0f);
        this.field_4065[6] = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.field_4065[7] = new Vector4f(-1.0f, 1.0f, 1.0f, 1.0f);
        for (int i = 0; i < 8; ++i) {
            this.field_4065[i].multiply(matrix4f3);
            this.field_4065[i].normalizeProjectiveCoordinates();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void render(MatrixStack matrixStack, float f, long l, boolean bl, Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f matrix4f) {
        int m;
        boolean bl3;
        Frustum frustum;
        boolean bl2;
        BlockEntityRenderDispatcher.INSTANCE.configure(this.world, this.client.getTextureManager(), this.client.textRenderer, camera, this.client.crosshairTarget);
        this.entityRenderDispatcher.configure(this.world, camera, this.client.targetedEntity);
        Profiler profiler = this.world.getProfiler();
        profiler.swap("light_updates");
        this.client.world.method_2935().getLightingProvider().doLightUpdates(Integer.MAX_VALUE, true, true);
        Vec3d vec3d = camera.getPos();
        double d = vec3d.getX();
        double e = vec3d.getY();
        double g = vec3d.getZ();
        Matrix4f matrix4f2 = matrixStack.peek().getModel();
        profiler.swap("culling");
        boolean bl4 = bl2 = this.capturedFrustum != null;
        if (bl2) {
            frustum = this.capturedFrustum;
            frustum.setPosition(this.capturedFrustumPosition.x, this.capturedFrustumPosition.y, this.capturedFrustumPosition.z);
        } else {
            frustum = new Frustum(matrix4f2, matrix4f);
            frustum.setPosition(d, e, g);
        }
        this.client.getProfiler().swap("captureFrustum");
        if (this.shouldCaptureFrustum) {
            this.captureFrustum(matrix4f2, matrix4f, vec3d.x, vec3d.y, vec3d.z, bl2 ? new Frustum(matrix4f2, matrix4f) : frustum);
            this.shouldCaptureFrustum = false;
        }
        profiler.swap("clear");
        BackgroundRenderer.render(camera, f, this.client.world, this.client.options.viewDistance, gameRenderer.getSkyDarkness(f));
        RenderSystem.clear(16640, MinecraftClient.IS_SYSTEM_MAC);
        float h = gameRenderer.getViewDistance();
        boolean bl5 = bl3 = this.client.world.dimension.isFogThick(MathHelper.floor(d), MathHelper.floor(e)) || this.client.inGameHud.getBossBarHud().shouldThickenFog();
        if (this.client.options.viewDistance >= 4) {
            BackgroundRenderer.applyFog(camera, BackgroundRenderer.FogType.FOG_SKY, h, bl3);
            profiler.swap("sky");
            this.renderSky(matrixStack, f);
        }
        profiler.swap("fog");
        BackgroundRenderer.applyFog(camera, BackgroundRenderer.FogType.FOG_TERRAIN, h, bl3);
        profiler.swap("terrain_setup");
        this.setUpTerrain(camera, frustum, bl2, this.frame++, this.client.player.isSpectator());
        profiler.swap("updatechunks");
        this.updateChunks(l);
        profiler.swap("terrain");
        this.renderLayer(RenderLayer.getSolid(), matrixStack, d, e, g);
        this.renderLayer(RenderLayer.getCutoutMipped(), matrixStack, d, e, g);
        this.renderLayer(RenderLayer.getCutout(), matrixStack, d, e, g);
        GuiLighting.enable(matrixStack.peek().getModel());
        profiler.swap("entities");
        profiler.push("prepare");
        this.regularEntityCount = 0;
        this.blockEntityCount = 0;
        profiler.swap("entities");
        if (this.canDrawEntityOutlines()) {
            profiler.swap("entityOutlines");
            this.entityOutlinesFramebuffer.clear(MinecraftClient.IS_SYSTEM_MAC);
            this.client.getFramebuffer().beginWrite(false);
        }
        boolean bl42 = false;
        VertexConsumerProvider.Immediate immediate = this.bufferBuilders.getEntityVertexConsumers();
        for (Entity entity : this.world.getEntities()) {
            Object vertexConsumerProvider;
            boolean bl52;
            if (!this.entityRenderDispatcher.shouldRender(entity, frustum, d, e, g) && !entity.hasPassengerDeep(this.client.player) || entity == camera.getFocusedEntity() && !camera.isThirdPerson() && (!(camera.getFocusedEntity() instanceof LivingEntity) || !((LivingEntity)camera.getFocusedEntity()).isSleeping()) || entity instanceof ClientPlayerEntity && camera.getFocusedEntity() != entity) continue;
            ++this.regularEntityCount;
            if (entity.age == 0) {
                entity.prevRenderX = entity.getX();
                entity.prevRenderY = entity.getY();
                entity.prevRenderZ = entity.getZ();
            }
            boolean bl6 = bl52 = this.canDrawEntityOutlines() && (entity.isGlowing() || entity instanceof PlayerEntity && this.client.player.isSpectator() && this.client.options.keySpectatorOutlines.isPressed());
            if (bl52) {
                bl42 = true;
                OutlineVertexConsumerProvider outlineVertexConsumerProvider = this.bufferBuilders.getOutlineVertexConsumers();
                vertexConsumerProvider = outlineVertexConsumerProvider;
                int i = entity.getTeamColorValue();
                int j = 255;
                int k = i >> 16 & 0xFF;
                m = i >> 8 & 0xFF;
                int n = i & 0xFF;
                outlineVertexConsumerProvider.setColor(k, m, n, 255);
            } else {
                vertexConsumerProvider = immediate;
            }
            this.renderEntity(entity, d, e, g, f, matrixStack, (VertexConsumerProvider)vertexConsumerProvider);
        }
        this.checkEmpty(matrixStack);
        profiler.swap("blockentities");
        for (BuiltChunkInfo builtChunkInfo : this.chunkInfos) {
            List<BlockEntity> list = builtChunkInfo.chunk.getData().getBlockEntities();
            if (list.isEmpty()) continue;
            for (BlockEntity blockEntity : list) {
                BlockPos blockPos = blockEntity.getPos();
                VertexConsumerProvider vertexConsumerProvider2 = immediate;
                matrixStack.push();
                matrixStack.translate((double)blockPos.getX() - d, (double)blockPos.getY() - e, (double)blockPos.getZ() - g);
                SortedSet sortedSet = (SortedSet)this.blockBreakingProgressions.get(blockPos.asLong());
                if (sortedSet != null && !sortedSet.isEmpty() && (m = ((BlockBreakingInfo)sortedSet.last()).getStage()) >= 0) {
                    TransformingVertexConsumer vertexConsumer = new TransformingVertexConsumer(this.bufferBuilders.getEffectVertexConsumers().getBuffer(RenderLayer.getBlockBreaking(m)), matrixStack.peek());
                    vertexConsumerProvider2 = renderLayer -> {
                        VertexConsumer vertexConsumer2 = immediate.getBuffer(renderLayer);
                        if (renderLayer.method_23037()) {
                            return new DelegatingVertexConsumer(ImmutableList.of(vertexConsumer, vertexConsumer2));
                        }
                        return vertexConsumer2;
                    };
                }
                BlockEntityRenderDispatcher.INSTANCE.render(blockEntity, f, matrixStack, vertexConsumerProvider2);
                matrixStack.pop();
            }
        }
        Set<BlockEntity> set = this.blockEntities;
        synchronized (set) {
            for (BlockEntity blockEntity2 : this.blockEntities) {
                BlockPos blockPos2 = blockEntity2.getPos();
                matrixStack.push();
                matrixStack.translate((double)blockPos2.getX() - d, (double)blockPos2.getY() - e, (double)blockPos2.getZ() - g);
                BlockEntityRenderDispatcher.INSTANCE.render(blockEntity2, f, matrixStack, immediate);
                matrixStack.pop();
            }
        }
        this.checkEmpty(matrixStack);
        immediate.draw(RenderLayer.getSolid());
        immediate.draw(RenderLayer.method_23946());
        immediate.draw(RenderLayer.method_23947());
        this.bufferBuilders.getOutlineVertexConsumers().draw();
        if (bl42) {
            this.entityOutlineShader.render(f);
            this.client.getFramebuffer().beginWrite(false);
        }
        profiler.swap("destroyProgress");
        for (Long2ObjectMap.Entry entry : this.blockBreakingProgressions.long2ObjectEntrySet()) {
            SortedSet sortedSet2;
            double q;
            double p;
            BlockPos blockPos3 = BlockPos.fromLong(entry.getLongKey());
            double o = (double)blockPos3.getX() - d;
            if (o * o + (p = (double)blockPos3.getY() - e) * p + (q = (double)blockPos3.getZ() - g) * q > 1024.0 || (sortedSet2 = (SortedSet)entry.getValue()) == null || sortedSet2.isEmpty()) continue;
            int r = ((BlockBreakingInfo)sortedSet2.last()).getStage();
            matrixStack.push();
            matrixStack.translate((double)blockPos3.getX() - d, (double)blockPos3.getY() - e, (double)blockPos3.getZ() - g);
            TransformingVertexConsumer vertexConsumer2 = new TransformingVertexConsumer(this.bufferBuilders.getEffectVertexConsumers().getBuffer(RenderLayer.getBlockBreaking(r)), matrixStack.peek());
            this.client.getBlockRenderManager().renderDamage(this.world.getBlockState(blockPos3), blockPos3, this.world, matrixStack, vertexConsumer2);
            matrixStack.pop();
        }
        this.checkEmpty(matrixStack);
        profiler.pop();
        HitResult hitResult = this.client.crosshairTarget;
        if (bl && hitResult != null && hitResult.getType() == HitResult.Type.BLOCK) {
            profiler.swap("outline");
            BlockPos blockPos = ((BlockHitResult)hitResult).getBlockPos();
            BlockState blockState = this.world.getBlockState(blockPos);
            if (!blockState.isAir() && this.world.getWorldBorder().contains(blockPos)) {
                VertexConsumer vertexConsumer3 = immediate.getBuffer(RenderLayer.getLines());
                this.drawBlockOutline(matrixStack, vertexConsumer3, camera.getFocusedEntity(), d, e, g, blockPos, blockState);
            }
        }
        RenderSystem.pushMatrix();
        RenderSystem.multMatrix(matrixStack.peek().getModel());
        this.client.debugRenderer.render(matrixStack, immediate, d, e, g, l);
        this.renderWorldBorder(camera);
        RenderSystem.popMatrix();
        immediate.draw(RenderLayer.getWaterMask());
        profiler.swap("translucent");
        this.renderLayer(RenderLayer.getTranslucent(), matrixStack, d, e, g);
        immediate.draw(RenderLayer.method_23949());
        immediate.draw(RenderLayer.method_23951());
        immediate.draw();
        this.bufferBuilders.getEffectVertexConsumers().draw();
        profiler.swap("particles");
        this.client.particleManager.renderParticles(matrixStack, immediate, lightmapTextureManager, camera, f);
        RenderSystem.pushMatrix();
        RenderSystem.multMatrix(matrixStack.peek().getModel());
        profiler.swap("cloudsLayers");
        if (this.client.options.getCloudRenderMode() != CloudRenderMode.OFF) {
            profiler.swap("clouds");
            this.renderClouds(matrixStack, f, d, e, g);
        }
        RenderSystem.depthMask(false);
        profiler.swap("weather");
        this.renderWeather(lightmapTextureManager, f, d, e, g);
        RenderSystem.depthMask(true);
        this.method_22989(camera);
        RenderSystem.shadeModel(7424);
        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();
        RenderSystem.popMatrix();
        BackgroundRenderer.method_23792();
    }

    private void checkEmpty(MatrixStack matrixStack) {
        if (!matrixStack.isEmpty()) {
            throw new IllegalStateException("Pose stack not empty");
        }
    }

    private void renderEntity(Entity entity, double d, double e, double f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider) {
        double h = MathHelper.lerp((double)g, entity.prevRenderX, entity.getX());
        double i = MathHelper.lerp((double)g, entity.prevRenderY, entity.getY());
        double j = MathHelper.lerp((double)g, entity.prevRenderZ, entity.getZ());
        float k = MathHelper.lerp(g, entity.prevYaw, entity.yaw);
        this.entityRenderDispatcher.render(entity, h - d, i - e, j - f, k, g, matrixStack, vertexConsumerProvider, EntityRenderDispatcher.method_23839(entity));
    }

    private void renderLayer(RenderLayer renderLayer, MatrixStack matrixStack, double d, double e, double f) {
        renderLayer.startDrawing();
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
                for (BuiltChunkInfo builtChunkInfo : this.chunkInfos) {
                    if (j >= 15 || !builtChunkInfo.chunk.scheduleSort(renderLayer, this.chunkBuilder)) continue;
                    ++j;
                }
            }
            this.client.getProfiler().pop();
        }
        this.client.getProfiler().push("filterempty");
        ArrayList<ChunkBuilder.BuiltChunk> list = Lists.newArrayList();
        for (BuiltChunkInfo builtChunkInfo2 : renderLayer == RenderLayer.getTranslucent() ? Lists.reverse(this.chunkInfos) : this.chunkInfos) {
            ChunkBuilder.BuiltChunk builtChunk = builtChunkInfo2.chunk;
            if (builtChunk.getData().isEmpty(renderLayer)) continue;
            list.add(builtChunk);
        }
        this.client.getProfiler().swap(() -> "render_" + renderLayer);
        for (ChunkBuilder.BuiltChunk builtChunk2 : list) {
            VertexBuffer vertexBuffer = builtChunk2.getBuffer(renderLayer);
            matrixStack.push();
            BlockPos blockPos = builtChunk2.getOrigin();
            matrixStack.translate((double)blockPos.getX() - d, (double)blockPos.getY() - e, (double)blockPos.getZ() - f);
            vertexBuffer.bind();
            this.field_20791.startDrawing(0L);
            vertexBuffer.draw(matrixStack.peek().getModel(), 7);
            matrixStack.pop();
        }
        VertexBuffer.unbind();
        RenderSystem.clearCurrentColor();
        this.field_20791.endDrawing();
        this.client.getProfiler().pop();
        renderLayer.endDrawing();
    }

    private void method_22989(Camera camera) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        if (this.client.field_20907 || this.client.field_20908) {
            double d = camera.getPos().getX();
            double e = camera.getPos().getY();
            double f = camera.getPos().getZ();
            RenderSystem.depthMask(true);
            RenderSystem.disableCull();
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.disableTexture();
            for (BuiltChunkInfo builtChunkInfo : this.chunkInfos) {
                int i;
                ChunkBuilder.BuiltChunk builtChunk = builtChunkInfo.chunk;
                RenderSystem.pushMatrix();
                BlockPos blockPos = builtChunk.getOrigin();
                RenderSystem.translated((double)blockPos.getX() - d, (double)blockPos.getY() - e, (double)blockPos.getZ() - f);
                if (this.client.field_20907) {
                    bufferBuilder.begin(1, VertexFormats.POSITION_COLOR);
                    RenderSystem.lineWidth(10.0f);
                    i = builtChunkInfo.field_4122 == 0 ? 0 : MathHelper.hsvToRgb((float)builtChunkInfo.field_4122 / 50.0f, 0.9f, 0.9f);
                    int j = i >> 16 & 0xFF;
                    int k = i >> 8 & 0xFF;
                    int l = i & 0xFF;
                    Direction direction = builtChunkInfo.field_4125;
                    if (direction != null) {
                        bufferBuilder.vertex(8.0, 8.0, 8.0).color(j, k, l, 255).next();
                        bufferBuilder.vertex(8 - 16 * direction.getOffsetX(), 8 - 16 * direction.getOffsetY(), 8 - 16 * direction.getOffsetZ()).color(j, k, l, 255).next();
                    }
                    tessellator.draw();
                    RenderSystem.lineWidth(1.0f);
                }
                if (this.client.field_20908 && !builtChunk.getData().isEmpty()) {
                    bufferBuilder.begin(1, VertexFormats.POSITION_COLOR);
                    RenderSystem.lineWidth(10.0f);
                    i = 0;
                    for (Direction direction : Direction.values()) {
                        for (Direction direction2 : Direction.values()) {
                            boolean bl = builtChunk.getData().isVisibleThrough(direction, direction2);
                            if (bl) continue;
                            ++i;
                            bufferBuilder.vertex(8 + 8 * direction.getOffsetX(), 8 + 8 * direction.getOffsetY(), 8 + 8 * direction.getOffsetZ()).color(1, 0, 0, 1).next();
                            bufferBuilder.vertex(8 + 8 * direction2.getOffsetX(), 8 + 8 * direction2.getOffsetY(), 8 + 8 * direction2.getOffsetZ()).color(1, 0, 0, 1).next();
                        }
                    }
                    tessellator.draw();
                    RenderSystem.lineWidth(1.0f);
                    if (i > 0) {
                        bufferBuilder.begin(7, VertexFormats.POSITION_COLOR);
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
                RenderSystem.popMatrix();
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
            RenderSystem.lineWidth(10.0f);
            RenderSystem.pushMatrix();
            RenderSystem.translatef((float)(this.capturedFrustumPosition.x - camera.getPos().x), (float)(this.capturedFrustumPosition.y - camera.getPos().y), (float)(this.capturedFrustumPosition.z - camera.getPos().z));
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
            RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
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
            RenderSystem.lineWidth(1.0f);
        }
    }

    private void method_22984(VertexConsumer vertexConsumer, int i) {
        vertexConsumer.vertex(this.field_4065[i].getX(), this.field_4065[i].getY(), this.field_4065[i].getZ()).next();
    }

    private void method_22985(VertexConsumer vertexConsumer, int i, int j, int k, int l, int m, int n, int o) {
        float f = 0.25f;
        vertexConsumer.vertex(this.field_4065[i].getX(), this.field_4065[i].getY(), this.field_4065[i].getZ()).color((float)m, (float)n, (float)o, 0.25f).next();
        vertexConsumer.vertex(this.field_4065[j].getX(), this.field_4065[j].getY(), this.field_4065[j].getZ()).color((float)m, (float)n, (float)o, 0.25f).next();
        vertexConsumer.vertex(this.field_4065[k].getX(), this.field_4065[k].getY(), this.field_4065[k].getZ()).color((float)m, (float)n, (float)o, 0.25f).next();
        vertexConsumer.vertex(this.field_4065[l].getX(), this.field_4065[l].getY(), this.field_4065[l].getZ()).color((float)m, (float)n, (float)o, 0.25f).next();
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

    private void removeBlockBreakingInfo(BlockBreakingInfo blockBreakingInfo) {
        long l = blockBreakingInfo.getPos().asLong();
        Set set = (Set)this.blockBreakingProgressions.get(l);
        set.remove(blockBreakingInfo);
        if (set.isEmpty()) {
            this.blockBreakingProgressions.remove(l);
        }
    }

    private void renderEndSky(MatrixStack matrixStack) {
        RenderSystem.disableAlphaTest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.depthMask(false);
        this.textureManager.bindTexture(END_SKY);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        for (int i = 0; i < 6; ++i) {
            matrixStack.push();
            if (i == 1) {
                matrixStack.multiply(Vector3f.POSITIVE_X.getRotationQuaternion(90.0f));
            }
            if (i == 2) {
                matrixStack.multiply(Vector3f.POSITIVE_X.getRotationQuaternion(-90.0f));
            }
            if (i == 3) {
                matrixStack.multiply(Vector3f.POSITIVE_X.getRotationQuaternion(180.0f));
            }
            if (i == 4) {
                matrixStack.multiply(Vector3f.POSITIVE_Z.getRotationQuaternion(90.0f));
            }
            if (i == 5) {
                matrixStack.multiply(Vector3f.POSITIVE_Z.getRotationQuaternion(-90.0f));
            }
            Matrix4f matrix4f = matrixStack.peek().getModel();
            bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
            bufferBuilder.vertex(matrix4f, -100.0f, -100.0f, -100.0f).texture(0.0f, 0.0f).color(40, 40, 40, 255).next();
            bufferBuilder.vertex(matrix4f, -100.0f, -100.0f, 100.0f).texture(0.0f, 16.0f).color(40, 40, 40, 255).next();
            bufferBuilder.vertex(matrix4f, 100.0f, -100.0f, 100.0f).texture(16.0f, 16.0f).color(40, 40, 40, 255).next();
            bufferBuilder.vertex(matrix4f, 100.0f, -100.0f, -100.0f).texture(16.0f, 0.0f).color(40, 40, 40, 255).next();
            tessellator.draw();
            matrixStack.pop();
        }
        RenderSystem.depthMask(true);
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
        RenderSystem.enableAlphaTest();
    }

    public void renderSky(MatrixStack matrixStack, float f) {
        float r;
        float q;
        float p;
        int n;
        float l;
        float j;
        if (this.client.world.dimension.getType() == DimensionType.THE_END) {
            this.renderEndSky(matrixStack);
            return;
        }
        if (!this.client.world.dimension.hasVisibleSky()) {
            return;
        }
        RenderSystem.disableTexture();
        Vec3d vec3d = this.world.method_23777(this.client.gameRenderer.getCamera().getBlockPos(), f);
        float g = (float)vec3d.x;
        float h = (float)vec3d.y;
        float i = (float)vec3d.z;
        BackgroundRenderer.setFogBlack();
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        RenderSystem.depthMask(false);
        RenderSystem.enableFog();
        RenderSystem.color3f(g, h, i);
        this.field_4087.bind();
        this.field_4100.startDrawing(0L);
        this.field_4087.draw(matrixStack.peek().getModel(), 7);
        VertexBuffer.unbind();
        this.field_4100.endDrawing();
        RenderSystem.disableFog();
        RenderSystem.disableAlphaTest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        float[] fs = this.world.dimension.getBackgroundColor(this.world.getSkyAngle(f), f);
        if (fs != null) {
            RenderSystem.disableTexture();
            RenderSystem.shadeModel(7425);
            matrixStack.push();
            matrixStack.multiply(Vector3f.POSITIVE_X.getRotationQuaternion(90.0f));
            j = MathHelper.sin(this.world.getSkyAngleRadians(f)) < 0.0f ? 180.0f : 0.0f;
            matrixStack.multiply(Vector3f.POSITIVE_Z.getRotationQuaternion(j));
            matrixStack.multiply(Vector3f.POSITIVE_Z.getRotationQuaternion(90.0f));
            float k = fs[0];
            l = fs[1];
            float m = fs[2];
            Matrix4f matrix4f = matrixStack.peek().getModel();
            bufferBuilder.begin(6, VertexFormats.POSITION_COLOR);
            bufferBuilder.vertex(matrix4f, 0.0f, 100.0f, 0.0f).color(k, l, m, fs[3]).next();
            n = 16;
            for (int o = 0; o <= 16; ++o) {
                p = (float)o * ((float)Math.PI * 2) / 16.0f;
                q = MathHelper.sin(p);
                r = MathHelper.cos(p);
                bufferBuilder.vertex(matrix4f, q * 120.0f, r * 120.0f, -r * 40.0f * fs[3]).color(fs[0], fs[1], fs[2], 0.0f).next();
            }
            bufferBuilder.end();
            BufferRenderer.draw(bufferBuilder);
            matrixStack.pop();
            RenderSystem.shadeModel(7424);
        }
        RenderSystem.enableTexture();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        matrixStack.push();
        j = 1.0f - this.world.getRainGradient(f);
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, j);
        matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion(-90.0f));
        matrixStack.multiply(Vector3f.POSITIVE_X.getRotationQuaternion(this.world.getSkyAngle(f) * 360.0f));
        Matrix4f matrix4f2 = matrixStack.peek().getModel();
        l = 30.0f;
        this.textureManager.bindTexture(SUN);
        bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE);
        bufferBuilder.vertex(matrix4f2, -l, 100.0f, -l).texture(0.0f, 0.0f).next();
        bufferBuilder.vertex(matrix4f2, l, 100.0f, -l).texture(1.0f, 0.0f).next();
        bufferBuilder.vertex(matrix4f2, l, 100.0f, l).texture(1.0f, 1.0f).next();
        bufferBuilder.vertex(matrix4f2, -l, 100.0f, l).texture(0.0f, 1.0f).next();
        bufferBuilder.end();
        BufferRenderer.draw(bufferBuilder);
        l = 20.0f;
        this.textureManager.bindTexture(MOON_PHASES);
        int s = this.world.getMoonPhase();
        int t = s % 4;
        n = s / 4 % 2;
        float u = (float)(t + 0) / 4.0f;
        p = (float)(n + 0) / 2.0f;
        q = (float)(t + 1) / 4.0f;
        r = (float)(n + 1) / 2.0f;
        bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE);
        bufferBuilder.vertex(matrix4f2, -l, -100.0f, l).texture(q, r).next();
        bufferBuilder.vertex(matrix4f2, l, -100.0f, l).texture(u, r).next();
        bufferBuilder.vertex(matrix4f2, l, -100.0f, -l).texture(u, p).next();
        bufferBuilder.vertex(matrix4f2, -l, -100.0f, -l).texture(q, p).next();
        bufferBuilder.end();
        BufferRenderer.draw(bufferBuilder);
        RenderSystem.disableTexture();
        float v = this.world.method_23787(f) * j;
        if (v > 0.0f) {
            RenderSystem.color4f(v, v, v, v);
            this.starsBuffer.bind();
            this.field_4100.startDrawing(0L);
            this.starsBuffer.draw(matrixStack.peek().getModel(), 7);
            VertexBuffer.unbind();
            this.field_4100.endDrawing();
        }
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.disableBlend();
        RenderSystem.enableAlphaTest();
        RenderSystem.enableFog();
        matrixStack.pop();
        RenderSystem.disableTexture();
        RenderSystem.color3f(0.0f, 0.0f, 0.0f);
        double d = this.client.player.getCameraPosVec((float)f).y - this.world.method_23788();
        if (d < 0.0) {
            matrixStack.push();
            matrixStack.translate(0.0, 12.0, 0.0);
            this.field_4102.bind();
            this.field_4100.startDrawing(0L);
            this.field_4102.draw(matrixStack.peek().getModel(), 7);
            VertexBuffer.unbind();
            this.field_4100.endDrawing();
            matrixStack.pop();
        }
        if (this.world.dimension.method_12449()) {
            RenderSystem.color3f(g * 0.2f + 0.04f, h * 0.2f + 0.04f, i * 0.6f + 0.1f);
        } else {
            RenderSystem.color3f(g, h, i);
        }
        RenderSystem.enableTexture();
        RenderSystem.depthMask(true);
        RenderSystem.disableFog();
    }

    public void renderClouds(MatrixStack matrixStack, float f, double d, double e, double g) {
        if (!this.client.world.dimension.hasVisibleSky()) {
            return;
        }
        RenderSystem.disableCull();
        RenderSystem.enableBlend();
        RenderSystem.enableAlphaTest();
        RenderSystem.enableDepthTest();
        RenderSystem.defaultAlphaFunc();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableFog();
        float h = 12.0f;
        float i = 4.0f;
        double j = 2.0E-4;
        double k = ((float)this.ticks + f) * 0.03f;
        double l = (d + k) / 12.0;
        double m = this.world.dimension.getCloudHeight() - (float)e + 0.33f;
        double n = g / 12.0 + (double)0.33f;
        l -= (double)(MathHelper.floor(l / 2048.0) * 2048);
        n -= (double)(MathHelper.floor(n / 2048.0) * 2048);
        float o = (float)(l - (double)MathHelper.floor(l));
        float p = (float)(m / 4.0 - (double)MathHelper.floor(m / 4.0)) * 4.0f;
        float q = (float)(n - (double)MathHelper.floor(n));
        Vec3d vec3d = this.world.method_23785(f);
        int r = (int)Math.floor(l);
        int s = (int)Math.floor(m / 4.0);
        int t = (int)Math.floor(n);
        if (r != this.field_4082 || s != this.field_4097 || t != this.field_4116 || this.client.options.getCloudRenderMode() != this.field_4080 || this.field_4072.squaredDistanceTo(vec3d) > 2.0E-4) {
            this.field_4082 = r;
            this.field_4097 = s;
            this.field_4116 = t;
            this.field_4072 = vec3d;
            this.field_4080 = this.client.options.getCloudRenderMode();
            this.cloudsDirty = true;
        }
        if (this.cloudsDirty) {
            this.cloudsDirty = false;
            BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
            if (this.cloudsBuffer != null) {
                this.cloudsBuffer.close();
            }
            this.cloudsBuffer = new VertexBuffer(VertexFormats.POSITION_TEXTURE_COLOR_NORMAL);
            this.renderClouds(bufferBuilder, l, m, n, vec3d);
            bufferBuilder.end();
            this.cloudsBuffer.upload(bufferBuilder);
        }
        this.textureManager.bindTexture(CLOUDS);
        matrixStack.push();
        matrixStack.scale(12.0f, 1.0f, 12.0f);
        matrixStack.translate(-o, p, -q);
        if (this.cloudsBuffer != null) {
            int u;
            this.cloudsBuffer.bind();
            VertexFormats.POSITION_TEXTURE_COLOR_NORMAL.startDrawing(0L);
            for (int v = u = this.field_4080 == CloudRenderMode.FANCY ? 0 : 1; v < 2; ++v) {
                if (v == 0) {
                    RenderSystem.colorMask(false, false, false, false);
                } else {
                    RenderSystem.colorMask(true, true, true, true);
                }
                this.cloudsBuffer.draw(matrixStack.peek().getModel(), 7);
            }
            VertexBuffer.unbind();
            VertexFormats.POSITION_TEXTURE_COLOR_NORMAL.endDrawing();
        }
        matrixStack.pop();
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.disableAlphaTest();
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
        RenderSystem.disableFog();
    }

    private void renderClouds(BufferBuilder bufferBuilder, double d, double e, double f, Vec3d vec3d) {
        float g = 4.0f;
        float h = 0.00390625f;
        int i = 8;
        int j = 4;
        float k = 9.765625E-4f;
        float l = (float)MathHelper.floor(d) * 0.00390625f;
        float m = (float)MathHelper.floor(f) * 0.00390625f;
        float n = (float)vec3d.x;
        float o = (float)vec3d.y;
        float p = (float)vec3d.z;
        float q = n * 0.9f;
        float r = o * 0.9f;
        float s = p * 0.9f;
        float t = n * 0.7f;
        float u = o * 0.7f;
        float v = p * 0.7f;
        float w = n * 0.8f;
        float x = o * 0.8f;
        float y = p * 0.8f;
        bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR_NORMAL);
        float z = (float)Math.floor(e / 4.0) * 4.0f;
        if (this.field_4080 == CloudRenderMode.FANCY) {
            for (int aa = -3; aa <= 4; ++aa) {
                for (int ab = -3; ab <= 4; ++ab) {
                    int ae;
                    float ac = aa * 8;
                    float ad = ab * 8;
                    if (z > -5.0f) {
                        bufferBuilder.vertex(ac + 0.0f, z + 0.0f, ad + 8.0f).texture((ac + 0.0f) * 0.00390625f + l, (ad + 8.0f) * 0.00390625f + m).color(t, u, v, 0.8f).normal(0.0f, -1.0f, 0.0f).next();
                        bufferBuilder.vertex(ac + 8.0f, z + 0.0f, ad + 8.0f).texture((ac + 8.0f) * 0.00390625f + l, (ad + 8.0f) * 0.00390625f + m).color(t, u, v, 0.8f).normal(0.0f, -1.0f, 0.0f).next();
                        bufferBuilder.vertex(ac + 8.0f, z + 0.0f, ad + 0.0f).texture((ac + 8.0f) * 0.00390625f + l, (ad + 0.0f) * 0.00390625f + m).color(t, u, v, 0.8f).normal(0.0f, -1.0f, 0.0f).next();
                        bufferBuilder.vertex(ac + 0.0f, z + 0.0f, ad + 0.0f).texture((ac + 0.0f) * 0.00390625f + l, (ad + 0.0f) * 0.00390625f + m).color(t, u, v, 0.8f).normal(0.0f, -1.0f, 0.0f).next();
                    }
                    if (z <= 5.0f) {
                        bufferBuilder.vertex(ac + 0.0f, z + 4.0f - 9.765625E-4f, ad + 8.0f).texture((ac + 0.0f) * 0.00390625f + l, (ad + 8.0f) * 0.00390625f + m).color(n, o, p, 0.8f).normal(0.0f, 1.0f, 0.0f).next();
                        bufferBuilder.vertex(ac + 8.0f, z + 4.0f - 9.765625E-4f, ad + 8.0f).texture((ac + 8.0f) * 0.00390625f + l, (ad + 8.0f) * 0.00390625f + m).color(n, o, p, 0.8f).normal(0.0f, 1.0f, 0.0f).next();
                        bufferBuilder.vertex(ac + 8.0f, z + 4.0f - 9.765625E-4f, ad + 0.0f).texture((ac + 8.0f) * 0.00390625f + l, (ad + 0.0f) * 0.00390625f + m).color(n, o, p, 0.8f).normal(0.0f, 1.0f, 0.0f).next();
                        bufferBuilder.vertex(ac + 0.0f, z + 4.0f - 9.765625E-4f, ad + 0.0f).texture((ac + 0.0f) * 0.00390625f + l, (ad + 0.0f) * 0.00390625f + m).color(n, o, p, 0.8f).normal(0.0f, 1.0f, 0.0f).next();
                    }
                    if (aa > -1) {
                        for (ae = 0; ae < 8; ++ae) {
                            bufferBuilder.vertex(ac + (float)ae + 0.0f, z + 0.0f, ad + 8.0f).texture((ac + (float)ae + 0.5f) * 0.00390625f + l, (ad + 8.0f) * 0.00390625f + m).color(q, r, s, 0.8f).normal(-1.0f, 0.0f, 0.0f).next();
                            bufferBuilder.vertex(ac + (float)ae + 0.0f, z + 4.0f, ad + 8.0f).texture((ac + (float)ae + 0.5f) * 0.00390625f + l, (ad + 8.0f) * 0.00390625f + m).color(q, r, s, 0.8f).normal(-1.0f, 0.0f, 0.0f).next();
                            bufferBuilder.vertex(ac + (float)ae + 0.0f, z + 4.0f, ad + 0.0f).texture((ac + (float)ae + 0.5f) * 0.00390625f + l, (ad + 0.0f) * 0.00390625f + m).color(q, r, s, 0.8f).normal(-1.0f, 0.0f, 0.0f).next();
                            bufferBuilder.vertex(ac + (float)ae + 0.0f, z + 0.0f, ad + 0.0f).texture((ac + (float)ae + 0.5f) * 0.00390625f + l, (ad + 0.0f) * 0.00390625f + m).color(q, r, s, 0.8f).normal(-1.0f, 0.0f, 0.0f).next();
                        }
                    }
                    if (aa <= 1) {
                        for (ae = 0; ae < 8; ++ae) {
                            bufferBuilder.vertex(ac + (float)ae + 1.0f - 9.765625E-4f, z + 0.0f, ad + 8.0f).texture((ac + (float)ae + 0.5f) * 0.00390625f + l, (ad + 8.0f) * 0.00390625f + m).color(q, r, s, 0.8f).normal(1.0f, 0.0f, 0.0f).next();
                            bufferBuilder.vertex(ac + (float)ae + 1.0f - 9.765625E-4f, z + 4.0f, ad + 8.0f).texture((ac + (float)ae + 0.5f) * 0.00390625f + l, (ad + 8.0f) * 0.00390625f + m).color(q, r, s, 0.8f).normal(1.0f, 0.0f, 0.0f).next();
                            bufferBuilder.vertex(ac + (float)ae + 1.0f - 9.765625E-4f, z + 4.0f, ad + 0.0f).texture((ac + (float)ae + 0.5f) * 0.00390625f + l, (ad + 0.0f) * 0.00390625f + m).color(q, r, s, 0.8f).normal(1.0f, 0.0f, 0.0f).next();
                            bufferBuilder.vertex(ac + (float)ae + 1.0f - 9.765625E-4f, z + 0.0f, ad + 0.0f).texture((ac + (float)ae + 0.5f) * 0.00390625f + l, (ad + 0.0f) * 0.00390625f + m).color(q, r, s, 0.8f).normal(1.0f, 0.0f, 0.0f).next();
                        }
                    }
                    if (ab > -1) {
                        for (ae = 0; ae < 8; ++ae) {
                            bufferBuilder.vertex(ac + 0.0f, z + 4.0f, ad + (float)ae + 0.0f).texture((ac + 0.0f) * 0.00390625f + l, (ad + (float)ae + 0.5f) * 0.00390625f + m).color(w, x, y, 0.8f).normal(0.0f, 0.0f, -1.0f).next();
                            bufferBuilder.vertex(ac + 8.0f, z + 4.0f, ad + (float)ae + 0.0f).texture((ac + 8.0f) * 0.00390625f + l, (ad + (float)ae + 0.5f) * 0.00390625f + m).color(w, x, y, 0.8f).normal(0.0f, 0.0f, -1.0f).next();
                            bufferBuilder.vertex(ac + 8.0f, z + 0.0f, ad + (float)ae + 0.0f).texture((ac + 8.0f) * 0.00390625f + l, (ad + (float)ae + 0.5f) * 0.00390625f + m).color(w, x, y, 0.8f).normal(0.0f, 0.0f, -1.0f).next();
                            bufferBuilder.vertex(ac + 0.0f, z + 0.0f, ad + (float)ae + 0.0f).texture((ac + 0.0f) * 0.00390625f + l, (ad + (float)ae + 0.5f) * 0.00390625f + m).color(w, x, y, 0.8f).normal(0.0f, 0.0f, -1.0f).next();
                        }
                    }
                    if (ab > 1) continue;
                    for (ae = 0; ae < 8; ++ae) {
                        bufferBuilder.vertex(ac + 0.0f, z + 4.0f, ad + (float)ae + 1.0f - 9.765625E-4f).texture((ac + 0.0f) * 0.00390625f + l, (ad + (float)ae + 0.5f) * 0.00390625f + m).color(w, x, y, 0.8f).normal(0.0f, 0.0f, 1.0f).next();
                        bufferBuilder.vertex(ac + 8.0f, z + 4.0f, ad + (float)ae + 1.0f - 9.765625E-4f).texture((ac + 8.0f) * 0.00390625f + l, (ad + (float)ae + 0.5f) * 0.00390625f + m).color(w, x, y, 0.8f).normal(0.0f, 0.0f, 1.0f).next();
                        bufferBuilder.vertex(ac + 8.0f, z + 0.0f, ad + (float)ae + 1.0f - 9.765625E-4f).texture((ac + 8.0f) * 0.00390625f + l, (ad + (float)ae + 0.5f) * 0.00390625f + m).color(w, x, y, 0.8f).normal(0.0f, 0.0f, 1.0f).next();
                        bufferBuilder.vertex(ac + 0.0f, z + 0.0f, ad + (float)ae + 1.0f - 9.765625E-4f).texture((ac + 0.0f) * 0.00390625f + l, (ad + (float)ae + 0.5f) * 0.00390625f + m).color(w, x, y, 0.8f).normal(0.0f, 0.0f, 1.0f).next();
                    }
                }
            }
        } else {
            boolean aa = true;
            int ab = 32;
            for (int af = -32; af < 32; af += 32) {
                for (int ag = -32; ag < 32; ag += 32) {
                    bufferBuilder.vertex(af + 0, z, ag + 32).texture((float)(af + 0) * 0.00390625f + l, (float)(ag + 32) * 0.00390625f + m).color(n, o, p, 0.8f).normal(0.0f, -1.0f, 0.0f).next();
                    bufferBuilder.vertex(af + 32, z, ag + 32).texture((float)(af + 32) * 0.00390625f + l, (float)(ag + 32) * 0.00390625f + m).color(n, o, p, 0.8f).normal(0.0f, -1.0f, 0.0f).next();
                    bufferBuilder.vertex(af + 32, z, ag + 0).texture((float)(af + 32) * 0.00390625f + l, (float)(ag + 0) * 0.00390625f + m).color(n, o, p, 0.8f).normal(0.0f, -1.0f, 0.0f).next();
                    bufferBuilder.vertex(af + 0, z, ag + 0).texture((float)(af + 0) * 0.00390625f + l, (float)(ag + 0) * 0.00390625f + m).color(n, o, p, 0.8f).normal(0.0f, -1.0f, 0.0f).next();
                }
            }
        }
    }

    private void updateChunks(long l) {
        this.needsTerrainUpdate |= this.chunkBuilder.upload();
        if (!this.chunksToRebuild.isEmpty()) {
            Iterator<ChunkBuilder.BuiltChunk> iterator = this.chunksToRebuild.iterator();
            while (iterator.hasNext()) {
                ChunkBuilder.BuiltChunk builtChunk = iterator.next();
                if (builtChunk.needsImportantRebuild()) {
                    this.chunkBuilder.rebuild(builtChunk);
                } else {
                    builtChunk.scheduleRebuild(this.chunkBuilder);
                }
                builtChunk.cancelRebuild();
                iterator.remove();
                long m = l - Util.getMeasuringTimeNano();
                if (m >= 0L) continue;
                break;
            }
        }
    }

    private void renderWorldBorder(Camera camera) {
        float v;
        double u;
        double t;
        float s;
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        WorldBorder worldBorder = this.world.getWorldBorder();
        double d = this.client.options.viewDistance * 16;
        if (camera.getPos().x < worldBorder.getBoundEast() - d && camera.getPos().x > worldBorder.getBoundWest() + d && camera.getPos().z < worldBorder.getBoundSouth() - d && camera.getPos().z > worldBorder.getBoundNorth() + d) {
            return;
        }
        double e = 1.0 - worldBorder.getDistanceInsideBorder(camera.getPos().x, camera.getPos().z) / d;
        e = Math.pow(e, 4.0);
        double f = camera.getPos().x;
        double g = camera.getPos().y;
        double h = camera.getPos().z;
        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        this.textureManager.bindTexture(FORCEFIELD);
        RenderSystem.depthMask(false);
        RenderSystem.pushMatrix();
        int i = worldBorder.getStage().getColor();
        float j = (float)(i >> 16 & 0xFF) / 255.0f;
        float k = (float)(i >> 8 & 0xFF) / 255.0f;
        float l = (float)(i & 0xFF) / 255.0f;
        RenderSystem.color4f(j, k, l, (float)e);
        RenderSystem.polygonOffset(-3.0f, -3.0f);
        RenderSystem.enablePolygonOffset();
        RenderSystem.defaultAlphaFunc();
        RenderSystem.enableAlphaTest();
        RenderSystem.disableCull();
        float m = (float)(Util.getMeasuringTimeMs() % 3000L) / 3000.0f;
        float n = 0.0f;
        float o = 0.0f;
        float p = 128.0f;
        bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE);
        double q = Math.max((double)MathHelper.floor(h - d), worldBorder.getBoundNorth());
        double r = Math.min((double)MathHelper.ceil(h + d), worldBorder.getBoundSouth());
        if (f > worldBorder.getBoundEast() - d) {
            s = 0.0f;
            t = q;
            while (t < r) {
                u = Math.min(1.0, r - t);
                v = (float)u * 0.5f;
                this.method_22978(bufferBuilder, f, g, h, worldBorder.getBoundEast(), 256, t, m + s, m + 0.0f);
                this.method_22978(bufferBuilder, f, g, h, worldBorder.getBoundEast(), 256, t + u, m + v + s, m + 0.0f);
                this.method_22978(bufferBuilder, f, g, h, worldBorder.getBoundEast(), 0, t + u, m + v + s, m + 128.0f);
                this.method_22978(bufferBuilder, f, g, h, worldBorder.getBoundEast(), 0, t, m + s, m + 128.0f);
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
                this.method_22978(bufferBuilder, f, g, h, worldBorder.getBoundWest(), 256, t, m + s, m + 0.0f);
                this.method_22978(bufferBuilder, f, g, h, worldBorder.getBoundWest(), 256, t + u, m + v + s, m + 0.0f);
                this.method_22978(bufferBuilder, f, g, h, worldBorder.getBoundWest(), 0, t + u, m + v + s, m + 128.0f);
                this.method_22978(bufferBuilder, f, g, h, worldBorder.getBoundWest(), 0, t, m + s, m + 128.0f);
                t += 1.0;
                s += 0.5f;
            }
        }
        q = Math.max((double)MathHelper.floor(f - d), worldBorder.getBoundWest());
        r = Math.min((double)MathHelper.ceil(f + d), worldBorder.getBoundEast());
        if (h > worldBorder.getBoundSouth() - d) {
            s = 0.0f;
            t = q;
            while (t < r) {
                u = Math.min(1.0, r - t);
                v = (float)u * 0.5f;
                this.method_22978(bufferBuilder, f, g, h, t, 256, worldBorder.getBoundSouth(), m + s, m + 0.0f);
                this.method_22978(bufferBuilder, f, g, h, t + u, 256, worldBorder.getBoundSouth(), m + v + s, m + 0.0f);
                this.method_22978(bufferBuilder, f, g, h, t + u, 0, worldBorder.getBoundSouth(), m + v + s, m + 128.0f);
                this.method_22978(bufferBuilder, f, g, h, t, 0, worldBorder.getBoundSouth(), m + s, m + 128.0f);
                t += 1.0;
                s += 0.5f;
            }
        }
        if (h < worldBorder.getBoundNorth() + d) {
            s = 0.0f;
            t = q;
            while (t < r) {
                u = Math.min(1.0, r - t);
                v = (float)u * 0.5f;
                this.method_22978(bufferBuilder, f, g, h, t, 256, worldBorder.getBoundNorth(), m + s, m + 0.0f);
                this.method_22978(bufferBuilder, f, g, h, t + u, 256, worldBorder.getBoundNorth(), m + v + s, m + 0.0f);
                this.method_22978(bufferBuilder, f, g, h, t + u, 0, worldBorder.getBoundNorth(), m + v + s, m + 128.0f);
                this.method_22978(bufferBuilder, f, g, h, t, 0, worldBorder.getBoundNorth(), m + s, m + 128.0f);
                t += 1.0;
                s += 0.5f;
            }
        }
        bufferBuilder.end();
        BufferRenderer.draw(bufferBuilder);
        RenderSystem.enableCull();
        RenderSystem.disableAlphaTest();
        RenderSystem.polygonOffset(0.0f, 0.0f);
        RenderSystem.disablePolygonOffset();
        RenderSystem.enableAlphaTest();
        RenderSystem.disableBlend();
        RenderSystem.popMatrix();
        RenderSystem.depthMask(true);
    }

    private void method_22978(BufferBuilder bufferBuilder, double d, double e, double f, double g, int i, double h, float j, float k) {
        bufferBuilder.vertex(g - d, (double)i - e, h - f).texture(j, k).next();
    }

    private void drawBlockOutline(MatrixStack matrixStack, VertexConsumer vertexConsumer, Entity entity, double d, double e, double f, BlockPos blockPos, BlockState blockState) {
        WorldRenderer.drawShapeOutline(matrixStack, vertexConsumer, blockState.getOutlineShape(this.world, blockPos, EntityContext.of(entity)), (double)blockPos.getX() - d, (double)blockPos.getY() - e, (double)blockPos.getZ() - f, 0.0f, 0.0f, 0.0f, 0.4f);
    }

    public static void method_22983(MatrixStack matrixStack, VertexConsumer vertexConsumer, VoxelShape voxelShape, double d, double e, double f, float g, float h, float i, float j) {
        List<Box> list = voxelShape.getBoundingBoxes();
        int k = MathHelper.ceil((double)list.size() / 3.0);
        for (int l = 0; l < list.size(); ++l) {
            Box box = list.get(l);
            float m = ((float)l % (float)k + 1.0f) / (float)k;
            float n = l / k;
            float o = m * (float)(n == 0.0f ? 1 : 0);
            float p = m * (float)(n == 1.0f ? 1 : 0);
            float q = m * (float)(n == 2.0f ? 1 : 0);
            WorldRenderer.drawShapeOutline(matrixStack, vertexConsumer, VoxelShapes.cuboid(box.offset(0.0, 0.0, 0.0)), d, e, f, o, p, q, 1.0f);
        }
    }

    private static void drawShapeOutline(MatrixStack matrixStack, VertexConsumer vertexConsumer, VoxelShape voxelShape, double d, double e, double f, float g, float h, float i, float j) {
        Matrix4f matrix4f = matrixStack.peek().getModel();
        voxelShape.forEachEdge((k, l, m, n, o, p) -> {
            vertexConsumer.vertex(matrix4f, (float)(k + d), (float)(l + e), (float)(m + f)).color(g, h, i, j).next();
            vertexConsumer.vertex(matrix4f, (float)(n + d), (float)(o + e), (float)(p + f)).color(g, h, i, j).next();
        });
    }

    public static void drawBox(VertexConsumer vertexConsumer, double d, double e, double f, double g, double h, double i, float j, float k, float l, float m) {
        WorldRenderer.drawBox(new MatrixStack(), vertexConsumer, d, e, f, g, h, i, j, k, l, m, j, k, l);
    }

    public static void drawBox(MatrixStack matrixStack, VertexConsumer vertexConsumer, Box box, float f, float g, float h, float i) {
        WorldRenderer.drawBox(matrixStack, vertexConsumer, box.x1, box.y1, box.z1, box.x2, box.y2, box.z2, f, g, h, i, f, g, h);
    }

    public static void drawBox(MatrixStack matrixStack, VertexConsumer vertexConsumer, double d, double e, double f, double g, double h, double i, float j, float k, float l, float m) {
        WorldRenderer.drawBox(matrixStack, vertexConsumer, d, e, f, g, h, i, j, k, l, m, j, k, l);
    }

    public static void drawBox(MatrixStack matrixStack, VertexConsumer vertexConsumer, double d, double e, double f, double g, double h, double i, float j, float k, float l, float m, float n, float o, float p) {
        Matrix4f matrix4f = matrixStack.peek().getModel();
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

    public static void drawBox(BufferBuilder bufferBuilder, double d, double e, double f, double g, double h, double i, float j, float k, float l, float m) {
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
        for (int i = blockPos.getZ() - 1; i <= blockPos.getZ() + 1; ++i) {
            for (int j = blockPos.getX() - 1; j <= blockPos.getX() + 1; ++j) {
                for (int k = blockPos.getY() - 1; k <= blockPos.getY() + 1; ++k) {
                    this.scheduleChunkRender(j >> 4, k >> 4, i >> 4, bl);
                }
            }
        }
    }

    public void scheduleBlockRenders(int i, int j, int k, int l, int m, int n) {
        for (int o = k - 1; o <= n + 1; ++o) {
            for (int p = i - 1; p <= l + 1; ++p) {
                for (int q = j - 1; q <= m + 1; ++q) {
                    this.scheduleBlockRender(p >> 4, q >> 4, o >> 4);
                }
            }
        }
    }

    public void checkBlockRerender(BlockPos blockPos, BlockState blockState, BlockState blockState2) {
        if (this.client.getBakedModelManager().shouldRerender(blockState, blockState2)) {
            this.scheduleBlockRenders(blockPos.getX(), blockPos.getY(), blockPos.getZ(), blockPos.getX(), blockPos.getY(), blockPos.getZ());
        }
    }

    public void scheduleBlockRenders(int i, int j, int k) {
        for (int l = k - 1; l <= k + 1; ++l) {
            for (int m = i - 1; m <= i + 1; ++m) {
                for (int n = j - 1; n <= j + 1; ++n) {
                    this.scheduleBlockRender(m, n, l);
                }
            }
        }
    }

    public void scheduleBlockRender(int i, int j, int k) {
        this.scheduleChunkRender(i, j, k, false);
    }

    private void scheduleChunkRender(int i, int j, int k, boolean bl) {
        this.renderedChunks.scheduleRebuild(i, j, k, bl);
    }

    public void playSong(@Nullable SoundEvent soundEvent, BlockPos blockPos) {
        SoundInstance soundInstance = this.playingSongs.get(blockPos);
        if (soundInstance != null) {
            this.client.getSoundManager().stop(soundInstance);
            this.playingSongs.remove(blockPos);
        }
        if (soundEvent != null) {
            MusicDiscItem musicDiscItem = MusicDiscItem.bySound(soundEvent);
            if (musicDiscItem != null) {
                this.client.inGameHud.setRecordPlayingOverlay(musicDiscItem.getDescription().asFormattedString());
            }
            soundInstance = PositionedSoundInstance.record(soundEvent, blockPos.getX(), blockPos.getY(), blockPos.getZ());
            this.playingSongs.put(blockPos, soundInstance);
            this.client.getSoundManager().play(soundInstance);
        }
        this.updateEntitiesForSong(this.world, blockPos, soundEvent != null);
    }

    private void updateEntitiesForSong(World world, BlockPos blockPos, boolean bl) {
        List<LivingEntity> list = world.getNonSpectatingEntities(LivingEntity.class, new Box(blockPos).expand(3.0));
        for (LivingEntity livingEntity : list) {
            livingEntity.setNearbySongPlaying(blockPos, bl);
        }
    }

    public void addParticle(ParticleEffect particleEffect, boolean bl, double d, double e, double f, double g, double h, double i) {
        this.addParticle(particleEffect, bl, false, d, e, f, g, h, i);
    }

    public void addParticle(ParticleEffect particleEffect, boolean bl, boolean bl2, double d, double e, double f, double g, double h, double i) {
        try {
            this.spawnParticle(particleEffect, bl, bl2, d, e, f, g, h, i);
        } catch (Throwable throwable) {
            CrashReport crashReport = CrashReport.create(throwable, "Exception while adding particle");
            CrashReportSection crashReportSection = crashReport.addElement("Particle being added");
            crashReportSection.add("ID", Registry.PARTICLE_TYPE.getId(particleEffect.getType()));
            crashReportSection.add("Parameters", particleEffect.asString());
            crashReportSection.add("Position", () -> CrashReportSection.createPositionString(d, e, f));
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
        if (this.client == null || !camera.isReady() || this.client.particleManager == null) {
            return null;
        }
        ParticlesOption particlesOption = this.getRandomParticleSpawnChance(bl2);
        if (bl) {
            return this.client.particleManager.addParticle(particleEffect, d, e, f, g, h, i);
        }
        if (camera.getPos().squaredDistanceTo(d, e, f) > 1024.0) {
            return null;
        }
        if (particlesOption == ParticlesOption.MINIMAL) {
            return null;
        }
        return this.client.particleManager.addParticle(particleEffect, d, e, f, g, h, i);
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
            case 1038: {
                Camera camera = this.client.gameRenderer.getCamera();
                if (!camera.isReady()) break;
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
                    this.world.playSound(h, k, l, SoundEvents.ENTITY_WITHER_SPAWN, SoundCategory.HOSTILE, 1.0f, 1.0f, false);
                    break;
                }
                if (i == 1038) {
                    this.world.playSound(h, k, l, SoundEvents.BLOCK_END_PORTAL_SPAWN, SoundCategory.HOSTILE, 1.0f, 1.0f, false);
                    break;
                }
                this.world.playSound(h, k, l, SoundEvents.ENTITY_ENDER_DRAGON_DEATH, SoundCategory.HOSTILE, 5.0f, 1.0f, false);
            }
        }
    }

    public void playLevelEvent(PlayerEntity playerEntity, int i, BlockPos blockPos, int j) {
        Random random = this.world.random;
        switch (i) {
            case 1035: {
                this.world.playSound(blockPos, SoundEvents.BLOCK_BREWING_STAND_BREW, SoundCategory.BLOCKS, 1.0f, 1.0f, false);
                break;
            }
            case 1033: {
                this.world.playSound(blockPos, SoundEvents.BLOCK_CHORUS_FLOWER_GROW, SoundCategory.BLOCKS, 1.0f, 1.0f, false);
                break;
            }
            case 1034: {
                this.world.playSound(blockPos, SoundEvents.BLOCK_CHORUS_FLOWER_DEATH, SoundCategory.BLOCKS, 1.0f, 1.0f, false);
                break;
            }
            case 1032: {
                this.client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.BLOCK_PORTAL_TRAVEL, random.nextFloat() * 0.4f + 0.8f));
                break;
            }
            case 1001: {
                this.world.playSound(blockPos, SoundEvents.BLOCK_DISPENSER_FAIL, SoundCategory.BLOCKS, 1.0f, 1.2f, false);
                break;
            }
            case 1000: {
                this.world.playSound(blockPos, SoundEvents.BLOCK_DISPENSER_DISPENSE, SoundCategory.BLOCKS, 1.0f, 1.0f, false);
                break;
            }
            case 1003: {
                this.world.playSound(blockPos, SoundEvents.ENTITY_ENDER_EYE_LAUNCH, SoundCategory.NEUTRAL, 1.0f, 1.2f, false);
                break;
            }
            case 1004: {
                this.world.playSound(blockPos, SoundEvents.ENTITY_FIREWORK_ROCKET_SHOOT, SoundCategory.NEUTRAL, 1.0f, 1.2f, false);
                break;
            }
            case 1002: {
                this.world.playSound(blockPos, SoundEvents.BLOCK_DISPENSER_LAUNCH, SoundCategory.BLOCKS, 1.0f, 1.2f, false);
                break;
            }
            case 2000: {
                Direction direction = Direction.byId(j);
                int k = direction.getOffsetX();
                int l = direction.getOffsetY();
                int m = direction.getOffsetZ();
                double d = (double)blockPos.getX() + (double)k * 0.6 + 0.5;
                double e = (double)blockPos.getY() + (double)l * 0.6 + 0.5;
                double f = (double)blockPos.getZ() + (double)m * 0.6 + 0.5;
                for (int n = 0; n < 10; ++n) {
                    double g = random.nextDouble() * 0.2 + 0.01;
                    double h = d + (double)k * 0.01 + (random.nextDouble() - 0.5) * (double)m * 0.5;
                    double o = e + (double)l * 0.01 + (random.nextDouble() - 0.5) * (double)l * 0.5;
                    double p = f + (double)m * 0.01 + (random.nextDouble() - 0.5) * (double)k * 0.5;
                    double q = (double)k * g + random.nextGaussian() * 0.01;
                    double r = (double)l * g + random.nextGaussian() * 0.01;
                    double s = (double)m * g + random.nextGaussian() * 0.01;
                    this.addParticle(ParticleTypes.SMOKE, h, o, p, q, r, s);
                }
                break;
            }
            case 2003: {
                double t = (double)blockPos.getX() + 0.5;
                double u = blockPos.getY();
                double d = (double)blockPos.getZ() + 0.5;
                for (int v = 0; v < 8; ++v) {
                    this.addParticle(new ItemStackParticleEffect(ParticleTypes.ITEM, new ItemStack(Items.ENDER_EYE)), t, u, d, random.nextGaussian() * 0.15, random.nextDouble() * 0.2, random.nextGaussian() * 0.15);
                }
                for (double e = 0.0; e < Math.PI * 2; e += 0.15707963267948966) {
                    this.addParticle(ParticleTypes.PORTAL, t + Math.cos(e) * 5.0, u - 0.4, d + Math.sin(e) * 5.0, Math.cos(e) * -5.0, 0.0, Math.sin(e) * -5.0);
                    this.addParticle(ParticleTypes.PORTAL, t + Math.cos(e) * 5.0, u - 0.4, d + Math.sin(e) * 5.0, Math.cos(e) * -7.0, 0.0, Math.sin(e) * -7.0);
                }
                break;
            }
            case 2002: 
            case 2007: {
                double t = blockPos.getX();
                double u = blockPos.getY();
                double d = blockPos.getZ();
                for (int v = 0; v < 8; ++v) {
                    this.addParticle(new ItemStackParticleEffect(ParticleTypes.ITEM, new ItemStack(Items.SPLASH_POTION)), t, u, d, random.nextGaussian() * 0.15, random.nextDouble() * 0.2, random.nextGaussian() * 0.15);
                }
                float w = (float)(j >> 16 & 0xFF) / 255.0f;
                float x = (float)(j >> 8 & 0xFF) / 255.0f;
                float y = (float)(j >> 0 & 0xFF) / 255.0f;
                DefaultParticleType particleEffect = i == 2007 ? ParticleTypes.INSTANT_EFFECT : ParticleTypes.EFFECT;
                for (int n = 0; n < 100; ++n) {
                    double g = random.nextDouble() * 4.0;
                    double h = random.nextDouble() * Math.PI * 2.0;
                    double o = Math.cos(h) * g;
                    double p = 0.01 + random.nextDouble() * 0.5;
                    double q = Math.sin(h) * g;
                    Particle particle = this.spawnParticle(particleEffect, particleEffect.getType().shouldAlwaysSpawn(), t + o * 0.1, u + 0.3, d + q * 0.1, o, p, q);
                    if (particle == null) continue;
                    float z = 0.75f + random.nextFloat() * 0.25f;
                    particle.setColor(w * z, x * z, y * z);
                    particle.move((float)g);
                }
                this.world.playSound(blockPos, SoundEvents.ENTITY_SPLASH_POTION_BREAK, SoundCategory.NEUTRAL, 1.0f, this.world.random.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 2001: {
                BlockState blockState = Block.getStateFromRawId(j);
                if (!blockState.isAir()) {
                    BlockSoundGroup blockSoundGroup = blockState.getSoundGroup();
                    this.world.playSound(blockPos, blockSoundGroup.getBreakSound(), SoundCategory.BLOCKS, (blockSoundGroup.getVolume() + 1.0f) / 2.0f, blockSoundGroup.getPitch() * 0.8f, false);
                }
                this.client.particleManager.addBlockBreakParticles(blockPos, blockState);
                break;
            }
            case 2004: {
                for (int k = 0; k < 20; ++k) {
                    double u = (double)blockPos.getX() + 0.5 + ((double)this.world.random.nextFloat() - 0.5) * 2.0;
                    double d = (double)blockPos.getY() + 0.5 + ((double)this.world.random.nextFloat() - 0.5) * 2.0;
                    double e = (double)blockPos.getZ() + 0.5 + ((double)this.world.random.nextFloat() - 0.5) * 2.0;
                    this.world.addParticle(ParticleTypes.SMOKE, u, d, e, 0.0, 0.0, 0.0);
                    this.world.addParticle(ParticleTypes.FLAME, u, d, e, 0.0, 0.0, 0.0);
                }
                break;
            }
            case 2005: {
                BoneMealItem.createParticles(this.world, blockPos, j);
                break;
            }
            case 2008: {
                this.world.addParticle(ParticleTypes.EXPLOSION, (double)blockPos.getX() + 0.5, (double)blockPos.getY() + 0.5, (double)blockPos.getZ() + 0.5, 0.0, 0.0, 0.0);
                break;
            }
            case 1500: {
                ComposterBlock.playEffects(this.world, blockPos, j > 0);
                break;
            }
            case 1501: {
                this.world.playSound(blockPos, SoundEvents.BLOCK_LAVA_EXTINGUISH, SoundCategory.BLOCKS, 0.5f, 2.6f + (this.world.getRandom().nextFloat() - this.world.getRandom().nextFloat()) * 0.8f, false);
                for (int k = 0; k < 8; ++k) {
                    this.world.addParticle(ParticleTypes.LARGE_SMOKE, (double)blockPos.getX() + Math.random(), (double)blockPos.getY() + 1.2, (double)blockPos.getZ() + Math.random(), 0.0, 0.0, 0.0);
                }
                break;
            }
            case 1502: {
                this.world.playSound(blockPos, SoundEvents.BLOCK_REDSTONE_TORCH_BURNOUT, SoundCategory.BLOCKS, 0.5f, 2.6f + (this.world.random.nextFloat() - this.world.random.nextFloat()) * 0.8f, false);
                for (int k = 0; k < 5; ++k) {
                    double u = (double)blockPos.getX() + random.nextDouble() * 0.6 + 0.2;
                    double d = (double)blockPos.getY() + random.nextDouble() * 0.6 + 0.2;
                    double e = (double)blockPos.getZ() + random.nextDouble() * 0.6 + 0.2;
                    this.world.addParticle(ParticleTypes.SMOKE, u, d, e, 0.0, 0.0, 0.0);
                }
                break;
            }
            case 1503: {
                this.world.playSound(blockPos, SoundEvents.BLOCK_END_PORTAL_FRAME_FILL, SoundCategory.BLOCKS, 1.0f, 1.0f, false);
                for (int k = 0; k < 16; ++k) {
                    double u = (float)blockPos.getX() + (5.0f + random.nextFloat() * 6.0f) / 16.0f;
                    double d = (float)blockPos.getY() + 0.8125f;
                    double e = (float)blockPos.getZ() + (5.0f + random.nextFloat() * 6.0f) / 16.0f;
                    double f = 0.0;
                    double aa = 0.0;
                    double ab = 0.0;
                    this.world.addParticle(ParticleTypes.SMOKE, u, d, e, 0.0, 0.0, 0.0);
                }
                break;
            }
            case 2006: {
                for (int k = 0; k < 200; ++k) {
                    float ac = random.nextFloat() * 4.0f;
                    float ad = random.nextFloat() * ((float)Math.PI * 2);
                    double d = MathHelper.cos(ad) * ac;
                    double e = 0.01 + random.nextDouble() * 0.5;
                    double f = MathHelper.sin(ad) * ac;
                    Particle particle2 = this.spawnParticle(ParticleTypes.DRAGON_BREATH, false, (double)blockPos.getX() + d * 0.1, (double)blockPos.getY() + 0.3, (double)blockPos.getZ() + f * 0.1, d, e, f);
                    if (particle2 == null) continue;
                    particle2.move(ac);
                }
                this.world.playSound(blockPos, SoundEvents.ENTITY_DRAGON_FIREBALL_EXPLODE, SoundCategory.HOSTILE, 1.0f, this.world.random.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 2009: {
                for (int k = 0; k < 8; ++k) {
                    this.world.addParticle(ParticleTypes.CLOUD, (double)blockPos.getX() + Math.random(), (double)blockPos.getY() + 1.2, (double)blockPos.getZ() + Math.random(), 0.0, 0.0, 0.0);
                }
                break;
            }
            case 1012: {
                this.world.playSound(blockPos, SoundEvents.BLOCK_WOODEN_DOOR_CLOSE, SoundCategory.BLOCKS, 1.0f, this.world.random.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1036: {
                this.world.playSound(blockPos, SoundEvents.BLOCK_IRON_TRAPDOOR_CLOSE, SoundCategory.BLOCKS, 1.0f, this.world.random.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1013: {
                this.world.playSound(blockPos, SoundEvents.BLOCK_WOODEN_TRAPDOOR_CLOSE, SoundCategory.BLOCKS, 1.0f, this.world.random.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1014: {
                this.world.playSound(blockPos, SoundEvents.BLOCK_FENCE_GATE_CLOSE, SoundCategory.BLOCKS, 1.0f, this.world.random.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1011: {
                this.world.playSound(blockPos, SoundEvents.BLOCK_IRON_DOOR_CLOSE, SoundCategory.BLOCKS, 1.0f, this.world.random.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1006: {
                this.world.playSound(blockPos, SoundEvents.BLOCK_WOODEN_DOOR_OPEN, SoundCategory.BLOCKS, 1.0f, this.world.random.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1007: {
                this.world.playSound(blockPos, SoundEvents.BLOCK_WOODEN_TRAPDOOR_OPEN, SoundCategory.BLOCKS, 1.0f, this.world.random.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1037: {
                this.world.playSound(blockPos, SoundEvents.BLOCK_IRON_TRAPDOOR_OPEN, SoundCategory.BLOCKS, 1.0f, this.world.random.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1008: {
                this.world.playSound(blockPos, SoundEvents.BLOCK_FENCE_GATE_OPEN, SoundCategory.BLOCKS, 1.0f, this.world.random.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1005: {
                this.world.playSound(blockPos, SoundEvents.BLOCK_IRON_DOOR_OPEN, SoundCategory.BLOCKS, 1.0f, this.world.random.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1009: {
                this.world.playSound(blockPos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.5f, 2.6f + (random.nextFloat() - random.nextFloat()) * 0.8f, false);
                break;
            }
            case 1029: {
                this.world.playSound(blockPos, SoundEvents.BLOCK_ANVIL_DESTROY, SoundCategory.BLOCKS, 1.0f, this.world.random.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1030: {
                this.world.playSound(blockPos, SoundEvents.BLOCK_ANVIL_USE, SoundCategory.BLOCKS, 1.0f, this.world.random.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1031: {
                this.world.playSound(blockPos, SoundEvents.BLOCK_ANVIL_LAND, SoundCategory.BLOCKS, 0.3f, this.world.random.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1039: {
                this.world.playSound(blockPos, SoundEvents.ENTITY_PHANTOM_BITE, SoundCategory.HOSTILE, 0.3f, this.world.random.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1010: {
                if (Item.byRawId(j) instanceof MusicDiscItem) {
                    this.playSong(((MusicDiscItem)Item.byRawId(j)).getSound(), blockPos);
                    break;
                }
                this.playSong(null, blockPos);
                break;
            }
            case 1015: {
                this.world.playSound(blockPos, SoundEvents.ENTITY_GHAST_WARN, SoundCategory.HOSTILE, 10.0f, (random.nextFloat() - random.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1017: {
                this.world.playSound(blockPos, SoundEvents.ENTITY_ENDER_DRAGON_SHOOT, SoundCategory.HOSTILE, 10.0f, (random.nextFloat() - random.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1016: {
                this.world.playSound(blockPos, SoundEvents.ENTITY_GHAST_SHOOT, SoundCategory.HOSTILE, 10.0f, (random.nextFloat() - random.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1019: {
                this.world.playSound(blockPos, SoundEvents.ENTITY_ZOMBIE_ATTACK_WOODEN_DOOR, SoundCategory.HOSTILE, 2.0f, (random.nextFloat() - random.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1022: {
                this.world.playSound(blockPos, SoundEvents.ENTITY_WITHER_BREAK_BLOCK, SoundCategory.HOSTILE, 2.0f, (random.nextFloat() - random.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1021: {
                this.world.playSound(blockPos, SoundEvents.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR, SoundCategory.HOSTILE, 2.0f, (random.nextFloat() - random.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1020: {
                this.world.playSound(blockPos, SoundEvents.ENTITY_ZOMBIE_ATTACK_IRON_DOOR, SoundCategory.HOSTILE, 2.0f, (random.nextFloat() - random.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1018: {
                this.world.playSound(blockPos, SoundEvents.ENTITY_BLAZE_SHOOT, SoundCategory.HOSTILE, 2.0f, (random.nextFloat() - random.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1024: {
                this.world.playSound(blockPos, SoundEvents.ENTITY_WITHER_SHOOT, SoundCategory.HOSTILE, 2.0f, (random.nextFloat() - random.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1026: {
                this.world.playSound(blockPos, SoundEvents.ENTITY_ZOMBIE_INFECT, SoundCategory.HOSTILE, 2.0f, (random.nextFloat() - random.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1027: {
                this.world.playSound(blockPos, SoundEvents.ENTITY_ZOMBIE_VILLAGER_CONVERTED, SoundCategory.NEUTRAL, 2.0f, (random.nextFloat() - random.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1040: {
                this.world.playSound(blockPos, SoundEvents.ENTITY_ZOMBIE_CONVERTED_TO_DROWNED, SoundCategory.NEUTRAL, 2.0f, (random.nextFloat() - random.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1041: {
                this.world.playSound(blockPos, SoundEvents.ENTITY_HUSK_CONVERTED_TO_ZOMBIE, SoundCategory.NEUTRAL, 2.0f, (random.nextFloat() - random.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1025: {
                this.world.playSound(blockPos, SoundEvents.ENTITY_BAT_TAKEOFF, SoundCategory.NEUTRAL, 0.05f, (random.nextFloat() - random.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1042: {
                this.world.playSound(blockPos, SoundEvents.BLOCK_GRINDSTONE_USE, SoundCategory.BLOCKS, 1.0f, this.world.random.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1043: {
                this.world.playSound(blockPos, SoundEvents.ITEM_BOOK_PAGE_TURN, SoundCategory.BLOCKS, 1.0f, this.world.random.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 3000: {
                this.world.addParticle(ParticleTypes.EXPLOSION_EMITTER, true, (double)blockPos.getX() + 0.5, (double)blockPos.getY() + 0.5, (double)blockPos.getZ() + 0.5, 0.0, 0.0, 0.0);
                this.world.playSound(blockPos, SoundEvents.BLOCK_END_GATEWAY_SPAWN, SoundCategory.BLOCKS, 10.0f, (1.0f + (this.world.random.nextFloat() - this.world.random.nextFloat()) * 0.2f) * 0.7f, false);
                break;
            }
            case 3001: {
                this.world.playSound(blockPos, SoundEvents.ENTITY_ENDER_DRAGON_GROWL, SoundCategory.HOSTILE, 64.0f, 0.8f + this.world.random.nextFloat() * 0.3f, false);
            }
        }
    }

    public void setBlockBreakingInfo(int i, BlockPos blockPos, int j) {
        if (j < 0 || j >= 10) {
            BlockBreakingInfo blockBreakingInfo = (BlockBreakingInfo)this.blockBreakingInfos.remove(i);
            if (blockBreakingInfo != null) {
                this.removeBlockBreakingInfo(blockBreakingInfo);
            }
        } else {
            BlockBreakingInfo blockBreakingInfo = (BlockBreakingInfo)this.blockBreakingInfos.get(i);
            if (blockBreakingInfo != null) {
                this.removeBlockBreakingInfo(blockBreakingInfo);
            }
            if (blockBreakingInfo == null || blockBreakingInfo.getPos().getX() != blockPos.getX() || blockBreakingInfo.getPos().getY() != blockPos.getY() || blockBreakingInfo.getPos().getZ() != blockPos.getZ()) {
                blockBreakingInfo = new BlockBreakingInfo(i, blockPos);
                this.blockBreakingInfos.put(i, blockBreakingInfo);
            }
            blockBreakingInfo.setStage(j);
            blockBreakingInfo.setLastUpdateTick(this.ticks);
            this.blockBreakingProgressions.computeIfAbsent(blockBreakingInfo.getPos().asLong(), l -> Sets.newTreeSet()).add(blockBreakingInfo);
        }
    }

    public boolean isTerrainRenderComplete() {
        return this.chunksToRebuild.isEmpty() && this.chunkBuilder.isEmpty();
    }

    public void scheduleTerrainUpdate() {
        this.needsTerrainUpdate = true;
        this.cloudsDirty = true;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void updateBlockEntities(Collection<BlockEntity> collection, Collection<BlockEntity> collection2) {
        Set<BlockEntity> set = this.blockEntities;
        synchronized (set) {
            this.blockEntities.removeAll(collection);
            this.blockEntities.addAll(collection2);
        }
    }

    public static int method_23794(BlockRenderView blockRenderView, BlockPos blockPos) {
        return WorldRenderer.method_23793(blockRenderView, blockRenderView.getBlockState(blockPos), blockPos);
    }

    public static int method_23793(BlockRenderView blockRenderView, BlockState blockState, BlockPos blockPos) {
        int k;
        if (blockState.hasEmissiveLighting()) {
            return 0xF000F0;
        }
        int i = blockRenderView.getLightLevel(LightType.SKY, blockPos);
        int j = blockRenderView.getLightLevel(LightType.BLOCK, blockPos);
        if (j < (k = blockState.getLuminance())) {
            j = k;
        }
        return i << 20 | j << 4;
    }

    public Framebuffer method_22990() {
        return this.entityOutlinesFramebuffer;
    }

    @Environment(value=EnvType.CLIENT)
    class BuiltChunkInfo {
        private final ChunkBuilder.BuiltChunk chunk;
        private final Direction field_4125;
        private byte field_4126;
        private final int field_4122;

        private BuiltChunkInfo(@Nullable ChunkBuilder.BuiltChunk builtChunk, Direction direction, int i) {
            this.chunk = builtChunk;
            this.field_4125 = direction;
            this.field_4122 = i;
        }

        public void method_3299(byte b, Direction direction) {
            this.field_4126 = (byte)(this.field_4126 | (b | 1 << direction.ordinal()));
        }

        public boolean method_3298(Direction direction) {
            return (this.field_4126 & 1 << direction.ordinal()) > 0;
        }
    }
}

