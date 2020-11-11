package net.minecraft.client.render.entity.model;

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
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;

@Environment(EnvType.CLIENT)
public class ShieldEntityModel extends Model {
	private final ModelPart field_27495;
	private final ModelPart plate;
	private final ModelPart handle;

	public ShieldEntityModel(ModelPart modelPart) {
		super(RenderLayer::getEntitySolid);
		this.field_27495 = modelPart;
		this.plate = modelPart.getChild("plate");
		this.handle = modelPart.getChild("handle");
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		modelPartData.addChild("plate", ModelPartBuilder.create().uv(0, 0).cuboid(-6.0F, -11.0F, -2.0F, 12.0F, 22.0F, 1.0F), ModelTransform.NONE);
		modelPartData.addChild("handle", ModelPartBuilder.create().uv(26, 0).cuboid(-1.0F, -3.0F, -1.0F, 2.0F, 6.0F, 6.0F), ModelTransform.NONE);
		return TexturedModelData.of(modelData, 64, 64);
	}

	public ModelPart getPlate() {
		return this.plate;
	}

	public ModelPart getHandle() {
		return this.handle;
	}

	@Override
	public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
		this.field_27495.render(matrices, vertices, light, overlay, red, green, blue, alpha);
	}
}
