package pokeregions.monsters.act3.enemies.rayquaza;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import pokeregions.BetterSpriterAnimation;
import pokeregions.cards.pokemonAllyCards.act3.Salamence;
import pokeregions.monsters.AbstractPokemonMonster;
import pokeregions.monsters.act3.enemies.SalamenceEnemy;
import pokeregions.powers.AbstractLambdaPower;
import pokeregions.util.Wiz;

import static pokeregions.PokemonRegions.makeID;
import static pokeregions.PokemonRegions.makeMonsterPath;
import static pokeregions.util.Wiz.*;

public class SalamenceR extends AbstractPokemonMonster
{
    public static final String ID = makeID(SalamenceEnemy.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;

    private static final byte DRAGON_RUSH = 0;
    private static final byte DRAGON_CLAW = 1;

    public final int STR = calcAscensionSpecial(1);

    public static final String POWER_ID = makeID("Moxie");
    public static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String POWER_NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public SalamenceR() {
        this(0.0f, 0.0f);
    }

    public SalamenceR(final float x, final float y) {
        super(NAME, ID, 140, 0.0F, 0, 230.0f, 200.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Salamence/Salamence.scml"));
        ((BetterSpriterAnimation)this.animation).myPlayer.setScale(Settings.scale * 1.5f);
        setHp(calcAscensionTankiness(150));
        addMove(DRAGON_RUSH, Intent.ATTACK, calcAscensionDamage(6), 2);
        addMove(DRAGON_CLAW, Intent.ATTACK, calcAscensionDamage(15));
    }

    @Override
    protected void setUpMisc() {
        super.setUpMisc();
        this.type = EnemyType.BOSS;
    }

    @Override
    public void usePreBattleAction() {
        super.usePreBattleAction();
        applyToTarget(this, this, new AbstractLambdaPower(POWER_ID, POWER_NAME, AbstractPower.PowerType.BUFF, false, this, STR, "berserk") {

            @Override
            public void onInflictDamage(DamageInfo info, int damageAmount, AbstractCreature target) {
                if (damageAmount > 0 && info.type == DamageInfo.DamageType.NORMAL) {
                    applyToTarget(owner, owner, new StrengthPower(owner, amount));
                }
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
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (lastMove(DRAGON_RUSH)) {
            setMoveShortcut(DRAGON_CLAW, MOVES[DRAGON_CLAW]);
        } else {
            setMoveShortcut(DRAGON_RUSH, MOVES[DRAGON_RUSH]);
        }
        super.postGetMove();
    }

    @Override
    public void die(boolean triggerRelics) {
        super.die(triggerRelics);
        AbstractPower str = this.getPower(StrengthPower.POWER_ID);
        if (str != null && str.amount > 0) {
            for (AbstractMonster mo : Wiz.getEnemies()) {
                if (mo instanceof RayquazaEnemy) {
                    applyToTarget(mo, this, new StrengthPower(mo, str.amount));
                }
            }
        }
        if (AbstractDungeon.getMonsters().areMonstersBasicallyDead()) {
            onBossVictoryLogic();
            onFinalBossVictoryLogic();
        }
    }

    @Override
    public AbstractCard getAssociatedPokemonCard() {
        return new Salamence();
    }

}