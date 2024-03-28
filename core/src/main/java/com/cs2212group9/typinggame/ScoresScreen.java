package com.cs2212group9.typinggame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.cs2212group9.typinggame.db.DBLevel;
import com.cs2212group9.typinggame.db.DBScores;
import com.cs2212group9.typinggame.db.DBUser;
import com.cs2212group9.typinggame.utils.InputListenerFactory;
import com.cs2212group9.typinggame.utils.ScoreEntry;

import java.util.List;

/**
 * This class is mainly used to set various parameters of the game according to user needs.
 * @author Group 9 members
 */
public class ScoresScreen implements Screen {
    final TypingGame game;
    OrthographicCamera camera;
    private final Stage stage;
    private final Viewport viewport;
    private final Skin skin = new Skin(Gdx.files.internal("ui/star-soldier/star-soldier-ui.json"));
    private String selectedUser;

    /** Constructor for the OptionsScreen, initializes camera & viewport, and sets up button skins
     * @param gam - the game object
     */
    public ScoresScreen(final TypingGame gam) {
        game = gam;

        camera = new OrthographicCamera();

        viewport = new FitViewport(1200, 800, camera);
        viewport.apply();

        camera.position.set(1200, 800, 0);
        camera.update();

        stage = new Stage();
    }

    @Override
    public void render(float delta) {
        // Gdx.gl.glClearColor(.1f, .12f, .16f, 1);
        Gdx.gl.glClear(Gdx.gl.GL_COLOR_BUFFER_BIT);

        stage.act();
        stage.draw();

        if (Gdx.input.isKeyPressed(Input.Keys.F5) && Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)
            && Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)) {
            dispose();
            game.setScreen(new OptionsScreen(game));
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);
        camera.update();
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        stage.clear();
        stage.dispose();
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);

        Table totalScoreTable = new Table(skin);
        totalScoreTable.setFillParent(true);
        totalScoreTable.top();
        totalScoreTable.padTop(100);

        Label title = new Label("High Scores", skin);
        title.setFontScale(3f);
        totalScoreTable.add(title).row();
        totalScoreTable.row().padTop(10);

        int maxEntries = 5;
        List<ScoreEntry> topScores = DBScores.getTopScores(maxEntries);
        int count = 1;

        Label totalTitle = new Label("Total Scores", skin);
        totalScoreTable.add(totalTitle).row();
        totalScoreTable.row().padTop(10);

        if (topScores.isEmpty()) {
            System.out.println("No scores to display.");
        } else {
            for (ScoreEntry entry : topScores) {
                Label scoreLabel = new Label(count + ". " + entry.toString(), skin);
                totalScoreTable.add(scoreLabel).row(); // Adjust as needed for your table setup
                totalScoreTable.padTop(10);
                System.out.println(entry);
                count++;
            }
        }

        // Fill in the remaining slots with dummy entries if needed
        for (int i = count; i < maxEntries + 1; i++) {
            Label dummyScoreLabel = new Label((i) + ". no score yet", skin);
            totalScoreTable.add(dummyScoreLabel).row();
            totalScoreTable.padTop(10);
            System.out.println((i) + ". -");
        }

        // display top scores for each level
        Table levelTable = new Table(skin);
        levelTable.setFillParent(true);
        levelTable.top();
        levelTable.padTop(300);
        for (int i = 0; i < DBLevel.getLevelCount() / 3; i++) {
            for (int j = 1; j <= 3; j++) {
                int level = i * 3 + j;
                ScoreEntry topScore = DBScores.getTopLevelScore(level);
                Label levelLabel = new Label("Level " + level + ": " + (topScore != null ? topScore : "none"), skin);
                levelTable.add(levelLabel);
            }
            levelTable.row().padTop(10).padLeft(10).padRight(10);
        }
        stage.addActor(levelTable);

        Table searchTable = new Table(skin);
        searchTable.setFillParent(true);
        searchTable.top();
        searchTable.padTop(225);
        if (DBUser.isAdmin(game.getUsername())) {
            searchTable.add(new Label("Search for user:", skin));
            final TextField userField = new TextField("", skin);
            final TextButton searchButton = new TextButton("Search", skin);
            searchButton.addListener(InputListenerFactory.createClickListener((event, x, y) -> {
                this.selectedUser = userField.getText();
                System.out.println("Selected user: " + this.selectedUser);
            }));
            userField.setWidth(340);
            searchTable.add(userField);
            searchTable.add(searchButton).row();
        }
        stage.addActor(searchTable);

        // display total score for selected user
        if (this.selectedUser != null) {
            System.out.println("Selected user: " + this.selectedUser);
            totalScoreTable.add(new Label("Total score for " + this.selectedUser + ":"
                + DBScores.getUserTotalScore(this.game.getUsername()), skin)).row();
        }

        stage.addActor(totalScoreTable);



        TextButton returnButton = new TextButton("Return to Main Menu", skin);
        returnButton.setPosition(5, 5);
        returnButton.setWidth(375);
        returnButton.addListener(InputListenerFactory.createClickListener((event, x, y) -> {
            dispose();
            game.setScreen(new MainMenuScreen(game));
        }));
        stage.addActor(returnButton);
    }
}
