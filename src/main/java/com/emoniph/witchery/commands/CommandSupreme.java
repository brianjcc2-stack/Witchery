package com.emoniph.witchery.commands;

import com.emoniph.witchery.common.ExtendedPlayer;
import com.emoniph.witchery.common.SupremeWitchData;
import com.emoniph.witchery.infusion.Infusion;
import com.emoniph.witchery.infusion.infusions.creature.CreaturePower;
import com.emoniph.witchery.util.ParticleEffect;
import com.emoniph.witchery.util.SoundEffect;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import java.util.Arrays;
import java.util.List;

public class CommandSupreme extends CommandBase {

    @Override
    public String getCommandName() {
        return "suprema";
    }

    @Override
    public List getCommandAliases() {
        return Arrays.asList("supreme");
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/suprema <maravillas|claim|revoke|silence|infusion|beast|aura|status|guia>";
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
            return getListOfStringsMatchingLastWord(args, "maravillas", "trial", "claim", "revoke", "silence", "infusion", "beast", "aura", "status", "guia", "reclamar", "renunciar", "silenciar", "infundir", "bestia", "ayuda");
        }
        if (args.length == 2 && (args[0].equalsIgnoreCase("guia") || args[0].equalsIgnoreCase("guide") || args[0].equalsIgnoreCase("ayuda"))) {
            return getListOfStringsMatchingLastWord(args, "1", "2");
        }
        if (args.length == 2 && (args[0].equalsIgnoreCase("infusion") || args[0].equalsIgnoreCase("infundir"))) {
            return getListOfStringsMatchingLastWord(args, "infernal", "light", "ender", "earth", "clear", "luz", "tierra", "fuego");
        }
        if (args.length == 2 && (args[0].equalsIgnoreCase("beast") || args[0].equalsIgnoreCase("bestia"))) {
            return getListOfStringsMatchingLastWord(args, "ghast", "creeper", "bat", "spider", "blaze");
        }
        if (args.length == 2 && (args[0].equalsIgnoreCase("silence") || args[0].equalsIgnoreCase("silenciar"))) {
            return getListOfStringsMatchingLastWord(args, MinecraftServer.getServer() != null ? MinecraftServer.getServer().getAllUsernames() : new String[0]);
        }
        if (args.length == 2 && (args[0].equalsIgnoreCase("aura") || args[0].equalsIgnoreCase("fx"))) {
            return getListOfStringsMatchingLastWord(args, "on", "off", "color", "cantidad", "densidad", "verde", "morado", "azul", "rojo", "dorado", "blanco", "rosa", "naranja", "negro", "1", "2", "3", "5", "10");
        }
        if (args.length == 3 && (args[0].equalsIgnoreCase("aura") || args[0].equalsIgnoreCase("fx"))) {
            if (args[1].equalsIgnoreCase("color")) {
                return getListOfStringsMatchingLastWord(args, "verde", "morado", "azul", "rojo", "dorado", "blanco", "rosa", "naranja", "negro");
            }
            if (args[1].equalsIgnoreCase("cantidad") || args[1].equalsIgnoreCase("densidad")) {
                return getListOfStringsMatchingLastWord(args, "1", "2", "3", "4", "5", "6", "8", "10");
            }
            if (SupremeWitchData.parseColor(args[1]) != -1) {
                return getListOfStringsMatchingLastWord(args, "1", "2", "3", "4", "5", "6", "8", "10");
            }
        }
        return null;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (!(sender instanceof EntityPlayer)) return;
        EntityPlayer player = (EntityPlayer) sender;
        ExtendedPlayer playerEx = ExtendedPlayer.get(player);
        if (playerEx == null) return;

        boolean isSupreme = SupremeWitchData.isPlayerSupreme(player);
        boolean isOp = MinecraftServer.getServer() == null || !MinecraftServer.getServer().isDedicatedServer() || MinecraftServer.getServer().getConfigurationManager().func_152596_g(player.getGameProfile());

