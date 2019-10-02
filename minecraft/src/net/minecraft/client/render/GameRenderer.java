package net.minecraft.client.render;

import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.IOException;
import java.util.Locale;
import java.util.Random;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4603;
import net.minecraft.block.BlockState;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.ShaderEffect;
import net.minecraft.client.gui.MapRenderer;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.util.ScreenshotUtils;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.Matrix4f;
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
import net.minecraft.util.SystemUtil;
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
import net.minecraft.util.math.MatrixStack;
import net.minecraft.world.GameMode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class GameRenderer implements AutoCloseable, SynchronousResourceReloadListener {
	private static final Logger LOGGER = LogManager.getLogger();
	private final MinecraftClient client;
	private final ResourceManager resourceContainer;
	private final Random random = new Random();
	private float viewDistance;
	public final FirstPersonRenderer firstPersonRenderer;
	private final MapRenderer mapRenderer;
	private final LayeredBufferBuilderStorage field_20948;
	private int ticks;
	private float movementFovMultiplier;
	private float lastMovementFovMultiplier;
	private float skyDarkness;
	private float lastSkyDarkness;
	private boolean renderHand = true;
	private boolean blockOutlineEnabled = true;
	private long lastWorldIconUpdate;
	private long lastWindowFocusedTime = SystemUtil.getMeasuringTimeMs();
	private final LightmapTextureManager lightmapTextureManager;
	private final OverlayTexture field_20949 = new OverlayTexture();
	private boolean field_4001;
	private float field_4005 = 1.0F;
	private float field_3988;
	private float field_4004;
	@Nullable
	private ItemStack floatingItem;
	private int floatingItemTimeLeft;
	private float floatingItemWidth;
	private float floatingItemHeight;
	@Nullable
	private ShaderEffect shader;
	private static final Identifier[] SHADERS_LOCATIONS = new Identifier[]{
		new Identifier("shaders/post/notch.json"),
		new Identifier("shaders/post/fxaa.json"),
		new Identifier("shaders/post/art.json"),
		new Identifier("shaders/post/bumpy.json"),
		new Identifier("shaders/post/blobs2.json"),
		new Identifier("shaders/post/pencil.json"),
		new Identifier("shaders/post/color_convolve.json"),
		new Identifier("shaders/post/deconverge.json"),
		new Identifier("shaders/post/flip.json"),
		new Identifier("shaders/post/invert.json"),
		new Identifier("shaders/post/ntsc.json"),
		new Identifier("shaders/post/outline.json"),
		new Identifier("shaders/post/phosphor.json"),
		new Identifier("shaders/post/scan_pincushion.json"),
		new Identifier("shaders/post/sobel.json"),
		new Identifier("shaders/post/bits.json"),
		new Identifier("shaders/post/desaturate.json"),
		new Identifier("shaders/post/green.json"),
		new Identifier("shaders/post/blur.json"),
		new Identifier("shaders/post/wobble.json"),
		new Identifier("shaders/post/blobs.json"),
		new Identifier("shaders/post/antialias.json"),
		new Identifier("shaders/post/creeper.json"),
		new Identifier("shaders/post/spider.json")
	};
	public static final int SHADER_COUNT = SHADERS_LOCATIONS.length;
	private int forcedShaderIndex = SHADER_COUNT;
	private boolean shadersEnabled;
	private final Camera camera = new Camera();

	public GameRenderer(MinecraftClient minecraftClient, ResourceManager resourceManager, LayeredBufferBuilderStorage layeredBufferBuilderStorage) {
		this.client = minecraftClient;
		this.resourceContainer = resourceManager;
		this.firstPersonRenderer = minecraftClient.getFirstPersonRenderer();
		this.mapRenderer = new MapRenderer(minecraftClient.getTextureManager());
		this.lightmapTextureManager = new LightmapTextureManager(this, minecraftClient);
		this.field_20948 = layeredBufferBuilderStorage;
		this.shader = null;
	}

	public void close() {
		this.lightmapTextureManager.close();
		this.mapRenderer.close();
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
		} catch (IOException var3) {
			LOGGER.warn("Failed to load shader: {}", identifier, var3);
			this.forcedShaderIndex = SHADER_COUNT;
			this.shadersEnabled = false;
		} catch (JsonSyntaxException var4) {
			LOGGER.warn("Failed to load shader: {}", identifier, var4);
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
		this.ticks++;
		this.firstPersonRenderer.updateHeldItems();
		this.client.worldRenderer.method_22713(this.camera);
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
		Entity entity = this.client.getCameraEntity();
		if (entity != null) {
			if (this.client.world != null) {
				this.client.getProfiler().push("pick");
				this.client.targetedEntity = null;
				double d = (double)this.client.interactionManager.getReachDistance();
				this.client.hitResult = entity.rayTrace(d, f, false);
				net.minecraft.util.math.Vec3d vec3d = entity.getCameraPosVec(f);
				boolean bl = false;
				int i = 3;
				double e = d;
				if (this.client.interactionManager.hasExtendedReach()) {
					e = 6.0;
					d = e;
				} else {
					if (d > 3.0) {
						bl = true;
					}

					d = d;
				}

				e *= e;
				if (this.client.hitResult != null) {
					e = this.client.hitResult.getPos().squaredDistanceTo(vec3d);
				}

				net.minecraft.util.math.Vec3d vec3d2 = entity.getRotationVec(1.0F);
				net.minecraft.util.math.Vec3d vec3d3 = vec3d.add(vec3d2.x * d, vec3d2.y * d, vec3d2.z * d);
				float g = 1.0F;
				Box box = entity.getBoundingBox().stretch(vec3d2.multiply(d)).expand(1.0, 1.0, 1.0);
				EntityHitResult entityHitResult = ProjectileUtil.rayTrace(entity, vec3d, vec3d3, box, entityx -> !entityx.isSpectator() && entityx.collides(), e);
				if (entityHitResult != null) {
					Entity entity2 = entityHitResult.getEntity();
					net.minecraft.util.math.Vec3d vec3d4 = entityHitResult.getPos();
					double h = vec3d.squaredDistanceTo(vec3d4);
					if (bl && h > 9.0) {
						this.client.hitResult = BlockHitResult.createMissed(vec3d4, Direction.getFacing(vec3d2.x, vec3d2.y, vec3d2.z), new BlockPos(vec3d4));
					} else if (h < e || this.client.hitResult == null) {
						this.client.hitResult = entityHitResult;
						if (entity2 instanceof LivingEntity || entity2 instanceof ItemFrameEntity) {
							this.client.targetedEntity = entity2;
						}
					}
				}

				this.client.getProfiler().pop();
			}
		}
	}

	private void updateMovementFovMultiplier() {
		float f = 1.0F;
		if (this.client.getCameraEntity() instanceof AbstractClientPlayerEntity) {
			AbstractClientPlayerEntity abstractClientPlayerEntity = (AbstractClientPlayerEntity)this.client.getCameraEntity();
			f = abstractClientPlayerEntity.getSpeed();
		}

		this.lastMovementFovMultiplier = this.movementFovMultiplier;
		this.movementFovMultiplier = this.movementFovMultiplier + (f - this.movementFovMultiplier) * 0.5F;
		if (this.movementFovMultiplier > 1.5F) {
			this.movementFovMultiplier = 1.5F;
		}

		if (this.movementFovMultiplier < 0.1F) {
			this.movementFovMultiplier = 0.1F;
		}
	}

	private double getFov(Camera camera, float f, boolean bl) {
		if (this.field_4001) {
			return 90.0;
		} else {
			double d = 70.0;
			if (bl) {
				d = this.client.options.fov;
				d *= (double)MathHelper.lerp(f, this.lastMovementFovMultiplier, this.movementFovMultiplier);
			}

			if (camera.getFocusedEntity() instanceof LivingEntity && ((LivingEntity)camera.getFocusedEntity()).getHealth() <= 0.0F) {
				float g = Math.min((float)((LivingEntity)camera.getFocusedEntity()).deathTime + f, 20.0F);
				d /= (double)((1.0F - 500.0F / (g + 500.0F)) * 2.0F + 1.0F);
			}

			FluidState fluidState = camera.getSubmergedFluidState();
			if (!fluidState.isEmpty()) {
				d = d * 60.0 / 70.0;
			}

			return d;
		}
	}

	private void bobViewWhenHurt(MatrixStack matrixStack, float f) {
		if (this.client.getCameraEntity() instanceof LivingEntity) {
			LivingEntity livingEntity = (LivingEntity)this.client.getCameraEntity();
			float g = (float)livingEntity.hurtTime - f;
			if (livingEntity.getHealth() <= 0.0F) {
				float h = Math.min((float)livingEntity.deathTime + f, 20.0F);
				matrixStack.multiply(Vector3f.POSITIVE_Z.getRotationQuaternion(40.0F - 8000.0F / (h + 200.0F), true));
			}

			if (g < 0.0F) {
				return;
			}

			g /= (float)livingEntity.maxHurtTime;
			g = MathHelper.sin(g * g * g * g * (float) Math.PI);
			float h = livingEntity.knockbackVelocity;
			matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion(-h, true));
			matrixStack.multiply(Vector3f.POSITIVE_Z.getRotationQuaternion(-g * 14.0F, true));
			matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion(h, true));
		}
	}

	private void bobView(MatrixStack matrixStack, float f) {
		if (this.client.getCameraEntity() instanceof PlayerEntity) {
			PlayerEntity playerEntity = (PlayerEntity)this.client.getCameraEntity();
			float g = playerEntity.horizontalSpeed - playerEntity.prevHorizontalSpeed;
			float h = -(playerEntity.horizontalSpeed + g * f);
			float i = MathHelper.lerp(f, playerEntity.field_7505, playerEntity.field_7483);
			matrixStack.translate((double)(MathHelper.sin(h * (float) Math.PI) * i * 0.5F), (double)(-Math.abs(MathHelper.cos(h * (float) Math.PI) * i)), 0.0);
			matrixStack.multiply(Vector3f.POSITIVE_Z.getRotationQuaternion(MathHelper.sin(h * (float) Math.PI) * i * 3.0F, true));
			matrixStack.multiply(Vector3f.POSITIVE_X.getRotationQuaternion(Math.abs(MathHelper.cos(h * (float) Math.PI - 0.2F) * i) * 5.0F, true));
		}
	}

	private void renderHand(MatrixStack matrixStack, Camera camera, float f) {
		if (!this.field_4001) {
			this.method_22709(camera, f, false, false, 2.0F);
			matrixStack.peek().loadIdentity();
			matrixStack.push();
			this.bobViewWhenHurt(matrixStack, f);
			if (this.client.options.bobView) {
				this.bobView(matrixStack, f);
			}

			boolean bl = this.client.getCameraEntity() instanceof LivingEntity && ((LivingEntity)this.client.getCameraEntity()).isSleeping();
			if (this.client.options.perspective == 0
				&& !bl
				&& !this.client.options.hudHidden
				&& this.client.interactionManager.getCurrentGameMode() != GameMode.SPECTATOR) {
				this.lightmapTextureManager.enable();
				this.firstPersonRenderer.method_22976(f, matrixStack, this.field_20948.method_23000());
				this.lightmapTextureManager.disable();
			}

			matrixStack.pop();
			if (this.client.options.perspective == 0 && !bl) {
				class_4603.method_23067(this.client, matrixStack);
				this.bobViewWhenHurt(matrixStack, f);
			}

			if (this.client.options.bobView) {
				this.bobView(matrixStack, f);
			}
		}
	}

	public void method_22709(Camera camera, float f, boolean bl, boolean bl2, float g) {
		RenderSystem.matrixMode(5889);
		RenderSystem.loadIdentity();
		RenderSystem.multMatrix(this.method_22973(camera, f, bl, bl2, g));
		RenderSystem.matrixMode(5888);
	}

	public Matrix4f method_22973(Camera camera, float f, boolean bl, boolean bl2, float g) {
		MatrixStack matrixStack = new MatrixStack();
		matrixStack.peek().loadIdentity();
		if (bl2 && this.field_4005 != 1.0F) {
			matrixStack.translate((double)this.field_3988, (double)(-this.field_4004), 0.0);
			matrixStack.scale(this.field_4005, this.field_4005, 1.0F);
		}

		matrixStack.peek()
			.multiply(
				Matrix4f.method_4929(
					this.getFov(camera, f, bl),
					(float)this.client.getWindow().getFramebufferWidth() / (float)this.client.getWindow().getFramebufferHeight(),
					0.05F,
					this.viewDistance * g
				)
			);
		return matrixStack.peek();
	}

	public static float getNightVisionStrength(LivingEntity livingEntity, float f) {
		int i = livingEntity.getStatusEffect(StatusEffects.NIGHT_VISION).getDuration();
		return i > 200 ? 1.0F : 0.7F + MathHelper.sin(((float)i - f) * (float) Math.PI * 0.2F) * 0.3F;
	}

	public void render(float f, long l, boolean bl) {
		if (!this.client.isWindowFocused()
			&& this.client.options.pauseOnLostFocus
			&& (!this.client.options.touchscreen || !this.client.mouse.wasRightButtonClicked())) {
			if (SystemUtil.getMeasuringTimeMs() - this.lastWindowFocusedTime > 500L) {
				this.client.openPauseMenu(false);
			}
		} else {
			this.lastWindowFocusedTime = SystemUtil.getMeasuringTimeMs();
		}

		if (!this.client.skipGameRender) {
			int i = (int)(this.client.mouse.getX() * (double)this.client.getWindow().getScaledWidth() / (double)this.client.getWindow().getWidth());
			int j = (int)(this.client.mouse.getY() * (double)this.client.getWindow().getScaledHeight() / (double)this.client.getWindow().getHeight());
			int k = this.client.options.maxFps;
			MatrixStack matrixStack = new MatrixStack();
			RenderSystem.viewport(0, 0, this.client.getWindow().getFramebufferWidth(), this.client.getWindow().getFramebufferHeight());
			if (bl && this.client.world != null) {
				this.client.getProfiler().push("level");
				int m = Math.min(MinecraftClient.getCurrentFps(), k);
				m = Math.max(m, 60);
				long n = SystemUtil.getMeasuringTimeNano() - l;
				long o = Math.max((long)(1000000000 / m / 4) - n, 0L);
				this.renderWorld(f, SystemUtil.getMeasuringTimeNano() + o, matrixStack);
				if (this.client.isIntegratedServerRunning() && this.lastWorldIconUpdate < SystemUtil.getMeasuringTimeMs() - 1000L) {
					this.lastWorldIconUpdate = SystemUtil.getMeasuringTimeMs();
					if (!this.client.getServer().hasIconFile()) {
						this.updateWorldIcon();
					}
				}

				this.client.worldRenderer.drawEntityOutlinesFramebuffer();
				if (this.shader != null && this.shadersEnabled) {
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
			RenderSystem.ortho(
				0.0, (double)window.getFramebufferWidth() / window.getScaleFactor(), (double)window.getFramebufferHeight() / window.getScaleFactor(), 0.0, 1000.0, 3000.0
			);
			RenderSystem.matrixMode(5888);
			RenderSystem.loadIdentity();
			RenderSystem.translatef(0.0F, 0.0F, -2000.0F);
			GuiLighting.enableForItems();
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
				} catch (Throwable var15) {
					CrashReport crashReport = CrashReport.create(var15, "Rendering overlay");
					CrashReportSection crashReportSection = crashReport.addElement("Overlay render details");
					crashReportSection.add("Overlay name", (CrashCallable<String>)(() -> this.client.overlay.getClass().getCanonicalName()));
					throw new CrashException(crashReport);
				}
			} else if (this.client.currentScreen != null) {
				try {
					this.client.currentScreen.render(i, j, this.client.getLastFrameDuration());
				} catch (Throwable var14) {
					CrashReport crashReport = CrashReport.create(var14, "Rendering screen");
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
			}
		}
	}

	private void updateWorldIcon() {
		if (this.client.worldRenderer.getCompletedChunkCount() > 10 && this.client.worldRenderer.isTerrainRenderComplete() && !this.client.getServer().hasIconFile()) {
			NativeImage nativeImage = ScreenshotUtils.takeScreenshot(
				this.client.getWindow().getFramebufferWidth(), this.client.getWindow().getFramebufferHeight(), this.client.getFramebuffer()
			);
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

				try (NativeImage nativeImage2 = new NativeImage(64, 64, false)) {
					nativeImage.resizeSubRectTo(k, l, i, j, nativeImage2);
					nativeImage2.writeFile(this.client.getServer().getIconFile());
				} catch (IOException var27) {
					LOGGER.warn("Couldn't save auto screenshot", (Throwable)var27);
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
			if (bl && !((PlayerEntity)entity).abilities.allowModifyWorld) {
				ItemStack itemStack = ((LivingEntity)entity).getMainHandStack();
				HitResult hitResult = this.client.hitResult;
				if (hitResult != null && hitResult.getType() == HitResult.Type.BLOCK) {
					BlockPos blockPos = ((BlockHitResult)hitResult).getBlockPos();
					BlockState blockState = this.client.world.getBlockState(blockPos);
					if (this.client.interactionManager.getCurrentGameMode() == GameMode.SPECTATOR) {
						bl = blockState.createContainerProvider(this.client.world, blockPos) != null;
					} else {
						CachedBlockPosition cachedBlockPosition = new CachedBlockPosition(this.client.world, blockPos, false);
						bl = !itemStack.isEmpty()
							&& (
								itemStack.canDestroy(this.client.world.getTagManager(), cachedBlockPosition)
									|| itemStack.canPlaceOn(this.client.world.getTagManager(), cachedBlockPosition)
							);
					}
				}
			}

			return bl;
		}
	}

	public void renderWorld(float f, long l, MatrixStack matrixStack) {
		this.lightmapTextureManager.update(f);
		if (this.client.getCameraEntity() == null) {
			this.client.setCameraEntity(this.client.player);
		}

		this.updateTargetedEntity(f);
		this.client.getProfiler().push("center");
		boolean bl = this.shouldRenderBlockOutline();
		this.client.getProfiler().swap("camera");
		Camera camera = this.camera;
		this.viewDistance = (float)(this.client.options.viewDistance * 16);
		this.method_22709(camera, f, true, true, MathHelper.SQUARE_ROOT_OF_TWO);
		this.bobViewWhenHurt(matrixStack, f);
		if (this.client.options.bobView) {
			this.bobView(matrixStack, f);
		}

		float g = MathHelper.lerp(f, this.client.player.lastNauseaStrength, this.client.player.nextNauseaStrength);
		if (g > 0.0F) {
			int i = 20;
			if (this.client.player.hasStatusEffect(StatusEffects.NAUSEA)) {
				i = 7;
			}

			float h = 5.0F / (g * g + 5.0F) - g * 0.04F;
			h *= h;
			Vector3f vector3f = new Vector3f(0.0F, 1.0F, 1.0F);
			matrixStack.multiply(vector3f.getRotationQuaternion(((float)this.ticks + f) * (float)i, true));
			matrixStack.scale(1.0F / h, 1.0F, 1.0F);
			matrixStack.multiply(vector3f.getRotationQuaternion(-((float)this.ticks + f) * (float)i, true));
		}

		camera.update(
			this.client.world,
			(Entity)(this.client.getCameraEntity() == null ? this.client.player : this.client.getCameraEntity()),
			this.client.options.perspective > 0,
			this.client.options.perspective == 2,
			f
		);
		matrixStack.multiply(Vector3f.POSITIVE_X.getRotationQuaternion(camera.getPitch(), true));
		matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion(camera.getYaw() + 180.0F, true));
		this.client.worldRenderer.render(matrixStack, f, l, bl, camera, this, this.lightmapTextureManager);
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
		this.floatingItemWidth = this.random.nextFloat() * 2.0F - 1.0F;
		this.floatingItemHeight = this.random.nextFloat() * 2.0F - 1.0F;
	}

	private void renderFloatingItem(int i, int j, float f) {
		if (this.floatingItem != null && this.floatingItemTimeLeft > 0) {
			int k = 40 - this.floatingItemTimeLeft;
			float g = ((float)k + f) / 40.0F;
			float h = g * g;
			float l = g * h;
			float m = 10.25F * l * h - 24.95F * h * h + 25.5F * l - 13.8F * h + 4.0F * g;
			float n = m * (float) Math.PI;
			float o = this.floatingItemWidth * (float)(i / 4);
			float p = this.floatingItemHeight * (float)(j / 4);
			RenderSystem.enableAlphaTest();
			RenderSystem.pushMatrix();
			RenderSystem.pushLightingAttributes();
			RenderSystem.enableDepthTest();
			RenderSystem.disableCull();
			MatrixStack matrixStack = new MatrixStack();
			matrixStack.push();
			matrixStack.translate(
				(double)((float)(i / 2) + o * MathHelper.abs(MathHelper.sin(n * 2.0F))), (double)((float)(j / 2) + p * MathHelper.abs(MathHelper.sin(n * 2.0F))), -50.0
			);
			float q = 50.0F + 175.0F * MathHelper.sin(n);
			matrixStack.scale(q, -q, q);
			matrixStack.scale(q, -q, q);
			matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion(900.0F * MathHelper.abs(MathHelper.sin(n)), true));
			matrixStack.multiply(Vector3f.POSITIVE_X.getRotationQuaternion(6.0F * MathHelper.cos(g * 8.0F), true));
			matrixStack.multiply(Vector3f.POSITIVE_Z.getRotationQuaternion(6.0F * MathHelper.cos(g * 8.0F), true));
			LayeredVertexConsumerStorage.class_4598 lv = LayeredVertexConsumerStorage.method_22991(Tessellator.getInstance().getBufferBuilder());
			this.client.getItemRenderer().method_23178(this.floatingItem, ModelTransformation.Type.FIXED, 15728880, matrixStack, lv);
			matrixStack.pop();
			lv.method_22993();
			RenderSystem.popAttributes();
			RenderSystem.popMatrix();
			RenderSystem.enableCull();
			RenderSystem.disableDepthTest();
		}
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
		return this.field_20949;
	}
}
