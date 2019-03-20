package net.minecraft.client.util;

import java.util.Random;
import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class DebugNameGenerator {
	private static final String[] ADJECTIVES = new String[]{
		"Slim", "Far", "River", "Silly", "Fat", "Thin", "Fish", "Bat", "Dark", "Oak", "Sly", "Bush", "Zen", "Bark", "Cry", "Slack", "Soup", "Grim", "Hook"
	};
	private static final String[] NOUNS = new String[]{
		"Fox", "Tail", "Jaw", "Whisper", "Twig", "Root", "Finder", "Nose", "Brow", "Blade", "Fry", "Seek", "Tooth", "Foot", "Leaf", "Stone", "Fall", "Face", "Tongue"
	};

	public static String getDebugName(UUID uUID) {
		Random random = createRandomForUUID(uUID);
		return getRandom(random, ADJECTIVES) + getRandom(random, NOUNS);
	}

	private static String getRandom(Random random, String[] strings) {
		return strings[random.nextInt(strings.length)];
	}

	private static Random createRandomForUUID(UUID uUID) {
		return new Random((long)(uUID.hashCode() >> 2));
	}
}
