package net.minecraft.client.render;

import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import java.io.IOException;
import java.util.Locale;
import java.util.Random;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1675;
import net.minecraft.class_4184;
import net.minecraft.block.BlockRenderLayer;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.GlProgramManager;
import net.minecraft.client.gl.ShaderEffect;
import net.minecraft.client.gui.MapRenderer;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.options.CloudRenderMode;
import net.minecraft.client.options.ParticlesOption;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.ScreenshotUtils;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.mob.SpiderEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.resource.ResourceImpl;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SynchronousResourceReloadListener;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.GameMode;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.Heightmap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class GameRenderer implements AutoCloseable, SynchronousResourceReloadListener {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Identifier field_4011 = new Identifier("textures/environment/rain.png");
	private static final Identifier field_4008 = new Identifier("textures/environment/snow.png");
	private final MinecraftClient client;
	private final ResourceManager field_4018;
	private final Random random = new Random();
	private float viewDistance;
	public final FirstPersonRenderer field_4012;
	private final MapRenderer mapRenderer;
	private int field_4027;
	private float field_4019;
	private float field_3999;
	private float field_4002;
	private float tickStartSkyDarkness;
	private boolean field_3992 = true;
	private boolean blockOutlineEnabled = true;
	private long lastWorldIconUpdate;
	private long lastRenderTime = SystemUtil.getMeasuringTimeMs();
	private final LightmapTextureManager field_4028;
	private int field_3995;
	private final float[] field_3991 = new float[1024];
	private final float[] field_3989 = new float[1024];
	private final BackgroundRenderer backgroundRenderer;
	private boolean field_4001;
	private double field_4005 = 1.0;
	private double field_3988;
	private double field_4004;
	private ItemStack floatingItem;
	private int floatingItemTimeLeft;
	private float floatingItemWidth;
	private float floatingItemHeight;
	private ShaderEffect field_4024;
	private static final Identifier[] field_3996 = new Identifier[]{
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
	public static final int SHADER_COUNT = field_3996.length;
	private int forcedShaderIndex = SHADER_COUNT;
	private boolean shadersEnabled;
	private int field_4021;
	private final class_4184 field_18765 = new class_4184();

	public GameRenderer(MinecraftClient minecraftClient, ResourceManager resourceManager) {
		this.client = minecraftClient;
		this.field_4018 = resourceManager;
		this.field_4012 = minecraftClient.method_1489();
		this.mapRenderer = new MapRenderer(minecraftClient.method_1531());
		this.field_4028 = new LightmapTextureManager(this);
		this.backgroundRenderer = new BackgroundRenderer(this);
		this.field_4024 = null;

		for (int i = 0; i < 32; i++) {
			for (int j = 0; j < 32; j++) {
				float f = (float)(j - 16);
				float g = (float)(i - 16);
				float h = MathHelper.sqrt(f * f + g * g);
				this.field_3991[i << 5 | j] = -g / h;
				this.field_3989[i << 5 | j] = f / h;
			}
		}
	}

	public void close() {
		this.field_4028.close();
		this.mapRenderer.close();
		this.disableShader();
	}

	public boolean method_3175() {
		return GLX.usePostProcess && this.field_4024 != null;
	}

	public void disableShader() {
		if (this.field_4024 != null) {
			this.field_4024.close();
		}

		this.field_4024 = null;
		this.forcedShaderIndex = SHADER_COUNT;
	}

	public void toggleShadersEnabled() {
		this.shadersEnabled = !this.shadersEnabled;
	}

	public void onCameraEntitySet(@Nullable Entity entity) {
		if (GLX.usePostProcess) {
			if (this.field_4024 != null) {
				this.field_4024.close();
			}

			this.field_4024 = null;
			if (entity instanceof CreeperEntity) {
				this.method_3168(new Identifier("shaders/post/creeper.json"));
			} else if (entity instanceof SpiderEntity) {
				this.method_3168(new Identifier("shaders/post/spider.json"));
			} else if (entity instanceof EndermanEntity) {
				this.method_3168(new Identifier("shaders/post/invert.json"));
			}
		}
	}

	private void method_3168(Identifier identifier) {
		if (this.field_4024 != null) {
			this.field_4024.close();
		}

		try {
			this.field_4024 = new ShaderEffect(this.client.method_1531(), this.field_4018, this.client.getFramebuffer(), identifier);
			this.field_4024.setupDimensions(this.client.window.getFramebufferWidth(), this.client.window.getFramebufferHeight());
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
		if (this.field_4024 != null) {
			this.field_4024.close();
		}

		this.field_4024 = null;
		if (this.forcedShaderIndex == SHADER_COUNT) {
			this.onCameraEntitySet(this.client.getCameraEntity());
		} else {
			this.method_3168(field_3996[this.forcedShaderIndex]);
		}
	}

	public void tick() {
		if (GLX.usePostProcess && GlProgramManager.getInstance() == null) {
			GlProgramManager.init();
		}

		this.method_3199();
		this.field_4028.tick();
		if (this.client.getCameraEntity() == null) {
			this.client.setCameraEntity(this.client.field_1724);
		}

		this.field_18765.method_19317();
		this.field_4027++;
		this.field_4012.updateHeldItems();
		this.method_3177();
		this.tickStartSkyDarkness = this.field_4002;
		if (this.client.field_1705.method_1740().shouldDarkenSky()) {
			this.field_4002 += 0.05F;
			if (this.field_4002 > 1.0F) {
				this.field_4002 = 1.0F;
			}
		} else if (this.field_4002 > 0.0F) {
			this.field_4002 -= 0.0125F;
		}

		if (this.floatingItemTimeLeft > 0) {
			this.floatingItemTimeLeft--;
			if (this.floatingItemTimeLeft == 0) {
				this.floatingItem = null;
			}
		}
	}

	public ShaderEffect method_3183() {
		return this.field_4024;
	}

	public void onResized(int i, int j) {
		if (GLX.usePostProcess) {
			if (this.field_4024 != null) {
				this.field_4024.setupDimensions(i, j);
			}

			this.client.field_1769.onResized(i, j);
		}
	}

	public void updateTargetedEntity(float f) {
		Entity entity = this.client.getCameraEntity();
		if (entity != null) {
			if (this.client.field_1687 != null) {
				this.client.getProfiler().push("pick");
				this.client.targetedEntity = null;
				double d = (double)this.client.field_1761.getReachDistance();
				this.client.hitResult = entity.method_5745(d, f, false);
				net.minecraft.util.math.Vec3d vec3d = entity.method_5836(f);
				boolean bl = false;
				int i = 3;
				double e = d;
				if (this.client.field_1761.hasExtendedReach()) {
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
					e = this.client.hitResult.method_17784().squaredDistanceTo(vec3d);
				}

				net.minecraft.util.math.Vec3d vec3d2 = entity.method_5828(1.0F);
				net.minecraft.util.math.Vec3d vec3d3 = vec3d.add(vec3d2.x * d, vec3d2.y * d, vec3d2.z * d);
				float g = 1.0F;
				BoundingBox boundingBox = entity.method_5829().method_18804(vec3d2.multiply(d)).expand(1.0, 1.0, 1.0);
				EntityHitResult entityHitResult = class_1675.method_18075(entity, vec3d, vec3d3, boundingBox, entityx -> !entityx.isSpectator() && entityx.doesCollide(), e);
				if (entityHitResult != null) {
					Entity entity2 = entityHitResult.getEntity();
					net.minecraft.util.math.Vec3d vec3d4 = entityHitResult.method_17784();
					double h = vec3d.squaredDistanceTo(vec3d4);
					if (bl && h > 9.0) {
						this.client.hitResult = BlockHitResult.method_17778(vec3d4, Direction.getFacing(vec3d2.x, vec3d2.y, vec3d2.z), new BlockPos(vec3d4));
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

	private void method_3199() {
		float f = 1.0F;
		if (this.client.getCameraEntity() instanceof AbstractClientPlayerEntity) {
			AbstractClientPlayerEntity abstractClientPlayerEntity = (AbstractClientPlayerEntity)this.client.getCameraEntity();
			f = abstractClientPlayerEntity.method_3118();
		}

		this.field_3999 = this.field_4019;
		this.field_4019 = this.field_4019 + (f - this.field_4019) * 0.5F;
		if (this.field_4019 > 1.5F) {
			this.field_4019 = 1.5F;
		}

		if (this.field_4019 < 0.1F) {
			this.field_4019 = 0.1F;
		}
	}

	private double method_3196(class_4184 arg, float f, boolean bl) {
		if (this.field_4001) {
			return 90.0;
		} else {
			double d = 70.0;
			if (bl) {
				d = this.client.field_1690.fov;
				d *= (double)MathHelper.lerp(f, this.field_3999, this.field_4019);
			}

			if (arg.method_19331() instanceof LivingEntity && ((LivingEntity)arg.method_19331()).getHealth() <= 0.0F) {
				float g = (float)((LivingEntity)arg.method_19331()).deathCounter + f;
				d /= (double)((1.0F - 500.0F / (g + 500.0F)) * 2.0F + 1.0F);
			}

			FluidState fluidState = arg.method_19334();
			if (!fluidState.isEmpty()) {
				d = d * 60.0 / 70.0;
			}

			return d;
		}
	}

	private void method_3198(float f) {
		if (this.client.getCameraEntity() instanceof LivingEntity) {
			LivingEntity livingEntity = (LivingEntity)this.client.getCameraEntity();
			float g = (float)livingEntity.hurtTime - f;
			if (livingEntity.getHealth() <= 0.0F) {
				float h = (float)livingEntity.deathCounter + f;
				GlStateManager.rotatef(40.0F - 8000.0F / (h + 200.0F), 0.0F, 0.0F, 1.0F);
			}

			if (g < 0.0F) {
				return;
			}

			g /= (float)livingEntity.field_6254;
			g = MathHelper.sin(g * g * g * g * (float) Math.PI);
			float h = livingEntity.field_6271;
			GlStateManager.rotatef(-h, 0.0F, 1.0F, 0.0F);
			GlStateManager.rotatef(-g * 14.0F, 0.0F, 0.0F, 1.0F);
			GlStateManager.rotatef(h, 0.0F, 1.0F, 0.0F);
		}
	}

	private void method_3186(float f) {
		if (this.client.getCameraEntity() instanceof PlayerEntity) {
			PlayerEntity playerEntity = (PlayerEntity)this.client.getCameraEntity();
			float g = playerEntity.field_5973 - playerEntity.field_6039;
			float h = -(playerEntity.field_5973 + g * f);
			float i = MathHelper.lerp(f, playerEntity.field_7505, playerEntity.field_7483);
			float j = MathHelper.lerp(f, playerEntity.field_6286, playerEntity.field_6223);
			GlStateManager.translatef(MathHelper.sin(h * (float) Math.PI) * i * 0.5F, -Math.abs(MathHelper.cos(h * (float) Math.PI) * i), 0.0F);
			GlStateManager.rotatef(MathHelper.sin(h * (float) Math.PI) * i * 3.0F, 0.0F, 0.0F, 1.0F);
			GlStateManager.rotatef(Math.abs(MathHelper.cos(h * (float) Math.PI - 0.2F) * i) * 5.0F, 1.0F, 0.0F, 0.0F);
			GlStateManager.rotatef(j, 1.0F, 0.0F, 0.0F);
		}
	}

	private void method_3185(float f) {
		this.viewDistance = (float)(this.client.field_1690.viewDistance * 16);
		GlStateManager.matrixMode(5889);
		GlStateManager.loadIdentity();
		if (this.field_4005 != 1.0) {
			GlStateManager.translatef((float)this.field_3988, (float)(-this.field_4004), 0.0F);
			GlStateManager.scaled(this.field_4005, this.field_4005, 1.0);
		}

		GlStateManager.multMatrix(
			Matrix4f.method_4929(
				this.method_3196(this.field_18765, f, true),
				(float)this.client.window.getFramebufferWidth() / (float)this.client.window.getFramebufferHeight(),
				0.05F,
				this.viewDistance * MathHelper.SQUARE_ROOT_OF_TWO
			)
		);
		GlStateManager.matrixMode(5888);
		GlStateManager.loadIdentity();
		this.method_3198(f);
		if (this.client.field_1690.bobView) {
			this.method_3186(f);
		}

		float g = MathHelper.lerp(f, this.client.field_1724.field_3911, this.client.field_1724.field_3929);
		if (g > 0.0F) {
			int i = 20;
			if (this.client.field_1724.hasPotionEffect(StatusEffects.field_5916)) {
				i = 7;
			}

			float h = 5.0F / (g * g + 5.0F) - g * 0.04F;
			h *= h;
			GlStateManager.rotatef(((float)this.field_4027 + f) * (float)i, 0.0F, 1.0F, 1.0F);
			GlStateManager.scalef(1.0F / h, 1.0F, 1.0F);
			GlStateManager.rotatef(-((float)this.field_4027 + f) * (float)i, 0.0F, 1.0F, 1.0F);
		}
	}

	private void method_3172(class_4184 arg, float f) {
		if (!this.field_4001) {
			GlStateManager.matrixMode(5889);
			GlStateManager.loadIdentity();
			GlStateManager.multMatrix(
				Matrix4f.method_4929(
					this.method_3196(arg, f, false),
					(float)this.client.window.getFramebufferWidth() / (float)this.client.window.getFramebufferHeight(),
					0.05F,
					this.viewDistance * 2.0F
				)
			);
			GlStateManager.matrixMode(5888);
			GlStateManager.loadIdentity();
			GlStateManager.pushMatrix();
			this.method_3198(f);
			if (this.client.field_1690.bobView) {
				this.method_3186(f);
			}

			boolean bl = this.client.getCameraEntity() instanceof LivingEntity && ((LivingEntity)this.client.getCameraEntity()).isSleeping();
			if (this.client.field_1690.perspective == 0
				&& !bl
				&& !this.client.field_1690.hudHidden
				&& this.client.field_1761.getCurrentGameMode() != GameMode.field_9219) {
				this.enableLightmap();
				this.field_4012.renderFirstPersonItem(f);
				this.disableLightmap();
			}

			GlStateManager.popMatrix();
			if (this.client.field_1690.perspective == 0 && !bl) {
				this.field_4012.renderOverlays(f);
				this.method_3198(f);
			}

			if (this.client.field_1690.bobView) {
				this.method_3186(f);
			}
		}
	}

	public void disableLightmap() {
		this.field_4028.disable();
	}

	public void enableLightmap() {
		this.field_4028.enable();
	}

	public float method_3174(LivingEntity livingEntity, float f) {
		int i = livingEntity.getPotionEffect(StatusEffects.field_5925).getDuration();
		return i > 200 ? 1.0F : 0.7F + MathHelper.sin(((float)i - f) * (float) Math.PI * 0.2F) * 0.3F;
	}

	public void render(float f, long l, boolean bl) {
		if (!this.client.isWindowFocused()
			&& this.client.field_1690.pauseOnLostFocus
			&& (!this.client.field_1690.touchscreen || !this.client.field_1729.method_1609())) {
			if (SystemUtil.getMeasuringTimeMs() - this.lastRenderTime > 500L) {
				this.client.openPauseMenu();
			}
		} else {
			this.lastRenderTime = SystemUtil.getMeasuringTimeMs();
		}

		if (!this.client.skipGameRender) {
			int i = (int)(this.client.field_1729.getX() * (double)this.client.window.getScaledWidth() / (double)this.client.window.getWidth());
			int j = (int)(this.client.field_1729.getY() * (double)this.client.window.getScaledHeight() / (double)this.client.window.getHeight());
			int k = this.client.field_1690.maxFps;
			if (bl && this.client.field_1687 != null) {
				this.client.getProfiler().push("level");
				int m = Math.min(MinecraftClient.getCurrentFps(), k);
				m = Math.max(m, 60);
				long n = SystemUtil.getMeasuringTimeNano() - l;
				long o = Math.max((long)(1000000000 / m / 4) - n, 0L);
				this.renderWorld(f, SystemUtil.getMeasuringTimeNano() + o);
				if (this.client.isIntegratedServerRunning() && this.lastWorldIconUpdate < SystemUtil.getMeasuringTimeMs() - 1000L) {
					this.lastWorldIconUpdate = SystemUtil.getMeasuringTimeMs();
					if (!this.client.method_1576().hasIconFile()) {
						this.updateWorldIcon();
					}
				}

				if (GLX.usePostProcess) {
					this.client.field_1769.drawEntityOutlinesFramebuffer();
					if (this.field_4024 != null && this.shadersEnabled) {
						GlStateManager.matrixMode(5890);
						GlStateManager.pushMatrix();
						GlStateManager.loadIdentity();
						this.field_4024.render(f);
						GlStateManager.popMatrix();
					}

					this.client.getFramebuffer().beginWrite(true);
				}

				this.client.getProfiler().swap("gui");
				if (!this.client.field_1690.hudHidden || this.client.field_1755 != null) {
					GlStateManager.alphaFunc(516, 0.1F);
					this.client.window.method_4493(MinecraftClient.IS_SYSTEM_MAC);
					this.renderFloatingItem(this.client.window.getScaledWidth(), this.client.window.getScaledHeight(), f);
					this.client.field_1705.draw(f);
				}

				this.client.getProfiler().pop();
			} else {
				GlStateManager.viewport(0, 0, this.client.window.getFramebufferWidth(), this.client.window.getFramebufferHeight());
				GlStateManager.matrixMode(5889);
				GlStateManager.loadIdentity();
				GlStateManager.matrixMode(5888);
				GlStateManager.loadIdentity();
				this.client.window.method_4493(MinecraftClient.IS_SYSTEM_MAC);
			}

			if (this.client.field_18175 != null) {
				GlStateManager.clear(256, MinecraftClient.IS_SYSTEM_MAC);

				try {
					this.client.field_18175.draw(i, j, this.client.getLastFrameDuration());
				} catch (Throwable var14) {
					CrashReport crashReport = CrashReport.create(var14, "Rendering overlay");
					CrashReportSection crashReportSection = crashReport.method_562("Overlay render details");
					crashReportSection.method_577("Overlay name", () -> this.client.field_18175.getClass().getCanonicalName());
					throw new CrashException(crashReport);
				}
			} else if (this.client.field_1755 != null) {
				GlStateManager.clear(256, MinecraftClient.IS_SYSTEM_MAC);

				try {
					this.client.field_1755.draw(i, j, this.client.getLastFrameDuration());
				} catch (Throwable var13) {
					CrashReport crashReport = CrashReport.create(var13, "Rendering screen");
					CrashReportSection crashReportSection = crashReport.method_562("Screen render details");
					crashReportSection.method_577("Screen name", () -> this.client.field_1755.getClass().getCanonicalName());
					crashReportSection.method_577(
						"Mouse location",
						() -> String.format(Locale.ROOT, "Scaled: (%d, %d). Absolute: (%f, %f)", i, j, this.client.field_1729.getX(), this.client.field_1729.getY())
					);
					crashReportSection.method_577(
						"Screen size",
						() -> String.format(
								Locale.ROOT,
								"Scaled: (%d, %d). Absolute: (%d, %d). Scale factor of %f",
								this.client.window.getScaledWidth(),
								this.client.window.getScaledHeight(),
								this.client.window.getFramebufferWidth(),
								this.client.window.getFramebufferHeight(),
								this.client.window.getScaleFactor()
							)
					);
					throw new CrashException(crashReport);
				}
			}
		}
	}

	private void updateWorldIcon() {
		if (this.client.field_1769.getChunkNumber() > 10 && this.client.field_1769.method_3281() && !this.client.method_1576().hasIconFile()) {
			NativeImage nativeImage = ScreenshotUtils.method_1663(
				this.client.window.getFramebufferWidth(), this.client.window.getFramebufferHeight(), this.client.getFramebuffer()
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
					nativeImage2.writeFile(this.client.method_1576().getIconFile());
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
			boolean bl = entity instanceof PlayerEntity && !this.client.field_1690.hudHidden;
			if (bl && !((PlayerEntity)entity).abilities.allowModifyWorld) {
				ItemStack itemStack = ((LivingEntity)entity).method_6047();
				HitResult hitResult = this.client.hitResult;
				if (hitResult != null && hitResult.getType() == HitResult.Type.BLOCK) {
					BlockPos blockPos = ((BlockHitResult)hitResult).method_17777();
					BlockState blockState = this.client.field_1687.method_8320(blockPos);
					if (this.client.field_1761.getCurrentGameMode() == GameMode.field_9219) {
						bl = blockState.method_17526(this.client.field_1687, blockPos) != null;
					} else {
						CachedBlockPosition cachedBlockPosition = new CachedBlockPosition(this.client.field_1687, blockPos, false);
						bl = !itemStack.isEmpty()
							&& (
								itemStack.method_7940(this.client.field_1687.method_8514(), cachedBlockPosition)
									|| itemStack.method_7944(this.client.field_1687.method_8514(), cachedBlockPosition)
							);
					}
				}
			}

			return bl;
		}
	}

	public void renderWorld(float f, long l) {
		this.field_4028.update(f);
		if (this.client.getCameraEntity() == null) {
			this.client.setCameraEntity(this.client.field_1724);
		}

		this.updateTargetedEntity(f);
		GlStateManager.enableDepthTest();
		GlStateManager.enableAlphaTest();
		GlStateManager.alphaFunc(516, 0.5F);
		this.client.getProfiler().push("center");
		this.renderCenter(f, l);
		this.client.getProfiler().pop();
	}

	private void renderCenter(float f, long l) {
		WorldRenderer worldRenderer = this.client.field_1769;
		ParticleManager particleManager = this.client.field_1713;
		boolean bl = this.shouldRenderBlockOutline();
		GlStateManager.enableCull();
		this.client.getProfiler().swap("camera");
		this.method_3185(f);
		class_4184 lv = this.field_18765;
		lv.method_19321(
			this.client.field_1687,
			(Entity)(this.client.getCameraEntity() == null ? this.client.field_1724 : this.client.getCameraEntity()),
			this.client.field_1690.perspective > 0,
			this.client.field_1690.perspective == 2,
			f
		);
		Frustum frustum = GlMatrixFrustum.method_3696();
		this.client.getProfiler().swap("clear");
		GlStateManager.viewport(0, 0, this.client.window.getFramebufferWidth(), this.client.window.getFramebufferHeight());
		this.backgroundRenderer.renderBackground(lv, f);
		GlStateManager.clear(16640, MinecraftClient.IS_SYSTEM_MAC);
		this.client.getProfiler().swap("culling");
		VisibleRegion visibleRegion = new FrustumWithOrigin(frustum);
		double d = lv.method_19326().x;
		double e = lv.method_19326().y;
		double g = lv.method_19326().z;
		visibleRegion.setOrigin(d, e, g);
		if (this.client.field_1690.viewDistance >= 4) {
			this.backgroundRenderer.applyFog(lv, -1);
			this.client.getProfiler().swap("sky");
			GlStateManager.matrixMode(5889);
			GlStateManager.loadIdentity();
			GlStateManager.multMatrix(
				Matrix4f.method_4929(
					this.method_3196(lv, f, true),
					(float)this.client.window.getFramebufferWidth() / (float)this.client.window.getFramebufferHeight(),
					0.05F,
					this.viewDistance * 2.0F
				)
			);
			GlStateManager.matrixMode(5888);
			worldRenderer.renderSky(f);
			GlStateManager.matrixMode(5889);
			GlStateManager.loadIdentity();
			GlStateManager.multMatrix(
				Matrix4f.method_4929(
					this.method_3196(lv, f, true),
					(float)this.client.window.getFramebufferWidth() / (float)this.client.window.getFramebufferHeight(),
					0.05F,
					this.viewDistance * MathHelper.SQUARE_ROOT_OF_TWO
				)
			);
			GlStateManager.matrixMode(5888);
		}

		this.backgroundRenderer.applyFog(lv, 0);
		GlStateManager.shadeModel(7425);
		if (lv.method_19326().y < 128.0) {
			this.method_3206(lv, worldRenderer, f, d, e, g);
		}

		this.client.getProfiler().swap("prepareterrain");
		this.backgroundRenderer.applyFog(lv, 0);
		this.client.method_1531().method_4618(SpriteAtlasTexture.field_5275);
		GuiLighting.disable();
		this.client.getProfiler().swap("terrain_setup");
		this.client.field_1687.method_2935().method_12130().doLightUpdates(Integer.MAX_VALUE, true, true);
		worldRenderer.method_3273(lv, visibleRegion, this.field_4021++, this.client.field_1724.isSpectator());
		this.client.getProfiler().swap("updatechunks");
		this.client.field_1769.updateChunks(l);
		this.client.getProfiler().swap("terrain");
		GlStateManager.matrixMode(5888);
		GlStateManager.pushMatrix();
		GlStateManager.disableAlphaTest();
		worldRenderer.renderLayer(BlockRenderLayer.SOLID, lv);
		GlStateManager.enableAlphaTest();
		worldRenderer.renderLayer(BlockRenderLayer.MIPPED_CUTOUT, lv);
		this.client.method_1531().method_4619(SpriteAtlasTexture.field_5275).pushFilter(false, false);
		worldRenderer.renderLayer(BlockRenderLayer.CUTOUT, lv);
		this.client.method_1531().method_4619(SpriteAtlasTexture.field_5275).popFilter();
		GlStateManager.shadeModel(7424);
		GlStateManager.alphaFunc(516, 0.1F);
		GlStateManager.matrixMode(5888);
		GlStateManager.popMatrix();
		GlStateManager.pushMatrix();
		GuiLighting.enable();
		this.client.getProfiler().swap("entities");
		worldRenderer.method_3271(lv, visibleRegion, f);
		GuiLighting.disable();
		this.disableLightmap();
		GlStateManager.matrixMode(5888);
		GlStateManager.popMatrix();
		if (bl && this.client.hitResult != null) {
			GlStateManager.disableAlphaTest();
			this.client.getProfiler().swap("outline");
			worldRenderer.drawHighlightedBlockOutline(lv, this.client.hitResult, 0);
			GlStateManager.enableAlphaTest();
		}

		if (this.client.field_1709.shouldRender()) {
			this.client.field_1709.renderDebuggers(l);
		}

		this.client.getProfiler().swap("destroyProgress");
		GlStateManager.enableBlend();
		GlStateManager.blendFuncSeparate(
			GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO
		);
		this.client.method_1531().method_4619(SpriteAtlasTexture.field_5275).pushFilter(false, false);
		worldRenderer.renderPartiallyBrokenBlocks(Tessellator.getInstance(), Tessellator.getInstance().getBufferBuilder(), lv);
		this.client.method_1531().method_4619(SpriteAtlasTexture.field_5275).popFilter();
		GlStateManager.disableBlend();
		this.enableLightmap();
		this.backgroundRenderer.applyFog(lv, 0);
		this.client.getProfiler().swap("particles");
		particleManager.renderUnlitParticles(lv, f);
		this.disableLightmap();
		GlStateManager.depthMask(false);
		GlStateManager.enableCull();
		this.client.getProfiler().swap("weather");
		this.method_3170(f);
		GlStateManager.depthMask(true);
		worldRenderer.renderWorldBorder(lv, f);
		GlStateManager.disableBlend();
		GlStateManager.enableCull();
		GlStateManager.blendFuncSeparate(
			GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO
		);
		GlStateManager.alphaFunc(516, 0.1F);
		this.backgroundRenderer.applyFog(lv, 0);
		GlStateManager.enableBlend();
		GlStateManager.depthMask(false);
		this.client.method_1531().method_4618(SpriteAtlasTexture.field_5275);
		GlStateManager.shadeModel(7425);
		this.client.getProfiler().swap("translucent");
		worldRenderer.renderLayer(BlockRenderLayer.TRANSLUCENT, lv);
		GlStateManager.shadeModel(7424);
		GlStateManager.depthMask(true);
		GlStateManager.enableCull();
		GlStateManager.disableBlend();
		GlStateManager.disableFog();
		if (lv.method_19326().y >= 128.0) {
			this.client.getProfiler().swap("aboveClouds");
			this.method_3206(lv, worldRenderer, f, d, e, g);
		}

		this.client.getProfiler().swap("hand");
		if (this.field_3992) {
			GlStateManager.clear(256, MinecraftClient.IS_SYSTEM_MAC);
			this.method_3172(lv, f);
		}
	}

	private void method_3206(class_4184 arg, WorldRenderer worldRenderer, float f, double d, double e, double g) {
		if (this.client.field_1690.getCloudRenderMode() != CloudRenderMode.field_18162) {
			this.client.getProfiler().swap("clouds");
			GlStateManager.matrixMode(5889);
			GlStateManager.loadIdentity();
			GlStateManager.multMatrix(
				Matrix4f.method_4929(
					this.method_3196(arg, f, true),
					(float)this.client.window.getFramebufferWidth() / (float)this.client.window.getFramebufferHeight(),
					0.05F,
					this.viewDistance * 4.0F
				)
			);
			GlStateManager.matrixMode(5888);
			GlStateManager.pushMatrix();
			this.backgroundRenderer.applyFog(arg, 0);
			worldRenderer.renderClouds(f, d, e, g);
			GlStateManager.disableFog();
			GlStateManager.popMatrix();
			GlStateManager.matrixMode(5889);
			GlStateManager.loadIdentity();
			GlStateManager.multMatrix(
				Matrix4f.method_4929(
					this.method_3196(arg, f, true),
					(float)this.client.window.getFramebufferWidth() / (float)this.client.window.getFramebufferHeight(),
					0.05F,
					this.viewDistance * MathHelper.SQUARE_ROOT_OF_TWO
				)
			);
			GlStateManager.matrixMode(5888);
		}
	}

	private void method_3177() {
		float f = this.client.field_1687.getRainGradient(1.0F);
		if (!this.client.field_1690.fancyGraphics) {
			f /= 2.0F;
		}

		if (f != 0.0F) {
			this.random.setSeed((long)this.field_4027 * 312987231L);
			ViewableWorld viewableWorld = this.client.field_1687;
			BlockPos blockPos = new BlockPos(this.field_18765.method_19326());
			int i = 10;
			double d = 0.0;
			double e = 0.0;
			double g = 0.0;
			int j = 0;
			int k = (int)(100.0F * f * f);
			if (this.client.field_1690.field_1882 == ParticlesOption.field_18198) {
				k >>= 1;
			} else if (this.client.field_1690.field_1882 == ParticlesOption.field_18199) {
				k = 0;
			}

			for (int l = 0; l < k; l++) {
				BlockPos blockPos2 = viewableWorld.method_8598(
					Heightmap.Type.MOTION_BLOCKING, blockPos.add(this.random.nextInt(10) - this.random.nextInt(10), 0, this.random.nextInt(10) - this.random.nextInt(10))
				);
				Biome biome = viewableWorld.method_8310(blockPos2);
				BlockPos blockPos3 = blockPos2.down();
				if (blockPos2.getY() <= blockPos.getY() + 10
					&& blockPos2.getY() >= blockPos.getY() - 10
					&& biome.getPrecipitation() == Biome.Precipitation.RAIN
					&& biome.method_8707(blockPos2) >= 0.15F) {
					double h = this.random.nextDouble();
					double m = this.random.nextDouble();
					BlockState blockState = viewableWorld.method_8320(blockPos3);
					FluidState fluidState = viewableWorld.method_8316(blockPos2);
					VoxelShape voxelShape = blockState.method_11628(viewableWorld, blockPos3);
					double n = voxelShape.method_1102(Direction.Axis.Y, h, m);
					double o = (double)fluidState.method_15763(viewableWorld, blockPos2);
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
						if (!fluidState.method_15767(FluidTags.field_15518)
							&& blockState.getBlock() != Blocks.field_10092
							&& (blockState.getBlock() != Blocks.field_17350 || !(Boolean)blockState.method_11654(CampfireBlock.field_17352))) {
							if (this.random.nextInt(++j) == 0) {
								d = (double)blockPos3.getX() + h;
								e = (double)((float)blockPos3.getY() + 0.1F) + p - 1.0;
								g = (double)blockPos3.getZ() + m;
							}

							this.client
								.field_1687
								.method_8406(
									ParticleTypes.field_11242, (double)blockPos3.getX() + h, (double)((float)blockPos3.getY() + 0.1F) + p, (double)blockPos3.getZ() + m, 0.0, 0.0, 0.0
								);
						} else {
							this.client
								.field_1687
								.method_8406(
									ParticleTypes.field_11251, (double)blockPos2.getX() + h, (double)((float)blockPos2.getY() + 0.1F) - q, (double)blockPos2.getZ() + m, 0.0, 0.0, 0.0
								);
						}
					}
				}
			}

			if (j > 0 && this.random.nextInt(3) < this.field_3995++) {
				this.field_3995 = 0;
				if (e > (double)(blockPos.getY() + 1)
					&& viewableWorld.method_8598(Heightmap.Type.MOTION_BLOCKING, blockPos).getY() > MathHelper.floor((float)blockPos.getY())) {
					this.client.field_1687.method_8486(d, e, g, SoundEvents.field_15020, SoundCategory.field_15252, 0.1F, 0.5F, false);
				} else {
					this.client.field_1687.method_8486(d, e, g, SoundEvents.field_14946, SoundCategory.field_15252, 0.2F, 1.0F, false);
				}
			}
		}
	}

	protected void method_3170(float f) {
		float g = this.client.field_1687.getRainGradient(f);
		if (!(g <= 0.0F)) {
			this.enableLightmap();
			World world = this.client.field_1687;
			int i = MathHelper.floor(this.field_18765.method_19326().x);
			int j = MathHelper.floor(this.field_18765.method_19326().y);
			int k = MathHelper.floor(this.field_18765.method_19326().z);
			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder bufferBuilder = tessellator.getBufferBuilder();
			GlStateManager.disableCull();
			GlStateManager.normal3f(0.0F, 1.0F, 0.0F);
			GlStateManager.enableBlend();
			GlStateManager.blendFuncSeparate(
				GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO
			);
			GlStateManager.alphaFunc(516, 0.1F);
			double d = this.field_18765.method_19326().x;
			double e = this.field_18765.method_19326().y;
			double h = this.field_18765.method_19326().z;
			int l = MathHelper.floor(e);
			int m = 5;
			if (this.client.field_1690.fancyGraphics) {
				m = 10;
			}

			int n = -1;
			float o = (float)this.field_4027 + f;
			bufferBuilder.setOffset(-d, -e, -h);
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			BlockPos.Mutable mutable = new BlockPos.Mutable();

			for (int p = k - m; p <= k + m; p++) {
				for (int q = i - m; q <= i + m; q++) {
					int r = (p - k + 16) * 32 + q - i + 16;
					double s = (double)this.field_3991[r] * 0.5;
					double t = (double)this.field_3989[r] * 0.5;
					mutable.set(q, 0, p);
					Biome biome = world.method_8310(mutable);
					if (biome.getPrecipitation() != Biome.Precipitation.NONE) {
						int u = world.method_8598(Heightmap.Type.MOTION_BLOCKING, mutable).getY();
						int v = j - m;
						int w = j + m;
						if (v < u) {
							v = u;
						}

						if (w < u) {
							w = u;
						}

						int x = u;
						if (u < l) {
							x = l;
						}

						if (v != w) {
							this.random.setSeed((long)(q * q * 3121 + q * 45238971 ^ p * p * 418711 + p * 13761));
							mutable.set(q, v, p);
							float y = biome.method_8707(mutable);
							if (y >= 0.15F) {
								if (n != 0) {
									if (n >= 0) {
										tessellator.draw();
									}

									n = 0;
									this.client.method_1531().method_4618(field_4011);
									bufferBuilder.method_1328(7, VertexFormats.field_1584);
								}

								double z = -((double)(this.field_4027 + q * q * 3121 + q * 45238971 + p * p * 418711 + p * 13761 & 31) + (double)f)
									/ 32.0
									* (3.0 + this.random.nextDouble());
								double aa = (double)((float)q + 0.5F) - this.field_18765.method_19326().x;
								double ab = (double)((float)p + 0.5F) - this.field_18765.method_19326().z;
								float ac = MathHelper.sqrt(aa * aa + ab * ab) / (float)m;
								float ad = ((1.0F - ac * ac) * 0.5F + 0.5F) * g;
								mutable.set(q, x, p);
								int ae = world.method_8313(mutable, 0);
								int af = ae >> 16 & 65535;
								int ag = ae & 65535;
								bufferBuilder.vertex((double)q - s + 0.5, (double)w, (double)p - t + 0.5)
									.texture(0.0, (double)v * 0.25 + z)
									.color(1.0F, 1.0F, 1.0F, ad)
									.texture(af, ag)
									.next();
								bufferBuilder.vertex((double)q + s + 0.5, (double)w, (double)p + t + 0.5)
									.texture(1.0, (double)v * 0.25 + z)
									.color(1.0F, 1.0F, 1.0F, ad)
									.texture(af, ag)
									.next();
								bufferBuilder.vertex((double)q + s + 0.5, (double)v, (double)p + t + 0.5)
									.texture(1.0, (double)w * 0.25 + z)
									.color(1.0F, 1.0F, 1.0F, ad)
									.texture(af, ag)
									.next();
								bufferBuilder.vertex((double)q - s + 0.5, (double)v, (double)p - t + 0.5)
									.texture(0.0, (double)w * 0.25 + z)
									.color(1.0F, 1.0F, 1.0F, ad)
									.texture(af, ag)
									.next();
							} else {
								if (n != 1) {
									if (n >= 0) {
										tessellator.draw();
									}

									n = 1;
									this.client.method_1531().method_4618(field_4008);
									bufferBuilder.method_1328(7, VertexFormats.field_1584);
								}

								double z = (double)(-((float)(this.field_4027 & 511) + f) / 512.0F);
								double aa = this.random.nextDouble() + (double)o * 0.01 * (double)((float)this.random.nextGaussian());
								double ab = this.random.nextDouble() + (double)(o * (float)this.random.nextGaussian()) * 0.001;
								double ah = (double)((float)q + 0.5F) - this.field_18765.method_19326().x;
								double ai = (double)((float)p + 0.5F) - this.field_18765.method_19326().z;
								float aj = MathHelper.sqrt(ah * ah + ai * ai) / (float)m;
								float ak = ((1.0F - aj * aj) * 0.3F + 0.5F) * g;
								mutable.set(q, x, p);
								int al = (world.method_8313(mutable, 0) * 3 + 15728880) / 4;
								int am = al >> 16 & 65535;
								int an = al & 65535;
								bufferBuilder.vertex((double)q - s + 0.5, (double)w, (double)p - t + 0.5)
									.texture(0.0 + aa, (double)v * 0.25 + z + ab)
									.color(1.0F, 1.0F, 1.0F, ak)
									.texture(am, an)
									.next();
								bufferBuilder.vertex((double)q + s + 0.5, (double)w, (double)p + t + 0.5)
									.texture(1.0 + aa, (double)v * 0.25 + z + ab)
									.color(1.0F, 1.0F, 1.0F, ak)
									.texture(am, an)
									.next();
								bufferBuilder.vertex((double)q + s + 0.5, (double)v, (double)p + t + 0.5)
									.texture(1.0 + aa, (double)w * 0.25 + z + ab)
									.color(1.0F, 1.0F, 1.0F, ak)
									.texture(am, an)
									.next();
								bufferBuilder.vertex((double)q - s + 0.5, (double)v, (double)p - t + 0.5)
									.texture(0.0 + aa, (double)w * 0.25 + z + ab)
									.color(1.0F, 1.0F, 1.0F, ak)
									.texture(am, an)
									.next();
							}
						}
					}
				}
			}

			if (n >= 0) {
				tessellator.draw();
			}

			bufferBuilder.setOffset(0.0, 0.0, 0.0);
			GlStateManager.enableCull();
			GlStateManager.disableBlend();
			GlStateManager.alphaFunc(516, 0.1F);
			this.disableLightmap();
		}
	}

	public void method_3201(boolean bl) {
		this.backgroundRenderer.updateFogColor(bl);
	}

	public void method_3203() {
		this.floatingItem = null;
		this.mapRenderer.clearStateTextures();
		this.field_18765.method_19337();
	}

	public MapRenderer getMapRenderer() {
		return this.mapRenderer;
	}

	public static void method_3179(TextRenderer textRenderer, String string, float f, float g, float h, int i, float j, float k, boolean bl) {
		GlStateManager.pushMatrix();
		GlStateManager.translatef(f, g, h);
		GlStateManager.normal3f(0.0F, 1.0F, 0.0F);
		GlStateManager.rotatef(-j, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotatef(k, 1.0F, 0.0F, 0.0F);
		GlStateManager.scalef(-0.025F, -0.025F, 0.025F);
		GlStateManager.disableLighting();
		GlStateManager.depthMask(false);
		if (!bl) {
			GlStateManager.disableDepthTest();
		}

		GlStateManager.enableBlend();
		GlStateManager.blendFuncSeparate(
			GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO
		);
		int l = textRenderer.getStringWidth(string) / 2;
		GlStateManager.disableTexture();
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBufferBuilder();
		bufferBuilder.method_1328(7, VertexFormats.field_1576);
		float m = MinecraftClient.getInstance().field_1690.method_19343(0.25F);
		bufferBuilder.vertex((double)(-l - 1), (double)(-1 + i), 0.0).color(0.0F, 0.0F, 0.0F, m).next();
		bufferBuilder.vertex((double)(-l - 1), (double)(8 + i), 0.0).color(0.0F, 0.0F, 0.0F, m).next();
		bufferBuilder.vertex((double)(l + 1), (double)(8 + i), 0.0).color(0.0F, 0.0F, 0.0F, m).next();
		bufferBuilder.vertex((double)(l + 1), (double)(-1 + i), 0.0).color(0.0F, 0.0F, 0.0F, m).next();
		tessellator.draw();
		GlStateManager.enableTexture();
		if (!bl) {
			textRenderer.draw(string, (float)(-textRenderer.getStringWidth(string) / 2), (float)i, 553648127);
			GlStateManager.enableDepthTest();
		}

		GlStateManager.depthMask(true);
		textRenderer.draw(string, (float)(-textRenderer.getStringWidth(string) / 2), (float)i, bl ? 553648127 : -1);
		GlStateManager.enableLighting();
		GlStateManager.disableBlend();
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.popMatrix();
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
			GlStateManager.enableAlphaTest();
			GlStateManager.pushMatrix();
			GlStateManager.pushLightingAttributes();
			GlStateManager.enableDepthTest();
			GlStateManager.disableCull();
			GuiLighting.enable();
			GlStateManager.translatef(
				(float)(i / 2) + o * MathHelper.abs(MathHelper.sin(n * 2.0F)), (float)(j / 2) + p * MathHelper.abs(MathHelper.sin(n * 2.0F)), -50.0F
			);
			float q = 50.0F + 175.0F * MathHelper.sin(n);
			GlStateManager.scalef(q, -q, q);
			GlStateManager.rotatef(900.0F * MathHelper.abs(MathHelper.sin(n)), 0.0F, 1.0F, 0.0F);
			GlStateManager.rotatef(6.0F * MathHelper.cos(g * 8.0F), 1.0F, 0.0F, 0.0F);
			GlStateManager.rotatef(6.0F * MathHelper.cos(g * 8.0F), 0.0F, 0.0F, 1.0F);
			this.client.method_1480().renderItem(this.floatingItem, ModelTransformation.Type.FIXED);
			GlStateManager.popAttributes();
			GlStateManager.popMatrix();
			GuiLighting.disable();
			GlStateManager.enableCull();
			GlStateManager.disableDepthTest();
		}
	}

	public MinecraftClient getClient() {
		return this.client;
	}

	public float getSkyDarkness(float f) {
		return MathHelper.lerp(f, this.tickStartSkyDarkness, this.field_4002);
	}

	public float getViewDistance() {
		return this.viewDistance;
	}

	public class_4184 method_19418() {
		return this.field_18765;
	}
}
