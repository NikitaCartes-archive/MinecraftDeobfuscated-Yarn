/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.item;

import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemStackSet;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

/**
 * A group of items that the items belong to. This is used by the creative inventory.
 */
public class ItemGroup {
    private final Text displayName;
    String texture = "items.png";
    boolean scrollbar = true;
    boolean renderName = true;
    boolean special = false;
    private final Row row;
    private final int column;
    private final Type type;
    @Nullable
    private ItemStack icon;
    private Collection<ItemStack> displayStacks = ItemStackSet.create();
    private Set<ItemStack> searchTabStacks = ItemStackSet.create();
    @Nullable
    private Consumer<List<ItemStack>> searchProviderReloader;
    private final Supplier<ItemStack> iconSupplier;
    private final EntryCollector entryCollector;

    ItemGroup(Row row, int column, Type type, Text displayName, Supplier<ItemStack> iconSupplier, EntryCollector entryCollector) {
        this.row = row;
        this.column = column;
        this.displayName = displayName;
        this.iconSupplier = iconSupplier;
        this.entryCollector = entryCollector;
        this.type = type;
    }

    public static Builder create(Row location, int column) {
        return new Builder(location, column);
    }

    public Text getDisplayName() {
        return this.displayName;
    }

    public ItemStack getIcon() {
        if (this.icon == null) {
            this.icon = this.iconSupplier.get();
        }
        return this.icon;
    }

