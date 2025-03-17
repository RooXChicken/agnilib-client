package com.rooxchicken.agnilib.data;

import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Scanner;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.rooxchicken.agnilib.AgniLib;
import com.rooxchicken.agnilib.AgniLibClient;
import com.rooxchicken.agnilib.event.KeybindCallback;

import net.minecraft.client.util.InputUtil;

public class AgniLibSettings
{
    private static final String saveFile = "agnilib.cfg";
    private static JsonObject keybinds;

    public static void save(boolean _combine)
    {
        try
        {
            File settingsFile = new File(saveFile);
            if(settingsFile.createNewFile())
                AgniLib.LOGGER.info("Created new AgniLib configuration file.");
            
            FileWriter writer = new FileWriter(settingsFile);
            writer.write(gatherAllSettings(_combine).toString());
            
            writer.close();
        }
        catch(Exception e)
        {
            AgniLib.LOGGER.error("Failed to save config file!", e);
        }
    }

    private static JsonObject gatherAllSettings(boolean _combine)
    {
        Gson gson = new Gson();
		JsonObject file = new JsonObject();
		
        file.addProperty("AGNILIB_CONFIG_VERSION", AgniLibClient.AgniLib_VERSION);
        
        HashMap<String, Object> _keybinds = KeybindCallback.saveSettings();

		file.addProperty("keybinds", gson.toJson(_keybinds));
		
		return file;
    }

    public static void load()
    {
        try
        {
            File _file = new File(saveFile);
            if(_file.createNewFile())
                save(false);

            Scanner scanner = new Scanner(_file);
            Gson gson = new Gson();
            JsonObject file = new Gson().fromJson(scanner.nextLine(), JsonObject.class);
    
            keybinds = gson.fromJson(gson.fromJson(file.get("keybinds"), JsonPrimitive.class).getAsString(), JsonObject.class);
    
            scanner.close();

        }
        catch(Exception e)
        {
            AgniLib.LOGGER.error("Failed to read AgniLib settings file!", e);
        }
    }

    public static int getKeybind(String _category, String _translation)
    {
        String _check = _category + "~" + _translation;
        if(keybinds == null || !keybinds.has(_check))
            return InputUtil.UNKNOWN_KEY.getCode();

        return keybinds.get(_check).getAsInt();
    }
}
