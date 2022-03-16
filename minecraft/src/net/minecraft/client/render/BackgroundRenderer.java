package net.minecraft.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.tag.BiomeTags;
import net.minecraft.util.CubicSampler;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeAccess;

@Environment(EnvType.CLIENT)
public class BackgroundRenderer {
	private static final int field_32685 = 96;
	public static final float field_32684 = 5000.0F;
	private static float red;
	private static float green;
	private static float blue;
	private static int waterFogColor = -1;
	private static int nextWaterFogColor = -1;
	private static long lastWaterFogColorUpdateTime = -1L;

	public static void render(Camera camera, float tickDelta, ClientWorld world, int viewDistance, float skyDarkness) {
		CameraSubmersionType cameraSubmersionType = camera.getSubmersionType();
		Entity entity = camera.getFocusedEntity();
		if (cameraSubmersionType == CameraSubmersionType.WATER) {
			long l = Util.getMeasuringTimeMs();
			int i = world.getBiome(new BlockPos(camera.getPos())).value().getWaterFogColor();
			if (lastWaterFogColorUpdateTime < 0L) {
				waterFogColor = i;
				nextWaterFogColor = i;
				lastWaterFogColorUpdateTime = l;
			}

			int j = waterFogColor >> 16 & 0xFF;
			int k = waterFogColor >> 8 & 0xFF;
			int m = waterFogColor & 0xFF;
			int n = nextWaterFogColor >> 16 & 0xFF;
			int o = nextWaterFogColor >> 8 & 0xFF;
			int p = nextWaterFogColor & 0xFF;
			float f = MathHelper.clamp((float)(l - lastWaterFogColorUpdateTime) / 5000.0F, 0.0F, 1.0F);
			float g = MathHelper.lerp(f, (float)n, (float)j);
			float h = MathHelper.lerp(f, (float)o, (float)k);
			float q = MathHelper.lerp(f, (float)p, (float)m);
			red = g / 255.0F;
			green = h / 255.0F;
			blue = q / 255.0F;
			if (waterFogColor != i) {
				waterFogColor = i;
				nextWaterFogColor = MathHelper.floor(g) << 16 | MathHelper.floor(h) << 8 | MathHelper.floor(q);
				lastWaterFogColorUpdateTime = l;
			}
		} else if (cameraSubmersionType == CameraSubmersionType.LAVA) {
			red = 0.6F;
			green = 0.1F;
			blue = 0.0F;
			lastWaterFogColorUpdateTime = -1L;
		} else if (cameraSubmersionType == CameraSubmersionType.POWDER_SNOW) {
			red = 0.623F;
			green = 0.734F;
			blue = 0.785F;
			lastWaterFogColorUpdateTime = -1L;
			RenderSystem.clearColor(red, green, blue, 0.0F);
		} else {
			float r = 0.25F + 0.75F * (float)viewDistance / 32.0F;
			r = 1.0F - (float)Math.pow((double)r, 0.25);
			Vec3d vec3d = world.getSkyColor(camera.getPos(), tickDelta);
			float s = (float)vec3d.x;
			float t = (float)vec3d.y;
			float u = (float)vec3d.z;
			float v = MathHelper.clamp(MathHelper.cos(world.getSkyAngle(tickDelta) * (float) (Math.PI * 2)) * 2.0F + 0.5F, 0.0F, 1.0F);
			BiomeAccess biomeAccess = world.getBiomeAccess();
			Vec3d vec3d2 = camera.getPos().subtract(2.0, 2.0, 2.0).multiply(0.25);
			Vec3d vec3d3 = CubicSampler.sampleColor(
				vec3d2, (xx, y, z) -> world.getDimensionEffects().adjustFogColor(Vec3d.unpackRgb(biomeAccess.getBiomeForNoiseGen(xx, y, z).value().getFogColor()), v)
			);
			red = (float)vec3d3.getX();
			green = (float)vec3d3.getY();
			blue = (float)vec3d3.getZ();
			if (viewDistance >= 4) {
				float f = MathHelper.sin(world.getSkyAngleRadians(tickDelta)) > 0.0F ? -1.0F : 1.0F;
				Vec3f vec3f = new Vec3f(f, 0.0F, 0.0F);
				float h = camera.getHorizontalPlane().dot(vec3f);
				if (h < 0.0F) {
					h = 0.0F;
				}

				if (h > 0.0F) {
					float[] fs = world.getDimensionEffects().getFogColorOverride(world.getSkyAngle(tickDelta), tickDelta);
					if (fs != null) {
						h *= fs[3];
						red = red * (1.0F - h) + fs[0] * h;
						green = green * (1.0F - h) + fs[1] * h;
						blue = blue * (1.0F - h) + fs[2] * h;
					}
				}
			}

			red = red + (s - red) * r;
			green = green + (t - green) * r;
			blue = blue + (u - blue) * r;
			float fx = world.getRainGradient(tickDelta);
			if (fx > 0.0F) {
				float g = 1.0F - fx * 0.5F;
				float hx = 1.0F - fx * 0.4F;
				red *= g;
				green *= g;
				blue *= hx;
			}

			float g = world.getThunderGradient(tickDelta);
			if (g > 0.0F) {
				float hx = 1.0F - g * 0.5F;
				red *= hx;
				green *= hx;
				blue *= hx;
			}

			lastWaterFogColorUpdateTime = -1L;
		}

		float rx = ((float)camera.getPos().y - (float)world.getBottomY()) * world.getLevelProperties().getHorizonShadingRatio();
		if (camera.getFocusedEntity() instanceof LivingEntity && ((LivingEntity)camera.getFocusedEntity()).hasStatusEffect(StatusEffects.BLINDNESS)) {
			int w = ((LivingEntity)camera.getFocusedEntity()).getStatusEffect(StatusEffects.BLINDNESS).getDuration();
			if (w < 20) {
				rx = 1.0F - (float)w / 20.0F;
			} else {
				rx = 0.0F;
			}
		}

		if (rx < 1.0F && cameraSubmersionType != CameraSubmersionType.LAVA && cameraSubmersionType != CameraSubmersionType.POWDER_SNOW) {
			if (rx < 0.0F) {
				rx = 0.0F;
			}

			rx *= rx;
			red *= rx;
			green *= rx;
			blue *= rx;
		}

		if (skyDarkness > 0.0F) {
			red = red * (1.0F - skyDarkness) + red * 0.7F * skyDarkness;
			green = green * (1.0F - skyDarkness) + green * 0.6F * skyDarkness;
			blue = blue * (1.0F - skyDarkness) + blue * 0.6F * skyDarkness;
		}

		float x;
		if (cameraSubmersionType == CameraSubmersionType.WATER) {
			if (entity instanceof ClientPlayerEntity) {
				x = ((ClientPlayerEntity)entity).getUnderwaterVisibility();
			} else {
				x = 1.0F;
			}
		} else if (entity instanceof LivingEntity && ((LivingEntity)entity).hasStatusEffect(StatusEffects.NIGHT_VISION)) {
			x = GameRenderer.getNightVisionStrength((LivingEntity)entity, tickDelta);
		} else {
			x = 0.0F;
		}

		if (red != 0.0F && green != 0.0F && blue != 0.0F) {
			float sx = Math.min(1.0F / red, Math.min(1.0F / green, 1.0F / blue));
			red = red * (1.0F - x) + red * sx * x;
			green = green * (1.0F - x) + green * sx * x;
			blue = blue * (1.0F - x) + blue * sx * x;
		}

		RenderSystem.clearColor(red, green, blue, 0.0F);
	}

