package me.unariginal.dexrewards.placeholders.interfaces;

import me.unariginal.dexrewards.config.DexTypeConfig;
import me.unariginal.dexrewards.placeholders.GenericResult;

import java.util.List;

public interface DexTypePlaceholder {
    GenericResult handle(DexTypeConfig dexTypeConfig, List<String> args);
    List<String> id();
}
