package net.minecraft;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;

public class class_5872 {
	public static final class_5872 field_29055 = new class_5872(false, Blocks.ACACIA_BUTTON.getDefaultState());
	public static final Codec<class_5872> field_29056 = RecordCodecBuilder.create(
		instance -> instance.group(
					Codec.BOOL.optionalFieldOf("debug_mode", Boolean.valueOf(false)).forGetter(class_5872::method_33970),
					BlockState.CODEC.optionalFieldOf("air_state", field_29055.method_33973()).forGetter(class_5872::method_33973)
				)
				.apply(instance, class_5872::new)
	);
	private boolean field_29057;
	private final BlockState field_29058;

	public static class_5872 method_33972(boolean bl, BlockState blockState) {
		return new class_5872(bl, blockState);
	}

	private class_5872(boolean bl, BlockState blockState) {
		this.field_29057 = bl;
		this.field_29058 = blockState;
	}

	public boolean method_33970() {
		return this.field_29057;
	}

	public BlockState method_33973() {
		return this.field_29058;
	}
}
