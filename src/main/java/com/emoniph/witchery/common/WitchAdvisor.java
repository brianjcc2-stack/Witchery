package com.emoniph.witchery.common;

import com.emoniph.witchery.Witchery;
import com.emoniph.witchery.blocks.BlockAltar;
import com.emoniph.witchery.entity.EntityCovenWitch;
import com.emoniph.witchery.familiar.Familiar;
import com.emoniph.witchery.infusion.Infusion;
import com.emoniph.witchery.infusion.infusions.symbols.SymbolEffect;
import com.emoniph.witchery.integration.ModHookMCA;
import com.emoniph.witchery.util.SoundEffect;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import java.util.ArrayList;
import java.util.List;

/**
 * WitchAdvisor: El Oráculo y Motor de Diagnóstico Místico.
 * Evalúa el progreso completo del jugador en Witchery (Altar, Espíritu,
 * Vampirismo, Licantropía, Aquelarre, Familiares, Bruja Suprema y relación con Aldeas MCA)
 * y genera susurros personalizados para guiar el siguiente nivel de poder.
 */
public class WitchAdvisor {

    public static void whisperProgression(EntityPlayer player, String witchName) {
        if (player == null || player.worldObj == null) {
            return;
        }

        if (witchName == null || witchName.trim().isEmpty()) {
            witchName = "Susurro del Aquelarre";
        }

        ExtendedPlayer playerEx = ExtendedPlayer.get(player);
        List<String> adviceList = new ArrayList<String>();

        // 1. Diagnóstico de Bruja Suprema
        boolean isSupreme = SupremeWitchData.isPlayerSupreme(player);
        int masteryScore = SupremeWitchData.calculateWitcheryMasteryScore(player);

        if (isSupreme) {
            adviceList.add("§6★ ¡Ostentas el manto de Bruja Suprema! §dTu poder resuena en todo el plano (" + masteryScore + " pts). Mantén tu liderazgo cumpliendo ritos y defendiendo el aquelarre.");
        } else {
            SupremeWitchData data = SupremeWitchData.get(player.worldObj);
            if (data != null && data.hasSupreme()) {
                adviceList.add("§dLa Bruja Suprema actual domina el aquelarre. Posees §6" + masteryScore + " pts§d de maestría; expande tus ritos y poderes de espíritu para arrebatarle el trono.");
            }
        }

        // 2. Diagnóstico de Nivel de Espíritu (0 a 10)
        int spiritLevel = playerEx != null ? playerEx.getSpiritLevel() : 0;
        if (spiritLevel == 0) {
            adviceList.add("§5[Espíritu: Inerte] §dTu conexión con el Más Allá duerme. Elabora una Pócima del Mundo de los Sueños (Brew of Sleeping) y bebe cerca de un Atrapasueños para cruzar el velo hacia el Reino Onírico.");
        } else if (spiritLevel == 1 || spiritLevel == 2) {
            adviceList.add("§5[Espíritu: Nivel " + spiritLevel + "] §dPara despertar al Nivel 3 y dominar la forma de Orbe Espiritual ('ex subduo'), debes invocar y someter a un espíritu atormentado usando un Círculo de Protección y un Frasco de Arcilla.");
        } else if (spiritLevel == 3) {
            adviceList.add("§5[Espíritu: Nivel 3] §dYa puedes flotar como orbe ('ex subduo'). Para alcanzar el Nivel 4 y liberar tu Forma Fantasmal completa ('ex spiritus'), debes enfrentar y derrotar a una Pesadilla (Nightmare) en lo profundo de los sueños.");
        } else if (spiritLevel < 10) {
            adviceList.add("§5[Espíritu: Nivel " + spiritLevel + "] §dTu alma espectral se fortalece. Purifica almas errantes (Lost Souls) en tus círculos rituales para alcanzar la trascendencia espiritual (Nivel 10).");
        } else {
            adviceList.add("§5[Espíritu: Trascendente] §a¡Has alcanzado el Nivel 10 de Espíritu! Puedes viajar entre dimensiones etéreas a voluntad.");
        }

        // 3. Diagnóstico de Aquelarre (Coven)
        int covenSize = EntityCovenWitch.getCovenSize(player);
        if (covenSize == 0) {
            adviceList.add("§9[Aquelarre: Solitaria] §dNo tienes ninguna bruja aliada. Adéntrate en los pantanos y bosques profundos, habla con brujas solitarias y cumple su pacto para unirlas a tu círculo.");
        } else if (covenSize < 6) {
            adviceList.add("§9[Aquelarre: " + covenSize + "/6 Hermanas] §dTu círculo cuenta con " + covenSize + " brujas. Recluta hasta 6 hermanas para desatar los Grandes Ritos Celestiales (Tormentas eternas, Eclipses y Nigromancia).");
        } else {
            adviceList.add("§9[Aquelarre: Círculo Completo] §a¡Tu aquelarre de 6 hermanas está completo! Invócalas en tus círculos de tiza para multiplicar por diez el poder de tus ritos.");
        }

        // 4. Diagnóstico de Familiar
        String familiar = Familiar.getFamiliarName(player);
        if (familiar == null || familiar.isEmpty()) {
            adviceList.add("§2[Familiar: Ausente] §dCaminas sin guía animal. Realiza el Rito de Atadura (Rite of Binding) con un gato, sapo o búho para obtener protección contra maleficios y visión nocturna.");
        } else {
            adviceList.add("§2[Familiar: Vinculado] §aTu compañero animal (" + familiar + ") vigila tus espaldas y canaliza la energía de la naturaleza hacia tus venas.");
        }

        // 5. Diagnóstico de Vampirismo y Licantropía
        int vampLevel = playerEx != null ? playerEx.getVampireLevel() : 0;
        int wolfLevel = playerEx != null ? playerEx.getWerewolfLevel() : 0;

        if (vampLevel > 0 && vampLevel < 10) {
            adviceList.add("§c[Vampiro: Rango " + vampLevel + "] §dLilith aguarda en el Cáliz. Alimenta tu sed bebiendo sangre nocturna de aldeanos dormidos o realiza los tributos oscuros en el ataúd para subir al Rango " + (vampLevel + 1) + ".");
        }
        if (wolfLevel > 0 && wolfLevel < 10) {
            adviceList.add("§6[Licántropo: Rango " + wolfLevel + "] §dLa bestia reclama caza. Haz sonar el Cuerno de Caza durante la Luna Llena y devora carne viva para despertar el Rango " + (wolfLevel + 1) + ".");
        }

        // 6. Diagnóstico de Hechizos con la Varita (Mystic Branch)
        String knowledge = SymbolEffect.getKnowledge(player);
        int spellCount = (knowledge != null && !knowledge.isEmpty()) ? knowledge.split(",").length : 0;
        if (spellCount < 5) {
            adviceList.add("§b[Rama Mística: " + spellCount + " símbolos] §dTu varita apenas canaliza los trazos arcanos. Dibuja los símbolos antiguos en el aire sobre tus círculos para aprender nuevos hechizos.");
        }

        // 7. Diagnóstico de Aldeas y Aldeanos MCA
        if (ModHookMCA.isMCAPresent()) {
            if (ModHookMCA.isTerrorizingVillage(player)) {
                adviceList.add("§4[Aldea MCA: Alerta de Caza] §cLos aldeanos te temen o han visto tus actos oscuros. Los Cazadores de Brujas (Witch Hunters) podrían organizar una purga pronto. Caliéntate con pociones de paz o cúralos.");
            } else {
                adviceList.add("§a[Aldea MCA: Relación Estable] §dPuedes usar el aquelarre para comerciar brebajes, sanar aldeanos enfermos con 'Odour of Purity' o reclamar tributos de amor.");
            }
        }

        // Enviar encabezado místico con sonido
        SoundEffect.WITCHERY_RANDOM_HYPNOSIS.playAtPlayer(player.worldObj, player, 0.6F, 1.2F);
        player.addChatMessage(new ChatComponentText("§5§m-----------------------------------------------------"));
        player.addChatMessage(new ChatComponentText("§5§l🔮 [Voz Mística: " + witchName + "]"));
        player.addChatMessage(new ChatComponentText("§7§o\"Escucha los susurros del velo... esto es lo que tu alma necesita para ascender:\""));

        for (String line : adviceList) {
            player.addChatMessage(new ChatComponentText("  §7• " + line));
        }

        player.addChatMessage(new ChatComponentText("§5§m-----------------------------------------------------"));
    }
}
