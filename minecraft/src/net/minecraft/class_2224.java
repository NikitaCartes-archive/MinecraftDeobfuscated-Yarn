package net.minecraft;

import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Arrays;
import java.util.Collection;

public interface class_2224<T extends class_2096<?>> extends ArgumentType<T> {
	static class_2224.class_2227 method_9422() {
		return new class_2224.class_2227();
	}

	public static class class_2225 implements class_2224<class_2096.class_2099> {
		private static final Collection<String> field_9937 = Arrays.asList("0..5.2", "0", "-5.4", "-100.76..", "..100");

		public class_2096.class_2099 method_9423(StringReader stringReader) throws CommandSyntaxException {
			return class_2096.class_2099.method_9049(stringReader);
		}

		@Override
		public Collection<String> getExamples() {
			return field_9937;
		}

		public static class class_2226 extends class_2224.class_2229<class_2224.class_2225> {
			public class_2224.class_2225 method_9424(class_2540 arg) {
				return new class_2224.class_2225();
			}
		}
	}

	public static class class_2227 implements class_2224<class_2096.class_2100> {
		private static final Collection<String> field_9938 = Arrays.asList("0..5", "0", "-5", "-100..", "..100");

		public static class_2096.class_2100 method_9425(CommandContext<class_2168> commandContext, String string) {
			return commandContext.getArgument(string, class_2096.class_2100.class);
		}

		public class_2096.class_2100 method_9426(StringReader stringReader) throws CommandSyntaxException {
			return class_2096.class_2100.method_9060(stringReader);
		}

		@Override
		public Collection<String> getExamples() {
			return field_9938;
		}

		public static class class_2228 extends class_2224.class_2229<class_2224.class_2227> {
			public class_2224.class_2227 method_9427(class_2540 arg) {
				return new class_2224.class_2227();
			}
		}
	}

	public abstract static class class_2229<T extends class_2224<?>> implements class_2314<T> {
		public void method_9429(T arg, class_2540 arg2) {
		}

		public void method_9428(T arg, JsonObject jsonObject) {
		}
	}
}
