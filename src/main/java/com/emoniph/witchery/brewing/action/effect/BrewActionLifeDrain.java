package com.emoniph.witchery.brewing.action.effect;

import com.emoniph.witchery.brewing.AltarPower;
import com.emoniph.witchery.brewing.BrewItemKey;
import com.emoniph.witchery.brewing.BrewNamePart;
import com.emoniph.witchery.brewing.EffectLevel;
import com.emoniph.witchery.brewing.ModifiersEffect;
import com.emoniph.witchery.brewing.Probability;
import com.emoniph.witchery.brewing.action.BrewActionEffect;
import com.emoniph.witchery.util.ParticleEffect;
import com.emoniph.witchery.util.SoundEffect;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class BrewActionLifeDrain extends BrewActionEffect {

    public BrewActionLifeDrain(BrewItemKey itemKey, BrewNamePart namePart, AltarPower powerCost, Probability baseProbability, EffectLevel effectLevel) {
        super(itemKey, namePart, powerCost, baseProbability, effectLevel);
    }

    @Override
    protected void doApplyToEntity(World world, EntityLivingBase targetEntity, ModifiersEffect modifiers, ItemStack stack) {
        if (!world.isRemote && targetEntity != null) {
            float damage = 6.0F + (float)modifiers.getStrength() * 3.0F;
            targetEntity.attackEntityFrom(DamageSource.magic, damage);
            if (modifiers.caster != null && !modifiers.caster.isDead && modifiers.caster != targetEntity) {
                modifiers.caster.heal(damage * 0.75F);
                ParticleEffect.HEART.send(SoundEffect.RANDOM_ORB, modifiers.caster, 1.0D, 1.5D, 10);
            }
            ParticleEffect.SPELL_COLORED.send(SoundEffect.RANDOM_ORB, targetEntity, 1.0D, 1.5D, 15);
        }
    }
}

