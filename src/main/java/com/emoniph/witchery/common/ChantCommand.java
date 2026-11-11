package com.emoniph.witchery.common;

import com.emoniph.witchery.Witchery;
import com.emoniph.witchery.util.ChatUtil;
import java.util.ArrayList;
import java.util.List;
import joptsimple.internal.Strings;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

public class ChantCommand implements ICommand {

   private final List aliases = new ArrayList();


   public ChantCommand() {
      this.aliases.add(this.getCommandName());
      this.aliases.add("cantar");
      this.aliases.add("recitar");
   }

   public String getCommandName() {
      return "chant";
   }

   public String getCommandUsage(ICommandSender icommandsender) {
      return this.getCommandName() + " <message>";
   }

   public List getCommandAliases() {
      return this.aliases;
   }

   public void processCommand(ICommandSender sender, String[] args) {
      if(args.length == 0 || args[0].equalsIgnoreCase("guia") || args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("ayuda")) {
         ChatUtil.sendPlain(EnumChatFormatting.DARK_PURPLE, sender, "=== Guía de Cánticos Místicos (/chant) ===");
         ChatUtil.sendPlain(EnumChatFormatting.YELLOW, sender, "• /chant ex subduo" + EnumChatFormatting.WHITE + " - Alterna Forma de Orbe Espiritual Subyugado (Espíritu Nvl 3+).");
         ChatUtil.sendPlain(EnumChatFormatting.YELLOW, sender, "• /chant ex humanus" + EnumChatFormatting.WHITE + " - Regresa a tu forma terrenal humana.");
         ChatUtil.sendPlain(EnumChatFormatting.YELLOW, sender, "• /chant ex corpus" + EnumChatFormatting.WHITE + " - Activa o finaliza la Proyección Astral (Espíritu Nvl 10).");
         ChatUtil.sendPlain(EnumChatFormatting.YELLOW, sender, "• /chant there's no place like home" + EnumChatFormatting.WHITE + " - Teletransporte a casa con Zapatillas de Rubí.");
         ChatUtil.sendPlain(EnumChatFormatting.YELLOW, sender, "• /chant mirror mirror send me home" + EnumChatFormatting.WHITE + " - Regresa a través de un Espejo Mágico.");
         ChatUtil.sendPlain(EnumChatFormatting.YELLOW, sender, "• /chant <destino>" + EnumChatFormatting.WHITE + " - Viaja por la Red Flu (estando dentro de Fuego Flu).");
      } else {
         World world = sender.getEntityWorld();
         if(world != null) {
            String strings = Strings.join(args, " ");
            EntityPlayer player = world.getPlayerEntityByName(sender.getCommandSenderName());
            if(player != null) {
               if(com.emoniph.witchery.common.ExtendedPlayer.trySayAstralProjection(player, strings)) {
                  return;
               }

               if(com.emoniph.witchery.common.ExtendedPlayer.trySaySpiritForm(player, strings)) {
                  return;
               }

               if(Witchery.Items.RUBY_SLIPPERS.trySayTheresNoPlaceLikeHome(player, strings)) {
                  return;
               }

               if(Witchery.Blocks.MIRROR.trySayMirrorMirrorSendMeHome(player, strings)) {
                  return;
               }

               if(com.emoniph.witchery.util.FlooNetwork.isEntityInFlooFire(player)) {
                  com.emoniph.witchery.util.FlooNetwork.travelFlooNetwork(player, strings);
                  return;
               }
            }
         }

         ChatUtil.sendTranslated(EnumChatFormatting.RED, sender, "witchery.rite.unknownchant", new Object[0]);
      }
   }

   public boolean canCommandSenderUseCommand(ICommandSender sender) {
      return true;
   }

   public List addTabCompletionOptions(ICommandSender icommandsender, String[] astring) {
      return null;
   }

   public boolean isUsernameIndex(String[] astring, int i) {
      return false;
   }

   public int compareTo(Object arg0) {
      return 0;
   }
}
