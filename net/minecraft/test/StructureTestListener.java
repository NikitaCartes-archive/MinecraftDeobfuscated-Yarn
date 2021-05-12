/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.test;

import com.google.common.base.MoreObjects;
import java.util.Arrays;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LecternBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.Structure;
import net.minecraft.test.GameTestState;
import net.minecraft.test.NotEnoughSuccessesError;
import net.minecraft.test.PositionedException;
import net.minecraft.test.TestFailureLogger;
import net.minecraft.test.TestFunction;
import net.minecraft.test.TestListener;
import net.minecraft.test.TestManager;
import net.minecraft.text.LiteralText;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import org.apache.commons.lang3.exception.ExceptionUtils;

class StructureTestListener
implements TestListener {
    private final GameTestState test;
    private final TestManager testManager;
    private final BlockPos pos;
    int attempt;
    int successes;

    public StructureTestListener(GameTestState test, TestManager testManager, BlockPos pos) {
        this.test = test;
        this.testManager = testManager;
        this.pos = pos;
        this.attempt = 0;
        this.successes = 0;
    }

    @Override
    public void onStarted(GameTestState test) {
        StructureTestListener.visualizeTest(this.test, Blocks.LIGHT_GRAY_STAINED_GLASS);
        ++this.attempt;
    }

    @Override
    public void onPassed(GameTestState test) {
        ++this.successes;
        if (!test.isFlaky()) {
            StructureTestListener.passTest(test, test.getStructurePath() + " passed!");
            return;
        }
        if (this.successes >= test.getRequiredSuccesses()) {
            StructureTestListener.passTest(test, test + " passed " + this.successes + " times of " + this.attempt + " attempts.");
        } else {
            StructureTestListener.sendMessageToAllPlayers(this.test.getWorld(), Formatting.GREEN, "Flaky test " + this.test + " succeeded, attempt: " + this.attempt + " successes: " + this.successes);
            this.init();
        }
    }

    @Override
    public void onFailed(GameTestState test) {
        if (!test.isFlaky()) {
            StructureTestListener.failTest(test, test.getThrowable());
            return;
        }
        TestFunction testFunction = this.test.getTestFunction();
        String string = "Flaky test " + this.test + " failed, attempt: " + this.attempt + "/" + testFunction.getMaxAttempts();
        if (testFunction.getRequiredSuccesses() > 1) {
            string = string + ", successes: " + this.successes + " (" + testFunction.getRequiredSuccesses() + " required)";
        }
        StructureTestListener.sendMessageToAllPlayers(this.test.getWorld(), Formatting.YELLOW, string);
        if (test.getMaxAttempts() - this.attempt + this.successes >= test.getRequiredSuccesses()) {
            this.init();
        } else {
            StructureTestListener.failTest(test, new NotEnoughSuccessesError(this.attempt, this.successes, test));
        }
    }

    public static void passTest(GameTestState test, String output) {
        StructureTestListener.visualizeTest(test, Blocks.LIME_STAINED_GLASS);
        StructureTestListener.finishPassedTest(test, output);
    }

    private static void finishPassedTest(GameTestState test, String output) {
        StructureTestListener.sendMessageToAllPlayers(test.getWorld(), Formatting.GREEN, output);
        TestFailureLogger.passTest(test);
    }

    protected static void failTest(GameTestState test, Throwable output) {
        StructureTestListener.visualizeTest(test, test.isRequired() ? Blocks.RED_STAINED_GLASS : Blocks.ORANGE_STAINED_GLASS);
        StructureTestListener.createTestOutputLectern(test, Util.getInnermostMessage(output));
        StructureTestListener.finishFailedTest(test, output);
    }

    protected static void finishFailedTest(GameTestState test, Throwable output) {
        String string = output.getMessage() + (String)(output.getCause() == null ? "" : " cause: " + Util.getInnermostMessage(output.getCause()));
        String string2 = (test.isRequired() ? "" : "(optional) ") + test.getStructurePath() + " failed! " + string;
        StructureTestListener.sendMessageToAllPlayers(test.getWorld(), test.isRequired() ? Formatting.RED : Formatting.YELLOW, string2);
        Throwable throwable = MoreObjects.firstNonNull(ExceptionUtils.getRootCause(output), output);
        if (throwable instanceof PositionedException) {
            PositionedException positionedException = (PositionedException)throwable;
            StructureTestListener.addGameTestMarker(test.getWorld(), positionedException.getPos(), positionedException.getDebugMessage());
        }
        TestFailureLogger.failTest(test);
    }

    private void init() {
        this.test.clearArea();
        GameTestState gameTestState = new GameTestState(this.test.getTestFunction(), this.test.getRotation(), this.test.getWorld());
        gameTestState.startCountdown();
        this.testManager.start(gameTestState);
        gameTestState.addListener(this);
        gameTestState.init(this.pos, 2);
    }

    protected static void visualizeTest(GameTestState test, Block block) {
        ServerWorld serverWorld = test.getWorld();
        BlockPos blockPos = test.getPos();
        BlockPos blockPos2 = new BlockPos(-1, -1, -1);
        BlockPos blockPos3 = Structure.transformAround(blockPos.add(blockPos2), BlockMirror.NONE, test.getRotation(), blockPos);
        serverWorld.setBlockState(blockPos3, Blocks.BEACON.getDefaultState().rotate(test.getRotation()));
        BlockPos blockPos4 = blockPos3.add(0, 1, 0);
        serverWorld.setBlockState(blockPos4, block.getDefaultState());
        for (int i = -1; i <= 1; ++i) {
            for (int j = -1; j <= 1; ++j) {
                BlockPos blockPos5 = blockPos3.add(i, -1, j);
                serverWorld.setBlockState(blockPos5, Blocks.IRON_BLOCK.getDefaultState());
            }
        }
    }

    private static void createTestOutputLectern(GameTestState test, String output) {
        ServerWorld serverWorld = test.getWorld();
        BlockPos blockPos = test.getPos();
        BlockPos blockPos2 = new BlockPos(-1, 1, -1);
        BlockPos blockPos3 = Structure.transformAround(blockPos.add(blockPos2), BlockMirror.NONE, test.getRotation(), blockPos);
        serverWorld.setBlockState(blockPos3, Blocks.LECTERN.getDefaultState().rotate(test.getRotation()));
        BlockState blockState = serverWorld.getBlockState(blockPos3);
        ItemStack itemStack = StructureTestListener.createBookWithText(test.getStructurePath(), test.isRequired(), output);
        LecternBlock.putBookIfAbsent(null, serverWorld, blockPos3, blockState, itemStack);
    }

    private static ItemStack createBookWithText(String text, boolean required, String output) {
        ItemStack itemStack = new ItemStack(Items.WRITABLE_BOOK);
        NbtList nbtList = new NbtList();
        StringBuffer stringBuffer = new StringBuffer();
        Arrays.stream(text.split("\\.")).forEach(line -> stringBuffer.append((String)line).append('\n'));
        if (!required) {
            stringBuffer.append("(optional)\n");
        }
        stringBuffer.append("-------------------\n");
        nbtList.add(NbtString.of(stringBuffer + output));
        itemStack.putSubTag("pages", nbtList);
        return itemStack;
    }

    protected static void sendMessageToAllPlayers(ServerWorld world, Formatting formatting, String message) {
        world.getPlayers(player -> true).forEach(player -> player.sendSystemMessage(new LiteralText(message).formatted(formatting), Util.NIL_UUID));
    }

    private static void addGameTestMarker(ServerWorld world, BlockPos pos, String message) {
        DebugInfoSender.addGameTestMarker(world, pos, message, -2130771968, Integer.MAX_VALUE);
    }
}

