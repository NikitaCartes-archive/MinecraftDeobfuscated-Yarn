package net.minecraft.client.gui.container;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.ContainerProvider;
import net.minecraft.client.gui.WrittenBookScreen;
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
public class LecternScreen extends WrittenBookScreen implements ContainerProvider<LecternContainer> {
	private final LecternContainer lecternContainer;
	private final ContainerListener listener = new ContainerListener() {
		@Override
		public void onContainerRegistered(Container container, DefaultedList<ItemStack> defaultedList) {
			LecternScreen.this.updatePageProvider();
		}

		@Override
		public void onContainerSlotUpdate(Container container, int i, ItemStack itemStack) {
			LecternScreen.this.updatePageProvider();
		}

		@Override
		public void onContainerPropertyUpdate(Container container, int i, int j) {
			if (i == 0) {
				LecternScreen.this.updatePage();
			}
		}
	};

	public LecternScreen(LecternContainer lecternContainer, PlayerInventory playerInventory, TextComponent textComponent) {
		this.lecternContainer = lecternContainer;
	}

	public LecternContainer method_17573() {
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
			this.addButton(new ButtonWidget(this.screenWidth / 2 - 100, 196, 98, 20, I18n.translate("gui.done")) {
				@Override
				public void onPressed(double d, double e) {
					LecternScreen.this.client.openScreen(null);
				}
			});
			this.addButton(new ButtonWidget(this.screenWidth / 2 + 2, 196, 98, 20, I18n.translate("lectern.take_book")) {
				@Override
				public void onPressed(double d, double e) {
					LecternScreen.this.sendButtonPressPacket(3);
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
	protected boolean jumpToPage(int i) {
		if (i != this.lecternContainer.getPage()) {
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
		this.setPageProvider(WrittenBookScreen.Contents.create(itemStack));
	}

	private void updatePage() {
		this.setPage(this.lecternContainer.getPage());
	}
}
