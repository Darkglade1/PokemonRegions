package code.monsters.act1.enemies;

import basemod.ReflectionHacks;
import code.BetterSpriterAnimation;
import code.PokemonRegions;
import code.cards.pokemonAllyCards.Rhyhorn;
import code.monsters.AbstractPokemonMonster;
import code.powers.BetterPlatedArmor;
import code.powers.RockHead;
import code.util.Details;
import com.brashmonkey.spriter.Player;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;

import java.util.ArrayList;

import static code.PokemonRegions.*;
import static code.PokemonRegions.BURN_DEBUFF_TEXTURE;
import static code.util.Wiz.*;

public class RhyhornEnemy extends AbstractPokemonMonster
{
    public static final String ID = makeID(RhyhornEnemy.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;

    private static final byte HORN_ATTACK = 0;
    private static final byte DRILL_RUN = 1;
    private static final byte ENDURE = 2;

    public final int PLATED_ARMOR = 5;
    public final int ATTACK_BLOCK = 7;
    public final int DEFEND_BLOCK = 5;

    public RhyhornEnemy() {
        this(0.0f, 0.0f);
    }

    public RhyhornEnemy(final float x, final float y) {
        super(NAME, ID, 140, 0.0F, 0, 130.0f, 100.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Rhyhorn/Rhyhorn.scml"));
        this.type = EnemyType.NORMAL;
        setHp(calcAscensionTankiness(52), calcAscensionTankiness(56));
        addMove(HORN_ATTACK, Intent.ATTACK_DEFEND, calcAscensionDamage(5));
        addMove(DRILL_RUN, Intent.ATTACK, calcAscensionDamage(10));
        addMove(ENDURE, Intent.DEFEND_BUFF);

        Player.PlayerListener listener = new PokemonListener(this);
        ((BetterSpriterAnimation)this.animation).myPlayer.addListener(listener);
    }

    @Override
    public void usePreBattleAction() {
        super.usePreBattleAction();
        applyToTarget(this, this, new RockHead(this));
        applyToTarget(this, this, new BetterPlatedArmor(this, PLATED_ARMOR));
        block(this, PLATED_ARMOR);
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        if(info.base > -1) {
            info.applyPowers(this, adp());
        }

        switch (this.nextMove) {
            case HORN_ATTACK: {
                useFastAttackAnimation();
                block(this, ATTACK_BLOCK);
                dmg(adp(), info, AbstractGameAction.AttackEffect.BLUNT_LIGHT);
                break;
            }
            case DRILL_RUN: {
                useFastAttackAnimation();
                dmg(adp(), info, AbstractGameAction.AttackEffect.BLUNT_HEAVY);
                break;
            }
            case ENDURE: {
                block(this, DEFEND_BLOCK);
                applyToTarget(this, this, new BetterPlatedArmor(this, PLATED_ARMOR));
                break;
            }
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (lastMove(HORN_ATTACK)) {
            setMoveShortcut(DRILL_RUN, MOVES[DRILL_RUN]);
        } else if (lastMove(DRILL_RUN)) {
            setMoveShortcut(ENDURE, MOVES[ENDURE]);
        } else {
            setMoveShortcut(HORN_ATTACK, MOVES[HORN_ATTACK]);
        }

        super.postGetMove();
    }

    protected void setDetailedIntents() {
        ArrayList<Details> details = new ArrayList<>();
        EnemyMoveInfo move = ReflectionHacks.getPrivate(this, AbstractMonster.class, "move");
        switch (move.nextMove) {
            case HORN_ATTACK: {
                Details blockDetails = new Details(this, ATTACK_BLOCK, BLOCK_TEXTURE);
                details.add(blockDetails);
                break;
            }
            case ENDURE: {
                Details blockDetails2 = new Details(this, DEFEND_BLOCK, BLOCK_TEXTURE);
                details.add(blockDetails2);
                Details powerDetail = new Details(this, PLATED_ARMOR, PLATED_ARMOR_TEXTURE);
                details.add(powerDetail);
                break;
            }
        }
        PokemonRegions.intents.put(this, details);
    }

    @Override
    public AbstractCard getAssociatedPokemonCard() {
        return new Rhyhorn();
    }

}