package net.minecraft.world.gen.chunk;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.ImmutableList.Builder;
import it.unimi.dsi.fastutil.longs.Long2IntMap;
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.class_6910;
import net.minecraft.class_6916;
import net.minecraft.class_6953;
import net.minecraft.class_6954;
import net.minecraft.class_6955;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.source.BiomeCoords;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChainedBlockSource;

public class ChunkNoiseSampler implements class_6910.class_6911, class_6910.class_6912 {
	private final GenerationShapeConfig generationShapeConfig;
	final int horizontalSize;
	final int height;
	final int minimumY;
	private final int x;
	private final int z;
	final int biomeX;
	final int biomeZ;
	final List<ChunkNoiseSampler.NoiseInterpolator> interpolators;
	final List<ChunkNoiseSampler.class_6949> field_36581;
	private final Map<class_6910, class_6910> field_36582 = new HashMap();
	private final Long2IntMap field_36273 = new Long2IntOpenHashMap();
	private final AquiferSampler aquiferSampler;
	private final class_6910 field_36583;
	private final ChunkNoiseSampler.BlockStateSampler field_36584;
	private final Blender blender;
	private final ChunkNoiseSampler.class_6951 field_36585;
	private final ChunkNoiseSampler.class_6951 field_36586;
	private long field_36587 = ChunkPos.MARKER;
	private Blender.class_6956 field_36588 = new Blender.class_6956(1.0, 0.0);
	final int field_36589;
	final int field_36590;
	final int field_36591;
	boolean field_36592;
	boolean field_36593;
	private int field_36594;
	int field_36572;
	private int field_36573;
	int field_36574;
	int field_36575;
	int field_36576;
	long field_36577;
	long field_36578;
	int field_36579;
	private final class_6910.class_6911 field_36580 = new class_6910.class_6911() {
		@Override
		public class_6910.class_6912 method_40477(int i) {
			ChunkNoiseSampler.this.field_36572 = (i + ChunkNoiseSampler.this.minimumY) * ChunkNoiseSampler.this.field_36591;
			ChunkNoiseSampler.this.field_36577++;
			ChunkNoiseSampler.this.field_36575 = 0;
			ChunkNoiseSampler.this.field_36579 = i;
			return ChunkNoiseSampler.this;
		}

		@Override
		public void method_40478(double[] ds, class_6910 arg) {
			for (int i = 0; i < ChunkNoiseSampler.this.height + 1; i++) {
				ChunkNoiseSampler.this.field_36572 = (i + ChunkNoiseSampler.this.minimumY) * ChunkNoiseSampler.this.field_36591;
				ChunkNoiseSampler.this.field_36577++;
				ChunkNoiseSampler.this.field_36575 = 0;
				ChunkNoiseSampler.this.field_36579 = i;
				ds[i] = arg.method_40464(ChunkNoiseSampler.this);
			}
		}
	};

	public static ChunkNoiseSampler create(
		Chunk chunk,
		class_6953 arg,
		Supplier<class_6910> supplier,
		ChunkGeneratorSettings chunkGeneratorSettings,
		AquiferSampler.FluidLevelSampler fluidLevelSampler,
		Blender blender
	) {
		ChunkPos chunkPos = chunk.getPos();
		GenerationShapeConfig generationShapeConfig = chunkGeneratorSettings.getGenerationShapeConfig();
		int i = Math.max(generationShapeConfig.minimumY(), chunk.getBottomY());
		int j = Math.min(generationShapeConfig.minimumY() + generationShapeConfig.height(), chunk.getTopY());
		int k = MathHelper.floorDiv(i, generationShapeConfig.verticalBlockSize());
		int l = MathHelper.floorDiv(j - i, generationShapeConfig.verticalBlockSize());
		return new ChunkNoiseSampler(
			16 / generationShapeConfig.horizontalBlockSize(),
			l,
			k,
			arg,
			chunkPos.getStartX(),
			chunkPos.getStartZ(),
			(class_6910)supplier.get(),
			chunkGeneratorSettings,
			fluidLevelSampler,
			blender
		);
	}

