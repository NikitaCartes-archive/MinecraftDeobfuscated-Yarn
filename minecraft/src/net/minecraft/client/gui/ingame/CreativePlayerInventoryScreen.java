package net.minecraft.client.gui.ingame;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_478;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.item.TooltipOptions;
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
import net.minecraft.text.StringTextComponent;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TextFormat;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;

@Environment(EnvType.CLIENT)
public class CreativePlayerInventoryScreen extends AbstractPlayerInventoryScreen<CreativePlayerInventoryScreen.CreativeContainer> {
	private static final Identifier TEXTURE = new Identifier("textures/gui/container/creative_inventory/tabs.png");
	private static final BasicInventory inventory = new BasicInventory(45);
	private static int selectedTab = ItemGroup.BUILDING_BLOCKS.getId();
	private float scrollPosition;
	private boolean field_2892;
	private TextFieldWidget searchBox;
	private List<Slot> slots;
	private Slot deleteItemSlot;
	private class_478 field_2891;
	private boolean field_2888;
	private boolean field_2887;
	private final Map<Identifier, Tag<Item>> field_16201 = Maps.<Identifier, Tag<Item>>newTreeMap();

	public CreativePlayerInventoryScreen(PlayerEntity playerEntity) {
		super(new CreativePlayerInventoryScreen.CreativeContainer(playerEntity), playerEntity.inventory, new StringTextComponent(""));
		playerEntity.container = this.container;
		this.field_2558 = true;
		this.containerHeight = 136;
		this.containerWidth = 195;
	}

	@Override
	public void update() {
		if (!this.client.interactionManager.hasCreativeInventory()) {
			this.client.openScreen(new PlayerInventoryScreen(this.client.player));
		} else if (this.searchBox != null) {
			this.searchBox.tick();
		}
	}

