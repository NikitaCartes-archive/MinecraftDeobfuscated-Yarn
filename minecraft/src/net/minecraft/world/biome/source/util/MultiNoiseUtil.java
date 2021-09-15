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
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.MathHelper;

public class MultiNoiseUtil {
	private static final boolean field_34477 = false;
	@VisibleForTesting
	protected static final int HYPERCUBE_DIMENSION = 7;

	public static MultiNoiseUtil.NoiseValuePoint createNoiseValuePoint(
		float temperatureNoise, float humidityNoise, float continentalnessNoise, float erosionNoise, float depth, float weirdnessNoise
	) {
		return new MultiNoiseUtil.NoiseValuePoint(temperatureNoise, humidityNoise, continentalnessNoise, erosionNoise, depth, weirdnessNoise);
	}

	public static MultiNoiseUtil.NoiseHypercube createNoiseHypercube(
		float temperature, float humidity, float continentalness, float erosion, float depth, float weirdness, float offset
	) {
		return new MultiNoiseUtil.NoiseHypercube(
			MultiNoiseUtil.ParameterRange.method_38120(temperature),
			MultiNoiseUtil.ParameterRange.method_38120(humidity),
			MultiNoiseUtil.ParameterRange.method_38120(continentalness),
			MultiNoiseUtil.ParameterRange.method_38120(erosion),
			MultiNoiseUtil.ParameterRange.method_38120(depth),
			MultiNoiseUtil.ParameterRange.method_38120(weirdness),
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

		@VisibleForTesting
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
			return this.getValue(point, MultiNoiseUtil.SearchTree.TreeNode::getSquaredDistance);
		}

		protected T getValue(MultiNoiseUtil.NoiseValuePoint point, MultiNoiseUtil.NodeDistanceFunction<T> distanceFunction) {
			return this.tree.get(point, distanceFunction);
		}
	}

	public interface MultiNoiseSampler {
		MultiNoiseUtil.NoiseValuePoint sample(int i, int j, int k);
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
		}

