package net.minecraft.client.render;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import java.nio.FloatBuffer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.GlAllocationUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.fluid.FluidState;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;

@Environment(EnvType.CLIENT)
public class BackgroundRenderer {
	private final FloatBuffer blackColorBuffer = GlAllocationUtils.allocateFloatBuffer(16);
	private final FloatBuffer colorBuffer = GlAllocationUtils.allocateFloatBuffer(16);
	private float red;
	private float green;
	private float blue;
	private float bufferRed = -1.0F;
	private float bufferGreen = -1.0F;
	private float bufferBlue = -1.0F;
	private int waterFogColor = -1;
	private int nextWaterFogColor = -1;
	private long lastWaterFogColorUpdateTime = -1L;
	private final GameRenderer gameRenderer;
	private final MinecraftClient client;

	public BackgroundRenderer(GameRenderer gameRenderer) {
		this.gameRenderer = gameRenderer;
		this.client = gameRenderer.getClient();
		this.blackColorBuffer.put(0.0F).put(0.0F).put(0.0F).put(1.0F).flip();
	}

	public void renderBackground(float f) {
		World world = this.client.world;
		Entity entity = this.client.getCameraEntity();
		FluidState fluidState = CameraHelper.method_1374(this.client.world, entity, f);
		if (fluidState.matches(FluidTags.field_15517)) {
			this.updateColorInWater(entity, world, f);
		} else if (fluidState.matches(FluidTags.field_15518)) {
			this.red = 0.6F;
			this.green = 0.1F;
			this.blue = 0.0F;
			this.lastWaterFogColorUpdateTime = -1L;
		} else {
			this.updateColorNotInWater(entity, world, f);
			this.lastWaterFogColorUpdateTime = -1L;
		}

		double d = MathHelper.lerp((double)f, entity.prevRenderY, entity.y) * world.dimension.getHorizonShadingRatio();
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
			this.red = (float)((double)this.red * d);
			this.green = (float)((double)this.green * d);
			this.blue = (float)((double)this.blue * d);
		}

		if (this.gameRenderer.getSkyDarkness(f) > 0.0F) {
			float g = this.gameRenderer.getSkyDarkness(f);
			this.red = this.red * (1.0F - g) + this.red * 0.7F * g;
			this.green = this.green * (1.0F - g) + this.green * 0.6F * g;
			this.blue = this.blue * (1.0F - g) + this.blue * 0.6F * g;
		}

		if (fluidState.matches(FluidTags.field_15517)) {
			float g = 0.0F;
			if (entity instanceof ClientPlayerEntity) {
				ClientPlayerEntity clientPlayerEntity = (ClientPlayerEntity)entity;
				g = clientPlayerEntity.method_3140();
			}

			float h = 1.0F / this.red;
			if (h > 1.0F / this.green) {
				h = 1.0F / this.green;
			}

			if (h > 1.0F / this.blue) {
				h = 1.0F / this.blue;
			}

			this.red = this.red * (1.0F - g) + this.red * h * g;
			this.green = this.green * (1.0F - g) + this.green * h * g;
			this.blue = this.blue * (1.0F - g) + this.blue * h * g;
		} else if (entity instanceof LivingEntity && ((LivingEntity)entity).hasPotionEffect(StatusEffects.field_5925)) {
			float gx = this.gameRenderer.method_3174((LivingEntity)entity, f);
			float hx = 1.0F / this.red;
			if (hx > 1.0F / this.green) {
				hx = 1.0F / this.green;
			}

			if (hx > 1.0F / this.blue) {
				hx = 1.0F / this.blue;
			}

			this.red = this.red * (1.0F - gx) + this.red * hx * gx;
			this.green = this.green * (1.0F - gx) + this.green * hx * gx;
			this.blue = this.blue * (1.0F - gx) + this.blue * hx * gx;
		}

