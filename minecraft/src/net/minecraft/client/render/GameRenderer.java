package net.minecraft.client.render;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.platform.GlConst;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.systems.VertexSorter;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.PostEffectProcessor;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gl.ShaderStage;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameOverlayRenderer;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.util.ScreenshotRecorder;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.mob.SpiderEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceFactory;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloader;
import net.minecraft.resource.SinglePreparationResourceReloader;
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
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.GameMode;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Vector3f;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class GameRenderer implements AutoCloseable {
	private static final Identifier NAUSEA_OVERLAY = new Identifier("textures/misc/nausea.png");
	private static final Identifier BLUR_PROCESSOR = new Identifier("shaders/post/blur.json");
	private static final float field_49904 = 10.0F;
	static final Logger LOGGER = LogUtils.getLogger();
	private static final boolean field_32688 = false;
	/**
	 * Since the camera is conceptualized as a single point, a depth of {@value}
	 * blocks is used to define a rectangular area to be rendered.
	 * 
	 * @see Camera#getProjection()
	 */
	public static final float CAMERA_DEPTH = 0.05F;
	private static final float field_44940 = 1000.0F;
	final MinecraftClient client;
	private final ResourceManager resourceManager;
	private final Random random = Random.create();
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
	PostEffectProcessor postProcessor;
	@Nullable
	private PostEffectProcessor blurPostProcessor;
	private boolean postProcessorEnabled;
	private final Camera camera = new Camera();
	public ShaderProgram blitScreenProgram;
	private final Map<String, ShaderProgram> programs = Maps.newHashMap();
	@Nullable
	private static ShaderProgram positionProgram;
	@Nullable
	private static ShaderProgram positionColorProgram;
	@Nullable
	private static ShaderProgram positionColorTexProgram;
	@Nullable
	private static ShaderProgram positionTexProgram;
	@Nullable
	private static ShaderProgram positionTexColorProgram;
	@Nullable
	private static ShaderProgram particleProgram;
	@Nullable
	private static ShaderProgram positionColorLightmapProgram;
	@Nullable
	private static ShaderProgram positionColorTexLightmapProgram;
	@Nullable
	private static ShaderProgram renderTypeSolidProgram;
	@Nullable
	private static ShaderProgram renderTypeCutoutMippedProgram;
	@Nullable
	private static ShaderProgram renderTypeCutoutProgram;
	@Nullable
	private static ShaderProgram renderTypeTranslucentProgram;
	@Nullable
	private static ShaderProgram renderTypeTranslucentMovingBlockProgram;
	@Nullable
	private static ShaderProgram renderTypeArmorCutoutNoCullProgram;
	@Nullable
	private static ShaderProgram renderTypeEntitySolidProgram;
	@Nullable
	private static ShaderProgram renderTypeEntityCutoutProgram;
	@Nullable
	private static ShaderProgram renderTypeEntityCutoutNoNullProgram;
	@Nullable
	private static ShaderProgram renderTypeEntityCutoutNoNullZOffsetProgram;
	@Nullable
	private static ShaderProgram renderTypeItemEntityTranslucentCullProgram;
	@Nullable
	private static ShaderProgram renderTypeEntityTranslucentCullProgram;
	@Nullable
	private static ShaderProgram renderTypeEntityTranslucentProgram;
	@Nullable
	private static ShaderProgram renderTypeEntityTranslucentEmissiveProgram;
	@Nullable
	private static ShaderProgram renderTypeEntitySmoothCutoutProgram;
	@Nullable
	private static ShaderProgram renderTypeBeaconBeamProgram;
	@Nullable
	private static ShaderProgram renderTypeEntityDecalProgram;
	@Nullable
	private static ShaderProgram renderTypeEntityNoOutlineProgram;
	@Nullable
	private static ShaderProgram renderTypeEntityShadowProgram;
	@Nullable
	private static ShaderProgram renderTypeEntityAlphaProgram;
	@Nullable
	private static ShaderProgram renderTypeEyesProgram;
	@Nullable
	private static ShaderProgram renderTypeEnergySwirlProgram;
	@Nullable
	private static ShaderProgram renderTypeBreezeWindProgram;
	@Nullable
	private static ShaderProgram renderTypeLeashProgram;
	@Nullable
	private static ShaderProgram renderTypeWaterMaskProgram;
	@Nullable
	private static ShaderProgram renderTypeOutlineProgram;
	@Nullable
	private static ShaderProgram renderTypeArmorGlintProgram;
	@Nullable
	private static ShaderProgram renderTypeArmorEntityGlintProgram;
	@Nullable
	private static ShaderProgram renderTypeGlintTranslucentProgram;
	@Nullable
	private static ShaderProgram renderTypeGlintProgram;
	@Nullable
	private static ShaderProgram renderTypeGlintDirectProgram;
	@Nullable
	private static ShaderProgram renderTypeEntityGlintProgram;
	@Nullable
	private static ShaderProgram renderTypeEntityGlintDirectProgram;
	@Nullable
	private static ShaderProgram renderTypeTextProgram;
	@Nullable
	private static ShaderProgram renderTypeTextBackgroundProgram;
	@Nullable
	private static ShaderProgram renderTypeTextIntensityProgram;
	@Nullable
	private static ShaderProgram renderTypeTextSeeThroughProgram;
	@Nullable
	private static ShaderProgram renderTypeTextBackgroundSeeThroughProgram;
	@Nullable
	private static ShaderProgram renderTypeTextIntensitySeeThroughProgram;
	@Nullable
	private static ShaderProgram renderTypeLightningProgram;
	@Nullable
	private static ShaderProgram renderTypeTripwireProgram;
	@Nullable
	private static ShaderProgram renderTypeEndPortalProgram;
	@Nullable
	private static ShaderProgram renderTypeEndGatewayProgram;
	@Nullable
	private static ShaderProgram renderTypeCloudsProgram;
	@Nullable
	private static ShaderProgram renderTypeLinesProgram;
	@Nullable
	private static ShaderProgram renderTypeCrumblingProgram;
	@Nullable
	private static ShaderProgram renderTypeGuiProgram;
	@Nullable
	private static ShaderProgram renderTypeGuiOverlayProgram;
	@Nullable
	private static ShaderProgram renderTypeGuiTextHighlightProgram;
	@Nullable
	private static ShaderProgram renderTypeGuiGhostRecipeOverlayProgram;

	public GameRenderer(MinecraftClient client, HeldItemRenderer heldItemRenderer, ResourceManager resourceManager, BufferBuilderStorage buffers) {
		this.client = client;
		this.resourceManager = resourceManager;
		this.firstPersonRenderer = heldItemRenderer;
		this.mapRenderer = new MapRenderer(client.getTextureManager(), client.getMapDecorationsAtlasManager());
		this.lightmapTextureManager = new LightmapTextureManager(this, client);
		this.buffers = buffers;
		this.postProcessor = null;
	}

	public void close() {
		this.lightmapTextureManager.close();
		this.mapRenderer.close();
		this.overlayTexture.close();
		this.disablePostProcessor();
		this.clearPrograms();
		if (this.blitScreenProgram != null) {
			this.blitScreenProgram.close();
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

	public void disablePostProcessor() {
		if (this.postProcessor != null) {
			this.postProcessor.close();
		}

		this.postProcessor = null;
	}

	public void togglePostProcessorEnabled() {
		this.postProcessorEnabled = !this.postProcessorEnabled;
	}

	public void onCameraEntitySet(@Nullable Entity entity) {
		if (this.postProcessor != null) {
			this.postProcessor.close();
		}

		this.postProcessor = null;
		if (entity instanceof CreeperEntity) {
			this.loadPostProcessor(new Identifier("shaders/post/creeper.json"));
		} else if (entity instanceof SpiderEntity) {
			this.loadPostProcessor(new Identifier("shaders/post/spider.json"));
		} else if (entity instanceof EndermanEntity) {
			this.loadPostProcessor(new Identifier("shaders/post/invert.json"));
		}
	}

	private void loadPostProcessor(Identifier id) {
		if (this.postProcessor != null) {
			this.postProcessor.close();
		}

		try {
			this.postProcessor = new PostEffectProcessor(this.client.getTextureManager(), this.resourceManager, this.client.getFramebuffer(), id);
			this.postProcessor.setupDimensions(this.client.getWindow().getFramebufferWidth(), this.client.getWindow().getFramebufferHeight());
			this.postProcessorEnabled = true;
		} catch (IOException var3) {
			LOGGER.warn("Failed to load shader: {}", id, var3);
			this.postProcessorEnabled = false;
		} catch (JsonSyntaxException var4) {
			LOGGER.warn("Failed to parse shader: {}", id, var4);
			this.postProcessorEnabled = false;
		}
	}

	public void loadBlurPostProcessor() {
		if (this.blurPostProcessor != null) {
			this.blurPostProcessor.close();
		}

		try {
			this.blurPostProcessor = new PostEffectProcessor(this.client.getTextureManager(), this.resourceManager, this.client.getFramebuffer(), BLUR_PROCESSOR);
			this.blurPostProcessor.setupDimensions(this.client.getWindow().getFramebufferWidth(), this.client.getWindow().getFramebufferHeight());
		} catch (IOException var2) {
			LOGGER.warn("Failed to load shader: {}", BLUR_PROCESSOR, var2);
		} catch (JsonSyntaxException var3) {
			LOGGER.warn("Failed to parse shader: {}", BLUR_PROCESSOR, var3);
		}
	}

	public void renderBlur(float delta) {
		float f = (float)this.client.options.getMenuBackgroundBlurrinessValue();
		float g = f * 10.0F;
		if (this.blurPostProcessor != null && g >= 1.0F) {
			RenderSystem.enableBlend();
			this.blurPostProcessor.setUniforms("Radius", g);
			this.blurPostProcessor.render(delta);
			RenderSystem.disableBlend();
		}
	}

	public ResourceReloader createProgramReloader() {
		return new SinglePreparationResourceReloader<GameRenderer.CachedResourceFactory>() {
			protected GameRenderer.CachedResourceFactory prepare(ResourceManager resourceManager, Profiler profiler) {
				Map<Identifier, Resource> map = resourceManager.findResources(
					"shaders",
					id -> {
						String string = id.getPath();
						return string.endsWith(".json")
							|| string.endsWith(ShaderStage.Type.FRAGMENT.getFileExtension())
							|| string.endsWith(ShaderStage.Type.VERTEX.getFileExtension())
							|| string.endsWith(".glsl");
					}
				);
				Map<Identifier, Resource> map2 = new HashMap();
				map.forEach((id, resource) -> {
					try {
						InputStream inputStream = resource.getInputStream();

						try {
							byte[] bs = inputStream.readAllBytes();
							map2.put(id, new Resource(resource.getPack(), () -> new ByteArrayInputStream(bs)));
						} catch (Throwable var7) {
							if (inputStream != null) {
								try {
									inputStream.close();
								} catch (Throwable var6) {
									var7.addSuppressed(var6);
								}
							}

							throw var7;
						}

						if (inputStream != null) {
							inputStream.close();
						}
					} catch (Exception var8) {
						GameRenderer.LOGGER.warn("Failed to read resource {}", id, var8);
					}
				});
				return new GameRenderer.CachedResourceFactory(resourceManager, map2);
			}

			protected void apply(GameRenderer.CachedResourceFactory cachedResourceFactory, ResourceManager resourceManager, Profiler profiler) {
				GameRenderer.this.loadPrograms(cachedResourceFactory);
				if (GameRenderer.this.postProcessor != null) {
					GameRenderer.this.postProcessor.close();
				}

				GameRenderer.this.postProcessor = null;
				GameRenderer.this.onCameraEntitySet(GameRenderer.this.client.getCameraEntity());
			}

			@Override
			public String getName() {
				return "Shader Loader";
			}
		};
	}

	public void preloadPrograms(ResourceFactory factory) {
		if (this.blitScreenProgram != null) {
			throw new RuntimeException("Blit shader already preloaded");
		} else {
			try {
				this.blitScreenProgram = new ShaderProgram(factory, "blit_screen", VertexFormats.BLIT_SCREEN);
			} catch (IOException var3) {
				throw new RuntimeException("could not preload blit shader", var3);
			}

			renderTypeGuiProgram = this.preloadProgram(factory, "rendertype_gui", VertexFormats.POSITION_COLOR);
			renderTypeGuiOverlayProgram = this.preloadProgram(factory, "rendertype_gui_overlay", VertexFormats.POSITION_COLOR);
			positionProgram = this.preloadProgram(factory, "position", VertexFormats.POSITION);
			positionColorProgram = this.preloadProgram(factory, "position_color", VertexFormats.POSITION_COLOR);
			positionColorTexProgram = this.preloadProgram(factory, "position_color_tex", VertexFormats.POSITION_COLOR_TEXTURE);
			positionTexProgram = this.preloadProgram(factory, "position_tex", VertexFormats.POSITION_TEXTURE);
			positionTexColorProgram = this.preloadProgram(factory, "position_tex_color", VertexFormats.POSITION_TEXTURE_COLOR);
			renderTypeTextProgram = this.preloadProgram(factory, "rendertype_text", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT);
		}
	}

	private ShaderProgram preloadProgram(ResourceFactory factory, String name, VertexFormat format) {
		try {
			ShaderProgram shaderProgram = new ShaderProgram(factory, name, format);
			this.programs.put(name, shaderProgram);
			return shaderProgram;
		} catch (Exception var5) {
			throw new IllegalStateException("could not preload shader " + name, var5);
		}
	}

	void loadPrograms(ResourceFactory factory) {
		RenderSystem.assertOnRenderThread();
		List<ShaderStage> list = Lists.<ShaderStage>newArrayList();
		list.addAll(ShaderStage.Type.FRAGMENT.getLoadedShaders().values());
		list.addAll(ShaderStage.Type.VERTEX.getLoadedShaders().values());
		list.forEach(ShaderStage::release);
		List<Pair<ShaderProgram, Consumer<ShaderProgram>>> list2 = Lists.<Pair<ShaderProgram, Consumer<ShaderProgram>>>newArrayListWithCapacity(this.programs.size());

		try {
			list2.add(Pair.of(new ShaderProgram(factory, "particle", VertexFormats.POSITION_TEXTURE_COLOR_LIGHT), (Consumer)program -> particleProgram = program));
			list2.add(Pair.of(new ShaderProgram(factory, "position", VertexFormats.POSITION), (Consumer)program -> positionProgram = program));
			list2.add(Pair.of(new ShaderProgram(factory, "position_color", VertexFormats.POSITION_COLOR), (Consumer)program -> positionColorProgram = program));
			list2.add(
				Pair.of(
					new ShaderProgram(factory, "position_color_lightmap", VertexFormats.POSITION_COLOR_LIGHT), (Consumer)program -> positionColorLightmapProgram = program
				)
			);
			list2.add(
				Pair.of(new ShaderProgram(factory, "position_color_tex", VertexFormats.POSITION_COLOR_TEXTURE), (Consumer)program -> positionColorTexProgram = program)
			);
			list2.add(
				Pair.of(
					new ShaderProgram(factory, "position_color_tex_lightmap", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT),
					(Consumer)program -> positionColorTexLightmapProgram = program
				)
			);
			list2.add(Pair.of(new ShaderProgram(factory, "position_tex", VertexFormats.POSITION_TEXTURE), (Consumer)program -> positionTexProgram = program));
			list2.add(
				Pair.of(new ShaderProgram(factory, "position_tex_color", VertexFormats.POSITION_TEXTURE_COLOR), (Consumer)program -> positionTexColorProgram = program)
			);
			list2.add(
				Pair.of(
					new ShaderProgram(factory, "rendertype_solid", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL), (Consumer)program -> renderTypeSolidProgram = program
				)
			);
			list2.add(
				Pair.of(
					new ShaderProgram(factory, "rendertype_cutout_mipped", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL),
					(Consumer)program -> renderTypeCutoutMippedProgram = program
				)
			);
			list2.add(
				Pair.of(
					new ShaderProgram(factory, "rendertype_cutout", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL), (Consumer)program -> renderTypeCutoutProgram = program
				)
			);
			list2.add(
				Pair.of(
					new ShaderProgram(factory, "rendertype_translucent", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL),
					(Consumer)program -> renderTypeTranslucentProgram = program
				)
			);
			list2.add(
				Pair.of(
					new ShaderProgram(factory, "rendertype_translucent_moving_block", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL),
					(Consumer)program -> renderTypeTranslucentMovingBlockProgram = program
				)
			);
			list2.add(
				Pair.of(
					new ShaderProgram(factory, "rendertype_armor_cutout_no_cull", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL),
					(Consumer)program -> renderTypeArmorCutoutNoCullProgram = program
				)
			);
			list2.add(
				Pair.of(
					new ShaderProgram(factory, "rendertype_entity_solid", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL),
					(Consumer)program -> renderTypeEntitySolidProgram = program
				)
			);
			list2.add(
				Pair.of(
					new ShaderProgram(factory, "rendertype_entity_cutout", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL),
					(Consumer)program -> renderTypeEntityCutoutProgram = program
				)
			);
			list2.add(
				Pair.of(
					new ShaderProgram(factory, "rendertype_entity_cutout_no_cull", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL),
					(Consumer)program -> renderTypeEntityCutoutNoNullProgram = program
				)
			);
			list2.add(
				Pair.of(
					new ShaderProgram(factory, "rendertype_entity_cutout_no_cull_z_offset", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL),
					(Consumer)program -> renderTypeEntityCutoutNoNullZOffsetProgram = program
				)
			);
			list2.add(
				Pair.of(
					new ShaderProgram(factory, "rendertype_item_entity_translucent_cull", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL),
					(Consumer)shaderProgram -> renderTypeItemEntityTranslucentCullProgram = shaderProgram
				)
			);
			list2.add(
				Pair.of(
					new ShaderProgram(factory, "rendertype_entity_translucent_cull", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL),
					(Consumer)program -> renderTypeEntityTranslucentCullProgram = program
				)
			);
			list2.add(
				Pair.of(
					new ShaderProgram(factory, "rendertype_entity_translucent", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL),
					(Consumer)program -> renderTypeEntityTranslucentProgram = program
				)
			);
			list2.add(
				Pair.of(
					new ShaderProgram(factory, "rendertype_entity_translucent_emissive", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL),
					(Consumer)program -> renderTypeEntityTranslucentEmissiveProgram = program
				)
			);
			list2.add(
				Pair.of(
					new ShaderProgram(factory, "rendertype_entity_smooth_cutout", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL),
					(Consumer)program -> renderTypeEntitySmoothCutoutProgram = program
				)
			);
			list2.add(
				Pair.of(
					new ShaderProgram(factory, "rendertype_beacon_beam", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL),
					(Consumer)program -> renderTypeBeaconBeamProgram = program
				)
			);
			list2.add(
				Pair.of(
					new ShaderProgram(factory, "rendertype_entity_decal", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL),
					(Consumer)program -> renderTypeEntityDecalProgram = program
				)
			);
			list2.add(
				Pair.of(
					new ShaderProgram(factory, "rendertype_entity_no_outline", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL),
					(Consumer)program -> renderTypeEntityNoOutlineProgram = program
				)
			);
			list2.add(
				Pair.of(
					new ShaderProgram(factory, "rendertype_entity_shadow", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL),
					(Consumer)program -> renderTypeEntityShadowProgram = program
				)
			);
			list2.add(
				Pair.of(
					new ShaderProgram(factory, "rendertype_entity_alpha", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL),
					(Consumer)program -> renderTypeEntityAlphaProgram = program
				)
			);
			list2.add(
				Pair.of(
					new ShaderProgram(factory, "rendertype_eyes", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL),
					(Consumer)program -> renderTypeEyesProgram = program
				)
			);
			list2.add(
				Pair.of(
					new ShaderProgram(factory, "rendertype_energy_swirl", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL),
					(Consumer)program -> renderTypeEnergySwirlProgram = program
				)
			);
			list2.add(Pair.of(new ShaderProgram(factory, "rendertype_leash", VertexFormats.POSITION_COLOR_LIGHT), (Consumer)program -> renderTypeLeashProgram = program));
			list2.add(Pair.of(new ShaderProgram(factory, "rendertype_water_mask", VertexFormats.POSITION), (Consumer)program -> renderTypeWaterMaskProgram = program));
			list2.add(
				Pair.of(new ShaderProgram(factory, "rendertype_outline", VertexFormats.POSITION_COLOR_TEXTURE), (Consumer)program -> renderTypeOutlineProgram = program)
			);
			list2.add(
				Pair.of(new ShaderProgram(factory, "rendertype_armor_glint", VertexFormats.POSITION_TEXTURE), (Consumer)program -> renderTypeArmorGlintProgram = program)
			);
			list2.add(
				Pair.of(
					new ShaderProgram(factory, "rendertype_armor_entity_glint", VertexFormats.POSITION_TEXTURE),
					(Consumer)program -> renderTypeArmorEntityGlintProgram = program
				)
			);
			list2.add(
				Pair.of(
					new ShaderProgram(factory, "rendertype_glint_translucent", VertexFormats.POSITION_TEXTURE),
					(Consumer)program -> renderTypeGlintTranslucentProgram = program
				)
			);
			list2.add(Pair.of(new ShaderProgram(factory, "rendertype_glint", VertexFormats.POSITION_TEXTURE), (Consumer)program -> renderTypeGlintProgram = program));
			list2.add(
				Pair.of(new ShaderProgram(factory, "rendertype_glint_direct", VertexFormats.POSITION_TEXTURE), (Consumer)program -> renderTypeGlintDirectProgram = program)
			);
			list2.add(
				Pair.of(new ShaderProgram(factory, "rendertype_entity_glint", VertexFormats.POSITION_TEXTURE), (Consumer)program -> renderTypeEntityGlintProgram = program)
			);
			list2.add(
				Pair.of(
					new ShaderProgram(factory, "rendertype_entity_glint_direct", VertexFormats.POSITION_TEXTURE),
					(Consumer)program -> renderTypeEntityGlintDirectProgram = program
				)
			);
			list2.add(
				Pair.of(new ShaderProgram(factory, "rendertype_text", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT), (Consumer)program -> renderTypeTextProgram = program)
			);
			list2.add(
				Pair.of(
					new ShaderProgram(factory, "rendertype_text_background", VertexFormats.POSITION_COLOR_LIGHT),
					(Consumer)program -> renderTypeTextBackgroundProgram = program
				)
			);
			list2.add(
				Pair.of(
					new ShaderProgram(factory, "rendertype_text_intensity", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT),
					(Consumer)program -> renderTypeTextIntensityProgram = program
				)
			);
			list2.add(
				Pair.of(
					new ShaderProgram(factory, "rendertype_text_see_through", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT),
					(Consumer)program -> renderTypeTextSeeThroughProgram = program
				)
			);
			list2.add(
				Pair.of(
					new ShaderProgram(factory, "rendertype_text_background_see_through", VertexFormats.POSITION_COLOR_LIGHT),
					(Consumer)program -> renderTypeTextBackgroundSeeThroughProgram = program
				)
			);
			list2.add(
				Pair.of(
					new ShaderProgram(factory, "rendertype_text_intensity_see_through", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT),
					(Consumer)program -> renderTypeTextIntensitySeeThroughProgram = program
				)
			);
			list2.add(
				Pair.of(new ShaderProgram(factory, "rendertype_lightning", VertexFormats.POSITION_COLOR), (Consumer)program -> renderTypeLightningProgram = program)
			);
			list2.add(
				Pair.of(
					new ShaderProgram(factory, "rendertype_tripwire", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL),
					(Consumer)program -> renderTypeTripwireProgram = program
				)
			);
			list2.add(Pair.of(new ShaderProgram(factory, "rendertype_end_portal", VertexFormats.POSITION), (Consumer)program -> renderTypeEndPortalProgram = program));
			list2.add(Pair.of(new ShaderProgram(factory, "rendertype_end_gateway", VertexFormats.POSITION), (Consumer)program -> renderTypeEndGatewayProgram = program));
			list2.add(
				Pair.of(
					new ShaderProgram(factory, "rendertype_clouds", VertexFormats.POSITION_TEXTURE_COLOR_NORMAL), (Consumer)program -> renderTypeCloudsProgram = program
				)
			);
			list2.add(Pair.of(new ShaderProgram(factory, "rendertype_lines", VertexFormats.LINES), (Consumer)program -> renderTypeLinesProgram = program));
			list2.add(
				Pair.of(
					new ShaderProgram(factory, "rendertype_crumbling", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL),
					(Consumer)program -> renderTypeCrumblingProgram = program
				)
			);
			list2.add(Pair.of(new ShaderProgram(factory, "rendertype_gui", VertexFormats.POSITION_COLOR), (Consumer)program -> renderTypeGuiProgram = program));
			list2.add(
				Pair.of(new ShaderProgram(factory, "rendertype_gui_overlay", VertexFormats.POSITION_COLOR), (Consumer)program -> renderTypeGuiOverlayProgram = program)
			);
			list2.add(
				Pair.of(
					new ShaderProgram(factory, "rendertype_gui_text_highlight", VertexFormats.POSITION_COLOR),
					(Consumer)program -> renderTypeGuiTextHighlightProgram = program
				)
			);
			list2.add(
				Pair.of(
					new ShaderProgram(factory, "rendertype_gui_ghost_recipe_overlay", VertexFormats.POSITION_COLOR),
					(Consumer)program -> renderTypeGuiGhostRecipeOverlayProgram = program
				)
			);
			list2.add(
				Pair.of(
					new ShaderProgram(factory, "rendertype_breeze_wind", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL),
					(Consumer)program -> renderTypeBreezeWindProgram = program
				)
			);
			this.loadBlurPostProcessor();
		} catch (IOException var5) {
			list2.forEach(pair -> ((ShaderProgram)pair.getFirst()).close());
			throw new RuntimeException("could not reload shaders", var5);
		}

		this.clearPrograms();
		list2.forEach(pair -> {
			ShaderProgram shaderProgram = (ShaderProgram)pair.getFirst();
			this.programs.put(shaderProgram.getName(), shaderProgram);
			((Consumer)pair.getSecond()).accept(shaderProgram);
		});
	}

	private void clearPrograms() {
		RenderSystem.assertOnRenderThread();
		this.programs.values().forEach(ShaderProgram::close);
		this.programs.clear();
	}

	@Nullable
	public ShaderProgram getProgram(@Nullable String name) {
		return name == null ? null : (ShaderProgram)this.programs.get(name);
	}

	public void tick() {
		this.updateFovMultiplier();
		this.lightmapTextureManager.tick();
		if (this.client.getCameraEntity() == null) {
			this.client.setCameraEntity(this.client.player);
		}

		this.camera.updateEyeHeight();
		this.firstPersonRenderer.updateHeldItems();
		++this.ticks;
		if (this.client.world.getTickManager().shouldTick()) {
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
				--this.floatingItemTimeLeft;
				if (this.floatingItemTimeLeft == 0) {
					this.floatingItem = null;
				}
			}
		}
	}

	@Nullable
	public PostEffectProcessor getPostProcessor() {
		return this.postProcessor;
	}

	public void onResized(int width, int height) {
		if (this.postProcessor != null) {
			this.postProcessor.setupDimensions(width, height);
		}

		if (this.blurPostProcessor != null) {
			this.blurPostProcessor.setupDimensions(width, height);
		}

		this.client.worldRenderer.onResized(width, height);
	}

	public void updateCrosshairTarget(float tickDelta) {
		Entity entity = this.client.getCameraEntity();
		if (entity != null) {
			if (this.client.world != null && this.client.player != null) {
				this.client.getProfiler().push("pick");
				double d = this.client.player.getBlockInteractionRange();
				double e = this.client.player.getEntityInteractionRange();
				HitResult hitResult = this.findCrosshairTarget(entity, d, e, tickDelta);
				this.client.crosshairTarget = hitResult;
				this.client.targetedEntity = hitResult instanceof EntityHitResult entityHitResult ? entityHitResult.getEntity() : null;
				this.client.getProfiler().pop();
			}
		}
	}

	private HitResult findCrosshairTarget(Entity camera, double blockInteractionRange, double entityInteractionRange, float tickDelta) {
		double d = Math.max(blockInteractionRange, entityInteractionRange);
		double e = MathHelper.square(d);
		Vec3d vec3d = camera.getCameraPosVec(tickDelta);
		HitResult hitResult = camera.raycast(d, tickDelta, false);
		double f = hitResult.getPos().squaredDistanceTo(vec3d);
		if (hitResult.getType() != HitResult.Type.MISS) {
			e = f;
			d = Math.sqrt(f);
		}

		Vec3d vec3d2 = camera.getRotationVec(tickDelta);
		Vec3d vec3d3 = vec3d.add(vec3d2.x * d, vec3d2.y * d, vec3d2.z * d);
		float g = 1.0F;
		Box box = camera.getBoundingBox().stretch(vec3d2.multiply(d)).expand(1.0, 1.0, 1.0);
		EntityHitResult entityHitResult = ProjectileUtil.raycast(camera, vec3d, vec3d3, box, entity -> !entity.isSpectator() && entity.canHit(), e);
		return entityHitResult != null && entityHitResult.getPos().squaredDistanceTo(vec3d) < f
			? ensureTargetInRange(entityHitResult, vec3d, entityInteractionRange)
			: ensureTargetInRange(hitResult, vec3d, blockInteractionRange);
	}

	private static HitResult ensureTargetInRange(HitResult hitResult, Vec3d cameraPos, double interactionRange) {
		Vec3d vec3d = hitResult.getPos();
		if (!vec3d.isInRange(cameraPos, interactionRange)) {
			Vec3d vec3d2 = hitResult.getPos();
			Direction direction = Direction.getFacing(vec3d2.x - cameraPos.x, vec3d2.y - cameraPos.y, vec3d2.z - cameraPos.z);
			return BlockHitResult.createMissed(vec3d2, direction, BlockPos.ofFloored(vec3d2));
		} else {
			return hitResult;
		}
	}

	private void updateFovMultiplier() {
		float f = 1.0F;
		Entity var3 = this.client.getCameraEntity();
		if (var3 instanceof AbstractClientPlayerEntity abstractClientPlayerEntity) {
			f = abstractClientPlayerEntity.getFovMultiplier();
		}

		this.lastFovMultiplier = this.fovMultiplier;
		this.fovMultiplier += (f - this.fovMultiplier) * 0.5F;
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
				d = (double)((Integer)this.client.options.getFov().getValue()).intValue();
				d *= (double)MathHelper.lerp(tickDelta, this.lastFovMultiplier, this.fovMultiplier);
			}

			if (camera.getFocusedEntity() instanceof LivingEntity && ((LivingEntity)camera.getFocusedEntity()).isDead()) {
				float f = Math.min((float)((LivingEntity)camera.getFocusedEntity()).deathTime + tickDelta, 20.0F);
				d /= (double)((1.0F - 500.0F / (f + 500.0F)) * 2.0F + 1.0F);
			}

			CameraSubmersionType cameraSubmersionType = camera.getSubmersionType();
			if (cameraSubmersionType == CameraSubmersionType.LAVA || cameraSubmersionType == CameraSubmersionType.WATER) {
				d *= MathHelper.lerp(this.client.options.getFovEffectScale().getValue(), 1.0, 0.85714287F);
			}

			return d;
		}
	}

	private void tiltViewWhenHurt(MatrixStack matrices, float tickDelta) {
		Entity f = this.client.getCameraEntity();
		if (f instanceof LivingEntity livingEntity) {
			float fx = (float)livingEntity.hurtTime - tickDelta;
			if (livingEntity.isDead()) {
				float g = Math.min((float)livingEntity.deathTime + tickDelta, 20.0F);
				matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(40.0F - 8000.0F / (g + 200.0F)));
			}

			if (fx < 0.0F) {
				return;
			}

			float var8 = fx / (float)livingEntity.maxHurtTime;
			float var9 = MathHelper.sin(var8 * var8 * var8 * var8 * (float) Math.PI);
			float g = livingEntity.getDamageTiltYaw();
			matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-g));
			float h = (float)((double)(-var9) * 14.0 * this.client.options.getDamageTiltStrength().getValue());
			matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(h));
			matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(g));
		}
	}

	private void bobView(MatrixStack matrices, float tickDelta) {
		if (this.client.getCameraEntity() instanceof PlayerEntity) {
			PlayerEntity playerEntity = (PlayerEntity)this.client.getCameraEntity();
			float f = playerEntity.horizontalSpeed - playerEntity.prevHorizontalSpeed;
			float g = -(playerEntity.horizontalSpeed + f * tickDelta);
			float h = MathHelper.lerp(tickDelta, playerEntity.prevStrideDistance, playerEntity.strideDistance);
			matrices.translate(MathHelper.sin(g * (float) Math.PI) * h * 0.5F, -Math.abs(MathHelper.cos(g * (float) Math.PI) * h), 0.0F);
			matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(MathHelper.sin(g * (float) Math.PI) * h * 3.0F));
			matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(Math.abs(MathHelper.cos(g * (float) Math.PI - 0.2F) * h) * 5.0F));
		}
	}

	public void renderWithZoom(float zoom, float zoomX, float zoomY) {
		this.zoom = zoom;
		this.zoomX = zoomX;
		this.zoomY = zoomY;
		this.setBlockOutlineEnabled(false);
		this.setRenderHand(false);
		this.renderWorld(1.0F, 0L);
		this.zoom = 1.0F;
	}

	private void renderHand(Camera camera, float tickDelta, Matrix4f matrix4f) {
		if (!this.renderingPanorama) {
			this.loadProjectionMatrix(this.getBasicProjectionMatrix(this.getFov(camera, tickDelta, false)));
			MatrixStack matrixStack = new MatrixStack();
			matrixStack.push();
			matrixStack.multiplyPositionMatrix(matrix4f.invert(new Matrix4f()));
			Matrix4fStack matrix4fStack = RenderSystem.getModelViewStack();
			matrix4fStack.pushMatrix().mul(matrix4f);
			RenderSystem.applyModelViewMatrix();
			this.tiltViewWhenHurt(matrixStack, tickDelta);
			if (this.client.options.getBobView().getValue()) {
				this.bobView(matrixStack, tickDelta);
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
						matrixStack,
						this.buffers.getEntityVertexConsumers(),
						this.client.player,
						this.client.getEntityRenderDispatcher().getLight(this.client.player, tickDelta)
					);
				this.lightmapTextureManager.disable();
			}

			matrix4fStack.popMatrix();
			RenderSystem.applyModelViewMatrix();
			matrixStack.pop();
			if (this.client.options.getPerspective().isFirstPerson() && !bl) {
				InGameOverlayRenderer.renderOverlays(this.client, matrixStack);
			}
		}
	}

	public void loadProjectionMatrix(Matrix4f projectionMatrix) {
		RenderSystem.setProjectionMatrix(projectionMatrix, VertexSorter.BY_DISTANCE);
	}

	public Matrix4f getBasicProjectionMatrix(double fov) {
		Matrix4f matrix4f = new Matrix4f();
		if (this.zoom != 1.0F) {
			matrix4f.translate(this.zoomX, -this.zoomY, 0.0F);
			matrix4f.scale(this.zoom, this.zoom, 1.0F);
		}

		return matrix4f.perspective(
			(float)(fov * (float) (Math.PI / 180.0)),
			(float)this.client.getWindow().getFramebufferWidth() / (float)this.client.getWindow().getFramebufferHeight(),
			0.05F,
			this.getFarPlaneDistance()
		);
	}

	public float getFarPlaneDistance() {
		return this.viewDistance * 4.0F;
	}

	public static float getNightVisionStrength(LivingEntity entity, float tickDelta) {
		StatusEffectInstance statusEffectInstance = entity.getStatusEffect(StatusEffects.NIGHT_VISION);
		return !statusEffectInstance.isDurationBelow(200)
			? 1.0F
			: 0.7F + MathHelper.sin(((float)statusEffectInstance.getDuration() - tickDelta) * (float) Math.PI * 0.2F) * 0.3F;
	}

	public void render(float tickDelta, long startTime, boolean tick) {
		if (!this.client.isWindowFocused()
			&& this.client.options.pauseOnLostFocus
			&& (!this.client.options.getTouchscreen().getValue() || !this.client.mouse.wasRightButtonClicked())) {
			if (Util.getMeasuringTimeMs() - this.lastWindowFocusedTime > 500L) {
				this.client.openGameMenu(false);
			}
		} else {
			this.lastWindowFocusedTime = Util.getMeasuringTimeMs();
		}

		if (!this.client.skipGameRender) {
			float f = this.client.world != null && this.client.world.getTickManager().shouldTick() ? tickDelta : 1.0F;
			boolean bl = this.client.isFinishedLoading();
			int i = (int)(this.client.mouse.getX() * (double)this.client.getWindow().getScaledWidth() / (double)this.client.getWindow().getWidth());
			int j = (int)(this.client.mouse.getY() * (double)this.client.getWindow().getScaledHeight() / (double)this.client.getWindow().getHeight());
			RenderSystem.viewport(0, 0, this.client.getWindow().getFramebufferWidth(), this.client.getWindow().getFramebufferHeight());
			if (bl && tick && this.client.world != null) {
				this.client.getProfiler().push("level");
				this.renderWorld(tickDelta, startTime);
				this.updateWorldIcon();
				this.client.worldRenderer.drawEntityOutlinesFramebuffer();
				if (this.postProcessor != null && this.postProcessorEnabled) {
					RenderSystem.disableBlend();
					RenderSystem.disableDepthTest();
					RenderSystem.resetTextureMatrix();
					this.postProcessor.render(f);
				}

				this.client.getFramebuffer().beginWrite(true);
			}

			Window window = this.client.getWindow();
			RenderSystem.clear(GlConst.GL_DEPTH_BUFFER_BIT, MinecraftClient.IS_SYSTEM_MAC);
			Matrix4f matrix4f = new Matrix4f()
				.setOrtho(
					0.0F,
					(float)((double)window.getFramebufferWidth() / window.getScaleFactor()),
					(float)((double)window.getFramebufferHeight() / window.getScaleFactor()),
					0.0F,
					1000.0F,
					21000.0F
				);
			RenderSystem.setProjectionMatrix(matrix4f, VertexSorter.BY_Z);
			Matrix4fStack matrix4fStack = RenderSystem.getModelViewStack();
			matrix4fStack.pushMatrix();
			matrix4fStack.translation(0.0F, 0.0F, -11000.0F);
			RenderSystem.applyModelViewMatrix();
			DiffuseLighting.enableGuiDepthLighting();
			DrawContext drawContext = new DrawContext(this.client, this.buffers.getEntityVertexConsumers());
			if (bl && tick && this.client.world != null) {
				this.client.getProfiler().swap("gui");
				if (this.client.player != null) {
					float g = MathHelper.lerp(f, this.client.player.prevNauseaIntensity, this.client.player.nauseaIntensity);
					float h = ((Double)this.client.options.getDistortionEffectScale().getValue()).floatValue();
					if (g > 0.0F && this.client.player.hasStatusEffect(StatusEffects.NAUSEA) && h < 1.0F) {
						this.renderNausea(drawContext, g * (1.0F - h));
					}
				}

				if (!this.client.options.hudHidden) {
					this.renderFloatingItem(this.client.getWindow().getScaledWidth(), this.client.getWindow().getScaledHeight(), f);
				}

				this.client.inGameHud.render(drawContext, f);
				RenderSystem.clear(GlConst.GL_DEPTH_BUFFER_BIT, MinecraftClient.IS_SYSTEM_MAC);
				this.client.getProfiler().pop();
			}

			if (this.client.getOverlay() != null) {
				try {
					this.client.getOverlay().render(drawContext, i, j, this.client.getLastFrameDuration());
				} catch (Throwable var18) {
					CrashReport crashReport = CrashReport.create(var18, "Rendering overlay");
					CrashReportSection crashReportSection = crashReport.addElement("Overlay render details");
					crashReportSection.add("Overlay name", (CrashCallable<String>)(() -> this.client.getOverlay().getClass().getCanonicalName()));
					throw new CrashException(crashReport);
				}
			} else if (bl && this.client.currentScreen != null) {
				try {
					this.client.currentScreen.renderWithTooltip(drawContext, i, j, this.client.getLastFrameDuration());
				} catch (Throwable var17) {
					CrashReport crashReport = CrashReport.create(var17, "Rendering screen");
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
				} catch (Throwable var16) {
					CrashReport crashReport = CrashReport.create(var16, "Narrating screen");
					CrashReportSection crashReportSection = crashReport.addElement("Screen details");
					crashReportSection.add("Screen name", (CrashCallable<String>)(() -> this.client.currentScreen.getClass().getCanonicalName()));
					throw new CrashException(crashReport);
				}
			}

			if (bl && tick && this.client.world != null) {
				this.client.inGameHud.renderAutosaveIndicator(drawContext, f);
			}

			if (bl) {
				this.client.getProfiler().push("toasts");
				this.client.getToastManager().draw(drawContext);
				this.client.getProfiler().pop();
			}

			drawContext.draw();
			matrix4fStack.popMatrix();
			RenderSystem.applyModelViewMatrix();
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
					LOGGER.warn("Couldn't save auto screenshot", var16);
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
						Registry<Block> registry = this.client.world.getRegistryManager().get(RegistryKeys.BLOCK);
						bl = !itemStack.isEmpty() && (itemStack.canBreak(cachedBlockPosition) || itemStack.canPlaceOn(cachedBlockPosition));
					}
				}
			}

			return bl;
		}
	}

	public void renderWorld(float tickDelta, long limitTime) {
		this.lightmapTextureManager.update(tickDelta);
		if (this.client.getCameraEntity() == null) {
			this.client.setCameraEntity(this.client.player);
		}

		this.updateCrosshairTarget(tickDelta);
		this.client.getProfiler().push("center");
		boolean bl = this.shouldRenderBlockOutline();
		this.client.getProfiler().swap("camera");
		Camera camera = this.camera;
		Entity entity = (Entity)(this.client.getCameraEntity() == null ? this.client.player : this.client.getCameraEntity());
		camera.update(
			this.client.world,
			entity,
			!this.client.options.getPerspective().isFirstPerson(),
			this.client.options.getPerspective().isFrontView(),
			this.client.world.getTickManager().shouldSkipTick(entity) ? 1.0F : tickDelta
		);
		this.viewDistance = (float)(this.client.options.getClampedViewDistance() * 16);
		double d = this.getFov(camera, tickDelta, true);
		Matrix4f matrix4f = this.getBasicProjectionMatrix(d);
		MatrixStack matrixStack = new MatrixStack();
		this.tiltViewWhenHurt(matrixStack, camera.getLastTickDelta());
		if (this.client.options.getBobView().getValue()) {
			this.bobView(matrixStack, camera.getLastTickDelta());
		}

		matrix4f.mul(matrixStack.peek().getPositionMatrix());
		float f = ((Double)this.client.options.getDistortionEffectScale().getValue()).floatValue();
		float g = MathHelper.lerp(tickDelta, this.client.player.prevNauseaIntensity, this.client.player.nauseaIntensity) * f * f;
		if (g > 0.0F) {
			int i = this.client.player.hasStatusEffect(StatusEffects.NAUSEA) ? 7 : 20;
			float h = 5.0F / (g * g + 5.0F) - g * 0.04F;
			h *= h;
			Vector3f vector3f = new Vector3f(0.0F, MathHelper.SQUARE_ROOT_OF_TWO / 2.0F, MathHelper.SQUARE_ROOT_OF_TWO / 2.0F);
			float j = ((float)this.ticks + tickDelta) * (float)i * (float) (Math.PI / 180.0);
			matrix4f.rotate(j, vector3f);
			matrix4f.scale(1.0F / h, 1.0F, 1.0F);
			matrix4f.rotate(-j, vector3f);
		}

		this.loadProjectionMatrix(matrix4f);
		Matrix4f matrix4f2 = new Matrix4f()
			.rotationXYZ(camera.getPitch() * (float) (Math.PI / 180.0), camera.getYaw() * (float) (Math.PI / 180.0) + (float) Math.PI, 0.0F);
		this.client
			.worldRenderer
			.setupFrustum(camera.getPos(), matrix4f2, this.getBasicProjectionMatrix(Math.max(d, (double)((Integer)this.client.options.getFov().getValue()).intValue())));
		this.client.worldRenderer.render(tickDelta, limitTime, bl, camera, this, this.lightmapTextureManager, matrix4f2, matrix4f);
		this.client.getProfiler().swap("hand");
		if (this.renderHand) {
			RenderSystem.clear(GlConst.GL_DEPTH_BUFFER_BIT, MinecraftClient.IS_SYSTEM_MAC);
			this.renderHand(camera, tickDelta, matrix4f2);
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
				(float)(scaledWidth / 2) + l * MathHelper.abs(MathHelper.sin(k * 2.0F)), (float)(scaledHeight / 2) + m * MathHelper.abs(MathHelper.sin(k * 2.0F)), -50.0F
			);
			float n = 50.0F + 175.0F * MathHelper.sin(k);
			matrixStack.scale(n, -n, n);
			matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(900.0F * MathHelper.abs(MathHelper.sin(k))));
			matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(6.0F * MathHelper.cos(f * 8.0F)));
			matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(6.0F * MathHelper.cos(f * 8.0F)));
			VertexConsumerProvider.Immediate immediate = this.buffers.getEntityVertexConsumers();
			this.client
				.getItemRenderer()
				.renderItem(this.floatingItem, ModelTransformationMode.FIXED, 15728880, OverlayTexture.DEFAULT_UV, matrixStack, immediate, this.client.world, 0);
			matrixStack.pop();
			immediate.draw();
			RenderSystem.enableCull();
			RenderSystem.disableDepthTest();
		}
	}

	private void renderNausea(DrawContext context, float distortionStrength) {
		int i = context.getScaledWindowWidth();
		int j = context.getScaledWindowHeight();
		context.getMatrices().push();
		float f = MathHelper.lerp(distortionStrength, 2.0F, 1.0F);
		context.getMatrices().translate((float)i / 2.0F, (float)j / 2.0F, 0.0F);
		context.getMatrices().scale(f, f, f);
		context.getMatrices().translate((float)(-i) / 2.0F, (float)(-j) / 2.0F, 0.0F);
		float g = 0.2F * distortionStrength;
		float h = 0.4F * distortionStrength;
		float k = 0.2F * distortionStrength;
		RenderSystem.disableDepthTest();
		RenderSystem.depthMask(false);
		RenderSystem.enableBlend();
		RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ONE, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ONE);
		context.setShaderColor(g, h, k, 1.0F);
		context.drawTexture(NAUSEA_OVERLAY, 0, 0, -90, 0.0F, 0.0F, i, j, i, j);
		context.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.defaultBlendFunc();
		RenderSystem.disableBlend();
		RenderSystem.depthMask(true);
		RenderSystem.enableDepthTest();
		context.getMatrices().pop();
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
	public static ShaderProgram getPositionProgram() {
		return positionProgram;
	}

	@Nullable
	public static ShaderProgram getPositionColorProgram() {
		return positionColorProgram;
	}

	@Nullable
	public static ShaderProgram getPositionColorTexProgram() {
		return positionColorTexProgram;
	}

	@Nullable
	public static ShaderProgram getPositionTexProgram() {
		return positionTexProgram;
	}

	@Nullable
	public static ShaderProgram getPositionTexColorProgram() {
		return positionTexColorProgram;
	}

	@Nullable
	public static ShaderProgram getParticleProgram() {
		return particleProgram;
	}

	@Nullable
	public static ShaderProgram getPositionColorLightmapProgram() {
		return positionColorLightmapProgram;
	}

	@Nullable
	public static ShaderProgram getPositionColorTexLightmapProgram() {
		return positionColorTexLightmapProgram;
	}

	@Nullable
	public static ShaderProgram getRenderTypeSolidProgram() {
		return renderTypeSolidProgram;
	}

	@Nullable
	public static ShaderProgram getRenderTypeCutoutMippedProgram() {
		return renderTypeCutoutMippedProgram;
	}

	@Nullable
	public static ShaderProgram getRenderTypeCutoutProgram() {
		return renderTypeCutoutProgram;
	}

	@Nullable
	public static ShaderProgram getRenderTypeTranslucentProgram() {
		return renderTypeTranslucentProgram;
	}

	@Nullable
	public static ShaderProgram getRenderTypeTranslucentMovingBlockProgram() {
		return renderTypeTranslucentMovingBlockProgram;
	}

	@Nullable
	public static ShaderProgram getRenderTypeArmorCutoutNoCullProgram() {
		return renderTypeArmorCutoutNoCullProgram;
	}

	@Nullable
	public static ShaderProgram getRenderTypeEntitySolidProgram() {
		return renderTypeEntitySolidProgram;
	}

	@Nullable
	public static ShaderProgram getRenderTypeEntityCutoutProgram() {
		return renderTypeEntityCutoutProgram;
	}

	@Nullable
	public static ShaderProgram getRenderTypeEntityCutoutNoNullProgram() {
		return renderTypeEntityCutoutNoNullProgram;
	}

	@Nullable
	public static ShaderProgram getRenderTypeEntityCutoutNoNullZOffsetProgram() {
		return renderTypeEntityCutoutNoNullZOffsetProgram;
	}

	@Nullable
	public static ShaderProgram getRenderTypeItemEntityTranslucentCullProgram() {
		return renderTypeItemEntityTranslucentCullProgram;
	}

	@Nullable
	public static ShaderProgram getRenderTypeEntityTranslucentCullProgram() {
		return renderTypeEntityTranslucentCullProgram;
	}

	@Nullable
	public static ShaderProgram getRenderTypeEntityTranslucentProgram() {
		return renderTypeEntityTranslucentProgram;
	}

	@Nullable
	public static ShaderProgram getRenderTypeEntityTranslucentEmissiveProgram() {
		return renderTypeEntityTranslucentEmissiveProgram;
	}

	@Nullable
	public static ShaderProgram getRenderTypeEntitySmoothCutoutProgram() {
		return renderTypeEntitySmoothCutoutProgram;
	}

	@Nullable
	public static ShaderProgram getRenderTypeBeaconBeamProgram() {
		return renderTypeBeaconBeamProgram;
	}

	@Nullable
	public static ShaderProgram getRenderTypeEntityDecalProgram() {
		return renderTypeEntityDecalProgram;
	}

	@Nullable
	public static ShaderProgram getRenderTypeEntityNoOutlineProgram() {
		return renderTypeEntityNoOutlineProgram;
	}

	@Nullable
	public static ShaderProgram getRenderTypeEntityShadowProgram() {
		return renderTypeEntityShadowProgram;
	}

	@Nullable
	public static ShaderProgram getRenderTypeEntityAlphaProgram() {
		return renderTypeEntityAlphaProgram;
	}

	@Nullable
	public static ShaderProgram getRenderTypeEyesProgram() {
		return renderTypeEyesProgram;
	}

	@Nullable
	public static ShaderProgram getRenderTypeEnergySwirlProgram() {
		return renderTypeEnergySwirlProgram;
	}

	@Nullable
	public static ShaderProgram getRenderTypeBreezeWindProgram() {
		return renderTypeBreezeWindProgram;
	}

	@Nullable
	public static ShaderProgram getRenderTypeLeashProgram() {
		return renderTypeLeashProgram;
	}

	@Nullable
	public static ShaderProgram getRenderTypeWaterMaskProgram() {
		return renderTypeWaterMaskProgram;
	}

	@Nullable
	public static ShaderProgram getRenderTypeOutlineProgram() {
		return renderTypeOutlineProgram;
	}

	@Nullable
	public static ShaderProgram getRenderTypeArmorGlintProgram() {
		return renderTypeArmorGlintProgram;
	}

	@Nullable
	public static ShaderProgram getRenderTypeArmorEntityGlintProgram() {
		return renderTypeArmorEntityGlintProgram;
	}

	@Nullable
	public static ShaderProgram getRenderTypeGlintTranslucentProgram() {
		return renderTypeGlintTranslucentProgram;
	}

	@Nullable
	public static ShaderProgram getRenderTypeGlintProgram() {
		return renderTypeGlintProgram;
	}

	@Nullable
	public static ShaderProgram getRenderTypeGlintDirectProgram() {
		return renderTypeGlintDirectProgram;
	}

	@Nullable
	public static ShaderProgram getRenderTypeEntityGlintProgram() {
		return renderTypeEntityGlintProgram;
	}

	@Nullable
	public static ShaderProgram getRenderTypeEntityGlintDirectProgram() {
		return renderTypeEntityGlintDirectProgram;
	}

	@Nullable
	public static ShaderProgram getRenderTypeTextProgram() {
		return renderTypeTextProgram;
	}

	@Nullable
	public static ShaderProgram getRenderTypeTextBackgroundProgram() {
		return renderTypeTextBackgroundProgram;
	}

	@Nullable
	public static ShaderProgram getRenderTypeTextIntensityProgram() {
		return renderTypeTextIntensityProgram;
	}

	@Nullable
	public static ShaderProgram getRenderTypeTextSeeThroughProgram() {
		return renderTypeTextSeeThroughProgram;
	}

	@Nullable
	public static ShaderProgram getRenderTypeTextBackgroundSeeThroughProgram() {
		return renderTypeTextBackgroundSeeThroughProgram;
	}

	@Nullable
	public static ShaderProgram getRenderTypeTextIntensitySeeThroughProgram() {
		return renderTypeTextIntensitySeeThroughProgram;
	}

	@Nullable
	public static ShaderProgram getRenderTypeLightningProgram() {
		return renderTypeLightningProgram;
	}

	@Nullable
	public static ShaderProgram getRenderTypeTripwireProgram() {
		return renderTypeTripwireProgram;
	}

	@Nullable
	public static ShaderProgram getRenderTypeEndPortalProgram() {
		return renderTypeEndPortalProgram;
	}

	@Nullable
	public static ShaderProgram getRenderTypeEndGatewayProgram() {
		return renderTypeEndGatewayProgram;
	}

	@Nullable
	public static ShaderProgram getRenderTypeCloudsProgram() {
		return renderTypeCloudsProgram;
	}

	/**
	 * {@return the {@code rendertype_lines} shader program}
	 * 
	 * <p>This shader program draws a line by drawing a quad (two triangles
	 * pushed together). Each line takes four vertices. The first vertex is
	 * the line start. The second one is a duplicate of the first one. The
	 * third one is the line end. The fourth one is a duplicate of the third
	 * one.
	 * 
	 * <p>The user of this shader program should use {@link
	 * VertexFormats#LINES} for the vertex format. The normal element is a
	 * direction vector from the starting position to the ending position.
	 * It's used to calculate in what directions the duplicated vertices
	 * should be offset to achieve thick lines. All four vertices should
	 * share the same value for the normal element.
	 * 
	 * <p>The width of the line can be set with {@link
	 * com.mojang.blaze3d.systems.RenderSystem#lineWidth
	 * RenderSystem#lineWidth}.
	 */
	@Nullable
	public static ShaderProgram getRenderTypeLinesProgram() {
		return renderTypeLinesProgram;
	}

	@Nullable
	public static ShaderProgram getRenderTypeCrumblingProgram() {
		return renderTypeCrumblingProgram;
	}

	@Nullable
	public static ShaderProgram getRenderTypeGuiProgram() {
		return renderTypeGuiProgram;
	}

	@Nullable
	public static ShaderProgram getRenderTypeGuiOverlayProgram() {
		return renderTypeGuiOverlayProgram;
	}

	@Nullable
	public static ShaderProgram getRenderTypeGuiTextHighlightProgram() {
		return renderTypeGuiTextHighlightProgram;
	}

	@Nullable
	public static ShaderProgram getRenderTypeGuiGhostRecipeOverlayProgram() {
		return renderTypeGuiGhostRecipeOverlayProgram;
	}

	@Environment(EnvType.CLIENT)
	public static record CachedResourceFactory(ResourceFactory original, Map<Identifier, Resource> cache) implements ResourceFactory {
		@Override
		public Optional<Resource> getResource(Identifier identifier) {
			Resource resource = (Resource)this.cache.get(identifier);
			return resource != null ? Optional.of(resource) : this.original.getResource(identifier);
		}
	}
}
