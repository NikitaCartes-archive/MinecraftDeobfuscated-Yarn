package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.class_4626;
import net.minecraft.class_4640;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ModifiableTestableWorld;

public class OakTreeFeature extends class_4626<class_4640> {
	public OakTreeFeature(Function<Dynamic<?>, ? extends class_4640> function) {
		super(function);
	}

	public boolean method_23402(
		ModifiableTestableWorld modifiableTestableWorld, Random random, BlockPos blockPos, Set<BlockPos> set, Set<BlockPos> set2, BlockBox blockBox, class_4640 arg
	) {
		int i = arg.field_21291 + random.nextInt(arg.field_21260 + 1) + random.nextInt(arg.field_21261 + 1);
		int j = arg.field_21262 >= 0 ? arg.field_21262 + random.nextInt(arg.field_21263 + 1) : i - (arg.field_21266 + random.nextInt(arg.field_21267 + 1));
		int k = arg.field_21259.method_23452(random, j, i, arg);
		Optional<BlockPos> optional = this.method_23378(modifiableTestableWorld, i, j, k, blockPos, arg);
		if (!optional.isPresent()) {
			return false;
		} else {
			BlockPos blockPos2 = (BlockPos)optional.get();
			this.setToDirt(modifiableTestableWorld, blockPos2.method_10074());
			arg.field_21259.method_23448(modifiableTestableWorld, random, arg, i, j, k, blockPos2, set2);
			this.method_23379(modifiableTestableWorld, random, i, blockPos2, arg.field_21264 + random.nextInt(arg.field_21265 + 1), set, blockBox, arg);
			return true;
		}
	}
}