        if (args.length == 0) {
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.DARK_PURPLE + "╔════════════════════════════════════════════╗"));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "  ★ PODERES DE LA BRUJA SUPREMA ★"));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.DARK_PURPLE + "╚════════════════════════════════════════════╝"));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.LIGHT_PURPLE + "Estado actual: " + (isSupreme ? EnumChatFormatting.GOLD + "ERES LA BRUJA SUPREMA" : EnumChatFormatting.GRAY + "No ostentas el Manto Sagrado")));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "/suprema maravillas" + EnumChatFormatting.GRAY + " - El Ritual de las Siete Maravillas (Narrativa y Ascensión)."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "/suprema claim" + EnumChatFormatting.GRAY + " - Reclama el Manto de la Bruja Suprema."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "/suprema revoke" + EnumChatFormatting.GRAY + " - Renuncia al Manto Sagrado."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "/suprema silence <jugador> [seg]" + EnumChatFormatting.GRAY + " - Silencia la magia de un rival."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "/suprema infusion <infernal|luz|ender|tierra>" + EnumChatFormatting.GRAY + " - Conmuta tu afinidad activa."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "/suprema beast <ghast|creeper|bat|spider|blaze>" + EnumChatFormatting.GRAY + " - Cargas máximas de bestia."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "/suprema aura [on|off]" + EnumChatFormatting.GRAY + " - Activa o desactiva tu aura mística."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "/suprema status" + EnumChatFormatting.GRAY + " - Consulta tu soberanía y dones supremos."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "/suprema guia [1|2]" + EnumChatFormatting.GRAY + " - Codex oficial de la Bruja Suprema."));
            return;
        }

        String sub = args[0].toLowerCase();

        if (sub.equals("maravillas") || sub.equals("trial") || sub.equals("siete_maravillas") || sub.equals("wonders")) {
            int done = SupremeWitchData.getCompletedWondersCount(player);
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.DARK_PURPLE + "╔══════════════════════════════════════════════════╗"));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "" + EnumChatFormatting.BOLD + "  ★ EL RITO DE LAS SIETE MARAVILLAS (" + done + "/7) ★"));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.DARK_PURPLE + "╚══════════════════════════════════════════════════╝"));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.GRAY + "" + EnumChatFormatting.ITALIC + "«Cuando una era termina, una nueva Suprema debe alzarse superando los siete actos antiguos de poder...»"));

            sendWonderLine(player, SupremeWitchData.WONDER_TELEKINESIS, "I. Telekinesis", "Mover la materia con la mente (Accio, Flipendo, Depulso, Leviosa).");
            sendWonderLine(player, SupremeWitchData.WONDER_CONCILIUM, "II. Concilium", "Doblegar una voluntad ajena (Hechizo Imperio o /imperio).");
            sendWonderLine(player, SupremeWitchData.WONDER_THERMOKINESIS, "III. Thermokinesis", "Desatar el fuego primordial (Incendio, Confringo, Fiendfyre).");
            sendWonderLine(player, SupremeWitchData.WONDER_DESCENSUM, "IV. Descensum", "Viajar al Más Allá y regresar (Mundo de los Sueños, Orbe o Proyección Astral).");
            sendWonderLine(player, SupremeWitchData.WONDER_VITALUM_VITALIS, "V. Vitalum Vitalis", "Tejer la vida y la muerte (Episkey, Vulnera Sanentur, Resurrection u Horrocrux).");
            sendWonderLine(player, SupremeWitchData.WONDER_TRANSMUTATION, "VI. Transmutation", "Plegar el espacio y la materia (Apparition, Aguamenti o Piedra Filosofal).");
            sendWonderLine(player, SupremeWitchData.WONDER_DIVINATION, "VII. Divination", "Ver lo oculto (Revelio, Homenum Revelio o vincular un Familiar con /pet bind).");

            if (done == 7 && !isSupreme) {
                SupremeWitchData data = SupremeWitchData.get(player.worldObj);
                if (data != null) {
                    data.setSupreme(player);
                }
                ParticleEffect.PORTAL.send(SoundEffect.RANDOM_LEVELUP, player.worldObj, player.posX, player.posY + 1.0D, player.posZ, 1.5D, 2.0D, 40);
                if (MinecraftServer.getServer() != null) {
                    MinecraftServer.getServer().getConfigurationManager().sendChatMsg(new ChatComponentText(
                            EnumChatFormatting.DARK_PURPLE + "★ " + EnumChatFormatting.GOLD + EnumChatFormatting.BOLD + player.getCommandSenderName() +
                            EnumChatFormatting.LIGHT_PURPLE + " ha consumado las " + EnumChatFormatting.GOLD + "Siete Maravillas" +
                            EnumChatFormatting.LIGHT_PURPLE + " y se ha alzado como la legítima " +
                            EnumChatFormatting.GOLD + EnumChatFormatting.BOLD + "Bruja Suprema" + EnumChatFormatting.LIGHT_PURPLE + "!"));
                }
            } else if (done < 7) {
                player.addChatMessage(new ChatComponentText(EnumChatFormatting.LIGHT_PURPLE + "Completa las " + (7 - done) + " maravillas restantes (+50 Maestría c/u) y vuelve a ejecutar " + EnumChatFormatting.YELLOW + "/suprema maravillas" + EnumChatFormatting.LIGHT_PURPLE + " para tu coronación."));
            } else {
                player.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "★ Has consumado las Siete Maravillas y reinas como la Bruja Suprema."));
            }
            return;
        }

        if (sub.equals("claim") || sub.equals("reclamar") || sub.equals("reinar")) {
            if (isSupreme) {
                player.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "Ya eres la Bruja Suprema reinante."));
                return;
            }
            SupremeWitchData data = SupremeWitchData.get(player.worldObj);
            if (data != null) {
                data.setSupreme(player);
            }
            ParticleEffect.PORTAL.send(SoundEffect.RANDOM_ORB, player.worldObj, player.posX, player.posY + 1.0D, player.posZ, 1.5D, 2.0D, 30);
            if (MinecraftServer.getServer() != null) {
                MinecraftServer.getServer().getConfigurationManager().sendChatMsg(new ChatComponentText(
                        EnumChatFormatting.GOLD + "✦ " + EnumChatFormatting.BOLD + player.getCommandSenderName() +
                                EnumChatFormatting.LIGHT_PURPLE + " ha reclamado el Manto Sagrado y ahora es la " +
                                EnumChatFormatting.GOLD + EnumChatFormatting.BOLD + "Bruja Suprema" +
                                EnumChatFormatting.LIGHT_PURPLE + "!"));
            }
            return;
        }

        if (sub.equals("revoke") || sub.equals("renunciar") || sub.equals("dejar")) {
            if (!isSupreme) {
                player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "No eres la Bruja Suprema."));
                return;
            }
            SupremeWitchData data = SupremeWitchData.get(player.worldObj);
            if (data != null) {
                data.clearSupreme();
            }
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "Has renunciado al Manto Sagrado de la Bruja Suprema."));
            return;
        }

        if (sub.equals("guia") || sub.equals("guide") || sub.equals("help") || sub.equals("ayuda")) {
            int page = (args.length >= 2 && args[1].equals("2")) ? 2 : 1;
            this.sendSupremeGuide(player, page);
            return;
        }

        if (sub.equals("silence") || sub.equals("silenciar")) {
            if (!isSupreme && !isOp) {
                player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Solo la Bruja Suprema tiene la autoridad para silenciar la magia de otras brujas."));
                return;
            }
            if (args.length < 2) {
                player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Uso: /suprema silence <jugador> [segundos]"));
                return;
            }
            String targetName = args[1];
            EntityPlayer targetPlayer = player.worldObj.getPlayerEntityByName(targetName);
            if (targetPlayer == null) {
                player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "No se encontró a la bruja '" + targetName + "' en este reino."));
                return;
            }
            if (SupremeWitchData.isPlayerSupreme(targetPlayer) && targetPlayer != player) {
                player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "No puedes silenciar a quien ostenta el manto sagrado."));
                return;
            }
            int durationSecs = 30;
            if (args.length >= 3) {
                try {
                    durationSecs = Integer.parseInt(args[2]);
                    if (durationSecs < 1) durationSecs = 1;
                    if (durationSecs > 300) durationSecs = 300;
                } catch (NumberFormatException ignored) {}
            }
            ExtendedPlayer targetEx = ExtendedPlayer.get(targetPlayer);
            if (targetEx != null) {
                targetEx.setSilenced(durationSecs);
                ParticleEffect.SPELL_COLORED.send(SoundEffect.NOTE_SNARE, targetPlayer.worldObj, targetPlayer.posX, targetPlayer.posY + 1.0D, targetPlayer.posZ, 0.8D, 1.2D, 25, 0x550055);
                targetPlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.DARK_RED + "¡La Bruja Suprema ha sellado tu magia! Tus hechizos y varitas han sido silenciados por " + durationSecs + " segundos."));
                player.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + "Has silenciado la magia de " + targetName + " por " + durationSecs + " segundos."));
            }
            return;
        }

        if (sub.equals("infusion") || sub.equals("infundir")) {
            if (!isSupreme && !isOp) {
                player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Solo la Bruja Suprema puede cambiar de infusión libremente."));
                return;
            }
            if (args.length < 2) {
                player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Uso: /suprema infusion <infernal|luz|ender|tierra|clear>"));
                return;
            }
            String type = args[1].toLowerCase();
            int id = 0;
            String name = "Ninguna";
            if (type.equals("infernal") || type.equals("fuego") || type.equals("nether") || type.equals("4")) {
                id = 4; name = "Infernal (Fuego y Bestias)";
            } else if (type.equals("light") || type.equals("luz") || type.equals("sol") || type.equals("1")) {
                id = 1; name = "Luz (Barreras y Resonancia)";
            } else if (type.equals("ender") || type.equals("vacio") || type.equals("otherwhere") || type.equals("3")) {
                id = 3; name = "Ender (Teletransporte y Dimensión)";
            } else if (type.equals("earth") || type.equals("tierra") || type.equals("world") || type.equals("overworld") || type.equals("2")) {
                id = 2; name = "Tierra (Terrenal)";
            } else if (type.equals("clear") || type.equals("limpiar") || type.equals("quitar") || type.equals("0")) {
                id = 0; name = "Limpia (Sin infusión)";
            } else {
                player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Tipo desconocido. Opciones: infernal, luz, ender, tierra, clear"));
                return;
            }
            Infusion.switchInfusionForSupreme(player, id);
            player.worldObj.playSoundAtEntity(player, "witchery:mob.spirit.say", 1.0F, 1.0F);
            ParticleEffect.FLAME.send(SoundEffect.RANDOM_ORB, player.worldObj, player.posX, player.posY + 1.0D, player.posZ, 0.8D, 1.2D, 16);
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.DARK_PURPLE + "[Bruja Suprema] " + EnumChatFormatting.LIGHT_PURPLE + "Infusión activa: " + EnumChatFormatting.GOLD + name + EnumChatFormatting.LIGHT_PURPLE + " (Energía: 3000/3000)"));
            return;
        }

        if (sub.equals("beast") || sub.equals("bestia")) {
            if (!isSupreme && !isOp) {
                player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Solo la Bruja Suprema puede invocar poderes de bestias infernales directamente."));
                return;
            }
            if (args.length < 2) {
                player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Uso: /suprema beast <ghast|creeper|bat|spider|blaze>"));
                return;
            }
            String b = args[1].toLowerCase();
            int powerId = 0;
            String beastName = "";
            if (b.equals("ghast")) {
                powerId = 6; beastName = "Bola de Fuego de Ghast";
            } else if (b.equals("creeper")) {
                powerId = 3; beastName = "Explosión de Creeper";
            } else if (b.equals("bat") || b.equals("murcielago")) {
                powerId = 4; beastName = "Vuelo de Murciélago";
            } else if (b.equals("spider") || b.equals("arana") || b.equals("araña")) {
                powerId = 2; beastName = "Trepar Místico de Araña";
            } else if (b.equals("blaze")) {
                powerId = 7; beastName = "Llama Ardiente de Blaze";
            } else {
                player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Bestia desconocida. Opciones: ghast, creeper, bat, spider, blaze"));
                return;
            }
            CreaturePower.setCreaturePowerID(player, powerId, 20);
            ParticleEffect.SPELL_COLORED.send(SoundEffect.RANDOM_ORB, player.worldObj, player.posX, player.posY + 1.0D, player.posZ, 0.8D, 1.0D, 20, 0xFF4500);
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "✦ [Bruja Suprema] Has canalizado el poder: " + EnumChatFormatting.YELLOW + beastName + EnumChatFormatting.WHITE + " (20 cargas)."));
            return;
        }

        if (sub.equals("aura") || sub.equals("fx")) {
            if (!isSupreme && !isOp) {
                player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Solo la Bruja Suprema posee un aura mística adaptable."));
                return;
            }
            SupremeWitchData data = SupremeWitchData.get(player.worldObj);
            if (data == null) return;

            if (args.length == 1) {
                boolean newState = !data.isAuraEnabled();
                data.setAuraEnabled(newState);
                player.addChatMessage(new ChatComponentText(newState ?
                        EnumChatFormatting.GOLD + "✦ [Bruja Suprema] Aura de partículas del altar ACTIVADA." :
                        EnumChatFormatting.GRAY + "✦ [Bruja Suprema] Aura mística DESACTIVADA (Modo discreto)."));
                return;
            }

            String first = args[1].toLowerCase();

            if (first.equals("on") || first.equals("activar") || first.equals("true")) {
                data.setAuraEnabled(true);
                player.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "✦ [Bruja Suprema] Aura de partículas del altar ACTIVADA."));
                return;
            }

            if (first.equals("off") || first.equals("desactivar") || first.equals("false")) {
                data.setAuraEnabled(false);
                player.addChatMessage(new ChatComponentText(EnumChatFormatting.GRAY + "✦ [Bruja Suprema] Aura mística DESACTIVADA (Modo discreto)."));
                return;
            }

            if (first.equals("status") || first.equals("info") || first.equals("estado")) {
                player.addChatMessage(new ChatComponentText(EnumChatFormatting.DARK_PURPLE + "★ Aura de la Bruja Suprema ★"));
                player.addChatMessage(new ChatComponentText(EnumChatFormatting.LIGHT_PURPLE + "Estado: " + (data.isAuraEnabled() ? EnumChatFormatting.GREEN + "Activada" : EnumChatFormatting.RED + "Desactivada")));
                player.addChatMessage(new ChatComponentText(EnumChatFormatting.LIGHT_PURPLE + "Color: " + EnumChatFormatting.AQUA + SupremeWitchData.getColorName(data.getAuraColor())));
                player.addChatMessage(new ChatComponentText(EnumChatFormatting.LIGHT_PURPLE + "Densidad / Cantidad: " + EnumChatFormatting.GOLD + data.getAuraAmount() + "/10"));
                return;
            }

            if ((first.equals("color") || first.equals("colour")) && args.length >= 3) {
                int parsed = SupremeWitchData.parseColor(args[2]);
                if (parsed != -1) {
                    data.setAuraColor(parsed);
                    data.setAuraEnabled(true);
                    player.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "✦ [Bruja Suprema] Color de partículas cambiado a: " + EnumChatFormatting.AQUA + SupremeWitchData.getColorName(parsed)));
                } else {
                    player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Color no reconocido. Opciones: verde, morado, azul, rojo, dorado, blanco, rosa, naranja, negro o código hex (#RRGGBB)."));
                }
                return;
            }

            if ((first.equals("cantidad") || first.equals("densidad") || first.equals("amount")) && args.length >= 3) {
                try {
                    int val = Integer.parseInt(args[2]);
                    val = Math.max(1, Math.min(10, val));
                    data.setAuraAmount(val);
                    data.setAuraEnabled(true);
                    player.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "✦ [Bruja Suprema] Densidad de partículas ajustada a: " + EnumChatFormatting.YELLOW + val + "/10" + EnumChatFormatting.GRAY + (val <= 2 ? " (Sutil)" : val <= 4 ? " (Moderada)" : " (Intensa)")));
                } catch (NumberFormatException e) {
                    player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Cantidad inválida. Introduce un número del 1 al 10."));
                }
                return;
            }

            // ¿Es número de cantidad directo? Ej: /suprema aura 5
            try {
                int val = Integer.parseInt(first);
                if (val >= 1 && val <= 10) {
                    data.setAuraAmount(val);
                    data.setAuraEnabled(true);
                    player.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "✦ [Bruja Suprema] Densidad de partículas ajustada a: " + EnumChatFormatting.YELLOW + val + "/10."));
                    return;
                }
            } catch (NumberFormatException ignored) {}

            // ¿Es color directo? Ej: /suprema aura morado  o  /suprema aura morado 5
            int directColor = SupremeWitchData.parseColor(first);
            if (directColor != -1) {
                data.setAuraColor(directColor);
                data.setAuraEnabled(true);
                if (args.length >= 3) {
                    try {
                        int val = Integer.parseInt(args[2]);
                        val = Math.max(1, Math.min(10, val));
                        data.setAuraAmount(val);
                        player.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "✦ [Bruja Suprema] Color: " + EnumChatFormatting.AQUA + SupremeWitchData.getColorName(directColor) + EnumChatFormatting.GOLD + " | Densidad: " + EnumChatFormatting.YELLOW + val + "/10."));
                        return;
                    } catch (NumberFormatException ignored) {}
                }
                player.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "✦ [Bruja Suprema] Color de partículas cambiado a: " + EnumChatFormatting.AQUA + SupremeWitchData.getColorName(directColor) + "."));
                return;
            }

            player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Uso: /suprema aura [on|off|<color>|<1-10>|color <nombre|hex>|cantidad <1-10>]"));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.GRAY + "Ejemplos: /suprema aura morado 5  |  /suprema aura cantidad 8  |  /suprema aura verde"));
            return;
        }

        if (sub.equals("status") || sub.equals("estado")) {
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.DARK_PURPLE + "╔════════════════════════════════════════════╗"));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "  ★ ESTADO DE LA BRUJA SUPREMA ★"));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.DARK_PURPLE + "╚════════════════════════════════════════════╝"));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.LIGHT_PURPLE + "Soberanía: " + (isSupreme ? EnumChatFormatting.GOLD + "ERES LA BRUJA SUPREMA" : EnumChatFormatting.GRAY + "No ostentas el Manto")));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.LIGHT_PURPLE + "Aura canalizadora: " + (SupremeWitchData.isSupremeAuraActive(player) ? EnumChatFormatting.GREEN + "Activada" : EnumChatFormatting.RED + "Desactivada") + EnumChatFormatting.GRAY + " (" + SupremeWitchData.getColorName(SupremeWitchData.getSupremeAuraColor(player)) + ", " + SupremeWitchData.getSupremeAuraAmount(player) + "/10)"));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.LIGHT_PURPLE + "Infusión actual: " + EnumChatFormatting.AQUA + Infusion.getInfusionID(player)));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.LIGHT_PURPLE + "Energía de infusión: " + EnumChatFormatting.GOLD + Infusion.getCurrentEnergy(player) + "/3000 (Regen. +15/s, -50% coste)"));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.LIGHT_PURPLE + "Puntuación de Maestría: " + EnumChatFormatting.GOLD + SupremeWitchData.calculateWitcheryMasteryScore(player) + " pts (Umbral auto: " + SupremeWitchData.SUPREME_MASTERY_THRESHOLD + ")"));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.LIGHT_PURPLE + "Las Siete Maravillas: " + EnumChatFormatting.YELLOW + SupremeWitchData.getCompletedWondersCount(player) + "/7 (/suprema maravillas)"));
            return;
        }

        player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Comando desconocido. Usa: /suprema <maravillas|claim|revoke|silence|infusion|beast|aura|status|guia>"));
    }

    private void sendWonderLine(EntityPlayer player, String key, String name, String desc) {
        boolean completed = SupremeWitchData.hasCompletedWonder(player, key);
        String mark = completed ? (EnumChatFormatting.GREEN + "  ✔ ") : (EnumChatFormatting.RED + "  ✖ ");
        player.addChatMessage(new ChatComponentText(mark + EnumChatFormatting.GOLD + name + EnumChatFormatting.GRAY + " — " + (completed ? EnumChatFormatting.WHITE : EnumChatFormatting.DARK_GRAY) + desc));
    }

    private void sendSupremeGuide(EntityPlayer player, int page) {
        if (page == 2) {
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.DARK_PURPLE + "╔════════════════════════════════════════════╗"));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "  ★ COMANDOS DE LA BRUJA SUPREMA (Pág. 2/2) ★"));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.DARK_PURPLE + "╚════════════════════════════════════════════╝"));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "/suprema silence <jugador> [seg] " + EnumChatFormatting.GRAY + "- Silencia la magia y varitas de un rival."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "/suprema infusion <luz|tierra|ender|infernal> " + EnumChatFormatting.GRAY + "- Conmuta afinidad activa (Reserva máxima: 3000)."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "/suprema beast <ghast|creeper|bat|spider|blaze> " + EnumChatFormatting.GRAY + "- Cargas del poder de bestia en tus manos."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "/suprema aura [on|off|color|cantidad] " + EnumChatFormatting.GRAY + "- Configura o personaliza el color y cantidad de partículas de tu altar viviente."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "/suprema status " + EnumChatFormatting.GRAY + "- Consulta tus dones y puntuación de maestría."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.AQUA + "Usa " + EnumChatFormatting.YELLOW + "/suprema guia 1" + EnumChatFormatting.AQUA + " para volver a los Dones Pasivos."));
        } else {
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.DARK_PURPLE + "╔════════════════════════════════════════════╗"));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "  ★ CODEX DE LA BRUJA SUPREMA (Pág. 1/2) ★"));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.DARK_PURPLE + "╚════════════════════════════════════════════╝"));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.LIGHT_PURPLE + "Ascenso automático al alcanzar " + EnumChatFormatting.GOLD + SupremeWitchData.SUPREME_MASTERY_THRESHOLD + "+ pts" + EnumChatFormatting.LIGHT_PURPLE + " de Maestría (Espíritu, Aquelarre, Familiar, Hechizos y derrotar a Baba Yaga):"));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "✦ Maestría de Rama Mística: " + EnumChatFormatting.WHITE + "Lanza cualquier hechizo (Luz, Tierra, Ender, Infernal) con 0% de fallo, -50% de coste de energía y -50% de enfriamiento."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "✦ Reserva Arcana Suprema: " + EnumChatFormatting.WHITE + "Capacidad de 3000 de energía con regeneración pasiva (+15/s) y sin pérdida de infusión al agotarse."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "✦ Altar Viviente y Descuento Ritual: " + EnumChatFormatting.WHITE + "Tus rituales consumen 50% menos poder de altar y canalizas hasta 4000 de poder móvil si no hay altar cerca."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "✦ Aquelarre e Inmunidad: " + EnumChatFormatting.WHITE + "Rituales con aquelarre completo (6/6), inmune a ser silenciada y sin restricciones lunares."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.AQUA + "Usa " + EnumChatFormatting.YELLOW + "/suprema guia 2" + EnumChatFormatting.AQUA + " para ver los comandos de combate y bestias."));
        }
    }
}
