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

	public LightmapTextureManager(GameRenderer gameRenderer) {
		this.worldRenderer = gameRenderer;
		this.client = gameRenderer.getClient();
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

	public void update(float f) {
		if (this.isDirty) {
			this.client.getProfiler().push("lightTex");
			World world = this.client.world;
			if (world != null) {
				float g = world.getAmbientLight(1.0F);
				float h = g * 0.95F + 0.05F;
				float i = this.client.player.method_3140();
				float j;
				if (this.client.player.hasStatusEffect(StatusEffects.field_5925)) {
					j = this.worldRenderer.getNightVisionStrength(this.client.player, f);
				} else if (i > 0.0F && this.client.player.hasStatusEffect(StatusEffects.field_5927)) {
					j = i;
				} else {
					j = 0.0F;
				}

				for (int k = 0; k < 16; k++) {
					for (int l = 0; l < 16; l++) {
						float m = world.dimension.getLightLevelToBrightness()[k] * h;
						float n = world.dimension.getLightLevelToBrightness()[l] * (this.prevFlicker * 0.1F + 1.5F);
						if (world.getTicksSinceLightning() > 0) {
							m = world.dimension.getLightLevelToBrightness()[k];
						}

						float o = m * (g * 0.65F + 0.35F);
						float p = m * (g * 0.65F + 0.35F);
						float s = n * ((n * 0.6F + 0.4F) * 0.6F + 0.4F);
						float t = n * (n * n * 0.6F + 0.4F);
						float u = o + n;
						float v = p + s;
						float w = m + t;
						u = u * 0.96F + 0.03F;
						v = v * 0.96F + 0.03F;
						w = w * 0.96F + 0.03F;
						if (this.worldRenderer.getSkyDarkness(f) > 0.0F) {
							float x = this.worldRenderer.getSkyDarkness(f);
							u = u * (1.0F - x) + u * 0.7F * x;
							v = v * (1.0F - x) + v * 0.6F * x;
							w = w * (1.0F - x) + w * 0.6F * x;
						}

						if (world.dimension.getType() == DimensionType.field_13078) {
							u = 0.22F + n * 0.75F;
							v = 0.28F + s * 0.75F;
							w = 0.25F + t * 0.75F;
						}

						if (j > 0.0F) {
							float x = 1.0F / u;
							if (x > 1.0F / v) {
								x = 1.0F / v;
							}

							if (x > 1.0F / w) {
								x = 1.0F / w;
							}

							u = u * (1.0F - j) + u * x * j;
							v = v * (1.0F - j) + v * x * j;
							w = w * (1.0F - j) + w * x * j;
						}

						if (u > 1.0F) {
							u = 1.0F;
						}

						if (v > 1.0F) {
							v = 1.0F;
						}

						if (w > 1.0F) {
							w = 1.0F;
						}

						float xx = (float)this.client.options.gamma;
						float y = 1.0F - u;
						float z = 1.0F - v;
						float aa = 1.0F - w;
						y = 1.0F - y * y * y * y;
						z = 1.0F - z * z * z * z;
						aa = 1.0F - aa * aa * aa * aa;
						u = u * (1.0F - xx) + y * xx;
						v = v * (1.0F - xx) + z * xx;
						w = w * (1.0F - xx) + aa * xx;
						u = u * 0.96F + 0.03F;
						v = v * 0.96F + 0.03F;
						w = w * 0.96F + 0.03F;
						if (u > 1.0F) {
							u = 1.0F;
						}

						if (v > 1.0F) {
							v = 1.0F;
						}

						if (w > 1.0F) {
							w = 1.0F;
						}

						if (u < 0.0F) {
							u = 0.0F;
						}

						if (v < 0.0F) {
							v = 0.0F;
						}

						if (w < 0.0F) {
							w = 0.0F;
						}

						int ab = 255;
						int ac = (int)(u * 255.0F);
						int ad = (int)(v * 255.0F);
						int ae = (int)(w * 255.0F);
						this.image.setPixelRGBA(l, k, 0xFF000000 | ae << 16 | ad << 8 | ac);
					}
				}

				this.texture.upload();
				this.isDirty = false;
				this.client.getProfiler().pop();
			}
		}
	}
}
