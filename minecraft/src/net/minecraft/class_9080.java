package net.minecraft;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;

@Environment(EnvType.CLIENT)
public class class_9080 {
	public static final float field_47848 = 200.0F;
	private final List<class_9080.class_9081> field_47849 = new ArrayList();

	public class_9080 method_55810(class_9080.class_9081 arg) {
		this.field_47849.add(arg);
		return this;
	}

	public class_9080 method_55811(class_9080 arg, BooleanSupplier booleanSupplier) {
		return this.method_55810((drawContext, f) -> {
			if (booleanSupplier.getAsBoolean()) {
				arg.method_55813(drawContext, f);
			}
		});
	}

	public void method_55809(DrawContext drawContext, float f) {
		drawContext.getMatrices().push();
		this.method_55813(drawContext, f);
		drawContext.getMatrices().pop();
	}

	private void method_55813(DrawContext drawContext, float f) {
		for (class_9080.class_9081 lv : this.field_47849) {
			lv.render(drawContext, f);
			drawContext.getMatrices().translate(0.0F, 0.0F, 200.0F);
		}
	}

	@Environment(EnvType.CLIENT)
	public interface class_9081 {
		void render(DrawContext drawContext, float f);
	}
}
