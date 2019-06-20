package net.minecraft;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Locale;
import java.util.UUID;
import java.util.function.Function;

public class class_3169 implements class_3162 {
	private static final SimpleCommandExceptionType field_13799 = new SimpleCommandExceptionType(new class_2588("commands.data.entity.invalid"));
	public static final Function<String, class_3164.class_3167> field_13800 = string -> new class_3164.class_3167() {
			@Override
			public class_3162 method_13924(CommandContext<class_2168> commandContext) throws CommandSyntaxException {
				return new class_3169(class_2186.method_9313(commandContext, string));
			}

			@Override
			public ArgumentBuilder<class_2168, ?> method_13925(
				ArgumentBuilder<class_2168, ?> argumentBuilder, Function<ArgumentBuilder<class_2168, ?>, ArgumentBuilder<class_2168, ?>> function
			) {
				return argumentBuilder.then(
					class_2170.method_9247("entity").then((ArgumentBuilder<class_2168, ?>)function.apply(class_2170.method_9244(string, class_2186.method_9309())))
				);
			}
		};
	private final class_1297 field_13801;

	public class_3169(class_1297 arg) {
		this.field_13801 = arg;
	}

	@Override
	public void method_13880(class_2487 arg) throws CommandSyntaxException {
		if (this.field_13801 instanceof class_1657) {
			throw field_13799.create();
		} else {
			UUID uUID = this.field_13801.method_5667();
			this.field_13801.method_5651(arg);
			this.field_13801.method_5826(uUID);
		}
	}

	@Override
	public class_2487 method_13881() {
		return class_2105.method_9076(this.field_13801);
	}

	@Override
	public class_2561 method_13883() {
		return new class_2588("commands.data.entity.modified", this.field_13801.method_5476());
	}

	@Override
	public class_2561 method_13882(class_2520 arg) {
		return new class_2588("commands.data.entity.query", this.field_13801.method_5476(), arg.method_10715());
	}

	@Override
	public class_2561 method_13879(class_2203.class_2209 arg, double d, int i) {
		return new class_2588("commands.data.entity.get", arg, this.field_13801.method_5476(), String.format(Locale.ROOT, "%.2f", d), i);
	}
}
