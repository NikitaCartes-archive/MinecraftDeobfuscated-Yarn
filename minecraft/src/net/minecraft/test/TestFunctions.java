package net.minecraft.test;

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

public class TestFunctions {
	private static final Collection<TestFunction> TEST_FUNCTIONS = Lists.<TestFunction>newArrayList();
	private static final Set<String> testClasses = Sets.<String>newHashSet();
	private static final Map<String, Consumer<ServerWorld>> WORLD_SETTERS = Maps.<String, Consumer<ServerWorld>>newHashMap();

	public static Collection<TestFunction> getTestFunctions(String string) {
		return (Collection<TestFunction>)TEST_FUNCTIONS.stream().filter(testFunction -> isInClass(testFunction, string)).collect(Collectors.toList());
	}

	public static Collection<TestFunction> getTestFunctions() {
		return TEST_FUNCTIONS;
	}

	public static Collection<String> getTestClasses() {
		return testClasses;
	}

	public static boolean testClassExists(String string) {
		return testClasses.contains(string);
	}

	@Nullable
	public static Consumer<ServerWorld> getWorldSetter(String string) {
		return (Consumer<ServerWorld>)WORLD_SETTERS.get(string);
	}

	public static Optional<TestFunction> getTestFunction(String string) {
		return getTestFunctions().stream().filter(testFunction -> testFunction.getStructurePath().equalsIgnoreCase(string)).findFirst();
	}

	public static TestFunction getTestFunctionOrThrow(String string) {
		Optional<TestFunction> optional = getTestFunction(string);
		if (!optional.isPresent()) {
			throw new IllegalArgumentException("Can't find the test function for " + string);
		} else {
			return (TestFunction)optional.get();
		}
	}

	private static boolean isInClass(TestFunction testFunction, String string) {
		return testFunction.getStructurePath().toLowerCase().startsWith(string.toLowerCase() + ".");
	}
}
