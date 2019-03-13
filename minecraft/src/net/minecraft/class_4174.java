package net.minecraft;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.entity.effect.StatusEffectInstance;
import org.apache.commons.lang3.tuple.Pair;

public class class_4174 {
	private final int field_18614;
	private final float field_18615;
	private final boolean field_18616;
	private final boolean field_18617;
	private final boolean field_18618;
	private final List<Pair<StatusEffectInstance, Float>> field_18619;

	private class_4174(int i, float f, boolean bl, boolean bl2, boolean bl3, List<Pair<StatusEffectInstance, Float>> list) {
		this.field_18614 = i;
		this.field_18615 = f;
		this.field_18616 = bl;
		this.field_18617 = bl2;
		this.field_18618 = bl3;
		this.field_18619 = list;
	}

	public int method_19230() {
		return this.field_18614;
	}

	public float method_19231() {
		return this.field_18615;
	}

	public boolean method_19232() {
		return this.field_18616;
	}

	public boolean method_19233() {
		return this.field_18617;
	}

	public boolean method_19234() {
		return this.field_18618;
	}

	public List<Pair<StatusEffectInstance, Float>> method_19235() {
		return this.field_18619;
	}

	public static class class_4175 {
		private int field_18620;
		private float field_18621;
		private boolean field_18622;
		private boolean field_18623;
		private boolean field_18624;
		private final List<Pair<StatusEffectInstance, Float>> field_18625 = Lists.<Pair<StatusEffectInstance, Float>>newArrayList();

		public class_4174.class_4175 method_19238(int i) {
			this.field_18620 = i;
			return this;
		}

		public class_4174.class_4175 method_19237(float f) {
			this.field_18621 = f;
			return this;
		}

		public class_4174.class_4175 method_19236() {
			this.field_18622 = true;
			return this;
		}

		public class_4174.class_4175 method_19240() {
			this.field_18623 = true;
			return this;
		}

		public class_4174.class_4175 method_19241() {
			this.field_18624 = true;
			return this;
		}

		public class_4174.class_4175 method_19239(StatusEffectInstance statusEffectInstance, float f) {
			this.field_18625.add(Pair.of(statusEffectInstance, f));
			return this;
		}

		public class_4174 method_19242() {
			return new class_4174(this.field_18620, this.field_18621, this.field_18622, this.field_18623, this.field_18624, this.field_18625);
		}
	}
}
