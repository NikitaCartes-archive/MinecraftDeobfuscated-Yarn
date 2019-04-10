package net.minecraft.client.gui;

import java.util.List;
import java.util.ListIterator;
import java.util.Optional;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface ParentElement extends Element {
	List<? extends Element> children();

	default Optional<Element> method_19355(double d, double e) {
		for (Element element : this.children()) {
			if (element.isMouseOver(d, e)) {
				return Optional.of(element);
			}
		}

		return Optional.empty();
	}

	@Override
	default boolean mouseClicked(double d, double e, int i) {
		for (Element element : this.children()) {
			if (element.mouseClicked(d, e, i)) {
				this.setFocused(element);
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
		return this.method_19355(d, e).filter(element -> element.mouseReleased(d, e, i)).isPresent();
	}

	@Override
	default boolean mouseDragged(double d, double e, int i, double f, double g) {
		return this.getFocused() != null && this.isDragging() && i == 0 ? this.getFocused().mouseDragged(d, e, i, f, g) : false;
	}

	boolean isDragging();

	void setDragging(boolean bl);

	@Override
	default boolean mouseScrolled(double d, double e, double f) {
		return this.method_19355(d, e).filter(element -> element.mouseScrolled(d, e, f)).isPresent();
	}

	@Override
	default boolean keyPressed(int i, int j, int k) {
		return this.getFocused() != null && this.getFocused().keyPressed(i, j, k);
	}

	@Override
	default boolean keyReleased(int i, int j, int k) {
		return this.getFocused() != null && this.getFocused().keyReleased(i, j, k);
	}

	@Override
	default boolean charTyped(char c, int i) {
		return this.getFocused() != null && this.getFocused().charTyped(c, i);
	}

	@Nullable
	Element getFocused();

	void setFocused(@Nullable Element element);

	default void method_20085(@Nullable Element element) {
		this.setFocused(element);
	}

	default void method_20086(@Nullable Element element) {
		this.setFocused(element);
	}

	@Override
	default boolean changeFocus(boolean bl) {
		Element element = this.getFocused();
		boolean bl2 = element != null;
		if (bl2 && element.changeFocus(bl)) {
			return true;
		} else {
			List<? extends Element> list = this.children();
			int i = list.indexOf(element);
			int j;
			if (bl2 && i >= 0) {
				j = i + (bl ? 1 : 0);
			} else if (bl) {
				j = 0;
			} else {
				j = list.size();
			}

			ListIterator<? extends Element> listIterator = list.listIterator(j);
			BooleanSupplier booleanSupplier = bl ? listIterator::hasNext : listIterator::hasPrevious;
			Supplier<? extends Element> supplier = bl ? listIterator::next : listIterator::previous;

			while (booleanSupplier.getAsBoolean()) {
				Element element2 = (Element)supplier.get();
				if (element2.changeFocus(bl)) {
					this.setFocused(element2);
					return true;
				}
			}

			this.setFocused(null);
			return false;
		}
	}
}
