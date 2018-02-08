package me.badbones69.crazyenchantments.enchantments;

import me.badbones69.crazyenchantments.Methods;
import me.badbones69.crazyenchantments.api.CrazyEnchantments;
import me.badbones69.crazyenchantments.api.currencyapi.Currency;
import me.badbones69.crazyenchantments.api.currencyapi.CurrencyAPI;
import me.badbones69.crazyenchantments.api.enums.CEnchantments;
import me.badbones69.crazyenchantments.api.events.DisarmerUseEvent;
import me.badbones69.crazyenchantments.api.events.EnchantmentUseEvent;
import me.badbones69.crazyenchantments.api.events.RageBreakEvent;
import me.badbones69.crazyenchantments.api.objects.CEPlayer;
import me.badbones69.crazyenchantments.api.objects.FileManager.Files;
import me.badbones69.crazyenchantments.api.objects.ItemBuilder;
import me.badbones69.crazyenchantments.multisupport.SpartanSupport;
import me.badbones69.crazyenchantments.multisupport.Support;
import me.badbones69.crazyenchantments.multisupport.Support.SupportedPlugins;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class Swords implements Listener {
	
	private CrazyEnchantments ce = CrazyEnchantments.getInstance();
	
	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerDamage(EntityDamageByEntityEvent e) {
		if(!e.isCancelled()) {
			if(!Support.isFriendly(e.getDamager(), e.getEntity())) {
				if(ce.isBreakRageOnDamageOn()) {
					if(e.getEntity() instanceof Player) {
						CEPlayer player = ce.getCEPlayer((Player) e.getEntity());
						RageBreakEvent event = new RageBreakEvent(player.getPlayer(), e.getDamager(), Methods.getItemInHand(player.getPlayer()));
						Bukkit.getPluginManager().callEvent(event);
						if(!event.isCancelled()) {
							UUID uuid = e.getEntity().getUniqueId();
							if(player.hasRage()) {
								player.getRageTask().cancel();
								player.setRageMultiplyer(0.0);
								player.setRageLevel(0);
								player.setRage(false);
								if(Files.MESSAGES.getFile().contains("Messages.Rage.Damaged")) {
									if(Files.MESSAGES.getFile().getString("Messages.Rage.Damaged").length() > 0) {
										e.getEntity().sendMessage(Methods.color(Files.MESSAGES.getFile().getString("Messages.Rage.Damaged")));
									}
								}else {
									e.getEntity().sendMessage(Methods.color("&7[&c&lRage&7]: &cYou have been hurt and it broke your Rage Multiplier!"));
								}
							}
						}
					}
				}
				if(e.getEntity() instanceof LivingEntity) {
					if(e.getDamager() instanceof Player) {
						final Player damager = (Player) e.getDamager();
						CEPlayer cePlayer = ce.getCEPlayer(damager);
						LivingEntity en = (LivingEntity) e.getEntity();
						ItemStack item = Methods.getItemInHand(damager);
						if(!e.getEntity().isDead()) {
							if(ce.hasEnchantments(item)) {
								if(ce.hasEnchantment(item, CEnchantments.DISARMER)) {
									if(CEnchantments.DISARMER.isEnabled()) {
										if(e.getEntity() instanceof Player) {
											Player player = (Player) e.getEntity();
											int slot = Methods.percentPick(4, 1);
											if(CEnchantments.DISARMER.chanceSuccessful(item)) {
												if(slot == 1) {
													if(player.getEquipment().getHelmet() != null) {
														ItemStack armor = player.getEquipment().getHelmet();
														DisarmerUseEvent event = new DisarmerUseEvent(player, damager, armor);
														Bukkit.getPluginManager().callEvent(event);
														if(!event.isCancelled()) {
															player.getEquipment().setHelmet(null);
															player.getInventory().addItem(armor);
														}
													}
												}
												if(slot == 2) {
													if(player.getEquipment().getChestplate() != null) {
														ItemStack armor = player.getEquipment().getChestplate();
														DisarmerUseEvent event = new DisarmerUseEvent(player, damager, armor);
														Bukkit.getPluginManager().callEvent(event);
														if(!event.isCancelled()) {
															player.getEquipment().setChestplate(null);
															player.getInventory().addItem(armor);
														}
													}
												}
												if(slot == 3) {
													if(player.getEquipment().getLeggings() != null) {
														ItemStack armor = player.getEquipment().getLeggings();
														DisarmerUseEvent event = new DisarmerUseEvent(player, damager, armor);
														Bukkit.getPluginManager().callEvent(event);
														if(!event.isCancelled()) {
															player.getEquipment().setLeggings(null);
															player.getInventory().addItem(armor);
														}
													}
												}
												if(slot == 4) {
													if(player.getEquipment().getBoots() != null) {
														ItemStack armor = player.getEquipment().getBoots();
														DisarmerUseEvent event = new DisarmerUseEvent(player, damager, armor);
														Bukkit.getPluginManager().callEvent(event);
														if(!event.isCancelled()) {
															player.getEquipment().setBoots(null);
															player.getInventory().addItem(armor);
														}
													}
												}
											}
										}
									}
								}
								if(ce.hasEnchantment(item, CEnchantments.RAGE)) {
									if(CEnchantments.RAGE.isEnabled()) {
										EnchantmentUseEvent event = new EnchantmentUseEvent(damager, CEnchantments.RAGE, item);
										Bukkit.getPluginManager().callEvent(event);
										if(!event.isCancelled()) {
											if(cePlayer.hasRage()) {
												cePlayer.getRageTask().cancel();
												if(cePlayer.getRageMultiplyer() <= ce.getRageMaxLevel()) {
													cePlayer.setRageMultiplyer(cePlayer.getRageMultiplyer() + (ce.getPower(item, CEnchantments.RAGE) * 0.1));
												}
												int rageUp = cePlayer.getRageLevel() + 1;
												if(cePlayer.getRageMultiplyer().intValue() == rageUp) {
													if(Files.MESSAGES.getFile().getString("Messages.Rage.Rage-Up").length() > 0) {
														damager.sendMessage(Methods.color(Files.MESSAGES.getFile().getString("Messages.Rage.Rage-Up")
														.replaceAll("%Level%", rageUp + "").replaceAll("%level%", rageUp + "")));
													}
													cePlayer.setRageLevel(rageUp);
												}
												e.setDamage(e.getDamage() * cePlayer.getRageMultiplyer());
											}
											if(!cePlayer.hasRage()) {
												cePlayer.setRageMultiplyer(1.0);
												cePlayer.setRageLevel(1);
												if(Files.MESSAGES.getFile().getString("Messages.Rage.Building").length() > 0) {
													damager.sendMessage(Methods.color(Files.MESSAGES.getFile().getString("Messages.Rage.Building")));
												}
											}
											cePlayer.setRageTask(new BukkitRunnable() {
												@Override
												public void run() {
													cePlayer.setRageMultiplyer(0.0);
													cePlayer.setRage(false);
													cePlayer.setRageLevel(0);
													if(Files.MESSAGES.getFile().getString("Messages.Rage.Cooled-Down").length() > 0) {
														damager.sendMessage(Methods.color(Files.MESSAGES.getFile().getString("Messages.Rage.Cooled-Down")));
													}
												}
											}.runTaskLater(ce.getPlugin(), 80));
										}
									}
								}
								if(ce.hasEnchantment(item, CEnchantments.SKILLSWIPE)) {
									if(CEnchantments.SKILLSWIPE.isEnabled()) {
										if(en instanceof Player) {
											Player player = (Player) en;
											int amount = 4 + ce.getPower(item, CEnchantments.SKILLSWIPE);
											if(player.getTotalExperience() > 0) {
												EnchantmentUseEvent event = new EnchantmentUseEvent(damager, CEnchantments.SKILLSWIPE, item);
												Bukkit.getPluginManager().callEvent(event);
												if(!event.isCancelled()) {
													if(CurrencyAPI.getCurrency(player, Currency.XP_TOTAL) >= amount) {
														CurrencyAPI.takeCurrency(player, Currency.XP_TOTAL, amount);
														CurrencyAPI.giveCurrency(damager, Currency.XP_TOTAL, amount);
													}else {
														player.setTotalExperience(0);
														CurrencyAPI.giveCurrency(damager, Currency.XP_TOTAL, amount);
													}
												}
											}
										}
									}
								}
								if(ce.hasEnchantment(item, CEnchantments.LIFESTEAL)) {
									if(CEnchantments.LIFESTEAL.isEnabled()) {
										int steal = ce.getPower(item, CEnchantments.LIFESTEAL);
										if(CEnchantments.LIFESTEAL.chanceSuccessful(item)) {
											EnchantmentUseEvent event = new EnchantmentUseEvent(damager, CEnchantments.LIFESTEAL, item);
											Bukkit.getPluginManager().callEvent(event);
											if(!event.isCancelled()) {
												if(damager.getHealth() + steal < damager.getMaxHealth()) {
													damager.setHealth(damager.getHealth() + steal);
												}
												if(damager.getHealth() + steal >= damager.getMaxHealth()) {
													damager.setHealth(damager.getMaxHealth());
												}
											}
										}
									}
								}
								if(ce.hasEnchantment(item, CEnchantments.NUTRITION)) {
									if(CEnchantments.NUTRITION.isEnabled()) {
										if(CEnchantments.NUTRITION.chanceSuccessful(item)) {
											EnchantmentUseEvent event = new EnchantmentUseEvent(damager, CEnchantments.NUTRITION, item);
											Bukkit.getPluginManager().callEvent(event);
											if(!event.isCancelled()) {
												if(SupportedPlugins.SPARTAN.isPluginLoaded()) {
													SpartanSupport.cancelFastEat(damager);
												}
												if(damager.getSaturation() + (2 * ce.getPower(item, CEnchantments.NUTRITION)) <= 20) {
													damager.setSaturation(damager.getSaturation() + (2 * ce.getPower(item, CEnchantments.NUTRITION)));
												}
												if(damager.getSaturation() + (2 * ce.getPower(item, CEnchantments.NUTRITION)) >= 20) {
													damager.setSaturation(20);
												}
											}
										}
									}
								}
								if(ce.hasEnchantment(item, CEnchantments.VAMPIRE)) {
									if(CEnchantments.VAMPIRE.isEnabled()) {
										if(CEnchantments.VAMPIRE.chanceSuccessful(item)) {
											EnchantmentUseEvent event = new EnchantmentUseEvent(damager, CEnchantments.VAMPIRE, item);
											Bukkit.getPluginManager().callEvent(event);
											if(!event.isCancelled()) {
												if(damager.getHealth() + e.getDamage() / 2 < damager.getMaxHealth()) {
													damager.setHealth(damager.getHealth() + e.getDamage() / 2);
												}
												if(damager.getHealth() + e.getDamage() / 2 >= damager.getMaxHealth()) {
													damager.setHealth(damager.getMaxHealth());
												}
											}
										}
									}
								}
								if(ce.hasEnchantment(item, CEnchantments.BLINDNESS)) {
									if(CEnchantments.BLINDNESS.isEnabled()) {
										if(CEnchantments.BLINDNESS.chanceSuccessful(item)) {
											EnchantmentUseEvent event = new EnchantmentUseEvent(damager, CEnchantments.BLINDNESS, item);
											Bukkit.getPluginManager().callEvent(event);
											if(!event.isCancelled()) {
												en.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 3 * 20, ce.getPower(item, CEnchantments.BLINDNESS) - 1));
											}
										}
									}
								}
								if(ce.hasEnchantment(item, CEnchantments.CONFUSION)) {
									if(CEnchantments.CONFUSION.isEnabled()) {
										if(CEnchantments.CONFUSION.chanceSuccessful(item)) {
											EnchantmentUseEvent event = new EnchantmentUseEvent(damager, CEnchantments.CONFUSION, item);
											Bukkit.getPluginManager().callEvent(event);
											if(!event.isCancelled()) {
												en.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 5 + (ce.getPower(item, CEnchantments.CONFUSION)) * 20, 0));
											}
										}
									}
								}
								if(ce.hasEnchantment(item, CEnchantments.DOUBLEDAMAGE)) {
									if(CEnchantments.DOUBLEDAMAGE.isEnabled()) {
										if(CEnchantments.DOUBLEDAMAGE.chanceSuccessful(item)) {
											EnchantmentUseEvent event = new EnchantmentUseEvent(damager, CEnchantments.DOUBLEDAMAGE, item);
											Bukkit.getPluginManager().callEvent(event);
											if(!event.isCancelled()) {
												e.setDamage((e.getDamage() * 2));
											}
										}
									}
								}
								if(ce.hasEnchantment(item, CEnchantments.EXECUTE)) {
									if(CEnchantments.EXECUTE.isEnabled()) {
										if(en.getHealth() <= 2) {
											EnchantmentUseEvent event = new EnchantmentUseEvent(damager, CEnchantments.EXECUTE, item);
											Bukkit.getPluginManager().callEvent(event);
											if(!event.isCancelled()) {
												damager.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 3 + (ce.getPower(item, CEnchantments.EXECUTE)) * 20, 3));
											}
										}
									}
								}
								if(ce.hasEnchantment(item, CEnchantments.FASTTURN)) {
									if(CEnchantments.FASTTURN.isEnabled()) {
										if(CEnchantments.FASTTURN.chanceSuccessful(item)) {
											EnchantmentUseEvent event = new EnchantmentUseEvent(damager, CEnchantments.FASTTURN, item);
											Bukkit.getPluginManager().callEvent(event);
											if(!event.isCancelled()) {
												e.setDamage(e.getDamage() + (e.getDamage() / 3));
											}
										}
									}
								}
								if(ce.hasEnchantment(item, CEnchantments.LIGHTWEIGHT)) {
									if(CEnchantments.LIGHTWEIGHT.isEnabled()) {
										if(CEnchantments.LIGHTWEIGHT.chanceSuccessful(item)) {
											EnchantmentUseEvent event = new EnchantmentUseEvent(damager, CEnchantments.LIGHTWEIGHT, item);
											Bukkit.getPluginManager().callEvent(event);
											if(!event.isCancelled()) {
												damager.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 5 * 20, ce.getPower(item, CEnchantments.LIGHTWEIGHT) - 1));
											}
										}
									}
								}
								if(ce.hasEnchantment(item, CEnchantments.OBLITERATE)) {
									if(CEnchantments.OBLITERATE.isEnabled()) {
										if(CEnchantments.OBLITERATE.chanceSuccessful(item)) {
											EnchantmentUseEvent event = new EnchantmentUseEvent(damager, CEnchantments.OBLITERATE, item);
											Bukkit.getPluginManager().callEvent(event);
											if(!event.isCancelled()) {
												if(SupportedPlugins.SPARTAN.isPluginLoaded()) {
													if(e.getEntity() instanceof Player) {
														SpartanSupport.cancelSpeed((Player) e.getEntity());
														SpartanSupport.cancelFly((Player) e.getEntity());
														SpartanSupport.cancelClip((Player) e.getEntity());
														SpartanSupport.cancelNormalMovements((Player) e.getEntity());
														SpartanSupport.cancelNoFall((Player) e.getEntity());
														SpartanSupport.cancelJesus((Player) e.getEntity());
													}
												}
												e.getEntity().setVelocity(damager.getLocation().getDirection().multiply(2).setY(1.25));
											}
										}
									}
								}
								if(ce.hasEnchantment(item, CEnchantments.PARALYZE)) {
									if(CEnchantments.PARALYZE.isEnabled()) {
										if(CEnchantments.PARALYZE.chanceSuccessful(item)) {
											EnchantmentUseEvent event = new EnchantmentUseEvent(damager, CEnchantments.PARALYZE, item);
											Bukkit.getPluginManager().callEvent(event);
											if(!event.isCancelled()) {
												Location loc = en.getLocation();
												loc.getWorld().strikeLightningEffect(loc);
												for(LivingEntity En : Methods.getNearbyLivingEntities(loc, 2D, damager)) {
													if(Support.allowsPVP(En.getLocation())) {
														if(!Support.isFriendly(damager, En)) {
															En.damage(5D);
														}
													}
												}
												en.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 3 * 20, 2));
												en.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 3 * 20, 2));
											}
										}
									}
								}
								if(ce.hasEnchantment(item, CEnchantments.SLOWMO)) {
									if(CEnchantments.SLOWMO.isEnabled()) {
										if(CEnchantments.SLOWMO.chanceSuccessful(item)) {
											EnchantmentUseEvent event = new EnchantmentUseEvent(damager, CEnchantments.SLOWMO, item);
											Bukkit.getPluginManager().callEvent(event);
											if(!event.isCancelled()) {
												en.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 3 * 20, ce.getPower(item, CEnchantments.SLOWMO)));
											}
										}
									}
								}
								if(ce.hasEnchantment(item, CEnchantments.SNARE)) {
									if(CEnchantments.SNARE.isEnabled()) {
										if(CEnchantments.SNARE.chanceSuccessful(item)) {
											EnchantmentUseEvent event = new EnchantmentUseEvent(damager, CEnchantments.SNARE, item);
											Bukkit.getPluginManager().callEvent(event);
											if(!event.isCancelled()) {
												en.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 3 * 20, 0));
												en.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 3 * 20, 0));
											}
										}
									}
								}
								if(ce.hasEnchantment(item, CEnchantments.TRAP)) {
									if(CEnchantments.TRAP.isEnabled()) {
										if(CEnchantments.TRAP.chanceSuccessful(item)) {
											EnchantmentUseEvent event = new EnchantmentUseEvent(damager, CEnchantments.TRAP, item);
											Bukkit.getPluginManager().callEvent(event);
											if(!event.isCancelled()) {
												en.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 3 * 20, 2));
											}
										}
									}
								}
								if(ce.hasEnchantment(item, CEnchantments.VIPER)) {
									if(CEnchantments.VIPER.isEnabled()) {
										if(CEnchantments.VIPER.chanceSuccessful(item)) {
											EnchantmentUseEvent event = new EnchantmentUseEvent(damager, CEnchantments.VIPER, item);
											Bukkit.getPluginManager().callEvent(event);
											if(!event.isCancelled()) {
												en.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 5 * 20, ce.getPower(item, CEnchantments.VIPER)));
											}
										}
									}
								}
								if(ce.hasEnchantment(item, CEnchantments.WITHER)) {
									if(CEnchantments.WITHER.isEnabled()) {
										if(CEnchantments.WITHER.chanceSuccessful(item)) {
											EnchantmentUseEvent event = new EnchantmentUseEvent(damager, CEnchantments.WITHER, item);
											Bukkit.getPluginManager().callEvent(event);
											if(!event.isCancelled()) {
												en.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 2 * 20, 2));
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerDeath(PlayerDeathEvent e) {
		if(Support.isFriendly(e.getEntity().getKiller(), e.getEntity())) return;
		if(!Support.allowsPVP(e.getEntity().getLocation())) return;
		if(e.getEntity().getKiller() instanceof Player) {
			Player damager = e.getEntity().getKiller();
			Player player = e.getEntity();
			ItemStack item = Methods.getItemInHand(damager);
			if(ce.hasEnchantments(item)) {
				if(ce.hasEnchantment(item, CEnchantments.HEADLESS)) {
					if(CEnchantments.HEADLESS.isEnabled()) {
						int power = ce.getPower(item, CEnchantments.HEADLESS);
						if(CEnchantments.HEADLESS.chanceSuccessful(item)) {
							EnchantmentUseEvent event = new EnchantmentUseEvent(damager, CEnchantments.HEADLESS, item);
							Bukkit.getPluginManager().callEvent(event);
							if(!event.isCancelled()) {
								ItemStack head = new ItemBuilder().setMaterial(Material.SKULL).setMetaData((short) 3).build();
								SkullMeta m = (SkullMeta) head.getItemMeta();
								m.setOwner(player.getName());
								head.setItemMeta(m);
								e.getDrops().add(head);
							}
						}
					}
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onEntityDeath(EntityDeathEvent e) {
		if(Support.isFriendly(e.getEntity().getKiller(), e.getEntity())) return;
		if(e.getEntity().getKiller() instanceof Player) {
			Player damager = e.getEntity().getKiller();
			ItemStack item = Methods.getItemInHand(damager);
			if(ce.hasEnchantments(item)) {
				if(ce.hasEnchantment(item, CEnchantments.INQUISITIVE)) {
					if(CEnchantments.INQUISITIVE.isEnabled()) {
						if(CEnchantments.INQUISITIVE.chanceSuccessful(item)) {
							EnchantmentUseEvent event = new EnchantmentUseEvent(damager, CEnchantments.INQUISITIVE, item);
							Bukkit.getPluginManager().callEvent(event);
							if(!event.isCancelled()) {
								e.setDroppedExp(e.getDroppedExp() * (ce.getPower(item, CEnchantments.INQUISITIVE) + 1));
							}
						}
					}
				}
			}
		}
	}
	
}