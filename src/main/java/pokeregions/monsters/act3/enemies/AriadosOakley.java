package pokeregions.monsters.act3.enemies;

import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.DrawReductionPower;
import com.megacrit.cardcrawl.powers.EntanglePower;
import com.megacrit.cardcrawl.powers.FrailPower;
import pokeregions.BetterSpriterAnimation;
import pokeregions.PokemonRegions;
import pokeregions.monsters.AbstractPokemonMonster;
import pokeregions.util.Details;
import pokeregions.util.TexLoader;

import java.util.ArrayList;

import static pokeregions.PokemonRegions.*;
import static pokeregions.util.Wiz.*;

public class AriadosOakley extends AbstractPokemonMonster
{
    public static final String ID = makeID(AriadosOakley.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;

    private static final byte INFESTATION = 0;
    private static final byte STICKY_WEB = 1;
    private static final byte CROSS_POISON = 2;

    public final int DEBUFF = calcAscensionSpecial(2);

    public AriadosOakley() {
        this(0.0f, 0.0f, false);
    }

    public AriadosOakley(final float x, final float y) {
        this(x, y, false);
    }

    public AriadosOakley(final float x, final float y, boolean isCatchable) {
        super(NAME, ID, 140, 0.0F, 0, 150.0f, 110.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Ariados/Ariados.scml"));
        ((BetterSpriterAnimation)this.animation).myPlayer.speed = 10;
        this.type = EnemyType.ELITE;
        this.isCatchable = isCatchable;
        setHp(calcAscensionTankiness(130), calcAscensionTankiness(142));
        addMove(INFESTATION, Intent.ATTACK_DEBUFF, calcAscensionDamage(11));
        addMove(STICKY_WEB, Intent.STRONG_DEBUFF);
        addMove(CROSS_POISON, Intent.ATTACK, calcAscensionDamage(5), 3);
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        if(info.base > -1) {
            info.applyPowers(this, adp());
        }

        switch (this.nextMove) {
            case INFESTATION: {
                useFastAttackAnimation();
                dmg(adp(), info, AbstractGameAction.AttackEffect.POISON);
                applyToTarget(adp(), this, new DrawReductionPower(adp(), 1));
                break;
            }
            case STICKY_WEB: {
                applyToTarget(adp(), this, new EntanglePower(adp()));
                applyToTarget(adp(), this, new FrailPower(adp(), DEBUFF, true));
                break;
            }
            case CROSS_POISON: {
                useFastAttackAnimation();
                for (int i = 0 ; i < multiplier; i++) {
                    dmg(adp(), info, AbstractGameAction.AttackEffect.SLASH_HEAVY);
                }
                break;
            }
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (lastMove(INFESTATION)) {
            setMoveShortcut(STICKY_WEB, MOVES[STICKY_WEB]);
        } else if (lastMove(STICKY_WEB)){
            setMoveShortcut(CROSS_POISON, MOVES[CROSS_POISON]);
        } else {
            setMoveShortcut(INFESTATION, MOVES[INFESTATION]);
        }
        super.postGetMove();
    }

    protected void setDetailedIntents() {
        ArrayList<Details> details = new ArrayList<>();
        EnemyMoveInfo move = ReflectionHacks.getPrivate(this, AbstractMonster.class, "move");
        String textureString = makeUIPath("Web.png");
        Texture texture = TexLoader.getTexture(textureString);
        switch (move.nextMove) {
            case INFESTATION: {
                Details powerDetail = new Details(this, 1, DRAW_DOWN_TEXTURE);
                details.add(powerDetail);
                break;
            }
            case STICKY_WEB: {
                Details powerDetails = new Details(this, 1, texture);
                details.add(powerDetails);
                Details powerDetails2 = new Details(this, DEBUFF, FRAIL_TEXTURE);
                details.add(powerDetails2);
                break;
            }
        }
        PokemonRegions.intents.put(this, details);
    }

}