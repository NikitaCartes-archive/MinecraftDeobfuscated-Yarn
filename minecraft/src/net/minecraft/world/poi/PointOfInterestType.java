package net.minecraft.world.poi;

import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.block.BlockState;
import net.minecraft.util.registry.RegistryEntry;

public record PointOfInterestType(Set<BlockState> blockStates, int ticketCount, int searchDistance) {
	public static final Predicate<RegistryEntry<PointOfInterestType>> NONE = type -> false;

	public PointOfInterestType(Set<BlockState> set, int i, int j) {
		set = Set.copyOf(set);
		this.blockStates = set;
		this.ticketCount = i;
		this.searchDistance = j;
	}

	public boolean contains(BlockState state) {
		return this.blockStates.contains(state);
	}
}
