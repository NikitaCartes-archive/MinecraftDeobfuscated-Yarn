package net.minecraft.client.gui.screen.ingame;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.options.HotbarStorage;
import net.minecraft.client.options.HotbarStorageEntry;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.search.SearchManager;
import net.minecraft.client.search.Searchable;
import net.minecraft.container.Container;
import net.minecraft.container.Slot;
import net.minecraft.container.SlotActionType;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.BasicInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tag.ItemTags;
import net.minecraft.tag.Tag;
import net.minecraft.tag.TagContainer;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;

@Environment(EnvType.CLIENT)
public class CreativeInventoryScreen extends AbstractInventoryScreen<CreativeInventoryScreen.CreativeContainer> {
	private static final Identifier TEXTURE = new Identifier("textures/gui/container/creative_inventory/tabs.png");
	private static final BasicInventory inventory = new BasicInventory(45);
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
	private final Map<Identifier, Tag<Item>> searchResultTags = Maps.<Identifier, Tag<Item>>newTreeMap();

	public CreativeInventoryScreen(PlayerEntity player) {
		super(new CreativeInventoryScreen.CreativeContainer(player), player.inventory, new LiteralText(""));
		player.container = this.container;
		this.passEvents = true;
		this.containerHeight = 136;
		this.containerWidth = 195;
	}

	@Override
	public void tick() {
		if (!this.minecraft.interactionManager.hasCreativeInventory()) {
			this.minecraft.openScreen(new InventoryScreen(this.minecraft.player));
		} else if (this.searchBox != null) {
			this.searchBox.tick();
		}
	}

