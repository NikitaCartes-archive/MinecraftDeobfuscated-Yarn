package net.minecraft.client.gui.screen.ingame;

import com.google.common.collect.Sets;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.GuiLighting;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.InputUtil;
import net.minecraft.container.Container;
import net.minecraft.container.Slot;
import net.minecraft.container.SlotActionType;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public abstract class AbstractContainerScreen<T extends Container> extends Screen implements ContainerProvider<T> {
	public static final Identifier BACKGROUND_TEXTURE = new Identifier("textures/gui/container/inventory.png");
	protected int containerWidth = 176;
	protected int containerHeight = 166;
	protected final T container;
	protected final PlayerInventory playerInventory;
	protected int left;
	protected int top;
	protected Slot focusedSlot;
	private Slot touchDragSlotStart;
	private boolean touchIsRightClickDrag;
	private ItemStack touchDragStack = ItemStack.EMPTY;
	private int touchDropX;
	private int touchDropY;
	private Slot touchDropOriginSlot;
	private long touchDropTime;
	private ItemStack touchDropReturningStack = ItemStack.EMPTY;
	private Slot touchHoveredSlot;
	private long touchDropTimer;
	protected final Set<Slot> cursorDragSlots = Sets.<Slot>newHashSet();
	protected boolean isCursorDragging;
	private int heldButtonType;
	private int heldButtonCode;
	private boolean cancelNextRelease;
	private int draggedStackRemainder;
	private long lastButtonClickTime;
	private Slot lastClickedSlot;
	private int lastClickedButton;
	private boolean isDoubleClicking;
	private ItemStack quickMovingStack = ItemStack.EMPTY;

	public AbstractContainerScreen(T container, PlayerInventory playerInventory, Text text) {
		super(text);
		this.container = container;
		this.playerInventory = playerInventory;
		this.cancelNextRelease = true;
	}

	@Override
	protected void init() {
		super.init();
		this.left = (this.width - this.containerWidth) / 2;
		this.top = (this.height - this.containerHeight) / 2;
	}

	@Override
	public void render(int i, int j, float f) {
		int k = this.left;
		int l = this.top;
		this.drawBackground(f, i, j);
		RenderSystem.disableRescaleNormal();
		GuiLighting.disable();
		RenderSystem.disableLighting();
		RenderSystem.disableDepthTest();
		super.render(i, j, f);
		GuiLighting.enableForItems();
		RenderSystem.pushMatrix();
		RenderSystem.translatef((float)k, (float)l, 0.0F);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.enableRescaleNormal();
		this.focusedSlot = null;
		int m = 240;
		int n = 240;
		RenderSystem.glMultiTexCoord2f(33985, 240.0F, 240.0F);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);

		for (int o = 0; o < this.container.slotList.size(); o++) {
			Slot slot = (Slot)this.container.slotList.get(o);
			if (slot.doDrawHoveringEffect()) {
				this.drawSlot(slot);
			}

			if (this.isPointOverSlot(slot, (double)i, (double)j) && slot.doDrawHoveringEffect()) {
				this.focusedSlot = slot;
				RenderSystem.disableLighting();
				RenderSystem.disableDepthTest();
				int p = slot.xPosition;
				int q = slot.yPosition;
				RenderSystem.colorMask(true, true, true, false);
				this.fillGradient(p, q, p + 16, q + 16, -2130706433, -2130706433);
				RenderSystem.colorMask(true, true, true, true);
				RenderSystem.enableLighting();
				RenderSystem.enableDepthTest();
			}
		}

		GuiLighting.disable();
		this.drawForeground(i, j);
		GuiLighting.enableForItems();
		PlayerInventory playerInventory = this.minecraft.player.inventory;
		ItemStack itemStack = this.touchDragStack.isEmpty() ? playerInventory.getCursorStack() : this.touchDragStack;
		if (!itemStack.isEmpty()) {
			int p = 8;
			int q = this.touchDragStack.isEmpty() ? 8 : 16;
			String string = null;
			if (!this.touchDragStack.isEmpty() && this.touchIsRightClickDrag) {
				itemStack = itemStack.copy();
				itemStack.setCount(MathHelper.ceil((float)itemStack.getCount() / 2.0F));
			} else if (this.isCursorDragging && this.cursorDragSlots.size() > 1) {
				itemStack = itemStack.copy();
				itemStack.setCount(this.draggedStackRemainder);
				if (itemStack.isEmpty()) {
					string = "" + Formatting.YELLOW + "0";
				}
			}

			this.drawItem(itemStack, i - k - 8, j - l - q, string);
		}

		if (!this.touchDropReturningStack.isEmpty()) {
			float g = (float)(SystemUtil.getMeasuringTimeMs() - this.touchDropTime) / 100.0F;
			if (g >= 1.0F) {
				g = 1.0F;
				this.touchDropReturningStack = ItemStack.EMPTY;
			}

			int q = this.touchDropOriginSlot.xPosition - this.touchDropX;
			int r = this.touchDropOriginSlot.yPosition - this.touchDropY;
			int s = this.touchDropX + (int)((float)q * g);
			int t = this.touchDropY + (int)((float)r * g);
			this.drawItem(this.touchDropReturningStack, s, t, null);
		}

		RenderSystem.popMatrix();
		RenderSystem.enableLighting();
		RenderSystem.enableDepthTest();
		GuiLighting.enable();
	}

	protected void drawMouseoverTooltip(int i, int j) {
		if (this.minecraft.player.inventory.getCursorStack().isEmpty() && this.focusedSlot != null && this.focusedSlot.hasStack()) {
			this.renderTooltip(this.focusedSlot.getStack(), i, j);
		}
	}

	private void drawItem(ItemStack itemStack, int i, int j, String string) {
		RenderSystem.translatef(0.0F, 0.0F, 32.0F);
		this.setBlitOffset(200);
		this.itemRenderer.zOffset = 200.0F;
		this.itemRenderer.renderGuiItem(itemStack, i, j);
		this.itemRenderer.renderGuiItemOverlay(this.font, itemStack, i, j - (this.touchDragStack.isEmpty() ? 0 : 8), string);
		this.setBlitOffset(0);
		this.itemRenderer.zOffset = 0.0F;
	}

	protected void drawForeground(int i, int j) {
	}

	protected abstract void drawBackground(float f, int i, int j);

	private void drawSlot(Slot slot) {
		int i = slot.xPosition;
		int j = slot.yPosition;
		ItemStack itemStack = slot.getStack();
		boolean bl = false;
		boolean bl2 = slot == this.touchDragSlotStart && !this.touchDragStack.isEmpty() && !this.touchIsRightClickDrag;
		ItemStack itemStack2 = this.minecraft.player.inventory.getCursorStack();
		String string = null;
		if (slot == this.touchDragSlotStart && !this.touchDragStack.isEmpty() && this.touchIsRightClickDrag && !itemStack.isEmpty()) {
			itemStack = itemStack.copy();
			itemStack.setCount(itemStack.getCount() / 2);
		} else if (this.isCursorDragging && this.cursorDragSlots.contains(slot) && !itemStack2.isEmpty()) {
			if (this.cursorDragSlots.size() == 1) {
				return;
			}

			if (Container.canInsertItemIntoSlot(slot, itemStack2, true) && this.container.canInsertIntoSlot(slot)) {
				itemStack = itemStack2.copy();
				bl = true;
				Container.calculateStackSize(this.cursorDragSlots, this.heldButtonType, itemStack, slot.getStack().isEmpty() ? 0 : slot.getStack().getCount());
				int k = Math.min(itemStack.getMaxCount(), slot.getMaxStackAmount(itemStack));
				if (itemStack.getCount() > k) {
					string = Formatting.YELLOW.toString() + k;
					itemStack.setCount(k);
				}
			} else {
				this.cursorDragSlots.remove(slot);
				this.calculateOffset();
			}
		}

		this.setBlitOffset(100);
		this.itemRenderer.zOffset = 100.0F;
		if (itemStack.isEmpty() && slot.doDrawHoveringEffect()) {
			String string2 = slot.getBackgroundSprite();
			if (string2 != null) {
				Sprite sprite = this.minecraft.getSpriteAtlas().getSprite(string2);
				RenderSystem.disableLighting();
				this.minecraft.getTextureManager().method_22813(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
				blit(i, j, this.getBlitOffset(), 16, 16, sprite);
				RenderSystem.enableLighting();
				bl2 = true;
			}
		}

		if (!bl2) {
			if (bl) {
				fill(i, j, i + 16, j + 16, -2130706433);
			}

			RenderSystem.enableDepthTest();
			this.itemRenderer.renderGuiItem(this.minecraft.player, itemStack, i, j);
			this.itemRenderer.renderGuiItemOverlay(this.font, itemStack, i, j, string);
		}

		this.itemRenderer.zOffset = 0.0F;
		this.setBlitOffset(0);
	}

	private void calculateOffset() {
		ItemStack itemStack = this.minecraft.player.inventory.getCursorStack();
		if (!itemStack.isEmpty() && this.isCursorDragging) {
			if (this.heldButtonType == 2) {
				this.draggedStackRemainder = itemStack.getMaxCount();
			} else {
				this.draggedStackRemainder = itemStack.getCount();

				for (Slot slot : this.cursorDragSlots) {
					ItemStack itemStack2 = itemStack.copy();
					ItemStack itemStack3 = slot.getStack();
					int i = itemStack3.isEmpty() ? 0 : itemStack3.getCount();
					Container.calculateStackSize(this.cursorDragSlots, this.heldButtonType, itemStack2, i);
					int j = Math.min(itemStack2.getMaxCount(), slot.getMaxStackAmount(itemStack2));
					if (itemStack2.getCount() > j) {
						itemStack2.setCount(j);
					}

					this.draggedStackRemainder = this.draggedStackRemainder - (itemStack2.getCount() - i);
				}
			}
		}
	}

	private Slot getSlotAt(double d, double e) {
		for (int i = 0; i < this.container.slotList.size(); i++) {
			Slot slot = (Slot)this.container.slotList.get(i);
			if (this.isPointOverSlot(slot, d, e) && slot.doDrawHoveringEffect()) {
				return slot;
			}
		}

		return null;
	}

	@Override
	public boolean mouseClicked(double d, double e, int i) {
		if (super.mouseClicked(d, e, i)) {
			return true;
		} else {
			boolean bl = this.minecraft.options.keyPickItem.matchesMouse(i);
			Slot slot = this.getSlotAt(d, e);
			long l = SystemUtil.getMeasuringTimeMs();
			this.isDoubleClicking = this.lastClickedSlot == slot && l - this.lastButtonClickTime < 250L && this.lastClickedButton == i;
			this.cancelNextRelease = false;
			if (i == 0 || i == 1 || bl) {
				int j = this.left;
				int k = this.top;
				boolean bl2 = this.isClickOutsideBounds(d, e, j, k, i);
				int m = -1;
				if (slot != null) {
					m = slot.id;
				}

				if (bl2) {
					m = -999;
				}

				if (this.minecraft.options.touchscreen && bl2 && this.minecraft.player.inventory.getCursorStack().isEmpty()) {
					this.minecraft.openScreen(null);
					return true;
				}

				if (m != -1) {
					if (this.minecraft.options.touchscreen) {
						if (slot != null && slot.hasStack()) {
							this.touchDragSlotStart = slot;
							this.touchDragStack = ItemStack.EMPTY;
							this.touchIsRightClickDrag = i == 1;
						} else {
							this.touchDragSlotStart = null;
						}
					} else if (!this.isCursorDragging) {
						if (this.minecraft.player.inventory.getCursorStack().isEmpty()) {
							if (this.minecraft.options.keyPickItem.matchesMouse(i)) {
								this.onMouseClick(slot, m, i, SlotActionType.CLONE);
							} else {
								boolean bl3 = m != -999
									&& (
										InputUtil.isKeyPressed(MinecraftClient.getInstance().method_22683().getHandle(), 340)
											|| InputUtil.isKeyPressed(MinecraftClient.getInstance().method_22683().getHandle(), 344)
									);
								SlotActionType slotActionType = SlotActionType.PICKUP;
								if (bl3) {
									this.quickMovingStack = slot != null && slot.hasStack() ? slot.getStack().copy() : ItemStack.EMPTY;
									slotActionType = SlotActionType.QUICK_MOVE;
								} else if (m == -999) {
									slotActionType = SlotActionType.THROW;
								}

								this.onMouseClick(slot, m, i, slotActionType);
							}

							this.cancelNextRelease = true;
						} else {
							this.isCursorDragging = true;
							this.heldButtonCode = i;
							this.cursorDragSlots.clear();
							if (i == 0) {
								this.heldButtonType = 0;
							} else if (i == 1) {
								this.heldButtonType = 1;
							} else if (this.minecraft.options.keyPickItem.matchesMouse(i)) {
								this.heldButtonType = 2;
							}
						}
					}
				}
			}

			this.lastClickedSlot = slot;
			this.lastButtonClickTime = l;
			this.lastClickedButton = i;
			return true;
		}
	}

	protected boolean isClickOutsideBounds(double d, double e, int i, int j, int k) {
		return d < (double)i || e < (double)j || d >= (double)(i + this.containerWidth) || e >= (double)(j + this.containerHeight);
	}

	@Override
	public boolean mouseDragged(double d, double e, int i, double f, double g) {
		Slot slot = this.getSlotAt(d, e);
		ItemStack itemStack = this.minecraft.player.inventory.getCursorStack();
		if (this.touchDragSlotStart != null && this.minecraft.options.touchscreen) {
			if (i == 0 || i == 1) {
				if (this.touchDragStack.isEmpty()) {
					if (slot != this.touchDragSlotStart && !this.touchDragSlotStart.getStack().isEmpty()) {
						this.touchDragStack = this.touchDragSlotStart.getStack().copy();
					}
				} else if (this.touchDragStack.getCount() > 1 && slot != null && Container.canInsertItemIntoSlot(slot, this.touchDragStack, false)) {
					long l = SystemUtil.getMeasuringTimeMs();
					if (this.touchHoveredSlot == slot) {
						if (l - this.touchDropTimer > 500L) {
							this.onMouseClick(this.touchDragSlotStart, this.touchDragSlotStart.id, 0, SlotActionType.PICKUP);
							this.onMouseClick(slot, slot.id, 1, SlotActionType.PICKUP);
							this.onMouseClick(this.touchDragSlotStart, this.touchDragSlotStart.id, 0, SlotActionType.PICKUP);
							this.touchDropTimer = l + 750L;
							this.touchDragStack.decrement(1);
						}
					} else {
						this.touchHoveredSlot = slot;
						this.touchDropTimer = l;
					}
				}
			}
		} else if (this.isCursorDragging
			&& slot != null
			&& !itemStack.isEmpty()
			&& (itemStack.getCount() > this.cursorDragSlots.size() || this.heldButtonType == 2)
			&& Container.canInsertItemIntoSlot(slot, itemStack, true)
			&& slot.canInsert(itemStack)
			&& this.container.canInsertIntoSlot(slot)) {
			this.cursorDragSlots.add(slot);
			this.calculateOffset();
		}

		return true;
	}

	@Override
	public boolean mouseReleased(double d, double e, int i) {
		Slot slot = this.getSlotAt(d, e);
		int j = this.left;
		int k = this.top;
		boolean bl = this.isClickOutsideBounds(d, e, j, k, i);
		int l = -1;
		if (slot != null) {
			l = slot.id;
		}

		if (bl) {
			l = -999;
		}

		if (this.isDoubleClicking && slot != null && i == 0 && this.container.canInsertIntoSlot(ItemStack.EMPTY, slot)) {
			if (hasShiftDown()) {
				if (!this.quickMovingStack.isEmpty()) {
					for (Slot slot2 : this.container.slotList) {
						if (slot2 != null
							&& slot2.canTakeItems(this.minecraft.player)
							&& slot2.hasStack()
							&& slot2.inventory == slot.inventory
							&& Container.canInsertItemIntoSlot(slot2, this.quickMovingStack, true)) {
							this.onMouseClick(slot2, slot2.id, i, SlotActionType.QUICK_MOVE);
						}
					}
				}
			} else {
				this.onMouseClick(slot, l, i, SlotActionType.PICKUP_ALL);
			}

			this.isDoubleClicking = false;
			this.lastButtonClickTime = 0L;
		} else {
			if (this.isCursorDragging && this.heldButtonCode != i) {
				this.isCursorDragging = false;
				this.cursorDragSlots.clear();
				this.cancelNextRelease = true;
				return true;
			}

			if (this.cancelNextRelease) {
				this.cancelNextRelease = false;
				return true;
			}

			if (this.touchDragSlotStart != null && this.minecraft.options.touchscreen) {
				if (i == 0 || i == 1) {
					if (this.touchDragStack.isEmpty() && slot != this.touchDragSlotStart) {
						this.touchDragStack = this.touchDragSlotStart.getStack();
					}

					boolean bl2 = Container.canInsertItemIntoSlot(slot, this.touchDragStack, false);
					if (l != -1 && !this.touchDragStack.isEmpty() && bl2) {
						this.onMouseClick(this.touchDragSlotStart, this.touchDragSlotStart.id, i, SlotActionType.PICKUP);
						this.onMouseClick(slot, l, 0, SlotActionType.PICKUP);
						if (this.minecraft.player.inventory.getCursorStack().isEmpty()) {
							this.touchDropReturningStack = ItemStack.EMPTY;
						} else {
							this.onMouseClick(this.touchDragSlotStart, this.touchDragSlotStart.id, i, SlotActionType.PICKUP);
							this.touchDropX = MathHelper.floor(d - (double)j);
							this.touchDropY = MathHelper.floor(e - (double)k);
							this.touchDropOriginSlot = this.touchDragSlotStart;
							this.touchDropReturningStack = this.touchDragStack;
							this.touchDropTime = SystemUtil.getMeasuringTimeMs();
						}
					} else if (!this.touchDragStack.isEmpty()) {
						this.touchDropX = MathHelper.floor(d - (double)j);
						this.touchDropY = MathHelper.floor(e - (double)k);
						this.touchDropOriginSlot = this.touchDragSlotStart;
						this.touchDropReturningStack = this.touchDragStack;
						this.touchDropTime = SystemUtil.getMeasuringTimeMs();
					}

					this.touchDragStack = ItemStack.EMPTY;
					this.touchDragSlotStart = null;
				}
			} else if (this.isCursorDragging && !this.cursorDragSlots.isEmpty()) {
				this.onMouseClick(null, -999, Container.packClickData(0, this.heldButtonType), SlotActionType.QUICK_CRAFT);

				for (Slot slot2x : this.cursorDragSlots) {
					this.onMouseClick(slot2x, slot2x.id, Container.packClickData(1, this.heldButtonType), SlotActionType.QUICK_CRAFT);
				}

				this.onMouseClick(null, -999, Container.packClickData(2, this.heldButtonType), SlotActionType.QUICK_CRAFT);
			} else if (!this.minecraft.player.inventory.getCursorStack().isEmpty()) {
				if (this.minecraft.options.keyPickItem.matchesMouse(i)) {
					this.onMouseClick(slot, l, i, SlotActionType.CLONE);
				} else {
					boolean bl2 = l != -999
						&& (
							InputUtil.isKeyPressed(MinecraftClient.getInstance().method_22683().getHandle(), 340)
								|| InputUtil.isKeyPressed(MinecraftClient.getInstance().method_22683().getHandle(), 344)
						);
					if (bl2) {
						this.quickMovingStack = slot != null && slot.hasStack() ? slot.getStack().copy() : ItemStack.EMPTY;
					}

					this.onMouseClick(slot, l, i, bl2 ? SlotActionType.QUICK_MOVE : SlotActionType.PICKUP);
				}
			}
		}

		if (this.minecraft.player.inventory.getCursorStack().isEmpty()) {
			this.lastButtonClickTime = 0L;
		}

		this.isCursorDragging = false;
		return true;
	}

	private boolean isPointOverSlot(Slot slot, double d, double e) {
		return this.isPointWithinBounds(slot.xPosition, slot.yPosition, 16, 16, d, e);
	}

	protected boolean isPointWithinBounds(int i, int j, int k, int l, double d, double e) {
		int m = this.left;
		int n = this.top;
		d -= (double)m;
		e -= (double)n;
		return d >= (double)(i - 1) && d < (double)(i + k + 1) && e >= (double)(j - 1) && e < (double)(j + l + 1);
	}

	protected void onMouseClick(Slot slot, int i, int j, SlotActionType slotActionType) {
		if (slot != null) {
			i = slot.id;
		}

		this.minecraft.interactionManager.clickSlot(this.container.syncId, i, j, slotActionType, this.minecraft.player);
	}

	@Override
	public boolean shouldCloseOnEsc() {
		return false;
	}

	@Override
	public boolean keyPressed(int i, int j, int k) {
		if (super.keyPressed(i, j, k)) {
			return true;
		} else {
			if (i == 256 || this.minecraft.options.keyInventory.matchesKey(i, j)) {
				this.minecraft.player.closeContainer();
			}

			this.handleHotbarKeyPressed(i, j);
			if (this.focusedSlot != null && this.focusedSlot.hasStack()) {
				if (this.minecraft.options.keyPickItem.matchesKey(i, j)) {
					this.onMouseClick(this.focusedSlot, this.focusedSlot.id, 0, SlotActionType.CLONE);
				} else if (this.minecraft.options.keyDrop.matchesKey(i, j)) {
					this.onMouseClick(this.focusedSlot, this.focusedSlot.id, hasControlDown() ? 1 : 0, SlotActionType.THROW);
				}
			}

			return true;
		}
	}

	protected boolean handleHotbarKeyPressed(int i, int j) {
		if (this.minecraft.player.inventory.getCursorStack().isEmpty() && this.focusedSlot != null) {
			for (int k = 0; k < 9; k++) {
				if (this.minecraft.options.keysHotbar[k].matchesKey(i, j)) {
					this.onMouseClick(this.focusedSlot, this.focusedSlot.id, k, SlotActionType.SWAP);
					return true;
				}
			}
		}

		return false;
	}

	@Override
	public void removed() {
		if (this.minecraft.player != null) {
			this.container.close(this.minecraft.player);
		}
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	@Override
	public void tick() {
		super.tick();
		if (!this.minecraft.player.isAlive() || this.minecraft.player.removed) {
			this.minecraft.player.closeContainer();
		}
	}

	@Override
	public T getContainer() {
		return this.container;
	}
}
