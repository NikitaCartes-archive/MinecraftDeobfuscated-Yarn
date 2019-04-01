package net.minecraft;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2761 implements class_2596<class_2602> {
	private long field_12621;
	private long field_12620;

	public class_2761() {
	}

	public class_2761(long l, long m, boolean bl) {
		this.field_12621 = l;
		this.field_12620 = m;
		if (!bl) {
			this.field_12620 = -this.field_12620;
			if (this.field_12620 == 0L) {
				this.field_12620 = -1L;
			}
		}
	}

	@Override
	public void method_11053(class_2540 arg) throws IOException {
		this.field_12621 = arg.readLong();
		this.field_12620 = arg.readLong();
	}

	@Override
	public void method_11052(class_2540 arg) throws IOException {
		arg.writeLong(this.field_12621);
		arg.writeLong(this.field_12620);
	}

	public void method_11872(class_2602 arg) {
		arg.method_11079(this);
	}

	@Environment(EnvType.CLIENT)
	public long method_11871() {
		return this.field_12621;
	}

	@Environment(EnvType.CLIENT)
	public long method_11873() {
		return this.field_12620;
	}
}