	public static ChunkNoiseSampler create(
		int minimumY,
		int i,
		int horizontalSize,
		int verticalNoiseResolution,
		class_6953 arg,
		ChunkGeneratorSettings chunkGeneratorSettings,
		AquiferSampler.FluidLevelSampler fluidLevelSampler
	) {
		return new ChunkNoiseSampler(
			1, verticalNoiseResolution, horizontalSize, arg, minimumY, i, class_6916.method_40479(), chunkGeneratorSettings, fluidLevelSampler, Blender.getNoBlending()
		);
	}

	private ChunkNoiseSampler(
		int horizontalNoiseResolution,
		int verticalNoiseResolution,
		int horizontalSize,
		class_6953 arg,
		int minimumY,
		int minimumZ,
		class_6910 arg2,
		ChunkGeneratorSettings chunkGeneratorSettings,
		AquiferSampler.FluidLevelSampler fluidLevelSampler,
		Blender blender
	) {
		this.generationShapeConfig = chunkGeneratorSettings.getGenerationShapeConfig();
		this.horizontalSize = horizontalNoiseResolution;
		this.height = verticalNoiseResolution;
		this.minimumY = horizontalSize;
		this.field_36590 = this.generationShapeConfig.horizontalBlockSize();
		this.field_36591 = this.generationShapeConfig.verticalBlockSize();
		this.x = Math.floorDiv(minimumY, this.field_36590);
		this.z = Math.floorDiv(minimumZ, this.field_36590);
		this.interpolators = Lists.<ChunkNoiseSampler.NoiseInterpolator>newArrayList();
		this.field_36581 = Lists.<ChunkNoiseSampler.class_6949>newArrayList();
		this.biomeX = BiomeCoords.fromBlock(minimumY);
		this.biomeZ = BiomeCoords.fromBlock(minimumZ);
		this.field_36589 = BiomeCoords.fromBlock(horizontalNoiseResolution * this.field_36590);
		this.blender = blender;
		this.field_36585 = new ChunkNoiseSampler.class_6951(new ChunkNoiseSampler.class_6946(), false);
		this.field_36586 = new ChunkNoiseSampler.class_6951(new ChunkNoiseSampler.class_6947(), false);

		for (int i = 0; i <= this.field_36589; i++) {
			int j = this.biomeX + i;
			int k = BiomeCoords.toBlock(j);

			for (int l = 0; l <= this.field_36589; l++) {
				int m = this.biomeZ + l;
				int n = BiomeCoords.toBlock(m);
				Blender.class_6956 lv = blender.method_39340(k, n);
				this.field_36585.field_36613[i][l] = lv.alpha();
				this.field_36586.field_36613[i][l] = lv.blendingOffset();
			}
		}

		if (!chunkGeneratorSettings.hasAquifers()) {
			this.aquiferSampler = AquiferSampler.seaLevel(fluidLevelSampler);
		} else {
			int i = ChunkSectionPos.getSectionCoord(minimumY);
			int j = ChunkSectionPos.getSectionCoord(minimumZ);
			this.aquiferSampler = AquiferSampler.aquifer(
				this,
				new ChunkPos(i, j),
				arg.barrierNoise(),
				arg.fluidLevelFloodednessNoise(),
				arg.fluidLevelSpreadNoise(),
				arg.lavaNoise(),
				arg.aquiferPositionalRandomFactory(),
				horizontalSize * this.field_36591,
				verticalNoiseResolution * this.field_36591,
				fluidLevelSampler
			);
		}

		Builder<ChunkNoiseSampler.BlockStateSampler> builder = ImmutableList.builder();
		class_6910 lv2 = class_6916.method_40510(class_6916.method_40486(arg.fullNoise(), arg2)).method_40469(this::method_40529);
		builder.add(arg2x -> this.aquiferSampler.apply(arg2x, lv2.method_40464(arg2x)));
		if (chunkGeneratorSettings.hasOreVeins()) {
			builder.add(
				class_6955.method_40548(
					arg.veinToggle().method_40469(this::method_40529),
					arg.veinRidged().method_40469(this::method_40529),
					arg.veinGap().method_40469(this::method_40529),
					arg.oreVeinsPositionalRandomFactory()
				)
			);
		}

		this.field_36584 = new ChainedBlockSource(builder.build());
		this.field_36583 = arg.initialDensityNoJaggedness().method_40469(this::method_40529);
	}

