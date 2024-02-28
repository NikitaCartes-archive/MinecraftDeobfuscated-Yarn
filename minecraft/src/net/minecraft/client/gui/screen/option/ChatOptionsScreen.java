package net.minecraft.client.gui.screen.option;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class ChatOptionsScreen extends SimpleOptionsScreen {
	private static final Text TITLE_TEXT = Text.translatable("options.chat.title");

	public ChatOptionsScreen(Screen parent, GameOptions options) {
		super(
			parent,
			options,
			TITLE_TEXT,
			new SimpleOption[]{
				options.getChatVisibility(),
				options.getChatColors(),
				options.getChatLinks(),
				options.getChatLinksPrompt(),
				options.getChatOpacity(),
				options.getTextBackgroundOpacity(),
				options.getChatScale(),
				options.getChatLineSpacing(),
				options.getChatDelay(),
				options.getChatWidth(),
				options.getChatHeightFocused(),
				options.getChatHeightUnfocused(),
				options.getNarrator(),
				options.getAutoSuggestions(),
				options.getHideMatchedNames(),
				options.getReducedDebugInfo(),
				options.getOnlyShowSecureChat()
			}
		);
	}
}
