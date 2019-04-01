package net.minecraft;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

public interface class_2394 {
	class_2396<?> method_10295();

	void method_10294(class_2540 arg);

	String method_10293();

	public interface class_2395<T extends class_2394> {
		T method_10296(class_2396<T> arg, StringReader stringReader) throws CommandSyntaxException;

		T method_10297(class_2396<T> arg, class_2540 arg2);
	}
}
