package net.minecraft;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.test.TestFunction;

public class class_4519 {
	private static final Collection<TestFunction> field_20570 = Lists.<TestFunction>newArrayList();
	private static final Set<String> field_20571 = Sets.<String>newHashSet();
	private static final Map<String, Consumer<ServerWorld>> field_20572 = Maps.<String, Consumer<ServerWorld>>newHashMap();

	public static Collection<TestFunction> method_22193(String string) {
		return (Collection<TestFunction>)field_20570.stream().filter(testFunction -> method_22192(testFunction, string)).collect(Collectors.toList());
	}

	public static Collection<TestFunction> method_22191() {
		return field_20570;
	}

	public static Collection<String> method_22195() {
		return field_20571;
	}

	public static boolean method_22196(String string) {
		return field_20571.contains(string);
	}

	@Nullable
	public static Consumer<ServerWorld> method_22198(String string) {
		return (Consumer<ServerWorld>)field_20572.get(string);
	}

	public static Optional<TestFunction> method_22199(String string) {
		return method_22191().stream().filter(testFunction -> testFunction.method_22296().equalsIgnoreCase(string)).findFirst();
	}

	public static TestFunction method_22200(String string) {
		Optional<TestFunction> optional = method_22199(string);
		if (!optional.isPresent()) {
			throw new IllegalArgumentException("Can't find the test function for " + string);
		} else {
			return (TestFunction)optional.get();
		}
	}

	private static boolean method_22192(TestFunction testFunction, String string) {
		return testFunction.method_22296().toLowerCase().startsWith(string.toLowerCase() + ".");
	}
}
