package net.minecraft;

import it.unimi.dsi.fastutil.longs.Long2ByteMap;
import it.unimi.dsi.fastutil.longs.Long2ByteOpenHashMap;
import java.io.File;
import java.util.Optional;
import java.util.function.BooleanSupplier;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.chunk.ChunkPos;

public class class_4153 extends class_4180<class_4157> {
	private final class_4153.class_4154 field_18484 = new class_4153.class_4154();

	public class_4153(File file) {
		super(file, class_4157::new, class_4157::new);
	}

	public void method_19115(BlockPos blockPos, class_4158 arg) {
		this.method_19295(ChunkSectionPos.from(blockPos).asLong()).method_19146(blockPos, arg);
	}

	public void method_19112(BlockPos blockPos) {
		this.method_19295(ChunkSectionPos.from(blockPos).asLong()).method_19145(blockPos);
	}

	private Stream<class_4156> method_19125(Predicate<class_4158> predicate, BlockPos blockPos, int i, class_4153.class_4155 arg) {
		int j = i * i;
		return ChunkPos.method_19280(new ChunkPos(blockPos), Math.floorDiv(i, 16))
			.flatMap(chunkPos -> this.method_19123(predicate, chunkPos, arg).filter(argxx -> argxx.method_19141().squaredDistanceTo(blockPos) <= (double)j));
	}

	public Stream<class_4156> method_19123(Predicate<class_4158> predicate, ChunkPos chunkPos, class_4153.class_4155 arg) {
		return IntStream.range(0, 16).boxed().flatMap(integer -> this.method_19119(predicate, ChunkSectionPos.from(chunkPos, integer).asLong(), arg));
	}

	private Stream<class_4156> method_19119(Predicate<class_4158> predicate, long l, class_4153.class_4155 arg) {
		return (Stream<class_4156>)this.method_19294(l).map(arg2 -> arg2.method_19150(predicate, arg)).orElseGet(Stream::empty);
	}

	public Optional<BlockPos> method_19127(Predicate<class_4158> predicate, Predicate<BlockPos> predicate2, BlockPos blockPos, int i, class_4153.class_4155 arg) {
		return this.method_19125(predicate, blockPos, i, arg).map(class_4156::method_19141).filter(predicate2).findFirst();
	}

	public Optional<BlockPos> method_19126(Predicate<class_4158> predicate, Predicate<BlockPos> predicate2, BlockPos blockPos, int i) {
		return this.method_19125(predicate, blockPos, i, class_4153.class_4155.field_18487)
			.filter(arg -> predicate2.test(arg.method_19141()))
			.findFirst()
			.map(arg -> {
				arg.method_19137();
				return arg.method_19141();
			});
	}

	public Optional<BlockPos> method_19131(Predicate<class_4158> predicate, Predicate<BlockPos> predicate2, BlockPos blockPos, int i) {
		return this.method_19125(predicate, blockPos, i, class_4153.class_4155.field_18487)
			.filter(arg -> predicate2.test(arg.method_19141()))
			.reduce((arg, arg2) -> arg.method_19141().squaredDistanceTo(blockPos) < arg2.method_19141().squaredDistanceTo(blockPos) ? arg : arg2)
			.map(arg -> {
				arg.method_19137();
				return arg.method_19141();
			});
	}

	public boolean method_19129(BlockPos blockPos) {
		return this.method_19295(ChunkSectionPos.from(blockPos).asLong()).method_19153(blockPos);
	}

	public boolean method_19116(BlockPos blockPos, Predicate<class_4158> predicate) {
		return (Boolean)this.method_19294(ChunkSectionPos.from(blockPos).asLong()).map(arg -> arg.method_19147(blockPos, predicate)).orElse(false);
	}

	public Optional<class_4158> method_19132(BlockPos blockPos) {
		return this.method_19295(ChunkSectionPos.from(blockPos).asLong()).method_19154(blockPos);
	}

	public int method_19118(ChunkSectionPos chunkSectionPos) {
		this.field_18484.method_19134();
		return this.field_18484.getCurrentLevelFor(chunkSectionPos.asLong());
	}

	private boolean method_19133(long l) {
		return this.method_19119(class_4158.field_18501, l, class_4153.class_4155.field_18488).count() > 0L;
	}

	@Override
	public void method_19290(BooleanSupplier booleanSupplier) {
		super.method_19290(booleanSupplier);
		this.field_18484.method_19134();
	}

	@Override
	protected void method_19288(long l) {
		super.method_19288(l);
		this.field_18484.method_18750(l, this.field_18484.method_18749(l), false);
	}

	@Override
	protected void method_19291(long l) {
		this.field_18484.method_18750(l, this.field_18484.method_18749(l), false);
	}

	final class class_4154 extends class_4079 {
		private final Long2ByteMap field_18486 = new Long2ByteOpenHashMap();

		protected class_4154() {
			super(5, 16, 256);
			this.field_18486.defaultReturnValue((byte)5);
		}

		@Override
		protected int method_18749(long l) {
			return class_4153.this.method_19133(l) ? 0 : 5;
		}

		@Override
		protected int getCurrentLevelFor(long l) {
			return this.field_18486.get(l);
		}

		@Override
		protected void setLevelFor(long l, int i) {
			if (i > 4) {
				this.field_18486.remove(l);
			} else {
				this.field_18486.put(l, (byte)i);
			}
		}

		public void method_19134() {
			super.updateLevels(Integer.MAX_VALUE);
		}
	}

	public static enum class_4155 {
		field_18487(class_4156::method_19139),
		field_18488(class_4156::method_19140),
		field_18489(arg -> true);

		private final Predicate<? super class_4156> field_18490;

		private class_4155(Predicate<? super class_4156> predicate) {
			this.field_18490 = predicate;
		}

		public Predicate<? super class_4156> method_19135() {
			return this.field_18490;
		}
	}
}
