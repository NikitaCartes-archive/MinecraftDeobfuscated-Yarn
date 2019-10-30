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
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;

@Environment(EnvType.CLIENT)
public class BackgroundRenderer {
	private static final FloatBuffer blackColorBuffer = Util.create(
		GlAllocationUtils.allocateFloatBuffer(16), floatBuffer -> floatBuffer.put(0.0F).put(0.0F).put(0.0F).put(1.0F).flip()
	);
	private static final FloatBuffer colorBuffer = GlAllocationUtils.allocateFloatBuffer(16);
	private float red;
	private float green;
	private float blue;
	private int waterFogColor = -1;
	private int nextWaterFogColor = -1;
	private long lastWaterFogColorUpdateTime = -1L;

	public void render(Camera camera, float tickDelta, World world, int viewDistance, float skyDarkness) {
		FluidState fluidState = camera.getSubmergedFluidState();
		float r;
		float s;
		float t;
		if (fluidState.matches(FluidTags.WATER)) {
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
			r = g / 255.0F;
			s = h / 255.0F;
			t = q / 255.0F;
			if (this.waterFogColor != i) {
				this.waterFogColor = i;
				this.nextWaterFogColor = MathHelper.floor(g) << 16 | MathHelper.floor(h) << 8 | MathHelper.floor(q);
				this.lastWaterFogColorUpdateTime = l;
			}
		} else if (fluidState.matches(FluidTags.LAVA)) {
			r = 0.6F;
			s = 0.1F;
			t = 0.0F;
			this.lastWaterFogColorUpdateTime = -1L;
		} else {
			float u = 0.25F + 0.75F * (float)viewDistance / 32.0F;
			u = 1.0F - (float)Math.pow((double)u, 0.25);
			Vec3d vec3d = world.getSkyColor(camera.getBlockPos(), tickDelta);
			float v = (float)vec3d.x;
			float w = (float)vec3d.y;
			float x = (float)vec3d.z;
			Vec3d vec3d2 = world.getFogColor(tickDelta);
			r = (float)vec3d2.x;
			s = (float)vec3d2.y;
			t = (float)vec3d2.z;
			if (viewDistance >= 4) {
				double d = MathHelper.sin(world.getSkyAngleRadians(tickDelta)) > 0.0F ? -1.0 : 1.0;
				Vec3d vec3d3 = new Vec3d(d, 0.0, 0.0);
				float f = (float)camera.getHorizontalPlane().dotProduct(vec3d3);
				if (f < 0.0F) {
					f = 0.0F;
				}

				if (f > 0.0F) {
					float[] fs = world.dimension.getBackgroundColor(world.getSkyAngle(tickDelta), tickDelta);
					if (fs != null) {
						f *= fs[3];
						r = r * (1.0F - f) + fs[0] * f;
						s = s * (1.0F - f) + fs[1] * f;
						t = t * (1.0F - f) + fs[2] * f;
					}
				}
			}

			r += (v - r) * u;
			s += (w - s) * u;
			t += (x - t) * u;
			float y = world.getRainGradient(tickDelta);
			if (y > 0.0F) {
				float z = 1.0F - y * 0.5F;
				float aa = 1.0F - y * 0.4F;
				r *= z;
				s *= z;
				t *= aa;
			}

			float z = world.getThunderGradient(tickDelta);
			if (z > 0.0F) {
				float aa = 1.0F - z * 0.5F;
				r *= aa;
				s *= aa;
				t *= aa;
			}

			this.lastWaterFogColorUpdateTime = -1L;
		}

		double e = camera.getPos().y * world.dimension.getHorizonShadingRatio();
		if (camera.getFocusedEntity() instanceof LivingEntity && ((LivingEntity)camera.getFocusedEntity()).hasStatusEffect(StatusEffects.BLINDNESS)) {
			int ix = ((LivingEntity)camera.getFocusedEntity()).getStatusEffect(StatusEffects.BLINDNESS).getDuration();
			if (ix < 20) {
				e *= (double)(1.0F - (float)ix / 20.0F);
			} else {
				e = 0.0;
			}
		}

		if (e < 1.0) {
			if (e < 0.0) {
				e = 0.0;
			}

			e *= e;
			r = (float)((double)r * e);
			s = (float)((double)s * e);
			t = (float)((double)t * e);
		}

		if (skyDarkness > 0.0F) {
			r = r * (1.0F - skyDarkness) + r * 0.7F * skyDarkness;
			s = s * (1.0F - skyDarkness) + s * 0.6F * skyDarkness;
			t = t * (1.0F - skyDarkness) + t * 0.6F * skyDarkness;
		}

		if (fluidState.matches(FluidTags.WATER)) {
			float vx = 0.0F;
			if (camera.getFocusedEntity() instanceof ClientPlayerEntity) {
				ClientPlayerEntity clientPlayerEntity = (ClientPlayerEntity)camera.getFocusedEntity();
				vx = clientPlayerEntity.method_3140();
			}

			float wx = Math.min(1.0F / r, Math.min(1.0F / s, 1.0F / t));
			r = r * (1.0F - vx) + r * wx * vx;
			s = s * (1.0F - vx) + s * wx * vx;
			t = t * (1.0F - vx) + t * wx * vx;
		} else if (camera.getFocusedEntity() instanceof LivingEntity && ((LivingEntity)camera.getFocusedEntity()).hasStatusEffect(StatusEffects.NIGHT_VISION)) {
			float vx = GameRenderer.getNightVisionStrength((LivingEntity)camera.getFocusedEntity(), tickDelta);
			float wx = Math.min(1.0F / r, Math.min(1.0F / s, 1.0F / t));
			r = r * (1.0F - vx) + r * wx * vx;
			s = s * (1.0F - vx) + s * wx * vx;
			t = t * (1.0F - vx) + t * wx * vx;
		}

		RenderSystem.clearColor(r, s, t, 0.0F);
		if (this.red != r || this.green != s || this.blue != t) {
			colorBuffer.clear();
			colorBuffer.put(r).put(s).put(t).put(1.0F);
			colorBuffer.flip();
			this.red = r;
			this.green = s;
			this.blue = t;
		}
	}

