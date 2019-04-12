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
import net.minecraft.command.BlockDataObject;
import net.minecraft.command.DataCommandObject;
import net.minecraft.command.EntityDataObject;
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
	private static final DynamicCommandExceptionType MODIFY_EXPECTED_LIST_EXCEPTION = new DynamicCommandExceptionType(
		object -> new TranslatableTextComponent("commands.data.modify.expected_list", object)
	);
	private static final DynamicCommandExceptionType MODIFY_EXPECTED_OBJECT_EXCEPTION = new DynamicCommandExceptionType(
		object -> new TranslatableTextComponent("commands.data.modify.expected_object", object)
	);
	private static final DynamicCommandExceptionType MODIFY_INVALID_INDEX_EXCEPTION = new DynamicCommandExceptionType(
		object -> new TranslatableTextComponent("commands.data.modify.invalid_index", object)
	);
	public static final List<Function<String, DataCommand.ObjectType>> OBJECT_TYPES = ImmutableList.of(EntityDataObject.field_13800, BlockDataObject.field_13786);
	public static final List<DataCommand.ObjectType> TARGET_OBJECT_TYPES = (List<DataCommand.ObjectType>)OBJECT_TYPES.stream()
		.map(function -> (DataCommand.ObjectType)function.apply("target"))
		.collect(ImmutableList.toImmutableList());
	public static final List<DataCommand.ObjectType> SOURCE_OBJECT_TYPES = (List<DataCommand.ObjectType>)OBJECT_TYPES.stream()
		.map(function -> (DataCommand.ObjectType)function.apply("source"))
		.collect(ImmutableList.toImmutableList());

	public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
		LiteralArgumentBuilder<ServerCommandSource> literalArgumentBuilder = CommandManager.literal("data")
			.requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2));

		for (DataCommand.ObjectType objectType : TARGET_OBJECT_TYPES) {
			literalArgumentBuilder.then(
					objectType.addArgumentsToBuilder(
						CommandManager.literal("merge"),
						argumentBuilder -> argumentBuilder.then(
								CommandManager.argument("nbt", NbtCompoundTagArgumentType.create())
									.executes(
										commandContext -> executeMerge(
												commandContext.getSource(), objectType.getObject(commandContext), NbtCompoundTagArgumentType.getCompoundTag(commandContext, "nbt")
											)
									)
							)
					)
				)
				.then(
					objectType.addArgumentsToBuilder(
						CommandManager.literal("get"),
						argumentBuilder -> argumentBuilder.executes(
									commandContext -> executeGet((ServerCommandSource)commandContext.getSource(), objectType.getObject(commandContext))
								)
								.then(
									CommandManager.argument("path", NbtPathArgumentType.create())
										.executes(
											commandContext -> executeGet(
													commandContext.getSource(), objectType.getObject(commandContext), NbtPathArgumentType.getNbtPath(commandContext, "path")
												)
										)
										.then(
											CommandManager.argument("scale", DoubleArgumentType.doubleArg())
												.executes(
													commandContext -> executeGet(
															commandContext.getSource(),
															objectType.getObject(commandContext),
															NbtPathArgumentType.getNbtPath(commandContext, "path"),
															DoubleArgumentType.getDouble(commandContext, "scale")
														)
												)
										)
								)
					)
				)
				.then(
					objectType.addArgumentsToBuilder(
						CommandManager.literal("remove"),
						argumentBuilder -> argumentBuilder.then(
								CommandManager.argument("path", NbtPathArgumentType.create())
									.executes(
										commandContext -> executeRemove(
												commandContext.getSource(), objectType.getObject(commandContext), NbtPathArgumentType.getNbtPath(commandContext, "path")
											)
									)
							)
					)
				)
				.then(
					addModifyArgument(
						(argumentBuilder, modifyArgumentCreator) -> argumentBuilder.then(
									CommandManager.literal("insert")
										.then(CommandManager.argument("index", IntegerArgumentType.integer()).then(modifyArgumentCreator.create((commandContext, compoundTag, arg, list) -> {
											int i = IntegerArgumentType.getInteger(commandContext, "index");
											return executeInsert(i, compoundTag, arg, list);
										})))
								)
								.then(
									CommandManager.literal("prepend")
										.then(modifyArgumentCreator.create((commandContext, compoundTag, arg, list) -> executeInsert(0, compoundTag, arg, list)))
								)
								.then(
									CommandManager.literal("append")
										.then(modifyArgumentCreator.create((commandContext, compoundTag, arg, list) -> executeInsert(-1, compoundTag, arg, list)))
								)
								.then(
									CommandManager.literal("set")
										.then(modifyArgumentCreator.create((commandContext, compoundTag, arg, list) -> arg.method_9368(compoundTag, Iterables.getLast(list)::copy)))
								)
								.then(CommandManager.literal("merge").then(modifyArgumentCreator.create((commandContext, compoundTag, arg, list) -> {
									Collection<Tag> collection = arg.method_9367(compoundTag, CompoundTag::new);
									int i = 0;

									for (Tag tag : collection) {
										if (!(tag instanceof CompoundTag)) {
											throw MODIFY_EXPECTED_OBJECT_EXCEPTION.create(tag);
										}

										CompoundTag compoundTag2 = (CompoundTag)tag;
										CompoundTag compoundTag3 = compoundTag2.method_10553();

										for (Tag tag2 : list) {
											if (!(tag2 instanceof CompoundTag)) {
												throw MODIFY_EXPECTED_OBJECT_EXCEPTION.create(tag2);
											}

											compoundTag2.copyFrom((CompoundTag)tag2);
										}

										i += compoundTag3.equals(compoundTag2) ? 0 : 1;
									}

									return i;
								})))
					)
				);
		}

		commandDispatcher.register(literalArgumentBuilder);
	}

	private static int executeInsert(int i, CompoundTag compoundTag, NbtPathArgumentType.class_2209 arg, List<Tag> list) throws CommandSyntaxException {
		Collection<Tag> collection = arg.method_9367(compoundTag, ListTag::new);
		int j = 0;

		for (Tag tag : collection) {
			if (!(tag instanceof AbstractListTag)) {
				throw MODIFY_EXPECTED_LIST_EXCEPTION.create(tag);
			}

			boolean bl = false;
			AbstractListTag<?> abstractListTag = (AbstractListTag<?>)tag;
			int k = i < 0 ? abstractListTag.size() + i + 1 : i;

			for (Tag tag2 : list) {
				try {
					if (abstractListTag.addTag(k, tag2.copy())) {
						k++;
						bl = true;
					}
				} catch (IndexOutOfBoundsException var14) {
					throw MODIFY_INVALID_INDEX_EXCEPTION.create(k);
				}
			}

			j += bl ? 1 : 0;
		}

		return j;
	}

	private static ArgumentBuilder<ServerCommandSource, ?> addModifyArgument(
		BiConsumer<ArgumentBuilder<ServerCommandSource, ?>, DataCommand.ModifyArgumentCreator> biConsumer
	) {
		LiteralArgumentBuilder<ServerCommandSource> literalArgumentBuilder = CommandManager.literal("modify");

		for (DataCommand.ObjectType objectType : TARGET_OBJECT_TYPES) {
			objectType.addArgumentsToBuilder(
				literalArgumentBuilder,
				argumentBuilder -> {
					ArgumentBuilder<ServerCommandSource, ?> argumentBuilder2 = CommandManager.argument("targetPath", NbtPathArgumentType.create());

					for (DataCommand.ObjectType objectType2 : SOURCE_OBJECT_TYPES) {
						biConsumer.accept(
							argumentBuilder2,
							(DataCommand.ModifyArgumentCreator)modifyOperation -> objectType2.addArgumentsToBuilder(
									CommandManager.literal("from"), argumentBuilderx -> argumentBuilderx.executes(commandContext -> {
											List<Tag> list = Collections.singletonList(objectType2.getObject(commandContext).getTag());
											return executeModify(commandContext, objectType, modifyOperation, list);
										}).then(CommandManager.argument("sourcePath", NbtPathArgumentType.create()).executes(commandContext -> {
											DataCommandObject dataCommandObject = objectType2.getObject(commandContext);
											NbtPathArgumentType.class_2209 lv = NbtPathArgumentType.getNbtPath(commandContext, "sourcePath");
											List<Tag> list = lv.method_9366(dataCommandObject.getTag());
											return executeModify(commandContext, objectType, modifyOperation, list);
										}))
								)
						);
					}

					biConsumer.accept(
						argumentBuilder2,
						(DataCommand.ModifyArgumentCreator)modifyOperation -> (LiteralArgumentBuilder)CommandManager.literal("value")
								.then(CommandManager.argument("value", NbtTagArgumentType.create()).executes(commandContext -> {
									List<Tag> list = Collections.singletonList(NbtTagArgumentType.getTag(commandContext, "value"));
									return executeModify(commandContext, objectType, modifyOperation, list);
								}))
					);
					return argumentBuilder.then(argumentBuilder2);
				}
			);
		}

		return literalArgumentBuilder;
	}

	private static int executeModify(
		CommandContext<ServerCommandSource> commandContext, DataCommand.ObjectType objectType, DataCommand.ModifyOperation modifyOperation, List<Tag> list
	) throws CommandSyntaxException {
		DataCommandObject dataCommandObject = objectType.getObject(commandContext);
		NbtPathArgumentType.class_2209 lv = NbtPathArgumentType.getNbtPath(commandContext, "targetPath");
		CompoundTag compoundTag = dataCommandObject.getTag();
		int i = modifyOperation.modify(commandContext, compoundTag, lv, list);
		if (i == 0) {
			throw MERGE_FAILED_EXCEPTION.create();
		} else {
			dataCommandObject.setTag(compoundTag);
			commandContext.getSource().sendFeedback(dataCommandObject.getModifiedFeedback(), true);
			return i;
		}
	}

	private static int executeRemove(ServerCommandSource serverCommandSource, DataCommandObject dataCommandObject, NbtPathArgumentType.class_2209 arg) throws CommandSyntaxException {
		CompoundTag compoundTag = dataCommandObject.getTag();
		int i = arg.method_9372(compoundTag);
		if (i == 0) {
			throw MERGE_FAILED_EXCEPTION.create();
		} else {
			dataCommandObject.setTag(compoundTag);
			serverCommandSource.sendFeedback(dataCommandObject.getModifiedFeedback(), true);
			return i;
		}
	}

	private static Tag getTag(NbtPathArgumentType.class_2209 arg, DataCommandObject dataCommandObject) throws CommandSyntaxException {
		Collection<Tag> collection = arg.method_9366(dataCommandObject.getTag());
		Iterator<Tag> iterator = collection.iterator();
		Tag tag = (Tag)iterator.next();
		if (iterator.hasNext()) {
			throw GET_MULTIPLE_EXCEPTION.create();
		} else {
			return tag;
		}
	}

	private static int executeGet(ServerCommandSource serverCommandSource, DataCommandObject dataCommandObject, NbtPathArgumentType.class_2209 arg) throws CommandSyntaxException {
		Tag tag = getTag(arg, dataCommandObject);
		int i;
		if (tag instanceof AbstractNumberTag) {
			i = MathHelper.floor(((AbstractNumberTag)tag).getDouble());
		} else if (tag instanceof AbstractListTag) {
			i = ((AbstractListTag)tag).size();
		} else if (tag instanceof CompoundTag) {
			i = ((CompoundTag)tag).getSize();
		} else {
			if (!(tag instanceof StringTag)) {
				throw GET_UNKNOWN_EXCEPTION.create(arg.toString());
			}

			i = tag.asString().length();
		}

		serverCommandSource.sendFeedback(dataCommandObject.getQueryFeedback(tag), false);
		return i;
	}

	private static int executeGet(ServerCommandSource serverCommandSource, DataCommandObject dataCommandObject, NbtPathArgumentType.class_2209 arg, double d) throws CommandSyntaxException {
		Tag tag = getTag(arg, dataCommandObject);
		if (!(tag instanceof AbstractNumberTag)) {
			throw GET_INVALID_EXCEPTION.create(arg.toString());
		} else {
			int i = MathHelper.floor(((AbstractNumberTag)tag).getDouble() * d);
			serverCommandSource.sendFeedback(dataCommandObject.getGetFeedback(arg, d, i), false);
			return i;
		}
	}

	private static int executeGet(ServerCommandSource serverCommandSource, DataCommandObject dataCommandObject) throws CommandSyntaxException {
		serverCommandSource.sendFeedback(dataCommandObject.getQueryFeedback(dataCommandObject.getTag()), false);
		return 1;
	}

	private static int executeMerge(ServerCommandSource serverCommandSource, DataCommandObject dataCommandObject, CompoundTag compoundTag) throws CommandSyntaxException {
		CompoundTag compoundTag2 = dataCommandObject.getTag();
		CompoundTag compoundTag3 = compoundTag2.method_10553().copyFrom(compoundTag);
		if (compoundTag2.equals(compoundTag3)) {
			throw MERGE_FAILED_EXCEPTION.create();
		} else {
			dataCommandObject.setTag(compoundTag3);
			serverCommandSource.sendFeedback(dataCommandObject.getModifiedFeedback(), true);
			return 1;
		}
	}

	interface ModifyArgumentCreator {
		ArgumentBuilder<ServerCommandSource, ?> create(DataCommand.ModifyOperation modifyOperation);
	}

	interface ModifyOperation {
		int modify(CommandContext<ServerCommandSource> commandContext, CompoundTag compoundTag, NbtPathArgumentType.class_2209 arg, List<Tag> list) throws CommandSyntaxException;
	}

	public interface ObjectType {
		DataCommandObject getObject(CommandContext<ServerCommandSource> commandContext) throws CommandSyntaxException;

		ArgumentBuilder<ServerCommandSource, ?> addArgumentsToBuilder(
			ArgumentBuilder<ServerCommandSource, ?> argumentBuilder, Function<ArgumentBuilder<ServerCommandSource, ?>, ArgumentBuilder<ServerCommandSource, ?>> function
		);
	}
}
