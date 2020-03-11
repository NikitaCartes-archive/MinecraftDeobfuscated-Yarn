package net.minecraft.datafixer.fix;

import com.mojang.datafixers.schemas.Schema;

public class BeehiveRenameFix extends PointOfInterestRenameFix {
	public BeehiveRenameFix(Schema outputSchema) {
		super(outputSchema, false);
	}

	@Override
	protected String rename(String input) {
		return input.equals("minecraft:bee_hive") ? "minecraft:beehive" : input;
	}
}
