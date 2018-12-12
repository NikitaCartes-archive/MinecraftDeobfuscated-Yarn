package net.minecraft.server.command;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import net.minecraft.class_3161;
import net.minecraft.class_3162;
import net.minecraft.class_3169;
import net.minecraft.command.arguments.NbtCompoundTagArgumentType;
import net.minecraft.command.arguments.NbtPathArgumentType;
import net.minecraft.command.arguments.NbtTagArgumentType;
import net.minecraft.nbt.AbstractListTag;
import net.minecraft.nbt.AbstractNumberTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.math.MathHelper;

public class DataCommand {
	private static final SimpleCommandExceptionType MERGE_FAILED_EXCEPTION = new SimpleCommandExceptionType(
		new TranslatableTextComponent("commands.data.merge.failed")
	);
	private static final DynamicCommandExceptionType GET_INVALID_EXCEPTION = new DynamicCommandExceptionType(
		object -> new TranslatableTextComponent("commands.data.get.invalid", object)
	);
	private static final DynamicCommandExceptionType GET_UNKNOWN_EXCEPTION = new DynamicCommandExceptionType(
		object -> new TranslatableTextComponent("commands.data.get.unknown", object)
	);
	private static final SimpleCommandExceptionType GET_MULTIPLE_EXCEPTION = new SimpleCommandExceptionType(
		new TranslatableTextComponent("commands.data.get.multiple")
	);
	private static final DynamicCommandExceptionType MODIFY_EXPECTELIST_EXCEPTION = new DynamicCommandExceptionType(
		object -> new TranslatableTextComponent("commands.data.modify.expected_list", object)
	);
	private static final DynamicCommandExceptionType MODIFY_EXPECTEDOBJECT_EXCEPTION = new DynamicCommandExceptionType(
		object -> new TranslatableTextComponent("commands.data.modify.expected_object", object)
	);
	public static final List<Function<String, DataCommand.class_3167>> field_13790 = ImmutableList.of(class_3169.field_13800, class_3161.field_13786);
	public static final List<DataCommand.class_3167> field_13798 = (List<DataCommand.class_3167>)field_13790.stream()
		.map(function -> (DataCommand.class_3167)function.apply("target"))
		.collect(ImmutableList.toImmutableList());
	public static final List<DataCommand.class_3167> field_13792 = (List<DataCommand.class_3167>)field_13790.stream()
		.map(function -> (DataCommand.class_3167)function.apply("source"))
		.collect(ImmutableList.toImmutableList());

