package net.minecraft.client.renderer.debug;

import java.util.Random;
import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class VillagerNamer {
	private static final String[] PREFIX = new String[]{
		"Slim", "Far", "River", "Silly", "Fat", "Thin", "Fish", "Bat", "Dark", "Oak", "Sly", "Bush", "Zen", "Bark", "Cry", "Slack", "Soup", "Grim", "Hook"
	};
	private static final String[] SUFFIX = new String[]{
		"Fox", "Tail", "Jaw", "Whisper", "Twig", "Root", "Finder", "Nose", "Brow", "Blade", "Fry", "Seek", "Tooth", "Foot", "Leaf", "Stone", "Fall", "Face", "Tongue"
	};

	public static String name(UUID uUID) {
		Random random = randomFromUuid(uUID);
		return getRandom(random, PREFIX) + getRandom(random, SUFFIX);
	}

	private static String getRandom(Random random, String[] strings) {
		return strings[random.nextInt(strings.length)];
	}

	private static Random randomFromUuid(UUID uUID) {
		return new Random((long)(uUID.hashCode() >> 2));
	}
}