	protected MultiNoiseUtil.MultiNoiseSampler method_40531(class_6953 arg) {
		return new MultiNoiseUtil.MultiNoiseSampler(
			arg.temperature().method_40469(this::method_40529),
			arg.humidity().method_40469(this::method_40529),
			arg.continentalness().method_40469(this::method_40529),
			arg.erosion().method_40469(this::method_40529),
			arg.depth().method_40469(this::method_40529),
			arg.weirdness().method_40469(this::method_40529),
			arg.spawnTarget()
		);
	}

	@Nullable
	protected BlockState method_40536() {
		return this.field_36584.sample(this);
	}

	@Override
	public int blockX() {
		return this.field_36594 + this.field_36574;
	}

	@Override
	public int blockY() {
		return this.field_36572 + this.field_36575;
	}

	@Override
	public int blockZ() {
		return this.field_36573 + this.field_36576;
	}

	public int method_39900(int i, int j) {
		return this.field_36273.computeIfAbsent(ChunkPos.toLong(BiomeCoords.fromBlock(i), BiomeCoords.fromBlock(j)), this::method_39899);
	}

	private int method_39899(long l) {
		int i = ChunkPos.getPackedX(l);
		int j = ChunkPos.getPackedZ(l);
		return (int)class_6954.method_40543(this.generationShapeConfig, this.field_36583, BiomeCoords.toBlock(i), BiomeCoords.toBlock(j));
	}

	@Override
	public Blender getBlender() {
		return this.blender;
	}

	private void method_40532(boolean bl, int i) {
		this.field_36594 = i * this.field_36590;
		this.field_36574 = 0;

		for (int j = 0; j < this.horizontalSize + 1; j++) {
			int k = this.z + j;
			this.field_36573 = k * this.field_36590;
			this.field_36576 = 0;
			this.field_36578++;

			for (ChunkNoiseSampler.NoiseInterpolator noiseInterpolator : this.interpolators) {
				double[] ds = (bl ? noiseInterpolator.startNoiseBuffer : noiseInterpolator.endNoiseBuffer)[j];
				noiseInterpolator.method_40470(ds, this.field_36580);
			}
		}

		this.field_36578++;
	}

	public void sampleStartNoise() {
		if (this.field_36592) {
			throw new IllegalStateException("Staring interpolation twice");
		} else {
			this.field_36592 = true;
			this.field_36577 = 0L;
			this.method_40532(true, this.x);
		}
	}

	public void sampleEndNoise(int x) {
		this.method_40532(false, this.x + x + 1);
		this.field_36594 = (this.x + x) * this.field_36590;
	}

	public ChunkNoiseSampler method_40477(int i) {
		int j = Math.floorMod(i, this.field_36590);
		int k = Math.floorDiv(i, this.field_36590);
		int l = Math.floorMod(k, this.field_36590);
		int m = this.field_36591 - 1 - Math.floorDiv(k, this.field_36590);
		this.field_36574 = l;
		this.field_36575 = m;
		this.field_36576 = j;
		this.field_36579 = i;
		return this;
	}

	@Override
	public void method_40478(double[] ds, class_6910 arg) {
		this.field_36579 = 0;

		for (int i = this.field_36591 - 1; i >= 0; i--) {
			this.field_36575 = i;

			for (int j = 0; j < this.field_36590; j++) {
				this.field_36574 = j;

				for (int k = 0; k < this.field_36590; k++) {
					this.field_36576 = k;
					ds[this.field_36579++] = arg.method_40464(this);
				}
			}
		}
	}

