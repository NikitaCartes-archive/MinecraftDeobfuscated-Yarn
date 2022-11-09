package net.minecraft.world.poi;

import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.block.BlockState;
import net.minecraft.registry.entry.RegistryEntry;

public record PointOfInterestType(Set<BlockState> blockStates, int ticketCount, int searchDistance) {
	public static final Predicate<RegistryEntry<PointOfInterestType>> NONE = type -> false;

	public PointOfInterestType(Set<BlockState> blockStates, int ticketCount, int searchDistance) {
		blockStates = Set.copyOf(blockStates);
		this.blockStates = blockStates;
		this.ticketCount = ticketCount;
		this.searchDistance = searchDistance;
	}

	public boolean contains(BlockState state) {
		return this.blockStates.contains(state);
	}
}
