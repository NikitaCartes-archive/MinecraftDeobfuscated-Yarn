package net.minecraft.block;

import com.mojang.authlib.GameProfile;
import javax.annotation.Nullable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.TagHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.commons.lang3.StringUtils;

public class PlayerSkullBlock extends SkullBlock {
	protected PlayerSkullBlock(Block.Settings settings) {
		super(SkullBlock.Type.field_11510, settings);
	}

	@Override
	public void onPlaced(World world, BlockPos blockPos, BlockState blockState, @Nullable LivingEntity livingEntity, ItemStack itemStack) {
		super.onPlaced(world, blockPos, blockState, livingEntity, itemStack);
		BlockEntity blockEntity = world.getBlockEntity(blockPos);
		if (blockEntity instanceof SkullBlockEntity) {
			SkullBlockEntity skullBlockEntity = (SkullBlockEntity)blockEntity;
			GameProfile gameProfile = null;
			if (itemStack.hasTag()) {
				CompoundTag compoundTag = itemStack.getTag();
				if (compoundTag.containsKey("SkullOwner", 10)) {
					gameProfile = TagHelper.deserializeProfile(compoundTag.getCompound("SkullOwner"));
				} else if (compoundTag.containsKey("SkullOwner", 8) && !StringUtils.isBlank(compoundTag.getString("SkullOwner"))) {
					gameProfile = new GameProfile(null, compoundTag.getString("SkullOwner"));
				}
			}

			skullBlockEntity.setOwnerAndType(gameProfile);
		}
	}
}
