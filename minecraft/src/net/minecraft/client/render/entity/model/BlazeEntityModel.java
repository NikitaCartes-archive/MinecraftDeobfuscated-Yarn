package net.minecraft.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import java.util.Arrays;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class BlazeEntityModel<T extends Entity> extends CompositeEntityModel<T> {
	private final ModelPart[] rods;
	private final ModelPart head = new ModelPart(this, 0, 0);
	private final ImmutableList<ModelPart> parts;

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
		this.parts = builder.build();
	}

	@Override
	public Iterable<ModelPart> getParts() {
		return this.parts;
	}

	@Override
	public void setAngles(T entity, float limbAngle, float limbDistance, float age, float headYaw, float headPitch) {
		float f = age * (float) Math.PI * -0.1F;

		for (int i = 0; i < 4; i++) {
			this.rods[i].pivotY = -2.0F + MathHelper.cos(((float)(i * 2) + age) * 0.25F);
			this.rods[i].pivotX = MathHelper.cos(f) * 9.0F;
			this.rods[i].pivotZ = MathHelper.sin(f) * 9.0F;
			f++;
		}

		f = (float) (Math.PI / 4) + age * (float) Math.PI * 0.03F;

		for (int i = 4; i < 8; i++) {
			this.rods[i].pivotY = 2.0F + MathHelper.cos(((float)(i * 2) + age) * 0.25F);
			this.rods[i].pivotX = MathHelper.cos(f) * 7.0F;
			this.rods[i].pivotZ = MathHelper.sin(f) * 7.0F;
			f++;
		}

		f = 0.47123894F + age * (float) Math.PI * -0.05F;

		for (int i = 8; i < 12; i++) {
			this.rods[i].pivotY = 11.0F + MathHelper.cos(((float)i * 1.5F + age) * 0.5F);
			this.rods[i].pivotX = MathHelper.cos(f) * 5.0F;
			this.rods[i].pivotZ = MathHelper.sin(f) * 5.0F;
			f++;
		}

		this.head.yaw = headYaw * (float) (Math.PI / 180.0);
		this.head.pitch = headPitch * (float) (Math.PI / 180.0);
	}
}
