package net.minecraft;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

public class class_2400 extends class_2396<class_2400> implements class_2394 {
	private static final class_2394.class_2395<class_2400> field_11259 = new class_2394.class_2395<class_2400>() {
		public class_2400 method_10307(class_2396<class_2400> arg, StringReader stringReader) throws CommandSyntaxException {
			return (class_2400)arg;
		}

		public class_2400 method_10306(class_2396<class_2400> arg, class_2540 arg2) {
			return (class_2400)arg;
		}
	};

	protected class_2400(boolean bl) {
		super(bl, field_11259);
	}

	@Override
	public class_2396<class_2400> method_10295() {
		return this;
	}

	@Override
	public void method_10294(class_2540 arg) {
	}

	@Override
	public String method_10293() {
		return class_2378.field_11141.method_10221(this).toString();
	}
}
