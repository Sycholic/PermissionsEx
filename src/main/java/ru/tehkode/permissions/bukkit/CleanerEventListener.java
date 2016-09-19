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

package ru.tehkode.permissions.bukkit;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import ru.tehkode.permissions.PermissionUser;


public class CleanerEventListener implements Listener {

    private final PermissionsEx cleanerEvent;

    public CleanerEventListener(final PermissionsEx cleanerEvent) {
        this.cleanerEvent = cleanerEvent;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        if (event.getLoginResult() == AsyncPlayerPreLoginEvent.Result.ALLOWED && !cleanerEvent.requiresLateUserSetup()) {
            cleanerEvent.getPermissionsManager().cacheUser(event.getUniqueId().toString(), event.getName());
        }
    }

    @EventHandler
    public void onPlayerLogin(PlayerJoinEvent event) {
        try {
            PermissionUser user = cleanerEvent.getPermissionsManager().getUser(event.getPlayer());
            if (!user.isVirtual()) {
                if (!event.getPlayer().getName().equals(user.getOption("name"))) {
                    // Update name only if user exists in config
                    user.setOption("name", event.getPlayer().getName());
                }
                if (!cleanerEvent.config.shouldLogPlayers()) {
                    return;
                }
                user.setOption("last-login-time", Long.toString(System.currentTimeMillis() / 1000L));
                // user.setOption("last-login-ip", event.getPlayer().getAddress().getAddress().getHostAddress()); // somehow this won't work
            }
        } catch (Throwable t) {
            ErrorReport.handleError("While login cleanup event", t);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        try {
            PermissionUser user = cleanerEvent.getPermissionsManager().getUser(event.getPlayer());
            if (!user.isVirtual()) {
                if (cleanerEvent.config.shouldLogPlayers()) {
                    user.setOption("last-logout-time", Long.toString(System.currentTimeMillis() / 1000L));
                }
                user.getName(); // Set name if user was created during server run
            }
            cleanerEvent.getPermissionsManager().resetUser(event.getPlayer());
        } catch (Throwable t) {
            ErrorReport.handleError("While logout cleanup event", t);
        }
    }
}
