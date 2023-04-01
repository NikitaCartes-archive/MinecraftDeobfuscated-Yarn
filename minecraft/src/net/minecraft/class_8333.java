package net.minecraft;

import com.mojang.serialization.Codec;
import java.util.Optional;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.random.Random;

public class class_8333 extends class_8277.class_8280 {
	private final Codec<class_8333.class_8334> field_43887 = ItemStack.CODEC.xmap(itemStack -> new class_8333.class_8334(itemStack), arg -> arg.field_43889);

	@Override
	public Codec<class_8291> method_50120() {
		return class_8289.method_50202(this.field_43887);
	}

	@Override
	public Optional<class_8291> method_50169(MinecraftServer minecraftServer, Random random) {
		Optional<RegistryEntry.Reference<Item>> optional = minecraftServer.getRegistryManager().get(RegistryKeys.ITEM).getRandom(random);
		return optional.map(reference -> {
			int i = random.nextBetween(1, ((Item)reference.value()).getMaxCount());
			return new class_8333.class_8334(new ItemStack(reference, i));
		});
	}

	protected class class_8334 extends class_8277.class_8278 {
		final ItemStack field_43889;
		private final Text field_43890;

		protected class_8334(ItemStack itemStack) {
			this.field_43889 = itemStack;
			this.field_43890 = Text.translatable("rule.give_items", itemStack.getCount(), itemStack.toHoverableText());
		}

		@Override
		protected Text method_50166() {
			return this.field_43890;
		}

		@Override
		public void method_50165(MinecraftServer minecraftServer) {
			for (ServerPlayerEntity serverPlayerEntity : minecraftServer.getPlayerManager().getPlayerList()) {
				ItemStack itemStack = this.field_43889.copy();
				if (serverPlayerEntity.giveItemStack(itemStack)) {
					serverPlayerEntity.world
						.playSound(
							null,
							serverPlayerEntity.getX(),
							serverPlayerEntity.getY(),
							serverPlayerEntity.getZ(),
							SoundEvents.ENTITY_ITEM_PICKUP,
							SoundCategory.PLAYERS,
							0.2F,
							((serverPlayerEntity.getRandom().nextFloat() - serverPlayerEntity.getRandom().nextFloat()) * 0.7F + 1.0F) * 2.0F
						);
				} else {
					ItemEntity itemEntity = serverPlayerEntity.dropItem(itemStack, false);
					if (itemEntity != null) {
						itemEntity.resetPickupDelay();
						itemEntity.setOwner(serverPlayerEntity.getUuid());
					}
				}

				serverPlayerEntity.currentScreenHandler.sendContentUpdates();
			}
		}
	}
}
