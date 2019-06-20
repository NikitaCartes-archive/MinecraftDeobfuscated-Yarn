package net.minecraft;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_1097 implements class_1087 {
	private final int field_5433;
	private final List<class_1097.class_1099> field_5434;
	private final class_1087 field_5435;

	public class_1097(List<class_1097.class_1099> list) {
		this.field_5434 = list;
		this.field_5433 = class_3549.method_15445(list);
		this.field_5435 = ((class_1097.class_1099)list.get(0)).field_5437;
	}

	@Override
	public List<class_777> method_4707(@Nullable class_2680 arg, @Nullable class_2350 arg2, Random random) {
		return class_3549.method_15447(this.field_5434, Math.abs((int)random.nextLong()) % this.field_5433).field_5437.method_4707(arg, arg2, random);
	}

	@Override
	public boolean method_4708() {
		return this.field_5435.method_4708();
	}

	@Override
	public boolean method_4712() {
		return this.field_5435.method_4712();
	}

	@Override
	public boolean method_4713() {
		return this.field_5435.method_4713();
	}

	@Override
	public class_1058 method_4711() {
		return this.field_5435.method_4711();
	}

	@Override
	public class_809 method_4709() {
		return this.field_5435.method_4709();
	}

	@Override
	public class_806 method_4710() {
		return this.field_5435.method_4710();
	}

	@Environment(EnvType.CLIENT)
	public static class class_1098 {
		private final List<class_1097.class_1099> field_5436 = Lists.<class_1097.class_1099>newArrayList();

		public class_1097.class_1098 method_4752(@Nullable class_1087 arg, int i) {
			if (arg != null) {
				this.field_5436.add(new class_1097.class_1099(arg, i));
			}

			return this;
		}

		@Nullable
		public class_1087 method_4751() {
			if (this.field_5436.isEmpty()) {
				return null;
			} else {
				return (class_1087)(this.field_5436.size() == 1 ? ((class_1097.class_1099)this.field_5436.get(0)).field_5437 : new class_1097(this.field_5436));
			}
		}
	}

	@Environment(EnvType.CLIENT)
	static class class_1099 extends class_3549.class_3550 {
		protected final class_1087 field_5437;

		public class_1099(class_1087 arg, int i) {
			super(i);
			this.field_5437 = arg;
		}
	}
}
