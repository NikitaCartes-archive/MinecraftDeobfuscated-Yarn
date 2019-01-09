package net.minecraft;

public class class_2603 extends class_2586 implements class_3000 {
	public class_2603() {
		super(class_2591.field_11900);
	}

	@Override
	public void method_16896() {
		if (this.field_11863 != null && !this.field_11863.field_9236 && this.field_11863.method_8510() % 20L == 0L) {
			class_2680 lv = this.method_11010();
			class_2248 lv2 = lv.method_11614();
			if (lv2 instanceof class_2309) {
				class_2309.method_9983(lv, this.field_11863, this.field_11867);
			}
		}
	}
}
