package net.minecraft;

import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public enum class_1157 {
	field_5650("movement", class_1151::new),
	field_5648("find_tree", class_1152::new),
	field_5649("punch_tree", class_1153::new),
	field_5652("open_inventory", class_1154::new),
	field_5655("craft_planks", class_1149::new),
	field_5653("none", class_1150::new);

	private final String field_5651;
	private final Function<class_1156, ? extends class_1155> field_5647;

	private <T extends class_1155> class_1157(String string2, Function<class_1156, T> function) {
		this.field_5651 = string2;
		this.field_5647 = function;
	}

	public class_1155 method_4918(class_1156 arg) {
		return (class_1155)this.field_5647.apply(arg);
	}

	public String method_4920() {
		return this.field_5651;
	}

	public static class_1157 method_4919(String string) {
		for (class_1157 lv : values()) {
			if (lv.field_5651.equals(string)) {
				return lv;
			}
		}

		return field_5653;
	}
}
