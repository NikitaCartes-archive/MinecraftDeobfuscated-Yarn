package net.minecraft.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.Entity;

@Environment(EnvType.CLIENT)
public class SlimeEntityModel<T extends Entity> extends CompositeEntityModel<T> {
	private final ModelPart innerCube;
	private final ModelPart rightEye;
	private final ModelPart leftEye;
	private final ModelPart mouth;

	public SlimeEntityModel(int i) {
		this.innerCube = new ModelPart(this, 0, i);
		this.rightEye = new ModelPart(this, 32, 0);
		this.leftEye = new ModelPart(this, 32, 4);
		this.mouth = new ModelPart(this, 32, 8);
		if (i > 0) {
			this.innerCube.addCuboid(-3.0F, 17.0F, -3.0F, 6.0F, 6.0F, 6.0F);
			this.rightEye.addCuboid(-3.25F, 18.0F, -3.5F, 2.0F, 2.0F, 2.0F);
			this.leftEye.addCuboid(1.25F, 18.0F, -3.5F, 2.0F, 2.0F, 2.0F);
			this.mouth.addCuboid(0.0F, 21.0F, -3.5F, 1.0F, 1.0F, 1.0F);
		} else {
			this.innerCube.addCuboid(-4.0F, 16.0F, -4.0F, 8.0F, 8.0F, 8.0F);
		}
	}

	@Override
	public void setAngles(T entity, float limbAngle, float limbDistance, float customAngle, float headYaw, float headPitch) {
	}

	@Override
	public Iterable<ModelPart> getParts() {
		return ImmutableList.<ModelPart>of(this.innerCube, this.rightEye, this.leftEye, this.mouth);
	}
}
