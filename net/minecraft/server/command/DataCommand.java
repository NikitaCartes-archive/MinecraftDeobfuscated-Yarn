/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.server.command;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
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
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.MathHelper;

public class DataCommand {
    private static final SimpleCommandExceptionType MERGE_FAILED_EXCEPTION = new SimpleCommandExceptionType(new TranslatableText("commands.data.merge.failed"));
    private static final DynamicCommandExceptionType GET_INVALID_EXCEPTION = new DynamicCommandExceptionType(object -> new TranslatableText("commands.data.get.invalid", object));
    private static final DynamicCommandExceptionType GET_UNKNOWN_EXCEPTION = new DynamicCommandExceptionType(object -> new TranslatableText("commands.data.get.unknown", object));
    private static final SimpleCommandExceptionType GET_MULTIPLE_EXCEPTION = new SimpleCommandExceptionType(new TranslatableText("commands.data.get.multiple"));
    private static final DynamicCommandExceptionType MODIFY_EXPECTED_LIST_EXCEPTION = new DynamicCommandExceptionType(object -> new TranslatableText("commands.data.modify.expected_list", object));
    private static final DynamicCommandExceptionType MODIFY_EXPECTED_OBJECT_EXCEPTION = new DynamicCommandExceptionType(object -> new TranslatableText("commands.data.modify.expected_object", object));
    private static final DynamicCommandExceptionType MODIFY_INVALID_INDEX_EXCEPTION = new DynamicCommandExceptionType(object -> new TranslatableText("commands.data.modify.invalid_index", object));
    public static final List<Function<String, ObjectType>> OBJECT_TYPE_FACTORIES = ImmutableList.of(EntityDataObject.TYPE_FACTORY, BlockDataObject.TYPE_FACTORY, StorageDataObject.TYPE_FACTORY);
    public static final List<ObjectType> TARGET_OBJECT_TYPES = OBJECT_TYPE_FACTORIES.stream().map(function -> (ObjectType)function.apply("target")).collect(ImmutableList.toImmutableList());
    public static final List<ObjectType> SOURCE_OBJECT_TYPES = OBJECT_TYPE_FACTORIES.stream().map(function -> (ObjectType)function.apply("source")).collect(ImmutableList.toImmutableList());

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        LiteralArgumentBuilder literalArgumentBuilder = (LiteralArgumentBuilder)CommandManager.literal("data").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2));
        for (ObjectType objectType : TARGET_OBJECT_TYPES) {
            ((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)literalArgumentBuilder.then(objectType.addArgumentsToBuilder(CommandManager.literal("merge"), argumentBuilder -> argumentBuilder.then(CommandManager.argument("nbt", NbtCompoundArgumentType.nbtCompound()).executes(commandContext -> DataCommand.executeMerge((ServerCommandSource)commandContext.getSource(), objectType.getObject(commandContext), NbtCompoundArgumentType.getNbtCompound(commandContext, "nbt"))))))).then(objectType.addArgumentsToBuilder(CommandManager.literal("get"), argumentBuilder -> ((ArgumentBuilder)argumentBuilder.executes(commandContext -> DataCommand.executeGet((ServerCommandSource)commandContext.getSource(), objectType.getObject(commandContext)))).then(((RequiredArgumentBuilder)CommandManager.argument("path", NbtPathArgumentType.nbtPath()).executes(commandContext -> DataCommand.executeGet((ServerCommandSource)commandContext.getSource(), objectType.getObject(commandContext), NbtPathArgumentType.getNbtPath(commandContext, "path")))).then(CommandManager.argument("scale", DoubleArgumentType.doubleArg()).executes(commandContext -> DataCommand.executeGet((ServerCommandSource)commandContext.getSource(), objectType.getObject(commandContext), NbtPathArgumentType.getNbtPath(commandContext, "path"), DoubleArgumentType.getDouble(commandContext, "scale")))))))).then(objectType.addArgumentsToBuilder(CommandManager.literal("remove"), argumentBuilder -> argumentBuilder.then(CommandManager.argument("path", NbtPathArgumentType.nbtPath()).executes(commandContext -> DataCommand.executeRemove((ServerCommandSource)commandContext.getSource(), objectType.getObject(commandContext), NbtPathArgumentType.getNbtPath(commandContext, "path"))))))).then(DataCommand.addModifyArgument((argumentBuilder, modifyArgumentCreator) -> ((ArgumentBuilder)((ArgumentBuilder)((ArgumentBuilder)((ArgumentBuilder)argumentBuilder.then(CommandManager.literal("insert").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("index", IntegerArgumentType.integer()).then(modifyArgumentCreator.create((commandContext, nbtCompound, nbtPath, list) -> {
                int i = IntegerArgumentType.getInteger(commandContext, "index");
                return DataCommand.executeInsert(i, nbtCompound, nbtPath, list);
            }))))).then(CommandManager.literal("prepend").then(modifyArgumentCreator.create((commandContext, nbtCompound, nbtPath, list) -> DataCommand.executeInsert(0, nbtCompound, nbtPath, list))))).then(CommandManager.literal("append").then(modifyArgumentCreator.create((commandContext, nbtCompound, nbtPath, list) -> DataCommand.executeInsert(-1, nbtCompound, nbtPath, list))))).then(CommandManager.literal("set").then(modifyArgumentCreator.create((commandContext, nbtCompound, nbtPath, list) -> nbtPath.put(nbtCompound, ((NbtElement)Iterables.getLast(list))::copy))))).then(CommandManager.literal("merge").then(modifyArgumentCreator.create((commandContext, nbtCompound, nbtPath, list) -> {
                List<NbtElement> collection = nbtPath.getOrInit(nbtCompound, NbtCompound::new);
                int i = 0;
                for (NbtElement nbtElement : collection) {
                    if (!(nbtElement instanceof NbtCompound)) {
                        throw MODIFY_EXPECTED_OBJECT_EXCEPTION.create(nbtElement);
                    }
                    NbtCompound nbtCompound2 = (NbtCompound)nbtElement;
                    NbtCompound nbtCompound3 = nbtCompound2.copy();
                    for (NbtElement nbtElement2 : list) {
                        if (!(nbtElement2 instanceof NbtCompound)) {
                            throw MODIFY_EXPECTED_OBJECT_EXCEPTION.create(nbtElement2);
                        }
                        nbtCompound2.copyFrom((NbtCompound)nbtElement2);
                    }
                    i += nbtCompound3.equals(nbtCompound2) ? 0 : 1;
                }
                return i;
            })))));
        }
        dispatcher.register(literalArgumentBuilder);
    }

    private static int executeInsert(int integer, NbtCompound sourceNbt, NbtPathArgumentType.NbtPath path, List<NbtElement> elements) throws CommandSyntaxException {
        List<NbtElement> collection = path.getOrInit(sourceNbt, NbtList::new);
        int i = 0;
        for (NbtElement nbtElement : collection) {
            if (!(nbtElement instanceof AbstractNbtList)) {
                throw MODIFY_EXPECTED_LIST_EXCEPTION.create(nbtElement);
            }
            boolean bl = false;
            AbstractNbtList abstractNbtList = (AbstractNbtList)nbtElement;
            int j = integer < 0 ? abstractNbtList.size() + integer + 1 : integer;
            for (NbtElement nbtElement2 : elements) {
                try {
                    if (!abstractNbtList.addElement(j, nbtElement2.copy())) continue;
                    ++j;
                    bl = true;
                } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
                    throw MODIFY_INVALID_INDEX_EXCEPTION.create(j);
                }
            }
            i += bl ? 1 : 0;
        }
        return i;
    }

    private static ArgumentBuilder<ServerCommandSource, ?> addModifyArgument(BiConsumer<ArgumentBuilder<ServerCommandSource, ?>, ModifyArgumentCreator> subArgumentAdder) {
        LiteralArgumentBuilder<ServerCommandSource> literalArgumentBuilder = CommandManager.literal("modify");
        for (ObjectType objectType : TARGET_OBJECT_TYPES) {
            objectType.addArgumentsToBuilder(literalArgumentBuilder, argumentBuilder -> {
                RequiredArgumentBuilder<ServerCommandSource, NbtPathArgumentType.NbtPath> argumentBuilder2 = CommandManager.argument("targetPath", NbtPathArgumentType.nbtPath());
                for (ObjectType objectType2 : SOURCE_OBJECT_TYPES) {
                    subArgumentAdder.accept(argumentBuilder2, modifyOperation -> objectType2.addArgumentsToBuilder(CommandManager.literal("from"), argumentBuilder -> ((ArgumentBuilder)argumentBuilder.executes(commandContext -> {
                        List<NbtElement> list = Collections.singletonList(objectType2.getObject(commandContext).getNbt());
                        return DataCommand.executeModify(commandContext, objectType, modifyOperation, list);
                    })).then(CommandManager.argument("sourcePath", NbtPathArgumentType.nbtPath()).executes(commandContext -> {
                        DataCommandObject dataCommandObject = objectType2.getObject(commandContext);
                        NbtPathArgumentType.NbtPath nbtPath = NbtPathArgumentType.getNbtPath(commandContext, "sourcePath");
                        List<NbtElement> list = nbtPath.get(dataCommandObject.getNbt());
                        return DataCommand.executeModify(commandContext, objectType, modifyOperation, list);
                    }))));
                }
                subArgumentAdder.accept(argumentBuilder2, modifyOperation -> (LiteralArgumentBuilder)CommandManager.literal("value").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("value", NbtElementArgumentType.nbtElement()).executes(commandContext -> {
                    List<NbtElement> list = Collections.singletonList(NbtElementArgumentType.getNbtElement(commandContext, "value"));
                    return DataCommand.executeModify(commandContext, objectType, modifyOperation, list);
                })));
                return argumentBuilder.then(argumentBuilder2);
            });
        }
        return literalArgumentBuilder;
    }

    private static int executeModify(CommandContext<ServerCommandSource> context, ObjectType objectType, ModifyOperation modifier, List<NbtElement> elements) throws CommandSyntaxException {
        DataCommandObject dataCommandObject = objectType.getObject(context);
        NbtPathArgumentType.NbtPath nbtPath = NbtPathArgumentType.getNbtPath(context, "targetPath");
        NbtCompound nbtCompound = dataCommandObject.getNbt();
        int i = modifier.modify(context, nbtCompound, nbtPath, elements);
        if (i == 0) {
            throw MERGE_FAILED_EXCEPTION.create();
        }
        dataCommandObject.setNbt(nbtCompound);
        context.getSource().sendFeedback(dataCommandObject.feedbackModify(), true);
        return i;
    }

    private static int executeRemove(ServerCommandSource source, DataCommandObject object, NbtPathArgumentType.NbtPath path) throws CommandSyntaxException {
        NbtCompound nbtCompound = object.getNbt();
        int i = path.remove(nbtCompound);
        if (i == 0) {
            throw MERGE_FAILED_EXCEPTION.create();
        }
        object.setNbt(nbtCompound);
        source.sendFeedback(object.feedbackModify(), true);
        return i;
    }

    private static NbtElement getNbt(NbtPathArgumentType.NbtPath path, DataCommandObject object) throws CommandSyntaxException {
        List<NbtElement> collection = path.get(object.getNbt());
        Iterator iterator = collection.iterator();
        NbtElement nbtElement = (NbtElement)iterator.next();
        if (iterator.hasNext()) {
            throw GET_MULTIPLE_EXCEPTION.create();
        }
        return nbtElement;
    }

    private static int executeGet(ServerCommandSource source, DataCommandObject object, NbtPathArgumentType.NbtPath path) throws CommandSyntaxException {
        int i;
        NbtElement nbtElement = DataCommand.getNbt(path, object);
        if (nbtElement instanceof AbstractNbtNumber) {
            i = MathHelper.floor(((AbstractNbtNumber)nbtElement).doubleValue());
        } else if (nbtElement instanceof AbstractNbtList) {
            i = ((AbstractNbtList)nbtElement).size();
        } else if (nbtElement instanceof NbtCompound) {
            i = ((NbtCompound)nbtElement).getSize();
        } else if (nbtElement instanceof NbtString) {
            i = nbtElement.asString().length();
        } else {
            throw GET_UNKNOWN_EXCEPTION.create(path.toString());
        }
        source.sendFeedback(object.feedbackQuery(nbtElement), false);
        return i;
    }

    private static int executeGet(ServerCommandSource source, DataCommandObject object, NbtPathArgumentType.NbtPath path, double scale) throws CommandSyntaxException {
        NbtElement nbtElement = DataCommand.getNbt(path, object);
        if (!(nbtElement instanceof AbstractNbtNumber)) {
            throw GET_INVALID_EXCEPTION.create(path.toString());
        }
        int i = MathHelper.floor(((AbstractNbtNumber)nbtElement).doubleValue() * scale);
        source.sendFeedback(object.feedbackGet(path, scale, i), false);
        return i;
    }

    private static int executeGet(ServerCommandSource source, DataCommandObject object) throws CommandSyntaxException {
        source.sendFeedback(object.feedbackQuery(object.getNbt()), false);
        return 1;
    }

    private static int executeMerge(ServerCommandSource source, DataCommandObject object, NbtCompound nbt) throws CommandSyntaxException {
        NbtCompound nbtCompound2;
        NbtCompound nbtCompound = object.getNbt();
        if (nbtCompound.equals(nbtCompound2 = nbtCompound.copy().copyFrom(nbt))) {
            throw MERGE_FAILED_EXCEPTION.create();
        }
        object.setNbt(nbtCompound2);
        source.sendFeedback(object.feedbackModify(), true);
        return 1;
    }

    public static interface ObjectType {
        public DataCommandObject getObject(CommandContext<ServerCommandSource> var1) throws CommandSyntaxException;

        public ArgumentBuilder<ServerCommandSource, ?> addArgumentsToBuilder(ArgumentBuilder<ServerCommandSource, ?> var1, Function<ArgumentBuilder<ServerCommandSource, ?>, ArgumentBuilder<ServerCommandSource, ?>> var2);
    }

    static interface ModifyArgumentCreator {
        public ArgumentBuilder<ServerCommandSource, ?> create(ModifyOperation var1);
    }

    static interface ModifyOperation {
        public int modify(CommandContext<ServerCommandSource> var1, NbtCompound var2, NbtPathArgumentType.NbtPath var3, List<NbtElement> var4) throws CommandSyntaxException;
    }
}

