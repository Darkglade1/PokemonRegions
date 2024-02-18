package pokeregions.monsters.act3.enemies;

import basemod.ReflectionHacks;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.actions.unique.VampireDamageAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.status.Burn;
import com.megacrit.cardcrawl.cards.status.VoidCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import pokeregions.BetterSpriterAnimation;
import pokeregions.PokemonRegions;
import pokeregions.cards.pokemonAllyCards.act3.Breloom;
import pokeregions.monsters.AbstractPokemonMonster;
import pokeregions.powers.AbstractLambdaPower;
import pokeregions.powers.NastyPlot;
import pokeregions.util.Details;

import java.util.ArrayList;

import static pokeregions.PokemonRegions.*;
import static pokeregions.util.Wiz.*;

public class LunatoneEnemy extends AbstractPokemonMonster
{
    public static final String ID = makeID(LunatoneEnemy.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;

    private static final byte CALM_MIND = 0;
    private static final byte POWER_GEM = 1;
    private static final byte DREAM_EATER = 2;
    private static final byte EVENING_MOONLIGHT = 3;

    public final int STR = calcAscensionSpecial(2);
    public final int BUFF = calcAscensionSpecialSmall(3);
    public final int STATUS = calcAscensionSpecial(2);
    public final int BLOCK = 8;

    public boolean advent = false;
    private SolrockEnemy solrock;

    public static final String POWER_ID = makeID("MoonAdvent");
    public static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String POWER_NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public LunatoneEnemy() {
        this(0.0f, 0.0f);
    }

    public LunatoneEnemy(final float x, final float y) {
        super(NAME, ID, 140, 0.0F, 0, 150.0f, 170.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Lunatone/Lunatone.scml"));
        ((BetterSpriterAnimation)this.animation).myPlayer.setScale(Settings.scale * 1.2f);
        setHp(calcAscensionTankiness(106), calcAscensionTankiness(118));
        addMove(CALM_MIND, Intent.DEFEND_BUFF);
        addMove(POWER_GEM, Intent.ATTACK, calcAscensionDamage(16));
        addMove(DREAM_EATER, Intent.STRONG_DEBUFF);
        addMove(EVENING_MOONLIGHT, Intent.ATTACK_BUFF, calcAscensionDamage(26));
    }

    @Override
    public void usePreBattleAction() {
        super.usePreBattleAction();
        for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (mo instanceof SolrockEnemy) {
                solrock = (SolrockEnemy)mo;
            }
        }
        applyToTarget(this, this, new AbstractLambdaPower(POWER_ID, POWER_NAME, AbstractPower.PowerType.BUFF, false, this, STR) {

            @Override
            public void atEndOfRound() {
                if (advent) {
                    this.flash();
                    applyToTarget(owner, owner, new StrengthPower(owner, amount));
                }
            }

            @Override
            public void updateDescription() {
                if (advent) {
                    description = POWER_DESCRIPTIONS[1] + amount + POWER_DESCRIPTIONS[2];
                } else {
                    description = POWER_DESCRIPTIONS[0];
                }
            }
        });
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        if(info.base > -1) {
            info.applyPowers(this, adp());
        }

        switch (this.nextMove) {
            case CALM_MIND:
                block(this, BLOCK);
                applyToTarget(this, this, new NastyPlot(this, BUFF));
                break;
            case POWER_GEM:
                useFastAttackAnimation();
                dmg(adp(), info, AbstractGameAction.AttackEffect.BLUNT_LIGHT);
                break;
            case DREAM_EATER:
                intoDrawMo(new VoidCard(), STATUS);
                break;
            case EVENING_MOONLIGHT: {
                useFastAttackAnimation();
                atb(new VampireDamageAction(adp(), info, AbstractGameAction.AttackEffect.POISON));
                break;
            }
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (this.advent) {
            if (this.lastMove(EVENING_MOONLIGHT)) {
                setMoveShortcut(DREAM_EATER, MOVES[DREAM_EATER]);
            } else {
                setMoveShortcut(EVENING_MOONLIGHT, MOVES[EVENING_MOONLIGHT]);
            }
        } else {
            if (this.lastMove(CALM_MIND)) {
                setMoveShortcut(POWER_GEM, MOVES[POWER_GEM]);
            } else {
                setMoveShortcut(CALM_MIND, MOVES[CALM_MIND]);
            }
        }
        super.postGetMove();
    }

    @Override
    public void die(boolean triggerRelics) {
        super.die(triggerRelics);
        if (solrock != null && !solrock.isDeadOrEscaped()) {
            solrock.advent = true;
            AbstractPower power = solrock.getPower(SolrockEnemy.POWER_ID);
            if (power != null) {
                power.flash();
                power.updateDescription();
            }
        }
    }

    protected void setDetailedIntents() {
        ArrayList<Details> details = new ArrayList<>();
        EnemyMoveInfo move = ReflectionHacks.getPrivate(this, AbstractMonster.class, "move");
        switch (move.nextMove) {
            case CALM_MIND: {
                Details blockDetail = new Details(this, BLOCK, BLOCK_TEXTURE);
                details.add(blockDetail);
                Details powerDetail = new Details(this, BUFF, NASTY_PLOT_TEXTURE);
                details.add(powerDetail);
                break;
            }
            case DREAM_EATER: {
                Details statusDetail = new Details(this, STATUS, VOID_TEXTURE, Details.TargetType.DRAW_PILE);
                details.add(statusDetail);
                break;
            }
            case EVENING_MOONLIGHT: {
                Details moveDetail = new Details(this, Details.LIFESTEAL);
                details.add(moveDetail);
                break;
            }
        }
        PokemonRegions.intents.put(this, details);
    }

    @Override
    public AbstractCard getAssociatedPokemonCard() {
        return new Breloom();
    }

}