package net.minecraft.util;

/**
 * An enum representing an entity's arm.
 * 
 * @see Hand
 */
public enum Arm implements TranslatableOption {
	LEFT(0, "options.mainHand.left"),
	RIGHT(1, "options.mainHand.right");

	private final int id;
	private final String translationKey;

	private Arm(int id, String translationKey) {
		this.id = id;
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
}
