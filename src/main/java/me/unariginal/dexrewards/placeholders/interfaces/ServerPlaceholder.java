package me.unariginal.dexrewards.placeholders.interfaces;

import me.unariginal.dexrewards.placeholders.GenericResult;

import java.util.List;

public interface ServerPlaceholder {
    GenericResult handle(List<String> args);
    List<String> id();
}
