package net.minecraft.client.render;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
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
	private static final List<BackgroundRenderer.StatusEffectFogModifier> field_38338 = Lists.<BackgroundRenderer.StatusEffectFogModifier>newArrayList(
		new BackgroundRenderer.BlindnessFogModifier(), new BackgroundRenderer.DarknessFogModifier()
	);
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
				vec3d2, (x, y, z) -> world.getDimensionEffects().adjustFogColor(Vec3d.unpackRgb(biomeAccess.getBiomeForNoiseGen(x, y, z).value().getFogColor()), v)
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
		BackgroundRenderer.StatusEffectFogModifier statusEffectFogModifier = getFogModifier(entity, tickDelta);
		if (statusEffectFogModifier != null) {
			LivingEntity livingEntity = (LivingEntity)entity;
			rx = statusEffectFogModifier.applyColorModifier(livingEntity, livingEntity.getStatusEffect(statusEffectFogModifier.getStatusEffect()), rx, tickDelta);
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

		float sx;
		if (cameraSubmersionType == CameraSubmersionType.WATER) {
			if (entity instanceof ClientPlayerEntity) {
				sx = ((ClientPlayerEntity)entity).getUnderwaterVisibility();
			} else {
				sx = 1.0F;
			}
		} else if (entity instanceof LivingEntity && ((LivingEntity)entity).hasStatusEffect(StatusEffects.NIGHT_VISION)) {
			sx = GameRenderer.getNightVisionStrength((LivingEntity)entity, tickDelta);
		} else {
			sx = 0.0F;
		}

		if (red != 0.0F && green != 0.0F && blue != 0.0F) {
			float tx = Math.min(1.0F / red, Math.min(1.0F / green, 1.0F / blue));
			red = red * (1.0F - sx) + red * tx * sx;
			green = green * (1.0F - sx) + green * tx * sx;
			blue = blue * (1.0F - sx) + blue * tx * sx;
		}

		RenderSystem.clearColor(red, green, blue, 0.0F);
	}

	public static void clearFog() {
		RenderSystem.setShaderFogStart(Float.MAX_VALUE);
	}

	@Nullable
	private static BackgroundRenderer.StatusEffectFogModifier getFogModifier(Entity entity, float f) {
		return entity instanceof LivingEntity livingEntity
			? (BackgroundRenderer.StatusEffectFogModifier)field_38338.stream()
				.filter(statusEffectFogModifier -> statusEffectFogModifier.shouldApply(livingEntity, f))
				.findFirst()
				.orElse(null)
			: null;
	}

	public static void applyFog(Camera camera, BackgroundRenderer.FogType fogType, float viewDistance, boolean thickFog, float f) {
		CameraSubmersionType cameraSubmersionType = camera.getSubmersionType();
		Entity entity = camera.getFocusedEntity();
		BackgroundRenderer.FogData fogData = new BackgroundRenderer.FogData(fogType);
		BackgroundRenderer.StatusEffectFogModifier statusEffectFogModifier = getFogModifier(entity, f);
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
		} else if (cameraSubmersionType == CameraSubmersionType.POWDER_SNOW) {
			if (entity.isSpectator()) {
				fogData.fogStart = -8.0F;
				fogData.fogEnd = viewDistance * 0.5F;
			} else {
				fogData.fogStart = 0.0F;
				fogData.fogEnd = 2.0F;
			}
		} else if (statusEffectFogModifier != null) {
			LivingEntity livingEntity = (LivingEntity)entity;
			StatusEffectInstance statusEffectInstance = livingEntity.getStatusEffect(statusEffectFogModifier.getStatusEffect());
			if (statusEffectInstance != null) {
				statusEffectFogModifier.applyStartEndModifier(fogData, livingEntity, statusEffectInstance, viewDistance, f);
			}
		} else if (cameraSubmersionType == CameraSubmersionType.WATER) {
			fogData.fogStart = -8.0F;
			fogData.fogEnd = 96.0F;
			if (entity instanceof ClientPlayerEntity clientPlayerEntity) {
				fogData.fogEnd = fogData.fogEnd * Math.max(0.25F, clientPlayerEntity.getUnderwaterVisibility());
				RegistryEntry<Biome> registryEntry = clientPlayerEntity.world.getBiome(clientPlayerEntity.getBlockPos());
				if (registryEntry.isIn(BiomeTags.HAS_CLOSER_WATER_FOG)) {
					fogData.fogEnd *= 0.85F;
				}
			}

			if (fogData.fogEnd > viewDistance) {
				fogData.fogEnd = viewDistance;
				fogData.field_38342 = FogShape.CYLINDER;
			}
		} else if (thickFog) {
			fogData.fogStart = viewDistance * 0.05F;
			fogData.fogEnd = Math.min(viewDistance, 192.0F) * 0.5F;
		} else if (fogType == BackgroundRenderer.FogType.FOG_SKY) {
			fogData.fogStart = 0.0F;
			fogData.fogEnd = viewDistance;
			fogData.field_38342 = FogShape.CYLINDER;
		} else {
			float g = MathHelper.clamp(viewDistance / 10.0F, 4.0F, 64.0F);
			fogData.fogStart = viewDistance - g;
			fogData.fogEnd = viewDistance;
			fogData.field_38342 = FogShape.CYLINDER;
		}

		RenderSystem.setShaderFogStart(fogData.fogStart);
		RenderSystem.setShaderFogEnd(fogData.fogEnd);
		RenderSystem.setShaderFogShape(fogData.field_38342);
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
		public void applyStartEndModifier(BackgroundRenderer.FogData fogData, LivingEntity livingEntity, StatusEffectInstance statusEffectInstance, float f, float g) {
			float h = MathHelper.lerp(Math.min(1.0F, (float)statusEffectInstance.getDuration() / 20.0F), f, 5.0F);
			if (fogData.fogType == BackgroundRenderer.FogType.FOG_SKY) {
				fogData.fogStart = 0.0F;
				fogData.fogEnd = h * 0.8F;
			} else {
				fogData.fogStart = h * 0.25F;
				fogData.fogEnd = h;
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
		public void applyStartEndModifier(BackgroundRenderer.FogData fogData, LivingEntity livingEntity, StatusEffectInstance statusEffectInstance, float f, float g) {
			if (!statusEffectInstance.getFactorCalculationData().isEmpty()) {
				float h = MathHelper.lerp(((StatusEffectInstance.FactorCalculationData)statusEffectInstance.getFactorCalculationData().get()).lerp(g), f, 15.0F);
				fogData.fogStart = fogData.fogType == BackgroundRenderer.FogType.FOG_SKY ? 0.0F : h * 0.75F;
				fogData.fogEnd = h;
			}
		}

		@Override
		public float applyColorModifier(LivingEntity livingEntity, StatusEffectInstance statusEffectInstance, float f, float g) {
			return statusEffectInstance.getFactorCalculationData().isEmpty()
				? 0.0F
				: 1.0F - ((StatusEffectInstance.FactorCalculationData)statusEffectInstance.getFactorCalculationData().get()).lerp(g);
		}
	}

	@Environment(EnvType.CLIENT)
	static class FogData {
		public final BackgroundRenderer.FogType fogType;
		public float fogStart;
		public float fogEnd;
		public FogShape field_38342 = FogShape.SPHERE;

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

		void applyStartEndModifier(BackgroundRenderer.FogData fogData, LivingEntity livingEntity, StatusEffectInstance statusEffectInstance, float f, float g);

		default boolean shouldApply(LivingEntity livingEntity, float f) {
			return livingEntity.hasStatusEffect(this.getStatusEffect());
		}

		default float applyColorModifier(LivingEntity livingEntity, StatusEffectInstance statusEffectInstance, float f, float g) {
			StatusEffectInstance statusEffectInstance2 = livingEntity.getStatusEffect(this.getStatusEffect());
			if (statusEffectInstance2 != null) {
				if (statusEffectInstance2.getDuration() < 20) {
					f = 1.0F - (float)statusEffectInstance2.getDuration() / 20.0F;
				} else {
					f = 0.0F;
				}
			}

			return f;
		}
	}
}
