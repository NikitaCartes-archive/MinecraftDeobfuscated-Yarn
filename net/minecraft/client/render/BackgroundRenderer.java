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
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;

@Environment(value=EnvType.CLIENT)
public class BackgroundRenderer {
    private static float red;
    private static float green;
    private static float blue;
    private static int waterFogColor;
    private static int nextWaterFogColor;
    private static long lastWaterFogColorUpdateTime;

    public static void render(Camera camera, float f, ClientWorld clientWorld, int i, float g) {
        int j;
        FluidState fluidState = camera.getSubmergedFluidState();
        if (fluidState.matches(FluidTags.WATER)) {
            long l = Util.getMeasuringTimeMs();
            j = clientWorld.method_23753(new BlockPos(camera.getPos())).getWaterFogColor();
            if (lastWaterFogColorUpdateTime < 0L) {
                waterFogColor = j;
                nextWaterFogColor = j;
                lastWaterFogColorUpdateTime = l;
            }
            int k = waterFogColor >> 16 & 0xFF;
            int m = waterFogColor >> 8 & 0xFF;
            int n = waterFogColor & 0xFF;
            int o = nextWaterFogColor >> 16 & 0xFF;
            int p = nextWaterFogColor >> 8 & 0xFF;
            int q = nextWaterFogColor & 0xFF;
            float h = MathHelper.clamp((float)(l - lastWaterFogColorUpdateTime) / 5000.0f, 0.0f, 1.0f);
            float r = MathHelper.lerp(h, o, k);
            float s = MathHelper.lerp(h, p, m);
            float t = MathHelper.lerp(h, q, n);
            red = r / 255.0f;
            green = s / 255.0f;
            blue = t / 255.0f;
            if (waterFogColor != j) {
                waterFogColor = j;
                nextWaterFogColor = MathHelper.floor(r) << 16 | MathHelper.floor(s) << 8 | MathHelper.floor(t);
                lastWaterFogColorUpdateTime = l;
            }
        } else if (fluidState.matches(FluidTags.LAVA)) {
            red = 0.6f;
            green = 0.1f;
            blue = 0.0f;
            lastWaterFogColorUpdateTime = -1L;
        } else {
            float aa;
            float z;
            float y;
            float u = 0.25f + 0.75f * (float)i / 32.0f;
            u = 1.0f - (float)Math.pow(u, 0.25);
            Vec3d vec3d = clientWorld.method_23777(camera.getBlockPos(), f);
            float v = (float)vec3d.x;
            float w = (float)vec3d.y;
            float x = (float)vec3d.z;
            Vec3d vec3d2 = clientWorld.method_23786(f);
            red = (float)vec3d2.x;
            green = (float)vec3d2.y;
            blue = (float)vec3d2.z;
            if (i >= 4) {
                float[] fs;
                y = MathHelper.sin(clientWorld.getSkyAngleRadians(f)) > 0.0f ? -1.0f : 1.0f;
                Vector3f vector3f = new Vector3f(y, 0.0f, 0.0f);
                z = camera.getHorizontalPlane().dot(vector3f);
                if (z < 0.0f) {
                    z = 0.0f;
                }
                if (z > 0.0f && (fs = clientWorld.dimension.getBackgroundColor(clientWorld.getSkyAngle(f), f)) != null) {
                    red = red * (1.0f - (z *= fs[3])) + fs[0] * z;
                    green = green * (1.0f - z) + fs[1] * z;
                    blue = blue * (1.0f - z) + fs[2] * z;
                }
            }
            red += (v - red) * u;
            green += (w - green) * u;
            blue += (x - blue) * u;
            y = clientWorld.getRainGradient(f);
            if (y > 0.0f) {
                float aa2 = 1.0f - y * 0.5f;
                z = 1.0f - y * 0.4f;
                red *= aa2;
                green *= aa2;
                blue *= z;
            }
            if ((aa = clientWorld.getThunderGradient(f)) > 0.0f) {
                z = 1.0f - aa * 0.5f;
                red *= z;
                green *= z;
                blue *= z;
            }
            lastWaterFogColorUpdateTime = -1L;
        }
        double d = camera.getPos().y * clientWorld.dimension.getHorizonShadingRatio();
        if (camera.getFocusedEntity() instanceof LivingEntity && ((LivingEntity)camera.getFocusedEntity()).hasStatusEffect(StatusEffects.BLINDNESS)) {
            j = ((LivingEntity)camera.getFocusedEntity()).getStatusEffect(StatusEffects.BLINDNESS).getDuration();
            d = j < 20 ? (d *= (double)(1.0f - (float)j / 20.0f)) : 0.0;
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
                v = clientPlayerEntity.method_3140();
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

    public static void applyFog(Camera camera, FogType fogType, float f, boolean bl) {
        boolean bl2;
        FluidState fluidState = camera.getSubmergedFluidState();
        Entity entity = camera.getFocusedEntity();
        boolean bl3 = bl2 = fluidState.getFluid() != Fluids.EMPTY;
        if (bl2) {
            float g = 1.0f;
            if (fluidState.matches(FluidTags.WATER)) {
                g = 0.05f;
                if (entity instanceof ClientPlayerEntity) {
                    ClientPlayerEntity clientPlayerEntity = (ClientPlayerEntity)entity;
                    g -= clientPlayerEntity.method_3140() * clientPlayerEntity.method_3140() * 0.03f;
                    Biome biome = clientPlayerEntity.world.method_23753(new BlockPos(clientPlayerEntity));
                    if (biome == Biomes.SWAMP || biome == Biomes.SWAMP_HILLS) {
                        g += 0.005f;
                    }
                }
            } else if (fluidState.matches(FluidTags.LAVA)) {
                g = 2.0f;
            }
            RenderSystem.fogDensity(g);
            RenderSystem.fogMode(GlStateManager.FogMode.EXP2);
        } else {
            float j;
            float g;
            if (entity instanceof LivingEntity && ((LivingEntity)entity).hasStatusEffect(StatusEffects.BLINDNESS)) {
                int i = ((LivingEntity)entity).getStatusEffect(StatusEffects.BLINDNESS).getDuration();
                float h = MathHelper.lerp(Math.min(1.0f, (float)i / 20.0f), f, 5.0f);
                if (fogType == FogType.FOG_SKY) {
                    g = 0.0f;
                    j = h * 0.8f;
                } else {
                    g = h * 0.25f;
                    j = h;
                }
            } else if (bl) {
                g = f * 0.05f;
                j = Math.min(f, 192.0f) * 0.5f;
            } else if (fogType == FogType.FOG_SKY) {
                g = 0.0f;
                j = f;
            } else {
                g = f * 0.75f;
                j = f;
            }
            RenderSystem.fogStart(g);
            RenderSystem.fogEnd(j);
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

