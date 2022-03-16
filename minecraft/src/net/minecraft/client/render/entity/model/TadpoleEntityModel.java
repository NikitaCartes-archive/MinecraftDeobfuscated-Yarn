package net.minecraft.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.entity.passive.TadpoleEntity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class TadpoleEntityModel<T extends TadpoleEntity> extends AnimalModel<T> {
	private final ModelPart root;
	private final ModelPart tail;

	public TadpoleEntityModel(ModelPart root) {
		super(true, 8.0F, 3.35F);
		this.root = root;
		this.tail = root.getChild(EntityModelPartNames.TAIL);
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		float f = 0.0F;
		float g = 22.0F;
		float h = -3.0F;
		modelPartData.addChild(
			EntityModelPartNames.BODY, ModelPartBuilder.create().uv(0, 0).cuboid(-1.5F, -1.0F, 0.0F, 3.0F, 2.0F, 3.0F), ModelTransform.pivot(0.0F, 22.0F, -3.0F)
		);
		modelPartData.addChild(
			EntityModelPartNames.TAIL, ModelPartBuilder.create().uv(0, 0).cuboid(0.0F, -1.0F, 0.0F, 0.0F, 2.0F, 7.0F), ModelTransform.pivot(0.0F, 22.0F, 0.0F)
		);
		return TexturedModelData.of(modelData, 16, 16);
	}

	@Override
	protected Iterable<ModelPart> getHeadParts() {
		return ImmutableList.<ModelPart>of(this.root);
	}

	@Override
	protected Iterable<ModelPart> getBodyParts() {
		return ImmutableList.<ModelPart>of(this.tail);
	}

	public void setAngles(T tadpoleEntity, float f, float g, float h, float i, float j) {
		float k = tadpoleEntity.isTouchingWater() ? 1.0F : 1.5F;
		this.tail.yaw = -k * 0.25F * MathHelper.sin(0.3F * h);
	}
}
