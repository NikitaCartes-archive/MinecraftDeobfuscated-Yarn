package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class CreeperEntityModel<T extends Entity> extends EntityModel<T> {
	private final ModelPart head;
	private final ModelPart headHat;
	private final ModelPart body;
	private final ModelPart leg1;
	private final ModelPart leg2;
	private final ModelPart leg3;
	private final ModelPart leg4;

	public CreeperEntityModel() {
		this(0.0F);
	}

	public CreeperEntityModel(float f) {
		int i = 6;
		this.head = new ModelPart(this, 0, 0);
		this.head.addCuboid(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, f);
		this.head.setRotationPoint(0.0F, 6.0F, 0.0F);
		this.headHat = new ModelPart(this, 32, 0);
		this.headHat.addCuboid(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, f + 0.5F);
		this.headHat.setRotationPoint(0.0F, 6.0F, 0.0F);
		this.body = new ModelPart(this, 16, 16);
		this.body.addCuboid(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, f);
		this.body.setRotationPoint(0.0F, 6.0F, 0.0F);
		this.leg1 = new ModelPart(this, 0, 16);
		this.leg1.addCuboid(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, f);
		this.leg1.setRotationPoint(-2.0F, 18.0F, 4.0F);
		this.leg2 = new ModelPart(this, 0, 16);
		this.leg2.addCuboid(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, f);
		this.leg2.setRotationPoint(2.0F, 18.0F, 4.0F);
		this.leg3 = new ModelPart(this, 0, 16);
		this.leg3.addCuboid(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, f);
		this.leg3.setRotationPoint(-2.0F, 18.0F, -4.0F);
		this.leg4 = new ModelPart(this, 0, 16);
		this.leg4.addCuboid(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, f);
		this.leg4.setRotationPoint(2.0F, 18.0F, -4.0F);
	}

	@Override
	public void render(T entity, float f, float g, float h, float i, float j, float k) {
		this.setAngles(entity, f, g, h, i, j, k);
		this.head.render(k);
		this.body.render(k);
		this.leg1.render(k);
		this.leg2.render(k);
		this.leg3.render(k);
		this.leg4.render(k);
	}

	@Override
	public void setAngles(T entity, float f, float g, float h, float i, float j, float k) {
		this.head.yaw = i * (float) (Math.PI / 180.0);
		this.head.pitch = j * (float) (Math.PI / 180.0);
		this.leg1.pitch = MathHelper.cos(f * 0.6662F) * 1.4F * g;
		this.leg2.pitch = MathHelper.cos(f * 0.6662F + (float) Math.PI) * 1.4F * g;
		this.leg3.pitch = MathHelper.cos(f * 0.6662F + (float) Math.PI) * 1.4F * g;
		this.leg4.pitch = MathHelper.cos(f * 0.6662F) * 1.4F * g;
	}
}
