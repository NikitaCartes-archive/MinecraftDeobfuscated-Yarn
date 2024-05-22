package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.block.entity.SkullBlockEntityModel;
import net.minecraft.client.util.math.MatrixStack;

@Environment(EnvType.CLIENT)
public class PiglinHeadEntityModel extends SkullBlockEntityModel {
	private final ModelPart head;
	private final ModelPart leftEar;
	private final ModelPart rightEar;

	public PiglinHeadEntityModel(ModelPart root) {
		this.head = root.getChild(EntityModelPartNames.HEAD);
		this.leftEar = this.head.getChild(EntityModelPartNames.LEFT_EAR);
		this.rightEar = this.head.getChild(EntityModelPartNames.RIGHT_EAR);
	}

	public static ModelData getModelData() {
		ModelData modelData = new ModelData();
		PiglinEntityModel.addHead(Dilation.NONE, modelData);
		return modelData;
	}

	@Override
	public void setHeadRotation(float animationProgress, float yaw, float pitch) {
		this.head.yaw = yaw * (float) (Math.PI / 180.0);
		this.head.pitch = pitch * (float) (Math.PI / 180.0);
		float f = 1.2F;
		this.leftEar.roll = (float)(-(Math.cos((double)(animationProgress * (float) Math.PI * 0.2F * 1.2F)) + 2.5)) * 0.2F;
		this.rightEar.roll = (float)(Math.cos((double)(animationProgress * (float) Math.PI * 0.2F)) + 2.5) * 0.2F;
	}

	@Override
	public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, int color) {
		this.head.render(matrices, vertices, light, overlay, color);
	}
}
