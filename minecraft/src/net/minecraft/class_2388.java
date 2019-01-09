package net.minecraft;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2388 implements class_2394 {
	public static final class_2394.class_2395<class_2388> field_11181 = new class_2394.class_2395<class_2388>() {
		public class_2388 method_10279(class_2396<class_2388> arg, StringReader stringReader) throws CommandSyntaxException {
			stringReader.expect(' ');
			return new class_2388(arg, new class_2259(stringReader, false).method_9678(false).method_9669());
		}

		public class_2388 method_10280(class_2396<class_2388> arg, class_2540 arg2) {
			return new class_2388(arg, class_2248.field_10651.method_10200(arg2.method_10816()));
		}
	};
	private final class_2396<class_2388> field_11183;
	private final class_2680 field_11182;

	public class_2388(class_2396<class_2388> arg, class_2680 arg2) {
		this.field_11183 = arg;
		this.field_11182 = arg2;
	}

	@Override
	public void method_10294(class_2540 arg) {
		arg.method_10804(class_2248.field_10651.method_10206(this.field_11182));
	}

	@Override
	public String method_10293() {
		return class_2378.field_11141.method_10221(this.method_10295()) + " " + class_2259.method_9685(this.field_11182);
	}

	@Override
	public class_2396<class_2388> method_10295() {
		return this.field_11183;
	}

	@Environment(EnvType.CLIENT)
	public class_2680 method_10278() {
		return this.field_11182;
	}
}
