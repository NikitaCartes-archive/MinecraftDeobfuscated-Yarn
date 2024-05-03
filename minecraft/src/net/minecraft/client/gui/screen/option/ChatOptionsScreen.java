package net.minecraft.client.gui.screen.option;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class ChatOptionsScreen extends GameOptionsScreen {
	private static final Text TITLE_TEXT = Text.translatable("options.chat.title");
	@Nullable
	private ClickableWidget field_51823;

	private static SimpleOption<?>[] method_60327(GameOptions gameOptions) {
		return new SimpleOption[]{
			gameOptions.getChatVisibility(),
			gameOptions.getChatColors(),
			gameOptions.getChatLinks(),
			gameOptions.getChatLinksPrompt(),
			gameOptions.getChatOpacity(),
			gameOptions.getTextBackgroundOpacity(),
			gameOptions.getChatScale(),
			gameOptions.getChatLineSpacing(),
			gameOptions.getChatDelay(),
			gameOptions.getChatWidth(),
			gameOptions.getChatHeightFocused(),
			gameOptions.getChatHeightUnfocused(),
			gameOptions.getNarrator(),
			gameOptions.getAutoSuggestions(),
			gameOptions.getHideMatchedNames(),
			gameOptions.getReducedDebugInfo(),
			gameOptions.getOnlyShowSecureChat()
		};
	}

	public ChatOptionsScreen(Screen parent, GameOptions options) {
		super(parent, options, TITLE_TEXT);
	}

	@Override
	public void init() {
		super.init();
		this.field_51823 = this.field_51824.getWidgetFor(this.gameOptions.getNarrator());
		if (this.field_51823 != null) {
			this.field_51823.active = this.client.getNarratorManager().isActive();
		}
	}

	@Override
	protected void method_60325() {
		this.field_51824.addAll(method_60327(this.gameOptions));
	}

	public void method_60326() {
		if (this.field_51823 instanceof CyclingButtonWidget) {
			((CyclingButtonWidget)this.field_51823).setValue(this.gameOptions.getNarrator().getValue());
		}
	}
}
