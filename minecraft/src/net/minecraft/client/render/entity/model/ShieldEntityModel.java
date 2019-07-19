package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelPart;

@Environment(EnvType.CLIENT)
public class ShieldEntityModel extends Model {
	private final ModelPart field_3550;
	private final ModelPart field_3551;

	public ShieldEntityModel() {
		this.textureWidth = 64;
		this.textureHeight = 64;
		this.field_3550 = new ModelPart(this, 0, 0);
		this.field_3550.addCuboid(-6.0F, -11.0F, -2.0F, 12, 22, 1, 0.0F);
		this.field_3551 = new ModelPart(this, 26, 0);
		this.field_3551.addCuboid(-1.0F, -3.0F, -1.0F, 2, 6, 6, 0.0F);
	}

	public void renderItem() {
		this.field_3550.render(0.0625F);
		this.field_3551.render(0.0625F);
	}
}
