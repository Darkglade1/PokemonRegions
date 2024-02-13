package pokeregions.monsters.act1.enemies;

import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.Texture;
import com.brashmonkey.spriter.Player;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.FrailPower;
import com.megacrit.cardcrawl.powers.WeakPower;
import pokeregions.BetterSpriterAnimation;
import pokeregions.PokemonRegions;
import pokeregions.monsters.AbstractPokemonMonster;
import pokeregions.powers.Taunt;
import pokeregions.util.Details;
import pokeregions.util.TexLoader;
import pokeregions.util.Wiz;

import java.util.ArrayList;

import static pokeregions.PokemonRegions.*;
import static pokeregions.util.Wiz.*;

public class VictreebelEnemy extends AbstractPokemonMonster
{
    public static final String ID = makeID(VictreebelEnemy.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;

    private static final byte RAGE_POWDER = 0;
    private static final byte RAZOR_LEAF = 1;
    private static final byte STUN_SPORE = 2;

    public final int BLOCK = 13;
    public final int DEBUFF = calcAscensionSpecial(1);

    public VictreebelEnemy() {
        this(0.0f, 0.0f, true);
    }

    public VictreebelEnemy(final float x, final float y) {
        this(x, y, true);
    }

    public VictreebelEnemy(final float x, final float y, boolean isCatchable) {
        super(NAME, ID, 140, 0.0F, 0, 150.0f, 160.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Victreebel/Victreebel.scml"));
        this.type = EnemyType.NORMAL;
        this.isCatchable = isCatchable;
        setHp(calcAscensionTankiness(70), calcAscensionTankiness(74));
        addMove(RAGE_POWDER, Intent.DEFEND_BUFF);
        addMove(RAZOR_LEAF, Intent.ATTACK, calcAscensionDamage(12));
        addMove(STUN_SPORE, Intent.DEBUFF);

        Player.PlayerListener listener = new PokemonListener(this);
        ((BetterSpriterAnimation)this.animation).myPlayer.addListener(listener);
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        if(info.base > -1) {
            info.applyPowers(this, adp());
        }

        switch (this.nextMove) {
            case RAGE_POWDER: {
                block(this, BLOCK);
                applyToTarget(this, this, new Taunt(this));
                break;
            }
            case RAZOR_LEAF: {
                useFastAttackAnimation();
                dmg(adp(), info, AbstractGameAction.AttackEffect.SLASH_HORIZONTAL);
                break;
            }
            case STUN_SPORE: {
                applyToTarget(adp(), this, new WeakPower(adp(), DEBUFF, true));
                applyToTarget(adp(), this, new FrailPower(adp(), DEBUFF, true));
                break;
            }
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (Wiz.getEnemies().size() == 1) {
            if (lastMove(STUN_SPORE)) {
                setMoveShortcut(RAZOR_LEAF, MOVES[RAZOR_LEAF]);
            } else {
                setMoveShortcut(STUN_SPORE, MOVES[STUN_SPORE]);
            }
        } else {
            if (lastMove(RAGE_POWDER)) {
                setMoveShortcut(RAZOR_LEAF, MOVES[RAZOR_LEAF]);
            } else {
                setMoveShortcut(RAGE_POWDER, MOVES[RAGE_POWDER]);
            }
        }
        super.postGetMove();
    }

    protected void setDetailedIntents() {
        ArrayList<Details> details = new ArrayList<>();
        EnemyMoveInfo move = ReflectionHacks.getPrivate(this, AbstractMonster.class, "move");
        String textureString = makePowerPath("Taunt32.png");
        Texture texture = TexLoader.getTexture(textureString);
        switch (move.nextMove) {
            case RAGE_POWDER: {
                Details blockDetails = new Details(this, BLOCK, BLOCK_TEXTURE);
                details.add(blockDetails);
                Details powerDetails = new Details(this, 1, texture);
                details.add(powerDetails);
                break;
            }
            case STUN_SPORE: {
                Details powerDetails = new Details(this, DEBUFF, WEAK_TEXTURE);
                details.add(powerDetails);
                Details powerDetails2 = new Details(this, DEBUFF, FRAIL_TEXTURE);
                details.add(powerDetails2);
                break;
            }
        }
        PokemonRegions.intents.put(this, details);
    }

}