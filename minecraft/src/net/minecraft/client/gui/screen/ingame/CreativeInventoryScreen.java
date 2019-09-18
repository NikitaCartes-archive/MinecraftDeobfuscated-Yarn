package net.minecraft.client.gui.screen.ingame;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.systems.RenderSystem;
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
import net.minecraft.client.render.GuiLighting;
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
	private List<Slot> slots;
	private Slot deleteItemSlot;
	private CreativeInventoryListener listener;
	private boolean ignoreTypedCharacter;
	private boolean lastClickOutsideBounds;
	private final Map<Identifier, Tag<Item>> searchResultTags = Maps.<Identifier, Tag<Item>>newTreeMap();

	public CreativeInventoryScreen(PlayerEntity playerEntity) {
		super(new CreativeInventoryScreen.CreativeContainer(playerEntity), playerEntity.inventory, new LiteralText(""));
		playerEntity.container = this.container;
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
	protected void onMouseClick(@Nullable Slot slot, int i, int j, SlotActionType slotActionType) {
		if (this.isCreativeInventorySlot(slot)) {
			this.searchBox.setCursorToEnd();
			this.searchBox.setSelectionEnd(0);
		}

		boolean bl = slotActionType == SlotActionType.QUICK_MOVE;
		slotActionType = i == -999 && slotActionType == SlotActionType.PICKUP ? SlotActionType.THROW : slotActionType;
		if (slot == null && selectedTab != ItemGroup.INVENTORY.getIndex() && slotActionType != SlotActionType.QUICK_CRAFT) {
			PlayerInventory playerInventory = this.minecraft.player.inventory;
			if (!playerInventory.getCursorStack().isEmpty() && this.lastClickOutsideBounds) {
				if (j == 0) {
					this.minecraft.player.dropItem(playerInventory.getCursorStack(), true);
					this.minecraft.interactionManager.dropCreativeStack(playerInventory.getCursorStack());
					playerInventory.setCursorStack(ItemStack.EMPTY);
				}

				if (j == 1) {
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
				for (int k = 0; k < this.minecraft.player.playerContainer.getStacks().size(); k++) {
					this.minecraft.interactionManager.clickCreativeStack(ItemStack.EMPTY, k);
				}
			} else if (selectedTab == ItemGroup.INVENTORY.getIndex()) {
				if (slot == this.deleteItemSlot) {
					this.minecraft.player.inventory.setCursorStack(ItemStack.EMPTY);
				} else if (slotActionType == SlotActionType.THROW && slot != null && slot.hasStack()) {
					ItemStack itemStack = slot.takeStack(j == 0 ? 1 : slot.getStack().getMaxCount());
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
						.onSlotClick(slot == null ? i : ((CreativeInventoryScreen.CreativeSlot)slot).slot.id, j, slotActionType, this.minecraft.player);
					this.minecraft.player.playerContainer.sendContentUpdates();
				}
			} else if (slotActionType != SlotActionType.QUICK_CRAFT && slot.inventory == inventory) {
				PlayerInventory playerInventory = this.minecraft.player.inventory;
				ItemStack itemStack2 = playerInventory.getCursorStack();
				ItemStack itemStack3 = slot.getStack();
				if (slotActionType == SlotActionType.SWAP) {
					if (!itemStack3.isEmpty() && j >= 0 && j < 9) {
						ItemStack itemStack4 = itemStack3.copy();
						itemStack4.setCount(itemStack4.getMaxCount());
						this.minecraft.player.inventory.setInvStack(j, itemStack4);
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
						itemStack4.setCount(j == 0 ? 1 : itemStack4.getMaxCount());
						this.minecraft.player.dropItem(itemStack4, true);
						this.minecraft.interactionManager.dropCreativeStack(itemStack4);
					}

					return;
				}

				if (!itemStack2.isEmpty() && !itemStack3.isEmpty() && itemStack2.isItemEqualIgnoreDamage(itemStack3) && ItemStack.areTagsEqual(itemStack2, itemStack3)) {
					if (j == 0) {
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
				} else if (j == 0) {
					playerInventory.setCursorStack(ItemStack.EMPTY);
				} else {
					playerInventory.getCursorStack().decrement(1);
				}
			} else if (this.container != null) {
				ItemStack itemStack = slot == null ? ItemStack.EMPTY : this.container.getSlot(slot.id).getStack();
				this.container.onSlotClick(slot == null ? i : slot.id, j, slotActionType, this.minecraft.player);
				if (Container.unpackButtonId(j) == 2) {
					for (int l = 0; l < 9; l++) {
						this.minecraft.interactionManager.clickCreativeStack(this.container.getSlot(45 + l).getStack(), 36 + l);
					}
				} else if (slot != null) {
					ItemStack itemStack2x = this.container.getSlot(slot.id).getStack();
					this.minecraft.interactionManager.clickCreativeStack(itemStack2x, slot.id - this.container.slotList.size() + 9 + 36);
					int m = 45 + j;
					if (slotActionType == SlotActionType.SWAP) {
						this.minecraft.interactionManager.clickCreativeStack(itemStack, m - this.container.slotList.size() + 9 + 36);
					} else if (slotActionType == SlotActionType.THROW && !itemStack.isEmpty()) {
						ItemStack itemStack4 = itemStack.copy();
						itemStack4.setCount(j == 0 ? 1 : itemStack4.getMaxCount());
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
		int i = this.left;
		super.applyStatusEffectOffset();
		if (this.searchBox != null && this.left != i) {
			this.searchBox.setX(this.left + 82);
		}
	}

	@Override
	protected void init() {
		if (this.minecraft.interactionManager.hasCreativeInventory()) {
			super.init();
			this.minecraft.keyboard.enableRepeatEvents(true);
			this.searchBox = new TextFieldWidget(this.font, this.left + 82, this.top + 6, 80, 9, I18n.translate("itemGroup.search"));
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
	public void resize(MinecraftClient minecraftClient, int i, int j) {
		String string = this.searchBox.getText();
		this.init(minecraftClient, i, j);
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
	public boolean charTyped(char c, int i) {
		if (this.ignoreTypedCharacter) {
			return false;
		} else if (selectedTab != ItemGroup.SEARCH.getIndex()) {
			return false;
		} else {
			String string = this.searchBox.getText();
			if (this.searchBox.charTyped(c, i)) {
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
	public boolean keyPressed(int i, int j, int k) {
		this.ignoreTypedCharacter = false;
		if (selectedTab != ItemGroup.SEARCH.getIndex()) {
			if (this.minecraft.options.keyChat.matchesKey(i, j)) {
				this.ignoreTypedCharacter = true;
				this.setSelectedTab(ItemGroup.SEARCH);
				return true;
			} else {
				return super.keyPressed(i, j, k);
			}
		} else {
			boolean bl = !this.isCreativeInventorySlot(this.focusedSlot) || this.focusedSlot != null && this.focusedSlot.hasStack();
			if (bl && this.handleHotbarKeyPressed(i, j)) {
				this.ignoreTypedCharacter = true;
				return true;
			} else {
				String string = this.searchBox.getText();
				if (this.searchBox.keyPressed(i, j, k)) {
					if (!Objects.equals(string, this.searchBox.getText())) {
						this.search();
					}

					return true;
				} else {
					return this.searchBox.isFocused() && this.searchBox.isVisible() && i != 256 ? true : super.keyPressed(i, j, k);
				}
			}
		}
	}

	@Override
	public boolean keyReleased(int i, int j, int k) {
		this.ignoreTypedCharacter = false;
		return super.keyReleased(i, j, k);
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
	protected void drawForeground(int i, int j) {
		ItemGroup itemGroup = ItemGroup.GROUPS[selectedTab];
		if (itemGroup.hasTooltip()) {
			RenderSystem.disableBlend();
			this.font.draw(I18n.translate(itemGroup.getTranslationKey()), 8.0F, 6.0F, 4210752);
		}
	}

	@Override
	public boolean mouseClicked(double d, double e, int i) {
		if (i == 0) {
			double f = d - (double)this.left;
			double g = e - (double)this.top;

			for (ItemGroup itemGroup : ItemGroup.GROUPS) {
				if (this.isClickInTab(itemGroup, f, g)) {
					return true;
				}
			}

			if (selectedTab != ItemGroup.INVENTORY.getIndex() && this.isClickInScrollbar(d, e)) {
				this.scrolling = this.hasScrollbar();
				return true;
			}
		}

		return super.mouseClicked(d, e, i);
	}

	@Override
	public boolean mouseReleased(double d, double e, int i) {
		if (i == 0) {
			double f = d - (double)this.left;
			double g = e - (double)this.top;
			this.scrolling = false;

			for (ItemGroup itemGroup : ItemGroup.GROUPS) {
				if (this.isClickInTab(itemGroup, f, g)) {
					this.setSelectedTab(itemGroup);
					return true;
				}
			}
		}

		return super.mouseReleased(d, e, i);
	}

	private boolean hasScrollbar() {
		return selectedTab != ItemGroup.INVENTORY.getIndex() && ItemGroup.GROUPS[selectedTab].hasScrollbar() && this.container.shouldShowScrollbar();
	}

	private void setSelectedTab(ItemGroup itemGroup) {
		int i = selectedTab;
		selectedTab = itemGroup.getIndex();
		this.cursorDragSlots.clear();
		this.container.itemList.clear();
		if (itemGroup == ItemGroup.HOTBAR) {
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
		} else if (itemGroup != ItemGroup.SEARCH) {
			itemGroup.appendStacks(this.container.itemList);
		}

		if (itemGroup == ItemGroup.INVENTORY) {
			Container container = this.minecraft.player.playerContainer;
			if (this.slots == null) {
				this.slots = ImmutableList.copyOf(this.container.slotList);
			}

			this.container.slotList.clear();

			for (int jx = 0; jx < container.slotList.size(); jx++) {
				Slot slot = new CreativeInventoryScreen.CreativeSlot((Slot)container.slotList.get(jx), jx);
				this.container.slotList.add(slot);
				if (jx >= 5 && jx < 9) {
					int kx = jx - 5;
					int l = kx / 2;
					int m = kx % 2;
					slot.xPosition = 54 + l * 54;
					slot.yPosition = 6 + m * 27;
				} else if (jx >= 0 && jx < 5) {
					slot.xPosition = -2000;
					slot.yPosition = -2000;
				} else if (jx == 45) {
					slot.xPosition = 35;
					slot.yPosition = 20;
				} else if (jx < container.slotList.size()) {
					int kx = jx - 9;
					int l = kx % 9;
					int m = kx / 9;
					slot.xPosition = 9 + l * 18;
					if (jx >= 36) {
						slot.yPosition = 112;
					} else {
						slot.yPosition = 54 + m * 18;
					}
				}
			}

			this.deleteItemSlot = new Slot(inventory, 0, 173, 112);
			this.container.slotList.add(this.deleteItemSlot);
		} else if (i == ItemGroup.INVENTORY.getIndex()) {
			this.container.slotList.clear();
			this.container.slotList.addAll(this.slots);
			this.slots = null;
		}

		if (this.searchBox != null) {
			if (itemGroup == ItemGroup.SEARCH) {
				this.searchBox.setVisible(true);
				this.searchBox.setFocusUnlocked(false);
				this.searchBox.setSelected(true);
				if (i != itemGroup.getIndex()) {
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
	public boolean mouseScrolled(double d, double e, double f) {
		if (!this.hasScrollbar()) {
			return false;
		} else {
			int i = (this.container.itemList.size() + 9 - 1) / 9 - 5;
			this.scrollPosition = (float)((double)this.scrollPosition - f / (double)i);
			this.scrollPosition = MathHelper.clamp(this.scrollPosition, 0.0F, 1.0F);
			this.container.scrollItems(this.scrollPosition);
			return true;
		}
	}

	@Override
	protected boolean isClickOutsideBounds(double d, double e, int i, int j, int k) {
		boolean bl = d < (double)i || e < (double)j || d >= (double)(i + this.containerWidth) || e >= (double)(j + this.containerHeight);
		this.lastClickOutsideBounds = bl && !this.isClickInTab(ItemGroup.GROUPS[selectedTab], d, e);
		return this.lastClickOutsideBounds;
	}

	protected boolean isClickInScrollbar(double d, double e) {
		int i = this.left;
		int j = this.top;
		int k = i + 175;
		int l = j + 18;
		int m = k + 14;
		int n = l + 112;
		return d >= (double)k && e >= (double)l && d < (double)m && e < (double)n;
	}

	@Override
	public boolean mouseDragged(double d, double e, int i, double f, double g) {
		if (this.scrolling) {
			int j = this.top + 18;
			int k = j + 112;
			this.scrollPosition = ((float)e - (float)j - 7.5F) / ((float)(k - j) - 15.0F);
			this.scrollPosition = MathHelper.clamp(this.scrollPosition, 0.0F, 1.0F);
			this.container.scrollItems(this.scrollPosition);
			return true;
		} else {
			return super.mouseDragged(d, e, i, f, g);
		}
	}

	@Override
	public void render(int i, int j, float f) {
		this.renderBackground();
		super.render(i, j, f);

		for (ItemGroup itemGroup : ItemGroup.GROUPS) {
			if (this.renderTabTooltipIfHovered(itemGroup, i, j)) {
				break;
			}
		}

		if (this.deleteItemSlot != null
			&& selectedTab == ItemGroup.INVENTORY.getIndex()
			&& this.isPointWithinBounds(this.deleteItemSlot.xPosition, this.deleteItemSlot.yPosition, 16, 16, (double)i, (double)j)) {
			this.renderTooltip(I18n.translate("inventory.binSlot"), i, j);
		}

		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.disableLighting();
		this.drawMouseoverTooltip(i, j);
	}

	@Override
	protected void renderTooltip(ItemStack itemStack, int i, int j) {
		if (selectedTab == ItemGroup.SEARCH.getIndex()) {
			List<Text> list = itemStack.getTooltip(
				this.minecraft.player, this.minecraft.options.advancedItemTooltips ? TooltipContext.Default.ADVANCED : TooltipContext.Default.NORMAL
			);
			List<String> list2 = Lists.<String>newArrayListWithCapacity(list.size());

			for (Text text : list) {
				list2.add(text.asFormattedString());
			}

			Item item = itemStack.getItem();
			ItemGroup itemGroup = item.getGroup();
			if (itemGroup == null && item == Items.ENCHANTED_BOOK) {
				Map<Enchantment, Integer> map = EnchantmentHelper.getEnchantments(itemStack);
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

			for (int k = 0; k < list2.size(); k++) {
				if (k == 0) {
					list2.set(k, itemStack.getRarity().formatting + (String)list2.get(k));
				} else {
					list2.set(k, Formatting.GRAY + (String)list2.get(k));
				}
			}

			this.renderTooltip(list2, i, j);
		} else {
			super.renderTooltip(itemStack, i, j);
		}
	}

	@Override
	protected void drawBackground(float f, int i, int j) {
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		GuiLighting.enableForItems();
		ItemGroup itemGroup = ItemGroup.GROUPS[selectedTab];

		for (ItemGroup itemGroup2 : ItemGroup.GROUPS) {
			this.minecraft.getTextureManager().bindTexture(TEXTURE);
			if (itemGroup2.getIndex() != selectedTab) {
				this.renderTabIcon(itemGroup2);
			}
		}

		this.minecraft.getTextureManager().bindTexture(new Identifier("textures/gui/container/creative_inventory/tab_" + itemGroup.getTexture()));
		this.blit(this.left, this.top, 0, 0, this.containerWidth, this.containerHeight);
		this.searchBox.render(i, j, f);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		int k = this.left + 175;
		int l = this.top + 18;
		int m = l + 112;
		this.minecraft.getTextureManager().bindTexture(TEXTURE);
		if (itemGroup.hasScrollbar()) {
			this.blit(k, l + (int)((float)(m - l - 17) * this.scrollPosition), 232 + (this.hasScrollbar() ? 0 : 12), 0, 12, 15);
		}

		this.renderTabIcon(itemGroup);
		if (itemGroup == ItemGroup.INVENTORY) {
			InventoryScreen.drawEntity(this.left + 88, this.top + 45, 20, (float)(this.left + 88 - i), (float)(this.top + 45 - 30 - j), this.minecraft.player);
		}
	}

	protected boolean isClickInTab(ItemGroup itemGroup, double d, double e) {
		int i = itemGroup.getColumn();
		int j = 28 * i;
		int k = 0;
		if (itemGroup.isSpecial()) {
			j = this.containerWidth - 28 * (6 - i) + 2;
		} else if (i > 0) {
			j += i;
		}

		if (itemGroup.isTopRow()) {
			k -= 32;
		} else {
			k += this.containerHeight;
		}

		return d >= (double)j && d <= (double)(j + 28) && e >= (double)k && e <= (double)(k + 32);
	}

	protected boolean renderTabTooltipIfHovered(ItemGroup itemGroup, int i, int j) {
		int k = itemGroup.getColumn();
		int l = 28 * k;
		int m = 0;
		if (itemGroup.isSpecial()) {
			l = this.containerWidth - 28 * (6 - k) + 2;
		} else if (k > 0) {
			l += k;
		}

		if (itemGroup.isTopRow()) {
			m -= 32;
		} else {
			m += this.containerHeight;
		}

		if (this.isPointWithinBounds(l + 3, m + 3, 23, 27, (double)i, (double)j)) {
			this.renderTooltip(I18n.translate(itemGroup.getTranslationKey()), i, j);
			return true;
		} else {
			return false;
		}
	}

	protected void renderTabIcon(ItemGroup itemGroup) {
		boolean bl = itemGroup.getIndex() == selectedTab;
		boolean bl2 = itemGroup.isTopRow();
		int i = itemGroup.getColumn();
		int j = i * 28;
		int k = 0;
		int l = this.left + 28 * i;
		int m = this.top;
		int n = 32;
		if (bl) {
			k += 32;
		}

		if (itemGroup.isSpecial()) {
			l = this.left + this.containerWidth - 28 * (6 - i);
		} else if (i > 0) {
			l += i;
		}

		if (bl2) {
			m -= 28;
		} else {
			k += 64;
			m += this.containerHeight - 4;
		}

		RenderSystem.disableLighting();
		this.blit(l, m, j, k, 28, 32);
		this.setBlitOffset(100);
		this.itemRenderer.zOffset = 100.0F;
		l += 6;
		m += 8 + (bl2 ? 1 : -1);
		RenderSystem.enableLighting();
		RenderSystem.enableRescaleNormal();
		ItemStack itemStack = itemGroup.getIcon();
		this.itemRenderer.renderGuiItem(itemStack, l, m);
		this.itemRenderer.renderGuiItemOverlay(this.font, itemStack, l, m);
		RenderSystem.disableLighting();
		this.itemRenderer.zOffset = 0.0F;
		this.setBlitOffset(0);
	}

	public int getSelectedTab() {
		return selectedTab;
	}

	public static void onHotbarKeyPress(MinecraftClient minecraftClient, int i, boolean bl, boolean bl2) {
		ClientPlayerEntity clientPlayerEntity = minecraftClient.player;
		HotbarStorage hotbarStorage = minecraftClient.getCreativeHotbarStorage();
		HotbarStorageEntry hotbarStorageEntry = hotbarStorage.getSavedHotbar(i);
		if (bl) {
			for (int j = 0; j < PlayerInventory.getHotbarSize(); j++) {
				ItemStack itemStack = hotbarStorageEntry.get(j).copy();
				clientPlayerEntity.inventory.setInvStack(j, itemStack);
				minecraftClient.interactionManager.clickCreativeStack(itemStack, 36 + j);
			}

			clientPlayerEntity.playerContainer.sendContentUpdates();
		} else if (bl2) {
			for (int j = 0; j < PlayerInventory.getHotbarSize(); j++) {
				hotbarStorageEntry.set(j, clientPlayerEntity.inventory.getInvStack(j).copy());
			}

			String string = minecraftClient.options.keysHotbar[i].getLocalizedName();
			String string2 = minecraftClient.options.keyLoadToolbarActivator.getLocalizedName();
			minecraftClient.inGameHud.setOverlayMessage(new TranslatableText("inventory.hotbarSaved", string2, string), false);
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
		public boolean canUse(PlayerEntity playerEntity) {
			return true;
		}

		public void scrollItems(float f) {
			int i = (this.itemList.size() + 9 - 1) / 9 - 5;
			int j = (int)((double)(f * (float)i) + 0.5);
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
		public ItemStack transferSlot(PlayerEntity playerEntity, int i) {
			if (i >= this.slotList.size() - 9 && i < this.slotList.size()) {
				Slot slot = (Slot)this.slotList.get(i);
				if (slot != null && slot.hasStack()) {
					slot.setStack(ItemStack.EMPTY);
				}
			}

			return ItemStack.EMPTY;
		}

		@Override
		public boolean canInsertIntoSlot(ItemStack itemStack, Slot slot) {
			return slot.inventory != CreativeInventoryScreen.inventory;
		}

		@Override
		public boolean canInsertIntoSlot(Slot slot) {
			return slot.inventory != CreativeInventoryScreen.inventory;
		}
	}

	@Environment(EnvType.CLIENT)
	class CreativeSlot extends Slot {
		private final Slot slot;

		public CreativeSlot(Slot slot, int i) {
			super(slot.inventory, i, 0, 0);
			this.slot = slot;
		}

		@Override
		public ItemStack onTakeItem(PlayerEntity playerEntity, ItemStack itemStack) {
			this.slot.onTakeItem(playerEntity, itemStack);
			return itemStack;
		}

		@Override
		public boolean canInsert(ItemStack itemStack) {
			return this.slot.canInsert(itemStack);
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
		public String getBackgroundSprite() {
			return this.slot.getBackgroundSprite();
		}

		@Override
		public ItemStack takeStack(int i) {
			return this.slot.takeStack(i);
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
		public LockableSlot(Inventory inventory, int i, int j, int k) {
			super(inventory, i, j, k);
		}

		@Override
		public boolean canTakeItems(PlayerEntity playerEntity) {
			return super.canTakeItems(playerEntity) && this.hasStack() ? this.getStack().getSubTag("CustomCreativeLock") == null : !this.hasStack();
		}
	}
}
