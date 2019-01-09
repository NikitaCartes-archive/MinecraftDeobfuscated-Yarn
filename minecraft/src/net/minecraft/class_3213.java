package net.minecraft;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

public class class_3213 extends class_1259 {
	private final Set<class_3222> field_13913 = Sets.<class_3222>newHashSet();
	private final Set<class_3222> field_13914 = Collections.unmodifiableSet(this.field_13913);
	private boolean field_13912 = true;

	public class_3213(class_2561 arg, class_1259.class_1260 arg2, class_1259.class_1261 arg3) {
		super(class_3532.method_15394(), arg, arg2, arg3);
	}

	@Override
	public void method_5408(float f) {
		if (f != this.field_5774) {
			super.method_5408(f);
			this.method_14090(class_2629.class_2630.field_12080);
		}
	}

	@Override
	public void method_5416(class_1259.class_1260 arg) {
		if (arg != this.field_5778) {
			super.method_5416(arg);
			this.method_14090(class_2629.class_2630.field_12081);
		}
	}

	@Override
	public void method_5409(class_1259.class_1261 arg) {
		if (arg != this.field_5779) {
			super.method_5409(arg);
			this.method_14090(class_2629.class_2630.field_12081);
		}
	}

	@Override
	public class_1259 method_5406(boolean bl) {
		if (bl != this.field_5776) {
			super.method_5406(bl);
			this.method_14090(class_2629.class_2630.field_12083);
		}

		return this;
	}

	@Override
	public class_1259 method_5410(boolean bl) {
		if (bl != this.field_5775) {
			super.method_5410(bl);
			this.method_14090(class_2629.class_2630.field_12083);
		}

		return this;
	}

	@Override
	public class_1259 method_5411(boolean bl) {
		if (bl != this.field_5773) {
			super.method_5411(bl);
			this.method_14090(class_2629.class_2630.field_12083);
		}

		return this;
	}

	@Override
	public void method_5413(class_2561 arg) {
		if (!Objects.equal(arg, this.field_5777)) {
			super.method_5413(arg);
			this.method_14090(class_2629.class_2630.field_12084);
		}
	}

	private void method_14090(class_2629.class_2630 arg) {
		if (this.field_13912) {
			class_2629 lv = new class_2629(arg, this);

			for (class_3222 lv2 : this.field_13913) {
				lv2.field_13987.method_14364(lv);
			}
		}
	}

	public void method_14088(class_3222 arg) {
		if (this.field_13913.add(arg) && this.field_13912) {
			arg.field_13987.method_14364(new class_2629(class_2629.class_2630.field_12078, this));
		}
	}

	public void method_14089(class_3222 arg) {
		if (this.field_13913.remove(arg) && this.field_13912) {
			arg.field_13987.method_14364(new class_2629(class_2629.class_2630.field_12082, this));
		}
	}

	public void method_14094() {
		if (!this.field_13913.isEmpty()) {
			for (class_3222 lv : Lists.newArrayList(this.field_13913)) {
				this.method_14089(lv);
			}
		}
	}

	public boolean method_14093() {
		return this.field_13912;
	}

	public void method_14091(boolean bl) {
		if (bl != this.field_13912) {
			this.field_13912 = bl;

			for (class_3222 lv : this.field_13913) {
				lv.field_13987.method_14364(new class_2629(bl ? class_2629.class_2630.field_12078 : class_2629.class_2630.field_12082, this));
			}
		}
	}

	public Collection<class_3222> method_14092() {
		return this.field_13914;
	}
}
