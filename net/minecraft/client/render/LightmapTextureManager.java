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
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

@Environment(value=EnvType.CLIENT)
public class LightmapTextureManager
implements AutoCloseable {
    private final NativeImageBackedTexture texture;
    private final NativeImage image;
    private final Identifier textureIdentifier;
    private boolean isDirty;
    private float prevFlicker;
    private float flicker;
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
                this.image.setPixelRGBA(j, i, -1);
            }
        }
        this.texture.upload();
    }

    @Override
    public void close() {
        this.texture.close();
    }

    public void tick() {
        this.flicker = (float)((double)this.flicker + (Math.random() - Math.random()) * Math.random() * Math.random());
        this.flicker = (float)((double)this.flicker * 0.9);
        this.prevFlicker += this.flicker - this.prevFlicker;
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
        this.client.getProfiler().push("lightTex");
        ClientWorld world = this.client.world;
        if (world == null) {
            return;
        }
        float g = world.getAmbientLight(1.0f);
        float h = g * 0.95f + 0.05f;
        float i = this.client.player.method_3140();
        float j = this.client.player.hasStatusEffect(StatusEffects.NIGHT_VISION) ? GameRenderer.getNightVisionStrength(this.client.player, f) : (i > 0.0f && this.client.player.hasStatusEffect(StatusEffects.CONDUIT_POWER) ? i : 0.0f);
        for (int k = 0; k < 16; ++k) {
            for (int l = 0; l < 16; ++l) {
                float x;
                float m = this.method_23284(world, k) * h;
                float n = this.method_23284(world, l) * (this.prevFlicker * 0.1f + 1.5f);
                if (world.getTicksSinceLightning() > 0) {
                    m = this.method_23284(world, k);
                }
                float o = m * (g * 0.65f + 0.35f);
                float p = m * (g * 0.65f + 0.35f);
                float q = m;
                float r = n;
                float s = n * ((n * 0.6f + 0.4f) * 0.6f + 0.4f);
                float t = n * (n * n * 0.6f + 0.4f);
                float u = o + r;
                float v = p + s;
                float w = q + t;
                u = u * 0.96f + 0.03f;
                v = v * 0.96f + 0.03f;
                w = w * 0.96f + 0.03f;
                if (this.worldRenderer.getSkyDarkness(f) > 0.0f) {
                    x = this.worldRenderer.getSkyDarkness(f);
                    u = u * (1.0f - x) + u * 0.7f * x;
                    v = v * (1.0f - x) + v * 0.6f * x;
                    w = w * (1.0f - x) + w * 0.6f * x;
                }
                if (world.dimension.getType() == DimensionType.THE_END) {
                    u = 0.22f + r * 0.75f;
                    v = 0.28f + s * 0.75f;
                    w = 0.25f + t * 0.75f;
                }
                if (j > 0.0f) {
                    x = Math.min(1.0f / u, Math.min(1.0f / v, 1.0f / w));
                    u = u * (1.0f - j) + u * x * j;
                    v = v * (1.0f - j) + v * x * j;
                    w = w * (1.0f - j) + w * x * j;
                }
                u = MathHelper.clamp(u, 0.0f, 1.0f);
                v = MathHelper.clamp(v, 0.0f, 1.0f);
                w = MathHelper.clamp(w, 0.0f, 1.0f);
                x = (float)this.client.options.gamma;
                float y = 1.0f - u;
                float z = 1.0f - v;
                float aa = 1.0f - w;
                y = 1.0f - y * y * y * y;
                z = 1.0f - z * z * z * z;
                aa = 1.0f - aa * aa * aa * aa;
                u = u * (1.0f - x) + y * x;
                v = v * (1.0f - x) + z * x;
                w = w * (1.0f - x) + aa * x;
                u = u * 0.96f + 0.03f;
                v = v * 0.96f + 0.03f;
                w = w * 0.96f + 0.03f;
                u = MathHelper.clamp(u, 0.0f, 1.0f);
                v = MathHelper.clamp(v, 0.0f, 1.0f);
                w = MathHelper.clamp(w, 0.0f, 1.0f);
                int ab = 255;
                int ac = (int)(u * 255.0f);
                int ad = (int)(v * 255.0f);
                int ae = (int)(w * 255.0f);
                this.image.setPixelRGBA(l, k, 0xFF000000 | ae << 16 | ad << 8 | ac);
            }
        }
        this.texture.upload();
        this.isDirty = false;
        this.client.getProfiler().pop();
    }

    private float method_23284(World world, int i) {
        return world.dimension.getLightLevelToBrightness()[i];
    }
}

