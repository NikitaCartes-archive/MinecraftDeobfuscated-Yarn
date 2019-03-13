package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Cuboid;
import net.minecraft.entity.Entity;

@Environment(EnvType.CLIENT)
public class CowEntityModel<T extends Entity> extends QuadrupedEntityModel<T> {
	public CowEntityModel() {
		super(12, 0.0F);
		this.field_3535 = new Cuboid(this, 0, 0);
		this.field_3535.addBox(-4.0F, -4.0F, -6.0F, 8, 8, 6, 0.0F);
		this.field_3535.setRotationPoint(0.0F, 4.0F, -8.0F);
		this.field_3535.setTextureOffset(22, 0).addBox(-5.0F, -5.0F, -4.0F, 1, 3, 1, 0.0F);
		this.field_3535.setTextureOffset(22, 0).addBox(4.0F, -5.0F, -4.0F, 1, 3, 1, 0.0F);
		this.field_3538 = new Cuboid(this, 18, 4);
		this.field_3538.addBox(-6.0F, -10.0F, -7.0F, 12, 18, 10, 0.0F);
		this.field_3538.setRotationPoint(0.0F, 5.0F, 2.0F);
		this.field_3538.setTextureOffset(52, 0).addBox(-2.0F, 2.0F, -8.0F, 4, 6, 1);
		this.field_3536.rotationPointX--;
		this.field_3534.rotationPointX++;
		this.field_3536.rotationPointZ += 0.0F;
		this.field_3534.rotationPointZ += 0.0F;
		this.field_3533.rotationPointX--;
		this.field_3539.rotationPointX++;
		this.field_3533.rotationPointZ--;
		this.field_3539.rotationPointZ--;
		this.field_3537 += 2.0F;
	}

	public Cuboid method_2800() {
		return this.field_3535;
	}
}
