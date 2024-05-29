package net.minecraft.client.gui.screen.option;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class ChatOptionsScreen extends GameOptionsScreen {
	private static final Text TITLE_TEXT = Text.translatable("options.chat.title");

	private static SimpleOption<?>[] getOptions(GameOptions gameOptions) {
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
	protected void addOptions() {
		this.body.addAll(getOptions(this.gameOptions));
	}
}
