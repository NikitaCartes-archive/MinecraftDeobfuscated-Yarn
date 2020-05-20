package net.minecraft.client.gui.screen.ingame;

import com.google.common.collect.Sets;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public abstract class HandledScreen<T extends ScreenHandler> extends Screen implements ScreenHandlerProvider<T> {
	public static final Identifier BACKGROUND_TEXTURE = new Identifier("textures/gui/container/inventory.png");
	protected int backgroundWidth = 176;
	protected int backgroundHeight = 166;
	protected final T handler;
	protected final PlayerInventory playerInventory;
	protected int x;
	protected int y;
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

	public HandledScreen(T handler, PlayerInventory inventory, Text title) {
		super(title);
		this.handler = handler;
		this.playerInventory = inventory;
		this.cancelNextRelease = true;
	}

	@Override
	protected void init() {
		super.init();
		this.x = (this.width - this.backgroundWidth) / 2;
		this.y = (this.height - this.backgroundHeight) / 2;
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		int i = this.x;
		int j = this.y;
		this.drawBackground(matrices, delta, mouseX, mouseY);
		RenderSystem.disableRescaleNormal();
		RenderSystem.disableDepthTest();
		super.render(matrices, mouseX, mouseY, delta);
		RenderSystem.pushMatrix();
		RenderSystem.translatef((float)i, (float)j, 0.0F);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.enableRescaleNormal();
		this.focusedSlot = null;
		int k = 240;
		int l = 240;
		RenderSystem.glMultiTexCoord2f(33986, 240.0F, 240.0F);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);

		for (int m = 0; m < this.handler.slots.size(); m++) {
			Slot slot = (Slot)this.handler.slots.get(m);
			if (slot.doDrawHoveringEffect()) {
				this.drawSlot(matrices, slot);
			}

			if (this.isPointOverSlot(slot, (double)mouseX, (double)mouseY) && slot.doDrawHoveringEffect()) {
				this.focusedSlot = slot;
				RenderSystem.disableDepthTest();
				int n = slot.x;
				int o = slot.y;
				RenderSystem.colorMask(true, true, true, false);
				this.fillGradient(matrices, n, o, n + 16, o + 16, -2130706433, -2130706433);
				RenderSystem.colorMask(true, true, true, true);
				RenderSystem.enableDepthTest();
			}
		}

		this.drawForeground(matrices, mouseX, mouseY);
		PlayerInventory playerInventory = this.client.player.inventory;
		ItemStack itemStack = this.touchDragStack.isEmpty() ? playerInventory.getCursorStack() : this.touchDragStack;
		if (!itemStack.isEmpty()) {
			int n = 8;
			int o = this.touchDragStack.isEmpty() ? 8 : 16;
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

			this.drawItem(itemStack, mouseX - i - 8, mouseY - j - o, string);
		}

		if (!this.touchDropReturningStack.isEmpty()) {
			float f = (float)(Util.getMeasuringTimeMs() - this.touchDropTime) / 100.0F;
			if (f >= 1.0F) {
				f = 1.0F;
				this.touchDropReturningStack = ItemStack.EMPTY;
			}

			int o = this.touchDropOriginSlot.x - this.touchDropX;
			int p = this.touchDropOriginSlot.y - this.touchDropY;
			int q = this.touchDropX + (int)((float)o * f);
			int r = this.touchDropY + (int)((float)p * f);
			this.drawItem(this.touchDropReturningStack, q, r, null);
		}

		RenderSystem.popMatrix();
		RenderSystem.enableDepthTest();
	}

	protected void drawMouseoverTooltip(MatrixStack matrices, int x, int y) {
		if (this.client.player.inventory.getCursorStack().isEmpty() && this.focusedSlot != null && this.focusedSlot.hasStack()) {
			this.renderTooltip(matrices, this.focusedSlot.getStack(), x, y);
		}
	}

	private void drawItem(ItemStack stack, int xPosition, int yPosition, String amountText) {
		RenderSystem.translatef(0.0F, 0.0F, 32.0F);
		this.setZOffset(200);
		this.itemRenderer.zOffset = 200.0F;
		this.itemRenderer.renderGuiItem(stack, xPosition, yPosition);
		this.itemRenderer.renderGuiItemOverlay(this.textRenderer, stack, xPosition, yPosition - (this.touchDragStack.isEmpty() ? 0 : 8), amountText);
		this.setZOffset(0);
		this.itemRenderer.zOffset = 0.0F;
	}

	protected void drawForeground(MatrixStack matrices, int mouseX, int mouseY) {
	}

	protected abstract void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY);

	private void drawSlot(MatrixStack matrices, Slot slot) {
		int i = slot.x;
		int j = slot.y;
		ItemStack itemStack = slot.getStack();
		boolean bl = false;
		boolean bl2 = slot == this.touchDragSlotStart && !this.touchDragStack.isEmpty() && !this.touchIsRightClickDrag;
		ItemStack itemStack2 = this.client.player.inventory.getCursorStack();
		String string = null;
		if (slot == this.touchDragSlotStart && !this.touchDragStack.isEmpty() && this.touchIsRightClickDrag && !itemStack.isEmpty()) {
			itemStack = itemStack.copy();
			itemStack.setCount(itemStack.getCount() / 2);
		} else if (this.isCursorDragging && this.cursorDragSlots.contains(slot) && !itemStack2.isEmpty()) {
			if (this.cursorDragSlots.size() == 1) {
				return;
			}

			if (ScreenHandler.canInsertItemIntoSlot(slot, itemStack2, true) && this.handler.canInsertIntoSlot(slot)) {
				itemStack = itemStack2.copy();
				bl = true;
				ScreenHandler.calculateStackSize(this.cursorDragSlots, this.heldButtonType, itemStack, slot.getStack().isEmpty() ? 0 : slot.getStack().getCount());
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

		this.setZOffset(100);
		this.itemRenderer.zOffset = 100.0F;
		if (itemStack.isEmpty() && slot.doDrawHoveringEffect()) {
			Pair<Identifier, Identifier> pair = slot.getBackgroundSprite();
			if (pair != null) {
				Sprite sprite = (Sprite)this.client.getSpriteAtlas(pair.getFirst()).apply(pair.getSecond());
				this.client.getTextureManager().bindTexture(sprite.getAtlas().getId());
				drawSprite(matrices, i, j, this.getZOffset(), 16, 16, sprite);
				bl2 = true;
			}
		}

		if (!bl2) {
			if (bl) {
				fill(matrices, i, j, i + 16, j + 16, -2130706433);
			}

			RenderSystem.enableDepthTest();
			this.itemRenderer.method_27951(this.client.player, itemStack, i, j);
			this.itemRenderer.renderGuiItemOverlay(this.textRenderer, itemStack, i, j, string);
		}

		this.itemRenderer.zOffset = 0.0F;
		this.setZOffset(0);
	}

	private void calculateOffset() {
		ItemStack itemStack = this.client.player.inventory.getCursorStack();
		if (!itemStack.isEmpty() && this.isCursorDragging) {
			if (this.heldButtonType == 2) {
				this.draggedStackRemainder = itemStack.getMaxCount();
			} else {
				this.draggedStackRemainder = itemStack.getCount();

				for (Slot slot : this.cursorDragSlots) {
					ItemStack itemStack2 = itemStack.copy();
					ItemStack itemStack3 = slot.getStack();
					int i = itemStack3.isEmpty() ? 0 : itemStack3.getCount();
					ScreenHandler.calculateStackSize(this.cursorDragSlots, this.heldButtonType, itemStack2, i);
					int j = Math.min(itemStack2.getMaxCount(), slot.getMaxStackAmount(itemStack2));
					if (itemStack2.getCount() > j) {
						itemStack2.setCount(j);
					}

					this.draggedStackRemainder = this.draggedStackRemainder - (itemStack2.getCount() - i);
				}
			}
		}
	}

	private Slot getSlotAt(double xPosition, double yPosition) {
		for (int i = 0; i < this.handler.slots.size(); i++) {
			Slot slot = (Slot)this.handler.slots.get(i);
			if (this.isPointOverSlot(slot, xPosition, yPosition) && slot.doDrawHoveringEffect()) {
				return slot;
			}
		}

		return null;
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (super.mouseClicked(mouseX, mouseY, button)) {
			return true;
		} else {
			boolean bl = this.client.options.keyPickItem.matchesMouse(button);
			Slot slot = this.getSlotAt(mouseX, mouseY);
			long l = Util.getMeasuringTimeMs();
			this.isDoubleClicking = this.lastClickedSlot == slot && l - this.lastButtonClickTime < 250L && this.lastClickedButton == button;
			this.cancelNextRelease = false;
			if (button == 0 || button == 1 || bl) {
				int i = this.x;
				int j = this.y;
				boolean bl2 = this.isClickOutsideBounds(mouseX, mouseY, i, j, button);
				int k = -1;
				if (slot != null) {
					k = slot.id;
				}

				if (bl2) {
					k = -999;
				}

				if (this.client.options.touchscreen && bl2 && this.client.player.inventory.getCursorStack().isEmpty()) {
					this.client.openScreen(null);
					return true;
				}

				if (k != -1) {
					if (this.client.options.touchscreen) {
						if (slot != null && slot.hasStack()) {
							this.touchDragSlotStart = slot;
							this.touchDragStack = ItemStack.EMPTY;
							this.touchIsRightClickDrag = button == 1;
						} else {
							this.touchDragSlotStart = null;
						}
					} else if (!this.isCursorDragging) {
						if (this.client.player.inventory.getCursorStack().isEmpty()) {
							if (this.client.options.keyPickItem.matchesMouse(button)) {
								this.onMouseClick(slot, k, button, SlotActionType.CLONE);
							} else {
								boolean bl3 = k != -999
									&& (
										InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), 340)
											|| InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), 344)
									);
								SlotActionType slotActionType = SlotActionType.PICKUP;
								if (bl3) {
									this.quickMovingStack = slot != null && slot.hasStack() ? slot.getStack().copy() : ItemStack.EMPTY;
									slotActionType = SlotActionType.QUICK_MOVE;
								} else if (k == -999) {
									slotActionType = SlotActionType.THROW;
								}

								this.onMouseClick(slot, k, button, slotActionType);
							}

							this.cancelNextRelease = true;
						} else {
							this.isCursorDragging = true;
							this.heldButtonCode = button;
							this.cursorDragSlots.clear();
							if (button == 0) {
								this.heldButtonType = 0;
							} else if (button == 1) {
								this.heldButtonType = 1;
							} else if (this.client.options.keyPickItem.matchesMouse(button)) {
								this.heldButtonType = 2;
							}
						}
					}
				}
			}

			this.lastClickedSlot = slot;
			this.lastButtonClickTime = l;
			this.lastClickedButton = button;
			return true;
		}
	}

	protected boolean isClickOutsideBounds(double mouseX, double mouseY, int left, int top, int button) {
		return mouseX < (double)left || mouseY < (double)top || mouseX >= (double)(left + this.backgroundWidth) || mouseY >= (double)(top + this.backgroundHeight);
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		Slot slot = this.getSlotAt(mouseX, mouseY);
		ItemStack itemStack = this.client.player.inventory.getCursorStack();
		if (this.touchDragSlotStart != null && this.client.options.touchscreen) {
			if (button == 0 || button == 1) {
				if (this.touchDragStack.isEmpty()) {
					if (slot != this.touchDragSlotStart && !this.touchDragSlotStart.getStack().isEmpty()) {
						this.touchDragStack = this.touchDragSlotStart.getStack().copy();
					}
				} else if (this.touchDragStack.getCount() > 1 && slot != null && ScreenHandler.canInsertItemIntoSlot(slot, this.touchDragStack, false)) {
					long l = Util.getMeasuringTimeMs();
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
			&& ScreenHandler.canInsertItemIntoSlot(slot, itemStack, true)
			&& slot.canInsert(itemStack)
			&& this.handler.canInsertIntoSlot(slot)) {
			this.cursorDragSlots.add(slot);
			this.calculateOffset();
		}

		return true;
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		Slot slot = this.getSlotAt(mouseX, mouseY);
		int i = this.x;
		int j = this.y;
		boolean bl = this.isClickOutsideBounds(mouseX, mouseY, i, j, button);
		int k = -1;
		if (slot != null) {
			k = slot.id;
		}

		if (bl) {
			k = -999;
		}

		if (this.isDoubleClicking && slot != null && button == 0 && this.handler.canInsertIntoSlot(ItemStack.EMPTY, slot)) {
			if (hasShiftDown()) {
				if (!this.quickMovingStack.isEmpty()) {
					for (Slot slot2 : this.handler.slots) {
						if (slot2 != null
							&& slot2.canTakeItems(this.client.player)
							&& slot2.hasStack()
							&& slot2.inventory == slot.inventory
							&& ScreenHandler.canInsertItemIntoSlot(slot2, this.quickMovingStack, true)) {
							this.onMouseClick(slot2, slot2.id, button, SlotActionType.QUICK_MOVE);
						}
					}
				}
			} else {
				this.onMouseClick(slot, k, button, SlotActionType.PICKUP_ALL);
			}

			this.isDoubleClicking = false;
			this.lastButtonClickTime = 0L;
		} else {
			if (this.isCursorDragging && this.heldButtonCode != button) {
				this.isCursorDragging = false;
				this.cursorDragSlots.clear();
				this.cancelNextRelease = true;
				return true;
			}

			if (this.cancelNextRelease) {
				this.cancelNextRelease = false;
				return true;
			}

			if (this.touchDragSlotStart != null && this.client.options.touchscreen) {
				if (button == 0 || button == 1) {
					if (this.touchDragStack.isEmpty() && slot != this.touchDragSlotStart) {
						this.touchDragStack = this.touchDragSlotStart.getStack();
					}

					boolean bl2 = ScreenHandler.canInsertItemIntoSlot(slot, this.touchDragStack, false);
					if (k != -1 && !this.touchDragStack.isEmpty() && bl2) {
						this.onMouseClick(this.touchDragSlotStart, this.touchDragSlotStart.id, button, SlotActionType.PICKUP);
						this.onMouseClick(slot, k, 0, SlotActionType.PICKUP);
						if (this.client.player.inventory.getCursorStack().isEmpty()) {
							this.touchDropReturningStack = ItemStack.EMPTY;
						} else {
							this.onMouseClick(this.touchDragSlotStart, this.touchDragSlotStart.id, button, SlotActionType.PICKUP);
							this.touchDropX = MathHelper.floor(mouseX - (double)i);
							this.touchDropY = MathHelper.floor(mouseY - (double)j);
							this.touchDropOriginSlot = this.touchDragSlotStart;
							this.touchDropReturningStack = this.touchDragStack;
							this.touchDropTime = Util.getMeasuringTimeMs();
						}
					} else if (!this.touchDragStack.isEmpty()) {
						this.touchDropX = MathHelper.floor(mouseX - (double)i);
						this.touchDropY = MathHelper.floor(mouseY - (double)j);
						this.touchDropOriginSlot = this.touchDragSlotStart;
						this.touchDropReturningStack = this.touchDragStack;
						this.touchDropTime = Util.getMeasuringTimeMs();
					}

					this.touchDragStack = ItemStack.EMPTY;
					this.touchDragSlotStart = null;
				}
			} else if (this.isCursorDragging && !this.cursorDragSlots.isEmpty()) {
				this.onMouseClick(null, -999, ScreenHandler.packQuickCraftData(0, this.heldButtonType), SlotActionType.QUICK_CRAFT);

				for (Slot slot2x : this.cursorDragSlots) {
					this.onMouseClick(slot2x, slot2x.id, ScreenHandler.packQuickCraftData(1, this.heldButtonType), SlotActionType.QUICK_CRAFT);
				}

				this.onMouseClick(null, -999, ScreenHandler.packQuickCraftData(2, this.heldButtonType), SlotActionType.QUICK_CRAFT);
			} else if (!this.client.player.inventory.getCursorStack().isEmpty()) {
				if (this.client.options.keyPickItem.matchesMouse(button)) {
					this.onMouseClick(slot, k, button, SlotActionType.CLONE);
				} else {
					boolean bl2 = k != -999
						&& (
							InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), 340)
								|| InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), 344)
						);
					if (bl2) {
						this.quickMovingStack = slot != null && slot.hasStack() ? slot.getStack().copy() : ItemStack.EMPTY;
					}

					this.onMouseClick(slot, k, button, bl2 ? SlotActionType.QUICK_MOVE : SlotActionType.PICKUP);
				}
			}
		}

		if (this.client.player.inventory.getCursorStack().isEmpty()) {
			this.lastButtonClickTime = 0L;
		}

		this.isCursorDragging = false;
		return true;
	}

	private boolean isPointOverSlot(Slot slot, double pointX, double pointY) {
		return this.isPointWithinBounds(slot.x, slot.y, 16, 16, pointX, pointY);
	}

	protected boolean isPointWithinBounds(int xPosition, int yPosition, int width, int height, double pointX, double pointY) {
		int i = this.x;
		int j = this.y;
		pointX -= (double)i;
		pointY -= (double)j;
		return pointX >= (double)(xPosition - 1)
			&& pointX < (double)(xPosition + width + 1)
			&& pointY >= (double)(yPosition - 1)
			&& pointY < (double)(yPosition + height + 1);
	}

	/**
	 * @see net.minecraft.screen.ScreenHandler#onSlotClick(int, int, net.minecraft.screen.slot.SlotActionType, net.minecraft.entity.player.PlayerEntity)
	 */
	protected void onMouseClick(Slot slot, int invSlot, int clickData, SlotActionType actionType) {
		if (slot != null) {
			invSlot = slot.id;
		}

		this.client.interactionManager.clickSlot(this.handler.syncId, invSlot, clickData, actionType, this.client.player);
	}

	@Override
	public boolean shouldCloseOnEsc() {
		return false;
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (super.keyPressed(keyCode, scanCode, modifiers)) {
			return true;
		} else {
			if (keyCode == 256 || this.client.options.keyInventory.matchesKey(keyCode, scanCode)) {
				this.client.player.closeHandledScreen();
			}

			this.handleHotbarKeyPressed(keyCode, scanCode);
			if (this.focusedSlot != null && this.focusedSlot.hasStack()) {
				if (this.client.options.keyPickItem.matchesKey(keyCode, scanCode)) {
					this.onMouseClick(this.focusedSlot, this.focusedSlot.id, 0, SlotActionType.CLONE);
				} else if (this.client.options.keyDrop.matchesKey(keyCode, scanCode)) {
					this.onMouseClick(this.focusedSlot, this.focusedSlot.id, hasControlDown() ? 1 : 0, SlotActionType.THROW);
				}
			}

			return true;
		}
	}

	protected boolean handleHotbarKeyPressed(int keyCode, int scanCode) {
		if (this.client.player.inventory.getCursorStack().isEmpty() && this.focusedSlot != null) {
			for (int i = 0; i < 9; i++) {
				if (this.client.options.keysHotbar[i].matchesKey(keyCode, scanCode)) {
					this.onMouseClick(this.focusedSlot, this.focusedSlot.id, i, SlotActionType.SWAP);
					return true;
				}
			}
		}

		return false;
	}

	@Override
	public void removed() {
		if (this.client.player != null) {
			this.handler.close(this.client.player);
		}
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	@Override
	public void tick() {
		super.tick();
		if (!this.client.player.isAlive() || this.client.player.removed) {
			this.client.player.closeHandledScreen();
		}
	}

	@Override
	public T getScreenHandler() {
		return this.handler;
	}
}
