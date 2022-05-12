/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.datafixers.util.Pair;
import java.util.Optional;
import net.minecraft.command.argument.RegistryPredicateArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryEntryList;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.structure.StructureType;
import net.minecraft.world.poi.PointOfInterestStorage;
import net.minecraft.world.poi.PointOfInterestType;

public class LocateCommand {
    private static final DynamicCommandExceptionType STRUCTURE_NOT_FOUND_EXCEPTION = new DynamicCommandExceptionType(id -> Text.translatable("commands.locate.structure.not_found", id));
    private static final DynamicCommandExceptionType STRUCTURE_INVALID_EXCEPTION = new DynamicCommandExceptionType(id -> Text.translatable("commands.locate.structure.invalid", id));
    private static final DynamicCommandExceptionType BIOME_NOT_FOUND_EXCEPTION = new DynamicCommandExceptionType(id -> Text.translatable("commands.locate.biome.not_found", id));
    private static final DynamicCommandExceptionType BIOME_INVALID_EXCEPTION = new DynamicCommandExceptionType(id -> Text.translatable("commands.locate.biome.invalid", id));
    private static final DynamicCommandExceptionType POI_NOT_FOUND_EXCEPTION = new DynamicCommandExceptionType(id -> Text.translatable("commands.locate.poi.not_found", id));
    private static final DynamicCommandExceptionType POI_INVALID_EXCEPTION = new DynamicCommandExceptionType(id -> Text.translatable("commands.locate.poi.invalid", id));
    private static final int field_39251 = 100;
    private static final int field_39252 = 6400;
    private static final int field_39253 = 32;
    private static final int field_39254 = 64;
    private static final int field_39255 = 256;

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("locate").requires(source -> source.hasPermissionLevel(2))).then(CommandManager.literal("structure").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("structure", RegistryPredicateArgumentType.registryPredicate(Registry.STRUCTURE_KEY)).executes(commandContext -> LocateCommand.executeLocateStructure((ServerCommandSource)commandContext.getSource(), RegistryPredicateArgumentType.getPredicate(commandContext, "structure", Registry.STRUCTURE_KEY, STRUCTURE_INVALID_EXCEPTION)))))).then(CommandManager.literal("biome").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("biome", RegistryPredicateArgumentType.registryPredicate(Registry.BIOME_KEY)).executes(commandContext -> LocateCommand.executeLocateBiome((ServerCommandSource)commandContext.getSource(), RegistryPredicateArgumentType.getPredicate(commandContext, "biome", Registry.BIOME_KEY, BIOME_INVALID_EXCEPTION)))))).then(CommandManager.literal("poi").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("poi", RegistryPredicateArgumentType.registryPredicate(Registry.POINT_OF_INTEREST_TYPE_KEY)).executes(commandContext -> LocateCommand.executeLocatePoi((ServerCommandSource)commandContext.getSource(), RegistryPredicateArgumentType.getPredicate(commandContext, "poi", Registry.POINT_OF_INTEREST_TYPE_KEY, POI_INVALID_EXCEPTION))))));
    }

    private static Optional<? extends RegistryEntryList.ListBacked<StructureType>> getStructureListForPredicate(RegistryPredicateArgumentType.RegistryPredicate<StructureType> predicate, Registry<StructureType> structureTypeRegistry) {
        return predicate.getKey().map(key -> structureTypeRegistry.getEntry((RegistryKey<StructureType>)key).map(entry -> RegistryEntryList.of(entry)), structureTypeRegistry::getEntryList);
    }

    private static int executeLocateStructure(ServerCommandSource source, RegistryPredicateArgumentType.RegistryPredicate<StructureType> predicate) throws CommandSyntaxException {
        Registry<StructureType> registry = source.getWorld().getRegistryManager().get(Registry.STRUCTURE_KEY);
        RegistryEntryList registryEntryList = LocateCommand.getStructureListForPredicate(predicate, registry).orElseThrow(() -> STRUCTURE_INVALID_EXCEPTION.create(predicate.asString()));
        BlockPos blockPos = new BlockPos(source.getPosition());
        ServerWorld serverWorld = source.getWorld();
        Pair<BlockPos, RegistryEntry<StructureType>> pair = serverWorld.getChunkManager().getChunkGenerator().locateStructure(serverWorld, registryEntryList, blockPos, 100, false);
        if (pair == null) {
            throw STRUCTURE_NOT_FOUND_EXCEPTION.create(predicate.asString());
        }
        return LocateCommand.sendCoordinates(source, predicate, blockPos, pair, "commands.locate.structure.success", false);
    }

    private static int executeLocateBiome(ServerCommandSource source, RegistryPredicateArgumentType.RegistryPredicate<Biome> predicate) throws CommandSyntaxException {
        BlockPos blockPos = new BlockPos(source.getPosition());
        Pair<BlockPos, RegistryEntry<Biome>> pair = source.getWorld().locateBiome(predicate, blockPos, 6400, 32, 64);
        if (pair == null) {
            throw BIOME_NOT_FOUND_EXCEPTION.create(predicate.asString());
        }
        return LocateCommand.sendCoordinates(source, predicate, blockPos, pair, "commands.locate.biome.success", true);
    }

    private static int executeLocatePoi(ServerCommandSource source, RegistryPredicateArgumentType.RegistryPredicate<PointOfInterestType> predicate) throws CommandSyntaxException {
        BlockPos blockPos = new BlockPos(source.getPosition());
        ServerWorld serverWorld = source.getWorld();
        Optional<Pair<RegistryEntry<PointOfInterestType>, BlockPos>> optional = serverWorld.getPointOfInterestStorage().getNearestTypeAndPosition(predicate, blockPos, 256, PointOfInterestStorage.OccupationStatus.ANY);
        if (optional.isEmpty()) {
            throw POI_NOT_FOUND_EXCEPTION.create(predicate.asString());
        }
        return LocateCommand.sendCoordinates(source, predicate, blockPos, optional.get().swap(), "commands.locate.poi.success", false);
    }

    public static int sendCoordinates(ServerCommandSource source, RegistryPredicateArgumentType.RegistryPredicate<?> structureFeature, BlockPos currentPos, Pair<BlockPos, ? extends RegistryEntry<?>> structurePosAndEntry, String successMessage, boolean bl) {
        BlockPos blockPos = structurePosAndEntry.getFirst();
        String string = structureFeature.getKey().map(key -> key.getValue().toString(), key2 -> "#" + key2.id() + " (" + ((RegistryEntry)structurePosAndEntry.getSecond()).getKey().map(key -> key.getValue().toString()).orElse("[unregistered]") + ")");
        int i = bl ? MathHelper.floor(MathHelper.sqrt((float)currentPos.getSquaredDistance(blockPos))) : MathHelper.floor(LocateCommand.getDistance(currentPos.getX(), currentPos.getZ(), blockPos.getX(), blockPos.getZ()));
        String string2 = bl ? String.valueOf(blockPos.getY()) : "~";
        MutableText text = Texts.bracketed(Text.translatable("chat.coordinates", blockPos.getX(), string2, blockPos.getZ())).styled(style -> style.withColor(Formatting.GREEN).withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/tp @s " + blockPos.getX() + " " + string2 + " " + blockPos.getZ())).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.translatable("chat.coordinates.tooltip"))));
        source.sendFeedback(Text.translatable(successMessage, string, text, i), false);
        return i;
    }

    private static float getDistance(int x1, int y1, int x2, int y2) {
        int i = x2 - x1;
        int j = y2 - y1;
        return MathHelper.sqrt(i * i + j * j);
    }
}