		public String toString() {
			return "[temp: "
				+ this.temperature
				+ ", hum: "
				+ this.humidity
				+ ", cont: "
				+ this.continentalness
				+ ", eros: "
				+ this.erosion
				+ ", depth: "
				+ this.depth
				+ ", weird: "
				+ this.weirdness
				+ ", offset: "
				+ this.offset
				+ "]";
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
				} else {
					return !this.weirdness.equals(noiseHypercube.weirdness) ? false : Float.compare(noiseHypercube.offset, this.offset) == 0;
				}
			} else {
				return false;
			}
		}

		public int hashCode() {
			return Objects.hash(new Object[]{this.temperature, this.humidity, this.continentalness, this.erosion, this.depth, this.weirdness, this.offset});
		}

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
			return ImmutableList.of(
				this.temperature, this.humidity, this.continentalness, this.erosion, this.depth, this.weirdness, MultiNoiseUtil.ParameterRange.method_38120(this.offset)
			);
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

		@VisibleForTesting
		protected float[] getNoiseValueList() {
			return new float[]{this.temperatureNoise, this.humidityNoise, this.continentalnessNoise, this.erosionNoise, this.depth, this.weirdnessNoise, 0.0F};
		}
	}

	public static final class ParameterRange {
		public static final Codec<MultiNoiseUtil.ParameterRange> CODEC = Codecs.method_37931(
			Codec.floatRange(-2.0F, 2.0F),
			"min",
			"max",
			(float_, float2) -> float_.compareTo(float2) > 0
					? DataResult.error("Cannon construct interval, min > max (" + float_ + " > " + float2 + ")")
					: DataResult.success(new MultiNoiseUtil.ParameterRange(float_, float2)),
			MultiNoiseUtil.ParameterRange::getMin,
			MultiNoiseUtil.ParameterRange::getMax
		);
		private final float min;
		private final float max;

		private ParameterRange(float min, float max) {
			this.min = min;
			this.max = max;
		}

		public static MultiNoiseUtil.ParameterRange method_38120(float f) {
			return method_38121(f, f);
		}

		public static MultiNoiseUtil.ParameterRange method_38121(float f, float g) {
			if (f > g) {
				throw new IllegalArgumentException("min > max: " + f + " " + g);
			} else {
				return new MultiNoiseUtil.ParameterRange(f, g);
			}
		}

		public static MultiNoiseUtil.ParameterRange method_38123(MultiNoiseUtil.ParameterRange parameterRange, MultiNoiseUtil.ParameterRange parameterRange2) {
			if (parameterRange.getMin() > parameterRange2.getMax()) {
				throw new IllegalArgumentException("min > max: " + parameterRange + " " + parameterRange2);
			} else {
				return new MultiNoiseUtil.ParameterRange(parameterRange.getMin(), parameterRange2.getMax());
			}
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

		public MultiNoiseUtil.ParameterRange combine(@Nullable MultiNoiseUtil.ParameterRange other) {
			return other == null ? this : new MultiNoiseUtil.ParameterRange(Math.min(this.min, other.getMin()), Math.max(this.max, other.getMax()));
		}
	}

	static final class SearchTree<T> {
		private static final int MAX_NODES_FOR_SIMPLE_TREE = 10;
		private final MultiNoiseUtil.SearchTree.TreeNode<T> firstNode;
		private final ThreadLocal<MultiNoiseUtil.SearchTree.TreeLeafNode<T>> field_34488 = new ThreadLocal();

		private SearchTree(MultiNoiseUtil.SearchTree.TreeNode<T> treeNode) {
			this.firstNode = treeNode;
		}

		public static <T> MultiNoiseUtil.SearchTree<T> create(List<Pair<MultiNoiseUtil.NoiseHypercube, Supplier<T>>> entries) {
			if (entries.isEmpty()) {
				throw new IllegalArgumentException("Need at least one biome to build the search tree.");
			} else {
				int i = ((MultiNoiseUtil.NoiseHypercube)((Pair)entries.get(0)).getFirst()).getParameters().size();
				if (i != 7) {
					throw new IllegalStateException("Expecting parameter space to be 7, got " + i);
				} else {
					List<MultiNoiseUtil.SearchTree.TreeLeafNode<T>> list = (List<MultiNoiseUtil.SearchTree.TreeLeafNode<T>>)entries.stream()
						.map(entry -> new MultiNoiseUtil.SearchTree.TreeLeafNode((MultiNoiseUtil.NoiseHypercube)entry.getFirst(), (Supplier<T>)entry.getSecond()))
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
				List<MultiNoiseUtil.SearchTree.TreeBranchNode<T>> list = null;

				for (int j = 0; j < parameterNumber; j++) {
					sortTree(subTree, parameterNumber, j, false);
					List<MultiNoiseUtil.SearchTree.TreeBranchNode<T>> list2 = getBatchedTree(subTree);
					float g = 0.0F;

					for (MultiNoiseUtil.SearchTree.TreeBranchNode<T> treeBranchNode : list2) {
						g += getRangeLengthSum(treeBranchNode.parameters);
					}

					if (f > g) {
						f = g;
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

		private static <T> void sortTree(List<? extends MultiNoiseUtil.SearchTree.TreeNode<T>> subTree, int i, int j, boolean bl) {
			Comparator<MultiNoiseUtil.SearchTree.TreeNode<T>> comparator = method_38149(j, bl);

			for (int k = 1; k < i; k++) {
				comparator = comparator.thenComparing(method_38149((j + k) % i, bl));
			}

			subTree.sort(comparator);
		}

		private static <T> Comparator<MultiNoiseUtil.SearchTree.TreeNode<T>> method_38149(int i, boolean bl) {
			return Comparator.comparingDouble(treeNode -> {
				MultiNoiseUtil.ParameterRange parameterRange = treeNode.parameters[i];
				float f = (parameterRange.getMin() + parameterRange.getMax()) / 2.0F;
				return bl ? (double)Math.abs(f) : (double)f;
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

		private static float getRangeLengthSum(MultiNoiseUtil.ParameterRange[] parameters) {
			float f = 0.0F;

			for (MultiNoiseUtil.ParameterRange parameterRange : parameters) {
				f += Math.abs(parameterRange.getMax() - parameterRange.getMin());
			}

			return f;
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
			float[] fs = point.getNoiseValueList();
			MultiNoiseUtil.SearchTree.TreeLeafNode<T> treeLeafNode = this.firstNode
				.getResultingNode(fs, (MultiNoiseUtil.SearchTree.TreeLeafNode<T>)this.field_34488.get(), distanceFunction);
			this.field_34488.set(treeLeafNode);
			return (T)treeLeafNode.value.get();
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
				float[] otherParameters, @Nullable MultiNoiseUtil.SearchTree.TreeLeafNode<T> treeLeafNode, MultiNoiseUtil.NodeDistanceFunction<T> nodeDistanceFunction
			) {
				float f = treeLeafNode == null ? Float.POSITIVE_INFINITY : nodeDistanceFunction.getDistance(treeLeafNode, otherParameters);
				MultiNoiseUtil.SearchTree.TreeLeafNode<T> treeLeafNode2 = treeLeafNode;

				for (MultiNoiseUtil.SearchTree.TreeNode<T> treeNode : this.subTree) {
					float g = nodeDistanceFunction.getDistance(treeNode, otherParameters);
					if (f > g) {
						MultiNoiseUtil.SearchTree.TreeLeafNode<T> treeLeafNode3 = treeNode.getResultingNode(otherParameters, null, nodeDistanceFunction);
						float h = treeNode == treeLeafNode3 ? g : nodeDistanceFunction.getDistance(treeLeafNode3, otherParameters);
						if (f > h) {
							f = h;
							treeLeafNode2 = treeLeafNode3;
						}
					}
				}

				return treeLeafNode2;
			}
		}

		static final class TreeLeafNode<T> extends MultiNoiseUtil.SearchTree.TreeNode<T> {
			final Supplier<T> value;

			TreeLeafNode(MultiNoiseUtil.NoiseHypercube parameters, Supplier<T> value) {
				super(parameters.getParameters());
				this.value = value;
			}

			@Override
			protected MultiNoiseUtil.SearchTree.TreeLeafNode<T> getResultingNode(
				float[] otherParameters, @Nullable MultiNoiseUtil.SearchTree.TreeLeafNode<T> treeLeafNode, MultiNoiseUtil.NodeDistanceFunction<T> nodeDistanceFunction
			) {
				return this;
			}
		}

		abstract static class TreeNode<T> {
			protected final MultiNoiseUtil.ParameterRange[] parameters;

			protected TreeNode(List<MultiNoiseUtil.ParameterRange> subTree) {
				this.parameters = (MultiNoiseUtil.ParameterRange[])subTree.toArray(new MultiNoiseUtil.ParameterRange[0]);
			}

			protected abstract MultiNoiseUtil.SearchTree.TreeLeafNode<T> getResultingNode(
				float[] otherParameters, @Nullable MultiNoiseUtil.SearchTree.TreeLeafNode<T> treeLeafNode, MultiNoiseUtil.NodeDistanceFunction<T> nodeDistanceFunction
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
