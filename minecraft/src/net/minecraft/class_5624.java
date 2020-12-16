package net.minecraft;

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
import net.minecraft.test.GameTest;
import net.minecraft.test.PositionedException;
import net.minecraft.test.TestFunction;
import net.minecraft.test.TestListener;
import net.minecraft.test.TestManager;
import net.minecraft.text.LiteralText;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import org.apache.commons.lang3.exception.ExceptionUtils;

public class class_5624 implements TestListener {
	private final GameTest field_27810;
	private final TestManager field_27811;
	private final BlockPos field_27812;
	int field_27808;
	int field_27809;

	public class_5624(GameTest gameTest, TestManager testManager, BlockPos blockPos) {
		this.field_27810 = gameTest;
		this.field_27811 = testManager;
		this.field_27812 = blockPos;
		this.field_27808 = 0;
		this.field_27809 = 0;
	}

	@Override
	public void onStarted(GameTest test) {
		method_32253(this.field_27810, Blocks.LIGHT_GRAY_STAINED_GLASS);
		this.field_27808++;
	}

	@Override
	public void method_33317(GameTest gameTest) {
		this.field_27809++;
		if (!gameTest.method_32241()) {
			method_33320(gameTest, gameTest.getStructurePath() + " passed!");
		} else {
			if (this.field_27809 >= gameTest.method_32243()) {
				method_33320(gameTest, gameTest + " passed " + this.field_27809 + " times of " + this.field_27808 + " attempts.");
			} else {
				method_32248(
					this.field_27810.getWorld(),
					Formatting.GREEN,
					"Flaky test " + this.field_27810 + " succeeded, attempt: " + this.field_27808 + " successes: " + this.field_27809
				);
				this.method_32246();
			}
		}
	}

	@Override
	public void onFailed(GameTest test) {
		if (!test.method_32241()) {
			method_32254(test, test.getThrowable());
		} else {
			TestFunction testFunction = this.field_27810.method_29403();
			String string = "Flaky test " + this.field_27810 + " failed, attempt: " + this.field_27808 + "/" + testFunction.method_32258();
			if (testFunction.method_32259() > 1) {
				string = string + ", successes: " + this.field_27809 + " (" + testFunction.method_32259() + " required)";
			}

			method_32248(this.field_27810.getWorld(), Formatting.YELLOW, string);
			if (test.method_32242() - this.field_27808 + this.field_27809 >= test.method_32243()) {
				this.method_32246();
			} else {
				method_32254(test, new class_5622(this.field_27808, this.field_27809, test));
			}
		}
	}

	public static void method_33320(GameTest gameTest, String string) {
		method_32253(gameTest, Blocks.LIME_STAINED_GLASS);
		method_33321(gameTest, string);
	}

	private static void method_33321(GameTest gameTest, String string) {
		method_32248(gameTest.getWorld(), Formatting.GREEN, string);
		class_5623.method_33319(gameTest);
	}

	protected static void method_32254(GameTest gameTest, Throwable throwable) {
		method_32253(gameTest, gameTest.isRequired() ? Blocks.RED_STAINED_GLASS : Blocks.ORANGE_STAINED_GLASS);
		method_32256(gameTest, Util.getInnermostMessage(throwable));
		method_32255(gameTest, throwable);
	}

	protected static void method_32255(GameTest gameTest, Throwable throwable) {
		String string = throwable.getMessage() + (throwable.getCause() == null ? "" : " cause: " + Util.getInnermostMessage(throwable.getCause()));
		String string2 = (gameTest.isRequired() ? "" : "(optional) ") + gameTest.getStructurePath() + " failed! " + string;
		method_32248(gameTest.getWorld(), gameTest.isRequired() ? Formatting.RED : Formatting.YELLOW, string2);
		Throwable throwable2 = MoreObjects.firstNonNull(ExceptionUtils.getRootCause(throwable), throwable);
		if (throwable2 instanceof PositionedException) {
			PositionedException positionedException = (PositionedException)throwable2;
			method_32247(gameTest.getWorld(), positionedException.getPos(), positionedException.getDebugMessage());
		}

		class_5623.method_32245(gameTest);
	}

	private void method_32246() {
		this.field_27810.method_32240();
		GameTest gameTest = new GameTest(this.field_27810.method_29403(), this.field_27810.method_29402(), this.field_27810.getWorld());
		gameTest.startCountdown();
		this.field_27811.start(gameTest);
		gameTest.addListener(this);
		gameTest.init(this.field_27812, 2);
	}

	protected static void method_32253(GameTest gameTest, Block block) {
		ServerWorld serverWorld = gameTest.getWorld();
		BlockPos blockPos = gameTest.getPos();
		BlockPos blockPos2 = new BlockPos(-1, -1, -1);
		BlockPos blockPos3 = Structure.transformAround(blockPos.add(blockPos2), BlockMirror.NONE, gameTest.method_29402(), blockPos);
		serverWorld.setBlockState(blockPos3, Blocks.BEACON.getDefaultState().rotate(gameTest.method_29402()));
		BlockPos blockPos4 = blockPos3.add(0, 1, 0);
		serverWorld.setBlockState(blockPos4, block.getDefaultState());

		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <= 1; j++) {
				BlockPos blockPos5 = blockPos3.add(i, -1, j);
				serverWorld.setBlockState(blockPos5, Blocks.IRON_BLOCK.getDefaultState());
			}
		}
	}

	private static void method_32256(GameTest gameTest, String string) {
		ServerWorld serverWorld = gameTest.getWorld();
		BlockPos blockPos = gameTest.getPos();
		BlockPos blockPos2 = new BlockPos(-1, 1, -1);
		BlockPos blockPos3 = Structure.transformAround(blockPos.add(blockPos2), BlockMirror.NONE, gameTest.method_29402(), blockPos);
		serverWorld.setBlockState(blockPos3, Blocks.LECTERN.getDefaultState().rotate(gameTest.method_29402()));
		BlockState blockState = serverWorld.getBlockState(blockPos3);
		ItemStack itemStack = method_32251(gameTest.getStructurePath(), gameTest.isRequired(), string);
		LecternBlock.putBookIfAbsent(serverWorld, blockPos3, blockState, itemStack);
	}

	private static ItemStack method_32251(String string, boolean bl, String string2) {
		ItemStack itemStack = new ItemStack(Items.WRITABLE_BOOK);
		ListTag listTag = new ListTag();
		StringBuffer stringBuffer = new StringBuffer();
		Arrays.stream(string.split("\\.")).forEach(stringx -> stringBuffer.append(stringx).append('\n'));
		if (!bl) {
			stringBuffer.append("(optional)\n");
		}

		stringBuffer.append("-------------------\n");
		listTag.add(StringTag.of(stringBuffer + string2));
		itemStack.putSubTag("pages", listTag);
		return itemStack;
	}

	protected static void method_32248(ServerWorld serverWorld, Formatting formatting, String string) {
		serverWorld.getPlayers(serverPlayerEntity -> true)
			.forEach(serverPlayerEntity -> serverPlayerEntity.sendSystemMessage(new LiteralText(string).formatted(formatting), Util.NIL_UUID));
	}

	private static void method_32247(ServerWorld serverWorld, BlockPos blockPos, String string) {
		DebugInfoSender.addGameTestMarker(serverWorld, blockPos, string, -2130771968, Integer.MAX_VALUE);
	}
}
