package net.minecraft.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.systems.VertexSorter;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.Locale;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.enums.CameraSubmersionType;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.PostEffectProcessor;
import net.minecraft.client.gl.ShaderLoader;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameOverlayRenderer;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.util.Pool;
import net.minecraft.client.util.ScreenshotRecorder;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.mob.SpiderEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ModelTransformationMode;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.resource.ResourceFactory;
import net.minecraft.resource.ResourceManager;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.crash.CrashCallable;
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
import net.minecraft.util.profiler.Profilers;
import net.minecraft.util.profiler.ScopedProfiler;
import net.minecraft.world.GameMode;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class GameRenderer implements AutoCloseable {
	private static final Identifier field_53899 = Identifier.ofVanilla("blur");
	public static final int field_49904 = 10;
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final boolean field_32688 = false;
	/**
	 * Since the camera is conceptualized as a single point, a depth of {@value}
	 * blocks is used to define a rectangular area to be rendered.
	 * 
	 * @see Camera#getProjection()
	 */
	public static final float CAMERA_DEPTH = 0.05F;
	private static final float field_44940 = 1000.0F;
	private final MinecraftClient client;
	private final ResourceManager resourceManager;
	private final Random random = Random.create();
	private float viewDistance;
	public final HeldItemRenderer firstPersonRenderer;
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
	private float zoom = 1.0F;
	private float zoomX;
	private float zoomY;
	public static final int field_32687 = 40;
	@Nullable
	private ItemStack floatingItem;
	private int floatingItemTimeLeft;
	private float floatingItemWidth;
	private float floatingItemHeight;
	private final Pool pool = new Pool(3);
	@Nullable
	private Identifier postProcessorId;
	private boolean postProcessorEnabled;
	private final Camera camera = new Camera();

	public GameRenderer(MinecraftClient client, HeldItemRenderer heldItemRenderer, ResourceManager resourceManager, BufferBuilderStorage buffers) {
		this.client = client;
		this.resourceManager = resourceManager;
		this.firstPersonRenderer = heldItemRenderer;
		this.lightmapTextureManager = new LightmapTextureManager(this, client);
		this.buffers = buffers;
	}

	public void close() {
		this.lightmapTextureManager.close();
		this.overlayTexture.close();
		this.pool.close();
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

	public void clearPostProcessor() {
		this.postProcessorId = null;
	}

	public void togglePostProcessorEnabled() {
		this.postProcessorEnabled = !this.postProcessorEnabled;
	}

	public void onCameraEntitySet(@Nullable Entity entity) {
		this.postProcessorId = null;
		if (entity instanceof CreeperEntity) {
			this.setPostProcessor(Identifier.ofVanilla("creeper"));
		} else if (entity instanceof SpiderEntity) {
			this.setPostProcessor(Identifier.ofVanilla("spider"));
		} else if (entity instanceof EndermanEntity) {
			this.setPostProcessor(Identifier.ofVanilla("invert"));
		}
	}

	private void setPostProcessor(Identifier id) {
		this.postProcessorId = id;
		this.postProcessorEnabled = true;
	}

	public void renderBlur() {
		float f = (float)this.client.options.getMenuBackgroundBlurrinessValue();
		if (!(f < 1.0F)) {
			PostEffectProcessor postEffectProcessor = this.client.getShaderLoader().loadPostEffect(field_53899, DefaultFramebufferSet.MAIN_ONLY);
			if (postEffectProcessor != null) {
				postEffectProcessor.setUniforms("Radius", f);
				postEffectProcessor.render(this.client.getFramebuffer(), this.pool);
			}
		}
	}

	public void preloadPrograms(ResourceFactory factory) {
		try {
			this.client
				.getShaderLoader()
				.preload(factory, ShaderProgramKeys.RENDERTYPE_GUI, ShaderProgramKeys.RENDERTYPE_GUI_OVERLAY, ShaderProgramKeys.POSITION_TEX_COLOR);
		} catch (ShaderLoader.LoadException | IOException var3) {
			throw new RuntimeException("Could not preload shaders for loading UI", var3);
		}
	}

	public void tick() {
		this.updateFovMultiplier();
		this.lightmapTextureManager.tick();
		if (this.client.getCameraEntity() == null) {
			this.client.setCameraEntity(this.client.player);
		}

		this.camera.updateEyeHeight();
		this.firstPersonRenderer.updateHeldItems();
		this.ticks++;
		if (this.client.world.getTickManager().shouldTick()) {
			this.client.worldRenderer.addWeatherParticlesAndSound(this.camera);
			this.lastSkyDarkness = this.skyDarkness;
			if (this.client.inGameHud.getBossBarHud().shouldDarkenSky()) {
				this.skyDarkness += 0.05F;
				if (this.skyDarkness > 1.0F) {
					this.skyDarkness = 1.0F;
				}
			} else if (this.skyDarkness > 0.0F) {
				this.skyDarkness -= 0.0125F;
			}

			if (this.floatingItemTimeLeft > 0) {
				this.floatingItemTimeLeft--;
				if (this.floatingItemTimeLeft == 0) {
					this.floatingItem = null;
				}
			}
		}
	}

	@Nullable
	public Identifier getPostProcessorId() {
		return this.postProcessorId;
	}

	public void onResized(int width, int height) {
		this.pool.clear();
		this.client.worldRenderer.onResized(width, height);
	}

	public void updateCrosshairTarget(float tickDelta) {
		Entity entity = this.client.getCameraEntity();
		if (entity != null) {
			if (this.client.world != null && this.client.player != null) {
				Profilers.get().push("pick");
				double d = this.client.player.getBlockInteractionRange();
				double e = this.client.player.getEntityInteractionRange();
				HitResult hitResult = this.findCrosshairTarget(entity, d, e, tickDelta);
				this.client.crosshairTarget = hitResult;
				this.client.targetedEntity = hitResult instanceof EntityHitResult entityHitResult ? entityHitResult.getEntity() : null;
				Profilers.get().pop();
			}
		}
	}

	private HitResult findCrosshairTarget(Entity camera, double blockInteractionRange, double entityInteractionRange, float tickDelta) {
		double d = Math.max(blockInteractionRange, entityInteractionRange);
		double e = MathHelper.square(d);
		Vec3d vec3d = camera.getCameraPosVec(tickDelta);
		HitResult hitResult = camera.raycast(d, tickDelta, false);
		double f = hitResult.getPos().squaredDistanceTo(vec3d);
		if (hitResult.getType() != HitResult.Type.MISS) {
			e = f;
			d = Math.sqrt(f);
		}

		Vec3d vec3d2 = camera.getRotationVec(tickDelta);
		Vec3d vec3d3 = vec3d.add(vec3d2.x * d, vec3d2.y * d, vec3d2.z * d);
		float g = 1.0F;
		Box box = camera.getBoundingBox().stretch(vec3d2.multiply(d)).expand(1.0, 1.0, 1.0);
		EntityHitResult entityHitResult = ProjectileUtil.raycast(camera, vec3d, vec3d3, box, EntityPredicates.CAN_HIT, e);
		return entityHitResult != null && entityHitResult.getPos().squaredDistanceTo(vec3d) < f
			? ensureTargetInRange(entityHitResult, vec3d, entityInteractionRange)
			: ensureTargetInRange(hitResult, vec3d, blockInteractionRange);
	}

	private static HitResult ensureTargetInRange(HitResult hitResult, Vec3d cameraPos, double interactionRange) {
		Vec3d vec3d = hitResult.getPos();
		if (!vec3d.isInRange(cameraPos, interactionRange)) {
			Vec3d vec3d2 = hitResult.getPos();
			Direction direction = Direction.getFacing(vec3d2.x - cameraPos.x, vec3d2.y - cameraPos.y, vec3d2.z - cameraPos.z);
			return BlockHitResult.createMissed(vec3d2, direction, BlockPos.ofFloored(vec3d2));
		} else {
			return hitResult;
		}
	}

	private void updateFovMultiplier() {
		float g;
		if (this.client.getCameraEntity() instanceof AbstractClientPlayerEntity abstractClientPlayerEntity) {
			GameOptions gameOptions = this.client.options;
			boolean bl = gameOptions.getPerspective().isFirstPerson();
			float f = gameOptions.getFovEffectScale().getValue().floatValue();
			g = abstractClientPlayerEntity.getFovMultiplier(bl, f);
		} else {
			g = 1.0F;
		}

		this.lastFovMultiplier = this.fovMultiplier;
		this.fovMultiplier = this.fovMultiplier + (g - this.fovMultiplier) * 0.5F;
		this.fovMultiplier = MathHelper.clamp(this.fovMultiplier, 0.1F, 1.5F);
	}

	private float getFov(Camera camera, float tickDelta, boolean changingFov) {
		if (this.renderingPanorama) {
			return 90.0F;
		} else {
			float f = 70.0F;
			if (changingFov) {
				f = (float)this.client.options.getFov().getValue().intValue();
				f *= MathHelper.lerp(tickDelta, this.lastFovMultiplier, this.fovMultiplier);
			}

			if (camera.getFocusedEntity() instanceof LivingEntity livingEntity && livingEntity.isDead()) {
				float g = Math.min((float)livingEntity.deathTime + tickDelta, 20.0F);
				f /= (1.0F - 500.0F / (g + 500.0F)) * 2.0F + 1.0F;
			}

			CameraSubmersionType cameraSubmersionType = camera.getSubmersionType();
			if (cameraSubmersionType == CameraSubmersionType.LAVA || cameraSubmersionType == CameraSubmersionType.WATER) {
				float g = this.client.options.getFovEffectScale().getValue().floatValue();
				f *= MathHelper.lerp(g, 1.0F, 0.85714287F);
			}

			return f;
		}
	}

	private void tiltViewWhenHurt(MatrixStack matrices, float tickDelta) {
		if (this.client.getCameraEntity() instanceof LivingEntity livingEntity) {
			float f = (float)livingEntity.hurtTime - tickDelta;
			if (livingEntity.isDead()) {
				float g = Math.min((float)livingEntity.deathTime + tickDelta, 20.0F);
				matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(40.0F - 8000.0F / (g + 200.0F)));
			}

			if (f < 0.0F) {
				return;
			}

			f /= (float)livingEntity.maxHurtTime;
			f = MathHelper.sin(f * f * f * f * (float) Math.PI);
			float g = livingEntity.getDamageTiltYaw();
			matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-g));
			float h = (float)((double)(-f) * 14.0 * this.client.options.getDamageTiltStrength().getValue());
			matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(h));
			matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(g));
		}
	}

	private void bobView(MatrixStack matrices, float tickDelta) {
		if (this.client.getCameraEntity() instanceof AbstractClientPlayerEntity abstractClientPlayerEntity) {
			float var7 = abstractClientPlayerEntity.distanceMoved - abstractClientPlayerEntity.lastDistanceMoved;
			float g = -(abstractClientPlayerEntity.distanceMoved + var7 * tickDelta);
			float h = MathHelper.lerp(tickDelta, abstractClientPlayerEntity.prevStrideDistance, abstractClientPlayerEntity.strideDistance);
			matrices.translate(MathHelper.sin(g * (float) Math.PI) * h * 0.5F, -Math.abs(MathHelper.cos(g * (float) Math.PI) * h), 0.0F);
			matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(MathHelper.sin(g * (float) Math.PI) * h * 3.0F));
			matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(Math.abs(MathHelper.cos(g * (float) Math.PI - 0.2F) * h) * 5.0F));
		}
	}

	public void renderWithZoom(float zoom, float zoomX, float zoomY) {
		this.zoom = zoom;
		this.zoomX = zoomX;
		this.zoomY = zoomY;
		this.setBlockOutlineEnabled(false);
		this.setRenderHand(false);
		this.renderWorld(RenderTickCounter.ZERO);
		this.zoom = 1.0F;
	}

	private void renderHand(Camera camera, float tickDelta, Matrix4f matrix4f) {
		if (!this.renderingPanorama) {
			Matrix4f matrix4f2 = this.getBasicProjectionMatrix(this.getFov(camera, tickDelta, false));
			RenderSystem.setProjectionMatrix(matrix4f2, VertexSorter.BY_DISTANCE);
			MatrixStack matrixStack = new MatrixStack();
			matrixStack.push();
			matrixStack.multiplyPositionMatrix(matrix4f.invert(new Matrix4f()));
			Matrix4fStack matrix4fStack = RenderSystem.getModelViewStack();
			matrix4fStack.pushMatrix().mul(matrix4f);
			this.tiltViewWhenHurt(matrixStack, tickDelta);
			if (this.client.options.getBobView().getValue()) {
				this.bobView(matrixStack, tickDelta);
			}

			boolean bl = this.client.getCameraEntity() instanceof LivingEntity && ((LivingEntity)this.client.getCameraEntity()).isSleeping();
			if (this.client.options.getPerspective().isFirstPerson()
				&& !bl
				&& !this.client.options.hudHidden
				&& this.client.interactionManager.getCurrentGameMode() != GameMode.SPECTATOR) {
				this.lightmapTextureManager.enable();
				this.firstPersonRenderer
					.renderItem(
						tickDelta,
						matrixStack,
						this.buffers.getEntityVertexConsumers(),
						this.client.player,
						this.client.getEntityRenderDispatcher().getLight(this.client.player, tickDelta)
					);
				this.lightmapTextureManager.disable();
			}

			matrix4fStack.popMatrix();
			matrixStack.pop();
			if (this.client.options.getPerspective().isFirstPerson() && !bl) {
				InGameOverlayRenderer.renderOverlays(this.client, matrixStack);
			}
		}
	}

	public Matrix4f getBasicProjectionMatrix(float fovDegrees) {
		Matrix4f matrix4f = new Matrix4f();
		if (this.zoom != 1.0F) {
			matrix4f.translate(this.zoomX, -this.zoomY, 0.0F);
			matrix4f.scale(this.zoom, this.zoom, 1.0F);
		}

		return matrix4f.perspective(
			fovDegrees * (float) (Math.PI / 180.0),
			(float)this.client.getWindow().getFramebufferWidth() / (float)this.client.getWindow().getFramebufferHeight(),
			0.05F,
			this.getFarPlaneDistance()
		);
	}

	public float getFarPlaneDistance() {
		return this.viewDistance * 4.0F;
	}

	public static float getNightVisionStrength(LivingEntity entity, float tickDelta) {
		StatusEffectInstance statusEffectInstance = entity.getStatusEffect(StatusEffects.NIGHT_VISION);
		return !statusEffectInstance.isDurationBelow(200)
			? 1.0F
			: 0.7F + MathHelper.sin(((float)statusEffectInstance.getDuration() - tickDelta) * (float) Math.PI * 0.2F) * 0.3F;
	}

	public void render(RenderTickCounter tickCounter, boolean tick) {
		if (!this.client.isWindowFocused()
			&& this.client.options.pauseOnLostFocus
			&& (!this.client.options.getTouchscreen().getValue() || !this.client.mouse.wasRightButtonClicked())) {
			if (Util.getMeasuringTimeMs() - this.lastWindowFocusedTime > 500L) {
				this.client.openGameMenu(false);
			}
		} else {
			this.lastWindowFocusedTime = Util.getMeasuringTimeMs();
		}

		if (!this.client.skipGameRender) {
			Profiler profiler = Profilers.get();
			boolean bl = this.client.isFinishedLoading();
			int i = (int)(this.client.mouse.getX() * (double)this.client.getWindow().getScaledWidth() / (double)this.client.getWindow().getWidth());
			int j = (int)(this.client.mouse.getY() * (double)this.client.getWindow().getScaledHeight() / (double)this.client.getWindow().getHeight());
			RenderSystem.viewport(0, 0, this.client.getWindow().getFramebufferWidth(), this.client.getWindow().getFramebufferHeight());
			if (bl && tick && this.client.world != null) {
				profiler.push("level");
				this.renderWorld(tickCounter);
				this.updateWorldIcon();
				this.client.worldRenderer.drawEntityOutlinesFramebuffer();
				if (this.postProcessorId != null && this.postProcessorEnabled) {
					RenderSystem.disableBlend();
					RenderSystem.disableDepthTest();
					RenderSystem.resetTextureMatrix();
					PostEffectProcessor postEffectProcessor = this.client.getShaderLoader().loadPostEffect(this.postProcessorId, DefaultFramebufferSet.MAIN_ONLY);
					if (postEffectProcessor != null) {
						postEffectProcessor.render(this.client.getFramebuffer(), this.pool);
					}
				}

				this.client.getFramebuffer().beginWrite(true);
			}

			Window window = this.client.getWindow();
			RenderSystem.clear(256);
			Matrix4f matrix4f = new Matrix4f()
				.setOrtho(
					0.0F,
					(float)((double)window.getFramebufferWidth() / window.getScaleFactor()),
					(float)((double)window.getFramebufferHeight() / window.getScaleFactor()),
					0.0F,
					1000.0F,
					21000.0F
				);
			RenderSystem.setProjectionMatrix(matrix4f, VertexSorter.BY_Z);
			float f = 1000.0F;
			Matrix4fStack matrix4fStack = RenderSystem.getModelViewStack();
			matrix4fStack.pushMatrix();
			matrix4fStack.translation(0.0F, 0.0F, -10000.0F);
			DiffuseLighting.enableGuiDepthLighting();
			DrawContext drawContext = new DrawContext(this.client, this.buffers.getEntityVertexConsumers());
			drawContext.getMatrices().translate(0.0F, 0.0F, -1000.0F);
			if (bl && tick && this.client.world != null) {
				profiler.swap("gui");
				if (!this.client.options.hudHidden) {
					this.renderFloatingItem(drawContext, tickCounter.getTickDelta(false));
				}

				this.client.inGameHud.render(drawContext, tickCounter);
				drawContext.draw();
				RenderSystem.clear(256);
				profiler.pop();
			}

			if (this.client.getOverlay() != null) {
				try {
					this.client.getOverlay().render(drawContext, i, j, tickCounter.getLastFrameDuration());
				} catch (Throwable var18) {
					CrashReport crashReport = CrashReport.create(var18, "Rendering overlay");
					CrashReportSection crashReportSection = crashReport.addElement("Overlay render details");
					crashReportSection.add("Overlay name", (CrashCallable<String>)(() -> this.client.getOverlay().getClass().getCanonicalName()));
					throw new CrashException(crashReport);
				}
			} else if (bl && this.client.currentScreen != null) {
				try {
					this.client.currentScreen.renderWithTooltip(drawContext, i, j, tickCounter.getLastFrameDuration());
				} catch (Throwable var17) {
					CrashReport crashReport = CrashReport.create(var17, "Rendering screen");
					CrashReportSection crashReportSection = crashReport.addElement("Screen render details");
					crashReportSection.add("Screen name", (CrashCallable<String>)(() -> this.client.currentScreen.getClass().getCanonicalName()));
					crashReportSection.add(
						"Mouse location",
						(CrashCallable<String>)(() -> String.format(Locale.ROOT, "Scaled: (%d, %d). Absolute: (%f, %f)", i, j, this.client.mouse.getX(), this.client.mouse.getY()))
					);
					crashReportSection.add(
						"Screen size",
						(CrashCallable<String>)(() -> String.format(
								Locale.ROOT,
								"Scaled: (%d, %d). Absolute: (%d, %d). Scale factor of %f",
								this.client.getWindow().getScaledWidth(),
								this.client.getWindow().getScaledHeight(),
								this.client.getWindow().getFramebufferWidth(),
								this.client.getWindow().getFramebufferHeight(),
								this.client.getWindow().getScaleFactor()
							))
					);
					throw new CrashException(crashReport);
				}

				try {
					if (this.client.currentScreen != null) {
						this.client.currentScreen.updateNarrator();
					}
				} catch (Throwable var16) {
					CrashReport crashReport = CrashReport.create(var16, "Narrating screen");
					CrashReportSection crashReportSection = crashReport.addElement("Screen details");
					crashReportSection.add("Screen name", (CrashCallable<String>)(() -> this.client.currentScreen.getClass().getCanonicalName()));
					throw new CrashException(crashReport);
				}
			}

			if (bl && tick && this.client.world != null) {
				this.client.inGameHud.renderAutosaveIndicator(drawContext, tickCounter);
			}

			if (bl) {
				try (ScopedProfiler scopedProfiler = profiler.scoped("toasts")) {
					this.client.getToastManager().draw(drawContext);
				}
			}

			drawContext.draw();
			matrix4fStack.popMatrix();
			this.pool.decrementLifespan();
		}
	}

	private void updateWorldIcon() {
		if (!this.hasWorldIcon && this.client.isInSingleplayer()) {
			long l = Util.getMeasuringTimeMs();
			if (l - this.lastWorldIconUpdate >= 1000L) {
				this.lastWorldIconUpdate = l;
				IntegratedServer integratedServer = this.client.getServer();
				if (integratedServer != null && !integratedServer.isStopped()) {
					integratedServer.getIconFile().ifPresent(path -> {
						if (Files.isRegularFile(path, new LinkOption[0])) {
							this.hasWorldIcon = true;
						} else {
							this.updateWorldIcon(path);
						}
					});
				}
			}
		}
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

				try (NativeImage nativeImage2 = new NativeImage(64, 64, false)) {
					nativeImage.resizeSubRectTo(k, l, i, j, nativeImage2);
					nativeImage2.writeTo(path);
				} catch (IOException var16) {
					LOGGER.warn("Couldn't save auto screenshot", (Throwable)var16);
				} finally {
					nativeImage.close();
				}
			});
		}
	}

	private boolean shouldRenderBlockOutline() {
		if (!this.blockOutlineEnabled) {
			return false;
		} else {
			Entity entity = this.client.getCameraEntity();
			boolean bl = entity instanceof PlayerEntity && !this.client.options.hudHidden;
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
						Registry<Block> registry = this.client.world.getRegistryManager().getOrThrow(RegistryKeys.BLOCK);
						bl = !itemStack.isEmpty() && (itemStack.canBreak(cachedBlockPosition) || itemStack.canPlaceOn(cachedBlockPosition));
					}
				}
			}

			return bl;
		}
	}

	public void renderWorld(RenderTickCounter renderTickCounter) {
		float f = renderTickCounter.getTickDelta(true);
		this.lightmapTextureManager.update(f);
		if (this.client.getCameraEntity() == null) {
			this.client.setCameraEntity(this.client.player);
		}

		this.updateCrosshairTarget(f);
		Profiler profiler = Profilers.get();
		profiler.push("center");
		boolean bl = this.shouldRenderBlockOutline();
		profiler.swap("camera");
		Camera camera = this.camera;
		Entity entity = (Entity)(this.client.getCameraEntity() == null ? this.client.player : this.client.getCameraEntity());
		float g = this.client.world.getTickManager().shouldSkipTick(entity) ? 1.0F : f;
		camera.update(this.client.world, entity, !this.client.options.getPerspective().isFirstPerson(), this.client.options.getPerspective().isFrontView(), g);
		this.viewDistance = (float)(this.client.options.getClampedViewDistance() * 16);
		float h = this.getFov(camera, f, true);
		Matrix4f matrix4f = this.getBasicProjectionMatrix(h);
		MatrixStack matrixStack = new MatrixStack();
		this.tiltViewWhenHurt(matrixStack, camera.getLastTickDelta());
		if (this.client.options.getBobView().getValue()) {
			this.bobView(matrixStack, camera.getLastTickDelta());
		}

		matrix4f.mul(matrixStack.peek().getPositionMatrix());
		float i = this.client.options.getDistortionEffectScale().getValue().floatValue();
		float j = MathHelper.lerp(f, this.client.player.prevNauseaIntensity, this.client.player.nauseaIntensity) * i * i;
		if (j > 0.0F) {
			int k = this.client.player.hasStatusEffect(StatusEffects.NAUSEA) ? 7 : 20;
			float l = 5.0F / (j * j + 5.0F) - j * 0.04F;
			l *= l;
			Vector3f vector3f = new Vector3f(0.0F, MathHelper.SQUARE_ROOT_OF_TWO / 2.0F, MathHelper.SQUARE_ROOT_OF_TWO / 2.0F);
			float m = ((float)this.ticks + f) * (float)k * (float) (Math.PI / 180.0);
			matrix4f.rotate(m, vector3f);
			matrix4f.scale(1.0F / l, 1.0F, 1.0F);
			matrix4f.rotate(-m, vector3f);
		}

		float n = Math.max(h, (float)this.client.options.getFov().getValue().intValue());
		Matrix4f matrix4f2 = this.getBasicProjectionMatrix(n);
		RenderSystem.setProjectionMatrix(matrix4f, VertexSorter.BY_DISTANCE);
		Quaternionf quaternionf = camera.getRotation().conjugate(new Quaternionf());
		Matrix4f matrix4f3 = new Matrix4f().rotation(quaternionf);
		this.client.worldRenderer.setupFrustum(camera.getPos(), matrix4f3, matrix4f2);
		this.client.getFramebuffer().beginWrite(true);
		this.client.worldRenderer.render(this.pool, renderTickCounter, bl, camera, this, this.lightmapTextureManager, matrix4f3, matrix4f);
		profiler.swap("hand");
		if (this.renderHand) {
			RenderSystem.clear(256);
			this.renderHand(camera, f, matrix4f3);
		}

		profiler.pop();
	}

	public void reset() {
		this.floatingItem = null;
		this.client.getMapTextureManager().clear();
		this.camera.reset();
		this.hasWorldIcon = false;
	}

	public void showFloatingItem(ItemStack floatingItem) {
		this.floatingItem = floatingItem;
		this.floatingItemTimeLeft = 40;
		this.floatingItemWidth = this.random.nextFloat() * 2.0F - 1.0F;
		this.floatingItemHeight = this.random.nextFloat() * 2.0F - 1.0F;
	}

	private void renderFloatingItem(DrawContext context, float tickDelta) {
		if (this.floatingItem != null && this.floatingItemTimeLeft > 0) {
			int i = 40 - this.floatingItemTimeLeft;
			float f = ((float)i + tickDelta) / 40.0F;
			float g = f * f;
			float h = f * g;
			float j = 10.25F * h * g - 24.95F * g * g + 25.5F * h - 13.8F * g + 4.0F * f;
			float k = j * (float) Math.PI;
			float l = this.floatingItemWidth * (float)(context.getScaledWindowWidth() / 4);
			float m = this.floatingItemHeight * (float)(context.getScaledWindowHeight() / 4);
			MatrixStack matrixStack = context.getMatrices();
			matrixStack.push();
			matrixStack.translate(
				(float)(context.getScaledWindowWidth() / 2) + l * MathHelper.abs(MathHelper.sin(k * 2.0F)),
				(float)(context.getScaledWindowHeight() / 2) + m * MathHelper.abs(MathHelper.sin(k * 2.0F)),
				-50.0F
			);
			float n = 50.0F + 175.0F * MathHelper.sin(k);
			matrixStack.scale(n, -n, n);
			matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(900.0F * MathHelper.abs(MathHelper.sin(k))));
			matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(6.0F * MathHelper.cos(f * 8.0F)));
			matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(6.0F * MathHelper.cos(f * 8.0F)));
			context.draw(
				vertexConsumers -> this.client
						.getItemRenderer()
						.renderItem(this.floatingItem, ModelTransformationMode.FIXED, 15728880, OverlayTexture.DEFAULT_UV, matrixStack, vertexConsumers, this.client.world, 0)
			);
			matrixStack.pop();
		}
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
}