	public static void applyFog(Camera camera, BackgroundRenderer.FogType fogType, float viewDistance, boolean thickFog) {
		setFogBlack(false);
		RenderSystem.normal3f(0.0F, -1.0F, 0.0F);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		FluidState fluidState = camera.getSubmergedFluidState();
		if (camera.getFocusedEntity() instanceof LivingEntity && ((LivingEntity)camera.getFocusedEntity()).hasStatusEffect(StatusEffects.BLINDNESS)) {
			float f = 5.0F;
			int i = ((LivingEntity)camera.getFocusedEntity()).getStatusEffect(StatusEffects.BLINDNESS).getDuration();
			if (i < 20) {
				f = MathHelper.lerp(1.0F - (float)i / 20.0F, 5.0F, viewDistance);
			}

			RenderSystem.fogMode(GlStateManager.FogMode.LINEAR);
			if (fogType == BackgroundRenderer.FogType.FOG_SKY) {
				RenderSystem.fogStart(0.0F);
				RenderSystem.fogEnd(f * 0.8F);
			} else {
				RenderSystem.fogStart(f * 0.25F);
				RenderSystem.fogEnd(f);
			}

			RenderSystem.setupNvFogDistance();
		} else if (fluidState.matches(FluidTags.WATER)) {
			RenderSystem.fogMode(GlStateManager.FogMode.EXP2);
			if (camera.getFocusedEntity() instanceof LivingEntity) {
				if (camera.getFocusedEntity() instanceof ClientPlayerEntity) {
					ClientPlayerEntity clientPlayerEntity = (ClientPlayerEntity)camera.getFocusedEntity();
					float g = 0.05F - clientPlayerEntity.method_3140() * clientPlayerEntity.method_3140() * 0.03F;
					Biome biome = clientPlayerEntity.world.getBiome(new BlockPos(clientPlayerEntity));
					if (biome == Biomes.SWAMP || biome == Biomes.SWAMP_HILLS) {
						g += 0.005F;
					}

					RenderSystem.fogDensity(g);
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
				RenderSystem.fogEnd(viewDistance);
			} else {
				RenderSystem.fogStart(viewDistance * 0.75F);
				RenderSystem.fogEnd(viewDistance);
			}

			RenderSystem.setupNvFogDistance();
			if (thickFog) {
				RenderSystem.fogStart(viewDistance * 0.05F);
				RenderSystem.fogEnd(Math.min(viewDistance, 192.0F) * 0.5F);
			}
		}

		RenderSystem.enableFog();
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
