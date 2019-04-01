package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_552 extends class_3879 {
	private final class_630 field_3316;
	private final class_630 field_3318;
	private final class_630[] field_3317 = new class_630[4];

	public class_552() {
		this.field_17138 = 64;
		this.field_17139 = 64;
		this.field_3316 = new class_630(this, 0, 0);
		this.field_3316.method_2856(0.0F, 0.0F, 0.0F, 16, 16, 6, 0.0F);
		this.field_3318 = new class_630(this, 0, 22);
		this.field_3318.method_2856(0.0F, 0.0F, 0.0F, 16, 16, 6, 0.0F);
		this.field_3317[0] = new class_630(this, 50, 0);
		this.field_3317[1] = new class_630(this, 50, 6);
		this.field_3317[2] = new class_630(this, 50, 12);
		this.field_3317[3] = new class_630(this, 50, 18);
		this.field_3317[0].method_2844(0.0F, 6.0F, -16.0F, 3, 3, 3);
		this.field_3317[1].method_2844(0.0F, 6.0F, 0.0F, 3, 3, 3);
		this.field_3317[2].method_2844(-16.0F, 6.0F, -16.0F, 3, 3, 3);
		this.field_3317[3].method_2844(-16.0F, 6.0F, 0.0F, 3, 3, 3);
		this.field_3317[0].field_3654 = (float) (Math.PI / 2);
		this.field_3317[1].field_3654 = (float) (Math.PI / 2);
		this.field_3317[2].field_3654 = (float) (Math.PI / 2);
		this.field_3317[3].field_3654 = (float) (Math.PI / 2);
		this.field_3317[0].field_3674 = 0.0F;
		this.field_3317[1].field_3674 = (float) (Math.PI / 2);
		this.field_3317[2].field_3674 = (float) (Math.PI * 3.0 / 2.0);
		this.field_3317[3].field_3674 = (float) Math.PI;
	}

	public void method_2794() {
		this.field_3316.method_2846(0.0625F);
		this.field_3318.method_2846(0.0625F);
		this.field_3317[0].method_2846(0.0625F);
		this.field_3317[1].method_2846(0.0625F);
		this.field_3317[2].method_2846(0.0625F);
		this.field_3317[3].method_2846(0.0625F);
	}

	public void method_2795(boolean bl) {
		this.field_3316.field_3665 = bl;
		this.field_3318.field_3665 = !bl;
		this.field_3317[0].field_3665 = !bl;
		this.field_3317[1].field_3665 = bl;
		this.field_3317[2].field_3665 = !bl;
		this.field_3317[3].field_3665 = bl;
	}
}
