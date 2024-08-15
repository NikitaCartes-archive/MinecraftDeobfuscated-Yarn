package net.minecraft.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gl.SimpleFramebuffer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.resource.ResourceFactory;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.dimension.DimensionType;
import org.joml.Vector3f;
import org.slf4j.Logger;

/**
 * The lightmap texture manager maintains a texture containing the RGBA overlay for each of the 16&times;16 sky and block light combinations.
 * <p>
 * Also contains some utilities to pack and unpack lightmap coordinates from sky and block light values,
 * and some lightmap coordinates constants.
 */
@Environment(EnvType.CLIENT)
public class LightmapTextureManager implements AutoCloseable {
	/**
	 * Represents the maximum lightmap coordinate, where both sky light and block light equals {@code 15}.
	 * The value of this maximum lightmap coordinate is {@value}.
	 */
	public static final int MAX_LIGHT_COORDINATE = 15728880;
	/**
	 * Represents the maximum sky-light-wise lightmap coordinate whose value is {@value}.
	 * This is equivalent to a {@code 15} sky light and {@code 0} block light.
	 */
	public static final int MAX_SKY_LIGHT_COORDINATE = 15728640;
	/**
	 * Represents the maximum block-light-wise lightmap coordinate whose value is {@value}.
	 * This is equivalent to a {@code 0} sky light and {@code 15} block light.
	 */
	public static final int MAX_BLOCK_LIGHT_COORDINATE = 240;
	private static final int field_53098 = 16;
	private static final Logger field_53099 = LogUtils.getLogger();
	@Nullable
	private ShaderProgram shaderProgram;
	private final SimpleFramebuffer lightmapFramebuffer;
	private boolean dirty;
	private float flickerIntensity;
	private final GameRenderer renderer;
	private final MinecraftClient client;

	public LightmapTextureManager(GameRenderer renderer, MinecraftClient client) {
		this.renderer = renderer;
		this.client = client;
		this.lightmapFramebuffer = new SimpleFramebuffer(16, 16, false);
		this.lightmapFramebuffer.setTexFilter(9729);
		this.lightmapFramebuffer.setClearColor(1.0F, 1.0F, 1.0F, 1.0F);
		this.lightmapFramebuffer.clear();
	}

	public void loadShaderProgram(ResourceFactory resourceFactory) {
		if (this.shaderProgram != null) {
			this.shaderProgram.close();
		}

		try {
			this.shaderProgram = new ShaderProgram(resourceFactory, "lightmap", VertexFormats.BLIT_SCREEN);
		} catch (IOException var3) {
			field_53099.error("Failed to load lightmap shader", (Throwable)var3);
			this.shaderProgram = null;
		}
	}

	public void close() {
		if (this.shaderProgram != null) {
			this.shaderProgram.close();
			this.shaderProgram = null;
		}

		this.lightmapFramebuffer.delete();
	}

	public void tick() {
		this.flickerIntensity = this.flickerIntensity + (float)((Math.random() - Math.random()) * Math.random() * Math.random() * 0.1);
		this.flickerIntensity *= 0.9F;
		this.dirty = true;
	}

	public void disable() {
		RenderSystem.setShaderTexture(2, 0);
	}

	public void enable() {
		RenderSystem.setShaderTexture(2, this.lightmapFramebuffer.getColorAttachment());
	}

	private float getDarknessFactor(float delta) {
		StatusEffectInstance statusEffectInstance = this.client.player.getStatusEffect(StatusEffects.DARKNESS);
		return statusEffectInstance != null ? statusEffectInstance.getFadeFactor(this.client.player, delta) : 0.0F;
	}

	private float getDarkness(LivingEntity entity, float factor, float delta) {
		float f = 0.45F * factor;
		return Math.max(0.0F, MathHelper.cos(((float)entity.age - delta) * (float) Math.PI * 0.025F) * f);
	}

