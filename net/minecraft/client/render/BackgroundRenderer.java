/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.nio.FloatBuffer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
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

@Environment(value=EnvType.CLIENT)
public class BackgroundRenderer {
    private static final FloatBuffer blackColorBuffer = Util.create(GlAllocationUtils.allocateFloatBuffer(16), floatBuffer -> floatBuffer.put(0.0f).put(0.0f).put(0.0f).put(1.0f).flip());
    private static final FloatBuffer colorBuffer = GlAllocationUtils.allocateFloatBuffer(16);
    private float red;
    private float green;
    private float blue;
    private int waterFogColor = -1;
    private int nextWaterFogColor = -1;
    private long lastWaterFogColorUpdateTime = -1L;

    public void render(Camera camera, float f, World world, int i, float g) {
        float w;
        float v;
        float u;
        int j;
        FluidState fluidState = camera.getSubmergedFluidState();
        if (fluidState.matches(FluidTags.WATER)) {
            long l = Util.getMeasuringTimeMs();
            j = world.getBiome(new BlockPos(camera.getPos())).getWaterFogColor();
            if (this.lastWaterFogColorUpdateTime < 0L) {
                this.waterFogColor = j;
                this.nextWaterFogColor = j;
                this.lastWaterFogColorUpdateTime = l;
            }
            int k = this.waterFogColor >> 16 & 0xFF;
            int m = this.waterFogColor >> 8 & 0xFF;
            int n = this.waterFogColor & 0xFF;
            int o = this.nextWaterFogColor >> 16 & 0xFF;
            int p = this.nextWaterFogColor >> 8 & 0xFF;
            int q = this.nextWaterFogColor & 0xFF;
            float h = MathHelper.clamp((float)(l - this.lastWaterFogColorUpdateTime) / 5000.0f, 0.0f, 1.0f);
            float r = MathHelper.lerp(h, o, k);
            float s = MathHelper.lerp(h, p, m);
            float t = MathHelper.lerp(h, q, n);
            u = r / 255.0f;
            v = s / 255.0f;
            w = t / 255.0f;
            if (this.waterFogColor != j) {
                this.waterFogColor = j;
                this.nextWaterFogColor = MathHelper.floor(r) << 16 | MathHelper.floor(s) << 8 | MathHelper.floor(t);
                this.lastWaterFogColorUpdateTime = l;
            }
        } else if (fluidState.matches(FluidTags.LAVA)) {
            u = 0.6f;
            v = 0.1f;
            w = 0.0f;
            this.lastWaterFogColorUpdateTime = -1L;
        } else {
            float ac;
            float x = 0.25f + 0.75f * (float)i / 32.0f;
            x = 1.0f - (float)Math.pow(x, 0.25);
            Vec3d vec3d = world.getSkyColor(camera.getBlockPos(), f);
            float y = (float)vec3d.x;
            float z = (float)vec3d.y;
            float aa = (float)vec3d.z;
            Vec3d vec3d2 = world.getFogColor(f);
            u = (float)vec3d2.x;
            v = (float)vec3d2.y;
            w = (float)vec3d2.z;
            if (i >= 4) {
                float[] fs;
                double d = MathHelper.sin(world.getSkyAngleRadians(f)) > 0.0f ? -1.0 : 1.0;
                Vec3d vec3d3 = new Vec3d(d, 0.0, 0.0);
                float h = (float)camera.getHorizontalPlane().dotProduct(vec3d3);
                if (h < 0.0f) {
                    h = 0.0f;
                }
                if (h > 0.0f && (fs = world.dimension.getBackgroundColor(world.getSkyAngle(f), f)) != null) {
                    u = u * (1.0f - (h *= fs[3])) + fs[0] * h;
                    v = v * (1.0f - h) + fs[1] * h;
                    w = w * (1.0f - h) + fs[2] * h;
                }
            }
            u += (y - u) * x;
            v += (z - v) * x;
            w += (aa - w) * x;
            float ab = world.getRainGradient(f);
            if (ab > 0.0f) {
                ac = 1.0f - ab * 0.5f;
                float ad = 1.0f - ab * 0.4f;
                u *= ac;
                v *= ac;
                w *= ad;
            }
            if ((ac = world.getThunderGradient(f)) > 0.0f) {
                float ad = 1.0f - ac * 0.5f;
                u *= ad;
                v *= ad;
                w *= ad;
            }
            this.lastWaterFogColorUpdateTime = -1L;
        }
        double e = camera.getPos().y * world.dimension.getHorizonShadingRatio();
        if (camera.getFocusedEntity() instanceof LivingEntity && ((LivingEntity)camera.getFocusedEntity()).hasStatusEffect(StatusEffects.BLINDNESS)) {
            j = ((LivingEntity)camera.getFocusedEntity()).getStatusEffect(StatusEffects.BLINDNESS).getDuration();
            e = j < 20 ? (e *= (double)(1.0f - (float)j / 20.0f)) : 0.0;
        }
        if (e < 1.0) {
            if (e < 0.0) {
                e = 0.0;
            }
            e *= e;
            u = (float)((double)u * e);
            v = (float)((double)v * e);
            w = (float)((double)w * e);
        }
        if (g > 0.0f) {
            u = u * (1.0f - g) + u * 0.7f * g;
            v = v * (1.0f - g) + v * 0.6f * g;
            w = w * (1.0f - g) + w * 0.6f * g;
        }
        if (fluidState.matches(FluidTags.WATER)) {
            float y = 0.0f;
            if (camera.getFocusedEntity() instanceof ClientPlayerEntity) {
                ClientPlayerEntity clientPlayerEntity = (ClientPlayerEntity)camera.getFocusedEntity();
                y = clientPlayerEntity.method_3140();
            }
            float z = Math.min(1.0f / u, Math.min(1.0f / v, 1.0f / w));
            u = u * (1.0f - y) + u * z * y;
            v = v * (1.0f - y) + v * z * y;
            w = w * (1.0f - y) + w * z * y;
        } else if (camera.getFocusedEntity() instanceof LivingEntity && ((LivingEntity)camera.getFocusedEntity()).hasStatusEffect(StatusEffects.NIGHT_VISION)) {
            float y = GameRenderer.getNightVisionStrength((LivingEntity)camera.getFocusedEntity(), f);
            float z = Math.min(1.0f / u, Math.min(1.0f / v, 1.0f / w));
            u = u * (1.0f - y) + u * z * y;
            v = v * (1.0f - y) + v * z * y;
            w = w * (1.0f - y) + w * z * y;
        }
        RenderSystem.clearColor(u, v, w, 0.0f);
        if (this.red != u || this.green != v || this.blue != w) {
            colorBuffer.clear();
            colorBuffer.put(u).put(v).put(w).put(1.0f);
            colorBuffer.flip();
            this.red = u;
            this.green = v;
            this.blue = w;
        }
    }

