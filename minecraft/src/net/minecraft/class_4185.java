package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_4185 extends class_4264 {
	protected final class_4185.class_4241 onPress;

	public class_4185(int i, int j, int k, int l, String string, class_4185.class_4241 arg) {
		super(i, j, k, l, string);
		this.onPress = arg;
	}

	@Override
	public void onPress() {
		this.onPress.onPress(this);
	}

	@Environment(EnvType.CLIENT)
	public interface class_4241 {
		void onPress(class_4185 arg);
	}
}
