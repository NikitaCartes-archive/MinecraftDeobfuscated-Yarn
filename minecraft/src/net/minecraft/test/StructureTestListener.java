package net.minecraft.test;

import com.google.common.base.MoreObjects;
import java.util.Arrays;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LecternBlock;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.WritableBookContentComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.text.RawFilteredPair;
import net.minecraft.text.Text;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import org.apache.commons.lang3.exception.ExceptionUtils;

class StructureTestListener implements TestListener {
	private int attempt = 0;
	private int successes = 0;

	public StructureTestListener() {
	}

	@Override
	public void onStarted(GameTestState test) {
		visualizeTest(test, Blocks.LIGHT_GRAY_STAINED_GLASS);
		this.attempt++;
	}

	private void retry(GameTestState state, TestRunContext context, boolean prevPassed) {
		TestAttemptConfig testAttemptConfig = state.getTestAttemptConfig();
		String string = String.format("[Run: %4d, Ok: %4d, Fail: %4d", this.attempt, this.successes, this.attempt - this.successes);
		if (!testAttemptConfig.isDisabled()) {
			string = string + String.format(", Left: %4d", testAttemptConfig.numberOfTries() - this.attempt);
		}

		string = string + "]";
		String string2 = state.getTemplatePath() + " " + (prevPassed ? "passed" : "failed") + "! " + state.getElapsedMilliseconds() + "ms";
		String string3 = String.format("%-53s%s", string, string2);
		if (prevPassed) {
			passTest(state, string3);
		} else {
			sendMessageToAllPlayers(state.getWorld(), Formatting.RED, string3);
		}

		if (testAttemptConfig.shouldTestAgain(this.attempt, this.successes)) {
			context.retry(state);
		}
	}

	@Override
	public void onPassed(GameTestState test, TestRunContext context) {
		this.successes++;
		if (test.getTestAttemptConfig().needsMultipleAttempts()) {
			this.retry(test, context, true);
		} else if (!test.isFlaky()) {
			passTest(test, test.getTemplatePath() + " passed! (" + test.getElapsedMilliseconds() + "ms)");
		} else {
			if (this.successes >= test.getRequiredSuccesses()) {
				passTest(test, test + " passed " + this.successes + " times of " + this.attempt + " attempts.");
			} else {
				sendMessageToAllPlayers(test.getWorld(), Formatting.GREEN, "Flaky test " + test + " succeeded, attempt: " + this.attempt + " successes: " + this.successes);
				context.retry(test);
			}
		}
	}

	@Override
	public void onFailed(GameTestState test, TestRunContext context) {
		if (!test.isFlaky()) {
			failTest(test, test.getThrowable());
			if (test.getTestAttemptConfig().needsMultipleAttempts()) {
				this.retry(test, context, false);
			}
		} else {
			TestFunction testFunction = test.getTestFunction();
			String string = "Flaky test " + test + " failed, attempt: " + this.attempt + "/" + testFunction.maxAttempts();
			if (testFunction.requiredSuccesses() > 1) {
				string = string + ", successes: " + this.successes + " (" + testFunction.requiredSuccesses() + " required)";
			}

			sendMessageToAllPlayers(test.getWorld(), Formatting.YELLOW, string);
			if (test.getMaxAttempts() - this.attempt + this.successes >= test.getRequiredSuccesses()) {
				context.retry(test);
			} else {
				failTest(test, new NotEnoughSuccessesError(this.attempt, this.successes, test));
			}
		}
	}

	@Override
	public void onRetry(GameTestState prevState, GameTestState nextState, TestRunContext context) {
		nextState.addListener(this);
	}

	public static void passTest(GameTestState test, String output) {
		placeAboveBeacon(test, Blocks.LIME_STAINED_GLASS);
		finishPassedTest(test, output);
	}

	private static void finishPassedTest(GameTestState test, String output) {
		sendMessageToAllPlayers(test.getWorld(), Formatting.GREEN, output);
		TestFailureLogger.passTest(test);
	}

	protected static void failTest(GameTestState test, Throwable output) {
		placeAboveBeacon(test, test.isRequired() ? Blocks.RED_STAINED_GLASS : Blocks.ORANGE_STAINED_GLASS);
		createTestOutputLectern(test, Util.getInnermostMessage(output));
		finishFailedTest(test, output);
	}

