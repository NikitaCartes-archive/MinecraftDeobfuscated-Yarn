package net.minecraft;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_763 {
	public final Int2ObjectMap<class_1091> field_4129 = new Int2ObjectOpenHashMap<>(256);
	private final Int2ObjectMap<class_1087> field_4130 = new Int2ObjectOpenHashMap<>(256);
	private final class_1092 field_4128;

	public class_763(class_1092 arg) {
		this.field_4128 = arg;
	}

	public class_1058 method_3307(class_1935 arg) {
		return this.method_3305(new class_1799(arg));
	}

	public class_1058 method_3305(class_1799 arg) {
		class_1087 lv = this.method_3308(arg);
		return (lv == this.field_4128.method_4744() || lv.method_4713()) && arg.method_7909() instanceof class_1747
			? this.field_4128.method_4743().method_3339(((class_1747)arg.method_7909()).method_7711().method_9564())
			: lv.method_4711();
	}

	public class_1087 method_3308(class_1799 arg) {
		class_1087 lv = this.method_3304(arg.method_7909());
		return lv == null ? this.field_4128.method_4744() : lv;
	}

	@Nullable
	public class_1087 method_3304(class_1792 arg) {
		return this.field_4130.get(method_3306(arg));
	}

	private static int method_3306(class_1792 arg) {
		return class_1792.method_7880(arg);
	}

	public void method_3309(class_1792 arg, class_1091 arg2) {
		this.field_4129.put(method_3306(arg), arg2);
		this.field_4130.put(method_3306(arg), this.field_4128.method_4742(arg2));
	}

	public class_1092 method_3303() {
		return this.field_4128;
	}

	public void method_3310() {
		this.field_4130.clear();

		for (Entry<Integer, class_1091> entry : this.field_4129.entrySet()) {
			this.field_4130.put((Integer)entry.getKey(), this.field_4128.method_4742((class_1091)entry.getValue()));
		}
	}
}
