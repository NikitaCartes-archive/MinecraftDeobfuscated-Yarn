package net.minecraft;

import com.google.common.collect.Maps;
import com.google.common.primitives.UnsignedLong;
import java.util.Comparator;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_236<T> {
	private static final Logger LOGGER = LogManager.getLogger();
	private final class_233<T> field_1314;
	private final Queue<class_236.class_237<T>> field_1313 = new PriorityQueue(method_987());
	private UnsignedLong field_1311 = UnsignedLong.ZERO;
	private final Map<String, class_236.class_237<T>> field_1312 = Maps.<String, class_236.class_237<T>>newHashMap();

	private static <T> Comparator<class_236.class_237<T>> method_987() {
		return (arg, arg2) -> {
			int i = Long.compare(arg.trigerTime, arg2.trigerTime);
			return i != 0 ? i : arg.field_1319.compareTo(arg2.field_1319);
		};
	}

	public class_236(class_233<T> arg) {
		this.field_1314 = arg;
	}

	public void method_988(T object, long l) {
		while (true) {
			class_236.class_237<T> lv = (class_236.class_237<T>)this.field_1313.peek();
			if (lv == null || lv.trigerTime > l) {
				return;
			}

			this.field_1313.remove();
			this.field_1312.remove(lv.name);
			lv.field_1316.method_974(object, this, l);
		}
	}

	private void method_985(String string, long l, class_234<T> arg) {
		this.field_1311 = this.field_1311.plus(UnsignedLong.ONE);
		class_236.class_237<T> lv = new class_236.class_237<>(l, this.field_1311, string, arg);
		this.field_1312.put(string, lv);
		this.field_1313.add(lv);
	}

	public boolean method_981(String string, long l, class_234<T> arg) {
		if (this.field_1312.containsKey(string)) {
			return false;
		} else {
			this.method_985(string, l, arg);
			return true;
		}
	}

	public void method_984(String string, long l, class_234<T> arg) {
		class_236.class_237<T> lv = (class_236.class_237<T>)this.field_1312.remove(string);
		if (lv != null) {
			this.field_1313.remove(lv);
		}

		this.method_985(string, l, arg);
	}

	private void method_986(CompoundTag compoundTag) {
		CompoundTag compoundTag2 = compoundTag.getCompound("Callback");
		class_234<T> lv = this.field_1314.method_972(compoundTag2);
		if (lv != null) {
			String string = compoundTag.getString("Name");
			long l = compoundTag.getLong("TriggerTime");
			this.method_981(string, l, lv);
		}
	}

	public void method_979(ListTag listTag) {
		this.field_1313.clear();
		this.field_1312.clear();
		this.field_1311 = UnsignedLong.ZERO;
		if (!listTag.isEmpty()) {
			if (listTag.getType() != 10) {
				LOGGER.warn("Invalid format of events: " + listTag);
			} else {
				for (Tag tag : listTag) {
					this.method_986((CompoundTag)tag);
				}
			}
		}
	}

	private CompoundTag method_980(class_236.class_237<T> arg) {
		CompoundTag compoundTag = new CompoundTag();
		compoundTag.putString("Name", arg.name);
		compoundTag.putLong("TriggerTime", arg.trigerTime);
		compoundTag.put("Callback", this.field_1314.method_973(arg.field_1316));
		return compoundTag;
	}

	public ListTag method_982() {
		ListTag listTag = new ListTag();
		this.field_1313.stream().sorted(method_987()).map(this::method_980).forEach(listTag::add);
		return listTag;
	}

	public static class class_237<T> {
		public final long trigerTime;
		public final UnsignedLong field_1319;
		public final String name;
		public final class_234<T> field_1316;

		private class_237(long l, UnsignedLong unsignedLong, String string, class_234<T> arg) {
			this.trigerTime = l;
			this.field_1319 = unsignedLong;
			this.name = string;
			this.field_1316 = arg;
		}
	}
}
