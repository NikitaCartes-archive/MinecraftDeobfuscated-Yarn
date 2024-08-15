package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.feature.SnowGolemPumpkinFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.SnowGolemEntityModel;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.entity.passive.SnowGolemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class SnowGolemEntityRenderer extends MobEntityRenderer<SnowGolemEntity, LivingEntityRenderState, SnowGolemEntityModel> {
	private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/snow_golem.png");

	public SnowGolemEntityRenderer(EntityRendererFactory.Context context) {
		super(context, new SnowGolemEntityModel(context.getPart(EntityModelLayers.SNOW_GOLEM)), 0.5F);
		this.addFeature(new SnowGolemPumpkinFeatureRenderer(this, context.getBlockRenderManager(), context.getItemRenderer()));
	}

	public Identifier getTexture(LivingEntityRenderState livingEntityRenderState) {
		return TEXTURE;
	}

	public LivingEntityRenderState getRenderState() {
		return new LivingEntityRenderState();
	}

	public void updateRenderState(SnowGolemEntity snowGolemEntity, LivingEntityRenderState livingEntityRenderState, float f) {
		super.updateRenderState(snowGolemEntity, livingEntityRenderState, f);
		livingEntityRenderState.headEquippedStack = snowGolemEntity.hasPumpkin() ? new ItemStack(Items.CARVED_PUMPKIN) : ItemStack.EMPTY;
		livingEntityRenderState.headEquippedItemModel = this.itemRenderer
			.getModel(livingEntityRenderState.headEquippedStack, snowGolemEntity, ModelTransformationMode.HEAD);
	}
}
