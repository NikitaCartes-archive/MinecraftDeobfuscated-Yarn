package net.minecraft.test;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Streams;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LecternBlock;
import net.minecraft.block.entity.StructureBlockBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.Structure;
import net.minecraft.text.LiteralText;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import org.apache.commons.lang3.mutable.MutableInt;

public class TestUtil {
	public static TestCompletionListener field_20573 = new FailureLoggingTestCompletionListener();

	public static void startTest(GameTest gameTest, BlockPos blockPos, TestManager testManager) {
		gameTest.startCountdown();
		testManager.start(gameTest);
		gameTest.addListener(new TestListener() {
			@Override
			public void onStarted(GameTest test) {
				TestUtil.createBeacon(test, Blocks.field_9996);
			}

			@Override
			public void onFailed(GameTest test) {
				TestUtil.createBeacon(test, test.isRequired() ? Blocks.field_10272 : Blocks.field_10227);
				TestUtil.createLectern(test, Util.getInnermostMessage(test.getThrowable()));
				TestUtil.handleTestFail(test);
			}
		});
		gameTest.init(blockPos, 2);
	}

	public static Collection<GameTest> runTestBatches(
		Collection<GameTestBatch> batches, BlockPos pos, BlockRotation blockRotation, ServerWorld serverWorld, TestManager testManager, int i
	) {
		TestRunner testRunner = new TestRunner(batches, pos, blockRotation, serverWorld, testManager, i);
		testRunner.run();
		return testRunner.getTests();
	}

	public static Collection<GameTest> runTestFunctions(
		Collection<TestFunction> testFunctions, BlockPos pos, BlockRotation blockRotation, ServerWorld serverWorld, TestManager testManager, int i
	) {
		return runTestBatches(createBatches(testFunctions), pos, blockRotation, serverWorld, testManager, i);
	}

	public static Collection<GameTestBatch> createBatches(Collection<TestFunction> testFunctions) {
		Map<String, Collection<TestFunction>> map = Maps.<String, Collection<TestFunction>>newHashMap();
		testFunctions.forEach(testFunction -> {
			String string = testFunction.getBatchId();
			Collection<TestFunction> collection = (Collection<TestFunction>)map.computeIfAbsent(string, stringx -> Lists.newArrayList());
			collection.add(testFunction);
		});
		return (Collection<GameTestBatch>)map.keySet()
			.stream()
			.flatMap(
				string -> {
					Collection<TestFunction> collection = (Collection<TestFunction>)map.get(string);
					Consumer<ServerWorld> consumer = TestFunctions.getWorldSetter(string);
					MutableInt mutableInt = new MutableInt();
					return Streams.stream(Iterables.partition(collection, 100))
						.map(list -> new GameTestBatch(string + ":" + mutableInt.incrementAndGet(), collection, consumer));
				}
			)
			.collect(Collectors.toList());
	}

	private static void handleTestFail(GameTest test) {
		Throwable throwable = test.getThrowable();
		String string = (test.isRequired() ? "" : "(optional) ") + test.getStructurePath() + " failed! " + Util.getInnermostMessage(throwable);
		sendMessage(test.getWorld(), test.isRequired() ? Formatting.field_1061 : Formatting.field_1054, string);
		if (throwable instanceof PositionedException) {
			PositionedException positionedException = (PositionedException)throwable;
			addDebugMarker(test.getWorld(), positionedException.getPos(), positionedException.getDebugMessage());
		}

		field_20573.onTestFailed(test);
	}

	private static void createBeacon(GameTest test, Block glass) {
		ServerWorld serverWorld = test.getWorld();
		BlockPos blockPos = test.getPos();
		BlockPos blockPos2 = new BlockPos(-1, -1, -1);
		BlockPos blockPos3 = Structure.transformAround(blockPos.add(blockPos2), BlockMirror.field_11302, test.method_29402(), blockPos);
		serverWorld.setBlockState(blockPos3, Blocks.field_10327.getDefaultState().rotate(test.method_29402()));
		BlockPos blockPos4 = blockPos3.add(0, 1, 0);
		serverWorld.setBlockState(blockPos4, glass.getDefaultState());

		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <= 1; j++) {
				BlockPos blockPos5 = blockPos3.add(i, -1, j);
				serverWorld.setBlockState(blockPos5, Blocks.field_10085.getDefaultState());
			}
		}
	}

	private static void createLectern(GameTest test, String message) {
		ServerWorld serverWorld = test.getWorld();
		BlockPos blockPos = test.getPos();
		BlockPos blockPos2 = new BlockPos(-1, 1, -1);
		BlockPos blockPos3 = Structure.transformAround(blockPos.add(blockPos2), BlockMirror.field_11302, test.method_29402(), blockPos);
		serverWorld.setBlockState(blockPos3, Blocks.field_16330.getDefaultState().rotate(test.method_29402()));
		BlockState blockState = serverWorld.getBlockState(blockPos3);
		ItemStack itemStack = createBook(test.getStructurePath(), test.isRequired(), message);
		LecternBlock.putBookIfAbsent(serverWorld, blockPos3, blockState, itemStack);
	}

	private static ItemStack createBook(String structureName, boolean required, String message) {
		ItemStack itemStack = new ItemStack(Items.field_8674);
		ListTag listTag = new ListTag();
		StringBuffer stringBuffer = new StringBuffer();
		Arrays.stream(structureName.split("\\.")).forEach(string -> stringBuffer.append(string).append('\n'));
		if (!required) {
			stringBuffer.append("(optional)\n");
		}

		stringBuffer.append("-------------------\n");
		listTag.add(StringTag.of(stringBuffer.toString() + message));
		itemStack.putSubTag("pages", listTag);
		return itemStack;
	}

	private static void sendMessage(ServerWorld world, Formatting formatting, String message) {
		world.getPlayers(serverPlayerEntity -> true)
			.forEach(serverPlayerEntity -> serverPlayerEntity.sendSystemMessage(new LiteralText(message).formatted(formatting), Util.NIL_UUID));
	}

	public static void clearDebugMarkers(ServerWorld world) {
		DebugInfoSender.clearGameTestMarkers(world);
	}

	private static void addDebugMarker(ServerWorld world, BlockPos pos, String message) {
		DebugInfoSender.addGameTestMarker(world, pos, message, -2130771968, Integer.MAX_VALUE);
	}

	public static void clearTests(ServerWorld world, BlockPos pos, TestManager testManager, int radius) {
		testManager.clear();
		BlockPos blockPos = pos.add(-radius, 0, -radius);
		BlockPos blockPos2 = pos.add(radius, 0, radius);
		BlockPos.stream(blockPos, blockPos2).filter(blockPosx -> world.getBlockState(blockPosx).isOf(Blocks.field_10465)).forEach(blockPosx -> {
			StructureBlockBlockEntity structureBlockBlockEntity = (StructureBlockBlockEntity)world.getBlockEntity(blockPosx);
			BlockPos blockPos2x = structureBlockBlockEntity.getPos();
			BlockBox blockBox = StructureTestUtil.method_29410(structureBlockBlockEntity);
			StructureTestUtil.clearArea(blockBox, blockPos2x.getY(), world);
		});
	}
}
