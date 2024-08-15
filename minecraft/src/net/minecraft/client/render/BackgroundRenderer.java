package net.minecraft.client.render;

import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.enums.CameraSubmersionType;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.BiomeTags;
import net.minecraft.util.CubicSampler;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeAccess;
import org.joml.Vector3f;
import org.joml.Vector4f;

@Environment(EnvType.CLIENT)
public class BackgroundRenderer {
	private static final int WATER_FOG_LENGTH = 96;
	private static final List<BackgroundRenderer.StatusEffectFogModifier> FOG_MODIFIERS = Lists.<BackgroundRenderer.StatusEffectFogModifier>newArrayList(
		new BackgroundRenderer.BlindnessFogModifier(), new BackgroundRenderer.DarknessFogModifier()
	);
	public static final float WATER_FOG_CHANGE_DURATION = 5000.0F;
	private static int waterFogColor = -1;
	private static int nextWaterFogColor = -1;
	private static long lastWaterFogColorUpdateTime = -1L;

	public static Vector4f getFogColor(Camera camera, float tickDelta, ClientWorld world, int clampedViewDistance, float skyDarkness) {
		CameraSubmersionType cameraSubmersionType = camera.getSubmersionType();
		Entity entity = camera.getFocusedEntity();
		float r;
		float s;
		float t;
		if (cameraSubmersionType == CameraSubmersionType.WATER) {
			long l = Util.getMeasuringTimeMs();
			int i = world.getBiome(BlockPos.ofFloored(camera.getPos())).value().getWaterFogColor();
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
			r = g / 255.0F;
			s = h / 255.0F;
			t = q / 255.0F;
			if (waterFogColor != i) {
				waterFogColor = i;
				nextWaterFogColor = MathHelper.floor(g) << 16 | MathHelper.floor(h) << 8 | MathHelper.floor(q);
				lastWaterFogColorUpdateTime = l;
			}
		} else if (cameraSubmersionType == CameraSubmersionType.LAVA) {
			r = 0.6F;
			s = 0.1F;
			t = 0.0F;
			lastWaterFogColorUpdateTime = -1L;
		} else if (cameraSubmersionType == CameraSubmersionType.POWDER_SNOW) {
			r = 0.623F;
			s = 0.734F;
			t = 0.785F;
			lastWaterFogColorUpdateTime = -1L;
		} else {
			float u = 0.25F + 0.75F * (float)clampedViewDistance / 32.0F;
			u = 1.0F - (float)Math.pow((double)u, 0.25);
			int v = world.getSkyColor(camera.getPos(), tickDelta);
			float w = ColorHelper.floatFromChannel(ColorHelper.getRed(v));
			float x = ColorHelper.floatFromChannel(ColorHelper.getGreen(v));
			float y = ColorHelper.floatFromChannel(ColorHelper.getBlue(v));
			float z = MathHelper.clamp(MathHelper.cos(world.getSkyAngle(tickDelta) * (float) (Math.PI * 2)) * 2.0F + 0.5F, 0.0F, 1.0F);
			BiomeAccess biomeAccess = world.getBiomeAccess();
			Vec3d vec3d = camera.getPos().subtract(2.0, 2.0, 2.0).multiply(0.25);
			Vec3d vec3d2 = CubicSampler.sampleColor(
				vec3d, (ix, j, k) -> world.getDimensionEffects().adjustFogColor(Vec3d.unpackRgb(biomeAccess.getBiomeForNoiseGen(ix, j, k).value().getFogColor()), z)
			);
			r = (float)vec3d2.getX();
			s = (float)vec3d2.getY();
			t = (float)vec3d2.getZ();
			if (clampedViewDistance >= 4) {
				float f = MathHelper.sin(world.getSkyAngleRadians(tickDelta)) > 0.0F ? -1.0F : 1.0F;
				Vector3f vector3f = new Vector3f(f, 0.0F, 0.0F);
				float h = camera.getHorizontalPlane().dot(vector3f);
				if (h < 0.0F) {
					h = 0.0F;
				}

				if (h > 0.0F && world.getDimensionEffects().method_62183(world.getSkyAngle(tickDelta))) {
					int aa = world.getDimensionEffects().getSkyColor(world.getSkyAngle(tickDelta));
					h *= ColorHelper.floatFromChannel(ColorHelper.getAlpha(aa));
					r = r * (1.0F - h) + ColorHelper.floatFromChannel(ColorHelper.getRed(aa)) * h;
					s = s * (1.0F - h) + ColorHelper.floatFromChannel(ColorHelper.getGreen(aa)) * h;
					t = t * (1.0F - h) + ColorHelper.floatFromChannel(ColorHelper.getBlue(aa)) * h;
				}
			}

			r += (w - r) * u;
			s += (x - s) * u;
			t += (y - t) * u;
			float fx = world.getRainGradient(tickDelta);
			if (fx > 0.0F) {
				float g = 1.0F - fx * 0.5F;
				float hx = 1.0F - fx * 0.4F;
				r *= g;
				s *= g;
				t *= hx;
			}

			float g = world.getThunderGradient(tickDelta);
			if (g > 0.0F) {
				float hx = 1.0F - g * 0.5F;
				r *= hx;
				s *= hx;
				t *= hx;
			}

			lastWaterFogColorUpdateTime = -1L;
		}

		float ux = ((float)camera.getPos().y - (float)world.getBottomY()) * world.getLevelProperties().getHorizonShadingRatio();
		BackgroundRenderer.StatusEffectFogModifier statusEffectFogModifier = getFogModifier(entity, tickDelta);
		if (statusEffectFogModifier != null) {
			LivingEntity livingEntity = (LivingEntity)entity;
			ux = statusEffectFogModifier.applyColorModifier(livingEntity, livingEntity.getStatusEffect(statusEffectFogModifier.getStatusEffect()), ux, tickDelta);
		}

		if (ux < 1.0F && cameraSubmersionType != CameraSubmersionType.LAVA && cameraSubmersionType != CameraSubmersionType.POWDER_SNOW) {
			if (ux < 0.0F) {
				ux = 0.0F;
			}

			ux *= ux;
			r *= ux;
			s *= ux;
			t *= ux;
		}

		if (skyDarkness > 0.0F) {
			r = r * (1.0F - skyDarkness) + r * 0.7F * skyDarkness;
			s = s * (1.0F - skyDarkness) + s * 0.6F * skyDarkness;
			t = t * (1.0F - skyDarkness) + t * 0.6F * skyDarkness;
		}

		float wx;
		if (cameraSubmersionType == CameraSubmersionType.WATER) {
			if (entity instanceof ClientPlayerEntity) {
				wx = ((ClientPlayerEntity)entity).getUnderwaterVisibility();
			} else {
				wx = 1.0F;
			}
		} else {
			label86: {
				if (entity instanceof LivingEntity livingEntity2
					&& livingEntity2.hasStatusEffect(StatusEffects.NIGHT_VISION)
					&& !livingEntity2.hasStatusEffect(StatusEffects.DARKNESS)) {
					wx = GameRenderer.getNightVisionStrength(livingEntity2, tickDelta);
					break label86;
				}

				wx = 0.0F;
			}
		}

		if (r != 0.0F && s != 0.0F && t != 0.0F) {
			float xx = Math.min(1.0F / r, Math.min(1.0F / s, 1.0F / t));
			r = r * (1.0F - wx) + r * xx * wx;
			s = s * (1.0F - wx) + s * xx * wx;
			t = t * (1.0F - wx) + t * xx * wx;
		}

		return new Vector4f(r, s, t, 1.0F);
	}

