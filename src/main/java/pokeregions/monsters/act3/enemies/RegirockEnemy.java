package pokeregions.monsters.act3.enemies;

import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.status.Wound;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import pokeregions.BetterSpriterAnimation;
import pokeregions.PokemonRegions;
import pokeregions.cards.pokemonAllyCards.act3.Regirock;
import pokeregions.monsters.AbstractPokemonMonster;
import pokeregions.powers.Taunt;
import pokeregions.util.Details;
import pokeregions.util.TexLoader;
import pokeregions.util.Wiz;

import java.util.ArrayList;

import static pokeregions.PokemonRegions.*;
import static pokeregions.util.Wiz.*;

public class RegirockEnemy extends AbstractPokemonMonster
{
    public static final String ID = makeID(RegirockEnemy.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;

    private static final byte SLAM = 0;
    private static final byte ANCIENT_POWER = 1;
    private static final byte ENTOMB = 2;

    public final int STR = calcAscensionSpecial(5);
    public final int STATUS = calcAscensionSpecial(1);
    public final int DEBUFF = 2;

    public RegirockEnemy() {
        this(0.0f, 0.0f);
    }

    public RegirockEnemy(final float x, final float y) {
        super(NAME, ID, 140, 0.0F, 0, 230.0f, 200.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Regirock/Regirock.scml"));
        ((BetterSpriterAnimation)this.animation).myPlayer.setScale(Settings.scale * 1.5f);
        setHp(calcAscensionTankiness(120));
        addMove(SLAM, Intent.ATTACK, calcAscensionDamage(20));
        addMove(ANCIENT_POWER, Intent.BUFF);
        addMove(ENTOMB, Intent.DEBUFF);
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
            case ANCIENT_POWER: {
                for (AbstractMonster mo : Wiz.getEnemies()) {
                    applyToTarget(mo, this, new StrengthPower(mo, STR));
                }
                break;
            }
            case ENTOMB: {
                intoDrawMo(new Wound(), STATUS);
                applyToTarget(adp(), this, new VulnerablePower(adp(), DEBUFF, true));
                applyToTarget(this, this, new Taunt(this));
                break;
            }
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (lastMove(ANCIENT_POWER)) {
            setMoveShortcut(ENTOMB, MOVES[ENTOMB]);
        } else if (lastMove(ENTOMB)) {
            setMoveShortcut(SLAM, MOVES[SLAM]);
        } else {
            setMoveShortcut(ANCIENT_POWER, MOVES[ANCIENT_POWER]);
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
            case ANCIENT_POWER: {
                Details powerDetail = new Details(this, STR, STRENGTH_TEXTURE, Details.TargetType.ALL_ENEMIES);
                details.add(powerDetail);
                break;
            }
            case ENTOMB: {
                Details statusDetail = new Details(this, STATUS, WOUND_TEXTURE, Details.TargetType.DRAW_PILE);
                details.add(statusDetail);
                Details powerDetail = new Details(this, DEBUFF, VULNERABLE_TEXTURE);
                details.add(powerDetail);
                Details powerDetails = new Details(this, 1, texture);
                details.add(powerDetails);
                break;
            }
        }
        PokemonRegions.intents.put(this, details);
    }

    @Override
    public AbstractCard getAssociatedPokemonCard() {
        return new Regirock();
    }

}