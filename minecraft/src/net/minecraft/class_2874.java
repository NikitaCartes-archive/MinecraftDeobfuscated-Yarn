package net.minecraft;

import java.io.File;
import java.util.function.BiFunction;
import javax.annotation.Nullable;

public class class_2874 {
	public static final class_2874 field_13072 = method_12486("overworld", new class_2874(1, "", "", class_2878::new, true));
	public static final class_2874 field_13076 = method_12486("the_nether", new class_2874(0, "_nether", "DIM-1", class_2872::new, false));
	public static final class_2874 field_13078 = method_12486("the_end", new class_2874(2, "_end", "DIM1", class_2880::new, false));
	private final int field_13074;
	private final String field_13077;
	private final String field_13079;
	private final BiFunction<class_1937, class_2874, ? extends class_2869> field_13075;
	private final boolean field_13073;

	private static class_2874 method_12486(String string, class_2874 arg) {
		return class_2378.method_10231(class_2378.field_11155, arg.field_13074, string, arg);
	}

	protected class_2874(int i, String string, String string2, BiFunction<class_1937, class_2874, ? extends class_2869> biFunction, boolean bl) {
		this.field_13074 = i;
		this.field_13077 = string;
		this.field_13079 = string2;
		this.field_13075 = biFunction;
		this.field_13073 = bl;
	}

	public static Iterable<class_2874> method_12482() {
		return class_2378.field_11155;
	}

	public int method_12484() {
		return this.field_13074 + -1;
	}

	public String method_12489() {
		return this.field_13077;
	}

	public File method_12488(File file) {
		return this.field_13079.isEmpty() ? file : new File(file, this.field_13079);
	}

	public class_2869 method_12487(class_1937 arg) {
		return (class_2869)this.field_13075.apply(arg, this);
	}

	public String toString() {
		return method_12485(this).toString();
	}

	@Nullable
	public static class_2874 method_12490(int i) {
		return class_2378.field_11155.method_10200(i - -1);
	}

	@Nullable
	public static class_2874 method_12483(class_2960 arg) {
		return class_2378.field_11155.method_10223(arg);
	}

	@Nullable
	public static class_2960 method_12485(class_2874 arg) {
		return class_2378.field_11155.method_10221(arg);
	}

	public boolean method_12491() {
		return this.field_13073;
	}
}
