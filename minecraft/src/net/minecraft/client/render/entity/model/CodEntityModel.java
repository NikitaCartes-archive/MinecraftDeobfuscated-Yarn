package net.minecraft.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class CodEntityModel<T extends Entity> extends CompositeEntityModel<T> {
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
		this.body.setPivot(0.0F, 22.0F, 0.0F);
		this.head = new ModelPart(this, 11, 0);
		this.head.addCuboid(-1.0F, -2.0F, -3.0F, 2.0F, 4.0F, 3.0F);
		this.head.setPivot(0.0F, 22.0F, 0.0F);
		this.mouth = new ModelPart(this, 0, 0);
		this.mouth.addCuboid(-1.0F, -2.0F, -1.0F, 2.0F, 3.0F, 1.0F);
		this.mouth.setPivot(0.0F, 22.0F, -3.0F);
		this.leftFint = new ModelPart(this, 22, 1);
		this.leftFint.addCuboid(-2.0F, 0.0F, -1.0F, 2.0F, 0.0F, 2.0F);
		this.leftFint.setPivot(-1.0F, 23.0F, 0.0F);
		this.leftFint.roll = (float) (-Math.PI / 4);
		this.rightFin = new ModelPart(this, 22, 4);
		this.rightFin.addCuboid(0.0F, 0.0F, -1.0F, 2.0F, 0.0F, 2.0F);
		this.rightFin.setPivot(1.0F, 23.0F, 0.0F);
		this.rightFin.roll = (float) (Math.PI / 4);
		this.tailFin = new ModelPart(this, 22, 3);
		this.tailFin.addCuboid(0.0F, -2.0F, 0.0F, 0.0F, 4.0F, 4.0F);
		this.tailFin.setPivot(0.0F, 22.0F, 7.0F);
		this.topFin = new ModelPart(this, 20, -6);
		this.topFin.addCuboid(0.0F, -1.0F, -1.0F, 0.0F, 1.0F, 6.0F);
		this.topFin.setPivot(0.0F, 20.0F, 0.0F);
	}

	@Override
	public Iterable<ModelPart> getParts() {
		return ImmutableList.<ModelPart>of(this.body, this.head, this.mouth, this.leftFint, this.rightFin, this.tailFin, this.topFin);
	}

	@Override
	public void setAngles(T entity, float limbAngle, float limbDistance, float age, float headYaw, float headPitch) {
		float f = 1.0F;
		if (!entity.isInsideWater()) {
			f = 1.5F;
		}

		this.tailFin.yaw = -f * 0.45F * MathHelper.sin(0.6F * age);
	}
}
