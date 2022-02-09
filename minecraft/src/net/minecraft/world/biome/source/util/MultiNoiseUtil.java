package net.minecraft.world.biome.source.util;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.source.BiomeCoords;
import net.minecraft.world.gen.NoiseColumnSampler;

public class MultiNoiseUtil {
	private static final boolean field_34477 = false;
	private static final float field_35359 = 10000.0F;
	@VisibleForTesting
	protected static final int HYPERCUBE_DIMENSION = 7;

	public static MultiNoiseUtil.NoiseValuePoint createNoiseValuePoint(
		float temperatureNoise, float humidityNoise, float continentalnessNoise, float erosionNoise, float depth, float weirdnessNoise
	) {
		return new MultiNoiseUtil.NoiseValuePoint(
			method_38665(temperatureNoise),
			method_38665(humidityNoise),
			method_38665(continentalnessNoise),
			method_38665(erosionNoise),
			method_38665(depth),
			method_38665(weirdnessNoise)
		);
	}

	public static MultiNoiseUtil.NoiseHypercube createNoiseHypercube(
		float temperature, float humidity, float continentalness, float erosion, float depth, float weirdness, float offset
	) {
		return new MultiNoiseUtil.NoiseHypercube(
			MultiNoiseUtil.ParameterRange.of(temperature),
			MultiNoiseUtil.ParameterRange.of(humidity),
			MultiNoiseUtil.ParameterRange.of(continentalness),
			MultiNoiseUtil.ParameterRange.of(erosion),
			MultiNoiseUtil.ParameterRange.of(depth),
			MultiNoiseUtil.ParameterRange.of(weirdness),
			method_38665(offset)
		);
	}

	public static MultiNoiseUtil.NoiseHypercube createNoiseHypercube(
		MultiNoiseUtil.ParameterRange temperature,
		MultiNoiseUtil.ParameterRange humidity,
		MultiNoiseUtil.ParameterRange continentalness,
		MultiNoiseUtil.ParameterRange erosion,
		MultiNoiseUtil.ParameterRange depth,
		MultiNoiseUtil.ParameterRange weirdness,
		float offset
	) {
		return new MultiNoiseUtil.NoiseHypercube(temperature, humidity, continentalness, erosion, depth, weirdness, method_38665(offset));
	}

	public static long method_38665(float f) {
		return (long)(f * 10000.0F);
	}

	public static float method_38666(long l) {
		return (float)l / 10000.0F;
	}

	public static BlockPos findFittestPosition(List<MultiNoiseUtil.NoiseHypercube> noises, NoiseColumnSampler sampler) {
		return (new MultiNoiseUtil.FittestPositionFinder(noises, sampler)).bestResult.location();
	}

	public static class Entries<T> {
		private final List<Pair<MultiNoiseUtil.NoiseHypercube, T>> entries;
		private final MultiNoiseUtil.SearchTree<T> tree;

		public Entries(List<Pair<MultiNoiseUtil.NoiseHypercube, T>> entries) {
			this.entries = entries;
			this.tree = MultiNoiseUtil.SearchTree.create(entries);
		}

		public List<Pair<MultiNoiseUtil.NoiseHypercube, T>> getEntries() {
			return this.entries;
		}

		public T method_39529(MultiNoiseUtil.NoiseValuePoint noiseValuePoint) {
			return this.method_39527(noiseValuePoint);
		}

		@VisibleForTesting
		public T method_39530(MultiNoiseUtil.NoiseValuePoint noiseValuePoint) {
			Iterator<Pair<MultiNoiseUtil.NoiseHypercube, T>> iterator = this.getEntries().iterator();
			Pair<MultiNoiseUtil.NoiseHypercube, T> pair = (Pair<MultiNoiseUtil.NoiseHypercube, T>)iterator.next();
			long l = pair.getFirst().getSquaredDistance(noiseValuePoint);
			T object = pair.getSecond();

			while (iterator.hasNext()) {
				Pair<MultiNoiseUtil.NoiseHypercube, T> pair2 = (Pair<MultiNoiseUtil.NoiseHypercube, T>)iterator.next();
				long m = pair2.getFirst().getSquaredDistance(noiseValuePoint);
				if (m < l) {
					l = m;
					object = pair2.getSecond();
				}
			}

			return object;
		}

