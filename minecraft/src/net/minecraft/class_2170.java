package net.minecraft;

import com.google.common.collect.Maps;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import java.util.Map;
import java.util.function.Predicate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_2170 {
	private static final Logger field_9833 = LogManager.getLogger();
	private final CommandDispatcher<class_2168> field_9832 = new CommandDispatcher<>();

	public class_2170(boolean bl) {
		class_3008.method_12980(this.field_9832);
		class_3050.method_13271(this.field_9832);
		class_3019.method_13053(this.field_9832);
		class_3020.method_13076(this.field_9832);
		class_3023.method_13089(this.field_9832);
		class_3164.method_13905(this.field_9832);
		class_3027.method_13125(this.field_9832);
		class_3032.method_13156(this.field_9832);
		class_3035.method_13166(this.field_9832);
		class_3036.method_13169(this.field_9832);
		class_3043.method_13229(this.field_9832);
		class_3045.method_13237(this.field_9832);
		class_3048.method_13243(this.field_9832);
		class_3054.method_13330(this.field_9832);
		class_3057.method_13347(this.field_9832);
		class_3060.method_13365(this.field_9832);
		class_3062.method_13380(this.field_9832);
		class_3064.method_13388(this.field_9832);
		class_3065.method_13392(this.field_9832);
		class_3068.method_13402(this.field_9832);
		class_3069.method_13405(this.field_9832);
		class_3073.method_13410(this.field_9832);
		class_3075.method_13429(this.field_9832);
		class_3078.method_13435(this.field_9832);
		class_3079.method_13443(this.field_9832);
		class_3039.method_13193(this.field_9832);
		class_3082.method_13461(this.field_9832);
		class_3089.method_13486(this.field_9832);
		class_3091.method_13500(this.field_9832);
		class_3093.method_13510(this.field_9832);
		class_3097.method_13529(this.field_9832);
		class_3095.method_13517(this.field_9832);
		class_3102.method_13541(this.field_9832);
		class_3110.method_13562(this.field_9832);
		class_3112.method_13567(this.field_9832);
		class_3115.method_13595(this.field_9832);
		class_3118.method_13616(this.field_9832);
		class_3119.method_13623(this.field_9832);
		class_3127.method_13641(this.field_9832);
		class_3128.method_13647(this.field_9832);
		class_3131.method_13654(this.field_9832);
		class_3136.method_13681(this.field_9832);
		class_3138.method_13690(this.field_9832);
		class_3140.method_13698(this.field_9832);
		class_3142.method_13736(this.field_9832);
		class_3945.method_17600(this.field_9832);
		class_3143.method_13760(this.field_9832);
		class_3146.method_13776(this.field_9832);
		class_3149.method_13786(this.field_9832);
		class_3151.method_13804(this.field_9832);
		class_3153.method_13813(this.field_9832);
		class_3155.method_13827(this.field_9832);
		class_3158.method_13858(this.field_9832);
		if (bl) {
			class_3012.method_13008(this.field_9832);
			class_3014.method_13014(this.field_9832);
			class_3016.method_13021(this.field_9832);
			class_3030.method_13143(this.field_9832);
			class_3083.method_13464(this.field_9832);
			class_3086.method_13472(this.field_9832);
			class_3088.method_13478(this.field_9832);
			class_3104.method_13551(this.field_9832);
			class_3106.method_13556(this.field_9832);
			class_3107.method_13559(this.field_9832);
			class_3123.method_13631(this.field_9832);
			class_3134.method_13675(this.field_9832);
			class_3156.method_13836(this.field_9832);
		}

		this.field_9832
			.findAmbiguities(
				(commandNode, commandNode2, commandNode3, collection) -> field_9833.warn(
						"Ambiguity between arguments {} and {} with inputs: {}", this.field_9832.getPath(commandNode2), this.field_9832.getPath(commandNode3), collection
					)
			);
		this.field_9832.setConsumer((commandContext, blx, i) -> commandContext.getSource().method_9215(commandContext, blx, i));
	}

	public int method_9249(class_2168 arg, String string) {
		StringReader stringReader = new StringReader(string);
		if (stringReader.canRead() && stringReader.peek() == '/') {
			stringReader.skip();
		}

		arg.method_9211().method_16044().method_15396(string);

		byte var20;
		try {
			return this.field_9832.execute(stringReader, arg);
		} catch (class_2164 var13) {
			arg.method_9213(var13.method_9199());
			return 0;
		} catch (CommandSyntaxException var14) {
			arg.method_9213(class_2564.method_10883(var14.getRawMessage()));
			if (var14.getInput() != null && var14.getCursor() >= 0) {
				int i = Math.min(var14.getInput().length(), var14.getCursor());
				class_2561 lv2 = new class_2585("")
					.method_10854(class_124.field_1080)
					.method_10859(argx -> argx.method_10958(new class_2558(class_2558.class_2559.field_11745, string)));
				if (i > 10) {
					lv2.method_10864("...");
				}

				lv2.method_10864(var14.getInput().substring(Math.max(0, i - 10), i));
				if (i < var14.getInput().length()) {
					class_2561 lv3 = new class_2585(var14.getInput().substring(i)).method_10856(new class_124[]{class_124.field_1061, class_124.field_1073});
					lv2.method_10852(lv3);
				}

				lv2.method_10852(new class_2588("command.context.here").method_10856(new class_124[]{class_124.field_1061, class_124.field_1056}));
				arg.method_9213(lv2);
			}

			return 0;
		} catch (Exception var15) {
			class_2561 lv4 = new class_2585(var15.getMessage() == null ? var15.getClass().getName() : var15.getMessage());
			if (field_9833.isDebugEnabled()) {
				StackTraceElement[] stackTraceElements = var15.getStackTrace();

				for (int j = 0; j < Math.min(stackTraceElements.length, 3); j++) {
					lv4.method_10864("\n\n")
						.method_10864(stackTraceElements[j].getMethodName())
						.method_10864("\n ")
						.method_10864(stackTraceElements[j].getFileName())
						.method_10864(":")
						.method_10864(String.valueOf(stackTraceElements[j].getLineNumber()));
				}
			}

			arg.method_9213(new class_2588("command.failed").method_10859(arg2 -> arg2.method_10949(new class_2568(class_2568.class_2569.field_11762, lv4))));
			var20 = 0;
		} finally {
			arg.method_9211().method_16044().method_15407();
		}

		return var20;
	}

	public void method_9241(class_3222 arg) {
		Map<CommandNode<class_2168>, CommandNode<class_2172>> map = Maps.<CommandNode<class_2168>, CommandNode<class_2172>>newHashMap();
		RootCommandNode<class_2172> rootCommandNode = new RootCommandNode<>();
		map.put(this.field_9832.getRoot(), rootCommandNode);
		this.method_9239(this.field_9832.getRoot(), rootCommandNode, arg.method_5671(), map);
		arg.field_13987.method_14364(new class_2641(rootCommandNode));
	}

	private void method_9239(
		CommandNode<class_2168> commandNode, CommandNode<class_2172> commandNode2, class_2168 arg, Map<CommandNode<class_2168>, CommandNode<class_2172>> map
	) {
		for (CommandNode<class_2168> commandNode3 : commandNode.getChildren()) {
			if (commandNode3.canUse(arg)) {
				ArgumentBuilder<class_2172, ?> argumentBuilder = commandNode3.createBuilder();
				argumentBuilder.requires(argx -> true);
				if (argumentBuilder.getCommand() != null) {
					argumentBuilder.executes(commandContext -> 0);
				}

				if (argumentBuilder instanceof RequiredArgumentBuilder) {
					RequiredArgumentBuilder<class_2172, ?> requiredArgumentBuilder = (RequiredArgumentBuilder<class_2172, ?>)argumentBuilder;
					if (requiredArgumentBuilder.getSuggestionsProvider() != null) {
						requiredArgumentBuilder.suggests(class_2321.method_10026(requiredArgumentBuilder.getSuggestionsProvider()));
					}
				}

				if (argumentBuilder.getRedirect() != null) {
					argumentBuilder.redirect((CommandNode<class_2172>)map.get(argumentBuilder.getRedirect()));
				}

				CommandNode<class_2172> commandNode4 = argumentBuilder.build();
				map.put(commandNode3, commandNode4);
				commandNode2.addChild(commandNode4);
				if (!commandNode3.getChildren().isEmpty()) {
					this.method_9239(commandNode3, commandNode4, arg, map);
				}
			}
		}
	}

	public static LiteralArgumentBuilder<class_2168> method_9247(String string) {
		return LiteralArgumentBuilder.literal(string);
	}

	public static <T> RequiredArgumentBuilder<class_2168, T> method_9244(String string, ArgumentType<T> argumentType) {
		return RequiredArgumentBuilder.argument(string, argumentType);
	}

	public static Predicate<String> method_9238(class_2170.class_2171 arg) {
		return string -> {
			try {
				arg.parse(new StringReader(string));
				return true;
			} catch (CommandSyntaxException var3) {
				return false;
			}
		};
	}

	public CommandDispatcher<class_2168> method_9235() {
		return this.field_9832;
	}

	@FunctionalInterface
	public interface class_2171 {
		void parse(StringReader stringReader) throws CommandSyntaxException;
	}
}
