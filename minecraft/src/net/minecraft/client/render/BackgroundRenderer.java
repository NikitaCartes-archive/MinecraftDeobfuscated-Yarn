package net.minecraft.client.render;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.nio.FloatBuffer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.GlAllocationUtils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.fluid.FluidState;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;

@Environment(EnvType.CLIENT)
public class BackgroundRenderer {
	private static final FloatBuffer blackColorBuffer = SystemUtil.consume(
		GlAllocationUtils.allocateFloatBuffer(16), floatBuffer -> floatBuffer.put(0.0F).put(0.0F).put(0.0F).put(1.0F).flip()
	);
	private static final FloatBuffer colorBuffer = GlAllocationUtils.allocateFloatBuffer(16);
	private float red;
	private float green;
	private float blue;
	private int waterFogColor = -1;
	private int nextWaterFogColor = -1;
	private long lastWaterFogColorUpdateTime = -1L;

	public void renderBackground(Camera camera, float f, World world, int i, float g) {
		FluidState fluidState = camera.getSubmergedFluidState();
		float u;
		float v;
		float w;
		if (fluidState.matches(FluidTags.WATER)) {
			long l = SystemUtil.getMeasuringTimeMs();
			int j = world.getBiome(new BlockPos(camera.getPos())).getWaterFogColor();
			if (this.lastWaterFogColorUpdateTime < 0L) {
				this.waterFogColor = j;
				this.nextWaterFogColor = j;
				this.lastWaterFogColorUpdateTime = l;
			}

			int k = this.waterFogColor >> 16 & 0xFF;
			int m = this.waterFogColor >> 8 & 0xFF;
			int n = this.waterFogColor & 0xFF;
			int o = this.nextWaterFogColor >> 16 & 0xFF;
			int p = this.nextWaterFogColor >> 8 & 0xFF;
			int q = this.nextWaterFogColor & 0xFF;
			float h = MathHelper.clamp((float)(l - this.lastWaterFogColorUpdateTime) / 5000.0F, 0.0F, 1.0F);
			float r = MathHelper.lerp(h, (float)o, (float)k);
			float s = MathHelper.lerp(h, (float)p, (float)m);
			float t = MathHelper.lerp(h, (float)q, (float)n);
			u = r / 255.0F;
			v = s / 255.0F;
			w = t / 255.0F;
			if (this.waterFogColor != j) {
				this.waterFogColor = j;
				this.nextWaterFogColor = MathHelper.floor(r) << 16 | MathHelper.floor(s) << 8 | MathHelper.floor(t);
				this.lastWaterFogColorUpdateTime = l;
			}
		} else if (fluidState.matches(FluidTags.LAVA)) {
			u = 0.6F;
			v = 0.1F;
			w = 0.0F;
			this.lastWaterFogColorUpdateTime = -1L;
		} else {
			float x = 0.25F + 0.75F * (float)i / 32.0F;
			x = 1.0F - (float)Math.pow((double)x, 0.25);
			net.minecraft.util.math.Vec3d vec3d = world.getSkyColor(camera.getBlockPos(), f);
			float y = (float)vec3d.x;
			float z = (float)vec3d.y;
			float aa = (float)vec3d.z;
			net.minecraft.util.math.Vec3d vec3d2 = world.getFogColor(f);
			u = (float)vec3d2.x;
			v = (float)vec3d2.y;
			w = (float)vec3d2.z;
			if (i >= 4) {
				double d = MathHelper.sin(world.getSkyAngleRadians(f)) > 0.0F ? -1.0 : 1.0;
				net.minecraft.util.math.Vec3d vec3d3 = new net.minecraft.util.math.Vec3d(d, 0.0, 0.0);
				float h = (float)camera.getHorizontalPlane().dotProduct(vec3d3);
				if (h < 0.0F) {
					h = 0.0F;
				}

				if (h > 0.0F) {
					float[] fs = world.dimension.getBackgroundColor(world.getSkyAngle(f), f);
					if (fs != null) {
						h *= fs[3];
						u = u * (1.0F - h) + fs[0] * h;
						v = v * (1.0F - h) + fs[1] * h;
						w = w * (1.0F - h) + fs[2] * h;
					}
				}
			}

			u += (y - u) * x;
			v += (z - v) * x;
			w += (aa - w) * x;
			float ab = world.getRainGradient(f);
			if (ab > 0.0F) {
				float ac = 1.0F - ab * 0.5F;
				float ad = 1.0F - ab * 0.4F;
				u *= ac;
				v *= ac;
				w *= ad;
			}

			float ac = world.getThunderGradient(f);
			if (ac > 0.0F) {
				float ad = 1.0F - ac * 0.5F;
				u *= ad;
				v *= ad;
				w *= ad;
			}

			this.lastWaterFogColorUpdateTime = -1L;
		}

		double e = camera.getPos().y * world.dimension.getHorizonShadingRatio();
		if (camera.getFocusedEntity() instanceof LivingEntity && ((LivingEntity)camera.getFocusedEntity()).hasStatusEffect(StatusEffects.BLINDNESS)) {
			int jx = ((LivingEntity)camera.getFocusedEntity()).getStatusEffect(StatusEffects.BLINDNESS).getDuration();
			if (jx < 20) {
				e *= (double)(1.0F - (float)jx / 20.0F);
			} else {
				e = 0.0;
			}
		}

		if (e < 1.0) {
			if (e < 0.0) {
				e = 0.0;
			}

			e *= e;
			u = (float)((double)u * e);
			v = (float)((double)v * e);
			w = (float)((double)w * e);
		}

		if (g > 0.0F) {
			u = u * (1.0F - g) + u * 0.7F * g;
			v = v * (1.0F - g) + v * 0.6F * g;
			w = w * (1.0F - g) + w * 0.6F * g;
		}

		if (fluidState.matches(FluidTags.WATER)) {
			float yx = 0.0F;
			if (camera.getFocusedEntity() instanceof ClientPlayerEntity) {
				ClientPlayerEntity clientPlayerEntity = (ClientPlayerEntity)camera.getFocusedEntity();
				yx = clientPlayerEntity.method_3140();
			}

			float zx = Math.min(1.0F / u, Math.min(1.0F / v, 1.0F / w));
			u = u * (1.0F - yx) + u * zx * yx;
			v = v * (1.0F - yx) + v * zx * yx;
			w = w * (1.0F - yx) + w * zx * yx;
		} else if (camera.getFocusedEntity() instanceof LivingEntity && ((LivingEntity)camera.getFocusedEntity()).hasStatusEffect(StatusEffects.NIGHT_VISION)) {
			float yx = GameRenderer.getNightVisionStrength((LivingEntity)camera.getFocusedEntity(), f);
			float zx = Math.min(1.0F / u, Math.min(1.0F / v, 1.0F / w));
			u = u * (1.0F - yx) + u * zx * yx;
			v = v * (1.0F - yx) + v * zx * yx;
			w = w * (1.0F - yx) + w * zx * yx;
		}

		RenderSystem.clearColor(u, v, w, 0.0F);
		if (this.red != u || this.green != v || this.blue != w) {
			colorBuffer.clear();
			colorBuffer.put(u).put(v).put(w).put(1.0F);
			colorBuffer.flip();
			this.red = u;
			this.green = v;
			this.blue = w;
		}
	}

