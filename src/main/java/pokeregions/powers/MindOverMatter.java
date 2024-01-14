package pokeregions.powers;

import com.megacrit.cardcrawl.actions.watcher.ChooseOneAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import pokeregions.PokemonRegions;
import pokeregions.cards.FutureSight;

import java.util.ArrayList;

import static pokeregions.util.Wiz.atb;

public class MindOverMatter extends AbstractUnremovablePower {
    public static final String POWER_ID = PokemonRegions.makeID(MindOverMatter.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public boolean aboutToTrigger = false;
    private int damage;
    private final int damageIncrease;
    private final int damagePerTurn = 5;

    public MindOverMatter(AbstractCreature owner, int baseDamage, int damageIncrease) {
        super(POWER_ID, NAME, PowerType.BUFF, false, owner, 0);
        this.damage = baseDamage;
        this.damageIncrease = damageIncrease;
        this.loadRegion("fasting");
        updateDescription();
    }

    @Override
    public void duringTurn() {
        if (!aboutToTrigger) {
            aboutToTrigger = true;
        } else {
            aboutToTrigger = false;
            ArrayList<AbstractCard> options = new ArrayList<>();
            options.add(new FutureSight(damage, 0, owner));
            int turn = 1;
            options.add(new FutureSight(getFutureSightDamage(turn), turn, owner));
            turn = 2;
            options.add(new FutureSight(getFutureSightDamage(turn), turn, owner));
            atb(new ChooseOneAction(options));
            damage += damageIncrease;
            updateDescription();
        }
    }

    private int getFutureSightDamage(int turn) {
        return  damage + (damagePerTurn * turn);
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0] + damageIncrease + DESCRIPTIONS[1] + damage + "/" + getFutureSightDamage(1) + "/" + getFutureSightDamage(2) + DESCRIPTIONS[2];
    }
}
