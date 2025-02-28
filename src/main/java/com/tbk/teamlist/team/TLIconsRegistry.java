package com.tbk.teamlist.team;

import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

public class TLIconsRegistry {
    public static final Map<String, ComponentTeam> mapIcons = new HashMap<>();

    public static final ComponentTeam ICON_ANNOY = register("icon_annoy",new ComponentTeam("icon_annoy"));
    public static final ComponentTeam ICON_BLUELINES = register("icon_bluelines",new ComponentTeam("icon_bluelines"));
    public static final ComponentTeam ICON_BLUESQUARE = register("icon_bluesquare",new ComponentTeam("icon_bluesquare"));
    public static final ComponentTeam ICON_CHECK = register("icon_check",new ComponentTeam("icon_check"));

    public static final ComponentTeam ICON_CROSS = register("icon_cross",new ComponentTeam("icon_cross"));
    public static final ComponentTeam ICON_CROWN = register("icon_crown",new ComponentTeam("icon_crown"));
    public static final ComponentTeam ICON_EXCLAMATION = register("icon_exclamation",new ComponentTeam("icon_exclamation"));
    public static final ComponentTeam ICON_GREENLINES = register("icon_greenlines",new ComponentTeam("icon_greenlines"));

    public static final ComponentTeam ICON_GREENSQUARE = register("icon_greensquare",new ComponentTeam("icon_greensquare"));
    public static final ComponentTeam ICON_HAPPY = register("icon_happy",new ComponentTeam("icon_happy"));
    public static final ComponentTeam ICON_INTERROGATION = register("icon_interrogation",new ComponentTeam("icon_interrogation"));
    public static final ComponentTeam ICON_MAD = register("icon_mad",new ComponentTeam("icon_mad"));

    public static final ComponentTeam ICON_ABSTRACT1 = register("icon_abstract1",new ComponentTeam("icon_abstract1"));
    public static final ComponentTeam ICON_ABSTRACT2 = register("icon_abstract2",new ComponentTeam("icon_abstract2"));

    public static final ComponentTeam ICON_NEUTRAL = register("icon_neutral",new ComponentTeam("icon_neutral"));
    public static final ComponentTeam ICON_RAT = register("icon_rat",new ComponentTeam("icon_rat"));
    public static final ComponentTeam ICON_REDLINES = register("icon_redlines",new ComponentTeam("icon_redlines"));
    public static final ComponentTeam ICON_REDSQUARE = register("icon_redsquare",new ComponentTeam("icon_redsquare"));

    public static final ComponentTeam ICON_SKULL = register("icon_skull",new ComponentTeam("icon_skull"));
    public static final ComponentTeam ICON_SPY = register("icon_spy",new ComponentTeam("icon_spy"));
    public static final ComponentTeam ICON_TOAST = register("icon_toast",new ComponentTeam("icon_toast"));
    public static final ComponentTeam ICON_TOILET = register("icon_toilet",new ComponentTeam("icon_toilet"));

    public static ComponentTeam register(String name,ComponentTeam team){
        mapIcons.put(name,team);
        return team;
    }
}