	@Nullable
	private static BackgroundRenderer.StatusEffectFogModifier getFogModifier(Entity entity, float tickDelta) {
		return entity instanceof LivingEntity livingEntity
			? (BackgroundRenderer.StatusEffectFogModifier)FOG_MODIFIERS.stream()
				.filter(modifier -> modifier.shouldApply(livingEntity, tickDelta))
				.findFirst()
				.orElse(null)
			: null;
	}

	public static Fog applyFog(Camera camera, BackgroundRenderer.FogType fogType, Vector4f color, float viewDistance, boolean thickenFog, float tickDelta) {
		CameraSubmersionType cameraSubmersionType = camera.getSubmersionType();
		Entity entity = camera.getFocusedEntity();
		BackgroundRenderer.FogData fogData = new BackgroundRenderer.FogData(fogType);
		BackgroundRenderer.StatusEffectFogModifier statusEffectFogModifier = getFogModifier(entity, tickDelta);
		if (cameraSubmersionType == CameraSubmersionType.LAVA) {
			if (entity.isSpectator()) {
				fogData.fogStart = -8.0F;
				fogData.fogEnd = viewDistance * 0.5F;
			} else if (entity instanceof LivingEntity && ((LivingEntity)entity).hasStatusEffect(StatusEffects.FIRE_RESISTANCE)) {
				fogData.fogStart = 0.0F;
				fogData.fogEnd = 5.0F;
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
				statusEffectFogModifier.applyStartEndModifier(fogData, livingEntity, statusEffectInstance, viewDistance, tickDelta);
			}
		} else if (cameraSubmersionType == CameraSubmersionType.WATER) {
			fogData.fogStart = -8.0F;
			fogData.fogEnd = 96.0F;
			if (entity instanceof ClientPlayerEntity clientPlayerEntity) {
				fogData.fogEnd = fogData.fogEnd * Math.max(0.25F, clientPlayerEntity.getUnderwaterVisibility());
				RegistryEntry<Biome> registryEntry = clientPlayerEntity.getWorld().getBiome(clientPlayerEntity.getBlockPos());
				if (registryEntry.isIn(BiomeTags.HAS_CLOSER_WATER_FOG)) {
					fogData.fogEnd *= 0.85F;
				}
			}

			if (fogData.fogEnd > viewDistance) {
				fogData.fogEnd = viewDistance;
				fogData.fogShape = FogShape.CYLINDER;
			}
		} else if (thickenFog) {
			fogData.fogStart = viewDistance * 0.05F;
			fogData.fogEnd = Math.min(viewDistance, 192.0F) * 0.5F;
		} else if (fogType == BackgroundRenderer.FogType.FOG_SKY) {
			fogData.fogStart = 0.0F;
			fogData.fogEnd = viewDistance;
			fogData.fogShape = FogShape.CYLINDER;
		} else if (fogType == BackgroundRenderer.FogType.FOG_TERRAIN) {
			float f = MathHelper.clamp(viewDistance / 10.0F, 4.0F, 64.0F);
			fogData.fogStart = viewDistance - f;
			fogData.fogEnd = viewDistance;
			fogData.fogShape = FogShape.CYLINDER;
		}

		return new Fog(fogData.fogStart, fogData.fogEnd, fogData.fogShape, color.x, color.y, color.z, color.w);
	}

