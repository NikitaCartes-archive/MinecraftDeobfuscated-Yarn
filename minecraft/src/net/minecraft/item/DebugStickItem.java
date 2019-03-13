package net.minecraft.item;

import java.util.Collection;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sortme.ChatMessageType;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.Property;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class DebugStickItem extends Item {
	public DebugStickItem(Item.Settings settings) {
		super(settings);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean method_7886(ItemStack itemStack) {
		return true;
	}

	@Override
	public boolean method_7885(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity) {
		if (!world.isClient) {
			this.method_7759(playerEntity, blockState, world, blockPos, false, playerEntity.method_5998(Hand.MAIN));
		}

		return false;
	}

	@Override
	public ActionResult method_7884(ItemUsageContext itemUsageContext) {
		PlayerEntity playerEntity = itemUsageContext.getPlayer();
		World world = itemUsageContext.method_8045();
		if (!world.isClient && playerEntity != null) {
			BlockPos blockPos = itemUsageContext.method_8037();
			this.method_7759(playerEntity, world.method_8320(blockPos), world, blockPos, true, itemUsageContext.getItemStack());
		}

		return ActionResult.field_5812;
	}

	private void method_7759(PlayerEntity playerEntity, BlockState blockState, IWorld iWorld, BlockPos blockPos, boolean bl, ItemStack itemStack) {
		if (playerEntity.isCreativeLevelTwoOp()) {
			Block block = blockState.getBlock();
			StateFactory<Block, BlockState> stateFactory = block.method_9595();
			Collection<Property<?>> collection = stateFactory.getProperties();
			String string = Registry.BLOCK.method_10221(block).toString();
			if (collection.isEmpty()) {
				method_7762(playerEntity, new TranslatableTextComponent(this.getTranslationKey() + ".empty", string));
			} else {
				CompoundTag compoundTag = itemStack.method_7911("DebugProperty");
				String string2 = compoundTag.getString(string);
				Property<?> property = stateFactory.method_11663(string2);
				if (bl) {
					if (property == null) {
						property = (Property<?>)collection.iterator().next();
					}

					BlockState blockState2 = method_7758(blockState, property, playerEntity.isSneaking());
					iWorld.method_8652(blockPos, blockState2, 18);
					method_7762(playerEntity, new TranslatableTextComponent(this.getTranslationKey() + ".update", property.getName(), method_7761(blockState2, property)));
				} else {
					property = method_7760(collection, property, playerEntity.isSneaking());
					String string3 = property.getName();
					compoundTag.putString(string, string3);
					method_7762(playerEntity, new TranslatableTextComponent(this.getTranslationKey() + ".select", string3, method_7761(blockState, property)));
				}
			}
		}
	}

	private static <T extends Comparable<T>> BlockState method_7758(BlockState blockState, Property<T> property, boolean bl) {
		return blockState.method_11657(property, method_7760(property.getValues(), blockState.method_11654(property), bl));
	}

	private static <T> T method_7760(Iterable<T> iterable, @Nullable T object, boolean bl) {
		return bl ? SystemUtil.method_645(iterable, object) : SystemUtil.method_660(iterable, object);
	}

	private static void method_7762(PlayerEntity playerEntity, TextComponent textComponent) {
		((ServerPlayerEntity)playerEntity).sendChatMessage(textComponent, ChatMessageType.field_11733);
	}

	private static <T extends Comparable<T>> String method_7761(BlockState blockState, Property<T> property) {
		return property.getValueAsString(blockState.method_11654(property));
	}
}
