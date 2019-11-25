package net.minecraft.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.client.world.ClientWorld;
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
	private float field_21528;
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
			this.isDirty = false;
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

				float h = this.client.player.method_3140();
				float i;
				if (this.client.player.hasStatusEffect(StatusEffects.NIGHT_VISION)) {
					i = GameRenderer.getNightVisionStrength(this.client.player, delta);
				} else if (h > 0.0F && this.client.player.hasStatusEffect(StatusEffects.CONDUIT_POWER)) {
					i = h;
				} else {
					i = 0.0F;
				}

				Vector3f vector3f = new Vector3f(f, f, 1.0F);
				vector3f.method_23847(new Vector3f(1.0F, 1.0F, 1.0F), 0.35F);
				float j = this.field_21528 + 1.5F;
				Vector3f vector3f2 = new Vector3f();

				for (int k = 0; k < 16; k++) {
					for (int l = 0; l < 16; l++) {
						float m = this.getBrightness(clientWorld, k) * g;
						float n = this.getBrightness(clientWorld, l) * j;
						float p = n * ((n * 0.6F + 0.4F) * 0.6F + 0.4F);
						float q = n * (n * n * 0.6F + 0.4F);
						vector3f2.set(n, p, q);
						if (clientWorld.dimension.getType() == DimensionType.THE_END) {
							vector3f2.method_23847(new Vector3f(0.99F, 1.12F, 1.0F), 0.25F);
						} else {
							Vector3f vector3f3 = vector3f.copy();
							vector3f3.scale(m);
							vector3f2.add(vector3f3);
							vector3f2.method_23847(new Vector3f(0.75F, 0.75F, 0.75F), 0.04F);
							if (this.worldRenderer.getSkyDarkness(delta) > 0.0F) {
								float r = this.worldRenderer.getSkyDarkness(delta);
								Vector3f vector3f4 = vector3f2.copy();
								vector3f4.piecewiseMultiply(0.7F, 0.6F, 0.6F);
								vector3f2.method_23847(vector3f4, r);
							}
						}

						vector3f2.clamp(0.0F, 1.0F);
						if (i > 0.0F) {
							float s = Math.max(vector3f2.getX(), Math.max(vector3f2.getY(), vector3f2.getZ()));
							if (s < 1.0F) {
								float r = 1.0F / s;
								Vector3f vector3f4 = vector3f2.copy();
								vector3f4.scale(r);
								vector3f2.method_23847(vector3f4, i);
							}
						}

						float s = (float)this.client.options.gamma;
						Vector3f vector3f5 = vector3f2.copy();
						vector3f5.method_23848(this::method_23795);
						vector3f2.method_23847(vector3f5, s);
						vector3f2.method_23847(new Vector3f(0.75F, 0.75F, 0.75F), 0.04F);
						vector3f2.clamp(0.0F, 1.0F);
						vector3f2.scale(255.0F);
						int t = 255;
						int u = (int)vector3f2.getX();
						int v = (int)vector3f2.getY();
						int w = (int)vector3f2.getZ();
						this.image.setPixelRgba(l, k, 0xFF000000 | w << 16 | v << 8 | u);
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
		return world.dimension.getBrightness(i);
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