	public void sampleNoiseCorners(int noiseY, int noiseZ) {
		this.interpolators.forEach(interpolator -> interpolator.sampleNoiseCorners(noiseY, noiseZ));
		this.field_36593 = true;
		this.field_36572 = (noiseY + this.minimumY) * this.field_36591;
		this.field_36573 = (this.z + noiseZ) * this.field_36590;
		this.field_36578++;

		for (ChunkNoiseSampler.class_6949 lv : this.field_36581) {
			lv.field_36603.method_40470(lv.field_36604, this);
		}

		this.field_36578++;
		this.field_36593 = false;
	}

	public void sampleNoiseY(int i, double d) {
		this.field_36575 = i - this.field_36572;
		this.interpolators.forEach(interpolator -> interpolator.sampleNoiseY(d));
	}

	public void sampleNoiseX(int i, double d) {
		this.field_36574 = i - this.field_36594;
		this.interpolators.forEach(interpolator -> interpolator.sampleNoiseX(d));
	}

	public void sampleNoise(int i, double d) {
		this.field_36576 = i - this.field_36573;
		this.field_36577++;
		this.interpolators.forEach(interpolator -> interpolator.sampleNoise(d));
	}

	public void method_40537() {
		if (!this.field_36592) {
			throw new IllegalStateException("Staring interpolation twice");
		} else {
			this.field_36592 = false;
		}
	}

	public void swapBuffers() {
		this.interpolators.forEach(ChunkNoiseSampler.NoiseInterpolator::swapBuffers);
	}

	public AquiferSampler getAquiferSampler() {
		return this.aquiferSampler;
	}

	Blender.class_6956 method_40535(int i, int j) {
		long l = ChunkPos.toLong(i, j);
		if (this.field_36587 == l) {
			return this.field_36588;
		} else {
			this.field_36587 = l;
			Blender.class_6956 lv = this.blender.method_39340(i, j);
			this.field_36588 = lv;
			return lv;
		}
	}

	protected class_6910 method_40529(class_6910 arg) {
		return (class_6910)this.field_36582.computeIfAbsent(arg, this::method_40533);
	}

	private class_6910 method_40533(class_6910 arg) {
		if (arg instanceof class_6916.class_6927 lv) {
			return (class_6910)(switch (lv.type()) {
				case INTERPOLATED -> new ChunkNoiseSampler.NoiseInterpolator(lv.function());
				case FLAT_CACHE -> new ChunkNoiseSampler.class_6951(lv.function(), true);
				case CACHE2D -> new ChunkNoiseSampler.class_6948(lv.function());
				case CACHE_ONCE -> new ChunkNoiseSampler.class_6950(lv.function());
				case CACHE_ALL_IN_CELL -> new ChunkNoiseSampler.class_6949(lv.function());
			});
		} else {
			if (this.blender != Blender.getNoBlending()) {
				if (arg == class_6916.class_6919.INSTANCE) {
					return this.field_36585;
				}

				if (arg == class_6916.class_6921.INSTANCE) {
					return this.field_36586;
				}
			}

			return arg;
		}
	}

	@FunctionalInterface
	public interface BlockStateSampler {
		@Nullable
		BlockState sample(class_6910.class_6912 arg);
	}

	public class NoiseInterpolator implements ChunkNoiseSampler.class_6952 {
		double[][] startNoiseBuffer;
		double[][] endNoiseBuffer;
		private final class_6910 columnSampler;
		private double x0y0z0;
		private double x0y0z1;
		private double x1y0z0;
		private double x1y0z1;
		private double x0y1z0;
		private double x0y1z1;
		private double x1y1z0;
		private double x1y1z1;
		private double x0z0;
		private double x1z0;
		private double x0z1;
		private double x1z1;
		private double z0;
		private double z1;
		private double result;

