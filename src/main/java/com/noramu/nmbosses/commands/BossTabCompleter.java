package com.noramu.nmbosses.commands;

import com.noramu.nmbosses.NmBosses;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class BossTabCompleter implements TabCompleter {

    private final NmBosses plugin;
    private final List<String> subCommands = Arrays.asList(
            "help", "list", "create", "delete", "spawn", "despawn",
            "setspawn", "autospawn", "reload", "gui");

    public BossTabCompleter(NmBosses plugin) {
        this.plugin = plugin;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (!sender.hasPermission("nmbosses.admin")) {
            return completions;
        }

        if (args.length == 1) {
            // Ana komutlar
            String input = args[0].toLowerCase();
            completions = subCommands.stream()
                    .filter(cmd -> cmd.startsWith(input))
                    .collect(Collectors.toList());
        } else if (args.length == 2) {
            String subCommand = args[0].toLowerCase();
            String input = args[1].toLowerCase();

            switch (subCommand) {
                case "delete":
                case "spawn":
                case "despawn":
                case "setspawn":
                case "autospawn":
                case "info":
                case "edit":
                    // Boss isimleri
                    completions = plugin.getBossManager().getBossIds().stream()
                            .filter(id -> id.toLowerCase().startsWith(input))
                            .collect(Collectors.toList());

                    if (subCommand.equals("despawn")) {
                        if ("all".startsWith(input)) {
                            completions.add("all");
                        }
                    }
                    break;

                case "create":
                    // Örnek isimler
                    List<String> examples = Arrays.asList(
                            "dragon_boss", "skeleton_king", "zombie_lord",
                            "giant_spider", "ender_knight");
                    completions = examples.stream()
                            .filter(name -> name.startsWith(input))
                            .collect(Collectors.toList());
                    break;
            }
        } else if (args.length == 3) {
            String subCommand = args[0].toLowerCase();
            String input = args[2].toLowerCase();

            if (subCommand.equals("create")) {
                // Entity tipleri
                completions = Arrays.stream(EntityType.values())
                        .filter(type -> type.isAlive() && type.isSpawnable())
                        .map(EntityType::name)
                        .filter(name -> name.toLowerCase().startsWith(input))
                        .collect(Collectors.toList());
            }
        }

        return completions;
    }
}
