package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_345 extends class_1259 {
	protected float field_2129;
	protected long field_2128;

	public class_345(class_2629 arg) {
		super(arg.method_11322(), arg.method_11328(), arg.method_11325(), arg.method_11329());
		this.field_2129 = arg.method_11327();
		this.field_5774 = arg.method_11327();
		this.field_2128 = class_156.method_658();
		this.method_5406(arg.method_11331());
		this.method_5410(arg.method_11321());
		this.method_5411(arg.method_11332());
	}

	@Override
	public void method_5408(float f) {
		this.field_5774 = this.method_5412();
		this.field_2129 = f;
		this.field_2128 = class_156.method_658();
	}

	@Override
	public float method_5412() {
		long l = class_156.method_658() - this.field_2128;
		float f = class_3532.method_15363((float)l / 100.0F, 0.0F, 1.0F);
		return class_3532.method_16439(f, this.field_5774, this.field_2129);
	}

	public void method_1894(class_2629 arg) {
		switch (arg.method_11324()) {
			case field_12084:
				this.method_5413(arg.method_11328());
				break;
			case field_12080:
				this.method_5408(arg.method_11327());
				break;
			case field_12081:
				this.method_5416(arg.method_11325());
				this.method_5409(arg.method_11329());
				break;
			case field_12083:
				this.method_5406(arg.method_11331());
				this.method_5410(arg.method_11321());
		}
	}
}
