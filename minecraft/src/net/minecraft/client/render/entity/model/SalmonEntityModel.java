package net.minecraft.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4595;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class SalmonEntityModel<T extends Entity> extends class_4595<T> {
	private final ModelPart field_3546;
	private final ModelPart field_3548;
	private final ModelPart field_3547;
	private final ModelPart field_3542;
	private final ModelPart field_3544;

	public SalmonEntityModel() {
		super(RenderLayer::getEntityCutoutNoCull);
		this.textureWidth = 32;
		this.textureHeight = 32;
		int i = 20;
		this.field_3546 = new ModelPart(this, 0, 0);
		this.field_3546.addCuboid(-1.5F, -2.5F, 0.0F, 3.0F, 5.0F, 8.0F);
		this.field_3546.setPivot(0.0F, 20.0F, 0.0F);
		this.field_3548 = new ModelPart(this, 0, 13);
		this.field_3548.addCuboid(-1.5F, -2.5F, 0.0F, 3.0F, 5.0F, 8.0F);
		this.field_3548.setPivot(0.0F, 20.0F, 8.0F);
		this.field_3547 = new ModelPart(this, 22, 0);
		this.field_3547.addCuboid(-1.0F, -2.0F, -3.0F, 2.0F, 4.0F, 3.0F);
		this.field_3547.setPivot(0.0F, 20.0F, 0.0F);
		ModelPart modelPart = new ModelPart(this, 20, 10);
		modelPart.addCuboid(0.0F, -2.5F, 0.0F, 0.0F, 5.0F, 6.0F);
		modelPart.setPivot(0.0F, 0.0F, 8.0F);
		this.field_3548.addChild(modelPart);
		ModelPart modelPart2 = new ModelPart(this, 2, 1);
		modelPart2.addCuboid(0.0F, 0.0F, 0.0F, 0.0F, 2.0F, 3.0F);
		modelPart2.setPivot(0.0F, -4.5F, 5.0F);
		this.field_3546.addChild(modelPart2);
		ModelPart modelPart3 = new ModelPart(this, 0, 2);
		modelPart3.addCuboid(0.0F, 0.0F, 0.0F, 0.0F, 2.0F, 4.0F);
		modelPart3.setPivot(0.0F, -4.5F, -1.0F);
		this.field_3548.addChild(modelPart3);
		this.field_3542 = new ModelPart(this, -4, 0);
		this.field_3542.addCuboid(-2.0F, 0.0F, 0.0F, 2.0F, 0.0F, 2.0F);
		this.field_3542.setPivot(-1.5F, 21.5F, 0.0F);
		this.field_3542.roll = (float) (-Math.PI / 4);
		this.field_3544 = new ModelPart(this, 0, 0);
		this.field_3544.addCuboid(0.0F, 0.0F, 0.0F, 2.0F, 0.0F, 2.0F);
		this.field_3544.setPivot(1.5F, 21.5F, 0.0F);
		this.field_3544.roll = (float) (Math.PI / 4);
	}

	@Override
	public Iterable<ModelPart> getParts() {
		return ImmutableList.<ModelPart>of(this.field_3546, this.field_3548, this.field_3547, this.field_3542, this.field_3544);
	}

	@Override
	public void setAngles(T entity, float f, float g, float h, float i, float j, float k) {
		float l = 1.0F;
		float m = 1.0F;
		if (!entity.isInsideWater()) {
			l = 1.3F;
			m = 1.7F;
		}

		this.field_3548.yaw = -l * 0.25F * MathHelper.sin(m * 0.6F * h);
	}
}
