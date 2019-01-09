package net.minecraft;

public class class_2600 {
	public static <T extends class_2547> void method_11073(class_2596<T> arg, T arg2, class_3218 arg3) throws class_2987 {
		method_11074(arg, arg2, arg3.method_8503());
	}

	public static <T extends class_2547> void method_11074(class_2596<T> arg, T arg2, class_1255<?> arg3) throws class_2987 {
		if (!arg3.method_5387()) {
			arg3.execute(() -> arg.method_11054(arg2));
			throw class_2987.field_13400;
		}
	}
}
