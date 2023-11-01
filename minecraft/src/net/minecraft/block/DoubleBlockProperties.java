package net.minecraft.block;

import java.util.function.BiPredicate;
import java.util.function.Function;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldAccess;

public class DoubleBlockProperties {
	public static <S extends BlockEntity> DoubleBlockProperties.PropertySource<S> toPropertySource(
		BlockEntityType<S> blockEntityType,
		Function<BlockState, DoubleBlockProperties.Type> typeMapper,
		Function<BlockState, Direction> directionMapper,
		DirectionProperty directionProperty,
		BlockState state,
		WorldAccess world,
		BlockPos pos,
		BiPredicate<WorldAccess, BlockPos> fallbackTester
	) {
		S blockEntity = blockEntityType.get(world, pos);
		if (blockEntity == null) {
			return DoubleBlockProperties.PropertyRetriever::getFallback;
		} else if (fallbackTester.test(world, pos)) {
			return DoubleBlockProperties.PropertyRetriever::getFallback;
		} else {
			DoubleBlockProperties.Type type = (DoubleBlockProperties.Type)typeMapper.apply(state);
			boolean bl = type == DoubleBlockProperties.Type.SINGLE;
			boolean bl2 = type == DoubleBlockProperties.Type.FIRST;
			if (bl) {
				return new DoubleBlockProperties.PropertySource.Single<>(blockEntity);
			} else {
				BlockPos blockPos = pos.offset((Direction)directionMapper.apply(state));
				BlockState blockState = world.getBlockState(blockPos);
				if (blockState.isOf(state.getBlock())) {
					DoubleBlockProperties.Type type2 = (DoubleBlockProperties.Type)typeMapper.apply(blockState);
					if (type2 != DoubleBlockProperties.Type.SINGLE && type != type2 && blockState.get(directionProperty) == state.get(directionProperty)) {
						if (fallbackTester.test(world, blockPos)) {
							return DoubleBlockProperties.PropertyRetriever::getFallback;
						}

						S blockEntity2 = blockEntityType.get(world, blockPos);
						if (blockEntity2 != null) {
							S blockEntity3 = bl2 ? blockEntity : blockEntity2;
							S blockEntity4 = bl2 ? blockEntity2 : blockEntity;
							return new DoubleBlockProperties.PropertySource.Pair<>(blockEntity3, blockEntity4);
						}
					}
				}

				return new DoubleBlockProperties.PropertySource.Single<>(blockEntity);
			}
		}
	}

	public interface PropertyRetriever<S, T> {
		T getFromBoth(S first, S second);

		T getFrom(S single);

		T getFallback();
	}

	public interface PropertySource<S> {
		<T> T apply(DoubleBlockProperties.PropertyRetriever<? super S, T> retriever);

		public static final class Pair<S> implements DoubleBlockProperties.PropertySource<S> {
			private final S first;
			private final S second;

			public Pair(S first, S second) {
				this.first = first;
				this.second = second;
			}

			@Override
			public <T> T apply(DoubleBlockProperties.PropertyRetriever<? super S, T> propertyRetriever) {
				return propertyRetriever.getFromBoth(this.first, this.second);
			}
		}

		public static final class Single<S> implements DoubleBlockProperties.PropertySource<S> {
			private final S single;

			public Single(S single) {
				this.single = single;
			}

			@Override
			public <T> T apply(DoubleBlockProperties.PropertyRetriever<? super S, T> propertyRetriever) {
				return propertyRetriever.getFrom(this.single);
			}
		}
	}

	public static enum Type {
		SINGLE,
		FIRST,
		SECOND;
	}
}
