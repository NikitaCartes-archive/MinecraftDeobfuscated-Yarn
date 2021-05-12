package net.minecraft.block;

import com.mojang.authlib.GameProfile;
import javax.annotation.Nullable;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
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
		if (world.getBlockEntity(pos) instanceof SkullBlockEntity skullBlockEntity) {
			GameProfile gameProfile = null;
			if (itemStack.hasTag()) {
				NbtCompound nbtCompound = itemStack.getTag();
				if (nbtCompound.contains("SkullOwner", NbtElement.COMPOUND_TYPE)) {
					gameProfile = NbtHelper.toGameProfile(nbtCompound.getCompound("SkullOwner"));
				} else if (nbtCompound.contains("SkullOwner", NbtElement.STRING_TYPE) && !StringUtils.isBlank(nbtCompound.getString("SkullOwner"))) {
					gameProfile = new GameProfile(null, nbtCompound.getString("SkullOwner"));
				}
			}

			skullBlockEntity.setOwner(gameProfile);
		}
	}
}
