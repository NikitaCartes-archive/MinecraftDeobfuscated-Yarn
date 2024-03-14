package net.minecraft.client.gui.screen.option;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.gui.widget.OptionListWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class SkinOptionsScreen extends GameOptionsScreen {
	private static final Text TITLE_TEXT = Text.translatable("options.skinCustomisation.title");
	@Nullable
	private OptionListWidget optionListWidget;

	public SkinOptionsScreen(Screen parent, GameOptions gameOptions) {
		super(parent, gameOptions, TITLE_TEXT);
	}

	@Override
	protected void init() {
		this.optionListWidget = this.addDrawableChild(new OptionListWidget(this.client, this.width, this.height, this));
		List<ClickableWidget> list = new ArrayList();

		for (PlayerModelPart playerModelPart : PlayerModelPart.values()) {
			list.add(
				CyclingButtonWidget.onOffBuilder(this.gameOptions.isPlayerModelPartEnabled(playerModelPart))
					.build(playerModelPart.getOptionName(), (button, enabled) -> this.gameOptions.togglePlayerModelPart(playerModelPart, enabled))
			);
		}

		list.add(this.gameOptions.getMainArm().createWidget(this.gameOptions));
		this.optionListWidget.addAll(list);
		super.init();
	}

	@Override
	protected void initTabNavigation() {
		super.initTabNavigation();
		if (this.optionListWidget != null) {
			this.optionListWidget.position(this.width, this.layout);
		}
	}
}
