package net.minecraft.client.gui.screen.ingame;

import java.util.List;
import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.util.Util;
import org.apache.commons.lang3.StringUtils;

@Environment(EnvType.CLIENT)
public class EnchantingPhrases {
	private static final EnchantingPhrases INSTANCE = new EnchantingPhrases();
	private final Random random = new Random();
	private final String[] phrases = "the elder scrolls klaatu berata niktu xyzzy bless curse light darkness fire air earth water hot dry cold wet ignite snuff embiggen twist shorten stretch fiddle destroy imbue galvanize enchant free limited range of towards inside sphere cube self other ball mental physical grow shrink demon elemental spirit animal creature beast humanoid undead fresh stale phnglui mglwnafh cthulhu rlyeh wgahnagl fhtagnbaguette"
		.split(" ");

	private EnchantingPhrases() {
	}

	public static EnchantingPhrases getInstance() {
		return INSTANCE;
	}

	public String generatePhrase(TextRenderer fontRenderer, int width) {
		int i = this.random.nextInt(2) + 3;
		String string = "";

		for (int j = 0; j < i; j++) {
			if (j > 0) {
				string = string + " ";
			}

			string = string + Util.getRandom(this.phrases, this.random);
		}

		List<String> list = fontRenderer.wrapStringToWidthAsList(string, width);
		return StringUtils.join(list.size() >= 2 ? list.subList(0, 2) : list, " ");
	}

	public void setSeed(long seed) {
		this.random.setSeed(seed);
	}
}
