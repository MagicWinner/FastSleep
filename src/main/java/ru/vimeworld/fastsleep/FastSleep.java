package ru.vimeworld.fastsleep;

import net.minecraft.server.v1_14_R1.IChatBaseComponent;
import net.minecraft.server.v1_14_R1.Packet;
import net.minecraft.server.v1_14_R1.PacketPlayOutChat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;


public final class FastSleep extends JavaPlugin implements Listener, CommandExecutor {
    private static FastSleep instance;
    private static BukkitTask task;

    @Override
    public void onLoad() {
        FastSleep.instance = this;
    }

    @Override
    public void onEnable() {
        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[FastSleep] Плагин запущен | Версия " + getDescription().getVersion());
        Bukkit.getPluginManager().registerEvents(this, this);
        getCommand("wakeup").setExecutor(this);
    }

    @Override
    public void onDisable() {
        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[FastSleep] Плагин выключен | Версия " + getDescription().getVersion());
    }

    @EventHandler
    public void onSleep(PlayerBedEnterEvent e) {
        Player sleeper = e.getPlayer();
        IChatBaseComponent component = IChatBaseComponent.ChatSerializer.a("[\"\",{\"text\":\"" +
                ChatColor.WHITE
                + "Player "
                + ChatColor.GREEN
                + sleeper.getName()
                + ChatColor.WHITE +
                " were sleeping now. You may choose: " +
                "\",\"color\":\"black\"}," +
                "{\"text\":\"[Разбудить]\",\"color\":\"yellow\",\"clickEvent\":" +
                "{\"action\":\"run_command\",\"value\":\"/wakeup\"}}]");
        Packet packet = new PacketPlayOutChat(component);
        if (e.getBedEnterResult() == PlayerBedEnterEvent.BedEnterResult.OK) {
            for (Player players : Bukkit.getServer().getOnlinePlayers()) {
                ((CraftPlayer) players).getHandle().playerConnection.sendPacket(packet);
            }
                task = Bukkit.getScheduler().runTaskLater(FastSleep.instance(), () -> {
                    sleeper.getWorld().setTime(0);
                    sleeper.getWorld().setThundering(false);
                    sleeper.getWorld().setStorm(false);
                }, 20 * 5);
            }
        }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] strings) {
        if (cmd.getName().equalsIgnoreCase("wakeup")) {
            if (sender instanceof Player) {
                for (Player sleeps : Bukkit.getServer().getOnlinePlayers()) {
                    if (sleeps.isSleepingIgnored() || sleeps.isSleeping()) {
                        if (!task.isCancelled()) {
                            task.cancel();
                            sleeps.wakeup(true);
                        }
                    }
                }
            }


        }
        return false;
    }

    private static FastSleep instance() {
        return instance;
    }
}