	public static void clearFog() {
		RenderSystem.setShaderFogStart(Float.MAX_VALUE);
	}

	public static void applyFog(Camera camera, BackgroundRenderer.FogType fogType, float viewDistance, boolean thickFog) {
		CameraSubmersionType cameraSubmersionType = camera.getSubmersionType();
		Entity entity = camera.getFocusedEntity();
		FogShape fogShape = FogShape.SPHERE;
		float f;
		float g;
		if (cameraSubmersionType == CameraSubmersionType.LAVA) {
			if (entity.isSpectator()) {
				f = -8.0F;
				g = viewDistance * 0.5F;
			} else if (entity instanceof LivingEntity && ((LivingEntity)entity).hasStatusEffect(StatusEffects.FIRE_RESISTANCE)) {
				f = 0.0F;
				g = 3.0F;
			} else {
				f = 0.25F;
				g = 1.0F;
			}
		} else if (cameraSubmersionType == CameraSubmersionType.POWDER_SNOW) {
			if (entity.isSpectator()) {
				f = -8.0F;
				g = viewDistance * 0.5F;
			} else {
				f = 0.0F;
				g = 2.0F;
			}
		} else if (entity instanceof LivingEntity && ((LivingEntity)entity).hasStatusEffect(StatusEffects.BLINDNESS)) {
			int i = ((LivingEntity)entity).getStatusEffect(StatusEffects.BLINDNESS).getDuration();
			float h = MathHelper.lerp(Math.min(1.0F, (float)i / 20.0F), viewDistance, 5.0F);
			if (fogType == BackgroundRenderer.FogType.FOG_SKY) {
				f = 0.0F;
				g = h * 0.8F;
			} else {
				f = cameraSubmersionType == CameraSubmersionType.WATER ? -4.0F : h * 0.25F;
				g = h;
			}
		} else if (cameraSubmersionType == CameraSubmersionType.WATER) {
			f = -8.0F;
			g = 96.0F;
			if (entity instanceof ClientPlayerEntity clientPlayerEntity) {
				g *= Math.max(0.25F, clientPlayerEntity.getUnderwaterVisibility());
				RegistryEntry<Biome> registryEntry = clientPlayerEntity.world.getBiome(clientPlayerEntity.getBlockPos());
				if (registryEntry.isIn(BiomeTags.HAS_CLOSER_WATER_FOG)) {
					g *= 0.85F;
				}
			}

			if (g > viewDistance) {
				g = viewDistance;
				fogShape = FogShape.CYLINDER;
			}
		} else if (thickFog) {
			f = viewDistance * 0.05F;
			g = Math.min(viewDistance, 192.0F) * 0.5F;
		} else if (fogType == BackgroundRenderer.FogType.FOG_SKY) {
			f = 0.0F;
			g = viewDistance;
			fogShape = FogShape.CYLINDER;
		} else {
			float j = MathHelper.clamp(viewDistance / 10.0F, 4.0F, 64.0F);
			f = viewDistance - j;
			g = viewDistance;
			fogShape = FogShape.CYLINDER;
		}

		RenderSystem.setShaderFogStart(f);
		RenderSystem.setShaderFogEnd(g);
		RenderSystem.setShaderFogShape(fogShape);
	}

	public static void setFogBlack() {
		RenderSystem.setShaderFogColor(red, green, blue);
	}

	@Environment(EnvType.CLIENT)
	public static enum FogType {
		FOG_SKY,
		FOG_TERRAIN;
	}
}
