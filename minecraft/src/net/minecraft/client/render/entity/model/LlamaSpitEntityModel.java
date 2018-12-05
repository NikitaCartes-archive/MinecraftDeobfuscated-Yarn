package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Cuboid;
import net.minecraft.client.model.Model;
import net.minecraft.entity.Entity;

@Environment(EnvType.CLIENT)
public class LlamaSpitEntityModel extends Model {
	private final Cuboid field_3433 = new Cuboid(this);

	public LlamaSpitEntityModel() {
		this(0.0F);
	}

	public LlamaSpitEntityModel(float f) {
		int i = 2;
		this.field_3433.setTextureOffset(0, 0).addBox(-4.0F, 0.0F, 0.0F, 2, 2, 2, f);
		this.field_3433.setTextureOffset(0, 0).addBox(0.0F, -4.0F, 0.0F, 2, 2, 2, f);
		this.field_3433.setTextureOffset(0, 0).addBox(0.0F, 0.0F, -4.0F, 2, 2, 2, f);
		this.field_3433.setTextureOffset(0, 0).addBox(0.0F, 0.0F, 0.0F, 2, 2, 2, f);
		this.field_3433.setTextureOffset(0, 0).addBox(2.0F, 0.0F, 0.0F, 2, 2, 2, f);
		this.field_3433.setTextureOffset(0, 0).addBox(0.0F, 2.0F, 0.0F, 2, 2, 2, f);
		this.field_3433.setTextureOffset(0, 0).addBox(0.0F, 0.0F, 2.0F, 2, 2, 2, f);
		this.field_3433.setRotationPoint(0.0F, 0.0F, 0.0F);
	}

	@Override
	public void render(Entity entity, float f, float g, float h, float i, float j, float k) {
		this.setRotationAngles(f, g, h, i, j, k, entity);
		this.field_3433.render(k);
	}
}
