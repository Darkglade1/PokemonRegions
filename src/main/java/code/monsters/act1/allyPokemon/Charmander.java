package code.monsters.act1.allyPokemon;

import code.BetterSpriterAnimation;
import code.CustomIntent.IntentEnums;
import code.PokemonRegions;
import code.actions.AllyDamageAllEnemiesAction;
import code.cards.AbstractAllyPokemonCard;
import code.monsters.AbstractPokemonAlly;
import code.util.AllyMove;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;

import static code.PokemonRegions.makeMonsterPath;
import static code.PokemonRegions.makeUIPath;
import static code.util.Wiz.atb;
import static code.util.Wiz.dmg;

public class Charmander extends AbstractPokemonAlly
{
    public static final String ID = PokemonRegions.makeID(Charmander.class.getSimpleName());
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    private static final byte MOVE_1 = 0;
    private static final byte MOVE_2 = 1;

    public Charmander() {
        this(0.0f, 0.0f);
    }

    public Charmander(final float x, final float y) {
        super(NAME, ID, 100, -5.0F, 0, 150.0f, 100.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Charmander/Charmander.scml"));
        this.animation.setFlip(true, false);

        allyCard = (AbstractAllyPokemonCard) CardLibrary.getCard(ID);

        this.setHp(allyCard.currentStamina);

        addMove(MOVE_1, Intent.ATTACK, allyCard.damage);
        addMove(MOVE_2, IntentEnums.MASS_ATTACK, allyCard.secondDamage);
    }

    @Override
    public void usePreBattleAction() {
        super.usePreBattleAction();

        AllyMove move1 = new AllyMove(allyCard.move1Name, this, new Texture(makeUIPath("attackIcon.png")), allyCard.move1Description, () -> {
            setMoveShortcut(MOVE_1);
            createIntent();
            AbstractDungeon.onModifyPower();
        });
        move1.setX(this.intentHb.x - ((50.0F + 32.0f) * Settings.scale));
        move1.setY(this.intentHb.cY - ((32.0f - 80.0f) * Settings.scale));
        allyMoves.add(move1);

        AllyMove move2 = new AllyMove(allyCard.move2Name, this, new Texture(makeUIPath("areaIntent.png")), allyCard.move2Description, () -> {
            setMoveShortcut(MOVE_2);
            createIntent();
            AbstractDungeon.onModifyPower();
        });
        move2.setX(this.intentHb.x - ((50.0F + 32.0f) * Settings.scale));
        move2.setY(this.intentHb.cY - ((32.0f - 160.0f) * Settings.scale));
        allyMoves.add(move2);

        //changeToGuard.setX(this.intentHb.x - ((50.0F + 32.0f) * Settings.scale));
        //changeToGuard.setY(this.intentHb.cY - ((32.0f - 240.0f) * Settings.scale));

        atb(new AbstractGameAction() {
            @Override
            public void update() {
                setMoveShortcut(MOVE_1);
                createIntent();
                this.isDone = true;
            }
        });

    }

    @Override
    public void takeTurn() {
        super.takeTurn();

        DamageInfo info;
        int multiplier = 0;
        if(moves.containsKey(this.nextMove)) {
            EnemyMoveInfo emi = moves.get(this.nextMove);
            info = new DamageInfo(this, emi.baseDamage, DamageInfo.DamageType.NORMAL);
            multiplier = emi.multiplier;
        } else {
            info = new DamageInfo(this, 0, DamageInfo.DamageType.NORMAL);
        }

        if(info.base > -1) {
            info.applyPowers(this, target);
        }
        switch (this.nextMove) {
            case MOVE_1: {
                dmg(target, info, AbstractGameAction.AttackEffect.FIRE);
                break;
            }
            case MOVE_2: {
                // add vfx of shooting fire bolts at each enemy
                atb(new AllyDamageAllEnemiesAction(this, calcMassAttack(info), DamageInfo.DamageType.NORMAL, AbstractGameAction.AttackEffect.FIRE));
                break;
            }
        }

        postTurn();
    }

}