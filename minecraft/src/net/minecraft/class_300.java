package net.minecraft;

import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_300 {
	private final class_634 field_1640;
	private int field_1641 = -1;
	@Nullable
	private Consumer<class_2487> field_1642;

	public class_300(class_634 arg) {
		this.field_1640 = arg;
	}

	public boolean method_1404(int i, @Nullable class_2487 arg) {
		if (this.field_1641 == i && this.field_1642 != null) {
			this.field_1642.accept(arg);
			this.field_1642 = null;
			return true;
		} else {
			return false;
		}
	}

	private int method_1402(Consumer<class_2487> consumer) {
		this.field_1642 = consumer;
		return ++this.field_1641;
	}

	public void method_1405(int i, Consumer<class_2487> consumer) {
		int j = this.method_1402(consumer);
		this.field_1640.method_2883(new class_2822(j, i));
	}

	public void method_1403(class_2338 arg, Consumer<class_2487> consumer) {
		int i = this.method_1402(consumer);
		this.field_1640.method_2883(new class_2795(i, arg));
	}
}
