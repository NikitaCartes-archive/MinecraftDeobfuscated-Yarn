package net.minecraft.data.server.tag;

import java.util.concurrent.CompletableFuture;
import net.minecraft.data.DataOutput;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.PointOfInterestTypeTags;
import net.minecraft.world.poi.PointOfInterestType;
import net.minecraft.world.poi.PointOfInterestTypes;

public class PointOfInterestTypeTagProvider extends AbstractTagProvider<PointOfInterestType> {
	public PointOfInterestTypeTagProvider(DataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookupFuture) {
		super(output, RegistryKeys.POINT_OF_INTEREST_TYPE, registryLookupFuture);
	}

	@Override
	protected void configure(RegistryWrapper.WrapperLookup lookup) {
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
