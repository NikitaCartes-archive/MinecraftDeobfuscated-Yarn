package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.entity.Entity;

@Environment(EnvType.CLIENT)
public class LeashKnotEntityModel<T extends Entity> extends SinglePartEntityModel<T> {
	private final ModelPart field_27442;
	private final ModelPart leashKnot;

	public LeashKnotEntityModel(ModelPart modelPart) {
		this.field_27442 = modelPart;
		this.leashKnot = modelPart.getChild("knot");
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		modelPartData.addChild("knot", ModelPartBuilder.create().uv(0, 0).cuboid(-3.0F, -8.0F, -3.0F, 6.0F, 8.0F, 6.0F), ModelTransform.NONE);
		return TexturedModelData.of(modelData, 32, 32);
	}

	@Override
	public ModelPart getPart() {
		return this.field_27442;
	}

	@Override
	public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
		this.leashKnot.yaw = headYaw * (float) (Math.PI / 180.0);
		this.leashKnot.pitch = headPitch * (float) (Math.PI / 180.0);
	}
}
