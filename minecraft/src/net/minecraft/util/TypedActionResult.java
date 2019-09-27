package net.minecraft.util;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class TypedActionResult<T> {
	private final ActionResult result;
	private final T value;
	private final boolean swingArm;

	public TypedActionResult(ActionResult actionResult, T object, boolean bl) {
		this.result = actionResult;
		this.value = object;
		this.swingArm = bl;
	}

	public ActionResult getResult() {
		return this.result;
	}

	public T getValue() {
		return this.value;
	}

	@Environment(EnvType.CLIENT)
	public boolean shouldSwingArm() {
		return this.swingArm;
	}

	public static <T> TypedActionResult<T> successWithSwing(T object) {
		return new TypedActionResult<>(ActionResult.SUCCESS, object, true);
	}

	public static <T> TypedActionResult<T> successWithoutSwing(T object) {
		return new TypedActionResult<>(ActionResult.SUCCESS, object, false);
	}

	public static <T> TypedActionResult<T> pass(@Nullable T object) {
		return new TypedActionResult<>(ActionResult.PASS, object, false);
	}

	public static <T> TypedActionResult<T> fail(@Nullable T object) {
		return new TypedActionResult<>(ActionResult.FAIL, object, false);
	}
}
