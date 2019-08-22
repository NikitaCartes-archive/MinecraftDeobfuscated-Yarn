package net.minecraft;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
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
import net.minecraft.client.network.DebugRendererInfoManager;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.test.GameTestBatch;
import net.minecraft.test.TestFunction;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.math.BlockPos;

public class class_4520 {
	public static class_4531 field_20573 = new class_4523();

	public static void method_22203(class_4517 arg, class_4521 arg2) {
		arg2.method_22227(arg);
		arg.method_22167(new class_4518() {
			@Override
			public void method_22188(class_4517 arg) {
				class_4520.method_22220(arg, Blocks.LIGHT_GRAY_STAINED_GLASS);
			}

			@Override
			public void method_22189(class_4517 arg) {
				class_4520.method_22220(arg, Blocks.LIME_STAINED_GLASS);
				class_4520.method_22225(arg);
			}

			@Override
			public void method_22190(class_4517 arg) {
				class_4520.method_22220(arg, arg.method_22183() ? Blocks.RED_STAINED_GLASS : Blocks.ORANGE_STAINED_GLASS);
				class_4520.method_22221(arg, SystemUtil.method_22321(arg.method_22182()));
				class_4520.method_22224(arg);
			}
		});
		arg.method_22166(2);
	}

	public static Collection<class_4517> method_22210(Collection<GameTestBatch> collection, BlockPos blockPos, ServerWorld serverWorld, class_4521 arg) {
		class_4515 lv = new class_4515(collection, blockPos, serverWorld, arg);
		lv.method_22160();
		return lv.method_22155();
	}

	public static Collection<class_4517> method_22222(Collection<TestFunction> collection, BlockPos blockPos, ServerWorld serverWorld, class_4521 arg) {
		return method_22210(method_22209(collection), blockPos, serverWorld, arg);
	}

	public static Collection<GameTestBatch> method_22209(Collection<TestFunction> collection) {
		Map<String, Collection<TestFunction>> map = Maps.<String, Collection<TestFunction>>newHashMap();
		collection.forEach(testFunction -> {
			String string = testFunction.method_22301();
			Collection<TestFunction> collectionx = (Collection<TestFunction>)map.computeIfAbsent(string, stringx -> Lists.newArrayList());
			collectionx.add(testFunction);
		});
		return (Collection<GameTestBatch>)map.keySet().stream().map(string -> {
			Collection<TestFunction> collectionx = (Collection<TestFunction>)map.get(string);
			Consumer<ServerWorld> consumer = class_4519.method_22198(string);
			return new GameTestBatch(string, collectionx, consumer);
		}).collect(Collectors.toList());
	}

	private static void method_22224(class_4517 arg) {
		Throwable throwable = arg.method_22182();
		String string = arg.method_22169() + " failed! " + SystemUtil.method_22321(throwable);
		method_22214(arg.getWorld(), Formatting.RED, string);
		if (throwable instanceof class_4513) {
			class_4513 lv = (class_4513)throwable;
			method_22217(arg.getWorld(), lv.method_22151(), lv.method_22150());
		}

		field_20573.method_22304(arg);
	}

	private static void method_22225(class_4517 arg) {
		method_22214(arg.getWorld(), Formatting.GREEN, arg.method_22169() + " passed!");
		field_20573.method_22305(arg);
	}

	private static void method_22220(class_4517 arg, Block block) {
		ServerWorld serverWorld = arg.getWorld();
		BlockPos blockPos = arg.getBlockPos();
		BlockPos blockPos2 = blockPos.add(-1, -1, -1);
		serverWorld.setBlockState(blockPos2, Blocks.BEACON.getDefaultState());
		BlockPos blockPos3 = blockPos2.add(0, 1, 0);
		serverWorld.setBlockState(blockPos3, block.getDefaultState());

		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <= 1; j++) {
				BlockPos blockPos4 = blockPos2.add(i, -1, j);
				serverWorld.setBlockState(blockPos4, Blocks.IRON_BLOCK.getDefaultState());
			}
		}
	}

	private static void method_22221(class_4517 arg, String string) {
		ServerWorld serverWorld = arg.getWorld();
		BlockPos blockPos = arg.getBlockPos();
		BlockPos blockPos2 = blockPos.add(-1, 1, -1);
		serverWorld.setBlockState(blockPos2, Blocks.LECTERN.getDefaultState());
		BlockState blockState = serverWorld.getBlockState(blockPos2);
		ItemStack itemStack = method_22207(arg.method_22169(), arg.method_22183(), string);
		LecternBlock.putBookIfAbsent(serverWorld, blockPos2, blockState, itemStack);
	}

	private static ItemStack method_22207(String string, boolean bl, String string2) {
		ItemStack itemStack = new ItemStack(Items.WRITABLE_BOOK);
		ListTag listTag = new ListTag();
		StringBuffer stringBuffer = new StringBuffer();
		Arrays.stream(string.split("\\.")).forEach(stringx -> stringBuffer.append(stringx).append('\n'));
		if (!bl) {
			stringBuffer.append("(optional)\n");
		}

		stringBuffer.append("-------------------\n");
		listTag.add(new StringTag(stringBuffer.toString() + string2));
		itemStack.putSubTag("pages", listTag);
		return itemStack;
	}

	private static void method_22214(ServerWorld serverWorld, Formatting formatting, String string) {
		serverWorld.getPlayers(serverPlayerEntity -> true)
			.forEach(serverPlayerEntity -> serverPlayerEntity.sendMessage(new LiteralText(string).formatted(formatting)));
	}

	public static void method_22213(ServerWorld serverWorld) {
		DebugRendererInfoManager.method_22317(serverWorld);
	}

	private static void method_22217(ServerWorld serverWorld, BlockPos blockPos, String string) {
		DebugRendererInfoManager.method_22318(serverWorld, blockPos, string, -2130771968, Integer.MAX_VALUE);
	}

	public static void method_22216(ServerWorld serverWorld, BlockPos blockPos, class_4521 arg, int i) {
		arg.method_22226();
		BlockPos blockPos2 = blockPos.add(-i, 0, -i);
		BlockPos blockPos3 = blockPos.add(i, 0, i);
		BlockPos.stream(blockPos2, blockPos3).filter(blockPosx -> serverWorld.getBlockState(blockPosx).getBlock() == Blocks.STRUCTURE_BLOCK).forEach(blockPosx -> {
			StructureBlockBlockEntity structureBlockBlockEntity = (StructureBlockBlockEntity)serverWorld.getBlockEntity(blockPosx);
			class_4525.method_22243(structureBlockBlockEntity, 2, serverWorld);
		});
	}
}