	@Override
	protected void onMouseClick(@Nullable Slot slot, int i, int j, SlotActionType slotActionType) {
		if (this.method_2470(slot)) {
			this.searchBox.method_1872();
			this.searchBox.method_1884(0);
		}

		boolean bl = slotActionType == SlotActionType.field_7794;
		slotActionType = i == -999 && slotActionType == SlotActionType.field_7790 ? SlotActionType.field_7795 : slotActionType;
		if (slot == null && selectedTab != ItemGroup.INVENTORY.getId() && slotActionType != SlotActionType.field_7789) {
			PlayerInventory playerInventory = this.client.player.inventory;
			if (!playerInventory.getCursorStack().isEmpty() && this.field_2887) {
				if (j == 0) {
					this.client.player.dropItem(playerInventory.getCursorStack(), true);
					this.client.interactionManager.method_2915(playerInventory.getCursorStack());
					playerInventory.setCursorStack(ItemStack.EMPTY);
				}

				if (j == 1) {
					ItemStack itemStack2 = playerInventory.getCursorStack().split(1);
					this.client.player.dropItem(itemStack2, true);
					this.client.interactionManager.method_2915(itemStack2);
				}
			}
		} else {
			if (slot != null && !slot.canTakeItems(this.client.player)) {
				return;
			}

			if (slot == this.deleteItemSlot && bl) {
				for (int k = 0; k < this.client.player.containerPlayer.getStacks().size(); k++) {
					this.client.interactionManager.method_2909(ItemStack.EMPTY, k);
				}
			} else if (selectedTab == ItemGroup.INVENTORY.getId()) {
				if (slot == this.deleteItemSlot) {
					this.client.player.inventory.setCursorStack(ItemStack.EMPTY);
				} else if (slotActionType == SlotActionType.field_7795 && slot != null && slot.hasStack()) {
					ItemStack itemStack = slot.takeStack(j == 0 ? 1 : slot.getStack().getMaxAmount());
					ItemStack itemStack2 = slot.getStack();
					this.client.player.dropItem(itemStack, true);
					this.client.interactionManager.method_2915(itemStack);
					this.client.interactionManager.method_2909(itemStack2, ((CreativePlayerInventoryScreen.CreativeSlot)slot).slot.id);
				} else if (slotActionType == SlotActionType.field_7795 && !this.client.player.inventory.getCursorStack().isEmpty()) {
					this.client.player.dropItem(this.client.player.inventory.getCursorStack(), true);
					this.client.interactionManager.method_2915(this.client.player.inventory.getCursorStack());
					this.client.player.inventory.setCursorStack(ItemStack.EMPTY);
				} else {
					this.client
						.player
						.containerPlayer
						.onSlotClick(slot == null ? i : ((CreativePlayerInventoryScreen.CreativeSlot)slot).slot.id, j, slotActionType, this.client.player);
					this.client.player.containerPlayer.sendContentUpdates();
				}
			} else if (slotActionType != SlotActionType.field_7789 && slot.inventory == inventory) {
				PlayerInventory playerInventory = this.client.player.inventory;
				ItemStack itemStack2 = playerInventory.getCursorStack();
				ItemStack itemStack3 = slot.getStack();
				if (slotActionType == SlotActionType.field_7791) {
					if (!itemStack3.isEmpty() && j >= 0 && j < 9) {
						ItemStack itemStack4 = itemStack3.copy();
						itemStack4.setAmount(itemStack4.getMaxAmount());
						this.client.player.inventory.setInvStack(j, itemStack4);
						this.client.player.containerPlayer.sendContentUpdates();
					}

					return;
				}

				if (slotActionType == SlotActionType.field_7796) {
					if (playerInventory.getCursorStack().isEmpty() && slot.hasStack()) {
						ItemStack itemStack4 = slot.getStack().copy();
						itemStack4.setAmount(itemStack4.getMaxAmount());
						playerInventory.setCursorStack(itemStack4);
					}

					return;
				}

				if (slotActionType == SlotActionType.field_7795) {
					if (!itemStack3.isEmpty()) {
						ItemStack itemStack4 = itemStack3.copy();
						itemStack4.setAmount(j == 0 ? 1 : itemStack4.getMaxAmount());
						this.client.player.dropItem(itemStack4, true);
						this.client.interactionManager.method_2915(itemStack4);
					}

					return;
				}

				if (!itemStack2.isEmpty() && !itemStack3.isEmpty() && itemStack2.isEqualIgnoreTags(itemStack3) && ItemStack.areTagsEqual(itemStack2, itemStack3)) {
					if (j == 0) {
						if (bl) {
							itemStack2.setAmount(itemStack2.getMaxAmount());
						} else if (itemStack2.getAmount() < itemStack2.getMaxAmount()) {
							itemStack2.addAmount(1);
						}
					} else {
						itemStack2.subtractAmount(1);
					}
				} else if (!itemStack3.isEmpty() && itemStack2.isEmpty()) {
					playerInventory.setCursorStack(itemStack3.copy());
					itemStack2 = playerInventory.getCursorStack();
					if (bl) {
						itemStack2.setAmount(itemStack2.getMaxAmount());
					}
				} else if (j == 0) {
					playerInventory.setCursorStack(ItemStack.EMPTY);
				} else {
					playerInventory.getCursorStack().subtractAmount(1);
				}
			} else if (this.container != null) {
				ItemStack itemStack = slot == null ? ItemStack.EMPTY : this.container.getSlot(slot.id).getStack();
				this.container.onSlotClick(slot == null ? i : slot.id, j, slotActionType, this.client.player);
				if (Container.method_7594(j) == 2) {
					for (int l = 0; l < 9; l++) {
						this.client.interactionManager.method_2909(this.container.getSlot(45 + l).getStack(), 36 + l);
					}
				} else if (slot != null) {
					ItemStack itemStack2x = this.container.getSlot(slot.id).getStack();
					this.client.interactionManager.method_2909(itemStack2x, slot.id - this.container.slotList.size() + 9 + 36);
					int m = 45 + j;
					if (slotActionType == SlotActionType.field_7791) {
						this.client.interactionManager.method_2909(itemStack, m - this.container.slotList.size() + 9 + 36);
					} else if (slotActionType == SlotActionType.field_7795 && !itemStack.isEmpty()) {
						ItemStack itemStack4 = itemStack.copy();
						itemStack4.setAmount(j == 0 ? 1 : itemStack4.getMaxAmount());
						this.client.player.dropItem(itemStack4, true);
						this.client.interactionManager.method_2915(itemStack4);
					}

					this.client.player.containerPlayer.sendContentUpdates();
				}
			}
		}
	}

	private boolean method_2470(@Nullable Slot slot) {
		return slot != null && slot.inventory == inventory;
	}

	@Override
	protected void method_2476() {
		int i = this.left;
		super.method_2476();
		if (this.searchBox != null && this.left != i) {
			this.searchBox.setX(this.left + 82);
		}
	}

