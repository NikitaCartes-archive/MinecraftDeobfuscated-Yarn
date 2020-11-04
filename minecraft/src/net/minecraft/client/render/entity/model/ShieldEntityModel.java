package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5603;
import net.minecraft.class_5606;
import net.minecraft.class_5607;
import net.minecraft.class_5609;
import net.minecraft.class_5610;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelPart;
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
		this.plate = modelPart.method_32086("plate");
		this.handle = modelPart.method_32086("handle");
	}

	public static class_5607 method_32039() {
		class_5609 lv = new class_5609();
		class_5610 lv2 = lv.method_32111();
		lv2.method_32117("plate", class_5606.method_32108().method_32101(0, 0).method_32097(-6.0F, -11.0F, -2.0F, 12.0F, 22.0F, 1.0F), class_5603.field_27701);
		lv2.method_32117("handle", class_5606.method_32108().method_32101(26, 0).method_32097(-1.0F, -3.0F, -1.0F, 2.0F, 6.0F, 6.0F), class_5603.field_27701);
		return class_5607.method_32110(lv, 64, 64);
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
