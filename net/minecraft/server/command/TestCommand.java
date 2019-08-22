/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.StructureBlockBlockEntity;
import net.minecraft.class_4517;
import net.minecraft.class_4518;
import net.minecraft.class_4519;
import net.minecraft.class_4520;
import net.minecraft.class_4521;
import net.minecraft.class_4524;
import net.minecraft.class_4525;
import net.minecraft.class_4526;
import net.minecraft.class_4530;
import net.minecraft.client.network.DebugRendererInfoManager;
import net.minecraft.command.arguments.BlockStateArgument;
import net.minecraft.data.dev.NbtProvider;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.test.TestFunction;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.Nullable;

public class TestCommand {
    public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
        commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("test").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.literal("runthis").executes(commandContext -> TestCommand.method_22277((ServerCommandSource)commandContext.getSource())))).then(CommandManager.literal("runthese").executes(commandContext -> TestCommand.method_22281((ServerCommandSource)commandContext.getSource())))).then(CommandManager.literal("run").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("testName", new class_4530()).executes(commandContext -> TestCommand.method_22266((ServerCommandSource)commandContext.getSource(), class_4530.method_22303(commandContext, "testName")))))).then(((LiteralArgumentBuilder)CommandManager.literal("runall").executes(commandContext -> TestCommand.method_22284((ServerCommandSource)commandContext.getSource()))).then(CommandManager.argument("testClassName", new class_4526()).executes(commandContext -> TestCommand.method_22267((ServerCommandSource)commandContext.getSource(), class_4526.method_22262(commandContext, "testClassName")))))).then(CommandManager.literal("export").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("testName", StringArgumentType.word()).executes(commandContext -> TestCommand.method_22282((ServerCommandSource)commandContext.getSource(), StringArgumentType.getString(commandContext, "testName")))))).then(CommandManager.literal("import").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("testName", StringArgumentType.word()).executes(commandContext -> TestCommand.method_22285((ServerCommandSource)commandContext.getSource(), StringArgumentType.getString(commandContext, "testName")))))).then(CommandManager.literal("pos").executes(commandContext -> TestCommand.method_22264((ServerCommandSource)commandContext.getSource())))).then(CommandManager.literal("create").then((ArgumentBuilder<ServerCommandSource, ?>)((RequiredArgumentBuilder)CommandManager.argument("testName", StringArgumentType.word()).executes(commandContext -> TestCommand.method_22268((ServerCommandSource)commandContext.getSource(), StringArgumentType.getString(commandContext, "testName"), 5, 5, 5))).then(((RequiredArgumentBuilder)CommandManager.argument("width", IntegerArgumentType.integer()).executes(commandContext -> TestCommand.method_22268((ServerCommandSource)commandContext.getSource(), StringArgumentType.getString(commandContext, "testName"), IntegerArgumentType.getInteger(commandContext, "width"), IntegerArgumentType.getInteger(commandContext, "width"), IntegerArgumentType.getInteger(commandContext, "width")))).then(CommandManager.argument("height", IntegerArgumentType.integer()).then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("depth", IntegerArgumentType.integer()).executes(commandContext -> TestCommand.method_22268((ServerCommandSource)commandContext.getSource(), StringArgumentType.getString(commandContext, "testName"), IntegerArgumentType.getInteger(commandContext, "width"), IntegerArgumentType.getInteger(commandContext, "height"), IntegerArgumentType.getInteger(commandContext, "depth"))))))))).then(((LiteralArgumentBuilder)CommandManager.literal("clearall").executes(commandContext -> TestCommand.method_22265((ServerCommandSource)commandContext.getSource(), 200))).then(CommandManager.argument("radius", IntegerArgumentType.integer()).executes(commandContext -> TestCommand.method_22265((ServerCommandSource)commandContext.getSource(), IntegerArgumentType.getInteger(commandContext, "radius"))))));
    }

    private static int method_22268(ServerCommandSource serverCommandSource, String string, int i, int j, int k) {
        if (i > 32 || j > 32 || k > 32) {
            throw new IllegalArgumentException("The structure must be less than 32 blocks big in each axis");
        }
        ServerWorld serverWorld = serverCommandSource.getWorld();
        BlockPos blockPos = new BlockPos(serverCommandSource.getPosition());
        BlockPos blockPos2 = new BlockPos(blockPos.getX(), 4, blockPos.getZ() + 3);
        class_4525.method_22251(string.toLowerCase(), blockPos2, new BlockPos(i, j, k), 2, serverWorld);
        for (int l = 0; l < i; ++l) {
            for (int m = 0; m < k; ++m) {
                BlockPos blockPos3 = new BlockPos(blockPos2.getX() + l, blockPos2.getY() + 1, blockPos2.getZ() + m);
                Block block = Blocks.POLISHED_ANDESITE;
                BlockStateArgument blockStateArgument = new BlockStateArgument(block.getDefaultState(), Collections.EMPTY_SET, null);
                blockStateArgument.setBlockState(serverWorld, blockPos3, 2);
            }
        }
        class_4525.method_22248(blockPos2.add(1, 0, -1), serverWorld);
        return 0;
    }

    private static int method_22264(ServerCommandSource serverCommandSource) throws CommandSyntaxException {
        ServerWorld serverWorld;
        BlockHitResult blockHitResult = (BlockHitResult)serverCommandSource.getPlayer().rayTrace(10.0, 1.0f, false);
        BlockPos blockPos = blockHitResult.getBlockPos();
        Optional<BlockPos> optional = class_4525.method_22244(blockPos, 15, serverWorld = serverCommandSource.getWorld());
        if (!optional.isPresent()) {
            optional = class_4525.method_22244(blockPos, 200, serverWorld);
        }
        if (!optional.isPresent()) {
            serverCommandSource.sendError(new LiteralText("Can't find a structure block that contains the targeted pos " + blockPos));
            return 0;
        }
        StructureBlockBlockEntity structureBlockBlockEntity = (StructureBlockBlockEntity)serverWorld.getBlockEntity(optional.get());
        BlockPos blockPos2 = blockPos.subtract(optional.get());
        String string = blockPos2.getX() + ", " + blockPos2.getY() + ", " + blockPos2.getZ();
        String string2 = structureBlockBlockEntity.method_21865();
        TestCommand.method_22278(serverCommandSource, "Position relative to " + string2 + ":");
        TestCommand.method_22278(serverCommandSource, string);
        DebugRendererInfoManager.method_22318(serverWorld, new BlockPos(blockPos), string, -2147418368, 10000);
        return 1;
    }

    private static int method_22277(ServerCommandSource serverCommandSource) {
        ServerWorld serverWorld;
        BlockPos blockPos = new BlockPos(serverCommandSource.getPosition());
        BlockPos blockPos2 = class_4525.method_22255(blockPos, 15, serverWorld = serverCommandSource.getWorld());
        if (blockPos2 == null) {
            TestCommand.method_22275(serverWorld, "Couldn't find any structure block within 15 radius", Formatting.RED);
            return 0;
        }
        class_4520.method_22213(serverWorld);
        TestCommand.method_22272(serverWorld, blockPos2, null);
        return 1;
    }

    private static int method_22281(ServerCommandSource serverCommandSource) {
        ServerWorld serverWorld;
        BlockPos blockPos2 = new BlockPos(serverCommandSource.getPosition());
        Collection<BlockPos> collection = class_4525.method_22258(blockPos2, 200, serverWorld = serverCommandSource.getWorld());
        if (collection.isEmpty()) {
            TestCommand.method_22275(serverWorld, "Couldn't find any structure blocks within 200 block radius", Formatting.RED);
            return 1;
        }
        class_4520.method_22213(serverWorld);
        TestCommand.method_22278(serverCommandSource, "Running " + collection.size() + " tests...");
        class_4524 lv = new class_4524();
        collection.forEach(blockPos -> TestCommand.method_22272(serverWorld, blockPos, lv));
        return 1;
    }

    private static void method_22272(ServerWorld serverWorld, BlockPos blockPos, @Nullable class_4524 arg) {
        StructureBlockBlockEntity structureBlockBlockEntity = (StructureBlockBlockEntity)serverWorld.getBlockEntity(blockPos);
        String string = structureBlockBlockEntity.method_21865();
        TestFunction testFunction = class_4519.method_22200(string);
        class_4517 lv = new class_4517(testFunction, blockPos, serverWorld);
        if (arg != null) {
            arg.method_22230(lv);
            lv.method_22167(new class_4528(serverWorld, arg));
        }
        class_4520.method_22203(lv, class_4521.field_20574);
    }

    private static void method_22280(ServerWorld serverWorld, class_4524 arg) {
        if (arg.method_22239()) {
            TestCommand.method_22275(serverWorld, "GameTest done! " + arg.method_22238() + " tests were run", Formatting.WHITE);
            if (arg.method_22236()) {
                TestCommand.method_22275(serverWorld, "" + arg.method_22229() + " required tests failed :(", Formatting.RED);
            } else {
                TestCommand.method_22275(serverWorld, "All required tests passed :)", Formatting.GREEN);
            }
            if (arg.method_22237()) {
                TestCommand.method_22275(serverWorld, "" + arg.method_22234() + " optional tests failed", Formatting.GRAY);
            }
        }
    }

    private static int method_22265(ServerCommandSource serverCommandSource, int i) {
        ServerWorld serverWorld = serverCommandSource.getWorld();
        class_4520.method_22213(serverWorld);
        BlockPos blockPos = new BlockPos(serverCommandSource.getPosition().x, 4.0, serverCommandSource.getPosition().z);
        class_4520.method_22216(serverWorld, blockPos, class_4521.field_20574, MathHelper.clamp(i, 0, 1024));
        return 1;
    }

    private static int method_22266(ServerCommandSource serverCommandSource, TestFunction testFunction) {
        ServerWorld serverWorld = serverCommandSource.getWorld();
        BlockPos blockPos = new BlockPos(serverCommandSource.getPosition());
        BlockPos blockPos2 = new BlockPos(blockPos.getX(), 4, blockPos.getZ() + 3);
        class_4520.method_22213(serverWorld);
        class_4517 lv = new class_4517(testFunction, blockPos2, serverWorld);
        class_4520.method_22203(lv, class_4521.field_20574);
        return 1;
    }

    private static int method_22284(ServerCommandSource serverCommandSource) {
        class_4520.method_22213(serverCommandSource.getWorld());
        TestCommand.method_22269(serverCommandSource, class_4519.method_22191());
        return 1;
    }

    private static int method_22267(ServerCommandSource serverCommandSource, String string) {
        Collection<TestFunction> collection = class_4519.method_22193(string);
        class_4520.method_22213(serverCommandSource.getWorld());
        TestCommand.method_22269(serverCommandSource, collection);
        return 1;
    }

    private static void method_22269(ServerCommandSource serverCommandSource, Collection<TestFunction> collection) {
        BlockPos blockPos = new BlockPos(serverCommandSource.getPosition());
        BlockPos blockPos2 = new BlockPos(blockPos.getX(), 4, blockPos.getZ() + 3);
        ServerWorld serverWorld = serverCommandSource.getWorld();
        TestCommand.method_22278(serverCommandSource, "Running " + collection.size() + " tests...");
        Collection<class_4517> collection2 = class_4520.method_22222(collection, blockPos2, serverWorld, class_4521.field_20574);
        class_4524 lv = new class_4524(collection2);
        lv.method_22231(new class_4528(serverWorld, lv));
    }

    private static void method_22278(ServerCommandSource serverCommandSource, String string) {
        serverCommandSource.sendFeedback(new LiteralText(string), false);
    }

    private static int method_22282(ServerCommandSource serverCommandSource, String string) {
        Path path = Paths.get(class_4525.field_20579, new String[0]);
        Identifier identifier = new Identifier("minecraft", string);
        Path path2 = serverCommandSource.getWorld().getStructureManager().getStructurePath(identifier, ".nbt");
        Path path3 = NbtProvider.method_10493(path2, string, path);
        if (path3 == null) {
            TestCommand.method_22278(serverCommandSource, "Failed to export " + path2);
            return 1;
        }
        try {
            Files.createDirectories(path3.getParent(), new FileAttribute[0]);
        } catch (IOException iOException) {
            TestCommand.method_22278(serverCommandSource, "Could not create folder " + path3.getParent());
            iOException.printStackTrace();
            return 1;
        }
        TestCommand.method_22278(serverCommandSource, "Exported to " + path3.toAbsolutePath());
        return 0;
    }

    private static int method_22285(ServerCommandSource serverCommandSource, String string) {
        Path path = Paths.get(class_4525.field_20579, string + ".snbt");
        Identifier identifier = new Identifier("minecraft", string);
        Path path2 = serverCommandSource.getWorld().getStructureManager().getStructurePath(identifier, ".nbt");
        try {
            BufferedReader bufferedReader = Files.newBufferedReader(path);
            String string2 = IOUtils.toString(bufferedReader);
            Files.createDirectories(path2.getParent(), new FileAttribute[0]);
            OutputStream outputStream = Files.newOutputStream(path2, new OpenOption[0]);
            NbtIo.writeCompressed(StringNbtReader.parse(string2), outputStream);
            TestCommand.method_22278(serverCommandSource, "Imported to " + path2.toAbsolutePath());
            return 0;
        } catch (CommandSyntaxException | IOException exception) {
            System.err.println("Failed to load structure " + string);
            exception.printStackTrace();
            return 1;
        }
    }

    private static void method_22275(ServerWorld serverWorld, String string, Formatting formatting) {
        serverWorld.getPlayers(serverPlayerEntity -> true).forEach(serverPlayerEntity -> serverPlayerEntity.sendMessage(new LiteralText((Object)((Object)formatting) + string)));
    }

    static class class_4528
    implements class_4518 {
        private final ServerWorld field_20581;
        private final class_4524 field_20582;

        public class_4528(ServerWorld serverWorld, class_4524 arg) {
            this.field_20581 = serverWorld;
            this.field_20582 = arg;
        }

        @Override
        public void method_22188(class_4517 arg) {
        }

        @Override
        public void method_22189(class_4517 arg) {
            TestCommand.method_22280(this.field_20581, this.field_20582);
        }

        @Override
        public void method_22190(class_4517 arg) {
            TestCommand.method_22280(this.field_20581, this.field_20582);
        }
    }
}

