package net.minecraft.block;

import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.registry.Registries;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class SpawnerBlock extends BlockWithEntity {
	protected SpawnerBlock(AbstractBlock.Settings settings) {
		super(settings);
	}

	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return new MobSpawnerBlockEntity(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
		return checkType(type, BlockEntityType.MOB_SPAWNER, world.isClient ? MobSpawnerBlockEntity::clientTick : MobSpawnerBlockEntity::serverTick);
	}

	@Override
	public void onStacksDropped(BlockState state, ServerWorld world, BlockPos pos, ItemStack stack, boolean dropExperience) {
		super.onStacksDropped(state, world, pos, stack, dropExperience);
		if (dropExperience) {
			int i = 15 + world.random.nextInt(15) + world.random.nextInt(15);
			this.dropExperience(world, pos, i);
		}
	}

	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
	}

	@Override
	public void appendTooltip(ItemStack stack, @Nullable BlockView world, List<Text> tooltip, TooltipContext options) {
		super.appendTooltip(stack, world, tooltip, options);
		Optional<Text> optional = this.getEntityNameForTooltip(stack);
		if (optional.isPresent()) {
			tooltip.add((Text)optional.get());
		} else {
			tooltip.add(ScreenTexts.EMPTY);
			tooltip.add(Text.translatable("block.minecraft.spawner.desc1").formatted(Formatting.GRAY));
			tooltip.add(Text.literal(" ").append(Text.translatable("block.minecraft.spawner.desc2").formatted(Formatting.BLUE)));
		}
	}

	private Optional<Text> getEntityNameForTooltip(ItemStack stack) {
		NbtCompound nbtCompound = BlockItem.getBlockEntityNbt(stack);
		if (nbtCompound != null && nbtCompound.contains("SpawnData", NbtElement.COMPOUND_TYPE)) {
			String string = nbtCompound.getCompound("SpawnData").getCompound("entity").getString("id");
			Identifier identifier = Identifier.tryParse(string);
			if (identifier != null) {
				return Registries.ENTITY_TYPE.getOrEmpty(identifier).map(entityType -> Text.translatable(entityType.getTranslationKey()).formatted(Formatting.GRAY));
			}
		}

		return Optional.empty();
	}
}
