/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.util.profiler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;
import java.util.function.DoubleSupplier;
import java.util.function.ToDoubleFunction;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.profiler.Metric;
import net.minecraft.network.PacketByteBuf;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class SamplingRecorder {
    private final Metric metric;
    private final DoubleSupplier timeGetter;
    private final ByteBuf buffer;
    private volatile boolean active;
    @Nullable
    private final Runnable startAction;
    @Nullable
    private final ValueConsumer writeAction;

    <T> SamplingRecorder(Metric metric, DoubleSupplier doubleSupplier, @Nullable Runnable runnable, @Nullable ValueConsumer valueConsumer) {
        this.metric = metric;
        this.startAction = runnable;
        this.timeGetter = doubleSupplier;
        this.writeAction = valueConsumer;
        this.buffer = new PacketByteBuf(Unpooled.directBuffer());
        this.active = true;
    }

    public static SamplingRecorder create(Metric metric, DoubleSupplier timeGetter) {
        return new SamplingRecorder(metric, timeGetter, null, null);
    }

    public static SamplingRecorder create(String name, DoubleSupplier timeGetter) {
        return SamplingRecorder.create(new Metric(name), timeGetter);
    }

    public static <T> SamplingRecorder create(String name, T context, ToDoubleFunction<T> timeFunc) {
        return SamplingRecorder.create(name, timeFunc, context).create();
    }

    public static <T> Builder<T> create(String name, ToDoubleFunction<T> timeFunc, T context) {
        return new Builder<T>(new Metric(name), timeFunc, context);
    }

    public int length() {
        return this.buffer.readableBytes() / 8;
    }

    public void start() {
        if (!this.active) {
            throw new IllegalStateException("Not running");
        }
        if (this.startAction != null) {
            this.startAction.run();
        }
    }

    public void sample() {
        this.checkState();
        double d = this.timeGetter.getAsDouble();
        this.buffer.writeDouble(d);
        if (this.writeAction != null) {
            this.writeAction.accept(d);
        }
    }

    public void stop() {
        this.checkState();
        this.buffer.release();
        this.active = false;
    }

    private void checkState() {
        if (!this.active) {
            throw new IllegalStateException(String.format("Sampler for metric %s not started!", this.metric.getName()));
        }
    }

    public Metric getMetric() {
        return this.metric;
    }

    public boolean canRead() {
        return this.buffer.isReadable(8);
    }

    public double read() {
        return this.buffer.readDouble();
    }

    @Environment(value=EnvType.CLIENT)
    public static interface ValueConsumer {
        public void accept(double var1);
    }

    @Environment(value=EnvType.CLIENT)
    public static class Builder<T> {
        private final Metric metric;
        private final DoubleSupplier timeGetter;
        private final T context;
        @Nullable
        private Runnable startAction = null;
        @Nullable
        private ValueConsumer writeAction;

        public Builder(Metric metric, ToDoubleFunction<T> contextTimeFunc, T context) {
            this.metric = metric;
            this.timeGetter = () -> contextTimeFunc.applyAsDouble(context);
            this.context = context;
        }

        public Builder<T> startAction(Consumer<T> action) {
            this.startAction = () -> action.accept(this.context);
            return this;
        }

        public Builder<T> writeAction(ValueConsumer writeAction) {
            this.writeAction = writeAction;
            return this;
        }

        public SamplingRecorder create() {
            return new SamplingRecorder(this.metric, this.timeGetter, this.startAction, this.writeAction);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class HighPassValueConsumer
    implements ValueConsumer {
        private final float threshold;
        private final DoubleConsumer valueConsumer;
        private double lastValue = Double.MIN_VALUE;

        public HighPassValueConsumer(float threshold, DoubleConsumer valueConsumer) {
            this.threshold = threshold;
            this.valueConsumer = valueConsumer;
        }

        @Override
        public void accept(double value) {
            boolean bl;
            boolean bl2 = bl = this.lastValue != Double.MIN_VALUE && value > this.lastValue && (value - this.lastValue) / this.lastValue >= (double)this.threshold;
            if (bl) {
                this.valueConsumer.accept(value);
            }
            this.lastValue = value;
        }
    }
}

