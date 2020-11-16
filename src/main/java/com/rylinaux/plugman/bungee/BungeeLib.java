package com.rylinaux.plugman.bungee;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.introspector.PropertyUtils;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginDescription;

public class BungeeLib {
    public static String loadJar(File pluginFile){
        try ( JarFile jar = new JarFile( pluginFile ) )
        {
            JarEntry pdf = jar.getJarEntry( "bungee.yml" );
            if ( pdf == null )
            {
                pdf = jar.getJarEntry( "plugin.yml" );
            }
            try ( InputStream in = jar.getInputStream( pdf ) )
            {
                Constructor yamlConstructor = new Constructor();
                PropertyUtils propertyUtils = yamlConstructor.getPropertyUtils();
                propertyUtils.setSkipMissingProperties( true );
                yamlConstructor.setPropertyUtils( propertyUtils );
                PluginDescription desc = new Yaml(yamlConstructor).loadAs( in, PluginDescription.class );
                if(!BungeeLib.enablePlugin(new HashMap<PluginDescription,Boolean>(), new Stack<PluginDescription>(), desc)){
                    return "FAILED TO LOAD";
                }
            }
        } catch (Exception e){
            e.printStackTrace();
            return "FAILED TO GET DESCRIPTION OR LOAD";
        }
        return "YES";
    }
    public static boolean enablePlugin(Map<PluginDescription, Boolean> pluginStatuses, Stack<PluginDescription> dependStack, PluginDescription plugin)
    {
        if ( pluginStatuses.containsKey( plugin ) )
        {
            return pluginStatuses.get( plugin );
        }

        // combine all dependencies for 'for loop'
        Set<String> dependencies = new HashSet<>();
        dependencies.addAll( plugin.getDepends() );
        dependencies.addAll( plugin.getSoftDepends() );

        // success status
        boolean status = true;

        // dont bother with depends

        // do actual loading
        if ( status )
        {
            try
            {
                URLClassLoader loader = new URLClassLoader(new URL[]{plugin.getFile().toURI().toURL()} );
                Class<?> main = loader.loadClass( plugin.getMain() );
                Plugin clazz = (Plugin) main.getDeclaredConstructor().newInstance();
                // Attempt to update the plugin list outside of the class, will likely fail.
                Field field = ProxyServer.class.getClass().getField("plugins");
                field.setAccessible(true);
                Map<String, Plugin> plugins = (Map<String, Plugin>) field.get(ProxyServer.getInstance().getPluginManager());
                plugins.put( plugin.getName(), clazz );
                field.set(ProxyServer.getInstance().getPluginManager(), plugins);
                clazz.onLoad();
                ProxyServer.getInstance().getLogger().log( Level.INFO, "Loaded plugin {0} version {1} by {2}", new Object[]
                {
                    plugin.getName(), plugin.getVersion(), plugin.getAuthor()
                } );
            } catch ( Throwable t )
            {
                ProxyServer.getInstance().getLogger().log( Level.WARNING, "Error enabling plugin " + plugin.getName(), t );
            }
        }

        pluginStatuses.put( plugin, status );
        return status;
    }
}
