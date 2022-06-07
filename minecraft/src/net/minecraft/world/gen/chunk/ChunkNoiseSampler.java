package net.minecraft.world.gen.chunk;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.ImmutableList.Builder;
import it.unimi.dsi.fastutil.longs.Long2IntMap;
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.util.dynamic.CodecHolder;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.ColumnPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.source.BiomeCoords;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChainedBlockSource;
import net.minecraft.world.gen.OreVeinSampler;
import net.minecraft.world.gen.densityfunction.DensityFunction;
import net.minecraft.world.gen.densityfunction.DensityFunctionTypes;
import net.minecraft.world.gen.noise.NoiseConfig;
import net.minecraft.world.gen.noise.NoiseRouter;

public class ChunkNoiseSampler implements DensityFunction.class_6911, DensityFunction.NoisePos {
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
	private final Map<DensityFunction, DensityFunction> field_36582 = new HashMap();
	private final Long2IntMap field_36273 = new Long2IntOpenHashMap();
	private final AquiferSampler aquiferSampler;
	private final DensityFunction field_36583;
	private final ChunkNoiseSampler.BlockStateSampler blockStateSampler;
	private final Blender blender;
	private final ChunkNoiseSampler.class_6951 field_36585;
	private final ChunkNoiseSampler.class_6951 field_36586;
	private final DensityFunctionTypes.class_7050 field_37113;
	private long field_36587 = ChunkPos.MARKER;
	private Blender.class_6956 field_36588 = new Blender.class_6956(1.0, 0.0);
	final int field_36589;
	final int horizontalBlockSize;
	final int verticalBlockSize;
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
	private final DensityFunction.class_6911 field_36580 = new DensityFunction.class_6911() {
		@Override
		public DensityFunction.NoisePos method_40477(int i) {
			ChunkNoiseSampler.this.field_36572 = (i + ChunkNoiseSampler.this.minimumY) * ChunkNoiseSampler.this.verticalBlockSize;
			ChunkNoiseSampler.this.field_36577++;
			ChunkNoiseSampler.this.field_36575 = 0;
			ChunkNoiseSampler.this.field_36579 = i;
			return ChunkNoiseSampler.this;
		}

		@Override
		public void method_40478(double[] ds, DensityFunction densityFunction) {
			for (int i = 0; i < ChunkNoiseSampler.this.height + 1; i++) {
				ChunkNoiseSampler.this.field_36572 = (i + ChunkNoiseSampler.this.minimumY) * ChunkNoiseSampler.this.verticalBlockSize;
				ChunkNoiseSampler.this.field_36577++;
				ChunkNoiseSampler.this.field_36575 = 0;
				ChunkNoiseSampler.this.field_36579 = i;
				ds[i] = densityFunction.sample(ChunkNoiseSampler.this);
			}
		}
	};

	public static ChunkNoiseSampler create(
		Chunk chunk,
		NoiseConfig noiseConfig,
		DensityFunctionTypes.class_7050 arg,
		ChunkGeneratorSettings chunkGeneratorSettings,
		AquiferSampler.FluidLevelSampler fluidLevelSampler,
		Blender blender
	) {
		GenerationShapeConfig generationShapeConfig = chunkGeneratorSettings.generationShapeConfig().method_42368(chunk);
		ChunkPos chunkPos = chunk.getPos();
		int i = 16 / generationShapeConfig.horizontalBlockSize();
		return new ChunkNoiseSampler(
			i, noiseConfig, chunkPos.getStartX(), chunkPos.getStartZ(), generationShapeConfig, arg, chunkGeneratorSettings, fluidLevelSampler, blender
		);
	}

