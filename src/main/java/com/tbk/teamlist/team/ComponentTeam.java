package com.tbk.teamlist.team;

import com.google.gson.JsonObject;
import com.tbk.teamlist.TeamList;
import net.minecraft.util.Identifier;

public class ComponentTeam {
    public static final ComponentTeam NONE = new ComponentTeam("none","none");
    public String name;
    public String id;
    public ComponentTeam(String name,String id){
        this.name=name;
        this.id=id;
    }
    public ComponentTeam(String name){
        this.name=name;
        this.id=TeamList.MOD_ID;
    }
    public ComponentTeam(JsonObject object){
        String[] id=object.get("icon").getAsString().split(":");
        this.name=id[1];
        this.id=id[0];
    }


    public Identifier getLocation(){
        return Identifier.of(this.id,"textures/teams/icons/"+this.name+".png");
    }
    public String getId(){
        return this.id+":"+this.name;
    }
}
