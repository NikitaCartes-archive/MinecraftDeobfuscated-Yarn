package net.minecraft;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Stream;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;
import net.minecraft.text.Text;
import net.minecraft.util.math.random.Random;

public class class_8307 extends class_8285<Block> {
	private final Map<RegistryKey<Block>, class_8307.class_8309> field_43752 = new HashMap();
	private boolean field_43753;

	public class_8307() {
		super(RegistryKeys.BLOCK);
	}

	protected Text method_50162(RegistryKey<Block> registryKey, RegistryKey<Block> registryKey2) {
		Text text = Registries.BLOCK.get(registryKey.getValue()).getName();
		Text text2 = Registries.BLOCK.get(registryKey2.getValue()).getName();
		return Text.translatable("rule.replace_block_model", text, text2);
	}

	@Override
	public Stream<class_8291> method_50118(MinecraftServer minecraftServer, Random random, int i) {
		Registry<Block> registry = minecraftServer.getRegistryManager().get(RegistryKeys.BLOCK);
		Set<class_8307.class_8308> set = new HashSet();

		for (int j = 0; j < 30; j++) {
			RegistryEntry.Reference<Block> reference = (RegistryEntry.Reference<Block>)registry.getRandom(random).orElse(null);
			RegistryEntry.Reference<Block> reference2 = (RegistryEntry.Reference<Block>)registry.getRandom(random).orElse(null);
			if (reference != null
				&& !reference.equals(reference2)
				&& reference2 != null
				&& reference.value().getDefaultState().getRenderType() == BlockRenderType.MODEL
				&& reference2.value().getDefaultState().getRenderType() == BlockRenderType.MODEL
				&& method_50309(reference.value(), reference2.value())) {
				set.add(new class_8307.class_8308(reference, reference2));
			}
		}

		List<class_8307.class_8308> list = new ArrayList(set);
		list.sort(Comparator.comparingInt(arg -> -arg.target.value().getStateManager().getProperties().size()));
		return list.stream().limit((long)i).map(arg -> new class_8275.class_8276(arg.source().registryKey(), arg.target().registryKey()));
	}

	@Override
	protected void method_50136(RegistryKey<Block> registryKey) {
		super.method_50136(registryKey);
		if (this.field_43752.remove(registryKey) != null) {
			this.field_43753 = true;
		}
	}

	public boolean method_50307() {
		boolean bl = this.field_43753;
		this.field_43753 = false;
		return bl;
	}

	@Override
	protected void method_50138(RegistryKey<Block> registryKey, RegistryKey<Block> registryKey2) {
		super.method_50138(registryKey, registryKey2);
		Block block = Registries.BLOCK.get(registryKey2);
		Block block2 = Registries.BLOCK.get(registryKey);
		if (block != null && block2 != null) {
			Builder<BlockState, BlockState> builder = ImmutableMap.builder();
			method_50310(block2, block, builder::put);
			Map<BlockState, BlockState> map = builder.build();
			this.field_43752.put(registryKey, new class_8307.class_8309(map));
			this.field_43753 = true;
		}
	}

	public BlockState method_50311(BlockState blockState) {
		class_8307.class_8309 lv = (class_8307.class_8309)this.field_43752.get(blockState.getBlock().getRegistryEntry().registryKey());
		return lv != null ? lv.method_50316(blockState) : blockState;
	}

	private static boolean method_50313(Property<?> property, Property<?> property2) {
		Set<Object> set = Set.copyOf(property2.getValues());
		Set<Object> set2 = new HashSet(property.getValues());
		set2.removeAll(set);
		return set2.isEmpty();
	}

	public static boolean method_50309(Block block, Block block2) {
		StateManager<Block, BlockState> stateManager = block2.getStateManager();
		StateManager<Block, BlockState> stateManager2 = block.getStateManager();
		Set<Property<?>> set = new HashSet(stateManager.getProperties());
		if (set.isEmpty()) {
			return true;
		} else {
			for (Property<?> property : stateManager2.getProperties()) {
				Property<?> property2 = stateManager.getProperty(property.getName());
				if (property2 != null && method_50313(property, property2)) {
					set.remove(property2);
				}
			}

			return set.isEmpty();
		}
	}

	public static void method_50310(Block block, Block block2, BiConsumer<BlockState, BlockState> biConsumer) {
		StateManager<Block, BlockState> stateManager = block2.getStateManager();
		StateManager<Block, BlockState> stateManager2 = block.getStateManager();
		Set<String> set = new HashSet();

		for (Property<?> property : stateManager2.getProperties()) {
			String string = property.getName();
			Property<?> property2 = stateManager.getProperty(string);
			if (property2 != null && method_50313(property, property2)) {
				set.add(string);
			}
		}

		BlockState blockState = block2.getDefaultState();

		for (BlockState blockState2 : stateManager2.getStates()) {
			BlockState blockState3 = blockState;

			for (String string2 : set) {
				blockState3 = method_50312(blockState2, stateManager2, blockState3, stateManager, string2);
			}

			biConsumer.accept(blockState2, blockState3);
		}
	}

	private static <T extends Comparable<T>> BlockState method_50312(
		BlockState blockState, StateManager<Block, BlockState> stateManager, BlockState blockState2, StateManager<Block, BlockState> stateManager2, String string
	) {
		try {
			Property<T> property = (Property<T>)stateManager.getProperty(string);
			Property<T> property2 = (Property<T>)stateManager2.getProperty(string);
			T comparable = blockState.get(property);
			return blockState2.with(property2, comparable);
		} catch (Exception var8) {
			return blockState2;
		}
	}

	static record class_8308(RegistryEntry.Reference<Block> source, RegistryEntry.Reference<Block> target) {
	}

	static record class_8309(Map<BlockState, BlockState> replacements) {
		public BlockState method_50316(BlockState blockState) {
			return (BlockState)this.replacements.getOrDefault(blockState, blockState);
		}
	}
}
