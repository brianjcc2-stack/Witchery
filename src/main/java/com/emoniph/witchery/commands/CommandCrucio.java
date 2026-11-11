package com.emoniph.witchery.commands;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;

import java.util.HashMap;
import java.util.Map;

public class CommandCrucio extends CommandBase {

    // Mapa para almacenar cuántos corazones de daño configuró cada jugador para su próximo hechizo Crucio.
    public static final Map<String, Integer> CRUCIO_POWER = new HashMap<String, Integer>();

    @Override
    public String getCommandName() {
        return "crucio";
    }

    @Override
    public java.util.List getCommandAliases() {
        return java.util.Arrays.asList("cruciatus");
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/crucio <1-10|guia>";
    }

    @Override
    public java.util.List addTabCompletionOptions(ICommandSender sender, String[] args) {
        if (args.length == 1) {
            return getListOfStringsMatchingLastWord(args, "1", "3", "5", "8", "10", "guia", "help", "ayuda");
        }
        return null;
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
    public void processCommand(ICommandSender sender, String[] args) {
        if (!(sender instanceof EntityPlayer)) return;
        EntityPlayer player = (EntityPlayer) sender;

        if (args.length == 0 || args[0].equalsIgnoreCase("guia") || args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("ayuda")) {
            int actual = CRUCIO_POWER.containsKey(player.getCommandSenderName()) ? CRUCIO_POWER.get(player.getCommandSenderName()) : 5;
            player.addChatMessage(new ChatComponentText("\u00a74\u00a7l=== Maldición Cruciatus (/crucio) ==="));
            player.addChatMessage(new ChatComponentText("\u00a7cPotencia actual configurada: \u00a7e" + actual + " corazones\u00a77 (máx. 10)."));
            player.addChatMessage(new ChatComponentText("\u00a7eUso: \u00a7f/crucio <1-10> \u00a77- Ajusta cuántos corazones de agonía progresiva infligirá tu próximo hechizo Crucio."));
            player.addChatMessage(new ChatComponentText("\u00a76Trazo en Rama Mística: \u00a7f↓ → ← ↑ ↓ \u00a77(Requiere Infusión Infernal o Bruja Suprema)."));
            return;
        }

        try {
            int corazones = Integer.parseInt(args[0]);
            if (corazones < 1) corazones = 1;
            
            // Límite de corazones solicitado: 10 (20 de vida total)
            if (corazones > 10) {
                corazones = 10;
                player.addChatMessage(new ChatComponentText("No puedes torturar por más de 10 corazones. ¡Para matar instantáneamente debes usar Avada Kedavra!"));
            }

            CRUCIO_POWER.put(player.getCommandSenderName(), corazones);
            player.addChatMessage(new ChatComponentText("Has configurado tu próxima Maldición Cruciatus a " + corazones + " corazones de agonía progresiva."));

        } catch (NumberFormatException e) {
            player.addChatMessage(new ChatComponentText("Por favor, introduce un número válido de corazones."));
        }
    }
}
