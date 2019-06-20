package net.minecraft;

import java.lang.reflect.Constructor;
import java.util.Arrays;

public class class_1527<T extends class_1521> {
	private static class_1527<?>[] field_7080 = new class_1527[0];
	public static final class_1527<class_1517> field_7069 = method_6870(class_1517.class, "HoldingPattern");
	public static final class_1527<class_1525> field_7076 = method_6870(class_1525.class, "StrafePlayer");
	public static final class_1527<class_1519> field_7071 = method_6870(class_1519.class, "LandingApproach");
	public static final class_1527<class_1518> field_7067 = method_6870(class_1518.class, "Landing");
	public static final class_1527<class_1524> field_7077 = method_6870(class_1524.class, "Takeoff");
	public static final class_1527<class_1523> field_7072 = method_6870(class_1523.class, "SittingFlaming");
	public static final class_1527<class_1522> field_7081 = method_6870(class_1522.class, "SittingScanning");
	public static final class_1527<class_1520> field_7073 = method_6870(class_1520.class, "SittingAttacking");
	public static final class_1527<class_1513> field_7078 = method_6870(class_1513.class, "ChargingPlayer");
	public static final class_1527<class_1515> field_7068 = method_6870(class_1515.class, "Dying");
	public static final class_1527<class_1516> field_7075 = method_6870(class_1516.class, "Hover");
	private final Class<? extends class_1521> field_7074;
	private final int field_7079;
	private final String field_7070;

	private class_1527(int i, Class<? extends class_1521> class_, String string) {
		this.field_7079 = i;
		this.field_7074 = class_;
		this.field_7070 = string;
	}

	public class_1521 method_6866(class_1510 arg) {
		try {
			Constructor<? extends class_1521> constructor = this.method_6867();
			return (class_1521)constructor.newInstance(arg);
		} catch (Exception var3) {
			throw new Error(var3);
		}
	}

	protected Constructor<? extends class_1521> method_6867() throws NoSuchMethodException {
		return this.field_7074.getConstructor(class_1510.class);
	}

	public int method_6871() {
		return this.field_7079;
	}

	public String toString() {
		return this.field_7070 + " (#" + this.field_7079 + ")";
	}

	public static class_1527<?> method_6868(int i) {
		return i >= 0 && i < field_7080.length ? field_7080[i] : field_7069;
	}

	public static int method_6869() {
		return field_7080.length;
	}

	private static <T extends class_1521> class_1527<T> method_6870(Class<T> class_, String string) {
		class_1527<T> lv = new class_1527<>(field_7080.length, class_, string);
		field_7080 = (class_1527<?>[])Arrays.copyOf(field_7080, field_7080.length + 1);
		field_7080[lv.method_6871()] = lv;
		return lv;
	}
}