	@Override
	protected void onInitialized() {
		if (this.client.interactionManager.hasCreativeInventory()) {
			super.onInitialized();
			this.client.keyboard.enableRepeatEvents(true);
			this.searchBox = new TextFieldWidget(0, this.fontRenderer, this.left + 82, this.top + 6, 80, 9);
			this.searchBox.setMaxLength(50);
			this.searchBox.setHasBorder(false);
			this.searchBox.setVisible(false);
			this.searchBox.method_1868(16777215);
			this.listeners.add(this.searchBox);
			int i = selectedTab;
			selectedTab = -1;
			this.setSelectedTab(ItemGroup.GROUPS[i]);
			this.client.player.containerPlayer.removeListener(this.field_2891);
			this.field_2891 = new class_478(this.client);
			this.client.player.containerPlayer.addListener(this.field_2891);
		} else {
			this.client.openScreen(new PlayerInventoryScreen(this.client.player));
		}
	}

	@Override
	public void onScaleChanged(MinecraftClient minecraftClient, int i, int j) {
		String string = this.searchBox.getText();
		this.initialize(minecraftClient, i, j);
		this.searchBox.setText(string);
		if (!this.searchBox.getText().isEmpty()) {
			this.method_2464();
		}
	}

	@Override
	public void onClosed() {
		super.onClosed();
		if (this.client.player != null && this.client.player.inventory != null) {
			this.client.player.containerPlayer.removeListener(this.field_2891);
		}

		this.client.keyboard.enableRepeatEvents(false);
	}

	@Override
	public boolean charTyped(char c, int i) {
		if (this.field_2888) {
			return false;
		} else if (selectedTab != ItemGroup.SEARCH.getId()) {
			return false;
		} else {
			String string = this.searchBox.getText();
			if (this.searchBox.charTyped(c, i)) {
				if (!Objects.equals(string, this.searchBox.getText())) {
					this.method_2464();
				}

				return true;
			} else {
				return false;
			}
		}
	}

	@Override
	public boolean keyPressed(int i, int j, int k) {
		this.field_2888 = false;
		if (selectedTab != ItemGroup.SEARCH.getId()) {
			if (this.client.options.keyChat.matchesKey(i, j)) {
				this.field_2888 = true;
				this.setSelectedTab(ItemGroup.SEARCH);
				return true;
			} else {
				return super.keyPressed(i, j, k);
			}
		} else {
			boolean bl = !this.method_2470(this.focusedSlot) || this.focusedSlot != null && this.focusedSlot.hasStack();
			if (bl && this.handleHotbarKeyPressed(i, j)) {
				this.field_2888 = true;
				return true;
			} else {
				String string = this.searchBox.getText();
				if (this.searchBox.keyPressed(i, j, k)) {
					if (!Objects.equals(string, this.searchBox.getText())) {
						this.method_2464();
					}

					return true;
				} else {
					return super.keyPressed(i, j, k);
				}
			}
		}
	}

	@Override
	public boolean keyReleased(int i, int j, int k) {
		this.field_2888 = false;
		return super.keyReleased(i, j, k);
	}

	private void method_2464() {
		this.container.itemList.clear();
		this.field_16201.clear();
		String string = this.searchBox.getText();
		if (string.isEmpty()) {
			for (Item item : Registry.ITEM) {
				item.addStacksForDisplay(ItemGroup.SEARCH, this.container.itemList);
			}
		} else {
			Searchable<ItemStack> searchable;
			if (string.startsWith("#")) {
				string = string.substring(1);
				searchable = this.client.getSearchableContainer(SearchManager.ITEM_TAG);
				this.method_15871(string);
			} else {
				searchable = this.client.getSearchableContainer(SearchManager.ITEM_TOOLTIP);
			}

			this.container.itemList.addAll(searchable.findAll(string.toLowerCase(Locale.ROOT)));
		}

		this.scrollPosition = 0.0F;
		this.container.method_2473(0.0F);
	}

