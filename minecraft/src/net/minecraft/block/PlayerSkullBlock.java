package net.minecraft.block;

import com.mojang.authlib.GameProfile;
import com.mojang.serialization.MapCodec;
import javax.annotation.Nullable;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class PlayerSkullBlock extends SkullBlock {
	public static final MapCodec<PlayerSkullBlock> CODEC = createCodec(PlayerSkullBlock::new);

	@Override
	public MapCodec<PlayerSkullBlock> getCodec() {
		return CODEC;
	}

	protected PlayerSkullBlock(AbstractBlock.Settings settings) {
		super(SkullBlock.Type.PLAYER, settings);
	}

	@Override
	public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
		super.onPlaced(world, pos, state, placer, itemStack);
		resolveSkullOwner(world, pos, itemStack);
	}

	public static void resolveSkullOwner(World world, BlockPos pos, ItemStack stack) {
		if (world.getBlockEntity(pos) instanceof SkullBlockEntity skullBlockEntity) {
			GameProfile gameProfile = null;
			if (stack.hasNbt()) {
				NbtCompound nbtCompound = stack.getNbt();
				if (nbtCompound.contains("SkullOwner", NbtElement.COMPOUND_TYPE)) {
					gameProfile = NbtHelper.toGameProfile(nbtCompound.getCompound("SkullOwner"));
				} else if (nbtCompound.contains("SkullOwner", NbtElement.STRING_TYPE) && !Util.isBlank(nbtCompound.getString("SkullOwner"))) {
					gameProfile = new GameProfile(Util.NIL_UUID, nbtCompound.getString("SkullOwner"));
				}
			}

			skullBlockEntity.setOwner(gameProfile);
		}
	}
}
