package pokeregions.monsters.act2.enemies;

import com.brashmonkey.spriter.Player;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.ArtifactPower;
import pokeregions.BetterSpriterAnimation;
import pokeregions.cards.pokemonAllyCards.act2.Mantine;
import pokeregions.monsters.AbstractPokemonMonster;
import pokeregions.powers.AbstractLambdaPower;
import pokeregions.powers.NastyPlot;
import pokeregions.util.Wiz;

import static pokeregions.PokemonRegions.makeID;
import static pokeregions.PokemonRegions.makeMonsterPath;
import static pokeregions.util.Wiz.*;

public class MantineEnemy extends AbstractPokemonMonster
{
    public static final String ID = makeID(MantineEnemy.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;

    private static final byte RAIN_DANCE = 0;
    private static final byte AIR_SLASH = 1;

    public final int BUFF = calcAscensionSpecial(2);
    public final int POWER_AMT = 1;

    public static final String POWER_ID = makeID("WaterVeil");
    public static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String POWER_NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public MantineEnemy() {
        this(0.0f, 0.0f);
    }

    public MantineEnemy(final float x, final float y) {
        super(NAME, ID, 140, 0.0F, 0, 160.0f, 100.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Mantine/Mantine.scml"));
        this.type = EnemyType.NORMAL;
        setHp(calcAscensionTankiness(55), calcAscensionTankiness(62));
        addMove(RAIN_DANCE, Intent.BUFF);
        addMove(AIR_SLASH, Intent.ATTACK, calcAscensionDamage(11));

        Player.PlayerListener listener = new PokemonListener(this);
        ((BetterSpriterAnimation)this.animation).myPlayer.addListener(listener);
    }

    @Override
    public void usePreBattleAction() {
        super.usePreBattleAction();
        applyToTarget(this, this, new AbstractLambdaPower(POWER_ID, POWER_NAME, AbstractPower.PowerType.BUFF, false, this, POWER_AMT, "like_water") {

            @Override
            public void onInitialApplication() {
                this.flash();
                for (AbstractMonster mo : Wiz.getEnemies()) {
                    if (!mo.hasPower(ArtifactPower.POWER_ID)) {
                        applyToTarget(mo, owner, new ArtifactPower(mo, amount));
                    }
                }
            }

            @Override
            public void atEndOfRound() {
                boolean triggered = false;
                for (AbstractMonster mo : Wiz.getEnemies()) {
                    if (!mo.hasPower(ArtifactPower.POWER_ID)) {
                        if (!triggered) {
                            this.flash();
                        }
                        applyToTarget(mo, owner, new ArtifactPower(mo, amount));
                        triggered = true;
                    }
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
            case RAIN_DANCE: {
                for (AbstractMonster mo : Wiz.getEnemies()) {
                    applyToTarget(mo, this, new NastyPlot(mo, BUFF));
                }
                break;
            }
            case AIR_SLASH: {
                useFastAttackAnimation();
                dmg(adp(), info, AbstractGameAction.AttackEffect.SLASH_DIAGONAL);
                break;
            }
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (this.lastMove(AIR_SLASH)) {
            setMoveShortcut(RAIN_DANCE, MOVES[RAIN_DANCE]);
        } else {
            setMoveShortcut(AIR_SLASH, MOVES[AIR_SLASH]);
        }
        super.postGetMove();
    }

    @Override
    public AbstractCard getAssociatedPokemonCard() {
        return new Mantine();
    }

}