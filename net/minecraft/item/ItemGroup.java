/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.item;

import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemStackSet;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

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
    @Nullable
    private DisplayParameters displayParameters;
    private boolean searchProviderDirty;
    @Nullable
    private Consumer<List<ItemStack>> searchProviderReloader;

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

    protected abstract void addItems(FeatureSet var1, Entries var2, boolean var3);

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

    private ItemStackSet getStacks(FeatureSet enabledFeatures, boolean search, boolean hasPermissions) {
        boolean bl;
        DisplayParameters displayParameters = new DisplayParameters(enabledFeatures, hasPermissions);
        boolean bl2 = bl = this.displayStacks == null || this.searchTabStacks == null || !Objects.equals(this.displayParameters, displayParameters);
        if (bl) {
            EntriesImpl entriesImpl = new EntriesImpl(this, enabledFeatures);
            this.addItems(enabledFeatures, entriesImpl, hasPermissions);
            this.displayStacks = entriesImpl.getParentTabStacks();
            this.searchTabStacks = entriesImpl.getSearchTabStacks();
            this.displayParameters = displayParameters;
        }
        if (this.searchProviderReloader != null && (bl || this.searchProviderDirty)) {
            this.searchProviderReloader.accept(Lists.newArrayList(this.searchTabStacks));
            this.markSearchProviderClean();
        }
        return search ? this.searchTabStacks : this.displayStacks;
    }

    public ItemStackSet getDisplayStacks(FeatureSet enabledFeatures, boolean hasPermissions) {
        return this.getStacks(enabledFeatures, false, hasPermissions);
    }

    public ItemStackSet getSearchTabStacks(FeatureSet enabledFeatures, boolean hasPermissions) {
        return this.getStacks(enabledFeatures, true, hasPermissions);
    }

    public boolean contains(FeatureSet enabledFeatures, ItemStack stack, boolean hasPermissions) {
        return this.getSearchTabStacks(enabledFeatures, hasPermissions).contains(stack);
    }

    public void setSearchProviderReloader(Consumer<List<ItemStack>> searchProviderReloader) {
        this.searchProviderReloader = searchProviderReloader;
    }

    public void markSearchProviderDirty() {
        this.searchProviderDirty = true;
    }

    private void markSearchProviderClean() {
        this.searchProviderDirty = false;
    }

    record DisplayParameters(FeatureSet enabledFeatures, boolean hasPermissions) {
    }

    static class EntriesImpl
    implements Entries {
        private final ItemStackSet parentTabStacks = new ItemStackSet();
        private final ItemStackSet searchTabStacks = new ItemStackSet();
        private final ItemGroup group;
        private final FeatureSet enabledFeatures;

        public EntriesImpl(ItemGroup group, FeatureSet enabledFeatures) {
            this.group = group;
            this.enabledFeatures = enabledFeatures;
        }

        @Override
        public void add(ItemStack stack, StackVisibility visibility) {
            boolean bl;
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

        public ItemStackSet getParentTabStacks() {
            return this.parentTabStacks;
        }

        public ItemStackSet getSearchTabStacks() {
            return this.searchTabStacks;
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

