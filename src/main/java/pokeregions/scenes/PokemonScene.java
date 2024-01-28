package pokeregions.scenes;

import pokeregions.monsters.act1.enemies.*;
import pokeregions.monsters.act1.enemies.birds.ArticunoEnemy;
import pokeregions.monsters.act1.enemies.birds.MoltresEnemy;
import pokeregions.monsters.act1.enemies.birds.ZapdosEnemy;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.CampfireUI;
import com.megacrit.cardcrawl.rooms.MonsterRoomBoss;
import com.megacrit.cardcrawl.rooms.ShopRoom;
import com.megacrit.cardcrawl.scenes.AbstractScene;
import pokeregions.monsters.act3.enemies.DeoxysEnemy;
import pokeregions.monsters.act3.enemies.KyogreEnemy;

public class PokemonScene extends AbstractScene {
    private TextureAtlas.AtlasRegion bg;

    public PokemonScene() {
        super("pokeRegionsResources/images/scenes/atlas.atlas");

        this.bg = this.atlas.findRegion("mod/Courtyard");

        this.ambianceName = "AMBIANCE_CITY";
        this.fadeInAmbiance();
    }

    @Override
    public void update() {
        super.update();
    }

    @Override
    public void randomizeScene() {
    }

    @Override
    public void nextRoom(AbstractRoom room) {
        super.nextRoom(room);
        this.randomizeScene();
        if (room instanceof MonsterRoomBoss) {
            CardCrawlGame.music.silenceBGM();
        }
        if (room.monsters != null) {
            for (AbstractMonster mo : room.monsters.monsters) {
                if (mo instanceof DiglettEnemy) {
                    this.bg = this.atlas.findRegion("mod/Desert");
                } else if (mo instanceof DugtrioEnemy) {
                    this.bg = this.atlas.findRegion("mod/Desert");
                } else if (mo instanceof CloysterEnemy) {
                    this.bg = this.atlas.findRegion("mod/IceCave");
                } else if (mo instanceof GastlyEnemy || mo instanceof HaunterEnemy || mo instanceof GengarEnemy) {
                    this.bg = this.atlas.findRegion("mod/Cave");
                } else if (mo instanceof DragoniteEnemy) {
                    this.bg = this.atlas.findRegion("mod/Volcano");
                } else if (mo instanceof ArbokEnemy) {
                    this.bg = this.atlas.findRegion("mod/Forest");
                } else if (mo instanceof VulpixEnemy) {
                    this.bg = this.atlas.findRegion("mod/Ruins");
                } else if (mo instanceof GolemEnemy) {
                    this.bg = this.atlas.findRegion("mod/Cave");
                } else if (mo instanceof MachampEnemy) {
                    this.bg = this.atlas.findRegion("mod/Arena");
                } else if (mo instanceof RattataEnemy) {
                    this.bg = this.atlas.findRegion("mod/Forest");
                } else if (mo instanceof RhyhornEnemy) {
                    this.bg = this.atlas.findRegion("mod/Desert");
                } else if (mo instanceof MoltresEnemy || mo instanceof ZapdosEnemy || mo instanceof ArticunoEnemy) {
                    this.bg = this.atlas.findRegion("mod/Holy");
                } else if (mo instanceof MewtwoEnemy) {
                    this.bg = this.atlas.findRegion("mod/Cosmic");
                } else if (mo instanceof CaterpieEnemy || mo instanceof WeedleEnemy) {
                    this.bg = this.atlas.findRegion("mod/Forest");
                } else if (mo instanceof OmastarEnemy) {
                    this.bg = this.atlas.findRegion("mod/Bridge");
                } else if (mo instanceof AlakazamEnemy) {
                    this.bg = this.atlas.findRegion("mod/Holy");
                } else if (mo instanceof KyogreEnemy) {
                    this.bg = this.atlas.findRegion("mod/Bridge");
                } else if (mo instanceof DeoxysEnemy) {
                    this.bg = this.atlas.findRegion("mod/Cosmic");
                } else {
                    this.bg = this.atlas.findRegion("mod/Forest");
                }
            }
        } else if (room instanceof ShopRoom) {
            this.bg = this.atlas.findRegion("mod/Castle");
        } else {
            this.bg = this.atlas.findRegion("mod/Courtyard");
        }
        this.fadeInAmbiance();
    }

    @Override
    public void renderCombatRoomBg(SpriteBatch sb) {
        sb.setColor(Color.WHITE.cpy());
        this.renderAtlasRegionIf(sb, bg, true);
        sb.setBlendFunction(Gdx.gl20.GL_SRC_ALPHA, Gdx.gl20.GL_ONE_MINUS_SRC_ALPHA);
    }

    @Override
    public void renderCombatRoomFg(SpriteBatch sb) {
        sb.setColor(Color.WHITE.cpy());
    }

    @Override
    public void renderCampfireRoom(SpriteBatch sb) {
        sb.setColor(Color.WHITE);
        this.renderAtlasRegionIf(sb, this.campfireBg, true);
        sb.setBlendFunction(Gdx.gl20.GL_SRC_ALPHA, Gdx.gl20.GL_ONE);
        sb.setColor(new Color(1.0f, 1.0f, 1.0f, MathUtils.cosDeg(System.currentTimeMillis() / 3L % 360L) / 10.0f + 0.8f));
        this.renderQuadrupleSize(sb, this.campfireGlow, !CampfireUI.hidden);
        sb.setBlendFunction(Gdx.gl20.GL_SRC_ALPHA, Gdx.gl20.GL_ONE_MINUS_SRC_ALPHA);
        sb.setColor(Color.WHITE);
        this.renderAtlasRegionIf(sb, this.campfireKindling, true);
    }
}