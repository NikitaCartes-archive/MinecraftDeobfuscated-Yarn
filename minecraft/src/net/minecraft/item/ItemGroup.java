package net.minecraft.item;

import java.util.Collection;
import javax.annotation.Nullable;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.text.Text;

/**
 * A group of items that the items belong to. This is used by the creative inventory.
 */
public abstract class ItemGroup {
	private final int index;
	private final Text displayName;
	private String texture = "items.png";
	private boolean scrollbar = true;
	private boolean renderName = true;
	private ItemStack icon;
	@Nullable
	private ItemStackSet displayStacks;
	@Nullable
	private ItemStackSet searchTabStacks;

	public ItemGroup(int index, Text displayName) {
		this.index = index;
		this.displayName = displayName;
		this.icon = ItemStack.EMPTY;
	}

	public int getIndex() {
		return this.index;
	}

	public Text getDisplayName() {
		return this.displayName;
	}

	public ItemStack getIcon() {
		if (this.icon.isEmpty()) {
			this.icon = this.createIcon();
		}

		return this.icon;
	}

	public abstract ItemStack createIcon();

	protected abstract void addItems(FeatureSet enabledFeatures, ItemGroup.Entries entries);

	public String getTexture() {
		return this.texture;
	}

	public ItemGroup setTexture(String texture) {
		this.texture = texture;
		return this;
	}

	/**
	 * Checks if this item group should render its name.
	 * 
	 * <p>The name is rendered below the top row of item groups and above the inventory.
	 */
	public boolean shouldRenderName() {
		return this.renderName;
	}

	/**
	 * Specifies that when this item group is selected, the name of the item group should not be rendered.
	 */
	public ItemGroup hideName() {
		this.renderName = false;
		return this;
	}

	public boolean hasScrollbar() {
		return this.scrollbar;
	}

	public ItemGroup setNoScrollbar() {
		this.scrollbar = false;
		return this;
	}

	public int getColumn() {
		return this.index % 6;
	}

	public boolean isTopRow() {
		return this.index < 6;
	}

	public boolean isSpecial() {
		return this.getColumn() == 5;
	}

	private ItemStackSet getStacks(FeatureSet enabledFeatures, boolean search) {
		if (this.displayStacks == null || this.searchTabStacks == null) {
			ItemGroup.EntriesImpl entriesImpl = new ItemGroup.EntriesImpl(this, enabledFeatures);
			this.addItems(enabledFeatures, entriesImpl);
			this.displayStacks = entriesImpl.getParentTabStacks();
			this.searchTabStacks = entriesImpl.getSearchTabStacks();
		}

		return search ? this.searchTabStacks : this.displayStacks;
	}

	public ItemStackSet getDisplayStacks(FeatureSet enabledFeatures) {
		return this.getStacks(enabledFeatures, false);
	}

	public ItemStackSet getSearchTabStacks(FeatureSet enabledFeatures) {
		return this.getStacks(enabledFeatures, true);
	}

	public boolean contains(FeatureSet enabledFeatures, ItemStack stack) {
		return this.getSearchTabStacks(enabledFeatures).contains(stack);
	}

	public void clearStacks() {
		this.displayStacks = null;
		this.searchTabStacks = null;
	}

	protected interface Entries {
		void add(ItemStack stack, ItemGroup.StackVisibility visibility);

		default void add(ItemStack stack) {
			this.add(stack, ItemGroup.StackVisibility.PARENT_AND_SEARCH_TABS);
		}

		default void add(ItemConvertible item, ItemGroup.StackVisibility visibility) {
			this.add(new ItemStack(item), visibility);
		}

		default void add(ItemConvertible item) {
			this.add(new ItemStack(item), ItemGroup.StackVisibility.PARENT_AND_SEARCH_TABS);
		}

		default void addAll(Collection<ItemStack> stacks, ItemGroup.StackVisibility visibility) {
			stacks.forEach(stack -> this.add(stack, visibility));
		}

		default void addAll(Collection<ItemStack> stacks) {
			this.addAll(stacks, ItemGroup.StackVisibility.PARENT_AND_SEARCH_TABS);
		}
	}

	static class EntriesImpl implements ItemGroup.Entries {
		private final ItemStackSet parentTabStacks = new ItemStackSet();
		private final ItemStackSet searchTabStacks = new ItemStackSet();
		private final ItemGroup group;
		private final FeatureSet enabledFeatures;

		public EntriesImpl(ItemGroup group, FeatureSet enabledFeatures) {
			this.group = group;
			this.enabledFeatures = enabledFeatures;
		}

		@Override
		public void add(ItemStack stack, ItemGroup.StackVisibility visibility) {
			boolean bl = this.parentTabStacks.contains(stack) && visibility != ItemGroup.StackVisibility.SEARCH_TAB_ONLY;
			if (bl) {
				throw new IllegalStateException(
					"Accidentally adding the same item stack twice "
						+ stack.toHoverableText().getString()
						+ " to a Creative Mode Tab: "
						+ this.group.getDisplayName().getString()
				);
			} else {
				if (stack.getItem().isEnabled(this.enabledFeatures)) {
					switch (visibility) {
						case PARENT_AND_SEARCH_TABS:
							this.parentTabStacks.add(stack);
							this.searchTabStacks.add(stack);
							break;
						case PARENT_TAB_ONLY:
							this.parentTabStacks.add(stack);
							break;
						case SEARCH_TAB_ONLY:
							this.searchTabStacks.add(stack);
					}
				}
			}
		}

		public ItemStackSet getParentTabStacks() {
			return this.parentTabStacks;
		}

		public ItemStackSet getSearchTabStacks() {
			return this.searchTabStacks;
		}
	}

	protected static enum StackVisibility {
		PARENT_AND_SEARCH_TABS,
		PARENT_TAB_ONLY,
		SEARCH_TAB_ONLY;
	}
}
