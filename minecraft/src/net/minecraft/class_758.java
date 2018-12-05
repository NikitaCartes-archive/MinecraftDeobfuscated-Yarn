package net.minecraft;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import java.nio.FloatBuffer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.GlAllocationUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.fluid.FluidState;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;

@Environment(EnvType.CLIENT)
public class class_758 {
	private final FloatBuffer field_4036 = GlAllocationUtils.allocateFloatBuffer(16);
	private final FloatBuffer field_4038 = GlAllocationUtils.allocateFloatBuffer(16);
	private float field_4034;
	private float field_4033;
	private float field_4032;
	private float field_4030 = -1.0F;
	private float field_4040 = -1.0F;
	private float field_4039 = -1.0F;
	private int field_4031 = -1;
	private int field_4041 = -1;
	private long field_4042 = -1L;
	private final WorldRenderer field_4035;
	private final MinecraftClient field_4037;

	public class_758(WorldRenderer worldRenderer) {
		this.field_4035 = worldRenderer;
		this.field_4037 = worldRenderer.getGame();
		this.field_4036.put(0.0F).put(0.0F).put(0.0F).put(1.0F).flip();
	}

	public void method_3210(float f) {
		World world = this.field_4037.world;
		Entity entity = this.field_4037.getCameraEntity();
		BlockState blockState = class_295.method_1376(this.field_4037.world, entity, f);
		FluidState fluidState = class_295.method_1374(this.field_4037.world, entity, f);
		if (fluidState.matches(FluidTags.field_15517)) {
			this.method_3213(entity, world, f);
		} else if (fluidState.matches(FluidTags.field_15518)) {
			this.field_4034 = 0.6F;
			this.field_4033 = 0.1F;
			this.field_4032 = 0.0F;
			this.field_4042 = -1L;
		} else {
			this.method_3208(entity, world, f);
			this.field_4042 = -1L;
		}

		double d = MathHelper.lerp((double)f, entity.prevRenderY, entity.y) * world.dimension.method_12459();
		if (entity instanceof LivingEntity && ((LivingEntity)entity).hasPotionEffect(StatusEffects.field_5919)) {
			int i = ((LivingEntity)entity).getPotionEffect(StatusEffects.field_5919).getDuration();
			if (i < 20) {
				d *= (double)(1.0F - (float)i / 20.0F);
			} else {
				d = 0.0;
			}
		}

		if (d < 1.0) {
			if (d < 0.0) {
				d = 0.0;
			}

			d *= d;
			this.field_4034 = (float)((double)this.field_4034 * d);
			this.field_4033 = (float)((double)this.field_4033 * d);
			this.field_4032 = (float)((double)this.field_4032 * d);
		}

		if (this.field_4035.method_3195(f) > 0.0F) {
			float g = this.field_4035.method_3195(f);
			this.field_4034 = this.field_4034 * (1.0F - g) + this.field_4034 * 0.7F * g;
			this.field_4033 = this.field_4033 * (1.0F - g) + this.field_4033 * 0.6F * g;
			this.field_4032 = this.field_4032 * (1.0F - g) + this.field_4032 * 0.6F * g;
		}

		if (fluidState.matches(FluidTags.field_15517)) {
			float g = 0.0F;
			if (entity instanceof ClientPlayerEntity) {
				ClientPlayerEntity clientPlayerEntity = (ClientPlayerEntity)entity;
				g = clientPlayerEntity.method_3140();
			}

			float h = 1.0F / this.field_4034;
			if (h > 1.0F / this.field_4033) {
				h = 1.0F / this.field_4033;
			}

			if (h > 1.0F / this.field_4032) {
				h = 1.0F / this.field_4032;
			}

			this.field_4034 = this.field_4034 * (1.0F - g) + this.field_4034 * h * g;
			this.field_4033 = this.field_4033 * (1.0F - g) + this.field_4033 * h * g;
			this.field_4032 = this.field_4032 * (1.0F - g) + this.field_4032 * h * g;
		} else if (entity instanceof LivingEntity && ((LivingEntity)entity).hasPotionEffect(StatusEffects.field_5925)) {
			float gx = this.field_4035.method_3174((LivingEntity)entity, f);
			float hx = 1.0F / this.field_4034;
			if (hx > 1.0F / this.field_4033) {
				hx = 1.0F / this.field_4033;
			}

			if (hx > 1.0F / this.field_4032) {
				hx = 1.0F / this.field_4032;
			}

			this.field_4034 = this.field_4034 * (1.0F - gx) + this.field_4034 * hx * gx;
			this.field_4033 = this.field_4033 * (1.0F - gx) + this.field_4033 * hx * gx;
			this.field_4032 = this.field_4032 * (1.0F - gx) + this.field_4032 * hx * gx;
		}

		GlStateManager.clearColor(this.field_4034, this.field_4033, this.field_4032, 0.0F);
	}

