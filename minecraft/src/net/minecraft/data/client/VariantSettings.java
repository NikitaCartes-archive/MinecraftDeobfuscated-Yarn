package net.minecraft.data.client;

import com.google.gson.JsonPrimitive;
import net.minecraft.util.Identifier;

public class VariantSettings {
	public static final VariantSetting<VariantSettings.Rotation> X = new VariantSetting<>("x", rotation -> new JsonPrimitive(rotation.degrees));
	public static final VariantSetting<VariantSettings.Rotation> Y = new VariantSetting<>("y", rotation -> new JsonPrimitive(rotation.degrees));
	public static final VariantSetting<Identifier> MODEL = new VariantSetting<>("model", id -> new JsonPrimitive(id.toString()));
	public static final VariantSetting<Boolean> UVLOCK = new VariantSetting<>("uvlock", JsonPrimitive::new);
	public static final VariantSetting<Integer> WEIGHT = new VariantSetting<>("weight", JsonPrimitive::new);

	public static enum Rotation {
		R0(0),
		R90(90),
		R180(180),
		R270(270);

		final int degrees;

		private Rotation(final int degrees) {
			this.degrees = degrees;
		}
	}
}
