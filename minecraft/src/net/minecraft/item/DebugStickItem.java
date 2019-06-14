package net.minecraft.item;

import java.util.Collection;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.MessageType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.Property;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
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
	public boolean hasEnchantmentGlint(ItemStack itemStack) {
		return true;
	}

	@Override
	public boolean method_7885(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity) {
		if (!world.isClient) {
			this.method_7759(playerEntity, blockState, world, blockPos, false, playerEntity.getStackInHand(Hand.field_5808));
		}

		return false;
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext itemUsageContext) {
		PlayerEntity playerEntity = itemUsageContext.getPlayer();
		World world = itemUsageContext.method_8045();
		if (!world.isClient && playerEntity != null) {
			BlockPos blockPos = itemUsageContext.getBlockPos();
			this.method_7759(playerEntity, world.method_8320(blockPos), world, blockPos, true, itemUsageContext.getStack());
		}

		return ActionResult.field_5812;
	}

	private void method_7759(PlayerEntity playerEntity, BlockState blockState, IWorld iWorld, BlockPos blockPos, boolean bl, ItemStack itemStack) {
		if (playerEntity.isCreativeLevelTwoOp()) {
			Block block = blockState.getBlock();
			StateFactory<Block, BlockState> stateFactory = block.method_9595();
			Collection<Property<?>> collection = stateFactory.getProperties();
			String string = Registry.BLOCK.getId(block).toString();
			if (collection.isEmpty()) {
				sendMessage(playerEntity, new TranslatableText(this.getTranslationKey() + ".empty", string));
			} else {
				CompoundTag compoundTag = itemStack.getOrCreateSubTag("DebugProperty");
				String string2 = compoundTag.getString(string);
				Property<?> property = stateFactory.method_11663(string2);
				if (bl) {
					if (property == null) {
						property = (Property<?>)collection.iterator().next();
					}

					BlockState blockState2 = method_7758(blockState, property, playerEntity.isSneaking());
					iWorld.method_8652(blockPos, blockState2, 18);
					sendMessage(playerEntity, new TranslatableText(this.getTranslationKey() + ".update", property.getName(), method_7761(blockState2, property)));
				} else {
					property = cycle(collection, property, playerEntity.isSneaking());
					String string3 = property.getName();
					compoundTag.putString(string, string3);
					sendMessage(playerEntity, new TranslatableText(this.getTranslationKey() + ".select", string3, method_7761(blockState, property)));
				}
			}
		}
	}

	private static <T extends Comparable<T>> BlockState method_7758(BlockState blockState, Property<T> property, boolean bl) {
		return blockState.method_11657(property, cycle(property.getValues(), blockState.method_11654(property), bl));
	}

	private static <T> T cycle(Iterable<T> iterable, @Nullable T object, boolean bl) {
		return bl ? SystemUtil.previous(iterable, object) : SystemUtil.next(iterable, object);
	}

	private static void sendMessage(PlayerEntity playerEntity, Text text) {
		((ServerPlayerEntity)playerEntity).sendChatMessage(text, MessageType.field_11733);
	}

	private static <T extends Comparable<T>> String method_7761(BlockState blockState, Property<T> property) {
		return property.getName(blockState.method_11654(property));
	}
}
