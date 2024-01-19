package pokeregions.powers;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import pokeregions.PokemonRegions;
import pokeregions.monsters.AbstractPokemonAlly;
import pokeregions.util.Wiz;

import static pokeregions.util.Wiz.atb;

public class Courage extends AbstractUnremovablePower {
    public static final String POWER_ID = PokemonRegions.makeID(Courage.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public Courage(AbstractCreature owner, int amount) {
        super(POWER_ID, NAME, PowerType.BUFF, false, owner, amount);
        this.priority = 99;
    }

    @Override
    public float atDamageGive(float damage, DamageInfo.DamageType type) {
        if (type == DamageInfo.DamageType.NORMAL) {
            return damage * (1 + ((float)amount / 100));
        } else {
            return damage;
        }
    }

    @Override
    public void onAttack(DamageInfo info, int damageAmount, AbstractCreature target) {
        if (info.type == DamageInfo.DamageType.NORMAL && info.owner != null) {
            Wiz.makePowerRemovable(this);
            if (owner instanceof AbstractPokemonAlly) {
                atb(new AbstractGameAction() {
                    @Override
                    public void update() {
                        owner.halfDead = false;
                        this.isDone = true;
                    }
                });
            }
            addToBot(new RemoveSpecificPowerAction(owner, owner, this));
            if (owner instanceof AbstractPokemonAlly) {
                atb(new AbstractGameAction() {
                    @Override
                    public void update() {
                        owner.halfDead = true;
                        this.isDone = true;
                    }
                });
            }
        }
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0] + amount + DESCRIPTIONS[1];
    }
}
