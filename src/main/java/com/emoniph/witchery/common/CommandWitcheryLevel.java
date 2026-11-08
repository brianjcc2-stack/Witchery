package com.emoniph.witchery.common;

import com.emoniph.witchery.Witchery;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import java.util.List;

public class CommandWitcheryLevel extends CommandBase {

    @Override
    public String getCommandName() {
        return "witcherylevel";
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
    public String getCommandUsage(ICommandSender sender) {
        return "/witcherylevel [player] <vampire|werewolf|spirit> <0-10>";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length == 0 && sender instanceof EntityPlayer) {
            EntityPlayer p = (EntityPlayer) sender;
            ExtendedPlayer pEx = ExtendedPlayer.get(p);
            if (pEx != null) {
                sender.addChatMessage(new ChatComponentText(EnumChatFormatting.DARK_PURPLE + "=== Niveles de Brujería (" + p.getCommandSenderName() + ") ==="));
                sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Vampiro: " + EnumChatFormatting.WHITE + pEx.getVampireLevel() + "/10"));
                sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "Hombre Lobo: " + EnumChatFormatting.WHITE + pEx.getWerewolfLevel() + "/10"));
                sender.addChatMessage(new ChatComponentText(EnumChatFormatting.AQUA + "Espíritu: " + EnumChatFormatting.WHITE + pEx.getSpiritLevel() + "/10"));
                sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GRAY + "Uso para cambiar: /witcherylevel [jugador] <vampire|werewolf|spirit> <0-10>"));
                return;
            }
        }

        if (args.length == 1) {
            EntityPlayerMP target = getPlayer(sender, args[0]);
            ExtendedPlayer pEx = ExtendedPlayer.get(target);
            if (pEx != null) {
                sender.addChatMessage(new ChatComponentText(EnumChatFormatting.DARK_PURPLE + "=== Niveles de Brujería (" + target.getCommandSenderName() + ") ==="));
                sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Vampiro: " + EnumChatFormatting.WHITE + pEx.getVampireLevel() + "/10"));
                sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "Hombre Lobo: " + EnumChatFormatting.WHITE + pEx.getWerewolfLevel() + "/10"));
                sender.addChatMessage(new ChatComponentText(EnumChatFormatting.AQUA + "Espíritu: " + EnumChatFormatting.WHITE + pEx.getSpiritLevel() + "/10"));
                return;
            }
        }

        EntityPlayerMP player;
        String type;
        int level;

        if (args.length == 2) {
            if (!(sender instanceof EntityPlayerMP)) {
                throw new WrongUsageException("Specify player name when running from console: " + getCommandUsage(sender));
            }
            player = (EntityPlayerMP) sender;
            type = args[0].toLowerCase();
            level = parseIntBounded(sender, args[1], 0, 10);
        } else if (args.length >= 3) {
            player = getPlayer(sender, args[0]);
            type = args[1].toLowerCase();
            level = parseIntBounded(sender, args[2], 0, 10);
        } else {
            throw new WrongUsageException(getCommandUsage(sender));
        }

        ExtendedPlayer playerEx = ExtendedPlayer.get(player);
        if (playerEx == null) {
            throw new CommandException("Player does not have Witchery data");
        }

        if (type.equals("vampire") || type.equals("vampiro")) {
            playerEx.setVampireLevel(level);
        } else if (type.equals("werewolf") || type.equals("lobo") || type.equals("licantropo")) {
            playerEx.setWerewolfLevel(level);
        } else if (type.equals("spirit") || type.equals("espiritu")) {
            playerEx.setSpiritLevel(level);
        } else {
            throw new WrongUsageException(getCommandUsage(sender));
        }

        sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + "Nivel de " + type + " establecido a " + level + " para " + player.getCommandSenderName()));
    }

    @Override
    public List addTabCompletionOptions(ICommandSender sender, String[] args) {
        if (args.length == 1) {
            return getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getAllUsernames());
        } else if (args.length == 2) {
            return getListOfStringsMatchingLastWord(args, new String[]{"vampire", "werewolf", "spirit"});
        }
        return null;
    }
}
