package net.minecraft.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import java.util.Arrays;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4595;
import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class BlazeEntityModel<T extends Entity> extends class_4595<T> {
	private final ModelPart[] rods;
	private final ModelPart head = new ModelPart(this, 0, 0);
	private final ImmutableList<ModelPart> field_20921;

	public BlazeEntityModel() {
		this.head.addCuboid(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F);
		this.rods = new ModelPart[12];

		for (int i = 0; i < this.rods.length; i++) {
			this.rods[i] = new ModelPart(this, 0, 16);
			this.rods[i].addCuboid(0.0F, 0.0F, 0.0F, 2.0F, 8.0F, 2.0F);
		}

		Builder<ModelPart> builder = ImmutableList.builder();
		builder.add(this.head);
		builder.addAll(Arrays.asList(this.rods));
		this.field_20921 = builder.build();
	}

	@Override
	public Iterable<ModelPart> getParts() {
		return this.field_20921;
	}

	@Override
	public void setAngles(T entity, float f, float g, float h, float i, float j, float k) {
		float l = h * (float) Math.PI * -0.1F;

		for (int m = 0; m < 4; m++) {
			this.rods[m].pivotY = -2.0F + MathHelper.cos(((float)(m * 2) + h) * 0.25F);
			this.rods[m].pivotX = MathHelper.cos(l) * 9.0F;
			this.rods[m].pivotZ = MathHelper.sin(l) * 9.0F;
			l++;
		}

		l = (float) (Math.PI / 4) + h * (float) Math.PI * 0.03F;

		for (int m = 4; m < 8; m++) {
			this.rods[m].pivotY = 2.0F + MathHelper.cos(((float)(m * 2) + h) * 0.25F);
			this.rods[m].pivotX = MathHelper.cos(l) * 7.0F;
			this.rods[m].pivotZ = MathHelper.sin(l) * 7.0F;
			l++;
		}

		l = 0.47123894F + h * (float) Math.PI * -0.05F;

		for (int m = 8; m < 12; m++) {
			this.rods[m].pivotY = 11.0F + MathHelper.cos(((float)m * 1.5F + h) * 0.5F);
			this.rods[m].pivotX = MathHelper.cos(l) * 5.0F;
			this.rods[m].pivotZ = MathHelper.sin(l) * 5.0F;
			l++;
		}

		this.head.yaw = i * (float) (Math.PI / 180.0);
		this.head.pitch = j * (float) (Math.PI / 180.0);
	}
}
