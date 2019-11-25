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
import net.minecraft.world.dimension.DimensionType;

@Environment(value=EnvType.CLIENT)
public class LightmapTextureManager
implements AutoCloseable {
    private final NativeImageBackedTexture texture;
    private final NativeImage image;
    private final Identifier textureIdentifier;
    private boolean isDirty;
    private float field_21528;
    private final GameRenderer worldRenderer;
    private final MinecraftClient client;

    public LightmapTextureManager(GameRenderer gameRenderer, MinecraftClient minecraftClient) {
        this.worldRenderer = gameRenderer;
        this.client = minecraftClient;
        this.texture = new NativeImageBackedTexture(16, 16, false);
        this.textureIdentifier = this.client.getTextureManager().registerDynamicTexture("light_map", this.texture);
        this.image = this.texture.getImage();
        for (int i = 0; i < 16; ++i) {
            for (int j = 0; j < 16; ++j) {
                this.image.setPixelRgba(j, i, -1);
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
        this.isDirty = true;
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

    public void update(float f) {
        if (!this.isDirty) {
            return;
        }
        this.isDirty = false;
        this.client.getProfiler().push("lightTex");
        ClientWorld clientWorld = this.client.world;
        if (clientWorld == null) {
            return;
        }
        float g = clientWorld.method_23783(1.0f);
        float h = clientWorld.getLightningTicksLeft() > 0 ? 1.0f : g * 0.95f + 0.05f;
        float i = this.client.player.method_3140();
        float j = this.client.player.hasStatusEffect(StatusEffects.NIGHT_VISION) ? GameRenderer.getNightVisionStrength(this.client.player, f) : (i > 0.0f && this.client.player.hasStatusEffect(StatusEffects.CONDUIT_POWER) ? i : 0.0f);
        Vector3f vector3f = new Vector3f(g, g, 1.0f);
        vector3f.method_23847(new Vector3f(1.0f, 1.0f, 1.0f), 0.35f);
        float k = this.field_21528 + 1.5f;
        Vector3f vector3f2 = new Vector3f();
        for (int l = 0; l < 16; ++l) {
            for (int m = 0; m < 16; ++m) {
                float t;
                Vector3f vector3f4;
                float s;
                float o;
                float n = this.getBrightness(clientWorld, l) * h;
                float p = o = this.getBrightness(clientWorld, m) * k;
                float q = o * ((o * 0.6f + 0.4f) * 0.6f + 0.4f);
                float r = o * (o * o * 0.6f + 0.4f);
                vector3f2.set(p, q, r);
                if (clientWorld.dimension.getType() == DimensionType.THE_END) {
                    vector3f2.method_23847(new Vector3f(0.99f, 1.12f, 1.0f), 0.25f);
                } else {
                    Vector3f vector3f3 = vector3f.copy();
                    vector3f3.scale(n);
                    vector3f2.add(vector3f3);
                    vector3f2.method_23847(new Vector3f(0.75f, 0.75f, 0.75f), 0.04f);
                    if (this.worldRenderer.getSkyDarkness(f) > 0.0f) {
                        s = this.worldRenderer.getSkyDarkness(f);
                        vector3f4 = vector3f2.copy();
                        vector3f4.piecewiseMultiply(0.7f, 0.6f, 0.6f);
                        vector3f2.method_23847(vector3f4, s);
                    }
                }
                vector3f2.clamp(0.0f, 1.0f);
                if (j > 0.0f && (t = Math.max(vector3f2.getX(), Math.max(vector3f2.getY(), vector3f2.getZ()))) < 1.0f) {
                    s = 1.0f / t;
                    vector3f4 = vector3f2.copy();
                    vector3f4.scale(s);
                    vector3f2.method_23847(vector3f4, j);
                }
                t = (float)this.client.options.gamma;
                Vector3f vector3f5 = vector3f2.copy();
                vector3f5.method_23848(this::method_23795);
                vector3f2.method_23847(vector3f5, t);
                vector3f2.method_23847(new Vector3f(0.75f, 0.75f, 0.75f), 0.04f);
                vector3f2.clamp(0.0f, 1.0f);
                vector3f2.scale(255.0f);
                int u = 255;
                int v = (int)vector3f2.getX();
                int w = (int)vector3f2.getY();
                int x = (int)vector3f2.getZ();
                this.image.setPixelRgba(m, l, 0xFF000000 | x << 16 | w << 8 | v);
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
        return world.dimension.getBrightness(i);
    }

    public static int pack(int i, int j) {
        return i << 4 | j << 20;
    }

    public static int getBlockLightCoordinates(int i) {
        return i >> 4 & 0xFFFF;
    }

    public static int getSkyLightCoordinates(int i) {
        return i >> 20 & 0xFFFF;
    }
}

