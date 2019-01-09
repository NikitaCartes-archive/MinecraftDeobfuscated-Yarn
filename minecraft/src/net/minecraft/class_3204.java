package net.minecraft;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Either;
import it.unimi.dsi.fastutil.longs.Long2ByteMap;
import it.unimi.dsi.fastutil.longs.Long2ByteOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2IntMap;
import it.unimi.dsi.fastutil.longs.Long2IntMaps;
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap.Entry;
import it.unimi.dsi.fastutil.objects.ObjectAVLTreeSet;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import javax.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class class_3204 extends class_3196 {
	private static final Logger field_16211 = LogManager.getLogger();
	private static final int field_17452 = 33 + class_2806.method_12175(class_2806.field_12803) - 2;
	private final Long2ObjectMap<ObjectSet<class_3222>> field_17453 = new Long2ObjectOpenHashMap<>();
	private final Long2ObjectOpenHashMap<ObjectSortedSet<class_3228<?>>> field_13895 = new Long2ObjectOpenHashMap<>();
	private final class_3204.class_3205 field_17454 = new class_3204.class_3205(8);
	private final class_3204.class_3948 field_17455 = new class_3204.class_3948(33);
	private final Set<class_3193> field_16210 = Sets.<class_3193>newHashSet();
	private final class_3193.class_3896 field_17456;
	private final class_3906<class_3900.class_3946<Runnable>> field_17457;
	private final class_3906<class_3900.class_3947> field_17458;
	private final LongSet field_17459 = new LongOpenHashSet();
	private final Executor field_17460;
	private long field_13894;

	protected class_3204(Executor executor, Executor executor2) {
		super(class_3215.field_13922 + 2, 16, 256);
		class_3846<Runnable> lv = class_3846.method_16902(executor2, "player ticket throttler");
		class_3900 lv2 = new class_3900(ImmutableList.of(lv), executor, 15);
		this.field_17456 = lv2;
		this.field_17457 = lv2.method_17622(lv, true);
		this.field_17458 = lv2.method_17614(lv);
		this.field_17460 = executor2;
	}

	protected void method_14045() {
		this.field_13894++;
		ObjectIterator<Entry<ObjectSortedSet<class_3228<?>>>> objectIterator = this.field_13895.long2ObjectEntrySet().fastIterator();

		while (objectIterator.hasNext()) {
			Entry<ObjectSortedSet<class_3228<?>>> entry = (Entry<ObjectSortedSet<class_3228<?>>>)objectIterator.next();
			if (((ObjectSortedSet)entry.getValue()).removeIf(arg -> arg.method_14281() == class_3230.field_14032 && arg.method_14284() != this.field_13894)) {
				this.method_14027(entry.getLongKey(), this.method_14046((ObjectSortedSet<class_3228<?>>)entry.getValue()), false);
			}

			if (((ObjectSortedSet)entry.getValue()).isEmpty()) {
				objectIterator.remove();
			}
		}
	}

	private int method_14046(ObjectSortedSet<class_3228<?>> objectSortedSet) {
		ObjectBidirectionalIterator<class_3228<?>> objectBidirectionalIterator = objectSortedSet.iterator();
		return objectBidirectionalIterator.hasNext() ? ((class_3228)objectBidirectionalIterator.next()).method_14283() : class_3215.field_13922 + 1;
	}

	@Override
	protected int method_15480(long l) {
		if (!this.method_14035(l)) {
			class_3193 lv = this.method_14038(l);
			if (lv != null) {
				return lv.method_14005();
			}
		}

		return class_3215.field_13922 + 1;
	}

	protected abstract boolean method_14035(long l);

	@Nullable
	protected abstract class_3193 method_14038(long l);

	@Override
	protected void method_15485(long l, int i) {
		class_3193 lv = this.method_14038(l);
		int j = lv == null ? class_3215.field_13922 + 1 : lv.method_14005();
		if (j != i) {
			lv = this.method_14053(l, i, lv, j);
			if (lv != null) {
				this.field_16210.add(lv);
			}
		}
	}

	@Nullable
	protected abstract class_3193 method_14053(long l, int i, @Nullable class_3193 arg, int j);

	@Override
	protected int method_14028(long l) {
		ObjectSortedSet<class_3228<?>> objectSortedSet = this.field_13895.get(l);
		if (objectSortedSet == null) {
			return Integer.MAX_VALUE;
		} else {
			ObjectBidirectionalIterator<class_3228<?>> objectBidirectionalIterator = objectSortedSet.iterator();
			return !objectBidirectionalIterator.hasNext() ? Integer.MAX_VALUE : ((class_3228)objectBidirectionalIterator.next()).method_14283();
		}
	}

	public boolean method_15892(class_3898 arg) {
		this.field_17454.method_14057();
		this.field_17455.method_14057();
		int i = Integer.MAX_VALUE - this.method_15492(Integer.MAX_VALUE);
		boolean bl = i != 0;
		if (bl) {
		}

		if (!this.field_16210.isEmpty()) {
			this.field_16210.forEach(arg2 -> arg2.method_14007(arg));
			this.field_16210.clear();
			return true;
		} else {
			if (!this.field_17459.isEmpty()) {
				LongIterator longIterator = this.field_17459.iterator();

				while (longIterator.hasNext()) {
					long l = longIterator.nextLong();
					if (this.method_14050(l).stream().anyMatch(argx -> argx.method_14281() == class_3230.field_14033)) {
						class_3193 lv = arg.method_17255(l);
						if (lv == null) {
							throw new IllegalStateException();
						}

						CompletableFuture<Either<class_2818, class_3193.class_3724>> completableFuture = lv.method_14003();
						completableFuture.thenAccept(either -> this.field_17460.execute(() -> this.field_17458.method_16901(class_3900.method_17627(() -> {
								}, l, false))));
					}
				}

				this.field_17459.clear();
			}

			return bl;
		}
	}

	private void method_14042(long l, class_3228<?> arg) {
		ObjectSortedSet<class_3228<?>> objectSortedSet = this.method_14050(l);
		ObjectBidirectionalIterator<class_3228<?>> objectBidirectionalIterator = objectSortedSet.iterator();
		int i;
		if (objectBidirectionalIterator.hasNext()) {
			i = ((class_3228)objectBidirectionalIterator.next()).method_14283();
		} else {
			i = class_3215.field_13922 + 1;
		}

		if (objectSortedSet.add(arg)) {
		}

		if (arg.method_14283() < i) {
			this.method_14027(l, arg.method_14283(), true);
		}
	}

	private void method_17645(long l, class_3228<?> arg) {
		ObjectSortedSet<class_3228<?>> objectSortedSet = this.method_14050(l);
		if (objectSortedSet.remove(arg)) {
		}

		if (objectSortedSet.isEmpty()) {
			this.field_13895.remove(l);
		}

		this.method_14027(l, this.method_14046(objectSortedSet), false);
	}

	public <T> void method_17290(class_3230<T> arg, class_1923 arg2, int i, T object) {
		this.method_14042(arg2.method_8324(), new class_3228<>(arg, i, object, this.field_13894));
	}

	public <T> void method_17291(class_3230<T> arg, class_1923 arg2, int i, T object) {
		this.method_14042(arg2.method_8324(), new class_3228<>(arg, 33 - i, object, this.field_13894));
	}

	public <T> void method_17292(class_3230<T> arg, class_1923 arg2, int i, T object) {
		class_3228<T> lv = new class_3228<>(arg, 33 - i, object, this.field_13894);
		this.method_17645(arg2.method_8324(), lv);
	}

	private ObjectSortedSet<class_3228<?>> method_14050(long l) {
		return this.field_13895.computeIfAbsent(l, lx -> new ObjectAVLTreeSet());
	}

	protected void method_14036(class_1923 arg, boolean bl) {
		class_3228<class_1923> lv = new class_3228<>(class_3230.field_14031, 32, arg, this.field_13894);
		if (bl) {
			this.method_14042(arg.method_8324(), lv);
		} else {
			this.method_17645(arg.method_8324(), lv);
		}
	}

	public void method_14048(long l, class_3222 arg) {
		class_1923 lv = new class_1923(l);
		arg.method_17668(lv);
		this.field_17453.computeIfAbsent(l, lx -> new ObjectOpenHashSet()).add(arg);
		this.field_17454.method_14027(l, 0, true);
		this.field_17455.method_14027(l, 0, true);
	}

	public void method_14051(long l, class_3222 arg) {
		ObjectSet<class_3222> objectSet = this.field_17453.get(l);
		if (objectSet != null) {
			objectSet.remove(arg);
			if (objectSet.isEmpty()) {
				this.field_17453.remove(l);
				this.field_17454.method_14027(l, Integer.MAX_VALUE, false);
				this.field_17455.method_14027(l, Integer.MAX_VALUE, false);
			}
		}
	}

	protected void method_14049(int i) {
		this.field_17455.method_17658(i);
	}

	public int method_14052() {
		this.field_17454.method_14057();
		return this.field_17454.field_13896.size();
	}

	class class_3205 extends class_3196 {
		protected final Long2ByteMap field_13896 = new Long2ByteOpenHashMap();
		protected final int field_17461;

		protected class_3205(int i) {
			super(i + 2, 16, 256);
			this.field_17461 = i;
			this.field_13896.defaultReturnValue((byte)(i + 2));
		}

		@Override
		protected int method_15480(long l) {
			return this.field_13896.get(l);
		}

		@Override
		protected void method_15485(long l, int i) {
			byte b;
			if (i > this.field_17461) {
				b = this.field_13896.remove(l);
			} else {
				b = this.field_13896.put(l, (byte)i);
			}

			this.method_17657(l, b, i);
		}

		protected void method_17657(long l, int i, int j) {
		}

		@Override
		protected int method_14028(long l) {
			return this.method_14056(l) ? 0 : Integer.MAX_VALUE;
		}

		private boolean method_14056(long l) {
			ObjectSet<class_3222> objectSet = class_3204.this.field_17453.get(l);
			return objectSet != null && !objectSet.isEmpty();
		}

		public void method_14057() {
			this.method_15492(Integer.MAX_VALUE);
		}
	}

	class class_3948 extends class_3204.class_3205 {
		private int field_17464;
		private final Long2IntMap field_17465 = Long2IntMaps.synchronize(new Long2IntOpenHashMap());
		private final LongSet field_17466 = new LongOpenHashSet();

		protected class_3948(int i) {
			super(i);
			this.field_17464 = 0;
			this.field_17465.defaultReturnValue(i + 2);
		}

		@Override
		protected void method_17657(long l, int i, int j) {
			this.field_17466.add(l);
		}

		public void method_17658(int i) {
			for (it.unimi.dsi.fastutil.longs.Long2ByteMap.Entry entry : this.field_13896.long2ByteEntrySet()) {
				byte b = entry.getByteValue();
				long l = entry.getLongKey();
				this.method_17660(l, b, this.method_17664(b), b <= i - 2);
			}

			this.field_17464 = i;
		}

		private void method_17660(long l, int i, boolean bl, boolean bl2) {
			if (bl != bl2) {
				class_3228<?> lv = new class_3228<>(class_3230.field_14033, class_3204.field_17452, new class_1923(l), class_3204.this.field_13894);
				if (bl2) {
					class_3204.this.field_17457.method_16901(class_3900.method_17626(() -> class_3204.this.field_17460.execute(() -> {
							class_3204.this.method_14042(l, lv);
							class_3204.this.field_17459.add(l);
						}), l, () -> i));
				} else {
					class_3204.this.field_17458
						.method_16901(class_3900.method_17627(() -> class_3204.this.field_17460.execute(() -> class_3204.this.method_17645(l, lv)), l, true));
				}
			}
		}

		@Override
		public void method_14057() {
			super.method_14057();
			if (!this.field_17466.isEmpty()) {
				LongIterator longIterator = this.field_17466.iterator();

				while (longIterator.hasNext()) {
					long l = longIterator.nextLong();
					int i = this.field_17465.get(l);
					int j = this.method_15480(l);
					if (i != j) {
						class_3204.this.field_17456.method_17209(new class_1923(l), () -> this.field_17465.get(l), j, ix -> this.field_17465.put(l, ix));
						this.method_17660(l, j, this.method_17664(i), this.method_17664(j));
					}
				}

				this.field_17466.clear();
			}
		}

		private boolean method_17664(int i) {
			return i <= this.field_17464 - 2;
		}
	}
}
