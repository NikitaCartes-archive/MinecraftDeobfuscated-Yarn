/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.fluid.FluidState;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.CubicSampler;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeAccess;

@Environment(value=EnvType.CLIENT)
public class BackgroundRenderer {
    private static float red;
    private static float green;
    private static float blue;
    private static int waterFogColor;
    private static int nextWaterFogColor;
    private static long lastWaterFogColorUpdateTime;

    public static void render(Camera camera, float tickDelta, ClientWorld world, int i2, float f) {
        int j2;
        FluidState fluidState = camera.getSubmergedFluidState();
        if (fluidState.isIn(FluidTags.WATER)) {
            long l = Util.getMeasuringTimeMs();
            j2 = world.getBiome(new BlockPos(camera.getPos())).getWaterFogColor();
            if (lastWaterFogColorUpdateTime < 0L) {
                waterFogColor = j2;
                nextWaterFogColor = j2;
                lastWaterFogColorUpdateTime = l;
            }
            int k2 = waterFogColor >> 16 & 0xFF;
            int m = waterFogColor >> 8 & 0xFF;
            int n = waterFogColor & 0xFF;
            int o = nextWaterFogColor >> 16 & 0xFF;
            int p = nextWaterFogColor >> 8 & 0xFF;
            int q = nextWaterFogColor & 0xFF;
            float g = MathHelper.clamp((float)(l - lastWaterFogColorUpdateTime) / 5000.0f, 0.0f, 1.0f);
            float h = MathHelper.lerp(g, o, k2);
            float r = MathHelper.lerp(g, p, m);
            float s = MathHelper.lerp(g, q, n);
            red = h / 255.0f;
            green = r / 255.0f;
            blue = s / 255.0f;
            if (waterFogColor != j2) {
                waterFogColor = j2;
                nextWaterFogColor = MathHelper.floor(h) << 16 | MathHelper.floor(r) << 8 | MathHelper.floor(s);
                lastWaterFogColorUpdateTime = l;
            }
        } else if (fluidState.isIn(FluidTags.LAVA)) {
            red = 0.6f;
            green = 0.1f;
            blue = 0.0f;
            lastWaterFogColorUpdateTime = -1L;
        } else {
            float h;
            float r;
            float g;
            float t = 0.25f + 0.75f * (float)i2 / 32.0f;
            t = 1.0f - (float)Math.pow(t, 0.25);
            Vec3d vec3d = world.method_23777(camera.getBlockPos(), tickDelta);
            float u = (float)vec3d.x;
            float v = (float)vec3d.y;
            float w = (float)vec3d.z;
            float x = MathHelper.clamp(MathHelper.cos(world.getSkyAngle(tickDelta) * ((float)Math.PI * 2)) * 2.0f + 0.5f, 0.0f, 1.0f);
            BiomeAccess biomeAccess = world.getBiomeAccess();
            Vec3d vec3d2 = camera.getPos().subtract(2.0, 2.0, 2.0).multiply(0.25);
            Vec3d vec3d3 = CubicSampler.sampleColor(vec3d2, (i, j, k) -> world.getSkyProperties().adjustFogColor(Vec3d.unpackRgb(biomeAccess.getBiomeForNoiseGen(i, j, k).getFogColor()), x));
            red = (float)vec3d3.getX();
            green = (float)vec3d3.getY();
            blue = (float)vec3d3.getZ();
            if (i2 >= 4) {
                float[] fs;
                g = MathHelper.sin(world.getSkyAngleRadians(tickDelta)) > 0.0f ? -1.0f : 1.0f;
                Vec3f vec3f = new Vec3f(g, 0.0f, 0.0f);
                r = camera.getHorizontalPlane().dot(vec3f);
                if (r < 0.0f) {
                    r = 0.0f;
                }
                if (r > 0.0f && (fs = world.getSkyProperties().getFogColorOverride(world.getSkyAngle(tickDelta), tickDelta)) != null) {
                    red = red * (1.0f - (r *= fs[3])) + fs[0] * r;
                    green = green * (1.0f - r) + fs[1] * r;
                    blue = blue * (1.0f - r) + fs[2] * r;
                }
            }
            red += (u - red) * t;
            green += (v - green) * t;
            blue += (w - blue) * t;
            g = world.getRainGradient(tickDelta);
            if (g > 0.0f) {
                float h2 = 1.0f - g * 0.5f;
                r = 1.0f - g * 0.4f;
                red *= h2;
                green *= h2;
                blue *= r;
            }
            if ((h = world.getThunderGradient(tickDelta)) > 0.0f) {
                r = 1.0f - h * 0.5f;
                red *= r;
                green *= r;
                blue *= r;
            }
            lastWaterFogColorUpdateTime = -1L;
        }
        double d = camera.getPos().y * world.getLevelProperties().getHorizonShadingRatio();
        if (camera.getFocusedEntity() instanceof LivingEntity && ((LivingEntity)camera.getFocusedEntity()).hasStatusEffect(StatusEffects.BLINDNESS)) {
            j2 = ((LivingEntity)camera.getFocusedEntity()).getStatusEffect(StatusEffects.BLINDNESS).getDuration();
            d = j2 < 20 ? (d *= (double)(1.0f - (float)j2 / 20.0f)) : 0.0;
        }
        if (d < 1.0 && !fluidState.isIn(FluidTags.LAVA)) {
            if (d < 0.0) {
                d = 0.0;
            }
            d *= d;
            red = (float)((double)red * d);
            green = (float)((double)green * d);
            blue = (float)((double)blue * d);
        }
        if (f > 0.0f) {
            red = red * (1.0f - f) + red * 0.7f * f;
            green = green * (1.0f - f) + green * 0.6f * f;
            blue = blue * (1.0f - f) + blue * 0.6f * f;
        }
        if (fluidState.isIn(FluidTags.WATER)) {
            float u = 0.0f;
            if (camera.getFocusedEntity() instanceof ClientPlayerEntity) {
                ClientPlayerEntity clientPlayerEntity = (ClientPlayerEntity)camera.getFocusedEntity();
                u = clientPlayerEntity.getUnderwaterVisibility();
            }
            float v = Math.min(1.0f / red, Math.min(1.0f / green, 1.0f / blue));
            red = red * (1.0f - u) + red * v * u;
            green = green * (1.0f - u) + green * v * u;
            blue = blue * (1.0f - u) + blue * v * u;
        } else if (camera.getFocusedEntity() instanceof LivingEntity && ((LivingEntity)camera.getFocusedEntity()).hasStatusEffect(StatusEffects.NIGHT_VISION)) {
            float u = GameRenderer.getNightVisionStrength((LivingEntity)camera.getFocusedEntity(), tickDelta);
            float v = Math.min(1.0f / red, Math.min(1.0f / green, 1.0f / blue));
            red = red * (1.0f - u) + red * v * u;
            green = green * (1.0f - u) + green * v * u;
            blue = blue * (1.0f - u) + blue * v * u;
        }
        RenderSystem.clearColor(red, green, blue, 0.0f);
    }

