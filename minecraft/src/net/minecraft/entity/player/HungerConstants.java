package net.minecraft.entity.player;

/**
 * Some constants on hunger values.
 * 
 * @see HungerManager
 */
public class HungerConstants {
	/**
	 * The maximum food level ({@value}) allowed in a hunger manager.
	 */
	public static final int FULL_FOOD_LEVEL = 20;
	public static final float field_30705 = 20.0F;
	/**
	 * The initial saturation level ({@value}) for a newly created hunger manager.
	 */
	public static final float INITIAL_SATURATION_LEVEL = 5.0F;
	public static final float field_30707 = 2.5F;
	/**
	 * A value {@value} that when the exhaustion reaches, the exhaustion minuses itself
	 * by to reduce the saturation or food level.
	 */
	public static final float EXHAUSTION_UNIT = 4.0F;
	/**
	 * When the food tick is a multiple of {@value}, the hunger manager may perform
	 * slow healing or starving logic.
	 */
	public static final int SLOW_HEALING_STARVING_INTERVAL = 80;
	/**
	 * When the food tick is a multiple of {@value}, the hunger manager may perform
	 * fast healing logic.
	 */
	public static final int FAST_HEALING_INTERVAL = 10;
	/**
	 * The minimum food level ({@value}) required for the slow-healing mechanism.
	 */
	public static final int SLOW_HEALING_FOOD_LEVEL = 18;
	/**
	 * The exhaustion from healing each hitpoint ({@value}), used for both fast and
	 * slow healing mechanisms.
	 */
	public static final int EXHAUSTION_PER_HITPOINT = 6;
	/**
	 * The maximum food level ({@value}) permitted for the starving mechanism to run.
	 */
	public static final int STARVING_FOOD_LEVEL = 0;
	public static final float field_30714 = 0.1F;
	public static final float field_30715 = 0.3F;
	public static final float field_30716 = 0.6F;
	public static final float field_30717 = 0.8F;
	public static final float field_30718 = 1.0F;
	public static final float field_30719 = 1.2F;
	public static final float field_30720 = 6.0F;
	public static final float field_30721 = 0.05F;
	public static final float field_30722 = 0.2F;
	public static final float field_30723 = 0.005F;
	public static final float field_30724 = 0.1F;
	public static final float field_30726 = 0.0F;
	public static final float field_30727 = 0.0F;
	public static final float field_30728 = 0.1F;
	public static final float field_30729 = 0.01F;
}
