package net.minecraft;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtType;

public class class_6841 extends class_6844 {
	private int field_36258;
	private final Set<NbtType<?>> field_36259;
	private final Deque<class_6841.class_6842> field_36260 = new ArrayDeque();

	public class_6841(class_6841.class_6843... args) {
		this.field_36258 = args.length;
		Builder<NbtType<?>> builder = ImmutableSet.builder();
		class_6841.class_6842 lv = new class_6841.class_6842(1);

		for (class_6841.class_6843 lv2 : args) {
			lv.method_39881(lv2);
			builder.add(lv2.type);
		}

		this.field_36260.push(lv);
		builder.add(NbtCompound.TYPE);
		this.field_36259 = builder.build();
	}

	@Override
	public class_6836.class_6838 method_39871(NbtType<?> nbtType) {
		return nbtType != NbtCompound.TYPE ? class_6836.class_6838.HALT : super.method_39871(nbtType);
	}

	@Override
	public class_6836.class_6837 method_39863(NbtType<?> nbtType) {
		class_6841.class_6842 lv = (class_6841.class_6842)this.field_36260.element();
		if (this.method_39888() > lv.depth()) {
			return super.method_39863(nbtType);
		} else if (this.field_36258 <= 0) {
			return class_6836.class_6837.HALT;
		} else {
			return !this.field_36259.contains(nbtType) ? class_6836.class_6837.SKIP : super.method_39863(nbtType);
		}
	}

	@Override
	public class_6836.class_6837 method_39865(NbtType<?> nbtType, String string) {
		class_6841.class_6842 lv = (class_6841.class_6842)this.field_36260.element();
		if (this.method_39888() > lv.depth()) {
			return super.method_39865(nbtType, string);
		} else if (lv.fieldsToGet.remove(string, nbtType)) {
			this.field_36258--;
			return super.method_39865(nbtType, string);
		} else {
			if (nbtType == NbtCompound.TYPE) {
				class_6841.class_6842 lv2 = (class_6841.class_6842)lv.fieldsToRecurse.get(string);
				if (lv2 != null) {
					this.field_36260.push(lv2);
					return super.method_39865(nbtType, string);
				}
			}

			return class_6836.class_6837.SKIP;
		}
	}

	@Override
	public class_6836.class_6838 method_39870() {
		if (this.method_39888() == ((class_6841.class_6842)this.field_36260.element()).depth()) {
			this.field_36260.pop();
		}

		return super.method_39870();
	}

	public int method_39879() {
		return this.field_36258;
	}

	static record class_6842() {
		private final int depth;
		final Map<String, NbtType<?>> fieldsToGet;
		final Map<String, class_6841.class_6842> fieldsToRecurse;

		public class_6842(int i) {
			this(i, new HashMap(), new HashMap());
		}

		private class_6842(int i, Map<String, NbtType<?>> map, Map<String, class_6841.class_6842> map2) {
			this.depth = i;
			this.fieldsToGet = map;
			this.fieldsToRecurse = map2;
		}

		public void method_39881(class_6841.class_6843 arg) {
			if (this.depth <= arg.path.size()) {
				((class_6841.class_6842)this.fieldsToRecurse.computeIfAbsent((String)arg.path.get(this.depth - 1), string -> new class_6841.class_6842(this.depth + 1)))
					.method_39881(arg);
			} else {
				this.fieldsToGet.put(arg.name, arg.type);
			}
		}
	}

	public static record class_6843() {
		final List<String> path;
		final NbtType<?> type;
		final String name;

		public class_6843(NbtType<?> nbtType, String string) {
			this(List.of(), nbtType, string);
		}

		public class_6843(String string, NbtType<?> nbtType, String string2) {
			this(List.of(string), nbtType, string2);
		}

		public class_6843(String string, String string2, NbtType<?> nbtType, String string3) {
			this(List.of(string, string2), nbtType, string3);
		}

		public class_6843(List<String> list, NbtType<?> nbtType, String string) {
			this.path = list;
			this.type = nbtType;
			this.name = string;
		}
	}
}
