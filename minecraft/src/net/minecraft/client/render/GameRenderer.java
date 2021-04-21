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
import net.minecraft.resource.SynchronousResourceReloader;
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
public class GameRenderer implements SynchronousResourceReloader, AutoCloseable {
	private static final Identifier NAUSEA_OVERLAY = new Identifier("textures/misc/nausea.png");
	private static final Logger LOGGER = LogManager.getLogger();
	private static final boolean field_32688 = false;
	public static final float field_32686 = 0.05F;
	private final MinecraftClient client;
	private final ResourceManager resourceManager;
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
	public static final int field_32687 = 40;
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
	public Shader blitScreenShader;
	private final Map<String, Shader> shaders = Maps.<String, Shader>newHashMap();
	@Nullable
	private static Shader positionShader;
	@Nullable
	private static Shader positionColorShader;
	@Nullable
	private static Shader positionColorTexShader;
	@Nullable
	private static Shader positionTexShader;
	@Nullable
	private static Shader positionTexColorShader;
	@Nullable
	private static Shader blockShader;
	@Nullable
	private static Shader newEntityShader;
	@Nullable
	private static Shader particleShader;
	@Nullable
	private static Shader positionColorLightmapShader;
	@Nullable
	private static Shader positionColorTexLightmapShader;
	@Nullable
	private static Shader positionTexColorNormalShader;
	@Nullable
	private static Shader positionTexLightmapColorShader;
	@Nullable
	private static Shader renderTypeSolidShader;
	@Nullable
	private static Shader renderTypeCutoutMippedShader;
	@Nullable
	private static Shader renderTypeCutoutShader;
	@Nullable
	private static Shader renderTypeTranslucentShader;
	@Nullable
	private static Shader renderTypeTranslucentMovingBlockShader;
	@Nullable
	private static Shader renderTypeTranslucentNoCrumblingShader;
	@Nullable
	private static Shader renderTypeArmorCutoutNoCullShader;
	@Nullable
	private static Shader renderTypeEntitySolidShader;
	@Nullable
	private static Shader renderTypeEntityCutoutShader;
	@Nullable
	private static Shader renderTypeEntityCutoutNoNullShader;
	@Nullable
	private static Shader renderTypeEntityCutoutNoNullZOffsetShader;
	@Nullable
	private static Shader renderTypeItemEntityTranslucentCullShader;
	@Nullable
	private static Shader renderTypeEntityTranslucentCullShader;
	@Nullable
	private static Shader renderTypeEntityTranslucentShader;
	@Nullable
	private static Shader renderTypeEntitySmoothCutoutShader;
	@Nullable
	private static Shader renderTypeBeaconBeamShader;
	@Nullable
	private static Shader renderTypeEntityDecalShader;
	@Nullable
	private static Shader renderTypeEntityNoOutlineShader;
	@Nullable
	private static Shader renderTypeEntityShadowShader;
	@Nullable
	private static Shader renderTypeEntityAlphaShader;
	@Nullable
	private static Shader renderTypeEyesShader;
	@Nullable
	private static Shader renderTypeEnergySwirlShader;
	@Nullable
	private static Shader renderTypeLeashShader;
	@Nullable
	private static Shader renderTypeWaterMaskShader;
	@Nullable
	private static Shader renderTypeOutlineShader;
	@Nullable
	private static Shader renderTypeArmorGlintShader;
	@Nullable
	private static Shader renderTypeArmorEntityGlintShader;
	@Nullable
	private static Shader renderTypeGlintTranslucentShader;
	@Nullable
	private static Shader renderTypeGlintShader;
	@Nullable
	private static Shader renderTypeGlintDirectShader;
	@Nullable
	private static Shader renderTypeEntityGlintShader;
	@Nullable
	private static Shader renderTypeEntityGlintDirectShader;
	@Nullable
	private static Shader renderTypeTextShader;
	@Nullable
	private static Shader field_33626;
	@Nullable
	private static Shader renderTypeTextSeeThroughShader;
	@Nullable
	private static Shader field_33627;
	@Nullable
	private static Shader renderTypeLightningShader;
	@Nullable
	private static Shader renderTypeTripwireShader;
	@Nullable
	private static Shader renderTypeEndPortalShader;
	@Nullable
	private static Shader renderTypeEndGatewayShader;
	@Nullable
	private static Shader renderTypeLinesShader;
	@Nullable
	private static Shader renderTypeCrumblingShader;

