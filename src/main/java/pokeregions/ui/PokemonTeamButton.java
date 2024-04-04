package pokeregions.ui;

import basemod.BaseMod;
import basemod.ReflectionHacks;
import basemod.TopPanelGroup;
import basemod.TopPanelItem;
import basemod.abstracts.CustomSavable;
import basemod.patches.com.megacrit.cardcrawl.helpers.TopPanel.TopPanelHelper;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import pokeregions.PokemonRegions;
import pokeregions.cards.AbstractAllyPokemonCard;
import pokeregions.patches.PlayerSpireFields;
import pokeregions.util.Tags;
import pokeregions.util.TexLoader;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.CardSave;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.TipHelper;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.vfx.cardManip.PurgeCardEffect;

import java.util.ArrayList;
import java.util.List;

import static pokeregions.PokemonRegions.makeID;
import static pokeregions.PokemonRegions.makeUIPath;
import static pokeregions.util.Wiz.adp;

public class PokemonTeamButton extends TopPanelItem implements CustomSavable<List<CardSave>> {
    private static final Texture IMG = TexLoader.getTexture(makeUIPath("PokemonTeamButton.png"));
    public static final String ID = makeID(PokemonTeamButton.class.getSimpleName());
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(ID);
    private static final String[] TEXT = uiStrings.TEXT;

    public static final int MAX_TEAM_SIZE = 6;
    private boolean releasingPokemon = false;

    public PokemonTeamButton() {
        super(IMG, ID);
    }

