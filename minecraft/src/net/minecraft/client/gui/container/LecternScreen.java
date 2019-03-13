package net.minecraft.client.gui.container;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4185;
import net.minecraft.client.gui.ContainerProvider;
import net.minecraft.client.gui.WrittenBookScreen;
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
		public void method_7634(Container container, DefaultedList<ItemStack> defaultedList) {
			LecternScreen.this.updatePageProvider();
		}

		@Override
		public void method_7635(Container container, int i, ItemStack itemStack) {
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
		this.lecternContainer.method_7596(this.listener);
	}

	@Override
	public void close() {
		this.client.field_1724.closeGui();
		super.close();
	}

	@Override
	public void onClosed() {
		super.onClosed();
		this.lecternContainer.method_7603(this.listener);
	}

	@Override
	protected void addCloseButton() {
		if (this.client.field_1724.canModifyWorld()) {
			this.addButton(new class_4185(this.screenWidth / 2 - 100, 196, 98, 20, I18n.translate("gui.done")) {
				@Override
				public void method_1826() {
					LecternScreen.this.client.method_1507(null);
				}
			});
			this.addButton(new class_4185(this.screenWidth / 2 + 2, 196, 98, 20, I18n.translate("lectern.take_book")) {
				@Override
				public void method_1826() {
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
		this.client.field_1761.clickButton(this.lecternContainer.syncId, i);
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	private void updatePageProvider() {
		ItemStack itemStack = this.lecternContainer.method_17418();
		this.setPageProvider(WrittenBookScreen.Contents.create(itemStack));
	}

	private void updatePage() {
		this.setPage(this.lecternContainer.getPage());
	}
}
