package net.minecraft.test;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.BlockRotation;

public class TestFunctions {
	private static final Collection<TestFunction> TEST_FUNCTIONS = Lists.<TestFunction>newArrayList();
	private static final Set<String> TEST_CLASSES = Sets.<String>newHashSet();
	private static final Map<String, Consumer<ServerWorld>> BEFORE_BATCH_CONSUMERS = Maps.<String, Consumer<ServerWorld>>newHashMap();
	private static final Map<String, Consumer<ServerWorld>> AFTER_BATCH_CONSUMERS = Maps.<String, Consumer<ServerWorld>>newHashMap();
	private static final Set<TestFunction> FAILED_TEST_FUNCTIONS = Sets.<TestFunction>newHashSet();

	public static void register(Class<?> testClass) {
		Arrays.stream(testClass.getDeclaredMethods()).sorted(Comparator.comparing(Method::getName)).forEach(TestFunctions::register);
	}

	public static void register(Method method) {
		String string = method.getDeclaringClass().getSimpleName();
		GameTest gameTest = (GameTest)method.getAnnotation(GameTest.class);
		if (gameTest != null) {
			TEST_FUNCTIONS.add(getTestFunction(method));
			TEST_CLASSES.add(string);
		}

		CustomTestProvider customTestProvider = (CustomTestProvider)method.getAnnotation(CustomTestProvider.class);
		if (customTestProvider != null) {
			TEST_FUNCTIONS.addAll(getCustomTestFunctions(method));
			TEST_CLASSES.add(string);
		}

		registerBatchConsumers(method, BeforeBatch.class, BeforeBatch::batchId, BEFORE_BATCH_CONSUMERS);
		registerBatchConsumers(method, AfterBatch.class, AfterBatch::batchId, AFTER_BATCH_CONSUMERS);
	}

	private static <T extends Annotation> void registerBatchConsumers(
		Method method, Class<T> clazz, Function<T, String> batchIdFunction, Map<String, Consumer<ServerWorld>> batchConsumerMap
	) {
		T annotation = (T)method.getAnnotation(clazz);
		if (annotation != null) {
			String string = (String)batchIdFunction.apply(annotation);
			Consumer<ServerWorld> consumer = (Consumer<ServerWorld>)batchConsumerMap.putIfAbsent(string, getInvoker(method));
			if (consumer != null) {
				throw new RuntimeException("Hey, there should only be one " + clazz + " method per batch. Batch '" + string + "' has more than one!");
			}
		}
	}

	public static Stream<TestFunction> getTestFunctions(String testClass) {
		return TEST_FUNCTIONS.stream().filter(testFunction -> isInClass(testFunction, testClass));
	}

	public static Collection<TestFunction> getTestFunctions() {
		return TEST_FUNCTIONS;
	}

	public static Collection<String> getTestClasses() {
		return TEST_CLASSES;
	}

	public static boolean testClassExists(String testClass) {
		return TEST_CLASSES.contains(testClass);
	}

	public static Consumer<ServerWorld> getBeforeBatchConsumer(String batchId) {
		return (Consumer<ServerWorld>)BEFORE_BATCH_CONSUMERS.getOrDefault(batchId, (Consumer)world -> {
		});
	}

	public static Consumer<ServerWorld> getAfterBatchConsumer(String batchId) {
		return (Consumer<ServerWorld>)AFTER_BATCH_CONSUMERS.getOrDefault(batchId, (Consumer)world -> {
		});
	}

	public static Optional<TestFunction> getTestFunction(String structurePath) {
		return getTestFunctions().stream().filter(testFunction -> testFunction.templatePath().equalsIgnoreCase(structurePath)).findFirst();
	}

	public static TestFunction getTestFunctionOrThrow(String structurePath) {
		Optional<TestFunction> optional = getTestFunction(structurePath);
		if (optional.isEmpty()) {
			throw new IllegalArgumentException("Can't find the test function for " + structurePath);
		} else {
			return (TestFunction)optional.get();
		}
	}

	private static Collection<TestFunction> getCustomTestFunctions(Method method) {
		try {
			Object object = method.getDeclaringClass().newInstance();
			return (Collection<TestFunction>)method.invoke(object);
		} catch (ReflectiveOperationException var2) {
			throw new RuntimeException(var2);
		}
	}

	private static TestFunction getTestFunction(Method method) {
		GameTest gameTest = (GameTest)method.getAnnotation(GameTest.class);
		String string = method.getDeclaringClass().getSimpleName();
		String string2 = string.toLowerCase();
		String string3 = string2 + "." + method.getName().toLowerCase();
		String string4 = gameTest.templateName().isEmpty() ? string3 : string2 + "." + gameTest.templateName();
		String string5 = gameTest.batchId();
		BlockRotation blockRotation = StructureTestUtil.getRotation(gameTest.rotation());
		return new TestFunction(
			string5,
			string3,
			string4,
			blockRotation,
			gameTest.tickLimit(),
			gameTest.duration(),
			gameTest.required(),
			gameTest.manualOnly(),
			gameTest.requiredSuccesses(),
			gameTest.maxAttempts(),
			gameTest.skyAccess(),
			(Consumer<TestContext>)getInvoker(method)
		);
	}

	private static Consumer<?> getInvoker(Method method) {
		return args -> {
			try {
				Object object = method.getDeclaringClass().newInstance();
				method.invoke(object, args);
			} catch (InvocationTargetException var3) {
				if (var3.getCause() instanceof RuntimeException) {
					throw (RuntimeException)var3.getCause();
				} else {
					throw new RuntimeException(var3.getCause());
				}
			} catch (ReflectiveOperationException var4) {
				throw new RuntimeException(var4);
			}
		};
	}

	private static boolean isInClass(TestFunction testFunction, String testClass) {
		return testFunction.templatePath().toLowerCase().startsWith(testClass.toLowerCase() + ".");
	}

	public static Stream<TestFunction> getFailedTestFunctions() {
		return FAILED_TEST_FUNCTIONS.stream();
	}

	public static void addFailedTestFunction(TestFunction testFunction) {
		FAILED_TEST_FUNCTIONS.add(testFunction);
	}

	public static void clearFailedTestFunctions() {
		FAILED_TEST_FUNCTIONS.clear();
	}
}
