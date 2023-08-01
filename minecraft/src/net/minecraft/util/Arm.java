package net.minecraft.util;

/**
 * An enum representing an entity's arm.
 * 
 * @see Hand
 */
public enum Arm implements TranslatableOption, StringIdentifiable {
	LEFT(0, "left", "options.mainHand.left"),
	RIGHT(1, "right", "options.mainHand.right");

	public static final com.mojang.serialization.Codec<Arm> CODEC = StringIdentifiable.createCodec(Arm::values);
	private final int id;
	private final String name;
	private final String translationKey;

	private Arm(int id, String name, String translationKey) {
		this.id = id;
		this.name = name;
		this.translationKey = translationKey;
	}

	/**
	 * {@return the arm on the opposite side}
	 */
	public Arm getOpposite() {
		return this == LEFT ? RIGHT : LEFT;
	}

	@Override
	public int getId() {
		return this.id;
	}

	@Override
	public String getTranslationKey() {
		return this.translationKey;
	}

	@Override
	public String asString() {
		return this.name;
	}
}
