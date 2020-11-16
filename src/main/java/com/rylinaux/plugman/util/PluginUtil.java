package com.rylinaux.plugman.util;

/*
 * #%L
 * PlugMan
 * %%
 * Copyright (C) 2010 - 2014 PlugMan
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */

import com.google.common.base.Joiner;
import com.google.common.util.concurrent.ExecutionError;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URLClassLoader;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.rylinaux.plugman.PlugMan;
import com.rylinaux.plugman.bungee.BungeeLib;

import org.yaml.snakeyaml.Yaml;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Event;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginDescription;
import net.md_5.bungee.api.plugin.PluginManager;

/**
 * Utilities for managing plugins.
 *
 * @author rylinaux
 */
public class PluginUtil {

    /**
     * Returns a plugin from an array of Strings.
     *
     * @param args  the array
     * @param start the index to start at
     * @return the plugin
     */
    public static Plugin getPluginByName(String[] args, int start) {
        return getPluginByName(StringUtil.consolidateStrings(args, start));
    }

    /**
     * Returns a plugin from a String.
     *
     * @param name the name of the plugin
     * @return the plugin
     */
    public static Plugin getPluginByName(String name) {
        return ProxyServer.getInstance().getPluginManager().getPlugin(name);
    }

    /**
     * Returns a List of plugin names.
     *
     * @return list of plugin names
     */
    public static List<String> getPluginNames() {
        List<String> plugins = new ArrayList<>();
        for (Plugin plugin : ProxyServer.getInstance().getPluginManager().getPlugins()) {
            plugins.add(plugin.getDescription().getName());
        }
        return plugins;
    }

    /**
     * Get the version of another plugin.
     *
     * @param name the name of the other plugin.
     * @return the version.
     */
    public static String getPluginVersion(String name) {
        Plugin plugin = getPluginByName(name);
        if (plugin != null && plugin.getDescription() != null) {
            return plugin.getDescription().getVersion();
        }
        return null;
    }

    /**
     * Loads and enables a plugin.
     *
     * @param plugin plugin to load
     * @return status message
     */
    private static String load(Plugin plugin) {
        return load(plugin.getDescription().getName());
    }

    /**
     * Loads and enables a plugin.
     *
     * @param name plugin's name
     * @return status message
     */
    public static String load(String name) {

        Plugin target = null;

        File pluginDir = new File("plugins");

        if (!pluginDir.isDirectory()) {
            return "load.plugin-directory";
        }

        File pluginFile = new File(pluginDir, name + ".jar");
        String out = BungeeLib.loadJar(pluginFile);
        if(!out.equals("YES")){
            return out;
        }

        target=getPluginByName(name);

        target.onLoad();

        return "load.loaded"+ target.getDescription().getName();

    }

    /**
     * Reload a plugin.
     *
     * @param plugin the plugin to reload
     */
    public static void reload(Plugin plugin) {
        if (plugin != null) {
            unload(plugin);
            load(plugin);
        }
    }

    /**
     * Reload all plugins.
     */
    public static void reloadAll() {
        for (Plugin plugin : ProxyServer.getInstance().getPluginManager().getPlugins()) {
            reload(plugin);
        }
    }

    /**
     * Unload a plugin.
     *
     * @param plugin the plugin to unload
     * @return the message to send to the user.
     */
    public static String unload(Plugin plugin) {

        String name = plugin.getDescription().getName();

        PluginManager pluginManager = ProxyServer.getInstance().getPluginManager();

        if (pluginManager != null) {

            // Call the disable function
            try {
                Class.forName(plugin.getDescription().getMain()).getMethod("onDisable").invoke(null);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
                    | NoSuchMethodException | SecurityException | ClassNotFoundException e1) {
                e1.printStackTrace();
            }

        }

        pluginManager.unregisterListeners(plugin);

        pluginManager.unregisterCommands(plugin);


        // Attempt to close the classloader to unlock any handles on the plugin's jar file.
        ClassLoader cl = plugin.getClass().getClassLoader();

        if (cl instanceof URLClassLoader) {

            try {

                Field pluginField = cl.getClass().getDeclaredField("plugin");
                pluginField.setAccessible(true);
                pluginField.set(cl, null);

                Field pluginInitField = cl.getClass().getDeclaredField("pluginInit");
                pluginInitField.setAccessible(true);
                pluginInitField.set(cl, null);

            } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException ex) {
                Logger.getLogger(PluginUtil.class.getName()).log(Level.SEVERE, null, ex);
            }

            try {

                ((URLClassLoader) cl).close();
            } catch (IOException ex) {
                Logger.getLogger(PluginUtil.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        System.gc();

        return "unload.unloaded "+name;

    }
}
