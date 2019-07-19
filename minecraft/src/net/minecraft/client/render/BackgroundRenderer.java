package net.minecraft.client.render;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import java.nio.FloatBuffer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.GlAllocationUtils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.fluid.FluidState;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.CollisionView;
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

	public void renderBackground(Camera camera, float tickDelta) {
		World world = this.client.world;
		FluidState fluidState = camera.getSubmergedFluidState();
		if (fluidState.matches(FluidTags.WATER)) {
			this.updateColorInWater(camera, world);
		} else if (fluidState.matches(FluidTags.LAVA)) {
			this.red = 0.6F;
			this.green = 0.1F;
			this.blue = 0.0F;
			this.lastWaterFogColorUpdateTime = -1L;
		} else {
			this.updateColorNotInWater(camera, world, tickDelta);
			this.lastWaterFogColorUpdateTime = -1L;
		}

		double d = camera.getPos().y * world.dimension.getHorizonShadingRatio();
		if (camera.getFocusedEntity() instanceof LivingEntity && ((LivingEntity)camera.getFocusedEntity()).hasStatusEffect(StatusEffects.BLINDNESS)) {
			int i = ((LivingEntity)camera.getFocusedEntity()).getStatusEffect(StatusEffects.BLINDNESS).getDuration();
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

		if (this.gameRenderer.getSkyDarkness(tickDelta) > 0.0F) {
			float f = this.gameRenderer.getSkyDarkness(tickDelta);
			this.red = this.red * (1.0F - f) + this.red * 0.7F * f;
			this.green = this.green * (1.0F - f) + this.green * 0.6F * f;
			this.blue = this.blue * (1.0F - f) + this.blue * 0.6F * f;
		}

		if (fluidState.matches(FluidTags.WATER)) {
			float f = 0.0F;
			if (camera.getFocusedEntity() instanceof ClientPlayerEntity) {
				ClientPlayerEntity clientPlayerEntity = (ClientPlayerEntity)camera.getFocusedEntity();
				f = clientPlayerEntity.method_3140();
			}

			float g = 1.0F / this.red;
			if (g > 1.0F / this.green) {
				g = 1.0F / this.green;
			}

			if (g > 1.0F / this.blue) {
				g = 1.0F / this.blue;
			}

			this.red = this.red * (1.0F - f) + this.red * g * f;
			this.green = this.green * (1.0F - f) + this.green * g * f;
			this.blue = this.blue * (1.0F - f) + this.blue * g * f;
		} else if (camera.getFocusedEntity() instanceof LivingEntity && ((LivingEntity)camera.getFocusedEntity()).hasStatusEffect(StatusEffects.NIGHT_VISION)) {
			float fx = this.gameRenderer.getNightVisionStrength((LivingEntity)camera.getFocusedEntity(), tickDelta);
			float gx = 1.0F / this.red;
			if (gx > 1.0F / this.green) {
				gx = 1.0F / this.green;
			}

			if (gx > 1.0F / this.blue) {
				gx = 1.0F / this.blue;
			}

			this.red = this.red * (1.0F - fx) + this.red * gx * fx;
			this.green = this.green * (1.0F - fx) + this.green * gx * fx;
			this.blue = this.blue * (1.0F - fx) + this.blue * gx * fx;
		}

		GlStateManager.clearColor(this.red, this.green, this.blue, 0.0F);
	}

	private void updateColorNotInWater(Camera camera, World world, float tickDelta) {
		float f = 0.25F + 0.75F * (float)this.client.options.viewDistance / 32.0F;
		f = 1.0F - (float)Math.pow((double)f, 0.25);
		Vec3d vec3d = world.getSkyColor(camera.getBlockPos(), tickDelta);
		float g = (float)vec3d.x;
		float h = (float)vec3d.y;
		float i = (float)vec3d.z;
		Vec3d vec3d2 = world.getFogColor(tickDelta);
		this.red = (float)vec3d2.x;
		this.green = (float)vec3d2.y;
		this.blue = (float)vec3d2.z;
		if (this.client.options.viewDistance >= 4) {
			double d = MathHelper.sin(world.getSkyAngleRadians(tickDelta)) > 0.0F ? -1.0 : 1.0;
			Vec3d vec3d3 = new Vec3d(d, 0.0, 0.0);
			float j = (float)camera.getHorizontalPlane().dotProduct(vec3d3);
			if (j < 0.0F) {
				j = 0.0F;
			}

			if (j > 0.0F) {
				float[] fs = world.dimension.getBackgroundColor(world.getSkyAngle(tickDelta), tickDelta);
				if (fs != null) {
					j *= fs[3];
					this.red = this.red * (1.0F - j) + fs[0] * j;
					this.green = this.green * (1.0F - j) + fs[1] * j;
					this.blue = this.blue * (1.0F - j) + fs[2] * j;
				}
			}
		}

		this.red = this.red + (g - this.red) * f;
		this.green = this.green + (h - this.green) * f;
		this.blue = this.blue + (i - this.blue) * f;
		float k = world.getRainGradient(tickDelta);
		if (k > 0.0F) {
			float l = 1.0F - k * 0.5F;
			float m = 1.0F - k * 0.4F;
			this.red *= l;
			this.green *= l;
			this.blue *= m;
		}

		float l = world.getThunderGradient(tickDelta);
		if (l > 0.0F) {
			float m = 1.0F - l * 0.5F;
			this.red *= m;
			this.green *= m;
			this.blue *= m;
		}
	}

	private void updateColorInWater(Camera camera, CollisionView world) {
		long l = Util.getMeasuringTimeMs();
		int i = world.getBiome(new BlockPos(camera.getPos())).getWaterFogColor();
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
		float f = MathHelper.clamp((float)(l - this.lastWaterFogColorUpdateTime) / 5000.0F, 0.0F, 1.0F);
		float g = MathHelper.lerp(f, (float)n, (float)j);
		float h = MathHelper.lerp(f, (float)o, (float)k);
		float q = MathHelper.lerp(f, (float)p, (float)m);
		this.red = g / 255.0F;
		this.green = h / 255.0F;
		this.blue = q / 255.0F;
		if (this.waterFogColor != i) {
			this.waterFogColor = i;
			this.nextWaterFogColor = MathHelper.floor(g) << 16 | MathHelper.floor(h) << 8 | MathHelper.floor(q);
			this.lastWaterFogColorUpdateTime = l;
		}
	}

	public void applyFog(Camera camera, int i) {
		this.setFogBlack(false);
		GlStateManager.normal3f(0.0F, -1.0F, 0.0F);
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		FluidState fluidState = camera.getSubmergedFluidState();
		if (camera.getFocusedEntity() instanceof LivingEntity && ((LivingEntity)camera.getFocusedEntity()).hasStatusEffect(StatusEffects.BLINDNESS)) {
			float f = 5.0F;
			int j = ((LivingEntity)camera.getFocusedEntity()).getStatusEffect(StatusEffects.BLINDNESS).getDuration();
			if (j < 20) {
				f = MathHelper.lerp(1.0F - (float)j / 20.0F, 5.0F, this.gameRenderer.getViewDistance());
			}

			GlStateManager.fogMode(GlStateManager.FogMode.LINEAR);
			if (i == -1) {
				GlStateManager.fogStart(0.0F);
				GlStateManager.fogEnd(f * 0.8F);
			} else {
				GlStateManager.fogStart(f * 0.25F);
				GlStateManager.fogEnd(f);
			}

			GLX.setupNvFogDistance();
		} else if (fluidState.matches(FluidTags.WATER)) {
			GlStateManager.fogMode(GlStateManager.FogMode.EXP2);
			if (camera.getFocusedEntity() instanceof LivingEntity) {
				if (camera.getFocusedEntity() instanceof ClientPlayerEntity) {
					ClientPlayerEntity clientPlayerEntity = (ClientPlayerEntity)camera.getFocusedEntity();
					float g = 0.05F - clientPlayerEntity.method_3140() * clientPlayerEntity.method_3140() * 0.03F;
					Biome biome = clientPlayerEntity.world.getBiome(new BlockPos(clientPlayerEntity));
					if (biome == Biomes.SWAMP || biome == Biomes.SWAMP_HILLS) {
						g += 0.005F;
					}

					GlStateManager.fogDensity(g);
				} else {
					GlStateManager.fogDensity(0.05F);
				}
			} else {
				GlStateManager.fogDensity(0.1F);
			}
		} else if (fluidState.matches(FluidTags.LAVA)) {
			GlStateManager.fogMode(GlStateManager.FogMode.EXP);
			GlStateManager.fogDensity(2.0F);
		} else {
			float fx = this.gameRenderer.getViewDistance();
			GlStateManager.fogMode(GlStateManager.FogMode.LINEAR);
			if (i == -1) {
				GlStateManager.fogStart(0.0F);
				GlStateManager.fogEnd(fx);
			} else {
				GlStateManager.fogStart(fx * 0.75F);
				GlStateManager.fogEnd(fx);
			}

			GLX.setupNvFogDistance();
			if (this.client.world.dimension.isFogThick(MathHelper.floor(camera.getPos().x), MathHelper.floor(camera.getPos().z))
				|| this.client.inGameHud.getBossBarHud().shouldThickenFog()) {
				GlStateManager.fogStart(fx * 0.05F);
				GlStateManager.fogEnd(Math.min(fx, 192.0F) * 0.5F);
			}
		}

		GlStateManager.enableColorMaterial();
		GlStateManager.enableFog();
		GlStateManager.colorMaterial(1028, 4608);
	}

	public void setFogBlack(boolean fogBlack) {
		if (fogBlack) {
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
