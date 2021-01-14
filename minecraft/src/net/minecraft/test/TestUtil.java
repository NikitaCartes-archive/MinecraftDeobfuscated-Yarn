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
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
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

	public static void startTest(GameTestState test, BlockPos pos, TestManager testManager) {
		test.startCountdown();
		testManager.start(test);
		test.addListener(new TestListener() {
			@Override
			public void onStarted(GameTestState test) {
				TestUtil.createBeacon(test, Blocks.LIGHT_GRAY_STAINED_GLASS);
			}

			@Override
			public void onFailed(GameTestState test) {
				TestUtil.createBeacon(test, test.isRequired() ? Blocks.RED_STAINED_GLASS : Blocks.ORANGE_STAINED_GLASS);
				TestUtil.createLectern(test, Util.getInnermostMessage(test.getThrowable()));
				TestUtil.handleTestFail(test);
			}
		});
		test.init(pos, 2);
	}

	public static Collection<GameTestState> runTestBatches(
		Collection<GameTestBatch> batches, BlockPos pos, BlockRotation rotation, ServerWorld world, TestManager testManager, int sizeZ
	) {
		TestRunner testRunner = new TestRunner(batches, pos, rotation, world, testManager, sizeZ);
		testRunner.run();
		return testRunner.getTests();
	}

	public static Collection<GameTestState> runTestFunctions(
		Collection<TestFunction> testFunctions, BlockPos pos, BlockRotation rotation, ServerWorld world, TestManager testManager, int sizeZ
	) {
		return runTestBatches(createBatches(testFunctions), pos, rotation, world, testManager, sizeZ);
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
					Consumer<ServerWorld> consumer = TestFunctions.getAfterBatchConsumer(string);
					MutableInt mutableInt = new MutableInt();
					return Streams.stream(Iterables.partition(collection, 100))
						.map(list -> new GameTestBatch(string + ":" + mutableInt.incrementAndGet(), collection, consumer));
				}
			)
			.collect(Collectors.toList());
	}

	private static void handleTestFail(GameTestState test) {
		Throwable throwable = test.getThrowable();
		String string = (test.isRequired() ? "" : "(optional) ") + test.getStructurePath() + " failed! " + Util.getInnermostMessage(throwable);
		sendMessage(test.getWorld(), test.isRequired() ? Formatting.RED : Formatting.YELLOW, string);
		if (throwable instanceof PositionedException) {
			PositionedException positionedException = (PositionedException)throwable;
			addDebugMarker(test.getWorld(), positionedException.getPos(), positionedException.getDebugMessage());
		}

		field_20573.onTestFailed(test);
	}

	private static void createBeacon(GameTestState test, Block glass) {
		ServerWorld serverWorld = test.getWorld();
		BlockPos blockPos = test.getPos();
		BlockPos blockPos2 = new BlockPos(-1, -1, -1);
		BlockPos blockPos3 = Structure.transformAround(blockPos.add(blockPos2), BlockMirror.NONE, test.method_29402(), blockPos);
		serverWorld.setBlockState(blockPos3, Blocks.BEACON.getDefaultState().rotate(test.method_29402()));
		BlockPos blockPos4 = blockPos3.add(0, 1, 0);
		serverWorld.setBlockState(blockPos4, glass.getDefaultState());

		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <= 1; j++) {
				BlockPos blockPos5 = blockPos3.add(i, -1, j);
				serverWorld.setBlockState(blockPos5, Blocks.IRON_BLOCK.getDefaultState());
			}
		}
	}

	private static void createLectern(GameTestState test, String message) {
		ServerWorld serverWorld = test.getWorld();
		BlockPos blockPos = test.getPos();
		BlockPos blockPos2 = new BlockPos(-1, 1, -1);
		BlockPos blockPos3 = Structure.transformAround(blockPos.add(blockPos2), BlockMirror.NONE, test.method_29402(), blockPos);
		serverWorld.setBlockState(blockPos3, Blocks.LECTERN.getDefaultState().rotate(test.method_29402()));
		BlockState blockState = serverWorld.getBlockState(blockPos3);
		ItemStack itemStack = createBook(test.getStructurePath(), test.isRequired(), message);
		LecternBlock.putBookIfAbsent(serverWorld, blockPos3, blockState, itemStack);
	}

	private static ItemStack createBook(String structureName, boolean required, String message) {
		ItemStack itemStack = new ItemStack(Items.WRITABLE_BOOK);
		NbtList nbtList = new NbtList();
		StringBuffer stringBuffer = new StringBuffer();
		Arrays.stream(structureName.split("\\.")).forEach(string -> stringBuffer.append(string).append('\n'));
		if (!required) {
			stringBuffer.append("(optional)\n");
		}

		stringBuffer.append("-------------------\n");
		nbtList.add(NbtString.of(stringBuffer.toString() + message));
		itemStack.putSubTag("pages", nbtList);
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
		BlockPos.stream(blockPos, blockPos2).filter(blockPosx -> world.getBlockState(blockPosx).isOf(Blocks.STRUCTURE_BLOCK)).forEach(blockPosx -> {
			StructureBlockBlockEntity structureBlockBlockEntity = (StructureBlockBlockEntity)world.getBlockEntity(blockPosx);
			BlockPos blockPos2x = structureBlockBlockEntity.getPos();
			BlockBox blockBox = StructureTestUtil.method_29410(structureBlockBlockEntity);
			StructureTestUtil.clearArea(blockBox, blockPos2x.getY(), world);
		});
	}
}
