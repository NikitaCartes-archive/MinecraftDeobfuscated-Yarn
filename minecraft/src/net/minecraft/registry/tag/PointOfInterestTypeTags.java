package net.minecraft.registry.tag;

import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.poi.PointOfInterestType;

public class PointOfInterestTypeTags {
	public static final TagKey<PointOfInterestType> ACQUIRABLE_JOB_SITE = of("acquirable_job_site");
	public static final TagKey<PointOfInterestType> VILLAGE = of("village");
	public static final TagKey<PointOfInterestType> BEE_HOME = of("bee_home");

	private PointOfInterestTypeTags() {
	}

	private static TagKey<PointOfInterestType> of(String id) {
		return TagKey.of(RegistryKeys.POINT_OF_INTEREST_TYPE, new Identifier(id));
	}
}
