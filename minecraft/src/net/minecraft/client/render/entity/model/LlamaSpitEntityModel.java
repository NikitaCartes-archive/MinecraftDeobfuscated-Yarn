package net.minecraft.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.Entity;

@Environment(EnvType.CLIENT)
public class LlamaSpitEntityModel<T extends Entity> extends CompositeEntityModel<T> {
	private final ModelPart spit = new ModelPart(this);

	public LlamaSpitEntityModel() {
		this(0.0F);
	}

	public LlamaSpitEntityModel(float scale) {
		int i = 2;
		this.spit.setTextureOffset(0, 0).addCuboid(-4.0F, 0.0F, 0.0F, 2.0F, 2.0F, 2.0F, scale);
		this.spit.setTextureOffset(0, 0).addCuboid(0.0F, -4.0F, 0.0F, 2.0F, 2.0F, 2.0F, scale);
		this.spit.setTextureOffset(0, 0).addCuboid(0.0F, 0.0F, -4.0F, 2.0F, 2.0F, 2.0F, scale);
		this.spit.setTextureOffset(0, 0).addCuboid(0.0F, 0.0F, 0.0F, 2.0F, 2.0F, 2.0F, scale);
		this.spit.setTextureOffset(0, 0).addCuboid(2.0F, 0.0F, 0.0F, 2.0F, 2.0F, 2.0F, scale);
		this.spit.setTextureOffset(0, 0).addCuboid(0.0F, 2.0F, 0.0F, 2.0F, 2.0F, 2.0F, scale);
		this.spit.setTextureOffset(0, 0).addCuboid(0.0F, 0.0F, 2.0F, 2.0F, 2.0F, 2.0F, scale);
		this.spit.setPivot(0.0F, 0.0F, 0.0F);
	}

	@Override
	public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
	}

	@Override
	public Iterable<ModelPart> getParts() {
		return ImmutableList.<ModelPart>of(this.spit);
	}
}
