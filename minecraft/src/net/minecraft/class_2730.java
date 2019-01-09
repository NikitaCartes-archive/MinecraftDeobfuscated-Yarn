package net.minecraft;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2730 implements class_2596<class_2602> {
	private class_2730.class_2731 field_12448;
	private int field_12446;
	private double field_12443;
	private double field_12441;
	private double field_12449;
	private double field_12447;
	private long field_12445;
	private int field_12444;
	private int field_12442;

	public class_2730() {
	}

	public class_2730(class_2784 arg, class_2730.class_2731 arg2) {
		this.field_12448 = arg2;
		this.field_12443 = arg.method_11964();
		this.field_12441 = arg.method_11980();
		this.field_12447 = arg.method_11965();
		this.field_12449 = arg.method_11954();
		this.field_12445 = arg.method_11962();
		this.field_12446 = arg.method_11959();
		this.field_12442 = arg.method_11972();
		this.field_12444 = arg.method_11956();
	}

	@Override
	public void method_11053(class_2540 arg) throws IOException {
		this.field_12448 = arg.method_10818(class_2730.class_2731.class);
		switch (this.field_12448) {
			case field_12456:
				this.field_12449 = arg.readDouble();
				break;
			case field_12452:
				this.field_12447 = arg.readDouble();
				this.field_12449 = arg.readDouble();
				this.field_12445 = arg.method_10792();
				break;
			case field_12450:
				this.field_12443 = arg.readDouble();
				this.field_12441 = arg.readDouble();
				break;
			case field_12451:
				this.field_12442 = arg.method_10816();
				break;
			case field_12455:
				this.field_12444 = arg.method_10816();
				break;
			case field_12454:
				this.field_12443 = arg.readDouble();
				this.field_12441 = arg.readDouble();
				this.field_12447 = arg.readDouble();
				this.field_12449 = arg.readDouble();
				this.field_12445 = arg.method_10792();
				this.field_12446 = arg.method_10816();
				this.field_12442 = arg.method_10816();
				this.field_12444 = arg.method_10816();
		}
	}

	@Override
	public void method_11052(class_2540 arg) throws IOException {
		arg.method_10817(this.field_12448);
		switch (this.field_12448) {
			case field_12456:
				arg.writeDouble(this.field_12449);
				break;
			case field_12452:
				arg.writeDouble(this.field_12447);
				arg.writeDouble(this.field_12449);
				arg.method_10791(this.field_12445);
				break;
			case field_12450:
				arg.writeDouble(this.field_12443);
				arg.writeDouble(this.field_12441);
				break;
			case field_12451:
				arg.method_10804(this.field_12442);
				break;
			case field_12455:
				arg.method_10804(this.field_12444);
				break;
			case field_12454:
				arg.writeDouble(this.field_12443);
				arg.writeDouble(this.field_12441);
				arg.writeDouble(this.field_12447);
				arg.writeDouble(this.field_12449);
				arg.method_10791(this.field_12445);
				arg.method_10804(this.field_12446);
				arg.method_10804(this.field_12442);
				arg.method_10804(this.field_12444);
		}
	}

	public void method_11796(class_2602 arg) {
		arg.method_11096(this);
	}

	@Environment(EnvType.CLIENT)
	public void method_11795(class_2784 arg) {
		switch (this.field_12448) {
			case field_12456:
				arg.method_11969(this.field_12449);
				break;
			case field_12452:
				arg.method_11957(this.field_12447, this.field_12449, this.field_12445);
				break;
			case field_12450:
				arg.method_11978(this.field_12443, this.field_12441);
				break;
			case field_12451:
				arg.method_11967(this.field_12442);
				break;
			case field_12455:
				arg.method_11975(this.field_12444);
				break;
			case field_12454:
				arg.method_11978(this.field_12443, this.field_12441);
				if (this.field_12445 > 0L) {
					arg.method_11957(this.field_12447, this.field_12449, this.field_12445);
				} else {
					arg.method_11969(this.field_12449);
				}

				arg.method_11973(this.field_12446);
				arg.method_11967(this.field_12442);
				arg.method_11975(this.field_12444);
		}
	}

	public static enum class_2731 {
		field_12456,
		field_12452,
		field_12450,
		field_12454,
		field_12455,
		field_12451;
	}
}
