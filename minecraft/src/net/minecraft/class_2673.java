package net.minecraft;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2673 implements class_2596<class_2602> {
	private int field_12241;
	private class_2338 field_12242;
	private int field_12239;
	private boolean field_12240;

	public class_2673() {
	}

	public class_2673(int i, class_2338 arg, int j, boolean bl) {
		this.field_12241 = i;
		this.field_12242 = arg.method_10062();
		this.field_12239 = j;
		this.field_12240 = bl;
	}

	@Override
	public void method_11053(class_2540 arg) throws IOException {
		this.field_12241 = arg.readInt();
		this.field_12242 = arg.method_10811();
		this.field_12239 = arg.readInt();
		this.field_12240 = arg.readBoolean();
	}

	@Override
	public void method_11052(class_2540 arg) throws IOException {
		arg.writeInt(this.field_12241);
		arg.method_10807(this.field_12242);
		arg.writeInt(this.field_12239);
		arg.writeBoolean(this.field_12240);
	}

	public void method_11535(class_2602 arg) {
		arg.method_11098(this);
	}

	@Environment(EnvType.CLIENT)
	public boolean method_11533() {
		return this.field_12240;
	}

	@Environment(EnvType.CLIENT)
	public int method_11532() {
		return this.field_12241;
	}

	@Environment(EnvType.CLIENT)
	public int method_11534() {
		return this.field_12239;
	}

	@Environment(EnvType.CLIENT)
	public class_2338 method_11531() {
		return this.field_12242;
	}
}
