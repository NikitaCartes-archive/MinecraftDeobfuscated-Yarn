/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
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

@Environment(value=EnvType.CLIENT)
public class BackgroundRenderer {
    private static final int field_32685 = 96;
    public static final float field_32684 = 5000.0f;
    private static float red;
    private static float green;
    private static float blue;
    private static int waterFogColor;
    private static int nextWaterFogColor;
    private static long lastWaterFogColorUpdateTime;

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
                Vec3f vec3f = new Vec3f(f, 0.0f, 0.0f);
                h = camera.getHorizontalPlane().dot(vec3f);
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
        if (camera.getFocusedEntity() instanceof LivingEntity && ((LivingEntity)camera.getFocusedEntity()).hasStatusEffect(StatusEffects.BLINDNESS)) {
            int w = ((LivingEntity)camera.getFocusedEntity()).getStatusEffect(StatusEffects.BLINDNESS).getDuration();
            r = w < 20 ? 1.0f - (float)w / 20.0f : 0.0f;
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
        float x2 = cameraSubmersionType == CameraSubmersionType.WATER ? (entity instanceof ClientPlayerEntity ? ((ClientPlayerEntity)entity).getUnderwaterVisibility() : 1.0f) : (entity instanceof LivingEntity && ((LivingEntity)entity).hasStatusEffect(StatusEffects.NIGHT_VISION) ? GameRenderer.getNightVisionStrength((LivingEntity)entity, tickDelta) : 0.0f);
        if (red != 0.0f && green != 0.0f && blue != 0.0f) {
            float s = Math.min(1.0f / red, Math.min(1.0f / green, 1.0f / blue));
            red = red * (1.0f - x2) + red * s * x2;
            green = green * (1.0f - x2) + green * s * x2;
            blue = blue * (1.0f - x2) + blue * s * x2;
        }
        RenderSystem.clearColor(red, green, blue, 0.0f);
    }

    public static void clearFog() {
        RenderSystem.setShaderFogStart(Float.MAX_VALUE);
    }

    public static void applyFog(Camera camera, FogType fogType, float viewDistance, boolean thickFog) {
        float g;
        float f;
        CameraSubmersionType cameraSubmersionType = camera.getSubmersionType();
        Entity entity = camera.getFocusedEntity();
        FogShape fogShape = FogShape.SPHERE;
        if (cameraSubmersionType == CameraSubmersionType.LAVA) {
            if (entity.isSpectator()) {
                f = -8.0f;
                g = viewDistance * 0.5f;
            } else if (entity instanceof LivingEntity && ((LivingEntity)entity).hasStatusEffect(StatusEffects.FIRE_RESISTANCE)) {
                f = 0.0f;
                g = 3.0f;
            } else {
                f = 0.25f;
                g = 1.0f;
            }
        } else if (cameraSubmersionType == CameraSubmersionType.POWDER_SNOW) {
            if (entity.isSpectator()) {
                f = -8.0f;
                g = viewDistance * 0.5f;
            } else {
                f = 0.0f;
                g = 2.0f;
            }
        } else if (entity instanceof LivingEntity && ((LivingEntity)entity).hasStatusEffect(StatusEffects.BLINDNESS)) {
            int i = ((LivingEntity)entity).getStatusEffect(StatusEffects.BLINDNESS).getDuration();
            float h = MathHelper.lerp(Math.min(1.0f, (float)i / 20.0f), viewDistance, 5.0f);
            if (fogType == FogType.FOG_SKY) {
                f = 0.0f;
                g = h * 0.8f;
            } else {
                f = cameraSubmersionType == CameraSubmersionType.WATER ? -4.0f : h * 0.25f;
                g = h;
            }
        } else if (cameraSubmersionType == CameraSubmersionType.WATER) {
            f = -8.0f;
            g = 96.0f;
            if (entity instanceof ClientPlayerEntity) {
                ClientPlayerEntity clientPlayerEntity = (ClientPlayerEntity)entity;
                g *= Math.max(0.25f, clientPlayerEntity.getUnderwaterVisibility());
                RegistryEntry<Biome> registryEntry = clientPlayerEntity.world.getBiome(clientPlayerEntity.getBlockPos());
                if (registryEntry.isIn(BiomeTags.HAS_CLOSER_WATER_FOG)) {
                    g *= 0.85f;
                }
            }
            if (g > viewDistance) {
                g = viewDistance;
                fogShape = FogShape.CYLINDER;
            }
        } else if (thickFog) {
            f = viewDistance * 0.05f;
            g = Math.min(viewDistance, 192.0f) * 0.5f;
        } else if (fogType == FogType.FOG_SKY) {
            f = 0.0f;
            g = viewDistance;
            fogShape = FogShape.CYLINDER;
        } else {
            float j = MathHelper.clamp(viewDistance / 10.0f, 4.0f, 64.0f);
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

    static {
        waterFogColor = -1;
        nextWaterFogColor = -1;
        lastWaterFogColorUpdateTime = -1L;
    }

    @Environment(value=EnvType.CLIENT)
    public static enum FogType {
        FOG_SKY,
        FOG_TERRAIN;

    }
}

