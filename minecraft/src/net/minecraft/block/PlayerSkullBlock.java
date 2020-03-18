package net.minecraft.block;

import com.mojang.authlib.GameProfile;
import javax.annotation.Nullable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.commons.lang3.StringUtils;

public class PlayerSkullBlock extends SkullBlock {
	protected PlayerSkullBlock(AbstractBlock.Settings settings) {
		super(SkullBlock.Type.PLAYER, settings);
	}

	@Override
	public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
		super.onPlaced(world, pos, state, placer, itemStack);
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (blockEntity instanceof SkullBlockEntity) {
			SkullBlockEntity skullBlockEntity = (SkullBlockEntity)blockEntity;
			GameProfile gameProfile = null;
			if (itemStack.hasTag()) {
				CompoundTag compoundTag = itemStack.getTag();
				if (compoundTag.contains("SkullOwner", 10)) {
					gameProfile = NbtHelper.toGameProfile(compoundTag.getCompound("SkullOwner"));
				} else if (compoundTag.contains("SkullOwner", 8) && !StringUtils.isBlank(compoundTag.getString("SkullOwner"))) {
					gameProfile = new GameProfile(null, compoundTag.getString("SkullOwner"));
				}
			}

			skullBlockEntity.setOwnerAndType(gameProfile);
		}
	}
}
