package net.minecraft.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.Entity;

@Environment(EnvType.CLIENT)
public class SlimeEntityModel<T extends Entity> extends CompositeEntityModel<T> {
	private final ModelPart field_3571;
	private final ModelPart field_3573;
	private final ModelPart field_3572;
	private final ModelPart field_3570;

	public SlimeEntityModel(int i) {
		this.field_3571 = new ModelPart(this, 0, i);
		this.field_3573 = new ModelPart(this, 32, 0);
		this.field_3572 = new ModelPart(this, 32, 4);
		this.field_3570 = new ModelPart(this, 32, 8);
		if (i > 0) {
			this.field_3571.addCuboid(-3.0F, 17.0F, -3.0F, 6.0F, 6.0F, 6.0F);
			this.field_3573.addCuboid(-3.25F, 18.0F, -3.5F, 2.0F, 2.0F, 2.0F);
			this.field_3572.addCuboid(1.25F, 18.0F, -3.5F, 2.0F, 2.0F, 2.0F);
			this.field_3570.addCuboid(0.0F, 21.0F, -3.5F, 1.0F, 1.0F, 1.0F);
		} else {
			this.field_3571.addCuboid(-4.0F, 16.0F, -4.0F, 8.0F, 8.0F, 8.0F);
		}
	}

	@Override
	public void setAngles(T entity, float limbAngle, float limbDistance, float age, float headYaw, float headPitch) {
	}

	@Override
	public Iterable<ModelPart> getParts() {
		return ImmutableList.<ModelPart>of(this.field_3571, this.field_3573, this.field_3572, this.field_3570);
	}
}
