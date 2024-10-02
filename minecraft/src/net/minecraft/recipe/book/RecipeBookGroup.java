package net.minecraft.recipe.book;

import io.netty.buffer.ByteBuf;
import java.util.function.IntFunction;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.function.ValueLists;

public enum RecipeBookGroup implements RecipeBookCategory {
	CRAFTING_BUILDING_BLOCKS(0),
	CRAFTING_REDSTONE(1),
	CRAFTING_EQUIPMENT(2),
	CRAFTING_MISC(3),
	FURNACE_FOOD(4),
	FURNACE_BLOCKS(5),
	FURNACE_MISC(6),
	BLAST_FURNACE_BLOCKS(7),
	BLAST_FURNACE_MISC(8),
	SMOKER_FOOD(9),
	STONECUTTER(10),
	SMITHING(11),
	CAMPFIRE(12);

	public static final IntFunction<RecipeBookGroup> ID_TO_VALUE = ValueLists.createIdToValueFunction(
		group -> group.id, values(), ValueLists.OutOfBoundsHandling.ZERO
	);
	public static final PacketCodec<ByteBuf, RecipeBookGroup> PACKET_CODEC = PacketCodecs.indexed(ID_TO_VALUE, group -> group.id);
	private final int id;

	private RecipeBookGroup(final int id) {
		this.id = id;
	}
}
