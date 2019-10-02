package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.passive.AbstractDonkeyEntity;

@Environment(EnvType.CLIENT)
public class DonkeyEntityModel<T extends AbstractDonkeyEntity> extends HorseEntityModel<T> {
	private final ModelPart field_3349 = new ModelPart(this, 26, 21);
	private final ModelPart field_3348;

	public DonkeyEntityModel(float f) {
		super(f);
		this.field_3349.addCuboid(-4.0F, 0.0F, -2.0F, 8.0F, 8.0F, 3.0F);
		this.field_3348 = new ModelPart(this, 26, 21);
		this.field_3348.addCuboid(-4.0F, 0.0F, -2.0F, 8.0F, 8.0F, 3.0F);
		this.field_3349.yaw = (float) (-Math.PI / 2);
		this.field_3348.yaw = (float) (Math.PI / 2);
		this.field_3349.setPivot(6.0F, -8.0F, 0.0F);
		this.field_3348.setPivot(-6.0F, -8.0F, 0.0F);
		this.field_3305.addChild(this.field_3349);
		this.field_3305.addChild(this.field_3348);
	}

	@Override
	protected void method_2789(ModelPart modelPart) {
		ModelPart modelPart2 = new ModelPart(this, 0, 12);
		modelPart2.addCuboid(-1.0F, -7.0F, 0.0F, 2.0F, 7.0F, 1.0F);
		modelPart2.setPivot(1.25F, -10.0F, 4.0F);
		ModelPart modelPart3 = new ModelPart(this, 0, 12);
		modelPart3.addCuboid(-1.0F, -7.0F, 0.0F, 2.0F, 7.0F, 1.0F);
		modelPart3.setPivot(-1.25F, -10.0F, 4.0F);
		modelPart2.pitch = (float) (Math.PI / 12);
		modelPart2.roll = (float) (Math.PI / 12);
		modelPart3.pitch = (float) (Math.PI / 12);
		modelPart3.roll = (float) (-Math.PI / 12);
		modelPart.addChild(modelPart2);
		modelPart.addChild(modelPart3);
	}

	public void method_17076(T abstractDonkeyEntity, float f, float g, float h, float i, float j, float k) {
		super.method_17085(abstractDonkeyEntity, f, g, h, i, j, k);
		if (abstractDonkeyEntity.hasChest()) {
			this.field_3349.visible = true;
			this.field_3348.visible = true;
		} else {
			this.field_3349.visible = false;
			this.field_3348.visible = false;
		}
	}
}
