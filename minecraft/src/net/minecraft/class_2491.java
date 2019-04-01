package net.minecraft;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class class_2491 implements class_2520 {
	@Override
	public void method_10709(DataInput dataInput, int i, class_2505 arg) throws IOException {
		arg.method_10623(64L);
	}

	@Override
	public void method_10713(DataOutput dataOutput) throws IOException {
	}

	@Override
	public byte method_10711() {
		return 0;
	}

	@Override
	public String toString() {
		return "END";
	}

	public class_2491 method_10586() {
		return new class_2491();
	}

	@Override
	public class_2561 method_10710(String string, int i) {
		return new class_2585("");
	}

	public boolean equals(Object object) {
		return object instanceof class_2491;
	}

	public int hashCode() {
		return this.method_10711();
	}
}
