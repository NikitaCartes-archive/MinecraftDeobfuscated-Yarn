package net.minecraft;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2622 implements class_2596<class_2602> {
	private class_2338 field_12040;
	private int field_12038;
	private class_2487 field_12039;

	public class_2622() {
	}

	public class_2622(class_2338 arg, int i, class_2487 arg2) {
		this.field_12040 = arg;
		this.field_12038 = i;
		this.field_12039 = arg2;
	}

	@Override
	public void method_11053(class_2540 arg) throws IOException {
		this.field_12040 = arg.method_10811();
		this.field_12038 = arg.readUnsignedByte();
		this.field_12039 = arg.method_10798();
	}

	@Override
	public void method_11052(class_2540 arg) throws IOException {
		arg.method_10807(this.field_12040);
		arg.writeByte((byte)this.field_12038);
		arg.method_10794(this.field_12039);
	}

	public void method_11292(class_2602 arg) {
		arg.method_11094(this);
	}

	@Environment(EnvType.CLIENT)
	public class_2338 method_11293() {
		return this.field_12040;
	}

	@Environment(EnvType.CLIENT)
	public int method_11291() {
		return this.field_12038;
	}

	@Environment(EnvType.CLIENT)
	public class_2487 method_11290() {
		return this.field_12039;
	}
}
