package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Cuboid;
import net.minecraft.entity.passive.AbstractDonkeyEntity;

@Environment(EnvType.CLIENT)
public class DonkeyEntityModel<T extends AbstractDonkeyEntity> extends HorseEntityModel<T> {
	private final Cuboid field_3349 = new Cuboid(this, 26, 21);
	private final Cuboid field_3348;

	public DonkeyEntityModel(float f) {
		super(f);
		this.field_3349.addBox(-4.0F, 0.0F, -2.0F, 8, 8, 3);
		this.field_3348 = new Cuboid(this, 26, 21);
		this.field_3348.addBox(-4.0F, 0.0F, -2.0F, 8, 8, 3);
		this.field_3349.yaw = (float) (-Math.PI / 2);
		this.field_3348.yaw = (float) (Math.PI / 2);
		this.field_3349.setRotationPoint(6.0F, -8.0F, 0.0F);
		this.field_3348.setRotationPoint(-6.0F, -8.0F, 0.0F);
		this.field_3305.addChild(this.field_3349);
		this.field_3305.addChild(this.field_3348);
	}

	@Override
	protected void method_2789(Cuboid cuboid) {
		Cuboid cuboid2 = new Cuboid(this, 0, 12);
		cuboid2.addBox(-1.0F, -7.0F, 0.0F, 2, 7, 1);
		cuboid2.setRotationPoint(1.25F, -10.0F, 4.0F);
		Cuboid cuboid3 = new Cuboid(this, 0, 12);
		cuboid3.addBox(-1.0F, -7.0F, 0.0F, 2, 7, 1);
		cuboid3.setRotationPoint(-1.25F, -10.0F, 4.0F);
		cuboid2.pitch = (float) (Math.PI / 12);
		cuboid2.roll = (float) (Math.PI / 12);
		cuboid3.pitch = (float) (Math.PI / 12);
		cuboid3.roll = (float) (-Math.PI / 12);
		cuboid.addChild(cuboid2);
		cuboid.addChild(cuboid3);
	}

	public void method_17076(T abstractDonkeyEntity, float f, float g, float h, float i, float j, float k) {
		if (abstractDonkeyEntity.hasChest()) {
			this.field_3349.visible = true;
			this.field_3348.visible = true;
		} else {
			this.field_3349.visible = false;
			this.field_3348.visible = false;
		}

		super.method_17085(abstractDonkeyEntity, f, g, h, i, j, k);
	}
}