	protected static void finishFailedTest(GameTestState test, Throwable output) {
		String string = output.getMessage() + (output.getCause() == null ? "" : " cause: " + Util.getInnermostMessage(output.getCause()));
		String string2 = (test.isRequired() ? "" : "(optional) ") + test.getTemplatePath() + " failed! " + string;
		sendMessageToAllPlayers(test.getWorld(), test.isRequired() ? Formatting.RED : Formatting.YELLOW, string2);
		Throwable throwable = MoreObjects.firstNonNull(ExceptionUtils.getRootCause(output), output);
		if (throwable instanceof PositionedException positionedException) {
			addGameTestMarker(test.getWorld(), positionedException.getPos(), positionedException.getDebugMessage());
		}

		TestFailureLogger.failTest(test);
	}

	protected static void visualizeTest(GameTestState test, Block block) {
		ServerWorld serverWorld = test.getWorld();
		BlockPos blockPos = getBeaconPos(test);
		serverWorld.setBlockState(blockPos, Blocks.BEACON.getDefaultState().rotate(test.getRotation()));
		placeAboveBeacon(test, block);

		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <= 1; j++) {
				BlockPos blockPos2 = blockPos.add(i, -1, j);
				serverWorld.setBlockState(blockPos2, Blocks.IRON_BLOCK.getDefaultState());
			}
		}
	}

	private static BlockPos getBeaconPos(GameTestState state) {
		BlockPos blockPos = state.getPos();
		BlockPos blockPos2 = new BlockPos(-1, -2, -1);
		return StructureTemplate.transformAround(blockPos.add(blockPos2), BlockMirror.NONE, state.getRotation(), blockPos);
	}

	private static void placeAboveBeacon(GameTestState state, Block block) {
		ServerWorld serverWorld = state.getWorld();
		BlockPos blockPos = getBeaconPos(state);
		if (serverWorld.getBlockState(blockPos).isOf(Blocks.BEACON)) {
			BlockPos blockPos2 = blockPos.add(0, 1, 0);
			serverWorld.setBlockState(blockPos2, block.getDefaultState());
		}
	}

	private static void createTestOutputLectern(GameTestState test, String output) {
		ServerWorld serverWorld = test.getWorld();
		BlockPos blockPos = test.getPos();
		BlockPos blockPos2 = new BlockPos(-1, 0, -1);
		BlockPos blockPos3 = StructureTemplate.transformAround(blockPos.add(blockPos2), BlockMirror.NONE, test.getRotation(), blockPos);
		serverWorld.setBlockState(blockPos3, Blocks.LECTERN.getDefaultState().rotate(test.getRotation()));
		BlockState blockState = serverWorld.getBlockState(blockPos3);
		ItemStack itemStack = createBookWithText(test.getTemplatePath(), test.isRequired(), output);
		LecternBlock.putBookIfAbsent(null, serverWorld, blockPos3, blockState, itemStack);
	}

	private static ItemStack createBookWithText(String text, boolean required, String output) {
		StringBuffer stringBuffer = new StringBuffer();
		Arrays.stream(text.split("\\.")).forEach(line -> stringBuffer.append(line).append('\n'));
		if (!required) {
			stringBuffer.append("(optional)\n");
		}

		stringBuffer.append("-------------------\n");
		ItemStack itemStack = new ItemStack(Items.WRITABLE_BOOK);
		itemStack.set(DataComponentTypes.WRITABLE_BOOK_CONTENT, new WritableBookContentComponent(List.of(RawFilteredPair.of(stringBuffer + output))));
		return itemStack;
	}

	protected static void sendMessageToAllPlayers(ServerWorld world, Formatting formatting, String message) {
		world.getPlayers(player -> true).forEach(player -> player.sendMessage(Text.literal(message).formatted(formatting)));
	}

	private static void addGameTestMarker(ServerWorld world, BlockPos pos, String message) {
		DebugInfoSender.addGameTestMarker(world, pos, message, -2130771968, Integer.MAX_VALUE);
	}
}
