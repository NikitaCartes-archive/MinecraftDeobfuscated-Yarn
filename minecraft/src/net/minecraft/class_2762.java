package net.minecraft;

import java.io.IOException;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2762 implements class_2596<class_2602> {
	private class_2762.class_2763 field_12625;
	private class_2561 field_12626;
	private int field_12624;
	private int field_12623;
	private int field_12622;

	public class_2762() {
	}

	public class_2762(class_2762.class_2763 arg, class_2561 arg2) {
		this(arg, arg2, -1, -1, -1);
	}

	public class_2762(int i, int j, int k) {
		this(class_2762.class_2763.field_12629, null, i, j, k);
	}

	public class_2762(class_2762.class_2763 arg, @Nullable class_2561 arg2, int i, int j, int k) {
		this.field_12625 = arg;
		this.field_12626 = arg2;
		this.field_12624 = i;
		this.field_12623 = j;
		this.field_12622 = k;
	}

	@Override
	public void method_11053(class_2540 arg) throws IOException {
		this.field_12625 = arg.method_10818(class_2762.class_2763.class);
		if (this.field_12625 == class_2762.class_2763.field_12630
			|| this.field_12625 == class_2762.class_2763.field_12632
			|| this.field_12625 == class_2762.class_2763.field_12627) {
			this.field_12626 = arg.method_10808();
		}

		if (this.field_12625 == class_2762.class_2763.field_12629) {
			this.field_12624 = arg.readInt();
			this.field_12623 = arg.readInt();
			this.field_12622 = arg.readInt();
		}
	}

	@Override
	public void method_11052(class_2540 arg) throws IOException {
		arg.method_10817(this.field_12625);
		if (this.field_12625 == class_2762.class_2763.field_12630
			|| this.field_12625 == class_2762.class_2763.field_12632
			|| this.field_12625 == class_2762.class_2763.field_12627) {
			arg.method_10805(this.field_12626);
		}

		if (this.field_12625 == class_2762.class_2763.field_12629) {
			arg.writeInt(this.field_12624);
			arg.writeInt(this.field_12623);
			arg.writeInt(this.field_12622);
		}
	}

	public void method_11879(class_2602 arg) {
		arg.method_11103(this);
	}

	@Environment(EnvType.CLIENT)
	public class_2762.class_2763 method_11878() {
		return this.field_12625;
	}

	@Environment(EnvType.CLIENT)
	public class_2561 method_11877() {
		return this.field_12626;
	}

	@Environment(EnvType.CLIENT)
	public int method_11874() {
		return this.field_12624;
	}

	@Environment(EnvType.CLIENT)
	public int method_11876() {
		return this.field_12623;
	}

	@Environment(EnvType.CLIENT)
	public int method_11875() {
		return this.field_12622;
	}

	public static enum class_2763 {
		field_12630,
		field_12632,
		field_12627,
		field_12629,
		field_12633,
		field_12628;
	}
}
