package net.minecraft.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
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

	public LightmapTextureManager(GameRenderer gameRenderer, MinecraftClient minecraftClient) {
		this.worldRenderer = gameRenderer;
		this.client = minecraftClient;
		this.texture = new NativeImageBackedTexture(16, 16, false);
		this.textureIdentifier = this.client.getTextureManager().registerDynamicTexture("light_map", this.texture);
		this.image = this.texture.getImage();

		for (int i = 0; i < 16; i++) {
			for (int j = 0; j < 16; j++) {
				this.image.setPixelRgba(j, i, -1);
			}
		}

		this.texture.upload();
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
		RenderSystem.activeTexture(33986);
		RenderSystem.disableTexture();
		RenderSystem.activeTexture(33984);
	}

	public void enable() {
		RenderSystem.activeTexture(33986);
		RenderSystem.matrixMode(5890);
		RenderSystem.loadIdentity();
		float f = 0.00390625F;
		RenderSystem.scalef(0.00390625F, 0.00390625F, 0.00390625F);
		RenderSystem.translatef(8.0F, 8.0F, 8.0F);
		RenderSystem.matrixMode(5888);
		this.client.getTextureManager().bindTexture(this.textureIdentifier);
		RenderSystem.texParameter(3553, 10241, 9729);
		RenderSystem.texParameter(3553, 10240, 9729);
		RenderSystem.texParameter(3553, 10242, 10496);
		RenderSystem.texParameter(3553, 10243, 10496);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.enableTexture();
		RenderSystem.activeTexture(33984);
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
					i = GameRenderer.getNightVisionStrength(this.client.player, delta);
				} else if (h > 0.0F && this.client.player.hasStatusEffect(StatusEffects.CONDUIT_POWER)) {
					i = h;
				} else {
					i = 0.0F;
				}

				for (int j = 0; j < 16; j++) {
					for (int k = 0; k < 16; k++) {
						float l = this.method_23284(world, j) * g;
						float m = this.method_23284(world, k) * (this.prevFlicker * 0.1F + 1.5F);
						if (world.getTicksSinceLightning() > 0) {
							l = this.method_23284(world, j);
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
							float w = Math.min(1.0F / t, Math.min(1.0F / u, 1.0F / v));
							t = t * (1.0F - i) + t * w * i;
							u = u * (1.0F - i) + u * w * i;
							v = v * (1.0F - i) + v * w * i;
						}

						t = MathHelper.clamp(t, 0.0F, 1.0F);
						u = MathHelper.clamp(u, 0.0F, 1.0F);
						v = MathHelper.clamp(v, 0.0F, 1.0F);
						float w = (float)this.client.options.gamma;
						float x = 1.0F - t;
						float y = 1.0F - u;
						float z = 1.0F - v;
						x = 1.0F - x * x * x * x;
						y = 1.0F - y * y * y * y;
						z = 1.0F - z * z * z * z;
						t = t * (1.0F - w) + x * w;
						u = u * (1.0F - w) + y * w;
						v = v * (1.0F - w) + z * w;
						t = t * 0.96F + 0.03F;
						u = u * 0.96F + 0.03F;
						v = v * 0.96F + 0.03F;
						t = MathHelper.clamp(t, 0.0F, 1.0F);
						u = MathHelper.clamp(u, 0.0F, 1.0F);
						v = MathHelper.clamp(v, 0.0F, 1.0F);
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

	private float method_23284(World world, int i) {
		return world.dimension.getLightLevelToBrightness()[i];
	}

	public static int method_23687(int i, int j) {
		return i | j << 16;
	}

	public static int method_23686(int i) {
		return i & 65535;
	}

	public static int method_23688(int i) {
		return i >> 16 & 65535;
	}
}