	private void method_15871(String string) {
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
			Tag var10000 = (Tag)this.field_16201.put(identifier, tagContainer.get(identifier));
		});
	}

	@Override
	protected void drawForeground(int i, int j) {
		ItemGroup itemGroup = ItemGroup.GROUPS[selectedTab];
		if (itemGroup.hasTooltip()) {
			GlStateManager.disableBlend();
			this.fontRenderer.draw(I18n.translate(itemGroup.getTranslationKey()), 8.0F, 6.0F, 4210752);
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

			if (selectedTab != ItemGroup.INVENTORY.getId() && this.method_2467(d, e)) {
				this.field_2892 = this.doRenderScrollBar();
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
			this.field_2892 = false;

			for (ItemGroup itemGroup : ItemGroup.GROUPS) {
				if (this.isClickInTab(itemGroup, f, g)) {
					this.setSelectedTab(itemGroup);
					return true;
				}
			}
		}

		return super.mouseReleased(d, e, i);
	}

	private boolean doRenderScrollBar() {
		return selectedTab != ItemGroup.INVENTORY.getId() && ItemGroup.GROUPS[selectedTab].useScrollBar() && this.container.method_2474();
	}

	private void setSelectedTab(ItemGroup itemGroup) {
		int i = selectedTab;
		selectedTab = itemGroup.getId();
		this.slots.clear();
		this.container.itemList.clear();
		if (itemGroup == ItemGroup.HOTBAR) {
			HotbarStorage hotbarStorage = this.client.getCreativeHotbarStorage();

			for (int j = 0; j < 9; j++) {
				HotbarStorageEntry hotbarStorageEntry = hotbarStorage.getSavedHotbar(j);
				if (hotbarStorageEntry.isEmpty()) {
					for (int k = 0; k < 9; k++) {
						if (k == j) {
							ItemStack itemStack = new ItemStack(Items.field_8407);
							itemStack.getOrCreateSubCompoundTag("CustomCreativeLock");
							String string = this.client.options.keysHotbar[j].getLocalizedName();
							String string2 = this.client.options.keySaveToolbarActivator.getLocalizedName();
							itemStack.setDisplayName(new TranslatableTextComponent("inventory.hotbarInfo", string2, string));
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
			itemGroup.getStacksForDisplay(this.container.itemList);
		}

		if (itemGroup == ItemGroup.INVENTORY) {
			Container container = this.client.player.containerPlayer;
			if (this.slots == null) {
				this.slots = ImmutableList.copyOf(this.container.slotList);
			}

			this.container.slotList.clear();

			for (int jx = 0; jx < container.slotList.size(); jx++) {
				Slot slot = new CreativePlayerInventoryScreen.CreativeSlot((Slot)container.slotList.get(jx), jx);
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
		} else if (i == ItemGroup.INVENTORY.getId()) {
			this.container.slotList.clear();
			this.container.slotList.addAll(this.slots);
			this.slots = null;
		}

		if (this.searchBox != null) {
			if (itemGroup == ItemGroup.SEARCH) {
				this.searchBox.setVisible(true);
				this.searchBox.method_1856(false);
				this.searchBox.setFocused(true);
				if (i != itemGroup.getId()) {
					this.searchBox.setText("");
				}

				this.method_2464();
			} else {
				this.searchBox.setVisible(false);
				this.searchBox.method_1856(true);
				this.searchBox.setFocused(false);
				this.searchBox.setText("");
			}
		}

		this.scrollPosition = 0.0F;
		this.container.method_2473(0.0F);
	}

	@Override
	public boolean mouseScrolled(double d) {
		if (!this.doRenderScrollBar()) {
			return false;
		} else {
			int i = (this.container.itemList.size() + 9 - 1) / 9 - 5;
			this.scrollPosition = (float)((double)this.scrollPosition - d / (double)i);
			this.scrollPosition = MathHelper.clamp(this.scrollPosition, 0.0F, 1.0F);
			this.container.method_2473(this.scrollPosition);
			return true;
		}
	}

	@Override
	protected boolean isClickOutsideBounds(double d, double e, int i, int j, int k) {
		boolean bl = d < (double)i || e < (double)j || d >= (double)(i + this.containerWidth) || e >= (double)(j + this.containerHeight);
		this.field_2887 = bl && !this.isClickInTab(ItemGroup.GROUPS[selectedTab], d, e);
		return this.field_2887;
	}

	protected boolean method_2467(double d, double e) {
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
		if (this.field_2892) {
			int j = this.top + 18;
			int k = j + 112;
			this.scrollPosition = ((float)e - (float)j - 7.5F) / ((float)(k - j) - 15.0F);
			this.scrollPosition = MathHelper.clamp(this.scrollPosition, 0.0F, 1.0F);
			this.container.method_2473(this.scrollPosition);
			return true;
		} else {
			return super.mouseDragged(d, e, i, f, g);
		}
	}

	@Override
	public void draw(int i, int j, float f) {
		this.drawBackground();
		super.draw(i, j, f);

		for (ItemGroup itemGroup : ItemGroup.GROUPS) {
			if (this.method_2471(itemGroup, i, j)) {
				break;
			}
		}

		if (this.deleteItemSlot != null
			&& selectedTab == ItemGroup.INVENTORY.getId()
			&& this.isPointWithinBounds(this.deleteItemSlot.xPosition, this.deleteItemSlot.yPosition, 16, 16, (double)i, (double)j)) {
			this.drawTooltip(I18n.translate("inventory.binSlot"), i, j);
		}

		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.disableLighting();
		this.drawMousoverTooltip(i, j);
	}

	@Override
	protected void drawStackTooltip(ItemStack itemStack, int i, int j) {
		if (selectedTab == ItemGroup.SEARCH.getId()) {
			List<TextComponent> list = itemStack.getTooltipText(
				this.client.player, this.client.options.advancedItemTooltips ? TooltipOptions.Instance.ADVANCED : TooltipOptions.Instance.NORMAL
			);
			List<String> list2 = Lists.<String>newArrayListWithCapacity(list.size());

			for (TextComponent textComponent : list) {
				list2.add(textComponent.getFormattedText());
			}

			Item item = itemStack.getItem();
			ItemGroup itemGroup = item.getItemGroup();
			if (itemGroup == null && item == Items.field_8598) {
				Map<Enchantment, Integer> map = EnchantmentHelper.getEnchantments(itemStack);
				if (map.size() == 1) {
					Enchantment enchantment = (Enchantment)map.keySet().iterator().next();

					for (ItemGroup itemGroup2 : ItemGroup.GROUPS) {
						if (itemGroup2.containsEnchantmentType(enchantment.type)) {
							itemGroup = itemGroup2;
							break;
						}
					}
				}
			}

			this.field_16201.forEach((identifier, tag) -> {
				if (tag.contains(item)) {
					list2.add(1, "" + TextFormat.BOLD + TextFormat.DARK_PURPLE + "#" + identifier);
				}
			});
			if (itemGroup != null) {
				list2.add(1, "" + TextFormat.BOLD + TextFormat.BLUE + I18n.translate(itemGroup.getTranslationKey()));
			}

			for (int k = 0; k < list2.size(); k++) {
				if (k == 0) {
					list2.set(k, itemStack.getRarity().formatting + (String)list2.get(k));
				} else {
					list2.set(k, TextFormat.GRAY + (String)list2.get(k));
				}
			}

			this.drawTooltip(list2, i, j);
		} else {
			super.drawStackTooltip(itemStack, i, j);
		}
	}

	@Override
	protected void drawBackground(float f, int i, int j) {
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		GuiLighting.enableForItems();
		ItemGroup itemGroup = ItemGroup.GROUPS[selectedTab];

		for (ItemGroup itemGroup2 : ItemGroup.GROUPS) {
			this.client.getTextureManager().bindTexture(TEXTURE);
			if (itemGroup2.getId() != selectedTab) {
				this.method_2468(itemGroup2);
			}
		}

		this.client.getTextureManager().bindTexture(new Identifier("textures/gui/container/creative_inventory/tab_" + itemGroup.getTexture()));
		this.drawTexturedRect(this.left, this.top, 0, 0, this.containerWidth, this.containerHeight);
		this.searchBox.render(i, j, f);
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		int k = this.left + 175;
		int l = this.top + 18;
		int m = l + 112;
		this.client.getTextureManager().bindTexture(TEXTURE);
		if (itemGroup.useScrollBar()) {
			this.drawTexturedRect(k, l + (int)((float)(m - l - 17) * this.scrollPosition), 232 + (this.doRenderScrollBar() ? 0 : 12), 0, 12, 15);
		}

		this.method_2468(itemGroup);
		if (itemGroup == ItemGroup.INVENTORY) {
			PlayerInventoryScreen.drawEntity(this.left + 88, this.top + 45, 20, (float)(this.left + 88 - i), (float)(this.top + 45 - 30 - j), this.client.player);
		}
	}

	protected boolean isClickInTab(ItemGroup itemGroup, double d, double e) {
		int i = itemGroup.getColumn();
		int j = 28 * i;
		int k = 0;
		if (itemGroup.isTabRightAligned()) {
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

	protected boolean method_2471(ItemGroup itemGroup, int i, int j) {
		int k = itemGroup.getColumn();
		int l = 28 * k;
		int m = 0;
		if (itemGroup.isTabRightAligned()) {
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
			this.drawTooltip(I18n.translate(itemGroup.getTranslationKey()), i, j);
			return true;
		} else {
			return false;
		}
	}

	protected void method_2468(ItemGroup itemGroup) {
		boolean bl = itemGroup.getId() == selectedTab;
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

		if (itemGroup.isTabRightAligned()) {
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

		GlStateManager.disableLighting();
		this.drawTexturedRect(l, m, j, k, 28, 32);
		this.zOffset = 100.0F;
		this.itemRenderer.zOffset = 100.0F;
		l += 6;
		m += 8 + (bl2 ? 1 : -1);
		GlStateManager.enableLighting();
		GlStateManager.enableRescaleNormal();
		ItemStack itemStack = itemGroup.getIconItemStack();
		this.itemRenderer.renderGuiItem(itemStack, l, m);
		this.itemRenderer.renderGuiItemOverlay(this.fontRenderer, itemStack, l, m);
		GlStateManager.disableLighting();
		this.itemRenderer.zOffset = 0.0F;
		this.zOffset = 0.0F;
	}

	public int method_2469() {
		return selectedTab;
	}

	public static void method_2462(MinecraftClient minecraftClient, int i, boolean bl, boolean bl2) {
		ClientPlayerEntity clientPlayerEntity = minecraftClient.player;
		HotbarStorage hotbarStorage = minecraftClient.getCreativeHotbarStorage();
		HotbarStorageEntry hotbarStorageEntry = hotbarStorage.getSavedHotbar(i);
		if (bl) {
			for (int j = 0; j < PlayerInventory.getHotbarSize(); j++) {
				ItemStack itemStack = hotbarStorageEntry.get(j).copy();
				clientPlayerEntity.inventory.setInvStack(j, itemStack);
				minecraftClient.interactionManager.method_2909(itemStack, 36 + j);
			}

			clientPlayerEntity.containerPlayer.sendContentUpdates();
		} else if (bl2) {
			for (int j = 0; j < PlayerInventory.getHotbarSize(); j++) {
				hotbarStorageEntry.set(j, clientPlayerEntity.inventory.getInvStack(j).copy());
			}

			String string = minecraftClient.options.keysHotbar[i].getLocalizedName();
			String string2 = minecraftClient.options.keyLoadToolbarActivator.getLocalizedName();
			minecraftClient.inGameHud.setOverlayMessage(new TranslatableTextComponent("inventory.hotbarSaved", string2, string), false);
			hotbarStorage.save();
		}
	}

	@Environment(EnvType.CLIENT)
	public static class CreativeContainer extends Container {
		public final DefaultedList<ItemStack> itemList = DefaultedList.create();

		public CreativeContainer(PlayerEntity playerEntity) {
			super(null, 0);
			PlayerInventory playerInventory = playerEntity.inventory;

			for (int i = 0; i < 5; i++) {
				for (int j = 0; j < 9; j++) {
					this.addSlot(new CreativePlayerInventoryScreen.class_482(CreativePlayerInventoryScreen.inventory, i * 9 + j, 9 + j * 18, 18 + i * 18));
				}
			}

			for (int i = 0; i < 9; i++) {
				this.addSlot(new Slot(playerInventory, i, 9 + i * 18, 112));
			}

			this.method_2473(0.0F);
		}

		@Override
		public boolean canUse(PlayerEntity playerEntity) {
			return true;
		}

		public void method_2473(float f) {
			int i = (this.itemList.size() + 9 - 1) / 9 - 5;
			int j = (int)((double)(f * (float)i) + 0.5);
			if (j < 0) {
				j = 0;
			}

			for (int k = 0; k < 5; k++) {
				for (int l = 0; l < 9; l++) {
					int m = l + (k + j) * 9;
					if (m >= 0 && m < this.itemList.size()) {
						CreativePlayerInventoryScreen.inventory.setInvStack(l + k * 9, this.itemList.get(m));
					} else {
						CreativePlayerInventoryScreen.inventory.setInvStack(l + k * 9, ItemStack.EMPTY);
					}
				}
			}
		}

		public boolean method_2474() {
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
		public boolean method_7613(ItemStack itemStack, Slot slot) {
			return slot.inventory != CreativePlayerInventoryScreen.inventory;
		}

		@Override
		public boolean method_7615(Slot slot) {
			return slot.inventory != CreativePlayerInventoryScreen.inventory;
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
	static class class_482 extends Slot {
		public class_482(Inventory inventory, int i, int j, int k) {
			super(inventory, i, j, k);
		}

		@Override
		public boolean canTakeItems(PlayerEntity playerEntity) {
			return super.canTakeItems(playerEntity) && this.hasStack() ? this.getStack().getSubCompoundTag("CustomCreativeLock") == null : !this.hasStack();
		}
	}
}
