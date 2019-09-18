package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelPart;

@Environment(EnvType.CLIENT)
public class SignBlockEntityModel extends Model {
	private final ModelPart field_3562 = new ModelPart(this, 0, 0);
	private final ModelPart signpost;

	public SignBlockEntityModel() {
		this.field_3562.addCuboid(-12.0F, -14.0F, -1.0F, 24.0F, 12.0F, 2.0F, 0.0F);
		this.signpost = new ModelPart(this, 0, 14);
		this.signpost.addCuboid(-1.0F, -2.0F, -1.0F, 2.0F, 14.0F, 2.0F, 0.0F);
	}

	public void render() {
		this.field_3562.render(0.0625F);
		this.signpost.render(0.0625F);
	}

	public ModelPart getSignpostModel() {
		return this.signpost;
	}
}
