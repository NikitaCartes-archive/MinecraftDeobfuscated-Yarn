package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Cuboid;
import net.minecraft.client.model.Model;

@Environment(EnvType.CLIENT)
public class SignBlockEntityModel extends Model {
	private final Cuboid field_3562 = new Cuboid(this, 0, 0);
	private final Cuboid field_3563;

	public SignBlockEntityModel() {
		this.field_3562.addBox(-12.0F, -14.0F, -1.0F, 24, 12, 2, 0.0F);
		this.field_3563 = new Cuboid(this, 0, 14);
		this.field_3563.addBox(-1.0F, -2.0F, -1.0F, 2, 14, 2, 0.0F);
	}

	public void render() {
		this.field_3562.render(0.0625F);
		this.field_3563.render(0.0625F);
	}

	public Cuboid method_2832() {
		return this.field_3563;
	}
}