		NoiseInterpolator(class_6910 columnSampler) {
			this.columnSampler = columnSampler;
			this.startNoiseBuffer = this.createBuffer(ChunkNoiseSampler.this.height, ChunkNoiseSampler.this.horizontalSize);
			this.endNoiseBuffer = this.createBuffer(ChunkNoiseSampler.this.height, ChunkNoiseSampler.this.horizontalSize);
			ChunkNoiseSampler.this.interpolators.add(this);
		}

		private double[][] createBuffer(int sizeZ, int sizeX) {
			int i = sizeX + 1;
			int j = sizeZ + 1;
			double[][] ds = new double[i][j];

			for (int k = 0; k < i; k++) {
				ds[k] = new double[j];
			}

			return ds;
		}

		void sampleNoiseCorners(int noiseY, int noiseZ) {
			this.x0y0z0 = this.startNoiseBuffer[noiseZ][noiseY];
			this.x0y0z1 = this.startNoiseBuffer[noiseZ + 1][noiseY];
			this.x1y0z0 = this.endNoiseBuffer[noiseZ][noiseY];
			this.x1y0z1 = this.endNoiseBuffer[noiseZ + 1][noiseY];
			this.x0y1z0 = this.startNoiseBuffer[noiseZ][noiseY + 1];
			this.x0y1z1 = this.startNoiseBuffer[noiseZ + 1][noiseY + 1];
			this.x1y1z0 = this.endNoiseBuffer[noiseZ][noiseY + 1];
			this.x1y1z1 = this.endNoiseBuffer[noiseZ + 1][noiseY + 1];
		}

		void sampleNoiseY(double deltaY) {
			this.x0z0 = MathHelper.lerp(deltaY, this.x0y0z0, this.x0y1z0);
			this.x1z0 = MathHelper.lerp(deltaY, this.x1y0z0, this.x1y1z0);
			this.x0z1 = MathHelper.lerp(deltaY, this.x0y0z1, this.x0y1z1);
			this.x1z1 = MathHelper.lerp(deltaY, this.x1y0z1, this.x1y1z1);
		}

		void sampleNoiseX(double deltaX) {
			this.z0 = MathHelper.lerp(deltaX, this.x0z0, this.x1z0);
			this.z1 = MathHelper.lerp(deltaX, this.x0z1, this.x1z1);
		}

		void sampleNoise(double deltaZ) {
			this.result = MathHelper.lerp(deltaZ, this.z0, this.z1);
		}

		@Override
		public double method_40464(class_6910.class_6912 arg) {
			if (arg != ChunkNoiseSampler.this) {
				return this.columnSampler.method_40464(arg);
			} else if (!ChunkNoiseSampler.this.field_36592) {
				throw new IllegalStateException("Trying to sample interpolator outside the interpolation loop");
			} else {
				return ChunkNoiseSampler.this.field_36593
					? MathHelper.lerp3(
						(double)ChunkNoiseSampler.this.field_36574 / (double)ChunkNoiseSampler.this.field_36590,
						(double)ChunkNoiseSampler.this.field_36575 / (double)ChunkNoiseSampler.this.field_36591,
						(double)ChunkNoiseSampler.this.field_36576 / (double)ChunkNoiseSampler.this.field_36590,
						this.x0y0z0,
						this.x1y0z0,
						this.x0y1z0,
						this.x1y1z0,
						this.x0y0z1,
						this.x1y0z1,
						this.x0y1z1,
						this.x1y1z1
					)
					: this.result;
			}
		}

		@Override
		public void method_40470(double[] ds, class_6910.class_6911 arg) {
			if (ChunkNoiseSampler.this.field_36593) {
				arg.method_40478(ds, this);
			} else {
				this.method_40538().method_40470(ds, arg);
			}
		}

		@Override
		public class_6910 method_40538() {
			return this.columnSampler;
		}

