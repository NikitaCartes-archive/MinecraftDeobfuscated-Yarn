package net.minecraft;

import com.google.common.collect.Lists;
import com.mojang.brigadier.ResultConsumer;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.function.BinaryOperator;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.server.MinecraftServer;

public class class_2168 implements class_2172 {
	public static final SimpleCommandExceptionType field_9824 = new SimpleCommandExceptionType(new class_2588("permissions.requires.player"));
	public static final SimpleCommandExceptionType field_9827 = new SimpleCommandExceptionType(new class_2588("permissions.requires.entity"));
	private final class_2165 field_9819;
	private final class_243 field_9817;
	private final class_3218 field_9828;
	private final int field_9815;
	private final String field_9826;
	private final class_2561 field_9825;
	private final MinecraftServer field_9818;
	private final boolean field_9823;
	@Nullable
	private final class_1297 field_9820;
	private final ResultConsumer<class_2168> field_9821;
	private final class_2183.class_2184 field_9816;
	private final class_241 field_9822;

	public class_2168(
		class_2165 arg,
		class_243 arg2,
		class_241 arg3,
		class_3218 arg4,
		int i,
		String string,
		class_2561 arg5,
		MinecraftServer minecraftServer,
		@Nullable class_1297 arg6
	) {
		this(arg, arg2, arg3, arg4, i, string, arg5, minecraftServer, arg6, false, (commandContext, bl, ix) -> {
		}, class_2183.class_2184.field_9853);
	}

	protected class_2168(
		class_2165 arg,
		class_243 arg2,
		class_241 arg3,
		class_3218 arg4,
		int i,
		String string,
		class_2561 arg5,
		MinecraftServer minecraftServer,
		@Nullable class_1297 arg6,
		boolean bl,
		ResultConsumer<class_2168> resultConsumer,
		class_2183.class_2184 arg7
	) {
		this.field_9819 = arg;
		this.field_9817 = arg2;
		this.field_9828 = arg4;
		this.field_9823 = bl;
		this.field_9820 = arg6;
		this.field_9815 = i;
		this.field_9826 = string;
		this.field_9825 = arg5;
		this.field_9818 = minecraftServer;
		this.field_9821 = resultConsumer;
		this.field_9816 = arg7;
		this.field_9822 = arg3;
	}

	public class_2168 method_9232(class_1297 arg) {
		return this.field_9820 == arg
			? this
			: new class_2168(
				this.field_9819,
				this.field_9817,
				this.field_9822,
				this.field_9828,
				this.field_9815,
				arg.method_5477().getString(),
				arg.method_5476(),
				this.field_9818,
				arg,
				this.field_9823,
				this.field_9821,
				this.field_9816
			);
	}

	public class_2168 method_9208(class_243 arg) {
		return this.field_9817.equals(arg)
			? this
			: new class_2168(
				this.field_9819,
				arg,
				this.field_9822,
				this.field_9828,
				this.field_9815,
				this.field_9826,
				this.field_9825,
				this.field_9818,
				this.field_9820,
				this.field_9823,
				this.field_9821,
				this.field_9816
			);
	}

	public class_2168 method_9216(class_241 arg) {
		return this.field_9822.method_1016(arg)
			? this
			: new class_2168(
				this.field_9819,
				this.field_9817,
				arg,
				this.field_9828,
				this.field_9815,
				this.field_9826,
				this.field_9825,
				this.field_9818,
				this.field_9820,
				this.field_9823,
				this.field_9821,
				this.field_9816
			);
	}

	public class_2168 method_9231(ResultConsumer<class_2168> resultConsumer) {
		return this.field_9821.equals(resultConsumer)
			? this
			: new class_2168(
				this.field_9819,
				this.field_9817,
				this.field_9822,
				this.field_9828,
				this.field_9815,
				this.field_9826,
				this.field_9825,
				this.field_9818,
				this.field_9820,
				this.field_9823,
				resultConsumer,
				this.field_9816
			);
	}

	public class_2168 method_9209(ResultConsumer<class_2168> resultConsumer, BinaryOperator<ResultConsumer<class_2168>> binaryOperator) {
		ResultConsumer<class_2168> resultConsumer2 = (ResultConsumer<class_2168>)binaryOperator.apply(this.field_9821, resultConsumer);
		return this.method_9231(resultConsumer2);
	}

	public class_2168 method_9217() {
		return this.field_9823
			? this
			: new class_2168(
				this.field_9819,
				this.field_9817,
				this.field_9822,
				this.field_9828,
				this.field_9815,
				this.field_9826,
				this.field_9825,
				this.field_9818,
				this.field_9820,
				true,
				this.field_9821,
				this.field_9816
			);
	}

	public class_2168 method_9206(int i) {
		return i == this.field_9815
			? this
			: new class_2168(
				this.field_9819,
				this.field_9817,
				this.field_9822,
				this.field_9828,
				i,
				this.field_9826,
				this.field_9825,
				this.field_9818,
				this.field_9820,
				this.field_9823,
				this.field_9821,
				this.field_9816
			);
	}

