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
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.CubicSampler;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.dimension.Dimension;

@Environment(value=EnvType.CLIENT)
public class BackgroundRenderer {
    private static float red;
    private static float green;
    private static float blue;
    private static int waterFogColor;
    private static int nextWaterFogColor;
    private static long lastWaterFogColorUpdateTime;

    public static void render(Camera camera, float f, ClientWorld clientWorld, int i2, float g) {
        int j2;
        FluidState fluidState = camera.getSubmergedFluidState();
        if (fluidState.matches(FluidTags.WATER)) {
            long l = Util.getMeasuringTimeMs();
            j2 = clientWorld.getBiome(new BlockPos(camera.getPos())).getWaterFogColor();
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
            float h = MathHelper.clamp((float)(l - lastWaterFogColorUpdateTime) / 5000.0f, 0.0f, 1.0f);
            float r = MathHelper.lerp(h, o, k2);
            float s = MathHelper.lerp(h, p, m);
            float t = MathHelper.lerp(h, q, n);
            red = r / 255.0f;
            green = s / 255.0f;
            blue = t / 255.0f;
            if (waterFogColor != j2) {
                waterFogColor = j2;
                nextWaterFogColor = MathHelper.floor(r) << 16 | MathHelper.floor(s) << 8 | MathHelper.floor(t);
                lastWaterFogColorUpdateTime = l;
            }
        } else if (fluidState.matches(FluidTags.LAVA)) {
            red = 0.6f;
            green = 0.1f;
            blue = 0.0f;
            lastWaterFogColorUpdateTime = -1L;
        } else {
            float s;
            float t;
            float r;
            float u = 0.25f + 0.75f * (float)i2 / 32.0f;
            u = 1.0f - (float)Math.pow(u, 0.25);
            Vec3d vec3d = clientWorld.method_23777(camera.getBlockPos(), f);
            float v = (float)vec3d.x;
            float w = (float)vec3d.y;
            float x = (float)vec3d.z;
            float y = MathHelper.clamp(MathHelper.cos(clientWorld.getSkyAngle(f) * ((float)Math.PI * 2)) * 2.0f + 0.5f, 0.0f, 1.0f);
            BiomeAccess biomeAccess = clientWorld.getBiomeAccess();
            Dimension dimension = clientWorld.getDimension();
            Vec3d vec3d2 = camera.getPos().subtract(2.0, 2.0, 2.0).multiply(0.25);
            Vec3d vec3d3 = CubicSampler.sampleColor(vec3d2, (i, j, k) -> dimension.modifyFogColor(Vec3d.unpackRgb(biomeAccess.getBiomeForNoiseGen(i, j, k).getFogColor()), y));
            red = (float)vec3d3.getX();
            green = (float)vec3d3.getY();
            blue = (float)vec3d3.getZ();
            if (i2 >= 4) {
                float[] fs;
                r = MathHelper.sin(clientWorld.getSkyAngleRadians(f)) > 0.0f ? -1.0f : 1.0f;
                Vector3f vector3f = new Vector3f(r, 0.0f, 0.0f);
                t = camera.getHorizontalPlane().dot(vector3f);
                if (t < 0.0f) {
                    t = 0.0f;
                }
                if (t > 0.0f && (fs = clientWorld.dimension.getBackgroundColor(clientWorld.getSkyAngle(f), f)) != null) {
                    red = red * (1.0f - (t *= fs[3])) + fs[0] * t;
                    green = green * (1.0f - t) + fs[1] * t;
                    blue = blue * (1.0f - t) + fs[2] * t;
                }
            }
            red += (v - red) * u;
            green += (w - green) * u;
            blue += (x - blue) * u;
            r = clientWorld.getRainGradient(f);
            if (r > 0.0f) {
                float s2 = 1.0f - r * 0.5f;
                t = 1.0f - r * 0.4f;
                red *= s2;
                green *= s2;
                blue *= t;
            }
            if ((s = clientWorld.getThunderGradient(f)) > 0.0f) {
                t = 1.0f - s * 0.5f;
                red *= t;
                green *= t;
                blue *= t;
            }
            lastWaterFogColorUpdateTime = -1L;
        }
        double d = camera.getPos().y * clientWorld.dimension.getHorizonShadingRatio();
        if (camera.getFocusedEntity() instanceof LivingEntity && ((LivingEntity)camera.getFocusedEntity()).hasStatusEffect(StatusEffects.BLINDNESS)) {
            j2 = ((LivingEntity)camera.getFocusedEntity()).getStatusEffect(StatusEffects.BLINDNESS).getDuration();
            d = j2 < 20 ? (d *= (double)(1.0f - (float)j2 / 20.0f)) : 0.0;
        }
        if (d < 1.0) {
            if (d < 0.0) {
                d = 0.0;
            }
            d *= d;
            red = (float)((double)red * d);
            green = (float)((double)green * d);
            blue = (float)((double)blue * d);
        }
        if (g > 0.0f) {
            red = red * (1.0f - g) + red * 0.7f * g;
            green = green * (1.0f - g) + green * 0.6f * g;
            blue = blue * (1.0f - g) + blue * 0.6f * g;
        }
        if (fluidState.matches(FluidTags.WATER)) {
            float v = 0.0f;
            if (camera.getFocusedEntity() instanceof ClientPlayerEntity) {
                ClientPlayerEntity clientPlayerEntity = (ClientPlayerEntity)camera.getFocusedEntity();
                v = clientPlayerEntity.getUnderwaterVisibility();
            }
            float w = Math.min(1.0f / red, Math.min(1.0f / green, 1.0f / blue));
            red = red * (1.0f - v) + red * w * v;
            green = green * (1.0f - v) + green * w * v;
            blue = blue * (1.0f - v) + blue * w * v;
        } else if (camera.getFocusedEntity() instanceof LivingEntity && ((LivingEntity)camera.getFocusedEntity()).hasStatusEffect(StatusEffects.NIGHT_VISION)) {
            float v = GameRenderer.getNightVisionStrength((LivingEntity)camera.getFocusedEntity(), f);
            float w = Math.min(1.0f / red, Math.min(1.0f / green, 1.0f / blue));
            red = red * (1.0f - v) + red * w * v;
            green = green * (1.0f - v) + green * w * v;
            blue = blue * (1.0f - v) + blue * w * v;
        }
        RenderSystem.clearColor(red, green, blue, 0.0f);
    }

