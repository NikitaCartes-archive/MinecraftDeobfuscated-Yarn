package net.minecraft.client.render;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonSyntaxException;
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
import java.util.Set;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.enums.CameraSubmersionType;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.PostEffectProcessor;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gl.ShaderStage;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameOverlayRenderer;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.util.Pool;
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
import net.minecraft.predicate.entity.EntityPredicates;
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
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class GameRenderer implements AutoCloseable {
	private static final Identifier BLUR_PROCESSOR = Identifier.ofVanilla("shaders/post/blur.json");
	public static final int field_49904 = 10;
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
	private final Pool field_53066 = new Pool(3);
	@Nullable
	PostEffectProcessor postProcessor;
	@Nullable
	private PostEffectProcessor blurPostProcessor;
	private boolean postProcessorEnabled;
	private final Camera camera = new Camera();
	@Nullable
	public ShaderProgram blitScreenProgram;
	private final Map<String, ShaderProgram> programs = Maps.<String, ShaderProgram>newHashMap();
	@Nullable
	private static ShaderProgram positionProgram;
	@Nullable
	private static ShaderProgram positionColorProgram;
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
		this.lightmapTextureManager = new LightmapTextureManager(this, client);
		this.buffers = buffers;
		this.postProcessor = null;
	}

	public void close() {
		this.lightmapTextureManager.close();
		this.overlayTexture.close();
		this.field_53066.close();
		this.disablePostProcessor();
		this.clearPrograms();
		if (this.blurPostProcessor != null) {
			this.blurPostProcessor.close();
		}

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
			this.loadPostProcessor(Identifier.ofVanilla("shaders/post/creeper.json"));
		} else if (entity instanceof SpiderEntity) {
			this.loadPostProcessor(Identifier.ofVanilla("shaders/post/spider.json"));
		} else if (entity instanceof EndermanEntity) {
			this.loadPostProcessor(Identifier.ofVanilla("shaders/post/invert.json"));
		}
	}

	private void loadPostProcessor(Identifier id) {
		if (this.postProcessor != null) {
			this.postProcessor.close();
		}

		try {
			this.postProcessor = PostEffectProcessor.parseEffect(this.resourceManager, this.client.getTextureManager(), id, Set.of(PostEffectProcessor.MAIN));
			this.postProcessorEnabled = true;
		} catch (IOException var3) {
			LOGGER.warn("Failed to load shader: {}", id, var3);
			this.postProcessorEnabled = false;
		} catch (JsonSyntaxException var4) {
			LOGGER.warn("Failed to parse shader: {}", id, var4);
			this.postProcessorEnabled = false;
		}
	}

	private void loadBlurPostProcessor(ResourceFactory resourceFactory) {
		if (this.blurPostProcessor != null) {
			this.blurPostProcessor.close();
		}

		try {
			this.blurPostProcessor = PostEffectProcessor.parseEffect(resourceFactory, this.client.getTextureManager(), BLUR_PROCESSOR, Set.of(PostEffectProcessor.MAIN));
		} catch (IOException var3) {
			LOGGER.warn("Failed to load shader: {}", BLUR_PROCESSOR, var3);
		} catch (JsonSyntaxException var4) {
			LOGGER.warn("Failed to parse shader: {}", BLUR_PROCESSOR, var4);
		}
	}

	public void renderBlur() {
		float f = (float)this.client.options.getMenuBackgroundBlurrinessValue();
		if (this.blurPostProcessor != null && f >= 1.0F) {
			this.blurPostProcessor.setUniforms("Radius", f);
			this.blurPostProcessor.render(this.client.getFramebuffer(), this.field_53066, this.client.getRenderTickCounter());
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
			list2.add(Pair.of(new ShaderProgram(factory, "particle", VertexFormats.POSITION_TEXTURE_COLOR_LIGHT), program -> particleProgram = program));
			list2.add(Pair.of(new ShaderProgram(factory, "position", VertexFormats.POSITION), program -> positionProgram = program));
			list2.add(Pair.of(new ShaderProgram(factory, "position_color", VertexFormats.POSITION_COLOR), program -> positionColorProgram = program));
			list2.add(
				Pair.of(new ShaderProgram(factory, "position_color_lightmap", VertexFormats.POSITION_COLOR_LIGHT), program -> positionColorLightmapProgram = program)
			);
			list2.add(
				Pair.of(
					new ShaderProgram(factory, "position_color_tex_lightmap", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT),
					program -> positionColorTexLightmapProgram = program
				)
			);
			list2.add(Pair.of(new ShaderProgram(factory, "position_tex", VertexFormats.POSITION_TEXTURE), program -> positionTexProgram = program));
			list2.add(Pair.of(new ShaderProgram(factory, "position_tex_color", VertexFormats.POSITION_TEXTURE_COLOR), program -> positionTexColorProgram = program));
			list2.add(
				Pair.of(new ShaderProgram(factory, "rendertype_solid", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL), program -> renderTypeSolidProgram = program)
			);
			list2.add(
				Pair.of(
					new ShaderProgram(factory, "rendertype_cutout_mipped", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL),
					program -> renderTypeCutoutMippedProgram = program
				)
			);
			list2.add(
				Pair.of(new ShaderProgram(factory, "rendertype_cutout", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL), program -> renderTypeCutoutProgram = program)
			);
			list2.add(
				Pair.of(
					new ShaderProgram(factory, "rendertype_translucent", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL), program -> renderTypeTranslucentProgram = program
				)
			);
			list2.add(
				Pair.of(
					new ShaderProgram(factory, "rendertype_translucent_moving_block", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL),
					program -> renderTypeTranslucentMovingBlockProgram = program
				)
			);
			list2.add(
				Pair.of(
					new ShaderProgram(factory, "rendertype_armor_cutout_no_cull", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL),
					program -> renderTypeArmorCutoutNoCullProgram = program
				)
			);
			list2.add(
				Pair.of(
					new ShaderProgram(factory, "rendertype_entity_solid", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL),
					program -> renderTypeEntitySolidProgram = program
				)
			);
			list2.add(
				Pair.of(
					new ShaderProgram(factory, "rendertype_entity_cutout", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL),
					program -> renderTypeEntityCutoutProgram = program
				)
			);
			list2.add(
				Pair.of(
					new ShaderProgram(factory, "rendertype_entity_cutout_no_cull", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL),
					program -> renderTypeEntityCutoutNoNullProgram = program
				)
			);
			list2.add(
				Pair.of(
					new ShaderProgram(factory, "rendertype_entity_cutout_no_cull_z_offset", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL),
					program -> renderTypeEntityCutoutNoNullZOffsetProgram = program
				)
			);
			list2.add(
				Pair.of(
					new ShaderProgram(factory, "rendertype_item_entity_translucent_cull", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL),
					shaderProgram -> renderTypeItemEntityTranslucentCullProgram = shaderProgram
				)
			);
			list2.add(
				Pair.of(
					new ShaderProgram(factory, "rendertype_entity_translucent_cull", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL),
					program -> renderTypeEntityTranslucentCullProgram = program
				)
			);
			list2.add(
				Pair.of(
					new ShaderProgram(factory, "rendertype_entity_translucent", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL),
					program -> renderTypeEntityTranslucentProgram = program
				)
			);
			list2.add(
				Pair.of(
					new ShaderProgram(factory, "rendertype_entity_translucent_emissive", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL),
					program -> renderTypeEntityTranslucentEmissiveProgram = program
				)
			);
			list2.add(
				Pair.of(
					new ShaderProgram(factory, "rendertype_entity_smooth_cutout", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL),
					program -> renderTypeEntitySmoothCutoutProgram = program
				)
			);
			list2.add(
				Pair.of(
					new ShaderProgram(factory, "rendertype_beacon_beam", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL), program -> renderTypeBeaconBeamProgram = program
				)
			);
			list2.add(
				Pair.of(
					new ShaderProgram(factory, "rendertype_entity_decal", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL),
					program -> renderTypeEntityDecalProgram = program
				)
			);
			list2.add(
				Pair.of(
					new ShaderProgram(factory, "rendertype_entity_no_outline", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL),
					program -> renderTypeEntityNoOutlineProgram = program
				)
			);
			list2.add(
				Pair.of(
					new ShaderProgram(factory, "rendertype_entity_shadow", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL),
					program -> renderTypeEntityShadowProgram = program
				)
			);
			list2.add(
				Pair.of(
					new ShaderProgram(factory, "rendertype_entity_alpha", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL),
					program -> renderTypeEntityAlphaProgram = program
				)
			);
			list2.add(
				Pair.of(
					new ShaderProgram(factory, "rendertype_eyes", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL), program -> renderTypeEyesProgram = program
				)
			);
			list2.add(
				Pair.of(
					new ShaderProgram(factory, "rendertype_energy_swirl", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL),
					program -> renderTypeEnergySwirlProgram = program
				)
			);
			list2.add(Pair.of(new ShaderProgram(factory, "rendertype_leash", VertexFormats.POSITION_COLOR_LIGHT), program -> renderTypeLeashProgram = program));
			list2.add(Pair.of(new ShaderProgram(factory, "rendertype_water_mask", VertexFormats.POSITION), program -> renderTypeWaterMaskProgram = program));
			list2.add(Pair.of(new ShaderProgram(factory, "rendertype_outline", VertexFormats.POSITION_TEXTURE_COLOR), program -> renderTypeOutlineProgram = program));
			list2.add(
				Pair.of(new ShaderProgram(factory, "rendertype_armor_entity_glint", VertexFormats.POSITION_TEXTURE), program -> renderTypeArmorEntityGlintProgram = program)
			);
			list2.add(
				Pair.of(new ShaderProgram(factory, "rendertype_glint_translucent", VertexFormats.POSITION_TEXTURE), program -> renderTypeGlintTranslucentProgram = program)
			);
			list2.add(Pair.of(new ShaderProgram(factory, "rendertype_glint", VertexFormats.POSITION_TEXTURE), program -> renderTypeGlintProgram = program));
			list2.add(Pair.of(new ShaderProgram(factory, "rendertype_entity_glint", VertexFormats.POSITION_TEXTURE), program -> renderTypeEntityGlintProgram = program));
			list2.add(
				Pair.of(
					new ShaderProgram(factory, "rendertype_entity_glint_direct", VertexFormats.POSITION_TEXTURE), program -> renderTypeEntityGlintDirectProgram = program
				)
			);
			list2.add(Pair.of(new ShaderProgram(factory, "rendertype_text", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT), program -> renderTypeTextProgram = program));
			list2.add(
				Pair.of(new ShaderProgram(factory, "rendertype_text_background", VertexFormats.POSITION_COLOR_LIGHT), program -> renderTypeTextBackgroundProgram = program)
			);
			list2.add(
				Pair.of(
					new ShaderProgram(factory, "rendertype_text_intensity", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT), program -> renderTypeTextIntensityProgram = program
				)
			);
			list2.add(
				Pair.of(
					new ShaderProgram(factory, "rendertype_text_see_through", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT),
					program -> renderTypeTextSeeThroughProgram = program
				)
			);
			list2.add(
				Pair.of(
					new ShaderProgram(factory, "rendertype_text_background_see_through", VertexFormats.POSITION_COLOR_LIGHT),
					program -> renderTypeTextBackgroundSeeThroughProgram = program
				)
			);
			list2.add(
				Pair.of(
					new ShaderProgram(factory, "rendertype_text_intensity_see_through", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT),
					program -> renderTypeTextIntensitySeeThroughProgram = program
				)
			);
			list2.add(Pair.of(new ShaderProgram(factory, "rendertype_lightning", VertexFormats.POSITION_COLOR), program -> renderTypeLightningProgram = program));
			list2.add(
				Pair.of(
					new ShaderProgram(factory, "rendertype_tripwire", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL), program -> renderTypeTripwireProgram = program
				)
			);
			list2.add(Pair.of(new ShaderProgram(factory, "rendertype_end_portal", VertexFormats.POSITION), program -> renderTypeEndPortalProgram = program));
			list2.add(Pair.of(new ShaderProgram(factory, "rendertype_end_gateway", VertexFormats.POSITION), program -> renderTypeEndGatewayProgram = program));
			list2.add(Pair.of(new ShaderProgram(factory, "rendertype_clouds", VertexFormats.POSITION_COLOR), program -> renderTypeCloudsProgram = program));
			list2.add(Pair.of(new ShaderProgram(factory, "rendertype_lines", VertexFormats.LINES), program -> renderTypeLinesProgram = program));
			list2.add(
				Pair.of(
					new ShaderProgram(factory, "rendertype_crumbling", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL), program -> renderTypeCrumblingProgram = program
				)
			);
			list2.add(Pair.of(new ShaderProgram(factory, "rendertype_gui", VertexFormats.POSITION_COLOR), program -> renderTypeGuiProgram = program));
			list2.add(Pair.of(new ShaderProgram(factory, "rendertype_gui_overlay", VertexFormats.POSITION_COLOR), program -> renderTypeGuiOverlayProgram = program));
			list2.add(
				Pair.of(new ShaderProgram(factory, "rendertype_gui_text_highlight", VertexFormats.POSITION_COLOR), program -> renderTypeGuiTextHighlightProgram = program)
			);
			list2.add(
				Pair.of(
					new ShaderProgram(factory, "rendertype_gui_ghost_recipe_overlay", VertexFormats.POSITION_COLOR),
					program -> renderTypeGuiGhostRecipeOverlayProgram = program
				)
			);
			list2.add(
				Pair.of(
					new ShaderProgram(factory, "rendertype_breeze_wind", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL),
					program -> renderTypeBreezeWindProgram = program
				)
			);
			this.loadBlurPostProcessor(factory);
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
		this.lightmapTextureManager.loadShaderProgram(factory);
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
		this.ticks++;
		if (this.client.world.getTickManager().shouldTick()) {
			this.client.worldRenderer.method_62209(this.camera);
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
	}

	@Nullable
	public PostEffectProcessor getPostProcessor() {
		return this.postProcessor;
	}

	public void onResized(int width, int height) {
		this.field_53066.clear();
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
		EntityHitResult entityHitResult = ProjectileUtil.raycast(camera, vec3d, vec3d3, box, EntityPredicates.CAN_HIT, e);
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
		float g;
		if (this.client.getCameraEntity() instanceof AbstractClientPlayerEntity abstractClientPlayerEntity) {
			GameOptions gameOptions = this.client.options;
			boolean bl = gameOptions.getPerspective().isFirstPerson();
			float f = gameOptions.getFovEffectScale().getValue().floatValue();
			g = MathHelper.lerp(f, 1.0F, abstractClientPlayerEntity.getFovMultiplier(bl));
		} else {
			g = 1.0F;
		}

		this.lastFovMultiplier = this.fovMultiplier;
		this.fovMultiplier = this.fovMultiplier + (g - this.fovMultiplier) * 0.5F;
		this.fovMultiplier = MathHelper.clamp(this.fovMultiplier, 0.1F, 1.5F);
	}

	private float getFov(Camera camera, float tickDelta, boolean changingFov) {
		if (this.renderingPanorama) {
			return 90.0F;
		} else {
			float f = 70.0F;
			if (changingFov) {
				f = (float)this.client.options.getFov().getValue().intValue();
				f *= MathHelper.lerp(tickDelta, this.lastFovMultiplier, this.fovMultiplier);
			}

			if (camera.getFocusedEntity() instanceof LivingEntity livingEntity && livingEntity.isDead()) {
				float g = Math.min((float)livingEntity.deathTime + tickDelta, 20.0F);
				f /= (1.0F - 500.0F / (g + 500.0F)) * 2.0F + 1.0F;
			}

			CameraSubmersionType cameraSubmersionType = camera.getSubmersionType();
			if (cameraSubmersionType == CameraSubmersionType.LAVA || cameraSubmersionType == CameraSubmersionType.WATER) {
				float g = this.client.options.getFovEffectScale().getValue().floatValue();
				f *= MathHelper.lerp(g, 1.0F, 0.85714287F);
			}

			return f;
		}
	}

	private void tiltViewWhenHurt(MatrixStack matrices, float tickDelta) {
		if (this.client.getCameraEntity() instanceof LivingEntity livingEntity) {
			float f = (float)livingEntity.hurtTime - tickDelta;
			if (livingEntity.isDead()) {
				float g = Math.min((float)livingEntity.deathTime + tickDelta, 20.0F);
				matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(40.0F - 8000.0F / (g + 200.0F)));
			}

			if (f < 0.0F) {
				return;
			}

			f /= (float)livingEntity.maxHurtTime;
			f = MathHelper.sin(f * f * f * f * (float) Math.PI);
			float g = livingEntity.getDamageTiltYaw();
			matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-g));
			float h = (float)((double)(-f) * 14.0 * this.client.options.getDamageTiltStrength().getValue());
			matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(h));
			matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(g));
		}
	}

	private void bobView(MatrixStack matrices, float tickDelta) {
		if (this.client.getCameraEntity() instanceof AbstractClientPlayerEntity abstractClientPlayerEntity) {
			float var7 = abstractClientPlayerEntity.field_53039 - abstractClientPlayerEntity.field_53038;
			float g = -(abstractClientPlayerEntity.field_53039 + var7 * tickDelta);
			float h = MathHelper.lerp(tickDelta, abstractClientPlayerEntity.prevStrideDistance, abstractClientPlayerEntity.strideDistance);
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
		this.renderWorld(RenderTickCounter.ZERO);
		this.zoom = 1.0F;
	}

	private void renderHand(Camera camera, float tickDelta, Matrix4f matrix4f) {
		if (!this.renderingPanorama) {
			Matrix4f matrix4f2 = this.getBasicProjectionMatrix(this.getFov(camera, tickDelta, false));
			RenderSystem.setProjectionMatrix(matrix4f2, VertexSorter.BY_DISTANCE);
			MatrixStack matrixStack = new MatrixStack();
			matrixStack.push();
			matrixStack.multiplyPositionMatrix(matrix4f.invert(new Matrix4f()));
			Matrix4fStack matrix4fStack = RenderSystem.getModelViewStack();
			matrix4fStack.pushMatrix().mul(matrix4f);
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
			matrixStack.pop();
			if (this.client.options.getPerspective().isFirstPerson() && !bl) {
				InGameOverlayRenderer.renderOverlays(this.client, matrixStack);
			}
		}
	}

	public Matrix4f getBasicProjectionMatrix(float f) {
		Matrix4f matrix4f = new Matrix4f();
		if (this.zoom != 1.0F) {
			matrix4f.translate(this.zoomX, -this.zoomY, 0.0F);
			matrix4f.scale(this.zoom, this.zoom, 1.0F);
		}

		return matrix4f.perspective(
			f * (float) (Math.PI / 180.0),
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

	public void render(RenderTickCounter tickCounter, boolean tick) {
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
			boolean bl = this.client.isFinishedLoading();
			int i = (int)(this.client.mouse.getX() * (double)this.client.getWindow().getScaledWidth() / (double)this.client.getWindow().getWidth());
			int j = (int)(this.client.mouse.getY() * (double)this.client.getWindow().getScaledHeight() / (double)this.client.getWindow().getHeight());
			RenderSystem.viewport(0, 0, this.client.getWindow().getFramebufferWidth(), this.client.getWindow().getFramebufferHeight());
			if (bl && tick && this.client.world != null) {
				this.client.getProfiler().push("level");
				this.renderWorld(tickCounter);
				this.updateWorldIcon();
				this.client.worldRenderer.drawEntityOutlinesFramebuffer();
				if (this.postProcessor != null && this.postProcessorEnabled) {
					RenderSystem.disableBlend();
					RenderSystem.disableDepthTest();
					RenderSystem.resetTextureMatrix();
					this.postProcessor.render(this.client.getFramebuffer(), this.field_53066, tickCounter);
				}

				this.client.getFramebuffer().beginWrite(true);
			}

			Window window = this.client.getWindow();
			RenderSystem.clear(256);
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
			DiffuseLighting.enableGuiDepthLighting();
			DrawContext drawContext = new DrawContext(this.client, this.buffers.getEntityVertexConsumers());
			if (bl && tick && this.client.world != null) {
				this.client.getProfiler().swap("gui");
				if (!this.client.options.hudHidden) {
					this.renderFloatingItem(drawContext, tickCounter.getTickDelta(false));
				}

				this.client.inGameHud.render(drawContext, tickCounter);
				drawContext.draw();
				RenderSystem.clear(256);
				this.client.getProfiler().pop();
			}

			if (this.client.getOverlay() != null) {
				try {
					this.client.getOverlay().render(drawContext, i, j, tickCounter.getLastFrameDuration());
				} catch (Throwable var15) {
					CrashReport crashReport = CrashReport.create(var15, "Rendering overlay");
					CrashReportSection crashReportSection = crashReport.addElement("Overlay render details");
					crashReportSection.add("Overlay name", (CrashCallable<String>)(() -> this.client.getOverlay().getClass().getCanonicalName()));
					throw new CrashException(crashReport);
				}
			} else if (bl && this.client.currentScreen != null) {
				try {
					this.client.currentScreen.renderWithTooltip(drawContext, i, j, tickCounter.getLastFrameDuration());
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

				try {
					if (this.client.currentScreen != null) {
						this.client.currentScreen.updateNarrator();
					}
				} catch (Throwable var13) {
					CrashReport crashReport = CrashReport.create(var13, "Narrating screen");
					CrashReportSection crashReportSection = crashReport.addElement("Screen details");
					crashReportSection.add("Screen name", (CrashCallable<String>)(() -> this.client.currentScreen.getClass().getCanonicalName()));
					throw new CrashException(crashReport);
				}
			}

			if (bl && tick && this.client.world != null) {
				this.client.inGameHud.renderAutosaveIndicator(drawContext, tickCounter);
			}

			if (bl) {
				this.client.getProfiler().push("toasts");
				this.client.getToastManager().draw(drawContext);
				this.client.getProfiler().pop();
			}

			drawContext.draw();
			matrix4fStack.popMatrix();
			this.field_53066.decrementLifespan();
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
						Registry<Block> registry = this.client.world.getRegistryManager().get(RegistryKeys.BLOCK);
						bl = !itemStack.isEmpty() && (itemStack.canBreak(cachedBlockPosition) || itemStack.canPlaceOn(cachedBlockPosition));
					}
				}
			}

			return bl;
		}
	}

	public void renderWorld(RenderTickCounter renderTickCounter) {
		float f = renderTickCounter.getTickDelta(true);
		this.lightmapTextureManager.update(f);
		if (this.client.getCameraEntity() == null) {
			this.client.setCameraEntity(this.client.player);
		}

		this.updateCrosshairTarget(f);
		this.client.getProfiler().push("center");
		boolean bl = this.shouldRenderBlockOutline();
		this.client.getProfiler().swap("camera");
		Camera camera = this.camera;
		Entity entity = (Entity)(this.client.getCameraEntity() == null ? this.client.player : this.client.getCameraEntity());
		float g = this.client.world.getTickManager().shouldSkipTick(entity) ? 1.0F : f;
		camera.update(this.client.world, entity, !this.client.options.getPerspective().isFirstPerson(), this.client.options.getPerspective().isFrontView(), g);
		this.viewDistance = (float)(this.client.options.getClampedViewDistance() * 16);
		float h = this.getFov(camera, f, true);
		Matrix4f matrix4f = this.getBasicProjectionMatrix(h);
		MatrixStack matrixStack = new MatrixStack();
		this.tiltViewWhenHurt(matrixStack, camera.getLastTickDelta());
		if (this.client.options.getBobView().getValue()) {
			this.bobView(matrixStack, camera.getLastTickDelta());
		}

		matrix4f.mul(matrixStack.peek().getPositionMatrix());
		float i = this.client.options.getDistortionEffectScale().getValue().floatValue();
		float j = MathHelper.lerp(f, this.client.player.prevNauseaIntensity, this.client.player.nauseaIntensity) * i * i;
		if (j > 0.0F) {
			int k = this.client.player.hasStatusEffect(StatusEffects.NAUSEA) ? 7 : 20;
			float l = 5.0F / (j * j + 5.0F) - j * 0.04F;
			l *= l;
			Vector3f vector3f = new Vector3f(0.0F, MathHelper.SQUARE_ROOT_OF_TWO / 2.0F, MathHelper.SQUARE_ROOT_OF_TWO / 2.0F);
			float m = ((float)this.ticks + f) * (float)k * (float) (Math.PI / 180.0);
			matrix4f.rotate(m, vector3f);
			matrix4f.scale(1.0F / l, 1.0F, 1.0F);
			matrix4f.rotate(-m, vector3f);
		}

		float n = Math.max(h, (float)this.client.options.getFov().getValue().intValue());
		Matrix4f matrix4f2 = this.getBasicProjectionMatrix(n);
		RenderSystem.setProjectionMatrix(matrix4f, VertexSorter.BY_DISTANCE);
		Quaternionf quaternionf = camera.getRotation().conjugate(new Quaternionf());
		Matrix4f matrix4f3 = new Matrix4f().rotation(quaternionf);
		this.client.worldRenderer.setupFrustum(camera.getPos(), matrix4f3, matrix4f2);
		this.client.getFramebuffer().beginWrite(true);
		this.client.worldRenderer.render(this.field_53066, renderTickCounter, bl, camera, this, this.lightmapTextureManager, matrix4f3, matrix4f);
		this.client.getProfiler().swap("hand");
		if (this.renderHand) {
			RenderSystem.clear(256);
			this.renderHand(camera, f, matrix4f3);
		}

		this.client.getProfiler().pop();
	}

	public void reset() {
		this.floatingItem = null;
		this.client.getMapTextureManager().clear();
		this.camera.reset();
		this.hasWorldIcon = false;
	}

	public void showFloatingItem(ItemStack floatingItem) {
		this.floatingItem = floatingItem;
		this.floatingItemTimeLeft = 40;
		this.floatingItemWidth = this.random.nextFloat() * 2.0F - 1.0F;
		this.floatingItemHeight = this.random.nextFloat() * 2.0F - 1.0F;
	}

	private void renderFloatingItem(DrawContext context, float tickDelta) {
		if (this.floatingItem != null && this.floatingItemTimeLeft > 0) {
			int i = 40 - this.floatingItemTimeLeft;
			float f = ((float)i + tickDelta) / 40.0F;
			float g = f * f;
			float h = f * g;
			float j = 10.25F * h * g - 24.95F * g * g + 25.5F * h - 13.8F * g + 4.0F * f;
			float k = j * (float) Math.PI;
			float l = this.floatingItemWidth * (float)(context.getScaledWindowWidth() / 4);
			float m = this.floatingItemHeight * (float)(context.getScaledWindowHeight() / 4);
			MatrixStack matrixStack = new MatrixStack();
			matrixStack.push();
			matrixStack.translate(
				(float)(context.getScaledWindowWidth() / 2) + l * MathHelper.abs(MathHelper.sin(k * 2.0F)),
				(float)(context.getScaledWindowHeight() / 2) + m * MathHelper.abs(MathHelper.sin(k * 2.0F)),
				-50.0F
			);
			float n = 50.0F + 175.0F * MathHelper.sin(k);
			matrixStack.scale(n, -n, n);
			matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(900.0F * MathHelper.abs(MathHelper.sin(k))));
			matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(6.0F * MathHelper.cos(f * 8.0F)));
			matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(6.0F * MathHelper.cos(f * 8.0F)));
			this.client
				.getItemRenderer()
				.renderItem(
					this.floatingItem, ModelTransformationMode.FIXED, 15728880, OverlayTexture.DEFAULT_UV, matrixStack, context.getVertexConsumers(), this.client.world, 0
				);
			matrixStack.pop();
		}
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
