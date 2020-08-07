package net.minecraft.client.options;

import java.util.function.BooleanSupplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.InputUtil;

@Environment(EnvType.CLIENT)
public class StickyKeyBinding extends KeyBinding {
	private final BooleanSupplier toggleGetter;

	public StickyKeyBinding(String id, int code, String category, BooleanSupplier toggleGetter) {
		super(id, InputUtil.Type.field_1668, code, category);
		this.toggleGetter = toggleGetter;
	}

	@Override
	public void setPressed(boolean pressed) {
		if (this.toggleGetter.getAsBoolean()) {
			if (pressed) {
				super.setPressed(!this.isPressed());
			}
		} else {
			super.setPressed(pressed);
		}
	}
}
