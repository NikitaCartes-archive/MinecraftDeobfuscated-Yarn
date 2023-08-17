package net.minecraft.client.realms.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.gui.widget.LayoutWidgets;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.client.gui.widget.ThreePartsLayoutWidget;
import net.minecraft.client.realms.dto.RealmsServer;
import net.minecraft.client.realms.task.WorldCreationTask;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Util;

@Environment(EnvType.CLIENT)
public class RealmsCreateRealmScreen extends RealmsScreen {
	private static final Text WORLD_NAME_TEXT = Text.translatable("mco.configure.world.name");
	private static final Text WORLD_DESCRIPTION_TEXT = Text.translatable("mco.configure.world.description");
	private static final int field_45243 = 10;
	private static final int field_45244 = 210;
	private final RealmsServer server;
	private final RealmsMainScreen parent;
	private final ThreePartsLayoutWidget layout = new ThreePartsLayoutWidget(this);
	private TextFieldWidget nameBox;
	private TextFieldWidget descriptionBox;

	public RealmsCreateRealmScreen(RealmsServer server, RealmsMainScreen parent) {
		super(Text.translatable("mco.selectServer.create"));
		this.server = server;
		this.parent = parent;
	}

	@Override
	public void init() {
		this.layout.addHeader(new TextWidget(this.title, this.textRenderer));
		DirectionalLayoutWidget directionalLayoutWidget = this.layout.addBody(DirectionalLayoutWidget.vertical()).spacing(10);
		ButtonWidget buttonWidget = ButtonWidget.builder(Text.translatable("mco.create.world"), button -> this.createWorld()).build();
		buttonWidget.active = false;
		this.nameBox = new TextFieldWidget(this.textRenderer, 210, 20, Text.translatable("mco.configure.world.name"));
		this.nameBox.setChangedListener(name -> buttonWidget.active = !Util.isBlank(name));
		this.descriptionBox = new TextFieldWidget(this.textRenderer, 210, 20, Text.translatable("mco.configure.world.description"));
		directionalLayoutWidget.add(LayoutWidgets.createLabeledWidget(this.textRenderer, this.nameBox, WORLD_NAME_TEXT));
		directionalLayoutWidget.add(LayoutWidgets.createLabeledWidget(this.textRenderer, this.descriptionBox, WORLD_DESCRIPTION_TEXT));
		DirectionalLayoutWidget directionalLayoutWidget2 = this.layout.addFooter(DirectionalLayoutWidget.horizontal().spacing(10));
		directionalLayoutWidget2.add(buttonWidget);
		directionalLayoutWidget2.add(ButtonWidget.builder(ScreenTexts.CANCEL, button -> this.close()).build());
		this.layout.forEachChild(child -> {
			ClickableWidget var10000 = this.addDrawableChild(child);
		});
		this.initTabNavigation();
		this.setInitialFocus(this.nameBox);
	}

	@Override
	protected void initTabNavigation() {
		this.layout.refreshPositions();
	}

	private void createWorld() {
		RealmsResetWorldScreen realmsResetWorldScreen = new RealmsResetWorldScreen(
			this.parent,
			this.server,
			Text.translatable("mco.selectServer.create"),
			Text.translatable("mco.create.world.subtitle"),
			-6250336,
			Text.translatable("mco.create.world.skip"),
			() -> this.client.execute(() -> this.client.setScreen(this.parent.newScreen())),
			() -> this.client.setScreen(this.parent.newScreen())
		);
		realmsResetWorldScreen.setResetTitle(Text.translatable("mco.create.world.reset.title"));
		this.client
			.setScreen(
				new RealmsLongRunningMcoTaskScreen(
					this.parent, new WorldCreationTask(this.server.id, this.nameBox.getText(), this.descriptionBox.getText(), realmsResetWorldScreen)
				)
			);
	}

	@Override
	public void close() {
		this.client.setScreen(this.parent);
	}
}
