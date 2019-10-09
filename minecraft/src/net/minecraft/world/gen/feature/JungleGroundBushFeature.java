package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.class_4643;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.ModifiableTestableWorld;

public class JungleGroundBushFeature extends AbstractTreeFeature<class_4643> {
	public JungleGroundBushFeature(Function<Dynamic<?>, ? extends class_4643> function) {
		super(function);
	}

	@Override
	public boolean generate(
		ModifiableTestableWorld modifiableTestableWorld, Random random, BlockPos blockPos, Set<BlockPos> set, Set<BlockPos> set2, BlockBox blockBox, class_4643 arg
	) {
		blockPos = modifiableTestableWorld.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, blockPos).method_10074();
		if (isNaturalDirtOrGrass(modifiableTestableWorld, blockPos)) {
			blockPos = blockPos.up();
			this.method_23382(modifiableTestableWorld, random, blockPos, set, blockBox, arg);

			for (int i = 0; i <= 2; i++) {
				int j = 2 - i;

				for (int k = -j; k <= j; k++) {
					for (int l = -j; l <= j; l++) {
						if (Math.abs(k) != j || Math.abs(l) != j || random.nextInt(2) != 0) {
							this.method_23383(modifiableTestableWorld, random, new BlockPos(k + blockPos.getX(), i + blockPos.getY(), l + blockPos.getZ()), set2, blockBox, arg);
						}
					}
				}
			}
		}

		return true;
	}
}
