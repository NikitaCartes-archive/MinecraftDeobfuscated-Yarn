package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.feature.SnowGolemPumpkinFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.SnowGolemEntityModel;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.entity.passive.SnowGolemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.ModelTransformationMode;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class SnowGolemEntityRenderer extends MobEntityRenderer<SnowGolemEntity, LivingEntityRenderState, SnowGolemEntityModel> {
	private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/snow_golem.png");

	public SnowGolemEntityRenderer(EntityRendererFactory.Context context) {
		super(context, new SnowGolemEntityModel(context.getPart(EntityModelLayers.SNOW_GOLEM)), 0.5F);
		this.addFeature(new SnowGolemPumpkinFeatureRenderer(this, context.getBlockRenderManager(), context.getItemRenderer()));
	}

	@Override
	public Identifier getTexture(LivingEntityRenderState state) {
		return TEXTURE;
	}

	public LivingEntityRenderState createRenderState() {
		return new LivingEntityRenderState();
	}

	public void updateRenderState(SnowGolemEntity snowGolemEntity, LivingEntityRenderState livingEntityRenderState, float f) {
		super.updateRenderState(snowGolemEntity, livingEntityRenderState, f);
		livingEntityRenderState.equippedHeadStack = snowGolemEntity.hasPumpkin() ? new ItemStack(Items.CARVED_PUMPKIN) : ItemStack.EMPTY;
		livingEntityRenderState.equippedHeadItemModel = this.itemRenderer
			.getModel(livingEntityRenderState.equippedHeadStack, snowGolemEntity, ModelTransformationMode.HEAD);
	}
}
