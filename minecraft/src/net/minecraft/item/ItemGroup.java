package net.minecraft.item;

import java.util.Collection;
import java.util.Set;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

/**
 * A group of items that the items belong to. This is used by the creative inventory.
 */
public class ItemGroup {
	static final Identifier ITEMS = getTabTextureId("items");
	private final Text displayName;
	Identifier texture = ITEMS;
	boolean scrollbar = true;
	boolean renderName = true;
	boolean special = false;
	private final ItemGroup.Row row;
	private final int column;
	private final ItemGroup.Type type;
	@Nullable
	private ItemStack icon;
	private Collection<ItemStack> displayStacks = ItemStackSet.create();
	private Set<ItemStack> searchTabStacks = ItemStackSet.create();
	private final Supplier<ItemStack> iconSupplier;
	private final ItemGroup.EntryCollector entryCollector;

	ItemGroup(ItemGroup.Row row, int column, ItemGroup.Type type, Text displayName, Supplier<ItemStack> iconSupplier, ItemGroup.EntryCollector entryCollector) {
		this.row = row;
		this.column = column;
		this.displayName = displayName;
		this.iconSupplier = iconSupplier;
		this.entryCollector = entryCollector;
		this.type = type;
	}

	public static Identifier getTabTextureId(String name) {
		return Identifier.ofVanilla("textures/gui/container/creative_inventory/tab_" + name + ".png");
	}

	public static ItemGroup.Builder create(ItemGroup.Row location, int column) {
		return new ItemGroup.Builder(location, column);
	}

	public Text getDisplayName() {
		return this.displayName;
	}

	public ItemStack getIcon() {
		if (this.icon == null) {
			this.icon = (ItemStack)this.iconSupplier.get();
		}

		return this.icon;
	}

	public Identifier getTexture() {
		return this.texture;
	}

	/**
	 * Checks if this item group should render its name.
	 * 
	 * <p>The name is rendered below the top row of item groups and above the inventory.
	 */
	public boolean shouldRenderName() {
		return this.renderName;
	}

	public boolean hasScrollbar() {
		return this.scrollbar;
	}

	public int getColumn() {
		return this.column;
	}

	public ItemGroup.Row getRow() {
		return this.row;
	}

	public boolean hasStacks() {
		return !this.displayStacks.isEmpty();
	}

	public boolean shouldDisplay() {
		return this.type != ItemGroup.Type.CATEGORY || this.hasStacks();
	}

	public boolean isSpecial() {
		return this.special;
	}

	public ItemGroup.Type getType() {
		return this.type;
	}

	public void updateEntries(ItemGroup.DisplayContext displayContext) {
		ItemGroup.EntriesImpl entriesImpl = new ItemGroup.EntriesImpl(this, displayContext.enabledFeatures);
		RegistryKey<ItemGroup> registryKey = (RegistryKey<ItemGroup>)Registries.ITEM_GROUP
			.getKey(this)
			.orElseThrow(() -> new IllegalStateException("Unregistered creative tab: " + this));
		this.entryCollector.accept(displayContext, entriesImpl);
		this.displayStacks = entriesImpl.parentTabStacks;
		this.searchTabStacks = entriesImpl.searchTabStacks;
	}

	public Collection<ItemStack> getDisplayStacks() {
		return this.displayStacks;
	}

	public Collection<ItemStack> getSearchTabStacks() {
		return this.searchTabStacks;
	}

	public boolean contains(ItemStack stack) {
		return this.searchTabStacks.contains(stack);
	}

	public static class Builder {
		private static final ItemGroup.EntryCollector EMPTY_ENTRIES = (displayContext, entries) -> {
		};
		private final ItemGroup.Row row;
		private final int column;
		private Text displayName = Text.empty();
		private Supplier<ItemStack> iconSupplier = () -> ItemStack.EMPTY;
		private ItemGroup.EntryCollector entryCollector = EMPTY_ENTRIES;
		private boolean scrollbar = true;
		private boolean renderName = true;
		private boolean special = false;
		private ItemGroup.Type type = ItemGroup.Type.CATEGORY;
		private Identifier texture = ItemGroup.ITEMS;

		public Builder(ItemGroup.Row row, int column) {
			this.row = row;
			this.column = column;
		}

		public ItemGroup.Builder displayName(Text displayName) {
			this.displayName = displayName;
			return this;
		}

		public ItemGroup.Builder icon(Supplier<ItemStack> iconSupplier) {
			this.iconSupplier = iconSupplier;
			return this;
		}

		public ItemGroup.Builder entries(ItemGroup.EntryCollector entryCollector) {
			this.entryCollector = entryCollector;
			return this;
		}

		public ItemGroup.Builder special() {
			this.special = true;
			return this;
		}

		public ItemGroup.Builder noRenderedName() {
			this.renderName = false;
			return this;
		}

		public ItemGroup.Builder noScrollbar() {
			this.scrollbar = false;
			return this;
		}

		protected ItemGroup.Builder type(ItemGroup.Type type) {
			this.type = type;
			return this;
		}

		public ItemGroup.Builder texture(Identifier texture) {
			this.texture = texture;
			return this;
		}

		public ItemGroup build() {
			if ((this.type == ItemGroup.Type.HOTBAR || this.type == ItemGroup.Type.INVENTORY) && this.entryCollector != EMPTY_ENTRIES) {
				throw new IllegalStateException("Special tabs can't have display items");
			} else {
				ItemGroup itemGroup = new ItemGroup(this.row, this.column, this.type, this.displayName, this.iconSupplier, this.entryCollector);
				itemGroup.special = this.special;
				itemGroup.renderName = this.renderName;
				itemGroup.scrollbar = this.scrollbar;
				itemGroup.texture = this.texture;
				return itemGroup;
			}
		}
	}

	public static record DisplayContext(FeatureSet enabledFeatures, boolean hasPermissions, RegistryWrapper.WrapperLookup lookup) {

		public boolean doesNotMatch(FeatureSet enabledFeatures, boolean hasPermissions, RegistryWrapper.WrapperLookup registries) {
			return !this.enabledFeatures.equals(enabledFeatures) || this.hasPermissions != hasPermissions || this.lookup != registries;
		}
	}

	public interface Entries {
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
		public final Collection<ItemStack> parentTabStacks = ItemStackSet.create();
		public final Set<ItemStack> searchTabStacks = ItemStackSet.create();
		private final ItemGroup group;
		private final FeatureSet enabledFeatures;

		public EntriesImpl(ItemGroup group, FeatureSet enabledFeatures) {
			this.group = group;
			this.enabledFeatures = enabledFeatures;
		}

		@Override
		public void add(ItemStack stack, ItemGroup.StackVisibility visibility) {
			if (stack.getCount() != 1) {
				throw new IllegalArgumentException("Stack size must be exactly 1");
			} else {
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
		}
	}

	@FunctionalInterface
	public interface EntryCollector {
		void accept(ItemGroup.DisplayContext displayContext, ItemGroup.Entries entries);
	}

	public static enum Row {
		TOP,
		BOTTOM;
	}

	protected static enum StackVisibility {
		PARENT_AND_SEARCH_TABS,
		PARENT_TAB_ONLY,
		SEARCH_TAB_ONLY;
	}

	public static enum Type {
		CATEGORY,
		INVENTORY,
		HOTBAR,
		SEARCH;
	}
}
