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
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

@Environment(value=EnvType.CLIENT)
public class LightmapTextureManager
implements AutoCloseable {
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
        RenderSystem.activeTexture(33986);
        RenderSystem.disableTexture();
        RenderSystem.activeTexture(33984);
    }

    public void enable() {
        RenderSystem.activeTexture(33986);
        RenderSystem.matrixMode(5890);
        RenderSystem.loadIdentity();
        float f = 0.00390625f;
        RenderSystem.scalef(0.00390625f, 0.00390625f, 0.00390625f);
        RenderSystem.translatef(8.0f, 8.0f, 8.0f);
        RenderSystem.matrixMode(5888);
        this.client.getTextureManager().bindTexture(this.textureIdentifier);
        RenderSystem.texParameter(3553, 10241, 9729);
        RenderSystem.texParameter(3553, 10240, 9729);
        RenderSystem.texParameter(3553, 10242, 10496);
        RenderSystem.texParameter(3553, 10243, 10496);
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.enableTexture();
        RenderSystem.activeTexture(33984);
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
        Vector3f vector3f = new Vector3f(f, f, 1.0f);
        vector3f.lerp(new Vector3f(1.0f, 1.0f, 1.0f), 0.35f);
        float j = this.field_21528 + 1.5f;
        Vector3f vector3f2 = new Vector3f();
        for (int k = 0; k < 16; ++k) {
            for (int l = 0; l < 16; ++l) {
                float s;
                Vector3f vector3f4;
                float r;
                float n;
                float m = this.getBrightness(clientWorld, k) * g;
                float o = n = this.getBrightness(clientWorld, l) * j;
                float p = n * ((n * 0.6f + 0.4f) * 0.6f + 0.4f);
                float q = n * (n * n * 0.6f + 0.4f);
                vector3f2.set(o, p, q);
                if (clientWorld.getSkyProperties().shouldRenderSky()) {
                    vector3f2.lerp(new Vector3f(0.99f, 1.12f, 1.0f), 0.25f);
                } else {
                    Vector3f vector3f3 = vector3f.copy();
                    vector3f3.scale(m);
                    vector3f2.add(vector3f3);
                    vector3f2.lerp(new Vector3f(0.75f, 0.75f, 0.75f), 0.04f);
                    if (this.renderer.getSkyDarkness(delta) > 0.0f) {
                        r = this.renderer.getSkyDarkness(delta);
                        vector3f4 = vector3f2.copy();
                        vector3f4.multiplyComponentwise(0.7f, 0.6f, 0.6f);
                        vector3f2.lerp(vector3f4, r);
                    }
                }
                vector3f2.clamp(0.0f, 1.0f);
                if (i > 0.0f && (s = Math.max(vector3f2.getX(), Math.max(vector3f2.getY(), vector3f2.getZ()))) < 1.0f) {
                    r = 1.0f / s;
                    vector3f4 = vector3f2.copy();
                    vector3f4.scale(r);
                    vector3f2.lerp(vector3f4, i);
                }
                s = (float)this.client.options.gamma;
                Vector3f vector3f5 = vector3f2.copy();
                vector3f5.modify(this::method_23795);
                vector3f2.lerp(vector3f5, s);
                vector3f2.lerp(new Vector3f(0.75f, 0.75f, 0.75f), 0.04f);
                vector3f2.clamp(0.0f, 1.0f);
                vector3f2.scale(255.0f);
                int t = 255;
                int u = (int)vector3f2.getX();
                int v = (int)vector3f2.getY();
                int w = (int)vector3f2.getZ();
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
        return world.getDimension().method_28516(i);
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

