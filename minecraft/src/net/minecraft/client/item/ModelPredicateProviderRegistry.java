package net.minecraft.client.item;

import com.google.common.collect.Maps;
import java.util.Map;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.LightBlock;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BundleItem;
import net.minecraft.item.CompassItem;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.FishingRodItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.Arm;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class ModelPredicateProviderRegistry {
	private static final Map<Identifier, ModelPredicateProvider> GLOBAL = Maps.<Identifier, ModelPredicateProvider>newHashMap();
	private static final String CUSTOM_MODEL_DATA_KEY = "CustomModelData";
	private static final Identifier DAMAGED_ID = new Identifier("damaged");
	private static final Identifier DAMAGE_ID = new Identifier("damage");
	private static final UnclampedModelPredicateProvider DAMAGED_PROVIDER = (stack, world, entity, seed) -> stack.isDamaged() ? 1.0F : 0.0F;
	private static final UnclampedModelPredicateProvider DAMAGE_PROVIDER = (stack, world, entity, seed) -> MathHelper.clamp(
			(float)stack.getDamage() / (float)stack.getMaxDamage(), 0.0F, 1.0F
		);
	private static final Map<Item, Map<Identifier, ModelPredicateProvider>> ITEM_SPECIFIC = Maps.<Item, Map<Identifier, ModelPredicateProvider>>newHashMap();

	private static UnclampedModelPredicateProvider register(Identifier id, UnclampedModelPredicateProvider provider) {
		GLOBAL.put(id, provider);
		return provider;
	}

	private static void registerCustomModelData(ModelPredicateProvider provider) {
		GLOBAL.put(new Identifier("custom_model_data"), provider);
	}

	private static void register(Item item, Identifier id, UnclampedModelPredicateProvider provider) {
		((Map)ITEM_SPECIFIC.computeIfAbsent(item, key -> Maps.newHashMap())).put(id, provider);
	}

	@Nullable
	public static ModelPredicateProvider get(Item item, Identifier id) {
		if (item.getMaxDamage() > 0) {
			if (DAMAGE_ID.equals(id)) {
				return DAMAGE_PROVIDER;
			}

			if (DAMAGED_ID.equals(id)) {
				return DAMAGED_PROVIDER;
			}
		}

		ModelPredicateProvider modelPredicateProvider = (ModelPredicateProvider)GLOBAL.get(id);
		if (modelPredicateProvider != null) {
			return modelPredicateProvider;
		} else {
			Map<Identifier, ModelPredicateProvider> map = (Map<Identifier, ModelPredicateProvider>)ITEM_SPECIFIC.get(item);
			return map == null ? null : (ModelPredicateProvider)map.get(id);
		}
	}

	static {
		register(new Identifier("lefthanded"), (stack, world, entity, seed) -> entity != null && entity.getMainArm() != Arm.RIGHT ? 1.0F : 0.0F);
		register(
			new Identifier("cooldown"),
			(stack, world, entity, seed) -> entity instanceof PlayerEntity
					? ((PlayerEntity)entity).getItemCooldownManager().getCooldownProgress(stack.getItem(), 0.0F)
					: 0.0F
		);
		registerCustomModelData((stack, world, entity, seed) -> stack.hasNbt() ? (float)stack.getNbt().getInt("CustomModelData") : 0.0F);
		register(Items.BOW, new Identifier("pull"), (stack, world, entity, seed) -> {
			if (entity == null) {
				return 0.0F;
			} else {
				return entity.getActiveItem() != stack ? 0.0F : (float)(stack.getMaxUseTime() - entity.getItemUseTimeLeft()) / 20.0F;
			}
		});
		register(
			Items.BOW,
			new Identifier("pulling"),
			(stack, world, entity, seed) -> entity != null && entity.isUsingItem() && entity.getActiveItem() == stack ? 1.0F : 0.0F
		);
		register(Items.BUNDLE, new Identifier("filled"), (stack, world, entity, seed) -> BundleItem.getAmountFilled(stack));
		register(Items.CLOCK, new Identifier("time"), new UnclampedModelPredicateProvider() {
			private double time;
			private double step;
			private long lastTick;

			@Override
			public float unclampedCall(ItemStack itemStack, @Nullable ClientWorld clientWorldx, @Nullable LivingEntity livingEntity, int i) {
				Entity entity = (Entity)(livingEntity != null ? livingEntity : itemStack.getHolder());
				if (entity == null) {
					return 0.0F;
				} else {
					if (clientWorldx == null && entity.world instanceof ClientWorld clientWorldx) {
						;
					}

					if (clientWorldx == null) {
						return 0.0F;
					} else {
						double d;
						if (clientWorldx.getDimension().natural()) {
							d = (double)clientWorldx.getSkyAngle(1.0F);
						} else {
							d = Math.random();
						}

						d = this.getTime(clientWorldx, d);
						return (float)d;
					}
				}
			}

			private double getTime(World world, double skyAngle) {
				if (world.getTime() != this.lastTick) {
					this.lastTick = world.getTime();
					double d = skyAngle - this.time;
					d = MathHelper.floorMod(d + 0.5, 1.0) - 0.5;
					this.step += d * 0.1;
					this.step *= 0.9;
					this.time = MathHelper.floorMod(this.time + this.step, 1.0);
				}

				return this.time;
			}
		});
		register(
			Items.COMPASS,
			new Identifier("angle"),
			new CompassAnglePredicateProvider(
				(world, stack, entity) -> CompassItem.hasLodestone(stack) ? CompassItem.createLodestonePos(stack.getOrCreateNbt()) : CompassItem.createSpawnPos(world)
			)
		);
		register(
			Items.RECOVERY_COMPASS,
			new Identifier("angle"),
			new CompassAnglePredicateProvider(
				(world, stack, entity) -> entity instanceof PlayerEntity playerEntity ? (GlobalPos)playerEntity.getLastDeathPos().orElse(null) : null
			)
		);
		register(Items.CROSSBOW, new Identifier("pull"), (stack, world, entity, seed) -> {
			if (entity == null) {
				return 0.0F;
			} else {
				return CrossbowItem.isCharged(stack) ? 0.0F : (float)(stack.getMaxUseTime() - entity.getItemUseTimeLeft()) / (float)CrossbowItem.getPullTime(stack);
			}
		});
		register(
			Items.CROSSBOW,
			new Identifier("pulling"),
			(stack, world, entity, seed) -> entity != null && entity.isUsingItem() && entity.getActiveItem() == stack && !CrossbowItem.isCharged(stack) ? 1.0F : 0.0F
		);
		register(Items.CROSSBOW, new Identifier("charged"), (stack, world, entity, seed) -> entity != null && CrossbowItem.isCharged(stack) ? 1.0F : 0.0F);
		register(
			Items.CROSSBOW,
			new Identifier("firework"),
			(stack, world, entity, seed) -> entity != null && CrossbowItem.isCharged(stack) && CrossbowItem.hasProjectile(stack, Items.FIREWORK_ROCKET) ? 1.0F : 0.0F
		);
		register(Items.ELYTRA, new Identifier("broken"), (stack, world, entity, seed) -> ElytraItem.isUsable(stack) ? 0.0F : 1.0F);
		register(Items.FISHING_ROD, new Identifier("cast"), (stack, world, entity, seed) -> {
			if (entity == null) {
				return 0.0F;
			} else {
				boolean bl = entity.getMainHandStack() == stack;
				boolean bl2 = entity.getOffHandStack() == stack;
				if (entity.getMainHandStack().getItem() instanceof FishingRodItem) {
					bl2 = false;
				}

				return (bl || bl2) && entity instanceof PlayerEntity && ((PlayerEntity)entity).fishHook != null ? 1.0F : 0.0F;
			}
		});
		register(
			Items.SHIELD,
			new Identifier("blocking"),
			(stack, world, entity, seed) -> entity != null && entity.isUsingItem() && entity.getActiveItem() == stack ? 1.0F : 0.0F
		);
		register(
			Items.TRIDENT,
			new Identifier("throwing"),
			(stack, world, entity, seed) -> entity != null && entity.isUsingItem() && entity.getActiveItem() == stack ? 1.0F : 0.0F
		);
		register(Items.LIGHT, new Identifier("level"), (stack, world, entity, seed) -> {
			NbtCompound nbtCompound = stack.getSubNbt("BlockStateTag");

			try {
				if (nbtCompound != null) {
					NbtElement nbtElement = nbtCompound.get(LightBlock.LEVEL_15.getName());
					if (nbtElement != null) {
						return (float)Integer.parseInt(nbtElement.asString()) / 16.0F;
					}
				}
			} catch (NumberFormatException var6) {
			}

			return 1.0F;
		});
	}
}
