package net.minecraft.client.render.block.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.model.EntityModelPartNames;

@Environment(EnvType.CLIENT)
public class ChestBlockModel extends Model {
	private static final String BOTTOM = "bottom";
	private static final String LID = "lid";
	private static final String LOCK = "lock";
	private final ModelPart root;
	private final ModelPart lid;
	private final ModelPart lock;

	public ChestBlockModel(ModelPart root) {
		super(RenderLayer::getEntitySolid);
		this.root = root;
		this.lid = root.getChild("lid");
		this.lock = root.getChild("lock");
	}

	public static TexturedModelData getSingleTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		modelPartData.addChild(EntityModelPartNames.BOTTOM, ModelPartBuilder.create().uv(0, 19).cuboid(1.0F, 0.0F, 1.0F, 14.0F, 10.0F, 14.0F), ModelTransform.NONE);
		modelPartData.addChild("lid", ModelPartBuilder.create().uv(0, 0).cuboid(1.0F, 0.0F, 0.0F, 14.0F, 5.0F, 14.0F), ModelTransform.pivot(0.0F, 9.0F, 1.0F));
		modelPartData.addChild("lock", ModelPartBuilder.create().uv(0, 0).cuboid(7.0F, -2.0F, 14.0F, 2.0F, 4.0F, 1.0F), ModelTransform.pivot(0.0F, 9.0F, 1.0F));
		return TexturedModelData.of(modelData, 64, 64);
	}

	public static TexturedModelData getDoubleChestRightTexturedBlockData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		modelPartData.addChild(EntityModelPartNames.BOTTOM, ModelPartBuilder.create().uv(0, 19).cuboid(1.0F, 0.0F, 1.0F, 15.0F, 10.0F, 14.0F), ModelTransform.NONE);
		modelPartData.addChild("lid", ModelPartBuilder.create().uv(0, 0).cuboid(1.0F, 0.0F, 0.0F, 15.0F, 5.0F, 14.0F), ModelTransform.pivot(0.0F, 9.0F, 1.0F));
		modelPartData.addChild("lock", ModelPartBuilder.create().uv(0, 0).cuboid(15.0F, -2.0F, 14.0F, 1.0F, 4.0F, 1.0F), ModelTransform.pivot(0.0F, 9.0F, 1.0F));
		return TexturedModelData.of(modelData, 64, 64);
	}

	public static TexturedModelData getDoubleChestLeftTexturedBlockData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		modelPartData.addChild(EntityModelPartNames.BOTTOM, ModelPartBuilder.create().uv(0, 19).cuboid(0.0F, 0.0F, 1.0F, 15.0F, 10.0F, 14.0F), ModelTransform.NONE);
		modelPartData.addChild("lid", ModelPartBuilder.create().uv(0, 0).cuboid(0.0F, 0.0F, 0.0F, 15.0F, 5.0F, 14.0F), ModelTransform.pivot(0.0F, 9.0F, 1.0F));
		modelPartData.addChild("lock", ModelPartBuilder.create().uv(0, 0).cuboid(0.0F, -2.0F, 14.0F, 1.0F, 4.0F, 1.0F), ModelTransform.pivot(0.0F, 9.0F, 1.0F));
		return TexturedModelData.of(modelData, 64, 64);
	}

	public void setLockAndLidPitch(float animationProgress) {
		this.lid.pitch = -(animationProgress * (float) (Math.PI / 2));
		this.lock.pitch = this.lid.pitch;
	}

	@Override
	public ModelPart getPart() {
		return this.root;
	}
}
