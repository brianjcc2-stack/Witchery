package com.emoniph.witchery.commands;

import com.emoniph.witchery.common.ExtendedPlayer;
import com.emoniph.witchery.common.Shapeshift;
import com.emoniph.witchery.util.CreatureUtil;
import com.emoniph.witchery.util.ParticleEffect;
import com.emoniph.witchery.util.SoundEffect;
import com.emoniph.witchery.util.TimeUtil;
import com.emoniph.witchery.util.TransformCreature;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;

import java.util.Arrays;
import java.util.List;

public class CommandWerewolf extends CommandBase {

    @Override
    public String getCommandName() {
        return "werewolf";
    }

    @Override
    public List getCommandAliases() {
        return Arrays.asList("licantropo", "lobo", "wolf");
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/werewolf <form|wolf|beast|human|howl|leap|scent|status|level <0-10>>";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

    @Override
    public List addTabCompletionOptions(ICommandSender sender, String[] args) {
        if (args.length == 1) {
            return getListOfStringsMatchingLastWord(args, "form", "wolf", "beast", "human", "howl", "leap", "scent", "status", "level",
                    "forma", "lobo", "bestia", "humano", "aullar", "saltar", "olfato", "estado", "nivel");
        }
        if (args.length == 2 && (args[0].equalsIgnoreCase("form") || args[0].equalsIgnoreCase("forma"))) {
            return getListOfStringsMatchingLastWord(args, "human", "wolf", "beast", "humano", "lobo", "bestia");
        }
        return null;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (!(sender instanceof EntityPlayer)) return;
        EntityPlayer player = (EntityPlayer) sender;
        ExtendedPlayer playerEx = ExtendedPlayer.get(player);
        if (playerEx == null) return;

        int lvl = playerEx.getWerewolfLevel();

        if (args.length == 0) {
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "=== Poderes de Licántropo (Nivel: " + lvl + "/10) ==="));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "/werewolf wolf" + EnumChatFormatting.GRAY + " (o lobo) - Adopta forma de lobo veloz (Req. Nvl 2)."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "/werewolf beast" + EnumChatFormatting.GRAY + " (o bestia) - Adopta la temible forma de hombre bestia (Req. Nvl 5)."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "/werewolf human" + EnumChatFormatting.GRAY + " (o humano) - Regresa a tu forma humana."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "/werewolf howl" + EnumChatFormatting.GRAY + " (o aullar) - Aullido que aterroriza enemigos y potencia lobos aliados."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "/werewolf leap" + EnumChatFormatting.GRAY + " (o saltar) - Embestida o gran salto hacia adelante sin daño de caída."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "/werewolf scent" + EnumChatFormatting.GRAY + " (o olfato) - Rastrea presas y criaturas en 32 bloques (Req. Nvl 3)."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "/werewolf status" + EnumChatFormatting.GRAY + " (o estado) - Muestra estado y fase lunar."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "/werewolf level <0-10>" + EnumChatFormatting.GRAY + " (o nivel) - Ajusta tu nivel de licantropía."));
            return;
        }

        String sub = args[0].toLowerCase();

        if (sub.equals("status") || sub.equals("estado")) {
            boolean fullMoon = CreatureUtil.isFullMoon(player.worldObj);
            float moonFactor = player.worldObj.getCurrentMoonPhaseFactor();
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "--- Estado de Licantropía ---"));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.GRAY + "Nivel: " + EnumChatFormatting.YELLOW + lvl + "/10"));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.GRAY + "Forma Actual: " + EnumChatFormatting.AQUA + playerEx.getCreatureType().name()));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.GRAY + "Fase Lunar: " + EnumChatFormatting.WHITE + (int)(moonFactor * 100) + "%" + (fullMoon ? EnumChatFormatting.RED + " (¡LUNA LLENA ACTIVA!)" : "")));
            return;
        }

        if (sub.equals("level") || sub.equals("nivel")) {
            if (args.length >= 2) {
                try {
                    int targetLvl = Integer.parseInt(args[1]);
                    if (targetLvl < 0 || targetLvl > 10) {
                        player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "El nivel debe estar entre 0 y 10."));
                        return;
                    }
                    playerEx.setWerewolfLevel(targetLvl);
                    player.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + "Nivel de Licántropo ajustado a: " + targetLvl));
                    SoundEffect.WITCHERY_MOB_WOLFMAN_TALK.playAtPlayer(player.worldObj, player);
                } catch (NumberFormatException e) {
                    player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Por favor ingresa un número válido entre 0 y 10."));
                }
            } else {
                player.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "Tu nivel actual de hombre lobo es: " + lvl));
            }
            return;
        }

        if (lvl <= 0 && !player.capabilities.isCreativeMode) {
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "No posees la sangre del lobo. Debes ser mordido bajo la luna o realizar el Ritual de la Luna de Sangre."));
            return;
        }

        if (sub.equals("human") || sub.equals("humano")) {
            Shapeshift.INSTANCE.shiftTo(player, TransformCreature.NONE);
            SoundEffect.RANDOM_FIZZ.playAtPlayer(player.worldObj, player);
            ParticleEffect.EXPLODE.send(SoundEffect.NONE, player.worldObj, player.posX, player.posY + 1.0D, player.posZ, 0.5D, 0.5D, 12);
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.GRAY + "Has vuelto a tu forma humana."));
            return;
        }

        if (sub.equals("wolf") || sub.equals("lobo")) {
            if (lvl < 2 && !player.capabilities.isCreativeMode) {
                player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Necesitas nivel de Hombre Lobo 2 o superior para transformarte voluntariamente."));
                return;
            }

            if (playerEx.getCreatureType() == TransformCreature.WOLF) {
                Shapeshift.INSTANCE.shiftTo(player, TransformCreature.NONE);
                player.addChatMessage(new ChatComponentText(EnumChatFormatting.GRAY + "Has vuelto a tu forma humana."));
            } else {
                Shapeshift.INSTANCE.shiftTo(player, TransformCreature.WOLF);
                player.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "¡Te has transformado en Lobo!"));
            }
            SoundEffect.RANDOM_FIZZ.playAtPlayer(player.worldObj, player);
            ParticleEffect.EXPLODE.send(SoundEffect.NONE, player.worldObj, player.posX, player.posY + 0.5D, player.posZ, 0.5D, 0.5D, 12);
            return;
        }

        if (sub.equals("beast") || sub.equals("bestia") || sub.equals("werewolf")) {
            if (lvl < 5 && !player.capabilities.isCreativeMode) {
                player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Necesitas nivel de Hombre Lobo 5 o superior para desatar la forma de hombre bestia."));
                return;
            }

            if (playerEx.getCreatureType() == TransformCreature.WOLFMAN) {
                Shapeshift.INSTANCE.shiftTo(player, TransformCreature.NONE);
                player.addChatMessage(new ChatComponentText(EnumChatFormatting.GRAY + "Has vuelto a tu forma humana."));
            } else {
                Shapeshift.INSTANCE.shiftTo(player, TransformCreature.WOLFMAN);
                player.addChatMessage(new ChatComponentText(EnumChatFormatting.DARK_RED + "¡Has desatado a la Bestia Licántropa!"));
            }
            SoundEffect.WITCHERY_MOB_WOLFMAN_HOWL.playAtPlayer(player.worldObj, player, 1.0F);
            ParticleEffect.EXPLODE.send(SoundEffect.NONE, player.worldObj, player.posX, player.posY + 1.0D, player.posZ, 0.8D, 1.0D, 20);
            return;
        }

        if (sub.equals("form") || sub.equals("forma")) {
            if (args.length >= 2) {
                String targetForm = args[1].toLowerCase();
                if (targetForm.equals("human") || targetForm.equals("humano")) {
                    Shapeshift.INSTANCE.shiftTo(player, TransformCreature.NONE);
                    player.addChatMessage(new ChatComponentText(EnumChatFormatting.GRAY + "Forma humana adoptada."));
                } else if (targetForm.equals("wolf") || targetForm.equals("lobo")) {
                    if (lvl < 2 && !player.capabilities.isCreativeMode) {
                        player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Requiere nivel 2 de licántropo."));
                        return;
                    }
                    Shapeshift.INSTANCE.shiftTo(player, TransformCreature.WOLF);
                    player.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "Forma de lobo adoptada."));
                } else if (targetForm.equals("beast") || targetForm.equals("bestia") || targetForm.equals("werewolf")) {
                    if (lvl < 5 && !player.capabilities.isCreativeMode) {
                        player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Requiere nivel 5 de licántropo."));
                        return;
                    }
                    Shapeshift.INSTANCE.shiftTo(player, TransformCreature.WOLFMAN);
                    player.addChatMessage(new ChatComponentText(EnumChatFormatting.DARK_RED + "Forma de bestia adoptada."));
                }
                SoundEffect.RANDOM_FIZZ.playAtPlayer(player.worldObj, player);
                ParticleEffect.EXPLODE.send(SoundEffect.NONE, player.worldObj, player.posX, player.posY + 0.8D, player.posZ, 0.5D, 0.5D, 12);
            } else {
                player.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "Usa: /werewolf form <human|wolf|beast>"));
            }
            return;
        }

        if (sub.equals("howl") || sub.equals("aullar")) {
            Shapeshift.INSTANCE.checkForHowling(player, playerEx);
            SoundEffect.WITCHERY_MOB_WOLFMAN_HOWL.playAtPlayer(player.worldObj, player, 1.0F);
            ParticleEffect.EXPLODE.send(SoundEffect.NONE, player.worldObj, player.posX, player.posY + 1.2D, player.posZ, 1.0D, 1.0D, 16);

            // Intimidate nearby monsters and inspire nearby wolves
            AxisAlignedBB bb = player.boundingBox.expand(16.0D, 8.0D, 16.0D);
            List entities = player.worldObj.getEntitiesWithinAABB(EntityLivingBase.class, bb);
            for (Object o : entities) {
                EntityLivingBase living = (EntityLivingBase) o;
                if (living instanceof EntityMob) {
                    living.addPotionEffect(new PotionEffect(Potion.weakness.id, TimeUtil.secsToTicks(10), 0));
                    living.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, TimeUtil.secsToTicks(8), 1));
                } else if (living instanceof EntityWolf) {
                    EntityWolf wolf = (EntityWolf) living;
                    wolf.addPotionEffect(new PotionEffect(Potion.damageBoost.id, TimeUtil.secsToTicks(30), 1));
                    wolf.addPotionEffect(new PotionEffect(Potion.moveSpeed.id, TimeUtil.secsToTicks(30), 1));
                }
            }
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "¡Tu aullido resuena en la noche, aterrorizando a tus enemigos e inspirando a la manada!"));
            return;
        }

        if (sub.equals("leap") || sub.equals("saltar") || sub.equals("embestir")) {
            if (lvl < 2 && !player.capabilities.isCreativeMode) {
                player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Necesitas nivel de Hombre Lobo 2 o superior para abalanzarte."));
                return;
            }

            float yaw = player.rotationYaw * 0.017453292F;
            double force = (playerEx.getCreatureType() == TransformCreature.WOLFMAN) ? 1.8D : 1.4D;
            player.motionX = -MathHelper.sin(yaw) * force;
            player.motionY = 0.60D;
            player.motionZ = MathHelper.cos(yaw) * force;
            player.velocityChanged = true;
            player.fallDistance = 0.0F;

            SoundEffect.WITCHERY_MOB_WOLFMAN_TALK.playAtPlayer(player.worldObj, player);
            ParticleEffect.EXPLODE.send(SoundEffect.NONE, player.worldObj, player.posX, player.posY, player.posZ, 0.4D, 0.2D, 8);
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "¡Te has abalanzado ferozmente!"));
            return;
        }

        if (sub.equals("scent") || sub.equals("olfato") || sub.equals("rastrear")) {
            if (lvl < 3 && !player.capabilities.isCreativeMode) {
                player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Necesitas nivel de Hombre Lobo 3 o superior para rastrear con el olfato."));
                return;
            }

            AxisAlignedBB bb = player.boundingBox.expand(32.0D, 16.0D, 32.0D);
            List list = player.worldObj.getEntitiesWithinAABBExcludingEntity(player, bb);
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "--- Olfateando el Viento (Radio 32m) ---"));
            int count = 0;
            for (Object obj : list) {
                if (obj instanceof EntityLivingBase) {
                    EntityLivingBase target = (EntityLivingBase) obj;
                    double dist = Math.round(target.getDistanceToEntity(player) * 10.0D) / 10.0D;
                    EnumChatFormatting color = (target instanceof EntityPlayer) ? EnumChatFormatting.AQUA : (target instanceof EntityMob ? EnumChatFormatting.RED : EnumChatFormatting.GREEN);
                    player.addChatMessage(new ChatComponentText(color + "- " + target.getCommandSenderName() + EnumChatFormatting.GRAY + " a " + dist + "m (Vida: " + (int)target.getHealth() + "/" + (int)target.getMaxHealth() + ")"));
                    count++;
                    if (count >= 6) break;
                }
            }
            if (count == 0) {
                player.addChatMessage(new ChatComponentText(EnumChatFormatting.GRAY + "No se detecta ningún olor en el aire cercano."));
            }
            SoundEffect.RANDOM_BREATH.playAtPlayer(player.worldObj, player);
            ParticleEffect.PORTAL.send(SoundEffect.NONE, player.worldObj, player.posX, player.posY + 1.0D, player.posZ, 0.5D, 0.5D, 10);
            return;
        }

        player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Subcomando desconocido. Usa: /werewolf <wolf|beast|human|howl|leap|scent|status|level>"));
    }
}
