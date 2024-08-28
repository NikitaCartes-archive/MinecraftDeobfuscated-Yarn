package net.minecraft.client.render.entity.model;

import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.state.SheepEntityRenderState;

@Environment(EnvType.CLIENT)
public class SheepEntityModel extends QuadrupedEntityModel<SheepEntityRenderState> {
	public static final ModelTransformer BABY_TRANSFORMER = new BabyModelTransformer(false, 8.0F, 4.0F, 2.0F, 2.0F, 24.0F, Set.of("head"));

	public SheepEntityModel(ModelPart modelPart) {
		super(modelPart);
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = QuadrupedEntityModel.getModelData(12, Dilation.NONE);
		ModelPartData modelPartData = modelData.getRoot();
		modelPartData.addChild(
			EntityModelPartNames.HEAD, ModelPartBuilder.create().uv(0, 0).cuboid(-3.0F, -4.0F, -6.0F, 6.0F, 6.0F, 8.0F), ModelTransform.pivot(0.0F, 6.0F, -8.0F)
		);
		modelPartData.addChild(
			EntityModelPartNames.BODY,
			ModelPartBuilder.create().uv(28, 8).cuboid(-4.0F, -10.0F, -7.0F, 8.0F, 16.0F, 6.0F),
			ModelTransform.of(0.0F, 5.0F, 2.0F, (float) (Math.PI / 2), 0.0F, 0.0F)
		);
		return TexturedModelData.of(modelData, 64, 32);
	}

	public void setAngles(SheepEntityRenderState sheepEntityRenderState) {
		super.setAngles(sheepEntityRenderState);
		this.head.pivotY = this.head.pivotY + sheepEntityRenderState.neckAngle * 9.0F * sheepEntityRenderState.ageScale;
		this.head.pitch = sheepEntityRenderState.headAngle;
	}
}
