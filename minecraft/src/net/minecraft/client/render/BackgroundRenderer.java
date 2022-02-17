package net.minecraft.client.render;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.LinkedList;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.CubicSampler;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeAccess;

@Environment(EnvType.CLIENT)
public class BackgroundRenderer {
	private static final int field_32685 = 192;
	private static final LinkedList<BackgroundRenderer.StatusEffectFogModifier> FOG_MODIFIERS = Util.make(Lists.newLinkedList(), linkedList -> {
		linkedList.add(new BackgroundRenderer.BlindnessFogModifier());
		linkedList.add(new BackgroundRenderer.DarknessFogModifier());
	});
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
			int i = world.getBiome(new BlockPos(camera.getPos())).getWaterFogColor();
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
				vec3d2, (x, y, z) -> world.getDimensionEffects().adjustFogColor(Vec3d.unpackRgb(biomeAccess.getBiomeForNoiseGen(x, y, z).getFogColor()), v)
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

		double d = (camera.getPos().y - (double)world.getBottomY()) * world.getLevelProperties().getHorizonShadingRatio();
		BackgroundRenderer.StatusEffectFogModifier statusEffectFogModifier = getFogModifier(entity, tickDelta);
		if (statusEffectFogModifier != null) {
			LivingEntity livingEntity = (LivingEntity)entity;
			d = statusEffectFogModifier.applyColorModifier(livingEntity, livingEntity.getStatusEffect(statusEffectFogModifier.getStatusEffect()), d, tickDelta);
		}

		if (d < 1.0 && cameraSubmersionType != CameraSubmersionType.LAVA) {
			if (d < 0.0) {
				d = 0.0;
			}

			d *= d;
			red = (float)((double)red * d);
			green = (float)((double)green * d);
			blue = (float)((double)blue * d);
		}

		if (skyDarkness > 0.0F) {
			red = red * (1.0F - skyDarkness) + red * 0.7F * skyDarkness;
			green = green * (1.0F - skyDarkness) + green * 0.6F * skyDarkness;
			blue = blue * (1.0F - skyDarkness) + blue * 0.6F * skyDarkness;
		}

		float tx;
		if (cameraSubmersionType == CameraSubmersionType.WATER) {
			if (entity instanceof ClientPlayerEntity) {
				tx = ((ClientPlayerEntity)entity).getUnderwaterVisibility();
			} else {
				tx = 1.0F;
			}
		} else if (entity instanceof LivingEntity && ((LivingEntity)entity).hasStatusEffect(StatusEffects.NIGHT_VISION)) {
			tx = GameRenderer.getNightVisionStrength((LivingEntity)entity, tickDelta);
		} else {
			tx = 0.0F;
		}

		if (red != 0.0F && green != 0.0F && blue != 0.0F) {
			float ux = Math.min(1.0F / red, Math.min(1.0F / green, 1.0F / blue));
			red = red * (1.0F - tx) + red * ux * tx;
			green = green * (1.0F - tx) + green * ux * tx;
			blue = blue * (1.0F - tx) + blue * ux * tx;
		}

