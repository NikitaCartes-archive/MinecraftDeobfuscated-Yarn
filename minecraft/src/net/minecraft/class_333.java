package net.minecraft;

import com.mojang.text2speech.Narrator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class class_333 implements class_334 {
	public static final class_2561 field_18967 = new class_2585("");
	private static final Logger field_18210 = LogManager.getLogger();
	public static final class_333 field_2054 = new class_333();
	private final Narrator field_2055 = Narrator.getNarrator();

	@Override
	public void method_1794(class_2556 arg, class_2561 arg2) {
		class_4065 lv = class_310.method_1551().field_1690.field_1896;
		if (lv != class_4065.field_18176 && this.field_2055.active()) {
			if (lv == class_4065.field_18177
				|| lv == class_4065.field_18178 && arg == class_2556.field_11737
				|| lv == class_4065.field_18179 && arg == class_2556.field_11735) {
				class_2561 lv2;
				if (arg2 instanceof class_2588 && "chat.type.text".equals(((class_2588)arg2).method_11022())) {
					lv2 = new class_2588("chat.type.text.narrate", ((class_2588)arg2).method_11023());
				} else {
					lv2 = arg2;
				}

				this.method_18621(arg.method_19457(), lv2.getString());
			}
		}
	}

	public void method_19788(String string) {
		class_4065 lv = class_310.method_1551().field_1690.field_1896;
		if (this.field_2055.active() && lv != class_4065.field_18176 && lv != class_4065.field_18178 && !string.isEmpty()) {
			this.field_2055.clear();
			this.method_18621(true, string);
		}
	}

	private void method_18621(boolean bl, String string) {
		if (class_155.field_1125) {
			field_18210.debug("Narrating: {}", string);
		}

		this.field_2055.say(string, bl);
	}

	public void method_1792(class_4065 arg) {
		this.field_2055.clear();
		this.field_2055.say(new class_2588("options.narrator").getString() + " : " + new class_2588(arg.method_18511()).getString(), true);
		class_374 lv = class_310.method_1551().method_1566();
		if (this.field_2055.active()) {
			if (arg == class_4065.field_18176) {
				class_370.method_1990(lv, class_370.class_371.field_2219, new class_2588("narrator.toast.disabled"), null);
			} else {
				class_370.method_1990(lv, class_370.class_371.field_2219, new class_2588("narrator.toast.enabled"), new class_2588(arg.method_18511()));
			}
		} else {
			class_370.method_1990(lv, class_370.class_371.field_2219, new class_2588("narrator.toast.disabled"), new class_2588("options.narrator.notavailable"));
		}
	}

	public boolean method_1791() {
		return this.field_2055.active();
	}

	public void method_1793() {
		this.field_2055.clear();
	}
}
