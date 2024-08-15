package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.client.render.entity.model.SheepEntityModel;
import net.minecraft.client.render.entity.model.SheepWoolEntityModel;
import net.minecraft.client.render.entity.state.SheepEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class SheepWoolFeatureRenderer extends FeatureRenderer<SheepEntityRenderState, SheepEntityModel> {
	private static final Identifier SKIN = Identifier.ofVanilla("textures/entity/sheep/sheep_fur.png");
	private final EntityModel<SheepEntityRenderState> woolModel;
	private final EntityModel<SheepEntityRenderState> babyWoolModel;

	public SheepWoolFeatureRenderer(FeatureRendererContext<SheepEntityRenderState, SheepEntityModel> context, EntityModelLoader loader) {
		super(context);
		this.woolModel = new SheepWoolEntityModel(loader.getModelPart(EntityModelLayers.SHEEP_WOOL));
		this.babyWoolModel = new SheepWoolEntityModel(loader.getModelPart(EntityModelLayers.SHEEP_BABY_WOOL));
	}

	public void render(
		MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, SheepEntityRenderState sheepEntityRenderState, float f, float g
	) {
		if (!sheepEntityRenderState.sheared) {
			EntityModel<SheepEntityRenderState> entityModel = sheepEntityRenderState.baby ? this.babyWoolModel : this.woolModel;
			if (sheepEntityRenderState.invisible) {
				if (sheepEntityRenderState.hasOutline) {
					entityModel.setAngles(sheepEntityRenderState);
					VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getOutline(SKIN));
					entityModel.render(matrixStack, vertexConsumer, i, LivingEntityRenderer.getOverlay(sheepEntityRenderState, 0.0F), -16777216);
				}
			} else {
				int r;
				if (sheepEntityRenderState.customName != null && "jeb_".equals(sheepEntityRenderState.customName.getString())) {
					int j = 25;
					int k = MathHelper.floor(sheepEntityRenderState.age);
					int l = k / 25 + sheepEntityRenderState.id;
					int m = DyeColor.values().length;
					int n = l % m;
					int o = (l + 1) % m;
					float h = ((float)(k % 25) + MathHelper.fractionalPart(sheepEntityRenderState.age)) / 25.0F;
					int p = SheepEntity.getRgbColor(DyeColor.byId(n));
					int q = SheepEntity.getRgbColor(DyeColor.byId(o));
					r = ColorHelper.lerp(h, p, q);
				} else {
					r = SheepEntity.getRgbColor(sheepEntityRenderState.color);
				}

				render(entityModel, SKIN, matrixStack, vertexConsumerProvider, i, sheepEntityRenderState, r);
			}
		}
	}
}
