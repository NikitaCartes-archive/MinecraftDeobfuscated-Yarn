package net.minecraft;

import com.mojang.datafixers.Dynamic;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.ModifiableTestableWorld;

public class class_4627 extends class_4626<class_4640> {
	public class_4627(Function<Dynamic<?>, ? extends class_4640> function) {
		super(function);
	}

	public boolean method_23386(
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
			Direction direction = Direction.Type.HORIZONTAL.random(random);
			int l = i - random.nextInt(4) - 1;
			int m = 3 - random.nextInt(3);
			BlockPos.Mutable mutable = new BlockPos.Mutable();
			int n = blockPos2.getX();
			int o = blockPos2.getZ();
			int p = 0;

			for (int q = 0; q < i; q++) {
				int r = blockPos2.getY() + q;
				if (q >= l && m > 0) {
					n += direction.getOffsetX();
					o += direction.getOffsetZ();
					m--;
				}

				if (this.method_23382(modifiableTestableWorld, random, mutable.set(n, r, o), set, blockBox, arg)) {
					p = r;
				}
			}

			BlockPos blockPos3 = new BlockPos(n, p, o);
			arg.field_21259.method_23448(modifiableTestableWorld, random, arg, i, j, k + 1, blockPos3, set2);
			n = blockPos2.getX();
			o = blockPos2.getZ();
			Direction direction2 = Direction.Type.HORIZONTAL.random(random);
			if (direction2 != direction) {
				int rx = l - random.nextInt(2) - 1;
				int s = 1 + random.nextInt(3);
				p = 0;

				for (int t = rx; t < i && s > 0; s--) {
					if (t >= 1) {
						int u = blockPos2.getY() + t;
						n += direction2.getOffsetX();
						o += direction2.getOffsetZ();
						if (this.method_23382(modifiableTestableWorld, random, mutable.set(n, u, o), set, blockBox, arg)) {
							p = u;
						}
					}

					t++;
				}

				if (p > 0) {
					BlockPos blockPos4 = new BlockPos(n, p, o);
					arg.field_21259.method_23448(modifiableTestableWorld, random, arg, i, j, k, blockPos4, set2);
				}
			}

			return true;
		}
	}
}