	public void update(float delta) {
		if (this.dirty && this.shaderProgram != null) {
			this.dirty = false;
			this.client.getProfiler().push("lightTex");
			ClientWorld clientWorld = this.client.world;
			if (clientWorld != null) {
				float f = clientWorld.getSkyBrightness(1.0F);
				float g;
				if (clientWorld.getLightningTicksLeft() > 0) {
					g = 1.0F;
				} else {
					g = f * 0.95F + 0.05F;
				}

				float h = this.client.options.getDarknessEffectScale().getValue().floatValue();
				float i = this.getDarknessFactor(delta) * h;
				float j = this.getDarkness(this.client.player, i, delta) * h;
				float k = this.client.player.getUnderwaterVisibility();
				float l;
				if (this.client.player.hasStatusEffect(StatusEffects.NIGHT_VISION)) {
					l = GameRenderer.getNightVisionStrength(this.client.player, delta);
				} else if (k > 0.0F && this.client.player.hasStatusEffect(StatusEffects.CONDUIT_POWER)) {
					l = k;
				} else {
					l = 0.0F;
				}

				Vector3f vector3f = new Vector3f(f, f, 1.0F).lerp(new Vector3f(1.0F, 1.0F, 1.0F), 0.35F);
				float m = this.flickerIntensity + 1.5F;
				float n = clientWorld.getDimension().ambientLight();
				boolean bl = clientWorld.getDimensionEffects().shouldBrightenLighting();
				float o = this.client.options.getGamma().getValue().floatValue();
				this.shaderProgram.getUniformOrDefault("AmbientLightFactor").set(n);
				this.shaderProgram.getUniformOrDefault("SkyFactor").set(g);
				this.shaderProgram.getUniformOrDefault("BlockFactor").set(m);
				this.shaderProgram.getUniformOrDefault("UseBrightLightmap").set(bl ? 1 : 0);
				this.shaderProgram.getUniformOrDefault("SkyLightColor").set(vector3f);
				this.shaderProgram.getUniformOrDefault("NightVisionFactor").set(l);
				this.shaderProgram.getUniformOrDefault("DarknessScale").set(j);
				this.shaderProgram.getUniformOrDefault("DarkenWorldFactor").set(this.renderer.getSkyDarkness(delta));
				this.shaderProgram.getUniformOrDefault("BrightnessFactor").set(Math.max(0.0F, o - i));
				this.shaderProgram.bind();
				this.lightmapFramebuffer.beginWrite(true);
				BufferBuilder bufferBuilder = RenderSystem.renderThreadTesselator().begin(VertexFormat.DrawMode.QUADS, VertexFormats.BLIT_SCREEN);
				bufferBuilder.vertex(0.0F, 0.0F, 0.0F);
				bufferBuilder.vertex(1.0F, 0.0F, 0.0F);
				bufferBuilder.vertex(1.0F, 1.0F, 0.0F);
				bufferBuilder.vertex(0.0F, 1.0F, 0.0F);
				BufferRenderer.draw(bufferBuilder.end());
				this.shaderProgram.unbind();
				this.lightmapFramebuffer.endWrite();
				this.client.getProfiler().pop();
			}
		}
	}

	public static float getBrightness(DimensionType type, int lightLevel) {
		return getBrightness(type.ambientLight(), lightLevel);
	}

	public static float getBrightness(float ambientLight, int lightLevel) {
		float f = (float)lightLevel / 15.0F;
		float g = f / (4.0F - 3.0F * f);
		return MathHelper.lerp(ambientLight, g, 1.0F);
	}

	public static int pack(int block, int sky) {
		return block << 4 | sky << 20;
	}

	public static int getBlockLightCoordinates(int light) {
		return light >>> 4 & 15;
	}

	public static int getSkyLightCoordinates(int light) {
		return light >>> 20 & 15;
	}

	public static int applyEmission(int light, int lightEmission) {
		int i = Math.max(getSkyLightCoordinates(light), lightEmission);
		int j = Math.max(getBlockLightCoordinates(light), lightEmission);
		return pack(j, i);
	}
}
