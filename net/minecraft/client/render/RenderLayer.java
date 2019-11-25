/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render;

import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.objects.ObjectOpenCustomHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalDouble;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.block.entity.EndPortalBlockEntityRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public abstract class RenderLayer
extends RenderPhase {
    private static final RenderLayer SOLID = RenderLayer.of("solid", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL, 7, 0x200000, true, false, PhaseData.builder().shadeModel(SMOOTH_SHADE_MODEL).lightmap(ENABLE_LIGHTMAP).texture(MIPMAP_BLOCK_ATLAS_TEXTURE).build(true));
    private static final RenderLayer CUTOUT_MIPPED = RenderLayer.of("cutout_mipped", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL, 7, 131072, true, false, PhaseData.builder().shadeModel(SMOOTH_SHADE_MODEL).lightmap(ENABLE_LIGHTMAP).texture(MIPMAP_BLOCK_ATLAS_TEXTURE).alpha(HALF_ALPHA).build(true));
    private static final RenderLayer CUTOUT = RenderLayer.of("cutout", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL, 7, 131072, true, false, PhaseData.builder().shadeModel(SMOOTH_SHADE_MODEL).lightmap(ENABLE_LIGHTMAP).texture(BLOCK_ATLAS_TEXTURE).alpha(HALF_ALPHA).build(true));
    private static final RenderLayer TRANSLUCENT = RenderLayer.of("translucent", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL, 7, 262144, true, true, RenderLayer.createTranslucentPhaseData());
    private static final RenderLayer TRANSLUCENT_NO_CRUMBLING = RenderLayer.of("translucent_no_crumbling", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL, 7, 262144, false, true, RenderLayer.createTranslucentPhaseData());
    private static final RenderLayer LEASH = RenderLayer.of("leash", VertexFormats.POSITION_COLOR_LIGHT, 7, 256, PhaseData.builder().texture(NO_TEXTURE).cull(DISABLE_CULLING).lightmap(ENABLE_LIGHTMAP).build(false));
    private static final RenderLayer WATER_MASK = RenderLayer.of("water_mask", VertexFormats.POSITION, 7, 256, PhaseData.builder().texture(NO_TEXTURE).writeMaskState(DEPTH_MASK).build(false));
    private static final RenderLayer GLINT = RenderLayer.of("glint", VertexFormats.POSITION_TEXTURE, 7, 256, PhaseData.builder().texture(new RenderPhase.Texture(ItemRenderer.ENCHANTED_ITEM_GLINT, false, false)).writeMaskState(COLOR_MASK).cull(DISABLE_CULLING).depthTest(EQUAL_DEPTH_TEST).transparency(GLINT_TRANSPARENCY).texturing(GLINT_TEXTURING).build(false));
    private static final RenderLayer ENTITY_GLINT = RenderLayer.of("entity_glint", VertexFormats.POSITION_TEXTURE, 7, 256, PhaseData.builder().texture(new RenderPhase.Texture(ItemRenderer.ENCHANTED_ITEM_GLINT, false, false)).writeMaskState(COLOR_MASK).cull(DISABLE_CULLING).depthTest(EQUAL_DEPTH_TEST).transparency(GLINT_TRANSPARENCY).texturing(ENTITY_GLINT_TEXTURING).build(false));
    private static final RenderLayer LIGHTNING = RenderLayer.of("lightning", VertexFormats.POSITION_COLOR, 7, 256, false, true, PhaseData.builder().writeMaskState(COLOR_MASK).transparency(LIGHTNING_TRANSPARENCY).shadeModel(SMOOTH_SHADE_MODEL).build(false));
    public static final MultiPhase LINES = RenderLayer.of("lines", VertexFormats.POSITION_COLOR, 1, 256, PhaseData.builder().lineWidth(new RenderPhase.LineWidth(OptionalDouble.empty())).layering(PROJECTION_LAYERING).transparency(TRANSLUCENT_TRANSPARENCY).build(false));
    private final VertexFormat vertexFormat;
    private final int drawMode;
    private final int expectedBufferSize;
    private final boolean hasCrumbling;
    private final boolean translucent;

    public static RenderLayer getSolid() {
        return SOLID;
    }

    public static RenderLayer getCutoutMipped() {
        return CUTOUT_MIPPED;
    }

    public static RenderLayer getCutout() {
        return CUTOUT;
    }

    private static PhaseData createTranslucentPhaseData() {
        return PhaseData.builder().shadeModel(SMOOTH_SHADE_MODEL).lightmap(ENABLE_LIGHTMAP).texture(MIPMAP_BLOCK_ATLAS_TEXTURE).transparency(TRANSLUCENT_TRANSPARENCY).build(true);
    }

    public static RenderLayer getTranslucent() {
        return TRANSLUCENT;
    }

    public static RenderLayer getTranslucentNoCrumbling() {
        return TRANSLUCENT_NO_CRUMBLING;
    }

    public static RenderLayer getEntitySolid(Identifier identifier) {
        PhaseData phaseData = PhaseData.builder().texture(new RenderPhase.Texture(identifier, false, false)).transparency(NO_TRANSPARENCY).diffuseLighting(ENABLE_DIFFUSE_LIGHTING).lightmap(ENABLE_LIGHTMAP).overlay(ENABLE_OVERLAY_COLOR).build(true);
        return RenderLayer.of("entity_solid", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL, 7, 256, true, false, phaseData);
    }

    public static RenderLayer getEntityCutout(Identifier identifier) {
        PhaseData phaseData = PhaseData.builder().texture(new RenderPhase.Texture(identifier, false, false)).transparency(NO_TRANSPARENCY).diffuseLighting(ENABLE_DIFFUSE_LIGHTING).alpha(ONE_TENTH_ALPHA).lightmap(ENABLE_LIGHTMAP).overlay(ENABLE_OVERLAY_COLOR).build(true);
        return RenderLayer.of("entity_cutout", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL, 7, 256, true, false, phaseData);
    }

    public static RenderLayer getEntityCutoutNoCull(Identifier identifier) {
        PhaseData phaseData = PhaseData.builder().texture(new RenderPhase.Texture(identifier, false, false)).transparency(NO_TRANSPARENCY).diffuseLighting(ENABLE_DIFFUSE_LIGHTING).alpha(ONE_TENTH_ALPHA).cull(DISABLE_CULLING).lightmap(ENABLE_LIGHTMAP).overlay(ENABLE_OVERLAY_COLOR).build(true);
        return RenderLayer.of("entity_cutout_no_cull", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL, 7, 256, true, false, phaseData);
    }

    public static RenderLayer getEntityTranslucentCull(Identifier identifier) {
        PhaseData phaseData = PhaseData.builder().texture(new RenderPhase.Texture(identifier, false, false)).transparency(TRANSLUCENT_TRANSPARENCY).diffuseLighting(ENABLE_DIFFUSE_LIGHTING).alpha(ONE_TENTH_ALPHA).lightmap(ENABLE_LIGHTMAP).overlay(ENABLE_OVERLAY_COLOR).build(true);
        return RenderLayer.of("entity_translucent_cull", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL, 7, 256, true, true, phaseData);
    }

    public static RenderLayer getEntityTranslucent(Identifier identifier) {
        PhaseData phaseData = PhaseData.builder().texture(new RenderPhase.Texture(identifier, false, false)).transparency(TRANSLUCENT_TRANSPARENCY).diffuseLighting(ENABLE_DIFFUSE_LIGHTING).alpha(ONE_TENTH_ALPHA).cull(DISABLE_CULLING).lightmap(ENABLE_LIGHTMAP).overlay(ENABLE_OVERLAY_COLOR).build(true);
        return RenderLayer.of("entity_translucent", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL, 7, 256, true, true, phaseData);
    }

    public static RenderLayer getEntitySmoothCutout(Identifier identifier) {
        PhaseData phaseData = PhaseData.builder().texture(new RenderPhase.Texture(identifier, false, false)).alpha(HALF_ALPHA).diffuseLighting(ENABLE_DIFFUSE_LIGHTING).shadeModel(SMOOTH_SHADE_MODEL).cull(DISABLE_CULLING).lightmap(ENABLE_LIGHTMAP).build(true);
        return RenderLayer.of("entity_smooth_cutout", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL, 7, 256, phaseData);
    }

    public static RenderLayer getBeaconBeam(Identifier identifier, boolean bl) {
        PhaseData phaseData = PhaseData.builder().texture(new RenderPhase.Texture(identifier, false, false)).transparency(bl ? TRANSLUCENT_TRANSPARENCY : NO_TRANSPARENCY).writeMaskState(bl ? COLOR_MASK : ALL_MASK).fog(NO_FOG).build(false);
        return RenderLayer.of("beacon_beam", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL, 7, 256, false, true, phaseData);
    }

    public static RenderLayer getEntityDecal(Identifier identifier) {
        PhaseData phaseData = PhaseData.builder().texture(new RenderPhase.Texture(identifier, false, false)).diffuseLighting(ENABLE_DIFFUSE_LIGHTING).alpha(ONE_TENTH_ALPHA).depthTest(EQUAL_DEPTH_TEST).cull(DISABLE_CULLING).lightmap(ENABLE_LIGHTMAP).overlay(ENABLE_OVERLAY_COLOR).build(false);
        return RenderLayer.of("entity_decal", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL, 7, 256, phaseData);
    }

    public static RenderLayer getEntityNoOutline(Identifier identifier) {
        PhaseData phaseData = PhaseData.builder().texture(new RenderPhase.Texture(identifier, false, false)).transparency(TRANSLUCENT_TRANSPARENCY).diffuseLighting(ENABLE_DIFFUSE_LIGHTING).alpha(ONE_TENTH_ALPHA).cull(DISABLE_CULLING).lightmap(ENABLE_LIGHTMAP).overlay(ENABLE_OVERLAY_COLOR).writeMaskState(COLOR_MASK).build(false);
        return RenderLayer.of("entity_no_outline", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL, 7, 256, false, true, phaseData);
    }

    public static RenderLayer getEntityAlpha(Identifier identifier, float f) {
        PhaseData phaseData = PhaseData.builder().texture(new RenderPhase.Texture(identifier, false, false)).alpha(new RenderPhase.Alpha(f)).cull(DISABLE_CULLING).build(true);
        return RenderLayer.of("entity_alpha", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL, 7, 256, phaseData);
    }

    public static RenderLayer getEyes(Identifier identifier) {
        RenderPhase.Texture texture = new RenderPhase.Texture(identifier, false, false);
        return RenderLayer.of("eyes", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL, 7, 256, false, true, PhaseData.builder().texture(texture).transparency(ADDITIVE_TRANSPARENCY).writeMaskState(COLOR_MASK).fog(BLACK_FOG).build(false));
    }

    public static RenderLayer getEnergySwirl(Identifier identifier, float f, float g) {
        return RenderLayer.of("energy_swirl", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL, 7, 256, false, true, PhaseData.builder().texture(new RenderPhase.Texture(identifier, false, false)).texturing(new RenderPhase.OffsetTexturing(f, g)).fog(BLACK_FOG).transparency(ADDITIVE_TRANSPARENCY).diffuseLighting(ENABLE_DIFFUSE_LIGHTING).alpha(ONE_TENTH_ALPHA).cull(DISABLE_CULLING).lightmap(ENABLE_LIGHTMAP).overlay(ENABLE_OVERLAY_COLOR).build(false));
    }

    public static RenderLayer getLeash() {
        return LEASH;
    }

    public static RenderLayer getWaterMask() {
        return WATER_MASK;
    }

    public static RenderLayer getOutline(Identifier identifier) {
        return RenderLayer.of("outline", VertexFormats.POSITION_COLOR_TEXTURE, 7, 256, PhaseData.builder().texture(new RenderPhase.Texture(identifier, false, false)).cull(DISABLE_CULLING).depthTest(ALWAYS_DEPTH_TEST).alpha(ONE_TENTH_ALPHA).texturing(OUTLINE_TEXTURING).fog(NO_FOG).target(OUTLINE_TARGET).build(false));
    }

    public static RenderLayer getGlint() {
        return GLINT;
    }

    public static RenderLayer getEntityGlint() {
        return ENTITY_GLINT;
    }

    public static RenderLayer getCrumbling(Identifier identifier) {
        RenderPhase.Texture texture = new RenderPhase.Texture(identifier, false, false);
        return RenderLayer.of("crumbling", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL, 7, 256, false, true, PhaseData.builder().texture(texture).alpha(ONE_TENTH_ALPHA).transparency(CRUMBLING_TRANSPARENCY).writeMaskState(COLOR_MASK).layering(POLYGON_OFFSET_LAYERING).build(false));
    }

    public static RenderLayer getText(Identifier identifier) {
        return RenderLayer.of("text", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT, 7, 256, false, true, PhaseData.builder().texture(new RenderPhase.Texture(identifier, false, false)).alpha(ONE_TENTH_ALPHA).transparency(TRANSLUCENT_TRANSPARENCY).lightmap(ENABLE_LIGHTMAP).build(false));
    }

    public static RenderLayer getTextSeeThrough(Identifier identifier) {
        return RenderLayer.of("text_see_through", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT, 7, 256, false, true, PhaseData.builder().texture(new RenderPhase.Texture(identifier, false, false)).alpha(ONE_TENTH_ALPHA).transparency(TRANSLUCENT_TRANSPARENCY).lightmap(ENABLE_LIGHTMAP).depthTest(ALWAYS_DEPTH_TEST).writeMaskState(COLOR_MASK).build(false));
    }

    public static RenderLayer getLightning() {
        return LIGHTNING;
    }

    public static RenderLayer getEndPortal(int i) {
        RenderPhase.Texture texture;
        RenderPhase.Transparency transparency;
        if (i <= 1) {
            transparency = TRANSLUCENT_TRANSPARENCY;
            texture = new RenderPhase.Texture(EndPortalBlockEntityRenderer.SKY_TEX, false, false);
        } else {
            transparency = ADDITIVE_TRANSPARENCY;
            texture = new RenderPhase.Texture(EndPortalBlockEntityRenderer.PORTAL_TEX, false, false);
        }
        return RenderLayer.of("end_portal", VertexFormats.POSITION_COLOR, 7, 256, false, true, PhaseData.builder().transparency(transparency).texture(texture).texturing(new RenderPhase.PortalTexturing(i)).fog(BLACK_FOG).build(false));
    }

    public static RenderLayer getLines() {
        return LINES;
    }

    public RenderLayer(String string, VertexFormat vertexFormat, int i, int j, boolean bl, boolean bl2, Runnable runnable, Runnable runnable2) {
        super(string, runnable, runnable2);
        this.vertexFormat = vertexFormat;
        this.drawMode = i;
        this.expectedBufferSize = j;
        this.hasCrumbling = bl;
        this.translucent = bl2;
    }

    public static MultiPhase of(String string, VertexFormat vertexFormat, int i, int j, PhaseData phaseData) {
        return RenderLayer.of(string, vertexFormat, i, j, false, false, phaseData);
    }

    public static MultiPhase of(String string, VertexFormat vertexFormat, int i, int j, boolean bl, boolean bl2, PhaseData phaseData) {
        return MultiPhase.of(string, vertexFormat, i, j, bl, bl2, phaseData);
    }

    public void draw(BufferBuilder bufferBuilder, int i, int j, int k) {
        if (!bufferBuilder.isBuilding()) {
            return;
        }
        if (this.translucent) {
            bufferBuilder.sortQuads(i, j, k);
        }
        bufferBuilder.end();
        this.startDrawing();
        BufferRenderer.draw(bufferBuilder);
        this.endDrawing();
    }

    public String toString() {
        return this.name;
    }

    public static List<RenderLayer> getBlockLayers() {
        return ImmutableList.of(RenderLayer.getSolid(), RenderLayer.getCutoutMipped(), RenderLayer.getCutout(), RenderLayer.getTranslucent());
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

    public boolean hasCrumbling() {
        return this.hasCrumbling;
    }

    @Environment(value=EnvType.CLIENT)
    static final class MultiPhase
    extends RenderLayer {
        private static final ObjectOpenCustomHashSet<MultiPhase> CACHE = new ObjectOpenCustomHashSet<MultiPhase>(class_4721.INSTANCE);
        private final PhaseData phases;
        private final int hash;
        private final Optional<RenderLayer> texture;

        private MultiPhase(String string, VertexFormat vertexFormat, int i, int j, boolean bl, boolean bl2, PhaseData phaseData) {
            super(string, vertexFormat, i, j, bl, bl2, () -> phaseData.phases.forEach(RenderPhase::startDrawing), () -> phaseData.phases.forEach(RenderPhase::endDrawing));
            this.phases = phaseData;
            this.texture = phaseData.textured ? phaseData.texture.getId().map(RenderLayer::getOutline) : Optional.empty();
            this.hash = Objects.hash(super.hashCode(), phaseData);
        }

        private static MultiPhase of(String string, VertexFormat vertexFormat, int i, int j, boolean bl, boolean bl2, PhaseData phaseData) {
            return CACHE.addOrGet(new MultiPhase(string, vertexFormat, i, j, bl, bl2, phaseData));
        }

        @Override
        public Optional<RenderLayer> getTexture() {
            return this.texture;
        }

        @Override
        public boolean equals(@Nullable Object object) {
            return this == object;
        }

        @Override
        public int hashCode() {
            return this.hash;
        }

        @Environment(value=EnvType.CLIENT)
        static enum class_4721 implements Hash.Strategy<MultiPhase>
        {
            INSTANCE;


            @Override
            public int hashCode(@Nullable MultiPhase multiPhase) {
                if (multiPhase == null) {
                    return 0;
                }
                return multiPhase.hash;
            }

            @Override
            public boolean equals(@Nullable MultiPhase multiPhase, @Nullable MultiPhase multiPhase2) {
                if (multiPhase == multiPhase2) {
                    return true;
                }
                if (multiPhase == null || multiPhase2 == null) {
                    return false;
                }
                return Objects.equals(multiPhase.phases, multiPhase2.phases);
            }

            @Override
            public /* synthetic */ boolean equals(@Nullable Object object, @Nullable Object object2) {
                return this.equals((MultiPhase)object, (MultiPhase)object2);
            }

            @Override
            public /* synthetic */ int hashCode(@Nullable Object object) {
                return this.hashCode((MultiPhase)object);
            }
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static final class PhaseData {
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
        private final ImmutableList<RenderPhase> phases;

        private PhaseData(RenderPhase.Texture texture, RenderPhase.Transparency transparency, RenderPhase.DiffuseLighting diffuseLighting, RenderPhase.ShadeModel shadeModel, RenderPhase.Alpha alpha, RenderPhase.DepthTest depthTest, RenderPhase.Cull cull, RenderPhase.Lightmap lightmap, RenderPhase.Overlay overlay, RenderPhase.Fog fog, RenderPhase.Layering layering, RenderPhase.Target target, RenderPhase.Texturing texturing, RenderPhase.WriteMaskState writeMaskState, RenderPhase.LineWidth lineWidth, boolean bl) {
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
            this.textured = bl;
            this.phases = ImmutableList.of(this.texture, this.transparency, this.diffuseLighting, this.shadeModel, this.alpha, this.depthTest, this.cull, this.lightmap, this.overlay, this.fog, this.layering, this.target, new RenderPhase[]{this.texturing, this.writeMaskState, this.lineWidth});
        }

        public boolean equals(Object object) {
            if (this == object) {
                return true;
            }
            if (object == null || this.getClass() != object.getClass()) {
                return false;
            }
            PhaseData phaseData = (PhaseData)object;
            return this.textured == phaseData.textured && this.phases.equals(phaseData.phases);
        }

        public int hashCode() {
            return Objects.hash(this.phases, this.textured);
        }

        public static Builder builder() {
            return new Builder();
        }

        @Environment(value=EnvType.CLIENT)
        public static class Builder {
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

            private Builder() {
            }

            public Builder texture(RenderPhase.Texture texture) {
                this.texture = texture;
                return this;
            }

            public Builder transparency(RenderPhase.Transparency transparency) {
                this.transparency = transparency;
                return this;
            }

            public Builder diffuseLighting(RenderPhase.DiffuseLighting diffuseLighting) {
                this.diffuseLighting = diffuseLighting;
                return this;
            }

            public Builder shadeModel(RenderPhase.ShadeModel shadeModel) {
                this.shadeModel = shadeModel;
                return this;
            }

            public Builder alpha(RenderPhase.Alpha alpha) {
                this.alpha = alpha;
                return this;
            }

            public Builder depthTest(RenderPhase.DepthTest depthTest) {
                this.depthTest = depthTest;
                return this;
            }

            public Builder cull(RenderPhase.Cull cull) {
                this.cull = cull;
                return this;
            }

            public Builder lightmap(RenderPhase.Lightmap lightmap) {
                this.lightmap = lightmap;
                return this;
            }

            public Builder overlay(RenderPhase.Overlay overlay) {
                this.overlay = overlay;
                return this;
            }

            public Builder fog(RenderPhase.Fog fog) {
                this.fog = fog;
                return this;
            }

            public Builder layering(RenderPhase.Layering layering) {
                this.layering = layering;
                return this;
            }

            public Builder target(RenderPhase.Target target) {
                this.target = target;
                return this;
            }

            public Builder texturing(RenderPhase.Texturing texturing) {
                this.texturing = texturing;
                return this;
            }

            public Builder writeMaskState(RenderPhase.WriteMaskState writeMaskState) {
                this.writeMaskState = writeMaskState;
                return this;
            }

            public Builder lineWidth(RenderPhase.LineWidth lineWidth) {
                this.lineWidth = lineWidth;
                return this;
            }

            public PhaseData build(boolean bl) {
                return new PhaseData(this.texture, this.transparency, this.diffuseLighting, this.shadeModel, this.alpha, this.depthTest, this.cull, this.lightmap, this.overlay, this.fog, this.layering, this.target, this.texturing, this.writeMaskState, this.lineWidth, bl);
            }
        }
    }
}