	public GameRenderer(MinecraftClient client, ResourceManager resourceManager, BufferBuilderStorage buffers) {
		this.client = client;
		this.resourceManager = resourceManager;
		this.firstPersonRenderer = client.getHeldItemRenderer();
		this.mapRenderer = new MapRenderer(client.getTextureManager());
		this.lightmapTextureManager = new LightmapTextureManager(this, client);
		this.buffers = buffers;
		this.shader = null;
	}

	public void close() {
		this.lightmapTextureManager.close();
		this.mapRenderer.close();
		this.overlayTexture.close();
		this.disableShader();
		this.clearShaders();
		if (this.blitScreenShader != null) {
			this.blitScreenShader.close();
		}
	}

	public void method_35768(boolean bl) {
		this.renderHand = bl;
	}

	public void method_35769(boolean bl) {
		this.blockOutlineEnabled = bl;
	}

	public void method_35770(boolean bl) {
		this.renderingPanorama = bl;
	}

	public boolean method_35765() {
		return this.renderingPanorama;
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

	public void method_35771() {
		if (this.client.getCameraEntity() instanceof PlayerEntity) {
			if (this.shader != null) {
				this.shader.close();
			}

			this.forcedShaderIndex = (this.forcedShaderIndex + 1) % (SHADERS_LOCATIONS.length + 1);
			if (this.forcedShaderIndex == SHADER_COUNT) {
				this.shader = null;
			} else {
				this.loadShader(SHADERS_LOCATIONS[this.forcedShaderIndex]);
			}
		}
	}

	private void loadShader(Identifier id) {
		if (this.shader != null) {
			this.shader.close();
		}

		try {
			this.shader = new ShaderEffect(this.client.getTextureManager(), this.resourceManager, this.client.getFramebuffer(), id);
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
	public void reload(ResourceManager manager) {
		this.loadShaders(manager);
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

	public void preloadShaders(ResourceFactory factory) {
		if (this.blitScreenShader != null) {
			throw new RuntimeException("Blit shader already preloaded");
		} else {
			try {
				this.blitScreenShader = new Shader(factory, "blit_screen", VertexFormats.BLIT_SCREEN);
				positionShader = this.loadShader(factory, "position", VertexFormats.POSITION);
				positionColorShader = this.loadShader(factory, "position_color", VertexFormats.POSITION_COLOR);
				positionColorTexShader = this.loadShader(factory, "position_color_tex", VertexFormats.POSITION_COLOR_TEXTURE);
				positionTexShader = this.loadShader(factory, "position_tex", VertexFormats.POSITION_TEXTURE);
				positionTexColorShader = this.loadShader(factory, "position_tex_color", VertexFormats.POSITION_TEXTURE_COLOR);
			} catch (IOException var3) {
				throw new RuntimeException("could not preload blit shader", var3);
			}
		}
	}

	private Shader loadShader(ResourceFactory factory, String name, VertexFormat vertexFormat) throws IOException {
		Shader shader = new Shader(factory, name, vertexFormat);
		this.shaders.put(name, shader);
		return shader;
	}

	public void loadShaders(ResourceManager manager) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		this.clearShaders();

		try {
			blockShader = this.loadShader(manager, "block", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL);
			newEntityShader = this.loadShader(manager, "new_entity", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL);
			particleShader = this.loadShader(manager, "particle", VertexFormats.POSITION_TEXTURE_COLOR_LIGHT);
			positionShader = this.loadShader(manager, "position", VertexFormats.POSITION);
			positionColorShader = this.loadShader(manager, "position_color", VertexFormats.POSITION_COLOR);
			positionColorLightmapShader = this.loadShader(manager, "position_color_lightmap", VertexFormats.POSITION_COLOR_LIGHT);
			positionColorTexShader = this.loadShader(manager, "position_color_tex", VertexFormats.POSITION_COLOR_TEXTURE);
			positionColorTexLightmapShader = this.loadShader(manager, "position_color_tex_lightmap", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT);
			positionTexShader = this.loadShader(manager, "position_tex", VertexFormats.POSITION_TEXTURE);
			positionTexColorShader = this.loadShader(manager, "position_tex_color", VertexFormats.POSITION_TEXTURE_COLOR);
			positionTexColorNormalShader = this.loadShader(manager, "position_tex_color_normal", VertexFormats.POSITION_TEXTURE_COLOR_NORMAL);
			positionTexLightmapColorShader = this.loadShader(manager, "position_tex_lightmap_color", VertexFormats.POSITION_TEXTURE_LIGHT_COLOR);
			renderTypeSolidShader = this.loadShader(manager, "rendertype_solid", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL);
			renderTypeCutoutMippedShader = this.loadShader(manager, "rendertype_cutout_mipped", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL);
			renderTypeCutoutShader = this.loadShader(manager, "rendertype_cutout", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL);
			renderTypeTranslucentShader = this.loadShader(manager, "rendertype_translucent", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL);
			renderTypeTranslucentMovingBlockShader = this.loadShader(manager, "rendertype_translucent_moving_block", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL);
			renderTypeTranslucentNoCrumblingShader = this.loadShader(manager, "rendertype_translucent_no_crumbling", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL);
			renderTypeArmorCutoutNoCullShader = this.loadShader(manager, "rendertype_armor_cutout_no_cull", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL);
			renderTypeEntitySolidShader = this.loadShader(manager, "rendertype_entity_solid", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL);
			renderTypeEntityCutoutShader = this.loadShader(manager, "rendertype_entity_cutout", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL);
			renderTypeEntityCutoutNoNullShader = this.loadShader(manager, "rendertype_entity_cutout_no_cull", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL);
			renderTypeEntityCutoutNoNullZOffsetShader = this.loadShader(
				manager, "rendertype_entity_cutout_no_cull_z_offset", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL
			);
			renderTypeItemEntityTranslucentCullShader = this.loadShader(
				manager, "rendertype_item_entity_translucent_cull", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL
			);
			renderTypeEntityTranslucentCullShader = this.loadShader(
				manager, "rendertype_entity_translucent_cull", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL
			);
			renderTypeEntityTranslucentShader = this.loadShader(manager, "rendertype_entity_translucent", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL);
			renderTypeEntitySmoothCutoutShader = this.loadShader(manager, "rendertype_entity_smooth_cutout", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL);
			renderTypeBeaconBeamShader = this.loadShader(manager, "rendertype_beacon_beam", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL);
			renderTypeEntityDecalShader = this.loadShader(manager, "rendertype_entity_decal", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL);
			renderTypeEntityNoOutlineShader = this.loadShader(manager, "rendertype_entity_no_outline", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL);
			renderTypeEntityShadowShader = this.loadShader(manager, "rendertype_entity_shadow", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL);
			renderTypeEntityAlphaShader = this.loadShader(manager, "rendertype_entity_alpha", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL);
			renderTypeEyesShader = this.loadShader(manager, "rendertype_eyes", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL);
			renderTypeEnergySwirlShader = this.loadShader(manager, "rendertype_energy_swirl", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL);
			renderTypeLeashShader = this.loadShader(manager, "rendertype_leash", VertexFormats.POSITION_COLOR_LIGHT);
			renderTypeWaterMaskShader = this.loadShader(manager, "rendertype_water_mask", VertexFormats.POSITION);
			renderTypeOutlineShader = this.loadShader(manager, "rendertype_outline", VertexFormats.POSITION_COLOR_TEXTURE);
			renderTypeArmorGlintShader = this.loadShader(manager, "rendertype_armor_glint", VertexFormats.POSITION_TEXTURE);
			renderTypeArmorEntityGlintShader = this.loadShader(manager, "rendertype_armor_entity_glint", VertexFormats.POSITION_TEXTURE);
			renderTypeGlintTranslucentShader = this.loadShader(manager, "rendertype_glint_translucent", VertexFormats.POSITION_TEXTURE);
			renderTypeGlintShader = this.loadShader(manager, "rendertype_glint", VertexFormats.POSITION_TEXTURE);
			renderTypeGlintDirectShader = this.loadShader(manager, "rendertype_glint_direct", VertexFormats.POSITION_TEXTURE);
			renderTypeEntityGlintShader = this.loadShader(manager, "rendertype_entity_glint", VertexFormats.POSITION_TEXTURE);
			renderTypeEntityGlintDirectShader = this.loadShader(manager, "rendertype_entity_glint_direct", VertexFormats.POSITION_TEXTURE);
			renderTypeTextShader = this.loadShader(manager, "rendertype_text", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT);
			field_33626 = this.loadShader(manager, "rendertype_text_intensity", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT);
			renderTypeTextSeeThroughShader = this.loadShader(manager, "rendertype_text_see_through", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT);
			field_33627 = this.loadShader(manager, "rendertype_text_intensity_see_through", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT);
			renderTypeLightningShader = this.loadShader(manager, "rendertype_lightning", VertexFormats.POSITION_COLOR);
			renderTypeTripwireShader = this.loadShader(manager, "rendertype_tripwire", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL);
			renderTypeEndPortalShader = this.loadShader(manager, "rendertype_end_portal", VertexFormats.POSITION);
			renderTypeEndGatewayShader = this.loadShader(manager, "rendertype_end_gateway", VertexFormats.POSITION);
			renderTypeLinesShader = this.loadShader(manager, "rendertype_lines", VertexFormats.LINES);
			renderTypeCrumblingShader = this.loadShader(manager, "rendertype_crumbling", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL);
		} catch (IOException var3) {
			throw new RuntimeException("could not reload shaders", var3);
		}
	}

	private void clearShaders() {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		this.shaders.values().forEach(Shader::close);
		this.shaders.clear();
	}

	@Nullable
	public Shader method_35767(@Nullable String string) {
		return string == null ? null : (Shader)this.shaders.get(string);
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

	public void onResized(int width, int height) {
		if (this.shader != null) {
			this.shader.setupDimensions(width, height);
		}

		this.client.worldRenderer.onResized(width, height);
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

	public void method_35766(float f, float g, float h) {
		this.zoom = f;
		this.zoomX = g;
		this.zoomY = h;
		this.method_35769(false);
		this.method_35768(false);
		this.renderWorld(1.0F, 0L, new MatrixStack());
		this.zoom = 1.0F;
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

			if (this.client.getOverlay() != null) {
				try {
					this.client.getOverlay().render(matrixStack2, i, j, this.client.getLastFrameDuration());
				} catch (Throwable var15) {
					CrashReport crashReport = CrashReport.create(var15, "Rendering overlay");
					CrashReportSection crashReportSection = crashReport.addElement("Overlay render details");
					crashReportSection.add("Overlay name", (CrashCallable<String>)(() -> this.client.getOverlay().getClass().getCanonicalName()));
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
		this.client.worldRenderer.setupFrustum(matrix, camera.getPos(), this.getBasicProjectionMatrix(Math.max(d, this.client.options.fov)));
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
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
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

	public MinecraftClient method_35772() {
		return this.client;
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
	public static Shader getPositionShader() {
		return positionShader;
	}

	@Nullable
	public static Shader getPositionColorShader() {
		return positionColorShader;
	}

	@Nullable
	public static Shader getPositionColorTexShader() {
		return positionColorTexShader;
	}

	@Nullable
	public static Shader getPositionTexShader() {
		return positionTexShader;
	}

	@Nullable
	public static Shader getPositionTexColorShader() {
		return positionTexColorShader;
	}

	@Nullable
	public static Shader getBlockShader() {
		return blockShader;
	}

	@Nullable
	public static Shader getNewEntityShader() {
		return newEntityShader;
	}

	@Nullable
	public static Shader getParticleShader() {
		return particleShader;
	}

	@Nullable
	public static Shader getPositionColorLightmapShader() {
		return positionColorLightmapShader;
	}

	@Nullable
	public static Shader getPositionColorTexLightmapShader() {
		return positionColorTexLightmapShader;
	}

	@Nullable
	public static Shader getPositionTexColorNormalShader() {
		return positionTexColorNormalShader;
	}

	@Nullable
	public static Shader method_35764() {
		return positionTexLightmapColorShader;
	}

	@Nullable
	public static Shader getRenderTypeSolidShader() {
		return renderTypeSolidShader;
	}

	@Nullable
	public static Shader getRenderTypeCutoutMippedShader() {
		return renderTypeCutoutMippedShader;
	}

	@Nullable
	public static Shader getRenderTypeCutoutShader() {
		return renderTypeCutoutShader;
	}

	@Nullable
	public static Shader getRenderTypeTranslucentShader() {
		return renderTypeTranslucentShader;
	}

	@Nullable
	public static Shader getRenderTypeTranslucentMovingBlockShader() {
		return renderTypeTranslucentMovingBlockShader;
	}

	@Nullable
	public static Shader getRenderTypeTranslucentNoCrumblingShader() {
		return renderTypeTranslucentNoCrumblingShader;
	}

	@Nullable
	public static Shader getRenderTypeArmorCutoutNoCullShader() {
		return renderTypeArmorCutoutNoCullShader;
	}

	@Nullable
	public static Shader getRenderTypeEntitySolidShader() {
		return renderTypeEntitySolidShader;
	}

	@Nullable
	public static Shader getRenderTypeEntityCutoutShader() {
		return renderTypeEntityCutoutShader;
	}

	@Nullable
	public static Shader getRenderTypeEntityCutoutNoNullShader() {
		return renderTypeEntityCutoutNoNullShader;
	}

	@Nullable
	public static Shader getRenderTypeEntityCutoutNoNullZOffsetShader() {
		return renderTypeEntityCutoutNoNullZOffsetShader;
	}

	@Nullable
	public static Shader getRenderTypeItemEntityTranslucentCullShader() {
		return renderTypeItemEntityTranslucentCullShader;
	}

	@Nullable
	public static Shader getRenderTypeEntityTranslucentCullShader() {
		return renderTypeEntityTranslucentCullShader;
	}

	@Nullable
	public static Shader getRenderTypeEntityTranslucentShader() {
		return renderTypeEntityTranslucentShader;
	}

	@Nullable
	public static Shader getRenderTypeEntitySmoothCutoutShader() {
		return renderTypeEntitySmoothCutoutShader;
	}

	@Nullable
	public static Shader getRenderTypeBeaconBeamShader() {
		return renderTypeBeaconBeamShader;
	}

	@Nullable
	public static Shader getRenderTypeEntityDecalShader() {
		return renderTypeEntityDecalShader;
	}

	@Nullable
	public static Shader getRenderTypeEntityNoOutlineShader() {
		return renderTypeEntityNoOutlineShader;
	}

	@Nullable
	public static Shader getRenderTypeEntityShadowShader() {
		return renderTypeEntityShadowShader;
	}

	@Nullable
	public static Shader getRenderTypeEntityAlphaShader() {
		return renderTypeEntityAlphaShader;
	}

	@Nullable
	public static Shader getRenderTypeEyesShader() {
		return renderTypeEyesShader;
	}

	@Nullable
	public static Shader getRenderTypeEnergySwirlShader() {
		return renderTypeEnergySwirlShader;
	}

	@Nullable
	public static Shader getRenderTypeLeashShader() {
		return renderTypeLeashShader;
	}

	@Nullable
	public static Shader getRenderTypeWaterMaskShader() {
		return renderTypeWaterMaskShader;
	}

	@Nullable
	public static Shader getRenderTypeOutlineShader() {
		return renderTypeOutlineShader;
	}

	@Nullable
	public static Shader getRenderTypeArmorGlintShader() {
		return renderTypeArmorGlintShader;
	}

	@Nullable
	public static Shader getRenderTypeArmorEntityGlintShader() {
		return renderTypeArmorEntityGlintShader;
	}

	@Nullable
	public static Shader getRenderTypeGlintTranslucentShader() {
		return renderTypeGlintTranslucentShader;
	}

	@Nullable
	public static Shader getRenderTypeGlintShader() {
		return renderTypeGlintShader;
	}

	@Nullable
	public static Shader getRenderTypeGlintDirectShader() {
		return renderTypeGlintDirectShader;
	}

	@Nullable
	public static Shader getRenderTypeEntityGlintShader() {
		return renderTypeEntityGlintShader;
	}

	@Nullable
	public static Shader getRenderTypeEntityGlintDirectShader() {
		return renderTypeEntityGlintDirectShader;
	}

	@Nullable
	public static Shader getRenderTypeTextShader() {
		return renderTypeTextShader;
	}

	@Nullable
	public static Shader method_36432() {
		return field_33626;
	}

	@Nullable
	public static Shader getRenderTypeTextSeeThroughShader() {
		return renderTypeTextSeeThroughShader;
	}

	@Nullable
	public static Shader method_36433() {
		return field_33627;
	}

	@Nullable
	public static Shader getRenderTypeLightningShader() {
		return renderTypeLightningShader;
	}

	@Nullable
	public static Shader getRenderTypeTripwireShader() {
		return renderTypeTripwireShader;
	}

	@Nullable
	public static Shader getRenderTypeEndPortalShader() {
		return renderTypeEndPortalShader;
	}

	@Nullable
	public static Shader getRenderTypeEndGatewayShader() {
		return renderTypeEndGatewayShader;
	}

	@Nullable
	public static Shader getRenderTypeLinesShader() {
		return renderTypeLinesShader;
	}

	@Nullable
	public static Shader getRenderTypeCrumblingShader() {
		return renderTypeCrumblingShader;
	}
}
