package net.minecraft.client.gui.container;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.ContainerScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.container.AnvilContainer;
import net.minecraft.container.Container;
import net.minecraft.container.ContainerListener;
import net.minecraft.container.Slot;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.packet.RenameItemC2SPacket;
import net.minecraft.text.TextComponent;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class AnvilScreen extends ContainerScreen<AnvilContainer> implements ContainerListener {
	private static final Identifier BG_TEX = new Identifier("textures/gui/container/anvil.png");
	private TextFieldWidget nameField;

	public AnvilScreen(AnvilContainer anvilContainer, PlayerInventory playerInventory, TextComponent textComponent) {
		super(anvilContainer, playerInventory, textComponent);
	}

	@Override
	protected void onInitialized() {
		super.onInitialized();
		this.client.keyboard.enableRepeatEvents(true);
		int i = (this.screenWidth - this.width) / 2;
		int j = (this.screenHeight - this.height) / 2;
		this.nameField = new TextFieldWidget(this.fontRenderer, i + 62, j + 24, 103, 12);
		this.nameField.method_1868(-1);
		this.nameField.method_1860(-1);
		this.nameField.setHasBorder(false);
		this.nameField.setMaxLength(35);
		this.nameField.setChangedListener(this::onRenamed);
		this.listeners.add(this.nameField);
		this.container.addListener(this);
		this.nameField.setFocused(true);
		this.focusOn(this.nameField);
	}

	@Override
	public void onScaleChanged(MinecraftClient minecraftClient, int i, int j) {
		String string = this.nameField.getText();
		this.initialize(minecraftClient, i, j);
		this.nameField.setText(string);
	}

	@Override
	public void onClosed() {
		super.onClosed();
		this.client.keyboard.enableRepeatEvents(false);
		this.container.removeListener(this);
	}

	@Override
	public boolean keyPressed(int i, int j, int k) {
		if (this.nameField.keyPressed(i, j, k)) {
			return true;
		} else {
			return this.nameField.isFocused() && this.nameField.isVisible() && i != 256 ? true : super.keyPressed(i, j, k);
		}
	}

	@Override
	protected void drawForeground(int i, int j) {
		GlStateManager.disableLighting();
		GlStateManager.disableBlend();
		this.fontRenderer.draw(this.name.getFormattedText(), 60.0F, 6.0F, 4210752);
		int k = this.container.getLevelCost();
		if (k > 0) {
			int l = 8453920;
			boolean bl = true;
			String string = I18n.translate("container.repair.cost", k);
			if (k >= 40 && !this.client.player.abilities.creativeMode) {
				string = I18n.translate("container.repair.expensive");
				l = 16736352;
			} else if (!this.container.getSlot(2).hasStack()) {
				bl = false;
			} else if (!this.container.getSlot(2).canTakeItems(this.playerInventory.player)) {
				l = 16736352;
			}

			if (bl) {
				int m = this.width - 8 - this.fontRenderer.getStringWidth(string) - 2;
				int n = 69;
				drawRect(m - 2, 67, this.width - 8, 79, 1325400064);
				this.fontRenderer.drawWithShadow(string, (float)m, 69.0F, l);
			}
		}

		GlStateManager.enableLighting();
	}

	private void onRenamed(String string) {
		if (!string.isEmpty()) {
			String string2 = string;
			Slot slot = this.container.getSlot(0);
			if (slot != null && slot.hasStack() && !slot.getStack().hasDisplayName() && string.equals(slot.getStack().getDisplayName().getString())) {
				string2 = "";
			}

			this.container.setNewItemName(string2);
			this.client.player.networkHandler.sendPacket(new RenameItemC2SPacket(string2));
		}
	}

	@Override
	public void draw(int i, int j, float f) {
		this.drawBackground();
		super.draw(i, j, f);
		this.drawMouseoverTooltip(i, j);
		GlStateManager.disableLighting();
		GlStateManager.disableBlend();
		this.nameField.draw(i, j, f);
	}

	@Override
	protected void drawBackground(float f, int i, int j) {
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.client.getTextureManager().bindTexture(BG_TEX);
		int k = (this.screenWidth - this.width) / 2;
		int l = (this.screenHeight - this.height) / 2;
		this.drawTexturedRect(k, l, 0, 0, this.width, this.height);
		this.drawTexturedRect(k + 59, l + 20, 0, this.height + (this.container.getSlot(0).hasStack() ? 0 : 16), 110, 16);
		if ((this.container.getSlot(0).hasStack() || this.container.getSlot(1).hasStack()) && !this.container.getSlot(2).hasStack()) {
			this.drawTexturedRect(k + 99, l + 45, this.width, 0, 28, 21);
		}
	}

	@Override
	public void onContainerRegistered(Container container, DefaultedList<ItemStack> defaultedList) {
		this.onContainerSlotUpdate(container, 0, container.getSlot(0).getStack());
	}

	@Override
	public void onContainerSlotUpdate(Container container, int i, ItemStack itemStack) {
		if (i == 0) {
			this.nameField.setText(itemStack.isEmpty() ? "" : itemStack.getDisplayName().getString());
			this.nameField.setIsEditable(!itemStack.isEmpty());
		}
	}

	@Override
	public void onContainerPropertyUpdate(Container container, int i, int j) {
	}
}