		public T method_39527(MultiNoiseUtil.NoiseValuePoint noiseValuePoint) {
			return this.method_39528(noiseValuePoint, MultiNoiseUtil.SearchTree.TreeNode::getSquaredDistance);
		}

		protected T method_39528(MultiNoiseUtil.NoiseValuePoint noiseValuePoint, MultiNoiseUtil.NodeDistanceFunction<T> nodeDistanceFunction) {
			return this.tree.get(noiseValuePoint, nodeDistanceFunction);
		}
	}

	static class FittestPositionFinder {
		MultiNoiseUtil.FittestPositionFinder.Result bestResult;

		FittestPositionFinder(List<MultiNoiseUtil.NoiseHypercube> noises, NoiseColumnSampler sampler) {
			this.bestResult = calculateFitness(noises, sampler, 0, 0);
			this.findFittest(noises, sampler, 2048.0F, 512.0F);
			this.findFittest(noises, sampler, 512.0F, 32.0F);
		}

		private void findFittest(List<MultiNoiseUtil.NoiseHypercube> noises, NoiseColumnSampler sampler, float maxDistance, float step) {
			float f = 0.0F;
			float g = step;
			BlockPos blockPos = this.bestResult.location();

			while (g <= maxDistance) {
				int i = blockPos.getX() + (int)(Math.sin((double)f) * (double)g);
				int j = blockPos.getZ() + (int)(Math.cos((double)f) * (double)g);
				MultiNoiseUtil.FittestPositionFinder.Result result = calculateFitness(noises, sampler, i, j);
				if (result.fitness() < this.bestResult.fitness()) {
					this.bestResult = result;
				}

				f += step / g;
				if ((double)f > Math.PI * 2) {
					f = 0.0F;
					g += step;
				}
			}
		}

		private static MultiNoiseUtil.FittestPositionFinder.Result calculateFitness(
			List<MultiNoiseUtil.NoiseHypercube> noises, NoiseColumnSampler sampler, int x, int z
		) {
			double d = MathHelper.square(2500.0);
			int i = 2;
			long l = (long)((double)MathHelper.square(10000.0F) * Math.pow((double)(MathHelper.square((long)x) + MathHelper.square((long)z)) / d, 2.0));
			MultiNoiseUtil.NoiseValuePoint noiseValuePoint = sampler.sample(BiomeCoords.fromBlock(x), 0, BiomeCoords.fromBlock(z));
			MultiNoiseUtil.NoiseValuePoint noiseValuePoint2 = new MultiNoiseUtil.NoiseValuePoint(
				noiseValuePoint.temperatureNoise(),
				noiseValuePoint.humidityNoise(),
				noiseValuePoint.continentalnessNoise(),
				noiseValuePoint.erosionNoise(),
				0L,
				noiseValuePoint.weirdnessNoise()
			);
			long m = Long.MAX_VALUE;

			for (MultiNoiseUtil.NoiseHypercube noiseHypercube : noises) {
				m = Math.min(m, noiseHypercube.getSquaredDistance(noiseValuePoint2));
			}

			return new MultiNoiseUtil.FittestPositionFinder.Result(new BlockPos(x, 0, z), l + m);
		}

		static record Result(BlockPos location, long fitness) {
		}
	}

	public interface MultiNoiseSampler {
		MultiNoiseUtil.NoiseValuePoint sample(int x, int y, int z);

		default BlockPos findBestSpawnPosition() {
			return BlockPos.ORIGIN;
		}
	}

	interface NodeDistanceFunction<T> {
		long getDistance(MultiNoiseUtil.SearchTree.TreeNode<T> node, long[] ls);
	}