	@Environment(EnvType.CLIENT)
	static class BlindnessFogModifier implements BackgroundRenderer.StatusEffectFogModifier {
		@Override
		public RegistryEntry<StatusEffect> getStatusEffect() {
			return StatusEffects.BLINDNESS;
		}

		@Override
		public void applyStartEndModifier(BackgroundRenderer.FogData fogData, LivingEntity entity, StatusEffectInstance effect, float viewDistance, float tickDelta) {
			float f = effect.isInfinite() ? 5.0F : MathHelper.lerp(Math.min(1.0F, (float)effect.getDuration() / 20.0F), viewDistance, 5.0F);
			if (fogData.fogType == BackgroundRenderer.FogType.FOG_SKY) {
				fogData.fogStart = 0.0F;
				fogData.fogEnd = f * 0.8F;
			} else if (fogData.fogType == BackgroundRenderer.FogType.FOG_TERRAIN) {
				fogData.fogStart = f * 0.25F;
				fogData.fogEnd = f;
			}
		}
	}

	@Environment(EnvType.CLIENT)
	static class DarknessFogModifier implements BackgroundRenderer.StatusEffectFogModifier {
		@Override
		public RegistryEntry<StatusEffect> getStatusEffect() {
			return StatusEffects.DARKNESS;
		}

		@Override
		public void applyStartEndModifier(BackgroundRenderer.FogData fogData, LivingEntity entity, StatusEffectInstance effect, float viewDistance, float tickDelta) {
			float f = MathHelper.lerp(effect.getFadeFactor(entity, tickDelta), viewDistance, 15.0F);

			fogData.fogStart = switch (fogData.fogType) {
				case FOG_SKY -> 0.0F;
				case FOG_TERRAIN -> f * 0.75F;
			};
			fogData.fogEnd = f;
		}

		@Override
		public float applyColorModifier(LivingEntity entity, StatusEffectInstance effect, float defaultModifier, float tickDelta) {
			return 1.0F - effect.getFadeFactor(entity, tickDelta);
		}
	}

	@Environment(EnvType.CLIENT)
	static class FogData {
		public final BackgroundRenderer.FogType fogType;
		public float fogStart;
		public float fogEnd;
		public FogShape fogShape = FogShape.SPHERE;

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
		RegistryEntry<StatusEffect> getStatusEffect();

		void applyStartEndModifier(BackgroundRenderer.FogData fogData, LivingEntity entity, StatusEffectInstance effect, float viewDistance, float tickDelta);

		default boolean shouldApply(LivingEntity entity, float tickDelta) {
			return entity.hasStatusEffect(this.getStatusEffect());
		}

		default float applyColorModifier(LivingEntity entity, StatusEffectInstance effect, float defaultModifier, float tickDelta) {
			StatusEffectInstance statusEffectInstance = entity.getStatusEffect(this.getStatusEffect());
			if (statusEffectInstance != null) {
				if (statusEffectInstance.isDurationBelow(19)) {
					defaultModifier = 1.0F - (float)statusEffectInstance.getDuration() / 20.0F;
				} else {
					defaultModifier = 0.0F;
				}
			}

			return defaultModifier;
		}
	}
}
