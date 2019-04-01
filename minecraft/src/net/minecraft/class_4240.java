package net.minecraft;

import java.util.Random;
import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_4240 {
	private static final String[] field_18962 = new String[]{
		"Slim", "Far", "River", "Silly", "Fat", "Thin", "Fish", "Bat", "Dark", "Oak", "Sly", "Bush", "Zen", "Bark", "Cry", "Slack", "Soup", "Grim", "Hook"
	};
	private static final String[] field_18963 = new String[]{
		"Fox", "Tail", "Jaw", "Whisper", "Twig", "Root", "Finder", "Nose", "Brow", "Blade", "Fry", "Seek", "Tooth", "Foot", "Leaf", "Stone", "Fall", "Face", "Tongue"
	};

	public static String method_19780(UUID uUID) {
		Random random = method_19781(uUID);
		return method_19779(random, field_18962) + method_19779(random, field_18963);
	}

	private static String method_19779(Random random, String[] strings) {
		return strings[random.nextInt(strings.length)];
	}

	private static Random method_19781(UUID uUID) {
		return new Random((long)(uUID.hashCode() >> 2));
	}
}
