package com.emoniph.witchery.commands;

import com.emoniph.witchery.common.ExtendedPlayer;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
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
        return "/spirit <form|astral|status|level <0-10>>";
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
            return getListOfStringsMatchingLastWord(args, "form", "astral", "status", "level", "forma", "estado", "nivel");
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
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.AQUA + "/spirit form" + EnumChatFormatting.GRAY + " (o forma) - Transfórmate en Espíritu etéreo (Req. Nivel 4)."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.AQUA + "/spirit astral" + EnumChatFormatting.GRAY + " - Separa tu espíritu de tu cuerpo carnal (Req. Nivel 10)."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.AQUA + "/spirit status" + EnumChatFormatting.GRAY + " (o estado) - Muestra tus poderes espirituales desbloqueados."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.AQUA + "/spirit level <0-10>" + EnumChatFormatting.GRAY + " (o nivel) - Ajusta tu nivel de espíritu."));
            return;
        }

        String sub = args[0].toLowerCase();

        if (sub.equals("form") || sub.equals("forma")) {
            if (playerEx.getSpiritLevel() < 4) {
                player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Necesitas nivel de Espíritu 4 o superior para adoptar la Forma de Espíritu. Tu nivel actual: " + playerEx.getSpiritLevel()));
                return;
            }
            ExtendedPlayer.trySaySpiritForm(player, "spirit");
            boolean inForm = (playerEx.getCreatureType() == com.emoniph.witchery.util.TransformCreature.SPIRIT);
            player.addChatMessage(new ChatComponentText(inForm ?
                    EnumChatFormatting.AQUA + "¡Has adoptado la Forma Espiritual!" :
                    EnumChatFormatting.YELLOW + "Has regresado a tu forma terrenal."));
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

        if (sub.equals("status") || sub.equals("estado")) {
            int lvl = playerEx.getSpiritLevel();
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.DARK_PURPLE + "=== Habilidades de Espíritu Desbloqueadas (Nivel " + lvl + "/10) ==="));
            player.addChatMessage(new ChatComponentText((lvl >= 1 ? EnumChatFormatting.GREEN + "✔ " : EnumChatFormatting.RED + "✖ ") + EnumChatFormatting.WHITE + "Nivel 1: Visión del Plano Espiritual (ver fantasmas y espíritus)."));
            player.addChatMessage(new ChatComponentText((lvl >= 4 ? EnumChatFormatting.GREEN + "✔ " : EnumChatFormatting.RED + "✖ ") + EnumChatFormatting.WHITE + "Nivel 4: Forma de Espíritu etérea (/spirit form o cántico 'spirit')."));
            player.addChatMessage(new ChatComponentText((lvl >= 6 ? EnumChatFormatting.GREEN + "✔ " : EnumChatFormatting.RED + "✖ ") + EnumChatFormatting.WHITE + "Nivel 6: Doma y montura de Pesadillas (Nightmare) y posesión cinética."));
            player.addChatMessage(new ChatComponentText((lvl >= 7 ? EnumChatFormatting.GREEN + "✔ " : EnumChatFormatting.RED + "✖ ") + EnumChatFormatting.WHITE + "Nivel 7: Velo de invisibilidad y anulación de agresividad al agacharte."));
            player.addChatMessage(new ChatComponentText((lvl >= 10 ? EnumChatFormatting.GREEN + "✔ " : EnumChatFormatting.RED + "✖ ") + EnumChatFormatting.WHITE + "Nivel 10: Proyección Astral total (/spirit astral o cántico 'astral')."));
            return;
        }

        player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Subcomando desconocido. Usa: /spirit <form|astral|status|level <0-10>>"));
    }
}

