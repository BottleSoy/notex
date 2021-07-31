package name.soy.notex;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.block.Block;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.BlockPredicateArgumentType;
import net.minecraft.command.argument.BlockStateArgument;
import net.minecraft.command.argument.BlockStateArgumentType;
import net.minecraft.server.command.*;
import net.minecraft.text.LiteralText;

import java.util.ArrayList;
import java.util.List;

public class NotexCommand {

	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(CommandManager.literal("notex")
				.requires(s -> s.hasPermissionLevel(2))
				.then(CommandManager.literal("reg")
						.then(CommandManager.argument("ins-name", StringArgumentType.string())
								.then(CommandManager.argument("sound-namespace", StringArgumentType.string())
										.suggests((context, builder) -> {
											List<String> ids = new ArrayList<>();
											context.getSource().getSoundIds().forEach(id -> ids.add(id.toString()));
											return CommandSource.suggestMatching(ids, builder);
										})
										.then(CommandManager.literal("separate")
												.then(CommandManager.argument("min-point", IntegerArgumentType.integer(0, 128))
														.then(CommandManager.argument("max-point", IntegerArgumentType.integer(0, 128))
																.then(CommandManager.argument("block", BlockStateArgumentType.blockState())
																		.executes(context -> regIns(context, CustomIns.InsMode.SEPARATE)))
														)
												)
										).then(CommandManager.literal("stretch")
												.then(CommandManager.argument("min-point", IntegerArgumentType.integer(0, 128))
														.then(CommandManager.argument("max-point", IntegerArgumentType.integer(0, 128))
																.then(CommandManager.argument("block", BlockStateArgumentType.blockState())
																		.executes(context -> regIns(context, CustomIns.InsMode.STRETCH))
																)
														)
												)
										)
								)
						)
				).then(CommandManager.literal("unreg")
						.then(CommandManager.argument("ins-name", StringArgumentType.string())
								.suggests((context, builder) -> CommandSource.suggestMatching(Notex.insNames.keySet(), builder))
								.executes(context -> {
									String ins = StringArgumentType.getString(context, "ins-name");
									if (Notex.removeIns(ins)) {
										context.getSource().sendFeedback(new LiteralText("乐器:" + ins + "取消注册成功"), true);
										return 0;
									} else {
										context.getSource().sendError(new LiteralText("乐器:" + ins + "取消注册失败"));
										return 1;
									}
								})
						)
				).then(CommandManager.literal("debug").executes(context -> {
					context.getSource().sendFeedback(new LiteralText(Notex.insNames.toString()), false);
					context.getSource().sendFeedback(new LiteralText(Notex.insMap.toString()), false);
					context.getSource().sendFeedback(new LiteralText(Notex.namePre.toString()), false);
					return 0;
				}))
		);
	}

	private static int regIns(CommandContext<ServerCommandSource> context, CustomIns.InsMode type) {
		String insName = StringArgumentType.getString(context, "ins-name");
		try {
			int min = IntegerArgumentType.getInteger(context, "min-point");
			int max = IntegerArgumentType.getInteger(context, "max-point");
			String soundSpace = StringArgumentType.getString(context, "sound-namespace");
			if (!Notex.insNames.containsKey(insName)) {
				Block block = BlockStateArgumentType.getBlockState(context, "block").getBlockState().getBlock();
				Notex.insMap.put(
						new SavedBlockPredicate.Builder().of(block).build(),
						new CustomIns(insName, min, max, soundSpace, type));
				context.getSource().sendFeedback(new LiteralText("乐器:" + insName + "注册成功"), true);
				return 0;
			}
			context.getSource().sendError(new LiteralText("乐器:" + insName + "注册注册失败,已有此乐器"));
			return 1;
		} catch (Exception e) {
			e.printStackTrace();
			context.getSource().sendError(new LiteralText("乐器:" + insName + "注册注册失败,发生了错误"));
			return 1;
		}
	}
}
