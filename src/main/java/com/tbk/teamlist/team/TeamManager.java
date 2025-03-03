package com.tbk.teamlist.team;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.moandjiezana.toml.Toml;
import com.tbk.teamlist.TeamList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Environment(EnvType.CLIENT)
public class TeamManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File FILE = new File("config/teamlists_teams.json");
    private static final File CUSTOM_FILE = new File("world/datapacks/registry_icons.toml");
    public static Map<String,Team> teams = new HashMap<>();
    public TeamManager(){
        teams = new HashMap<>();
    }

    public static boolean isTeamPresent(String team){
        return teams.containsKey(team);
    }

    public static boolean isPresentPlayer(String team,String name){
        return teams.get(team).players.contains(name);
    }
    public static boolean isColor(String color){
        return Color.fromName(color)!=null;
    }
    public static boolean isIcon(String icon){
        return TLIconsRegistry.mapIcons.containsKey(icon.split(":")[1]);
    }
    public static Team getTeam(String name){
        for (Team team : teams.values()){
            for (String s : team.players){
                if(name.equals(s)){
                    return team;
                }
            }
        }
        return null;
    }
    public static void addPlayer(String team,String player){
        teams.get(team).players.add(player);
        TeamManager.save();
    }
    public static void removePlayer(String team,String player){
        teams.get(team).players.remove(player);
        TeamManager.save();

    }
    public static void createTeam(String team){
        teams.put(team,new Team(team,Color.WHITE.rgb));
        TeamManager.save();

    }
    public static void createTeam(String team ,String color){
        teams.put(team,new Team(team,Color.fromName(color).getRGB()));
        TeamManager.save();
    }
    public static void deleteTeam(String team){
        teams.remove(team);
        TeamManager.save();
    }

    public static void modifyColor(String team,String color){
        teams.get(team).color=Color.fromName(color).getRGB();
        TeamManager.save();
    }

    public static void modifyIcon(String team,String icon){
        teams.get(team).icon = TLIconsRegistry.mapIcons.get(icon.split(":")[1]);
        TeamManager.save();
    }

    public static void removeIcon(String team){
        teams.get(team).icon = ComponentTeam.NONE;
        TeamManager.save();
    }

    public static void save() {
        try (FileWriter writer = new FileWriter(FILE)) {
            JsonObject json = new JsonObject();
            for (Map.Entry<String, Team> entry : teams.entrySet()) {
                JsonObject object = new JsonObject();
                json.add(entry.getKey(),entry.getValue().save(object));
            }
            GSON.toJson(json, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void load() {
        if (!FILE.exists()) return;
        try (FileReader reader = new FileReader(FILE)) {
            JsonObject json = GSON.fromJson(reader, JsonObject.class);
            teams.clear();
            for (String name : json.keySet()) {
                JsonObject playerArray = json.getAsJsonObject(name);
                Team team = new Team(playerArray,name);

                teams.put(name, team);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadIconCustom() {
        if (!CUSTOM_FILE.exists()) return;

        try {
            // Cargar el archivo TOML
            Toml toml = new Toml().read(CUSTOM_FILE);

            // Obtener la lista de iconos desde el archivo TOML
            List<String> icons = toml.getList("icons");

            // Iterar sobre los iconos y procesarlos
            for (String name : icons) {
                String id = name.split(":")[0];
                String icon = name.split(":")[1];
                registerTexture(icon);
                TLIconsRegistry.register(icon, new ComponentTeam(icon, id));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Identifier registerTexture(String name) {
        BufferedImage image = loadPNG(name);
        if(image!=null){
            NativeImage nativeImage = convertToNative(image);
            AbstractTexture texture = new NativeImageBackedTexture(nativeImage);

            Identifier identifier = Identifier.of(TeamList.MOD_ID, "textures/"+name+"png");
            MinecraftClient.getInstance().getTextureManager().registerTexture(identifier, texture);
            return identifier;
        }

        return null;
    }

    public static NativeImage convertToNative(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        NativeImage nativeImage = new NativeImage(width, height, true);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int p = image.getRGB(x, y);

                int a = (p >> 24) & 0xFF;
                int r = (p >> 16) & 0xFF;
                int g = (p >> 8) & 0xFF;
                int b = (p) & 0xFF;

                // Reordenar a RGBA
                int fixedColor = (a << 24) | (b << 16) | (g << 8) | r;
                nativeImage.setColor(x, y, fixedColor);
            }
        }

        return nativeImage;
    }
    public static BufferedImage loadPNG(String filename) {
        File file = new File("world/datapacks/custom/data/team-list/icons/" + filename+".png");
        if (!file.exists()) {
            System.out.println("No se encontró el archivo: " + file.getAbsolutePath());
            return null;
        }

        try {
            BufferedImage rawImage = ImageIO.read(file);
            BufferedImage image = new BufferedImage(rawImage.getWidth(), rawImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = image.createGraphics();
            g.drawImage(rawImage, 0, 0, null);
            g.dispose();
            return image;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public enum Color{
        WHITE(-1),                      // 0xFFFFFFFF (correcto)
        BLACK(-16777216),               // 0xFF000000 (correcto)
        GRAY(-8355712),                 // 0xFF808080 (correcto)
        LIGHT_GRAY(-6250336),           // 0xFFA0A0A0 (correcto)
        ALTERNATE_WHITE(-4539718),      // 0xFFBABABA (correcto)
        RED(-65536),                    // 0xFFFF0000 (correcto)
        GREEN(-16711936),               // 0xFF00FF00 (correcto)
        BLUE(-16776961),                // 0xFF0000FF (correcto)
        LIGHT_RED(-2142128),            // 0xFFDF5050 (corregido, ahora es rojo claro)
        YELLOW(-256),                   // 0xFFFFFF00 (correcto)
        LIGHT_YELLOW(-171),             // 0xFFFFFF55 (correcto)

        // Colores corregidos
        ORANGE(-29696),        // 0xFFFF8C00 (Dark Orange)
        PURPLE(-9162624),      // 0xFF7D007D (Dark Purple)
        CYAN(-16741493),       // 0xFF008B8B (Dark Cyan)
        MAGENTA(-7667573),     // 0xFF8B008B (Dark Magenta)
        BROWN(6052956),        // 0xFF5A2E0E (Darker Brown)
        PINK(-16181),        // 0xFF8B5A65 (Deep Pink)
        DARK_GREEN(-16751616), // 0xFF004B23 (Darker Green)
        DARK_BLUE(-16777113),  // 0xFF000074 (Deeper Dark Blue)
        GOLD(-23808),          // 0xFFCCAC00 (Darker Gold)
        SILVER(-6250336),      // 0xFF979797 (Darker Silver)
        NAVY(-16777216),       // 0xFF000040 (Deep Navy)
        TEAL(-16748608);       // 0xFF006060 (Darker Teal)

        private final int rgb;

        Color(int rgb) {
            this.rgb = rgb;
        }

        public int getRGB() {
            return rgb;
        }
        public static List<String> getNames() {
            return Arrays.stream(Color.values())
                    .map(Enum::name)
                    .collect(Collectors.toList());
        }

        public static Color fromName(String name) {
            try {
                return Color.valueOf(name.toUpperCase());
            } catch (IllegalArgumentException e) {
                return null; // O puedes lanzar una excepción si prefieres manejarlo de otra manera
            }
        }
    }
}
