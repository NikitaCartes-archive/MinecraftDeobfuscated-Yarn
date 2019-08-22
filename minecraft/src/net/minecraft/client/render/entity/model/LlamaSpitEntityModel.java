package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.Entity;

@Environment(EnvType.CLIENT)
public class LlamaSpitEntityModel<T extends Entity> extends EntityModel<T> {
	private final ModelPart field_3433 = new ModelPart(this);

	public LlamaSpitEntityModel() {
		this(0.0F);
	}

	public LlamaSpitEntityModel(float f) {
		int i = 2;
		this.field_3433.setTextureOffset(0, 0).addCuboid(-4.0F, 0.0F, 0.0F, 2, 2, 2, f);
		this.field_3433.setTextureOffset(0, 0).addCuboid(0.0F, -4.0F, 0.0F, 2, 2, 2, f);
		this.field_3433.setTextureOffset(0, 0).addCuboid(0.0F, 0.0F, -4.0F, 2, 2, 2, f);
		this.field_3433.setTextureOffset(0, 0).addCuboid(0.0F, 0.0F, 0.0F, 2, 2, 2, f);
		this.field_3433.setTextureOffset(0, 0).addCuboid(2.0F, 0.0F, 0.0F, 2, 2, 2, f);
		this.field_3433.setTextureOffset(0, 0).addCuboid(0.0F, 2.0F, 0.0F, 2, 2, 2, f);
		this.field_3433.setTextureOffset(0, 0).addCuboid(0.0F, 0.0F, 2.0F, 2, 2, 2, f);
		this.field_3433.setRotationPoint(0.0F, 0.0F, 0.0F);
	}

	@Override
	public void render(T entity, float f, float g, float h, float i, float j, float k) {
		this.setAngles(entity, f, g, h, i, j, k);
		this.field_3433.render(k);
	}
}
