package net.minecraft;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.stream.Stream;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;

public class class_4565 {
	private final Map<String, class_4565.class_4566> field_20747 = Maps.<String, class_4565.class_4566>newHashMap();
	private final PersistentStateManager field_20748;

	public class_4565(PersistentStateManager persistentStateManager) {
		this.field_20748 = persistentStateManager;
	}

	private class_4565.class_4566 method_22544(String string, String string2) {
		class_4565.class_4566 lv = new class_4565.class_4566(string2);
		this.field_20747.put(string, lv);
		return lv;
	}

	public CompoundTag method_22546(Identifier identifier) {
		String string = identifier.getNamespace();
		String string2 = method_22543(string);
		class_4565.class_4566 lv = this.field_20748.method_20786(() -> this.method_22544(string, string2), string2);
		return lv != null ? lv.method_22550(identifier.getPath()) : new CompoundTag();
	}

	public void method_22547(Identifier identifier, CompoundTag compoundTag) {
		String string = identifier.getNamespace();
		String string2 = method_22543(string);
		this.field_20748.<class_4565.class_4566>getOrCreate(() -> this.method_22544(string, string2), string2).method_22552(identifier.getPath(), compoundTag);
	}

	public Stream<Identifier> method_22542() {
		return this.field_20747.entrySet().stream().flatMap(entry -> ((class_4565.class_4566)entry.getValue()).method_22554((String)entry.getKey()));
	}

	private static String method_22543(String string) {
		return "command_storage_" + string;
	}

	static class class_4566 extends PersistentState {
		private final Map<String, CompoundTag> field_20749 = Maps.<String, CompoundTag>newHashMap();

		public class_4566(String string) {
			super(string);
		}

		@Override
		public void fromTag(CompoundTag compoundTag) {
			CompoundTag compoundTag2 = compoundTag.getCompound("contents");

			for (String string : compoundTag2.getKeys()) {
				this.field_20749.put(string, compoundTag2.getCompound(string));
			}
		}

		@Override
		public CompoundTag toTag(CompoundTag compoundTag) {
			CompoundTag compoundTag2 = new CompoundTag();
			this.field_20749.forEach((string, compoundTag2x) -> compoundTag2.put(string, compoundTag2x.method_10553()));
			compoundTag.put("contents", compoundTag2);
			return compoundTag;
		}

		public CompoundTag method_22550(String string) {
			CompoundTag compoundTag = (CompoundTag)this.field_20749.get(string);
			return compoundTag != null ? compoundTag : new CompoundTag();
		}

		public void method_22552(String string, CompoundTag compoundTag) {
			if (compoundTag.isEmpty()) {
				this.field_20749.remove(string);
			} else {
				this.field_20749.put(string, compoundTag);
			}

			this.markDirty();
		}

		public Stream<Identifier> method_22554(String string) {
			return this.field_20749.keySet().stream().map(string2 -> new Identifier(string, string2));
		}
	}
}
