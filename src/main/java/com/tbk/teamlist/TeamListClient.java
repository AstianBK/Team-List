package com.tbk.teamlist;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.tbk.teamlist.team.ComponentTeam;
import com.tbk.teamlist.team.TLIconsRegistry;
import com.tbk.teamlist.team.TeamManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;

import java.awt.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;


public class TeamListClient implements ClientModInitializer {
	private static final SuggestionProvider<FabricClientCommandSource> PLAYERS = (context, builder) -> {
		MinecraftClient client = MinecraftClient.getInstance();
		if (client.getNetworkHandler() == null) return Suggestions.empty();

		Collection<String> players = client.getNetworkHandler().getPlayerList()
				.stream()
				.map(PlayerListEntry::getProfile)
				.map(GameProfile::getName) // Obtiene los nombres de los jugadores conectados
				.collect(Collectors.toList());

		return CommandSource.suggestMatching(players, builder);	};
	private static final SuggestionProvider<FabricClientCommandSource> PLAYERS_TEAMS = (context, builder) -> {
		Collection<String> players = TeamManager.teams.get(context.getArgument("team",String.class)).players;

		return CommandSource.suggestMatching(players, builder); // Devuelve la lista de sugerencias
	};
	private static final SuggestionProvider<FabricClientCommandSource> ICONS = (context, builder) -> {
		Collection<String> players = TLIconsRegistry.mapIcons.values().stream()
				.map(ComponentTeam::getId).collect(Collectors.toList());

		return CommandSource.suggestMatching(players, builder); // Devuelve la lista de sugerencias
	};