	private void method_3208(Entity entity, World world, float f) {
		float g = 0.25F + 0.75F * (float)this.field_4037.options.viewDistance / 32.0F;
		g = 1.0F - (float)Math.pow((double)g, 0.25);
		Vec3d vec3d = world.method_8548(this.field_4037.getCameraEntity(), f);
		float h = (float)vec3d.x;
		float i = (float)vec3d.y;
		float j = (float)vec3d.z;
		Vec3d vec3d2 = world.getFogColor(f);
		this.field_4034 = (float)vec3d2.x;
		this.field_4033 = (float)vec3d2.y;
		this.field_4032 = (float)vec3d2.z;
		if (this.field_4037.options.viewDistance >= 4) {
			double d = MathHelper.sin(world.method_8442(f)) > 0.0F ? -1.0 : 1.0;
			Vec3d vec3d3 = new Vec3d(d, 0.0, 0.0);
			float k = (float)entity.getRotationVec(f).dotProduct(vec3d3);
			if (k < 0.0F) {
				k = 0.0F;
			}

			if (k > 0.0F) {
				float[] fs = world.dimension.method_12446(world.method_8400(f), f);
				if (fs != null) {
					k *= fs[3];
					this.field_4034 = this.field_4034 * (1.0F - k) + fs[0] * k;
					this.field_4033 = this.field_4033 * (1.0F - k) + fs[1] * k;
					this.field_4032 = this.field_4032 * (1.0F - k) + fs[2] * k;
				}
			}
		}

		this.field_4034 = this.field_4034 + (h - this.field_4034) * g;
		this.field_4033 = this.field_4033 + (i - this.field_4033) * g;
		this.field_4032 = this.field_4032 + (j - this.field_4032) * g;
		float l = world.getRainGradient(f);
		if (l > 0.0F) {
			float m = 1.0F - l * 0.5F;
			float n = 1.0F - l * 0.4F;
			this.field_4034 *= m;
			this.field_4033 *= m;
			this.field_4032 *= n;
		}

		float m = world.getThunderGradient(f);
		if (m > 0.0F) {
			float n = 1.0F - m * 0.5F;
			this.field_4034 *= n;
			this.field_4033 *= n;
			this.field_4032 *= n;
		}
	}

	private void method_3213(Entity entity, ViewableWorld viewableWorld, float f) {
		long l = SystemUtil.getMeasuringTimeMili();
		int i = viewableWorld.getBiome(new BlockPos(class_295.method_1379(entity, (double)f))).getWaterFogColor();
		if (this.field_4042 < 0L) {
			this.field_4031 = i;
			this.field_4041 = i;
			this.field_4042 = l;
		}

		int j = this.field_4031 >> 16 & 0xFF;
		int k = this.field_4031 >> 8 & 0xFF;
		int m = this.field_4031 & 0xFF;
		int n = this.field_4041 >> 16 & 0xFF;
		int o = this.field_4041 >> 8 & 0xFF;
		int p = this.field_4041 & 0xFF;
		float g = MathHelper.clamp((float)(l - this.field_4042) / 5000.0F, 0.0F, 1.0F);
		float h = MathHelper.lerp(g, (float)n, (float)j);
		float q = MathHelper.lerp(g, (float)o, (float)k);
		float r = MathHelper.lerp(g, (float)p, (float)m);
		this.field_4034 = h / 255.0F;
		this.field_4033 = q / 255.0F;
		this.field_4032 = r / 255.0F;
		if (this.field_4031 != i) {
			this.field_4031 = i;
			this.field_4041 = MathHelper.floor(h) << 16 | MathHelper.floor(q) << 8 | MathHelper.floor(r);
			this.field_4042 = l;
		}
	}

