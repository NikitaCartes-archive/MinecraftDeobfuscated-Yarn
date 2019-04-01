package net.minecraft;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import java.util.Random;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_1093 implements class_1087 {
	protected final List<class_777> field_5411;
	protected final Map<class_2350, List<class_777>> field_5414;
	protected final boolean field_5415;
	protected final boolean field_5413;
	protected final class_1058 field_5416;
	protected final class_809 field_5417;
	protected final class_806 field_5412;

	public class_1093(List<class_777> list, Map<class_2350, List<class_777>> map, boolean bl, boolean bl2, class_1058 arg, class_809 arg2, class_806 arg3) {
		this.field_5411 = list;
		this.field_5414 = map;
		this.field_5415 = bl;
		this.field_5413 = bl2;
		this.field_5416 = arg;
		this.field_5417 = arg2;
		this.field_5412 = arg3;
	}

	@Override
	public List<class_777> method_4707(@Nullable class_2680 arg, @Nullable class_2350 arg2, Random random) {
		return arg2 == null ? this.field_5411 : (List)this.field_5414.get(arg2);
	}

	@Override
	public boolean method_4708() {
		return this.field_5415;
	}

	@Override
	public boolean method_4712() {
		return this.field_5413;
	}

	@Override
	public boolean method_4713() {
		return false;
	}

	@Override
	public class_1058 method_4711() {
		return this.field_5416;
	}

	@Override
	public class_809 method_4709() {
		return this.field_5417;
	}

	@Override
	public class_806 method_4710() {
		return this.field_5412;
	}

	@Environment(EnvType.CLIENT)
	public static class class_1094 {
		private final List<class_777> field_5419 = Lists.<class_777>newArrayList();
		private final Map<class_2350, List<class_777>> field_5422 = Maps.newEnumMap(class_2350.class);
		private final class_806 field_5423;
		private final boolean field_5421;
		private class_1058 field_5424;
		private final boolean field_5420;
		private final class_809 field_5418;

		public class_1094(class_793 arg, class_806 arg2) {
			this(arg.method_3444(), arg.method_3445(), arg.method_3443(), arg2);
		}

		public class_1094(class_2680 arg, class_1087 arg2, class_1058 arg3, Random random, long l) {
			this(arg2.method_4708(), arg2.method_4712(), arg2.method_4709(), arg2.method_4710());
			this.field_5424 = arg2.method_4711();

			for (class_2350 lv : class_2350.values()) {
				random.setSeed(l);

				for (class_777 lv2 : arg2.method_4707(arg, lv, random)) {
					this.method_4745(lv, new class_798(lv2, arg3));
				}
			}

			random.setSeed(l);

			for (class_777 lv3 : arg2.method_4707(arg, null, random)) {
				this.method_4748(new class_798(lv3, arg3));
			}
		}

		private class_1094(boolean bl, boolean bl2, class_809 arg, class_806 arg2) {
			for (class_2350 lv : class_2350.values()) {
				this.field_5422.put(lv, Lists.newArrayList());
			}

			this.field_5423 = arg2;
			this.field_5421 = bl;
			this.field_5420 = bl2;
			this.field_5418 = arg;
		}

		public class_1093.class_1094 method_4745(class_2350 arg, class_777 arg2) {
			((List)this.field_5422.get(arg)).add(arg2);
			return this;
		}

		public class_1093.class_1094 method_4748(class_777 arg) {
			this.field_5419.add(arg);
			return this;
		}

		public class_1093.class_1094 method_4747(class_1058 arg) {
			this.field_5424 = arg;
			return this;
		}

		public class_1087 method_4746() {
			if (this.field_5424 == null) {
				throw new RuntimeException("Missing particle!");
			} else {
				return new class_1093(this.field_5419, this.field_5422, this.field_5421, this.field_5420, this.field_5424, this.field_5418, this.field_5423);
			}
		}
	}
}
