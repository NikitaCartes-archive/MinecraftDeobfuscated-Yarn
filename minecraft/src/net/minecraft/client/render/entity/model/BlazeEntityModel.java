package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class BlazeEntityModel<T extends Entity> extends EntityModel<T> {
	private final ModelPart[] field_3328 = new ModelPart[12];
	private final ModelPart field_3329;

	public BlazeEntityModel() {
		for (int i = 0; i < this.field_3328.length; i++) {
			this.field_3328[i] = new ModelPart(this, 0, 16);
			this.field_3328[i].addCuboid(0.0F, 0.0F, 0.0F, 2, 8, 2);
		}

		this.field_3329 = new ModelPart(this, 0, 0);
		this.field_3329.addCuboid(-4.0F, -4.0F, -4.0F, 8, 8, 8);
	}

	@Override
	public void render(T entity, float f, float g, float h, float i, float j, float k) {
		this.setAngles(entity, f, g, h, i, j, k);
		this.field_3329.render(k);

		for (ModelPart modelPart : this.field_3328) {
			modelPart.render(k);
		}
	}

	@Override
	public void setAngles(T entity, float f, float g, float h, float i, float j, float k) {
		float l = h * (float) Math.PI * -0.1F;

		for (int m = 0; m < 4; m++) {
			this.field_3328[m].rotationPointY = -2.0F + MathHelper.cos(((float)(m * 2) + h) * 0.25F);
			this.field_3328[m].rotationPointX = MathHelper.cos(l) * 9.0F;
			this.field_3328[m].rotationPointZ = MathHelper.sin(l) * 9.0F;
			l++;
		}

		l = (float) (Math.PI / 4) + h * (float) Math.PI * 0.03F;

		for (int m = 4; m < 8; m++) {
			this.field_3328[m].rotationPointY = 2.0F + MathHelper.cos(((float)(m * 2) + h) * 0.25F);
			this.field_3328[m].rotationPointX = MathHelper.cos(l) * 7.0F;
			this.field_3328[m].rotationPointZ = MathHelper.sin(l) * 7.0F;
			l++;
		}

		l = 0.47123894F + h * (float) Math.PI * -0.05F;

		for (int m = 8; m < 12; m++) {
			this.field_3328[m].rotationPointY = 11.0F + MathHelper.cos(((float)m * 1.5F + h) * 0.5F);
			this.field_3328[m].rotationPointX = MathHelper.cos(l) * 5.0F;
			this.field_3328[m].rotationPointZ = MathHelper.sin(l) * 5.0F;
			l++;
		}

		this.field_3329.yaw = i * (float) (Math.PI / 180.0);
		this.field_3329.pitch = j * (float) (Math.PI / 180.0);
	}
}
