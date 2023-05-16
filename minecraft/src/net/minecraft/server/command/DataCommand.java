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
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import net.minecraft.command.BlockDataObject;
import net.minecraft.command.DataCommandObject;
import net.minecraft.command.EntityDataObject;
import net.minecraft.command.StorageDataObject;
import net.minecraft.command.argument.NbtCompoundArgumentType;
import net.minecraft.command.argument.NbtElementArgumentType;
import net.minecraft.command.argument.NbtPathArgumentType;
import net.minecraft.nbt.AbstractNbtList;
import net.minecraft.nbt.AbstractNbtNumber;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtString;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

public class DataCommand {
	private static final SimpleCommandExceptionType MERGE_FAILED_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("commands.data.merge.failed"));
	private static final DynamicCommandExceptionType GET_INVALID_EXCEPTION = new DynamicCommandExceptionType(
		path -> Text.translatable("commands.data.get.invalid", path)
	);
	private static final DynamicCommandExceptionType GET_UNKNOWN_EXCEPTION = new DynamicCommandExceptionType(
		path -> Text.translatable("commands.data.get.unknown", path)
	);
	private static final SimpleCommandExceptionType GET_MULTIPLE_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("commands.data.get.multiple"));
	private static final DynamicCommandExceptionType MODIFY_EXPECTED_OBJECT_EXCEPTION = new DynamicCommandExceptionType(
		nbt -> Text.translatable("commands.data.modify.expected_object", nbt)
	);
	private static final DynamicCommandExceptionType MODIFY_EXPECTED_VALUE_EXCEPTION = new DynamicCommandExceptionType(
		nbt -> Text.translatable("commands.data.modify.expected_value", nbt)
	);
	private static final Dynamic2CommandExceptionType MODIFY_INVALID_SUBSTRING_EXCEPTION = new Dynamic2CommandExceptionType(
		(startIndex, endIndex) -> Text.translatable("commands.data.modify.invalid_substring", startIndex, endIndex)
	);
	public static final List<Function<String, DataCommand.ObjectType>> OBJECT_TYPE_FACTORIES = ImmutableList.of(
		EntityDataObject.TYPE_FACTORY, BlockDataObject.TYPE_FACTORY, StorageDataObject.TYPE_FACTORY
	);
	public static final List<DataCommand.ObjectType> TARGET_OBJECT_TYPES = (List<DataCommand.ObjectType>)OBJECT_TYPE_FACTORIES.stream()
		.map(factory -> (DataCommand.ObjectType)factory.apply("target"))
		.collect(ImmutableList.toImmutableList());
	public static final List<DataCommand.ObjectType> SOURCE_OBJECT_TYPES = (List<DataCommand.ObjectType>)OBJECT_TYPE_FACTORIES.stream()
		.map(factory -> (DataCommand.ObjectType)factory.apply("source"))
		.collect(ImmutableList.toImmutableList());

	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		LiteralArgumentBuilder<ServerCommandSource> literalArgumentBuilder = CommandManager.literal("data").requires(source -> source.hasPermissionLevel(2));

		for (DataCommand.ObjectType objectType : TARGET_OBJECT_TYPES) {
			literalArgumentBuilder.then(
					objectType.addArgumentsToBuilder(
						CommandManager.literal("merge"),
						builder -> builder.then(
								CommandManager.argument("nbt", NbtCompoundArgumentType.nbtCompound())
									.executes(context -> executeMerge(context.getSource(), objectType.getObject(context), NbtCompoundArgumentType.getNbtCompound(context, "nbt")))
							)
					)
				)
				.then(
					objectType.addArgumentsToBuilder(
						CommandManager.literal("get"),
						builder -> builder.executes(context -> executeGet((ServerCommandSource)context.getSource(), objectType.getObject(context)))
								.then(
									CommandManager.argument("path", NbtPathArgumentType.nbtPath())
										.executes(context -> executeGet(context.getSource(), objectType.getObject(context), NbtPathArgumentType.getNbtPath(context, "path")))
										.then(
											CommandManager.argument("scale", DoubleArgumentType.doubleArg())
												.executes(
													context -> executeGet(
															context.getSource(),
															objectType.getObject(context),
															NbtPathArgumentType.getNbtPath(context, "path"),
															DoubleArgumentType.getDouble(context, "scale")
														)
												)
										)
								)
					)
				)
				.then(
					objectType.addArgumentsToBuilder(
						CommandManager.literal("remove"),
						builder -> builder.then(
								CommandManager.argument("path", NbtPathArgumentType.nbtPath())
									.executes(context -> executeRemove(context.getSource(), objectType.getObject(context), NbtPathArgumentType.getNbtPath(context, "path")))
							)
					)
				)
				.then(
					addModifyArgument(
						(builder, modifier) -> builder.then(
									CommandManager.literal("insert")
										.then(
											CommandManager.argument("index", IntegerArgumentType.integer())
												.then(modifier.create((context, sourceNbt, path, elements) -> path.insert(IntegerArgumentType.getInteger(context, "index"), sourceNbt, elements)))
										)
								)
								.then(CommandManager.literal("prepend").then(modifier.create((context, sourceNbt, path, elements) -> path.insert(0, sourceNbt, elements))))
								.then(CommandManager.literal("append").then(modifier.create((context, sourceNbt, path, elements) -> path.insert(-1, sourceNbt, elements))))
								.then(CommandManager.literal("set").then(modifier.create((context, sourceNbt, path, elements) -> path.put(sourceNbt, Iterables.getLast(elements)))))
								.then(CommandManager.literal("merge").then(modifier.create((context, element, path, elements) -> {
									NbtCompound nbtCompound = new NbtCompound();

									for (NbtElement nbtElement : elements) {
										if (NbtPathArgumentType.NbtPath.isTooDeep(nbtElement, 0)) {
											throw NbtPathArgumentType.TOO_DEEP_EXCEPTION.create();
										}

										if (!(nbtElement instanceof NbtCompound nbtCompound2)) {
											throw MODIFY_EXPECTED_OBJECT_EXCEPTION.create(nbtElement);
										}

										nbtCompound.copyFrom(nbtCompound2);
									}

									Collection<NbtElement> collection = path.getOrInit(element, NbtCompound::new);
									int i = 0;

									for (NbtElement nbtElement2 : collection) {
										if (!(nbtElement2 instanceof NbtCompound nbtCompound3)) {
											throw MODIFY_EXPECTED_OBJECT_EXCEPTION.create(nbtElement2);
										}

										NbtCompound nbtCompound4 = nbtCompound3.copy();
										nbtCompound3.copyFrom(nbtCompound);
										i += nbtCompound4.equals(nbtCompound3) ? 0 : 1;
									}

									return i;
								})))
					)
				);
		}

		dispatcher.register(literalArgumentBuilder);
	}

	private static String asString(NbtElement nbt) throws CommandSyntaxException {
		if (nbt.getNbtType().isImmutable()) {
			return nbt.asString();
		} else {
			throw MODIFY_EXPECTED_VALUE_EXCEPTION.create(nbt);
		}
	}

	private static List<NbtElement> mapValues(List<NbtElement> list, DataCommand.Processor processor) throws CommandSyntaxException {
		List<NbtElement> list2 = new ArrayList(list.size());

		for (NbtElement nbtElement : list) {
			String string = asString(nbtElement);
			list2.add(NbtString.of(processor.process(string)));
		}

		return list2;
	}

	private static ArgumentBuilder<ServerCommandSource, ?> addModifyArgument(
		BiConsumer<ArgumentBuilder<ServerCommandSource, ?>, DataCommand.ModifyArgumentCreator> subArgumentAdder
	) {
		LiteralArgumentBuilder<ServerCommandSource> literalArgumentBuilder = CommandManager.literal("modify");

		for (DataCommand.ObjectType objectType : TARGET_OBJECT_TYPES) {
			objectType.addArgumentsToBuilder(
				literalArgumentBuilder,
				builder -> {
					ArgumentBuilder<ServerCommandSource, ?> argumentBuilder = CommandManager.argument("targetPath", NbtPathArgumentType.nbtPath());

					for (DataCommand.ObjectType objectType2 : SOURCE_OBJECT_TYPES) {
						subArgumentAdder.accept(
							argumentBuilder,
							(DataCommand.ModifyArgumentCreator)operation -> objectType2.addArgumentsToBuilder(
									CommandManager.literal("from"),
									builderx -> builderx.executes(context -> executeModify(context, objectType, operation, getValues(context, objectType2)))
											.then(
												CommandManager.argument("sourcePath", NbtPathArgumentType.nbtPath())
													.executes(context -> executeModify(context, objectType, operation, getValuesByPath(context, objectType2)))
											)
								)
						);
						subArgumentAdder.accept(
							argumentBuilder,
							(DataCommand.ModifyArgumentCreator)operation -> objectType2.addArgumentsToBuilder(
									CommandManager.literal("string"),
									builderx -> builderx.executes(context -> executeModify(context, objectType, operation, mapValues(getValues(context, objectType2), value -> value)))
											.then(
												CommandManager.argument("sourcePath", NbtPathArgumentType.nbtPath())
													.executes(context -> executeModify(context, objectType, operation, mapValues(getValuesByPath(context, objectType2), value -> value)))
													.then(
														CommandManager.argument("start", IntegerArgumentType.integer())
															.executes(
																context -> executeModify(
																		context,
																		objectType,
																		operation,
																		mapValues(getValuesByPath(context, objectType2), value -> substring(value, IntegerArgumentType.getInteger(context, "start")))
																	)
															)
															.then(
																CommandManager.argument("end", IntegerArgumentType.integer())
																	.executes(
																		context -> executeModify(
																				context,
																				objectType,
																				operation,
																				mapValues(
																					getValuesByPath(context, objectType2),
																					value -> substring(value, IntegerArgumentType.getInteger(context, "start"), IntegerArgumentType.getInteger(context, "end"))
																				)
																			)
																	)
															)
													)
											)
								)
						);
					}

					subArgumentAdder.accept(
						argumentBuilder,
						(DataCommand.ModifyArgumentCreator)modifier -> CommandManager.literal("value")
								.then(CommandManager.argument("value", NbtElementArgumentType.nbtElement()).executes(context -> {
									List<NbtElement> list = Collections.singletonList(NbtElementArgumentType.getNbtElement(context, "value"));
									return executeModify(context, objectType, modifier, list);
								}))
					);
					return builder.then(argumentBuilder);
				}
			);
		}

		return literalArgumentBuilder;
	}

	private static String substringInternal(String string, int startIndex, int endIndex) throws CommandSyntaxException {
		if (startIndex >= 0 && endIndex <= string.length() && startIndex <= endIndex) {
			return string.substring(startIndex, endIndex);
		} else {
			throw MODIFY_INVALID_SUBSTRING_EXCEPTION.create(startIndex, endIndex);
		}
	}

	private static String substring(String string, int startIndex, int endIndex) throws CommandSyntaxException {
		int i = string.length();
		int j = getSubstringIndex(startIndex, i);
		int k = getSubstringIndex(endIndex, i);
		return substringInternal(string, j, k);
	}

	private static String substring(String string, int startIndex) throws CommandSyntaxException {
		int i = string.length();
		return substringInternal(string, getSubstringIndex(startIndex, i), i);
	}

	private static int getSubstringIndex(int index, int length) {
		return index >= 0 ? index : length + index;
	}

	private static List<NbtElement> getValues(CommandContext<ServerCommandSource> context, DataCommand.ObjectType objectType) throws CommandSyntaxException {
		DataCommandObject dataCommandObject = objectType.getObject(context);
		return Collections.singletonList(dataCommandObject.getNbt());
	}

	private static List<NbtElement> getValuesByPath(CommandContext<ServerCommandSource> context, DataCommand.ObjectType objectType) throws CommandSyntaxException {
		DataCommandObject dataCommandObject = objectType.getObject(context);
		NbtPathArgumentType.NbtPath nbtPath = NbtPathArgumentType.getNbtPath(context, "sourcePath");
		return nbtPath.get(dataCommandObject.getNbt());
	}

	private static int executeModify(
		CommandContext<ServerCommandSource> context, DataCommand.ObjectType objectType, DataCommand.ModifyOperation modifier, List<NbtElement> elements
	) throws CommandSyntaxException {
		DataCommandObject dataCommandObject = objectType.getObject(context);
		NbtPathArgumentType.NbtPath nbtPath = NbtPathArgumentType.getNbtPath(context, "targetPath");
		NbtCompound nbtCompound = dataCommandObject.getNbt();
		int i = modifier.modify(context, nbtCompound, nbtPath, elements);
		if (i == 0) {
			throw MERGE_FAILED_EXCEPTION.create();
		} else {
			dataCommandObject.setNbt(nbtCompound);
			context.getSource().sendFeedback(() -> dataCommandObject.feedbackModify(), true);
			return i;
		}
	}

	private static int executeRemove(ServerCommandSource source, DataCommandObject object, NbtPathArgumentType.NbtPath path) throws CommandSyntaxException {
		NbtCompound nbtCompound = object.getNbt();
		int i = path.remove(nbtCompound);
		if (i == 0) {
			throw MERGE_FAILED_EXCEPTION.create();
		} else {
			object.setNbt(nbtCompound);
			source.sendFeedback(() -> object.feedbackModify(), true);
			return i;
		}
	}

	private static NbtElement getNbt(NbtPathArgumentType.NbtPath path, DataCommandObject object) throws CommandSyntaxException {
		Collection<NbtElement> collection = path.get(object.getNbt());
		Iterator<NbtElement> iterator = collection.iterator();
		NbtElement nbtElement = (NbtElement)iterator.next();
		if (iterator.hasNext()) {
			throw GET_MULTIPLE_EXCEPTION.create();
		} else {
			return nbtElement;
		}
	}

	private static int executeGet(ServerCommandSource source, DataCommandObject object, NbtPathArgumentType.NbtPath path) throws CommandSyntaxException {
		NbtElement nbtElement = getNbt(path, object);
		int i;
		if (nbtElement instanceof AbstractNbtNumber) {
			i = MathHelper.floor(((AbstractNbtNumber)nbtElement).doubleValue());
		} else if (nbtElement instanceof AbstractNbtList) {
			i = ((AbstractNbtList)nbtElement).size();
		} else if (nbtElement instanceof NbtCompound) {
			i = ((NbtCompound)nbtElement).getSize();
		} else {
			if (!(nbtElement instanceof NbtString)) {
				throw GET_UNKNOWN_EXCEPTION.create(path.toString());
			}

			i = nbtElement.asString().length();
		}

		source.sendFeedback(() -> object.feedbackQuery(nbtElement), false);
		return i;
	}

	private static int executeGet(ServerCommandSource source, DataCommandObject object, NbtPathArgumentType.NbtPath path, double scale) throws CommandSyntaxException {
		NbtElement nbtElement = getNbt(path, object);
		if (!(nbtElement instanceof AbstractNbtNumber)) {
			throw GET_INVALID_EXCEPTION.create(path.toString());
		} else {
			int i = MathHelper.floor(((AbstractNbtNumber)nbtElement).doubleValue() * scale);
			source.sendFeedback(() -> object.feedbackGet(path, scale, i), false);
			return i;
		}
	}

	private static int executeGet(ServerCommandSource source, DataCommandObject object) throws CommandSyntaxException {
		NbtCompound nbtCompound = object.getNbt();
		source.sendFeedback(() -> object.feedbackQuery(nbtCompound), false);
		return 1;
	}

	private static int executeMerge(ServerCommandSource source, DataCommandObject object, NbtCompound nbt) throws CommandSyntaxException {
		NbtCompound nbtCompound = object.getNbt();
		if (NbtPathArgumentType.NbtPath.isTooDeep(nbt, 0)) {
			throw NbtPathArgumentType.TOO_DEEP_EXCEPTION.create();
		} else {
			NbtCompound nbtCompound2 = nbtCompound.copy().copyFrom(nbt);
			if (nbtCompound.equals(nbtCompound2)) {
				throw MERGE_FAILED_EXCEPTION.create();
			} else {
				object.setNbt(nbtCompound2);
				source.sendFeedback(() -> object.feedbackModify(), true);
				return 1;
			}
		}
	}

	@FunctionalInterface
	interface ModifyArgumentCreator {
		ArgumentBuilder<ServerCommandSource, ?> create(DataCommand.ModifyOperation modifier);
	}

	@FunctionalInterface
	interface ModifyOperation {
		int modify(CommandContext<ServerCommandSource> context, NbtCompound sourceNbt, NbtPathArgumentType.NbtPath path, List<NbtElement> elements) throws CommandSyntaxException;
	}

	public interface ObjectType {
		DataCommandObject getObject(CommandContext<ServerCommandSource> context) throws CommandSyntaxException;

		ArgumentBuilder<ServerCommandSource, ?> addArgumentsToBuilder(
			ArgumentBuilder<ServerCommandSource, ?> argument, Function<ArgumentBuilder<ServerCommandSource, ?>, ArgumentBuilder<ServerCommandSource, ?>> argumentAdder
		);
	}

	@FunctionalInterface
	interface Processor {
		String process(String string) throws CommandSyntaxException;
	}
}
