package pokeregions.monsters.act1.enemies;

import actlikeit.dungeons.CustomDungeon;
import basemod.ReflectionHacks;
import pokeregions.BetterSpriterAnimation;
import pokeregions.PokemonRegions;
import pokeregions.cards.pokemonAllyCards.act1.Dragonite;
import pokeregions.monsters.AbstractPokemonMonster;
import pokeregions.powers.Outrage;
import pokeregions.util.Details;
import pokeregions.util.TexLoader;
import pokeregions.util.Wiz;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.StrengthPower;

import java.util.ArrayList;

import static pokeregions.PokemonRegions.*;
import static pokeregions.util.Wiz.*;

public class DragoniteEnemy extends AbstractPokemonMonster
{
    public static final String ID = makeID(DragoniteEnemy.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;

    private static final byte DRAGON_DANCE = 0;
    private static final byte OUTRAGE = 1;
    private static final byte STUNNED = 2;

    public final int OUTRAGE_BASE_DAMAGE = calcAscensionDamage(13);
    public final int OUTRAGE_DAMAGE_INCREASE = calcAscensionSpecial(5);
    public final int STR = calcAscensionSpecialSmall(5);
    public final int OUTRAGE_BASE_TURNS = 2;
    public final int OUTRAGE_DAMAGE_THRESHOLD = 30;

    private AbstractPower outrage;

    public DragoniteEnemy() {
        this(0.0f, 0.0f);
    }

    public DragoniteEnemy(final float x, final float y) {
        super(NAME, ID, 140, 0.0F, 0, 250.0f, 290.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Dragonite/Dragonite.scml"));
        ((BetterSpriterAnimation)this.animation).myPlayer.setScale(Settings.scale * 2.0f);
        setHp(calcAscensionTankiness(280));
        addMove(DRAGON_DANCE, Intent.BUFF);
        addMove(OUTRAGE, Intent.ATTACK, OUTRAGE_BASE_DAMAGE);
        addMove(STUNNED, Intent.STUN);
    }

    @Override
    protected void setUpMisc() {
        super.setUpMisc();
        this.type = EnemyType.BOSS;
    }

    @Override
    public void usePreBattleAction() {
        super.usePreBattleAction();
        CustomDungeon.playTempMusicInstantly("Zinnia");
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        if(info.base > -1) {
            info.applyPowers(this, adp());
        }

        switch (this.nextMove) {
            case DRAGON_DANCE: {
                applyToTarget(this, this, new StrengthPower(this, STR));
                outrage = new Outrage(this, OUTRAGE_BASE_TURNS, OUTRAGE_DAMAGE_THRESHOLD);
                applyToTarget(this, this, outrage);
                break;
            }
            case OUTRAGE: {
                useFastAttackAnimation();
                dmg(adp(), info, AbstractGameAction.AttackEffect.BLUNT_HEAVY);
                if (outrage.amount == 1) {
                    Wiz.makePowerRemovable(outrage);
                }
                atb(new ReducePowerAction(this, this, Outrage.POWER_ID, 1));
                int newDamage = moves.get(OUTRAGE).baseDamage += OUTRAGE_DAMAGE_INCREASE;
                addMove(OUTRAGE, Intent.ATTACK, newDamage);
                break;
            }
            case STUNNED: {
                addMove(OUTRAGE, Intent.ATTACK, OUTRAGE_BASE_DAMAGE);
                break;
            }
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (lastMove(DRAGON_DANCE)) {
            setMoveShortcut(OUTRAGE, MOVES[OUTRAGE]);
        } else if (lastMove(OUTRAGE)) {
            if (this.hasPower(Outrage.POWER_ID)) {
                setMoveShortcut(OUTRAGE, MOVES[OUTRAGE]);
            } else {
                setMoveShortcut(STUNNED, MOVES[STUNNED]);
            }
        } else {
            setMoveShortcut(DRAGON_DANCE, MOVES[DRAGON_DANCE]);
        }
        super.postGetMove();
    }

    protected void setDetailedIntents() {
        ArrayList<Details> details = new ArrayList<>();
        EnemyMoveInfo move = ReflectionHacks.getPrivate(this, AbstractMonster.class, "move");
        String textureString = makeUIPath("Enrage.png");
        Texture texture = TexLoader.getTexture(textureString);
        switch (move.nextMove) {
            case DRAGON_DANCE: {
                Details powerDetail = new Details(this, STR, STRENGTH_TEXTURE);
                details.add(powerDetail);
                Details powerDetail2 = new Details(this, OUTRAGE_BASE_TURNS, texture);
                details.add(powerDetail2);
                break;
            }
        }
        PokemonRegions.intents.put(this, details);
    }

    @Override
    public void die(boolean triggerRelics) {
        super.die(triggerRelics);
        onBossVictoryLogic();
    }

    @Override
    public AbstractCard getAssociatedPokemonCard() {
        return new Dragonite();
    }

}