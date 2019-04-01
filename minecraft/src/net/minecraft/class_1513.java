package net.minecraft;

import javax.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_1513 extends class_1512 {
	private static final Logger field_7039 = LogManager.getLogger();
	private class_243 field_7038;
	private int field_7037;

	public class_1513(class_1510 arg) {
		super(arg);
	}

	@Override
	public void method_6855() {
		if (this.field_7038 == null) {
			field_7039.warn("Aborting charge player as no target was set.");
			this.field_7036.method_6831().method_6863(class_1527.field_7069);
		} else if (this.field_7037 > 0 && this.field_7037++ >= 10) {
			this.field_7036.method_6831().method_6863(class_1527.field_7069);
		} else {
			double d = this.field_7038.method_1028(this.field_7036.field_5987, this.field_7036.field_6010, this.field_7036.field_6035);
			if (d < 100.0 || d > 22500.0 || this.field_7036.field_5976 || this.field_7036.field_5992) {
				this.field_7037++;
			}
		}
	}

	@Override
	public void method_6856() {
		this.field_7038 = null;
		this.field_7037 = 0;
	}

	public void method_6840(class_243 arg) {
		this.field_7038 = arg;
	}

	@Override
	public float method_6846() {
		return 3.0F;
	}

	@Nullable
	@Override
	public class_243 method_6851() {
		return this.field_7038;
	}

	@Override
	public class_1527<class_1513> method_6849() {
		return class_1527.field_7078;
	}
}
