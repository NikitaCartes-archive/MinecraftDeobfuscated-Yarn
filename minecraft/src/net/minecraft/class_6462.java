package net.minecraft;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.floats.FloatArrayList;
import it.unimi.dsi.fastutil.floats.FloatList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.annotation.Nullable;
import net.minecraft.util.math.MathHelper;

public final class class_6462<C> implements class_6468<C> {
	final class_6468<C> field_34216;
	final float[] field_34217;
	final List<class_6468<C>> field_34218;
	final float[] field_34219;
	String field_34220;

	class_6462(String string, class_6468<C> arg, float[] fs, List<class_6468<C>> list, float[] gs) {
		this.field_34220 = string;
		if (fs.length == list.size() && fs.length == gs.length) {
			this.field_34216 = arg;
			this.field_34217 = fs;
			this.field_34218 = list;
			this.field_34219 = gs;
		} else {
			throw new IllegalArgumentException("All lengths must be equal, got: " + fs.length + " " + list.size() + " " + gs.length);
		}
	}

	public class_6462<C> method_37719() {
		return this;
	}

	@Override
	public float apply(C object) {
		return MathHelper.method_37487(this.field_34216.apply(object), this.field_34217, this.field_34218, this.field_34219).apply(object);
	}

	public static <C> class_6462.class_6463<C> method_37721(class_6468<C> arg) {
		return new class_6462.class_6463<>(arg);
	}

	private String method_37722(float[] fs) {
		return "["
			+ (String)IntStream.range(0, fs.length).mapToDouble(i -> (double)fs[i]).mapToObj(d -> String.format("%.2f", d)).collect(Collectors.joining(", "))
			+ "]";
	}

	public String toString() {
		return "Spline{name="
			+ this.field_34220
			+ ", locations="
			+ this.method_37722(this.field_34217)
			+ ", derivatives="
			+ this.method_37722(this.field_34219)
			+ ", values="
			+ this.field_34218
			+ "}";
	}

	public static final class class_6463<C> {
		private final class_6468<C> field_34221;
		private final FloatList field_34222 = new FloatArrayList();
		private final List<class_6468<C>> field_34223 = Lists.<class_6468<C>>newArrayList();
		private final FloatList field_34224 = new FloatArrayList();
		@Nullable
		private Float field_34225 = null;
		private String field_34226 = null;

		public class_6463(class_6468<C> arg) {
			this.field_34221 = arg;
		}

		public class_6462.class_6463<C> method_37728(String string) {
			this.field_34226 = string;
			return this;
		}

		public class_6462.class_6463<C> method_37725(float f, float g, float h) {
			return this.method_37729(f, new class_6462.class_6464<>(g), h);
		}

		public class_6462.class_6463<C> method_37727(float f, class_6468<C> arg, float g) {
			return this.method_37729(f, arg, g);
		}

		public class_6462.class_6463<C> method_37726(float f, class_6462<C> arg, float g) {
			return this.method_37729(f, arg.method_37719(), g);
		}

		private class_6462.class_6463<C> method_37729(float f, class_6468<C> arg, float g) {
			if (this.field_34225 != null && f <= this.field_34225) {
				throw new IllegalArgumentException("The way things are right now, we depend on registration in descending order");
			} else {
				this.field_34222.add(f);
				this.field_34223.add(arg);
				this.field_34224.add(g);
				this.field_34225 = f;
				return this;
			}
		}

		public class_6462<C> method_37724() {
			if (this.field_34226 == null) {
				throw new IllegalStateException("Splines require a name");
			} else if (this.field_34222.isEmpty()) {
				throw new IllegalStateException("No elements added");
			} else {
				return new class_6462<>(
					this.field_34226, this.field_34221, this.field_34222.toFloatArray(), ImmutableList.copyOf(this.field_34223), this.field_34224.toFloatArray()
				);
			}
		}
	}

	static class class_6464<C> implements class_6468<C> {
		private float field_34227;

		public class_6464(float f) {
			this.field_34227 = f;
		}

		@Override
		public float apply(C object) {
			return this.field_34227;
		}

		public String toString() {
			return String.format("k=%.2f", this.field_34227);
		}
	}

	public interface class_6465 {
		float combine(float f, float g);
	}
}
