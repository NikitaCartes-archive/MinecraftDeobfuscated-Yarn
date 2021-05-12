/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3f;
import net.minecraft.world.World;

@Environment(value=EnvType.CLIENT)
public class LightmapTextureManager
implements AutoCloseable {
    public static final int field_32767 = 0xF000F0;
    public static final int field_32768 = 0xF00000;
    public static final int field_32769 = 240;
    private final NativeImageBackedTexture texture;
    private final NativeImage image;
    private final Identifier textureIdentifier;
    private boolean dirty;
    private float field_21528;
    private final GameRenderer renderer;
    private final MinecraftClient client;

    public LightmapTextureManager(GameRenderer renderer, MinecraftClient client) {
        this.renderer = renderer;
        this.client = client;
        this.texture = new NativeImageBackedTexture(16, 16, false);
        this.textureIdentifier = this.client.getTextureManager().registerDynamicTexture("light_map", this.texture);
        this.image = this.texture.getImage();
        for (int i = 0; i < 16; ++i) {
            for (int j = 0; j < 16; ++j) {
                this.image.setPixelColor(j, i, -1);
            }
        }
        this.texture.upload();
    }

    @Override
    public void close() {
        this.texture.close();
    }

    public void tick() {
        this.field_21528 = (float)((double)this.field_21528 + (Math.random() - Math.random()) * Math.random() * Math.random() * 0.1);
        this.field_21528 = (float)((double)this.field_21528 * 0.9);
        this.dirty = true;
    }

    public void disable() {
        RenderSystem.setShaderTexture(2, 0);
    }

    public void enable() {
        RenderSystem.setShaderTexture(2, this.textureIdentifier);
        this.client.getTextureManager().bindTexture(this.textureIdentifier);
        RenderSystem.texParameter(3553, 10241, 9729);
        RenderSystem.texParameter(3553, 10240, 9729);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
    }

    public void update(float delta) {
        if (!this.dirty) {
            return;
        }
        this.dirty = false;
        this.client.getProfiler().push("lightTex");
        ClientWorld clientWorld = this.client.world;
        if (clientWorld == null) {
            return;
        }
        float f = clientWorld.method_23783(1.0f);
        float g = clientWorld.getLightningTicksLeft() > 0 ? 1.0f : f * 0.95f + 0.05f;
        float h = this.client.player.getUnderwaterVisibility();
        float i = this.client.player.hasStatusEffect(StatusEffects.NIGHT_VISION) ? GameRenderer.getNightVisionStrength(this.client.player, delta) : (h > 0.0f && this.client.player.hasStatusEffect(StatusEffects.CONDUIT_POWER) ? h : 0.0f);
        Vec3f vec3f = new Vec3f(f, f, 1.0f);
        vec3f.lerp(new Vec3f(1.0f, 1.0f, 1.0f), 0.35f);
        float j = this.field_21528 + 1.5f;
        Vec3f vec3f2 = new Vec3f();
        for (int k = 0; k < 16; ++k) {
            for (int l = 0; l < 16; ++l) {
                float s;
                Vec3f vec3f4;
                float r;
                float n;
                float m = this.getBrightness(clientWorld, k) * g;
                float o = n = this.getBrightness(clientWorld, l) * j;
                float p = n * ((n * 0.6f + 0.4f) * 0.6f + 0.4f);
                float q = n * (n * n * 0.6f + 0.4f);
                vec3f2.set(o, p, q);
                if (clientWorld.getSkyProperties().shouldBrightenLighting()) {
                    vec3f2.lerp(new Vec3f(0.99f, 1.12f, 1.0f), 0.25f);
                } else {
                    Vec3f vec3f3 = vec3f.copy();
                    vec3f3.scale(m);
                    vec3f2.add(vec3f3);
                    vec3f2.lerp(new Vec3f(0.75f, 0.75f, 0.75f), 0.04f);
                    if (this.renderer.getSkyDarkness(delta) > 0.0f) {
                        r = this.renderer.getSkyDarkness(delta);
                        vec3f4 = vec3f2.copy();
                        vec3f4.multiplyComponentwise(0.7f, 0.6f, 0.6f);
                        vec3f2.lerp(vec3f4, r);
                    }
                }
                vec3f2.clamp(0.0f, 1.0f);
                if (i > 0.0f && (s = Math.max(vec3f2.getX(), Math.max(vec3f2.getY(), vec3f2.getZ()))) < 1.0f) {
                    r = 1.0f / s;
                    vec3f4 = vec3f2.copy();
                    vec3f4.scale(r);
                    vec3f2.lerp(vec3f4, i);
                }
                float s2 = (float)this.client.options.gamma;
                Vec3f vec3f5 = vec3f2.copy();
                vec3f5.modify(this::method_23795);
                vec3f2.lerp(vec3f5, s2);
                vec3f2.lerp(new Vec3f(0.75f, 0.75f, 0.75f), 0.04f);
                vec3f2.clamp(0.0f, 1.0f);
                vec3f2.scale(255.0f);
                int t = 255;
                int u = (int)vec3f2.getX();
                int v = (int)vec3f2.getY();
                int w = (int)vec3f2.getZ();
                this.image.setPixelColor(l, k, 0xFF000000 | w << 16 | v << 8 | u);
            }
        }
        this.texture.upload();
        this.client.getProfiler().pop();
    }

    private float method_23795(float f) {
        float g = 1.0f - f;
        return 1.0f - g * g * g * g;
    }

    private float getBrightness(World world, int i) {
        return world.getDimension().getBrightness(i);
    }

    public static int pack(int block, int sky) {
        return block << 4 | sky << 20;
    }

    public static int getBlockLightCoordinates(int light) {
        return light >> 4 & 0xFFFF;
    }

    public static int getSkyLightCoordinates(int light) {
        return light >> 20 & 0xFFFF;
    }
}

