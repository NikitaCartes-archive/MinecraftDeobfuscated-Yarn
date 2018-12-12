package net.minecraft.client.render;

import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_295;
import net.minecraft.class_330;
import net.minecraft.class_758;
import net.minecraft.class_855;
import net.minecraft.class_856;
import net.minecraft.class_857;
import net.minecraft.class_858;
import net.minecraft.block.BedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderLayer;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.pattern.BlockProxy;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.FontRenderer;
import net.minecraft.client.gl.GlProgramManager;
import net.minecraft.client.gl.ShaderEffect;
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
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.resource.ResourceImpl;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloadListener;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.HitResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.crash.ICrashCallable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.FluidRayTraceMode;
import net.minecraft.world.GameMode;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.Heightmap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class GameRenderer implements AutoCloseable, ResourceReloadListener {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Identifier RAIN_LOC = new Identifier("textures/environment/rain.png");
	private static final Identifier SNOW_LOC = new Identifier("textures/environment/snow.png");
	private final MinecraftClient client;
	private final ResourceManager resourceContainer;
	private final Random random = new Random();
	private float field_4025;
	public final FirstPersonRenderer firstPersonRenderer;
	private final class_330 field_4026;
	private int field_4027;
	private Entity field_4014;
	private final float field_4020 = 4.0F;
	private float field_4000 = 4.0F;
	private float field_4019;
	private float field_3999;
	private float field_4016;
	private float field_3997;
	private boolean field_3992 = true;
	private boolean field_4009 = true;
	private long field_4017;
	private long field_3998 = SystemUtil.getMeasuringTimeMs();
	private final LightmapTextureManager lightmapTextureManager;
	private int field_3995;
	private final float[] field_3991 = new float[1024];
	private final float[] field_3989 = new float[1024];
	private final class_758 field_3990;
	private boolean field_4001;
	private double field_4005 = 1.0;
	private double field_3988;
	private double field_4004;
	private ItemStack field_4006;
	private int field_4007;
	private float field_4029;
	private float field_4003;
	private ShaderEffect field_4024;
	private float field_4002;
	private float field_4022;
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
	public static final int field_4010 = field_3996.length;
	private int field_4023 = field_4010;
	private boolean field_4013;
	private int field_4021;

	public GameRenderer(MinecraftClient minecraftClient, ResourceManager resourceManager) {
		this.client = minecraftClient;
		this.resourceContainer = resourceManager;
		this.firstPersonRenderer = minecraftClient.getFirstPersonRenderer();
		this.field_4026 = new class_330(minecraftClient.getTextureManager());
		this.lightmapTextureManager = new LightmapTextureManager(this);
		this.field_3990 = new class_758(this);
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
		this.lightmapTextureManager.close();
		this.field_4026.close();
		this.method_3207();
	}

	public boolean method_3175() {
		return GLX.usePostProcess && this.field_4024 != null;
	}

	public void method_3207() {
		if (this.field_4024 != null) {
			this.field_4024.close();
		}

		this.field_4024 = null;
		this.field_4023 = field_4010;
	}

	public void method_3184() {
		this.field_4013 = !this.field_4013;
	}

	public void onSetCameraEntity(@Nullable Entity entity) {
		if (GLX.usePostProcess) {
			if (this.field_4024 != null) {
				this.field_4024.close();
			}

			this.field_4024 = null;
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
		if (this.field_4024 != null) {
			this.field_4024.close();
		}

		try {
			this.field_4024 = new ShaderEffect(this.client.getTextureManager(), this.resourceContainer, this.client.getFramebuffer(), identifier);
			this.field_4024.setupDimensions(this.client.window.getWindowWidth(), this.client.window.getWindowHeight());
			this.field_4013 = true;
		} catch (IOException var3) {
			LOGGER.warn("Failed to load shader: {}", identifier, var3);
			this.field_4023 = field_4010;
			this.field_4013 = false;
		} catch (JsonSyntaxException var4) {
			LOGGER.warn("Failed to load shader: {}", identifier, var4);
			this.field_4023 = field_4010;
			this.field_4013 = false;
		}
	}

	@Override
	public void onResourceReload(ResourceManager resourceManager) {
		if (this.field_4024 != null) {
			this.field_4024.close();
		}

		this.field_4024 = null;
		if (this.field_4023 == field_4010) {
			this.onSetCameraEntity(this.client.getCameraEntity());
		} else {
			this.loadShader(field_3996[this.field_4023]);
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
		this.field_3997 = this.field_4016;
		if (this.client.hudInGame.getHudBossBar().shouldDarkenSky()) {
			this.field_4016 += 0.05F;
			if (this.field_4016 > 1.0F) {
				this.field_4016 = 1.0F;
			}
		} else if (this.field_4016 > 0.0F) {
			this.field_4016 -= 0.0125F;
		}

		if (this.field_4007 > 0) {
			this.field_4007--;
			if (this.field_4007 == 0) {
				this.field_4006 = null;
			}
		}
	}

	public ShaderEffect getShader() {
		return this.field_4024;
	}

	public void method_3169(int i, int j) {
		if (GLX.usePostProcess) {
			if (this.field_4024 != null) {
				this.field_4024.setupDimensions(i, j);
			}

			this.client.field_1769.method_3242(i, j);
		}
	}

	public void method_3190(float f) {
		Entity entity = this.client.getCameraEntity();
		if (entity != null) {
			if (this.client.world != null) {
				this.client.getProfiler().push("pick");
				this.client.field_1692 = null;
				double d = (double)this.client.interactionManager.getReachDistance();
				this.client.hitResult = entity.rayTrace(d, f, FluidRayTraceMode.NONE);
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

				if (this.client.hitResult != null) {
					e = this.client.hitResult.pos.distanceTo(vec3d);
				}

				net.minecraft.util.math.Vec3d vec3d2 = entity.getRotationVec(1.0F);
				net.minecraft.util.math.Vec3d vec3d3 = vec3d.add(vec3d2.x * d, vec3d2.y * d, vec3d2.z * d);
				this.field_4014 = null;
				net.minecraft.util.math.Vec3d vec3d4 = null;
				float g = 1.0F;
				List<Entity> list = this.client
					.world
					.getEntities(
						entity,
						entity.getBoundingBox().stretch(vec3d2.x * d, vec3d2.y * d, vec3d2.z * d).expand(1.0, 1.0, 1.0),
						EntityPredicates.EXCEPT_SPECTATOR.and(Entity::doesCollide)
					);
				double h = e;

				for (int j = 0; j < list.size(); j++) {
					Entity entity2 = (Entity)list.get(j);
					BoundingBox boundingBox = entity2.getBoundingBox().expand((double)entity2.method_5871());
					HitResult hitResult = boundingBox.rayTrace(vec3d, vec3d3);
					if (boundingBox.contains(vec3d)) {
						if (h >= 0.0) {
							this.field_4014 = entity2;
							vec3d4 = hitResult == null ? vec3d : hitResult.pos;
							h = 0.0;
						}
					} else if (hitResult != null) {
						double k = vec3d.distanceTo(hitResult.pos);
						if (k < h || h == 0.0) {
							if (entity2.getTopmostRiddenEntity() == entity.getTopmostRiddenEntity()) {
								if (h == 0.0) {
									this.field_4014 = entity2;
									vec3d4 = hitResult.pos;
								}
							} else {
								this.field_4014 = entity2;
								vec3d4 = hitResult.pos;
								h = k;
							}
						}
					}
				}

				if (this.field_4014 != null && bl && vec3d.distanceTo(vec3d4) > 3.0) {
					this.field_4014 = null;
					this.client.hitResult = new HitResult(HitResult.Type.NONE, vec3d4, null, new BlockPos(vec3d4));
				}

				if (this.field_4014 != null && (h < e || this.client.hitResult == null)) {
					this.client.hitResult = new HitResult(this.field_4014, vec3d4);
					if (this.field_4014 instanceof LivingEntity || this.field_4014 instanceof ItemFrameEntity) {
						this.client.field_1692 = this.field_4014;
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
				d = this.client.field_1690.fov;
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
			if (!this.client.field_1690.field_1821) {
				BlockPos blockPos = new BlockPos(entity);
				BlockState blockState = this.client.world.getBlockState(blockPos);
				Block block = blockState.getBlock();
				if (block instanceof BedBlock) {
					GlStateManager.rotatef(((Direction)blockState.get(BedBlock.field_11177)).asRotation(), 0.0F, 1.0F, 0.0F);
				}

				GlStateManager.rotatef(MathHelper.lerp(f, entity.prevYaw, entity.yaw) + 180.0F, 0.0F, -1.0F, 0.0F);
				GlStateManager.rotatef(MathHelper.lerp(f, entity.prevPitch, entity.pitch), -1.0F, 0.0F, 0.0F);
			}
		} else if (this.client.field_1690.field_1850 > 0) {
			double i = (double)MathHelper.lerp(f, this.field_4000, 4.0F);
			if (this.client.field_1690.field_1821) {
				GlStateManager.translatef(0.0F, 0.0F, (float)(-i));
			} else {
				float j = entity.yaw;
				float k = entity.pitch;
				if (this.client.field_1690.field_1850 == 2) {
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
							new net.minecraft.util.math.Vec3d(d + (double)p, e + (double)q, h + (double)r),
							new net.minecraft.util.math.Vec3d(d - l + (double)p + (double)r, e - n + (double)q, h - m + (double)r)
						);
					if (hitResult != null) {
						double s = hitResult.pos.distanceTo(new net.minecraft.util.math.Vec3d(d, e, h));
						if (s < i) {
							i = s;
						}
					}
				}

				if (this.client.field_1690.field_1850 == 2) {
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

		if (!this.client.field_1690.field_1821) {
			GlStateManager.rotatef(entity.getPitch(f), 1.0F, 0.0F, 0.0F);
			GlStateManager.rotatef(entity.getYaw(f) + 180.0F, 0.0F, 1.0F, 0.0F);
		}

		GlStateManager.translatef(0.0F, -g, 0.0F);
	}

	private void method_3185(float f) {
		this.field_4025 = (float)(this.client.field_1690.viewDistance * 16);
		GlStateManager.matrixMode(5889);
		GlStateManager.loadIdentity();
		if (this.field_4005 != 1.0) {
			GlStateManager.translatef((float)this.field_3988, (float)(-this.field_4004), 0.0F);
			GlStateManager.scaled(this.field_4005, this.field_4005, 1.0);
		}

		GlStateManager.multMatrix(
			Matrix4f.method_4929(
				this.method_3196(f, true),
				(float)this.client.window.getWindowWidth() / (float)this.client.window.getWindowHeight(),
				0.05F,
				this.field_4025 * MathHelper.SQUARE_ROOT_OF_TWO
			)
		);
		GlStateManager.matrixMode(5888);
		GlStateManager.loadIdentity();
		this.method_3198(f);
		if (this.client.field_1690.bobView) {
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
					this.method_3196(f, false), (float)this.client.window.getWindowWidth() / (float)this.client.window.getWindowHeight(), 0.05F, this.field_4025 * 2.0F
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
			if (this.client.field_1690.field_1850 == 0
				&& !bl
				&& !this.client.field_1690.field_1842
				&& this.client.interactionManager.getCurrentGameMode() != GameMode.field_9219) {
				this.enableLightmap();
				this.firstPersonRenderer.renderFirstPersonItem(f);
				this.disableLightmap();
			}

			GlStateManager.popMatrix();
			if (this.client.field_1690.field_1850 == 0 && !bl) {
				this.firstPersonRenderer.renderOverlays(f);
				this.method_3198(f);
			}

			if (this.client.field_1690.bobView) {
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

	public void method_3192(float f, long l, boolean bl) {
		if (!this.client.isWindowFocused()
			&& this.client.field_1690.pauseOnLostFocus
			&& (!this.client.field_1690.touchscreen || !this.client.field_1729.method_1609())) {
			if (SystemUtil.getMeasuringTimeMs() - this.field_3998 > 500L) {
				this.client.openInGameMenu();
			}
		} else {
			this.field_3998 = SystemUtil.getMeasuringTimeMs();
		}

		if (!this.client.field_1743) {
			int i = (int)(this.client.field_1729.getX() * (double)this.client.window.getScaledWidth() / (double)this.client.window.method_4480());
			int j = (int)(this.client.field_1729.getY() * (double)this.client.window.getScaledHeight() / (double)this.client.window.method_4507());
			int k = this.client.field_1690.maxFps;
			if (bl && this.client.world != null) {
				this.client.getProfiler().push("level");
				int m = Math.min(MinecraftClient.getCurrentFps(), k);
				m = Math.max(m, 60);
				long n = SystemUtil.getMeasuringTimeNano() - l;
				long o = Math.max((long)(1000000000 / m / 4) - n, 0L);
				this.method_3188(f, SystemUtil.getMeasuringTimeNano() + o);
				if (this.client.method_1496() && this.field_4017 < SystemUtil.getMeasuringTimeMs() - 1000L) {
					this.field_4017 = SystemUtil.getMeasuringTimeMs();
					if (!this.client.getServer().hasIconFile()) {
						this.method_3176();
					}
				}

				if (GLX.usePostProcess) {
					this.client.field_1769.drawFramebuffer();
					if (this.field_4024 != null && this.field_4013) {
						GlStateManager.matrixMode(5890);
						GlStateManager.pushMatrix();
						GlStateManager.loadIdentity();
						this.field_4024.render(f);
						GlStateManager.popMatrix();
					}

					this.client.getFramebuffer().beginWrite(true);
				}

				this.client.getProfiler().swap("gui");
				if (!this.client.field_1690.field_1842 || this.client.currentGui != null) {
					GlStateManager.alphaFunc(516, 0.1F);
					this.client.window.method_4493(MinecraftClient.isSystemMac);
					this.method_3171(this.client.window.getScaledWidth(), this.client.window.getScaledHeight(), f);
					this.client.hudInGame.draw(f);
				}

				this.client.getProfiler().pop();
			} else {
				GlStateManager.viewport(0, 0, this.client.window.getWindowWidth(), this.client.window.getWindowHeight());
				GlStateManager.matrixMode(5889);
				GlStateManager.loadIdentity();
				GlStateManager.matrixMode(5888);
				GlStateManager.loadIdentity();
				this.client.window.method_4493(MinecraftClient.isSystemMac);
			}

			if (this.client.currentGui != null) {
				GlStateManager.clear(256, MinecraftClient.isSystemMac);

				try {
					this.client.currentGui.draw(i, j, this.client.method_1534());
				} catch (Throwable var13) {
					CrashReport crashReport = CrashReport.create(var13, "Rendering screen");
					CrashReportSection crashReportSection = crashReport.method_562("Screen render details");
					crashReportSection.add("Screen name", (ICrashCallable<String>)(() -> this.client.currentGui.getClass().getCanonicalName()));
					crashReportSection.add(
						"Mouse location",
						(ICrashCallable<String>)(() -> String.format(
								Locale.ROOT, "Scaled: (%d, %d). Absolute: (%f, %f)", i, j, this.client.field_1729.getX(), this.client.field_1729.getY()
							))
					);
					crashReportSection.add(
						"Screen size",
						(ICrashCallable<String>)(() -> String.format(
								Locale.ROOT,
								"Scaled: (%d, %d). Absolute: (%d, %d). Scale factor of %f",
								this.client.window.getScaledWidth(),
								this.client.window.getScaledHeight(),
								this.client.window.getWindowWidth(),
								this.client.window.getWindowHeight(),
								this.client.window.method_4495()
							))
					);
					throw new CrashException(crashReport);
				}
			}
		}
	}

	private void method_3176() {
		if (this.client.field_1769.getChunkNumber() > 10 && this.client.field_1769.method_3281() && !this.client.getServer().hasIconFile()) {
			NativeImage nativeImage = ScreenshotUtils.method_1663(
				this.client.window.getWindowWidth(), this.client.window.getWindowHeight(), this.client.getFramebuffer()
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
		this.client.window.method_4493(MinecraftClient.isSystemMac);
	}

	private boolean method_3202() {
		if (!this.field_4009) {
			return false;
		} else {
			Entity entity = this.client.getCameraEntity();
			boolean bl = entity instanceof PlayerEntity && !this.client.field_1690.field_1842;
			if (bl && !((PlayerEntity)entity).abilities.allowModifyWorld) {
				ItemStack itemStack = ((PlayerEntity)entity).getMainHandStack();
				if (this.client.hitResult != null && this.client.hitResult.type == HitResult.Type.BLOCK) {
					BlockPos blockPos = this.client.hitResult.getBlockPos();
					Block block = this.client.world.getBlockState(blockPos).getBlock();
					if (this.client.interactionManager.getCurrentGameMode() == GameMode.field_9219) {
						bl = block.hasBlockEntity() && this.client.world.getBlockEntity(blockPos) instanceof Inventory;
					} else {
						BlockProxy blockProxy = new BlockProxy(this.client.world, blockPos, false);
						bl = !itemStack.isEmpty()
							&& (
								itemStack.getCustomCanHarvest(this.client.world.getTagManager(), blockProxy)
									|| itemStack.getCustomCanPlace(this.client.world.getTagManager(), blockProxy)
							);
					}
				}
			}

			return bl;
		}
	}

	public void method_3188(float f, long l) {
		this.lightmapTextureManager.update(f);
		if (this.client.getCameraEntity() == null) {
			this.client.setCameraEntity(this.client.player);
		}

		this.method_3190(f);
		GlStateManager.enableDepthTest();
		GlStateManager.enableAlphaTest();
		GlStateManager.alphaFunc(516, 0.5F);
		this.client.getProfiler().push("center");
		this.method_3178(f, l);
		this.client.getProfiler().pop();
	}

	private void method_3178(float f, long l) {
		WorldRenderer worldRenderer = this.client.field_1769;
		ParticleManager particleManager = this.client.particleManager;
		boolean bl = this.method_3202();
		GlStateManager.enableCull();
		this.client.getProfiler().swap("clear");
		GlStateManager.viewport(0, 0, this.client.window.getWindowWidth(), this.client.window.getWindowHeight());
		this.field_3990.method_3210(f);
		GlStateManager.clear(16640, MinecraftClient.isSystemMac);
		this.client.getProfiler().swap("camera");
		this.method_3185(f);
		class_857 lv = class_855.method_3696();
		class_295.method_1373(this.client.player, this.client.field_1690.field_1850 == 2, this.field_4025, lv);
		this.client.getProfiler().swap("culling");
		class_856 lv2 = new class_858(lv);
		Entity entity = this.client.getCameraEntity();
		double d = MathHelper.lerp((double)f, entity.prevRenderX, entity.x);
		double e = MathHelper.lerp((double)f, entity.prevRenderY, entity.y);
		double g = MathHelper.lerp((double)f, entity.prevRenderZ, entity.z);
		lv2.method_3700(d, e, g);
		if (this.client.field_1690.viewDistance >= 4) {
			this.field_3990.method_3211(-1, f);
			this.client.getProfiler().swap("sky");
			GlStateManager.matrixMode(5889);
			GlStateManager.loadIdentity();
			GlStateManager.multMatrix(
				Matrix4f.method_4929(
					this.method_3196(f, true), (float)this.client.window.getWindowWidth() / (float)this.client.window.getWindowHeight(), 0.05F, this.field_4025 * 2.0F
				)
			);
			GlStateManager.matrixMode(5888);
			worldRenderer.renderSky(f);
			GlStateManager.matrixMode(5889);
			GlStateManager.loadIdentity();
			GlStateManager.multMatrix(
				Matrix4f.method_4929(
					this.method_3196(f, true),
					(float)this.client.window.getWindowWidth() / (float)this.client.window.getWindowHeight(),
					0.05F,
					this.field_4025 * MathHelper.SQUARE_ROOT_OF_TWO
				)
			);
			GlStateManager.matrixMode(5888);
		}

		this.field_3990.method_3211(0, f);
		GlStateManager.shadeModel(7425);
		if (entity.y + (double)entity.getEyeHeight() < 128.0) {
			this.method_3206(worldRenderer, f, d, e, g);
		}

		this.client.getProfiler().swap("prepareterrain");
		this.field_3990.method_3211(0, f);
		this.client.getTextureManager().bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
		GuiLighting.disable();
		this.client.getProfiler().swap("terrain_setup");
		this.client.world.getChunkProvider().getLightingProvider().doLightUpdates(Integer.MAX_VALUE, true, true);
		worldRenderer.method_3273(entity, f, lv2, this.field_4021++, this.client.player.isSpectator());
		this.client.getProfiler().swap("updatechunks");
		this.client.field_1769.method_3269(l);
		this.client.getProfiler().swap("terrain");
		GlStateManager.matrixMode(5888);
		GlStateManager.pushMatrix();
		GlStateManager.disableAlphaTest();
		worldRenderer.method_3251(BlockRenderLayer.SOLID, (double)f, entity);
		GlStateManager.enableAlphaTest();
		worldRenderer.method_3251(BlockRenderLayer.MIPPED_CUTOUT, (double)f, entity);
		this.client.getTextureManager().getTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX).pushFilter(false, false);
		worldRenderer.method_3251(BlockRenderLayer.CUTOUT, (double)f, entity);
		this.client.getTextureManager().getTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX).popFilter();
		GlStateManager.shadeModel(7424);
		GlStateManager.alphaFunc(516, 0.1F);
		GlStateManager.matrixMode(5888);
		GlStateManager.popMatrix();
		GlStateManager.pushMatrix();
		GuiLighting.enable();
		this.client.getProfiler().swap("entities");
		worldRenderer.renderEntities(entity, lv2, f);
		GuiLighting.disable();
		this.disableLightmap();
		GlStateManager.matrixMode(5888);
		GlStateManager.popMatrix();
		if (bl && this.client.hitResult != null) {
			PlayerEntity playerEntity = (PlayerEntity)entity;
			GlStateManager.disableAlphaTest();
			this.client.getProfiler().swap("outline");
			worldRenderer.drawHighlightedBlockOutline(playerEntity, this.client.hitResult, 0, f);
			GlStateManager.enableAlphaTest();
		}

		if (this.client.renderDebug.method_3710()) {
			this.client.renderDebug.renderDebuggers(f, l);
		}

		this.client.getProfiler().swap("destroyProgress");
		GlStateManager.enableBlend();
		GlStateManager.blendFuncSeparate(
			GlStateManager.SrcBlendFactor.SRC_ALPHA, GlStateManager.DstBlendFactor.ONE, GlStateManager.SrcBlendFactor.ONE, GlStateManager.DstBlendFactor.ZERO
		);
		this.client.getTextureManager().getTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX).pushFilter(false, false);
		worldRenderer.renderPartiallyBrokenBlocks(Tessellator.getInstance(), Tessellator.getInstance().getBufferBuilder(), entity, f);
		this.client.getTextureManager().getTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX).popFilter();
		GlStateManager.disableBlend();
		this.enableLightmap();
		this.client.getProfiler().swap("litParticles");
		particleManager.renderLitParticles(entity, f);
		GuiLighting.disable();
		this.field_3990.method_3211(0, f);
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
			GlStateManager.SrcBlendFactor.SRC_ALPHA,
			GlStateManager.DstBlendFactor.ONE_MINUS_SRC_ALPHA,
			GlStateManager.SrcBlendFactor.ONE,
			GlStateManager.DstBlendFactor.ZERO
		);
		GlStateManager.alphaFunc(516, 0.1F);
		this.field_3990.method_3211(0, f);
		GlStateManager.enableBlend();
		GlStateManager.depthMask(false);
		this.client.getTextureManager().bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
		GlStateManager.shadeModel(7425);
		this.client.getProfiler().swap("translucent");
		worldRenderer.method_3251(BlockRenderLayer.TRANSLUCENT, (double)f, entity);
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
			GlStateManager.clear(256, MinecraftClient.isSystemMac);
			this.method_3172(f);
		}
	}

	private void method_3206(WorldRenderer worldRenderer, float f, double d, double e, double g) {
		if (this.client.field_1690.getCloudRenderMode() != 0) {
			this.client.getProfiler().swap("clouds");
			GlStateManager.matrixMode(5889);
			GlStateManager.loadIdentity();
			GlStateManager.multMatrix(
				Matrix4f.method_4929(
					this.method_3196(f, true), (float)this.client.window.getWindowWidth() / (float)this.client.window.getWindowHeight(), 0.05F, this.field_4025 * 4.0F
				)
			);
			GlStateManager.matrixMode(5888);
			GlStateManager.pushMatrix();
			this.field_3990.method_3211(0, f);
			worldRenderer.renderClouds(f, d, e, g);
			GlStateManager.disableFog();
			GlStateManager.popMatrix();
			GlStateManager.matrixMode(5889);
			GlStateManager.loadIdentity();
			GlStateManager.multMatrix(
				Matrix4f.method_4929(
					this.method_3196(f, true),
					(float)this.client.window.getWindowWidth() / (float)this.client.window.getWindowHeight(),
					0.05F,
					this.field_4025 * MathHelper.SQUARE_ROOT_OF_TWO
				)
			);
			GlStateManager.matrixMode(5888);
		}
	}

	private void method_3177() {
		float f = this.client.world.getRainGradient(1.0F);
		if (!this.client.field_1690.fancyGraphics) {
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
			if (this.client.field_1690.particles == 1) {
				k >>= 1;
			} else if (this.client.field_1690.particles == 2) {
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
					double o = (double)fluidState.method_15763();
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
						if (!fluidState.matches(FluidTags.field_15518) && blockState.getBlock() != Blocks.field_10092) {
							if (this.random.nextInt(++j) == 0) {
								d = (double)blockPos3.getX() + h;
								e = (double)((float)blockPos3.getY() + 0.1F) + p - 1.0;
								g = (double)blockPos3.getZ() + m;
							}

							this.client
								.world
								.method_8406(
									ParticleTypes.field_11242, (double)blockPos3.getX() + h, (double)((float)blockPos3.getY() + 0.1F) + p, (double)blockPos3.getZ() + m, 0.0, 0.0, 0.0
								);
						} else {
							this.client
								.world
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
				GlStateManager.SrcBlendFactor.SRC_ALPHA,
				GlStateManager.DstBlendFactor.ONE_MINUS_SRC_ALPHA,
				GlStateManager.SrcBlendFactor.ONE,
				GlStateManager.DstBlendFactor.ZERO
			);
			GlStateManager.alphaFunc(516, 0.1F);
			double d = MathHelper.lerp((double)f, entity.prevRenderX, entity.x);
			double e = MathHelper.lerp((double)f, entity.prevRenderY, entity.y);
			double h = MathHelper.lerp((double)f, entity.prevRenderZ, entity.z);
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
		this.field_3990.method_3212(bl);
	}

	public void method_3203() {
		this.field_4006 = null;
		this.field_4026.method_1771();
	}

	public class_330 method_3194() {
		return this.field_4026;
	}

	public static void method_3179(FontRenderer fontRenderer, String string, float f, float g, float h, int i, float j, float k, boolean bl, boolean bl2) {
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
			GlStateManager.SrcBlendFactor.SRC_ALPHA,
			GlStateManager.DstBlendFactor.ONE_MINUS_SRC_ALPHA,
			GlStateManager.SrcBlendFactor.ONE,
			GlStateManager.DstBlendFactor.ZERO
		);
		int l = fontRenderer.getStringWidth(string) / 2;
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
			fontRenderer.draw(string, (float)(-fontRenderer.getStringWidth(string) / 2), (float)i, 553648127);
			GlStateManager.enableDepthTest();
		}

		GlStateManager.depthMask(true);
		fontRenderer.draw(string, (float)(-fontRenderer.getStringWidth(string) / 2), (float)i, bl2 ? 553648127 : -1);
		GlStateManager.enableLighting();
		GlStateManager.disableBlend();
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.popMatrix();
	}

	public void method_3189(ItemStack itemStack) {
		this.field_4006 = itemStack;
		this.field_4007 = 40;
		this.field_4029 = this.random.nextFloat() * 2.0F - 1.0F;
		this.field_4003 = this.random.nextFloat() * 2.0F - 1.0F;
	}

	private void method_3171(int i, int j, float f) {
		if (this.field_4006 != null && this.field_4007 > 0) {
			int k = 40 - this.field_4007;
			float g = ((float)k + f) / 40.0F;
			float h = g * g;
			float l = g * h;
			float m = 10.25F * l * h - 24.95F * h * h + 25.5F * l - 13.8F * h + 4.0F * g;
			float n = m * (float) Math.PI;
			float o = this.field_4029 * (float)(i / 4);
			float p = this.field_4003 * (float)(j / 4);
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
			this.client.getItemRenderer().renderItemWithTransformation(this.field_4006, ModelTransformation.Type.FIXED);
			GlStateManager.popAttributes();
			GlStateManager.popMatrix();
			GuiLighting.disable();
			GlStateManager.enableCull();
			GlStateManager.disableDepthTest();
		}
	}

	public MinecraftClient getGame() {
		return this.client;
	}

	public float method_3195(float f) {
		return MathHelper.lerp(f, this.field_3997, this.field_4016);
	}

	public float method_3193() {
		return this.field_4025;
	}
}
