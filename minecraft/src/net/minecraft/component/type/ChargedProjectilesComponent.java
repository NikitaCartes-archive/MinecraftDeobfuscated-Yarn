package net.minecraft.component.type;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import java.util.List;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

public final class ChargedProjectilesComponent {
	public static final ChargedProjectilesComponent DEFAULT = new ChargedProjectilesComponent(List.of());
	public static final Codec<ChargedProjectilesComponent> CODEC = ItemStack.CODEC
		.listOf()
		.xmap(ChargedProjectilesComponent::new, chargedProjectilesComponent -> chargedProjectilesComponent.projectiles);
	public static final PacketCodec<RegistryByteBuf, ChargedProjectilesComponent> PACKET_CODEC = ItemStack.PACKET_CODEC
		.collect(PacketCodecs.toList())
		.xmap(ChargedProjectilesComponent::new, chargedProjectilesComponent -> chargedProjectilesComponent.projectiles);
	private final List<ItemStack> projectiles;

	private ChargedProjectilesComponent(List<ItemStack> projectiles) {
		this.projectiles = projectiles;
	}

	public static ChargedProjectilesComponent of(ItemStack projectile) {
		return new ChargedProjectilesComponent(List.of(projectile.copy()));
	}

	public static ChargedProjectilesComponent of(List<ItemStack> projectiles) {
		return new ChargedProjectilesComponent(List.copyOf(Lists.<ItemStack, ItemStack>transform(projectiles, ItemStack::copy)));
	}

	public boolean contains(Item item) {
		for (ItemStack itemStack : this.projectiles) {
			if (itemStack.isOf(item)) {
				return true;
			}
		}

		return false;
	}

	public List<ItemStack> getProjectiles() {
		return Lists.transform(this.projectiles, ItemStack::copy);
	}

	public boolean isEmpty() {
		return this.projectiles.isEmpty();
	}

	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else {
			if (o instanceof ChargedProjectilesComponent chargedProjectilesComponent && ItemStack.stacksEqual(this.projectiles, chargedProjectilesComponent.projectiles)
				)
			 {
				return true;
			}

			return false;
		}
	}

	public int hashCode() {
		return ItemStack.listHashCode(this.projectiles);
	}

	public String toString() {
		return "ChargedProjectiles[items=" + this.projectiles + "]";
	}
}
