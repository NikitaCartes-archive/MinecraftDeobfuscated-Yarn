package net.minecraft.client.render.debug;

import java.util.UUID;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.util.math.random.AbstractRandom;

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

	public static String name(Entity entity) {
		if (entity instanceof PlayerEntity) {
			return entity.getName().getString();
		} else {
			Text text = entity.getCustomName();
			return text != null ? text.getString() : name(entity.getUuid());
		}
	}

	public static String name(UUID uuid) {
		AbstractRandom abstractRandom = randomFromUuid(uuid);
		return getRandom(abstractRandom, PREFIX) + getRandom(abstractRandom, SUFFIX);
	}

	private static String getRandom(AbstractRandom random, String[] options) {
		return Util.getRandom(options, random);
	}

	private static AbstractRandom randomFromUuid(UUID uuid) {
		return AbstractRandom.createAtomic((long)(uuid.hashCode() >> 2));
	}
}
