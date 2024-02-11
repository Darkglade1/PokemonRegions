package pokeregions.monsters.act3.enemies;

import basemod.ReflectionHacks;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.status.Burn;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.FrailPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import pokeregions.BetterSpriterAnimation;
import pokeregions.PokemonRegions;
import pokeregions.cards.pokemonAllyCards.act3.Salamence;
import pokeregions.monsters.AbstractPokemonMonster;
import pokeregions.powers.AbstractLambdaPower;
import pokeregions.util.Details;

import java.util.ArrayList;

import static pokeregions.PokemonRegions.*;
import static pokeregions.util.Wiz.*;

public class SalamenceEnemy extends AbstractPokemonMonster
{
    public static final String ID = makeID(SalamenceEnemy.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;

    private static final byte DRAGON_RUSH = 0;
    private static final byte DRAGON_CLAW = 1;
    private static final byte DRAGON_BREATH = 2;

    public final int STR = 3;
    public final int DEBUFF = 1;
    public final int STATUS = calcAscensionSpecial(1);

    public static final String POWER_ID = makeID("Moxie");
    public static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String POWER_NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    private boolean triggeredPower = false;
    private final AbstractCard status = new Burn();

    public SalamenceEnemy() {
        this(0.0f, 0.0f);
    }

    public SalamenceEnemy(final float x, final float y) {
        super(NAME, ID, 140, 0.0F, 0, 230.0f, 200.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Salamence/Salamence.scml"));
        ((BetterSpriterAnimation)this.animation).myPlayer.setScale(Settings.scale * 1.5f);
        setHp(calcAscensionTankiness(300));
        addMove(DRAGON_RUSH, Intent.ATTACK, calcAscensionDamage(12), 2);
        addMove(DRAGON_CLAW, Intent.ATTACK, calcAscensionDamage(30));
        addMove(DRAGON_BREATH, Intent.DEBUFF);
        if (AbstractDungeon.ascensionLevel >= 18) {
            status.upgrade();
        }
    }

    @Override
    protected void setUpMisc() {
        super.setUpMisc();
        this.type = EnemyType.ELITE;
    }

    @Override
    public void usePreBattleAction() {
        super.usePreBattleAction();
        applyToTarget(this, this, new AbstractLambdaPower(POWER_ID, POWER_NAME, AbstractPower.PowerType.BUFF, false, this, STR, "berserk") {

            @Override
            public void onInflictDamage(DamageInfo info, int damageAmount, AbstractCreature target) {
                if (damageAmount > 0 && info.type == DamageInfo.DamageType.NORMAL) {
                    applyToTarget(owner, owner, new StrengthPower(owner, amount));
                    triggeredPower = true;
                }
            }

            @Override
            public void atEndOfRound() {
                triggeredPower = false;
            }

            @Override
            public void updateDescription() {
                description = POWER_DESCRIPTIONS[0] + amount + POWER_DESCRIPTIONS[1];
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
            case DRAGON_RUSH: {
                useFastAttackAnimation();
                for (int i = 0; i < multiplier; i++) {
                    dmg(adp(), info, AbstractGameAction.AttackEffect.BLUNT_HEAVY);
                }
                break;
            }
            case DRAGON_CLAW: {
                useFastAttackAnimation();
                dmg(adp(), info, AbstractGameAction.AttackEffect.SLASH_HEAVY);
                break;
            }
            case DRAGON_BREATH: {
                intoDiscardMo(status.makeStatEquivalentCopy(), STATUS);
                applyToTarget(adp(), this, new VulnerablePower(adp(), DEBUFF, true));
                applyToTarget(adp(), this, new FrailPower(adp(), DEBUFF, true));
                break;
            }
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (firstMove) {
            setMoveShortcut(DRAGON_RUSH, MOVES[DRAGON_RUSH]);
        } else if (!triggeredPower && !lastMove(DRAGON_BREATH)) {
            setMoveShortcut(DRAGON_BREATH, MOVES[DRAGON_BREATH]);
        } else if (lastMove(DRAGON_BREATH) || lastMove(DRAGON_RUSH)) {
            setMoveShortcut(DRAGON_CLAW, MOVES[DRAGON_CLAW]);
        } else {
            setMoveShortcut(DRAGON_RUSH, MOVES[DRAGON_RUSH]);
        }
        super.postGetMove();
    }

    protected void setDetailedIntents() {
        ArrayList<Details> details = new ArrayList<>();
        EnemyMoveInfo move = ReflectionHacks.getPrivate(this, AbstractMonster.class, "move");
        switch (move.nextMove) {
            case DRAGON_BREATH: {
                Details statusDetail = new Details(this, STATUS, BURN_TEXTURE, Details.TargetType.DISCARD_PILE);
                details.add(statusDetail);
                Details vulnerableDetail = new Details(this, DEBUFF, VULNERABLE_TEXTURE);
                details.add(vulnerableDetail);
                Details powerDetail = new Details(this, DEBUFF, FRAIL_TEXTURE);
                details.add(powerDetail);
                break;
            }
        }
        PokemonRegions.intents.put(this, details);
    }

    @Override
    public AbstractCard getAssociatedPokemonCard() {
        return new Salamence();
    }

}