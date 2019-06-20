package net.minecraft;

import java.util.List;
import java.util.ListIterator;
import java.util.Optional;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface class_4069 extends class_364 {
	List<? extends class_364> children();

	default Optional<class_364> method_19355(double d, double e) {
		for (class_364 lv : this.children()) {
			if (lv.isMouseOver(d, e)) {
				return Optional.of(lv);
			}
		}

		return Optional.empty();
	}

	@Override
	default boolean mouseClicked(double d, double e, int i) {
		for (class_364 lv : this.children()) {
			if (lv.mouseClicked(d, e, i)) {
				this.setFocused(lv);
				if (i == 0) {
					this.setDragging(true);
				}

				return true;
			}
		}

		return false;
	}

	@Override
	default boolean mouseReleased(double d, double e, int i) {
		this.setDragging(false);
		return this.method_19355(d, e).filter(arg -> arg.mouseReleased(d, e, i)).isPresent();
	}

	@Override
	default boolean mouseDragged(double d, double e, int i, double f, double g) {
		return this.getFocused() != null && this.isDragging() && i == 0 ? this.getFocused().mouseDragged(d, e, i, f, g) : false;
	}

	boolean isDragging();

	void setDragging(boolean bl);

	@Override
	default boolean mouseScrolled(double d, double e, double f) {
		return this.method_19355(d, e).filter(arg -> arg.mouseScrolled(d, e, f)).isPresent();
	}

	@Override
	default boolean keyPressed(int i, int j, int k) {
		return this.getFocused() != null && this.getFocused().keyPressed(i, j, k);
	}

	@Override
	default boolean method_16803(int i, int j, int k) {
		return this.getFocused() != null && this.getFocused().method_16803(i, j, k);
	}

	@Override
	default boolean charTyped(char c, int i) {
		return this.getFocused() != null && this.getFocused().charTyped(c, i);
	}

	@Nullable
	class_364 getFocused();

	void setFocused(@Nullable class_364 arg);

	default void method_20085(@Nullable class_364 arg) {
		this.setFocused(arg);
	}

	default void method_20086(@Nullable class_364 arg) {
		this.setFocused(arg);
	}

	@Override
	default boolean changeFocus(boolean bl) {
		class_364 lv = this.getFocused();
		boolean bl2 = lv != null;
		if (bl2 && lv.changeFocus(bl)) {
			return true;
		} else {
			List<? extends class_364> list = this.children();
			int i = list.indexOf(lv);
			int j;
			if (bl2 && i >= 0) {
				j = i + (bl ? 1 : 0);
			} else if (bl) {
				j = 0;
			} else {
				j = list.size();
			}

			ListIterator<? extends class_364> listIterator = list.listIterator(j);
			BooleanSupplier booleanSupplier = bl ? listIterator::hasNext : listIterator::hasPrevious;
			Supplier<? extends class_364> supplier = bl ? listIterator::next : listIterator::previous;

			while (booleanSupplier.getAsBoolean()) {
				class_364 lv2 = (class_364)supplier.get();
				if (lv2.changeFocus(bl)) {
					this.setFocused(lv2);
					return true;
				}
			}

			this.setFocused(null);
			return false;
		}
	}
}
