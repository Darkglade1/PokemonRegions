package pokeregions.monsters.act1.allyPokemon;

import pokeregions.BetterSpriterAnimation;
import pokeregions.PokemonRegions;
import pokeregions.cards.AbstractAllyPokemonCard;
import pokeregions.cards.pokemonAllyCards.act1.Diglett;
import pokeregions.monsters.AbstractPokemonAlly;
import pokeregions.util.ProAudio;
import pokeregions.util.Wiz;
import pokeregions.vfx.WaitEffect;
import com.brashmonkey.spriter.Animation;
import com.brashmonkey.spriter.Player;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;

import static pokeregions.PokemonRegions.makeMonsterPath;
import static pokeregions.util.Wiz.atb;
import static pokeregions.util.Wiz.dmg;

public class DiglettAlly extends AbstractPokemonAlly
{
    public static final String ID = PokemonRegions.makeID(Diglett.class.getSimpleName());
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;

    public boolean burrowed = false;

    public DiglettAlly(final float x, final float y, AbstractAllyPokemonCard allyCard) {
        super(NAME, ID, 100, -5.0F, 0, 120.0f, 80.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Diglett/Diglett.scml"));
        this.animation.setFlip(true, false);
        Player.PlayerListener listener = new DiglettListener(this);
        ((BetterSpriterAnimation)this.animation).myPlayer.addListener(listener);

        this.allyCard = allyCard;
        setStaminaInfo(allyCard);

        move1Intent = Intent.BUFF;
        move2Intent = Intent.ATTACK;
        addMove(MOVE_1, move1Intent);
        addMove(MOVE_2, move2Intent, Diglett.MOVE_2_DAMAGE);
        defaultMove = MOVE_1;
        move2RequiresTarget = true;
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        switch (this.nextMove) {
            case MOVE_1: {
                runAnim("Dig");
                Wiz.playAudio(ProAudio.BURROW);
                this.burrowed = true;
                setMoveShortcut(MOVE_2);
                break;
            }
            case MOVE_2: {
                if (this.burrowed) {
                    runAnim("Emerge");
                    this.burrowed = false;
                    atb(new VFXAction(new WaitEffect(), 0.2f));
                    atb(new AbstractGameAction() {
                        @Override
                        public void update() {
                            useFastAttackAnimation();
                            this.isDone = true;
                        }
                    });
                    dmg(target, info, AbstractGameAction.AttackEffect.BLUNT_HEAVY);
                    setMoveShortcut(MOVE_1);
                }
                break;
            }
        }
        postTurn();
    }

    @Override
    public boolean canUseMove2() {
        return super.canUseMove2() && this.burrowed;
    }

    public static class DiglettListener implements Player.PlayerListener {

        private final DiglettAlly character;

        public DiglettListener(DiglettAlly character) {
            this.character = character;
        }

        public void animationFinished(Animation animation){
            if (character.burrowed) {
                if (!animation.name.equals("DigIdle")) {
                    ((BetterSpriterAnimation)character.animation).myPlayer.setAnimation("DigIdle");
                }
            } else {
                if (!animation.name.equals("Idle")) {
                    character.resetAnimation();
                }
            }
        }

        //UNUSED
        public void animationChanged(Animation var1, Animation var2){
        }

        //UNUSED
        public void preProcess(Player var1){
        }

        //UNUSED
        public void postProcess(Player var1){
        }

        //UNUSED
        public void mainlineKeyChanged(com.brashmonkey.spriter.Mainline.Key var1, com.brashmonkey.spriter.Mainline.Key var2){
        }
    }

}