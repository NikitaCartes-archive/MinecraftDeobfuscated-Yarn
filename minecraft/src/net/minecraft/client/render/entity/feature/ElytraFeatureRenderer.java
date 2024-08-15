package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.ElytraEntityModel;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.client.render.entity.state.BipedEntityRenderState;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.SkinTextures;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class ElytraFeatureRenderer<S extends BipedEntityRenderState, M extends EntityModel<S>> extends FeatureRenderer<S, M> {
	private static final Identifier SKIN = Identifier.ofVanilla("textures/entity/elytra.png");
	private final ElytraEntityModel model;
	private final ElytraEntityModel babyModel;

	public ElytraFeatureRenderer(FeatureRendererContext<S, M> context, EntityModelLoader loader) {
		super(context);
		this.model = new ElytraEntityModel(loader.getModelPart(EntityModelLayers.ELYTRA));
		this.babyModel = new ElytraEntityModel(loader.getModelPart(EntityModelLayers.ELYTRA_BABY));
	}

	public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, S bipedEntityRenderState, float f, float g) {
		if (bipedEntityRenderState.equippedChestStack.isOf(Items.ELYTRA)) {
			Identifier identifier;
			if (bipedEntityRenderState instanceof PlayerEntityRenderState playerEntityRenderState) {
				SkinTextures skinTextures = playerEntityRenderState.skinTextures;
				if (skinTextures.elytraTexture() != null) {
					identifier = skinTextures.elytraTexture();
				} else if (skinTextures.capeTexture() != null && playerEntityRenderState.capeVisible) {
					identifier = skinTextures.capeTexture();
				} else {
					identifier = SKIN;
				}
			} else {
				identifier = SKIN;
			}

			ElytraEntityModel elytraEntityModel = bipedEntityRenderState.baby ? this.babyModel : this.model;
			matrixStack.push();
			matrixStack.translate(0.0F, 0.0F, 0.125F);
			elytraEntityModel.setAngles(bipedEntityRenderState);
			VertexConsumer vertexConsumer = ItemRenderer.getArmorGlintConsumer(
				vertexConsumerProvider, RenderLayer.getArmorCutoutNoCull(identifier), bipedEntityRenderState.equippedChestStack.hasGlint()
			);
			elytraEntityModel.render(matrixStack, vertexConsumer, i, OverlayTexture.DEFAULT_UV);
			matrixStack.pop();
		}
	}
}
