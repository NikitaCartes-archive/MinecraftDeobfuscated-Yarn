/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.client.render.entity.model.LargeTropicalFishEntityModel;
import net.minecraft.client.render.entity.model.SmallTropicalFishEntityModel;
import net.minecraft.client.render.entity.model.TintableCompositeModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.TropicalFishEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class TropicalFishColorFeatureRenderer
extends FeatureRenderer<TropicalFishEntity, TintableCompositeModel<TropicalFishEntity>> {
    private static final Identifier KOB_TEXTURE = new Identifier("textures/entity/fish/tropical_a_pattern_1.png");
    private static final Identifier SUNSTREAK_TEXTURE = new Identifier("textures/entity/fish/tropical_a_pattern_2.png");
    private static final Identifier SNOOPER_TEXTURE = new Identifier("textures/entity/fish/tropical_a_pattern_3.png");
    private static final Identifier DASHER_TEXTURE = new Identifier("textures/entity/fish/tropical_a_pattern_4.png");
    private static final Identifier BRINELY_TEXTURE = new Identifier("textures/entity/fish/tropical_a_pattern_5.png");
    private static final Identifier SPOTTY_TEXTURE = new Identifier("textures/entity/fish/tropical_a_pattern_6.png");
    private static final Identifier FLOPPER_TEXTURE = new Identifier("textures/entity/fish/tropical_b_pattern_1.png");
    private static final Identifier STRIPEY_TEXTURE = new Identifier("textures/entity/fish/tropical_b_pattern_2.png");
    private static final Identifier GLITTER_TEXTURE = new Identifier("textures/entity/fish/tropical_b_pattern_3.png");
    private static final Identifier BLOCKFISH_TEXTURE = new Identifier("textures/entity/fish/tropical_b_pattern_4.png");
    private static final Identifier BETTY_TEXTURE = new Identifier("textures/entity/fish/tropical_b_pattern_5.png");
    private static final Identifier CLAYFISH_TEXTURE = new Identifier("textures/entity/fish/tropical_b_pattern_6.png");
    private final SmallTropicalFishEntityModel<TropicalFishEntity> smallModel;
    private final LargeTropicalFishEntityModel<TropicalFishEntity> largeModel;

    public TropicalFishColorFeatureRenderer(FeatureRendererContext<TropicalFishEntity, TintableCompositeModel<TropicalFishEntity>> context, EntityModelLoader loader) {
        super(context);
        this.smallModel = new SmallTropicalFishEntityModel(loader.getModelPart(EntityModelLayers.TROPICAL_FISH_SMALL_PATTERN));
        this.largeModel = new LargeTropicalFishEntityModel(loader.getModelPart(EntityModelLayers.TROPICAL_FISH_LARGE_PATTERN));
    }

    @Override
    public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, TropicalFishEntity tropicalFishEntity, float f, float g, float h, float j, float k, float l) {
        TropicalFishEntity.Variety variety = tropicalFishEntity.getVariant();
        TintableCompositeModel entityModel = switch (variety.getSize()) {
            default -> throw new IncompatibleClassChangeError();
            case TropicalFishEntity.Size.SMALL -> this.smallModel;
            case TropicalFishEntity.Size.LARGE -> this.largeModel;
        };
        Identifier identifier = switch (variety) {
            default -> throw new IncompatibleClassChangeError();
            case TropicalFishEntity.Variety.KOB -> KOB_TEXTURE;
            case TropicalFishEntity.Variety.SUNSTREAK -> SUNSTREAK_TEXTURE;
            case TropicalFishEntity.Variety.SNOOPER -> SNOOPER_TEXTURE;
            case TropicalFishEntity.Variety.DASHER -> DASHER_TEXTURE;
            case TropicalFishEntity.Variety.BRINELY -> BRINELY_TEXTURE;
            case TropicalFishEntity.Variety.SPOTTY -> SPOTTY_TEXTURE;
            case TropicalFishEntity.Variety.FLOPPER -> FLOPPER_TEXTURE;
            case TropicalFishEntity.Variety.STRIPEY -> STRIPEY_TEXTURE;
            case TropicalFishEntity.Variety.GLITTER -> GLITTER_TEXTURE;
            case TropicalFishEntity.Variety.BLOCKFISH -> BLOCKFISH_TEXTURE;
            case TropicalFishEntity.Variety.BETTY -> BETTY_TEXTURE;
            case TropicalFishEntity.Variety.CLAYFISH -> CLAYFISH_TEXTURE;
        };
        float[] fs = tropicalFishEntity.getPatternColorComponents().getColorComponents();
        TropicalFishColorFeatureRenderer.render(this.getContextModel(), entityModel, identifier, matrixStack, vertexConsumerProvider, i, tropicalFishEntity, f, g, j, k, l, h, fs[0], fs[1], fs[2]);
    }
}

