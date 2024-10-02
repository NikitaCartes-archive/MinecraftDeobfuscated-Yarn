package net.minecraft.recipe.book;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import java.util.function.IntFunction;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.function.ValueLists;

public enum CookingRecipeCategory implements StringIdentifiable {
	FOOD(0, "food"),
	BLOCKS(1, "blocks"),
	MISC(2, "misc");

	private static final IntFunction<CookingRecipeCategory> BY_ID = ValueLists.createIdToValueFunction(
		category -> category.id, values(), ValueLists.OutOfBoundsHandling.ZERO
	);
	public static final Codec<CookingRecipeCategory> CODEC = StringIdentifiable.createCodec(CookingRecipeCategory::values);
	public static final PacketCodec<ByteBuf, CookingRecipeCategory> PACKET_CODEC = PacketCodecs.indexed(BY_ID, category -> category.id);
	private final int id;
	private final String name;

	private CookingRecipeCategory(final int id, final String name) {
		this.id = id;
		this.name = name;
	}

	@Override
	public String asString() {
		return this.name;
	}
}
