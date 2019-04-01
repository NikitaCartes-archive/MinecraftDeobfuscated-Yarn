package net.minecraft;

import java.io.Closeable;
import java.io.InputStream;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public interface class_3298 extends Closeable {
	@Environment(EnvType.CLIENT)
	class_2960 method_14483();

	InputStream method_14482();

	@Nullable
	@Environment(EnvType.CLIENT)
	<T> T method_14481(class_3270<T> arg);

	String method_14480();
}
