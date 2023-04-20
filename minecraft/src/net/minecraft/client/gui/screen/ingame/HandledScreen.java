package net.minecraft.client.gui.screen.ingame;

import com.google.common.collect.Sets;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.InputUtil;
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
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public abstract class HandledScreen<T extends ScreenHandler> extends Screen implements ScreenHandlerProvider<T> {
	public static final Identifier BACKGROUND_TEXTURE = new Identifier("textures/gui/container/inventory.png");
	private static final float field_32318 = 100.0F;
	private static final int field_32319 = 500;
	public static final int field_32322 = 100;
	private static final int field_32321 = 200;
	protected int backgroundWidth = 176;
	protected int backgroundHeight = 166;
	protected int titleX;
	protected int titleY;
	protected int playerInventoryTitleX;
	protected int playerInventoryTitleY;
	protected final T handler;
	protected final Text playerInventoryTitle;
	@Nullable
	protected Slot focusedSlot;
	@Nullable
	private Slot touchDragSlotStart;
	@Nullable
	private Slot touchDropOriginSlot;
	@Nullable
	private Slot touchHoveredSlot;
	@Nullable
	private Slot lastClickedSlot;
	protected int x;
	protected int y;
	private boolean touchIsRightClickDrag;
	private ItemStack touchDragStack = ItemStack.EMPTY;
	private int touchDropX;
	private int touchDropY;
	private long touchDropTime;
	private ItemStack touchDropReturningStack = ItemStack.EMPTY;
	private long touchDropTimer;
	protected final Set<Slot> cursorDragSlots = Sets.<Slot>newHashSet();
	protected boolean cursorDragging;
	private int heldButtonType;
	private int heldButtonCode;
	private boolean cancelNextRelease;
	private int draggedStackRemainder;
	private long lastButtonClickTime;
	private int lastClickedButton;
	private boolean doubleClicking;
	private ItemStack quickMovingStack = ItemStack.EMPTY;

	public HandledScreen(T handler, PlayerInventory inventory, Text title) {
		super(title);
		this.handler = handler;
		this.playerInventoryTitle = inventory.getDisplayName();
		this.cancelNextRelease = true;
		this.titleX = 8;
		this.titleY = 6;
		this.playerInventoryTitleX = 8;
		this.playerInventoryTitleY = this.backgroundHeight - 94;
	}

	@Override
	protected void init() {
		this.x = (this.width - this.backgroundWidth) / 2;
		this.y = (this.height - this.backgroundHeight) / 2;
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		int i = this.x;
		int j = this.y;
		this.drawBackground(context, delta, mouseX, mouseY);
		RenderSystem.disableDepthTest();
		super.render(context, mouseX, mouseY, delta);
		context.getMatrices().push();
		context.getMatrices().translate((float)i, (float)j, 0.0F);
		this.focusedSlot = null;

		for (int k = 0; k < this.handler.slots.size(); k++) {
			Slot slot = this.handler.slots.get(k);
			if (slot.isEnabled()) {
				this.drawSlot(context, slot);
			}

			if (this.isPointOverSlot(slot, (double)mouseX, (double)mouseY) && slot.isEnabled()) {
				this.focusedSlot = slot;
				int l = slot.x;
				int m = slot.y;
				if (this.focusedSlot.method_51306()) {
					drawSlotHighlight(context, l, m, 0);
				}
			}
		}

		this.drawForeground(context, mouseX, mouseY);
		ItemStack itemStack = this.touchDragStack.isEmpty() ? this.handler.getCursorStack() : this.touchDragStack;
		if (!itemStack.isEmpty()) {
			int n = 8;
			int l = this.touchDragStack.isEmpty() ? 8 : 16;
			String string = null;
			if (!this.touchDragStack.isEmpty() && this.touchIsRightClickDrag) {
				itemStack = itemStack.copyWithCount(MathHelper.ceil((float)itemStack.getCount() / 2.0F));
			} else if (this.cursorDragging && this.cursorDragSlots.size() > 1) {
				itemStack = itemStack.copyWithCount(this.draggedStackRemainder);
				if (itemStack.isEmpty()) {
					string = Formatting.YELLOW + "0";
				}
			}

			this.drawItem(context, itemStack, mouseX - i - 8, mouseY - j - l, string);
		}

		if (!this.touchDropReturningStack.isEmpty()) {
			float f = (float)(Util.getMeasuringTimeMs() - this.touchDropTime) / 100.0F;
			if (f >= 1.0F) {
				f = 1.0F;
				this.touchDropReturningStack = ItemStack.EMPTY;
			}

			int l = this.touchDropOriginSlot.x - this.touchDropX;
			int m = this.touchDropOriginSlot.y - this.touchDropY;
			int o = this.touchDropX + (int)((float)l * f);
			int p = this.touchDropY + (int)((float)m * f);
			this.drawItem(context, this.touchDropReturningStack, o, p, null);
		}

		context.getMatrices().pop();
		RenderSystem.enableDepthTest();
	}

	public static void drawSlotHighlight(DrawContext context, int x, int y, int z) {
		RenderSystem.disableDepthTest();
		RenderSystem.colorMask(true, true, true, false);
		context.fillGradient(x, y, x + 16, y + 16, z, -2130706433, -2130706433);
		RenderSystem.colorMask(true, true, true, true);
		RenderSystem.enableDepthTest();
	}

	protected void drawMouseoverTooltip(DrawContext context, int x, int y) {
		if (this.handler.getCursorStack().isEmpty() && this.focusedSlot != null && this.focusedSlot.hasStack()) {
			ItemStack itemStack = this.focusedSlot.getStack();
			context.drawTooltip(this.textRenderer, this.getTooltipFromItem(itemStack), itemStack.getTooltipData(), x, y);
		}
	}

	protected List<Text> getTooltipFromItem(ItemStack stack) {
		return getTooltipFromItem(this.client, stack);
	}

	private void drawItem(DrawContext context, ItemStack stack, int x, int y, String amountText) {
		context.getMatrices().push();
		context.getMatrices().translate(0.0F, 0.0F, 232.0F);
		context.drawItem(stack, x, y);
		context.drawItemInSlot(this.textRenderer, stack, x, y - (this.touchDragStack.isEmpty() ? 0 : 8), amountText);
		context.getMatrices().pop();
	}

	protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
		context.drawText(this.textRenderer, this.title, this.titleX, this.titleY, 4210752, false);
		context.drawText(this.textRenderer, this.playerInventoryTitle, this.playerInventoryTitleX, this.playerInventoryTitleY, 4210752, false);
	}

	protected abstract void drawBackground(DrawContext context, float delta, int mouseX, int mouseY);

	private void drawSlot(DrawContext context, Slot slot) {
		int i = slot.x;
		int j = slot.y;
		ItemStack itemStack = slot.getStack();
		boolean bl = false;
		boolean bl2 = slot == this.touchDragSlotStart && !this.touchDragStack.isEmpty() && !this.touchIsRightClickDrag;
		ItemStack itemStack2 = this.handler.getCursorStack();
		String string = null;
		if (slot == this.touchDragSlotStart && !this.touchDragStack.isEmpty() && this.touchIsRightClickDrag && !itemStack.isEmpty()) {
			itemStack = itemStack.copyWithCount(itemStack.getCount() / 2);
		} else if (this.cursorDragging && this.cursorDragSlots.contains(slot) && !itemStack2.isEmpty()) {
			if (this.cursorDragSlots.size() == 1) {
				return;
			}

			if (ScreenHandler.canInsertItemIntoSlot(slot, itemStack2, true) && this.handler.canInsertIntoSlot(slot)) {
				bl = true;
				int k = Math.min(itemStack2.getMaxCount(), slot.getMaxItemCount(itemStack2));
				int l = slot.getStack().isEmpty() ? 0 : slot.getStack().getCount();
				int m = ScreenHandler.calculateStackSize(this.cursorDragSlots, this.heldButtonType, itemStack2) + l;
				if (m > k) {
					m = k;
					string = Formatting.YELLOW.toString() + k;
				}

				itemStack = itemStack2.copyWithCount(m);
			} else {
				this.cursorDragSlots.remove(slot);
				this.calculateOffset();
			}
		}

		context.getMatrices().push();
		context.getMatrices().translate(0.0F, 0.0F, 100.0F);
		if (itemStack.isEmpty() && slot.isEnabled()) {
			Pair<Identifier, Identifier> pair = slot.getBackgroundSprite();
			if (pair != null) {
				Sprite sprite = (Sprite)this.client.getSpriteAtlas(pair.getFirst()).apply(pair.getSecond());
				context.drawSprite(i, j, 0, 16, 16, sprite);
				bl2 = true;
			}
		}

		if (!bl2) {
			if (bl) {
				context.fill(i, j, i + 16, j + 16, -2130706433);
			}

			context.drawItem(itemStack, i, j, slot.x + slot.y * this.backgroundWidth);
			context.drawItemInSlot(this.textRenderer, itemStack, i, j, string);
		}

		context.getMatrices().pop();
	}

	private void calculateOffset() {
		ItemStack itemStack = this.handler.getCursorStack();
		if (!itemStack.isEmpty() && this.cursorDragging) {
			if (this.heldButtonType == 2) {
				this.draggedStackRemainder = itemStack.getMaxCount();
			} else {
				this.draggedStackRemainder = itemStack.getCount();

				for (Slot slot : this.cursorDragSlots) {
					ItemStack itemStack2 = slot.getStack();
					int i = itemStack2.isEmpty() ? 0 : itemStack2.getCount();
					int j = Math.min(itemStack.getMaxCount(), slot.getMaxItemCount(itemStack));
					int k = Math.min(ScreenHandler.calculateStackSize(this.cursorDragSlots, this.heldButtonType, itemStack) + i, j);
					this.draggedStackRemainder -= k - i;
				}
			}
		}
	}

	@Nullable
	private Slot getSlotAt(double x, double y) {
		for (int i = 0; i < this.handler.slots.size(); i++) {
			Slot slot = this.handler.slots.get(i);
			if (this.isPointOverSlot(slot, x, y) && slot.isEnabled()) {
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
			boolean bl = this.client.options.pickItemKey.matchesMouse(button) && this.client.interactionManager.hasCreativeInventory();
			Slot slot = this.getSlotAt(mouseX, mouseY);
			long l = Util.getMeasuringTimeMs();
			this.doubleClicking = this.lastClickedSlot == slot && l - this.lastButtonClickTime < 250L && this.lastClickedButton == button;
			this.cancelNextRelease = false;
			if (button != 0 && button != GLFW.GLFW_MOUSE_BUTTON_RIGHT && !bl) {
				this.onMouseClick(button);
			} else {
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

				if (this.client.options.getTouchscreen().getValue() && bl2 && this.handler.getCursorStack().isEmpty()) {
					this.close();
					return true;
				}

				if (k != -1) {
					if (this.client.options.getTouchscreen().getValue()) {
						if (slot != null && slot.hasStack()) {
							this.touchDragSlotStart = slot;
							this.touchDragStack = ItemStack.EMPTY;
							this.touchIsRightClickDrag = button == GLFW.GLFW_MOUSE_BUTTON_RIGHT;
						} else {
							this.touchDragSlotStart = null;
						}
					} else if (!this.cursorDragging) {
						if (this.handler.getCursorStack().isEmpty()) {
							if (bl) {
								this.onMouseClick(slot, k, button, SlotActionType.CLONE);
							} else {
								boolean bl3 = k != -999
									&& (
										InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), GLFW.GLFW_KEY_LEFT_SHIFT)
											|| InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), GLFW.GLFW_KEY_RIGHT_SHIFT)
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
							this.cursorDragging = true;
							this.heldButtonCode = button;
							this.cursorDragSlots.clear();
							if (button == 0) {
								this.heldButtonType = 0;
							} else if (button == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
								this.heldButtonType = 1;
							} else if (bl) {
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

	private void onMouseClick(int button) {
		if (this.focusedSlot != null && this.handler.getCursorStack().isEmpty()) {
			if (this.client.options.swapHandsKey.matchesMouse(button)) {
				this.onMouseClick(this.focusedSlot, this.focusedSlot.id, 40, SlotActionType.SWAP);
				return;
			}

			for (int i = 0; i < 9; i++) {
				if (this.client.options.hotbarKeys[i].matchesMouse(button)) {
					this.onMouseClick(this.focusedSlot, this.focusedSlot.id, i, SlotActionType.SWAP);
				}
			}
		}
	}

	protected boolean isClickOutsideBounds(double mouseX, double mouseY, int left, int top, int button) {
		return mouseX < (double)left || mouseY < (double)top || mouseX >= (double)(left + this.backgroundWidth) || mouseY >= (double)(top + this.backgroundHeight);
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		Slot slot = this.getSlotAt(mouseX, mouseY);
		ItemStack itemStack = this.handler.getCursorStack();
		if (this.touchDragSlotStart != null && this.client.options.getTouchscreen().getValue()) {
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
		} else if (this.cursorDragging
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
		int k = GLFW.GLFW_KEY_UNKNOWN;
		if (slot != null) {
			k = slot.id;
		}

		if (bl) {
			k = -999;
		}

		if (this.doubleClicking && slot != null && button == 0 && this.handler.canInsertIntoSlot(ItemStack.EMPTY, slot)) {
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

			this.doubleClicking = false;
			this.lastButtonClickTime = 0L;
		} else {
			if (this.cursorDragging && this.heldButtonCode != button) {
				this.cursorDragging = false;
				this.cursorDragSlots.clear();
				this.cancelNextRelease = true;
				return true;
			}

			if (this.cancelNextRelease) {
				this.cancelNextRelease = false;
				return true;
			}

			if (this.touchDragSlotStart != null && this.client.options.getTouchscreen().getValue()) {
				if (button == 0 || button == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
					if (this.touchDragStack.isEmpty() && slot != this.touchDragSlotStart) {
						this.touchDragStack = this.touchDragSlotStart.getStack();
					}

					boolean bl2 = ScreenHandler.canInsertItemIntoSlot(slot, this.touchDragStack, false);
					if (k != GLFW.GLFW_KEY_UNKNOWN && !this.touchDragStack.isEmpty() && bl2) {
						this.onMouseClick(this.touchDragSlotStart, this.touchDragSlotStart.id, button, SlotActionType.PICKUP);
						this.onMouseClick(slot, k, 0, SlotActionType.PICKUP);
						if (this.handler.getCursorStack().isEmpty()) {
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

					this.endTouchDrag();
				}
			} else if (this.cursorDragging && !this.cursorDragSlots.isEmpty()) {
				this.onMouseClick(null, -999, ScreenHandler.packQuickCraftData(0, this.heldButtonType), SlotActionType.QUICK_CRAFT);

				for (Slot slot2x : this.cursorDragSlots) {
					this.onMouseClick(slot2x, slot2x.id, ScreenHandler.packQuickCraftData(1, this.heldButtonType), SlotActionType.QUICK_CRAFT);
				}

				this.onMouseClick(null, -999, ScreenHandler.packQuickCraftData(2, this.heldButtonType), SlotActionType.QUICK_CRAFT);
			} else if (!this.handler.getCursorStack().isEmpty()) {
				if (this.client.options.pickItemKey.matchesMouse(button)) {
					this.onMouseClick(slot, k, button, SlotActionType.CLONE);
				} else {
					boolean bl2 = k != -999
						&& (
							InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), GLFW.GLFW_KEY_LEFT_SHIFT)
								|| InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), GLFW.GLFW_KEY_RIGHT_SHIFT)
						);
					if (bl2) {
						this.quickMovingStack = slot != null && slot.hasStack() ? slot.getStack().copy() : ItemStack.EMPTY;
					}

					this.onMouseClick(slot, k, button, bl2 ? SlotActionType.QUICK_MOVE : SlotActionType.PICKUP);
				}
			}
		}

		if (this.handler.getCursorStack().isEmpty()) {
			this.lastButtonClickTime = 0L;
		}

		this.cursorDragging = false;
		return true;
	}

	public void endTouchDrag() {
		this.touchDragStack = ItemStack.EMPTY;
		this.touchDragSlotStart = null;
	}

	private boolean isPointOverSlot(Slot slot, double pointX, double pointY) {
		return this.isPointWithinBounds(slot.x, slot.y, 16, 16, pointX, pointY);
	}

	protected boolean isPointWithinBounds(int x, int y, int width, int height, double pointX, double pointY) {
		int i = this.x;
		int j = this.y;
		pointX -= (double)i;
		pointY -= (double)j;
		return pointX >= (double)(x - 1) && pointX < (double)(x + width + 1) && pointY >= (double)(y - 1) && pointY < (double)(y + height + 1);
	}

	/**
	 * @see net.minecraft.screen.ScreenHandler#onSlotClick(int, int, net.minecraft.screen.slot.SlotActionType, net.minecraft.entity.player.PlayerEntity)
	 */
	protected void onMouseClick(Slot slot, int slotId, int button, SlotActionType actionType) {
		if (slot != null) {
			slotId = slot.id;
		}

		this.client.interactionManager.clickSlot(this.handler.syncId, slotId, button, actionType, this.client.player);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (super.keyPressed(keyCode, scanCode, modifiers)) {
			return true;
		} else if (this.client.options.inventoryKey.matchesKey(keyCode, scanCode)) {
			this.close();
			return true;
		} else {
			this.handleHotbarKeyPressed(keyCode, scanCode);
			if (this.focusedSlot != null && this.focusedSlot.hasStack()) {
				if (this.client.options.pickItemKey.matchesKey(keyCode, scanCode)) {
					this.onMouseClick(this.focusedSlot, this.focusedSlot.id, 0, SlotActionType.CLONE);
				} else if (this.client.options.dropKey.matchesKey(keyCode, scanCode)) {
					this.onMouseClick(this.focusedSlot, this.focusedSlot.id, hasControlDown() ? 1 : 0, SlotActionType.THROW);
				}
			}

			return true;
		}
	}

	protected boolean handleHotbarKeyPressed(int keyCode, int scanCode) {
		if (this.handler.getCursorStack().isEmpty() && this.focusedSlot != null) {
			if (this.client.options.swapHandsKey.matchesKey(keyCode, scanCode)) {
				this.onMouseClick(this.focusedSlot, this.focusedSlot.id, 40, SlotActionType.SWAP);
				return true;
			}

			for (int i = 0; i < 9; i++) {
				if (this.client.options.hotbarKeys[i].matchesKey(keyCode, scanCode)) {
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
			this.handler.onClosed(this.client.player);
		}
	}

	@Override
	public boolean shouldPause() {
		return false;
	}

	@Override
	public final void tick() {
		super.tick();
		if (this.client.player.isAlive() && !this.client.player.isRemoved()) {
			this.handledScreenTick();
		} else {
			this.client.player.closeHandledScreen();
		}
	}

	protected void handledScreenTick() {
	}

	@Override
	public T getScreenHandler() {
		return this.handler;
	}

	@Override
	public void close() {
		this.client.player.closeHandledScreen();
		super.close();
	}
}
