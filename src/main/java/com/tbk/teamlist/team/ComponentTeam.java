package com.tbk.teamlist.team;

import com.google.gson.JsonObject;
import com.tbk.teamlist.TeamList;
import net.minecraft.util.Identifier;

public class ComponentTeam {
    public static final ComponentTeam NONE = new ComponentTeam("none","none",false);
    public String name;
    public String id;
    public boolean isCustom;
    public ComponentTeam(String name,String id,boolean isCustom){
        this.name=name;
        this.id=id;
        this.isCustom=isCustom;
    }
    public ComponentTeam(String name,String id){
        this.name=name;
        this.id=id;
        this.isCustom=true;
    }
    public ComponentTeam(String name){
        this.name=name;
        this.id=TeamList.MOD_ID;
        this.isCustom=false;
    }
    public ComponentTeam(JsonObject object){
        String[] id=object.get("icon").getAsString().split(":");
        this.isCustom = id[0].equals("custom");
        this.name=id[1];
        this.id=id[0];
    }


    public Identifier getLocation(){
        if(this.isCustom && TeamManager.registerTexture(name)!=null){
            return TeamManager.registerTexture(name);
        }
        return Identifier.of(this.id,"textures/teams/icons/"+this.name+".png");
    }

    public String getId(){
        return this.id+":"+this.name;
    }
}
