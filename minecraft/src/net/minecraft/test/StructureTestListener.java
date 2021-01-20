package net.minecraft.test;

import com.google.common.base.MoreObjects;
import java.util.Arrays;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LecternBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.Structure;
import net.minecraft.text.LiteralText;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import org.apache.commons.lang3.exception.ExceptionUtils;

class StructureTestListener implements TestListener {
	private final GameTest test;
	private final TestManager testManager;
	private final BlockPos pos;
	int attempt;
	int successes;

	public StructureTestListener(GameTest test, TestManager testManager, BlockPos pos) {
		this.test = test;
		this.testManager = testManager;
		this.pos = pos;
		this.attempt = 0;
		this.successes = 0;
	}

	@Override
	public void onStarted(GameTest test) {
		visualizeTest(this.test, Blocks.LIGHT_GRAY_STAINED_GLASS);
		this.attempt++;
	}

	@Override
	public void onPassed(GameTest test) {
		this.successes++;
		if (!test.isFlaky()) {
			passTest(test, test.getStructurePath() + " passed!");
		} else {
			if (this.successes >= test.getRequiredSuccesses()) {
				passTest(test, test + " passed " + this.successes + " times of " + this.attempt + " attempts.");
			} else {
				sendMessageToAllPlayers(
					this.test.getWorld(), Formatting.GREEN, "Flaky test " + this.test + " succeeded, attempt: " + this.attempt + " successes: " + this.successes
				);
				this.init();
			}
		}
	}

	@Override
	public void onFailed(GameTest test) {
		if (!test.isFlaky()) {
			failTest(test, test.getThrowable());
		} else {
			TestFunction testFunction = this.test.getTestFunction();
			String string = "Flaky test " + this.test + " failed, attempt: " + this.attempt + "/" + testFunction.getMaxAttempts();
			if (testFunction.getRequiredSuccesses() > 1) {
				string = string + ", successes: " + this.successes + " (" + testFunction.getRequiredSuccesses() + " required)";
			}

			sendMessageToAllPlayers(this.test.getWorld(), Formatting.YELLOW, string);
			if (test.getMaxAttempts() - this.attempt + this.successes >= test.getRequiredSuccesses()) {
				this.init();
			} else {
				failTest(test, new NotEnoughSuccessesError(this.attempt, this.successes, test));
			}
		}
	}

	public static void passTest(GameTest test, String output) {
		visualizeTest(test, Blocks.LIME_STAINED_GLASS);
		finishPassedTest(test, output);
	}

	private static void finishPassedTest(GameTest test, String output) {
		sendMessageToAllPlayers(test.getWorld(), Formatting.GREEN, output);
		TestFailureLogger.passTest(test);
	}

	protected static void failTest(GameTest test, Throwable output) {
		visualizeTest(test, test.isRequired() ? Blocks.RED_STAINED_GLASS : Blocks.ORANGE_STAINED_GLASS);
		createTestOutputLectern(test, Util.getInnermostMessage(output));
		finishFailedTest(test, output);
	}

	protected static void finishFailedTest(GameTest test, Throwable output) {
		String string = output.getMessage() + (output.getCause() == null ? "" : " cause: " + Util.getInnermostMessage(output.getCause()));
		String string2 = (test.isRequired() ? "" : "(optional) ") + test.getStructurePath() + " failed! " + string;
		sendMessageToAllPlayers(test.getWorld(), test.isRequired() ? Formatting.RED : Formatting.YELLOW, string2);
		Throwable throwable = MoreObjects.firstNonNull(ExceptionUtils.getRootCause(output), output);
		if (throwable instanceof PositionedException) {
			PositionedException positionedException = (PositionedException)throwable;
			addGameTestMarker(test.getWorld(), positionedException.getPos(), positionedException.getDebugMessage());
		}

		TestFailureLogger.failTest(test);
	}

	private void init() {
		this.test.clearArea();
		GameTest gameTest = new GameTest(this.test.getTestFunction(), this.test.getRotation(), this.test.getWorld());
		gameTest.startCountdown();
		this.testManager.start(gameTest);
		gameTest.addListener(this);
		gameTest.init(this.pos, 2);
	}

	protected static void visualizeTest(GameTest test, Block block) {
		ServerWorld serverWorld = test.getWorld();
		BlockPos blockPos = test.getPos();
		BlockPos blockPos2 = new BlockPos(-1, -1, -1);
		BlockPos blockPos3 = Structure.transformAround(blockPos.add(blockPos2), BlockMirror.NONE, test.getRotation(), blockPos);
		serverWorld.setBlockState(blockPos3, Blocks.BEACON.getDefaultState().rotate(test.getRotation()));
		BlockPos blockPos4 = blockPos3.add(0, 1, 0);
		serverWorld.setBlockState(blockPos4, block.getDefaultState());

		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <= 1; j++) {
				BlockPos blockPos5 = blockPos3.add(i, -1, j);
				serverWorld.setBlockState(blockPos5, Blocks.IRON_BLOCK.getDefaultState());
			}
		}
	}

	private static void createTestOutputLectern(GameTest test, String output) {
		ServerWorld serverWorld = test.getWorld();
		BlockPos blockPos = test.getPos();
		BlockPos blockPos2 = new BlockPos(-1, 1, -1);
		BlockPos blockPos3 = Structure.transformAround(blockPos.add(blockPos2), BlockMirror.NONE, test.getRotation(), blockPos);
		serverWorld.setBlockState(blockPos3, Blocks.LECTERN.getDefaultState().rotate(test.getRotation()));
		BlockState blockState = serverWorld.getBlockState(blockPos3);
		ItemStack itemStack = createBookWithText(test.getStructurePath(), test.isRequired(), output);
		LecternBlock.putBookIfAbsent(serverWorld, blockPos3, blockState, itemStack);
	}

	private static ItemStack createBookWithText(String text, boolean required, String output) {
		ItemStack itemStack = new ItemStack(Items.WRITABLE_BOOK);
		ListTag listTag = new ListTag();
		StringBuffer stringBuffer = new StringBuffer();
		Arrays.stream(text.split("\\.")).forEach(string -> stringBuffer.append(string).append('\n'));
		if (!required) {
			stringBuffer.append("(optional)\n");
		}

		stringBuffer.append("-------------------\n");
		listTag.add(StringTag.of(stringBuffer + output));
		itemStack.putSubTag("pages", listTag);
		return itemStack;
	}

	protected static void sendMessageToAllPlayers(ServerWorld world, Formatting formatting, String message) {
		world.getPlayers(player -> true).forEach(player -> player.sendSystemMessage(new LiteralText(message).formatted(formatting), Util.NIL_UUID));
	}

	private static void addGameTestMarker(ServerWorld world, BlockPos pos, String message) {
		DebugInfoSender.addGameTestMarker(world, pos, message, -2130771968, Integer.MAX_VALUE);
	}
}
