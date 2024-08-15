package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;

@Environment(EnvType.CLIENT)
public class Deadmau5EarsEntityModel extends BipedEntityModel<PlayerEntityRenderState> {
	public Deadmau5EarsEntityModel(ModelPart modelPart) {
		super(modelPart);
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = BipedEntityModel.getModelData(Dilation.NONE, 0.0F);
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData modelPartData2 = modelPartData.addChild("head");
		modelPartData2.addChild("hat");
		modelPartData.addChild("body");
		modelPartData.addChild("left_arm");
		modelPartData.addChild("right_arm");
		modelPartData.addChild("left_leg");
		modelPartData.addChild("right_leg");
		ModelPartBuilder modelPartBuilder = ModelPartBuilder.create().uv(24, 0).cuboid(-3.0F, -6.0F, -1.0F, 6.0F, 6.0F, 1.0F, new Dilation(1.0F));
		modelPartData2.addChild(EntityModelPartNames.LEFT_EAR, modelPartBuilder, ModelTransform.pivot(-6.0F, -6.0F, 0.0F));
		modelPartData2.addChild(EntityModelPartNames.RIGHT_EAR, modelPartBuilder, ModelTransform.pivot(6.0F, -6.0F, 0.0F));
		return TexturedModelData.of(modelData, 64, 64);
	}
}
