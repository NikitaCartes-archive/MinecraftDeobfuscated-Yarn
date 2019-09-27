package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.Entity;

@Environment(EnvType.CLIENT)
public abstract class class_4595<E extends Entity> extends EntityModel<E> {
	@Override
	public void method_17116(class_4587 arg, class_4588 arg2, int i, float f, float g, float h) {
		this.method_22960().forEach(modelPart -> modelPart.method_22699(arg, arg2, 0.0625F, i, null, f, g, h));
	}

	public abstract Iterable<ModelPart> method_22960();
}