    public static void applyFog(Camera camera, FogType fogType, float f, boolean bl) {
        BackgroundRenderer.setFogBlack(false);
        RenderSystem.normal3f(0.0f, -1.0f, 0.0f);
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        FluidState fluidState = camera.getSubmergedFluidState();
        if (camera.getFocusedEntity() instanceof LivingEntity && ((LivingEntity)camera.getFocusedEntity()).hasStatusEffect(StatusEffects.BLINDNESS)) {
            float g = 5.0f;
            int i = ((LivingEntity)camera.getFocusedEntity()).getStatusEffect(StatusEffects.BLINDNESS).getDuration();
            if (i < 20) {
                g = MathHelper.lerp(1.0f - (float)i / 20.0f, 5.0f, f);
            }
            RenderSystem.fogMode(GlStateManager.FogMode.LINEAR);
            if (fogType == FogType.FOG_SKY) {
                RenderSystem.fogStart(0.0f);
                RenderSystem.fogEnd(g * 0.8f);
            } else {
                RenderSystem.fogStart(g * 0.25f);
                RenderSystem.fogEnd(g);
            }
            RenderSystem.setupNvFogDistance();
        } else if (fluidState.matches(FluidTags.WATER)) {
            RenderSystem.fogMode(GlStateManager.FogMode.EXP2);
            if (camera.getFocusedEntity() instanceof LivingEntity) {
                if (camera.getFocusedEntity() instanceof ClientPlayerEntity) {
                    ClientPlayerEntity clientPlayerEntity = (ClientPlayerEntity)camera.getFocusedEntity();
                    float h = 0.05f - clientPlayerEntity.method_3140() * clientPlayerEntity.method_3140() * 0.03f;
                    Biome biome = clientPlayerEntity.world.getBiome(new BlockPos(clientPlayerEntity));
                    if (biome == Biomes.SWAMP || biome == Biomes.SWAMP_HILLS) {
                        h += 0.005f;
                    }
                    RenderSystem.fogDensity(h);
                } else {
                    RenderSystem.fogDensity(0.05f);
                }
            } else {
                RenderSystem.fogDensity(0.1f);
            }
        } else if (fluidState.matches(FluidTags.LAVA)) {
            RenderSystem.fogMode(GlStateManager.FogMode.EXP);
            RenderSystem.fogDensity(2.0f);
        } else {
            RenderSystem.fogMode(GlStateManager.FogMode.LINEAR);
            if (fogType == FogType.FOG_SKY) {
                RenderSystem.fogStart(0.0f);
                RenderSystem.fogEnd(f);
            } else {
                RenderSystem.fogStart(f * 0.75f);
                RenderSystem.fogEnd(f);
            }
            RenderSystem.setupNvFogDistance();
            if (bl) {
                RenderSystem.fogStart(f * 0.05f);
                RenderSystem.fogEnd(Math.min(f, 192.0f) * 0.5f);
            }
        }
        RenderSystem.enableFog();
    }

    public static void setFogBlack(boolean bl) {
        RenderSystem.fog(2918, bl ? blackColorBuffer : colorBuffer);
    }

    @Environment(value=EnvType.CLIENT)
    public static enum FogType {
        FOG_SKY,
        FOG_TERRAIN;

    }
}

