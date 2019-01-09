package net.minecraft;

import java.io.IOException;
import java.util.Objects;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2736 implements class_2596<class_2602> {
	private int field_12464;
	private String field_12465;

	public class_2736() {
	}

	public class_2736(int i, @Nullable class_266 arg) {
		this.field_12464 = i;
		if (arg == null) {
			this.field_12465 = "";
		} else {
			this.field_12465 = arg.method_1113();
		}
	}

	@Override
	public void method_11053(class_2540 arg) throws IOException {
		this.field_12464 = arg.readByte();
		this.field_12465 = arg.method_10800(16);
	}

	@Override
	public void method_11052(class_2540 arg) throws IOException {
		arg.writeByte(this.field_12464);
		arg.method_10814(this.field_12465);
	}

	public void method_11805(class_2602 arg) {
		arg.method_11159(this);
	}

	@Environment(EnvType.CLIENT)
	public int method_11806() {
		return this.field_12464;
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	public String method_11804() {
		return Objects.equals(this.field_12465, "") ? null : this.field_12465;
	}
}
