package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class TridentRiptideEntityModel extends EntityModel<PlayerEntityRenderState> {
	private static final int field_54016 = 2;
	private final ModelPart[] parts = new ModelPart[2];

	public TridentRiptideEntityModel(ModelPart modelPart) {
		super(modelPart);

		for (int i = 0; i < 2; i++) {
			this.parts[i] = modelPart.getChild(getPartName(i));
		}
	}

	private static String getPartName(int index) {
		return "box" + index;
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();

		for (int i = 0; i < 2; i++) {
			float f = -3.2F + 9.6F * (float)(i + 1);
			float g = 0.75F * (float)(i + 1);
			modelPartData.addChild(
				getPartName(i), ModelPartBuilder.create().uv(0, 0).cuboid(-8.0F, -16.0F + f, -8.0F, 16.0F, 32.0F, 16.0F), ModelTransform.NONE.withScale(g)
			);
		}

		return TexturedModelData.of(modelData, 64, 64);
	}

	public void setAngles(PlayerEntityRenderState playerEntityRenderState) {
		super.setAngles(playerEntityRenderState);

		for (int i = 0; i < this.parts.length; i++) {
			float f = playerEntityRenderState.age * (float)(-(45 + (i + 1) * 5));
			this.parts[i].yaw = MathHelper.wrapDegrees(f) * (float) (Math.PI / 180.0);
		}
	}
}
