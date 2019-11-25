/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render;

import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.IOException;
import java.util.Locale;
import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.ShaderEffect;
import net.minecraft.client.gui.MapRenderer;
import net.minecraft.client.gui.hud.InGameOverlayRenderer;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.BufferBuilderStorage;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.FirstPersonRenderer;
import net.minecraft.client.render.GuiLighting;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.util.ScreenshotUtils;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ProjectileUtil;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.mob.SpiderEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.resource.ResourceImpl;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SynchronousResourceReloadListener;
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
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class GameRenderer
implements AutoCloseable,
SynchronousResourceReloadListener {
    private static final Logger LOGGER = LogManager.getLogger();
    private final MinecraftClient client;
    private final ResourceManager resourceContainer;
    private final Random random = new Random();
    private float viewDistance;
    public final FirstPersonRenderer firstPersonRenderer;
    private final MapRenderer mapRenderer;
    private final BufferBuilderStorage buffers;
    private int ticks;
    private float movementFovMultiplier;
    private float lastMovementFovMultiplier;
    private float skyDarkness;
    private float lastSkyDarkness;
    private boolean renderHand = true;
    private boolean blockOutlineEnabled = true;
    private long lastWorldIconUpdate;
    private long lastWindowFocusedTime = Util.getMeasuringTimeMs();
    private final LightmapTextureManager lightmapTextureManager;
    private final OverlayTexture overlayTexture = new OverlayTexture();
    private boolean renderingPanorama;
    private float zoom = 1.0f;
    private float zoomX;
    private float zoomY;
    @Nullable
    private ItemStack floatingItem;
    private int floatingItemTimeLeft;
    private float floatingItemWidth;
    private float floatingItemHeight;
    @Nullable
    private ShaderEffect shader;
    private static final Identifier[] SHADERS_LOCATIONS = new Identifier[]{new Identifier("shaders/post/notch.json"), new Identifier("shaders/post/fxaa.json"), new Identifier("shaders/post/art.json"), new Identifier("shaders/post/bumpy.json"), new Identifier("shaders/post/blobs2.json"), new Identifier("shaders/post/pencil.json"), new Identifier("shaders/post/color_convolve.json"), new Identifier("shaders/post/deconverge.json"), new Identifier("shaders/post/flip.json"), new Identifier("shaders/post/invert.json"), new Identifier("shaders/post/ntsc.json"), new Identifier("shaders/post/outline.json"), new Identifier("shaders/post/phosphor.json"), new Identifier("shaders/post/scan_pincushion.json"), new Identifier("shaders/post/sobel.json"), new Identifier("shaders/post/bits.json"), new Identifier("shaders/post/desaturate.json"), new Identifier("shaders/post/green.json"), new Identifier("shaders/post/blur.json"), new Identifier("shaders/post/wobble.json"), new Identifier("shaders/post/blobs.json"), new Identifier("shaders/post/antialias.json"), new Identifier("shaders/post/creeper.json"), new Identifier("shaders/post/spider.json")};
    public static final int SHADER_COUNT = SHADERS_LOCATIONS.length;
    private int forcedShaderIndex = SHADER_COUNT;
    private boolean shadersEnabled;
    private final Camera camera = new Camera();

    public GameRenderer(MinecraftClient minecraftClient, ResourceManager resourceManager, BufferBuilderStorage bufferBuilderStorage) {
        this.client = minecraftClient;
        this.resourceContainer = resourceManager;
        this.firstPersonRenderer = minecraftClient.getFirstPersonRenderer();
        this.mapRenderer = new MapRenderer(minecraftClient.getTextureManager());
        this.lightmapTextureManager = new LightmapTextureManager(this, minecraftClient);
        this.buffers = bufferBuilderStorage;
        this.shader = null;
    }

    @Override
    public void close() {
        this.lightmapTextureManager.close();
        this.mapRenderer.close();
        this.overlayTexture.close();
        this.disableShader();
    }

    public void disableShader() {
        if (this.shader != null) {
            this.shader.close();
        }
        this.shader = null;
        this.forcedShaderIndex = SHADER_COUNT;
    }

    public void toggleShadersEnabled() {
        this.shadersEnabled = !this.shadersEnabled;
    }

    public void onCameraEntitySet(@Nullable Entity entity) {
        if (this.shader != null) {
            this.shader.close();
        }
        this.shader = null;
        if (entity instanceof CreeperEntity) {
            this.loadShader(new Identifier("shaders/post/creeper.json"));
        } else if (entity instanceof SpiderEntity) {
            this.loadShader(new Identifier("shaders/post/spider.json"));
        } else if (entity instanceof EndermanEntity) {
            this.loadShader(new Identifier("shaders/post/invert.json"));
        }
    }

    private void loadShader(Identifier identifier) {
        if (this.shader != null) {
            this.shader.close();
        }
        try {
            this.shader = new ShaderEffect(this.client.getTextureManager(), this.resourceContainer, this.client.getFramebuffer(), identifier);
            this.shader.setupDimensions(this.client.getWindow().getFramebufferWidth(), this.client.getWindow().getFramebufferHeight());
            this.shadersEnabled = true;
        } catch (IOException iOException) {
            LOGGER.warn("Failed to load shader: {}", (Object)identifier, (Object)iOException);
            this.forcedShaderIndex = SHADER_COUNT;
            this.shadersEnabled = false;
        } catch (JsonSyntaxException jsonSyntaxException) {
            LOGGER.warn("Failed to load shader: {}", (Object)identifier, (Object)jsonSyntaxException);
            this.forcedShaderIndex = SHADER_COUNT;
            this.shadersEnabled = false;
        }
    }

    @Override
    public void apply(ResourceManager resourceManager) {
        if (this.shader != null) {
            this.shader.close();
        }
        this.shader = null;
        if (this.forcedShaderIndex == SHADER_COUNT) {
            this.onCameraEntitySet(this.client.getCameraEntity());
        } else {
            this.loadShader(SHADERS_LOCATIONS[this.forcedShaderIndex]);
        }
    }

    public void tick() {
        this.updateMovementFovMultiplier();
        this.lightmapTextureManager.tick();
        if (this.client.getCameraEntity() == null) {
            this.client.setCameraEntity(this.client.player);
        }
        this.camera.updateEyeHeight();
        ++this.ticks;
        this.firstPersonRenderer.updateHeldItems();
        this.client.worldRenderer.method_22713(this.camera);
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
    public ShaderEffect getShader() {
        return this.shader;
    }

    public void onResized(int i, int j) {
        if (this.shader != null) {
            this.shader.setupDimensions(i, j);
        }
        this.client.worldRenderer.onResized(i, j);
    }

    public void updateTargetedEntity(float f) {
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
        this.client.crosshairTarget = entity2.rayTrace(d, f, false);
        Vec3d vec3d = entity2.getCameraPosVec(f);
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
        float g = 1.0f;
        Box box = entity2.getBoundingBox().stretch(vec3d2.multiply(d)).expand(1.0, 1.0, 1.0);
        EntityHitResult entityHitResult = ProjectileUtil.rayTrace(entity2, vec3d, vec3d3, box, entity -> !entity.isSpectator() && entity.collides(), e);
        if (entityHitResult != null) {
            Entity entity22 = entityHitResult.getEntity();
            Vec3d vec3d4 = entityHitResult.getPos();
            double h = vec3d.squaredDistanceTo(vec3d4);
            if (bl && h > 9.0) {
                this.client.crosshairTarget = BlockHitResult.createMissed(vec3d4, Direction.getFacing(vec3d2.x, vec3d2.y, vec3d2.z), new BlockPos(vec3d4));
            } else if (h < e || this.client.crosshairTarget == null) {
                this.client.crosshairTarget = entityHitResult;
                if (entity22 instanceof LivingEntity || entity22 instanceof ItemFrameEntity) {
                    this.client.targetedEntity = entity22;
                }
            }
        }
        this.client.getProfiler().pop();
    }

    private void updateMovementFovMultiplier() {
        float f = 1.0f;
        if (this.client.getCameraEntity() instanceof AbstractClientPlayerEntity) {
            AbstractClientPlayerEntity abstractClientPlayerEntity = (AbstractClientPlayerEntity)this.client.getCameraEntity();
            f = abstractClientPlayerEntity.getSpeed();
        }
        this.lastMovementFovMultiplier = this.movementFovMultiplier;
        this.movementFovMultiplier += (f - this.movementFovMultiplier) * 0.5f;
        if (this.movementFovMultiplier > 1.5f) {
            this.movementFovMultiplier = 1.5f;
        }
        if (this.movementFovMultiplier < 0.1f) {
            this.movementFovMultiplier = 0.1f;
        }
    }

    private double getFov(Camera camera, float f, boolean bl) {
        FluidState fluidState;
        if (this.renderingPanorama) {
            return 90.0;
        }
        double d = 70.0;
        if (bl) {
            d = this.client.options.fov;
            d *= (double)MathHelper.lerp(f, this.lastMovementFovMultiplier, this.movementFovMultiplier);
        }
        if (camera.getFocusedEntity() instanceof LivingEntity && ((LivingEntity)camera.getFocusedEntity()).getHealth() <= 0.0f) {
            float g = Math.min((float)((LivingEntity)camera.getFocusedEntity()).deathTime + f, 20.0f);
            d /= (double)((1.0f - 500.0f / (g + 500.0f)) * 2.0f + 1.0f);
        }
        if (!(fluidState = camera.getSubmergedFluidState()).isEmpty()) {
            d = d * 60.0 / 70.0;
        }
        return d;
    }

    private void bobViewWhenHurt(MatrixStack matrixStack, float f) {
        if (this.client.getCameraEntity() instanceof LivingEntity) {
            float h;
            LivingEntity livingEntity = (LivingEntity)this.client.getCameraEntity();
            float g = (float)livingEntity.hurtTime - f;
            if (livingEntity.getHealth() <= 0.0f) {
                h = Math.min((float)livingEntity.deathTime + f, 20.0f);
                matrixStack.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(40.0f - 8000.0f / (h + 200.0f)));
            }
            if (g < 0.0f) {
                return;
            }
            g /= (float)livingEntity.maxHurtTime;
            g = MathHelper.sin(g * g * g * g * (float)Math.PI);
            h = livingEntity.knockbackVelocity;
            matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(-h));
            matrixStack.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(-g * 14.0f));
            matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(h));
        }
    }

    private void bobView(MatrixStack matrixStack, float f) {
        if (!(this.client.getCameraEntity() instanceof PlayerEntity)) {
            return;
        }
        PlayerEntity playerEntity = (PlayerEntity)this.client.getCameraEntity();
        float g = playerEntity.horizontalSpeed - playerEntity.prevHorizontalSpeed;
        float h = -(playerEntity.horizontalSpeed + g * f);
        float i = MathHelper.lerp(f, playerEntity.field_7505, playerEntity.field_7483);
        matrixStack.translate(MathHelper.sin(h * (float)Math.PI) * i * 0.5f, -Math.abs(MathHelper.cos(h * (float)Math.PI) * i), 0.0);
        matrixStack.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(MathHelper.sin(h * (float)Math.PI) * i * 3.0f));
        matrixStack.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(Math.abs(MathHelper.cos(h * (float)Math.PI - 0.2f) * i) * 5.0f));
    }

    private void renderHand(MatrixStack matrixStack, Camera camera, float f) {
        boolean bl;
        if (this.renderingPanorama) {
            return;
        }
        this.method_22709(this.method_22973(camera, f, false));
        MatrixStack.Entry entry = matrixStack.peek();
        entry.getModel().loadIdentity();
        entry.getNormal().loadIdentity();
        matrixStack.push();
        this.bobViewWhenHurt(matrixStack, f);
        if (this.client.options.bobView) {
            this.bobView(matrixStack, f);
        }
        boolean bl2 = bl = this.client.getCameraEntity() instanceof LivingEntity && ((LivingEntity)this.client.getCameraEntity()).isSleeping();
        if (this.client.options.perspective == 0 && !bl && !this.client.options.hudHidden && this.client.interactionManager.getCurrentGameMode() != GameMode.SPECTATOR) {
            this.lightmapTextureManager.enable();
            this.firstPersonRenderer.method_22976(f, matrixStack, this.buffers.getEntityVertexConsumers(), this.client.player, this.client.getEntityRenderManager().method_23839(this.client.player, f));
            this.lightmapTextureManager.disable();
        }
        matrixStack.pop();
        if (this.client.options.perspective == 0 && !bl) {
            InGameOverlayRenderer.renderOverlays(this.client, matrixStack);
            this.bobViewWhenHurt(matrixStack, f);
        }
        if (this.client.options.bobView) {
            this.bobView(matrixStack, f);
        }
    }

    public void method_22709(Matrix4f matrix4f) {
        RenderSystem.matrixMode(5889);
        RenderSystem.loadIdentity();
        RenderSystem.multMatrix(matrix4f);
        RenderSystem.matrixMode(5888);
    }

    public Matrix4f method_22973(Camera camera, float f, boolean bl) {
        MatrixStack matrixStack = new MatrixStack();
        matrixStack.peek().getModel().loadIdentity();
        if (this.zoom != 1.0f) {
            matrixStack.translate(this.zoomX, -this.zoomY, 0.0);
            matrixStack.scale(this.zoom, this.zoom, 1.0f);
        }
        matrixStack.peek().getModel().multiply(Matrix4f.viewboxMatrix(this.getFov(camera, f, bl), (float)this.client.getWindow().getFramebufferWidth() / (float)this.client.getWindow().getFramebufferHeight(), 0.05f, this.viewDistance * 4.0f));
        return matrixStack.peek().getModel();
    }

    public static float getNightVisionStrength(LivingEntity livingEntity, float f) {
        int i = livingEntity.getStatusEffect(StatusEffects.NIGHT_VISION).getDuration();
        if (i > 200) {
            return 1.0f;
        }
        return 0.7f + MathHelper.sin(((float)i - f) * (float)Math.PI * 0.2f) * 0.3f;
    }

    public void render(float f, long l, boolean bl) {
        if (this.client.isWindowFocused() || !this.client.options.pauseOnLostFocus || this.client.options.touchscreen && this.client.mouse.wasRightButtonClicked()) {
            this.lastWindowFocusedTime = Util.getMeasuringTimeMs();
        } else if (Util.getMeasuringTimeMs() - this.lastWindowFocusedTime > 500L) {
            this.client.openPauseMenu(false);
        }
        if (this.client.skipGameRender) {
            return;
        }
        int i = (int)(this.client.mouse.getX() * (double)this.client.getWindow().getScaledWidth() / (double)this.client.getWindow().getWidth());
        int j = (int)(this.client.mouse.getY() * (double)this.client.getWindow().getScaledHeight() / (double)this.client.getWindow().getHeight());
        MatrixStack matrixStack = new MatrixStack();
        RenderSystem.viewport(0, 0, this.client.getWindow().getFramebufferWidth(), this.client.getWindow().getFramebufferHeight());
        if (bl && this.client.world != null) {
            this.client.getProfiler().push("level");
            this.renderWorld(f, l, matrixStack);
            if (this.client.isIntegratedServerRunning() && this.lastWorldIconUpdate < Util.getMeasuringTimeMs() - 1000L) {
                this.lastWorldIconUpdate = Util.getMeasuringTimeMs();
                if (!this.client.getServer().hasIconFile()) {
                    this.updateWorldIcon();
                }
            }
            this.client.worldRenderer.drawEntityOutlinesFramebuffer();
            if (this.shader != null && this.shadersEnabled) {
                RenderSystem.disableBlend();
                RenderSystem.disableDepthTest();
                RenderSystem.disableAlphaTest();
                RenderSystem.enableTexture();
                RenderSystem.matrixMode(5890);
                RenderSystem.pushMatrix();
                RenderSystem.loadIdentity();
                this.shader.render(f);
                RenderSystem.popMatrix();
            }
            this.client.getFramebuffer().beginWrite(true);
        }
        Window window = this.client.getWindow();
        RenderSystem.clear(256, MinecraftClient.IS_SYSTEM_MAC);
        RenderSystem.matrixMode(5889);
        RenderSystem.loadIdentity();
        RenderSystem.ortho(0.0, (double)window.getFramebufferWidth() / window.getScaleFactor(), (double)window.getFramebufferHeight() / window.getScaleFactor(), 0.0, 1000.0, 3000.0);
        RenderSystem.matrixMode(5888);
        RenderSystem.loadIdentity();
        RenderSystem.translatef(0.0f, 0.0f, -2000.0f);
        GuiLighting.enableForItems(matrixStack.peek().getModel());
        if (bl && this.client.world != null) {
            this.client.getProfiler().swap("gui");
            if (!this.client.options.hudHidden || this.client.currentScreen != null) {
                RenderSystem.defaultAlphaFunc();
                this.renderFloatingItem(this.client.getWindow().getScaledWidth(), this.client.getWindow().getScaledHeight(), f);
                this.client.inGameHud.render(f);
                RenderSystem.clear(256, MinecraftClient.IS_SYSTEM_MAC);
            }
            this.client.getProfiler().pop();
        }
        if (this.client.overlay != null) {
            try {
                this.client.overlay.render(i, j, this.client.getLastFrameDuration());
            } catch (Throwable throwable) {
                CrashReport crashReport = CrashReport.create(throwable, "Rendering overlay");
                CrashReportSection crashReportSection = crashReport.addElement("Overlay render details");
                crashReportSection.add("Overlay name", () -> this.client.overlay.getClass().getCanonicalName());
                throw new CrashException(crashReport);
            }
        }
        if (this.client.currentScreen != null) {
            try {
                this.client.currentScreen.render(i, j, this.client.getLastFrameDuration());
            } catch (Throwable throwable) {
                CrashReport crashReport = CrashReport.create(throwable, "Rendering screen");
                CrashReportSection crashReportSection = crashReport.addElement("Screen render details");
                crashReportSection.add("Screen name", () -> this.client.currentScreen.getClass().getCanonicalName());
                crashReportSection.add("Mouse location", () -> String.format(Locale.ROOT, "Scaled: (%d, %d). Absolute: (%f, %f)", i, j, this.client.mouse.getX(), this.client.mouse.getY()));
                crashReportSection.add("Screen size", () -> String.format(Locale.ROOT, "Scaled: (%d, %d). Absolute: (%d, %d). Scale factor of %f", this.client.getWindow().getScaledWidth(), this.client.getWindow().getScaledHeight(), this.client.getWindow().getFramebufferWidth(), this.client.getWindow().getFramebufferHeight(), this.client.getWindow().getScaleFactor()));
                throw new CrashException(crashReport);
            }
        }
    }

    private void updateWorldIcon() {
        if (this.client.worldRenderer.getCompletedChunkCount() > 10 && this.client.worldRenderer.isTerrainRenderComplete() && !this.client.getServer().hasIconFile()) {
            NativeImage nativeImage = ScreenshotUtils.takeScreenshot(this.client.getWindow().getFramebufferWidth(), this.client.getWindow().getFramebufferHeight(), this.client.getFramebuffer());
            ResourceImpl.RESOURCE_IO_EXECUTOR.execute(() -> {
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
                    nativeImage2.writeFile(this.client.getServer().getIconFile());
                } catch (IOException iOException) {
                    LOGGER.warn("Couldn't save auto screenshot", (Throwable)iOException);
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
        if (bl && !((PlayerEntity)entity).abilities.allowModifyWorld) {
            ItemStack itemStack = ((LivingEntity)entity).getMainHandStack();
            HitResult hitResult = this.client.crosshairTarget;
            if (hitResult != null && hitResult.getType() == HitResult.Type.BLOCK) {
                BlockPos blockPos = ((BlockHitResult)hitResult).getBlockPos();
                BlockState blockState = this.client.world.getBlockState(blockPos);
                if (this.client.interactionManager.getCurrentGameMode() == GameMode.SPECTATOR) {
                    bl = blockState.createContainerProvider(this.client.world, blockPos) != null;
                } else {
                    CachedBlockPosition cachedBlockPosition = new CachedBlockPosition(this.client.world, blockPos, false);
                    bl = !itemStack.isEmpty() && (itemStack.canDestroy(this.client.world.getTagManager(), cachedBlockPosition) || itemStack.canPlaceOn(this.client.world.getTagManager(), cachedBlockPosition));
                }
            }
        }
        return bl;
    }

    public void renderWorld(float f, long l, MatrixStack matrixStack) {
        float g;
        this.lightmapTextureManager.update(f);
        if (this.client.getCameraEntity() == null) {
            this.client.setCameraEntity(this.client.player);
        }
        this.updateTargetedEntity(f);
        this.client.getProfiler().push("center");
        boolean bl = this.shouldRenderBlockOutline();
        this.client.getProfiler().swap("camera");
        Camera camera = this.camera;
        this.viewDistance = this.client.options.viewDistance * 16;
        MatrixStack matrixStack2 = new MatrixStack();
        matrixStack2.peek().getModel().multiply(this.method_22973(camera, f, true));
        this.bobViewWhenHurt(matrixStack2, f);
        if (this.client.options.bobView) {
            this.bobView(matrixStack2, f);
        }
        if ((g = MathHelper.lerp(f, this.client.player.lastNauseaStrength, this.client.player.nextNauseaStrength)) > 0.0f) {
            int i = 20;
            if (this.client.player.hasStatusEffect(StatusEffects.NAUSEA)) {
                i = 7;
            }
            float h = 5.0f / (g * g + 5.0f) - g * 0.04f;
            h *= h;
            Vector3f vector3f = new Vector3f(0.0f, MathHelper.SQUARE_ROOT_OF_TWO / 2.0f, MathHelper.SQUARE_ROOT_OF_TWO / 2.0f);
            matrixStack2.multiply(vector3f.getDegreesQuaternion(((float)this.ticks + f) * (float)i));
            matrixStack2.scale(1.0f / h, 1.0f, 1.0f);
            float j = -((float)this.ticks + f) * (float)i;
            matrixStack2.multiply(vector3f.getDegreesQuaternion(j));
        }
        Matrix4f matrix4f = matrixStack2.peek().getModel();
        this.method_22709(matrix4f);
        camera.update(this.client.world, this.client.getCameraEntity() == null ? this.client.player : this.client.getCameraEntity(), this.client.options.perspective > 0, this.client.options.perspective == 2, f);
        matrixStack.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(camera.getPitch()));
        matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(camera.getYaw() + 180.0f));
        this.client.worldRenderer.render(matrixStack, f, l, bl, camera, this, this.lightmapTextureManager, matrix4f);
        this.client.getProfiler().swap("hand");
        if (this.renderHand) {
            RenderSystem.clear(256, MinecraftClient.IS_SYSTEM_MAC);
            this.renderHand(matrixStack, camera, f);
        }
        this.client.getProfiler().pop();
    }

    public void reset() {
        this.floatingItem = null;
        this.mapRenderer.clearStateTextures();
        this.camera.reset();
    }

    public MapRenderer getMapRenderer() {
        return this.mapRenderer;
    }

    public void showFloatingItem(ItemStack itemStack) {
        this.floatingItem = itemStack;
        this.floatingItemTimeLeft = 40;
        this.floatingItemWidth = this.random.nextFloat() * 2.0f - 1.0f;
        this.floatingItemHeight = this.random.nextFloat() * 2.0f - 1.0f;
    }

    private void renderFloatingItem(int i, int j, float f) {
        if (this.floatingItem == null || this.floatingItemTimeLeft <= 0) {
            return;
        }
        int k = 40 - this.floatingItemTimeLeft;
        float g = ((float)k + f) / 40.0f;
        float h = g * g;
        float l = g * h;
        float m = 10.25f * l * h - 24.95f * h * h + 25.5f * l - 13.8f * h + 4.0f * g;
        float n = m * (float)Math.PI;
        float o = this.floatingItemWidth * (float)(i / 4);
        float p = this.floatingItemHeight * (float)(j / 4);
        RenderSystem.enableAlphaTest();
        RenderSystem.pushMatrix();
        RenderSystem.pushLightingAttributes();
        RenderSystem.enableDepthTest();
        RenderSystem.disableCull();
        MatrixStack matrixStack = new MatrixStack();
        matrixStack.push();
        matrixStack.translate((float)(i / 2) + o * MathHelper.abs(MathHelper.sin(n * 2.0f)), (float)(j / 2) + p * MathHelper.abs(MathHelper.sin(n * 2.0f)), -50.0);
        float q = 50.0f + 175.0f * MathHelper.sin(n);
        matrixStack.scale(q, -q, q);
        matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(900.0f * MathHelper.abs(MathHelper.sin(n))));
        matrixStack.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(6.0f * MathHelper.cos(g * 8.0f)));
        matrixStack.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(6.0f * MathHelper.cos(g * 8.0f)));
        VertexConsumerProvider.Immediate immediate = this.buffers.getEntityVertexConsumers();
        this.client.getItemRenderer().method_23178(this.floatingItem, ModelTransformation.Type.FIXED, 0xF000F0, OverlayTexture.DEFAULT_UV, matrixStack, immediate);
        matrixStack.pop();
        immediate.draw();
        RenderSystem.popAttributes();
        RenderSystem.popMatrix();
        RenderSystem.enableCull();
        RenderSystem.disableDepthTest();
    }

    public float getSkyDarkness(float f) {
        return MathHelper.lerp(f, this.lastSkyDarkness, this.skyDarkness);
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

    public OverlayTexture method_22975() {
        return this.overlayTexture;
    }
}