    public static void method_23792() {
        RenderSystem.fogDensity(0.0f);
        RenderSystem.fogMode(GlStateManager.FogMode.EXP2);
    }

    public static void applyFog(Camera camera, FogType fogType, float viewDistance, boolean thickFog) {
        boolean bl;
        FluidState fluidState = camera.getSubmergedFluidState();
        Entity entity = camera.getFocusedEntity();
        boolean bl2 = bl = fluidState.getFluid() != Fluids.EMPTY;
        if (bl) {
            float f = 1.0f;
            if (fluidState.matches(FluidTags.WATER)) {
                f = 0.05f;
                if (entity instanceof ClientPlayerEntity) {
                    ClientPlayerEntity clientPlayerEntity = (ClientPlayerEntity)entity;
                    f -= clientPlayerEntity.getUnderwaterVisibility() * clientPlayerEntity.getUnderwaterVisibility() * 0.03f;
                    Biome biome = clientPlayerEntity.world.getBiome(clientPlayerEntity.getSenseCenterPos());
                    if (biome == Biomes.SWAMP || biome == Biomes.SWAMP_HILLS) {
                        f += 0.005f;
                    }
                }
            } else if (fluidState.matches(FluidTags.LAVA)) {
                f = 2.0f;
            }
            RenderSystem.fogDensity(f);
            RenderSystem.fogMode(GlStateManager.FogMode.EXP2);
        } else {
            float h;
            float f;
            if (entity instanceof LivingEntity && ((LivingEntity)entity).hasStatusEffect(StatusEffects.BLINDNESS)) {
                int i = ((LivingEntity)entity).getStatusEffect(StatusEffects.BLINDNESS).getDuration();
                float g = MathHelper.lerp(Math.min(1.0f, (float)i / 20.0f), viewDistance, 5.0f);
                if (fogType == FogType.FOG_SKY) {
                    f = 0.0f;
                    h = g * 0.8f;
                } else {
                    f = g * 0.25f;
                    h = g;
                }
            } else if (thickFog) {
                f = viewDistance * 0.05f;
                h = Math.min(viewDistance, 192.0f) * 0.5f;
            } else if (fogType == FogType.FOG_SKY) {
                f = 0.0f;
                h = viewDistance;
            } else {
                f = viewDistance * 0.75f;
                h = viewDistance;
            }
            RenderSystem.fogStart(f);
            RenderSystem.fogEnd(h);
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

