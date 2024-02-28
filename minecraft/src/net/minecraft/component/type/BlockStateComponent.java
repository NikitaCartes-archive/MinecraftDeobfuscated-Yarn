package net.minecraft.component.type;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;
import net.minecraft.util.Util;

public record BlockStateComponent(Map<String, String> properties) {
	public static final BlockStateComponent DEFAULT = new BlockStateComponent(Map.of());
	public static final Codec<BlockStateComponent> CODEC = Codec.unboundedMap(Codec.STRING, Codec.STRING)
		.xmap(BlockStateComponent::new, BlockStateComponent::properties);
	private static final PacketCodec<ByteBuf, Map<String, String>> MAP_PACKET_CODEC = PacketCodecs.map(
		Object2ObjectOpenHashMap::new, PacketCodecs.STRING, PacketCodecs.STRING
	);
	public static final PacketCodec<ByteBuf, BlockStateComponent> PACKET_CODEC = MAP_PACKET_CODEC.xmap(BlockStateComponent::new, BlockStateComponent::properties);

	public <T extends Comparable<T>> BlockStateComponent with(Property<T> property, T value) {
		return new BlockStateComponent(Util.mapWith(this.properties, property.getName(), property.name(value)));
	}

	public <T extends Comparable<T>> BlockStateComponent with(Property<T> property, BlockState fromState) {
		return this.with(property, fromState.get(property));
	}

	@Nullable
	public <T extends Comparable<T>> T getValue(Property<T> property) {
		String string = (String)this.properties.get(property.getName());
		return (T)(string == null ? null : property.parse(string).orElse(null));
	}

	public BlockState applyToState(BlockState state) {
		StateManager<Block, BlockState> stateManager = state.getBlock().getStateManager();

		for (Entry<String, String> entry : this.properties.entrySet()) {
			Property<?> property = stateManager.getProperty((String)entry.getKey());
			if (property != null) {
				state = applyToState(state, property, (String)entry.getValue());
			}
		}

		return state;
	}

	private static <T extends Comparable<T>> BlockState applyToState(BlockState state, Property<T> property, String value) {
		return (BlockState)property.parse(value).map(valuex -> state.with(property, valuex)).orElse(state);
	}

	public boolean isEmpty() {
		return this.properties.isEmpty();
	}
}
