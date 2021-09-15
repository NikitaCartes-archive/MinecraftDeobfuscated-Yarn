/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.biome.source.util;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

public class MultiNoiseUtil {
    private static final boolean field_34477 = false;
    @VisibleForTesting
    protected static final int HYPERCUBE_DIMENSION = 7;

    public static NoiseValuePoint createNoiseValuePoint(float temperatureNoise, float humidityNoise, float continentalnessNoise, float erosionNoise, float depth, float weirdnessNoise) {
        return new NoiseValuePoint(temperatureNoise, humidityNoise, continentalnessNoise, erosionNoise, depth, weirdnessNoise);
    }

    public static NoiseHypercube createNoiseHypercube(float temperature, float humidity, float continentalness, float erosion, float depth, float weirdness, float offset) {
        return new NoiseHypercube(ParameterRange.method_38120(temperature), ParameterRange.method_38120(humidity), ParameterRange.method_38120(continentalness), ParameterRange.method_38120(erosion), ParameterRange.method_38120(depth), ParameterRange.method_38120(weirdness), offset);
    }

    public static NoiseHypercube createNoiseHypercube(ParameterRange temperature, ParameterRange humidity, ParameterRange continentalness, ParameterRange erosion, ParameterRange depth, ParameterRange weirdness, float offset) {
        return new NoiseHypercube(temperature, humidity, continentalness, erosion, depth, weirdness, offset);
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
            return new float[]{this.temperatureNoise, this.humidityNoise, this.continentalnessNoise, this.erosionNoise, this.depth, this.weirdnessNoise, 0.0f};
        }
    }

    public static final class NoiseHypercube {
        public static final Codec<NoiseHypercube> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)ParameterRange.CODEC.fieldOf("temperature")).forGetter(noiseHypercube -> noiseHypercube.temperature), ((MapCodec)ParameterRange.CODEC.fieldOf("humidity")).forGetter(noiseHypercube -> noiseHypercube.humidity), ((MapCodec)ParameterRange.CODEC.fieldOf("continentalness")).forGetter(noiseHypercube -> noiseHypercube.continentalness), ((MapCodec)ParameterRange.CODEC.fieldOf("erosion")).forGetter(noiseHypercube -> noiseHypercube.erosion), ((MapCodec)ParameterRange.CODEC.fieldOf("depth")).forGetter(noiseHypercube -> noiseHypercube.depth), ((MapCodec)ParameterRange.CODEC.fieldOf("weirdness")).forGetter(noiseHypercube -> noiseHypercube.weirdness), ((MapCodec)Codec.floatRange(0.0f, 1.0f).fieldOf("offset")).forGetter(noiseHypercube -> Float.valueOf(noiseHypercube.offset))).apply((Applicative<NoiseHypercube, ?>)instance, NoiseHypercube::new));
        private final ParameterRange temperature;
        private final ParameterRange humidity;
        private final ParameterRange continentalness;
        private final ParameterRange erosion;
        private final ParameterRange depth;
        private final ParameterRange weirdness;
        private final float offset;

        NoiseHypercube(ParameterRange temperature, ParameterRange humidity, ParameterRange continentalness, ParameterRange erosion, ParameterRange depth, ParameterRange weirdness, float offset) {
            this.temperature = temperature;
            this.humidity = humidity;
            this.continentalness = continentalness;
            this.erosion = erosion;
            this.depth = depth;
            this.weirdness = weirdness;
            this.offset = offset;
        }

        public String toString() {
            return "[temp: " + this.temperature + ", hum: " + this.humidity + ", cont: " + this.continentalness + ", eros: " + this.erosion + ", depth: " + this.depth + ", weird: " + this.weirdness + ", offset: " + this.offset + "]";
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            NoiseHypercube noiseHypercube = (NoiseHypercube)o;
            if (!this.temperature.equals(noiseHypercube.temperature)) {
                return false;
            }
            if (!this.humidity.equals(noiseHypercube.humidity)) {
                return false;
            }
            if (!this.continentalness.equals(noiseHypercube.continentalness)) {
                return false;
            }
            if (!this.erosion.equals(noiseHypercube.erosion)) {
                return false;
            }
            if (!this.depth.equals(noiseHypercube.depth)) {
                return false;
            }
            if (!this.weirdness.equals(noiseHypercube.weirdness)) {
                return false;
            }
            return Float.compare(noiseHypercube.offset, this.offset) == 0;
        }

        public int hashCode() {
            return Objects.hash(this.temperature, this.humidity, this.continentalness, this.erosion, this.depth, this.weirdness, Float.valueOf(this.offset));
        }

        float getSquaredDistance(NoiseValuePoint point) {
            return MathHelper.square(this.temperature.getDistance(point.temperatureNoise)) + MathHelper.square(this.humidity.getDistance(point.humidityNoise)) + MathHelper.square(this.continentalness.getDistance(point.continentalnessNoise)) + MathHelper.square(this.erosion.getDistance(point.erosionNoise)) + MathHelper.square(this.depth.getDistance(point.depth)) + MathHelper.square(this.weirdness.getDistance(point.weirdnessNoise)) + MathHelper.square(this.offset);
        }

        public ParameterRange getTemperature() {
            return this.temperature;
        }

        public ParameterRange getHumidity() {
            return this.humidity;
        }

        public ParameterRange getContinentalness() {
            return this.continentalness;
        }

        public ParameterRange getErosion() {
            return this.erosion;
        }

        public ParameterRange getDepth() {
            return this.depth;
        }

        public ParameterRange getWeirdness() {
            return this.weirdness;
        }

        public float getOffset() {
            return this.offset;
        }

        protected List<ParameterRange> getParameters() {
            return ImmutableList.of(this.temperature, this.humidity, this.continentalness, this.erosion, this.depth, this.weirdness, ParameterRange.method_38120(this.offset));
        }
    }

    public static final class ParameterRange {
        public static final Codec<ParameterRange> CODEC = Codecs.method_37931(Codec.floatRange(-2.0f, 2.0f), "min", "max", (float_, float2) -> {
            if (float_.compareTo((Float)float2) > 0) {
                return DataResult.error("Cannon construct interval, min > max (" + float_ + " > " + float2 + ")");
            }
            return DataResult.success(new ParameterRange(float_.floatValue(), float2.floatValue()));
        }, ParameterRange::getMin, ParameterRange::getMax);
        private final float min;
        private final float max;

        private ParameterRange(float min, float max) {
            this.min = min;
            this.max = max;
        }

        public static ParameterRange method_38120(float f) {
            return ParameterRange.method_38121(f, f);
        }

        public static ParameterRange method_38121(float f, float g) {
            if (f > g) {
                throw new IllegalArgumentException("min > max: " + f + " " + g);
            }
            return new ParameterRange(f, g);
        }

        public static ParameterRange method_38123(ParameterRange parameterRange, ParameterRange parameterRange2) {
            if (parameterRange.getMin() > parameterRange2.getMax()) {
                throw new IllegalArgumentException("min > max: " + parameterRange + " " + parameterRange2);
            }
            return new ParameterRange(parameterRange.getMin(), parameterRange2.getMax());
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
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            ParameterRange parameterRange = (ParameterRange)o;
            return Float.compare(parameterRange.min, this.min) == 0 && Float.compare(parameterRange.max, this.max) == 0;
        }

        public int hashCode() {
            return Objects.hash(Float.valueOf(this.min), Float.valueOf(this.max));
        }

        public String toString() {
            return this.min == this.max ? String.format("%.2f", Float.valueOf(this.min)) : String.format("[%.2f-%.2f]", Float.valueOf(this.min), Float.valueOf(this.max));
        }

        public float getDistance(float noise) {
            float f = noise - this.max;
            float g = this.min - noise;
            if (f > 0.0f) {
                return f;
            }
            return Math.max(g, 0.0f);
        }

        public float getDistance(ParameterRange other) {
            float f = other.getMin() - this.max;
            float g = this.min - other.getMax();
            if (f > 0.0f) {
                return f;
            }
            return Math.max(g, 0.0f);
        }

        public ParameterRange combine(@Nullable ParameterRange other) {
            return other == null ? this : new ParameterRange(Math.min(this.min, other.getMin()), Math.max(this.max, other.getMax()));
        }
    }

    public static interface MultiNoiseSampler {
        public NoiseValuePoint sample(int var1, int var2, int var3);
    }

    public static class Entries<T> {
        private final List<Pair<NoiseHypercube, Supplier<T>>> entries;
        private final SearchTree<T> tree;

        public Entries(List<Pair<NoiseHypercube, Supplier<T>>> entries) {
            this.entries = entries;
            this.tree = SearchTree.create(entries);
        }

        public List<Pair<NoiseHypercube, Supplier<T>>> getEntries() {
            return this.entries;
        }

        public T getValue(NoiseValuePoint point, Supplier<T> defaultValue) {
            return this.getValue(point);
        }

        @VisibleForTesting
        public T getValueSimple(NoiseValuePoint point, Supplier<T> defaultValue) {
            float f = Float.MAX_VALUE;
            Supplier<T> supplier = defaultValue;
            for (Pair<NoiseHypercube, Supplier<T>> pair : this.getEntries()) {
                float g = pair.getFirst().getSquaredDistance(point);
                if (!(g < f)) continue;
                f = g;
                supplier = pair.getSecond();
            }
            return supplier.get();
        }

        public T getValue(NoiseValuePoint point) {
            return this.getValue(point, SearchTree.TreeNode::getSquaredDistance);
        }

        protected T getValue(NoiseValuePoint point, NodeDistanceFunction<T> distanceFunction) {
            return this.tree.get(point, distanceFunction);
        }
    }

    static final class SearchTree<T> {
        private static final int MAX_NODES_FOR_SIMPLE_TREE = 10;
        private final TreeNode<T> firstNode;
        private final ThreadLocal<TreeLeafNode<T>> field_34488 = new ThreadLocal();

        private SearchTree(TreeNode<T> treeNode) {
            this.firstNode = treeNode;
        }

        public static <T> SearchTree<T> create(List<Pair<NoiseHypercube, Supplier<T>>> entries) {
            if (entries.isEmpty()) {
                throw new IllegalArgumentException("Need at least one biome to build the search tree.");
            }
            int i = entries.get(0).getFirst().getParameters().size();
            if (i != 7) {
                throw new IllegalStateException("Expecting parameter space to be 7, got " + i);
            }
            List list = entries.stream().map(entry -> new TreeLeafNode((NoiseHypercube)entry.getFirst(), (Supplier)entry.getSecond())).collect(Collectors.toCollection(ArrayList::new));
            return new SearchTree<T>(SearchTree.createNode(i, list));
        }

        private static <T> TreeNode<T> createNode(int parameterNumber, List<? extends TreeNode<T>> subTree) {
            if (subTree.isEmpty()) {
                throw new IllegalStateException("Need at least one child to build a node");
            }
            if (subTree.size() == 1) {
                return subTree.get(0);
            }
            if (subTree.size() <= 10) {
                subTree.sort(Comparator.comparingDouble(node -> {
                    float f = 0.0f;
                    for (int j = 0; j < parameterNumber; ++j) {
                        ParameterRange parameterRange = node.parameters[j];
                        f += Math.abs((parameterRange.getMin() + parameterRange.getMax()) / 2.0f);
                    }
                    return f;
                }));
                return new TreeBranchNode(subTree);
            }
            float f = Float.POSITIVE_INFINITY;
            int i = -1;
            List<TreeBranchNode<T>> list = null;
            for (int j = 0; j < parameterNumber; ++j) {
                SearchTree.sortTree(subTree, parameterNumber, j, false);
                List<TreeBranchNode<T>> list2 = SearchTree.getBatchedTree(subTree);
                float g = 0.0f;
                for (TreeBranchNode<T> treeBranchNode : list2) {
                    g += SearchTree.getRangeLengthSum(treeBranchNode.parameters);
                }
                if (!(f > g)) continue;
                f = g;
                i = j;
                list = list2;
            }
            SearchTree.sortTree(list, parameterNumber, i, true);
            return new TreeBranchNode(list.stream().map(node -> SearchTree.createNode(parameterNumber, Arrays.asList(node.subTree))).collect(Collectors.toList()));
        }

        private static <T> void sortTree(List<? extends TreeNode<T>> subTree, int i, int j, boolean bl) {
            Comparator<TreeNode<TreeNode<T>>> comparator = SearchTree.method_38149(j, bl);
            for (int k = 1; k < i; ++k) {
                comparator = comparator.thenComparing(SearchTree.method_38149((j + k) % i, bl));
            }
            subTree.sort(comparator);
        }

        private static <T> Comparator<TreeNode<T>> method_38149(int i, boolean bl) {
            return Comparator.comparingDouble(treeNode -> {
                ParameterRange parameterRange = treeNode.parameters[i];
                float f = (parameterRange.getMin() + parameterRange.getMax()) / 2.0f;
                return bl ? (double)Math.abs(f) : (double)f;
            });
        }

        private static <T> List<TreeBranchNode<T>> getBatchedTree(List<? extends TreeNode<T>> nodes) {
            ArrayList<TreeBranchNode<T>> list = Lists.newArrayList();
            ArrayList<TreeNode<T>> list2 = Lists.newArrayList();
            int i = (int)Math.pow(10.0, Math.floor(Math.log((double)nodes.size() - 0.01) / Math.log(10.0)));
            for (TreeNode<T> treeNode : nodes) {
                list2.add(treeNode);
                if (list2.size() < i) continue;
                list.add(new TreeBranchNode(list2));
                list2 = Lists.newArrayList();
            }
            if (!list2.isEmpty()) {
                list.add(new TreeBranchNode(list2));
            }
            return list;
        }

        private static float getRangeLengthSum(ParameterRange[] parameters) {
            float f = 0.0f;
            for (ParameterRange parameterRange : parameters) {
                f += Math.abs(parameterRange.getMax() - parameterRange.getMin());
            }
            return f;
        }

        static <T> List<ParameterRange> getEnclosingParameters(List<? extends TreeNode<T>> subTree) {
            if (subTree.isEmpty()) {
                throw new IllegalArgumentException("SubTree needs at least one child");
            }
            int i = 7;
            ArrayList<ParameterRange> list = Lists.newArrayList();
            for (int j = 0; j < 7; ++j) {
                list.add(null);
            }
            for (TreeNode<T> treeNode : subTree) {
                for (int k = 0; k < 7; ++k) {
                    list.set(k, treeNode.parameters[k].combine((ParameterRange)list.get(k)));
                }
            }
            return list;
        }

        public T get(NoiseValuePoint point, NodeDistanceFunction<T> distanceFunction) {
            float[] fs = point.getNoiseValueList();
            TreeLeafNode<T> treeLeafNode = this.firstNode.getResultingNode(fs, this.field_34488.get(), distanceFunction);
            this.field_34488.set(treeLeafNode);
            return treeLeafNode.value.get();
        }

        static abstract class TreeNode<T> {
            protected final ParameterRange[] parameters;

            protected TreeNode(List<ParameterRange> subTree) {
                this.parameters = subTree.toArray(new ParameterRange[0]);
            }

            protected abstract TreeLeafNode<T> getResultingNode(float[] var1, @Nullable TreeLeafNode<T> var2, NodeDistanceFunction<T> var3);

            protected float getSquaredDistance(float[] otherParameters) {
                float f = 0.0f;
                for (int i = 0; i < 7; ++i) {
                    f += MathHelper.square(this.parameters[i].getDistance(otherParameters[i]));
                }
                return f;
            }

            public String toString() {
                return Arrays.toString(this.parameters);
            }
        }

        static final class TreeBranchNode<T>
        extends TreeNode<T> {
            final TreeNode<T>[] subTree;

            protected TreeBranchNode(List<? extends TreeNode<T>> list) {
                this(SearchTree.getEnclosingParameters(list), list);
            }

            protected TreeBranchNode(List<ParameterRange> parameters, List<? extends TreeNode<T>> subTree) {
                super(parameters);
                this.subTree = subTree.toArray(new TreeNode[0]);
            }

            @Override
            protected TreeLeafNode<T> getResultingNode(float[] otherParameters, @Nullable TreeLeafNode<T> treeLeafNode, NodeDistanceFunction<T> nodeDistanceFunction) {
                float f = treeLeafNode == null ? Float.POSITIVE_INFINITY : nodeDistanceFunction.getDistance(treeLeafNode, otherParameters);
                TreeLeafNode<T> treeLeafNode2 = treeLeafNode;
                for (TreeNode treeNode : this.subTree) {
                    float h;
                    float g = nodeDistanceFunction.getDistance(treeNode, otherParameters);
                    if (!(f > g)) continue;
                    TreeLeafNode<T> treeLeafNode3 = treeNode.getResultingNode(otherParameters, null, nodeDistanceFunction);
                    float f2 = h = treeNode == treeLeafNode3 ? g : nodeDistanceFunction.getDistance(treeLeafNode3, otherParameters);
                    if (!(f > h)) continue;
                    f = h;
                    treeLeafNode2 = treeLeafNode3;
                }
                return treeLeafNode2;
            }
        }

        static final class TreeLeafNode<T>
        extends TreeNode<T> {
            final Supplier<T> value;

            TreeLeafNode(NoiseHypercube parameters, Supplier<T> value) {
                super(parameters.getParameters());
                this.value = value;
            }

            @Override
            protected TreeLeafNode<T> getResultingNode(float[] otherParameters, @Nullable TreeLeafNode<T> treeLeafNode, NodeDistanceFunction<T> nodeDistanceFunction) {
                return this;
            }
        }
    }

    static interface NodeDistanceFunction<T> {
        public float getDistance(SearchTree.TreeNode<T> var1, float[] var2);
    }
}

