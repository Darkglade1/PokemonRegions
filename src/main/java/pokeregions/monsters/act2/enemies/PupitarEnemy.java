package pokeregions.monsters.act2.enemies;

import basemod.ReflectionHacks;
import com.brashmonkey.spriter.Player;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.FrailPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import pokeregions.BetterSpriterAnimation;
import pokeregions.PokemonRegions;
import pokeregions.cards.pokemonAllyCards.act2.Pupitar;
import pokeregions.monsters.AbstractPokemonMonster;
import pokeregions.util.Details;
import pokeregions.util.Wiz;

import java.util.ArrayList;

import static pokeregions.PokemonRegions.*;
import static pokeregions.util.Wiz.*;

public class PupitarEnemy extends AbstractPokemonMonster
{
    public static final String ID = makeID(PupitarEnemy.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;

    private static final byte DANCE = 0;
    private static final byte SMACK_DOWN = 1;
    private static final byte ROCK_SLIDE = 2;

    public final int DEBUFF = calcAscensionSpecial(1);
    public final int STR = calcAscensionSpecial(2);
    private TyranitarEnemy parent;

    private boolean attackFirst;

    public PupitarEnemy() {
        this(0.0f, 0.0f, false);
    }

    public PupitarEnemy(final float x, final float y, boolean attackFirst) {
        super(NAME, ID, 140, 0.0F, 0, 130.0f, 110.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Pupitar/Pupitar.scml"));
        ((BetterSpriterAnimation)this.animation).myPlayer.speed = 10;
        int time = ((BetterSpriterAnimation)this.animation).myPlayer.getAnimation().length;
        ((BetterSpriterAnimation)this.animation).myPlayer.setTime((int)(time * Math.random()));
        this.attackFirst = attackFirst;

        setHp(calcAscensionTankiness(60), calcAscensionTankiness(68));
        addMove(DANCE, Intent.BUFF);
        addMove(SMACK_DOWN, Intent.ATTACK_DEBUFF, calcAscensionDamage(9));
        addMove(ROCK_SLIDE, Intent.ATTACK, calcAscensionDamage(6), 2);

        Player.PlayerListener listener = new PokemonListener(this);
        ((BetterSpriterAnimation)this.animation).myPlayer.addListener(listener);
    }

    @Override
    protected void setUpMisc() {
        super.setUpMisc();
        this.type = EnemyType.ELITE;
    }

    @Override
    public void usePreBattleAction() {
        super.usePreBattleAction();
        for (AbstractMonster mo : Wiz.getEnemies()) {
            if (mo instanceof TyranitarEnemy) {
                parent = (TyranitarEnemy) mo;
            }
        }
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        if(info.base > -1) {
            info.applyPowers(this, adp());
        }

        switch (this.nextMove) {
            case DANCE: {
                for (AbstractMonster mo : Wiz.getEnemies()) {
                    applyToTarget(mo, this, new StrengthPower(mo, STR));
                }
                break;
            }
            case SMACK_DOWN: {
                useFastAttackAnimation();
                dmg(adp(), info, AbstractGameAction.AttackEffect.BLUNT_LIGHT);
                applyToTarget(adp(), this, new FrailPower(adp(), DEBUFF, true));
                break;
            }
            case ROCK_SLIDE: {
                useFastAttackAnimation();
                for (int i = 0; i < multiplier; i++) {
                    dmg(adp(), info, AbstractGameAction.AttackEffect.BLUNT_LIGHT);
                }
                break;
            }
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (attackFirst) {
            if (lastMove(ROCK_SLIDE)) {
                setMoveShortcut(SMACK_DOWN, MOVES[SMACK_DOWN]);
            } else if (lastMove(SMACK_DOWN)) {
                setMoveShortcut(DANCE, MOVES[DANCE]);
            } else {
                setMoveShortcut(ROCK_SLIDE, MOVES[ROCK_SLIDE]);
            }
        } else {
            if (lastMove(SMACK_DOWN)) {
                setMoveShortcut(ROCK_SLIDE, MOVES[ROCK_SLIDE]);
            } else if (lastMove(ROCK_SLIDE)) {
                setMoveShortcut(DANCE, MOVES[DANCE]);
            } else {
                setMoveShortcut(SMACK_DOWN, MOVES[SMACK_DOWN]);
            }
        }
        super.postGetMove();
    }

    protected void setDetailedIntents() {
        ArrayList<Details> details = new ArrayList<>();
        EnemyMoveInfo move = ReflectionHacks.getPrivate(this, AbstractMonster.class, "move");
        switch (move.nextMove) {
            case DANCE: {
                Details powerDetail = new Details(this, STR, STRENGTH_TEXTURE, Details.TargetType.ALL_ENEMIES);
                details.add(powerDetail);
                break;
            }
            case SMACK_DOWN: {
                Details powerDetail = new Details(this, DEBUFF, FRAIL_TEXTURE);
                details.add(powerDetail);
                break;
            }
        }
        PokemonRegions.intents.put(this, details);
    }

    @Override
    public void damage(DamageInfo info) {
        super.damage(info);
        if (underHPThreshold()) {
            parent.startTaunting();
        }
    }

    private boolean underHPThreshold() {
        return this.currentHealth < calculateHPThreshold();
    }

    private int calculateHPThreshold() {
        return Math.round(this.maxHealth * ((float)parent.HP_THRESHOLD / 100));
    }

    @Override
    public AbstractCard getAssociatedPokemonCard() {
        return new Pupitar();
    }

}