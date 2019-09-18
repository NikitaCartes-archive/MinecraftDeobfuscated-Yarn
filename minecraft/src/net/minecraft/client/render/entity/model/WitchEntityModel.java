package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class WitchEntityModel<T extends Entity> extends VillagerResemblingModel<T> {
	private boolean field_3614;
	private final ModelPart mole = new ModelPart(this).setTextureSize(64, 128);

	public WitchEntityModel(float f) {
		super(f, 64, 128);
		this.mole.setRotationPoint(0.0F, -2.0F, 0.0F);
		this.mole.setTextureOffset(0, 0).addCuboid(0.0F, 3.0F, -6.75F, 1.0F, 1.0F, 1.0F, -0.25F);
		this.nose.addChild(this.mole);
		this.head = new ModelPart(this).setTextureSize(64, 128);
		this.head.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.head.setTextureOffset(0, 0).addCuboid(-4.0F, -10.0F, -4.0F, 8.0F, 10.0F, 8.0F, f);
		this.headOverlay = new ModelPart(this).setTextureSize(64, 128);
		this.headOverlay.setRotationPoint(-5.0F, -10.03125F, -5.0F);
		this.headOverlay.setTextureOffset(0, 64).addCuboid(0.0F, 0.0F, 0.0F, 10.0F, 2.0F, 10.0F);
		this.head.addChild(this.headOverlay);
		this.head.addChild(this.nose);
		ModelPart modelPart = new ModelPart(this).setTextureSize(64, 128);
		modelPart.setRotationPoint(1.75F, -4.0F, 2.0F);
		modelPart.setTextureOffset(0, 76).addCuboid(0.0F, 0.0F, 0.0F, 7.0F, 4.0F, 7.0F);
		modelPart.pitch = -0.05235988F;
		modelPart.roll = 0.02617994F;
		this.headOverlay.addChild(modelPart);
		ModelPart modelPart2 = new ModelPart(this).setTextureSize(64, 128);
		modelPart2.setRotationPoint(1.75F, -4.0F, 2.0F);
		modelPart2.setTextureOffset(0, 87).addCuboid(0.0F, 0.0F, 0.0F, 4.0F, 4.0F, 4.0F);
		modelPart2.pitch = -0.10471976F;
		modelPart2.roll = 0.05235988F;
		modelPart.addChild(modelPart2);
		ModelPart modelPart3 = new ModelPart(this).setTextureSize(64, 128);
		modelPart3.setRotationPoint(1.75F, -2.0F, 2.0F);
		modelPart3.setTextureOffset(0, 95).addCuboid(0.0F, 0.0F, 0.0F, 1.0F, 2.0F, 1.0F, 0.25F);
		modelPart3.pitch = (float) (-Math.PI / 15);
		modelPart3.roll = 0.10471976F;
		modelPart2.addChild(modelPart3);
	}

	@Override
	public void setAngles(T entity, float f, float g, float h, float i, float j, float k) {
		super.setAngles(entity, f, g, h, i, j, k);
		this.nose.setRotationPoint(0.0F, -2.0F, 0.0F);
		float l = 0.01F * (float)(entity.getEntityId() % 10);
		this.nose.pitch = MathHelper.sin((float)entity.age * l) * 4.5F * (float) (Math.PI / 180.0);
		this.nose.yaw = 0.0F;
		this.nose.roll = MathHelper.cos((float)entity.age * l) * 2.5F * (float) (Math.PI / 180.0);
		if (this.field_3614) {
			this.nose.setRotationPoint(0.0F, 1.0F, -1.5F);
			this.nose.pitch = -0.9F;
		}
	}

	public ModelPart method_2839() {
		return this.nose;
	}

	public void method_2840(boolean bl) {
		this.field_3614 = bl;
	}
}