	/**
	 * Represents a hypercube in a multi-dimensional cartesian plane. The multi-noise
	 * biome source picks the closest noise hypercube from its selected point
	 * and chooses the biome associated to it.
	 */
	public static record NoiseHypercube(
		MultiNoiseUtil.ParameterRange temperature,
		MultiNoiseUtil.ParameterRange humidity,
		MultiNoiseUtil.ParameterRange continentalness,
		MultiNoiseUtil.ParameterRange erosion,
		MultiNoiseUtil.ParameterRange depth,
		MultiNoiseUtil.ParameterRange weirdness,
		long offset
	) {
		public static final Codec<MultiNoiseUtil.NoiseHypercube> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
						MultiNoiseUtil.ParameterRange.CODEC.fieldOf("temperature").forGetter(noiseHypercube -> noiseHypercube.temperature),
						MultiNoiseUtil.ParameterRange.CODEC.fieldOf("humidity").forGetter(noiseHypercube -> noiseHypercube.humidity),
						MultiNoiseUtil.ParameterRange.CODEC.fieldOf("continentalness").forGetter(noiseHypercube -> noiseHypercube.continentalness),
						MultiNoiseUtil.ParameterRange.CODEC.fieldOf("erosion").forGetter(noiseHypercube -> noiseHypercube.erosion),
						MultiNoiseUtil.ParameterRange.CODEC.fieldOf("depth").forGetter(noiseHypercube -> noiseHypercube.depth),
						MultiNoiseUtil.ParameterRange.CODEC.fieldOf("weirdness").forGetter(noiseHypercube -> noiseHypercube.weirdness),
						Codec.floatRange(0.0F, 1.0F)
							.fieldOf("offset")
							.xmap(MultiNoiseUtil::method_38665, MultiNoiseUtil::method_38666)
							.forGetter(noiseHypercube -> noiseHypercube.offset)
					)
					.apply(instance, MultiNoiseUtil.NoiseHypercube::new)
		);

		/**
		 * Calculates the distance from this noise point to another one. The
		 * distance is a squared distance in a multi-dimensional cartesian plane
		 * from a mathematical point of view, with a special parameter that
		 * reduces the calculated distance.
		 * 
		 * <p>For most fields except weight, smaller difference between
		 * two points' fields will lead to smaller distance. For weight,
		 * larger differences lead to smaller distance.
		 * 
		 * <p>This distance is used by the mixed-noise biome layer source. The
		 * layer source calculates an arbitrary noise point, and selects the
		 * biome that offers a closest point to its arbitrary point.
		 */
		long getSquaredDistance(MultiNoiseUtil.NoiseValuePoint point) {
			return MathHelper.square(this.temperature.getDistance(point.temperatureNoise))
				+ MathHelper.square(this.humidity.getDistance(point.humidityNoise))
				+ MathHelper.square(this.continentalness.getDistance(point.continentalnessNoise))
				+ MathHelper.square(this.erosion.getDistance(point.erosionNoise))
				+ MathHelper.square(this.depth.getDistance(point.depth))
				+ MathHelper.square(this.weirdness.getDistance(point.weirdnessNoise))
				+ MathHelper.square(this.offset);
		}

		protected List<MultiNoiseUtil.ParameterRange> getParameters() {
			return ImmutableList.of(
				this.temperature,
				this.humidity,
				this.continentalness,
				this.erosion,
				this.depth,
				this.weirdness,
				new MultiNoiseUtil.ParameterRange(this.offset, this.offset)
			);
		}
	}

	public static record NoiseValuePoint(long temperatureNoise, long humidityNoise, long continentalnessNoise, long erosionNoise, long depth, long weirdnessNoise) {

		@VisibleForTesting
		protected long[] getNoiseValueList() {
			return new long[]{this.temperatureNoise, this.humidityNoise, this.continentalnessNoise, this.erosionNoise, this.depth, this.weirdnessNoise, 0L};
		}
	}

	public static record ParameterRange(long min, long max) {
		public static final Codec<MultiNoiseUtil.ParameterRange> CODEC = Codecs.createCodecForPairObject(
			Codec.floatRange(-2.0F, 2.0F),
			"min",
			"max",
			(min, max) -> min.compareTo(max) > 0
					? DataResult.error("Cannon construct interval, min > max (" + min + " > " + max + ")")
					: DataResult.success(new MultiNoiseUtil.ParameterRange(MultiNoiseUtil.method_38665(min), MultiNoiseUtil.method_38665(max))),
			parameterRange -> MultiNoiseUtil.method_38666(parameterRange.min()),
			parameterRange -> MultiNoiseUtil.method_38666(parameterRange.max())
		);

		public static MultiNoiseUtil.ParameterRange of(float point) {
			return of(point, point);
		}

		public static MultiNoiseUtil.ParameterRange of(float min, float max) {
			if (min > max) {
				throw new IllegalArgumentException("min > max: " + min + " " + max);
			} else {
				return new MultiNoiseUtil.ParameterRange(MultiNoiseUtil.method_38665(min), MultiNoiseUtil.method_38665(max));
			}
		}

		/**
		 * Creates a new {@link MultiNoiseUtil.ParameterRange} that combines the parameters.
		 * 
		 * @return the created parameter range.
		 * 
		 * @param min this will be used for the created range's minimum value
		 * @param max this will be used for the created range's maximum value
		 */
		public static MultiNoiseUtil.ParameterRange combine(MultiNoiseUtil.ParameterRange min, MultiNoiseUtil.ParameterRange max) {
			if (min.min() > max.max()) {
				throw new IllegalArgumentException("min > max: " + min + " " + max);
			} else {
				return new MultiNoiseUtil.ParameterRange(min.min(), max.max());
			}
		}

		public String toString() {
			return this.min == this.max ? String.format("%d", this.min) : String.format("[%d-%d]", this.min, this.max);
		}

		public long getDistance(long noise) {
			long l = noise - this.max;
			long m = this.min - noise;
			return l > 0L ? l : Math.max(m, 0L);
		}

		public long getDistance(MultiNoiseUtil.ParameterRange other) {
			long l = other.min() - this.max;
			long m = this.min - other.max();
			return l > 0L ? l : Math.max(m, 0L);
		}

		public MultiNoiseUtil.ParameterRange combine(@Nullable MultiNoiseUtil.ParameterRange other) {
			return other == null ? this : new MultiNoiseUtil.ParameterRange(Math.min(this.min, other.min()), Math.max(this.max, other.max()));
		}
	}

	protected static final class SearchTree<T> {
		private static final int MAX_NODES_FOR_SIMPLE_TREE = 10;
		private final MultiNoiseUtil.SearchTree.TreeNode<T> firstNode;
		private final ThreadLocal<MultiNoiseUtil.SearchTree.TreeLeafNode<T>> previousResultNode = new ThreadLocal();

		private SearchTree(MultiNoiseUtil.SearchTree.TreeNode<T> firstNode) {
			this.firstNode = firstNode;
		}

		public static <T> MultiNoiseUtil.SearchTree<T> create(List<Pair<MultiNoiseUtil.NoiseHypercube, T>> entries) {
			if (entries.isEmpty()) {
				throw new IllegalArgumentException("Need at least one value to build the search tree.");
			} else {
				int i = ((MultiNoiseUtil.NoiseHypercube)((Pair)entries.get(0)).getFirst()).getParameters().size();
				if (i != 7) {
					throw new IllegalStateException("Expecting parameter space to be 7, got " + i);
				} else {
					List<MultiNoiseUtil.SearchTree.TreeLeafNode<T>> list = (List<MultiNoiseUtil.SearchTree.TreeLeafNode<T>>)entries.stream()
						.map(entry -> new MultiNoiseUtil.SearchTree.TreeLeafNode<>((MultiNoiseUtil.NoiseHypercube)entry.getFirst(), entry.getSecond()))
						.collect(Collectors.toCollection(ArrayList::new));
					return new MultiNoiseUtil.SearchTree<>(createNode(i, list));
				}
			}
		}

		private static <T> MultiNoiseUtil.SearchTree.TreeNode<T> createNode(int parameterNumber, List<? extends MultiNoiseUtil.SearchTree.TreeNode<T>> subTree) {
			if (subTree.isEmpty()) {
				throw new IllegalStateException("Need at least one child to build a node");
			} else if (subTree.size() == 1) {
				return (MultiNoiseUtil.SearchTree.TreeNode<T>)subTree.get(0);
			} else if (subTree.size() <= 10) {
				subTree.sort(Comparator.comparingLong(node -> {
					long lx = 0L;

					for (int jx = 0; jx < parameterNumber; jx++) {
						MultiNoiseUtil.ParameterRange parameterRange = node.parameters[jx];
						lx += Math.abs((parameterRange.min() + parameterRange.max()) / 2L);
					}

					return lx;
				}));
				return new MultiNoiseUtil.SearchTree.TreeBranchNode<>(subTree);
			} else {
				long l = Long.MAX_VALUE;
				int i = -1;
				List<MultiNoiseUtil.SearchTree.TreeBranchNode<T>> list = null;

				for (int j = 0; j < parameterNumber; j++) {
					sortTree(subTree, parameterNumber, j, false);
					List<MultiNoiseUtil.SearchTree.TreeBranchNode<T>> list2 = getBatchedTree(subTree);
					long m = 0L;

					for (MultiNoiseUtil.SearchTree.TreeBranchNode<T> treeBranchNode : list2) {
						m += getRangeLengthSum(treeBranchNode.parameters);
					}

					if (l > m) {
						l = m;
						i = j;
						list = list2;
					}
				}

				sortTree(list, parameterNumber, i, true);
				return new MultiNoiseUtil.SearchTree.TreeBranchNode<>(
					(List<? extends MultiNoiseUtil.SearchTree.TreeNode<T>>)list.stream()
						.map(node -> createNode(parameterNumber, Arrays.asList(node.subTree)))
						.collect(Collectors.toList())
				);
			}
		}

		private static <T> void sortTree(List<? extends MultiNoiseUtil.SearchTree.TreeNode<T>> subTree, int parameterNumber, int currentParameter, boolean abs) {
			Comparator<MultiNoiseUtil.SearchTree.TreeNode<T>> comparator = createNodeComparator(currentParameter, abs);

			for (int i = 1; i < parameterNumber; i++) {
				comparator = comparator.thenComparing(createNodeComparator((currentParameter + i) % parameterNumber, abs));
			}

			subTree.sort(comparator);
		}

		private static <T> Comparator<MultiNoiseUtil.SearchTree.TreeNode<T>> createNodeComparator(int currentParameter, boolean abs) {
			return Comparator.comparingLong(treeNode -> {
				MultiNoiseUtil.ParameterRange parameterRange = treeNode.parameters[currentParameter];
				long l = (parameterRange.min() + parameterRange.max()) / 2L;
				return abs ? Math.abs(l) : l;
			});
		}

		private static <T> List<MultiNoiseUtil.SearchTree.TreeBranchNode<T>> getBatchedTree(List<? extends MultiNoiseUtil.SearchTree.TreeNode<T>> nodes) {
			List<MultiNoiseUtil.SearchTree.TreeBranchNode<T>> list = Lists.<MultiNoiseUtil.SearchTree.TreeBranchNode<T>>newArrayList();
			List<MultiNoiseUtil.SearchTree.TreeNode<T>> list2 = Lists.<MultiNoiseUtil.SearchTree.TreeNode<T>>newArrayList();
			int i = (int)Math.pow(10.0, Math.floor(Math.log((double)nodes.size() - 0.01) / Math.log(10.0)));

			for (MultiNoiseUtil.SearchTree.TreeNode<T> treeNode : nodes) {
				list2.add(treeNode);
				if (list2.size() >= i) {
					list.add(new MultiNoiseUtil.SearchTree.TreeBranchNode(list2));
					list2 = Lists.<MultiNoiseUtil.SearchTree.TreeNode<T>>newArrayList();
				}
			}

			if (!list2.isEmpty()) {
				list.add(new MultiNoiseUtil.SearchTree.TreeBranchNode(list2));
			}

			return list;
		}

		private static long getRangeLengthSum(MultiNoiseUtil.ParameterRange[] parameters) {
			long l = 0L;

			for (MultiNoiseUtil.ParameterRange parameterRange : parameters) {
				l += Math.abs(parameterRange.max() - parameterRange.min());
			}

			return l;
		}

		static <T> List<MultiNoiseUtil.ParameterRange> getEnclosingParameters(List<? extends MultiNoiseUtil.SearchTree.TreeNode<T>> subTree) {
			if (subTree.isEmpty()) {
				throw new IllegalArgumentException("SubTree needs at least one child");
			} else {
				int i = 7;
				List<MultiNoiseUtil.ParameterRange> list = Lists.<MultiNoiseUtil.ParameterRange>newArrayList();

				for (int j = 0; j < 7; j++) {
					list.add(null);
				}

				for (MultiNoiseUtil.SearchTree.TreeNode<T> treeNode : subTree) {
					for (int k = 0; k < 7; k++) {
						list.set(k, treeNode.parameters[k].combine((MultiNoiseUtil.ParameterRange)list.get(k)));
					}
				}

				return list;
			}
		}

		public T get(MultiNoiseUtil.NoiseValuePoint point, MultiNoiseUtil.NodeDistanceFunction<T> distanceFunction) {
			long[] ls = point.getNoiseValueList();
			MultiNoiseUtil.SearchTree.TreeLeafNode<T> treeLeafNode = this.firstNode
				.getResultingNode(ls, (MultiNoiseUtil.SearchTree.TreeLeafNode<T>)this.previousResultNode.get(), distanceFunction);
			this.previousResultNode.set(treeLeafNode);
			return treeLeafNode.value;
		}

		static final class TreeBranchNode<T> extends MultiNoiseUtil.SearchTree.TreeNode<T> {
			final MultiNoiseUtil.SearchTree.TreeNode<T>[] subTree;

			protected TreeBranchNode(List<? extends MultiNoiseUtil.SearchTree.TreeNode<T>> list) {
				this(MultiNoiseUtil.SearchTree.getEnclosingParameters(list), list);
			}

			protected TreeBranchNode(List<MultiNoiseUtil.ParameterRange> parameters, List<? extends MultiNoiseUtil.SearchTree.TreeNode<T>> subTree) {
				super(parameters);
				this.subTree = (MultiNoiseUtil.SearchTree.TreeNode<T>[])subTree.toArray(new MultiNoiseUtil.SearchTree.TreeNode[0]);
			}

			@Override
			protected MultiNoiseUtil.SearchTree.TreeLeafNode<T> getResultingNode(
				long[] otherParameters, @Nullable MultiNoiseUtil.SearchTree.TreeLeafNode<T> alternative, MultiNoiseUtil.NodeDistanceFunction<T> distanceFunction
			) {
				long l = alternative == null ? Long.MAX_VALUE : distanceFunction.getDistance(alternative, otherParameters);
				MultiNoiseUtil.SearchTree.TreeLeafNode<T> treeLeafNode = alternative;

				for (MultiNoiseUtil.SearchTree.TreeNode<T> treeNode : this.subTree) {
					long m = distanceFunction.getDistance(treeNode, otherParameters);
					if (l > m) {
						MultiNoiseUtil.SearchTree.TreeLeafNode<T> treeLeafNode2 = treeNode.getResultingNode(otherParameters, treeLeafNode, distanceFunction);
						long n = treeNode == treeLeafNode2 ? m : distanceFunction.getDistance(treeLeafNode2, otherParameters);
						if (l > n) {
							l = n;
							treeLeafNode = treeLeafNode2;
						}
					}
				}

				return treeLeafNode;
			}
		}

		static final class TreeLeafNode<T> extends MultiNoiseUtil.SearchTree.TreeNode<T> {
			final T value;

			TreeLeafNode(MultiNoiseUtil.NoiseHypercube parameters, T value) {
				super(parameters.getParameters());
				this.value = value;
			}

			@Override
			protected MultiNoiseUtil.SearchTree.TreeLeafNode<T> getResultingNode(
				long[] otherParameters, @Nullable MultiNoiseUtil.SearchTree.TreeLeafNode<T> alternative, MultiNoiseUtil.NodeDistanceFunction<T> distanceFunction
			) {
				return this;
			}
		}

		abstract static class TreeNode<T> {
			protected final MultiNoiseUtil.ParameterRange[] parameters;

			protected TreeNode(List<MultiNoiseUtil.ParameterRange> parameters) {
				this.parameters = (MultiNoiseUtil.ParameterRange[])parameters.toArray(new MultiNoiseUtil.ParameterRange[0]);
			}

			protected abstract MultiNoiseUtil.SearchTree.TreeLeafNode<T> getResultingNode(
				long[] otherParameters, @Nullable MultiNoiseUtil.SearchTree.TreeLeafNode<T> alternative, MultiNoiseUtil.NodeDistanceFunction<T> distanceFunction
			);

			protected long getSquaredDistance(long[] otherParameters) {
				long l = 0L;

				for (int i = 0; i < 7; i++) {
					l += MathHelper.square(this.parameters[i].getDistance(otherParameters[i]));
				}

				return l;
			}

			public String toString() {
				return Arrays.toString(this.parameters);
			}
		}
	}
}
