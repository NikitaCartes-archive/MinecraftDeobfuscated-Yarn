package net.minecraft.world.biome.source.util;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;

public class MultiNoiseUtil {
	private static final int HYPERCUBE_DIMENSION = 7;

	public static MultiNoiseUtil.NoiseValuePoint createNoiseValuePoint(
		float temperatureNoise, float humidityNoise, float continentalnessNoise, float erosionNoise, float depth, float weirdnessNoise
	) {
		return new MultiNoiseUtil.NoiseValuePoint(temperatureNoise, humidityNoise, continentalnessNoise, erosionNoise, depth, weirdnessNoise);
	}

	public static MultiNoiseUtil.NoiseHypercube createNoiseHypercube(
		float temperature, float humidity, float continentalness, float erosion, float depth, float weirdness, float offset
	) {
		return new MultiNoiseUtil.NoiseHypercube(
			createParameterRange(temperature),
			createParameterRange(humidity),
			createParameterRange(continentalness),
			createParameterRange(erosion),
			createParameterRange(depth),
			createParameterRange(weirdness),
			offset
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
		return new MultiNoiseUtil.NoiseHypercube(temperature, humidity, continentalness, erosion, depth, weirdness, offset);
	}

	public static MultiNoiseUtil.ParameterRange createParameterRange(float point) {
		return createParameterRange(point, point);
	}

	public static MultiNoiseUtil.ParameterRange createParameterRange(float min, float max) {
		return new MultiNoiseUtil.ParameterRange(min, max);
	}

	/**
	 * Creates a new {@link MultiNoiseUtil.ParameterRange} that combines the parameters.
	 * 
	 * @return the created parameter range.
	 * 
	 * @param min this will be used for the created range's minimum value
	 * @param max this will be used for the created range's maximum value
	 */
	public static MultiNoiseUtil.ParameterRange combineParameterRange(MultiNoiseUtil.ParameterRange min, MultiNoiseUtil.ParameterRange max) {
		return new MultiNoiseUtil.ParameterRange(min.getMin(), max.getMax());
	}

	public static class Entries<T> {
		private final List<Pair<MultiNoiseUtil.NoiseHypercube, Supplier<T>>> entries;
		private final MultiNoiseUtil.SearchTree<T> tree;

		public Entries(List<Pair<MultiNoiseUtil.NoiseHypercube, Supplier<T>>> entries) {
			this.entries = entries;
			this.tree = MultiNoiseUtil.SearchTree.create(entries);
		}

		public List<Pair<MultiNoiseUtil.NoiseHypercube, Supplier<T>>> getEntries() {
			return this.entries;
		}

		public T getValue(MultiNoiseUtil.NoiseValuePoint point, Supplier<T> defaultValue) {
			return this.getValue(point);
		}

		public T getValueSimple(MultiNoiseUtil.NoiseValuePoint point, Supplier<T> defaultValue) {
			float f = Float.MAX_VALUE;
			Supplier<T> supplier = defaultValue;

			for (Pair<MultiNoiseUtil.NoiseHypercube, Supplier<T>> pair : this.getEntries()) {
				float g = pair.getFirst().getSquaredDistance(point);
				if (g < f) {
					f = g;
					supplier = pair.getSecond();
				}
			}

			return (T)supplier.get();
		}

		public T getValue(MultiNoiseUtil.NoiseValuePoint point) {
			return this.getValue(point, (MultiNoiseUtil.NodeDistanceFunction<T>)((node, otherParameters) -> node.getSquaredDistance(otherParameters)));
		}

		T getValue(MultiNoiseUtil.NoiseValuePoint point, MultiNoiseUtil.NodeDistanceFunction<T> distanceFunction) {
			return this.tree.get(point, distanceFunction);
		}
	}

	interface NodeDistanceFunction<T> {
		float getDistance(MultiNoiseUtil.SearchTree.TreeNode<T> node, float[] otherParameters);
	}

	/**
	 * Represents a hypercube in a multi-dimensional cartesian plane. The multi-noise
	 * biome source picks the closest noise hypercube from its selected point
	 * and chooses the biome associated to it.
	 */
	public static final class NoiseHypercube {
		public static final Codec<MultiNoiseUtil.NoiseHypercube> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
						MultiNoiseUtil.ParameterRange.CODEC.fieldOf("temperature").forGetter(noiseHypercube -> noiseHypercube.temperature),
						MultiNoiseUtil.ParameterRange.CODEC.fieldOf("humidity").forGetter(noiseHypercube -> noiseHypercube.humidity),
						MultiNoiseUtil.ParameterRange.CODEC.fieldOf("continentalness").forGetter(noiseHypercube -> noiseHypercube.continentalness),
						MultiNoiseUtil.ParameterRange.CODEC.fieldOf("erosion").forGetter(noiseHypercube -> noiseHypercube.erosion),
						MultiNoiseUtil.ParameterRange.CODEC.fieldOf("depth").forGetter(noiseHypercube -> noiseHypercube.depth),
						MultiNoiseUtil.ParameterRange.CODEC.fieldOf("weirdness").forGetter(noiseHypercube -> noiseHypercube.weirdness),
						Codec.floatRange(0.0F, 1.0F).fieldOf("offset").forGetter(noiseHypercube -> noiseHypercube.offset)
					)
					.apply(instance, MultiNoiseUtil.NoiseHypercube::new)
		);
		private final MultiNoiseUtil.ParameterRange temperature;
		private final MultiNoiseUtil.ParameterRange humidity;
		private final MultiNoiseUtil.ParameterRange continentalness;
		private final MultiNoiseUtil.ParameterRange erosion;
		private final MultiNoiseUtil.ParameterRange depth;
		private final MultiNoiseUtil.ParameterRange weirdness;
		/**
		 * This value works differently from the other parameters, in that it is
		 * always {@code 0} during biome generation and does not use noise.
		 * This means that setting it to a non-null number will make the biome smaller.
		 * The farther {@code offset} is from {@code 0}, the smaller the biome will be.
		 * For this, it does not matter whether {@code offset} is positive or negative.
		 */
		private final float offset;
		private final List<MultiNoiseUtil.ParameterRange> parameters;

