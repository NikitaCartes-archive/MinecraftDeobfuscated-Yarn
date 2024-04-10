package net.minecraft.recipe.book;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import java.util.function.IntFunction;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.function.ValueLists;

public enum CraftingRecipeCategory implements StringIdentifiable {
	BUILDING("building", 0),
	REDSTONE("redstone", 1),
	EQUIPMENT("equipment", 2),
	MISC("misc", 3);

	public static final Codec<CraftingRecipeCategory> CODEC = StringIdentifiable.createCodec(CraftingRecipeCategory::values);
	public static final IntFunction<CraftingRecipeCategory> INDEX_TO_VALUE = ValueLists.createIdToValueFunction(
		CraftingRecipeCategory::getIndex, values(), ValueLists.OutOfBoundsHandling.ZERO
	);
	public static final PacketCodec<ByteBuf, CraftingRecipeCategory> PACKET_CODEC = PacketCodecs.indexed(INDEX_TO_VALUE, CraftingRecipeCategory::getIndex);
	private final String id;
	private final int index;

	private CraftingRecipeCategory(final String id, final int index) {
		this.id = id;
		this.index = index;
	}

	@Override
	public String asString() {
		return this.id;
	}

	private int getIndex() {
		return this.index;
	}
}
