package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Cuboid;
import net.minecraft.client.model.Model;

@Environment(EnvType.CLIENT)
public class BedEntityModel extends Model {
	private final Cuboid field_3316;
	private final Cuboid field_3318;
	private final Cuboid[] field_3317 = new Cuboid[4];

	public BedEntityModel() {
		this.textureWidth = 64;
		this.textureHeight = 64;
		this.field_3316 = new Cuboid(this, 0, 0);
		this.field_3316.addBox(0.0F, 0.0F, 0.0F, 16, 16, 6, 0.0F);
		this.field_3318 = new Cuboid(this, 0, 22);
		this.field_3318.addBox(0.0F, 0.0F, 0.0F, 16, 16, 6, 0.0F);
		this.field_3317[0] = new Cuboid(this, 50, 0);
		this.field_3317[1] = new Cuboid(this, 50, 6);
		this.field_3317[2] = new Cuboid(this, 50, 12);
		this.field_3317[3] = new Cuboid(this, 50, 18);
		this.field_3317[0].addBox(0.0F, 6.0F, -16.0F, 3, 3, 3);
		this.field_3317[1].addBox(0.0F, 6.0F, 0.0F, 3, 3, 3);
		this.field_3317[2].addBox(-16.0F, 6.0F, -16.0F, 3, 3, 3);
		this.field_3317[3].addBox(-16.0F, 6.0F, 0.0F, 3, 3, 3);
		this.field_3317[0].pitch = (float) (Math.PI / 2);
		this.field_3317[1].pitch = (float) (Math.PI / 2);
		this.field_3317[2].pitch = (float) (Math.PI / 2);
		this.field_3317[3].pitch = (float) (Math.PI / 2);
		this.field_3317[0].roll = 0.0F;
		this.field_3317[1].roll = (float) (Math.PI / 2);
		this.field_3317[2].roll = (float) (Math.PI * 3.0 / 2.0);
		this.field_3317[3].roll = (float) Math.PI;
	}

	public void render() {
		this.field_3316.render(0.0625F);
		this.field_3318.render(0.0625F);
		this.field_3317[0].render(0.0625F);
		this.field_3317[1].render(0.0625F);
		this.field_3317[2].render(0.0625F);
		this.field_3317[3].render(0.0625F);
	}

	public void setVisible(boolean bl) {
		this.field_3316.visible = bl;
		this.field_3318.visible = !bl;
		this.field_3317[0].visible = !bl;
		this.field_3317[1].visible = bl;
		this.field_3317[2].visible = !bl;
		this.field_3317[3].visible = bl;
	}
}
