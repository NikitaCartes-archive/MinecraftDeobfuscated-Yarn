/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render;

import com.mojang.blaze3d.platform.GlConst;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.dimension.DimensionType;
import org.joml.Vector3f;

/**
 * The lightmap texture manager maintains a texture containing the RGBA overlay for each of the 16&times;16 sky and block light combinations.
 * <p>
 * Also contains some utilities to pack and unpack lightmap coordinates from sky and block light values,
 * and some lightmap coordinates constants.
 */
@Environment(value=EnvType.CLIENT)
public class LightmapTextureManager
implements AutoCloseable {
    /**
     * Represents the maximum lightmap coordinate, where both sky light and block light equals {@code 15}.
     * The value of this maximum lightmap coordinate is {@value}.
     */
    public static final int MAX_LIGHT_COORDINATE = 0xF000F0;
    /**
     * Represents the maximum sky-light-wise lightmap coordinate whose value is {@value}.
     * This is equivalent to a {@code 15} sky light and {@code 0} block light.
     */
    public static final int MAX_SKY_LIGHT_COORDINATE = 0xF00000;
    /**
     * Represents the maximum block-light-wise lightmap coordinate whose value is {@value}.
     * This is equivalent to a {@code 0} sky light and {@code 15} block light.
     */
    public static final int MAX_BLOCK_LIGHT_COORDINATE = 240;
    private final NativeImageBackedTexture texture;
    private final NativeImage image;
    private final Identifier textureIdentifier;
    private boolean dirty;
    private float flickerIntensity;
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
                this.image.setColor(j, i, -1);
            }
        }
        this.texture.upload();
    }

    @Override
    public void close() {
        this.texture.close();
    }

    public void tick() {
        this.flickerIntensity += (float)((Math.random() - Math.random()) * Math.random() * Math.random() * 0.1);
        this.flickerIntensity *= 0.9f;
        this.dirty = true;
    }

    public void disable() {
        RenderSystem.setShaderTexture(2, 0);
    }

    public void enable() {
        RenderSystem.setShaderTexture(2, this.textureIdentifier);
        this.client.getTextureManager().bindTexture(this.textureIdentifier);
        RenderSystem.texParameter(GlConst.GL_TEXTURE_2D, GlConst.GL_TEXTURE_MIN_FILTER, GlConst.GL_LINEAR);
        RenderSystem.texParameter(GlConst.GL_TEXTURE_2D, GlConst.GL_TEXTURE_MAG_FILTER, GlConst.GL_LINEAR);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
    }

    private float getDarknessFactor(float delta) {
        StatusEffectInstance statusEffectInstance;
        if (this.client.player.hasStatusEffect(StatusEffects.DARKNESS) && (statusEffectInstance = this.client.player.getStatusEffect(StatusEffects.DARKNESS)) != null && statusEffectInstance.getFactorCalculationData().isPresent()) {
            return statusEffectInstance.getFactorCalculationData().get().lerp(this.client.player, delta);
        }
        return 0.0f;
    }

    private float getDarkness(LivingEntity entity, float factor, float delta) {
        float f = 0.45f * factor;
        return Math.max(0.0f, MathHelper.cos(((float)entity.age - delta) * (float)Math.PI * 0.025f) * f);
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
        float f = clientWorld.getStarBrightness(1.0f);
        float g = clientWorld.getLightningTicksLeft() > 0 ? 1.0f : f * 0.95f + 0.05f;
        float h = this.client.options.getDarknessEffectScale().getValue().floatValue();
        float i = this.getDarknessFactor(delta) * h;
        float j = this.getDarkness(this.client.player, i, delta) * h;
        float k = this.client.player.getUnderwaterVisibility();
        float l = this.client.player.hasStatusEffect(StatusEffects.NIGHT_VISION) ? GameRenderer.getNightVisionStrength(this.client.player, delta) : (k > 0.0f && this.client.player.hasStatusEffect(StatusEffects.CONDUIT_POWER) ? k : 0.0f);
        Vector3f vector3f = new Vector3f(f, f, 1.0f).lerp(new Vector3f(1.0f, 1.0f, 1.0f), 0.35f);
        float m = this.flickerIntensity + 1.5f;
        Vector3f vector3f2 = new Vector3f();
        for (int n = 0; n < 16; ++n) {
            for (int o = 0; o < 16; ++o) {
                float v;
                Vector3f vector3f4;
                float u;
                float q;
                float p = LightmapTextureManager.getBrightness(clientWorld.getDimension(), n) * g;
                float r = q = LightmapTextureManager.getBrightness(clientWorld.getDimension(), o) * m;
                float s = q * ((q * 0.6f + 0.4f) * 0.6f + 0.4f);
                float t = q * (q * q * 0.6f + 0.4f);
                vector3f2.set(r, s, t);
                boolean bl = clientWorld.getDimensionEffects().shouldBrightenLighting();
                if (bl) {
                    vector3f2.lerp(new Vector3f(0.99f, 1.12f, 1.0f), 0.25f);
                    LightmapTextureManager.clamp(vector3f2);
                } else {
                    Vector3f vector3f3 = new Vector3f(vector3f).mul(p);
                    vector3f2.add(vector3f3);
                    vector3f2.lerp(new Vector3f(0.75f, 0.75f, 0.75f), 0.04f);
                    if (this.renderer.getSkyDarkness(delta) > 0.0f) {
                        u = this.renderer.getSkyDarkness(delta);
                        vector3f4 = new Vector3f(vector3f2).mul(0.7f, 0.6f, 0.6f);
                        vector3f2.lerp(vector3f4, u);
                    }
                }
                if (l > 0.0f && (v = Math.max(vector3f2.x(), Math.max(vector3f2.y(), vector3f2.z()))) < 1.0f) {
                    u = 1.0f / v;
                    vector3f4 = new Vector3f(vector3f2).mul(u);
                    vector3f2.lerp(vector3f4, l);
                }
                if (!bl) {
                    if (j > 0.0f) {
                        vector3f2.add(-j, -j, -j);
                    }
                    LightmapTextureManager.clamp(vector3f2);
                }
                float v2 = this.client.options.getGamma().getValue().floatValue();
                Vector3f vector3f5 = new Vector3f(this.easeOutQuart(vector3f2.x), this.easeOutQuart(vector3f2.y), this.easeOutQuart(vector3f2.z));
                vector3f2.lerp(vector3f5, Math.max(0.0f, v2 - i));
                vector3f2.lerp(new Vector3f(0.75f, 0.75f, 0.75f), 0.04f);
                LightmapTextureManager.clamp(vector3f2);
                vector3f2.mul(255.0f);
                int w = 255;
                int x = (int)vector3f2.x();
                int y = (int)vector3f2.y();
                int z = (int)vector3f2.z();
                this.image.setColor(o, n, 0xFF000000 | z << 16 | y << 8 | x);
            }
        }
        this.texture.upload();
        this.client.getProfiler().pop();
    }

    /**
     * Clamps each component of {@code vec} between {@code 0.0f} and {@code 1.0f}.
     */
    private static void clamp(Vector3f vec) {
        vec.set(MathHelper.clamp(vec.x, 0.0f, 1.0f), MathHelper.clamp(vec.y, 0.0f, 1.0f), MathHelper.clamp(vec.z, 0.0f, 1.0f));
    }

    /**
     * Represents an easing function.
     * <p>
     * In this class, it's also used to brighten colors,
     * then the result is used to lerp between the normal and brightened color
     * with the gamma value.
     * 
     * @see <a href="https://easings.net/#easeOutQuart">https://easings.net/#easeOutQuart</a>
     * 
     * @param x represents the absolute progress of the animation in the bounds of 0 (beginning of the animation) and 1 (end of animation)
     */
    private float easeOutQuart(float x) {
        float f = 1.0f - x;
        return 1.0f - f * f * f * f;
    }

    public static float getBrightness(DimensionType type, int lightLevel) {
        float f = (float)lightLevel / 15.0f;
        float g = f / (4.0f - 3.0f * f);
        return MathHelper.lerp(type.ambientLight(), g, 1.0f);
    }

    public static int pack(int block, int sky) {
        return block << 4 | sky << 20;
    }

    public static int getBlockLightCoordinates(int light) {
        return light >> 4 & (MAX_BLOCK_LIGHT_COORDINATE | 0xFF0F);
    }

    public static int getSkyLightCoordinates(int light) {
        return light >> 20 & (MAX_BLOCK_LIGHT_COORDINATE | 0xFF0F);
    }
}

