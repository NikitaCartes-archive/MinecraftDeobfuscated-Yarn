package net.minecraft.util;

public class TypedActionResult<T> {
	private final ActionResult result;
	private final T value;

	public TypedActionResult(ActionResult actionResult, T object) {
		this.result = actionResult;
		this.value = object;
	}

	public ActionResult getResult() {
		return this.result;
	}

	public T getValue() {
		return this.value;
	}

	public static <T> TypedActionResult<T> successWithSwing(T object) {
		return new TypedActionResult<>(ActionResult.SUCCESS, object);
	}

	public static <T> TypedActionResult<T> successWithoutSwing(T object) {
		return new TypedActionResult<>(ActionResult.CONSUME, object);
	}

	public static <T> TypedActionResult<T> pass(T object) {
		return new TypedActionResult<>(ActionResult.PASS, object);
	}

	public static <T> TypedActionResult<T> fail(T object) {
		return new TypedActionResult<>(ActionResult.FAIL, object);
	}
}
