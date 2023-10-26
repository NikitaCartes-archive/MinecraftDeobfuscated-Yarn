package net.minecraft.client.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.multiplayer.ConnectScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.gui.widget.SimplePositioningWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.network.ClientConnection;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class ReconfiguringScreen extends Screen {
	private static final int field_45508 = 600;
	private final ClientConnection connection;
	private ButtonWidget disconnectButton;
	private int tick;
	private final DirectionalLayoutWidget layout = DirectionalLayoutWidget.vertical();

	public ReconfiguringScreen(Text title, ClientConnection connection) {
		super(title);
		this.connection = connection;
	}

	@Override
	public boolean shouldCloseOnEsc() {
		return false;
	}

	@Override
	protected void init() {
		this.layout.getMainPositioner().alignHorizontalCenter().margin(10);
		this.layout.add(new TextWidget(this.title, this.textRenderer));
		this.disconnectButton = this.layout
			.add(ButtonWidget.builder(ScreenTexts.DISCONNECT, button -> this.connection.disconnect(ConnectScreen.ABORTED_TEXT)).build());
		this.disconnectButton.active = false;
		this.layout.refreshPositions();
		this.layout.forEachChild(child -> {
			ClickableWidget var10000 = this.addDrawableChild(child);
		});
		this.initTabNavigation();
	}

	@Override
	protected void initTabNavigation() {
		SimplePositioningWidget.setPos(this.layout, this.getNavigationFocus());
	}

	@Override
	public void tick() {
		super.tick();
		this.tick++;
		if (this.tick == 600) {
			this.disconnectButton.active = true;
		}

		if (this.connection.isOpen()) {
			this.connection.tick();
		} else {
			this.connection.handleDisconnection();
		}
	}
}
