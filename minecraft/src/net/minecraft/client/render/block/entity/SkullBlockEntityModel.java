package net.minecraft.client.render.block.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.RenderLayer;

@Environment(EnvType.CLIENT)
public abstract class SkullBlockEntityModel extends Model {
	public SkullBlockEntityModel() {
		super(RenderLayer::getEntityTranslucent);
	}

	public abstract void method_2821(float f, float g, float h);
}
