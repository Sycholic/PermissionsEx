/*
 * PermissionsEx - Permissions plugin for Bukkit
 * Copyright (C) 2011 t3hk0d3 http://www.tehkode.ru
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package ru.tehkode.permissions.bukkit.commands;

import java.util.Map;
import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.tehkode.permissions.PermissionGroup;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;
import ru.tehkode.permissions.commands.Command;
import ru.tehkode.permissions.exceptions.RankingException;

public class PromotionCommands extends PermissionsCommand {

    @Command(name = "pex",
            syntax = "group <group> rank [rank] [ladder]",
            description = "Get or set <group> [rank] [ladder]",
            isPrimary = true,
            permission = "permissions.groups.rank.<group>")
    public void rankGroup(PermissionsEx plugin, CommandSender sender, Map<String, String> args) {
        String groupName = this.autoCompleteGroupName(args.get("group"));

        PermissionGroup group = plugin.getPermissionsManager().getGroup(groupName);

        if (group == null) {
            sender.sendMessage(ChatColor.RED + "Group \"" + groupName + "\" not found");
            return;
        }

        if (args.get("rank") != null) {
            String newRank = args.get("rank").trim();

            try {
                group.setRank(Integer.parseInt(newRank));
            } catch (NumberFormatException e) {
                sender.sendMessage("Wrong rank. Make sure it's number.");
            }

            if (args.containsKey("ladder")) {
                group.setRankLadder(args.get("ladder"));
            }
        }

        int rank = group.getRank();

        if (rank > 0) {
            sender.sendMessage("Group " + group.getIdentifier() + " rank is " + rank + " (ladder = " + group.getRankLadder() + ")");
        } else {
            sender.sendMessage("Group " + group.getIdentifier() + " is unranked");
        }
    }

    @Command(name = "pex",
            syntax = "promote <user> [ladder]",
            description = "Promotes <user> to next group on [ladder]",
            isPrimary = true)
    public void promoteUser(PermissionsEx plugin, CommandSender sender, Map<String, String> args) {
        if (args.get("user").length() < 2) {
            sender.sendMessage(ChatColor.RED + "User name length can not be less then 2!");
            return;
        }

        String userName = this.autoCompletePlayerName(args.get("user"));

        if (userName == null) {
            sender.sendMessage(ChatColor.RED + "User does not match any known names!");
            return;
        }
        PermissionUser user = plugin.getPermissionsManager().getUser(userName);

        String promoterName = "console";
        String ladder = "default";

        if (args.containsKey("ladder")) {
            ladder = args.get("ladder");
        }

        PermissionUser promoter = null;
        if (sender instanceof Player) {
            promoter = plugin.getPermissionsManager().getUser((Player) sender);
            if (!promoter.has("permissions.user.promote." + ladder, ((Player) sender).getWorld().getName())) {
                sender.sendMessage(ChatColor.RED + "You don't have enough permissions to promote on this ladder");
                return;
            }

            promoterName = promoter.getName();
        }

        try {
            PermissionGroup targetGroup = user.promote(promoter, ladder);

            this.informPlayer(plugin, user, "You have been promoted on " + targetGroup.getRankLadder() + " ladder to " + targetGroup.getIdentifier() + " group");
            sender.sendMessage("User " + describeUser(user) + " promoted to " + targetGroup.getIdentifier() + " group");
            plugin.getLogger().log(Level.INFO, "User {0} has been promoted to {1} group on {2} ladder by {3}", new Object[]{describeUser(user), targetGroup.getIdentifier(), targetGroup.getRankLadder(), promoterName});
        } catch (RankingException e) {
            sender.sendMessage(ChatColor.RED + "Promotion error: " + e.getMessage());
            plugin.getLogger().log(Level.SEVERE, "Ranking Error ({0} > {1}): {2}", new Object[]{promoterName, describeUser(e.getTarget()), e.getMessage()});
        }
    }

    @Command(name = "pex",
            syntax = "demote <user> [ladder]",
            description = "Demotes <user> to previous group or [ladder]",
            isPrimary = true)
    public void demoteUser(PermissionsEx plugin, CommandSender sender, Map<String, String> args) {
        if (args.get("user").length() < 2) {
            sender.sendMessage(ChatColor.RED + "User name length can not be less then 2!");
            return;
        }

        String userName = this.autoCompletePlayerName(args.get("user"));
        if (userName == null) {
            sender.sendMessage(ChatColor.RED + "User does not match any known names!");
            return;
        }
        PermissionUser user = plugin.getPermissionsManager().getUser(userName);

        String demoterName = "console";
        String ladder = "default";

        if (args.containsKey("ladder")) {
            ladder = args.get("ladder");
        }

        PermissionUser demoter = null;
        if (sender instanceof Player) {
            demoter = plugin.getPermissionsManager().getUser((Player) sender);

            if (!demoter.has("permissions.user.demote." + ladder, ((Player) sender).getWorld().getName())) {
                sender.sendMessage(ChatColor.RED + "You don't have enough permissions to demote on this ladder");
                return;
            }

            demoterName = demoter.getName();
        }

        try {
            PermissionGroup targetGroup = user.demote(demoter, args.get("ladder"));

            this.informPlayer(plugin, user, "You have been demoted on " + targetGroup.getRankLadder() + " ladder to " + targetGroup.getIdentifier() + " group");
            sender.sendMessage("User " + describeUser(user) + " demoted to " + targetGroup.getIdentifier() + " group");
            plugin.getLogger().log(Level.INFO, "User {0} has been demoted to {1} group on {2} ladder by {3}", new Object[]{describeUser(user), targetGroup.getIdentifier(), targetGroup.getRankLadder(), demoterName});
        } catch (RankingException e) {
            sender.sendMessage(ChatColor.RED + "Demotion error: " + e.getMessage());
            plugin.getLogger().log(Level.SEVERE, "Ranking Error ({0} demotes {1}): {2}", new Object[]{demoterName, describeUser(e.getTarget()), e.getMessage()});
        }
    }

    @Command(name = "promote",
            syntax = "<user> [ladder]",
            description = "Promotes <user> to next group",
            isPrimary = true,
            permission = "permissions.user.rank.promote")
    public void promoteUserAlias(PermissionsEx plugin, CommandSender sender, Map<String, String> args) {
        this.promoteUser(plugin, sender, args);
    }

    @Command(name = "demote",
            syntax = "<user> [ladder]",
            description = "Demotes <user> to previous group",
            isPrimary = true,
            permission = "permissions.user.rank.demote")
    public void demoteUserAlias(PermissionsEx plugin, CommandSender sender, Map<String, String> args) {
        this.demoteUser(plugin, sender, args);
    }
}
