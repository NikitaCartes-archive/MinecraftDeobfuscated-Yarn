package net.minecraft.client.realms.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.realms.dto.RealmsServer;
import net.minecraft.client.realms.gui.RealmsPopups;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.StringHelper;

@Environment(EnvType.CLIENT)
public class RealmsSettingsScreen extends RealmsScreen {
	private static final int TEXT_FIELD_WIDTH = 212;
	private static final Text WORLD_NAME_TEXT = Text.translatable("mco.configure.world.name");
	private static final Text WORLD_DESCRIPTION_TEXT = Text.translatable("mco.configure.world.description");
	private final RealmsConfigureWorldScreen parent;
	private final RealmsServer serverData;
	private TextFieldWidget descEdit;
	private TextFieldWidget nameEdit;

	public RealmsSettingsScreen(RealmsConfigureWorldScreen parent, RealmsServer serverData) {
		super(Text.translatable("mco.configure.world.settings.title"));
		this.parent = parent;
		this.serverData = serverData;
	}

	@Override
	public void init() {
		int i = this.width / 2 - 106;
		String string = this.serverData.state == RealmsServer.State.OPEN ? "mco.configure.world.buttons.close" : "mco.configure.world.buttons.open";
		ButtonWidget buttonWidget = ButtonWidget.builder(
				Text.translatable(string),
				button -> {
					if (this.serverData.state == RealmsServer.State.OPEN) {
						this.client
							.setScreen(RealmsPopups.createInfoPopup(this, Text.translatable("mco.configure.world.close.question.line1"), popupScreen -> this.parent.closeTheWorld()));
					} else {
						this.parent.openTheWorld(false);
					}
				}
			)
			.dimensions(this.width / 2 - 53, row(0), 106, 20)
			.build();
		this.addDrawableChild(buttonWidget);
		this.nameEdit = new TextFieldWidget(this.client.textRenderer, i, row(4), 212, 20, Text.translatable("mco.configure.world.name"));
		this.nameEdit.setMaxLength(32);
		this.nameEdit.setText(this.serverData.getName());
		this.addDrawableChild(this.nameEdit);
		this.descEdit = new TextFieldWidget(this.client.textRenderer, i, row(8), 212, 20, Text.translatable("mco.configure.world.description"));
		this.descEdit.setMaxLength(32);
		this.descEdit.setText(this.serverData.getDescription());
		this.addDrawableChild(this.descEdit);
		ButtonWidget buttonWidget2 = this.addDrawableChild(
			ButtonWidget.builder(Text.translatable("mco.configure.world.buttons.done"), button -> this.save()).dimensions(i - 2, row(12), 106, 20).build()
		);
		this.nameEdit.setChangedListener(name -> buttonWidget2.active = !StringHelper.isBlank(name));
		this.addDrawableChild(ButtonWidget.builder(ScreenTexts.CANCEL, button -> this.close()).dimensions(this.width / 2 + 2, row(12), 106, 20).build());
	}

	@Override
	protected void setInitialFocus() {
		this.setInitialFocus(this.nameEdit);
	}

	@Override
	public void close() {
		this.client.setScreen(this.parent);
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		super.render(context, mouseX, mouseY, delta);
		context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 17, Colors.WHITE);
		context.drawText(this.textRenderer, WORLD_NAME_TEXT, this.width / 2 - 106, row(3), Colors.WHITE, false);
		context.drawText(this.textRenderer, WORLD_DESCRIPTION_TEXT, this.width / 2 - 106, row(7), Colors.WHITE, false);
	}

	public void save() {
		this.parent.saveSettings(this.nameEdit.getText(), this.descEdit.getText());
	}
}
