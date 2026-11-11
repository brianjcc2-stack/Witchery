package com.emoniph.witchery.brewing.action.effect;

import com.emoniph.witchery.Witchery;
import com.emoniph.witchery.brewing.AltarPower;
import com.emoniph.witchery.brewing.BrewItemKey;
import com.emoniph.witchery.brewing.BrewNamePart;
import com.emoniph.witchery.brewing.EffectLevel;
import com.emoniph.witchery.brewing.ModifiersEffect;
import com.emoniph.witchery.brewing.Probability;
import com.emoniph.witchery.brewing.action.BrewActionEffect;
import com.emoniph.witchery.util.ParticleEffect;
import com.emoniph.witchery.util.SoundEffect;
import com.emoniph.witchery.util.TimeUtil;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

public class BrewActionPolymorph extends BrewActionEffect {

    private final int form;

    public BrewActionPolymorph(BrewItemKey itemKey, BrewNamePart namePart, AltarPower powerCost, Probability baseProbability, EffectLevel effectLevel, int form) {
        super(itemKey, namePart, powerCost, baseProbability, effectLevel);
        this.form = form;
    }

    @Override
    protected void doApplyToEntity(World world, EntityLivingBase targetEntity, ModifiersEffect modifiers, ItemStack stack) {
        if (!world.isRemote && targetEntity != null && !modifiers.disableEntityTarget) {
            int duration = TimeUtil.secsToTicks(30 + modifiers.getStrength() * 15);
            targetEntity.addPotionEffect(new PotionEffect(Witchery.Potions.POLYMORPH.id, duration, this.form, false));
            ParticleEffect.MAGIC_CRIT.send(SoundEffect.RANDOM_POP, targetEntity, 1.0D, 1.0D, 16);
        }
    }
}

