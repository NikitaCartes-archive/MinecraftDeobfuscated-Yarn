package net.minecraft;

import com.mojang.datafixers.Dynamic;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class class_4140<U> {
	public static final class_4140<Void> field_18437 = method_19092("dummy", Optional.empty());
	public static final class_4140<class_4208> field_18438 = method_19092("home", Optional.of(class_4208::method_19444));
	public static final class_4140<class_4208> field_18439 = method_19092("job_site", Optional.of(class_4208::method_19444));
	public static final class_4140<class_4208> field_18440 = method_19092("meeting_point", Optional.of(class_4208::method_19444));
	public static final class_4140<List<LivingEntity>> field_18441 = method_19092("mobs", Optional.empty());
	public static final class_4140<List<LivingEntity>> field_18442 = method_19092("visible_mobs", Optional.empty());
	public static final class_4140<List<PlayerEntity>> field_18443 = method_19092("nearest_players", Optional.empty());
	public static final class_4140<PlayerEntity> field_18444 = method_19092("nearest_visible_player", Optional.empty());
	public static final class_4140<class_4142> field_18445 = method_19092("walk_target", Optional.empty());
	public static final class_4140<class_4115> field_18446 = method_19092("look_target", Optional.empty());
	public static final class_4140<LivingEntity> field_18447 = method_19092("interaction_target", Optional.empty());
	public static final class_4140<VillagerEntity> field_18448 = method_19092("breed_target", Optional.empty());
	public static final class_4140<Path> field_18449 = method_19092("path", Optional.empty());
	public static final class_4140<List<class_4208>> field_18450 = method_19092("interactable_doors", Optional.empty());
	public static final class_4140<DamageSource> field_18451 = method_19092("hurt_by", Optional.empty());
	public static final class_4140<LivingEntity> field_18452 = method_19092("hurt_by_entity", Optional.empty());
	public static final class_4140<LivingEntity> field_18453 = method_19092("nearest_hostile", Optional.empty());
	private final Optional<Function<Dynamic<?>, U>> field_18454;
	private final Identifier field_18455;

	private class_4140(String string, Optional<Function<Dynamic<?>, U>> optional) {
		this.field_18455 = new Identifier(string);
		this.field_18454 = optional;
	}

	public Identifier method_19091() {
		return this.field_18455;
	}

	public String toString() {
		return this.field_18455.toString();
	}

	public Optional<Function<Dynamic<?>, U>> method_19093() {
		return this.field_18454;
	}

	private static <U> class_4140<U> method_19092(String string, Optional<Function<Dynamic<?>, U>> optional) {
		return Registry.method_10230(Registry.field_18793, new Identifier(string), new class_4140<>(string, optional));
	}
}
