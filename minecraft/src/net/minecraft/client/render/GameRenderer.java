package net.minecraft.client.render;

import com.google.common.collect.Maps;
import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.IOException;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5944;
import net.minecraft.block.BlockState;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.ShaderEffect;
import net.minecraft.client.gui.hud.InGameOverlayRenderer;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.util.ScreenshotUtils;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.mob.SpiderEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.resource.ResourceFactory;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SynchronousResourceReloadListener;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.crash.CrashCallable;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import net.minecraft.world.GameMode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class GameRenderer implements SynchronousResourceReloadListener, AutoCloseable {
	private static final Identifier NAUSEA_OVERLAY = new Identifier("textures/misc/nausea.png");
	private static final Logger LOGGER = LogManager.getLogger();
	private final MinecraftClient client;
	private final ResourceManager resourceContainer;
	private final Random random = new Random();
	private float viewDistance;
	public final HeldItemRenderer firstPersonRenderer;
	private final MapRenderer mapRenderer;
	private final BufferBuilderStorage buffers;
	private int ticks;
	private float movementFovMultiplier;
	private float lastMovementFovMultiplier;
	private float skyDarkness;
	private float lastSkyDarkness;
	private boolean renderHand = true;
	private boolean blockOutlineEnabled = true;
	private long lastWorldIconUpdate;
	private long lastWindowFocusedTime = Util.getMeasuringTimeMs();
	private final LightmapTextureManager lightmapTextureManager;
	private final OverlayTexture overlayTexture = new OverlayTexture();
	private boolean renderingPanorama;
	private float zoom = 1.0F;
	private float zoomX;
	private float zoomY;
	@Nullable
	private ItemStack floatingItem;
	private int floatingItemTimeLeft;
	private float floatingItemWidth;
	private float floatingItemHeight;
	@Nullable
	private ShaderEffect shader;
	private static final Identifier[] SHADERS_LOCATIONS = new Identifier[]{
		new Identifier("shaders/post/notch.json"),
		new Identifier("shaders/post/fxaa.json"),
		new Identifier("shaders/post/art.json"),
		new Identifier("shaders/post/bumpy.json"),
		new Identifier("shaders/post/blobs2.json"),
		new Identifier("shaders/post/pencil.json"),
		new Identifier("shaders/post/color_convolve.json"),
		new Identifier("shaders/post/deconverge.json"),
		new Identifier("shaders/post/flip.json"),
		new Identifier("shaders/post/invert.json"),
		new Identifier("shaders/post/ntsc.json"),
		new Identifier("shaders/post/outline.json"),
		new Identifier("shaders/post/phosphor.json"),
		new Identifier("shaders/post/scan_pincushion.json"),
		new Identifier("shaders/post/sobel.json"),
		new Identifier("shaders/post/bits.json"),
		new Identifier("shaders/post/desaturate.json"),
		new Identifier("shaders/post/green.json"),
		new Identifier("shaders/post/blur.json"),
		new Identifier("shaders/post/wobble.json"),
		new Identifier("shaders/post/blobs.json"),
		new Identifier("shaders/post/antialias.json"),
		new Identifier("shaders/post/creeper.json"),
		new Identifier("shaders/post/spider.json")
	};
	public static final int SHADER_COUNT = SHADERS_LOCATIONS.length;
	private int forcedShaderIndex = SHADER_COUNT;
	private boolean shadersEnabled;
	private final Camera camera = new Camera();
	public class_5944 field_29403;
	private final Map<String, class_5944> field_29350 = Maps.<String, class_5944>newHashMap();
	@Nullable
	private static class_5944 field_29351;
	@Nullable
	private static class_5944 field_29352;
	@Nullable
	private static class_5944 field_29353;
	@Nullable
	private static class_5944 field_29354;
	@Nullable
	private static class_5944 field_29355;
	@Nullable
	private static class_5944 field_29356;
	@Nullable
	private static class_5944 field_29357;
	@Nullable
	private static class_5944 field_29358;
	@Nullable
	private static class_5944 field_29359;
	@Nullable
	private static class_5944 field_29360;
	@Nullable
	private static class_5944 field_29361;
	@Nullable
	private static class_5944 field_29362;
	@Nullable
	private static class_5944 field_29363;
	@Nullable
	private static class_5944 field_29364;
	@Nullable
	private static class_5944 field_29365;
	@Nullable
	private static class_5944 field_29366;
	@Nullable
	private static class_5944 field_29377;
	@Nullable
	private static class_5944 field_29378;
	@Nullable
	private static class_5944 field_29379;
	@Nullable
	private static class_5944 field_29380;
	@Nullable
	private static class_5944 field_29381;
	@Nullable
	private static class_5944 field_29382;
	@Nullable
	private static class_5944 field_29383;
	@Nullable
	private static class_5944 field_29384;
	@Nullable
	private static class_5944 field_29385;
	@Nullable
	private static class_5944 field_29386;
	@Nullable
	private static class_5944 field_29387;
	@Nullable
	private static class_5944 field_29388;
	@Nullable
	private static class_5944 field_29389;
	@Nullable
	private static class_5944 field_29390;
	@Nullable
	private static class_5944 field_29391;
	@Nullable
	private static class_5944 field_29392;
	@Nullable
	private static class_5944 field_29393;
	@Nullable
	private static class_5944 field_29394;
	@Nullable
	private static class_5944 field_29395;
	@Nullable
	private static class_5944 field_29396;
	@Nullable
	private static class_5944 field_29397;
	@Nullable
	private static class_5944 field_29398;
	@Nullable
	private static class_5944 field_29399;
	@Nullable
	private static class_5944 field_29400;
	@Nullable
	private static class_5944 field_29401;
	@Nullable
	private static class_5944 field_29402;
	@Nullable
	private static class_5944 field_29367;
	@Nullable
	private static class_5944 field_29368;
	@Nullable
	private static class_5944 field_29369;
	@Nullable
	private static class_5944 field_29370;
	@Nullable
	private static class_5944 field_29371;
	@Nullable
	private static class_5944 field_29372;
	@Nullable
	private static class_5944 field_29373;
	@Nullable
	private static class_5944 field_29374;
	@Nullable
	private static class_5944 field_29375;
	@Nullable
	private static class_5944 field_29376;

	public GameRenderer(MinecraftClient minecraftClient, ResourceManager resourceManager, BufferBuilderStorage bufferBuilderStorage) {
		this.client = minecraftClient;
		this.resourceContainer = resourceManager;
		this.firstPersonRenderer = minecraftClient.getHeldItemRenderer();
		this.mapRenderer = new MapRenderer(minecraftClient.getTextureManager());
		this.lightmapTextureManager = new LightmapTextureManager(this, minecraftClient);
		this.buffers = bufferBuilderStorage;
		this.shader = null;
	}

	public void close() {
		this.lightmapTextureManager.close();
		this.mapRenderer.close();
		this.overlayTexture.close();
		this.disableShader();
		this.method_34537();
		if (this.field_29403 != null) {
			this.field_29403.close();
		}
	}

	public void disableShader() {
		if (this.shader != null) {
			this.shader.close();
		}

		this.shader = null;
		this.forcedShaderIndex = SHADER_COUNT;
	}

	public void toggleShadersEnabled() {
		this.shadersEnabled = !this.shadersEnabled;
	}

	public void onCameraEntitySet(@Nullable Entity entity) {
		if (this.shader != null) {
			this.shader.close();
		}

		this.shader = null;
		if (entity instanceof CreeperEntity) {
			this.loadShader(new Identifier("shaders/post/creeper.json"));
		} else if (entity instanceof SpiderEntity) {
			this.loadShader(new Identifier("shaders/post/spider.json"));
		} else if (entity instanceof EndermanEntity) {
			this.loadShader(new Identifier("shaders/post/invert.json"));
		}
	}

	private void loadShader(Identifier id) {
		if (this.shader != null) {
			this.shader.close();
		}

		try {
			this.shader = new ShaderEffect(this.client.getTextureManager(), this.resourceContainer, this.client.getFramebuffer(), id);
			this.shader.setupDimensions(this.client.getWindow().getFramebufferWidth(), this.client.getWindow().getFramebufferHeight());
			this.shadersEnabled = true;
		} catch (IOException var3) {
			LOGGER.warn("Failed to load shader: {}", id, var3);
			this.forcedShaderIndex = SHADER_COUNT;
			this.shadersEnabled = false;
		} catch (JsonSyntaxException var4) {
			LOGGER.warn("Failed to parse shader: {}", id, var4);
			this.forcedShaderIndex = SHADER_COUNT;
			this.shadersEnabled = false;
		}
	}

	@Override
	public void apply(ResourceManager manager) {
		this.method_34538(manager);
		if (this.shader != null) {
			this.shader.close();
		}

		this.shader = null;
		if (this.forcedShaderIndex == SHADER_COUNT) {
			this.onCameraEntitySet(this.client.getCameraEntity());
		} else {
			this.loadShader(SHADERS_LOCATIONS[this.forcedShaderIndex]);
		}
	}

	public void method_34521(ResourceFactory factory) {
		if (this.field_29403 != null) {
			throw new RuntimeException("Blit shader already preloaded");
		} else {
			try {
				this.field_29403 = new class_5944(factory, "blit_screen", VertexFormats.field_29336);
				field_29351 = this.method_34522(factory, "position", VertexFormats.POSITION);
				field_29352 = this.method_34522(factory, "position_color", VertexFormats.POSITION_COLOR);
				field_29353 = this.method_34522(factory, "position_color_tex", VertexFormats.POSITION_COLOR_TEXTURE);
				field_29354 = this.method_34522(factory, "position_tex", VertexFormats.POSITION_TEXTURE);
				field_29355 = this.method_34522(factory, "position_tex_color", VertexFormats.POSITION_TEXTURE_COLOR);
			} catch (IOException var3) {
				throw new RuntimeException("could not preload blit shader", var3);
			}
		}
	}

	private class_5944 method_34522(ResourceFactory resourceFactory, String string, VertexFormat vertexFormat) throws IOException {
		class_5944 lv = new class_5944(resourceFactory, string, vertexFormat);
		this.field_29350.put(string, lv);
		return lv;
	}

	public void method_34538(ResourceManager resourceManager) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		this.method_34537();

		try {
			field_29356 = this.method_34522(resourceManager, "block", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL);
			field_29357 = this.method_34522(resourceManager, "new_entity", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL);
			field_29358 = this.method_34522(resourceManager, "particle", VertexFormats.POSITION_TEXTURE_COLOR_LIGHT);
			field_29351 = this.method_34522(resourceManager, "position", VertexFormats.POSITION);
			field_29352 = this.method_34522(resourceManager, "position_color", VertexFormats.POSITION_COLOR);
			field_29359 = this.method_34522(resourceManager, "position_color_lightmap", VertexFormats.POSITION_COLOR_LIGHT);
			field_29353 = this.method_34522(resourceManager, "position_color_tex", VertexFormats.POSITION_COLOR_TEXTURE);
			field_29360 = this.method_34522(resourceManager, "position_color_tex_lightmap", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT);
			field_29354 = this.method_34522(resourceManager, "position_tex", VertexFormats.POSITION_TEXTURE);
			field_29355 = this.method_34522(resourceManager, "position_tex_color", VertexFormats.POSITION_TEXTURE_COLOR);
			field_29361 = this.method_34522(resourceManager, "position_tex_color_normal", VertexFormats.POSITION_TEXTURE_COLOR_NORMAL);
			field_29362 = this.method_34522(resourceManager, "position_tex_lightmap_color", VertexFormats.POSITION_TEXTURE_LIGHT_COLOR);
			field_29363 = this.method_34522(resourceManager, "rendertype_solid", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL);
			field_29364 = this.method_34522(resourceManager, "rendertype_cutout_mipped", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL);
			field_29365 = this.method_34522(resourceManager, "rendertype_cutout", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL);
			field_29366 = this.method_34522(resourceManager, "rendertype_translucent", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL);
			field_29377 = this.method_34522(resourceManager, "rendertype_translucent_moving_block", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL);
			field_29378 = this.method_34522(resourceManager, "rendertype_translucent_no_crumbling", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL);
			field_29379 = this.method_34522(resourceManager, "rendertype_armor_cutout_no_cull", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL);
			field_29380 = this.method_34522(resourceManager, "rendertype_entity_solid", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL);
			field_29381 = this.method_34522(resourceManager, "rendertype_entity_cutout", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL);
			field_29382 = this.method_34522(resourceManager, "rendertype_entity_cutout_no_cull", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL);
			field_29383 = this.method_34522(resourceManager, "rendertype_entity_cutout_no_cull_z_offset", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL);
			field_29384 = this.method_34522(resourceManager, "rendertype_item_entity_translucent_cull", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL);
			field_29385 = this.method_34522(resourceManager, "rendertype_entity_translucent_cull", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL);
			field_29386 = this.method_34522(resourceManager, "rendertype_entity_translucent", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL);
			field_29387 = this.method_34522(resourceManager, "rendertype_entity_smooth_cutout", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL);
			field_29388 = this.method_34522(resourceManager, "rendertype_beacon_beam", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL);
			field_29389 = this.method_34522(resourceManager, "rendertype_entity_decal", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL);
			field_29390 = this.method_34522(resourceManager, "rendertype_entity_no_outline", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL);
			field_29391 = this.method_34522(resourceManager, "rendertype_entity_shadow", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL);
			field_29392 = this.method_34522(resourceManager, "rendertype_entity_alpha", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL);
			field_29393 = this.method_34522(resourceManager, "rendertype_eyes", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL);
			field_29394 = this.method_34522(resourceManager, "rendertype_energy_swirl", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL);
			field_29395 = this.method_34522(resourceManager, "rendertype_leash", VertexFormats.POSITION_COLOR_LIGHT);
			field_29396 = this.method_34522(resourceManager, "rendertype_water_mask", VertexFormats.POSITION);
			field_29397 = this.method_34522(resourceManager, "rendertype_outline", VertexFormats.POSITION_COLOR_TEXTURE);
			field_29398 = this.method_34522(resourceManager, "rendertype_armor_glint", VertexFormats.POSITION_TEXTURE);
			field_29399 = this.method_34522(resourceManager, "rendertype_armor_entity_glint", VertexFormats.POSITION_TEXTURE);
			field_29400 = this.method_34522(resourceManager, "rendertype_glint_translucent", VertexFormats.POSITION_TEXTURE);
			field_29401 = this.method_34522(resourceManager, "rendertype_glint", VertexFormats.POSITION_TEXTURE);
			field_29402 = this.method_34522(resourceManager, "rendertype_glint_direct", VertexFormats.POSITION_TEXTURE);
			field_29367 = this.method_34522(resourceManager, "rendertype_entity_glint", VertexFormats.POSITION_TEXTURE);
			field_29368 = this.method_34522(resourceManager, "rendertype_entity_glint_direct", VertexFormats.POSITION_TEXTURE);
			field_29369 = this.method_34522(resourceManager, "rendertype_text", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT);
			field_29370 = this.method_34522(resourceManager, "rendertype_text_see_through", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT);
			field_29371 = this.method_34522(resourceManager, "rendertype_lightning", VertexFormats.POSITION_COLOR);
			field_29372 = this.method_34522(resourceManager, "rendertype_tripwire", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL);
			field_29373 = this.method_34522(resourceManager, "rendertype_end_portal", VertexFormats.POSITION);
			field_29374 = this.method_34522(resourceManager, "rendertype_end_gateway", VertexFormats.POSITION);
			field_29375 = this.method_34522(resourceManager, "rendertype_lines", VertexFormats.field_29337);
			field_29376 = this.method_34522(resourceManager, "rendertype_crumbling", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL);
		} catch (IOException var3) {
			throw new RuntimeException("could not reload shaders", var3);
		}
	}

	private void method_34537() {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		this.field_29350.values().forEach(class_5944::close);
		this.field_29350.clear();
	}

	public void tick() {
		this.updateMovementFovMultiplier();
		this.lightmapTextureManager.tick();
		if (this.client.getCameraEntity() == null) {
			this.client.setCameraEntity(this.client.player);
		}

		this.camera.updateEyeHeight();
		this.ticks++;
		this.firstPersonRenderer.updateHeldItems();
		this.client.worldRenderer.tickRainSplashing(this.camera);
		this.lastSkyDarkness = this.skyDarkness;
		if (this.client.inGameHud.getBossBarHud().shouldDarkenSky()) {
			this.skyDarkness += 0.05F;
			if (this.skyDarkness > 1.0F) {
				this.skyDarkness = 1.0F;
			}
		} else if (this.skyDarkness > 0.0F) {
			this.skyDarkness -= 0.0125F;
		}

		if (this.floatingItemTimeLeft > 0) {
			this.floatingItemTimeLeft--;
			if (this.floatingItemTimeLeft == 0) {
				this.floatingItem = null;
			}
		}
	}

	@Nullable
	public ShaderEffect getShader() {
		return this.shader;
	}

	public void onResized(int i, int j) {
		if (this.shader != null) {
			this.shader.setupDimensions(i, j);
		}

		this.client.worldRenderer.onResized(i, j);
	}

	public void updateTargetedEntity(float tickDelta) {
		Entity entity = this.client.getCameraEntity();
		if (entity != null) {
			if (this.client.world != null) {
				this.client.getProfiler().push("pick");
				this.client.targetedEntity = null;
				double d = (double)this.client.interactionManager.getReachDistance();
				this.client.crosshairTarget = entity.raycast(d, tickDelta, false);
				Vec3d vec3d = entity.getCameraPosVec(tickDelta);
				boolean bl = false;
				int i = 3;
				double e = d;
				if (this.client.interactionManager.hasExtendedReach()) {
					e = 6.0;
					d = e;
				} else {
					if (d > 3.0) {
						bl = true;
					}

					d = d;
				}

				e *= e;
				if (this.client.crosshairTarget != null) {
					e = this.client.crosshairTarget.getPos().squaredDistanceTo(vec3d);
				}

				Vec3d vec3d2 = entity.getRotationVec(1.0F);
				Vec3d vec3d3 = vec3d.add(vec3d2.x * d, vec3d2.y * d, vec3d2.z * d);
				float f = 1.0F;
				Box box = entity.getBoundingBox().stretch(vec3d2.multiply(d)).expand(1.0, 1.0, 1.0);
				EntityHitResult entityHitResult = ProjectileUtil.raycast(entity, vec3d, vec3d3, box, entityx -> !entityx.isSpectator() && entityx.collides(), e);
				if (entityHitResult != null) {
					Entity entity2 = entityHitResult.getEntity();
					Vec3d vec3d4 = entityHitResult.getPos();
					double g = vec3d.squaredDistanceTo(vec3d4);
					if (bl && g > 9.0) {
						this.client.crosshairTarget = BlockHitResult.createMissed(vec3d4, Direction.getFacing(vec3d2.x, vec3d2.y, vec3d2.z), new BlockPos(vec3d4));
					} else if (g < e || this.client.crosshairTarget == null) {
						this.client.crosshairTarget = entityHitResult;
						if (entity2 instanceof LivingEntity || entity2 instanceof ItemFrameEntity) {
							this.client.targetedEntity = entity2;
						}
					}
				}

				this.client.getProfiler().pop();
			}
		}
	}

	private void updateMovementFovMultiplier() {
		float f = 1.0F;
		if (this.client.getCameraEntity() instanceof AbstractClientPlayerEntity) {
			AbstractClientPlayerEntity abstractClientPlayerEntity = (AbstractClientPlayerEntity)this.client.getCameraEntity();
			f = abstractClientPlayerEntity.getSpeed();
		}

		this.lastMovementFovMultiplier = this.movementFovMultiplier;
		this.movementFovMultiplier = this.movementFovMultiplier + (f - this.movementFovMultiplier) * 0.5F;
		if (this.movementFovMultiplier > 1.5F) {
			this.movementFovMultiplier = 1.5F;
		}

		if (this.movementFovMultiplier < 0.1F) {
			this.movementFovMultiplier = 0.1F;
		}
	}

	private double getFov(Camera camera, float tickDelta, boolean changingFov) {
		if (this.renderingPanorama) {
			return 90.0;
		} else {
			double d = 70.0;
			if (changingFov) {
				d = this.client.options.fov;
				d *= (double)MathHelper.lerp(tickDelta, this.lastMovementFovMultiplier, this.movementFovMultiplier);
			}

			if (camera.getFocusedEntity() instanceof LivingEntity && ((LivingEntity)camera.getFocusedEntity()).isDead()) {
				float f = Math.min((float)((LivingEntity)camera.getFocusedEntity()).deathTime + tickDelta, 20.0F);
				d /= (double)((1.0F - 500.0F / (f + 500.0F)) * 2.0F + 1.0F);
			}

			CameraSubmersionType cameraSubmersionType = camera.getSubmersionType();
			if (cameraSubmersionType == CameraSubmersionType.LAVA || cameraSubmersionType == CameraSubmersionType.WATER) {
				d *= (double)MathHelper.lerp(this.client.options.fovEffectScale, 1.0F, 0.85714287F);
			}

			return d;
		}
	}

	private void bobViewWhenHurt(MatrixStack matrices, float f) {
		if (this.client.getCameraEntity() instanceof LivingEntity) {
			LivingEntity livingEntity = (LivingEntity)this.client.getCameraEntity();
			float g = (float)livingEntity.hurtTime - f;
			if (livingEntity.isDead()) {
				float h = Math.min((float)livingEntity.deathTime + f, 20.0F);
				matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(40.0F - 8000.0F / (h + 200.0F)));
			}

			if (g < 0.0F) {
				return;
			}

			g /= (float)livingEntity.maxHurtTime;
			g = MathHelper.sin(g * g * g * g * (float) Math.PI);
			float h = livingEntity.knockbackVelocity;
			matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(-h));
			matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(-g * 14.0F));
			matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(h));
		}
	}

	private void bobView(MatrixStack matrices, float f) {
		if (this.client.getCameraEntity() instanceof PlayerEntity) {
			PlayerEntity playerEntity = (PlayerEntity)this.client.getCameraEntity();
			float g = playerEntity.horizontalSpeed - playerEntity.prevHorizontalSpeed;
			float h = -(playerEntity.horizontalSpeed + g * f);
			float i = MathHelper.lerp(f, playerEntity.prevStrideDistance, playerEntity.strideDistance);
			matrices.translate((double)(MathHelper.sin(h * (float) Math.PI) * i * 0.5F), (double)(-Math.abs(MathHelper.cos(h * (float) Math.PI) * i)), 0.0);
			matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(MathHelper.sin(h * (float) Math.PI) * i * 3.0F));
			matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(Math.abs(MathHelper.cos(h * (float) Math.PI - 0.2F) * i) * 5.0F));
		}
	}

	private void renderHand(MatrixStack matrices, Camera camera, float tickDelta) {
		if (!this.renderingPanorama) {
			this.loadProjectionMatrix(this.getBasicProjectionMatrix(this.getFov(camera, tickDelta, false)));
			MatrixStack.Entry entry = matrices.peek();
			entry.getModel().loadIdentity();
			entry.getNormal().loadIdentity();
			matrices.push();
			this.bobViewWhenHurt(matrices, tickDelta);
			if (this.client.options.bobView) {
				this.bobView(matrices, tickDelta);
			}

			boolean bl = this.client.getCameraEntity() instanceof LivingEntity && ((LivingEntity)this.client.getCameraEntity()).isSleeping();
			if (this.client.options.getPerspective().isFirstPerson()
				&& !bl
				&& !this.client.options.hudHidden
				&& this.client.interactionManager.getCurrentGameMode() != GameMode.SPECTATOR) {
				this.lightmapTextureManager.enable();
				this.firstPersonRenderer
					.renderItem(
						tickDelta,
						matrices,
						this.buffers.getEntityVertexConsumers(),
						this.client.player,
						this.client.getEntityRenderDispatcher().getLight(this.client.player, tickDelta)
					);
				this.lightmapTextureManager.disable();
			}

			matrices.pop();
			if (this.client.options.getPerspective().isFirstPerson() && !bl) {
				InGameOverlayRenderer.renderOverlays(this.client, matrices);
				this.bobViewWhenHurt(matrices, tickDelta);
			}

			if (this.client.options.bobView) {
				this.bobView(matrices, tickDelta);
			}
		}
	}

	public void loadProjectionMatrix(Matrix4f matrix4f) {
		RenderSystem.setProjectionMatrix(matrix4f);
	}

	public Matrix4f getBasicProjectionMatrix(double d) {
		MatrixStack matrixStack = new MatrixStack();
		matrixStack.peek().getModel().loadIdentity();
		if (this.zoom != 1.0F) {
			matrixStack.translate((double)this.zoomX, (double)(-this.zoomY), 0.0);
			matrixStack.scale(this.zoom, this.zoom, 1.0F);
		}

		matrixStack.peek()
			.getModel()
			.multiply(
				Matrix4f.viewboxMatrix(
					d, (float)this.client.getWindow().getFramebufferWidth() / (float)this.client.getWindow().getFramebufferHeight(), 0.05F, this.method_32796()
				)
			);
		return matrixStack.peek().getModel();
	}

	public float method_32796() {
		return this.viewDistance * 4.0F;
	}

	public static float getNightVisionStrength(LivingEntity entity, float f) {
		int i = entity.getStatusEffect(StatusEffects.NIGHT_VISION).getDuration();
		return i > 200 ? 1.0F : 0.7F + MathHelper.sin(((float)i - f) * (float) Math.PI * 0.2F) * 0.3F;
	}

	public void render(float tickDelta, long startTime, boolean tick) {
		if (!this.client.isWindowFocused()
			&& this.client.options.pauseOnLostFocus
			&& (!this.client.options.touchscreen || !this.client.mouse.wasRightButtonClicked())) {
			if (Util.getMeasuringTimeMs() - this.lastWindowFocusedTime > 500L) {
				this.client.openPauseMenu(false);
			}
		} else {
			this.lastWindowFocusedTime = Util.getMeasuringTimeMs();
		}

		if (!this.client.skipGameRender) {
			int i = (int)(this.client.mouse.getX() * (double)this.client.getWindow().getScaledWidth() / (double)this.client.getWindow().getWidth());
			int j = (int)(this.client.mouse.getY() * (double)this.client.getWindow().getScaledHeight() / (double)this.client.getWindow().getHeight());
			RenderSystem.viewport(0, 0, this.client.getWindow().getFramebufferWidth(), this.client.getWindow().getFramebufferHeight());
			if (tick && this.client.world != null) {
				this.client.getProfiler().push("level");
				this.renderWorld(tickDelta, startTime, new MatrixStack());
				if (this.client.isIntegratedServerRunning() && this.lastWorldIconUpdate < Util.getMeasuringTimeMs() - 1000L) {
					this.lastWorldIconUpdate = Util.getMeasuringTimeMs();
					if (!this.client.getServer().hasIconFile()) {
						this.updateWorldIcon();
					}
				}

				this.client.worldRenderer.drawEntityOutlinesFramebuffer();
				if (this.shader != null && this.shadersEnabled) {
					RenderSystem.disableBlend();
					RenderSystem.disableDepthTest();
					RenderSystem.enableTexture();
					RenderSystem.resetTextureMatrix();
					this.shader.render(tickDelta);
				}

				this.client.getFramebuffer().beginWrite(true);
			}

			Window window = this.client.getWindow();
			RenderSystem.clear(256, MinecraftClient.IS_SYSTEM_MAC);
			Matrix4f matrix4f = Matrix4f.method_34239(
				0.0F,
				(float)((double)window.getFramebufferWidth() / window.getScaleFactor()),
				0.0F,
				(float)((double)window.getFramebufferHeight() / window.getScaleFactor()),
				1000.0F,
				3000.0F
			);
			RenderSystem.setProjectionMatrix(matrix4f);
			MatrixStack matrixStack = RenderSystem.getModelViewStack();
			matrixStack.loadIdentity();
			matrixStack.translate(0.0, 0.0, -2000.0);
			RenderSystem.applyModelViewMatrix();
			DiffuseLighting.enableGuiDepthLighting();
			MatrixStack matrixStack2 = new MatrixStack();
			if (tick && this.client.world != null) {
				this.client.getProfiler().swap("gui");
				if (this.client.player != null) {
					float f = MathHelper.lerp(tickDelta, this.client.player.lastNauseaStrength, this.client.player.nextNauseaStrength);
					if (f > 0.0F && this.client.player.hasStatusEffect(StatusEffects.NAUSEA) && this.client.options.distortionEffectScale < 1.0F) {
						this.method_31136(f * (1.0F - this.client.options.distortionEffectScale));
					}
				}

				if (!this.client.options.hudHidden || this.client.currentScreen != null) {
					this.renderFloatingItem(this.client.getWindow().getScaledWidth(), this.client.getWindow().getScaledHeight(), tickDelta);
					this.client.inGameHud.render(matrixStack2, tickDelta);
					RenderSystem.clear(256, MinecraftClient.IS_SYSTEM_MAC);
				}

				this.client.getProfiler().pop();
			}

			if (this.client.overlay != null) {
				try {
					this.client.overlay.render(matrixStack2, i, j, this.client.getLastFrameDuration());
				} catch (Throwable var15) {
					CrashReport crashReport = CrashReport.create(var15, "Rendering overlay");
					CrashReportSection crashReportSection = crashReport.addElement("Overlay render details");
					crashReportSection.add("Overlay name", (CrashCallable<String>)(() -> this.client.overlay.getClass().getCanonicalName()));
					throw new CrashException(crashReport);
				}
			} else if (this.client.currentScreen != null) {
				try {
					this.client.currentScreen.render(matrixStack2, i, j, this.client.getLastFrameDuration());
				} catch (Throwable var14) {
					CrashReport crashReport = CrashReport.create(var14, "Rendering screen");
					CrashReportSection crashReportSection = crashReport.addElement("Screen render details");
					crashReportSection.add("Screen name", (CrashCallable<String>)(() -> this.client.currentScreen.getClass().getCanonicalName()));
					crashReportSection.add(
						"Mouse location",
						(CrashCallable<String>)(() -> String.format(Locale.ROOT, "Scaled: (%d, %d). Absolute: (%f, %f)", i, j, this.client.mouse.getX(), this.client.mouse.getY()))
					);
					crashReportSection.add(
						"Screen size",
						(CrashCallable<String>)(() -> String.format(
								Locale.ROOT,
								"Scaled: (%d, %d). Absolute: (%d, %d). Scale factor of %f",
								this.client.getWindow().getScaledWidth(),
								this.client.getWindow().getScaledHeight(),
								this.client.getWindow().getFramebufferWidth(),
								this.client.getWindow().getFramebufferHeight(),
								this.client.getWindow().getScaleFactor()
							))
					);
					throw new CrashException(crashReport);
				}
			}
		}
	}

	private void updateWorldIcon() {
		if (this.client.worldRenderer.getCompletedChunkCount() > 10 && this.client.worldRenderer.isTerrainRenderComplete() && !this.client.getServer().hasIconFile()) {
			NativeImage nativeImage = ScreenshotUtils.takeScreenshot(
				this.client.getWindow().getFramebufferWidth(), this.client.getWindow().getFramebufferHeight(), this.client.getFramebuffer()
			);
			Util.getIoWorkerExecutor().execute(() -> {
				int i = nativeImage.getWidth();
				int j = nativeImage.getHeight();
				int k = 0;
				int l = 0;
				if (i > j) {
					k = (i - j) / 2;
					i = j;
				} else {
					l = (j - i) / 2;
					j = i;
				}

				try (NativeImage nativeImage2 = new NativeImage(64, 64, false)) {
					nativeImage.resizeSubRectTo(k, l, i, j, nativeImage2);
					nativeImage2.writeFile(this.client.getServer().getIconFile());
				} catch (IOException var27) {
					LOGGER.warn("Couldn't save auto screenshot", (Throwable)var27);
				} finally {
					nativeImage.close();
				}
			});
		}
	}

	private boolean shouldRenderBlockOutline() {
		if (!this.blockOutlineEnabled) {
			return false;
		} else {
			Entity entity = this.client.getCameraEntity();
			boolean bl = entity instanceof PlayerEntity && !this.client.options.hudHidden;
			if (bl && !((PlayerEntity)entity).getAbilities().allowModifyWorld) {
				ItemStack itemStack = ((LivingEntity)entity).getMainHandStack();
				HitResult hitResult = this.client.crosshairTarget;
				if (hitResult != null && hitResult.getType() == HitResult.Type.BLOCK) {
					BlockPos blockPos = ((BlockHitResult)hitResult).getBlockPos();
					BlockState blockState = this.client.world.getBlockState(blockPos);
					if (this.client.interactionManager.getCurrentGameMode() == GameMode.SPECTATOR) {
						bl = blockState.createScreenHandlerFactory(this.client.world, blockPos) != null;
					} else {
						CachedBlockPosition cachedBlockPosition = new CachedBlockPosition(this.client.world, blockPos, false);
						bl = !itemStack.isEmpty()
							&& (
								itemStack.canDestroy(this.client.world.getTagManager(), cachedBlockPosition)
									|| itemStack.canPlaceOn(this.client.world.getTagManager(), cachedBlockPosition)
							);
					}
				}
			}

			return bl;
		}
	}

	public void renderWorld(float tickDelta, long limitTime, MatrixStack matrix) {
		this.lightmapTextureManager.update(tickDelta);
		if (this.client.getCameraEntity() == null) {
			this.client.setCameraEntity(this.client.player);
		}

		this.updateTargetedEntity(tickDelta);
		this.client.getProfiler().push("center");
		boolean bl = this.shouldRenderBlockOutline();
		this.client.getProfiler().swap("camera");
		Camera camera = this.camera;
		this.viewDistance = (float)(this.client.options.viewDistance * 16);
		MatrixStack matrixStack = new MatrixStack();
		double d = this.getFov(camera, tickDelta, true);
		matrixStack.peek().getModel().multiply(this.getBasicProjectionMatrix(d));
		this.bobViewWhenHurt(matrixStack, tickDelta);
		if (this.client.options.bobView) {
			this.bobView(matrixStack, tickDelta);
		}

		float f = MathHelper.lerp(tickDelta, this.client.player.lastNauseaStrength, this.client.player.nextNauseaStrength)
			* this.client.options.distortionEffectScale
			* this.client.options.distortionEffectScale;
		if (f > 0.0F) {
			int i = this.client.player.hasStatusEffect(StatusEffects.NAUSEA) ? 7 : 20;
			float g = 5.0F / (f * f + 5.0F) - f * 0.04F;
			g *= g;
			Vec3f vec3f = new Vec3f(0.0F, MathHelper.SQUARE_ROOT_OF_TWO / 2.0F, MathHelper.SQUARE_ROOT_OF_TWO / 2.0F);
			matrixStack.multiply(vec3f.getDegreesQuaternion(((float)this.ticks + tickDelta) * (float)i));
			matrixStack.scale(1.0F / g, 1.0F, 1.0F);
			float h = -((float)this.ticks + tickDelta) * (float)i;
			matrixStack.multiply(vec3f.getDegreesQuaternion(h));
		}

		Matrix4f matrix4f = matrixStack.peek().getModel();
		this.loadProjectionMatrix(matrix4f);
		camera.update(
			this.client.world,
			(Entity)(this.client.getCameraEntity() == null ? this.client.player : this.client.getCameraEntity()),
			!this.client.options.getPerspective().isFirstPerson(),
			this.client.options.getPerspective().isFrontView(),
			tickDelta
		);
		matrix.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(camera.getPitch()));
		matrix.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(camera.getYaw() + 180.0F));
		this.client.worldRenderer.method_32133(matrix, camera.getPos(), this.getBasicProjectionMatrix(Math.max(d, this.client.options.fov)));
		this.client.worldRenderer.render(matrix, tickDelta, limitTime, bl, camera, this, this.lightmapTextureManager, matrix4f);
		this.client.getProfiler().swap("hand");
		if (this.renderHand) {
			RenderSystem.clear(256, MinecraftClient.IS_SYSTEM_MAC);
			this.renderHand(matrix, camera, tickDelta);
		}

		this.client.getProfiler().pop();
	}

	public void reset() {
		this.floatingItem = null;
		this.mapRenderer.clearStateTextures();
		this.camera.reset();
	}

	public MapRenderer getMapRenderer() {
		return this.mapRenderer;
	}

	public void showFloatingItem(ItemStack floatingItem) {
		this.floatingItem = floatingItem;
		this.floatingItemTimeLeft = 40;
		this.floatingItemWidth = this.random.nextFloat() * 2.0F - 1.0F;
		this.floatingItemHeight = this.random.nextFloat() * 2.0F - 1.0F;
	}

	private void renderFloatingItem(int scaledWidth, int scaledHeight, float tickDelta) {
		if (this.floatingItem != null && this.floatingItemTimeLeft > 0) {
			int i = 40 - this.floatingItemTimeLeft;
			float f = ((float)i + tickDelta) / 40.0F;
			float g = f * f;
			float h = f * g;
			float j = 10.25F * h * g - 24.95F * g * g + 25.5F * h - 13.8F * g + 4.0F * f;
			float k = j * (float) Math.PI;
			float l = this.floatingItemWidth * (float)(scaledWidth / 4);
			float m = this.floatingItemHeight * (float)(scaledHeight / 4);
			RenderSystem.enableDepthTest();
			RenderSystem.disableCull();
			MatrixStack matrixStack = new MatrixStack();
			matrixStack.push();
			matrixStack.translate(
				(double)((float)(scaledWidth / 2) + l * MathHelper.abs(MathHelper.sin(k * 2.0F))),
				(double)((float)(scaledHeight / 2) + m * MathHelper.abs(MathHelper.sin(k * 2.0F))),
				-50.0
			);
			float n = 50.0F + 175.0F * MathHelper.sin(k);
			matrixStack.scale(n, -n, n);
			matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(900.0F * MathHelper.abs(MathHelper.sin(k))));
			matrixStack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(6.0F * MathHelper.cos(f * 8.0F)));
			matrixStack.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(6.0F * MathHelper.cos(f * 8.0F)));
			VertexConsumerProvider.Immediate immediate = this.buffers.getEntityVertexConsumers();
			this.client.getItemRenderer().renderItem(this.floatingItem, ModelTransformation.Mode.FIXED, 15728880, OverlayTexture.DEFAULT_UV, matrixStack, immediate, 0);
			matrixStack.pop();
			immediate.draw();
			RenderSystem.enableCull();
			RenderSystem.disableDepthTest();
		}
	}

	private void method_31136(float f) {
		int i = this.client.getWindow().getScaledWidth();
		int j = this.client.getWindow().getScaledHeight();
		double d = MathHelper.lerp((double)f, 2.0, 1.0);
		float g = 0.2F * f;
		float h = 0.4F * f;
		float k = 0.2F * f;
		double e = (double)i * d;
		double l = (double)j * d;
		double m = ((double)i - e) / 2.0;
		double n = ((double)j - l) / 2.0;
		RenderSystem.disableDepthTest();
		RenderSystem.depthMask(false);
		RenderSystem.enableBlend();
		RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ONE, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ONE);
		RenderSystem.setShaderColor(g, h, k, 1.0F);
		RenderSystem.setShaderTexture(0, NAUSEA_OVERLAY);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
		bufferBuilder.vertex(m, n + l, -90.0).texture(0.0F, 1.0F).next();
		bufferBuilder.vertex(m + e, n + l, -90.0).texture(1.0F, 1.0F).next();
		bufferBuilder.vertex(m + e, n, -90.0).texture(1.0F, 0.0F).next();
		bufferBuilder.vertex(m, n, -90.0).texture(0.0F, 0.0F).next();
		tessellator.draw();
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.defaultBlendFunc();
		RenderSystem.disableBlend();
		RenderSystem.depthMask(true);
		RenderSystem.enableDepthTest();
	}

	public float getSkyDarkness(float tickDelta) {
		return MathHelper.lerp(tickDelta, this.lastSkyDarkness, this.skyDarkness);
	}

	public float getViewDistance() {
		return this.viewDistance;
	}

	public Camera getCamera() {
		return this.camera;
	}

	public LightmapTextureManager getLightmapTextureManager() {
		return this.lightmapTextureManager;
	}

	public OverlayTexture getOverlayTexture() {
		return this.overlayTexture;
	}

	@Nullable
	public static class_5944 method_34539() {
		return field_29351;
	}

	@Nullable
	public static class_5944 method_34540() {
		return field_29352;
	}

	@Nullable
	public static class_5944 method_34541() {
		return field_29353;
	}

	@Nullable
	public static class_5944 method_34542() {
		return field_29354;
	}

	@Nullable
	public static class_5944 method_34543() {
		return field_29355;
	}

	@Nullable
	public static class_5944 method_34544() {
		return field_29356;
	}

	@Nullable
	public static class_5944 method_34545() {
		return field_29357;
	}

	@Nullable
	public static class_5944 method_34546() {
		return field_29358;
	}

	@Nullable
	public static class_5944 method_34547() {
		return field_29359;
	}

	@Nullable
	public static class_5944 method_34548() {
		return field_29360;
	}

	@Nullable
	public static class_5944 method_34549() {
		return field_29361;
	}

	@Nullable
	public static class_5944 method_34495() {
		return field_29363;
	}

	@Nullable
	public static class_5944 method_34496() {
		return field_29364;
	}

	@Nullable
	public static class_5944 method_34497() {
		return field_29365;
	}

	@Nullable
	public static class_5944 method_34498() {
		return field_29366;
	}

	@Nullable
	public static class_5944 method_34499() {
		return field_29377;
	}

	@Nullable
	public static class_5944 method_34500() {
		return field_29378;
	}

	@Nullable
	public static class_5944 method_34501() {
		return field_29379;
	}

	@Nullable
	public static class_5944 method_34502() {
		return field_29380;
	}

	@Nullable
	public static class_5944 method_34503() {
		return field_29381;
	}

	@Nullable
	public static class_5944 method_34504() {
		return field_29382;
	}

	@Nullable
	public static class_5944 method_34505() {
		return field_29383;
	}

	@Nullable
	public static class_5944 method_34506() {
		return field_29384;
	}

	@Nullable
	public static class_5944 method_34507() {
		return field_29385;
	}

	@Nullable
	public static class_5944 method_34508() {
		return field_29386;
	}

	@Nullable
	public static class_5944 method_34509() {
		return field_29387;
	}

	@Nullable
	public static class_5944 method_34510() {
		return field_29388;
	}

	@Nullable
	public static class_5944 method_34511() {
		return field_29389;
	}

	@Nullable
	public static class_5944 method_34512() {
		return field_29390;
	}

	@Nullable
	public static class_5944 method_34513() {
		return field_29391;
	}

	@Nullable
	public static class_5944 method_34514() {
		return field_29392;
	}

	@Nullable
	public static class_5944 method_34515() {
		return field_29393;
	}

	@Nullable
	public static class_5944 method_34516() {
		return field_29394;
	}

	@Nullable
	public static class_5944 method_34517() {
		return field_29395;
	}

	@Nullable
	public static class_5944 method_34518() {
		return field_29396;
	}

	@Nullable
	public static class_5944 method_34519() {
		return field_29397;
	}

	@Nullable
	public static class_5944 method_34520() {
		return field_29398;
	}

	@Nullable
	public static class_5944 method_34523() {
		return field_29399;
	}

	@Nullable
	public static class_5944 method_34524() {
		return field_29400;
	}

	@Nullable
	public static class_5944 method_34525() {
		return field_29401;
	}

	@Nullable
	public static class_5944 method_34526() {
		return field_29402;
	}

	@Nullable
	public static class_5944 method_34527() {
		return field_29367;
	}

	@Nullable
	public static class_5944 method_34528() {
		return field_29368;
	}

	@Nullable
	public static class_5944 method_34529() {
		return field_29369;
	}

	@Nullable
	public static class_5944 method_34530() {
		return field_29370;
	}

	@Nullable
	public static class_5944 method_34531() {
		return field_29371;
	}

	@Nullable
	public static class_5944 method_34532() {
		return field_29372;
	}

	@Nullable
	public static class_5944 method_34533() {
		return field_29373;
	}

	@Nullable
	public static class_5944 method_34534() {
		return field_29374;
	}

	@Nullable
	public static class_5944 method_34535() {
		return field_29375;
	}

	@Nullable
	public static class_5944 method_34536() {
		return field_29376;
	}
}
