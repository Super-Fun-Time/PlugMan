NON-functional
Would need to push some code unstream for allowing to unload plugins in addition to pusing just one in PluginManager


This version is for bungeecord, it just has load/unload and is very rough, but may work.



<h1 align="center">PlugMan is no longer actively maintained.</h1>

# PlugMan

PlugMan is a simple, easy to use plugin that lets server admins manage plugins from either in-game or console without the need to restart the server.

## Features
* Enable, disable, restart, load, reload, and unload plugins from in-game or console.
* List plugins alphabetically, with version if specified.
* Get useful information on plugins such as commands, version, author(s), etc.
* Easily manage plugins without having to constantly restart your server.
* List commands a plugin has registered.
* Find the plugin a command is registered to.
* Tab completion for command names and plugin names.
* Dump plugin list with versions to a file.
* Check if a plugin is up-to-date with dev.bukkit.org
* Permissions Support - All commands default to OP.

## Commands
| Command | Description |
| --------------- | ---------------- |
| /plugman load [plugin] | Load a plugin. |
| /plugman unload [plugin] | Unload a plugin. |

## Permissions
| Permission Node | Default | Description |
| ------------------------- | ---------- | ---------------- |
| plugman.load | OP | Allow use of the load command. |
| plugman.unload | OP | Allow use of the unload command. |

## Configuration
| File | URL |
| ----- | ------- |
| config.yml | https://github.com/r-clancy/PlugMan/blob/master/src/main/resources/config.yml |

## Sponsors

<div style="text-align:center" markdown="1">

![image](https://raw.githubusercontent.com/r-clancy/PlugMan/master/images/jetbrains_logo.png "JetBrains")

JetBrains is kindly supporting the PlugMan open source project with it's full-featured Java IDE.

Take a look a JetBrain's leading software products over on <a href="http://www.jetbrains.com/">their website.</a>

---

![image](https://raw.githubusercontent.com/r-clancy/PlugMan/master/images/yourkit_logo.png "YourKit")

YourKit is kindly supporting the PlugMan open source project with its full-featured Java Profiler.

YourKit, LLC is the creator of innovative and intelligent tools for profiling Java and .NET applications.

Take a look at YourKit's leading software products: <a href="http://www.yourkit.com/java/profiler/index.jsp">YourKit Java Profiler</a> and <a href="http://www.yourkit.com/.net/profiler/index.jsp">YourKit .NET Profiler</a>.

---

![image](https://raw.githubusercontent.com/r-clancy/PlugMan/master/images/intreppid_logo.png "Intreppid Logo")

Intreppid is kindly supporting the PlugMan open source project with hosting for the Jenkins build server along with a test platform for development.

Take a look at Intreppid's premium Minecraft server and dedicated servers on <a href="https://www.intreppid.com/">their website</a>.
