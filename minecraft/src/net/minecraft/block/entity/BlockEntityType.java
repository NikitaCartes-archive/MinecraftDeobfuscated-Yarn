package net.minecraft.block.entity;

import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.types.Type;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.datafixers.Schemas;
import net.minecraft.datafixers.TypeReferences;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BlockEntityType<T extends BlockEntity> {
	private static final Logger LOGGER = LogManager.getLogger();
	public static final BlockEntityType<FurnaceBlockEntity> FURNACE = create("furnace", BlockEntityType.Builder.create(FurnaceBlockEntity::new));
	public static final BlockEntityType<ChestBlockEntity> CHEST = create("chest", BlockEntityType.Builder.create(ChestBlockEntity::new));
	public static final BlockEntityType<TrappedChestBlockEntity> TRAPPED_CHEST = create(
		"trapped_chest", BlockEntityType.Builder.create(TrappedChestBlockEntity::new)
	);
	public static final BlockEntityType<EnderChestBlockEntity> ENDER_CHEST = create("ender_chest", BlockEntityType.Builder.create(EnderChestBlockEntity::new));
	public static final BlockEntityType<JukeboxBlockEntity> JUKEBOX = create("jukebox", BlockEntityType.Builder.create(JukeboxBlockEntity::new));
	public static final BlockEntityType<DispenserBlockEntity> DISPENSER = create("dispenser", BlockEntityType.Builder.create(DispenserBlockEntity::new));
	public static final BlockEntityType<DropperBlockEntity> DROPPER = create("dropper", BlockEntityType.Builder.create(DropperBlockEntity::new));
	public static final BlockEntityType<SignBlockEntity> SIGN = create("sign", BlockEntityType.Builder.create(SignBlockEntity::new));
	public static final BlockEntityType<MobSpawnerBlockEntity> MOB_SPAWNER = create("mob_spawner", BlockEntityType.Builder.create(MobSpawnerBlockEntity::new));
	public static final BlockEntityType<PistonBlockEntity> PISTON = create("piston", BlockEntityType.Builder.create(PistonBlockEntity::new));
	public static final BlockEntityType<BrewingStandBlockEntity> BREWING_STAND = create(
		"brewing_stand", BlockEntityType.Builder.create(BrewingStandBlockEntity::new)
	);
	public static final BlockEntityType<EnchantingTableBlockEntity> ENCHANTING_TABLE = create(
		"enchanting_table", BlockEntityType.Builder.create(EnchantingTableBlockEntity::new)
	);
	public static final BlockEntityType<EndPortalBlockEntity> END_PORTAL = create("end_portal", BlockEntityType.Builder.create(EndPortalBlockEntity::new));
	public static final BlockEntityType<BeaconBlockEntity> BEACON = create("beacon", BlockEntityType.Builder.create(BeaconBlockEntity::new));
	public static final BlockEntityType<SkullBlockEntity> SKULL = create("skull", BlockEntityType.Builder.create(SkullBlockEntity::new));
	public static final BlockEntityType<DaylightDetectorBlockEntity> DAYLIGHT_DETECTOR = create(
		"daylight_detector", BlockEntityType.Builder.create(DaylightDetectorBlockEntity::new)
	);
	public static final BlockEntityType<HopperBlockEntity> HOPPER = create("hopper", BlockEntityType.Builder.create(HopperBlockEntity::new));
	public static final BlockEntityType<ComparatorBlockEntity> COMPARATOR = create("comparator", BlockEntityType.Builder.create(ComparatorBlockEntity::new));
	public static final BlockEntityType<BannerBlockEntity> BANNER = create("banner", BlockEntityType.Builder.create(BannerBlockEntity::new));
	public static final BlockEntityType<StructureBlockBlockEntity> STRUCTURE_BLOCK = create(
		"structure_block", BlockEntityType.Builder.create(StructureBlockBlockEntity::new)
	);
	public static final BlockEntityType<EndGatewayBlockEntity> END_GATEWAY = create("end_gateway", BlockEntityType.Builder.create(EndGatewayBlockEntity::new));
	public static final BlockEntityType<CommandBlockBlockEntity> COMMAND_BLOCK = create(
		"command_block", BlockEntityType.Builder.create(CommandBlockBlockEntity::new)
	);
	public static final BlockEntityType<ShulkerBoxBlockEntity> SHUlKER_BOX = create("shulker_box", BlockEntityType.Builder.create(ShulkerBoxBlockEntity::new));
	public static final BlockEntityType<BedBlockEntity> BED = create("bed", BlockEntityType.Builder.create(BedBlockEntity::new));
	public static final BlockEntityType<ConduitBlockEntity> CONDUIT = create("conduit", BlockEntityType.Builder.create(ConduitBlockEntity::new));
	public static final BlockEntityType<BarrelBlockEntity> BARREL = create("barrel", BlockEntityType.Builder.create(BarrelBlockEntity::new));
	public static final BlockEntityType<SmokerBlockEntity> SMOKER = create("smoker", BlockEntityType.Builder.create(SmokerBlockEntity::new));
	public static final BlockEntityType<BlastFurnaceBlockEntity> BLAST_FURNACE = create(
		"blast_furnace", BlockEntityType.Builder.create(BlastFurnaceBlockEntity::new)
	);
	public static final BlockEntityType<LecternBlockEntity> LECTERN = create("lectern", BlockEntityType.Builder.create(LecternBlockEntity::new));
	public static final BlockEntityType<BellBlockEntity> BELL = create("bell", BlockEntityType.Builder.create(BellBlockEntity::new));
	public static final BlockEntityType<JigsawBlockEntity> JIGSAW = create("jigsaw", BlockEntityType.Builder.create(JigsawBlockEntity::new));
	private final Supplier<? extends T> supplier;
	private final Type<?> type;

	@Nullable
	public static Identifier getId(BlockEntityType<?> blockEntityType) {
		return Registry.BLOCK_ENTITY.getId(blockEntityType);
	}

	private static <T extends BlockEntity> BlockEntityType<T> create(String string, BlockEntityType.Builder<T> builder) {
		Type<?> type = null;

		try {
			type = Schemas.getFixer()
				.getSchema(DataFixUtils.makeKey(SharedConstants.getGameVersion().getWorldVersion()))
				.getChoiceType(TypeReferences.BLOCK_ENTITY, string);
		} catch (IllegalStateException var4) {
			if (SharedConstants.isDevelopment) {
				throw var4;
			}

			LOGGER.warn("No data fixer registered for block entity {}", string);
		}

		return Registry.register(Registry.BLOCK_ENTITY, string, builder.method_11034(type));
	}

	public BlockEntityType(Supplier<? extends T> supplier, Type<?> type) {
		this.supplier = supplier;
		this.type = type;
	}

	@Nullable
	public T instantiate() {
		return (T)this.supplier.get();
	}

	@Nullable
	static BlockEntity instantiate(String string) {
		BlockEntityType<?> blockEntityType = Registry.BLOCK_ENTITY.get(new Identifier(string));
		return blockEntityType == null ? null : blockEntityType.instantiate();
	}

	public static final class Builder<T extends BlockEntity> {
		private final Supplier<? extends T> supplier;

		private Builder(Supplier<? extends T> supplier) {
			this.supplier = supplier;
		}

		public static <T extends BlockEntity> BlockEntityType.Builder<T> create(Supplier<? extends T> supplier) {
			return new BlockEntityType.Builder<>(supplier);
		}

		public BlockEntityType<T> method_11034(Type<?> type) {
			return new BlockEntityType<>(this.supplier, type);
		}
	}
}
