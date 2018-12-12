package net.minecraft.client.gui.container;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.ContainerGui;
import net.minecraft.client.gui.GuiEventListener;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.container.AnvilContainer;
import net.minecraft.container.Container;
import net.minecraft.container.ContainerListener;
import net.minecraft.container.Slot;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.packet.RenameItemServerPacket;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class AnvilGui extends ContainerGui implements ContainerListener {
	private static final Identifier BG_TEX = new Identifier("textures/gui/container/anvil.png");
	private final AnvilContainer anvilContainer;
	private TextFieldWidget nameField;
	private final PlayerInventory playerInv;

	public AnvilGui(PlayerInventory playerInventory, World world) {
		super(new AnvilContainer(playerInventory, world, MinecraftClient.getInstance().player));
		this.playerInv = playerInventory;
		this.anvilContainer = (AnvilContainer)this.container;
	}

	@Override
	public GuiEventListener getFocused() {
		return this.nameField.isFocused() ? this.nameField : null;
	}

	@Override
	protected void onInitialized() {
		super.onInitialized();
		this.client.keyboard.enableRepeatEvents(true);
		int i = (this.width - this.containerWidth) / 2;
		int j = (this.height - this.containerHeight) / 2;
		this.nameField = new TextFieldWidget(0, this.fontRenderer, i + 62, j + 24, 103, 12);
		this.nameField.method_1868(-1);
		this.nameField.method_1860(-1);
		this.nameField.setHasBorder(false);
		this.nameField.setMaxLength(35);
		this.nameField.setChangedListener(this::method_2403);
		this.listeners.add(this.nameField);
		this.container.removeListener(this);
		this.container.addListener(this);
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
	protected void drawForeground(int i, int j) {
		GlStateManager.disableLighting();
		GlStateManager.disableBlend();
		this.fontRenderer.draw(I18n.translate("container.repair"), 60.0F, 6.0F, 4210752);
		if (this.anvilContainer.field_7772 > 0) {
			int k = 8453920;
			boolean bl = true;
			String string = I18n.translate("container.repair.cost", this.anvilContainer.field_7772);
			if (this.anvilContainer.field_7772 >= 40 && !this.client.player.abilities.creativeMode) {
				string = I18n.translate("container.repair.expensive");
				k = 16736352;
			} else if (!this.anvilContainer.getSlot(2).hasStack()) {
				bl = false;
			} else if (!this.anvilContainer.getSlot(2).canTakeItems(this.playerInv.player)) {
				k = 16736352;
			}

			if (bl) {
				int l = this.containerWidth - 8 - this.fontRenderer.getStringWidth(string) - 2;
				int m = 69;
				drawRect(l - 2, 67, this.containerWidth - 8, 79, 1325400064);
				this.fontRenderer.drawWithShadow(string, (float)l, 69.0F, k);
			}
		}

		GlStateManager.enableLighting();
	}

	private void method_2403(int i, String string) {
		if (!string.isEmpty()) {
			String string2 = string;
			Slot slot = this.anvilContainer.getSlot(0);
			if (slot != null && slot.hasStack() && !slot.getStack().hasDisplayName() && string.equals(slot.getStack().getDisplayName().getString())) {
				string2 = "";
			}

			this.anvilContainer.setNewItemName(string2);
			this.client.player.networkHandler.sendPacket(new RenameItemServerPacket(string2));
		}
	}

	@Override
	public void draw(int i, int j, float f) {
		this.drawBackground();
		super.draw(i, j, f);
		this.drawMousoverTooltip(i, j);
		GlStateManager.disableLighting();
		GlStateManager.disableBlend();
		this.nameField.render(i, j, f);
	}

	@Override
	protected void drawBackground(float f, int i, int j) {
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.client.getTextureManager().bindTexture(BG_TEX);
		int k = (this.width - this.containerWidth) / 2;
		int l = (this.height - this.containerHeight) / 2;
		this.drawTexturedRect(k, l, 0, 0, this.containerWidth, this.containerHeight);
		this.drawTexturedRect(k + 59, l + 20, 0, this.containerHeight + (this.anvilContainer.getSlot(0).hasStack() ? 0 : 16), 110, 16);
		if ((this.anvilContainer.getSlot(0).hasStack() || this.anvilContainer.getSlot(1).hasStack()) && !this.anvilContainer.getSlot(2).hasStack()) {
			this.drawTexturedRect(k + 99, l + 45, this.containerWidth, 0, 28, 21);
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

	@Override
	public void onContainerInvRegistered(Container container, Inventory inventory) {
	}
}
