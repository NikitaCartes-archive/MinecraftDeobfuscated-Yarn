package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Cuboid;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class MagmaCubeEntityModel<T extends SlimeEntity> extends EntityModel<T> {
	private final Cuboid[] field_3427 = new Cuboid[8];
	private final Cuboid field_3428;

	public MagmaCubeEntityModel() {
		for (int i = 0; i < this.field_3427.length; i++) {
			int j = 0;
			int k = i;
			if (i == 2) {
				j = 24;
				k = 10;
			} else if (i == 3) {
				j = 24;
				k = 19;
			}

			this.field_3427[i] = new Cuboid(this, j, k);
			this.field_3427[i].addBox(-4.0F, (float)(16 + i), -4.0F, 8, 1, 8);
		}

		this.field_3428 = new Cuboid(this, 0, 16);
		this.field_3428.addBox(-2.0F, 18.0F, -2.0F, 4, 4, 4);
	}

	public void method_17098(T slimeEntity, float f, float g, float h) {
		float i = MathHelper.lerp(h, slimeEntity.lastStretch, slimeEntity.stretch);
		if (i < 0.0F) {
			i = 0.0F;
		}

		for (int j = 0; j < this.field_3427.length; j++) {
			this.field_3427[j].rotationPointY = (float)(-(4 - j)) * i * 1.7F;
		}
	}

	public void method_17099(T slimeEntity, float f, float g, float h, float i, float j, float k) {
		this.setAngles(slimeEntity, f, g, h, i, j, k);
		this.field_3428.render(k);

		for (Cuboid cuboid : this.field_3427) {
			cuboid.render(k);
		}
	}
}
