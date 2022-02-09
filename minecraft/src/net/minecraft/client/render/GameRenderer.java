package net.minecraft.client.render;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Program;
import net.minecraft.client.gl.ShaderEffect;
import net.minecraft.client.gui.hud.InGameOverlayRenderer;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.util.ScreenshotRecorder;
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
import net.minecraft.server.integrated.IntegratedServer;
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
import net.minecraft.util.math.Matrix3f;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.GameMode;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class GameRenderer implements SynchronousResourceReloader, AutoCloseable {
	private static final Identifier NAUSEA_OVERLAY = new Identifier("textures/misc/nausea.png");
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final boolean field_32688 = false;
	/**
	 * Since the camera is conceptualized as a single point, a depth of {@value}
	 * blocks is used to define a rectangular area to be rendered.
	 * 
	 * @see Camera#getProjection()
	 * @see Matrix4f#viewboxMatrix
	 */
	public static final float CAMERA_DEPTH = 0.05F;
	private final MinecraftClient client;
	private final ResourceManager resourceManager;
	private final Random random = new Random();
	private float viewDistance;
	public final HeldItemRenderer firstPersonRenderer;
	private final MapRenderer mapRenderer;
	private final BufferBuilderStorage buffers;
	private int ticks;
	private float fovMultiplier;
	private float lastFovMultiplier;
	private float skyDarkness;
	private float lastSkyDarkness;
	private boolean renderHand = true;
	private boolean blockOutlineEnabled = true;
	private long lastWorldIconUpdate;
	private boolean hasWorldIcon;
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
	private static Shader renderTypeTextIntensityShader;
	@Nullable
	private static Shader renderTypeTextSeeThroughShader;
	@Nullable
	private static Shader renderTypeTextIntensitySeeThroughShader;
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

	public void setRenderHand(boolean renderHand) {
		this.renderHand = renderHand;
	}

	public void setBlockOutlineEnabled(boolean blockOutlineEnabled) {
		this.blockOutlineEnabled = blockOutlineEnabled;
	}

	public void setRenderingPanorama(boolean renderingPanorama) {
		this.renderingPanorama = renderingPanorama;
	}

	public boolean isRenderingPanorama() {
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

	public void loadForcedShader() {
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
			} catch (IOException var3) {
				throw new RuntimeException("could not preload blit shader", var3);
			}

			positionShader = this.loadShader(factory, "position", VertexFormats.POSITION);
			positionColorShader = this.loadShader(factory, "position_color", VertexFormats.POSITION_COLOR);
			positionColorTexShader = this.loadShader(factory, "position_color_tex", VertexFormats.POSITION_COLOR_TEXTURE);
			positionTexShader = this.loadShader(factory, "position_tex", VertexFormats.POSITION_TEXTURE);
			positionTexColorShader = this.loadShader(factory, "position_tex_color", VertexFormats.POSITION_TEXTURE_COLOR);
			renderTypeTextShader = this.loadShader(factory, "rendertype_text", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT);
		}
	}

	private Shader loadShader(ResourceFactory factory, String name, VertexFormat vertexFormat) {
		try {
			Shader shader = new Shader(factory, name, vertexFormat);
			this.shaders.put(name, shader);
			return shader;
		} catch (Exception var5) {
			throw new IllegalStateException("could not preload shader " + name, var5);
		}
	}

	public void loadShaders(ResourceManager manager) {
		RenderSystem.assertOnRenderThread();
		List<Program> list = Lists.<Program>newArrayList();
		list.addAll(Program.Type.FRAGMENT.getProgramCache().values());
		list.addAll(Program.Type.VERTEX.getProgramCache().values());
		list.forEach(Program::release);
		List<Pair<Shader, Consumer<Shader>>> list2 = Lists.<Pair<Shader, Consumer<Shader>>>newArrayListWithCapacity(this.shaders.size());

		try {
			list2.add(Pair.of(new Shader(manager, "block", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL), shader -> blockShader = shader));
			list2.add(Pair.of(new Shader(manager, "new_entity", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL), shader -> newEntityShader = shader));
			list2.add(Pair.of(new Shader(manager, "particle", VertexFormats.POSITION_TEXTURE_COLOR_LIGHT), shader -> particleShader = shader));
			list2.add(Pair.of(new Shader(manager, "position", VertexFormats.POSITION), shader -> positionShader = shader));
			list2.add(Pair.of(new Shader(manager, "position_color", VertexFormats.POSITION_COLOR), shader -> positionColorShader = shader));
			list2.add(Pair.of(new Shader(manager, "position_color_lightmap", VertexFormats.POSITION_COLOR_LIGHT), shader -> positionColorLightmapShader = shader));
			list2.add(Pair.of(new Shader(manager, "position_color_tex", VertexFormats.POSITION_COLOR_TEXTURE), shader -> positionColorTexShader = shader));
			list2.add(
				Pair.of(new Shader(manager, "position_color_tex_lightmap", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT), shader -> positionColorTexLightmapShader = shader)
			);
			list2.add(Pair.of(new Shader(manager, "position_tex", VertexFormats.POSITION_TEXTURE), shader -> positionTexShader = shader));
			list2.add(Pair.of(new Shader(manager, "position_tex_color", VertexFormats.POSITION_TEXTURE_COLOR), shader -> positionTexColorShader = shader));
			list2.add(
				Pair.of(new Shader(manager, "position_tex_color_normal", VertexFormats.POSITION_TEXTURE_COLOR_NORMAL), shader -> positionTexColorNormalShader = shader)
			);
			list2.add(
				Pair.of(new Shader(manager, "position_tex_lightmap_color", VertexFormats.POSITION_TEXTURE_LIGHT_COLOR), shader -> positionTexLightmapColorShader = shader)
			);
			list2.add(Pair.of(new Shader(manager, "rendertype_solid", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL), shader -> renderTypeSolidShader = shader));
			list2.add(
				Pair.of(new Shader(manager, "rendertype_cutout_mipped", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL), shader -> renderTypeCutoutMippedShader = shader)
			);
			list2.add(Pair.of(new Shader(manager, "rendertype_cutout", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL), shader -> renderTypeCutoutShader = shader));
			list2.add(
				Pair.of(new Shader(manager, "rendertype_translucent", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL), shader -> renderTypeTranslucentShader = shader)
			);
			list2.add(
				Pair.of(
					new Shader(manager, "rendertype_translucent_moving_block", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL),
					shader -> renderTypeTranslucentMovingBlockShader = shader
				)
			);
			list2.add(
				Pair.of(
					new Shader(manager, "rendertype_translucent_no_crumbling", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL),
					shader -> renderTypeTranslucentNoCrumblingShader = shader
				)
			);
			list2.add(
				Pair.of(
					new Shader(manager, "rendertype_armor_cutout_no_cull", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL),
					shader -> renderTypeArmorCutoutNoCullShader = shader
				)
			);
			list2.add(
				Pair.of(
					new Shader(manager, "rendertype_entity_solid", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL), shader -> renderTypeEntitySolidShader = shader
				)
			);
			list2.add(
				Pair.of(
					new Shader(manager, "rendertype_entity_cutout", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL),
					shader -> renderTypeEntityCutoutShader = shader
				)
			);
			list2.add(
				Pair.of(
					new Shader(manager, "rendertype_entity_cutout_no_cull", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL),
					shader -> renderTypeEntityCutoutNoNullShader = shader
				)
			);
			list2.add(
				Pair.of(
					new Shader(manager, "rendertype_entity_cutout_no_cull_z_offset", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL),
					shader -> renderTypeEntityCutoutNoNullZOffsetShader = shader
				)
			);
			list2.add(
				Pair.of(
					new Shader(manager, "rendertype_item_entity_translucent_cull", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL),
					shader -> renderTypeItemEntityTranslucentCullShader = shader
				)
			);
			list2.add(
				Pair.of(
					new Shader(manager, "rendertype_entity_translucent_cull", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL),
					shader -> renderTypeEntityTranslucentCullShader = shader
				)
			);
			list2.add(
				Pair.of(
					new Shader(manager, "rendertype_entity_translucent", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL),
					shader -> renderTypeEntityTranslucentShader = shader
				)
			);
			list2.add(
				Pair.of(
					new Shader(manager, "rendertype_entity_smooth_cutout", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL),
					shader -> renderTypeEntitySmoothCutoutShader = shader
				)
			);
			list2.add(
				Pair.of(new Shader(manager, "rendertype_beacon_beam", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL), shader -> renderTypeBeaconBeamShader = shader)
			);
			list2.add(
				Pair.of(
					new Shader(manager, "rendertype_entity_decal", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL), shader -> renderTypeEntityDecalShader = shader
				)
			);
			list2.add(
				Pair.of(
					new Shader(manager, "rendertype_entity_no_outline", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL),
					shader -> renderTypeEntityNoOutlineShader = shader
				)
			);
			list2.add(
				Pair.of(
					new Shader(manager, "rendertype_entity_shadow", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL),
					shader -> renderTypeEntityShadowShader = shader
				)
			);
			list2.add(
				Pair.of(
					new Shader(manager, "rendertype_entity_alpha", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL), shader -> renderTypeEntityAlphaShader = shader
				)
			);
			list2.add(
				Pair.of(new Shader(manager, "rendertype_eyes", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL), shader -> renderTypeEyesShader = shader)
			);
			list2.add(
				Pair.of(
					new Shader(manager, "rendertype_energy_swirl", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL), shader -> renderTypeEnergySwirlShader = shader
				)
			);
			list2.add(Pair.of(new Shader(manager, "rendertype_leash", VertexFormats.POSITION_COLOR_LIGHT), shader -> renderTypeLeashShader = shader));
			list2.add(Pair.of(new Shader(manager, "rendertype_water_mask", VertexFormats.POSITION), shader -> renderTypeWaterMaskShader = shader));
			list2.add(Pair.of(new Shader(manager, "rendertype_outline", VertexFormats.POSITION_COLOR_TEXTURE), shader -> renderTypeOutlineShader = shader));
			list2.add(Pair.of(new Shader(manager, "rendertype_armor_glint", VertexFormats.POSITION_TEXTURE), shader -> renderTypeArmorGlintShader = shader));
			list2.add(Pair.of(new Shader(manager, "rendertype_armor_entity_glint", VertexFormats.POSITION_TEXTURE), shader -> renderTypeArmorEntityGlintShader = shader));
			list2.add(Pair.of(new Shader(manager, "rendertype_glint_translucent", VertexFormats.POSITION_TEXTURE), shader -> renderTypeGlintTranslucentShader = shader));
			list2.add(Pair.of(new Shader(manager, "rendertype_glint", VertexFormats.POSITION_TEXTURE), shader -> renderTypeGlintShader = shader));
			list2.add(Pair.of(new Shader(manager, "rendertype_glint_direct", VertexFormats.POSITION_TEXTURE), shader -> renderTypeGlintDirectShader = shader));
			list2.add(Pair.of(new Shader(manager, "rendertype_entity_glint", VertexFormats.POSITION_TEXTURE), shader -> renderTypeEntityGlintShader = shader));
			list2.add(
				Pair.of(new Shader(manager, "rendertype_entity_glint_direct", VertexFormats.POSITION_TEXTURE), shader -> renderTypeEntityGlintDirectShader = shader)
			);
			list2.add(Pair.of(new Shader(manager, "rendertype_text", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT), shader -> renderTypeTextShader = shader));
			list2.add(
				Pair.of(new Shader(manager, "rendertype_text_intensity", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT), shader -> renderTypeTextIntensityShader = shader)
			);
			list2.add(
				Pair.of(new Shader(manager, "rendertype_text_see_through", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT), shader -> renderTypeTextSeeThroughShader = shader)
			);
			list2.add(
				Pair.of(
					new Shader(manager, "rendertype_text_intensity_see_through", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT),
					shader -> renderTypeTextIntensitySeeThroughShader = shader
				)
			);
			list2.add(Pair.of(new Shader(manager, "rendertype_lightning", VertexFormats.POSITION_COLOR), shader -> renderTypeLightningShader = shader));
			list2.add(
				Pair.of(new Shader(manager, "rendertype_tripwire", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL), shader -> renderTypeTripwireShader = shader)
			);
			list2.add(Pair.of(new Shader(manager, "rendertype_end_portal", VertexFormats.POSITION), shader -> renderTypeEndPortalShader = shader));
			list2.add(Pair.of(new Shader(manager, "rendertype_end_gateway", VertexFormats.POSITION), shader -> renderTypeEndGatewayShader = shader));
			list2.add(Pair.of(new Shader(manager, "rendertype_lines", VertexFormats.LINES), shader -> renderTypeLinesShader = shader));
			list2.add(
				Pair.of(new Shader(manager, "rendertype_crumbling", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL), shader -> renderTypeCrumblingShader = shader)
			);
		} catch (IOException var5) {
			list2.forEach(pair -> ((Shader)pair.getFirst()).close());
			throw new RuntimeException("could not reload shaders", var5);
		}

		this.clearShaders();
		list2.forEach(pair -> {
			Shader shader = (Shader)pair.getFirst();
			this.shaders.put(shader.getName(), shader);
			((Consumer)pair.getSecond()).accept(shader);
		});
	}

	private void clearShaders() {
		RenderSystem.assertOnRenderThread();
		this.shaders.values().forEach(Shader::close);
		this.shaders.clear();
	}

	@Nullable
	public Shader getShader(@Nullable String name) {
		return name == null ? null : (Shader)this.shaders.get(name);
	}

	public void tick() {
		this.updateFovMultiplier();
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

	private void updateFovMultiplier() {
		float f = 1.0F;
		if (this.client.getCameraEntity() instanceof AbstractClientPlayerEntity) {
			AbstractClientPlayerEntity abstractClientPlayerEntity = (AbstractClientPlayerEntity)this.client.getCameraEntity();
			f = abstractClientPlayerEntity.getFovMultiplier();
		}

		this.lastFovMultiplier = this.fovMultiplier;
		this.fovMultiplier = this.fovMultiplier + (f - this.fovMultiplier) * 0.5F;
		if (this.fovMultiplier > 1.5F) {
			this.fovMultiplier = 1.5F;
		}

		if (this.fovMultiplier < 0.1F) {
			this.fovMultiplier = 0.1F;
		}
	}

	private double getFov(Camera camera, float tickDelta, boolean changingFov) {
		if (this.renderingPanorama) {
			return 90.0;
		} else {
			double d = 70.0;
			if (changingFov) {
				d = this.client.options.fov;
				d *= (double)MathHelper.lerp(tickDelta, this.lastFovMultiplier, this.fovMultiplier);
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

	private void bobViewWhenHurt(MatrixStack matrices, float tickDelta) {
		if (this.client.getCameraEntity() instanceof LivingEntity) {
			LivingEntity livingEntity = (LivingEntity)this.client.getCameraEntity();
			float f = (float)livingEntity.hurtTime - tickDelta;
			if (livingEntity.isDead()) {
				float g = Math.min((float)livingEntity.deathTime + tickDelta, 20.0F);
				matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(40.0F - 8000.0F / (g + 200.0F)));
			}

			if (f < 0.0F) {
				return;
			}

			f /= (float)livingEntity.maxHurtTime;
			f = MathHelper.sin(f * f * f * f * (float) Math.PI);
			float g = livingEntity.knockbackVelocity;
			matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(-g));
			matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(-f * 14.0F));
			matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(g));
		}
	}

	private void bobView(MatrixStack matrices, float tickDelta) {
		if (this.client.getCameraEntity() instanceof PlayerEntity) {
			PlayerEntity playerEntity = (PlayerEntity)this.client.getCameraEntity();
			float f = playerEntity.horizontalSpeed - playerEntity.prevHorizontalSpeed;
			float g = -(playerEntity.horizontalSpeed + f * tickDelta);
			float h = MathHelper.lerp(tickDelta, playerEntity.prevStrideDistance, playerEntity.strideDistance);
			matrices.translate((double)(MathHelper.sin(g * (float) Math.PI) * h * 0.5F), (double)(-Math.abs(MathHelper.cos(g * (float) Math.PI) * h)), 0.0);
			matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(MathHelper.sin(g * (float) Math.PI) * h * 3.0F));
			matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(Math.abs(MathHelper.cos(g * (float) Math.PI - 0.2F) * h) * 5.0F));
		}
	}

	public void renderWithZoom(float zoom, float zoomX, float zoomY) {
		this.zoom = zoom;
		this.zoomX = zoomX;
		this.zoomY = zoomY;
		this.setBlockOutlineEnabled(false);
		this.setRenderHand(false);
		this.renderWorld(1.0F, 0L, new MatrixStack());
		this.zoom = 1.0F;
	}

	private void renderHand(MatrixStack matrices, Camera camera, float tickDelta) {
		if (!this.renderingPanorama) {
			this.loadProjectionMatrix(this.getBasicProjectionMatrix(this.getFov(camera, tickDelta, false)));
			MatrixStack.Entry entry = matrices.peek();
			entry.getPositionMatrix().loadIdentity();
			entry.getNormalMatrix().loadIdentity();
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

	public void loadProjectionMatrix(Matrix4f projectionMatrix) {
		RenderSystem.setProjectionMatrix(projectionMatrix);
	}

	public Matrix4f getBasicProjectionMatrix(double fov) {
		MatrixStack matrixStack = new MatrixStack();
		matrixStack.peek().getPositionMatrix().loadIdentity();
		if (this.zoom != 1.0F) {
			matrixStack.translate((double)this.zoomX, (double)(-this.zoomY), 0.0);
			matrixStack.scale(this.zoom, this.zoom, 1.0F);
		}

		matrixStack.peek()
			.getPositionMatrix()
			.multiply(
				Matrix4f.viewboxMatrix(
					fov, (float)this.client.getWindow().getFramebufferWidth() / (float)this.client.getWindow().getFramebufferHeight(), 0.05F, this.method_32796()
				)
			);
		return matrixStack.peek().getPositionMatrix();
	}

	public float method_32796() {
		return this.viewDistance * 4.0F;
	}

	public static float getNightVisionStrength(LivingEntity entity, float tickDelta) {
		int i = entity.getStatusEffect(StatusEffects.NIGHT_VISION).getDuration();
		return i > 200 ? 1.0F : 0.7F + MathHelper.sin(((float)i - tickDelta) * (float) Math.PI * 0.2F) * 0.3F;
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
				this.updateWorldIcon();
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
			Matrix4f matrix4f = Matrix4f.projectionMatrix(
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
						this.renderNausea(f * (1.0F - this.client.options.distortionEffectScale));
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
				} catch (Throwable var16) {
					CrashReport crashReport = CrashReport.create(var16, "Rendering overlay");
					CrashReportSection crashReportSection = crashReport.addElement("Overlay render details");
					crashReportSection.add("Overlay name", (CrashCallable<String>)(() -> this.client.getOverlay().getClass().getCanonicalName()));
					throw new CrashException(crashReport);
				}
			} else if (this.client.currentScreen != null) {
				try {
					this.client.currentScreen.render(matrixStack2, i, j, this.client.getLastFrameDuration());
				} catch (Throwable var15) {
					CrashReport crashReport = CrashReport.create(var15, "Rendering screen");
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

				try {
					if (this.client.currentScreen != null) {
						this.client.currentScreen.updateNarrator();
					}
				} catch (Throwable var14) {
					CrashReport crashReport = CrashReport.create(var14, "Narrating screen");
					CrashReportSection crashReportSection = crashReport.addElement("Screen details");
					crashReportSection.add("Screen name", (CrashCallable<String>)(() -> this.client.currentScreen.getClass().getCanonicalName()));
					throw new CrashException(crashReport);
				}
			}
		}
	}

	private void updateWorldIcon() {
		if (!this.hasWorldIcon && this.client.isInSingleplayer()) {
			long l = Util.getMeasuringTimeMs();
			if (l - this.lastWorldIconUpdate >= 1000L) {
				this.lastWorldIconUpdate = l;
				IntegratedServer integratedServer = this.client.getServer();
				if (integratedServer != null && !integratedServer.isStopped()) {
					integratedServer.getIconFile().ifPresent(path -> {
						if (Files.isRegularFile(path, new LinkOption[0])) {
							this.hasWorldIcon = true;
						} else {
							this.updateWorldIcon(path);
						}
					});
				}
			}
		}
	}

	private void updateWorldIcon(Path path) {
		if (this.client.worldRenderer.getCompletedChunkCount() > 10 && this.client.worldRenderer.isTerrainRenderComplete()) {
			NativeImage nativeImage = ScreenshotRecorder.takeScreenshot(this.client.getFramebuffer());
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
					nativeImage2.writeTo(path);
				} catch (IOException var16) {
					LOGGER.warn("Couldn't save auto screenshot", (Throwable)var16);
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
						Registry<Block> registry = this.client.world.getRegistryManager().get(Registry.BLOCK_KEY);
						bl = !itemStack.isEmpty() && (itemStack.canDestroy(registry, cachedBlockPosition) || itemStack.canPlaceOn(registry, cachedBlockPosition));
					}
				}
			}

			return bl;
		}
	}

	public void renderWorld(float tickDelta, long limitTime, MatrixStack matrices) {
		this.lightmapTextureManager.update(tickDelta);
		if (this.client.getCameraEntity() == null) {
			this.client.setCameraEntity(this.client.player);
		}

		this.updateTargetedEntity(tickDelta);
		this.client.getProfiler().push("center");
		boolean bl = this.shouldRenderBlockOutline();
		this.client.getProfiler().swap("camera");
		Camera camera = this.camera;
		this.viewDistance = (float)(this.client.options.getViewDistance() * 16);
		MatrixStack matrixStack = new MatrixStack();
		double d = this.getFov(camera, tickDelta, true);
		matrixStack.peek().getPositionMatrix().multiply(this.getBasicProjectionMatrix(d));
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

		Matrix4f matrix4f = matrixStack.peek().getPositionMatrix();
		this.loadProjectionMatrix(matrix4f);
		camera.update(
			this.client.world,
			(Entity)(this.client.getCameraEntity() == null ? this.client.player : this.client.getCameraEntity()),
			!this.client.options.getPerspective().isFirstPerson(),
			this.client.options.getPerspective().isFrontView(),
			tickDelta
		);
		matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(camera.getPitch()));
		matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(camera.getYaw() + 180.0F));
		Matrix3f matrix3f = matrices.peek().getNormalMatrix().copy();
		if (matrix3f.invert()) {
			RenderSystem.setInverseViewRotationMatrix(matrix3f);
		}

		this.client.worldRenderer.setupFrustum(matrices, camera.getPos(), this.getBasicProjectionMatrix(Math.max(d, this.client.options.fov)));
		this.client.worldRenderer.render(matrices, tickDelta, limitTime, bl, camera, this, this.lightmapTextureManager, matrix4f);
		this.client.getProfiler().swap("hand");
		if (this.renderHand) {
			RenderSystem.clear(256, MinecraftClient.IS_SYSTEM_MAC);
			this.renderHand(matrices, camera, tickDelta);
		}

		this.client.getProfiler().pop();
	}

	public void reset() {
		this.floatingItem = null;
		this.mapRenderer.clearStateTextures();
		this.camera.reset();
		this.hasWorldIcon = false;
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
			this.client
				.getItemRenderer()
				.renderItem(
					this.floatingItem, ModelTransformation.Mode.FIXED, LightmapTextureManager.MAX_LIGHT_COORDINATE, OverlayTexture.DEFAULT_UV, matrixStack, immediate, 0
				);
			matrixStack.pop();
			immediate.draw();
			RenderSystem.enableCull();
			RenderSystem.disableDepthTest();
		}
	}

	private void renderNausea(float distortionStrength) {
		int i = this.client.getWindow().getScaledWidth();
		int j = this.client.getWindow().getScaledHeight();
		double d = MathHelper.lerp((double)distortionStrength, 2.0, 1.0);
		float f = 0.2F * distortionStrength;
		float g = 0.4F * distortionStrength;
		float h = 0.2F * distortionStrength;
		double e = (double)i * d;
		double k = (double)j * d;
		double l = ((double)i - e) / 2.0;
		double m = ((double)j - k) / 2.0;
		RenderSystem.disableDepthTest();
		RenderSystem.depthMask(false);
		RenderSystem.enableBlend();
		RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ONE, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ONE);
		RenderSystem.setShaderColor(f, g, h, 1.0F);
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderTexture(0, NAUSEA_OVERLAY);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
		bufferBuilder.vertex(l, m + k, -90.0).texture(0.0F, 1.0F).next();
		bufferBuilder.vertex(l + e, m + k, -90.0).texture(1.0F, 1.0F).next();
		bufferBuilder.vertex(l + e, m, -90.0).texture(1.0F, 0.0F).next();
		bufferBuilder.vertex(l, m, -90.0).texture(0.0F, 0.0F).next();
		tessellator.draw();
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.defaultBlendFunc();
		RenderSystem.disableBlend();
		RenderSystem.depthMask(true);
		RenderSystem.enableDepthTest();
	}

	public MinecraftClient getClient() {
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
	public static Shader getPositionTexLightmapColorShader() {
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
	public static Shader getRenderTypeTextIntensityShader() {
		return renderTypeTextIntensityShader;
	}

	@Nullable
	public static Shader getRenderTypeTextSeeThroughShader() {
		return renderTypeTextSeeThroughShader;
	}

	@Nullable
	public static Shader getRenderTypeTextIntensitySeeThroughShader() {
		return renderTypeTextIntensitySeeThroughShader;
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
