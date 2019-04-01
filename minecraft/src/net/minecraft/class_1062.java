package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import java.io.IOException;
import java.util.concurrent.Executor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface class_1062 {
	void method_4626(boolean bl, boolean bl2);

	void method_4627();

	void method_4625(class_3300 arg) throws IOException;

	int method_4624();

	default void method_4623() {
		GlStateManager.bindTexture(this.method_4624());
	}

	default void method_18169(class_1060 arg, class_3300 arg2, class_2960 arg3, Executor executor) {
		arg.method_4616(arg3, this);
	}
}
