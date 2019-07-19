package net.minecraft.client.render;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

@Environment(EnvType.CLIENT)
public class LightmapTextureManager implements AutoCloseable {
	private final NativeImageBackedTexture texture;
	private final NativeImage image;
	private final Identifier textureIdentifier;
	private boolean isDirty;
	private float prevFlicker;
	private float flicker;
	private final GameRenderer worldRenderer;
	private final MinecraftClient client;

	public LightmapTextureManager(GameRenderer worldRenderer) {
		this.worldRenderer = worldRenderer;
		this.client = worldRenderer.getClient();
		this.texture = new NativeImageBackedTexture(16, 16, false);
		this.textureIdentifier = this.client.getTextureManager().registerDynamicTexture("light_map", this.texture);
		this.image = this.texture.getImage();
	}

	public void close() {
		this.texture.close();
	}

	public void tick() {
		this.flicker = (float)((double)this.flicker + (Math.random() - Math.random()) * Math.random() * Math.random());
		this.flicker = (float)((double)this.flicker * 0.9);
		this.prevFlicker = this.prevFlicker + (this.flicker - this.prevFlicker);
		this.isDirty = true;
	}

	public void disable() {
		GlStateManager.activeTexture(GLX.GL_TEXTURE1);
		GlStateManager.disableTexture();
		GlStateManager.activeTexture(GLX.GL_TEXTURE0);
	}

	public void enable() {
		GlStateManager.activeTexture(GLX.GL_TEXTURE1);
		GlStateManager.matrixMode(5890);
		GlStateManager.loadIdentity();
		float f = 0.00390625F;
		GlStateManager.scalef(0.00390625F, 0.00390625F, 0.00390625F);
		GlStateManager.translatef(8.0F, 8.0F, 8.0F);
		GlStateManager.matrixMode(5888);
		this.client.getTextureManager().bindTexture(this.textureIdentifier);
		GlStateManager.texParameter(3553, 10241, 9729);
		GlStateManager.texParameter(3553, 10240, 9729);
		GlStateManager.texParameter(3553, 10242, 10496);
		GlStateManager.texParameter(3553, 10243, 10496);
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.enableTexture();
		GlStateManager.activeTexture(GLX.GL_TEXTURE0);
	}

	public void update(float delta) {
		if (this.isDirty) {
			this.client.getProfiler().push("lightTex");
			World world = this.client.world;
			if (world != null) {
				float f = world.getAmbientLight(1.0F);
				float g = f * 0.95F + 0.05F;
				float h = this.client.player.method_3140();
				float i;
				if (this.client.player.hasStatusEffect(StatusEffects.NIGHT_VISION)) {
					i = this.worldRenderer.getNightVisionStrength(this.client.player, delta);
				} else if (h > 0.0F && this.client.player.hasStatusEffect(StatusEffects.CONDUIT_POWER)) {
					i = h;
				} else {
					i = 0.0F;
				}

				for (int j = 0; j < 16; j++) {
					for (int k = 0; k < 16; k++) {
						float l = world.dimension.getLightLevelToBrightness()[j] * g;
						float m = world.dimension.getLightLevelToBrightness()[k] * (this.prevFlicker * 0.1F + 1.5F);
						if (world.getTicksSinceLightning() > 0) {
							l = world.dimension.getLightLevelToBrightness()[j];
						}

						float n = l * (f * 0.65F + 0.35F);
						float o = l * (f * 0.65F + 0.35F);
						float r = m * ((m * 0.6F + 0.4F) * 0.6F + 0.4F);
						float s = m * (m * m * 0.6F + 0.4F);
						float t = n + m;
						float u = o + r;
						float v = l + s;
						t = t * 0.96F + 0.03F;
						u = u * 0.96F + 0.03F;
						v = v * 0.96F + 0.03F;
						if (this.worldRenderer.getSkyDarkness(delta) > 0.0F) {
							float w = this.worldRenderer.getSkyDarkness(delta);
							t = t * (1.0F - w) + t * 0.7F * w;
							u = u * (1.0F - w) + u * 0.6F * w;
							v = v * (1.0F - w) + v * 0.6F * w;
						}

						if (world.dimension.getType() == DimensionType.THE_END) {
							t = 0.22F + m * 0.75F;
							u = 0.28F + r * 0.75F;
							v = 0.25F + s * 0.75F;
						}

						if (i > 0.0F) {
							float w = 1.0F / t;
							if (w > 1.0F / u) {
								w = 1.0F / u;
							}

							if (w > 1.0F / v) {
								w = 1.0F / v;
							}

							t = t * (1.0F - i) + t * w * i;
							u = u * (1.0F - i) + u * w * i;
							v = v * (1.0F - i) + v * w * i;
						}

						if (t > 1.0F) {
							t = 1.0F;
						}

						if (u > 1.0F) {
							u = 1.0F;
						}

						if (v > 1.0F) {
							v = 1.0F;
						}

						float wx = (float)this.client.options.gamma;
						float x = 1.0F - t;
						float y = 1.0F - u;
						float z = 1.0F - v;
						x = 1.0F - x * x * x * x;
						y = 1.0F - y * y * y * y;
						z = 1.0F - z * z * z * z;
						t = t * (1.0F - wx) + x * wx;
						u = u * (1.0F - wx) + y * wx;
						v = v * (1.0F - wx) + z * wx;
						t = t * 0.96F + 0.03F;
						u = u * 0.96F + 0.03F;
						v = v * 0.96F + 0.03F;
						if (t > 1.0F) {
							t = 1.0F;
						}

						if (u > 1.0F) {
							u = 1.0F;
						}

						if (v > 1.0F) {
							v = 1.0F;
						}

						if (t < 0.0F) {
							t = 0.0F;
						}

						if (u < 0.0F) {
							u = 0.0F;
						}

						if (v < 0.0F) {
							v = 0.0F;
						}

						int aa = 255;
						int ab = (int)(t * 255.0F);
						int ac = (int)(u * 255.0F);
						int ad = (int)(v * 255.0F);
						this.image.setPixelRgba(k, j, 0xFF000000 | ad << 16 | ac << 8 | ab);
					}
				}

				this.texture.upload();
				this.isDirty = false;
				this.client.getProfiler().pop();
			}
		}
	}
}
