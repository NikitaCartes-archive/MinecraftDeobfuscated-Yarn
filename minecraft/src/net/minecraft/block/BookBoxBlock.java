package net.minecraft.block;

import java.util.Random;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class BookBoxBlock extends Block {
	private static final char[] RANDOM_CHARACTERS = new char[]{
		' ', ',', '.', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'
	};
	public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;

	public BookBoxBlock(AbstractBlock.Settings settings) {
		super(settings);
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		Direction direction = state.get(FACING);
		int i = pos.getY();
		int j;
		int k;
		switch (direction) {
			case NORTH:
				j = 15 - pos.getX() & 15;
				k = 0;
				break;
			case SOUTH:
				j = pos.getX() & 15;
				k = 2;
				break;
			case EAST:
				j = 15 - pos.getZ() & 15;
				k = 1;
				break;
			case WEST:
			default:
				j = pos.getZ() & 15;
				k = 3;
		}

		if (j > 0 && j < 15) {
			ChunkPos chunkPos = new ChunkPos(pos);
			String string = chunkPos.x + "/" + chunkPos.z + "/" + k + "/" + j + "/" + i;
			Random random = new Random((long)chunkPos.x);
			Random random2 = new Random((long)chunkPos.z);
			Random random3 = new Random((long)((j << 8) + (i << 4) + k));
			ItemStack itemStack = new ItemStack(Items.WRITTEN_BOOK);
			CompoundTag compoundTag = itemStack.getOrCreateTag();
			ListTag listTag = new ListTag();

			for (int l = 0; l < 16; l++) {
				StringBuilder stringBuilder = new StringBuilder();

				for (int m = 0; m < 128; m++) {
					int n = random.nextInt() + random2.nextInt() + -random3.nextInt();
					stringBuilder.append(RANDOM_CHARACTERS[Math.floorMod(n, RANDOM_CHARACTERS.length)]);
				}

				listTag.add(StringTag.of(Text.Serializer.toJson(new LiteralText(stringBuilder.toString()))));
			}

			compoundTag.put("pages", listTag);
			compoundTag.putString("author", Formatting.OBFUSCATED + "Universe itself");
			compoundTag.putString("title", string);
			dropStack(world, pos.offset(hit.getSide()), itemStack);
			return ActionResult.SUCCESS;
		} else {
			return ActionResult.FAIL;
		}
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		Direction direction = ctx.getPlayerFacing().getOpposite();
		return this.getDefaultState().with(FACING, direction);
	}
}
