package net.minecraft;

import com.google.common.collect.Maps;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2641 implements class_2596<class_2602> {
	private RootCommandNode<class_2172> field_12123;

	public class_2641() {
	}

	public class_2641(RootCommandNode<class_2172> rootCommandNode) {
		this.field_12123 = rootCommandNode;
	}

	@Override
	public void method_11053(class_2540 arg) throws IOException {
		class_2641.class_2642[] lvs = new class_2641.class_2642[arg.method_10816()];
		Deque<class_2641.class_2642> deque = new ArrayDeque(lvs.length);

		for (int i = 0; i < lvs.length; i++) {
			lvs[i] = this.method_11405(arg);
			deque.add(lvs[i]);
		}

		while (!deque.isEmpty()) {
			boolean bl = false;
			Iterator<class_2641.class_2642> iterator = deque.iterator();

			while (iterator.hasNext()) {
				class_2641.class_2642 lv = (class_2641.class_2642)iterator.next();
				if (lv.method_11406(lvs)) {
					iterator.remove();
					bl = true;
				}
			}

			if (!bl) {
				throw new IllegalStateException("Server sent an impossible command tree");
			}
		}

		this.field_12123 = (RootCommandNode<class_2172>)lvs[arg.method_10816()].field_12128;
	}

	@Override
	public void method_11052(class_2540 arg) throws IOException {
		Map<CommandNode<class_2172>, Integer> map = Maps.<CommandNode<class_2172>, Integer>newHashMap();
		Deque<CommandNode<class_2172>> deque = new ArrayDeque();
		deque.add(this.field_12123);

		while (!deque.isEmpty()) {
			CommandNode<class_2172> commandNode = (CommandNode<class_2172>)deque.pollFirst();
			if (!map.containsKey(commandNode)) {
				int i = map.size();
				map.put(commandNode, i);
				deque.addAll(commandNode.getChildren());
				if (commandNode.getRedirect() != null) {
					deque.add(commandNode.getRedirect());
				}
			}
		}

		CommandNode<class_2172>[] commandNodes = new CommandNode[map.size()];

		for (Entry<CommandNode<class_2172>, Integer> entry : map.entrySet()) {
			commandNodes[entry.getValue()] = (CommandNode<class_2172>)entry.getKey();
		}

		arg.method_10804(commandNodes.length);

		for (CommandNode<class_2172> commandNode2 : commandNodes) {
			this.method_11401(arg, commandNode2, map);
		}

		arg.method_10804((Integer)map.get(this.field_12123));
	}

	private class_2641.class_2642 method_11405(class_2540 arg) {
		byte b = arg.readByte();
		int[] is = arg.method_10787();
		int i = (b & 8) != 0 ? arg.method_10816() : 0;
		ArgumentBuilder<class_2172, ?> argumentBuilder = this.method_11402(arg, b);
		return new class_2641.class_2642(argumentBuilder, b, i, is);
	}

	@Nullable
	private ArgumentBuilder<class_2172, ?> method_11402(class_2540 arg, byte b) {
		int i = b & 3;
		if (i == 2) {
			String string = arg.method_10800(32767);
			ArgumentType<?> argumentType = class_2316.method_10014(arg);
			if (argumentType == null) {
				return null;
			} else {
				RequiredArgumentBuilder<class_2172, ?> requiredArgumentBuilder = RequiredArgumentBuilder.argument(string, argumentType);
				if ((b & 16) != 0) {
					requiredArgumentBuilder.suggests(class_2321.method_10024(arg.method_10810()));
				}

				return requiredArgumentBuilder;
			}
		} else {
			return i == 1 ? LiteralArgumentBuilder.literal(arg.method_10800(32767)) : null;
		}
	}

	private void method_11401(class_2540 arg, CommandNode<class_2172> commandNode, Map<CommandNode<class_2172>, Integer> map) {
		byte b = 0;
		if (commandNode.getRedirect() != null) {
			b = (byte)(b | 8);
		}

		if (commandNode.getCommand() != null) {
			b = (byte)(b | 4);
		}

		if (commandNode instanceof RootCommandNode) {
			b = (byte)(b | 0);
		} else if (commandNode instanceof ArgumentCommandNode) {
			b = (byte)(b | 2);
			if (((ArgumentCommandNode)commandNode).getCustomSuggestions() != null) {
				b = (byte)(b | 16);
			}
		} else {
			if (!(commandNode instanceof LiteralCommandNode)) {
				throw new UnsupportedOperationException("Unknown node type " + commandNode);
			}

			b = (byte)(b | 1);
		}

		arg.writeByte(b);
		arg.method_10804(commandNode.getChildren().size());

		for (CommandNode<class_2172> commandNode2 : commandNode.getChildren()) {
			arg.method_10804((Integer)map.get(commandNode2));
		}

		if (commandNode.getRedirect() != null) {
			arg.method_10804((Integer)map.get(commandNode.getRedirect()));
		}

		if (commandNode instanceof ArgumentCommandNode) {
			ArgumentCommandNode<class_2172, ?> argumentCommandNode = (ArgumentCommandNode<class_2172, ?>)commandNode;
			arg.method_10814(argumentCommandNode.getName());
			class_2316.method_10019(arg, argumentCommandNode.getType());
			if (argumentCommandNode.getCustomSuggestions() != null) {
				arg.method_10812(class_2321.method_10027(argumentCommandNode.getCustomSuggestions()));
			}
		} else if (commandNode instanceof LiteralCommandNode) {
			arg.method_10814(((LiteralCommandNode)commandNode).getLiteral());
		}
	}

	public void method_11404(class_2602 arg) {
		arg.method_11145(this);
	}

	@Environment(EnvType.CLIENT)
	public RootCommandNode<class_2172> method_11403() {
		return this.field_12123;
	}

	static class class_2642 {
		@Nullable
		private final ArgumentBuilder<class_2172, ?> field_12127;
		private final byte field_12124;
		private final int field_12126;
		private final int[] field_12125;
		private CommandNode<class_2172> field_12128;

		private class_2642(@Nullable ArgumentBuilder<class_2172, ?> argumentBuilder, byte b, int i, int[] is) {
			this.field_12127 = argumentBuilder;
			this.field_12124 = b;
			this.field_12126 = i;
			this.field_12125 = is;
		}

		public boolean method_11406(class_2641.class_2642[] args) {
			if (this.field_12128 == null) {
				if (this.field_12127 == null) {
					this.field_12128 = new RootCommandNode<>();
				} else {
					if ((this.field_12124 & 8) != 0) {
						if (args[this.field_12126].field_12128 == null) {
							return false;
						}

						this.field_12127.redirect(args[this.field_12126].field_12128);
					}

					if ((this.field_12124 & 4) != 0) {
						this.field_12127.executes(commandContext -> 0);
					}

					this.field_12128 = this.field_12127.build();
				}
			}

			for (int i : this.field_12125) {
				if (args[i].field_12128 == null) {
					return false;
				}
			}

			for (int ix : this.field_12125) {
				CommandNode<class_2172> commandNode = args[ix].field_12128;
				if (!(commandNode instanceof RootCommandNode)) {
					this.field_12128.addChild(commandNode);
				}
			}

			return true;
		}
	}
}
