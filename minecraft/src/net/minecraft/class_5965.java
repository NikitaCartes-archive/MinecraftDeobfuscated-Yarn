package net.minecraft;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;
import java.util.function.DoubleSupplier;
import java.util.function.ToDoubleFunction;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.PacketByteBuf;

@Environment(EnvType.CLIENT)
public class class_5965 {
	private final class_5951 field_29598;
	private final DoubleSupplier field_29599;
	private final ByteBuf field_29600;
	private volatile boolean field_29601;
	@Nullable
	private final Runnable field_29602;
	@Nullable
	private final class_5965.class_5967 field_29603;

	private <T> class_5965(class_5951 arg, DoubleSupplier doubleSupplier, @Nullable Runnable runnable, @Nullable class_5965.class_5967 arg2) {
		this.field_29598 = arg;
		this.field_29602 = runnable;
		this.field_29599 = doubleSupplier;
		this.field_29603 = arg2;
		this.field_29600 = new PacketByteBuf(Unpooled.directBuffer());
		this.field_29601 = true;
	}

	public static class_5965 method_34776(class_5951 arg, DoubleSupplier doubleSupplier) {
		return new class_5965(arg, doubleSupplier, null, null);
	}

	public static class_5965 method_34778(String string, DoubleSupplier doubleSupplier) {
		return method_34776(new class_5951(string), doubleSupplier);
	}

	public static <T> class_5965 method_34777(String string, T object, ToDoubleFunction<T> toDoubleFunction) {
		return method_34779(string, toDoubleFunction, object).method_34787();
	}

	public static <T> class_5965.class_5966<T> method_34779(String string, ToDoubleFunction<T> toDoubleFunction, T object) {
		return new class_5965.class_5966<>(new class_5951(string), toDoubleFunction, object);
	}

	public int method_34775() {
		return this.field_29600.readableBytes() / 8;
	}

	public void method_34780() {
		if (!this.field_29601) {
			throw new IllegalStateException("Not running");
		} else {
			if (this.field_29602 != null) {
				this.field_29602.run();
			}
		}
	}

	public void method_34781() {
		this.method_34786();
		double d = this.field_29599.getAsDouble();
		this.field_29600.writeDouble(d);
		if (this.field_29603 != null) {
			this.field_29603.method_34792(d);
		}
	}

	public void method_34782() {
		this.method_34786();
		this.field_29600.release();
		this.field_29601 = false;
	}

	private void method_34786() {
		if (!this.field_29601) {
			throw new IllegalStateException(String.format("Sampler for metric %s not started!", this.field_29598.method_34704()));
		}
	}

	public class_5951 method_34783() {
		return this.field_29598;
	}

	public boolean method_34784() {
		return this.field_29600.isReadable(8);
	}

	public double method_34785() {
		return this.field_29600.readDouble();
	}

	@Environment(EnvType.CLIENT)
	public static class class_5966<T> {
		private final class_5951 field_29604;
		private final DoubleSupplier field_29605;
		private final T field_29606;
		@Nullable
		private Runnable field_29607 = null;
		@Nullable
		private class_5965.class_5967 field_29608;

		public class_5966(class_5951 arg, ToDoubleFunction<T> toDoubleFunction, T object) {
			this.field_29604 = arg;
			this.field_29605 = () -> toDoubleFunction.applyAsDouble(object);
			this.field_29606 = object;
		}

		public class_5965.class_5966<T> method_34789(Consumer<T> consumer) {
			this.field_29607 = () -> consumer.accept(this.field_29606);
			return this;
		}

		public class_5965.class_5966<T> method_34788(class_5965.class_5967 arg) {
			this.field_29608 = arg;
			return this;
		}

		public class_5965 method_34787() {
			return new class_5965(this.field_29604, this.field_29605, this.field_29607, this.field_29608);
		}
	}

	@Environment(EnvType.CLIENT)
	public interface class_5967 {
		void method_34792(double d);
	}

	@Environment(EnvType.CLIENT)
	public static class class_5968 implements class_5965.class_5967 {
		private final float field_29609;
		private final DoubleConsumer field_29610;
		private double field_29611 = Double.MIN_VALUE;

		public class_5968(float f, DoubleConsumer doubleConsumer) {
			this.field_29609 = f;
			this.field_29610 = doubleConsumer;
		}

		@Override
		public void method_34792(double d) {
			boolean bl = this.field_29611 != Double.MIN_VALUE && d > this.field_29611 && (d - this.field_29611) / this.field_29611 >= (double)this.field_29609;
			if (bl) {
				this.field_29610.accept(d);
			}

			this.field_29611 = d;
		}
	}
}
