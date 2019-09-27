package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4587;
import net.minecraft.class_4588;
import net.minecraft.client.model.ModelPart;

@Environment(EnvType.CLIENT)
public class SkullOverlayEntityModel extends SkullEntityModel {
	private final ModelPart field_3377 = new ModelPart(this, 32, 0);

	public SkullOverlayEntityModel() {
		super(0, 0, 64, 64);
		this.field_3377.addCuboid(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, 0.25F);
		this.field_3377.setRotationPoint(0.0F, 0.0F, 0.0F);
	}

	@Override
	public void render(class_4587 arg, class_4588 arg2, float f, float g, float h, float i, int j) {
		super.render(arg, arg2, f, g, h, i, j);
		this.field_3377.yaw = this.skull.yaw;
		this.field_3377.pitch = this.skull.pitch;
		this.field_3377.method_22698(arg, arg2, i, j, null);
	}
}
