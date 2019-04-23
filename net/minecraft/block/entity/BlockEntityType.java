/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block.entity;

import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.types.Type;
import java.util.function.Supplier;
import net.minecraft.SharedConstants;
import net.minecraft.block.entity.BannerBlockEntity;
import net.minecraft.block.entity.BarrelBlockEntity;
import net.minecraft.block.entity.BeaconBlockEntity;
import net.minecraft.block.entity.BedBlockEntity;
import net.minecraft.block.entity.BellBlockEntity;
import net.minecraft.block.entity.BlastFurnaceBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BrewingStandBlockEntity;
import net.minecraft.block.entity.CampfireBlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.CommandBlockBlockEntity;
import net.minecraft.block.entity.ComparatorBlockEntity;
import net.minecraft.block.entity.ConduitBlockEntity;
import net.minecraft.block.entity.DaylightDetectorBlockEntity;
import net.minecraft.block.entity.DispenserBlockEntity;
import net.minecraft.block.entity.DropperBlockEntity;
import net.minecraft.block.entity.EnchantingTableBlockEntity;
import net.minecraft.block.entity.EndGatewayBlockEntity;
import net.minecraft.block.entity.EndPortalBlockEntity;
import net.minecraft.block.entity.EnderChestBlockEntity;
import net.minecraft.block.entity.FurnaceBlockEntity;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.block.entity.JigsawBlockEntity;
import net.minecraft.block.entity.JukeboxBlockEntity;
import net.minecraft.block.entity.LecternBlockEntity;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.block.entity.PistonBlockEntity;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.block.entity.SmokerBlockEntity;
import net.minecraft.block.entity.StructureBlockBlockEntity;
import net.minecraft.block.entity.TrappedChestBlockEntity;
import net.minecraft.datafixers.Schemas;
import net.minecraft.datafixers.TypeReferences;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public class BlockEntityType<T extends BlockEntity> {
    private static final Logger LOGGER = LogManager.getLogger();
    public static final BlockEntityType<FurnaceBlockEntity> FURNACE = BlockEntityType.create("furnace", Builder.create(FurnaceBlockEntity::new));
    public static final BlockEntityType<ChestBlockEntity> CHEST = BlockEntityType.create("chest", Builder.create(ChestBlockEntity::new));
    public static final BlockEntityType<TrappedChestBlockEntity> TRAPPED_CHEST = BlockEntityType.create("trapped_chest", Builder.create(TrappedChestBlockEntity::new));
    public static final BlockEntityType<EnderChestBlockEntity> ENDER_CHEST = BlockEntityType.create("ender_chest", Builder.create(EnderChestBlockEntity::new));
    public static final BlockEntityType<JukeboxBlockEntity> JUKEBOX = BlockEntityType.create("jukebox", Builder.create(JukeboxBlockEntity::new));
    public static final BlockEntityType<DispenserBlockEntity> DISPENSER = BlockEntityType.create("dispenser", Builder.create(DispenserBlockEntity::new));
    public static final BlockEntityType<DropperBlockEntity> DROPPER = BlockEntityType.create("dropper", Builder.create(DropperBlockEntity::new));
    public static final BlockEntityType<SignBlockEntity> SIGN = BlockEntityType.create("sign", Builder.create(SignBlockEntity::new));
    public static final BlockEntityType<MobSpawnerBlockEntity> MOB_SPAWNER = BlockEntityType.create("mob_spawner", Builder.create(MobSpawnerBlockEntity::new));
    public static final BlockEntityType<PistonBlockEntity> PISTON = BlockEntityType.create("piston", Builder.create(PistonBlockEntity::new));
    public static final BlockEntityType<BrewingStandBlockEntity> BREWING_STAND = BlockEntityType.create("brewing_stand", Builder.create(BrewingStandBlockEntity::new));
    public static final BlockEntityType<EnchantingTableBlockEntity> ENCHANTING_TABLE = BlockEntityType.create("enchanting_table", Builder.create(EnchantingTableBlockEntity::new));
    public static final BlockEntityType<EndPortalBlockEntity> END_PORTAL = BlockEntityType.create("end_portal", Builder.create(EndPortalBlockEntity::new));
    public static final BlockEntityType<BeaconBlockEntity> BEACON = BlockEntityType.create("beacon", Builder.create(BeaconBlockEntity::new));
    public static final BlockEntityType<SkullBlockEntity> SKULL = BlockEntityType.create("skull", Builder.create(SkullBlockEntity::new));
    public static final BlockEntityType<DaylightDetectorBlockEntity> DAYLIGHT_DETECTOR = BlockEntityType.create("daylight_detector", Builder.create(DaylightDetectorBlockEntity::new));
    public static final BlockEntityType<HopperBlockEntity> HOPPER = BlockEntityType.create("hopper", Builder.create(HopperBlockEntity::new));
    public static final BlockEntityType<ComparatorBlockEntity> COMPARATOR = BlockEntityType.create("comparator", Builder.create(ComparatorBlockEntity::new));
    public static final BlockEntityType<BannerBlockEntity> BANNER = BlockEntityType.create("banner", Builder.create(BannerBlockEntity::new));
    public static final BlockEntityType<StructureBlockBlockEntity> STRUCTURE_BLOCK = BlockEntityType.create("structure_block", Builder.create(StructureBlockBlockEntity::new));
    public static final BlockEntityType<EndGatewayBlockEntity> END_GATEWAY = BlockEntityType.create("end_gateway", Builder.create(EndGatewayBlockEntity::new));
    public static final BlockEntityType<CommandBlockBlockEntity> COMMAND_BLOCK = BlockEntityType.create("command_block", Builder.create(CommandBlockBlockEntity::new));
    public static final BlockEntityType<ShulkerBoxBlockEntity> SHULKER_BOX = BlockEntityType.create("shulker_box", Builder.create(ShulkerBoxBlockEntity::new));
    public static final BlockEntityType<BedBlockEntity> BED = BlockEntityType.create("bed", Builder.create(BedBlockEntity::new));
    public static final BlockEntityType<ConduitBlockEntity> CONDUIT = BlockEntityType.create("conduit", Builder.create(ConduitBlockEntity::new));
    public static final BlockEntityType<BarrelBlockEntity> BARREL = BlockEntityType.create("barrel", Builder.create(BarrelBlockEntity::new));
    public static final BlockEntityType<SmokerBlockEntity> SMOKER = BlockEntityType.create("smoker", Builder.create(SmokerBlockEntity::new));
    public static final BlockEntityType<BlastFurnaceBlockEntity> BLAST_FURNACE = BlockEntityType.create("blast_furnace", Builder.create(BlastFurnaceBlockEntity::new));
    public static final BlockEntityType<LecternBlockEntity> LECTERN = BlockEntityType.create("lectern", Builder.create(LecternBlockEntity::new));
    public static final BlockEntityType<BellBlockEntity> BELL = BlockEntityType.create("bell", Builder.create(BellBlockEntity::new));
    public static final BlockEntityType<JigsawBlockEntity> JIGSAW = BlockEntityType.create("jigsaw", Builder.create(JigsawBlockEntity::new));
    public static final BlockEntityType<CampfireBlockEntity> CAMPFIRE = BlockEntityType.create("campfire", Builder.create(CampfireBlockEntity::new));
    private final Supplier<? extends T> supplier;
    private final Type<?> type;

    @Nullable
    public static Identifier getId(BlockEntityType<?> blockEntityType) {
        return Registry.BLOCK_ENTITY.getId(blockEntityType);
    }

    private static <T extends BlockEntity> BlockEntityType<T> create(String string, Builder<T> builder) {
        Type<?> type = null;
        try {
            type = Schemas.getFixer().getSchema(DataFixUtils.makeKey(SharedConstants.getGameVersion().getWorldVersion())).getChoiceType(TypeReferences.BLOCK_ENTITY, string);
        } catch (IllegalStateException illegalStateException) {
            if (SharedConstants.isDevelopment) {
                throw illegalStateException;
            }
            LOGGER.warn("No data fixer registered for block entity {}", (Object)string);
        }
        return Registry.register(Registry.BLOCK_ENTITY, string, builder.build(type));
    }

    public BlockEntityType(Supplier<? extends T> supplier, Type<?> type) {
        this.supplier = supplier;
        this.type = type;
    }

    @Nullable
    public T instantiate() {
        return (T)((BlockEntity)this.supplier.get());
    }

    public static final class Builder<T extends BlockEntity> {
        private final Supplier<? extends T> supplier;

        private Builder(Supplier<? extends T> supplier) {
            this.supplier = supplier;
        }

        public static <T extends BlockEntity> Builder<T> create(Supplier<? extends T> supplier) {
            return new Builder<T>(supplier);
        }

        public BlockEntityType<T> build(Type<?> type) {
            return new BlockEntityType<T>(this.supplier, type);
        }
    }
}

