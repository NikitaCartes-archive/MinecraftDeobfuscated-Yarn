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
import org.joml.Quaternionf;

@Environment(EnvType.CLIENT)
public class PlayerCapeModel<T extends PlayerEntityRenderState> extends BipedEntityModel<T> {
	private static final String CAPE = "cape";
	private final ModelPart cape = this.body.getChild("cape");

	public PlayerCapeModel(ModelPart modelPart) {
		super(modelPart);
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = BipedEntityModel.getModelData(Dilation.NONE, 0.0F);
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData modelPartData2 = modelPartData.addChild("head");
		modelPartData2.addChild("hat");
		ModelPartData modelPartData3 = modelPartData.addChild("body");
		modelPartData.addChild("left_arm");
		modelPartData.addChild("right_arm");
		modelPartData.addChild("left_leg");
		modelPartData.addChild("right_leg");
		modelPartData3.addChild(
			"cape",
			ModelPartBuilder.create().uv(0, 0).cuboid(-5.0F, 0.0F, 0.0F, 10.0F, 16.0F, 1.0F, Dilation.NONE, 1.0F, 0.5F),
			ModelTransform.of(0.0F, 0.0F, 3.0F, 0.0F, (float) Math.PI, 0.0F)
		);
		return TexturedModelData.of(modelData, 64, 64);
	}

	public void setAngles(T playerEntityRenderState) {
		super.setAngles(playerEntityRenderState);
		this.cape.resetTransform();
		if (!playerEntityRenderState.equippedChestStack.isEmpty()) {
			this.cape.pivotZ++;
			this.cape.pivotY -= 0.85F;
		}

		this.cape
			.method_62132(
				new Quaternionf()
					.rotationX((6.0F + playerEntityRenderState.field_53537 / 2.0F + playerEntityRenderState.field_53536) * (float) (Math.PI / 180.0))
					.rotateZ(playerEntityRenderState.field_53538 / 2.0F * (float) (Math.PI / 180.0))
					.rotateY(-playerEntityRenderState.field_53538 / 2.0F * (float) (Math.PI / 180.0))
			);
	}
}
