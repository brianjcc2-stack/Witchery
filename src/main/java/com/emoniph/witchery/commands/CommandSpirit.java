package com.emoniph.witchery.commands;

import com.emoniph.witchery.Witchery;
import com.emoniph.witchery.common.ExtendedPlayer;
import com.emoniph.witchery.common.Shapeshift;
import com.emoniph.witchery.common.SupremeWitchData;
import com.emoniph.witchery.entity.EntityLostSoul;
import com.emoniph.witchery.infusion.Infusion;
import com.emoniph.witchery.util.ParticleEffect;
import com.emoniph.witchery.util.SoundEffect;
import com.emoniph.witchery.util.TimeUtil;
import com.emoniph.witchery.util.TransformCreature;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import java.util.Arrays;
import java.util.List;

public class CommandSpirit extends CommandBase {

    @Override
    public String getCommandName() {
        return "spirit";
    }

    @Override
    public List getCommandAliases() {
        return Arrays.asList("espiritu", "astral");
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/spirit <form|subdued|astral|summon|supreme|status|level <0-10>>";
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
            return getListOfStringsMatchingLastWord(args, "form", "subdued", "astral", "summon", "supreme", "status", "level", "forma", "orbe", "invocar", "suprema", "estado", "nivel");
        }
        if (args.length == 2 && (args[0].equalsIgnoreCase("form") || args[0].equalsIgnoreCase("forma"))) {
            return getListOfStringsMatchingLastWord(args, "spirit", "subdued", "espectro", "orbe");
        }
        if (args.length == 2 && (args[0].equalsIgnoreCase("supreme") || args[0].equalsIgnoreCase("suprema"))) {
            return getListOfStringsMatchingLastWord(args, "claim", "revoke", "status", "reclamar", "renunciar");
        }
        return null;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (!(sender instanceof EntityPlayer)) return;
        EntityPlayer player = (EntityPlayer) sender;
        ExtendedPlayer playerEx = ExtendedPlayer.get(player);
        if (playerEx == null) return;

