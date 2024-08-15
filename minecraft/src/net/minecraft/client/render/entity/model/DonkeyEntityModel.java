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
import net.minecraft.client.render.entity.state.DonkeyEntityRenderState;

@Environment(EnvType.CLIENT)
public class DonkeyEntityModel extends AbstractHorseEntityModel<DonkeyEntityRenderState> {
	private final ModelPart leftChest = this.body.getChild(EntityModelPartNames.LEFT_CHEST);
	private final ModelPart rightChest = this.body.getChild(EntityModelPartNames.RIGHT_CHEST);

	public DonkeyEntityModel(ModelPart modelPart) {
		super(modelPart);
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = AbstractHorseEntityModel.getModelData(Dilation.NONE);
		addDonkeyParts(modelData.getRoot());
		return TexturedModelData.of(modelData, 64, 64);
	}

	public static TexturedModelData getBabyTexturedModelData() {
		ModelData modelData = AbstractHorseEntityModel.getBabyModelData(Dilation.NONE);
		addDonkeyParts(modelData.getRoot());
		return TexturedModelData.of(AbstractHorseEntityModel.BABY_TRANSFORMER.apply(modelData), 64, 64);
	}

	private static void addDonkeyParts(ModelPartData root) {
		ModelPartData modelPartData = root.getChild(EntityModelPartNames.BODY);
		ModelPartBuilder modelPartBuilder = ModelPartBuilder.create().uv(26, 21).cuboid(-4.0F, 0.0F, -2.0F, 8.0F, 8.0F, 3.0F);
		modelPartData.addChild(EntityModelPartNames.LEFT_CHEST, modelPartBuilder, ModelTransform.of(6.0F, -8.0F, 0.0F, 0.0F, (float) (-Math.PI / 2), 0.0F));
		modelPartData.addChild(EntityModelPartNames.RIGHT_CHEST, modelPartBuilder, ModelTransform.of(-6.0F, -8.0F, 0.0F, 0.0F, (float) (Math.PI / 2), 0.0F));
		ModelPartData modelPartData2 = root.getChild("head_parts").getChild(EntityModelPartNames.HEAD);
		ModelPartBuilder modelPartBuilder2 = ModelPartBuilder.create().uv(0, 12).cuboid(-1.0F, -7.0F, 0.0F, 2.0F, 7.0F, 1.0F);
		modelPartData2.addChild(
			EntityModelPartNames.LEFT_EAR, modelPartBuilder2, ModelTransform.of(1.25F, -10.0F, 4.0F, (float) (Math.PI / 12), 0.0F, (float) (Math.PI / 12))
		);
		modelPartData2.addChild(
			EntityModelPartNames.RIGHT_EAR, modelPartBuilder2, ModelTransform.of(-1.25F, -10.0F, 4.0F, (float) (Math.PI / 12), 0.0F, (float) (-Math.PI / 12))
		);
	}

	public void setAngles(DonkeyEntityRenderState donkeyEntityRenderState) {
		super.setAngles(donkeyEntityRenderState);
		this.leftChest.visible = donkeyEntityRenderState.hasChest;
		this.rightChest.visible = donkeyEntityRenderState.hasChest;
	}
}
