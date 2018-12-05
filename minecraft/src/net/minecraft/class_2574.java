package net.minecraft;

import com.google.common.base.Joiner;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.command.EntitySelector;
import net.minecraft.command.EntitySelectorReader;
import net.minecraft.command.arguments.BlockPosArgumentType;
import net.minecraft.command.arguments.NbtPathArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.predicate.NbtPredicate;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.AbstractTextComponent;
import net.minecraft.text.StringTextComponent;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TextFormatter;
import net.minecraft.util.math.BlockPos;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class class_2574 extends AbstractTextComponent implements class_2566 {
	private static final Logger field_11777 = LogManager.getLogger();
	protected final boolean field_11778;
	protected final String field_11776;
	@Nullable
	protected final NbtPathArgumentType.class_2209 field_11779;

	@Nullable
	private static NbtPathArgumentType.class_2209 method_10919(String string) {
		try {
			return new NbtPathArgumentType().method_9362(new StringReader(string));
		} catch (CommandSyntaxException var2) {
			return null;
		}
	}

	public class_2574(String string, boolean bl) {
		this(string, method_10919(string), bl);
	}

	protected class_2574(String string, @Nullable NbtPathArgumentType.class_2209 arg, boolean bl) {
		this.field_11776 = string;
		this.field_11779 = arg;
		this.field_11778 = bl;
	}

	protected abstract Stream<CompoundTag> method_10916(ServerCommandSource serverCommandSource) throws CommandSyntaxException;

	@Override
	public String getText() {
		return "";
	}

	public String method_10920() {
		return this.field_11776;
	}

	public boolean method_10921() {
		return this.field_11778;
	}

	@Override
	public TextComponent method_10890(@Nullable ServerCommandSource serverCommandSource, @Nullable Entity entity) throws CommandSyntaxException {
		if (serverCommandSource != null && this.field_11779 != null) {
			Stream<String> stream = this.method_10916(serverCommandSource).flatMap(compoundTag -> {
				try {
					return this.field_11779.method_9366(compoundTag).stream();
				} catch (CommandSyntaxException var3x) {
					return Stream.empty();
				}
			}).map(Tag::asString);
			return (TextComponent)(this.field_11778
				? (TextComponent)stream.flatMap(string -> {
					try {
						TextComponent textComponent = TextComponent.Serializer.fromJsonString(string);
						return Stream.of(TextFormatter.method_10881(serverCommandSource, textComponent, entity));
					} catch (Exception var4) {
						field_11777.warn("Failed to parse component: " + string, (Throwable)var4);
						return Stream.of();
					}
				}).reduce((textComponent, textComponent2) -> textComponent.append(", ").append(textComponent2)).orElse(new StringTextComponent(""))
				: new StringTextComponent(Joiner.on(", ").join(stream.iterator())));
		} else {
			return new StringTextComponent("");
		}
	}

	public static class class_2575 extends class_2574 {
		private final String field_11780;
		@Nullable
		private final class_2267 field_16408;

		public class_2575(String string, boolean bl, String string2) {
			super(string, bl);
			this.field_11780 = string2;
			this.field_16408 = this.method_16121(this.field_11780);
		}

		@Nullable
		private class_2267 method_16121(String string) {
			try {
				return BlockPosArgumentType.create().method_9699(new StringReader(string));
			} catch (CommandSyntaxException var3) {
				return null;
			}
		}

		private class_2575(String string, @Nullable NbtPathArgumentType.class_2209 arg, boolean bl, String string2, @Nullable class_2267 arg2) {
			super(string, arg, bl);
			this.field_11780 = string2;
			this.field_16408 = arg2;
		}

		@Nullable
		public String method_10922() {
			return this.field_11780;
		}

		@Override
		public TextComponent cloneShallow() {
			return new class_2574.class_2575(this.field_11776, this.field_11779, this.field_11778, this.field_11780, this.field_16408);
		}

		@Override
		protected Stream<CompoundTag> method_10916(ServerCommandSource serverCommandSource) {
			if (this.field_16408 != null) {
				ServerWorld serverWorld = serverCommandSource.getWorld();
				BlockPos blockPos = this.field_16408.method_9704(serverCommandSource);
				if (serverWorld.method_8477(blockPos)) {
					BlockEntity blockEntity = serverWorld.getBlockEntity(blockPos);
					if (blockEntity != null) {
						return Stream.of(blockEntity.toTag(new CompoundTag()));
					}
				}
			}

			return Stream.empty();
		}

		@Override
		public boolean equals(Object object) {
			if (this == object) {
				return true;
			} else if (!(object instanceof class_2574.class_2575)) {
				return false;
			} else {
				class_2574.class_2575 lv = (class_2574.class_2575)object;
				return Objects.equals(this.field_11780, lv.field_11780) && Objects.equals(this.field_11776, lv.field_11776) && super.equals(object);
			}
		}

		@Override
		public String toString() {
			return "BlockPosArgument{pos='"
				+ this.field_11780
				+ '\''
				+ "path='"
				+ this.field_11776
				+ '\''
				+ ", siblings="
				+ this.children
				+ ", style="
				+ this.getStyle()
				+ '}';
		}
	}

	public static class class_2576 extends class_2574 {
		private final String field_11782;
		@Nullable
		private final EntitySelector field_11781;

		public class_2576(String string, boolean bl, String string2) {
			super(string, bl);
			this.field_11782 = string2;
			this.field_11781 = method_10923(string2);
		}

		@Nullable
		private static EntitySelector method_10923(String string) {
			try {
				EntitySelectorReader entitySelectorReader = new EntitySelectorReader(new StringReader(string));
				return entitySelectorReader.read();
			} catch (CommandSyntaxException var2) {
				return null;
			}
		}

		private class_2576(String string, @Nullable NbtPathArgumentType.class_2209 arg, boolean bl, String string2, @Nullable EntitySelector entitySelector) {
			super(string, arg, bl);
			this.field_11782 = string2;
			this.field_11781 = entitySelector;
		}

		public String method_10924() {
			return this.field_11782;
		}

		@Override
		public TextComponent cloneShallow() {
			return new class_2574.class_2576(this.field_11776, this.field_11779, this.field_11778, this.field_11782, this.field_11781);
		}

		@Override
		protected Stream<CompoundTag> method_10916(ServerCommandSource serverCommandSource) throws CommandSyntaxException {
			if (this.field_11781 != null) {
				List<? extends Entity> list = this.field_11781.getEntities(serverCommandSource);
				return list.stream().map(NbtPredicate::method_9076);
			} else {
				return Stream.empty();
			}
		}

		@Override
		public boolean equals(Object object) {
			if (this == object) {
				return true;
			} else if (!(object instanceof class_2574.class_2576)) {
				return false;
			} else {
				class_2574.class_2576 lv = (class_2574.class_2576)object;
				return Objects.equals(this.field_11782, lv.field_11782) && Objects.equals(this.field_11776, lv.field_11776) && super.equals(object);
			}
		}

		@Override
		public String toString() {
			return "EntityNbtComponent{selector='"
				+ this.field_11782
				+ '\''
				+ "path='"
				+ this.field_11776
				+ '\''
				+ ", siblings="
				+ this.children
				+ ", style="
				+ this.getStyle()
				+ '}';
		}
	}
}
