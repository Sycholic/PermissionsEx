package ru.tehkode.permissions;

public interface PermissionMatcher {

    boolean isMatches(String expression, String permission);

}