		GlStateManager.clearColor(this.red, this.green, this.blue, 0.0F);
	}

	private void updateColorNotInWater(Entity entity, World world, float f) {
		float g = 0.25F + 0.75F * (float)this.client.options.viewDistance / 32.0F;
		g = 1.0F - (float)Math.pow((double)g, 0.25);
		net.minecraft.util.math.Vec3d vec3d = world.getSkyColor(this.client.getCameraEntity(), f);
		float h = (float)vec3d.x;
		float i = (float)vec3d.y;
		float j = (float)vec3d.z;
		net.minecraft.util.math.Vec3d vec3d2 = world.getFogColor(f);
		this.red = (float)vec3d2.x;
		this.green = (float)vec3d2.y;
		this.blue = (float)vec3d2.z;
		if (this.client.options.viewDistance >= 4) {
			double d = MathHelper.sin(world.method_8442(f)) > 0.0F ? -1.0 : 1.0;
			net.minecraft.util.math.Vec3d vec3d3 = new net.minecraft.util.math.Vec3d(d, 0.0, 0.0);
			float k = (float)entity.getRotationVec(f).dotProduct(vec3d3);
			if (k < 0.0F) {
				k = 0.0F;
			}

			if (k > 0.0F) {
				float[] fs = world.dimension.getBackgroundColor(world.getSkyAngle(f), f);
				if (fs != null) {
					k *= fs[3];
					this.red = this.red * (1.0F - k) + fs[0] * k;
					this.green = this.green * (1.0F - k) + fs[1] * k;
					this.blue = this.blue * (1.0F - k) + fs[2] * k;
				}
			}
		}

		this.red = this.red + (h - this.red) * g;
		this.green = this.green + (i - this.green) * g;
		this.blue = this.blue + (j - this.blue) * g;
		float l = world.getRainGradient(f);
		if (l > 0.0F) {
			float m = 1.0F - l * 0.5F;
			float n = 1.0F - l * 0.4F;
			this.red *= m;
			this.green *= m;
			this.blue *= n;
		}

		float m = world.getThunderGradient(f);
		if (m > 0.0F) {
			float n = 1.0F - m * 0.5F;
			this.red *= n;
			this.green *= n;
			this.blue *= n;
		}
	}

	private void updateColorInWater(Entity entity, ViewableWorld viewableWorld, float f) {
		long l = SystemUtil.getMeasuringTimeMs();
		int i = viewableWorld.getBiome(new BlockPos(CameraHelper.interpolateEntityPos(entity, (double)f))).getWaterFogColor();
		if (this.lastWaterFogColorUpdateTime < 0L) {
			this.waterFogColor = i;
			this.nextWaterFogColor = i;
			this.lastWaterFogColorUpdateTime = l;
		}

		int j = this.waterFogColor >> 16 & 0xFF;
		int k = this.waterFogColor >> 8 & 0xFF;
		int m = this.waterFogColor & 0xFF;
		int n = this.nextWaterFogColor >> 16 & 0xFF;
		int o = this.nextWaterFogColor >> 8 & 0xFF;
		int p = this.nextWaterFogColor & 0xFF;
		float g = MathHelper.clamp((float)(l - this.lastWaterFogColorUpdateTime) / 5000.0F, 0.0F, 1.0F);
		float h = MathHelper.lerp(g, (float)n, (float)j);
		float q = MathHelper.lerp(g, (float)o, (float)k);
		float r = MathHelper.lerp(g, (float)p, (float)m);
		this.red = h / 255.0F;
		this.green = q / 255.0F;
		this.blue = r / 255.0F;
		if (this.waterFogColor != i) {
			this.waterFogColor = i;
			this.nextWaterFogColor = MathHelper.floor(h) << 16 | MathHelper.floor(q) << 8 | MathHelper.floor(r);
			this.lastWaterFogColorUpdateTime = l;
		}
	}

	public void applyFog(int i, float f) {
		Entity entity = this.client.getCameraEntity();
		this.updateFogColor(false);
		GlStateManager.normal3f(0.0F, -1.0F, 0.0F);
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		FluidState fluidState = CameraHelper.method_1374(this.client.world, entity, f);
		if (entity instanceof LivingEntity && ((LivingEntity)entity).hasPotionEffect(StatusEffects.field_5919)) {
			float g = 5.0F;
			int j = ((LivingEntity)entity).getPotionEffect(StatusEffects.field_5919).getDuration();
			if (j < 20) {
				g = MathHelper.lerp(1.0F - (float)j / 20.0F, 5.0F, this.gameRenderer.getViewDistance());
			}

			GlStateManager.fogMode(GlStateManager.FogMode.field_5095);
			if (i == -1) {
				GlStateManager.fogStart(0.0F);
				GlStateManager.fogEnd(g * 0.8F);
			} else {
				GlStateManager.fogStart(g * 0.25F);
				GlStateManager.fogEnd(g);
			}

			GLX.setupNvFogDistance();
		} else if (fluidState.matches(FluidTags.field_15517)) {
			GlStateManager.fogMode(GlStateManager.FogMode.field_5097);
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
			GlStateManager.fogMode(GlStateManager.FogMode.field_5096);
			GlStateManager.fogDensity(2.0F);
		} else {
			float gx = this.gameRenderer.getViewDistance();
			GlStateManager.fogMode(GlStateManager.FogMode.field_5095);
			if (i == -1) {
				GlStateManager.fogStart(0.0F);
				GlStateManager.fogEnd(gx);
			} else {
				GlStateManager.fogStart(gx * 0.75F);
				GlStateManager.fogEnd(gx);
			}

			GLX.setupNvFogDistance();
			if (this.client.world.dimension.shouldRenderFog((int)entity.x, (int)entity.z) || this.client.inGameHud.getBossBarHud().shouldThickenFog()) {
				GlStateManager.fogStart(gx * 0.05F);
				GlStateManager.fogEnd(Math.min(gx, 192.0F) * 0.5F);
			}
		}

		GlStateManager.enableColorMaterial();
		GlStateManager.enableFog();
		GlStateManager.colorMaterial(1028, 4608);
	}

	public void updateFogColor(boolean bl) {
		if (bl) {
			GlStateManager.fog(2918, this.blackColorBuffer);
		} else {
			GlStateManager.fog(2918, this.getColorAsBuffer());
		}
	}

	private FloatBuffer getColorAsBuffer() {
		if (this.bufferRed != this.red || this.bufferGreen != this.green || this.bufferBlue != this.blue) {
			this.colorBuffer.clear();
			this.colorBuffer.put(this.red).put(this.green).put(this.blue).put(1.0F);
			this.colorBuffer.flip();
			this.bufferRed = this.red;
			this.bufferGreen = this.green;
			this.bufferBlue = this.blue;
		}

		return this.colorBuffer;
	}
}
