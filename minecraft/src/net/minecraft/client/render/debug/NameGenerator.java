package net.minecraft.client.render.debug;

import java.util.Random;
import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Util;

@Environment(EnvType.CLIENT)
public class NameGenerator {
	private static final String[] PREFIX = new String[]{
		"Slim",
		"Far",
		"River",
		"Silly",
		"Fat",
		"Thin",
		"Fish",
		"Bat",
		"Dark",
		"Oak",
		"Sly",
		"Bush",
		"Zen",
		"Bark",
		"Cry",
		"Slack",
		"Soup",
		"Grim",
		"Hook",
		"Dirt",
		"Mud",
		"Sad",
		"Hard",
		"Crook",
		"Sneak",
		"Stink",
		"Weird",
		"Fire",
		"Soot",
		"Soft",
		"Rough",
		"Cling",
		"Scar"
	};
	private static final String[] SUFFIX = new String[]{
		"Fox",
		"Tail",
		"Jaw",
		"Whisper",
		"Twig",
		"Root",
		"Finder",
		"Nose",
		"Brow",
		"Blade",
		"Fry",
		"Seek",
		"Wart",
		"Tooth",
		"Foot",
		"Leaf",
		"Stone",
		"Fall",
		"Face",
		"Tongue",
		"Voice",
		"Lip",
		"Mouth",
		"Snail",
		"Toe",
		"Ear",
		"Hair",
		"Beard",
		"Shirt",
		"Fist"
	};

	public static String name(UUID uuid) {
		Random random = randomFromUuid(uuid);
		return getRandom(random, PREFIX) + getRandom(random, SUFFIX);
	}

	private static String getRandom(Random random, String[] options) {
		return Util.getRandom(options, random);
	}

	private static Random randomFromUuid(UUID uuid) {
		return new Random((long)(uuid.hashCode() >> 2));
	}
}
