package net.minecraft.client.gui.screen.ingame;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.HotbarStorage;
import net.minecraft.client.option.HotbarStorageEntry;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.search.SearchManager;
import net.minecraft.client.search.SearchProvider;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemStackSet;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public class CreativeInventoryScreen extends AbstractInventoryScreen<CreativeInventoryScreen.CreativeScreenHandler> {
	private static final Identifier TEXTURE = new Identifier("textures/gui/container/creative_inventory/tabs.png");
	private static final String TAB_TEXTURE_PREFIX = "textures/gui/container/creative_inventory/tab_";
	private static final String CUSTOM_CREATIVE_LOCK_KEY = "CustomCreativeLock";
	private static final int ROWS_COUNT = 5;
	private static final int COLUMNS_COUNT = 9;
	private static final int TAB_WIDTH = 26;
	private static final int TAB_HEIGHT = 32;
	private static final int SCROLLBAR_WIDTH = 12;
	private static final int SCROLLBAR_HEIGHT = 15;
	static final SimpleInventory INVENTORY = new SimpleInventory(45);
	private static final Text DELETE_ITEM_SLOT_TEXT = Text.translatable("inventory.binSlot");
	private static final int WHITE = 16777215;
	private static ItemGroup selectedTab = ItemGroups.getDefaultTab();
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
	private final Set<TagKey<Item>> searchResultTags = new HashSet();
	private final boolean operatorTabEnabled;

	public CreativeInventoryScreen(PlayerEntity player, FeatureSet enabledFeatures, boolean operatorTabEnabled) {
		super(new CreativeInventoryScreen.CreativeScreenHandler(player), player.getInventory(), ScreenTexts.EMPTY);
		player.currentScreenHandler = this.handler;
		this.passEvents = true;
		this.backgroundHeight = 136;
		this.backgroundWidth = 195;
		this.operatorTabEnabled = operatorTabEnabled;
		ItemGroups.updateDisplayParameters(enabledFeatures, this.shouldShowOperatorTab(player));
	}

	private boolean shouldShowOperatorTab(PlayerEntity player) {
		return player.isCreativeLevelTwoOp() && this.operatorTabEnabled;
	}

	private void updateDisplayParameters(FeatureSet enabledFeatures, boolean showOperatorTab) {
		if (ItemGroups.updateDisplayParameters(enabledFeatures, showOperatorTab)) {
			for (ItemGroup itemGroup : ItemGroups.getGroups()) {
				ItemStackSet itemStackSet = itemGroup.getDisplayStacks();
				if (itemGroup == selectedTab) {
					if (itemGroup.getType() == ItemGroup.Type.CATEGORY && itemStackSet.isEmpty()) {
						this.setSelectedTab(ItemGroups.getDefaultTab());
					} else {
						this.refreshSelectedTab(itemStackSet);
					}
				}
			}
		}
	}

	private void refreshSelectedTab(ItemStackSet stacks) {
		int i = this.handler.getRow(this.scrollPosition);
		this.handler.itemList.clear();
		if (selectedTab.getType() == ItemGroup.Type.SEARCH) {
			this.search();
		} else {
			this.handler.itemList.addAll(stacks);
		}

		this.scrollPosition = this.handler.getScrollPosition(i);
		this.handler.scrollItems(this.scrollPosition);
	}

	@Override
	public void handledScreenTick() {
		super.handledScreenTick();
		if (this.client != null) {
			if (this.client.player != null) {
				this.updateDisplayParameters(this.client.player.networkHandler.getEnabledFeatures(), this.shouldShowOperatorTab(this.client.player));
			}

			if (!this.client.interactionManager.hasCreativeInventory()) {
				this.client.setScreen(new InventoryScreen(this.client.player));
			} else {
				this.searchBox.tick();
			}
		}
	}

	@Override
	protected void onMouseClick(@Nullable Slot slot, int slotId, int button, SlotActionType actionType) {
		if (this.isCreativeInventorySlot(slot)) {
			this.searchBox.setCursorToEnd();
			this.searchBox.setSelectionEnd(0);
		}

		boolean bl = actionType == SlotActionType.QUICK_MOVE;
		actionType = slotId == -999 && actionType == SlotActionType.PICKUP ? SlotActionType.THROW : actionType;
		if (slot == null && selectedTab.getType() != ItemGroup.Type.INVENTORY && actionType != SlotActionType.QUICK_CRAFT) {
			if (!this.handler.getCursorStack().isEmpty() && this.lastClickOutsideBounds) {
				if (button == 0) {
					this.client.player.dropItem(this.handler.getCursorStack(), true);
					this.client.interactionManager.dropCreativeStack(this.handler.getCursorStack());
					this.handler.setCursorStack(ItemStack.EMPTY);
				}

				if (button == 1) {
					ItemStack itemStack = this.handler.getCursorStack().split(1);
					this.client.player.dropItem(itemStack, true);
					this.client.interactionManager.dropCreativeStack(itemStack);
				}
			}
		} else {
			if (slot != null && !slot.canTakeItems(this.client.player)) {
				return;
			}

			if (slot == this.deleteItemSlot && bl) {
				for (int i = 0; i < this.client.player.playerScreenHandler.getStacks().size(); i++) {
					this.client.interactionManager.clickCreativeStack(ItemStack.EMPTY, i);
				}
			} else if (selectedTab.getType() == ItemGroup.Type.INVENTORY) {
				if (slot == this.deleteItemSlot) {
					this.handler.setCursorStack(ItemStack.EMPTY);
				} else if (actionType == SlotActionType.THROW && slot != null && slot.hasStack()) {
					ItemStack itemStack = slot.takeStack(button == 0 ? 1 : slot.getStack().getMaxCount());
					ItemStack itemStack2 = slot.getStack();
					this.client.player.dropItem(itemStack, true);
					this.client.interactionManager.dropCreativeStack(itemStack);
					this.client.interactionManager.clickCreativeStack(itemStack2, ((CreativeInventoryScreen.CreativeSlot)slot).slot.id);
				} else if (actionType == SlotActionType.THROW && !this.handler.getCursorStack().isEmpty()) {
					this.client.player.dropItem(this.handler.getCursorStack(), true);
					this.client.interactionManager.dropCreativeStack(this.handler.getCursorStack());
					this.handler.setCursorStack(ItemStack.EMPTY);
				} else {
					this.client
						.player
						.playerScreenHandler
						.onSlotClick(slot == null ? slotId : ((CreativeInventoryScreen.CreativeSlot)slot).slot.id, button, actionType, this.client.player);
					this.client.player.playerScreenHandler.sendContentUpdates();
				}
			} else if (actionType != SlotActionType.QUICK_CRAFT && slot.inventory == INVENTORY) {
				ItemStack itemStack = this.handler.getCursorStack();
				ItemStack itemStack2 = slot.getStack();
				if (actionType == SlotActionType.SWAP) {
					if (!itemStack2.isEmpty()) {
						ItemStack itemStack3 = itemStack2.copy();
						itemStack3.setCount(itemStack3.getMaxCount());
						this.client.player.getInventory().setStack(button, itemStack3);
						this.client.player.playerScreenHandler.sendContentUpdates();
					}

					return;
				}

				if (actionType == SlotActionType.CLONE) {
					if (this.handler.getCursorStack().isEmpty() && slot.hasStack()) {
						ItemStack itemStack3 = slot.getStack().copy();
						itemStack3.setCount(itemStack3.getMaxCount());
						this.handler.setCursorStack(itemStack3);
					}

					return;
				}

				if (actionType == SlotActionType.THROW) {
					if (!itemStack2.isEmpty()) {
						ItemStack itemStack3 = itemStack2.copy();
						itemStack3.setCount(button == 0 ? 1 : itemStack3.getMaxCount());
						this.client.player.dropItem(itemStack3, true);
						this.client.interactionManager.dropCreativeStack(itemStack3);
					}

					return;
				}

				if (!itemStack.isEmpty() && !itemStack2.isEmpty() && itemStack.isItemEqualIgnoreDamage(itemStack2) && ItemStack.areNbtEqual(itemStack, itemStack2)) {
					if (button == 0) {
						if (bl) {
							itemStack.setCount(itemStack.getMaxCount());
						} else if (itemStack.getCount() < itemStack.getMaxCount()) {
							itemStack.increment(1);
						}
					} else {
						itemStack.decrement(1);
					}
				} else if (!itemStack2.isEmpty() && itemStack.isEmpty()) {
					this.handler.setCursorStack(itemStack2.copy());
					itemStack = this.handler.getCursorStack();
					if (bl) {
						itemStack.setCount(itemStack.getMaxCount());
					}
				} else if (button == 0) {
					this.handler.setCursorStack(ItemStack.EMPTY);
				} else {
					this.handler.getCursorStack().decrement(1);
				}
			} else if (this.handler != null) {
				ItemStack itemStackx = slot == null ? ItemStack.EMPTY : this.handler.getSlot(slot.id).getStack();
				this.handler.onSlotClick(slot == null ? slotId : slot.id, button, actionType, this.client.player);
				if (ScreenHandler.unpackQuickCraftStage(button) == 2) {
					for (int j = 0; j < 9; j++) {
						this.client.interactionManager.clickCreativeStack(this.handler.getSlot(45 + j).getStack(), 36 + j);
					}
				} else if (slot != null) {
					ItemStack itemStack2x = this.handler.getSlot(slot.id).getStack();
					this.client.interactionManager.clickCreativeStack(itemStack2x, slot.id - this.handler.slots.size() + 9 + 36);
					int k = 45 + button;
					if (actionType == SlotActionType.SWAP) {
						this.client.interactionManager.clickCreativeStack(itemStackx, k - this.handler.slots.size() + 9 + 36);
					} else if (actionType == SlotActionType.THROW && !itemStackx.isEmpty()) {
						ItemStack itemStack4 = itemStackx.copy();
						itemStack4.setCount(button == 0 ? 1 : itemStack4.getMaxCount());
						this.client.player.dropItem(itemStack4, true);
						this.client.interactionManager.dropCreativeStack(itemStack4);
					}

					this.client.player.playerScreenHandler.sendContentUpdates();
				}
			}
		}
	}

	private boolean isCreativeInventorySlot(@Nullable Slot slot) {
		return slot != null && slot.inventory == INVENTORY;
	}

	@Override
	protected void init() {
		if (this.client.interactionManager.hasCreativeInventory()) {
			super.init();
			this.searchBox = new TextFieldWidget(this.textRenderer, this.x + 82, this.y + 6, 80, 9, Text.translatable("itemGroup.search"));
			this.searchBox.setMaxLength(50);
			this.searchBox.setDrawsBackground(false);
			this.searchBox.setVisible(false);
			this.searchBox.setEditableColor(16777215);
			this.addSelectableChild(this.searchBox);
			ItemGroup itemGroup = selectedTab;
			selectedTab = ItemGroups.getDefaultTab();
			this.setSelectedTab(itemGroup);
			this.client.player.playerScreenHandler.removeListener(this.listener);
			this.listener = new CreativeInventoryListener(this.client);
			this.client.player.playerScreenHandler.addListener(this.listener);
			if (!selectedTab.shouldDisplay()) {
				this.setSelectedTab(ItemGroups.getDefaultTab());
			}
		} else {
			this.client.setScreen(new InventoryScreen(this.client.player));
		}
	}

	@Override
	public void resize(MinecraftClient client, int width, int height) {
		int i = this.handler.getRow(this.scrollPosition);
		String string = this.searchBox.getText();
		this.init(client, width, height);
		this.searchBox.setText(string);
		if (!this.searchBox.getText().isEmpty()) {
			this.search();
		}

		this.scrollPosition = this.handler.getScrollPosition(i);
		this.handler.scrollItems(this.scrollPosition);
	}

	@Override
	public void removed() {
		super.removed();
		if (this.client.player != null && this.client.player.getInventory() != null) {
			this.client.player.playerScreenHandler.removeListener(this.listener);
		}
	}

	@Override
	public boolean charTyped(char chr, int modifiers) {
		if (this.ignoreTypedCharacter) {
			return false;
		} else if (selectedTab.getType() != ItemGroup.Type.SEARCH) {
			return false;
		} else {
			String string = this.searchBox.getText();
			if (this.searchBox.charTyped(chr, modifiers)) {
				if (!Objects.equals(string, this.searchBox.getText())) {
					this.search();
				}

				return true;
			} else {
				return false;
			}
		}
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		this.ignoreTypedCharacter = false;
		if (selectedTab.getType() != ItemGroup.Type.SEARCH) {
			if (this.client.options.chatKey.matchesKey(keyCode, scanCode)) {
				this.ignoreTypedCharacter = true;
				this.setSelectedTab(ItemGroups.getSearchGroup());
				return true;
			} else {
				return super.keyPressed(keyCode, scanCode, modifiers);
			}
		} else {
			boolean bl = !this.isCreativeInventorySlot(this.focusedSlot) || this.focusedSlot.hasStack();
			boolean bl2 = InputUtil.fromKeyCode(keyCode, scanCode).toInt().isPresent();
			if (bl && bl2 && this.handleHotbarKeyPressed(keyCode, scanCode)) {
				this.ignoreTypedCharacter = true;
				return true;
			} else {
				String string = this.searchBox.getText();
				if (this.searchBox.keyPressed(keyCode, scanCode, modifiers)) {
					if (!Objects.equals(string, this.searchBox.getText())) {
						this.search();
					}

					return true;
				} else {
					return this.searchBox.isFocused() && this.searchBox.isVisible() && keyCode != GLFW.GLFW_KEY_ESCAPE ? true : super.keyPressed(keyCode, scanCode, modifiers);
				}
			}
		}
	}

	@Override
	public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
		this.ignoreTypedCharacter = false;
		return super.keyReleased(keyCode, scanCode, modifiers);
	}

	private void search() {
		this.handler.itemList.clear();
		this.searchResultTags.clear();
		String string = this.searchBox.getText();
		if (string.isEmpty()) {
			this.handler.itemList.addAll(selectedTab.getDisplayStacks());
		} else {
			SearchProvider<ItemStack> searchProvider;
			if (string.startsWith("#")) {
				string = string.substring(1);
				searchProvider = this.client.getSearchProvider(SearchManager.ITEM_TAG);
				this.searchForTags(string);
			} else {
				searchProvider = this.client.getSearchProvider(SearchManager.ITEM_TOOLTIP);
			}

			this.handler.itemList.addAll(searchProvider.findAll(string.toLowerCase(Locale.ROOT)));
		}

		this.scrollPosition = 0.0F;
		this.handler.scrollItems(0.0F);
	}

	private void searchForTags(String id) {
		int i = id.indexOf(58);
		Predicate<Identifier> predicate;
		if (i == -1) {
			predicate = idx -> idx.getPath().contains(id);
		} else {
			String string = id.substring(0, i).trim();
			String string2 = id.substring(i + 1).trim();
			predicate = idx -> idx.getNamespace().contains(string) && idx.getPath().contains(string2);
		}

		Registries.ITEM.streamTags().filter(tag -> predicate.test(tag.id())).forEach(this.searchResultTags::add);
	}

	@Override
	protected void drawForeground(MatrixStack matrices, int mouseX, int mouseY) {
		if (selectedTab.shouldRenderName()) {
			RenderSystem.disableBlend();
			this.textRenderer.draw(matrices, selectedTab.getDisplayName(), 8.0F, 6.0F, 4210752);
		}
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (button == 0) {
			double d = mouseX - (double)this.x;
			double e = mouseY - (double)this.y;

			for (ItemGroup itemGroup : ItemGroups.getGroupsToDisplay()) {
				if (this.isClickInTab(itemGroup, d, e)) {
					return true;
				}
			}

			if (selectedTab.getType() != ItemGroup.Type.INVENTORY && this.isClickInScrollbar(mouseX, mouseY)) {
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

			for (ItemGroup itemGroup : ItemGroups.getGroupsToDisplay()) {
				if (this.isClickInTab(itemGroup, d, e)) {
					this.setSelectedTab(itemGroup);
					return true;
				}
			}
		}

		return super.mouseReleased(mouseX, mouseY, button);
	}

	private boolean hasScrollbar() {
		return selectedTab.hasScrollbar() && this.handler.shouldShowScrollbar();
	}

	private void setSelectedTab(ItemGroup group) {
		ItemGroup itemGroup = selectedTab;
		selectedTab = group;
		this.cursorDragSlots.clear();
		this.handler.itemList.clear();
		this.endTouchDrag();
		if (selectedTab.getType() == ItemGroup.Type.HOTBAR) {
			HotbarStorage hotbarStorage = this.client.getCreativeHotbarStorage();

			for (int i = 0; i < 9; i++) {
				HotbarStorageEntry hotbarStorageEntry = hotbarStorage.getSavedHotbar(i);
				if (hotbarStorageEntry.isEmpty()) {
					for (int j = 0; j < 9; j++) {
						if (j == i) {
							ItemStack itemStack = new ItemStack(Items.PAPER);
							itemStack.getOrCreateSubNbt("CustomCreativeLock");
							Text text = this.client.options.hotbarKeys[i].getBoundKeyLocalizedText();
							Text text2 = this.client.options.saveToolbarActivatorKey.getBoundKeyLocalizedText();
							itemStack.setCustomName(Text.translatable("inventory.hotbarInfo", text2, text));
							this.handler.itemList.add(itemStack);
						} else {
							this.handler.itemList.add(ItemStack.EMPTY);
						}
					}
				} else {
					this.handler.itemList.addAll(hotbarStorageEntry);
				}
			}
		} else if (selectedTab.getType() == ItemGroup.Type.CATEGORY) {
			this.handler.itemList.addAll(selectedTab.getDisplayStacks());
		}

		if (selectedTab.getType() == ItemGroup.Type.INVENTORY) {
			ScreenHandler screenHandler = this.client.player.playerScreenHandler;
			if (this.slots == null) {
				this.slots = ImmutableList.copyOf(this.handler.slots);
			}

			this.handler.slots.clear();

			for (int ix = 0; ix < screenHandler.slots.size(); ix++) {
				int n;
				int jx;
				if (ix >= 5 && ix < 9) {
					int k = ix - 5;
					int l = k / 2;
					int m = k % 2;
					n = 54 + l * 54;
					jx = 6 + m * 27;
				} else if (ix >= 0 && ix < 5) {
					n = -2000;
					jx = -2000;
				} else if (ix == 45) {
					n = 35;
					jx = 20;
				} else {
					int k = ix - 9;
					int l = k % 9;
					int m = k / 9;
					n = 9 + l * 18;
					if (ix >= 36) {
						jx = 112;
					} else {
						jx = 54 + m * 18;
					}
				}

				Slot slot = new CreativeInventoryScreen.CreativeSlot(screenHandler.slots.get(ix), ix, n, jx);
				this.handler.slots.add(slot);
			}

			this.deleteItemSlot = new Slot(INVENTORY, 0, 173, 112);
			this.handler.slots.add(this.deleteItemSlot);
		} else if (itemGroup.getType() == ItemGroup.Type.INVENTORY) {
			this.handler.slots.clear();
			this.handler.slots.addAll(this.slots);
			this.slots = null;
		}

		if (selectedTab.getType() == ItemGroup.Type.SEARCH) {
			this.searchBox.setVisible(true);
			this.searchBox.setFocusUnlocked(false);
			this.searchBox.setTextFieldFocused(true);
			if (itemGroup != group) {
				this.searchBox.setText("");
			}

			this.search();
		} else {
			this.searchBox.setVisible(false);
			this.searchBox.setFocusUnlocked(true);
			this.searchBox.setTextFieldFocused(false);
			this.searchBox.setText("");
		}

		this.scrollPosition = 0.0F;
		this.handler.scrollItems(0.0F);
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
		if (!this.hasScrollbar()) {
			return false;
		} else {
			this.scrollPosition = this.handler.getScrollPosition(this.scrollPosition, amount);
			this.handler.scrollItems(this.scrollPosition);
			return true;
		}
	}

	@Override
	protected boolean isClickOutsideBounds(double mouseX, double mouseY, int left, int top, int button) {
		boolean bl = mouseX < (double)left
			|| mouseY < (double)top
			|| mouseX >= (double)(left + this.backgroundWidth)
			|| mouseY >= (double)(top + this.backgroundHeight);
		this.lastClickOutsideBounds = bl && !this.isClickInTab(selectedTab, mouseX, mouseY);
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
			this.scrollPosition = ((float)mouseY - (float)i - 7.5F) / ((float)(j - i) - 15.0F);
			this.scrollPosition = MathHelper.clamp(this.scrollPosition, 0.0F, 1.0F);
			this.handler.scrollItems(this.scrollPosition);
			return true;
		} else {
			return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
		}
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackground(matrices);
		super.render(matrices, mouseX, mouseY, delta);

		for (ItemGroup itemGroup : ItemGroups.getGroupsToDisplay()) {
			if (this.renderTabTooltipIfHovered(matrices, itemGroup, mouseX, mouseY)) {
				break;
			}
		}

		if (this.deleteItemSlot != null
			&& selectedTab.getType() == ItemGroup.Type.INVENTORY
			&& this.isPointWithinBounds(this.deleteItemSlot.x, this.deleteItemSlot.y, 16, 16, (double)mouseX, (double)mouseY)) {
			this.renderTooltip(matrices, DELETE_ITEM_SLOT_TEXT, mouseX, mouseY);
		}

		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		this.drawMouseoverTooltip(matrices, mouseX, mouseY);
	}

	@Override
	protected void renderTooltip(MatrixStack matrices, ItemStack stack, int x, int y) {
		boolean bl = this.focusedSlot != null && this.focusedSlot instanceof CreativeInventoryScreen.LockableSlot;
		boolean bl2 = selectedTab.getType() == ItemGroup.Type.CATEGORY;
		boolean bl3 = selectedTab.getType() == ItemGroup.Type.SEARCH;
		TooltipContext.Default default_ = this.client.options.advancedItemTooltips ? TooltipContext.Default.ADVANCED : TooltipContext.Default.BASIC;
		TooltipContext tooltipContext = bl ? default_.withCreative() : default_;
		List<Text> list = stack.getTooltip(this.client.player, tooltipContext);
		List<Text> list2;
		if (bl2 && bl) {
			list2 = list;
		} else {
			list2 = Lists.<Text>newArrayList(list);
			if (bl3 && bl) {
				this.searchResultTags.forEach(tag -> {
					if (stack.isIn(tag)) {
						list2.add(1, Text.literal("#" + tag.id()).formatted(Formatting.DARK_PURPLE));
					}
				});
			}

			int i = 1;

			for (ItemGroup itemGroup : ItemGroups.getGroupsToDisplay()) {
				if (itemGroup.getType() != ItemGroup.Type.SEARCH && itemGroup.contains(stack)) {
					list2.add(i++, itemGroup.getDisplayName().copy().formatted(Formatting.BLUE));
				}
			}
		}

		this.renderTooltip(matrices, list2, stack.getTooltipData(), x, y);
	}

	@Override
	protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

		for (ItemGroup itemGroup : ItemGroups.getGroupsToDisplay()) {
			RenderSystem.setShader(GameRenderer::getPositionTexProgram);
			RenderSystem.setShaderTexture(0, TEXTURE);
			if (itemGroup != selectedTab) {
				this.renderTabIcon(matrices, itemGroup);
			}
		}

		RenderSystem.setShader(GameRenderer::getPositionTexProgram);
		RenderSystem.setShaderTexture(0, new Identifier("textures/gui/container/creative_inventory/tab_" + selectedTab.getTexture()));
		this.drawTexture(matrices, this.x, this.y, 0, 0, this.backgroundWidth, this.backgroundHeight);
		this.searchBox.render(matrices, mouseX, mouseY, delta);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		int i = this.x + 175;
		int j = this.y + 18;
		int k = j + 112;
		RenderSystem.setShader(GameRenderer::getPositionTexProgram);
		RenderSystem.setShaderTexture(0, TEXTURE);
		if (selectedTab.hasScrollbar()) {
			this.drawTexture(matrices, i, j + (int)((float)(k - j - 17) * this.scrollPosition), 232 + (this.hasScrollbar() ? 0 : 12), 0, 12, 15);
		}

		this.renderTabIcon(matrices, selectedTab);
		if (selectedTab.getType() == ItemGroup.Type.INVENTORY) {
			InventoryScreen.drawEntity(this.x + 88, this.y + 45, 20, (float)(this.x + 88 - mouseX), (float)(this.y + 45 - 30 - mouseY), this.client.player);
		}
	}

	private int getTabX(ItemGroup group) {
		int i = group.getColumn();
		int j = 27;
		int k = 27 * i;
		if (group.isSpecial()) {
			k = this.backgroundWidth - 27 * (7 - i);
		}

		return k;
	}

	private int getTabY(ItemGroup group) {
		int i = 0;
		if (group.getRow() == ItemGroup.Row.TOP) {
			i -= 32;
		} else {
			i += this.backgroundHeight;
		}

		return i;
	}

	protected boolean isClickInTab(ItemGroup group, double mouseX, double mouseY) {
		int i = this.getTabX(group);
		int j = this.getTabY(group);
		return mouseX >= (double)i && mouseX <= (double)(i + 26) && mouseY >= (double)j && mouseY <= (double)(j + 32);
	}

	protected boolean renderTabTooltipIfHovered(MatrixStack matrices, ItemGroup group, int mouseX, int mouseY) {
		int i = this.getTabX(group);
		int j = this.getTabY(group);
		if (this.isPointWithinBounds(i + 3, j + 3, 21, 27, (double)mouseX, (double)mouseY)) {
			this.renderTooltip(matrices, group.getDisplayName(), mouseX, mouseY);
			return true;
		} else {
			return false;
		}
	}

	protected void renderTabIcon(MatrixStack matrices, ItemGroup group) {
		boolean bl = group == selectedTab;
		boolean bl2 = group.getRow() == ItemGroup.Row.TOP;
		int i = group.getColumn();
		int j = i * 26;
		int k = 0;
		int l = this.x + this.getTabX(group);
		int m = this.y;
		int n = 32;
		if (bl) {
			k += 32;
		}

		if (bl2) {
			m -= 28;
		} else {
			k += 64;
			m += this.backgroundHeight - 4;
		}

		this.drawTexture(matrices, l, m, j, k, 26, 32);
		this.itemRenderer.zOffset = 100.0F;
		l += 5;
		m += 8 + (bl2 ? 1 : -1);
		ItemStack itemStack = group.getIcon();
		this.itemRenderer.renderInGuiWithOverrides(itemStack, l, m);
		this.itemRenderer.renderGuiItemOverlay(this.textRenderer, itemStack, l, m);
		this.itemRenderer.zOffset = 0.0F;
	}

	public boolean isInventoryTabSelected() {
		return selectedTab.getType() == ItemGroup.Type.INVENTORY;
	}

	public static void onHotbarKeyPress(MinecraftClient client, int index, boolean restore, boolean save) {
		ClientPlayerEntity clientPlayerEntity = client.player;
		HotbarStorage hotbarStorage = client.getCreativeHotbarStorage();
		HotbarStorageEntry hotbarStorageEntry = hotbarStorage.getSavedHotbar(index);
		if (restore) {
			for (int i = 0; i < PlayerInventory.getHotbarSize(); i++) {
				ItemStack itemStack = hotbarStorageEntry.get(i);
				ItemStack itemStack2 = itemStack.isItemEnabled(clientPlayerEntity.world.getEnabledFeatures()) ? itemStack.copy() : ItemStack.EMPTY;
				clientPlayerEntity.getInventory().setStack(i, itemStack2);
				client.interactionManager.clickCreativeStack(itemStack2, 36 + i);
			}

			clientPlayerEntity.playerScreenHandler.sendContentUpdates();
		} else if (save) {
			for (int i = 0; i < PlayerInventory.getHotbarSize(); i++) {
				hotbarStorageEntry.set(i, clientPlayerEntity.getInventory().getStack(i).copy());
			}

			Text text = client.options.hotbarKeys[index].getBoundKeyLocalizedText();
			Text text2 = client.options.loadToolbarActivatorKey.getBoundKeyLocalizedText();
			Text text3 = Text.translatable("inventory.hotbarSaved", text2, text);
			client.inGameHud.setOverlayMessage(text3, false);
			client.getNarratorManager().narrate(text3);
			hotbarStorage.save();
		}
	}

	@Environment(EnvType.CLIENT)
	public static class CreativeScreenHandler extends ScreenHandler {
		public final DefaultedList<ItemStack> itemList = DefaultedList.of();
		private final ScreenHandler parent;

		public CreativeScreenHandler(PlayerEntity player) {
			super(null, 0);
			this.parent = player.playerScreenHandler;
			PlayerInventory playerInventory = player.getInventory();

			for (int i = 0; i < 5; i++) {
				for (int j = 0; j < 9; j++) {
					this.addSlot(new CreativeInventoryScreen.LockableSlot(CreativeInventoryScreen.INVENTORY, i * 9 + j, 9 + j * 18, 18 + i * 18));
				}
			}

			for (int i = 0; i < 9; i++) {
				this.addSlot(new Slot(playerInventory, i, 9 + i * 18, 112));
			}

			this.scrollItems(0.0F);
		}

		@Override
		public boolean canUse(PlayerEntity player) {
			return true;
		}

		protected int getOverflowRows() {
			return MathHelper.ceilDiv(this.itemList.size(), 9) - 5;
		}

		protected int getRow(float scroll) {
			return Math.max((int)((double)(scroll * (float)this.getOverflowRows()) + 0.5), 0);
		}

		protected float getScrollPosition(int row) {
			return MathHelper.clamp((float)row / (float)this.getOverflowRows(), 0.0F, 1.0F);
		}

		protected float getScrollPosition(float current, double amount) {
			return MathHelper.clamp(current - (float)(amount / (double)this.getOverflowRows()), 0.0F, 1.0F);
		}

		public void scrollItems(float position) {
			int i = this.getRow(position);

			for (int j = 0; j < 5; j++) {
				for (int k = 0; k < 9; k++) {
					int l = k + (j + i) * 9;
					if (l >= 0 && l < this.itemList.size()) {
						CreativeInventoryScreen.INVENTORY.setStack(k + j * 9, this.itemList.get(l));
					} else {
						CreativeInventoryScreen.INVENTORY.setStack(k + j * 9, ItemStack.EMPTY);
					}
				}
			}
		}

		public boolean shouldShowScrollbar() {
			return this.itemList.size() > 45;
		}

		@Override
		public ItemStack quickMove(PlayerEntity player, int slot) {
			if (slot >= this.slots.size() - 9 && slot < this.slots.size()) {
				Slot slot2 = this.slots.get(slot);
				if (slot2 != null && slot2.hasStack()) {
					slot2.setStack(ItemStack.EMPTY);
				}
			}

			return ItemStack.EMPTY;
		}

		@Override
		public boolean canInsertIntoSlot(ItemStack stack, Slot slot) {
			return slot.inventory != CreativeInventoryScreen.INVENTORY;
		}

		@Override
		public boolean canInsertIntoSlot(Slot slot) {
			return slot.inventory != CreativeInventoryScreen.INVENTORY;
		}

		@Override
		public ItemStack getCursorStack() {
			return this.parent.getCursorStack();
		}

		@Override
		public void setCursorStack(ItemStack stack) {
			this.parent.setCursorStack(stack);
		}
	}

	@Environment(EnvType.CLIENT)
	static class CreativeSlot extends Slot {
		final Slot slot;

		public CreativeSlot(Slot slot, int invSlot, int x, int y) {
			super(slot.inventory, invSlot, x, y);
			this.slot = slot;
		}

		@Override
		public void onTakeItem(PlayerEntity player, ItemStack stack) {
			this.slot.onTakeItem(player, stack);
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

		@Nullable
		@Override
		public Pair<Identifier, Identifier> getBackgroundSprite() {
			return this.slot.getBackgroundSprite();
		}

		@Override
		public ItemStack takeStack(int amount) {
			return this.slot.takeStack(amount);
		}

		@Override
		public boolean isEnabled() {
			return this.slot.isEnabled();
		}

		@Override
		public boolean canTakeItems(PlayerEntity playerEntity) {
			return this.slot.canTakeItems(playerEntity);
		}
	}

	@Environment(EnvType.CLIENT)
	static class LockableSlot extends Slot {
		public LockableSlot(Inventory inventory, int i, int j, int k) {
			super(inventory, i, j, k);
		}

		@Override
		public boolean canTakeItems(PlayerEntity playerEntity) {
			ItemStack itemStack = this.getStack();
			return super.canTakeItems(playerEntity) && !itemStack.isEmpty()
				? itemStack.isItemEnabled(playerEntity.world.getEnabledFeatures()) && itemStack.getSubNbt("CustomCreativeLock") == null
				: itemStack.isEmpty();
		}
	}
}
