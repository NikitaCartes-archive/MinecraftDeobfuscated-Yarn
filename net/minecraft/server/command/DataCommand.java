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
import net.minecraft.command.arguments.NbtCompoundTagArgumentType;
import net.minecraft.command.arguments.NbtPathArgumentType;
import net.minecraft.command.arguments.NbtTagArgumentType;
import net.minecraft.nbt.AbstractListTag;
import net.minecraft.nbt.AbstractNumberTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.MathHelper;

public class DataCommand {
    private static final SimpleCommandExceptionType MERGE_FAILED_EXCEPTION = new SimpleCommandExceptionType(new TranslatableText("commands.data.merge.failed", new Object[0]));
    private static final DynamicCommandExceptionType GET_INVALID_EXCEPTION = new DynamicCommandExceptionType(object -> new TranslatableText("commands.data.get.invalid", object));
    private static final DynamicCommandExceptionType GET_UNKNOWN_EXCEPTION = new DynamicCommandExceptionType(object -> new TranslatableText("commands.data.get.unknown", object));
    private static final SimpleCommandExceptionType GET_MULTIPLE_EXCEPTION = new SimpleCommandExceptionType(new TranslatableText("commands.data.get.multiple", new Object[0]));
    private static final DynamicCommandExceptionType MODIFY_EXPECTED_LIST_EXCEPTION = new DynamicCommandExceptionType(object -> new TranslatableText("commands.data.modify.expected_list", object));
    private static final DynamicCommandExceptionType MODIFY_EXPECTED_OBJECT_EXCEPTION = new DynamicCommandExceptionType(object -> new TranslatableText("commands.data.modify.expected_object", object));
    private static final DynamicCommandExceptionType MODIFY_INVALID_INDEX_EXCEPTION = new DynamicCommandExceptionType(object -> new TranslatableText("commands.data.modify.invalid_index", object));
    public static final List<Function<String, ObjectType>> OBJECT_TYPE_FACTORIES = ImmutableList.of(EntityDataObject.TYPE_FACTORY, BlockDataObject.TYPE_FACTORY, StorageDataObject.TYPE_FACTORY);
    public static final List<ObjectType> TARGET_OBJECT_TYPES = OBJECT_TYPE_FACTORIES.stream().map(function -> (ObjectType)function.apply("target")).collect(ImmutableList.toImmutableList());
    public static final List<ObjectType> SOURCE_OBJECT_TYPES = OBJECT_TYPE_FACTORIES.stream().map(function -> (ObjectType)function.apply("source")).collect(ImmutableList.toImmutableList());

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        LiteralArgumentBuilder literalArgumentBuilder = (LiteralArgumentBuilder)CommandManager.literal("data").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2));
        for (ObjectType objectType : TARGET_OBJECT_TYPES) {
            ((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)literalArgumentBuilder.then(objectType.addArgumentsToBuilder(CommandManager.literal("merge"), argumentBuilder -> argumentBuilder.then(CommandManager.argument("nbt", NbtCompoundTagArgumentType.nbtCompound()).executes(commandContext -> DataCommand.executeMerge((ServerCommandSource)commandContext.getSource(), objectType.getObject(commandContext), NbtCompoundTagArgumentType.getCompoundTag(commandContext, "nbt"))))))).then(objectType.addArgumentsToBuilder(CommandManager.literal("get"), argumentBuilder -> ((ArgumentBuilder)argumentBuilder.executes(commandContext -> DataCommand.executeGet((ServerCommandSource)commandContext.getSource(), objectType.getObject(commandContext)))).then(((RequiredArgumentBuilder)CommandManager.argument("path", NbtPathArgumentType.nbtPath()).executes(commandContext -> DataCommand.executeGet((ServerCommandSource)commandContext.getSource(), objectType.getObject(commandContext), NbtPathArgumentType.getNbtPath(commandContext, "path")))).then(CommandManager.argument("scale", DoubleArgumentType.doubleArg()).executes(commandContext -> DataCommand.executeGet((ServerCommandSource)commandContext.getSource(), objectType.getObject(commandContext), NbtPathArgumentType.getNbtPath(commandContext, "path"), DoubleArgumentType.getDouble(commandContext, "scale")))))))).then(objectType.addArgumentsToBuilder(CommandManager.literal("remove"), argumentBuilder -> argumentBuilder.then(CommandManager.argument("path", NbtPathArgumentType.nbtPath()).executes(commandContext -> DataCommand.executeRemove((ServerCommandSource)commandContext.getSource(), objectType.getObject(commandContext), NbtPathArgumentType.getNbtPath(commandContext, "path"))))))).then(DataCommand.addModifyArgument((argumentBuilder, modifyArgumentCreator) -> ((ArgumentBuilder)((ArgumentBuilder)((ArgumentBuilder)((ArgumentBuilder)argumentBuilder.then(CommandManager.literal("insert").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("index", IntegerArgumentType.integer()).then(modifyArgumentCreator.create((commandContext, compoundTag, nbtPath, list) -> {
                int i = IntegerArgumentType.getInteger(commandContext, "index");
                return DataCommand.executeInsert(i, compoundTag, nbtPath, list);
            }))))).then(CommandManager.literal("prepend").then(modifyArgumentCreator.create((commandContext, compoundTag, nbtPath, list) -> DataCommand.executeInsert(0, compoundTag, nbtPath, list))))).then(CommandManager.literal("append").then(modifyArgumentCreator.create((commandContext, compoundTag, nbtPath, list) -> DataCommand.executeInsert(-1, compoundTag, nbtPath, list))))).then(CommandManager.literal("set").then(modifyArgumentCreator.create((commandContext, compoundTag, nbtPath, list) -> nbtPath.put(compoundTag, ((Tag)Iterables.getLast(list))::copy))))).then(CommandManager.literal("merge").then(modifyArgumentCreator.create((commandContext, compoundTag, nbtPath, list) -> {
                List<Tag> collection = nbtPath.getOrInit(compoundTag, CompoundTag::new);
                int i = 0;
                for (Tag tag : collection) {
                    if (!(tag instanceof CompoundTag)) {
                        throw MODIFY_EXPECTED_OBJECT_EXCEPTION.create(tag);
                    }
                    CompoundTag compoundTag2 = (CompoundTag)tag;
                    CompoundTag compoundTag3 = compoundTag2.copy();
                    for (Tag tag2 : list) {
                        if (!(tag2 instanceof CompoundTag)) {
                            throw MODIFY_EXPECTED_OBJECT_EXCEPTION.create(tag2);
                        }
                        compoundTag2.copyFrom((CompoundTag)tag2);
                    }
                    i += compoundTag3.equals(compoundTag2) ? 0 : 1;
                }
                return i;
            })))));
        }
        dispatcher.register(literalArgumentBuilder);
    }

    private static int executeInsert(int integer, CompoundTag sourceTag, NbtPathArgumentType.NbtPath path, List<Tag> tags) throws CommandSyntaxException {
        List<Tag> collection = path.getOrInit(sourceTag, ListTag::new);
        int i = 0;
        for (Tag tag : collection) {
            if (!(tag instanceof AbstractListTag)) {
                throw MODIFY_EXPECTED_LIST_EXCEPTION.create(tag);
            }
            boolean bl = false;
            AbstractListTag abstractListTag = (AbstractListTag)tag;
            int j = integer < 0 ? abstractListTag.size() + integer + 1 : integer;
            for (Tag tag2 : tags) {
                try {
                    if (!abstractListTag.addTag(j, tag2.copy())) continue;
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
                        List<Tag> list = Collections.singletonList(objectType2.getObject(commandContext).getTag());
                        return DataCommand.executeModify(commandContext, objectType, modifyOperation, list);
                    })).then(CommandManager.argument("sourcePath", NbtPathArgumentType.nbtPath()).executes(commandContext -> {
                        DataCommandObject dataCommandObject = objectType2.getObject(commandContext);
                        NbtPathArgumentType.NbtPath nbtPath = NbtPathArgumentType.getNbtPath(commandContext, "sourcePath");
                        List<Tag> list = nbtPath.get(dataCommandObject.getTag());
                        return DataCommand.executeModify(commandContext, objectType, modifyOperation, list);
                    }))));
                }
                subArgumentAdder.accept(argumentBuilder2, modifyOperation -> (LiteralArgumentBuilder)CommandManager.literal("value").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("value", NbtTagArgumentType.nbtTag()).executes(commandContext -> {
                    List<Tag> list = Collections.singletonList(NbtTagArgumentType.getTag(commandContext, "value"));
                    return DataCommand.executeModify(commandContext, objectType, modifyOperation, list);
                })));
                return argumentBuilder.then(argumentBuilder2);
            });
        }
        return literalArgumentBuilder;
    }

    private static int executeModify(CommandContext<ServerCommandSource> context, ObjectType objectType, ModifyOperation modifier, List<Tag> tags) throws CommandSyntaxException {
        DataCommandObject dataCommandObject = objectType.getObject(context);
        NbtPathArgumentType.NbtPath nbtPath = NbtPathArgumentType.getNbtPath(context, "targetPath");
        CompoundTag compoundTag = dataCommandObject.getTag();
        int i = modifier.modify(context, compoundTag, nbtPath, tags);
        if (i == 0) {
            throw MERGE_FAILED_EXCEPTION.create();
        }
        dataCommandObject.setTag(compoundTag);
        context.getSource().sendFeedback(dataCommandObject.feedbackModify(), true);
        return i;
    }

    private static int executeRemove(ServerCommandSource source, DataCommandObject object, NbtPathArgumentType.NbtPath path) throws CommandSyntaxException {
        CompoundTag compoundTag = object.getTag();
        int i = path.remove(compoundTag);
        if (i == 0) {
            throw MERGE_FAILED_EXCEPTION.create();
        }
        object.setTag(compoundTag);
        source.sendFeedback(object.feedbackModify(), true);
        return i;
    }

    private static Tag getTag(NbtPathArgumentType.NbtPath path, DataCommandObject object) throws CommandSyntaxException {
        List<Tag> collection = path.get(object.getTag());
        Iterator iterator = collection.iterator();
        Tag tag = (Tag)iterator.next();
        if (iterator.hasNext()) {
            throw GET_MULTIPLE_EXCEPTION.create();
        }
        return tag;
    }

    private static int executeGet(ServerCommandSource source, DataCommandObject object, NbtPathArgumentType.NbtPath path) throws CommandSyntaxException {
        int i;
        Tag tag = DataCommand.getTag(path, object);
        if (tag instanceof AbstractNumberTag) {
            i = MathHelper.floor(((AbstractNumberTag)tag).getDouble());
        } else if (tag instanceof AbstractListTag) {
            i = ((AbstractListTag)tag).size();
        } else if (tag instanceof CompoundTag) {
            i = ((CompoundTag)tag).getSize();
        } else if (tag instanceof StringTag) {
            i = tag.asString().length();
        } else {
            throw GET_UNKNOWN_EXCEPTION.create(path.toString());
        }
        source.sendFeedback(object.feedbackQuery(tag), false);
        return i;
    }

    private static int executeGet(ServerCommandSource source, DataCommandObject object, NbtPathArgumentType.NbtPath path, double scale) throws CommandSyntaxException {
        Tag tag = DataCommand.getTag(path, object);
        if (!(tag instanceof AbstractNumberTag)) {
            throw GET_INVALID_EXCEPTION.create(path.toString());
        }
        int i = MathHelper.floor(((AbstractNumberTag)tag).getDouble() * scale);
        source.sendFeedback(object.feedbackGet(path, scale, i), false);
        return i;
    }

    private static int executeGet(ServerCommandSource source, DataCommandObject object) throws CommandSyntaxException {
        source.sendFeedback(object.feedbackQuery(object.getTag()), false);
        return 1;
    }

    private static int executeMerge(ServerCommandSource source, DataCommandObject object, CompoundTag tag) throws CommandSyntaxException {
        CompoundTag compoundTag2;
        CompoundTag compoundTag = object.getTag();
        if (compoundTag.equals(compoundTag2 = compoundTag.copy().copyFrom(tag))) {
            throw MERGE_FAILED_EXCEPTION.create();
        }
        object.setTag(compoundTag2);
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
        public int modify(CommandContext<ServerCommandSource> var1, CompoundTag var2, NbtPathArgumentType.NbtPath var3, List<Tag> var4) throws CommandSyntaxException;
    }
}

