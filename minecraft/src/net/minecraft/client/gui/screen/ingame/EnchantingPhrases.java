package net.minecraft.client.gui.screen.ingame;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.random.Random;

@Environment(EnvType.CLIENT)
public class EnchantingPhrases {
	private static final Identifier FONT_ID = Identifier.ofVanilla("alt");
	private static final Style STYLE = Style.EMPTY.withFont(FONT_ID);
	private static final EnchantingPhrases INSTANCE = new EnchantingPhrases();
	private final Random random = Random.create();
	private final String[] phrases = new String[]{
		"the",
		"elder",
		"scrolls",
		"klaatu",
		"berata",
		"niktu",
		"xyzzy",
		"bless",
		"curse",
		"light",
		"darkness",
		"fire",
		"air",
		"earth",
		"water",
		"hot",
		"dry",
		"cold",
		"wet",
		"ignite",
		"snuff",
		"embiggen",
		"twist",
		"shorten",
		"stretch",
		"fiddle",
		"destroy",
		"imbue",
		"galvanize",
		"enchant",
		"free",
		"limited",
		"range",
		"of",
		"towards",
		"inside",
		"sphere",
		"cube",
		"self",
		"other",
		"ball",
		"mental",
		"physical",
		"grow",
		"shrink",
		"demon",
		"elemental",
		"spirit",
		"animal",
		"creature",
		"beast",
		"humanoid",
		"undead",
		"fresh",
		"stale",
		"phnglui",
		"mglwnafh",
		"cthulhu",
		"rlyeh",
		"wgahnagl",
		"fhtagn",
		"baguette"
	};

	private EnchantingPhrases() {
	}

	public static EnchantingPhrases getInstance() {
		return INSTANCE;
	}

	public StringVisitable generatePhrase(TextRenderer textRenderer, int width) {
		StringBuilder stringBuilder = new StringBuilder();
		int i = this.random.nextInt(2) + 3;

		for (int j = 0; j < i; j++) {
			if (j != 0) {
				stringBuilder.append(" ");
			}

			stringBuilder.append(Util.getRandom(this.phrases, this.random));
		}

		return textRenderer.getTextHandler().trimToWidth(Text.literal(stringBuilder.toString()).fillStyle(STYLE), width, Style.EMPTY);
	}

	public void setSeed(long seed) {
		this.random.setSeed(seed);
	}
}