	private static final SuggestionProvider<FabricClientCommandSource> COLOR = (context, builder) -> {
		Collection<String> players = TeamManager.Color.getNames();

		return CommandSource.suggestMatching(players, builder); // Devuelve la lista de sugerencias
	};
	private static final SuggestionProvider<FabricClientCommandSource> TEAMS = (context, builder) -> {
		Collection<String> players = TeamManager.teams.keySet();

		return CommandSource.suggestMatching(players, builder); // Devuelve la lista de sugerencias
	};
	@Override
	public void onInitializeClient() {
		ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
			TeamManager.loadIconCustom();
			TeamManager.load();
		});

		ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
			TeamManager.save();
		});
		ClientCommandRegistrationCallback.EVENT.register(this::registerCommands);
	}

	private void registerCommands(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess) {
		dispatcher.register(ClientCommandManager.literal("teamlist")
				.then(ClientCommandManager.literal("createTeam")
						.then(ClientCommandManager.argument("team", StringArgumentType.string())
								.executes(context -> {
									String team = StringArgumentType.getString(context, "team");

									if(!TeamManager.isTeamPresent(team)){
										TeamManager.createTeam(team);
										MinecraftClient.getInstance().player.sendMessage(Text.translatable("msg.teamlist.team").append(Text.literal(team)).append(Text.translatable("msg.teamlist.team_create")));
									}else {
										MinecraftClient.getInstance().player.sendMessage(Text.translatable("msg.teamlist.team").append(Text.literal(team)).append(Text.translatable("msg.teamlist.is_present")));
									}
									return 1;
								}))
				).then(ClientCommandManager.literal("deleteTeam")
						.then(ClientCommandManager.argument("team", StringArgumentType.string()).suggests(TEAMS)
								.executes(context -> {
									String team = StringArgumentType.getString(context, "team");

									if(TeamManager.isTeamPresent(team)){
										MinecraftClient.getInstance().player.sendMessage(Text.translatable("msg.teamlist.team").append(Text.literal(team).withColor(TeamManager.teams.get(team).color)).append(Text.translatable("msg.teamlist.team_remove")));
										TeamManager.deleteTeam(team);
									}else {
										MinecraftClient.getInstance().player.sendMessage(Text.translatable("msg.teamlist.team").append(Text.literal(team)).append(Text.translatable("msg.teamlist.no_present")));
									}
									return 1;
								})))
				.then(ClientCommandManager.literal("changeColor")
						.then(ClientCommandManager.argument("team", StringArgumentType.string()).suggests(TEAMS)
								.then(ClientCommandManager.argument("color", StringArgumentType.string()).suggests(COLOR)
										.executes(context -> {
											String team = StringArgumentType.getString(context, "team");
											String color = StringArgumentType.getString(context,"color");

											if(TeamManager.isTeamPresent(team) && TeamManager.isColor(color)){
												TeamManager.modifyColor(team,color);
												MinecraftClient.getInstance().player.sendMessage(Text.translatable("msg.teamlist.team").append(Text.literal(team).withColor(TeamManager.teams.get(team).color)).append(Text.translatable("msg.teamlist.change_color").append(Text.literal(color))),false);
											}else {
												MinecraftClient.getInstance().player.sendMessage(Text.translatable("msg.teamlist.team").append(Text.literal(team)).append(Text.translatable("msg.teamlist.no_change_color").append(Text.literal(color))));
											}
											return 1;
										})
								)))
				.then(ClientCommandManager.literal("changeIcon")
						.then(ClientCommandManager.argument("team", StringArgumentType.string()).suggests(TEAMS)
								.then(ClientCommandManager.argument("icon", StringArgumentType.greedyString()).suggests(ICONS)
										.executes(context -> {
											String team = StringArgumentType.getString(context, "team");
											String color = StringArgumentType.getString(context,"icon");

											if(TeamManager.isTeamPresent(team) && TeamManager.isIcon(color)){
												TeamManager.modifyIcon(team,color);
												MinecraftClient.getInstance().player.sendMessage(Text.translatable("msg.teamlist.team").append(Text.literal(team).withColor(TeamManager.teams.get(team).color)).append(Text.translatable("msg.teamlist.change_icon").append(Text.literal(color))));
											}else {
												MinecraftClient.getInstance().player.sendMessage(Text.translatable("msg.teamlist.team").append(Text.literal(team)).append(Text.translatable("msg.teamlist.no_change_icon").append(Text.literal(color))));
											}
											return 1;
										})
								)))
				.then(ClientCommandManager.literal("removeIcon")
						.then(ClientCommandManager.argument("team", StringArgumentType.string()).suggests(TEAMS)
								.executes(context -> {
									String team = StringArgumentType.getString(context, "team");

									if(TeamManager.isTeamPresent(team)){
										TeamManager.removeIcon(team);
										MinecraftClient.getInstance().player.sendMessage(Text.translatable("msg.teamlist.team").append(Text.literal(team).withColor(TeamManager.teams.get(team).color)).append(Text.translatable("msg.teamlist.remove_icon")),false);
									}else {
										MinecraftClient.getInstance().player.sendMessage(Text.translatable("msg.teamlist.team").append(Text.literal(team)).append(Text.translatable("msg.teamlist.no_remove_icon")));
									}
									return 1;
								})))
				.then(ClientCommandManager.literal("addPlayer")
						.then(ClientCommandManager.argument("team", StringArgumentType.string()).suggests(TEAMS)
								.then(ClientCommandManager.argument("player", StringArgumentType.greedyString()).suggests(PLAYERS)
										.executes(context -> {
											String team = StringArgumentType.getString(context, "team");
											String player = StringArgumentType.getString(context, "player");

											if(TeamManager.teams.containsKey(team) && !TeamManager.isPresentPlayer(team,player)){
												TeamManager.addPlayer(team,player);
												MinecraftClient.getInstance().player.sendMessage(Text.translatable("msg.teamlist.team").append(Text.literal(team).withColor(TeamManager.teams.get(team).color)).append(Text.translatable("msg.teamlist.add_player")).append(Text.literal(player)));
											}else {
												MinecraftClient.getInstance().player.sendMessage(Text.translatable("msg.teamlist.team").append(Text.literal(team)).append(Text.translatable("msg.teamlist.no_add_player")));
											}
											return 1;
										}))
						)
				).then(ClientCommandManager.literal("removePlayer")
						.then(ClientCommandManager.argument("team", StringArgumentType.string()).suggests(TEAMS)
								.then(ClientCommandManager.argument("player", StringArgumentType.greedyString()).suggests(PLAYERS_TEAMS)
										.executes(context -> {
											String team = StringArgumentType.getString(context, "team");
											String player = StringArgumentType.getString(context, "player");

											if(TeamManager.teams.containsKey(team) && TeamManager.isPresentPlayer(team,player)){
												TeamManager.removePlayer(team,player);
												MinecraftClient.getInstance().player.sendMessage(Text.translatable("msg.teamlist.team").append(Text.literal(team).withColor(TeamManager.teams.get(team).color)).append(Text.translatable("msg.teamlist.remove_player")).append(Text.literal(player)));
											}else {
												MinecraftClient.getInstance().player.sendMessage(Text.translatable("msg.teamlist.team").append(Text.literal(team)).append(Text.translatable("msg.teamlist.no_remove_player")).append(Text.literal(player)));
											}
											return 1;
										}))
						)
				)
		);
	}



}