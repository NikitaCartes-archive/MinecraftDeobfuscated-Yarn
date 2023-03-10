/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.platform.GlConst;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.PostEffectProcessor;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gl.ShaderStage;
import net.minecraft.client.gui.hud.InGameOverlayRenderer;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferBuilderStorage;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.CameraSubmersionType;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.MapRenderer;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.util.ScreenshotRecorder;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.mob.SpiderEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceFactory;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloader;
import net.minecraft.resource.SinglePreparationResourceReloader;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.GameMode;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class GameRenderer
implements AutoCloseable {
    private static final Identifier NAUSEA_OVERLAY = new Identifier("textures/misc/nausea.png");
    static final Logger LOGGER = LogUtils.getLogger();
    private static final boolean field_32688 = false;
    /**
     * Since the camera is conceptualized as a single point, a depth of {@value}
     * blocks is used to define a rectangular area to be rendered.
     * 
     * @see Camera#getProjection()
     */
    public static final float CAMERA_DEPTH = 0.05f;
    final MinecraftClient client;
    private final ResourceManager resourceManager;
    private final Random random = Random.create();
    private float viewDistance;
    public final HeldItemRenderer firstPersonRenderer;
    private final MapRenderer mapRenderer;
    private final BufferBuilderStorage buffers;
    private int ticks;
    private float fovMultiplier;
    private float lastFovMultiplier;
    private float skyDarkness;
    private float lastSkyDarkness;
    private boolean renderHand = true;
    private boolean blockOutlineEnabled = true;
    private long lastWorldIconUpdate;
    private boolean hasWorldIcon;
    private long lastWindowFocusedTime = Util.getMeasuringTimeMs();
    private final LightmapTextureManager lightmapTextureManager;
    private final OverlayTexture overlayTexture = new OverlayTexture();
    private boolean renderingPanorama;
    private float zoom = 1.0f;
    private float zoomX;
    private float zoomY;
    public static final int field_32687 = 40;
    @Nullable
    private ItemStack floatingItem;
    private int floatingItemTimeLeft;
    private float floatingItemWidth;
    private float floatingItemHeight;
    @Nullable
    PostEffectProcessor postProcessor;
    static final Identifier[] SUPER_SECRET_SETTING_PROGRAMS = new Identifier[]{new Identifier("shaders/post/notch.json"), new Identifier("shaders/post/fxaa.json"), new Identifier("shaders/post/art.json"), new Identifier("shaders/post/bumpy.json"), new Identifier("shaders/post/blobs2.json"), new Identifier("shaders/post/pencil.json"), new Identifier("shaders/post/color_convolve.json"), new Identifier("shaders/post/deconverge.json"), new Identifier("shaders/post/flip.json"), new Identifier("shaders/post/invert.json"), new Identifier("shaders/post/ntsc.json"), new Identifier("shaders/post/outline.json"), new Identifier("shaders/post/phosphor.json"), new Identifier("shaders/post/scan_pincushion.json"), new Identifier("shaders/post/sobel.json"), new Identifier("shaders/post/bits.json"), new Identifier("shaders/post/desaturate.json"), new Identifier("shaders/post/green.json"), new Identifier("shaders/post/blur.json"), new Identifier("shaders/post/wobble.json"), new Identifier("shaders/post/blobs.json"), new Identifier("shaders/post/antialias.json"), new Identifier("shaders/post/creeper.json"), new Identifier("shaders/post/spider.json")};
    public static final int SUPER_SECRET_SETTING_COUNT = SUPER_SECRET_SETTING_PROGRAMS.length;
    int superSecretSettingIndex = SUPER_SECRET_SETTING_COUNT;
    private boolean postProcessorEnabled;
    private final Camera camera = new Camera();
    public ShaderProgram blitScreenProgram;
    private final Map<String, ShaderProgram> programs = Maps.newHashMap();
    @Nullable
    private static ShaderProgram positionProgram;
    @Nullable
    private static ShaderProgram positionColorProgram;
    @Nullable
    private static ShaderProgram positionColorTexProgram;
    @Nullable
    private static ShaderProgram positionTexProgram;
    @Nullable
    private static ShaderProgram positionTexColorProgram;
    @Nullable
    private static ShaderProgram blockProgram;
    @Nullable
    private static ShaderProgram newEntityProgram;
    @Nullable
    private static ShaderProgram particleProgram;
    @Nullable
    private static ShaderProgram positionColorLightmapProgram;
    @Nullable
    private static ShaderProgram positionColorTexLightmapProgram;
    @Nullable
    private static ShaderProgram positionTexColorNormalProgram;
    @Nullable
    private static ShaderProgram positionTexLightmapColorProgram;
    @Nullable
    private static ShaderProgram renderTypeSolidProgram;
    @Nullable
    private static ShaderProgram renderTypeCutoutMippedProgram;
    @Nullable
    private static ShaderProgram renderTypeCutoutProgram;
    @Nullable
    private static ShaderProgram renderTypeTranslucentProgram;
    @Nullable
    private static ShaderProgram renderTypeTranslucentMovingBlockProgram;
    @Nullable
    private static ShaderProgram renderTypeTranslucentNoCrumblingProgram;
    @Nullable
    private static ShaderProgram renderTypeArmorCutoutNoCullProgram;
    @Nullable
    private static ShaderProgram renderTypeEntitySolidProgram;
    @Nullable
    private static ShaderProgram renderTypeEntityCutoutProgram;
    @Nullable
    private static ShaderProgram renderTypeEntityCutoutNoNullProgram;
    @Nullable
    private static ShaderProgram renderTypeEntityCutoutNoNullZOffsetProgram;
    @Nullable
    private static ShaderProgram renderTypeItemEntityTranslucentCullProgram;
    @Nullable
    private static ShaderProgram renderTypeEntityTranslucentCullProgram;
    @Nullable
    private static ShaderProgram renderTypeEntityTranslucentProgram;
    @Nullable
    private static ShaderProgram renderTypeEntityTranslucentEmissiveProgram;
    @Nullable
    private static ShaderProgram renderTypeEntitySmoothCutoutProgram;
    @Nullable
    private static ShaderProgram renderTypeBeaconBeamProgram;
    @Nullable
    private static ShaderProgram renderTypeEntityDecalProgram;
    @Nullable
    private static ShaderProgram renderTypeEntityNoOutlineProgram;
    @Nullable
    private static ShaderProgram renderTypeEntityShadowProgram;
    @Nullable
    private static ShaderProgram renderTypeEntityAlphaProgram;
    @Nullable
    private static ShaderProgram renderTypeEyesProgram;
    @Nullable
    private static ShaderProgram renderTypeEnergySwirlProgram;
    @Nullable
    private static ShaderProgram renderTypeLeashProgram;
    @Nullable
    private static ShaderProgram renderTypeWaterMaskProgram;
    @Nullable
    private static ShaderProgram renderTypeOutlineProgram;
    @Nullable
    private static ShaderProgram renderTypeArmorGlintProgram;
    @Nullable
    private static ShaderProgram renderTypeArmorEntityGlintProgram;
    @Nullable
    private static ShaderProgram renderTypeGlintTranslucentProgram;
    @Nullable
    private static ShaderProgram renderTypeGlintProgram;
    @Nullable
    private static ShaderProgram renderTypeGlintDirectProgram;
    @Nullable
    private static ShaderProgram renderTypeEntityGlintProgram;
    @Nullable
    private static ShaderProgram renderTypeEntityGlintDirectProgram;
    @Nullable
    private static ShaderProgram renderTypeTextProgram;
    @Nullable
    private static ShaderProgram renderTypeTextBackgroundProgram;
    @Nullable
    private static ShaderProgram renderTypeTextIntensityProgram;
    @Nullable
    private static ShaderProgram renderTypeTextSeeThroughProgram;
    @Nullable
    private static ShaderProgram renderTypeTextBackgroundSeeThroughProgram;
    @Nullable
    private static ShaderProgram renderTypeTextIntensitySeeThroughProgram;
    @Nullable
    private static ShaderProgram renderTypeLightningProgram;
    @Nullable
    private static ShaderProgram renderTypeTripwireProgram;
    @Nullable
    private static ShaderProgram renderTypeEndPortalProgram;
    @Nullable
    private static ShaderProgram renderTypeEndGatewayProgram;
    @Nullable
    private static ShaderProgram renderTypeLinesProgram;
    @Nullable
    private static ShaderProgram renderTypeCrumblingProgram;

    public GameRenderer(MinecraftClient client, HeldItemRenderer heldItemRenderer, ResourceManager resourceManager, BufferBuilderStorage buffers) {
        this.client = client;
        this.resourceManager = resourceManager;
        this.firstPersonRenderer = heldItemRenderer;
        this.mapRenderer = new MapRenderer(client.getTextureManager());
        this.lightmapTextureManager = new LightmapTextureManager(this, client);
        this.buffers = buffers;
        this.postProcessor = null;
    }

    @Override
    public void close() {
        this.lightmapTextureManager.close();
        this.mapRenderer.close();
        this.overlayTexture.close();
        this.disablePostProcessor();
        this.clearPrograms();
        if (this.blitScreenProgram != null) {
            this.blitScreenProgram.close();
        }
    }

    public void setRenderHand(boolean renderHand) {
        this.renderHand = renderHand;
    }

    public void setBlockOutlineEnabled(boolean blockOutlineEnabled) {
        this.blockOutlineEnabled = blockOutlineEnabled;
    }

    public void setRenderingPanorama(boolean renderingPanorama) {
        this.renderingPanorama = renderingPanorama;
    }

    public boolean isRenderingPanorama() {
        return this.renderingPanorama;
    }

    public void disablePostProcessor() {
        if (this.postProcessor != null) {
            this.postProcessor.close();
        }
        this.postProcessor = null;
        this.superSecretSettingIndex = SUPER_SECRET_SETTING_COUNT;
    }

    public void togglePostProcessorEnabled() {
        this.postProcessorEnabled = !this.postProcessorEnabled;
    }

    public void onCameraEntitySet(@Nullable Entity entity) {
        if (this.postProcessor != null) {
            this.postProcessor.close();
        }
        this.postProcessor = null;
        if (entity instanceof CreeperEntity) {
            this.loadPostProcessor(new Identifier("shaders/post/creeper.json"));
        } else if (entity instanceof SpiderEntity) {
            this.loadPostProcessor(new Identifier("shaders/post/spider.json"));
        } else if (entity instanceof EndermanEntity) {
            this.loadPostProcessor(new Identifier("shaders/post/invert.json"));
        }
    }

    public void cycleSuperSecretSetting() {
        if (!(this.client.getCameraEntity() instanceof PlayerEntity)) {
            return;
        }
        if (this.postProcessor != null) {
            this.postProcessor.close();
        }
        this.superSecretSettingIndex = (this.superSecretSettingIndex + 1) % (SUPER_SECRET_SETTING_PROGRAMS.length + 1);
        if (this.superSecretSettingIndex == SUPER_SECRET_SETTING_COUNT) {
            this.postProcessor = null;
        } else {
            this.loadPostProcessor(SUPER_SECRET_SETTING_PROGRAMS[this.superSecretSettingIndex]);
        }
    }

    void loadPostProcessor(Identifier id) {
        if (this.postProcessor != null) {
            this.postProcessor.close();
        }
        try {
            this.postProcessor = new PostEffectProcessor(this.client.getTextureManager(), this.resourceManager, this.client.getFramebuffer(), id);
            this.postProcessor.setupDimensions(this.client.getWindow().getFramebufferWidth(), this.client.getWindow().getFramebufferHeight());
            this.postProcessorEnabled = true;
        } catch (IOException iOException) {
            LOGGER.warn("Failed to load shader: {}", (Object)id, (Object)iOException);
            this.superSecretSettingIndex = SUPER_SECRET_SETTING_COUNT;
            this.postProcessorEnabled = false;
        } catch (JsonSyntaxException jsonSyntaxException) {
            LOGGER.warn("Failed to parse shader: {}", (Object)id, (Object)jsonSyntaxException);
            this.superSecretSettingIndex = SUPER_SECRET_SETTING_COUNT;
            this.postProcessorEnabled = false;
        }
    }

    public ResourceReloader createProgramReloader() {
        return new SinglePreparationResourceReloader<CachedResourceFactory>(){

            @Override
            protected CachedResourceFactory prepare(ResourceManager resourceManager, Profiler profiler) {
                Map<Identifier, Resource> map = resourceManager.findResources("shaders", id -> {
                    String string = id.getPath();
                    return string.endsWith(".json") || string.endsWith(ShaderStage.Type.FRAGMENT.getFileExtension()) || string.endsWith(ShaderStage.Type.VERTEX.getFileExtension()) || string.endsWith(".glsl");
                });
                HashMap<Identifier, Resource> map2 = new HashMap<Identifier, Resource>();
                map.forEach((id, resource) -> {
                    try (InputStream inputStream = resource.getInputStream();){
                        byte[] bs = inputStream.readAllBytes();
                        map2.put((Identifier)id, new Resource(resource.getPack(), () -> new ByteArrayInputStream(bs)));
                    } catch (Exception exception) {
                        LOGGER.warn("Failed to read resource {}", id, (Object)exception);
                    }
                });
                return new CachedResourceFactory(resourceManager, map2);
            }

            @Override
            protected void apply(CachedResourceFactory cachedResourceFactory, ResourceManager resourceManager, Profiler profiler) {
                GameRenderer.this.loadPrograms(cachedResourceFactory);
                if (GameRenderer.this.postProcessor != null) {
                    GameRenderer.this.postProcessor.close();
                }
                GameRenderer.this.postProcessor = null;
                if (GameRenderer.this.superSecretSettingIndex == SUPER_SECRET_SETTING_COUNT) {
                    GameRenderer.this.onCameraEntitySet(GameRenderer.this.client.getCameraEntity());
                } else {
                    GameRenderer.this.loadPostProcessor(SUPER_SECRET_SETTING_PROGRAMS[GameRenderer.this.superSecretSettingIndex]);
                }
            }

            @Override
            public String getName() {
                return "Shader Loader";
            }

            @Override
            protected /* synthetic */ Object prepare(ResourceManager manager, Profiler profiler) {
                return this.prepare(manager, profiler);
            }
        };
    }

    public void preloadPrograms(ResourceFactory factory) {
        if (this.blitScreenProgram != null) {
            throw new RuntimeException("Blit shader already preloaded");
        }
        try {
            this.blitScreenProgram = new ShaderProgram(factory, "blit_screen", VertexFormats.BLIT_SCREEN);
        } catch (IOException iOException) {
            throw new RuntimeException("could not preload blit shader", iOException);
        }
        positionProgram = this.preloadProgram(factory, "position", VertexFormats.POSITION);
        positionColorProgram = this.preloadProgram(factory, "position_color", VertexFormats.POSITION_COLOR);
        positionColorTexProgram = this.preloadProgram(factory, "position_color_tex", VertexFormats.POSITION_COLOR_TEXTURE);
        positionTexProgram = this.preloadProgram(factory, "position_tex", VertexFormats.POSITION_TEXTURE);
        positionTexColorProgram = this.preloadProgram(factory, "position_tex_color", VertexFormats.POSITION_TEXTURE_COLOR);
        renderTypeTextProgram = this.preloadProgram(factory, "rendertype_text", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT);
    }

    private ShaderProgram preloadProgram(ResourceFactory factory, String name, VertexFormat format) {
        try {
            ShaderProgram shaderProgram = new ShaderProgram(factory, name, format);
            this.programs.put(name, shaderProgram);
            return shaderProgram;
        } catch (Exception exception) {
            throw new IllegalStateException("could not preload shader " + name, exception);
        }
    }

    void loadPrograms(ResourceFactory factory) {
        RenderSystem.assertOnRenderThread();
        ArrayList<ShaderStage> list = Lists.newArrayList();
        list.addAll(ShaderStage.Type.FRAGMENT.getLoadedShaders().values());
        list.addAll(ShaderStage.Type.VERTEX.getLoadedShaders().values());
        list.forEach(ShaderStage::release);
        ArrayList<Pair<ShaderProgram, Consumer<ShaderProgram>>> list2 = Lists.newArrayListWithCapacity(this.programs.size());
        try {
            list2.add(Pair.of(new ShaderProgram(factory, "block", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL), program -> {
                blockProgram = program;
            }));
            list2.add(Pair.of(new ShaderProgram(factory, "new_entity", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL), program -> {
                newEntityProgram = program;
            }));
            list2.add(Pair.of(new ShaderProgram(factory, "particle", VertexFormats.POSITION_TEXTURE_COLOR_LIGHT), program -> {
                particleProgram = program;
            }));
            list2.add(Pair.of(new ShaderProgram(factory, "position", VertexFormats.POSITION), program -> {
                positionProgram = program;
            }));
            list2.add(Pair.of(new ShaderProgram(factory, "position_color", VertexFormats.POSITION_COLOR), program -> {
                positionColorProgram = program;
            }));
            list2.add(Pair.of(new ShaderProgram(factory, "position_color_lightmap", VertexFormats.POSITION_COLOR_LIGHT), program -> {
                positionColorLightmapProgram = program;
            }));
            list2.add(Pair.of(new ShaderProgram(factory, "position_color_tex", VertexFormats.POSITION_COLOR_TEXTURE), program -> {
                positionColorTexProgram = program;
            }));
            list2.add(Pair.of(new ShaderProgram(factory, "position_color_tex_lightmap", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT), program -> {
                positionColorTexLightmapProgram = program;
            }));
            list2.add(Pair.of(new ShaderProgram(factory, "position_tex", VertexFormats.POSITION_TEXTURE), program -> {
                positionTexProgram = program;
            }));
            list2.add(Pair.of(new ShaderProgram(factory, "position_tex_color", VertexFormats.POSITION_TEXTURE_COLOR), program -> {
                positionTexColorProgram = program;
            }));
            list2.add(Pair.of(new ShaderProgram(factory, "position_tex_color_normal", VertexFormats.POSITION_TEXTURE_COLOR_NORMAL), program -> {
                positionTexColorNormalProgram = program;
            }));
            list2.add(Pair.of(new ShaderProgram(factory, "position_tex_lightmap_color", VertexFormats.POSITION_TEXTURE_LIGHT_COLOR), program -> {
                positionTexLightmapColorProgram = program;
            }));
            list2.add(Pair.of(new ShaderProgram(factory, "rendertype_solid", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL), program -> {
                renderTypeSolidProgram = program;
            }));
            list2.add(Pair.of(new ShaderProgram(factory, "rendertype_cutout_mipped", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL), program -> {
                renderTypeCutoutMippedProgram = program;
            }));
            list2.add(Pair.of(new ShaderProgram(factory, "rendertype_cutout", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL), program -> {
                renderTypeCutoutProgram = program;
            }));
            list2.add(Pair.of(new ShaderProgram(factory, "rendertype_translucent", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL), program -> {
                renderTypeTranslucentProgram = program;
            }));
            list2.add(Pair.of(new ShaderProgram(factory, "rendertype_translucent_moving_block", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL), program -> {
                renderTypeTranslucentMovingBlockProgram = program;
            }));
            list2.add(Pair.of(new ShaderProgram(factory, "rendertype_translucent_no_crumbling", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL), program -> {
                renderTypeTranslucentNoCrumblingProgram = program;
            }));
            list2.add(Pair.of(new ShaderProgram(factory, "rendertype_armor_cutout_no_cull", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL), program -> {
                renderTypeArmorCutoutNoCullProgram = program;
            }));
            list2.add(Pair.of(new ShaderProgram(factory, "rendertype_entity_solid", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL), program -> {
                renderTypeEntitySolidProgram = program;
            }));
            list2.add(Pair.of(new ShaderProgram(factory, "rendertype_entity_cutout", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL), program -> {
                renderTypeEntityCutoutProgram = program;
            }));
            list2.add(Pair.of(new ShaderProgram(factory, "rendertype_entity_cutout_no_cull", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL), program -> {
                renderTypeEntityCutoutNoNullProgram = program;
            }));
            list2.add(Pair.of(new ShaderProgram(factory, "rendertype_entity_cutout_no_cull_z_offset", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL), program -> {
                renderTypeEntityCutoutNoNullZOffsetProgram = program;
            }));
            list2.add(Pair.of(new ShaderProgram(factory, "rendertype_item_entity_translucent_cull", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL), program -> {
                renderTypeItemEntityTranslucentCullProgram = program;
            }));
            list2.add(Pair.of(new ShaderProgram(factory, "rendertype_entity_translucent_cull", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL), program -> {
                renderTypeEntityTranslucentCullProgram = program;
            }));
            list2.add(Pair.of(new ShaderProgram(factory, "rendertype_entity_translucent", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL), program -> {
                renderTypeEntityTranslucentProgram = program;
            }));
            list2.add(Pair.of(new ShaderProgram(factory, "rendertype_entity_translucent_emissive", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL), program -> {
                renderTypeEntityTranslucentEmissiveProgram = program;
            }));
            list2.add(Pair.of(new ShaderProgram(factory, "rendertype_entity_smooth_cutout", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL), program -> {
                renderTypeEntitySmoothCutoutProgram = program;
            }));
            list2.add(Pair.of(new ShaderProgram(factory, "rendertype_beacon_beam", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL), program -> {
                renderTypeBeaconBeamProgram = program;
            }));
            list2.add(Pair.of(new ShaderProgram(factory, "rendertype_entity_decal", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL), program -> {
                renderTypeEntityDecalProgram = program;
            }));
            list2.add(Pair.of(new ShaderProgram(factory, "rendertype_entity_no_outline", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL), program -> {
                renderTypeEntityNoOutlineProgram = program;
            }));
            list2.add(Pair.of(new ShaderProgram(factory, "rendertype_entity_shadow", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL), program -> {
                renderTypeEntityShadowProgram = program;
            }));
            list2.add(Pair.of(new ShaderProgram(factory, "rendertype_entity_alpha", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL), program -> {
                renderTypeEntityAlphaProgram = program;
            }));
            list2.add(Pair.of(new ShaderProgram(factory, "rendertype_eyes", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL), program -> {
                renderTypeEyesProgram = program;
            }));
            list2.add(Pair.of(new ShaderProgram(factory, "rendertype_energy_swirl", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL), program -> {
                renderTypeEnergySwirlProgram = program;
            }));
            list2.add(Pair.of(new ShaderProgram(factory, "rendertype_leash", VertexFormats.POSITION_COLOR_LIGHT), program -> {
                renderTypeLeashProgram = program;
            }));
            list2.add(Pair.of(new ShaderProgram(factory, "rendertype_water_mask", VertexFormats.POSITION), program -> {
                renderTypeWaterMaskProgram = program;
            }));
            list2.add(Pair.of(new ShaderProgram(factory, "rendertype_outline", VertexFormats.POSITION_COLOR_TEXTURE), program -> {
                renderTypeOutlineProgram = program;
            }));
            list2.add(Pair.of(new ShaderProgram(factory, "rendertype_armor_glint", VertexFormats.POSITION_TEXTURE), program -> {
                renderTypeArmorGlintProgram = program;
            }));
            list2.add(Pair.of(new ShaderProgram(factory, "rendertype_armor_entity_glint", VertexFormats.POSITION_TEXTURE), program -> {
                renderTypeArmorEntityGlintProgram = program;
            }));
            list2.add(Pair.of(new ShaderProgram(factory, "rendertype_glint_translucent", VertexFormats.POSITION_TEXTURE), program -> {
                renderTypeGlintTranslucentProgram = program;
            }));
            list2.add(Pair.of(new ShaderProgram(factory, "rendertype_glint", VertexFormats.POSITION_TEXTURE), program -> {
                renderTypeGlintProgram = program;
            }));
            list2.add(Pair.of(new ShaderProgram(factory, "rendertype_glint_direct", VertexFormats.POSITION_TEXTURE), program -> {
                renderTypeGlintDirectProgram = program;
            }));
            list2.add(Pair.of(new ShaderProgram(factory, "rendertype_entity_glint", VertexFormats.POSITION_TEXTURE), program -> {
                renderTypeEntityGlintProgram = program;
            }));
            list2.add(Pair.of(new ShaderProgram(factory, "rendertype_entity_glint_direct", VertexFormats.POSITION_TEXTURE), program -> {
                renderTypeEntityGlintDirectProgram = program;
            }));
            list2.add(Pair.of(new ShaderProgram(factory, "rendertype_text", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT), program -> {
                renderTypeTextProgram = program;
            }));
            list2.add(Pair.of(new ShaderProgram(factory, "rendertype_text_background", VertexFormats.POSITION_COLOR_LIGHT), program -> {
                renderTypeTextBackgroundProgram = program;
            }));
            list2.add(Pair.of(new ShaderProgram(factory, "rendertype_text_intensity", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT), program -> {
                renderTypeTextIntensityProgram = program;
            }));
            list2.add(Pair.of(new ShaderProgram(factory, "rendertype_text_see_through", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT), program -> {
                renderTypeTextSeeThroughProgram = program;
            }));
            list2.add(Pair.of(new ShaderProgram(factory, "rendertype_text_background_see_through", VertexFormats.POSITION_COLOR_LIGHT), program -> {
                renderTypeTextBackgroundSeeThroughProgram = program;
            }));
            list2.add(Pair.of(new ShaderProgram(factory, "rendertype_text_intensity_see_through", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT), program -> {
                renderTypeTextIntensitySeeThroughProgram = program;
            }));
            list2.add(Pair.of(new ShaderProgram(factory, "rendertype_lightning", VertexFormats.POSITION_COLOR), program -> {
                renderTypeLightningProgram = program;
            }));
            list2.add(Pair.of(new ShaderProgram(factory, "rendertype_tripwire", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL), program -> {
                renderTypeTripwireProgram = program;
            }));
            list2.add(Pair.of(new ShaderProgram(factory, "rendertype_end_portal", VertexFormats.POSITION), program -> {
                renderTypeEndPortalProgram = program;
            }));
            list2.add(Pair.of(new ShaderProgram(factory, "rendertype_end_gateway", VertexFormats.POSITION), program -> {
                renderTypeEndGatewayProgram = program;
            }));
            list2.add(Pair.of(new ShaderProgram(factory, "rendertype_lines", VertexFormats.LINES), program -> {
                renderTypeLinesProgram = program;
            }));
            list2.add(Pair.of(new ShaderProgram(factory, "rendertype_crumbling", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL), program -> {
                renderTypeCrumblingProgram = program;
            }));
        } catch (IOException iOException) {
            list2.forEach(pair -> ((ShaderProgram)pair.getFirst()).close());
            throw new RuntimeException("could not reload shaders", iOException);
        }
        this.clearPrograms();
        list2.forEach(pair -> {
            ShaderProgram shaderProgram = (ShaderProgram)pair.getFirst();
            this.programs.put(shaderProgram.getName(), shaderProgram);
            ((Consumer)pair.getSecond()).accept(shaderProgram);
        });
    }

    private void clearPrograms() {
        RenderSystem.assertOnRenderThread();
        this.programs.values().forEach(ShaderProgram::close);
        this.programs.clear();
    }

    @Nullable
    public ShaderProgram getProgram(@Nullable String name) {
        if (name == null) {
            return null;
        }
        return this.programs.get(name);
    }

    public void tick() {
        this.updateFovMultiplier();
        this.lightmapTextureManager.tick();
        if (this.client.getCameraEntity() == null) {
            this.client.setCameraEntity(this.client.player);
        }
        this.camera.updateEyeHeight();
        ++this.ticks;
        this.firstPersonRenderer.updateHeldItems();
        this.client.worldRenderer.tickRainSplashing(this.camera);
        this.lastSkyDarkness = this.skyDarkness;
        if (this.client.inGameHud.getBossBarHud().shouldDarkenSky()) {
            this.skyDarkness += 0.05f;
            if (this.skyDarkness > 1.0f) {
                this.skyDarkness = 1.0f;
            }
        } else if (this.skyDarkness > 0.0f) {
            this.skyDarkness -= 0.0125f;
        }
        if (this.floatingItemTimeLeft > 0) {
            --this.floatingItemTimeLeft;
            if (this.floatingItemTimeLeft == 0) {
                this.floatingItem = null;
            }
        }
    }

    @Nullable
    public PostEffectProcessor getPostProcessor() {
        return this.postProcessor;
    }

    public void onResized(int width, int height) {
        if (this.postProcessor != null) {
            this.postProcessor.setupDimensions(width, height);
        }
        this.client.worldRenderer.onResized(width, height);
    }

    public void updateTargetedEntity(float tickDelta) {
        Entity entity2 = this.client.getCameraEntity();
        if (entity2 == null) {
            return;
        }
        if (this.client.world == null) {
            return;
        }
        this.client.getProfiler().push("pick");
        this.client.targetedEntity = null;
        double d = this.client.interactionManager.getReachDistance();
        this.client.crosshairTarget = entity2.raycast(d, tickDelta, false);
        Vec3d vec3d = entity2.getCameraPosVec(tickDelta);
        boolean bl = false;
        int i = 3;
        double e = d;
        if (this.client.interactionManager.hasExtendedReach()) {
            d = e = 6.0;
        } else {
            if (e > 3.0) {
                bl = true;
            }
            d = e;
        }
        e *= e;
        if (this.client.crosshairTarget != null) {
            e = this.client.crosshairTarget.getPos().squaredDistanceTo(vec3d);
        }
        Vec3d vec3d2 = entity2.getRotationVec(1.0f);
        Vec3d vec3d3 = vec3d.add(vec3d2.x * d, vec3d2.y * d, vec3d2.z * d);
        float f = 1.0f;
        Box box = entity2.getBoundingBox().stretch(vec3d2.multiply(d)).expand(1.0, 1.0, 1.0);
        EntityHitResult entityHitResult = ProjectileUtil.raycast(entity2, vec3d, vec3d3, box, entity -> !entity.isSpectator() && entity.canHit(), e);
        if (entityHitResult != null) {
            Entity entity22 = entityHitResult.getEntity();
            Vec3d vec3d4 = entityHitResult.getPos();
            double g = vec3d.squaredDistanceTo(vec3d4);
            if (bl && g > 9.0) {
                this.client.crosshairTarget = BlockHitResult.createMissed(vec3d4, Direction.getFacing(vec3d2.x, vec3d2.y, vec3d2.z), BlockPos.ofFloored(vec3d4));
            } else if (g < e || this.client.crosshairTarget == null) {
                this.client.crosshairTarget = entityHitResult;
                if (entity22 instanceof LivingEntity || entity22 instanceof ItemFrameEntity) {
                    this.client.targetedEntity = entity22;
                }
            }
        }
        this.client.getProfiler().pop();
    }

    private void updateFovMultiplier() {
        float f = 1.0f;
        if (this.client.getCameraEntity() instanceof AbstractClientPlayerEntity) {
            AbstractClientPlayerEntity abstractClientPlayerEntity = (AbstractClientPlayerEntity)this.client.getCameraEntity();
            f = abstractClientPlayerEntity.getFovMultiplier();
        }
        this.lastFovMultiplier = this.fovMultiplier;
        this.fovMultiplier += (f - this.fovMultiplier) * 0.5f;
        if (this.fovMultiplier > 1.5f) {
            this.fovMultiplier = 1.5f;
        }
        if (this.fovMultiplier < 0.1f) {
            this.fovMultiplier = 0.1f;
        }
    }

    private double getFov(Camera camera, float tickDelta, boolean changingFov) {
        CameraSubmersionType cameraSubmersionType;
        if (this.renderingPanorama) {
            return 90.0;
        }
        double d = 70.0;
        if (changingFov) {
            d = this.client.options.getFov().getValue().intValue();
            d *= (double)MathHelper.lerp(tickDelta, this.lastFovMultiplier, this.fovMultiplier);
        }
        if (camera.getFocusedEntity() instanceof LivingEntity && ((LivingEntity)camera.getFocusedEntity()).isDead()) {
            float f = Math.min((float)((LivingEntity)camera.getFocusedEntity()).deathTime + tickDelta, 20.0f);
            d /= (double)((1.0f - 500.0f / (f + 500.0f)) * 2.0f + 1.0f);
        }
        if ((cameraSubmersionType = camera.getSubmersionType()) == CameraSubmersionType.LAVA || cameraSubmersionType == CameraSubmersionType.WATER) {
            d *= MathHelper.lerp(this.client.options.getFovEffectScale().getValue(), 1.0, 0.8571428656578064);
        }
        return d;
    }

    private void tiltViewWhenHurt(MatrixStack matrices, float tickDelta) {
        if (this.client.getCameraEntity() instanceof LivingEntity) {
            float g;
            LivingEntity livingEntity = (LivingEntity)this.client.getCameraEntity();
            float f = (float)livingEntity.hurtTime - tickDelta;
            if (livingEntity.isDead()) {
                g = Math.min((float)livingEntity.deathTime + tickDelta, 20.0f);
                matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(40.0f - 8000.0f / (g + 200.0f)));
            }
            if (f < 0.0f) {
                return;
            }
            f /= (float)livingEntity.maxHurtTime;
            f = MathHelper.sin(f * f * f * f * (float)Math.PI);
            g = livingEntity.getDamageTiltYaw();
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-g));
            float h = (float)((double)(-f) * 14.0 * this.client.options.getDamageTiltStrength().getValue());
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(h));
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(g));
        }
    }

    private void bobView(MatrixStack matrices, float tickDelta) {
        if (!(this.client.getCameraEntity() instanceof PlayerEntity)) {
            return;
        }
        PlayerEntity playerEntity = (PlayerEntity)this.client.getCameraEntity();
        float f = playerEntity.horizontalSpeed - playerEntity.prevHorizontalSpeed;
        float g = -(playerEntity.horizontalSpeed + f * tickDelta);
        float h = MathHelper.lerp(tickDelta, playerEntity.prevStrideDistance, playerEntity.strideDistance);
        matrices.translate(MathHelper.sin(g * (float)Math.PI) * h * 0.5f, -Math.abs(MathHelper.cos(g * (float)Math.PI) * h), 0.0f);
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(MathHelper.sin(g * (float)Math.PI) * h * 3.0f));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(Math.abs(MathHelper.cos(g * (float)Math.PI - 0.2f) * h) * 5.0f));
    }

    public void renderWithZoom(float zoom, float zoomX, float zoomY) {
        this.zoom = zoom;
        this.zoomX = zoomX;
        this.zoomY = zoomY;
        this.setBlockOutlineEnabled(false);
        this.setRenderHand(false);
        this.renderWorld(1.0f, 0L, new MatrixStack());
        this.zoom = 1.0f;
    }

    private void renderHand(MatrixStack matrices, Camera camera, float tickDelta) {
        boolean bl;
        if (this.renderingPanorama) {
            return;
        }
        this.loadProjectionMatrix(this.getBasicProjectionMatrix(this.getFov(camera, tickDelta, false)));
        matrices.loadIdentity();
        matrices.push();
        this.tiltViewWhenHurt(matrices, tickDelta);
        if (this.client.options.getBobView().getValue().booleanValue()) {
            this.bobView(matrices, tickDelta);
        }
        boolean bl2 = bl = this.client.getCameraEntity() instanceof LivingEntity && ((LivingEntity)this.client.getCameraEntity()).isSleeping();
        if (this.client.options.getPerspective().isFirstPerson() && !bl && !this.client.options.hudHidden && this.client.interactionManager.getCurrentGameMode() != GameMode.SPECTATOR) {
            this.lightmapTextureManager.enable();
            this.firstPersonRenderer.renderItem(tickDelta, matrices, this.buffers.getEntityVertexConsumers(), this.client.player, this.client.getEntityRenderDispatcher().getLight(this.client.player, tickDelta));
            this.lightmapTextureManager.disable();
        }
        matrices.pop();
        if (this.client.options.getPerspective().isFirstPerson() && !bl) {
            InGameOverlayRenderer.renderOverlays(this.client, matrices);
            this.tiltViewWhenHurt(matrices, tickDelta);
        }
        if (this.client.options.getBobView().getValue().booleanValue()) {
            this.bobView(matrices, tickDelta);
        }
    }

    public void loadProjectionMatrix(Matrix4f projectionMatrix) {
        RenderSystem.setProjectionMatrix(projectionMatrix);
    }

    public Matrix4f getBasicProjectionMatrix(double fov) {
        MatrixStack matrixStack = new MatrixStack();
        matrixStack.peek().getPositionMatrix().identity();
        if (this.zoom != 1.0f) {
            matrixStack.translate(this.zoomX, -this.zoomY, 0.0f);
            matrixStack.scale(this.zoom, this.zoom, 1.0f);
        }
        matrixStack.peek().getPositionMatrix().mul(new Matrix4f().setPerspective((float)(fov * 0.01745329238474369), (float)this.client.getWindow().getFramebufferWidth() / (float)this.client.getWindow().getFramebufferHeight(), 0.05f, this.method_32796()));
        return matrixStack.peek().getPositionMatrix();
    }

    public float method_32796() {
        return this.viewDistance * 4.0f;
    }

    public static float getNightVisionStrength(LivingEntity entity, float tickDelta) {
        StatusEffectInstance statusEffectInstance = entity.getStatusEffect(StatusEffects.NIGHT_VISION);
        if (!statusEffectInstance.isDurationBelow(200)) {
            return 1.0f;
        }
        return 0.7f + MathHelper.sin(((float)statusEffectInstance.getDuration() - tickDelta) * (float)Math.PI * 0.2f) * 0.3f;
    }

    public void render(float tickDelta, long startTime, boolean tick) {
        if (this.client.isWindowFocused() || !this.client.options.pauseOnLostFocus || this.client.options.getTouchscreen().getValue().booleanValue() && this.client.mouse.wasRightButtonClicked()) {
            this.lastWindowFocusedTime = Util.getMeasuringTimeMs();
        } else if (Util.getMeasuringTimeMs() - this.lastWindowFocusedTime > 500L) {
            this.client.openPauseMenu(false);
        }
        if (this.client.skipGameRender) {
            return;
        }
        int i = (int)(this.client.mouse.getX() * (double)this.client.getWindow().getScaledWidth() / (double)this.client.getWindow().getWidth());
        int j = (int)(this.client.mouse.getY() * (double)this.client.getWindow().getScaledHeight() / (double)this.client.getWindow().getHeight());
        RenderSystem.viewport(0, 0, this.client.getWindow().getFramebufferWidth(), this.client.getWindow().getFramebufferHeight());
        if (tick && this.client.world != null) {
            this.client.getProfiler().push("level");
            this.renderWorld(tickDelta, startTime, new MatrixStack());
            this.updateWorldIcon();
            this.client.worldRenderer.drawEntityOutlinesFramebuffer();
            if (this.postProcessor != null && this.postProcessorEnabled) {
                RenderSystem.disableBlend();
                RenderSystem.disableDepthTest();
                RenderSystem.resetTextureMatrix();
                this.postProcessor.render(tickDelta);
            }
            this.client.getFramebuffer().beginWrite(true);
        }
        Window window = this.client.getWindow();
        RenderSystem.clear(GlConst.GL_DEPTH_BUFFER_BIT, MinecraftClient.IS_SYSTEM_MAC);
        Matrix4f matrix4f = new Matrix4f().setOrtho(0.0f, (float)((double)window.getFramebufferWidth() / window.getScaleFactor()), (float)((double)window.getFramebufferHeight() / window.getScaleFactor()), 0.0f, 1000.0f, 3000.0f);
        RenderSystem.setProjectionMatrix(matrix4f);
        MatrixStack matrixStack = RenderSystem.getModelViewStack();
        matrixStack.push();
        matrixStack.loadIdentity();
        matrixStack.translate(0.0f, 0.0f, -2000.0f);
        RenderSystem.applyModelViewMatrix();
        DiffuseLighting.enableGuiDepthLighting();
        MatrixStack matrixStack2 = new MatrixStack();
        if (tick && this.client.world != null) {
            this.client.getProfiler().swap("gui");
            if (this.client.player != null) {
                float f = MathHelper.lerp(tickDelta, this.client.player.lastNauseaStrength, this.client.player.nextNauseaStrength);
                float g = this.client.options.getDistortionEffectScale().getValue().floatValue();
                if (f > 0.0f && this.client.player.hasStatusEffect(StatusEffects.NAUSEA) && g < 1.0f) {
                    this.renderNausea(f * (1.0f - g));
                }
            }
            if (!this.client.options.hudHidden || this.client.currentScreen != null) {
                this.renderFloatingItem(this.client.getWindow().getScaledWidth(), this.client.getWindow().getScaledHeight(), tickDelta);
                this.client.inGameHud.render(matrixStack2, tickDelta);
                RenderSystem.clear(GlConst.GL_DEPTH_BUFFER_BIT, MinecraftClient.IS_SYSTEM_MAC);
            }
            this.client.getProfiler().pop();
        }
        if (this.client.getOverlay() != null) {
            try {
                this.client.getOverlay().render(matrixStack2, i, j, this.client.getLastFrameDuration());
            } catch (Throwable throwable) {
                CrashReport crashReport = CrashReport.create(throwable, "Rendering overlay");
                CrashReportSection crashReportSection = crashReport.addElement("Overlay render details");
                crashReportSection.add("Overlay name", () -> this.client.getOverlay().getClass().getCanonicalName());
                throw new CrashException(crashReport);
            }
        }
        if (this.client.currentScreen != null) {
            try {
                this.client.currentScreen.renderWithTooltip(matrixStack2, i, j, this.client.getLastFrameDuration());
            } catch (Throwable throwable) {
                CrashReport crashReport = CrashReport.create(throwable, "Rendering screen");
                CrashReportSection crashReportSection = crashReport.addElement("Screen render details");
                crashReportSection.add("Screen name", () -> this.client.currentScreen.getClass().getCanonicalName());
                crashReportSection.add("Mouse location", () -> String.format(Locale.ROOT, "Scaled: (%d, %d). Absolute: (%f, %f)", i, j, this.client.mouse.getX(), this.client.mouse.getY()));
                crashReportSection.add("Screen size", () -> String.format(Locale.ROOT, "Scaled: (%d, %d). Absolute: (%d, %d). Scale factor of %f", this.client.getWindow().getScaledWidth(), this.client.getWindow().getScaledHeight(), this.client.getWindow().getFramebufferWidth(), this.client.getWindow().getFramebufferHeight(), this.client.getWindow().getScaleFactor()));
                throw new CrashException(crashReport);
            }
            try {
                if (this.client.currentScreen != null) {
                    this.client.currentScreen.updateNarrator();
                }
            } catch (Throwable throwable) {
                CrashReport crashReport = CrashReport.create(throwable, "Narrating screen");
                CrashReportSection crashReportSection = crashReport.addElement("Screen details");
                crashReportSection.add("Screen name", () -> this.client.currentScreen.getClass().getCanonicalName());
                throw new CrashException(crashReport);
            }
        }
        this.client.getProfiler().push("toasts");
        this.client.getToastManager().draw(matrixStack2);
        this.client.getProfiler().pop();
        matrixStack.pop();
        RenderSystem.applyModelViewMatrix();
    }

    private void updateWorldIcon() {
        if (this.hasWorldIcon || !this.client.isInSingleplayer()) {
            return;
        }
        long l = Util.getMeasuringTimeMs();
        if (l - this.lastWorldIconUpdate < 1000L) {
            return;
        }
        this.lastWorldIconUpdate = l;
        IntegratedServer integratedServer = this.client.getServer();
        if (integratedServer == null || integratedServer.isStopped()) {
            return;
        }
        integratedServer.getIconFile().ifPresent(path -> {
            if (Files.isRegularFile(path, new LinkOption[0])) {
                this.hasWorldIcon = true;
            } else {
                this.updateWorldIcon((Path)path);
            }
        });
    }

    private void updateWorldIcon(Path path) {
        if (this.client.worldRenderer.getCompletedChunkCount() > 10 && this.client.worldRenderer.isTerrainRenderComplete()) {
            NativeImage nativeImage = ScreenshotRecorder.takeScreenshot(this.client.getFramebuffer());
            Util.getIoWorkerExecutor().execute(() -> {
                int i = nativeImage.getWidth();
                int j = nativeImage.getHeight();
                int k = 0;
                int l = 0;
                if (i > j) {
                    k = (i - j) / 2;
                    i = j;
                } else {
                    l = (j - i) / 2;
                    j = i;
                }
                try (NativeImage nativeImage2 = new NativeImage(64, 64, false);){
                    nativeImage.resizeSubRectTo(k, l, i, j, nativeImage2);
                    nativeImage2.writeTo(path);
                } catch (IOException iOException) {
                    LOGGER.warn("Couldn't save auto screenshot", iOException);
                } finally {
                    nativeImage.close();
                }
            });
        }
    }

    private boolean shouldRenderBlockOutline() {
        boolean bl;
        if (!this.blockOutlineEnabled) {
            return false;
        }
        Entity entity = this.client.getCameraEntity();
        boolean bl2 = bl = entity instanceof PlayerEntity && !this.client.options.hudHidden;
        if (bl && !((PlayerEntity)entity).getAbilities().allowModifyWorld) {
            ItemStack itemStack = ((LivingEntity)entity).getMainHandStack();
            HitResult hitResult = this.client.crosshairTarget;
            if (hitResult != null && hitResult.getType() == HitResult.Type.BLOCK) {
                BlockPos blockPos = ((BlockHitResult)hitResult).getBlockPos();
                BlockState blockState = this.client.world.getBlockState(blockPos);
                if (this.client.interactionManager.getCurrentGameMode() == GameMode.SPECTATOR) {
                    bl = blockState.createScreenHandlerFactory(this.client.world, blockPos) != null;
                } else {
                    CachedBlockPosition cachedBlockPosition = new CachedBlockPosition(this.client.world, blockPos, false);
                    Registry<Block> registry = this.client.world.getRegistryManager().get(RegistryKeys.BLOCK);
                    bl = !itemStack.isEmpty() && (itemStack.canDestroy(registry, cachedBlockPosition) || itemStack.canPlaceOn(registry, cachedBlockPosition));
                }
            }
        }
        return bl;
    }

    public void renderWorld(float tickDelta, long limitTime, MatrixStack matrices) {
        this.lightmapTextureManager.update(tickDelta);
        if (this.client.getCameraEntity() == null) {
            this.client.setCameraEntity(this.client.player);
        }
        this.updateTargetedEntity(tickDelta);
        this.client.getProfiler().push("center");
        boolean bl = this.shouldRenderBlockOutline();
        this.client.getProfiler().swap("camera");
        Camera camera = this.camera;
        this.viewDistance = this.client.options.getClampedViewDistance() * 16;
        MatrixStack matrixStack = new MatrixStack();
        double d = this.getFov(camera, tickDelta, true);
        matrixStack.multiplyPositionMatrix(this.getBasicProjectionMatrix(d));
        this.tiltViewWhenHurt(matrixStack, tickDelta);
        if (this.client.options.getBobView().getValue().booleanValue()) {
            this.bobView(matrixStack, tickDelta);
        }
        float f = this.client.options.getDistortionEffectScale().getValue().floatValue();
        float g = MathHelper.lerp(tickDelta, this.client.player.lastNauseaStrength, this.client.player.nextNauseaStrength) * (f * f);
        if (g > 0.0f) {
            int i = this.client.player.hasStatusEffect(StatusEffects.NAUSEA) ? 7 : 20;
            float h = 5.0f / (g * g + 5.0f) - g * 0.04f;
            h *= h;
            RotationAxis rotationAxis = RotationAxis.of(new Vector3f(0.0f, MathHelper.SQUARE_ROOT_OF_TWO / 2.0f, MathHelper.SQUARE_ROOT_OF_TWO / 2.0f));
            matrixStack.multiply(rotationAxis.rotationDegrees(((float)this.ticks + tickDelta) * (float)i));
            matrixStack.scale(1.0f / h, 1.0f, 1.0f);
            float j = -((float)this.ticks + tickDelta) * (float)i;
            matrixStack.multiply(rotationAxis.rotationDegrees(j));
        }
        Matrix4f matrix4f = matrixStack.peek().getPositionMatrix();
        this.loadProjectionMatrix(matrix4f);
        camera.update(this.client.world, this.client.getCameraEntity() == null ? this.client.player : this.client.getCameraEntity(), !this.client.options.getPerspective().isFirstPerson(), this.client.options.getPerspective().isFrontView(), tickDelta);
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(camera.getPitch()));
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(camera.getYaw() + 180.0f));
        Matrix3f matrix3f = new Matrix3f(matrices.peek().getNormalMatrix()).invert();
        RenderSystem.setInverseViewRotationMatrix(matrix3f);
        this.client.worldRenderer.setupFrustum(matrices, camera.getPos(), this.getBasicProjectionMatrix(Math.max(d, (double)this.client.options.getFov().getValue().intValue())));
        this.client.worldRenderer.render(matrices, tickDelta, limitTime, bl, camera, this, this.lightmapTextureManager, matrix4f);
        this.client.getProfiler().swap("hand");
        if (this.renderHand) {
            RenderSystem.clear(GlConst.GL_DEPTH_BUFFER_BIT, MinecraftClient.IS_SYSTEM_MAC);
            this.renderHand(matrices, camera, tickDelta);
        }
        this.client.getProfiler().pop();
    }

    public void reset() {
        this.floatingItem = null;
        this.mapRenderer.clearStateTextures();
        this.camera.reset();
        this.hasWorldIcon = false;
    }

    public MapRenderer getMapRenderer() {
        return this.mapRenderer;
    }

    public void showFloatingItem(ItemStack floatingItem) {
        this.floatingItem = floatingItem;
        this.floatingItemTimeLeft = 40;
        this.floatingItemWidth = this.random.nextFloat() * 2.0f - 1.0f;
        this.floatingItemHeight = this.random.nextFloat() * 2.0f - 1.0f;
    }

    private void renderFloatingItem(int scaledWidth, int scaledHeight, float tickDelta) {
        if (this.floatingItem == null || this.floatingItemTimeLeft <= 0) {
            return;
        }
        int i = 40 - this.floatingItemTimeLeft;
        float f = ((float)i + tickDelta) / 40.0f;
        float g = f * f;
        float h = f * g;
        float j = 10.25f * h * g - 24.95f * g * g + 25.5f * h - 13.8f * g + 4.0f * f;
        float k = j * (float)Math.PI;
        float l = this.floatingItemWidth * (float)(scaledWidth / 4);
        float m = this.floatingItemHeight * (float)(scaledHeight / 4);
        RenderSystem.enableDepthTest();
        RenderSystem.disableCull();
        MatrixStack matrixStack = new MatrixStack();
        matrixStack.push();
        matrixStack.translate((float)(scaledWidth / 2) + l * MathHelper.abs(MathHelper.sin(k * 2.0f)), (float)(scaledHeight / 2) + m * MathHelper.abs(MathHelper.sin(k * 2.0f)), -50.0f);
        float n = 50.0f + 175.0f * MathHelper.sin(k);
        matrixStack.scale(n, -n, n);
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(900.0f * MathHelper.abs(MathHelper.sin(k))));
        matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(6.0f * MathHelper.cos(f * 8.0f)));
        matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(6.0f * MathHelper.cos(f * 8.0f)));
        VertexConsumerProvider.Immediate immediate = this.buffers.getEntityVertexConsumers();
        this.client.getItemRenderer().renderItem(this.floatingItem, ModelTransformationMode.FIXED, 0xF000F0, OverlayTexture.DEFAULT_UV, matrixStack, immediate, this.client.world, 0);
        matrixStack.pop();
        immediate.draw();
        RenderSystem.enableCull();
        RenderSystem.disableDepthTest();
    }

    private void renderNausea(float distortionStrength) {
        int i = this.client.getWindow().getScaledWidth();
        int j = this.client.getWindow().getScaledHeight();
        double d = MathHelper.lerp((double)distortionStrength, 2.0, 1.0);
        float f = 0.2f * distortionStrength;
        float g = 0.4f * distortionStrength;
        float h = 0.2f * distortionStrength;
        double e = (double)i * d;
        double k = (double)j * d;
        double l = ((double)i - e) / 2.0;
        double m = ((double)j - k) / 2.0;
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ONE, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ONE);
        RenderSystem.setShaderColor(f, g, h, 1.0f);
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderTexture(0, NAUSEA_OVERLAY);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
        bufferBuilder.vertex(l, m + k, -90.0).texture(0.0f, 1.0f).next();
        bufferBuilder.vertex(l + e, m + k, -90.0).texture(1.0f, 1.0f).next();
        bufferBuilder.vertex(l + e, m, -90.0).texture(1.0f, 0.0f).next();
        bufferBuilder.vertex(l, m, -90.0).texture(0.0f, 0.0f).next();
        tessellator.draw();
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
    }

    public MinecraftClient getClient() {
        return this.client;
    }

    public float getSkyDarkness(float tickDelta) {
        return MathHelper.lerp(tickDelta, this.lastSkyDarkness, this.skyDarkness);
    }

    public float getViewDistance() {
        return this.viewDistance;
    }

    public Camera getCamera() {
        return this.camera;
    }

    public LightmapTextureManager getLightmapTextureManager() {
        return this.lightmapTextureManager;
    }

    public OverlayTexture getOverlayTexture() {
        return this.overlayTexture;
    }

    @Nullable
    public static ShaderProgram getPositionProgram() {
        return positionProgram;
    }

    @Nullable
    public static ShaderProgram getPositionColorProgram() {
        return positionColorProgram;
    }

    @Nullable
    public static ShaderProgram getPositionColorTexProgram() {
        return positionColorTexProgram;
    }

    @Nullable
    public static ShaderProgram getPositionTexProgram() {
        return positionTexProgram;
    }

    @Nullable
    public static ShaderProgram getPositionTexColorProgram() {
        return positionTexColorProgram;
    }

    @Nullable
    public static ShaderProgram getBlockProgram() {
        return blockProgram;
    }

    @Nullable
    public static ShaderProgram getNewEntityProgram() {
        return newEntityProgram;
    }

    @Nullable
    public static ShaderProgram getParticleProgram() {
        return particleProgram;
    }

    @Nullable
    public static ShaderProgram getPositionColorLightmapProgram() {
        return positionColorLightmapProgram;
    }

    @Nullable
    public static ShaderProgram getPositionColorTexLightmapProgram() {
        return positionColorTexLightmapProgram;
    }

    @Nullable
    public static ShaderProgram getPositionTexColorNormalProgram() {
        return positionTexColorNormalProgram;
    }

    @Nullable
    public static ShaderProgram getPositionTexLightmapColorProgram() {
        return positionTexLightmapColorProgram;
    }

    @Nullable
    public static ShaderProgram getRenderTypeSolidProgram() {
        return renderTypeSolidProgram;
    }

    @Nullable
    public static ShaderProgram getRenderTypeCutoutMippedProgram() {
        return renderTypeCutoutMippedProgram;
    }

    @Nullable
    public static ShaderProgram getRenderTypeCutoutProgram() {
        return renderTypeCutoutProgram;
    }

    @Nullable
    public static ShaderProgram getRenderTypeTranslucentProgram() {
        return renderTypeTranslucentProgram;
    }

    @Nullable
    public static ShaderProgram getRenderTypeTranslucentMovingBlockProgram() {
        return renderTypeTranslucentMovingBlockProgram;
    }

    @Nullable
    public static ShaderProgram getRenderTypeTranslucentNoCrumblingProgram() {
        return renderTypeTranslucentNoCrumblingProgram;
    }

    @Nullable
    public static ShaderProgram getRenderTypeArmorCutoutNoCullProgram() {
        return renderTypeArmorCutoutNoCullProgram;
    }

    @Nullable
    public static ShaderProgram getRenderTypeEntitySolidProgram() {
        return renderTypeEntitySolidProgram;
    }

    @Nullable
    public static ShaderProgram getRenderTypeEntityCutoutProgram() {
        return renderTypeEntityCutoutProgram;
    }

    @Nullable
    public static ShaderProgram getRenderTypeEntityCutoutNoNullProgram() {
        return renderTypeEntityCutoutNoNullProgram;
    }

    @Nullable
    public static ShaderProgram getRenderTypeEntityCutoutNoNullZOffsetProgram() {
        return renderTypeEntityCutoutNoNullZOffsetProgram;
    }

    @Nullable
    public static ShaderProgram getRenderTypeItemEntityTranslucentCullProgram() {
        return renderTypeItemEntityTranslucentCullProgram;
    }

    @Nullable
    public static ShaderProgram getRenderTypeEntityTranslucentCullProgram() {
        return renderTypeEntityTranslucentCullProgram;
    }

    @Nullable
    public static ShaderProgram getRenderTypeEntityTranslucentProgram() {
        return renderTypeEntityTranslucentProgram;
    }

    @Nullable
    public static ShaderProgram getRenderTypeEntityTranslucentEmissiveProgram() {
        return renderTypeEntityTranslucentEmissiveProgram;
    }

    @Nullable
    public static ShaderProgram getRenderTypeEntitySmoothCutoutProgram() {
        return renderTypeEntitySmoothCutoutProgram;
    }

    @Nullable
    public static ShaderProgram getRenderTypeBeaconBeamProgram() {
        return renderTypeBeaconBeamProgram;
    }

    @Nullable
    public static ShaderProgram getRenderTypeEntityDecalProgram() {
        return renderTypeEntityDecalProgram;
    }

    @Nullable
    public static ShaderProgram getRenderTypeEntityNoOutlineProgram() {
        return renderTypeEntityNoOutlineProgram;
    }

    @Nullable
    public static ShaderProgram getRenderTypeEntityShadowProgram() {
        return renderTypeEntityShadowProgram;
    }

    @Nullable
    public static ShaderProgram getRenderTypeEntityAlphaProgram() {
        return renderTypeEntityAlphaProgram;
    }

    @Nullable
    public static ShaderProgram getRenderTypeEyesProgram() {
        return renderTypeEyesProgram;
    }

    @Nullable
    public static ShaderProgram getRenderTypeEnergySwirlProgram() {
        return renderTypeEnergySwirlProgram;
    }

    @Nullable
    public static ShaderProgram getRenderTypeLeashProgram() {
        return renderTypeLeashProgram;
    }

    @Nullable
    public static ShaderProgram getRenderTypeWaterMaskProgram() {
        return renderTypeWaterMaskProgram;
    }

    @Nullable
    public static ShaderProgram getRenderTypeOutlineProgram() {
        return renderTypeOutlineProgram;
    }

    @Nullable
    public static ShaderProgram getRenderTypeArmorGlintProgram() {
        return renderTypeArmorGlintProgram;
    }

    @Nullable
    public static ShaderProgram getRenderTypeArmorEntityGlintProgram() {
        return renderTypeArmorEntityGlintProgram;
    }

    @Nullable
    public static ShaderProgram getRenderTypeGlintTranslucentProgram() {
        return renderTypeGlintTranslucentProgram;
    }

    @Nullable
    public static ShaderProgram getRenderTypeGlintProgram() {
        return renderTypeGlintProgram;
    }

    @Nullable
    public static ShaderProgram getRenderTypeGlintDirectProgram() {
        return renderTypeGlintDirectProgram;
    }

    @Nullable
    public static ShaderProgram getRenderTypeEntityGlintProgram() {
        return renderTypeEntityGlintProgram;
    }

    @Nullable
    public static ShaderProgram getRenderTypeEntityGlintDirectProgram() {
        return renderTypeEntityGlintDirectProgram;
    }

    @Nullable
    public static ShaderProgram getRenderTypeTextProgram() {
        return renderTypeTextProgram;
    }

    @Nullable
    public static ShaderProgram getRenderTypeTextBackgroundProgram() {
        return renderTypeTextBackgroundProgram;
    }

    @Nullable
    public static ShaderProgram getRenderTypeTextIntensityProgram() {
        return renderTypeTextIntensityProgram;
    }

    @Nullable
    public static ShaderProgram getRenderTypeTextSeeThroughProgram() {
        return renderTypeTextSeeThroughProgram;
    }

    @Nullable
    public static ShaderProgram getRenderTypeTextBackgroundSeeThroughProgram() {
        return renderTypeTextBackgroundSeeThroughProgram;
    }

    @Nullable
    public static ShaderProgram getRenderTypeTextIntensitySeeThroughProgram() {
        return renderTypeTextIntensitySeeThroughProgram;
    }

    @Nullable
    public static ShaderProgram getRenderTypeLightningProgram() {
        return renderTypeLightningProgram;
    }

    @Nullable
    public static ShaderProgram getRenderTypeTripwireProgram() {
        return renderTypeTripwireProgram;
    }

    @Nullable
    public static ShaderProgram getRenderTypeEndPortalProgram() {
        return renderTypeEndPortalProgram;
    }

    @Nullable
    public static ShaderProgram getRenderTypeEndGatewayProgram() {
        return renderTypeEndGatewayProgram;
    }

    /**
     * {@return the {@code rendertype_lines} shader program}
     * 
     * <p>This shader program draws a line by drawing a quad (two triangles
     * pushed together). Each line takes four vertices. The first vertex is
     * the line start. The second one is a duplicate of the first one. The
     * third one is the line end. The fourth one is a duplicate of the third
     * one.
     * 
     * <p>The user of this shader program should use {@link
     * VertexFormats#LINES} for the vertex format. The normal element is a
     * direction vector from the starting position to the ending position.
     * It's used to calculate in what directions the duplicated vertices
     * should be offset to achieve thick lines. All four vertices should
     * share the same value for the normal element.
     * 
     * <p>The width of the line can be set with {@link
     * com.mojang.blaze3d.systems.RenderSystem#lineWidth
     * RenderSystem#lineWidth}.
     */
    @Nullable
    public static ShaderProgram getRenderTypeLinesProgram() {
        return renderTypeLinesProgram;
    }

    @Nullable
    public static ShaderProgram getRenderTypeCrumblingProgram() {
        return renderTypeCrumblingProgram;
    }

    @Environment(value=EnvType.CLIENT)
    public record CachedResourceFactory(ResourceFactory original, Map<Identifier, Resource> cache) implements ResourceFactory
    {
        @Override
        public Optional<Resource> getResource(Identifier identifier) {
            Resource resource = this.cache.get(identifier);
            if (resource != null) {
                return Optional.of(resource);
            }
            return this.original.getResource(identifier);
        }
    }
}