	public ChunkNoiseSampler(
		int horizontalSize,
		NoiseConfig noiseConfig,
		int i,
		int j,
		GenerationShapeConfig generationShapeConfig,
		DensityFunctionTypes.class_7050 arg,
		ChunkGeneratorSettings chunkGeneratorSettings,
		AquiferSampler.FluidLevelSampler fluidLevelSampler,
		Blender blender
	) {
		this.generationShapeConfig = generationShapeConfig;
		this.horizontalBlockSize = generationShapeConfig.horizontalBlockSize();
		this.verticalBlockSize = generationShapeConfig.verticalBlockSize();
		this.horizontalSize = horizontalSize;
		this.height = MathHelper.floorDiv(generationShapeConfig.height(), this.verticalBlockSize);
		this.minimumY = MathHelper.floorDiv(generationShapeConfig.minimumY(), this.verticalBlockSize);
		this.x = Math.floorDiv(i, this.horizontalBlockSize);
		this.z = Math.floorDiv(j, this.horizontalBlockSize);
		this.interpolators = Lists.<ChunkNoiseSampler.NoiseInterpolator>newArrayList();
		this.field_36581 = Lists.<ChunkNoiseSampler.class_6949>newArrayList();
		this.biomeX = BiomeCoords.fromBlock(i);
		this.biomeZ = BiomeCoords.fromBlock(j);
		this.field_36589 = BiomeCoords.fromBlock(horizontalSize * this.horizontalBlockSize);
		this.blender = blender;
		this.field_37113 = arg;
		this.field_36585 = new ChunkNoiseSampler.class_6951(new ChunkNoiseSampler.class_6946(), false);
		this.field_36586 = new ChunkNoiseSampler.class_6951(new ChunkNoiseSampler.class_6947(), false);

		for (int k = 0; k <= this.field_36589; k++) {
			int l = this.biomeX + k;
			int m = BiomeCoords.toBlock(l);

			for (int n = 0; n <= this.field_36589; n++) {
				int o = this.biomeZ + n;
				int p = BiomeCoords.toBlock(o);
				Blender.class_6956 lv = blender.method_39340(m, p);
				this.field_36585.field_36613[k][n] = lv.alpha();
				this.field_36586.field_36613[k][n] = lv.blendingOffset();
			}
		}

		NoiseRouter noiseRouter = noiseConfig.getNoiseRouter();
		NoiseRouter noiseRouter2 = noiseRouter.method_41544(this::method_40529);
		if (!chunkGeneratorSettings.hasAquifers()) {
			this.aquiferSampler = AquiferSampler.seaLevel(fluidLevelSampler);
		} else {
			int m = ChunkSectionPos.getSectionCoord(i);
			int n = ChunkSectionPos.getSectionCoord(j);
			this.aquiferSampler = AquiferSampler.aquifer(
				this,
				new ChunkPos(m, n),
				noiseRouter2,
				noiseConfig.getAquiferRandomDeriver(),
				generationShapeConfig.minimumY(),
				generationShapeConfig.height(),
				fluidLevelSampler
			);
		}

		Builder<ChunkNoiseSampler.BlockStateSampler> builder = ImmutableList.builder();
		DensityFunction densityFunction = DensityFunctionTypes.cacheAllInCell(
				DensityFunctionTypes.add(noiseRouter2.finalDensity(), DensityFunctionTypes.Beardifier.INSTANCE)
			)
			.apply(this::method_40529);
		builder.add(noisePos -> this.aquiferSampler.apply(noisePos, densityFunction.sample(noisePos)));
		if (chunkGeneratorSettings.oreVeins()) {
			builder.add(OreVeinSampler.create(noiseRouter2.veinToggle(), noiseRouter2.veinRidged(), noiseRouter2.veinGap(), noiseConfig.getOreRandomDeriver()));
		}

		this.blockStateSampler = new ChainedBlockSource(builder.build());
		this.field_36583 = noiseRouter2.initialDensityWithoutJaggedness();
	}

	protected MultiNoiseUtil.MultiNoiseSampler createMultiNoiseSampler(NoiseRouter noiseRouter, List<MultiNoiseUtil.NoiseHypercube> list) {
		return new MultiNoiseUtil.MultiNoiseSampler(
			noiseRouter.temperature().apply(this::method_40529),
			noiseRouter.vegetation().apply(this::method_40529),
			noiseRouter.continents().apply(this::method_40529),
			noiseRouter.erosion().apply(this::method_40529),
			noiseRouter.depth().apply(this::method_40529),
			noiseRouter.ridges().apply(this::method_40529),
			list
		);
	}

