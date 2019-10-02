package net.minecraft.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4595;
import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.Entity;

@Environment(EnvType.CLIENT)
public class LeashEntityModel<T extends Entity> extends class_4595<T> {
	private final ModelPart field_3431;

	public LeashEntityModel() {
		this(0, 0, 32, 32);
	}

	public LeashEntityModel(int i, int j, int k, int l) {
		this.textureWidth = k;
		this.textureHeight = l;
		this.field_3431 = new ModelPart(this, i, j);
		this.field_3431.addCuboid(-3.0F, -6.0F, -3.0F, 6.0F, 8.0F, 6.0F, 0.0F);
		this.field_3431.setPivot(0.0F, 0.0F, 0.0F);
	}

	@Override
	public Iterable<ModelPart> getParts() {
		return ImmutableList.<ModelPart>of(this.field_3431);
	}

	@Override
	public void setAngles(T entity, float f, float g, float h, float i, float j, float k) {
		this.field_3431.yaw = i * (float) (Math.PI / 180.0);
		this.field_3431.pitch = j * (float) (Math.PI / 180.0);
	}
}
