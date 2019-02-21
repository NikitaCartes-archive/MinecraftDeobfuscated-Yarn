package net.minecraft.client.gui;

import com.google.common.collect.Sets;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GuiLighting;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.InputUtil;
import net.minecraft.container.Container;
import net.minecraft.container.Slot;
import net.minecraft.container.SlotActionType;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TextFormat;
import net.minecraft.util.Identifier;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public abstract class ContainerScreen<T extends Container> extends Screen implements ContainerProvider<T> {
	public static final Identifier BACKGROUND_TEXTURE = new Identifier("textures/gui/container/inventory.png");
	public int width = 176;
	protected int height = 166;
	public final T container;
	protected final PlayerInventory playerInventory;
	protected final TextComponent name;
	public int left;
	protected int top;
	protected Slot focusedSlot;
	private Slot field_2777;
	private boolean field_2789;
	private ItemStack field_2782 = ItemStack.EMPTY;
	private int field_2784;
	private int field_2796;
	private Slot field_2802;
	private long field_2795;
	private ItemStack field_2785 = ItemStack.EMPTY;
	private Slot field_2780;
	private long field_2781;
	protected final Set<Slot> slots = Sets.<Slot>newHashSet();
	protected boolean field_2794;
	private int field_2790;
	private int field_2778;
	private boolean field_2798;
	private int field_2803;
	private long field_2788;
	private Slot field_2799;
	private int field_2786;
	private boolean field_2783;
	private ItemStack field_2791 = ItemStack.EMPTY;

	public ContainerScreen(T container, PlayerInventory playerInventory, TextComponent textComponent) {
		this.container = container;
		this.playerInventory = playerInventory;
		this.name = textComponent;
		this.field_2798 = true;
	}

	@Override
	protected void onInitialized() {
		super.onInitialized();
		this.left = (this.screenWidth - this.width) / 2;
		this.top = (this.screenHeight - this.height) / 2;
	}

	@Override
	public void draw(int i, int j, float f) {
		int k = this.left;
		int l = this.top;
		this.drawBackground(f, i, j);
		GlStateManager.disableRescaleNormal();
		GuiLighting.disable();
		GlStateManager.disableLighting();
		GlStateManager.disableDepthTest();
		super.draw(i, j, f);
		GuiLighting.enableForItems();
		GlStateManager.pushMatrix();
		GlStateManager.translatef((float)k, (float)l, 0.0F);
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.enableRescaleNormal();
		this.focusedSlot = null;
		int m = 240;
		int n = 240;
		GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, 240.0F, 240.0F);
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);

		for (int o = 0; o < this.container.slotList.size(); o++) {
			Slot slot = (Slot)this.container.slotList.get(o);
			if (slot.doDrawHoveringEffect()) {
				this.drawSlot(slot);
			}

			if (this.isPointOverSlot(slot, (double)i, (double)j) && slot.doDrawHoveringEffect()) {
				this.focusedSlot = slot;
				GlStateManager.disableLighting();
				GlStateManager.disableDepthTest();
				int p = slot.xPosition;
				int q = slot.yPosition;
				GlStateManager.colorMask(true, true, true, false);
				this.drawGradientRect(p, q, p + 16, q + 16, -2130706433, -2130706433);
				GlStateManager.colorMask(true, true, true, true);
				GlStateManager.enableLighting();
				GlStateManager.enableDepthTest();
			}
		}

		GuiLighting.disable();
		this.drawForeground(i, j);
		GuiLighting.enableForItems();
		PlayerInventory playerInventory = this.client.player.inventory;
		ItemStack itemStack = this.field_2782.isEmpty() ? playerInventory.getCursorStack() : this.field_2782;
		if (!itemStack.isEmpty()) {
			int p = 8;
			int q = this.field_2782.isEmpty() ? 8 : 16;
			String string = null;
			if (!this.field_2782.isEmpty() && this.field_2789) {
				itemStack = itemStack.copy();
				itemStack.setAmount(MathHelper.ceil((float)itemStack.getAmount() / 2.0F));
			} else if (this.field_2794 && this.slots.size() > 1) {
				itemStack = itemStack.copy();
				itemStack.setAmount(this.field_2803);
				if (itemStack.isEmpty()) {
					string = "" + TextFormat.field_1054 + "0";
				}
			}

			this.drawItem(itemStack, i - k - 8, j - l - q, string);
		}

		if (!this.field_2785.isEmpty()) {
			float g = (float)(SystemUtil.getMeasuringTimeMs() - this.field_2795) / 100.0F;
			if (g >= 1.0F) {
				g = 1.0F;
				this.field_2785 = ItemStack.EMPTY;
			}

			int q = this.field_2802.xPosition - this.field_2784;
			int r = this.field_2802.yPosition - this.field_2796;
			int s = this.field_2784 + (int)((float)q * g);
			int t = this.field_2796 + (int)((float)r * g);
			this.drawItem(this.field_2785, s, t, null);
		}

		GlStateManager.popMatrix();
		GlStateManager.enableLighting();
		GlStateManager.enableDepthTest();
		GuiLighting.enable();
	}

	protected void drawMouseoverTooltip(int i, int j) {
		if (this.client.player.inventory.getCursorStack().isEmpty() && this.focusedSlot != null && this.focusedSlot.hasStack()) {
			this.drawStackTooltip(this.focusedSlot.getStack(), i, j);
		}
	}

	private void drawItem(ItemStack itemStack, int i, int j, String string) {
		GlStateManager.translatef(0.0F, 0.0F, 32.0F);
		this.zOffset = 200.0F;
		this.itemRenderer.zOffset = 200.0F;
		this.itemRenderer.renderGuiItem(itemStack, i, j);
		this.itemRenderer.renderGuiItemOverlay(this.fontRenderer, itemStack, i, j - (this.field_2782.isEmpty() ? 0 : 8), string);
		this.zOffset = 0.0F;
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
		boolean bl2 = slot == this.field_2777 && !this.field_2782.isEmpty() && !this.field_2789;
		ItemStack itemStack2 = this.client.player.inventory.getCursorStack();
		String string = null;
		if (slot == this.field_2777 && !this.field_2782.isEmpty() && this.field_2789 && !itemStack.isEmpty()) {
			itemStack = itemStack.copy();
			itemStack.setAmount(itemStack.getAmount() / 2);
		} else if (this.field_2794 && this.slots.contains(slot) && !itemStack2.isEmpty()) {
			if (this.slots.size() == 1) {
				return;
			}

			if (Container.canInsertItemIntoSlot(slot, itemStack2, true) && this.container.canInsertIntoSlot(slot)) {
				itemStack = itemStack2.copy();
				bl = true;
				Container.calculateStackSize(this.slots, this.field_2790, itemStack, slot.getStack().isEmpty() ? 0 : slot.getStack().getAmount());
				int k = Math.min(itemStack.getMaxAmount(), slot.getMaxStackAmount(itemStack));
				if (itemStack.getAmount() > k) {
					string = TextFormat.field_1054.toString() + k;
					itemStack.setAmount(k);
				}
			} else {
				this.slots.remove(slot);
				this.calculateOffset();
			}
		}

		this.zOffset = 100.0F;
		this.itemRenderer.zOffset = 100.0F;
		if (itemStack.isEmpty() && slot.doDrawHoveringEffect()) {
			String string2 = slot.getBackgroundSprite();
			if (string2 != null) {
				Sprite sprite = this.client.getSpriteAtlas().getSprite(string2);
				GlStateManager.disableLighting();
				this.client.getTextureManager().bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
				this.drawTexturedRect(i, j, sprite, 16, 16);
				GlStateManager.enableLighting();
				bl2 = true;
			}
		}

		if (!bl2) {
			if (bl) {
				drawRect(i, j, i + 16, j + 16, -2130706433);
			}

			GlStateManager.enableDepthTest();
			this.itemRenderer.renderGuiItem(this.client.player, itemStack, i, j);
			this.itemRenderer.renderGuiItemOverlay(this.fontRenderer, itemStack, i, j, string);
		}

		this.itemRenderer.zOffset = 0.0F;
		this.zOffset = 0.0F;
	}

	private void calculateOffset() {
		ItemStack itemStack = this.client.player.inventory.getCursorStack();
		if (!itemStack.isEmpty() && this.field_2794) {
			if (this.field_2790 == 2) {
				this.field_2803 = itemStack.getMaxAmount();
			} else {
				this.field_2803 = itemStack.getAmount();

				for (Slot slot : this.slots) {
					ItemStack itemStack2 = itemStack.copy();
					ItemStack itemStack3 = slot.getStack();
					int i = itemStack3.isEmpty() ? 0 : itemStack3.getAmount();
					Container.calculateStackSize(this.slots, this.field_2790, itemStack2, i);
					int j = Math.min(itemStack2.getMaxAmount(), slot.getMaxStackAmount(itemStack2));
					if (itemStack2.getAmount() > j) {
						itemStack2.setAmount(j);
					}

					this.field_2803 = this.field_2803 - (itemStack2.getAmount() - i);
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
			boolean bl = this.client.options.keyPickItem.matchesMouse(i);
			Slot slot = this.getSlotAt(d, e);
			long l = SystemUtil.getMeasuringTimeMs();
			this.field_2783 = this.field_2799 == slot && l - this.field_2788 < 250L && this.field_2786 == i;
			this.field_2798 = false;
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

				if (this.client.options.touchscreen && bl2 && this.client.player.inventory.getCursorStack().isEmpty()) {
					this.client.openScreen(null);
					return true;
				}

				if (m != -1) {
					if (this.client.options.touchscreen) {
						if (slot != null && slot.hasStack()) {
							this.field_2777 = slot;
							this.field_2782 = ItemStack.EMPTY;
							this.field_2789 = i == 1;
						} else {
							this.field_2777 = null;
						}
					} else if (!this.field_2794) {
						if (this.client.player.inventory.getCursorStack().isEmpty()) {
							if (this.client.options.keyPickItem.matchesMouse(i)) {
								this.onMouseClick(slot, m, i, SlotActionType.field_7796);
							} else {
								boolean bl3 = m != -999
									&& (
										InputUtil.isKeyPressed(MinecraftClient.getInstance().window.getHandle(), 340)
											|| InputUtil.isKeyPressed(MinecraftClient.getInstance().window.getHandle(), 344)
									);
								SlotActionType slotActionType = SlotActionType.field_7790;
								if (bl3) {
									this.field_2791 = slot != null && slot.hasStack() ? slot.getStack().copy() : ItemStack.EMPTY;
									slotActionType = SlotActionType.field_7794;
								} else if (m == -999) {
									slotActionType = SlotActionType.field_7795;
								}

								this.onMouseClick(slot, m, i, slotActionType);
							}

							this.field_2798 = true;
						} else {
							this.field_2794 = true;
							this.field_2778 = i;
							this.slots.clear();
							if (i == 0) {
								this.field_2790 = 0;
							} else if (i == 1) {
								this.field_2790 = 1;
							} else if (this.client.options.keyPickItem.matchesMouse(i)) {
								this.field_2790 = 2;
							}
						}
					}
				}
			}

			this.field_2799 = slot;
			this.field_2788 = l;
			this.field_2786 = i;
			return true;
		}
	}

	protected boolean isClickOutsideBounds(double d, double e, int i, int j, int k) {
		return d < (double)i || e < (double)j || d >= (double)(i + this.width) || e >= (double)(j + this.height);
	}

	@Override
	public boolean mouseDragged(double d, double e, int i, double f, double g) {
		Slot slot = this.getSlotAt(d, e);
		ItemStack itemStack = this.client.player.inventory.getCursorStack();
		if (this.field_2777 != null && this.client.options.touchscreen) {
			if (i == 0 || i == 1) {
				if (this.field_2782.isEmpty()) {
					if (slot != this.field_2777 && !this.field_2777.getStack().isEmpty()) {
						this.field_2782 = this.field_2777.getStack().copy();
					}
				} else if (this.field_2782.getAmount() > 1 && slot != null && Container.canInsertItemIntoSlot(slot, this.field_2782, false)) {
					long l = SystemUtil.getMeasuringTimeMs();
					if (this.field_2780 == slot) {
						if (l - this.field_2781 > 500L) {
							this.onMouseClick(this.field_2777, this.field_2777.id, 0, SlotActionType.field_7790);
							this.onMouseClick(slot, slot.id, 1, SlotActionType.field_7790);
							this.onMouseClick(this.field_2777, this.field_2777.id, 0, SlotActionType.field_7790);
							this.field_2781 = l + 750L;
							this.field_2782.subtractAmount(1);
						}
					} else {
						this.field_2780 = slot;
						this.field_2781 = l;
					}
				}
			}
		} else if (this.field_2794
			&& slot != null
			&& !itemStack.isEmpty()
			&& (itemStack.getAmount() > this.slots.size() || this.field_2790 == 2)
			&& Container.canInsertItemIntoSlot(slot, itemStack, true)
			&& slot.canInsert(itemStack)
			&& this.container.canInsertIntoSlot(slot)) {
			this.slots.add(slot);
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

		if (this.field_2783 && slot != null && i == 0 && this.container.canInsertIntoSlot(ItemStack.EMPTY, slot)) {
			if (isShiftPressed()) {
				if (!this.field_2791.isEmpty()) {
					for (Slot slot2 : this.container.slotList) {
						if (slot2 != null
							&& slot2.canTakeItems(this.client.player)
							&& slot2.hasStack()
							&& slot2.inventory == slot.inventory
							&& Container.canInsertItemIntoSlot(slot2, this.field_2791, true)) {
							this.onMouseClick(slot2, slot2.id, i, SlotActionType.field_7794);
						}
					}
				}
			} else {
				this.onMouseClick(slot, l, i, SlotActionType.field_7793);
			}

			this.field_2783 = false;
			this.field_2788 = 0L;
		} else {
			if (this.field_2794 && this.field_2778 != i) {
				this.field_2794 = false;
				this.slots.clear();
				this.field_2798 = true;
				return true;
			}

			if (this.field_2798) {
				this.field_2798 = false;
				return true;
			}

			if (this.field_2777 != null && this.client.options.touchscreen) {
				if (i == 0 || i == 1) {
					if (this.field_2782.isEmpty() && slot != this.field_2777) {
						this.field_2782 = this.field_2777.getStack();
					}

					boolean bl2 = Container.canInsertItemIntoSlot(slot, this.field_2782, false);
					if (l != -1 && !this.field_2782.isEmpty() && bl2) {
						this.onMouseClick(this.field_2777, this.field_2777.id, i, SlotActionType.field_7790);
						this.onMouseClick(slot, l, 0, SlotActionType.field_7790);
						if (this.client.player.inventory.getCursorStack().isEmpty()) {
							this.field_2785 = ItemStack.EMPTY;
						} else {
							this.onMouseClick(this.field_2777, this.field_2777.id, i, SlotActionType.field_7790);
							this.field_2784 = MathHelper.floor(d - (double)j);
							this.field_2796 = MathHelper.floor(e - (double)k);
							this.field_2802 = this.field_2777;
							this.field_2785 = this.field_2782;
							this.field_2795 = SystemUtil.getMeasuringTimeMs();
						}
					} else if (!this.field_2782.isEmpty()) {
						this.field_2784 = MathHelper.floor(d - (double)j);
						this.field_2796 = MathHelper.floor(e - (double)k);
						this.field_2802 = this.field_2777;
						this.field_2785 = this.field_2782;
						this.field_2795 = SystemUtil.getMeasuringTimeMs();
					}

					this.field_2782 = ItemStack.EMPTY;
					this.field_2777 = null;
				}
			} else if (this.field_2794 && !this.slots.isEmpty()) {
				this.onMouseClick(null, -999, Container.packClickData(0, this.field_2790), SlotActionType.field_7789);

				for (Slot slot2x : this.slots) {
					this.onMouseClick(slot2x, slot2x.id, Container.packClickData(1, this.field_2790), SlotActionType.field_7789);
				}

				this.onMouseClick(null, -999, Container.packClickData(2, this.field_2790), SlotActionType.field_7789);
			} else if (!this.client.player.inventory.getCursorStack().isEmpty()) {
				if (this.client.options.keyPickItem.matchesMouse(i)) {
					this.onMouseClick(slot, l, i, SlotActionType.field_7796);
				} else {
					boolean bl2 = l != -999
						&& (
							InputUtil.isKeyPressed(MinecraftClient.getInstance().window.getHandle(), 340)
								|| InputUtil.isKeyPressed(MinecraftClient.getInstance().window.getHandle(), 344)
						);
					if (bl2) {
						this.field_2791 = slot != null && slot.hasStack() ? slot.getStack().copy() : ItemStack.EMPTY;
					}

					this.onMouseClick(slot, l, i, bl2 ? SlotActionType.field_7794 : SlotActionType.field_7790);
				}
			}
		}

		if (this.client.player.inventory.getCursorStack().isEmpty()) {
			this.field_2788 = 0L;
		}

		this.field_2794 = false;
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

		this.client.interactionManager.method_2906(this.container.syncId, i, j, slotActionType, this.client.player);
	}

	@Override
	public boolean doesEscapeKeyClose() {
		return false;
	}

	@Override
	public boolean keyPressed(int i, int j, int k) {
		if (super.keyPressed(i, j, k)) {
			return true;
		} else {
			if (i == 256 || this.client.options.keyInventory.matchesKey(i, j)) {
				this.client.player.closeGui();
			}

			this.handleHotbarKeyPressed(i, j);
			if (this.focusedSlot != null && this.focusedSlot.hasStack()) {
				if (this.client.options.keyPickItem.matchesKey(i, j)) {
					this.onMouseClick(this.focusedSlot, this.focusedSlot.id, 0, SlotActionType.field_7796);
				} else if (this.client.options.keyDrop.matchesKey(i, j)) {
					this.onMouseClick(this.focusedSlot, this.focusedSlot.id, isControlPressed() ? 1 : 0, SlotActionType.field_7795);
				}
			}

			return true;
		}
	}

	protected boolean handleHotbarKeyPressed(int i, int j) {
		if (this.client.player.inventory.getCursorStack().isEmpty() && this.focusedSlot != null) {
			for (int k = 0; k < 9; k++) {
				if (this.client.options.keysHotbar[k].matchesKey(i, j)) {
					this.onMouseClick(this.focusedSlot, this.focusedSlot.id, k, SlotActionType.field_7791);
					return true;
				}
			}
		}

		return false;
	}

	@Override
	public void onClosed() {
		if (this.client.player != null) {
			this.container.close(this.client.player);
		}
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	@Override
	public void update() {
		super.update();
		if (!this.client.player.isValid() || this.client.player.invalid) {
			this.client.player.closeGui();
		}
	}

	@Override
	public T getContainer() {
		return this.container;
	}
}
