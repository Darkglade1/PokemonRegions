package pokeregions.monsters.act3.enemies;

import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.FrailPower;
import com.megacrit.cardcrawl.powers.WeakPower;
import pokeregions.BetterSpriterAnimation;
import pokeregions.PokemonRegions;
import pokeregions.cards.Frozen;
import pokeregions.cards.pokemonAllyCards.act3.Regice;
import pokeregions.monsters.AbstractPokemonMonster;
import pokeregions.powers.Taunt;
import pokeregions.util.Details;
import pokeregions.util.TexLoader;
import pokeregions.util.Wiz;

import java.util.ArrayList;

import static pokeregions.PokemonRegions.*;
import static pokeregions.util.Wiz.*;

public class RegiceEnemy extends AbstractPokemonMonster
{
    public static final String ID = makeID(RegiceEnemy.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;

    private static final byte SLAM = 0;
    private static final byte ICY_WIND = 1;
    private static final byte FREEZE_DRY = 2;

    public final int DEBUFF = 2;
    public final int STATUS = calcAscensionSpecial(3);

    public RegiceEnemy() {
        this(0.0f, 0.0f);
    }

    public RegiceEnemy(final float x, final float y) {
        super(NAME, ID, 140, 0.0F, 0, 230.0f, 200.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Regice/Regice.scml"));
        ((BetterSpriterAnimation)this.animation).myPlayer.setScale(Settings.scale * 1.5f);
        setHp(calcAscensionTankiness(120));
        addMove(SLAM, Intent.ATTACK, calcAscensionDamage(20));
        addMove(ICY_WIND, Intent.DEBUFF);
        addMove(FREEZE_DRY, Intent.STRONG_DEBUFF);
    }

    @Override
    protected void setUpMisc() {
        super.setUpMisc();
        this.type = EnemyType.ELITE;
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        if(info.base > -1) {
            info.applyPowers(this, adp());
        }

        switch (this.nextMove) {
            case SLAM: {
                useFastAttackAnimation();
                dmg(adp(), info, AbstractGameAction.AttackEffect.BLUNT_HEAVY);
                break;
            }
            case ICY_WIND: {
                applyToTarget(adp(), this, new WeakPower(adp(), DEBUFF, true));
                applyToTarget(adp(), this, new FrailPower(adp(), DEBUFF, true));
                applyToTarget(this, this, new Taunt(this));
                break;
            }
            case FREEZE_DRY: {
                intoDiscardMo(new Frozen(), STATUS);
                break;
            }
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (lastMove(ICY_WIND)) {
            setMoveShortcut(SLAM, MOVES[SLAM]);
        } else if(lastMove(SLAM)) {
            setMoveShortcut(FREEZE_DRY, MOVES[FREEZE_DRY]);
        } else {
            setMoveShortcut(ICY_WIND, MOVES[ICY_WIND]);
        }
        if (Wiz.getEnemies().size() == 1) {
            setMoveShortcut(SLAM, MOVES[SLAM]);
        }
        super.postGetMove();
    }

    protected void setDetailedIntents() {
        ArrayList<Details> details = new ArrayList<>();
        EnemyMoveInfo move = ReflectionHacks.getPrivate(this, AbstractMonster.class, "move");
        String textureString = makePowerPath("Taunt32.png");
        Texture texture = TexLoader.getTexture(textureString);
        switch (move.nextMove) {
            case ICY_WIND: {
                Details powerDetail = new Details(this, DEBUFF, WEAK_TEXTURE);
                details.add(powerDetail);
                Details powerDetail2 = new Details(this, DEBUFF, FRAIL_TEXTURE);
                details.add(powerDetail2);
                Details powerDetails = new Details(this, 1, texture);
                details.add(powerDetails);
                break;
            }
            case FREEZE_DRY: {
                Details statusDetail = new Details(this, STATUS, FROZEN_TEXTURE, Details.TargetType.DISCARD_PILE);
                details.add(statusDetail);
                break;
            }
        }
        PokemonRegions.intents.put(this, details);
    }

    @Override
    public AbstractCard getAssociatedPokemonCard() {
        return new Regice();
    }

}