package net.minecraft.data.client;

import com.google.gson.JsonElement;
import java.util.function.Supplier;
import net.minecraft.block.Block;

/**
 * A supplier of a block state JSON definition.
 */
public interface BlockStateSupplier extends Supplier<JsonElement> {
	Block getBlock();
}
