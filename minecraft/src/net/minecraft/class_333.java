package net.minecraft;

import com.mojang.text2speech.Narrator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_333 implements class_334 {
	public static final class_333 field_2054 = new class_333();
	private final Narrator field_2055 = Narrator.getNarrator();

	@Override
	public void method_1794(class_2556 arg, class_2561 arg2) {
		int i = class_310.method_1551().field_1690.field_1896;
		if (i != 0 && this.field_2055.active()) {
			if (i == 1 || i == 2 && arg == class_2556.field_11737 || i == 3 && arg == class_2556.field_11735) {
				if (arg2 instanceof class_2588 && "chat.type.text".equals(((class_2588)arg2).method_11022())) {
					this.field_2055.say(new class_2588("chat.type.text.narrate", ((class_2588)arg2).method_11023()).getString());
				} else {
					this.field_2055.say(arg2.getString());
				}
			}
		}
	}

	public void method_1792(int i) {
		this.field_2055.clear();
		this.field_2055.say(new class_2588("options.narrator").getString() + " : " + new class_2588(class_315.field_1898[i]).getString());
		class_374 lv = class_310.method_1551().method_1566();
		if (this.field_2055.active()) {
			if (i == 0) {
				class_370.method_1990(lv, class_370.class_371.field_2219, new class_2588("narrator.toast.disabled"), null);
			} else {
				class_370.method_1990(lv, class_370.class_371.field_2219, new class_2588("narrator.toast.enabled"), new class_2588(class_315.field_1898[i]));
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