        if (args.length == 0) {
            int lvl = playerEx.getSpiritLevel();
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.DARK_PURPLE + "=== Magia del Plano Espiritual (Nivel: " + lvl + "/10) ==="));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.AQUA + "/spirit form [spirit|subdued]" + EnumChatFormatting.GRAY + " - Transfórmate en Espectro o Espíritu Subyugado (Orbe)."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.AQUA + "/spirit subdued" + EnumChatFormatting.GRAY + " (o /spirit orbe) - Forma de Espíritu Subyugado veloz e intangible."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.AQUA + "/spirit summon" + EnumChatFormatting.GRAY + " (o /spirit invocar) - Invoca espíritus subyugados para atacar como Leonard."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.AQUA + "/spirit astral" + EnumChatFormatting.GRAY + " - Separa tu espíritu de tu cuerpo carnal (Req. Nivel 10)."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.AQUA + "/spirit supreme" + EnumChatFormatting.GRAY + " (o suprema) - Consulta o gestiona el Manto de la Bruja Suprema."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.AQUA + "/spirit status" + EnumChatFormatting.GRAY + " (o estado) - Muestra tus poderes espirituales desbloqueados."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.AQUA + "/spirit level <0-10>" + EnumChatFormatting.GRAY + " (o nivel) - Ajusta tu nivel de espíritu."));
            return;
        }

        String sub = args[0].toLowerCase();

        if (sub.equals("form") || sub.equals("forma")) {
            boolean isSupreme = SupremeWitchData.isPlayerSupreme(player);

            if (args.length >= 2) {
                String type = args[1].toLowerCase();
                if (type.equals("subdued") || type.equals("orbe") || type.equals("wisp") || type.equals("subyugado")) {
                    if (playerEx.getSpiritLevel() < 3 && !isSupreme) {
                        player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Necesitas nivel de Espíritu 3 o superior (o ser la Bruja Suprema) para adoptar la Forma de Espíritu Subyugado. Tu nivel: " + playerEx.getSpiritLevel()));
                        return;
                    }
                    Shapeshift.INSTANCE.shiftTo(player, TransformCreature.SUBDUED_SPIRIT);
                    player.worldObj.playSoundAtEntity(player, "witchery:mob.spirit.say", 1.0F, 1.2F);
                    ParticleEffect.PORTAL.send(SoundEffect.NONE, player.worldObj, player.posX, player.posY + 1.0D, player.posZ, 0.5D, 1.0D, 15);
                    player.addChatMessage(new ChatComponentText(EnumChatFormatting.AQUA + "¡Has adoptado la Forma de Espíritu Subyugado!"));
                    return;
                } else if (type.equals("spirit") || type.equals("espectro") || type.equals("humanoide")) {
                    if (playerEx.getSpiritLevel() < 4) {
                        player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Necesitas nivel de Espíritu 4 o superior para adoptar la Forma de Espectro. Tu nivel: " + playerEx.getSpiritLevel()));
                        return;
                    }
                    Shapeshift.INSTANCE.shiftTo(player, TransformCreature.SPIRIT);
                    player.worldObj.playSoundAtEntity(player, "witchery:mob.spirit.say", 1.0F, 0.8F);
                    ParticleEffect.PORTAL.send(SoundEffect.NONE, player.worldObj, player.posX, player.posY + 1.0D, player.posZ, 0.5D, 1.0D, 15);
                    player.addChatMessage(new ChatComponentText(EnumChatFormatting.AQUA + "¡Has adoptado la Forma de Espectro!"));
                    return;
                }
            }

            TransformCreature current = playerEx.getCreatureType();
            if (current == TransformCreature.SPIRIT) {
                if (playerEx.getSpiritLevel() >= 3 || isSupreme) {
                    Shapeshift.INSTANCE.shiftTo(player, TransformCreature.SUBDUED_SPIRIT);
                    player.worldObj.playSoundAtEntity(player, "witchery:mob.spirit.say", 1.0F, 1.2F);
                    ParticleEffect.PORTAL.send(SoundEffect.NONE, player.worldObj, player.posX, player.posY + 1.0D, player.posZ, 0.5D, 1.0D, 15);
                    player.addChatMessage(new ChatComponentText(EnumChatFormatting.AQUA + "¡Has cambiado a Forma de Espíritu Subyugado (Orbe)!"));
                } else {
                    Shapeshift.INSTANCE.shiftTo(player, TransformCreature.NONE);
                    player.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "Has regresado a tu forma terrenal."));
                }
            } else if (current == TransformCreature.SUBDUED_SPIRIT) {
                Shapeshift.INSTANCE.shiftTo(player, TransformCreature.NONE);
                player.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "Has regresado a tu forma terrenal."));
            } else {
                if (playerEx.getSpiritLevel() >= 4) {
                    Shapeshift.INSTANCE.shiftTo(player, TransformCreature.SPIRIT);
                    player.worldObj.playSoundAtEntity(player, "witchery:mob.spirit.say", 1.0F, 0.8F);
                    ParticleEffect.PORTAL.send(SoundEffect.NONE, player.worldObj, player.posX, player.posY + 1.0D, player.posZ, 0.5D, 1.0D, 15);
                    player.addChatMessage(new ChatComponentText(EnumChatFormatting.AQUA + "¡Has adoptado la Forma de Espectro!"));
                } else if (playerEx.getSpiritLevel() >= 3 || isSupreme) {
                    Shapeshift.INSTANCE.shiftTo(player, TransformCreature.SUBDUED_SPIRIT);
                    player.worldObj.playSoundAtEntity(player, "witchery:mob.spirit.say", 1.0F, 1.2F);
                    ParticleEffect.PORTAL.send(SoundEffect.NONE, player.worldObj, player.posX, player.posY + 1.0D, player.posZ, 0.5D, 1.0D, 15);
                    player.addChatMessage(new ChatComponentText(EnumChatFormatting.AQUA + "¡Has adoptado la Forma de Espíritu Subyugado!"));
                } else {
                    player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Necesitas nivel de Espíritu 3 para Espíritu Subyugado, o 4 para Espectro. Tu nivel: " + playerEx.getSpiritLevel()));
                }
            }
            return;
        }

        if (sub.equals("subdued") || sub.equals("orbe") || sub.equals("wisp") || sub.equals("subyugado")) {
            boolean isSupreme = SupremeWitchData.isPlayerSupreme(player);
            if (playerEx.getSpiritLevel() < 3 && !isSupreme) {
                player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Necesitas nivel de Espíritu 3 o superior (o ser la Bruja Suprema) para adoptar la Forma de Espíritu Subyugado. Tu nivel: " + playerEx.getSpiritLevel()));
                return;
            }
            if (playerEx.getCreatureType() == TransformCreature.SUBDUED_SPIRIT) {
                Shapeshift.INSTANCE.shiftTo(player, TransformCreature.NONE);
                player.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "Has regresado a tu forma terrenal."));
            } else {
                Shapeshift.INSTANCE.shiftTo(player, TransformCreature.SUBDUED_SPIRIT);
                player.worldObj.playSoundAtEntity(player, "witchery:mob.spirit.say", 1.0F, 1.2F);
                ParticleEffect.PORTAL.send(SoundEffect.NONE, player.worldObj, player.posX, player.posY + 1.0D, player.posZ, 0.5D, 1.0D, 15);
                player.addChatMessage(new ChatComponentText(EnumChatFormatting.AQUA + "¡Has adoptado la Forma de Espíritu Subyugado!"));
            }
            return;
        }

        if (sub.equals("summon") || sub.equals("invocar") || sub.equals("attack") || sub.equals("atacar")) {
            if (playerEx.getSpiritLevel() < 5) {
                player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Necesitas nivel de Espíritu 5 o superior para invocar la danza de espíritus subyugados. Tu nivel: " + playerEx.getSpiritLevel()));
                return;
            }

            int count = 4;
            EntityLivingBase targetEnemy = null;
            AxisAlignedBB aabb = AxisAlignedBB.getBoundingBox(player.posX - 16.0D, player.posY - 8.0D, player.posZ - 16.0D, player.posX + 16.0D, player.posY + 8.0D, player.posZ + 16.0D);
            List entities = player.worldObj.getEntitiesWithinAABB(EntityMob.class, aabb);
            if (!entities.isEmpty()) {
                targetEnemy = (EntityLivingBase) entities.get(player.worldObj.rand.nextInt(entities.size()));
            }

            for (int i = 0; i < count; i++) {
                double spawnX = player.posX + (player.worldObj.rand.nextDouble() - 0.5D) * 4.0D;
                double spawnY = player.posY + 1.0D + player.worldObj.rand.nextDouble() * 1.5D;
                double spawnZ = player.posZ + (player.worldObj.rand.nextDouble() - 0.5D) * 4.0D;

                EntityLostSoul soul = (EntityLostSoul) Infusion.spawnCreature(player.worldObj, EntityLostSoul.class, (int)spawnX, (int)spawnY, (int)spawnZ, null, 0, 0, ParticleEffect.INSTANT_SPELL, SoundEffect.RANDOM_POP);
                if (soul != null) {
                    soul.setSummoner(player);
                    soul.setSoulType(i % 3);
                    soul.setTimeToLive(TimeUtil.secsToTicks(50));
                    if (targetEnemy != null) {
                        soul.setAttackTarget(targetEnemy);
                    }
                }
            }

            player.worldObj.playSoundAtEntity(player, "witchery:mob.spirit.say", 1.2F, 0.9F);
            ParticleEffect.PORTAL.send(SoundEffect.RANDOM_ORB, player.worldObj, player.posX, player.posY + 1.0D, player.posZ, 2.0D, 2.0D, 30);
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.LIGHT_PURPLE + "¡Has desatado una horda de espíritus subyugados para que luchen por ti!"));
            return;
        }

        if (sub.equals("astral") || sub.equals("corpus") || sub.equals("proyeccion")) {
            if (playerEx.getSpiritLevel() < 10) {
                player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Necesitas maestría de Espíritu 10 para la Proyección Astral. Tu nivel actual: " + playerEx.getSpiritLevel()));
                return;
            }
            ExtendedPlayer.trySayAstralProjection(player, "astral");
            boolean astral = playerEx.isAstralProjecting();
            player.addChatMessage(new ChatComponentText(astral ?
                    EnumChatFormatting.LIGHT_PURPLE + "¡Tu espíritu se ha desprendido de tu cuerpo carnal! Vuela libremente por el plano astral." :
                    EnumChatFormatting.YELLOW + "Tu espíritu se ha reintegrado en tu cuerpo físico."));
            return;
        }

        if (sub.equals("level") || sub.equals("nivel")) {
            if (args.length < 2) {
                player.addChatMessage(new ChatComponentText(EnumChatFormatting.AQUA + "Nivel actual de Espíritu: " + playerEx.getSpiritLevel() + "/10"));
                return;
            }
            try {
                int newLvl = Integer.parseInt(args[1]);
                if (newLvl < 0) newLvl = 0;
                if (newLvl > 10) newLvl = 10;
                playerEx.setSpiritLevel(newLvl);
                player.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + "Nivel de Espíritu establecido a " + newLvl + "/10."));
            } catch (NumberFormatException e) {
                player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Introduce un nivel válido de 0 a 10."));
            }
            return;
        }

        if (sub.equals("supreme") || sub.equals("suprema")) {
            SupremeWitchData data = SupremeWitchData.get(player.worldObj);
            if (data == null) {
                player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Error obteniendo datos de la Bruja Suprema."));
                return;
            }

            if (args.length >= 2) {
                String action = args[1].toLowerCase();
                if (action.equals("claim") || action.equals("reclamar")) {
                    data.setSupreme(player);
                    if (playerEx.getSpiritLevel() == 0) {
                        playerEx.setSpiritLevel(1);
                    }
                    player.addChatMessage(new ChatComponentText(EnumChatFormatting.DARK_PURPLE + "" + EnumChatFormatting.BOLD + "[Bruja Suprema] " + EnumChatFormatting.LIGHT_PURPLE + "¡Has reclamado el manto sagrado de la " + EnumChatFormatting.GOLD + "Bruja Suprema" + EnumChatFormatting.LIGHT_PURPLE + "!"));
                    player.addChatMessage(new ChatComponentText(EnumChatFormatting.DARK_PURPLE + "[Bruja Suprema] " + EnumChatFormatting.AQUA + "Como Suprema, puedes adoptar la Forma de Espíritu Subyugado (/spirit subdued) desde el inicio."));
                    if (net.minecraft.server.MinecraftServer.getServer() != null) {
                        net.minecraft.server.MinecraftServer.getServer().getConfigurationManager().sendChatMsg(new ChatComponentText(EnumChatFormatting.DARK_PURPLE + "" + EnumChatFormatting.BOLD + "★ [Witchery] " + EnumChatFormatting.LIGHT_PURPLE + "¡" + EnumChatFormatting.GOLD + player.getCommandSenderName() + EnumChatFormatting.LIGHT_PURPLE + " ha ascendido como la " + EnumChatFormatting.GOLD + "Bruja Suprema" + EnumChatFormatting.LIGHT_PURPLE + "!"));
                    }
                    return;
                } else if (action.equals("revoke") || action.equals("renunciar") || action.equals("clear") || action.equals("quitar")) {
                    boolean isOp = net.minecraft.server.MinecraftServer.getServer() == null || !net.minecraft.server.MinecraftServer.getServer().isDedicatedServer() || net.minecraft.server.MinecraftServer.getServer().getConfigurationManager().func_152596_g(player.getGameProfile()) || player.capabilities.isCreativeMode;
                    if (data.isSupreme(player) || isOp) {
                        data.clearSupreme();
                        player.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "El manto de la Bruja Suprema ha sido liberado. El trono queda vacante."));
                        if (net.minecraft.server.MinecraftServer.getServer() != null) {
                            net.minecraft.server.MinecraftServer.getServer().getConfigurationManager().sendChatMsg(new ChatComponentText(EnumChatFormatting.DARK_PURPLE + "★ [Witchery] " + EnumChatFormatting.LIGHT_PURPLE + "El trono de la Bruja Suprema ha quedado vacante."));
                        }
                    } else {
                        player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Solo la Bruja Suprema o un administrador pueden liberar el manto."));
                    }
                    return;
                }
            }

            int myScore = SupremeWitchData.calculateWitcheryMasteryScore(player);
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.DARK_PURPLE + "=== Manto de la Bruja Suprema ==="));
            if (data.hasSupreme()) {
                boolean isYou = data.isSupreme(player);
                player.addChatMessage(new ChatComponentText(EnumChatFormatting.LIGHT_PURPLE + "Bruja Suprema actual: " + EnumChatFormatting.GOLD + data.getSupremeName() + (isYou ? EnumChatFormatting.GREEN + " (¡Eres tú!)" : "")));
                if (isYou) {
                    player.addChatMessage(new ChatComponentText(EnumChatFormatting.AQUA + "Posees el Manto Sagrado (Forma de Espíritu Subyugado desbloqueada y aura astral)."));
                    player.addChatMessage(new ChatComponentText(EnumChatFormatting.GRAY + "Usa '/spirit supreme revoke' si deseas renunciar al manto."));
                }
            } else {
                player.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "El trono está actualmente vacante."));
            }
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.AQUA + "Tu Puntuación de Maestría en Brujería: " + EnumChatFormatting.GOLD + myScore + " pts"));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.GRAY + "(Basado en: Nivel de Espíritu, Aquelarre, Familiar, Infusión y Hechizos)"));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.DARK_PURPLE + "✦ ¡La bruja con mayor maestría en el servidor ostenta el manto!"));
            return;
        }

        if (sub.equals("status") || sub.equals("estado")) {
            int lvl = playerEx.getSpiritLevel();
            boolean isSupreme = SupremeWitchData.isPlayerSupreme(player);
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.DARK_PURPLE + "=== Senda Espiritual (Nivel " + lvl + "/10)" + (isSupreme ? EnumChatFormatting.GOLD + " [Bruja Suprema]" : "") + " ==="));
            player.addChatMessage(new ChatComponentText((lvl >= 1 ? EnumChatFormatting.GREEN + "✔ " : EnumChatFormatting.RED + "✖ ") + EnumChatFormatting.WHITE + "Nivel 1: Percepción del Plano Espiritual (visión de fantasmas y espíritus)."));
            player.addChatMessage(new ChatComponentText((lvl >= 2 ? EnumChatFormatting.GREEN + "✔ " : EnumChatFormatting.RED + "✖ ") + EnumChatFormatting.WHITE + "Nivel 2: Resonancia de Almas (sintonía ectoplásmica y resistencia)."));
            player.addChatMessage(new ChatComponentText((lvl >= 3 || isSupreme ? EnumChatFormatting.GREEN + "✔ " : EnumChatFormatting.RED + "✖ ") + EnumChatFormatting.WHITE + "Nivel 3: Forma de Espíritu Subyugado (/spirit subdued)" + (isSupreme && lvl < 3 ? EnumChatFormatting.GOLD + " [Manto Suprema]" : "") + "."));
            player.addChatMessage(new ChatComponentText((lvl >= 4 ? EnumChatFormatting.GREEN + "✔ " : EnumChatFormatting.RED + "✖ ") + EnumChatFormatting.WHITE + "Nivel 4: Forma de Espectro (/spirit form spirit - vuelo etéreo)."));
            player.addChatMessage(new ChatComponentText((lvl >= 5 ? EnumChatFormatting.GREEN + "✔ " : EnumChatFormatting.RED + "✖ ") + EnumChatFormatting.WHITE + "Nivel 5: Invocación de Espíritus Subyugados (/spirit summon - danza de Leonard)."));
            player.addChatMessage(new ChatComponentText((lvl >= 6 ? EnumChatFormatting.GREEN + "✔ " : EnumChatFormatting.RED + "✖ ") + EnumChatFormatting.WHITE + "Nivel 6: Velo de Sigilo Ectoplásmico (invisibilidad ante monstruos al agacharte)."));
            player.addChatMessage(new ChatComponentText((lvl >= 7 ? EnumChatFormatting.GREEN + "✔ " : EnumChatFormatting.RED + "✖ ") + EnumChatFormatting.WHITE + "Nivel 7: Salvación del Alma (Death Ward ante golpes mortales con orbe salvador)."));
            player.addChatMessage(new ChatComponentText((lvl >= 8 ? EnumChatFormatting.GREEN + "✔ " : EnumChatFormatting.RED + "✖ ") + EnumChatFormatting.WHITE + "Nivel 8: Posesión Espiritual (click derecho agachado para poseer criaturas)."));
            player.addChatMessage(new ChatComponentText((lvl >= 9 ? EnumChatFormatting.GREEN + "✔ " : EnumChatFormatting.RED + "✖ ") + EnumChatFormatting.WHITE + "Nivel 9: Transfase y Paso Astral (atravesar muros sólidos en forma de espíritu)."));
            player.addChatMessage(new ChatComponentText((lvl >= 10 ? EnumChatFormatting.GREEN + "✔ " : EnumChatFormatting.RED + "✖ ") + EnumChatFormatting.WHITE + "Nivel 10: Proyección Astral Total (/spirit astral - separación corporal)."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.DARK_PURPLE + "✦ " + EnumChatFormatting.LIGHT_PURPLE + "¿Cómo subir de nivel?: " + EnumChatFormatting.GRAY + "Derrota criaturas del plano espiritual (Banshees, Poltergeists, Pesadillas o jefes como Leonard y Lord of the Torment) para absorber su esencia."));
            return;
        }

        player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Subcomando desconocido. Usa: /spirit <form|subdued|summon|astral|supreme|status|level <0-10>>"));
    }
}
