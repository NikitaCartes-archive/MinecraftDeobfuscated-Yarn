package net.minecraft.world.gen.stateprovider;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Collection;
import java.util.Optional;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.UniformIntDistribution;

/**
 * A {@linkplain BlockStateProvider block state provider} that randomizes a single {@link IntProperty} of a block state provided by another provider.
 */
public class RandomizedIntBlockStateProvider extends BlockStateProvider {
	public static final Codec<RandomizedIntBlockStateProvider> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					BlockStateProvider.TYPE_CODEC.fieldOf("source").forGetter(randomizedIntBlockStateProvider -> randomizedIntBlockStateProvider.source),
					Codec.STRING.fieldOf("property").forGetter(randomizedIntBlockStateProvider -> randomizedIntBlockStateProvider.propertyName),
					UniformIntDistribution.CODEC.fieldOf("values").forGetter(randomizedIntBlockStateProvider -> randomizedIntBlockStateProvider.values)
				)
				.apply(instance, RandomizedIntBlockStateProvider::new)
	);
	private final BlockStateProvider source;
	private final String propertyName;
	@Nullable
	private IntProperty property;
	private final UniformIntDistribution values;

	public RandomizedIntBlockStateProvider(BlockStateProvider source, IntProperty property, UniformIntDistribution values) {
		this.source = source;
		this.property = property;
		this.propertyName = property.getName();
		this.values = values;
		Collection<Integer> collection = property.getValues();

		for (int i = values.minValue(); i < values.maxValue(); i++) {
			if (!collection.contains(i)) {
				throw new IllegalArgumentException("Property value out of range: " + property.getName() + ": " + i);
			}
		}
	}

	public RandomizedIntBlockStateProvider(BlockStateProvider source, String propertyName, UniformIntDistribution values) {
		this.source = source;
		this.propertyName = propertyName;
		this.values = values;
	}

	@Override
	protected BlockStateProviderType<?> getType() {
		return BlockStateProviderType.RANDOMIZED_INT_STATE_PROVIDER;
	}

	@Override
	public BlockState getBlockState(Random random, BlockPos pos) {
		BlockState blockState = this.source.getBlockState(random, pos);
		if (this.property == null || !blockState.contains(this.property)) {
			this.property = getIntPropertyByName(blockState, this.propertyName);
		}

		return blockState.with(this.property, Integer.valueOf(this.values.getValue(random)));
	}

	private static IntProperty getIntPropertyByName(BlockState state, String propertyName) {
		Collection<Property<?>> collection = state.getProperties();
		Optional<IntProperty> optional = collection.stream()
			.filter(property -> property.getName().equals(propertyName))
			.filter(property -> property instanceof IntProperty)
			.map(property -> (IntProperty)property)
			.findAny();
		return (IntProperty)optional.orElseThrow(() -> new IllegalArgumentException("Illegal property: " + propertyName));
	}
}
