package net.minecraft.test;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Streams;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
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
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;

public class TestUtil {
	public static TestCompletionListener field_20573 = new FailureLoggingTestCompletionListener();

	public static void startTest(GameTest gameTest, TestManager testManager) {
		gameTest.startCountdown();
		testManager.start(gameTest);
		gameTest.addListener(new TestListener() {
			@Override
			public void onStarted(GameTest test) {
				TestUtil.createBeacon(test, Blocks.LIGHT_GRAY_STAINED_GLASS);
			}

			@Override
			public void onFailed(GameTest test) {
				TestUtil.createBeacon(test, test.isRequired() ? Blocks.RED_STAINED_GLASS : Blocks.ORANGE_STAINED_GLASS);
				TestUtil.createLectern(test, Util.getInnermostMessage(test.getThrowable()));
				TestUtil.handleTestFail(test);
			}
		});
		gameTest.init(2);
	}

	public static Collection<GameTest> runTestBatches(Collection<GameTestBatch> batches, BlockPos pos, ServerWorld world, TestManager testManager) {
		TestRunner testRunner = new TestRunner(batches, pos, world, testManager);
		testRunner.run();
		return testRunner.getTests();
	}

	public static Collection<GameTest> runTestFunctions(Collection<TestFunction> testFunctions, BlockPos pos, ServerWorld world, TestManager testManager) {
		return runTestBatches(createBatches(testFunctions), pos, world, testManager);
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
					AtomicInteger atomicInteger = new AtomicInteger();
					return Streams.stream(Iterables.partition(collection, 100))
						.map(list -> new GameTestBatch(string + ":" + atomicInteger.incrementAndGet(), collection, consumer));
				}
			)
			.collect(Collectors.toList());
	}

	private static void handleTestFail(GameTest test) {
		Throwable throwable = test.getThrowable();
		String string = test.getStructurePath() + " failed! " + Util.getInnermostMessage(throwable);
		sendMessage(test.getWorld(), Formatting.RED, string);
		if (throwable instanceof PositionedException) {
			PositionedException positionedException = (PositionedException)throwable;
			addDebugMarker(test.getWorld(), positionedException.getPos(), positionedException.getDebugMessage());
		}

		field_20573.onTestFailed(test);
	}

	private static void createBeacon(GameTest test, Block glass) {
		ServerWorld serverWorld = test.getWorld();
		BlockPos blockPos = test.getPos();
		BlockPos blockPos2 = blockPos.add(-1, -1, -1);
		serverWorld.setBlockState(blockPos2, Blocks.BEACON.getDefaultState());
		BlockPos blockPos3 = blockPos2.add(0, 1, 0);
		serverWorld.setBlockState(blockPos3, glass.getDefaultState());

		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <= 1; j++) {
				BlockPos blockPos4 = blockPos2.add(i, -1, j);
				serverWorld.setBlockState(blockPos4, Blocks.IRON_BLOCK.getDefaultState());
			}
		}
	}

	private static void createLectern(GameTest test, String message) {
		ServerWorld serverWorld = test.getWorld();
		BlockPos blockPos = test.getPos();
		BlockPos blockPos2 = blockPos.add(-1, 1, -1);
		serverWorld.setBlockState(blockPos2, Blocks.LECTERN.getDefaultState());
		BlockState blockState = serverWorld.getBlockState(blockPos2);
		ItemStack itemStack = createBook(test.getStructurePath(), test.isRequired(), message);
		LecternBlock.putBookIfAbsent(serverWorld, blockPos2, blockState, itemStack);
	}

	private static ItemStack createBook(String structureName, boolean required, String message) {
		ItemStack itemStack = new ItemStack(Items.WRITABLE_BOOK);
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
		world.getPlayers(serverPlayerEntity -> true).forEach(serverPlayerEntity -> serverPlayerEntity.sendMessage(new LiteralText(message).formatted(formatting)));
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
		BlockPos.stream(blockPos, blockPos2).filter(blockPosx -> world.getBlockState(blockPosx).getBlock() == Blocks.STRUCTURE_BLOCK).forEach(blockPosx -> {
			StructureBlockBlockEntity structureBlockBlockEntity = (StructureBlockBlockEntity)world.getBlockEntity(blockPosx);
			BlockPos blockPos2x = structureBlockBlockEntity.getPos();
			BlockBox blockBox = StructureTestUtil.createArea(blockPos2x, structureBlockBlockEntity.getSize(), 2);
			StructureTestUtil.clearArea(blockBox, blockPos2x.getY(), world);
		});
	}
}