	public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
		LiteralArgumentBuilder<ServerCommandSource> literalArgumentBuilder = ServerCommandManager.literal("data")
			.requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2));

		for (DataCommand.class_3167 lv : field_13798) {
			literalArgumentBuilder.then(
					lv.method_13925(
						ServerCommandManager.literal("merge"),
						argumentBuilder -> argumentBuilder.then(
								ServerCommandManager.argument("nbt", NbtCompoundTagArgumentType.create())
									.executes(
										commandContext -> method_13901(
												commandContext.getSource(), lv.method_13924(commandContext), NbtCompoundTagArgumentType.getCompoundArgument(commandContext, "nbt")
											)
									)
							)
					)
				)
				.then(
					lv.method_13925(
						ServerCommandManager.literal("get"),
						argumentBuilder -> argumentBuilder.executes(
									commandContext -> method_13908((ServerCommandSource)commandContext.getSource(), lv.method_13924(commandContext))
								)
								.then(
									ServerCommandManager.argument("path", NbtPathArgumentType.create())
										.executes(
											commandContext -> method_13916(commandContext.getSource(), lv.method_13924(commandContext), NbtPathArgumentType.method_9358(commandContext, "path"))
										)
										.then(
											ServerCommandManager.argument("scale", DoubleArgumentType.doubleArg())
												.executes(
													commandContext -> method_13903(
															commandContext.getSource(),
															lv.method_13924(commandContext),
															NbtPathArgumentType.method_9358(commandContext, "path"),
															DoubleArgumentType.getDouble(commandContext, "scale")
														)
												)
										)
								)
					)
				)
				.then(
					lv.method_13925(
						ServerCommandManager.literal("remove"),
						argumentBuilder -> argumentBuilder.then(
								ServerCommandManager.argument("path", NbtPathArgumentType.create())
									.executes(
										commandContext -> method_13885(commandContext.getSource(), lv.method_13924(commandContext), NbtPathArgumentType.method_9358(commandContext, "path"))
									)
							)
					)
				)
				.then(
					method_13898(
						(argumentBuilder, arg) -> argumentBuilder.then(
									ServerCommandManager.literal("insert")
										.then(
											ServerCommandManager.literal("before")
												.then(ServerCommandManager.argument("index", IntegerArgumentType.integer()).then(arg.create((commandContext, compoundTag, argx, list) -> {
													int i = IntegerArgumentType.getInteger(commandContext, "index");
													return method_13910(i, false, compoundTag, argx, list);
												})))
										)
										.then(
											ServerCommandManager.literal("after")
												.then(ServerCommandManager.argument("index", IntegerArgumentType.integer()).then(arg.create((commandContext, compoundTag, argx, list) -> {
													int i = IntegerArgumentType.getInteger(commandContext, "index");
													return method_13910(i, true, compoundTag, argx, list);
												})))
										)
								)
								.then(
									ServerCommandManager.literal("prepend").then(arg.create((commandContext, compoundTag, argx, list) -> method_13910(0, false, compoundTag, argx, list)))
								)
								.then(
									ServerCommandManager.literal("append").then(arg.create((commandContext, compoundTag, argx, list) -> method_13910(-1, true, compoundTag, argx, list)))
								)
								.then(
									ServerCommandManager.literal("set")
										.then(arg.create((commandContext, compoundTag, argx, list) -> argx.method_9368(compoundTag, Iterables.getLast(list)::copy)))
								)
								.then(ServerCommandManager.literal("merge").then(arg.create((commandContext, compoundTag, argx, list) -> {
									Collection<Tag> collection = argx.method_9367(compoundTag, CompoundTag::new);

									for (Tag tag : collection) {
										if (!(tag instanceof CompoundTag)) {
											throw MODIFY_EXPECTEDOBJECT_EXCEPTION.create(tag);
										}

										CompoundTag compoundTag2 = (CompoundTag)tag;

										for (Tag tag2 : list) {
											if (!(tag2 instanceof CompoundTag)) {
												throw MODIFY_EXPECTEDOBJECT_EXCEPTION.create(tag2);
											}

											compoundTag2.copyFrom((CompoundTag)tag2);
										}
									}

									return collection.size();
								})))
					)
				);
		}

		commandDispatcher.register(literalArgumentBuilder);
	}

	public static int method_13910(int i, boolean bl, CompoundTag compoundTag, NbtPathArgumentType.class_2209 arg, List<Tag> list) throws CommandSyntaxException {
		Collection<Tag> collection = arg.method_9367(compoundTag, ListTag::new);

		for (Tag tag : collection) {
			if (!(tag instanceof AbstractListTag)) {
				throw MODIFY_EXPECTELIST_EXCEPTION.create(tag);
			}

			AbstractListTag<?> abstractListTag = (AbstractListTag<?>)tag;
			int j = (i < 0 ? abstractListTag.size() + i : i) + (bl ? 1 : 0);

			for (Tag tag2 : list) {
				abstractListTag.append(j, tag2.copy());
			}
		}

		return collection.size();
	}

	private static ArgumentBuilder<ServerCommandSource, ?> method_13898(BiConsumer<ArgumentBuilder<ServerCommandSource, ?>, DataCommand.class_3166> biConsumer) {
		LiteralArgumentBuilder<ServerCommandSource> literalArgumentBuilder = ServerCommandManager.literal("modify");

		for (DataCommand.class_3167 lv : field_13798) {
			lv.method_13925(
				literalArgumentBuilder,
				argumentBuilder -> {
					ArgumentBuilder<ServerCommandSource, ?> argumentBuilder2 = ServerCommandManager.argument("targetPath", NbtPathArgumentType.create());

					for (DataCommand.class_3167 lvx : field_13792) {
						biConsumer.accept(
							argumentBuilder2,
							(DataCommand.class_3166)arg3 -> lvx.method_13925(ServerCommandManager.literal("from"), argumentBuilderx -> argumentBuilderx.executes(commandContext -> {
										List<Tag> list = Collections.singletonList(lvx.method_13924(commandContext).method_13881());
										return method_13920(commandContext, lv, arg3, list);
									}).then(ServerCommandManager.argument("sourcePath", NbtPathArgumentType.create()).executes(commandContext -> {
										class_3162 lvxx = lvx.method_13924(commandContext);
										NbtPathArgumentType.class_2209 lv2 = NbtPathArgumentType.method_9358(commandContext, "sourcePath");
										List<Tag> list = lv2.method_9366(lvxx.method_13881());
										return method_13920(commandContext, lv, arg3, list);
									})))
						);
					}

					biConsumer.accept(
						argumentBuilder2,
						(DataCommand.class_3166)arg2 -> (LiteralArgumentBuilder)ServerCommandManager.literal("value")
								.then(ServerCommandManager.argument("value", NbtTagArgumentType.create()).executes(commandContext -> {
									List<Tag> list = Collections.singletonList(NbtTagArgumentType.getNbtTagArgument(commandContext, "value"));
									return method_13920(commandContext, lv, arg2, list);
								}))
					);
					return argumentBuilder.then(argumentBuilder2);
				}
			);
		}

		return literalArgumentBuilder;
	}

	private static int method_13920(CommandContext<ServerCommandSource> commandContext, DataCommand.class_3167 arg, DataCommand.class_3165 arg2, List<Tag> list) throws CommandSyntaxException {
		class_3162 lv = arg.method_13924(commandContext);
		NbtPathArgumentType.class_2209 lv2 = NbtPathArgumentType.method_9358(commandContext, "targetPath");
		CompoundTag compoundTag = lv.method_13881();
		CompoundTag compoundTag2 = compoundTag.copy();
		int i = arg2.modify(commandContext, compoundTag2, lv2, list);
		if (compoundTag.equals(compoundTag2)) {
			throw MERGE_FAILED_EXCEPTION.create();
		} else {
			lv.method_13880(compoundTag2);
			commandContext.getSource().sendFeedback(lv.method_13883(), true);
			return i;
		}
	}

	private static int method_13885(ServerCommandSource serverCommandSource, class_3162 arg, NbtPathArgumentType.class_2209 arg2) throws CommandSyntaxException {
		CompoundTag compoundTag = arg.method_13881();
		CompoundTag compoundTag2 = compoundTag.copy();
		int i = arg2.method_9372(compoundTag);
		if (compoundTag2.equals(compoundTag)) {
			throw MERGE_FAILED_EXCEPTION.create();
		} else {
			arg.method_13880(compoundTag);
			serverCommandSource.sendFeedback(arg.method_13883(), true);
			return i;
		}
	}

	private static Tag method_13921(NbtPathArgumentType.class_2209 arg, class_3162 arg2) throws CommandSyntaxException {
		Collection<Tag> collection = arg.method_9366(arg2.method_13881());
		Iterator<Tag> iterator = collection.iterator();
		Tag tag = (Tag)iterator.next();
		if (iterator.hasNext()) {
			throw GET_MULTIPLE_EXCEPTION.create();
		} else {
			return tag;
		}
	}

	private static int method_13916(ServerCommandSource serverCommandSource, class_3162 arg, NbtPathArgumentType.class_2209 arg2) throws CommandSyntaxException {
		Tag tag = method_13921(arg2, arg);
		int i;
		if (tag instanceof AbstractNumberTag) {
			i = MathHelper.floor(((AbstractNumberTag)tag).getDouble());
		} else if (tag instanceof AbstractListTag) {
			i = ((AbstractListTag)tag).size();
		} else if (tag instanceof CompoundTag) {
			i = ((CompoundTag)tag).getSize();
		} else {
			if (!(tag instanceof StringTag)) {
				throw GET_UNKNOWN_EXCEPTION.create(arg2.toString());
			}

			i = tag.asString().length();
		}

		serverCommandSource.sendFeedback(arg.method_13882(tag), false);
		return i;
	}

	private static int method_13903(ServerCommandSource serverCommandSource, class_3162 arg, NbtPathArgumentType.class_2209 arg2, double d) throws CommandSyntaxException {
		Tag tag = method_13921(arg2, arg);
		if (!(tag instanceof AbstractNumberTag)) {
			throw GET_INVALID_EXCEPTION.create(arg2.toString());
		} else {
			int i = MathHelper.floor(((AbstractNumberTag)tag).getDouble() * d);
			serverCommandSource.sendFeedback(arg.method_13879(arg2, d, i), false);
			return i;
		}
	}

	private static int method_13908(ServerCommandSource serverCommandSource, class_3162 arg) throws CommandSyntaxException {
		serverCommandSource.sendFeedback(arg.method_13882(arg.method_13881()), false);
		return 1;
	}

	private static int method_13901(ServerCommandSource serverCommandSource, class_3162 arg, CompoundTag compoundTag) throws CommandSyntaxException {
		CompoundTag compoundTag2 = arg.method_13881();
		CompoundTag compoundTag3 = compoundTag2.copy().copyFrom(compoundTag);
		if (compoundTag2.equals(compoundTag3)) {
			throw MERGE_FAILED_EXCEPTION.create();
		} else {
			arg.method_13880(compoundTag3);
			serverCommandSource.sendFeedback(arg.method_13883(), true);
			return 1;
		}
	}

	interface class_3165 {
		int modify(CommandContext<ServerCommandSource> commandContext, CompoundTag compoundTag, NbtPathArgumentType.class_2209 arg, List<Tag> list) throws CommandSyntaxException;
	}

	interface class_3166 {
		ArgumentBuilder<ServerCommandSource, ?> create(DataCommand.class_3165 arg);
	}

	public interface class_3167 {
		class_3162 method_13924(CommandContext<ServerCommandSource> commandContext) throws CommandSyntaxException;

		ArgumentBuilder<ServerCommandSource, ?> method_13925(
			ArgumentBuilder<ServerCommandSource, ?> argumentBuilder, Function<ArgumentBuilder<ServerCommandSource, ?>, ArgumentBuilder<ServerCommandSource, ?>> function
		);
	}
}
