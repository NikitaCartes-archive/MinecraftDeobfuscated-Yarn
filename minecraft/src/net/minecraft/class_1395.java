package net.minecraft;

import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.LeavesBlock;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class class_1395 extends class_1394 {
	public class_1395(MobEntityWithAi mobEntityWithAi, double d) {
		super(mobEntityWithAi, d);
	}

	@Nullable
	@Override
	protected Vec3d method_6302() {
		Vec3d vec3d = null;
		if (this.owner.isInsideWater()) {
			vec3d = class_1414.method_6378(this.owner, 15, 15);
		}

		if (this.owner.getRand().nextFloat() >= this.field_6626) {
			vec3d = this.method_6314();
		}

		return vec3d == null ? super.method_6302() : vec3d;
	}

	@Nullable
	private Vec3d method_6314() {
		BlockPos blockPos = new BlockPos(this.owner);
		BlockPos.Mutable mutable = new BlockPos.Mutable();
		BlockPos.Mutable mutable2 = new BlockPos.Mutable();

		for (BlockPos blockPos2 : BlockPos.iterateBoxPositions(
			MathHelper.floor(this.owner.x - 3.0),
			MathHelper.floor(this.owner.y - 6.0),
			MathHelper.floor(this.owner.z - 3.0),
			MathHelper.floor(this.owner.x + 3.0),
			MathHelper.floor(this.owner.y + 6.0),
			MathHelper.floor(this.owner.z + 3.0)
		)) {
			if (!blockPos.equals(blockPos2)) {
				Block block = this.owner.field_6002.method_8320(mutable2.method_10101(blockPos2).method_10098(Direction.DOWN)).getBlock();
				boolean bl = block instanceof LeavesBlock || block.method_9525(BlockTags.field_15475);
				if (bl && this.owner.field_6002.method_8623(blockPos2) && this.owner.field_6002.method_8623(mutable.method_10101(blockPos2).method_10098(Direction.UP))) {
					return new Vec3d((double)blockPos2.getX(), (double)blockPos2.getY(), (double)blockPos2.getZ());
				}
			}
		}

		return null;
	}
}