	@Override
	protected void onMouseClick(@Nullable Slot slot, int invSlot, int button, SlotActionType slotActionType) {
		if (this.isCreativeInventorySlot(slot)) {
			this.searchBox.setCursorToEnd();
			this.searchBox.setSelectionEnd(0);
		}

		boolean bl = slotActionType == SlotActionType.QUICK_MOVE;
		slotActionType = invSlot == -999 && slotActionType == SlotActionType.PICKUP ? SlotActionType.THROW : slotActionType;
		if (slot == null && selectedTab != ItemGroup.INVENTORY.getIndex() && slotActionType != SlotActionType.QUICK_CRAFT) {
			PlayerInventory playerInventory = this.minecraft.player.inventory;
			if (!playerInventory.getCursorStack().isEmpty() && this.lastClickOutsideBounds) {
				if (button == 0) {
					this.minecraft.player.dropItem(playerInventory.getCursorStack(), true);
					this.minecraft.interactionManager.dropCreativeStack(playerInventory.getCursorStack());
					playerInventory.setCursorStack(ItemStack.EMPTY);
				}

				if (button == 1) {
					ItemStack itemStack2 = playerInventory.getCursorStack().split(1);
					this.minecraft.player.dropItem(itemStack2, true);
					this.minecraft.interactionManager.dropCreativeStack(itemStack2);
				}
			}
		} else {
			if (slot != null && !slot.canTakeItems(this.minecraft.player)) {
				return;
			}

			if (slot == this.deleteItemSlot && bl) {
				for (int i = 0; i < this.minecraft.player.playerContainer.getStacks().size(); i++) {
					this.minecraft.interactionManager.clickCreativeStack(ItemStack.EMPTY, i);
				}
			} else if (selectedTab == ItemGroup.INVENTORY.getIndex()) {
				if (slot == this.deleteItemSlot) {
					this.minecraft.player.inventory.setCursorStack(ItemStack.EMPTY);
				} else if (slotActionType == SlotActionType.THROW && slot != null && slot.hasStack()) {
					ItemStack itemStack = slot.takeStack(button == 0 ? 1 : slot.getStack().getMaxCount());
					ItemStack itemStack2 = slot.getStack();
					this.minecraft.player.dropItem(itemStack, true);
					this.minecraft.interactionManager.dropCreativeStack(itemStack);
					this.minecraft.interactionManager.clickCreativeStack(itemStack2, ((CreativeInventoryScreen.CreativeSlot)slot).slot.id);
				} else if (slotActionType == SlotActionType.THROW && !this.minecraft.player.inventory.getCursorStack().isEmpty()) {
					this.minecraft.player.dropItem(this.minecraft.player.inventory.getCursorStack(), true);
					this.minecraft.interactionManager.dropCreativeStack(this.minecraft.player.inventory.getCursorStack());
					this.minecraft.player.inventory.setCursorStack(ItemStack.EMPTY);
				} else {
					this.minecraft
						.player
						.playerContainer
						.onSlotClick(slot == null ? invSlot : ((CreativeInventoryScreen.CreativeSlot)slot).slot.id, button, slotActionType, this.minecraft.player);
					this.minecraft.player.playerContainer.sendContentUpdates();
				}
			} else if (slotActionType != SlotActionType.QUICK_CRAFT && slot.inventory == inventory) {
				PlayerInventory playerInventory = this.minecraft.player.inventory;
				ItemStack itemStack2 = playerInventory.getCursorStack();
				ItemStack itemStack3 = slot.getStack();
				if (slotActionType == SlotActionType.SWAP) {
					if (!itemStack3.isEmpty() && button >= 0 && button < 9) {
						ItemStack itemStack4 = itemStack3.copy();
						itemStack4.setCount(itemStack4.getMaxCount());
						this.minecraft.player.inventory.setInvStack(button, itemStack4);
						this.minecraft.player.playerContainer.sendContentUpdates();
					}

					return;
				}

				if (slotActionType == SlotActionType.CLONE) {
					if (playerInventory.getCursorStack().isEmpty() && slot.hasStack()) {
						ItemStack itemStack4 = slot.getStack().copy();
						itemStack4.setCount(itemStack4.getMaxCount());
						playerInventory.setCursorStack(itemStack4);
					}

					return;
				}

				if (slotActionType == SlotActionType.THROW) {
					if (!itemStack3.isEmpty()) {
						ItemStack itemStack4 = itemStack3.copy();
						itemStack4.setCount(button == 0 ? 1 : itemStack4.getMaxCount());
						this.minecraft.player.dropItem(itemStack4, true);
						this.minecraft.interactionManager.dropCreativeStack(itemStack4);
					}

					return;
				}

				if (!itemStack2.isEmpty() && !itemStack3.isEmpty() && itemStack2.isItemEqualIgnoreDamage(itemStack3) && ItemStack.areTagsEqual(itemStack2, itemStack3)) {
					if (button == 0) {
						if (bl) {
							itemStack2.setCount(itemStack2.getMaxCount());
						} else if (itemStack2.getCount() < itemStack2.getMaxCount()) {
							itemStack2.increment(1);
						}
					} else {
						itemStack2.decrement(1);
					}
				} else if (!itemStack3.isEmpty() && itemStack2.isEmpty()) {
					playerInventory.setCursorStack(itemStack3.copy());
					itemStack2 = playerInventory.getCursorStack();
					if (bl) {
						itemStack2.setCount(itemStack2.getMaxCount());
					}
				} else if (button == 0) {
					playerInventory.setCursorStack(ItemStack.EMPTY);
				} else {
					playerInventory.getCursorStack().decrement(1);
				}
			} else if (this.container != null) {
				ItemStack itemStack = slot == null ? ItemStack.EMPTY : this.container.getSlot(slot.id).getStack();
				this.container.onSlotClick(slot == null ? invSlot : slot.id, button, slotActionType, this.minecraft.player);
				if (Container.unpackButtonId(button) == 2) {
					for (int j = 0; j < 9; j++) {
						this.minecraft.interactionManager.clickCreativeStack(this.container.getSlot(45 + j).getStack(), 36 + j);
					}
				} else if (slot != null) {
					ItemStack itemStack2x = this.container.getSlot(slot.id).getStack();
					this.minecraft.interactionManager.clickCreativeStack(itemStack2x, slot.id - this.container.slots.size() + 9 + 36);
					int k = 45 + button;
					if (slotActionType == SlotActionType.SWAP) {
						this.minecraft.interactionManager.clickCreativeStack(itemStack, k - this.container.slots.size() + 9 + 36);
					} else if (slotActionType == SlotActionType.THROW && !itemStack.isEmpty()) {
						ItemStack itemStack4 = itemStack.copy();
						itemStack4.setCount(button == 0 ? 1 : itemStack4.getMaxCount());
						this.minecraft.player.dropItem(itemStack4, true);
						this.minecraft.interactionManager.dropCreativeStack(itemStack4);
					}

					this.minecraft.player.playerContainer.sendContentUpdates();
				}
			}
		}
	}