		RenderSystem.clearColor(red, green, blue, 0.0F);
	}

	public static void clearFog() {
		RenderSystem.setShaderFogStart(Float.MAX_VALUE);
	}

	@Nullable
	private static BackgroundRenderer.StatusEffectFogModifier getFogModifier(Entity entity, float tickDelta) {
		return entity instanceof LivingEntity livingEntity
			? (BackgroundRenderer.StatusEffectFogModifier)FOG_MODIFIERS.stream()
				.filter(fogModifier -> fogModifier.shouldApply(livingEntity, tickDelta))
				.findFirst()
				.orElse(null)
			: null;
	}

	public static void applyFog(Camera camera, BackgroundRenderer.FogType fogType, float viewDistance, boolean thickFog, float tickDelta) {
		CameraSubmersionType cameraSubmersionType = camera.getSubmersionType();
		Entity entity = camera.getFocusedEntity();
		if (cameraSubmersionType == CameraSubmersionType.WATER) {
			float f = 192.0F;
			if (entity instanceof ClientPlayerEntity clientPlayerEntity) {
				f *= Math.max(0.25F, clientPlayerEntity.getUnderwaterVisibility());
				Biome biome = clientPlayerEntity.world.getBiome(clientPlayerEntity.getBlockPos());
				if (biome.getCategory() == Biome.Category.SWAMP) {
					f *= 0.85F;
				}
			}

			RenderSystem.setShaderFogStart(-8.0F);
			RenderSystem.setShaderFogEnd(f * 0.5F);
		} else {
			BackgroundRenderer.FogData fogData = new BackgroundRenderer.FogData(fogType);
			BackgroundRenderer.StatusEffectFogModifier statusEffectFogModifier = getFogModifier(entity, tickDelta);
			if (cameraSubmersionType == CameraSubmersionType.LAVA) {
				if (entity.isSpectator()) {
					fogData.fogStart = -8.0F;
					fogData.fogEnd = viewDistance * 0.5F;
				} else if (entity instanceof LivingEntity && ((LivingEntity)entity).hasStatusEffect(StatusEffects.FIRE_RESISTANCE)) {
					fogData.fogStart = 0.0F;
					fogData.fogEnd = 3.0F;
				} else {
					fogData.fogStart = 0.25F;
					fogData.fogEnd = 1.0F;
				}
			} else if (statusEffectFogModifier != null) {
				LivingEntity livingEntity = (LivingEntity)entity;
				StatusEffectInstance statusEffectInstance = livingEntity.getStatusEffect(statusEffectFogModifier.getStatusEffect());
				if (statusEffectInstance != null) {
					statusEffectFogModifier.applyStartEndModifier(fogData, livingEntity, statusEffectInstance, viewDistance, tickDelta);
				}
			} else if (cameraSubmersionType == CameraSubmersionType.POWDER_SNOW) {
				if (entity.isSpectator()) {
					fogData.fogStart = -8.0F;
					fogData.fogEnd = viewDistance * 0.5F;
				} else {
					fogData.fogStart = 0.0F;
					fogData.fogEnd = 2.0F;
				}
			} else if (thickFog) {
				fogData.fogStart = viewDistance * 0.05F;
				fogData.fogEnd = Math.min(viewDistance, 192.0F) * 0.5F;
			} else if (fogType == BackgroundRenderer.FogType.FOG_SKY) {
				fogData.fogStart = 0.0F;
				fogData.fogEnd = viewDistance;
			} else {
				float g = MathHelper.clamp(viewDistance / 10.0F, 4.0F, 64.0F);
				fogData.fogStart = viewDistance - g;
				fogData.fogEnd = viewDistance;
			}

			RenderSystem.setShaderFogStart(fogData.fogStart);
			RenderSystem.setShaderFogEnd(fogData.fogEnd);
		}
	}

	public static void setFogBlack() {
		RenderSystem.setShaderFogColor(red, green, blue);
	}

	@Environment(EnvType.CLIENT)
	static class BlindnessFogModifier implements BackgroundRenderer.StatusEffectFogModifier {
		@Override
		public StatusEffect getStatusEffect() {
			return StatusEffects.BLINDNESS;
		}

		@Override
		public void applyStartEndModifier(
			BackgroundRenderer.FogData fogData, LivingEntity entity, StatusEffectInstance statusEffectInstance, float viewDistance, float tickDelta
		) {
			float f = MathHelper.lerp(Math.min(1.0F, (float)statusEffectInstance.getDuration() / 20.0F), viewDistance, 5.0F);
			if (fogData.fogType == BackgroundRenderer.FogType.FOG_SKY) {
				fogData.fogStart = 0.0F;
				fogData.fogEnd = f * 0.8F;
			} else {
				fogData.fogStart = f * 0.25F;
				fogData.fogEnd = f;
			}
		}
	}

	@Environment(EnvType.CLIENT)
	static class DarknessFogModifier implements BackgroundRenderer.StatusEffectFogModifier {
		@Override
		public StatusEffect getStatusEffect() {
			return StatusEffects.DARKNESS;
		}

		@Override
		public void applyStartEndModifier(
			BackgroundRenderer.FogData fogData, LivingEntity entity, StatusEffectInstance statusEffectInstance, float viewDistance, float tickDelta
		) {
			if (statusEffectInstance.getFactorCalculationData().isPresent()) {
				float f = MathHelper.lerp(
					((StatusEffectInstance.FactorCalculationData)statusEffectInstance.getFactorCalculationData().get()).lerp(tickDelta), viewDistance, 15.0F
				);
				fogData.fogStart = fogData.fogType == BackgroundRenderer.FogType.FOG_SKY ? 0.0F : f * 0.75F;
				fogData.fogEnd = f;
			}
		}

		@Override
		public double applyColorModifier(LivingEntity entity, StatusEffectInstance statusEffectInstance, double colorModifier, float tickDelta) {
			return !statusEffectInstance.getFactorCalculationData().isPresent()
				? 0.0
				: (double)(1.0F - ((StatusEffectInstance.FactorCalculationData)statusEffectInstance.getFactorCalculationData().get()).lerp(tickDelta));
		}
	}

	@Environment(EnvType.CLIENT)
	static class FogData {
		public final BackgroundRenderer.FogType fogType;
		public float fogStart;
		public float fogEnd;

		public FogData(BackgroundRenderer.FogType fogType) {
			this.fogType = fogType;
		}
	}

	@Environment(EnvType.CLIENT)
	public static enum FogType {
		FOG_SKY,
		FOG_TERRAIN;
	}

	@Environment(EnvType.CLIENT)
	interface StatusEffectFogModifier {
		StatusEffect getStatusEffect();

		void applyStartEndModifier(
			BackgroundRenderer.FogData fogData, LivingEntity entity, StatusEffectInstance statusEffectInstance, float viewDistance, float tickDelta
		);

		default boolean shouldApply(LivingEntity entity, float tickDelta) {
			return entity.hasStatusEffect(this.getStatusEffect());
		}

		default double applyColorModifier(LivingEntity entity, StatusEffectInstance statusEffectInstance, double colorModifier, float tickDelta) {
			StatusEffectInstance statusEffectInstance2 = entity.getStatusEffect(this.getStatusEffect());
			if (statusEffectInstance2 != null) {
				if (statusEffectInstance2.getDuration() < 20) {
					colorModifier *= (double)(1.0F - (float)statusEffectInstance2.getDuration() / 20.0F);
				} else {
					colorModifier = 0.0;
				}
			}

			return colorModifier;
		}
	}
}