    public String getTexture() {
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

    public Row getRow() {
        return this.row;
    }

    public boolean hasStacks() {
        return !this.displayStacks.isEmpty();
    }

    public boolean shouldDisplay() {
        return this.type != Type.CATEGORY || this.hasStacks();
    }

    public boolean isSpecial() {
        return this.special;
    }

    public Type getType() {
        return this.type;
    }

    public void updateEntries(DisplayContext displayContext) {
        EntriesImpl entriesImpl = new EntriesImpl(this, displayContext.enabledFeatures);
        this.entryCollector.accept(displayContext, entriesImpl);
        this.displayStacks = entriesImpl.parentTabStacks;
        this.searchTabStacks = entriesImpl.searchTabStacks;
        this.reloadSearchProvider();
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

    public void setSearchProviderReloader(Consumer<List<ItemStack>> searchProviderReloader) {
        this.searchProviderReloader = searchProviderReloader;
    }

    public void reloadSearchProvider() {
        if (this.searchProviderReloader != null) {
            this.searchProviderReloader.accept(Lists.newArrayList(this.searchTabStacks));
        }
    }

    public static enum Row {
        TOP,
        BOTTOM;

    }

    @FunctionalInterface
    public static interface EntryCollector {
        public void accept(DisplayContext var1, Entries var2);
    }

    public static enum Type {
        CATEGORY,
        INVENTORY,
        HOTBAR,
        SEARCH;

    }

    public static class Builder {
        private static final EntryCollector EMPTY_ENTRIES = (displayContext, entries) -> {};
        private final Row row;
        private final int column;
        private Text displayName = Text.empty();
        private Supplier<ItemStack> iconSupplier = () -> ItemStack.EMPTY;
        private EntryCollector entryCollector = EMPTY_ENTRIES;
        private boolean scrollbar = true;
        private boolean renderName = true;
        private boolean special = false;
        private Type type = Type.CATEGORY;
        private String texture = "items.png";

        public Builder(Row row, int column) {
            this.row = row;
            this.column = column;
        }

        public Builder displayName(Text displayName) {
            this.displayName = displayName;
            return this;
        }

        public Builder icon(Supplier<ItemStack> iconSupplier) {
            this.iconSupplier = iconSupplier;
            return this;
        }

        public Builder entries(EntryCollector entryCollector) {
            this.entryCollector = entryCollector;
            return this;
        }

        public Builder special() {
            this.special = true;
            return this;
        }

        public Builder noRenderedName() {
            this.renderName = false;
            return this;
        }

        public Builder noScrollbar() {
            this.scrollbar = false;
            return this;
        }

        protected Builder type(Type type) {
            this.type = type;
            return this;
        }

        public Builder texture(String texture) {
            this.texture = texture;
            return this;
        }

        public ItemGroup build() {
            if ((this.type == Type.HOTBAR || this.type == Type.INVENTORY) && this.entryCollector != EMPTY_ENTRIES) {
                throw new IllegalStateException("Special tabs can't have display items");
            }
            ItemGroup itemGroup = new ItemGroup(this.row, this.column, this.type, this.displayName, this.iconSupplier, this.entryCollector);
            itemGroup.special = this.special;
            itemGroup.renderName = this.renderName;
            itemGroup.scrollbar = this.scrollbar;
            itemGroup.texture = this.texture;
            return itemGroup;
        }
    }

    static class EntriesImpl
    implements Entries {
        public final Collection<ItemStack> parentTabStacks = ItemStackSet.create();
        public final Set<ItemStack> searchTabStacks = ItemStackSet.create();
        private final ItemGroup group;
        private final FeatureSet enabledFeatures;

        public EntriesImpl(ItemGroup group, FeatureSet enabledFeatures) {
            this.group = group;
            this.enabledFeatures = enabledFeatures;
        }

        @Override
        public void add(ItemStack stack, StackVisibility visibility) {
            boolean bl;
            if (stack.getCount() != 1) {
                throw new IllegalArgumentException("Stack size must be exactly 1");
            }
            boolean bl2 = bl = this.parentTabStacks.contains(stack) && visibility != StackVisibility.SEARCH_TAB_ONLY;
            if (bl) {
                throw new IllegalStateException("Accidentally adding the same item stack twice " + stack.toHoverableText().getString() + " to a Creative Mode Tab: " + this.group.getDisplayName().getString());
            }
            if (stack.getItem().isEnabled(this.enabledFeatures)) {
                switch (visibility) {
                    case PARENT_AND_SEARCH_TABS: {
                        this.parentTabStacks.add(stack);
                        this.searchTabStacks.add(stack);
                        break;
                    }
                    case PARENT_TAB_ONLY: {
                        this.parentTabStacks.add(stack);
                        break;
                    }
                    case SEARCH_TAB_ONLY: {
                        this.searchTabStacks.add(stack);
                    }
                }
            }
        }
    }

    public record DisplayContext(FeatureSet enabledFeatures, boolean hasPermissions, RegistryWrapper.WrapperLookup lookup) {
        public boolean doesNotMatch(FeatureSet enabledFeatures, boolean hasPermissions, RegistryWrapper.WrapperLookup lookup) {
            return !this.enabledFeatures.equals(enabledFeatures) || this.hasPermissions != hasPermissions || this.lookup != lookup;
        }
    }

    protected static interface Entries {
        public void add(ItemStack var1, StackVisibility var2);

        default public void add(ItemStack stack) {
            this.add(stack, StackVisibility.PARENT_AND_SEARCH_TABS);
        }

        default public void add(ItemConvertible item, StackVisibility visibility) {
            this.add(new ItemStack(item), visibility);
        }

        default public void add(ItemConvertible item) {
            this.add(new ItemStack(item), StackVisibility.PARENT_AND_SEARCH_TABS);
        }

        default public void addAll(Collection<ItemStack> stacks, StackVisibility visibility) {
            stacks.forEach(stack -> this.add((ItemStack)stack, visibility));
        }

        default public void addAll(Collection<ItemStack> stacks) {
            this.addAll(stacks, StackVisibility.PARENT_AND_SEARCH_TABS);
        }
    }

    protected static enum StackVisibility {
        PARENT_AND_SEARCH_TABS,
        PARENT_TAB_ONLY,
        SEARCH_TAB_ONLY;

    }
}

