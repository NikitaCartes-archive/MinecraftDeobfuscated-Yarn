package net.minecraft;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2392 implements class_2394 {
	public static final class_2394.class_2395<class_2392> field_11191 = new class_2394.class_2395<class_2392>() {
		public class_2392 method_10290(class_2396<class_2392> arg, StringReader stringReader) throws CommandSyntaxException {
			stringReader.expect(' ');
			class_2291 lv = new class_2291(stringReader, false).method_9789();
			class_1799 lv2 = new class_2290(lv.method_9786(), lv.method_9797()).method_9781(1, false);
			return new class_2392(arg, lv2);
		}

		public class_2392 method_10291(class_2396<class_2392> arg, class_2540 arg2) {
			return new class_2392(arg, arg2.method_10819());
		}
	};
	private final class_2396<class_2392> field_11193;
	private final class_1799 field_11192;

	public class_2392(class_2396<class_2392> arg, class_1799 arg2) {
		this.field_11193 = arg;
		this.field_11192 = arg2;
	}

	@Override
	public void method_10294(class_2540 arg) {
		arg.method_10793(this.field_11192);
	}

	@Override
	public String method_10293() {
		return class_2378.field_11141.method_10221(this.method_10295())
			+ " "
			+ new class_2290(this.field_11192.method_7909(), this.field_11192.method_7969()).method_9782();
	}

	@Override
	public class_2396<class_2392> method_10295() {
		return this.field_11193;
	}

	@Environment(EnvType.CLIENT)
	public class_1799 method_10289() {
		return this.field_11192;
	}
}
