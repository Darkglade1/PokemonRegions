package pokeregions.actions;

import pokeregions.cards.AbstractAllyPokemonCard;
import pokeregions.monsters.AbstractPokemonAlly;
import pokeregions.patches.PlayerSpireFields;
import pokeregions.relics.OnPokemonSwitchRelic;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.SpawnMonsterAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;

import static pokeregions.PokemonRegions.makeID;
import static pokeregions.util.Wiz.adp;
import static pokeregions.util.Wiz.atb;

public class SwitchPokemonAction extends AbstractGameAction {
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(makeID("SwitchPokemon"));
    private static final String[] TEXT = uiStrings.TEXT;
    private final boolean canCancel;

    public SwitchPokemonAction(boolean canCancel) {
        this.actionType = ActionType.CARD_MANIPULATION;
        this.duration = this.startDuration = Settings.ACTION_DUR_FAST;
        this.canCancel = canCancel;
    }

    public SwitchPokemonAction() {
        this(true);
    }

    public void update() {
        if (this.duration == this.startDuration) {
            CardGroup availablePokemon =  new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
            for (AbstractCard card : PlayerSpireFields.pokemonTeam.get(adp()).group) {
                if (card instanceof AbstractAllyPokemonCard) {
                    int stamina = ((AbstractAllyPokemonCard) card).currentStamina;
                    if (stamina > 0 && PlayerSpireFields.activePokemon.get(adp()).allyCard != card) {
                        availablePokemon.addToTop(card);
                    }
                }
            }
            if (availablePokemon.size() > 0) {
                if (canCancel) {
                    AbstractDungeon.gridSelectScreen.open(availablePokemon, 1,  TEXT[2], false, false, true, false);
                    AbstractDungeon.overlayMenu.cancelButton.show(TEXT[4]);
                } else {
                    AbstractDungeon.gridSelectScreen.open(availablePokemon, 1, TEXT[2], false);
                }
                this.tickDuration();
            } else {
                atb(new RemoveMonsterAction(PlayerSpireFields.activePokemon.get(adp())));
                PlayerSpireFields.activePokemon.set(adp(), null);
                this.isDone = true;
            }
        } else {
            if (!AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
                AbstractCard selectedPokemon = AbstractDungeon.gridSelectScreen.selectedCards.get(0);
                if (selectedPokemon instanceof AbstractAllyPokemonCard) {
                    AbstractAllyPokemonCard pokemonCard = (AbstractAllyPokemonCard)selectedPokemon;
                    AbstractPokemonAlly pokemon = pokemonCard.getAssociatedPokemon(AbstractPokemonAlly.X_POSITION, AbstractPokemonAlly.Y_POSITION);
                    if (pokemon != null) {
                        atb(new RemoveMonsterAction(PlayerSpireFields.activePokemon.get(adp())));
                        PlayerSpireFields.activePokemon.set(adp(), pokemon);
                        PlayerSpireFields.mostRecentlyUsedPokemonCardID.set(adp(), pokemonCard.cardID);
                        atb(new SpawnMonsterAction(pokemon, false));
                        atb(new UsePreBattleActionAction(pokemon));
                        for (AbstractRelic relic : adp().relics) {
                            if (relic instanceof OnPokemonSwitchRelic) {
                                ((OnPokemonSwitchRelic) relic).onPokemonSwitch(pokemon);
                            }
                        }
                        pokemon.onSwitchIn();
                    }
                }
                AbstractDungeon.gridSelectScreen.selectedCards.clear();
            }
            this.tickDuration();
        }
    }
}
