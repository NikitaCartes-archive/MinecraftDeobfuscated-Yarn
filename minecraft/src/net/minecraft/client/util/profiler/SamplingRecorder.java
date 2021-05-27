package net.minecraft.client.util.profiler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import it.unimi.dsi.fastutil.ints.Int2DoubleMap;
import it.unimi.dsi.fastutil.ints.Int2DoubleOpenHashMap;
import java.util.function.Consumer;
import java.util.function.DoubleSupplier;
import java.util.function.ToDoubleFunction;
import javax.annotation.Nullable;

public class SamplingRecorder {
	private final String field_33882;
	private final SamplingChannel metric;
	private final DoubleSupplier timeGetter;
	private final ByteBuf field_33883;
	private final ByteBuf buffer;
	private volatile boolean active;
	@Nullable
	private final Runnable startAction;
	@Nullable
	public final SamplingRecorder.ValueConsumer writeAction;
	private double field_33884;

	protected SamplingRecorder(
		String string,
		SamplingChannel samplingChannel,
		DoubleSupplier doubleSupplier,
		@Nullable Runnable runnable,
		@Nullable SamplingRecorder.ValueConsumer valueConsumer
	) {
		this.field_33882 = string;
		this.metric = samplingChannel;
		this.startAction = runnable;
		this.timeGetter = doubleSupplier;
		this.writeAction = valueConsumer;
		this.buffer = ByteBufAllocator.DEFAULT.buffer();
		this.field_33883 = ByteBufAllocator.DEFAULT.buffer();
		this.active = true;
	}

	public static SamplingRecorder create(String string, SamplingChannel samplingChannel, DoubleSupplier doubleSupplier) {
		return new SamplingRecorder(string, samplingChannel, doubleSupplier, null, null);
	}

	public static <T> SamplingRecorder create(String string, SamplingChannel samplingChannel, T object, ToDoubleFunction<T> toDoubleFunction) {
		return create(string, samplingChannel, toDoubleFunction, object).create();
	}

	public static <T> SamplingRecorder.Builder<T> create(String name, SamplingChannel samplingChannel, ToDoubleFunction<T> toDoubleFunction, T object) {
		return new SamplingRecorder.Builder<>(name, samplingChannel, toDoubleFunction, object);
	}

	public void start() {
		if (!this.active) {
			throw new IllegalStateException("Not running");
		} else {
			if (this.startAction != null) {
				this.startAction.run();
			}
		}
	}

	public void sample(int i) {
		this.checkState();
		this.field_33884 = this.timeGetter.getAsDouble();
		this.buffer.writeDouble(this.field_33884);
		this.field_33883.writeInt(i);
	}

	public void stop() {
		this.checkState();
		this.buffer.release();
		this.field_33883.release();
		this.active = false;
	}

	private void checkState() {
		if (!this.active) {
			throw new IllegalStateException(String.format("Sampler for metric %s not started!", this.field_33882));
		}
	}

	public DoubleSupplier method_37170() {
		return this.timeGetter;
	}

	public String method_37171() {
		return this.field_33882;
	}

	public SamplingChannel method_37172() {
		return this.metric;
	}

	public SamplingRecorder.class_6398 method_37173() {
		Int2DoubleMap int2DoubleMap = new Int2DoubleOpenHashMap();
		int i = Integer.MIN_VALUE;
		int j = Integer.MIN_VALUE;

		while (this.buffer.isReadable(8)) {
			int k = this.field_33883.readInt();
			if (i == Integer.MIN_VALUE) {
				i = k;
			}

			int2DoubleMap.put(k, this.buffer.readDouble());
			j = k;
		}

		return new SamplingRecorder.class_6398(i, j, int2DoubleMap);
	}

	public boolean method_37174() {
		return this.writeAction != null && this.writeAction.accept(this.field_33884);
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (object != null && this.getClass() == object.getClass()) {
			SamplingRecorder samplingRecorder = (SamplingRecorder)object;
			return this.field_33882.equals(samplingRecorder.field_33882) && this.metric.equals(samplingRecorder.metric);
		} else {
			return false;
		}
	}

	public int hashCode() {
		return this.field_33882.hashCode();
	}

	public static class Builder<T> {
		private final String field_33885;
		private final SamplingChannel field_33886;
		private final DoubleSupplier timeGetter;
		private final T context;
		@Nullable
		private Runnable startAction;
		@Nullable
		private SamplingRecorder.ValueConsumer writeAction;

		public Builder(String string, SamplingChannel samplingChannel, ToDoubleFunction<T> toDoubleFunction, T object) {
			this.field_33885 = string;
			this.field_33886 = samplingChannel;
			this.timeGetter = () -> toDoubleFunction.applyAsDouble(object);
			this.context = object;
		}

		public SamplingRecorder.Builder<T> startAction(Consumer<T> action) {
			this.startAction = () -> action.accept(this.context);
			return this;
		}

		public SamplingRecorder.Builder<T> writeAction(SamplingRecorder.ValueConsumer writeAction) {
			this.writeAction = writeAction;
			return this;
		}

		public SamplingRecorder create() {
			return new SamplingRecorder(this.field_33885, this.field_33886, this.timeGetter, this.startAction, this.writeAction);
		}
	}

	public static class HighPassValueConsumer implements SamplingRecorder.ValueConsumer {
		private final float threshold;
		private double lastValue = Double.MIN_VALUE;

		public HighPassValueConsumer(float threshold) {
			this.threshold = threshold;
		}

		@Override
		public boolean accept(double value) {
			boolean bl;
			if (this.lastValue != Double.MIN_VALUE && !(value <= this.lastValue)) {
				bl = (value - this.lastValue) / this.lastValue >= (double)this.threshold;
			} else {
				bl = false;
			}

			this.lastValue = value;
			return bl;
		}
	}

	public interface ValueConsumer {
		boolean accept(double value);
	}

	public static class class_6398 {
		private final Int2DoubleMap field_33887;
		private final int field_33888;
		private final int field_33889;

		public class_6398(int i, int j, Int2DoubleMap int2DoubleMap) {
			this.field_33888 = i;
			this.field_33889 = j;
			this.field_33887 = int2DoubleMap;
		}

		public double method_37176(int i) {
			return this.field_33887.get(i);
		}

		public int method_37175() {
			return this.field_33888;
		}

		public int method_37177() {
			return this.field_33889;
		}
	}
}
