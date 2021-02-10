/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.screen.ingame;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryListener;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.HotbarStorage;
import net.minecraft.client.option.HotbarStorageEntry;
import net.minecraft.client.search.SearchManager;
import net.minecraft.client.search.SearchableContainer;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.tag.ItemTags;
import net.minecraft.tag.Tag;
import net.minecraft.tag.TagGroup;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class CreativeInventoryScreen
extends AbstractInventoryScreen<CreativeScreenHandler> {
    private static final Identifier TEXTURE = new Identifier("textures/gui/container/creative_inventory/tabs.png");
    private static final SimpleInventory INVENTORY = new SimpleInventory(45);
    private static final Text DELETE_ITEM_SLOT_TEXT = new TranslatableText("inventory.binSlot");
    private static int selectedTab = ItemGroup.BUILDING_BLOCKS.getIndex();
    private float scrollPosition;
    private boolean scrolling;
    private TextFieldWidget searchBox;
    @Nullable
    private List<Slot> slots;
    @Nullable
    private Slot deleteItemSlot;
    private CreativeInventoryListener listener;
    private boolean ignoreTypedCharacter;
    private boolean lastClickOutsideBounds;
    private final Map<Identifier, Tag<Item>> searchResultTags = Maps.newTreeMap();

    public CreativeInventoryScreen(PlayerEntity player) {
        super(new CreativeScreenHandler(player), player.getInventory(), LiteralText.EMPTY);
        player.currentScreenHandler = this.handler;
        this.passEvents = true;
        this.backgroundHeight = 136;
        this.backgroundWidth = 195;
    }

    @Override
    public void tick() {
        if (!this.client.interactionManager.hasCreativeInventory()) {
            this.client.openScreen(new InventoryScreen(this.client.player));
        } else if (this.searchBox != null) {
            this.searchBox.tick();
        }
    }

    @Override
    protected void onMouseClick(@Nullable Slot slot, int invSlot, int clickData, SlotActionType actionType) {
        if (this.isCreativeInventorySlot(slot)) {
            this.searchBox.setCursorToEnd();
            this.searchBox.setSelectionEnd(0);
        }
        boolean bl = actionType == SlotActionType.QUICK_MOVE;
        SlotActionType slotActionType = actionType = invSlot == -999 && actionType == SlotActionType.PICKUP ? SlotActionType.THROW : actionType;
        if (slot != null || selectedTab == ItemGroup.INVENTORY.getIndex() || actionType == SlotActionType.QUICK_CRAFT) {
            if (slot != null && !slot.canTakeItems(this.client.player)) {
                return;
            }
            if (slot == this.deleteItemSlot && bl) {
                for (int i = 0; i < this.client.player.playerScreenHandler.getStacks().size(); ++i) {
                    this.client.interactionManager.clickCreativeStack(ItemStack.EMPTY, i);
                }
            } else if (selectedTab == ItemGroup.INVENTORY.getIndex()) {
                if (slot == this.deleteItemSlot) {
                    this.client.player.getInventory().setCursorStack(ItemStack.EMPTY);
                } else if (actionType == SlotActionType.THROW && slot != null && slot.hasStack()) {
                    ItemStack itemStack = slot.takeStack(clickData == 0 ? 1 : slot.getStack().getMaxCount());
                    ItemStack itemStack2 = slot.getStack();
                    this.client.player.dropItem(itemStack, true);
                    this.client.interactionManager.dropCreativeStack(itemStack);
                    this.client.interactionManager.clickCreativeStack(itemStack2, ((CreativeSlot)((CreativeSlot)slot)).slot.id);
                } else if (actionType == SlotActionType.THROW && !this.client.player.getInventory().getCursorStack().isEmpty()) {
                    this.client.player.dropItem(this.client.player.getInventory().getCursorStack(), true);
                    this.client.interactionManager.dropCreativeStack(this.client.player.getInventory().getCursorStack());
                    this.client.player.getInventory().setCursorStack(ItemStack.EMPTY);
                } else {
                    this.client.player.playerScreenHandler.onSlotClick(slot == null ? invSlot : ((CreativeSlot)((CreativeSlot)slot)).slot.id, clickData, actionType, this.client.player);
                    this.client.player.playerScreenHandler.sendContentUpdates();
                }
            } else if (actionType != SlotActionType.QUICK_CRAFT && slot.inventory == INVENTORY) {
                PlayerInventory playerInventory = this.client.player.getInventory();
                ItemStack itemStack2 = playerInventory.getCursorStack();
                ItemStack itemStack3 = slot.getStack();
                if (actionType == SlotActionType.SWAP) {
                    if (!itemStack3.isEmpty()) {
                        ItemStack itemStack4 = itemStack3.copy();
                        itemStack4.setCount(itemStack4.getMaxCount());
                        this.client.player.getInventory().setStack(clickData, itemStack4);
                        this.client.player.playerScreenHandler.sendContentUpdates();
                    }
                    return;
                }
                if (actionType == SlotActionType.CLONE) {
                    if (playerInventory.getCursorStack().isEmpty() && slot.hasStack()) {
                        ItemStack itemStack4 = slot.getStack().copy();
                        itemStack4.setCount(itemStack4.getMaxCount());
                        playerInventory.setCursorStack(itemStack4);
                    }
                    return;
                }
                if (actionType == SlotActionType.THROW) {
                    if (!itemStack3.isEmpty()) {
                        ItemStack itemStack4 = itemStack3.copy();
                        itemStack4.setCount(clickData == 0 ? 1 : itemStack4.getMaxCount());
                        this.client.player.dropItem(itemStack4, true);
                        this.client.interactionManager.dropCreativeStack(itemStack4);
                    }
                    return;
                }
                if (!itemStack2.isEmpty() && !itemStack3.isEmpty() && itemStack2.isItemEqualIgnoreDamage(itemStack3) && ItemStack.areTagsEqual(itemStack2, itemStack3)) {
                    if (clickData == 0) {
                        if (bl) {
                            itemStack2.setCount(itemStack2.getMaxCount());
                        } else if (itemStack2.getCount() < itemStack2.getMaxCount()) {
                            itemStack2.increment(1);
                        }
                    } else {
                        itemStack2.decrement(1);
                    }
                } else if (itemStack3.isEmpty() || !itemStack2.isEmpty()) {
                    if (clickData == 0) {
                        playerInventory.setCursorStack(ItemStack.EMPTY);
                    } else {
                        playerInventory.getCursorStack().decrement(1);
                    }
                } else {
                    playerInventory.setCursorStack(itemStack3.copy());
                    itemStack2 = playerInventory.getCursorStack();
                    if (bl) {
                        itemStack2.setCount(itemStack2.getMaxCount());
                    }
                }
            } else if (this.handler != null) {
                ItemStack itemStack = slot == null ? ItemStack.EMPTY : ((CreativeScreenHandler)this.handler).getSlot(slot.id).getStack();
                ((CreativeScreenHandler)this.handler).onSlotClick(slot == null ? invSlot : slot.id, clickData, actionType, this.client.player);
                if (ScreenHandler.unpackQuickCraftStage(clickData) == 2) {
                    for (int j = 0; j < 9; ++j) {
                        this.client.interactionManager.clickCreativeStack(((CreativeScreenHandler)this.handler).getSlot(45 + j).getStack(), 36 + j);
                    }
                } else if (slot != null) {
                    ItemStack itemStack2 = ((CreativeScreenHandler)this.handler).getSlot(slot.id).getStack();
                    this.client.interactionManager.clickCreativeStack(itemStack2, slot.id - ((CreativeScreenHandler)this.handler).slots.size() + 9 + 36);
                    int k = 45 + clickData;
                    if (actionType == SlotActionType.SWAP) {
                        this.client.interactionManager.clickCreativeStack(itemStack, k - ((CreativeScreenHandler)this.handler).slots.size() + 9 + 36);
                    } else if (actionType == SlotActionType.THROW && !itemStack.isEmpty()) {
                        ItemStack itemStack4 = itemStack.copy();
                        itemStack4.setCount(clickData == 0 ? 1 : itemStack4.getMaxCount());
                        this.client.player.dropItem(itemStack4, true);
                        this.client.interactionManager.dropCreativeStack(itemStack4);
                    }
                    this.client.player.playerScreenHandler.sendContentUpdates();
                }
            }
        } else {
            PlayerInventory playerInventory = this.client.player.getInventory();
            if (!playerInventory.getCursorStack().isEmpty() && this.lastClickOutsideBounds) {
                if (clickData == 0) {
                    this.client.player.dropItem(playerInventory.getCursorStack(), true);
                    this.client.interactionManager.dropCreativeStack(playerInventory.getCursorStack());
                    playerInventory.setCursorStack(ItemStack.EMPTY);
                }
                if (clickData == 1) {
                    ItemStack itemStack2 = playerInventory.getCursorStack().split(1);
                    this.client.player.dropItem(itemStack2, true);
                    this.client.interactionManager.dropCreativeStack(itemStack2);
                }
            }
        }
    }

    private boolean isCreativeInventorySlot(@Nullable Slot slot) {
        return slot != null && slot.inventory == INVENTORY;
    }

    @Override
    protected void applyStatusEffectOffset() {
        int i = this.x;
        super.applyStatusEffectOffset();
        if (this.searchBox != null && this.x != i) {
            this.searchBox.setX(this.x + 82);
        }
    }

    @Override
    protected void init() {
        if (this.client.interactionManager.hasCreativeInventory()) {
            super.init();
            this.client.keyboard.setRepeatEvents(true);
            this.searchBox = new TextFieldWidget(this.textRenderer, this.x + 82, this.y + 6, 80, this.textRenderer.fontHeight, new TranslatableText("itemGroup.search"));
            this.searchBox.setMaxLength(50);
            this.searchBox.setDrawsBackground(false);
            this.searchBox.setVisible(false);
            this.searchBox.setEditableColor(0xFFFFFF);
            this.children.add(this.searchBox);
            int i = selectedTab;
            selectedTab = -1;
            this.setSelectedTab(ItemGroup.GROUPS[i]);
            this.client.player.playerScreenHandler.removeListener(this.listener);
            this.listener = new CreativeInventoryListener(this.client);
            this.client.player.playerScreenHandler.addListener(this.listener);
        } else {
            this.client.openScreen(new InventoryScreen(this.client.player));
        }
    }

    @Override
    public void resize(MinecraftClient client, int width, int height) {
        String string = this.searchBox.getText();
        this.init(client, width, height);
        this.searchBox.setText(string);
        if (!this.searchBox.getText().isEmpty()) {
            this.search();
        }
    }

    @Override
    public void removed() {
        super.removed();
        if (this.client.player != null && this.client.player.getInventory() != null) {
            this.client.player.playerScreenHandler.removeListener(this.listener);
        }
        this.client.keyboard.setRepeatEvents(false);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (this.ignoreTypedCharacter) {
            return false;
        }
        if (selectedTab != ItemGroup.SEARCH.getIndex()) {
            return false;
        }
        String string = this.searchBox.getText();
        if (this.searchBox.charTyped(chr, modifiers)) {
            if (!Objects.equals(string, this.searchBox.getText())) {
                this.search();
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        this.ignoreTypedCharacter = false;
        if (selectedTab != ItemGroup.SEARCH.getIndex()) {
            if (this.client.options.keyChat.matchesKey(keyCode, scanCode)) {
                this.ignoreTypedCharacter = true;
                this.setSelectedTab(ItemGroup.SEARCH);
                return true;
            }
            return super.keyPressed(keyCode, scanCode, modifiers);
        }
        boolean bl = !this.isCreativeInventorySlot(this.focusedSlot) || this.focusedSlot.hasStack();
        boolean bl2 = InputUtil.fromKeyCode(keyCode, scanCode).toInt().isPresent();
        if (bl && bl2 && this.handleHotbarKeyPressed(keyCode, scanCode)) {
            this.ignoreTypedCharacter = true;
            return true;
        }
        String string = this.searchBox.getText();
        if (this.searchBox.keyPressed(keyCode, scanCode, modifiers)) {
            if (!Objects.equals(string, this.searchBox.getText())) {
                this.search();
            }
            return true;
        }
        if (this.searchBox.isFocused() && this.searchBox.isVisible() && keyCode != 256) {
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        this.ignoreTypedCharacter = false;
        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    private void search() {
        ((CreativeScreenHandler)this.handler).itemList.clear();
        this.searchResultTags.clear();
        String string = this.searchBox.getText();
        if (string.isEmpty()) {
            for (Item item : Registry.ITEM) {
                item.appendStacks(ItemGroup.SEARCH, ((CreativeScreenHandler)this.handler).itemList);
            }
        } else {
            SearchableContainer<ItemStack> searchable;
            if (string.startsWith("#")) {
                string = string.substring(1);
                searchable = this.client.getSearchableContainer(SearchManager.ITEM_TAG);
                this.searchForTags(string);
            } else {
                searchable = this.client.getSearchableContainer(SearchManager.ITEM_TOOLTIP);
            }
            ((CreativeScreenHandler)this.handler).itemList.addAll(searchable.findAll(string.toLowerCase(Locale.ROOT)));
        }
        this.scrollPosition = 0.0f;
        ((CreativeScreenHandler)this.handler).scrollItems(0.0f);
    }

    private void searchForTags(String string) {
        Predicate<Identifier> predicate;
        int i = string.indexOf(58);
        if (i == -1) {
            predicate = identifier -> identifier.getPath().contains(string);
        } else {
            String string2 = string.substring(0, i).trim();
            String string3 = string.substring(i + 1).trim();
            predicate = identifier -> identifier.getNamespace().contains(string2) && identifier.getPath().contains(string3);
        }
        TagGroup<Item> tagGroup = ItemTags.getTagGroup();
        tagGroup.getTagIds().stream().filter(predicate).forEach(identifier -> this.searchResultTags.put((Identifier)identifier, tagGroup.getTag((Identifier)identifier)));
    }

    @Override
    protected void drawForeground(MatrixStack matrices, int mouseX, int mouseY) {
        ItemGroup itemGroup = ItemGroup.GROUPS[selectedTab];
        if (itemGroup.shouldRenderName()) {
            RenderSystem.disableBlend();
            this.textRenderer.draw(matrices, itemGroup.getTranslationKey(), 8.0f, 6.0f, 0x404040);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            double d = mouseX - (double)this.x;
            double e = mouseY - (double)this.y;
            for (ItemGroup itemGroup : ItemGroup.GROUPS) {
                if (!this.isClickInTab(itemGroup, d, e)) continue;
                return true;
            }
            if (selectedTab != ItemGroup.INVENTORY.getIndex() && this.isClickInScrollbar(mouseX, mouseY)) {
                this.scrolling = this.hasScrollbar();
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0) {
            double d = mouseX - (double)this.x;
            double e = mouseY - (double)this.y;
            this.scrolling = false;
            for (ItemGroup itemGroup : ItemGroup.GROUPS) {
                if (!this.isClickInTab(itemGroup, d, e)) continue;
                this.setSelectedTab(itemGroup);
                return true;
            }
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    private boolean hasScrollbar() {
        return selectedTab != ItemGroup.INVENTORY.getIndex() && ItemGroup.GROUPS[selectedTab].hasScrollbar() && ((CreativeScreenHandler)this.handler).shouldShowScrollbar();
    }

    private void setSelectedTab(ItemGroup group) {
        int k;
        int j;
        int i = selectedTab;
        selectedTab = group.getIndex();
        this.cursorDragSlots.clear();
        ((CreativeScreenHandler)this.handler).itemList.clear();
        if (group == ItemGroup.HOTBAR) {
            HotbarStorage hotbarStorage = this.client.getCreativeHotbarStorage();
            for (j = 0; j < 9; ++j) {
                HotbarStorageEntry hotbarStorageEntry = hotbarStorage.getSavedHotbar(j);
                if (hotbarStorageEntry.isEmpty()) {
                    for (k = 0; k < 9; ++k) {
                        if (k == j) {
                            ItemStack itemStack = new ItemStack(Items.PAPER);
                            itemStack.getOrCreateSubTag("CustomCreativeLock");
                            Text text = this.client.options.keysHotbar[j].getBoundKeyLocalizedText();
                            Text text2 = this.client.options.keySaveToolbarActivator.getBoundKeyLocalizedText();
                            itemStack.setCustomName(new TranslatableText("inventory.hotbarInfo", text2, text));
                            ((CreativeScreenHandler)this.handler).itemList.add(itemStack);
                            continue;
                        }
                        ((CreativeScreenHandler)this.handler).itemList.add(ItemStack.EMPTY);
                    }
                    continue;
                }
                ((CreativeScreenHandler)this.handler).itemList.addAll(hotbarStorageEntry);
            }
        } else if (group != ItemGroup.SEARCH) {
            group.appendStacks(((CreativeScreenHandler)this.handler).itemList);
        }
        if (group == ItemGroup.INVENTORY) {
            PlayerScreenHandler screenHandler = this.client.player.playerScreenHandler;
            if (this.slots == null) {
                this.slots = ImmutableList.copyOf(((CreativeScreenHandler)this.handler).slots);
            }
            ((CreativeScreenHandler)this.handler).slots.clear();
            for (j = 0; j < screenHandler.slots.size(); ++j) {
                int o;
                int n;
                int m;
                if (j >= 5 && j < 9) {
                    int l = j - 5;
                    m = l / 2;
                    n = l % 2;
                    o = 54 + m * 54;
                    k = 6 + n * 27;
                } else if (j >= 0 && j < 5) {
                    o = -2000;
                    k = -2000;
                } else if (j == 45) {
                    o = 35;
                    k = 20;
                } else {
                    int l = j - 9;
                    m = l % 9;
                    n = l / 9;
                    o = 9 + m * 18;
                    k = j >= 36 ? 112 : 54 + n * 18;
                }
                CreativeSlot slot = new CreativeSlot(screenHandler.slots.get(j), j, o, k);
                ((CreativeScreenHandler)this.handler).slots.add(slot);
            }
            this.deleteItemSlot = new Slot(INVENTORY, 0, 173, 112);
            ((CreativeScreenHandler)this.handler).slots.add(this.deleteItemSlot);
        } else if (i == ItemGroup.INVENTORY.getIndex()) {
            ((CreativeScreenHandler)this.handler).slots.clear();
            ((CreativeScreenHandler)this.handler).slots.addAll(this.slots);
            this.slots = null;
        }
        if (this.searchBox != null) {
            if (group == ItemGroup.SEARCH) {
                this.searchBox.setVisible(true);
                this.searchBox.setFocusUnlocked(false);
                this.searchBox.setTextFieldFocused(true);
                if (i != group.getIndex()) {
                    this.searchBox.setText("");
                }
                this.search();
            } else {
                this.searchBox.setVisible(false);
                this.searchBox.setFocusUnlocked(true);
                this.searchBox.setTextFieldFocused(false);
                this.searchBox.setText("");
            }
        }
        this.scrollPosition = 0.0f;
        ((CreativeScreenHandler)this.handler).scrollItems(0.0f);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        if (!this.hasScrollbar()) {
            return false;
        }
        int i = (((CreativeScreenHandler)this.handler).itemList.size() + 9 - 1) / 9 - 5;
        this.scrollPosition = (float)((double)this.scrollPosition - amount / (double)i);
        this.scrollPosition = MathHelper.clamp(this.scrollPosition, 0.0f, 1.0f);
        ((CreativeScreenHandler)this.handler).scrollItems(this.scrollPosition);
        return true;
    }

    @Override
    protected boolean isClickOutsideBounds(double mouseX, double mouseY, int left, int top, int button) {
        boolean bl = mouseX < (double)left || mouseY < (double)top || mouseX >= (double)(left + this.backgroundWidth) || mouseY >= (double)(top + this.backgroundHeight);
        this.lastClickOutsideBounds = bl && !this.isClickInTab(ItemGroup.GROUPS[selectedTab], mouseX, mouseY);
        return this.lastClickOutsideBounds;
    }

    protected boolean isClickInScrollbar(double mouseX, double mouseY) {
        int i = this.x;
        int j = this.y;
        int k = i + 175;
        int l = j + 18;
        int m = k + 14;
        int n = l + 112;
        return mouseX >= (double)k && mouseY >= (double)l && mouseX < (double)m && mouseY < (double)n;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (this.scrolling) {
            int i = this.y + 18;
            int j = i + 112;
            this.scrollPosition = ((float)mouseY - (float)i - 7.5f) / ((float)(j - i) - 15.0f);
            this.scrollPosition = MathHelper.clamp(this.scrollPosition, 0.0f, 1.0f);
            ((CreativeScreenHandler)this.handler).scrollItems(this.scrollPosition);
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
        for (ItemGroup itemGroup : ItemGroup.GROUPS) {
            if (this.renderTabTooltipIfHovered(matrices, itemGroup, mouseX, mouseY)) break;
        }
        if (this.deleteItemSlot != null && selectedTab == ItemGroup.INVENTORY.getIndex() && this.isPointWithinBounds(this.deleteItemSlot.x, this.deleteItemSlot.y, 16, 16, mouseX, mouseY)) {
            this.renderTooltip(matrices, DELETE_ITEM_SLOT_TEXT, mouseX, mouseY);
        }
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.drawMouseoverTooltip(matrices, mouseX, mouseY);
    }

    @Override
    protected void renderTooltip(MatrixStack matrices, ItemStack stack, int x, int y) {
        if (selectedTab == ItemGroup.SEARCH.getIndex()) {
            Map<Enchantment, Integer> map;
            List<Text> list = stack.getTooltip(this.client.player, this.client.options.advancedItemTooltips ? TooltipContext.Default.ADVANCED : TooltipContext.Default.NORMAL);
            ArrayList<Text> list2 = Lists.newArrayList(list);
            Item item = stack.getItem();
            ItemGroup itemGroup = item.getGroup();
            if (itemGroup == null && stack.isOf(Items.ENCHANTED_BOOK) && (map = EnchantmentHelper.get(stack)).size() == 1) {
                Enchantment enchantment = map.keySet().iterator().next();
                for (ItemGroup itemGroup2 : ItemGroup.GROUPS) {
                    if (!itemGroup2.containsEnchantments(enchantment.type)) continue;
                    itemGroup = itemGroup2;
                    break;
                }
            }
            this.searchResultTags.forEach((identifier, tag) -> {
                if (stack.isIn((Tag<Item>)tag)) {
                    list2.add(1, new LiteralText("#" + identifier).formatted(Formatting.DARK_PURPLE));
                }
            });
            if (itemGroup != null) {
                list2.add(1, itemGroup.getTranslationKey().shallowCopy().formatted(Formatting.BLUE));
            }
            this.renderTooltip(matrices, list2, stack.getTooltipData(), x, y);
        } else {
            super.renderTooltip(matrices, stack, x, y);
        }
    }

    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        ItemGroup itemGroup = ItemGroup.GROUPS[selectedTab];
        for (ItemGroup itemGroup2 : ItemGroup.GROUPS) {
            this.client.getTextureManager().bindTexture(TEXTURE);
            if (itemGroup2.getIndex() == selectedTab) continue;
            this.renderTabIcon(matrices, itemGroup2);
        }
        this.client.getTextureManager().bindTexture(new Identifier("textures/gui/container/creative_inventory/tab_" + itemGroup.getTexture()));
        this.drawTexture(matrices, this.x, this.y, 0, 0, this.backgroundWidth, this.backgroundHeight);
        this.searchBox.render(matrices, mouseX, mouseY, delta);
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        int i = this.x + 175;
        int j = this.y + 18;
        int k = j + 112;
        this.client.getTextureManager().bindTexture(TEXTURE);
        if (itemGroup.hasScrollbar()) {
            this.drawTexture(matrices, i, j + (int)((float)(k - j - 17) * this.scrollPosition), 232 + (this.hasScrollbar() ? 0 : 12), 0, 12, 15);
        }
        this.renderTabIcon(matrices, itemGroup);
        if (itemGroup == ItemGroup.INVENTORY) {
            InventoryScreen.drawEntity(this.x + 88, this.y + 45, 20, this.x + 88 - mouseX, this.y + 45 - 30 - mouseY, this.client.player);
        }
    }

    protected boolean isClickInTab(ItemGroup group, double mouseX, double mouseY) {
        int i = group.getColumn();
        int j = 28 * i;
        int k = 0;
        if (group.isSpecial()) {
            j = this.backgroundWidth - 28 * (6 - i) + 2;
        } else if (i > 0) {
            j += i;
        }
        k = group.isTopRow() ? (k -= 32) : (k += this.backgroundHeight);
        return mouseX >= (double)j && mouseX <= (double)(j + 28) && mouseY >= (double)k && mouseY <= (double)(k + 32);
    }

    protected boolean renderTabTooltipIfHovered(MatrixStack matrices, ItemGroup group, int i, int j) {
        int k = group.getColumn();
        int l = 28 * k;
        int m = 0;
        if (group.isSpecial()) {
            l = this.backgroundWidth - 28 * (6 - k) + 2;
        } else if (k > 0) {
            l += k;
        }
        m = group.isTopRow() ? (m -= 32) : (m += this.backgroundHeight);
        if (this.isPointWithinBounds(l + 3, m + 3, 23, 27, i, j)) {
            this.renderTooltip(matrices, group.getTranslationKey(), i, j);
            return true;
        }
        return false;
    }

    protected void renderTabIcon(MatrixStack matrices, ItemGroup group) {
        boolean bl = group.getIndex() == selectedTab;
        boolean bl2 = group.isTopRow();
        int i = group.getColumn();
        int j = i * 28;
        int k = 0;
        int l = this.x + 28 * i;
        int m = this.y;
        int n = 32;
        if (bl) {
            k += 32;
        }
        if (group.isSpecial()) {
            l = this.x + this.backgroundWidth - 28 * (6 - i);
        } else if (i > 0) {
            l += i;
        }
        if (bl2) {
            m -= 28;
        } else {
            k += 64;
            m += this.backgroundHeight - 4;
        }
        this.drawTexture(matrices, l, m, j, k, 28, 32);
        this.itemRenderer.zOffset = 100.0f;
        int n2 = bl2 ? 1 : -1;
        RenderSystem.enableRescaleNormal();
        ItemStack itemStack = group.getIcon();
        this.itemRenderer.renderInGuiWithOverrides(itemStack, l += 6, m += 8 + n2);
        this.itemRenderer.renderGuiItemOverlay(this.textRenderer, itemStack, l, m);
        this.itemRenderer.zOffset = 0.0f;
    }

    public int getSelectedTab() {
        return selectedTab;
    }

    public static void onHotbarKeyPress(MinecraftClient client, int index, boolean restore, boolean save) {
        ClientPlayerEntity clientPlayerEntity = client.player;
        HotbarStorage hotbarStorage = client.getCreativeHotbarStorage();
        HotbarStorageEntry hotbarStorageEntry = hotbarStorage.getSavedHotbar(index);
        if (restore) {
            for (int i = 0; i < PlayerInventory.getHotbarSize(); ++i) {
                ItemStack itemStack = ((ItemStack)hotbarStorageEntry.get(i)).copy();
                clientPlayerEntity.getInventory().setStack(i, itemStack);
                client.interactionManager.clickCreativeStack(itemStack, 36 + i);
            }
            clientPlayerEntity.playerScreenHandler.sendContentUpdates();
        } else if (save) {
            for (int i = 0; i < PlayerInventory.getHotbarSize(); ++i) {
                hotbarStorageEntry.set(i, clientPlayerEntity.getInventory().getStack(i).copy());
            }
            Text text = client.options.keysHotbar[index].getBoundKeyLocalizedText();
            Text text2 = client.options.keyLoadToolbarActivator.getBoundKeyLocalizedText();
            client.inGameHud.setOverlayMessage(new TranslatableText("inventory.hotbarSaved", text2, text), false);
            hotbarStorage.save();
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class LockableSlot
    extends Slot {
        public LockableSlot(Inventory inventory, int i, int j, int k) {
            super(inventory, i, j, k);
        }

        @Override
        public boolean canTakeItems(PlayerEntity playerEntity) {
            if (super.canTakeItems(playerEntity) && this.hasStack()) {
                return this.getStack().getSubTag("CustomCreativeLock") == null;
            }
            return !this.hasStack();
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class CreativeSlot
    extends Slot {
        private final Slot slot;

        public CreativeSlot(Slot slot, int invSlot, int x, int y) {
            super(slot.inventory, invSlot, x, y);
            this.slot = slot;
        }

        @Override
        public ItemStack onTakeItem(PlayerEntity player, ItemStack stack) {
            return this.slot.onTakeItem(player, stack);
        }

        @Override
        public boolean canInsert(ItemStack stack) {
            return this.slot.canInsert(stack);
        }

        @Override
        public ItemStack getStack() {
            return this.slot.getStack();
        }

        @Override
        public boolean hasStack() {
            return this.slot.hasStack();
        }

        @Override
        public void setStack(ItemStack stack) {
            this.slot.setStack(stack);
        }

        @Override
        public void markDirty() {
            this.slot.markDirty();
        }

        @Override
        public int getMaxItemCount() {
            return this.slot.getMaxItemCount();
        }

        @Override
        public int getMaxItemCount(ItemStack stack) {
            return this.slot.getMaxItemCount(stack);
        }

        @Override
        @Nullable
        public Pair<Identifier, Identifier> getBackgroundSprite() {
            return this.slot.getBackgroundSprite();
        }

        @Override
        public ItemStack takeStack(int amount) {
            return this.slot.takeStack(amount);
        }

        @Override
        public boolean doDrawHoveringEffect() {
            return this.slot.doDrawHoveringEffect();
        }

        @Override
        public boolean canTakeItems(PlayerEntity playerEntity) {
            return this.slot.canTakeItems(playerEntity);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class CreativeScreenHandler
    extends ScreenHandler {
        public final DefaultedList<ItemStack> itemList = DefaultedList.of();

        public CreativeScreenHandler(PlayerEntity player) {
            super(null, 0);
            int i;
            PlayerInventory playerInventory = player.getInventory();
            for (i = 0; i < 5; ++i) {
                for (int j = 0; j < 9; ++j) {
                    this.addSlot(new LockableSlot(INVENTORY, i * 9 + j, 9 + j * 18, 18 + i * 18));
                }
            }
            for (i = 0; i < 9; ++i) {
                this.addSlot(new Slot(playerInventory, i, 9 + i * 18, 112));
            }
            this.scrollItems(0.0f);
        }

        @Override
        public boolean canUse(PlayerEntity player) {
            return true;
        }

        public void scrollItems(float position) {
            int i = (this.itemList.size() + 9 - 1) / 9 - 5;
            int j = (int)((double)(position * (float)i) + 0.5);
            if (j < 0) {
                j = 0;
            }
            for (int k = 0; k < 5; ++k) {
                for (int l = 0; l < 9; ++l) {
                    int m = l + (k + j) * 9;
                    if (m >= 0 && m < this.itemList.size()) {
                        INVENTORY.setStack(l + k * 9, this.itemList.get(m));
                        continue;
                    }
                    INVENTORY.setStack(l + k * 9, ItemStack.EMPTY);
                }
            }
        }

        public boolean shouldShowScrollbar() {
            return this.itemList.size() > 45;
        }

        @Override
        public ItemStack transferSlot(PlayerEntity player, int index) {
            Slot slot;
            if (index >= this.slots.size() - 9 && index < this.slots.size() && (slot = (Slot)this.slots.get(index)) != null && slot.hasStack()) {
                slot.setStack(ItemStack.EMPTY);
            }
            return ItemStack.EMPTY;
        }

        @Override
        public boolean canInsertIntoSlot(ItemStack stack, Slot slot) {
            return slot.inventory != INVENTORY;
        }

        @Override
        public boolean canInsertIntoSlot(Slot slot) {
            return slot.inventory != INVENTORY;
        }
    }
}

