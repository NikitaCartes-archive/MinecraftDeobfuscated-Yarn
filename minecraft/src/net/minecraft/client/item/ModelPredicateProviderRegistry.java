package net.minecraft.client.item;

import com.google.common.collect.Maps;
import java.util.Map;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.LightBlock;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BlockStateComponent;
import net.minecraft.component.type.ChargedProjectilesComponent;
import net.minecraft.component.type.CustomModelDataComponent;
import net.minecraft.component.type.LodestoneTrackerComponent;
import net.minecraft.data.client.ItemModelGenerator;
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
import net.minecraft.item.trim.ArmorTrim;
import net.minecraft.util.Arm;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class ModelPredicateProviderRegistry {
	private static final Map<Identifier, ModelPredicateProvider> GLOBAL = Maps.<Identifier, ModelPredicateProvider>newHashMap();
	private static final Identifier DAMAGED_ID = Identifier.method_60656("damaged");
	private static final Identifier DAMAGE_ID = Identifier.method_60656("damage");
	private static final ClampedModelPredicateProvider DAMAGED_PROVIDER = (stack, world, entity, seed) -> stack.isDamaged() ? 1.0F : 0.0F;
	private static final ClampedModelPredicateProvider DAMAGE_PROVIDER = (stack, world, entity, seed) -> MathHelper.clamp(
			(float)stack.getDamage() / (float)stack.getMaxDamage(), 0.0F, 1.0F
		);
	private static final Map<Item, Map<Identifier, ModelPredicateProvider>> ITEM_SPECIFIC = Maps.<Item, Map<Identifier, ModelPredicateProvider>>newHashMap();

	private static ClampedModelPredicateProvider register(Identifier id, ClampedModelPredicateProvider provider) {
		GLOBAL.put(id, provider);
		return provider;
	}

	private static void registerCustomModelData(ModelPredicateProvider provider) {
		GLOBAL.put(Identifier.method_60656("custom_model_data"), provider);
	}

	private static void register(Item item, Identifier id, ClampedModelPredicateProvider provider) {
		((Map)ITEM_SPECIFIC.computeIfAbsent(item, key -> Maps.newHashMap())).put(id, provider);
	}

	@Nullable
	public static ModelPredicateProvider get(ItemStack stack, Identifier id) {
		if (stack.getMaxDamage() > 0) {
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
			Map<Identifier, ModelPredicateProvider> map = (Map<Identifier, ModelPredicateProvider>)ITEM_SPECIFIC.get(stack.getItem());
			return map == null ? null : (ModelPredicateProvider)map.get(id);
		}
	}

	static {
		register(Identifier.method_60656("lefthanded"), (stack, world, entity, seed) -> entity != null && entity.getMainArm() != Arm.RIGHT ? 1.0F : 0.0F);
		register(
			Identifier.method_60656("cooldown"),
			(stack, world, entity, seed) -> entity instanceof PlayerEntity
					? ((PlayerEntity)entity).getItemCooldownManager().getCooldownProgress(stack.getItem(), 0.0F)
					: 0.0F
		);
		ClampedModelPredicateProvider clampedModelPredicateProvider = (stack, world, entity, seed) -> {
			ArmorTrim armorTrim = stack.get(DataComponentTypes.TRIM);
			return armorTrim != null ? armorTrim.getMaterial().value().itemModelIndex() : Float.NEGATIVE_INFINITY;
		};
		register(ItemModelGenerator.TRIM_TYPE, clampedModelPredicateProvider);
		registerCustomModelData(
			(stack, world, entity, seed) -> (float)stack.getOrDefault(DataComponentTypes.CUSTOM_MODEL_DATA, CustomModelDataComponent.DEFAULT).value()
		);
		register(Items.BOW, Identifier.method_60656("pull"), (stack, world, entity, seed) -> {
			if (entity == null) {
				return 0.0F;
			} else {
				return entity.getActiveItem() != stack ? 0.0F : (float)(stack.getMaxUseTime(entity) - entity.getItemUseTimeLeft()) / 20.0F;
			}
		});
		register(
			Items.BRUSH,
			Identifier.method_60656("brushing"),
			(stack, world, entity, seed) -> entity != null && entity.getActiveItem() == stack ? (float)(entity.getItemUseTimeLeft() % 10) / 10.0F : 0.0F
		);
		register(
			Items.BOW,
			Identifier.method_60656("pulling"),
			(stack, world, entity, seed) -> entity != null && entity.isUsingItem() && entity.getActiveItem() == stack ? 1.0F : 0.0F
		);
		register(Items.BUNDLE, Identifier.method_60656("filled"), (stack, world, entity, seed) -> BundleItem.getAmountFilled(stack));
		register(Items.CLOCK, Identifier.method_60656("time"), new ClampedModelPredicateProvider() {
			private double time;
			private double step;
			private long lastTick;

			@Override
			public float unclampedCall(ItemStack itemStack, @Nullable ClientWorld clientWorld, @Nullable LivingEntity livingEntity, int i) {
				Entity entity = (Entity)(livingEntity != null ? livingEntity : itemStack.getHolder());
				if (entity == null) {
					return 0.0F;
				} else {
					if (clientWorld == null && entity.getWorld() instanceof ClientWorld) {
						clientWorld = (ClientWorld)entity.getWorld();
					}

					if (clientWorld == null) {
						return 0.0F;
					} else {
						double d;
						if (clientWorld.getDimension().natural()) {
							d = (double)clientWorld.getSkyAngle(1.0F);
						} else {
							d = Math.random();
						}

						d = this.getTime(clientWorld, d);
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
		register(Items.COMPASS, Identifier.method_60656("angle"), new CompassAnglePredicateProvider((world, stack, entity) -> {
			LodestoneTrackerComponent lodestoneTrackerComponent = stack.get(DataComponentTypes.LODESTONE_TRACKER);
			return lodestoneTrackerComponent != null ? (GlobalPos)lodestoneTrackerComponent.target().orElse(null) : CompassItem.createSpawnPos(world);
		}));
		register(
			Items.RECOVERY_COMPASS,
			Identifier.method_60656("angle"),
			new CompassAnglePredicateProvider(
				(world, stack, entity) -> entity instanceof PlayerEntity playerEntity ? (GlobalPos)playerEntity.getLastDeathPos().orElse(null) : null
			)
		);
		register(Items.CROSSBOW, Identifier.method_60656("pull"), (stack, world, entity, seed) -> {
			if (entity == null) {
				return 0.0F;
			} else {
				return CrossbowItem.isCharged(stack) ? 0.0F : (float)(stack.getMaxUseTime(entity) - entity.getItemUseTimeLeft()) / (float)CrossbowItem.getPullTime(entity);
			}
		});
		register(
			Items.CROSSBOW,
			Identifier.method_60656("pulling"),
			(stack, world, entity, seed) -> entity != null && entity.isUsingItem() && entity.getActiveItem() == stack && !CrossbowItem.isCharged(stack) ? 1.0F : 0.0F
		);
		register(Items.CROSSBOW, Identifier.method_60656("charged"), (stack, world, entity, seed) -> CrossbowItem.isCharged(stack) ? 1.0F : 0.0F);
		register(Items.CROSSBOW, Identifier.method_60656("firework"), (stack, world, entity, seed) -> {
			ChargedProjectilesComponent chargedProjectilesComponent = stack.get(DataComponentTypes.CHARGED_PROJECTILES);
			return chargedProjectilesComponent != null && chargedProjectilesComponent.contains(Items.FIREWORK_ROCKET) ? 1.0F : 0.0F;
		});
		register(Items.ELYTRA, Identifier.method_60656("broken"), (stack, world, entity, seed) -> ElytraItem.isUsable(stack) ? 0.0F : 1.0F);
		register(Items.FISHING_ROD, Identifier.method_60656("cast"), (stack, world, entity, seed) -> {
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
			Identifier.method_60656("blocking"),
			(stack, world, entity, seed) -> entity != null && entity.isUsingItem() && entity.getActiveItem() == stack ? 1.0F : 0.0F
		);
		register(
			Items.TRIDENT,
			Identifier.method_60656("throwing"),
			(stack, world, entity, seed) -> entity != null && entity.isUsingItem() && entity.getActiveItem() == stack ? 1.0F : 0.0F
		);
		register(Items.LIGHT, Identifier.method_60656("level"), (stack, world, entity, seed) -> {
			BlockStateComponent blockStateComponent = stack.getOrDefault(DataComponentTypes.BLOCK_STATE, BlockStateComponent.DEFAULT);
			Integer integer = blockStateComponent.getValue(LightBlock.LEVEL_15);
			return integer != null ? (float)integer.intValue() / 16.0F : 1.0F;
		});
		register(
			Items.GOAT_HORN,
			Identifier.method_60656("tooting"),
			(stack, world, entity, seed) -> entity != null && entity.isUsingItem() && entity.getActiveItem() == stack ? 1.0F : 0.0F
		);
	}
}
