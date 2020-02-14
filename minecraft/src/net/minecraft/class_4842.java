package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.BipedEntityRenderer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class class_4842 extends BipedEntityRenderer<class_4836, class_4840<class_4836>> {
	private static final Identifier field_22410 = new Identifier("textures/entity/piglin/piglin.png");

	public class_4842(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new class_4840<>(0.0F, 128, 64), 0.5F);
		this.addFeature(new class_4843<>(this, new BipedEntityModel(0.5F), new BipedEntityModel(1.0F), method_24813()));
	}

	private static <T extends class_4836> class_4840<T> method_24813() {
		class_4840<T> lv = new class_4840<>(1.0F, 64, 16);
		lv.field_22405.visible = false;
		lv.field_22404.visible = false;
		return lv;
	}

	public Identifier getTexture(class_4836 arg) {
		return field_22410;
	}

	protected void setupTransforms(class_4836 arg, MatrixStack matrixStack, float f, float g, float h) {
		if (arg.method_24704()) {
			g += (float)(Math.cos((double)arg.age * 3.25) * Math.PI * 0.5);
		}

		super.setupTransforms(arg, matrixStack, f, g, h);
	}
}
