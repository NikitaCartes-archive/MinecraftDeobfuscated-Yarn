package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.CatEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.client.render.entity.state.CatEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class CatCollarFeatureRenderer extends FeatureRenderer<CatEntityRenderState, CatEntityModel> {
	private static final Identifier SKIN = Identifier.ofVanilla("textures/entity/cat/cat_collar.png");
	private final CatEntityModel model;
	private final CatEntityModel babyModel;

	public CatCollarFeatureRenderer(FeatureRendererContext<CatEntityRenderState, CatEntityModel> context, EntityModelLoader loader) {
		super(context);
		this.model = new CatEntityModel(loader.getModelPart(EntityModelLayers.CAT_COLLAR));
		this.babyModel = new CatEntityModel(loader.getModelPart(EntityModelLayers.CAT_BABY_COLLAR));
	}

	public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CatEntityRenderState catEntityRenderState, float f, float g) {
		DyeColor dyeColor = catEntityRenderState.collarColor;
		if (dyeColor != null) {
			int j = dyeColor.getEntityColor();
			CatEntityModel catEntityModel = catEntityRenderState.baby ? this.babyModel : this.model;
			render(catEntityModel, SKIN, matrixStack, vertexConsumerProvider, i, catEntityRenderState, j);
		}
	}
}
