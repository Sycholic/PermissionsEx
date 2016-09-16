package ru.tehkode.permissions;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface PermissionsData {

    /**
     * Preload data from entity
     */
    void load();

    /**
     * Returns the current identifier of the user
     *
     * @return
     */
    String getIdentifier();

    /**
     * Returns all permissions for specified world
     *
     * @param worldName
     * @return
     */
    List<String> getPermissions(String worldName);

    /**
     * Set permissions for specified world
     *
     * @param permissions
     * @param worldName
     */
    void setPermissions(List<String> permissions, String worldName);

    /**
     * Returns ALL permissions for each world
     *
     * @return
     */
    Map<String, List<String>> getPermissionsMap();

    /**
     * Returns worlds where entity has permissions/options
     *
     * @return
     */
    Set<String> getWorlds();

    /**
     * Returns option value in specified worlds. null if option is not defined
     * in that world
     *
     * @param option
     * @param worldName
     * @return
     */
    String getOption(String option, String worldName);

    /**
     * Sets option value in specified world
     *
     * @param option
     * @param value
     * @param world
     */
    void setOption(String option, String value, String world);

    /**
     * Returns all options in specified world
     *
     * @param worldName
     * @return
     */
    Map<String, String> getOptions(String worldName);

    /**
     * Returns ALL options in each world
     *
     * @return
     */
    Map<String, Map<String, String>> getOptionsMap();

    /**
     * Return the parent groups of a user or group
     *
     * @param worldName World or null for common
     * @return Unmodifiable list of parents
     */
    List<String> getParents(String worldName);

    /**
     * Set parent groups of a user or group
     *
     * @param parents New list of parents
     * @param worldName World name or null for common
     */
    void setParents(List<String> parents, String worldName);

    /**
     * Returns true if this User/Group exists only in server memory
     *
     * @return
     */
    boolean isVirtual();

    /**
     * Commit data to backend
     */
    void save();

    /**
     * Completely remove data from backend
     */
    void remove();

    /**
     * Return map of parents for all worlds
     *
     * @return Parents for all worlds
     */
    Map<String, List<String>> getParentsMap();
}
