/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.server.command;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic4CommandExceptionType;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.Vec2ArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.world.BlockView;

public class SpreadPlayersCommand {
    private static final Dynamic4CommandExceptionType FAILED_TEAMS_EXCEPTION = new Dynamic4CommandExceptionType((object, object2, object3, object4) -> new TranslatableText("commands.spreadplayers.failed.teams", object, object2, object3, object4));
    private static final Dynamic4CommandExceptionType FAILED_ENTITIES_EXCEPTION = new Dynamic4CommandExceptionType((object, object2, object3, object4) -> new TranslatableText("commands.spreadplayers.failed.entities", object, object2, object3, object4));

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("spreadplayers").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))).then(CommandManager.argument("center", Vec2ArgumentType.vec2()).then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("spreadDistance", FloatArgumentType.floatArg(0.0f)).then((ArgumentBuilder<ServerCommandSource, ?>)((RequiredArgumentBuilder)CommandManager.argument("maxRange", FloatArgumentType.floatArg(1.0f)).then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("respectTeams", BoolArgumentType.bool()).then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("targets", EntityArgumentType.entities()).executes(commandContext -> SpreadPlayersCommand.execute((ServerCommandSource)commandContext.getSource(), Vec2ArgumentType.getVec2(commandContext, "center"), FloatArgumentType.getFloat(commandContext, "spreadDistance"), FloatArgumentType.getFloat(commandContext, "maxRange"), ((ServerCommandSource)commandContext.getSource()).getWorld().getTopY(), BoolArgumentType.getBool(commandContext, "respectTeams"), EntityArgumentType.getEntities(commandContext, "targets")))))).then(CommandManager.literal("under").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("maxHeight", IntegerArgumentType.integer(0)).then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("respectTeams", BoolArgumentType.bool()).then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("targets", EntityArgumentType.entities()).executes(commandContext -> SpreadPlayersCommand.execute((ServerCommandSource)commandContext.getSource(), Vec2ArgumentType.getVec2(commandContext, "center"), FloatArgumentType.getFloat(commandContext, "spreadDistance"), FloatArgumentType.getFloat(commandContext, "maxRange"), IntegerArgumentType.getInteger(commandContext, "maxHeight"), BoolArgumentType.getBool(commandContext, "respectTeams"), EntityArgumentType.getEntities(commandContext, "targets")))))))))));
    }

    private static int execute(ServerCommandSource source, Vec2f center, float spreadDistance, float maxRange, int i, boolean bl, Collection<? extends Entity> collection) throws CommandSyntaxException {
        Random random = new Random();
        double d = center.x - maxRange;
        double e = center.y - maxRange;
        double f = center.x + maxRange;
        double g = center.y + maxRange;
        Pile[] piles = SpreadPlayersCommand.makePiles(random, bl ? SpreadPlayersCommand.getPileCountRespectingTeams(collection) : collection.size(), d, e, f, g);
        SpreadPlayersCommand.spread(center, spreadDistance, source.getWorld(), random, d, e, f, g, i, piles, bl);
        double h = SpreadPlayersCommand.getMinDistance(collection, source.getWorld(), piles, i, bl);
        source.sendFeedback(new TranslatableText("commands.spreadplayers.success." + (bl ? "teams" : "entities"), piles.length, Float.valueOf(center.x), Float.valueOf(center.y), String.format(Locale.ROOT, "%.2f", h)), true);
        return piles.length;
    }

    private static int getPileCountRespectingTeams(Collection<? extends Entity> entities) {
        HashSet<AbstractTeam> set = Sets.newHashSet();
        for (Entity entity : entities) {
            if (entity instanceof PlayerEntity) {
                set.add(entity.getScoreboardTeam());
                continue;
            }
            set.add(null);
        }
        return set.size();
    }

    private static void spread(Vec2f center, double spreadDistance, ServerWorld world, Random random, double minX, double minZ, double maxX, double maxZ, int i, Pile[] piles, boolean bl) throws CommandSyntaxException {
        int j;
        boolean bl2 = true;
        double d = 3.4028234663852886E38;
        for (j = 0; j < 10000 && bl2; ++j) {
            bl2 = false;
            d = 3.4028234663852886E38;
            for (int k = 0; k < piles.length; ++k) {
                Pile pile = piles[k];
                int l = 0;
                Pile pile2 = new Pile();
                for (int m = 0; m < piles.length; ++m) {
                    if (k == m) continue;
                    Pile pile3 = piles[m];
                    double e = pile.getDistance(pile3);
                    d = Math.min(e, d);
                    if (!(e < spreadDistance)) continue;
                    ++l;
                    pile2.x = pile2.x + (pile3.x - pile.x);
                    pile2.z = pile2.z + (pile3.z - pile.z);
                }
                if (l > 0) {
                    pile2.x = pile2.x / (double)l;
                    pile2.z = pile2.z / (double)l;
                    double f = pile2.absolute();
                    if (f > 0.0) {
                        pile2.normalize();
                        pile.subtract(pile2);
                    } else {
                        pile.setPileLocation(random, minX, minZ, maxX, maxZ);
                    }
                    bl2 = true;
                }
                if (!pile.clamp(minX, minZ, maxX, maxZ)) continue;
                bl2 = true;
            }
            if (bl2) continue;
            for (Pile pile2 : piles) {
                if (pile2.isSafe(world, i)) continue;
                pile2.setPileLocation(random, minX, minZ, maxX, maxZ);
                bl2 = true;
            }
        }
        if (d == 3.4028234663852886E38) {
            d = 0.0;
        }
        if (j >= 10000) {
            if (bl) {
                throw FAILED_TEAMS_EXCEPTION.create(piles.length, Float.valueOf(center.x), Float.valueOf(center.y), String.format(Locale.ROOT, "%.2f", d));
            }
            throw FAILED_ENTITIES_EXCEPTION.create(piles.length, Float.valueOf(center.x), Float.valueOf(center.y), String.format(Locale.ROOT, "%.2f", d));
        }
    }

    private static double getMinDistance(Collection<? extends Entity> entities, ServerWorld world, Pile[] piles, int i, boolean bl) {
        double d = 0.0;
        int j = 0;
        HashMap<AbstractTeam, Pile> map = Maps.newHashMap();
        for (Entity entity : entities) {
            Pile pile;
            if (bl) {
                AbstractTeam abstractTeam;
                AbstractTeam abstractTeam2 = abstractTeam = entity instanceof PlayerEntity ? entity.getScoreboardTeam() : null;
                if (!map.containsKey(abstractTeam)) {
                    map.put(abstractTeam, piles[j++]);
                }
                pile = (Pile)map.get(abstractTeam);
            } else {
                pile = piles[j++];
            }
            entity.teleport((double)MathHelper.floor(pile.x) + 0.5, pile.getY(world, i), (double)MathHelper.floor(pile.z) + 0.5);
            double e = Double.MAX_VALUE;
            for (Pile pile2 : piles) {
                if (pile == pile2) continue;
                double f = pile.getDistance(pile2);
                e = Math.min(f, e);
            }
            d += e;
        }
        if (entities.size() < 2) {
            return 0.0;
        }
        return d /= (double)entities.size();
    }

    private static Pile[] makePiles(Random random, int count, double minX, double minZ, double maxX, double maxZ) {
        Pile[] piles = new Pile[count];
        for (int i = 0; i < piles.length; ++i) {
            Pile pile = new Pile();
            pile.setPileLocation(random, minX, minZ, maxX, maxZ);
            piles[i] = pile;
        }
        return piles;
    }

    static class Pile {
        private double x;
        private double z;

        private Pile() {
        }

        double getDistance(Pile other) {
            double d = this.x - other.x;
            double e = this.z - other.z;
            return Math.sqrt(d * d + e * e);
        }

        void normalize() {
            double d = this.absolute();
            this.x /= d;
            this.z /= d;
        }

        float absolute() {
            return MathHelper.sqrt(this.x * this.x + this.z * this.z);
        }

        public void subtract(Pile other) {
            this.x -= other.x;
            this.z -= other.z;
        }

        public boolean clamp(double minX, double minZ, double maxX, double maxZ) {
            boolean bl = false;
            if (this.x < minX) {
                this.x = minX;
                bl = true;
            } else if (this.x > maxX) {
                this.x = maxX;
                bl = true;
            }
            if (this.z < minZ) {
                this.z = minZ;
                bl = true;
            } else if (this.z > maxZ) {
                this.z = maxZ;
                bl = true;
            }
            return bl;
        }

        public int getY(BlockView blockView, int i) {
            BlockPos.Mutable mutable = new BlockPos.Mutable(this.x, (double)(i + 1), this.z);
            boolean bl = blockView.getBlockState(mutable).isAir();
            mutable.move(Direction.DOWN);
            boolean bl2 = blockView.getBlockState(mutable).isAir();
            while (mutable.getY() > blockView.getBottomY()) {
                mutable.move(Direction.DOWN);
                boolean bl3 = blockView.getBlockState(mutable).isAir();
                if (!bl3 && bl2 && bl) {
                    return mutable.getY() + 1;
                }
                bl = bl2;
                bl2 = bl3;
            }
            return i + 1;
        }

        public boolean isSafe(BlockView world, int i) {
            BlockPos blockPos = new BlockPos(this.x, (double)(this.getY(world, i) - 1), this.z);
            BlockState blockState = world.getBlockState(blockPos);
            Material material = blockState.getMaterial();
            return blockPos.getY() < i && !material.isLiquid() && material != Material.FIRE;
        }

        public void setPileLocation(Random random, double minX, double minZ, double maxX, double maxZ) {
            this.x = MathHelper.nextDouble(random, minX, maxX);
            this.z = MathHelper.nextDouble(random, minZ, maxZ);
        }
    }
}