	public static void applyFog(Camera camera, BackgroundRenderer.FogType fogType, float f, boolean bl) {
		setFogBlack(false);
		RenderSystem.normal3f(0.0F, -1.0F, 0.0F);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		FluidState fluidState = camera.getSubmergedFluidState();
		if (camera.getFocusedEntity() instanceof LivingEntity && ((LivingEntity)camera.getFocusedEntity()).hasStatusEffect(StatusEffects.BLINDNESS)) {
			float g = 5.0F;
			int i = ((LivingEntity)camera.getFocusedEntity()).getStatusEffect(StatusEffects.BLINDNESS).getDuration();
			if (i < 20) {
				g = MathHelper.lerp(1.0F - (float)i / 20.0F, 5.0F, f);
			}

			RenderSystem.fogMode(GlStateManager.FogMode.LINEAR);
			if (fogType == BackgroundRenderer.FogType.FOG_SKY) {
				RenderSystem.fogStart(0.0F);
				RenderSystem.fogEnd(g * 0.8F);
			} else {
				RenderSystem.fogStart(g * 0.25F);
				RenderSystem.fogEnd(g);
			}

			RenderSystem.setupNvFogDistance();
		} else if (fluidState.matches(FluidTags.WATER)) {
			RenderSystem.fogMode(GlStateManager.FogMode.EXP2);
			if (camera.getFocusedEntity() instanceof LivingEntity) {
				if (camera.getFocusedEntity() instanceof ClientPlayerEntity) {
					ClientPlayerEntity clientPlayerEntity = (ClientPlayerEntity)camera.getFocusedEntity();
					float h = 0.05F - clientPlayerEntity.method_3140() * clientPlayerEntity.method_3140() * 0.03F;
					Biome biome = clientPlayerEntity.world.getBiome(new BlockPos(clientPlayerEntity));
					if (biome == Biomes.SWAMP || biome == Biomes.SWAMP_HILLS) {
						h += 0.005F;
					}

					RenderSystem.fogDensity(h);
				} else {
					RenderSystem.fogDensity(0.05F);
				}
			} else {
				RenderSystem.fogDensity(0.1F);
			}
		} else if (fluidState.matches(FluidTags.LAVA)) {
			RenderSystem.fogMode(GlStateManager.FogMode.EXP);
			RenderSystem.fogDensity(2.0F);
		} else {
			RenderSystem.fogMode(GlStateManager.FogMode.LINEAR);
			if (fogType == BackgroundRenderer.FogType.FOG_SKY) {
				RenderSystem.fogStart(0.0F);
				RenderSystem.fogEnd(f);
			} else {
				RenderSystem.fogStart(f * 0.75F);
				RenderSystem.fogEnd(f);
			}

			RenderSystem.setupNvFogDistance();
			if (bl) {
				RenderSystem.fogStart(f * 0.05F);
				RenderSystem.fogEnd(Math.min(f, 192.0F) * 0.5F);
			}
		}

		RenderSystem.enableFog();
		RenderSystem.colorMaterial(1028, 4608);
	}

	public static void setFogBlack(boolean bl) {
		RenderSystem.fog(2918, bl ? blackColorBuffer : colorBuffer);
	}

	@Environment(EnvType.CLIENT)
	public static enum FogType {
		FOG_SKY,
		FOG_TERRAIN;
	}
}
