package me.itsjbey.lookteleport;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.BlockIterator;

import java.util.HashMap;

public class EVENTS implements Listener {

	Main main = Main.getInstance();

	HashMap<Player, BukkitTask> runningTeleports = new HashMap<>();

	HashMap<Player, BukkitTask> runningParticles = new HashMap<>();

	HashMap<Player, Long> cooldown = new HashMap<>();

	@EventHandler
	public void onInteract(PlayerInteractEvent e) {

		if(e.getPlayer().hasPermission(Main.PERMISSION) && e.getPlayer().isSneaking()) {

			if(e.getAction() != Action.LEFT_CLICK_AIR && e.getAction() != Action.LEFT_CLICK_BLOCK)
				return;

			if(!e.getPlayer().hasPermission(Main.PERMISSION_BYPASS)) {

				if(cooldown.containsKey(e.getPlayer()) && System.currentTimeMillis() - cooldown.get(e.getPlayer()) < Main.COOLDOWN * 1000) {

					e.getPlayer().sendMessage("§cPlease wait " + Math.round((Main.COOLDOWN * 1000 - (System.currentTimeMillis() - cooldown.get(e.getPlayer()))) / 100d) / 10d + " seconds longer.");

					return;

				}

			}

			BlockIterator blockIterator = new BlockIterator(e.getPlayer(), Main.MAX_DISTANCE);

			while(blockIterator.hasNext()) {

				Block b = blockIterator.next();

				if(b.getType() != Material.AIR) {

					if(b.getLocation().getWorld().getBlockAt(b.getLocation().clone().add(0, 1, 0)).getType() == Material.AIR) {

						reset(e.getPlayer());

						startNewTask(e.getPlayer(), b.getLocation());

						return;

					}

				}

			}

			e.getPlayer().sendMessage("§cNo block in front of you was found.");

		}

	}

	private void reset(Player p) {

		if(runningParticles.containsKey(p))
			runningParticles.get(p).cancel();
		if(runningTeleports.containsKey(p))
			runningTeleports.get(p).cancel();

	}

	@EventHandler
	public void onMoveEvent(PlayerMoveEvent e) {

		if(runningTeleports.containsKey(e.getPlayer()) && !runningTeleports.get(e.getPlayer()).isCancelled()) {

			e.getPlayer().sendMessage("§cYou moved. The telport got cancelled.");

			runningTeleports.get(e.getPlayer()).cancel();

		}

		if(runningParticles.containsKey(e.getPlayer()) && !runningParticles.get(e.getPlayer()).isCancelled()) {

			runningParticles.get(e.getPlayer()).cancel();

		}

	}

	private void startNewTask(Player p, Location l) {

		l.add(0.5, 1, 0.5);

		runningParticles.put(p, new BukkitRunnable() {

			int x = 0;

			@Override
			public void run() {

				l.clone().add(Math.sin(x), Math.sin(x), Math.cos(x)).getWorld().spawnParticle(Particle.PORTAL, l, 10, 0.1, 0.1, 0.1);

				x += 0.1;

			}

		}.runTaskTimer(main, 0, 2));

		runningTeleports.put(p, new BukkitRunnable() {

			@Override
			public void run() {

				p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 3 * 20, 1, false, false));

				p.teleport(l);

				runningParticles.get(p).cancel();

				runningParticles.remove(p);
				runningTeleports.remove(p);

				cooldown.put(p, System.currentTimeMillis());

			}

		}.runTaskLater(main, Main.TELEPORT_DELAY * 20));

	}

}