		NoiseHypercube(
			MultiNoiseUtil.ParameterRange temperature,
			MultiNoiseUtil.ParameterRange humidity,
			MultiNoiseUtil.ParameterRange continentalness,
			MultiNoiseUtil.ParameterRange erosion,
			MultiNoiseUtil.ParameterRange depth,
			MultiNoiseUtil.ParameterRange weirdness,
			float offset
		) {
			this.temperature = temperature;
			this.humidity = humidity;
			this.continentalness = continentalness;
			this.erosion = erosion;
			this.depth = depth;
			this.weirdness = weirdness;
			this.offset = offset;
			this.parameters = ImmutableList.of(temperature, humidity, continentalness, erosion, depth, weirdness, new MultiNoiseUtil.ParameterRange(offset, offset));
		}

		public String toString() {
			return "temp: "
				+ this.temperature
				+ ", hum: "
				+ this.humidity
				+ ", alt: "
				+ this.continentalness
				+ ", str: "
				+ this.erosion
				+ ", depth: "
				+ this.depth
				+ ", weird: "
				+ this.weirdness
				+ ", offset: "
				+ this.offset;
		}

		public boolean equals(Object o) {
			if (this == o) {
				return true;
			} else if (o != null && this.getClass() == o.getClass()) {
				MultiNoiseUtil.NoiseHypercube noiseHypercube = (MultiNoiseUtil.NoiseHypercube)o;
				if (!this.temperature.equals(noiseHypercube.temperature)) {
					return false;
				} else if (!this.humidity.equals(noiseHypercube.humidity)) {
					return false;
				} else if (!this.continentalness.equals(noiseHypercube.continentalness)) {
					return false;
				} else if (!this.erosion.equals(noiseHypercube.erosion)) {
					return false;
				} else if (!this.depth.equals(noiseHypercube.depth)) {
					return false;
				} else if (this.weirdness.equals(noiseHypercube.weirdness)) {
					return true;
				} else {
					return Float.compare(noiseHypercube.offset, this.offset) != 0 ? false : false;
				}
			} else {
				return false;
			}
		}

		public int hashCode() {
			return Objects.hash(new Object[]{this.temperature, this.humidity, this.continentalness, this.erosion, this.depth, this.weirdness, this.offset});
		}

		private float getSquaredDistance(MultiNoiseUtil.NoiseHypercube other) {
			return MathHelper.square(this.temperature.getDistance(other.temperature))
				+ MathHelper.square(this.humidity.getDistance(other.humidity))
				+ MathHelper.square(this.continentalness.getDistance(other.continentalness))
				+ MathHelper.square(this.erosion.getDistance(other.erosion))
				+ MathHelper.square(this.depth.getDistance(other.depth))
				+ MathHelper.square(this.weirdness.getDistance(other.weirdness))
				+ MathHelper.square(this.offset - other.offset);
		}

