package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.RenderLayer;

@Environment(EnvType.CLIENT)
public class StingerModel extends Model {
	public StingerModel(ModelPart root) {
		super(root, RenderLayer::getEntityCutout);
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartBuilder modelPartBuilder = ModelPartBuilder.create().uv(0, 0).cuboid(-1.0F, -0.5F, 0.0F, 2.0F, 1.0F, 0.0F);
		modelPartData.addChild("cross_1", modelPartBuilder, ModelTransform.rotation((float) (Math.PI / 4), 0.0F, 0.0F));
		modelPartData.addChild("cross_2", modelPartBuilder, ModelTransform.rotation((float) (Math.PI * 3.0 / 4.0), 0.0F, 0.0F));
		return TexturedModelData.of(modelData, 16, 16);
	}
}
