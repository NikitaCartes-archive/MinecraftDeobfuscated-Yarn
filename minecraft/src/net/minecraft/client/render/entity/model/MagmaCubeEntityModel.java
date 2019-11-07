package net.minecraft.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import java.util.Arrays;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class MagmaCubeEntityModel<T extends SlimeEntity> extends CompositeEntityModel<T> {
	private final ModelPart[] field_3427 = new ModelPart[8];
	private final ModelPart field_3428;
	private final ImmutableList<ModelPart> field_20934;

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

			this.field_3427[i] = new ModelPart(this, j, k);
			this.field_3427[i].addCuboid(-4.0F, (float)(16 + i), -4.0F, 8.0F, 1.0F, 8.0F);
		}

		this.field_3428 = new ModelPart(this, 0, 16);
		this.field_3428.addCuboid(-2.0F, 18.0F, -2.0F, 4.0F, 4.0F, 4.0F);
		Builder<ModelPart> builder = ImmutableList.builder();
		builder.add(this.field_3428);
		builder.addAll(Arrays.asList(this.field_3427));
		this.field_20934 = builder.build();
	}

	public void method_22958(T slimeEntity, float f, float g, float h, float i, float j) {
	}

	public void method_17098(T slimeEntity, float f, float g, float h) {
		float i = MathHelper.lerp(h, slimeEntity.lastStretch, slimeEntity.stretch);
		if (i < 0.0F) {
			i = 0.0F;
		}

		for (int j = 0; j < this.field_3427.length; j++) {
			this.field_3427[j].pivotY = (float)(-(4 - j)) * i * 1.7F;
		}
	}

	public ImmutableList<ModelPart> method_22959() {
		return this.field_20934;
	}
}
