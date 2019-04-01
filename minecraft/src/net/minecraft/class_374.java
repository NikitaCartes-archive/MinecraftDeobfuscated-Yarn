package net.minecraft;

import com.google.common.collect.Queues;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Arrays;
import java.util.Deque;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_374 extends class_332 {
	private final class_310 field_2238;
	private final class_374.class_375<?>[] field_2239 = new class_374.class_375[5];
	private final Deque<class_368> field_2240 = Queues.<class_368>newArrayDeque();

	public class_374(class_310 arg) {
		this.field_2238 = arg;
	}

	public void method_1996() {
		if (!this.field_2238.field_1690.field_1842) {
			class_308.method_1450();

			for (int i = 0; i < this.field_2239.length; i++) {
				class_374.class_375<?> lv = this.field_2239[i];
				if (lv != null && lv.method_2002(this.field_2238.field_1704.method_4486(), i)) {
					this.field_2239[i] = null;
				}

				if (this.field_2239[i] == null && !this.field_2240.isEmpty()) {
					this.field_2239[i] = new class_374.class_375((class_368)this.field_2240.removeFirst());
				}
			}
		}
	}

	@Nullable
	public <T extends class_368> T method_1997(Class<? extends T> class_, Object object) {
		for (class_374.class_375<?> lv : this.field_2239) {
			if (lv != null && class_.isAssignableFrom(lv.method_2001().getClass()) && lv.method_2001().method_1987().equals(object)) {
				return (T)lv.method_2001();
			}
		}

		for (class_368 lv2 : this.field_2240) {
			if (class_.isAssignableFrom(lv2.getClass()) && lv2.method_1987().equals(object)) {
				return (T)lv2;
			}
		}

		return null;
	}

	public void method_2000() {
		Arrays.fill(this.field_2239, null);
		this.field_2240.clear();
	}

	public void method_1999(class_368 arg) {
		this.field_2240.add(arg);
	}

	public class_310 method_1995() {
		return this.field_2238;
	}

	@Environment(EnvType.CLIENT)
	class class_375<T extends class_368> {
		private final T field_2241;
		private long field_2243 = -1L;
		private long field_2242 = -1L;
		private class_368.class_369 field_2244 = class_368.class_369.field_2210;

		private class_375(T arg2) {
			this.field_2241 = arg2;
		}

		public T method_2001() {
			return this.field_2241;
		}

		private float method_2003(long l) {
			float f = class_3532.method_15363((float)(l - this.field_2243) / 600.0F, 0.0F, 1.0F);
			f *= f;
			return this.field_2244 == class_368.class_369.field_2209 ? 1.0F - f : f;
		}

		public boolean method_2002(int i, int j) {
			long l = class_156.method_658();
			if (this.field_2243 == -1L) {
				this.field_2243 = l;
				this.field_2244.method_1988(class_374.this.field_2238.method_1483());
			}

			if (this.field_2244 == class_368.class_369.field_2210 && l - this.field_2243 <= 600L) {
				this.field_2242 = l;
			}

			GlStateManager.pushMatrix();
			GlStateManager.translatef((float)i - 160.0F * this.method_2003(l), (float)(j * 32), (float)(500 + j));
			class_368.class_369 lv = this.field_2241.method_1986(class_374.this, l - this.field_2242);
			GlStateManager.popMatrix();
			if (lv != this.field_2244) {
				this.field_2243 = l - (long)((int)((1.0F - this.method_2003(l)) * 600.0F));
				this.field_2244 = lv;
				this.field_2244.method_1988(class_374.this.field_2238.method_1483());
			}

			return this.field_2244 == class_368.class_369.field_2209 && l - this.field_2243 > 600L;
		}
	}
}
