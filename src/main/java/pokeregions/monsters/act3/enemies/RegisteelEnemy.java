package pokeregions.monsters.act3.enemies;

import actlikeit.dungeons.CustomDungeon;
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
import com.megacrit.cardcrawl.powers.MetallicizePower;
import pokeregions.BetterSpriterAnimation;
import pokeregions.PokemonRegions;
import pokeregions.cards.pokemonAllyCards.act3.Registeel;
import pokeregions.monsters.AbstractPokemonMonster;
import pokeregions.powers.MonsterNextTurnBlockPower;
import pokeregions.powers.Taunt;
import pokeregions.util.Details;
import pokeregions.util.TexLoader;
import pokeregions.util.Wiz;

import java.util.ArrayList;

import static pokeregions.PokemonRegions.*;
import static pokeregions.util.Wiz.*;

public class RegisteelEnemy extends AbstractPokemonMonster
{
    public static final String ID = makeID(RegisteelEnemy.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;

    private static final byte SLAM = 0;
    private static final byte WIDE_GUARD = 1;
    private static final byte PROTECT = 2;

    public final int BIG_BLOCK = 16;
    public final int SMALL_BLOCK = 8;
    public final int METALLICIZE = calcAscensionSpecialSmall(3);

    public RegisteelEnemy() {
        this(0.0f, 0.0f);
    }

    public RegisteelEnemy(final float x, final float y) {
        super(NAME, ID, 140, 0.0F, 0, 230.0f, 200.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Registeel/Registeel.scml"));
        ((BetterSpriterAnimation)this.animation).myPlayer.setScale(Settings.scale * 1.5f);
        setHp(calcAscensionTankiness(120));
        addMove(SLAM, Intent.ATTACK, calcAscensionDamage(20));
        addMove(WIDE_GUARD, Intent.DEFEND);
        addMove(PROTECT, Intent.DEFEND_BUFF);
    }

    @Override
    protected void setUpMisc() {
        super.setUpMisc();
        this.type = EnemyType.ELITE;
    }

    @Override
    public void usePreBattleAction() {
        super.usePreBattleAction();
        CustomDungeon.playTempMusicInstantly("RegiTrio");
        applyToTarget(this, this, new Taunt(this, false));
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
            case WIDE_GUARD: {
                for (AbstractMonster mo : Wiz.getEnemies()) {
                    applyToTarget(mo, this, new MonsterNextTurnBlockPower(mo, BIG_BLOCK));
                }
                applyToTarget(this, this, new Taunt(this));
                break;
            }
            case PROTECT: {
                for (AbstractMonster mo : Wiz.getEnemies()) {
                    applyToTarget(mo, this, new MonsterNextTurnBlockPower(mo, SMALL_BLOCK));
                    applyToTarget(mo, this, new MetallicizePower(mo, METALLICIZE));
                }
                break;
            }
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (lastMove(SLAM)) {
            setMoveShortcut(PROTECT, MOVES[PROTECT]);
        } else if(lastMove(PROTECT)){
            setMoveShortcut(WIDE_GUARD, MOVES[WIDE_GUARD]);
        } else {
            setMoveShortcut(SLAM, MOVES[SLAM]);
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
            case WIDE_GUARD: {
                Details blockDetail = new Details(this, BIG_BLOCK, BLOCK_TEXTURE, Details.TargetType.ALL_ENEMIES);
                details.add(blockDetail);
                Details powerDetails = new Details(this, 1, texture);
                details.add(powerDetails);
                break;
            }
            case PROTECT: {
                Details blockDetail = new Details(this, SMALL_BLOCK, BLOCK_TEXTURE, Details.TargetType.ALL_ENEMIES);
                details.add(blockDetail);
                Details powerDetail = new Details(this, METALLICIZE, METALLICIZE_TEXTURE, Details.TargetType.ALL_ENEMIES);
                details.add(powerDetail);
                break;
            }
        }
        PokemonRegions.intents.put(this, details);
    }

    @Override
    public AbstractCard getAssociatedPokemonCard() {
        return new Registeel();
    }

}