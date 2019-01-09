package net.minecraft;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Locale;
import java.util.function.Function;

public class class_3161 implements class_3162 {
	private static final SimpleCommandExceptionType field_13785 = new SimpleCommandExceptionType(new class_2588("commands.data.block.invalid"));
	public static final Function<String, class_3164.class_3167> field_13786 = string -> new class_3164.class_3167() {
			@Override
			public class_3162 method_13924(CommandContext<class_2168> commandContext) throws CommandSyntaxException {
				class_2338 lv = class_2262.method_9696(commandContext, string + "Pos");
				class_2586 lv2 = ((class_2168)commandContext.getSource()).method_9225().method_8321(lv);
				if (lv2 == null) {
					throw class_3161.field_13785.create();
				} else {
					return new class_3161(lv2, lv);
				}
			}

			@Override
			public ArgumentBuilder<class_2168, ?> method_13925(
				ArgumentBuilder<class_2168, ?> argumentBuilder, Function<ArgumentBuilder<class_2168, ?>, ArgumentBuilder<class_2168, ?>> function
			) {
				return argumentBuilder.then(
					class_2170.method_9247("block").then((ArgumentBuilder<class_2168, ?>)function.apply(class_2170.method_9244(string + "Pos", class_2262.method_9698())))
				);
			}
		};
	private final class_2586 field_13784;
	private final class_2338 field_13783;

	public class_3161(class_2586 arg, class_2338 arg2) {
		this.field_13784 = arg;
		this.field_13783 = arg2;
	}

	@Override
	public void method_13880(class_2487 arg) {
		arg.method_10569("x", this.field_13783.method_10263());
		arg.method_10569("y", this.field_13783.method_10264());
		arg.method_10569("z", this.field_13783.method_10260());
		this.field_13784.method_11014(arg);
		this.field_13784.method_5431();
		class_2680 lv = this.field_13784.method_10997().method_8320(this.field_13783);
		this.field_13784.method_10997().method_8413(this.field_13783, lv, lv, 3);
	}

	@Override
	public class_2487 method_13881() {
		return this.field_13784.method_11007(new class_2487());
	}

	@Override
	public class_2561 method_13883() {
		return new class_2588("commands.data.block.modified", this.field_13783.method_10263(), this.field_13783.method_10264(), this.field_13783.method_10260());
	}

	@Override
	public class_2561 method_13882(class_2520 arg) {
		return new class_2588(
			"commands.data.block.query", this.field_13783.method_10263(), this.field_13783.method_10264(), this.field_13783.method_10260(), arg.method_10715()
		);
	}

	@Override
	public class_2561 method_13879(class_2203.class_2209 arg, double d, int i) {
		return new class_2588(
			"commands.data.block.get",
			arg,
			this.field_13783.method_10263(),
			this.field_13783.method_10264(),
			this.field_13783.method_10260(),
			String.format(Locale.ROOT, "%.2f", d),
			i
		);
	}
}
