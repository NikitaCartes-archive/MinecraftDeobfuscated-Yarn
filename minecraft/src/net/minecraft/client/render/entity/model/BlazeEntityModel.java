package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class BlazeEntityModel<T extends Entity> extends EntityModel<T> {
	private final ModelPart[] rods = new ModelPart[12];
	private final ModelPart head;

	public BlazeEntityModel() {
		for (int i = 0; i < this.rods.length; i++) {
			this.rods[i] = new ModelPart(this, 0, 16);
			this.rods[i].addCuboid(0.0F, 0.0F, 0.0F, 2.0F, 8.0F, 2.0F);
		}

		this.head = new ModelPart(this, 0, 0);
		this.head.addCuboid(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F);
	}

	@Override
	public void render(T entity, float f, float g, float h, float i, float j, float k) {
		this.setAngles(entity, f, g, h, i, j, k);
		this.head.render(k);

		for (ModelPart modelPart : this.rods) {
			modelPart.render(k);
		}
	}

	@Override
	public void setAngles(T entity, float f, float g, float h, float i, float j, float k) {
		float l = h * (float) Math.PI * -0.1F;

		for (int m = 0; m < 4; m++) {
			this.rods[m].rotationPointY = -2.0F + MathHelper.cos(((float)(m * 2) + h) * 0.25F);
			this.rods[m].rotationPointX = MathHelper.cos(l) * 9.0F;
			this.rods[m].rotationPointZ = MathHelper.sin(l) * 9.0F;
			l++;
		}

		l = (float) (Math.PI / 4) + h * (float) Math.PI * 0.03F;

		for (int m = 4; m < 8; m++) {
			this.rods[m].rotationPointY = 2.0F + MathHelper.cos(((float)(m * 2) + h) * 0.25F);
			this.rods[m].rotationPointX = MathHelper.cos(l) * 7.0F;
			this.rods[m].rotationPointZ = MathHelper.sin(l) * 7.0F;
			l++;
		}

		l = 0.47123894F + h * (float) Math.PI * -0.05F;

		for (int m = 8; m < 12; m++) {
			this.rods[m].rotationPointY = 11.0F + MathHelper.cos(((float)m * 1.5F + h) * 0.5F);
			this.rods[m].rotationPointX = MathHelper.cos(l) * 5.0F;
			this.rods[m].rotationPointZ = MathHelper.sin(l) * 5.0F;
			l++;
		}

		this.head.yaw = i * (float) (Math.PI / 180.0);
		this.head.pitch = j * (float) (Math.PI / 180.0);
	}
}