		private void swapBuffers() {
			double[][] ds = this.startNoiseBuffer;
			this.startNoiseBuffer = this.endNoiseBuffer;
			this.endNoiseBuffer = ds;
		}
	}

	class class_6946 implements ChunkNoiseSampler.class_6952 {
		@Override
		public class_6910 method_40538() {
			return class_6916.class_6919.INSTANCE;
		}

		@Override
		public double method_40464(class_6910.class_6912 arg) {
			return ChunkNoiseSampler.this.method_40535(arg.blockX(), arg.blockZ()).alpha();
		}

		@Override
		public void method_40470(double[] ds, class_6910.class_6911 arg) {
			arg.method_40478(ds, this);
		}

		@Override
		public double minValue() {
			return 0.0;
		}

		@Override
		public double maxValue() {
			return 1.0;
		}
	}

	class class_6947 implements ChunkNoiseSampler.class_6952 {
		@Override
		public class_6910 method_40538() {
			return class_6916.class_6921.INSTANCE;
		}

		@Override
		public double method_40464(class_6910.class_6912 arg) {
			return ChunkNoiseSampler.this.method_40535(arg.blockX(), arg.blockZ()).blendingOffset();
		}

		@Override
		public void method_40470(double[] ds, class_6910.class_6911 arg) {
			arg.method_40478(ds, this);
		}

		@Override
		public double minValue() {
			return Double.NEGATIVE_INFINITY;
		}

		@Override
		public double maxValue() {
			return Double.POSITIVE_INFINITY;
		}
	}

	static class class_6948 implements ChunkNoiseSampler.class_6952 {
		private final class_6910 field_36599;
		private long field_36600 = ChunkPos.MARKER;
		private double field_36601;

		class_6948(class_6910 arg) {
			this.field_36599 = arg;
		}

		@Override
		public double method_40464(class_6910.class_6912 arg) {
			int i = arg.blockX();
			int j = arg.blockZ();
			long l = ChunkPos.toLong(i, j);
			if (this.field_36600 == l) {
				return this.field_36601;
			} else {
				this.field_36600 = l;
				double d = this.field_36599.method_40464(arg);
				this.field_36601 = d;
				return d;
			}
		}

		@Override
		public void method_40470(double[] ds, class_6910.class_6911 arg) {
			this.field_36599.method_40470(ds, arg);
		}

		@Override
		public class_6910 method_40538() {
			return this.field_36599;
		}
	}

	class class_6949 implements ChunkNoiseSampler.class_6952 {
		final class_6910 field_36603;
		final double[] field_36604;

		class_6949(class_6910 arg) {
			this.field_36603 = arg;
			this.field_36604 = new double[ChunkNoiseSampler.this.field_36590 * ChunkNoiseSampler.this.field_36590 * ChunkNoiseSampler.this.field_36591];
			ChunkNoiseSampler.this.field_36581.add(this);
		}

		@Override
		public double method_40464(class_6910.class_6912 arg) {
			if (arg != ChunkNoiseSampler.this) {
				return this.field_36603.method_40464(arg);
			} else if (!ChunkNoiseSampler.this.field_36592) {
				throw new IllegalStateException("Trying to sample interpolator outside the interpolation loop");
			} else {
				int i = ChunkNoiseSampler.this.field_36574;
				int j = ChunkNoiseSampler.this.field_36575;
				int k = ChunkNoiseSampler.this.field_36576;
				return i >= 0
						&& j >= 0
						&& k >= 0
						&& i < ChunkNoiseSampler.this.field_36590
						&& j < ChunkNoiseSampler.this.field_36591
						&& k < ChunkNoiseSampler.this.field_36590
					? this.field_36604[((ChunkNoiseSampler.this.field_36591 - 1 - j) * ChunkNoiseSampler.this.field_36590 + i) * ChunkNoiseSampler.this.field_36590 + k]
					: this.field_36603.method_40464(arg);
			}
		}

