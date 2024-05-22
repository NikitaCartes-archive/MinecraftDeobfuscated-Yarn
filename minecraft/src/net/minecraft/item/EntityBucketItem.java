package net.minecraft.item;

import com.mojang.serialization.MapCodec;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.Bucketable;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.passive.TropicalFishEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.event.GameEvent;

public class EntityBucketItem extends BucketItem {
	private static final MapCodec<TropicalFishEntity.Variant> TROPICAL_FISH_VARIANT_MAP_CODEC = TropicalFishEntity.Variant.CODEC.fieldOf("BucketVariantTag");
	private final EntityType<?> entityType;
	private final SoundEvent emptyingSound;

	public EntityBucketItem(EntityType<?> type, Fluid fluid, SoundEvent emptyingSound, Item.Settings settings) {
		super(fluid, settings);
		this.entityType = type;
		this.emptyingSound = emptyingSound;
	}

	@Override
	public void onEmptied(@Nullable PlayerEntity player, World world, ItemStack stack, BlockPos pos) {
		if (world instanceof ServerWorld) {
			this.spawnEntity((ServerWorld)world, stack, pos);
			world.emitGameEvent(player, GameEvent.ENTITY_PLACE, pos);
		}
	}

	@Override
	protected void playEmptyingSound(@Nullable PlayerEntity player, WorldAccess world, BlockPos pos) {
		world.playSound(player, pos, this.emptyingSound, SoundCategory.NEUTRAL, 1.0F, 1.0F);
	}

	private void spawnEntity(ServerWorld world, ItemStack stack, BlockPos pos) {
		if (this.entityType.spawnFromItemStack(world, stack, null, pos, SpawnReason.BUCKET, true, false) instanceof Bucketable bucketable) {
			NbtComponent nbtComponent = stack.getOrDefault(DataComponentTypes.BUCKET_ENTITY_DATA, NbtComponent.DEFAULT);
			bucketable.copyDataFromNbt(nbtComponent.copyNbt());
			bucketable.setFromBucket(true);
		}
	}

	@Override
	public void appendTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType type) {
		if (this.entityType == EntityType.TROPICAL_FISH) {
			NbtComponent nbtComponent = stack.getOrDefault(DataComponentTypes.BUCKET_ENTITY_DATA, NbtComponent.DEFAULT);
			if (nbtComponent.isEmpty()) {
				return;
			}

			Optional<TropicalFishEntity.Variant> optional = nbtComponent.get(TROPICAL_FISH_VARIANT_MAP_CODEC).result();
			if (optional.isPresent()) {
				TropicalFishEntity.Variant variant = (TropicalFishEntity.Variant)optional.get();
				Formatting[] formattings = new Formatting[]{Formatting.ITALIC, Formatting.GRAY};
				String string = "color.minecraft." + variant.baseColor();
				String string2 = "color.minecraft." + variant.patternColor();
				int i = TropicalFishEntity.COMMON_VARIANTS.indexOf(variant);
				if (i != -1) {
					tooltip.add(Text.translatable(TropicalFishEntity.getToolTipForVariant(i)).formatted(formattings));
					return;
				}

				tooltip.add(variant.variety().getText().copyContentOnly().formatted(formattings));
				MutableText mutableText = Text.translatable(string);
				if (!string.equals(string2)) {
					mutableText.append(", ").append(Text.translatable(string2));
				}

				mutableText.formatted(formattings);
				tooltip.add(mutableText);
			}
		}
	}
}
