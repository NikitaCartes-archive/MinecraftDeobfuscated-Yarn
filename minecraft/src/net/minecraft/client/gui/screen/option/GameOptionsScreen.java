package net.minecraft.client.gui.screen.option;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.OptionListWidget;
import net.minecraft.client.gui.widget.ThreePartsLayoutWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public abstract class GameOptionsScreen extends Screen {
	protected final Screen parent;
	protected final GameOptions gameOptions;
	@Nullable
	protected OptionListWidget field_51824;
	public final ThreePartsLayoutWidget layout = new ThreePartsLayoutWidget(this);

	public GameOptionsScreen(Screen parent, GameOptions gameOptions, Text title) {
		super(title);
		this.parent = parent;
		this.gameOptions = gameOptions;
	}

	@Override
	protected void init() {
		this.initHeader();
		this.method_60329();
		this.initFooter();
		this.layout.forEachChild(element -> {
			ClickableWidget var10000 = this.addDrawableChild(element);
		});
		this.initTabNavigation();
	}

	protected void initHeader() {
		this.layout.addHeader(this.title, this.textRenderer);
	}

	protected void method_60329() {
		this.field_51824 = this.layout.addBody(new OptionListWidget(this.client, this.width, this));
		this.method_60325();
	}

	protected abstract void method_60325();

	protected void initFooter() {
		this.layout.addFooter(ButtonWidget.builder(ScreenTexts.DONE, button -> this.close()).width(200).build());
	}

	@Override
	protected void initTabNavigation() {
		this.layout.refreshPositions();
		if (this.field_51824 != null) {
			this.field_51824.position(this.width, this.layout);
		}
	}

	@Override
	public void removed() {
		this.client.options.write();
	}

	@Override
	public void close() {
		if (this.field_51824 != null) {
			this.field_51824.applyAllPendingValues();
		}

		this.client.setScreen(this.parent);
	}
}
