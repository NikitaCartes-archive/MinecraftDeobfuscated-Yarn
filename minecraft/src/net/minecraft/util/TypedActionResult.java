package net.minecraft.util;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class TypedActionResult<T> {
	private final ActionResult result;
	private final T value;
	private final boolean field_20683;

	public TypedActionResult(ActionResult actionResult, T object, boolean bl) {
		this.result = actionResult;
		this.value = object;
		this.field_20683 = bl;
	}

	public ActionResult getResult() {
		return this.result;
	}

	public T getValue() {
		return this.value;
	}

	@Environment(EnvType.CLIENT)
	public boolean method_22429() {
		return this.field_20683;
	}

	public static <T> TypedActionResult<T> method_22427(T object) {
		return new TypedActionResult<>(ActionResult.SUCCESS, object, true);
	}

	public static <T> TypedActionResult<T> method_22428(T object) {
		return new TypedActionResult<>(ActionResult.SUCCESS, object, false);
	}

	public static <T> TypedActionResult<T> method_22430(@Nullable T object) {
		return new TypedActionResult<>(ActionResult.PASS, object, false);
	}

	public static <T> TypedActionResult<T> method_22431(@Nullable T object) {
		return new TypedActionResult<>(ActionResult.FAIL, object, false);
	}
}
