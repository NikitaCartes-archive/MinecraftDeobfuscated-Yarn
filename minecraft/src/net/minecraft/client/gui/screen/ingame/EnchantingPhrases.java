package net.minecraft.client.gui.screen.ingame;

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

@Environment(EnvType.CLIENT)
public class EnchantingPhrases {
	private static final Identifier field_24283 = new Identifier("minecraft", "alt");
	private static final Style field_24284 = Style.EMPTY.withFont(field_24283);
	private static final EnchantingPhrases INSTANCE = new EnchantingPhrases();
	private final Random random = new Random();
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

	public MutableText generatePhrase(TextRenderer fontRenderer, int width) {
		StringBuilder stringBuilder = new StringBuilder();
		int i = this.random.nextInt(2) + 3;

		for (int j = 0; j < i; j++) {
			if (j != 0) {
				stringBuilder.append(" ");
			}

			stringBuilder.append(Util.getRandom(this.phrases, this.random));
		}

		return fontRenderer.getTextHandler().trimToWidth(new LiteralText(stringBuilder.toString()).fillStyle(field_24284), width, Style.EMPTY);
	}

	public void setSeed(long seed) {
		this.random.setSeed(seed);
	}
}
