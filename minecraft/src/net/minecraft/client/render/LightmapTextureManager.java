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
				this.image.setPixelRGBA(j, i, -1);
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

	public void update(float f) {
		if (this.isDirty) {
			this.client.getProfiler().push("lightTex");
			World world = this.client.world;
			if (world != null) {
				float g = world.getAmbientLight(1.0F);
				float h = g * 0.95F + 0.05F;
				float i = this.client.player.method_3140();
				float j;
				if (this.client.player.hasStatusEffect(StatusEffects.NIGHT_VISION)) {
					j = GameRenderer.getNightVisionStrength(this.client.player, f);
				} else if (i > 0.0F && this.client.player.hasStatusEffect(StatusEffects.CONDUIT_POWER)) {
					j = i;
				} else {
					j = 0.0F;
				}

				for (int k = 0; k < 16; k++) {
					for (int l = 0; l < 16; l++) {
						float m = this.method_23284(world, k) * h;
						float n = this.method_23284(world, l) * (this.prevFlicker * 0.1F + 1.5F);
						if (world.getTicksSinceLightning() > 0) {
							m = this.method_23284(world, k);
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

						if (world.dimension.getType() == DimensionType.THE_END) {
							u = 0.22F + n * 0.75F;
							v = 0.28F + s * 0.75F;
							w = 0.25F + t * 0.75F;
						}

						if (j > 0.0F) {
							float x = Math.min(1.0F / u, Math.min(1.0F / v, 1.0F / w));
							u = u * (1.0F - j) + u * x * j;
							v = v * (1.0F - j) + v * x * j;
							w = w * (1.0F - j) + w * x * j;
						}

						u = MathHelper.clamp(u, 0.0F, 1.0F);
						v = MathHelper.clamp(v, 0.0F, 1.0F);
						w = MathHelper.clamp(w, 0.0F, 1.0F);
						float x = (float)this.client.options.gamma;
						float y = 1.0F - u;
						float z = 1.0F - v;
						float aa = 1.0F - w;
						y = 1.0F - y * y * y * y;
						z = 1.0F - z * z * z * z;
						aa = 1.0F - aa * aa * aa * aa;
						u = u * (1.0F - x) + y * x;
						v = v * (1.0F - x) + z * x;
						w = w * (1.0F - x) + aa * x;
						u = u * 0.96F + 0.03F;
						v = v * 0.96F + 0.03F;
						w = w * 0.96F + 0.03F;
						u = MathHelper.clamp(u, 0.0F, 1.0F);
						v = MathHelper.clamp(v, 0.0F, 1.0F);
						w = MathHelper.clamp(w, 0.0F, 1.0F);
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

	private float method_23284(World world, int i) {
		return world.dimension.getLightLevelToBrightness()[i];
	}
}
