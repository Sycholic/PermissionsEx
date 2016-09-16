package ru.tehkode.permissions;

import ru.tehkode.permissions.events.PermissionEvent;

import java.util.UUID;

/**
 * Various mappings for UUID handling
 */
public interface NativeInterface {

    /**
     * Converts a uuid to a name. Preferably as minimally blocking as possible.
     *
     * @param uid The UUID to convert to a name
     * @return The name associated with a UUID, or null if unknown
     */
    String UUIDToName(UUID uid);

    /**
     * Returns the UUID associated with a name.
     *
     * @param name The name to convert
     * @return The UUID associated with this name, or null if unknown
     */
    UUID nameToUUID(String name);

    /**
     * Returns whether the player associated with this uuid is online
     *
     * @param uuid The uuid to check
     * @return Whether this uuid is online
     */
    boolean isOnline(UUID uuid);

    /**
     * Return the UUID associated with this server Mostly used internally
     *
     * @return This server's uuid
     */
    UUID getServerUUID();

    /**
     * Calls an event on the server
     *
     * @param event The event to call
     */
    void callEvent(PermissionEvent event);
}
