package net.minecraft;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public interface class_3262 extends Closeable {
	@Environment(EnvType.CLIENT)
	InputStream method_14410(String string) throws IOException;

	InputStream method_14405(class_3264 arg, class_2960 arg2) throws IOException;

	Collection<class_2960> method_14408(class_3264 arg, String string, int i, Predicate<String> predicate);

	boolean method_14411(class_3264 arg, class_2960 arg2);

	Set<String> method_14406(class_3264 arg);

	@Nullable
	<T> T method_14407(class_3270<T> arg) throws IOException;

	String method_14409();
}