		@Override
		public void method_40470(double[] ds, class_6910.class_6911 arg) {
			arg.method_40478(ds, this);
		}

		@Override
		public class_6910 method_40538() {
			return this.field_36603;
		}
	}

	class class_6950 implements ChunkNoiseSampler.class_6952 {
		private final class_6910 field_36606;
		private long field_36607;
		private long field_36608;
		private double field_36609;
		@Nullable
		private double[] field_36610;

		class_6950(class_6910 arg) {
			this.field_36606 = arg;
		}

		@Override
		public double method_40464(class_6910.class_6912 arg) {
			if (arg != ChunkNoiseSampler.this) {
				return this.field_36606.method_40464(arg);
			} else if (this.field_36610 != null && this.field_36608 == ChunkNoiseSampler.this.field_36578) {
				return this.field_36610[ChunkNoiseSampler.this.field_36579];
			} else if (this.field_36607 == ChunkNoiseSampler.this.field_36577) {
				return this.field_36609;
			} else {
				this.field_36607 = ChunkNoiseSampler.this.field_36577;
				double d = this.field_36606.method_40464(arg);
				this.field_36609 = d;
				return d;
			}
		}

		@Override
		public void method_40470(double[] ds, class_6910.class_6911 arg) {
			if (this.field_36610 != null && this.field_36608 == ChunkNoiseSampler.this.field_36578) {
				System.arraycopy(this.field_36610, 0, ds, 0, ds.length);
			} else {
				this.method_40538().method_40470(ds, arg);
				if (this.field_36610 != null && this.field_36610.length == ds.length) {
					System.arraycopy(ds, 0, this.field_36610, 0, ds.length);
				} else {
					this.field_36610 = (double[])ds.clone();
				}

				this.field_36608 = ChunkNoiseSampler.this.field_36578;
			}
		}

		@Override
		public class_6910 method_40538() {
			return this.field_36606;
		}
	}

	class class_6951 implements ChunkNoiseSampler.class_6952 {
		private final class_6910 field_36612;
		final double[][] field_36613;

		class_6951(class_6910 arg, boolean bl) {
			this.field_36612 = arg;
			this.field_36613 = new double[ChunkNoiseSampler.this.field_36589 + 1][ChunkNoiseSampler.this.field_36589 + 1];
			if (bl) {
				for (int i = 0; i <= ChunkNoiseSampler.this.field_36589; i++) {
					int j = ChunkNoiseSampler.this.biomeX + i;
					int k = BiomeCoords.toBlock(j);

					for (int l = 0; l <= ChunkNoiseSampler.this.field_36589; l++) {
						int m = ChunkNoiseSampler.this.biomeZ + l;
						int n = BiomeCoords.toBlock(m);
						this.field_36613[i][l] = arg.method_40464(new class_6910.class_6914(k, 0, n));
					}
				}
			}
		}

		@Override
		public double method_40464(class_6910.class_6912 arg) {
			int i = BiomeCoords.fromBlock(arg.blockX());
			int j = BiomeCoords.fromBlock(arg.blockZ());
			int k = i - ChunkNoiseSampler.this.biomeX;
			int l = j - ChunkNoiseSampler.this.biomeZ;
			int m = this.field_36613.length;
			return k >= 0 && l >= 0 && k < m && l < m ? this.field_36613[k][l] : this.field_36612.method_40464(arg);
		}

		@Override
		public void method_40470(double[] ds, class_6910.class_6911 arg) {
			arg.method_40478(ds, this);
		}

		@Override
		public class_6910 method_40538() {
			return this.field_36612;
		}
	}

	interface class_6952 extends class_6910 {
		class_6910 method_40538();

		@Override
		default class_6910 method_40469(class_6910.class_6915 arg) {
			return this.method_40538().method_40469(arg);
		}

		@Override
		default double minValue() {
			return this.method_40538().minValue();
		}

		@Override
		default double maxValue() {
			return this.method_40538().maxValue();
		}
	}
}
