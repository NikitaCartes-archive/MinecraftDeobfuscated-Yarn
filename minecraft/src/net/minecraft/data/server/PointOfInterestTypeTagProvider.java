package net.minecraft.data.server;

import net.minecraft.data.DataGenerator;
import net.minecraft.tag.PointOfInterestTypeTags;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.poi.PointOfInterestType;
import net.minecraft.world.poi.PointOfInterestTypes;

public class PointOfInterestTypeTagProvider extends AbstractTagProvider<PointOfInterestType> {
	public PointOfInterestTypeTagProvider(DataGenerator dataGenerator) {
		super(dataGenerator, Registry.POINT_OF_INTEREST_TYPE);
	}

	@Override
	protected void configure() {
		this.getOrCreateTagBuilder(PointOfInterestTypeTags.ACQUIRABLE_JOB_SITE)
			.add(
				PointOfInterestTypes.ARMORER,
				PointOfInterestTypes.BUTCHER,
				PointOfInterestTypes.CARTOGRAPHER,
				PointOfInterestTypes.CLERIC,
				PointOfInterestTypes.FARMER,
				PointOfInterestTypes.FISHERMAN,
				PointOfInterestTypes.FLETCHER,
				PointOfInterestTypes.LEATHERWORKER,
				PointOfInterestTypes.LIBRARIAN,
				PointOfInterestTypes.MASON,
				PointOfInterestTypes.SHEPHERD,
				PointOfInterestTypes.TOOLSMITH,
				PointOfInterestTypes.WEAPONSMITH
			);
		this.getOrCreateTagBuilder(PointOfInterestTypeTags.VILLAGE)
			.addTag(PointOfInterestTypeTags.ACQUIRABLE_JOB_SITE)
			.add(PointOfInterestTypes.HOME, PointOfInterestTypes.MEETING);
		this.getOrCreateTagBuilder(PointOfInterestTypeTags.BEE_HOME).add(PointOfInterestTypes.BEEHIVE, PointOfInterestTypes.BEE_NEST);
	}
}