    public static void method_23792() {
        RenderSystem.fogDensity(0.0f);
        RenderSystem.fogMode(GlStateManager.FogMode.EXP2);
    }

    public static void applyFog(Camera camera, FogType fogType, float viewDistance, boolean thickFog) {
        FluidState fluidState = camera.getSubmergedFluidState();
        Entity entity = camera.getFocusedEntity();
        if (fluidState.isIn(FluidTags.WATER)) {
            float f = 1.0f;
            f = 0.05f;
            if (entity instanceof ClientPlayerEntity) {
                ClientPlayerEntity clientPlayerEntity = (ClientPlayerEntity)entity;
                f -= clientPlayerEntity.getUnderwaterVisibility() * clientPlayerEntity.getUnderwaterVisibility() * 0.03f;
                Biome biome = clientPlayerEntity.world.getBiome(clientPlayerEntity.getBlockPos());
                if (biome.getCategory() == Biome.Category.SWAMP) {
                    f += 0.005f;
                }
            }
            RenderSystem.fogDensity(f);
            RenderSystem.fogMode(GlStateManager.FogMode.EXP2);
        } else {
            float g;
            float f;
            if (fluidState.isIn(FluidTags.LAVA)) {
                if (entity instanceof LivingEntity && ((LivingEntity)entity).hasStatusEffect(StatusEffects.FIRE_RESISTANCE)) {
                    f = 0.0f;
                    g = 3.0f;
                } else {
                    f = 0.25f;
                    g = 1.0f;
                }
            } else if (entity instanceof LivingEntity && ((LivingEntity)entity).hasStatusEffect(StatusEffects.BLINDNESS)) {
                int i = ((LivingEntity)entity).getStatusEffect(StatusEffects.BLINDNESS).getDuration();
                float h = MathHelper.lerp(Math.min(1.0f, (float)i / 20.0f), viewDistance, 5.0f);
                if (fogType == FogType.FOG_SKY) {
                    f = 0.0f;
                    g = h * 0.8f;
                } else {
                    f = h * 0.25f;
                    g = h;
                }
            } else if (thickFog) {
                f = viewDistance * 0.05f;
                g = Math.min(viewDistance, 192.0f) * 0.5f;
            } else if (fogType == FogType.FOG_SKY) {
                f = 0.0f;
                g = viewDistance;
            } else {
                f = viewDistance * 0.75f;
                g = viewDistance;
            }
            RenderSystem.fogStart(f);
            RenderSystem.fogEnd(g);
            RenderSystem.fogMode(GlStateManager.FogMode.LINEAR);
            RenderSystem.setupNvFogDistance();
        }
    }

    public static void setFogBlack() {
        RenderSystem.fog(2918, red, green, blue, 1.0f);
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

