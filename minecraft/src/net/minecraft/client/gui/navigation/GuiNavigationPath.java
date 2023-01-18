package net.minecraft.client.gui.navigation;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.ParentElement;

/**
 * Represents a path of currently navigating elements from the root element
 * to the leaf.
 */
@Environment(EnvType.CLIENT)
public interface GuiNavigationPath {
	static GuiNavigationPath of(Element leaf) {
		return new GuiNavigationPath.Leaf(leaf);
	}

	@Nullable
	static GuiNavigationPath of(ParentElement element, @Nullable GuiNavigationPath childPath) {
		return childPath == null ? null : new GuiNavigationPath.IntermediaryNode(element, childPath);
	}

	static GuiNavigationPath of(Element leaf, ParentElement... elements) {
		GuiNavigationPath guiNavigationPath = of(leaf);

		for (ParentElement parentElement : elements) {
			guiNavigationPath = of(parentElement, guiNavigationPath);
		}

		return guiNavigationPath;
	}

	Element component();

	/**
	 * Sets whether the path is focused. This propagates to children.
	 */
	void setFocused(boolean focused);

	@Environment(EnvType.CLIENT)
	public static record IntermediaryNode(ParentElement component, GuiNavigationPath childPath) implements GuiNavigationPath {
		@Override
		public void setFocused(boolean focused) {
			if (!focused) {
				this.component.setFocused(null);
			} else {
				this.component.setFocused(this.childPath.component());
			}

			this.childPath.setFocused(focused);
		}
	}

	@Environment(EnvType.CLIENT)
	public static record Leaf(Element component) implements GuiNavigationPath {
		@Override
		public void setFocused(boolean focused) {
			this.component.setFocused(focused);
		}
	}
}
