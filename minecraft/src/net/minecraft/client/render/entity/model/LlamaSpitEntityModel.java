package net.minecraft.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.Entity;

@Environment(EnvType.CLIENT)
public class LlamaSpitEntityModel<T extends Entity> extends CompositeEntityModel<T> {
	private final ModelPart field_3433 = new ModelPart(this);

	public LlamaSpitEntityModel() {
		this(0.0F);
	}

	public LlamaSpitEntityModel(float f) {
		int i = 2;
		this.field_3433.setTextureOffset(0, 0).addCuboid(-4.0F, 0.0F, 0.0F, 2.0F, 2.0F, 2.0F, f);
		this.field_3433.setTextureOffset(0, 0).addCuboid(0.0F, -4.0F, 0.0F, 2.0F, 2.0F, 2.0F, f);
		this.field_3433.setTextureOffset(0, 0).addCuboid(0.0F, 0.0F, -4.0F, 2.0F, 2.0F, 2.0F, f);
		this.field_3433.setTextureOffset(0, 0).addCuboid(0.0F, 0.0F, 0.0F, 2.0F, 2.0F, 2.0F, f);
		this.field_3433.setTextureOffset(0, 0).addCuboid(2.0F, 0.0F, 0.0F, 2.0F, 2.0F, 2.0F, f);
		this.field_3433.setTextureOffset(0, 0).addCuboid(0.0F, 2.0F, 0.0F, 2.0F, 2.0F, 2.0F, f);
		this.field_3433.setTextureOffset(0, 0).addCuboid(0.0F, 0.0F, 2.0F, 2.0F, 2.0F, 2.0F, f);
		this.field_3433.setPivot(0.0F, 0.0F, 0.0F);
	}

	@Override
	public void setAngles(T entity, float f, float g, float h, float i, float j, float k) {
	}

	@Override
	public Iterable<ModelPart> getParts() {
		return ImmutableList.<ModelPart>of(this.field_3433);
	}
}
