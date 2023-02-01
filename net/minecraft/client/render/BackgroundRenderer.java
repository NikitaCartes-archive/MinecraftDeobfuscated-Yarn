/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.CameraSubmersionType;
import net.minecraft.client.render.FogShape;
import net.minecraft.client.render.GameRenderer;
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
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeAccess;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

@Environment(value=EnvType.CLIENT)
public class BackgroundRenderer {
    private static final int field_32685 = 96;
    private static final List<StatusEffectFogModifier> FOG_MODIFIERS = Lists.newArrayList(new BlindnessFogModifier(), new DarknessFogModifier());
    public static final float field_32684 = 5000.0f;
    private static float red;
    private static float green;
    private static float blue;
    private static int waterFogColor;
    private static int nextWaterFogColor;
    private static long lastWaterFogColorUpdateTime;

    public static void render(Camera camera, float tickDelta, ClientWorld world, int viewDistance, float skyDarkness) {
        LivingEntity livingEntity2;
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
            float f = MathHelper.clamp((float)(l - lastWaterFogColorUpdateTime) / 5000.0f, 0.0f, 1.0f);
            float g = MathHelper.lerp(f, n, j);
            float h = MathHelper.lerp(f, o, k);
            float q = MathHelper.lerp(f, p, m);
            red = g / 255.0f;
            green = h / 255.0f;
            blue = q / 255.0f;
            if (waterFogColor != i) {
                waterFogColor = i;
                nextWaterFogColor = MathHelper.floor(g) << 16 | MathHelper.floor(h) << 8 | MathHelper.floor(q);
                lastWaterFogColorUpdateTime = l;
            }
        } else if (cameraSubmersionType == CameraSubmersionType.LAVA) {
            red = 0.6f;
            green = 0.1f;
            blue = 0.0f;
            lastWaterFogColorUpdateTime = -1L;
        } else if (cameraSubmersionType == CameraSubmersionType.POWDER_SNOW) {
            red = 0.623f;
            green = 0.734f;
            blue = 0.785f;
            lastWaterFogColorUpdateTime = -1L;
            RenderSystem.clearColor(red, green, blue, 0.0f);
        } else {
            float g;
            float h;
            float f;
            float r = 0.25f + 0.75f * (float)viewDistance / 32.0f;
            r = 1.0f - (float)Math.pow(r, 0.25);
            Vec3d vec3d = world.getSkyColor(camera.getPos(), tickDelta);
            float s = (float)vec3d.x;
            float t = (float)vec3d.y;
            float u = (float)vec3d.z;
            float v = MathHelper.clamp(MathHelper.cos(world.getSkyAngle(tickDelta) * ((float)Math.PI * 2)) * 2.0f + 0.5f, 0.0f, 1.0f);
            BiomeAccess biomeAccess = world.getBiomeAccess();
            Vec3d vec3d2 = camera.getPos().subtract(2.0, 2.0, 2.0).multiply(0.25);
            Vec3d vec3d3 = CubicSampler.sampleColor(vec3d2, (x, y, z) -> world.getDimensionEffects().adjustFogColor(Vec3d.unpackRgb(biomeAccess.getBiomeForNoiseGen(x, y, z).value().getFogColor()), v));
            red = (float)vec3d3.getX();
            green = (float)vec3d3.getY();
            blue = (float)vec3d3.getZ();
            if (viewDistance >= 4) {
                float[] fs;
                f = MathHelper.sin(world.getSkyAngleRadians(tickDelta)) > 0.0f ? -1.0f : 1.0f;
                Vector3f vector3f = new Vector3f(f, 0.0f, 0.0f);
                h = camera.getHorizontalPlane().dot(vector3f);
                if (h < 0.0f) {
                    h = 0.0f;
                }
                if (h > 0.0f && (fs = world.getDimensionEffects().getFogColorOverride(world.getSkyAngle(tickDelta), tickDelta)) != null) {
                    red = red * (1.0f - (h *= fs[3])) + fs[0] * h;
                    green = green * (1.0f - h) + fs[1] * h;
                    blue = blue * (1.0f - h) + fs[2] * h;
                }
            }
            red += (s - red) * r;
            green += (t - green) * r;
            blue += (u - blue) * r;
            f = world.getRainGradient(tickDelta);
            if (f > 0.0f) {
                float g2 = 1.0f - f * 0.5f;
                h = 1.0f - f * 0.4f;
                red *= g2;
                green *= g2;
                blue *= h;
            }
            if ((g = world.getThunderGradient(tickDelta)) > 0.0f) {
                h = 1.0f - g * 0.5f;
                red *= h;
                green *= h;
                blue *= h;
            }
            lastWaterFogColorUpdateTime = -1L;
        }
        float r = ((float)camera.getPos().y - (float)world.getBottomY()) * world.getLevelProperties().getHorizonShadingRatio();
        StatusEffectFogModifier statusEffectFogModifier = BackgroundRenderer.getFogModifier(entity, tickDelta);
        if (statusEffectFogModifier != null) {
            LivingEntity livingEntity = (LivingEntity)entity;
            r = statusEffectFogModifier.applyColorModifier(livingEntity, livingEntity.getStatusEffect(statusEffectFogModifier.getStatusEffect()), r, tickDelta);
        }
        if (r < 1.0f && cameraSubmersionType != CameraSubmersionType.LAVA && cameraSubmersionType != CameraSubmersionType.POWDER_SNOW) {
            if (r < 0.0f) {
                r = 0.0f;
            }
            r *= r;
            red *= r;
            green *= r;
            blue *= r;
        }
        if (skyDarkness > 0.0f) {
            red = red * (1.0f - skyDarkness) + red * 0.7f * skyDarkness;
            green = green * (1.0f - skyDarkness) + green * 0.6f * skyDarkness;
            blue = blue * (1.0f - skyDarkness) + blue * 0.6f * skyDarkness;
        }
        float s = cameraSubmersionType == CameraSubmersionType.WATER ? (entity instanceof ClientPlayerEntity ? ((ClientPlayerEntity)entity).getUnderwaterVisibility() : 1.0f) : (entity instanceof LivingEntity && (livingEntity2 = (LivingEntity)entity).hasStatusEffect(StatusEffects.NIGHT_VISION) && !livingEntity2.hasStatusEffect(StatusEffects.DARKNESS) ? GameRenderer.getNightVisionStrength(livingEntity2, tickDelta) : 0.0f);
        if (red != 0.0f && green != 0.0f && blue != 0.0f) {
            float t = Math.min(1.0f / red, Math.min(1.0f / green, 1.0f / blue));
            red = red * (1.0f - s) + red * t * s;
            green = green * (1.0f - s) + green * t * s;
            blue = blue * (1.0f - s) + blue * t * s;
        }
        RenderSystem.clearColor(red, green, blue, 0.0f);
    }

    public static void clearFog() {
        RenderSystem.setShaderFogStart(Float.MAX_VALUE);
    }

    @Nullable
    private static StatusEffectFogModifier getFogModifier(Entity entity, float tickDelta) {
        if (entity instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity)entity;
            return FOG_MODIFIERS.stream().filter(modifier -> modifier.shouldApply(livingEntity, tickDelta)).findFirst().orElse(null);
        }
        return null;
    }

    public static void applyFog(Camera camera, FogType fogType, float viewDistance, boolean thickFog, float tickDelta) {
        CameraSubmersionType cameraSubmersionType = camera.getSubmersionType();
        Entity entity = camera.getFocusedEntity();
        FogData fogData = new FogData(fogType);
        StatusEffectFogModifier statusEffectFogModifier = BackgroundRenderer.getFogModifier(entity, tickDelta);
        if (cameraSubmersionType == CameraSubmersionType.LAVA) {
            if (entity.isSpectator()) {
                fogData.fogStart = -8.0f;
                fogData.fogEnd = viewDistance * 0.5f;
            } else if (entity instanceof LivingEntity && ((LivingEntity)entity).hasStatusEffect(StatusEffects.FIRE_RESISTANCE)) {
                fogData.fogStart = 0.0f;
                fogData.fogEnd = 3.0f;
            } else {
                fogData.fogStart = 0.25f;
                fogData.fogEnd = 1.0f;
            }
        } else if (cameraSubmersionType == CameraSubmersionType.POWDER_SNOW) {
            if (entity.isSpectator()) {
                fogData.fogStart = -8.0f;
                fogData.fogEnd = viewDistance * 0.5f;
            } else {
                fogData.fogStart = 0.0f;
                fogData.fogEnd = 2.0f;
            }
        } else if (statusEffectFogModifier != null) {
            LivingEntity livingEntity = (LivingEntity)entity;
            StatusEffectInstance statusEffectInstance = livingEntity.getStatusEffect(statusEffectFogModifier.getStatusEffect());
            if (statusEffectInstance != null) {
                statusEffectFogModifier.applyStartEndModifier(fogData, livingEntity, statusEffectInstance, viewDistance, tickDelta);
            }
        } else if (cameraSubmersionType == CameraSubmersionType.WATER) {
            fogData.fogStart = -8.0f;
            fogData.fogEnd = 96.0f;
            if (entity instanceof ClientPlayerEntity) {
                ClientPlayerEntity clientPlayerEntity = (ClientPlayerEntity)entity;
                fogData.fogEnd *= Math.max(0.25f, clientPlayerEntity.getUnderwaterVisibility());
                RegistryEntry<Biome> registryEntry = clientPlayerEntity.world.getBiome(clientPlayerEntity.getBlockPos());
                if (registryEntry.isIn(BiomeTags.HAS_CLOSER_WATER_FOG)) {
                    fogData.fogEnd *= 0.85f;
                }
            }
            if (fogData.fogEnd > viewDistance) {
                fogData.fogEnd = viewDistance;
                fogData.fogShape = FogShape.CYLINDER;
            }
        } else if (thickFog) {
            fogData.fogStart = viewDistance * 0.05f;
            fogData.fogEnd = Math.min(viewDistance, 192.0f) * 0.5f;
        } else if (fogType == FogType.FOG_SKY) {
            fogData.fogStart = 0.0f;
            fogData.fogEnd = viewDistance;
            fogData.fogShape = FogShape.CYLINDER;
        } else {
            float f = MathHelper.clamp(viewDistance / 10.0f, 4.0f, 64.0f);
            fogData.fogStart = viewDistance - f;
            fogData.fogEnd = viewDistance;
            fogData.fogShape = FogShape.CYLINDER;
        }
        RenderSystem.setShaderFogStart(fogData.fogStart);
        RenderSystem.setShaderFogEnd(fogData.fogEnd);
        RenderSystem.setShaderFogShape(fogData.fogShape);
    }

    public static void setFogBlack() {
        RenderSystem.setShaderFogColor(red, green, blue);
    }

    static {
        waterFogColor = -1;
        nextWaterFogColor = -1;
        lastWaterFogColorUpdateTime = -1L;
    }

    @Environment(value=EnvType.CLIENT)
    static interface StatusEffectFogModifier {
        public StatusEffect getStatusEffect();

        public void applyStartEndModifier(FogData var1, LivingEntity var2, StatusEffectInstance var3, float var4, float var5);

        default public boolean shouldApply(LivingEntity entity, float tickDelta) {
            return entity.hasStatusEffect(this.getStatusEffect());
        }

        default public float applyColorModifier(LivingEntity entity, StatusEffectInstance effect, float f, float tickDelta) {
            StatusEffectInstance statusEffectInstance = entity.getStatusEffect(this.getStatusEffect());
            if (statusEffectInstance != null) {
                f = statusEffectInstance.isDurationBelow(19) ? 1.0f - (float)statusEffectInstance.getDuration() / 20.0f : 0.0f;
            }
            return f;
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class FogData {
        public final FogType fogType;
        public float fogStart;
        public float fogEnd;
        public FogShape fogShape = FogShape.SPHERE;

        public FogData(FogType fogType) {
            this.fogType = fogType;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static enum FogType {
        FOG_SKY,
        FOG_TERRAIN;

    }

    @Environment(value=EnvType.CLIENT)
    static class BlindnessFogModifier
    implements StatusEffectFogModifier {
        BlindnessFogModifier() {
        }

        @Override
        public StatusEffect getStatusEffect() {
            return StatusEffects.BLINDNESS;
        }

        @Override
        public void applyStartEndModifier(FogData fogData, LivingEntity entity, StatusEffectInstance effect, float viewDistance, float tickDelta) {
            float f;
            float f2 = f = effect.isInfinite() ? 5.0f : MathHelper.lerp(Math.min(1.0f, (float)effect.getDuration() / 20.0f), viewDistance, 5.0f);
            if (fogData.fogType == FogType.FOG_SKY) {
                fogData.fogStart = 0.0f;
                fogData.fogEnd = f * 0.8f;
            } else {
                fogData.fogStart = f * 0.25f;
                fogData.fogEnd = f;
            }
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class DarknessFogModifier
    implements StatusEffectFogModifier {
        DarknessFogModifier() {
        }

        @Override
        public StatusEffect getStatusEffect() {
            return StatusEffects.DARKNESS;
        }

        @Override
        public void applyStartEndModifier(FogData fogData, LivingEntity entity, StatusEffectInstance effect, float viewDistance, float tickDelta) {
            if (effect.getFactorCalculationData().isEmpty()) {
                return;
            }
            float f = MathHelper.lerp(effect.getFactorCalculationData().get().lerp(entity, tickDelta), viewDistance, 15.0f);
            fogData.fogStart = fogData.fogType == FogType.FOG_SKY ? 0.0f : f * 0.75f;
            fogData.fogEnd = f;
        }

        @Override
        public float applyColorModifier(LivingEntity entity, StatusEffectInstance effect, float f, float tickDelta) {
            if (effect.getFactorCalculationData().isEmpty()) {
                return 0.0f;
            }
            return 1.0f - effect.getFactorCalculationData().get().lerp(entity, tickDelta);
        }
    }
}

