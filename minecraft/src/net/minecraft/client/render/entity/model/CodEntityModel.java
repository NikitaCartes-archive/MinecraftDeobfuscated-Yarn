package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class CodEntityModel<T extends Entity> extends EntityModel<T> {
	private final ModelPart body;
	private final ModelPart topFin;
	private final ModelPart head;
	private final ModelPart mouth;
	private final ModelPart leftFint;
	private final ModelPart rightFin;
	private final ModelPart tailFin;

	public CodEntityModel() {
		this.textureWidth = 32;
		this.textureHeight = 32;
		int i = 22;
		this.body = new ModelPart(this, 0, 0);
		this.body.addCuboid(-1.0F, -2.0F, 0.0F, 2.0F, 4.0F, 7.0F);
		this.body.setRotationPoint(0.0F, 22.0F, 0.0F);
		this.head = new ModelPart(this, 11, 0);
		this.head.addCuboid(-1.0F, -2.0F, -3.0F, 2.0F, 4.0F, 3.0F);
		this.head.setRotationPoint(0.0F, 22.0F, 0.0F);
		this.mouth = new ModelPart(this, 0, 0);
		this.mouth.addCuboid(-1.0F, -2.0F, -1.0F, 2.0F, 3.0F, 1.0F);
		this.mouth.setRotationPoint(0.0F, 22.0F, -3.0F);
		this.leftFint = new ModelPart(this, 22, 1);
		this.leftFint.addCuboid(-2.0F, 0.0F, -1.0F, 2.0F, 0.0F, 2.0F);
		this.leftFint.setRotationPoint(-1.0F, 23.0F, 0.0F);
		this.leftFint.roll = (float) (-Math.PI / 4);
		this.rightFin = new ModelPart(this, 22, 4);
		this.rightFin.addCuboid(0.0F, 0.0F, -1.0F, 2.0F, 0.0F, 2.0F);
		this.rightFin.setRotationPoint(1.0F, 23.0F, 0.0F);
		this.rightFin.roll = (float) (Math.PI / 4);
		this.tailFin = new ModelPart(this, 22, 3);
		this.tailFin.addCuboid(0.0F, -2.0F, 0.0F, 0.0F, 4.0F, 4.0F);
		this.tailFin.setRotationPoint(0.0F, 22.0F, 7.0F);
		this.topFin = new ModelPart(this, 20, -6);
		this.topFin.addCuboid(0.0F, -1.0F, -1.0F, 0.0F, 1.0F, 6.0F);
		this.topFin.setRotationPoint(0.0F, 20.0F, 0.0F);
	}

	@Override
	public void render(T entity, float f, float g, float h, float i, float j, float k) {
		this.setAngles(entity, f, g, h, i, j, k);
		this.body.render(k);
		this.head.render(k);
		this.mouth.render(k);
		this.leftFint.render(k);
		this.rightFin.render(k);
		this.tailFin.render(k);
		this.topFin.render(k);
	}

	@Override
	public void setAngles(T entity, float f, float g, float h, float i, float j, float k) {
		float l = 1.0F;
		if (!entity.isInsideWater()) {
			l = 1.5F;
		}

		this.tailFin.yaw = -l * 0.45F * MathHelper.sin(0.6F * h);
	}
}
