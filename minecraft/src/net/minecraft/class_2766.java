package net.minecraft;

public enum class_2766 implements class_3542 {
	field_12648("harp", class_3417.field_15114),
	field_12653("basedrum", class_3417.field_15047),
	field_12643("snare", class_3417.field_14708),
	field_12645("hat", class_3417.field_15204),
	field_12651("bass", class_3417.field_14624),
	field_12650("flute", class_3417.field_14989),
	field_12644("bell", class_3417.field_14793),
	field_12654("guitar", class_3417.field_14903),
	field_12647("chime", class_3417.field_14725),
	field_12655("xylophone", class_3417.field_14776);

	private final String field_12646;
	private final class_3414 field_12649;

	private class_2766(String string2, class_3414 arg) {
		this.field_12646 = string2;
		this.field_12649 = arg;
	}

	@Override
	public String method_15434() {
		return this.field_12646;
	}

	public class_3414 method_11886() {
		return this.field_12649;
	}

	public static class_2766 method_11887(class_2680 arg) {
		class_2248 lv = arg.method_11614();
		if (lv == class_2246.field_10460) {
			return field_12650;
		} else if (lv == class_2246.field_10205) {
			return field_12644;
		} else if (lv.method_9525(class_3481.field_15481)) {
			return field_12654;
		} else if (lv == class_2246.field_10225) {
			return field_12647;
		} else if (lv == class_2246.field_10166) {
			return field_12655;
		} else {
			class_3614 lv2 = arg.method_11620();
			if (lv2 == class_3614.field_15914) {
				return field_12653;
			} else if (lv2 == class_3614.field_15916) {
				return field_12643;
			} else if (lv2 == class_3614.field_15942) {
				return field_12645;
			} else {
				return lv2 == class_3614.field_15932 ? field_12651 : field_12648;
			}
		}
	}
}