	private boolean isCreativeInventorySlot(@Nullable Slot slot) {
		return slot != null && slot.inventory == inventory;
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
		if (this.minecraft.interactionManager.hasCreativeInventory()) {
			super.init();
			this.minecraft.keyboard.enableRepeatEvents(true);
			this.searchBox = new TextFieldWidget(this.font, this.x + 82, this.y + 6, 80, 9, I18n.translate("itemGroup.search"));
			this.searchBox.setMaxLength(50);
			this.searchBox.setHasBorder(false);
			this.searchBox.setVisible(false);
			this.searchBox.setEditableColor(16777215);
			this.children.add(this.searchBox);
			int i = selectedTab;
			selectedTab = -1;
			this.setSelectedTab(ItemGroup.GROUPS[i]);
			this.minecraft.player.playerContainer.removeListener(this.listener);
			this.listener = new CreativeInventoryListener(this.minecraft);
			this.minecraft.player.playerContainer.addListener(this.listener);
		} else {
			this.minecraft.openScreen(new InventoryScreen(this.minecraft.player));
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
		if (this.minecraft.player != null && this.minecraft.player.inventory != null) {
			this.minecraft.player.playerContainer.removeListener(this.listener);
		}

		this.minecraft.keyboard.enableRepeatEvents(false);
	}

	@Override
	public boolean charTyped(char chr, int keyCode) {
		if (this.ignoreTypedCharacter) {
			return false;
		} else if (selectedTab != ItemGroup.SEARCH.getIndex()) {
			return false;
		} else {
			String string = this.searchBox.getText();
			if (this.searchBox.charTyped(chr, keyCode)) {
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
		if (selectedTab != ItemGroup.SEARCH.getIndex()) {
			if (this.minecraft.options.keyChat.matchesKey(keyCode, scanCode)) {
				this.ignoreTypedCharacter = true;
				this.setSelectedTab(ItemGroup.SEARCH);
				return true;
			} else {
				return super.keyPressed(keyCode, scanCode, modifiers);
			}
		} else {
			boolean bl = !this.isCreativeInventorySlot(this.focusedSlot) || this.focusedSlot != null && this.focusedSlot.hasStack();
			if (bl && this.handleHotbarKeyPressed(keyCode, scanCode)) {
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
					return this.searchBox.isFocused() && this.searchBox.isVisible() && keyCode != 256 ? true : super.keyPressed(keyCode, scanCode, modifiers);
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
		this.container.itemList.clear();
		this.searchResultTags.clear();
		String string = this.searchBox.getText();
		if (string.isEmpty()) {
			for (Item item : Registry.ITEM) {
				item.appendStacks(ItemGroup.SEARCH, this.container.itemList);
			}
		} else {
			Searchable<ItemStack> searchable;
			if (string.startsWith("#")) {
				string = string.substring(1);
				searchable = this.minecraft.getSearchableContainer(SearchManager.ITEM_TAG);
				this.searchForTags(string);
			} else {
				searchable = this.minecraft.getSearchableContainer(SearchManager.ITEM_TOOLTIP);
			}

			this.container.itemList.addAll(searchable.findAll(string.toLowerCase(Locale.ROOT)));
		}

		this.scrollPosition = 0.0F;
		this.container.scrollItems(0.0F);
	}

	private void searchForTags(String string) {
		int i = string.indexOf(58);
		Predicate<Identifier> predicate;
		if (i == -1) {
			predicate = identifier -> identifier.getPath().contains(string);
		} else {
			String string2 = string.substring(0, i).trim();
			String string3 = string.substring(i + 1).trim();
			predicate = identifier -> identifier.getNamespace().contains(string2) && identifier.getPath().contains(string3);
		}

		TagContainer<Item> tagContainer = ItemTags.getContainer();
		tagContainer.getKeys().stream().filter(predicate).forEach(identifier -> {
			Tag var10000 = (Tag)this.searchResultTags.put(identifier, tagContainer.get(identifier));
		});
	}

	@Override
	protected void drawForeground(int mouseX, int mouseY) {
		ItemGroup itemGroup = ItemGroup.GROUPS[selectedTab];
		if (itemGroup.hasTooltip()) {
			RenderSystem.disableBlend();
			this.font.draw(I18n.translate(itemGroup.getTranslationKey()), 8.0F, 6.0F, 4210752);
		}
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (button == 0) {
			double d = mouseX - (double)this.x;
			double e = mouseY - (double)this.y;

			for (ItemGroup itemGroup : ItemGroup.GROUPS) {
				if (this.isClickInTab(itemGroup, d, e)) {
					return true;
				}
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
				if (this.isClickInTab(itemGroup, d, e)) {
					this.setSelectedTab(itemGroup);
					return true;
				}
			}
		}

		return super.mouseReleased(mouseX, mouseY, button);
	}

	private boolean hasScrollbar() {
		return selectedTab != ItemGroup.INVENTORY.getIndex() && ItemGroup.GROUPS[selectedTab].hasScrollbar() && this.container.shouldShowScrollbar();
	}

	private void setSelectedTab(ItemGroup group) {
		int i = selectedTab;
		selectedTab = group.getIndex();
		this.cursorDragSlots.clear();
		this.container.itemList.clear();
		if (group == ItemGroup.HOTBAR) {
			HotbarStorage hotbarStorage = this.minecraft.getCreativeHotbarStorage();

			for (int j = 0; j < 9; j++) {
				HotbarStorageEntry hotbarStorageEntry = hotbarStorage.getSavedHotbar(j);
				if (hotbarStorageEntry.isEmpty()) {
					for (int k = 0; k < 9; k++) {
						if (k == j) {
							ItemStack itemStack = new ItemStack(Items.PAPER);
							itemStack.getOrCreateSubTag("CustomCreativeLock");
							String string = this.minecraft.options.keysHotbar[j].getLocalizedName();
							String string2 = this.minecraft.options.keySaveToolbarActivator.getLocalizedName();
							itemStack.setCustomName(new TranslatableText("inventory.hotbarInfo", string2, string));
							this.container.itemList.add(itemStack);
						} else {
							this.container.itemList.add(ItemStack.EMPTY);
						}
					}
				} else {
					this.container.itemList.addAll(hotbarStorageEntry);
				}
			}
		} else if (group != ItemGroup.SEARCH) {
			group.appendStacks(this.container.itemList);
		}

		if (group == ItemGroup.INVENTORY) {
			Container container = this.minecraft.player.playerContainer;
			if (this.slots == null) {
				this.slots = ImmutableList.copyOf(this.container.slots);
			}

			this.container.slots.clear();

			for (int jx = 0; jx < container.slots.size(); jx++) {
				int o;
				int kx;
				if (jx >= 5 && jx < 9) {
					int l = jx - 5;
					int m = l / 2;
					int n = l % 2;
					o = 54 + m * 54;
					kx = 6 + n * 27;
				} else if (jx >= 0 && jx < 5) {
					o = -2000;
					kx = -2000;
				} else if (jx == 45) {
					o = 35;
					kx = 20;
				} else {
					int l = jx - 9;
					int m = l % 9;
					int n = l / 9;
					o = 9 + m * 18;
					if (jx >= 36) {
						kx = 112;
					} else {
						kx = 54 + n * 18;
					}
				}

				Slot slot = new CreativeInventoryScreen.CreativeSlot((Slot)container.slots.get(jx), jx, o, kx);
				this.container.slots.add(slot);
			}

			this.deleteItemSlot = new Slot(inventory, 0, 173, 112);
			this.container.slots.add(this.deleteItemSlot);
		} else if (i == ItemGroup.INVENTORY.getIndex()) {
			this.container.slots.clear();
			this.container.slots.addAll(this.slots);
			this.slots = null;
		}

		if (this.searchBox != null) {
			if (group == ItemGroup.SEARCH) {
				this.searchBox.setVisible(true);
				this.searchBox.setFocusUnlocked(false);
				this.searchBox.setSelected(true);
				if (i != group.getIndex()) {
					this.searchBox.setText("");
				}

				this.search();
			} else {
				this.searchBox.setVisible(false);
				this.searchBox.setFocusUnlocked(true);
				this.searchBox.setSelected(false);
				this.searchBox.setText("");
			}
		}

		this.scrollPosition = 0.0F;
		this.container.scrollItems(0.0F);
	}

	@Override
	public boolean mouseScrolled(double d, double e, double amount) {
		if (!this.hasScrollbar()) {
			return false;
		} else {
			int i = (this.container.itemList.size() + 9 - 1) / 9 - 5;
			this.scrollPosition = (float)((double)this.scrollPosition - amount / (double)i);
			this.scrollPosition = MathHelper.clamp(this.scrollPosition, 0.0F, 1.0F);
			this.container.scrollItems(this.scrollPosition);
			return true;
		}
	}

	@Override
	protected boolean isClickOutsideBounds(double mouseX, double mouseY, int left, int top, int button) {
		boolean bl = mouseX < (double)left
			|| mouseY < (double)top
			|| mouseX >= (double)(left + this.containerWidth)
			|| mouseY >= (double)(top + this.containerHeight);
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
			this.scrollPosition = ((float)mouseY - (float)i - 7.5F) / ((float)(j - i) - 15.0F);
			this.scrollPosition = MathHelper.clamp(this.scrollPosition, 0.0F, 1.0F);
			this.container.scrollItems(this.scrollPosition);
			return true;
		} else {
			return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
		}
	}

	@Override
	public void render(int mouseX, int mouseY, float delta) {
		this.renderBackground();
		super.render(mouseX, mouseY, delta);

		for (ItemGroup itemGroup : ItemGroup.GROUPS) {
			if (this.renderTabTooltipIfHovered(itemGroup, mouseX, mouseY)) {
				break;
			}
		}

		if (this.deleteItemSlot != null
			&& selectedTab == ItemGroup.INVENTORY.getIndex()
			&& this.isPointWithinBounds(this.deleteItemSlot.xPosition, this.deleteItemSlot.yPosition, 16, 16, (double)mouseX, (double)mouseY)) {
			this.renderTooltip(I18n.translate("inventory.binSlot"), mouseX, mouseY);
		}

		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.drawMouseoverTooltip(mouseX, mouseY);
	}

	@Override
	protected void renderTooltip(ItemStack stack, int x, int y) {
		if (selectedTab == ItemGroup.SEARCH.getIndex()) {
			List<Text> list = stack.getTooltip(
				this.minecraft.player, this.minecraft.options.advancedItemTooltips ? TooltipContext.Default.ADVANCED : TooltipContext.Default.NORMAL
			);
			List<String> list2 = Lists.<String>newArrayListWithCapacity(list.size());

			for (Text text : list) {
				list2.add(text.asFormattedString());
			}

			Item item = stack.getItem();
			ItemGroup itemGroup = item.getGroup();
			if (itemGroup == null && item == Items.ENCHANTED_BOOK) {
				Map<Enchantment, Integer> map = EnchantmentHelper.getEnchantments(stack);
				if (map.size() == 1) {
					Enchantment enchantment = (Enchantment)map.keySet().iterator().next();

					for (ItemGroup itemGroup2 : ItemGroup.GROUPS) {
						if (itemGroup2.containsEnchantments(enchantment.type)) {
							itemGroup = itemGroup2;
							break;
						}
					}
				}
			}

			this.searchResultTags.forEach((identifier, tag) -> {
				if (tag.contains(item)) {
					list2.add(1, "" + Formatting.BOLD + Formatting.DARK_PURPLE + "#" + identifier);
				}
			});
			if (itemGroup != null) {
				list2.add(1, "" + Formatting.BOLD + Formatting.BLUE + I18n.translate(itemGroup.getTranslationKey()));
			}

			for (int i = 0; i < list2.size(); i++) {
				if (i == 0) {
					list2.set(i, stack.getRarity().formatting + (String)list2.get(i));
				} else {
					list2.set(i, Formatting.GRAY + (String)list2.get(i));
				}
			}

			this.renderTooltip(list2, x, y);
		} else {
			super.renderTooltip(stack, x, y);
		}
	}

	@Override
	protected void drawBackground(float delta, int mouseX, int mouseY) {
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		ItemGroup itemGroup = ItemGroup.GROUPS[selectedTab];

		for (ItemGroup itemGroup2 : ItemGroup.GROUPS) {
			this.minecraft.getTextureManager().bindTexture(TEXTURE);
			if (itemGroup2.getIndex() != selectedTab) {
				this.renderTabIcon(itemGroup2);
			}
		}

		this.minecraft.getTextureManager().bindTexture(new Identifier("textures/gui/container/creative_inventory/tab_" + itemGroup.getTexture()));
		this.blit(this.x, this.y, 0, 0, this.containerWidth, this.containerHeight);
		this.searchBox.render(mouseX, mouseY, delta);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		int i = this.x + 175;
		int j = this.y + 18;
		int k = j + 112;
		this.minecraft.getTextureManager().bindTexture(TEXTURE);
		if (itemGroup.hasScrollbar()) {
			this.blit(i, j + (int)((float)(k - j - 17) * this.scrollPosition), 232 + (this.hasScrollbar() ? 0 : 12), 0, 12, 15);
		}

		this.renderTabIcon(itemGroup);
		if (itemGroup == ItemGroup.INVENTORY) {
			InventoryScreen.drawEntity(this.x + 88, this.y + 45, 20, (float)(this.x + 88 - mouseX), (float)(this.y + 45 - 30 - mouseY), this.minecraft.player);
		}
	}

	protected boolean isClickInTab(ItemGroup group, double mouseX, double mouseY) {
		int i = group.getColumn();
		int j = 28 * i;
		int k = 0;
		if (group.isSpecial()) {
			j = this.containerWidth - 28 * (6 - i) + 2;
		} else if (i > 0) {
			j += i;
		}

		if (group.isTopRow()) {
			k -= 32;
		} else {
			k += this.containerHeight;
		}

		return mouseX >= (double)j && mouseX <= (double)(j + 28) && mouseY >= (double)k && mouseY <= (double)(k + 32);
	}

	protected boolean renderTabTooltipIfHovered(ItemGroup group, int mouseX, int mouseY) {
		int i = group.getColumn();
		int j = 28 * i;
		int k = 0;
		if (group.isSpecial()) {
			j = this.containerWidth - 28 * (6 - i) + 2;
		} else if (i > 0) {
			j += i;
		}

		if (group.isTopRow()) {
			k -= 32;
		} else {
			k += this.containerHeight;
		}

		if (this.isPointWithinBounds(j + 3, k + 3, 23, 27, (double)mouseX, (double)mouseY)) {
			this.renderTooltip(I18n.translate(group.getTranslationKey()), mouseX, mouseY);
			return true;
		} else {
			return false;
		}
	}

	protected void renderTabIcon(ItemGroup group) {
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
			l = this.x + this.containerWidth - 28 * (6 - i);
		} else if (i > 0) {
			l += i;
		}

		if (bl2) {
			m -= 28;
		} else {
			k += 64;
			m += this.containerHeight - 4;
		}

		this.blit(l, m, j, k, 28, 32);
		this.setBlitOffset(100);
		this.itemRenderer.zOffset = 100.0F;
		l += 6;
		m += 8 + (bl2 ? 1 : -1);
		RenderSystem.enableRescaleNormal();
		ItemStack itemStack = group.getIcon();
		this.itemRenderer.renderGuiItem(itemStack, l, m);
		this.itemRenderer.renderGuiItemOverlay(this.font, itemStack, l, m);
		this.itemRenderer.zOffset = 0.0F;
		this.setBlitOffset(0);
	}

	public int getSelectedTab() {
		return selectedTab;
	}

	public static void onHotbarKeyPress(MinecraftClient client, int index, boolean restore, boolean save) {
		ClientPlayerEntity clientPlayerEntity = client.player;
		HotbarStorage hotbarStorage = client.getCreativeHotbarStorage();
		HotbarStorageEntry hotbarStorageEntry = hotbarStorage.getSavedHotbar(index);
		if (restore) {
			for (int i = 0; i < PlayerInventory.getHotbarSize(); i++) {
				ItemStack itemStack = hotbarStorageEntry.get(i).copy();
				clientPlayerEntity.inventory.setInvStack(i, itemStack);
				client.interactionManager.clickCreativeStack(itemStack, 36 + i);
			}

			clientPlayerEntity.playerContainer.sendContentUpdates();
		} else if (save) {
			for (int i = 0; i < PlayerInventory.getHotbarSize(); i++) {
				hotbarStorageEntry.set(i, clientPlayerEntity.inventory.getInvStack(i).copy());
			}

			String string = client.options.keysHotbar[index].getLocalizedName();
			String string2 = client.options.keyLoadToolbarActivator.getLocalizedName();
			client.inGameHud.setOverlayMessage(new TranslatableText("inventory.hotbarSaved", string2, string), false);
			hotbarStorage.save();
		}
	}

	@Environment(EnvType.CLIENT)
	public static class CreativeContainer extends Container {
		public final DefaultedList<ItemStack> itemList = DefaultedList.of();

		public CreativeContainer(PlayerEntity playerEntity) {
			super(null, 0);
			PlayerInventory playerInventory = playerEntity.inventory;

			for (int i = 0; i < 5; i++) {
				for (int j = 0; j < 9; j++) {
					this.addSlot(new CreativeInventoryScreen.LockableSlot(CreativeInventoryScreen.inventory, i * 9 + j, 9 + j * 18, 18 + i * 18));
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

		public void scrollItems(float position) {
			int i = (this.itemList.size() + 9 - 1) / 9 - 5;
			int j = (int)((double)(position * (float)i) + 0.5);
			if (j < 0) {
				j = 0;
			}

			for (int k = 0; k < 5; k++) {
				for (int l = 0; l < 9; l++) {
					int m = l + (k + j) * 9;
					if (m >= 0 && m < this.itemList.size()) {
						CreativeInventoryScreen.inventory.setInvStack(l + k * 9, this.itemList.get(m));
					} else {
						CreativeInventoryScreen.inventory.setInvStack(l + k * 9, ItemStack.EMPTY);
					}
				}
			}
		}

		public boolean shouldShowScrollbar() {
			return this.itemList.size() > 45;
		}

		@Override
		public ItemStack transferSlot(PlayerEntity player, int invSlot) {
			if (invSlot >= this.slots.size() - 9 && invSlot < this.slots.size()) {
				Slot slot = (Slot)this.slots.get(invSlot);
				if (slot != null && slot.hasStack()) {
					slot.setStack(ItemStack.EMPTY);
				}
			}

			return ItemStack.EMPTY;
		}

		@Override
		public boolean canInsertIntoSlot(ItemStack stack, Slot slot) {
			return slot.inventory != CreativeInventoryScreen.inventory;
		}

		@Override
		public boolean canInsertIntoSlot(Slot slot) {
			return slot.inventory != CreativeInventoryScreen.inventory;
		}
	}

	@Environment(EnvType.CLIENT)
	static class CreativeSlot extends Slot {
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
		public void setStack(ItemStack itemStack) {
			this.slot.setStack(itemStack);
		}

		@Override
		public void markDirty() {
			this.slot.markDirty();
		}

		@Override
		public int getMaxStackAmount() {
			return this.slot.getMaxStackAmount();
		}

		@Override
		public int getMaxStackAmount(ItemStack itemStack) {
			return this.slot.getMaxStackAmount(itemStack);
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
		public boolean doDrawHoveringEffect() {
			return this.slot.doDrawHoveringEffect();
		}

		@Override
		public boolean canTakeItems(PlayerEntity playerEntity) {
			return this.slot.canTakeItems(playerEntity);
		}
	}

	@Environment(EnvType.CLIENT)
	static class LockableSlot extends Slot {
		public LockableSlot(Inventory invSlot, int xPosition, int yPosition, int i) {
			super(invSlot, xPosition, yPosition, i);
		}

		@Override
		public boolean canTakeItems(PlayerEntity playerEntity) {
			return super.canTakeItems(playerEntity) && this.hasStack() ? this.getStack().getSubTag("CustomCreativeLock") == null : !this.hasStack();
		}
	}
}
