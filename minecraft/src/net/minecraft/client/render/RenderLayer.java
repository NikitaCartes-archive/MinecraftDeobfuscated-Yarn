package net.minecraft.client.render;

import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.Hash.Strategy;
import it.unimi.dsi.fastutil.objects.ObjectOpenCustomHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalDouble;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.block.entity.EndPortalBlockEntityRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public abstract class RenderLayer extends RenderPhase {
	private static final RenderLayer SOLID = method_24049(
		"solid",
		VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL,
		7,
		2097152,
		true,
		false,
		RenderLayer.MultiPhaseData.builder().shadeModel(SMOOTH_SHADE_MODEL).lightmap(ENABLE_LIGHTMAP).texture(MIPMAP_BLOCK_ATLAS_TEXTURE).build(true)
	);
	private static final RenderLayer CUTOUT_MIPPED = method_24049(
		"cutout_mipped",
		VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL,
		7,
		131072,
		true,
		false,
		RenderLayer.MultiPhaseData.builder()
			.shadeModel(SMOOTH_SHADE_MODEL)
			.lightmap(ENABLE_LIGHTMAP)
			.texture(MIPMAP_BLOCK_ATLAS_TEXTURE)
			.alpha(HALF_ALPHA)
			.build(true)
	);
	private static final RenderLayer CUTOUT = method_24049(
		"cutout",
		VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL,
		7,
		131072,
		true,
		false,
		RenderLayer.MultiPhaseData.builder().shadeModel(SMOOTH_SHADE_MODEL).lightmap(ENABLE_LIGHTMAP).texture(BLOCK_ATLAS_TEXTURE).alpha(HALF_ALPHA).build(true)
	);
	private static final RenderLayer TRANSLUCENT = method_24049(
		"translucent", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL, 7, 262144, true, true, method_24051()
	);
	private static final RenderLayer TRANSLUCENT_NO_CRUMBLING = method_24049(
		"translucent_no_crumbling", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL, 7, 262144, false, true, method_24051()
	);
	private static final RenderLayer LEASH = method_24048(
		"leash",
		VertexFormats.POSITION_COLOR_LIGHT,
		7,
		256,
		RenderLayer.MultiPhaseData.builder().texture(NO_TEXTURE).cull(DISABLE_CULLING).lightmap(ENABLE_LIGHTMAP).build(false)
	);
	private static final RenderLayer WATER_MASK = method_24048(
		"water_mask", VertexFormats.POSITION, 7, 256, RenderLayer.MultiPhaseData.builder().texture(NO_TEXTURE).writeMaskState(DEPTH_MASK).build(false)
	);
	private static final RenderLayer GLINT = method_24048(
		"glint",
		VertexFormats.POSITION_TEXTURE,
		7,
		256,
		RenderLayer.MultiPhaseData.builder()
			.texture(new RenderPhase.Texture(ItemRenderer.field_21010, false, false))
			.writeMaskState(COLOR_MASK)
			.cull(DISABLE_CULLING)
			.depthTest(EQUAL_DEPTH_TEST)
			.transparency(GLINT_TRANSPARENCY)
			.texturing(GLINT_TEXTURING)
			.build(false)
	);
	private static final RenderLayer ENTITY_GLINT = method_24048(
		"entity_glint",
		VertexFormats.POSITION_TEXTURE,
		7,
		256,
		RenderLayer.MultiPhaseData.builder()
			.texture(new RenderPhase.Texture(ItemRenderer.field_21010, false, false))
			.writeMaskState(COLOR_MASK)
			.cull(DISABLE_CULLING)
			.depthTest(EQUAL_DEPTH_TEST)
			.transparency(GLINT_TRANSPARENCY)
			.texturing(ENTITY_GLINT_TEXTURING)
			.build(false)
	);
	private static final RenderLayer LIGHTNING = method_24049(
		"lightning",
		VertexFormats.POSITION_COLOR,
		7,
		256,
		false,
		true,
		RenderLayer.MultiPhaseData.builder().writeMaskState(COLOR_MASK).transparency(LIGHTNING_TRANSPARENCY).shadeModel(SMOOTH_SHADE_MODEL).build(false)
	);
	public static final RenderLayer.MultiPhase LINES = method_24048(
		"lines",
		VertexFormats.POSITION_COLOR,
		1,
		256,
		RenderLayer.MultiPhaseData.builder()
			.lineWidth(new RenderPhase.LineWidth(OptionalDouble.empty()))
			.layering(PROJECTION_LAYERING)
			.transparency(TRANSLUCENT_TRANSPARENCY)
			.build(false)
	);
	private final VertexFormat vertexFormat;
	private final int drawMode;
	private final int expectedBufferSize;
	private final boolean field_20975;
	private final boolean field_21402;

	public static RenderLayer getSolid() {
		return SOLID;
	}

	public static RenderLayer getCutoutMipped() {
		return CUTOUT_MIPPED;
	}

	public static RenderLayer getCutout() {
		return CUTOUT;
	}

	private static RenderLayer.MultiPhaseData method_24051() {
		return RenderLayer.MultiPhaseData.builder()
			.shadeModel(SMOOTH_SHADE_MODEL)
			.lightmap(ENABLE_LIGHTMAP)
			.texture(MIPMAP_BLOCK_ATLAS_TEXTURE)
			.transparency(TRANSLUCENT_TRANSPARENCY)
			.build(true);
	}

	public static RenderLayer getTranslucent() {
		return TRANSLUCENT;
	}

	public static RenderLayer getTranslucentNoCrumbling() {
		return TRANSLUCENT_NO_CRUMBLING;
	}

	public static RenderLayer getEntitySolid(Identifier identifier) {
		RenderLayer.MultiPhaseData multiPhaseData = RenderLayer.MultiPhaseData.builder()
			.texture(new RenderPhase.Texture(identifier, false, false))
			.transparency(NO_TRANSPARENCY)
			.diffuseLighting(ENABLE_DIFFUSE_LIGHTING)
			.lightmap(ENABLE_LIGHTMAP)
			.overlay(ENABLE_OVERLAY_COLOR)
			.build(true);
		return method_24049("entity_solid", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL, 7, 256, true, false, multiPhaseData);
	}

	public static RenderLayer getEntityCutout(Identifier identifier) {
		RenderLayer.MultiPhaseData multiPhaseData = RenderLayer.MultiPhaseData.builder()
			.texture(new RenderPhase.Texture(identifier, false, false))
			.transparency(NO_TRANSPARENCY)
			.diffuseLighting(ENABLE_DIFFUSE_LIGHTING)
			.alpha(ONE_TENTH_ALPHA)
			.lightmap(ENABLE_LIGHTMAP)
			.overlay(ENABLE_OVERLAY_COLOR)
			.build(true);
		return method_24049("entity_cutout", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL, 7, 256, true, false, multiPhaseData);
	}

	public static RenderLayer getEntityCutoutNoCull(Identifier identifier) {
		RenderLayer.MultiPhaseData multiPhaseData = RenderLayer.MultiPhaseData.builder()
			.texture(new RenderPhase.Texture(identifier, false, false))
			.transparency(NO_TRANSPARENCY)
			.diffuseLighting(ENABLE_DIFFUSE_LIGHTING)
			.alpha(ONE_TENTH_ALPHA)
			.cull(DISABLE_CULLING)
			.lightmap(ENABLE_LIGHTMAP)
			.overlay(ENABLE_OVERLAY_COLOR)
			.build(true);
		return method_24049("entity_cutout_no_cull", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL, 7, 256, true, false, multiPhaseData);
	}

	public static RenderLayer getEntityTranslucentCull(Identifier identifier) {
		RenderLayer.MultiPhaseData multiPhaseData = RenderLayer.MultiPhaseData.builder()
			.texture(new RenderPhase.Texture(identifier, false, false))
			.transparency(TRANSLUCENT_TRANSPARENCY)
			.diffuseLighting(ENABLE_DIFFUSE_LIGHTING)
			.alpha(ONE_TENTH_ALPHA)
			.lightmap(ENABLE_LIGHTMAP)
			.overlay(ENABLE_OVERLAY_COLOR)
			.build(true);
		return method_24049("entity_translucent_cull", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL, 7, 256, true, true, multiPhaseData);
	}

	public static RenderLayer getEntityTranslucent(Identifier identifier) {
		RenderLayer.MultiPhaseData multiPhaseData = RenderLayer.MultiPhaseData.builder()
			.texture(new RenderPhase.Texture(identifier, false, false))
			.transparency(TRANSLUCENT_TRANSPARENCY)
			.diffuseLighting(ENABLE_DIFFUSE_LIGHTING)
			.alpha(ONE_TENTH_ALPHA)
			.cull(DISABLE_CULLING)
			.lightmap(ENABLE_LIGHTMAP)
			.overlay(ENABLE_OVERLAY_COLOR)
			.build(true);
		return method_24049("entity_translucent", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL, 7, 256, true, true, multiPhaseData);
	}

	public static RenderLayer getEntitySmoothCutout(Identifier texture) {
		RenderLayer.MultiPhaseData multiPhaseData = RenderLayer.MultiPhaseData.builder()
			.texture(new RenderPhase.Texture(texture, false, false))
			.alpha(HALF_ALPHA)
			.diffuseLighting(ENABLE_DIFFUSE_LIGHTING)
			.shadeModel(SMOOTH_SHADE_MODEL)
			.cull(DISABLE_CULLING)
			.lightmap(ENABLE_LIGHTMAP)
			.build(true);
		return method_24048("entity_smooth_cutout", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL, 7, 256, multiPhaseData);
	}

	public static RenderLayer getBeaconBeam(Identifier identifier, boolean bl) {
		RenderLayer.MultiPhaseData multiPhaseData = RenderLayer.MultiPhaseData.builder()
			.texture(new RenderPhase.Texture(identifier, false, false))
			.transparency(bl ? TRANSLUCENT_TRANSPARENCY : NO_TRANSPARENCY)
			.writeMaskState(bl ? COLOR_MASK : ALL_MASK)
			.fog(NO_FOG)
			.build(false);
		return method_24049("beacon_beam", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL, 7, 256, false, true, multiPhaseData);
	}

	public static RenderLayer getEntityDecal(Identifier texture) {
		RenderLayer.MultiPhaseData multiPhaseData = RenderLayer.MultiPhaseData.builder()
			.texture(new RenderPhase.Texture(texture, false, false))
			.diffuseLighting(ENABLE_DIFFUSE_LIGHTING)
			.alpha(ONE_TENTH_ALPHA)
			.depthTest(EQUAL_DEPTH_TEST)
			.cull(DISABLE_CULLING)
			.lightmap(ENABLE_LIGHTMAP)
			.overlay(ENABLE_OVERLAY_COLOR)
			.build(false);
		return method_24048("entity_decal", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL, 7, 256, multiPhaseData);
	}

	public static RenderLayer getEntityNoOutline(Identifier texture) {
		RenderLayer.MultiPhaseData multiPhaseData = RenderLayer.MultiPhaseData.builder()
			.texture(new RenderPhase.Texture(texture, false, false))
			.transparency(TRANSLUCENT_TRANSPARENCY)
			.diffuseLighting(ENABLE_DIFFUSE_LIGHTING)
			.alpha(ONE_TENTH_ALPHA)
			.cull(DISABLE_CULLING)
			.lightmap(ENABLE_LIGHTMAP)
			.overlay(ENABLE_OVERLAY_COLOR)
			.writeMaskState(COLOR_MASK)
			.build(false);
		return method_24049("entity_no_outline", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL, 7, 256, false, true, multiPhaseData);
	}

	public static RenderLayer getEntityAlpha(Identifier texture, float alpha) {
		RenderLayer.MultiPhaseData multiPhaseData = RenderLayer.MultiPhaseData.builder()
			.texture(new RenderPhase.Texture(texture, false, false))
			.alpha(new RenderPhase.Alpha(alpha))
			.cull(DISABLE_CULLING)
			.build(true);
		return method_24048("entity_alpha", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL, 7, 256, multiPhaseData);
	}

	public static RenderLayer getEyes(Identifier texture) {
		RenderPhase.Texture texture2 = new RenderPhase.Texture(texture, false, false);
		return method_24049(
			"eyes",
			VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL,
			7,
			256,
			false,
			true,
			RenderLayer.MultiPhaseData.builder().texture(texture2).transparency(ADDITIVE_TRANSPARENCY).writeMaskState(COLOR_MASK).fog(BLACK_FOG).build(false)
		);
	}

	public static RenderLayer getEnergySwirl(Identifier texture, float x, float y) {
		return method_24049(
			"energy_swirl",
			VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL,
			7,
			256,
			false,
			true,
			RenderLayer.MultiPhaseData.builder()
				.texture(new RenderPhase.Texture(texture, false, false))
				.texturing(new RenderPhase.OffsetTexturing(x, y))
				.fog(BLACK_FOG)
				.transparency(ADDITIVE_TRANSPARENCY)
				.diffuseLighting(ENABLE_DIFFUSE_LIGHTING)
				.alpha(ONE_TENTH_ALPHA)
				.cull(DISABLE_CULLING)
				.lightmap(ENABLE_LIGHTMAP)
				.overlay(ENABLE_OVERLAY_COLOR)
				.build(false)
		);
	}

	public static RenderLayer getLeash() {
		return LEASH;
	}

	public static RenderLayer getWaterMask() {
		return WATER_MASK;
	}

	private static RenderLayer getOutline(Identifier texture) {
		return method_24048(
			"outline",
			VertexFormats.POSITION_COLOR_TEXTURE,
			7,
			256,
			RenderLayer.MultiPhaseData.builder()
				.texture(new RenderPhase.Texture(texture, false, false))
				.cull(DISABLE_CULLING)
				.depthTest(ALWAYS_DEPTH_TEST)
				.alpha(ONE_TENTH_ALPHA)
				.texturing(OUTLINE_TEXTURING)
				.fog(NO_FOG)
				.target(OUTLINE_TARGET)
				.build(false)
		);
	}

	public static RenderLayer getGlint() {
		return GLINT;
	}

	public static RenderLayer getEntityGlint() {
		return ENTITY_GLINT;
	}

	public static RenderLayer getBlockBreaking(Identifier identifier) {
		RenderPhase.Texture texture = new RenderPhase.Texture(identifier, false, false);
		return method_24049(
			"crumbling",
			VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL,
			7,
			256,
			false,
			true,
			RenderLayer.MultiPhaseData.builder()
				.texture(texture)
				.alpha(ONE_TENTH_ALPHA)
				.transparency(CRUMBLING_TRANSPARENCY)
				.writeMaskState(COLOR_MASK)
				.layering(POLYGON_OFFSET_LAYERING)
				.build(false)
		);
	}

	public static RenderLayer getText(Identifier texture) {
		return method_24049(
			"text",
			VertexFormats.POSITION_COLOR_TEXTURE_LIGHT,
			7,
			256,
			false,
			true,
			RenderLayer.MultiPhaseData.builder()
				.texture(new RenderPhase.Texture(texture, false, false))
				.alpha(ONE_TENTH_ALPHA)
				.transparency(TRANSLUCENT_TRANSPARENCY)
				.lightmap(ENABLE_LIGHTMAP)
				.build(false)
		);
	}

	public static RenderLayer getTextSeeThrough(Identifier texture) {
		return method_24049(
			"text_see_through",
			VertexFormats.POSITION_COLOR_TEXTURE_LIGHT,
			7,
			256,
			false,
			true,
			RenderLayer.MultiPhaseData.builder()
				.texture(new RenderPhase.Texture(texture, false, false))
				.alpha(ONE_TENTH_ALPHA)
				.transparency(TRANSLUCENT_TRANSPARENCY)
				.lightmap(ENABLE_LIGHTMAP)
				.depthTest(ALWAYS_DEPTH_TEST)
				.writeMaskState(COLOR_MASK)
				.build(false)
		);
	}

	public static RenderLayer getLightning() {
		return LIGHTNING;
	}

	public static RenderLayer getEndPortal(int layer) {
		RenderPhase.Transparency transparency;
		RenderPhase.Texture texture;
		if (layer <= 1) {
			transparency = TRANSLUCENT_TRANSPARENCY;
			texture = new RenderPhase.Texture(EndPortalBlockEntityRenderer.SKY_TEX, false, false);
		} else {
			transparency = ADDITIVE_TRANSPARENCY;
			texture = new RenderPhase.Texture(EndPortalBlockEntityRenderer.PORTAL_TEX, false, false);
		}

		return method_24049(
			"end_portal",
			VertexFormats.POSITION_COLOR,
			7,
			256,
			false,
			true,
			RenderLayer.MultiPhaseData.builder()
				.transparency(transparency)
				.texture(texture)
				.texturing(new RenderPhase.PortalTexturing(layer))
				.fog(BLACK_FOG)
				.build(false)
		);
	}

	public static RenderLayer getLines() {
		return LINES;
	}

	public RenderLayer(
		String name, VertexFormat vertexFormat, int drawMode, int expectedBufferSize, boolean bl, boolean bl2, Runnable startAction, Runnable endAction
	) {
		super(name, startAction, endAction);
		this.vertexFormat = vertexFormat;
		this.drawMode = drawMode;
		this.expectedBufferSize = expectedBufferSize;
		this.field_20975 = bl;
		this.field_21402 = bl2;
	}

	public static RenderLayer.MultiPhase method_24048(String string, VertexFormat vertexFormat, int i, int j, RenderLayer.MultiPhaseData multiPhaseData) {
		return method_24049(string, vertexFormat, i, j, false, false, multiPhaseData);
	}

	public static RenderLayer.MultiPhase method_24049(
		String string, VertexFormat vertexFormat, int i, int j, boolean bl, boolean bl2, RenderLayer.MultiPhaseData multiPhaseData
	) {
		return RenderLayer.MultiPhase.method_24055(string, vertexFormat, i, j, bl, bl2, multiPhaseData);
	}

	public void draw(BufferBuilder bufferBuilder, int i, int j, int k) {
		if (bufferBuilder.isBuilding()) {
			if (this.field_21402) {
				bufferBuilder.sortQuads((float)i, (float)j, (float)k);
			}

			bufferBuilder.end();
			this.startDrawing();
			BufferRenderer.draw(bufferBuilder);
			this.endDrawing();
		}
	}

	public String toString() {
		return this.name;
	}

	public static List<RenderLayer> getBlockLayers() {
		return ImmutableList.of(getSolid(), getCutoutMipped(), getCutout(), getTranslucent());
	}

	public int getExpectedBufferSize() {
		return this.expectedBufferSize;
	}

	public VertexFormat getVertexFormat() {
		return this.vertexFormat;
	}

	public int getDrawMode() {
		return this.drawMode;
	}

	public Optional<RenderLayer> getTexture() {
		return Optional.empty();
	}

	public boolean method_23037() {
		return this.field_20975;
	}

	@Environment(EnvType.CLIENT)
	static final class MultiPhase extends RenderLayer {
		private static final ObjectOpenCustomHashSet<RenderLayer.MultiPhase> field_21696 = new ObjectOpenCustomHashSet<>(RenderLayer.MultiPhase.class_4721.INSTANCE);
		private final RenderLayer.MultiPhaseData data;
		private final int hash;
		private final Optional<RenderLayer> field_21697;

		private MultiPhase(String string, VertexFormat vertexFormat, int i, int j, boolean bl, boolean bl2, RenderLayer.MultiPhaseData multiPhaseData) {
			super(
				string,
				vertexFormat,
				i,
				j,
				bl,
				bl2,
				() -> multiPhaseData.components.forEach(RenderPhase::startDrawing),
				() -> multiPhaseData.components.forEach(RenderPhase::endDrawing)
			);
			this.data = multiPhaseData;
			this.field_21697 = multiPhaseData.textured ? multiPhaseData.texture.getId().map(identifier -> RenderLayer.getOutline(identifier)) : Optional.empty();
			this.hash = Objects.hash(new Object[]{super.hashCode(), multiPhaseData});
		}

		private static RenderLayer.MultiPhase method_24055(
			String string, VertexFormat vertexFormat, int i, int j, boolean bl, boolean bl2, RenderLayer.MultiPhaseData multiPhaseData
		) {
			return field_21696.addOrGet(new RenderLayer.MultiPhase(string, vertexFormat, i, j, bl, bl2, multiPhaseData));
		}

		@Override
		public Optional<RenderLayer> getTexture() {
			return this.field_21697;
		}

		@Override
		public boolean equals(@Nullable Object object) {
			return this == object;
		}

		@Override
		public int hashCode() {
			return this.hash;
		}

		@Environment(EnvType.CLIENT)
		static enum class_4721 implements Strategy<RenderLayer.MultiPhase> {
			INSTANCE;

			public int hashCode(@Nullable RenderLayer.MultiPhase multiPhase) {
				return multiPhase == null ? 0 : multiPhase.hash;
			}

			public boolean equals(@Nullable RenderLayer.MultiPhase multiPhase, @Nullable RenderLayer.MultiPhase multiPhase2) {
				if (multiPhase == multiPhase2) {
					return true;
				} else {
					return multiPhase != null && multiPhase2 != null ? Objects.equals(multiPhase.data, multiPhase2.data) : false;
				}
			}
		}
	}

	@Environment(EnvType.CLIENT)
	public static final class MultiPhaseData {
		private final RenderPhase.Texture texture;
		private final RenderPhase.Transparency transparency;
		private final RenderPhase.DiffuseLighting diffuseLighting;
		private final RenderPhase.ShadeModel shadeModel;
		private final RenderPhase.Alpha alpha;
		private final RenderPhase.DepthTest depthTest;
		private final RenderPhase.Cull cull;
		private final RenderPhase.Lightmap lightmap;
		private final RenderPhase.Overlay overlay;
		private final RenderPhase.Fog fog;
		private final RenderPhase.Layering layering;
		private final RenderPhase.Target target;
		private final RenderPhase.Texturing texturing;
		private final RenderPhase.WriteMaskState writeMaskState;
		private final RenderPhase.LineWidth lineWidth;
		private final boolean textured;
		private final ImmutableList<RenderPhase> components;

		private MultiPhaseData(
			RenderPhase.Texture texture,
			RenderPhase.Transparency transparency,
			RenderPhase.DiffuseLighting diffuseLighting,
			RenderPhase.ShadeModel shadeModel,
			RenderPhase.Alpha alpha,
			RenderPhase.DepthTest depthTest,
			RenderPhase.Cull cull,
			RenderPhase.Lightmap lightmap,
			RenderPhase.Overlay overlay,
			RenderPhase.Fog fog,
			RenderPhase.Layering layering,
			RenderPhase.Target target,
			RenderPhase.Texturing texturing,
			RenderPhase.WriteMaskState writeMaskState,
			RenderPhase.LineWidth lineWidth,
			boolean textured
		) {
			this.texture = texture;
			this.transparency = transparency;
			this.diffuseLighting = diffuseLighting;
			this.shadeModel = shadeModel;
			this.alpha = alpha;
			this.depthTest = depthTest;
			this.cull = cull;
			this.lightmap = lightmap;
			this.overlay = overlay;
			this.fog = fog;
			this.layering = layering;
			this.target = target;
			this.texturing = texturing;
			this.writeMaskState = writeMaskState;
			this.lineWidth = lineWidth;
			this.textured = textured;
			this.components = ImmutableList.of(
				this.texture,
				this.transparency,
				this.diffuseLighting,
				this.shadeModel,
				this.alpha,
				this.depthTest,
				this.cull,
				this.lightmap,
				this.overlay,
				this.fog,
				this.layering,
				this.target,
				this.texturing,
				this.writeMaskState,
				this.lineWidth
			);
		}

		public boolean equals(Object object) {
			if (this == object) {
				return true;
			} else if (object != null && this.getClass() == object.getClass()) {
				RenderLayer.MultiPhaseData multiPhaseData = (RenderLayer.MultiPhaseData)object;
				return this.textured == multiPhaseData.textured && this.components.equals(multiPhaseData.components);
			} else {
				return false;
			}
		}

		public int hashCode() {
			return Objects.hash(new Object[]{this.components, this.textured});
		}

		public static RenderLayer.MultiPhaseData.MultiPhaseDataBuilder builder() {
			return new RenderLayer.MultiPhaseData.MultiPhaseDataBuilder();
		}

		@Environment(EnvType.CLIENT)
		public static class MultiPhaseDataBuilder {
			private RenderPhase.Texture texture = RenderPhase.NO_TEXTURE;
			private RenderPhase.Transparency transparency = RenderPhase.NO_TRANSPARENCY;
			private RenderPhase.DiffuseLighting diffuseLighting = RenderPhase.DISABLE_DIFFUSE_LIGHTING;
			private RenderPhase.ShadeModel shadeModel = RenderPhase.SHADE_MODEL;
			private RenderPhase.Alpha alpha = RenderPhase.ZERO_ALPHA;
			private RenderPhase.DepthTest depthTest = RenderPhase.LEQUAL_DEPTH_TEST;
			private RenderPhase.Cull cull = RenderPhase.ENABLE_CULLING;
			private RenderPhase.Lightmap lightmap = RenderPhase.DISABLE_LIGHTMAP;
			private RenderPhase.Overlay overlay = RenderPhase.DISABLE_OVERLAY_COLOR;
			private RenderPhase.Fog fog = RenderPhase.FOG;
			private RenderPhase.Layering layering = RenderPhase.NO_LAYERING;
			private RenderPhase.Target target = RenderPhase.MAIN_TARGET;
			private RenderPhase.Texturing texturing = RenderPhase.DEFAULT_TEXTURING;
			private RenderPhase.WriteMaskState writeMaskState = RenderPhase.ALL_MASK;
			private RenderPhase.LineWidth lineWidth = RenderPhase.FULL_LINEWIDTH;

			private MultiPhaseDataBuilder() {
			}

			public RenderLayer.MultiPhaseData.MultiPhaseDataBuilder texture(RenderPhase.Texture texture) {
				this.texture = texture;
				return this;
			}

			public RenderLayer.MultiPhaseData.MultiPhaseDataBuilder transparency(RenderPhase.Transparency transparency) {
				this.transparency = transparency;
				return this;
			}

			public RenderLayer.MultiPhaseData.MultiPhaseDataBuilder diffuseLighting(RenderPhase.DiffuseLighting diffuseLighting) {
				this.diffuseLighting = diffuseLighting;
				return this;
			}

			public RenderLayer.MultiPhaseData.MultiPhaseDataBuilder shadeModel(RenderPhase.ShadeModel shadeModel) {
				this.shadeModel = shadeModel;
				return this;
			}

			public RenderLayer.MultiPhaseData.MultiPhaseDataBuilder alpha(RenderPhase.Alpha alpha) {
				this.alpha = alpha;
				return this;
			}

			public RenderLayer.MultiPhaseData.MultiPhaseDataBuilder depthTest(RenderPhase.DepthTest depthTest) {
				this.depthTest = depthTest;
				return this;
			}

			public RenderLayer.MultiPhaseData.MultiPhaseDataBuilder cull(RenderPhase.Cull cull) {
				this.cull = cull;
				return this;
			}

			public RenderLayer.MultiPhaseData.MultiPhaseDataBuilder lightmap(RenderPhase.Lightmap lightmap) {
				this.lightmap = lightmap;
				return this;
			}

			public RenderLayer.MultiPhaseData.MultiPhaseDataBuilder overlay(RenderPhase.Overlay overlay) {
				this.overlay = overlay;
				return this;
			}

			public RenderLayer.MultiPhaseData.MultiPhaseDataBuilder fog(RenderPhase.Fog fog) {
				this.fog = fog;
				return this;
			}

			public RenderLayer.MultiPhaseData.MultiPhaseDataBuilder layering(RenderPhase.Layering layering) {
				this.layering = layering;
				return this;
			}

			public RenderLayer.MultiPhaseData.MultiPhaseDataBuilder target(RenderPhase.Target target) {
				this.target = target;
				return this;
			}

			public RenderLayer.MultiPhaseData.MultiPhaseDataBuilder texturing(RenderPhase.Texturing texturing) {
				this.texturing = texturing;
				return this;
			}

			public RenderLayer.MultiPhaseData.MultiPhaseDataBuilder writeMaskState(RenderPhase.WriteMaskState writeMaskState) {
				this.writeMaskState = writeMaskState;
				return this;
			}

			public RenderLayer.MultiPhaseData.MultiPhaseDataBuilder lineWidth(RenderPhase.LineWidth lineWidth) {
				this.lineWidth = lineWidth;
				return this;
			}

			public RenderLayer.MultiPhaseData build(boolean textured) {
				return new RenderLayer.MultiPhaseData(
					this.texture,
					this.transparency,
					this.diffuseLighting,
					this.shadeModel,
					this.alpha,
					this.depthTest,
					this.cull,
					this.lightmap,
					this.overlay,
					this.fog,
					this.layering,
					this.target,
					this.texturing,
					this.writeMaskState,
					this.lineWidth,
					textured
				);
			}
		}
	}
}
