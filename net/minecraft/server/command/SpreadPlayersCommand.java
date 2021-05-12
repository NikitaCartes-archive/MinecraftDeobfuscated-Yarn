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
    private static final int MAX_ATTEMPTS = 10000;
    private static final Dynamic4CommandExceptionType FAILED_TEAMS_EXCEPTION = new Dynamic4CommandExceptionType((pilesCount, x, z, maxSpreadDistance) -> new TranslatableText("commands.spreadplayers.failed.teams", pilesCount, x, z, maxSpreadDistance));
    private static final Dynamic4CommandExceptionType FAILED_ENTITIES_EXCEPTION = new Dynamic4CommandExceptionType((pilesCount, x, z, maxSpreadDistance) -> new TranslatableText("commands.spreadplayers.failed.entities", pilesCount, x, z, maxSpreadDistance));

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("spreadplayers").requires(source -> source.hasPermissionLevel(2))).then(CommandManager.argument("center", Vec2ArgumentType.vec2()).then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("spreadDistance", FloatArgumentType.floatArg(0.0f)).then((ArgumentBuilder<ServerCommandSource, ?>)((RequiredArgumentBuilder)CommandManager.argument("maxRange", FloatArgumentType.floatArg(1.0f)).then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("respectTeams", BoolArgumentType.bool()).then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("targets", EntityArgumentType.entities()).executes(context -> SpreadPlayersCommand.execute((ServerCommandSource)context.getSource(), Vec2ArgumentType.getVec2(context, "center"), FloatArgumentType.getFloat(context, "spreadDistance"), FloatArgumentType.getFloat(context, "maxRange"), ((ServerCommandSource)context.getSource()).getWorld().getTopY(), BoolArgumentType.getBool(context, "respectTeams"), EntityArgumentType.getEntities(context, "targets")))))).then(CommandManager.literal("under").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("maxHeight", IntegerArgumentType.integer(0)).then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("respectTeams", BoolArgumentType.bool()).then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("targets", EntityArgumentType.entities()).executes(context -> SpreadPlayersCommand.execute((ServerCommandSource)context.getSource(), Vec2ArgumentType.getVec2(context, "center"), FloatArgumentType.getFloat(context, "spreadDistance"), FloatArgumentType.getFloat(context, "maxRange"), IntegerArgumentType.getInteger(context, "maxHeight"), BoolArgumentType.getBool(context, "respectTeams"), EntityArgumentType.getEntities(context, "targets")))))))))));
    }

    private static int execute(ServerCommandSource source, Vec2f center, float spreadDistance, float maxRange, int maxY, boolean respectTeams, Collection<? extends Entity> players) throws CommandSyntaxException {
        Random random = new Random();
        double d = center.x - maxRange;
        double e = center.y - maxRange;
        double f = center.x + maxRange;
        double g = center.y + maxRange;
        Pile[] piles = SpreadPlayersCommand.makePiles(random, respectTeams ? SpreadPlayersCommand.getPileCountRespectingTeams(players) : players.size(), d, e, f, g);
        SpreadPlayersCommand.spread(center, spreadDistance, source.getWorld(), random, d, e, f, g, maxY, piles, respectTeams);
        double h = SpreadPlayersCommand.getMinDistance(players, source.getWorld(), piles, maxY, respectTeams);
        source.sendFeedback(new TranslatableText("commands.spreadplayers.success." + (respectTeams ? "teams" : "entities"), piles.length, Float.valueOf(center.x), Float.valueOf(center.y), String.format(Locale.ROOT, "%.2f", h)), true);
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

    private static void spread(Vec2f center, double spreadDistance, ServerWorld world, Random random, double minX, double minZ, double maxX, double maxZ, int maxY, Pile[] piles, boolean respectTeams) throws CommandSyntaxException {
        int i;
        boolean bl = true;
        double d = 3.4028234663852886E38;
        for (i = 0; i < 10000 && bl; ++i) {
            bl = false;
            d = 3.4028234663852886E38;
            for (int j = 0; j < piles.length; ++j) {
                Pile pile = piles[j];
                int k = 0;
                Pile pile2 = new Pile();
                for (int l = 0; l < piles.length; ++l) {
                    if (j == l) continue;
                    Pile pile3 = piles[l];
                    double e = pile.getDistance(pile3);
                    d = Math.min(e, d);
                    if (!(e < spreadDistance)) continue;
                    ++k;
                    pile2.x += pile3.x - pile.x;
                    pile2.z += pile3.z - pile.z;
                }
                if (k > 0) {
                    pile2.x /= (double)k;
                    pile2.z /= (double)k;
                    double f = pile2.absolute();
                    if (f > 0.0) {
                        pile2.normalize();
                        pile.subtract(pile2);
                    } else {
                        pile.setPileLocation(random, minX, minZ, maxX, maxZ);
                    }
                    bl = true;
                }
                if (!pile.clamp(minX, minZ, maxX, maxZ)) continue;
                bl = true;
            }
            if (bl) continue;
            for (Pile pile2 : piles) {
                if (pile2.isSafe(world, maxY)) continue;
                pile2.setPileLocation(random, minX, minZ, maxX, maxZ);
                bl = true;
            }
        }
        if (d == 3.4028234663852886E38) {
            d = 0.0;
        }
        if (i >= 10000) {
            if (respectTeams) {
                throw FAILED_TEAMS_EXCEPTION.create(piles.length, Float.valueOf(center.x), Float.valueOf(center.y), String.format(Locale.ROOT, "%.2f", d));
            }
            throw FAILED_ENTITIES_EXCEPTION.create(piles.length, Float.valueOf(center.x), Float.valueOf(center.y), String.format(Locale.ROOT, "%.2f", d));
        }
    }

    private static double getMinDistance(Collection<? extends Entity> entities, ServerWorld world, Pile[] piles, int maxY, boolean respectTeams) {
        double d = 0.0;
        int i = 0;
        HashMap<AbstractTeam, Pile> map = Maps.newHashMap();
        for (Entity entity : entities) {
            Pile pile;
            if (respectTeams) {
                AbstractTeam abstractTeam;
                AbstractTeam abstractTeam2 = abstractTeam = entity instanceof PlayerEntity ? entity.getScoreboardTeam() : null;
                if (!map.containsKey(abstractTeam)) {
                    map.put(abstractTeam, piles[i++]);
                }
                pile = (Pile)map.get(abstractTeam);
            } else {
                pile = piles[i++];
            }
            entity.teleport((double)MathHelper.floor(pile.x) + 0.5, pile.getY(world, maxY), (double)MathHelper.floor(pile.z) + 0.5);
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
        double x;
        double z;

        Pile() {
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

        public int getY(BlockView blockView, int maxY) {
            BlockPos.Mutable mutable = new BlockPos.Mutable(this.x, (double)(maxY + 1), this.z);
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
            return maxY + 1;
        }

        public boolean isSafe(BlockView world, int maxY) {
            BlockPos blockPos = new BlockPos(this.x, (double)(this.getY(world, maxY) - 1), this.z);
            BlockState blockState = world.getBlockState(blockPos);
            Material material = blockState.getMaterial();
            return blockPos.getY() < maxY && !material.isLiquid() && material != Material.FIRE;
        }

        public void setPileLocation(Random random, double minX, double minZ, double maxX, double maxZ) {
            this.x = MathHelper.nextDouble(random, minX, maxX);
            this.z = MathHelper.nextDouble(random, minZ, maxZ);
        }
    }
}

