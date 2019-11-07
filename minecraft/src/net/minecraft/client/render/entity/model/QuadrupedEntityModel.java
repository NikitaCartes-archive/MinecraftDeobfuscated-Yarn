package net.minecraft.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class QuadrupedEntityModel<T extends Entity> extends AnimalModel<T> {
	protected ModelPart head = new ModelPart(this, 0, 0);
	protected ModelPart body;
	protected ModelPart leg1;
	protected ModelPart leg2;
	protected ModelPart leg3;
	protected ModelPart leg4;

	public QuadrupedEntityModel(int ySize, float scale, boolean bl, float f, float g, float h, float i, int j) {
		super(bl, f, g, h, i, (float)j);
		this.head.addCuboid(-4.0F, -4.0F, -8.0F, 8.0F, 8.0F, 8.0F, scale);
		this.head.setPivot(0.0F, (float)(18 - ySize), -6.0F);
		this.body = new ModelPart(this, 28, 8);
		this.body.addCuboid(-5.0F, -10.0F, -7.0F, 10.0F, 16.0F, 8.0F, scale);
		this.body.setPivot(0.0F, (float)(17 - ySize), 2.0F);
		this.leg1 = new ModelPart(this, 0, 16);
		this.leg1.addCuboid(-2.0F, 0.0F, -2.0F, 4.0F, (float)ySize, 4.0F, scale);
		this.leg1.setPivot(-3.0F, (float)(24 - ySize), 7.0F);
		this.leg2 = new ModelPart(this, 0, 16);
		this.leg2.addCuboid(-2.0F, 0.0F, -2.0F, 4.0F, (float)ySize, 4.0F, scale);
		this.leg2.setPivot(3.0F, (float)(24 - ySize), 7.0F);
		this.leg3 = new ModelPart(this, 0, 16);
		this.leg3.addCuboid(-2.0F, 0.0F, -2.0F, 4.0F, (float)ySize, 4.0F, scale);
		this.leg3.setPivot(-3.0F, (float)(24 - ySize), -5.0F);
		this.leg4 = new ModelPart(this, 0, 16);
		this.leg4.addCuboid(-2.0F, 0.0F, -2.0F, 4.0F, (float)ySize, 4.0F, scale);
		this.leg4.setPivot(3.0F, (float)(24 - ySize), -5.0F);
	}

	@Override
	protected Iterable<ModelPart> getHeadParts() {
		return ImmutableList.<ModelPart>of(this.head);
	}

	@Override
	protected Iterable<ModelPart> getBodyParts() {
		return ImmutableList.<ModelPart>of(this.body, this.leg1, this.leg2, this.leg3, this.leg4);
	}

	@Override
	public void setAngles(T entity, float limbAngle, float limbDistance, float age, float headYaw, float headPitch) {
		this.head.pitch = headPitch * (float) (Math.PI / 180.0);
		this.head.yaw = headYaw * (float) (Math.PI / 180.0);
		this.body.pitch = (float) (Math.PI / 2);
		this.leg1.pitch = MathHelper.cos(limbAngle * 0.6662F) * 1.4F * limbDistance;
		this.leg2.pitch = MathHelper.cos(limbAngle * 0.6662F + (float) Math.PI) * 1.4F * limbDistance;
		this.leg3.pitch = MathHelper.cos(limbAngle * 0.6662F + (float) Math.PI) * 1.4F * limbDistance;
		this.leg4.pitch = MathHelper.cos(limbAngle * 0.6662F) * 1.4F * limbDistance;
	}
}
