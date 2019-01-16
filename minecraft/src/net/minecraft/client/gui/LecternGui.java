package net.minecraft.client.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.container.Container;
import net.minecraft.container.ContainerListener;
import net.minecraft.container.LecternContainer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.text.TextComponent;
import net.minecraft.util.DefaultedList;

@Environment(EnvType.CLIENT)
public class LecternGui extends WrittenBookGui implements ContainerProvider<LecternContainer> {
	private final LecternContainer lecternContainer;
	private final ContainerListener listener = new ContainerListener() {
		@Override
		public void onContainerRegistered(Container container, DefaultedList<ItemStack> defaultedList) {
			LecternGui.this.updatePageProvider();
		}

		@Override
		public void onContainerSlotUpdate(Container container, int i, ItemStack itemStack) {
			LecternGui.this.updatePageProvider();
		}

		@Override
		public void onContainerPropertyUpdate(Container container, int i, int j) {
			if (i == 0) {
				LecternGui.this.updatePage();
			}
		}
	};

	public LecternGui(LecternContainer lecternContainer, PlayerInventory playerInventory, TextComponent textComponent) {
		this.lecternContainer = lecternContainer;
	}

	public LecternContainer getContainer() {
		return this.lecternContainer;
	}

	@Override
	protected void onInitialized() {
		super.onInitialized();
		this.lecternContainer.addListener(this.listener);
	}

	@Override
	public void close() {
		this.client.player.closeGui();
		super.close();
	}

	@Override
	public void onClosed() {
		super.onClosed();
		this.lecternContainer.removeListener(this.listener);
	}

	@Override
	protected void addCloseButton() {
		if (this.client.player.canModifyWorld()) {
			this.addButton(new ButtonWidget(0, this.width / 2 - 100, 196, 98, 20, I18n.translate("gui.done")) {
				@Override
				public void onPressed(double d, double e) {
					LecternGui.this.client.openGui(null);
				}
			});
			this.addButton(new ButtonWidget(3, this.width / 2 + 2, 196, 98, 20, I18n.translate("lectern.take_book")) {
				@Override
				public void onPressed(double d, double e) {
					LecternGui.this.sendButtonPressPacket(3);
				}
			});
		} else {
			super.addCloseButton();
		}
	}

	@Override
	protected void goToPreviousPage() {
		this.sendButtonPressPacket(1);
	}

	@Override
	protected void goToNextPage() {
		this.sendButtonPressPacket(2);
	}

	@Override
	protected boolean method_17789(int i) {
		if (i != this.lecternContainer.method_17419()) {
			this.sendButtonPressPacket(100 + i);
			return true;
		} else {
			return false;
		}
	}

	@Override
	protected void playPageTurnSound() {
	}

	private void sendButtonPressPacket(int i) {
		this.client.interactionManager.clickButton(this.lecternContainer.syncId, i);
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	private void updatePageProvider() {
		ItemStack itemStack = this.lecternContainer.getBookItem();
		this.setPageProvider(WrittenBookGui.class_3931.method_17562(itemStack));
	}

	private void updatePage() {
		this.setPage(this.lecternContainer.method_17419());
	}
}
