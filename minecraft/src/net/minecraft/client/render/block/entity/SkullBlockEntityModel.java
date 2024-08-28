package net.minecraft.client.render.block.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;

@Environment(EnvType.CLIENT)
public abstract class SkullBlockEntityModel extends Model {
	public SkullBlockEntityModel(ModelPart root) {
		super(root, RenderLayer::getEntityTranslucent);
	}

	public abstract void setHeadRotation(float animationProgress, float yaw, float pitch);
}
