package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.CartographyTableScreenHandler;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CartographyTableBlock extends Block {
	public static final MapCodec<CartographyTableBlock> CODEC = createCodec(CartographyTableBlock::new);
	private static final Text TITLE = Text.translatable("container.cartography_table");

	@Override
	public MapCodec<CartographyTableBlock> getCodec() {
		return CODEC;
	}

	protected CartographyTableBlock(AbstractBlock.Settings settings) {
		super(settings);
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
		if (world.isClient) {
			return ActionResult.SUCCESS;
		} else {
			player.openHandledScreen(state.createScreenHandlerFactory(world, pos));
			player.incrementStat(Stats.INTERACT_WITH_CARTOGRAPHY_TABLE);
			return ActionResult.CONSUME;
		}
	}

	@Nullable
	@Override
	public NamedScreenHandlerFactory createScreenHandlerFactory(BlockState state, World world, BlockPos pos) {
		return new SimpleNamedScreenHandlerFactory(
			(syncId, inventory, player) -> new CartographyTableScreenHandler(syncId, inventory, ScreenHandlerContext.create(world, pos)), TITLE
		);
	}
}