	public void method_3211(int i, float f) {
		Entity entity = this.field_4037.getCameraEntity();
		this.method_3212(false);
		GlStateManager.normal3f(0.0F, -1.0F, 0.0F);
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		FluidState fluidState = class_295.method_1374(this.field_4037.world, entity, f);
		if (entity instanceof LivingEntity && ((LivingEntity)entity).hasPotionEffect(StatusEffects.field_5919)) {
			float g = 5.0F;
			int j = ((LivingEntity)entity).getPotionEffect(StatusEffects.field_5919).getDuration();
			if (j < 20) {
				g = MathHelper.lerp(1.0F - (float)j / 20.0F, 5.0F, this.field_4035.method_3193());
			}

			GlStateManager.fogMode(GlStateManager.FogMode.LINEAR);
			if (i == -1) {
				GlStateManager.fogStart(0.0F);
				GlStateManager.fogEnd(g * 0.8F);
			} else {
				GlStateManager.fogStart(g * 0.25F);
				GlStateManager.fogEnd(g);
			}

			GLX.setupNvFogDistance();
		} else if (fluidState.matches(FluidTags.field_15517)) {
			GlStateManager.fogMode(GlStateManager.FogMode.EXP2);
			if (entity instanceof LivingEntity) {
				if (entity instanceof ClientPlayerEntity) {
					ClientPlayerEntity clientPlayerEntity = (ClientPlayerEntity)entity;
					float h = 0.05F - clientPlayerEntity.method_3140() * clientPlayerEntity.method_3140() * 0.03F;
					Biome biome = clientPlayerEntity.world.getBiome(new BlockPos(clientPlayerEntity));
					if (biome == Biomes.field_9471 || biome == Biomes.field_9479) {
						h += 0.005F;
					}

					GlStateManager.fogDensity(h);
				} else {
					GlStateManager.fogDensity(0.05F);
				}
			} else {
				GlStateManager.fogDensity(0.1F);
			}
		} else if (fluidState.matches(FluidTags.field_15518)) {
			GlStateManager.fogMode(GlStateManager.FogMode.EXP);
			GlStateManager.fogDensity(2.0F);
		} else {
			float gx = this.field_4035.method_3193();
			GlStateManager.fogMode(GlStateManager.FogMode.LINEAR);
			if (i == -1) {
				GlStateManager.fogStart(0.0F);
				GlStateManager.fogEnd(gx);
			} else {
				GlStateManager.fogStart(gx * 0.75F);
				GlStateManager.fogEnd(gx);
			}

			GLX.setupNvFogDistance();
			if (this.field_4037.world.dimension.method_12453((int)entity.x, (int)entity.z) || this.field_4037.hudInGame.getHudBossBar().shouldThickenFog()) {
				GlStateManager.fogStart(gx * 0.05F);
				GlStateManager.fogEnd(Math.min(gx, 192.0F) * 0.5F);
			}
		}

		GlStateManager.enableColorMaterial();
		GlStateManager.enableFog();
		GlStateManager.colorMaterial(1028, 4608);
	}

	public void method_3212(boolean bl) {
		if (bl) {
			GlStateManager.fog(2918, this.field_4036);
		} else {
			GlStateManager.fog(2918, this.method_3209());
		}
	}

	private FloatBuffer method_3209() {
		if (this.field_4030 != this.field_4034 || this.field_4040 != this.field_4033 || this.field_4039 != this.field_4032) {
			this.field_4038.clear();
			this.field_4038.put(this.field_4034).put(this.field_4033).put(this.field_4032).put(1.0F);
			this.field_4038.flip();
			this.field_4030 = this.field_4034;
			this.field_4040 = this.field_4033;
			this.field_4039 = this.field_4032;
		}

		return this.field_4038;
	}
}