	@Nullable
	protected BlockState sampleBlockState() {
		return this.blockStateSampler.sample(this);
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
		int k = BiomeCoords.toBlock(BiomeCoords.fromBlock(i));
		int l = BiomeCoords.toBlock(BiomeCoords.fromBlock(j));
		return this.field_36273.computeIfAbsent(ColumnPos.pack(k, l), this::method_39899);
	}

	private int method_39899(long l) {
		int i = ColumnPos.getX(l);
		int j = ColumnPos.getZ(l);
		int k = this.generationShapeConfig.minimumY();

		for (int m = k + this.generationShapeConfig.height(); m >= k; m -= this.verticalBlockSize) {
			if (this.field_36583.sample(new DensityFunction.UnblendedNoisePos(i, m, j)) > 0.390625) {
				return m;
			}
		}

		return Integer.MAX_VALUE;
	}

	@Override
	public Blender getBlender() {
		return this.blender;
	}

	private void method_40532(boolean bl, int i) {
		this.field_36594 = i * this.horizontalBlockSize;
		this.field_36574 = 0;

		for (int j = 0; j < this.horizontalSize + 1; j++) {
			int k = this.z + j;
			this.field_36573 = k * this.horizontalBlockSize;
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
		this.field_36594 = (this.x + x) * this.horizontalBlockSize;
	}

	public ChunkNoiseSampler method_40477(int i) {
		int j = Math.floorMod(i, this.horizontalBlockSize);
		int k = Math.floorDiv(i, this.horizontalBlockSize);
		int l = Math.floorMod(k, this.horizontalBlockSize);
		int m = this.verticalBlockSize - 1 - Math.floorDiv(k, this.horizontalBlockSize);
		this.field_36574 = l;
		this.field_36575 = m;
		this.field_36576 = j;
		this.field_36579 = i;
		return this;
	}

	@Override
	public void method_40478(double[] ds, DensityFunction densityFunction) {
		this.field_36579 = 0;

		for (int i = this.verticalBlockSize - 1; i >= 0; i--) {
			this.field_36575 = i;

			for (int j = 0; j < this.horizontalBlockSize; j++) {
				this.field_36574 = j;

				for (int k = 0; k < this.horizontalBlockSize; k++) {
					this.field_36576 = k;
					ds[this.field_36579++] = densityFunction.sample(this);
				}
			}
		}
	}

	public void sampleNoiseCorners(int noiseY, int noiseZ) {
		this.interpolators.forEach(interpolator -> interpolator.sampleNoiseCorners(noiseY, noiseZ));
		this.field_36593 = true;
		this.field_36572 = (noiseY + this.minimumY) * this.verticalBlockSize;
		this.field_36573 = (this.z + noiseZ) * this.horizontalBlockSize;
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

	public void sampleNoiseZ(int i, double d) {
		this.field_36576 = i - this.field_36573;
		this.field_36577++;
		this.interpolators.forEach(interpolator -> interpolator.sampleNoiseZ(d));
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

	protected int getHorizontalBlockSize() {
		return this.horizontalBlockSize;
	}

	protected int getVerticalBlockSize() {
		return this.verticalBlockSize;
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

	protected DensityFunction method_40529(DensityFunction densityFunction) {
		return (DensityFunction)this.field_36582.computeIfAbsent(densityFunction, this::method_40533);
	}

	private DensityFunction method_40533(DensityFunction densityFunction) {
		if (densityFunction instanceof DensityFunctionTypes.class_6927 lv) {
			return (DensityFunction)(switch (lv.type()) {
				case INTERPOLATED -> new ChunkNoiseSampler.NoiseInterpolator(lv.wrapped());
				case FLAT_CACHE -> new ChunkNoiseSampler.class_6951(lv.wrapped(), true);
				case CACHE2D -> new ChunkNoiseSampler.class_6948(lv.wrapped());
				case CACHE_ONCE -> new ChunkNoiseSampler.class_6950(lv.wrapped());
				case CACHE_ALL_IN_CELL -> new ChunkNoiseSampler.class_6949(lv.wrapped());
			});
		} else {
			if (this.blender != Blender.getNoBlending()) {
				if (densityFunction == DensityFunctionTypes.BlendAlpha.INSTANCE) {
					return this.field_36585;
				}

				if (densityFunction == DensityFunctionTypes.BlendOffset.INSTANCE) {
					return this.field_36586;
				}
			}

			if (densityFunction == DensityFunctionTypes.Beardifier.INSTANCE) {
				return this.field_37113;
			} else {
				return densityFunction instanceof DensityFunctionTypes.RegistryEntryHolder registryEntryHolder ? registryEntryHolder.function().value() : densityFunction;
			}
		}
	}

	@FunctionalInterface
	public interface BlockStateSampler {
		@Nullable
		BlockState sample(DensityFunction.NoisePos pos);
	}

	public class NoiseInterpolator implements DensityFunctionTypes.Wrapper, ChunkNoiseSampler.ParentedNoiseType {
		double[][] startNoiseBuffer;
		double[][] endNoiseBuffer;
		private final DensityFunction columnSampler;
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

		NoiseInterpolator(DensityFunction columnSampler) {
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

		void sampleNoiseZ(double deltaZ) {
			this.result = MathHelper.lerp(deltaZ, this.z0, this.z1);
		}

		@Override
		public double sample(DensityFunction.NoisePos pos) {
			if (pos != ChunkNoiseSampler.this) {
				return this.columnSampler.sample(pos);
			} else if (!ChunkNoiseSampler.this.field_36592) {
				throw new IllegalStateException("Trying to sample interpolator outside the interpolation loop");
			} else {
				return ChunkNoiseSampler.this.field_36593
					? MathHelper.lerp3(
						(double)ChunkNoiseSampler.this.field_36574 / (double)ChunkNoiseSampler.this.horizontalBlockSize,
						(double)ChunkNoiseSampler.this.field_36575 / (double)ChunkNoiseSampler.this.verticalBlockSize,
						(double)ChunkNoiseSampler.this.field_36576 / (double)ChunkNoiseSampler.this.horizontalBlockSize,
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
		public void method_40470(double[] ds, DensityFunction.class_6911 arg) {
			if (ChunkNoiseSampler.this.field_36593) {
				arg.method_40478(ds, this);
			} else {
				this.wrapped().method_40470(ds, arg);
			}
		}

		@Override
		public DensityFunction wrapped() {
			return this.columnSampler;
		}

		private void swapBuffers() {
			double[][] ds = this.startNoiseBuffer;
			this.startNoiseBuffer = this.endNoiseBuffer;
			this.endNoiseBuffer = ds;
		}

		@Override
		public DensityFunctionTypes.class_6927.Type type() {
			return DensityFunctionTypes.class_6927.Type.INTERPOLATED;
		}
	}

	interface ParentedNoiseType extends DensityFunction {
		DensityFunction wrapped();

		@Override
		default double minValue() {
			return this.wrapped().minValue();
		}

		@Override
		default double maxValue() {
			return this.wrapped().maxValue();
		}
	}

	class class_6946 implements ChunkNoiseSampler.ParentedNoiseType {
		@Override
		public DensityFunction wrapped() {
			return DensityFunctionTypes.BlendAlpha.INSTANCE;
		}

		@Override
		public DensityFunction apply(DensityFunction.DensityFunctionVisitor visitor) {
			return this.wrapped().apply(visitor);
		}

		@Override
		public double sample(DensityFunction.NoisePos pos) {
			return ChunkNoiseSampler.this.method_40535(pos.blockX(), pos.blockZ()).alpha();
		}

		@Override
		public void method_40470(double[] ds, DensityFunction.class_6911 arg) {
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

		@Override
		public CodecHolder<? extends DensityFunction> getCodec() {
			return DensityFunctionTypes.BlendAlpha.CODEC;
		}
	}

	class class_6947 implements ChunkNoiseSampler.ParentedNoiseType {
		@Override
		public DensityFunction wrapped() {
			return DensityFunctionTypes.BlendOffset.INSTANCE;
		}

		@Override
		public DensityFunction apply(DensityFunction.DensityFunctionVisitor visitor) {
			return this.wrapped().apply(visitor);
		}

		@Override
		public double sample(DensityFunction.NoisePos pos) {
			return ChunkNoiseSampler.this.method_40535(pos.blockX(), pos.blockZ()).blendingOffset();
		}

		@Override
		public void method_40470(double[] ds, DensityFunction.class_6911 arg) {
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

		@Override
		public CodecHolder<? extends DensityFunction> getCodec() {
			return DensityFunctionTypes.BlendOffset.CODEC;
		}
	}

	static class class_6948 implements DensityFunctionTypes.Wrapper, ChunkNoiseSampler.ParentedNoiseType {
		private final DensityFunction field_36599;
		private long field_36600 = ChunkPos.MARKER;
		private double field_36601;

		class_6948(DensityFunction densityFunction) {
			this.field_36599 = densityFunction;
		}

		@Override
		public double sample(DensityFunction.NoisePos pos) {
			int i = pos.blockX();
			int j = pos.blockZ();
			long l = ChunkPos.toLong(i, j);
			if (this.field_36600 == l) {
				return this.field_36601;
			} else {
				this.field_36600 = l;
				double d = this.field_36599.sample(pos);
				this.field_36601 = d;
				return d;
			}
		}

		@Override
		public void method_40470(double[] ds, DensityFunction.class_6911 arg) {
			this.field_36599.method_40470(ds, arg);
		}

		@Override
		public DensityFunction wrapped() {
			return this.field_36599;
		}

		@Override
		public DensityFunctionTypes.class_6927.Type type() {
			return DensityFunctionTypes.class_6927.Type.CACHE2D;
		}
	}

	class class_6949 implements DensityFunctionTypes.Wrapper, ChunkNoiseSampler.ParentedNoiseType {
		final DensityFunction field_36603;
		final double[] field_36604;

		class_6949(DensityFunction densityFunction) {
			this.field_36603 = densityFunction;
			this.field_36604 = new double[ChunkNoiseSampler.this.horizontalBlockSize
				* ChunkNoiseSampler.this.horizontalBlockSize
				* ChunkNoiseSampler.this.verticalBlockSize];
			ChunkNoiseSampler.this.field_36581.add(this);
		}

		@Override
		public double sample(DensityFunction.NoisePos pos) {
			if (pos != ChunkNoiseSampler.this) {
				return this.field_36603.sample(pos);
			} else if (!ChunkNoiseSampler.this.field_36592) {
				throw new IllegalStateException("Trying to sample interpolator outside the interpolation loop");
			} else {
				int i = ChunkNoiseSampler.this.field_36574;
				int j = ChunkNoiseSampler.this.field_36575;
				int k = ChunkNoiseSampler.this.field_36576;
				return i >= 0
						&& j >= 0
						&& k >= 0
						&& i < ChunkNoiseSampler.this.horizontalBlockSize
						&& j < ChunkNoiseSampler.this.verticalBlockSize
						&& k < ChunkNoiseSampler.this.horizontalBlockSize
					? this.field_36604[((ChunkNoiseSampler.this.verticalBlockSize - 1 - j) * ChunkNoiseSampler.this.horizontalBlockSize + i)
							* ChunkNoiseSampler.this.horizontalBlockSize
						+ k]
					: this.field_36603.sample(pos);
			}
		}

		@Override
		public void method_40470(double[] ds, DensityFunction.class_6911 arg) {
			arg.method_40478(ds, this);
		}

		@Override
		public DensityFunction wrapped() {
			return this.field_36603;
		}

		@Override
		public DensityFunctionTypes.class_6927.Type type() {
			return DensityFunctionTypes.class_6927.Type.CACHE_ALL_IN_CELL;
		}
	}

	class class_6950 implements DensityFunctionTypes.Wrapper, ChunkNoiseSampler.ParentedNoiseType {
		private final DensityFunction field_36606;
		private long field_36607;
		private long field_36608;
		private double field_36609;
		@Nullable
		private double[] field_36610;

		class_6950(DensityFunction densityFunction) {
			this.field_36606 = densityFunction;
		}

		@Override
		public double sample(DensityFunction.NoisePos pos) {
			if (pos != ChunkNoiseSampler.this) {
				return this.field_36606.sample(pos);
			} else if (this.field_36610 != null && this.field_36608 == ChunkNoiseSampler.this.field_36578) {
				return this.field_36610[ChunkNoiseSampler.this.field_36579];
			} else if (this.field_36607 == ChunkNoiseSampler.this.field_36577) {
				return this.field_36609;
			} else {
				this.field_36607 = ChunkNoiseSampler.this.field_36577;
				double d = this.field_36606.sample(pos);
				this.field_36609 = d;
				return d;
			}
		}

		@Override
		public void method_40470(double[] ds, DensityFunction.class_6911 arg) {
			if (this.field_36610 != null && this.field_36608 == ChunkNoiseSampler.this.field_36578) {
				System.arraycopy(this.field_36610, 0, ds, 0, ds.length);
			} else {
				this.wrapped().method_40470(ds, arg);
				if (this.field_36610 != null && this.field_36610.length == ds.length) {
					System.arraycopy(ds, 0, this.field_36610, 0, ds.length);
				} else {
					this.field_36610 = (double[])ds.clone();
				}

				this.field_36608 = ChunkNoiseSampler.this.field_36578;
			}
		}

		@Override
		public DensityFunction wrapped() {
			return this.field_36606;
		}

		@Override
		public DensityFunctionTypes.class_6927.Type type() {
			return DensityFunctionTypes.class_6927.Type.CACHE_ONCE;
		}
	}

	class class_6951 implements DensityFunctionTypes.Wrapper, ChunkNoiseSampler.ParentedNoiseType {
		private final DensityFunction field_36612;
		final double[][] field_36613;

		class_6951(DensityFunction densityFunction, boolean bl) {
			this.field_36612 = densityFunction;
			this.field_36613 = new double[ChunkNoiseSampler.this.field_36589 + 1][ChunkNoiseSampler.this.field_36589 + 1];
			if (bl) {
				for (int i = 0; i <= ChunkNoiseSampler.this.field_36589; i++) {
					int j = ChunkNoiseSampler.this.biomeX + i;
					int k = BiomeCoords.toBlock(j);

					for (int l = 0; l <= ChunkNoiseSampler.this.field_36589; l++) {
						int m = ChunkNoiseSampler.this.biomeZ + l;
						int n = BiomeCoords.toBlock(m);
						this.field_36613[i][l] = densityFunction.sample(new DensityFunction.UnblendedNoisePos(k, 0, n));
					}
				}
			}
		}

		@Override
		public double sample(DensityFunction.NoisePos pos) {
			int i = BiomeCoords.fromBlock(pos.blockX());
			int j = BiomeCoords.fromBlock(pos.blockZ());
			int k = i - ChunkNoiseSampler.this.biomeX;
			int l = j - ChunkNoiseSampler.this.biomeZ;
			int m = this.field_36613.length;
			return k >= 0 && l >= 0 && k < m && l < m ? this.field_36613[k][l] : this.field_36612.sample(pos);
		}

		@Override
		public void method_40470(double[] ds, DensityFunction.class_6911 arg) {
			arg.method_40478(ds, this);
		}

		@Override
		public DensityFunction wrapped() {
			return this.field_36612;
		}

		@Override
		public DensityFunctionTypes.class_6927.Type type() {
			return DensityFunctionTypes.class_6927.Type.FLAT_CACHE;
		}
	}
}
