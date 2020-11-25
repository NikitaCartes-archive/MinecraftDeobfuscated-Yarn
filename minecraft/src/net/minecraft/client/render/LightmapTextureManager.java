package net.minecraft.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3f;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class LightmapTextureManager implements AutoCloseable {
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

		for (int i = 0; i < 16; i++) {
			for (int j = 0; j < 16; j++) {
				this.image.setPixelColor(j, i, -1);
			}
		}

		this.texture.upload();
	}

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
		if (this.dirty) {
			this.dirty = false;
			this.client.getProfiler().push("lightTex");
			ClientWorld clientWorld = this.client.world;
			if (clientWorld != null) {
				float f = clientWorld.method_23783(1.0F);
				float g;
				if (clientWorld.getLightningTicksLeft() > 0) {
					g = 1.0F;
				} else {
					g = f * 0.95F + 0.05F;
				}

				float h = this.client.player.getUnderwaterVisibility();
				float i;
				if (this.client.player.hasStatusEffect(StatusEffects.NIGHT_VISION)) {
					i = GameRenderer.getNightVisionStrength(this.client.player, delta);
				} else if (h > 0.0F && this.client.player.hasStatusEffect(StatusEffects.CONDUIT_POWER)) {
					i = h;
				} else {
					i = 0.0F;
				}

				Vec3f vec3f = new Vec3f(f, f, 1.0F);
				vec3f.lerp(new Vec3f(1.0F, 1.0F, 1.0F), 0.35F);
				float j = this.field_21528 + 1.5F;
				Vec3f vec3f2 = new Vec3f();

				for (int k = 0; k < 16; k++) {
					for (int l = 0; l < 16; l++) {
						float m = this.getBrightness(clientWorld, k) * g;
						float n = this.getBrightness(clientWorld, l) * j;
						float p = n * ((n * 0.6F + 0.4F) * 0.6F + 0.4F);
						float q = n * (n * n * 0.6F + 0.4F);
						vec3f2.set(n, p, q);
						if (clientWorld.getSkyProperties().shouldBrightenLighting()) {
							vec3f2.lerp(new Vec3f(0.99F, 1.12F, 1.0F), 0.25F);
						} else {
							Vec3f vec3f3 = vec3f.copy();
							vec3f3.scale(m);
							vec3f2.add(vec3f3);
							vec3f2.lerp(new Vec3f(0.75F, 0.75F, 0.75F), 0.04F);
							if (this.renderer.getSkyDarkness(delta) > 0.0F) {
								float r = this.renderer.getSkyDarkness(delta);
								Vec3f vec3f4 = vec3f2.copy();
								vec3f4.multiplyComponentwise(0.7F, 0.6F, 0.6F);
								vec3f2.lerp(vec3f4, r);
							}
						}

						vec3f2.clamp(0.0F, 1.0F);
						if (i > 0.0F) {
							float s = Math.max(vec3f2.getX(), Math.max(vec3f2.getY(), vec3f2.getZ()));
							if (s < 1.0F) {
								float r = 1.0F / s;
								Vec3f vec3f4 = vec3f2.copy();
								vec3f4.scale(r);
								vec3f2.lerp(vec3f4, i);
							}
						}

						float s = (float)this.client.options.gamma;
						Vec3f vec3f5 = vec3f2.copy();
						vec3f5.modify(this::method_23795);
						vec3f2.lerp(vec3f5, s);
						vec3f2.lerp(new Vec3f(0.75F, 0.75F, 0.75F), 0.04F);
						vec3f2.clamp(0.0F, 1.0F);
						vec3f2.scale(255.0F);
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
		}
	}

	private float method_23795(float f) {
		float g = 1.0F - f;
		return 1.0F - g * g * g * g;
	}

	private float getBrightness(World world, int i) {
		return world.getDimension().method_28516(i);
	}

	public static int pack(int block, int sky) {
		return block << 4 | sky << 20;
	}

	public static int getBlockLightCoordinates(int light) {
		return light >> 4 & 65535;
	}

	public static int getSkyLightCoordinates(int light) {
		return light >> 20 & 65535;
	}
}
