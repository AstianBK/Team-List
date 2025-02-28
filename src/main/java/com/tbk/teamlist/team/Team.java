package com.tbk.teamlist.team;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.tbk.teamlist.TeamList;

import java.util.HashSet;
import java.util.Set;

public class Team{
    public int color = -1;
    public String name = "";
    public Set<String> players;
    public ComponentTeam icon;
    public Team(String name ,int color){
        this.name=name;
        this.color=color;
        this.players=new HashSet<>();
        this.icon = ComponentTeam.NONE;
    }
    public Team(JsonObject object,String name){
        this.name=name;
        this.color=object.get("color").getAsInt();
        Set<String> set = new HashSet<>();
        this.icon=new ComponentTeam(object);
        JsonArray array = object.getAsJsonArray("players");
        for (int i = 0 ; i<array.size() ; i++){
            TeamList.LOGGER.debug("Se agrego el jugador "+array.get(i).getAsString());
            set.add(array.get(i).getAsString());
        }

        this.players = set;
    }
    public JsonObject save(JsonObject object) {
        object.addProperty("color",this.color);
        JsonArray list = new JsonArray();
        players.forEach(list::add);
        object.add("players",list);
        object.addProperty("icon",this.icon.getId());
        return object;
    }



}