	public class_2168 method_9230(int i) {
		return i <= this.field_9815
			? this
			: new class_2168(
				this.field_9819,
				this.field_9817,
				this.field_9822,
				this.field_9828,
				i,
				this.field_9826,
				this.field_9825,
				this.field_9818,
				this.field_9820,
				this.field_9823,
				this.field_9821,
				this.field_9816
			);
	}

	public class_2168 method_9218(class_2183.class_2184 arg) {
		return arg == this.field_9816
			? this
			: new class_2168(
				this.field_9819,
				this.field_9817,
				this.field_9822,
				this.field_9828,
				this.field_9815,
				this.field_9826,
				this.field_9825,
				this.field_9818,
				this.field_9820,
				this.field_9823,
				this.field_9821,
				arg
			);
	}

	public class_2168 method_9227(class_3218 arg) {
		return arg == this.field_9828
			? this
			: new class_2168(
				this.field_9819,
				this.field_9817,
				this.field_9822,
				arg,
				this.field_9815,
				this.field_9826,
				this.field_9825,
				this.field_9818,
				this.field_9820,
				this.field_9823,
				this.field_9821,
				this.field_9816
			);
	}

	public class_2168 method_9220(class_1297 arg, class_2183.class_2184 arg2) throws CommandSyntaxException {
		return this.method_9221(arg2.method_9302(arg));
	}

	public class_2168 method_9221(class_243 arg) throws CommandSyntaxException {
		class_243 lv = this.field_9816.method_9299(this);
		double d = arg.field_1352 - lv.field_1352;
		double e = arg.field_1351 - lv.field_1351;
		double f = arg.field_1350 - lv.field_1350;
		double g = (double)class_3532.method_15368(d * d + f * f);
		float h = class_3532.method_15393((float)(-(class_3532.method_15349(e, g) * 180.0F / (float)Math.PI)));
		float i = class_3532.method_15393((float)(class_3532.method_15349(f, d) * 180.0F / (float)Math.PI) - 90.0F);
		return this.method_9216(new class_241(h, i));
	}

	public class_2561 method_9223() {
		return this.field_9825;
	}

	public String method_9214() {
		return this.field_9826;
	}

	@Override
	public boolean method_9259(int i) {
		return this.field_9815 >= i;
	}

	public class_243 method_9222() {
		return this.field_9817;
	}

	public class_3218 method_9225() {
		return this.field_9828;
	}

	@Nullable
	public class_1297 method_9228() {
		return this.field_9820;
	}

	public class_1297 method_9229() throws CommandSyntaxException {
		if (this.field_9820 == null) {
			throw field_9827.create();
		} else {
			return this.field_9820;
		}
	}

	public class_3222 method_9207() throws CommandSyntaxException {
		if (!(this.field_9820 instanceof class_3222)) {
			throw field_9824.create();
		} else {
			return (class_3222)this.field_9820;
		}
	}

	public class_241 method_9210() {
		return this.field_9822;
	}

	public MinecraftServer method_9211() {
		return this.field_9818;
	}

	public class_2183.class_2184 method_9219() {
		return this.field_9816;
	}

	public void method_9226(class_2561 arg, boolean bl) {
		if (this.field_9819.method_9200() && !this.field_9823) {
			this.field_9819.method_9203(arg);
		}

		if (bl && this.field_9819.method_9201() && !this.field_9823) {
			this.method_9212(arg);
		}
	}

	private void method_9212(class_2561 arg) {
		class_2561 lv = new class_2588("chat.type.admin", this.method_9223(), arg).method_10856(new class_124[]{class_124.field_1080, class_124.field_1056});
		if (this.field_9818.method_3767().method_8355("sendCommandFeedback")) {
			for (class_3222 lv2 : this.field_9818.method_3760().method_14571()) {
				if (lv2 != this.field_9819 && this.field_9818.method_3760().method_14569(lv2.method_7334())) {
					lv2.method_9203(lv);
				}
			}
		}

		if (this.field_9819 != this.field_9818 && this.field_9818.method_3767().method_8355("logAdminCommands")) {
			this.field_9818.method_9203(lv);
		}
	}

	public void method_9213(class_2561 arg) {
		if (this.field_9819.method_9202() && !this.field_9823) {
			this.field_9819.method_9203(new class_2585("").method_10852(arg).method_10854(class_124.field_1061));
		}
	}

	public void method_9215(CommandContext<class_2168> commandContext, boolean bl, int i) {
		if (this.field_9821 != null) {
			this.field_9821.onCommandComplete(commandContext, bl, i);
		}
	}

	@Override
	public Collection<String> method_9262() {
		return Lists.<String>newArrayList(this.field_9818.method_3858());
	}

	@Override
	public Collection<String> method_9267() {
		return this.field_9818.method_3845().method_1196();
	}

	@Override
	public Collection<class_2960> method_9254() {
		return class_2378.field_11156.method_10235();
	}

	@Override
	public Stream<class_2960> method_9273() {
		return this.field_9818.method_3772().method_8127();
	}

	@Override
	public CompletableFuture<Suggestions> method_9261(CommandContext<class_2172> commandContext, SuggestionsBuilder suggestionsBuilder) {
		return null;
	}
}
