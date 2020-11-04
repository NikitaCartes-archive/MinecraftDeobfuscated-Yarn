package net.minecraft;

import java.util.UUID;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.util.math.Box;

public interface class_5577<T extends class_5568> {
	@Nullable
	T method_31804(int i);

	@Nullable
	T method_31808(UUID uUID);

	Iterable<T> method_31803();

	<U extends T> void method_31806(class_5575<T, U> arg, Consumer<U> consumer);

	void method_31807(Box box, Consumer<T> consumer);

	<U extends T> void method_31805(class_5575<T, U> arg, Box box, Consumer<U> consumer);
}
