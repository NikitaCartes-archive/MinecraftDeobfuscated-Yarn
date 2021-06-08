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
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.mob.ShulkerEntity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class ShulkerEntityModel<T extends ShulkerEntity> extends CompositeEntityModel<T> {
	/**
	 * The key of the lid model part, whose value is {@value}.
	 */
	private static final String LID = "lid";
	/**
	 * The key of the base model part, whose value is {@value}.
	 */
	private static final String BASE = "base";
	private final ModelPart base;
	private final ModelPart lid;
	private final ModelPart head;

	public ShulkerEntityModel(ModelPart root) {
		super(RenderLayer::getEntityCutoutNoCullZOffset);
		this.lid = root.getChild("lid");
		this.base = root.getChild("base");
		this.head = root.getChild(EntityModelPartNames.HEAD);
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		modelPartData.addChild("lid", ModelPartBuilder.create().uv(0, 0).cuboid(-8.0F, -16.0F, -8.0F, 16.0F, 12.0F, 16.0F), ModelTransform.pivot(0.0F, 24.0F, 0.0F));
		modelPartData.addChild("base", ModelPartBuilder.create().uv(0, 28).cuboid(-8.0F, -8.0F, -8.0F, 16.0F, 8.0F, 16.0F), ModelTransform.pivot(0.0F, 24.0F, 0.0F));
		modelPartData.addChild(
			EntityModelPartNames.HEAD, ModelPartBuilder.create().uv(0, 52).cuboid(-3.0F, 0.0F, -3.0F, 6.0F, 6.0F, 6.0F), ModelTransform.pivot(0.0F, 12.0F, 0.0F)
		);
		return TexturedModelData.of(modelData, 64, 64);
	}

	public void setAngles(T shulkerEntity, float f, float g, float h, float i, float j) {
		float k = h - (float)shulkerEntity.age;
		float l = (0.5F + shulkerEntity.getOpenProgress(k)) * (float) Math.PI;
		float m = -1.0F + MathHelper.sin(l);
		float n = 0.0F;
		if (l > (float) Math.PI) {
			n = MathHelper.sin(h * 0.1F) * 0.7F;
		}

		this.lid.setPivot(0.0F, 16.0F + MathHelper.sin(l) * 8.0F + n, 0.0F);
		if (shulkerEntity.getOpenProgress(k) > 0.3F) {
			this.lid.yaw = m * m * m * m * (float) Math.PI * 0.125F;
		} else {
			this.lid.yaw = 0.0F;
		}

		this.head.pitch = j * (float) (Math.PI / 180.0);
		this.head.yaw = (shulkerEntity.headYaw - 180.0F - shulkerEntity.bodyYaw) * (float) (Math.PI / 180.0);
	}

	@Override
	public Iterable<ModelPart> getParts() {
		return ImmutableList.<ModelPart>of(this.base, this.lid);
	}

	public ModelPart getLid() {
		return this.lid;
	}

	public ModelPart getHead() {
		return this.head;
	}
}