    @Override
    protected void onClick() {
        if (!CardCrawlGame.isPopupOpen) {
            CardCrawlGame.sound.play("DECK_OPEN");
            toggleScreen();
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        super.render(sb);
        CardGroup pokemonTeam = PlayerSpireFields.pokemonTeam.get(adp());
        FontHelper.renderFontRightTopAligned(sb, FontHelper.topPanelAmountFont, Integer.toString(pokemonTeam.size()), this.x + 58.0f * Settings.scale, this.y + 25.0f * Settings.scale, Color.WHITE.cpy());
    }

    @Override
    public List<CardSave> onSave() {
        CardGroup pokemonTeam = PlayerSpireFields.pokemonTeam.get(adp());
        ArrayList<CardSave> retVal = new ArrayList<>();
        for (AbstractCard card : pokemonTeam.group) {
            retVal.add(new CardSave(card.cardID, card.timesUpgraded, card.misc));
        }
        return retVal;
    }

    @Override
    public void onLoad(List<CardSave> cardSaves) {
        CardGroup pokemonTeam = PlayerSpireFields.pokemonTeam.get(adp());
        for (CardSave s : cardSaves) {
            AbstractCard card = CardLibrary.getCopy(s.id, s.upgrades, s.misc);
            if (card instanceof AbstractAllyPokemonCard) {
                ((AbstractAllyPokemonCard) card).currentStamina = s.misc;
                ((AbstractAllyPokemonCard) card).initializeDescriptionFromMoves();
            }
            pokemonTeam.addToTop(card);
        }
        ArrayList<TopPanelItem> topPanelItems = ReflectionHacks.getPrivate(TopPanelHelper.topPanelGroup, TopPanelGroup.class, "topPanelItems");
        for (TopPanelItem item : topPanelItems) {
            if (item instanceof PokemonTeamButton) {
                return;
            }
        }
        BaseMod.addTopPanelItem(this);
    }

    @Override
    protected void onHover() {
        super.onHover();
        if (this.hitbox.justHovered) {
            CardCrawlGame.sound.playV("UI_HOVER", 0.75F);
        }
        if (this.hitbox.hovered) {
            TipHelper.renderGenericTip(1550.0F * Settings.scale, (float)Settings.HEIGHT - 120.0F * Settings.scale, TEXT[0], TEXT[1]);
        }
    }

    public static void releaseExcessPokemon() {
        CardGroup releaseablePokemon =  new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
        for (AbstractCard card : PlayerSpireFields.pokemonTeam.get(adp()).group) {
            if (!card.hasTag(Tags.STARTER_POKEMON)) {
                releaseablePokemon.addToTop(card);
            }
        }
        if (releaseablePokemon.size() > 0) {
            PokemonRegions.releasingPokemon = true;
            if (AbstractDungeon.isScreenUp) {
                AbstractDungeon.dynamicBanner.hide();
                AbstractDungeon.overlayMenu.cancelButton.hide();
                AbstractDungeon.previousScreen = AbstractDungeon.screen;
            }
            AbstractDungeon.getCurrRoom().phase = AbstractRoom.RoomPhase.INCOMPLETE;
            AbstractDungeon.gridSelectScreen.open(releaseablePokemon, 1, TEXT[2], false, false, false, true);
        }
    }

    public static void teamWideHeal(float percent) {
        for (AbstractCard card : PlayerSpireFields.pokemonTeam.get(adp()).group) {
            if (card instanceof AbstractAllyPokemonCard) {
                AbstractAllyPokemonCard pokemonCard = (AbstractAllyPokemonCard) card;
                int staminaHeal = Math.round(pokemonCard.maxStamina * percent);
                pokemonCard.updateStamina(pokemonCard.currentStamina + staminaHeal);
            }
        }
    }

    public static void teamWideHeal(int amount) {
        for (AbstractCard card : PlayerSpireFields.pokemonTeam.get(adp()).group) {
            if (card instanceof AbstractAllyPokemonCard) {
                AbstractAllyPokemonCard pokemonCard = (AbstractAllyPokemonCard) card;
                pokemonCard.updateStamina(pokemonCard.currentStamina + amount);
            }
        }
    }

    private static void toggleScreen() {
        if (AbstractDungeon.screen == PokemonTeamViewScreen.Enum.POKEMON_TEAM_VIEW_SCREEN) {
            AbstractDungeon.closeCurrentScreen();
        } else {
            if (AbstractDungeon.screen == AbstractDungeon.CurrentScreen.COMBAT_REWARD) {
                AbstractDungeon.closeCurrentScreen();
                AbstractDungeon.previousScreen = AbstractDungeon.CurrentScreen.COMBAT_REWARD;
            } else if (!AbstractDungeon.isScreenUp) {
            } else if (AbstractDungeon.screen == AbstractDungeon.CurrentScreen.MASTER_DECK_VIEW) {
                if (AbstractDungeon.previousScreen != null) {
                    AbstractDungeon.screenSwap = true;
                }

                AbstractDungeon.closeCurrentScreen();
            } else if (AbstractDungeon.screen == AbstractDungeon.CurrentScreen.DEATH) {
                AbstractDungeon.previousScreen = AbstractDungeon.CurrentScreen.DEATH;
                AbstractDungeon.deathScreen.hide();
            } else if (AbstractDungeon.screen == AbstractDungeon.CurrentScreen.BOSS_REWARD) {
                AbstractDungeon.previousScreen = AbstractDungeon.CurrentScreen.BOSS_REWARD;
                AbstractDungeon.bossRelicScreen.hide();
            } else if (AbstractDungeon.screen == AbstractDungeon.CurrentScreen.SHOP) {
                AbstractDungeon.overlayMenu.cancelButton.hide();
                AbstractDungeon.previousScreen = AbstractDungeon.CurrentScreen.SHOP;
            } else if (AbstractDungeon.screen == AbstractDungeon.CurrentScreen.MAP && !AbstractDungeon.dungeonMapScreen.dismissable) {
                AbstractDungeon.previousScreen = AbstractDungeon.CurrentScreen.MAP;
            } else if (AbstractDungeon.screen != AbstractDungeon.CurrentScreen.SETTINGS && AbstractDungeon.screen != AbstractDungeon.CurrentScreen.MAP) {
                if (AbstractDungeon.screen == AbstractDungeon.CurrentScreen.INPUT_SETTINGS) {
                    if (AbstractDungeon.previousScreen != null) {
                        AbstractDungeon.screenSwap = true;
                    }

                    AbstractDungeon.closeCurrentScreen();
                } else if (AbstractDungeon.screen == AbstractDungeon.CurrentScreen.CARD_REWARD) {
                    AbstractDungeon.previousScreen = AbstractDungeon.CurrentScreen.CARD_REWARD;
                    AbstractDungeon.dynamicBanner.hide();
                } else if (AbstractDungeon.screen == AbstractDungeon.CurrentScreen.GRID) {
                    AbstractDungeon.previousScreen = AbstractDungeon.CurrentScreen.GRID;
                    AbstractDungeon.gridSelectScreen.hide();
                } else if (AbstractDungeon.screen == AbstractDungeon.CurrentScreen.HAND_SELECT) {
                    AbstractDungeon.previousScreen = AbstractDungeon.CurrentScreen.HAND_SELECT;
                }
            } else {
                if (AbstractDungeon.previousScreen != null) {
                    AbstractDungeon.screenSwap = true;
                }

                AbstractDungeon.closeCurrentScreen();
            }
            if (AbstractDungeon.screen != AbstractDungeon.CurrentScreen.VICTORY) {
                BaseMod.openCustomScreen(PokemonTeamViewScreen.Enum.POKEMON_TEAM_VIEW_SCREEN);
            }
        }
        InputHelper.justClickedLeft = false;
    }
}