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
import net.minecraft.class_295;
import net.minecraft.class_4063;
import net.minecraft.class_4066;
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
import net.minecraft.util.crash.ICrashCallable;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.GameMode;
import net.minecraft.world.RayTraceContext;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.Heightmap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class GameRenderer implements AutoCloseable, SynchronousResourceReloadListener {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Identifier RAIN_LOC = new Identifier("textures/environment/rain.png");
	private static final Identifier SNOW_LOC = new Identifier("textures/environment/snow.png");
	private final MinecraftClient client;
	private final ResourceManager resourceContainer;
	private final Random random = new Random();
	private float viewDistance;
	public final FirstPersonRenderer firstPersonRenderer;
	private final MapRenderer mapRenderer;
	private int field_4027;
	private final float field_4020 = 4.0F;
	private float field_4000 = 4.0F;
	private float field_4019;
	private float field_3999;
	private float tickEndSkyDarkness;
	private float tickStartSkyDarkness;
	private boolean field_3992 = true;
	private boolean blockOutlineEnabled = true;
	private long lastWorldIconUpdate;
	private long lastRenderTime = SystemUtil.getMeasuringTimeMs();
	private final LightmapTextureManager lightmapTextureManager;
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
	private ShaderEffect shader;
	private float field_4002;
	private float field_4022;
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
	private int field_4021;

	public GameRenderer(MinecraftClient minecraftClient, ResourceManager resourceManager) {
		this.client = minecraftClient;
		this.resourceContainer = resourceManager;
		this.firstPersonRenderer = minecraftClient.getFirstPersonRenderer();
		this.mapRenderer = new MapRenderer(minecraftClient.getTextureManager());
		this.lightmapTextureManager = new LightmapTextureManager(this);
		this.backgroundRenderer = new BackgroundRenderer(this);
		this.shader = null;

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
		this.lightmapTextureManager.close();
		this.mapRenderer.close();
		this.disableShader();
	}

	public boolean method_3175() {
		return GLX.usePostProcess && this.shader != null;
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
		if (GLX.usePostProcess) {
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
	}

	private void loadShader(Identifier identifier) {
		if (this.shader != null) {
			this.shader.close();
		}

		try {
			this.shader = new ShaderEffect(this.client.getTextureManager(), this.resourceContainer, this.client.getFramebuffer(), identifier);
			this.shader.setupDimensions(this.client.window.getFramebufferWidth(), this.client.window.getFramebufferHeight());
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
		if (GLX.usePostProcess && GlProgramManager.getInstance() == null) {
			GlProgramManager.init();
		}

		this.method_3199();
		this.lightmapTextureManager.tick();
		this.field_4000 = 4.0F;
		if (this.client.getCameraEntity() == null) {
			this.client.setCameraEntity(this.client.player);
		}

		this.field_4022 = this.field_4002;
		this.field_4002 = this.field_4002 + (this.client.getCameraEntity().getEyeHeight() - this.field_4002) * 0.5F;
		this.field_4027++;
		this.firstPersonRenderer.updateHeldItems();
		this.method_3177();
		this.tickStartSkyDarkness = this.tickEndSkyDarkness;
		if (this.client.inGameHud.getBossBarHud().shouldDarkenSky()) {
			this.tickEndSkyDarkness += 0.05F;
			if (this.tickEndSkyDarkness > 1.0F) {
				this.tickEndSkyDarkness = 1.0F;
			}
		} else if (this.tickEndSkyDarkness > 0.0F) {
			this.tickEndSkyDarkness -= 0.0125F;
		}

		if (this.floatingItemTimeLeft > 0) {
			this.floatingItemTimeLeft--;
			if (this.floatingItemTimeLeft == 0) {
				this.floatingItem = null;
			}
		}
	}

	public ShaderEffect getShader() {
		return this.shader;
	}

	public void onResized(int i, int j) {
		if (GLX.usePostProcess) {
			if (this.shader != null) {
				this.shader.setupDimensions(i, j);
			}

			this.client.worldRenderer.onResized(i, j);
		}
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
				BoundingBox boundingBox = entity.getBoundingBox().stretch(vec3d2.x * d, vec3d2.y * d, vec3d2.z * d).expand(1.0, 1.0, 1.0);
				EntityHitResult entityHitResult = class_1675.method_18075(entity, vec3d, vec3d3, boundingBox, entityx -> !entityx.isSpectator() && entityx.doesCollide(), e);
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

	private double method_3196(float f, boolean bl) {
		if (this.field_4001) {
			return 90.0;
		} else {
			Entity entity = this.client.getCameraEntity();
			double d = 70.0;
			if (bl) {
				d = this.client.options.fov;
				d *= (double)MathHelper.lerp(f, this.field_3999, this.field_4019);
			}

			if (entity instanceof LivingEntity && ((LivingEntity)entity).getHealth() <= 0.0F) {
				float g = (float)((LivingEntity)entity).deathCounter + f;
				d /= (double)((1.0F - 500.0F / (g + 500.0F)) * 2.0F + 1.0F);
			}

			FluidState fluidState = class_295.method_1374(this.client.world, entity, f);
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

	private void method_3197(float f) {
		Entity entity = this.client.getCameraEntity();
		float g = MathHelper.lerp(f, this.field_4022, this.field_4002);
		double d = MathHelper.lerp((double)f, entity.prevX, entity.x);
		double e = MathHelper.lerp((double)f, entity.prevY, entity.y) + (double)entity.getEyeHeight();
		double h = MathHelper.lerp((double)f, entity.prevZ, entity.z);
		if (entity instanceof LivingEntity && ((LivingEntity)entity).isSleeping()) {
			g = (float)((double)g + 1.0);
			GlStateManager.translatef(0.0F, 0.3F, 0.0F);
			if (!this.client.options.field_1821) {
				Direction direction = ((LivingEntity)entity).method_18401();
				if (direction != null) {
					GlStateManager.rotatef(direction.asRotation(), 0.0F, 1.0F, 0.0F);
				}

				GlStateManager.rotatef(MathHelper.lerp(f, entity.prevYaw, entity.yaw) + 180.0F, 0.0F, -1.0F, 0.0F);
				GlStateManager.rotatef(MathHelper.lerp(f, entity.prevPitch, entity.pitch), -1.0F, 0.0F, 0.0F);
			}
		} else if (this.client.options.perspective > 0) {
			double i = (double)MathHelper.lerp(f, this.field_4000, 4.0F);
			if (this.client.options.field_1821) {
				GlStateManager.translatef(0.0F, 0.0F, (float)(-i));
			} else {
				float j = entity.yaw;
				float k = entity.pitch;
				if (this.client.options.perspective == 2) {
					k += 180.0F;
				}

				double l = (double)(-MathHelper.sin(j * (float) (Math.PI / 180.0)) * MathHelper.cos(k * (float) (Math.PI / 180.0))) * i;
				double m = (double)(MathHelper.cos(j * (float) (Math.PI / 180.0)) * MathHelper.cos(k * (float) (Math.PI / 180.0))) * i;
				double n = (double)(-MathHelper.sin(k * (float) (Math.PI / 180.0))) * i;

				for (int o = 0; o < 8; o++) {
					float p = (float)((o & 1) * 2 - 1);
					float q = (float)((o >> 1 & 1) * 2 - 1);
					float r = (float)((o >> 2 & 1) * 2 - 1);
					p *= 0.1F;
					q *= 0.1F;
					r *= 0.1F;
					HitResult hitResult = this.client
						.world
						.rayTrace(
							new RayTraceContext(
								new net.minecraft.util.math.Vec3d(d + (double)p, e + (double)q, h + (double)r),
								new net.minecraft.util.math.Vec3d(d - l + (double)p + (double)r, e - n + (double)q, h - m + (double)r),
								RayTraceContext.ShapeType.field_17559,
								RayTraceContext.FluidHandling.NONE,
								entity
							)
						);
					if (hitResult.getType() != HitResult.Type.NONE) {
						double s = hitResult.getPos().distanceTo(new net.minecraft.util.math.Vec3d(d, e, h));
						if (s < i) {
							i = s;
						}
					}
				}

				if (this.client.options.perspective == 2) {
					GlStateManager.rotatef(180.0F, 0.0F, 1.0F, 0.0F);
				}

				GlStateManager.rotatef(entity.pitch - k, 1.0F, 0.0F, 0.0F);
				GlStateManager.rotatef(entity.yaw - j, 0.0F, 1.0F, 0.0F);
				GlStateManager.translatef(0.0F, 0.0F, (float)(-i));
				GlStateManager.rotatef(j - entity.yaw, 0.0F, 1.0F, 0.0F);
				GlStateManager.rotatef(k - entity.pitch, 1.0F, 0.0F, 0.0F);
			}
		} else if (!this.field_4001) {
			GlStateManager.translatef(0.0F, 0.0F, 0.05F);
		}

		if (!this.client.options.field_1821) {
			GlStateManager.rotatef(entity.getPitch(f), 1.0F, 0.0F, 0.0F);
			GlStateManager.rotatef(entity.getYaw(f) + 180.0F, 0.0F, 1.0F, 0.0F);
		}

		GlStateManager.translatef(0.0F, -g, 0.0F);
	}

	private void method_3185(float f) {
		this.viewDistance = (float)(this.client.options.viewDistance * 16);
		GlStateManager.matrixMode(5889);
		GlStateManager.loadIdentity();
		if (this.field_4005 != 1.0) {
			GlStateManager.translatef((float)this.field_3988, (float)(-this.field_4004), 0.0F);
			GlStateManager.scaled(this.field_4005, this.field_4005, 1.0);
		}

		GlStateManager.multMatrix(
			Matrix4f.method_4929(
				this.method_3196(f, true),
				(float)this.client.window.getFramebufferWidth() / (float)this.client.window.getFramebufferHeight(),
				0.05F,
				this.viewDistance * MathHelper.SQUARE_ROOT_OF_TWO
			)
		);
		GlStateManager.matrixMode(5888);
		GlStateManager.loadIdentity();
		this.method_3198(f);
		if (this.client.options.bobView) {
			this.method_3186(f);
		}

		float g = MathHelper.lerp(f, this.client.player.field_3911, this.client.player.field_3929);
		if (g > 0.0F) {
			int i = 20;
			if (this.client.player.hasPotionEffect(StatusEffects.field_5916)) {
				i = 7;
			}

			float h = 5.0F / (g * g + 5.0F) - g * 0.04F;
			h *= h;
			GlStateManager.rotatef(((float)this.field_4027 + f) * (float)i, 0.0F, 1.0F, 1.0F);
			GlStateManager.scalef(1.0F / h, 1.0F, 1.0F);
			GlStateManager.rotatef(-((float)this.field_4027 + f) * (float)i, 0.0F, 1.0F, 1.0F);
		}

		this.method_3197(f);
	}

	private void method_3172(float f) {
		if (!this.field_4001) {
			GlStateManager.matrixMode(5889);
			GlStateManager.loadIdentity();
			GlStateManager.multMatrix(
				Matrix4f.method_4929(
					this.method_3196(f, false),
					(float)this.client.window.getFramebufferWidth() / (float)this.client.window.getFramebufferHeight(),
					0.05F,
					this.viewDistance * 2.0F
				)
			);
			GlStateManager.matrixMode(5888);
			GlStateManager.loadIdentity();
			GlStateManager.pushMatrix();
			this.method_3198(f);
			if (this.client.options.bobView) {
				this.method_3186(f);
			}

			boolean bl = this.client.getCameraEntity() instanceof LivingEntity && ((LivingEntity)this.client.getCameraEntity()).isSleeping();
			if (this.client.options.perspective == 0
				&& !bl
				&& !this.client.options.hudHidden
				&& this.client.interactionManager.getCurrentGameMode() != GameMode.field_9219) {
				this.enableLightmap();
				this.firstPersonRenderer.renderFirstPersonItem(f);
				this.disableLightmap();
			}

			GlStateManager.popMatrix();
			if (this.client.options.perspective == 0 && !bl) {
				this.firstPersonRenderer.renderOverlays(f);
				this.method_3198(f);
			}

			if (this.client.options.bobView) {
				this.method_3186(f);
			}
		}
	}

	public void disableLightmap() {
		this.lightmapTextureManager.disable();
	}

	public void enableLightmap() {
		this.lightmapTextureManager.enable();
	}

	public float method_3174(LivingEntity livingEntity, float f) {
		int i = livingEntity.getPotionEffect(StatusEffects.field_5925).getDuration();
		return i > 200 ? 1.0F : 0.7F + MathHelper.sin(((float)i - f) * (float) Math.PI * 0.2F) * 0.3F;
	}

	public void render(float f, long l, boolean bl) {
		if (!this.client.isWindowFocused() && this.client.options.pauseOnLostFocus && (!this.client.options.touchscreen || !this.client.mouse.method_1609())) {
			if (SystemUtil.getMeasuringTimeMs() - this.lastRenderTime > 500L) {
				this.client.openPauseMenu();
			}
		} else {
			this.lastRenderTime = SystemUtil.getMeasuringTimeMs();
		}

		if (!this.client.skipGameRender) {
			int i = (int)(this.client.mouse.getX() * (double)this.client.window.getScaledWidth() / (double)this.client.window.getWidth());
			int j = (int)(this.client.mouse.getY() * (double)this.client.window.getScaledHeight() / (double)this.client.window.getHeight());
			int k = this.client.options.maxFps;
			if (bl && this.client.world != null) {
				this.client.getProfiler().push("level");
				int m = Math.min(MinecraftClient.getCurrentFps(), k);
				m = Math.max(m, 60);
				long n = SystemUtil.getMeasuringTimeNano() - l;
				long o = Math.max((long)(1000000000 / m / 4) - n, 0L);
				this.renderWorld(f, SystemUtil.getMeasuringTimeNano() + o);
				if (this.client.isIntegratedServerRunning() && this.lastWorldIconUpdate < SystemUtil.getMeasuringTimeMs() - 1000L) {
					this.lastWorldIconUpdate = SystemUtil.getMeasuringTimeMs();
					if (!this.client.getServer().hasIconFile()) {
						this.updateWorldIcon();
					}
				}

				if (GLX.usePostProcess) {
					this.client.worldRenderer.drawEntityOutlinesFramebuffer();
					if (this.shader != null && this.shadersEnabled) {
						GlStateManager.matrixMode(5890);
						GlStateManager.pushMatrix();
						GlStateManager.loadIdentity();
						this.shader.render(f);
						GlStateManager.popMatrix();
					}

					this.client.getFramebuffer().beginWrite(true);
				}

				this.client.getProfiler().swap("gui");
				if (!this.client.options.hudHidden || this.client.currentScreen != null) {
					GlStateManager.alphaFunc(516, 0.1F);
					this.client.window.method_4493(MinecraftClient.IS_SYSTEM_MAC);
					this.renderFloatingItem(this.client.window.getScaledWidth(), this.client.window.getScaledHeight(), f);
					this.client.inGameHud.draw(f);
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
					this.client.field_18175.method_18326(i, j, this.client.getLastFrameDuration());
				} catch (Throwable var14) {
					CrashReport crashReport = CrashReport.create(var14, "Rendering overlay");
					CrashReportSection crashReportSection = crashReport.addElement("Overlay render details");
					crashReportSection.add("Overlay name", (ICrashCallable<String>)(() -> this.client.field_18175.getClass().getCanonicalName()));
					throw new CrashException(crashReport);
				}
			} else if (this.client.currentScreen != null) {
				GlStateManager.clear(256, MinecraftClient.IS_SYSTEM_MAC);

				try {
					this.client.currentScreen.method_18326(i, j, this.client.getLastFrameDuration());
				} catch (Throwable var13) {
					CrashReport crashReport = CrashReport.create(var13, "Rendering screen");
					CrashReportSection crashReportSection = crashReport.addElement("Screen render details");
					crashReportSection.add("Screen name", (ICrashCallable<String>)(() -> this.client.currentScreen.getClass().getCanonicalName()));
					crashReportSection.add(
						"Mouse location",
						(ICrashCallable<String>)(() -> String.format(
								Locale.ROOT, "Scaled: (%d, %d). Absolute: (%f, %f)", i, j, this.client.mouse.getX(), this.client.mouse.getY()
							))
					);
					crashReportSection.add(
						"Screen size",
						(ICrashCallable<String>)(() -> String.format(
								Locale.ROOT,
								"Scaled: (%d, %d). Absolute: (%d, %d). Scale factor of %f",
								this.client.window.getScaledWidth(),
								this.client.window.getScaledHeight(),
								this.client.window.getFramebufferWidth(),
								this.client.window.getFramebufferHeight(),
								this.client.window.getScaleFactor()
							))
					);
					throw new CrashException(crashReport);
				}
			}
		}
	}

	private void updateWorldIcon() {
		if (this.client.worldRenderer.getChunkNumber() > 10 && this.client.worldRenderer.method_3281() && !this.client.getServer().hasIconFile()) {
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
					nativeImage2.writeFile(this.client.getServer().getIconFile());
				} catch (IOException var27) {
					LOGGER.warn("Couldn't save auto screenshot", (Throwable)var27);
				} finally {
					nativeImage.close();
				}
			});
		}
	}

	public void method_3200(float f) {
		this.client.window.method_4493(MinecraftClient.IS_SYSTEM_MAC);
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
					if (this.client.interactionManager.getCurrentGameMode() == GameMode.field_9219) {
						bl = blockState.createContainerProvider(this.client.world, blockPos) != null;
					} else {
						CachedBlockPosition cachedBlockPosition = new CachedBlockPosition(this.client.world, blockPos, false);
						bl = !itemStack.isEmpty()
							&& (
								itemStack.getCustomCanHarvest(this.client.world.getTagManager(), cachedBlockPosition)
									|| itemStack.getCustomCanPlace(this.client.world.getTagManager(), cachedBlockPosition)
							);
					}
				}
			}

			return bl;
		}
	}

	public void renderWorld(float f, long l) {
		this.lightmapTextureManager.update(f);
		if (this.client.getCameraEntity() == null) {
			this.client.setCameraEntity(this.client.player);
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
		WorldRenderer worldRenderer = this.client.worldRenderer;
		ParticleManager particleManager = this.client.particleManager;
		boolean bl = this.shouldRenderBlockOutline();
		GlStateManager.enableCull();
		this.client.getProfiler().swap("clear");
		GlStateManager.viewport(0, 0, this.client.window.getFramebufferWidth(), this.client.window.getFramebufferHeight());
		this.backgroundRenderer.renderBackground(f);
		GlStateManager.clear(16640, MinecraftClient.IS_SYSTEM_MAC);
		this.client.getProfiler().swap("camera");
		this.method_3185(f);
		Frustum frustum = GlMatrixFrustum.get();
		class_295.method_1373(this.client.player, this.client.options.perspective == 2, this.viewDistance, frustum);
		this.client.getProfiler().swap("culling");
		VisibleRegion visibleRegion = new FrustumWithOrigin(frustum);
		Entity entity = this.client.getCameraEntity();
		double d = MathHelper.lerp((double)f, entity.prevRenderX, entity.x);
		double e = MathHelper.lerp((double)f, entity.prevRenderY, entity.y);
		double g = MathHelper.lerp((double)f, entity.prevRenderZ, entity.z);
		visibleRegion.setOrigin(d, e, g);
		if (this.client.options.viewDistance >= 4) {
			this.backgroundRenderer.applyFog(-1, f);
			this.client.getProfiler().swap("sky");
			GlStateManager.matrixMode(5889);
			GlStateManager.loadIdentity();
			GlStateManager.multMatrix(
				Matrix4f.method_4929(
					this.method_3196(f, true),
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
					this.method_3196(f, true),
					(float)this.client.window.getFramebufferWidth() / (float)this.client.window.getFramebufferHeight(),
					0.05F,
					this.viewDistance * MathHelper.SQUARE_ROOT_OF_TWO
				)
			);
			GlStateManager.matrixMode(5888);
		}

		this.backgroundRenderer.applyFog(0, f);
		GlStateManager.shadeModel(7425);
		if (entity.y + (double)entity.getEyeHeight() < 128.0) {
			this.method_3206(worldRenderer, f, d, e, g);
		}

		this.client.getProfiler().swap("prepareterrain");
		this.backgroundRenderer.applyFog(0, f);
		this.client.getTextureManager().bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
		GuiLighting.disable();
		this.client.getProfiler().swap("terrain_setup");
		this.client.world.method_2935().getLightingProvider().doLightUpdates(Integer.MAX_VALUE, true, true);
		worldRenderer.setUpTerrain(entity, f, visibleRegion, this.field_4021++, this.client.player.isSpectator());
		this.client.getProfiler().swap("updatechunks");
		this.client.worldRenderer.updateChunks(l);
		this.client.getProfiler().swap("terrain");
		GlStateManager.matrixMode(5888);
		GlStateManager.pushMatrix();
		GlStateManager.disableAlphaTest();
		worldRenderer.renderLayer(BlockRenderLayer.SOLID, (double)f, entity);
		GlStateManager.enableAlphaTest();
		worldRenderer.renderLayer(BlockRenderLayer.MIPPED_CUTOUT, (double)f, entity);
		this.client.getTextureManager().getTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX).pushFilter(false, false);
		worldRenderer.renderLayer(BlockRenderLayer.CUTOUT, (double)f, entity);
		this.client.getTextureManager().getTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX).popFilter();
		GlStateManager.shadeModel(7424);
		GlStateManager.alphaFunc(516, 0.1F);
		GlStateManager.matrixMode(5888);
		GlStateManager.popMatrix();
		GlStateManager.pushMatrix();
		GuiLighting.enable();
		this.client.getProfiler().swap("entities");
		worldRenderer.renderEntities(entity, visibleRegion, f);
		GuiLighting.disable();
		this.disableLightmap();
		GlStateManager.matrixMode(5888);
		GlStateManager.popMatrix();
		if (bl && this.client.hitResult != null) {
			GlStateManager.disableAlphaTest();
			this.client.getProfiler().swap("outline");
			worldRenderer.drawHighlightedBlockOutline(entity, this.client.hitResult, 0, f);
			GlStateManager.enableAlphaTest();
		}

		if (this.client.debugRenderer.shouldRender()) {
			this.client.debugRenderer.renderDebuggers(f, l);
		}

		this.client.getProfiler().swap("destroyProgress");
		GlStateManager.enableBlend();
		GlStateManager.blendFuncSeparate(
			GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO
		);
		this.client.getTextureManager().getTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX).pushFilter(false, false);
		worldRenderer.renderPartiallyBrokenBlocks(Tessellator.getInstance(), Tessellator.getInstance().getBufferBuilder(), entity, f);
		this.client.getTextureManager().getTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX).popFilter();
		GlStateManager.disableBlend();
		this.enableLightmap();
		this.backgroundRenderer.applyFog(0, f);
		this.client.getProfiler().swap("particles");
		particleManager.renderUnlitParticles(entity, f);
		this.disableLightmap();
		GlStateManager.depthMask(false);
		GlStateManager.enableCull();
		this.client.getProfiler().swap("weather");
		this.method_3170(f);
		GlStateManager.depthMask(true);
		worldRenderer.renderWorldBorder(entity, f);
		GlStateManager.disableBlend();
		GlStateManager.enableCull();
		GlStateManager.blendFuncSeparate(
			GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO
		);
		GlStateManager.alphaFunc(516, 0.1F);
		this.backgroundRenderer.applyFog(0, f);
		GlStateManager.enableBlend();
		GlStateManager.depthMask(false);
		this.client.getTextureManager().bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
		GlStateManager.shadeModel(7425);
		this.client.getProfiler().swap("translucent");
		worldRenderer.renderLayer(BlockRenderLayer.TRANSLUCENT, (double)f, entity);
		GlStateManager.shadeModel(7424);
		GlStateManager.depthMask(true);
		GlStateManager.enableCull();
		GlStateManager.disableBlend();
		GlStateManager.disableFog();
		if (entity.y + (double)entity.getEyeHeight() >= 128.0) {
			this.client.getProfiler().swap("aboveClouds");
			this.method_3206(worldRenderer, f, d, e, g);
		}

		this.client.getProfiler().swap("hand");
		if (this.field_3992) {
			GlStateManager.clear(256, MinecraftClient.IS_SYSTEM_MAC);
			this.method_3172(f);
		}
	}

	private void method_3206(WorldRenderer worldRenderer, float f, double d, double e, double g) {
		if (this.client.options.getCloudRenderMode() != class_4063.field_18162) {
			this.client.getProfiler().swap("clouds");
			GlStateManager.matrixMode(5889);
			GlStateManager.loadIdentity();
			GlStateManager.multMatrix(
				Matrix4f.method_4929(
					this.method_3196(f, true),
					(float)this.client.window.getFramebufferWidth() / (float)this.client.window.getFramebufferHeight(),
					0.05F,
					this.viewDistance * 4.0F
				)
			);
			GlStateManager.matrixMode(5888);
			GlStateManager.pushMatrix();
			this.backgroundRenderer.applyFog(0, f);
			worldRenderer.renderClouds(f, d, e, g);
			GlStateManager.disableFog();
			GlStateManager.popMatrix();
			GlStateManager.matrixMode(5889);
			GlStateManager.loadIdentity();
			GlStateManager.multMatrix(
				Matrix4f.method_4929(
					this.method_3196(f, true),
					(float)this.client.window.getFramebufferWidth() / (float)this.client.window.getFramebufferHeight(),
					0.05F,
					this.viewDistance * MathHelper.SQUARE_ROOT_OF_TWO
				)
			);
			GlStateManager.matrixMode(5888);
		}
	}

	private void method_3177() {
		float f = this.client.world.getRainGradient(1.0F);
		if (!this.client.options.fancyGraphics) {
			f /= 2.0F;
		}

		if (f != 0.0F) {
			this.random.setSeed((long)this.field_4027 * 312987231L);
			Entity entity = this.client.getCameraEntity();
			ViewableWorld viewableWorld = this.client.world;
			BlockPos blockPos = new BlockPos(entity);
			int i = 10;
			double d = 0.0;
			double e = 0.0;
			double g = 0.0;
			int j = 0;
			int k = (int)(100.0F * f * f);
			if (this.client.options.particles == class_4066.field_18198) {
				k >>= 1;
			} else if (this.client.options.particles == class_4066.field_18199) {
				k = 0;
			}

			for (int l = 0; l < k; l++) {
				BlockPos blockPos2 = viewableWorld.getTopPosition(
					Heightmap.Type.MOTION_BLOCKING, blockPos.add(this.random.nextInt(10) - this.random.nextInt(10), 0, this.random.nextInt(10) - this.random.nextInt(10))
				);
				Biome biome = viewableWorld.getBiome(blockPos2);
				BlockPos blockPos3 = blockPos2.down();
				if (blockPos2.getY() <= blockPos.getY() + 10
					&& blockPos2.getY() >= blockPos.getY() - 10
					&& biome.getPrecipitation() == Biome.Precipitation.RAIN
					&& biome.getTemperature(blockPos2) >= 0.15F) {
					double h = this.random.nextDouble();
					double m = this.random.nextDouble();
					BlockState blockState = viewableWorld.getBlockState(blockPos3);
					FluidState fluidState = viewableWorld.getFluidState(blockPos2);
					VoxelShape voxelShape = blockState.getCollisionShape(viewableWorld, blockPos3);
					double n = voxelShape.method_1102(Direction.Axis.Y, h, m);
					double o = (double)fluidState.getHeight(viewableWorld, blockPos2);
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
						if (!fluidState.matches(FluidTags.field_15518)
							&& blockState.getBlock() != Blocks.field_10092
							&& (blockState.getBlock() != Blocks.field_17350 || !(Boolean)blockState.get(CampfireBlock.LIT))) {
							if (this.random.nextInt(++j) == 0) {
								d = (double)blockPos3.getX() + h;
								e = (double)((float)blockPos3.getY() + 0.1F) + p - 1.0;
								g = (double)blockPos3.getZ() + m;
							}

							this.client
								.world
								.addParticle(
									ParticleTypes.field_11242, (double)blockPos3.getX() + h, (double)((float)blockPos3.getY() + 0.1F) + p, (double)blockPos3.getZ() + m, 0.0, 0.0, 0.0
								);
						} else {
							this.client
								.world
								.addParticle(
									ParticleTypes.field_11251, (double)blockPos2.getX() + h, (double)((float)blockPos2.getY() + 0.1F) - q, (double)blockPos2.getZ() + m, 0.0, 0.0, 0.0
								);
						}
					}
				}
			}

			if (j > 0 && this.random.nextInt(3) < this.field_3995++) {
				this.field_3995 = 0;
				if (e > (double)(blockPos.getY() + 1)
					&& viewableWorld.getTopPosition(Heightmap.Type.MOTION_BLOCKING, blockPos).getY() > MathHelper.floor((float)blockPos.getY())) {
					this.client.world.playSound(d, e, g, SoundEvents.field_15020, SoundCategory.field_15252, 0.1F, 0.5F, false);
				} else {
					this.client.world.playSound(d, e, g, SoundEvents.field_14946, SoundCategory.field_15252, 0.2F, 1.0F, false);
				}
			}
		}
	}

	protected void method_3170(float f) {
		float g = this.client.world.getRainGradient(f);
		if (!(g <= 0.0F)) {
			this.enableLightmap();
			Entity entity = this.client.getCameraEntity();
			World world = this.client.world;
			int i = MathHelper.floor(entity.x);
			int j = MathHelper.floor(entity.y);
			int k = MathHelper.floor(entity.z);
			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder bufferBuilder = tessellator.getBufferBuilder();
			GlStateManager.disableCull();
			GlStateManager.normal3f(0.0F, 1.0F, 0.0F);
			GlStateManager.enableBlend();
			GlStateManager.blendFuncSeparate(
				GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO
			);
			GlStateManager.alphaFunc(516, 0.1F);
			double d = MathHelper.lerp((double)f, entity.prevRenderX, entity.x);
			double e = MathHelper.lerp((double)f, entity.prevRenderY, entity.y);
			double h = MathHelper.lerp((double)f, entity.prevRenderZ, entity.z);
			int l = MathHelper.floor(e);
			int m = 5;
			if (this.client.options.fancyGraphics) {
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
					Biome biome = world.getBiome(mutable);
					if (biome.getPrecipitation() != Biome.Precipitation.NONE) {
						int u = world.getTopPosition(Heightmap.Type.MOTION_BLOCKING, mutable).getY();
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
							float y = biome.getTemperature(mutable);
							if (y >= 0.15F) {
								if (n != 0) {
									if (n >= 0) {
										tessellator.draw();
									}

									n = 0;
									this.client.getTextureManager().bindTexture(RAIN_LOC);
									bufferBuilder.begin(7, VertexFormats.POSITION_UV_COLOR_LMAP);
								}

								double z = -((double)(this.field_4027 + q * q * 3121 + q * 45238971 + p * p * 418711 + p * 13761 & 31) + (double)f)
									/ 32.0
									* (3.0 + this.random.nextDouble());
								double aa = (double)((float)q + 0.5F) - entity.x;
								double ab = (double)((float)p + 0.5F) - entity.z;
								float ac = MathHelper.sqrt(aa * aa + ab * ab) / (float)m;
								float ad = ((1.0F - ac * ac) * 0.5F + 0.5F) * g;
								mutable.set(q, x, p);
								int ae = world.getLightmapIndex(mutable, 0);
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
									this.client.getTextureManager().bindTexture(SNOW_LOC);
									bufferBuilder.begin(7, VertexFormats.POSITION_UV_COLOR_LMAP);
								}

								double z = (double)(-((float)(this.field_4027 & 511) + f) / 512.0F);
								double aa = this.random.nextDouble() + (double)o * 0.01 * (double)((float)this.random.nextGaussian());
								double ab = this.random.nextDouble() + (double)(o * (float)this.random.nextGaussian()) * 0.001;
								double ah = (double)((float)q + 0.5F) - entity.x;
								double ai = (double)((float)p + 0.5F) - entity.z;
								float aj = MathHelper.sqrt(ah * ah + ai * ai) / (float)m;
								float ak = ((1.0F - aj * aj) * 0.3F + 0.5F) * g;
								mutable.set(q, x, p);
								int al = (world.getLightmapIndex(mutable, 0) * 3 + 15728880) / 4;
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
	}

	public MapRenderer getMapRenderer() {
		return this.mapRenderer;
	}

	public static void method_3179(TextRenderer textRenderer, String string, float f, float g, float h, int i, float j, float k, boolean bl, boolean bl2) {
		GlStateManager.pushMatrix();
		GlStateManager.translatef(f, g, h);
		GlStateManager.normal3f(0.0F, 1.0F, 0.0F);
		GlStateManager.rotatef(-j, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotatef((float)(bl ? -1 : 1) * k, 1.0F, 0.0F, 0.0F);
		GlStateManager.scalef(-0.025F, -0.025F, 0.025F);
		GlStateManager.disableLighting();
		GlStateManager.depthMask(false);
		if (!bl2) {
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
		bufferBuilder.begin(7, VertexFormats.POSITION_COLOR);
		bufferBuilder.vertex((double)(-l - 1), (double)(-1 + i), 0.0).color(0.0F, 0.0F, 0.0F, 0.25F).next();
		bufferBuilder.vertex((double)(-l - 1), (double)(8 + i), 0.0).color(0.0F, 0.0F, 0.0F, 0.25F).next();
		bufferBuilder.vertex((double)(l + 1), (double)(8 + i), 0.0).color(0.0F, 0.0F, 0.0F, 0.25F).next();
		bufferBuilder.vertex((double)(l + 1), (double)(-1 + i), 0.0).color(0.0F, 0.0F, 0.0F, 0.25F).next();
		tessellator.draw();
		GlStateManager.enableTexture();
		if (!bl2) {
			textRenderer.draw(string, (float)(-textRenderer.getStringWidth(string) / 2), (float)i, 553648127);
			GlStateManager.enableDepthTest();
		}

		GlStateManager.depthMask(true);
		textRenderer.draw(string, (float)(-textRenderer.getStringWidth(string) / 2), (float)i, bl2 ? 553648127 : -1);
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
			this.client.getItemRenderer().renderItem(this.floatingItem, ModelTransformation.Type.FIXED);
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
		return MathHelper.lerp(f, this.tickStartSkyDarkness, this.tickEndSkyDarkness);
	}

	public float getViewDistance() {
		return this.viewDistance;
	}
}
