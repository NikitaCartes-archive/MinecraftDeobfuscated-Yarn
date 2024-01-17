package net.minecraft.test;

import java.util.stream.Stream;

@FunctionalInterface
public interface TestFunctionFinder {
	Stream<TestFunction> findTestFunctions();
}