		float getSquaredDistance(MultiNoiseUtil.NoiseValuePoint point) {
			return MathHelper.square(this.temperature.getDistance(point.temperatureNoise))
				+ MathHelper.square(this.humidity.getDistance(point.humidityNoise))
				+ MathHelper.square(this.continentalness.getDistance(point.continentalnessNoise))
				+ MathHelper.square(this.erosion.getDistance(point.erosionNoise))
				+ MathHelper.square(this.depth.getDistance(point.depth))
				+ MathHelper.square(this.weirdness.getDistance(point.weirdnessNoise))
				+ MathHelper.square(this.offset);
		}

		public MultiNoiseUtil.ParameterRange getTemperature() {
			return this.temperature;
		}

		public MultiNoiseUtil.ParameterRange getHumidity() {
			return this.humidity;
		}

		public MultiNoiseUtil.ParameterRange getContinentalness() {
			return this.continentalness;
		}

		public MultiNoiseUtil.ParameterRange getErosion() {
			return this.erosion;
		}

		public MultiNoiseUtil.ParameterRange getDepth() {
			return this.depth;
		}

		public MultiNoiseUtil.ParameterRange getWeirdness() {
			return this.weirdness;
		}

		public float getOffset() {
			return this.offset;
		}

		protected List<MultiNoiseUtil.ParameterRange> getParameters() {
			return this.parameters;
		}
	}

	public static final class NoiseValuePoint {
		final float temperatureNoise;
		final float humidityNoise;
		final float continentalnessNoise;
		final float erosionNoise;
		final float depth;
		final float weirdnessNoise;

		NoiseValuePoint(float temperatureNoise, float humidityNoise, float continentalnessNoise, float erosionNoise, float depth, float weirdnessNoise) {
			this.temperatureNoise = temperatureNoise;
			this.humidityNoise = humidityNoise;
			this.continentalnessNoise = continentalnessNoise;
			this.erosionNoise = erosionNoise;
			this.depth = depth;
			this.weirdnessNoise = weirdnessNoise;
		}

		public float getTemperatureNoise() {
			return this.temperatureNoise;
		}

		public float getHumidityNoise() {
			return this.humidityNoise;
		}

		public float getContinentalnessNoise() {
			return this.continentalnessNoise;
		}

		public float getErosionNoise() {
			return this.erosionNoise;
		}

		public float getDepth() {
			return this.depth;
		}

		public float getWeirdnessNoise() {
			return this.weirdnessNoise;
		}

		public float[] getNoiseValueList() {
			return new float[]{this.temperatureNoise, this.humidityNoise, this.continentalnessNoise, this.erosionNoise, this.depth, this.weirdnessNoise, 0.0F};
		}
	}

	public static final class ParameterRange {
		public static final Codec<MultiNoiseUtil.ParameterRange> CODEC = Codec.either(Codec.floatRange(-2.0F, 2.0F), Codec.list(Codec.floatRange(-2.0F, 2.0F)))
			.comapFlatMap(
				either -> either.map(
						float_ -> DataResult.success(new MultiNoiseUtil.ParameterRange(float_, float_)),
						list -> Util.toArray(list, 2).map(listx -> new MultiNoiseUtil.ParameterRange((Float)listx.get(0), (Float)listx.get(1)))
					),
				parameterRange -> parameterRange.getMin() == parameterRange.getMax()
						? Either.left(parameterRange.getMin())
						: Either.right(ImmutableList.of(parameterRange.getMin(), parameterRange.getMax()))
			);
		private final float min;
		private final float max;

		ParameterRange(float min, float max) {
			this.min = min;
			this.max = max;
		}

		public float getMin() {
			return this.min;
		}

		public float getMax() {
			return this.max;
		}

		public boolean equals(Object o) {
			if (this == o) {
				return true;
			} else if (o != null && this.getClass() == o.getClass()) {
				MultiNoiseUtil.ParameterRange parameterRange = (MultiNoiseUtil.ParameterRange)o;
				return Float.compare(parameterRange.min, this.min) == 0 && Float.compare(parameterRange.max, this.max) == 0;
			} else {
				return false;
			}
		}

		public int hashCode() {
			return Objects.hash(new Object[]{this.min, this.max});
		}

		public String toString() {
			return this.min == this.max ? String.format("%.2f", this.min) : String.format("[%.2f-%.2f]", this.min, this.max);
		}

		public float getDistance(float noise) {
			float f = noise - this.max;
			float g = this.min - noise;
			return f > 0.0F ? f : Math.max(g, 0.0F);
		}

		public float getDistance(MultiNoiseUtil.ParameterRange other) {
			float f = other.getMin() - this.max;
			float g = this.min - other.getMax();
			return f > 0.0F ? f : Math.max(g, 0.0F);
		}

		public MultiNoiseUtil.ParameterRange combine(MultiNoiseUtil.ParameterRange other) {
			return new MultiNoiseUtil.ParameterRange(Math.min(this.min, other.getMin()), Math.max(this.max, other.getMax()));
		}
	}

	static final class SearchTree<T> {
		private static final int MAX_NODES_FOR_SIMPLE_TREE = 10;
		private final MultiNoiseUtil.SearchTree.TreeNode<T> firstNode;

		private SearchTree(MultiNoiseUtil.SearchTree.TreeNode<T> firstNode) {
			this.firstNode = firstNode;
		}

		public static <T> MultiNoiseUtil.SearchTree<T> create(List<Pair<MultiNoiseUtil.NoiseHypercube, Supplier<T>>> entries) {
			if (entries.isEmpty()) {
				throw new IllegalArgumentException("Need at least one biome to build the search tree.");
			} else {
				int i = ((MultiNoiseUtil.NoiseHypercube)((Pair)entries.get(0)).getFirst()).getParameters().size();
				if (i != 7) {
					throw new IllegalStateException("Expecting parameter space to be 7, got " + i);
				} else {
					List<MultiNoiseUtil.SearchTree.TreeLeafNode<T>> list = new ArrayList(
						(Collection)entries.stream()
							.map(entry -> new MultiNoiseUtil.SearchTree.TreeLeafNode((MultiNoiseUtil.NoiseHypercube)entry.getFirst(), (Supplier<T>)entry.getSecond()))
							.collect(Collectors.toList())
					);
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
				subTree.sort(Comparator.comparingDouble(node -> {
					float fx = 0.0F;

					for (int jx = 0; jx < parameterNumber; jx++) {
						MultiNoiseUtil.ParameterRange parameterRange = node.parameters[jx];
						fx += Math.abs((parameterRange.getMin() + parameterRange.getMax()) / 2.0F);
					}

					return (double)fx;
				}));
				return new MultiNoiseUtil.SearchTree.TreeBranchNode<>(subTree);
			} else {
				float f = Float.POSITIVE_INFINITY;
				int i = -1;

				for (int j = 0; j < parameterNumber; j++) {
					sortTree(subTree, j, false);
					List<MultiNoiseUtil.SearchTree.TreeBranchNode<T>> list = getBatchedTree(subTree);
					float g = 0.0F;

					for (MultiNoiseUtil.SearchTree.TreeBranchNode<T> treeBranchNode : list) {
						g += getRangeLengthSum(treeBranchNode.parameters);
					}

					if (f > g) {
						f = g;
						i = j;
					}
				}

				sortTree(subTree, i, false);
				List<MultiNoiseUtil.SearchTree.TreeBranchNode<T>> list = getBatchedTree(subTree);
				sortTree(list, i, true);
				return new MultiNoiseUtil.SearchTree.TreeBranchNode<>(
					(List<? extends MultiNoiseUtil.SearchTree.TreeNode<T>>)list.stream()
						.map(node -> createNode(parameterNumber, Arrays.asList(node.subTree)))
						.collect(Collectors.toList())
				);
			}
		}

		private static <T> void sortTree(List<? extends MultiNoiseUtil.SearchTree.TreeNode<T>> subTree, int parameterIndex, boolean abs) {
			subTree.sort(Comparator.comparingDouble(node -> {
				MultiNoiseUtil.ParameterRange parameterRange = node.parameters[parameterIndex];
				float f = (parameterRange.getMin() + parameterRange.getMax()) / 2.0F;
				return abs ? (double)Math.abs(f) : (double)f;
			}));
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

		private static float getRangeLengthSum(MultiNoiseUtil.ParameterRange[] parameters) {
			float f = 0.0F;

			for (MultiNoiseUtil.ParameterRange parameterRange : parameters) {
				f += Math.abs(parameterRange.getMax() - parameterRange.getMin());
			}

			return f;
		}

		static List<MultiNoiseUtil.ParameterRange> createDefaultParameterList(int parameterNumber) {
			return (List<MultiNoiseUtil.ParameterRange>)IntStream.range(0, parameterNumber)
				.mapToObj(i -> new MultiNoiseUtil.ParameterRange(Float.POSITIVE_INFINITY, Float.NEGATIVE_INFINITY))
				.collect(Collectors.toList());
		}

		static <T> List<MultiNoiseUtil.ParameterRange> getEnclosingParameters(List<? extends MultiNoiseUtil.SearchTree.TreeNode<T>> subTree) {
			if (subTree.isEmpty()) {
				throw new IllegalArgumentException("SubTree needs at least one child");
			} else {
				int i = 7;
				List<MultiNoiseUtil.ParameterRange> list = createDefaultParameterList(7);

				for (MultiNoiseUtil.SearchTree.TreeNode<T> treeNode : subTree) {
					List<MultiNoiseUtil.ParameterRange> list2 = list;
					list = (List<MultiNoiseUtil.ParameterRange>)IntStream.range(0, 7)
						.mapToObj(ix -> ((MultiNoiseUtil.ParameterRange)list2.get(ix)).combine(treeNode.parameters[ix]))
						.collect(Collectors.toList());
				}

				return list;
			}
		}

		public T get(MultiNoiseUtil.NoiseValuePoint point, MultiNoiseUtil.NodeDistanceFunction<T> distanceFunction) {
			float[] fs = point.getNoiseValueList();
			if (fs.length != 7) {
				throw new IllegalArgumentException(String.format("Target size (%s) does not match expected size (%s)", fs.length, this.firstNode.parameters.length));
			} else {
				MultiNoiseUtil.SearchTree.TreeLeafNode<T> treeLeafNode = this.firstNode.getResultingNode(fs, distanceFunction);
				return (T)treeLeafNode.value.get();
			}
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
			protected MultiNoiseUtil.SearchTree.TreeLeafNode<T> getResultingNode(float[] otherParameters, MultiNoiseUtil.NodeDistanceFunction<T> distanceFunction) {
				float f = Float.POSITIVE_INFINITY;
				MultiNoiseUtil.SearchTree.TreeLeafNode<T> treeLeafNode = null;

				for (MultiNoiseUtil.SearchTree.TreeNode<T> treeNode : this.subTree) {
					float g = distanceFunction.getDistance(treeNode, otherParameters);
					if (f > g) {
						MultiNoiseUtil.SearchTree.TreeLeafNode<T> treeLeafNode2 = treeNode.getResultingNode(otherParameters, distanceFunction);
						float h = treeNode == treeLeafNode2 ? g : distanceFunction.getDistance(treeLeafNode2, otherParameters);
						if (f > h) {
							f = h;
							treeLeafNode = treeLeafNode2;
						}
					}
				}

				return treeLeafNode;
			}
		}

		static final class TreeLeafNode<T> extends MultiNoiseUtil.SearchTree.TreeNode<T> {
			final Supplier<T> value;

			TreeLeafNode(MultiNoiseUtil.NoiseHypercube parameters, Supplier<T> value) {
				super(parameters.getParameters());
				this.value = value;
			}

			@Override
			protected MultiNoiseUtil.SearchTree.TreeLeafNode<T> getResultingNode(float[] otherParameters, MultiNoiseUtil.NodeDistanceFunction<T> distanceFunction) {
				return this;
			}
		}

		abstract static class TreeNode<T> {
			protected final MultiNoiseUtil.ParameterRange[] parameters;

			protected TreeNode(List<MultiNoiseUtil.ParameterRange> subTree) {
				this.parameters = (MultiNoiseUtil.ParameterRange[])subTree.toArray(new MultiNoiseUtil.ParameterRange[0]);
			}

			protected abstract MultiNoiseUtil.SearchTree.TreeLeafNode<T> getResultingNode(
				float[] otherParameters, MultiNoiseUtil.NodeDistanceFunction<T> distanceFunction
			);

			protected float getSquaredDistance(float[] otherParameters) {
				float f = 0.0F;

				for (int i = 0; i < 7; i++) {
					f += MathHelper.square(this.parameters[i].getDistance(otherParameters[i]));
				}

				return f;
			}

			public String toString() {
				return Arrays.toString(this.parameters);
			}
		}
	}
}
