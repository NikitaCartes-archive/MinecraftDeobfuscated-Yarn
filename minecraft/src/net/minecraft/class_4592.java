package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.Entity;

@Environment(EnvType.CLIENT)
public abstract class class_4592<E extends Entity> extends EntityModel<E> {
	private final boolean field_20915;
	private final float field_20916;
	private final float field_20917;
	private final float field_20918;
	private final float field_20919;
	private final float field_20920;

	protected class_4592(boolean bl, float f, float g) {
		this(bl, f, g, 2.0F, 2.0F, 24.0F);
	}

	protected class_4592(boolean bl, float f, float g, float h, float i, float j) {
		this.field_20915 = bl;
		this.field_20916 = f;
		this.field_20917 = g;
		this.field_20918 = h;
		this.field_20919 = i;
		this.field_20920 = j;
	}

	protected class_4592() {
		this(false, 5.0F, 2.0F);
	}

	@Override
	public void method_17116(class_4587 arg, class_4588 arg2, int i, float f, float g, float h) {
		if (this.isChild) {
			arg.method_22903();
			if (this.field_20915) {
				float j = 1.5F / this.field_20918;
				arg.method_22905(j, j, j);
			}

			arg.method_22904(0.0, (double)(this.field_20916 / 16.0F), (double)(this.field_20917 / 16.0F));
			this.method_22946().forEach(modelPart -> modelPart.method_22699(arg, arg2, 0.0625F, i, null, f, g, h));
			arg.method_22909();
			arg.method_22903();
			float j = 1.0F / this.field_20919;
			arg.method_22905(j, j, j);
			arg.method_22904(0.0, (double)(this.field_20920 / 16.0F), 0.0);
			this.method_22948().forEach(modelPart -> modelPart.method_22699(arg, arg2, 0.0625F, i, null, f, g, h));
			arg.method_22909();
		} else {
			this.method_22946().forEach(modelPart -> modelPart.method_22699(arg, arg2, 0.0625F, i, null, f, g, h));
			this.method_22948().forEach(modelPart -> modelPart.method_22699(arg, arg2, 0.0625F, i, null, f, g, h));
		}
	}

	protected abstract Iterable<ModelPart> method_22946();

	protected abstract Iterable<ModelPart> method_22948();
}
